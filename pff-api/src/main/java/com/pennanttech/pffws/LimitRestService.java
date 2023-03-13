package com.pennanttech.pffws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.ws.exception.ServiceException;

import jakarta.jws.WebParam;

@Produces("application/json")
public interface LimitRestService {

	@GET
	@Path("/limitService/getCustomerLimitStructure/{structureCode}")
	public LimitStructure getCustomerLimitStructure(@PathParam("structureCode") String structureCode)
			throws ServiceException;

	@POST
	@Path("/limitService/getLimitSetup")
	public LimitHeader getLimitSetup(@WebParam(name = "limitSetup") LimitHeader limitHeader) throws ServiceException;

	@POST
	@Path("/limitService/createLimitSetup")
	public LimitHeader createLimitSetup(@WebParam(name = "limitSetup") LimitHeader limitHeader) throws ServiceException;

	@POST
	@Path("/limitService/updateLimitSetup")
	public WSReturnStatus updateLimitSetup(@WebParam(name = "limitSetup") LimitHeader limitHeader)
			throws ServiceException;

	@POST
	@Path("/limitService/reserveLimit")
	public WSReturnStatus reserveLimit(@WebParam(name = "limitSetup") LimitTransactionDetail limitTransDetail)
			throws ServiceException;

	@POST
	@Path("/limitService/cancelLimitReserve")
	public WSReturnStatus cancelLimitReserve(@WebParam(name = "limitSetup") LimitTransactionDetail limitTransDetail)
			throws ServiceException;

	@POST
	@Path("/limitService/blockLimit")
	public WSReturnStatus blockLimit(@WebParam(name = "limitSetup") LimitHeader limitHeader) throws ServiceException;

	@POST
	@Path("/limitService/unBlockLimit")
	public WSReturnStatus unBlockLimit(@WebParam(name = "limitSetup") LimitHeader limitHeader) throws ServiceException;

	@POST
	@Path("/limitService/getInstitutionLimitSetup")
	public LimitHeader getInstitutionLimitSetup(@WebParam(name = "limitSetup") LimitHeader limitHeader)
			throws ServiceException;
}
