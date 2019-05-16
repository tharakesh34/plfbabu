package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FinRepayHeader implements Serializable {
	private static final long serialVersionUID = 1L;

	private long repayID = 0;
	private long receiptSeqID = 0;
	private String finReference;
	private Date valueDate;
	private String finEvent;
	private BigDecimal repayAmount = BigDecimal.ZERO;
	private BigDecimal priAmount = BigDecimal.ZERO;
	private BigDecimal pftAmount = BigDecimal.ZERO;
	private BigDecimal latePftAmount = BigDecimal.ZERO;
	private BigDecimal totalPenalty = BigDecimal.ZERO;
	private BigDecimal totalRefund = BigDecimal.ZERO;
	private BigDecimal totalWaiver = BigDecimal.ZERO;
	private BigDecimal insRefund = BigDecimal.ZERO;
	private String repayAccountId;
	private String earlyPayEffMtd;
	private Date earlyPayDate;
	private boolean schdRegenerated;
	private long linkedTranId = 0;
	private BigDecimal totalIns = BigDecimal.ZERO;
	private BigDecimal totalSuplRent = BigDecimal.ZERO;
	private BigDecimal totalIncrCost = BigDecimal.ZERO;
	private BigDecimal totalSchdFee = BigDecimal.ZERO;
	private String payApportionment;
	private BigDecimal realizeUnAmz = BigDecimal.ZERO;
	private BigDecimal cpzChg = BigDecimal.ZERO;
	private BigDecimal adviseAmount = BigDecimal.ZERO;
	private BigDecimal feeAmount = BigDecimal.ZERO;
	private BigDecimal excessAmount = BigDecimal.ZERO;
	private BigDecimal tdsAmount = BigDecimal.ZERO;
	private BigDecimal lpiAmount = BigDecimal.ZERO;
	private List<RepayScheduleDetail> repayScheduleDetails = new ArrayList<>(1);

	public FinRepayHeader() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public BigDecimal getPriAmount() {
		return priAmount;
	}

	public void setPriAmount(BigDecimal priAmount) {
		this.priAmount = priAmount;
	}

	public BigDecimal getPftAmount() {
		return pftAmount;
	}

	public void setPftAmount(BigDecimal pftAmount) {
		this.pftAmount = pftAmount;
	}

	public BigDecimal getTotalRefund() {
		return totalRefund;
	}

	public void setTotalRefund(BigDecimal totalRefund) {
		this.totalRefund = totalRefund;
	}

	public BigDecimal getTotalWaiver() {
		return totalWaiver;
	}

	public void setTotalWaiver(BigDecimal totalWaiver) {
		this.totalWaiver = totalWaiver;
	}

	public BigDecimal getInsRefund() {
		return insRefund;
	}

	public void setInsRefund(BigDecimal insRefund) {
		this.insRefund = insRefund;
	}

	public String getRepayAccountId() {
		return repayAccountId;
	}

	public void setRepayAccountId(String repayAccountId) {
		this.repayAccountId = repayAccountId;
	}

	public String getEarlyPayEffMtd() {
		return earlyPayEffMtd;
	}

	public void setEarlyPayEffMtd(String earlyPayEffMtd) {
		this.earlyPayEffMtd = earlyPayEffMtd;
	}

	public Date getEarlyPayDate() {
		return earlyPayDate;
	}

	public void setEarlyPayDate(Date earlyPayDate) {
		this.earlyPayDate = earlyPayDate;
	}

	public boolean isSchdRegenerated() {
		return schdRegenerated;
	}

	public void setSchdRegenerated(boolean schdRegenerated) {
		this.schdRegenerated = schdRegenerated;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public BigDecimal getTotalIns() {
		return totalIns;
	}

	public void setTotalIns(BigDecimal totalIns) {
		this.totalIns = totalIns;
	}

	public BigDecimal getTotalSuplRent() {
		return totalSuplRent;
	}

	public void setTotalSuplRent(BigDecimal totalSuplRent) {
		this.totalSuplRent = totalSuplRent;
	}

	public BigDecimal getTotalIncrCost() {
		return totalIncrCost;
	}

	public void setTotalIncrCost(BigDecimal totalIncrCost) {
		this.totalIncrCost = totalIncrCost;
	}

	public BigDecimal getTotalSchdFee() {
		return totalSchdFee;
	}

	public void setTotalSchdFee(BigDecimal totalSchdFee) {
		this.totalSchdFee = totalSchdFee;
	}

	public String getPayApportionment() {
		return payApportionment;
	}

	public void setPayApportionment(String payApportionment) {
		this.payApportionment = payApportionment;
	}

	public long getRepayID() {
		return repayID;
	}

	public void setRepayID(long repayID) {
		this.repayID = repayID;
	}

	public List<RepayScheduleDetail> getRepayScheduleDetails() {
		return repayScheduleDetails;
	}

	public void setRepayScheduleDetails(List<RepayScheduleDetail> repayScheduleDetails) {
		this.repayScheduleDetails = repayScheduleDetails;
	}

	public long getReceiptSeqID() {
		return receiptSeqID;
	}

	public void setReceiptSeqID(long receiptSeqID) {
		this.receiptSeqID = receiptSeqID;
	}

	public BigDecimal getLatePftAmount() {
		return latePftAmount;
	}

	public void setLatePftAmount(BigDecimal latePftAmount) {
		this.latePftAmount = latePftAmount;
	}

	public BigDecimal getTotalPenalty() {
		return totalPenalty;
	}

	public void setTotalPenalty(BigDecimal totalPenalty) {
		this.totalPenalty = totalPenalty;
	}

	public BigDecimal getCpzChg() {
		return cpzChg;
	}

	public void setCpzChg(BigDecimal cpzChg) {
		this.cpzChg = cpzChg;
	}

	public BigDecimal getRealizeUnAmz() {
		return realizeUnAmz;
	}

	public void setRealizeUnAmz(BigDecimal realizeUnAmz) {
		this.realizeUnAmz = realizeUnAmz;
	}

	public BigDecimal getAdviseAmount() {
		return adviseAmount;
	}

	public void setAdviseAmount(BigDecimal adviseAmount) {
		this.adviseAmount = adviseAmount;
	}

	public BigDecimal getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}

	public BigDecimal getExcessAmount() {
		return excessAmount;
	}

	public void setExcessAmount(BigDecimal excessAmount) {
		this.excessAmount = excessAmount;
	}

	public BigDecimal getTdsAmount() {
		return tdsAmount;
	}

	public void setTdsAmount(BigDecimal tdsAmount) {
		this.tdsAmount = tdsAmount;
	}

	public BigDecimal getLpiAmount() {
		return lpiAmount;
	}

	public void setLpiAmount(BigDecimal lpiAmount) {
		this.lpiAmount = lpiAmount;
	}

}
