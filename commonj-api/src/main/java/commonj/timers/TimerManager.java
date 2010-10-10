/* Timer for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.timers;

import java.util.Date;

/**
 * Applications use a TimerManager to schedule TimerListeners.   
 * Each of the TimerManager schedule methods returns a Timer object.  
 * The returned Timer can then be queried and/or cancelled.  Applications 
 * are required to implement the TimerListener interface and may optionally 
 * implement one or both of the CancelTimerListener and StopTimerListener interfaces.
 *  
 * All Timers execute in the same JVM as the thread that created
 * the Timer and are transient.  If the JVM fails then all Timers will be lost.<p>
 * 
 * TimerManagers are bound to a J2EE component using a resource-reference, similar
 * to a WorkManager or DataSource. Each lookup returns a new logical TimerManager who's
 * lifecycle can be managed independently of all other TimerManagers. Components should 
 * cache the TimerManager rather than look it up every time.<p>
 * 
 * All TimerManagers are bound to the lifecycle of the application that looks it up.
 * When the application state changes, all TimerManagers associated with that
 * application will be stopped, suspended or resumed, depending on the capabilities
 * of the application server. 
 * 
 * TimerManagers are configured by the server administrator.  
 * The vendor specific systems management console allows the administrator to create one or more TimerManagers and associate
 * a JNDI name with each one. The administrator may specify implementation specific information such as
 * the maximum number of concurrent timers or thread priorities for each TimerManager. An application that requires a TimerManager
 * should declare a resource-ref in the EJB or webapp that needs the TimerManager. The vendor descriptor editor or
 * J2EE IDE can be used to bind this resource-ref to a physical TimerManager at
 * deploy or development time. An EJB or servlet can then get a reference to
 * a TimerManager by looking up the resource-ref name in JNDI and then casting it. For example, if the resource-ref was called tm/TimerManager:
 * <pre>
 * &lt;resource-ref&gt;
 *   &lt;res-ref-name&gt;tm/TimerManager&lt;/res-ref-name&gt;
 *   &lt;res-type&gt;commonj.timers.TimerManager&lt;/res-type&gt;
 *   &lt;res-auth&gt;Container&lt;/res-auth&gt;
 *   &lt;res-sharing-scope&gt;Unshareable&lt;/res-sharing-scope&gt;
 * &lt;/resource-ref&gt;
 * </pre>
 * The res-auth is ignored in this version of the specification. The sharing scope must be set to unshareable. If this is specified as shareable then a vendor specific action will occur. The Java code to look this up would look like:
 * <pre>
 *   InitialContext ic = new InitialContext();
 *   TimerManager tm = (TimerManager)ic.lookup("java:comp/env/tm/TimerManager");
 * </pre>
 * The EJB or servlet can then use the TimerManager as it needs to.<p>

 * Recurring timers will execute their TimerListener multiple times.  
 * Invocations of the TimerListener are executed serially.  That is, 
 * if a timer is scheduled to repeat every 5 seconds and a previous 
 * execution is still running, then the subsequent execution is delayed 
 * until the currently running execution completes.<p>
 * 
 * A TimerListener scheduled multiple times, resulting in the return of multiple Timer instances, 
 * may execute concurrently depending on implementation. Proper thread safety techniques 
 * need to be employed.<p>
 * 
 * When a TimerListener is scheduled, the declared context that is present on the thread 
 * (the J2EE context) will be saved and propagated to the asynchronous methods that are 
 * executed.  This J2EE context at minimum will contain the java:comp namespace and 
 * ClassLoader of the scheduler unless specified otherwise.  Other J2EE contexts such as 
 * security or a transactional context may be optionally added by the application server vendor.  
 * Global transactions are always available using the java:comp/UserTransaction JNDI 
 * name and are used in the same fashion as they are used in servlets and bean-managed 
 * transaction Enterprise Java Beans.<p> 
 * 
 * Two types of repeating timers are available:
 * <ul>
 * <li><strong>fixed-rate</strong> - 
 * TimerListeners are executed at a constant rate based on the 
 * initial scheduled execution time.  Each subsequent execution time is based off of the period
 * and the first execution time.  This is appropriate for recurring 
 * activities that are sensitive to absolute time, such as ringing a chime
 * every hour on the hour.  It is also appropriate for recurring activities 
 * where the total time to perform a fixed number of executions is important, such
 * as a countdown timer that ticks once every second for ten seconds.<br><br>
 * 
 * If a fixed-rate execution is delayed for any reason (such as garbage collection, 
 * suspension, or other background activity), 
 * the late TimerListener will execute one time immediately, and then again at the next 
 * scheduled fixed-rate time.<br><br>
 *
 * For example, if a fixed-rate timer was to execute every hour on the hour 
 * and missed 5 executions because the TimerManager was suspended for over 
 * 5 hours,  when the TimerManager is resumed, the TimerListener will 
 * run once immedately, and each execution thereafter will be on the hour.<p>
 *  
 * <li><strong>fixed-delay</strong> - 
 * TimerListeners are executed at a constant delay based on the
 * actual execution time.  Each subsequent execution time is based off of the period 
 * and the end time of the previous execution.  The period is the delay in between 
 * TimerListener executions.<br><br>
 * 
 * If a fixed-delay execution is delayed for any reason (such as garbage collection, 
 * suspension, or other background activity), subsequent executions will be delayed as well. In the 
 * long run, the frequency of execution will generally be slightly lower than the 
 * reciprocal of the specified period (assuming the system clock underlying 
 * Object.wait(long) is accurate). Fixed-delay execution is appropriate for 
 * recurring activities that require &quot;smoothness&quot;. In other words, it is appropriate for 
 * activities where it is more important to keep the frequency accurate in the short run 
 * than in the long run. This includes most animation tasks, such as blinking a cursor 
 * at regular intervals. It also includes tasks wherein regular activity is performed 
 * in response to human input, such as automatically repeating a character as long as 
 * a key is held down.<p>
 *
 * For example, if a fixed-delay timer was to execute every hour on the hour 
 * and missed 5 executions because the TimerManager was suspended for over 
 * 5 hours,  when the TimerManager is resumed, the TimerListener would execute
 * immediately and the next execution would occur one hour after the completion 
 * of the TimerListener.
 * </ul>
 * 
 * @since 1.0
 * @version 1.1
 */
public interface TimerManager
{	

    /**
     * This constant can be used as a timeout for the waitForXXX methods. 
     * It will check the current state of the TimerManager and 
     * return immediately with the result.
     * @since 1.1
     */
    long IMMEDIATE = 0; // immediate action
    
    /**
     * This constant can be used as a timeout for either the waitForXXX methods. 
     * It will cause the waitForXXX methods to block indefinitely until the the
     * wait is satisfied.
     * @since 1.1
     */
    long INDEFINITE = Long.MAX_VALUE; // no time constraint

	/**
	 * Suspend the TimerManager.<p>
     * 
     * Execution of listeners for timers that expire while the TimerManager 
     * is suspended is deferred until the TimerManager is resumed.<p>
     * 
     * This method will return immediately.  Use the 
     * {@link TimerManager#isSuspended()} and {@link TimerManager#waitForSuspend(long)}
     * methods to determine the state of the suspend.
     * 
	 * @throws IllegalStateException if this TimerManager has been stopped.
     * @since 1.0
	 */
	void suspend();
    
    /**
     * Returns <tt>true</tt> if this TimerManager is in the process
     * of or has completed suspending.
     *
     * @return <tt>true</tt> if this TimerManager has been suspended
     * @throws IllegalStateException if this TimerManager has been stopped.
     * @since 1.1
     */
    boolean isSuspending() throws IllegalStateException;
    
    /**
     * Returns <tt>true</tt> if all TimerListeners have completed following suspend.
     *
     * @return <tt>true</tt> if all TimerListeners have completed following suspend
     * @throws IllegalStateException if this TimerManager has been stopped.
     * @since 1.1
     */
    boolean isSuspended() throws IllegalStateException;
    
    /**
     * Blocks until all TimerListeners have completed execution after a suspend
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     *
     * @param timeout_ms the maximum time to wait
     * @return <tt>true</tt> if this TimerManager suspended and <tt>false</tt>
     * if the timeout elapsed before the suspend completed
     * @throws InterruptedException if interrupted while waiting
     * @throws IllegalStateException if this TimerManager has been stopped.
     * @throws IllegalArgumentException thrown if the timeout_ms is negative.
     * @since 1.1
     */
    boolean waitForSuspend(long timeout_ms) throws InterruptedException, IllegalStateException, IllegalArgumentException;
	
	/**
	 * Resume the TimerManager. All scheduled timers that
	 * have expired while suspended will execute immediately.  All fixed-rate
     * timers will execute one time, and then again at the next 
     * scheduled fixed-rate time.<p>
     * 
     * @throws IllegalStateException if this TimerManager has been stopped.
     * @since 1.0
	 */
	void resume() throws IllegalStateException;
	
	/**
	 * Destroy the TimerManager. All Timers are stopped and all 
     * currently executing listeners are permitted to complete.<p>
     *   
     * <code>StopTimerListener.stopTimer</code> events will be 
     * called serially with any <code>TimerListener.timerExpired</code>
     * methods.  They will not execute concurrently.  If a TimerListener
     * is executing, the <code>stopTimer</code> method will be called after the 
     * <code>timerExpired</code> method has completed.
     * 
     * <code>CancelTimerListener.timerCancel</code> events do not execute
     * as a result of this method<p>
     *  
	 * Once the a timer manager has been stopped, it can never be re-started.
	 * Attempts to schedule a TimerListener, and resume or suspend the TimerManager
	 * will result in a thrown IllegalStateException.
     * 
	 * @throws IllegalStateException If this TimerManager was already stopped.
     * @since 1.0
	 */	
	void stop() throws IllegalStateException;

    /**
     * Returns <tt>true</tt> if all TimerListeners have completed following stop.
     *
     * @return <tt>true</tt> if all TimerListeners have completed following stop
     * @since 1.1
     */
    boolean isStopped();
    
    /**
     * Returns <tt>true</tt> if this TimerManager is stopping or has been stopped.
     *
     * @return <tt>true</tt> if this TimerManager is stopping or has been stopped
     * @since 1.1
     */
    boolean isStopping();
    
    /**
     * Blocks until all TimerListeners have completed execution after a stop
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     *
     * @param timeout_ms the maximum time to wait
     * @return <tt>true</tt> if this TimerManager stopped and <tt>false</tt>
     * if the timeout elapsed before the stop completed
     * @throws InterruptedException if interrupted while waiting
     * @throws IllegalArgumentException thrown if the timeout_ms is negative.
     * @since 1.1
     */
    boolean waitForStop(long timeout_ms) throws InterruptedException, IllegalArgumentException;
    	
	/**
	 * Schedules a TimerListener to execute at a specified time. 
	 * If the time is in the past, the TimerListener will execute immediately.
     *  
	 * @param listener the TimerListener implementation to invoke when the timer expires.
	 * @param time the time at which the timer will expire.
	 * @return the resulting Timer object.
     * @throws IllegalArgumentException thrown when the listener or firstTime is null
     * @throws IllegalStateException thrown when the TimerManager has been stopped.
     * @since 1.0
	 */
	Timer schedule(TimerListener listener, Date time) 
        throws IllegalArgumentException, IllegalStateException;

    /**
     * Schedules a TimerListener to execute once after the specified delay.<p>
     * 
     * @param listener the TimerListener implementation to invoke when the Timer expires.
     * @param delay the number of milliseconds to wait before the Timer expires.
     * @return  the resulting Timer object.
     * @throws IllegalArgumentException thrown when the listener is null or the delay is negative.
     * @throws IllegalStateException thrown when the TimerManager has been stopped.
     * @since 1.0
     */
	Timer schedule(TimerListener listener, long delay) 
        throws IllegalArgumentException, IllegalStateException;

    /**
     * Schedules a TimerListener to execute repeatedly using <strong>fixed-delay</strong> 
     * execution after the <code>firstTime</code> elapses.<p>
     * 
     * TimerListeners are executed at a constant delay based on the
     * actual execution time.  Each subsequent execution time is based off of the period 
     * and the end time of the previous execution.  The period is the delay in between 
     * TimerListener executions.<p>
     * 
     * @param listener the TimerListener implementation to invoke when the Timer expires.
     * @param firstTime the time at which the first TimerListener will execute.
     * @param period the number of milliseconds between repeating expiration intervals.
     * @return  the resulting Timer object.
     * @throws IllegalArgumentException thrown when the listener or firstTime is null or the period is negative.
     * @throws IllegalStateException thrown when the TimerManager has been stopped.
     * @since 1.0
     */
    Timer schedule(TimerListener listener, Date firstTime, long period) 
        throws IllegalArgumentException, IllegalStateException;
    
    /**
     * Schedules a TimerListener to execute repeatedly using <strong>fixed-delay</strong> 
     * execution after the specified delay.<p>
     * 
     * TimerListeners are executed at a constant delay based on the
     * actual execution time.  Each subsequent execution time is based off of the period 
     * and the end time of the previous execution.  The period is the delay in between 
     * TimerListener executions.<p>
     * 
     * @param listener the TimerListener implementation to invoke when the Timer expires.
     * @param delay the number of milliseconds to wait before the first Timer expires.
     * @param period the number of milliseconds between repeating expiration intervals.
     * @return  the resulting Timer object.
     * @throws IllegalArgumentException thrown when the listener is null or the period or delay is negative.
     * @throws IllegalStateException thrown when the TimerManager has been stopped.
     * @since 1.0
     */
	Timer schedule(TimerListener listener, long delay, long period)
        throws IllegalArgumentException, IllegalStateException;

    /**
     * Schedules a TimerListener to execute repeatedly using <strong>fixed-rate</strong> 
     * execution after the <code>firstTime</code> elapses.<p>
     * 
     * TimerListeners are executed at a constant rate based on the 
     * initial scheduled execution time.  Each subsequent execution time is based off of the period
     * and the first execution time.  This is appropriate for recurring 
     * activities that are sensitive to absolute time, such as ringing a chime
     * every hour on the hour.  It is also appropriate for recurring activities 
     * where the total time to perform a fixed number of executions is important, such
     * as a countdown timer that ticks once every second for ten seconds.
     * 
     * @param listener the TimerListener implementation to invoke when the Timer expires.
     * @param firstTime the time at which the first timer will execute.
     * @param period the number of milliseconds between repeating expiration intervals.
     * @return  the resulting Timer object.
     * @throws IllegalArgumentException thrown when the listener or firstTime is null or the period is negative.
     * @throws IllegalStateException thrown when the TimerManager has been stopped.
     * @since 1.0
     */
	Timer scheduleAtFixedRate(TimerListener listener, Date firstTime, long period) 
        throws IllegalArgumentException, IllegalStateException;
        
    /**
     * Schedules a TimerListener to execute repeatedly using <strong>fixed-rate</strong> 
     * execution after the specified delay.<p>
     * 
     * TimerListeners are executed at a constant rate based on the 
     * initial scheduled execution time.  Each subsequent execution time is based off of the period
     * and the first execution time.  This is appropriate for recurring 
     * activities that are sensitive to absolute time, such as ringing a chime
     * every hour on the hour.  It is also appropriate for recurring activities 
     * where the total time to perform a fixed number of executions is important, such
     * as a countdown timer that ticks once every second for ten seconds.
     * 
     * 
     * @param listener the TimerListener implementation to invoke when the Timer expires.
     * @param delay the number of milliseconds to wait before the first Timer expires.
     * @param period the number of milliseconds between repeating expiration intervals.
     * @return  the resulting Timer object.
     * @throws IllegalArgumentException thrown when the listener is null or the period or delay is negative.
     * @throws IllegalStateException thrown when the TimerManager has been stopped.
     * @since 1.0
     */
	Timer scheduleAtFixedRate(TimerListener listener, long delay, long period) 
        throws IllegalArgumentException, IllegalStateException;
}

