package executor.commonj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import commonj.work.WorkException;
import commonj.work.WorkItem;
import commonj.work.WorkManager;


public class CommonjWorkManagerExecutorService implements ExecutorService {

	private WorkManager workManager;
	
	public void execute(Runnable command) {
		if(command == null) throw new NullPointerException("command cannot be null");
		try {
			workManager.schedule(new RunnableWork(command));
		} catch (WorkException e) {
			throw new RejectedExecutionException(e);			
		}
	}

	public <T> Future<T> submit(Callable<T> task) {
		checkNotNull("task",task);
		try {
			WorkItem item = workManager.schedule(new CallableWork<T>(task));
			return new WorkItemFuture<T>(workManager,item);
		} catch (WorkException e) {
			throw new RejectedExecutionException(e);			
		}
	}

	public <T> Future<T> submit(Runnable task, T result) {
		checkNotNull("task", task);
		return submit(new RunnableWithResultCallable<T>(task,result));
	}

	public Future<?> submit(Runnable task) {
		checkNotNull("task", task);
		return submit(task, null);
	}

	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		checkNotNull("tasks", tasks);
		if(tasks.isEmpty()) return Collections.emptyList();
		List<Future<T>> futures = new ArrayList<Future<T>>(tasks.size());
		List<WorkItem> workItems = new ArrayList<WorkItem>(tasks.size());
		try {
			for(Callable<T> task : tasks) {
					WorkItem item = workManager.schedule(new CallableWork<T>(task));
					workItems.add(item);
					futures.add(new WorkItemFuture<T>(workManager,item));
			}
		} catch (WorkException e) {
			throw new RejectedExecutionException(e);
		}
		if(workManager.waitForAll(workItems, WorkManager.INDEFINITE)) {
			return futures;
		} else {
			throw new IllegalStateException("waitForAll(WorkManager.INDEFINITE) should always return true");
		}
	}

	public <T> List<Future<T>> invokeAll(
			Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		throw new UnsupportedOperationException("WorkManager items cannot be cancelled which this method specifies");
	}

	public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		throw new UnsupportedOperationException("WorkManager items cannot be cancelled which this method specifies");
	}

	public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
			long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		throw new UnsupportedOperationException("WorkManager items cannot be cancelled which this method specifies");
	}
	
	private void checkNotNull(String paramName,Object param) {
		if(param == null) throw new NullPointerException(paramName+" must not be null");
	}
	
	public void shutdown() {
		throw new UnsupportedOperationException("WorkManager lifecycle is not manageable");
	}

	public List<Runnable> shutdownNow() {
		throw new UnsupportedOperationException("WorkManager lifecycle is not manageable");
	}

	public boolean isShutdown() {
		return false;
	}

	public boolean isTerminated() {
		return false;
	}

	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		throw new UnsupportedOperationException("WorkManager lifecycle is not manageable");
	}

}
