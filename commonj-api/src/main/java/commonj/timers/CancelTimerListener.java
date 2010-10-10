/* Timer for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.timers;

/**
 * This is implemented by developers that require a cancel event.<p>
 * 
 * The J2EE context active on the thread during TimerListener scheduling 
 * will be applied to the timerCancel method.  See {@link TimerManager} for details.
 * @since 1.0
 * @version 1.1
 */
public interface CancelTimerListener extends TimerListener 
{
	/**
	 * This is called when timer is cancelled using the {@link Timer#cancel()} method.
	 * @param timer the Timer object that was cancelled.
     * @since 1.0
	 */
	void timerCancel(Timer timer);
}
