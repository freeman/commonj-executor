/* Timer for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.timers;

/**
 * A Timer is returned when a TimerListener
 * is scheduled using one of the <code>TimerManager.schedule</code> methods.
 * It allows retrieving information about the scheduled TimerListener and
 * allows cancelling it.<p>
 * 
 * Timers are transient and not transactional and run inside the
 * JVM which created them. If a cluster-wide scheduled event is required
 * or it needs to be persistent, transactional or recoverable then a service such
 * as the EJB 2.1 Timer service should be used instead.
 * @see Timer
 * @see TimerListener
 * @since 1.0
 * @version 1.1
 */
public interface Timer
{
	/**
	 * This cancels the timer and all future TimerListener invocations and
     * may be called during the <code>TimerListener.timerExpired</code> 
     * method.<p>
     * 
     * <code>CancelTimerListener.timerCancel</code> events may be 
     * called concurrently with any <code>TimerListener.timerExpired</code>
     * methods.  Proper thread synchronization techiniques must be employed to 
     * ensure consistency.
     * 
	 * Once a Timer is cancelled an application <b>must</b> not use
	 * the Timer instance again.<p>
     * 
	 * @return true if this prevented the next execution 
     * of this timer. False if this was already cancelled 
     * or had already expired in the one shot case.
     * @since 1.0
     * @see CancelTimerListener
	 */
	boolean cancel();
	
	/**
	 * Returns the application-supplied TimerListener associated
	 * with this Timer.
	 * @return The TimerListener associated with the timer.
     * @throws IllegalStateException if the TimerManager has been stopped.
     * @since 1.0
	 */
	TimerListener getTimerListener();
	
	/**
     * Returns the next absolute <i>scheduled</i> execution time in milliseconds.<p>
     * 
     * If invoked while a TimerListener is running,  
     * the return value is the <i>scheduled</i> execution time of 
     * the current TimerListener execution.<p>
     * 
     * This method is typically invoked from within a TimerListener's timerExpired
     * method, to determine whether the current execution of the task is sufficiently
     * timely to warrant performing the scheduled activity:
     *  <pre>
     *    public void timerExpired(Timer t) {
     *        if (System.currentTimeMillis() - t.scheduledExecutionTime() >=
     *            MAX_TARDINESS)
     *                return;  // Too late; skip this execution.
     *        // Perform the task
     *    }
     *  </pre>
     * 
     * If the timer has been suspended, the time reflects the most recently-calculated
     * execution time prior to being suspended.
     * 
	 * @return the time in milliseconds at which the TimerListener is scheduled to run next.
     * @throws IllegalStateException if the TimerManager has been stopped.
     * @since 1.1
	 */
	long getScheduledExecutionTime()
        throws IllegalStateException;
    
	/**
	 * Return the period used to compute the time this timer will repeat. 
	 * A value of zero indicates that the timer is non-repeating. 
	 * @return the period in milliseconds between timer executions.
     * @since 1.0
	 */
	long getPeriod();
}

