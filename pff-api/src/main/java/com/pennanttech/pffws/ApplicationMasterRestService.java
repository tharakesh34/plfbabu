package com.pennanttech.pffws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.applicationmaster.ReasonCodeResponse;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.deviation.ManualDeviationAuthReq;
import com.pennanttech.ws.model.deviation.ManualDeviationAuthorities;
import com.pennanttech.ws.model.deviation.ManualDeviationList;

@Produces("application/json")
public interface ApplicationMasterRestService {

	@GET
	@Path("/applicationMasterService/getReasonCodeDetails/{reasonTypeCode}")
	ReasonCodeResponse getReasonCodeDetails(@PathParam("reasonTypeCode") String reasonTypeCode) throws ServiceException;

	@GET
	@Path("/applicationMasterService/getManualDeviationList/{categorizationCode}")
	ManualDeviationList getManualDeviationList(@PathParam("categorizationCode") String categorizationCode)
			throws ServiceException;

	@POST
	@Path("/applicationMasterService/getManualDeviationAuthorities")
	ManualDeviationAuthorities getManualDeviationAuthorities(ManualDeviationAuthReq request) throws ServiceException;

}
