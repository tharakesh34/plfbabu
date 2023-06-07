package com.pennanttech.external.extractions.service.micro;

import java.util.Date;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.BeforeEodExternalProcessHook;
import com.pennanttech.external.extractions.dao.ExtExtractionDao;
import com.pennanttech.pennapps.core.util.DateUtil;

public class BeforeEodExternalProcessService implements BeforeEodExternalProcessHook {
	private ExtExtractionDao extExtractionDao;

	@Override
	public void truncateExtractionTables() {

		Date appDate = SysParamUtil.getAppDate();

		if (appDate.compareTo(DateUtil.getMonthEnd(appDate)) == 0) {
			extExtractionDao.truncateStageTable("EXT_BASEL_ONE");
			extExtractionDao.truncateStageTable("ALM_REPORT");
			extExtractionDao.truncateStageTable("BASELTWO");
			extExtractionDao.truncateStageTable("RPMSEXTRACT");
		}
	}

	public void setExtExtractionDao(ExtExtractionDao extExtractionDao) {
		this.extExtractionDao = extExtractionDao;
	}
}
