/* WorkManager for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.work;

import java.util.EventListener;

/**
 * This is a callback interface called to report on the dispatching of a Work.
 * It is implemented by the application and supplied to scheduleWork
 * methods on a WorkManager. 
 * 
 * The J2EE context active on the thread during WorkManager scheduling 
 * will be applied each of the event methods.  See {@link WorkManager} for details.
 * 
 * @see WorkManager
 * @since 1.0
 * @version 1.1
 */
public interface WorkListener extends EventListener 
{
	/**
	 * This is called when the Work is accepted for dispatching. 
     * @since 1.0
	 */
	void workAccepted(WorkEvent we);
    
	/**
	 * This is called when the Work cannot be processed prior to starting but after accept.
     * @since 1.0
	 */
	void workRejected(WorkEvent we);
    
	/**
	 * This is called when the Work is about to start. When this returns the Work.run is called.
     * @since 1.0
	 */
	void workStarted(WorkEvent we);
    
	/**
	 * This is called once Work.run returns.
     * @since 1.0
	 */
	void workCompleted(WorkEvent we);
}

