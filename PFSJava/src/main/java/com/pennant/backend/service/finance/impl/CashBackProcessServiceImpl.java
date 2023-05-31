package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
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
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
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
import com.pennant.backend.dao.transactionmapping.TransactionMappingDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.CashBackDetail;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.RestructureCharge;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.CashBackProcessService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pff.cd.model.Manufacturer;
import com.pennanttech.pff.cd.model.TransactionMapping;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennattech.pff.cd.dao.ManufacturerDAO;

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
	private FinODDetailsDAO finODDetailsDAO;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private PostingsPreparationUtil postingsPreparationUtil;
	private TransactionMappingDAO transactionMappingDAO;
	private ManufacturerDAO manufacturerDAO;

	private String tdsRoundMode = null;
	private int tdsRoundingTarget = 0;
	private BigDecimal tdsPerc = BigDecimal.ZERO;

	@Override
	public void createCashBackAdvice(FinanceMain fm, Date appDate, FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		if (!StringUtils.equals(FinanceConstants.PRODUCT_CD, fm.getProductCategory())) {
			logger.debug(Literal.LEAVING);
			return;
		}

		List<CashBackDetail> cashBackDetailList = new ArrayList<>();
		TaxAmountSplit finTax = new TaxAmountSplit();
		ManualAdvise manualAdvise = null;
		Promotion promotion = fd.getPromotion();

		BigDecimal retensionAmount = BigDecimal.ZERO;

		switch (promotion.getCbPyt()) {

		case PennantConstants.DBD_AND_MBD_SEPARATELY:

			boolean dbdProcAvail = false;
			BigDecimal dbdAmount = null;

			if (promotion.isDbd()) {

				dbdAmount = fm.getFinAmount().multiply(promotion.getDbdPerc()).divide(new BigDecimal(100), 0,
						RoundingMode.HALF_DOWN);

				if (!promotion.isDbdRtnd()) {
					// Payable Advise creation against cash back amount in Hold Status
					// Hold is for to avoid usage in screens till Settlement process completed
					manualAdvise = cashBackDueCreation(fm, promotion.getDbdFeeTypId(), dbdAmount, appDate);

					// Logging Cash back process details for future usage
					cashBackDetailList.add(prepareCashbackLog(fm, manualAdvise, PennantConstants.DBD_RETAINED,
							BigDecimal.ZERO, finTax, null, 0));

					dbdProcAvail = true;
				} else {
					retensionAmount = dbdAmount;
					preparACDataSet(fm, retensionAmount, PennantConstants.DBD_RETAINED, cashBackDetailList, fd);
					dbdProcAvail = true;
				}
			}

			if (promotion.isMbd()) {
				BigDecimal mbdAmount = BigDecimal.ZERO;

				if (!promotion.isMbdRtnd()) {

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
					cashBackDetailList.add(prepareCashbackLog(fm, manualAdvise, PennantConstants.MBD_RETAINED,
							BigDecimal.ZERO, finTax, null, 0));
				} else {

					if (dbdProcAvail) {
						retensionAmount = fm.getSvAmount().subtract(dbdAmount);
					} else {
						retensionAmount = fm.getSvAmount();
					}
					preparACDataSet(fm, retensionAmount, PennantConstants.MBD_RETAINED, cashBackDetailList, fd);
				}
			}

			break;
		case PennantConstants.DBD_AND_MBD_TOGETHER:
			if (promotion.isMbd() && promotion.isDbd() && (!promotion.isMbdRtnd() || !promotion.isDbdRtnd())) {
				// Payable Advise creation against cash back amount in Hold Status
				// Hold is for to avoid usage in screens till Settlement process completed
				manualAdvise = cashBackDueCreation(fm, promotion.getDbdAndMbdFeeTypId(), fm.getSvAmount(), appDate);

				// Logging Cash back process details for future usage

				cashBackDetailList.add(prepareCashbackLog(fm, manualAdvise, PennantConstants.DBMBD_RETAINED,
						BigDecimal.ZERO, finTax, null, 0));
			} else {
				retensionAmount = fm.getSvAmount();
				preparACDataSet(fm, retensionAmount, PennantConstants.MBD_RETAINED, cashBackDetailList, fd);
			}
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

	private AEEvent preparACDataSet(FinanceMain fm, BigDecimal retensionAmount, String type,
			List<CashBackDetail> cbdList, FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldRender efr = fd.getExtendedFieldRender();

		Map<String, BigDecimal> percentages = GSTCalculator.getManufacMerchTaxPercentages(fm.getFinID(),
				fm.getFinBranch(), efr, type);

		// Accounting process for Retension of Cashback Amounts
		AEEvent aeEvent = new AEEvent();

		aeEvent.setAccountingEvent(AccountingEvent.CBRET);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		// Finance main
		amountCodes.setFinType(fm.getFinType());

		aeEvent.setPostingUserBranch(fm.getUserDetails().getBranchCode());
		aeEvent.setValueDate(SysParamUtil.getAppDate());
		aeEvent.setPostDate(SysParamUtil.getAppDate());
		aeEvent.setEntityCode(fm.getEntityCode());
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCustID(fm.getCustID());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setFinReference(fm.getFinReference());
		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());

		TaxAmountSplit finTax = GSTCalculator.getInclusiveGST(retensionAmount, percentages);

		Map<String, Object> eventMapping = aeEvent.getDataMap();
		eventMapping.put("cbret_cgst", finTax.getcGST());
		eventMapping.put("cbret_sgst", finTax.getsGST());
		eventMapping.put("cbret_ugst", finTax.getuGST());
		eventMapping.put("cbret_igst", finTax.getiGST());
		eventMapping.put("cbret_cess", finTax.getCess());

		// GST parameters
		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(fm.getFinID());
		if (gstExecutionMap != null) {
			for (String key : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					eventMapping.put(key, gstExecutionMap.get(key));
				}
			}
		}

		eventMapping.put("ae_cbret", retensionAmount);

		if (type.equals(PennantConstants.MBD_RETAINED)) {
			eventMapping.put("ae_mbd_cbret", retensionAmount);
			eventMapping.put("mbd_cbret_cgst", finTax.getcGST());
			eventMapping.put("mbd_cbret_sgst", finTax.getsGST());
			eventMapping.put("mbd_cbret_ugst", finTax.getuGST());
			eventMapping.put("mbd_cbret_igst", finTax.getiGST());
			eventMapping.put("mbd_cbret_cess", finTax.getCess());
		} else if (type.equals(PennantConstants.DBD_RETAINED)) {
			eventMapping.put("ae_dbd_cbret", retensionAmount);
			eventMapping.put("dbd_cbret_cgst", finTax.getcGST());
			eventMapping.put("dbd_cbret_sgst", finTax.getsGST());
			eventMapping.put("dbd_cbret_ugst", finTax.getuGST());
			eventMapping.put("dbd_cbret_igst", finTax.getiGST());
			eventMapping.put("dbd_cbret_cess", finTax.getCess());
		}
		aeEvent.setDataMap(eventMapping);
		Long accountsetId = AccountingEngine.getAccountSetID(fm, AccountingEvent.CBRET,
				FinanceConstants.MODULEID_FINTYPE);

		if (accountsetId != null && accountsetId > 0) {
			aeEvent.getAcSetIDList().add(accountsetId);
		}

		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

		if (!aeEvent.isPostingSucess()) {
			throw new InterfaceException("9998", "Cash Retension accounting postings failed.");
		}

		BigDecimal retensionAmountexcludeGST = retensionAmount.subtract(finTax.getcGST()).subtract(finTax.getsGST())
				.subtract(finTax.getuGST()).subtract(finTax.getiGST()).subtract(finTax.getCess());
		CashBackDetail cbDetail = prepareCashbackLog(fm, null, type, retensionAmountexcludeGST, finTax,
				fd.getExtendedFieldRender(), aeEvent.getLinkedTranId());
		cbdList.add(cbDetail);

		// GST Invoice Preparation
		if (gstInvoiceTxnService != null && aeEvent.getLinkedTranId() > 0 && CollectionUtils.isNotEmpty(cbdList)) {

		}
		return aeEvent;

	}

	private CashBackDetail prepareCashbackLog(FinanceMain fm, ManualAdvise ma, String type,
			BigDecimal retensionAmountexcludeGST, TaxAmountSplit finTax, ExtendedFieldRender aExtendedFieldRender,
			long linkedTranId) {
		logger.debug(Literal.ENTERING);

		CashBackDetail cbDetail = new CashBackDetail();
		if (aExtendedFieldRender != null) {
			if (type.equals("DBD")) {
				long mId = Long.valueOf(aExtendedFieldRender.getMapValues().get("MID").toString());
				String tId = aExtendedFieldRender.getMapValues().get("TID").toString();
				TransactionMapping tm = transactionMappingDAO.getDealerDetails(mId, tId);
				cbDetail.setFeeTypeId(100);
				cbDetail.setManfMerchId(String.valueOf(tm.getDealerCode()));
				cbDetail.setStoreName(tm.getDealerName());
			} else if (type.equals("MBD")) {
				long oEMID = Long.valueOf(aExtendedFieldRender.getMapValues().get("OEMID").toString());
				Manufacturer manfDetails = manufacturerDAO.getDetails(oEMID);
				cbDetail.setFeeTypeId(200);
				cbDetail.setManfMerchId(String.valueOf(oEMID));
				cbDetail.setStoreName(manfDetails.getName());
			}
		}

		cbDetail.setFinReference(fm.getFinReference());
		cbDetail.setType(type);
		if (ma != null) {
			cbDetail.setAdviseId(ma.getAdviseID());
			cbDetail.setAmount(ma.getAdviseAmount());
		} else {
			cbDetail.setRetainedAmount(retensionAmountexcludeGST);
			cbDetail.setLinkedTranId(linkedTranId);
		}

		cbDetail.setcGST(BigDecimal.ZERO);
		cbDetail.setsGST(BigDecimal.ZERO);
		cbDetail.setuGST(BigDecimal.ZERO);
		cbDetail.setiGST(BigDecimal.ZERO);
		cbDetail.setCess(BigDecimal.ZERO);
		cbDetail.settGST(BigDecimal.ZERO);
		cbDetail.setRefunded(false);

		logger.debug(Literal.LEAVING);
		return cbDetail;
	}

	private ManualAdvise cashBackDueCreation(FinanceMain finMain, long feeId, BigDecimal cashbackAmount, Date appDate) {
		logger.debug(Literal.ENTERING);

		ManualAdvise ma = new ManualAdvise();
		ma.setAdviseID(manualAdviseService.getNewAdviseID());
		ma.setFinReference(finMain.getFinReference());
		ma.setAdviseType(AdviseType.PAYABLE.id());
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

		Timestamp sysDate = new Timestamp(System.currentTimeMillis());

		// Payment Header
		PaymentHeader ph = new PaymentHeader();
		ph.setFinID(finMain.getFinID());
		ph.setFinReference(finMain.getFinReference());
		ph.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		ph.setCreatedOn(sysDate);
		ph.setApprovedOn(sysDate);
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
		FinServiceInstruction serviceInstr = createFinServInstr(finID, finReference, rcptAmt, valueDate);
		serviceInstr.setAdviseId(cb.getAdviseId());
		serviceInstr.setAdviseAmount(rcptAmt);

		serviceInstr.setReceiptPurpose(ReceiptPurpose.SCHDRPY.code());
		FinanceDetail detail = receiptService.receiptTransaction(serviceInstr);
		if (detail.getReturnStatus() != null) {
			throw new AppException("AppException", detail.getReturnStatus().getReturnText());
		}

		return actCashBack.subtract(rcptAmt);
	}

	private FinServiceInstruction createFinServInstr(long finID, String finReference, BigDecimal rcptAmount,
			Date valueDate) {
		FinServiceInstruction fsi = new FinServiceInstruction();

		fsi.setFinID(finID);
		fsi.setFinReference(finReference);
		fsi.setModule("Receipts");
		fsi.setValueDate(valueDate);
		fsi.setAmount(rcptAmount);
		fsi.setAllocationType(AllocationType.AUTO);

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

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(finID);

		for (FinanceScheduleDetail curSchd : schdList) {
			BigDecimal priBal = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
			BigDecimal pftBal = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());

			if (priBal.compareTo(BigDecimal.ZERO) <= 0 && pftBal.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			if (DateUtil.compare(curSchd.getSchDate(), valueDate) > 0) {
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

			String lppTaxType = feeTypeDAO.getTaxCompByCode(Allocation.ODC);
			for (FinODDetails fod : penaltyList) {
				if (fod.getTotPenaltyBal().compareTo(BigDecimal.ZERO) <= 0
						&& fod.getLPIBal().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}
				BigDecimal lppBal = fod.getTotPenaltyBal();
				TaxAmountSplit tax = GSTCalculator.calculateGST(taxPercentages, lppTaxType, fod.getTotPenaltyBal());

				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(lppTaxType)) {
					lppBal = lppBal.add(tax.gettGST());
				}
				cashBackAmount = cashBackAmount.subtract(lppBal);

				if (cashBackAmount.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
			}
		}

		// Fetch all Bounce & Receivable Charges against all Loans by Date Order
		if (cashBackAmount.compareTo(BigDecimal.ZERO) > 0) {
			List<ManualAdvise> adviseList = manualAdviseDAO.getReceivableAdvises(finID, "_AView");

			String bounceTaxType = feeTypeDAO.getTaxCompByCode(Allocation.BOUNCE);

			for (ManualAdvise adv : adviseList) {

				BigDecimal balAmt = adv.getAdviseAmount().subtract(adv.getPaidAmount()).subtract(adv.getWaivedAmount());
				if (balAmt.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				String taxComponent = adv.getTaxComponent();

				if (adv.getBounceID() > 0) {
					taxComponent = bounceTaxType;
				}

				TaxAmountSplit tax = GSTCalculator.calculateGST(taxPercentages, taxComponent, balAmt);

				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
					balAmt = balAmt.add(tax.gettGST());
				}

				cashBackAmount = cashBackAmount.subtract(balAmt);

				if (cashBackAmount.compareTo(BigDecimal.ZERO) <= 0) {
					break;
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

	@Override
	public void createRestructReceipt(FinanceDetail fd) {
		FinScheduleData schData = fd.getFinScheduleData();
		FinanceMain fm = schData.getFinanceMain();
		RestructureDetail rd = schData.getRestructureDetail();

		List<UploadAlloctionDetail> uplAlocList = new ArrayList<>();
		BigDecimal totAmount = BigDecimal.ZERO;
		BigDecimal bpiAmount = BigDecimal.ZERO;

		for (RestructureCharge rc : rd.getChargeList()) {
			if (!rc.isCapitalized()) {
				continue;
			}

			if ("BPI".equals(rc.getAlocType()) || Allocation.FEE.equals(rc.getAlocType())) {
				if ("BPI".equals(rc.getAlocType())) {
					bpiAmount = rc.getTotalAmount();
				}

				continue;
			}

			String alocType = "";
			switch (rc.getAlocType()) {
			case Allocation.PFT:
				alocType = "I";
				break;
			case Allocation.PRI:
				alocType = "P";
				break;
			case Allocation.LPFT:
				alocType = "L";
				break;
			case Allocation.ODC:
				alocType = "O";
				break;
			case Allocation.MANADV:
				alocType = "M";
				break;
			default:
				alocType = "B";
				break;
			}

			if (StringUtils.isBlank(alocType)) {
				continue;// ERROR
			}

			UploadAlloctionDetail detail = new UploadAlloctionDetail();
			detail.setAllocationType(alocType);
			detail.setReferenceCode(rc.getFeeCode());
			detail.setPaidAmount(rc.getTotalAmount());
			uplAlocList.add(detail);

			totAmount = totAmount.add(rc.getTotalAmount());
		}

		FinServiceInstruction fsi = new FinServiceInstruction();
		fsi.setFinID(fm.getFinID());
		fsi.setFinReference(fm.getFinReference());
		fsi.setModule("Receipts");
		fsi.setValueDate(rd.getRestructureDate());
		fsi.setAmount(totAmount);
		fsi.setAllocationType(AllocationType.MANUAL);
		fsi.setLoggedInUser(fm.getUserDetails());
		fsi.setStatus(RepayConstants.PAYSTATUS_APPROVED);
		fsi.setDepositDate(rd.getRestructureDate());
		fsi.setRealizationDate(rd.getRestructureDate());
		fsi.setInstrumentDate(rd.getRestructureDate());
		fsi.setReceivedDate(rd.getRestructureDate());
		fsi.setRemarks("Restructure Capitalization");
		fsi.setPaymentMode(RepayConstants.PAYTYPE_RESTRUCT);
		fsi.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		fsi.setEntity(fm.getEntityCode());
		fsi.setReceiptPurpose(FinServiceEvent.RESTRUCTURE);
		fsi.setReceiptdetailExits(false);
		fsi.setUploadAllocationDetails(uplAlocList);
		fsi.setReqType(RepayConstants.REQTYPE_POST);
		fsi.setRecalType(rd.getRecalculationType());
		fsi.setBpiAmount(bpiAmount);
		fsi.setFromDate(rd.getRestructureDate());

		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setAmount(totAmount);
		rcd.setValueDate(rd.getRestructureDate());
		rcd.setDepositDate(rd.getRestructureDate());
		rcd.setReceivedDate(rd.getRestructureDate());
		rcd.setStatus(fsi.getStatus());
		rcd.setRemarks("Restructure Capitalization");
		rcd.setReference(fm.getFinReference());
		rcd.setReceiptPurpose(FinServiceEvent.RESTRUCTURE);
		fsi.setReceiptDetail(rcd);
		fsi.setExcldTdsCal(true);
		fsi.setOldShedules(fm.getOldSchedules());
		fsi.setReceiptPurpose(ReceiptPurpose.RESTRUCTURE.code());

		receiptService = (ReceiptService) SpringBeanUtil.getBean("receiptService");
		FinanceDetail detail = receiptService.receiptTransaction(fsi);
		if (detail.getReturnStatus() != null) {
			throw new AppException("AppException", detail.getReturnStatus().getReturnText());
		}
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	@Autowired
	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	@Autowired
	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

	@Autowired
	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	@Autowired
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	@Autowired
	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setPaymentHeaderDAO(PaymentHeaderDAO paymentHeaderDAO) {
		this.paymentHeaderDAO = paymentHeaderDAO;
	}

	@Autowired
	public void setCashBackDetailDAO(CashBackDetailDAO cashBackDetailDAO) {
		this.cashBackDetailDAO = cashBackDetailDAO;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	@Autowired
	public void setManufacturerDAO(ManufacturerDAO manufacturerDAO) {
		this.manufacturerDAO = manufacturerDAO;
	}

	@Autowired
	public void setTransactionMappingDAO(TransactionMappingDAO transactionMappingDAO) {
		this.transactionMappingDAO = transactionMappingDAO;
	}
}
