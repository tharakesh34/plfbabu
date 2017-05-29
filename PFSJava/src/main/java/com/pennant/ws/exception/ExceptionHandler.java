package com.pennant.ws.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * This Exception is for handling JSON response when there is a exception
 * with the REST services request handling.
 */
public class ExceptionHandler implements ExceptionMapper<ServiceException> {
	public Response toResponse(ServiceException exception) {

		ResponseBuilder builder = Response.status(Response.Status.NOT_ACCEPTABLE);
		builder.type("application/json");
		builder.entity(exception.getFaultDetails());
		return builder.build();
	}
}