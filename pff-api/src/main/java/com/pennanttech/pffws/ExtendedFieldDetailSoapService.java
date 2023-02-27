package com.pennanttech.pffws;

import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.ws.exception.ServiceException;

import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface ExtendedFieldDetailSoapService {

	@WebResult(name = "extendedDetail")
	public ExtendedFieldHeader getExtendedFieldDetails(ExtendedFieldHeader extendedFieldHeader) throws ServiceException;
}
