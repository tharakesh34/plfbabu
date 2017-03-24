package com.pennanttech.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.FinanceTypeController;
import com.pennanttech.pffws.PromotionTypeRestService;
import com.pennanttech.pffws.PromotionTypeSoapService;
import com.pennanttech.ws.model.financetype.FinanceTypeRequest;
import com.pennanttech.ws.model.financetype.FinanceTypeResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class PromotionTypeWebServiceImpl implements PromotionTypeRestService,PromotionTypeSoapService {

	private final static Logger logger = Logger.getLogger(PromotionTypeWebServiceImpl.class);

	private FinanceTypeController financeTypeController;
	private ValidationUtility validationUtility;
	private FinanceTypeService financeTypeService;
	private StepPolicyService stepPolicyService;

	/**
	 * Fetch promotion type details
	 * 
	 * @param finTypeReq
	 * @return FinanceTypeResponse
	 */
	@Override
	public FinanceTypeResponse getPromotion(FinanceTypeRequest finTypeReq) throws ServiceException {
		logger.debug("Entering");

		if (finTypeReq != null) {
			// Mandatory validation
			if (StringUtils.isBlank(finTypeReq.getFinType())) {
				validationUtility.fieldLevelException();
			}

			// validate financeType
			String finType = finTypeReq.getFinType();
			int count = financeTypeService.getPromotionTypeCountById(finType);

			FinanceTypeResponse response = new FinanceTypeResponse();
			if (count > 0) {
				response = financeTypeController.getFinanceTypeDetails(finTypeReq);
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = finType;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90222", valueParm));
			}

			logger.debug("Leaving");
			return response;
		} else {
			FinanceTypeResponse response = new FinanceTypeResponse();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
	}

	/**
	 * Fetch Step Policy details from system.
	 * 
	 * @param policyCode
	 */
	@Override
	public StepPolicyHeader getStepPolicy(String policyCode) throws ServiceException {
		logger.debug("Entering");

		// Mandatory validation
		if (StringUtils.isBlank(policyCode)) {
			validationUtility.fieldLevelException();
		}

		StepPolicyHeader response = new StepPolicyHeader();

		// validate policyCode
		StepPolicyHeader header = stepPolicyService.getStepPolicyHeaderById(policyCode);
		if (header != null) {
			response = financeTypeController.getStepPolicyDetails(policyCode);
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = policyCode;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90501", valueParm));
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * Fetch list of promotion details from system.
	 * 
	 * @param productCode
	 */
	@Override
	public FinanceTypeResponse getPromotions(String productCode) throws ServiceException {
		logger.debug("Entering");

		// Mandatory validation
		if (StringUtils.isBlank(productCode)) {
			validationUtility.fieldLevelException();
		}

		FinanceTypeResponse response = new FinanceTypeResponse();

		// validate productCode
		int productCount = financeTypeService.getProductCountById(productCode);
		if (productCount == 0) {
			String[] valueParm = new String[1];
			valueParm[0] = productCode;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90222", valueParm));

			return response;
		}

		response = financeTypeController.getPromotionsByProduct(productCode);

		logger.debug("Leaving");
		return response;
	}

	@Autowired
	public void setFinanceTypeController(FinanceTypeController financeTypeController) {
		this.financeTypeController = financeTypeController;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	@Autowired
	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}
}