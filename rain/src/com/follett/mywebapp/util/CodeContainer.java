package com.follett.mywebapp.util;

import java.io.Serializable;
import java.util.ArrayList;

public class CodeContainer implements Serializable{

	private static final long serialVersionUID = 1L;

	private ArrayList<CodeStep> steps;

	public CodeContainer() {
		this.steps = new ArrayList<CodeStep>();
	}

	public void addStep(String tagID, ArrayList<String> variables) {
		this.steps.add(new CodeStep(tagID, variables));
	}

	public void addStep(CodeStep step) {
		this.steps.add(step);
	}

	public ArrayList<CodeStep> getStepList(){
		return this.steps;
	}

	@Override
	public String toString() {
		return this.steps.toString();
	}
}
