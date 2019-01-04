package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.ws.exception.ServiceException;

@WebService
public interface MiscellaneousSoapService {
	
	public WSReturnStatus createFinancePosting(@WebParam(name ="posting") JVPosting posting) throws ServiceException;

}
