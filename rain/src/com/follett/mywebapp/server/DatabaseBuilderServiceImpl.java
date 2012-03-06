package com.follett.mywebapp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.follett.mywebapp.client.DatabaseBuilderService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
public class DatabaseBuilderServiceImpl extends RemoteServiceServlet implements DatabaseBuilderService {

	private static final long serialVersionUID = 1L;

	@Override
	public Boolean buildDatabase() {
		Boolean success = Boolean.TRUE;
		try {
			Class.forName(DatabaseParameterBean.getTDSDriver()).newInstance();
			String url = "jdbc:jtds:sqlserver://127.0.0.1:1433";
			Connection conn = DriverManager.getConnection(url, DatabaseParameterBean.getConnectionProperties());
			if(conn == null) {
				success = Boolean.FALSE;
			}
			Statement stmt = conn.createStatement();
			String sql = "CREATE DATABASE Rain";
			stmt.executeUpdate(sql);
			url = "jdbc:jtds:sqlserver://127.0.0.1:1433/Rain";
			conn = DriverManager.getConnection(url, DatabaseParameterBean.getConnectionProperties());
			stmt = conn.createStatement();
			sql = "CREATE TABLE Code (tagID nvarchar(50) not null, code nvarchar(max), primary key(tagID))";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE Setup (columnHeading nvarchar(50) not null, tagID nvarchar(50) not null, checkbox bit not null, label nvarchar(50) not null, textfields bigint, tab nvarchar(50) not null, fieldDescriptions nvarchar(max), primary key(tagID))";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE Tests (TestNumber int not null, Steps nvarchar(max) not null, primary key(TestNumber))";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE TreeItems (tagID nvarchar(50) not null, parentTagID nvarchar(50), label text, textfields bigint, fieldDescriptions nvarchar(max) null, primary key(tagID))";
			stmt.executeUpdate(sql);
		} catch (InstantiationException e) {
			success = Boolean.FALSE;
		} catch (IllegalAccessException e) {
			success = Boolean.FALSE;
		} catch (ClassNotFoundException e) {
			success = Boolean.FALSE;
		} catch (SQLException e) {
			success = Boolean.FALSE;
		}
		return success;
	}

	@Override
	public Boolean checkDatabase() {
		Boolean success = Boolean.FALSE;
		try {
			Class.forName(DatabaseParameterBean.getTDSDriver()).newInstance();
			String url = DatabaseParameterBean.getDatabaseURL();
			Connection conn = DriverManager.getConnection(url, DatabaseParameterBean.getConnectionProperties());
			success = Boolean.TRUE;
		} catch (InstantiationException e) {
			success = Boolean.FALSE;
		} catch (IllegalAccessException e) {
			success = Boolean.FALSE;
		} catch (ClassNotFoundException e) {
			success = Boolean.FALSE;
		} catch (SQLException e) {
			success = Boolean.FALSE;
		}
		return success;
	}
}
