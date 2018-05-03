package com.pff.framework.logging;

import java.util.HashMap;

/**
 * Factory class, which creates the logger object.
 * 
 */
public final class LogFactory
{

    /** 
     * Cache of loggers.
     */
    private static final HashMap<String,Log> M_LOG_INSTANCES = new HashMap<String,Log>();
    
    /** 
     * Default Constructor
     */
    private LogFactory()
    {
        
    }/*end constructor*/

    /**  
     * Gets the instance of logger which is associated with the given
     *  class. 
     * <p>
     * This method searches its cache for the logger instance
     * with the given class name. 
     * <p>
     * If an instance of the logger is not found
     * in the cache, then a new instance of logger
     * is created and returned.
     *
     * @param clss a <code>Class</code> object interested
     *              in using the logger.
     * 
     * @return an implementation of <code>Log</code>
     * 
     */
    public static Log getLog(Class<?> clss)
    {
        String name = clss.getName();
        
        Log log = (Log) M_LOG_INSTANCES.get(name);
        
        if (log == null)
        {
            
            synchronized(LogFactory.class)
            {
                log = new AppLogger(clss);
                
                M_LOG_INSTANCES.put(name, log);
                
            }//end synchronized
            
        } //end if        
        
        return log;
    }
    
    /**  
     * Gets the instance of logger which is associated with the given
     *  class. 
     * <p>
     * This method searches its cache for the logger instance
     * with the given class name. 
     * <p>
     * If an instance of the logger is not found
     * in the cache, then a new instance of logger
     * is created and returned.
     *
     * @param clss a <code>Class</code> object interested
     *              in using the logger.
     * 
     * @param fileName The filename to which the logs need to be sent.
     * 
     * @return an implementation of <code>Log</code>
     */
    public static Log getLog(Class<?> clss , String fileName)
    {
        String name = clss.getName();
        
        Log log = (Log) M_LOG_INSTANCES.get(name);
        
        if (log == null)
        {
            
            synchronized(LogFactory.class)
            {
                log = new AppLogger(clss , fileName);
                
                M_LOG_INSTANCES.put(name, log);
                
            }//end synchronized
            
        } //end if        
        
        return log;
    }
   
}