package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AccountPostingTemp implements Serializable {

	private static final long serialVersionUID = 9105442914597078688L;

	public AccountPostingTemp() {
    	super();
    }
	
	private int reqRefId;
	private int reqRefSeq;
	private String reqShadow;
	private String accNumber;
	private String postingBranch;
	private String postingCcy;
	private String postingCode;
	private BigDecimal postingAmount;
	private Date postingDate;
	private Date valueDate;
	private String postingRef;
	private String postingNar1;
	private String postingNar2;
	private String postingNar3;
	private String postingNar4;
	private String error;
	private String errDesc;
	

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

	public String getReqShadow() {
		return reqShadow;
	}

	public void setReqShadow(String reqShadow) {
		this.reqShadow = reqShadow;
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public String getPostingBranch() {
		return postingBranch;
	}

	public void setPostingBranch(String postingBranch) {
		this.postingBranch = postingBranch;
	}

	public String getPostingCcy() {
		return postingCcy;
	}

	public void setPostingCcy(String postingCcy) {
		this.postingCcy = postingCcy;
	}

	public String getPostingCode() {
		return postingCode;
	}

	public void setPostingCode(String postingCode) {
		this.postingCode = postingCode;
	}

	public BigDecimal getPostingAmount() {
		return postingAmount;
	}

	public void setPostingAmount(BigDecimal postingAmount) {
		this.postingAmount = postingAmount;
	}

	public Date getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getPostingRef() {
		return postingRef;
	}

	public void setPostingRef(String postingRef) {
		this.postingRef = postingRef;
	}

	public String getPostingNar1() {
		return postingNar1;
	}

	public void setPostingNar1(String postingNar1) {
		this.postingNar1 = postingNar1;
	}

	public String getPostingNar2() {
		return postingNar2;
	}

	public void setPostingNar2(String postingNar2) {
		this.postingNar2 = postingNar2;
	}

	public String getPostingNar3() {
		return postingNar3;
	}

	public void setPostingNar3(String postingNar3) {
		this.postingNar3 = postingNar3;
	}

	public String getPostingNar4() {
		return postingNar4;
	}

	public void setPostingNar4(String postingNar4) {
		this.postingNar4 = postingNar4;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrDesc() {
		return errDesc;
	}

	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}

}
