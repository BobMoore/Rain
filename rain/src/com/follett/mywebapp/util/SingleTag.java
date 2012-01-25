package com.follett.mywebapp.util;

import java.io.Serializable;
import java.util.ArrayList;

public class SingleTag implements Serializable{


	private static final long serialVersionUID = 1L;

	private String tag;
	private ArrayList<String> params;

	public SingleTag() {
		this.tag = null;
		this.params = null;
	}

	public SingleTag(String tag, ArrayList<String> params) {
		this.tag = tag;
		this.params = params;
	}

	public String getTag() {
		return this.tag;
	}

	public ArrayList<String> getParams() {
		return this.params;
	}

	@Override
	public String toString() {
		return this.tag + " " + this.params;
	}
}