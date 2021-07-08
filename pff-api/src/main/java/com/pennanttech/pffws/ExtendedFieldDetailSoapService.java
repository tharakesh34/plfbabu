package com.pennanttech.pffws;

import jakarta.jws.WebResult;
import jakarta.jws.WebService;

import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.ws.exception.ServiceException;

@WebService
public interface ExtendedFieldDetailSoapService {

	@WebResult(name = "extendedDetail")
	public ExtendedFieldHeader getExtendedFieldDetails(ExtendedFieldHeader extendedFieldHeader) throws ServiceException;
}
