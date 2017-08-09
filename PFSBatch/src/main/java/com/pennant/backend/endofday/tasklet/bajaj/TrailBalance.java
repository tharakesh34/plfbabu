package com.pennant.backend.endofday.tasklet.bajaj;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.services.TrailBalanceReportService;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class TrailBalance implements Tasklet {
	private Logger logger = Logger.getLogger(TrailBalance.class);

	private DataSource dataSource;
	
	@Autowired
	private EODConfigDAO eodConfigDAO;

	@Autowired
	private TrailBalanceReportService trailBalanceReportService;

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

			new TrailBalanceProcessThread(new Long(1000), trailBalanceReportService).start();
			DataEngineStatus status1 = BajajInterfaceConstants.GL_TRAIL_BALANCE_EXPORT;
			DataEngineStatus status2 = BajajInterfaceConstants.GL_TRANSACTION_SUMMARY_EXPORT;
			DataEngineStatus status3 = BajajInterfaceConstants.GL_TRAIL_BALANCE_EXPORT;
			status1.setStatus("I");
			status2.setStatus("I");
			status3.setStatus("I");

			while ("I".equals(status1.getStatus()) || "I".equals(status2.getStatus())
					|| "I".equals(status3.getStatus())) {
				BatchUtil.setExecution(context, "TOTAL", String
						.valueOf(status1.getTotalRecords() + status2.getTotalRecords() + status3.getTotalRecords()));
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(
						status1.getProcessedRecords() + status2.getProcessedRecords() + status3.getProcessedRecords()));
				
				if("F".equals(status1.getStatus()) || "F".equals(status2.getStatus())
					|| "F".equals(status3.getStatus())) {
					throw new Exception();
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

	public class TrailBalanceProcessThread extends Thread {
		private long userId;
		private TrailBalanceReportService trailBalanceReportService;

		public TrailBalanceProcessThread(long userId, TrailBalanceReportService trailBalanceReportService) {
			this.userId = userId;
			this.trailBalanceReportService = trailBalanceReportService;
		}

		public void run() {
			try {
				logger.debug("Trail Balance Request Service started...");
				this.trailBalanceReportService.generateReport(userId);
				sleep(1000);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}




}
