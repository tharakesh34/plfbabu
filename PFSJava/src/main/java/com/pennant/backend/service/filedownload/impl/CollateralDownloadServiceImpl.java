package com.pennant.backend.service.filedownload.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.filedownload.CollateralDownloadService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.GlemCollateralProcess;

public class CollateralDownloadServiceImpl implements CollateralDownloadService {

	private static final Logger logger = LogManager.getLogger(CollateralDownloadServiceImpl.class);

	@Autowired
	private CollateralSetupService collateralSetupService;
	@Autowired(required = false)
	private GlemCollateralProcess glemsCollateralProcess;

	@Override
	public String processDownload(List<String> collateralRef) throws Exception {
		logger.debug(Literal.ENTERING);

		List<CollateralSetup> collateralSetups = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(collateralRef)) {
			for (String reference : collateralRef) {
				collateralSetups.add(collateralSetupService.getCollateralSetupByRef(reference, "", false));
			}
		}
		if (CollectionUtils.isNotEmpty(collateralSetups)) {
			return glemsCollateralProcess.processDownload(collateralSetups);
		}
		logger.debug(Literal.LEAVING);
		return "";
	}

}
