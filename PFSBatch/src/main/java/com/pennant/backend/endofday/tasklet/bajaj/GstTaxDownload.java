package com.pennant.backend.endofday.tasklet.bajaj;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.bajaj.process.TaxDownlaodProcess;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class GstTaxDownload implements Tasklet {
	private Logger						logger	= Logger.getLogger(GstTaxDownload.class);

	private DataSource					dataSource;
	
	@Autowired
	private EODConfigDAO				eodConfigDAO;

	public EODConfig getEodConfig() {
		try {
			List<EODConfig> list = eodConfigDAO.getEODConfig();
			if (!list.isEmpty()) {
				return list.get(0);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return null;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		try {
			logger.debug("START: GST-TAX Download Process for the value date: ".concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));
			
			DataEngineStatus status = TaxDownlaodProcess.EXTRACT_STATUS;
			status.setStatus("I");
			new Thread(new GSTTaxProcessThread(new Long(1000))).start();
			Thread.sleep(1000);
			BatchUtil.setExecutionStatus(context, status);
			
			logger.debug("COMPLETED: GST-TAX Download Process for the value date: ".concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));
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
	
	public class GSTTaxProcessThread implements Runnable {
		private long userId;

		public GSTTaxProcessThread(long userId) {
			this.userId = userId;
		}

		public void run() {
			try {
				Date appDate = DateUtility.getAppDate();
				Date monthEndDate = DateUtility.getMonthEndDate(appDate);
				
				String isDailyDownlaod = SysParamUtil.getValueAsString("GST_TAXDETAIL_DOWNLOAD");
				TaxDownlaodProcess process = null;
				if (StringUtils.equalsIgnoreCase("Y", isDailyDownlaod)) {
					process = new TaxDownlaodProcess(dataSource, userId, appDate, appDate, appDate);
				} else if (DateUtility.compare(appDate, monthEndDate) == 0) {
					Date monthStartDate = DateUtility.getMonthEndDate(appDate);
					process = new TaxDownlaodProcess(dataSource, userId, appDate, monthStartDate, monthEndDate);
				}

				process.process("GST_TAXDOWNLOAD_DETAILS");
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

}
