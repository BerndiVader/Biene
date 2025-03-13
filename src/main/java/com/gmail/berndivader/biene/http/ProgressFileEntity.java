package com.gmail.berndivader.biene.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;

public class ProgressFileEntity extends FileEntity {

	private final ProgressListener listener;
	
	public ProgressFileEntity(File file,ContentType type,ProgressListener listener) {
		super(file,type);
		this.listener=listener;
	}
	
	@Override
	public void writeTo(OutputStream out) throws IOException {
		try(FileInputStream filestream=new FileInputStream(file)) {
			byte[]buffer=new byte[4096];
			long totalbytes=file.length();
			long transferred=0l;
			int read;
			while((read=filestream.read(buffer))!=-1) {
				out.write(buffer,0,read);
				transferred+=read;
				listener.progress(transferred,totalbytes);
			}
		}
	}

}
