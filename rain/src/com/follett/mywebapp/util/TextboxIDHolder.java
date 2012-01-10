package com.follett.mywebapp.util;

import com.google.gwt.user.client.ui.TextBox;

public class TextboxIDHolder extends TextBox {

	private String tagID;

	public TextboxIDHolder(String tagID) {
		super();
		this.tagID = tagID;
	}

	public String getTagID() {
		return this.tagID;
	}

	public void setTagID(String tagID) {
		this.tagID = tagID;
	}
}
