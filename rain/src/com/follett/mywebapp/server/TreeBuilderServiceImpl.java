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
		  while(rs.next()) {
			  tagID = rs.getString("tagID");
			  parentTagID = rs.getString("parentTagID");
			  description = rs.getString("description");
			  fields = Integer.valueOf(rs.getInt("fields"));
			  ValidationTreeDataItem item = new ValidationTreeDataItem(tagID, parentTagID, description, fields);
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
}
