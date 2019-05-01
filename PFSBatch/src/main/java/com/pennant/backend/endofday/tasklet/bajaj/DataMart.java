package com.pennant.backend.endofday.tasklet.bajaj;

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

import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.external.DataMartProcess;
import com.pennanttech.pff.external.datamart.DataMartExtarct;

public class DataMart implements Tasklet {
	private Logger logger = Logger.getLogger(DataMart.class);

	private Date valueDate;
	private Date appDate;
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
		valueDate = (Date) context.getStepContext().getJobExecutionContext().get("APP_VALUEDATE");
		appDate = (Date) context.getStepContext().getJobExecutionContext().get("APP_DATE");

		try {
			logger.debug("START: Data-Mart Process for the value date: "
					.concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));

			DataEngineStatus status = DataMartExtarct.EXTRACT_STATUS;
			status.setStatus("I");
			new Thread(new DataMartProcessThread(1000)).start();
			Thread.sleep(1000);
			BatchUtil.setExecutionStatus(context, status);

			logger.debug("Eompleted: Data-Mart Process for the value date: "
					.concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

		return RepeatStatus.FINISHED;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public class DataMartProcessThread implements Runnable {
		private long userId;

		public DataMartProcessThread(long userId) {
			this.userId = userId;
		}

		public void run() {
			try {
				logger.debug("DataMart Request Service started...");
				DataMartProcess dataMart = new DataMartExtarct(dataSource, userId, valueDate, appDate);
				dataMart.process();
				TimeUnit.SECONDS.sleep(1);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

}
