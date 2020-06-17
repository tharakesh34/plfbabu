//
// Decompiled by Procyon v0.5.36
//

package com.pennant.datamigration.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.LatePayBucketService;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.payment.PaymentDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.reports.SOAReportGenerationDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.datamigration.dao.DRFinanceDetailsDAO;
import com.pennant.datamigration.model.BlockedFinance;
import com.pennant.datamigration.model.DRCorrections;
import com.pennant.datamigration.model.DREMIHoliday;
import com.pennant.datamigration.model.DRFinanceDetails;
import com.pennant.datamigration.model.DRRateReviewScheduleChange;
import com.pennant.datamigration.model.MigrationData;
import com.pennant.datamigration.model.ReferenceID;
import com.pennant.datamigration.service.DMTransactionService;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.TableType;

public class DMTransactionServiceImpl implements DMTransactionService {
	private FinanceMainDAO financeMainDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private OverdueChargeRecoveryDAO recoveryDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinStatusDetailDAO finStatusDetailDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private FinanceTypeDAO financeTypeDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private BaseRateDAO baseRateDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ReceiptAllocationDetailDAO allocationDetailDAO;
	private SOAReportGenerationDAO soaReportGenerationDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private DRFinanceDetailsDAO drFinanceDetailsDAO;
	private AccrualService accrualService;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private PresentmentDetailDAO presentmentDetailDAO;
	private FinFeeReceiptDAO finFeeReceiptDAO;
	private PaymentDetailDAO paymentDetailDAO;
	private PostingsDAO postingsDAO;
	private LatePayMarkingService latePayMarkingService;
	private LatePayBucketService latePayBucketService;
	private ScheduleCalculator scheduleCalculator;
	private FinLogEntryDetailDAO finLogEntryDetailDAO;

	BigDecimal emiPaid;
	BigDecimal principalPaid;
	BigDecimal interestPaid;
	BigDecimal tdsPaid;
	BigDecimal excessAdjustAmt;
	BigDecimal lppPaid;
	BigDecimal totEmiPaid;
	BigDecimal totPrincipalPaid;
	BigDecimal totInterestPaid;
	BigDecimal totTdsPaid;
	int newRPSID;
	BigDecimal newExcess;
	boolean isCorrectionReq;

	public DMTransactionServiceImpl() {
		this.emiPaid = BigDecimal.ZERO;
		this.principalPaid = BigDecimal.ZERO;
		this.interestPaid = BigDecimal.ZERO;
		this.tdsPaid = BigDecimal.ZERO;
		this.excessAdjustAmt = BigDecimal.ZERO;
		this.lppPaid = BigDecimal.ZERO;
		this.totEmiPaid = BigDecimal.ZERO;
		this.totPrincipalPaid = BigDecimal.ZERO;
		this.totInterestPaid = BigDecimal.ZERO;
		this.totTdsPaid = BigDecimal.ZERO;
		this.newRPSID = 11;
		this.newExcess = BigDecimal.ZERO;
		this.isCorrectionReq = false;
	}

	public FinScheduleData getFinanceDetails(final String finReference, final String type,
			final DRRateReviewScheduleChange drRRS) {
		final FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinReference(finReference);
		finSchData.setFinanceMain(financeMainDAO.getFinanceMainById(finReference, "_View", false));
		if (!finSchData.getFinanceMain().isFinIsActive()) {
			drRRS.setReconSts(-1);
			drRRS.setRemarks("Inactive Loans");
			return finSchData;
		}
		final Date appDate = SysParamUtil.getAppDate();
		if (finSchData.getFinanceMain().getMaturityDate().compareTo(appDate) <= 0) {
			drRRS.setReconSts(-2);
			drRRS.setRemarks("Maturity date Passed");
			return finSchData;
		}

		finSchData.setFinanceType(
				financeTypeDAO.getFinanceTypeByID(finSchData.getFinanceMain().getFinType(), type));
		finSchData.setFinanceScheduleDetails(
				financeScheduleDetailDAO.getFinScheduleDetails(finReference, type, false));
		finSchData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finReference, type, false));
		finSchData.setFinPftDeatil(this.getProfitDetailsDAO().getFinProfitDetailsById(finReference));
		if (StringUtils.equals(drRRS.getCategory(), "10")) {
			return finSchData;
		}
		finSchData.setDisbursementDetails(
				this.getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));
		finSchData.setRepayDetails(this.getFinanceRepaymentsDAO().getFinRepayListByFinRef(finReference, false, type));
		finSchData.setPenaltyDetails(this.getRecoveryDAO().getFinancePenaltysByFinRef(finReference, type));
		return finSchData;
	}

	public FinScheduleData getRRFinanceDetails(final String finReference, final String type) {
		final FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinReference(finReference);
		finSchData.setFinanceMain(financeMainDAO.getFinanceMainById(finReference, "_View", false));
		final String finType = finSchData.getFinanceMain().getFinType();
		finSchData.setFinanceType(financeTypeDAO.getFinanceTypeByID(finType, type));
		finSchData.setFinanceScheduleDetails(
				financeScheduleDetailDAO.getFinScheduleDetails(finReference, type, false));
		finSchData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finReference, type, false));
		finSchData.setFinPftDeatil(this.getProfitDetailsDAO().getFinProfitDetailsById(finReference));
		return finSchData;
	}

	public int validateSchedules(final FinScheduleData fsd, final TableType type,
			final DRRateReviewScheduleChange drRRS) {
		final FinanceMain finMain = fsd.getFinanceMain();
		final List<FinanceScheduleDetail> fsdList = fsd.getFinanceScheduleDetails();
		final int lastIdx = fsdList.size() - 1;
		BigDecimal diffMaxExpected = BigDecimal.ZERO;
		drRRS.setOldEMI(fsdList.get(lastIdx - 1).getRepayAmount());
		drRRS.setNewEMI(drRRS.getOldEMI());
		drRRS.setOldLastEMI(fsdList.get(lastIdx).getRepayAmount());
		drRRS.setNewLastEMI(drRRS.getOldLastEMI());
		drRRS.setDiffEMI(drRRS.getOldLastEMI().subtract(drRRS.getOldEMI()).abs());
		drRRS.setDiffLastEMI(drRRS.getDiffEMI());
		drRRS.setNoOfTerms(finMain.getNumberOfTerms());
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		diffMaxExpected = new BigDecimal((drRRS.getNoOfTerms() / 2 + 1) * 1000);
		drRRS.setFlexi(true);
		for (int i = 0; i < lastIdx; ++i) {
			curSchd = fsdList.get(i);
			if (finMain.isAlwFlexi() && i < lastIdx - 1) {
				diffMaxExpected = diffMaxExpected.add(curSchd.getProfitFraction());
			}
			drRRS.setOldInterest(drRRS.getOldInterest().add(curSchd.getProfitSchd()).add(curSchd.getCpzAmount()));
		}
		diffMaxExpected = diffMaxExpected.abs();
		drRRS.setDiffMaxExpected(diffMaxExpected);
		drRRS.setNewInterest(drRRS.getOldInterest());
		if (drRRS.getDiffEMI().compareTo(drRRS.getDiffMaxExpected()) <= 0) {
			drRRS.setReconSts(-20);
			drRRS.setRemarks("With in Limits before Recalculation");
		}
		return 0;
	}

	public void adjustUMFC(final FinScheduleData fsd, final BigDecimal difInterest, String eventCode)
			throws InterfaceException {
		final FinanceMain financeMain = fsd.getFinanceMain();
		final AEEvent aeEvent = new AEEvent();
		aeEvent.setAeAmountCodes(new AEAmountCodes());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		final Date curBDay = DateUtility.getAppDate();
		aeEvent.setBranch(financeMain.getFinBranch());

		if (StringUtils.isNotBlank(financeMain.getPromotionCode()) && financeMain.getPromotionSeqId() == 0L) {
			aeEvent.getAcSetIDList()
			.add(AccountingConfigCache.getAccountSetID(financeMain.getPromotionCode(), eventCode, 2));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getFinType(), eventCode, 1));
		}
		amountCodes.setPftChg(difInterest);
		amountCodes.setBusinessvertical(financeMain.getBusinessVerticalCode());
		aeEvent.setFinReference(financeMain.getFinReference());
		aeEvent.setFinType(financeMain.getFinType());
		aeEvent.setBranch(financeMain.getFinBranch());
		aeEvent.setCcy(financeMain.getFinCcy());
		aeEvent.setPostingUserBranch(financeMain.getFinBranch());
		aeEvent.setValueDate(curBDay);
		aeEvent.setPostDate(curBDay);
		aeEvent.setEntityCode(financeMain.getLovDescEntityCode());
		aeEvent.setEOD(true);
		aeEvent.setAccountingEvent(eventCode);
		aeEvent.setPostingUserBranch("EOD");
		amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setFinType(financeMain.getFinType());
		amountCodes.setBusinessvertical(financeMain.getBusinessVerticalCode());
		amountCodes.setAlwflexi(financeMain.isAlwFlexi());
		amountCodes.setFinbranch(financeMain.getFinBranch());
		amountCodes.setEntitycode(financeMain.getEntityCode());
		amountCodes.setEntitycode(financeMain.getLovDescEntityCode());

		/*HashMap<String, Object> dataMap = (HashMap<String, Object>) aeEvent.getDataMap();
		dataMap = (HashMap<String, Object>) amountCodes.getDeclaredFieldValues((HashMap) dataMap);
		final Map<String, Object> map = (Map<String, Object>) this.financeMainDAO
				.getGLSubHeadCodes(financeMain.getFinReference());
		dataMap.put("emptype", map.get("emptype"));
		dataMap.put("branchcity", map.get("branchcity"));
		dataMap.put("fincollateralreq", map.get("fincollateralreq"));
		dataMap.put("btloan", financeMain.getLoanCategory());
		dataMap.put("division", map.get("FinDivision"));
		aeEvent.setDataMap(dataMap);*/
		this.getPostingsPreparationUtil().postAccounting(aeEvent);
	}

	public void adjustReage(final FinScheduleData fsData, DREMIHoliday drEH, String eventCode)
			throws InterfaceException {
		final FinanceMain financeMain = fsData.getFinanceMain();

		final AEEvent aeEvent = new AEEvent();
		aeEvent.setAeAmountCodes(new AEAmountCodes());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		final Date curBDay = DateUtility.getAppDate();
		aeEvent.setBranch(financeMain.getFinBranch());
		if (StringUtils.isNotBlank(financeMain.getPromotionCode()) && financeMain.getPromotionSeqId() == 0L) {
			aeEvent.getAcSetIDList()
			.add(AccountingConfigCache.getAccountSetID(financeMain.getPromotionCode(), eventCode, 2));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getFinType(), eventCode, 1));
		}

		amountCodes.setPastCpzChg(drEH.getPastCpzChg());
		amountCodes.setInstChg(drEH.getInstChg());
		amountCodes.setInstIntChg(drEH.getInstIntChg());
		amountCodes.setInstPriChg(drEH.getInstPriChg());
		amountCodes.setPftChg(drEH.getPftChg());
		amountCodes.setBusinessvertical(financeMain.getBusinessVerticalCode());

		aeEvent.setFinReference(financeMain.getFinReference());
		aeEvent.setFinType(financeMain.getFinType());
		aeEvent.setBranch(financeMain.getFinBranch());
		aeEvent.setCcy(financeMain.getFinCcy());
		aeEvent.setPostingUserBranch(financeMain.getFinBranch());
		aeEvent.setValueDate(curBDay);
		aeEvent.setPostDate(curBDay);
		aeEvent.setEntityCode(financeMain.getLovDescEntityCode());
		aeEvent.setEOD(false);
		aeEvent.setAccountingEvent(eventCode);
		aeEvent.setPostingUserBranch("EOD");
		aeEvent.setCcy("INR");
		aeEvent.setEntityCode(fsData.getFinanceMain().getEntityCode());

		amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setFinType(financeMain.getFinType());
		amountCodes.setBusinessvertical(financeMain.getBusinessVerticalCode());
		amountCodes.setAlwflexi(financeMain.isAlwFlexi());
		amountCodes.setFinbranch(financeMain.getFinBranch());
		amountCodes.setEntitycode(financeMain.getEntityCode());
		amountCodes.setEntitycode(financeMain.getLovDescEntityCode());

		HashMap<String, Object> dataMap = (HashMap<String, Object>) aeEvent.getDataMap();
		dataMap = (HashMap<String, Object>) amountCodes.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);

		/*
		 * HashMap<String, Object> dataMap = (HashMap<String, Object>) aeEvent.getDataMap(); dataMap = (HashMap<String,
		 * Object>) amountCodes.getDeclaredFieldValues(dataMap); final Map<String, Object> map = financeMainDAO
		 * .getGLSubHeadCodes(financeMain.getFinReference()); dataMap.put("emptype", map.get("emptype"));
		 * dataMap.put("branchcity", map.get("branchcity")); dataMap.put("fincollateralreq",
		 * map.get("fincollateralreq")); dataMap.put("btloan", financeMain.getLoanCategory()); dataMap.put("division",
		 * map.get("FinDivision"));
		 *
		 * aeEvent.setDataMap(dataMap);
		 */
		this.getPostingsPreparationUtil().postAccounting(aeEvent);
	}

	public List<DRFinanceDetails> getDRFinanceReferenceList() {
		return this.drFinanceDetailsDAO.getDRFinanceReferenceList();
	}

	public List<DRFinanceDetails> getDRCorrectionDM(final String reasonCode) {
		return this.drFinanceDetailsDAO.getDRCorrectionDM(reasonCode);
	}

	public void removeBlockedFinance(final BlockedFinance blockedFinance) {
		this.drFinanceDetailsDAO.removeBlockedFinance(blockedFinance);
	}

	public void setFinanceMainDAO(final FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return this.financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(final FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setFinanceScheduleDetailDAO(final FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return this.recoveryDAO;
	}

	public void setRecoveryDAO(final OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return this.finODDetailsDAO;
	}

	public void setFinODDetailsDAO(final FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinODPenaltyRateDAO getFinODPenaltyRateDAO() {
		return this.finODPenaltyRateDAO;
	}

	public void setFinODPenaltyRateDAO(final FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return this.financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(final FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public FinStatusDetailDAO getFinStatusDetailDAO() {
		return this.finStatusDetailDAO;
	}

	public void setFinStatusDetailDAO(final FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return this.financeSuspHeadDAO;
	}

	public void setFinanceSuspHeadDAO(final FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public void setFinanceTypeDAO(final FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setRepayInstructionDAO(final RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public BaseRateDAO getBaseRateDAO() {
		return this.baseRateDAO;
	}

	public void setBaseRateDAO(final BaseRateDAO baseRateDAO) {
		this.baseRateDAO = baseRateDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return this.finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(final FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return this.finReceiptDetailDAO;
	}

	public void setFinReceiptDetailDAO(final FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public ReceiptAllocationDetailDAO getAllocationDetailDAO() {
		return this.allocationDetailDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return this.finExcessAmountDAO;
	}

	public void setAllocationDetailDAO(final ReceiptAllocationDetailDAO allocationDetailDAO) {
		this.allocationDetailDAO = allocationDetailDAO;
	}

	public SOAReportGenerationDAO getSoaReportGenerationDAO() {
		return this.soaReportGenerationDAO;
	}

	public void setSoaReportGenerationDAO(final SOAReportGenerationDAO soaReportGenerationDAO) {
		this.soaReportGenerationDAO = soaReportGenerationDAO;
	}

	public void setFinExcessAmountDAO(final FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public List<FinReceiptHeader> getFinReceiptHeaderById(final String finReference, final String type) {
		return null;
	}

	public void setDrFinanceDetailsDAO(final DRFinanceDetailsDAO drFinanceDetailsDAO) {
		this.drFinanceDetailsDAO = drFinanceDetailsDAO;
	}

	public void setAccrualService(final AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return this.postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(final PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return this.profitDetailsDAO;
	}

	public void setProfitDetailsDAO(final FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public int updateFullFinanceDetails(final FinScheduleData financeDetails, final DRFinanceDetails drFinanceDetails,
			final String type) {
		return 0;
	}

	public MigrationData procEHSchedule(MigrationData sMD, ReferenceID rid) throws Exception {
		MigrationData dMD = sMD;
		FinanceMain fm = dMD.getFinanceMain();
		FinanceProfitDetail fpd = dMD.getFinProfitDetails();
		DREMIHoliday drEH = dMD.getDrEH();
		int iUPEH = -1;
		int newUPEH = 0;
		int cntSchd = 0;
		boolean isIntServiceLoan = true;

		if (!StringUtils.equals(fm.getScheduleMethod(), "PFT")) {
			isIntServiceLoan = false;
		}

		BigDecimal actPriSchd = BigDecimal.ZERO;
		List<FinanceScheduleDetail> ehFsdList = new ArrayList<FinanceScheduleDetail>(1);

		List<RepayInstruction> riList = dMD.getRepayInstructions();
		if (riList.size() > 1) {
			RepayInstruction ri = riList.get(riList.size() - 1);
			if (ri.getRepayDate().compareTo(fm.getMaturityDate()) == 0) {
				riList.remove(riList.size() - 1);
			}
		}

		//Defaulting to Capitalize, Add terms and Reqcalculate
		if (StringUtils.isBlank(drEH.getEHMethod())) {
			drEH.setEHMethod("CE");
		}

		drEH.setAppDate(rid.getAppDate());
		drEH.setBranchCode(fm.getFinBranch());
		drEH.setProductType(fm.getProductCategory());
		drEH.setFinType(fm.getFinType());
		drEH.setOldBucket(fm.getDueBucket());
		drEH.setOldEMIOS(fpd.getTdSchdPftBal().add(fpd.getTdSchdPriBal()));
		drEH.setOldBalTenure(fpd.getRemainingTenor());
		drEH.setOldMaturity(fm.getMaturityDate());
		drEH.setLastBilledDate(fpd.getPrvRpySchDate());
		drEH.setActLoanAmount(fm.getFinAssetValue());
		drEH.setOldTenure(fpd.getTotalTenor());
		drEH.setOldInterest(fpd.getTotalPftSchd());
		drEH.setOldMaxUnPlannedEMI(fm.getMaxUnplannedEmi());
		drEH.setOldAvailedUnPlanEMI(fm.getAvailedUnPlanEmi());

		BigDecimal oldTotalIntToAccrue = fpd.getTotalPftSchd().add(fpd.getTotalPftCpz());

		if (drEH.getEHInst() == 0) {
			drEH.setEHStatus("E");
			drEH.setEhStatusRemarks("Requested Postponements are ZERO");
			dMD = saveEHSchedule(dMD, rid, false);
			return dMD;
		}

		if (!fm.isFinIsActive()) {
			drEH.setEHStatus("E");
			drEH.setEhStatusRemarks("Inactive Loan");
			dMD = saveEHSchedule(dMD, rid, false);
			return dMD;
		}

		if (fm.getMaturityDate().compareTo(rid.getAppDate()) <= 0) {
			drEH.setEHStatus("E");
			drEH.setEhStatusRemarks("Maturity Date Crossed");
			dMD = saveEHSchedule(dMD, rid, false);
			return dMD;
		}

		boolean isDailyFrqLoan = false;
		if (StringUtils.equals(fm.getRepayFrq().substring(0, 1), FrequencyCodeTypes.FRQ_DAILY)) {
			isDailyFrqLoan = true;
		}

		List<FinanceScheduleDetail> fsdList = dMD.getFinScheduleDetails();
		drEH.setOldFinalEMI(fsdList.get(fsdList.size() - 1 - fm.getAdvTerms()).getRepayAmount());
		Date oldEventFromdate=null;
		Date oldEventtodate=null;

		// In case of Moratorium effect on grace period
		int mortOnGrcTermCount = 0;
		boolean startOnGrcPrd = false;
		for (int iFsd = 0; iFsd < fsdList.size() - fm.getAdvTerms(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);

			if (cntSchd >= drEH.getEHInst()) {
				break;
			}

			if (fsd.getSchDate().compareTo(drEH.getLastBilledDate()) == 0) {
				drEH.setLastBilledInstNo(fsd.getInstNumber());
			}

			// Skip if schedule date is before requested date
			if (fsd.getSchDate().compareTo(drEH.getEHStartDate()) < 0) {
				continue;
			}

			if (fsd.getPresentmentId() > 0) {
				//cntSchd = cntSchd + 1;//FIXME
				continue;
			}

			// Skip if schedule date is after allowed date
			if (fsd.getSchDate().compareTo(drEH.getEhEndDate()) > 0) {
				break;
			}

			actPriSchd = fsd.getPrincipalSchd().subtract(fsd.getPartialPaidAmt());
			// Skip if not installment payment
			if (fsd.getProfitSchd().compareTo(BigDecimal.ZERO) == 0 && actPriSchd.compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			if (isIntServiceLoan) {
				if (actPriSchd.compareTo(BigDecimal.ZERO) > 0) {
					isIntServiceLoan = false;
				}
			}

			// Skip if already paid
			if (fsd.getProfitSchd().compareTo(fsd.getSchdPftPaid()) == 0
					&& fsd.getPrincipalSchd().compareTo(fsd.getSchdPriPaid()) == 0) {
				//cntSchd = cntSchd + 1;//FIXME
				continue;
			}

			//Skip if scheduled amount already paid
			if (fsd.getProfitSchd().compareTo(BigDecimal.ZERO) == 0
					&& actPriSchd.compareTo(fsd.getPrincipalSchd().subtract(fsd.getPartialPaidAmt())) == 0) {
				cntSchd = cntSchd + 1;
				continue;
			}

			// FLAG_30DAYS_FIXED, FLAG_GLMINPFT not addressed.
			if (StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)
					|| StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_POSTPONE)
					|| StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)
					|| StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)
					|| StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_MORTEMIHOLIDAY)
					|| StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_MORTEMIHOLIDAY)
					|| StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLDEMI)
					|| StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_STRTPRDHLD)) {
				cntSchd = cntSchd + 1;
				continue;
			}

			if (fsd.getSchDate().compareTo(rid.getAppDate()) <= 0) {
				FinanceScheduleDetail ehFsd = new FinanceScheduleDetail();
				ehFsd.setSchDate(fsd.getSchDate());
				ehFsd.setProfitSchd(fsd.getProfitSchd());
				ehFsd.setPrincipalSchd(fsd.getPrincipalSchd());
				ehFsd.setCpzAmount(fsd.getCpzAmount());
				ehFsd.setRepayAmount(fsd.getRepayAmount());
				ehFsd.setSchdPftPaid(fsd.getSchdPftPaid());
				ehFsd.setSchdPriPaid(fsd.getSchdPriPaid());
				ehFsd.setPartialPaidAmt(fsd.getPartialPaidAmt());
				ehFsdList.add(ehFsd);
			}

			fsd.setBpiOrHoliday(FinanceConstants.FLAG_MORTEMIHOLIDAY);
			if (!isDailyFrqLoan) {
				fsd.setCpzOnSchDate(true);
			} else {
				Date monthEndDate = DateUtility.getMonthEnd(fsd.getSchDate());
				if (DateUtility.compare(fsd.getSchDate(), monthEndDate) == 0) {
					fsd.setCpzOnSchDate(true);
				}
			}

			// Set first EMI holiday index
			if (iUPEH < 0) {
				iUPEH = iFsd;
				oldEventFromdate=fsd.getSchDate();
				fm.setEventFromDate(fsd.getSchDate());
				fm.setRecalFromDate(fsd.getSchDate());
			}

			if(DateUtility.compare(fsd.getSchDate(), fm.getGrcPeriodEndDate()) <= 0 || startOnGrcPrd){
				startOnGrcPrd = true;
				mortOnGrcTermCount = mortOnGrcTermCount + 1;
				fsd.setSchdMethod(fm.getGrcSchdMthd());
			}
			newUPEH = newUPEH + 1;
			cntSchd = cntSchd + 1;
			fm.setEventToDate(fsd.getSchDate());
			oldEventtodate = fsd.getSchDate();

			// Reached requested holidays count
			if (newUPEH == drEH.getEHInst()) {
				break;
			}
		}

		if (newUPEH == 0) {
			drEH.setEHStatus("E");
			drEH.setEhStatusRemarks("No Schedules found for EMI Holiday");
			dMD = saveEHSchedule(dMD, rid, false);
			return dMD;
		}

		BigDecimal oldRpyInstAmt = BigDecimal.ZERO;
		if(mortOnGrcTermCount > 0){

			Date grcEndCalFrom = fm.getGrcPeriodEndDate();
			if(DateUtility.compare(fm.getEventToDate(), grcEndCalFrom) > 0){
				grcEndCalFrom = fm.getEventToDate();
			}
			FinanceMain main = financeMainDAO.getFinanceMainById(fm.getFinReference(), "", false);
			List<Calendar> scheduleDateList = FrequencyUtil
					.getNextDate(fm.getGrcPftFrq(), mortOnGrcTermCount,
							grcEndCalFrom, HolidayHandlerTypes.MOVE_NONE, false)
					.getScheduleList();

			final Date oldGrcEnd = fm.getGrcPeriodEndDate();
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				main.setGrcPeriodEndDate(DateUtility.getDate(DateUtility.format(calendar.getTime(), PennantConstants.dateFormat)));
				main.setGraceTerms(fm.getGraceTerms() + mortOnGrcTermCount);
			}

			boolean emiFetched = false;
			for (int iFsd = 1; iFsd < fsdList.size(); iFsd++) {
				FinanceScheduleDetail fsd = fsdList.get(iFsd);

				if (!emiFetched && StringUtils.equals(drEH.getEHMethod(), "CT")) {
					if (DateUtility.compare(fsd.getSchDate(), oldGrcEnd) > 0 && fsd.isRepayOnSchDate()) {

						if(StringUtils.equals(fsd.getSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)){
							oldRpyInstAmt = fsd.getRepayAmount();
							emiFetched = true;
						}else if(StringUtils.equals(fsd.getSchdMethod(), CalculationConstants.SCHMTHD_PRI_PFT) ||
								StringUtils.equals(fsd.getSchdMethod(), CalculationConstants.SCHMTHD_PRI)){
							oldRpyInstAmt = fsd.getPrincipalSchd();
							emiFetched = true;
						}
					}
				}

				if (DateUtility.compare(fsd.getSchDate(), main.getGrcPeriodEndDate()) <= 0) {
					if(DateUtility.compare(fsd.getSchDate(), oldEventFromdate) > 0){
						fsd.setSchdMethod(fm.getGrcSchdMthd());
						fsd.setPrincipalSchd(fsd.getSchdPriPaid());
					}
					continue;
				}

				if(!fsd.isRepayOnSchDate()){
					continue;
				}

				main.setNextRepayDate(fsd.getSchDate());
				main.setNextRepayPftDate(fsd.getSchDate());
				main.setNextRepayRvwDate(fsd.getSchDate());
				main.setNextRepayCpzDate(fsd.getSchDate());
				break;
			}

			main.setEventFromDate(oldEventFromdate);
			main.setEventToDate(fm.getEventToDate());
			main.setRecalFromDate(fm.getRecalFromDate());

			// Maturity date
			scheduleDateList = FrequencyUtil
					.getNextDate(fm.getRepayFrq(), mortOnGrcTermCount,
							main.getMaturityDate(), HolidayHandlerTypes.MOVE_NONE, false)
					.getScheduleList();

			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				main.setMaturityDate(DateUtility.getDate(DateUtility.format(calendar.getTime(), PennantConstants.dateFormat)));
			}

			fm = main;
		}
		if (isIntServiceLoan) {
			for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
				FinanceScheduleDetail fsd = fsdList.get(iFsd);
				actPriSchd = fsd.getPrincipalSchd().subtract(fsd.getPartialPaidAmt());

				if (actPriSchd.compareTo(BigDecimal.ZERO) > 0
						&& fsd.getClosingBalance().compareTo(BigDecimal.ZERO) != 0) {
					isIntServiceLoan = false;
					break;
				}
			}
		}

		// Available Unplanned EMI holidays
		int availableEH = fm.getMaxUnplannedEmi() - fm.getAvailedUnPlanEmi();

		// If available less than requested then add shortfall to max. unplanned
		// EMIs
		if (availableEH < newUPEH) {
			int additionalRequired = newUPEH - availableEH;
			fm.setMaxUnplannedEmi(fm.getMaxUnplannedEmi() + additionalRequired);
		}

		FinScheduleData fsData = new FinScheduleData();
		fsData.setFinanceMain(fm);
		fsData.setFinanceScheduleDetails(dMD.getFinScheduleDetails());
		fsData.setDisbursementDetails(dMD.getFinDisbursements());
		fsData.setRepayInstructions(dMD.getRepayInstructions());
		fsData.setFinanceType(dMD.getFinType());
		fsData.setFinPftDeatil(dMD.getFinProfitDetails());

		if(mortOnGrcTermCount == 0){
			fm.setAdjTerms(newUPEH);
			if (StringUtils.equals(drEH.getEHMethod(), "CT")) {
				if (isIntServiceLoan) {
					fm.setRecalType(CalculationConstants.RPYCHG_ADDRECAL);
					drEH.setEhStatusRemarks("ISL. Recal Method forced to ADDRECAL");//Interest Servicing Loan
				} else {
					fm.setRecalType(CalculationConstants.RPYCHG_ADJTERMS);
				}

			} else if (StringUtils.equals(drEH.getEHMethod(), "CE")) {
				fm.setRecalType(CalculationConstants.RPYCHG_ADDRECAL);
			}

			fsData = ScheduleCalculator.calDREMIHolidays(fsData);
		}else{

			if (StringUtils.equals(drEH.getEHMethod(), "CT")) {
				fm.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
			} else if (StringUtils.equals(drEH.getEHMethod(), "CE")) {
				fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			}
			fsData = ScheduleCalculator.changeGraceEnd(fsData);

			if (StringUtils.equals(drEH.getEHMethod(), "CT") && 
					oldRpyInstAmt.compareTo(BigDecimal.ZERO) > 0) {

				fm.setAdjTerms(newUPEH);
				List<RepayInstruction> rpyInstList = fsData.getRepayInstructions();
				for (RepayInstruction instr : rpyInstList) {
					if(DateUtility.compare(instr.getRepayDate(), fm.getGrcPeriodEndDate()) > 0){
						instr.setRepayAmount(oldRpyInstAmt);
						break;
					}
				}

				// Recalculate schedule
				if (isIntServiceLoan) {
					fm.setRecalType(CalculationConstants.RPYCHG_ADDRECAL);
					drEH.setEhStatusRemarks("ISL. Recal Method forced to ADDRECAL");//Interest Servicing Loan
				} else {
					fm.setRecalType(CalculationConstants.RPYCHG_ADJTERMS);
				}

				fsData = ScheduleCalculator.calDREMIHolidays(fsData);
			}
		}

		if (fsData.getErrorDetails() != null && !fsData.getErrorDetails().isEmpty()) {
			drEH.setEHStatus("E");
			drEH.setEhStatusRemarks(fsData.getErrorDetails().get(0).getMessage().substring(0, 50));
			dMD = saveEHSchedule(dMD, rid, false);
			return dMD;
		}

		dMD.setFinanceMain(fsData.getFinanceMain());
		dMD.setFinScheduleDetails(fsData.getFinanceScheduleDetails());
		dMD.setRepayInstructions(fsData.getRepayInstructions());
		dMD.setFinProfitDetails(fsData.getFinPftDeatil());

		fm = fsData.getFinanceMain();
		fpd = fsData.getFinPftDeatil();
		fsdList = fsData.getFinanceScheduleDetails();

		setFMDates(fm, fsdList, rid.getAppDate());
		for (int iFsd = iUPEH; iFsd < fsdList.size() - fm.getAdvTerms(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);

			if (fsd.getSchDate().compareTo(oldEventtodate) > 0) {
				break;
			}

			if (fsd.getSchDate().compareTo(oldEventFromdate) >= 0
					&& fsd.getSchDate().compareTo(rid.getAppDate()) <= 0
					&& StringUtils.equals(fsd.getBpiOrHoliday(), "U")) {

				for (int iEH = 0; iEH < ehFsdList.size(); iEH++) {
					FinanceScheduleDetail ehFsd = ehFsdList.get(iEH);

					if (ehFsd.getSchDate().compareTo(rid.getAppDate()) > 0) {
						break;
					}

					if (fsd.getSchDate().compareTo(ehFsd.getSchDate()) == 0) {
						drEH.setPastCpzChg(drEH.getPastCpzChg().add(fsd.getCpzAmount()).subtract(ehFsd.getCpzAmount()));
					}
				}
			}

			drEH.setCpzInterest(drEH.getCpzInterest().add(fsd.getCpzAmount()));
		}

		/*
		 * BigDecimal netExcessAvailable = BigDecimal.ZERO; List<FinExcessAmount> feaList = dMD.getFinExcessAmounts();
		 * for (int iFea = 0; iFea < feaList.size(); iFea++) { FinExcessAmount fea = feaList.get(iFea);
		 *
		 * netExcessAvailable = fea.getAmount().subtract(fea.getUtilisedAmt()).subtract(fea.getReservedAmt()); }
		 */

		Date odValueDate = DateUtility.addDays(rid.getAppDate(), -1);
		List<FinODDetails> fodList = dMD.getFinODDetails();

		for (int iFod = 0; iFod < fodList.size(); iFod++) {
			FinODDetails fod = fodList.get(iFod);

			if (fod.getFinODSchdDate().compareTo(drEH.getEHStartDate()) < 0) {
				fodList.remove(iFod);
				iFod = iFod - 1;
				continue;

			}

			for (int iFsd = iUPEH; iFsd < fsdList.size() - fm.getAdvTerms(); iFsd++) {
				FinanceScheduleDetail fsd = fsdList.get(iFsd);

				if (fsd.getSchDate().compareTo(rid.getAppDate()) >= 0) {
					break;
				}

				if (fsd.getSchDate().compareTo(fod.getFinODSchdDate()) == 0) {
					if (fsd.getProfitSchd().compareTo(BigDecimal.ZERO) == 0
							&& fsd.getPrincipalSchd().compareTo(BigDecimal.ZERO) == 0) {
						fod.setFinODFor("D");
						continue;
					}

					dMD.setRepayDetails(this.getFinanceRepaymentsDAO().getByFinRefAndSchdDate(fod.getFinReference(),
							fod.getFinODSchdDate()));
					latePayMarkingService.latePayMarking(fm, fod, dMD.getPenaltyrate(), fsdList, dMD.getRepayDetails(),
							fsd, odValueDate, odValueDate, false);
				}

			}
		}

		latePayBucketService.updateDPDBuketing(fsdList, fm, fpd, rid.getAppDate(), false);

		fsData.setFinanceMain(fm);
		fsData.setFinPftDeatil(fpd);

		fsData.setFinPftDeatil(accrualService.calProfitDetails(fsData.getFinanceMain(),
				fsData.getFinanceScheduleDetails(), fsData.getFinPftDeatil(), rid.getAppDate()));

		drEH.setNewBucket(fm.getDueBucket());
		drEH.setDpd(fpd.getCurODDays());
		drEH.setNewEMIOS(fpd.getTdSchdPftBal().add(fpd.getTdSchdPriBal()));
		drEH.setNewBalTenure(fpd.getRemainingTenor());
		drEH.setNewMaturity(fm.getMaturityDate());
		drEH.setNewTenure(fpd.getTotalTenor());
		drEH.setNewInterest(fpd.getTotalPftSchd());
		drEH.setNewMaxUnPlannedEMI(fm.getMaxUnplannedEmi());
		drEH.setNewAvailedUnPlanEMI(fm.getAvailedUnPlanEmi());
		drEH.setNewFinalEMI(fsdList.get(fsdList.size() - 1).getRepayAmount());

		drEH.setEHStatus("S");

		dMD.setDrEH(drEH);
		dMD = saveEHSchedule(dMD, rid, true);

		// adjustUMFC(fsData, difInterest, "SCDCHG");

		for (int iEH = 0; iEH < ehFsdList.size(); iEH++) {
			FinanceScheduleDetail ehFsd = ehFsdList.get(iEH);

			if (ehFsd.getSchDate().compareTo(rid.getAppDate()) > 0) {
				break;
			}

			for (int iFsd = iUPEH; iFsd < fsdList.size() - fm.getAdvTerms(); iFsd++) {
				FinanceScheduleDetail fsd = fsdList.get(iFsd);

				if (fsd.getSchDate().compareTo(ehFsd.getSchDate()) == 0) {
					drEH.setInstIntChg(drEH.getInstIntChg().subtract(fsd.getProfitSchd()).add(ehFsd.getProfitSchd()));
					drEH.setInstPriChg(
							drEH.getInstPriChg().subtract(fsd.getPrincipalSchd()).add(ehFsd.getPrincipalSchd()));
					break;
				}
			}
		}

		drEH.setInstChg(drEH.getInstIntChg().add(drEH.getInstPriChg()));
		BigDecimal newTotalIntToAccrue = fpd.getTotalPftSchd().add(fpd.getTotalPftCpz());
		drEH.setPftChg(newTotalIntToAccrue.subtract(oldTotalIntToAccrue));

		adjustReage(fsData, drEH, "REAGING");

		return dMD;
	}

	public void setFMDates(FinanceMain fm, List<FinanceScheduleDetail> finSchdDetails, Date appDate) throws Exception {
		// GrcPeriodEndDate
		fm.setNextGrcPftDate(fm.getFinStartDate());
		fm.setNextGrcPftRvwDate(fm.getFinStartDate());
		fm.setNextGrcCpzDate(fm.getFinStartDate());

		fm.setNextRepayDate(fm.getFinStartDate());
		fm.setNextRepayPftDate(fm.getFinStartDate());
		fm.setNextRepayRvwDate(fm.getFinStartDate());
		fm.setNextRepayCpzDate(fm.getFinStartDate());

		fm.setLastRepayDate(fm.getGrcPeriodEndDate());
		fm.setLastRepayPftDate(fm.getGrcPeriodEndDate());
		fm.setLastRepayRvwDate(fm.getGrcPeriodEndDate());
		fm.setLastRepayCpzDate(fm.getGrcPeriodEndDate());

		// Place schedule dates to Map
		for (int i = 0; i < finSchdDetails.size(); i++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);

			curSchd.setFinReference(fm.getFinReference());
			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) <= 0) {

				if (fm.getNextGrcPftDate().compareTo(appDate) <= 0) {
					if (curSchd.isPftOnSchDate() || curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) > 0) {
						fm.setNextGrcPftDate(curSchd.getSchDate());
					}
				}

				if (fm.getNextGrcPftRvwDate().compareTo(appDate) <= 0) {
					if (curSchd.isRvwOnSchDate()) {
						fm.setNextGrcPftRvwDate(curSchd.getSchDate());
					}
				}

				if (fm.getNextGrcCpzDate().compareTo(appDate) <= 0) {
					if (curSchd.isCpzOnSchDate()) {
						fm.setNextGrcCpzDate(curSchd.getSchDate());
					}
				}

			} else {
				if (curSchd.getSchDate().compareTo(appDate) <= 0) {

					if (curSchd.isPftOnSchDate() || curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) > 0) {
						fm.setLastRepayPftDate(fm.getFinStartDate());
					}

					if (curSchd.isRepayOnSchDate() || curSchd.getPrincipalSchd().compareTo(BigDecimal.ZERO) > 0) {
						fm.setLastRepayDate(curSchd.getSchDate());
					}

					if (curSchd.isRvwOnSchDate()) {
						fm.setLastRepayRvwDate(curSchd.getSchDate());
					}

					if (curSchd.isCpzOnSchDate()) {
						fm.setLastRepayCpzDate(fm.getFinStartDate());
					}

				} else {

					if (fm.getNextRepayPftDate().compareTo(appDate) <= 0) {
						if (curSchd.isPftOnSchDate() || curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) > 0) {
							fm.setNextRepayPftDate(curSchd.getSchDate());
						}
					}

					if (fm.getNextRepayDate().compareTo(appDate) <= 0) {
						if (curSchd.isRepayOnSchDate() || curSchd.getPrincipalSchd().compareTo(BigDecimal.ZERO) > 0) {
							fm.setNextRepayDate(curSchd.getSchDate());
						}
					}

					if (fm.getNextRepayRvwDate().compareTo(appDate) <= 0) {
						if (curSchd.isRvwOnSchDate()) {
							fm.setNextRepayRvwDate(curSchd.getSchDate());
						}
					}

					if (fm.getNextRepayCpzDate().compareTo(appDate) <= 0) {
						if (curSchd.isCpzOnSchDate()) {
							fm.setNextRepayCpzDate(curSchd.getSchDate());
						}
					}
				}
			}

		}

	}

	public MigrationData saveEHSchedule(MigrationData dMD, ReferenceID rid, boolean isSucess) throws Exception {

		FinanceMain fm = dMD.getFinanceMain();
		String finReference = fm.getFinReference();
		Date evtFromDate = fm.getEventFromDate();

		updateDREMIHoliday(dMD.getDrEH());

		if (!isSucess) {
			return dMD;
		}

		String type = "";
		fm.setVersion(fm.getVersion() + 1);
		this.financeMainDAO.updateEHFinanceMain(fm);
		setLogKey(fm, dMD.getOldFinScheduleDetails());

		this.financeScheduleDetailDAO.saveList(dMD.getOldFinScheduleDetails(), "_Log", false);
		this.financeScheduleDetailDAO.deleteByFinReference(finReference, "", false, 0L);
		this.financeScheduleDetailDAO.saveList(dMD.getFinScheduleDetails(), "", false);

		this.repayInstructionDAO.deleteInEOD(finReference);
		for (RepayInstruction instruction : dMD.getRepayInstructions()) {
			instruction.setFinReference(finReference);
		}
		this.repayInstructionDAO.saveList(dMD.getRepayInstructions(), "", false);

		profitDetailsDAO.updateEOD(dMD.getFinProfitDetails(), false, false);

		List<FinODDetails> fodList = dMD.getFinODDetails();
		for (int iFod = 0; iFod < fodList.size(); iFod++) {
			FinODDetails fod = fodList.get(iFod);
			finODDetailsDAO.deleteAfterODDate(finReference, fod.getFinODSchdDate());
			if (StringUtils.equals(fod.getFinODFor(), "D")) {
				continue;
			}

			finODDetailsDAO.save(fod);
		}

		return dMD;
	}

	private void setLogKey(FinanceMain fm, List<FinanceScheduleDetail> previosSchedules) {
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();
		entryDetail.setReversalCompleted(false);
		entryDetail.setPostDate(DateUtility.getAppDate());
		entryDetail.setFinReference(fm.getFinReference());
		entryDetail.setSchdlRecal(fm.isScheduleChange());
		entryDetail.setEventAction(AccountEventConstants.ACCEVENT_GRACEEND);
		long logKey = finLogEntryDetailDAO.save(entryDetail);

		for (FinanceScheduleDetail schedule : previosSchedules) {
			schedule.setLogKey(logKey);
		}

	}

	private MigrationData reCalCchd(final MigrationData dMD, final ReferenceID rid) {
		FinScheduleData fsd = new FinScheduleData();
		FinanceMain dFM = dMD.getFinanceMain();
		dFM.setRecalIdx(rid.getiDRFrom());
		fsd.setFinanceType(dMD.getFinType());
		fsd.setFinanceMain(dFM);
		fsd.setFinanceScheduleDetails(dMD.getFinScheduleDetails());
		fsd.setRepayInstructions(dMD.getRepayInstructions());
		fsd.setDisbursementDetails(dMD.getFinDisbursements());
		final FinanceScheduleDetail shcd = dMD.getFinScheduleDetails().get(rid.getiDRFrom());
		final long presentmentID = shcd.getPresentmentId();
		shcd.setPresentmentId(0L);
		shcd.setSchPftPaid(false);
		shcd.setSchPriPaid(false);
		dFM.setRecalFromDate(shcd.getSchDate());
		// fsd = ScheduleCalculator.reCalSchdDM(fsd);
		shcd.setPresentmentId(presentmentID);
		dFM = fsd.getFinanceMain();
		dMD.setFinScheduleDetails(fsd.getFinanceScheduleDetails());
		dMD.setRepayInstructions(fsd.getRepayInstructions());
		return dMD;
	}

	public List<ReceiptAllocationDetail> addDRAllocations(final MigrationData dMD, final ReferenceID rid,
			final List<ReceiptAllocationDetail> radList) {
		final FinReceiptHeader rch = dMD.getFinReceiptHeaders().get(0);
		final FinanceScheduleDetail fsd = dMD.getFinScheduleDetails().get(rid.getiDRFrom());
		int allocationID = 0;
		if (fsd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
			allocationID = this.setPftAllocation(radList, rch, fsd, allocationID);
		}
		if (fsd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
			final ReceiptAllocationDetail rad = new ReceiptAllocationDetail();
			++allocationID;
			rad.setReceiptID(rch.getReceiptID());
			rad.setAllocationID(allocationID);
			rad.setAllocationType("PRI");
			rad.setPaidAmount(fsd.getSchdPriPaid());
			rad.setTotalDue(fsd.getSchdPriPaid());
			radList.add(rad);
		}
		return radList;
	}

	private int setPftAllocation(final List<ReceiptAllocationDetail> radList, final FinReceiptHeader rch,
			final FinanceScheduleDetail fsd, int allocationID) {
		ReceiptAllocationDetail rad = new ReceiptAllocationDetail();
		++allocationID;
		rad.setReceiptID(rch.getReceiptID());
		rad.setAllocationID(allocationID);
		rad.setAllocationType("PFT");
		rad.setPaidAmount(fsd.getSchdPftPaid());
		rad.setTotalDue(fsd.getSchdPftPaid());
		radList.add(rad);
		rad = new ReceiptAllocationDetail();
		++allocationID;
		rad.setReceiptID(rch.getReceiptID());
		rad.setAllocationID(allocationID);
		rad.setAllocationType("NPFT");
		rad.setPaidAmount(fsd.getSchdPftPaid());
		rad.setTotalDue(fsd.getSchdPftPaid());
		radList.add(rad);
		return allocationID;
	}

	public void setPresentmentDetailDAO(final PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public PresentmentDetailDAO getPresentmentDetailDAO() {
		return this.presentmentDetailDAO;
	}

	public FinFeeReceiptDAO getFinFeeReceiptDAO() {
		return this.finFeeReceiptDAO;
	}

	public void setFinFeeReceiptDAO(final FinFeeReceiptDAO finFeeReceiptDAO) {
		this.finFeeReceiptDAO = finFeeReceiptDAO;
	}

	public void prepareHC41() {
		this.drFinanceDetailsDAO.prepareHC41();
	}

	public PaymentDetailDAO getPaymentDetailDAO() {
		return this.paymentDetailDAO;
	}

	public void setPaymentDetailDAO(final PaymentDetailDAO paymentDetailDAO) {
		this.paymentDetailDAO = paymentDetailDAO;
	}

	public void updateDMCorrectionSts(final DRFinanceDetails details) {
		this.drFinanceDetailsDAO.updateDMCorrectionSts(details);
	}

	public PostingsDAO getPostingsDAO() {
		return this.postingsDAO;
	}

	public void setPostingsDAO(final PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public FinScheduleData getFinanceDetails(final String finReference) {
		final FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinReference(finReference);
		finSchData.setFinanceMain(this.financeMainDAO.getFinanceMainById(finReference, "_View", false));
		finSchData.setFinanceType(this.financeTypeDAO.getFinanceTypeByID(finSchData.getFinanceMain().getFinType(), ""));
		finSchData.setFinanceScheduleDetails(
				this.financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", false));
		finSchData
		.setRepayInstructions(this.repayInstructionDAO.getRepayInstructions(finReference, "_20200313", false));
		finSchData.setFinPftDeatil(this.getProfitDetailsDAO().getFinProfitDetailsById(finReference));
		finSchData.setDisbursementDetails(
				this.getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, "", false));
		return finSchData;
	}

	public FinScheduleData processDR(FinScheduleData finSchData) {
		//finSchData = ScheduleCalculator.resetRpyInst(finSchData);
		this.repayInstructionDAO.deleteByFinReference(finSchData.getFinanceMain().getFinReference(), "", false, 0L);
		this.repayInstructionDAO.saveList(finSchData.getRepayInstructions(), "", false);
		return finSchData;
	}

	public DRCorrections getFullFinanceDetails(DRCorrections p0, boolean p1, boolean p2) {
		// TODO Auto-generated method stub
		return null;
	}

	public int updateHCFinanceDetails(DRCorrections p0, TableType p1) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void sortRepaySchdDetails(List<RepayScheduleDetail> rsdList) {

		if (CollectionUtils.isNotEmpty(rsdList)) {
			Collections.sort(rsdList, new Comparator<RepayScheduleDetail>() {
				public int compare(RepayScheduleDetail rsd1, RepayScheduleDetail rsd2) {
					Long linkTrn1 = rsd1.getLinkedTranId();
					Long linkTrn2 = rsd2.getLinkedTranId();
					return linkTrn1.compareTo(linkTrn2);
				}
			});
		}
	}

	public List<DREMIHoliday> getDREHListList() {
		return this.drFinanceDetailsDAO.getDREMIHoliday();
	}

	public void setLatePayBucketService(LatePayBucketService latePayBucketService) {
		this.latePayBucketService = latePayBucketService;
	}

	public ScheduleCalculator getScheduleCalculator() {
		return scheduleCalculator;
	}

	public void setScheduleCalculator(ScheduleCalculator scheduleCalculator) {
		this.scheduleCalculator = scheduleCalculator;
	}

	public void updateDREMIHoliday(DREMIHoliday erEH) {
		this.drFinanceDetailsDAO.updateDREMIHoliday(erEH);
	}

	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

}
