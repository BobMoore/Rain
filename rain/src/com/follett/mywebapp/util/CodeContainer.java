package com.follett.mywebapp.util;

import java.util.ArrayList;

public class CodeContainer {

	private ArrayList<CodeStep> steps;

	public CodeContainer() {
		this.steps = new ArrayList<CodeStep>();
	}

	public void addStep(String tagID, ArrayList<String> variables) {
		this.steps.add(new CodeStep(tagID, variables));
	}

	public ArrayList<CodeStep> getStepList(){
		return this.steps;
	}

	@Override
	public String toString() {
		return this.steps.toString();
	}
}
