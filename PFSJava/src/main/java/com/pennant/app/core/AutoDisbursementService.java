package com.pennant.app.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.eod.util.EODProperties;

public class AutoDisbursementService extends ServiceHelper {
	private static final long		serialVersionUID	= 1442146139821584760L;
	private Logger					logger				= Logger.getLogger(AutoDisbursementService.class);


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
		String finType = finEODEvent.getFinType().getFinType();
		boolean isAccountingReq = false;
		List<FinTypeAccounting> acountingSets = EODProperties.getFinanceType(finType).getFinTypeAccountingList();

		for (int i = 0; i < acountingSets.size(); i++) {
			if (!StringUtils.equals(AccountEventConstants.ACCEVENT_ADDDBSN, acountingSets.get(i).getEvent())) {
				continue;
			}

			isAccountingReq = true;
			break;
		}

		if (!isAccountingReq) {
			return;
		}

		String finRef = curDisbursment.getFinReference();

		AEEvent aeEvent = new AEEvent();
		if (aeEvent.getAeAmountCodes() == null) {
			aeEvent.setAeAmountCodes(new AEAmountCodes());
		}
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		aeEvent.setFinReference(finRef);
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_ADDDBSN);
		aeEvent.setValueDate(valueDate);
		aeEvent.setSchdDate(curDisbursment.getDisbDate());
		aeEvent.setPostDate(DateUtility.getAppDate());
		aeEvent.setFinType(finType);
		aeEvent.setBranch(finEODEvent.getFinanceMain().getFinBranch());
		amountCodes.setDisburse(curDisbursment.getDisbAmount().add(curDisbursment.getFeeChargeAmt()));
		HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		//Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent, dataMap);
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		
		curDisbursment.setDisbDisbursed(true);
		curDisbursment.setLinkedTranId(aeEvent.getLinkedTranId());
		getFinanceDisbursementDAO().updateBatchDisb(curDisbursment, "");
		logger.debug(" Leaving ");
	}

}
