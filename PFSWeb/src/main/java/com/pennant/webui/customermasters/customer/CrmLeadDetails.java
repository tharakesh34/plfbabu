package com.pennant.webui.customermasters.customer;

public class CrmLeadDetails {

	LeadDetails LeadDetailsObject;
	ReturnStatus ReturnStatusObject;

	// Getter Methods

	public LeadDetails getLeadDetails() {
		return LeadDetailsObject;
	}

	public ReturnStatus getReturnStatus() {
		return ReturnStatusObject;
	}

	// Setter Methods

	public void setLeadDetails(LeadDetails leadDetailsObject) {
		this.LeadDetailsObject = leadDetailsObject;
	}

	public void setReturnStatus(ReturnStatus returnStatusObject) {
		this.ReturnStatusObject = returnStatusObject;
	}
}
