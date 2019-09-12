package com.pennanttech.pff.external;

import java.util.List;

import com.pennant.backend.model.collateral.CollateralSetup;

public interface GlemCollateralProcess {
	String processDownload(List<CollateralSetup> collateralSetups) throws Exception;
}
