package sidplay.ini;

import static sidplay.ini.IniDefaults.DEFAULT_3SID_EMULATION;
import static sidplay.ini.IniDefaults.DEFAULT_3SID_FILTER_6581;
import static sidplay.ini.IniDefaults.DEFAULT_3SID_FILTER_8580;
import static sidplay.ini.IniDefaults.DEFAULT_3SID_MODEL;
import static sidplay.ini.IniDefaults.DEFAULT_CLOCK_SPEED;
import static sidplay.ini.IniDefaults.DEFAULT_DIGI_BOOSTED_8580;
import static sidplay.ini.IniDefaults.DEFAULT_DUAL_SID_BASE;
import static sidplay.ini.IniDefaults.DEFAULT_EMULATION;
import static sidplay.ini.IniDefaults.DEFAULT_ENGINE;
import static sidplay.ini.IniDefaults.DEFAULT_FAKE_STEREO;
import static sidplay.ini.IniDefaults.DEFAULT_FILTER_6581;
import static sidplay.ini.IniDefaults.DEFAULT_FILTER_8580;
import static sidplay.ini.IniDefaults.DEFAULT_FORCE_3SID_TUNE;
import static sidplay.ini.IniDefaults.DEFAULT_FORCE_STEREO_TUNE;
import static sidplay.ini.IniDefaults.DEFAULT_HARD_SID_6581;
import static sidplay.ini.IniDefaults.DEFAULT_HARD_SID_8580;
import static sidplay.ini.IniDefaults.DEFAULT_NETSIDDEV_HOST;
import static sidplay.ini.IniDefaults.DEFAULT_NETSIDDEV_PORT;
import static sidplay.ini.IniDefaults.DEFAULT_MUTE_STEREO_VOICE1;
import static sidplay.ini.IniDefaults.DEFAULT_MUTE_STEREO_VOICE2;
import static sidplay.ini.IniDefaults.DEFAULT_MUTE_STEREO_VOICE3;
import static sidplay.ini.IniDefaults.DEFAULT_MUTE_STEREO_VOICE4;
import static sidplay.ini.IniDefaults.DEFAULT_MUTE_THIRDSID_VOICE1;
import static sidplay.ini.IniDefaults.DEFAULT_MUTE_THIRDSID_VOICE2;
import static sidplay.ini.IniDefaults.DEFAULT_MUTE_THIRDSID_VOICE3;
import static sidplay.ini.IniDefaults.DEFAULT_MUTE_THIRDSID_VOICE4;
import static sidplay.ini.IniDefaults.DEFAULT_MUTE_VOICE1;
import static sidplay.ini.IniDefaults.DEFAULT_MUTE_VOICE2;
import static sidplay.ini.IniDefaults.DEFAULT_MUTE_VOICE3;
import static sidplay.ini.IniDefaults.DEFAULT_MUTE_VOICE4;
import static sidplay.ini.IniDefaults.DEFAULT_NETSID_3SID_FILTER_6581;
import static sidplay.ini.IniDefaults.DEFAULT_NETSID_3SID_FILTER_8580;
import static sidplay.ini.IniDefaults.DEFAULT_NETSID_FILTER_6581;
import static sidplay.ini.IniDefaults.DEFAULT_NETSID_FILTER_8580;
import static sidplay.ini.IniDefaults.DEFAULT_NETSID_STEREO_FILTER_6581;
import static sidplay.ini.IniDefaults.DEFAULT_NETSID_STEREO_FILTER_8580;
import static sidplay.ini.IniDefaults.DEFAULT_ReSIDfp_3SID_FILTER_6581;
import static sidplay.ini.IniDefaults.DEFAULT_ReSIDfp_3SID_FILTER_8580;
import static sidplay.ini.IniDefaults.DEFAULT_ReSIDfp_FILTER_6581;
import static sidplay.ini.IniDefaults.DEFAULT_ReSIDfp_FILTER_8580;
import static sidplay.ini.IniDefaults.DEFAULT_ReSIDfp_STEREO_FILTER_6581;
import static sidplay.ini.IniDefaults.DEFAULT_ReSIDfp_STEREO_FILTER_8580;
import static sidplay.ini.IniDefaults.DEFAULT_SID_MODEL;
import static sidplay.ini.IniDefaults.DEFAULT_SID_NUM_TO_READ;
import static sidplay.ini.IniDefaults.DEFAULT_STEREO_EMULATION;
import static sidplay.ini.IniDefaults.DEFAULT_STEREO_FILTER_6581;
import static sidplay.ini.IniDefaults.DEFAULT_STEREO_FILTER_8580;
import static sidplay.ini.IniDefaults.DEFAULT_STEREO_MODEL;
import static sidplay.ini.IniDefaults.DEFAULT_THIRD_SID_BASE;
import static sidplay.ini.IniDefaults.DEFAULT_USER_CLOCK_SPEED;
import static sidplay.ini.IniDefaults.DEFAULT_USER_EMULATION;
import static sidplay.ini.IniDefaults.DEFAULT_USER_MODEL;
import static sidplay.ini.IniDefaults.DEFAULT_USE_3SID_FILTER;
import static sidplay.ini.IniDefaults.DEFAULT_USE_FILTER;
import static sidplay.ini.IniDefaults.DEFAULT_USE_STEREO_FILTER;

import libsidplay.common.CPUClock;
import libsidplay.common.ChipModel;
import libsidplay.common.Emulation;
import libsidplay.common.Engine;
import libsidplay.config.IEmulationSection;

/**
 * Emulation section of the INI file.
 * 
 * @author Ken Händel
 * 
 */
public class IniEmulationSection extends IniSection implements IEmulationSection {
	protected IniEmulationSection(IniReader iniReader) {
		super(iniReader);
	}

	@Override
	public Engine getEngine() {
		return iniReader.getPropertyEnum("Emulation", "Engine", DEFAULT_ENGINE, Engine.class);
	}

	@Override
	public void setEngine(Engine engine) {
		iniReader.setProperty("Emulation", "Engine", engine);
	}

	@Override
	public Emulation getDefaultEmulation() {
		return iniReader.getPropertyEnum("Emulation", "DefaultEmulation", DEFAULT_EMULATION, Emulation.class);
	}

	@Override
	public void setDefaultEmulation(Emulation emulation) {
		iniReader.setProperty("Emulation", "DefaultEmulation", emulation);
	}

	@Override
	public final Emulation getUserEmulation() {
		return iniReader.getPropertyEnum("Emulation", "UserEmulation", DEFAULT_USER_EMULATION, Emulation.class);
	}

	@Override
	public final void setUserEmulation(final Emulation emulation) {
		iniReader.setProperty("Emulation", "UserEmulation", emulation);
	}

	@Override
	public final Emulation getStereoEmulation() {
		return iniReader.getPropertyEnum("Emulation", "StereoEmulation", DEFAULT_STEREO_EMULATION, Emulation.class);
	}

	@Override
	public final void setStereoEmulation(final Emulation model) {
		iniReader.setProperty("Emulation", "StereoEmulation", model);
	}

	@Override
	public final Emulation getThirdEmulation() {
		return iniReader.getPropertyEnum("Emulation", "3rdEmulation", DEFAULT_3SID_EMULATION, Emulation.class);
	}

	@Override
	public final void setThirdEmulation(final Emulation emulation) {
		iniReader.setProperty("Emulation", "3rdEmulation", emulation);
	}

	@Override
	public final CPUClock getDefaultClockSpeed() {
		return iniReader.getPropertyEnum("Emulation", "DefaultClockSpeed", DEFAULT_CLOCK_SPEED, CPUClock.class);
	}

	@Override
	public final void setDefaultClockSpeed(final CPUClock speed) {
		iniReader.setProperty("Emulation", "DefaultClockSpeed", speed);
	}

	@Override
	public final CPUClock getUserClockSpeed() {
		return iniReader.getPropertyEnum("Emulation", "UserClockSpeed", DEFAULT_USER_CLOCK_SPEED, CPUClock.class);
	}

	@Override
	public final void setUserClockSpeed(final CPUClock speed) {
		iniReader.setProperty("Emulation", "UserClockSpeed", speed);
	}

	@Override
	public final ChipModel getDefaultSidModel() {
		return iniReader.getPropertyEnum("Emulation", "DefaultSidModel", DEFAULT_SID_MODEL, ChipModel.class);
	}

	@Override
	public final void setDefaultSidModel(ChipModel model) {
		iniReader.setProperty("Emulation", "DefaultSidModel", model);
	}

	@Override
	public final ChipModel getUserSidModel() {
		return iniReader.getPropertyEnum("Emulation", "UserSidModel", DEFAULT_USER_MODEL, ChipModel.class);
	}

	@Override
	public final void setUserSidModel(final ChipModel model) {
		iniReader.setProperty("Emulation", "UserSidModel", model);
	}

	@Override
	public final ChipModel getStereoSidModel() {
		return iniReader.getPropertyEnum("Emulation", "StereoSidModel", DEFAULT_STEREO_MODEL, ChipModel.class);
	}

	@Override
	public final void setStereoSidModel(final ChipModel model) {
		iniReader.setProperty("Emulation", "StereoSidModel", model);
	}

	@Override
	public final ChipModel getThirdSIDModel() {
		return iniReader.getPropertyEnum("Emulation", "3rdSIDModel", DEFAULT_3SID_MODEL, ChipModel.class);
	}

	@Override
	public final void setThirdSIDModel(final ChipModel model) {
		iniReader.setProperty("Emulation", "3rdSIDModel", model);
	}

	@Override
	public final int getHardsid6581() {
		return iniReader.getPropertyInt("Emulation", "HardSID6581", DEFAULT_HARD_SID_6581);
	}

	@Override
	public final void setHardsid6581(final int chip) {
		iniReader.setProperty("Emulation", "HardSID6581", chip);
	}

	@Override
	public final int getHardsid8580() {
		return iniReader.getPropertyInt("Emulation", "HardSID8580", DEFAULT_HARD_SID_8580);
	}

	@Override
	public final void setHardsid8580(final int chip) {
		iniReader.setProperty("Emulation", "HardSID8580", chip);
	}

	@Override
	public String getNetSIDDevHost() {
		return iniReader.getPropertyString("Emulation", "NetSIDDev Host", DEFAULT_NETSIDDEV_HOST);
	}
	
	@Override
	public void setNetSIDDevHost(String hostname) {
		iniReader.setProperty("Emulation", "NetSIDDev Host", hostname);
	}
	
	@Override
	public int getNetSIDDevPort() {
		return iniReader.getPropertyInt("Emulation", "NetSIDDev Port", DEFAULT_NETSIDDEV_PORT);
	}
	
	@Override
	public void setNetSIDDevPort(int port) {
		iniReader.setProperty("Emulation", "NetSIDDev Port", port);
	}
	
	@Override
	public final boolean isFilter() {
		return iniReader.getPropertyBool("Emulation", "UseFilter", DEFAULT_USE_FILTER);
	}

	@Override
	public final void setFilter(final boolean enable) {
		iniReader.setProperty("Emulation", "UseFilter", enable);
	}

	@Override
	public final boolean isStereoFilter() {
		return iniReader.getPropertyBool("Emulation", "UseStereoFilter", DEFAULT_USE_STEREO_FILTER);
	}

	@Override
	public final void setStereoFilter(final boolean enable) {
		iniReader.setProperty("Emulation", "UseStereoFilter", enable);
	}

	@Override
	public final boolean isThirdSIDFilter() {
		return iniReader.getPropertyBool("Emulation", "Use3rdSIDFilter", DEFAULT_USE_3SID_FILTER);
	}

	@Override
	public final void setThirdSIDFilter(final boolean enable) {
		iniReader.setProperty("Emulation", "Use3rdSIDFilter", enable);
	}

	@Override
	public int getSidNumToRead() {
		return iniReader.getPropertyInt("Emulation", "SidNumToRead", DEFAULT_SID_NUM_TO_READ);
	}

	@Override
	public void setSidNumToRead(int sidNumToRead) {
		iniReader.setProperty("Emulation", "SidNumToRead", sidNumToRead);
	}

	@Override
	public final boolean isDigiBoosted8580() {
		return iniReader.getPropertyBool("Emulation", "DigiBoosted8580", DEFAULT_DIGI_BOOSTED_8580);
	}

	@Override
	public final void setDigiBoosted8580(final boolean boost) {
		iniReader.setProperty("Emulation", "DigiBoosted8580", boost);
	}

	@Override
	public final int getDualSidBase() {
		return iniReader.getPropertyInt("Emulation", "dualSidBase", DEFAULT_DUAL_SID_BASE);
	}

	@Override
	public final void setDualSidBase(final int base) {
		iniReader.setProperty("Emulation", "dualSidBase", String.format("0x%04x", base));
	}

	@Override
	public final int getThirdSIDBase() {
		return iniReader.getPropertyInt("Emulation", "thirdSIDBase", DEFAULT_THIRD_SID_BASE);
	}

	@Override
	public final void setThirdSIDBase(final int base) {
		iniReader.setProperty("Emulation", "thirdSIDBase", String.format("0x%04x", base));
	}

	@Override
	public final boolean isFakeStereo() {
		return iniReader.getPropertyBool("Emulation", "fakeStereo", DEFAULT_FAKE_STEREO);
	}

	@Override
	public final void setFakeStereo(boolean fakeStereo) {
		iniReader.setProperty("Emulation", "fakeStereo", fakeStereo);
	}

	@Override
	public final boolean isForceStereoTune() {
		return iniReader.getPropertyBool("Emulation", "forceStereoTune", DEFAULT_FORCE_STEREO_TUNE);
	}

	@Override
	public final void setForceStereoTune(final boolean force) {
		iniReader.setProperty("Emulation", "forceStereoTune", force);
	}

	@Override
	public final boolean isForce3SIDTune() {
		return iniReader.getPropertyBool("Emulation", "force3SIDTune", DEFAULT_FORCE_3SID_TUNE);
	}

	@Override
	public final void setForce3SIDTune(final boolean force) {
		iniReader.setProperty("Emulation", "force3SIDTune", force);
	}

	@Override
	public boolean isMuteVoice1() {
		return iniReader.getPropertyBool("Emulation", "muteVoice1", DEFAULT_MUTE_VOICE1);
	}

	@Override
	public void setMuteVoice1(boolean mute) {
		iniReader.setProperty("Emulation", "muteVoice1", mute);
	}

	@Override
	public boolean isMuteVoice2() {
		return iniReader.getPropertyBool("Emulation", "muteVoice2", DEFAULT_MUTE_VOICE2);
	}

	@Override
	public void setMuteVoice2(boolean mute) {
		iniReader.setProperty("Emulation", "muteVoice2", mute);
	}

	@Override
	public boolean isMuteVoice3() {
		return iniReader.getPropertyBool("Emulation", "muteVoice3", DEFAULT_MUTE_VOICE3);
	}

	@Override
	public void setMuteVoice3(boolean mute) {
		iniReader.setProperty("Emulation", "muteVoice3", mute);
	}

	@Override
	public boolean isMuteVoice4() {
		return iniReader.getPropertyBool("Emulation", "muteVoice4", DEFAULT_MUTE_VOICE4);
	}

	@Override
	public void setMuteVoice4(boolean mute) {
		iniReader.setProperty("Emulation", "muteVoice4", mute);
	}

	@Override
	public boolean isMuteStereoVoice1() {
		return iniReader.getPropertyBool("Emulation", "muteStereoVoice1", DEFAULT_MUTE_STEREO_VOICE1);
	}

	@Override
	public void setMuteStereoVoice1(boolean mute) {
		iniReader.setProperty("Emulation", "muteStereoVoice1", mute);
	}

	@Override
	public boolean isMuteStereoVoice2() {
		return iniReader.getPropertyBool("Emulation", "muteStereoVoice2", DEFAULT_MUTE_STEREO_VOICE2);
	}

	@Override
	public void setMuteStereoVoice2(boolean mute) {
		iniReader.setProperty("Emulation", "muteStereoVoice2", mute);
	}

	@Override
	public boolean isMuteStereoVoice3() {
		return iniReader.getPropertyBool("Emulation", "muteStereoVoice3", DEFAULT_MUTE_STEREO_VOICE3);
	}

	@Override
	public void setMuteStereoVoice3(boolean mute) {
		iniReader.setProperty("Emulation", "muteStereoVoice3", mute);
	}

	@Override
	public boolean isMuteStereoVoice4() {
		return iniReader.getPropertyBool("Emulation", "muteStereoVoice4", DEFAULT_MUTE_STEREO_VOICE4);
	}

	@Override
	public void setMuteStereoVoice4(boolean mute) {
		iniReader.setProperty("Emulation", "muteStereoVoice4", mute);
	}

	@Override
	public boolean isMuteThirdSIDVoice1() {
		return iniReader.getPropertyBool("Emulation", "muteThirdSIDVoice1", DEFAULT_MUTE_THIRDSID_VOICE1);
	}

	@Override
	public void setMuteThirdSIDVoice1(boolean mute) {
		iniReader.setProperty("Emulation", "muteThirdSIDVoice1", mute);
	}

	@Override
	public boolean isMuteThirdSIDVoice2() {
		return iniReader.getPropertyBool("Emulation", "muteThirdSIDVoice2", DEFAULT_MUTE_THIRDSID_VOICE2);
	}

	@Override
	public void setMuteThirdSIDVoice2(boolean mute) {
		iniReader.setProperty("Emulation", "muteThirdSIDVoice2", mute);
	}

	@Override
	public boolean isMuteThirdSIDVoice3() {
		return iniReader.getPropertyBool("Emulation", "muteThirdSIDVoice3", DEFAULT_MUTE_THIRDSID_VOICE3);
	}

	@Override
	public void setMuteThirdSIDVoice3(boolean mute) {
		iniReader.setProperty("Emulation", "muteThirdSIDVoice3", mute);
	}

	@Override
	public boolean isMuteThirdSIDVoice4() {
		return iniReader.getPropertyBool("Emulation", "muteThirdSIDVoice4", DEFAULT_MUTE_THIRDSID_VOICE4);
	}

	@Override
	public void setMuteThirdSIDVoice4(boolean mute) {
		iniReader.setProperty("Emulation", "muteThirdSIDVoice4", mute);
	}

	@Override
	public final String getNetSIDFilter6581() {
		return iniReader.getPropertyString("Emulation", "NetSID_Filter6581", DEFAULT_NETSID_FILTER_6581);
	}

	@Override
	public final void setNetSIDFilter6581(final String filterName) {
		iniReader.setProperty("Emulation", "NetSID_Filter6581", filterName);
	}

	@Override
	public final String getNetSIDStereoFilter6581() {
		return iniReader.getPropertyString("Emulation", "NetSID_Stereo_Filter6581", DEFAULT_NETSID_STEREO_FILTER_6581);
	}

	@Override
	public final void setNetSIDStereoFilter6581(final String filterName) {
		iniReader.setProperty("Emulation", "NetSID_Stereo_Filter6581", filterName);
	}

	@Override
	public final String getNetSIDThirdSIDFilter6581() {
		return iniReader.getPropertyString("Emulation", "NetSID_3rdSID_Filter6581", DEFAULT_NETSID_3SID_FILTER_6581);
	}

	@Override
	public final void setNetSIDThirdSIDFilter6581(final String filterName) {
		iniReader.setProperty("Emulation", "NetSID_3rdSID_Filter6581", filterName);
	}

	@Override
	public final String getNetSIDFilter8580() {
		return iniReader.getPropertyString("Emulation", "NetSID_Filter8580", DEFAULT_NETSID_FILTER_8580);
	}

	public final void setNetSIDFilter8580(final String filterName) {
		iniReader.setProperty("Emulation", "NetSID_Filter8580", filterName);
	}

	@Override
	public final String getNetSIDStereoFilter8580() {
		return iniReader.getPropertyString("Emulation", "NetSID_Stereo_Filter8580", DEFAULT_NETSID_STEREO_FILTER_8580);
	}

	@Override
	public final void setNetSIDStereoFilter8580(final String filterName) {
		iniReader.setProperty("Emulation", "NetSID_Stereo_Filter8580", filterName);
	}

	@Override
	public final String getNetSIDThirdSIDFilter8580() {
		return iniReader.getPropertyString("Emulation", "NetSID_3rdSID_Filter8580", DEFAULT_NETSID_3SID_FILTER_8580);
	}

	@Override
	public final void setNetSIDThirdSIDFilter8580(final String filterName) {
		iniReader.setProperty("Emulation", "NetSID_3rdSID_Filter8580", filterName);
	}

	@Override
	public final String getFilter6581() {
		return iniReader.getPropertyString("Emulation", "Filter6581", DEFAULT_FILTER_6581);
	}

	@Override
	public final void setFilter6581(final String filterName) {
		iniReader.setProperty("Emulation", "Filter6581", filterName);
	}

	@Override
	public final String getStereoFilter6581() {
		return iniReader.getPropertyString("Emulation", "Stereo_Filter6581", DEFAULT_STEREO_FILTER_6581);
	}

	@Override
	public final void setStereoFilter6581(final String filterName) {
		iniReader.setProperty("Emulation", "Stereo_Filter6581", filterName);
	}

	@Override
	public final String getThirdSIDFilter6581() {
		return iniReader.getPropertyString("Emulation", "3rdSID_Filter6581", DEFAULT_3SID_FILTER_6581);
	}

	@Override
	public final void setThirdSIDFilter6581(final String filterName) {
		iniReader.setProperty("Emulation", "3rdSID_Filter6581", filterName);
	}

	@Override
	public final String getFilter8580() {
		return iniReader.getPropertyString("Emulation", "Filter8580", DEFAULT_FILTER_8580);
	}

	@Override
	public final void setFilter8580(final String filterName) {
		iniReader.setProperty("Emulation", "Filter8580", filterName);
	}

	@Override
	public final String getStereoFilter8580() {
		return iniReader.getPropertyString("Emulation", "Stereo_Filter8580", DEFAULT_STEREO_FILTER_8580);
	}

	@Override
	public final void setStereoFilter8580(final String filterName) {
		iniReader.setProperty("Emulation", "Stereo_Filter8580", filterName);
	}

	@Override
	public final String getThirdSIDFilter8580() {
		return iniReader.getPropertyString("Emulation", "3rdSID_Filter8580", DEFAULT_3SID_FILTER_8580);
	}

	@Override
	public final void setThirdSIDFilter8580(final String filterName) {
		iniReader.setProperty("Emulation", "3rdSID_Filter8580", filterName);
	}

	@Override
	public final String getReSIDfpFilter6581() {
		return iniReader.getPropertyString("Emulation", "ReSIDfp_Filter6581", DEFAULT_ReSIDfp_FILTER_6581);
	}

	@Override
	public final void setReSIDfpFilter6581(final String filterName) {
		iniReader.setProperty("Emulation", "ReSIDfp_Filter6581", filterName);
	}

	@Override
	public final String getReSIDfpStereoFilter6581() {
		return iniReader.getPropertyString("Emulation", "ReSIDfp_Stereo_Filter6581",
				DEFAULT_ReSIDfp_STEREO_FILTER_6581);
	}

	@Override
	public final void setReSIDfpStereoFilter6581(final String filterName) {
		iniReader.setProperty("Emulation", "ReSIDfp_Stereo_Filter6581", filterName);
	}

	@Override
	public final String getReSIDfpThirdSIDFilter6581() {
		return iniReader.getPropertyString("Emulation", "ReSIDfp_3rdSID_Filter6581", DEFAULT_ReSIDfp_3SID_FILTER_6581);
	}

	@Override
	public final void setReSIDfpThirdSIDFilter6581(final String filterName) {
		iniReader.setProperty("Emulation", "ReSIDfp_3rdSID_Filter6581", filterName);
	}

	@Override
	public final String getReSIDfpFilter8580() {
		return iniReader.getPropertyString("Emulation", "ReSIDfp_Filter8580", DEFAULT_ReSIDfp_FILTER_8580);
	}

	public final void setReSIDfpFilter8580(final String filterName) {
		iniReader.setProperty("Emulation", "ReSIDfp_Filter8580", filterName);
	}

	@Override
	public final String getReSIDfpStereoFilter8580() {
		return iniReader.getPropertyString("Emulation", "ReSIDfp_Stereo_Filter8580",
				DEFAULT_ReSIDfp_STEREO_FILTER_8580);
	}

	@Override
	public final void setReSIDfpStereoFilter8580(final String filterName) {
		iniReader.setProperty("Emulation", "ReSIDfp_Stereo_Filter8580", filterName);
	}

	@Override
	public final String getReSIDfpThirdSIDFilter8580() {
		return iniReader.getPropertyString("Emulation", "ReSIDfp_3rdSID_Filter8580", DEFAULT_ReSIDfp_3SID_FILTER_8580);
	}

	@Override
	public final void setReSIDfpThirdSIDFilter8580(final String filterName) {
		iniReader.setProperty("Emulation", "ReSIDfp_3rdSID_Filter8580", filterName);
	}

}