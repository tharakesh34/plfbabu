package com.pennant.coreinterface.model.account;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AccountingEntries")
public class AccountEntries implements Serializable {

	private static final long serialVersionUID = -8204809211257397469L;
	
	private String custCIF;
	private String acCcy;
	private String acType;
	private String acBranch;
	private String account;
	private boolean internalAc;
	private String transOrderId;
	private String returnCode;
	private String returnText;
	private String tranCode;
	private String revTranCode;
	private String drOrCr;
	private boolean shadow;
	private BigDecimal postAmount = BigDecimal.ZERO;
	private String postRef;
	private String finEvent;
	private String finReference;

	public AccountEntries() {
		
	}

	@XmlElement(name = "ReturnCode")
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	@XmlElement(name = "ReturnText")
	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	@XmlElement(name = "TranCode")
	public String getTranCode() {
		return tranCode;
	}

	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}

	@XmlElement(name = "RevTranCode")
	public String getRevTranCode() {
		return revTranCode;
	}

	public void setRevTranCode(String revTranCode) {
		this.revTranCode = revTranCode;
	}

	@XmlElement(name = "Shadow")
	public boolean isShadow() {
		return shadow;
	}

	public void setShadow(boolean shadow) {
		this.shadow = shadow;
	}

	@XmlElement(name = "PostRef")
	public String getPostRef() {
		return postRef;
	}

	public void setPostRef(String postRef) {
		this.postRef = postRef;
	}

	@XmlElement(name = "FinEvent")
	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	@XmlElement(name = "FinReference")
	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	@XmlElement(name = "Currency")
	public String getAcCcy() {
		return acCcy;
	}

	public void setAcCcy(String acCcy) {
		this.acCcy = acCcy;
	}

	@XmlElement(name = "AccountNumber")
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@XmlElement(name = "TranType")
	public String getDrOrCr() {
		return drOrCr;
	}

	public void setDrOrCr(String drOrCr) {
		this.drOrCr = drOrCr;
	}

	@XmlElement(name = "InternalAcct")
	public boolean isInternalAc() {
		return internalAc;
	}

	public void setInternalAc(boolean internalAc) {
		this.internalAc = internalAc;
	}

	@XmlElement(name = "CustId")
	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	@XmlElement(name = "AcctType")
	public String getAcType() {
		return acType;
	}

	public void setAcType(String acType) {
		this.acType = acType;
	}

	@XmlElement(name = "Branch")
	public String getAcBranch() {
		return acBranch;
	}

	public void setAcBranch(String acBranch) {
		this.acBranch = acBranch;
	}

	@XmlElement(name = "TranOrder")
	public String getTransOrderId() {
		return transOrderId;
	}

	public void setTransOrderId(String transOrderId) {
		this.transOrderId = transOrderId;
	}

	@XmlElement(name = "Amount")
	public BigDecimal getPostAmount() {
		return postAmount;
	}

	public void setPostAmount(BigDecimal postAmount) {
		this.postAmount = postAmount;
	}

}
