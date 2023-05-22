package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface PaymentInstructionRestService {

	@POST
	@Path("/PaymentInstructionService/createPaymentInstruction")
	public WSReturnStatus createPaymentInstruction(PaymentHeader paymentHeader) throws ServiceException;

	@POST
	@Path("/PaymentInstructionService/getPaymentInstruction")
	public PaymentHeader getPaymentInstruction(PaymentHeader paymentHeader) throws ServiceException;

	@POST
	@Path("/PaymentInstructionService/updatePaymentInstruction")
	public WSReturnStatus updatePaymentInstruction(PaymentHeader paymentHeader) throws ServiceException;
}
