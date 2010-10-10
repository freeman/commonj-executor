/* WorkManager for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.work;

/**
 * This is the base class for all Work related exceptions.
 * @since 1.0
 * @version 1.1
 */
public class WorkException extends Exception
{

    /**
     * Constructor for WorkException
     * @since 1.0
     */
    public WorkException()
    {
        super();
    }

    /**
     * Constructor for WorkException
     * @param message
     * @since 1.0
     */
    public WorkException(String message)
    {
        super(message);
    }

    /**
     * Constructor for WorkException
     * @param message
     * @param cause
     * @since 1.0
     */
    public WorkException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructor for WorkException
     * @param cause
     * @since 1.0
     */
    public WorkException(Throwable cause)
    {
        super(cause);
    }

}

