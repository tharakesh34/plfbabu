package com.pennanttech.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.WorkFlowDetailsService;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.WorkFlowRESTService;
import com.pennanttech.pffws.WorkFlowSOAPService;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class WorkFlowWebServiceImpl implements WorkFlowRESTService,WorkFlowSOAPService {
	private static final Logger logger = Logger.getLogger(WorkFlowWebServiceImpl.class);
	@Autowired
	WorkFlowDetailsService workFlowDetailsService;
	private  final String Create = "create";
	private  final String Update = "update";
	private  final String Get = "get";

	@Override
	public WorkFlowDetails createWorkFlow(WorkFlowDetails workFlowDetails) throws ServiceException {
		logger.debug("Entering");
		// for logging purpose
		APIErrorHandlerService.logReference("CREATEWORKFLOW");
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		WorkFlowDetails response = new WorkFlowDetails();
		workFlowDetails.setNewRecord(true);
		workFlowDetails.setWorkflowId(0);
		workFlowDetails.setLastMntBy(userDetails.getUserId());
		workFlowDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			List<ErrorDetail> errorDetails = workFlowDetailsService.doValidations(workFlowDetails, Create); // validating object
			for(ErrorDetail errDetail : errorDetails) {// returning in case of exception
				WSReturnStatus status = new WSReturnStatus();
				status.setReturnCode(errDetail.getCode());
				status.setReturnText(errDetail.getError());
				response.setReturnStatus(status);
				return response;
			}
			// proceeding for insertion
			workFlowDetails.setNewRecord(true);
			workFlowDetails.setWorkFlowActive(true); 
			List<String> firstTaskOwnersAndActors = getFirstTaskOwnersNActors(workFlowDetails.getWorkFlowXml());// getting FirstTaskOwners and Actors.
			workFlowDetails.setFirstTaskOwner(firstTaskOwnersAndActors.get(0));
			workFlowDetails.setWorkFlowRoles(firstTaskOwnersAndActors.get(1));
			AuditDetail auditDetail = new AuditDetail("", 1, workFlowDetails.getBefImage(), workFlowDetails);
			AuditHeader auditHeader = new AuditHeader(String.valueOf(workFlowDetails.getId()), null, null, null,
					auditDetail, workFlowDetails.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());

			auditHeader = workFlowDetailsService.saveOrUpdate(auditHeader);
			workFlowDetails = (WorkFlowDetails) auditHeader.getAuditDetail().getModelData();

			doEmptyResponseObject(workFlowDetails);

			response.setWorkFlowActive(true);
			response.setWorkFlowDesignId(workFlowDetails.getId());
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug("Leaving");
		return response;
	}

	@Override
	public WorkFlowDetails updateWorkFlow(WorkFlowDetails workFlowDetails) throws ServiceException {
		logger.debug("Entering");
		// for logging purpose
		APIErrorHandlerService.logReference("UPDATEWORKFLOW");
		WorkFlowDetails response = new WorkFlowDetails();
		try {
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			workFlowDetails.setLastMntBy(userDetails.getUserId());
			workFlowDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			workFlowDetails.setWorkFlowId(workFlowDetails.getWorkFlowDesignId());
			List<ErrorDetail> errorDetails = workFlowDetailsService.doValidations(workFlowDetails, Update);

			for(ErrorDetail errDetail : errorDetails) {
				WSReturnStatus status = new WSReturnStatus();
				status.setReturnCode(errDetail.getCode());
				status.setReturnText(errDetail.getError());
				response.setReturnStatus(status);
				return response;
			}
			// proceeding for updation
			workFlowDetails.setNewRecord(false);
			workFlowDetails.setBefImage(workFlowDetails);
			List<String> firstTaskOwnersAndActors = getFirstTaskOwnersNActors(workFlowDetails.getWorkFlowXml());// getting FirstTaskOwners and Actors.
			workFlowDetails.setFirstTaskOwner(firstTaskOwnersAndActors.get(0));
			workFlowDetails.setWorkFlowRoles(firstTaskOwnersAndActors.get(1));
			workFlowDetails.setVersion(workFlowDetailsService.getWorkFlowDetailsVersionByID(workFlowDetails.getWorkflowId()));
			AuditDetail auditDetail = new AuditDetail("", 1, workFlowDetails.getBefImage(), workFlowDetails);
			AuditHeader auditHeader = new AuditHeader(String.valueOf(workFlowDetails.getId()), null, null, null,
					auditDetail, workFlowDetails.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
			auditHeader = workFlowDetailsService.saveOrUpdate(auditHeader);
			doEmptyResponseObject(response);
			response.setWorkFlowDesignId(((WorkFlowDetails) auditHeader.getAuditDetail().getModelData()).getId());
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("112121", "Something Went Wrong"));
		}
		logger.debug("Leaving");
		return response;
	}

	@Override
	public WorkFlowDetails getWorkFlowDetails(String workFlowId) throws ServiceException {
		logger.debug("Entering");
		// for logging purpose
		APIErrorHandlerService.logReference("GETWORKFLOWDETAILS"+workFlowId);				
		WorkFlowDetails workFlowDetails = new WorkFlowDetails();
			if(StringUtils.isNotBlank(workFlowId))
			workFlowDetails.setWorkflowId(Long.valueOf(workFlowId));
			List<ErrorDetail> errorDetails = workFlowDetailsService.doValidations(workFlowDetails, Get);
			for(ErrorDetail errDetail : errorDetails) {
				WSReturnStatus status = new WSReturnStatus();
				status.setReturnCode(errDetail.getCode());
				status.setReturnText(errDetail.getError());
				workFlowDetails.setReturnStatus(status);
				return workFlowDetails;
			}
			workFlowDetails = workFlowDetailsService.getWorkFlowDetailsByID(Long.valueOf(workFlowId)); // getting result
			workFlowDetails.setWorkFlowDesignId(workFlowDetails.getWorkFlowId());
			workFlowDetails.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		return workFlowDetails;
	}

	/**
	 * Nullify the un-necessary objects to prepare response in a structured format specified in API.
	 * 
	 * @param response
	 * @throws Exception
	 */
	private void doEmptyResponseObject(WorkFlowDetails response) {
		response.setWorkFlowType(null);
		response.setWorkFlowSubType(null);
		response.setWorkFlowDesc(null);
		response.setWorkFlowXml(null);
		response.setWorkFlowRoles(null);
		response.setFirstTaskOwner(null);
		response.setJsonDesign(null);
	}
	
	private List<String> getFirstTaskOwnersNActors(String xml){
		String actrors = "";
		List<String> firstTaskOwnersAndActors =  new ArrayList<String>();
		try{
		WorkflowEngine workflowEngine = new WorkflowEngine(xml);
		firstTaskOwnersAndActors.add(workflowEngine.firstTaskOwner());
		for (String actor : workflowEngine.getActors(false)) {
			actrors += actor+";";
		}
		firstTaskOwnersAndActors.add(actrors.substring(0, actrors.length() - 1));
		}catch(Exception e){
			e.printStackTrace();
		}
		return firstTaskOwnersAndActors;
	}
}
