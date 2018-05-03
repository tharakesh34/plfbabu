package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.financetype.FinanceInquiry;

@WebService
public interface CreateFinanceSoapService {

	@WebResult(name = "finance")
	public FinanceDetail createFinance(@WebParam(name = "finance") FinanceDetail financeDetail) throws ServiceException;
	
	@WebResult(name = "finance")
	public FinanceDetail createFinanceWithWIF(@WebParam(name = "finance") FinanceDetail financeDetail) throws ServiceException;
	
	@WebResult(name = "finReference")
	public FinanceDetail getFinanceDetails(@WebParam(name ="finReference") String finReference) throws ServiceException;
	
	@WebResult(name = "finance")
	public FinanceDetail getFinInquiryDetails(@WebParam(name ="finReference") String finReference) throws ServiceException;
	
	@WebResult(name = "finance")
	public FinanceInquiry getFinanceWithCustomer(@WebParam(name ="cif") String custCif) throws ServiceException;
	
	@WebResult(name = "finance")
	public FinanceInquiry getFinanceWithCollateral(@WebParam(name ="collateralRef") String collateralRef) throws ServiceException;
	
	@WebResult(name = "finance")
	public WSReturnStatus updateFinance(@WebParam(name = "finance") FinanceDetail financeDetail) throws ServiceException;
}
