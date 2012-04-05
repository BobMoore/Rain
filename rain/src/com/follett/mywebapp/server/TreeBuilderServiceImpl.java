package com.follett.mywebapp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.follett.mywebapp.client.TreeBuilderService;
import com.follett.mywebapp.util.DBNames;
import com.follett.mywebapp.util.ValidationTreeDataItem;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
public class TreeBuilderServiceImpl extends RemoteServiceServlet implements TreeBuilderService {

  private static final long serialVersionUID = 1L;

  /**
   * Implement the connection to the server and gather the necessary data
   * */
  @Override
  public HashMap<String, ArrayList<ValidationTreeDataItem>> getTreeItems() {
	  HashMap<String, ArrayList<ValidationTreeDataItem>> returnable = new HashMap<String, ArrayList<ValidationTreeDataItem>>();
	  ArrayList<ValidationTreeDataItem> exception = new ArrayList<ValidationTreeDataItem>();
	  exception.add(new ValidationTreeDataItem("","root","Exception", Integer.valueOf(0)));
	  try {
		  Class.forName(DatabaseParameterBean.getTDSDriver()).newInstance();
		  String url = DatabaseParameterBean.getDatabaseURL();
		  Connection conn = DriverManager.getConnection(url, DatabaseParameterBean.getConnectionProperties());
		  Statement stmt = conn.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT * FROM " + DBNames.TABLE_VALIDATION + " ORDER BY " + DBNames.VALIDATION_PARENT_TAG_ID);
		  String tagID;
		  String parentTagID;
		  String description;
		  Integer fields;
		  String fieldDescriptions;
		  while(rs.next()) {
			  tagID = rs.getString(DBNames.TAGID);
			  parentTagID = rs.getString(DBNames.VALIDATION_PARENT_TAG_ID);
			  description = rs.getString(DBNames.LABEL);
			  fields = Integer.valueOf(rs.getInt(DBNames.TEXTFIELDS));
			  fieldDescriptions = rs.getString(DBNames.FIELD_DESCRIPTIONS);
			  ValidationTreeDataItem item = new ValidationTreeDataItem(tagID, parentTagID, description, fields);
			  item.addDescriptions(fieldDescriptions);
			  ArrayList<ValidationTreeDataItem> currentList;
			  if(returnable.containsKey(parentTagID)) {
				  currentList = returnable.get(parentTagID);
			  } else {
				  currentList = new ArrayList<ValidationTreeDataItem>();
			  }
			  currentList.add(item);
			  returnable.put(parentTagID, currentList);
		  }
		  conn.close();
	  } catch (SQLException e) {
		  exception.get(0).setDescription(e.toString());
		  returnable.put("root", exception);
	  } catch (ClassNotFoundException e) {
		  exception.get(0).setDescription(e.toString());
		  returnable.put("root", exception);
	  } catch (InstantiationException e) {
		  exception.get(0).setDescription(e.toString());
		  returnable.put("root", exception);
	  } catch (IllegalAccessException e) {
		  exception.get(0).setDescription(e.toString());
		  returnable.put("root", exception);
	  }
	  return returnable;
  }

  public Boolean saveTreeItems(ArrayList<ValidationTreeDataItem> nodes) {
	  Boolean exception = Boolean.FALSE;
	  try {
		  Class.forName(DatabaseParameterBean.getTDSDriver()).newInstance();
		  String url = DatabaseParameterBean.getDatabaseURL();
		  Connection conn = DriverManager.getConnection(url, DatabaseParameterBean.getConnectionProperties());
		  Statement stmt = conn.createStatement();
		  String sql = "";
		  for (ValidationTreeDataItem item : nodes) {
			  sql = "UPDATE " + DBNames.TABLE_VALIDATION + " SET " +
			  DBNames.VALIDATION_PARENT_TAG_ID +" = '" + item.getParentTagID() + "', " +
			  DBNames.LABEL + " = '" + item.getDescription() + "', " +
			  DBNames.TEXTFIELDS + " = '" + item.getFields() + "', " +
			  DBNames.FIELD_DESCRIPTIONS + " = '" + item.getDescriptionsToString() + "' " +
			  "WHERE " + DBNames.TAGID + " = '" + item.getTagID() + "'";
			  int success = stmt.executeUpdate(sql);
			  if(success == 0) {
				  sql = "INSERT INTO " + DBNames.TABLE_VALIDATION + " " +
				  	"(" + DBNames.TAGID + " , " +
				  	DBNames.VALIDATION_PARENT_TAG_ID + " , " +
				  	DBNames.LABEL + " , " +
				  	DBNames.TEXTFIELDS + "," +
				  	DBNames.FIELD_DESCRIPTIONS + ") VALUES (" +
				  	"'" + item.getTagID() + "', " +
				  	"'" + item.getParentTagID() + "', " +
				  	"'" + item.getDescription() + "', " +
				  	"'" + item.getFields() + "', " +
				  	"'" + item.getDescriptionsToString() + "') ";
				  success = stmt.executeUpdate(sql);
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
