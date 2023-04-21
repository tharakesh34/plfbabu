package com.pennanttech.external.ucic.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.ucic.dao.ExtUcicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicDataExtractor {
	private static final Logger logger = LogManager.getLogger(ExtUcicDataExtractor.class);

	private ExtUcicDao extUcicDao;

	public String extractCustomerData() {
		logger.debug(Literal.ENTERING);
		String resp = extUcicDao.executeDataExtractionFromSP();
		if (resp != null && "SUCCESS".equals(resp)) {
			logger.debug("Successfully extracted customers data.");
		}

		logger.debug(Literal.LEAVING);
		return resp;
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

}
