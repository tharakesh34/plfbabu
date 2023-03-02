package com.pennanttech.pffws;

import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;
import com.pennanttech.ws.model.disbursement.DisbursementRequestDetail;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface DisbursementSOAPService {

	@WebResult(name = "Disbursement")
	public DisbursementRequestDetail getDisbursementInstructions(FinAdvancePayments fap) throws ServiceException;

	public DisbursementRequestDetail downloadDisbursementInstructions(
			@WebParam(name = "finAdvancePayments") List<FinAdvancePayments> fap) throws ServiceException;

	public WSReturnStatus updateDisbursementInstructionStatus(
			@WebParam(name = "disbRequest") List<DisbursementRequest> disbRequest) throws ServiceException;
}
