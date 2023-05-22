package com.pennant.backend.endofday.tasklet.bajaj;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.pennanttech.pennapps.pff.external.controldump.ControlDumpExtract;
import com.pennanttech.pff.external.ControlDumpProcess;

public class ControlDump implements Tasklet {
	private Logger logger = LogManager.getLogger(ControlDump.class);

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
			logger.debug("START: Control-Dump Process for the value date: "
					.concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));

			DataEngineStatus status = ControlDumpExtract.EXTRACT_STATUS;
			status.setStatus("I");
			new Thread(new ControlDumpProcessThread(1000L)).start();
			Thread.sleep(1000);
			BatchUtil.setExecutionStatus(context, status);

			logger.debug("COMPLETED: ontrol-Dump Process for the value date: "
					.concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));
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

	public class ControlDumpProcessThread implements Runnable {
		private long userId;

		public ControlDumpProcessThread(long userId) {
			this.userId = userId;
		}

		public void run() {
			try {
				logger.debug("Control Dump Request Service started...");
				ControlDumpProcess process = new ControlDumpExtract(dataSource, userId, valueDate, appDate);
				process.process();
				TimeUnit.SECONDS.sleep(1);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}
}
