/* WorkManager for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.work;

/**
 * This is implemented by applications when they want to run
 * code blocks asynchronously. If an application supplied Work implements
 * java.io.Serializable then this tells the runtime that the application will
 * not interact with the Work directly while it is running. The runtime may decide
 * in this case to dispatch the Work in a seperate JVM that also hosts this application. Obviously, if this happens
 * then the Work will be serialized to the remote JVM, executed and the results 
 * propagated back. The WorkItem API will work correctly in this case. However,
 * the application cannot in this case interrogate the local Work supplied to scheduleWork
 * for results as it wasn't executed, a copy would have been executed remotely. The
 * {@link RemoteWorkItem#getResult} method will return the copy in this case.<p>
 * If the Work does not implement Serializable then it is guaranteed to execute in
 * the same JVM as the thread invoking WorkManager.startWork.<p>
 * If the server is quiescing then the runtime may call the release method on the
 * Work once it starts executing to tell it to exit. If it doesn't respond to this
 * 'signal' then it may be terminated by the runtime without warning.<p>
 * 
 * The J2EE context active on the thread during WorkManager scheduling 
 * will be applied to the <code>run</code> method.  See {@link WorkManager} for details.
 * @see WorkManager
 * @see WorkItem
 * @see RemoteWorkItem
 * @since 1.0
 * @version 1.1
 */
public interface Work extends Runnable
{
	/**
	 * The implementor should return from the Runnable.run method
	 * once this is called.<p>
	 * This method can be called by the runtime when the JVM is
	 * shutting down. This method is not called using the Work creator J2EE context
	 * and hence it cannot access any JNDI resources reliably. It should simply
	 * set any variables used to terminate the main loop in run and return. This method is also
	 * called on a different thread than the one used for the run method so the application
	 * should use inter-thread communication techniques (i.e. synchronized etc).
     * @since 1.0
	 */
	void release();

	/**
	 * This should return true if the work is long lived (Daemon) versus short-lived.<p>
     * 
     * Non-daemon Works are allocated from a thread pool and normally 
     * should not last longer than the submitting container method.
     * The submitting method should wait until the short-lived (non-daemon) work 
     * is complete if using resources that are only valid during the method's duration.<p>
     * 
     * Daemon Works are not allocated from a thread pool and will automatically be 
     * released when the submitting application ends. 
	 * @return true if daemon (long lived)
     * @since 1.0
	 */	
	boolean isDaemon();
}

