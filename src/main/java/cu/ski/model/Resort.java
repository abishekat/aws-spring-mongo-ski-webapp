package cu.ski.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resort_table")
public class Resort {

	public Resort() {
		super();
	}

	// @Column(name="resortname")
	private String resortName;

//	@Column(name="resortid")
	private String resortId;

	@Override
	public String toString() {
		return "Resort [resortName=" + resortName + ", resortId=" + resortId + "]";
	}

	public String getResortName() {
		return resortName;
	}

	public void setResortName(String resortName) {
		this.resortName = resortName;
	}

	public String getResortId() {
		return resortId;
	}

	public void setResortId(String resortId) {
		this.resortId = resortId;
	}


}
