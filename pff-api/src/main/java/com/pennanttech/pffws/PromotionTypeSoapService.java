package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.financetype.FinanceTypeRequest;
import com.pennanttech.ws.model.financetype.FinanceTypeResponse;

@WebService
public interface PromotionTypeSoapService {

	@WebResult(name = "financeType")
	public FinanceTypeResponse getPromotion(@WebParam(name = "financeType") FinanceTypeRequest finTypeReq) throws ServiceException;
	
	@WebResult(name = "stepHeader")
	public StepPolicyHeader getStepPolicy(@WebParam(name = "policyCode") String policyCode) throws ServiceException;
	
	@WebResult(name = "product")
	public FinanceTypeResponse getPromotions(@WebParam(name = "productCode") String productCode) throws ServiceException;
}
