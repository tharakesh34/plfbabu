package com.pennanttech.service;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.collateral.CollateralDetail;

public interface CollateralWebService {

	public CollateralStructure getCollateralType(String collateralType) throws ServiceException;

	public CollateralSetup createCollateral(CollateralSetup collateralSetup) throws ServiceException;

	public WSReturnStatus updateCollateral(CollateralSetup collateralSetup) throws ServiceException;

	public WSReturnStatus deleteCollateral(CollateralSetup collateralSetup) throws ServiceException;

	public CollateralDetail getCollaterals(String cif) throws ServiceException;
}
