package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.DateService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.util.AmortizationConstants;

public class DatesUpdate implements Tasklet {
	private Logger logger = Logger.getLogger(DatesUpdate.class);

	@SuppressWarnings("unused")
	private DataSource dataSource;
	private DateService dateService;

	@Autowired
	private ProjectedAmortizationDAO projectedAmortizationDAO;
	@Autowired
	private GSTInvoiceTxnDAO gstInvoiceTxnDAO;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Update Dates On : " + valueDate);

		// Previous Month End ACCRUAL Records to working table to allow indexing
		// for next run
		String accrualCalForAMZ = SysParamUtil.getValueAsString(AmortizationConstants.MONTHENDACC_CALREQ);
		if (StringUtils.endsWithIgnoreCase(accrualCalForAMZ, "Y")) {

			if (valueDate.compareTo(DateUtility.getMonthEnd(valueDate)) == 0
					|| StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("EOM_ON_EOD"))) {

				Date monthStart = DateUtility.getMonthStart(valueDate);
				Date prvMonthEnd = DateUtility.addDays(monthStart, -1);
				Date curMonthEnd = DateUtility.addMonths(monthStart, 1);
				curMonthEnd = DateUtility.addDays(curMonthEnd, -1);

				if (valueDate.compareTo(DateUtility.getMonthEnd(valueDate)) == 0) {
					projectedAmortizationDAO.preparePrvProjectedAccruals(curMonthEnd);
				} else {
					projectedAmortizationDAO.preparePrvProjectedAccruals(prvMonthEnd);
				}
			}
		}

		//GST invoice Sequences set it to 0 in month end
		if (valueDate.compareTo(DateUtility.getMonthEnd(valueDate)) == 0) {
			this.gstInvoiceTxnDAO.updateSeqNo();
		}

		// check extended month end and update the dates.
		dateService.doUpdateAftereod(true);

		logger.debug("COMPLETE:  Update Dates On :" + valueDate);
		return RepeatStatus.FINISHED;
	}

	// getters / setters

	public void setDateService(DateService dateService) {
		this.dateService = dateService;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
