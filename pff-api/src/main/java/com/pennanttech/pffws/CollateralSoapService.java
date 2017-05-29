package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.collateral.CollateralDetail;

@WebService
public interface CollateralSoapService {

	public CollateralStructure getCollateralType(@WebParam(name = "collateralType") String collateralType)
			throws ServiceException;

	public CollateralSetup createCollateral(@WebParam(name = "collateral") CollateralSetup collateralSetup)
			throws ServiceException;

	public WSReturnStatus updateCollateral(@WebParam(name = "collateral") CollateralSetup collateralSetup)
			throws ServiceException;

	public WSReturnStatus deleteCollateral(@WebParam(name = "collateral") CollateralSetup collateralSetup)
			throws ServiceException;

	public CollateralDetail getCollaterals(@WebParam(name = "cif") String cif)
			throws ServiceException;

}
