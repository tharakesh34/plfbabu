package com.pennant.backend.service.payorderissue.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
//import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.exception.PFFInterfaceException;

public class DisbursementPostings {
	private Logger						logger	= Logger.getLogger(DisbursementPostings.class);

	private AccountEngineExecution		engineExecution;
	private FinAdvancePaymentsService	finAdvancePaymentsService;
	private FinanceMainService			financeMainService;
	protected AEAmountCodes				amountCodes;
	protected PostingsPreparationUtil	postingsPreparationUtil;

	public DisbursementPostings() {
		super();
	}

	public List<ReturnDataSet> getDisbPosting(PayOrderIssueHeader header) throws IllegalAccessException,
			InvocationTargetException, PFFInterfaceException {
		Map<Long, List<ReturnDataSet>> map = prepareDisbPosting(header);
		List<ReturnDataSet> datasetList = new ArrayList<ReturnDataSet>();
		for (Entry<Long, List<ReturnDataSet>> entry : map.entrySet()) {
			datasetList.addAll(entry.getValue());
		}
		return datasetList;

	}

	private Map<Long, List<ReturnDataSet>> prepareDisbPosting(PayOrderIssueHeader header)
			throws IllegalAccessException, InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering");

		String finRef = header.getFinReference();
		HashMap<String, Object> executingMap = new HashMap<String, Object>();

		Map<Long, List<ReturnDataSet>> disbMap = new HashMap<>();

		List<FinAdvancePayments> advPaymentsList = header.getFinAdvancePaymentsList();
		List<FinAdvancePayments> approvedList = finAdvancePaymentsService.getFinAdvancePaymentsById(finRef, "");

		if (advPaymentsList != null && !advPaymentsList.isEmpty()) {

			for (FinAdvancePayments finAdvancePayments : advPaymentsList) {

				executingMap.put("ae_disbInstAmt", DateUtility.getSysDate());
				executingMap.put("ValueDate", DateUtility.getSysDate());
				executingMap.put("fm_finCcy", SysParamUtil.getAppCurrency());
				executingMap.put("fm_finBranch", PennantConstants.IBD_Branch);
				executingMap.put("fm_finReference", finRef);
				executingMap.put("ae_finEvent", AccountEventConstants.ACCEVENT_DISBINS);
				executingMap.put("ft_finType", header.getFinanceMain().getFinType());
				executingMap.put("ae_disbInstAmt", finAdvancePayments.getAmtToBeReleased());
				executingMap.put("disb_partnerBank", finAdvancePayments.getAcType());
				executingMap.put("fm_custID", header.getFinanceMain().getCustID());
				executingMap.put("ae_finEvent", AccountEventConstants.ACCEVENT_DISBINS);

				FinAdvancePayments finApprovedPay = isApproved(approvedList, finAdvancePayments.getPaymentId());

				if (finApprovedPay != null
						&& StringUtils.equals(finApprovedPay.getStatus(), finAdvancePayments.getStatus())
						&& !StringUtils.equals(PennantConstants.RCD_DEL, finAdvancePayments.getRecordType())) {
					continue;
				}

				if (StringUtils.equals(PennantConstants.RCD_DEL, finAdvancePayments.getRecordType())) {
					//Reverse postings cancel case.
					long linkedTranId = finApprovedPay.getLinkedTranId();
					List<ReturnDataSet> datasetList=engineExecution.cancelPostings(linkedTranId);
					disbMap.put(finAdvancePayments.getPaymentId(), datasetList);

				} else {
					//Prepare posting for  new added 
					List<ReturnDataSet> datasetList = new ArrayList<>();
					datasetList = engineExecution.getAccEngineExecResults("N", executingMap, false);
					disbMap.put(finAdvancePayments.getPaymentId(), datasetList);

				}

			}

		}
		logger.debug("Leaving");
		return disbMap;
	}

	public List<Object> processPostings(PayOrderIssueHeader issueHeader, String tranType) {
		logger.debug(" Entering ");
		//get Accounting set and prepared return data set
		List<Object> returnList = new ArrayList<Object>();
		try {
			String errorMessage;
			Map<Long, List<ReturnDataSet>> disbMap  = prepareDisbPosting(issueHeader);

			if (disbMap != null) {
				for (Entry<Long, List<ReturnDataSet>> entry : disbMap.entrySet()) {
					returnList = postingsPreparationUtil.processPostings(entry.getValue());
					if (returnList != null) {
						if ((!(Boolean) returnList.get(0))) {
							errorMessage = StringUtils.trimToEmpty(returnList.get(1).toString());
							returnList.add(false);
							returnList.add(errorMessage);
						} else {
							finAdvancePaymentsService.Update(entry.getKey(), Long.valueOf(returnList
									.get(2).toString()));
							
						}
					}
				}

			} else {
				returnList.add(false);
				returnList.add("Accounting not defined");
				return returnList;
			}

		} catch (Exception e) {
			logger.debug(e);
			returnList.add(false);
			returnList.add(e.getMessage());
		}
		logger.debug(" Leaving ");
		return returnList;

	}

	private FinAdvancePayments isApproved(List<FinAdvancePayments> advPayment, long paymentID) {
		if (advPayment == null || advPayment.isEmpty()) {
			return null;
		}
		for (FinAdvancePayments finAdvancePayments : advPayment) {
			if (finAdvancePayments.getPaymentId() == paymentID) {
				return finAdvancePayments;
			}
		}
		return null;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public FinAdvancePaymentsService getFinAdvancePaymentsService() {
		return finAdvancePaymentsService;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public AEAmountCodes getAmountCodes() {
		return amountCodes;
	}

	public void setAmountCodes(AEAmountCodes amountCodes) {
		this.amountCodes = amountCodes;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

}
