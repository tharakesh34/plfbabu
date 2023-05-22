package com.pennant.app.core;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pennapps.core.resource.Literal;

public class DateRollOverService extends ServiceHelper {
	private static Logger logger = LogManager.getLogger(DateRollOverService.class);

	public void process(CustEODEvent custEODEvent) throws Exception {
		logger.debug(Literal.ENTERING);

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {

			// Set Next Grace Capitalization Date
			if (finEODEvent.getIdxGrcCpz() >= 0) {
				setNextGraceCpzDate(finEODEvent);
			}

			// Set Next Grace Profit Date
			if (finEODEvent.getIdxGrcPft() >= 0) {
				setNextGrcPftDate(finEODEvent);
			}

			// Set Next Grace Profit Review Date
			if (finEODEvent.getIdxGrcPftRvw() >= 0) {
				setNextGrcPftRvwDate(finEODEvent, custEODEvent.getEodValueDate());
			}

			// Set Next Repay Capitalization Date
			if (finEODEvent.getIdxRpyCpz() >= 0) {
				setNextRepayCpzDate(finEODEvent);
			}

			// Set Next Repayment Date
			if (finEODEvent.getIdxRpy() >= 0) {
				setNextRepayDate(finEODEvent);
			}

			// Set Next Repayment Profit Date
			if (finEODEvent.getIdxRpyPft() >= 0) {
				setNextRepayPftDate(finEODEvent);
			}

			// Set Next Repayment Profit Review Date
			if (finEODEvent.getIdxRpyPftRvw() >= 0) {
				setNextRepayRvwDate(finEODEvent, custEODEvent.getEodValueDate());
			}

			if (finEODEvent.isRateReviewExist()) {
				custEODEvent.setRateRvwExist(true);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	// --------------------------------------------------------------------------------------------------------------------------
	// Next Grace Capitalization Date
	// --------------------------------------------------------------------------------------------------------------------------
	private void setNextGraceCpzDate(FinEODEvent finEODEvent) {
		FinanceMain fm = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		int i = finEODEvent.getIdxGrcCpz();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < schedules.size(); j++) {
			curSchd = schedules.get(j);
			if (curSchd.getSchDate().compareTo(fm.getNextGrcCpzDate()) <= 0) {
				continue;
			}

			if (curSchd.isCpzOnSchDate()) {
				fm.setNextGrcCpzDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("NextGrcCpzDate");
				return;
			}

			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) >= 0) {
				return;
			}
		}

		return;
	}

	// --------------------------------------------------------------------------------------------------------------------------
	// Next Grace Profit Date
	// --------------------------------------------------------------------------------------------------------------------------
	private void setNextGrcPftDate(FinEODEvent finEODEvent) {
		FinanceMain fm = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		int i = finEODEvent.getIdxGrcPft();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < schedules.size(); j++) {
			curSchd = schedules.get(j);
			if (curSchd.getSchDate().compareTo(fm.getNextGrcPftDate()) <= 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
				fm.setNextGrcPftDate(schedules.get(i).getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("NextGrcPftDate");

				return;
			}

			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) >= 0) {
				return;
			}

		}

		return;
	}

	// --------------------------------------------------------------------------------------------------------------------------
	// Next Grace Profit Review Date
	// --------------------------------------------------------------------------------------------------------------------------
	private void setNextGrcPftRvwDate(FinEODEvent finEODEvent, Date valueDate) {
		FinanceMain fm = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		if (!CalculationConstants.RATEREVIEW_NORVW.equals(fm.getRvwRateApplFor())) {
			if (fm.getFinStartDate().compareTo(valueDate) != 0 && fm.getMaturityDate().compareTo(valueDate) != 0) {
				finEODEvent.setRateReviewExist(true);
			}
		}

		int i = finEODEvent.getIdxGrcPftRvw();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < schedules.size(); j++) {
			curSchd = schedules.get(j);

			if (curSchd.getSchDate().compareTo(fm.getNextGrcPftRvwDate()) <= 0) {
				continue;
			}

			if (curSchd.isRvwOnSchDate() || curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) == 0) {
				fm.setNextGrcPftRvwDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("NextGrcPftRvwDate");
				return;
			}

			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) >= 0) {
				return;
			}
		}

		return;
	}

	// --------------------------------------------------------------------------------------------------------------------------
	// Next Repay Capitalization Date
	// --------------------------------------------------------------------------------------------------------------------------
	private void setNextRepayCpzDate(FinEODEvent finEODEvent) {
		FinanceMain fm = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		int i = finEODEvent.getIdxRpyCpz();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < schedules.size(); j++) {
			curSchd = schedules.get(j);
			if (curSchd.getSchDate().compareTo(fm.getNextRepayCpzDate()) <= 0) {
				continue;
			}

			if (curSchd.isCpzOnSchDate()) {
				fm.setLastRepayCpzDate(fm.getNextRepayCpzDate());
				fm.setNextRepayCpzDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("LastRepayCpzDate");
				finEODEvent.addToFinMianUpdate("NextRepayCpzDate");
				return;
			}
		}

		return;
	}

	// --------------------------------------------------------------------------------------------------------------------------
	// Next Repayment Date
	// --------------------------------------------------------------------------------------------------------------------------
	private void setNextRepayDate(FinEODEvent finEODEvent) {
		FinanceMain fm = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		int i = finEODEvent.getIdxRpy();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < schedules.size(); j++) {
			curSchd = schedules.get(j);
			if (curSchd.getSchDate().compareTo(fm.getNextRepayDate()) <= 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()
					|| curSchd.getSchDate().compareTo(fm.getMaturityDate()) == 0) {
				fm.setLastRepayDate(fm.getNextRepayDate());
				fm.setNextRepayDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("LastRepayDate");
				finEODEvent.addToFinMianUpdate("NextRepayDate");
				return;
			}
		}

		return;
	}

	// --------------------------------------------------------------------------------------------------------------------------
	// Next Repay Profit Date
	// --------------------------------------------------------------------------------------------------------------------------
	private void setNextRepayPftDate(FinEODEvent finEODEvent) {
		FinanceMain fm = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		int i = finEODEvent.getIdxRpyPft();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < schedules.size(); j++) {
			curSchd = schedules.get(j);
			if (curSchd.getSchDate().compareTo(fm.getNextRepayPftDate()) <= 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.getSchDate().compareTo(fm.getMaturityDate()) == 0) {
				fm.setLastRepayPftDate(fm.getNextRepayPftDate());
				fm.setNextRepayPftDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("LastRepayPftDate");
				finEODEvent.addToFinMianUpdate("NextRepayPftDate");
				return;
			}
		}

		return;
	}

	// --------------------------------------------------------------------------------------------------------------------------
	// Next Repay Review Date
	// --------------------------------------------------------------------------------------------------------------------------
	private void setNextRepayRvwDate(FinEODEvent finEODEvent, Date valueDate) {
		FinanceMain fm = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		if (!CalculationConstants.RATEREVIEW_NORVW.equals(fm.getRvwRateApplFor())) {
			if (fm.getFinStartDate().compareTo(valueDate) != 0 && fm.getMaturityDate().compareTo(valueDate) != 0) {
				finEODEvent.setRateReviewExist(true);
			}
		}

		int i = finEODEvent.getIdxRpyPftRvw();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < schedules.size(); j++) {
			curSchd = schedules.get(j);
			if (curSchd.getSchDate().compareTo(fm.getNextRepayRvwDate()) <= 0) {
				continue;
			}

			if (curSchd.isRvwOnSchDate()) {
				fm.setLastRepayRvwDate(fm.getNextRepayRvwDate());
				fm.setNextRepayRvwDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("LastRepayRvwDate");
				finEODEvent.addToFinMianUpdate("NextRepayRvwDate");
				return;
			}
		}

		return;
	}
}