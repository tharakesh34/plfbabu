package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.documentdetails.DocumentDetails;

@Produces(MediaType.APPLICATION_JSON)
public interface DocumentRestService {

	@POST
	@Path("/documentService/addDocument")
	WSReturnStatus addDocument(DocumentDetails documentDetails);

	@POST
	@Path("/documentService/getFinDocument")
	DocumentDetails getFinanceDocument(DocumentDetails documentDetails);

}