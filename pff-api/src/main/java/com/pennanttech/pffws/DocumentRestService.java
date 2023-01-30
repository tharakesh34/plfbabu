package com.pennanttech.pffws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.customer.DocumentList;

@Produces(MediaType.APPLICATION_JSON)
public interface DocumentRestService {
	@POST
	@Path("/documentService/addDocument")
	WSReturnStatus addDocument(DocumentDetails documentDetails);

	@POST
	@Path("/documentService/addDocuments")
	WSReturnStatus addDocuments(DocumentDetails documentDetails);

	@POST
	@Path("/documentService/getFinDocument")
	DocumentDetails getFinanceDocument(DocumentDetails documentDetails);

	@GET
	@Path("/documentService/getDocuments/{finReferance}")
	DocumentList getDocuments(@PathParam("finReferance") String finReferance) throws ServiceException;
}