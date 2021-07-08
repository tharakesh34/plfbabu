package com.pennanttech.pffws;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.pff.model.subvention.SubventionHeader;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.finance.DisbRequest;
import com.pennanttech.ws.model.finance.FinAdvPaymentDetail;

@WebService
public interface FinServiceInstSOAPService {

	@WebResult(name = "finance")
	public FinanceDetail addRateChange(@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest)
			throws ServiceException;

	@WebResult(name = "finance")
	public FinanceDetail changeRepayAmt(@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest)
			throws ServiceException;

	@WebResult(name = "finance")
	public FinanceDetail deferments(@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest)
			throws ServiceException;

	@WebResult(name = "finance")
	public FinanceDetail addTerms(@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest)
			throws ServiceException;

	@WebResult(name = "finance")
	public FinanceDetail manualPayment(@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest)
			throws ServiceException;

	@WebResult(name = "finance")
	public FinanceDetail removeTerms(@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest)
			throws ServiceException;

	public FinanceTaxDetail fetchGSTDetails(@WebParam(name = "finReference") String finReference)
			throws ServiceException;

	public WSReturnStatus addGSTDetails(@WebParam(name = "financeTaxDetail") FinanceTaxDetail financeTaxDetail)
			throws ServiceException;

	public WSReturnStatus updateGSTDetails(@WebParam(name = "financeTaxDetail") FinanceTaxDetail financeTaxDetail)
			throws ServiceException;

	@WebResult(name = "finance")
	public FinAdvPaymentDetail getDisbursmentDetails(@WebParam(name = "finReference") String finReference)
			throws ServiceException;

	@WebResult(name = "finance")
	public WSReturnStatus approveDisbursementResponse(DisbRequest disbRequest) throws ServiceException;

	@WebResult(name = "finance")
	public WSReturnStatus cancelDisbursementInstructions(
			@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@WebResult(name = "finance")
	public FinanceDetail partCancellation(@WebParam(name = "finance") FinServiceInstruction finServiceInstruction)
			throws ServiceException;

	public WSReturnStatus subventionKnockOff(SubventionHeader subventionHead) throws ServiceException;
}
