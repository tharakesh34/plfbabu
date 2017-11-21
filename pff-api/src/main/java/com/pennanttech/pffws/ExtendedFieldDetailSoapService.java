package com.pennanttech.pffws;

import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.backend.model.extendedfields.ExtendedFieldHeader;
import com.pennant.ws.exception.ServiceException;

@WebService
public interface ExtendedFieldDetailSoapService {

	@WebResult(name = "extendedDetail")
	public ExtendedFieldHeader getExtendedFieldDetails(ExtendedFieldHeader extendedFieldHeader) throws ServiceException;
}
