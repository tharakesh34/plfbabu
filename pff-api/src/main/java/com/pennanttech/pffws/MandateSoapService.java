package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.mandate.MandateDetial;

@WebService
public interface MandateSoapService {
	
	public Mandate getMandate(@WebParam(name = "mandateID") long mandateID)throws ServiceException;

	public Mandate createMandate(@WebParam(name = "mandate") Mandate mandate)throws ServiceException;

	public WSReturnStatus updateMandate(@WebParam(name = "mandate") Mandate mandate)throws ServiceException;

	public WSReturnStatus deleteMandate(@WebParam(name = "mandateID") long mandateID)throws ServiceException;

	public MandateDetial getMandates(@WebParam(name = "cif") String cif)throws ServiceException;

	public WSReturnStatus loanMandateSwapping(@WebParam(name = "mandate") MandateDetial mandate)throws ServiceException;

}
