package com.follett.mywebapp.util;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.TreeItem;

public class ValidationTreeNode extends TreeItem{

	private String tagID;
	private String parentTagID;
	private Integer fields;
	private ArrayList<String> descriptions;

	public ValidationTreeNode(String tagID, String parentTagID, String description, Integer fields) {
		this.tagID = tagID;
		this.parentTagID = parentTagID;
		this.setText(description);
		this.fields = fields;
		this.descriptions = new ArrayList<String>();
	}

	public ValidationTreeNode(ValidationTreeDataItem item) {
		this.tagID = item.getTagID();
		this.parentTagID = item.getParentTagID();
		this.setText(item.getDescription());
		this.fields = item.getFields();
		this.descriptions = item.getDescriptions();
	}

	public void addDescriptions(String fieldDescriptions) {
		while(!(fieldDescriptions == null || fieldDescriptions.isEmpty())){
			if(fieldDescriptions.contains(",")) {
				String param = fieldDescriptions.substring(0, fieldDescriptions.indexOf(","));
				this.descriptions.add(param.trim());
				fieldDescriptions = fieldDescriptions.substring(fieldDescriptions.indexOf(",") + 1);
			} else {
				this.descriptions.add(fieldDescriptions.trim());
				fieldDescriptions = null;
			}
		}
	}

	public void setDescriptions(String fieldDescriptions) {
		this.descriptions = new ArrayList<String>();
		addDescriptions(fieldDescriptions);
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

	public static String incrementTagID(String lastTag) {
		boolean addOne = true;
		StringBuilder newTag = new StringBuilder(lastTag);
		int index = newTag.length() - 1;
		while(addOne && index != -1) {
			char digit = newTag.charAt(index);
			digit += 1;
			if(digit - 'a' < 26) {
				newTag.setCharAt(index, digit);
				addOne = false;
			}
			index--;
		}
		if(addOne) {
			newTag.insert(0, 'a');
		}
		return newTag.toString();
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

	public ArrayList<String> getDescriptions() {
		return this.descriptions;
	}
}
