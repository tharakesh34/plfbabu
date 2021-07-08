package com.pennanttech.pffws;

import jakarta.jws.WebResult;
import jakarta.jws.WebService;

import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.systemDate.SystemDate;

@WebService
public interface SystemDateSoapService {

	@WebResult(name = "systemDate")
	public SystemDate getSystemDate() throws ServiceException;
}
