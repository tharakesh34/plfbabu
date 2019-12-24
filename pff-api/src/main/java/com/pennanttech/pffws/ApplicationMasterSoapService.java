package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.backend.model.applicationmaster.ReasonCodeResponse;
import com.pennant.ws.exception.ServiceException;

@WebService
public interface ApplicationMasterSoapService {

	ReasonCodeResponse getReasonCodeDetails(@WebParam(name = "reasonTypeCode") String reasonTypeCode)
			throws ServiceException;

}
