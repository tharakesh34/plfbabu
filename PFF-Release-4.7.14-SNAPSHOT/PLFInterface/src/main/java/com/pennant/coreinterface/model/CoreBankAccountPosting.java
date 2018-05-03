package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CoreBankAccountPosting implements Serializable {

	private static final long serialVersionUID = -6462962406863237754L;
	
	public CoreBankAccountPosting() {
    	super();
    }
	
	private	String	linkedTranId;
	private	String 	finReference;
	private	String 	finEvent;
	private Date 	postingDate;
	private	Date	valueDate;
	private String  finType;
	
	private String  transOrderId;
	private String 	custCIF;
	private String 	acCcy;
	private String 	acBranch;
	private String 	acType;
	private String 	createNew;
	private String 	createIfNF;
	private String 	internalAc;
	private	String 	postStatus;
	private	String 	tranCode;
	private	String 	revTranCode;
	private	String 	drOrCr;
	private	String	shadow;
	private	String 	account;
	private	BigDecimal	postAmount = BigDecimal.ZERO;
	private	String 	errorId;
	private	String 	errorMsg;
	
	private String openStatus;
	private String errorCode;
	private String errorMessage;	
	private String acSPCode;
	private int reqRefId;
	private int reqRefSeq;
	private String postRef; 
	private String postingID;
	private String postToSys;
	private int derivedTranOrder = 0;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public void setPostingStatus(String postStatus, String errorId, String errorMsg){
		this.postStatus = postStatus;
		this.errorId = errorId;
		this.errorMsg = errorMsg;
	}
	
	public String getLinkedTranId() {
		return linkedTranId;
	}
	public void setLinkedTranId(String linkedTranId) {
		this.linkedTranId = linkedTranId;
	}
	
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
	
	public Date getPostingDate() {
		return postingDate;
	}
	public void setPostingDate(java.sql.Date postingDate) {
		this.postingDate = postingDate;
	}
	public Date getValueDate() {
		return valueDate;
	}
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	public void setTransOrderId(String transOrderId) {
		this.transOrderId = transOrderId;
	}
	public String getTransOrderId() {
		return transOrderId;
	}
	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
	
	public String getAcCcy() {
		return acCcy;
	}
	public void setAcCcy(String acCcy) {
		this.acCcy = acCcy;
	}
	
	public String getAcBranch() {
		return acBranch;
	}
	public void setAcBranch(String acBranch) {
		this.acBranch = acBranch;
	}
	
	public String getAcType() {
		return acType;
	}
	public void setAcType(String acType) {
		this.acType = acType;
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
	
	public String getInternalAc() {
		return internalAc;
	}
	public void setInternalAc(String internalAc) {
		this.internalAc = internalAc;
	}
	
	public String getPostStatus() {
		return postStatus;
	}
	public void setPostStatus(String postStatus) {
		this.postStatus = postStatus;
	}
	
	public String getTranCode() {
		return tranCode;
	}
	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}
	
	public String getRevTranCode() {
		return revTranCode;
	}
	public void setRevTranCode(String revTranCode) {
		this.revTranCode = revTranCode;
	}
	
	public String getDrOrCr() {
		return drOrCr;
	}
	public void setDrOrCr(String drOrCr) {
		this.drOrCr = drOrCr;
	}
	
	public String getShadow() {
		return shadow;
	}
	public void setShadow(String shadow) {
		this.shadow = shadow;
	}
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	public BigDecimal getPostAmount() {
		return postAmount;
	}
	public void setPostAmount(BigDecimal postAmount) {
		this.postAmount = postAmount;
	}
	
	public String getErrorId() {
		return errorId;
	}
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
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
	
	public void setPostRef(String postRef) {
	    this.postRef = postRef;
    }
	public String getPostRef() {
	    return postRef;
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
	public String getPostingID() {
		return postingID;
	}
	public void setPostingID(String postingID) {
		this.postingID = postingID;
	}

	public String getPostToSys() {
		return postToSys;
	}

	public void setPostToSys(String postToSys) {
		this.postToSys = postToSys;
	}

	public int getDerivedTranOrder() {
		return derivedTranOrder;
	}

	public void setDerivedTranOrder(int derivedTranOrder) {
		this.derivedTranOrder = derivedTranOrder;
	}
	
		
}
