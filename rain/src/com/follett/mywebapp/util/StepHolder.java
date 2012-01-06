package com.follett.mywebapp.util;

import com.google.gwt.user.client.ui.Composite;

public class StepHolder extends Composite {

	private String tagID;

	public StepHolder(String description, String tagID) {
		initWidget(this);
		this.setTitle(description);
		this.tagID = tagID;
	}

	public String getTagID() {
		return this.tagID;
	}

	public void setTagID(String tagID) {
		this.tagID = tagID;
	}
}
