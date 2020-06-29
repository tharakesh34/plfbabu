package com.pennant.backend.service.incomeamortization.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.core.FinEODEvent;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.incomeamortization.IncomeAmortizationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class AmortizationProcess extends Thread {

	private static final Logger logger = Logger.getLogger(AmortizationProcess.class);

	private List<FinanceMain> financeList = null;
	private ProjectedAmortization projectedAmortization = null;
	private IncomeAmortizationService incomeAmortizationService;

	public AtomicLong finCount = null;
	public int finListSize = 0;

	public AmortizationProcess() {
		super();
	}

	public AmortizationProcess(ProjectedAmortization projectedAmortization, List<FinanceMain> financeList_Thread,
			AtomicLong finCount, int finListSize, IncomeAmortizationService incomeAmortizationService) {

		this.finCount = finCount;
		this.finListSize = finListSize;
		this.financeList = financeList_Thread;
		this.projectedAmortization = projectedAmortization;
		this.incomeAmortizationService = incomeAmortizationService;
	}

	@Override
	public void run() {
		logger.debug(Literal.ENTERING);

		try {

			// Start Amortization Process
			this.incomeAmortizationService.processAmortization(this.financeList,
					this.projectedAmortization.getMonthEndDate());

			// Increment and Update Log status
			this.finCount.addAndGet(this.financeList.size());
			if (this.finListSize == this.finCount.get()) {
				this.incomeAmortizationService.updateAmzStatus(EodConstants.PROGRESS_SUCCESS,
						this.projectedAmortization.getAmzLogId()); // 2
			}

		} catch (Exception e) {
			this.incomeAmortizationService.updateAmzStatus(EodConstants.PROGRESS_FAILED,
					this.projectedAmortization.getAmzLogId()); // 3
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
	}

	// getters / setters

	public void setIncomeAmortizationService(IncomeAmortizationService incomeAmortizationService) {
		this.incomeAmortizationService = incomeAmortizationService;
	}

	// unused methods

	/**
	 * 
	 * Method for preparing finance data for amortization
	 * 
	 * @param monthEndDate
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<FinEODEvent> prepareFinDataForAMZ(Date monthEndDate, List<FinanceMain> financeList) {

		/**
		 * Earlier Approach </br>
		 * 
		 * Date curMonthStart = DateUtility.getMonthStartDate(monthEndDate); List<FinanceMain> financeList =
		 * projectedAmortizationService.getFinListForIncomeAMZ(curMonthStart);
		 * 
		 * FinanceProfitDetail Retrieval </br>
		 * 
		 * List<FinanceProfitDetail> finProfitList = this.incomeAmortizationService.getFinProfitList(finRefList);
		 * FinanceProfitDetail finPftDetail = getFinPftDetailRef(finMain.getFinReference(), finProfitList);
		 */

		FinEODEvent finEODEvent = null;
		List<FinEODEvent> finEODEventsList = new ArrayList<FinEODEvent>();

		for (FinanceMain finMain : financeList) {

			// Finance Type from Cache and FinPftDetails
			FinanceType financeType = getFinanceType(finMain.getFinType());
			FinanceProfitDetail finPftDetail = this.incomeAmortizationService
					.getFinProfitForAMZ(finMain.getFinReference());

			finEODEvent = new FinEODEvent();
			finEODEvent.setEventFromDate(monthEndDate);

			finEODEvent.setFinanceMain(finMain);
			finEODEvent.setFinType(financeType);
			finEODEvent.setFinProfitDetail(finPftDetail);

			finEODEventsList.add(finEODEvent);
		}

		return finEODEventsList;
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
	@SuppressWarnings("unused")
	private List<FinEODEvent> prepareFinDataForAMZandAccruals(Date startDate, Date monthEndDate,
			List<FinanceMain> financeList) {

		FinEODEvent finEODEvent = null;
		List<FinanceScheduleDetail> finScheduleDetails = null;
		List<FinEODEvent> finEODEventsList = new ArrayList<FinEODEvent>();

		for (FinanceMain finMain : financeList) {

			// Finance Type from Cache and FinPftDetails
			FinanceType financeType = getFinanceType(finMain.getFinType());
			FinanceProfitDetail finPftDetail = this.incomeAmortizationService
					.getFinProfitForAMZ(finMain.getFinReference());

			Date finMaturityDate = finMain.getMaturityDate();
			finScheduleDetails = getFinScheduleDetails(finMain.getFinReference(), monthEndDate);

			if (!finScheduleDetails.isEmpty()) {

				Date schdMaturityDate = finScheduleDetails.get(finScheduleDetails.size() - 1).getSchDate();
				finMain.setMaturityDate(schdMaturityDate);
				finPftDetail.setMaturityDate(schdMaturityDate);

				if (StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_CANCELLED)) {
					if (schdMaturityDate.compareTo(finMaturityDate) > 0) {
						finMain.setClosingStatus(null);
						finPftDetail.setClosingStatus(null);
					}
				}
			}

			finEODEvent = new FinEODEvent();
			finEODEvent.setEventFromDate(monthEndDate);

			finEODEvent.setFinanceMain(finMain);
			finEODEvent.setFinType(financeType);
			finEODEvent.setFinProfitDetail(finPftDetail);
			finEODEvent.setFinanceScheduleDetails(finScheduleDetails);

			finEODEventsList.add(finEODEvent);
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
	 * @param fintype
	 * @return
	 */
	private final FinanceType getFinanceType(String fintype) {
		return FinanceConfigCache.getCacheFinanceType(StringUtils.trimToEmpty(fintype));

	}

	/**
	 * 
	 * @param finMainRef
	 * @param listprofitDetails
	 * @return
	 */
	@SuppressWarnings("unused")
	private FinanceProfitDetail getFinPftDetailRef(String finMainRef, List<FinanceProfitDetail> listprofitDetails) {

		FinanceProfitDetail profitDetail = null;
		Iterator<FinanceProfitDetail> it = listprofitDetails.iterator();

		while (it.hasNext()) {
			FinanceProfitDetail financeProfitDetail = (FinanceProfitDetail) it.next();
			if (StringUtils.equals(financeProfitDetail.getFinReference(), finMainRef)) {
				profitDetail = financeProfitDetail;
				it.remove();
				break;
			}
		}

		return profitDetail;
	}
}