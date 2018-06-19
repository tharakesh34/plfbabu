package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "postref", "finEvent", "postDate", "valueDate", "tranCode",
		"tranDesc", "revTranCode", "account", "drOrCr", "acCcy",
		"postAmount"})
public class ReturnDataSet implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 5269669204845337757L;

	private long linkedTranId = Long.MIN_VALUE;
	@XmlElement
	private String postref;
	private String postingId;
	private String finReference;
	@XmlElement(name="accEvent")
	private String finEvent;
	private String lovDescEventCodeName;
	@XmlElement
	private Date postDate;
	@XmlElement
	private Date valueDate;
	private Date appDate;
	private Date appValueDate;
	private Date custAppDate;
	@XmlElement
	private String tranCode;
	private int transOrder;
	private int derivedTranOrder = 0;
	@XmlElement
	private String tranDesc;
	@XmlElement
	private String revTranCode;
	@XmlElement
	private String drOrCr;
	private boolean shadowPosting;
	@XmlElement
	private String account;
	@XmlElement
	private BigDecimal postAmount = BigDecimal.ZERO;
	private String amountType;
	private String postStatus;
	private String errorId;
	private String errorMsg;
	private long custId = Long.MIN_VALUE;
	@XmlElement
	private String acCcy;
	private String tranOrderId;
	private String postToSys;
	private String postBranch;
	private BigDecimal	exchangeRate = BigDecimal.ZERO;
	private BigDecimal	postAmountLcCcy = BigDecimal.ZERO;

	private String accountType;
	//External Purpose fields
	//private String ruleDecider;
	private String eventCodeName;
	private String accSetCodeName;
	private long accSetId;
	private String finType;
	private String CustCIF;
//	private String finBranch;
	private boolean flagCreateNew;
	private boolean flagCreateIfNF;
	private boolean internalAc;
	private int formatter = 0;
	private String finPurpose;
	private String postingGroupBy;
	
	private String secondaryAccounts;//multiple accounts with ";" separated
	private String finRpyFor;
	private String userBranch;
	private long oldLinkedTranId = 0;
	private String entityCode;
	/**
	 * Possible values 
	 * 0- NON EOD posting 
	 * 1- EOD Postings 
	 * 2- EOD Postings and Accounts Updates
	 */
	private int	postCategory = 0;//FIXME:How to use constants-AccountConstants.POSTING_CATEGORY_NORMAL;;
	
	public ReturnDataSet() {
		
	}
	
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
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

//	public String getFinBranch() {
//		return finBranch;
//	}
//
//	public void setFinBranch(String finBranch) {
//		this.finBranch = finBranch;
//	}

	public boolean isFlagCreateNew() {
		return flagCreateNew;
	}

	public void setFlagCreateNew(boolean flagCreateNew) {
		this.flagCreateNew = flagCreateNew;
	}

	public boolean isFlagCreateIfNF() {
		return flagCreateIfNF;
	}

	public void setFlagCreateIfNF(boolean flagCreateIfNF) {
		this.flagCreateIfNF = flagCreateIfNF;
	}

	public boolean isInternalAc() {
		return internalAc;
	}

	public void setInternalAc(boolean internalAc) {
		this.internalAc = internalAc;
	}

	public void setTransOrder(int tranOrder) {
	    this.transOrder = tranOrder;
    }
	public int getTransOrder() {
	    return transOrder;
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

	public void setCustId(long custId) {
	    this.custId = custId;
    }

	public long getCustId() {
	    return custId;
    }

	public String getAcCcy() {
	    return acCcy;
    }

	public void setAcCcy(String acCcy) {
	    this.acCcy = acCcy;
    }

	public int getFormatter() {
	    return formatter;
    }

	public void setFormatter(int formatter) {
	    this.formatter = formatter;
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

	public String getFinPurpose() {
		return finPurpose;
	}

	public void setFinPurpose(String finPurpose) {
		this.finPurpose = finPurpose;
	}

	public String getPostBranch() {
		return postBranch;
	}
	public void setPostBranch(String postBranch) {
		this.postBranch = postBranch;
	}

	public String getSecondaryAccounts() {
		return secondaryAccounts;
	}

	public void setSecondaryAccounts(String secondaryAccounts) {
		this.secondaryAccounts = secondaryAccounts;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getFinRpyFor() {
		return finRpyFor;
	}

	public void setFinRpyFor(String finRpyFor) {
		this.finRpyFor = finRpyFor;
	}

	public BigDecimal getPostAmountLcCcy() {
		return postAmountLcCcy;
	}

	public void setPostAmountLcCcy(BigDecimal postAmountLcCcy) {
		this.postAmountLcCcy = postAmountLcCcy;
	}

	public String getPostingGroupBy() {
		return postingGroupBy;
	}
	public void setPostingGroupBy(String postingGroupBy) {
		this.postingGroupBy = postingGroupBy;
	}

	public String getAccSetCodeName() {
		return accSetCodeName;
	}
	public void setAccSetCodeName(String accSetCodeName) {
		this.accSetCodeName = accSetCodeName;
	}

	public long getAccSetId() {
		return accSetId;
	}
	public void setAccSetId(long accSetId) {
		this.accSetId = accSetId;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public Date getAppValueDate() {
		return appValueDate;
	}

	public void setAppValueDate(Date appValueDate) {
		this.appValueDate = appValueDate;
	}

	public String getUserBranch() {
		return userBranch;
	}

	public void setUserBranch(String userBranch) {
		this.userBranch = userBranch;
	}

	public Date getCustAppDate() {
		return custAppDate;
	}

	public void setCustAppDate(Date custAppDate) {
		this.custAppDate = custAppDate;
	}

	public int getPostCategory() {
		return postCategory;
	}

	public void setPostCategory(int postCategory) {
		this.postCategory = postCategory;
	}

	public long getOldLinkedTranId() {
		return oldLinkedTranId;
	}

	public void setOldLinkedTranId(long oldLinkedTranId) {
		this.oldLinkedTranId = oldLinkedTranId;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	
}