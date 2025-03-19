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
import com.gmail.berndivader.biene.http.post.PostImportCSVSync;
import com.gmail.berndivader.biene.http.post.PostUploadCSV;
import com.gmail.berndivader.biene.http.post.PostValidateImageFileSync;
import com.gmail.berndivader.biene.rtf2html.RtfHtml;
import com.gmail.berndivader.biene.rtf2html.RtfReader;

public 
class
UpdateShopTask
extends
QueryBatchTask
{
	
	private static final String START_INFO="-- Starte Shop Update Task %s...";
	private static final String SCHEDULED_INFO="-- Scheduled Shop Update Task %s...";
	private static final String END_INFO="-- Beende Shop Update Task %s.";
	
	private RtfReader rtf_reader;
	private RtfHtml rtf_html;
	
	private boolean withImageUpdate;
	
	public UpdateShopTask(boolean withImageUpdate) {
		super(Config.data.winline_query());
		
		rtf_reader=new RtfReader();
		rtf_html=new RtfHtml();
		this.withImageUpdate=withImageUpdate;
		
		this.add();
		Logger.$(String.format(SCHEDULED_INFO,this.uuid.toString()),false,false);
	}
	
	public UpdateShopTask() {
		this(true);
	}
	
	@Override
	public Boolean call() throws Exception {
		Logger.$(String.format(START_INFO,this.uuid.toString()),false,true);
		int mesoYear=Config.data.meso_year();
		query=query.replace("$mesoyear$",Integer.toString((mesoYear-1900)*12));

		try(Connection conn=DatabaseConnection.getNewConnection()) {
			conn.prepareStatement(this.query).execute();
			
			String update_info="";
			StringBuilder csv_string=new StringBuilder(Config.data.csv_header().concat("\n"));
			int change_counter=0;
			
			try(PreparedStatement statement=conn.prepareStatement(Config.data.updates_query(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)) {
				try(ResultSet changes=statement.executeQuery()) {
					if(changes.first()) {
						do {
							validateImage(changes.getString("c076"));
							csv_string.append(Utils.makeCSVLine(Action.UPDATE,changes,rtf_reader,rtf_html));
						} while(changes.next());
						changes.last();
						change_counter+=changes.getRow();
						String s1=changes.getRow()==1?"":"en";
						update_info+=Integer.toString(changes.getRow()).concat(" Änderung").concat(s1).concat(", ");
					} else {
						update_info+="0 Änderungen, ";
					}
				}
			}
			
			try(PreparedStatement statement=conn.prepareStatement(Config.data.inserts_query(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)) {
				try(ResultSet inserts=statement.executeQuery()) {
					if(inserts.first()) {
						do {
							validateImage(inserts.getString("c076"));
							csv_string.append(Utils.makeCSVLine(Action.INSERT,inserts,rtf_reader,rtf_html));
						} while(inserts.next());
						inserts.last();
						change_counter+=inserts.getRow();
						String s1=inserts.getRow()==1?"":"en";
						update_info+=Integer.toString(inserts.getRow()).concat(" Neuerung").concat(s1).concat(" und ");
					} else {
						update_info+=("0 Neuerungen und ");
					}
				}
			}
			
			try(PreparedStatement statement=conn.prepareStatement(Config.data.deletes_query(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)) {
				try(ResultSet deletes=statement.executeQuery()) {
					if(deletes.first()) {
						do {
							csv_string.append(Utils.makeCSVLine(Action.DELETE,deletes,rtf_reader,rtf_html));
						} while(deletes.next());
						deletes.last();
						change_counter+=deletes.getRow();
						String s1=deletes.getRow()==1?"":"en";
						update_info+=Integer.toString(deletes.getRow()).concat(" Löschung").concat(s1).concat(" gefunden.");
					} else {
						update_info+="0 Löschungen gefunden.";
					}
				}
			}
			
			Logger.$(update_info,false,true);
			
			if(change_counter>0) {
				File csv_file=Utils.create_csv_file(csv_string.toString());
				if(csv_file!=null&&csv_file.exists()) {
					String file_name=csv_file.getName();
					PostUploadCSV upload=new PostUploadCSV(Config.data.http_string(),csv_file);
					upload.latch.await(upload.max_seconds,TimeUnit.SECONDS);
					if(!upload.failed) {
						PostImportCSVSync csv_import=new PostImportCSVSync(Config.data.http_string(),file_name);
						if(csv_import.join()&&!csv_import.failed) {
							SimpleQuery query=new SimpleQuery(Config.data.verify_query());
							query.latch.await(query.max_seconds,TimeUnit.SECONDS);
						}
					} else {
						Logger.$("-- Upload csv file failed", true);
					}
				} else {
					Logger.$("-- Failed to create csv file", true);
				}
				csv_file.delete();
			}
			this.completed(null);
			
		} catch (Exception ex) {
			Logger.$(ex,false,true);
			this.failed(null);
		}
		
		if(withImageUpdate) {
			new UpdatePicturesTask();
		}
		this.took();
		latch.countDown();
		return true;
	}
	
	private void validateImage(String image_name) {
        if(image_name!=null&&image_name.length()>0) {
        	new PostValidateImageFileSync(Config.data.http_string(),image_name);
        }
	}

	@Override
	public void completed(Void result) {
		Logger.$(String.format(END_INFO,this.uuid.toString()),false,true);
	}

	@Override
	public void failed(Void result) {
	}

	/**
	 * Worker timeout to 3 minutes.
	 */
	@Override
	protected void max_seconds(long max) {
		this.max_seconds=3l*60l;
	}
	
}
