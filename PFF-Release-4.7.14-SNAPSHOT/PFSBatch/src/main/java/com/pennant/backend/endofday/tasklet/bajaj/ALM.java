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

import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.alm.ALMExtarct;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.external.ALMProcess;

public class ALM implements Tasklet {
	private Logger logger = Logger.getLogger(ALM.class);
	
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
						
			logger.debug("START: ALM Process for the value date: ".concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));
			DataEngineStatus status = ALMProcess.EXTRACT_STATUS;
			status.setStatus("I");
			new Thread(new ALMProcessThread(new Long(1000))).start();;
			Thread.sleep(1000);
			BatchUtil.setExecutionStatus(context, status);

			logger.debug("COMPLETED: ALM Process for the value date: ".concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));
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
	
	public class ALMProcessThread implements Runnable {
		private long userId;
		
		public ALMProcessThread(long userId) {
			this.userId = userId;
		}

		public void run() {
			try {
				logger.debug("ALM process started...");
				ALMProcess process = new ALMExtarct(dataSource, userId, valueDate, appDate);
				process.process();
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}


}
