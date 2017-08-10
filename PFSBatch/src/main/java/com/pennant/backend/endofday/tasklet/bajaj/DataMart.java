package com.pennant.backend.endofday.tasklet.bajaj;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.bajaj.process.DataMartRequestProcess;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;

public class DataMart implements Tasklet {
	private Logger logger = Logger.getLogger(DataMart.class);

	private DataSource dataSource;
	
	@Autowired
	private EODConfigDAO eodConfigDAO;
	
	public EODConfig getEodConfig() {
		try {
			List<EODConfig> list = eodConfigDAO.getEODConfig();
			if (!list.isEmpty()) {
				return list.get(0);
			}

		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return null;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Data Extract Preparation On : " + valueDate);

		try {
			
			boolean monthEnd = false;
			int amzPostingEvent = SysParamUtil.getValueAsInt(AccountConstants.AMZ_POSTING_EVENT);
			if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_MTH_END) {
				if (valueDate.compareTo(DateUtility.getMonthEnd(valueDate)) == 0) {
					monthEnd = true;
				}
			} else if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_EXT_MTH_END) {
				if (getEodConfig() != null && getEodConfig().isInExtMnth()) {
					if (getEodConfig().getMnthExtTo().compareTo(valueDate) == 0) {
						monthEnd = true;
					}
				}

			}
			// if month end then only it should run
			if (monthEnd) {

			}
			
			DataEngineStatus status = BajajInterfaceConstants.DATA_MART_STATUS;
			DataMartRequestProcess requestProcess = new DataMartRequestProcess(dataSource, new Long(1000), DateUtility.getAppValueDate(), DateUtility.getAppDate());
			requestProcess.process("DATA_MART_REQUEST");
			
			while (DataMartRequestProcess.running) {
				DataMartProcessThread.sleep(2000);
				BatchUtil.setExecution(context, "TOTAL", String.valueOf(status.getTotalRecords()));
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(status.getProcessedRecords()));

				if ("F".equals(status.getStatus())) {
					throw new Exception(status.getRemarks());
				}
			}
			
		} catch (Exception e) {
			logger.error("Exception", e);
		}

		logger.debug("COMPLETE: Data Extract Preparation On :" + valueDate);
		return RepeatStatus.FINISHED;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	
	public class DataMartProcessThread extends Thread {
		private long userId;

		public DataMartProcessThread(long userId) {
			this.userId = userId;
		}

		public void run() {
			try {
				logger.debug("DataMart Request Service started...");
				sleep(5000);
				DataMartRequestProcess requestProcess = new DataMartRequestProcess(dataSource, userId, DateUtility.getAppValueDate(), DateUtility.getAppDate());
				requestProcess.process("DATA_MART_REQUEST");
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	
}
