package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.backend.model.finance.AdvancePayment;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.AdvancePaymentUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class AdvansePaymentService extends ServiceHelper {
	private static final long serialVersionUID = 1442146139821584760L;
	private Logger logger = Logger.getLogger(AdvansePaymentService.class);

	/**
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processAdvansePayments(CustEODEvent custEODEvent) throws Exception {
		logger.debug(Literal.ENTERING);

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		FinanceMain fm = null;
		for (FinEODEvent finEODEvent : finEODEvents) {

			fm = finEODEvent.getFinanceMain();

			if (fm.getGrcAdvType() == null || fm.getAdvType() == null) {
				continue;
			}

			long accountingID = getAccountingID(fm, AccountEventConstants.ACCEVENT_REPAY);

			if (accountingID == Long.MIN_VALUE) {
				continue;
			}

			int idx = finEODEvent.getIdxDue();
			if (idx == -1) {
				continue;
			}

			FinanceScheduleDetail curSchd = finEODEvent.getFinanceScheduleDetails().get(idx);
			postInstallmentDues(finEODEvent, curSchd, custEODEvent, accountingID);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param resultSet
	 * @throws Exception
	 */
	public void postInstallmentDues(FinEODEvent finEODEvent, FinanceScheduleDetail curSchd, CustEODEvent custEODEvent,
			long accountingID) throws Exception {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = finEODEvent.getFinanceMain();
		Date valueDate = custEODEvent.getEodValueDate();
		List<FinExcessAmount> excessAmounts = finEODEvent.getFinExcessAmounts();

		FinanceProfitDetail profiDetails = finEODEvent.getFinProfitDetail();
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(profiDetails, finEODEvent.getFinanceScheduleDetails(),
				AccountEventConstants.ACCEVENT_REPAY, valueDate, curSchd.getSchDate());
		aeEvent.getAcSetIDList().add(accountingID);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		BigDecimal schdPriDue = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
		BigDecimal schdIntDue = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());

		AdvancePayment advancePayment = new AdvancePayment(fm.getGrcAdvType(), fm.getAdvType(),
				fm.getGrcPeriodEndDate());
		advancePayment.setExcessAmounts(excessAmounts);
		advancePayment.setSchdPriDue(schdPriDue);
		advancePayment.setSchdIntDue(schdIntDue);
		advancePayment.setValueDate(valueDate);

		AdvancePaymentUtil.calculateDue(advancePayment);

		amountCodes.setIntAdjusted(advancePayment.getIntAdjusted());
		amountCodes.setIntAdvAvailable(advancePayment.getIntAdvAvailable());
		amountCodes.setIntDue(advancePayment.getIntDue());
		amountCodes.setEmiAdjusted(advancePayment.getEmiAdjusted());
		amountCodes.setEmiAdvAvailable(advancePayment.getEmiAdvAvailable());
		amountCodes.setEmiDue(advancePayment.getEmiDue());

		HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		aeEvent.setDataMap(dataMap);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent.setPostDate(custEODEvent.getCustomer().getCustAppDate());
		//Postings Process and save all postings related to finance for one time accounts update

		aeEvent = postAccountingEOD(aeEvent);

		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		logger.debug(Literal.LEAVING);
	}

	//FIXME Back value

}
