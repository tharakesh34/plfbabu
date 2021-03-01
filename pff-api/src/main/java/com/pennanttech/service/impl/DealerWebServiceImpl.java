package com.pennanttech.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.DealerController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pffws.DealerRestService;
import com.pennanttech.pffws.DealerSaopService;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class DealerWebServiceImpl implements DealerSaopService, DealerRestService {
	private final Logger logger = LogManager.getLogger(getClass());

	private ValidationUtility validationUtility;
	private VehicleDealerService vehicleDealerService;
	private DealerController dealerController;

	@Override
	public VehicleDealer getDealer(long dealerId) throws ServiceException {
		logger.debug("Entering");

		// validate dealerID
		if (StringUtils.isBlank(String.valueOf(dealerId))) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(String.valueOf(dealerId));
		VehicleDealer vehicleDealer = null;

		// call getVehicleDealer service
		vehicleDealer = vehicleDealerService.getApprovedVehicleDealerById(dealerId);

		if (vehicleDealer != null) {
			vehicleDealer.setDealerId(dealerId);
			vehicleDealer.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} else {
			vehicleDealer = new VehicleDealer();
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(dealerId);
			vehicleDealer.setReturnStatus(APIErrorHandlerService.getFailedStatus("90908", valueParm));
		}

		logger.debug("Leaving");
		return vehicleDealer;
	}

	@Override
	public VehicleDealer createDealer(VehicleDealer vehicleDealer) throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(vehicleDealer, SaveValidationGroup.class);
		VehicleDealer response = null;
		try {
			//bussiness validations
			AuditDetail auditDetail = vehicleDealerService.doValidations(vehicleDealer);

			if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
				for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
					response = new VehicleDealer();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				// call create collateral controller service
				response = dealerController.createDealer(vehicleDealer);
			}
		} catch (Exception e) {
			logger.error(e);
			response = new VehicleDealer();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		//for logging purpose
		String[] logFields = new String[1];
		if (response != null) {
			logFields[0] = String.valueOf(response.getDealerId());
		}
		APIErrorHandlerService.logKeyFields(logFields);
		APIErrorHandlerService.logReference(String.valueOf(vehicleDealer.getDealerId()));
		logger.debug("Leaving");
		return response;
	}

	/**
	 * prepare create vehicle dealer response object
	 * 
	 * @param vehicleDealer
	 * @return
	 */
	private VehicleDealer getCreateDealerResponse(VehicleDealer vehicleDealer) {
		logger.debug("Entering");
		VehicleDealer response = new VehicleDealer();
		response.setDealerId(vehicleDealer.getDealerId());
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug("Leaving");
		return response;
	}

	//Getter and Setter
	public VehicleDealerService getVehicleDealerService() {
		return vehicleDealerService;
	}

	@Autowired
	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public ValidationUtility getValidationUtility() {
		return validationUtility;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	public DealerController getDealerController() {
		return dealerController;
	}

	@Autowired(required = false)
	public void setDealerController(DealerController dealerController) {
		this.dealerController = dealerController;
	}

}
