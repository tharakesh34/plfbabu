package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.Date;

public class DataSet {
	
	private String finReference;
	private String finEvent;
	private Date postDate;
	private Date valueDate;
	private Date schdDate;
	private Date maturityDate;
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
	private BigDecimal downPayBank = BigDecimal.ZERO;
	private BigDecimal downPaySupl = BigDecimal.ZERO;
	private BigDecimal feeAmount = BigDecimal.ZERO;
	private String downPayAccount;
	private String finType;
	private String finPurpose;
	private int noOfTerms;
	private int tenure;
	private boolean isNewRecord = false;
	private int finJointAcCount = 0;
	private int curRpyDefCount = 0;
	private BigDecimal securityDeposit = BigDecimal.ZERO;
	private BigDecimal curDisbRet = BigDecimal.ZERO;
	private BigDecimal netRetDue = BigDecimal.ZERO;
	private BigDecimal grcPftChg = BigDecimal.ZERO;
	private BigDecimal claimAmt = BigDecimal.ZERO;
	private BigDecimal grcPftTillNow = BigDecimal.ZERO;
	private BigDecimal advDue = BigDecimal.ZERO;
	
	//Commitment
	private String cmtReference;
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
	
	public String getFinPurpose() {
    	return finPurpose;
    }
	public void setFinPurpose(String finPurpose) {
    	this.finPurpose = finPurpose;
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
	
	public void setCmtReference(String cmtReference) {
	    this.cmtReference = cmtReference;
    }
	public String getCmtReference() {
	    return cmtReference;
    }
	
	public void setFinJointAcCount(int finJointAcCount) {
	    this.finJointAcCount = finJointAcCount;
    }
	public int getFinJointAcCount() {
	    return finJointAcCount;
    }
	
	public void setDownPayBank(BigDecimal downPayBank) {
	    this.downPayBank = downPayBank;
    }
	public BigDecimal getDownPayBank() {
	    return downPayBank;
    }
	
	public void setDownPaySupl(BigDecimal downPaySupl) {
	    this.downPaySupl = downPaySupl;
    }
	public BigDecimal getDownPaySupl() {
	    return downPaySupl;
    }
	
	public void setDownPayAccount(String downPayAccount) {
	    this.downPayAccount = downPayAccount;
    }
	public String getDownPayAccount() {
	    return downPayAccount;
    }
	
	public void setSecurityDeposit(BigDecimal securityDeposit) {
	    this.securityDeposit = securityDeposit;
    }
	public BigDecimal getSecurityDeposit() {
	    return securityDeposit;
    }
	
	public void setCurDisbRet(BigDecimal curDisbRet) {
	    this.curDisbRet = curDisbRet;
    }
	public BigDecimal getCurDisbRet() {
	    return curDisbRet;
    }
	
	public void setNetRetDue(BigDecimal netRetDue) {
	    this.netRetDue = netRetDue;
    }
	public BigDecimal getNetRetDue() {
	    return netRetDue;
    }
	
	public void setGrcPftChg(BigDecimal grcPftChg) {
	    this.grcPftChg = grcPftChg;
    }
	public BigDecimal getGrcPftChg() {
	    return grcPftChg;
    }
	
	public void setClaimAmt(BigDecimal claimAmt) {
	    this.claimAmt = claimAmt;
    }
	public BigDecimal getClaimAmt() {
	    return claimAmt;
    }
	public void setGrcPftTillNow(BigDecimal grcPftTillNow) {
	    this.grcPftTillNow = grcPftTillNow;
    }
	public BigDecimal getGrcPftTillNow() {
	    return grcPftTillNow;
    }
	public void setAdvDue(BigDecimal advDue) {
	    this.advDue = advDue;
    }
	public BigDecimal getAdvDue() {
	    return advDue;
    }
	public void setTenure(int tenure) {
	    this.tenure = tenure;
    }
	public int getTenure() {
	    return tenure;
    }
	public Date getMaturityDate() {
	    return maturityDate;
    }
	public void setMaturityDate(Date maturityDate) {
	    this.maturityDate = maturityDate;
    }
	public int getCurRpyDefCount() {
	    return curRpyDefCount;
    }
	public void setCurRpyDefCount(int curRpyDefCount) {
	    this.curRpyDefCount = curRpyDefCount;
    }
	public BigDecimal getFeeAmount() {
	    return feeAmount;
    }
	public void setFeeAmount(BigDecimal feeAmount) {
	    this.feeAmount = feeAmount;
    }
	
}
