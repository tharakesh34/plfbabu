package com.pennant.backend.model.FinRepayQueue;

import java.math.BigDecimal;
import java.util.Date;

public class FinRepayQueue {

	private String finReference;
	private Date rpyDate;
	private String finRpyFor;
	private int finPriority;
	private String finType;
	private String Branch;
	private long customerID;
	private BigDecimal schdPft = new BigDecimal(0);
	private BigDecimal schdPri = new BigDecimal(0);
	private BigDecimal schdPftPaid = new BigDecimal(0);
	private BigDecimal schdPriPaid = new BigDecimal(0);
	private BigDecimal schdPftBal = new BigDecimal(0);
	private BigDecimal schdPriBal = new BigDecimal(0);
	private boolean schdIsPftPaid;
	private boolean schdIsPriPaid;

	// External Fields Used for EOD process
	private BigDecimal refundAmount = new BigDecimal(0);
	private boolean rcdNotExist = false;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getRpyDate() {
		return rpyDate;
	}

	public void setRpyDate(Date rpyDate) {
		this.rpyDate = rpyDate;
	}

	public String getFinRpyFor() {
		return finRpyFor;
	}

	public void setFinRpyFor(String finRpyFor) {
		this.finRpyFor = finRpyFor;
	}

	public int getFinPriority() {
		return finPriority;
	}

	public void setFinPriority(int finPriority) {
		this.finPriority = finPriority;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getBranch() {
		return Branch;
	}

	public void setBranch(String branch) {
		Branch = branch;
	}

	public long getCustomerID() {
		return customerID;
	}

	public void setCustomerID(long customerID) {
		this.customerID = customerID;
	}

	public BigDecimal getSchdPft() {
		return schdPft;
	}

	public void setSchdPft(BigDecimal schdPft) {
		this.schdPft = schdPft;
	}

	public BigDecimal getSchdPri() {
		return schdPri;
	}

	public void setSchdPri(BigDecimal schdPri) {
		this.schdPri = schdPri;
	}

	public BigDecimal getSchdPftPaid() {
		return schdPftPaid;
	}

	public void setSchdPftPaid(BigDecimal schdPftPaid) {
		this.schdPftPaid = schdPftPaid;
	}

	public BigDecimal getSchdPriPaid() {
		return schdPriPaid;
	}

	public void setSchdPriPaid(BigDecimal schdPriPaid) {
		this.schdPriPaid = schdPriPaid;
	}

	public BigDecimal getSchdPftBal() {
		return schdPftBal;
	}

	public void setSchdPftBal(BigDecimal schdPftBal) {
		this.schdPftBal = schdPftBal;
	}

	public BigDecimal getSchdPriBal() {
		return schdPriBal;
	}

	public void setSchdPriBal(BigDecimal schdPriBal) {
		this.schdPriBal = schdPriBal;
	}

	public boolean isSchdIsPftPaid() {
		return schdIsPftPaid;
	}

	public void setSchdIsPftPaid(boolean schdIsPftPaid) {
		this.schdIsPftPaid = schdIsPftPaid;
	}

	public boolean isSchdIsPriPaid() {
		return schdIsPriPaid;
	}

	public void setSchdIsPriPaid(boolean schdIsPriPaid) {
		this.schdIsPriPaid = schdIsPriPaid;
	}

	public BigDecimal getRefundAmount() {
    	return refundAmount;
    }
	public void setRefundAmount(BigDecimal refundAmount) {
    	this.refundAmount = refundAmount;
    }

	public boolean isRcdNotExist() {
    	return rcdNotExist;
    }
	public void setRcdNotExist(boolean rcdNotExist) {
    	this.rcdNotExist = rcdNotExist;
    }

}