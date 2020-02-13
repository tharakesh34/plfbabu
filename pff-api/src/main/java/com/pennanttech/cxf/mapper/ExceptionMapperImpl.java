package com.pennanttech.cxf.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.resource.Literal;

@Provider
public class ExceptionMapperImpl implements ExceptionMapper<Exception> {
	private static final Logger logger = LogManager.getLogger(ExceptionMapperImpl.class);

	@Override
	public Response toResponse(Exception exception) {
		logger.error(Literal.EXCEPTION, exception);
		WSReturnStatus returnStatus = new WSReturnStatus();
		returnStatus.setReturnCode("900");
		returnStatus.setReturnText(
				"Unable to process the request. Please try again later or contact the system administrator.");
		ResponseBuilder builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
		builder.type("application/json");
		builder.entity(returnStatus);
		return builder.build();

	}
}
