package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.Entity;

public class ReturnDataSet implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 5269669204845337757L;

	private long linkedTranId = Long.MIN_VALUE;
	private String postref;
	private String postingId;
	private String finReference;
	private String finEvent;
	private String lovDescEventCodeName;
	private Date postDate;
	private Date valueDate;
	private String tranCode;
	private int tranOrder;
	private String tranDesc;
	private String revTranCode;
	private String drOrCr;
	private boolean shadowPosting;
	private String account;
	private BigDecimal postAmount = new BigDecimal(0);
	private String amountType;
	private String postStatus;
	private String errorId;
	private String errorMsg;
	private String hostAccountNumber;

	//External Purpose fields
	private String accountType;
	private String ruleDecider;
	private String eventCodeName;
	private String finType;
	private String CustCIF;
	private String finCcy;
	private String finBranch;
	private String flagCreateNew;
	private String flagCreateIfNF;
	private String internalAc;
	private String tranOrderId;

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public long getId() {
		return linkedTranId;
	}

	@Override
	public void setId(long id) {
		this.linkedTranId = id;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public String getPostref() {
		return postref;
	}

	public void setPostref(String postref) {
		this.postref = postref;
	}

	public String getPostingId() {
		return postingId;
	}

	public void setPostingId(String postingId) {
		this.postingId = postingId;
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
	
	public String getLovDescEventCodeName() {
    	return lovDescEventCodeName;
    }

	public void setLovDescEventCodeName(String lovDescEventCodeName) {
    	this.lovDescEventCodeName = lovDescEventCodeName;
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

	public String getTranCode() {
		return tranCode;
	}

	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}
	
	public String getTranDesc() {
    	return tranDesc;
    }

	public void setTranDesc(String tranDesc) {
    	this.tranDesc = tranDesc;
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

	public boolean isShadowPosting() {
		return shadowPosting;
	}

	public void setShadowPosting(boolean shadowPosting) {
		this.shadowPosting = shadowPosting;
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

	public String getPostStatus() {
		return postStatus;
	}

	public void setPostStatus(String postStatus) {
		this.postStatus = postStatus;
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

	public String getHostAccountNumber() {
		return hostAccountNumber;
	}

	public void setHostAccountNumber(String hostAccountNumber) {
		this.hostAccountNumber = hostAccountNumber;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getRuleDecider() {
		return ruleDecider;
	}

	public void setRuleDecider(String ruleDecider) {
		this.ruleDecider = ruleDecider;
	}

	public String getEventCodeName() {
		return eventCodeName;
	}

	public void setEventCodeName(String eventCodeName) {
		this.eventCodeName = eventCodeName;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getCustCIF() {
		return CustCIF;
	}

	public void setCustCIF(String custCIF) {
		CustCIF = custCIF;
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

	public String getFlagCreateNew() {
		return flagCreateNew;
	}

	public void setFlagCreateNew(String flagCreateNew) {
		this.flagCreateNew = flagCreateNew;
	}

	public String getFlagCreateIfNF() {
		return flagCreateIfNF;
	}

	public void setFlagCreateIfNF(String flagCreateIfNF) {
		this.flagCreateIfNF = flagCreateIfNF;
	}

	public String getInternalAc() {
		return internalAc;
	}

	public void setInternalAc(String internalAc) {
		this.internalAc = internalAc;
	}

	public void setTranOrder(int tranOrder) {
	    this.tranOrder = tranOrder;
    }
	public int getTranOrder() {
	    return tranOrder;
    }

	public void setAmountType(String amountType) {
	    this.amountType = amountType;
    }

	public String getAmountType() {
	    return amountType;
    }

	public void setTranOrderId(String tranOrderId) {
	    this.tranOrderId = tranOrderId;
    }

	public String getTranOrderId() {
	    return tranOrderId;
    }

}