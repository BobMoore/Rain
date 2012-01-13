package com.follett.mywebapp.util;

import java.io.Serializable;
import java.util.ArrayList;

public class TableData implements Serializable{

	private static final long serialVersionUID = 1729345848896800198L;

	private String tagID;
	private String label;
	private boolean checkbox;
	private Integer textfields;
	private ArrayList<String> descriptions;

	public TableData () {
		this.tagID = null;
		this.label = null;
		this.checkbox = false;
		this.textfields = null;
		this.descriptions = new ArrayList<String>();
	}

	public TableData (String tagID, String label, boolean checkbox, Integer textfields) {
		this.tagID = tagID;
		this.label = label;
		this.checkbox = checkbox;
		this.textfields = textfields;
		this.descriptions = new ArrayList<String>();
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

	public String getTagID() {
		return this.tagID;
	}

	public String getLabel() {
		return this.label;
	}

	public boolean isCheckbox() {
		return this.checkbox;
	}

	public Integer getTextfields() {
		return this.textfields;
	}

	public ArrayList<String> getDescriptions() {
		return this.descriptions;
	}

	public void setDescriptions(ArrayList<String> descriptions) {
		this.descriptions = descriptions;
	}

	public void setCheckbox(boolean checkbox) {
		this.checkbox = checkbox;
	}
}
