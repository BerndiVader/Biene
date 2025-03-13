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
	private String shop_user;
	private String shop_password;
	private boolean cf_enabled;
	private String cf_client;
	private String cf_secret;
	private String bearer_token;
	private String bearer_expire;
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
	}
	public void connection_string(String connection_string) {
		this.connection_string=connection_string;
	}
	
	public String username() {
		return username;
	}
	public void username(String username) {
		this.username=username;
	}
	
	public String password() {
		return password;
	}
	public void password(String password) {
		this.password=password;
	}
	
	public String database() {
		return database;
	}
	public void database(String database) {
		this.database=database;
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
	
	public void cf_enabled(boolean cloudflare_enabled) {
		cf_enabled=cloudflare_enabled;
	}
	public boolean cf_enabled() {
		return cf_enabled;
	}
	
	public void cf_client(String cloudflare_client) {
		cf_client=cloudflare_client;
	}
	public String cf_client() {
		return cf_client;
	}
	
	public void cf_secret(String cloudflare_secret) {
		cf_secret=cloudflare_secret;
	}
	public String cf_secret() {
		return cf_secret;
	}

	public void shop_user(String user) {
		shop_user=user;
	}
	public String shop_user() {
		return shop_user;
	}

	public void shop_password(String password) {
		shop_password=password;
	}
	public String shop_password() {
		return shop_password;
	}
	
	public void bearer_token(String token) {
		if(token==null) return;
		bearer_token=Utils.encrypt(token);
	}
	public String bearer_token() {
		return bearer_token!=null?Utils.decrypt(bearer_token):"";
	}
	
	public void bearer_expire(long expire) {
		bearer_expire=Utils.encrypt(Long.toString(expire));
	}
	public long bearer_expire() {
		if(bearer_expire==null) return 0l;
		try {
			return Long.parseLong(Utils.decrypt(bearer_expire));
		} catch(Exception e) {
			return 0l;
		}
	}
	
	private static String encode(String value) {
		return Utils.encrypt(value);
	}
	private static String decode(String value) {
		return Utils.decrypt(value);
	}
	
}
