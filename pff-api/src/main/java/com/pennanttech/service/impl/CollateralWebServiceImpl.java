package com.pennanttech.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.validation.DeleteValidationGroup;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.UpdateValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.CollateralController;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.CollateralRestService;
import com.pennanttech.pffws.CollateralSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.collateral.CollateralDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class CollateralWebServiceImpl extends ExtendedTestClass
		implements CollateralRestService, CollateralSoapService {
	Logger logger = LogManager.getLogger(CollateralWebServiceImpl.class);

	private CollateralSetupService collateralSetupService;
	private CustomerDetailsService customerDetailsService;
	private CollateralController collateralController;
	private ValidationUtility validationUtility;

	/**
	 * Get collateral structure details by validating the requested collateral type.
	 * 
	 * @param collateralType
	 * @return CollateralStructure
	 */
	@Override
	public CollateralStructure getCollateralType(String collateralType) throws ServiceException {
		logger.debug("Entering");

		if (StringUtils.isBlank(collateralType)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(collateralType);

		// call get collateralType method
		CollateralStructure collateralStructure = collateralController.getCollateralType(collateralType);

		logger.debug("Leaving");
		return collateralStructure;
	}

	/**
	 * Method for validate and creating new collateral setup
	 * 
	 * @param collateralSetup
	 * @return CollateralSetup
	 */
	@Override
	public CollateralSetup createCollateral(CollateralSetup collateralSetup) throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(collateralSetup, SaveValidationGroup.class);
		CollateralSetup response = null;
		try {
			// bussiness validations
			AuditDetail auditDetail = collateralSetupService.doValidations(collateralSetup, "create", false);

			if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
				for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
					response = new CollateralSetup();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				// call create collateral controller service
				response = collateralController.createCollateral(collateralSetup);

				if (StringUtils.equals(response.getReturnStatus().getReturnCode(), APIConstants.RES_SUCCESS_CODE)) {
					response = getCreateCollateralResponse(response);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			response = new CollateralSetup();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		// for logging purpose
		String[] logFields = new String[1];
		if (response != null) {
			logFields[0] = response.getCollateralRef();
		}
		APIErrorHandlerService.logKeyFields(logFields);
		APIErrorHandlerService.logReference(collateralSetup.getDepositorCif());
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for validate and update collateral setup
	 * 
	 * @param collateralSetup
	 * @return WSReturnStatus
	 */
	@Override
	public WSReturnStatus updateCollateral(CollateralSetup collateralSetup) throws ServiceException {
		logger.debug("Entering");
		// for logging purpose
		String[] logFields = new String[2];
		logFields[0] = collateralSetup.getDepositorCif();
		logFields[1] = collateralSetup.getCollateralType();
		APIErrorHandlerService.logKeyFields(logFields);
		// bean validations
		validationUtility.validate(collateralSetup, UpdateValidationGroup.class);
		WSReturnStatus response = null;
		try {
			// bussiness validations
			AuditDetail auditDetail = collateralSetupService.doValidations(collateralSetup, "update", false);

			if (auditDetail.getErrorDetails() != null) {
				for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}

			// call create collateral controller service
			APIErrorHandlerService.logReference(collateralSetup.getCollateralRef());
			response = collateralController.updateCollateral(collateralSetup);
		} catch (Exception e) {
			logger.error(e);
			response = new WSReturnStatus();
			response = APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for validate and delete customer collateral
	 * 
	 * @param custCollateral
	 * @return WSReturnStatus
	 */
	@Override
	public WSReturnStatus deleteCollateral(CollateralSetup collateralSetup) throws ServiceException {
		logger.debug("Entering");
		// for logging purpose
		String[] logFields = new String[1];
		logFields[0] = collateralSetup.getDepositorCif();
		APIErrorHandlerService.logKeyFields(logFields);

		// bean validations
		validationUtility.validate(collateralSetup, DeleteValidationGroup.class);
		WSReturnStatus response = null;
		try {
			// for logging purpose
			APIErrorHandlerService.logReference(collateralSetup.getCollateralRef());
			AuditDetail auditDetail = collateralSetupService.doValidations(collateralSetup, "delete", false);

			if (auditDetail.getErrorDetails() != null) {
				for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}

			// call delete collateral service
			response = collateralController.deleteCollateral(collateralSetup);
		} catch (Exception e) {
			logger.error(e);
			response = new WSReturnStatus();
			response = APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Fetch list customer collateral details by cif
	 * 
	 * @param cif
	 * @return
	 */
	@Override
	public CollateralDetail getCollaterals(String cif) throws ServiceException {
		logger.debug("Entering");

		// validate collateral cif
		if (StringUtils.isBlank(cif)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		CollateralDetail collateralDetail = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		if (customer != null) {
			// call getCollaterals service
			collateralDetail = collateralController.getCollaterals(customer.getCustID(), "");

			if (collateralDetail != null) {
				collateralDetail.setCif(cif);
				collateralDetail.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				collateralDetail = new CollateralDetail();
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				collateralDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90908", valueParm));
			}
		} else {
			collateralDetail = new CollateralDetail();
			String[] valueParm = new String[1];
			valueParm[0] = cif;
			collateralDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
		}

		logger.debug(Literal.LEAVING);
		return collateralDetail;
	}

	@Override
	public WSReturnStatus pendingUpdateCollateral(CollateralSetup collateralSetup) throws ServiceException {
		logger.debug(Literal.ENTERING);

		String[] logFields = new String[2];
		logFields[0] = collateralSetup.getDepositorCif();
		logFields[1] = collateralSetup.getCollateralType();
	
		APIErrorHandlerService.logKeyFields(logFields);

		validationUtility.validate(collateralSetup, UpdateValidationGroup.class);
		
		WSReturnStatus response = null;
		try {
			CollateralSetup collateral = collateralSetupService
					.getCollateralSetupDetails(collateralSetup.getCollateralRef(), "_Temp");
			AuditDetail auditDetail = collateralSetupService.doValidations(collateralSetup, "update", true);

			if (CollectionUtils.isNotEmpty(auditDetail.getErrorDetails())) {
				ErrorDetail errorDetail = auditDetail.getErrorDetails().get(0);
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
			// call create collateral controller service
			if (collateral != null) {
				APIErrorHandlerService.logReference(collateralSetup.getCollateralRef());
				response = collateralController.pendingUpdateCollateral(collateralSetup);
			} else {
				collateral = new CollateralSetup();
				String[] valueParm = new String[1];
				valueParm[0] = collateralSetup.getCollateralRef();
				collateral.setReturnStatus(APIErrorHandlerService.getFailedStatus("90908", valueParm));
			}

		} catch (Exception e) {
			logger.error(e);
			response = APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public CollateralDetail getPendingCollaterals(String cif) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// validate collateral cif
		if (StringUtils.isBlank(cif)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		CollateralDetail collateralDetail = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		if (customer != null) {

			// call getCollaterals service
			collateralDetail = collateralController.getCollaterals(customer.getCustID(), "_Temp");

			if (collateralDetail != null) {
				collateralDetail.setCif(cif);
				collateralDetail.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				collateralDetail = new CollateralDetail();
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				collateralDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90908", valueParm));
			}
		} else {
			collateralDetail = new CollateralDetail();
			String[] valueParm = new String[1];
			valueParm[0] = cif;
			collateralDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
		}

		logger.debug("Leaving");
		return collateralDetail;
	}

	/**
	 * prepare create collateral response object
	 * 
	 * @param collateralSetup
	 * @return
	 */
	private CollateralSetup getCreateCollateralResponse(CollateralSetup collateralSetup) {
		logger.debug("Entering");
		CollateralSetup response = new CollateralSetup();
		response.setCollateralRef(collateralSetup.getCollateralRef());
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug("Leaving");
		return response;
	}

	@Autowired
	public void setCollateralController(CollateralController collateralController) {
		this.collateralController = collateralController;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

}
