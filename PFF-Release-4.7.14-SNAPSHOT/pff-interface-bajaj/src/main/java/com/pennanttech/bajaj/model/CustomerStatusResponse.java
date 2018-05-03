package com.pennanttech.bajaj.model;


public class CustomerStatusResponse {
	
	private String dealId;
	private String areaStatus;
	private String customerStatus;
	private String customerID;
	private String applicationID;
	private String dedupeLANMatches;
	private String applicationNumberDisburse;
	private String elcFlag;
	private double elcLimit;
	private String rejectLANMatches;
	private String fraudLANMatches;
	private double  cobrandLimit;
	private double cobrandAvailLimit;
	private double cobrandCount;
	private String wipLANMatches;
	private String rblLanMatches;

	public String getDealId() {
		return dealId;
	}
	public void setDealId(String dealId) {
		this.dealId = dealId;
	}
	public String getAreaStatus() {
		return areaStatus;
	}
	public void setAreaStatus(String areaStatus) {
		this.areaStatus = areaStatus;
	}
	public String getCustomerStatus() {
		return customerStatus;
	}
	public void setCustomerStatus(String customerStatus) {
		this.customerStatus = customerStatus;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public String getApplicationID() {
		return applicationID;
	}
	public void setApplicationID(String applicationID) {
		this.applicationID = applicationID;
	}
	public String getDedupeLANMatches() {
		return dedupeLANMatches;
	}
	public void setDedupeLANMatches(String dedupeLANMatches) {
		this.dedupeLANMatches = dedupeLANMatches;
	}
	public String getApplicationNumberDisburse() {
		return applicationNumberDisburse;
	}
	public void setApplicationNumberDisburse(String applicationNumberDisburse) {
		this.applicationNumberDisburse = applicationNumberDisburse;
	}
	public String getElcFlag() {
		return elcFlag;
	}
	public void setElcFlag(String elcFlag) {
		this.elcFlag = elcFlag;
	}
	public double getElcLimit() {
		return elcLimit;
	}
	public void setElcLimit(double elcLimit) {
		this.elcLimit = elcLimit;
	}
	public String getRejectLANMatches() {
		return rejectLANMatches;
	}
	public void setRejectLANMatches(String rejectLANMatches) {
		this.rejectLANMatches = rejectLANMatches;
	}
	public String getFraudLANMatches() {
		return fraudLANMatches;
	}
	public void setFraudLANMatches(String fraudLANMatches) {
		this.fraudLANMatches = fraudLANMatches;
	}
	public double getCobrandLimit() {
		return cobrandLimit;
	}
	public void setCobrandLimit(double cobrandLimit) {
		this.cobrandLimit = cobrandLimit;
	}
	public double getCobrandAvailLimit() {
		return cobrandAvailLimit;
	}
	public void setCobrandAvailLimit(double cobrandAvailLimit) {
		this.cobrandAvailLimit = cobrandAvailLimit;
	}
	public double getCobrandCount() {
		return cobrandCount;
	}
	public void setCobrandCount(double cobrandCount) {
		this.cobrandCount = cobrandCount;
	}
	public String getWipLANMatches() {
		return wipLANMatches;
	}
	public void setWipLANMatches(String wipLANMatches) {
		this.wipLANMatches = wipLANMatches;
	}
	public String getRblLanMatches() {
		return rblLanMatches;
	}
	public void setRblLanMatches(String rblLanMatches) {
		this.rblLanMatches = rblLanMatches;
	}
	@Override
	public String toString() {
		return "CustomerStatusResponse [dealId=" + dealId + ", areaStatus="
				+ areaStatus + ", customerStatus=" + customerStatus
				+ ", customerID=" + customerID + ", applicationID="
				+ applicationID + ", dedupeLANMatches=" + dedupeLANMatches
				+ ", applicationNumberDisburse=" + applicationNumberDisburse
				+ ", elcFlag=" + elcFlag + ", elcLimit=" + elcLimit
				+ ", rejectLANMatches=" + rejectLANMatches
				+ ", fraudLANMatches=" + fraudLANMatches + ", cobrandLimit="
				+ cobrandLimit + ", cobrandAvailLimit=" + cobrandAvailLimit
				+ ", cobrandCount=" + cobrandCount + ", wipLANMatches="
				+ wipLANMatches + ", rblLanMatches=" + rblLanMatches + "]";
	}

	
}
