package executor.commonj;

import commonj.work.Work;

public abstract class AbstractWork implements Work {

	public AbstractWork() {
		super();
	}

	public void release() {
	}

	public boolean isDaemon() {
		return false;
	}

}