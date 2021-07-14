package com.pennant.backend.model.applicationmaster;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "loanPendingDetails", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
public class LoanPendingDetails extends AbstractWorkflowEntity implements java.io.Serializable {

	private static final long serialVersionUID = -2496487222454398311L;

	private long userID = 0;
	@JsonProperty("loanPendingDetails")
	private List<LoanPendingData> customerODLoanDataList = new ArrayList<>();
	@XmlElement
	private WSReturnStatus returnStatus;

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<LoanPendingData> getCustomerODLoanDataList() {
		return customerODLoanDataList;
	}

	public void setCustomerODLoanDataList(List<LoanPendingData> customerODLoanDataList) {
		this.customerODLoanDataList = customerODLoanDataList;
	}

}
