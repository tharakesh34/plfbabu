package com.pennanttech.pffws;

import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.financetype.FinanceTypeRequest;
import com.pennanttech.ws.model.financetype.FinanceTypeResponse;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface PromotionTypeSoapService {

	@WebResult(name = "financeType")
	public FinanceTypeResponse getPromotion(@WebParam(name = "financeType") FinanceTypeRequest finTypeReq)
			throws ServiceException;

	@WebResult(name = "stepHeader")
	public StepPolicyHeader getStepPolicy(@WebParam(name = "policyCode") String policyCode) throws ServiceException;

	@WebResult(name = "product")
	public FinanceTypeResponse getPromotions(@WebParam(name = "productCode") String productCode)
			throws ServiceException;
}
