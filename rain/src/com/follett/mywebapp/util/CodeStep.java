package com.follett.mywebapp.util;

import java.util.ArrayList;

public class CodeStep {
	private String tagID;
	private ArrayList<String> variables;

	public CodeStep (String tagID, ArrayList<String> variables) {
		this.tagID = tagID;
		this.variables = variables;
	}

	@Override
	public String toString() {
		return this.tagID + " " + this.variables.toString();
	}
}