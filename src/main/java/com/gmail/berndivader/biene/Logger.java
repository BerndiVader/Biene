package com.gmail.berndivader.biene;

import java.awt.EventQueue;
import java.awt.TrayIcon.MessageType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import com.gmail.berndivader.biene.gui.Main;
import com.gmail.berndivader.biene.http.Helper;

public
class 
Logger 
{
	static File error_log;
	
	static {
		error_log=new File(Utils.working_dir+"/error.log");
		try {
			error_log.createNewFile();
		} catch (IOException e) {
			Logger.$(e);
		}
	}
	
	public static void $(String text,boolean balloned,boolean http_log) {
		if(http_log&&Helper.client.isRunning()) Utils.writeLog(text);
		if(!Biene.no_gui) {
			awt_log(text,balloned,http_log);
		} else {
			cli_log(text,balloned,http_log);
		}
	}
	
	public static void $(String text) {
		$(text,false,false);
	}
	
	public static void $(String text,boolean balloned) {
		$(text,balloned,false);
	}
	
	public static void $(Exception e) {
		$(e,false,false);
	}
	
	public static void $(Exception e,boolean balloned,boolean http_log) {
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		
		try(PrintWriter out=new PrintWriter(new BufferedWriter(new FileWriter(error_log.getAbsoluteFile(),true)))) {
		    out.println(sw.toString());
		} catch (IOException e1) {
			Logger.$(e1);
		}
		
		$("ERROR: "+e.getMessage(),false,false);
		
		pw.close();
		try {
			sw.close();
		} catch (IOException e1) {
			Logger.$(e1);
		}
	}
	
	static void awt_log(String text,boolean balloned,boolean http_log) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				String[]arr=Main.frame.log_area.getText().split("\\n");
				if(arr.length>80) {
					String[]arr1=Arrays.copyOfRange(arr, 50, arr.length);
					String trimmed="";
					for(int i1=0;i1<arr1.length;i1++) {
						trimmed+=arr1[i1]+"\n";
					}
					Main.frame.log_area.setText(trimmed);
				}
				Main.frame.log_area.append(text+"\n");
				if(balloned) Main.frame.tray_icon.displayMessage("Info",text,MessageType.NONE);
			}
		});
	}
	
	static void cli_log(String text,boolean balloned,boolean http_log) {
		System.out.println(text);
	}
	
}
