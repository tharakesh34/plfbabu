package com.pennant.batchupload.customexception;

import java.util.Collection;

public class ValidationException extends RuntimeException{

	 /**
	 * 
	 */
	private static final long serialVersionUID = -89437348988071L;
	private Collection<String> messages;

	    public ValidationException(String msg){
	        super(msg);
	    }


	    public ValidationException(String msg, Exception cause){
	        super(msg, cause);
	    }


	    public ValidationException(Collection<String> messages){
	        super();
	        this.messages= messages;
	    }


	    public ValidationException (Collection<String> messages, Exception cause){
	        super(cause);
	        this.messages= messages;
	    }

	    @Override
	    public String getMessage(){
	        String msg;

	        if(this.messages!=null && !this.messages.isEmpty()){
	            msg="[";

	            for(String message : this.messages){
	                msg+=message+", ";
	            }

	            msg= msg.substring(0, msg.length() - 2);

	        }else msg= super.getMessage();

	        return msg;
	    }
	
}
