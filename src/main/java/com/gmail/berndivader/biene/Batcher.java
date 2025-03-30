package com.gmail.berndivader.biene;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.db.QueryBatchTask;
import com.gmail.berndivader.biene.db.UpdateShopTask;
import com.gmail.berndivader.biene.gui.Main;

public 
class 
Batcher
implements
Runnable
{
	public static final ConcurrentLinkedQueue<QueryBatchTask>QUERY_STACK;
	
	public long update_start;
	public boolean auto_update;
	private long startTime;
	
	private int active_threads,scheduled_threads;
	
	private QueryBatchTask current;
	
	static {
		QUERY_STACK=new ConcurrentLinkedQueue<>();
	}
	
	public Batcher() {
		active_threads=scheduled_threads=-1;
		auto_update=Config.data.auto_update();
		update_start=Config.data.update_interval();
		
		if(!QUERY_STACK.isEmpty()&&(current=QUERY_STACK.poll())!=null) current.batch();
		startTime=Utils.getCurrentTimeMinutes();
		
		Helper.scheduler.scheduleAtFixedRate(this,0l,200l,TimeUnit.MILLISECONDS);
	}
	
	@Override
    public void run() {
		
		if(Helper.scheduler.isShutdown()) return;
				
		if(auto_update) {
			long elapsedTime=Utils.getCurrentTimeMinutes()-startTime;
			
			if(elapsedTime>=update_start) {
				startTime=Utils.getCurrentTimeMinutes();
				new UpdateShopTask();
			}
		}
		
		if(current!=null&&current.getRunningTime()*0.001>current.max_seconds&&!current.future.isDone()&&!current.future.isCancelled()) {
			try {
				current.future.get(3l,TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				Logger.$("Cancelled task ".concat(current.getClass().getName()).concat(" because of timeout."),false);
				current.future.cancel(false);
			}
		}
		
		if(current==null||current.future.isDone()||current.future.isCancelled()) {
			if(!QUERY_STACK.isEmpty()&&(current=QUERY_STACK.poll())!=null) current.batch();
		}
		
		if(!Biene.no_gui&&(Helper.executor.getActiveCount()!=active_threads||QUERY_STACK.size()!=scheduled_threads)) {
			active_threads=Helper.executor.getActiveCount();
			scheduled_threads=QUERY_STACK.size();
			Main.instance.setTitle(Main.APP_NAME.concat(
					String.format(" [active:%s][queried:%s]",Integer.toString(active_threads),Integer.toString(scheduled_threads))));
		}

	}

}
