package cu.assignment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "audioItem")
public class AudioItem {
	@Id
	private String id;

	public AudioItem() {
	}

	private String trackTitle;

	private String albumTitle;

	private String trackNumber;

	private String year;

	private String numberOfReviews;

	private String noOfCopiesSold;

	public String getTrackTitle() {
		return trackTitle;
	}

	public void setTrackTitle(String trackTitle) {
		this.trackTitle = trackTitle;
	}

	public String getAlbumTitle() {
		return albumTitle;
	}

	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
	}

	public String getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(String trackNumber) {
		this.trackNumber = trackNumber;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getNumberOfReviews() {
		return numberOfReviews;
	}

	public void setNumberOfReviews(String numberOfReviews) {
		this.numberOfReviews = numberOfReviews;
	}

	public String getNoOfCopiesSold() {
		return noOfCopiesSold;
	}

	public void setNoOfCopiesSold(String noOfCopiesSold) {
		this.noOfCopiesSold = noOfCopiesSold;
	}

	private String artistName;

	public AudioItem(String id, String trackTitle, String albumTitle, String trackNumber, String year,
			String numberOfReviews, String noOfCopiesSold, String artistName) {
		super();
		this.id = id;
		this.trackTitle = trackTitle;
		this.albumTitle = albumTitle;
		this.trackNumber = trackNumber;
		this.year = year;
		this.numberOfReviews = numberOfReviews;
		this.noOfCopiesSold = noOfCopiesSold;
		this.artistName = artistName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

}
