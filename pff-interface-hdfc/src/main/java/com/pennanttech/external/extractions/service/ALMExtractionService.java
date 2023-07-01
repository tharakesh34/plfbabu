package com.pennanttech.external.extractions.service;

import com.pennanttech.external.app.config.dao.ExternalDao;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;

public class ALMExtractionService implements ExtIntfConfigConstants {

	private ExternalDao externalDao;

	public void processExtraction() {

		externalDao.executeSP(SP_ALM_REPORT);

	}

	public void setExternalDao(ExternalDao externalDao) {
		this.externalDao = externalDao;
	}

}
