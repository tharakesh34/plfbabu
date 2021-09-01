package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.CashBackDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.dao.rmtmasters.GSTRateDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.finance.CashBackDetail;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.GSTRate;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.finance.CashBackProcessService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;

public class CashBackProcessServiceImpl implements CashBackProcessService {
	private static final Logger logger = LogManager.getLogger(CashBackProcessServiceImpl.class);

	private FeeTypeDAO feeTypeDAO;
	private ManualAdviseService manualAdviseService;
	private CashBackDetailDAO cashBackDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinTypePartnerBankDAO finTypePartnerBankDAO;
	private BankBranchDAO bankBranchDAO;
	private MandateDAO mandateDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private PaymentHeaderService paymentHeaderService;
	private PartnerBankDAO partnerBankDAO;
	private PaymentHeaderDAO paymentHeaderDAO;
	private ReceiptService receiptService;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RuleDAO ruleDAO;
	private GSTRateDAO gstRateDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private LatePayMarkingService latePayMarkingService;

	private String tdsRoundMode = null;
	private int tdsRoundingTarget = 0;
	private BigDecimal tdsPerc = BigDecimal.ZERO;

	private BigDecimal cgstPerc = BigDecimal.ZERO;
	private BigDecimal sgstPerc = BigDecimal.ZERO;
	private BigDecimal ugstPerc = BigDecimal.ZERO;
	private BigDecimal igstPerc = BigDecimal.ZERO;
	private BigDecimal cessPerc = BigDecimal.ZERO;
	private BigDecimal tgstPerc = BigDecimal.ZERO;

	@Override
	public void createCashBackAdvice(FinanceMain fm, Promotion promotion, Date appDate) {
		logger.debug(Literal.ENTERING);

		if (!StringUtils.equals(FinanceConstants.PRODUCT_CD, fm.getProductCategory())) {
			logger.debug(Literal.LEAVING);
			return;
		}

		List<CashBackDetail> cashBackDetailList = new ArrayList<>();
		ManualAdvise manualAdvise = null;

		switch (promotion.getCbPyt()) {

		case PennantConstants.DBD_AND_MBD_SEPARATELY:

			boolean dbdProcAvail = false;
			BigDecimal dbdAmount = null;

			if (promotion.isDbd() && !promotion.isDbdRtnd()) {

				dbdAmount = fm.getFinAmount().multiply(promotion.getDbdPerc()).divide(new BigDecimal(100), 0,
						RoundingMode.HALF_DOWN);

				// Payable Advise creation against cash back amount in Hold Status
				// Hold is for to avoid usage in screens till Settlement process completed
				manualAdvise = cashBackDueCreation(fm, promotion.getDbdFeeTypId(), dbdAmount, appDate);

				// Logging Cash back process details for future usage
				cashBackDetailList.add(prepareCashbackLog(fm, manualAdvise, "DBD"));

				dbdProcAvail = true;
			}

			if (promotion.isMbd() && !promotion.isMbdRtnd()) {
				BigDecimal mbdAmount = BigDecimal.ZERO;

				// If DBD Available balance amount from Subvention will be MDB CashBack
				// Total CashBack should not cross Subvention amount always
				if (dbdProcAvail) {
					mbdAmount = fm.getSvAmount().subtract(dbdAmount);
				} else {
					mbdAmount = fm.getSvAmount();
				}

				// Payable Advise creation against cash back amount in Hold Status
				// Hold is for to avoid usage in screens till Settlement process completed
				manualAdvise = cashBackDueCreation(fm, promotion.getMbdFeeTypId(), mbdAmount, appDate);

				// Logging Cash back process details for future usage
				cashBackDetailList.add(prepareCashbackLog(fm, manualAdvise, "MBD"));
			}

			break;
		case PennantConstants.DBD_AND_MBD_TOGETHER:

			// Payable Advise creation against cash back amount in Hold Status
			// Hold is for to avoid usage in screens till Settlement process completed
			manualAdvise = cashBackDueCreation(fm, promotion.getDbdAndMbdFeeTypId(), fm.getSvAmount(), appDate);

			// Logging Cash back process details for future usage
			cashBackDetailList.add(prepareCashbackLog(fm, manualAdvise, "DBMBD"));
			break;

		default:
			break;
		}

		// Saving the all cashBack details
		if (CollectionUtils.isNotEmpty(cashBackDetailList)) {
			cashBackDetailDAO.save(cashBackDetailList);
		}

		logger.debug(Literal.LEAVING);
	}

	private CashBackDetail prepareCashbackLog(FinanceMain finMain, ManualAdvise manualAdvise, String type) {
		logger.debug(Literal.ENTERING);

		CashBackDetail cbDetail = new CashBackDetail();
		cbDetail.setFinReference(finMain.getFinReference());
		cbDetail.setType(type);
		cbDetail.setAdviseId(manualAdvise.getAdviseID());
		cbDetail.setAmount(manualAdvise.getAdviseAmount());
		cbDetail.setRefunded(false);

		logger.debug(Literal.LEAVING);
		return cbDetail;
	}

	private ManualAdvise cashBackDueCreation(FinanceMain finMain, long feeId, BigDecimal cashbackAmount, Date appDate) {
		logger.debug(Literal.ENTERING);

		ManualAdvise ma = new ManualAdvise();
		ma.setAdviseID(manualAdviseService.getNewAdviseID());
		ma.setFinReference(finMain.getFinReference());
		ma.setAdviseType(FinanceConstants.MANUAL_ADVISE_PAYABLE);
		ma.setAdviseAmount(cashbackAmount);
		ma.setBalanceAmt(ma.getAdviseAmount());
		ma.setHoldDue(true);
		ma.setFinSource(UploadConstants.FINSOURCE_ID_CD_UPLOAD);

		FeeType cbFeeType = feeTypeDAO.getFeeTypeById(feeId, "_AView");
		com.pennant.backend.model.finance.FeeType modelFeeType = new com.pennant.backend.model.finance.FeeType();
		BeanUtils.copyProperties(cbFeeType, modelFeeType);
		ma.setFeeType(modelFeeType);
		ma.setFeeTypeCode(cbFeeType.getFeeTypeCode());
		ma.setFeeTypeID(cbFeeType.getFeeTypeID());

		ma.setValueDate(appDate);
		ma.setPostDate(appDate);
		ma.setVersion(1);
		ma.setLastMntBy(finMain.getLastMntBy());
		ma.setLastMntOn(finMain.getLastMntOn());
		ma.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		ma.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		ma.setNewRecord(true);
		ma.setUserDetails(finMain.getUserDetails());

		// Save Cashback Advice
		manualAdviseService.doApprove(getAuditHeader(ma, PennantConstants.TRAN_WF));

		logger.debug(Literal.LEAVING);
		return ma;
	}

	@Override
	public void createPaymentInstruction(FinanceMain finMain, String feeypeCode, long adviseId, BigDecimal instAmount) {
		logger.debug(Literal.ENTERING);

		PaymentHeader paymentHeader = preparePaymentInst(finMain, feeypeCode, adviseId, instAmount);
		AuditHeader paymentsAuditHeader = getAuditHeader(paymentHeader, PennantConstants.TRAN_WF);

		paymentHeaderService.doApprove(paymentsAuditHeader);
		cashBackDetailDAO.updateCashBackDetail(adviseId);

		logger.debug(Literal.LEAVING);
	}

	private PaymentHeader preparePaymentInst(FinanceMain finMain, String feeTypeCode, long adviseId,
			BigDecimal instAmount) {
		logger.debug("Entering");

		long bankBranchId = 0;
		Date appDate = SysParamUtil.getAppDate();

		// Payment Header
		PaymentHeader ph = new PaymentHeader();
		ph.setFinReference(finMain.getFinReference());
		ph.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		ph.setCreatedOn(appDate);
		ph.setApprovedOn(appDate);
		ph.setStatus(RepayConstants.PAYMENT_APPROVE);
		ph.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		ph.setNewRecord(true);
		ph.setVersion(1);
		ph.setUserDetails(finMain.getUserDetails());
		ph.setLastMntBy(finMain.getLastMntBy());
		ph.setLastMntOn(finMain.getLastMntOn());
		ph.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		ph.setPaymentId(paymentHeaderDAO.getNewPaymentHeaderId());
		ph.setFinSource(UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD);

		FinanceMain fm = financeMainDAO.getFinanceForIncomeAMZ(finMain.getFinID());

		if (fm == null) {
			throw new InterfaceException("9999", "Loan Reference should not exist.");
		}
		long partnerBankId = Long.valueOf(SysParamUtil.getValueAsInt("DISB_PARTNERBANK"));
		String partnerBankcode = partnerBankDAO.getPartnerBankCodeById(partnerBankId);

		FinTypePartnerBank finTypePartnerBank = finTypePartnerBankDAO.getFinTypePartnerBankByPartnerBankCode(
				partnerBankcode, fm.getFinType(), DisbursementConstants.PAYMENT_TYPE_NEFT);

		if (finTypePartnerBank == null) {
			throw new InterfaceException("9999", "Partner banks should not linked to Loan Type.");
		}

		Mandate mandate = mandateDAO.getMandateById(finMain.getMandateID(), "_AView");
		if (mandate == null) {
			throw new InterfaceException("9999", "Please link Mandate.");
		}
		BankBranch bankBranch = bankBranchDAO.getBankBrachByIFSCandMICR(mandate.getIFSC(), mandate.getMICR(), "");

		if (bankBranch == null) {
			throw new InterfaceException("9999", "Invalid IFSC/MICR.");
		} else {
			bankBranchId = bankBranch.getBankBranchID();
		}

		// Payment Details
		List<PaymentDetail> paymentDetailList = new ArrayList<PaymentDetail>();

		ManualAdvise advise = manualAdviseDAO.getManualAdviseById(adviseId, "_AView");

		if (advise == null) {
			return null;
		}

		// Payment Instruction Details preparation
		PaymentDetail pd = new PaymentDetail();
		pd.setAmount(instAmount);
		ph.setPaymentAmount(instAmount);
		pd.setReferenceId(advise.getAdviseID());
		pd.setAvailableAmount(advise.getBalanceAmt());
		pd.setAmountType(String.valueOf(advise.getAdviseType()));
		pd.setFeeTypeCode(advise.getFeeTypeCode());
		pd.setFeeTypeDesc(advise.getFeeTypeDesc());
		pd.setRecordType(PennantConstants.RCD_ADD);
		pd.setNewRecord(true);
		pd.setVersion(1);
		pd.setUserDetails(finMain.getUserDetails());
		pd.setLastMntBy(finMain.getLastMntBy());
		pd.setLastMntOn(finMain.getLastMntOn());
		pd.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		pd.setApiRequest(true);
		pd.setFinSource(UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD);
		paymentDetailList.add(pd);

		// Payment Instructions
		PaymentInstruction pi = new PaymentInstruction();
		pi.setPostDate(appDate);
		pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_NEFT);
		pi.setPaymentAmount(instAmount);
		pi.setBankBranchCode(mandate.getBranchCode());
		pi.setBankBranchId(bankBranchId);
		pi.setAcctHolderName(mandate.getAccHolderName());
		pi.setAccountNo(mandate.getAccNumber());
		pi.setPhoneNumber(mandate.getPhoneNumber());
		pi.setValueDate(appDate);
		pi.setPaymentCCy(finMain.getFinCcy());
		pi.setPartnerBankCode(partnerBankcode);
		pi.setPartnerBankId(partnerBankId);
		pi.setStatus(DisbursementConstants.STATUS_NEW);
		pi.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		pi.setRecordType(PennantConstants.RCD_ADD);
		pi.setNewRecord(true);
		pi.setVersion(1);
		pi.setUserDetails(finMain.getUserDetails());
		pi.setLastMntBy(finMain.getLastMntBy());
		pi.setLastMntOn(finMain.getLastMntOn());

		// Extra validation fields
		pi.setPartnerBankAcType(finTypePartnerBank.getAccountType());
		pi.setApiRequest(true);

		ph.setPaymentDetailList(paymentDetailList);
		ph.setPaymentInstruction(pi);

		logger.debug("Leaving");
		return ph;
	}

	@Override
	public BigDecimal createReceiptOnCashBack(CashBackDetail cb) throws AppException {

		// Fetch Loan Level Receipt Adjustment Summary Details to create Receipts
		long finID = cb.getFinID();
		String finReference = cb.getFinReference();
		Date valueDate = SysParamUtil.getAppDate();

		// Actual Cash back Amount
		BigDecimal actCashBack = cb.getAmount();

		// Fetch TDS Parameter Details
		tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
		tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());

		// Prepare Due AMount against Each Loan Reference & Value Date
		BigDecimal cashBackBal = prepareLoanAlocList(finID, valueDate, cb.getAmount());
		if (cashBackBal.compareTo(BigDecimal.ZERO) <= 0) {
			cashBackBal = BigDecimal.ZERO;
		}

		if (actCashBack.compareTo(cashBackBal) == 0) {
			return actCashBack;
		}

		// Create Finance Service Instruction
		BigDecimal rcptAmt = actCashBack.subtract(cashBackBal);
		FinServiceInstruction serviceInstr = createFinServInstr(finReference, rcptAmt, valueDate);
		serviceInstr.setAdviseId(cb.getAdviseId());
		serviceInstr.setAdviseAmount(rcptAmt);

		// Create Receipt Against Loan
		FinanceDetail detail = receiptService.receiptTransaction(serviceInstr, FinServiceEvent.SCHDRPY);
		if (detail.getReturnStatus() != null) {
			throw new AppException("AppException", detail.getReturnStatus().getReturnText());
		}

		return actCashBack.subtract(rcptAmt);
	}

	private FinServiceInstruction createFinServInstr(String finReference, BigDecimal rcptAmount, Date valueDate) {

		FinServiceInstruction fsi = new FinServiceInstruction();
		fsi.setFinReference(finReference);
		fsi.setModule("Receipts");
		fsi.setValueDate(valueDate);
		fsi.setAmount(rcptAmount);
		fsi.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		long partnerBankId = Long.valueOf(SysParamUtil.getValueAsInt("DISB_PARTNERBANK"));
		PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(partnerBankId, "_AView");

		if (partnerBankId > 0) {
			fsi.setFundingAc(partnerBankId);
		}
		LoggedInUser loggedInUser = new LoggedInUser();
		loggedInUser.setLoginUsrID(1000);
		fsi.setLoggedInUser(loggedInUser);
		fsi.setBankCode(partnerBank.getBankCode());
		fsi.setStatus("A");
		fsi.setDepositDate(valueDate);
		fsi.setRealizationDate(valueDate);
		fsi.setInstrumentDate(valueDate);
		fsi.setReceivedDate(valueDate);
		fsi.setRemarks("Cash Back");
		fsi.setPaymentMode(RepayConstants.PAYTYPE_PAYABLE);
		fsi.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);

		fsi.setEntity(partnerBank.getEntity());
		fsi.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		fsi.setReceiptdetailExits(false);
		fsi.setUploadAllocationDetails(null);
		fsi.setReqType(RepayConstants.REQTYPE_POST);

		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setAmount(fsi.getAmount());

		rcd.setValueDate(valueDate);
		rcd.setBankCode(partnerBank.getBankCode());
		rcd.setDepositDate(valueDate);

		if (partnerBankId > 0) {
			rcd.setFundingAc(partnerBankId);
		}
		rcd.setReceivedDate(valueDate);
		rcd.setStatus(fsi.getStatus());
		rcd.setRemarks("Cash Back");
		rcd.setReference(finReference);
		rcd.setReceiptPurpose(FinServiceEvent.SCHDRPY);

		fsi.setReceiptDetail(rcd);
		return fsi;
	}

	private BigDecimal prepareLoanAlocList(long finID, Date valueDate, BigDecimal cashBackAmount) {

		// Fetch all Schedules Dues (Principal + Interest) against all Loans by Date Order
		List<FinanceScheduleDetail> schdList = financeScheduleDetailDAO.getDueSchedulesByFacilityRef(finID, valueDate);

		for (FinanceScheduleDetail curSchd : schdList) {
			BigDecimal priBal = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
			BigDecimal pftBal = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());

			if (priBal.compareTo(BigDecimal.ZERO) <= 0 && pftBal.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			if (DateUtility.compare(curSchd.getSchDate(), valueDate) > 0) {
				continue;
			}

			cashBackAmount = cashBackAmount.subtract(priBal);
			if (curSchd.isTDSApplicable()) {
				cashBackAmount = cashBackAmount.subtract(pftBal.subtract(getTDS(pftBal)));
			} else {
				cashBackAmount = cashBackAmount.subtract(pftBal);
			}

			if (cashBackAmount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}

		// Fetch all LPP & LPI Dues against all Loans by Date Order
		if (cashBackAmount.compareTo(BigDecimal.ZERO) > 0) {

			List<FinODDetails> penaltyList = finODDetailsDAO.getFinODBalByFinRef(finID);

			String lppTaxType = feeTypeDAO.getTaxCompByCode(RepayConstants.ALLOCATION_ODC);
			for (FinODDetails fod : penaltyList) {
				if (fod.getTotPenaltyBal().compareTo(BigDecimal.ZERO) <= 0
						&& fod.getLPIBal().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}
				BigDecimal lppBal = fod.getTotPenaltyBal();
				if (StringUtils.equals(lppTaxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
					lppBal = lppBal.add(getGST(fod.getFinReference(), fod.getTotPenaltyBal(), lppTaxType));
				}
				cashBackAmount = cashBackAmount.subtract(lppBal);

				if (cashBackAmount.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
			}
		}

		// Fetch all Bounce & Receivable Charges against all Loans by Date Order
		if (cashBackAmount.compareTo(BigDecimal.ZERO) > 0) {
			List<ManualAdvise> adviseList = manualAdviseDAO.getManualAdviseByRef(finID, 1, "_AView");
			if (adviseList != null && !adviseList.isEmpty()) {

				String bounceTaxType = feeTypeDAO.getTaxCompByCode(RepayConstants.ALLOCATION_BOUNCE);
				for (ManualAdvise adv : adviseList) {

					BigDecimal balAmt = adv.getAdviseAmount().subtract(adv.getPaidAmount())
							.subtract(adv.getWaivedAmount());
					if (balAmt.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}

					// Bounce Amount
					if (adv.getBounceID() > 0) {
						if (StringUtils.equals(bounceTaxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
							balAmt = balAmt.add(getGST(adv.getFinReference(), balAmt, bounceTaxType));
						}
					} else {
						if (StringUtils.equals(adv.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
							balAmt = balAmt.add(getGST(adv.getFinReference(), balAmt, adv.getTaxComponent()));
						}
					}
					cashBackAmount = cashBackAmount.subtract(balAmt);

					if (cashBackAmount.compareTo(BigDecimal.ZERO) <= 0) {
						break;
					}
				}
			}
		}
		return cashBackAmount;
	}

	// Return TDS Amount on Paid Interest. (i.e Total Paid = Net Due + TDS Returned)
	private BigDecimal getTDS(BigDecimal amount) {

		BigDecimal tds = amount.multiply(tdsPerc);
		tds = tds.divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_UP);
		tds = CalculationUtil.roundAmount(tds, tdsRoundMode, tdsRoundingTarget);
		return tds;
	}

	private BigDecimal getGST(String finReference, BigDecimal amount, String taxType) {

		// No AMount to calculate Tax
		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}

		// No Tax calculation required
		if (StringUtils.isBlank(taxType) || StringUtils.equals(taxType, PennantConstants.List_Select)) {
			return BigDecimal.ZERO;
		}

		// Fetch and store Tax percentages one time
		if (tgstPerc.compareTo(BigDecimal.ZERO) == 0) {
			setTaxPercValues(finReference);
		}

		if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
			return getInclusiveGST(amount);
		} else if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
			return getExclusiveGST(amount);
		}

		return BigDecimal.ZERO;
	}

	private void setTaxPercValues(String finReference) {

		Map<String, Object> dataMap = GSTCalculator.getGSTDataMap(finReference);
		List<Rule> rules = ruleDAO.getGSTRuleDetails(RuleConstants.MODULE_GSTRULE, "");
		String finCcy = SysParamUtil.getAppCurrency();

		tgstPerc = BigDecimal.ZERO;
		if (SysParamUtil.isAllowed(SMTParameterConstants.CALCULATE_GST_ON_GSTRATE_MASTER)) {

			if (dataMap.containsKey("fromState") && dataMap.containsKey("toState")) {
				String fromState = (String) dataMap.get("fromState");
				String toState = (String) dataMap.get("toState");
				if (StringUtils.isNotBlank(fromState) && StringUtils.isNotBlank(toState)) {
					List<GSTRate> gstRateDetailList = gstRateDAO.getGSTRateByStates(fromState, toState, "_AView");
					if (CollectionUtils.isNotEmpty(gstRateDetailList)) {
						for (GSTRate gstRate : gstRateDetailList) {
							if (StringUtils.equals(RuleConstants.CODE_CGST, gstRate.getTaxType())) {
								cgstPerc = gstRate.getPercentage();
								tgstPerc = tgstPerc.add(cgstPerc);
							} else if (StringUtils.equals(RuleConstants.CODE_IGST, gstRate.getTaxType())) {
								igstPerc = gstRate.getPercentage();
								tgstPerc = tgstPerc.add(igstPerc);
							} else if (StringUtils.equals(RuleConstants.CODE_SGST, gstRate.getTaxType())) {
								sgstPerc = gstRate.getPercentage();
								tgstPerc = tgstPerc.add(sgstPerc);
							} else if (StringUtils.equals(RuleConstants.CODE_UGST, gstRate.getTaxType())) {
								ugstPerc = gstRate.getPercentage();
								tgstPerc = tgstPerc.add(ugstPerc);
							} else if (StringUtils.equals(RuleConstants.CODE_CESS, gstRate.getTaxType())) {
								cessPerc = gstRate.getPercentage();
								tgstPerc = tgstPerc.add(cessPerc);
							}
						}
					}
				}

			}
		} else {

			for (Rule rule : rules) {
				if (StringUtils.equals(RuleConstants.CODE_CGST, rule.getRuleCode())) {
					cgstPerc = RuleExecutionUtil.getRuleResult(rule.getSQLRule(), dataMap, finCcy);
					tgstPerc = tgstPerc.add(cgstPerc);
				} else if (StringUtils.equals(RuleConstants.CODE_IGST, rule.getRuleCode())) {
					igstPerc = RuleExecutionUtil.getRuleResult(rule.getSQLRule(), dataMap, finCcy);
					tgstPerc = tgstPerc.add(igstPerc);
				} else if (StringUtils.equals(RuleConstants.CODE_SGST, rule.getRuleCode())) {
					sgstPerc = RuleExecutionUtil.getRuleResult(rule.getSQLRule(), dataMap, finCcy);
					tgstPerc = tgstPerc.add(sgstPerc);
				} else if (StringUtils.equals(RuleConstants.CODE_UGST, rule.getRuleCode())) {
					ugstPerc = RuleExecutionUtil.getRuleResult(rule.getSQLRule(), dataMap, finCcy);
					tgstPerc = tgstPerc.add(ugstPerc);
				}
			}
		}
	}

	private BigDecimal getExclusiveGST(BigDecimal taxableAmount) {
		BigDecimal totalGST = BigDecimal.ZERO;
		totalGST = GSTCalculator.getExclusiveTax(taxableAmount, cgstPerc);
		totalGST = totalGST.add(GSTCalculator.getExclusiveTax(taxableAmount, sgstPerc));
		totalGST = totalGST.add(GSTCalculator.getExclusiveTax(taxableAmount, ugstPerc));
		totalGST = totalGST.add(GSTCalculator.getExclusiveTax(taxableAmount, igstPerc));
		totalGST = totalGST.add(GSTCalculator.getExclusiveTax(taxableAmount, cessPerc));
		return totalGST;
	}

	private BigDecimal getInclusiveGST(BigDecimal taxableAmount) {
		BigDecimal netAmount = GSTCalculator.getInclusiveAmount(taxableAmount, tgstPerc);

		BigDecimal totalGST = BigDecimal.ZERO;
		totalGST = GSTCalculator.getExclusiveTax(taxableAmount, cgstPerc);
		totalGST = totalGST.add(GSTCalculator.getExclusiveTax(netAmount, sgstPerc));
		totalGST = totalGST.add(GSTCalculator.getExclusiveTax(netAmount, ugstPerc));
		totalGST = totalGST.add(GSTCalculator.getExclusiveTax(netAmount, igstPerc));
		totalGST = totalGST.add(GSTCalculator.getExclusiveTax(netAmount, cessPerc));

		if (netAmount.add(totalGST).compareTo(taxableAmount) != 0) {
			BigDecimal diff = taxableAmount.subtract(netAmount.add(totalGST));
			totalGST = totalGST.add(diff);
		}
		return totalGST;
	}

	private AuditHeader getAuditHeader(ManualAdvise aManualAdvise, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aManualAdvise.getBefImage(), aManualAdvise);
		return new AuditHeader(String.valueOf(aManualAdvise.getAdviseID()), String.valueOf(aManualAdvise.getAdviseID()),
				null, null, auditDetail, aManualAdvise.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	private AuditHeader getAuditHeader(PaymentHeader paymentHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, paymentHeader.getBefImage(), paymentHeader);
		return new AuditHeader(String.valueOf(paymentHeader.getPaymentId()),
				String.valueOf(paymentHeader.getPaymentId()), null, null, auditDetail, paymentHeader.getUserDetails(),
				new HashMap<String, List<ErrorDetail>>());
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public void setCashBackDetailDAO(CashBackDetailDAO cashBackDetailDAO) {
		this.cashBackDetailDAO = cashBackDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	public void setPaymentHeaderDAO(PaymentHeaderDAO paymentHeaderDAO) {
		this.paymentHeaderDAO = paymentHeaderDAO;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public void setGstRateDAO(GSTRateDAO gstRateDAO) {
		this.gstRateDAO = gstRateDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

}
