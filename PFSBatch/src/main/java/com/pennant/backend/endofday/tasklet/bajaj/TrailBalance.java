package com.pennant.backend.endofday.tasklet.bajaj;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.bajaj.process.TrailBalanceEngine;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
			if (!monthEnd) {
				return RepeatStatus.FINISHED;
			}
			
			logger.debug("START: Trial-Balance Process for the value date: ".concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));
			
			DataEngineStatus status = TrailBalanceEngine.EXTRACT_STATUS;
			status.setStatus("I");
			new Thread(new TrailBalanceProcessThread(new Long(1000))).start();
			Thread.sleep(1000);
			BatchUtil.setExecutionStatus(context, status);
			
			logger.debug("START: Trial-Balance Process for the value date: ".concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));
		
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

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

	public class TrailBalanceProcessThread implements Runnable {
		private long userId;

		public TrailBalanceProcessThread(long userId) {
			this.userId = userId;
		}

		public void run() {
			try {
				logger.debug("Trail Balance Request Service started...");
				new TrailBalanceEngine(dataSource, userId, DateUtility.getAppValueDate(), DateUtility.getAppDate()).extractReport();
				TimeUnit.SECONDS.sleep(1);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}




}
