package com.pennanttech.service;

import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.financetype.FinanceTypeRequest;
import com.pennanttech.ws.model.financetype.FinanceTypeResponse;

public interface PromotionTypeWebService {

	public FinanceTypeResponse getPromotion(FinanceTypeRequest finTypeRequest) throws ServiceException;
	
	public StepPolicyHeader getStepPolicy(String policyCode) throws ServiceException;
	
	public FinanceTypeResponse getPromotions(String productCode) throws ServiceException;
	
}
