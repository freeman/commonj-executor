/* WorkManager for Application Servers
 * Version 1.1
 * Licensed Materials - Property of BEA and IBM
 *
 * © Copyright BEA Systems, Inc. and International Business Machines Corp 2003-2004. All rights reserved.
 */

package commonj.work;

import java.util.ArrayList;
import java.util.List;

/**
 * This exception indicates that a Work started but completed with
 * an exception.
 * @since 1.0
 * @version 1.1
 */
public class WorkCompletedException extends WorkException 
{
	List exceptionList;
	
    /**
     * Constructor for a WorkCompletedException
     * @since 1.0
     */
    public WorkCompletedException()
    {
        super();
    }

    /**
     * Constructor for a WorkCompletedException
     * @param message
     * @since 1.0
     */
    public WorkCompletedException(String message)
    {
        super(message);
    }

    /**
     * Constructor for a WorkCompletedException
     * @param message
     * @param cause
     * @since 1.0
     */
    public WorkCompletedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructor for a WorkCompletedException
     * @param cause
     * @since 1.0
     */
    public WorkCompletedException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructor for a WorkCompletedException
     * @param message
     * @param list
     * @since 1.0
     */
	public WorkCompletedException(String message, List list)
	{
		super(message);
		exceptionList = list;
	}
	/**
	 * This allows a list of exceptions to be returned. When the Work
	 * fails to complete because of an exception then the list has a single
	 * entry and this is the same as if getCause was called.
	 * @return The list of null is no exception is present.
     * @since 1.0
	 */
	public List getExceptionList()
	{
		if(exceptionList == null && getCause() != null)
		{
			ArrayList a = new ArrayList();
			a.add(getCause());
			return a;
		}
		return exceptionList;
	}
}

