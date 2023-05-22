package com.pennant.backend.model.Repayments;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FinanceRepayments implements Serializable {
	private static final long serialVersionUID = -2866729395743867717L;

	private long finID;
	private String finReference;
	private Date finPostDate;
	private String finRpyFor;
	private long finPaySeq = Long.MIN_VALUE;
	private long linkedTranId;
	private BigDecimal finRpyAmount = BigDecimal.ZERO;
	private Date finSchdDate;
	private Date finValueDate;
	private String finBranch;
	private String finType;
	private long finCustID;
	private long receiptId = 0L;
	private BigDecimal penaltyPaid = BigDecimal.ZERO;
	private BigDecimal penaltyWaived = BigDecimal.ZERO;
	private BigDecimal finSchdPriPaid = BigDecimal.ZERO;
	private BigDecimal finSchdPftPaid = BigDecimal.ZERO;
	private BigDecimal finSchdTdsPaid = BigDecimal.ZERO;
	private BigDecimal finTotSchdPaid = BigDecimal.ZERO;
	private BigDecimal finFee = BigDecimal.ZERO;
	private BigDecimal finWaiver = BigDecimal.ZERO;
	private BigDecimal finRefund = BigDecimal.ZERO;

	// Fee Details
	private BigDecimal schdFeePaid = BigDecimal.ZERO;

	// not in database
	private BigDecimal lovDescSchdPriPaid = BigDecimal.ZERO;
	private BigDecimal lovDescSchdPftPaid = BigDecimal.ZERO;
	private BigDecimal RepayBal = BigDecimal.ZERO;
	private BigDecimal Repaypri = BigDecimal.ZERO;
	private BigDecimal RepayPft = BigDecimal.ZERO;
	private BigDecimal RepayPenal = BigDecimal.ZERO;
	private BigDecimal RepayWaiver = BigDecimal.ZERO;
	private BigDecimal RepayRefund = BigDecimal.ZERO;

	private BigDecimal priPenaltyPaid = BigDecimal.ZERO;
	private BigDecimal pftPenaltyPaid = BigDecimal.ZERO;
	private BigDecimal priPenaltyWaived = BigDecimal.ZERO;
	private BigDecimal pftPenaltyWaived = BigDecimal.ZERO;
	private BigDecimal lpftWaived = BigDecimal.ZERO;
	private BigDecimal lpftPaid = BigDecimal.ZERO;

	// Profit waiver
	private long waiverId;

	public FinanceRepayments() {
	    super();
	}

	public FinanceRepayments copyEntity() {
		FinanceRepayments entity = new FinanceRepayments();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setFinPostDate(this.finPostDate);
		entity.setFinRpyFor(this.finRpyFor);
		entity.setFinPaySeq(this.finPaySeq);
		entity.setLinkedTranId(this.linkedTranId);
		entity.setFinRpyAmount(this.finRpyAmount);
		entity.setFinSchdDate(this.finSchdDate);
		entity.setFinValueDate(this.finValueDate);
		entity.setFinBranch(this.finBranch);
		entity.setFinType(this.finType);
		entity.setFinCustID(this.finCustID);
		entity.setReceiptId(this.receiptId);
		entity.setPenaltyPaid(this.penaltyPaid);
		entity.setPenaltyWaived(this.penaltyWaived);
		entity.setFinSchdPriPaid(this.finSchdPriPaid);
		entity.setFinSchdPftPaid(this.finSchdPftPaid);
		entity.setFinSchdTdsPaid(this.finSchdTdsPaid);
		entity.setFinTotSchdPaid(this.finTotSchdPaid);
		entity.setFinFee(this.finFee);
		entity.setFinWaiver(this.finWaiver);
		entity.setFinRefund(this.finRefund);
		entity.setSchdFeePaid(this.schdFeePaid);
		entity.setLovDescSchdPriPaid(this.lovDescSchdPriPaid);
		entity.setLovDescSchdPftPaid(this.lovDescSchdPftPaid);
		entity.setRepayBal(this.RepayBal);
		entity.setRepaypri(this.Repaypri);
		entity.setRepayPft(this.RepayPft);
		entity.setRepayPenal(this.RepayPenal);
		entity.setRepayWaiver(this.RepayWaiver);
		entity.setRepayRefund(this.RepayRefund);
		entity.setWaiverId(this.waiverId);
		return entity;
	}

	public long getId() {
		return this.finPaySeq;
	}

	public void setId(long id) {
		this.finPaySeq = id;

	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getFinPostDate() {
		return finPostDate;
	}

	public void setFinPostDate(Date finPostDate) {
		this.finPostDate = finPostDate;
	}

	public String getFinRpyFor() {
		return finRpyFor;
	}

	public void setFinRpyFor(String finRpyFor) {
		this.finRpyFor = finRpyFor;
	}

	public long getFinPaySeq() {
		return finPaySeq;
	}

	public void setFinPaySeq(long finPaySeq) {
		this.finPaySeq = finPaySeq;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public BigDecimal getFinRpyAmount() {
		return finRpyAmount;
	}

	public void setFinRpyAmount(BigDecimal finRpyAmount) {
		this.finRpyAmount = finRpyAmount;
	}

	public Date getFinSchdDate() {
		return finSchdDate;
	}

	public void setFinSchdDate(Date finSchdDate) {
		this.finSchdDate = finSchdDate;
	}

	public Date getFinValueDate() {
		return finValueDate;
	}

	public void setFinValueDate(Date finValueDate) {
		this.finValueDate = finValueDate;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public long getFinCustID() {
		return finCustID;
	}

	public void setFinCustID(long finCustID) {
		this.finCustID = finCustID;
	}

	public BigDecimal getFinSchdPriPaid() {
		return finSchdPriPaid;
	}

	public void setFinSchdPriPaid(BigDecimal finSchdPriPaid) {
		this.finSchdPriPaid = finSchdPriPaid;
	}

	public BigDecimal getFinSchdPftPaid() {
		return finSchdPftPaid;
	}

	public void setFinSchdPftPaid(BigDecimal finSchdPftPaid) {
		this.finSchdPftPaid = finSchdPftPaid;
	}

	public BigDecimal getFinTotSchdPaid() {
		return finTotSchdPaid;
	}

	public void setFinTotSchdPaid(BigDecimal finTotSchdPaid) {
		this.finTotSchdPaid = finTotSchdPaid;
	}

	public BigDecimal getFinFee() {
		return finFee;
	}

	public void setFinFee(BigDecimal finFee) {
		this.finFee = finFee;
	}

	public BigDecimal getFinWaiver() {
		return finWaiver;
	}

	public void setFinWaiver(BigDecimal finWaiver) {
		this.finWaiver = finWaiver;
	}

	public BigDecimal getFinRefund() {
		return finRefund;
	}

	public void setFinRefund(BigDecimal finRefund) {
		this.finRefund = finRefund;
	}

	public BigDecimal getLovDescSchdPriPaid() {
		return lovDescSchdPriPaid;
	}

	public void setLovDescSchdPriPaid(BigDecimal lovDescSchdPriPaid) {
		this.lovDescSchdPriPaid = lovDescSchdPriPaid;
	}

	public BigDecimal getLovDescSchdPftPaid() {
		return lovDescSchdPftPaid;
	}

	public void setLovDescSchdPftPaid(BigDecimal lovDescSchdPftPaid) {
		this.lovDescSchdPftPaid = lovDescSchdPftPaid;
	}

	public BigDecimal getRepayBal() {
		return RepayBal;
	}

	public void setRepayBal(BigDecimal repayBal) {
		RepayBal = repayBal;
	}

	public BigDecimal getRepaypri() {
		return Repaypri;
	}

	public void setRepaypri(BigDecimal repaypri) {
		Repaypri = repaypri;
	}

	public BigDecimal getRepayPft() {
		return RepayPft;
	}

	public void setRepayPft(BigDecimal repayPft) {
		RepayPft = repayPft;
	}

	public BigDecimal getRepayPenal() {
		return RepayPenal;
	}

	public void setRepayPenal(BigDecimal repayPenal) {
		RepayPenal = repayPenal;
	}

	public BigDecimal getRepayWaiver() {
		return RepayWaiver;
	}

	public void setRepayWaiver(BigDecimal repayWaiver) {
		RepayWaiver = repayWaiver;
	}

	public BigDecimal getRepayRefund() {
		return RepayRefund;
	}

	public void setRepayRefund(BigDecimal repayRefund) {
		RepayRefund = repayRefund;
	}

	public BigDecimal getSchdFeePaid() {
		return schdFeePaid;
	}

	public void setSchdFeePaid(BigDecimal schdFeePaid) {
		this.schdFeePaid = schdFeePaid;
	}

	public BigDecimal getFinSchdTdsPaid() {
		return finSchdTdsPaid;
	}

	public void setFinSchdTdsPaid(BigDecimal finSchdTdsPaid) {
		this.finSchdTdsPaid = finSchdTdsPaid;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public BigDecimal getPenaltyPaid() {
		return penaltyPaid;
	}

	public void setPenaltyPaid(BigDecimal penaltyPaid) {
		this.penaltyPaid = penaltyPaid;
	}

	public BigDecimal getPenaltyWaived() {
		return penaltyWaived;
	}

	public void setPenaltyWaived(BigDecimal penaltyWaived) {
		this.penaltyWaived = penaltyWaived;
	}

	public long getWaiverId() {
		return waiverId;
	}

	public void setWaiverId(long waiverId) {
		this.waiverId = waiverId;
	}

	public BigDecimal getPriPenaltyPaid() {
		return priPenaltyPaid;
	}

	public void setPriPenaltyPaid(BigDecimal priPenaltyPaid) {
		this.priPenaltyPaid = priPenaltyPaid;
	}

	public BigDecimal getPftPenaltyPaid() {
		return pftPenaltyPaid;
	}

	public void setPftPenaltyPaid(BigDecimal pftPenaltyPaid) {
		this.pftPenaltyPaid = pftPenaltyPaid;
	}

	public BigDecimal getPriPenaltyWaived() {
		return priPenaltyWaived;
	}

	public void setPriPenaltyWaived(BigDecimal priPenaltyWaived) {
		this.priPenaltyWaived = priPenaltyWaived;
	}

	public BigDecimal getPftPenaltyWaived() {
		return pftPenaltyWaived;
	}

	public void setPftPenaltyWaived(BigDecimal pftPenaltyWaived) {
		this.pftPenaltyWaived = pftPenaltyWaived;
	}

	public BigDecimal getLpftWaived() {
		return lpftWaived;
	}

	public void setLpftWaived(BigDecimal lpftWaived) {
		this.lpftWaived = lpftWaived;
	}

	public BigDecimal getLpftPaid() {
		return lpftPaid;
	}

	public void setLpftPaid(BigDecimal lpftPaid) {
		this.lpftPaid = lpftPaid;
	}

}
