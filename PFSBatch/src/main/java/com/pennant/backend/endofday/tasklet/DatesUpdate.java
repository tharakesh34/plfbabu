package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.DateService;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class DatesUpdate implements Tasklet {
	private Logger logger = LogManager.getLogger(DatesUpdate.class);

	private DateService dateService;
	private ProjectedAmortizationDAO projectedAmortizationDAO;
	private GSTInvoiceTxnDAO gstInvoiceTxnDAO;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = SysParamUtil.getAppValueDate();
		logger.info("START Update Dates On {}", valueDate);

		/* Previous Month End ACCRUAL Records to working table to allow indexing for next run */
		if (SysParamUtil.isAllowed(AmortizationConstants.MONTHENDACC_CALREQ)) {
			if (valueDate.compareTo(DateUtil.getMonthEnd(valueDate)) == 0 || SysParamUtil.isAllowed("EOM_ON_EOD")) {
				Date monthStart = DateUtil.getMonthStart(valueDate);
				Date prvMonthEnd = DateUtil.addDays(monthStart, -1);
				Date curMonthEnd = DateUtil.addMonths(monthStart, 1);
				curMonthEnd = DateUtil.addDays(curMonthEnd, -1);

				if (valueDate.compareTo(DateUtil.getMonthEnd(valueDate)) == 0) {
					projectedAmortizationDAO.preparePrvProjectedAccruals(curMonthEnd);
				} else {
					projectedAmortizationDAO.preparePrvProjectedAccruals(prvMonthEnd);
				}
			}
		}

		/*
		 * GST invoice Sequences set it to 0 in month end if (valueDate.compareTo(DateUtil.getMonthStart(valueDate)) ==
		 * 0) { this.gstInvoiceTxnDAO.updateSeqNo(); }
		 */

		BatchUtil.setExecutionStatus(context, StepUtil.DATES_UPDATE);
		StepUtil.DATES_UPDATE.setTotalRecords(1);
		StepUtil.DATES_UPDATE.setProcessedRecords(1);

		/* Check extended month end and update the dates. */
		dateService.doUpdateAftereod(true);

		logger.info("COMPLETE Update Dates On {}", valueDate);
		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setDateService(DateService dateService) {
		this.dateService = dateService;
	}

	@Autowired
	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}

	@Autowired
	public void setGstInvoiceTxnDAO(GSTInvoiceTxnDAO gstInvoiceTxnDAO) {
		this.gstInvoiceTxnDAO = gstInvoiceTxnDAO;
	}

}
