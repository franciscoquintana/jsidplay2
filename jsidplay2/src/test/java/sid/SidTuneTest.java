package sid;

import static javafx.scene.input.KeyCode.ENTER;
import javafx.scene.control.Label;

import org.junit.Assert;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import resid_builder.resid.ChipModel;

import common.JSIDPlay2Test;

public class SidTuneTest extends JSIDPlay2Test {

	@Test
	public void detectSidModelTest() {
		config.getSidplay2().setLastDirectory("target/test-classes/sid");

		config.getEmulation().setUserSidModel(null);
		config.getEmulation().setDefaultSidModel(ChipModel.MOS8580);
		click("#file");
		click("#load");
		sleep(FILE_BROWSER_OPENED_TIMEOUT);
		type("sid_detection.prg");
		push(ENTER);
		sleep(SID_TUNE_LOADED_TIMEOUT);
		Assert.assertTrue(checkScreenMessage("b", 1, 1));

		config.getEmulation().setDefaultSidModel(ChipModel.MOS6581);
		player.play(player.getTune());
		sleep(SID_TUNE_LOADED_TIMEOUT);
		Assert.assertTrue(checkScreenMessage("a", 1, 1));
	}

	@Test
	public void loadPSidStereoTest() {
		config.getSidplay2().setLastDirectory(
				"target/test-classes/sid/examples/stereo/2sids/Nata");
		click("#file");
		click("#load");
		sleep(FILE_BROWSER_OPENED_TIMEOUT);
		type("Anthrox_2SID.sid");
		push(ENTER);
		sleep(SID_TUNE_LOADED_TIMEOUT);

		Assert.assertTrue(player.getC64().getSID(0) != null);
		Assert.assertTrue(player.getC64().getSID(1) != null);
		// Driver
		Assert.assertTrue(checkRam(0x0400, new byte[] { 0x4c, 0x03, 0x10 }));
		// Load/Init
		Assert.assertTrue(checkRam(0x1000, new byte[] { 0x4c, 0x06, 0x10 }));
		// Play
		Assert.assertTrue(checkRam(0x1003, new byte[] { 0x4c, 0x40, 0x10 }));
	}

	@Test
	public void loadPSidTest() {
		config.getSidplay2().setLastDirectory(
				"target/test-classes/sid/examples");
		click("#file");
		click("#load");
		sleep(FILE_BROWSER_OPENED_TIMEOUT);
		type("Turrican_2-The_Final_Fight.sid");
		push(ENTER);
		sleep(SID_TUNE_LOADED_TIMEOUT);

		// Driver
		Assert.assertTrue(checkRam(0xc000, new byte[] { 0x4c, 0x00, 0x08 }));
		// Load/Play
		Assert.assertTrue(checkRam(0x0800,
				new byte[] { 0x4c, (byte) 0xb6, 0x66 }));
		// Init
		Assert.assertTrue(checkRam(0x0803, new byte[] { (byte) 0xa2, 0x35 }));
	}

	@Test
	public void loadP00Test() {
		config.getSidplay2().setLastDirectory(
				"target/test-classes/sid/examples");
		click("#file");
		click("#load");
		sleep(FILE_BROWSER_OPENED_TIMEOUT);
		type("PUZZLEND.P00");
		push(ENTER);
		sleep(SID_TUNE_LOADED_TIMEOUT);

		Assert.assertTrue(checkRam(0x0801, new byte[] { (byte) 0xde, 0x38,
				(byte) 0xa9 }));
	}

	@Test
	public void loadMusTest() {
		config.getSidplay2().setLastDirectory(
				"target/test-classes/sid/examples");
		click("#file");
		click("#load");
		sleep(FILE_BROWSER_OPENED_TIMEOUT);
		type("Angie_A.mus");
		push(ENTER);
		sleep(SID_TUNE_LOADED_TIMEOUT);

		// Driver
		Assert.assertTrue(checkRam(0x0400, new byte[] { 0x4c, (byte) 0x80,
				(byte) 0xec }));
		// Load
		Assert.assertTrue(checkRam(0x0900, new byte[] { (byte) 0xc9, 0x28 }));
		// Init
		Assert.assertTrue(checkRam(0xec60, new byte[] { (byte) 0xa2, 0x51 }));
		// Play
		Assert.assertTrue(checkRam(0xec80, new byte[] { (byte) 0xa9, 0x07 }));
	}

	@Test
	public void loadMusStereoTest() {
		config.getSidplay2().setLastDirectory(
				"target/test-classes/sid/examples/stereo");
		click("#file");
		click("#load");
		sleep(FILE_BROWSER_OPENED_TIMEOUT);
		type("Safety_Dance.mus");
		push(ENTER);
		sleep(SID_TUNE_LOADED_TIMEOUT);

		Assert.assertTrue(player.getC64().getSID(0) != null);
		Assert.assertTrue(player.getC64().getSID(1) != null);
		// Driver
		Assert.assertTrue(checkRam(0x0400, new byte[] { 0x4c, (byte) 0x96,
				(byte) 0xfc }));
		// Load
		Assert.assertTrue(checkRam(0x0900, new byte[] { 0x00 }));
		// Init
		Assert.assertTrue(checkRam(0xfc90,
				new byte[] { 0x20, 0x60, (byte) 0xec }));
		// Play
		Assert.assertTrue(checkRam(0xfc96, new byte[] { 0x20, (byte) 0x80,
				(byte) 0xec }));
	}

	@Test
	public void loadPrgTest() {
		config.getSidplay2().setLastDirectory(
				"target/test-classes/sid/examples");
		click("#file");
		click("#load");
		sleep(FILE_BROWSER_OPENED_TIMEOUT);
		type("radiantx_spiral_silicon_towers.prg");
		push(ENTER);
		sleep(SID_TUNE_LOADED_TIMEOUT);

		Assert.assertTrue(checkScreenMessage("Spiral Silicon Towers", 2, 1));
	}

	@Test
	public void statusTest() {
		Label status = ((Label) GuiTest.find("#status"));

		Assert.assertNotNull(status);
	}

}