package com.pennanttech.pffws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface DealerRestService {
	@GET
	@Path("/dealerService/getDealer/{dealerId}")
	public VehicleDealer getDealer(@PathParam("dealerId") long dealerId) throws ServiceException;

	@POST
	@Path("/dealerService/createDealer")
	public VehicleDealer createDealer(VehicleDealer vehicleDealer) throws ServiceException;
}
