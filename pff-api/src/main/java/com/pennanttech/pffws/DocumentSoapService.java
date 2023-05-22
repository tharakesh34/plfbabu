package com.pennanttech.pffws;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.ws.model.customer.DocumentList;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface DocumentSoapService {

	@WebResult(name = "document")
	WSReturnStatus addDocument(@WebParam(name = "document") DocumentDetails documentDetails);

	@WebResult(name = "document")
	WSReturnStatus addDocuments(@WebParam(name = "document") DocumentDetails documentDetails);

	@WebResult(name = "document")
	DocumentDetails getFinanceDocument(@WebParam(name = "document") DocumentDetails documentDetails);

	@WebResult(name = "finReferance")
	DocumentList getDocuments(@WebParam(name = "finReferance") String finReferance);

}
