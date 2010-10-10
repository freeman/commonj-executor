/* WorkManager for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.work;

/**
 * This is thrown then the Work cannot be started. This can happen
 * security contexts are invalid when the Work is started or
 * when using remote Work objects, the remote JVM hosting the WorkManager has failed.
 * @since 1.0
 * @version 1.1
 */
public class WorkRejectedException extends WorkException 
{

    /**
     * Constructor for a WorkRejectedException
     * @since 1.0
     */
    public WorkRejectedException()
    {
        super();
    }

    /**
     * Constructor for a WorkRejectedException
     * @param message
     * @since 1.0
     */
    public WorkRejectedException(String message)
    {
        super(message);
    }

    /**
     * Constructor for a WorkRejectedException
     * @param message
     * @param cause
     * @since 1.0
     */
    public WorkRejectedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructor for a WorkRejectedException
     * @param cause
     * @since 1.0
     */
    public WorkRejectedException(Throwable cause)
    {
        super(cause);
    }

}

