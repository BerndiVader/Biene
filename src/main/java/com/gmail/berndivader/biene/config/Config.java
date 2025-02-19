package com.gmail.berndivader.biene.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import com.gmail.berndivader.biene.Biene;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;

public 
class 
Config
{
	public static File config_dir,config_file;
	public static Gdata data;
	public static int config_version;
	private static String BUILD="19700101";
	
	static {
		initVersions();
		config_dir=new File(Utils.working_dir.getAbsolutePath()+"/config");
		config_file=new File(config_dir.getAbsolutePath()+"/config.json");
	}
	
	public Config() {
		if(!config_dir.exists()||!config_file.exists()) {
			createDefault();
		} else {
			if(loadConfig()) Logger.$("Konfiguration erfolgfreich geladen.",false,false);
		}
		if(data.config_version()<config_version) {
			data.config_version(config_version);
			Logger.$("Konfigurationsdatei Älter als Biene, schreibe default.",false);
			saveConfig();
			if(loadConfig()) Logger.$("Konfiguration erfolgreich geladen.",false,false);
		}
	}
	
	
	
	private static String csv_header() {
		return Utils.getStringFromResource("csv_header.txt");
	}
	
	private static void initVersions() {
		Properties properties=new Properties();
		try(InputStream stream=Biene.class.getClassLoader().getResourceAsStream("version.properties")) {
			if(stream==null) throw new IOException("version.properties file not found!");
			properties.load(stream);
		} catch (IOException e) {
			Logger.$(e);
		}
		Config.BUILD=properties.getProperty("build.date","19700101");
		config_version=Integer.parseInt(properties.getProperty("config.version","4"));
		Logger.$("Biene ".concat(Config.BUILD).concat(" - Config: ")+config_version,false);
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
			if(data.csv_header()==null||data.csv_header().isEmpty()) data.csv_header(csv_header());
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
		try (Reader reader=new InputStreamReader(Biene.class.getClassLoader().getResourceAsStream("config.json"))){
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
	
}
