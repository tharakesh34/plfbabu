package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.Date;

public class DataSet {
	
	private String finReference;
	private String finEvent;
	private Date postDate;
	private Date valueDate;
	private Date schdDate;
	private String disburseAccount;
	private String repayAccount;
	private String finAccount;
	private String finCustPftAccount;
	private String finCcy;
	private String finBranch;
	private long custId = Long.MIN_VALUE;
	private BigDecimal disburseAmount = BigDecimal.ZERO;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal downPayment = BigDecimal.ZERO;
	private String finType;
	private int noOfTerms;
	private boolean isNewRecord = false;
	
	//Commitment
	private String cmtAccount;
	private String cmtChargeAccount;
	private BigDecimal cmtChargeAmount = BigDecimal.ZERO;
	private boolean openNewCmtAc;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	public String getFinEvent() {
		return finEvent;
	}
	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}
	
	public Date getPostDate() {
		return postDate;
	}
	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}
	
	public Date getValueDate() {
		return valueDate;
	}
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}
	
	public Date getSchdDate() {
		return schdDate;
	}
	public void setSchdDate(Date schdDate) {
		this.schdDate = schdDate;
	}
	
	public String getDisburseAccount() {
		return disburseAccount;
	}
	public void setDisburseAccount(String disburseAccount) {
		this.disburseAccount = disburseAccount;
	}
	
	public String getRepayAccount() {
		return repayAccount;
	}
	public void setRepayAccount(String repayAccount) {
		this.repayAccount = repayAccount;
	}
	
	public String getFinAccount() {
		return finAccount;
	}
	public void setFinAccount(String finAccount) {
		this.finAccount = finAccount;
	}
	
	public String getFinCustPftAccount() {
		return finCustPftAccount;
	}
	public void setFinCustPftAccount(String finCustPftAccount) {
		this.finCustPftAccount = finCustPftAccount;
	}
	
	public String getFinCcy() {
		return finCcy;
	}
	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}
	
	public String getFinBranch() {
		return finBranch;
	}
	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}
	
	public long getCustId() {
		return custId;
	}
	public void setCustId(long custId) {
		this.custId = custId;
	}
	
	public BigDecimal getDisburseAmount() {
    	return disburseAmount;
    }
	public void setDisburseAmount(BigDecimal disburseAmount) {
    	this.disburseAmount = disburseAmount;
    }
	
	public BigDecimal getFinAmount() {
		return finAmount;
	}
	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}
	
	public BigDecimal getDownPayment() {
		return downPayment;
	}
	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	public int getNoOfTerms() {
		return noOfTerms;
	}
	public void setNoOfTerms(int noOfTerms) {
		this.noOfTerms = noOfTerms;
	}
	
	public boolean isNewRecord() {
		return isNewRecord;
	}
	public void setNewRecord(boolean isNewRecord) {
		this.isNewRecord = isNewRecord;
	}
	
	public void setCmtAccount(String cmtAccount) {
	    this.cmtAccount = cmtAccount;
    }
	public String getCmtAccount() {
	    return cmtAccount;
    }
	
	public String getCmtChargeAccount() {
    	return cmtChargeAccount;
    }
	public void setCmtChargeAccount(String cmtChargeAccount) {
    	this.cmtChargeAccount = cmtChargeAccount;
    }	
	
	public void setCmtChargeAmount(BigDecimal cmtChargeAmount) {
	    this.cmtChargeAmount = cmtChargeAmount;
    }
	public BigDecimal getCmtChargeAmount() {
	    return cmtChargeAmount;
    }
	
	public void setOpenNewCmtAc(boolean openNewCmtAc) {
	    this.openNewCmtAc = openNewCmtAc;
    }
	public boolean isOpenNewCmtAc() {
	    return openNewCmtAc;
    }
	
}
