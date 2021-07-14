package com.pennanttech.pffws;

import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.systemDate.SystemDate;

import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface SystemDateSoapService {

	@WebResult(name = "systemDate")
	public SystemDate getSystemDate() throws ServiceException;
}
