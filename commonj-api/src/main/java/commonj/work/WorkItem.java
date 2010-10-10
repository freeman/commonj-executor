/* WorkManager for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.work;

/**
 * This is returned once a Work is submitted to a WorkManager.
 * It can be used to check the status of Work after it's finished and to
 * check any exceptions that it threw. If the Work was serializable and
 * the vendor implementation supports distributed WorkManagers then this
 * object can be cast to RemoteWorkItem where additional APIs are defined
 * to exploit this capability. WorkItem instances can also be used with the
 * {@link WorkManager#waitForAny} or {@link WorkManager#waitForAll} to wait
 * for remote or local WorkItems to complete.<p>
 * 
 * Applications can use the WorkItem as a key to correlate the WorkItem back 
 * to the running Work object.  The WorkItem should implement the equals, compareTo 
 * and hashCode methods and is therefore Collection-friendly.
 * 
 * @see WorkManager
 * @see RemoteWorkItem
 * @see WorkListener
 * @since 1.0
 * @version 1.1
 */
public interface WorkItem extends Comparable
{
    /**
     * This returns the Work once it has completed.
     * If the Work threw an exception during run then the exception
     * is rethrown here.
     * @return the completed Work or null if the Work is not yet complete.
     * @since 1.1
     */  
    Work getResult()
        throws WorkException;

	/**
	 * This returns the current status of dispatching the Work. See WorkEvent for the values.
	 * @see WorkEvent
     * @return one of the events specified on the {@link WorkEvent} interface.
     * @since 1.0
	 */
	int getStatus();
};
