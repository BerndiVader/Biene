package com.gmail.berndivader.biene;

public
abstract
class 
Worker
{
	protected long start_time;
	public long max_time;
	final static String message=" ms";
	
	public Worker() {
		start_time=System.currentTimeMillis();
		setMaxTime(1l);
	}
	
	public void took() {
		if(Biene.take_time) {
			long eclapsed_time=System.currentTimeMillis()-start_time;
			Logger.$(Long.toString(eclapsed_time)+message,false);
		}
	}
	
	public long getRunningTime() {
		return System.currentTimeMillis()-start_time;
	}
	
	protected abstract void setMaxTime(long max);
}
