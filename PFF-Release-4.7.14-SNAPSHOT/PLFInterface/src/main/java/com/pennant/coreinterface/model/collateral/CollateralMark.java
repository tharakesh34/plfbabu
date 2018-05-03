package com.pennant.coreinterface.model.collateral;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.pennant.coreinterface.model.account.AccountDetail;

@XmlRootElement(name = "CollateralMark")
public class CollateralMark implements Serializable {

	private static final long serialVersionUID = 1571411887181211920L;

	private String referenceNum;
	private String branchCode;
	private String status;
	private String returnCode;
	private String returnText;
	private String finReference;
	private long timeStamp;

	private List<AccountDetail> accountDetail;
	private List<DepositDetail> depositDetail;

	public CollateralMark() {
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
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
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
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

	@XmlElement(name = "DepositDetails")
	public List<DepositDetail> getDepositDetail() {
		return depositDetail;
	}

	public void setDepositDetail(List<DepositDetail> depositDetail) {
		this.depositDetail = depositDetail;
	}
}
