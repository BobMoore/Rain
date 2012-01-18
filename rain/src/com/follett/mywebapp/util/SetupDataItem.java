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

	public void overWriteTab(String oldTab, String newTab) {
		if(!this.tabs.contains(oldTab)) {
			this.tabs.remove(oldTab);
		}
		this.tabs.add(newTab);
	}

	public ArrayList<String> getTabs(){
		return this.tabs;
	}

	public ArrayList<String> getColumnsOnTab(String tab) {
		return this.columnsOnTab.get(tab);
	}

	public void updateTabWithColumns(String oldTab, String newTab, ArrayList<String> columns) {
		if(this.columnsOnTab.get(oldTab) != null) {
			this.columnsOnTab.remove(oldTab);
		}
		this.columnsOnTab.put(newTab, columns);
		for (String column : columns) {
			if(this.dataInColumn.get(column) == null) {
				this.dataInColumn.put(column, new ArrayList<TableData>());
			}
		}
	}

	public String getNextHighestTag() {
		String highest = "";
		for (String tab : this.tabs) {
			ArrayList<String> columns = this.columnsOnTab.get(tab);
			if(!(columns == null || columns.isEmpty())) {
				for (String column : columns) {
					ArrayList<TableData> columnData = this.dataInColumn.get(column);
					for (TableData data : columnData) {
						highest = ((data.getTagID().compareTo(highest)) > 0 ) ? data.getTagID() : highest;
					}
				}
			}
		}
		return incrementTagID(highest);
	}

	public static String incrementTagID(String lastTag) {
		String returnString = "";
		Integer returnValue = Integer.valueOf(lastTag);
		returnValue = Integer.valueOf(returnValue.intValue() + 1);
		for(int a = 0; a < 6 - returnValue.toString().length(); a++) {
			returnString += "0";
		}
		return returnString + returnValue.toString();
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
	public void overWriteColumnsOnTab(String tab, ArrayList<String> columns) {
		this.columnsOnTab.put(tab, columns);
		for (String column : columns) {
			if(this.dataInColumn.get(column) == null) {
				this.dataInColumn.put(column, new ArrayList<TableData>());
			}
		}
	}

	public HashMap<String, ArrayList<TableData>> getData() {
		return this.dataInColumn;
	}

	public ArrayList<TableData> getDataforColumn(String column) {
		return this.dataInColumn.get(column);
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

	public void updateDataInColumn(String column, ArrayList<TableData> data) {
		this.dataInColumn.put(column, data);
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

	public boolean doesTabExist(String tab) {
		return this.tabs.contains(tab);
	}
}
