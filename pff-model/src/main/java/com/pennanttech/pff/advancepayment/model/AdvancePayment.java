package com.pennanttech.pff.advancepayment.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class AdvancePayment implements Serializable {
	private static final long serialVersionUID = 1L;

	private String finReference;
	private String finBranch;
	private String advancePaymentType;
	private String grcAdvType;
	private String advType;
	private Date grcPeriodEndDate;
	private BigDecimal schdPriDue = BigDecimal.ZERO;
	private BigDecimal schdIntDue = BigDecimal.ZERO;
	private List<FinExcessAmount> excessAmounts = new ArrayList<>();
	private Date valueDate;

	private BigDecimal intAdjusted = BigDecimal.ZERO;
	private BigDecimal intAdvAvailable = BigDecimal.ZERO;
	private BigDecimal intDue = BigDecimal.ZERO;
	private BigDecimal advIntDue = BigDecimal.ZERO;
	private BigDecimal emiAdjusted = BigDecimal.ZERO;
	private BigDecimal emiAdvAvailable = BigDecimal.ZERO;
	private BigDecimal emiDue = BigDecimal.ZERO;
	private BigDecimal availableAmt = BigDecimal.ZERO;
	private BigDecimal balanceAmt = BigDecimal.ZERO;
	private FinExcessAmount finExcessAmount;
	private BigDecimal requestedAmt = BigDecimal.ZERO;
	private FinanceScheduleDetail curSchd;
	private FinanceScheduleDetail nextSchd;

	public AdvancePayment() {
		super();
	}

	public AdvancePayment(String grcAdvType, String advType, Date grcPeriodEndDate) {
		super();
		this.grcAdvType = grcAdvType;
		this.advType = advType;
		this.grcPeriodEndDate = grcPeriodEndDate;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
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

	public BigDecimal getSchdPriDue() {
		return schdPriDue;
	}

	public void setSchdPriDue(BigDecimal schdPriDue) {
		this.schdPriDue = schdPriDue;
	}

	public BigDecimal getSchdIntDue() {
		return schdIntDue;
	}

	public void setSchdIntDue(BigDecimal schdIntDue) {
		this.schdIntDue = schdIntDue;
	}

	public List<FinExcessAmount> getExcessAmounts() {
		return excessAmounts;
	}

	public void setExcessAmounts(List<FinExcessAmount> excessAmounts) {
		this.excessAmounts = excessAmounts;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getIntAdjusted() {
		return intAdjusted;
	}

	public void setIntAdjusted(BigDecimal intAdjusted) {
		this.intAdjusted = intAdjusted;
	}

	public BigDecimal getIntAdvAvailable() {
		return intAdvAvailable;
	}

	public void setIntAdvAvailable(BigDecimal intAdvAvailable) {
		this.intAdvAvailable = intAdvAvailable;
	}

	public BigDecimal getIntDue() {
		return intDue;
	}

	public void setIntDue(BigDecimal intDue) {
		this.intDue = intDue;
	}

	public BigDecimal getAdvIntDue() {
		return advIntDue;
	}

	public void setAdvIntDue(BigDecimal advIntDue) {
		this.advIntDue = advIntDue;
	}

	public BigDecimal getEmiAdjusted() {
		return emiAdjusted;
	}

	public void setEmiAdjusted(BigDecimal emiAdjusted) {
		this.emiAdjusted = emiAdjusted;
	}

	public BigDecimal getEmiAdvAvailable() {
		return emiAdvAvailable;
	}

	public void setEmiAdvAvailable(BigDecimal emiAdvAvailable) {
		this.emiAdvAvailable = emiAdvAvailable;
	}

	public BigDecimal getEmiDue() {
		return emiDue;
	}

	public void setEmiDue(BigDecimal emiDue) {
		this.emiDue = emiDue;
	}

	public BigDecimal getBalanceAmt() {
		return balanceAmt;
	}

	public void setBalanceAmt(BigDecimal balanceAmt) {
		this.balanceAmt = balanceAmt;
	}

	public BigDecimal getAvailableAmt() {
		return availableAmt;
	}

	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}

	public FinExcessAmount getFinExcessAmount() {
		return finExcessAmount;
	}

	public void setFinExcessAmount(FinExcessAmount finExcessAmount) {
		this.finExcessAmount = finExcessAmount;
	}

	public BigDecimal getRequestedAmt() {
		return requestedAmt;
	}

	public void setRequestedAmt(BigDecimal requestedAmt) {
		this.requestedAmt = requestedAmt;
	}

	public FinanceScheduleDetail getCurSchd() {
		return curSchd;
	}

	public void setCurSchd(FinanceScheduleDetail curSchd) {
		this.curSchd = curSchd;
	}

	public FinanceScheduleDetail getNextSchd() {
		return nextSchd;
	}

	public void setNextSchd(FinanceScheduleDetail nextSchd) {
		this.nextSchd = nextSchd;
	}

}
