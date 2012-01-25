package com.follett.mywebapp.util;

import java.io.Serializable;
import java.util.ArrayList;

public class CodeStep implements Serializable{

	private static final long serialVersionUID = 1L;

	private SingleTag singleTag;
	private ArrayList<SingleTag> multiTag;

	public CodeStep() {
		this.singleTag = null;
		this.multiTag = new ArrayList<SingleTag>();
	}

	public CodeStep (String tagID, ArrayList<String> variables, ArrayList<String> titles) {
		this.singleTag = new SingleTag(tagID, variables, titles);
		this.multiTag = null;
	}

	public void addTag(String tag, ArrayList<String> params, ArrayList<String> titles) {
		if(this.multiTag == null) {
			this.multiTag = new ArrayList<SingleTag>();
		}
		if(this.singleTag.getTag() != null) {
			this.multiTag.add(new SingleTag(this.singleTag.getTag(), this.singleTag.getParams(), this.singleTag.getTitles()));
			this.singleTag = null;
		}
		this.multiTag.add(new SingleTag(tag, params, titles));
	}

	public boolean validation() {
		String comparable = "~";
		if(this.singleTag != null) {
			comparable = this.singleTag.getTag().substring(0, 1);
		} else {
			comparable = this.multiTag.get(0).getTag().substring(0, 1);
		}
		return (comparable.compareTo("a") <= 0) && (comparable.compareTo("Z") >= 0);
	}

	@Override
	public String toString() {
		String returnable = "";
		if(this.singleTag == null && (this.multiTag != null && this.multiTag.size() > 1)) {
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
		} else if(this.singleTag == null && (this.multiTag != null && this.multiTag.size() == 1)) {
			ArrayList<String> params = this.multiTag.get(0).getParams();
			returnable = this.multiTag.get(0).getTag() + " ";
			returnable += (params != null) ? params.toString() : "[]";
		} else {
			returnable = this.singleTag.getTag() + " ";
			returnable += (this.singleTag.getParams() != null) ? this.singleTag.getParams().toString() : "[]";
		}
		return returnable;
	}

	public String getTagID() {
		return this.singleTag.getTag();
	}

	public ArrayList<String> getVariables() {
		return this.singleTag.getParams();
	}

	public ArrayList<String> getTitles() {
		return this.singleTag.getTitles();
	}

	public ArrayList<SingleTag> getMultiTag() {
		return this.multiTag;
	}
}