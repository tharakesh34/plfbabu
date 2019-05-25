package com.pennant.backend.model.FinRepayQueue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class FinRepayQueueHeader {

	private BigDecimal principal = BigDecimal.ZERO;
	private BigDecimal profit = BigDecimal.ZERO;
	private BigDecimal tds = BigDecimal.ZERO;
	private BigDecimal futPrincipal = BigDecimal.ZERO;
	private BigDecimal futProfit = BigDecimal.ZERO;
	private BigDecimal futTds = BigDecimal.ZERO;
	private BigDecimal lateProfit = BigDecimal.ZERO;
	private BigDecimal penalty = BigDecimal.ZERO;
	private BigDecimal fee = BigDecimal.ZERO;
	private BigDecimal insurance = BigDecimal.ZERO;
	private BigDecimal suplRent = BigDecimal.ZERO;
	private BigDecimal incrCost = BigDecimal.ZERO;

	private BigDecimal partialPaid = BigDecimal.ZERO;

	private BigDecimal priWaived = BigDecimal.ZERO;
	private BigDecimal pftWaived = BigDecimal.ZERO;
	private BigDecimal futPriWaived = BigDecimal.ZERO;
	private BigDecimal futPftWaived = BigDecimal.ZERO;
	private BigDecimal latePftWaived = BigDecimal.ZERO;
	private BigDecimal penaltyWaived = BigDecimal.ZERO;
	private BigDecimal feeWaived = BigDecimal.ZERO;
	private BigDecimal insWaived = BigDecimal.ZERO;
	private BigDecimal suplRentWaived = BigDecimal.ZERO;
	private BigDecimal incrCostWaived = BigDecimal.ZERO;

	private BigDecimal manAdvPaid = BigDecimal.ZERO;
	private BigDecimal manAdvWaived = BigDecimal.ZERO;

	private BigDecimal paidPenaltyCGST = BigDecimal.ZERO;
	private BigDecimal paidPenaltySGST = BigDecimal.ZERO;
	private BigDecimal paidPenaltyUGST = BigDecimal.ZERO;
	private BigDecimal paidPenaltyIGST = BigDecimal.ZERO;

	private String payType;
	private String postBranch;
	private String cashierBranch;
	private String partnerBankAc;
	private String partnerBankAcType;
	private boolean pftChgAccReq;
	private long receiptId;
	private boolean cashTranExecuted = false;
	private boolean stageAccExecuted = false;

	private Map<String, BigDecimal> extDataMap = null;
	private Map<String, Object> gstExecutionMap = null;
	private List<FinRepayQueue> queueList = null;

	public FinRepayQueueHeader() {

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getLateProfit() {
		return lateProfit;
	}

	public void setLateProfit(BigDecimal lateProfit) {
		this.lateProfit = lateProfit;
	}

	public BigDecimal getPenalty() {
		return penalty;
	}

	public void setPenalty(BigDecimal penalty) {
		this.penalty = penalty;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getInsurance() {
		return insurance;
	}

	public void setInsurance(BigDecimal insurance) {
		this.insurance = insurance;
	}

	public BigDecimal getSuplRent() {
		return suplRent;
	}

	public void setSuplRent(BigDecimal suplRent) {
		this.suplRent = suplRent;
	}

	public BigDecimal getIncrCost() {
		return incrCost;
	}

	public void setIncrCost(BigDecimal incrCost) {
		this.incrCost = incrCost;
	}

	public BigDecimal getPriWaived() {
		return priWaived;
	}

	public void setPriWaived(BigDecimal priWaived) {
		this.priWaived = priWaived;
	}

	public BigDecimal getPftWaived() {
		return pftWaived;
	}

	public void setPftWaived(BigDecimal pftWaived) {
		this.pftWaived = pftWaived;
	}

	public BigDecimal getLatePftWaived() {
		return latePftWaived;
	}

	public void setLatePftWaived(BigDecimal latePftWaived) {
		this.latePftWaived = latePftWaived;
	}

	public BigDecimal getPenaltyWaived() {
		return penaltyWaived;
	}

	public void setPenaltyWaived(BigDecimal penaltyWaived) {
		this.penaltyWaived = penaltyWaived;
	}

	public BigDecimal getFeeWaived() {
		return feeWaived;
	}

	public void setFeeWaived(BigDecimal feeWaived) {
		this.feeWaived = feeWaived;
	}

	public BigDecimal getInsWaived() {
		return insWaived;
	}

	public void setInsWaived(BigDecimal insWaived) {
		this.insWaived = insWaived;
	}

	public BigDecimal getSuplRentWaived() {
		return suplRentWaived;
	}

	public void setSuplRentWaived(BigDecimal suplRentWaived) {
		this.suplRentWaived = suplRentWaived;
	}

	public BigDecimal getIncrCostWaived() {
		return incrCostWaived;
	}

	public void setIncrCostWaived(BigDecimal incrCostWaived) {
		this.incrCostWaived = incrCostWaived;
	}

	public List<FinRepayQueue> getQueueList() {
		return queueList;
	}

	public void setQueueList(List<FinRepayQueue> queueList) {
		this.queueList = queueList;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPostBranch() {
		return postBranch;
	}

	public void setPostBranch(String postBranch) {
		this.postBranch = postBranch;
	}

	public String getPartnerBankAc() {
		return partnerBankAc;
	}

	public void setPartnerBankAc(String partnerBankAc) {
		this.partnerBankAc = partnerBankAc;
	}

	public String getPartnerBankAcType() {
		return partnerBankAcType;
	}

	public void setPartnerBankAcType(String partnerBankAcType) {
		this.partnerBankAcType = partnerBankAcType;
	}

	public BigDecimal getTds() {
		return tds;
	}

	public void setTds(BigDecimal tds) {
		this.tds = tds;
	}

	public boolean isPftChgAccReq() {
		return pftChgAccReq;
	}

	public void setPftChgAccReq(boolean pftChgAccReq) {
		this.pftChgAccReq = pftChgAccReq;
	}

	public Map<String, BigDecimal> getExtDataMap() {
		return extDataMap;
	}

	public void setExtDataMap(Map<String, BigDecimal> extDataMap) {
		this.extDataMap = extDataMap;
	}

	public BigDecimal getManAdvPaid() {
		return manAdvPaid;
	}

	public void setManAdvPaid(BigDecimal manAdvPaid) {
		this.manAdvPaid = manAdvPaid;
	}

	public BigDecimal getManAdvWaived() {
		return manAdvWaived;
	}

	public void setManAdvWaived(BigDecimal manAdvWaived) {
		this.manAdvWaived = manAdvWaived;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public String getCashierBranch() {
		return cashierBranch;
	}

	public void setCashierBranch(String cashierBranch) {
		this.cashierBranch = cashierBranch;
	}

	public boolean isStageAccExecuted() {
		return stageAccExecuted;
	}

	public void setStageAccExecuted(boolean stageAccExecuted) {
		this.stageAccExecuted = stageAccExecuted;
	}

	public BigDecimal getPartialPaid() {
		return partialPaid;
	}

	public void setPartialPaid(BigDecimal partialPaid) {
		this.partialPaid = partialPaid;
	}

	public BigDecimal getFutPrincipal() {
		return futPrincipal;
	}

	public void setFutPrincipal(BigDecimal futPrincipal) {
		this.futPrincipal = futPrincipal;
	}

	public BigDecimal getFutProfit() {
		return futProfit;
	}

	public void setFutProfit(BigDecimal futProfit) {
		this.futProfit = futProfit;
	}

	public BigDecimal getFutTds() {
		return futTds;
	}

	public void setFutTds(BigDecimal futTds) {
		this.futTds = futTds;
	}

	public BigDecimal getFutPriWaived() {
		return futPriWaived;
	}

	public void setFutPriWaived(BigDecimal futPriWaived) {
		this.futPriWaived = futPriWaived;
	}

	public BigDecimal getFutPftWaived() {
		return futPftWaived;
	}

	public void setFutPftWaived(BigDecimal futPftWaived) {
		this.futPftWaived = futPftWaived;
	}

	public Map<String, Object> getGstExecutionMap() {
		return gstExecutionMap;
	}

	public void setGstExecutionMap(Map<String, Object> gstExecutionMap) {
		this.gstExecutionMap = gstExecutionMap;
	}

	public BigDecimal getPaidPenaltyCGST() {
		return paidPenaltyCGST;
	}

	public void setPaidPenaltyCGST(BigDecimal paidPenaltyCGST) {
		this.paidPenaltyCGST = paidPenaltyCGST;
	}

	public BigDecimal getPaidPenaltySGST() {
		return paidPenaltySGST;
	}

	public void setPaidPenaltySGST(BigDecimal paidPenaltySGST) {
		this.paidPenaltySGST = paidPenaltySGST;
	}

	public BigDecimal getPaidPenaltyUGST() {
		return paidPenaltyUGST;
	}

	public void setPaidPenaltyUGST(BigDecimal paidPenaltyUGST) {
		this.paidPenaltyUGST = paidPenaltyUGST;
	}

	public BigDecimal getPaidPenaltyIGST() {
		return paidPenaltyIGST;
	}

	public void setPaidPenaltyIGST(BigDecimal paidPenaltyIGST) {
		this.paidPenaltyIGST = paidPenaltyIGST;
	}

}