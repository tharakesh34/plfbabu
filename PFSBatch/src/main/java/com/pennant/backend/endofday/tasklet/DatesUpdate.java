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
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class DatesUpdate extends SequenceDao<Object> implements Tasklet {
	private Logger logger = LogManager.getLogger(DatesUpdate.class);

	private DateService dateService;
	private ProjectedAmortizationDAO projectedAmortizationDAO;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		EventProperties eventProperties = EODUtil.getEventProperties(EODUtil.EVENT_PROPERTIES, context);
		Date valueDate = eventProperties.getAppValueDate();

		logger.info("START Update Dates On {}", valueDate);

		/* Previous Month End ACCRUAL Records to working table to allow indexing for next run */
		if (eventProperties.isMonthEndAccCallReq()) {
			if (valueDate.compareTo(DateUtil.getMonthEnd(valueDate)) == 0 || eventProperties.isEomOnEOD()) {
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

		EODUtil.updateEventProperties(context, eventProperties);
		EODUtil.setDatesReload(true);

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

}
