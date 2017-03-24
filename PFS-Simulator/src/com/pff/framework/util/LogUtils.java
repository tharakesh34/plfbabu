package com.pff.framework.util;

import java.io.File;
import java.io.InputStream;
import java.security.CodeSource;

/**
 * Utilities class providing common functionality
 * which can be used across the project.
 * 
 */
public final class LogUtils
{
    
    //===============================
    // Constructor
    //===============================
    
    /**
     * Default constructor
     */
    private LogUtils()
    {
        
    }/*end constructor*/

    //===============================
    // Static Methods
    //===============================
    
    /**
     * Loads the given file from the classpath.
     *
     * @param fileName a string containing the file name.
     * 
     * @return an <code>InputStream</code> object
     *      corresponding to the loaded input file.
     * 
     * @exception RuntimeException in case of failure to
     *          load a resource.
     */
    public static InputStream loadFromClasspath(String fileName)
        throws RuntimeException
    {
        InputStream is = LogUtils.class.getResourceAsStream("/"
                    + fileName);
                        
        if(is == null)
        {
            throw new RuntimeException(
                "Failed to load "+ fileName +" from CLASSPATH.");
                               
        }/*end if*/
        
        return is;
        
    }/*end method loadFromClasspath*/
    
    /**
     * Replaces the question marks in the text provided
     * as first parameter, with values in the array of
     * strings provided as the second parameter.
     * <p>
     * <u>Example:</u><br>
     * <blockquote>
     * Input: <code>" a b ? d e ?", {"c","f"}</code><p>
     * Output: <code>a b c d e f</code>
     * </blockquote>
     * 
     * @param text a string containing the text with question
     *              marks to be replaced.
     * 
     * @param params an array of strings containing the values
     *                  to be used to replace question marks. 
     * 
     * @return the input string with question marks substituted
     *          with the values.
     */
    public static String replaceQMarks(String text,String[] params)
    {
        final String Q_MARK="?";
        
        if(params==null)
        {
          return text;
        }
        
        //resultant text  
        String resText="";
        
        int prevQpos=0,nextQpos=0;
        
        nextQpos=text.indexOf(Q_MARK,prevQpos);
          
        //We need to replace all ? with the values in the params.
        for(int i=0;nextQpos!=-1 && i < params.length ; ++i)
        {
            resText += text.substring(prevQpos,nextQpos)+params[i];
            
            prevQpos = nextQpos+1;
            
            nextQpos=text.indexOf(Q_MARK,prevQpos);            
        }/*end for*/
       
        
        return resText+text.substring(prevQpos);
    }    

  
   
    public static String getApplicationPath(){
    	
		 try {
			 CodeSource codeSource = LogUtils.class.getProtectionDomain().getCodeSource();
			File jarFile = new File(codeSource.getLocation().toURI().getPath());
			return jarFile.getParentFile().getPath();
		} catch (Exception e) {
			return "";
			
		}

   	
   }  
} // end of class Utils
