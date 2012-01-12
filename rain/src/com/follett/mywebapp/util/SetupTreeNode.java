package com.follett.mywebapp.util;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.TreeItem;

public class SetupTreeNode extends TreeItem{

	private String tagID;
	private String label;
	private Integer fields;
	private ArrayList<String> descriptions;

	public SetupTreeNode(String tagID, String label, Integer fields) {
		this.tagID = tagID;
		this.label = label;
		this.fields = fields;
		this.descriptions = new ArrayList<String>();
	}

	public SetupTreeNode(TableData child) {
		this.tagID = child.getTagID();
		this.label = child.getLabel();
		this.fields = child.getTextfields();
		this.descriptions = child.getDescriptions();
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
		Integer returnValue = Integer.valueOf(lastTag);
		returnValue = Integer.valueOf(returnValue.intValue() + 1);
		return returnValue.toString();
	}

	public String getTagID() {
		return this.tagID;
	}

	public void setTagID(String tagID) {
		this.tagID = tagID;
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

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
