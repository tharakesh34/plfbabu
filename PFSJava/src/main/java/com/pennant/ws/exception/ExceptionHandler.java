package com.pennant.ws.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.commons.lang3.StringUtils;

/**
 * This Exception is for handling JSON response when there is a exception with the REST services request handling.
 */
public class ExceptionHandler implements ExceptionMapper<ServiceException> {

	public static final String RES_SERVICE_NOT_FOUND = "9997";

	public Response toResponse(ServiceException exception) {

		ServiceExceptionDetails[] faultDetails = exception.getFaultDetails();
		if (serviceNotFound(faultDetails)) {
			ResponseBuilder builder = Response.status(Response.Status.NOT_FOUND);
			return builder.build();
		}

		ResponseBuilder builder = Response.status(Response.Status.NOT_ACCEPTABLE);
		builder.type("application/json");
		builder.entity(faultDetails);
		return builder.build();

	}

	private boolean serviceNotFound(ServiceExceptionDetails[] faultDetails) {
		return faultDetails != null && faultDetails[0] != null
				&& StringUtils.equals(faultDetails[0].getFaultCode(), RES_SERVICE_NOT_FOUND);
	}
}