package com.pff.framework.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;

import com.pff.framework.util.LogUtils;



public class AppLogger implements Log
{
    /**
     * Enter method prefix
     */
    private static final String ENTER_PREFIX = " ENTERING METHOD : ";

    /**
     * Exit method prefix
     */
    private static final String EXIT_PREFIX = " EXITING METHOD : ";

    /**
     * Parameter prefix
     */
    private static final String PARAM_PREFIX = " PARAM";

    /**
     * Return value prefix
     */
    private static final String RETURN_PREFIX = " RETURN VALUE = ";
    
    /**
     * Query Description prefix
     */
    private static final String QUERY_DESC_PREFIX =
                                        " QUERY DESCRIPTION = ";
    
    /**
     * Query Data prefix
     */
    private static final String QUERY_PREFIX = " QUERY = ";
    
    private static final String LAYOUT 
                = "%d{dd MMM yyyy HH:mm:ss} %p %t %c (%F:%L) - %m%n";

    /**
     * Name of this class.
     */
    private static final String APP_LOGGER_CLASS_NAME = AppLogger.class.getName();
                                    
    /**
     * Log4j logger for logging
     */
    private final Logger logger;

    /**
     * Creates a logger for the given class.
     * 
     * @param cls the <code>Class</code> object interested in logging.
     */
    public AppLogger(Class<?> cls)
    {
        logger=Logger.getLogger(cls);
        
        boolean foundFile = true;
         InputStream iStream = null;
        try
        {
            try 
            {
                iStream = LogUtils.loadFromClasspath("log.properties");
            }
            catch(RuntimeException re)
            {
                foundFile = false;
            }
            
            if(foundFile) 
            {
                Properties props = new Properties();
                
                props.load(iStream);
                
                PropertyConfigurator.configure(props);
            }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    
    /**
     * Creates a logger for the given class.
     * 
     * @param cls the <code>Class</code> object interested in logging.
     */
    public AppLogger(Class<?> cls , String fileName)
    {
        logger = Logger.getLogger(cls);
        
        boolean foundFile = true;
        InputStream iStream = null;
        try
        {
            try 
            {
                iStream = LogUtils.loadFromClasspath("log.properties");
            }
            catch(RuntimeException re)
            {
                foundFile = false;
            }
            
            if(foundFile) 
            {
                Properties props = new Properties();
                
                props.load(iStream);
                
                PropertyConfigurator.configure(props);
            }
            else
            {
            
                PatternLayout layout = new PatternLayout(AppLogger.LAYOUT);
                
                RollingFileAppender appender = null;
                
                appender = 
                        new RollingFileAppender(layout , fileName , true);
                appender.setMaxBackupIndex(5);
                appender.setMaxFileSize("3000KB");
                
                logger.addAppender(appender);
                logger.setLevel(Level.ALL);
            }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    
    /**
     * Returns the logger object being used for
     * logging by this class.
     * 
     * @return a <code>Logger</code> object being used for
     * logging by this class.
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    //inherit javadoc comment
    public void trace(String message)
    {
        logger.log(APP_LOGGER_CLASS_NAME, Level.DEBUG, message, null);
        
    }


    //inherit javadoc comment
    public void debug(String message)
    {
        logger.log(APP_LOGGER_CLASS_NAME, Level.DEBUG, message, null);
    }


    //inherit javadoc comment
    public void info(String message)
    {
        logger.log(APP_LOGGER_CLASS_NAME, Level.INFO, message, null);
    }


    //inherit javadoc comment
    public void warn(String message)
    {
        logger.log(APP_LOGGER_CLASS_NAME, Level.WARN, message, null);
    }


    //inherit javadoc comment
    public void error(String message)
    {
        logger.log(APP_LOGGER_CLASS_NAME, Level.ERROR, message, null);
    }

    //inherit javadoc comment
    public void error(String message, Throwable throwable)
    {
        logger.log(APP_LOGGER_CLASS_NAME, Level.ERROR, message, throwable);
    }

    //inherit javadoc comment
    public void fatal(String message)
    {
        logger.log(APP_LOGGER_CLASS_NAME, Level.FATAL, message, null);
    }

    //inherit javadoc comment
    public void fatal(String message, Throwable throwable)
    {
        logger.log(APP_LOGGER_CLASS_NAME, Level.FATAL, message, throwable);
    }

    //inherit javadoc comment
    public void entering(String methodName)
    {
        entering(methodName, null);
    }

    //inherit javadoc comment
    public void entering(String methodName, Object param1)
    {
        Object[] params =
        { param1 };
        entering(methodName, params);
    }

    //inherit javadoc comment
    public void entering(String methodName, Object param1, 
            Object param2)
    {
        Object[] params =
        { param1, param2 };
        entering(methodName, params);
    }

    //inherit javadoc comment
    public void entering(String methodName, Object param1,
            Object param2, Object param3)
    {
        Object[] params =
        { param1, param2, param3 };
        entering(methodName, params);
    }

    //inherit javadoc comment
    public void entering(String methodName, Object[] params)
    {
        
        StringBuffer buf = new StringBuffer(ENTER_PREFIX);
        buf.append(methodName+"(..)");

        if (params != null)
        {
            for (int i = 0; i < params.length; i++)
            {
                if (i > 0 && i != params.length)
                {
                    buf.append(",");
                }

                buf.append(PARAM_PREFIX);
                buf.append(i + 1);
                buf.append(" = [");
                buf.append(params[i]);
                buf.append("]");

            } // end for

        } // end if
        
        logger.log(APP_LOGGER_CLASS_NAME, Level.DEBUG, buf.toString(), null);
        
    } // end of method enterMethod

    //inherit javadoc comment
    public void exiting(String methodName)
    {
        
        StringBuffer buf = new StringBuffer();
        buf.append(EXIT_PREFIX);
        buf.append(methodName+"(..)");

        logger.log(APP_LOGGER_CLASS_NAME, Level.DEBUG, buf.toString(), null);

    } // end of method exitMethod

    //inherit javadoc comment
    public void exiting(String methodName, Object retVal)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(EXIT_PREFIX);
        buf.append(methodName+"(..)");
        buf.append(RETURN_PREFIX);
        buf.append("[ ");
        buf.append(retVal);
        buf.append(" ]");

        logger.log(APP_LOGGER_CLASS_NAME, Level.DEBUG, buf.toString(), null);


    } // end of method exitMethod
    
    //inherit javadoc comment
    public void query(String query, String[] params, String desc)
    { 
        String resQuery=LogUtils.replaceQMarks(query,params);
        
        this.query(resQuery,desc);
        
    }/*end method query*/
    
    //inherit javadoc comment
    public void query(String query,String desc)
    {
        logger.log(APP_LOGGER_CLASS_NAME, Level.DEBUG
                , AppLogger.QUERY_PREFIX + "[ "+ query +" ]"
                + AppLogger.QUERY_DESC_PREFIX
                + "[ "+ desc +" ]", null);
                
    }/*end method query*/
}
