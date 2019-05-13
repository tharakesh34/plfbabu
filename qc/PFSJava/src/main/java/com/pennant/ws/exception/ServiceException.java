package com.pennant.ws.exception;

/**
 * This is generic exception for all the SOAP/REST service exceptions. After marshaling the web service request to the
 * bean object and ValidationUtil is called, if there are any validation errors, this ServiceException is created.
 */
public class ServiceException extends RuntimeException {
	private static final long serialVersionUID = 8847524135826237203L;

	private final ServiceExceptionDetails[] faultDetails;

	public ServiceException(ServiceExceptionDetails[] faultDetails) {
		super();
		this.faultDetails = faultDetails;
	}

	public ServiceException(String message, ServiceExceptionDetails[] faultDetails) {
		super(message);
		this.faultDetails = faultDetails;
	}

	public ServiceExceptionDetails[] getFaultDetails() {
		return faultDetails;
	}
}
