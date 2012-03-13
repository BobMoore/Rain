package com.follett.mywebapp.server;

import java.io.Serializable;
import java.util.Properties;

import com.follett.mywebapp.util.DBNames;

public class DatabaseParameterBean implements Serializable, DatabaseParameter{

	private static final long serialVersionUID = 1L;
	final static String databaseURL = "jdbc:jtds:sqlserver://";
	final static String tdsDriver = "net.sourceforge.jtds.jdbc.Driver";
	static Properties connectionProps;

	public DatabaseParameterBean() {
	}

	public static String getDriverAndIp() {
		return databaseURL + ip;
	}

	public static String getDatabaseURL() {
		return databaseURL + ip + "/" + DBNames.DB_NAME;
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
