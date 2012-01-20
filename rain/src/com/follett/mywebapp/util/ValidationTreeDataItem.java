package com.follett.mywebapp.util;

import java.io.Serializable;
import java.util.ArrayList;

public class ValidationTreeDataItem implements Serializable{

	private static final long serialVersionUID = 1729345848896800198L;

	private String tagID;
	private String parentTagID;
	private String description;
	private Integer fields;
	private ArrayList<String> descriptions;

	//added an empty constructor to allow for serializable.
	public ValidationTreeDataItem() {
		this.tagID = null;
		this.parentTagID = null;
		this.description = null;
		this.fields = null;
		this.descriptions = new ArrayList<String>();
	}

	public ValidationTreeDataItem(String tagID, String parentTagID, String description, Integer fields) {
		this.tagID = tagID;
		this.parentTagID = parentTagID;
		this.description = description;
		this.fields = fields;
		this.descriptions = new ArrayList<String>();
	}

	public ValidationTreeDataItem(ValidationTreeNode item) {
		this.tagID = item.getTagID();
		this.parentTagID = item.getParentTagID();
		this.description = item.getText();
		this.fields = item.getFields();
		this.descriptions = item.getDescriptions();
	}

	public void addDescriptions(String fieldDescriptions) {
		while(!(fieldDescriptions == null || fieldDescriptions.isEmpty())){
			if(fieldDescriptions.contains(",")) {
				String param = fieldDescriptions.substring(0, fieldDescriptions.indexOf(","));
				this.descriptions.add(param);
				fieldDescriptions = fieldDescriptions.substring(fieldDescriptions.indexOf(",") + 1);
			} else {
				this.descriptions.add(fieldDescriptions);
				fieldDescriptions = null;
			}
		}
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

	public ArrayList<String> getDescriptions() {
		return this.descriptions;
	}

	public String getDescriptionsToString() {
		String returnable = "";
		boolean first = true;
		for (String item : this.descriptions) {
			if(first) {
				returnable += item;
				first = false;
			}else {
				returnable += ", " + item;
			}
		}
		return returnable;
	}
}
