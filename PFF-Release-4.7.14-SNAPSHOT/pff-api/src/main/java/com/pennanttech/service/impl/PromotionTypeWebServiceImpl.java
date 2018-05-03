package com.pennanttech.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.FinanceConstants;
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

	private static final Logger logger = Logger.getLogger(PromotionTypeWebServiceImpl.class);

	private FinanceTypeController financeTypeController;
	private ValidationUtility validationUtility;
	private StepPolicyService stepPolicyService;
	private PromotionService promotionService;

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
			if (StringUtils.isBlank(finTypeReq.getPromotionType())) {
				validationUtility.fieldLevelException();
			}

			//for failure case logging purpose
			APIErrorHandlerService.logReference(finTypeReq.getPromotionType());

			// validate financeType
			String promotionCode = finTypeReq.getPromotionType();
			Promotion promotion = promotionService.getApprovedPromotionById(promotionCode,
					FinanceConstants.MODULEID_PROMOTION, false);

			FinanceTypeResponse response = new FinanceTypeResponse();
			if (promotion != null && promotion.isActive()) {
				finTypeReq.setFinType(promotion.getFinType());
				response = financeTypeController.getFinanceTypeDetails(finTypeReq, true);
				response.setPromotionDesc(promotion.getPromotionDesc());
				response.setStartDate(promotion.getStartDate());
				response.setEndDate(promotion.getEndDate());
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = promotionCode;
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
		//for logging purpose
		APIErrorHandlerService.logReference(policyCode);	

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
	 * @param finType
	 */
	@Override
	public FinanceTypeResponse getPromotions(String finType) throws ServiceException {
		logger.debug("Entering");

		// Mandatory validation
		if (StringUtils.isBlank(finType)) {
			validationUtility.fieldLevelException();
		}
		//for logging purpose
		APIErrorHandlerService.logReference(finType);	

		FinanceTypeResponse response = new FinanceTypeResponse();

		// validate productCode
		int financeTypeCount = promotionService.getFinanceTypeCountById(finType);
		if (financeTypeCount == 0) {
			String[] valueParm = new String[1];
			valueParm[0] = finType;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90222", valueParm));
			return response;
		}

		response = financeTypeController.getPromotionsByFinType(finType);

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
	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}
	@Autowired
	public void setPromotionService(PromotionService promotionService) {
		this.promotionService = promotionService;
	}
}