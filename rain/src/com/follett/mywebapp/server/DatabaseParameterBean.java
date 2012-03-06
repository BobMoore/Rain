package com.follett.mywebapp.server;

import java.io.Serializable;
import java.util.Properties;

public class DatabaseParameterBean implements Serializable, DatabaseParameter{

	private static final long serialVersionUID = 1L;
	final static String databaseURL = "jdbc:jtds:sqlserver://127.0.0.1:1433/Rain";
	final static String tdsDriver = "net.sourceforge.jtds.jdbc.Driver";
	static Properties connectionProps;

	public DatabaseParameterBean() {
	}

	public static String getDatabaseURL() {
		return databaseURL;
	}

	public static String getTDSDriver() {
		return tdsDriver;
	}

	public static Properties getConnectionProperties() {
		connectionProps = new Properties();
		connectionProps.put("user", user);
		connectionProps.put("password", password);
		return connectionProps;
	}
}
