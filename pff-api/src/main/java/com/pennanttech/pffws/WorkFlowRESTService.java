package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.ProcessViewDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface WorkFlowRESTService {

	@POST
	@Path("/workFlowService/createWorkFlow")
	public WorkFlowDetails createWorkFlow(@WebParam(name = "workFlow") WorkFlowDetails workFlowDetails)
			throws ServiceException;

	@POST
	@Path("/workFlowService/updateWorkFlow")
	public WorkFlowDetails updateWorkFlow(@WebParam(name = "workFlow") WorkFlowDetails workFlowDetails)
			throws ServiceException;
	
	@GET
	@Path("/workFlowService/getWorkFlowDetails/{workflowID: .*}")
	public WorkFlowDetails getWorkFlowDetails(@PathParam("workflowID") String workFlowId) throws ServiceException;
	
	@GET
	@Path("/workFlowService/getProcessView/{args: .*}")
	public ProcessViewDetails getProcessViewDetails(@PathParam("args") String args) throws ServiceException;
	
}
