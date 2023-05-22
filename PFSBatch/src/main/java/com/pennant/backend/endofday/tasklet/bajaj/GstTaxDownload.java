package com.pennant.backend.endofday.tasklet.bajaj;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.external.TaxDownloadProcess;
import com.pennanttech.pff.external.gst.TaxDownlaodExtract;

public class GstTaxDownload implements Tasklet {
	private Logger logger = LogManager.getLogger(GstTaxDownload.class);

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
			logger.error(Literal.EXCEPTION, e);
		}
		return null;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		valueDate = (Date) context.getStepContext().getJobExecutionContext().get("APP_VALUEDATE");
		appDate = (Date) context.getStepContext().getJobExecutionContext().get("APP_DATE");

		try {
			logger.debug("START: GST-TAX Download Process for the value date: "
					.concat(DateUtil.format(appDate, DateFormat.LONG_DATE)));

			DataEngineStatus status = TaxDownlaodExtract.EXTRACT_STATUS;
			status.setStatus("I");
			new Thread(new GSTTaxProcessThread(1000L)).start();
			Thread.sleep(1000);
			BatchUtil.setExecutionStatus(context, status);

			logger.debug("COMPLETED: GST-TAX Download Process for the value date: "
					.concat(DateUtil.format(appDate, DateFormat.LONG_DATE)));
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
				TaxDownloadProcess process = null;

				if (StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("GST_TAX_DOWNLOAD_DAILY"))) {
					process = new TaxDownlaodExtract(dataSource, userId, valueDate, appDate, appDate, appDate);
				} else {
					process = new TaxDownlaodExtract(dataSource, userId, valueDate, appDate,
							DateUtil.getMonthStart(appDate), DateUtil.getMonthEnd(appDate));
				}
				process.process();
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

}
