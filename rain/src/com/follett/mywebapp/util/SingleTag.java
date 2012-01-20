package com.follett.mywebapp.util;

import java.util.ArrayList;

public class SingleTag{
	private String tag;
	private ArrayList<String> params;

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
}