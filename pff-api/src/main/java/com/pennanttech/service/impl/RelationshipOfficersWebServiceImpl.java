package com.pennanttech.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.UpdateValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.RelationShipOfficersController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.RelationshipOfficersRestService;
import com.pennanttech.pffws.RelationshipOfficersSoapService;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class RelationshipOfficersWebServiceImpl
		implements RelationshipOfficersSoapService, RelationshipOfficersRestService {
	private final Logger logger = LogManager.getLogger(getClass());
	private ValidationUtility validationUtility;
	private RelationshipOfficerService relationshipOfficerService;
	private RelationShipOfficersController relationShipOfficersController;

	@Override
	public RelationshipOfficer createRelationshipOfficer(RelationshipOfficer relationshipOfficer)
			throws ServiceException {
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(relationshipOfficer, SaveValidationGroup.class);
		RelationshipOfficer response = null;
		try {
			// bussiness validations
			AuditDetail auditDetail = relationshipOfficerService.doValidations(relationshipOfficer);

			if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
				for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
					response = new RelationshipOfficer();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				// call create Relationship Officer controller service
				response = relationShipOfficersController.createRelationshipOfficer(relationshipOfficer);
			}
		} catch (Exception e) {
			logger.error(e);
			response = new RelationshipOfficer();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		// for logging purpose
		String[] logFields = new String[1];
		if (response != null) {
			logFields[0] = String.valueOf(response.getROfficerCode());
		}
		APIErrorHandlerService.logKeyFields(logFields);
		APIErrorHandlerService.logReference(String.valueOf(relationshipOfficer.getROfficerCode()));
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus updateRelationshipOfficer(RelationshipOfficer relationshipOfficer) throws ServiceException {
		logger.debug(Literal.ENTERING);
		String[] logFields = new String[1];
		logFields[0] = String.valueOf(relationshipOfficer.getROfficerCode());
		APIErrorHandlerService.logKeyFields(logFields);
		// bean validations
		validationUtility.validate(relationshipOfficer, UpdateValidationGroup.class);
		WSReturnStatus response = null;
		try {
			RelationshipOfficer befRelationshipOfficer = relationshipOfficerService
					.getRelationshipOfficerById(relationshipOfficer.getROfficerCode());
			if (befRelationshipOfficer == null) { // if records not exists in the
				String[] valueParm = new String[2];
				valueParm[0] = "rOfficerCode";
				valueParm[1] = relationshipOfficer.getROfficerCode();
				ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParm));
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
			// bussiness validations
			AuditDetail auditDetail = relationshipOfficerService.doValidations(relationshipOfficer);

			if (auditDetail.getErrorDetails() != null) {
				for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}

			// call update RelationshipOfficer controller service
			APIErrorHandlerService.logReference(String.valueOf(relationshipOfficer.getROfficerCode()));
			relationshipOfficer.setVersion(befRelationshipOfficer.getVersion() + 1);
			response = relationShipOfficersController.updateRelationshipOfficer(relationshipOfficer);
		} catch (Exception e) {
			logger.error(e);
			response = new WSReturnStatus();
			response = APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	public ValidationUtility getValidationUtility() {
		return validationUtility;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	public RelationshipOfficerService getRelationshipOfficerService() {
		return relationshipOfficerService;
	}

	@Autowired
	public void setRelationshipOfficerService(RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}

	public RelationShipOfficersController getRelationShipOfficersController() {
		return relationShipOfficersController;
	}

	@Autowired(required = false)
	public void setRelationShipOfficersController(RelationShipOfficersController relationShipOfficersController) {
		this.relationShipOfficersController = relationShipOfficersController;
	}
}