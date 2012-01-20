package com.follett.mywebapp.util;

import java.util.ArrayList;

public class CodeStep {

	private String tagID;
	private ArrayList<String> variables;
	private ArrayList<SingleTag> multiTag;

	public CodeStep() {
		this.tagID = null;
		this.variables = null;
		this.multiTag = new ArrayList<SingleTag>();
	}

	public CodeStep (String tagID, ArrayList<String> variables) {
		this.tagID = tagID;
		this.variables = variables;
		this.multiTag = null;
	}

	public void addTag(String tag, ArrayList<String> params) {
		this.multiTag.add(new SingleTag(tag, params));
	}

	public char getFirstChar() {
		char returnable = '~';
		if(this.tagID != null) {
			returnable = this.tagID.charAt(0);
		} else {
			//this could have an empty multiTag variable... but not likely
			returnable = this.multiTag.get(0).getTag().charAt(0);
		}
		return returnable;
	}

	@Override
	public String toString() {
		String returnable = "";
		if(this.tagID == null) {
			returnable = "[";
			boolean firstTag = true;
			for (SingleTag tag : this.multiTag) {
				if(firstTag) {
					returnable += tag.getTag() + " [";
					firstTag = false;
				} else {
					returnable += ", " + tag.getTag() + " [";
				}
				boolean firstParam = true;
				for (String param : tag.getParams()) {
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

	public String getTagID() {
		return this.tagID;
	}

	public ArrayList<String> getVariables() {
		return this.variables;
	}

	public ArrayList<SingleTag> getMultiTag() {
		return this.multiTag;
	}
}