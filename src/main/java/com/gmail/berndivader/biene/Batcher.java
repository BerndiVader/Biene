package com.gmail.berndivader.biene;

import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.db.QueryBatchTask;
import com.gmail.berndivader.biene.db.UpdateShopTask;

public 
class 
Batcher 
extends
Thread
implements
Runnable
{
	public static Stack<QueryBatchTask>query_stack;
	QueryBatchTask current;
	public long update_start;
	public boolean auto_update;
	
	static {
		query_stack=new Stack<>();
	}
	
	public Batcher() {
		auto_update=Config.data.getAutoUpdate();
		update_start=Config.data.getUpdateInterval();
	}
	
    @Override
    public void run() {
		if(!query_stack.empty()&&(current=query_stack.pop())!=null) current.batch();
		long startTime=Utils.getCurrentTimeMinutes();
		
		while(!this.isInterrupted()) {
			
			if(auto_update) {
				long elapsedTime=Utils.getCurrentTimeMinutes()-startTime;
				
				if(elapsedTime>=update_start) {
					startTime=Utils.getCurrentTimeMinutes();
					new UpdateShopTask(Config.data.getWinlineQuery());
				}
			}
			
			if(current!=null&&!current.future.isDone()&&!current.future.isCancelled()&&current.getRunningTime()/60000>1) {
				try {
					current.future.get(1,TimeUnit.SECONDS);
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					Logger.$("Cancelled task "+current.getClass().getName()+" because of timeout.",false);
					current.future.cancel(false);
				}
			}
			
			if(current==null||current.future.isDone()||current.future.isCancelled()) {
				if(!query_stack.empty()&&(current=query_stack.pop())!=null) current.batch();
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Logger.$(e,false,true);
			}
		}
	}

}
