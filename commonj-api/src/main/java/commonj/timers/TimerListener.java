/* Timer for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.timers;

/**
 * Objects that implement this interface and are supplied
 * to one of the <code>TimerManager#schedule</code> methods can receieve timer
 * notifications. When the timer expires, the {@link #timerExpired(commonj.timers.Timer)} method is called and the 
 * Timer object that has expired is provided.<p>
 * 
 * When the timer expires, the {@link #timerExpired(commonj.timers.Timer)} method is invoked.  
 * The Timer object passed in is the Timer that was returned when the TimerListener 
 * was originally scheduled.  If the TimerListener was scheduled with a period, 
 * then the same Timer object is passed to the TimerListener on each invocation.<p>
 * 
 * The J2EE context active on the thread during TimerListener scheduling 
 * will be applied to the timerExpired method.  See {@link TimerManager} for details.
 * @see Timer
 * @see TimerManager
 * @since 1.0
 * @version 1.1
 */
public interface TimerListener
{
	/**
     * This method is called when a Timer expires and will run with the J2EE context of the scheduler.
	 * @param timer the Timer object that expired.
     * @since 1.0
	 */
	void timerExpired(Timer timer);
}

