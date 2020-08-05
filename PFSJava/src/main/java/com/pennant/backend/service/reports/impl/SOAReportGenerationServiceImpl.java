/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		: SOAReportGenerationServiceImpl.java							        *                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  5-09-2012															*
 *                                                                  
 * Modified Date    :  5-09-2012														    *
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 5-09-2012	       Pennant	                 0.1                                        * 
 * 24-05-2018          Srikanth                  0.2           Merge the Code From Bajaj To Core                                                                                        * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.google.common.collect.ComparisonChain;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.reports.SOAReportGenerationDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.systemmasters.ApplicantDetail;
import com.pennant.backend.model.systemmasters.InterestRateDetail;
import com.pennant.backend.model.systemmasters.OtherFinanceDetail;
import com.pennant.backend.model.systemmasters.SOASummaryReport;
import com.pennant.backend.model.systemmasters.SOATransactionReport;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.reports.SOAReportGenerationService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.soa.SOAReportService;

public class SOAReportGenerationServiceImpl extends GenericService<StatementOfAccount>
		implements SOAReportGenerationService {
	private static Logger logger = Logger.getLogger(SOAReportGenerationServiceImpl.class);

	private static final String inclusive = "*";
	private static final String exclusive = "^";

	private SOAReportGenerationDAO soaReportGenerationDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private CustomerDetailsService customerDetailsService;
	private RuleDAO ruleDAO;
	private FinFeeDetailService finFeeDetailService;
	private RuleExecutionUtil ruleExecutionUtil;
	private FeeTypeDAO feeTypeDAO;
	private FinODAmzTaxDetailDAO finODAmzTaxDetailDAO;
	private PromotionDAO promotionDAO;
	@Autowired(required = false)
	private SOAReportService soaReportService;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;

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

	private List<ManualAdvise> getManualAdvise(String finReference) {
		return this.soaReportGenerationDAO.getManualAdvise(finReference);
	}

	private List<ManualAdviseMovements> getManualAdviseMovements(String finReference) {
		List<ManualAdviseMovements> list = soaReportGenerationDAO.getManualAdviseMovements(finReference);
		if (CollectionUtils.isEmpty(list)) {
			return list;
		}

		for (ManualAdviseMovements ma : list) {
			Long taxHeaderId = ma.getTaxHeaderId();

			if (taxHeaderId != null && taxHeaderId > 0) {
				TaxHeader taxHeader = taxHeaderDetailsDAO.getTaxHeaderDetailsById(taxHeaderId, "");
				if (taxHeader != null) {
					List<Taxes> taxDetails = taxHeaderDetailsDAO.getTaxDetailById(taxHeader.getHeaderId(), "");
					taxHeader.setTaxDetails(taxDetails);
					ma.setTaxHeader(taxHeader);
				}
			}
		}

		return list;
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
	
	private Map<Long,List<ReceiptAllocationDetail>> getReceiptAllocationDetailMap(String finReference){
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

	@SuppressWarnings("deprecation")
	@Override
	public StatementOfAccount getStatmentofAccountDetails(String finReference, Date startDate, Date endDate,
			boolean isAPI) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		long custId = 0;
		List<ApplicantDetail> applicantDetails = null;
		List<OtherFinanceDetail> otherFinanceDetails = null;
		//get the Loan Basic Details
		StatementOfAccount statementOfAccount = getSOALoanDetails(finReference);
		int ccyEditField = 0;

		if (isAPI) {
			ccyEditField = 0;
		} else {
			ccyEditField = statementOfAccount.getCcyEditField();
		}

		//get the FinProfitDeatails
		FinanceProfitDetail financeProfitDetail = getFinanceProfitDetails(finReference);
		List<FinExcessAmount> finExcessAmountsList = getFinExcessAmountsList(finReference);
		//get the finance basic details
		FinanceMain finMain = getFinanceMain(finReference);

		if (financeProfitDetail != null) {

			custId = financeProfitDetail.getCustId();
			int activeCount = getFinanceProfitDetailActiveCount(custId, true);
			int closeCount = getFinanceProfitDetailActiveCount(custId, false);

			statementOfAccount.setCustID(custId);
			statementOfAccount.setActiveCnt(activeCount);
			statementOfAccount.setCloseCnt(closeCount);
			statementOfAccount.setTot(activeCount + closeCount);
			statementOfAccount.setFinStartDate(financeProfitDetail.getFinStartDate());
			statementOfAccount.setLinkedFinRef(financeProfitDetail.getLinkedFinRef());
			statementOfAccount.setClosedlinkedFinRef(financeProfitDetail.getClosedlinkedFinRef());

			//BFSD Related
			statementOfAccount.setFinPurpose(financeProfitDetail.getFinPurpose());
			statementOfAccount.setCurrentDate(DateUtility.getAppDate());
			statementOfAccount.setMaturityDate(financeProfitDetail.getMaturityDate());
			statementOfAccount.setNoPaidInst(financeProfitDetail.getNOPaidInst());
			statementOfAccount.setTotalPriPaid(
					PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPriPaid(), ccyEditField));
			statementOfAccount.setTotalPftPaid(
					PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPftPaid(), ccyEditField));
			BigDecimal paidTotal = financeProfitDetail.getTotalPriPaid().add(financeProfitDetail.getTotalPftPaid());
			statementOfAccount.setPaidTotal(PennantApplicationUtil.formateAmount(paidTotal, ccyEditField));
			statementOfAccount.setTotalPriBal(
					PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPriBal(), ccyEditField));
			statementOfAccount.setTotalPftBal(
					PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPftBal(), ccyEditField));
			BigDecimal totalOutStanding = financeProfitDetail.getTotalPriBal()
					.add(financeProfitDetail.getTotalPftBal());
			statementOfAccount
					.setTotalOutStanding(PennantApplicationUtil.formateAmount(totalOutStanding, ccyEditField));
			int futureInst = financeProfitDetail.getNOInst() - financeProfitDetail.getNOPaidInst();
			statementOfAccount.setNoOfOutStandInst(futureInst);
			statementOfAccount.setFinCurrAssetValue(
					PennantApplicationUtil.formateAmount(finMain.getFinCurrAssetValue(), ccyEditField));
			//Next Installment Amount
			statementOfAccount.setNextRpyDate(financeProfitDetail.getNSchdDate());
			statementOfAccount.setNextRpyPri(
					PennantApplicationUtil.formateAmount(financeProfitDetail.getNSchdPri(), ccyEditField));
			statementOfAccount.setNextRpyPft(
					PennantApplicationUtil.formateAmount(financeProfitDetail.getNSchdPft(), ccyEditField));

			//Rate Code will be displayed when Referential Rate is selected against the loan
			String plrRate = statementOfAccount.getPlrRate();
			if (StringUtils.isEmpty(plrRate)) {
				statementOfAccount.setPlrRate("-");
			} else {
				statementOfAccount.setPlrRate(plrRate + "/" + finMain.getRepayMargin());
			}

			if (AdvanceType.hasAdvEMI(finMain.getAdvType())) {
				statementOfAccount.setAdvEmiApplicable(true);
			}
			//Added RepaymentFrequence		
			if (statementOfAccount.getRepayFrq() != null && statementOfAccount.getRepayFrq() != "") {
				statementOfAccount.setRepayFrq(FrequencyUtil.getRepayFrequencyLabel(statementOfAccount.getRepayFrq()));
			}

			//Including advance EMI terms
			int tenure = statementOfAccount.getTenure();

			// Based on Repay Frequency codes it will set
			String frequency = FrequencyUtil.getFrequencyCode(finMain.getRepayFrq());
			statementOfAccount.setTenureLabel(FrequencyUtil.getRepayFrequencyLabel(frequency));
			statementOfAccount.setTenure(tenure);

			//FinExcess Amount
			statementOfAccount.setAdvInstAmt("0.00 / 0");
			statementOfAccount.setAdvEMIAmt("0.00 / 0.00");
			statementOfAccount.setAdvIntAmt("0.00 / 0.00");
			statementOfAccount.setCashCollAmt("0.00 / 0.00");
			statementOfAccount.setDsraAmt("0.00 / 0.00");

			// Advance EMI Installments
			statementOfAccount.setAdvInstAmt(PennantApplicationUtil.amountFormate(finMain.getAdvanceEMI(), ccyEditField)
					+ " / " + finMain.getAdvTerms());

			if (CollectionUtils.isNotEmpty(finExcessAmountsList)) {
				for (FinExcessAmount finExcessAmount : finExcessAmountsList) {
					String balanceAmt = PennantApplicationUtil.amountFormate(finExcessAmount.getBalanceAmt(),
							ccyEditField);
					String actualAmt = PennantApplicationUtil.amountFormate(finExcessAmount.getAmount(), ccyEditField);
					if (AdvanceRuleCode.ADVEMI.name().equals(finExcessAmount.getAmountType())) {
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
				statementOfAccount.setSvamount(
						PennantApplicationUtil.formateAmount(statementOfAccount.getSvamount(), ccyEditField));
				statementOfAccount.setAdvInstAmt(
						PennantApplicationUtil.formateAmount(finMain.getDownPayment(), ccyEditField).toString());
				if (!finMain.getPromotionCode().isEmpty()) {
					Promotion promotions = promotionDAO.getPromotionByReferenceId(finMain.getPromotionSeqId(),
							"_AView");
					statementOfAccount.setTenure(promotions.getTenor());
					statementOfAccount.setAdvEmiTerms(promotions.getAdvEMITerms());
				}

			}

			//get the Customer Details
			StatementOfAccount statementOfAccountCustDetails = getSOACustomerDetails(custId);

			//Co-Applicant/Borrower Details
			applicantDetails = getApplicantDetails(finReference);

			//Other Finance Details
			otherFinanceDetails = getCustOtherFinDetails(custId, finReference);

			if (statementOfAccountCustDetails != null) {
				statementOfAccount.setCustShrtName(
						StringUtils.capitaliseAllWords(statementOfAccountCustDetails.getCustShrtName()));
				statementOfAccount.setCustCIF(statementOfAccountCustDetails.getCustCIF());
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

			//to get the FinType and FinBranch
			StatementOfAccount statementOfAccountProductDetails = getSOAProductDetails(
					financeProfitDetail.getFinBranch(), financeProfitDetail.getFinType());

			if (statementOfAccountProductDetails != null) {
				statementOfAccount.setFinType(statementOfAccountProductDetails.getFinType());
				statementOfAccount.setFinBranch(statementOfAccountProductDetails.getFinBranch());
			}
		}

		BigDecimal ccyMinorCcyUnits = statementOfAccount.getCcyMinorCcyUnits();
		if (startDate == null) {
			startDate = financeProfitDetail.getFinStartDate();
		}
		if (endDate == null) {
			endDate = DateUtility.getAppDate();
		} else { //endDate should be grater than app date then set to the Application date
			if (DateUtility.compare(endDate, DateUtility.getAppDate()) > 0) {
				endDate = DateUtility.getAppDate();
			}
		}
		statementOfAccount.setStartDate(startDate);
		statementOfAccount.setEndDate(endDate);

		//Formatting the amounts
		statementOfAccount
				.setLoanAmount(PennantApplicationUtil.formateAmount(statementOfAccount.getLoanAmount(), ccyEditField));
		statementOfAccount.setPreferredCardLimit(
				PennantApplicationUtil.formateAmount(statementOfAccount.getPreferredCardLimit(), ccyEditField));
		statementOfAccount.setChargeCollCust(
				PennantApplicationUtil.formateAmount(statementOfAccount.getChargeCollCust(), ccyEditField));
		statementOfAccount.setUpfrontIntCust(
				PennantApplicationUtil.formateAmount(statementOfAccount.getUpfrontIntCust(), ccyEditField));
		statementOfAccount.setLinkedFinRef(
				PennantApplicationUtil.formateAmount(statementOfAccount.getLinkedFinRef(), ccyEditField));
		statementOfAccount.setClosedlinkedFinRef(
				PennantApplicationUtil.formateAmount(statementOfAccount.getClosedlinkedFinRef(), ccyEditField));

		if (AdvanceType.hasAdvEMI(finMain.getAdvType())) {
			statementOfAccount.setEmiReceivedPri(PennantApplicationUtil
					.formateAmount(statementOfAccount.getEmiReceivedPri().add(finMain.getAdvanceEMI()), ccyEditField));
		} else {
			statementOfAccount.setEmiReceivedPri(
					PennantApplicationUtil.formateAmount(statementOfAccount.getEmiReceivedPri(), ccyEditField));
		}

		statementOfAccount.setEmiReceivedPft(
				PennantApplicationUtil.formateAmount(statementOfAccount.getEmiReceivedPft(), ccyEditField));

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

		//get the Summary Details
		List<SOASummaryReport> soaSummaryDetailsList = getSOASummaryDetails(finReference, finMain, financeProfitDetail);

		for (SOASummaryReport summary : soaSummaryDetailsList) {
			summary.setFinReference(finReference);
			summary.setCcyEditField(ccyEditField);
			summary.setCcyMinorCcyUnits(ccyMinorCcyUnits);
			summary.setAppDate(DateUtility.getAppDate());
			summary.setDue(PennantApplicationUtil.formateAmount(summary.getDue(), ccyEditField));
			summary.setReceipt(PennantApplicationUtil.formateAmount(summary.getReceipt(), ccyEditField));
			summary.setOverDue(PennantApplicationUtil.formateAmount(summary.getOverDue(), ccyEditField));
		}

		//get the Transaction Details
		List<SOATransactionReport> soaTransactionReportsList = getTransactionDetails(finReference, statementOfAccount,
				finMain);

		List<SOATransactionReport> finalSOATransactionReports = new ArrayList<SOATransactionReport>();

		//Transaction Details Filtering
		for (SOATransactionReport soaTransactionReport : soaTransactionReportsList) {
			if (DateUtility.compare(soaTransactionReport.getTransactionDate(), startDate) >= 0
					&& DateUtility.compare(soaTransactionReport.getTransactionDate(), endDate) <= 0) {

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
		//Get the Selected Loan Types are Adding ValueDate and balance for the SOA Report.
		List<String> soaFinTypes = getSOAFinTypes();
		if (soaFinTypes != null && soaFinTypes.contains(finMain.getFinType())) {
			Collections.sort(finalSOATransactionReports, new Comparator<SOATransactionReport>() {
				public int compare(SOATransactionReport o1, SOATransactionReport o2) {

					return ComparisonChain.start().compare(o1.getValueDate(), o2.getValueDate())
							.compare(o1.getPriority(), o2.getPriority()).result();
				}
			});

		} else {

			Collections.sort(finalSOATransactionReports, new Comparator<SOATransactionReport>() {
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
		//send the toDate and from Date for Report
		if (finalSOATransactionReports.isEmpty()) {
			SOATransactionReport sOATransactionReport = new SOATransactionReport();
			sOATransactionReport.setFromDate(startDate);
			sOATransactionReport.setToDate(endDate);
			finalSOATransactionReports.add(sOATransactionReport);
		}
		//Displaying Interest Rate And months for LAN.  	
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
			int noOfMonths = DateUtility.getMonthsBetween(finMain.getFinStartDate(), fixedEndDate);
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
				statementOfAccount.setIntRateType("Fixed");
			} else if (StringUtils.equals(CalculationConstants.RATE_BASIS_R, finMain.getRepayRateBasis())) {
				statementOfAccount.setIntRateType("Floating");
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

		//Summary Reports List
		statementOfAccount.setSoaSummaryReports(soaSummaryDetailsList);

		//Transaction Reports List
		statementOfAccount.setTransactionReports(finalSOATransactionReports);

		//Other Finance Details
		statementOfAccount.setOtherFinanceDetails(otherFinanceDetails);

		//Customer Loan Reference Details
		//statementOfAccount.setOtherFinanceDetails(otherFinanceRefDetails);

		//Co-Applicant/Borrower Details
		statementOfAccount.setApplicantDetails(applicantDetails);

		//Interest Rate Details
		statementOfAccount.setInterestRateDetails(interestRateDetails);
		statementOfAccount.setFutureInterestComponent(statementOfAccount.getFutureInterestComponent());
		statementOfAccount.setFuturePrincipalComponent(statementOfAccount.getFuturePrincipalComponent());
		statementOfAccount.setFutureInstAmount(statementOfAccount.getFutureInstAmount());

		logger.debug("Leaving");
		return statementOfAccount;
	}

	/**
	 * get the Report Summary Details
	 * 
	 */
	private List<SOASummaryReport> getSOASummaryDetails(String finReference, FinanceMain finMain,
			FinanceProfitDetail financeProfitDetail) {
		logger.debug("Enetring");

		SOASummaryReport soaSummaryReport = null;
		List<SOASummaryReport> soaSummaryReportsList = new ArrayList<SOASummaryReport>();

		//FinanceMain finMain = getFinanceMain(finReference);

		if (finMain == null || FinanceConstants.CLOSE_STATUS_CANCELLED.equals(finMain.getClosingStatus())) {
			return soaSummaryReportsList;
		}

		List<FinanceScheduleDetail> finSchdDetList = getFinScheduleDetails(finReference);
		//FinanceProfitDetail financeProfitDetail = getFinanceProfitDetails(finReference);

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

		if (finSchdDetList != null && !finSchdDetList.isEmpty()) {

			for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {

				if ((DateUtility.compare(finSchdDetail.getSchDate(), SysParamUtil.getAppDate()) <= 0)) {

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
				}
			}

			due = totalProfitSchd.add(totalPrincipalSchd).add(totalFeeschd);
			receipt = totalSchdPriPaid.add(totalSchdPftPaid).add(totalSchdfeepaid);

			overDue = due.subtract(receipt);

			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Installment Amount");
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue);
			netDue = overDue;
			soaSummaryReportsList.add(soaSummaryReport);

			due = totalPrincipalSchd.subtract(finMain.getAdvanceEMI());
			receipt = totalSchdPriPaid;

			overDue = due.subtract(receipt);

			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Principal Component");
			soaSummaryReport.setDue(due.add(finMain.getAdvanceEMI()));
			soaSummaryReport.setReceipt(receipt.add(finMain.getAdvanceEMI()));
			soaSummaryReport.setOverDue(overDue);
			soaSummaryReportsList.add(soaSummaryReport);

			due = totalProfitSchd;
			receipt = totalSchdPftPaid;
			overDue = due.subtract(receipt);

			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Interest Component");
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue);

			soaSummaryReportsList.add(soaSummaryReport);
		}

		if (financeProfitDetail == null) {
			return soaSummaryReportsList;
		}

		List<FinODDetails> finODDetailsList = getFinODDetails(finReference);
		List<ManualAdvise> manualAdviseList = getManualAdvise(finReference);
		List<FinExcessAmount> finExcessAmountsList = getFinExcessAmountsList(finReference);

		// FinODDetails

		BigDecimal totPenaltyAmt = BigDecimal.ZERO;
		BigDecimal totwaived = BigDecimal.ZERO;
		BigDecimal totPenaltyPaid = BigDecimal.ZERO;
		BigDecimal lpiAmt = BigDecimal.ZERO;
		BigDecimal lpiWaived = BigDecimal.ZERO;
		BigDecimal lpiPaid = BigDecimal.ZERO;

		boolean isODCExclusive = false;
		odcFeeType = getFeeTypeDAO().getTaxDetailByCode(RepayConstants.ALLOCATION_ODC);
		if (odcFeeType != null && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(odcFeeType.getTaxComponent())) {
			isODCExclusive = true;
		}

		if (CollectionUtils.isNotEmpty(finODDetailsList)) {
			for (FinODDetails finODDetails : finODDetailsList) {

				if (isODCExclusive && finODDetails.getTotPenaltyAmt() != null) { //GST Calculation only for Exclusive case
					BigDecimal gstAmount = GSTCalculator.getTotalGST(finReference, finODDetails.getTotPenaltyAmt(),
							odcFeeType.getTaxComponent());
					totPenaltyAmt = totPenaltyAmt.add(finODDetails.getTotPenaltyAmt()).add(gstAmount);
				} else if (finODDetails.getTotPenaltyAmt() != null) {
					totPenaltyAmt = totPenaltyAmt.add(finODDetails.getTotPenaltyAmt());
				}

				if (isODCExclusive && finODDetails.getTotPenaltyPaid() != null) { //GST Calculation only for Exclusive case
					BigDecimal gstAmount = GSTCalculator.getTotalGST(finReference, finODDetails.getTotPenaltyPaid(),
							odcFeeType.getTaxComponent());
					totPenaltyPaid = totPenaltyPaid.add(finODDetails.getTotPenaltyPaid()).add(gstAmount);
				} else if (finODDetails.getTotPenaltyAmt() != null) {
					totPenaltyPaid = totPenaltyPaid.add(finODDetails.getTotPenaltyPaid());
				}

				if (isODCExclusive && finODDetails.getTotWaived() != null) { //GST Calculation only for Exclusive case
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

		String narration = Labels.getLabel("label_SOA_Late_Payment_Penalty.value");//Late Payment Penalty

		if (StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("LATE_PAYMENT_INTEREST_CLIX"))) {
			narration = "Late Payment Interest";
		} else {

			due = lpiAmt.subtract(lpiWaived);
			receipt = lpiPaid;
			overDue = due.subtract(receipt);
			netDue = netDue.add(overDue);

			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Late Payment Interest");
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue);

			soaSummaryReportsList.add(soaSummaryReport);

		}
		due = totPenaltyAmt.subtract(totwaived);
		receipt = totPenaltyPaid;
		overDue = due.subtract(receipt);

		soaSummaryReport = new SOASummaryReport();
		soaSummaryReport.setComponent(narration);
		soaSummaryReport.setDue(due);
		soaSummaryReport.setReceipt(receipt);
		soaSummaryReport.setOverDue(overDue);
		netDue = netDue.add(overDue);

		soaSummaryReportsList.add(soaSummaryReport);

		BigDecimal bounceDue = BigDecimal.ZERO;
		BigDecimal bounceRecipt = BigDecimal.ZERO;

		BigDecimal otherReceivableDue = BigDecimal.ZERO;
		BigDecimal otherReceivableReceipt = BigDecimal.ZERO;

		BigDecimal otherPayableDue = BigDecimal.ZERO;

		//Manual Advise
		if (manualAdviseList != null && !manualAdviseList.isEmpty()) {

			BigDecimal adviseBalanceAmt = BigDecimal.ZERO;
			BigDecimal bounceZeroAdviseAmount = BigDecimal.ZERO;
			BigDecimal bounceGreaterZeroAdviseAmount = BigDecimal.ZERO;
			BigDecimal bounceZeroPaidAmount = BigDecimal.ZERO;
			BigDecimal bounceGreaterZeroPaidAmount = BigDecimal.ZERO;

			for (ManualAdvise manualAdvise : manualAdviseList) {
				if (manualAdvise.getAdviseType() == 2 && manualAdvise.getBalanceAmt() != null) {
					BigDecimal gstAmount = BigDecimal.ZERO;
					// # PSD :151411 17-03-20 :In SOA, Payable amount(Other Payables) displayed with GST when User add Payable Exclusive Charge.
					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(manualAdvise.getTaxComponent())
							&& manualAdvise.getBalanceAmt().compareTo(BigDecimal.ZERO) > 0) {
						gstAmount = GSTCalculator.getTotalGST(finReference, manualAdvise.getAdviseAmount(),
								manualAdvise.getTaxComponent());
					}
					adviseBalanceAmt = adviseBalanceAmt.add(manualAdvise.getBalanceAmt().add(gstAmount));
				}

				if (manualAdvise.getAdviseType() == 1 && manualAdvise.getBounceID() == 0) {

					if (manualAdvise.getAdviseAmount() != null) {
						bounceZeroAdviseAmount = bounceZeroAdviseAmount.add(manualAdvise.getAdviseAmount())
								.subtract(manualAdvise.getWaivedAmount());

						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(manualAdvise.getTaxComponent())) { //GST Calculation only for Exclusive case
							BigDecimal gstAmount = GSTCalculator.getTotalGST(finReference,
									manualAdvise.getAdviseAmount().subtract(manualAdvise.getWaivedAmount()),
									manualAdvise.getTaxComponent());
							bounceZeroAdviseAmount = bounceZeroAdviseAmount.add(gstAmount);
						}
					}

					if (manualAdvise.getPaidAmount() != null) {
						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(manualAdvise.getTaxComponent())) { //GST Calculation only for Exclusive case
							bounceZeroPaidAmount = bounceZeroPaidAmount.add(manualAdvise.getPaidAmount())
									.add(CalculationUtil.getTotalPaidGST(manualAdvise));
						} else {
							bounceZeroPaidAmount = bounceZeroPaidAmount.add(manualAdvise.getPaidAmount());
						}
					}
				}

				if (manualAdvise.getBounceID() > 0) {

					if (bounceFeeType == null) {
						bounceFeeType = getFeeTypeDAO().getTaxDetailByCode(RepayConstants.ALLOCATION_BOUNCE);
					}

					if (manualAdvise.getAdviseAmount() != null) {
						bounceGreaterZeroAdviseAmount = bounceGreaterZeroAdviseAmount
								.add(manualAdvise.getAdviseAmount()).subtract(manualAdvise.getWaivedAmount());

						if (bounceFeeType != null && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE
								.equals(bounceFeeType.getTaxComponent())) { //GST Calculation only for Exclusive case
							BigDecimal gstAmount = GSTCalculator.getTotalGST(finReference,
									manualAdvise.getAdviseAmount().subtract(manualAdvise.getWaivedAmount()),
									bounceFeeType.getTaxComponent());

							bounceGreaterZeroAdviseAmount = bounceGreaterZeroAdviseAmount.add(gstAmount);
						}
					}

					if (manualAdvise.getPaidAmount() != null) {
						if (bounceFeeType != null && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE
								.equals(bounceFeeType.getTaxComponent())) { //GST Calculation only for Exclusive case
							bounceGreaterZeroPaidAmount = bounceGreaterZeroPaidAmount.add(manualAdvise.getPaidAmount())
									.add(CalculationUtil.getTotalPaidGST(manualAdvise));
						} else {
							bounceGreaterZeroPaidAmount = bounceGreaterZeroPaidAmount.add(manualAdvise.getPaidAmount());
						}
					}
				}
			}

			bounceDue = bounceGreaterZeroAdviseAmount;
			bounceRecipt = bounceGreaterZeroPaidAmount;

			otherReceivableDue = bounceZeroAdviseAmount;
			otherReceivableReceipt = bounceZeroPaidAmount;

			otherPayableDue = adviseBalanceAmt;
		}

		overDue = bounceDue.subtract(bounceRecipt);

		soaSummaryReport = new SOASummaryReport();
		soaSummaryReport.setComponent("Bounce Charges");
		soaSummaryReport.setDue(bounceDue);
		soaSummaryReport.setReceipt(bounceRecipt);
		soaSummaryReport.setOverDue(overDue);
		netDue = netDue.add(overDue);
		soaSummaryReportsList.add(soaSummaryReport);

		overDue = otherReceivableDue.subtract(otherReceivableReceipt);

		soaSummaryReport = new SOASummaryReport();
		soaSummaryReport.setComponent("Other Receivables");
		soaSummaryReport.setDue(otherReceivableDue);
		soaSummaryReport.setReceipt(otherReceivableReceipt);
		soaSummaryReport.setOverDue(overDue);

		netDue = netDue.add(overDue);

		soaSummaryReportsList.add(soaSummaryReport);

		receipt = BigDecimal.ZERO;
		overDue = BigDecimal.ZERO;

		soaSummaryReport = new SOASummaryReport();
		soaSummaryReport.setComponent("Other Payables");
		soaSummaryReport.setDue(otherPayableDue);
		soaSummaryReport.setReceipt(receipt);
		soaSummaryReport.setOverDue(overDue);
		netDue = netDue.subtract(otherPayableDue);

		soaSummaryReportsList.add(soaSummaryReport);

		//FinExcess Amount
		if (finExcessAmountsList != null && !finExcessAmountsList.isEmpty()) {

			BigDecimal balanceAmt = BigDecimal.ZERO;

			for (FinExcessAmount finExcessAmount : finExcessAmountsList) {
				if (finExcessAmount.getBalanceAmt() != null) {
					balanceAmt = balanceAmt.add(finExcessAmount.getBalanceAmt());
				}
			}

			due = balanceAmt;
			overDue = BigDecimal.ZERO;
			receipt = BigDecimal.ZERO;

			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Unadjusted Amount");
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue.negate());
			netDue = netDue.subtract(overDue);
			soaSummaryReportsList.add(soaSummaryReport);
		} else {

			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Unadjusted Amount");
			soaSummaryReport.setDue(BigDecimal.ZERO);
			soaSummaryReport.setReceipt(BigDecimal.ZERO);
			soaSummaryReport.setOverDue(BigDecimal.ZERO);

			soaSummaryReportsList.add(soaSummaryReport);
		}
		soaSummaryReport = new SOASummaryReport();
		soaSummaryReport.setComponent("Net Receivable");
		soaSummaryReport.setDue(BigDecimal.ZERO);
		soaSummaryReport.setReceipt(BigDecimal.ZERO);
		soaSummaryReport.setOverDue(netDue);
		netDue = overDue;

		soaSummaryReportsList.add(soaSummaryReport);

		logger.debug("Leaving");
		return soaSummaryReportsList;
	}

	/**
	 * to get the Report Transaction Details
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public List<SOATransactionReport> getTransactionDetails(String finReference, StatementOfAccount statementOfAccount,
			FinanceMain finMain) throws IllegalAccessException, InvocationTargetException {

		logger.debug("Enetring");

		//Fin Schedule details
		String finSchedulePayable = "Amount Financed - Payable ";//Add Disbursement  1

		String brokenPeriodEvent = "Broken Period Interest Receivable "; //6
		String foreclosureAmount = "Foreclosure Amount "; //19
		String dueForInstallment = "Due for Installment "; //3
		String partPrepayment = "Part Prepayment Amount "; //5

		//Fin Advance Payments
		String advancePayment = "Amount Paid Vide "; //Amount Paid Vide  2

		//Payment Instructions
		String payInsEvent = "Amount Paid Vide ";//"STLMNT"; 7

		// FinODDetails

		//FinODDetails
		String penality = Labels.getLabel("label_penalty_due_created_for_past_due_till_date.value");//"Penalty Due Created for Past due till date "; //16
		String penaltyUnAccrued = "Unaccrued Penalty due till date "; // 16
		String penalityMnthEnd = "Penalty Accrued on Month-End date ";

		//Manual Advise Movement List
		String manualAdviseMovementEvent = "Waived Amount "; //13

		//Manual Advise
		String manualAdvFeeType = "- Payable"; //12
		String manualAdvPrentmentNotIn = "FeeDesc or Bounce - Due "; //10
		String manualAdvPrentmentIn = ""; // 11

		//Receipt Header
		String rHEventExcess = "Payment Received vide ";//10
		String rHTdsAdjust = "TDS Adjustment"; //17
		String rHPaymentBouncedFor = "Payment Bounced For "; //9
		String rHTdsAdjustReversal = "TDS Adjustment Reversal "; //18

		//FinFeeDetails
		String finFeeDetailOrgination = "- Due "; //14
		String finFeeDetailNotInDISBorPOSP = "- Due "; //15
		String finFeeDetailEvent = "- Due "; //4
		String finRef = "";//"(" + finReference + ")";
		String rHPftWaived = "Interest from customer Waived Off ";
		String rHPriWaived = "Principal from customer Waived Off ";
		String rHPenaltyWaived = "Penalty from customer Waived Off ";
		String lppWaived = "Penalty from customer Waived Off ";
		String lpiIWaived = "Penalty Interest from customer Waived Off ";

		//AdvanceEMI Details
		String advEmiDebitEntry = "Total Disbursement, Advance EMI"; //25
		String advEmiCreditEntry = "Advance EMI with maturity date"; //26
		String downPaymentEntry = "Advance EMI"; //26
		SOATransactionReport soaTranReport = null;
		List<SOATransactionReport> soaTransactionReports = new ArrayList<SOATransactionReport>();

		//FinanceMain finMain = getFinanceMain(finReference);

		if (finMain != null) {

			FeeType bounceFeeType = null;

			//Finance Schedule Details
			List<FinanceScheduleDetail> finSchdDetList = getFinScheduleDetails(finReference);
			Date maxSchDate = getMaxSchDate(finReference);

			//Finance Advance Payment Details
			List<FinAdvancePayments> finAdvancePaymentsList = getFinAdvancePayments(finReference);

			//Payment Instruction Details
			List<PaymentInstruction> paymentInstructionsList = getPaymentInstructions(finReference);

			//FinODDetails
			List<FinODDetails> finODDetailsList = getFinODDetails(finReference);

			//Manual Advise Movements List
			List<ManualAdviseMovements> manualAdviseMovementsList = getManualAdviseMovements(finReference);

			//Manual Advise List
			List<ManualAdvise> manualAdviseList = getManualAdvise(finReference);

			//PresentmentDetails
			List<PresentmentDetail> PresentmentDetailsList = getPresentmentDetailsList(finReference);

			//Fin Receipt Header
			List<FinReceiptHeader> finReceiptHeadersList = getFinReceiptHeaders(finReference);
			
			Map<Long,List<ReceiptAllocationDetail>> radMap = getReceiptAllocationDetailMap(finReference);

			//Fin Fee Details
			List<FinFeeDetail> finFeedetailsList = getFinFeedetails(finReference);

			List<FeeWaiverDetail> feeWaiverDetailList = getFeeWaiverDetail(finReference);

			//Finance Schedule Details
			String closingStatus = finMain.getClosingStatus();
			for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {
				String bpiOrHoliday = finSchdDetail.getBpiOrHoliday();
				BigDecimal repayAmount = finSchdDetail.getRepayAmount();

				if (StringUtils.isBlank(closingStatus) || !StringUtils.equalsIgnoreCase(closingStatus, "C")) {

					// Add disbursement
					if (finSchdDetail.isDisbOnSchDate()) {

						BigDecimal transactionAmount = BigDecimal.ZERO;

						if (finSchdDetail.getDisbAmount() != null) {
							transactionAmount = finSchdDetail.getDisbAmount();
						}

						if (DateUtility.compare(finSchdDetail.getSchDate(), finMain.getFinStartDate()) == 0) {
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
					}

					//Broken Period Interest Receivable- Due
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

				//fore closure Amount 
				BigDecimal partialPaidAmt = finSchdDetail.getPartialPaidAmt();
				if (maxSchDate != null && DateUtility.compare(maxSchDate, finSchdDetail.getSchDate()) == 0) {
					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(foreclosureAmount + finRef);
					soaTranReport.setTransactionDate(finSchdDetail.getSchDate());
					soaTranReport.setValueDate(finSchdDetail.getSchDate());
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
					soaTranReport.setDebitAmount(repayAmount.subtract(partialPaidAmt));
					soaTranReport.setPriority(23);
					soaTransactionReports.add(soaTranReport);
				}

				if ((DateUtility.compare(finSchdDetail.getSchDate(), SysParamUtil.getAppDate()) <= 0)) {

					// Partial Prepayment Amount
					if (partialPaidAmt != null && partialPaidAmt.compareTo(BigDecimal.ZERO) > 0) {

						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(partPrepayment + finRef);
						soaTranReport.setTransactionDate(finSchdDetail.getSchDate());
						soaTranReport.setValueDate(finSchdDetail.getSchDate());
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setDebitAmount(partialPaidAmt);
						soaTranReport.setPriority(7);
						soaTransactionReports.add(soaTranReport);
					}

					//Due for Installment 
					if ((StringUtils.isBlank(closingStatus) || !StringUtils.equalsIgnoreCase(closingStatus, "C"))
							&& (!finSchdDetail.isDisbOnSchDate()
									&& DateUtility.compare(maxSchDate, finSchdDetail.getSchDate()) != 0)
							&& (!StringUtils.equalsIgnoreCase(bpiOrHoliday, "H")
									&& !StringUtils.equalsIgnoreCase(bpiOrHoliday, "B"))) {

						BigDecimal transactionAmt = BigDecimal.ZERO;

						if (repayAmount != null) {
							transactionAmt = repayAmount;
						}

						if (partialPaidAmt != null) {
							transactionAmt = transactionAmt.subtract(partialPaidAmt);
						}

						soaTranReport = new SOATransactionReport();
						soaTranReport.setEvent(dueForInstallment + finSchdDetail.getInstNumber() + finRef);
						soaTranReport.setTransactionDate(finSchdDetail.getSchDate());
						soaTranReport.setValueDate(finSchdDetail.getSchDate());
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setDebitAmount(transactionAmt);
						soaTranReport.setPriority(3);
						if (soaTranReport.getDebitAmount().compareTo(BigDecimal.ZERO) > 0) {
							soaTransactionReports.add(soaTranReport);
						}
					}
				}
				if (finSchdDetail.getInstNumber() == finMain.getFixedRateTenor()) {
					fixedEndDate = null;
					fixedEndDate = finSchdDetail.getSchDate();
				}
			}

			//fin Advance Payments List 
			if (finAdvancePaymentsList != null && !finAdvancePaymentsList.isEmpty()) {
				for (FinAdvancePayments finAdvancePayments : finAdvancePaymentsList) {
					advancePayment = "Amount Paid Vide ";
					String status = "";
					if (StringUtils.equals(finAdvancePayments.getStatus(), DisbursementConstants.STATUS_AWAITCON)) {
						status = " - Subject to realization";
					}
					soaTranReport = new SOATransactionReport();
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
					} else {
						String transactionRef = finAdvancePayments.getTransactionRef();
						if (StringUtils.isNotBlank(transactionRef)) {
							advancePayment = advancePayment.concat(transactionRef);
						}
						soaTranReport.setValueDate(finAdvancePayments.getLlDate());
					}
					soaTranReport.setEvent(advancePayment + finRef + status);
					soaTranReport.setTransactionDate(finAdvancePayments.getLlDate());
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
					soaTranReport.setDebitAmount(finAdvancePayments.getAmtToBeReleased());
					soaTranReport.setPriority(2);

					soaTransactionReports.add(soaTranReport);
				}
			}

			//paymentInstructionsList 
			for (PaymentInstruction payInstruction : paymentInstructionsList) {
				soaTranReport = new SOATransactionReport();
				payInsEvent = "Amount Paid Vide ";
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
			if (finODDetailsList != null && !finODDetailsList.isEmpty()
					&& (StringUtils.isBlank(closingStatus) || !StringUtils.equalsIgnoreCase(closingStatus, "C"))) {

				for (FinODDetails finODDetails : finODDetailsList) {
					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(penality
							+ DateUtility.format(finODDetails.getFinODTillDate(), DateFormat.SHORT_DATE.getPattern()));
					soaTranReport.setTransactionDate(finODDetails.getFinODSchdDate());
					soaTranReport.setValueDate(finODDetails.getFinODSchdDate());
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
					soaTranReport.setDebitAmount(finODDetails.getTotPenaltyAmt());
					soaTranReport.setPriority(19);
					soaTransactionReports.add(soaTranReport);
				}
			}

			//Manual Advise Movement List 
			if (manualAdviseMovementsList != null && !manualAdviseMovementsList.isEmpty()) {
				for (ManualAdviseMovements ma : manualAdviseMovementsList) {

					if (ma.getWaivedAmount().compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}
					soaTranReport = new SOATransactionReport();
					manualAdviseMovementEvent = "Waived Amount ";
					if (StringUtils.isNotBlank(ma.getFeeTypeDesc())) {
						manualAdviseMovementEvent = manualAdviseMovementEvent.concat(ma.getFeeTypeDesc());
					} else {
						manualAdviseMovementEvent = manualAdviseMovementEvent.concat("Bounce");
						if (bounceFeeType == null) {
							bounceFeeType = getFeeTypeDAO().getTaxDetailByCode(RepayConstants.ALLOCATION_BOUNCE);
						}
						ma.setTaxComponent(bounceFeeType.getTaxComponent());
					}
					soaTranReport.setEvent(manualAdviseMovementEvent.concat(finRef));
					soaTranReport.setTransactionDate(ma.getMovementDate());
					soaTranReport.setValueDate(ma.getValueDate());
					soaTranReport.setCreditAmount(ma.getWaivedAmount());
					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(ma.getTaxComponent())) {
						soaTranReport.setCreditAmount(ma.getWaivedAmount().add(getWaivedGST(ma.getTaxHeader())));
					}
					soaTranReport.setDebitAmount(BigDecimal.ZERO);
					soaTranReport.setPriority(15);

					soaTransactionReports.add(soaTranReport);
				}
			}

			List<Long> presentmentReceiptIds = new ArrayList<Long>();

			for (PresentmentDetail presentmentDetail : PresentmentDetailsList) {
				if (!presentmentReceiptIds.contains(presentmentDetail.getReceiptID())) {
					presentmentReceiptIds.add(presentmentDetail.getReceiptID());
				}
			}

			//Manual Advise 
			for (ManualAdvise manualAdvise : manualAdviseList) {
				manualAdvFeeType = "- Payable"; //12
				manualAdvPrentmentNotIn = "FeeDesc or Bounce - Due "; //10
				manualAdvPrentmentIn = " "; // 11

				if ((manualAdvise.getFeeTypeID() != 0 && manualAdvise.getFeeTypeID() != Long.MIN_VALUE)
						&& StringUtils.isNotBlank(manualAdvise.getFeeTypeDesc()) && manualAdvise.getAdviseType() == 2
						&& manualAdvise.getAdviseAmount().compareTo(BigDecimal.ZERO) > 0) {
					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(manualAdvise.getFeeTypeDesc() + manualAdvFeeType + finRef);
					soaTranReport.setTransactionDate(manualAdvise.getPostDate());
					soaTranReport.setValueDate(manualAdvise.getValueDate());

					//03-09-18:GST Amount For Payable advices is not reflecting in SOA
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
				//Bounce/Fee - Due
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
								bounceFeeType = getFeeTypeDAO().getTaxDetailByCode(RepayConstants.ALLOCATION_BOUNCE);
							}
							if (bounceFeeType != null) {
								taxComponent = bounceFeeType.getTaxComponent();
							}
							manualAdvPrentmentNotIn = "Bounce - Due";
						}

						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
							gstAmount = GSTCalculator.getTotalGST(finReference, manualAdvise.getWaivedAmount(),
									taxComponent);
						}

						soaTranReport.setEvent(manualAdvPrentmentNotIn + finRef);
						soaTranReport.setTransactionDate(manualAdvise.getPostDate());
						soaTranReport.setValueDate(manualAdvise.getValueDate());
						soaTranReport.setCreditAmount(BigDecimal.ZERO);
						soaTranReport.setDebitAmount(manualAdvise.getWaivedAmount().add(gstAmount));
						soaTranReport.setPriority(12);

						soaTransactionReports.add(soaTranReport);
					} else {
						//Bounce created for particular on Installment 
						if (manualAdvise.getFeeTypeID() == 0) {
							String taxComponent = "";
							BigDecimal gstAmount = BigDecimal.ZERO;
							taxComponent = manualAdvise.getTaxComponent();

							for (PresentmentDetail presentmentDetail : PresentmentDetailsList) {

								if (manualAdvise.getReceiptID() == presentmentDetail.getReceiptID()) {

									for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {

										if (DateUtility.compare(presentmentDetail.getSchDate(),
												finSchdDetail.getSchDate()) == 0) {
											manualAdvPrentmentIn = "Bounce Created for " + "'"
													+ presentmentDetail.getBounceReason() + "'" + " on Installment:";
											soaTranReport = new SOATransactionReport();
											if (finSchdDetail.getInstNumber() > 0) {
												manualAdvPrentmentIn = manualAdvPrentmentIn
														.concat(String.valueOf(finSchdDetail.getInstNumber()));
											}
											manualAdvPrentmentIn = manualAdvPrentmentIn.concat(finRef);
											soaTranReport.setEvent(manualAdvPrentmentIn);
											soaTranReport.setTransactionDate(manualAdvise.getPostDate());
											soaTranReport.setValueDate(manualAdvise.getValueDate());
											soaTranReport.setCreditAmount(BigDecimal.ZERO);

											if (bounceFeeType == null) {
												bounceFeeType = getFeeTypeDAO()
														.getTaxDetailByCode(RepayConstants.ALLOCATION_BOUNCE);
											}
											if (bounceFeeType != null) {
												taxComponent = bounceFeeType.getTaxComponent();
											}
											if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
												gstAmount = GSTCalculator.getTotalGST(finReference,
														manualAdvise.getAdviseAmount(), taxComponent);

											}
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

			if (finReceiptHeadersList != null && !finReceiptHeadersList.isEmpty()) {

				//FinReceiptDetails List
				List<FinReceiptDetail> finReceiptDetailsList = getFinReceiptDetails(finReference);

				//FinRepayHeaders List
				List<FinRepayHeader> finRepayHeadersList = getFinRepayHeadersList(finReference);

				//FinRepayscheduledetails List
				List<RepayScheduleDetail> finRepaySchdDetails = getRepayScheduleDetailsList(finReference);

				//FinReceipt Allocation Details
				List<ReceiptAllocationDetail> finReceiptAllocDetails = getReceiptAllocationDetailsList(finReference);

				for (FinReceiptHeader finReceiptHeader : finReceiptHeadersList) {

					for (FinReceiptDetail finReceiptDetail : finReceiptDetailsList) {

						long receiptID = finReceiptDetail.getReceiptID();
						long rhReceiptID = finReceiptHeader.getReceiptID();
						if (receiptID == rhReceiptID) {
							rHEventExcess = "Payment Received vide ";
							String rpaymentType = StringUtils.trimToEmpty(finReceiptDetail.getPaymentType());
							String receiptModeStatus = StringUtils.trimToEmpty(finReceiptHeader.getReceiptModeStatus());
							//30-08-2019:Receipt date and value date should populate in SOA
							Date receiptDate = finReceiptHeader.getReceiptDate();
							Date receivedDate = finReceiptDetail.getValueDate();

							String favourNumber = finReceiptDetail.getFavourNumber();

							String status = "";
							String paymentType = "";
							int instlNo = 0;
							soaTranReport = new SOATransactionReport();
							soaTranReport.setTransactionDate(receiptDate);
							if (!(StringUtils.equals("EXCESS", rpaymentType)
									|| StringUtils.equals("CASH", rpaymentType))) {
								for (PresentmentDetail presentmentDetail : PresentmentDetailsList) {
									String mandateType = StringUtils.trimToEmpty(presentmentDetail.getMandateType());

									if (receiptID == presentmentDetail.getReceiptID() && StringUtils
											.equals(rpaymentType, RepayConstants.RECEIPTMODE_PRESENTMENT)) {
										if (StringUtils.equals(presentmentDetail.getStatus(),
												RepayConstants.PEXC_APPROV)) {
											status = " - Subject to realization";
										}
										instlNo = presentmentDetail.getEmiNo();

										if (mandateType.equals(MandateConstants.TYPE_DDM)) {
											paymentType = "Direct Debit";
										} else {
											paymentType = mandateType;
										}
										paymentType = paymentType.concat(" EMI NO.: " + instlNo);
									}

								}

								if (StringUtils.equals(rpaymentType, RepayConstants.RECEIPTMODE_CHEQUE)
										&& StringUtils.equals(receiptModeStatus, RepayConstants.PAYSTATUS_DEPOSITED)) {
									status = " - Subject to realization";
								}

								if (StringUtils.isNotBlank(rpaymentType)) {

									if (StringUtils.equals(rpaymentType, RepayConstants.RECEIPTMODE_CHEQUE)) {
										paymentType = StringUtils.capitaliseAllWords(rpaymentType) + " No.:";
									} else if (!StringUtils.equals(rpaymentType, RepayConstants.RECEIPTMODE_PRESENTMENT)
											&& !StringUtils.equals("PAYABLE", rpaymentType)) {
										paymentType = rpaymentType + " No.:";
									}
									rHEventExcess = rHEventExcess.concat(paymentType);
								}
								if (StringUtils.isNotBlank(finReceiptDetail.getTransactionRef())) {
									rHEventExcess = rHEventExcess.concat(finReceiptDetail.getTransactionRef());
								}
								if (StringUtils.isNotBlank(favourNumber)) {
									rHEventExcess = rHEventExcess.concat(favourNumber);
								}
								rHEventExcess = rHEventExcess.concat(" " + finRef);

							} else if (StringUtils.equals("EXCESS", rpaymentType)
									|| StringUtils.equals("PAYABLE", rpaymentType)) {
								rHEventExcess = "Amount Adjusted " + finRef;
							} else if (StringUtils.equals("CASH", rpaymentType)) {
								rHEventExcess = "Cash Received Vide Receipt No";
								if (StringUtils.isNotBlank(finReceiptDetail.getPaymentRef())) {
									rHEventExcess = rHEventExcess.concat(finReceiptDetail.getPaymentRef() + finRef);
								}
							}
							String allocMsg = "";
                            if (radMap.containsKey(finReceiptHeader.getReceiptID())){
								allocMsg = buildAllocationData(radMap.get(finReceiptHeader.getReceiptID()),
										CurrencyUtil.getFormat(finMain.getFinCcy()));
                            }
							soaTranReport.setValueDate(receivedDate);
							soaTranReport.setEvent(rHEventExcess + status + allocMsg);
							soaTranReport.setCreditAmount(finReceiptDetail.getAmount());

							if (StringUtils.equals(rpaymentType, "EXCESS")) {
								soaTranReport.setDebitAmount(finReceiptDetail.getAmount());
							} else {
								soaTranReport.setDebitAmount(BigDecimal.ZERO);
							}
							soaTranReport.setPriority(10);
							if (!StringUtils.equals(receiptModeStatus, RepayConstants.PAYSTATUS_CANCEL)) {
								soaTransactionReports.add(soaTranReport);
							}

							//Cancelled Receipt's Details
							if (SysParamUtil.isAllowed(SMTParameterConstants.SOA_SHOW_CANCEL_RECEIPT)
									&& StringUtils.equals(receiptModeStatus, RepayConstants.PAYSTATUS_CANCEL)) {
								soaTransactionReports.add(soaTranReport);
								SOATransactionReport cancelReport = new SOATransactionReport();
								BeanUtils.copyProperties(cancelReport, soaTranReport);
								cancelReport.setDebitAmount(finReceiptDetail.getAmount());
								if (StringUtils.equals(rpaymentType, "EXCESS")) {
									cancelReport.setCreditAmount(finReceiptDetail.getAmount());
								} else {
									cancelReport.setCreditAmount(BigDecimal.ZERO);
								}
								cancelReport.setEvent(rHEventExcess.replaceAll("Received", "Cancelled") + status);
								soaTransactionReports.add(cancelReport);
							}

							boolean tdsEntryReq = true;
							if ((StringUtils.equalsIgnoreCase(finReceiptHeader.getReceiptModeStatus(), "B")
									|| StringUtils.equalsIgnoreCase(finReceiptHeader.getReceiptModeStatus(), "C"))
									&& !SysParamUtil.isAllowed(SMTParameterConstants.DISPLAY_TDS_REV_SOA)) {
								tdsEntryReq = false;
							}

							// Receipt Allocation Details
							if (tdsEntryReq) {
								for (ReceiptAllocationDetail finReceiptAllocationDetail : finReceiptAllocDetails) {

									if (rhReceiptID == finReceiptAllocationDetail.getReceiptID()
											&& StringUtils.equalsIgnoreCase("TDS",
													finReceiptAllocationDetail.getAllocationType())
											&& finReceiptAllocationDetail.getPaidAmount()
													.compareTo(BigDecimal.ZERO) > 0) {

										soaTranReport = new SOATransactionReport();
										soaTranReport.setEvent(rHTdsAdjust + finRef);
										soaTranReport.setTransactionDate(receiptDate);
										soaTranReport.setValueDate(receiptDate);
										soaTranReport.setCreditAmount(finReceiptAllocationDetail.getPaidAmount());
										soaTranReport.setDebitAmount(BigDecimal.ZERO);
										soaTranReport.setPriority(21);
										soaTransactionReports.add(soaTranReport);
									}
								}
							}

							//Receipt Header with Manual Advise 
							if (StringUtils.equals(finReceiptHeader.getReceiptMode(), rpaymentType)) {

								for (ManualAdvise manualAdvise : manualAdviseList) {
									rHPaymentBouncedFor = "Payment Bounced For "; //9
									if (rhReceiptID == manualAdvise.getReceiptID()) {

										if (StringUtils.equals(receiptModeStatus, "B")
												&& manualAdvise.getAdviseType() == 1
												&& manualAdvise.getBounceID() > 0) {

											soaTranReport = new SOATransactionReport();
											if (!(StringUtils.equals("EXCESS", rpaymentType)
													|| StringUtils.equals("CASH", rpaymentType))) {
												rHPaymentBouncedFor = rHPaymentBouncedFor.concat(rpaymentType + "No.:");
												if (StringUtils.isNotBlank(favourNumber)) {
													rHPaymentBouncedFor = rHPaymentBouncedFor.concat(favourNumber);
												}
												rHPaymentBouncedFor = rHPaymentBouncedFor + finRef;
											} else if (StringUtils.equals("CASH", rpaymentType)) {
												rHPaymentBouncedFor = "Cash Bounced For Receipt No.";
												if (StringUtils.isNotBlank(finReceiptDetail.getPaymentRef())) {
													rHPaymentBouncedFor = rHPaymentBouncedFor
															.concat(finReceiptDetail.getPaymentRef());
												}
												rHPaymentBouncedFor = rHPaymentBouncedFor.concat(finRef);
											}
											soaTranReport.setEvent(rHPaymentBouncedFor);
											soaTranReport.setTransactionDate(receiptDate);
											soaTranReport.setValueDate(finReceiptHeader.getBounceDate());
											soaTranReport.setCreditAmount(BigDecimal.ZERO);
											soaTranReport.setDebitAmount(finReceiptDetail.getAmount());
											soaTranReport.setPriority(11);
											soaTransactionReports.add(soaTranReport);
										}
									}
								}
							}

							if (finRepayHeadersList != null && !finRepayHeadersList.isEmpty()) {

								for (FinRepayHeader finRepayHeader : finRepayHeadersList) {

									if (finReceiptDetail.getReceiptSeqID() == finRepayHeader.getReceiptSeqID()) {

										BigDecimal totalTdsSchdPayNow = BigDecimal.ZERO;
										for (RepayScheduleDetail finRpySchdDetail : finRepaySchdDetails) {

											if (finRpySchdDetail.getRepayID() == finRepayHeader.getRepayID()) {

												if (finRpySchdDetail.getTdsSchdPayNow() != null && finRpySchdDetail
														.getTdsSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
													totalTdsSchdPayNow = totalTdsSchdPayNow
															.add(finRpySchdDetail.getTdsSchdPayNow());

												}
												//Interest from customer Waived Off
												BigDecimal pftSchdWaivedNow = finRpySchdDetail.getPftSchdWaivedNow();
												if (pftSchdWaivedNow != null
														&& pftSchdWaivedNow.compareTo(BigDecimal.ZERO) > 0) {
													soaTranReport = new SOATransactionReport();
													soaTranReport.setEvent(rHPftWaived + finRef);
													soaTranReport.setTransactionDate(receivedDate);
													soaTranReport.setValueDate(finReceiptDetail.getValueDate());
													soaTranReport.setCreditAmount(pftSchdWaivedNow);
													soaTranReport.setDebitAmount(BigDecimal.ZERO);
													soaTranReport.setPriority(4);
													soaTransactionReports.add(soaTranReport);

												}
												//Principal from customer Waived Off
												BigDecimal principalSchdPayNow = finRpySchdDetail
														.getPrincipalSchdPayNow();
												if (principalSchdPayNow != null
														&& principalSchdPayNow.compareTo(BigDecimal.ZERO) > 0) {
													soaTranReport = new SOATransactionReport();
													soaTranReport.setEvent(rHPriWaived + finRef);
													soaTranReport.setTransactionDate(receivedDate);
													soaTranReport.setValueDate(finReceiptDetail.getValueDate());
													soaTranReport.setCreditAmount(principalSchdPayNow);
													soaTranReport.setDebitAmount(BigDecimal.ZERO);
													soaTranReport.setPriority(5);
													soaTransactionReports.add(soaTranReport);

												}
												//Penalty from customer Waived Off
												BigDecimal waivedAmt = finRpySchdDetail.getWaivedAmt();
												if ((StringUtils.isBlank(closingStatus)
														|| !StringUtils.equalsIgnoreCase(closingStatus, "C"))
														&& waivedAmt != null
														&& waivedAmt.compareTo(BigDecimal.ZERO) > 0) {
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

										if ((StringUtils.equalsIgnoreCase(finReceiptDetail.getStatus(), "B")
												|| StringUtils.equalsIgnoreCase(finReceiptDetail.getStatus(), "C"))
												&& tdsEntryReq) {
											//TDS Adjustment Reversal 
											if (totalTdsSchdPayNow.compareTo(BigDecimal.ZERO) > 0) {
												soaTranReport = new SOATransactionReport();
												soaTranReport.setEvent(rHTdsAdjustReversal + finRef);
												soaTranReport.setTransactionDate(finReceiptHeader.getReceiptDate());
												soaTranReport.setValueDate(finReceiptHeader.getBounceDate());
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
				}
			}

			if (StringUtils.isBlank(closingStatus) || !StringUtils.equalsIgnoreCase(closingStatus, "C")) {

				//Fin Fee Details List
				if (finFeedetailsList != null && !finFeedetailsList.isEmpty()) {

					// VAS Recordings
					List<VASRecording> VASRecordingsList = getVASRecordingsList(finReference);

					// Fin fee schedule details
					List<FinFeeScheduleDetail> finFeeScheduleDetailsList = getFinFeeScheduleDetailsList(finReference);

					for (FinFeeDetail finFeeDetail : finFeedetailsList) {
						finFeeDetailOrgination = " Amount";
						finFeeDetailNotInDISBorPOSP = " Amount"; //15
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

							// if (finFeeDetail.isOriginationFee()) {} Removed this as part of Advance Interest/Advance EMI Changes.

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
								} else {
									finFeeDetailNotInDISBorPOSP = vasProduct;
								}
								soaTranReport.setEvent(finFeeDetailNotInDISBorPOSP + " Amount" + finRef);
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

					//Fin Fee Schedule Details List 
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

			//LPP and LPI waived from the waiver screen
			if (feeWaiverDetailList != null && !feeWaiverDetailList.isEmpty()) {
				for (FeeWaiverDetail waiverDetail : feeWaiverDetailList) {
					String lable = "";
					if (RepayConstants.ALLOCATION_ODC.equalsIgnoreCase(waiverDetail.getFeeTypeCode())) {
						lable = lppWaived;
					} else if (RepayConstants.ALLOCATION_LPFT.equalsIgnoreCase(waiverDetail.getFeeTypeCode())) {
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

			//Advance EMI should be shown on transaction as total disbursement, advance emi debit entry.
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
			if (finMain != null && FinanceConstants.PRODUCT_CD.equals(finMain.getFinCategory())) {
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

			//AdvanceEMI credit entry with maturity date
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

			//LPP Penalty Accrued on Month-End date
			List<FinODAmzTaxDetail> finODAmzTaxDetailList = getFinODAmzTaxDetailDAO()
					.getFinODAmzTaxDetail(finReference);
			if (finODAmzTaxDetailList != null && !finODAmzTaxDetailList.isEmpty()) {
				for (FinODAmzTaxDetail finODAmzTaxDetail : finODAmzTaxDetailList) {
					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(
							penalityMnthEnd + DateUtility.format(finODAmzTaxDetail.getValueDate(), "dd/MM/yyyy"));
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

			//LPP Penalty UnAccrued Paid on Receipts
			List<FinTaxIncomeDetail> incomeList = getFinODAmzTaxDetailDAO().getFinTaxIncomeList(finReference, "LPP");
			if (incomeList != null && !incomeList.isEmpty()) {
				for (FinTaxIncomeDetail detail : incomeList) {
					soaTranReport = new SOATransactionReport();
					soaTranReport.setEvent(penaltyUnAccrued + DateUtility.format(detail.getValueDate(), "dd/MM/yyyy"));
					soaTranReport.setTransactionDate(detail.getPostDate());
					soaTranReport.setValueDate(detail.getValueDate());
					soaTranReport.setDebitAmount(detail.getReceivedAmount());
					soaTranReport.setCreditAmount(BigDecimal.ZERO);
					soaTranReport.setPriority(27);
					soaTransactionReports.add(soaTranReport);
				}
			}

		}

		logger.debug("Leaving");
		return soaTransactionReports;
	}

	private BigDecimal getWaivedGST(TaxHeader taxHeader) {
		BigDecimal waivedGST = BigDecimal.ZERO;
		if (taxHeader == null || CollectionUtils.isEmpty(taxHeader.getTaxDetails())) {
			return waivedGST;
		}

		BigDecimal cgstWaived = BigDecimal.ZERO;
		BigDecimal sgstWaived = BigDecimal.ZERO;
		BigDecimal igstWaived = BigDecimal.ZERO;
		BigDecimal ugstWaived = BigDecimal.ZERO;
		BigDecimal cessWaived = BigDecimal.ZERO;

		for (Taxes taxes : taxHeader.getTaxDetails()) {
			switch (taxes.getTaxType()) {
			case RuleConstants.CODE_CGST:
				cgstWaived = cgstWaived.add(taxes.getWaivedTax());
				break;
			case RuleConstants.CODE_SGST:
				sgstWaived = sgstWaived.add(taxes.getWaivedTax());
				break;
			case RuleConstants.CODE_UGST:
				ugstWaived = ugstWaived.add(taxes.getWaivedTax());
				break;
			case RuleConstants.CODE_IGST:
				igstWaived = igstWaived.add(taxes.getWaivedTax());
				break;
			case RuleConstants.CODE_CESS:
				cessWaived = cessWaived.add(taxes.getWaivedTax());
				break;
			default:
				break;
			}
		}

		waivedGST = cgstWaived.add(sgstWaived).add(ugstWaived).add(igstWaived).add(cessWaived);
		return waivedGST;
	}

	@Override
	public EventProperties getEventPropertiesList(String configName) {
		return this.soaReportGenerationDAO.getEventPropertiesList(configName);
	}

	@Override
	public List<String> getSOAFinTypes() {

		return soaReportGenerationDAO.getSOAFinTypes();
	}

	/**
	 * 
	 * @deprecated The logic in the below method is moved to <{@link GSTCalculator#getTaxPercentages(String)}
	 */
	public Map<String, BigDecimal> getTaxPercentages(FinanceMain finMain) {

		//FinanceDetail financeDetail = financeDetailService.getFinSchdDetailById(finMain.FinReference(), "", false);

		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.getFinScheduleData().setFinanceMain(finMain);

		// Map Preparation for Executing GST rules
		Map<String, Object> dataMap = GSTCalculator.getGSTDataMap(finMain.getFinReference());

		List<Rule> rules = getRuleDAO().getGSTRuleDetails(RuleConstants.MODULE_GSTRULE, "");
		String finCcy = financeDetail.getFinScheduleData().getFinanceMain().getFinCcy();

		BigDecimal totalTaxPerc = BigDecimal.ZERO;
		Map<String, BigDecimal> taxPercMap = new HashMap<>();
		taxPercMap.put(RuleConstants.CODE_CGST, BigDecimal.ZERO);
		taxPercMap.put(RuleConstants.CODE_IGST, BigDecimal.ZERO);
		taxPercMap.put(RuleConstants.CODE_SGST, BigDecimal.ZERO);
		taxPercMap.put(RuleConstants.CODE_UGST, BigDecimal.ZERO);
		taxPercMap.put(RuleConstants.CODE_CESS, BigDecimal.ZERO);

		for (Rule rule : rules) {
			BigDecimal taxPerc = BigDecimal.ZERO;
			switch (rule.getRuleCode()) {
			case RuleConstants.CODE_CGST:
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_CGST, taxPerc);
				break;
			case RuleConstants.CODE_SGST:
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_SGST, taxPerc);
				break;
			case RuleConstants.CODE_IGST:
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_IGST, taxPerc);
				break;
			case RuleConstants.CODE_UGST:
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_UGST, taxPerc);
				break;
			case RuleConstants.CODE_CESS:
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_CESS, taxPerc);
				break;
			default:

			}
		}
		taxPercMap.put(RuleConstants.CODE_TOTAL_GST, totalTaxPerc);

		return taxPercMap;
	}

	/**
	 * Method for Processing of SQL Rule and get Executed Result
	 * 
	 * @return
	 */
	private BigDecimal getRuleResult(String sqlRule, Map<String, Object> executionMap, String finCcy) {
		logger.debug("Entering");

		BigDecimal result = BigDecimal.ZERO;
		try {
			Object exereslut = getRuleExecutionUtil().executeRule(sqlRule, executionMap, finCcy,
					RuleReturnType.DECIMAL);
			if (exereslut == null || StringUtils.isEmpty(exereslut.toString())) {
				result = BigDecimal.ZERO;
			} else {
				result = new BigDecimal(exereslut.toString());
			}
		} catch (Exception e) {
			logger.debug(e);
		}

		logger.debug("Leaving");
		return result;
	}
	
	private String buildAllocationData (List<ReceiptAllocationDetail> radList,int formatter){
		StringBuffer data = new StringBuffer();
		Map<String,BigDecimal> allocMap = new LinkedHashMap<String, BigDecimal>();
		if (CollectionUtils.isNotEmpty(radList)){
			for (ReceiptAllocationDetail rad : radList){
				String allocType = rad.getAllocationType();
				String allocTypeMsg = "";
				BigDecimal paidAmount = rad.getPaidAmount();
				if (RepayConstants.ALLOCATION_EMI.equals(allocType)){
					allocTypeMsg = "EMI Adjusted";
				}else if (RepayConstants.ALLOCATION_MANADV.equals(allocType)){
					allocTypeMsg = rad.getTypeDesc();
				}else if (RepayConstants.ALLOCATION_BOUNCE.equals(allocType)){
					allocTypeMsg = "Bounce Charges";
				}else if (RepayConstants.ALLOCATION_FUT_PRI.equals(allocType)){
					allocTypeMsg = "Principal";
				}else if (RepayConstants.ALLOCATION_FUT_NPFT.equals(allocType)){
					allocTypeMsg = "Interest";
				}else if (RepayConstants.ALLOCATION_PP.equals(allocType)){
					allocTypeMsg = "Principal";
				}else if (RepayConstants.ALLOCATION_FEE.equals(allocType)){
					allocTypeMsg = "Fees";
				}
				if (!allocTypeMsg.isEmpty()){
					if (allocMap.containsKey(allocTypeMsg)){
						paidAmount = paidAmount.add(allocMap.get(allocTypeMsg));
					}
					allocMap.put(allocTypeMsg, paidAmount);
				}
				
			}
			
		}
		data = data.append("\n");
		for (Map.Entry<String, BigDecimal> entry: allocMap.entrySet()){
			data = data.append(entry.getKey()).append(" : ").append(PennantApplicationUtil.formateAmount(entry.getValue(), formatter));
			data = data.append("\n");
		}
		return data.toString();
	}

	private BigDecimal getGstAmount(String manualAdviseMovementEvent, ManualAdviseMovements manualAdviseMovements,
			ManualAdvise advise) {
		BigDecimal waivedGst = BigDecimal.ZERO;
		if (manualAdviseMovements.getTaxHeaderId() != null) {
			List<Taxes> taxDetails = getTaxHeaderDetailsDAO().getTaxDetailById(manualAdviseMovements.getTaxHeaderId(),
					"");
			if (advise.getAdviseID() == manualAdviseMovements.getAdviseID()
					&& !StringUtils.containsIgnoreCase(manualAdviseMovementEvent, PennantConstants.FEETYPE_BOUNCE)) {
				FeeType feeType = getFeeTypeDAO().getFeeTypeById(advise.getFeeTypeID(), "");
				if (StringUtils.equals(feeType.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
					for (Taxes taxes : taxDetails) {
						BigDecimal waivedTax = taxes.getWaivedTax();
						waivedGst = waivedGst.add(waivedTax);
					}
				}
			} else {
				for (Taxes taxes : taxDetails) {
					BigDecimal waivedTax = taxes.getWaivedTax();
					waivedGst = waivedGst.add(waivedTax);
				}
			}
		}
		return waivedGst;
	}

	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
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

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
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

}