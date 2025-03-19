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
	@DefaultValue("4")
	private int config_version;
	@DefaultValue("dbc:sqlserver://SERVER_NAME;databaseName=DATABASE_NAME")
	private String connection_string;
	@DefaultValue("MSSQL_USER_NAME")
	private String username;
	@DefaultValue("MSSQL_USER_PASSWORD")
	private String password;
	@DefaultValue("MSSQL_DATABASE_NAME")
	private String database;
	@DefaultValue("ZTZLb/cazfdfvAFJAeHZF1FGo1iJadxLJ+GYUHrrRFL6HmDgMlpn9ITXB0LhWiF2n7TRNhFOzbhtEohdRAXvCoxoh+ZagWJ/AEHkYiiChexPaMnv7f+Q8J+00TYRTs241vT9JPdbTbWYLwgsJ6ukYX5Q3LfNoAr6BLIa53aG1KfeKXSKHtFIKW76KuJ9fD40iGrA1AVgqUQHmXK2MpSQCPSf6FtjV11wHBYrZp1xsizsUMNm1JBRK0wGwsUkaJnbh97Hih/tPjPIEyoKInjm9jGs0Gx2o3kAEYcuTymPtedysdYckIK9CDw8dXpjkjsEp6KH7Vvocr2Cwh211k9qgNoUt5OJn/nSKfVFnhmt0t020b9GEA8KhnW/AqzvCDkStZh4DSRyDut55bQH5V79vVtADaSS5zY2h3r1Kl99sylMxLHNV8jRZqX+/+TvfSv19a2Sy3cBIZrbONiioLKLu06Jf/I7jEW6lEih0oeoDMsKL2+FonGSzLNX15zYWDPMmjPkGkBjzLQs0vCzibn2OLVuzXeAjXoqwY0L3EsZKiR+YJbOq/7UtYoTURMXkABDkoSq5tXCSmAeo4cTHQAcMN9AiZZMfb2AskhOmkEiu+RRrZLbexzzKyLLgEa94FL+s7zcXuv3cTW1M1PgXN4pb16f2Pbq5vSeiZEEWKO3+e9QzYfE21T4W6hFR1r9LglPAd+lT3sUlU3Vftns2SScozV9itHME1G4/ZE+lAScgvHUyLAqc8n8cNNepV6powVSQl5GLUCOzttd65BMC0ZBUWmsRWt9gLddOtiZ9fuIKYENfFO8ZW64s/ztv8o+jZkxpEeXv7hkIK7W/rSUd7l65PSZWZXhbyo1VdQr9iDUwTa0f9awR+0KNnNBoJkJWKh1mUQcjXZOYCS1wzxEtLnc8ye9y3qj3JDxPgDmfs9CAO8Bm6PYVFX5WwivqLa/sEarBxBkU7IfgxqgZfnsuiqIBPUn/RzZcLPtgWgzCbpp/Sym+L1+7rBhlvzgTZvnD3gMaHVmKSKsCwZQgUYPUZeT7ktzhFTbPecN33JO0ib3vHUT2N4QP/nbu8AyPBhB8VWJtckjirVfxKVkQpqDL/fGhFp0YEL+rZXPuaLGXXg+MLZ+UNy3zaAK+qEzckUCv2wqbn3IkjgcNSPwv1Buq2qK1gwEpTp/mF4457Djamy3T+NhKsgshL0UsQMmcfdObSAHKrWwcqfAJ7Nh9BwXUhvVchg30g8cVERxn3mC45Cnwri4Q88dpyVdYpE6nIjsRRhkEcZExrdh46AG4pR5R8Smc3V7agz8Jh/bQ0gEwCmmI9e1mHgNJHIO6xqwMajOgDxcn9z5H5wf7fAetmfxqLsFlWjHdPmmU7Qz8JVqXn3g1GWc5ewn+mkm491ui7Cf9e+ItZh4DSRyDuvJuFsRuiT6wpkOAS3f7fhDXrljw52labitiHMghep1oiqCge1E6VT7hjtVn9u6SoLFOfEcXi5QWnifyQLXHUcAB0sjkakzB63gYdLCa6Ht+plPOcV+X+yiP13mjeCTUuFD06eoLwX/ESX/ySA92a1kaMd0+aZTtDOV3/xS7RGF0ff2GBOXiA0hUx9BpEDTxcCLOow3dwZ9Cmhq7T98yejpN84hlbmJyWKEYv/HvlnNq9o49tUAe6FiloZQcGL8ATi5pRsTJb1DnFHFD46dTaRREWroZ43AHBBsI0RB6Jtv2bnDzgM6uVyGratoNGSjMDXSpx95+pd8p9KAT8Gfam41B8YtJAHcOJD2b5xtyFTWXK69I7i1HrGfivwZKl+Vopz4RUJrFpG7sfEMqkUFP83bCg0Hpbr6Cdo5tjNMiOqPovyLo9RICENAyWhkwRLwfyCSsV76VG4PG2/PcHxm3OFtjmYIqxb+o64=")
	private String sql_command;
	@DefaultValue("jmYIqxb+o64=")
	private String sql_stapelpreise;
	@DefaultValue("ZTZLb/cazfdrv0x7xzd4/AsjR+danuCkfKYbtWP4gg/vUUG1++0UChik4BpMZ1jRKKB63Y8yJxJDoRzNelx9ea62dQlYHloZ920EXkn4FUhJ2vlDo4on6xjnJytM2R78Q/VIrKF0/ejJaGTBEvB/IJKxXvpUbg8bJaaXy/OGjc7aKA5Rwky+sDF3oK4xCZhPMYtMuro6Y7C4h3inqYFq9YIM530l4H6z")
	private String sql_updates;
	@DefaultValue("ZTZLb/cazfdrv0x7xzd4/OaaCr6WpUpYWhTR46dOpsLvUUG1++0UChik4BpMZ1jRKKB63Y8yJxJDoRzNelx9ea62dQlYHloZgc6TgIjfFLs/9LIckuLvllB7O4B+ivlb5UNpEVREcJbTs7Sy72bk2lRTGeaw+Icm/Iuj1EgIQ0CaPl3SwwW0rQ==")
	private String sql_inserts;
	@DefaultValue("ZTZLb/cazfdrv0x7xzd4/O2eWuZ3OMEEfKYbtWP4gg/vUUG1++0UChik4BpMZ1jRKKB63Y8yJxJDoRzNelx9eZ92dLaeC3Gugc6TgIjfFLs/9LIckuLvllB7O4B+ivlb5UNpEVREcJbTs7Sy72bk2lRTGeaw+Icm1MiwKnPJ/HCaPl3SwwW0rQ==")
	private String sql_deletes;
	@DefaultValue("ZTZLb/cazfeumEAW6aB89OhOzV9dBfsJunGSnrwBY+ZZ4D6sparlRhgxoQMuvUlH3WKavAV+e4t9g63D8oXBw/stdJCYjW5pkrFe+lRuDxv0my8T0pJqfVyVZYAwhaZzObYzTIjqj6L8i6PUSAhDQJffu8kIQ7RwXOru9eUAsRj2b5xtyFTWXFclrOvQpKIGkjOj6YWxbZll0vvpyyujmQ==")
	private String sql_verify;
	@DefaultValue("2015")
	private String meso_year;
	@DefaultValue("https://URL.TO.MODIFIED/shop/export/winline2_xtc.php")
	private String http_string;
	@DefaultValue("MODIFIED_USER_NAME")
	private String shop_user;
	@DefaultValue("MODIFIED_USER_PASSWORD")
	private String shop_password;
	@DefaultValue("false")
	private boolean cf_enabled;
	@DefaultValue("CLOUDFLARED_CLIENT_.access")
	private String cf_client;
	@DefaultValue("CLOUDFLARED_SECRET_TOKEN")
	private String cf_secret;
	@DefaultValue("XAUTH_WL2XTC_TOKEN")
	private String bearer_token;
	@DefaultValue("XTSOL|action|p_model|p_stock|p_sorting|p_shipping|p_tpl|p_manufacturer|p_fsk18|p_priceNoTax|p_priceNoTax.1|p_priceNoTax.2|p_priceNoTax.3|p_priceNoTax.4|p_priceNoTax.5|p_priceNoTax.6|p_groupAcc.0|p_groupAcc.1|p_groupAcc.2|p_groupAcc.3|p_groupAcc.4|p_groupAcc.5|p_groupAcc.6|p_tax|p_status|p_weight|p_ean|p_disc|p_opttpl|p_vpe|p_vpe_status|p_vpe_value|p_image.1|p_image.2|p_image|p_name.en|p_desc.en|p_shortdesc.en|p_keywords.en|p_meta_title.en|p_meta_desc.en|p_meta_key.en|p_url.en|p_name.de|p_desc.de|p_shortdesc.de|p_keywords.de|p_meta_title.de|p_meta_desc.de|p_meta_key.de|p_url.de|p_cat.0|p_cat.1|p_cat.2|p_cat.3|p_cat.4|p_cat.5")
	private String csv_header;
	@DefaultValue("f6ZXBeW2zNO2CmipZ8KmKfm6WZg/+rLIzhcsFuuvYPasZnLTJLSPnGnY6BKhA+6HYobLLZOmHejdpC+w8eTwfBxNw5rnJEPO/kApqnuZU6Ub01xemiNjJPhGo3qIOeTkYobLLZOmHeh9b+GFpYeck5sKxxx0G956ioswonh6ny8eCg+hatuDtbu1MFgHRMpKBqUqNfLDh/C8BWQEDron463l8rCu8vS6rxa7TUH8RXXsC2CbiufCV1j/+Vkpkj3OtgpoqWfCpin5ulmYP/qyyLTzLLkrsiHkyT7ZW5JCVln2c5gTSSQfXDX45dnd5z/TKHXjzbcuJGDWvgMtqNDn2+f3VPJ4pfFCsfYxyKYiOEfC4C/eA6/W76u355zvpZBpJp/LSacO9XqI1HbEljuJDjabM/02c+AaXMCahYzhJ+p3wkFw/LcdsR6QCfqG0Ss1nHWB8ajJe0qnM9ps8NpKIRPVwhaZFhH4lY89KnCjdyR9Uf8qP1jUlFriPHi72WpdiDiQKUpcq0rUqBlXYus7BvuV7CVfjS8HD8cg6kM0a8R9Uf8qP1jUlL5JQMQSYW8nkhQSq2ZIyZgU9iCAKQUqHMLgL94Dr9bvBZDorY3h7yyRlzVdTzJPkStnUG4/vV1QKHXjzbcuJGDCA8prv6rpTKwqWoQzsEywb+HjYc8XYp8XP1peXfW0kw84mA2x0BKhyr1oovnoMkOA1Id0C7NVeg6vYpV6nlovwuAv3gOv1u+VRVxn0ia2VaDbnlMudXX1KsnRANq6vDMKYY0XRI6LPOL3KmChl+RrJL11H2xkkVcqNPm9MGQGPSCov/QvG2OpHpAJ+obRKzVJ9xRM6R8M5KIsxJcNX5D/rWCzkCGP3mDC4C/eA6/W79GaCT6HGiq/x5Q6Ax2zwXPya+R05dKA4X1R/yo/WNSUvZ4AYFgIDcnjAMKHpFPsKF88Y9ECe9GoHpAJ+obRKzUBJNnMB0/HA+mMoAeVTJ8Zx6fL9axnC2qexiPKUwtGjA==")
	private String katalog;
	@DefaultValue("true")
	private boolean auto_update;
	@DefaultValue("60")
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
	
	public int meso_year() {
		try {
			return Integer.parseInt(meso_year);
		} catch (Exception e) {
			return 2015;
		}
	}
	public void meso_year(String meso_year) {
		this.meso_year=meso_year;
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
		bearer_token=token;
	}
	public String bearer_token() {
		return bearer_token;
	}
		
	private static String encode(String value) {
		return Utils.encrypt(value);
	}
	private static String decode(String value) {
		return Utils.decrypt(value);
	}
	
}
