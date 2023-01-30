package com.pennanttech.pffws;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.collateral.CollateralDetail;

@Produces("application/json")
public interface CollateralRestService {

	@GET
	@Path("/collateralService/getCollateralType/{collateralType}")
	public CollateralStructure getCollateralType(@PathParam("collateralType") String collateralType)
			throws ServiceException;

	@POST
	@Path("/collateralService/createCollateral")
	public CollateralSetup createCollateral(CollateralSetup collateralSetup) throws ServiceException;

	@POST
	@Path("/collateralService/updateCollateral")
	public WSReturnStatus updateCollateral(CollateralSetup collateralSetup) throws ServiceException;

	@DELETE
	@Path("/collateralService/deleteCollateral")
	public WSReturnStatus deleteCollateral(CollateralSetup collateralSetup) throws ServiceException;

	@GET
	@Path("/collateralService/getCollaterals/{cif}")
	public CollateralDetail getCollaterals(@PathParam("cif") String cif) throws ServiceException;

	@POST
	@Path("/collateralService/pendingUpdateCollateral")
	public WSReturnStatus pendingUpdateCollateral(CollateralSetup collateralSetup) throws ServiceException;

	@GET
	@Path("/collateralService/getPendingCollaterals/{cif}")
	public CollateralDetail getPendingCollaterals(@PathParam("cif") String cif) throws ServiceException;

}
