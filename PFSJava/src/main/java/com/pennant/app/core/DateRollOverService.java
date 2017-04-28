package com.pennant.app.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pff.core.TableType;

public class DateRollOverService extends ServiceHelper {

	private static final long	serialVersionUID	= -3371115026576113554L;

	public List<FinEODEvent> process(List<FinEODEvent> custEODEvents) throws Exception {

		for (FinEODEvent finEODEvent : custEODEvents) {
			Date valueDate = finEODEvent.getEodValueDate();
			FinanceMain finMain = finEODEvent.getFinanceMain();

			//Set Next Grace Capitalization Date
			if (finMain.getNextGrcCpzDate() != null && finMain.getNextGrcCpzDate().compareTo(valueDate) == 0) {
				setNextGraceCpzDate(finEODEvent);
			}

			//Set Next Grace Profit Date
			if (finMain.getNextGrcPftDate() != null && finMain.getNextGrcPftDate().compareTo(valueDate) == 0) {
				setNextGrcPftDate(finEODEvent);
			}

			//Set Next Grace Profit Review Date
			if (finMain.getNextGrcPftRvwDate() != null && finMain.getNextGrcPftRvwDate().compareTo(valueDate) == 0) {
				setNextGrcPftRvwDate(finEODEvent);
			}

			//Set Next Repay Capitalization Date
			if (finMain.getNextRepayCpzDate() != null && finMain.getNextRepayCpzDate().compareTo(valueDate) == 0) {
				setNextRepayCpzDate(finEODEvent);
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
			if (finMain.getNextRepayRvwDate() != null && finMain.getNextRepayRvwDate().compareTo(valueDate) == 0) {
				setNextRepayRvwDate(finEODEvent);
			}

			//Set Next Depreciation Date
			if (finMain.getNextDepDate() != null && finMain.getNextDepDate().compareTo(valueDate) == 0) {
				if (!StringUtils.isEmpty(finMain.getDepreciationFrq())) {
					if (finMain.getNextDepDate().compareTo(finMain.getMaturityDate()) < 0) {
						finMain.setNextDepDate(FrequencyUtil.getNextDate(finMain.getDepreciationFrq(), 1, valueDate,
								"A", false).getNextFrequencyDate());
					}

					if (finMain.getNextDepDate().compareTo(finMain.getMaturityDate()) > 0) {
						finMain.setNextDepDate(finMain.getMaturityDate());
					}

					finEODEvent.setUpdFinMain(true);
				}
			}
		}

		return custEODEvents;

	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Grace Capitalization Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextGraceCpzDate(FinEODEvent finEODEvent) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();

		if (!finMain.isAllowGrcCpz()) {
			return;
		}

		if (finMain.getNextGrcPftDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
			return;
		}

		int i = datesMap.get(finMain.getNextGrcCpzDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextGrcCpzDate()) <= 0) {
				continue;
			}

			if (curSchd.isCpzOnSchDate()) {
				finMain.setNextGrcCpzDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
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

		if (finMain.getNextGrcPftDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
			return;
		}

		int i = datesMap.get(finMain.getNextGrcPftDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextGrcPftDate()) <= 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
				finMain.setNextGrcPftDate(finSchdDetails.get(i).getSchDate());
				finEODEvent.setUpdFinMain(true);

				return;
			}
		}

		return;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Grace Profit Review Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextGrcPftRvwDate(FinEODEvent finEODEvent) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();

		if (!finMain.isAllowGrcPftRvw()) {
			return;
		}

		if (finMain.getNextGrcPftRvwDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
			return;
		}

		if (!StringUtils.equals(finMain.getRvwRateApplFor(), CalculationConstants.RATEREVIEW_NORVW)) {
			if (finMain.getFinStartDate().compareTo(finEODEvent.getEodValueDate()) != 0
					&& finMain.getMaturityDate().compareTo(finEODEvent.getEodValueDate()) != 0) {
				finEODEvent.setRateReview(true);
			}
		}

		int i = datesMap.get(finMain.getNextGrcPftRvwDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);

			if (curSchd.getSchDate().compareTo(finMain.getNextGrcPftRvwDate()) <= 0) {
				continue;
			}

			if (curSchd.isRvwOnSchDate() || curSchd.getSchDate().compareTo(finMain.getGrcPeriodEndDate()) == 0) {
				finMain.setNextGrcPftRvwDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
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

		if (!finMain.isAllowRepayCpz()) {
			return;
		}

		if (finMain.getNextRepayCpzDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return;
		}

		int i = datesMap.get(finMain.getNextRepayCpzDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextRepayCpzDate()) <= 0) {
				continue;
			}

			if (curSchd.isCpzOnSchDate()) {
				finMain.setLastRepayCpzDate(finMain.getNextRepayCpzDate());
				finMain.setNextRepayCpzDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
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

		if (finMain.getNextRepayDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return;
		}

		int i = datesMap.get(finMain.getNextRepayDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextRepayDate()) <= 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()
					|| curSchd.getSchDate().compareTo(finMain.getMaturityDate()) == 0) {
				finMain.setLastRepayDate(finMain.getNextRepayDate());
				finMain.setNextRepayDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
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

		if (finMain.getNextRepayPftDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return;
		}

		int i = datesMap.get(finMain.getNextRepayPftDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextRepayPftDate()) <= 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.getSchDate().compareTo(finMain.getMaturityDate()) == 0) {
				finMain.setLastRepayPftDate(finMain.getNextRepayPftDate());
				finMain.setNextRepayPftDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				return;
			}
		}

		return;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Repay Review Date
	//--------------------------------------------------------------------------------------------------------------------------
	private void setNextRepayRvwDate(FinEODEvent finEODEvent) {
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();

		if (!finMain.isAllowRepayRvw()) {
			return;
		}

		if (finMain.getNextRepayRvwDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return;
		}

		if (!StringUtils.equals(finMain.getRvwRateApplFor(), CalculationConstants.RATEREVIEW_NORVW)) {
			if (finMain.getFinStartDate().compareTo(finEODEvent.getEodValueDate()) != 0
					&& finMain.getMaturityDate().compareTo(finEODEvent.getEodValueDate()) != 0) {
				finEODEvent.setRateReview(true);
			}
		}

		int i = datesMap.get(finMain.getNextRepayRvwDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextRepayRvwDate()) <= 0) {
				continue;
			}

			if (curSchd.isRvwOnSchDate()) {
				finMain.setLastRepayRvwDate(finMain.getNextRepayRvwDate());
				finMain.setNextRepayRvwDate(curSchd.getSchDate());
				finEODEvent.setUpdFinMain(true);
				;
				return;
			}
		}

		return;
	}

	public List<FinEODEvent> prepareFinEODEvents(long custId, Date date) throws Exception {

		List<FinanceMain> custFinMains = getFinanceMainDAO().getFinanceMainsByCustId(custId);
		List<FinEODEvent> custEODEvents = new ArrayList<FinEODEvent>();

		for (int i = 0; i < custFinMains.size(); i++) {
			FinEODEvent finEODEvent = new FinEODEvent();
			Map<Date, Integer> datesMap = new HashMap<Date, Integer>();

			finEODEvent.setEodDate(date);
			finEODEvent.setEodValueDate(DateUtility.addDays(finEODEvent.getEodDate(), 1));
			finEODEvent.setFinanceMain(custFinMains.get(i));

			List<FinanceScheduleDetail> finSchdDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(
					finEODEvent.getFinanceMain().getFinReference(), TableType.MAIN_TAB.getSuffix(), false);

			//Place schedule dates to Map
			for (int j = 0; j < finSchdDetails.size(); j++) {
				datesMap.put(finSchdDetails.get(j).getSchDate(), j);
			}

			finEODEvent.setDatesMap(datesMap);
			finEODEvent.setFinanceScheduleDetails(finSchdDetails);

			custEODEvents.add(finEODEvent);

		}
		return custEODEvents;
	}

}