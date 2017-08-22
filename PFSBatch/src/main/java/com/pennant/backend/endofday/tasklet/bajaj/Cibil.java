package com.pennant.backend.endofday.tasklet.bajaj;

import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.reports.cibil.CIBILReport;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class Cibil implements Tasklet {
	private Logger logger = Logger.getLogger(Cibil.class);
	
	private Date valueDate;
	private DataSource dataSource;
	
	@Autowired
	private EODConfigDAO eodConfigDAO;


	@Autowired
	private CIBILReport cibilReport;


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
		
		try {			
			logger.debug("START: CIBIL Process for the value date: ".concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));
			
			DataEngineStatus status = CIBILReport.EXTRACT_STATUS;
			status.setStatus("I");
			new Thread(new CIBILProcessThread(cibilReport)).start();
			Thread.sleep(1000);
			BatchUtil.setExecutionStatus(context, status);
			
			logger.debug("COMPLETED: CIBIL Process for the value date: ".concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));
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

	public class CIBILProcessThread implements Runnable {
		private CIBILReport cibilReport;

		public CIBILProcessThread(CIBILReport cibilReport) {
			this.cibilReport = cibilReport;
		}

		public void run() {
			try {
				logger.debug("Control Dump Request Service started...");
				cibilReport.generateReport();
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}
	
}
