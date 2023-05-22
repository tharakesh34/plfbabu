package com.pennanttech.pffws;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.collateral.CollateralDetail;

import jakarta.jws.WebParam;
import jakarta.jws.WebService;

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

	public CollateralDetail getCollaterals(@WebParam(name = "cif") String cif) throws ServiceException;

	public WSReturnStatus pendingUpdateCollateral(@WebParam(name = "collateral") CollateralSetup collateralSetup)
			throws ServiceException;

	public CollateralDetail getPendingCollaterals(@WebParam(name = "cif") String cif) throws ServiceException;

}
