package com.pennanttech.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.ProcessViewDetails;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.WorkFlowDetailsService;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.activity.log.Activity;
import com.pennanttech.activity.log.ActivityLogService;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine.Flow;
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

	private ActivityLogService activityLogService;

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
		List<ErrorDetail> errorDetails = workFlowDetailsService.doValidations(workFlowDetails, Create, true); // validating object
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
			List<ErrorDetail> errorDetails = workFlowDetailsService.doValidations(workFlowDetails, Update, true);

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
		List<ErrorDetail> errorDetails = workFlowDetailsService.doValidations(workFlowDetails, Get, false);
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

	@Override
	public ProcessViewDetails getProcessViewDetails(String args) {
		logger.debug(Literal.ENTERING);

		String[] valueParm = new String[1];
		ErrorDetail errorDetail;
		WSReturnStatus status = new WSReturnStatus();
		ProcessViewDetails processViewDetails = new ProcessViewDetails();

		// Get the parameters from the comma delimited arguments.
		if (StringUtils.isEmpty(args)) {
			valueParm[0] = "args parameter";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("WFEE08", valueParm), "EN");
			status.setReturnCode(errorDetail.getCode());
			status.setReturnText(errorDetail.getError());
			processViewDetails.setReturnStatus(status);
			return processViewDetails;
		}

		String params[] = args.split(",");

		if (params.length != 4) {
			valueParm[0] = "args";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("WFEE09", valueParm), "EN");
			status.setReturnCode(errorDetail.getCode());
			status.setReturnText(errorDetail.getError());
			processViewDetails.setReturnStatus(status);
			return processViewDetails;
		}

		// Get the activities for the event.
		List<Activity> activities = new ArrayList<>();

		try {
			activities = activityLogService.getActivities(params[0], params[1], Long.valueOf(params[2]),
					Long.valueOf(params[3]));
		} catch (Exception e) {
			logger.error(e);
			valueParm[0] = "args";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("WFEE10", valueParm), "EN");
			status.setReturnCode(errorDetail.getCode());
			status.setReturnText(errorDetail.getError());
			processViewDetails.setReturnStatus(status);
			return processViewDetails;
		}

		if (activities.isEmpty()) {
			valueParm[0] = "activities";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("WFEE08", valueParm), "EN");
			status.setReturnCode(errorDetail.getCode());
			status.setReturnText(errorDetail.getError());
			processViewDetails.setReturnStatus(status);
			return processViewDetails;
		}

		// Prepare the result.
		processViewDetails.setRoles(getRoles(activities));
		processViewDetails.setRolesWithCount(getRolesWithCount(activities));
		processViewDetails.setRecordStatuses(getRecordStatuses(activities));
		processViewDetails.setVisitedRoles(getVisitedRoles(activities));
		processViewDetails.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		logger.debug(Literal.LEAVING);
		return processViewDetails;
	}

	/**
	 * getRoleCodes method gives the ordered RoleCodes through activities and do the commenting steps in the
	 * getRoleCodes method<br>
	 * 
	 * @param activities
	 *            (List<Activity>)
	 * 
	 * @return roles
	 */
	private List<String> getRoles(List<Activity> activities) {
		logger.debug(Literal.ENTERING);

		List<String> roles = new ArrayList<>();

		// Get the process flow details.
		long workflowId = activities.get(0).getWorkflowId();
		WorkFlowDetails workflow = WorkFlowUtil.getWorkflow(workflowId);

		if (workflow == null) {
			return roles;
		}

		// Add the roles of visited stages.
		for (Activity activity : activities) {
			if (!roles.contains(activity.getRoleCode())) {
				roles.add(activity.getRoleCode());
			}
		}

		// Get the next role code of last stage.
		String nextRoleCode = activities.get(activities.size() - 1).getNextRoleCode();

		if (StringUtils.isEmpty(nextRoleCode)) {
			roles.add("completed");
		} else {
			// Check whether the next role code is a predecessor.
			String[] nextRoleCodes = nextRoleCode.split(",");
			WorkflowEngine engine = new WorkflowEngine(workflow.getWorkFlowXml());
			String nextTask;
			String task;
			List<String> nextRoles = new ArrayList<>();

			for (String nextRole : nextRoleCodes) {
				nextTask = engine.getUserTaskId(nextRole);

				for (String role : roles) {
					task = engine.getUserTaskId(role);

					if (engine.compareTo(task, nextTask) == Flow.PREDECESSOR) {
						nextRoles.add(role);
					}
				}
			}

			// Add the roles that can be visited.
			if (!nextRoles.isEmpty()) {
				roles.addAll(nextRoles);
			}

			// Add the next role code.
			roles.add(nextRoleCode.replace(',', ';'));
		}


		logger.debug(Literal.LEAVING);
		return roles;
	}

	/**
	 * To get the Roles with count i.e the number of times the Respective Record has met the same Role.
	 * 
	 * @param activities
	 *            (List<Activity>)
	 * 
	 * @return rolesWithCount.
	 */
	private String getRolesWithCount(List<Activity> activities) {
		logger.debug(Literal.ENTERING);

		StringBuilder rolesWithCount = new StringBuilder();
		List<String> uniqueRoles = getUniqueRoles(activities);

		for (int i = 0; i < uniqueRoles.size(); i++) {
			int roleCount = 0;
			String role = uniqueRoles.get(i);

			if (i == 0) {
				roleCount = 1;
			}

			for (Activity activity : activities) {
				if (StringUtils.isNotEmpty(activity.getNextRoleCode())) {
					String nextRoleCodes[] = activity.getNextRoleCode().split(",");
					for (String nextRoleCode : nextRoleCodes) {
						if (role.equals(nextRoleCode) && !activity.getRecordStatus().equals("Saved"))
							++roleCount;
					}
				}
			}

			if (i == 0) {
				rolesWithCount = rolesWithCount.append(role + "," + roleCount);
			} else {
				rolesWithCount = rolesWithCount.append("," + role + "," + roleCount);
			}
		}

		logger.debug(Literal.LEAVING);
		return rolesWithCount.toString();
	}

	// returns the Record Statuses
	private List<String> getRecordStatuses(List<Activity> activities) {
		logger.debug(Literal.ENTERING);

		List<String> recordStatuses = new ArrayList<>();

		for (Activity activity : activities) {
			if (!activity.getRecordStatus().equals("Saved")) {
				recordStatuses.add(activity.getRecordStatus());
			}
		}

		logger.debug(Literal.LEAVING);
		return recordStatuses;
	}

	// returns the Visited Role Codes
	private List<String> getVisitedRoles(List<Activity> activities) {
		logger.debug(Literal.ENTERING);

		List<String> visitedRoles = new ArrayList<>();

		for (Activity activity : activities) {
			if (!activity.getRecordStatus().equals("Saved") && !visitedRoles.contains(activity.getRoleCode())) {
				visitedRoles.add(activity.getRoleCode());
			}
		}

		logger.debug(Literal.LEAVING);
		return visitedRoles;
	}

	/**
	 * getUniqueNextRoleCodes method is to filter the unique nextRoleCodes from the activities
	 * 
	 * @param activities
	 *            (List<Activity>)
	 * 
	 * @return uniqueRoles.
	 */
	private List<String> getUniqueRoles(List<Activity> activities) {
		logger.debug(Literal.ENTERING);

		List<String> uniqueRoles = new ArrayList<String>();
		uniqueRoles.add(activities.get(0).getRoleCode());

		for (Activity activity : activities) {
			if (StringUtils.isNotEmpty(activity.getNextRoleCode())) {
				String tempNextRoleCodes[] = activity.getNextRoleCode().split(",");
				for (String tempNextRoleCode : tempNextRoleCodes) {
					if (!(uniqueRoles.contains(tempNextRoleCode))) {
						uniqueRoles.add(tempNextRoleCode);
					}
				}
			}

		}

		logger.debug(Literal.LEAVING);
		return uniqueRoles;
	}

	@Autowired
	public void setActivityLogService(ActivityLogService activityLogService) {
		this.activityLogService = activityLogService;
	}

}
