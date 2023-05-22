package com.pennant.ws.exception;

import java.io.Serializable;

/**
 * This is generic exception for all the SOAP/REST service exceptions. After marshelling the webservice request to the
 * bean object and ValidationUtil is called, if there are any validation errors, this ServiceExceptionDetails are
 * created and this details are included in ServiceException.
 */

public class ServiceExceptionDetails implements Serializable {

	private static final long serialVersionUID = 8467599618143221437L;

	private String faultCode;
	private String faultMessage;

	public ServiceExceptionDetails() {
	    super();
	}

	public String getFaultCode() {
		return faultCode;
	}

	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}

	public String getFaultMessage() {
		return faultMessage;
	}

	public void setFaultMessage(String faultMessage) {
		this.faultMessage = faultMessage;
	}
}
