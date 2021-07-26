package com.pennant.backend.service.payorderissue.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.AccountingEvent;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;

@Deprecated
public class DisbursementPostings {
	private Logger logger = LogManager.getLogger(DisbursementPostings.class);

	private AccountEngineExecution engineExecution;
	private FinAdvancePaymentsService finAdvancePaymentsService;
	private PostingsPreparationUtil postingsPreparationUtil;
	protected AEAmountCodes amountCodes;

	public DisbursementPostings() {
		super();
	}

	public List<ReturnDataSet> getDisbPosting(List<FinAdvancePayments> advPaymentsList, FinanceMain finMain)
			throws IllegalAccessException, InvocationTargetException, InterfaceException {
		Map<Long, List<ReturnDataSet>> map = prepareDisbPosting(advPaymentsList, finMain, null);
		List<ReturnDataSet> datasetList = new ArrayList<ReturnDataSet>();
		for (Entry<Long, List<ReturnDataSet>> entry : map.entrySet()) {
			datasetList.addAll(entry.getValue());
		}
		return datasetList;
	}

	private Map<Long, List<ReturnDataSet>> prepareDisbPosting(List<FinAdvancePayments> advPaymentsList,
			FinanceMain finMain, String usrBranch)
			throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug(Literal.ENTERING);

		String finRef = finMain.getFinReference();

		Map<Long, List<ReturnDataSet>> disbMap = new HashMap<>();

		List<FinAdvancePayments> approvedList = finAdvancePaymentsService.getFinAdvancePaymentsById(finRef, "");

		if (CollectionUtils.isEmpty(approvedList)) {
			return disbMap;
		}

		for (FinAdvancePayments finAdvancePayments : advPaymentsList) {
			FinAdvancePayments finApprovedPay = isApproved(approvedList, finAdvancePayments.getPaymentId());

			if (finApprovedPay != null && StringUtils.equals(finApprovedPay.getStatus(), finAdvancePayments.getStatus())
					&& !StringUtils.equals(PennantConstants.RCD_DEL, finAdvancePayments.getRecordType())) {
				continue;
			}

			AEEvent aeEvent = new AEEvent();
			AEAmountCodes amountCodes = new AEAmountCodes();
			Map<String, Object> dataMap = aeEvent.getDataMap();

			aeEvent.setValueDate(finAdvancePayments.getLlDate());
			aeEvent.setCcy(finAdvancePayments.getDisbCCy());
			aeEvent.setBranch(finMain.getFinBranch());
			aeEvent.setFinReference(finRef);
			aeEvent.setFinType(finMain.getFinType());
			if (StringUtils.equals(finAdvancePayments.getPaymentDetail(), DisbursementConstants.PAYMENT_DETAIL_VAS)) {
				amountCodes.setVasInstAmt(finAdvancePayments.getAmtToBeReleased());
				aeEvent.setAccountingEvent(AccountingEvent.INSPAY);
				//For GL Code
				dataMap.put("ae_productCode", finAdvancePayments.getProductShortCode());
				dataMap.put("ae_dealerCode", finAdvancePayments.getDealerShortCode());
				dataMap.put("id_totPayAmount", finAdvancePayments.getAmtToBeReleased());
			} else {
				aeEvent.setAccountingEvent(AccountingEvent.DISBINS);
				amountCodes.setDisbInstAmt(finAdvancePayments.getAmtToBeReleased());
			}
			amountCodes.setIntTdsAdjusted(finMain.getIntTdsAdjusted());
			amountCodes.setPartnerBankAc(finAdvancePayments.getPartnerBankAc());
			amountCodes.setPartnerBankAcType(finAdvancePayments.getPartnerBankAcType());
			amountCodes.setFinType(aeEvent.getFinType());
			aeEvent.setCustID(finMain.getCustID());
			aeEvent.setValueDate(finAdvancePayments.getLlDate());
			aeEvent.setPostingUserBranch(usrBranch);

			if (PennantConstants.RCD_DEL.equals(finAdvancePayments.getRecordType())) {
				// Reverse postings cancel case.
				long linkedTranId = finApprovedPay.getLinkedTranId();
				List<ReturnDataSet> datasetList = postingsPreparationUtil.getReversalsByLinkedTranID(linkedTranId);
				disbMap.put(finAdvancePayments.getPaymentId(), datasetList);
			} else {
				// Prepare posting for new added
				List<ReturnDataSet> datasetList = new ArrayList<>();

				dataMap = amountCodes.getDeclaredFieldValues();
				aeEvent.setDataMap(dataMap);

				dataMap.putAll(amountCodes.getDeclaredFieldValues());
				aeEvent.setDataMap(dataMap);
				if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
					aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(),
							aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_PROMOTION));
				} else {
					aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(aeEvent.getFinType(),
							aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_FINTYPE));
				}
				aeEvent.setLinkedTranId(0);
				aeEvent.setEntityCode(finMain.getLovDescEntityCode());
				engineExecution.getAccEngineExecResults(aeEvent);

				datasetList = aeEvent.getReturnDataSet();
				disbMap.put(finAdvancePayments.getPaymentId(), datasetList);

			}
		}

		logger.debug(Literal.LEAVING);
		return disbMap;
	}

	public Map<Integer, Long> prepareDisbPostingApproval(List<FinAdvancePayments> advPaymentsList, FinanceMain finMain,
			String usrBranch) throws InterfaceException {
		logger.debug("Entering");

		String finRef = finMain.getFinReference();

		Map<Integer, Long> disbMap = new HashMap<>();

		List<FinAdvancePayments> approvedList = finAdvancePaymentsService.getFinAdvancePaymentsById(finRef, "_AView");

		if (advPaymentsList != null && !advPaymentsList.isEmpty()) {

			for (FinAdvancePayments finAdvancePayments : advPaymentsList) {
				FinAdvancePayments finApprovedPay = isApproved(approvedList, finAdvancePayments.getPaymentId());

				if (finApprovedPay != null
						&& StringUtils.equals(finApprovedPay.getStatus(), finAdvancePayments.getStatus())
						&& !StringUtils.equals(PennantConstants.RCD_DEL, finAdvancePayments.getRecordType())
						&& !finAdvancePayments.isPostingQdp()) {
					continue;
				}

				if (finApprovedPay != null) {
					if (StringUtils.isBlank(finAdvancePayments.getPartnerBankAc())) {
						finAdvancePayments.setPartnerBankAc(finApprovedPay.getPartnerBankAc());
					}

					if (StringUtils.isBlank(finAdvancePayments.getPartnerBankAcType())) {
						finAdvancePayments.setPartnerBankAcType(finApprovedPay.getPartnerBankAcType());
					}

					if (StringUtils.isBlank(finAdvancePayments.getDisbCCy())) {
						finAdvancePayments.setDisbCCy(finApprovedPay.getDisbCCy());
					}

					if (finAdvancePayments.getLlDate() == null) {
						finAdvancePayments.setLLDate(finApprovedPay.getLlDate());
					}
				}

				if (StringUtils.equals(PennantConstants.RCD_DEL, finAdvancePayments.getRecordType())
						|| StringUtils.equals(DisbursementConstants.STATUS_REVERSED, finAdvancePayments.getStatus())) {
					// Reverse postings cancel case.
					long linkedTranId = finApprovedPay.getLinkedTranId();
					postingsPreparationUtil.postReversalsByLinkedTranID(linkedTranId);

				} else {
					AEEvent aeEvent = new AEEvent();
					AEAmountCodes amountCodes = new AEAmountCodes();
					Map<String, Object> dataMap = aeEvent.getDataMap();

					aeEvent.setValueDate(finAdvancePayments.getLlDate());
					aeEvent.setCcy(finAdvancePayments.getDisbCCy());
					aeEvent.setBranch(finMain.getFinBranch());
					aeEvent.setFinReference(finRef);
					aeEvent.setFinType(finMain.getFinType());
					if (StringUtils.equals(finAdvancePayments.getPaymentDetail(),
							DisbursementConstants.PAYMENT_DETAIL_VAS)) {
						amountCodes.setVasInstAmt(finAdvancePayments.getAmtToBeReleased());
						aeEvent.setAccountingEvent(AccountingEvent.INSPAY);
						//For GL Code
						dataMap.put("ae_productCode", finAdvancePayments.getProductShortCode());
						dataMap.put("ae_dealerCode", finAdvancePayments.getDealerShortCode());
						dataMap.put("id_totPayAmount", finAdvancePayments.getAmtToBeReleased());

					} else {
						amountCodes.setDisbInstAmt(finAdvancePayments.getAmtToBeReleased());
						aeEvent.setAccountingEvent(AccountingEvent.DISBINS);
					}
					amountCodes.setIntTdsAdjusted(finMain.getIntTdsAdjusted());
					amountCodes.setPartnerBankAc(finAdvancePayments.getPartnerBankAc());
					amountCodes.setPartnerBankAcType(finAdvancePayments.getPartnerBankAcType());
					amountCodes.setPaymentType(finAdvancePayments.getPaymentType());
					amountCodes.setFinType(aeEvent.getFinType());
					aeEvent.setCustID(finMain.getCustID());
					aeEvent.setPostingUserBranch(usrBranch);
					aeEvent.setEntityCode(finMain.getLovDescEntityCode());
					// Prepare posting for new added
					boolean posted = true;
					dataMap.putAll(amountCodes.getDeclaredFieldValues());
					aeEvent.setDataMap(dataMap);
					if (StringUtils.isNotBlank(finMain.getPromotionCode()) && finMain.getPromotionSeqId() == 0) {
						aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(),
								aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_PROMOTION));
					} else {
						aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(aeEvent.getFinType(),
								aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_FINTYPE));
					}
					aeEvent.setLinkedTranId(0);
					try {
						aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
					} catch (Exception e) {
						posted = false;
					}
					if (!aeEvent.isPostingSucess()) {
						posted = false;
					}

					if (!posted) {
						disbMap.put(finAdvancePayments.getPaymentSeq(), Long.MIN_VALUE);//To Identify
					} else {
						disbMap.put(finAdvancePayments.getPaymentSeq(), aeEvent.getLinkedTranId());
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

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

}
