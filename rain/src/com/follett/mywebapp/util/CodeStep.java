package com.follett.mywebapp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class CodeStep {

	private String tagID;
	private HashMap<String, ArrayList<String>> multiTags;
	private ArrayList<String> variables;

	public CodeStep() {
		this.tagID = null;
		this.variables = null;
		this.multiTags = null;
	}

	public CodeStep (String tagID, ArrayList<String> variables) {
		this.tagID = tagID;
		this.variables = variables;
		this.multiTags = null;
	}

	public CodeStep (HashMap<String, ArrayList<String>> step) {
		this.tagID = null;
		this.multiTags = step;
	}

	@Override
	public String toString() {
		String returnable = "";
		if(this.tagID == null) {
			Set<String> tags = this.multiTags.keySet();
			returnable = "[";
			boolean firstTag = true;
			for (String tag : tags) {
				if(firstTag) {
					returnable += tag + " [";
					firstTag = false;
				} else {
					returnable += ", " + tag + " [";
				}
				ArrayList<String> params = this.multiTags.get(tag);
				boolean firstParam = true;
				for (String param : params) {
					if(firstParam) {
						returnable += param;
						firstTag = false;
					} else {
						returnable += ", " + param;
					}
				}
				returnable += "]";
			}
			returnable += "]";
		} else {
			returnable = this.tagID + " " + this.variables.toString();
		}
		return returnable;
	}
}