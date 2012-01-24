package com.follett.mywebapp.util;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Button;

public class StepHolder extends Button {

	private String tagID;
	private ArrayList<String> multiTags;

	public StepHolder(String text, String tagID) {
		super(text);
		this.tagID = tagID;
		this.multiTags = null;
	}

	public StepHolder(String text) {
		super(text);
		this.tagID = null;
		this.multiTags = new ArrayList<String>();
	}

	public String getTagID() {
		return this.tagID;
	}

	public void setTagID(String tagID) {
		this.tagID = tagID;
	}

	public void addTagID(String singleTagID) {
		this.multiTags.add(singleTagID);
	}

	public ArrayList<String> getMultiTags() {
		return this.multiTags;
	}
}
