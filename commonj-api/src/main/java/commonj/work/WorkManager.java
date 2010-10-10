/* WorkManager for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.work;

import java.util.Collection;

/**
 * The WorkManager is the abstraction for dispatching and monitoring asynchronous
 * work and is a factory for creating application short or long lived Works.
 * <p> 
 * WorkManagers are created by the server administrator.  
 * The vendor specific systems management console allows the administrator to create one or more WorkManagers and associate
 * a JNDI name with each one. The administrator may specify implementation specific information such as
 * min/max Works for each WorkManager. An application that requires a WorkManager
 * should declare a resource-ref in the EJB or webapp that needs the WorkManager. The vendor descriptor editor or
 * J2EE IDE can be used to bind this resource-ref to a physical WorkManager at
 * deploy or development time. An EJB or servlet can then get a reference to
 * a WorkManager by looking up the resource-ref name in JNDI and then casting it. For example, if the resource-ref was called wm/WorkManager:
 * <pre>
 * &lt;resource-ref&gt;
 *   &lt;res-ref-name&gt;wm/WorkManager&lt;/res-ref-name&gt;
 *   &lt;res-type&gt;commonj.work.WorkManager&lt;/res-type&gt;
 *   &lt;res-auth&gt;Container&lt;/res-auth&gt;
 *   &lt;res-sharing-scope&gt;Shareable&lt;/res-sharing-scope&gt;
 * &lt;/resource-ref&gt;
 * </pre>
 * The Java code to look this up would look like:
 * <pre>
 *   InitialContext ic = new InitialContext();
 *   WorkManager wm = (WorkManager)ic.lookup("java:comp/env/wm/WorkManager");
 * </pre>
 * The res-auth and res-sharing scopes are ignored in this version of the specification.
 * The EJB or servlet can then use the WorkManager as it needs to.<p>
 * 
 * When a Work is scheduled, the declared context that is present on the thread 
 * (the J2EE context) will be saved and propagated to the asynchronous methods that 
 * are executed.  This J2EE context at minimum will contain the java:comp namespace 
 * and ClassLoader of the scheduler unless specified otherwise.  Other J2EE contexts 
 * such as security or a transactional context may be optionally added by the application 
 * server vendor.  Global transactions are always available using the 
 * java:comp/UserTransaction JNDI name and are used in the same fashion as they are used 
 * in servlets and bean-managed transaction Enterprise Java Beans.<p>
 * 
 * A WorkManager can also be a pinned WorkManager. This is a WorkManager obtained using the RemoteWorkItem.getWorkManager
 * method. This allows subsequent scheduleWorks to be send to the same remote WorkManager as the one that is associated
 * with the RemoteWorkItem. Pinned WorkManagers are only supported on vendor implementations that support remote Works. However,
 * applications that follow the programming model will work on all implementations however serializable Works will be
 * executed within the local JVM only on those implementations.<p>
 * 
 * If the scheduled <code>Work</code> is a daemon Work, then the life-cycle of that Work is tied to 
 * the application that scheduled it.  If the application is stopped, the <code>Work.release()</code> method
 * will be called.<p>
 *  
 * @see commonj.work.Work
 * @see commonj.work.RemoteWorkItem
 * @since 1.0
 * @version 1.1
 */
public interface WorkManager
{
	/**
	 * This constant can be used as a timeout for the waitForXXX methods. 
     * It will check the current state of the supplied Work objects and 
     * return immediately with the result.
     * @since 1.0
	 */
	long IMMEDIATE = 0; // immediate action
    
	/**
	 * This constant can be used as a timeout for either the waitForXXX methods. 
	 * It will cause the waitForXXX to block indefinitely until the the
     * wait is satisfied.
     * @since 1.0
	 */
	long INDEFINITE = Long.MAX_VALUE; // no time constraint
	
	/**
	 * Dispatches a Work asynchronously.
	 * The work is dispatched and the method returns immediately. The
	 * J2EE context of the caller is used to execute the Work.<p>
     * 
	 * At-most-once semantics are provided. If the server fails then the
	 * Work will not be executed on restart.<p>
	 * If this WorkManager is a pinned one, i.e. one obtained using {@link RemoteWorkItem#getPinnedWorkManager() } and that JVM that it
	 * represents has failed then a {@link WorkRejectedException } will be thrown even if the remote JVM restarts. The pinned WorkManager
	 * must be refreshed by using a normal WorkManager and then acquiring a new pinned WorkManager.
	 * @param work the Work to execute.
	 * @return the workitem representing the asynchronous work. If the Work is serializable then a RemoteWorkItem is always returned.
	 * @exception If queuing this up results in an exception then a
	 * WorkException is thrown.
     * @throws IllegalArgumentException thrown if work is a javax.ejb.EnterpriseBean.
     * @since 1.0
	 */
	WorkItem schedule(Work work)
		throws WorkException, IllegalArgumentException;

	/**
     * Dispatches a Work asynchronously.
     * The work is dispatched and the method returns immediately. The
     * J2EE context of the caller is used to execute the Work.<p>
     * 
     * At-most-once semantics are provided. If the server fails then the
     * Work will not be executed on restart.<p>
     * 
	 * The WorkListener methods are called using the 
	 * J2EE context of the caller as the Work progresses through processing. <p>
	 * If this WorkManager is a pinned one, i.e. one obtained using {@link RemoteWorkItem#getPinnedWorkManager() } and that JVM that it
	 * represents has failed then a {@link WorkRejectedException } will be thrown even if the remote JVM restarts. The pinned WorkManager
	 * must be refreshed by using a normal WorkManager and then acquiring a new pinned WorkManager.
     * 
	 * @param work the Work to execute.
	 * @param wl can be null or a WorkListener which is used to inform
	 * the application of the progress of a Work.
	 * @return The workitem representing the asynchronous work. If the Work is serializable then a RemoteWorkItem is always returned.
	 * @throws WorkException If queuing this up results in an exception then a
	 * WorkException is thrown.
	 * @throws IllegalArgumentException thrown if work is a javax.ejb.EnterpriseBean.
     * @since 1.0
	 */
	WorkItem schedule(Work work, WorkListener wl)
		throws WorkException, IllegalArgumentException;

	/**
	 * Wait for all WorkItems in the collection to finish successfully or otherwise. 
	 * WorkItems from different WorkManagers can be placed in a single collection and waited
	 * on together.<p>
     * The WorkItems collection should not be altered once submitted until the method returns.
     * @param workItems the Collection of WorkItem objects to wait for.
	 * @param timeout_ms the timout in milliseconds. If this is 0 then this method returns immediately.
	 * @return true if all WorkItems have completed, false if the timeout has expired.
     * @throws InterruptedException thrown if the wait is interrupted.
     * @throws IllegalArgumentException thrown if workItems is null, any of the objects in the collection are 
     * not WorkItems or the timeout_ms is negative.
     * @since 1.0
	 */
	boolean waitForAll(Collection workItems, long timeout_ms)
        throws InterruptedException, IllegalArgumentException;

	/**
	 * Wait for any of the WorkItems in the collection to finish.
	 * If there are no WorkItems in the list then it returns immediately indicating
	 * a timeout.
	 * WorkItems from different WorkManagers can be placed in a single collection and waited
	 * on together.<p>
     * The WorkItems collection should not be altered once submitted until the method returns.
     * @param workItems the Collection of WorkItem objects to wait for.
	 * @param timeout_ms the timeout in ms. If this is 0 then the method returns immediately, i.e. does not block.
	 * @return the WorkItems that have completed or an empty Collection if it time out expires before any finished.
     * @throws InterruptedException thrown if the wait is interrupted.
     * @throws IllegalArgumentException thrown if workItems is null, any of the objects in the collection are 
     * not WorkItems or the timeout_ms is negative.
     * @since 1.0
	 */
	Collection waitForAny(Collection workItems, long timeout_ms)
        throws InterruptedException, IllegalArgumentException;
}

