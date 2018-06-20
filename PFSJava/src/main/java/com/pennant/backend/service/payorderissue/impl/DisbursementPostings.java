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
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
//import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;

public class DisbursementPostings {
	private Logger logger = Logger.getLogger(DisbursementPostings.class);

	private AccountEngineExecution engineExecution;
	private FinAdvancePaymentsService finAdvancePaymentsService;
	private FinanceMainService financeMainService;
	protected AEAmountCodes amountCodes;
	protected PostingsPreparationUtil postingsPreparationUtil;

	public DisbursementPostings() {
		super();
	}

	public List<ReturnDataSet> getDisbPosting(List<FinAdvancePayments> advPaymentsList,  FinanceMain finMain ) throws IllegalAccessException,
			InvocationTargetException, InterfaceException {
		Map<Long, List<ReturnDataSet>> map = prepareDisbPosting(advPaymentsList, finMain, null);
		List<ReturnDataSet> datasetList = new ArrayList<ReturnDataSet>();
		for (Entry<Long, List<ReturnDataSet>> entry : map.entrySet()) {
			datasetList.addAll(entry.getValue());
		}
		return datasetList;

	}

	private Map<Long, List<ReturnDataSet>> prepareDisbPosting(List<FinAdvancePayments> advPaymentsList,  FinanceMain finMain, String usrBranch)
			throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering");

		String finRef = finMain.getFinReference();

		Map<Long, List<ReturnDataSet>> disbMap = new HashMap<>();

		List<FinAdvancePayments> approvedList = finAdvancePaymentsService.getFinAdvancePaymentsById(finRef, "");

		if (advPaymentsList != null && !advPaymentsList.isEmpty()) {

			for (FinAdvancePayments finAdvancePayments : advPaymentsList) {
				FinAdvancePayments finApprovedPay = isApproved(approvedList, finAdvancePayments.getPaymentId());

				if (finApprovedPay != null
						&& StringUtils.equals(finApprovedPay.getStatus(), finAdvancePayments.getStatus())
						&& !StringUtils.equals(PennantConstants.RCD_DEL, finAdvancePayments.getRecordType())) {
					continue;
				}

				AEEvent aeEvent = new AEEvent();
				AEAmountCodes amountCodes = new AEAmountCodes();
				HashMap<String, Object> dataMap = aeEvent.getDataMap();

				aeEvent.setValueDate(finAdvancePayments.getLlDate());
				aeEvent.setCcy(finAdvancePayments.getDisbCCy());
				aeEvent.setBranch(finMain.getFinBranch());
				aeEvent.setFinReference(finRef);
				aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_DISBINS);
				aeEvent.setFinType(finMain.getFinType());
				amountCodes.setDisbInstAmt(finAdvancePayments.getAmtToBeReleased());
				amountCodes.setPartnerBankAc(finAdvancePayments.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(finAdvancePayments.getPartnerBankAcType());
				amountCodes.setFinType(aeEvent.getFinType());
				aeEvent.setCustID(finMain.getCustID());
				aeEvent.setValueDate(finAdvancePayments.getLlDate());
				aeEvent.setPostingUserBranch(usrBranch);

				if (StringUtils.equals(PennantConstants.RCD_DEL, finAdvancePayments.getRecordType())) {
					// Reverse postings cancel case.
					long linkedTranId = finApprovedPay.getLinkedTranId();
					List<ReturnDataSet> datasetList = postingsPreparationUtil.getReversalsByLinkedTranID(linkedTranId);
					disbMap.put(finAdvancePayments.getPaymentId(), datasetList);

				} else {
					// Prepare posting for new added
					List<ReturnDataSet> datasetList = new ArrayList<>();

					dataMap = amountCodes.getDeclaredFieldValues();
					aeEvent.setDataMap(dataMap);
					if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
						aeEvent.getAcSetIDList().add(
								AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(),
										aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_PROMOTION));
					} else {
						aeEvent.getAcSetIDList().add(
								AccountingConfigCache.getAccountSetID(aeEvent.getFinType(),
										aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_FINTYPE));
					}
					aeEvent.setLinkedTranId(0);
					aeEvent.setEntityCode(finMain.getLovDescEntityCode());
					aeEvent = engineExecution.getAccEngineExecResults(aeEvent);

					datasetList = aeEvent.getReturnDataSet();
					disbMap.put(finAdvancePayments.getPaymentId(), datasetList);

				}
			}
		}
		logger.debug("Leaving");
		return disbMap;
	}
	
	
	
	public Map<Long, Long> prepareDisbPostingApproval(List<FinAdvancePayments> advPaymentsList, FinanceMain finMain, String usrBranch)  throws InterfaceException {
		logger.debug("Entering");

		String finRef = finMain.getFinReference();

		Map<Long,Long> disbMap = new HashMap<>();

		List<FinAdvancePayments> approvedList = finAdvancePaymentsService.getFinAdvancePaymentsById(finRef, "");

		if (advPaymentsList != null && !advPaymentsList.isEmpty()) {

			for (FinAdvancePayments finAdvancePayments : advPaymentsList) {
				FinAdvancePayments finApprovedPay = isApproved(approvedList, finAdvancePayments.getPaymentId());

				if (finApprovedPay != null
						&& StringUtils.equals(finApprovedPay.getStatus(), finAdvancePayments.getStatus())
						&& !StringUtils.equals(PennantConstants.RCD_DEL, finAdvancePayments.getRecordType())) {
					continue;
				}


				if (StringUtils.equals(PennantConstants.RCD_DEL, finAdvancePayments.getRecordType())) {
					// Reverse postings cancel case.
					long linkedTranId = finApprovedPay.getLinkedTranId();
					postingsPreparationUtil.postReversalsByLinkedTranID(linkedTranId);

				} else {
					AEEvent aeEvent = new AEEvent();
					AEAmountCodes amountCodes = new AEAmountCodes();
					HashMap<String, Object> dataMap = aeEvent.getDataMap();

					aeEvent.setValueDate(finAdvancePayments.getLlDate());
					aeEvent.setCcy(finAdvancePayments.getDisbCCy());
					aeEvent.setBranch(finMain.getFinBranch());
					aeEvent.setFinReference(finRef);
					aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_DISBINS);
					aeEvent.setFinType(finMain.getFinType());
					amountCodes.setDisbInstAmt(finAdvancePayments.getAmtToBeReleased());
					amountCodes.setPartnerBankAc(finAdvancePayments.getPartnerBankAc());
					amountCodes.setPartnerBankAcType(finAdvancePayments.getPartnerBankAcType());
					amountCodes.setFinType(aeEvent.getFinType());
					aeEvent.setCustID(finMain.getCustID());
					aeEvent.setValueDate(finAdvancePayments.getLlDate());
					aeEvent.setPostingUserBranch(usrBranch);
					aeEvent.setEntityCode(finMain.getLovDescEntityCode());
					// Prepare posting for new added
					boolean posted=true;
					dataMap = amountCodes.getDeclaredFieldValues();
					aeEvent.setDataMap(dataMap);
					if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
						aeEvent.getAcSetIDList().add(
								AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(),
										aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_PROMOTION));
					} else {
						aeEvent.getAcSetIDList().add(
								AccountingConfigCache.getAccountSetID(aeEvent.getFinType(),
										aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_FINTYPE));
					}
					aeEvent.setLinkedTranId(0);
					try {
						aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
					} catch (Exception e) {
						posted=false;
					}
					if (!aeEvent.isPostingSucess()) {
						posted=false;
					}

					if (!posted) {
						disbMap.put(finAdvancePayments.getPaymentId(), Long.MIN_VALUE);//To Identify
					}else{
						disbMap.put(finAdvancePayments.getPaymentId(), aeEvent.getLinkedTranId());
					}
					

				}
			}
		}
		logger.debug("Leaving");
		return disbMap;
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
