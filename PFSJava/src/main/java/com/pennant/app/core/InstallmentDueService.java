package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
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
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;

public class InstallmentDueService extends ServiceHelper {
	private static final long serialVersionUID = 1442146139821584760L;
	private Logger logger = Logger.getLogger(InstallmentDueService.class);

	private AccountEngineExecution engineExecution;
	private GSTInvoiceTxnService gstInvoiceTxnService;

	/**
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processDueDatePostings(CustEODEvent custEODEvent) throws Exception {
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param resultSet
	 * @throws Exception
	 */
	public void postInstallmentDues(FinEODEvent finEODEvent, FinanceScheduleDetail curSchd, CustEODEvent custEODEvent,
			long accountingID) throws Exception {
		logger.debug(Literal.ENTERING);

		String finReference = curSchd.getFinReference();

		FinanceMain fm = finEODEvent.getFinanceMain();

		BigDecimal dueAmount = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());
		Date valueDate = custEODEvent.getEodValueDate();
		if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
			finEODEvent.setFinFeeScheduleDetails(finFeeScheduleDetailDAO.getFeeSchdTPost(finReference, valueDate));
		}

		FinanceProfitDetail profiDetails = finEODEvent.getFinProfitDetail();
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(fm, profiDetails, finEODEvent.getFinanceScheduleDetails(),
				AccountEventConstants.ACCEVENT_INSTDATE, valueDate, curSchd.getSchDate());
		aeEvent.getAcSetIDList().add(accountingID);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setInstpft(curSchd.getProfitSchd());
		amountCodes.setInstTds(curSchd.getTDSAmount());
		amountCodes.setInstpri(curSchd.getPrincipalSchd());
		amountCodes.setInstcpz(curSchd.getCpzAmount());
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

		//Provision and Fin Od days Greater than zero
		if (profiDetails.isProvision() && profiDetails.getCurODDays() > 0) {
			setProvisionData(amountCodes);
		}

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

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

		aeEvent.setDataMap(dataMap);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent.setPostDate(custEODEvent.getCustomer().getCustAppDate());
		//Postings Process and save all postings related to finance for one time accounts update

		aeEvent = postAccountingEOD(aeEvent);
		fm.setEodValueDate(valueDate);

		long linkedTranId = aeEvent.getLinkedTranId();
		if (ImplementationConstants.ALW_PROFIT_SCHD_INVOICE && linkedTranId > 0) {
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(finEODEvent.getFinanceMain());
			financeDetail.getFinScheduleData().setFinanceType(finEODEvent.getFinType());
			financeDetail.setFinanceTaxDetail(null);
			financeDetail.setCustomerDetails(null);
			createInovice(financeDetail, curSchd, linkedTranId);
		}

		//Accrual posted on the installment due postings
		if (aeEvent.isuAmzExists()) {
			profiDetails.setAmzTillLBD(profiDetails.getAmzTillLBD().add(aeEvent.getAeAmountCodes().getuAmz()));
			finEODEvent.setUpdLBDPostings(true);
		} else if (SysParamUtil.isAllowed(SMTParameterConstants.ACCRUAL_REVERSAL_REQ)) {
			profiDetails.setAmzTillLBD(profiDetails.getAmzTillLBD().add(amountCodes.getInstpft()));
			finEODEvent.setUpdLBDPostings(true);
		}

		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		logger.debug(Literal.LEAVING);
	}

	private void createInovice(FinanceDetail financeDetail, FinanceScheduleDetail curSchd, long linkedTranId) {
		BigDecimal pftAmount = BigDecimal.ZERO;
		BigDecimal priAmount = BigDecimal.ZERO;

		switch (ImplementationConstants.GST_SCHD_CAL_ON) {
		case FinanceConstants.GST_SCHD_CAL_ON_PFT:
			pftAmount = curSchd.getProfitSchd();
			priAmount = curSchd.getPrincipalSchd();
			break;
		case FinanceConstants.GST_SCHD_CAL_ON_PRI:
			priAmount = curSchd.getPrincipalSchd();
			break;
		case FinanceConstants.GST_SCHD_CAL_ON_EMI:
			pftAmount = curSchd.getProfitSchd();
			break;
		default:
			break;
		}

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setLinkedTranId(linkedTranId);
		invoiceDetail.setFinanceDetail(financeDetail);
		invoiceDetail.setPftAmount(pftAmount);
		invoiceDetail.setPriAmount(priAmount);
		invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED);

		Long invoiceID = gstInvoiceTxnService.schdDueTaxInovicePrepration(invoiceDetail);

		saveDueTaxDetail(curSchd, invoiceID);
	}

	/**
	 * Method for saving Schedule Due Tax Details
	 */
	private void saveDueTaxDetail(FinanceScheduleDetail curSchd, Long invoiceID) {
		ScheduleDueTaxDetail dueTaxDetail = new ScheduleDueTaxDetail();
		dueTaxDetail.setFinReference(curSchd.getFinReference());
		dueTaxDetail.setSchDate(curSchd.getSchDate());
		dueTaxDetail.setTaxType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED);
		dueTaxDetail.setTaxCalcOn(ImplementationConstants.GST_SCHD_CAL_ON);

		BigDecimal invoiceAmt = BigDecimal.ZERO;
		switch (ImplementationConstants.GST_SCHD_CAL_ON) {
		case FinanceConstants.GST_SCHD_CAL_ON_PFT:
			invoiceAmt = curSchd.getProfitSchd();
			break;
		case FinanceConstants.GST_SCHD_CAL_ON_PRI:
			invoiceAmt = curSchd.getPrincipalSchd();
			break;
		case FinanceConstants.GST_SCHD_CAL_ON_EMI:
			invoiceAmt = curSchd.getPrincipalSchd().add(curSchd.getProfitSchd());
			break;
		default:
			break;
		}

		dueTaxDetail.setAmount(invoiceAmt);
		dueTaxDetail.setInvoiceID(invoiceID);

		financeScheduleDetailDAO.saveSchDueTaxDetail(dueTaxDetail);
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
	 * @throws Exception
	 */
	public List<ReturnDataSet> processbackDateInstallmentDues(FinanceDetail financeDetail,
			FinanceProfitDetail profiDetails, Date appDate, boolean post, String postBranch) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> datasets = new ArrayList<ReturnDataSet>();
		FinanceMain main = financeDetail.getFinScheduleData().getFinanceMain();
		List<FinanceScheduleDetail> list = financeDetail.getFinScheduleData().getFinanceScheduleDetails();

		long accountingID = Long.MIN_VALUE;
		//FIXME: PV:  28AUG19. No Separate Accounting for Promotion
		/*
		 * if (StringUtils.isNotBlank(main.getPromotionCode())) { accountingID =
		 * AccountingConfigCache.getCacheAccountSetID(main.getPromotionCode(), AccountEventConstants.ACCEVENT_INSTDATE,
		 * FinanceConstants.MODULEID_PROMOTION); } else { accountingID =
		 * AccountingConfigCache.getCacheAccountSetID(main.getFinType(), AccountEventConstants.ACCEVENT_INSTDATE,
		 * FinanceConstants.MODULEID_FINTYPE); }
		 */

		accountingID = AccountingConfigCache.getCacheAccountSetID(main.getFinType(),
				AccountEventConstants.ACCEVENT_INSTDATE, FinanceConstants.MODULEID_FINTYPE);

		if (accountingID == Long.MIN_VALUE) {
			return datasets;
		}

		if (main.getFinStartDate().compareTo(SysParamUtil.getAppDate()) >= 0) {
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

		//check the schedule is back dated or not if yes then post them
		for (FinanceScheduleDetail financeScheduleDetail : list) {

			if (financeScheduleDetail.getDefSchdDate().compareTo(SysParamUtil.getAppDate()) > 0) {
				break;
			}

			if (StringUtils.equals(FinanceConstants.FLAG_BPI, financeScheduleDetail.getBpiOrHoliday())) {
				if (main.isAlwBPI() && StringUtils.equals(FinanceConstants.BPI_DISBURSMENT, main.getBpiTreatment())) {
					continue;
				}
			}

			FinanceScheduleDetail curSchd = financeScheduleDetail;
			// Installment Due Exist
			BigDecimal dueAmount = curSchd.getPrincipalSchd().add(curSchd.getProfitSchd())
					.subtract(curSchd.getSchdPriPaid()).subtract(curSchd.getSchdPftPaid());

			if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			List<FinFeeScheduleDetail> feelist = new ArrayList<FinFeeScheduleDetail>();

			dueAmount = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());

			//prepare fee list
			if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
				for (FinFeeScheduleDetail finFeeScheduleDetail : finFeeSchdDet) {
					if (finFeeScheduleDetail.getSchDate().compareTo(curSchd.getSchDate()) == 0) {
						feelist.add(finFeeScheduleDetail);
					}
				}
			}

			AEEvent aeEvent = AEAmounts.procCalAEAmounts(main, profiDetails, list,
					AccountEventConstants.ACCEVENT_INSTDATE, curSchd.getSchDate(), curSchd.getSchDate());
			aeEvent.getAcSetIDList().add(accountingID);

			aeEvent.setPostingUserBranch(postBranch);
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

			Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

			if (feelist != null && !feelist.isEmpty()) {
				for (FinFeeScheduleDetail feeSchd : feelist) {
					//"_C" Should be there to post then amount
					dataMap.put(feeSchd.getFeeTypeCode() + "_C", feeSchd.getSchAmount());
					dataMap.put(feeSchd.getFeeTypeCode() + "_SCH", feeSchd.getSchAmount());
					dataMap.put(feeSchd.getFeeTypeCode() + "_P", feeSchd.getPaidAmount());
					dataMap.put(feeSchd.getFeeTypeCode() + "_W", feeSchd.getWaiverAmount());
				}
			}

			aeEvent.setDataMap(dataMap);
			aeEvent.setPostDate(appDate);
			if (post) {
				aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);

				if (SysParamUtil.isAllowed(SMTParameterConstants.ACCRUAL_REVERSAL_REQ)) {
					profiDetails.setAmzTillLBD(profiDetails.getAmzTillLBD().add(amountCodes.getInstpft()));
				}

				long linkedTranId = aeEvent.getLinkedTranId();
				if (ImplementationConstants.ALW_PROFIT_SCHD_INVOICE && linkedTranId > 0) {
					createInovice(financeDetail, curSchd, linkedTranId);
				}
			} else {
				aeEvent = engineExecution.getAccEngineExecResults(aeEvent);
				datasets.addAll(aeEvent.getReturnDataSet());
			}

		}
		logger.debug(Literal.LEAVING);
		return datasets;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

}
