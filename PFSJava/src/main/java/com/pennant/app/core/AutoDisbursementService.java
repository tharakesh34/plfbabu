package com.pennant.app.core;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.AEAmounts;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;

public class AutoDisbursementService extends ServiceHelper {
	private Logger logger = LogManager.getLogger(AutoDisbursementService.class);

	public void processDisbursementPostings(CustEODEvent custEODEvent) {
		logger.debug(Literal.ENTERING);

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date eodDate = custEODEvent.getEodValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {
			if (finEODEvent.getIdxDisb() < 0) {
				continue;
			}

			FinanceMain fm = finEODEvent.getFinanceMain();
			long finID = fm.getFinID();

			finEODEvent.setFinanceDisbursements(financeDisbursementDAO.getDisbursementToday(finID, eodDate));

			List<FinanceDisbursement> disbrusments = finEODEvent.getFinanceDisbursements();
			for (FinanceDisbursement financeDisbursement : disbrusments) {
				postFutureDisbursement(custEODEvent, finEODEvent, financeDisbursement);
			}

		}

		logger.debug(Literal.LEAVING);
	}

	public void postFutureDisbursement(CustEODEvent custEODEvent, FinEODEvent finEODEvent,
			FinanceDisbursement curDisbursment) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = finEODEvent.getFinanceMain();
		Long accountingID = getAccountingID(fm, AccountingEvent.ADDDBSN);

		if (accountingID == null || accountingID == Long.MIN_VALUE) {
			logger.debug(Literal.LEAVING);
			return;
		}

		FinanceProfitDetail pfd = finEODEvent.getFinProfitDetail();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
		Date eodValueDate = custEODEvent.getEodValueDate();
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(fm, pfd, schedules, AccountingEvent.ADDDBSN, eodValueDate,
				eodValueDate);
		aeEvent.getAcSetIDList().add(accountingID);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setDisburse(curDisbursment.getDisbAmount().add(curDisbursment.getFeeChargeAmt()));
		aeEvent.setDataMap(aeEvent.getAeAmountCodes().getDeclaredFieldValues());
		Customer customer = custEODEvent.getCustomer();
		aeEvent.setCustAppDate(customer.getCustAppDate());
		aeEvent.setPostDate(customer.getCustAppDate());
		// Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		// FIXME how to handle in case of cancellation
		curDisbursment.setLinkedTranId(aeEvent.getLinkedTranId());
		curDisbursment.setPosted(true);

		logger.debug(Literal.LEAVING);
	}

}
