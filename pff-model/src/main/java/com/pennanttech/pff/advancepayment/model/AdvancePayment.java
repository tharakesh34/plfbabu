package com.pennanttech.pff.advancepayment.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.finance.FinanceMain;

public class AdvancePayment implements Serializable {
	private static final long serialVersionUID = 1L;

	private FinanceMain financeMain;
	private String advancePaymentType;
	private String grcAdvType;
	private String advType;
	private Date grcPeriodEndDate;

	private BigDecimal intAdjusted = BigDecimal.ZERO;
	private BigDecimal emiAdjusted = BigDecimal.ZERO;
	private BigDecimal intTdsAdjusted = BigDecimal.ZERO;
	private BigDecimal requestedAmt = BigDecimal.ZERO;

	public AdvancePayment() {
		super();
	}

	public AdvancePayment(FinanceMain financeMain) {
		super();
		this.grcAdvType = financeMain.getGrcAdvType();
		this.advType = financeMain.getAdvType();
		this.grcPeriodEndDate = financeMain.getGrcPeriodEndDate();
		this.financeMain = financeMain;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public String getAdvancePaymentType() {
		return advancePaymentType;
	}

	public void setAdvancePaymentType(String advancePaymentType) {
		this.advancePaymentType = advancePaymentType;
	}

	public String getGrcAdvType() {
		return grcAdvType;
	}

	public void setGrcAdvType(String grcAdvType) {
		this.grcAdvType = grcAdvType;
	}

	public String getAdvType() {
		return advType;
	}

	public Date getGrcPeriodEndDate() {
		return grcPeriodEndDate;
	}

	public void setGrcPeriodEndDate(Date grcPeriodEndDate) {
		this.grcPeriodEndDate = grcPeriodEndDate;
	}

	public void setAdvType(String advType) {
		this.advType = advType;
	}

	public BigDecimal getIntAdjusted() {
		return intAdjusted;
	}

	public void setIntAdjusted(BigDecimal intAdjusted) {
		this.intAdjusted = intAdjusted;
	}

	public BigDecimal getEmiAdjusted() {
		return emiAdjusted;
	}

	public void setEmiAdjusted(BigDecimal emiAdjusted) {
		this.emiAdjusted = emiAdjusted;
	}

	public BigDecimal getIntTdsAdjusted() {
		return intTdsAdjusted;
	}

	public void setIntTdsAdjusted(BigDecimal intTdsAdjusted) {
		this.intTdsAdjusted = intTdsAdjusted;
	}

	public BigDecimal getRequestedAmt() {
		return requestedAmt;
	}

	public void setRequestedAmt(BigDecimal requestedAmt) {
		this.requestedAmt = requestedAmt;
	}
}
