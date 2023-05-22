package com.pennant.Interface.model;

import java.math.BigDecimal;

public class IAccounts implements java.io.Serializable {

	private static final long serialVersionUID = -1673137129792916291L;
	private String accountId;
	private String acCcy;
	private String acType;
	private String acBranch;
	private String acCustCIF;
	private String acFullName;
	private String acShortName;
	private String acPurpose;
	private boolean internalAc = true;
	private boolean flagCreateNew = true;
	private boolean flagCreateIfNF = true;
	private String flagPostStatus;
	private String transOrder;
	private String errorCode;
	private String errorMsg;
	private String availBalSign;
	private BigDecimal acAvailableBal;
	private String lovValue;
	private String division;

	// For Temporary Purpose in Core banking
	private String tranAc;

	public IAccounts() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAcCcy() {
		return acCcy;
	}

	public void setAcCcy(String acCcy) {
		this.acCcy = acCcy;
	}

	public String getAcType() {
		return acType;
	}

	public void setAcType(String acType) {
		this.acType = acType;
	}

	public String getAcBranch() {
		return acBranch;
	}

	public void setAcBranch(String acBranch) {
		this.acBranch = acBranch;
	}

	public String getAcCustCIF() {
		return acCustCIF;
	}

	public void setAcCustCIF(String acCustCIF) {
		this.acCustCIF = acCustCIF;
	}

	public String getAcFullName() {
		return acFullName;
	}

	public void setAcFullName(String acFullName) {
		this.acFullName = acFullName;
	}

	public String getAcShortName() {
		return acShortName;
	}

	public void setAcShortName(String acShortName) {
		this.acShortName = acShortName;
	}

	public String getAcPurpose() {
		return acPurpose;
	}

	public void setAcPurpose(String acPurpose) {
		this.acPurpose = acPurpose;
	}

	public boolean getInternalAc() {
		return internalAc;
	}

	public void setInternalAc(boolean internalAc) {
		this.internalAc = internalAc;
	}

	public boolean getFlagCreateNew() {
		return flagCreateNew;
	}

	public void setFlagCreateNew(boolean flagCreateNew) {
		this.flagCreateNew = flagCreateNew;
	}

	public boolean getFlagCreateIfNF() {
		return flagCreateIfNF;
	}

	public void setFlagCreateIfNF(boolean flagCreateIfNF) {
		this.flagCreateIfNF = flagCreateIfNF;
	}

	public String getFlagPostStatus() {
		return flagPostStatus;
	}

	public void setFlagPostStatus(String flagPostStatus) {
		this.flagPostStatus = flagPostStatus;
	}

	public void setTransOrder(String transOrder) {
		this.transOrder = transOrder;
	}

	public String getTransOrder() {
		return transOrder;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getAvailBalSign() {
		return availBalSign;
	}

	public void setAvailBalSign(String availBalSign) {
		this.availBalSign = availBalSign;
	}

	public BigDecimal getAcAvailableBal() {
		return acAvailableBal;
	}

	public void setAcAvailableBal(BigDecimal acAvailableBal) {
		this.acAvailableBal = acAvailableBal;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public String getLovValue() {
		return lovValue;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getTranAc() {
		return tranAc;
	}

	public void setTranAc(String tranAc) {
		this.tranAc = tranAc;
	}

}
