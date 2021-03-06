package ui.webview;

import static ui.webview.WebViewType.Constants.DOC_IMAGES_DIR;
import static ui.webview.WebViewType.Constants.DOC_RESOURCE_DIR;
import static ui.webview.WebViewType.Constants.JAR_URL;
import static ui.webview.WebViewType.Constants.JAVADOC_RESOURCE_DIR;

import java.net.URL;

public enum WebViewType {
	/**
	 * The C-64 Scene Database
	 */
	CSDB("http://csdb.dk/"),
	/**
	 * Codebase 64 Wiki
	 */
	CODEBASE64("http://codebase64.org/"),
	/**
	 * Remix.Kwed.Org The Devinitive Guide To C64 Remakes
	 */
	REMIX_KWED_ORG("http://remix.kwed.org/"),
	/**
	 * The SID 6581/8580 Recordings Archive
	 */
	SID_OTH4_COM("http://sid.oth4.com/"),
	/**
	 * C-64 Portal
	 */
	C64_SK("http://www.c64.sk/"),
	/**
	 * Forum 64
	 */
	FORUM64_DE("http://www.forum64.de/"),
	/**
	 * Lemon Retro Store
	 */
	LEMON64_COM("http://www.lemon64.com/"),
	/**
	 * Stone Oakvalley's Authentic SID Collection (SOASC=)
	 */
	SOASC("http://se2a1.bigbox.info:40000/soasc/"),
	/**
	 * JSIDPlay2 Source Code
	 */
	JSIDPLAY2_SRC("http://sourceforge.net/p/jsidplay2/code/HEAD/tree/trunk/jsidplay2/"),
	/**
	 * JSIDPlay2 Java Documentation
	 */
	JSIDPLAY2_JAVADOC(JAR_URL + JAVADOC_RESOURCE_DIR + "index.html"),
	/**
	 * JSIDPlay2 User Guide
	 */
	USERGUIDE(JAR_URL + DOC_RESOURCE_DIR + "UserGuide.html");

	private String url;

	private WebViewType(String url) {
		this.url = url;
	}

	public String getUrl() {
		if (url.startsWith(JAR_URL)) {
			URL resource = getClass().getResource(url.replace(JAR_URL, ""));
			return resource != null ? resource.toExternalForm() : "";
		}
		return url;
	}

	/**
	 * Convert relative path names starting with "images/" of the documentation to
	 * absolute path names (This is for the internal {@link WebViewType#USERGUIDE} contained in
	 * the main JAR as resources located in sub-folder "/doc/").
	 * 
	 * @param url
	 *            URL to make absolute
	 * @return absolute URL
	 */
	public static String toAbsoluteUrl(String url) {
		if (url.startsWith(DOC_IMAGES_DIR)) {
			return DOC_RESOURCE_DIR + url;
		}
		return url;
	}

	static class Constants {
		static final String JAR_URL = "jar:file:";
		static final String DOC_RESOURCE_DIR = "/doc/";
		static final String DOC_IMAGES_DIR = "images/";
		static final String JAVADOC_RESOURCE_DIR = "/javadoc/";
	}
}
