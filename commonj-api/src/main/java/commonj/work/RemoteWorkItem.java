/* WorkManager for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.work;

/**
 * If a Work instance is serializable and the WorkManager implementation supports Remoteable
 * WorkManager then the Work can be sent to a remote member of the application cluster for execution. 
 * This allows serializable Works to be executed on any member of the J2EE cluster containing the application. Subsequent scheduleWork
 * calls will result in the Works being load balanced over the available members in the cluster using a vendor specific algorithm. <p>
 * Clearly, if this is the case then the Work object supplied to scheduleWork cannot be used to interrogate the result when the
 * Work completes as it was a copy that was executed on the remote JVM. The Work instance is copied and marshalled to the remote JVM 
 * where it is executed and then a copy
 * of the Work instance is send back to the JVM that called scheduleWork. This returned copy is then available using the
 * {@link WorkItem#getResult()} method.<p>
 * 
 * If the application wants to send multiple Work objects to the same remote JVM then it 
 * should schedule Work on a resource-ref obtained WorkManager and then
 * schedule subsequent Work objects using the WorkManager obtained from the 
 * {@link RemoteWorkItem#getPinnedWorkManager} method. This is called a pinned WorkManager.
 * This provides affinity to the same remote JVM to be maintained. 
 * If the remote JVM fails then subsequent calls to scheduleWork on the pinned
 * WorkManager will fail with a {@link WorkRejectedException} once the failure has been detected.<p>
 * 
 * If a remote JVM containing pending Works fails then these Works are marked as complete with a status of rejected. 
 * @since 1.0
 * @version 1.1
 */
public interface RemoteWorkItem extends WorkItem 
{

	/**
	 * Calls the remote Work object's {@link Work#release()} method.  
     * It instructs the Work associated with this RemoteWorkItem to 'stop' voluntarily. If
	 * the work has already stopped then this has no effect. The {@link WorkItem#getStatus()} can be
	 * used to determine whether it has stopped or not but the Work can always stop
	 * just after getStatus returns 'still working'.
	 * The best way to wait for such Works to release is using the 
     * {@link WorkManager#waitForAll(java.util.Collection, long)} API.
     * @since 1.0
	 */
	void release();


	/**
	 * This returns a pinned WorkManager which represents the JVM that was used to execute this Work. This allows subsequent
	 * remote Works to be sent to the same JVM as the one that was used to execute this
	 * WorkItem. If the remote JVM fails then subsequent scheduleWorks on this
	 * WorkManager will fail with a rejected exception even if the remote JVM restarts. The pinned WorkManager is associated with
	 * the JVM instance that was running as opposed to any future JVM instance.
	 * @return the WorkManager associated with the JVM that was used to execute this RemoteWorkItem
     * @since 1.0
	 */
	WorkManager getPinnedWorkManager();	
}
