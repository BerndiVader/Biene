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
import java.util.Optional;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.enums.EventEnum;
import com.gmail.berndivader.biene.http.Helper;

public class ValidatePicture extends ResultQueryTask<String> {
	
	static String q="select c076 from t031 where upper(c076) = upper('%1%')";
	public boolean bool=false;
	String fname;
	
	public ValidatePicture(String filename) {
		super(q.replace("%1%",filename), EventEnum.UNKOWN);
		fname=filename;
		future=Helper.executor.submit(this);
	}
	
	@Override
	public void completed() {
		if(result.isPresent()) {
			ResultSet source=result.get();
			try {
				bool=source.last();
			} catch (SQLException e) {
				Logger.$(e);
			}
		}
	}
	
	@Override
	public void failed() {
		Logger.$("Failed to execute: "+query);
	}
	
	@Override
	public ResultSet call() throws Exception {
		try (Connection conn=DatabaseConnection.getNewConnection()) {
			PreparedStatement statement=conn.prepareStatement(this.query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			result=Optional.ofNullable(statement.executeQuery());
			this.completed();
		} catch (SQLException ex) {
			Logger.$(ex,false,true);
			this.failed();
		}
		
		adjustJpeg();
		
		this.took();
		latch.countDown();
		return result.orElse(null);
	}
	
	void adjustJpeg() throws IOException {
		File file=new File(Utils.working_dir+"/Bilder/"+this.fname);
		if(file.exists()) {
			if(file.length()>3500000l) {
				JPEGImageWriteParam param=new JPEGImageWriteParam(Locale.GERMAN);
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(0.75f);
				BufferedImage image=ImageIO.read(file);
				final ImageWriter writer=ImageIO.getImageWritersByFormatName("jpg").next();
				writer.setOutput(new FileOutputStream(file));
				writer.write(null,new IIOImage(image,null,null),param);
			}
		}
	}
	
}
