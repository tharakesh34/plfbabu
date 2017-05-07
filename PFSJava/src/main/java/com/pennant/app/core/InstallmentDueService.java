package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.eod.util.EODProperties;

public class InstallmentDueService extends ServiceHelper {
	private static final long	serialVersionUID	= 1442146139821584760L;
	private Logger				logger				= Logger.getLogger(InstallmentDueService.class);

	/**
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processDueDatePostings(CustEODEvent custEODEvent) throws Exception {

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {

			List<FinanceScheduleDetail> scheduledetails = finEODEvent.getFinanceScheduleDetails();
			for (FinanceScheduleDetail finSchd : scheduledetails) {
				if (finSchd.getSchDate().compareTo(valueDate) < 0) {
					continue;
				}

				if (finSchd.getSchDate().compareTo(valueDate) > 0) {
					break;
				}

				if (finSchd.getSchDate().compareTo(valueDate) == 0) {
					postInstallmentDues(finEODEvent, finSchd, valueDate);
				}

			}

		}
	}

	/**
	 * @param resultSet
	 * @throws Exception
	 */
	public void postInstallmentDues(FinEODEvent finEODEvent, FinanceScheduleDetail finSchd, Date valueDate)
			throws Exception {
		logger.debug(" Entering ");
		String finType = finEODEvent.getFinType().getFinType();
		boolean isAccountingReq = false;
		List<FinTypeAccounting> acountingSets = EODProperties.getFinanceType(finType).getFinTypeAccountingList();

		for (int i = 0; i < acountingSets.size(); i++) {
			if (!StringUtils.equals(AccountEventConstants.ACCEVENT_INSTDATE, acountingSets.get(i).getEvent())) {
				continue;
			}

			isAccountingReq = true;
			break;
		}

		if (!isAccountingReq) {
			return;
		}

		FinanceProfitDetail profiDetails = finEODEvent.getFinProfitDetail();

		String finRef = finSchd.getFinReference();

		//Amount Codes preparation using FinProfitDetails
		AEEvent aeEvent = new AEEvent();
		if (aeEvent.getAeAmountCodes() == null) {
			aeEvent.setAeAmountCodes(new AEAmountCodes());
		}
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		aeEvent.setFinReference(finRef);
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_INSTDATE);
		aeEvent.setValueDate(valueDate);
		aeEvent.setSchdDate(finSchd.getSchDate());
		aeEvent.setPostDate(DateUtility.getAppDate());
		aeEvent.setFinType(finType);
		aeEvent.setBranch(profiDetails.getFinBranch());

		//TODO: decide required or not
		amountCodes.setdAccrue(BigDecimal.ZERO);

		amountCodes.setInstpft(finSchd.getProfitSchd());
		amountCodes.setInstpri(finSchd.getPrincipalSchd());
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
		HashMap<String, Object> executingMap = amountCodes.getDeclaredFieldValues();

		List<FinFeeScheduleDetail> feelist = finEODEvent.getFinFeeScheduleDetails();
		if (feelist != null && !feelist.isEmpty()) {
			for (FinFeeScheduleDetail feeSchd : feelist) {
				executingMap.put(feeSchd.getFeeTypeCode() + "_SCH", feeSchd.getSchAmount());
				executingMap.put(feeSchd.getFeeTypeCode() + "_P", feeSchd.getPaidAmount());
				executingMap.put(feeSchd.getFeeTypeCode() + "_W", feeSchd.getWaiverAmount());
			}
		}

		List<FinSchFrqInsurance> finInsList = finEODEvent.getFinSchFrqInsurances();

		if (finInsList != null && !finInsList.isEmpty()) {
			for (FinSchFrqInsurance insschd : finInsList) {
				executingMap.put(insschd.getInsuranceType() + "_SCH", insschd.getAmount());
				executingMap.put(insschd.getInsuranceType() + "_P", insschd.getInsurancePaid());
			}
		}

		//DataSet Object preparation for AccountingSet Execution
		FinanceType financeType = getFinanceType(aeEvent.getFinType());
		financeType.getDeclaredFieldValues(executingMap);

		//Postings Process
		postAccountingEOD(aeEvent, executingMap);
		logger.debug(" Leaving ");
	}

}
