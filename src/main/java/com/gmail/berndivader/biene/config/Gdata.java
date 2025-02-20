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
	
	public int config_version() {
		return config_version;
	}
	public void config_version(int config_version) {
		this.config_version = config_version;
	}
	
	public String csv_header() {
		return csv_header;
	}
	public void csv_header(String csv_header) {
		this.csv_header=csv_header;
	}
	
	public String connection_string() {
		return connection_string;
		//return connection_string!=null?decode(connection_string):null;
	}
	public void connection_string(String connection_string) {
		this.connection_string=connection_string;
		//this.connection_string=encode(connection_string);
	}
	
	public String username() {
		return username;
		//return username!=null?decode(username):null;
	}
	public void username(String username) {
		this.username=username;
		//this.username=encode(username);
	}
	
	public String password() {
		return password;
		//return password!=null?decode(password):null;
	}
	public void password(String password) {
		this.password=password;
		//this.password=encode(password);
	}
	
	public String database() {
		return database;
		//return database!=null?decode(database):null;
	}
	public void database(String database) {
		this.database=database;
		//this.database=encode(database);
	}
	
	public String winline_query() {
		return sql_command!=null?decode(sql_command):null;
	}
	public void winline_query(String sql_command) {
		this.sql_command=encode(sql_command);
	}
	
	public String stapelpreise_query() {
		return sql_stapelpreise!=null?decode(sql_stapelpreise):null;
	}
	public void stapelpreise_query(String sql_stapelpreise) {
		this.sql_stapelpreise=encode(sql_stapelpreise);
	}
	
	public String updates_query() {
		return sql_updates!=null?decode(sql_updates):null;
	}
	public void updates_query(String sql_updates) {
		this.sql_updates=encode(sql_updates);
	}
	
	public String inserts_query() {
		return sql_inserts!=null?decode(sql_inserts):null;
	}
	public void inserts_query(String sql_inserts) {
		this.sql_inserts=encode(sql_inserts);
	}
	
	public String deletes_query() {
		return sql_deletes!=null?decode(sql_deletes):null;
	}
	public void deletes_query(String sql_deletes) {
		this.sql_deletes=encode(sql_deletes);
	}
	
	public String verify_query() {
		return sql_verify!=null?decode(sql_verify):null;
	}
	public void verify_query(String sql_verify) {
		this.sql_verify=encode(sql_verify);
	}
	
	public boolean auto_update() {
		return this.auto_update;
	}
	public void auto_update(boolean auto_update) {
		this.auto_update=auto_update;
		if(Biene.batcher!=null) {
			Biene.batcher.auto_update=auto_update;
		}
	}
	
	public int update_interval() {
		int interval=60;
		try {
			interval=Integer.parseInt(this.update_inverval);
		} catch (Exception e) {
			Logger.$(e);
		}
		return interval;
	}
	public void update_interval(String interval_string) {
		this.update_inverval=interval_string;
		if(Biene.batcher!=null) Biene.batcher.update_start=update_interval()*60;
	}
	
	public SimpleEntry<String,String>katalogs(int id){
		if(katalogs==null) katalogs(katalog());
		if(katalogs.containsKey(id)) return katalogs.get(id);
		return new SimpleEntry<String,String>("","1TEMP");
	}
	
	public String katalog() {
		return katalog!=null?decode(katalog):null;
	}
	public void katalogs(String katalog) {
		this.katalog=encode(katalog);
		this.katalogs=new HashMap<>();
		String[]lines=katalog.split("[\\r\\n]+");
		int size=lines.length;
		for(int i1=0;i1<size;i1++) {
			String[]line=lines[i1].split(",");
			this.katalogs.put(Integer.parseInt(line[0]),new SimpleEntry<>(line[1],line[2]));
		}
	}
	
	public String meso_year() {
		return meso_year!=null?decode(meso_year):null;
	}
	public void meso_year(String meso_year) {
		this.meso_year=encode(meso_year);
	}
	
	public String http_string() {
		return http_string;
	}
	public void http_string(String http_string) {
		this.http_string=http_string;
	}
	
	public String shop_user() {
		String http=http_string();
		if(http!=null) {
			String string=http.split("user=")[1];
			String[]parse=string.split("&password=");
			return parse[0];
		}
		return null;
	}
	
	public String shop_password() {
		String http=http_string();
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
