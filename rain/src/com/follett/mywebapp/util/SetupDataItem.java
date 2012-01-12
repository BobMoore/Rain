package com.follett.mywebapp.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class SetupDataItem implements Serializable{

	private static final long serialVersionUID = 1729345848896800198L;

	private ArrayList<String> tabs;
	private HashMap<String, ArrayList<String>> columnsOnTab;
	private HashMap<String, ArrayList<TableData>> dataInColumn;
	private HashMap<String, ArrayList<String>> fieldDescriptionsForID;

	public SetupDataItem() {
		this.tabs = new ArrayList<String>();
		this.columnsOnTab = new HashMap<String, ArrayList<String>>();
		this.dataInColumn = new HashMap<String, ArrayList<TableData>>();
		this.fieldDescriptionsForID = new HashMap<String, ArrayList<String>>();
	}

	public void addTab(String newTab) {
		if(!this.tabs.contains(newTab)) {
			this.tabs.add(newTab);
		}
	}

	public ArrayList<String> getTabs(){
		return this.tabs;
	}

	public ArrayList<String> getColumnsOnTab(String tab) {
		if(this.columnsOnTab.containsKey(tab)) {
			return this.columnsOnTab.get(tab);
		}else {
			return null;
		}
	}

	public HashMap<String, ArrayList<String>> getAllColumns() {
		return this.columnsOnTab;
	}

	public void addColumnToTab(String tab, String column) {
		ArrayList<String> currentList = new ArrayList<String>();
		if(this.columnsOnTab.containsKey(tab)) {
			currentList = this.columnsOnTab.get(tab);
		}
		if(!currentList.contains(column)) {
			currentList.add(column);
		}
		this.columnsOnTab.put(tab, currentList);
	}

	public HashMap<String, ArrayList<TableData>> getData() {
		return this.dataInColumn;
	}

	public void addDataToColumn(String column, TableData data) {
		ArrayList<TableData> currentList = new ArrayList<TableData>();
		if(this.dataInColumn.containsKey(column)) {
			currentList = this.dataInColumn.get(column);
		}
		if(!currentList.contains(data)) {
			currentList.add(data);
		}
		this.dataInColumn.put(column, currentList);
	}



	public HashMap<String, ArrayList<String>> getFieldDescriptionsForID() {
		return this.fieldDescriptionsForID;
	}

	public void setData(SetupDataItem result) {
		this.tabs = result.getTabs();
		this.columnsOnTab = result.getAllColumns();
		this.dataInColumn = result.getData();
		this.fieldDescriptionsForID = result.getFieldDescriptionsForID();
	}
}
