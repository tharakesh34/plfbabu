package com.pennant.backend.endofday.tasklet;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.services.ControlDumpRequestService;
import com.pennanttech.pff.core.services.TrailBalanceReportService;
import com.pennanttech.pff.core.taxdownload.TaxDownlaodDetailService;
import com.pennanttech.pff.reports.cibil.CIBILReport;
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

public class DataExtract implements Tasklet {
	private Logger logger = Logger.getLogger(DataExtract.class);

	private DataSource dataSource;
	@Autowired
	private EODConfigDAO eodConfigDAO;

	@Autowired
	private ControlDumpRequestService controlDumpRequestService;
	
	@Autowired
	private TrailBalanceReportService trailBalanceReportService;
	@Autowired
	private CIBILReport cibilReport;
	@Autowired
	private TaxDownlaodDetailService taxDownlaodDetailService;

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

			new TrailBalanceReportThread(new Long(1000), trailBalanceReportService).start();
			new ControlDumpRequestThread(new Long(1000), controlDumpRequestService).start();
			new TaxDownlaodDetailThread(new Long(1000), taxDownlaodDetailService).start();
			new CibilReportThread(cibilReport).start();

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

	public class ControlDumpRequestThread extends Thread {
		private long userId;
		private ControlDumpRequestService controlDumpRequestService;

		public ControlDumpRequestThread(long userId, ControlDumpRequestService controlDumpRequestService) {
			this.userId = userId;
			this.controlDumpRequestService = controlDumpRequestService;
		}

		public void run() {
			try {
				logger.debug("Control Dump Request Service started...");

				Date monthStartDate = DateUtility.getMonthStartDate(DateUtility.getAppValueDate());
				Date monthEndDate = DateUtility.getMonthEnd(DateUtility.getAppValueDate());

				this.controlDumpRequestService.sendReqest(userId, DateUtility.getAppValueDate(),
						DateUtility.getAppDate(), monthStartDate, monthEndDate);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	public class TrailBalanceReportThread extends Thread {
		private long userId;
		private TrailBalanceReportService trailBalanceReportService;

		public TrailBalanceReportThread(long userId, TrailBalanceReportService trailBalanceReportService) {
			this.userId = userId;
			this.trailBalanceReportService = trailBalanceReportService;
		}

		public void run() {
			try {
				logger.debug("Trail Balance Request Service started...");
				this.trailBalanceReportService.generateReport(userId);

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	public class CibilReportThread extends Thread {
		private CIBILReport cibilReport;

		public CibilReportThread(CIBILReport cibilReport) {
			this.cibilReport = cibilReport;
		}

		public void run() {
			try {
				logger.debug("Cibil Report Service started...");
				cibilReport.generateReport();
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	public class TaxDownlaodDetailThread extends Thread {
		private long userId;
		private TaxDownlaodDetailService taxDownlaodDetailService;

		public TaxDownlaodDetailThread(long userId, TaxDownlaodDetailService taxDownlaodDetailService) {
			this.userId = userId;
			this.taxDownlaodDetailService = taxDownlaodDetailService;
		}

		public void run() {
			try {
				logger.debug("TaxDownlaodDetail Service started...");

				Date appDate = DateUtility.getAppValueDate();
				Date monthEndDate = DateUtility.getMonthEndDate(appDate);
				String isDailyDownlaod = SysParamUtil.getValueAsString("GST_TAXDETAIL_DOWNLOAD");
				
				if (StringUtils.equalsIgnoreCase("Y", isDailyDownlaod)) {
					this.taxDownlaodDetailService.sendReqest(userId, appDate, appDate, appDate);
				} else if (DateUtility.compare(appDate, monthEndDate) == 0) {
					Date monthStartDate = DateUtility.getMonthEndDate(appDate);
					this.taxDownlaodDetailService.sendReqest(userId, appDate, monthStartDate, monthEndDate);
				}
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}
}
