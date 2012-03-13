package com.follett.mywebapp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.follett.mywebapp.client.DatabaseBuilderService;
import com.follett.mywebapp.util.DBNames;
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
			Connection conn = DriverManager.getConnection(DatabaseParameterBean.getDriverAndIp(), DatabaseParameterBean.getConnectionProperties());
			if(conn == null) {
				success = Boolean.FALSE;
			}
			Statement stmt = conn.createStatement();
			String sql = "CREATE DATABASE " + DBNames.DB_NAME;
			stmt.executeUpdate(sql);
			conn = DriverManager.getConnection(DatabaseParameterBean.getDatabaseURL(), DatabaseParameterBean.getConnectionProperties());
			stmt = conn.createStatement();
			sql = "CREATE TABLE " + DBNames.TABLE_CODE + " (" + DBNames.TAGID + " nvarchar(50) not null, " + DBNames.CODE_CODE +
				" nvarchar(max), primary key(" + DBNames.TAGID + "))";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE " + DBNames.TABLE_SETUP + " (" + DBNames.SETUP_COLUMN_HEADING + " nvarchar(50) not null, " + DBNames.TAGID +
				" nvarchar(50) not null, " + DBNames.SETUP_CHECKBOX + " bit not null, " + DBNames.LABEL + " nvarchar(50) not null, " +
				DBNames.TEXTFIELDS + " bigint, " + DBNames.SETUP_TAB + " nvarchar(50) not null, " + DBNames.FIELD_DESCRIPTIONS +
				" nvarchar(max), primary key(" + DBNames.TAGID + "))";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE " + DBNames.TABLE_TESTS + " (" + DBNames.TESTS_TEST_NUMBER + " int not null, " + DBNames.TESTS_STEPS + " nvarchar(max) not null, " +
					"primary key(" + DBNames.TESTS_TEST_NUMBER + "))";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE " + DBNames.TABLE_VALIDATION + " (" + DBNames.TAGID + " nvarchar(50) not null, " + DBNames.VALIDATION_PARENT_TAG_ID + " nvarchar(50), " +
			  	DBNames.LABEL + " text, " + DBNames.TEXTFIELDS + " bigint, " + DBNames.FIELD_DESCRIPTIONS + " nvarchar(max) null," +
			  			" primary key(" + DBNames.TAGID + "))";
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
