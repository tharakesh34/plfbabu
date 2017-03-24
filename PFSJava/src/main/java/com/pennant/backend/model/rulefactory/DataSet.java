package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.Date;

public class DataSet {
	
	private String finReference;
	private String finEvent;
	private String moduledefiner;
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
	private BigDecimal insAmount = BigDecimal.ZERO;
	private String downPayAccount;
	private String finCancelAc;
	private String finWriteoffAc;
	private String feeAccountId;
	private String finType;
	private String finPurpose;
	private String finRepayMethod;
	private int noOfTerms;
	private int tenure;
	private boolean isNewRecord = false;
	private int finJointAcCount = 0;
	private int curRpyDefCount = 0;
	private BigDecimal securityDeposit = BigDecimal.ZERO;
	private BigDecimal deductFeeDisb = BigDecimal.ZERO;
	private BigDecimal curDisbRet = BigDecimal.ZERO;
	private BigDecimal netRetDue = BigDecimal.ZERO;
	private BigDecimal grcPftChg = BigDecimal.ZERO;
	private BigDecimal claimAmt = BigDecimal.ZERO;
	private BigDecimal grcPftTillNow = BigDecimal.ZERO;
	private BigDecimal advDue = BigDecimal.ZERO;
	private String finWriteoffPayAc;
	//Commitment
	private String cmtReference;
	private String cmtAccount;
	private String cmtChargeAccount;
	private BigDecimal cmtChargeAmount = BigDecimal.ZERO;
	private boolean openNewCmtAc;
	
	//Commodity
	private BigDecimal PURAMOUNT = BigDecimal.ZERO;
	private long QUANTITY;
	private String brokerAccount;
	private BigDecimal UNITPRICE = BigDecimal.ZERO;
	private BigDecimal UNSOLDFEE = BigDecimal.ZERO;
	private BigDecimal SETTLEAMT = BigDecimal.ZERO;

	//Pre Approval Fee
	private BigDecimal PREAPPFEE = BigDecimal.ZERO;
	private BigDecimal PREAPPFEEW = BigDecimal.ZERO;
	private BigDecimal PREAPPFEEP = BigDecimal.ZERO;
	private BigDecimal deductInsDisb = BigDecimal.ZERO;
	
	//Rebate
	private BigDecimal rebate = BigDecimal.ZERO;
	
	private String custCIF;
	
	public DataSet() {
		
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
	
	public BigDecimal getDeductFeeDisb() {
		return deductFeeDisb;
	}
	public void setDeductFeeDisb(BigDecimal deductFeeDisb) {
		this.deductFeeDisb = deductFeeDisb;
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
	public String getFinRepayMethod() {
	    return finRepayMethod;
    }
	public void setFinRepayMethod(String finRepayMethod) {
	    this.finRepayMethod = finRepayMethod;
    }

	public String getModuledefiner() {
	    return moduledefiner;
    }

	public void setModuledefiner(String moduledefiner) {
	    this.moduledefiner = moduledefiner;
    }

	public BigDecimal getPURAMOUNT() {
		return PURAMOUNT;
	}

	public void setPURAMOUNT(BigDecimal pURAMOUNT) {
		PURAMOUNT = pURAMOUNT;
	}

	public long getQUANTITY() {
		return QUANTITY;
	}

	public void setQUANTITY(long qUANTITY) {
		QUANTITY = qUANTITY;
	}

	public BigDecimal getUNITPRICE() {
		return UNITPRICE;
	}

	public void setUNITPRICE(BigDecimal uNITPRICE) {
		UNITPRICE = uNITPRICE;
	}

	public BigDecimal getPREAPPFEE() {
	    return PREAPPFEE;
    }

	public void setPREAPPFEE(BigDecimal pREAPPFEE) {
	    PREAPPFEE = pREAPPFEE;
    }

	public BigDecimal getPREAPPFEEW() {
	    return PREAPPFEEW;
    }

	public void setPREAPPFEEW(BigDecimal pREAPPFEEW) {
	    PREAPPFEEW = pREAPPFEEW;
    }

	public BigDecimal getPREAPPFEEP() {
	    return PREAPPFEEP;
    }

	public void setPREAPPFEEP(BigDecimal pREAPPFEEP) {
	    PREAPPFEEP = pREAPPFEEP;
    }

	public String getFinCancelAc() {
		return finCancelAc;
	}

	public void setFinCancelAc(String finCancelAc) {
		this.finCancelAc = finCancelAc;
	}

	public String getFeeAccountId() {
		return feeAccountId;
	}

	public void setFeeAccountId(String feeAccountId) {
		this.feeAccountId = feeAccountId;
	}

	public String getFinWriteoffAc() {
		return finWriteoffAc;
	}

	public void setFinWriteoffAc(String finWriteoffAc) {
		this.finWriteoffAc = finWriteoffAc;
	}

	public String getFinWriteoffPayAc() {
		return finWriteoffPayAc;
	}

	public void setFinWriteoffPayAc(String finWriteoffPayAc) {
		this.finWriteoffPayAc = finWriteoffPayAc;
	}
	
	public BigDecimal getRebate() {
		return rebate;
	}
	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public String getBrokerAccount() {
		return brokerAccount;
	}

	public void setBrokerAccount(String brokerAccount) {
		this.brokerAccount = brokerAccount;
	}

	public BigDecimal getUNSOLDFEE() {
		return UNSOLDFEE;
	}

	public void setUNSOLDFEE(BigDecimal uNSOLDFEE) {
		UNSOLDFEE = uNSOLDFEE;
	}

	public BigDecimal getSETTLEAMT() {
		return SETTLEAMT;
	}

	public void setSETTLEAMT(BigDecimal sETTLEAMT) {
		SETTLEAMT = sETTLEAMT;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public BigDecimal getInsAmount() {
		return insAmount;
	}

	public void setInsAmount(BigDecimal insAmount) {
		this.insAmount = insAmount;
	}

	public BigDecimal getDeductInsDisb() {
		return deductInsDisb;
	}

	public void setDeductInsDisb(BigDecimal deductInsDisb) {
		this.deductInsDisb = deductInsDisb;
	}



}
