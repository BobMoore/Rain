package com.follett.mywebapp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.follett.mywebapp.util.ValidationTreeDataItem;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TreeBuilderServiceImpl extends RemoteServiceServlet implements TreeBuilderService {

  private static final long serialVersionUID = 1L;

  /**
   * Implement the connection to the server and gather the necessary data
   * */
  @Override
  public HashMap<String, ArrayList<ValidationTreeDataItem>> getTreeItems() {
	  HashMap<String, ArrayList<ValidationTreeDataItem>> returnable = new HashMap<String, ArrayList<ValidationTreeDataItem>>();
	  try {
		  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		  // TODO get tree items and build them into the tree
		  String url = "jdbc:sqlserver://127.0.0.1:1433;" +
		  "databaseName=Rain;user=sa;password=stuffy;";
		  Connection conn = DriverManager.getConnection(url);
		  Statement stmt = conn.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT * FROM dbo.TreeItems ORDER BY parentTagID");
		  String tagID;
		  String parentTagID;
		  String description;
		  Integer fields;
		  String fieldDescriptions;
		  while(rs.next()) {
			  tagID = rs.getString("tagID");
			  parentTagID = rs.getString("parentTagID");
			  description = rs.getString("description");
			  fields = Integer.valueOf(rs.getInt("fields"));
			  fieldDescriptions = rs.getString("fieldDescriptions");
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
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  } catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  return returnable;
  }

  public Boolean saveTreeItems(ArrayList<ValidationTreeDataItem> nodes) {
	  Boolean exception = Boolean.FALSE;
	  try {
		  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		  String url = "jdbc:sqlserver://127.0.0.1:1433;" +
		  "databaseName=Rain;user=sa;password=stuffy;";
		  Connection conn = DriverManager.getConnection(url);
		  Statement stmt = conn.createStatement();
		  String sql = "";
		  for (ValidationTreeDataItem item : nodes) {
			  sql = "UPDATE dbo.TreeItems SET " +
			  "parentTagID = '" + item.getParentTagID() + "', " +
			  "description = '" + item.getDescription() + "', " +
			  "fields = '" + item.getFields() + "', " +
			  "fieldDescriptions = '" + item.getDescriptionsToString() + "' " +
			  "WHERE tagID = '" + item.getTagID() + "'";
			  int success = stmt.executeUpdate(sql);
			  if(success == 0) {
				  sql = "INSERT INTO dbo.TreeItems " +
				  "(tagID, parentTagID, description, fields, fieldDescriptions) values (" +
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
