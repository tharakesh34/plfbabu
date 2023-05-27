/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : SOAReportGenerationServiceImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 5-09-2012 *
 * 
 * Modified Date : 5-09-2012 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 5-09-2012 Pennant 0.1 * 24-05-2018 Srikanth 0.2 Merge the Code From Bajaj To Core * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.reports.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.resource.Labels;

import com.google.common.collect.ComparisonChain;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FeeWaiverDetailDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.FinODCAmountDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.reports.SOAReportGenerationDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.AdviseDueTaxDetail;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeRefundDetails;
import com.pennant.backend.model.finance.FinFeeRefundHeader;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinOverDueCharges;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.LinkedFinances;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.RestructureCharge;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.systemmasters.ApplicantDetail;
import com.pennant.backend.model.systemmasters.InterestRateDetail;
import com.pennant.backend.model.systemmasters.OtherFinanceDetail;
import com.pennant.backend.model.systemmasters.SOAFeeDetails;
import com.pennant.backend.model.systemmasters.SOASummaryReport;
import com.pennant.backend.model.systemmasters.SOATransactionReport;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.LinkedFinancesService;
import com.pennant.backend.service.reports.SOAReportGenerationService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.extension.LPPExtension;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.extension.feature.SOAExtensionService;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.soa.SOAReportService;

public class SOAReportGenerationServiceImpl extends GenericService<StatementOfAccount>
		implements SOAReportGenerationService {
	private static Logger logger = LogManager.getLogger(SOAReportGenerationServiceImpl.class);

	private static final String inclusive = ImplementationConstants.GST_INCLUSIVE_SYMBOL;
	private static final String exclusive = ImplementationConstants.GST_EXCLUSIVE_SYMBOL;
	private static final String URD = "URD";

	private SOAReportGenerationDAO soaReportGenerationDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private RuleDAO ruleDAO;
	private FinFeeDetailService finFeeDetailService;
	private FeeTypeDAO feeTypeDAO;
	private FinODAmzTaxDetailDAO finODAmzTaxDetailDAO;
	private PromotionDAO promotionDAO;
	@Autowired(required = false)
	private SOAReportService soaReportService;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private FeeWaiverDetailDAO feeWaiverDetailDAO;
	@Autowired(required = false)
	@Qualifier("sOAExtensionService")
	private SOAExtensionService sOAExtensionService;
	private LinkedFinancesService linkedFinancesService;
	private FinODCAmountDAO finODCAmountDAO;

	private Date fixedEndDate = null;

	public SOAReportGenerationServiceImpl() {
		super();
	}

	private FinanceMain getFinanceMain(String finReference) {
		return this.soaReportGenerationDAO.getFinanceMain(finReference);
	}

	private List<FinanceScheduleDetail> getFinScheduleDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinScheduleDetails(finReference);
	}

	private List<FinAdvancePayments> getFinAdvancePayments(String finReference) {
		return this.soaReportGenerationDAO.getFinAdvancePayments(finReference);
	}

	private List<PaymentInstruction> getPaymentInstructions(String finReference) {
		return this.soaReportGenerationDAO.getPaymentInstructions(finReference);
	}

	private List<FinODDetails> getFinODDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinODDetails(finReference);
	}

	private List<ManualAdvise> getManualAdvise(String finReference, Date valueDate) {
		return this.soaReportGenerationDAO.getManualAdvise(finReference, valueDate);
	}

	private List<ReceiptAllocationDetail> getReceiptAllocationDetailsList(String finReference) {
		return this.soaReportGenerationDAO.getReceiptAllocationDetailsList(finReference);
	}

	private List<FinReceiptHeader> getFinReceiptHeaders(String finReference) {
		return this.soaReportGenerationDAO.getFinReceiptHeaders(finReference);
	}

	private List<FinReceiptDetail> getFinReceiptDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinReceiptDetails(finReference);
	}

	private StatementOfAccount getSOALoanDetails(String finReference) {
		return this.soaReportGenerationDAO.getSOALoanDetails(finReference);
	}

	private FinanceProfitDetail getFinanceProfitDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinanceProfitDetails(finReference);
	}

	private int getFinanceProfitDetailActiveCount(long finProfitDetailActiveCount, boolean active) {
		return this.soaReportGenerationDAO.getFinanceProfitDetailActiveCount(finProfitDetailActiveCount, active);
	}

	private Map<Long, List<ReceiptAllocationDetail>> getReceiptAllocationDetailMap(String finReference) {
		return this.soaReportGenerationDAO.getReceiptAllocationDetailsMap(finReference);
	}

	private StatementOfAccount getSOACustomerDetails(long custId) {
		return this.soaReportGenerationDAO.getSOACustomerDetails(custId);
	}

	private StatementOfAccount getSOAProductDetails(String finBranch, String finType) {
		return this.soaReportGenerationDAO.getSOAProductDetails(finBranch, finType);
	}

	private List<FinExcessAmount> getFinExcessAmountsList(String finReference) {
		return this.soaReportGenerationDAO.getFinExcessAmountsList(finReference);
	}

	private List<FinRepayHeader> getFinRepayHeadersList(String finReference) {
		return this.soaReportGenerationDAO.getFinRepayHeadersList(finReference);
	}

	private List<FinFeeDetail> getFinFeedetails(String finReference) {
		return this.soaReportGenerationDAO.getFinFeedetails(finReference);
	}

	private List<FeeWaiverDetail> getFeeWaiverDetail(String finReference) {
		return this.soaReportGenerationDAO.getFeeWaiverDetail(finReference);
	}

	private Date getMaxSchDate(String finReference) {
		return this.soaReportGenerationDAO.getMaxSchDate(finReference);
	}

	private List<PresentmentDetail> getPresentmentDetailsList(String finReference) {
		return this.soaReportGenerationDAO.getPresentmentDetailsList(finReference);
	}

	private List<RepayScheduleDetail> getRepayScheduleDetailsList(String finReference) {
		return this.soaReportGenerationDAO.getRepayScheduleDetailsList(finReference);
	}

	private List<VASRecording> getVASRecordingsList(String finReference) {
		return this.soaReportGenerationDAO.getVASRecordingsList(finReference);
	}

	private List<FinFeeScheduleDetail> getFinFeeScheduleDetailsList(String finReference) {
		return this.soaReportGenerationDAO.getFinFeeScheduleDetailsList(finReference);
	}

	private List<ApplicantDetail> getApplicantDetails(String finReference) {
		return this.soaReportGenerationDAO.getApplicantDetails(finReference);
	}

	private List<OtherFinanceDetail> getCustOtherFinDetails(long custID, String finReference) {
		return this.soaReportGenerationDAO.getCustOtherFinDetails(custID, finReference);
	}

	public void setSoaReportGenerationDAO(SOAReportGenerationDAO soaReportGenerationDAO) {
		this.soaReportGenerationDAO = soaReportGenerationDAO;
	}

	private List<RestructureCharge> getRestructureChargeList(String finReference) {
		return this.soaReportGenerationDAO.getRestructureChargeList(finReference);
	}

	private List<FinFeeRefundHeader> getFinFeeRefundHeader(String finReference) {
		return this.soaReportGenerationDAO.getFinFeeRefundHeader(finReference);
	}

	private List<FinFeeRefundDetails> getFinFeeRefundDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinFeeRefundDetails(finReference);
	}

	@SuppressWarnings("deprecation")
	@Override
	public StatementOfAccount getStatmentofAccountDetails(String finReference, Date startDate, Date endDate,
			boolean isAPI) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		long custId = 0;
		List<ApplicantDetail> applicantDetails = null;
		List<OtherFinanceDetail> otherFinanceDetails = null;
		// get the Loan Basic Details
		StatementOfAccount statementOfAccount = getSOALoanDetails(finReference);
		int ccyEditField = 0;

		if (isAPI) {
			ccyEditField = 0;
		} else {
			ccyEditField = statementOfAccount.getCcyEditField();
		}

		// get the FinProfitDeatails
		FinanceProfitDetail financeProfitDetail = getFinanceProfitDetails(finReference);
		List<FinExcessAmount> finExcessAmountsList = getFinExcessAmountsList(finReference);
		// get the finance basic details
		FinanceMain finMain = getFinanceMain(finReference);

		// Fetch Schedule details
		List<FinanceScheduleDetail> finSchdDetList = getFinScheduleDetails(finReference);

		List<LinkedFinances> list = linkedFinancesService.getFinIsLinkedActive(finReference);
		String activeLoans = "";
		String inActiveLoans = "";
		List<String> actvlans = new ArrayList<>();
		List<String> inActvlans = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (LinkedFinances linkedFin : list) {
				if (linkedFin.getFinIsActive()) {
					if (StringUtils.isNotEmpty(activeLoans)) {
						activeLoans = activeLoans + ",";
					}

					if (finReference.equals(linkedFin.getFinReference())) {
						if (!actvlans.contains(linkedFin.getLinkedReference())) {
							activeLoans = activeLoans + linkedFin.getLinkedReference();
							actvlans.add(linkedFin.getLinkedReference());
						}

					} else if (finReference.equals(linkedFin.getLinkedReference())) {
						if (!actvlans.contains(linkedFin.getFinReference())) {
							activeLoans = activeLoans + linkedFin.getFinReference();
							actvlans.add(linkedFin.getFinReference());
						}
					}
				} else {
					if (StringUtils.isNotEmpty(inActiveLoans)) {
						inActiveLoans = inActiveLoans + ",";
					}

					if (finReference.equals(linkedFin.getFinReference())) {
						if (!inActvlans.contains(linkedFin.getFinReference())) {
							inActiveLoans = inActiveLoans + linkedFin.getLinkedReference();
							inActvlans.add(linkedFin.getLinkedReference());
						}

					} else if (finReference.equals(linkedFin.getLinkedReference())) {
						if (!inActvlans.contains(linkedFin.getFinReference())) {
							inActiveLoans = inActiveLoans + linkedFin.getFinReference();
							inActvlans.add(linkedFin.getFinReference());
						}
					}
				}
			}

			// Linked Loan Reference
			statementOfAccount.setLinkedFinRef(activeLoans);
			statementOfAccount.setClosedlinkedFinRef(inActiveLoans);
		}

		BigDecimal loanAmount = statementOfAccount.getLoanAmount();

		if (financeProfitDetail != null) {

			custId = financeProfitDetail.getCustId();
			int activeCount = getFinanceProfitDetailActiveCount(custId, true);
			int closeCount = getFinanceProfitDetailActiveCount(custId, false);

			statementOfAccount.setCustID(custId);
			statementOfAccount.setActiveCnt(activeCount);
			statementOfAccount.setCloseCnt(closeCount);
			statementOfAccount.setTot(activeCount + closeCount);
			statementOfAccount.setFinStartDate(financeProfitDetail.getFinStartDate());
			// statementOfAccount.setLinkedFinRef(financeProfitDetail.getLinkedFinRef());
			// statementOfAccount.setClosedlinkedFinRef(financeProfitDetail.getClosedlinkedFinRef());

			// BFSD Related
			statementOfAccount.setFinPurpose(financeProfitDetail.getFinPurpose());
			statementOfAccount.setCurrentDate(SysParamUtil.getAppDate());
			if (FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(finMain.getClosingStatus())) {
				statementOfAccount.setMaturityDate(finMain.getClosedDate());
				statementOfAccount.setNextRpyDate(finMain.getClosedDate());
			} else {
				statementOfAccount.setMaturityDate(financeProfitDetail.getMaturityDate());
				statementOfAccount.setNextRpyDate(financeProfitDetail.getNSchdDate());
			}
			statementOfAccount.setNoPaidInst(financeProfitDetail.getNOPaidInst());
			statementOfAccount.setTotalPriPaid(
					PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPriPaid(), ccyEditField));
			try {
				statementOfAccount
						.setTotalPriPaidInWords(
								financeProfitDetail.getTotalPriPaid() == BigDecimal.ZERO ? ""
										: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
												PennantApplicationUtil.formateAmount(
														financeProfitDetail.getTotalPriPaid(), ccyEditField),
												finMain.getFinCcy())));
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
			statementOfAccount.setTotalPftPaid(
					PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPftPaid(), ccyEditField));
			BigDecimal paidTotal = financeProfitDetail.getTotalPriPaid().add(financeProfitDetail.getTotalPftPaid());
			try {
				statementOfAccount
						.setTotalPftPaidInWords(
								financeProfitDetail.getTotalPftPaid() == BigDecimal.ZERO ? ""
										: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
												PennantApplicationUtil.formateAmount(
														financeProfitDetail.getTotalPftPaid(), ccyEditField),
												finMain.getFinCcy())));
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
			statementOfAccount.setPaidTotal(PennantApplicationUtil.formateAmount(paidTotal, ccyEditField));

			// Get closing balance amount upto given end date and set to principal balance.
			int emiHoliday = 0;
			int odTerm = 0;
			int cpzTerms = 0;
			BigDecimal priOutStanding = BigDecimal.ZERO;
			BigDecimal odAmount = BigDecimal.ZERO;
			if (CollectionUtils.isNotEmpty(finSchdDetList)) {

				for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {

					if ((DateUtil.compare(finSchdDetail.getSchDate(), endDate) > 0)) {
						break;
					}

					if ("H".equals(finSchdDetail.getBpiOrHoliday())) {
						emiHoliday = emiHoliday + 1;
					}

					// PSD#169696 added condition to check actual installment term or not
					if (finSchdDetail.isCpzOnSchDate() && !finSchdDetail.isPftOnSchDate()
							&& finSchdDetail.getInstNumber() > 0) {
						cpzTerms = cpzTerms + 1;
						continue;
					}

					priOutStanding = priOutStanding
							.add((finSchdDetail.getDisbAmount().add(finSchdDetail.getCpzAmount()))
									.subtract(finSchdDetail.getSchdPriPaid()));
					if (((finSchdDetail.getSchdPriPaid().add(finSchdDetail.getSchdPftPaid()))
							.compareTo((finSchdDetail.getPrincipalSchd().add(finSchdDetail.getProfitSchd()))) != 0)) {
						odTerm = odTerm + 1;
						odAmount = odAmount.add(finSchdDetail.getRepayAmount())
								.subtract(finSchdDetail.getSchdPriPaid().add(finSchdDetail.getSchdPftPaid()));
					}
				}
			}

			// Current Applicable EMI
			BigDecimal applicableEMI = financeProfitDetail.getNSchdPri().add(financeProfitDetail.getNSchdPft());
			statementOfAccount.setCurApplicableEMI(PennantApplicationUtil.formateAmount(applicableEMI, ccyEditField));
			try {
				statementOfAccount.setCurApplicableEMIInWords(applicableEMI == BigDecimal.ZERO ? ""
						: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
								PennantApplicationUtil.formateAmount(applicableEMI, ccyEditField),
								finMain.getFinCcy())));
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

			if (ImplementationConstants.CUSTOMIZED_SOAREPORT && !finMain.isFinIsActive()
					|| FinanceConstants.CLOSE_STATUS_CANCELLED.equals(finMain.getClosingStatus())) {
				statementOfAccount.setTotalPriBal(BigDecimal.ZERO);
				statementOfAccount.setCurApplicableEMI(BigDecimal.ZERO);
			} else if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
				statementOfAccount.setTotalPriBal(PennantApplicationUtil.formateAmount(priOutStanding, ccyEditField));
				try {
					statementOfAccount.setTotalPriBalInwords(priOutStanding == BigDecimal.ZERO ? ""
							: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
									PennantApplicationUtil.formateAmount(priOutStanding, ccyEditField),
									finMain.getFinCcy())));
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
				// Next schedule details
				statementOfAccount.setNoOfEmiOverDue(odTerm);
				// EmiAmtOverdue
				statementOfAccount.setEmiAmtOverdue(PennantApplicationUtil.formateAmount(odAmount, ccyEditField));
			} else {
				statementOfAccount.setTotalPriBal(
						PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPriBal(), ccyEditField));

				try {
					statementOfAccount
							.setTotalPriBalInwords(
									financeProfitDetail.getTotalPriBal() == BigDecimal.ZERO ? ""
											: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
													PennantApplicationUtil.formateAmount(
															financeProfitDetail.getTotalPriBal(), ccyEditField),
													finMain.getFinCcy())));
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}

			statementOfAccount.setTotalPriBal(
					PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPriBal(), ccyEditField));
			statementOfAccount.setTotalPftBal(
					PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPftBal(), ccyEditField));
			BigDecimal totalOutStanding = financeProfitDetail.getTotalPriBal()
					.add(financeProfitDetail.getTotalPftBal());
			statementOfAccount
					.setTotalOutStanding(PennantApplicationUtil.formateAmount(totalOutStanding, ccyEditField));

			int futureInst = finMain.getCalTerms() - financeProfitDetail.getNOPaidInst();
			if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
				futureInst = futureInst + finMain.getGraceTerms() - cpzTerms - emiHoliday - odTerm;
				statementOfAccount.setFutureInstNo(futureInst);
			}

			statementOfAccount.setFinCurrAssetValue(
					PennantApplicationUtil.formateAmount(finMain.getFinCurrAssetValue(), ccyEditField));

			if (ImplementationConstants.CUSTOMIZED_SOAREPORT
					&& ((DateUtil.compare(finMain.getMaturityDate(), SysParamUtil.getAppDate()) < 0)
							|| (!finMain.isFinIsActive() && StringUtils.equals(finMain.getClosingStatus(),
									FinanceConstants.CLOSE_STATUS_CANCELLED)))) {
				// Nothing To Do.
			} else {
				statementOfAccount.setNoOfOutStandInst(futureInst);
				// Next Installment Amount
				if (finMain.isStepFinance()
						&& StringUtils.equals(finMain.getCalcOfSteps(), PennantConstants.STEPPING_CALC_AMT)) {
					Date appDate = SysParamUtil.getAppDate();
					for (FinanceScheduleDetail fsd : finSchdDetList) {
						if (fsd.getSchDate().compareTo(appDate) > 0
								&& fsd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
							statementOfAccount.setNextRpyDate(fsd.getSchDate());
							break;
						}
					}
				}
				statementOfAccount.setNextRpyPri(
						PennantApplicationUtil.formateAmount(financeProfitDetail.getNSchdPri(), ccyEditField));
				statementOfAccount.setNextRpyPft(
						PennantApplicationUtil.formateAmount(financeProfitDetail.getNSchdPft(), ccyEditField));
			}

			// Rate Code will be displayed when Referential Rate is selected against the loan
			String plrRate = statementOfAccount.getPlrRate();
			if (StringUtils.isEmpty(plrRate)) {
				statementOfAccount.setPlrRate("-");
			} else {
				statementOfAccount.setPlrRate(plrRate + "/" + finMain.getRepayMargin());
			}

			if (AdvanceType.hasAdvEMI(finMain.getAdvType())) {
				statementOfAccount.setAdvEmiApplicable(true);
			}
			// Added RepaymentFrequence
			if (statementOfAccount.getRepayFrq() != null && statementOfAccount.getRepayFrq() != "") {
				statementOfAccount.setRepayFrq(FrequencyUtil.getRepayFrequencyLabel(statementOfAccount.getRepayFrq()));
			}

			// Based on Repay Frequency codes it will set
			String frequency = FrequencyUtil.getFrequencyCode(finMain.getRepayFrq());
			statementOfAccount.setTenureLabel(FrequencyUtil.getRepayFrequencyLabel(frequency));

			if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
				statementOfAccount.setTenure(finMain.getAdvTerms() + finMain.getGraceTerms());
			} else {
				if (ImplementationConstants.ALW_DOWNPAY_IN_LOANENQ_AND_SOA) {
					statementOfAccount.setDownPayment(new BigDecimal(statementOfAccount.getAdvInstAmt()));
				}
			}

			if (sOAExtensionService == null) {
				// FinExcess Amount
				statementOfAccount.setAdvInstAmt("0.00 / 0");
				statementOfAccount.setAdvEMIAmt("0.00 / 0.00");
				statementOfAccount.setAdvIntAmt("0.00 / 0.00");
				statementOfAccount.setCashCollAmt("0.00 / 0.00");
				statementOfAccount.setDsraAmt("0.00 / 0.00");

				// Advance EMI Installments
				statementOfAccount
						.setAdvInstAmt(PennantApplicationUtil.amountFormate(finMain.getAdvanceEMI(), ccyEditField)
								+ " / " + finMain.getAdvTerms());

				if (CollectionUtils.isNotEmpty(finExcessAmountsList)) {
					for (FinExcessAmount finExcessAmount : finExcessAmountsList) {
						String balanceAmt = PennantApplicationUtil.amountFormate(finExcessAmount.getBalanceAmt(),
								ccyEditField);
						String actualAmt = PennantApplicationUtil.amountFormate(finExcessAmount.getAmount(),
								ccyEditField);
						if (RepayConstants.EXAMOUNTTYPE_EMIINADV.equals(finExcessAmount.getAmountType())) {
							statementOfAccount.setAdvEMIAmt(actualAmt + " / " + balanceAmt);
							int advEMITerms = finMain.getAdvTerms();
							statementOfAccount.setAdvInstAmt(actualAmt + " / " + advEMITerms);
						} else if (AdvanceRuleCode.ADVINT.name().equals(finExcessAmount.getAmountType())) {
							int grcAdvTerms = finMain.getGrcAdvTerms();
							int advTerms = 0;
							if (AdvanceType.UT.name().equals(finMain.getAdvType())) {
								advTerms = finMain.getAdvTerms();
							}
							if (AdvanceType.UF.name().equals(finMain.getGrcAdvType())) {
								grcAdvTerms = finMain.getGraceTerms();
							}
							if (AdvanceType.UF.name().equals(finMain.getAdvType())) {
								advTerms = finMain.getNumberOfTerms();
							}
							int advIntTerms = grcAdvTerms + advTerms;
							statementOfAccount.setAdvIntAmt(actualAmt + " / " + advIntTerms);
						} else if (AdvanceRuleCode.CASHCLT.name().equals(finExcessAmount.getAmountType())) {
							statementOfAccount.setCashCollAmt(actualAmt + " / " + balanceAmt);
						} else if (AdvanceRuleCode.DSF.name().equals(finExcessAmount.getAmountType())) {
							statementOfAccount.setDsraAmt(actualAmt + " / " + balanceAmt);
						}
					}
				}
			} else {

				// FinExcess Amount
				statementOfAccount.setAdvInstAmt("0.00 / 0");
				statementOfAccount.setAdvEMIAmt("0.00");
				statementOfAccount.setAdvIntAmt("0.00");
				statementOfAccount.setCashCollAmt("0.00");
				statementOfAccount.setDsraAmt("0.00");

				// Advance EMI Installments
				statementOfAccount
						.setAdvInstAmt(PennantApplicationUtil.amountFormate(finMain.getAdvanceEMI(), ccyEditField)
								+ " / " + finMain.getAdvTerms());

				if (CollectionUtils.isNotEmpty(finExcessAmountsList)) {
					for (FinExcessAmount finExcessAmount : finExcessAmountsList) {
						String balanceAmt = PennantApplicationUtil.amountFormate(finExcessAmount.getBalanceAmt(),
								ccyEditField);
						if (RepayConstants.EXAMOUNTTYPE_EMIINADV.equals(finExcessAmount.getAmountType())) {
							statementOfAccount.setAdvEMIAmt(balanceAmt);
						} else if (AdvanceRuleCode.CASHCLT.name().equals(finExcessAmount.getAmountType())) {
							statementOfAccount.setCashCollAmt(balanceAmt);
						} else if (AdvanceRuleCode.DSF.name().equals(finExcessAmount.getAmountType())) {
							statementOfAccount.setDsraAmt(balanceAmt);
						} else if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(finExcessAmount.getAmountType())) {
							statementOfAccount.setBalanceAmt(PennantApplicationUtil
									.formateAmount(finExcessAmount.getBalanceAmt(), ccyEditField));
						}
					}
				}

			}

			if (soaReportService != null) {
				statementOfAccount.setSheduleReports(
						soaReportService.getSOAScheduleReport(getFinScheduleDetails(finReference), ccyEditField));
				statementOfAccount.setExtendedDetails(soaReportService.extendedFieldDetailsService(finReference));

				for (Map<String, Object> extMap : soaReportService.extendedFieldDetailsService(finReference)) {
					if (extMap.containsKey("PRODUCTID") && extMap.get("PRODUCTID") != null) {
						statementOfAccount.setProductId(String.valueOf(extMap.get("PRODUCTID")));
					}
					if (extMap.containsKey("PRODUCTSKU") && extMap.get("PRODUCTSKU") != null) {
						statementOfAccount.setProductSku(String.valueOf(extMap.get("PRODUCTSKU")));
					}
				}
				statementOfAccount.setInstChrgForCust(
						PennantApplicationUtil.formateAmount(finMain.getRepayProfitRate().compareTo(BigDecimal.ZERO) > 0
								? financeProfitDetail.getTotalPftSchd()
								: statementOfAccount.getSvamount(), ccyEditField));
				statementOfAccount.setSvamount(
						PennantApplicationUtil.formateAmount(statementOfAccount.getSvamount(), ccyEditField));
				statementOfAccount.setAdvInstAmt(
						String.valueOf(PennantApplicationUtil.amountFormate(finMain.getDownPayment(), ccyEditField)));
				if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
					Promotion promotions = promotionDAO.getPromotionByReferenceId(finMain.getPromotionSeqId(),
							"_AView");
					statementOfAccount.setTenure(promotions.getTenor());
					statementOfAccount.setAdvEmiTerms(promotions.getAdvEMITerms());
					if ((promotions.isDbd() || promotions.isMbd())) {
						statementOfAccount.setSvamount(
								PennantApplicationUtil.formateAmount(statementOfAccount.getSvamount(), ccyEditField));
					} else {
						statementOfAccount.setSvamount(BigDecimal.ZERO);
					}
				}

			}

			// get the Customer Details
			StatementOfAccount statementOfAccountCustDetails = getSOACustomerDetails(custId);

			// Co-Applicant/Borrower Details
			applicantDetails = getApplicantDetails(finReference);

			// Other Finance Details
			otherFinanceDetails = getCustOtherFinDetails(custId, finReference);

			if (statementOfAccountCustDetails != null) {
				statementOfAccount.setCustShrtName(
						StringUtils.capitaliseAllWords(statementOfAccountCustDetails.getCustShrtName()));
				statementOfAccount
						.setCustSalutation(StringUtils.trimToEmpty(statementOfAccountCustDetails.getCustSalutation()));
				statementOfAccount.setCustCIF(statementOfAccountCustDetails.getCustCIF());
				statementOfAccount.setCustCtgCode(statementOfAccountCustDetails.getCustCtgCode());
				statementOfAccount.setCustAddrHNbr(statementOfAccountCustDetails.getCustAddrHNbr());
				statementOfAccount.setCustFlatNbr(statementOfAccountCustDetails.getCustFlatNbr());
				statementOfAccount.setCustAddrStreet(statementOfAccountCustDetails.getCustAddrStreet());
				statementOfAccount.setCustPOBox(statementOfAccountCustDetails.getCustPOBox());
				statementOfAccount.setCustAddrCity(statementOfAccountCustDetails.getCustAddrCity());
				statementOfAccount.setCustAddrProvince(statementOfAccountCustDetails.getCustAddrProvince());
				statementOfAccount.setCustAddrCountry(statementOfAccountCustDetails.getCustAddrCountry());
				statementOfAccount.setPhoneCountryCode(statementOfAccountCustDetails.getPhoneCountryCode());
				statementOfAccount.setPhoneAreaCode(statementOfAccountCustDetails.getPhoneAreaCode());
				statementOfAccount.setPhoneNumber(statementOfAccountCustDetails.getPhoneNumber());
				statementOfAccount.setCustEMail(statementOfAccountCustDetails.getCustEMail());
				statementOfAccount.setCustAddrLine1(statementOfAccountCustDetails.getCustAddrLine1());
				statementOfAccount.setCustAddrLine2(statementOfAccountCustDetails.getCustAddrLine2());
				statementOfAccount.setCustAddrZIP(statementOfAccountCustDetails.getCustAddrZIP());
			}

			// to get the FinType and FinBranch
			StatementOfAccount statementOfAccountProductDetails = getSOAProductDetails(
					financeProfitDetail.getFinBranch(), financeProfitDetail.getFinType());

			if (statementOfAccountProductDetails != null) {
				statementOfAccount.setFinType(statementOfAccountProductDetails.getFinType());
				statementOfAccount.setFinBranch(statementOfAccountProductDetails.getFinBranch());
			}

			if (!ImplementationConstants.CUSTOMIZED_SOAREPORT) {
				// Next schedule details
				statementOfAccount.setNoOfEmiOverDue(financeProfitDetail.getNOODInst());
				// EmiAmtOverdue
				BigDecimal value = financeProfitDetail.getTdSchdPri().subtract(financeProfitDetail.getTotalPriPaid());
				statementOfAccount.setEmiAmtOverdue(PennantApplicationUtil.formateAmount(value, ccyEditField));
			}

			// to get fin entity code and description
			StatementOfAccount finEntity = getFinEntity(financeProfitDetail.getFinType());
			if (finEntity != null) {
				statementOfAccount.setEntityCode(finEntity.getEntityCode());
				statementOfAccount.setEntityDesc(finEntity.getEntityDesc());
				statementOfAccount.setStateCode(finEntity.getStateCode());
			}

			// to get Fin GSTIN value using entity code and fin branch
			String finGSTIN = getFinGSTINDetails(finEntity.getStateCode(), statementOfAccount.getEntityCode());
			statementOfAccount.setProviderGSTIN(finGSTIN);

			// to get Customer GSTIN number and province
			StatementOfAccount custGSTINDetails = getCustGSTINDetails(finReference);
			if (custGSTINDetails != null && StringUtils.isNotEmpty(custGSTINDetails.getCustGSTIN())) {
				statementOfAccount.setCustGSTIN(custGSTINDetails.getCustGSTIN());
				statementOfAccount.setPlaceOfSupply(custGSTINDetails.getPlaceOfSupply());
			} else {
				statementOfAccount.setCustGSTIN(URD);
			}

		}

		BigDecimal ccyMinorCcyUnits = statementOfAccount.getCcyMinorCcyUnits();
		if (startDate == null) {
			startDate = financeProfitDetail.getFinStartDate();
		}
		if (endDate == null) {
			endDate = SysParamUtil.getAppDate();
		} else { // endDate should be grater than app date then set to the Application date
			if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
				// nothing to do
			} else if (DateUtil.compare(endDate, SysParamUtil.getAppDate()) > 0) {
				endDate = SysParamUtil.getAppDate();
			}
		}
		statementOfAccount.setStartDate(startDate);
		statementOfAccount.setEndDate(endDate);

		// Including advance and moratorium EMI terms
		int tenure = statementOfAccount.getTenure();
		statementOfAccount.setTenure(tenure);
		if (sOAExtensionService != null) {
			tenure = tenure + sOAExtensionService.getMortoriumTerms(finReference);
			tenure = tenure + finMain.getAdvTerms();
			sOAExtensionService.setRequiredFields(statementOfAccount, startDate, endDate);

			for (Map<String, Object> extMap : statementOfAccount.getCustomerDetails()) {
				if (extMap.containsKey("UCIC") && extMap.get("UCIC") != null) {
					statementOfAccount.setUcic(String.valueOf(extMap.get("UCIC")));
				}
			}

			statementOfAccount.setOdPrincipal(
					PennantApplicationUtil.formateAmount(statementOfAccount.getOdPrincipal(), ccyEditField));
			statementOfAccount
					.setOdProfit(PennantApplicationUtil.formateAmount(statementOfAccount.getOdProfit(), ccyEditField));
			statementOfAccount.setTotalPriBal(
					PennantApplicationUtil.formateAmount(statementOfAccount.getTotalPriBal(), ccyEditField));

			statementOfAccount.setSOAFeeDetails(getSOAFeeDetails(statementOfAccount));

		}

		// Formatting the amounts
		// Formatting the amounts
		if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
			statementOfAccount
					.setLoanAmount(PennantApplicationUtil.formateAmount(finMain.getFinAssetValue(), ccyEditField));
			try {
				statementOfAccount.setLoanAmountInWords(loanAmount == BigDecimal.ZERO ? ""
						: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
								PennantApplicationUtil.formateAmount(finMain.getFinAssetValue(), ccyEditField),
								finMain.getFinCcy())));
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		} else {
			if (ImplementationConstants.ALW_DOWNPAY_IN_LOANENQ_AND_SOA) {
				BigDecimal amount = statementOfAccount.getLoanAmount().add(statementOfAccount.getDownPayment());
				statementOfAccount.setLoanAmount(PennantApplicationUtil.formateAmount(amount, ccyEditField));
			} else {
				statementOfAccount.setLoanAmount(
						PennantApplicationUtil.formateAmount(statementOfAccount.getLoanAmount(), ccyEditField));
			}
			try {
				statementOfAccount
						.setLoanAmountInWords(loanAmount == BigDecimal.ZERO ? ""
								: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
										PennantApplicationUtil.formateAmount(loanAmount, ccyEditField),
										finMain.getFinCcy())));
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		statementOfAccount.setFinAssetValue(
				PennantApplicationUtil.formateAmount(statementOfAccount.getFinAssetValue(), ccyEditField));
		statementOfAccount.setPreferredCardLimit(
				PennantApplicationUtil.formateAmount(statementOfAccount.getPreferredCardLimit(), ccyEditField));
		statementOfAccount.setChargeCollCust(
				PennantApplicationUtil.formateAmount(statementOfAccount.getChargeCollCust(), ccyEditField));
		statementOfAccount.setUpfrontIntCust(
				PennantApplicationUtil.formateAmount(statementOfAccount.getUpfrontIntCust(), ccyEditField));
		statementOfAccount.setLinkedFinRef(statementOfAccount.getLinkedFinRef());
		statementOfAccount.setClosedlinkedFinRef(statementOfAccount.getClosedlinkedFinRef());

		if (AdvanceType.hasAdvEMI(finMain.getAdvType())) {
			statementOfAccount.setEmiReceivedPri(PennantApplicationUtil
					.formateAmount(statementOfAccount.getEmiReceivedPri().add(finMain.getAdvanceEMI()), ccyEditField));
		} else {
			statementOfAccount.setEmiReceivedPri(
					PennantApplicationUtil.formateAmount(statementOfAccount.getEmiReceivedPri(), ccyEditField));
		}

		statementOfAccount.setEmiReceivedPft(
				PennantApplicationUtil.formateAmount(statementOfAccount.getEmiReceivedPft(), ccyEditField));
		// setting emiReceived for restructured loans
		if (ImplementationConstants.RESTRUCTURE_DATE_ALW_EDIT && finMain.isRestructure()) {
			BigDecimal rstPri = BigDecimal.ZERO;
			BigDecimal rstPft = BigDecimal.ZERO;

			List<RestructureCharge> rstChrgs = getRestructureChargeList(statementOfAccount.getFinReference());

			for (RestructureCharge rstChrg : rstChrgs) {
				String alocType = rstChrg.getAlocType();
				if (!Allocation.PRI.equals(alocType) && !Allocation.PFT.equals(alocType)) {
					continue;
				}

				if (Allocation.PRI.equals(alocType)) {
					rstPri = PennantApplicationUtil.formateAmount(rstChrg.getTotalAmount(), ccyEditField);
				} else {
					rstPft = PennantApplicationUtil.formateAmount(rstChrg.getTotalAmount(), ccyEditField);
				}
			}

			statementOfAccount.setEmiReceivedPri(statementOfAccount.getEmiReceivedPri().subtract(rstPri));
			statementOfAccount.setEmiReceivedPft(statementOfAccount.getEmiReceivedPft().subtract(rstPft));
		}
		statementOfAccount.setPrevInstAmtPri(
				PennantApplicationUtil.formateAmount(statementOfAccount.getPrevInstAmtPri(), ccyEditField));
		statementOfAccount.setPrevInstAmtPft(
				PennantApplicationUtil.formateAmount(statementOfAccount.getPrevInstAmtPft(), ccyEditField));

		statementOfAccount
				.setFuturePri1(PennantApplicationUtil.formateAmount(statementOfAccount.getFuturePri1(), ccyEditField));
		statementOfAccount
				.setFuturePri2(PennantApplicationUtil.formateAmount(statementOfAccount.getFuturePri2(), ccyEditField));

		statementOfAccount.setFutureRpyPft1(
				PennantApplicationUtil.formateAmount(statementOfAccount.getFutureRpyPft1(), ccyEditField));
		statementOfAccount.setFutureRpyPft2(
				PennantApplicationUtil.formateAmount(statementOfAccount.getFutureRpyPft2(), ccyEditField));
		if (statementOfAccount.isFinIsActive()) {
			statementOfAccount.setLatestRpyDate(statementOfAccount.getEndInstallmentDate());
		} else if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(statementOfAccount.getClosingStatus())) {
			statementOfAccount.setLatestRpyDate(statementOfAccount.getClosedDate());
		}

		// get the Summary Details
		List<FinODDetails> finODDetailsList = getFinODDetails(finReference);
		if (CollectionUtils.isNotEmpty(finODDetailsList)) {
			statementOfAccount.setNoOfODTerms(finODDetailsList.size());
			BigDecimal totalODAmt = BigDecimal.ZERO;
			for (FinODDetails od : finODDetailsList) {
				totalODAmt = totalODAmt.add(od.getFinCurODAmt());
			}
			statementOfAccount.setTotalODAmt(totalODAmt);
		}

		List<SOASummaryReport> soaSummaryDetailsList = getSOASummaryDetails(finReference, finMain, finSchdDetList,
				financeProfitDetail, finODDetailsList, endDate, statementOfAccount);

		for (SOASummaryReport summary : soaSummaryDetailsList) {
			summary.setFinReference(finReference);
			summary.setCcyEditField(ccyEditField);
			summary.setCcyMinorCcyUnits(ccyMinorCcyUnits);
			summary.setAppDate(SysParamUtil.getAppDate());
			String component = summary.getComponent();
			// Loan Receivables and Loan Summary
			if (component.equals("Bounce Charges")) {
				statementOfAccount
						.setBounceDue(PennantApplicationUtil.formateAmount(summary.getOverDue(), ccyEditField));
			} else if (component.equals("Other Payables")) {
				statementOfAccount.setOtherPayableDue(PennantApplicationUtil
						.formateAmount(summary.getDue().subtract(summary.getReceipt()), ccyEditField));
			} else if (component.equals("Other Receivables")) {
				statementOfAccount.setOtherReceivableDue(PennantApplicationUtil
						.formateAmount(summary.getDue().subtract(summary.getReceipt()), ccyEditField));
			} else if (component.equals("Late Payment Penalty")) {
				statementOfAccount.setTotPenaltyAmt(PennantApplicationUtil
						.formateAmount(summary.getDue().subtract(summary.getReceipt()), ccyEditField));
			}
			summary.setDue(PennantApplicationUtil.formateAmount(summary.getDue(), ccyEditField));
			summary.setReceipt(PennantApplicationUtil.formateAmount(summary.getReceipt(), ccyEditField));
			summary.setWaiver(PennantApplicationUtil.formateAmount(summary.getWaiver(), ccyEditField));
			summary.setOverDue(PennantApplicationUtil.formateAmount(summary.getOverDue(), ccyEditField));
		}

		// get the Transaction Details
		List<SOATransactionReport> soaTransactionReportsList = getTransactionDetails(finReference, statementOfAccount,
				finMain);

		List<SOATransactionReport> finalSOATransactionReports = new ArrayList<SOATransactionReport>();

		// Transaction Details Filtering
		for (SOATransactionReport soaTransactionReport : soaTransactionReportsList) {
			if (DateUtil.compare(soaTransactionReport.getTransactionDate(), startDate) >= 0
					&& DateUtil.compare(soaTransactionReport.getTransactionDate(), endDate) <= 0) {

				soaTransactionReport.setFinReference(finReference);
				soaTransactionReport.setCcyEditField(statementOfAccount.getCcyEditField());
				soaTransactionReport.setFromDate(startDate);
				soaTransactionReport.setToDate(endDate);
				soaTransactionReport.setCcyMinorCcyUnits(ccyMinorCcyUnits);

				soaTransactionReport.setDebitAmount(
						PennantApplicationUtil.formateAmount(soaTransactionReport.getDebitAmount(), ccyEditField));
				soaTransactionReport.setCreditAmount(
						PennantApplicationUtil.formateAmount(soaTransactionReport.getCreditAmount(), ccyEditField));
				finalSOATransactionReports.add(soaTransactionReport);
			}

		}
		// Get the Selected Loan Types are Adding ValueDate and balance for the SOA Report.
		List<String> soaFinTypes = getSOAFinTypes();
		if (soaFinTypes != null && soaFinTypes.contains(finMain.getFinType())) {
			Collections.sort(finalSOATransactionReports, new Comparator<SOATransactionReport>() {
				@Override
				public int compare(SOATransactionReport o1, SOATransactionReport o2) {

					return ComparisonChain.start().compare(o1.getValueDate(), o2.getValueDate())
							.compare(o1.getPriority(), o2.getPriority()).result();
				}
			});

		} else {

			Collections.sort(finalSOATransactionReports, new Comparator<SOATransactionReport>() {
				@Override
				public int compare(SOATransactionReport o1, SOATransactionReport o2) {

					return ComparisonChain.start().compare(o1.getTransactionDate(), o2.getTransactionDate())
							.compare(o1.getPriority(), o2.getPriority()).result();
				}
			});
		}

		BigDecimal balanceAmt = BigDecimal.ZERO;
		for (SOATransactionReport soaTransactionReport : finalSOATransactionReports) {
			if (BigDecimal.ZERO.compareTo(soaTransactionReport.getDebitAmount()) != 0) {
				balanceAmt = balanceAmt.add(soaTransactionReport.getDebitAmount());
			} else if (BigDecimal.ZERO.compareTo(soaTransactionReport.getCreditAmount()) != 0) {
				balanceAmt = balanceAmt.subtract(soaTransactionReport.getCreditAmount());
			}
			soaTransactionReport.setBalanceAmount(balanceAmt);
		}
		// send the toDate and from Date for Report
		if (finalSOATransactionReports.isEmpty()) {
			SOATransactionReport sOATransactionReport = new SOATransactionReport();
			sOATransactionReport.setFromDate(startDate);
			sOATransactionReport.setToDate(endDate);
			finalSOATransactionReports.add(sOATransactionReport);
		}
		// Displaying Interest Rate And months for LAN.
		BigDecimal calrate = BigDecimal.ZERO;
		List<InterestRateDetail> interestRateDetails = new ArrayList<>();

		if (finMain.getFixedRateTenor() > 0) {

			if (StringUtils.isNotBlank(finMain.getRepayBaseRate())) {
				calrate = RateUtil
						.rates(finMain.getRepayBaseRate(), finMain.getFinCcy(), finMain.getRepaySpecialRate(),
								finMain.getRepayMargin(), finMain.getRpyMinRate(), finMain.getRpyMaxRate())
						.getNetRefRateLoan();
			} else {
				calrate = finMain.getRepayProfitRate();
			}
			int noOfMonths = DateUtil.getMonthsBetween(finMain.getFinStartDate(), fixedEndDate);
			statementOfAccount.setIntRateType("Fixed and Floating");
			InterestRateDetail fixedDetail = new InterestRateDetail();
			fixedDetail.setFormTenure(1);
			fixedDetail.setToTenure(finMain.getFixedRateTenor() + finMain.getGraceTerms());
			fixedDetail.setInterestType("Fixed");
			fixedDetail.setInterestRate(finMain.getFixedTenorRate());
			fixedDetail.setNoOfMonths(noOfMonths);
			interestRateDetails.add(fixedDetail);

			InterestRateDetail floatDetail = new InterestRateDetail();
			floatDetail.setFormTenure(finMain.getFixedRateTenor() + 1);
			floatDetail.setToTenure(financeProfitDetail.getNOInst());
			floatDetail.setInterestType("Float");
			floatDetail.setInterestRate(calrate);
			floatDetail.setNoOfMonths(financeProfitDetail.getTotalTenor() - noOfMonths);
			interestRateDetails.add(floatDetail);
		} else {
			if (StringUtils.equals(CalculationConstants.RATE_BASIS_F, finMain.getRepayRateBasis())) {
				if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
					statementOfAccount.setIntRateType("Flat");
				} else {
					statementOfAccount.setIntRateType("Fixed");
				}
			} else if (StringUtils.equals(CalculationConstants.RATE_BASIS_R, finMain.getRepayRateBasis())) {
				statementOfAccount.setIntRateType("Floating");
				if (StringUtils.isEmpty(finMain.getRepayBaseRate())) {
					statementOfAccount.setIntRateType("Fixed");
				}
			}
			calrate = financeProfitDetail.getCurReducingRate();
			InterestRateDetail detail = new InterestRateDetail();
			detail.setFormTenure(finMain.getFixedRateTenor() + 1);
			detail.setToTenure(financeProfitDetail.getNOInst());
			detail.setInterestType("Float");
			detail.setInterestRate(calrate);
			detail.setNoOfMonths(financeProfitDetail.getTotalTenor());
			interestRateDetails.add(detail);
		}

		// Summary Reports List
		statementOfAccount.setSoaSummaryReports(soaSummaryDetailsList);

		// Transaction Reports List
		statementOfAccount.setTransactionReports(finalSOATransactionReports);

		// Other Finance Details
		statementOfAccount.setOtherFinanceDetails(otherFinanceDetails);

		// Customer Loan Reference Details
		// statementOfAccount.setOtherFinanceDetails(otherFinanceRefDetails);

		// Co-Applicant/Borrower Details
		statementOfAccount.setApplicantDetails(applicantDetails);

		// Interest Rate Details
		statementOfAccount.setInterestRateDetails(interestRateDetails);
		statementOfAccount.setFutureInterestComponent(statementOfAccount.getFutureInterestComponent());
		statementOfAccount.setFuturePrincipalComponent(statementOfAccount.getFuturePrincipalComponent());
		statementOfAccount.setFutureInstAmount(statementOfAccount.getFutureInstAmount());

		logger.debug("Leaving");
		return statementOfAccount;
	}

	private StatementOfAccount getCustGSTINDetails(String finReference) {
		return soaReportGenerationDAO.getCustGSTINDetails(finReference);
	}

	private StatementOfAccount getFinEntity(String finType) {
		return soaReportGenerationDAO.getFinEntity(finType);
	}

	private String getFinGSTINDetails(String stateCode, String entityCode) {
		return soaReportGenerationDAO.getFinGSTINDetails(stateCode, entityCode);
	}

	/**
	 * get the Report Summary Details
	 * 
	 */
	private List<SOASummaryReport> getSOASummaryDetails(String finReference, FinanceMain fm,
			List<FinanceScheduleDetail> finSchdDetList, FinanceProfitDetail financeProfitDetail,
			List<FinODDetails> finODDetailsList, Date endDate, StatementOfAccount statementOfAccount) {
		logger.debug("Enetring");

		SOASummaryReport soaSummaryReport = null;
		List<SOASummaryReport> soaSummaryReportsList = new ArrayList<SOASummaryReport>();

		// FinanceMain finMain = getFinanceMain(finReference);

		if (fm == null || FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())) {
			return soaSummaryReportsList;
		}

		List<ReceiptAllocationDetail> radList = getReceiptAllocationDetailsList(finReference);
		// List<FinanceScheduleDetail> finSchdDetList = getFinScheduleDetails(finReference);
		// FinanceProfitDetail financeProfitDetail = getFinanceProfitDetails(finReference);

		FeeType bounceFeeType = null;
		FeeType odcFeeType = null;

		BigDecimal due = BigDecimal.ZERO;
		BigDecimal receipt = BigDecimal.ZERO;
		BigDecimal overDue = BigDecimal.ZERO;
		BigDecimal netDue = BigDecimal.ZERO;

		BigDecimal totalProfitSchd = BigDecimal.ZERO;
		BigDecimal totalPrincipalSchd = BigDecimal.ZERO;
		BigDecimal totalFeeschd = BigDecimal.ZERO;

		BigDecimal totalSchdPriPaid = BigDecimal.ZERO;
		BigDecimal totalSchdPftPaid = BigDecimal.ZERO;
		BigDecimal totalSchdfeepaid = BigDecimal.ZERO;

		BigDecimal totalschdPftWaiver = BigDecimal.ZERO;

		BigDecimal totalCpzAmt = BigDecimal.ZERO;

		List<ManualAdvise> manualAdviseList = getManualAdvise(finReference, endDate);
		List<FinExcessAmount> finExcessAmountsList = getFinExcessAmountsList(finReference);

		BigDecimal bounceDue = BigDecimal.ZERO;
		BigDecimal bounceRecipt = BigDecimal.ZERO;
		BigDecimal bounceWaiver = BigDecimal.ZERO;

		BigDecimal otherReceivableDue = BigDecimal.ZERO;
		BigDecimal otherReceivableReceipt = BigDecimal.ZERO;
		BigDecimal otherReceivableWaiver = BigDecimal.ZERO;

		BigDecimal otherPayableDue = BigDecimal.ZERO;

		BigDecimal principalDue = BigDecimal.ZERO;
		BigDecimal principalPaid = BigDecimal.ZERO;
		BigDecimal principalWaived = BigDecimal.ZERO;

		BigDecimal profitDue = BigDecimal.ZERO;
		BigDecimal profitPaid = BigDecimal.ZERO;
		BigDecimal profitWaived = BigDecimal.ZERO;

		BigDecimal penaltyDue = BigDecimal.ZERO;
		BigDecimal penaltyPaid = BigDecimal.ZERO;
		BigDecimal penaltyWaived = BigDecimal.ZERO;

		BigDecimal bounceDuee = BigDecimal.ZERO;
		BigDecimal bouncePaidAmt = BigDecimal.ZERO;
		BigDecimal bounceWaivedAmt = BigDecimal.ZERO;

		// Manual Advise
		if (CollectionUtils.isNotEmpty(manualAdviseList)) {
			BigDecimal adviseBalanceAmt = BigDecimal.ZERO;
			BigDecimal rcvAdvAmount = BigDecimal.ZERO;
			BigDecimal bounceAdvAmount = BigDecimal.ZERO;
			BigDecimal rcvPaid = BigDecimal.ZERO;
			BigDecimal bouncePaid = BigDecimal.ZERO;
			BigDecimal bounceWaived = BigDecimal.ZERO;
			BigDecimal rcvWaived = BigDecimal.ZERO;

			for (ManualAdvise ma : manualAdviseList) {
				String taxComponent = ma.getTaxComponent();
				if (sOAExtensionService != null) {
					String rcvComponent = sOAExtensionService.getReceivableComponent(ma.getFeeTypeID());
					if (StringUtils.isNotEmpty(rcvComponent)) {
						switch (rcvComponent) {
						case "PRIN":
							principalDue = principalDue.add(ma.getAdviseAmount());
							principalPaid = principalPaid.add(ma.getPaidAmount());
							principalWaived = principalWaived.add(ma.getWaivedAmount());
							break;
						case "INT":
							profitDue = profitDue.add(ma.getAdviseAmount());
							profitPaid = profitPaid.add(ma.getPaidAmount());
							profitWaived = profitWaived.add(ma.getWaivedAmount());
							break;
						case "PNLTY":
							penaltyDue = penaltyDue.add(ma.getAdviseAmount());
							penaltyPaid = penaltyPaid.add(ma.getPaidAmount());
							penaltyWaived = penaltyWaived.add(ma.getWaivedAmount());
							break;
						case "BOUNCE":
							BigDecimal gstAmount = BigDecimal.ZERO;
							if (ma.getAdviseAmount() != null) {
								bounceDuee = bounceDuee.add(ma.getAdviseAmount());
								if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
									gstAmount = GSTCalculator.getTotalGST(finReference, ma.getAdviseAmount(),
											taxComponent);
									bounceDuee = bounceDuee.add(gstAmount);
								}
							}
							if (ma.getPaidAmount() != null) {
								bouncePaidAmt = bouncePaidAmt.add(ma.getPaidAmount());
								if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
									bouncePaidAmt = bouncePaidAmt.add(CalculationUtil.getTotalPaidGST(ma));
								}
							}
							if (ma.getWaivedAmount() != null) {
								bounceWaivedAmt = bounceWaivedAmt.add(ma.getWaivedAmount());
								if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
									bounceWaivedAmt = bounceWaivedAmt.add(CalculationUtil.getTotalWaivedGST(ma));
								}
							}
							break;
						default:
							break;
						}
					}
				}

				if (ma.getAdviseType() == 2 && ma.getBalanceAmt() != null) {
					BigDecimal gstAmount = BigDecimal.ZERO;
					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)
							&& ma.getBalanceAmt().compareTo(BigDecimal.ZERO) > 0) {
						gstAmount = GSTCalculator.getTotalGST(finReference, ma.getBalanceAmt(), taxComponent);
						BigDecimal totGST = GSTCalculator.getTotalGST(finReference, ma.getAdviseAmount(), taxComponent);

						BigDecimal paidGST = CalculationUtil.getTotalPaidGST(ma);
						BigDecimal waivedGST = CalculationUtil.getTotalWaivedGST(ma);
						gstAmount = gstAmount.subtract(gstAmount.add(paidGST).add(waivedGST).subtract(totGST));
					}
					adviseBalanceAmt = adviseBalanceAmt.add(ma.getBalanceAmt().add(gstAmount));
				}
				if (ma.getBounceID() != 0) {
					ma.setAdviseID(-3);
				}

				if (ma.getAdviseType() == 1 && ma.getBounceID() == 0) {
					BigDecimal paidGst = CalculationUtil.getTotalPaidGST(ma);
					BigDecimal waivedGst = CalculationUtil.getTotalWaivedGST(ma);
					BigDecimal balAmt = ma.getAdviseAmount().subtract(ma.getPaidAmount().add(ma.getWaivedAmount()));

					if (balAmt.compareTo(BigDecimal.ZERO) <= 0
							&& FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
						rcvAdvAmount = rcvAdvAmount.add(ma.getPaidAmount()).add(ma.getWaivedAmount()).add(paidGst)
								.add(waivedGst);
					} else if (ma.isDueCreation()) {
						AdviseDueTaxDetail dueTaxDetail = soaReportGenerationDAO
								.getAdviseDueTaxDetails(ma.getAdviseID());
						if (dueTaxDetail != null
								&& FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(dueTaxDetail.getTaxType())) {
							rcvAdvAmount = rcvAdvAmount.add(dueTaxDetail.getTotalGST());
						} else {
							/* Handled only for migration cases. */
							BigDecimal gstAmount = GSTCalculator.getTotalGST(finReference, ma.getAdviseAmount(),
									taxComponent);
							rcvAdvAmount = rcvAdvAmount.add(ma.getAdviseAmount()).add(gstAmount);
						}
					} else if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
						BigDecimal gstAmount = GSTCalculator.getTotalGST(finReference, ma.getAdviseAmount(),
								taxComponent);
						rcvAdvAmount = rcvAdvAmount.add(ma.getAdviseAmount()).add(gstAmount);
					} else {
						rcvAdvAmount = rcvAdvAmount.add(ma.getAdviseAmount());
					}

					if (ma.getPaidAmount() != null) {
						/* GST Calculation only for Exclusive case */
						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
							rcvPaid = rcvPaid.add(ma.getPaidAmount()).add(CalculationUtil.getTotalPaidGST(ma));
						} else {
							rcvPaid = rcvPaid.add(ma.getPaidAmount());
						}
					}
					// waiver
					if (ma.getWaivedAmount() != null && ma.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
						BigDecimal currWaiverGst = feeWaiverDetailDAO.getFeeWaiverDetailList(finReference,
								ma.getAdviseID());
						if (currWaiverGst == null) {
							currWaiverGst = BigDecimal.ZERO;
						}
						/* GST Calculation only for Exclusive case */
						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
							rcvWaived = rcvWaived.add(ma.getWaivedAmount()).add(CalculationUtil.getTotalWaivedGST(ma));
						} else {
							rcvWaived = rcvWaived.add(ma.getWaivedAmount());
						}
					}
				}

				if (ma.getBounceID() > 0) {
					if (bounceFeeType == null) {
						bounceFeeType = feeTypeDAO.getTaxDetailByCode(Allocation.BOUNCE);
					}

					if (ma.getAdviseAmount() != null) {
						BigDecimal paidGst = CalculationUtil.getTotalPaidGST(ma);
						BigDecimal waivedGst = CalculationUtil.getTotalWaivedGST(ma);
						BigDecimal balAmt = ma.getAdviseAmount().subtract(ma.getPaidAmount().add(ma.getWaivedAmount()));

						if (balAmt.compareTo(BigDecimal.ZERO) <= 0) {
							bounceAdvAmount = bounceAdvAmount.add(ma.getPaidAmount()).add(ma.getWaivedAmount())
									.add(paidGst).add(waivedGst);
						} else if (ma.isDueCreation()) {
							AdviseDueTaxDetail dueTaxDetail = soaReportGenerationDAO
									.getAdviseDueTaxDetails(ma.getAdviseID());
							if (dueTaxDetail != null
									&& FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(dueTaxDetail.getTaxType())) {
								bounceAdvAmount = bounceAdvAmount.add(dueTaxDetail.getTotalGST());
							} else {
								BigDecimal gstAmount = GSTCalculator.getTotalGST(finReference, ma.getAdviseAmount(),
										taxComponent);
								bounceAdvAmount = bounceAdvAmount.add(ma.getAdviseAmount()).add(gstAmount);
							}
						} else if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
							BigDecimal gstAmount = GSTCalculator.getTotalGST(finReference, ma.getAdviseAmount(),
									taxComponent);
							bounceAdvAmount = bounceAdvAmount.add(ma.getAdviseAmount()).add(gstAmount);
						} else {
							bounceAdvAmount = bounceAdvAmount.add(ma.getAdviseAmount());
						}
					}

					taxComponent = bounceFeeType.getTaxComponent();
					if (ma.getPaidAmount() != null) {
						/* GST Calculation only for Exclusive case */
						if (bounceFeeType != null && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
							bouncePaid = bouncePaid.add(ma.getPaidAmount()).add(CalculationUtil.getTotalPaidGST(ma));
						} else {
							bouncePaid = bouncePaid.add(ma.getPaidAmount());
						}
					}

					if (ma.getWaivedAmount() != null) {
						/* GST Calculation only for Exclusive case */
						if (bounceFeeType != null && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
							bounceWaived = bounceWaived.add(ma.getWaivedAmount())
									.add(CalculationUtil.getTotalWaivedGST(ma));
						} else {
							bounceWaived = bounceWaived.add(ma.getWaivedAmount());
						}
					}
				}
			}

			bounceDue = bounceAdvAmount.add(bounceDuee);
			bounceRecipt = bouncePaid.add(bouncePaidAmt);
			bounceWaiver = bounceWaived.add(bounceWaivedAmt);

			otherReceivableDue = rcvAdvAmount;
			otherReceivableReceipt = rcvPaid;
			otherReceivableWaiver = rcvWaived;

			otherPayableDue = adviseBalanceAmt;
		}

		if (finSchdDetList != null && !finSchdDetList.isEmpty()) {
			Date appDate = SysParamUtil.getAppDate();

			for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {

				if ((DateUtil.compare(finSchdDetail.getSchDate(), appDate) <= 0)) {

					if (finSchdDetail.getProfitSchd() != null) {
						totalProfitSchd = totalProfitSchd.add(finSchdDetail.getProfitSchd());
					}

					if (finSchdDetail.getPrincipalSchd() != null) {
						totalPrincipalSchd = totalPrincipalSchd.add(finSchdDetail.getPrincipalSchd());
					}

					if (finSchdDetail.getFeeSchd() != null) {
						totalFeeschd = totalFeeschd.add(finSchdDetail.getFeeSchd());
					}

					if (finSchdDetail.getSchdPriPaid() != null) {
						totalSchdPriPaid = totalSchdPriPaid.add(finSchdDetail.getSchdPriPaid());
					}

					if (finSchdDetail.getSchdPftPaid() != null) {
						totalSchdPftPaid = totalSchdPftPaid.add(finSchdDetail.getSchdPftPaid());
					}

					if (finSchdDetail.getSchdFeePaid() != null) {
						totalSchdfeepaid = totalSchdfeepaid.add(finSchdDetail.getSchdFeePaid());
					}

					if (finSchdDetail.getSchdPftWaiver() != null) {
						totalschdPftWaiver = totalschdPftWaiver.add(finSchdDetail.getSchdPftWaiver());
					}

					if (finSchdDetail.getCpzAmount() != null) {
						totalCpzAmt = totalCpzAmt.add(finSchdDetail.getCpzAmount());
					}

				}
			}

			BigDecimal priWaivedOff = BigDecimal.ZERO;
			BigDecimal pftWaivedOff = BigDecimal.ZERO;
			if (CollectionUtils.isNotEmpty(radList)) {
				for (ReceiptAllocationDetail rad : radList) {
					if (Allocation.EMI.equals(rad.getAllocationType())) {
						continue;
					}
					if (Allocation.FUT_PFT.equals(rad.getAllocationType())
							|| Allocation.PFT.equals(rad.getAllocationType())) {
						pftWaivedOff = pftWaivedOff.add(rad.getWaivedAmount());
					}
					if (Allocation.FUT_PFT.equals(rad.getAllocationType())
							|| Allocation.PFT.equals(rad.getAllocationType())) {
						priWaivedOff = priWaivedOff.add(rad.getWaivedAmount());
					}
				}
			}

			due = totalProfitSchd.add(totalPrincipalSchd).add(totalFeeschd).add(principalDue).add(profitDue);
			receipt = totalSchdPriPaid.add(totalSchdPftPaid).add(totalSchdfeepaid).subtract(totalschdPftWaiver)
					.add(principalPaid).add(profitPaid);

			BigDecimal formatedprincipalPaid = PennantApplicationUtil.formateAmount(principalPaid, 2);
			BigDecimal formatedpftPaid = PennantApplicationUtil.formateAmount(profitPaid, 2);

			statementOfAccount.setEmiReceivedPri(statementOfAccount.getEmiReceivedPri().add(formatedprincipalPaid));
			statementOfAccount.setEmiReceivedPft(statementOfAccount.getEmiReceivedPft().add(formatedpftPaid));

			overDue = due.subtract(receipt).subtract(totalschdPftWaiver).subtract(principalWaived)
					.subtract(profitWaived);

			if (!ImplementationConstants.CUSTOMIZED_SOAREPORT) {
				soaSummaryReport = new SOASummaryReport();
				soaSummaryReport.setComponent("Installment Amount");
				soaSummaryReport.setDue(due);
				soaSummaryReport.setReceipt(receipt.subtract(priWaivedOff).subtract(pftWaivedOff));
				soaSummaryReport.setWaiver(priWaivedOff.add(pftWaivedOff).add(principalWaived).add(profitWaived));
				soaSummaryReport.setOverDue(overDue);
				netDue = overDue;
				soaSummaryReportsList.add(soaSummaryReport);

				due = totalPrincipalSchd.subtract(fm.getAdvanceEMI()).add(principalDue);
				receipt = totalSchdPriPaid.add(principalPaid);

				overDue = due.subtract(receipt).subtract(principalWaived);

				soaSummaryReport = new SOASummaryReport();
				soaSummaryReport.setComponent("Principal Component");
				soaSummaryReport.setDue(due.add(fm.getAdvanceEMI()));
				soaSummaryReport.setReceipt(receipt.subtract(priWaivedOff));
				soaSummaryReport.setWaiver(priWaivedOff.add(principalWaived));
				soaSummaryReport.setOverDue(overDue.add(fm.getAdvanceEMI()));
				soaSummaryReportsList.add(soaSummaryReport);

				due = totalProfitSchd.add(profitDue).add(totalCpzAmt);
				receipt = totalSchdPftPaid.add(profitPaid).add(totalCpzAmt);
				overDue = due.subtract(receipt).subtract(profitWaived);

				soaSummaryReport = new SOASummaryReport();
				soaSummaryReport.setComponent("Interest Component");
				soaSummaryReport.setDue(due);
				soaSummaryReport.setReceipt(receipt.subtract(pftWaivedOff));
				soaSummaryReport.setWaiver(totalschdPftWaiver.add(pftWaivedOff).add(profitWaived));
				soaSummaryReport.setOverDue(overDue);

				soaSummaryReportsList.add(soaSummaryReport);
			}
		}

		if (financeProfitDetail == null) {
			return soaSummaryReportsList;
		}

		BigDecimal totPenaltyAmt = BigDecimal.ZERO;
		BigDecimal totwaived = BigDecimal.ZERO;
		BigDecimal totPenaltyPaid = BigDecimal.ZERO;
		BigDecimal lpiAmt = BigDecimal.ZERO;
		BigDecimal lpiWaived = BigDecimal.ZERO;
		BigDecimal lpiPaid = BigDecimal.ZERO;
		BigDecimal totalCharges = BigDecimal.ZERO;

		boolean isODCExclusive = false;
		odcFeeType = getFeeTypeDAO().getTaxDetailByCode(Allocation.ODC);
		if (odcFeeType != null && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(odcFeeType.getTaxComponent())) {
			isODCExclusive = true;
		}

		if (CollectionUtils.isNotEmpty(finODDetailsList)) {
			for (FinODDetails finODDetails : finODDetailsList) {
				/* GST Calculation only for Exclusive case */
				if (isODCExclusive && finODDetails.getTotPenaltyAmt() != null) {
					BigDecimal gstAmount = GSTCalculator.getTotalGST(finReference, finODDetails.getTotPenaltyAmt(),
							odcFeeType.getTaxComponent());
					totPenaltyAmt = totPenaltyAmt.add(finODDetails.getTotPenaltyAmt()).add(gstAmount);
				} else if (finODDetails.getTotPenaltyAmt() != null) {
					totPenaltyAmt = totPenaltyAmt.add(finODDetails.getTotPenaltyAmt());
				}
				/* GST Calculation only for Exclusive case */
				if (isODCExclusive && finODDetails.getTotPenaltyPaid() != null) {
					BigDecimal gstAmount = GSTCalculator.getTotalGST(finReference, finODDetails.getTotPenaltyPaid(),
							odcFeeType.getTaxComponent());
					totPenaltyPaid = totPenaltyPaid.add(finODDetails.getTotPenaltyPaid()).add(gstAmount);
				} else if (finODDetails.getTotPenaltyAmt() != null) {
					totPenaltyPaid = totPenaltyPaid.add(finODDetails.getTotPenaltyPaid());
				}
				/* GST Calculation only for Exclusive case */
				if (isODCExclusive && finODDetails.getTotWaived() != null) {
					BigDecimal gstAmount = GSTCalculator.getTotalGST(finReference, finODDetails.getTotWaived(),
							odcFeeType.getTaxComponent());
					totwaived = totwaived.add(finODDetails.getTotWaived()).add(gstAmount);
				} else if (finODDetails.getTotWaived() != null) {
					totwaived = totwaived.add(finODDetails.getTotWaived());
				}

				if (finODDetails.getLPIAmt() != null) {
					lpiAmt = lpiAmt.add(finODDetails.getLPIAmt());
				}

				if (finODDetails.getLPIWaived() != null) {
					lpiWaived = lpiWaived.add(finODDetails.getLPIWaived());
				}
				if (finODDetails.getLPIPaid() != null) {
					lpiPaid = lpiPaid.add(finODDetails.getLPIPaid());
				}
			}
		}

		String narration = Labels.getLabel("label_SOA_Late_Payment_Penalty.value");// Late Payment Penalty
		if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
			narration = "Penal Charges";
		}
		if (StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("LATE_PAYMENT_INTEREST_CLIX"))) {
			narration = "Late Payment Interest";
		} else {

			due = lpiAmt;
			receipt = lpiPaid;
			overDue = due.subtract(receipt);
			netDue = netDue.add(overDue).subtract(lpiWaived);

			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Late Payment Interest");
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setWaiver(lpiWaived);
			soaSummaryReport.setOverDue(overDue);

			if (!ImplementationConstants.CUSTOMIZED_SOAREPORT) {
				soaSummaryReportsList.add(soaSummaryReport);
			}

		}
		if (totPenaltyAmt.subtract(totPenaltyPaid).subtract(totwaived).compareTo(BigDecimal.ZERO) >= 0) {
			due = totPenaltyAmt.add(penaltyDue);
			receipt = totPenaltyPaid.add(penaltyPaid);
			overDue = due.subtract(receipt).subtract(totwaived).subtract(penaltyWaived);
		} else {
			due = BigDecimal.ZERO;
			receipt = BigDecimal.ZERO;
			overDue = due.subtract(receipt);
		}

		if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
			due = due.subtract(receipt);
			totalCharges = totalCharges.add(due);
		}

		soaSummaryReport = new SOASummaryReport();
		soaSummaryReport.setComponent(narration);
		soaSummaryReport.setDue(due);
		soaSummaryReport.setReceipt(receipt);
		soaSummaryReport.setWaiver(totwaived.add(penaltyWaived));
		soaSummaryReport.setOverDue(overDue);
		netDue = netDue.add(overDue);

		soaSummaryReportsList.add(soaSummaryReport);

		overDue = bounceDue.subtract(bounceRecipt).subtract(bounceWaiver);

		soaSummaryReport = new SOASummaryReport();
		soaSummaryReport.setComponent("Bounce Charges");
		soaSummaryReport.setDue(bounceDue);

		if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
			bounceDue = overDue;
			if (!fm.isFinIsActive()
					&& !StringUtils.equals(fm.getClosingStatus(), FinanceConstants.CLOSE_STATUS_CANCELLED)) {
				soaSummaryReport.setDue(BigDecimal.ZERO);
				bounceDue = BigDecimal.ZERO;
			}
		}

		soaSummaryReport.setDue(bounceDue);
		totalCharges = totalCharges.add(bounceDue);

		soaSummaryReport.setReceipt(bounceRecipt);
		soaSummaryReport.setWaiver(bounceWaiver);
		soaSummaryReport.setOverDue(overDue);
		netDue = netDue.add(overDue);
		soaSummaryReportsList.add(soaSummaryReport);

		overDue = otherReceivableDue.subtract(otherReceivableReceipt.add(otherReceivableWaiver));

		soaSummaryReport = new SOASummaryReport();
		soaSummaryReport.setComponent("Other Receivables");

		List<FinFeeDetail> fees = getFinFeedetails(finReference);
		for (FinFeeDetail ffd : fees) {
			if (AccountingEvent.EARLYSTL.equals(ffd.getFinEvent())) {
				otherReceivableDue = otherReceivableDue.add(ffd.getPaidAmount()).add(ffd.getRemainingFee());
				otherReceivableReceipt = otherReceivableReceipt.add(ffd.getPaidAmount()).add(ffd.getRemainingFee());
			}
		}

		soaSummaryReport.setDue(otherReceivableDue);
		soaSummaryReport.setReceipt(otherReceivableReceipt);
		soaSummaryReport.setWaiver(otherReceivableWaiver);
		soaSummaryReport.setOverDue(overDue);

		netDue = netDue.add(overDue);

		soaSummaryReportsList.add(soaSummaryReport);

		// Other Receivables duplicate row removed
		/*
		 * if (!ImplementationConstants.CUSTOMIZED_SOAREPORT) { soaSummaryReportsList.add(soaSummaryReport); }
		 */

		receipt = BigDecimal.ZERO;
		overDue = BigDecimal.ZERO;

		soaSummaryReport = new SOASummaryReport();
		soaSummaryReport.setComponent("Other Payables");

		if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
			soaSummaryReport.setComponent("Other Charges");
			totalCharges = totalCharges.add(otherReceivableDue.subtract(otherReceivableReceipt));
			soaSummaryReport.setDue(otherReceivableDue.subtract(otherReceivableReceipt));
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue);
			netDue = netDue.subtract(otherReceivableDue);
		} else {
			totalCharges = totalCharges.add(otherPayableDue);
			soaSummaryReport.setDue(otherPayableDue);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue);
			netDue = netDue.subtract(otherPayableDue);
		}
		soaSummaryReport.setWaiver(BigDecimal.ZERO);

		soaSummaryReportsList.add(soaSummaryReport);

		// FinExcess Amount
		String excessAmt = Labels.getLabel("label_SOA_Unadjustedamount.value");
		BigDecimal unAdjustedAmt = BigDecimal.ZERO;

		finExcessAmountsList = groupByAmountType(finExcessAmountsList);

		if (CollectionUtils.isEmpty(finExcessAmountsList)) {
			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent(excessAmt);
			soaSummaryReport.setDue(BigDecimal.ZERO);
			soaSummaryReport.setReceipt(BigDecimal.ZERO);
			soaSummaryReport.setWaiver(BigDecimal.ZERO);
			soaSummaryReport.setOverDue(BigDecimal.ZERO);
			statementOfAccount.setUnAdjAmt(BigDecimal.ZERO);
			if (!ImplementationConstants.CUSTOMIZED_SOAREPORT) {
				soaSummaryReportsList.add(soaSummaryReport);
			}
		}

		for (FinExcessAmount finExcessAmount : finExcessAmountsList) {
			due = finExcessAmount.getBalanceAmt();

			if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
				unAdjustedAmt = finExcessAmount.getBalanceAmt();
				due = finExcessAmount.getBalanceAmt();
				statementOfAccount.setUnAdjAmt(due.divide(new BigDecimal(100)));
				try {
					statementOfAccount.setUnAdjAmtInWords(due == BigDecimal.ZERO ? ""
							: WordUtils.capitalize(NumberToEnglishWords
									.getAmountInText(PennantApplicationUtil.formateAmount(due, 2), fm.getFinCcy())));
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}

			overDue = BigDecimal.ZERO;
			receipt = BigDecimal.ZERO;

			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent(Labels.getLabel("label_Excess_Type_" + finExcessAmount.getAmountType()));
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setWaiver(BigDecimal.ZERO);
			soaSummaryReport.setOverDue(overDue.negate());
			if (netDue.compareTo(due) < 0) {
				netDue = due.subtract(netDue);
			} else {
				netDue = netDue.subtract(due);
			}
			if (!ImplementationConstants.CUSTOMIZED_SOAREPORT) {
				soaSummaryReportsList.add(soaSummaryReport);
			}
		}

		soaSummaryReport = new SOASummaryReport();
		soaSummaryReport.setComponent("Net Receivable");
		if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
			if (!fm.isFinIsActive()
					&& StringUtils.equals(fm.getClosingStatus(), FinanceConstants.CLOSE_STATUS_CANCELLED)) {
				soaSummaryReport.setDue(BigDecimal.ZERO);
				statementOfAccount.setNetReceivable(BigDecimal.ZERO);
				statementOfAccount.setNetRcvbleInWords("");
			} else {
				soaSummaryReport.setDue(totalCharges.subtract(unAdjustedAmt));
				statementOfAccount.setNetReceivable((totalCharges.subtract(unAdjustedAmt)).divide(new BigDecimal(100)));
				try {
					statementOfAccount.setNetRcvbleInWords(due == BigDecimal.ZERO ? ""
							: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
									PennantApplicationUtil.formateAmount(totalCharges.subtract(unAdjustedAmt), 2),
									fm.getFinCcy())));
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} else {
			soaSummaryReport.setDue(BigDecimal.ZERO);
		}
		soaSummaryReport.setDue(BigDecimal.ZERO);
		soaSummaryReport.setReceipt(BigDecimal.ZERO);
		soaSummaryReport.setWaiver(BigDecimal.ZERO);
		soaSummaryReport.setOverDue(netDue);
		netDue = overDue;

		if (!ImplementationConstants.CUSTOMIZED_SOAREPORT) {
			soaSummaryReportsList.add(soaSummaryReport);
		}

		for (SOASummaryReport report : soaSummaryReportsList) {
			report.setTotalCharges(totalCharges.divide(new BigDecimal(100)));
			try {
				report.setTotalChargesInWords(totalCharges == BigDecimal.ZERO ? ""
						: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
								PennantApplicationUtil.formateAmount(totalCharges, 2), fm.getFinCcy())));
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
			try {
				report.setDueInWords(report.getDue() == BigDecimal.ZERO ? ""
						: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
								PennantApplicationUtil.formateAmount(report.getDue(), 2), fm.getFinCcy())));
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

		logger.debug("Leaving");
		return soaSummaryReportsList;
	}

	/**
	 * to get the Report Fee Details
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private List<SOAFeeDetails> getSOAFeeDetails(StatementOfAccount soa) {

		SOAFeeDetails soaFeeDetails = new SOAFeeDetails();
		List<ManualAdvise> manualAdviseList = getManualAdvise(soa.getFinReference(), soa.getEndDate());
		List<SOAFeeDetails> soaFeeDetailsList = new ArrayList<>();
		// Charges Receivable
		for (ManualAdvise advise : manualAdviseList) {
			if (AdviseType.isReceivable(advise.getAdviseType())) {
				BigDecimal advAmount = advise.getAdviseAmount();
				BigDecimal bal = BigDecimal.ZERO;
				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(advise.getTaxComponent())) {
					Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(soa.getFinID());
					TaxAmountSplit taxAmountSplit = null;
					taxAmountSplit = GSTCalculator.getExclusiveGST(advAmount, taxPercentages);

					BigDecimal paidGst = advise.getPaidCGST().add(advise.getPaidSGST()).add(advise.getPaidIGST())
							.add(advise.getPaidUGST()).add(advise.getPaidIGST()).add(advise.getPaidCESS());
					BigDecimal waivedGst = advise.getWaivedCGST().add(advise.getWaivedSGST())
							.add(advise.getWaivedIGST()).add(advise.getWaivedUGST()).add(advise.getWaivedIGST())
							.add(advise.getWaivedCESS());

					BigDecimal balGst = taxAmountSplit.gettGST().subtract(paidGst).subtract(waivedGst);

					bal = advAmount.subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount()).add(balGst);
				} else {
					bal = advAmount.subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount());
				}
				soaFeeDetails = new SOAFeeDetails();
				if (!(bal.compareTo(BigDecimal.ZERO) == 0)) {
					soaFeeDetails.setFeeType(advise.getFeeTypeDesc());
					soaFeeDetails.setAmount(PennantApplicationUtil.formateAmount(bal, soa.getCcyEditField()));
					soaFeeDetails.setBookingDate(advise.getPostDate());
					soaFeeDetails.setCcyEditField(soa.getCcyEditField());
					soaFeeDetailsList.add(soaFeeDetails);
				}
			}
		}
		return soaFeeDetailsList;
	}

	/**
	 * to get the Report Transaction Details
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public List<SOATransactionReport> getTransactionDetails(String finReference, StatementOfAccount statementOfAccount,
			FinanceMain finMain) throws IllegalAccessException, InvocationTargetException {

		logger.debug("Enetring");

		// Fin Schedule details
		String finSchedulePayable = "Amount Financed - Payable ";// Add Disbursement 1

		String brokenPeriodEvent = "Broken Period Interest Receivable "; // 6
		String foreclosureAmount = "Foreclosure Amount "; // 19
		String dueForInstallment = "Due for Installment "; // 3
		String partPrepayment = "Part Prepayment Amount "; // 5

		// Fin Advance Payments
		String advancePayment = "Amount Paid Vide "; // Amount Paid Vide 2

		// Payment Instructions
		String payInsEvent = "Amount Paid Vide ";// "STLMNT"; 7

		// FinODDetails

		// FinODDetails
		String penality = Labels.getLabel("label_penalty_due_created_for_past_due_till_date.value");// "Penalty Due
																									// Created for Past
																									// due till date ";
																									// //16
		String finODCDue = Labels.getLabel("label_penalty_due_created_for_installemnt_date.value");
		String penaltyUnAccrued = "Unaccrued Penalty due till date "; // 16
		String penalityMnthEnd = "Penalty Accrued on Month-End date ";

		// Manual Advise Movement List
		String manualAdviseMovementEvent = "Waived Amount "; // 13

		// Manual Advise
		String manualAdvFeeType = "- Payable"; // 12
		String manualAdvPrentmentNotIn = "FeeDesc or Bounce - Due "; // 10
		String manualAdvPrentmentIn = ""; // 11

		// Receipt Header
		String rHEventExcess = "Payment Received vide ";// 10
		String rHTdsAdjust = "TDS Adjustment"; // 17
		String rHPaymentBouncedFor = "Payment Bounced For "; // 9
		String rHTdsAdjustReversal = "TDS Adjustment Reversal "; // 18
		String rHManualTdsAmount = "TDS Received vide Receipt No "; // 10
		String rHManualTdsReversalAmount = "TDS Reversal vide Receipt No "; // 10

		// FinFeeDetails
		String finFeeDetailOrgination = "- Due "; // 14
		String finFeeDetailNotInDISBorPOSP = "- Due "; // 15
		String finFeeDetailEvent = "- Due "; // 4
		String finRef = "";// "(" + finReference + ")";
		String rHPftWaived = "Interest from customer Waived Off ";
		String rHPriWaived = "Principal from customer Waived Off ";
		String rHPenaltyWaived = "Penalty from customer Waived Off ";
		String lppWaived = "Penalty from customer Waived Off ";
		String lpiIWaived = "Penalty Interest from customer Waived Off ";

		// AdvanceEMI Details
		String advEmiDebitEntry = "Total Disbursement, Advance EMI"; // 25
		String advEmiCreditEntry = "Advance EMI with maturity date"; // 26
		String downPaymentEntry = "";
		if (ImplementationConstants.ALW_DOWNPAY_IN_LOANENQ_AND_SOA) {
			downPaymentEntry = "DownPayment Amount"; // 26
		} else {
			downPaymentEntry = "Advance EMI";
		}

		String overDuePenalty = "Overdue Penalty";
		SOATransactionReport soaTranReport = null;
		List<SOATransactionReport> soaTransactionReports = new ArrayList<SOATransactionReport>();

		// FinanceMain finMain = getFinanceMain(finReference);

		if (finMain != null) {

			FeeType bounceFeeType = null;

			// Finance Schedule Details
			List<FinanceScheduleDetail> finSchdDetList = getFinScheduleDetails(finReference);
			Date maxSchDate = getMaxSchDate(finReference);

			// Finance Advance Payment Details
			List<FinAdvancePayments> finAdvancePaymentsList = getFinAdvancePayments(finReference);

			// Payment Instruction Details
			List<PaymentInstruction> paymentInstructionsList = getPaymentInstructions(finReference);

			// FinODDetails
			List<FinODDetails> finODDetailsList = getFinODDetails(finReference);

			// Manual Advises List
			List<ManualAdvise> manualAdviseList = getManualAdvise(finReference, statementOfAccount.getEndDate());

			// PresentmentDetails
			List<PresentmentDetail> PresentmentDetailsList = getPresentmentDetailsList(finReference);

			// Fin Receipt Header
			List<FinReceiptHeader> finReceiptHeadersList = getFinReceiptHeaders(finReference);

			Map<Long, List<ReceiptAllocationDetail>> radMap = getReceiptAllocationDetailMap(finReference);

			List<RestructureCharge> rstChrgs = getRestructureChargeList(finReference);

			// Fin Fee Details
			List<FinFeeDetail> finFeedetailsList = getFinFeedetails(finReference);

			List<FeeWaiverDetail> feeWaiverDetailList = getFeeWaiverDetail(finReference);

			// LPP Due creation on monthly
			List<FinOverDueCharges> odcAmounts = getFinODCAmtByRef(finMain.getFinID());

			// Finance Schedule Details
			String closingStatus = finMain.getClosingStatus();
			for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {
				String bpiOrHoliday = finSchdDetail.getBpiOrHoliday();
				BigDecimal repayAmount = finSchdDetail.getRepayAmount();
				BigDecimal principal = finSchdDetail.getPrincipalSchd();
				BigDecimal interest = finSchdDetail.getProfitSchd();

				if (StringUtils.isBlank(closingStatus) || !StringUtils.equalsIgnoreCase(closingStatus, "C")) {

					// Add disbursement
					if (finSchdDetail.isDisbOnSchDate()) {

						BigDecimal transactionAmount = BigDecimal.ZERO;

						if (finSchdDetail.getDisbAmount() != null) {
							transactionAmount = finSchdDetail.getDisbAmount();
						}

						if (DateUtil.compare(finSchdDetail.getSchDate(), finMain.getFinStartDate()) == 0) {
							transactionAmount = transactionAmount.add(finMain.getFeeChargeAmt());
						}

						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(finSchedulePayable + finRef);
						soaTranReport.setTransactionDate(finMain.getFinApprovedDate());
						soaTranReport.setValueDate(finSchdDetail.getSchDate());
						soaTranReport.setCreditAmount(transactionAmount);
						soaTranReport.setDebitAmount(BigDecimal.ZERO);
						soaTranReport.setPriority(1);

						soaTransactionReports.add(soaTranReport);
					}

					// Broken Period Interest Receivable- Due
					if (StringUtils.equalsIgnoreCase("B", bpiOrHoliday) && repayAmount != null
							&& repayAmount.compareTo(BigDecimal.ZERO) > 0) {
						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(brokenPeriodEvent + finRef);
						soaTranReport.setTransactionDate(finSchdDetail.getSchDate());
						soaTranReport.setValueDate(finSchdDetail.getSchDate());
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setDebitAmount(repayAmount);
						soaTranReport.setPriority(8);

						soaTransactionReports.add(soaTranReport);
					}
				}

				// fore closure Amount
				BigDecimal partialPaidAmt = finSchdDetail.getPartialPaidAmt();
				if (maxSchDate != null && DateUtil.compare(maxSchDate, finSchdDetail.getSchDate()) == 0) {
					if (repayAmount.subtract(partialPaidAmt).compareTo(BigDecimal.ZERO) > 0) {
						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(foreclosureAmount + finRef);
						soaTranReport.setTransactionDate(finSchdDetail.getSchDate());
						soaTranReport.setValueDate(finSchdDetail.getSchDate());
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setDebitAmount(repayAmount.subtract(partialPaidAmt));
						soaTranReport.setPriority(23);
						soaTransactionReports.add(soaTranReport);
					}
				}

				if ((DateUtil.compare(finSchdDetail.getSchDate(), SysParamUtil.getAppDate()) <= 0)) {

					// Partial Prepayment Amount
					if (partialPaidAmt != null && partialPaidAmt.compareTo(BigDecimal.ZERO) > 0) {
						if (FinanceConstants.PRODUCT_CD.equals(finMain.getFinCategory())) {
							partPrepayment = "Part Cancellation Amount";
						}
						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(partPrepayment + finRef);
						soaTranReport.setTransactionDate(finSchdDetail.getSchDate());
						soaTranReport.setValueDate(finSchdDetail.getSchDate());
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setDebitAmount(partialPaidAmt);
						soaTranReport.setPriority(7);
						soaTransactionReports.add(soaTranReport);
					}

					// Due for Installment
					if ((StringUtils.isBlank(closingStatus) || !StringUtils.equalsIgnoreCase(closingStatus, "C"))
							&& (!finSchdDetail.isDisbOnSchDate()
									&& DateUtil.compare(maxSchDate, finSchdDetail.getSchDate()) != 0)
							&& (!StringUtils.equalsIgnoreCase(bpiOrHoliday, "H")
									&& !StringUtils.equalsIgnoreCase(bpiOrHoliday, "B"))) {

						if (ImplementationConstants.SOA_INSTALlEMENT_BIFURCATION) {
							BigDecimal principalAmount = BigDecimal.ZERO;
							BigDecimal interestAmount = BigDecimal.ZERO;

							if (principal != null) {
								principalAmount = principal;
								if (partialPaidAmt != null) {
									principalAmount = principalAmount.subtract(partialPaidAmt);
								}
							}

							if (interest != null) {
								interestAmount = interest;
							}

							if (principalAmount.compareTo(BigDecimal.ZERO) > 0) {
								soaTransactionReports
										.add(addInstallment(finSchdDetail, principalAmount, " Principal :"));
							}

							if (interestAmount.compareTo(BigDecimal.ZERO) > 0) {
								soaTransactionReports.add(addInstallment(finSchdDetail, interestAmount, " Interest :"));
							}
						} else {
							BigDecimal transactionAmt = BigDecimal.ZERO;
							if (repayAmount != null) {
								transactionAmt = repayAmount;
							}

							if (partialPaidAmt != null) {
								transactionAmt = transactionAmt.subtract(partialPaidAmt);
							}

							if (transactionAmt.compareTo(BigDecimal.ZERO) > 0) {
								soaTransactionReports.add(addInstallment(finSchdDetail, transactionAmt, ""));
							}
						}
					}
				}
				if (finSchdDetail.getInstNumber() == finMain.getFixedRateTenor()) {
					fixedEndDate = null;
					fixedEndDate = finSchdDetail.getSchDate();
				}

				if (StringUtils.isBlank(closingStatus)
						|| FinanceConstants.CLOSE_STATUS_CANCELLED.equals(finMain.getClosingStatus())) {
					String event = "";
					for (FinOverDueCharges finODCAmount : odcAmounts) {
						if (finODCAmount.getSchDate().compareTo(finSchdDetail.getSchDate()) == 0
								&& finODCAmount.getAmount().compareTo(BigDecimal.ZERO) > 0) {
							event = finODCDue.concat(String.valueOf(finSchdDetail.getInstNumber()));

							if ((StringUtils.equals(finSchdDetail.getSchdMethod(),
									CalculationConstants.SCHMTHD_EQUAL))) {
								// penality = stringReplacement(events.get("PENALTYDUE").toString(), placeHolderMap);
							}

							Date valueDate = finODCAmount.getPostDate();
							if (finODCAmount.getValueDate() != null) {
								valueDate = finODCAmount.getValueDate();
							}
							soaTranReport = new SOATransactionReport();
							soaTranReport.setEvent(event);
							soaTranReport.setTransactionDate(finODCAmount.getPostDate());
							soaTranReport.setValueDate(valueDate);
							soaTranReport.setCreditAmount(BigDecimal.ZERO);
							soaTranReport.setDebitAmount(finODCAmount.getAmount());
							soaTranReport.setPriority(19);
							soaTransactionReports.add(soaTranReport);
						}
					}
				}
			}

			// fin Advance Payments List
			if (finAdvancePaymentsList != null && !finAdvancePaymentsList.isEmpty()) {
				for (FinAdvancePayments fap : finAdvancePaymentsList) {
					if ("VAS".equals(fap.getPaymentDetail())) {
						continue;
					}
					advancePayment = "Amount Paid Vide ";
					String status = "";
					if (StringUtils.equals(fap.getStatus(), DisbursementConstants.STATUS_AWAITCON)) {
						status = " - Subject to realization";
					}
					soaTranReport = new SOATransactionReport();
					String paymentType = fap.getPaymentType();
					if (StringUtils.isNotBlank(paymentType)) {
						advancePayment = advancePayment.concat(paymentType + ":");
					}
					if (StringUtils.equals(paymentType, DisbursementConstants.PAYMENT_TYPE_CHEQUE)
							|| StringUtils.equals(paymentType, DisbursementConstants.PAYMENT_TYPE_DD)) {
						String llReferenceNo = fap.getLlReferenceNo();
						if (StringUtils.isNotBlank(llReferenceNo)) {
							advancePayment = advancePayment.concat(llReferenceNo);
						}
						soaTranReport.setValueDate(fap.getValueDate());
					} else {
						String transactionRef = fap.getTransactionRef();
						if (StringUtils.isNotBlank(transactionRef)) {
							advancePayment = advancePayment.concat(transactionRef);
						}
						soaTranReport.setValueDate(fap.getLlDate());
					}
					soaTranReport.setEvent(advancePayment + finRef + status);
					soaTranReport.setTransactionDate(getTransactionDate(fap));
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
					soaTranReport.setDebitAmount(fap.getAmtToBeReleased());
					soaTranReport.setPriority(2);

					soaTransactionReports.add(soaTranReport);
				}
			}

			// paymentInstructionsList
			for (PaymentInstruction payInstruction : paymentInstructionsList) {
				soaTranReport = new SOATransactionReport();
				if (StringUtils.equals(payInstruction.getStatus(), "REJECTED")) {
					payInsEvent = "Amount Paid Vide-Rejected ";
				} else {
					payInsEvent = "Amount Paid Vide ";
				}
				if (StringUtils.isNotBlank(payInstruction.getPaymentType())) {
					payInsEvent = payInsEvent.concat(payInstruction.getPaymentType() + ":");
				}
				if (StringUtils.equals(payInstruction.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
						|| StringUtils.equals(payInstruction.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_DD)) {
					if (StringUtils.isNotBlank(payInstruction.getFavourNumber())) {
						payInsEvent = payInsEvent.concat(payInstruction.getFavourNumber());
					}
					soaTranReport.setValueDate(payInstruction.getValueDate());
				} else {
					if (StringUtils.isNotBlank(payInstruction.getTransactionRef())) {
						payInsEvent = payInsEvent.concat(payInstruction.getTransactionRef());
					}
					soaTranReport.setValueDate(payInstruction.getPostDate());
				}
				soaTranReport.setEvent(payInsEvent + finRef);
				soaTranReport.setTransactionDate(payInstruction.getPostDate());
				soaTranReport.setCreditAmount(BigDecimal.ZERO);
				soaTranReport.setDebitAmount(payInstruction.getPaymentAmount());
				soaTranReport.setPriority(9);

				soaTransactionReports.add(soaTranReport);
			}

			// FinODDetails
			/*
			 * if (finODDetailsList != null && !finODDetailsList.isEmpty() && (StringUtils.isBlank(closingStatus) ||
			 * !StringUtils.equalsIgnoreCase(closingStatus, "C"))) {
			 * 
			 * for (FinODDetails finODDetails : finODDetailsList) { soaTranReport = new SOATransactionReport();
			 * soaTranReport.setEvent(penality + DateUtility.format(finODDetails.getFinODTillDate(),
			 * DateFormat.SHORT_DATE.getPattern())); soaTranReport.setTransactionDate(finODDetails.getFinODSchdDate());
			 * soaTranReport.setValueDate(finODDetails.getFinODSchdDate());
			 * soaTranReport.setCreditAmount(BigDecimal.ZERO);
			 * soaTranReport.setDebitAmount(finODDetails.getTotPenaltyAmt()); soaTranReport.setPriority(19);
			 * soaTransactionReports.add(soaTranReport); } }
			 */

			for (FeeWaiverDetail fwd : feeWaiverDetailList) {
				if (fwd.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				if (Allocation.ODC.equals(fwd.getFeeTypeCode()) || Allocation.LPFT.equals(fwd.getFeeTypeCode())) {
					continue;
				}

				soaTranReport = new SOATransactionReport();
				manualAdviseMovementEvent = "Waived Amount ";
				manualAdviseMovementEvent = manualAdviseMovementEvent.concat(fwd.getFeeTypeDesc());
				soaTranReport.setEvent(manualAdviseMovementEvent);
				soaTranReport.setTransactionDate(fwd.getPostingDate());
				soaTranReport.setValueDate(fwd.getValueDate());
				soaTranReport.setDebitAmount(BigDecimal.ZERO);
				soaTranReport.setPriority(15);
				soaTranReport.setCreditAmount(fwd.getCurrWaiverAmount());
				soaTransactionReports.add(soaTranReport);
			}

			List<Long> presentmentReceiptIds = new ArrayList<Long>();

			for (PresentmentDetail presentmentDetail : PresentmentDetailsList) {
				if (!presentmentReceiptIds.contains(presentmentDetail.getReceiptID())) {
					presentmentReceiptIds.add(presentmentDetail.getReceiptID());
				}
			}

			// Manual Advise
			for (ManualAdvise manualAdvise : manualAdviseList) {
				manualAdvFeeType = "- Payable"; // 12
				manualAdvPrentmentNotIn = "FeeDesc or Bounce - Due "; // 10
				manualAdvPrentmentIn = " "; // 11

				if ((manualAdvise.getFeeTypeID() != 0 && manualAdvise.getFeeTypeID() != Long.MIN_VALUE)
						&& StringUtils.isNotBlank(manualAdvise.getFeeTypeDesc()) && manualAdvise.getAdviseType() == 2
						&& manualAdvise.getAdviseAmount().compareTo(BigDecimal.ZERO) > 0) {
					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(manualAdvise.getFeeTypeDesc() + manualAdvFeeType + finRef);
					soaTranReport.setTransactionDate(manualAdvise.getPostDate());
					soaTranReport.setValueDate(manualAdvise.getValueDate());

					// 03-09-18:GST Amount For Payable advices is not reflecting in SOA
					BigDecimal gstAmount = BigDecimal.ZERO;
					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(manualAdvise.getTaxComponent())) {
						gstAmount = GSTCalculator.getTotalGST(finReference, manualAdvise.getAdviseAmount(),
								manualAdvise.getTaxComponent());
					}

					soaTranReport.setCreditAmount(manualAdvise.getAdviseAmount().add(gstAmount));
					soaTranReport.setDebitAmount(BigDecimal.ZERO);
					soaTranReport.setPriority(14);

					soaTransactionReports.add(soaTranReport);
				}
				// Bounce/Fee - Due
				if (manualAdvise.getAdviseType() != 2
						&& manualAdvise.getAdviseAmount().compareTo(BigDecimal.ZERO) > 0) {

					if (!presentmentReceiptIds.contains(manualAdvise.getReceiptID())) {

						soaTranReport = new SOATransactionReport();
						String taxComponent = "";
						BigDecimal gstAmount = BigDecimal.ZERO;

						if (manualAdvise.getFeeTypeID() > 0) {

							manualAdvPrentmentNotIn = manualAdvise.getFeeTypeDesc() + " - Due";
							taxComponent = manualAdvise.getTaxComponent();

							if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
								manualAdvPrentmentNotIn = manualAdvPrentmentNotIn + inclusive;
							} else if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
								manualAdvPrentmentNotIn = manualAdvPrentmentNotIn + exclusive;
							}
						} else {
							if (bounceFeeType == null) {
								bounceFeeType = getFeeTypeDAO().getTaxDetailByCode(Allocation.BOUNCE);
							}
							if (bounceFeeType != null) {
								taxComponent = bounceFeeType.getTaxComponent();
								if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
									manualAdvPrentmentNotIn = manualAdvPrentmentNotIn + inclusive;
								}
								if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
									gstAmount = GSTCalculator.getTotalGST(finReference, manualAdvise.getAdviseAmount(),
											taxComponent);
									manualAdvPrentmentNotIn = manualAdvPrentmentNotIn + exclusive;
								}
							}
							manualAdvPrentmentNotIn = "Bounce - Due";
						}

						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
							gstAmount = GSTCalculator.getTotalGST(finReference, manualAdvise.getAdviseAmount(),
									taxComponent);
						}

						soaTranReport.setEvent(manualAdvPrentmentNotIn + finRef);
						soaTranReport.setTransactionDate(manualAdvise.getPostDate());
						soaTranReport.setValueDate(manualAdvise.getValueDate());
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setDebitAmount(manualAdvise.getAdviseAmount().add(gstAmount));
						soaTranReport.setPriority(12);

						soaTransactionReports.add(soaTranReport);
					} else {
						// Bounce created for particular on Installment
						if (manualAdvise.getFeeTypeID() == 0) {
							String taxComponent = "";
							BigDecimal gstAmount = BigDecimal.ZERO;
							taxComponent = manualAdvise.getTaxComponent();

							for (PresentmentDetail presentmentDetail : PresentmentDetailsList) {

								if (manualAdvise.getReceiptID() == presentmentDetail.getReceiptID()) {

									for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {

										if (DateUtil.compare(presentmentDetail.getSchDate(),
												finSchdDetail.getSchDate()) == 0) {
											manualAdvPrentmentIn = presentmentDetail.getBounceReason() != null
													? "Bounce Created for " + "'" + presentmentDetail.getBounceReason()
															+ "'" + " on Installment:"
													: "Bounce Created for " + "'"
															+ StringUtils.trimToEmpty(manualAdvise.getBounceCodeDesc())
															+ "'" + " on Installment:";
											soaTranReport = new SOATransactionReport();
											if (finSchdDetail.getInstNumber() > 0) {
												manualAdvPrentmentIn = manualAdvPrentmentIn
														.concat(String.valueOf(finSchdDetail.getInstNumber()));
											}
											if (bounceFeeType == null) {
												bounceFeeType = getFeeTypeDAO().getTaxDetailByCode(Allocation.BOUNCE);
											}
											if (bounceFeeType != null) {
												taxComponent = bounceFeeType.getTaxComponent();
											}
											if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
												gstAmount = GSTCalculator.getTotalGST(finReference,
														manualAdvise.getAdviseAmount(), taxComponent);
												manualAdvPrentmentIn = manualAdvPrentmentIn + exclusive;
											} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE
													.equals(taxComponent)) {
												manualAdvPrentmentIn = manualAdvPrentmentIn + inclusive;
											}

											manualAdvPrentmentIn = manualAdvPrentmentIn.concat(finRef);
											if (manualAdvise.getRemarks() != null) {
												manualAdvPrentmentIn = manualAdvPrentmentIn
														.concat(" and Bounce Remarks: " + manualAdvise.getRemarks());
											}
											soaTranReport.setEvent(manualAdvPrentmentIn);
											soaTranReport.setTransactionDate(manualAdvise.getPostDate());
											soaTranReport.setValueDate(manualAdvise.getValueDate());
											soaTranReport.setCreditAmount(BigDecimal.ZERO);

											soaTranReport.setDebitAmount(manualAdvise.getAdviseAmount().add(gstAmount));
											soaTranReport.setPriority(13);

											soaTransactionReports.add(soaTranReport);
										}
									}
								}
							}
						}
					}
				}
			}

			// FinReceiptDetails List
			List<FinReceiptDetail> finReceiptDetailsList = getFinReceiptDetails(finReference);

			// FinRepayHeaders List
			List<FinRepayHeader> finRepayHeadersList = getFinRepayHeadersList(finReference);

			// FinRepayscheduledetails List
			List<RepayScheduleDetail> finRepaySchdDetails = getRepayScheduleDetailsList(finReference);

			// FinReceipt Allocation Details
			List<ReceiptAllocationDetail> finReceiptAllocDetails = getReceiptAllocationDetailsList(finReference);

			// FinReceiptDetails List
			finReceiptDetailsList = getFinReceiptDetails(finReference);

			// FinRepayHeaders List
			finRepayHeadersList = getFinRepayHeadersList(finReference);

			// FinRepayscheduledetails List
			finRepaySchdDetails = getRepayScheduleDetailsList(finReference);

			// FinReceipt Allocation Details
			finReceiptAllocDetails = getReceiptAllocationDetailsList(finReference);

			// Inst NO Based on
			// Map<Long, Integer> instNumbers = soaReportGenerationDAO.getInstNumber(finReference);

			for (FinReceiptHeader rh : finReceiptHeadersList) {
				for (FinReceiptDetail rd : finReceiptDetailsList) {
					long receiptID = rd.getReceiptID();
					long rhReceiptID = rh.getReceiptID();
					// int instlNo = 0;

					if (receiptID != rhReceiptID) {
						continue;
					}

					rHEventExcess = "Payment Received vide ";
					String rpaymentType = StringUtils.trimToEmpty(rd.getPaymentType());

					// Displaying label instead of Constant
					if (ReceiptMode.DIGITAL.equals(rpaymentType)) {
						rpaymentType = Labels.getLabel("label_PaymentType_DIGITAL");
					}

					String receiptModeStatus = StringUtils.trimToEmpty(rh.getReceiptModeStatus());
					// 30-08-2019:Receipt date and value date should populate in SOA
					Date receiptDate = rh.getReceiptDate();
					Date receivedDate = rh.getValueDate();

					if (receivedDate == null) {
						receivedDate = rd.getValueDate();
					}

					String presentmentType = "";

					String favourNumber = rd.getFavourNumber();

					String status = "";
					String paymentType = "";
					soaTranReport = new SOATransactionReport();

					if (!(StringUtils.equals("EXCESS", rpaymentType) || StringUtils.equals("CASH", rpaymentType))) {
						receiptDate = rh.getReceivedDate();
						receivedDate = rh.getReceiptDate();

						for (PresentmentDetail pd : PresentmentDetailsList) {
							String mandateType = StringUtils.trimToEmpty(pd.getMandateType());

							if (receiptID == pd.getReceiptID() && ReceiptMode.PRESENTMENT.equals(rpaymentType)) {
								presentmentType = pd.getPresentmentType();
								if (StringUtils.equals(pd.getStatus(), RepayConstants.PEXC_APPROV)) {
									status = " - Subject to realization";
								}

								if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
									if (("P".equals(pd.getPresentmentType()))) {
										soaTranReport.setTransactionDate(receivedDate);
									}
								}

								// instlNo = instNumbers.computeIfAbsent(pd.getReceiptID(), rid -> 0);

								if (InstrumentType.isDD(mandateType)) {
									paymentType = "Direct Debit";
								} else {
									paymentType = mandateType;
								}
								if (pd.getPresentmentType().equals(PennantConstants.PROCESS_REPRESENTMENT)) {
									paymentType = paymentType.concat(
											" " + Labels.getLabel("label_PresentmentExtractionType_RePresentment"));
								}
								paymentType = paymentType.concat(" EMI NO.: " + pd.getEmiNo());
							}

						}

						if (StringUtils.equals(rpaymentType, ReceiptMode.CHEQUE)
								&& StringUtils.equals(receiptModeStatus, RepayConstants.PAYSTATUS_DEPOSITED)) {
							status = " - Subject to realization";
						}

						if (StringUtils.isNotBlank(rpaymentType)) {
							if (ReceiptMode.CHEQUE.equals(rpaymentType) || ReceiptMode.DD.equals(rpaymentType)) {
								paymentType = WordUtils.capitalize(rpaymentType) + " No.:";
								if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
									soaTranReport.setTransactionDate(receivedDate);
								}
							} else if (!StringUtils.equals(rpaymentType, ReceiptMode.PRESENTMENT)
									&& !StringUtils.equals("PAYABLE", rpaymentType)) {
								paymentType = rpaymentType + " No.:";
							}

							if ("PAYABLE".equals(rpaymentType)) {
								rHEventExcess = "Amount Adjusted " + finRef;
							}

							// Restructure Receipt
							if (AccountingEvent.RESTRUCTURE.equals(rpaymentType)) {
								rHEventExcess = "Amount Capitalized vide  ";
								receivedDate = rh.getValueDate();
							}

							rHEventExcess = rHEventExcess.concat(paymentType);
						}
						if (StringUtils.isNotBlank(rd.getTransactionRef())) {
							rHEventExcess = rHEventExcess.concat(rd.getTransactionRef());
						}
						if (StringUtils.isNotBlank(favourNumber)) {
							rHEventExcess = rHEventExcess.concat(favourNumber);
						}
						rHEventExcess = rHEventExcess.concat(" " + finRef);

					} else if (StringUtils.equals("EXCESS", rpaymentType)) {
						rHEventExcess = "Amount Adjusted " + finRef;
					} else if (StringUtils.equals("CASH", rpaymentType)) {
						rHEventExcess = "Cash Received Vide Receipt No ";
						if (StringUtils.isNotBlank(rd.getTransactionRef())
								&& ImplementationConstants.ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE) {
							rHEventExcess = rHEventExcess.concat(rd.getTransactionRef());
						} else if (StringUtils.isNotBlank(rd.getPaymentRef())) {
							rHEventExcess = rHEventExcess.concat(rd.getPaymentRef() + finRef);
						} else {
							if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
								rHEventExcess = rHEventExcess.concat(String.valueOf(receiptID));
							}
						}

					}
					String allocMsg = "";
					if (radMap.containsKey(rh.getReceiptID())) {
						int ccy = CurrencyUtil.getFormat(finMain.getFinCcy());

						if (AccountingEvent.RESTRUCTURE.equals(rpaymentType)) {
							allocMsg = buildAllocationDataForRestructure(rstChrgs, ccy);
						} else {
							allocMsg = buildAllocationData(radMap.get(rh.getReceiptID()), ccy);
						}
					}
					soaTranReport.setValueDate(receivedDate);
					soaTranReport.setEvent(rHEventExcess + status + allocMsg);
					soaTranReport.setTransactionDate(receiptDate);
					soaTranReport.setCreditAmount(rd.getAmount());

					if ("EXCESS".equals(rpaymentType) || "PAYABLE".equals(rpaymentType)) {
						soaTranReport.setDebitAmount(rd.getAmount());
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
					} else {
						soaTranReport.setDebitAmount(BigDecimal.ZERO);
					}
					soaTranReport.setPriority(10);
					if (!RepayConstants.PAYSTATUS_CANCEL.equals(receiptModeStatus)) {
						soaTransactionReports.add(soaTranReport);
					}

					// Restructure BPI & Fee capitalization events
					if (rpaymentType.equals(AccountingEvent.RESTRUCTURE)) {
						for (RestructureCharge rstChrg : rstChrgs) {

							if (!StringUtils.equals("BPI", rstChrg.getAlocType())
									&& !StringUtils.equals(Allocation.FEE, rstChrg.getAlocType())) {
								continue;
							}

							soaTranReport = new SOATransactionReport();
							soaTranReport.setValueDate(receivedDate);
							soaTranReport.setTransactionDate(receiptDate);
							if (StringUtils.equals("BPI", rstChrg.getAlocType())) {
								soaTranReport.setEvent("BPI Capitalized vide " + rpaymentType);
							} else if (StringUtils.equals(Allocation.FEE, rstChrg.getAlocType())) {
								soaTranReport.setEvent("Fee Capitalized vide " + rpaymentType);
							}
							soaTranReport.setCreditAmount(BigDecimal.ZERO);
							soaTranReport.setDebitAmount(rstChrg.getTotalAmount());
							soaTranReport.setPriority(28);
							soaTransactionReports.add(soaTranReport);
						}
					}

					// Manual TDS
					if (rh.getTdsAmount().compareTo(BigDecimal.ZERO) > 0) {
						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(rHManualTdsAmount.concat(String.valueOf(receiptID)));
						soaTranReport.setTransactionDate(receiptDate);
						soaTranReport.setValueDate(receiptDate);
						soaTranReport.setCreditAmount(rh.getTdsAmount());
						soaTranReport.setDebitAmount(BigDecimal.ZERO);
						soaTranReport.setPriority(10);
						soaTransactionReports.add(soaTranReport);
					}

					// Cancelled Manual TDS
					if (rh.getTdsAmount().compareTo(BigDecimal.ZERO) > 0
							&& ("B".equalsIgnoreCase(rd.getStatus()) || "C".equalsIgnoreCase(rd.getStatus()))) {
						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(rHManualTdsReversalAmount.concat(String.valueOf(receiptID)));
						soaTranReport.setTransactionDate(receiptDate);
						soaTranReport.setValueDate(receiptDate);
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setDebitAmount(rh.getTdsAmount());
						soaTranReport.setPriority(10);
						soaTransactionReports.add(soaTranReport);
					}

					// Cancelled Receipt's Details
					if (SysParamUtil.isAllowed(SMTParameterConstants.SOA_SHOW_CANCEL_RECEIPT)
							&& !FinanceConstants.CLOSE_STATUS_CANCELLED.equals(finMain.getClosingStatus())
							&& RepayConstants.PAYSTATUS_CANCEL.equals(receiptModeStatus)) {
						soaTransactionReports.add(soaTranReport);
						SOATransactionReport cancelReport = new SOATransactionReport();
						BeanUtils.copyProperties(cancelReport, soaTranReport);
						cancelReport.setDebitAmount(rd.getAmount());
						if (StringUtils.equals(rpaymentType, "EXCESS")) {
							cancelReport.setCreditAmount(rd.getAmount());
						} else {
							cancelReport.setCreditAmount(BigDecimal.ZERO);
						}
						cancelReport.setEvent(rHEventExcess.replaceAll("Received", "Cancelled") + status);
						soaTransactionReports.add(cancelReport);
					}

					boolean tdsEntryReq = true;
					if ((StringUtils.equalsIgnoreCase(rh.getReceiptModeStatus(), "B")
							|| StringUtils.equalsIgnoreCase(rh.getReceiptModeStatus(), "C"))
							&& !SysParamUtil.isAllowed(SMTParameterConstants.DISPLAY_TDS_REV_SOA)) {
						tdsEntryReq = false;
					}

					// Receipt Allocation Details
					if (tdsEntryReq) {
						for (ReceiptAllocationDetail finReceiptAllocationDetail : finReceiptAllocDetails) {

							if (rhReceiptID == finReceiptAllocationDetail.getReceiptID()
									&& (StringUtils.equalsIgnoreCase("TDS",
											finReceiptAllocationDetail.getAllocationType())
											|| finReceiptAllocationDetail.getTdsPaid().compareTo(BigDecimal.ZERO) > 0)
									&& (finReceiptAllocationDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0)) {

								soaTranReport = new SOATransactionReport();
								soaTranReport.setEvent(rHTdsAdjust + finRef);
								soaTranReport.setTransactionDate(receiptDate);
								soaTranReport.setValueDate(receivedDate);
								soaTranReport.setCreditAmount(
										finReceiptAllocationDetail.getTdsPaid().compareTo(BigDecimal.ZERO) > 0
												? finReceiptAllocationDetail.getTdsPaid()
												: finReceiptAllocationDetail.getPaidAmount());
								soaTranReport.setDebitAmount(BigDecimal.ZERO);
								soaTranReport.setPriority(21);
								soaTransactionReports.add(soaTranReport);
							}
						}
					}

					// Receipt Header with Manual Advise
					if (StringUtils.equals(rh.getReceiptMode(), rpaymentType)) {

						for (ManualAdvise manualAdvise : manualAdviseList) {
							rHPaymentBouncedFor = "Payment Bounced For "; // 9
							if (rhReceiptID == manualAdvise.getReceiptID()) {

								if (StringUtils.equals(receiptModeStatus, "B") && manualAdvise.getAdviseType() == 1
										&& manualAdvise.getBounceID() > 0) {

									soaTranReport = new SOATransactionReport();
									if (!(StringUtils.equals("EXCESS", rpaymentType)
											|| StringUtils.equals("CASH", rpaymentType))) {
										rHPaymentBouncedFor = rHPaymentBouncedFor.concat(rpaymentType + "No.:");
										if (StringUtils.isNotBlank(favourNumber)) {
											rHPaymentBouncedFor = rHPaymentBouncedFor.concat(favourNumber);
										} else if (StringUtils.isNotBlank(rd.getTransactionRef())) {
											rHPaymentBouncedFor = rHPaymentBouncedFor.concat(rd.getTransactionRef());
										}
									} else if (StringUtils.equals("CASH", rpaymentType)) {
										rHPaymentBouncedFor = "Cash Bounced For Receipt No.";
										if (StringUtils.isNotBlank(rd.getPaymentRef())) {
											rHPaymentBouncedFor = rHPaymentBouncedFor.concat(rd.getPaymentRef());
										}
										rHPaymentBouncedFor = rHPaymentBouncedFor.concat(finRef);
									}
									soaTranReport.setEvent(rHPaymentBouncedFor);
									soaTranReport.setTransactionDate(manualAdvise.getPostDate());
									if (ImplementationConstants.CUSTOMIZED_SOAREPORT) {
										if ("P".equals(presentmentType)) {
											soaTranReport.setTransactionDate(receivedDate);
										}
									}
									soaTranReport.setValueDate(rh.getBounceDate());
									soaTranReport.setCreditAmount(BigDecimal.ZERO);
									soaTranReport.setDebitAmount(rd.getAmount());
									soaTranReport.setPriority(11);
									soaTransactionReports.add(soaTranReport);
								}
							}
						}
					}

					if (finRepayHeadersList != null && !finRepayHeadersList.isEmpty()) {

						for (FinRepayHeader finRepayHeader : finRepayHeadersList) {

							if (rd.getReceiptSeqID() == finRepayHeader.getReceiptSeqID()) {

								BigDecimal totalTdsSchdPayNow = BigDecimal.ZERO;
								for (RepayScheduleDetail finRpySchdDetail : finRepaySchdDetails) {

									if (finRpySchdDetail.getRepayID() == finRepayHeader.getRepayID()) {

										if (finRpySchdDetail.getTdsSchdPayNow() != null
												&& finRpySchdDetail.getTdsSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
											totalTdsSchdPayNow = totalTdsSchdPayNow
													.add(finRpySchdDetail.getTdsSchdPayNow());

										}
										// Interest from customer Waived Off
										BigDecimal pftSchdWaivedNow = finRpySchdDetail.getPftSchdWaivedNow();
										if (pftSchdWaivedNow != null
												&& pftSchdWaivedNow.compareTo(BigDecimal.ZERO) > 0) {
											soaTranReport = new SOATransactionReport();
											soaTranReport.setEvent(rHPftWaived + finRef);
											soaTranReport.setTransactionDate(receivedDate);
											soaTranReport.setValueDate(rd.getValueDate());
											soaTranReport.setCreditAmount(pftSchdWaivedNow);
											soaTranReport.setDebitAmount(BigDecimal.ZERO);
											soaTranReport.setPriority(4);
											soaTransactionReports.add(soaTranReport);

										}
										// Principal from customer Waived Off
										BigDecimal principalSchdPayNow = finRpySchdDetail.getPrincipalSchdPayNow();
										if (principalSchdPayNow != null
												&& principalSchdPayNow.compareTo(BigDecimal.ZERO) > 0) {
											soaTranReport = new SOATransactionReport();
											soaTranReport.setEvent(rHPriWaived + finRef);
											soaTranReport.setTransactionDate(receivedDate);
											soaTranReport.setValueDate(rd.getValueDate());
											soaTranReport.setCreditAmount(principalSchdPayNow);
											soaTranReport.setDebitAmount(BigDecimal.ZERO);
											soaTranReport.setPriority(5);
											soaTransactionReports.add(soaTranReport);

										}
										// Penalty from customer Waived Off
										BigDecimal waivedAmt = finRpySchdDetail.getWaivedAmt();
										if ((StringUtils.isBlank(closingStatus)
												|| !StringUtils.equalsIgnoreCase(closingStatus, "C"))
												&& waivedAmt != null && waivedAmt.compareTo(BigDecimal.ZERO) > 0) {
											soaTranReport = new SOATransactionReport();
											soaTranReport.setEvent(rHPenaltyWaived + finRef);
											soaTranReport.setTransactionDate(receiptDate);
											soaTranReport.setValueDate(receivedDate);
											soaTranReport.setCreditAmount(waivedAmt);
											soaTranReport.setDebitAmount(BigDecimal.ZERO);
											soaTranReport.setPriority(20);
											soaTransactionReports.add(soaTranReport);
										}
									}
								}

								if ((StringUtils.equalsIgnoreCase(rd.getStatus(), "B")
										|| StringUtils.equalsIgnoreCase(rd.getStatus(), "C")) && tdsEntryReq) {
									// TDS Adjustment Reversal
									if (totalTdsSchdPayNow.compareTo(BigDecimal.ZERO) > 0) {
										soaTranReport = new SOATransactionReport();
										soaTranReport.setEvent(rHTdsAdjustReversal + finRef);
										soaTranReport.setTransactionDate(rh.getReceiptDate());
										soaTranReport.setValueDate(rh.getBounceDate());
										soaTranReport.setCreditAmount(BigDecimal.ZERO);
										soaTranReport.setDebitAmount(totalTdsSchdPayNow);
										soaTranReport.setPriority(22);
										soaTransactionReports.add(soaTranReport);
									}

								}
							}

						}
					}
				}
			}

			// VAS Recordings
			List<VASRecording> VASRecordingsList = getVASRecordingsList(finReference);
			// Fin fee schedule details
			List<FinFeeScheduleDetail> finFeeScheduleDetailsList = getFinFeeScheduleDetailsList(finReference);

			if (StringUtils.isBlank(closingStatus) || !StringUtils.equalsIgnoreCase(closingStatus, "C")) {

				// Fin Fee Details List
				if (finFeedetailsList != null && !finFeedetailsList.isEmpty()) {

					VASRecordingsList = getVASRecordingsList(finReference);
					finFeeScheduleDetailsList = getFinFeeScheduleDetailsList(finReference);

					for (FinFeeDetail finFeeDetail : finFeedetailsList) {
						if (AccountingEvent.RESTRUCTURE.equals(finFeeDetail.getFinEvent())) {
							continue;
						}
						finFeeDetailOrgination = " Amount";
						finFeeDetailNotInDISBorPOSP = " Amount"; // 15
						String vasProduct = null;
						for (VASRecording vASRecording : VASRecordingsList) {
							if (StringUtils.equals(finReference, vASRecording.getPrimaryLinkRef()) && StringUtils
									.equals(finFeeDetail.getVasReference(), vASRecording.getVasReference())) {
								vasProduct = vASRecording.getProductDesc();
							}
						}
						BigDecimal debitAmount = BigDecimal.ZERO;
						BigDecimal waivedAmount = BigDecimal.ZERO;
						// Fee/Vas - Due
						BigDecimal paidAmount = finFeeDetail.getPaidAmount();
						String feeTypeDesc = finFeeDetail.getFeeTypeDesc();

						if (CalculationConstants.REMFEE_PART_OF_DISBURSE.equals(finFeeDetail.getFeeScheduleMethod())
								|| CalculationConstants.REMFEE_PART_OF_SALE_PRICE
										.equals(finFeeDetail.getFeeScheduleMethod())) {

							// if (finFeeDetail.isOriginationFee()) {} Removed this as part of Advance Interest/Advance
							// EMI Changes.

							if (finFeeDetail.getRemainingFee() != null) {
								debitAmount = finFeeDetail.getRemainingFee();
							}

							if (paidAmount != null) {
								debitAmount = debitAmount.add(paidAmount);
							}

							if (debitAmount.compareTo(BigDecimal.ZERO) > 0) {

								soaTranReport = new SOATransactionReport();
								if (StringUtils.isNotBlank(feeTypeDesc)) {

									finFeeDetailOrgination = feeTypeDesc + " Amount";
									String taxComponent = finFeeDetail.getTaxComponent();
									if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
										finFeeDetailOrgination = finFeeDetailOrgination + inclusive;
									} else if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
										finFeeDetailOrgination = finFeeDetailOrgination + exclusive;
									}
								} else {
									finFeeDetailOrgination = vasProduct + " Amount";
								}
								soaTranReport.setEvent(finFeeDetailOrgination + finRef);
								soaTranReport.setTransactionDate(finMain.getFinApprovedDate());
								soaTranReport.setValueDate(finMain.getFinStartDate());
								soaTranReport.setCreditAmount(BigDecimal.ZERO);
								soaTranReport.setDebitAmount(debitAmount);
								soaTranReport.setPriority(16);

								soaTransactionReports.add(soaTranReport);
							}

						} else {
							if (paidAmount != null && paidAmount.compareTo(BigDecimal.ZERO) > 0) {
								soaTranReport = new SOATransactionReport();
								if (StringUtils.isNotBlank(feeTypeDesc)) {
									finFeeDetailNotInDISBorPOSP = feeTypeDesc;
								} else if (StringUtils.isNotBlank(vasProduct)) {
									finFeeDetailNotInDISBorPOSP = vasProduct;
								} else {
									finFeeDetailNotInDISBorPOSP = finFeeDetail.getFinEvent();
								}
								soaTranReport.setEvent(finFeeDetailNotInDISBorPOSP + " Amount" + finRef);
								if (StringUtils.isNotBlank(exclusive)) {
									if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE
											.equals(finFeeDetail.getTaxComponent())) {
										soaTranReport.setEvent(finFeeDetailNotInDISBorPOSP + " Amount" + exclusive);
									} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE
											.equals(finFeeDetail.getTaxComponent())) {
										soaTranReport.setEvent(finFeeDetailNotInDISBorPOSP + " Amount" + inclusive);
									}
								}

								soaTranReport.setTransactionDate(finFeeDetail.getPostDate());
								soaTranReport.setValueDate(finFeeDetail.getPostDate());
								soaTranReport.setCreditAmount(BigDecimal.ZERO);
								soaTranReport.setDebitAmount(paidAmount);
								soaTranReport.setPriority(17);
								soaTransactionReports.add(soaTranReport);
							}
						}
						// Waived amount for Fee/Vas

						if (finFeeDetail.getWaivedAmount() != null) {
							waivedAmount = waivedAmount.add(finFeeDetail.getWaivedAmount());
						}
						if (finFeeDetail.getWaivedAmount() != null && waivedAmount.compareTo(BigDecimal.ZERO) > 0) {
							soaTranReport = new SOATransactionReport();
							if (StringUtils.isNotBlank(finFeeDetail.getFeeTypeDesc())) {
								finFeeDetailOrgination = finFeeDetail.getFeeTypeDesc();
							} else {
								finFeeDetailOrgination = vasProduct;
							}
							soaTranReport.setEvent(finFeeDetailOrgination + " from customer Waived Off" + finRef);
							soaTranReport.setTransactionDate(finFeeDetail.getPostDate());
							soaTranReport.setValueDate(finFeeDetail.getPostDate());
							soaTranReport.setCreditAmount(waivedAmount);
							soaTranReport.setDebitAmount(BigDecimal.ZERO);
							soaTranReport.setPriority(18);
							soaTransactionReports.add(soaTranReport);
						}
					}

					// Fin Fee Schedule Details List
					if (finFeeScheduleDetailsList != null && !finFeeScheduleDetailsList.isEmpty()) {

						for (FinFeeScheduleDetail finFeeScheduleDetail : finFeeScheduleDetailsList) {

							soaTranReport = new SOATransactionReport();
							String feeTypeDesc = StringUtils.trimToEmpty(finFeeScheduleDetail.getFeeTypeDesc());
							soaTranReport.setEvent(feeTypeDesc + finFeeDetailEvent + finRef);
							soaTranReport.setTransactionDate(finFeeScheduleDetail.getSchDate());
							soaTranReport.setValueDate(finFeeScheduleDetail.getSchDate());
							soaTranReport.setCreditAmount(BigDecimal.ZERO);
							soaTranReport.setDebitAmount(finFeeScheduleDetail.getSchAmount());
							soaTranReport.setPriority(6);

							soaTransactionReports.add(soaTranReport);
						}
					}
				}

			}

			// LPP and LPI waived from the waiver screen
			if (feeWaiverDetailList != null && !feeWaiverDetailList.isEmpty()) {
				for (FeeWaiverDetail waiverDetail : feeWaiverDetailList) {
					String lable = "";
					if (Allocation.ODC.equalsIgnoreCase(waiverDetail.getFeeTypeCode())) {
						lable = lppWaived;
					} else if (Allocation.LPFT.equalsIgnoreCase(waiverDetail.getFeeTypeCode())) {
						lable = lpiIWaived;
					}

					if (StringUtils.isNotBlank(lable)
							&& waiverDetail.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) > 0) {
						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(lable + finRef);
						soaTranReport.setTransactionDate(waiverDetail.getValueDate());
						soaTranReport.setValueDate(waiverDetail.getValueDate());
						soaTranReport.setCreditAmount(waiverDetail.getCurrWaiverAmount());
						soaTranReport.setDebitAmount(BigDecimal.ZERO);
						soaTranReport.setPriority(24);
						soaTransactionReports.add(soaTranReport);
					}

				}
			}

			// Advance EMI should be shown on transaction as total disbursement, advance emi debit entry.
			if (finSchdDetList != null && !finSchdDetList.isEmpty()) {
				for (FinanceScheduleDetail financeScheduleDetail : finSchdDetList) {
					if (financeScheduleDetail.getDisbAmount() != null
							&& BigDecimal.ZERO.compareTo(finMain.getAdvanceEMI()) != 0) {
						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(advEmiDebitEntry + finRef);
						soaTranReport.setTransactionDate(finMain.getFinApprovedDate());
						soaTranReport.setValueDate(finMain.getFinStartDate());
						if (StringUtils.equals(finMain.getAdvType(), AdvanceType.AE.name())) {
							soaTranReport.setDebitAmount(finMain.getAdvanceEMI());
						} else {
							soaTranReport.setDebitAmount(BigDecimal.ZERO);
						}
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setPriority(25);
						soaTransactionReports.add(soaTranReport);
					}
					break;
				}
			}

			// Advance EMI
			if (finMain != null && (FinanceConstants.PRODUCT_CD.equals(finMain.getFinCategory())
					|| ImplementationConstants.ALW_DOWNPAY_IN_LOANENQ_AND_SOA)) {
				if (finSchdDetList != null && !finSchdDetList.isEmpty()) {
					for (FinanceScheduleDetail financeScheduleDetail : finSchdDetList) {
						if (financeScheduleDetail.getDisbAmount() != null
								&& BigDecimal.ZERO.compareTo(finMain.getDownPayment()) != 0) {
							soaTranReport = new SOATransactionReport();
							soaTranReport.setEvent(downPaymentEntry + finRef);
							soaTranReport.setTransactionDate(finMain.getFinApprovedDate());
							soaTranReport.setValueDate(finMain.getFinStartDate());
							soaTranReport.setDebitAmount(finMain.getDownPayment());
							soaTranReport.setCreditAmount(BigDecimal.ZERO);
							soaTranReport.setPriority(6);
							soaTransactionReports.add(soaTranReport);
						}
						break;
					}
				}
			}

			// AdvanceEMI credit entry with maturity date
			if (BigDecimal.ZERO.compareTo(finMain.getAdvanceEMI()) != 0) {
				soaTranReport = new SOATransactionReport();
				soaTranReport.setTransactionDate(finMain.getMaturityDate());
				soaTranReport.setValueDate(finMain.getMaturityDate());
				soaTranReport.setEvent(advEmiCreditEntry + finRef);
				if (StringUtils.equals(finMain.getAdvType(), AdvanceType.AE.name())) {
					soaTranReport.setCreditAmount(finMain.getAdvanceEMI());
				} else {
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
				}
				soaTranReport.setPriority(26);
				soaTransactionReports.add(soaTranReport);
			}

			// LPP Penalty Accrued on Month-End date
			List<FinODAmzTaxDetail> finODAmzTaxDetailList = getFinODAmzTaxDetailDAO()
					.getFinODAmzTaxDetail(finMain.getFinID());
			if (finODAmzTaxDetailList != null && !finODAmzTaxDetailList.isEmpty()) {
				for (FinODAmzTaxDetail finODAmzTaxDetail : finODAmzTaxDetailList) {
					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(
							penalityMnthEnd + DateUtil.format(finODAmzTaxDetail.getValueDate(), "dd/MM/yyyy"));
					soaTranReport.setTransactionDate(finODAmzTaxDetail.getValueDate());
					soaTranReport.setValueDate(finODAmzTaxDetail.getValueDate());
					if (StringUtils.equals(finODAmzTaxDetail.getTaxType(),
							FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
						soaTranReport
								.setDebitAmount(finODAmzTaxDetail.getAmount().add(finODAmzTaxDetail.getTotalGST()));
					} else if (StringUtils.equals(finODAmzTaxDetail.getTaxType(),
							FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
						soaTranReport.setDebitAmount(finODAmzTaxDetail.getAmount());
					}
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
					soaTranReport.setPriority(27);
					soaTransactionReports.add(soaTranReport);
				}
			}

			// LPP Penalty UnAccrued Paid on Receipts
			if (ImplementationConstants.SOA_SHOW_UNACCURED_PENALITY && LPPExtension.LPP_DUE_CREATION_REQ) {
				List<FinTaxIncomeDetail> incomeList = finODAmzTaxDetailDAO.getFinTaxIncomeList(finMain.getFinID(),
						"LPP");
				if (incomeList != null && !incomeList.isEmpty()) {
					for (FinTaxIncomeDetail detail : incomeList) {
						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(penaltyUnAccrued + DateUtil.format(detail.getValueDate(), "dd/MM/yyyy"));
						soaTranReport.setTransactionDate(detail.getPostDate());
						soaTranReport.setValueDate(detail.getValueDate());
						soaTranReport.setDebitAmount(detail.getReceivedAmount());
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setPriority(27);
						soaTransactionReports.add(soaTranReport);
					}
				}
			}

			if (ImplementationConstants.CUSTOMIZED_SOAREPORT && !finMain.isFinIsActive()
					&& StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_CANCELLED)) {
				getCancelLoanTransactionDetails(finReference, soaTransactionReports, paymentInstructionsList,
						manualAdviseList, finReceiptHeadersList, finFeedetailsList, feeWaiverDetailList,
						PresentmentDetailsList, finSchdDetList, VASRecordingsList, finFeeScheduleDetailsList, finMain,
						maxSchDate, finReceiptDetailsList, finRepayHeadersList, finRepaySchdDetails,
						finReceiptAllocDetails);
			}

		}

		logger.debug("Leaving");
		return soaTransactionReports;
	}

	private SOATransactionReport getlppTrnscEvent(String finODCDue, List<SOATransactionReport> soaTransactionReports,
			FinanceScheduleDetail finSchdDetail, FinOverDueCharges odc, Date valueDate, BigDecimal debitAmount) {
		SOATransactionReport soaTranReport;
		soaTranReport = new SOATransactionReport();
		soaTranReport.setEvent(finODCDue.concat(String.valueOf(finSchdDetail.getInstNumber())));
		soaTranReport.setTransactionDate(odc.getPostDate());
		soaTranReport.setValueDate(valueDate);
		soaTranReport.setCreditAmount(BigDecimal.ZERO);
		soaTranReport.setDebitAmount(debitAmount);
		soaTranReport.setPriority(9);
		soaTransactionReports.add(soaTranReport);
		return soaTranReport;
	}

	// Getting Reversal Transactions for Cancel Loan.
	private void getCancelLoanTransactionDetails(String finReference, List<SOATransactionReport> soaTransactionReports,
			List<PaymentInstruction> paymentInstructionsList, List<ManualAdvise> manualAdviseList,
			List<FinReceiptHeader> finReceiptHeadersList, List<FinFeeDetail> finFeedetailsList,
			List<FeeWaiverDetail> feeWaiverDetailList, List<PresentmentDetail> presentmentDetailsList,
			List<FinanceScheduleDetail> finSchdDetList, List<VASRecording> vasRecordingsList,
			List<FinFeeScheduleDetail> finFeeScheduleDetailsList, FinanceMain finMain, Date maxSchDate,
			List<FinReceiptDetail> finReceiptDetailsList, List<FinRepayHeader> finRepayHeadersList,
			List<RepayScheduleDetail> finRepaySchdDetails, List<ReceiptAllocationDetail> finReceiptAllocDetails)
			throws IllegalAccessException, InvocationTargetException {

		logger.debug(Literal.ENTERING);

		SOATransactionReport soaTranReport = null;
		SOATransactionReport soaTranReportReversal = null;
		String finRef = "";// "(" + finReference + ")";
		String advancePayment = "Amount Paid Vide ";
		String finSchedulePayable = "Amount Financed - Payable ";// Add Disbursement 1
		String finScheduleReceivable = "Amount Financed - Receivable ";// Add Disbursement 1
		String partPrepayment = "Part Prepayment Amount "; // 5
		String brokenPeriodEvent = "Broken Period Interest Receivable "; // 6
		String foreclosureAmount = "Foreclosure Amount "; // 19

		if (CollectionUtils.isNotEmpty(finSchdDetList)) {
			for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {
				String bpiOrHoliday = finSchdDetail.getBpiOrHoliday();
				BigDecimal repayAmount = finSchdDetail.getRepayAmount();

				// Add disbursement
				if (finSchdDetail.isDisbOnSchDate()) {

					BigDecimal transactionAmount = BigDecimal.ZERO;

					if (finSchdDetail.getDisbAmount() != null) {
						transactionAmount = finSchdDetail.getDisbAmount();
					}

					if (DateUtil.compare(finSchdDetail.getSchDate(), finMain.getFinStartDate()) == 0) {
						transactionAmount = transactionAmount.add(finMain.getFeeChargeAmt());
					}

					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(finSchedulePayable + finRef);
					soaTranReport.setTransactionDate(finSchdDetail.getSchDate());
					soaTranReport.setValueDate(finMain.getFinStartDate());
					soaTranReport.setCreditAmount(transactionAmount);
					soaTranReport.setDebitAmount(BigDecimal.ZERO);
					soaTranReport.setPriority(1);

					soaTransactionReports.add(soaTranReport);

					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(finScheduleReceivable + finRef);
					soaTranReport.setTransactionDate(finMain.getClosedDate());
					soaTranReport.setValueDate(finMain.getFinStartDate());
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
					soaTranReport.setDebitAmount(transactionAmount);
					soaTranReport.setPriority(20);

					soaTransactionReports.add(soaTranReport);
				}

				// Broken Period Interest Receivable- Due
				if (StringUtils.equalsIgnoreCase("B", bpiOrHoliday) && repayAmount != null
						&& repayAmount.compareTo(BigDecimal.ZERO) > 0) {

					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(brokenPeriodEvent + finRef);
					soaTranReport.setTransactionDate(finSchdDetail.getSchDate());
					soaTranReport.setValueDate(finSchdDetail.getSchDate());
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
					soaTranReport.setDebitAmount(repayAmount);
					soaTranReport.setPriority(2);
					soaTransactionReports.add(soaTranReport);

					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(brokenPeriodEvent + finRef);
					soaTranReport.setTransactionDate(finMain.getClosedDate());
					soaTranReport.setValueDate(finSchdDetail.getSchDate());
					soaTranReport.setCreditAmount(repayAmount);
					soaTranReport.setDebitAmount(BigDecimal.ZERO);
					soaTranReport.setPriority(21);
					soaTransactionReports.add(soaTranReport);
				}

				// fore closure Amount
				BigDecimal partialPaidAmt = finSchdDetail.getPartialPaidAmt();
				if (maxSchDate != null && DateUtil.compare(maxSchDate, finSchdDetail.getSchDate()) == 0) {
					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(foreclosureAmount + finRef);
					soaTranReport.setTransactionDate(finSchdDetail.getSchDate());
					soaTranReport.setValueDate(finSchdDetail.getSchDate());
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
					soaTranReport.setDebitAmount(repayAmount.subtract(partialPaidAmt));
					soaTranReport.setPriority(3);
					soaTransactionReports.add(soaTranReport);

					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(foreclosureAmount + finRef);
					soaTranReport.setTransactionDate(finMain.getClosedDate());
					soaTranReport.setValueDate(finSchdDetail.getSchDate());
					soaTranReport.setCreditAmount(repayAmount.subtract(partialPaidAmt));
					soaTranReport.setDebitAmount(BigDecimal.ZERO);
					soaTranReport.setPriority(22);
					soaTransactionReports.add(soaTranReport);
				}

				if ((DateUtil.compare(finSchdDetail.getSchDate(), SysParamUtil.getAppDate()) <= 0)) {

					// Partial Prepayment Amount
					if (partialPaidAmt != null && partialPaidAmt.compareTo(BigDecimal.ZERO) > 0) {

						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(partPrepayment + finRef);
						soaTranReport.setTransactionDate(finSchdDetail.getSchDate());
						soaTranReport.setValueDate(finSchdDetail.getSchDate());
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setDebitAmount(partialPaidAmt);
						soaTranReport.setPriority(4);
						soaTransactionReports.add(soaTranReport);

						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(partPrepayment + finRef);
						soaTranReport.setTransactionDate(finSchdDetail.getSchDate());
						soaTranReport.setValueDate(finSchdDetail.getSchDate());
						soaTranReport.setCreditAmount(partialPaidAmt);
						soaTranReport.setDebitAmount(BigDecimal.ZERO);
						soaTranReport.setPriority(23);
						soaTransactionReports.add(soaTranReport);
					}
				}
				if (finSchdDetail.getInstNumber() == finMain.getFixedRateTenor()) {
					fixedEndDate = null;
					fixedEndDate = finSchdDetail.getSchDate();
				}
			}
		}

		// Finance Advance Payment Details
		List<FinAdvancePayments> finAdvancePaymentsList = soaReportGenerationDAO
				.getFinAdvPaymentsForCancelLoan(finReference);
		if (CollectionUtils.isNotEmpty(finAdvancePaymentsList)) {
			for (FinAdvancePayments finAdvancePayments : finAdvancePaymentsList) {
				advancePayment = "Amount Paid Vide ";
				String status = "";

				soaTranReport = new SOATransactionReport();
				soaTranReportReversal = new SOATransactionReport();
				String paymentType = finAdvancePayments.getPaymentType();
				if (StringUtils.isNotBlank(paymentType)) {
					advancePayment = advancePayment.concat(paymentType + ":");
				}
				if (StringUtils.equals(paymentType, DisbursementConstants.PAYMENT_TYPE_CHEQUE)
						|| StringUtils.equals(paymentType, DisbursementConstants.PAYMENT_TYPE_DD)) {
					String llReferenceNo = finAdvancePayments.getLlReferenceNo();
					if (StringUtils.isNotBlank(llReferenceNo)) {
						advancePayment = advancePayment.concat(llReferenceNo);
					}
					soaTranReport.setValueDate(finAdvancePayments.getValueDate());
					soaTranReportReversal.setValueDate(finAdvancePayments.getValueDate());
				} else {
					String transactionRef = finAdvancePayments.getTransactionRef();
					if (StringUtils.isNotBlank(transactionRef)) {
						advancePayment = advancePayment.concat(transactionRef);
					}
					soaTranReport.setValueDate(finAdvancePayments.getLlDate());
					soaTranReportReversal.setValueDate(finAdvancePayments.getLlDate());
				}
				soaTranReport.setEvent(advancePayment + finRef + status);
				soaTranReport.setTransactionDate(finAdvancePayments.getLlDate());
				soaTranReport.setCreditAmount(BigDecimal.ZERO);
				soaTranReport.setDebitAmount(finAdvancePayments.getAmtToBeReleased());
				soaTranReport.setPriority(6);

				soaTranReportReversal.setEvent(advancePayment + finRef + status);
				soaTranReportReversal.setTransactionDate(finMain.getClosedDate());
				soaTranReportReversal.setCreditAmount(finAdvancePayments.getAmtToBeReleased());
				soaTranReportReversal.setDebitAmount(BigDecimal.ZERO);
				soaTranReportReversal.setPriority(25);

				soaTransactionReports.add(soaTranReport);
				soaTransactionReports.add(soaTranReportReversal);
			}
		}

		if (CollectionUtils.isNotEmpty(finFeedetailsList)) {

			for (FinFeeDetail finFeeDetail : finFeedetailsList) {
				String finFeeDetailOrgination = " Amount";
				String finFeeDetailNotInDISBorPOSP = " Amount";
				String vasProduct = null;
				if (CollectionUtils.isNotEmpty(vasRecordingsList)) {
					for (VASRecording vASRecording : vasRecordingsList) {
						if (StringUtils.equals(finReference, vASRecording.getPrimaryLinkRef())
								&& StringUtils.equals(finFeeDetail.getVasReference(), vASRecording.getVasReference())) {
							vasProduct = vASRecording.getProductDesc();
						}
					}
				}
				BigDecimal debitAmount = BigDecimal.ZERO;
				BigDecimal waivedAmount = BigDecimal.ZERO;
				// Fee/Vas - Due
				BigDecimal paidAmount = finFeeDetail.getPaidAmount();
				String feeTypeDesc = finFeeDetail.getFeeTypeDesc();
				BigDecimal remainingFeeAmount = finFeeDetail.getRemainingFee();

				if (CalculationConstants.REMFEE_PART_OF_DISBURSE.equals(finFeeDetail.getFeeScheduleMethod())
						|| CalculationConstants.REMFEE_PART_OF_SALE_PRICE.equals(finFeeDetail.getFeeScheduleMethod())) {

					// if (finFeeDetail.isOriginationFee()) {} Removed this as part of Advance Interest/Advance EMI
					// Changes.

					if (finFeeDetail.getRemainingFee() != null) {
						debitAmount = finFeeDetail.getRemainingFee();
					}

					if (paidAmount != null) {
						debitAmount = debitAmount.add(paidAmount);
					}

					if (debitAmount.compareTo(BigDecimal.ZERO) > 0) {

						soaTranReport = new SOATransactionReport();
						if (StringUtils.isNotBlank(feeTypeDesc)) {

							finFeeDetailOrgination = feeTypeDesc + " Amount";
							String taxComponent = finFeeDetail.getTaxComponent();
							if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
								finFeeDetailOrgination = finFeeDetailOrgination + inclusive;
							} else if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
								finFeeDetailOrgination = finFeeDetailOrgination + exclusive;
							}
						} else {
							finFeeDetailOrgination = vasProduct + " Amount";
						}
						soaTranReport.setEvent(finFeeDetailOrgination + finRef);
						soaTranReport.setTransactionDate(finMain.getFinApprovedDate());
						soaTranReport.setValueDate(finMain.getFinStartDate());
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setDebitAmount(debitAmount);
						soaTranReport.setPriority(8);
						soaTransactionReports.add(soaTranReport);

						if (remainingFeeAmount.compareTo(BigDecimal.ZERO) > 0) {
							soaTranReport = new SOATransactionReport();
							soaTranReport.setEvent(finFeeDetailOrgination + finRef);
							soaTranReport.setTransactionDate(finMain.getClosedDate());
							soaTranReport.setValueDate(finMain.getFinStartDate());
							soaTranReport.setCreditAmount(debitAmount);
							soaTranReport.setDebitAmount(BigDecimal.ZERO);
							soaTranReport.setPriority(27);
						}

						soaTransactionReports.add(soaTranReport);
					}

				} else {
					if (paidAmount != null && paidAmount.compareTo(BigDecimal.ZERO) > 0) {
						soaTranReport = new SOATransactionReport();
						if (StringUtils.isNotBlank(feeTypeDesc)) {
							finFeeDetailNotInDISBorPOSP = feeTypeDesc;
						} else {
							finFeeDetailNotInDISBorPOSP = vasProduct;
						}
						soaTranReport.setEvent(finFeeDetailNotInDISBorPOSP + " Amount" + finRef);
						soaTranReport.setTransactionDate(finFeeDetail.getPostDate());
						soaTranReport.setValueDate(finFeeDetail.getPostDate());
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setDebitAmount(paidAmount);
						soaTranReport.setPriority(9);
						soaTransactionReports.add(soaTranReport);

						soaTranReport.setEvent(finFeeDetailNotInDISBorPOSP + " Amount" + finRef);
						soaTranReport.setTransactionDate(finMain.getClosedDate());
						soaTranReport.setValueDate(finFeeDetail.getPostDate());
						soaTranReport.setCreditAmount(paidAmount);
						soaTranReport.setDebitAmount(BigDecimal.ZERO);
						soaTranReport.setPriority(28);

						soaTransactionReports.add(soaTranReport);
					}
				}
				// Waived amount for Fee/Vas

				if (finFeeDetail.getWaivedAmount() != null) {
					waivedAmount = waivedAmount.add(finFeeDetail.getWaivedAmount());
				}
				if (finFeeDetail.getWaivedAmount() != null && waivedAmount.compareTo(BigDecimal.ZERO) > 0) {
					soaTranReport = new SOATransactionReport();
					if (StringUtils.isNotBlank(finFeeDetail.getFeeTypeDesc())) {
						finFeeDetailOrgination = finFeeDetail.getFeeTypeDesc();
					} else {
						finFeeDetailOrgination = vasProduct;
					}
					soaTranReport.setEvent(finFeeDetailOrgination + " from customer Waived Off" + finRef);
					soaTranReport.setTransactionDate(finFeeDetail.getPostDate());
					soaTranReport.setValueDate(finFeeDetail.getPostDate());
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
					soaTranReport.setDebitAmount(waivedAmount);
					soaTranReport.setPriority(10);
					soaTransactionReports.add(soaTranReport);

					soaTranReport = new SOATransactionReport();

					soaTranReport.setEvent(finFeeDetailOrgination + " from customer Waived Off" + finRef);
					soaTranReport.setTransactionDate(finMain.getClosedDate());
					soaTranReport.setValueDate(finFeeDetail.getPostDate());
					soaTranReport.setCreditAmount(waivedAmount);
					soaTranReport.setDebitAmount(BigDecimal.ZERO);
					soaTranReport.setPriority(29);

					soaTransactionReports.add(soaTranReport);
				}
			}

			// Fin Fee Schedule Details List
			if (CollectionUtils.isNotEmpty(finFeeScheduleDetailsList)) {
				String finFeeDetailEvent = "- Due ";
				for (FinFeeScheduleDetail finFeeScheduleDetail : finFeeScheduleDetailsList) {
					soaTranReport = new SOATransactionReport();
					String feeTypeDesc = StringUtils.trimToEmpty(finFeeScheduleDetail.getFeeTypeDesc());
					soaTranReport.setEvent(feeTypeDesc + finFeeDetailEvent + finRef);
					soaTranReport.setTransactionDate(finFeeScheduleDetail.getSchDate());
					soaTranReport.setValueDate(finFeeScheduleDetail.getSchDate());
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
					soaTranReport.setDebitAmount(finFeeScheduleDetail.getSchAmount());
					soaTranReport.setPriority(11);
					soaTransactionReports.add(soaTranReport);

					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(feeTypeDesc + finFeeDetailEvent + finRef);
					soaTranReport.setTransactionDate(finMain.getClosedDate());
					soaTranReport.setValueDate(finFeeScheduleDetail.getSchDate());
					soaTranReport.setCreditAmount(finFeeScheduleDetail.getSchAmount());
					soaTranReport.setDebitAmount(BigDecimal.ZERO);
					soaTranReport.setPriority(30);
					soaTransactionReports.add(soaTranReport);
				}
			}
		}

		// LPP and LPI waived from the waiver screen
		if (CollectionUtils.isNotEmpty(feeWaiverDetailList)) {
			String lppWaived = "Penalty from customer Waived Off ";
			String lpiIWaived = "Penalty Interest from customer Waived Off ";
			for (FeeWaiverDetail waiverDetail : feeWaiverDetailList) {
				String label = "";
				if (Allocation.ODC.equalsIgnoreCase(waiverDetail.getFeeTypeCode())) {
					label = lppWaived;
				} else if (Allocation.LPFT.equalsIgnoreCase(waiverDetail.getFeeTypeCode())) {
					label = lpiIWaived;
				}

				if (StringUtils.isNotBlank(label)) {
					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(label + finRef);
					soaTranReport.setTransactionDate(waiverDetail.getValueDate());
					soaTranReport.setValueDate(waiverDetail.getValueDate());
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
					soaTranReport.setDebitAmount(waiverDetail.getCurrWaiverAmount());
					soaTranReport.setPriority(12);
					soaTransactionReports.add(soaTranReport);

					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(label + finRef);
					soaTranReport.setTransactionDate(finMain.getClosedDate());
					soaTranReport.setValueDate(waiverDetail.getValueDate());
					soaTranReport.setCreditAmount(waiverDetail.getCurrWaiverAmount());
					soaTranReport.setDebitAmount(BigDecimal.ZERO);
					soaTranReport.setPriority(31);
					soaTransactionReports.add(soaTranReport);

				}
			}
		}

		// Manual Advise
		FeeType bounceFeeType = null;
		String manualAdvFeeType = "- Payable"; // 12
		String manualAdvPrentmentNotIn = "FeeDesc or Bounce - Due "; // 10
		String manualAdvPrentmentIn = " "; // 11
		List<Long> presentmentReceiptIds = new ArrayList<Long>();

		if (CollectionUtils.isNotEmpty(manualAdviseList)) {
			for (ManualAdvise manualAdvise : manualAdviseList) {
				if ((manualAdvise.getFeeTypeID() != 0 && manualAdvise.getFeeTypeID() != Long.MIN_VALUE)
						&& StringUtils.isNotBlank(manualAdvise.getFeeTypeDesc()) && manualAdvise.getAdviseType() == 2
						&& manualAdvise.getAdviseAmount().compareTo(BigDecimal.ZERO) > 0) {
					soaTranReport = new SOATransactionReport();

					soaTranReport.setEvent(manualAdvise.getFeeTypeDesc() + manualAdvFeeType + finRef);
					soaTranReport.setTransactionDate(finMain.getClosedDate());
					soaTranReport.setValueDate(manualAdvise.getValueDate());

					// 03-09-18:GST Amount For Payable advices is not reflecting in SOA
					BigDecimal gstAmount = BigDecimal.ZERO;
					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(manualAdvise.getTaxComponent())) {
						gstAmount = GSTCalculator.getTotalGST(finReference, manualAdvise.getAdviseAmount(),
								manualAdvise.getTaxComponent());
					}

					soaTranReport.setCreditAmount(manualAdvise.getAdviseAmount().add(gstAmount));
					soaTranReport.setDebitAmount(BigDecimal.ZERO);
					soaTranReport.setPriority(32);

					soaTransactionReports.add(soaTranReport);
				}
				// Bounce/Fee - Due
				BigDecimal remAdviseAmt = manualAdvise.getAdviseAmount().subtract(manualAdvise.getPaidAmount())
						.subtract(manualAdvise.getWaivedAmount());
				if (manualAdvise.getAdviseType() != 2 && remAdviseAmt.compareTo(BigDecimal.ZERO) > 0) {

					if (!presentmentReceiptIds.contains(manualAdvise.getReceiptID())) {

						soaTranReport = new SOATransactionReport();
						String taxComponent = "";
						BigDecimal gstAmount = BigDecimal.ZERO;

						if (manualAdvise.getFeeTypeID() > 0) {

							manualAdvPrentmentNotIn = manualAdvise.getFeeTypeDesc() + " - Due";
							taxComponent = manualAdvise.getTaxComponent();

							if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
								manualAdvPrentmentNotIn = manualAdvPrentmentNotIn + inclusive;
							} else if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
								manualAdvPrentmentNotIn = manualAdvPrentmentNotIn + exclusive;
							}
						} else {
							if (bounceFeeType == null) {
								bounceFeeType = getFeeTypeDAO().getTaxDetailByCode(Allocation.BOUNCE);
							}
							if (bounceFeeType != null) {
								taxComponent = bounceFeeType.getTaxComponent();
							}
							manualAdvPrentmentNotIn = "Bounce - Due";
						}

						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
							gstAmount = GSTCalculator.getTotalGST(finReference, remAdviseAmt, taxComponent);
						}

						soaTranReport.setEvent(manualAdvPrentmentNotIn + finRef);
						soaTranReport.setTransactionDate(finMain.getClosedDate());
						soaTranReport.setValueDate(manualAdvise.getValueDate());
						soaTranReport.setCreditAmount(remAdviseAmt.add(gstAmount));
						soaTranReport.setDebitAmount(BigDecimal.ZERO);
						soaTranReport.setPriority(33);
						soaTransactionReports.add(soaTranReport);

					} else {
						// Bounce created for particular on Installment
						if (manualAdvise.getFeeTypeID() == 0) {
							String taxComponent = "";
							BigDecimal gstAmount = BigDecimal.ZERO;
							taxComponent = manualAdvise.getTaxComponent();

							for (PresentmentDetail presentmentDetail : presentmentDetailsList) {

								if (manualAdvise.getReceiptID() == presentmentDetail.getReceiptID()) {

									for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {

										if (DateUtil.compare(presentmentDetail.getSchDate(),
												finSchdDetail.getSchDate()) == 0) {
											manualAdvPrentmentIn = presentmentDetail.getBounceReason() != null
													? "Bounce Created for " + "'" + presentmentDetail.getBounceReason()
															+ "'" + " on Installment:"
													: "Bounce Created for " + "'"
															+ StringUtils.trimToEmpty(manualAdvise.getBounceCodeDesc())
															+ "'" + " on Installment:";
											soaTranReport = new SOATransactionReport();
											if (finSchdDetail.getInstNumber() > 0) {
												manualAdvPrentmentIn = manualAdvPrentmentIn
														.concat(String.valueOf(finSchdDetail.getInstNumber()));
											}
											manualAdvPrentmentIn = manualAdvPrentmentIn.concat(finRef);
											soaTranReport.setEvent(manualAdvPrentmentIn);
											soaTranReport.setTransactionDate(manualAdvise.getPostDate());
											soaTranReport.setValueDate(manualAdvise.getValueDate());
											soaTranReport.setDebitAmount(BigDecimal.ZERO);

											if (bounceFeeType == null) {
												bounceFeeType = getFeeTypeDAO().getTaxDetailByCode(Allocation.BOUNCE);
											}
											if (bounceFeeType != null) {
												taxComponent = bounceFeeType.getTaxComponent();
											}
											if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
												gstAmount = GSTCalculator.getTotalGST(finReference, remAdviseAmt,
														taxComponent);

											}
											soaTranReport.setCreditAmount(remAdviseAmt.add(gstAmount));
											soaTranReport.setPriority(34);

											soaTransactionReports.add(soaTranReport);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// UpfrontFee Refund
		List<FinFeeRefundHeader> finFeeRefundHeaderList = getFinFeeRefundHeader(finReference);
		if (CollectionUtils.isNotEmpty(finFeeRefundHeaderList)) {
			List<FinFeeRefundDetails> finFeeRefundDetailList = getFinFeeRefundDetails(finReference);
			if (CollectionUtils.isNotEmpty(finFeeRefundDetailList)) {
				for (FinFeeRefundHeader finFeeRefundHeader : finFeeRefundHeaderList) {

					for (FinFeeRefundDetails finFeeRefundDetail : finFeeRefundDetailList) {
						if (finFeeRefundHeader.getHeaderId() == finFeeRefundDetail.getHeaderId()) {
							if (finFeeRefundDetail.getRefundAmount().compareTo(BigDecimal.ZERO) > 0) {
								soaTranReport = new SOATransactionReport();
								soaTranReport.setEvent(StringUtils.trimToEmpty(finFeeRefundDetail.getFeeTypeCode())
										+ " Amount Refunded");
								soaTranReport.setTransactionDate(finMain.getClosedDate());
								soaTranReport.setValueDate(finMain.getClosedDate());
								soaTranReport.setCreditAmount(finFeeRefundDetail.getRefundAmount());
								soaTranReport.setDebitAmount(BigDecimal.ZERO);
								soaTranReport.setPriority(35);
								soaTransactionReports.add(soaTranReport);

							}
						}
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private Date getTransactionDate(FinAdvancePayments finAdvancePayments) {
		Date transactionDate = finAdvancePayments.getLlDate();

		if (transactionDate == null) {
			transactionDate = finAdvancePayments.getLastMntOn();
		}

		if (transactionDate == null) {
			transactionDate = finAdvancePayments.getValueDate();
		}

		return transactionDate;
	}

	@Override
	public EventProperties getEventPropertiesList(String configName) {
		return this.soaReportGenerationDAO.getEventPropertiesList(configName);
	}

	@Override
	public List<String> getSOAFinTypes() {

		return soaReportGenerationDAO.getSOAFinTypes();
	}

	private String buildAllocationData(List<ReceiptAllocationDetail> radList, int formatter) {
		StringBuilder data = new StringBuilder();
		Map<String, BigDecimal> allocMap = new LinkedHashMap<String, BigDecimal>();
		if (CollectionUtils.isNotEmpty(radList)) {
			for (ReceiptAllocationDetail rad : radList) {
				if (rad.getPaidAmount().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}
				String allocType = rad.getAllocationType();
				String allocTypeMsg = "";
				BigDecimal paidAmount = rad.getPaidAmount();
				if (Allocation.EMI.equals(allocType)) {
					allocTypeMsg = "EMI Adjusted";
				} else if (Allocation.MANADV.equals(allocType)) {
					allocTypeMsg = rad.getTypeDesc();
				} else if (Allocation.BOUNCE.equals(allocType)) {
					allocTypeMsg = "Bounce Charges";
				} else if (Allocation.FUT_PRI.equals(allocType)) {
					allocTypeMsg = "Principal";
				} else if (Allocation.FUT_NPFT.equals(allocType)) {
					allocTypeMsg = "Interest";
				} else if (Allocation.PP.equals(allocType)) {
					allocTypeMsg = "Principal";
				} else if (Allocation.FEE.equals(allocType)) {
					allocTypeMsg = "Fees";
				} else if (Allocation.ODC.equals(allocType)) {
					allocTypeMsg = "Late Pay Penalty";
				} else if (Allocation.LPFT.equals(allocType)) {
					allocTypeMsg = "Late Pay Interest";
				}
				if (StringUtils.isNotEmpty(allocTypeMsg)) {
					if (allocMap.containsKey(allocTypeMsg)) {
						paidAmount = paidAmount.add(allocMap.get(allocTypeMsg));
					}
					allocMap.put(allocTypeMsg, paidAmount);
				}

			}

		}
		data = data.append("\n");
		for (Map.Entry<String, BigDecimal> entry : allocMap.entrySet()) {
			data = data.append(entry.getKey()).append(" : ")
					.append(PennantApplicationUtil.formateAmount(entry.getValue(), formatter));
			data = data.append("\n");
		}
		return data.toString();
	}

	private String buildAllocationDataForRestructure(List<RestructureCharge> rstChrgs, int formatter) {
		StringBuilder data = new StringBuilder();

		Map<String, BigDecimal> allocMap = new LinkedHashMap<>();

		if (rstChrgs == null) {
			rstChrgs = new ArrayList<>();
		}

		for (RestructureCharge rstChrg : rstChrgs) {
			if (rstChrg.getActualAmount().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			String allocType = rstChrg.getAlocType();
			String allocTypeMsg = "";
			BigDecimal capitalizedAmount = rstChrg.getTotalAmount();

			switch (allocType) {
			case Allocation.EMI:
				allocTypeMsg = "EMI Capitalized";
				break;
			case Allocation.MANADV:
				FeeType feeType = feeTypeDAO.getTaxDetailByCode(rstChrg.getFeeCode());
				if (feeType != null) {
					allocTypeMsg = feeType.getFeeTypeDesc().concat(" Capitalized");
				}
				break;
			case Allocation.BOUNCE:
				allocTypeMsg = "Bounce Charges Capitalized";
				break;
			case Allocation.PRI:
				allocTypeMsg = "Principal Capitalized";
				break;
			case Allocation.PFT:
				allocTypeMsg = "Interest Capitalized";
				break;
			case Allocation.ODC:
				allocTypeMsg = "Late Pay Penalty Capitalized";
				break;
			case Allocation.LPFT:
				allocTypeMsg = "Late Pay Interest Capitalized";
				break;
			default:
				break;
			}

			if (!allocTypeMsg.isEmpty()) {
				if (allocMap.containsKey(allocTypeMsg)) {
					capitalizedAmount = capitalizedAmount.add(allocMap.get(allocTypeMsg));
				}
				allocMap.put(allocTypeMsg, capitalizedAmount);
			}
		}

		data = data.append("\n");
		for (Map.Entry<String, BigDecimal> entry : allocMap.entrySet()) {
			data = data.append(entry.getKey()).append(" : ")
					.append(PennantApplicationUtil.formateAmount(entry.getValue(), formatter));
			data = data.append("\n");
		}

		return data.toString();
	}

	private List<FinExcessAmount> groupByAmountType(List<FinExcessAmount> finExcessAmountsList) {
		List<FinExcessAmount> list = new ArrayList<>();

		Map<String, List<FinExcessAmount>> map = new HashMap<>();

		for (FinExcessAmount ea : finExcessAmountsList) {
			String amountType = ea.getAmountType();

			List<FinExcessAmount> list2 = map.get(amountType);
			if (list2 == null) {
				list2 = new ArrayList<>();
				map.put(amountType, list2);
			}

			list2.add(ea);
		}

		for (Entry<String, List<FinExcessAmount>> finExcessAmount : map.entrySet()) {
			String amountType = finExcessAmount.getKey();
			BigDecimal amount = BigDecimal.ZERO;
			BigDecimal balanceAmt = BigDecimal.ZERO;

			for (FinExcessAmount ea : finExcessAmount.getValue()) {
				amount = amount.add(ea.getAmount());
				balanceAmt = balanceAmt.add(ea.getBalanceAmt());
			}

			if (balanceAmt.compareTo(BigDecimal.ZERO) > 0) {
				FinExcessAmount fa = new FinExcessAmount();
				fa.setAmountType(amountType);
				fa.setAmount(amount);
				fa.setBalanceAmt(balanceAmt);
				list.add(fa);
			}

		}

		return list;
	}

	public List<FinOverDueCharges> getFinODCAmtByRef(long finID) {
		return finODCAmountDAO.getFinODCAmtByRef(finID, RepayConstants.FEE_TYPE_LPP);
	}

	private SOATransactionReport addInstallment(FinanceScheduleDetail schedule, BigDecimal amount, String allocation) {
		SOATransactionReport soa = new SOATransactionReport();
		StringBuilder event = new StringBuilder();
		event.append("Due for Installment ");
		event.append(schedule.getInstNumber());
		event.append(allocation);

		soa.setEvent(event.toString());
		soa.setTransactionDate(schedule.getSchDate());
		soa.setValueDate(schedule.getSchDate());
		soa.setCreditAmount(BigDecimal.ZERO);
		soa.setDebitAmount(amount);
		soa.setPriority(3);

		return soa;
	}

	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public FeeTypeDAO getFeeTypeDAO() {
		return feeTypeDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public FinODAmzTaxDetailDAO getFinODAmzTaxDetailDAO() {
		return finODAmzTaxDetailDAO;
	}

	public void setFinODAmzTaxDetailDAO(FinODAmzTaxDetailDAO finODAmzTaxDetailDAO) {
		this.finODAmzTaxDetailDAO = finODAmzTaxDetailDAO;
	}

	public PromotionDAO getPromotionDAO() {
		return promotionDAO;
	}

	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	public TaxHeaderDetailsDAO getTaxHeaderDetailsDAO() {
		return taxHeaderDetailsDAO;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

	public FeeWaiverDetailDAO getFeeWaiverDetailDAO() {
		return feeWaiverDetailDAO;
	}

	public void setFeeWaiverDetailDAO(FeeWaiverDetailDAO feeWaiverDetailDAO) {
		this.feeWaiverDetailDAO = feeWaiverDetailDAO;
	}

	public void setLinkedFinancesService(LinkedFinancesService linkedFinancesService) {
		this.linkedFinancesService = linkedFinancesService;
	}

	@Autowired
	public void setFinODCAmountDAO(FinODCAmountDAO finODCAmountDAO) {
		this.finODCAmountDAO = finODCAmountDAO;
	}

}