package com.pennanttech.pffws;

import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.financetype.FinanceTypeRequest;
import com.pennanttech.ws.model.financetype.FinanceTypeResponse;
import com.pennanttech.ws.model.financetype.ProductType;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface FinanceTypeSoapService {

	@WebResult(name = "financeType")
	public FinanceTypeResponse getFinanceTypeDetails(@WebParam(name = "financeType") FinanceTypeRequest finTypeReq)
			throws ServiceException;

	@WebResult(name = "stepHeader")
	public StepPolicyHeader getStepPolicyDetails(@WebParam(name = "policyCode") String policyCode)
			throws ServiceException;

	@WebResult(name = "product")
	public ProductType getLoanTypes(@WebParam(name = "productCode") String productCode) throws ServiceException;
}
