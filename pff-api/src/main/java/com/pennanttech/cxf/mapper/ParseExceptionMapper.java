package com.pennanttech.cxf.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonParseException;

/**
 * Exception mapping class to handle wrong formated JSON request.
 * 
 * @author pennant
 *
 */
@Provider
public class ParseExceptionMapper implements ExceptionMapper<JsonParseException> {

    @Override
    public Response toResponse(JsonParseException exception)  {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).type( MediaType.TEXT_PLAIN).build();
    }

}