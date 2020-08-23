package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class RelationShipOfficersController {
	Logger logger = Logger.getLogger(RelationShipOfficersController.class);
	private final String PROCESS_TYPE_SAVE = "Save";
	private final String PROCESS_TYPE_UPDATE = "Update";
	public RelationshipOfficerService relationshipOfficerService;

	/**
	 * Method for create RealationShipOfficer in PLF system.
	 * 
	 * @param relationshipOfficer
	 * @return response
	 */
	public RelationshipOfficer createRelationshipOfficer(RelationshipOfficer relationshipOfficer) {
		logger.debug(Literal.ENTERING);
		RelationshipOfficer response = null;
		try {
			// setting required values which are not received from API
			prepareRequiredData(relationshipOfficer, PROCESS_TYPE_SAVE);
			relationshipOfficer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			relationshipOfficer.setNewRecord(true);
			relationshipOfficer.setVersion(1);
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(relationshipOfficer, PennantConstants.TRAN_WF);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = relationshipOfficerService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new RelationshipOfficer();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				relationshipOfficer = (RelationshipOfficer) auditHeader.getAuditDetail().getModelData();
				response = new RelationshipOfficer();
				response.setROfficerCode(relationshipOfficer.getROfficerCode());
				response.setROfficerIsActive(relationshipOfficer.isROfficerIsActive());
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new RelationshipOfficer();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug(Literal.LEAVING);

		return response;
	}

	private void prepareRequiredData(RelationshipOfficer relationshipOfficer, String processType) {
		logger.debug(Literal.ENTERING);
		if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
			relationshipOfficer.setNewRecord(true);
			relationshipOfficer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		} else if (StringUtils.equals(processType, PROCESS_TYPE_UPDATE)) {
			relationshipOfficer.setNewRecord(false);
			relationshipOfficer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		}
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		relationshipOfficer.setUserDetails(userDetails);
		relationshipOfficer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		relationshipOfficer.setLastMntBy(userDetails.getUserId());
		relationshipOfficer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		relationshipOfficer.setSourceId(APIConstants.FINSOURCE_ID_API);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param relationshipOfficer
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(RelationshipOfficer relationshipOfficer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, relationshipOfficer.getBefImage(), relationshipOfficer);
		return new AuditHeader(String.valueOf(relationshipOfficer.getROfficerCode()),
				String.valueOf(relationshipOfficer.getROfficerCode()), null, null, auditDetail,
				relationshipOfficer.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	/**
	 * Method for Update RelationshipOfficer in PLF system.
	 * 
	 * @param relationshipOfficer
	 * @return response
	 */
	public WSReturnStatus updateRelationshipOfficer(RelationshipOfficer relationshipOfficer) {
		logger.debug(Literal.ENTERING);
		try {
			prepareRequiredData(relationshipOfficer, PROCESS_TYPE_UPDATE);
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(relationshipOfficer, "");
			auditHeader.setApiHeader(reqHeaderDetails);

			// call relationshipOfficer update method
			auditHeader = relationshipOfficerService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
		} catch (BadSqlGrammarException badSqlE) {
			logger.error(badSqlE);
			APIErrorHandlerService.logUnhandledException(badSqlE);
			if (badSqlE.getCause() != null) {
				return APIErrorHandlerService.getFailedStatus("9999", badSqlE.getCause().getMessage());
			} else {
				return APIErrorHandlerService.getFailedStatus();
			}
		} catch (NumberFormatException nfe) {
			logger.error(nfe);
			APIErrorHandlerService.logUnhandledException(nfe);
			return APIErrorHandlerService.getFailedStatus("90275");
		} catch (DataIntegrityViolationException e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus("90275");
		} catch (Exception e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	/******* Getters and Setters ***************/
	public RelationshipOfficerService getRelationshipOfficerService() {
		return relationshipOfficerService;
	}

	public void setRelationshipOfficerService(RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}
}
