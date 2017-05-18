package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.DateUtility;
import com.pennanttech.bajaj.services.ALMRequestService;
import com.pennanttech.bajaj.services.ControlDumpRequestService;
import com.pennanttech.bajaj.services.PosidexRequestService;
import com.pennanttech.bajaj.services.PosidexResponceService;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.util.DateUtil;

public class DataExtract implements Tasklet {
	private Logger logger = Logger.getLogger(DataExtract.class);

	private DataSource dataSource;
	private ALMRequestService almRequestService;
	private ControlDumpRequestService controlDumpRequestService;
	private PosidexRequestService posidexRequestService;
	private PosidexResponceService posidexResponceService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Data Extract Preparation On : " + valueDate);

		try {
			// ALMRequestService
			try {
				logger.debug("ALMRequestService started...");
				Date alFromDate = DateUtil.getMonthStart(DateUtil.addMonths(DateUtil.getSysDate(), -1));
				Date alToDate = DateUtil.getMonthEnd(DateUtil.addMonths(DateUtil.getSysDate(), -1));
				this.almRequestService.sendReqest(new Long(1000), alFromDate, alToDate);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

			// ControlDumpRequestService
			try {
				logger.debug("ControlDumpRequestService started...");
				this.controlDumpRequestService.sendReqest(new Long(1000));
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

			// PosidexRequestService
			try {
				logger.debug("PosidexRequestService started...");
				Date poFromDate = DateUtil.getMonthStart(DateUtil.addMonths(DateUtil.getSysDate(), -1));
				Date toDate = DateUtil.getMonthEnd(DateUtil.addMonths(DateUtil.getSysDate(), -1));
				this.posidexRequestService.sendReqest(new Long(1000), poFromDate, toDate);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

			// PosidexResponceService
			try {
				logger.debug("PosidexResponceService started...");
				this.posidexResponceService.sendReqest(new Long(1000));
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

	public void setAlmRequestService(ALMRequestService almRequestService) {
		this.almRequestService = almRequestService;
	}

	public void setControlDumpRequestService(ControlDumpRequestService controlDumpRequestService) {
		this.controlDumpRequestService = controlDumpRequestService;
	}

	public void setPosidexRequestService(PosidexRequestService posidexRequestService) {
		this.posidexRequestService = posidexRequestService;
	}

	public void setPosidexResponceService(PosidexResponceService posidexResponceService) {
		this.posidexResponceService = posidexResponceService;
	}

}
