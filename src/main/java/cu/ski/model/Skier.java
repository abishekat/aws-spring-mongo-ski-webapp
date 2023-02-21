package cu.ski.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "skier_table")
public class Skier {
	private String resortID;
	private String skierID;
	private String liftID;
	private String year;
	private String resortName;
	private String dayID;
	private String seasonID;

	@Override
	public String toString() {
		return "Skier [resortID=" + resortID + ", skierID=" + skierID + ", liftID=" + liftID + ", year=" + year
				+ ", resortName=" + resortName + ", dayID=" + dayID + ", seasonID=" + seasonID + "]";
	}

	public Skier(String resortID, String year) {
		super();
		this.resortID = resortID;
		this.year = year;
	}

	public String getResortID() {
		return resortID;
	}

	public void setResortID(String resortID) {
		this.resortID = resortID;
	}

	public String getSkierID() {
		return skierID;
	}

	public void setSkierID(String skierID) {
		this.skierID = skierID;
	}

	public String getLiftID() {
		return liftID;
	}

	public void setLiftID(String liftID) {
		this.liftID = liftID;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getResortName() {
		return resortName;
	}

	public void setResortName(String resortName) {
		this.resortName = resortName;
	}

	public String getDayID() {
		return dayID;
	}

	public void setDayID(String dayID) {
		this.dayID = dayID;
	}

	public String getSeasonID() {
		return seasonID;
	}

	public void setSeasonID(String seasonID) {
		this.seasonID = seasonID;
	}

}
