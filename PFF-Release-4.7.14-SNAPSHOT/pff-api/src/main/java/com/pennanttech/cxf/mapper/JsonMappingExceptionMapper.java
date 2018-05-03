package com.pennanttech.cxf.mapper;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonMappingException.Reference;

/**
 * Exception Mapping file to display the wrong values in request fields.
 * @author pennant
 *
 */
@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

    @Override
    public Response toResponse(JsonMappingException exception) {
    	
    	String message = "Invalid value in field : ";
    	List<Reference> refs = exception.getPath();
		if (refs.size() > 0) {
			for (int fieldCount = 0; fieldCount < refs.size(); fieldCount++) {
				if (refs.get(fieldCount).getFieldName() != null) {
					message += refs.get(fieldCount).getFieldName();
					message +="/";
				}
			}
		} else {
			message += exception.getLocation().getLineNr() + " ,column " + exception.getLocation().getColumnNr();
		}
		if(message.endsWith("/")) {
			message = message.substring(0, message.length()-1);
		}
        return Response.status(Response.Status.BAD_REQUEST).entity(message).type( MediaType.TEXT_PLAIN).build();
    }

}