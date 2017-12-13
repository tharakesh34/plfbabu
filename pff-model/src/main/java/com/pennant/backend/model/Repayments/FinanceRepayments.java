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
	private BigDecimal finRpyAmount = BigDecimal.ZERO;
	private Date finSchdDate;
	private Date finValueDate;
	private String finBranch;
	private String finType;
	private long finCustID;
	private BigDecimal finSchdPriPaid = BigDecimal.ZERO;
	private BigDecimal finSchdPftPaid = BigDecimal.ZERO;
	private BigDecimal finSchdTdsPaid = BigDecimal.ZERO;
	private BigDecimal finTotSchdPaid = BigDecimal.ZERO;
	private BigDecimal finFee = BigDecimal.ZERO;
	private BigDecimal finWaiver = BigDecimal.ZERO;
	private BigDecimal finRefund = BigDecimal.ZERO;
	
	//Fee Details
	private BigDecimal schdFeePaid = BigDecimal.ZERO;
	private BigDecimal schdInsPaid = BigDecimal.ZERO;
	private BigDecimal schdSuplRentPaid = BigDecimal.ZERO;
	private BigDecimal schdIncrCostPaid = BigDecimal.ZERO;
	
	// not in database
	private BigDecimal lovDescSchdPriPaid = BigDecimal.ZERO;
	private BigDecimal lovDescSchdPftPaid = BigDecimal.ZERO;
	private BigDecimal RepayBal = BigDecimal.ZERO;
	private BigDecimal Repaypri = BigDecimal.ZERO;
	private BigDecimal RepayPft = BigDecimal.ZERO;
	private BigDecimal RepayPenal = BigDecimal.ZERO;
	private BigDecimal RepayWaiver = BigDecimal.ZERO;
	private BigDecimal RepayRefund = BigDecimal.ZERO;

	public FinanceRepayments() {
		
	}
	 
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public BigDecimal getSchdInsPaid() {
		return schdInsPaid;
	}
	public void setSchdInsPaid(BigDecimal schdInsPaid) {
		this.schdInsPaid = schdInsPaid;
	}

	public BigDecimal getSchdSuplRentPaid() {
		return schdSuplRentPaid;
	}
	public void setSchdSuplRentPaid(BigDecimal schdSuplRentPaid) {
		this.schdSuplRentPaid = schdSuplRentPaid;
	}

	public BigDecimal getSchdIncrCostPaid() {
		return schdIncrCostPaid;
	}
	public void setSchdIncrCostPaid(BigDecimal schdIncrCostPaid) {
		this.schdIncrCostPaid = schdIncrCostPaid;
	}

	public BigDecimal getFinSchdTdsPaid() {
		return finSchdTdsPaid;
	}

	public void setFinSchdTdsPaid(BigDecimal finSchdTdsPaid) {
		this.finSchdTdsPaid = finSchdTdsPaid;
	}
	
}
