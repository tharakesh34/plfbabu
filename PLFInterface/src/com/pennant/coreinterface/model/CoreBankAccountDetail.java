package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CoreBankAccountDetail implements Serializable {

	private static final long serialVersionUID = -5109328909531296518L;

	private String custCIF;
	private String acBranch;
	private String acCcy;
	private String acType;
	private String acShrtName;
	private String tranAc;
	private String internalAc;
	private String createNew;
	private String createIfNF;
	private String amountSign;
	private String transOrder;

	private String custShrtName;
	private String accountNumber;
	private BigDecimal acBal;

	private String openStatus;
	private String errorCode;
	private String errorMessage;

	private String acSPCode;
	private int reqRefId;
	private int reqRefSeq;

	private String division;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getAcBranch() {
		return acBranch;
	}

	public void setAcBranch(String acBranch) {
		this.acBranch = acBranch;
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

	public String getAcShrtName() {
		return acShrtName;
	}

	public void setAcShrtName(String acShrtName) {
		this.acShrtName = acShrtName;
	}

	public String getTranAc() {
		return tranAc;
	}

	public void setTranAc(String tranAc) {
		this.tranAc = tranAc;
	}

	public String getInternalAc() {
		return internalAc;
	}

	public void setInternalAc(String internalAc) {
		this.internalAc = internalAc;
	}

	public String getCreateNew() {
		return createNew;
	}

	public void setCreateNew(String createNew) {
		this.createNew = createNew;
	}

	public String getCreateIfNF() {
		return createIfNF;
	}

	public void setCreateIfNF(String createIfNF) {
		this.createIfNF = createIfNF;
	}

	public String getAmountSign() {
		return amountSign;
	}

	public void setAmountSign(String amountSign) {
		this.amountSign = amountSign;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getAcBal() {
		return acBal;
	}

	public void setAcBal(BigDecimal acBal) {
		this.acBal = acBal;
	}

	public String getOpenStatus() {
		return openStatus;
	}

	public void setOpenStatus(String openStatus) {
		this.openStatus = openStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getAcSPCode() {
		return acSPCode;
	}

	public void setAcSPCode(String acSPCode) {
		this.acSPCode = acSPCode;
	}

	public int getReqRefId() {
		return reqRefId;
	}

	public void setReqRefId(int reqRefId) {
		this.reqRefId = reqRefId;
	}

	public int getReqRefSeq() {
		return reqRefSeq;
	}

	public void setReqRefSeq(int reqRefSeq) {
		this.reqRefSeq = reqRefSeq;
	}

	public void setTransOrder(String transOrder) {
		this.transOrder = transOrder;
	}

	public String getTransOrder() {
		return transOrder;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

}
