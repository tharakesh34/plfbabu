package com.pennanttech.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pffws.CollateralRestService;
import com.pennanttech.pffws.CollateralSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.collateral.CollateralDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class CollateralWebServiceImpl implements CollateralRestService,CollateralSoapService  {
	Logger logger = Logger.getLogger(CollateralWebServiceImpl.class);
	
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
		
		if(StringUtils.isBlank(collateralType)) {
			validationUtility.fieldLevelException();
		}
		
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
			//bussiness validations
			AuditDetail auditDetail = collateralSetupService.doValidations(collateralSetup, "create");

			if (auditDetail.getErrorDetails() != null) {
				for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
					response = new CollateralSetup();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			// call create collateral controller service
			response = collateralController.createCollateral(collateralSetup);

			if (StringUtils.equals(response.getReturnStatus().getReturnCode(), APIConstants.RES_SUCCESS_CODE)) {
				response = getCreateCollateralResponse(response);
			}
		} catch (Exception e) {
			logger.error(e);
			response = new CollateralSetup();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

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
		// bean validations
		validationUtility.validate(collateralSetup, UpdateValidationGroup.class);
		WSReturnStatus response = null;
		try{
		// bussiness validations
		AuditDetail auditDetail = collateralSetupService.doValidations(collateralSetup, "update");

		
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		// call create collateral controller service
		response = collateralController.updateCollateral(collateralSetup);
		}catch (Exception e) {
			logger.error(e);
			response = new WSReturnStatus();
			response=APIErrorHandlerService.getFailedStatus();
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
		
		// bean validations
		validationUtility.validate(collateralSetup, DeleteValidationGroup.class); 
		WSReturnStatus response = null;
		try{
		AuditDetail auditDetail = collateralSetupService.doValidations(collateralSetup, "delete");
		
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}
		
		// call delete collateral service
		response = collateralController.deleteCollateral(collateralSetup);
		}catch (Exception e) {
			logger.error(e);
			response = new WSReturnStatus();
			response=APIErrorHandlerService.getFailedStatus();
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

		CollateralDetail collateralDetail = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		if (customer != null) {
			
			// call getCollaterals service
			collateralDetail = collateralController.getCollaterals(customer.getCustID());
			
			// for logging purpose
			APIErrorHandlerService.logReference(cif);

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
