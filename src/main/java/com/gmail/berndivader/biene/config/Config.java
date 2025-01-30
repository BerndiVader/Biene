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

public 
class 
Config
{
	public static File config_dir,config_file;
	public static Gdata data;
	public static int version;
	
	static {
		getVersion();
		config_dir=new File(Utils.working_dir.getAbsolutePath()+"/config");
		config_file=new File(config_dir.getAbsolutePath()+"/config.json");
	}
	
	public Config() {
		if(!config_dir.exists()||!config_file.exists()) {
			createDefault();
		} else {
			if(loadConfig()) Logger.$("Konfiguration erfolgfreich geladen.",false,false);
		}
		if(data.getConfig_version()<version) {
			data.setConfig_version(version);
			Logger.$("Konfigurationsdatei Älter als Biene, schreibe default.",false);
			saveConfig();
			if(loadConfig()) Logger.$("Konfiguration erfolgreich geladen.",false,false);
		}
	}
	
	private static String getCSVHeader() {
		return inputstream2String(Biene.class.getResourceAsStream("/csv_header.txt"));
	}
	
	private static void getVersion() {
		String parse=inputstream2String(Biene.class.getResourceAsStream("/version.info"));
		if(parse!=null) version=Integer.parseInt(parse);
		Logger.$("Biene ".concat(Biene.BUILD).concat(" - Config: ")+version,false);
	}
	
	public static boolean loadConfig() {
		boolean ok=true;
		try (FileReader reader=new FileReader(config_file.getAbsoluteFile())) {
			data=Utils.GSON.fromJson(reader,Gdata.class);
		} catch (IOException e) {
			ok=false;
			Logger.$(e);
		}
		if (!ok) {
			Logger.$("Konfiguration konnte nicht geladen werden.",false,true);
		} else {
			if(data.getCSVHeader()==null||data.getCSVHeader().isEmpty()) data.set_csv_header(getCSVHeader());
		}
		return ok;
	}
	
	public static boolean saveConfig() {
		boolean error=false;
		try (FileWriter writer=new FileWriter(config_file.getAbsoluteFile())) {
	        Utils.GSON.toJson(data,writer);
		} catch (IOException e) {
			error=true;
			Logger.$(e);
		}		
		return error;
	}
	
	private static void createDefault() {
		boolean error=false;
		config_dir.mkdir();
		try (Reader reader=new InputStreamReader(Biene.class.getResourceAsStream("/config.json"))){
			data=Utils.GSON.fromJson(reader,Gdata.class);
		} catch (IOException e) {
			Logger.$(e);
		};
		
		error=saveConfig();
		if(!error) {
			Logger.$("Default Konfiguration erfolgreich erstellt.",false);
		} else {
			Logger.$("Fehler beim Erstellen der default Konfiguration aufgetreten.",false,true);
			System.exit(0);
		}
	}
	
	static String inputstream2String(InputStream is) {
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
