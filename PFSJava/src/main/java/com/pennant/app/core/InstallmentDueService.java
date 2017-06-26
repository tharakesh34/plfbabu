package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;

public class InstallmentDueService extends ServiceHelper {
	private static final long	serialVersionUID	= 1442146139821584760L;
	private Logger				logger				= Logger.getLogger(InstallmentDueService.class);

	/**
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processDueDatePostings(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {

			long accountingID = getAccountingID(finEODEvent.getFinanceMain(), AccountEventConstants.ACCEVENT_INSTDATE);

			if (accountingID == Long.MIN_VALUE) {
				return;
			}

			int idx = finEODEvent.getIdxDue();
			if (idx == -1) {
				continue;
			}

			FinanceScheduleDetail curSchd = finEODEvent.getFinanceScheduleDetails().get(idx);
			postInstallmentDues(finEODEvent, curSchd, custEODEvent, accountingID);

		}

		logger.debug(" Leaving ");
	}

	/**
	 * @param resultSet
	 * @throws Exception
	 */
	public void postInstallmentDues(FinEODEvent finEODEvent, FinanceScheduleDetail curSchd, CustEODEvent custEODEvent,
			long accountingID) throws Exception {
		logger.debug(" Entering ");

		String finReference = curSchd.getFinReference();

		BigDecimal dueAmount = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());
		Date valueDate=custEODEvent.getEodValueDate();
		if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
			finEODEvent.setFinFeeScheduleDetails(getFinFeeScheduleDetailDAO().getFeeSchdTPost(finReference, valueDate));
		}

		dueAmount = curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid());
		if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
			finEODEvent.setFinSchFrqInsurances(getFinInsurancesDAO().getInsSchdToPost(finReference, valueDate));
		}

		FinanceProfitDetail profiDetails = finEODEvent.getFinProfitDetail();
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(profiDetails, AccountEventConstants.ACCEVENT_INSTDATE, valueDate,
				curSchd.getSchDate());
		aeEvent.getAcSetIDList().add(accountingID);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setInstpft(curSchd.getProfitSchd());
		amountCodes.setInstpri(curSchd.getPrincipalSchd());
		amountCodes.setInsttot(amountCodes.getInstpft().add(amountCodes.getInstpri()));

		amountCodes.setPftS(profiDetails.getTdSchdPft());
		amountCodes.setPftSP(profiDetails.getTdSchdPftPaid());
		amountCodes.setPftSB(amountCodes.getPftS().subtract(amountCodes.getPftSP()));

		if (amountCodes.getPftSB().compareTo(BigDecimal.ZERO) < 0) {
			amountCodes.setPftSB(BigDecimal.ZERO);
		}

		amountCodes.setPriS(profiDetails.getTdSchdPri());
		amountCodes.setPriSP(profiDetails.getTdSchdPriPaid());
		amountCodes.setPriSB(amountCodes.getPriS().subtract(amountCodes.getPriSP()));

		if (amountCodes.getPriSB().compareTo(BigDecimal.ZERO) < 0) {
			amountCodes.setPriSB(BigDecimal.ZERO);
		}

		HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		List<FinFeeScheduleDetail> feelist = finEODEvent.getFinFeeScheduleDetails();
		if (feelist != null && !feelist.isEmpty()) {
			for (FinFeeScheduleDetail feeSchd : feelist) {
				//"_C" Should be there to post then amount
				dataMap.put(feeSchd.getFeeTypeCode() + "_C", feeSchd.getSchAmount());
				dataMap.put(feeSchd.getFeeTypeCode() + "_SCH", feeSchd.getSchAmount());
				dataMap.put(feeSchd.getFeeTypeCode() + "_P", feeSchd.getPaidAmount());
				dataMap.put(feeSchd.getFeeTypeCode() + "_W", feeSchd.getWaiverAmount());
			}
		}

		List<FinSchFrqInsurance> finInsList = finEODEvent.getFinSchFrqInsurances();
		if (finInsList != null && !finInsList.isEmpty()) {
			for (FinSchFrqInsurance insschd : finInsList) {
				//"_C" Should be there to post then amount
				dataMap.put(insschd.getInsuranceType() + "_C", insschd.getAmount());
				dataMap.put(insschd.getInsuranceType() + "_SCH", insschd.getAmount());
				dataMap.put(insschd.getInsuranceType() + "_P", insschd.getInsurancePaid());
			}
		}

		aeEvent.setDataMap(dataMap);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent.setPostDate(custEODEvent.getCustomer().getCustAppDate());
		//Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		logger.debug(" Leaving ");
	}

}
