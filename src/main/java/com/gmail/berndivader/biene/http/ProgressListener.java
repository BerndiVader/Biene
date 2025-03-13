package com.gmail.berndivader.biene.http;

public interface ProgressListener {
	void progress(long transmittedBytes,long totalBytes);
}
