package com.pennanttech.pffws;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.mandate.MandateDetial;

@Produces("application/json")
public interface MandateRestService {

	@GET
	@Path("/mandateService/getMandate/{mandateID}")
	public Mandate getMandate(@PathParam("mandateID") long mandateID) throws ServiceException;

	@POST
	@Path("/mandateService/createMandate")
	public Mandate createMandate(Mandate mandate) throws ServiceException;

	@POST
	@Path("/mandateService/updateMandate")
	public WSReturnStatus updateMandate(Mandate mandate) throws ServiceException;

	@DELETE
	@Path("/mandateService/deleteMandate/{mandateID}")
	public WSReturnStatus deleteMandate(@PathParam("mandateID") long mandateID) throws ServiceException;

	@GET
	@Path("/mandateService/getMandates/{cif}")
	public MandateDetial getMandates(@PathParam("cif") String cif) throws ServiceException;

	@POST
	@Path("/mandateService/loanMandateSwapping")
	public WSReturnStatus loanMandateSwapping(MandateDetial mandate) throws ServiceException;

}
