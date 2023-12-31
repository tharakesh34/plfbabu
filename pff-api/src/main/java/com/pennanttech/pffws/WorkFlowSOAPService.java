package com.pennanttech.pffws;

import javax.ws.rs.PathParam;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.ws.exception.ServiceException;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface WorkFlowSOAPService {

	@WebResult(name = "workFlow")
	public WorkFlowDetails createWorkFlow(@WebParam(name = "workFlow") WorkFlowDetails workFlowDetails)
			throws ServiceException;

	@WebResult(name = "workFlow")
	public WorkFlowDetails updateWorkFlow(@WebParam(name = "workFlow") WorkFlowDetails workFlowDetails)
			throws ServiceException;

	@WebResult(name = "workFlow")
	public WorkFlowDetails getWorkFlowDetails(@PathParam("wfID") String workFlowId) throws ServiceException;
}
