package com.pennant.ws.exception;

import java.io.Serializable;

/**
 * This is generic exception for all the SOAP/REST service exceptions.
 * After marshelling the webservice request to the bean object and
 * ValidationUtil is called, if there are any validation errors, this 
 * ServiceException is created.
 */
public class ServiceException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 8847524135826237203L;
	
	private ServiceExceptionDetails faultDetails[];

	public ServiceException(ServiceExceptionDetails faultDetails[]) {
		this.faultDetails = faultDetails;
	}

	public ServiceException(String message,
			ServiceExceptionDetails faultDetails[]) {
		super(message);
		this.faultDetails = faultDetails;
	}

	public ServiceExceptionDetails[] getFaultDetails() {
		return faultDetails;
	}

}
