package cu.ski.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "season_table")
public class Season {
	@Id
	private String id;
	
//	@Field(name="seasonid")
	private int season;
	
	
	private Resort resort;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public int getSeason() {
		return season;
	}


	public void setSeason(int season) {
		this.season = season;
	}


	public Resort getResort() {
		return resort;
	}


	public void setResort(Resort resort) {
		this.resort = resort;
	}
	
	

}
