package com.follett.mywebapp.util;

import com.google.gwt.user.client.ui.TreeItem;

public class ValidationTreeNode extends TreeItem{

	private String tagID;
	private String parentTagID;
	private Integer fields;

	public ValidationTreeNode(String tagID, String parentTagID, String description, Integer fields) {
		this.tagID = tagID;
		this.parentTagID = parentTagID;
		this.setText(description);
		this.fields = fields;
	}

	public ValidationTreeNode(ValidationTreeDataItem item) {
		this.tagID = item.getTagID();
		this.parentTagID = item.getParentTagID();
		this.setText(item.getDescription());
		this.fields = item.getFields();
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

	public Integer getFields() {
		return this.fields;
	}

	public void setFields(Integer fields) {
		this.fields = fields;
	}
}
