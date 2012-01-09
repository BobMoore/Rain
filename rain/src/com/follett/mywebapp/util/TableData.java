package com.follett.mywebapp.util;

import java.io.Serializable;

public class TableData implements Serializable{

	private static final long serialVersionUID = 1729345848896800198L;

	private String tagID;
	private String label;
	private boolean checkbox;
	private Integer textfields;

	public TableData () {
		this.tagID = null;
		this.label = null;
		this.checkbox = false;
		this.textfields = null;
	}

	public TableData (String tagID, String label, boolean checkbox, Integer textfields) {
		this.tagID = tagID;
		this.label = label;
		this.checkbox = checkbox;
		this.textfields = textfields;
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
}
