package hardsid_builder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.ArrayList;

import libsidplay.common.EventScheduler;
import libsidplay.common.SIDBuilder;
import libsidplay.common.SIDEmu;
import resid_builder.resid.ChipModel;
import sidplay.ini.intf.IConfig;

/**
 * <pre>
 * **************************************************************************
 *       hardsid-builder.cpp - HardSID builder class for creating/controlling
 *                             HardSIDs.
 *                             -------------------
 *  begin                : Wed Sep 5 2001
 *  copyright            : (C) 2001 by Simon White
 *  email                : s_a_white@email.com
 * **************************************************************************
 * **************************************************************************
 * * This program is free software; you can redistribute it and/or modify * it
 * under the terms of the GNU General Public License as published by * the Free
 * Software Foundation; either version 2 of the License, or * (at your option)
 * any later version. * *
 * **************************************************************************
 * </pre>
 * 
 * @author Ken H�ndel
 * 
 */
public class HardSIDBuilder extends SIDBuilder {
	public static final String VERSION = "1.0.1";
	public static final int HSID_VERSION_MIN = 0x0200;
	public static final int HSID_VERSION_204 = 0x0204;
	public static final int HSID_VERSION_207 = 0x0207;
	/* New, since HardSID4U */
	public static final int HSID_VERSION_301 = 0x0301;

	private String m_errorBuffer;
	private final ArrayList<HardSID> sidobjs = new ArrayList<HardSID>(4);

	private boolean m_status;

	private static HsidDLL2 hsid2;
	private int sid6581, sid8580;

	private IConfig iniCfg;

	private int init() {
		{
			// Extract fake HardSID driver recognizing fake and real devices
			m_status = false;
			final String driverPath = extract("/hardsid/cpp/Debug/",
					"HardSID.dll");
			if (driverPath == null) {
				m_status = false;
				m_errorBuffer = String
						.format("HARDSID ERROR: HardSID.dll not found");
				return -1;
			}
			// Extract original HardSID4U driver, loaded by the driver above
			final String origDriverPath = extract("/hardsid/cpp/Debug/",
					"HardSID_orig.dll");
			if (origDriverPath == null) {
				m_status = false;
				m_errorBuffer = String
						.format("HARDSID ERROR: HardSID_orig.dll not found");
				return -1;
			}
			// Extract JNI driver wrapper
			final String jniDriverPath = extract("/hardsid_builder/cpp/Debug/",
					"JHardSID.dll");
			try {
				if (jniDriverPath != null) {
					System.load(jniDriverPath);
				} else {
					throw new UnsatisfiedLinkError();
				}
			} catch (final UnsatisfiedLinkError e) {
				m_errorBuffer = String
						.format("HARDSID ERROR: JHardSID.dll not found!");
				return -1;
			}
			// Go and use JNI driver wrapper
			hsid2 = new HsidDLL2();
			// JNI driver wrapper loads the fake HardSID driver
			// (that internally loads the original HardSID driver)
			if (!hsid2.LoadLibrary(driverPath)) {
				m_errorBuffer = driverPath + " could not be loaded!";
				return -1;
			}
			hsid2.InitHardSID_Mapper();
		}

		{
			// Check major version
			final int version = hsid2.HardSID_Version();
			if (version >> 8 < HSID_VERSION_MIN >> 8) {
				m_errorBuffer = String.format(
						"HARDSID ERROR: HardSID.dll not V%d",
						HSID_VERSION_MIN >> 8);
				return -1;
			}
			// Check minor version
			if (version < HSID_VERSION_MIN) {
				m_errorBuffer = String
						.format("HARDSID ERROR: HardSID.dll must be V%02d.%02d or greater",
								HSID_VERSION_MIN >> 8, HSID_VERSION_MIN & 0xff);
				return -1;
			}
		}

		m_status = true;
		return 0;
	}

	private String extract(final String path, final String libName) {
		try {
			final InputStream str = getClass().getResourceAsStream(
					path + libName);
			try {
				File f = new File(new File(iniCfg.getSidplay2().getTmpDir()),
						libName);
				f.deleteOnExit();
				// install new library
				System.out.println("Save library: " + f.getAbsolutePath());
				final BufferedOutputStream bout = new BufferedOutputStream(
						new FileOutputStream(f));
				copyFile(str, bout);
				return f.getAbsolutePath();
			} catch (final IOException e1) {
				e1.printStackTrace();
				return null;
			}
		} catch (final AccessControlException e) {
			// unsigned ui.
			return null;
		}
	}

	private void copyFile(final InputStream in, final OutputStream out)
			throws IOException {
		// Transfer bytes from in to out
		final byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public HardSIDBuilder(IConfig iniCfg) {
		this.iniCfg = iniCfg;
		sid8580 = iniCfg.getEmulation().getHardsid8580() - 1;
		sid6581 = iniCfg.getEmulation().getHardsid6581() - 1;
		m_errorBuffer = "N/A";
		m_status = true;

		if (init() < 0) {
			System.err.println(error());
		}
	}

	@Override
	public SIDEmu lock(final EventScheduler context, ChipModel model) {
		ChipModel alreadyUsedModel = null;
		if (sidobjs.size() > 0) {
			// Stereo? Use a HardSID different to the first SID
			alreadyUsedModel = sidobjs.get(0).getChipModel();
			if (model == alreadyUsedModel) {
				if (model == ChipModel.MOS6581) {
					model = ChipModel.MOS8580;
				} else {
					model = ChipModel.MOS6581;
				}
			}
		}
		HardSID hsid = new HardSID(context, hsid2,
				model == ChipModel.MOS6581 ? sid6581 : sid8580, model);
		if (hsid.bool()) {
			if (hsid.lock(true)) {
				sidobjs.add(hsid);
				for (HardSID hardSid : sidobjs) {
					hardSid.setChipsUsed(sidobjs.size());
				}
				return hsid;
			}
		}

		// Unable to locate free SID
		m_status = false;
		m_errorBuffer = "HardSID ERROR: No available SIDs to lock";
		return null;
	}

	public boolean bool() {
		return m_status;
	}

	@Override
	public void unlock(final SIDEmu device) {
		for (HardSID hardSid : sidobjs) {
			hardSid.setChipsUsed(sidobjs.size() - 1);
			hardSid.flush();
			hardSid.reset((byte) 0);
		}
		if (sidobjs.remove(device)) {
			((HardSID) device).lock(false);
		}
	}

	@Override
	public void setSIDVolume(int sidNum, float volumnInDb) {
	}

	@Override
	public int getNumDevices() {
		return hsid2.GetHardSIDCount();
	}
	
	public String error() {
		return m_errorBuffer;
	}

	public static final String credits() {
		String credit = String.format("HardSID V%s Engine:\n", VERSION);
		credit += "\tCopyright (C) 1999-2002 Simon White <sidplay2@yahoo.com>\n";
		return credit;
	}

}
