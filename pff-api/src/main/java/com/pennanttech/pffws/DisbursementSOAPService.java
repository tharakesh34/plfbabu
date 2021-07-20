package com.pennanttech.pffws;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;
import com.pennanttech.ws.model.disbursement.DisbursementRequestDetail;

@WebService
public interface DisbursementSOAPService {

	@WebResult(name = "Disbursement")
	public DisbursementRequestDetail getDisbursementInstructions(FinAdvancePayments fap) throws ServiceException;

	public DisbursementRequestDetail downloadDisbursementInstructions(
			@WebParam(name = "finAdvancePayments") List<FinAdvancePayments> fap) throws ServiceException;

	public WSReturnStatus updateDisbursementInstructionStatus(
			@WebParam(name = "disbRequest") List<DisbursementRequest> disbRequest) throws ServiceException;
}
