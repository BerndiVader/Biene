package com.gmail.berndivader.biene.db;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.enums.Tasks;
import com.gmail.berndivader.biene.Helper;

public class ValidatePicture extends ResultQueryTask<String> {
	
	static String q="select c076 from t031 where upper(c076) = upper('%1%')";
	public boolean bool=false;
	String fname;
	
	public ValidatePicture(String filename) {
		super(q.replace("%1%",filename),Tasks.UNKOWN);
		fname=filename;
		future=Helper.executor.submit(this);
	}
	
	@Override
	public void completed(ResultSet result) {
		if(result!=null) {
			try {
				bool=result.last();
			} catch (SQLException e) {
				Logger.$(e);
			}
		}
	}
	
	@Override
	public void failed(ResultSet result) {
		Logger.$("Failed to execute: "+action.action());
	}
	
	@Override
	public ResultSet call() throws Exception {
		ResultSet result=null;
		try (Connection conn=DatabaseConnection.getNewConnection()) {
			PreparedStatement statement=conn.prepareStatement(this.query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			result=statement.executeQuery();
			this.completed(result);
		} catch (SQLException ex) {
			Logger.$(ex,false,true);
			this.failed(null);
		}
		
		adjustJpeg();
		
		this.took();
		latch.countDown();
		return result;
	}
	
	private void adjustJpeg() throws IOException {
		File file=new File(Utils.working_dir+"/Bilder/"+this.fname);
		if(file.exists()) {
			if(file.length()>0l) {
				JPEGImageWriteParam param=new JPEGImageWriteParam(Locale.GERMAN);
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(0.85f);
				BufferedImage image=ImageIO.read(file);
				final ImageWriter writer=ImageIO.getImageWritersByFormatName("jpg").next();
				writer.setOutput(new FileOutputStream(file));
				writer.write(null,new IIOImage(image,null,null),param);
			}
		}
	}

	@Override
	protected void max_seconds(long max) {
		this.max_seconds=max;
	}
	
}
