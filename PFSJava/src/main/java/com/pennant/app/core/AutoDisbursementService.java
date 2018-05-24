package com.pennant.app.core;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;

public class AutoDisbursementService extends ServiceHelper {
	private static final long	serialVersionUID	= 1442146139821584760L;
	private Logger				logger				= Logger.getLogger(AutoDisbursementService.class);

	/**
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processDisbursementPostings(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {

			if (finEODEvent.getIdxDisb() < 0) {
				continue;
			}

			String finReference = finEODEvent.getFinanceMain().getFinReference();

			finEODEvent.setFinanceDisbursements(getFinanceDisbursementDAO().getDisbursementToday(finReference,
					custEODEvent.getEodValueDate()));

			List<FinanceDisbursement> disbrusments = finEODEvent.getFinanceDisbursements();
			for (FinanceDisbursement financeDisbursement : disbrusments) {
				postFutureDisbursement(custEODEvent, finEODEvent, financeDisbursement);
			}

		}

		logger.debug(" Leaving ");

	}

	/**
	 * @param resultSet
	 * @throws Exception
	 */
	public void postFutureDisbursement(CustEODEvent custEODEvent, FinEODEvent finEODEvent,
			FinanceDisbursement curDisbursment) throws Exception {
		logger.debug(" Entering ");
		long accountingID = getAccountingID(finEODEvent.getFinanceMain(), AccountEventConstants.ACCEVENT_ADDDBSN);

		if (accountingID == Long.MIN_VALUE) {
			return;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(finEODEvent.getFinProfitDetail(),finEODEvent.getFinanceScheduleDetails(),
				AccountEventConstants.ACCEVENT_ADDDBSN, custEODEvent.getEodValueDate(), custEODEvent.getEodValueDate());
		aeEvent.getAcSetIDList().add(accountingID);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setDisburse(curDisbursment.getDisbAmount().add(curDisbursment.getFeeChargeAmt()));
		aeEvent.setDataMap(aeEvent.getAeAmountCodes().getDeclaredFieldValues());
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent.setPostDate(custEODEvent.getCustomer().getCustAppDate());
		//Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		curDisbursment.setDisbDisbursed(true);
		//FIXME how to handle in case of cancellation
		curDisbursment.setLinkedTranId(aeEvent.getLinkedTranId());
		curDisbursment.setPosted(true);
		logger.debug(" Leaving ");
	}

}
