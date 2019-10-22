package com.pennant.backend.dao.payment.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.insurance.InsuranceDetailService;
import com.pennant.backend.service.payment.PaymentDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.process.PaymentProcess;

public class PaymentProcessImpl implements PaymentProcess {
	private static Logger logger = Logger.getLogger(PaymentProcessImpl.class);

	@Autowired
	private FinanceMainDAO financeMainDAO;
	@Autowired
	private BeneficiaryDAO beneficiaryDAO;
	@Autowired
	private PaymentDetailService paymentDetailService;
	@Autowired
	protected PostingsPreparationUtil postingsPreparationUtil;
	@Autowired
	protected InsuranceDetailService insuranceDetailService;

	@Override
	public void process(PaymentInstruction paymentInstruction) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = null;
		List<ReturnDataSet> list = null;
		try {
			financeMain = financeMainDAO.getDisbursmentFinMainById(paymentInstruction.getFinReference(),
					TableType.MAIN_TAB);
			String paymentType = paymentInstruction.getPaymentType();

			if (StringUtils.equals("E", paymentInstruction.getStatus())) {
				paymentInstruction.setStatus(DisbursementConstants.STATUS_PAID);
			} else {
				paymentDetailService.paymentReversal(paymentInstruction);
				AEEvent aeEvent = new AEEvent();
				aeEvent.setLinkedTranId(paymentInstruction.getLinkedTranId());
				list = postingsPreparationUtil.postReversalsByLinkedTranID(paymentInstruction.getLinkedTranId());
				aeEvent.setReturnDataSet(list);
				aeEvent = postingsPreparationUtil.processPostings(aeEvent);

				paymentInstruction.setStatus(DisbursementConstants.STATUS_REJECTED);
			}

			if (DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_RTGS.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_IFT.equals(paymentType)) {
				addToCustomerBeneficiary(paymentInstruction, financeMain.getCustID());
			}

			// update paid or rejected
			paymentDetailService.updatePaymentStatus(paymentInstruction);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	//Processing the Insurance payments 
	@Override
	public void processInsPayments(InsurancePaymentInstructions instruction) {
		logger.debug(Literal.ENTERING);

		try {
			String paymentType = instruction.getPaymentType();

			if (StringUtils.equals("E", instruction.getStatus())) {
				instruction.setStatus(DisbursementConstants.STATUS_PAID);
				if (SysParamUtil.isAllowed(SMTParameterConstants.HOLD_INS_INST_POST)) {
					insuranceDetailService.executeVasPaymentsAccountingProcess(instruction);
				}
			} else if (StringUtils.equals("P", instruction.getStatus())) {
				instruction.setStatus(DisbursementConstants.STATUS_REALIZED);
				if (SysParamUtil.isAllowed(SMTParameterConstants.HOLD_INS_INST_POST)) {
					insuranceDetailService.executeVasPaymentsAccountingProcess(instruction);
				}
			} else {
				if (!SysParamUtil.isAllowed(SMTParameterConstants.HOLD_INS_INST_POST)) {
					AEEvent aeEvent = new AEEvent();
					aeEvent.setLinkedTranId(instruction.getLinkedTranId());
					List<ReturnDataSet> list = postingsPreparationUtil
							.postReversalsByLinkedTranID(instruction.getLinkedTranId());
					aeEvent.setReturnDataSet(list);
					aeEvent = postingsPreparationUtil.processPostings(aeEvent);
				}
				instruction.setStatus(DisbursementConstants.STATUS_REJECTED);
			}
			// addToCustomerBeneficiary(instruction, financeMain.getCustID()); FIXME to check the benficiary  adding required
			if (DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_RTGS.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_IFT.equals(paymentType)) {
			}
			// update paid or rejected
			insuranceDetailService.updatePaymentStatus(instruction);
		} catch (Exception e) {
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
			beneficiary.setPhoneNumber(instruction.getPhoneNumber());//FIXME for 3numbners
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
}
