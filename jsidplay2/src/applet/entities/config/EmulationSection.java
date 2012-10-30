package applet.entities.config;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import libsidplay.common.ISID2Types.Clock;
import resid_builder.resid.ISIDDefs.ChipModel;
import sidplay.ini.intf.IEmulationSection;

@Embeddable
public class EmulationSection implements IEmulationSection {

	@Enumerated(EnumType.STRING)
	private Clock defaultClockSpeed = Clock.PAL;

	@Override
	public Clock getDefaultClockSpeed() {
		return this.defaultClockSpeed;
	}

	@Override
	public void setDefaultClockSpeed(Clock speed) {
		this.defaultClockSpeed = speed;
	}

	@Enumerated(EnumType.STRING)
	private Clock userClockSpeed;

	@Override
	public Clock getUserClockSpeed() {
		return userClockSpeed;
	}

	@Override
	public void setUserClockSpeed(Clock userClockSpeed) {
		this.userClockSpeed = userClockSpeed;
	}

	private ChipModel defaultSidModel = ChipModel.MOS6581;

	@Override
	public ChipModel getDefaultSidModel() {
		return defaultSidModel;
	}

	@Override
	public void setDefaultSidModel(ChipModel defaultSidModel) {
		this.defaultSidModel = defaultSidModel;
	}

	private ChipModel userSidModel;

	@Override
	public ChipModel getUserSidModel() {
		return userSidModel;
	}

	@Override
	public void setUserSidModel(ChipModel userSidModel) {
		this.userSidModel = userSidModel;
	}

	private int hardsid6581 = 1;

	@Override
	public int getHardsid6581() {
		return hardsid6581;
	}

	@Override
	public void setHardsid6581(int hardsid6581) {
		this.hardsid6581 = hardsid6581;
	}

	private int hardsid8580 = 2;

	@Override
	public int getHardsid8580() {
		return hardsid8580;
	}

	@Override
	public void setHardsid8580(int hardsid8580) {
		this.hardsid8580 = hardsid8580;
	}

	private boolean filter = true;

	@Override
	public boolean isFilter() {
		return filter;
	}

	@Override
	public void setFilter(boolean isFilter) {
		this.filter = isFilter;
	}

	private String filter6581 = "FilterAverage6581";

	@Override
	public String getFilter6581() {
		return filter6581;
	}

	@Override
	public void setFilter6581(String filter6581) {
		this.filter6581 = filter6581;
	}

	private String filter8580 = "FilterAverage8580";

	@Override
	public String getFilter8580() {
		return filter8580;
	}

	@Override
	public void setFilter8580(String filter8580) {
		this.filter8580 = filter8580;
	}

	private boolean digiBoosted8580;

	@Override
	public boolean isDigiBoosted8580() {
		return digiBoosted8580;
	}

	@Override
	public void setDigiBoosted8580(boolean isDigiBoosted8580) {
		this.digiBoosted8580 = isDigiBoosted8580;
	}

	private int dualSidBase = 0xd420;

	@Override
	public int getDualSidBase() {
		return dualSidBase;
	}

	@Override
	public void setDualSidBase(int dualSidBase) {
		this.dualSidBase = dualSidBase;
	}

	private boolean forceStereoTune;

	@Override
	public boolean isForceStereoTune() {
		return forceStereoTune;
	}

	@Override
	public void setForceStereoTune(boolean isForceStereoTune) {
		this.forceStereoTune = isForceStereoTune;
	}

	private ChipModel stereoSidModel;

	@Override
	public ChipModel getStereoSidModel() {
		return stereoSidModel;
	}

	@Override
	public void setStereoSidModel(ChipModel stereoSidModel) {
		this.stereoSidModel = stereoSidModel;
	}
}