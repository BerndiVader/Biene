package com.gmail.berndivader.biene;

public
abstract
class 
Worker
{
	/**
	 * In milliseconds.
	 */
	protected long start_time;
	
	/**
	 * Max seconds the task is allowed to run until timeout.
	 */
	public long max_seconds;
	private final static String message=" ms";
	
	public Worker() {
		start_time=System.currentTimeMillis();
		max_seconds(60l);
	}
	
	public Worker(long max_time) {
		start_time=System.currentTimeMillis();
		this.max_seconds=max_time;
	}
	
	
	public void took() {
		if(Biene.take_time) {
			long eclapsed_time=System.currentTimeMillis()-start_time;
			Logger.$(Long.toString(eclapsed_time)+message,false);
		}
	}
	
	/**
	 * Returns the running time in milliseconds since the worker started.
	 *
	 * @return the running time in milliseconds
	 */	
	public long getRunningTime() {
		return System.currentTimeMillis()-start_time;
	}
	
	protected abstract void max_seconds(long max);
	
}
