package libsidplay;

import java.util.Arrays;

import libsidplay.common.CPUClock;
import libsidplay.common.Event;
import libsidplay.common.Event.Phase;
import libsidplay.common.EventScheduler;
import libsidplay.common.IReSIDExtension;
import libsidplay.common.SIDEmu;
import libsidplay.components.c1530.DatasetteEnvironment;
import libsidplay.components.c1541.C1541Environment;
import libsidplay.components.c1541.IParallelCable;
import libsidplay.components.cart.Cartridge;
import libsidplay.components.joystick.IJoystick;
import libsidplay.components.keyboard.Keyboard;
import libsidplay.components.mos6510.IMOS6510Extension;
import libsidplay.components.mos6510.MOS6510;
import libsidplay.components.mos6526.MOS6526;
import libsidplay.components.mos656x.MOS6567;
import libsidplay.components.mos656x.MOS6569;
import libsidplay.components.mos656x.VIC;
import libsidplay.components.pla.Bank;
import libsidplay.components.pla.PLA;
import libsidplay.components.printer.UserportPrinterEnvironment;
import libsidplay.components.ram.SystemRAMBank;

/**
 * Commodore 64 emulation core.
 * 
 * It consists of the following chips: PLA, MOS6510, MOS6526(a), VIC
 * 6569(PAL)/6567(NTSC), RAM/ROM.<BR>
 * Some connectors exist additionally: Keyboard, two Joysticks, parallel cable
 * and some memory expansions and other cartridges can be plugged in.
 * 
 * @author Antti Lankila
 * @author Ken H�ndel
 * 
 */
public abstract class C64 implements DatasetteEnvironment, C1541Environment,
		UserportPrinterEnvironment {
	/** Maximum number of supported SIDs (mono and stereo) */
	final static int MAX_SIDS = 2;

	/** Currently active CIA model. */
	private static final MOS6526.Model CIAMODEL = MOS6526.Model.MOS6526;

	/** System clock */
	protected CPUClock clock;

	/** MMU chip */
	protected final PLA pla;

	/** CPU */
	private final MOS6510 cpu;

	/** CIA1 */
	protected final MOS6526 cia1;

	/** CIA2 */
	protected final MOS6526 cia2;

	/** Keyboard */
	protected final Keyboard keyboard;

	/** Attached parallel cable */
	protected IParallelCable parallelCable;

	/**
	 * Specific VIC used for PAL.
	 */
	protected final VIC palVic;

	/**
	 * Specific VIC used for NTSC.
	 */
	protected final VIC ntscVic;

	/** System memory array */
	protected final SystemRAMBank ramBank = new SystemRAMBank();

	/** System event context */
	protected final EventScheduler context;

	/** Number of entrances to play routine */
	protected int callsToPlayRoutine;

	/** Playroutine address */
	protected int playAddr;

	/** The interested party for playroutine entrances. */
	protected IMOS6510Extension playRoutineObserver;

	/** Joystick port devices */
	protected final IJoystick[] joystickPort = new IJoystick[2];

	/**
	 * This class implements a disconnected joystick.
	 * 
	 * @author Ken H�ndel
	 * 
	 */
	protected static final class DisconnectedJoystick implements IJoystick {
		@Override
		public byte getValue() {
			return (byte) 0xff;
		}
	}

	/** Implementation of a disconnected Joystick */
	private final DisconnectedJoystick disconnectedJoystick = new DisconnectedJoystick();

	/** SID chip memory bank maps reads and writes to the assigned SID chip */
	protected class SIDBank extends Bank {
		/**
		 * Size of mapping table. Each 32 bytes another SID chip base address
		 * can be assigned to.
		 */
		private final static int MAPPER_SIZE = 32;
		/** Number of SID chip registers */
		private final static int REG_COUNT = 32;

		/**
		 * Mapping table in d400-d7ff. Maps a SID chip base address to a SID
		 * chip number.
		 */
		private final int sidmapper[] = new int[MAPPER_SIZE];

		/** Contains a SID chip implementation for each SID chip number. */
		private final SIDEmu[] sidemu = new SIDEmu[MAX_SIDS];

		/** SID listener for detecting SID writes */
		private final IReSIDExtension[] sidWriteListener = new IReSIDExtension[MAX_SIDS];

		/** Reset SID chips using highest volume setting. */
		public void reset() {
			for (final SIDEmu s : sidemu) {
				if (s != null) {
					s.reset((byte) 0xf);
				}
			}
			Arrays.fill(sidWriteListener, null);
		}

		/** Assign SID chip base address to a SID chip number */
		public void setSIDMapping(final int address, final int chipNum) {
			sidmapper[address >> 5 & MAPPER_SIZE - 1] = chipNum;
		}

		/**
		 * Install a SID register write listener for a specific SID chip number.
		 */
		public void setSidWriteListener(final int chipNum,
				final IReSIDExtension listener) {
			sidWriteListener[chipNum] = listener;
		}

		@Override
		/** SID register read access redirected to a specific SID chip number configured earlier. */
		public byte read(final int address) {
			final int chipNum = sidmapper[address >> 5 & MAPPER_SIZE - 1];
			if (sidemu[chipNum] != null) {
				return sidemu[chipNum].read(address & REG_COUNT - 1);
			} else {
				return (byte) 0xff;
			}
		}

		@Override
		/** SID register write access redirected to a specific SID chip number configured earlier. */
		public void write(final int address, final byte value) {
			final int chipNum = sidmapper[address >> 5 & MAPPER_SIZE - 1];
			if (sidemu[chipNum] != null) {
				sidemu[chipNum].write(address & REG_COUNT - 1, value);
			}
			if (sidWriteListener[chipNum] != null) {
				final long time = context.getTime(Event.Phase.PHI2);
				sidWriteListener[chipNum].write(time, chipNum, address
						& REG_COUNT - 1, value);
			}
		}

		/** Get SID chip implementation of a specific SID chip number. */
		public SIDEmu getSID(final int chipNum) {
			return sidemu[chipNum];
		}

		/** Set SID chip implementation of a specific SID chip number. */
		public void setSID(final int chipNum, final SIDEmu s) {
			sidemu[chipNum] = s;
		}
	}

	/** SID chip memory bank */
	private final SIDBank sidBank = new SIDBank();

	/**
	 * Area backed by RAM, including cpu port addresses 0 and 1.
	 * 
	 * This is bit of a fake. We know that the CPU port is an internal detail of
	 * the CPU, and therefore CPU should simply pay the price for
	 * reading/writing to 0/1.
	 * 
	 * However, that would slow down all accesses, which is suboptimal.
	 * Therefore we install this little hook to the 4k 0 region to deal with
	 * this.
	 * 
	 * @author Antti Lankila
	 */
	protected class ZeroRAMBank extends Bank {
		/** Value written to processor port. */
		private byte dir;
		private byte data;

		/** Value read from processor port. */
		private byte dataRead;

		/** State of processor port pins. */
		private byte dataOut;

		/** $01 bits 6 and 7 fall-off cycles (1->0), average is about 350 msec */
		private static final long C64_CPU_DATA_PORT_FALL_OFF_CYCLES = 350000;

		/** cycle that should invalidate the unused bits of the data port. */
		private long dataSetClkBit6;
		private long dataSetClkBit7;

		/**
		 * indicates if the unused bits of the data port are still valid or
		 * should be read as 0, 1 = unused bits valid, 0 = unused bits should be
		 * 0
		 */
		private boolean dataSetBit6;
		private boolean dataSetBit7;

		/** indicated if the unused bits are in the process of falling off. */
		private boolean dataFalloffBit6;
		private boolean dataFalloffBit7;

		/** Tape motor status. */
		private byte oldPortDataOut;
		/** Tape write line status. */
		private byte oldPortWriteBit;

		public void reset() {
			oldPortDataOut = (byte) 0xff;
			oldPortWriteBit = (byte) 0xff;
			data = 0x3f;
			dataOut = 0x3f;
			dataRead = 0x3f;
			dir = 0;
			dataSetBit6 = false;
			dataSetBit7 = false;
			dataFalloffBit6 = false;
			dataFalloffBit7 = false;
			updateCpuPort();
		}

		private void updateCpuPort() {
			dataOut = (byte) (dataOut & ~dir | data & dir);
			dataRead = (byte) ((data | ~dir) & (dataOut | 0x17));
			pla.setCpuPort(dataRead);

			if (0 == (dir & 0x20)) {
				dataRead &= 0xdf;
			}
			if (0 == (dir & 0x10) && C64.this.getTapeSense()) {
				dataRead &= 0xef;
			}

			if ((dir & data & 0x20) != oldPortDataOut) {
				oldPortDataOut = (byte) (dir & data & 0x20);
				C64.this.setMotor(0 == oldPortDataOut);
			}

			if (((~dir | data) & 0x8) != oldPortWriteBit) {
				oldPortWriteBit = (byte) ((~dir | data) & 0x8);
				C64.this.toggleWriteBit(((~dir | data) & 0x8) != 0);
			}
		}

		@Override
		public byte read(final int address) {
			if (address == 0) {
				return dir;
			} else if (address == 1) {
				if (dataFalloffBit6 || dataFalloffBit7) {
					if (dataSetClkBit6 < context.getTime(Phase.PHI2)) {
						dataFalloffBit6 = false;
						dataSetBit6 = false;
					}

					if (dataSetClkBit7 < context.getTime(Phase.PHI2)) {
						dataSetBit7 = false;
						dataFalloffBit7 = false;
					}
				}
				return (byte) (dataRead & 0xff - (((!dataSetBit6 ? 1 : 0) << 6) + ((!dataSetBit7 ? 1
						: 0) << 7)));
			} else {
				return ramBank.read(address);
			}
		}

		@Override
		public void write(final int address, byte value) {
			if (address == 0) {
				if (dataSetBit7 && (value & 0x80) == 0 && !dataFalloffBit7) {
					dataFalloffBit7 = true;
					dataSetClkBit7 = context.getTime(Phase.PHI2)
							+ C64_CPU_DATA_PORT_FALL_OFF_CYCLES;
				}
				if (dataSetBit6 && (value & 0x40) == 0 && !dataFalloffBit6) {
					dataFalloffBit6 = true;
					dataSetClkBit6 = context.getTime(Phase.PHI2)
							+ C64_CPU_DATA_PORT_FALL_OFF_CYCLES;
				}
				if (dataSetBit7 && (value & 0x80) != 0 && dataFalloffBit7) {
					dataFalloffBit7 = false;
				}
				if (dataSetBit6 && (value & 0x40) != 0 && dataFalloffBit6) {
					dataFalloffBit6 = false;
				}
				dir = value;
				updateCpuPort();
				value = pla.getDisconnectedBusBank().read(address);
			} else if (address == 1) {
				if ((dir & 0x80) != 0 && (value & 0x80) != 0) {
					dataSetBit7 = true;
				}
				if ((dir & 0x40) != 0 && (value & 0x40) != 0) {
					dataSetBit6 = true;
				}
				data = value;
				updateCpuPort();
				value = pla.getDisconnectedBusBank().read(address);
			}
			ramBank.write(address, value);
		}
	}

	/** Zero page memory bank */
	private final ZeroRAMBank zeroRAMBank = new ZeroRAMBank();

	/**
	 * Set play routine address to watch by CPU emulation.
	 * 
	 * @param playAddr
	 *            Observe calls of SID player (JSR $PlayAddr).
	 */
	public void setPlayAddr(final int playAddr) {
		this.playAddr = playAddr;
	}

	public final int callsToPlayRoutineSinceLastTime() {
		final int val = callsToPlayRoutine;
		callsToPlayRoutine = 0;
		return val;
	}

	public final void setParallelCable(final IParallelCable parallelCable) {
		this.parallelCable = parallelCable;
	}

	public C64() {
		context = new EventScheduler();

		pla = new PLA(context, sidBank, zeroRAMBank, ramBank);

		cpu = new MOS6510(context) {
			@Override
			public byte cpuRead(final int address) {
				return pla.cpuRead(address);
			}

			@Override
			public void cpuWrite(final int address, final byte value) {
				pla.cpuWrite(address, value);
			}

			@Override
			protected void doJSR() {
				super.doJSR();
				if (Register_ProgramCounter == playAddr) {
					if (playRoutineObserver != null) {
						final long time = context.getTime(Event.Phase.PHI2);
						playRoutineObserver.fetch(time);
					}
					callsToPlayRoutine++;
				}
			}
		};
		pla.setCpu(cpu);

		palVic = new MOS6569(pla, context);
		ntscVic = new MOS6567(pla, context);
		pla.setVic(palVic);

		cia1 = new MOS6526(context, CIAMODEL) {
			@Override
			public void interrupt(final boolean state) {
				pla.setIRQ(state);
			}

			@Override
			public void writePRA(final byte data) {
			}

			@Override
			public void writePRB(final byte data) {
				if ((data & 0x10) == 0) {
					getVIC().triggerLightpen();
				} else {
					getVIC().clearLightpen();
				}
			}

			@Override
			public byte readPRA() {
				byte prbOut = (byte) (regs[PRB] | ~regs[DDRB]);
				prbOut &= joystickPort[0].getValue();
				final byte kbd = keyboard.readColumn(prbOut);
				final byte joy = joystickPort[1].getValue();
				return (byte) (kbd & joy);
			}

			@Override
			public byte readPRB() {
				byte praOut = (byte) (regs[PRA] | ~regs[DDRA]);
				praOut &= joystickPort[1].getValue();
				final byte kbd = keyboard.readRow(praOut);
				final byte joy = joystickPort[0].getValue();
				return (byte) (kbd & joy);
			}

			@Override
			public void pulse() {
				/* Nobody home */
			}
		};
		pla.setCia1(cia1);

		cia2 = new MOS6526(context, CIAMODEL) {
			@Override
			public void interrupt(final boolean state) {
				pla.setNMI(state);
			}

			@Override
			public void writePRA(final byte data) {
				pla.setVicMemBase((~data & 3) << 14);
				C64.this.writeToIECBus((byte) ~data);
				C64.this.printerUserportWriteStrobe((~data & 0x04) != 0);
			}

			@Override
			public void writePRB(final byte data) {
				parallelCable.c64Write(data);
			}

			@Override
			public byte readPRA() {
				return (byte) (C64.this.readFromIECBus() | 0x3f);
			}

			@Override
			public byte readPRB() {
				return parallelCable.c64Read();
			}

			@Override
			public void pulse() {
				parallelCable.pulse();
				C64.this.printerUserportWriteData(regs[PRB]);
			}

			@Override
			public void reset() {
				super.reset();
				C64.this.printerUserportWriteStrobe(true);
				C64.this.printerUserportWriteData((byte) 0xff);
			}
		};
		pla.setCia2(cia2);

		keyboard = new Keyboard() {
			@Override
			public void restore() {
				/*
				 * in reality, the PLA chip has a RESTORE line, and it pulls the
				 * NMI low. We're modeling the overall NMI and IRQ state in the
				 * PLA, so we can't really do that. We just pulse NMI instead.
				 */
				pla.setNMI(true);
				pla.setNMI(false);
			}
		};

		setJoystick(0, null);
		setJoystick(1, null);
	}

	/**
	 * Perform the equivalent of full power-on reset of C64, re-initializing
	 * everything.
	 * 
	 * @throws InterruptedException
	 */
	public void reset() throws InterruptedException {
		context.reset();
		keyboard.reset();
		pla.reset();
		cpu.triggerRST();
		cia1.reset();
		cia2.reset();
		sidBank.reset();
		getVIC().reset();
		zeroRAMBank.reset();
		ramBank.reset();

		callsToPlayRoutine = 0;
		playAddr = -1;
	}

	/**
	 * Return the requested SID
	 * 
	 * @param chipNo
	 *            (0..MAX_SIDS-1)
	 * @return the SID
	 */
	public SIDEmu getSID(final int chipNo) {
		return sidBank.getSID(chipNo);
	}

	/**
	 * Set the requested SID
	 * 
	 * @param chipNo
	 *            (0..MAX_SIDS-1)
	 * @param sidemu
	 *            the sid to set
	 */
	public void setSID(final int chipNo, final SIDEmu sidemu) {
		sidBank.setSID(chipNo, sidemu);
	}

	/**
	 * Set the base address of a stereo SID chip
	 * 
	 * @param secondSidChipBase
	 *            base address (e.g. 0xd420)
	 */
	public void setSecondSIDAddress(final int secondSidChipBase) {
		sidBank.setSIDMapping(0xd400, 0);
		sidBank.setSIDMapping(secondSidChipBase, 1);
	}

	/**
	 * Set a SID write listener to debug SID register writes.
	 * 
	 * @param chipNo
	 *            (0..MAX_SIDS-1)
	 * @param listener
	 *            listener to debug SID register writes
	 */
	public void setSidWriteListener(final int chipNo,
			final IReSIDExtension listener) {
		sidBank.setSidWriteListener(chipNo, listener);
	}

	/**
	 * Return the array backing C64 RAM
	 * 
	 * @return the RAM
	 */
	public byte[] getRAM() {
		return ramBank.array();
	}

	/**
	 * Return CPU emulator
	 * 
	 * @return the cpu
	 */
	public MOS6510 getCPU() {
		return cpu;
	}

	/**
	 * Install a play routine observer to hook the JSR command of the CPU. It
	 * gets called, if the player address gets called.
	 * 
	 * @param observer
	 *            play routine observer
	 */
	public void setPlayRoutineObserver(final IMOS6510Extension observer) {
		playRoutineObserver = observer;
	}

	/**
	 * Get VIC chip emulator (PAL/NTSC).
	 * 
	 * @return VIC chip
	 */
	public VIC getVIC() {
		return clock == CPUClock.NTSC ? ntscVic : palVic;
	}

	public final VIC getPalVIC() {
		return palVic;
	}

	public final VIC getNtscVIC() {
		return ntscVic;
	}

	/**
	 * Set system clock (PAL/NTSC).
	 * 
	 * @param clock
	 *            system clock (PAL/NTSC)
	 */
	public void setClock(final CPUClock clock) {
		this.clock = clock;

		context.setCyclesPerSecond(clock.getCpuFrequency());
		cia1.setDayOfTimeRate(clock.getCyclesPerFrame());
		cia2.setDayOfTimeRate(clock.getCyclesPerFrame());
		pla.setVic(getVIC());
	}

	/**
	 * Get system clock (PAL/NTSC).
	 * 
	 * @return system clock (PAL/NTSC)
	 */
	public CPUClock getClock() {
		return clock;
	}

	/**
	 * Get C64's event scheduler
	 * 
	 * @return the scheduler
	 */
	public EventScheduler getEventScheduler() {
		return context;
	}

	/**
	 * Installs a custom Kernal ROM.
	 * 
	 * @param kernalRom
	 *            Kernal ROM replacement (null means original Kernal)
	 */
	public void setCustomKernal(final byte[] kernalRom) {
		if (kernalRom == null) {
			pla.setCustomKernalRomBank(null);
		} else {
			pla.setCustomKernalRomBank(new Bank() {
				@Override
				public byte read(final int address) {
					return kernalRom[address & 0x1fff];
				}

				@Override
				public void write(final int address, final byte value) {
					throw new RuntimeException(
							"This bank should never be mapped to W mode");
				}
			});
		}
	}

	/**
	 * Get current keyboard emulation.
	 * 
	 * @return current keyboard emulation
	 */
	public Keyboard getKeyboard() {
		return keyboard;
	}

	/**
	 * Set joystick implementation.
	 * 
	 * @param portNumber
	 *            joystick port (0-1)
	 * @param joystickReader
	 *            joystick implementation or null (disconnected)
	 */
	public final void setJoystick(final int portNumber,
			final IJoystick joystickReader) {
		joystickPort[portNumber] = joystickReader == null ? disconnectedJoystick
				: joystickReader;
	}

	/**
	 * Is joystick connected?
	 * 
	 * @param portNumber
	 *            joystick port (0-1)
	 * @return joystick connected?
	 */
	public final boolean isJoystickConnected(final int portNumber) {
		return !joystickPort[portNumber].equals(disconnectedJoystick);
	}

	public PLA getPla() {
		return pla;
	}

	/**
	 * Eject multi purpose cartridge from the expansion port of the C64.
	 */
	public final void ejectCartridge() {
		pla.setCartridge(null);
	}

	/**
	 * Get current multi purpose cartridge.
	 * 
	 * @return multi purpose cartridge
	 */
	public final Cartridge getCartridge() {
		return pla.getCartridge();
	}
}
