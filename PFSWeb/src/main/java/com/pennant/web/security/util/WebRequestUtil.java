package com.pennant.web.security.util;

import org.springframework.web.context.request.WebRequest;

public class WebRequestUtil {

	public WebRequestUtil(){
		super();
	}
	 private static String IS_SESSION_OK = "SessionOK";
	 
	 public static boolean isSessionOk(WebRequest request) {
	        Boolean useSessionForRequestProcessing = (Boolean) request.getAttribute(IS_SESSION_OK, WebRequest.SCOPE_REQUEST);
	        if (useSessionForRequestProcessing == null) {
	            return true;
	        } else {
	            return useSessionForRequestProcessing.booleanValue();
	        }
	    }
	 
	 	public static Object getSessionAttributeIfOk(WebRequest request, String attribute) {
	        if (isSessionOk(request)) {
	            return request.getAttribute(attribute, WebRequest.SCOPE_GLOBAL_SESSION);
	        }
	        return null;
	    }
	 	
	 	 public static boolean setSessionAttributeIfOk(WebRequest request, String attribute, Object value) {
	         if (isSessionOk(request)) {
	             request.setAttribute(attribute, value, WebRequest.SCOPE_GLOBAL_SESSION);
	             return true;
	         }
	         return false;
	     }    
	 
	 	public static void setSessionOk(WebRequest request, Boolean value) {
	        request.setAttribute(IS_SESSION_OK, value, WebRequest.SCOPE_REQUEST);
	    }

	 	 public static String getHeaderParameter(WebRequest request, String varName) {
	         String returnValue = request.getHeader(varName);
	         if (returnValue == null) {
	             returnValue = request.getParameter(varName);
	         }
	         return returnValue;
	     }
	 	
}