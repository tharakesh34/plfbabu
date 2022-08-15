package com.pennant.backend.dao.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.service.payment.PaymentDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.process.PaymentProcess;

public class PaymentProcessImpl implements PaymentProcess {
	private static Logger logger = LogManager.getLogger(PaymentProcessImpl.class);

	private FinanceMainDAO financeMainDAO;
	private BeneficiaryDAO beneficiaryDAO;
	private PaymentDetailService paymentDetailService;
	private PostingsPreparationUtil postingsPreparationUtil;
	private PlatformTransactionManager transactionManager;
	private static String PAID_STATUS = "E";
	private static String REALIZED_STATUS = "P";

	@Override
	public int processPayment(PaymentInstruction pi) {
		int count = 0;

		try {
			String disbStatus = SysParamUtil.getValueAsString(SMTParameterConstants.DISB_PAID_STATUS);
			String finReference = pi.getFinReference();
			FinanceMain fm = financeMainDAO.getDisbursmentFinMainById(finReference, TableType.MAIN_TAB);

			if (StringUtils.isNotBlank(disbStatus)) {
				PAID_STATUS = disbStatus;
			}
			// In case of IMD or excess amount processing there is a chance of LAN in temp queue.
			if (fm == null) {
				fm = financeMainDAO.getDisbursmentFinMainById(pi.getFinReference(), TableType.TEMP_TAB);
			}

			String paymentType = pi.getPaymentType();

			// E -> PAID
			String status = pi.getClearingStatus();
			if (PAID_STATUS.equals(status) || DisbursementConstants.STATUS_PAID.equals(status)) {
				pi.setStatus(DisbursementConstants.STATUS_PAID);
			} else if (REALIZED_STATUS.equals(status) || DisbursementConstants.STATUS_REALIZED.equals(status)) {
				pi.setStatus(DisbursementConstants.STATUS_REALIZED);
			} else {
				paymentDetailService.paymentReversal(pi);
				postingsPreparationUtil.postReversalsByLinkedTranID(pi.getLinkedTranId());
				pi.setStatus(DisbursementConstants.STATUS_REJECTED);
			}

			if (DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_RTGS.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_IFT.equals(paymentType)) {
				addToCustomerBeneficiary(pi, fm.getCustID());
			}

			// update paid or rejected
			count = paymentDetailService.updatePaymentStatus(pi);

		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	@Override
	public void process(PaymentInstruction paymentInstruction) {
		logger.debug(Literal.ENTERING);

		TransactionStatus txStatus = null;
		try {
			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			txStatus = this.transactionManager.getTransaction(txDef);

			int count = processPayment(paymentInstruction);
			if (count == 0 || count > 1) {
				transactionManager.rollback(txStatus);
			} else {
				this.transactionManager.commit(txStatus);
			}
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void addToCustomerBeneficiary(PaymentInstruction instruction, long cusID) {
		int count = beneficiaryDAO.getBeneficiaryByBankBranchId(instruction.getAccountNo(),
				instruction.getBankBranchId(), "_View");
		if (count == 0) {
			Beneficiary beneficiary = new Beneficiary();
			beneficiary.setCustID(cusID);
			beneficiary.setBankBranchID(instruction.getBankBranchId());
			beneficiary.setAccNumber(instruction.getAccountNo());
			beneficiary.setAccHolderName(instruction.getAcctHolderName());
			beneficiary.setPhoneCountryCode(instruction.getPhoneCountryCode());
			beneficiary.setPhoneAreaCode(instruction.getPhoneCountryCode());
			beneficiary.setPhoneNumber(instruction.getPhoneNumber());// FIXME for 3numbners
			beneficiaryDAO.save(beneficiary, "");
		}
	}

	@Override
	public void updateStatus(Object... params) {
		logger.debug(Literal.ENTERING);

		long paymentId = (Long) params[0];
		String status = (String) params[1];
		String rejectReason = (String) params[2];
		String tranReference = (String) params[3];

		PaymentInstruction instruction = new PaymentInstruction();
		instruction.setPaymentInstructionId(paymentId);

		instruction = this.paymentDetailService.getPaymentInstruction(paymentId, "");

		if (instruction == null) {
			instruction = this.paymentDetailService.getPaymentInstructionDetails(paymentId, "");
		}

		if (DisbursementConstants.STATUS_PAID.equals(status)) {
			instruction.setStatus("E");
		} else {
			instruction.setStatus("R");
		}
		instruction.setRejectReason(rejectReason);
		instruction.setTransactionRef(tranReference);

		process(instruction);

		logger.debug(Literal.LEAVING);
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setBeneficiaryDAO(BeneficiaryDAO beneficiaryDAO) {
		this.beneficiaryDAO = beneficiaryDAO;
	}

	public void setPaymentDetailService(PaymentDetailService paymentDetailService) {
		this.paymentDetailService = paymentDetailService;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
