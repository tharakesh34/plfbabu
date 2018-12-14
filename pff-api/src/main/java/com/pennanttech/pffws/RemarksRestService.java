package com.pennanttech.pffws;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface RemarksRestService {
	
	@POST
	@Path("/remarksService/createRemarks")
	public WSReturnStatus addRemarks(List<Notes> remarks) throws ServiceException;
	
}
