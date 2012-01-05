package com.follett.mywebapp.util;

import java.io.Serializable;

public class ValidationTreeDataItem implements Serializable{

	private static final long serialVersionUID = 1729345848896800198L;

	private String tagID;
	private String parentTagID;
	private String description;
	private Integer fields;

	//added an empty constructor to allow for serializable.
	public ValidationTreeDataItem() {
		this.tagID = null;
		this.parentTagID = null;
		this.description = null;
		this.fields = null;
	}

	public ValidationTreeDataItem(String tagID, String parentTagID, String description, Integer fields) {
		this.tagID = tagID;
		this.parentTagID = parentTagID;
		this.description = description;
		this.fields = fields;
	}

	public String getTagID() {
		return this.tagID;
	}

	public void setTagID(String tagID) {
		this.tagID = tagID;
	}

	public String getParentTagID() {
		return this.parentTagID;
	}

	public void setParentTagID(String parentTagID) {
		this.parentTagID = parentTagID;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getFields() {
		return this.fields;
	}

	public void setFields(Integer fields) {
		this.fields = fields;
	}
}
