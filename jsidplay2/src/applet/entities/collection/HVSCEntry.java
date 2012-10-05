package applet.entities.collection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import libsidplay.sidtune.SidTune.Clock;
import libsidplay.sidtune.SidTune.Compatibility;
import libsidplay.sidtune.SidTune.Model;
import libsidplay.sidtune.SidTune.Speed;

@Entity
public class HVSCEntry {
	@Id
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private String author;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	private String released;

	public String getReleased() {
		return released;
	}

	public void setReleased(String released) {
		this.released = released;
	}

	private String format;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Column(length=2048)
	private String playerId;

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	private Integer noOfSongs;

	public Integer getNoOfSongs() {
		return noOfSongs;
	}

	public void setNoOfSongs(Integer noOfSongs) {
		this.noOfSongs = noOfSongs;
	}

	private Integer startSong;

	public Integer getStartSong() {
		return startSong;
	}

	public void setStartSong(Integer startSong) {
		this.startSong = startSong;
	}

	@Enumerated(EnumType.STRING)
	private Clock clockFreq;

	public Clock getClockFreq() {
		return clockFreq;
	}

	public void setClockFreq(Clock clockFreq) {
		this.clockFreq = clockFreq;
	}

	@Enumerated(EnumType.STRING)
	private Speed speed;

	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}

	@Enumerated(EnumType.STRING)
	private Model sidModel1;

	public Model getSidModel1() {
		return sidModel1;
	}

	public void setSidModel1(Model sidModel1) {
		this.sidModel1 = sidModel1;
	}

	@Enumerated(EnumType.STRING)
	private Model sidModel2;

	public Model getSidModel2() {
		return sidModel2;
	}

	public void setSidModel2(Model sidModel2) {
		this.sidModel2 = sidModel2;
	}

	@Enumerated(EnumType.STRING)
	private Compatibility compatibility;

	public Compatibility getCompatibility() {
		return compatibility;
	}

	public void setCompatibility(Compatibility compatibility) {
		this.compatibility = compatibility;
	}

	private Long tuneLength;

	public Long getTuneLength() {
		return tuneLength;
	}

	public void setTuneLength(Long tuneLength) {
		this.tuneLength = tuneLength;
	}

	private String audio;

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	private Integer sidChipBase1;

	public Integer getSidChipBase1() {
		return sidChipBase1;
	}

	public void setSidChipBase1(Integer sidChipBase1) {
		this.sidChipBase1 = sidChipBase1;
	}

	private Integer sidChipBase2;

	public Integer getSidChipBase2() {
		return sidChipBase2;
	}

	public void setSidChipBase2(Integer sidChipBase2) {
		this.sidChipBase2 = sidChipBase2;
	}

	private Integer driverAddress;

	public Integer getDriverAddress() {
		return driverAddress;
	}

	public void setDriverAddress(Integer driverAddress) {
		this.driverAddress = driverAddress;
	}

	private Integer loadAddress;

	public Integer getLoadAddress() {
		return loadAddress;
	}

	public void setLoadAddress(Integer loadAddress) {
		this.loadAddress = loadAddress;
	}

	private Integer loadLength;

	public Integer getLoadLength() {
		return loadLength;
	}

	public void setLoadLength(Integer loadLength) {
		this.loadLength = loadLength;
	}

	private Integer initAddress;

	public Integer getInitAddress() {
		return initAddress;
	}

	public void setInitAddress(Integer initAddress) {
		this.initAddress = initAddress;
	}

	private Integer playerAddress;

	public Integer getPlayerAddress() {
		return playerAddress;
	}

	public void setPlayerAddress(Integer playerAddress) {
		this.playerAddress = playerAddress;
	}

	private Date fileDate;

	public Date getFileDate() {
		return fileDate;
	}

	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}

	private Long fileSizeKb;

	public Long getFileSizeKb() {
		return fileSizeKb;
	}

	public void setFileSizeKb(Long fileSizeInKb) {
		this.fileSizeKb = fileSizeInKb;
	}

	private Long tuneSizeB;

	public Long getTuneSizeB() {
		return tuneSizeB;
	}

	public void setTuneSizeB(Long tuneSizeInB) {
		this.tuneSizeB = tuneSizeInB;
	}

	private Short relocStartPage;

	public Short getRelocStartPage() {
		return relocStartPage;
	}

	public void setRelocStartPage(Short relocStartPage) {
		this.relocStartPage = relocStartPage;
	}

	private Short relocNoPages;

	public Short getRelocNoPages() {
		return relocNoPages;
	}

	public void setRelocNoPages(Short relocNoPages) {
		this.relocNoPages = relocNoPages;
	}

	@Column(length=4096)
	private String stilGlbComment;

	public String getStilGlbComment() {
		return stilGlbComment;
	}

	public void setStilGlbComment(String stilComment) {
		this.stilGlbComment = stilComment;
	}

	@OneToMany(mappedBy = "hvscEntry")
	private List<STIL> stil;

	public List<STIL> getStil() {
		if (stil == null) {
			stil = new ArrayList<STIL>();
		}
		return stil;
	}
}