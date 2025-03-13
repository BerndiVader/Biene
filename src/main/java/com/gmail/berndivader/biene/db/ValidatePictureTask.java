package com.gmail.berndivader.biene.db;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.enums.Tasks;

public
class
ValidatePictureTask
extends
ResultQueryTask<File[]>
{
	String file_name;
	FilenameFilter filter;
	File[]file_list;
	
	public ValidatePictureTask(String query,Tasks event_enum,File[]files) {
		super(query,event_enum);
		
		String[]split=query.split("c076=");
		file_name=split[1].substring(1,split[1].length()-1);
		file_list=new File("Bilder/").listFiles();
		this.execute();
	}
	
	@Override
	public void completed(ResultSet result) {
		if(result!=null) {
			try {
				if(!result.next()) {
					File file=new File("Bilder/"+file_name);
					if(file.exists()) {
						Logger.$("Entferne Bild "+file.getName()+" - kein Artikel gefunden.",false,false);
						file.delete();
					}
				}
			} catch (SQLException e) {
				Logger.$(e,false,false);
			}
			Utils.updatePicturesList();
		}
	}

	@Override
	public void failed(ResultSet result) {
		Logger.$("ValidatePictureTask konnte nicht abgeschlossen werden.",false,false);
	}

	@Override
	protected void max_seconds(long max) {
		this.max_seconds=max;
	}
}
