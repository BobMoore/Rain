package com.follett.mywebapp.util;

import com.google.gwt.user.client.ui.Button;

public class StepHolder extends Button {

	private String tagID;

	public StepHolder(String text, String tagID) {
		super(text);
		this.tagID = tagID;
	}

	public String getTagID() {
		return this.tagID;
	}

	public void setTagID(String tagID) {
		this.tagID = tagID;
	}
}
