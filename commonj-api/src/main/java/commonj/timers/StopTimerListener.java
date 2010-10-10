/* Timer for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.timers;

/**
 * Applications requiring timer stop events should have their timers
 * implement this interface.<p>
 * 
 * The J2EE context active on the thread during TimerListener scheduling 
 * will be applied to the timerStop method.  See {@link TimerManager} for details.
 * @since 1.0
 * @version 1.1
 */
public interface StopTimerListener extends TimerListener 
{
	/**
	 * This is called if the timer is stopped. This happens if the
	 * TimerManager is stopped using the {@link TimerManager#stop()} method or application is stopped.
	 * @param timer the Timer that was stopped.
     * @since 1.0
	 */
	void timerStop(Timer timer);
}
