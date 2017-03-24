package com.pennanttech.service;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.mandate.MandateDetial;

public interface MandateWebService {
	
	public Mandate getMandate(long mandateID) throws ServiceException;

	public Mandate createMandate(Mandate mandate) throws ServiceException, PFFInterfaceException;

	public WSReturnStatus updateMandate(Mandate mandate) throws ServiceException;

	public WSReturnStatus deleteMandate(long mandateID) throws ServiceException;

	public MandateDetial getMandates(String cif) throws ServiceException;

	public WSReturnStatus loanMandateSwapping(MandateDetial mandate) throws ServiceException;

}
