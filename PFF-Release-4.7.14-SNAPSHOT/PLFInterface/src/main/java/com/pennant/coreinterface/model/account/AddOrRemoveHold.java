package com.pennant.coreinterface.model.account;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AddOrRemoveHold")
public class AddOrRemoveHold implements Serializable {

	private static final long serialVersionUID = -5637756045210123456L;
	
	private String referenceNum;
	private String branchCode;
	private String returnCode;
	private String returnText;
	private long timeStamp;
	private List<AccountDetail> accountDetail;
	
	public AddOrRemoveHold() {
		
	}
	
	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name = "BranchCode")
	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
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
	
	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	@XmlElement(name = "AccountDetails")
	public List<AccountDetail> getAccountDetail() {
		return accountDetail;
	}

	public void setAccountDetail(List<AccountDetail> accountDetail) {
		this.accountDetail = accountDetail;
	}
}
