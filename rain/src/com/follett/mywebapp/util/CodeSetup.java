package com.follett.mywebapp.util;

import java.util.ArrayList;

public class CodeSetup {
	private String tagID;
	private ArrayList<String> variables;

	public CodeSetup (String tagID, ArrayList<String> variables) {
		this.tagID = tagID;
		this.variables = variables;
	}

	@Override
	public String toString() {
		return this.tagID + " " + this.variables.toString();
	}
}