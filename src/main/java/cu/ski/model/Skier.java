package cu.ski.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "skier_table")
public class Skier {
	public int getResortID() {
		return resortID;
	}
	public void setResortID(int resortID) {
		this.resortID = resortID;
	}
	public int getSkierID() {
		return skierID;
	}
	public void setSkierID(int skierID) {
		this.skierID = skierID;
	}
	public int getLiftID() {
		return liftID;
	}
	public void setLiftID(int liftID) {
		this.liftID = liftID;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	private int resortID;
	private int skierID;
	private int liftID;
	private int year;

}
