package com.example.transactioncard.object;

public class Description {

	private long descriptionId;
	private String mDescription;

	public Description() {

	}

	public Description(String descriptionName) {

	}

	public void setDescription(String description) {
		this.mDescription = description;
	}

	public void setId(long id) {
		this.descriptionId = id;
	}

	public String getDescription() {
		return this.mDescription;
	}

	public long getId() {
		return this.descriptionId;
	}
}
