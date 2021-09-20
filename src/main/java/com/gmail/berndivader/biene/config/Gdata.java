package com.gmail.berndivader.biene.config;

import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

import com.gmail.berndivader.biene.Biene;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;

public
class
Gdata 
implements
Cloneable
{
	private int config_version;
	private String connection_string;
	private String username;
	private String password;
	private String database;
	private String sql_command;
	private String sql_stapelpreise;
	private String sql_updates;
	private String sql_inserts;
	private String sql_deletes;
	private String sql_verify;
	private String meso_year;
	private String http_string;
	private String csv_header;
	private String katalog;
	private boolean auto_update;
	private String update_inverval;
	private transient HashMap<Integer,SimpleEntry<String,String>>katalogs;
	
	public int getConfig_version() {
		return config_version;
	}
	public void setConfig_version(int config_version) {
		this.config_version = config_version;
	}
	
	public String get_csv_header() {
		return csv_header;
	}
	public void set_csv_header(String csv_header) {
		this.csv_header=csv_header;
	}
	
	public String getConnection_string() {
		return connection_string!=null?decode(connection_string):null;
	}
	public void setConnection_string(String connection_string) {
		this.connection_string=encode(connection_string);
	}
	
	public String getUsername() {
		return username!=null?decode(username):null;
	}
	public void setUsername(String username) {
		this.username=encode(username);
	}
	
	public String getPassword() {
		return password!=null?decode(password):null;
	}
	public void setPassword(String password) {
		this.password=encode(password);
	}
	
	public String getDatabase() {
		return database!=null?decode(database):null;
	}
	public void setDatabase(String database) {
		this.database=encode(database);
	}
	
	public String getWinlineQuery() {
		return sql_command!=null?decode(sql_command):null;
	}
	public void setWinlineQuery(String sql_command) {
		this.sql_command=encode(sql_command);
	}
	
	public String getStapelpreiseQuery() {
		return sql_stapelpreise!=null?decode(sql_stapelpreise):null;
	}
	public void setStapelpreiseQuery(String sql_stapelpreise) {
		this.sql_stapelpreise=encode(sql_stapelpreise);
	}
	
	public String getUpdatesQuery() {
		return sql_updates!=null?decode(sql_updates):null;
	}
	public void setUpdatesQuery(String sql_updates) {
		this.sql_updates=encode(sql_updates);
	}
	
	public String getInsertsQuery() {
		return sql_inserts!=null?decode(sql_inserts):null;
	}
	public void setInsertsQuery(String sql_inserts) {
		this.sql_inserts=encode(sql_inserts);
	}
	
	public String getDeletesQuery() {
		return sql_deletes!=null?decode(sql_deletes):null;
	}
	public void setDeletesQuery(String sql_deletes) {
		this.sql_deletes=encode(sql_deletes);
	}
	
	public String getVerifyQuery() {
		return sql_verify!=null?decode(sql_verify):null;
	}
	public void setVerifyQuery(String sql_verify) {
		this.sql_verify=encode(sql_verify);
	}
	
	public boolean getAutoUpdate() {
		return this.auto_update;
	}
	public void setAutoUpdate(boolean auto_update) {
		this.auto_update=auto_update;
		if(Biene.batcher!=null) {
			Biene.batcher.auto_update=auto_update;
		}
	}
	
	public int getUpdateInterval() {
		int interval=60;
		try {
			interval=Integer.parseInt(this.update_inverval);
		} catch (Exception e) {
			Logger.$(e);
		}
		return interval;
	}
	public void setUpdateInterval(String interval_string) {
		this.update_inverval=interval_string;
		if(Biene.batcher!=null) Biene.batcher.update_start=getUpdateInterval()*60;
	}
	
	public SimpleEntry<String,String>getKatalogs(int id){
		if(katalogs==null) setKatalog(getKatalog());
		if(katalogs.containsKey(id)) return katalogs.get(id);
		return null;
	}
	
	public String getKatalog() {
		return katalog!=null?decode(katalog):null;
	}
	public void setKatalog(String katalog) {
		this.katalog=encode(katalog);
		this.katalogs=new HashMap<>();
		String[]lines=katalog.split("[\\r\\n]+");
		int size=lines.length;
		for(int i1=0;i1<size;i1++) {
			String[]line=lines[i1].split(",");
			this.katalogs.put(Integer.parseInt(line[0]),new SimpleEntry<>(line[1],line[2]));
		}
	}
	
	public String getMeso_year() {
		return meso_year!=null?decode(meso_year):null;
	}
	public void setMeso_year(String meso_year) {
		this.meso_year=encode(meso_year);
	}
	
	public String getHttp_string() {
		return http_string!=null?decode(http_string):null;
	}
	public void setHttp_string(String http_string) {
		this.http_string=encode(http_string);
	}
	
	public String getShopUser() {
		String http=getHttp_string();
		if(http!=null) {
			String string=http.split("user=")[1];
			String[]parse=string.split("&password=");
			return parse[0];
		}
		return null;
	}
	
	public String getShopPassword() {
		String http=getHttp_string();
		if(http!=null) {
			String string=http.split("user=")[1];
			String[]parse=string.split("&password=");
			return parse[1];
		}
		return null;
	}
	
	private static String encode(String value) {
		return Utils.encrypt(value);
	}
	private static String decode(String value) {
		return Utils.decrypt(value);
	}
	
}
