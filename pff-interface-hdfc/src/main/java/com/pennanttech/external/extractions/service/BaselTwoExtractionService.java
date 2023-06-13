package com.pennanttech.external.extractions.service;

import com.pennanttech.external.app.config.dao.ExternalDao;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;

public class BaselTwoExtractionService implements ExtIntfConfigConstants {
	private ExternalDao externalDao;

	public void processExtraction() {
		externalDao.executeSP(SP_BASEL_TWO);

	}

	public void setExternalDao(ExternalDao externalDao) {
		this.externalDao = externalDao;
	}

}
