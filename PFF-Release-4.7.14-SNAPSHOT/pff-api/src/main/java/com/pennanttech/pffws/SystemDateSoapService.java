package com.pennanttech.pffws;

import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.systemDate.SystemDate;

@WebService
public interface SystemDateSoapService {

	@WebResult(name = "systemDate")
	public SystemDate getSystemDate() throws ServiceException;
}
