package com.follett.mywebapp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.follett.mywebapp.client.SetupBuilderService;
import com.follett.mywebapp.util.DBNames;
import com.follett.mywebapp.util.SetupDataItem;
import com.follett.mywebapp.util.TableData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
public class SetupBuilderServiceImpl extends RemoteServiceServlet implements SetupBuilderService {

  private static final long serialVersionUID = 1L;

  /**
   * Implement the connection to the server and gather the necessary data
   * */
  @Override
  public SetupDataItem getSetupData() {
	  SetupDataItem returnable = new SetupDataItem();
	  try {
		  //send everything!
		  Class.forName(DatabaseParameterBean.getTDSDriver()).newInstance();

		  String url = DatabaseParameterBean.getDatabaseURL();
		  Connection conn = DriverManager.getConnection(url, DatabaseParameterBean.getConnectionProperties());
		  Statement stmt = conn.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT * FROM " + DBNames.TABLE_SETUP + " ORDER BY " + DBNames.SETUP_TAB);
		  String column;
		  String tagID;
		  boolean checkbox;
		  String label;
		  Integer fields;
		  String tab;
		  String fieldDescriptions;
		  while(rs.next()) {
			  column = rs.getString(DBNames.SETUP_COLUMN_HEADING);
			  tagID = rs.getString(DBNames.TAGID);
			  checkbox = rs.getBoolean(DBNames.SETUP_CHECKBOX);
			  label = rs.getString(DBNames.LABEL);
			  fields = Integer.valueOf(rs.getInt(DBNames.TEXTFIELDS));
			  tab = rs.getString(DBNames.SETUP_TAB);
			  fieldDescriptions = rs.getString(DBNames.FIELD_DESCRIPTIONS);
			  TableData data = new TableData(tagID, label, checkbox, fields);
			  data.addDescriptions(fieldDescriptions);
			  returnable.addTab(tab);
			  returnable.addColumnToTab(tab, column);
			  returnable.addDataToColumn(column, data);
		  }
		  conn.close();
	  } catch (SQLException e) {
		  e.printStackTrace();
	  } catch (ClassNotFoundException e) {
		  e.printStackTrace();
	  } catch (InstantiationException e) {
		  e.printStackTrace();
	  } catch (IllegalAccessException e) {
		  e.printStackTrace();
	  }
	  return returnable;
  }

  public Boolean saveSetupData(SetupDataItem allData) {
	  Boolean exception = Boolean.FALSE;
	  try {
		  Class.forName(DatabaseParameterBean.getTDSDriver()).newInstance();
		  String url = DatabaseParameterBean.getDatabaseURL();
		  Connection conn = DriverManager.getConnection(url, DatabaseParameterBean.getConnectionProperties());
		  Statement stmt = conn.createStatement();
		  String sql = "";
		  ArrayList<String> tabs = allData.getTabs();
		  //THIS DOESN'T REMOVE ANYTHING
		  for (String tab : tabs) {
			  ArrayList<String> columns = allData.getColumnsOnTab(tab);
			  for (String column : columns) {
				  ArrayList<TableData> dataSet = allData.getDataforColumn(column);
				  if(dataSet != null) {
					  for (TableData item : dataSet) {
						  sql = "UPDATE " + DBNames.TABLE_SETUP + "SET " +
						  DBNames.SETUP_COLUMN_HEADING + " = '" + column + "', " +
						  DBNames.SETUP_CHECKBOX + " = '" + item.isCheckbox() + "', " +
						  DBNames.LABEL + " = '" + item.getLabel() + "', " +
						  DBNames.TEXTFIELDS + " = '" + item.getTextfields() + "', " +
						  DBNames.SETUP_TAB + " = '" + tab + "', " +
						  DBNames.FIELD_DESCRIPTIONS + " = '" + item.getDescriptionsToString() + "' " +
						  "WHERE "+ DBNames.TAGID + " = '" + item.getTagID() + "'";
						  int success = stmt.executeUpdate(sql);
						  if(success == 0) {
							  sql = "INSERT INTO dbo.Setup " +
							  "("+ DBNames.SETUP_COLUMN_HEADING + ", " +
							  DBNames.TAGID + ", " +
							  DBNames.SETUP_CHECKBOX + ", " +
							  DBNames.LABEL +	", " +
							  DBNames.TEXTFIELDS + ", " +
							  DBNames.SETUP_TAB + ", " +
							  DBNames.FIELD_DESCRIPTIONS + ") values (" +
							  "'" + column + "', " +
							  "'" + item.getTagID() + "', " +
							  "'" + item.isCheckbox() + "', " +
							  "'" + item.getLabel() + "', " +
							  "'" + item.getTextfields() + "', " +
							  "'" + tab + "', " +
							  "'" + item.getDescriptionsToString() + "' " +
							  ") ";
							  success = stmt.executeUpdate(sql);
						  }
					  }
				  }
			  }
		  }
		  conn.close();
	  } catch (InstantiationException e) {
		  exception = Boolean.TRUE;
	  } catch (IllegalAccessException e) {
		  exception = Boolean.TRUE;
	  } catch (ClassNotFoundException e) {
		  exception = Boolean.TRUE;
	  } catch (SQLException e) {
		  exception = Boolean.TRUE;
	  }
	  return exception;
  }
}
