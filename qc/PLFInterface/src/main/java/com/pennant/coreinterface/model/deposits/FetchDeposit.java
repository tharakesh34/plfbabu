package com.pennant.coreinterface.model.deposits;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetInvestmentAccountReply")
public class FetchDeposit implements Serializable {

	private static final long serialVersionUID = 2467344889696127687L;

	public FetchDeposit() {

	}

	private String referenceNum;
	private String customerNo;
	private String returnCode;
	private String returnText;
	private long timeStamp;

	private List<InvestmentContract> invstMentContactList;

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

	@XmlElement(name = "CustomerNo")
	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
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

	@XmlElement(name = "InvestmentContract")
	public List<InvestmentContract> getInvstMentContactList() {
		return invstMentContactList;
	}

	public void setInvstMentContactList(List<InvestmentContract> invstMentContactList) {
		this.invstMentContactList = invstMentContactList;
	}

}
