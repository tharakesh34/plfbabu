package com.pennant.backend.service.payorderissue.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.FinAutoApprovalProcess;
import com.pennant.app.core.InstBasedSchdProcess;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAutoApprovalDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InstBasedSchdDetails;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.process.DisbursementProcess;
import com.pennanttech.pff.external.disbursement.dao.DisbursementDAO;
import com.pennanttech.pff.logging.dao.FinAutoApprovalDetailDAO;
import com.pennanttech.pff.logging.dao.InstBasedSchdDetailDAO;

public class DisbursementProcessImpl implements DisbursementProcess {
	private static Logger logger = LogManager.getLogger(DisbursementProcessImpl.class);

	private BeneficiaryDAO beneficiaryDAO;
	private FinanceMainDAO financeMainDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private DisbursementDAO disbursementRequestDAO;
	private FinAutoApprovalDetailDAO finAutoApprovalDetailDAO;
	private FinAutoApprovalProcess finAutoApprovalProcess;
	private InstBasedSchdProcess instBasedSchdProcess;
	private InstBasedSchdDetailDAO instBasedSchdDetailDAO;

	private PlatformTransactionManager transactionManager;

	private static String paidStatus = "E";
	private static boolean customStatusLoaded = false;
	private static String realizedStatus = "P";
	private static String printStatus = "PR";

	@Override
	public int processDisbursement(FinanceMain fm, FinAdvancePayments fap) {
		logger.info(Literal.ENTERING);

		loadDefaultStatus();

		String status = fap.getClearingStatus();

		boolean statusFlag = true;
		if (paidStatus.equals(status)) {
			fap.setStatus(DisbursementConstants.STATUS_PAID);
			if (ImplementationConstants.HOLD_DISB_INST_POST) {
				postDisbInsTransactions(fap, fm);
			}
		} else if (realizedStatus.equals(status)) {
			fap.setStatus(DisbursementConstants.STATUS_REALIZED);
		} else if (printStatus.equals(status)) {
			fap.setStatus(DisbursementConstants.STATUS_PRINT);
		} else {
			if (!ImplementationConstants.HOLD_DISB_INST_POST) {
				postingsPreparationUtil.postReversalsByLinkedTranID(fap.getLinkedTranId());
			}

			fap.setStatus(DisbursementConstants.STATUS_REJECTED);
			statusFlag = false;
		}

		addToCustomerBeneficiary(fap, fm.getCustID());

		fap.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		if (statusFlag && fm.isInstBasedSchd()) {
			processInstBasedSchd(fap);
		}

		if (statusFlag && fm.isQuickDisb()) {
			executeQDP(fap);
		}

		logger.info(Literal.LEAVING);
		return finAdvancePaymentsDAO.updateDisbursmentStatus(fap);
	}

	private void loadDefaultStatus() {
		if (customStatusLoaded) {
			return;
		}

		String disbStatus = SysParamUtil.getValueAsString(SMTParameterConstants.DISB_PAID_STATUS);
		if (StringUtils.isNotBlank(disbStatus)) {
			paidStatus = disbStatus;
		}
		customStatusLoaded = true;
	}

	private void executeQDP(FinAdvancePayments fap) {
		logger.info(Literal.ENTERING);

		FinAutoApprovalDetails fad = new FinAutoApprovalDetails();
		fad.setFinID(fap.getFinID());
		fad.setFinReference(fap.getFinReference());
		fad.setBatchId(fap.getDisbResponseBatchId());
		fad.setDisbId(fap.getPaymentId());
		fad.setRealizedDate(fap.getRealizationDate());
		fad.setStatus(DisbursementConstants.AUTODISB_STATUS_PENDING);
		fad.setUserId(fap.getUserId());
		fad.setDownloadedOn(fap.getDownloadedon());

		if (fad.getRealizedDate() == null) {
			fad.setRealizedDate(fap.getClearingDate());
		}

		finAutoApprovalDetailDAO.delete(fad);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		TransactionStatus txStatus = this.transactionManager.getTransaction(txDef);

		try {
			finAutoApprovalDetailDAO.save(fad);
			this.transactionManager.commit(txStatus);
		} catch (Exception e) {
			this.transactionManager.rollback(txStatus);
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			finAutoApprovalProcess.process(fad);
			if (!DisbursementConstants.AUTODISB_STATUS_SUCCESS.equals(fad.getStatus())) {
				throw new AppException(fad.getErrorDesc());
			}

		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			txStatus = this.transactionManager.getTransaction(txDef);
			try {
				finAutoApprovalDetailDAO.update(fad);
				this.transactionManager.commit(txStatus);
			} catch (Exception e) {
				this.transactionManager.rollback(txStatus);
				logger.error(Literal.EXCEPTION, e);
			}
		}

		logger.info(Literal.LEAVING);
	}

	private void processInstBasedSchd(FinAdvancePayments fap) {
		logger.info(Literal.ENTERING);

		InstBasedSchdDetails instBasedSchd = new InstBasedSchdDetails();
		instBasedSchd.setFinReference(fap.getFinReference());
		instBasedSchd.setBatchId(fap.getDisbResponseBatchId());
		instBasedSchd.setDisbId(fap.getPaymentId());
		instBasedSchd.setRealizedDate(fap.getRealizationDate());
		instBasedSchd.setDisbAmount(fap.getAmtToBeReleased());
		instBasedSchd.setStatus(DisbursementConstants.AUTODISB_STATUS_PENDING);
		instBasedSchd.setUserId(fap.getUserId());
		instBasedSchd.setDownloadedOn(fap.getDownloadedon());
		instBasedSchd.setLinkedTranId(fap.getLinkedTranId());

		if (instBasedSchd.getRealizedDate() == null) {
			instBasedSchd.setRealizedDate(fap.getClearingDate());
		}

		instBasedSchdDetailDAO.delete(instBasedSchd);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		TransactionStatus txStatus = this.transactionManager.getTransaction(txDef);

		try {
			instBasedSchdDetailDAO.save(instBasedSchd);
			this.transactionManager.commit(txStatus);
		} catch (Exception e) {
			this.transactionManager.rollback(txStatus);
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			instBasedSchdProcess.process(instBasedSchd);
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			txStatus = this.transactionManager.getTransaction(txDef);
			try {
				instBasedSchdDetailDAO.update(instBasedSchd);
				this.transactionManager.commit(txStatus);
			} catch (Exception e) {
				this.transactionManager.rollback(txStatus);
				logger.error(Literal.EXCEPTION, e);
			}
		}

		logger.info(Literal.LEAVING);
	}

	private void postDisbInsTransactions(FinAdvancePayments disbursement, FinanceMain fm) {
		logger.info(Literal.ENTERING);
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.getFinScheduleData().setFinanceMain(fm);

		List<FinAdvancePayments> list = new ArrayList<>();
		list.add(disbursement);
		financeDetail.setAdvancePaymentsList(list);

		AccountingEngine.post(AccountingEvent.DISBINS, financeDetail, fm.getFinBranch());

		for (FinAdvancePayments advPayment : list) {
			finAdvancePaymentsDAO.updateLinkedTranId(advPayment);
		}
		logger.info(Literal.LEAVING);
	}

	@Override
	public void process(FinanceMain fm, FinAdvancePayments fap) {
		logger.info(Literal.ENTERING);

		TransactionStatus txStatus = null;

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		txStatus = this.transactionManager.getTransaction(txDef);
		int count = 0;

		try {
			count = processDisbursement(fm, fap);
		} catch (Exception e) {
			this.transactionManager.rollback(txStatus);
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

		if (count == 0) {
			transactionManager.rollback(txStatus);
		} else {
			this.transactionManager.commit(txStatus);
		}

		logger.info(Literal.LEAVING);
	}

	public void addToCustomerBeneficiary(FinAdvancePayments finAdvPay, long cusID) {
		logger.info(Literal.ENTERING);
		String paymentType = finAdvPay.getPaymentType();

		if (!(DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentType)
				|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(paymentType)
				|| DisbursementConstants.PAYMENT_TYPE_RTGS.equals(paymentType)
				|| DisbursementConstants.PAYMENT_TYPE_IFT.equals(paymentType))) {

			return;
		}

		String beneficiaryAccNo = finAdvPay.getBeneficiaryAccNo();
		long bankBranchID = finAdvPay.getBankBranchID();

		int count = beneficiaryDAO.getBeneficiaryByBankBranchId(beneficiaryAccNo, bankBranchID, "_View");

		if (count == 0) {
			Beneficiary beneficiary = new Beneficiary();
			beneficiary.setCustID(cusID);
			beneficiary.setBankBranchID(bankBranchID);
			beneficiary.setAccNumber(beneficiaryAccNo);
			beneficiary.setAccHolderName(finAdvPay.getBeneficiaryName());
			beneficiary.setPhoneCountryCode(finAdvPay.getPhoneCountryCode());
			beneficiary.setPhoneAreaCode(finAdvPay.getPhoneAreaCode());
			beneficiary.setPhoneNumber(finAdvPay.getPhoneNumber());

			try {
				beneficiaryDAO.save(beneficiary, "");
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				throw new AppException("Unable to create Beneficiary.");
			}
		}

		logger.info(Literal.LEAVING);
	}

	@Override
	public FinAdvancePayments getFinAdvancePayments(FinAdvancePayments finAdvancePayments) {
		return this.finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePayments, "");
	}

	@Override
	public FinanceMain getDisbursmentFinMainById(long finID, TableType tableType) {
		return financeMainDAO.getDisbursmentFinMainById(finID, tableType);
	}

	@Override
	public List<PaymentInstruction> getPaymentInstructionsByRespBatchId(long respBatchId) {
		return disbursementRequestDAO.getPaymentInstructionsByRespBatchId(respBatchId);
	}

	@Override
	public List<FinAdvancePayments> getDisbRequestsByRespBatchId(long respBatchId) {
		return disbursementRequestDAO.getDisbRequestsByRespBatchId(respBatchId);
	}

	public void setBeneficiaryDAO(BeneficiaryDAO beneficiaryDAO) {
		this.beneficiaryDAO = beneficiaryDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public void setDisbursementRequestDAO(DisbursementDAO disbursementRequestDAO) {
		this.disbursementRequestDAO = disbursementRequestDAO;
	}

	public void setFinAutoApprovalDetailDAO(FinAutoApprovalDetailDAO finAutoApprovalDetailDAO) {
		this.finAutoApprovalDetailDAO = finAutoApprovalDetailDAO;
	}

	public void setFinAutoApprovalProcess(FinAutoApprovalProcess finAutoApprovalProcess) {
		this.finAutoApprovalProcess = finAutoApprovalProcess;
	}

	public void setInstBasedSchdProcess(InstBasedSchdProcess instBasedSchdProcess) {
		this.instBasedSchdProcess = instBasedSchdProcess;
	}

	public void setInstBasedSchdDetailDAO(InstBasedSchdDetailDAO instBasedSchdDetailDAO) {
		this.instBasedSchdDetailDAO = instBasedSchdDetailDAO;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
