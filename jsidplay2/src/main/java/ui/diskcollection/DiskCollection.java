package ui.diskcollection;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.DirectoryChooser;
import libsidplay.sidtune.SidTuneError;
import libsidutils.PathUtils;
import libsidutils.DesktopIntegration;
import sidplay.Player;
import ui.common.C64Window;
import ui.common.Convenience;
import ui.common.UIPart;
import ui.common.UIUtil;
import ui.directory.Directory;
import ui.download.DownloadThread;
import ui.download.ProgressListener;
import ui.entities.config.SidPlay2Section;
import ui.filefilter.DiskFileFilter;
import ui.filefilter.DocsFileFilter;
import ui.filefilter.ScreenshotFileFilter;
import ui.filefilter.TapeFileFilter;

public class DiskCollection extends Tab implements UIPart {

	public static final String MAGS_ID = "MAGS";
	public static final String DEMOS_ID = "DEMOS";
	public static final String HVMEC_ID = "HVMEC";
	private static final String HVMEC_DATA = "DATA";
	private static final String HVMEC_CONTROL = "CONTROL";

	@FXML
	private CheckBox autoConfiguration;
	@FXML
	private Directory directory;
	@FXML
	private ImageView screenshot;
	@FXML
	private TreeView<File> fileBrowser;
	@FXML
	private ContextMenu contextMenu;
	@FXML
	private MenuItem start, attachDisk;
	@FXML
	private TextField collectionDir;

	private UIUtil util;

	private Convenience convenience;
	private ObjectProperty<DiskCollectionType> type;

	public DiskCollectionType getType() {
		return type.get();
	}

	public void setType(DiskCollectionType type) {
		switch (type) {
		case HVMEC:
			setId(HVMEC_ID);
			break;
		case DEMOS:
			setId(DEMOS_ID);
			break;
		case MAGS:
			setId(MAGS_ID);
			break;

		default:
			break;
		}
		setText(util.getBundle().getString(getId()));
		this.type.set(type);
	}

	private String downloadUrl;

	private final FileFilter screenshotsFileFilter = new ScreenshotFileFilter();
	protected final FileFilter diskFileFilter = new DiskFileFilter();
	private final FileFilter fileBrowserFileFilter = new FileFilter() {

		private final TapeFileFilter tapeFileFilter = new TapeFileFilter();
		private final DocsFileFilter docsFileFilter = new DocsFileFilter();

		@Override
		public boolean accept(File file) {
			if (getType() == DiskCollectionType.HVMEC && file.isDirectory() && file.getName().equals(HVMEC_CONTROL)) {
				return false;
			}
			file = extractGZip(file);
			return diskFileFilter.accept(file) || tapeFileFilter.accept(file) || docsFileFilter.accept(file);
		}
	};

	public DiskCollection(C64Window window, Player player) {
		util = new UIUtil(window, player, this);
		util.parse(this);
	}

	@FXML
	private void initialize() {
		convenience = new Convenience(util.getPlayer());
		directory.setPrefHeight(Double.MAX_VALUE);
		directory.getAutoStartFileProperty().addListener(
				(observable) -> attachAndRunDemo(fileBrowser.getSelectionModel().getSelectedItem().getValue(),
						directory.getAutoStartFileProperty().get()));
		fileBrowser.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.getValue().isFile()) {
				File file = newValue.getValue();
				showScreenshot(file);
				try {
					directory.loadPreview(extract(file));
				} catch (Exception e) {
					System.err.println(String.format("Cannot insert media file '%s'.", file.getAbsolutePath()));
				}
			}
		});
		fileBrowser.setOnKeyPressed((event) -> {
			TreeItem<File> selectedItem = fileBrowser.getSelectionModel().getSelectedItem();
			if (event.getCode() == KeyCode.ENTER && selectedItem != null) {
				if (selectedItem.getValue().isFile()) {
					File file = selectedItem.getValue();
					if (file.isFile()) {
						attachAndRunDemo(selectedItem.getValue(), null);
					}
				}
			}
		});
		fileBrowser.setOnMousePressed((event) -> {
			final TreeItem<File> selectedItem = fileBrowser.getSelectionModel().getSelectedItem();
			if (selectedItem != null && selectedItem.getValue().isFile() && event.isPrimaryButtonDown()
					&& event.getClickCount() > 1) {
				attachAndRunDemo(fileBrowser.getSelectionModel().getSelectedItem().getValue(), null);
			}
		});
		contextMenu.setOnShown((event) -> {
			TreeItem<File> selectedItem = fileBrowser.getSelectionModel().getSelectedItem();
			boolean disable = selectedItem == null || !selectedItem.getValue().isFile();
			start.setDisable(disable);
			attachDisk.setDisable(disable);
		});
		type = new SimpleObjectProperty<>();
		type.addListener((observable, oldValue, newValue) -> {
			Platform.runLater(() -> {
				File initialRoot;
				switch (getType()) {
				case HVMEC:
					this.downloadUrl = util.getConfig().getOnlineSection().getHvmecUrl();
					initialRoot = util.getConfig().getSidplay2Section().getHVMECFile();
					break;

				case DEMOS:
					this.downloadUrl = util.getConfig().getOnlineSection().getDemosUrl();
					initialRoot = util.getConfig().getSidplay2Section().getDemosFile();
					break;

				case MAGS:
					this.downloadUrl = util.getConfig().getOnlineSection().getMagazinesUrl();
					initialRoot = util.getConfig().getSidplay2Section().getMagsFile();
					break;

				default:
					throw new RuntimeException("Illegal disk collection type : " + type);
				}
				if (initialRoot != null) {
					setRootFile(initialRoot);
				}
			});
		});
	}

	@FXML
	private void doAutoConfiguration() {
		if (autoConfiguration.isSelected()) {
			autoConfiguration.setDisable(true);
			try {
				DownloadThread downloadThread = new DownloadThread(util.getConfig(),
						new ProgressListener(util, fileBrowser.getScene()) {

							@Override
							public void downloaded(final File downloadedFile) {
								Platform.runLater(() -> {
									autoConfiguration.setDisable(false);
									if (downloadedFile != null) {
										setRootFile(downloadedFile);
									}
								});
							}
						}, new URL(downloadUrl));
				downloadThread.start();
			} catch (MalformedURLException e2) {
				e2.printStackTrace();
			}
		}
	}

	@FXML
	private void attachDisk() {
		File file = fileBrowser.getSelectionModel().getSelectedItem().getValue();
		try {
			File extractedFile = extract(file);
			util.getPlayer().insertDisk(extractedFile);
		} catch (IOException | SidTuneError e) {
			System.err.println(String.format("Cannot insert media file '%s'.", file.getAbsolutePath()));
		}
	}

	@FXML
	private void start() {
		attachAndRunDemo(fileBrowser.getSelectionModel().getSelectedItem().getValue(), null);
	}

	@FXML
	private void doBrowse() {
		DirectoryChooser fileDialog = new DirectoryChooser();
		SidPlay2Section sidplay2 = util.getConfig().getSidplay2Section();
		fileDialog.setInitialDirectory(sidplay2.getLastDirectoryFolder());
		File directory = fileDialog.showDialog(autoConfiguration.getScene().getWindow());
		if (directory != null) {
			sidplay2.setLastDirectory(directory.getAbsolutePath());
			setRootFile(directory);
		}
	}

	protected void setRootFile(final File rootFile) {
		if (rootFile.exists()) {
			collectionDir.setText(rootFile.getAbsolutePath());

			final File theRootFile = new TFile(rootFile);
			fileBrowser.setRoot(new DiskCollectionTreeItem(theRootFile, theRootFile, fileBrowserFileFilter));

			if (getType() == DiskCollectionType.HVMEC) {
				util.getConfig().getSidplay2Section().setHVMEC(rootFile.getAbsolutePath());
			} else if (getType() == DiskCollectionType.DEMOS) {
				util.getConfig().getSidplay2Section().setDemos(rootFile.getAbsolutePath());
			} else if (getType() == DiskCollectionType.MAGS) {
				util.getConfig().getSidplay2Section().setMags(rootFile.getAbsolutePath());
			}
		}
	}

	protected void attachAndRunDemo(File file, final File autoStartFile) {
		if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(".pdf")) {
			String tmpDir = util.getConfig().getSidplay2Section().getTmpDir();
			File dst = new File(tmpDir, file.getName());
			if (!dst.exists()) {
				try {
					TFile.cp(file, dst);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			dst.deleteOnExit();
			DesktopIntegration.open(dst);
		} else {
			try {
				File extractedFile = extract(file);
				if (convenience.autostart(extractedFile, Convenience.LEXICALLY_FIRST_MEDIA, autoStartFile)) {
					util.setPlayingTab(this);
				}
			} catch (IOException | SidTuneError | URISyntaxException e) {
				System.err.println(String.format("Cannot insert media file '%s'.", file.getAbsolutePath()));
			}
		}
	}

	protected void showScreenshot(final File file) {
		Image image = createImage(file);
		if (image != null) {
			screenshot.setImage(image);
		}
	}

	private Image createImage(final File file) {
		try {
			File screenshot = findScreenshot(file);
			if (screenshot != null && screenshot.exists()) {
				try (TFileInputStream is = new TFileInputStream(screenshot)) {
					return new Image(is);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private File findScreenshot(File file) {
		TreeItem<File> rootItem = fileBrowser.getRoot();
		if (rootItem == null) {
			return null;
		}
		String parentPath = getType() == DiskCollectionType.HVMEC
				? file.getParentFile().getPath().replace(HVMEC_DATA, HVMEC_CONTROL) : file.getParentFile().getPath();
		List<File> parentFiles = PathUtils.getFiles(parentPath, rootItem.getValue(), null);
		if (parentFiles.size() > 0) {
			File parentFile = parentFiles.get(parentFiles.size() - 1);
			final File[] listFiles = parentFile.listFiles();
			if (listFiles == null) {
				return null;
			}
			for (File photoFile : listFiles) {
				if (!photoFile.isDirectory() && screenshotsFileFilter.accept(photoFile)) {
					return photoFile;
				}
			}
		}
		return null;
	}

	private File extract(File file) throws IOException {
		if (file.getName().toLowerCase(Locale.US).endsWith(".gz")) {
			return extractGZip(file);
		} else {
			String tmpDir = util.getConfig().getSidplay2Section().getTmpDir();
			File dst = new File(tmpDir, file.getName());
			if (!dst.exists()) {
				TFile.cp(file, dst);
			}
			dst.deleteOnExit();
			return dst;
		}
	}

	private File extractGZip(File file) {
		if (file.getName().toLowerCase(Locale.US).endsWith(".gz")) {
			String tmpDir = util.getConfig().getSidplay2Section().getTmpDir();
			File dst = new File(tmpDir, PathUtils.getFilenameWithoutSuffix(file.getName()));
			try (InputStream is = new GZIPInputStream(new TFileInputStream(file))) {
				if (!dst.exists()) {
					TFile.cp(is, dst);
				}
			} catch (IOException e) {
				return file;
			}
			dst.deleteOnExit();
			return dst;
		}
		return file;
	}
}
