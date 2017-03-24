package com.pennanttech.service;

import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.financetype.FinanceTypeRequest;
import com.pennanttech.ws.model.financetype.FinanceTypeResponse;
import com.pennanttech.ws.model.financetype.ProductType;

public interface FinanceTypeWebService {

	public FinanceTypeResponse getFinanceTypeDetails(FinanceTypeRequest finTypeRequest) throws ServiceException;
	
	public StepPolicyHeader getStepPolicyDetails(String policyCode) throws ServiceException;
	
	public ProductType getLoanTypes(String productCode) throws ServiceException;
	
}
