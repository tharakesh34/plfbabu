package com.pennant.backend.service.incomeamortization.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.core.FinEODEvent;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.incomeamortization.IncomeAmortizationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class CalAvgPOSProcess extends Thread {
	private static final Logger logger = LogManager.getLogger(CalAvgPOSProcess.class);

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
		Date monthEndDate = getFormatDate(projectedAmortization.getMonthEndDate());

		List<FinEODEvent> finEODEventList = prepareFinDataForAMZandAccruals(startDate, monthEndDate, financeList);

		long amzLogId = projectedAmortization.getAmzLogId();
		try {

			if (!finEODEventList.isEmpty()) {
				incomeAmortizationService.calAndUpdateAvgPOS(finEODEventList);
			}

			finCount.addAndGet(financeList.size());
			if (finListSize == finCount.get()) {
				incomeAmortizationService.updateCalAvgPOSStatus(EodConstants.PROGRESS_SUCCESS, amzLogId);
			}
		} catch (Exception e) {
			this.incomeAmortizationService.updateCalAvgPOSStatus(EodConstants.PROGRESS_FAILED, amzLogId);
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
	}

	private List<FinEODEvent> prepareFinDataForAMZandAccruals(Date startDate, Date monthEndDate,
			List<FinanceMain> financeList) {

		FinEODEvent finEODEvent = null;
		List<FinanceScheduleDetail> finScheduleDetails = null;
		List<FinEODEvent> finEODEventsList = new ArrayList<FinEODEvent>();

		for (FinanceMain fm : financeList) {
			Date finMaturityDate = fm.getMaturityDate();
			finScheduleDetails = getFinScheduleDetails(fm.getFinID(), monthEndDate);

			if (CollectionUtils.isEmpty(finScheduleDetails)) {
				continue;
			}

			Date schdMaturityDate = finScheduleDetails.get(finScheduleDetails.size() - 1).getSchDate();
			fm.setMaturityDate(schdMaturityDate);

			if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())) {
				if (schdMaturityDate.compareTo(finMaturityDate) > 0) {
					fm.setClosingStatus(null);
				}
			}

			finEODEvent = new FinEODEvent();
			finEODEvent.setEventFromDate(monthEndDate);

			finEODEvent.setFinanceMain(fm);
			finEODEvent.setFinanceScheduleDetails(finScheduleDetails);

			finEODEventsList.add(finEODEvent);
		}
		return finEODEventsList;
	}

	private List<FinanceScheduleDetail> getFinScheduleDetails(long finID, Date monthEndDate) {

		List<FinanceScheduleDetail> financeScheduleDetails = new ArrayList<FinanceScheduleDetail>();

		long logKey = this.incomeAmortizationService.getPrevSchedLogKey(finID, monthEndDate);

		if (logKey > 0) {
			financeScheduleDetails = incomeAmortizationService.getFinScheduleDetails(finID, "_Log", logKey);
		} else {
			financeScheduleDetails = incomeAmortizationService.getFinScheduleDetails(finID, "", 0);
		}

		return financeScheduleDetails;
	}

	private static Date getFormatDate(Date date) {
		return DateUtil.getDatePart(date);
	}

	public void setIncomeAmortizationService(IncomeAmortizationService incomeAmortizationService) {
		this.incomeAmortizationService = incomeAmortizationService;
	}
}