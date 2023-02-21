package cu.ski.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "skier_table")
public class Skier {
	private String resortID;
	private String skierID;
	private String liftID;
	private String resortName;
	private String dayID;
	private String seasonID;
	private Long vertical;
	private String time;

	public Skier() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Skier [resortID=" + resortID + ", skierID=" + skierID + ", liftID=" + liftID + ", resortName="
				+ resortName + ", dayID=" + dayID + ", seasonID=" + seasonID + ", vertical=" + vertical + ", time="
				+ time + "]";
	}

	public Long getVertical() {
		return vertical;
	}

	public void setVertical(Long vertical) {
		this.vertical = vertical;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Skier(String resortID, String seasonID) {
		super();
		this.resortID = resortID;
		this.seasonID = seasonID;
	}

	public Skier(String resortID, String skierID, String liftID, String dayID, String seasonID, String time) {
		super();
		this.resortID = resortID;
		this.skierID = skierID;
		this.liftID = liftID;
		this.dayID = dayID;
		this.seasonID = seasonID;
		this.time = time;
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

	public Skier(String skierID) {
		super();
		this.skierID = skierID;
	}

}
