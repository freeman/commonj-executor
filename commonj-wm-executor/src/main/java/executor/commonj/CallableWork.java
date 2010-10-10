package executor.commonj;

import java.util.concurrent.Callable;

import commonj.work.Work;

public class CallableWork<T> extends AbstractWork implements Work {

	private Callable<T> task;
	private T result = null;

	public CallableWork(Callable<T> task) {
		this.task = task;
	}

	public void run() {
		try {
			result = task.call();
		} catch (Exception e) {
			throw new CallableThrowedException(e);
		}
	}

	public T getResult() {
		return result;
	}

}
