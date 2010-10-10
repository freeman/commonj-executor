/* WorkManager for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.work;

/**
 * This is sent to a WorkListener as the Work is processed by a WorkManager.
 * @since 1.0
 * @version 1.1
 */

public interface WorkEvent 
{
	/**
	 * Work has been accepted
     * @since 1.0
	 */
	int WORK_ACCEPTED = 1;
	/**
	 * Work has been rejected.
     * @since 1.0
	 */
	int WORK_REJECTED = 2;
	/**
	 * Work is currently running.
     * @since 1.0
	 */
	int WORK_STARTED = 3;
	/**
	 * Work has completed.
     * @since 1.0
	 */
	int WORK_COMPLETED = 4;

	/**
	 * This returns WORK_ACCEPTED etc
     * @return one of the event constants that indicate the type of event this WorkEvent represents.
     * @since 1.0
	 */
	public int getType();

	/**
	 * This returns a WorkItem that represents the 
     * Work submitted to a WorkManager.  This WorkItem
     * may not be the same WorkItem reference that was returned from the 
     * WorkManager.schedule() methods.
     * @return the WorkItem that this WorkEvent is for.
     * @since 1.1
	 */
	public WorkItem getWorkItem();

	/**
	 * This returns the exception if any in the case of WORK_COMPLETED.
     * @return the WorkException thrown if a Work completed with exception.  Null if there is no exception.
     * @since 1.0
	 */
	public WorkException getException();
}

