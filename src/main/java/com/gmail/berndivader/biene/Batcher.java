package com.gmail.berndivader.biene;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.db.QueryBatchTask;
import com.gmail.berndivader.biene.db.UpdateShopTask;

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
	
	private QueryBatchTask current;
	
	static {
		QUERY_STACK=new ConcurrentLinkedQueue<>();
	}
	
	public Batcher() {
		auto_update=Config.data.getAutoUpdate();
		update_start=Config.data.getUpdateInterval();
		
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
				new UpdateShopTask(Config.data.getWinlineQuery());
			}
		}
		
		if(current!=null&&current.getRunningTime()/60000>current.max_time&&!current.future.isDone()&&!current.future.isCancelled()) {
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
			
	}

}
