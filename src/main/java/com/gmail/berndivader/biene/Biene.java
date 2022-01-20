package com.gmail.berndivader.biene;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.gui.Main;
import com.gmail.berndivader.biene.http.Helper;

public 
class 
Biene 
{
	public static final String BUILD = "20220112";
	public static boolean take_time=false;
	public static boolean no_gui=false;
	
	public static Config config;
	public static boolean strict;
	
	public static Batcher batcher;
	public static Headless headless;
	
	
	public static void main(String[] args) {
		strict=false;
		try {
			if(Utils.checkForInstance()) System.exit(0);
		} catch (IOException e) {
			Logger.$(e,false,false);
			e.printStackTrace();
			System.exit(0);
		}
		
		int size=args.length;
		if(size>0) {
			for(int i1=0;i1<size;i1++) {
				if(args[i1].equalsIgnoreCase("-headless")) {
					no_gui=true;
				} else if(args[i1].equalsIgnoreCase("-timer")) {
					take_time=true;
				}
			}
		}
		
		if(!GraphicsEnvironment.isHeadless()&&!no_gui) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					Main.init();
				}
			});
			
			config=new Config();
			
			batcher=new Batcher();
			batcher.start();
			
			Helper.init();
			Utils.init();
			
		} else {
			
			config=new Config();
			
			batcher=new Batcher();
			batcher.start();
			
			Helper.init();
			Utils.init();
			
			headless=new Headless();
		}
		
		
		
		
	}	
}
