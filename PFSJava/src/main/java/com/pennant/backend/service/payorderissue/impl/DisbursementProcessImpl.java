package com.pennant.backend.service.payorderissue.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.AccountEngineExecution;
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
	private static Logger logger = Logger.getLogger(DisbursementProcessImpl.class);
	@Autowired
	private BeneficiaryDAO beneficiaryDAO;
	@Autowired
	private FinanceMainDAO financeMainDAO;
	@Autowired
	private AccountEngineExecution engineExecution;
	@Autowired
	protected PostingsPreparationUtil postingsPreparationUtil;
	@Autowired
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;

	@Autowired
	private DisbursementPostings disbursementPostings;

	private static String PAID_STATUS = "E";

	@Override
	public void process(FinAdvancePayments disbursement) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = null;
		String disbStatus = SysParamUtil.getValueAsString(SMTParameterConstants.DISB_PAID_STATUS);
		if (StringUtils.isNotBlank(disbStatus)) {
			PAID_STATUS = disbStatus;
		}

		financeMain = financeMainDAO.getDisbursmentFinMainById(disbursement.getFinReference(), TableType.MAIN_TAB);
		String paymentType = disbursement.getPaymentType();

		try {
			if (StringUtils.equals(PAID_STATUS, disbursement.getStatus())) {
				disbursement.setStatus(DisbursementConstants.STATUS_PAID);
				//Postings entry
				if (SysParamUtil.isAllowed(SMTParameterConstants.HOLD_DISB_INST_POST)) {
					financeMain.setLovDescEntityCode(
							financeMainDAO.getLovDescEntityCode(disbursement.getFinReference(), "_View"));
					FinanceDetail financeDetail = new FinanceDetail();

					List<FinAdvancePayments> finAdvancePayments = new ArrayList<FinAdvancePayments>();
					finAdvancePayments.add(disbursement);
					financeDetail.setAdvancePaymentsList(finAdvancePayments);

					Map<Integer, Long> finAdvanceMap = disbursementPostings.prepareDisbPostingApproval(
							financeDetail.getAdvancePaymentsList(), financeMain, financeMain.getFinBranch());

					List<FinAdvancePayments> advPayList = financeDetail.getAdvancePaymentsList();

					// loop through the disbursements.
					if (CollectionUtils.isNotEmpty(advPayList)) {
						for (int i = 0; i < advPayList.size(); i++) {
							FinAdvancePayments advPayment = advPayList.get(i);
							if (finAdvanceMap.containsKey(advPayment.getPaymentSeq())) {
								advPayment.setLinkedTranId(finAdvanceMap.get(advPayment.getPaymentSeq()));
								finAdvancePaymentsDAO.updateLinkedTranId(advPayment);
							}
						}
					}
				}

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
				addToCustomerBeneficiary(disbursement, financeMain.getCustID());
			}

			//update paid or rejected
			finAdvancePaymentsDAO.updateDisbursmentStatus(disbursement);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
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
}
