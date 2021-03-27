package com.pennant.backend.service.payorderissue.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.process.DisbursementProcess;

public class DisbursementProcessImpl implements DisbursementProcess {
	private static Logger logger = LogManager.getLogger(DisbursementProcessImpl.class);

	private BeneficiaryDAO beneficiaryDAO;
	private FinanceMainDAO financeMainDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private DisbursementPostings disbursementPostings;
	private PlatformTransactionManager transactionManager;

	private static String PAID_STATUS = "E";
	private static String REALIZED_STATUS = "P";

	@Override
	public int processDisbursement(FinAdvancePayments disbursement) {
		int count = 0;

		try {
			//FIXME:Shinde - Need to check the Impact Rendering the data from View.
			String finReference = disbursement.getFinReference();
			FinanceMain fm = financeMainDAO.getDisbursmentFinMainById(finReference, TableType.VIEW);
			String paymentType = disbursement.getPaymentType();

			if (StringUtils.equals(PAID_STATUS, disbursement.getStatus())) {
				disbursement.setStatus(DisbursementConstants.STATUS_PAID);
				// Postings entry
				if (SysParamUtil.isAllowed(SMTParameterConstants.HOLD_DISB_INST_POST)) {
					fm.setLovDescEntityCode(financeMainDAO.getLovDescEntityCode(finReference, "_View"));
					FinanceDetail financeDetail = new FinanceDetail();

					List<FinAdvancePayments> list = new ArrayList<>();
					list.add(disbursement);
					financeDetail.setAdvancePaymentsList(list);

					Map<Integer, Long> map = disbursementPostings.prepareDisbPostingApproval(list, fm,
							fm.getFinBranch());

					// loop through the disbursements.
					for (FinAdvancePayments advPayment : list) {
						if (map.containsKey(advPayment.getPaymentSeq())) {
							advPayment.setLinkedTranId(map.get(advPayment.getPaymentSeq()));
							finAdvancePaymentsDAO.updateLinkedTranId(advPayment);
						}
					}
				}
			} else if (StringUtils.equals(REALIZED_STATUS, disbursement.getStatus())) {
				disbursement.setStatus(DisbursementConstants.STATUS_REALIZED);
			} else {
				if (!PennantConstants.YES.equalsIgnoreCase(SMTParameterConstants.HOLD_DISB_INST_POST)) {
					postingsPreparationUtil.postReversalsByLinkedTranID(disbursement.getLinkedTranId());
				}

				/*
				 * Bug On 19-08-2017 with the mail subject Issue in Posting after Disbursement cancellation AEEvent
				 * aeEvent = new AEEvent(); aeEvent.setLinkedTranId(disbursement.getLinkedTranId());
				 * aeEvent.setReturnDataSet(list); aeEvent = postingsPreparationUtil.processPostings(aeEvent);
				 */

				disbursement.setStatus(DisbursementConstants.STATUS_REJECTED);
			}

			if (DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_RTGS.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_IFT.equals(paymentType)) {
				addToCustomerBeneficiary(disbursement, fm.getCustID());
			}

			// update paid or rejected
			count = finAdvancePaymentsDAO.updateDisbursmentStatus(disbursement);
		} catch (Exception e) {
			throw e;
		}

		return count;
	}

	@Override
	public void process(FinAdvancePayments disbursement) {
		logger.debug(Literal.ENTERING);

		TransactionStatus txStatus = null;

		String disbStatus = SysParamUtil.getValueAsString(SMTParameterConstants.DISB_PAID_STATUS);
		if (StringUtils.isNotBlank(disbStatus)) {
			PAID_STATUS = disbStatus;
		}

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		txStatus = this.transactionManager.getTransaction(txDef);
		int count = 0;

		try {
			count = processDisbursement(disbursement);
		} catch (Exception e) {
			this.transactionManager.rollback(txStatus);
			logger.error(Literal.EXCEPTION, e);
		}

		if (count == 0) {
			transactionManager.rollback(txStatus);
		} else {
			this.transactionManager.commit(txStatus);
		}

		logger.debug(Literal.LEAVING);
	}

	public void addToCustomerBeneficiary(FinAdvancePayments finAdvPay, long cusID) {
		int count = beneficiaryDAO.getBeneficiaryByBankBranchId(finAdvPay.getBeneficiaryAccNo(),
				finAdvPay.getBankBranchID(), "_View");
		if (count == 0) {
			Beneficiary beneficiary = new Beneficiary();
			beneficiary.setCustID(cusID);
			beneficiary.setBankBranchID(finAdvPay.getBankBranchID());
			beneficiary.setAccNumber(finAdvPay.getBeneficiaryAccNo());
			beneficiary.setAccHolderName(finAdvPay.getBeneficiaryName());
			beneficiary.setPhoneCountryCode(finAdvPay.getPhoneCountryCode());
			beneficiary.setPhoneAreaCode(finAdvPay.getPhoneAreaCode());
			beneficiary.setPhoneNumber(finAdvPay.getPhoneNumber());
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

		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setPaymentId(paymentId);
		finAdvancePayments = this.finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePayments, "");

		if (DisbursementConstants.STATUS_PAID.equals(status)) {
			finAdvancePayments.setStatus("E");
		} else {
			finAdvancePayments.setStatus("R");
		}
		finAdvancePayments.setRejectReason(rejectReason);
		finAdvancePayments.setTransactionRef(tranReference);

		process(finAdvancePayments);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public FinAdvancePayments getFinAdvancePayments(FinAdvancePayments finAdvancePayments) {
		return this.finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePayments, "");
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

	public void setDisbursementPostings(DisbursementPostings disbursementPostings) {
		this.disbursementPostings = disbursementPostings;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
