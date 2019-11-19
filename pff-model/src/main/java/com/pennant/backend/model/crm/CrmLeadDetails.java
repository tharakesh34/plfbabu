package com.pennant.backend.model.crm;

public class CrmLeadDetails {

	LeadDetails LeadDetailsObject;
	ReturnStatus ReturnStatusObject;

	public CrmLeadDetails() {
		super();
	}

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
