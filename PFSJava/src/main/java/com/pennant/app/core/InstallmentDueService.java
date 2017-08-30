package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.cache.util.AccountingConfigCache;

public class InstallmentDueService extends ServiceHelper {
	private static final long	serialVersionUID	= 1442146139821584760L;
	private Logger				logger				= Logger.getLogger(InstallmentDueService.class);
	
	@Autowired
	private AccountEngineExecution engineExecution;
	
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
	
	/**
	 * @param custAppDate
	 * @param postdate
	 * @param resultSet
	 * @return
	 * @throws Exception
	 */
	public List<ReturnDataSet> processbackDateInstallmentDues(FinanceDetail financeDetail,
			FinanceProfitDetail profiDetails, Date appDate, boolean post) {
		logger.debug(" Entering ");
		
		List<ReturnDataSet> datasets = new ArrayList<ReturnDataSet>();
		FinanceMain main = financeDetail.getFinScheduleData().getFinanceMain();
		List<FinanceScheduleDetail> list = financeDetail.getFinScheduleData().getFinanceScheduleDetails();

		long accountingID = Long.MIN_VALUE;
		if (StringUtils.isNotBlank(main.getPromotionCode())) {
			accountingID = AccountingConfigCache.getCacheAccountSetID(main.getPromotionCode(),
					AccountEventConstants.ACCEVENT_INSTDATE, FinanceConstants.MODULEID_PROMOTION);
		} else {
			accountingID = AccountingConfigCache.getCacheAccountSetID(main.getFinType(),
					AccountEventConstants.ACCEVENT_INSTDATE, FinanceConstants.MODULEID_FINTYPE);
		}

		if (accountingID == Long.MIN_VALUE) {
			return datasets;
		}
		

		if (main.getFinStartDate().compareTo(DateUtility.getAppDate()) >= 0) {
			return datasets;
		}
		
		//prepare schedule based fees
		List<FinFeeDetail> totalFees = financeDetail.getFinScheduleData().getFinFeeDetailList();
		List<FinFeeScheduleDetail> finFeeSchdDet = new ArrayList<FinFeeScheduleDetail>();
		if (totalFees != null && !totalFees.isEmpty()) {
			for (FinFeeDetail detail : totalFees) {
				for (FinFeeScheduleDetail finFeeScheduleDetail : detail.getFinFeeScheduleDetailList()) {
					finFeeScheduleDetail.setFeeTypeCode(detail.getFeeTypeCode());
					finFeeSchdDet.add(finFeeScheduleDetail);
				}
			}
		}

		//prepare schedule based insurance
		List<FinInsurances> totIns = financeDetail.getFinScheduleData().getFinInsuranceList();
		List<FinSchFrqInsurance> totfinschIns = new ArrayList<FinSchFrqInsurance>();
		if (totIns != null && !totIns.isEmpty()) {
			for (FinInsurances finInsurances : totIns) {
				for (FinSchFrqInsurance finSchFrqInsurance : finInsurances.getFinSchFrqInsurances()) {
					finSchFrqInsurance.setInsuranceType(finInsurances.getInsuranceType());
					totfinschIns.add(finSchFrqInsurance);
				}
			}
		}

		//check the schedule is back dated or not if yes then post them
		for (FinanceScheduleDetail financeScheduleDetail : list) {

			if (financeScheduleDetail.getDefSchdDate().compareTo(DateUtility.getAppDate()) > 0) {
				break;
			}

			FinanceScheduleDetail curSchd = financeScheduleDetail;
			// Installment Due Exist
			BigDecimal dueAmount = curSchd.getPrincipalSchd().add(curSchd.getProfitSchd())
					.subtract(curSchd.getSchdPriPaid()).subtract(curSchd.getSchdPftPaid());

			if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			List<FinFeeScheduleDetail> feelist = new ArrayList<FinFeeScheduleDetail>();
			List<FinSchFrqInsurance> finInsList = new ArrayList<FinSchFrqInsurance>();
			
			dueAmount = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());

			//prepare fee list
			if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
				for (FinFeeScheduleDetail finFeeScheduleDetail : finFeeSchdDet) {
					if (finFeeScheduleDetail.getSchDate().compareTo(curSchd.getSchDate()) == 0) {
						feelist.add(finFeeScheduleDetail);
					}
				}
			}

			dueAmount = curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid());
			if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
				for (FinSchFrqInsurance finSchFrqInsurance : totfinschIns) {
					if (finSchFrqInsurance.getInsSchDate().compareTo(curSchd.getSchDate()) == 0) {
						finInsList.add(finSchFrqInsurance);
					}
				}
			}

			
			AEEvent aeEvent = AEAmounts.procCalAEAmounts(profiDetails, AccountEventConstants.ACCEVENT_INSTDATE,
					curSchd.getSchDate(), curSchd.getSchDate());
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

			if (feelist != null && !feelist.isEmpty()) {
				for (FinFeeScheduleDetail feeSchd : feelist) {
					//"_C" Should be there to post then amount
					dataMap.put(feeSchd.getFeeTypeCode() + "_C", feeSchd.getSchAmount());
					dataMap.put(feeSchd.getFeeTypeCode() + "_SCH", feeSchd.getSchAmount());
					dataMap.put(feeSchd.getFeeTypeCode() + "_P", feeSchd.getPaidAmount());
					dataMap.put(feeSchd.getFeeTypeCode() + "_W", feeSchd.getWaiverAmount());
				}
			}

			if (finInsList != null && !finInsList.isEmpty()) {
				for (FinSchFrqInsurance insschd : finInsList) {
					//"_C" Should be there to post then amount
					dataMap.put(insschd.getInsuranceType() + "_C", insschd.getAmount());
					dataMap.put(insschd.getInsuranceType() + "_SCH", insschd.getAmount());
					dataMap.put(insschd.getInsuranceType() + "_P", insschd.getInsurancePaid());
				}
			}

			aeEvent.setDataMap(dataMap);
			aeEvent.setPostDate(appDate);
			if (post) {
				getPostingsPreparationUtil().postAccounting(aeEvent);
			} else {
				aeEvent = engineExecution.getAccEngineExecResults(aeEvent);
				datasets.addAll(aeEvent.getReturnDataSet());
			}
		}
		logger.debug(" Leaving ");
		return datasets;
	}

}
