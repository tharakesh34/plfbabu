package com.pennant.app.core;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class DateRollOverService extends ServiceHelper {

	private static Logger		logger				= Logger.getLogger(DateRollOverService.class);
	private static final long	serialVersionUID	= -3371115026576113554L;

	public CustEODEvent process(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {

			//Set Next Grace Capitalization Date
			if (finEODEvent.getIdxGrcCpz() >= 0) {
				setNextGraceCpzDate(finEODEvent);
			}

			//Set Next Grace Profit Date
			if (finEODEvent.getIdxGrcPft() >= 0) {
				setNextGrcPftDate(finEODEvent);
			}

			//Set Next Grace Profit Review Date
			if (finEODEvent.getIdxGrcPftRvw() >= 0) {
				setNextGrcPftRvwDate(finEODEvent, custEODEvent.getEodValueDate());
			}

			//Set Next Repay Capitalization Date
			if (finEODEvent.getIdxRpyCpz() >= 0) {
				setNextRepayCpzDate(finEODEvent);
			}

			//Set Next Repayment Date
			if (finEODEvent.getIdxRpy() >= 0) {
				setNextRepayDate(finEODEvent);
			}

			//Set Next Repayment Profit Date
			if (finEODEvent.getIdxRpyPft() >= 0) {
				setNextRepayPftDate(finEODEvent);
			}

			//Set Next Repayment Profit Review Date
			if (finEODEvent.getIdxRpyPftRvw() >= 0) {
				setNextRepayRvwDate(finEODEvent, custEODEvent.getEodValueDate());
			}
			
			if (finEODEvent.isRateReviewExist()) {
				custEODEvent.setRateRvwExist(true);
			}
		}

		logger.debug(" Leaving ");
		return custEODEvent;

	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Grace Capitalization Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextGraceCpzDate(FinEODEvent finEODEvent) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();

		int i = finEODEvent.getIdxGrcCpz();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(j);
			if (curSchd.getSchDate().compareTo(finMain.getNextGrcCpzDate()) <= 0) {
				continue;
			}

			if (curSchd.isCpzOnSchDate()) {
				finMain.setNextGrcCpzDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("NextGrcCpzDate");
				return;
			}

			if (curSchd.getSchDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
				return;
			}
		}

		return;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Grace Profit Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextGrcPftDate(FinEODEvent finEODEvent) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();

		int i = finEODEvent.getIdxGrcPft();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(j);
			if (curSchd.getSchDate().compareTo(finMain.getNextGrcPftDate()) <= 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
				finMain.setNextGrcPftDate(finSchdDetails.get(i).getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("NextGrcPftDate");

				return;
			}

			if (curSchd.getSchDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
				return;
			}

		}

		return;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Grace Profit Review Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextGrcPftRvwDate(FinEODEvent finEODEvent, Date valueDate) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();

		if (!StringUtils.equals(finMain.getRvwRateApplFor(), CalculationConstants.RATEREVIEW_NORVW)) {
			if (finMain.getFinStartDate().compareTo(valueDate) != 0
					&& finMain.getMaturityDate().compareTo(valueDate) != 0) {
				finEODEvent.setRateReviewExist(true);
			}
		}

		int i = finEODEvent.getIdxGrcPftRvw();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(j);

			if (curSchd.getSchDate().compareTo(finMain.getNextGrcPftRvwDate()) <= 0) {
				continue;
			}

			if (curSchd.isRvwOnSchDate() || curSchd.getSchDate().compareTo(finMain.getGrcPeriodEndDate()) == 0) {
				finMain.setNextGrcPftRvwDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("NextGrcPftRvwDate");
				return;
			}

			if (curSchd.getSchDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
				return;
			}
		}

		return;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Repay Capitalization Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextRepayCpzDate(FinEODEvent finEODEvent) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();

		int i = finEODEvent.getIdxRpyCpz();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(j);
			if (curSchd.getSchDate().compareTo(finMain.getNextRepayCpzDate()) <= 0) {
				continue;
			}

			if (curSchd.isCpzOnSchDate()) {
				finMain.setLastRepayCpzDate(finMain.getNextRepayCpzDate());
				finMain.setNextRepayCpzDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("LastRepayCpzDate");
				finEODEvent.addToFinMianUpdate("NextRepayCpzDate");
				return;
			}
		}

		return;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Repayment Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextRepayDate(FinEODEvent finEODEvent) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();

		int i = finEODEvent.getIdxRpy();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(j);
			if (curSchd.getSchDate().compareTo(finMain.getNextRepayDate()) <= 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()
					|| curSchd.getSchDate().compareTo(finMain.getMaturityDate()) == 0) {
				finMain.setLastRepayDate(finMain.getNextRepayDate());
				finMain.setNextRepayDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("LastRepayDate");
				finEODEvent.addToFinMianUpdate("NextRepayDate");
				return;
			}
		}

		return;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Repay Profit Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextRepayPftDate(FinEODEvent finEODEvent) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();

		int i = finEODEvent.getIdxRpyPft();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(j);
			if (curSchd.getSchDate().compareTo(finMain.getNextRepayPftDate()) <= 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.getSchDate().compareTo(finMain.getMaturityDate()) == 0) {
				finMain.setLastRepayPftDate(finMain.getNextRepayPftDate());
				finMain.setNextRepayPftDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("LastRepayPftDate");
				finEODEvent.addToFinMianUpdate("NextRepayPftDate");
				return;
			}
		}

		return;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Repay Review Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextRepayRvwDate(FinEODEvent finEODEvent, Date valueDate) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();

		if (!StringUtils.equals(finMain.getRvwRateApplFor(), CalculationConstants.RATEREVIEW_NORVW)) {
			if (finMain.getFinStartDate().compareTo(valueDate) != 0
					&& finMain.getMaturityDate().compareTo(valueDate) != 0) {
				finEODEvent.setRateReviewExist(true);
			}
		}

		int i = finEODEvent.getIdxRpyPftRvw();
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(j);
			if (curSchd.getSchDate().compareTo(finMain.getNextRepayRvwDate()) <= 0) {
				continue;
			}

			if (curSchd.isRvwOnSchDate()) {
				finMain.setLastRepayRvwDate(finMain.getNextRepayRvwDate());
				finMain.setNextRepayRvwDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("LastRepayRvwDate");
				finEODEvent.addToFinMianUpdate("NextRepayRvwDate");
				return;
			}
		}

		return;
	}
}