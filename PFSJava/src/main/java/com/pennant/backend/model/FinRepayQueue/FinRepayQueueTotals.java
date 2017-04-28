package com.pennant.backend.model.FinRepayQueue;

import java.math.BigDecimal;
import java.util.List;

public class FinRepayQueueTotals {

	private BigDecimal	principal	= BigDecimal.ZERO;
	private BigDecimal	profit		= BigDecimal.ZERO;
	private BigDecimal	lateProfit	= BigDecimal.ZERO;
	private BigDecimal	penalty		= BigDecimal.ZERO;
	private BigDecimal	fee			= BigDecimal.ZERO;
	private BigDecimal	insurance	= BigDecimal.ZERO;
	private BigDecimal	suplRent	= BigDecimal.ZERO;
	private BigDecimal	incrCost	= BigDecimal.ZERO;
	private BigDecimal	excess	= BigDecimal.ZERO;
	
	private List<FinRepayQueue> queueList = null;
	
	public FinRepayQueueTotals() {

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

	public List<FinRepayQueue> getQueueList() {
		return queueList;
	}
	public void setQueueList(List<FinRepayQueue> queueList) {
		this.queueList = queueList;
	}

	public BigDecimal getExcess() {
		return excess;
	}

	public void setExcess(BigDecimal excess) {
		this.excess = excess;
	}

}