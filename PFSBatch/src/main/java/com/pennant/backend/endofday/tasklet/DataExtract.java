package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.DateUtility;
import com.pennanttech.bajaj.services.ALMRequestService;
import com.pennanttech.bajaj.services.ControlDumpRequestService;
import com.pennanttech.bajaj.services.DataMartRequestService;
import com.pennanttech.bajaj.services.PosidexRequestService;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.generalledger.TrailBalanceReportService;
import com.pennanttech.pff.reports.cibil.CIBILReport;

public class DataExtract implements Tasklet {
	private Logger						logger	= Logger.getLogger(DataExtract.class);

	private DataSource					dataSource;

	@Autowired
	private ALMRequestService			almRequestService;
	@Autowired
	private ControlDumpRequestService	controlDumpRequestService;
	@Autowired
	private PosidexRequestService		posidexRequestService;
	@Autowired
	private DataMartRequestService		dataMartRequestService;
	@Autowired
	private TrailBalanceReportService	trailBalanceReportService;
	@Autowired
	private CIBILReport              	cibilReport;
	

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Data Extract Preparation On : " + valueDate);

		try {

			Date monthEndDate = DateUtility.getMonthEnd(valueDate);
			//if month end then only it should run
//			if (monthEndDate.compareTo(valueDate) == 0) {
				new AMLRequest(new Long(1000), almRequestService).start();
				new ControlDumpRequest(new Long(1000), controlDumpRequestService).start();
//			}

			// PosidexRequestService
			new PosidexRequest(new Long(1000), posidexRequestService).start();
			new DataMartRequest(new Long(1000), dataMartRequestService).start();
			new TrailBalanceReport(new Long(1000), trailBalanceReportService).start();
			new CibilReport().start();

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

	public class AMLRequest extends Thread {

		private long				userId;
		private ALMRequestService	almRequestService;

		public AMLRequest(long userId, ALMRequestService almRequestService) {
			this.userId = userId;
			this.almRequestService=almRequestService;
		}

		public void run() {
			// ALMRequestService
			try {
				logger.debug("ALM Request Service started...");
				this.almRequestService.sendReqest(userId, DateUtility.getAppValueDate(), DateUtility.getAppDate());
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	public class ControlDumpRequest extends Thread {
		private long						userId;
		private ControlDumpRequestService	controlDumpRequestService;

		public ControlDumpRequest(long userId, ControlDumpRequestService controlDumpRequestService) {
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

	public class PosidexRequest extends Thread {
		private long					userId;
		private PosidexRequestService	posidexRequestService;

		public PosidexRequest(long userId, PosidexRequestService posidexRequestService) {
			this.userId = userId;
			this.posidexRequestService = posidexRequestService;
		}

		public void run() {
			try {
				logger.debug("Control Dump Request Service started...");
				this.posidexRequestService.sendReqest(userId, DateUtility.getAppValueDate(), DateUtility.getAppDate());

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}
	
	
	public class DataMartRequest extends Thread {
		private long					userId;
		private DataMartRequestService	dataMartRequestService;

		public DataMartRequest(long userId, DataMartRequestService dataMartRequestService) {
			this.userId = userId;
			this.dataMartRequestService = dataMartRequestService;
		}

		public void run() {
			try {
				logger.debug("DataMart Request Service started...");
				this.dataMartRequestService.sendReqest(userId, DateUtility.getAppValueDate(), DateUtility.getAppDate());

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	public class TrailBalanceReport extends Thread {
		private long							userId;
		private TrailBalanceReportService   	trailBalanceReportservice;

		public TrailBalanceReport(long userId,TrailBalanceReportService trailBalanceReportService) {
			this.userId = userId;
			this.trailBalanceReportservice = trailBalanceReportService;
		}

		public void run() {
			try {
				logger.debug("Trail Balance Request Service started...");
				this.trailBalanceReportservice.generateReport(userId);

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}
	
	
	public class CibilReport extends Thread {
		private CIBILReport cibilReport;

		public CibilReport() {
		}

		public void run() {
			try {
				logger.debug("Cibil Report Service started...");
				this.cibilReport.generateReport();

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}
	
}
