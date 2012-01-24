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
		if(this.multiTag == null) {
			this.multiTag = new ArrayList<SingleTag>();
		}
		if(this.tagID != null) {
			this.multiTag.add(new SingleTag(this.tagID, this.variables));
			this.tagID = null;
			this.variables = null;
		}
		this.multiTag.add(new SingleTag(tag, params));
	}

	public boolean validation() {
		String comparable = "~";
		if(this.tagID != null) {
			comparable = this.tagID.substring(0, 1);
		} else {
			comparable = this.multiTag.get(0).getTag().substring(0, 1);
		}
		return (comparable.compareTo("a") <= 0) && (comparable.compareTo("Z") >= 0);
	}

	@Override
	public String toString() {
		String returnable = "";
		if(this.tagID == null && this.multiTag.size() > 1) {
			returnable = "[";
			boolean firstTag = true;
			for (SingleTag tag : this.multiTag) {
				if(firstTag) {
					returnable += tag.getTag();
					firstTag = false;
				} else {
					returnable += ", " + tag.getTag();
				}
				if(tag.getParams() != null) {
					returnable += tag.getParams().toString();
				}else {
					returnable += "[]";
				}
			}
			returnable += "]";
		} else if(this.tagID == null && this.multiTag.size() == 1) {
			ArrayList<String> params = this.multiTag.get(0).getParams();
			returnable = this.multiTag.get(0).getTag() + " ";
			returnable += (params != null) ? params.toString() : "[]";
		} else {
			returnable = this.tagID + " ";
			returnable += (this.variables != null) ? this.variables.toString() : "[]";
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