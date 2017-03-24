package com.pennanttech.cxf.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;

/**
 * Exception mapping class to handle wrong field names in the request.
 * 
 * @author pennant
 *
 */
@Provider
public class UnrecognizedPropertyExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException> {

    @Override
    public Response toResponse(UnrecognizedPropertyException exception)  {
        return Response.status(Response.Status.BAD_REQUEST).entity("'" + exception.getUnrecognizedPropertyName() 
        		+ "' is an unrecognized field.").type( MediaType.TEXT_PLAIN).build();
    }

}