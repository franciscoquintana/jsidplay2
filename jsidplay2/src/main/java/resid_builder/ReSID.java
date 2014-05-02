/**
 *                           ReSid Emulation
 *                           ---------------
 *  begin                : Fri Apr 4 2001
 *  copyright            : (C) 2001 by Simon White
 *  email                : s_a_white@email.com
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 * @author Ken H�ndel
 *
 */
package resid_builder;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import libsidplay.common.Event;
import libsidplay.common.EventScheduler;
import libsidplay.common.SIDEmu;
import resid_builder.ReSIDBuilder.MixerEvent;
import resid_builder.resid.ChipModel;
import resid_builder.resid.SID;
import resid_builder.resid.SamplingMethod;
import sidplay.ini.intf.IConfig;
import sidplay.ini.intf.IFilterSection;

/**
 * ReSID emulation.
 */
public class ReSID extends SIDEmu {
	private static final Logger RESID = Logger.getLogger(ReSID.class.getName());

	private static final String VERSION = "0.0.2";

	/*
	 * supports 5 ms chunk at 96 kHz
	 */
	private static final int OUTPUTBUFFERSIZE = 5000;
	private final SID sid = new SID();

	protected int position;

	protected int[] buffer;

	private final ReSIDBuilder.MixerEvent mixerEvent;

	/**
	 * Constructor
	 *
	 * @param context
	 *            {@link EventScheduler} context to use.
	 * @param mixerEvent
	 *            {@link MixerEvent} to use.
	 */
	public ReSID(EventScheduler context, MixerEvent mixerEvent) {
		super(context);
		this.mixerEvent = mixerEvent;
		buffer = new int[OUTPUTBUFFERSIZE];
		position = 0;
		reset((byte) 0);
	}

	@Override
	public void reset(final byte volume) {
		clocksSinceLastAccess();
		sid.reset();
		sid.write(0x18, volume);
		/*
		 * No matter how many chips are in use, mixerEvent is singleton with
		 * respect to them. Only one will be scheduled. This is a bit dirty,
		 * though.
		 */
		context.cancel(mixerEvent);
		mixerEvent.setContext(context);
		context.schedule(mixerEvent, 0, Event.Phase.PHI2);
	}

	@Override
	public byte read(int addr) {
		addr &= 0x1f;
		// correction for sid_detection.prg
		lastTime--;
		clock();
		return sid.read(addr);
	}

	@Override
	public void write(int addr, final byte data) {
		addr &= 0x1f;
		super.write(addr, data);
		if (RESID.isLoggable(Level.FINE)) {
			RESID.fine(String.format("write 0x%02x=0x%02x", addr, data));
		}

		clock();
		sid.write(addr, data);
	}

	@Override
	public void clock() {
		int cycles = clocksSinceLastAccess();
		position += sid.clock(cycles, buffer, position);
	}

	@Override
	public void setFilter(final boolean enable) {
		sid.getFilter6581().enable(enable);
		sid.getFilter8580().enable(enable);
	}

	public void setFilter(IConfig config) {
		IFilterSection f6581 = null;
		IFilterSection f8580 = null;
		String filter6581 = config.getEmulation().getFilter6581();
		String filter8580 = config.getEmulation().getFilter8580();
		List<? extends IFilterSection> filters = config.getFilter();
		for (IFilterSection filter : filters) {
			if (filter.getName().equals(filter6581)
					&& filter.getFilter8580CurvePosition() == 0) {
				f6581 = filter;
			} else if (filter.getName().equals(filter8580)
					&& filter.getFilter8580CurvePosition() != 0) {
				f8580 = filter;
			}
		}
		setFilter(config.getEmulation().isFilter());
		setFilter(f6581, f8580);
	}

	private void setFilter(final IFilterSection filter6581,
			final IFilterSection filter8580) {
		if (filter6581 != null) {
			sid.getFilter6581().setFilterCurve(
					filter6581.getFilter6581CurvePosition());
		}

		if (filter8580 != null) {
			sid.getFilter8580().setFilterCurve(
					filter8580.getFilter8580CurvePosition());
		}
	}

	@Override
	public void setVoiceMute(final int num, final boolean mute) {
		sid.mute(num, mute);
	}

	/**
	 * Sets the SID sampling parameters.
	 *
	 * @param systemClock
	 *            System clock to use for the SID.
	 * @param freq
	 *            Frequency to use for the SID.
	 * @param method
	 *            {@link SamplingMethod} to use for the SID.
	 */
	public void setSampling(final double systemClock, final float freq,
			final SamplingMethod method) {
		sid.setSamplingParameters(systemClock, method, freq, 20000);
	}

	/**
	 * Set the emulated SID model
	 * 
	 * @param model
	 *            The emulated SID chip model to use.
	 */
	public void setChipModel(final ChipModel model) {
		sid.setChipModel(model);
	}

	@Override
	public void input(int input) {
		sid.input(input);
	}

	/**
	 * Credits string.
	 *
	 * @return String of credits.
	 */
	public static final String credits() {
		String m_credit = "ReSID V" + VERSION + " Engine:\n";
		m_credit += "\tCopyright (C) 1999-2002 Simon White <sidplay2@yahoo.com>\n";
		m_credit += "MOS6581/8580 (SID) Emulation:\n";
		m_credit += "\tCopyright (C) 1999-2004 Dag Lem <resid@nimrod.no>\n";
		m_credit += "\tCopyright (C) 2005-2011 Antti S. Lankila <alankila@bel.fi>\n";
		return m_credit;
	}

	// Getters and setters.

	/**
	 * Gets the {@link SID} instance being used.
	 *
	 * @return The {@link SID} instance being used.
	 */
	public SID sid() {
		return sid;
	}

	@Override
	public ChipModel getChipModel() {
		return sid.getChipModel();
	}

	/**
	 * Gets the current position that audio is being written to.
	 *
	 * @return The current position that audio is being written to.
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Sets the position to write audio to the buffer.
	 *
	 * @param position
	 *            The new position to start at.
	 */
	public void setPosition(final int position) {
		this.position = position;
	}

	/**
	 * Gets the audio output sample buffer.
	 *
	 * @return The audio output sample buffer.
	 */
	public int[] getBuffer() {
		return buffer;
	}
}