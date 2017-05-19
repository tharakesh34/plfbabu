package com.pennant.app.core;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class DateRollOverService extends ServiceHelper {

	private static Logger		logger				= Logger.getLogger(DateRollOverService.class);
	private static final long	serialVersionUID	= -3371115026576113554L;

	public CustEODEvent process(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		custEODEvent.setEodValueDate(DateUtility.addDays(custEODEvent.getEodValueDate(), 1));

		for (FinEODEvent finEODEvent : finEODEvents) {
			Date valueDate = custEODEvent.getEodValueDate();
			FinanceMain finMain = finEODEvent.getFinanceMain();

			if (finMain.isAllowGrcPeriod()) {
				//Set Next Grace Capitalization Date
				if (finMain.isAllowGrcCpz()) {
					if (finMain.getNextGrcCpzDate().compareTo(valueDate) == 0
							&& finMain.getNextGrcPftDate().compareTo(finMain.getGrcPeriodEndDate()) < 0) {
						setNextGraceCpzDate(finEODEvent);
					}
				}

				//Set Next Grace Profit Date
				if (finMain.getNextGrcPftDate().compareTo(valueDate) == 0
						&& finMain.getNextGrcPftDate().compareTo(finMain.getGrcPeriodEndDate()) < 0) {
					setNextGrcPftDate(finEODEvent);
				}

				//Set Next Grace Profit Review Date
				if (finMain.isAllowGrcPftRvw()) {
					if (finMain.getNextGrcPftRvwDate().compareTo(valueDate) == 0
							&& finMain.getNextGrcPftRvwDate().compareTo(finMain.getGrcPeriodEndDate()) < 0) {
						setNextGrcPftRvwDate(finEODEvent, custEODEvent.getEodValueDate());
					}
				}

			}

			//Next Dates in Reapyment Period
			if (finMain.getNextRepayDate().compareTo(finMain.getMaturityDate()) >= 0) {
				continue;
			}

			//Set Next Repay Capitalization Date

			if (finMain.isAllowRepayCpz()) {
				if (finMain.getNextRepayCpzDate().compareTo(valueDate) == 0) {
					setNextRepayCpzDate(finEODEvent);
				}
			}

			//Set Next Repayment Date
			if (finMain.getNextRepayDate().compareTo(valueDate) == 0) {
				setNextRepayDate(finEODEvent);
			}

			//Set Next Repayment Profit Date
			if (finMain.getNextRepayPftDate().compareTo(valueDate) == 0) {
				setNextRepayPftDate(finEODEvent);
			}

			//Set Next Repayment Profit Review Date
			if (finMain.isAllowRepayRvw()) {
				if (finMain.getNextRepayRvwDate() != null && finMain.getNextRepayRvwDate().compareTo(valueDate) == 0) {
					setNextRepayRvwDate(finEODEvent, custEODEvent.getEodValueDate());
				}
			}

			//Set Next Depreciation Date
			if (finMain.getNextDepDate() != null && finMain.getNextDepDate().compareTo(valueDate) == 0) {
				if (!StringUtils.isEmpty(finMain.getDepreciationFrq())) {
					if (finMain.getNextDepDate().compareTo(finMain.getMaturityDate()) < 0) {
						finMain.setNextDepDate(
								FrequencyUtil.getNextDate(finMain.getDepreciationFrq(), 1, valueDate, "A", false)
										.getNextFrequencyDate());
					}

					if (finMain.getNextDepDate().compareTo(finMain.getMaturityDate()) > 0) {
						finMain.setNextDepDate(finMain.getMaturityDate());
					}

					finEODEvent.setUpdFinMain(true);
					finEODEvent.addToFinMianUpdate("NextDepDate");
				}
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
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();

		int i = getIndexFromMap(datesMap, finMain.getNextGrcCpzDate());
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
		}

		return;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Grace Profit Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextGrcPftDate(FinEODEvent finEODEvent) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();

		int i = getIndexFromMap(datesMap, finMain.getNextGrcPftDate());
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
		}

		return;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Grace Profit Review Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextGrcPftRvwDate(FinEODEvent finEODEvent, Date valueDate) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();

		if (!StringUtils.equals(finMain.getRvwRateApplFor(), CalculationConstants.RATEREVIEW_NORVW)) {
			if (finMain.getFinStartDate().compareTo(valueDate) != 0
					&& finMain.getMaturityDate().compareTo(valueDate) != 0) {
				finEODEvent.setRateReviewExist(true);
			}
		}

		int i = getIndexFromMap(datesMap, finMain.getNextGrcPftRvwDate());
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
		}

		return;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Repay Capitalization Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextRepayCpzDate(FinEODEvent finEODEvent) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();

		int i = getIndexFromMap(datesMap, finMain.getNextRepayCpzDate());
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
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();

		int i = getIndexFromMap(datesMap, finMain.getNextRepayDate());
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
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();

		int i = getIndexFromMap(datesMap, finMain.getNextRepayPftDate());
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
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();

		if (!StringUtils.equals(finMain.getRvwRateApplFor(), CalculationConstants.RATEREVIEW_NORVW)) {
			if (finMain.getFinStartDate().compareTo(valueDate) != 0
					&& finMain.getMaturityDate().compareTo(valueDate) != 0) {
				finEODEvent.setRateReviewExist(true);
			}
		}

		int i = getIndexFromMap(datesMap, finMain.getNextRepayRvwDate());
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