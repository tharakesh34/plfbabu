package com.pennanttech.pffws;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;
import com.pennanttech.ws.model.disbursement.DisbursementRequestDetail;

@Produces(MediaType.APPLICATION_JSON)
public interface DisbursementRESTService {

	// FIXME <<disbParty>> the field needs to be included in API Specification

	@POST
	@Path("/disbursementService/getDisbursementInstructions")
	public DisbursementRequestDetail getDisbursementInstructions(FinAdvancePayments finAdvancePayments)
			throws ServiceException;

	@POST
	@Path("/disbursementService/downloadDisbursementInstructions")
	public DisbursementRequestDetail downloadDisbursementInstructions(List<FinAdvancePayments> finAdvancePayments)
			throws ServiceException;

	@POST
	@Path("/disbursementService/updateDisbursementInstructionStatus")
	public WSReturnStatus updateDisbursementInstructionStatus(List<DisbursementRequest> disbRequest)
			throws ServiceException;

}
