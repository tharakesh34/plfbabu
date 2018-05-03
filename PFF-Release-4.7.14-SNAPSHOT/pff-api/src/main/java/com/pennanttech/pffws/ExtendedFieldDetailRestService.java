package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface ExtendedFieldDetailRestService {

	@POST
	@Path("/extendedFieldDetailsService/getExtendedFieldDetail")
	public ExtendedFieldHeader getExtendedFieldDetails(ExtendedFieldHeader extendedFieldHeader) throws ServiceException;
}
