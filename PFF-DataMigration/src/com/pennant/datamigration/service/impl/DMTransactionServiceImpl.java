package com.pennant.datamigration.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinPlanEmiHolidayDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.payment.PaymentDetailDAO;
import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.datamigration.constants.MigrationConstants;
import com.pennant.datamigration.dao.BasicLoanReconDAO;
import com.pennant.datamigration.model.BasicLoanRecon;
import com.pennant.datamigration.model.FeeTypeVsGLMapping;
import com.pennant.datamigration.model.MigrationData;
import com.pennant.datamigration.model.ReferenceID;
import com.pennant.datamigration.model.SourceReport;
import com.pennant.datamigration.service.DMTransactionService;
import com.pennanttech.pff.core.TableType;

public class DMTransactionServiceImpl implements DMTransactionService {
	private static Logger logger = Logger.getLogger(DMTransactionServiceImpl.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ReceiptAllocationDetailDAO receiptAllocationDetailDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private PaymentHeaderDAO paymentHeaderDAO;
	private PaymentDetailDAO paymentDetailDAO;
	private PaymentInstructionDAO paymentInstructionDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private ProvisionDAO provisionDAO;
	private FinFeeDetailDAO finFeeDetailDAO;
	private FinFeeScheduleDetailDAO finFeeScheduleDetailDAO;
	private FinPlanEmiHolidayDAO finPlanEmiHolidayDAO;
	private MandateDAO mandateDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private JountAccountDetailDAO jountAccountDetailDAO;
	private GuarantorDetailDAO guarantorDetailDAO;
	private LatePayMarkingService latePayMarkingService;

	// Data Not Received???
	private OverdueChargeRecoveryDAO recoveryDAO;
	private FinStatusDetailDAO finStatusDetailDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private FinanceTypeDAO financeTypeDAO;
	private RepayInstructionDAO repayInstructionDAO;

	private BasicLoanReconDAO basicLoanReconDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private PostingsDAO postingsDAO;

	public Logger getLogger() {
		return logger;
	}

	// Setting Finance Details
	public MigrationData getFinanceDetails(MigrationData sMD, ReferenceID rid, String type) throws Exception {
		logger.debug("Entering");
		MigrationData dMD = new MigrationData();
		dMD.setSourceReport(basicLoanReconDAO.getSourceReportDetails(sMD.getFinanceMain().getFinReference()));

		/*if (StringUtils.equals(sMD.getFinanceMain().getFinReference(), "ELPNADNM0002736")
				|| StringUtils.equals(sMD.getFinanceMain().getFinReference(), "SEPNMLE0117650")) {
			dMD.setWorkOnEMI(true);
		} else {
			dMD.setWorkOnEMI(false);
		}*/

		// Reset Fee Received
		List<FeeTypeVsGLMapping> feeGLList = rid.getFeeVsGLList();
		for (int i = 0; i < feeGLList.size(); i++) {
			feeGLList.get(i).setFeePaid(BigDecimal.ZERO);
		}

		rid.setBounceReceived(BigDecimal.ZERO);
		rid.setOdcReceived(BigDecimal.ZERO);
		rid.setTotalBankAmount(BigDecimal.ZERO);

		// sMD.setFinScheduleDetails(sortSchdDetails(sMD.getFinScheduleDetails()));
		dMD = setFinanceMain(sMD, dMD);
		dMD = setDisbursements(sMD, dMD, rid);
		dMD = setSchedule(sMD, dMD, rid);
		dMD = setReceipts(sMD, dMD, rid);
		dMD = setFinPftDetails(dMD, rid);
		dMD.setPenaltyrate(sMD.getPenaltyrate());
		dMD.getPenaltyrate().setFinEffectDate(dMD.getFinanceMain().getFinStartDate());
		dMD = recalOverdue(dMD, rid);
		dMD = setBasicLoanRecon(sMD, dMD, rid);
		dMD = addFutureManualAdvises(sMD, dMD, rid);
		dMD = addPostingEntries(dMD, rid);
		
		dMD = setFinanceStatus(dMD);

		logger.debug("Leaving");
		return dMD;
	}

	// ---------------------------------------------------------------------------
	// Setting Finance Main
	// ---------------------------------------------------------------------------
	public MigrationData setFinanceMain(MigrationData sMD, MigrationData dMD) {
		logger.debug("Entering");

		FinanceMain sFM = sMD.getFinanceMain();
		FinanceMain dFM = new FinanceMain();

		// Basic Details
		//-----------------------------------
		dFM.setFinReference(sFM.getFinReference());
		dFM.setCustID(sFM.getCustID());
		dFM.setFinBranch(sFM.getFinBranch());
		dFM.setFinSourceID(MigrationConstants.SOURCE_ID);//Overwritten
		dFM.setFinType(sFM.getFinType());
		dFM.setFinCcy(sFM.getFinCcy());
		dFM.setFinStartDate(sFM.getFinStartDate());
		dFM.setFinContractDate(sFM.getFinContractDate());
		dFM.setFinAssetValue(sFM.getFinAssetValue());
		dFM.setFinCategory(sFM.getFinCategory());
		dFM.setProductCategory(FinanceConstants.PRODUCT_CONVENTIONAL);//Overwritten
		dFM.setLovDescEntityCode(sFM.getLovDescEntityCode());
		
		dFM.setAccountsOfficer(sFM.getAccountsOfficer());
		dFM.setDsaCode(sFM.getDsaCode());
		dFM.setApplicationNo(sFM.getApplicationNo());
		dFM.setReferralId(sFM.getReferralId());
		dFM.setDmaCode(sFM.getDmaCode());
		dFM.setSalesDepartment(sFM.getSalesDepartment());

		// Grace Period Details
		//-----------------------------------
		dFM.setAllowGrcPeriod(false);
		dFM.setGraceTerms(0);
		dFM.setGrcPftFrq("");
		dFM.setNextGrcPftDate(null);
		dFM.setGrcCpzFrq("");
		dFM.setNextGrcCpzDate(null);
		dFM.setGrcPftRvwFrq("");
		dFM.setNextGrcPftRvwDate(null);
		dFM.setAllowGrcRepay(false);
		dFM.setGraceBaseRate(null);
		dFM.setGraceSpecialRate(null);
		dFM.setGrcMargin(BigDecimal.ZERO);
		dFM.setGrcAdvBaseRate(null);
		dFM.setGrcAdvMargin(BigDecimal.ZERO);
		dFM.setGrcAdvPftRate(BigDecimal.ZERO);
		dFM.setGrcPftRate(BigDecimal.ZERO);
		dFM.setGrcPeriodEndDate(sFM.getFinStartDate());
		dFM.setGrcProfitDaysBasis(MigrationConstants.GRC_PFT_DAYS_BASIS);//Overwritten

		// Repay Period Details
		//-----------------------------------
		dFM.setRecalType(MigrationConstants.RECAL_TYPE);//Overwritten
		dFM.setRepayBaseRate(sFM.getRepayBaseRate());
		dFM.setRepaySpecialRate(sFM.getRepaySpecialRate());
		dFM.setRepayMargin(sFM.getRepayMargin());
		dFM.setRepayProfitRate(sFM.getRepayProfitRate());
		dFM.setFinRepayPftOnFrq(true);
		dFM.setRepayFrq(sFM.getRepayFrq());
		dFM.setRepayPftFrq(sFM.getRepayPftFrq());
		dFM.setNextRepayDate(dFM.getFinStartDate());
		dFM.setNextRepayPftDate(dFM.getFinStartDate());

		// Set same as Repay Frequency
		dFM.setAllowRepayRvw(sFM.isAllowRepayRvw());
		if (dFM.isAllowRepayRvw()) {
			dFM.setRepayRvwFrq(dFM.getRepayFrq());
		}
		dFM.setNextRepayRvwDate(dFM.getFinStartDate());
		dFM.setSchCalOnRvw(CalculationConstants.RPYCHG_TILLMDT);
		dFM.setRateChgAnyDay(sMD.getFinType().isRateChgAnyDay());
		
		// Set same as Repay Frequency
		dFM.setAllowRepayCpz(sFM.isAllowRepayCpz());
		if (dFM.isAllowRepayCpz()) {
			dFM.setRepayCpzFrq(dFM.getRepayFrq());
		}
		dFM.setNextRepayCpzDate(dFM.getFinStartDate());

		dFM.setNumberOfTerms(sFM.getNumberOfTerms());
		dFM.setReqRepayAmount(sFM.getReqRepayAmount());
		dFM.setMaturityDate(sFM.getMaturityDate());
		dFM.setRepayRateBasis(MigrationConstants.REPAY_RATE_BASIS);
		dFM.setScheduleMethod(sFM.getScheduleMethod());
		dFM.setProfitDaysBasis(sFM.getProfitDaysBasis());
		dFM.setFinRepayMethod(sFM.getFinRepayMethod());
		dFM.setMandateID(sFM.getMandateID());
		dFM.setTDSApplicable(sFM.istDSApplicable());
		dFM.setProfitDaysBasis(MigrationConstants.RPY_PFT_DAYS_BASIS);//Overwritten
		
		dFM.setNextDepDate(dFM.getFinStartDate());
		dFM.setNextRolloverDate(dFM.getFinStartDate());
		dFM.setLastDepDate(dFM.getFinStartDate());
		dFM.setLastRepayDate(dFM.getFinStartDate());
		dFM.setLastRepayPftDate(dFM.getFinStartDate());
		dFM.setLastRepayRvwDate(dFM.getFinStartDate());
		dFM.setLastRepayCpzDate(dFM.getFinStartDate());
		dFM.setLastDisbDate(dFM.getFinStartDate());
		
		// BPI Details
		//-----------------------------------
		dFM.setAlwBPI(sFM.isAlwBPI());
		if(dFM.isAlwBPI()){
			dFM.setBpiTreatment(sFM.getBpiTreatment());
			dFM.setBpiPftDaysBasis(MigrationConstants.BPI_PFT_DAYS_BASIS);//Overwritten
			dFM.setBpiAmount(sFM.getBpiAmount());
		}
		
		dFM.setPlanEMIHAlw(false);
		dFM.setPlanEMIHMethod(null);
		dFM.setPlanEMIHMaxPerYear(0);
		dFM.setPlanEMIHMax(0);
		dFM.setPlanEMIHLockPeriod(0);
		dFM.setPlanEMICpz(false);
		dFM.setUnPlanEMIHLockPeriod(0);
		dFM.setMaxUnplannedEmi(0);
		dFM.setMaxReAgeHolidays(0);
		dFM.setUnPlanEMICpz(false);
		dFM.setReAgeCpz(false);
		dFM.setPromotionCode("");

		// Additional Parameters
		//-----------------------------------
		dFM.setReqMaturity(sFM.getReqMaturity());
		dFM.setCalTerms(sFM.getCalTerms());
		dFM.setCalMaturity(sFM.getCalMaturity());
		dFM.setClosingStatus(sFM.getClosingStatus());
		dFM.setFinApprovedDate(dFM.getFinStartDate());
		dFM.setFinIsActive(sFM.isFinIsActive());
		dFM.setMigratedFinance(true);
		dFM.setScheduleMaintained(sFM.isScheduleMaintained());
		dFM.setScheduleRegenerated(sFM.isScheduleRegenerated());
		dFM.setScheduleChange(sFM.isScheduleChange());
		dFM.setFinPurpose(sFM.getFinPurpose());
		if (sFM.getCustDSR() == null) {
			dFM.setCustDSR(BigDecimal.ZERO);
		} else {
			dFM.setCustDSR(sFM.getCustDSR());
		}
		dFM.setJointCustId(sFM.getJointCustId());
		dFM.setJointAccount(sFM.isJointAccount());
		dFM.setSecurityDeposit(sFM.getSecurityDeposit());

		// Workflow Parameters
		//-----------------------------------
		dFM.setVersion(sFM.getVersion());
		dFM.setLastMntBy(sFM.getLastMntBy());
		dFM.setLastMntOn(sFM.getLastMntOn());
		dFM.setRecordStatus(sFM.getRecordStatus());
		dFM.setRoleCode(sFM.getRoleCode());
		dFM.setNextRoleCode(sFM.getNextRoleCode());
		dFM.setRecordType(sFM.getRecordType());
		dFM.setInitiateUser(sFM.getInitiateUser());
		if (sFM.getInitiateDate() == null) {
			dFM.setInitiateDate(dFM.getFinStartDate());
		} else {
			dFM.setInitiateDate(sFM.getInitiateDate());
		}

		// FIXME: Derived from Disbursement Count
		dFM.setAlwMultiDisb(false);

		// Overwrite Additional Parameters
		dFM.setCalRoundingMode(MigrationConstants.ROUNDING_MODE);
		dFM.setRoundingTarget(MigrationConstants.ROUNDING_TARGET);

		dMD.setFinanceMain(dFM);

		logger.debug("Leaving");
		return dMD;
	}

	// ---------------------------------------------------------------------------
	// Setting Disbursements, FinAdvance Payments, Fee Details
	// ---------------------------------------------------------------------------
	public MigrationData setDisbursements(MigrationData sMD, MigrationData dMD, ReferenceID rid) {
		logger.debug("Entering");
		
		List<FinanceDisbursement> sFddList = sMD.getFinDisbursements();
		List<FinanceDisbursement> dFddList = dMD.getFinDisbursements();
		FinanceMain dFM = dMD.getFinanceMain();

		Date prvDate = dMD.getFinanceMain().getFinStartDate();
		prvDate = DateUtility.addDays(prvDate, -1);
		int seq = 0;

		// Sort Disbursement Details
		if (sFddList.size() > 1) {
			sFddList = sortDisbDetails(sFddList);
		}

		// Add Destination Disbursement Details
		dFM.setFinAmount(BigDecimal.ZERO);
		dFM.setFinCurrAssetValue(BigDecimal.ZERO);
		for (int i = 0; i < sFddList.size(); i++) {
			FinanceDisbursement sFdd = sFddList.get(i);

			// Do not take cancelled disbursements
			if (!sFdd.isDisbDisbursed() || StringUtils.equals(DisbursementConstants.STATUS_CANCEL, sFdd.getDisbStatus())) {
				continue;
			}

			// New Disbursement Date
			seq = seq + 1;

			dFddList = addNewDisb(sFdd, dFddList, seq);
			prvDate = sFdd.getDisbDate();

			// To overcome issue with finstart date in few loans
			if (seq == 1) {
				dFM.setFinStartDate(sFdd.getDisbDate());
			}

			// Finance Amount
			if (DateUtility.compare(sFdd.getDisbDate(), dFM.getFinStartDate()) == 0) {
				dFM.setFinAmount(dFM.getFinAmount().add(sFdd.getDisbAmount()));
			}else{
				dFM.setScheduleMaintained(true);
			}

			// Current Finance Amount
			sFdd.setFeeChargeAmt(BigDecimal.ZERO);
			dFM.setFinCurrAssetValue(dFM.getFinCurrAssetValue().add(sFdd.getDisbAmount()));
		}

		// Multi Disbursed or NOT
		if (dFM.getFinAmount().compareTo(dFM.getFinCurrAssetValue()) < 0) {
			dFM.setAlwMultiDisb(true);
		}

		// FinAdvance Payments
		List<FinAdvancePayments> sFapList = sMD.getFinAdvancePayments();

		for (int i = 0; i < sFapList.size(); i++) {
			FinAdvancePayments dFap = sFapList.get(i);

			// Ignore cancelled Instructions
			if (StringUtils.equals(dFap.getStatus(), "C")) {
				continue;
			}

			for (int j = 0; j < dFddList.size(); j++) {
				FinanceDisbursement dFdd = dFddList.get(j);

				// Set Disbursement Sequence
				if (dFap.getLlDate().compareTo(dFdd.getDisbDate()) == 0) {
					dFap.setDisbSeq(dFdd.getDisbSeq());
					dFap.setValueDate(dFap.getLlDate());

					dMD.getFinAdvancePayments().add(dFap);

					// FIXME: Why DisbDate is there in table but not in bean
					// dFap.setDisbDate(dFap.getLlDate());
				}
			}
		}

		// FinFeeDetail
		List<FinFeeDetail> dFfdList = sMD.getFinFeeDetails();
		dFM.setDeductFeeDisb(BigDecimal.ZERO);
		dFM.setFeeChargeAmt(BigDecimal.ZERO);
		for (int i = 0; i < dFfdList.size(); i++) {
			FinFeeDetail ffd = dFfdList.get(i);
			ffd.setFeeSeq(i);
			ffd.setFeeOrder(i);

			if (StringUtils.isBlank(ffd.getCalculationType())) {
				ffd.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT);
			}

			ffd.setMaxWaiverPerc(BigDecimal.ZERO);
			ffd.setTaxPercent(BigDecimal.ZERO);
			ffd.setPostDate(dFM.getFinStartDate());

			// FIXME: Check when GST is Available
			if (StringUtils.equals(ffd.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
				ffd.setNetAmount(ffd.getActualAmount().subtract(ffd.getWaivedAmount()));
				ffd.setNetAmountOriginal(ffd.getActualAmount().subtract(ffd.getWaivedAmount()));
				ffd.setRemainingFee(ffd.getActualAmount().subtract(ffd.getWaivedAmount()));
				ffd.setRemainingFeeOriginal(ffd.getActualAmount().subtract(ffd.getWaivedAmount()));
				ffd.setActualAmountOriginal(ffd.getActualAmount());
				ffd.setPaidAmount(BigDecimal.ZERO);
				ffd.setPaidAmountOriginal(BigDecimal.ZERO);
				
				dFM.setDeductFeeDisb(dFM.getDeductFeeDisb().add(ffd.getNetAmount()));
			}
			
			// FIXME: Check when GST is Available
			if (StringUtils.equals(ffd.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				ffd.setNetAmount(ffd.getActualAmount().subtract(ffd.getWaivedAmount()));
				ffd.setNetAmountOriginal(ffd.getActualAmount().subtract(ffd.getWaivedAmount()));
				ffd.setActualAmountOriginal(ffd.getActualAmount());
				ffd.setPaidAmountOriginal(ffd.getPaidAmount());
			}
			
			// Calculating Percentage of Fee calculation in Reverse Order
			if (StringUtils.equals(PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE,ffd.getCalculationType())) {
				
				BigDecimal calculatedAmt = BigDecimal.ZERO;
				switch (ffd.getCalculateOn()) {
				case PennantConstants.FEE_CALCULATEDON_TOTALASSETVALUE:
					calculatedAmt = dFM.getFinAssetValue();
					break;
				case PennantConstants.FEE_CALCULATEDON_LOANAMOUNT:
					calculatedAmt = dFM.getFinAmount().subtract(dFM.getDownPayment());
					break;
				default:
					break;
				}
				
				if(calculatedAmt.compareTo(BigDecimal.ZERO) > 0){
					BigDecimal perc = (ffd.getNetAmount().divide(calculatedAmt, 2, RoundingMode.HALF_DOWN)).multiply(BigDecimal.valueOf(100));
					ffd.setPercentage(perc);
				}
					
			}

			// Add Fee and Charges to Disbursement Detail
			for (int j = 0; j < dFddList.size(); j++) {
				FinanceDisbursement fdd = dFddList.get(j);

				if (fdd.getDisbDate().compareTo(ffd.getPostDate()) != 0) {
					continue;
				}

				if (StringUtils.equals(ffd.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					dFM.setFeeChargeAmt(dFM.getFeeChargeAmt().add(ffd.getPaidAmount()));
					fdd.setFeeChargeAmt(fdd.getFeeChargeAmt().add(ffd.getPaidAmount()));
				}

				// Add Paid Fee Amounts
				if (!fdd.isDisbDisbursed() || !StringUtils.isBlank(fdd.getDisbStatus())) {
					continue;
				}

				for (int k = 0; k < rid.getFeeVsGLList().size(); k++) {
					FeeTypeVsGLMapping ftg = rid.getFeeVsGLList().get(k);
					if (ffd.getFeeTypeID() == ftg.getFeeTypeID()) {
						ftg.setFeePaid(ftg.getFeePaid().add(ffd.getPaidAmount()));
					}

					if (ftg.getFeeTypeID() >= ffd.getFeeTypeID()) {
						break;
					}
				}
			}
		}
		
		// Reset Dates for Start Date
		dFM.setNextRepayDate(dFM.getFinStartDate());
		dFM.setNextRepayPftDate(dFM.getFinStartDate());
		dFM.setNextRepayRvwDate(dFM.getFinStartDate());
		dFM.setNextRepayCpzDate(dFM.getFinStartDate());
		dFM.setNextDepDate(dFM.getFinStartDate());
		dFM.setNextRolloverDate(dFM.getFinStartDate());
		dFM.setLastDepDate(dFM.getFinStartDate());
		dFM.setLastRepayDate(dFM.getFinStartDate());
		dFM.setLastRepayPftDate(dFM.getFinStartDate());
		dFM.setLastRepayRvwDate(dFM.getFinStartDate());
		dFM.setLastRepayCpzDate(dFM.getFinStartDate());
		dFM.setLastDisbDate(dFM.getFinStartDate());
		dFM.setFinApprovedDate(dFM.getFinStartDate());
		if (sMD.getFinanceMain().getInitiateDate() == null || 
				DateUtility.compare(dFM.getInitiateDate(), dFM.getFinStartDate()) > 0) {
			dFM.setInitiateDate(dFM.getFinStartDate());
		}
		
		dMD.setFinDisbursements(dFddList);
		dMD.setFinFeeDetails(dFfdList);
		dMD.setFinanceMain(dFM);
		logger.debug("Leaving");
		return dMD;
	}
	
	private List<FinanceDisbursement> sortDisbDetails(List<FinanceDisbursement> disbursements){

		if (disbursements != null && disbursements.size() > 1) {
			Collections.sort(disbursements, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement detail1, FinanceDisbursement detail2) {
					if (detail1.getDisbSeq() > detail2.getDisbSeq()) {
						return 1;
					} else if(detail1.getDisbSeq() < detail2.getDisbSeq()) {
						return -1;
					} 
					return 0;
				}
			});
		}
		return disbursements;
	}

	// Add New Disbursement to Disbursement Details
	public List<FinanceDisbursement> addNewDisb(FinanceDisbursement sFdd, List<FinanceDisbursement> dFddList, int seq) {
		logger.debug("Entering");
		FinanceDisbursement dFdd = new FinanceDisbursement();
		dFdd.setFinReference(sFdd.getFinReference());
		dFdd.setDisbDate(sFdd.getDisbDate());
		dFdd.setDisbSeq(seq);
		dFdd.setDisbDesc(sFdd.getDisbDesc());
		dFdd.setDisbAmount(sFdd.getDisbAmount());
		// dFdd.setFeeChargeAmt(sFdd.getFeeChargeAmt());

		dFdd.setDisbReqDate(sFdd.getDisbReqDate());
		dFdd.setDisbDisbursed(sFdd.isDisbDisbursed());
		dFdd.setDisbIsActive(sFdd.isDisbIsActive());
		dFdd.setDisbRemarks(sFdd.getDisbRemarks());
		dFdd.setLinkedTranId(0);
		dFdd.setVersion(sFdd.getVersion());
		dFdd.setLastMntBy(sFdd.getLastMntBy());
		dFdd.setLastMntOn(sFdd.getLastMntOn());
		dFdd.setRecordStatus(sFdd.getRecordStatus());

		dFddList.add(dFdd);

		logger.debug("Leaving");
		return dFddList;
	}

	// ---------------------------------------------------------------------------
	// Setting FinScehdule Details
	// ---------------------------------------------------------------------------
	public MigrationData setSchedule(MigrationData sMD, MigrationData dMD, ReferenceID rid) {
		logger.debug("Entering");
		FinanceMain dFM = dMD.getFinanceMain();
		List<FinanceScheduleDetail> sFsdList = sMD.getFinScheduleDetails();
		List<FinanceScheduleDetail> dFsdList = null;

		FinanceScheduleDetail sFsd = null;
		int bpiInstNumber = -1;
		Date bpiDate = dFM.getFinStartDate();

		// Find Grace Period End Date
		dFM.setGrcPeriodEndDate(dFM.getFinStartDate());

		for (int i = 0; i < sFsdList.size(); i++) {
			sFsd = sFsdList.get(i);

			// Grace period crossed
			if (sFsd.getInstNumber() > 1) {
				break;
			}

			// Not BPI installments
			if (sFsd.getInstNumber() > bpiInstNumber) {
				continue;
			}

			// BPI installment
			if (sFsd.getInstNumber() == bpiInstNumber) {
				dFM.setAlwBPI(true);
				dFM.setBpiAmount(sFsd.getRepayAmount());
				dFM.setBpiTreatment(MigrationConstants.BPI_TREATMENT);
				bpiDate = sFsd.getSchDate();
				continue;
			}

			// Grace Period End Date?
			if (sFsd.getSchDate().compareTo(sFsdList.get(i - 1).getSchDate()) > 0) {
				dFM.setGrcPeriodEndDate(sFsd.getSchDate());
			}
		}

		// Add Disbursements to Schedule
		dFsdList = addDisbSchd(sMD, dMD);

		// Allow Grace Period?
		if (dFM.getFinStartDate().compareTo(dFM.getGrcPeriodEndDate()) == 0) {
			dFM.setAllowGrcPeriod(false);
		} else {
			dFM.setAllowGrcPeriod(true);
			dFM.setGrcRateBasis(CalculationConstants.RATE_BASIS_R);
			dFM.setGrcSchdMthd(CalculationConstants.SCHMTHD_PFT);
		}

		// Add Schedule Records
		int instNo = 1;
		for (int i = 0; i < sFsdList.size(); i++) {
			sFsd = sFsdList.get(i);
			FinanceScheduleDetail fsd = new FinanceScheduleDetail();

			// Existing Schedule Record
			int index = getSchdIndex(dFsdList, sFsd.getSchDate());

			// is New Record
			if (index < 0) {
				fsd = setNewSchdRecord(dFM, sFsd);
				dFsdList.add(fsd);
				// Existing Record
			} else {
				fsd = dFsdList.get(index);
				fsd.setProfitSchd(fsd.getProfitSchd().add(sFsd.getProfitSchd()));
				fsd.setPrincipalSchd(fsd.getPrincipalSchd().add(sFsd.getPrincipalSchd()));
				fsd.setRepayAmount(fsd.getRepayAmount().add(sFsd.getRepayAmount()));
				
			}
			
			if (fsd.getSchDate().compareTo(dFM.getFinStartDate()) == 0) {
				fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_SELECT);

				if (dFM.isAllowGrcPeriod()) {
					fsd.setSchdMethod(CalculationConstants.SCHMTHD_PFT);
				} else {
					fsd.setSchdMethod(sFsd.getSchdMethod());
				}
			} else if (fsd.getSchDate().compareTo(dFM.getGrcPeriodEndDate()) < 0) {
				fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
				fsd.setSchdMethod(CalculationConstants.SCHMTHD_PFT);
			} else if (fsd.getSchDate().compareTo(dFM.getGrcPeriodEndDate()) == 0) {
				fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);

				if (dFM.isAllowGrcPeriod()) {
					fsd.setSchdMethod(CalculationConstants.SCHMTHD_PFT);
				} else {
					fsd.setSchdMethod(sFsd.getSchdMethod());
				}

			} else if (fsd.getSchDate().compareTo(dFM.getMaturityDate()) < 0) {
				fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
				fsd.setSchdMethod(dFM.getScheduleMethod());
			} else if (fsd.getSchDate().compareTo(dFM.getMaturityDate()) == 0) {
				fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);
				fsd.setSchdMethod(dFM.getScheduleMethod());
			} else {
				// No other then need to looked in to and fix
				fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_SELECT);
				fsd.setSchdMethod(sFsd.getSchdMethod());
			}
			
			if(fsd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0 && !StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)){
				fsd.setInstNumber(instNo);
				instNo = instNo + 1;
			}else{
				fsd.setInstNumber(0);
			}
		}

		// Start Schedule Calculation
		dFsdList = sortSchdDetails(dFsdList);
		dMD = calSchedule(dMD, rid);

		logger.debug("Leaving");
		return dMD;
	}

	// Add Disbursement Record to Fin Schedule
	public List<FinanceScheduleDetail> addDisbSchd(MigrationData sMD, MigrationData dMD) {
		logger.debug("Entering");
		List<FinanceScheduleDetail> dFsdList = dMD.getFinScheduleDetails();
		List<FinanceDisbursement> dFddList = dMD.getFinDisbursements();
		List<FinanceScheduleDetail> sFsdList = sMD.getFinScheduleDetails();
		FinanceMain fm = dMD.getFinanceMain();

		for (int i = 0; i < dFddList.size(); i++) {
			FinanceDisbursement fdd = dFddList.get(i);
			FinanceScheduleDetail fsd = new FinanceScheduleDetail();
			fsd.setFinReference(fdd.getFinReference());
			fsd.setSchDate(fdd.getDisbDate());
			fsd.setSchSeq(1);

			if (i == 0) {
				fsd.setRvwOnSchDate(true);
			}

			fsd.setDisbOnSchDate(true);
			fsd.setDefSchdDate(fdd.getDisbDate());
			fsd.setBaseRate(fm.getRepayBaseRate());
			fsd.setSplRate(fm.getRepaySpecialRate());
			fsd.setMrgRate(fm.getRepayMargin());

			BigDecimal actRate = BigDecimal.ZERO;
			for (int j = 0; j < sFsdList.size(); j++) {

				if (sFsdList.get(j).getActRate().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				if (actRate.compareTo(BigDecimal.ZERO) > 0) {
					if (sFsdList.get(j).getSchDate().compareTo(fdd.getDisbDate()) >= 0) {
						break;
					}
				}

				actRate = sFsdList.get(j).getActRate();
			}

			fsd.setActRate(actRate);
			fsd.setCalculatedRate(actRate);
			fsd.setDisbAmount(fdd.getDisbAmount());
			
			if(DateUtility.compare(fdd.getDisbDate(), fm.getGrcPeriodEndDate()) < 0){
				fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
				fsd.setPftDaysBasis(fm.getGrcProfitDaysBasis());
				fsd.setSchdMethod(fm.getGrcSchdMthd());
			}else if(DateUtility.compare(fdd.getDisbDate(), fm.getGrcPeriodEndDate()) == 0){
				fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);
				fsd.setPftDaysBasis(fm.getProfitDaysBasis());
				if(fm.isAllowGrcPeriod()){
					fsd.setSchdMethod(fm.getGrcSchdMthd());
				}else{
					fsd.setSchdMethod(fm.getScheduleMethod());
				}
			}else{
				fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
				fsd.setPftDaysBasis(fm.getProfitDaysBasis());
				fsd.setSchdMethod(fm.getScheduleMethod());
			}

			dFsdList.add(fsd);
		}

		logger.debug("Leaving");
		return dFsdList;
	}

	// Get Existing record index in Destination FinSchedule Details
	private int getSchdIndex(List<FinanceScheduleDetail> dFsdList, Date instDate) {
		int index = -1;
		for (int i = 0; i < dFsdList.size(); i++) {
			if (dFsdList.get(i).getSchDate().compareTo(instDate) == 0) {
				index = i;
				break;
			}

			if (dFsdList.get(i).getSchDate().compareTo(instDate) > 0) {
				break;
			}
		}

		return index;
	}

	// Set New Destination Schedule Record
	private FinanceScheduleDetail setNewSchdRecord(FinanceMain dFM, FinanceScheduleDetail sFsd) {
		FinanceScheduleDetail fsd = new FinanceScheduleDetail();

		fsd.setFinReference(sFsd.getFinReference());
		fsd.setSchDate(sFsd.getSchDate());
		fsd.setSchSeq(1);
		fsd.setDefSchdDate(fsd.getSchDate());
		// fsd.setBaseRate(sFsd.getBaseRate());
		// fsd.setSplRate(sFsd.getSplRate());
		// fsd.setMrgRate(sFsd.getMrgRate());

		fsd.setActRate(sFsd.getActRate());

		// Fix: why calculated rate is coming zerp
		fsd.setCalculatedRate(sFsd.getActRate());

		fsd.setProfitSchd(sFsd.getProfitSchd());
		fsd.setPrincipalSchd(sFsd.getPrincipalSchd());
		fsd.setRepayAmount(sFsd.getRepayAmount());
		// fsd.setSchdPftPaid(sFsd.getSchdPftPaid());
		// fsd.setSchdPriPaid(sFsd.getSchdPriPaid());

		// Equitas BPI period IDB
		if (sFsd.getInstNumber() == (-1)) {
			fsd.setPftDaysBasis(dFM.getBpiPftDaysBasis());
			fsd.setBpiOrHoliday(FinanceConstants.FLAG_BPI);
		} else {
			if (fsd.getSchDate().compareTo(dFM.getGrcPeriodEndDate()) < 0) {
				fsd.setPftDaysBasis(dFM.getGrcProfitDaysBasis());
			}else{
				fsd.setPftDaysBasis(dFM.getProfitDaysBasis());
			}
			fsd.setBpiOrHoliday("");
		}

		fsd.setOrgPft(sFsd.getProfitSchd());
		fsd.setOrgPri(sFsd.getPrincipalSchd());
		fsd.setInstNumber(sFsd.getInstNumber());

		fsd.setPresentmentId(sFsd.getPresentmentId());

		return fsd;
	}

	// -----------------------------------------------------------------------------------
	// Schedule Calculation
	// -----------------------------------------------------------------------------------

	public MigrationData calSchedule(MigrationData dMD, ReferenceID rid) {
		logger.debug("Entering");
		FinanceMain dFM = dMD.getFinanceMain();
		List<FinanceScheduleDetail> dFsdList = dMD.getFinScheduleDetails();
		BasicLoanRecon blr = dMD.getBasicLoanRecon();

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		FinanceScheduleDetail prvSchd = new FinanceScheduleDetail();

		boolean isFrqDate = false;
		BigDecimal zero = BigDecimal.ZERO;
		BigDecimal calInt = zero;

		String roundingMode = dFM.getCalRoundingMode();
		int roundingTarget = dFM.getRoundingTarget();

		boolean isNextGrcDateSet = false;
		boolean isNextRpyDateSet = false;
		boolean isGracePeriod = false;

		int scale = 12;
		BigDecimal bd100 = new BigDecimal(100);
		BigDecimal bd500 = new BigDecimal(500);

		BigDecimal idb30Fraction = new BigDecimal(30 / 360d);
		BigDecimal idb30FractionRounded = idb30Fraction.setScale(scale, RoundingMode.UP);

		BigDecimal calSchd = BigDecimal.ZERO;
		BigDecimal intDiff = BigDecimal.ZERO;
		BigDecimal daysFactor = BigDecimal.ZERO;
		BigDecimal prvClosingBalance = BigDecimal.ZERO;
		boolean isSkipNextCheck = false;

		dFsdList.get(0).setClosingBalance(dFsdList.get(0).getDisbAmount());
		dFsdList.get(0).setActRate(dFsdList.get(1).getActRate());
		dFsdList.get(0).setCalculatedRate(dFsdList.get(1).getCalculatedRate());
		
		dFM.setFinRepaymentAmount(BigDecimal.ZERO);
		dFM.setTotalRepayAmt(BigDecimal.ZERO);
		dFM.setTotalGracePft(BigDecimal.ZERO);

		for (int i = 1; i < dFsdList.size(); i++) {
			curSchd = dFsdList.get(i);
			prvSchd = dFsdList.get(i - 1);
			isFrqDate = false;
			isSkipNextCheck = false;

			Date curSchDate = curSchd.getSchDate();
			Date prvSchDate = prvSchd.getSchDate();

			String idb = CalculationConstants.IDB_30U360;

			if (i < (dFsdList.size() - 1)) {
				curSchd.setActRate(dFsdList.get(i + 1).getActRate());
				curSchd.setCalculatedRate(dFsdList.get(i + 1).getCalculatedRate());

				if (curSchd.getActRate().compareTo(BigDecimal.ZERO) == 0) {
					BigDecimal actRate = getActRate(dFsdList, i);
					curSchd.setActRate(actRate);
					curSchd.setCalculatedRate(actRate);
				}
			}

			// fields which are used for calculation
			prvClosingBalance = prvSchd.getClosingBalance();
			curSchd.setBalanceForPftCal(prvClosingBalance);
			curSchd.setNoOfDays(DateUtility.getDaysBetween(curSchDate, prvSchDate));

			daysFactor = CalculationUtil.getInterestDays(prvSchDate, curSchDate, idb).setScale(scale, RoundingMode.UP);

			if (daysFactor.compareTo(idb30FractionRounded) == 0) {

				calInt = CalculationUtil.calInterestWithDaysFactor(daysFactor, prvClosingBalance,
						prvSchd.getCalculatedRate());

				calInt = CalculationUtil.roundAmount(calInt, roundingMode, roundingTarget);

				calSchd = calInt.add(prvSchd.getProfitBalance());
				intDiff = calSchd.subtract(curSchd.getProfitSchd()).abs();

				// To overcome rounding differences
				if (intDiff.compareTo(bd100) == 0) {
					calInt = curSchd.getProfitSchd().subtract(prvSchd.getProfitBalance());
					blr.setInformation(blr.getInformation().concat("/Rounding Adjustment:")
							.concat(curSchd.getSchDate().toString()));
				}

				curSchd.setBpiOrHoliday("");
				isSkipNextCheck = true;

			}

			if (!isSkipNextCheck) {
				daysFactor = idb30Fraction;

				calInt = CalculationUtil.calInterestWithDaysFactor(daysFactor, prvClosingBalance,
						prvSchd.getCalculatedRate());

				calInt = CalculationUtil.roundAmount(calInt, roundingMode, roundingTarget);

				calSchd = calInt.add(prvSchd.getProfitBalance());
				intDiff = calSchd.subtract(curSchd.getProfitSchd()).abs();

				// To overcome rounding differences
				if (intDiff.compareTo(bd100) == 0) {
					calInt = curSchd.getProfitSchd().subtract(prvSchd.getProfitBalance());
					blr.setInformation(blr.getInformation().concat("/Rounding Adjustment:")
							.concat(curSchd.getSchDate().toString()));
					calSchd = calInt.add(prvSchd.getProfitBalance());
				}

				if (calSchd.compareTo(curSchd.getProfitSchd()) == 0) {
					curSchd.setBpiOrHoliday(FinanceConstants.FLAG_30DAYS_FIXED);
					isSkipNextCheck = true;
					blr.setWarnings(
							blr.getWarnings().concat("/30 Days Fixed:").concat(curSchd.getSchDate().toString()));
				}
			}

			if (!isSkipNextCheck) {
				idb = CalculationConstants.IDB_ACT_365FIXED;
				calInt = CalculationUtil.calInterest(prvSchDate, curSchDate, prvClosingBalance, idb,
						prvSchd.getCalculatedRate());

				if (calInt.compareTo(zero) != 0) {
					calInt = CalculationUtil.roundAmount(calInt, roundingMode, roundingTarget);
				}

				calSchd = calInt.add(prvSchd.getProfitBalance());
				intDiff = calSchd.subtract(curSchd.getProfitSchd()).abs();

				// To overcome rounding differences
				if (intDiff.compareTo(bd100) == 0) {
					calInt = curSchd.getProfitSchd().subtract(prvSchd.getProfitBalance());
					blr.setInformation(blr.getInformation().concat("/Rounding Adjustment:")
							.concat(curSchd.getSchDate().toString()));
				}

				if (calSchd.compareTo(curSchd.getProfitSchd()) == 0) {
					curSchd.setBpiOrHoliday(FinanceConstants.FLAG_BPI);
					isSkipNextCheck = true;
				}

			}

			curSchd.setDayFactor(daysFactor);
			curSchd.setProfitCalc(calInt);
			prvSchd.setPftDaysBasis(idb);
			curSchd.setProfitCalc(calInt);

			if (i == (dFsdList.size() - 1)) {
				curSchd.setPrincipalSchd(prvClosingBalance);
				curSchd.setProfitSchd(curSchd.getRepayAmount().subtract(curSchd.getPrincipalSchd()));
				// curSchd.setProfitSchd(curSchd.getProfitCalc().add(prvSchd.getProfitBalance()));
				// curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

				intDiff = calInt.add(prvSchd.getProfitBalance()).subtract(curSchd.getProfitSchd()).abs();

				if (intDiff.compareTo(bd500) <= 0) {
					intDiff = calInt.add(prvSchd.getProfitBalance()).subtract(curSchd.getProfitSchd());
					calInt = calInt.subtract(intDiff);
					curSchd.setProfitCalc(calInt);

					if (intDiff.compareTo(BigDecimal.ZERO) != 0) {
						blr.setInformation(blr.getInformation().concat("/Rounding Adjustment On MDT:")
								.concat(curSchd.getSchDate().toString()));
					}
				}

				// curSchd.setProfitSchd(curSchd.getProfitCalc().add(prvSchd.getProfitBalance()));
				// curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

			}

			curSchd.setProfitBalance(prvSchd.getProfitBalance().add(calInt).subtract(curSchd.getProfitSchd()));

			if (i == (dFsdList.size() - 1)) {
				if (curSchd.getProfitBalance().compareTo(BigDecimal.ZERO) != 0) {
					blr.setErrors(blr.getErrors().concat("/Interest Balance Left"));
				}

			}

			curSchd.setClosingBalance(
					prvClosingBalance.add(curSchd.getDisbAmount()).subtract(curSchd.getPrincipalSchd()));

			curSchd.setPrvRepayAmount(prvSchd.getRepayAmount());

			// Update flags
			if (FrequencyUtil.isFrqDate(dFM.getRepayPftFrq(), curSchd.getSchDate())) {
				isFrqDate = true;
			}

			if (curSchd.getProfitSchd().compareTo(zero) > 0 || isFrqDate) {
				curSchd.setPftOnSchDate(true);

				if (curSchd.getRepayAmount().compareTo(zero) > 0 || isFrqDate) {
					curSchd.setRepayOnSchDate(true);
				}

				curSchd.setFrqDate(isFrqDate);

				// Update Finance Main Fields
				dFM.setFinRepaymentAmount(dFM.getFinRepaymentAmount().add(curSchd.getSchdPriPaid()));
				dFM.setTotalRepayAmt(dFM.getTotalRepayAmt().add(curSchd.getRepayAmount()));

				if (DateUtility.compare(curSchDate, dFM.getGrcPeriodEndDate()) > 0 && isFrqDate
						&& dFM.getFirstRepay().compareTo(BigDecimal.ZERO) == 0) {
					dFM.setFirstRepay(curSchd.getRepayAmount());
				}

				isGracePeriod = false;
				// Grace Period Dates
				if (dFM.isAllowGrcPeriod()) {

					if (curSchDate.compareTo(dFM.getGrcPeriodEndDate()) <= 0) {
						isGracePeriod = true;
						dFM.setTotalGracePft(dFM.getTotalGracePft().add(curSchd.getProfitSchd()));
					}


					if (!isNextGrcDateSet) {
						if (isGracePeriod) {
							dFM.setNextGrcPftDate(curSchDate);
						} else {
							dFM.setNextGrcPftDate(dFM.getGrcPeriodEndDate());
						}

						if (curSchDate.compareTo(dFM.getGrcPeriodEndDate()) >= 0
								|| curSchDate.compareTo(rid.getAppDate()) > 0) {
							isNextGrcDateSet = true;
						}
					}
				}

				// Repay Period Dates
				if (curSchDate.compareTo(rid.getAppDate()) <= 0 && !isGracePeriod) {
					dFM.setLastRepayPftDate(curSchDate);
				}

				if (!isNextRpyDateSet) {
					dFM.setNextRepayPftDate(curSchDate);

					if (!isGracePeriod && curSchDate.compareTo(rid.getAppDate()) > 0) {
						isNextRpyDateSet = true;
					}
				}

				dFM.setCalMaturity(curSchDate);
				if(!isGracePeriod){
					dFM.setTotalProfit(dFM.getTotalProfit().add(curSchd.getProfitSchd()));
				}

				if (isFrqDate) {
					if (isGracePeriod) {
						dFM.setGraceTerms(dFM.getGraceTerms() + 1);
					} else {
						dFM.setCalTerms(dFM.getCalTerms() + 1);
					}
				}
			}
			
		}

		// Summaries
		dFM.setTotalGrossPft(dFM.getTotalProfit().add(dFM.getTotalGracePft()));
		dFM.setTotalGrossGrcPft(dFM.getTotalGracePft());

		dFM.setGraceBaseRate(null);
		dFM.setGraceSpecialRate(null);
		dFM.setGrcMargin(zero);

		if (dFM.isAllowGrcPeriod()) {
			dFM.setGrcPftRate(dFsdList.get(0).getActRate());
			dFM.setGrcPftFrq(dFM.getRepayPftFrq());
			dFM.setAllowGrcPftRvw(true);
			dFM.setGrcPftRvwFrq(dFM.getRepayPftFrq());
			dFM.setNextGrcPftRvwDate(dFM.getNextGrcPftDate());
		} else {
			dFM.setGrcPftRate(zero);
			dFM.setGrcPftFrq(null);
		}

		dFM.setNextRepayDate(dFM.getNextRepayPftDate());
		dFM.setNextRepayRvwDate(dFM.getNextRepayPftDate());
		dFM.setLastRepay(dFsdList.get(dFsdList.size() - 1).getRepayAmount());
		dFM.setLastRepayDate(dFM.getLastRepayPftDate());

		if (dFM.isAllowGrcPeriod()) {
			dMD = setRpyInstructDetails(dMD, dFsdList.get(1).getSchDate(), dFM.getGrcPeriodEndDate(), zero,
					CalculationConstants.SCHMTHD_PFT);
		}

		BigDecimal prvAmount = null;
		// Set Repayment Instructions
		for (int i = 1; i < (dFsdList.size() - 1); i++) {
			curSchd = dFsdList.get(i);

			if (curSchd.getSchDate().compareTo(dFM.getGrcPeriodEndDate()) <= 0) {
				continue;
			}

			if (prvAmount == null || prvAmount.compareTo(curSchd.getRepayAmount()) != 0) {
				if (curSchd.getInstNumber() > 0 && curSchd.isRepayOnSchDate()) {
					dMD = setRpyInstructDetails(dMD, curSchd.getSchDate(), dFM.getMaturityDate(),
							curSchd.getRepayAmount(), CalculationConstants.SCHMTHD_EQUAL);
					prvAmount = curSchd.getRepayAmount();
				}
			}

		}

		logger.debug("Leaving");
		return dMD;

	}

	private BigDecimal getActRate(List<FinanceScheduleDetail> dFsdList, int iStart) {

		BigDecimal actRate = BigDecimal.ZERO;

		for (int i = iStart; i < dFsdList.size(); i++) {
			FinanceScheduleDetail nextSchd = dFsdList.get(i + 1);

			if (nextSchd.getActRate().compareTo(BigDecimal.ZERO) > 0) {
				actRate = nextSchd.getActRate();
				break;
			}
		}

		return actRate;

	}

	// -----------------------------------------------------------------------------------
	// Repay Instructions
	// -----------------------------------------------------------------------------------

	private MigrationData setRpyInstructDetails(MigrationData dMD, Date fromDate, Date toDate, BigDecimal repayAmount,
			String schdMethod) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> fsdList = dMD.getFinScheduleDetails();

		BigDecimal nextInstructAmount = BigDecimal.ZERO;
		Date nextInstructDate = null;
		String nextInstructSchdMethod = null;

		boolean isAddNewInstruction = true;
		int instructIndex = -1;
		FinanceMain fm = dMD.getFinanceMain();

		// Find next date for instruction
		if (DateUtility.compare(toDate, fm.getMaturityDate()) >= 0) {
			nextInstructDate = fm.getMaturityDate();
		} else {
			int sdSize = fsdList.size();
			FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

			for (int i = 0; i < sdSize; i++) {
				curSchd = fsdList.get(i);

				if (curSchd.getSchDate().after(toDate) && (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate())) {
					nextInstructDate = curSchd.getSchDate();
					nextInstructSchdMethod = curSchd.getSchdMethod();
					break;
				}
			}
			// Next instruction amount and schedule method
			sortRepayInstructions(dMD.getRepayInstructions());
			if (nextInstructDate != null) {
				instructIndex = fetchRpyInstruction(dMD, nextInstructDate);
			}

			if (instructIndex >= 0) {
				nextInstructAmount = dMD.getRepayInstructions().get(instructIndex).getRepayAmount();
				nextInstructSchdMethod = dMD.getRepayInstructions().get(instructIndex).getRepaySchdMethod();
			}
		}

		RepayInstruction curInstruction = new RepayInstruction();

		// Remove any instructions between fromdate and todate
		for (int i = 0; i < dMD.getRepayInstructions().size(); i++) {
			curInstruction = dMD.getRepayInstructions().get(i);

			if (DateUtility.compare(curInstruction.getRepayDate(), fromDate) >= 0
					&& DateUtility.compare(curInstruction.getRepayDate(), toDate) <= 0) {
				// finScheduleData.getRepayInstructions().remove(curInstruction);
				dMD.getRepayInstructions().remove(i);
				i = i - 1;
			}

			if (DateUtility.compare(curInstruction.getRepayDate(), nextInstructDate) == 0) {
				isAddNewInstruction = false;
			}
		}

		dMD.setRepayInstructions(sortRepayInstructions(dMD.getRepayInstructions()));

		// Add repay instructions on from date
		RepayInstruction ri = new RepayInstruction();
		ri.setFinReference(dMD.getFinanceMain().getFinReference());
		ri.setRepayDate(fromDate);
		ri.setRepayAmount(repayAmount);
		ri.setRepaySchdMethod(schdMethod);

		dMD.getRepayInstructions().add(ri);

		// Add (reset) repay instruction after todate
		if (DateUtility.compare(toDate, fm.getMaturityDate()) >= 0 || !isAddNewInstruction) {
			dMD.setRepayInstructions(sortRepayInstructions(dMD.getRepayInstructions()));
			return dMD;
		}

		if (DateUtility.compare(nextInstructDate, fromDate) > 0) {
			ri = new RepayInstruction();
			ri.setFinReference(dMD.getFinanceMain().getFinReference());
			ri.setRepayDate(nextInstructDate);
			ri.setRepayAmount(nextInstructAmount);
			ri.setRepaySchdMethod(nextInstructSchdMethod);
			dMD.getRepayInstructions().add(ri);
		}

		dMD.setRepayInstructions(sortRepayInstructions(dMD.getRepayInstructions()));

		logger.debug("Leaving");
		return dMD;
	}

	private int fetchRpyInstruction(MigrationData dMD, Date instructDate) {

		int riSize = dMD.getRepayInstructions().size();
		int j = -1;

		for (int i = 0; i < riSize; i++) {
			RepayInstruction curInstruction = dMD.getRepayInstructions().get(i);

			if (curInstruction.getRepayDate().after(instructDate)) {
				break;
			}

			j = i;
		}

		return j;
	}

	public List<String> getFinanceReferenceList(String type) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFinanceMainDAO().getFinanceReferenceList(type);
	}

	public int updateFinanceDetails(FinScheduleData finScheduleData, String type) {
		logger.debug("Entering");
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		getFinanceMainDAO().save(financeMain, TableType.MAIN_TAB, false);
		listSave(finScheduleData, type, false);

		logger.debug("Leaving");
		return 0;
	}

	/**
	 * Method to save what if inquiry lists
	 */
	public void listSave(FinScheduleData finDetail, String tableType, boolean isWIF) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < finDetail.getFinanceScheduleDetails().size(); i++) {
			finDetail.getFinanceScheduleDetails().get(i).setLastMntBy(finDetail.getFinanceMain().getLastMntBy());
			finDetail.getFinanceScheduleDetails().get(i).setFinReference(finDetail.getFinReference());
			int seqNo = 0;

			if (mapDateSeq.containsKey(finDetail.getFinanceScheduleDetails().get(i).getSchDate())) {
				seqNo = mapDateSeq.get(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
				mapDateSeq.remove(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(finDetail.getFinanceScheduleDetails().get(i).getSchDate(), seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setSchSeq(seqNo);
		}

		getFinanceScheduleDetailDAO().saveList(finDetail.getFinanceScheduleDetails(), tableType, isWIF);

		// Finance Disbursement Details
		mapDateSeq = new HashMap<Date, Integer>();
		Date curBDay = SysParamUtil.getValueAsDate(SysParamUtil.Param.APP_DFT_CURR.name());
		for (int i = 0; i < finDetail.getDisbursementDetails().size(); i++) {
			finDetail.getDisbursementDetails().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
			finDetail.getDisbursementDetails().get(i).setDisbIsActive(true);
			finDetail.getDisbursementDetails().get(i).setDisbDisbursed(true);
		}
		getFinanceDisbursementDAO().saveList(finDetail.getDisbursementDetails(), tableType, isWIF);

		// Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinReference());
		}
		getRepayInstructionDAO().saveList(finDetail.getRepayInstructions(), tableType, isWIF);

		// Finance Overdue Penalty Rates
		FinODPenaltyRate penaltyRate = finDetail.getFinODPenaltyRate();
		if (penaltyRate == null) {
			penaltyRate = new FinODPenaltyRate();
			penaltyRate.setApplyODPenalty(false);
			penaltyRate.setODIncGrcDays(false);
			penaltyRate.setODChargeType("");
			penaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
			penaltyRate.setODChargeCalOn("");
			penaltyRate.setODGraceDays(0);
			penaltyRate.setODAllowWaiver(false);
			penaltyRate.setODMaxWaiverPerc(BigDecimal.ZERO);
		}
		penaltyRate.setFinReference(finDetail.getFinReference());
		penaltyRate.setFinEffectDate(DateUtility.getSysDate());
		getFinODPenaltyRateDAO().save(penaltyRate, tableType);

		logger.debug("Leaving ");
	}

	public void saveFinanceDetails(MigrationData dMD) {
		logger.debug("Entering");
		getFinanceMainDAO().save(dMD.getFinanceMain(), TableType.MAIN_TAB, false);
		getFinanceDisbursementDAO().saveList(dMD.getFinDisbursements(), "", false);

		for (int i = 0; i < dMD.getFinAdvancePayments().size(); i++) {
			getFinAdvancePaymentsDAO().save(dMD.getFinAdvancePayments().get(i), "");
		}

		getFinanceScheduleDetailDAO().saveList(dMD.getFinScheduleDetails(), "", false);

		for (int i = 0; i < dMD.getFinFeeDetails().size(); i++) {
			getFinFeeDetailDAO().save(dMD.getFinFeeDetails().get(i), false, "");
		}

		getRepayInstructionDAO().saveList(dMD.getRepayInstructions(), "", false);

		// Receipt Headers
		for (int i = 0; i < dMD.getFinReceiptHeaders().size(); i++) {
			getFinReceiptHeaderDAO().save(dMD.getFinReceiptHeaders().get(i), TableType.MAIN_TAB);
		}

		// Receipt Details
		for (int i = 0; i < dMD.getFinReceiptDetails().size(); i++) {
			getFinReceiptDetailDAO().save(dMD.getFinReceiptDetails().get(i), TableType.MAIN_TAB);
		}

		// Allocation Details
		getReceiptAllocationDetailDAO().saveAllocations(dMD.getReceiptAllocationDetails(), TableType.MAIN_TAB);

		// Excess Amounts
		if (dMD.getFinExcessAmounts().size() > 0) {
			getFinExcessAmountDAO().saveExcess(dMD.getFinExcessAmounts().get(0));

			// Excess Amount Movements
			for (int i = 0; i < dMD.getFinExcessMovements().size(); i++) {
				getFinExcessAmountDAO().saveExcessMovement(dMD.getFinExcessMovements().get(i));
			}

		}

		// Manual Advises
		for (int i = 0; i < dMD.getManualAdvises().size(); i++) {
			getManualAdviseDAO().save(dMD.getManualAdvises().get(i), TableType.MAIN_TAB);
		}

		for (int i = 0; i < dMD.getManualAdviseMovements().size(); i++) {
			getManualAdviseDAO().saveMovement(dMD.getManualAdviseMovements().get(i), "");
		}

		// Repayment Header
		for (int i = 0; i < dMD.getFinRepayHeaders().size(); i++) {
			getFinanceRepaymentsDAO().saveFinRepayHeader(dMD.getFinRepayHeaders().get(i), "");
		}

		// Repay Schedule Details
		getFinanceRepaymentsDAO().saveRpySchdList(dMD.getRepayScheduleDetails(), "");

		// Repayment Details
		for (int i = 0; i < dMD.getRepayDetails().size(); i++) {
			getFinanceRepaymentsDAO().save(dMD.getRepayDetails().get(i), "");
		}
		
		// Repayment Details
		for (int i = 0; i < dMD.getFinODDetails().size(); i++) {
			getFinODDetailsDAO().save(dMD.getFinODDetails().get(i));
		}

		// Presentment Details
		for (int i = 0; i < dMD.getPresentmentDetails().size(); i++) {
			getPresentmentDetailDAO().save(dMD.getPresentmentDetails().get(i), TableType.MAIN_TAB);
		}

		getFinanceProfitDetailDAO().save(dMD.getFinProfitDetails());

		getFinODPenaltyRateDAO().save(dMD.getPenaltyrate(), "");

		if (dMD.getBasicLoanRecon().getErrors().length() > 900) {
			dMD.getBasicLoanRecon().setErrors(dMD.getBasicLoanRecon().getErrors().substring(0, 900));
		}

		if (dMD.getBasicLoanRecon().getWarnings().length() > 900) {
			dMD.getBasicLoanRecon().setWarnings(dMD.getBasicLoanRecon().getWarnings().substring(0, 900));
		}

		if (dMD.getBasicLoanRecon().getInformation().length() > 900) {
			dMD.getBasicLoanRecon().setInformation(dMD.getBasicLoanRecon().getInformation().substring(0, 900));
		}

		getBasicLoanReconDAO().saveRecon(dMD.getBasicLoanRecon());

		getPostingsDAO().saveBatch(dMD.getPostEntries());

		logger.debug("Leaving");
	}

	// ----------------------------------------------------------------------------
	// Receipt Data
	// ----------------------------------------------------------------------------
	public MigrationData setReceipts(MigrationData sMD, MigrationData dMD, ReferenceID rid) {
		List<FinReceiptHeader> sRchList = sMD.getFinReceiptHeaders();

		// Clean Receipts
		sMD = cleanAllocations(sMD);
		sMD = cleanZeroReceipts(sMD);

		for (int i = 0; i < sRchList.size(); i++) {
			FinReceiptHeader sRch = sRchList.get(i);
			FinReceiptHeader dRch = sRch;
			dMD.getFinReceiptHeaders().add(dRch);

			// Add New Receipt Header and Details
			dMD = addNewReceiptDetail(sMD, dMD, i);

			// Set Allocation Details
			dMD = addNewAllocations(sMD, dMD, rid);

			// Set Repay Header & Detail
			dMD = addNewRepayments(sMD, dMD, rid);

			// Add Presentment Details
			dMD = addPresentments(sMD, dMD, rid);

			sRchList.remove(i);
			i = i - 1;
		}

		return dMD;
	}

	public MigrationData cleanAllocations(MigrationData sMD) {
		List<ReceiptAllocationDetail> sRadList = sMD.getReceiptAllocationDetails();
		List<FinReceiptHeader> sRchList = sMD.getFinReceiptHeaders();
		List<FinReceiptDetail> sRcdList = sMD.getFinReceiptDetails();

		// Clean Receipt Header Status
		/*
		 * for (int i = 0; i < sRchList.size(); i++) { if
		 * (StringUtils.isBlank(sRchList.get(i).getReceiptModeStatus())) { for
		 * (int j = 0; j < sRcdList.size(); j++) { if
		 * (sRcdList.get(j).getReceiptID()!=sRchList.get(i).getReceiptID()) {
		 * continue; }
		 * sRchList.get(i).setReceiptModeStatus(sRcdList.get(j).getStatus());
		 * break; } } }
		 */
		boolean isSkip = false;

		for (int i = 0; i < sRadList.size(); i++) {
			ReceiptAllocationDetail sRad = sRadList.get(i);
			if (sRad.getPaidAmount().compareTo(BigDecimal.ZERO) >= 0) {
				continue;
			}

			BigDecimal balReduction = sRad.getPaidAmount().abs();
			BigDecimal reduction = BigDecimal.ZERO;
			String allocationType = sRad.getAllocationType();
			long redRchID = -1;
			isSkip = false;

			// If -ve amount is not approved then delete and skip
			for (int j = 0; j < sRchList.size(); j++) {
				if (sRad.getReceiptID() != sRchList.get(j).getReceiptID()) {
					continue;
				}

				if (!StringUtils.equals(sRchList.get(j).getReceiptModeStatus(), RepayConstants.PAYSTATUS_APPROVED)) {
					isSkip = true;
				}

				break;
			}

			if (isSkip) {
				sRadList.remove(i);
				i = i - 1;
				continue;
			}

			// Reduce Previous Allocation Amount
			for (int j = i - 1; j >= 0; j--) {
				ReceiptAllocationDetail rad = sRadList.get(j);
				if (!StringUtils.equals(allocationType, rad.getAllocationType())) {
					continue;
				}

				// Find receipt is cancelled or bounced
				for (int k = 0; k < sRchList.size(); k++) {
					if (rad.getReceiptID() != sRchList.get(k).getReceiptID()) {
						continue;
					}

					if (StringUtils.equals(sRchList.get(k).getReceiptModeStatus(), RepayConstants.PAYSTATUS_APPROVED)) {

						if (rad.getPaidAmount().compareTo(balReduction) >= 0) {
							reduction = balReduction;
							balReduction = BigDecimal.ZERO;
						} else {
							reduction = rad.getPaidAmount();
							balReduction = balReduction.subtract(reduction);
						}

						sRchList.get(k).setReceiptAmount(sRchList.get(k).getReceiptAmount().subtract(reduction));
						rad.setPaidAmount(rad.getPaidAmount().subtract(reduction));

						break;
					}
				}

				if (balReduction.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}

			if (balReduction.compareTo(BigDecimal.ZERO) == 0) {
				sRadList.remove(i);
				i = i - 1;
				continue;
			}

			// If balance reduction still exist
			// Reduce from Next Allocation Amount
			for (int j = i + 1; j < sRadList.size(); j++) {
				ReceiptAllocationDetail rad = sRadList.get(j);
				if (!StringUtils.equals(allocationType, rad.getAllocationType())) {
					continue;
				}

				// Find receipt is cancelled or bounced
				for (int k = 0; k < sRchList.size(); k++) {
					if (rad.getReceiptID() != sRchList.get(k).getReceiptID()) {
						continue;
					}

					if (StringUtils.equals(sRchList.get(k).getReceiptModeStatus(), RepayConstants.PAYSTATUS_APPROVED)) {

						if (rad.getPaidAmount().compareTo(balReduction) >= 0) {
							reduction = balReduction;
							balReduction = BigDecimal.ZERO;
						} else {
							reduction = rad.getPaidAmount();
							balReduction = balReduction.subtract(reduction);
						}

						sRchList.get(k).setReceiptAmount(sRchList.get(k).getReceiptAmount().subtract(reduction));
						rad.setPaidAmount(rad.getPaidAmount().subtract(reduction));

						break;
					}
				}

				if (balReduction.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}

			sRadList.remove(i);
			i = i - 1;
		}

		return sMD;
	}

	public MigrationData cleanZeroReceipts(MigrationData sMD) {
		List<FinReceiptHeader> sRchList = sMD.getFinReceiptHeaders();

		// Clean receipts with zero amount
		for (int i = 0; i < sRchList.size(); i++) {
			if (sRchList.get(i).getReceiptAmount().compareTo(BigDecimal.ZERO) == 0) {
				sRchList.remove(i);
				i = i - 1;
			}
		}

		// Clean zero Allocations
		List<ReceiptAllocationDetail> sRadList = sMD.getReceiptAllocationDetails();
		for (int i = 0; i < sRadList.size(); i++) {
			if (sRadList.get(i).getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
				sRadList.remove(i);
				i = i - 1;
			}
		}

		return sMD;
	}

	// ADD NEW RECEIPT
	public MigrationData addNewReceiptDetail(MigrationData sMD, MigrationData dMD, int iRch) {
		FinReceiptHeader sRch = sMD.getFinReceiptHeaders().get(iRch);
		FinReceiptHeader dRch = dMD.getFinReceiptHeaders().get(dMD.getFinReceiptHeaders().size() - 1);

		// Set Receipt Detail
		List<FinReceiptDetail> sRcdList = sMD.getFinReceiptDetails();
		for (int i = 0; i < sRcdList.size(); i++) {
			if (sRcdList.get(i).getReceiptID() != sRch.getReceiptID()) {
				continue;
			}

			FinReceiptDetail dRcd = sRcdList.get(i);

			/*
			 * if (StringUtils.isBlank(sRch.getReceiptModeStatus())) {
			 * dRch.setReceiptModeStatus(dRcd.getStatus()); } else {
			 * dRcd.setStatus(dRch.getReceiptModeStatus()); }
			 */

			dRcd.setPaymentType(dRch.getReceiptMode());
			dRcd.setReceivedDate(dRch.getReceiptDate());
			dRcd.setStatus(dRch.getReceiptModeStatus());
			dMD.getFinReceiptDetails().add(dRcd);
			sRcdList.remove(i);
			break;
		}

		return dMD;
	}

	// ADD NEW RECEIPT ALLOCATION
	public MigrationData addNewAllocations(MigrationData sMD, MigrationData dMD, ReferenceID rid) {

		int iRch = dMD.getFinReceiptHeaders().size() - 1;

		FinReceiptHeader dRch = dMD.getFinReceiptHeaders().get(iRch);
		BigDecimal allocatedAmount = BigDecimal.ZERO;

		int iRad = 0;

		dMD.setRpyPri(BigDecimal.ZERO);
		dMD.setRpyInt(BigDecimal.ZERO);
		dMD.setRpyODC(BigDecimal.ZERO);
		dMD.setRpyOther(BigDecimal.ZERO);
		dMD.setRpyExcessAmount(BigDecimal.ZERO);

		// Set Receipt Allocation Details
		List<ReceiptAllocationDetail> sRadList = sMD.getReceiptAllocationDetails();
		int alocID = 0;
		for (int i = 0; i < sRadList.size(); i++) {
			if (sRadList.get(i).getReceiptID() != dRch.getReceiptID()) {
				continue;
			}

			ReceiptAllocationDetail dRad = sRadList.get(i);
			dRad.setReceiptAllocationid(rid.getReceiptAlocID()+1);
			rid.setReceiptAlocID(rid.getReceiptAlocID()+1);
			dRad.setAllocationID(alocID+1);
			alocID = alocID+1;
			
			BigDecimal prvDupAmt = BigDecimal.ZERO;
			for (ReceiptAllocationDetail aloc : dMD.getReceiptAllocationDetails()) {
				if (dRch.getReceiptID() != aloc.getReceiptID()) {
					continue;
				}
				
				if(StringUtils.equals(aloc.getAllocationType(), dRad.getAllocationType()) && 
						dRad.getAllocationTo() == aloc.getAllocationTo()){
					dRad.setPaidAmount(dRad.getPaidAmount().add(aloc.getPaidAmount()));
					prvDupAmt = aloc.getPaidAmount();
					dMD.getReceiptAllocationDetails().remove(aloc);
					sRadList.remove(aloc);
					break;
				}
			}
			
			dMD.getReceiptAllocationDetails().add(dRad);

			if (StringUtils.equals(dRad.getAllocationType(), RepayConstants.ALLOCATION_PRI)
					|| StringUtils.equals(dRad.getAllocationType(), RepayConstants.ALLOCATION_PFT)
					|| StringUtils.equals(dRad.getAllocationType(), RepayConstants.ALLOCATION_ODC)
					|| StringUtils.equals(dRad.getAllocationType(), RepayConstants.ALLOCATION_LPFT)
					|| StringUtils.equals(dRad.getAllocationType(), RepayConstants.ALLOCATION_NPFT)) {
				dRad.setAllocationTo(0);

				if (StringUtils.equals(dRad.getAllocationType(), RepayConstants.ALLOCATION_PRI)) {
					dMD.setRpyPri(dMD.getRpyPri().add(dRad.getPaidAmount()).subtract(prvDupAmt));
				} else if (StringUtils.equals(dRad.getAllocationType(), RepayConstants.ALLOCATION_PFT)) {
					dMD.setRpyInt(dMD.getRpyInt().add(dRad.getPaidAmount()).subtract(prvDupAmt));
				} else if (StringUtils.equals(dRad.getAllocationType(), RepayConstants.ALLOCATION_ODC)) {
					dMD.setRpyODC(dMD.getRpyODC().add(dRad.getPaidAmount()).subtract(prvDupAmt));
					rid.setOdcReceived(rid.getOdcReceived().add(dRad.getPaidAmount()).subtract(prvDupAmt));
				}

			} else {

				dMD.setRpyOther(dMD.getRpyOther().add(dRad.getPaidAmount()).subtract(prvDupAmt));

				if (!StringUtils.equals(dRch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)
						&& !StringUtils.equals(dRch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)) {
					if (dRad.getAllocationTo() == 0) {
						// Create Manual Advise
						rid.setManualAdviseID(rid.getManualAdviseID() + 1);
						dMD = addManualAdvise(sMD, dMD, rid, iRad, iRch);

					} else {
						// Set Manual Advise
						dMD = resetManualAdvise(sMD, dMD, rid, i);

						if (StringUtils.equals(dRad.getAllocationType(), RepayConstants.ALLOCATION_BOUNCE)
								|| StringUtils.equals(dRad.getAllocationType(), "Bounce")) {
							dRad.setAllocationType(RepayConstants.ALLOCATION_BOUNCE);
							dRad.setAllocationTo(0);
							rid.setBounceReceived(rid.getBounceReceived().add(dRad.getPaidAmount()).subtract(prvDupAmt));
						}

					}
				}else{
					if (StringUtils.equals(dRad.getAllocationType(), RepayConstants.ALLOCATION_BOUNCE)
							|| StringUtils.equals(dRad.getAllocationType(), "Bounce")) {
						dRad.setAllocationType(RepayConstants.ALLOCATION_BOUNCE);
						dRad.setAllocationTo(0);
					}
				}
			}

			iRad = iRad + 1;
			allocatedAmount = allocatedAmount.add(dRad.getPaidAmount().subtract(prvDupAmt));
			sRadList.remove(i);
			i = i - 1;
		}

		BigDecimal excessAmount = dRch.getReceiptAmount().subtract(allocatedAmount);

		// Approved OR Realized receipt check for excess
		if (StringUtils.equals(dRch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_APPROVED)
				|| StringUtils.equals(dRch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_REALIZED)) {

			// Receipt adjusted to Excess
			if (!StringUtils.equals(dRch.getReceiptMode(), RepayConstants.RECEIPTMODE_EXCESS)) {
				if (excessAmount.compareTo(BigDecimal.ZERO) < 0) {
					dMD.getBasicLoanRecon().setErrors(dMD.getBasicLoanRecon().getErrors().concat("Allocation is More: ")
							.concat(dRch.getReceiptDate().toString()));
				}

				// Excess Amount
				if (excessAmount.compareTo(BigDecimal.ZERO) > 0) {
					dMD = addExcessAmount(dMD, rid, excessAmount, dRch.getReceiptDate(), dRch.getReceiptID());
					dMD.setRpyExcessAmount(excessAmount);
				}

				// Excess used for Receipt
			} else {
				dMD = useExcessAmount(dMD, rid, dRch.getReceiptAmount(), dRch.getReceiptDate(), dRch.getReceiptID());

				// Excess Amount
				if (excessAmount.compareTo(BigDecimal.ZERO) > 0) {
					dMD.setRpyExcessAmount(excessAmount);
					dMD = addExcessAmount(dMD, rid, excessAmount, dRch.getReceiptDate(), dRch.getReceiptID());
				}
			}
		}

		return dMD;
	}

	public MigrationData resetManualAdvise(MigrationData sMD, MigrationData dMD, ReferenceID rid, int iRad) {
		ReceiptAllocationDetail dRad = sMD.getReceiptAllocationDetails().get(iRad);
		List<ManualAdvise> sMaList = sMD.getManualAdvises();

		for (int i = 0; i < sMaList.size(); i++) {
			ManualAdvise dMa = sMaList.get(i);
			if (dMa.getAdviseID() != dRad.getAllocationTo()) {
				continue;
			}

			if (!(StringUtils.equals(dRad.getAllocationType(), RepayConstants.ALLOCATION_BOUNCE)
					|| StringUtils.equals(dRad.getAllocationType(), "Bounce"))) {
				dRad.setAllocationType(RepayConstants.ALLOCATION_MANADV);
			}else{
				dMa.setFeeTypeID(0);
				dMa.setBounceID(25);
			}
			
			dMa.setAdviseAmount(dRad.getPaidAmount());
			dMa.setPaidAmount(dRad.getPaidAmount());

			for (int j = 0; j < rid.getFeeVsGLList().size(); j++) {
				FeeTypeVsGLMapping ftg = rid.getFeeVsGLList().get(j);

				if (dMa.getFeeTypeID() == ftg.getFeeTypeID()) {
					ftg.setFeePaid(ftg.getFeePaid().add(dMa.getPaidAmount()));
				}
			}

			dMD.getManualAdvises().add(dMa);
			sMaList.remove(i);
			break;
		}

		return dMD;
	}

	public MigrationData addManualAdvise(MigrationData sMD, MigrationData dMD, ReferenceID rid, int iRad, int iRch) {
		ReceiptAllocationDetail dRad = dMD.getReceiptAllocationDetails().get(iRad);
		ManualAdvise dMa = new ManualAdvise();
		dMa.setAdviseID(rid.getManualAdviseID());
		dMa.setAdviseType(1);
		dMa.setFinReference(dMD.getFinanceMain().getFinReference());

		if (StringUtils.equals(dRad.getAllocationType(), "FRE")) {
			dMa.setFeeTypeID(28);
		}

		if (StringUtils.equals(dRad.getAllocationType(), "INS")) {
			dMa.setFeeTypeID(33);
		}

		dRad.setAllocationType(RepayConstants.ALLOCATION_MANADV);

		dMa.setAdviseAmount(dRad.getPaidAmount());
		dMa.setPaidAmount(dRad.getPaidAmount());
		dMa.setVersion(1);
		dMa.setReceiptID(dRad.getReceiptID());
		dMa.setValueDate(dMD.getFinReceiptHeaders().get(iRch).getReceiptDate());
		dMa.setPostDate(dMD.getFinReceiptHeaders().get(iRch).getRealizationDate());

		dMa.setLastMntBy(1000);
		dMa.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		dMD.getManualAdvises().add(dMa);

		for (int i = 0; i < rid.getFeeVsGLList().size(); i++) {
			FeeTypeVsGLMapping ftg = rid.getFeeVsGLList().get(i);

			if (dMa.getFeeTypeID() == ftg.getFeeTypeID()) {
				ftg.setFeePaid(ftg.getFeePaid().add(dMa.getPaidAmount()));
			}
		}

		return dMD;
	}

	public MigrationData addFutureManualAdvises(MigrationData sMD, MigrationData dMD, ReferenceID rid) {

		List<ManualAdvise> sMadList = sMD.getManualAdvises();

		for (int i = 0; i < sMadList.size(); i++) {
			ManualAdvise mad = sMadList.get(i);

			if (mad.getReceiptID() > 0) {
				continue;
			}

			dMD.getManualAdvises().add(mad);
		}

		// Add Manual Advise Movements
		List<ManualAdvise> dMadList = dMD.getManualAdvises();
		for (int i = 0; i < dMadList.size(); i++) {
			ManualAdvise mad = dMadList.get(i);
			ManualAdviseMovements madm = new ManualAdviseMovements();

			rid.setMadMovementID(rid.getMadMovementID() + 1);
			madm.setMovementID(rid.getMadMovementID());
			madm.setAdviseID(mad.getAdviseID());
			madm.setReceiptID(mad.getReceiptID());
			madm.setReceiptSeqID(mad.getReceiptID());
			madm.setFeeTypeCode(mad.getFeeTypeCode());
			madm.setFeeTypeDesc(mad.getFeeTypeDesc());
			madm.setMovementDate(mad.getPostDate());
			madm.setMovementAmount(mad.getAdviseAmount());
			madm.setPaidAmount(mad.getPaidAmount());
			dMD.getManualAdviseMovements().add(madm);
		}

		return dMD;
	}

	private MigrationData addExcessAmount(MigrationData dMD, ReferenceID rid, BigDecimal excessAmount,
			Date movementDate, long receiptID) {

		// Add OR Maintain Excess Amount
		FinExcessAmount fea = new FinExcessAmount();
		boolean isExcessFound = false;

		if (dMD.getFinExcessAmounts().size() == 1) {
			fea = dMD.getFinExcessAmounts().get(0);
			isExcessFound = true;
		} else {
			rid.setExcessID(rid.getExcessID() + 1);
			fea.setExcessID(rid.getExcessID());
			fea.setFinReference(dMD.getFinanceMain().getFinReference());
			fea.setAmountType(RepayConstants.EXAMOUNTTYPE_EXCESS);
		}

		fea.setAmount(fea.getAmount().add(excessAmount));
		fea.setBalanceAmt(fea.getAmount().subtract(fea.getUtilisedAmt()));

		if (isExcessFound) {
			dMD.getFinExcessAmounts().set(0, fea);
		} else {
			dMD.getFinExcessAmounts().add(fea);
		}

		// Add Excess Movement
		FinExcessMovement fem = new FinExcessMovement();
		fem.setExcessID(rid.getExcessID());
		fem.setReceiptID(receiptID);
		fem.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
		fem.setTranType(AccountConstants.TRANTYPE_CREDIT);
		fem.setAmount(excessAmount);
		dMD.getFinExcessMovements().add(fem);

		return dMD;
	}

	private MigrationData useExcessAmount(MigrationData dMD, ReferenceID rid, BigDecimal excessAmount,
			Date movementDate, long receiptID) {

		// Add OR Maintain Excess Amount
		FinExcessAmount fea = new FinExcessAmount();
		boolean isExcessFound = false;

		if (dMD.getFinExcessAmounts().size() == 1) {
			fea = dMD.getFinExcessAmounts().get(0);
			isExcessFound = true;
		} else {
			rid.setExcessID(rid.getExcessID() + 1);
			fea.setExcessID(rid.getExcessID());
			fea.setFinReference(dMD.getFinanceMain().getFinReference());
			fea.setAmountType(RepayConstants.EXAMOUNTTYPE_EXCESS);
		}

		fea.setUtilisedAmt(fea.getUtilisedAmt().add(excessAmount));
		fea.setBalanceAmt(fea.getAmount().subtract(fea.getUtilisedAmt()));

		if (isExcessFound) {
			dMD.getFinExcessAmounts().set(0, fea);
		} else {
			dMD.getFinExcessAmounts().add(fea);
		}

		// Add Excess Movement
		FinExcessMovement fem = new FinExcessMovement();
		fem.setExcessID(rid.getExcessID());
		fem.setReceiptID(receiptID);
		fem.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
		fem.setTranType(AccountConstants.TRANTYPE_DEBIT);
		fem.setAmount(excessAmount);
		dMD.getFinExcessMovements().add(fem);

		return dMD;
	}

	// -----------------------------------------------------------------------------------
	// REPAYMENTS
	// -----------------------------------------------------------------------------------
	public MigrationData addNewRepayments(MigrationData sMD, MigrationData dMD, ReferenceID rid) {
		BigDecimal totRepay = BigDecimal.ZERO;
		int iRch = dMD.getFinReceiptHeaders().size() - 1;

		if (dMD.getRpyPri().compareTo(BigDecimal.ZERO) > 0 || dMD.getRpyInt().compareTo(BigDecimal.ZERO) > 0
				|| dMD.getRpyODC().compareTo(BigDecimal.ZERO) > 0) {
			totRepay = dMD.getRpyPri().add(dMD.getRpyInt()).add(dMD.getRpyODC().add(dMD.getRpyOther()));
			dMD = addNewRepayHeader(dMD, rid, FinanceConstants.FINSER_EVENT_SCHDRPY, totRepay);
			dMD.setRepaySchID(0);

			// Add Repay Schedule Details
			if (StringUtils.equals(dMD.getFinReceiptHeaders().get(iRch).getReceiptModeStatus(),
					RepayConstants.PAYSTATUS_APPROVED)
					|| StringUtils.equals(dMD.getFinReceiptHeaders().get(iRch).getReceiptModeStatus(),
							RepayConstants.PAYSTATUS_REALIZED)) {

				if (dMD.getRpyInt().compareTo(BigDecimal.ZERO) > 0 || dMD.getRpyPri().compareTo(BigDecimal.ZERO) > 0
						|| dMD.getRpyEMI().compareTo(BigDecimal.ZERO) > 0) {

					if (dMD.isWorkOnEMI()) {
						dMD = addRepayScheduleByEMI(dMD, rid);
					} else {
						dMD = addRepaySchedule(dMD, rid);
					}
				}

			}

		}

		if (dMD.getRpyExcessAmount().compareTo(BigDecimal.ZERO) > 0 ||
				(dMD.getRpyInt().add(dMD.getRpyPri()).compareTo(BigDecimal.ZERO) > 0 && !dMD.isWorkOnEMI() &&
						!StringUtils.equals(dMD.getFinReceiptHeaders().get(iRch).getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)
						&& !StringUtils.equals(dMD.getFinReceiptHeaders().get(iRch).getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE))) {
			totRepay = dMD.getRpyExcessAmount();
			if(totRepay.compareTo(BigDecimal.ZERO) == 0 && !dMD.isWorkOnEMI()  &&
					!StringUtils.equals(dMD.getFinReceiptHeaders().get(iRch).getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)
					&& !StringUtils.equals(dMD.getFinReceiptHeaders().get(iRch).getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)){
				totRepay = dMD.getRpyInt().add(dMD.getRpyPri());
				int iRph = dMD.getFinRepayHeaders().size() - 1;
				dMD.getFinRepayHeaders().get(iRph).setPriAmount(dMD.getFinRepayHeaders().get(iRph).getPriAmount().subtract(dMD.getRpyPri()));
				dMD.getFinRepayHeaders().get(iRph).setPftAmount(dMD.getFinRepayHeaders().get(iRph).getPftAmount().subtract(dMD.getRpyInt()));
				
				// Excess Amount
				if (totRepay.compareTo(BigDecimal.ZERO) > 0) {
					dMD = addExcessAmount(dMD, rid, totRepay, dMD.getFinRepayHeaders().get(iRph).getValueDate(), 
							dMD.getFinReceiptHeaders().get(iRch).getReceiptID());
					dMD.setRpyExcessAmount(totRepay);
				}
			}
			dMD = addNewRepayHeader(dMD, rid, RepayConstants.EXCESSADJUSTTO_EXCESS, totRepay);
		}

		return dMD;
	}

	public MigrationData addNewRepayHeader(MigrationData dMD, ReferenceID rid, String finEvent, BigDecimal totRepay) {
		int iRcd = dMD.getFinReceiptDetails().size() - 1;
		FinReceiptDetail rcd = dMD.getFinReceiptDetails().get(iRcd);
		FinRepayHeader rph = new FinRepayHeader();

		rph.setFinReference(dMD.getFinanceMain().getFinReference());
		rph.setValueDate(rcd.getValueDate());
		rph.setFinEvent(finEvent);
		rph.setRepayAmount(totRepay);

		if (!StringUtils.equals(finEvent, "E")) {
			rph.setPriAmount(dMD.getRpyPri());
			rph.setPftAmount(dMD.getRpyInt());
			rph.setTotalPenalty(dMD.getRpyODC());
		}

		rph.setReceiptSeqID(rcd.getReceiptSeqID());

		rid.setRepayID(rid.getRepayID() + 1);
		rph.setRepayID(rid.getRepayID());

		dMD.getFinRepayHeaders().add(rph);
		return dMD;
	}

	// FIN REPAY SCHEDULE DETAILS
	public MigrationData addRepaySchedule(MigrationData dMD, ReferenceID rid) {
		List<FinanceScheduleDetail> fsdList = dMD.getFinScheduleDetails();

		// for (int i = iStart; i < fsdList.size(); i++) {
		for (int i = 1; i < fsdList.size(); i++) {
			FinanceScheduleDetail fsd = fsdList.get(i);
			dMD.setIntBal(fsd.getProfitSchd().subtract(fsd.getSchdPftPaid()));
			dMD.setPriBal(fsd.getPrincipalSchd().subtract(fsd.getSchdPriPaid()));

			dMD.setIntPaidNow(BigDecimal.ZERO);
			dMD.setPriPaidNow(BigDecimal.ZERO);

			if (dMD.getIntBal().compareTo(BigDecimal.ZERO) > 0) {
				if (dMD.getRpyInt().compareTo(dMD.getIntBal()) > 0) {
					dMD.setIntPaidNow(dMD.getIntBal());
				} else {
					dMD.setIntPaidNow(dMD.getRpyInt());
				}
			}

			if (dMD.getPriBal().compareTo(BigDecimal.ZERO) > 0) {
				if (dMD.getRpyPri().compareTo(dMD.getPriBal()) > 0) {
					dMD.setPriPaidNow(dMD.getPriBal());
				} else {
					dMD.setPriPaidNow(dMD.getRpyPri());
				}
			}

			if (dMD.getIntPaidNow().compareTo(BigDecimal.ZERO) > 0
					|| dMD.getPriPaidNow().compareTo(BigDecimal.ZERO) > 0) {
				dMD = addNewRepaySchedule(dMD, rid, i);
			}

			if (dMD.getRpyInt().compareTo(BigDecimal.ZERO) == 0 && dMD.getRpyPri().compareTo(BigDecimal.ZERO) == 0) {
				break;
			}

		}

		return dMD;
	}

	// FIN REPAY SCHEDULE DETAILS
	public MigrationData addRepayScheduleByEMI(MigrationData dMD, ReferenceID rid) {
		List<FinanceScheduleDetail> fsdList = dMD.getFinScheduleDetails();
		dMD.setRpyEMI(dMD.getRpyPri().add(dMD.getRpyInt()));
		dMD.setRpyInt(BigDecimal.ZERO);
		dMD.setRpyPri(BigDecimal.ZERO);

		// for (int i = iStart; i < fsdList.size(); i++) {
		for (int i = 1; i < fsdList.size(); i++) {
			FinanceScheduleDetail fsd = fsdList.get(i);
			dMD.setIntBal(fsd.getProfitSchd().subtract(fsd.getSchdPftPaid()));
			dMD.setPriBal(fsd.getPrincipalSchd().subtract(fsd.getSchdPriPaid()));

			dMD.setIntPaidNow(BigDecimal.ZERO);
			dMD.setPriPaidNow(BigDecimal.ZERO);

			if (dMD.getIntBal().compareTo(BigDecimal.ZERO) > 0) {
				if (dMD.getRpyEMI().compareTo(dMD.getIntBal()) > 0) {
					dMD.setIntPaidNow(dMD.getIntBal());
				} else {
					dMD.setIntPaidNow(dMD.getRpyEMI());
				}

				dMD.setRpyEMI(dMD.getRpyEMI().subtract(dMD.getIntPaidNow()));
				dMD.setRpyInt(dMD.getRpyInt().add(dMD.getIntPaidNow()));
			}

			if (dMD.getPriBal().compareTo(BigDecimal.ZERO) > 0) {
				if (dMD.getRpyEMI().compareTo(dMD.getPriBal()) > 0) {
					dMD.setPriPaidNow(dMD.getPriBal());
				} else {
					dMD.setPriPaidNow(dMD.getRpyEMI());
				}

				dMD.setRpyEMI(dMD.getRpyEMI().subtract(dMD.getPriPaidNow()));
				dMD.setRpyPri(dMD.getRpyPri().add(dMD.getPriPaidNow()));
			}

			if (dMD.getIntPaidNow().compareTo(BigDecimal.ZERO) > 0
					|| dMD.getPriPaidNow().compareTo(BigDecimal.ZERO) > 0) {
				dMD = addNewRepaySchedule(dMD, rid, i);
			}

			if (dMD.getRpyEMI().compareTo(BigDecimal.ZERO) == 0) {
				break;
			}

		}

		return dMD;
	}

	public MigrationData addNewRepaySchedule(MigrationData dMD, ReferenceID rid, int iSch) {
		RepayScheduleDetail rsd = new RepayScheduleDetail();
		FinanceScheduleDetail fsd = dMD.getFinScheduleDetails().get(iSch);
		int iRcd = dMD.getFinReceiptDetails().size() - 1;
		int iRch = dMD.getFinReceiptHeaders().size() - 1;
		int iRph = dMD.getFinRepayHeaders().size() - 1;

		rsd.setFinReference(dMD.getFinanceMain().getFinReference());
		rsd.setSchDate(fsd.getSchDate());
		rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		rsd.setProfitSchdBal(dMD.getIntBal());
		rsd.setPrincipalSchdBal(dMD.getPriBal());
		rsd.setProfitSchdPayNow(dMD.getIntPaidNow());
		rsd.setPrincipalSchdPayNow(dMD.getPriPaidNow());

		int daysLate = DateUtility.getDaysBetween(fsd.getSchDate(),
				dMD.getFinReceiptDetails().get(iRcd).getValueDate());
		rsd.setDaysLate(daysLate);

		rsd.setRepayBalance(dMD.getIntBal().add(dMD.getPriBal()));
		rsd.setProfitSchd(fsd.getProfitSchd());
		rsd.setProfitSchdPaid(fsd.getSchdPftPaid());

		rsd.setPrincipalSchd(fsd.getPrincipalSchd());
		rsd.setPrincipalSchdPaid(fsd.getSchdPriPaid());
		rsd.setPenaltyPayNow(dMD.getRpyODC());

		dMD.setRepaySchID(dMD.getRepaySchID() + 1);
		rsd.setRepaySchID(dMD.getRepaySchID());
		rsd.setRepayID(rid.getRepayID());
		
		if(dMD.isWorkOnEMI()){
			// Update RepayHeader
			dMD.getFinRepayHeaders().get(iRph).setPriAmount(dMD.getRpyPri());
			dMD.getFinRepayHeaders().get(iRph).setPftAmount(dMD.getRpyInt());

			// Update Receipt Allocation Detail
			List<ReceiptAllocationDetail> alocList = dMD.getReceiptAllocationDetails();
			if(alocList != null && !alocList.isEmpty()){
				int maxAlocID = 0;
				ReceiptAllocationDetail pftAloc = null;
				for (ReceiptAllocationDetail aloc : alocList) {
					if (dMD.getFinReceiptHeaders().get(iRch).getReceiptID() != aloc.getReceiptID()) {
						continue;
					}
					
					if(StringUtils.equals(aloc.getAllocationType(), RepayConstants.ALLOCATION_PRI)){
						aloc.setPaidAmount(dMD.getRpyPri());
						if(maxAlocID < aloc.getAllocationID()){
							maxAlocID = aloc.getAllocationID();
						}
					}
					
					if(StringUtils.equals(aloc.getAllocationType(), RepayConstants.ALLOCATION_PFT)){
						pftAloc = aloc;
					}
				}
				
				if(pftAloc == null){
					pftAloc = new ReceiptAllocationDetail();
					dMD.getReceiptAllocationDetails().add(pftAloc);
					pftAloc.setAllocationID(maxAlocID+1);
					
					pftAloc.setReceiptAllocationid(rid.getReceiptAlocID()+1);
					rid.setReceiptAlocID(rid.getReceiptAlocID()+1);
					
					pftAloc.setReceiptID(dMD.getFinReceiptHeaders().get(iRch).getReceiptID());
					pftAloc.setAllocationType(RepayConstants.ALLOCATION_PFT);
					pftAloc.setAllocationTo(0);
				}
				pftAloc.setPaidAmount(dMD.getRpyInt());

			}
		}

		dMD.getRepayScheduleDetails().add(rsd);

		// Add Fin Repay Detail
		long paySeq = 0;
		FinanceRepayments rpd = new FinanceRepayments();
		rpd.setFinReference(rsd.getFinReference());
		rpd.setFinSchdDate(rsd.getSchDate());
		rpd.setFinRpyFor(rsd.getSchdFor());
		
		// If For Same Schedule date payment already exists then Payment sequence ID should increase
		for (FinanceRepayments rpy : dMD.getRepayDetails()) {
			if(DateUtility.compare(rpy.getFinSchdDate(), rsd.getSchDate()) != 0){
				continue;
			}
			if(rpy.getFinPaySeq() > paySeq){
				paySeq = rpy.getFinPaySeq();
			}
		}
		
		rpd.setFinPaySeq(paySeq+1);
		rpd.setFinRpyAmount(dMD.getFinReceiptHeaders().get(iRch).getReceiptAmount());
		rpd.setFinPostDate(dMD.getFinReceiptHeaders().get(iRch).getReceiptDate());
		rpd.setFinValueDate(dMD.getFinReceiptDetails().get(iRcd).getValueDate());
		rpd.setFinBranch(dMD.getFinanceMain().getFinBranch());
		rpd.setFinType(dMD.getFinanceMain().getFinType());
		rpd.setFinCustID(dMD.getFinanceMain().getCustID());
		rpd.setFinSchdPriPaid(rsd.getPrincipalSchdPayNow());
		rpd.setFinSchdPftPaid(rsd.getProfitSchdPayNow());
		rpd.setFinTotSchdPaid(rpd.getFinRpyAmount());

		dMD.getRepayDetails().add(rpd);

		// Update Paid and Balances
		fsd.setSchdPftPaid(fsd.getSchdPftPaid().add(dMD.getIntPaidNow()));
		fsd.setSchdPriPaid(fsd.getSchdPriPaid().add(dMD.getPriPaidNow()));

		if (fsd.getSchDate().compareTo(rid.getAppDate()) <= 0) {
			if (fsd.getProfitSchd().compareTo(fsd.getSchdPftPaid()) == 0) {
				fsd.setSchPftPaid(true);
			}

			if (fsd.getPrincipalSchd().compareTo(fsd.getSchdPriPaid()) == 0) {
				fsd.setSchPriPaid(true);
			}
		}

		if (!dMD.isWorkOnEMI()) {
			dMD.setRpyInt(dMD.getRpyInt().subtract(dMD.getIntPaidNow()));
			dMD.setRpyPri(dMD.getRpyPri().subtract(dMD.getPriPaidNow()));
		}

		return dMD;
	}

	public MigrationData addPresentments(MigrationData sMD, MigrationData dMD, ReferenceID rid) {
		int iRch = dMD.getFinReceiptHeaders().size() - 1;
		FinReceiptHeader rch = dMD.getFinReceiptHeaders().get(iRch);

		if (!StringUtils.equals(rch.getReceiptMode(), "PRESENT")) {
			return dMD;
		}

		List<PresentmentDetail> prdList = sMD.getPresentmentDetails();

		for (int i = 0; i < prdList.size(); i++) {
			if (prdList.get(i).getReceiptID() == 0) {
				if (prdList.get(i).getSchDate().compareTo(rch.getReceiptDate()) != 0) {
					continue;
				}
			} else {
				if (prdList.get(i).getReceiptID() != rch.getReceiptID()) {
					continue;
				}
			}

			PresentmentDetail prd = prdList.get(i);

			if (prdList.get(i).getReceiptID() == 0) {
				prd.setReceiptID(rch.getReceiptID());
			}

			if (StringUtils.equals(dMD.getFinReceiptHeaders().get(iRch).getReceiptModeStatus(),
					RepayConstants.PAYSTATUS_BOUNCE)) {
				prd.setStatus("B");
			} else if (StringUtils.equals(dMD.getFinReceiptHeaders().get(iRch).getReceiptModeStatus(),
					RepayConstants.PAYSTATUS_CANCEL)) {
				prd.setBounceID(0);
				prd.setStatus("F");
			} else {
				prd.setBounceID(0);
				prd.setStatus("S");
			}

			dMD.getPresentmentDetails().add(prd);
			sMD.getPresentmentDetails().remove(i);
			break;

		}

		return dMD;
	}

	// -----------------------------------------------------------------------------------
	// Basic Loan Reconciliation
	// -----------------------------------------------------------------------------------
	public MigrationData setBasicLoanRecon(MigrationData sMD, MigrationData dMD, ReferenceID rid) {
		BasicLoanRecon blr = dMD.getBasicLoanRecon();

		blr.setFinReference(dMD.getFinanceMain().getFinReference());
		blr.setBranch(dMD.getFinanceMain().getFinBranch());
		blr.setFintype(dMD.getFinanceMain().getFinType());
		blr.setCustID(dMD.getFinanceMain().getCustID());
		blr.setGraceExist(dMD.getFinanceMain().isAllowGrcPeriod());
		blr.setBpiExist(dMD.getFinanceMain().isAlwBPI());
		blr.setSanctionedAmount(dMD.getFinanceMain().getFinAssetValue());
		blr.setDisbursedAmount(dMD.getFinanceMain().getFinCurrAssetValue());
		blr.setUnDisbursedAmount(blr.getSanctionedAmount().subtract(blr.getDisbursedAmount()));

		SourceReport esr = dMD.getSourceReport();
		BigDecimal mp = BigDecimal.valueOf(100);

		blr.setSrcSanctionedAmount(esr.getLoan_Amount().multiply(mp));
		blr.setSrcDisbursedAmount(esr.getTotal_Disbursed_Amount().multiply(mp));
		blr.setSrcUnDisbursedAmount(esr.getTotal_UnDisbursed_Amount().multiply(mp));

		blr.setSrcEMISchd(esr.getTotal_EMI_Amount().multiply(mp));
		blr.setSrcPriSchd(esr.getTotal_Principal_Amount().multiply(mp));
		blr.setSrcIntSchd(esr.getTotal_Interest_Amount().multiply(mp));

		blr.setSrcEMIReceived(esr.getEMI_Received_Amount().multiply(mp));
		blr.setSrcPriReceived(esr.getPrincipal_Received_Amount().multiply(mp));
		blr.setSrcIntReceived(esr.getInterest_Received_Amount().multiply(mp));

		blr.setSrcEMIPastDue(esr.getTot_Pastdue_Amount().multiply(mp));
		blr.setSrcPriPastDue(esr.getTot_Pastdue_Principal().multiply(mp));
		blr.setSrcIntPastDue(esr.getTot_Pastdue_Interest().multiply(mp));

		// Destination Schedule
		List<FinanceScheduleDetail> dFsdList = dMD.getFinScheduleDetails();
		for (int i = 0; i < dFsdList.size(); i++) {
			FinanceScheduleDetail curSchd = dFsdList.get(i);
			blr.setPlfEMISchd(blr.getPlfEMISchd().add(curSchd.getRepayAmount()));
			blr.setPlfIntSchd(blr.getPlfIntSchd().add(curSchd.getProfitSchd()));
			blr.setPlfPriSchd(blr.getPlfPriSchd().add(curSchd.getPrincipalSchd()));

			// Paid Amount
			blr.setPlfIntReceived(blr.getPlfIntReceived().add(curSchd.getSchdPftPaid()));
			blr.setPlfPriReceived(blr.getPlfPriReceived().add(curSchd.getSchdPriPaid()));

			// Past Due Amount
			if (curSchd.getSchDate().compareTo(rid.getAppDate()) < 0) {
				blr.setPlfIntPastDue(
						blr.getPlfIntPastDue().add(curSchd.getProfitSchd()).subtract(curSchd.getSchdPftPaid()));
				blr.setPlfPriPastDue(
						blr.getPlfPriPastDue().add(curSchd.getPrincipalSchd()).subtract(curSchd.getSchdPriPaid()));
			}
		}

		blr.setPlfEMIReceived(blr.getPlfIntReceived().add(blr.getPlfPriReceived()));
		blr.setPlfEMIPastDue(blr.getPlfIntPastDue().add(blr.getPlfPriPastDue()));

		// Set Differences in schedule
		blr.setDifEMISchd(blr.getSrcEMISchd().subtract(blr.getPlfEMISchd()));
		blr.setDifIntSchd(blr.getSrcIntSchd().subtract(blr.getPlfIntSchd()));
		blr.setDifPriSchd(blr.getSrcPriSchd().subtract(blr.getPlfPriSchd()));

		blr.setDifEMIReceived(blr.getSrcEMIReceived().subtract(blr.getPlfEMIReceived()));
		blr.setDifIntReceived(blr.getSrcIntReceived().subtract(blr.getPlfIntReceived()));
		blr.setDifPriReceived(blr.getSrcPriReceived().subtract(blr.getPlfPriReceived()));

		blr.setDifEMIPastDue(blr.getSrcEMIPastDue().subtract(blr.getPlfEMIPastDue()));
		blr.setDifIntPastDue(blr.getSrcIntPastDue().subtract(blr.getPlfIntPastDue()));
		blr.setDifPriPastDue(blr.getSrcPriPastDue().subtract(blr.getPlfPriPastDue()));

		return dMD;
	}

	// -----------------------------------------------------------------------------------
	// Basic Loan Reconciliation
	// -----------------------------------------------------------------------------------
	public MigrationData setFinPftDetails(MigrationData dMD, ReferenceID rid) {
		FinanceMain fm = dMD.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = dMD.getFinScheduleDetails();
		FinanceProfitDetail fpd = dMD.getFinProfitDetails();

		String finRef = fm.getFinReference();
		Date dateSusp = null;

		int suspReq = SysParamUtil.getValueAsInt(SMTParameterConstants.SUSP_CHECK_REQ);

		if (dateSusp == null) {
			dateSusp = DateUtility.addDays(fm.getMaturityDate(), 1);
		}

		// Reset Totals
		resetCalculatedTotals(fm, fpd);

		// Calculate Accruals
		calAccruals(fm, fsdList, fpd, rid.getAppDate(), dateSusp);

		// Gross Totals
		calculateTotals(fm, fpd, dateSusp, rid.getAppDate());
		fpd.setAmzTillLBD(dMD.getSourceReport().getInterest_Prev_Month().multiply(BigDecimal.valueOf(100)));

		dMD.setFinProfitDetails(fpd);

		return dMD;
	}

	private void resetCalculatedTotals(FinanceMain finMain, FinanceProfitDetail pftDetail) {

		pftDetail.setFinReference(finMain.getFinReference());
		pftDetail.setFinStartDate(finMain.getFinStartDate());
		pftDetail.setCustId(finMain.getCustID());
		pftDetail.setCustCIF(finMain.getCustCIF());
		pftDetail.setFinBranch(finMain.getFinBranch());
		pftDetail.setFinType(finMain.getFinType());
		pftDetail.setFinCcy(finMain.getFinCcy());
		pftDetail.setFinPurpose(finMain.getFinPurpose());
		pftDetail.setFinContractDate(finMain.getFinContractDate());
		pftDetail.setFinApprovedDate(finMain.getFinApprovedDate());
		pftDetail.setFullPaidDate(finMain.getFinStartDate());
		pftDetail.setFinAmount(finMain.getFinAmount());
		pftDetail.setDownPayment(finMain.getDownPayment());
		pftDetail.setFinCommitmentRef(finMain.getFinCommitmentRef());
		pftDetail.setFinCategory(finMain.getFinCategory());
		pftDetail.setProductCategory("CONV");
		pftDetail.setFirstODDate(pftDetail.getFinStartDate());
		pftDetail.setPrvODDate(pftDetail.getFinStartDate());

		// Miscellaneous Fields
		pftDetail.setLastMdfDate(DateUtility.getAppDate());
		pftDetail.setMaturityDate(finMain.getMaturityDate());
		pftDetail.setFinIsActive(finMain.isFinIsActive());
		pftDetail.setClosingStatus(finMain.getClosingStatus());
		pftDetail.setRepayFrq(finMain.getRepayFrq());
		pftDetail.setFinStatus(finMain.getFinStatus());
		pftDetail.setFinStsReason(finMain.getFinStsReason());
		pftDetail.setFinWorstStatus(finMain.getFinStatus());

		// Setting date for recal purpose
		pftDetail.setFirstRepayDate(pftDetail.getFinStartDate());
		pftDetail.setPrvRpySchDate(pftDetail.getFinStartDate());
		pftDetail.setNSchdDate(pftDetail.getMaturityDate());
		pftDetail.setFirstDisbDate(pftDetail.getMaturityDate());
		pftDetail.setLatestDisbDate(pftDetail.getMaturityDate());
		pftDetail.setLatestRpyDate(finMain.getFinStartDate());

		// Interest Calculaiton on Pastdue
		if (StringUtils.equals(finMain.getPastduePftCalMthd(), CalculationConstants.PDPFTCAL_NOTAPP)) {
			pftDetail.setCalPftOnPD(false);
		} else {
			pftDetail.setCalPftOnPD(true);
		}
		pftDetail.setPftOnPDMethod(finMain.getPastduePftCalMthd());
		pftDetail.setPftOnPDMrg(finMain.getPastduePftMargin());

		// profit
		pftDetail.setTotalPftSchd(BigDecimal.ZERO);
		pftDetail.setTotalPftCpz(BigDecimal.ZERO);
		pftDetail.setTotalPftPaid(BigDecimal.ZERO);
		pftDetail.setTotalPftBal(BigDecimal.ZERO);

		pftDetail.setTdSchdPft(BigDecimal.ZERO);
		pftDetail.setTdPftCpz(BigDecimal.ZERO);
		pftDetail.setTdSchdPftPaid(BigDecimal.ZERO);
		pftDetail.setTdSchdPftBal(BigDecimal.ZERO);

		pftDetail.setNSchdPft(BigDecimal.ZERO);
		pftDetail.setNSchdPftDue(BigDecimal.ZERO);
		pftDetail.setPrvRpySchPft(BigDecimal.ZERO);

		// principal
		pftDetail.setTotalpriSchd(BigDecimal.ZERO);
		pftDetail.setTotalPriPaid(BigDecimal.ZERO);
		pftDetail.setTotalPriBal(BigDecimal.ZERO);

		pftDetail.setTdSchdPri(BigDecimal.ZERO);
		pftDetail.setTdSchdPriPaid(BigDecimal.ZERO);
		pftDetail.setTdSchdPriBal(BigDecimal.ZERO);

		pftDetail.setNSchdPri(BigDecimal.ZERO);
		pftDetail.setNSchdPriDue(BigDecimal.ZERO);
		pftDetail.setPrvRpySchPri(BigDecimal.ZERO);

		// advised Profit & Rebate
		pftDetail.setTotalAdvPftSchd(BigDecimal.ZERO);
		pftDetail.setTotalRbtSchd(BigDecimal.ZERO);
		pftDetail.setTdSchdAdvPft(BigDecimal.ZERO);
		pftDetail.setTdSchdRbt(BigDecimal.ZERO);

		// Accruals and amortizations
		pftDetail.setPftAccrued(BigDecimal.ZERO);
		pftDetail.setPftAccrueSusp(BigDecimal.ZERO);
		pftDetail.setPftAmz(BigDecimal.ZERO);
		pftDetail.setPftAmzNormal(BigDecimal.ZERO);
		pftDetail.setPftAmzPD(BigDecimal.ZERO);
		pftDetail.setPftAmzSusp(BigDecimal.ZERO);

		pftDetail.setTotalPriPaidInAdv(BigDecimal.ZERO);
		pftDetail.setTotalPftPaidInAdv(BigDecimal.ZERO);

		// Terms
		pftDetail.setNOInst(0);
		pftDetail.setNOPaidInst(0);
		pftDetail.setFutureInst(0);
		pftDetail.setRemainingTenor(0);
		pftDetail.setTotalTenor(0);

	}

	private void calAccruals(FinanceMain finMain, List<FinanceScheduleDetail> schdDetails,
			FinanceProfitDetail pftDetail, Date valueDate, Date dateSusp) {
		String finState = CalculationConstants.FIN_STATE_NORMAL;
		FinanceScheduleDetail prvSchd = null;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail nextSchd = null;

		Date prvSchdDate = null;
		Date curSchdDate = null;
		Date nextSchdDate = null;

		int valueToadd = SysParamUtil.getValueAsInt(SMTParameterConstants.ACCRUAL_CAL_ON);
		Date accrualDate = DateUtility.addDays(valueDate, valueToadd);
		Date pdDate = pftDetail.getPrvODDate();
		finMain.setFinRepaymentAmount(BigDecimal.ZERO);

		for (int i = 0; i < schdDetails.size(); i++) {
			curSchd = schdDetails.get(i);
			curSchdDate = curSchd.getSchDate();

			if (i == 0) {
				prvSchd = curSchd;
			} else {
				prvSchd = schdDetails.get(i - 1);
			}

			prvSchdDate = prvSchd.getSchDate();

			// Next details: in few cases there might be schedules present even
			// after the maturity date. ex: when calculating the fees
			if (curSchdDate.compareTo(finMain.getMaturityDate()) == 0 || i == schdDetails.size() - 1) {
				nextSchd = curSchd;
			} else {
				nextSchd = schdDetails.get(i + 1);
			}

			nextSchdDate = nextSchd.getSchDate();

			// -------------------------------------------------------------------------------------
			// Cumulative Totals
			// -------------------------------------------------------------------------------------
			calCumulativeTotals(pftDetail, curSchd);

			// -------------------------------------------------------------------------------------
			// Till Date and Future Date Totals
			// -------------------------------------------------------------------------------------

			// Till date Calculation
			if (curSchdDate.compareTo(valueDate) <= 0) {
				calTillDateTotals(pftDetail, curSchd);
			} else {
				calNextDateTotals(pftDetail, curSchd);
			}

			// -------------------------------------------------------------------------------------
			// ACCRUAL CALCULATION
			// -------------------------------------------------------------------------------------
			BigDecimal pftAmz = BigDecimal.ZERO;
			BigDecimal pftAmzNormal = BigDecimal.ZERO;
			BigDecimal pftAmzPD = BigDecimal.ZERO;
			BigDecimal acrNormal = BigDecimal.ZERO;

			// Amortization
			if (curSchdDate.compareTo(accrualDate) < 0) {
				pftAmz = curSchd.getProfitCalc();
			} else if (accrualDate.compareTo(prvSchdDate) > 0 && accrualDate.compareTo(nextSchdDate) <= 0) {
				int days = DateUtility.getDaysBetween(prvSchdDate, accrualDate);
				int daysInCurPeriod = curSchd.getNoOfDays();

				BigDecimal amzForCal = curSchd.getProfitCalc()
						.add(curSchd.getProfitFraction().subtract(prvSchd.getProfitFraction()));
				pftAmz = amzForCal.multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 9,
						RoundingMode.HALF_DOWN);
				pftAmz = pftAmz.add(prvSchd.getProfitFraction());
				pftAmz = CalculationUtil.roundAmount(pftAmz, finMain.getCalRoundingMode(), finMain.getRoundingTarget());

			} else {
				// Do Nothing
			}

			acrNormal = pftAmz.subtract(curSchd.getSchdPftPaid());

			if ((curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate()) && curSchdDate.compareTo(valueDate) < 0) {
				if ((curSchd.getSchdPriPaid().compareTo(curSchd.getPrincipalSchd()) < 0
						|| (curSchd.getSchdPftPaid().compareTo(curSchd.getProfitSchd()) < 0))) {
					finState = CalculationConstants.FIN_STATE_PD;
				}
			}

			if (curSchd.getSchDate().compareTo(pdDate) <= 0) {
				pftAmzNormal = pftAmz;
			}

			if (finState.equals(CalculationConstants.FIN_STATE_PD)) {
				// PD Amortization
				if (curSchdDate.compareTo(dateSusp) < 0) {
					pftAmzPD = pftAmz;
				} else if (dateSusp.compareTo(curSchdDate) >= 0 && dateSusp.compareTo(nextSchdDate) < 0) {
					int days = DateUtility.getDaysBetween(prvSchdDate, dateSusp);
					int daysInCurPeriod = curSchd.getNoOfDays();

					BigDecimal amzForCal = curSchd.getProfitCalc()
							.add(curSchd.getProfitFraction().subtract(prvSchd.getProfitFraction()));
					pftAmzPD = amzForCal.multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 9,
							RoundingMode.HALF_DOWN);
					pftAmzPD = pftAmzPD.add(prvSchd.getProfitFraction());
					pftAmzPD = CalculationUtil.roundAmount(pftAmzPD, finMain.getCalRoundingMode(),
							finMain.getRoundingTarget());

				} else {
					// Do Nothing
				}

				pftAmzPD = pftAmzPD.subtract(pftAmzNormal);
			}

			// This field will carry amortization till suspend date at this
			// stage
			pftDetail.setPftAccrueSusp(pftDetail.getPftAccrueSusp().add(acrNormal));

			// Set Amortization for various periods
			pftDetail.setPftAmz(pftDetail.getPftAmz().add(pftAmz));
			pftDetail.setPftAmzNormal(pftDetail.getPftAmzNormal().add(pftAmzNormal));
			pftDetail.setPftAmzPD(pftDetail.getPftAmzPD().add(pftAmzPD));
			pftDetail.setPftAccrued(pftDetail.getPftAccrued().add(acrNormal));
			
			// Set Total Repayment Amount on FinanceMain Object
			finMain.setFinRepaymentAmount(finMain.getFinRepaymentAmount().add(curSchd.getSchdPriPaid()));
		}

	}

	private static void calCumulativeTotals(FinanceProfitDetail pftDetail, FinanceScheduleDetail curSchd) {
		logger.debug("Entering");

		// profit
		pftDetail.setTotalPftSchd(pftDetail.getTotalPftSchd().add(curSchd.getProfitSchd()));
		pftDetail.setTotalPftCpz(pftDetail.getTotalPftCpz().add(curSchd.getCpzAmount()));
		pftDetail.setTotalPftPaid(pftDetail.getTotalPftPaid().add(curSchd.getSchdPftPaid()));

		// principal
		pftDetail.setTotalpriSchd(pftDetail.getTotalpriSchd().add(curSchd.getPrincipalSchd()));
		pftDetail.setTotalPriPaid(pftDetail.getTotalPriPaid().add(curSchd.getSchdPriPaid()));

		// advised Profit
		pftDetail.setTotalAdvPftSchd(pftDetail.getTotalAdvPftSchd().add(curSchd.getAdvProfit()));
		pftDetail.setTotalRbtSchd(pftDetail.getTotalRbtSchd().add(curSchd.getRebate()));

		// Schedule Information
		if ((curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate())) {
			if ((curSchd.isFrqDate() && !isHoliday(curSchd.getBpiOrHoliday()))
					|| curSchd.getSchDate().compareTo(pftDetail.getMaturityDate()) == 0) {
				// Installments, Paid and OD
				pftDetail.setNOInst(pftDetail.getNOInst() + 1);

				if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
					pftDetail.setNOPaidInst(pftDetail.getNOPaidInst() + 1);
				}

				// First Repayments Date and Amount
				if (curSchd.getSchDate().compareTo(pftDetail.getFinStartDate()) > 0) {
					if (pftDetail.getFirstRepayDate().compareTo(pftDetail.getFinStartDate()) == 0) {
						pftDetail.setFirstRepayDate(curSchd.getSchDate());
						pftDetail.setFirstRepayAmt(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
					}
				}
			}
		}

		// Final Repayments Amount
		if (curSchd.getSchDate().compareTo(pftDetail.getMaturityDate()) == 0) {
			pftDetail.setFinalRepayAmt(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
		}

		if (curSchd.isDisbOnSchDate()) {
			if (pftDetail.getFirstDisbDate().compareTo(pftDetail.getMaturityDate()) == 0) {
				pftDetail.setFirstDisbDate(curSchd.getSchDate());
				pftDetail.setLatestDisbDate(curSchd.getSchDate());
			}

			pftDetail.setLatestDisbDate(curSchd.getSchDate());
		}

		logger.debug("Leaving");
	}

	private static void calTillDateTotals(FinanceProfitDetail pftDetail, FinanceScheduleDetail curSchd) {
		logger.debug("Entering");

		// profit
		pftDetail.setTdSchdPft(pftDetail.getTdSchdPft().add(curSchd.getProfitSchd()));
		pftDetail.setTdPftCpz(pftDetail.getTdPftCpz().add(curSchd.getCpzAmount()));
		pftDetail.setTdSchdPftPaid(pftDetail.getTdSchdPftPaid().add(curSchd.getSchdPftPaid()));

		// principal
		pftDetail.setTdSchdPri(pftDetail.getTdSchdPri().add(curSchd.getPrincipalSchd()));
		pftDetail.setTdSchdPriPaid(pftDetail.getTdSchdPriPaid().add(curSchd.getSchdPriPaid()));

		// advised Profit
		pftDetail.setTdSchdAdvPft(pftDetail.getTdSchdAdvPft().add(curSchd.getAdvProfit()));
		pftDetail.setTdSchdRbt(pftDetail.getTdSchdRbt().add(curSchd.getRebate()));

		// Fully paid Date. Fully paid flags will be only used for setting the
		// fully paid date. will not update back in the schedule
		if (curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) <= 0) {
			curSchd.setSchPftPaid(true);
		}

		if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) <= 0) {
			curSchd.setSchPriPaid(true);
		}

		if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
			pftDetail.setFullPaidDate(curSchd.getSchDate());
		}

		if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
			if (curSchd.isFrqDate() && !isHoliday(curSchd.getBpiOrHoliday())) {
				pftDetail.setPrvRpySchDate(curSchd.getSchDate());
				pftDetail.setPrvRpySchPft(curSchd.getProfitSchd());
				pftDetail.setPrvRpySchPri(curSchd.getPrincipalSchd());
			}
		}

		pftDetail.setCurReducingRate(curSchd.getCalculatedRate());

		logger.debug("Leaving");
	}

	private static void calNextDateTotals(FinanceProfitDetail pftDetail, FinanceScheduleDetail curSchd) {
		logger.debug("Entering");

		// advance Profit and Principal
		pftDetail.setTotalPftPaidInAdv(pftDetail.getTotalPftPaidInAdv().add(curSchd.getSchdPftPaid()));
		pftDetail.setTotalPriPaidInAdv(pftDetail.getTotalPriPaidInAdv().add(curSchd.getSchdPriPaid()));

		// NEXT Schedule Details
		if ((curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate())) {
			if ((curSchd.isFrqDate() && !isHoliday(curSchd.getBpiOrHoliday()))
					|| curSchd.getSchDate().compareTo(pftDetail.getMaturityDate()) == 0) {
				if (pftDetail.getNSchdDate().compareTo(pftDetail.getMaturityDate()) == 0) {
					pftDetail.setNSchdDate(curSchd.getSchDate());
					pftDetail.setNSchdPri(curSchd.getPrincipalSchd());
					pftDetail.setNSchdPft(curSchd.getProfitSchd());
					pftDetail.setNSchdPriDue(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
					pftDetail.setNSchdPftDue(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
				}
				pftDetail.setFutureInst(pftDetail.getFutureInst() + 1);
			}
		}

		if (curSchd.getSchDate().compareTo(pftDetail.getMaturityDate()) == 0
				&& pftDetail.getNSchdDate().compareTo(pftDetail.getMaturityDate()) == 0) {
			pftDetail.setNSchdPri(curSchd.getPrincipalSchd());
			pftDetail.setNSchdPft(curSchd.getProfitSchd());
			pftDetail.setNSchdPriDue(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
			pftDetail.setNSchdPftDue(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
		}

		logger.debug("Leaving");
	}

	private static boolean isHoliday(String bpiOrHoliday) {
		if (StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_HOLIDAY)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_POSTPONE)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_UNPLANNED)) {
			return true;
		} else {
			return false;
		}
	}

	private void calculateTotals(FinanceMain finMain, FinanceProfitDetail pftDetail, Date dateSusp, Date valueDate) {
		logger.debug("Entering");

		pftDetail.setTotalPftBal(pftDetail.getTotalPftSchd().subtract(pftDetail.getTotalPftPaid()));
		pftDetail.setTotalPriBal(pftDetail.getTotalpriSchd().subtract(pftDetail.getTotalPriPaid()));
		pftDetail.setTdSchdPftBal(pftDetail.getTdSchdPft().subtract(pftDetail.getTdSchdPftPaid()));
		pftDetail.setTdSchdPriBal(pftDetail.getTdSchdPri().subtract(pftDetail.getTdSchdPriPaid()));

		// Current Flat Rate
		BigDecimal calPart1 = pftDetail.getTotalPftSchd().add(pftDetail.getTotalPftCpz());
		BigDecimal calPart2 = pftDetail.getTotalpriSchd().subtract(pftDetail.getTotalPftCpz());
		BigDecimal daysFactor = CalculationUtil.getInterestDays(pftDetail.getFinStartDate(),
				pftDetail.getMaturityDate(), finMain.getProfitDaysBasis());
		if (calPart2.compareTo(BigDecimal.ZERO) > 0) {
			pftDetail.setCurFlatRate(calPart1.divide((calPart2.multiply(new BigDecimal(100)).multiply(daysFactor)), 9,
					RoundingMode.HALF_DOWN));
		} else {
			pftDetail.setCurFlatRate(BigDecimal.ZERO);
		}

		// Calculated at individual level
		// pftDetail.setPftAccrued(pftDetail.getPftAmz().subtract(pftDetail.getTotalPftPaid()));

		// Suspense Amortization
		if (dateSusp.compareTo(pftDetail.getMaturityDate()) <= 0) {
			pftDetail.setPftAmzSusp(
					pftDetail.getPftAmz().subtract(pftDetail.getPftAmzNormal()).subtract(pftDetail.getPftAmzPD()));
			pftDetail.setPftInSusp(true);
			// Value Equivalent accrual after suspended date
			pftDetail.setPftAccrueSusp(pftDetail.getPftAccrued().subtract(pftDetail.getPftAccrueSusp()));
		} else {
			pftDetail.setPftInSusp(false);
			pftDetail.setPftAccrueSusp(BigDecimal.ZERO);
		}

		int tenor = DateUtility.getMonthsBetween(valueDate, pftDetail.getMaturityDate(), true);
		pftDetail.setRemainingTenor(tenor);

		tenor = DateUtility.getMonthsBetween(pftDetail.getFinStartDate(), pftDetail.getMaturityDate(), true);
		pftDetail.setTotalTenor(tenor);

		logger.debug("Leaving");
	}

	// -----------------------------------------------------------------------------------
	// Set GL Heads
	// -----------------------------------------------------------------------------------
	public MigrationData addPostingEntries(MigrationData dMD, ReferenceID rid) {
		List<FeeTypeVsGLMapping> feeGLList = rid.getFeeVsGLList();
		FinanceProfitDetail fpd = dMD.getFinProfitDetails();

		BigDecimal postAmount = BigDecimal.ZERO;
		rid.setTranOrder(0);
		rid.setLinkedTranID(rid.getLinkedTranID() + 1);

		// Loan Entry
		rid.setAccountType("LOAN");
		rid.setAccount(dMD.getFinanceMain().getFinType().trim().concat(rid.getAccountType()));
		postAmount = fpd.getTotalpriSchd().add(fpd.getTotalPftSchd()).subtract(fpd.getTdSchdPri())
				.subtract(fpd.getTdSchdPft());

		rid.setPostAmount(postAmount);
		rid.setTranDesc("Schedule Total O/S");
		rid.setDrOrcr("D");
		dMD = setPostingEntry(dMD, rid);

		// Unaccrued Interest
		rid.setAccountType("ACCINT");
		rid.setAccount(dMD.getFinanceMain().getFinType().trim().concat(rid.getAccountType()));
		postAmount = fpd.getTotalPftSchd().subtract(fpd.getAmzTillLBD());

		rid.setTranDesc("Un Accrued Interest");
		rid.setPostAmount(postAmount);
		rid.setDrOrcr("C");
		dMD = setPostingEntry(dMD, rid);

		// EMIRE
		rid.setAccountType("EMIRE");
		rid.setAccount(dMD.getFinanceMain().getFinType().trim().concat(rid.getAccountType()));
		postAmount = fpd.getTdSchdPriBal().add(fpd.getTdSchdPftBal());

		rid.setTranDesc("EMI Receivable");
		rid.setPostAmount(postAmount);
		rid.setDrOrcr("D");
		dMD = setPostingEntry(dMD, rid);

		// Interest Income
		rid.setAccountType("INTIN");
		rid.setAccount(dMD.getFinanceMain().getFinType().trim().concat(rid.getAccountType()));
		postAmount = fpd.getAmzTillLBD();

		rid.setTranDesc("Interest Income");
		rid.setPostAmount(postAmount);
		rid.setDrOrcr("C");
		dMD = setPostingEntry(dMD, rid);

		// ODC
		rid.setAccountType("LPPIN");
		rid.setAccount(dMD.getFinanceMain().getFinType().trim().concat(rid.getAccountType()));
		postAmount = rid.getOdcReceived();

		rid.setTranDesc("Late Payment Charge");
		rid.setPostAmount(postAmount);
		rid.setDrOrcr("C");
		dMD = setPostingEntry(dMD, rid);

		// Bounce
		rid.setAccountType("BOUIN");
		rid.setAccount(dMD.getFinanceMain().getFinType().trim().concat(rid.getAccountType()));
		postAmount = rid.getBounceReceived();

		rid.setTranDesc("Bounce Charges");
		rid.setPostAmount(postAmount);
		rid.setDrOrcr("C");
		dMD = setPostingEntry(dMD, rid);

		// Fee and Charges
		for (int i = 0; i < feeGLList.size(); i++) {
			FeeTypeVsGLMapping feeGL = feeGLList.get(i);
			if (feeGL.getFeePaid().compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			rid.setAccountType(feeGL.getFeeTypeCode());
			rid.setAccount(dMD.getFinanceMain().getFinType().trim().concat(feeGL.getGlHead()));
			postAmount = feeGL.getFeePaid();

			rid.setTranDesc(feeGL.getFeeTypeDesc());
			rid.setPostAmount(postAmount);
			rid.setDrOrcr("C");
			dMD = setPostingEntry(dMD, rid);
		}

		// Excess Amount
		if (dMD.getFinExcessAmounts().size() > 0) {
			rid.setAccountType("XESS");
			rid.setAccount(dMD.getFinanceMain().getFinType().trim().concat(rid.getAccountType()));
			postAmount = dMD.getFinExcessAmounts().get(0).getBalanceAmt();

			rid.setTranDesc("Excess Amount");
			rid.setPostAmount(postAmount);
			rid.setDrOrcr("C");
			dMD = setPostingEntry(dMD, rid);
		}

		// Bank
		rid.setAccountType("MIGR");
		rid.setAccount(dMD.getFinanceMain().getFinType().trim().concat(rid.getAccountType()));
		postAmount = rid.getTotalBankAmount();

		rid.setTranDesc("Migration Account");
		rid.setPostAmount(postAmount);
		rid.setDrOrcr("C");
		dMD = setPostingEntry(dMD, rid);

		return dMD;
	}

	public MigrationData setPostingEntry(MigrationData dMD, ReferenceID rid) {

		if (rid.getPostAmount().compareTo(BigDecimal.ZERO) == 0) {
			return dMD;
		}

		FinanceMain fm = dMD.getFinanceMain();
		ReturnDataSet rds = new ReturnDataSet();

		rds.setLinkedTranId(rid.getLinkedTranID());
		rds.setPostref(fm.getFinBranch().concat("-").concat(rid.getAccountType()).concat("-INR"));
		rds.setPostingId(fm.getFinType().concat("/").concat(fm.getFinReference()).concat("/MIGR")
				.concat(String.valueOf(rid.getTranOrder())));
		rds.setFinReference(fm.getFinReference());
		rds.setFinEvent("MIGR");
		rds.setPostDate(rid.getAppDate());
		rds.setValueDate(rid.getAppDate());
		rds.setAppValueDate(rid.getAppDate());
		rds.setCustAppDate(rid.getAppDate());
		rds.setAppDate(rid.getAppDate());

		if (StringUtils.equals(rid.getDrOrcr(), "D")) {
			if (rid.getPostAmount().compareTo(BigDecimal.ZERO) < 0) {
				rds.setPostAmount(rid.getPostAmount().abs());
				rds.setDrOrCr("C");
				rds.setTranCode("510");
				rds.setRevTranCode("010");
			} else {
				rds.setPostAmount(rid.getPostAmount().abs());
				rds.setDrOrCr("D");
				rds.setTranCode("010");
				rds.setRevTranCode("510");
			}
		} else {
			if (rid.getPostAmount().compareTo(BigDecimal.ZERO) < 0) {
				rds.setPostAmount(rid.getPostAmount().abs());
				rds.setDrOrCr("D");
				rds.setTranCode("010");
				rds.setRevTranCode("510");
			} else {
				rds.setPostAmount(rid.getPostAmount().abs());
				rds.setDrOrCr("C");
				rds.setTranCode("510");
				rds.setRevTranCode("010");
			}
		}

		rid.setTranOrder(rid.getTranOrder() + 10);
		rds.setTransOrder(rid.getTranOrder());
		rds.setTranDesc(rid.getTranDesc());
		rds.setDerivedTranOrder(rid.getTranOrder());
		rds.setShadowPosting(false);
		rds.setAccount(rid.getAccount());
		rds.setAmountType("D");
		rds.setPostStatus("S");
		rds.setCustId(fm.getCustID());
		rds.setAcCcy("INR");
		rds.setTranOrderId(String.valueOf(rid.getTranOrder()));
		rds.setPostToSys("E");
		rds.setPostBranch(fm.getFinBranch());
		rds.setExchangeRate(BigDecimal.ONE);
		rds.setPostAmountLcCcy(rds.getPostAmount());
		rds.setAccountType(rid.getAccountType());
		rds.setEntityCode("EQITAS");
		rds.setPostCategory(-1);

		dMD.getPostEntries().add(rds);

		if (StringUtils.equals(rds.getDrOrCr(), "C")) {
			rid.setTotalBankAmount(rid.getTotalBankAmount().subtract(rds.getPostAmount()));
		} else {
			rid.setTotalBankAmount(rid.getTotalBankAmount().add(rds.getPostAmount()));
		}

		return dMD;
	}

	// Sort Schedule Details
	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> fsdList) {

		if (fsdList != null && fsdList.size() > 0) {
			Collections.sort(fsdList, new Comparator<FinanceScheduleDetail>() {
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return fsdList;
	}

	// Sort Repayment Instructions
	private List<RepayInstruction> sortRepayInstructions(List<RepayInstruction> repayInstructions) {
		Collections.sort(repayInstructions, new Comparator<RepayInstruction>() {
			public int compare(RepayInstruction detail1, RepayInstruction detail2) {
				return DateUtility.compare(detail1.getRepayDate(), detail2.getRepayDate());
			}
		});
		return repayInstructions;
	}

	// -----------------------------------------------------------------------------------
	// Recalculate Overdue Details
	// -----------------------------------------------------------------------------------

	private MigrationData recalOverdue(MigrationData migrationData, ReferenceID rid) throws Exception {

		FinanceMain finMain = migrationData.getFinanceMain();
		finMain.setPastduePftCalMthd(CalculationConstants.PDPFTCAL_NOTAPP);
		List<FinanceScheduleDetail> finSchdDetails = migrationData.getFinScheduleDetails();
		FinODPenaltyRate penaltyRate = migrationData.getPenaltyrate();

		if(migrationData.getFinODDetails() == null){
			migrationData.setFinODDetails(new ArrayList<FinODDetails>());
		}
		
		List<FinanceRepayments> repayments = sortRepayDetails(migrationData.getRepayDetails());
		
		Date latestRpyDate = null;
		BigDecimal latestRpyPri = BigDecimal.ZERO;
		BigDecimal latestRpyPft = BigDecimal.ZERO;

		for (int i = 1; i < finSchdDetails.size(); i++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);
			
			// Not considering Past Fully Paid Schedule Dates
			if(curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) == 0 &&
					curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) == 0){
				continue;
			}

			//Include Today in Late payment Calculation or NOT?
			if (ImplementationConstants.LP_MARK_FIRSTDAY) {
				if (curSchd.getSchDate().compareTo(rid.getAppDate()) > 0) {
					break;
				}
			} else {
				if (curSchd.getSchDate().compareTo(rid.getAppDate()) >= 0) {
					break;
				}
			}

			FinODDetails fod = getLatePayMarkingService().createODDetails(curSchd, finMain, penaltyRate, rid.getAppDate());
			
			//Load Overdue Charge Recovery from Repayments Movements
			BigDecimal priBalMaxOD = curSchd.getPrincipalSchd();
			BigDecimal pftBalMaxOD = curSchd.getProfitSchd();
			for (int j = 0; j < repayments.size(); j++) {
				FinanceRepayments repayment = repayments.get(j);
				
				if(latestRpyDate == null || latestRpyDate.compareTo(repayment.getFinPostDate()) < 0){
					latestRpyDate = repayment.getFinPostDate();
					latestRpyPri = repayment.getFinSchdPriPaid();
					latestRpyPft = repayment.getFinSchdPftPaid();
				}

				//check the payment made against the actual schedule date 
				if (repayment.getFinSchdDate().compareTo(fod.getFinODSchdDate()) != 0) {
					continue;
				}

				//MAx OD amounts is same as repayments balance amounts
				if (fod.getFinODSchdDate().compareTo(repayment.getFinValueDate()) == 0) {
					if(repayment.getFinSchdPftPaid().compareTo(curSchd.getProfitSchd()) == 0 &&
							repayment.getFinSchdPriPaid().compareTo(curSchd.getPrincipalSchd()) == 0){
					}else{
						priBalMaxOD = priBalMaxOD.subtract(repayment.getFinSchdPriPaid());
						pftBalMaxOD = pftBalMaxOD.subtract(repayment.getFinSchdPftPaid());
					}
				}
			}
			
			// Schedule Fully Paid On Time
			if(priBalMaxOD.compareTo(BigDecimal.ZERO) == 0 && pftBalMaxOD.compareTo(BigDecimal.ZERO) == 0){
				continue;
			}
				
			fod.setFinMaxODPri(priBalMaxOD);
			fod.setFinMaxODPft(pftBalMaxOD);
			fod.setFinMaxODAmt(priBalMaxOD.add(pftBalMaxOD));

			//penalty calculation will done in SOD
			Date penaltyCalDate = rid.getAppDate();
			if (ImplementationConstants.LPP_CALC_SOD) {
				penaltyCalDate = DateUtility.addDays(rid.getAppDate(), 1);
			}

			getLatePayMarkingService().latePayMarking(finMain, fod, penaltyRate, 
					finSchdDetails, repayments, curSchd, rid.getAppDate(),penaltyCalDate);
			
			if(fod.getTotPenaltyAmt().compareTo(BigDecimal.ZERO) > 0 || 
					fod.getLPIAmt().compareTo(BigDecimal.ZERO) > 0){
				migrationData.getFinODDetails().add(fod);
			}
		}
		
		// Setting Latest Repayment Details in Profit Details
		FinanceProfitDetail profitDetail = migrationData.getFinProfitDetails();
		profitDetail.setLatestRpyDate(latestRpyDate);
		profitDetail.setLatestRpyPri(latestRpyPri);
		profitDetail.setLatestRpyPft(latestRpyPft);

		if(migrationData.getFinODDetails() != null && !migrationData.getFinODDetails().isEmpty()){
			getLatePayMarkingService().updateFinPftDetails(migrationData.getFinProfitDetails(),
					migrationData.getFinODDetails(), rid.getAppDate());
		}
		return migrationData;
	}
	
	private List<FinanceRepayments> sortRepayDetails(List<FinanceRepayments> repayments){

		if (repayments != null && repayments.size() > 1) {
			Collections.sort(repayments, new Comparator<FinanceRepayments>() {
				@Override
				public int compare(FinanceRepayments detail1, FinanceRepayments detail2) {
					int returnValue = DateUtility.compare(detail1.getFinSchdDate(), detail2.getFinSchdDate());
					if(returnValue != 0){
						return returnValue;
					}else{
						if (detail1.getFinPaySeq() > detail2.getFinPaySeq()) {
							return 1;
						} else if(detail1.getFinPaySeq() < detail2.getFinPaySeq()) {
							return -1;
						} 
						return 0;
					}
				}
			});
		}
		return repayments;
	}
	
	/**
	 * Method for Checking Schedule is Fully Paid or not
	 * 
	 * @param finReference
	 * @param scheduleDetails
	 * @return
	 */
	public MigrationData setFinanceStatus(MigrationData dMD) {
		//Check Total Finance profit Amount
		boolean fullyPaid = true;
		List<FinanceScheduleDetail> shcdList = dMD.getFinScheduleDetails();
		for (int i = 1; i < shcdList.size(); i++) {
			FinanceScheduleDetail curSchd = shcdList.get(i);

			// Profit
			if ((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Principal
			if ((curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Fees
			if ((curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Insurance
			if ((curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Supplementary Rent
			if ((curSchd.getSuplRent().subtract(curSchd.getSuplRentPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Increased Cost
			if ((curSchd.getIncrCost().subtract(curSchd.getIncrCostPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}
		}

		// Check Receivable Advises paid Fully or not
		if (fullyPaid) {
			List<ManualAdvise> advList = dMD.getManualAdvises();
			for (ManualAdvise adv : advList) {
				BigDecimal adviseBal = adv.getAdviseAmount().subtract(adv.getPaidAmount()).subtract(adv.getWaivedAmount());
				if (adviseBal!=null && adviseBal.compareTo(BigDecimal.ZERO) > 0) {
					fullyPaid = false;
					break;
				}
			}
		}
		
		if (fullyPaid) {
			dMD.getFinanceMain().setFinIsActive(false);
			dMD.getFinanceMain().setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
			// Previous Month Amortization reset to Total Profit to avoid posting on closing Month End
			dMD.getFinProfitDetails().setPrvMthAmz(dMD.getFinProfitDetails().getTotalPftSchd());
			dMD.getFinProfitDetails().setAmzTillLBD(dMD.getFinProfitDetails().getTotalPftSchd());
			
		} else {
			dMD.getFinanceMain().setFinIsActive(true);
			dMD.getFinanceMain().setClosingStatus(null);
		}

		dMD.getFinProfitDetails().setFinIsActive(dMD.getFinanceMain().isFinIsActive());
		dMD.getFinProfitDetails().setClosingStatus(dMD.getFinanceMain().getClosingStatus());

		return dMD;
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public FinAdvancePaymentsDAO getFinAdvancePaymentsDAO() {
		return finAdvancePaymentsDAO;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public ReceiptAllocationDetailDAO getReceiptAllocationDetailDAO() {
		return receiptAllocationDetailDAO;
	}

	public void setReceiptAllocationDetailDAO(ReceiptAllocationDetailDAO receiptAllocationDetailDAO) {
		this.receiptAllocationDetailDAO = receiptAllocationDetailDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public PresentmentDetailDAO getPresentmentDetailDAO() {
		return presentmentDetailDAO;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public PaymentHeaderDAO getPaymentHeaderDAO() {
		return paymentHeaderDAO;
	}

	public void setPaymentHeaderDAO(PaymentHeaderDAO paymentHeaderDAO) {
		this.paymentHeaderDAO = paymentHeaderDAO;
	}

	public PaymentDetailDAO getPaymentDetailDAO() {
		return paymentDetailDAO;
	}

	public void setPaymentDetailDAO(PaymentDetailDAO paymentDetailDAO) {
		this.paymentDetailDAO = paymentDetailDAO;
	}

	public PaymentInstructionDAO getPaymentInstructionDAO() {
		return paymentInstructionDAO;
	}

	public void setPaymentInstructionDAO(PaymentInstructionDAO paymentInstructionDAO) {
		this.paymentInstructionDAO = paymentInstructionDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinODPenaltyRateDAO getFinODPenaltyRateDAO() {
		return finODPenaltyRateDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public ProvisionDAO getProvisionDAO() {
		return provisionDAO;
	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public FinFeeScheduleDetailDAO getFinFeeScheduleDetailDAO() {
		return finFeeScheduleDetailDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public FinPlanEmiHolidayDAO getFinPlanEmiHolidayDAO() {
		return finPlanEmiHolidayDAO;
	}

	public void setFinPlanEmiHolidayDAO(FinPlanEmiHolidayDAO finPlanEmiHolidayDAO) {
		this.finPlanEmiHolidayDAO = finPlanEmiHolidayDAO;
	}

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return recoveryDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public FinStatusDetailDAO getFinStatusDetailDAO() {
		return finStatusDetailDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public MandateDAO getMandateDAO() {
		return mandateDAO;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public JountAccountDetailDAO getJountAccountDetailDAO() {
		return jountAccountDetailDAO;
	}

	public void setJountAccountDetailDAO(JountAccountDetailDAO jountAccountDetailDAO) {
		this.jountAccountDetailDAO = jountAccountDetailDAO;
	}

	public GuarantorDetailDAO getGuarantorDetailDAO() {
		return guarantorDetailDAO;
	}

	public void setGuarantorDetailDAO(GuarantorDetailDAO guarantorDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
	}

	public FinServiceInstrutionDAO getFinServiceInstructionDAO() {
		return finServiceInstructionDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	public BasicLoanReconDAO getBasicLoanReconDAO() {
		return basicLoanReconDAO;
	}

	public void setBasicLoanReconDAO(BasicLoanReconDAO basicLoanReconDAO) {
		this.basicLoanReconDAO = basicLoanReconDAO;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public LatePayMarkingService getLatePayMarkingService() {
		return latePayMarkingService;
	}

	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

}
