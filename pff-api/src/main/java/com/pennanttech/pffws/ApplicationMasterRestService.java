package com.pennanttech.pffws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.applicationmaster.ReasonCodeResponse;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface ApplicationMasterRestService {

	@GET
	@Path("/applicationMasterService/getReasonCodeDetails/{reasonTypeCode}")
	ReasonCodeResponse getReasonCodeDetails(@PathParam("reasonTypeCode") String reasonTypeCode) throws ServiceException;

}
