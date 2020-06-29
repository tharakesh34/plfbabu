package com.pennant.backend.service.incomeamortization.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.core.FinEODEvent;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.incomeamortization.IncomeAmortizationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class CalAvgPOSProcess extends Thread {

	private static final Logger logger = Logger.getLogger(CalAvgPOSProcess.class);

	private List<FinanceMain> financeList = null;
	private ProjectedAmortization projectedAmortization = null;
	private IncomeAmortizationService incomeAmortizationService;

	Date startDate = null;
	public AtomicLong finCount = null;
	public int finListSize = 0;

	public CalAvgPOSProcess() {
		super();
	}

	public CalAvgPOSProcess(ProjectedAmortization projectedAmortization, List<FinanceMain> financeList_Thread,
			AtomicLong finCount, int finListSize, IncomeAmortizationService incomeAmortizationService, Date startDate) {

		this.finCount = finCount;
		this.finListSize = finListSize;
		this.startDate = startDate;
		this.financeList = financeList_Thread;
		this.projectedAmortization = projectedAmortization;
		this.incomeAmortizationService = incomeAmortizationService;
	}

	@Override
	public void run() {
		logger.debug(Literal.ENTERING);

		Date startDate = getFormatDate(this.startDate);
		Date monthEndDate = getFormatDate(this.projectedAmortization.getMonthEndDate());

		List<FinEODEvent> finEODEventList = prepareFinDataForAMZandAccruals(startDate, monthEndDate, this.financeList);

		try {

			if (!finEODEventList.isEmpty()) {
				this.incomeAmortizationService.calAndUpdateAvgPOS(finEODEventList);
			}

			// Increment and Update Log status
			finCount.addAndGet(financeList.size());
			if (finListSize == finCount.get()) {
				this.incomeAmortizationService.updateCalAvgPOSStatus(EodConstants.PROGRESS_SUCCESS,
						projectedAmortization.getAmzLogId()); // 2
			}
		} catch (Exception e) {
			this.incomeAmortizationService.updateCalAvgPOSStatus(EodConstants.PROGRESS_FAILED,
					projectedAmortization.getAmzLogId()); // 3
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * Method for preparing finance data for amortization and Re Calculate Month End ACCRUALS
	 * 
	 * Schedules Details from LOG Table
	 * 
	 * @param startDate
	 * @param monthEndDate
	 * @return
	 */
	private List<FinEODEvent> prepareFinDataForAMZandAccruals(Date startDate, Date monthEndDate,
			List<FinanceMain> financeList) {

		FinEODEvent finEODEvent = null;
		List<FinanceScheduleDetail> finScheduleDetails = null;
		List<FinEODEvent> finEODEventsList = new ArrayList<FinEODEvent>();

		for (FinanceMain finMain : financeList) {

			// Finance Type from Cache and FinPftDetails
			Date finMaturityDate = finMain.getMaturityDate();
			finScheduleDetails = getFinScheduleDetails(finMain.getFinReference(), monthEndDate);

			if (!finScheduleDetails.isEmpty()) {

				Date schdMaturityDate = finScheduleDetails.get(finScheduleDetails.size() - 1).getSchDate();
				finMain.setMaturityDate(schdMaturityDate);

				if (StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_CANCELLED)) {
					if (schdMaturityDate.compareTo(finMaturityDate) > 0) {
						finMain.setClosingStatus(null);
					}
				}

				finEODEvent = new FinEODEvent();
				finEODEvent.setEventFromDate(monthEndDate);

				finEODEvent.setFinanceMain(finMain);
				finEODEvent.setFinanceScheduleDetails(finScheduleDetails);

				finEODEventsList.add(finEODEvent);
			}
		}
		return finEODEventsList;
	}

	/**
	 * get Schedule Details at that point of time
	 * 
	 * @param finReference
	 * @return
	 */
	private List<FinanceScheduleDetail> getFinScheduleDetails(String finReference, Date monthEndDate) {

		List<FinanceScheduleDetail> financeScheduleDetails = new ArrayList<FinanceScheduleDetail>();

		long logKey = this.incomeAmortizationService.getPrevSchedLogKey(finReference, monthEndDate);

		if (logKey > 0) {
			financeScheduleDetails = this.incomeAmortizationService.getFinScheduleDetails(finReference, "_Log", logKey);
		} else {
			financeScheduleDetails = this.incomeAmortizationService.getFinScheduleDetails(finReference, "", 0);
		}

		return financeScheduleDetails;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	private static Date getFormatDate(Date date) {
		return DateUtility.getDBDate(DateUtility.format(date, PennantConstants.DBDateFormat));
	}

	// getters / setters

	public void setIncomeAmortizationService(IncomeAmortizationService incomeAmortizationService) {
		this.incomeAmortizationService = incomeAmortizationService;
	}
}