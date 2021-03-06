package ui.proxysettings;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import sidplay.Player;
import ui.common.C64Window;

public class ProxySettings extends C64Window {

	@FXML
	protected TextField proxyHost, proxyPort;
	@FXML
	private CheckBox proxyEnable;

	public ProxySettings(Player player) {
		super(player);
	}

	@FXML
	private void initialize() {
		proxyEnable.setSelected(util.getConfig().getSidplay2Section().isEnableProxy());
		proxyHost.setText(util.getConfig().getSidplay2Section().getProxyHostname());
		proxyHost.setEditable(proxyEnable.isSelected());
		proxyPort.setText(String.valueOf(util.getConfig().getSidplay2Section().getProxyPort()));
		proxyPort.setEditable(proxyEnable.isSelected());
	}

	@FXML
	private void setEnableProxy() {
		proxyHost.setEditable(proxyEnable.isSelected());
		proxyPort.setEditable(proxyEnable.isSelected());
		util.getConfig().getSidplay2Section().setEnableProxy(proxyEnable.isSelected());
	}

	@FXML
	private void setProxyHost() {
		util.getConfig().getSidplay2Section().setProxyHostname(proxyHost.getText());
	}

	@FXML
	private void setProxyPort() {
		util.getConfig().getSidplay2Section()
				.setProxyPort(proxyPort.getText().length() > 0 ? Integer.valueOf(proxyPort.getText()) : 80);
	}

}
