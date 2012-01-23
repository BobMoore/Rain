package com.follett.mywebapp.util;

import java.io.Serializable;
import java.util.ArrayList;

public class StepTableData implements Serializable{

	private static final long serialVersionUID = 1729345848896800198L;

	private String tagID;
	private String label;
	private Integer textfields;
	private ArrayList<String> descriptions;
	private boolean newStep = false;

	private boolean setup;

	public StepTableData () {
		this.tagID = null;
		this.label = null;
		this.textfields = null;
		this.setup = true;
		this.descriptions = new ArrayList<String>();
	}

	public StepTableData (String tagID, String label, Integer textfields, boolean trueForSetup, boolean newStep) {
		this.tagID = tagID;
		this.label = label;
		this.textfields = textfields;
		this.descriptions = new ArrayList<String>();
		this.setup = trueForSetup;
		this.newStep = newStep;
	}

	public void addDescriptions(String fieldDescriptions) {
		while(!(fieldDescriptions == null || fieldDescriptions.isEmpty())){
			if(fieldDescriptions.contains(",")) {
				String param = fieldDescriptions.substring(0, fieldDescriptions.indexOf(","));
				this.descriptions.add(param.trim());
				fieldDescriptions = fieldDescriptions.substring(fieldDescriptions.indexOf(",") + 1);
			} else {
				this.descriptions.add(fieldDescriptions.trim());
				fieldDescriptions = null;
			}
		}
	}

	public String getDescriptionsToString() {
		String returnable = "";
		boolean first = true;
		for (String item : this.descriptions) {
			if(first) {
				returnable += item;
				first = false;
			}else {
				returnable += ", " + item;
			}
		}
		return returnable;
	}

	public String getTagID() {
		return this.tagID;
	}

	public String getLabel() {
		return this.label;
	}

	public Integer getTextfields() {
		return this.textfields;
	}

	public ArrayList<String> getDescriptions() {
		return this.descriptions;
	}

	public void setDescriptions(ArrayList<String> descriptions) {
		this.descriptions = descriptions;
	}

	@Override
	public String toString() {
		return "Label: " + this.label + " TagID: " + this.tagID;
	}

	public void setSetup(boolean setup) {
		this.setup = setup;
	}

	public boolean isSetup() {
		return this.setup;
	}

	public void setNewStep(boolean newStep) {
		this.newStep = newStep;
	}

	public boolean isNewStep() {
		return this.newStep;
	}
}
