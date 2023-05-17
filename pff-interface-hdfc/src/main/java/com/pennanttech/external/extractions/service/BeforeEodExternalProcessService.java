package com.pennanttech.external.extractions.service;

import java.util.Date;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.BeforeEodExternalProcessHook;
import com.pennanttech.external.app.config.dao.ExtStagingDao;
import com.pennanttech.pennapps.core.util.DateUtil;

public class BeforeEodExternalProcessService implements BeforeEodExternalProcessHook {
	private ExtStagingDao extStageDao;

	@Override
	public void truncateExtractionTables() {

		Date appDate = SysParamUtil.getAppDate();

		if (appDate.compareTo(DateUtil.getMonthEnd(appDate)) == 0) {
			extStageDao.truncateTable("EXT_BASEL_ONE");
			extStageDao.truncateTable("ALM_REPORT");
			extStageDao.truncateTable("BASELTWO");
			extStageDao.truncateTable("RPMSEXTRACT");
		}

	}

	public void setExtStageDao(ExtStagingDao extStageDao) {
		this.extStageDao = extStageDao;
	}
}
