package com.pennant.backend.model.FinRepayQueue;

import java.math.BigDecimal;
import java.util.List;

public class FinRepayQueueHeader {

	private BigDecimal	principal	= BigDecimal.ZERO;
	private BigDecimal	profit		= BigDecimal.ZERO;
	private BigDecimal	tds			= BigDecimal.ZERO;
	private BigDecimal	lateProfit	= BigDecimal.ZERO;
	private BigDecimal	penalty		= BigDecimal.ZERO;
	private BigDecimal	fee			= BigDecimal.ZERO;
	private BigDecimal	insurance	= BigDecimal.ZERO;
	private BigDecimal	suplRent	= BigDecimal.ZERO;
	private BigDecimal	incrCost	= BigDecimal.ZERO;
	
	private BigDecimal	priWaived		= BigDecimal.ZERO;
	private BigDecimal	pftWaived		= BigDecimal.ZERO;
	private BigDecimal	latePftWaived	= BigDecimal.ZERO;
	private BigDecimal	penaltyWaived	= BigDecimal.ZERO;
	private BigDecimal	feeWaived		= BigDecimal.ZERO;
	private BigDecimal	insWaived		= BigDecimal.ZERO;
	private BigDecimal	suplRentWaived	= BigDecimal.ZERO;
	private BigDecimal	incrCostWaived	= BigDecimal.ZERO;
	
	private String payType;
	private String postBranch;
	private String partnerBankAc;
	private String partnerBankAcType;
	private boolean pftChgAccReq;
	
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

}