package com.pennant.backend.model.Repayments;

import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.Entity;

public class FinanceRepayments implements java.io.Serializable, Entity {

	private static final long serialVersionUID = -2866729395743867717L;

	private String finReference;
	private Date finPostDate;
	private String finRpyFor;
	private long finPaySeq = Long.MIN_VALUE;
	private long linkedTranId;
	private BigDecimal finRpyAmount = new BigDecimal(0);
	private Date finSchdDate;
	private Date finValueDate;
	private String finBranch;
	private String finType;
	private long finCustID;
	private BigDecimal finSchdPriPaid = new BigDecimal(0);
	private BigDecimal finSchdPftPaid = new BigDecimal(0);
	private BigDecimal finTotSchdPaid = new BigDecimal(0);
	private BigDecimal finFee = new BigDecimal(0);
	private BigDecimal finWaiver = new BigDecimal(0);
	private BigDecimal finRefund = new BigDecimal(0);
	
	// not in database
	private BigDecimal lovDescSchdPriPaid = new BigDecimal(0);
	private BigDecimal lovDescSchdPftPaid = new BigDecimal(0);
	private BigDecimal RepayBal = new BigDecimal(0);
	private BigDecimal Repaypri = new BigDecimal(0);
	private BigDecimal RepayPft = new BigDecimal(0);
	private BigDecimal RepayPenal = new BigDecimal(0);
	private BigDecimal RepayWaiver = new BigDecimal(0);
	private BigDecimal RepayRefund = new BigDecimal(0);

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public long getId() {
		return this.finPaySeq;
	}

	@Override
	public void setId(long id) {
		this.finPaySeq = id;

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

}
