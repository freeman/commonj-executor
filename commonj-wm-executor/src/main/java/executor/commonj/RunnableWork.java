package executor.commonj;

import commonj.work.Work;

public class RunnableWork extends AbstractWork implements Work {

	private Runnable runnable;

	public RunnableWork(Runnable command) {
		this.runnable = command;
	}

	public void run() {
		runnable.run();
	}

}
