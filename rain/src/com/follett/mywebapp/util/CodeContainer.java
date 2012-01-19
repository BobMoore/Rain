package com.follett.mywebapp.util;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeContainer {

	private ArrayList<CodeStep> steps;

	public CodeContainer() {
		this.steps = new ArrayList<CodeStep>();
	}

	public void addStep(String tagID, ArrayList<String> variables) {
		this.steps.add(new CodeStep(tagID, variables));
	}

	public void addStep(HashMap<String, ArrayList<String>> step) {
		this.steps.add(new CodeStep(step));
	}

	public ArrayList<CodeStep> getStepList(){
		return this.steps;
	}

	@Override
	public String toString() {
		return this.steps.toString();
	}
}
