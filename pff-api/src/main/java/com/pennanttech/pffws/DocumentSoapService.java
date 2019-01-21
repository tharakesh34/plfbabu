package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.documentdetails.DocumentDetails;

@WebService
public interface DocumentSoapService {

	@WebResult(name = "document")
	WSReturnStatus addDocument(@WebParam(name = "document") DocumentDetails documentDetails);

}
