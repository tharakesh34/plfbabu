package com.pennanttech.pffws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface VASRestService {
	@GET
	@Path("/vasService/getVASProduct/{product}")
	public VASConfiguration getVASProduct(@PathParam("product") String product) throws ServiceException;

	@POST
	@Path("/vasService/recordVAS")
	public VASRecording recordVAS(VASRecording vasRecording) throws ServiceException;

	@POST
	@Path("/vasService/cancelVAS")
	public WSReturnStatus cancelVAS(VASRecording vasRecording) throws ServiceException;

	@POST
	@Path("/vasService/getRecordVAS")
	public WSReturnStatus getRecordVAS(VASConfiguration vasConfiguration) throws ServiceException;
	
}
