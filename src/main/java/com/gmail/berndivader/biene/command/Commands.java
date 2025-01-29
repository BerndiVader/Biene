package com.gmail.berndivader.biene.command;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.gmail.berndivader.biene.Biene;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.command.commands.Unkown;

public class Commands {
	
    public static Commands instance;

    private final static String PACKAGE_NAME="com/gmail/berndivader/biene/command/commands";
    private static String fileName;
    public HashMap<String,String>commands;
    
    static {
        try {
            fileName=URLDecoder.decode(
                    Biene.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
                    StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            try {
                fileName=URLDecoder.decode(
                        Biene.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
                        StandardCharsets.ISO_8859_1.toString());
            } catch(UnsupportedEncodingException e1) {
                Logger.$("Fehler, weder UTF-8 noch ISO-8859 encoding gefunden.");
                Logger.$(e1);
            }
        }
    }
    
	public Commands() {
		commands=new HashMap<>();
		try {
			loadCommandClasses();
		} catch (IOException | ClassNotFoundException e) {
			Logger.$("Konsolecommandos konnten nicht geladen werden!");
			Logger.$(e);
		}
	}
	
	private void loadCommandClasses() throws IOException, ClassNotFoundException {
		
		try(JarInputStream jarStream=new JarInputStream(new FileInputStream(fileName))) {
			JarEntry entry;
			while(jarStream.available()==1) {
				entry=jarStream.getNextJarEntry();
				if(entry!=null) {
					String className=entry.getName();
					if(className.endsWith(".class")&&className.startsWith(PACKAGE_NAME)) {
						className=className.substring(0,className.length()-6).replace("/",".");
						Class<?>clazz=Class.forName(className);
						CommandAnnotation annotation=clazz.getAnnotation(CommandAnnotation.class);
						if(annotation!=null) commands.put(annotation.name(),className);
					}
				}
			}
		}
		
	}
	
	public Command getCommand(String name) {
		if(commands.containsKey(name)) {
			String className=commands.get(name);
			try {
				return (Command)Class.forName(className).getDeclaredConstructor().newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				Logger.$(String.format("Fehler beim inizialisieren vom Befehl: %s",name));
				Logger.$(e);
			}
		}
		return new Unkown();
	}
	
}
