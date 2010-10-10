package executor.commonj;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkItem;
import commonj.work.WorkManager;

public class WorkItemFuture<T> implements Future<T> {

	private WorkManager manager;
	private WorkItem item;

	public WorkItemFuture(WorkManager manager, WorkItem item) {
		this.item = item;
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		throw new UnsupportedOperationException("WorkItem cannot be cancelled");
	}

	public boolean isCancelled() {
		return false;
	}

	public boolean isDone() {
		return (WorkEvent.WORK_COMPLETED == item.getStatus());
	}

	public T get() throws InterruptedException, ExecutionException {
		try {
			return get(WorkManager.INDEFINITE);
		} catch (TimeoutException e) {
			// The timeout should never happen. Rethrow anyway.
			throw new IllegalStateException(e);			
		}
	}

	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return get(TimeUnit.MILLISECONDS.convert(timeout, unit));
	}
	
	@SuppressWarnings("unchecked")
	private T get(long timeoutmillis) throws InterruptedException, ExecutionException, TimeoutException {
		try {
			if(!manager.waitForAll(Collections.singleton(item), timeoutmillis)) {
				throw new TimeoutException();
			}
			return ((CallableWork<T>) item.getResult()).getResult();
		} catch (WorkException e) {
			Throwable cause = e.getCause();
			if(cause != null) {
				if(cause instanceof CallableThrowedException) {
					throw new ExecutionException(cause.getCause());
				}
				throw new ExecutionException(cause);				
			}
			throw new ExecutionException(e);
		}		
	}

}
