package com.pennanttech.pffws;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.remark.RemarksResponse;

@Produces("application/json")
public interface RemarksRestService {

	@POST
	@Path("/remarksService/createRemarks")
	WSReturnStatus addRemarks(List<Notes> remarks) throws ServiceException;

	@GET
	@Path("/remarksService/getRemarks/{finReference}")
	RemarksResponse getRemarks(@PathParam("finReference") String finReference) throws ServiceException;

}
