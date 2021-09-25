package com.gmail.berndivader.biene.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;

import com.gmail.berndivader.biene.Biene;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public 
class 
Config
{
	public static File config_dir,config_file;
	public static Gdata data;
	public static int version;
	
	static {
		get_version();
		config_dir=new File(Utils.working_dir.getAbsolutePath()+"/config");
		config_file=new File(config_dir.getAbsolutePath()+"/config.json");
	}
	
	public Config() {
		if(!config_dir.exists()||!config_file.exists()) {
			create_default();
		} else {
			if(load_config()) Logger.$("Konfiguration erfolgfreich geladen.",false,false);
		}
		if(data.getConfig_version()<version) {
			data.setConfig_version(version);
			Logger.$("Konfigurationsdatei älter als Biene, schreibe default.",false);
			save_config();
			if(load_config()) Logger.$("Konfiguration erfolgfreich geladen.",false,false);
		}
	}
	
	private static String get_csv_header() {
		return inputstream_to_string(Biene.class.getResourceAsStream("/csv_header.txt"));
	}
	
	private static void get_version() {
		String parse=inputstream_to_string(Biene.class.getResourceAsStream("/version.info"));
		if(parse!=null) version=Integer.parseInt(parse);
		Logger.$("Biene "+Biene.BUILD+" - Config: "+version,false);
	}
	
	public static boolean load_config() {
		boolean ok=true;
		try (FileReader reader=new FileReader(config_file.getAbsoluteFile())) {
			data=new Gson().fromJson(reader,Gdata.class);
		} catch (IOException e) {
			ok=false;
			Logger.$(e);
		}
		if (!ok) {
			Logger.$("Konfiguration konnte nicht geladen werden.",false,true);
		} else {
			if(data.get_csv_header()==null||data.get_csv_header().isEmpty()) data.set_csv_header(get_csv_header());
		}
		return ok;
	}
	
	public static boolean save_config() {
		boolean error=false;
		try (FileWriter writer=new FileWriter(config_file.getAbsoluteFile())) {
	        new GsonBuilder().setPrettyPrinting().create().toJson(data,writer);
		} catch (IOException e) {
			error=true;
			Logger.$(e);
		}		
		return error;
	}
	
	private static void create_default() {
		boolean error=false;
		config_dir.mkdir();
		try (Reader reader=new InputStreamReader(Biene.class.getResourceAsStream("/config.json"))){
			data=new Gson().fromJson(reader,Gdata.class);
		} catch (IOException e) {
			Logger.$(e);
		};
		
		error=save_config();
		if(!error) {
			Logger.$("Default Konfiguration erfolgreich erstellt.",false);
		} else {
			Logger.$("Fehler beim Erstellen der default Konfiguration aufgetreten.",false,true);
			System.exit(0);
		}
	}
	
	static String inputstream_to_string(InputStream is) {
		String output=null;
		try (Scanner s=new Scanner(is)){
			s.useDelimiter("\\A");
			output=s.hasNext()?s.next():"";
			try {
				is.close();
			} catch (IOException e1) {
				Logger.$(e1);
			}
		};
		return output;
	}
}
