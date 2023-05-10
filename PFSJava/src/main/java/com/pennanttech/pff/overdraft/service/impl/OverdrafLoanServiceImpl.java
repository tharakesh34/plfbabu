package com.pennanttech.pff.overdraft.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.ProductUtil;
import com.pennanttech.pff.overdraft.OverdraftConstants;
import com.pennanttech.pff.overdraft.dao.OverdraftLimitDAO;
import com.pennanttech.pff.overdraft.dao.OverdraftLoanDAO;
import com.pennanttech.pff.overdraft.dao.OverdraftPresentmentDAO;
import com.pennanttech.pff.overdraft.model.OverdraftDTO;
import com.pennanttech.pff.overdraft.model.OverdraftLimit;
import com.pennanttech.pff.overdraft.model.OverdraftLimitTransation;
import com.pennanttech.pff.overdraft.model.OverdraftScheduleDetail;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;
import com.pennanttech.pff.payment.model.LoanPayment;
import com.pennanttech.pff.payment.service.LoanPaymentService;
import com.pennanttech.pff.presentment.model.PresentmentCharge;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class OverdrafLoanServiceImpl extends GenericService<OverdraftLimit> implements OverdrafLoanService {
	private static Logger logger = LogManager.getLogger(OverdrafLoanServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private ManualAdviseService manualAdviseService;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private OverdraftPresentmentDAO overdraftPresentmentDAO;
	private OverdraftLoanDAO overdraftLoanDAO;
	private OverdraftLimitDAO overdraftLimitDAO;
	private FeeTypeDAO feeTypeDAO;
	private LoanPaymentService loanPaymentService;

	public OverdrafLoanServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		OverdraftLimit odlh = (OverdraftLimit) auditHeader.getAuditDetail().getModelData();
		odlh.setCreatedOn(new Timestamp(System.currentTimeMillis()));

		TableType tableType = TableType.MAIN_TAB;
		if (odlh.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (odlh.isNewRecord()) {
			overdraftLimitDAO.createLimit(odlh, tableType);
			auditHeader.getAuditDetail().setModelData(odlh);
			auditHeader.setAuditReference(String.valueOf(odlh.getId()));
		} else {
			overdraftLimitDAO.updateLimit(odlh, tableType);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new OverdraftLimit(), odlh.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], odlh.getBefImage(), odlh));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		OverdraftLimit txnHeader = ((OverdraftLimit) auditHeader.getAuditDetail().getModelData()).copyEntity();

		overdraftLimitDAO.deleteLimit(txnHeader.getId(), TableType.TEMP_TAB);

		long finID = txnHeader.getFinID();
		OverdraftLimit befTxnHeader = overdraftLimitDAO.getLimit(finID);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(txnHeader.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(befTxnHeader);
		}

		if (txnHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			overdraftLimitDAO.deleteLimit(txnHeader.getId(), TableType.MAIN_TAB);
		} else {
			txnHeader.setRoleCode("");
			txnHeader.setNextRoleCode("");
			txnHeader.setTaskId("");
			txnHeader.setNextTaskId("");
			txnHeader.setWorkflowId(0);
			txnHeader.setId(befTxnHeader.getId());
			txnHeader.setRecordType("");

			tranType = PennantConstants.TRAN_UPD;
			overdraftLimitDAO.updateLimit(txnHeader, TableType.MAIN_TAB);
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(txnHeader);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		OverdraftLimit txnHeader = (OverdraftLimit) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		overdraftLimitDAO.deleteLimit(txnHeader.getId(), TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		OverdraftLimit txnHeader = (OverdraftLimit) auditHeader.getAuditDetail().getModelData();
		overdraftLimitDAO.deleteLimit(txnHeader.getId(), TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = auditHeader.getAuditDetail();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public void createDisbursment(FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		Timestamp createdOn = new Timestamp(System.currentTimeMillis());

		BigDecimal monthlyLimit = getMonthlyLimit(fm);

		OverdraftLimit limit = new OverdraftLimit();
		limit.setFinID(Long.MIN_VALUE);
		limit.setFinReference(fm.getFinReference());
		limit.setActualLimit(fm.getFinAssetValue());
		limit.setMonthlyLimit(monthlyLimit);
		limit.setActualLimitBal(fm.getFinAssetValue());
		limit.setMonthlyLimitBal(monthlyLimit);
		limit.setBlockLimit(false);
		limit.setBlockType("NA");

		limit.setVersion(1);
		limit.setCreatedBy(fm.getUserDetails().getUserId());
		limit.setCreatedOn(createdOn);
		limit.setLastMntBy(fm.getUserDetails().getUserId());
		limit.setLastMntOn(createdOn);
		limit.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		limit.setRoleCode("");
		limit.setNextRoleCode("");
		limit.setTaskId("");
		limit.setNextTaskId("");
		limit.setRecordType("");
		limit.setWorkflowId(0);

		long headerId = overdraftLimitDAO.createLimit(limit, TableType.MAIN_TAB);

		OverdraftLimitTransation txnDetail = new OverdraftLimitTransation();

		txnDetail.setLimitID(headerId);
		txnDetail.setActualLimit(limit.getActualLimit());
		txnDetail.setMonthlyLimit(limit.getMonthlyLimit());
		txnDetail.setActualLimitBal(limit.getActualLimitBal());
		txnDetail.setMonthlyLimitBal(limit.getMonthlyLimitBal());
		txnDetail.setTxnType(OverdraftConstants.TRANS_TYPE_LOAN_ORG);
		txnDetail.setTxnAmount(BigDecimal.ZERO);
		txnDetail.setTxnCharge(BigDecimal.ZERO);
		txnDetail.setNarration("Disbursement");
		txnDetail.setLastMntBy(fm.getLastMntBy());
		txnDetail.setLastMntOn(createdOn);
		txnDetail.setValueDate(SysParamUtil.getAppDate());

		overdraftLimitDAO.createTransaction(txnDetail);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void createDisbursement(FinAdvancePayments fap) {
		logger.debug(Literal.ENTERING);

		OverdraftDTO overDraft = overdraftLoanDAO.getLoanDetails(fap.getFinID());

		if (overDraft == null) {
			logger.debug(Literal.LEAVING);
			return;
		}

		BigDecimal disbAmount = fap.getAmtToBeReleased();

		BigDecimal feeAmount = overDraft.getOverdraftChrgAmtOrPerc();

		if (!FinanceConstants.FIXED_AMOUNT.equals(overDraft.getOverdraftCalcChrg())) {
			BigDecimal feePercent = feeAmount.divide(new BigDecimal(100));
			feeAmount = PennantApplicationUtil.getPercentageValue(fap.getAmtToBeReleased(), feePercent);
		}

		overDraft.setFinReference(fap.getFinReference());
		overDraft.setUserDetails(fap.getUserDetails());
		overDraft.setLastMntOn(fap.getLastMntOn());
		overDraft.setLastMntBy(fap.getLastMntBy());
		overDraft.setAppDate(null);

		if (overDraft.isOverdraftTxnChrgReq()) {
			createTransactionCharge(overDraft, feeAmount);
		}

		OverdraftLimit limit = overdraftLimitDAO.getLimit(fap.getFinID());

		limit.setActualLimitBal(limit.getActualLimitBal().subtract(disbAmount).subtract(feeAmount));
		limit.setMonthlyLimitBal(limit.getMonthlyLimitBal().subtract(disbAmount).subtract(feeAmount));

		OverdraftLimitTransation transaction = new OverdraftLimitTransation();
		transaction.setLimitID(limit.getId());
		transaction.setMonthlyLimit(limit.getMonthlyLimit());
		transaction.setActualLimit(limit.getActualLimit());
		transaction.setActualLimitBal(limit.getActualLimitBal());
		transaction.setMonthlyLimitBal(limit.getMonthlyLimitBal());
		transaction.setTxnAmount(disbAmount);
		transaction.setTxnCharge(feeAmount);
		transaction.setTxnType(OverdraftConstants.TRANS_TYPE_ADD_DISB);
		transaction.setNarration("Disbursement");
		transaction.setLastMntBy(fap.getLastMntBy());
		transaction.setLastMntOn(fap.getLastMntOn());
		transaction.setValueDate(SysParamUtil.getAppDate());

		overdraftLimitDAO.createTransaction(transaction);

		overdraftLimitDAO.logLimt(limit.getId());

		overdraftLimitDAO.updateBalances(limit);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void createBills(CustEODEvent custEODEvent) {
		logger.debug(Literal.ENTERING);

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		Date nextBUsDate = custEODEvent.getEventProperties().getNextDate();

		List<OverdraftLimitTransation> trnsactions = new ArrayList<>();

		for (FinEODEvent finEODEvent : finEODEvents) {
			FinanceMain fm = finEODEvent.getFinanceMain();

			if (ProductUtil.isNotOverDraft(fm)) {
				continue;
			}

			long finID = fm.getFinID();
			OverdraftLimit limit = overdraftLimitDAO.getLimit(finID);

			OverdraftLimitTransation transaction = new OverdraftLimitTransation();
			transaction.setLimitID(limit.getId());
			transaction.setMonthlyLimit(limit.getMonthlyLimit());
			transaction.setActualLimit(limit.getActualLimit());
			transaction.setActualLimitBal(limit.getActualLimitBal());
			transaction.setMonthlyLimitBal(limit.getMonthlyLimitBal());
			transaction.setTxnAmount(BigDecimal.ZERO);
			transaction.setTxnCharge(BigDecimal.ZERO);
			transaction.setTxnType(OverdraftConstants.TRANS_TYPE_EOM);
			transaction.setNarration("Monthly Bill");
			transaction.setLastMntBy(0);
			transaction.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			transaction.setValueDate(nextBUsDate);

			trnsactions.add(transaction);
		}

		overdraftLimitDAO.createTransactions(trnsactions);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void createPayment(FinReceiptHeader frh) {
		logger.debug(Literal.ENTERING);

		FinanceMain overDraft = overdraftLoanDAO.getLoanDetails(frh.getFinID());

		if (overDraft == null || !overDraft.isOverdraftTxnChrgReq()) {
			return;
		}

		List<ManualAdvise> receivableAdvises = frh.getReceivableAdvises();
		List<ReceiptAllocationDetail> allocations = frh.getAllocations();
		List<ReceiptAllocationDetail> allocationsummary = frh.getAllocationsSummary();

		long overdraftTxnChrgFeeType = overDraft.getOverdraftTxnChrgFeeType();

		BigDecimal principalAmt = BigDecimal.ZERO;
		BigDecimal txnCharges = BigDecimal.ZERO;

		for (ManualAdvise ma : receivableAdvises) {
			if (overdraftTxnChrgFeeType == ma.getFeeTypeID()) {
				for (ReceiptAllocationDetail rad : allocationsummary) {
					if (StringUtils.equals(rad.getFeeTypeCode(), ma.getFeeTypeCode())) {
						txnCharges = rad.getPaidAmount().subtract(rad.getPaidGST());
						break;
					}
				}
			}
		}

		Map<Long, Long> feeTypes = new HashMap<Long, Long>();

		String receiptMode = frh.getReceiptMode();
		if (txnCharges.compareTo(BigDecimal.ZERO) == 0 && ReceiptMode.PRESENTMENT.equals(receiptMode)) {
			for (ReceiptAllocationDetail allocation : frh.getAllocations()) {
				String allocationType = allocation.getAllocationType();
				long adviseId = allocation.getAllocationTo();

				if (Allocation.MANADV.equals(allocationType)) {
					BigDecimal paidAmount = allocation.getPaidAmount();
					BigDecimal waivedAmount = allocation.getWaivedAmount();
					if (paidAmount.compareTo(BigDecimal.ZERO) > 0 || waivedAmount.compareTo(BigDecimal.ZERO) > 0) {
						for (FinReceiptDetail rcd : frh.getReceiptDetails()) {
							for (ManualAdviseMovements movement : rcd.getAdvMovements()) {
								if (adviseId == movement.getAdviseID()) {
									long feeTypeId = getFeeTypeID(adviseId, feeTypes);
									if (feeTypeId == overdraftTxnChrgFeeType) {
										txnCharges = movement.getPaidAmount();
									}
								}
							}
						}
					}
				}
			}
		}

		for (ReceiptAllocationDetail rad : allocations) {
			if (Allocation.PRI.equals(rad.getAllocationType())) {
				principalAmt = rad.getPaidAmount();
				break;
			}
		}

		OverdraftLimit limit = overdraftLimitDAO.getLimit(frh.getFinID());
		limit.setActualLimitBal(limit.getActualLimitBal().add(principalAmt).add(txnCharges));

		OverdraftLimitTransation transaction = new OverdraftLimitTransation();
		transaction.setLimitID(limit.getId());
		transaction.setMonthlyLimit(limit.getMonthlyLimit());
		transaction.setActualLimit(limit.getActualLimit());
		transaction.setActualLimitBal(limit.getActualLimitBal());
		transaction.setMonthlyLimitBal(limit.getMonthlyLimitBal());
		transaction.setTxnAmount(principalAmt.add(txnCharges));
		transaction.setTxnCharge(txnCharges);
		transaction.setTxnType(OverdraftConstants.TRANS_TYPE_EOM);
		transaction.setNarration("Customer Payment");
		transaction.setTxnDate(new Timestamp(System.currentTimeMillis()));
		transaction.setValueDate(frh.getValueDate());

		if (transaction.getTxnAmount().compareTo(BigDecimal.ZERO) > 0) {
			overdraftLimitDAO.createTransaction(transaction);

			overdraftLimitDAO.logLimt(limit.getId());

			overdraftLimitDAO.updateBalances(limit);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void createWaiver(List<FeeWaiverDetail> fwdList) {
		logger.debug(Literal.ENTERING);

		Long finID = fwdList.get(0).getFinID();

		OverdraftDTO fm = overdraftLoanDAO.getChargeConfig(finID);

		if (ProductUtil.isOverDraftChargeNotReq(fm)) {
			return;
		}

		OverdraftLimit limit = overdraftLimitDAO.getLimit(finID);

		List<OverdraftLimitTransation> transactions = new ArrayList<>();

		Date appDate = SysParamUtil.getAppDate();

		for (FeeWaiverDetail fwd : fwdList) {
			if (!StringUtils.equals(fm.getTxnChrFeeType().getFeeTypeCode(), fwd.getFeeTypeCode())) {
				continue;
			}

			limit.setActualLimitBal(limit.getActualLimitBal().add(fwd.getCurrActualWaiver()));

			OverdraftLimitTransation transaction = new OverdraftLimitTransation();
			transaction.setLimitID(limit.getId());
			transaction.setMonthlyLimit(limit.getMonthlyLimit());
			transaction.setActualLimit(limit.getActualLimit());
			transaction.setActualLimitBal(limit.getActualLimitBal());
			transaction.setMonthlyLimitBal(limit.getMonthlyLimitBal());
			transaction.setTxnAmount(fwd.getCurrActualWaiver());
			transaction.setTxnCharge(BigDecimal.ZERO);
			transaction.setTxnType(OverdraftConstants.TRANS_TYPE_FEE_WAIVER);
			transaction.setNarration("Transaction Charge Waiver");
			transaction.setTxnDate(new Timestamp(System.currentTimeMillis()));
			transaction.setValueDate(appDate);

			transactions.add(transaction);
		}

		overdraftLimitDAO.createTransactions(transactions);

		overdraftLimitDAO.logLimt(limit.getId());

		overdraftLimitDAO.updateBalances(limit);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void cancelPayment(BigDecimal adviseAmount, BigDecimal totalPriAmount, FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		if (ProductUtil.isOverDraftChargeNotReq(fm)) {
			return;
		}

		OverdraftLimit limit = overdraftLimitDAO.getLimit(fm.getFinID());
		limit.setActualLimitBal(limit.getActualLimitBal().subtract(totalPriAmount.subtract(adviseAmount)));

		OverdraftLimitTransation transaction = new OverdraftLimitTransation();
		transaction.setLimitID(limit.getId());
		transaction.setMonthlyLimit(limit.getMonthlyLimit());
		transaction.setActualLimit(limit.getActualLimit());
		transaction.setActualLimitBal(limit.getActualLimitBal());
		transaction.setMonthlyLimitBal(limit.getMonthlyLimitBal());
		transaction.setTxnAmount(totalPriAmount.subtract(adviseAmount));
		transaction.setTxnCharge(BigDecimal.ZERO);
		transaction.setTxnType(OverdraftConstants.TRANS_TYPE_RECEIPT);
		transaction.setNarration("Payment Cancelled/Bounced");
		transaction.setTxnDate(new Timestamp(System.currentTimeMillis()));
		transaction.setValueDate(SysParamUtil.getAppDate());

		overdraftLimitDAO.createTransaction(transaction);

		overdraftLimitDAO.logLimt(limit.getId());

		overdraftLimitDAO.updateBalances(limit);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void createPenalties(CustEODEvent custEODEvent) {
		logger.debug(Literal.ENTERING);

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		Date appDate = custEODEvent.getEventProperties().getAppDate();

		List<OverdraftLimit> blockedLoans = new ArrayList<>();
		List<OverdraftLimitTransation> transactions = new ArrayList<>();

		for (FinEODEvent finEODEvent : finEODEvents) {
			OverdraftDTO odto = finEODEvent.getOverDraftFM();

			if (odto == null) {
				continue;
			}

			odto.setAppDate(appDate);

			FinODPenaltyRate pr = odto.getPenaltyRate();
			List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

			BigDecimal collectionAmt = pr.getOverDraftColAmt();
			if (collectionAmt == null || collectionAmt.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			boolean block = false;
			long finID = odto.getFinID();
			OverdraftLimit limit = overdraftLimitDAO.getLimit(finID);

			FinODDetails overdue = finODDetailsDAO.getTotals(finID);

			for (FinanceScheduleDetail schd : schedules) {
				Date schDate = schd.getSchDate();

				if (schd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}

				int totalGraceDays = Integer.sum(pr.getODGraceDays(), pr.getOverDraftExtGraceDays());

				if (DateUtil.addDays(schDate, totalGraceDays).compareTo(appDate) != 0) {
					continue;
				}

				if (!isFullyPaid(finID, overdue, schd)) {
					createTransactionCharge(odto, collectionAmt);

					block = true;

					OverdraftLimitTransation transaction = new OverdraftLimitTransation();
					transaction.setLimitID(limit.getId());
					transaction.setMonthlyLimit(limit.getMonthlyLimit());
					transaction.setActualLimit(limit.getActualLimit());
					transaction.setActualLimitBal(limit.getActualLimitBal());
					transaction.setMonthlyLimitBal(limit.getMonthlyLimitBal());
					transaction.setTxnAmount(collectionAmt);
					transaction.setTxnCharge(BigDecimal.ZERO);
					transaction.setTxnType(OverdraftConstants.TRANS_TYPE_COLLECT_CHRG);
					transaction.setNarration("Cash Collection Penalty");
					transaction.setTxnDate(new Timestamp(System.currentTimeMillis()));
					transaction.setValueDate(appDate);

					transactions.add(transaction);

					break;
				}
			}

			if (block) {
				blockedLoans.add(limit);
			}
		}

		if (CollectionUtils.isNotEmpty(blockedLoans)) {
			overdraftLimitDAO.blockLimit(blockedLoans);
		}

		overdraftLimitDAO.createTransactions(transactions);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public FinODDetails calculateODAmounts(FinanceMain fm, Date schDate, String custBranch) {
		FinODDetails odDetails = new FinODDetails();

		long finID = fm.getFinID();
		long custID = fm.getCustID();
		String finCcy = fm.getFinCcy();
		String finBranch = fm.getFinBranch();

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(custID, finCcy, custBranch, finBranch);

		List<ManualAdvise> manualAdvises = manualAdviseDAO.getAdvisesByDueDate(finID, schDate, "_AView");

		BigDecimal balanceAmount = BigDecimal.ZERO;
		BigDecimal actualAmt = BigDecimal.ZERO;
		BigDecimal paidAmt = BigDecimal.ZERO;
		BigDecimal maxOdAmount = BigDecimal.ZERO;

		for (ManualAdvise ma : manualAdvises) {
			BigDecimal adviseAmount = ma.getAdviseAmount();

			if (ma.isTaxApplicable() && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(ma.getTaxComponent())) {
				TaxAmountSplit tax = GSTCalculator.getExclusiveGST(adviseAmount, taxPercentages);

				actualAmt = actualAmt.add(adviseAmount.add(tax.gettGST()));

				paidAmt = paidAmt.add(ma.getPaidAmount());
				paidAmt = paidAmt.add(ma.getWaivedAmount());
				paidAmt = paidAmt.add(CalculationUtil.getTotalPaidGST(ma));
				paidAmt = paidAmt.add(CalculationUtil.getTotalWaivedGST(ma));

				maxOdAmount = maxOdAmount.add(actualAmt);
				balanceAmount = balanceAmount.add(actualAmt.subtract(paidAmt));
			} else {
				balanceAmount = balanceAmount.add(adviseAmount);
				maxOdAmount = maxOdAmount.add(adviseAmount);
			}
		}

		odDetails.setCurOverdraftTxnChrg(balanceAmount);
		odDetails.setMaxOverdraftTxnChrg(maxOdAmount);

		return odDetails;
	}

	@Override
	public boolean isFullyPaid(long finID, FinODDetails overdue, FinanceScheduleDetail schd) {
		boolean fullyPaid = isPaid(schd) && isPaid(overdue) && isChargesPaid(finID, schd.getSchDate());
		return fullyPaid;
	}

	@Override
	public int getGraceDays(FinanceMain fm) {
		List<FinODPenaltyRate> penaltyRates = fm.getPenaltyRates();

		if (CollectionUtils.isEmpty(penaltyRates)) {
			penaltyRates = finODPenaltyRateDAO.getFinODPenaltyRateByRef(fm.getFinID(), "");
			fm.setPenaltyRates(penaltyRates);
		}

		if (CollectionUtils.isEmpty(penaltyRates)) {
			FinODPenaltyRate penaltyRate = new FinODPenaltyRate();
			penaltyRates = new ArrayList<>();
			penaltyRates.add(penaltyRate);
			fm.setPenaltyRates(penaltyRates);
		}

		FinODPenaltyRate penaltyRate = penaltyRates.get(0);

		if (penaltyRate.isODIncGrcDays()) {
			return penaltyRate.getODGraceDays();
		} else {
			return penaltyRate.getODGraceDays() + penaltyRate.getOverDraftExtGraceDays();
		}
	}

	@Override
	public BigDecimal getTransactionCharge(List<ManualAdviseMovements> movements, Date schDate, Date grcDate) {
		BigDecimal maxODTxnChrg = BigDecimal.ZERO;

		for (ManualAdviseMovements mam : movements) {
			Date finSchdDate = mam.getDueDate();
			if (finSchdDate.compareTo(schDate) != 0) {
				continue;
			}

			// Max OD amounts is same as rpdList balance amounts
			Date finValueDate = mam.getMovementDate();
			BigDecimal paidAdviseAmt = mam.getPaidAmount().add(mam.getWaivedAmount());

			String taxType = mam.getTaxComponent();
			if (mam.isTaxApplicable() && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
				BigDecimal paidGST = CalculationUtil.getTotalPaidGST(mam);
				BigDecimal waivedGST = CalculationUtil.getTotalWaivedGST(mam);
				paidAdviseAmt = paidAdviseAmt.add(paidGST).add(waivedGST);
			}

			String status = mam.getStatus();
			if (RepayConstants.MODULETYPE_BOUNCE.equals(status) || RepayConstants.MODULETYPE_CANCEL.equals(status)) {
				paidAdviseAmt = BigDecimal.ZERO;
			}

			if (finSchdDate.compareTo(finValueDate) == 0 || DateUtil.compare(grcDate, finValueDate) >= 0) {
				maxODTxnChrg = maxODTxnChrg.add(paidAdviseAmt);
			}
		}

		return maxODTxnChrg;
	}

	@Override
	public void closeByMaturity(CustEODEvent custEODEvent) {
		logger.debug(Literal.ENTERING);

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		EventProperties eventProperties = custEODEvent.getEventProperties();
		Date valueDate = eventProperties.getValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {
			FinanceMain fm = finEODEvent.getFinanceMain();

			long finID = fm.getFinID();
			String finReference = fm.getFinReference();

			if (ProductUtil.isNotOverDraft(fm)) {
				continue;
			}

			List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

			LoanPayment lp = new LoanPayment(finID, finReference, schedules, valueDate);
			boolean isFinFullyPaid = loanPaymentService.isSchdFullyPaid(lp);

			if (!(isFinFullyPaid && DateUtil.compare(valueDate, fm.getMaturityDate()) >= 0)) {
				continue;
			}

			financeMainDAO.updateMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false, valueDate);
			financeProfitDetailDAO.updateFinPftMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void unBlockLimit(long finID, List<FinanceScheduleDetail> schedules, Date valueDate) {
		boolean isFinFullyPaid = false;

		FinODDetails overdue = finODDetailsDAO.getTotals(finID);

		for (FinanceScheduleDetail schd : schedules) {
			Date schDate = schd.getSchDate();
			if (DateUtil.compare(schDate, valueDate) <= 0) {
				continue;
			}

			isFinFullyPaid = isFullyPaid(finID, overdue, schd);
		}

		if (isFinFullyPaid && isAutoBlock(finID)) {
			unBlockLimit(finID);
		}
	}

	@Override
	public List<ErrorDetail> validateDisbursment(FinScheduleData schdData, BigDecimal disbAmt, Date fromDate) {
		FinanceMain fm = schdData.getFinanceMain();

		List<ErrorDetail> ed = new ArrayList<>();
		int ccyFormat = CurrencyUtil.getFormat(fm.getFinCcy());

		List<OverdraftScheduleDetail> overdraftSchedules = schdData.getOverdraftScheduleDetails();

		BigDecimal availableLimit = getAvailableLimit(fromDate, overdraftSchedules);

		if (availableLimit.compareTo(BigDecimal.ZERO) <= 0) {
			availableLimit = fm.getFinAssetValue();
		}

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		availableLimit = availableLimit.subtract(getClosingBalance(fromDate, schedules));

		if (disbAmt.compareTo(availableLimit) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = PennantApplicationUtil.amountFormate(disbAmt, ccyFormat);
			valueParm[1] = PennantApplicationUtil.amountFormate(availableLimit, ccyFormat);

			ed.add(ErrorUtil.getErrorDetail(new ErrorDetail("91119", valueParm)));
		} else {
			BigDecimal totDisbAmount = BigDecimal.ZERO;
			for (FinanceScheduleDetail curSchd : schedules) {
				totDisbAmount = totDisbAmount.add(curSchd.getDisbAmount().subtract(curSchd.getSchdPriPaid()));
			}

			totDisbAmount = disbAmt.add(totDisbAmount);

			if (totDisbAmount.compareTo(fm.getFinAssetValue()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = PennantApplicationUtil.amountFormate(totDisbAmount, ccyFormat);
				valueParm[1] = PennantApplicationUtil.amountFormate(fm.getFinAssetValue(), ccyFormat);

				ed.add(ErrorUtil.getErrorDetail(new ErrorDetail("91120", valueParm)));
			}
		}

		OverdraftLimit limit = getLimit(fm.getFinID());

		BigDecimal monthlyLmtBal = limit.getMonthlyLimitBal();
		BigDecimal actualLmtBal = limit.getActualLimitBal();
		BigDecimal feeAmount = BigDecimal.ZERO;

		if (FinanceConstants.FIXED_AMOUNT.equals(fm.getOverdraftCalcChrg())) {
			feeAmount = fm.getOverdraftChrgAmtOrPerc();
		} else {
			BigDecimal feePercent = fm.getOverdraftChrgAmtOrPerc().divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
			feeAmount = PennantApplicationUtil.getPercentageValue(disbAmt, feePercent);
		}

		if (monthlyLmtBal != null || actualLmtBal != null) {
			disbAmt = disbAmt.add(feeAmount);
			if (disbAmt.compareTo(monthlyLmtBal) > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Insufficient Monthly limit";

				ed.add(ErrorUtil.getErrorDetail(new ErrorDetail("21005", valueParm)));
			}
			if (disbAmt.compareTo(actualLmtBal) > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Insufficient Actual limit";

				ed.add(ErrorUtil.getErrorDetail(new ErrorDetail("21005", valueParm)));
			}
		}

		if (isLimitBlock(fm.getFinID())) {
			String[] valueParm = new String[1];
			valueParm[0] = "This OD Loan was Blocked.Not allowed to do Add Disbursement.";

			ed.add(ErrorUtil.getErrorDetail(new ErrorDetail("21005", valueParm)));
		}

		return ed;
	}

	private BigDecimal getAvailableLimit(Date fromDate, List<OverdraftScheduleDetail> overdraftSchedules) {
		BigDecimal availableLimit = BigDecimal.ZERO;
		for (OverdraftScheduleDetail overDraftSchedule : overdraftSchedules) {
			if (overDraftSchedule.getDroplineDate().compareTo(fromDate) > 0) {
				break;
			}
			availableLimit = overDraftSchedule.getODLimit();
		}

		return availableLimit;
	}

	private BigDecimal getClosingBalance(Date fromDate, List<FinanceScheduleDetail> schedules) {
		BigDecimal closingbal = BigDecimal.ZERO;
		for (FinanceScheduleDetail schdule : schedules) {
			if (DateUtil.compare(schdule.getSchDate(), fromDate) > 0) {
				break;
			}
			closingbal = schdule.getClosingBalance();
		}
		return closingbal;
	}

	private boolean isPaid(FinanceScheduleDetail curSchd) {
		if ((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0) {
			return false;
		}

		if ((curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())).compareTo(BigDecimal.ZERO) > 0) {
			return false;
		}

		if ((curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())).compareTo(BigDecimal.ZERO) > 0) {
			return false;
		}

		return true;
	}

	private boolean isPaid(FinODDetails overdue) {
		BigDecimal penaltyBalance = CalculationUtil.getPenaltyBalance(overdue);
		return penaltyBalance.compareTo(BigDecimal.ZERO) <= 0;
	}

	private boolean isChargesPaid(long finID, Date schdDate) {
		BigDecimal adviseBal = manualAdviseService.getBalanceAmt(finID, schdDate);

		return adviseBal.compareTo(BigDecimal.ZERO) <= 0;
	}

	private void createTransactionCharge(OverdraftDTO fm, BigDecimal feeAmount) {
		logger.debug(Literal.ENTERING);

		ManualAdvise ma = new ManualAdvise();

		ma.setAdviseID(manualAdviseService.getNewAdviseID());
		ma.setFinReference(fm.getFinReference());
		ma.setAdviseType(AdviseType.RECEIVABLE.id());
		ma.setAdviseAmount(feeAmount);
		ma.setBalanceAmt(feeAmount);

		Date nextRepayDate = fm.getNextSchdDate();

		FeeType feeType = null;

		if (fm.getAppDate() == null) {
			feeType = fm.getTxnChrFeeType();

			ma.setFinSource("ADD_DISB");
			ma.setValueDate(nextRepayDate);
			ma.setPostDate(nextRepayDate);
			ma.setDueDate(nextRepayDate);
		} else {
			feeType = fm.getColChrFeeType();

			ma.setFinSource("COL_EOD");
			ma.setValueDate(fm.getAppDate());
			ma.setPostDate(fm.getAppDate());
			ma.setDueDate(null);
		}

		ma.setFeeTypeID(feeType.getFeeTypeID());
		ma.setFeeTypeCode(feeType.getFeeTypeCode());
		ma.setFeeType(feeType);

		ma.setVersion(1);
		ma.setLastMntBy(fm.getLastMntBy());
		ma.setLastMntOn(fm.getLastMntOn());
		ma.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		ma.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		ma.setNewRecord(true);
		ma.setUserDetails(fm.getUserDetails());

		manualAdviseService.doApprove(getAuditHeader(ma, PennantConstants.TRAN_WF));

		logger.debug(Literal.LEAVING);
	}

	private BigDecimal getMonthlyLimit(FinanceMain fm) {
		EventProperties ep = fm.getEventProperties();

		int monthlyPer = 0;
		if (ep.isParameterLoaded()) {
			monthlyPer = ep.getOverDraftMonthlyLimit();
		} else {
			monthlyPer = SysParamUtil.getValueAsInt(SMTParameterConstants.OVERDRAFT_LOANS_MONTHLY_LIMIT);
		}

		return fm.getFinAssetValue().multiply(BigDecimal.valueOf(monthlyPer)).divide(new BigDecimal(100));
	}

	private AuditHeader getAuditHeader(ManualAdvise aManualAdvise, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aManualAdvise.getBefImage(), aManualAdvise);
		return new AuditHeader(String.valueOf(aManualAdvise.getAdviseID()), String.valueOf(aManualAdvise.getAdviseID()),
				null, null, auditDetail, aManualAdvise.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	@Override
	public OverdraftLimit getLimit(long finID) {
		return overdraftLimitDAO.getLimit(finID);
	}

	@Override
	public List<OverdraftLimitTransation> getTransactions(long finID) {
		return overdraftLimitDAO.getTransactions(finID);
	}

	@Override
	public boolean isLimitBlock(long finID) {
		return overdraftLimitDAO.isLimitBlock(finID);
	}

	@Override
	public boolean isAutoBlock(long finID) {
		return overdraftLimitDAO.isAutoBlock(finID);
	}

	@Override
	public void unBlockLimit(long finID) {
		overdraftLimitDAO.unBlockLimit(finID);
	}

	@Override
	public long getOverdraftTxnChrgFeeType(String finType) {
		return overdraftLoanDAO.getOverdraftTxnChrgFeeType(finType);
	}

	@Override
	public OverdraftLimit getLimitByReference(long finID, String type) {
		return overdraftLimitDAO.getLimit(finID, type);
	}

	@Override
	public void createCharges(List<PresentmentDetail> presentments) {
		logger.debug(Literal.ENTERING);

		Date appDate = SysParamUtil.getAppDate();

		List<PresentmentCharge> savePresentments = new ArrayList<>();
		List<ManualAdvise> maList = new ArrayList<>();
		List<FinODDetails> lpiList = new ArrayList<>();
		List<FinODDetails> lppList = new ArrayList<>();

		String bounceTaxComponent = feeTypeDAO.getTaxCompByCode(Allocation.BOUNCE);

		for (PresentmentDetail pd : presentments) {

			pd.setAppDate(appDate);

			chargesCalculation(pd, bounceTaxComponent);

			if (CollectionUtils.isEmpty(pd.getPresentmentCharges())) {
				continue;
			}

			long detailId = pd.getId();
			overdraftPresentmentDAO.updateCharges(detailId, pd.getCharges());

			for (PresentmentCharge pc : pd.getPresentmentCharges()) {
				pc.setPresenmentID(detailId);

				savePresentments.add(pc);
				if (savePresentments.size() >= 500) {
					overdraftPresentmentDAO.savePresentmentCharge(savePresentments, TableType.MAIN_TAB);
					savePresentments.clear();
				}

				// Updating presentmentDetail Id in Manual Advises and
				// FinODDetails
				if (pc.getAdviseId() > 0) {
					ManualAdvise ma = new ManualAdvise();
					ma.setAdviseID(pc.getAdviseId());
					ma.setPresentmentID(detailId);
					maList.add(ma);

					if (maList.size() >= 500) {
						overdraftPresentmentDAO.update(maList);
						maList.clear();
					}

				} else {
					String finReference = pd.getFinReference();

					FinODDetails od = new FinODDetails();
					od.setFinReference(finReference);
					od.setPresentmentID(detailId);

					if (RepayConstants.FEE_TYPE_LPP.equals(pc.getFeeType())) {
						lppList.add(od);
					} else if (RepayConstants.FEE_TYPE_LPI.equals(pc.getFeeType())) {
						lpiList.add(od);
					}

					if (lpiList.size() >= 500) {
						overdraftPresentmentDAO.update(lpiList, RepayConstants.FEE_TYPE_LPI);
						lpiList.clear();
					}

					if (lppList.size() >= 500) {
						overdraftPresentmentDAO.update(lppList, RepayConstants.FEE_TYPE_LPP);
						lppList.clear();
					}
				}
			}
		}

		if (CollectionUtils.isNotEmpty(savePresentments)) {
			overdraftPresentmentDAO.savePresentmentCharge(savePresentments, TableType.MAIN_TAB);
			savePresentments.clear();
		}

		if (CollectionUtils.isNotEmpty(maList)) {
			overdraftPresentmentDAO.update(maList);
			maList.clear();
		}

		if (CollectionUtils.isNotEmpty(lpiList)) {
			overdraftPresentmentDAO.update(lpiList, RepayConstants.FEE_TYPE_LPI);
			lpiList.clear();
		}

		if (CollectionUtils.isNotEmpty(lppList)) {
			overdraftPresentmentDAO.update(lppList, RepayConstants.FEE_TYPE_LPP);
			lppList.clear();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void cancelCharges(long presentmetID) {
		overdraftPresentmentDAO.cancelManualAdvise(presentmetID);
		overdraftPresentmentDAO.cancelODDetails(presentmetID);
		overdraftPresentmentDAO.cancelPresentmentCharges(presentmetID);
	}

	private void chargesCalculation(PresentmentDetail pd, String bounceTaxComponent) {
		logger.debug(Literal.ENTERING);

		long finID = pd.getFinID();

		List<ManualAdvise> advises = manualAdviseDAO.getReceivableAdvises(finID, "_AView");

		if (CollectionUtils.isEmpty(advises)) {
			pd.setPresentmentCharges(new ArrayList<>());
			pd.setCharges(BigDecimal.ZERO);

			logger.debug(Literal.LEAVING);
			return;
		}

		BigDecimal totalCharges = BigDecimal.ZERO;

		List<PresentmentCharge> charges = new ArrayList<>();

		FinanceMain overDraft = overdraftLoanDAO.getLoanDetails(finID);

		long overdraftTxnChrgFeeType = overDraft.getOverdraftTxnChrgFeeType();

		Map<String, BigDecimal> taxPercmap = GSTCalculator.getTaxPercentages(finID);

		int seqNo = 1;
		for (ManualAdvise advise : advises) {
			if (pd.getAppDate().compareTo(advise.getDueDate()) <= 0) {

				if (overdraftTxnChrgFeeType != advise.getFeeTypeID()) {
					continue;
				}

				BigDecimal adviseBal = BigDecimal.ZERO;
				adviseBal = adviseBal.add(advise.getAdviseAmount());
				adviseBal = adviseBal.subtract(advise.getPaidAmount());
				adviseBal = adviseBal.subtract(advise.getWaivedAmount());

				if (adviseBal.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				String taxComponent = null;
				if (advise.getBounceID() == 0) {
					taxComponent = advise.getTaxComponent();
				} else {
					taxComponent = bounceTaxComponent;
				}

				TaxAmountSplit tax = GSTCalculator.calculateGST(taxPercmap, taxComponent, adviseBal);

				BigDecimal paidGST = CalculationUtil.getTotalPaidGST(advise);
				BigDecimal waiedGST = CalculationUtil.getTotalWaivedGST(advise);
				BigDecimal gstAmount = tax.gettGST().subtract(paidGST).subtract(waiedGST);

				BigDecimal actFeeAmount = adviseBal;

				BigDecimal feeAmount = adviseBal;

				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
					feeAmount = adviseBal.add(gstAmount);
				}

				totalCharges = totalCharges.add(feeAmount);

				PresentmentCharge pc = new PresentmentCharge();
				if (advise.getBounceID() == 0) {
					pc.setFeeType(RepayConstants.FEE_TYPE_MANUAL_ADVISE);
				} else {
					pc.setFeeType(RepayConstants.FEE_TYPE_BOUNCE);
				}

				pc.setCgstAmount(tax.getcGST().subtract(advise.getPaidCGST()).subtract(advise.getWaivedCGST()));
				pc.setSgstAmount(tax.getsGST().subtract(advise.getPaidSGST()).subtract(advise.getWaivedSGST()));
				pc.setUgstAmount(tax.getuGST().subtract(advise.getPaidUGST()).subtract(advise.getWaivedUGST()));
				pc.setIgstAmount(tax.getiGST().subtract(advise.getPaidIGST()).subtract(advise.getWaivedIGST()));
				pc.setCessAmount(tax.getCess().subtract(advise.getPaidCESS()).subtract(advise.getWaivedCESS()));

				pc.setActualFeeAmount(actFeeAmount);
				pc.setFeeAmount(feeAmount);
				pc.setSeqNo(seqNo++);
				pc.setAdviseId(advise.getAdviseID());

				charges.add(pc);
			}
		}

		pd.setPresentmentCharges(charges);
		pd.setCharges(totalCharges);

		logger.debug(Literal.LEAVING);
	}

	private Long getFeeTypeID(final Long adviseId, Map<Long, Long> feeTypes) {
		return feeTypes.computeIfAbsent(adviseId, abc -> getFeeTypeID(adviseId));
	}

	private Long getFeeTypeID(Long adviseId) {
		return manualAdviseDAO.getFeeTypeId(adviseId);
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	@Autowired
	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	@Autowired
	public void setOverdraftPresentmentDAO(OverdraftPresentmentDAO overdraftPresentmentDAO) {
		this.overdraftPresentmentDAO = overdraftPresentmentDAO;
	}

	@Autowired
	public void setOverdraftLoanDAO(OverdraftLoanDAO overdraftLoanDAO) {
		this.overdraftLoanDAO = overdraftLoanDAO;
	}

	@Autowired
	public void setOverdraftLimitDAO(OverdraftLimitDAO overdraftLimitDAO) {
		this.overdraftLimitDAO = overdraftLimitDAO;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	@Autowired
	public void setLoanPaymentService(LoanPaymentService loanPaymentService) {
		this.loanPaymentService = loanPaymentService;
	}

}
