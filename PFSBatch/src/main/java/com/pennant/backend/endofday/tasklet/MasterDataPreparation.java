package com.pennant.backend.endofday.tasklet;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.eod.ExtractDataExecution;
import com.pennanttech.pennapps.core.resource.Literal;

public class MasterDataPreparation implements Tasklet {
	private Logger logger = Logger.getLogger(MasterDataPreparation.class);

	private ExtractDataExecution extractData;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		//Return if extraction is not required
		if (!ImplementationConstants.ALW_MASTER_DATA_EXTRACTION) {
			return RepeatStatus.FINISHED;
		}
		//current date starting from midnight
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		Timestamp currentTime = new Timestamp(cal.getTimeInMillis());
		if (ImplementationConstants.IS_DATA_SYNC_REQ_BY_APP_DATE) {
			//getting last business date, since app date is updated to next business day(ex: EOD on 1-1-2020 then Appdate is updated as 2-1-2020)
			currentTime = DateUtility.getTimestamp(SysParamUtil.getLastBusinessdate());
		}
		logger.debug("START: Master Data preparation : " + currentTime);
		try {
			getExtractData().processExtractMasterDetails(currentTime);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + " While executing master data extraction");
		}
		logger.debug("COMPLETE: Master Data preparation :" + currentTime);
		return RepeatStatus.FINISHED;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public ExtractDataExecution getExtractData() {
		return extractData;
	}

	public void setExtractData(ExtractDataExecution extractData) {
		this.extractData = extractData;
	}
}
