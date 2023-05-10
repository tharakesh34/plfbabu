package com.pennant.backend.service.incomeamortization.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ProjectedAmortization;
import com.pennant.backend.service.incomeamortization.IncomeAmortizationService;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class AmortizationProcess extends Thread {
	private static final Logger logger = LogManager.getLogger(AmortizationProcess.class);

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

		long amzLogId = projectedAmortization.getAmzLogId();
		try {

			// Start Amortization Process
			incomeAmortizationService.processAmortization(financeList, projectedAmortization.getMonthEndDate());

			// Increment and Update Log status
			finCount.addAndGet(financeList.size());
			if (finListSize == finCount.get()) {
				incomeAmortizationService.updateAmzStatus(EodConstants.PROGRESS_SUCCESS, amzLogId);
			}

		} catch (Exception e) {
			incomeAmortizationService.updateAmzStatus(EodConstants.PROGRESS_FAILED, amzLogId);
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void setIncomeAmortizationService(IncomeAmortizationService incomeAmortizationService) {
		this.incomeAmortizationService = incomeAmortizationService;
	}

}