package com.pennant.app.core;

import java.util.Date;
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

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {

			List<FinanceDisbursement> disbrusments = finEODEvent.getFinanceDisbursements();
			for (FinanceDisbursement financeDisbursement : disbrusments) {
				if (financeDisbursement.getDisbDate().compareTo(valueDate) != 0) {
					continue;
				}

				if (financeDisbursement.getDisbDate().compareTo(valueDate) == 0) {
					postFutureDisbursement(valueDate, finEODEvent, financeDisbursement);
				}
			}

		}

	}

	/**
	 * @param resultSet
	 * @throws Exception
	 */
	public void postFutureDisbursement(Date valueDate, FinEODEvent finEODEvent, FinanceDisbursement curDisbursment)
			throws Exception {
		logger.debug(" Entering ");
		long accountingID = getAccountingID(finEODEvent.getFinanceMain(), AccountEventConstants.ACCEVENT_ADDDBSN);
		if (accountingID == Long.MIN_VALUE) {
			return;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(finEODEvent.getFinProfitDetail(),
				AccountEventConstants.ACCEVENT_ADDDBSN, valueDate, valueDate);
		aeEvent.getAcSetIDList().add(accountingID);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setDisburse(curDisbursment.getDisbAmount().add(curDisbursment.getFeeChargeAmt()));
		aeEvent.setDataMap(aeEvent.getAeAmountCodes().getDeclaredFieldValues());
		//Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		curDisbursment.setDisbDisbursed(true);
		curDisbursment.setLinkedTranId(aeEvent.getLinkedTranId());
		getFinanceDisbursementDAO().updateBatchDisb(curDisbursment, "");
		logger.debug(" Leaving ");
	}

}
