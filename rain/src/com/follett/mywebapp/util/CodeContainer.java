package com.follett.mywebapp.util;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeContainer {

	private HashMap<String, ArrayList<String>> steps;

	public CodeContainer() {
		this.steps = new HashMap<String, ArrayList<String>>();
	}

	public void addStep(String tagID, ArrayList<String> variables) {
		this.steps.put(tagID, variables);
	}

	public HashMap<String, ArrayList<String>> getStepList(){
		return this.steps;
	}
}
