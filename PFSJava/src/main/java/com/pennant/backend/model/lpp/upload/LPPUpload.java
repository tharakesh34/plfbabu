package com.pennant.backend.model.lpp.upload;

import java.math.BigDecimal;

import com.pennant.pff.upload.model.UploadDetails;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class LPPUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private String loanType;
	private String applyToExistingLoans;
	private String applyOverDue;
	private String penaltyType;
	private String includeGraceDays;
	private int graceDays;
	private String calculatedOn;
	private BigDecimal amountOrPercent = BigDecimal.ZERO;
	private String allowWaiver;
	private BigDecimal maxWaiver = BigDecimal.ZERO;
	private String holdStatus;
	private String reason;
	private String remarks;
	private LoggedInUser userDetails;
	private int pickUpFlag;
	private BigDecimal oDMinAmount = BigDecimal.ZERO;

	public LPPUpload() {
		super();
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getApplyToExistingLoans() {
		return applyToExistingLoans;
	}

	public void setApplyToExistingLoans(String applyToExistingLoans) {
		this.applyToExistingLoans = applyToExistingLoans;
	}

	public String getApplyOverDue() {
		return applyOverDue;
	}

	public void setApplyOverDue(String applyOverDue) {
		this.applyOverDue = applyOverDue;
	}

	public String getPenaltyType() {
		return penaltyType;
	}

	public void setPenaltyType(String penaltyType) {
		this.penaltyType = penaltyType;
	}

	public String getIncludeGraceDays() {
		return includeGraceDays;
	}

	public void setIncludeGraceDays(String includeGraceDays) {
		this.includeGraceDays = includeGraceDays;
	}

	public int getGraceDays() {
		return graceDays;
	}

	public void setGraceDays(int graceDays) {
		this.graceDays = graceDays;
	}

	public String getCalculatedOn() {
		return calculatedOn;
	}

	public void setCalculatedOn(String calculatedOn) {
		this.calculatedOn = calculatedOn;
	}

	public BigDecimal getAmountOrPercent() {
		return amountOrPercent;
	}

	public void setAmountOrPercent(BigDecimal amountOrPercent) {
		this.amountOrPercent = amountOrPercent;
	}

	public String getAllowWaiver() {
		return allowWaiver;
	}

	public void setAllowWaiver(String allowWaiver) {
		this.allowWaiver = allowWaiver;
	}

	public BigDecimal getMaxWaiver() {
		return maxWaiver;
	}

	public void setMaxWaiver(BigDecimal maxWaiver) {
		this.maxWaiver = maxWaiver;
	}

	public String getHoldStatus() {
		return holdStatus;
	}

	public void setHoldStatus(String holdStatus) {
		this.holdStatus = holdStatus;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public int getPickUpFlag() {
		return pickUpFlag;
	}

	public void setPickUpFlag(int pickUpFlag) {
		this.pickUpFlag = pickUpFlag;
	}

	public BigDecimal getODMinAmount() {
		return oDMinAmount;
	}

	public void setODMinAmount(BigDecimal oDMinAmount) {
		this.oDMinAmount = oDMinAmount;
	}

}