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
import com.pennanttech.bajaj.services.PosidexRequestService;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.util.DateUtil;

public class DataExtract implements Tasklet {
	private Logger						logger	= Logger.getLogger(DataExtract.class);

	private DataSource					dataSource;

	@Autowired
	private ALMRequestService			almRequestService;
	@Autowired
	private ControlDumpRequestService	controlDumpRequestService;
	@Autowired
	private PosidexRequestService		posidexRequestService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Data Extract Preparation On : " + valueDate);

		try {
			new AMLRequest(new Long(1000), almRequestService).start();

			new ControlDumpRequest(new Long(1000), controlDumpRequestService);

			// PosidexRequestService
			try {
				logger.debug("Posidex Request Service started...");
				Date poFromDate = DateUtil.getMonthStart(DateUtil.addMonths(DateUtil.getSysDate(), -1));
				Date toDate = DateUtil.getMonthEnd(DateUtil.addMonths(DateUtil.getSysDate(), -1));
				this.posidexRequestService.sendReqest(new Long(1000), poFromDate, toDate);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

			// PosidexResponceService
			try {
				logger.debug("Posidex Responce Service started...");
				this.posidexRequestService.sendReqest(new Long(1000));
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
			// FIXME date values and mappings.....

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
		}

		public void run() {
			// ALMRequestService
			try {
				logger.debug("ALM Request Service started...");
				this.almRequestService.sendReqest(userId);
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
				this.controlDumpRequestService.sendReqest(userId);
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
				this.posidexRequestService.sendReqest(userId);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

}
