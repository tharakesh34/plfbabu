package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ScheduleDueTaxDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.advancepayment.service.AdvancePaymentService;
import com.pennanttech.pff.constants.AccountingEvent;

public class InstallmentDueService extends ServiceHelper {
	private Logger logger = LogManager.getLogger(InstallmentDueService.class);

	private AccountEngineExecution engineExecution;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private AdvancePaymentService advancePaymentService;

	public void processDueDatePostings(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {

			Long accountingID = getAccountingID(finEODEvent.getFinanceMain(), AccountingEvent.INSTDATE);

			if (accountingID == null || accountingID == Long.MIN_VALUE) {
				return;
			}

			int idx = finEODEvent.getIdxDue();
			if (idx == -1) {
				continue;
			}

			FinanceScheduleDetail schd = finEODEvent.getFinanceScheduleDetails().get(idx);
			postInstallmentDues(finEODEvent, schd, custEODEvent, accountingID);
		}
	}

	private void postInstallmentDues(FinEODEvent finEODEvent, FinanceScheduleDetail schd, CustEODEvent custEODEvent,
			long accountingID) {
		FinanceMain fm = finEODEvent.getFinanceMain();
		logger.info("Installment due date postings started for the FinReference {}.", fm.getFinReference());

		String finReference = schd.getFinReference();

		fm.setEventProperties(custEODEvent.getEventProperties());

		BigDecimal dueAmount = schd.getFeeSchd().subtract(schd.getSchdFeePaid());
		Date valueDate = custEODEvent.getEodValueDate();
		if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
			finEODEvent.setFinFeeScheduleDetails(finFeeScheduleDetailDAO.getFeeSchdTPost(finReference, valueDate));
		}

		FinanceProfitDetail fpd = finEODEvent.getFinProfitDetail();
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(fm, fpd, finEODEvent.getFinanceScheduleDetails(),
				AccountingEvent.INSTDATE, valueDate, schd.getSchDate());
		aeEvent.getAcSetIDList().add(accountingID);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setInstpft(schd.getProfitSchd());
		amountCodes.setInstTds(schd.getTDSAmount());
		amountCodes.setInstpri(schd.getPrincipalSchd());
		amountCodes.setInstcpz(schd.getCpzAmount());
		amountCodes.setInsttot(amountCodes.getInstpft().add(amountCodes.getInstpri()));
		amountCodes.setNpa(finEODEvent.isNpaStage());
		advancePaymentService.setIntAdvFlag(fm, amountCodes, true);
		amountCodes.setPftS(fpd.getTdSchdPft());
		amountCodes.setPftSP(fpd.getTdSchdPftPaid());
		amountCodes.setPftSB(amountCodes.getPftS().subtract(amountCodes.getPftSP()));

		if (amountCodes.getPftSB().compareTo(BigDecimal.ZERO) < 0) {
			amountCodes.setPftSB(BigDecimal.ZERO);
		}

		amountCodes.setPriS(fpd.getTdSchdPri());
		amountCodes.setPriSP(fpd.getTdSchdPriPaid());
		amountCodes.setPriSB(amountCodes.getPriS().subtract(amountCodes.getPriSP()));

		if (amountCodes.getPriSB().compareTo(BigDecimal.ZERO) < 0) {
			amountCodes.setPriSB(BigDecimal.ZERO);
		}

		adjustAdvInt(finEODEvent, schd, amountCodes);

		// Provision and Fin Od days Greater than zero
		if (fpd.isProvision() && fpd.getCurODDays() > 0) {
			setProvisionData(amountCodes);
		}

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		List<FinFeeScheduleDetail> feelist = finEODEvent.getFinFeeScheduleDetails();
		if (feelist != null && !feelist.isEmpty()) {
			for (FinFeeScheduleDetail feeSchd : feelist) {
				// "_C" Should be there to post then amount
				String feeTypeCode = feeSchd.getFeeTypeCode();
				dataMap.put(feeTypeCode + "_C", feeSchd.getSchAmount());
				dataMap.put(feeTypeCode + "_SCH", feeSchd.getSchAmount());
				dataMap.put(feeTypeCode + "_P", feeSchd.getPaidAmount());
				dataMap.put(feeTypeCode + "_W", feeSchd.getWaiverAmount());
			}
		}

		aeEvent.setDataMap(dataMap);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent.setPostDate(custEODEvent.getCustomer().getCustAppDate());
		// Postings Process and save all postings related to finance for one time accounts update

		EventProperties eventProperties = fm.getEventProperties();
		aeEvent.setAppDate(eventProperties.getAppDate());
		aeEvent.setAppValueDate(eventProperties.getAppValueDate());
		aeEvent.setEventProperties(eventProperties);

		postAccountingEOD(aeEvent);
		fm.setEodValueDate(valueDate);

		long linkedTranId = aeEvent.getLinkedTranId();
		if (ImplementationConstants.ALW_PROFIT_SCHD_INVOICE && linkedTranId > 0) {
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(fm);
			financeDetail.getFinScheduleData().setFinanceType(finEODEvent.getFinType());
			financeDetail.setFinanceTaxDetail(null);
			financeDetail.setCustomerDetails(null);
			createInovice(financeDetail, schd, linkedTranId);
		}

		// Accrual posted on the installment due postings
		if (aeEvent.isuAmzExists()) {
			fpd.setAmzTillLBD(fpd.getAmzTillLBD().add(aeEvent.getAeAmountCodes().getuAmz()));
			finEODEvent.setUpdLBDPostings(true);
		} else {
			boolean accrualReversalReq = false;
			if (eventProperties.isParameterLoaded()) {
				accrualReversalReq = eventProperties.isAccrualReversalReq();
			} else {
				accrualReversalReq = SysParamUtil.isAllowed(SMTParameterConstants.ACCRUAL_REVERSAL_REQ);
			}

			if (accrualReversalReq) {
				fpd.setAmzTillLBD(fpd.getAmzTillLBD().add(amountCodes.getInstpft()));
				finEODEvent.setUpdLBDPostings(true);
			}
		}

		overdrafLoanService.createBills(custEODEvent);

		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		logger.info("Installment due date postings completed for the FinReference {}.", fm.getFinReference());
	}

	private void createInovice(FinanceDetail fd, FinanceScheduleDetail schd, long linkedTranId) {
		BigDecimal pftAmount = BigDecimal.ZERO;
		BigDecimal priAmount = BigDecimal.ZERO;

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		EventProperties eventProperties = fm.getEventProperties();

		switch (ImplementationConstants.GST_SCHD_CAL_ON) {
		case FinanceConstants.GST_SCHD_CAL_ON_PFT:
			pftAmount = schd.getProfitSchd();
			priAmount = schd.getPrincipalSchd();
			break;
		case FinanceConstants.GST_SCHD_CAL_ON_PRI:
			priAmount = schd.getPrincipalSchd();
			break;
		case FinanceConstants.GST_SCHD_CAL_ON_EMI:
			pftAmount = schd.getProfitSchd();
			break;
		default:
			break;
		}

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setLinkedTranId(linkedTranId);
		invoiceDetail.setFinanceDetail(fd);
		invoiceDetail.setPftAmount(pftAmount);
		invoiceDetail.setPriAmount(priAmount);
		invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED);
		invoiceDetail.setEventProperties(eventProperties);

		Long invoiceID = gstInvoiceTxnService.schdDueTaxInovicePrepration(invoiceDetail);

		if (schd.getFinReference() == null) {
			schd.setFinID(fm.getFinID());
			schd.setFinReference(fm.getFinReference());
		}

		saveDueTaxDetail(schd, invoiceID);
	}

	private void saveDueTaxDetail(FinanceScheduleDetail schd, Long invoiceID) {
		String gstShdCalOn = ImplementationConstants.GST_SCHD_CAL_ON;

		ScheduleDueTaxDetail sdtd = new ScheduleDueTaxDetail();

		sdtd.setFinID(schd.getFinID());
		sdtd.setFinReference(schd.getFinReference());
		sdtd.setSchDate(schd.getSchDate());
		sdtd.setTaxType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED);
		sdtd.setTaxCalcOn(gstShdCalOn);

		BigDecimal invoiceAmt = BigDecimal.ZERO;

		switch (gstShdCalOn) {
		case FinanceConstants.GST_SCHD_CAL_ON_PFT:
			invoiceAmt = schd.getProfitSchd();
			break;
		case FinanceConstants.GST_SCHD_CAL_ON_PRI:
			invoiceAmt = schd.getPrincipalSchd();
			break;
		case FinanceConstants.GST_SCHD_CAL_ON_EMI:
			invoiceAmt = schd.getPrincipalSchd().add(schd.getProfitSchd());
			break;
		default:
			break;
		}

		sdtd.setAmount(invoiceAmt);
		sdtd.setInvoiceID(invoiceID);

		financeScheduleDetailDAO.saveSchDueTaxDetail(sdtd);
	}

	private void setProvisionData(AEAmountCodes amountCodes) {
		amountCodes.setInstpftPr(amountCodes.getInstpft());
		amountCodes.setInstpriPr(amountCodes.getInstpri());
		amountCodes.setdAmzPr(amountCodes.getdAmz());

		amountCodes.setInstpft(BigDecimal.ZERO);
		amountCodes.setInstpri(BigDecimal.ZERO);
		amountCodes.setdAmz(BigDecimal.ZERO);
		amountCodes.setInsttot(BigDecimal.ZERO);
	}

	/**
	 * @param custAppDate
	 * @param postdate
	 * @param resultSet
	 * @return
	 */
	public List<ReturnDataSet> processbackDateInstallmentDues(FinanceDetail fd, FinanceProfitDetail pfd, Date appDate,
			boolean post, String postBranch) {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> datasets = new ArrayList<>();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		List<FinanceScheduleDetail> schedules = fd.getFinScheduleData().getFinanceScheduleDetails();
		EventProperties eventProperties = fm.getEventProperties();

		Long accountingID = Long.MIN_VALUE;
		// FIXME: PV: 28AUG19. No Separate Accounting for Promotion
		/*
		 * if (StringUtils.isNotBlank(main.getPromotionCode())) { accountingID =
		 * AccountingConfigCache.getCacheAccountSetID(main.getPromotionCode(), AccountEventConstants.ACCEVENT_INSTDATE,
		 * FinanceConstants.MODULEID_PROMOTION); } else { accountingID =
		 * AccountingConfigCache.getCacheAccountSetID(main.getFinType(), AccountEventConstants.ACCEVENT_INSTDATE,
		 * FinanceConstants.MODULEID_FINTYPE); }
		 */

		accountingID = AccountingConfigCache.getCacheAccountSetID(fm.getFinType(), AccountingEvent.INSTDATE,
				FinanceConstants.MODULEID_FINTYPE);

		if (accountingID == null || accountingID == Long.MIN_VALUE) {
			logger.debug(Literal.LEAVING);
			return datasets;
		}

		if (fm.getFinStartDate().compareTo(appDate) >= 0) {
			logger.debug(Literal.LEAVING);
			return datasets;
		}

		// prepare schedule based fees
		List<FinFeeDetail> totalFees = fd.getFinScheduleData().getFinFeeDetailList();
		List<FinFeeScheduleDetail> scheduleFees = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(totalFees)) {
			for (FinFeeDetail detail : totalFees) {
				String feeTypeCode = detail.getFeeTypeCode();
				List<FinFeeScheduleDetail> feeSchedules = detail.getFinFeeScheduleDetailList();
				for (FinFeeScheduleDetail finFeeScheduleDetail : feeSchedules) {
					finFeeScheduleDetail.setFeeTypeCode(feeTypeCode);
					scheduleFees.add(finFeeScheduleDetail);
				}
			}
		}

		BigDecimal totPft = BigDecimal.ZERO;
		// check the schedule is back dated or not if yes then post them
		for (FinanceScheduleDetail curSchd : schedules) {

			if (curSchd.getDefSchdDate().compareTo(SysParamUtil.getAppDate()) > 0) {
				break;
			}

			if (FinanceConstants.FLAG_BPI.equals(curSchd.getBpiOrHoliday())) {
				if (fm.isAlwBPI() && FinanceConstants.BPI_DISBURSMENT.equals(fm.getBpiTreatment())) {
					continue;
				}
			}

			// Installment Due Exist
			BigDecimal dueAmount = curSchd.getPrincipalSchd().add(curSchd.getProfitSchd())
					.subtract(curSchd.getSchdPriPaid()).subtract(curSchd.getSchdPftPaid());
			totPft = totPft.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));

			if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			List<FinFeeScheduleDetail> feelist = new ArrayList<>();

			dueAmount = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());
			Date schDate = curSchd.getSchDate();

			// prepare fee list
			if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
				for (FinFeeScheduleDetail scheduleFee : scheduleFees) {
					if (scheduleFee.getSchDate().compareTo(schDate) == 0) {
						feelist.add(scheduleFee);
					}
				}
			}

			AEEvent aeEvent = AEAmounts.procCalAEAmounts(fm, pfd, schedules, AccountingEvent.INSTDATE, schDate,
					schDate);
			aeEvent.getAcSetIDList().add(accountingID);

			aeEvent.setPostingUserBranch(postBranch);
			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
			amountCodes.setInstpft(curSchd.getProfitSchd());
			amountCodes.setInstpri(curSchd.getPrincipalSchd());
			amountCodes.setInsttot(amountCodes.getInstpft().add(amountCodes.getInstpri()));

			amountCodes.setPftS(pfd.getTdSchdPft());
			amountCodes.setPftSP(pfd.getTdSchdPftPaid());
			amountCodes.setPftSB(amountCodes.getPftS().subtract(amountCodes.getPftSP()));

			if (amountCodes.getPftSB().compareTo(BigDecimal.ZERO) < 0) {
				amountCodes.setPftSB(BigDecimal.ZERO);
			}

			amountCodes.setPriS(pfd.getTdSchdPri());
			amountCodes.setPriSP(pfd.getTdSchdPriPaid());
			amountCodes.setPriSB(amountCodes.getPriS().subtract(amountCodes.getPriSP()));

			if (amountCodes.getPriSB().compareTo(BigDecimal.ZERO) < 0) {
				amountCodes.setPriSB(BigDecimal.ZERO);
			}

			Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

			if (CollectionUtils.isNotEmpty(feelist)) {
				for (FinFeeScheduleDetail feeSchd : feelist) {
					// "_C" Should be there to post then amount
					dataMap.put(feeSchd.getFeeTypeCode() + "_C", feeSchd.getSchAmount());
					dataMap.put(feeSchd.getFeeTypeCode() + "_SCH", feeSchd.getSchAmount());
					dataMap.put(feeSchd.getFeeTypeCode() + "_P", feeSchd.getPaidAmount());
					dataMap.put(feeSchd.getFeeTypeCode() + "_W", feeSchd.getWaiverAmount());
				}
			}

			aeEvent.setDataMap(dataMap);
			aeEvent.setPostDate(appDate);
			if (post) {
				aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

				long linkedTranId = aeEvent.getLinkedTranId();
				if (ImplementationConstants.ALW_PROFIT_SCHD_INVOICE && linkedTranId > 0) {
					createInovice(fd, curSchd, linkedTranId);
				}
			} else {
				engineExecution.getAccEngineExecResults(aeEvent);
				datasets.addAll(aeEvent.getReturnDataSet());
			}
		}

		pfd.setAmzTillLBD(pfd.getAmzTillLBD().add(totPft));
		logger.debug(Literal.LEAVING);
		return datasets;
	}

	private void adjustAdvInt(FinEODEvent finEODEvent, FinanceScheduleDetail schd, AEAmountCodes amountCodes) {
		BigDecimal advInst = advancePaymentService.getBalAdvIntAmt(finEODEvent.getFinanceMain(), schd.getSchDate());

		FinanceProfitDetail fpd = finEODEvent.getFinProfitDetail();
		BigDecimal acrTillLBD = fpd.getAcrTillLBD();
		amountCodes.setAccrTillBd(acrTillLBD);

		BigDecimal profitSchd = schd.getProfitSchd();
		if (advInst.compareTo(BigDecimal.ZERO) > 0 && acrTillLBD.compareTo(BigDecimal.ZERO) == 0
				&& profitSchd.compareTo(advInst) >= 0) {
			amountCodes.setAdvInst(advInst);
		} else if (advInst.compareTo(BigDecimal.ZERO) > 0 && acrTillLBD.compareTo(advInst) < 0
				&& acrTillLBD.compareTo(BigDecimal.ZERO) > 0 && advInst.compareTo(acrTillLBD) > 0
				&& advInst.compareTo(profitSchd) <= 0) {
			amountCodes.setAdvInst(advInst.subtract(acrTillLBD));
			amountCodes.setdAmz(advInst.compareTo(profitSchd) < 0 ? profitSchd : amountCodes.getdAmz());
		} else if (amountCodes.getAccrue().compareTo(acrTillLBD) > 0 && profitSchd.compareTo(advInst) > 0) {
			amountCodes.setdAmz(amountCodes.getAccrue().subtract(acrTillLBD));
		} else if (profitSchd.compareTo(advInst) < 0) {
			amountCodes.setAdvInst(profitSchd);
		}
		if (profitSchd.compareTo(acrTillLBD) > 0 && advInst.compareTo(BigDecimal.ZERO) > 0
				&& advInst.compareTo(profitSchd) > 0) {
			amountCodes.setAdvInst(profitSchd.subtract(acrTillLBD));
			amountCodes.setdAmz(amountCodes.getAdvInst());
		}
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public void setAdvancePaymentService(AdvancePaymentService advancePaymentService) {
		this.advancePaymentService = advancePaymentService;
	}

}