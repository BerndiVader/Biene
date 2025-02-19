package com.gmail.berndivader.biene.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.config.Config;

public
class 
DatabaseConnection
{
	private static String sql_string;
	
	static {
		sql_string="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	}
	
	public DatabaseConnection() {
  		try {
			Class.forName(sql_string);
	   		Connection connection=DriverManager.getConnection(Config.data.connection_string(),Config.data.username(),Config.data.password());
	   		if(connection==null||connection.isClosed()) {
	   			Logger.$("Verbindung zum MS-SQL Server fehlgeschlagen.",false,true);
	   		}
	   		connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			Logger.$(e);
		}
	}
	
	public static Connection getNewConnection() {
		Connection connection=null;
		try {
			connection=(DriverManager.getConnection(Config.data.connection_string(),Config.data.username(),Config.data.password()));
		} catch (SQLException e) {
			Logger.$(e);
		}
		return connection;
	}
	
	public static boolean testConnection() {
		boolean error=false;
		try {
			Connection connection=getNewConnection();
			if(connection!=null&&!connection.isClosed()) {
				Logger.$("Verbindungstest mit Winline SQL-Server erfolgreich.",true,true);
				Logger.$("User: ".concat(connection.getMetaData().getUserName()),false,true);
				Logger.$("Datenbank: ".concat(connection.getCatalog()),false,true);
				connection.close();
			} else {
				Logger.$("Verbindungstest mit Winline SQL-Server fehlgeschlagen.",true,true);
				error=true;
			}
		} catch (SQLException e1) {
			error=true;
			Logger.$(e1);
		}
		return error;
	}
	
	
}
