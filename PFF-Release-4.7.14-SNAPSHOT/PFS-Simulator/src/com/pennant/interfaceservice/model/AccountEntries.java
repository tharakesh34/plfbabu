package com.pennant.interfaceservice.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AccountingEntries")
public class AccountEntries {

	private String custId;
	private String acCcy;//Renamed as in Pff
	private String accountType;//Renamed as in Pff
	private String branch;//Not Available
	private String account;//Account No Renamed as in pff
	private boolean internalAc;//Renamed as in pff
	private String tranOrder;//Renamed
	private String returnCode;//Not available
	private String returnText;//Not available
	private String tranCode;
	private String revTranCode;
	private String drOrCr;//tranType fix me
	private boolean shadow;
	private BigDecimal amount = BigDecimal.ZERO;
	private String postRef;//fix me
	private String finEvent;
	private String finReference;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


	@XmlElement(name = "Branch")
	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
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

	@XmlElement(name = "Amount")
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	@XmlElement(name = "AcctType")
	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	@XmlElement(name = "AccountNumber")
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@XmlElement(name = "CustId")
	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}
	
	@XmlElement(name = "TranOrder")
	public String getTranOrder() {
		return tranOrder;
	}

	public void setTranOrder(String tranOrder) {
		this.tranOrder = tranOrder;
	}

	@XmlElement(name = "TranType")
	public String getDrOrCr() {
		return drOrCr;
	}

	public void setDrOrCr(String drOrCr) {
		this.drOrCr = drOrCr;
	}

	public boolean isInternalAc() {
		return internalAc;
	}

	public void setInternalAc(boolean internalAc) {
		this.internalAc = internalAc;
	}
	
	
	
}
