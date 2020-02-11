package com.pennanttech.cxf.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pennant.backend.model.WSReturnStatus;

@Provider
public class UnknownExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		WSReturnStatus returnStatus = new WSReturnStatus();
		returnStatus.setReturnCode("9999");
		returnStatus.setReturnText("Unable to process request");
		ResponseBuilder builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
		builder.type("application/json");
		builder.entity(returnStatus);
		return builder.build();

	}
}
