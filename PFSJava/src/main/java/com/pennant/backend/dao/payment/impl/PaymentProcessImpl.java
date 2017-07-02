package com.pennant.backend.dao.payment.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.payment.PaymentDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.process.PaymentProcess;

public class PaymentProcessImpl implements PaymentProcess {
	private static Logger logger = Logger.getLogger(PaymentProcessImpl.class);

	@Autowired
	private FinanceMainDAO 			financeMainDAO;
	@Autowired
	private BeneficiaryDAO 			beneficiaryDAO;
	@Autowired
	private PaymentDetailService paymentDetailService;
	@Autowired
	protected PostingsPreparationUtil	postingsPreparationUtil;
	
	@Override
	public void process(PaymentInstruction paymentInstruction) throws Exception {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = null;
		List<ReturnDataSet> list = null;
		try {
			financeMain = financeMainDAO.getDisbursmentFinMainById(paymentInstruction.getFinReference(), TableType.MAIN_TAB);
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
					|| DisbursementConstants.PAYMENT_TYPE_RTGS.equals(paymentType)) {
				addToCustomerBeneficiary(paymentInstruction, financeMain.getCustID());
			}

			// update paid or rejected
			paymentDetailService.updatePaymentStatus(paymentInstruction);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}
	
	public void addToCustomerBeneficiary(PaymentInstruction instruction, long cusID) {
		int count = beneficiaryDAO.getBeneficiaryByBankBranchId(instruction.getAccountNo(), instruction.getBankBranchId(), "_View");
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

}
