package ui.oscilloscope;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TitledPane;
import libsidplay.common.SIDEmu;
import sidplay.Player;
import ui.common.C64Window;

public final class FilterGauge extends SIDGauge {

	@FXML
	private TitledPane border;
	@FXML
	private Canvas area;

	public FilterGauge(C64Window window, Player player) {
		super(window, player);
	}

	@Override
	protected Canvas getArea() {
		return area;
	}

	@Override
	protected TitledPane getTitledPane() {
		return border;
	}

	@Override
	public SIDGauge sample(SIDEmu sidemu) {
		int fc = sidemu.readInternalRegister(0x15) & 7;
		fc |= (sidemu.readInternalRegister(0x16) & 0xff) << 3;
		accumulate(fc / 2047.0f);
		return this;
	}

	@Override
	public void updateGauge(SIDEmu sidemu) {
		super.updateGauge(sidemu);
		if (sidemu != null) {
			final byte vol = sidemu.readInternalRegister(0x18);
			setText((localizer != null ? localizer.getString("FILTER") : "") + " " + ((vol & 0x10) != 0 ? "L" : "")
					+ ((vol & 0x20) != 0 ? "B" : "") + ((vol & 0x40) != 0 ? "H" : ""));
		}
	}
}