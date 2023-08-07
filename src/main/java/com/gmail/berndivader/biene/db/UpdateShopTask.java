package com.gmail.berndivader.biene.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.enums.Action;
import com.gmail.berndivader.biene.http.post.PostImportCSV;
import com.gmail.berndivader.biene.http.post.PostUploadCSV;
import com.gmail.berndivader.biene.http.post.PostValidateImageFile;
import com.gmail.berndivader.biene.rtf2html.RtfHtml;
import com.gmail.berndivader.biene.rtf2html.RtfReader;

public 
class
UpdateShopTask
extends
QueryBatchTask
{
	
	static String start_info="-- Starte Shop Update Task %s...";
	static String scheduled_info="-- Scheduled Shop Update Task %s...";
	static String ende_info="-- Beende Shop Update Task %s.";
	
	RtfReader rtf_reader;
	RtfHtml rtf_html;
	
	public UpdateShopTask(String query) {
		super(query);
		
		rtf_reader=new RtfReader();
		rtf_html=new RtfHtml();
		
		this.add();
		Logger.$(String.format(scheduled_info,this.uuid.toString()),false,false);
	}
	
	@Override
	public ResultSet call() throws Exception {
		Logger.$(String.format(start_info,this.uuid.toString()),false,true);
		int mesoYear=Integer.parseInt(Config.data.getMeso_year());
		query=query.replace("$mesoyear$",Integer.toString((mesoYear-1900)*12));
		try (Connection conn=DatabaseConnection.getNewConnection()) {
			conn.prepareStatement(this.query).execute();
			PreparedStatement statement=conn.prepareStatement(Config.data.getUpdatesQuery(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet changes=statement.executeQuery();
			statement=conn.prepareStatement(Config.data.getInsertsQuery(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet inserts=statement.executeQuery();
			statement=conn.prepareStatement(Config.data.getDeletesQuery(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet deletes=statement.executeQuery();
			String update_info="";
			int change_counter=0;
			if(changes.next()) {
				changes.last();
				change_counter+=changes.getRow();
				String s1=changes.getRow()==1?"":"en";
				update_info+=Integer.toString(changes.getRow()).concat(" Änderung").concat(s1).concat(", ");
			} else {
				update_info.concat("0 Änderungen, ");
			}
			if(inserts.next()) {
				inserts.last();
				change_counter+=inserts.getRow();
				String s1=inserts.getRow()==1?"":"en";
				update_info+=Integer.toString(inserts.getRow()).concat(" Neuerung").concat(s1).concat(" und ");
			} else {
				update_info.concat("0 Neuerungen und ");
			}
			if(deletes.next()) {
				deletes.last();
				change_counter+=deletes.getRow();
				String s1=deletes.getRow()==1?"":"en";
				update_info+=Integer.toString(deletes.getRow()).concat(" Löschung").concat(s1).concat(" gefunden.");
			} else {
				update_info.concat("0 Löschungen gefunden.");
			}
			Logger.$(update_info,false,true);
			
			if(change_counter>0) {
				changes.beforeFirst();
				inserts.beforeFirst();
				deletes.beforeFirst();
				
				String csv_string=Config.data.getCSVHeader().concat("\n");
				
				if(changes.next()) {
					do {
						validateImage(changes.getString("c076"));
						csv_string+=Utils.makeCSVLine(Action.UPDATE,changes,rtf_reader,rtf_html);
					}while(changes.next());
				}
				if(inserts.next()) {
					do {
						validateImage(changes.getString("c076"));
						csv_string+=Utils.makeCSVLine(Action.INSERT,inserts,rtf_reader,rtf_html);
					}while(inserts.next());
				}
				if(deletes.next()) {
					do {
						csv_string+=Utils.makeCSVLine(Action.DELETE,deletes,rtf_reader,rtf_html);
					}while(deletes.next());
				}
				
				File csv_file=Utils.create_csv_file(csv_string);
				if(csv_file!=null&&csv_file.exists()) {
					String file_name=csv_file.getName();
					PostUploadCSV upload=new PostUploadCSV(Config.data.getHttp_string(),csv_file);
					upload.latch.await(5,TimeUnit.MINUTES);
					if(!upload.failed) {
						PostImportCSV csv_import=new PostImportCSV(Config.data.getHttp_string(),file_name);
						csv_import.latch.await(3,TimeUnit.MINUTES);
						if(!csv_import.failed) {
							SimpleQuery query=new SimpleQuery(Config.data.getVerifyQuery());
							query.latch.await(3,TimeUnit.MINUTES);
						}
					} else {
						Logger.$("-- Upload csv file failed", true);
					}
				} else {
					Logger.$("-- Failed to create csv file", true);
				}
				csv_file.delete();
			}
			this.completed();
			changes.close();
			deletes.close();
			inserts.close();
			statement.close();
		} catch (Exception ex) {
			Logger.$(ex,false,true);
			this.failed();
		} finally {
			
		}
		new UpdatePicturesTask("");
		this.took();
		latch.countDown();
		return null;
	}
	
	private void validateImage(String image_name) {
        if(image_name!=null&&image_name.length()>0) {
        	new PostValidateImageFile(Config.data.getHttp_string(),image_name);
        }
	}

	@Override
	public void completed() {
		Logger.$(String.format(ende_info,this.uuid.toString()),false,true);
	}

	@Override
	public void failed() {
	}
	
}
