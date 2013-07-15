package com.pennant.backend.model.lmtmasters;

import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;

/**
 * Model class for the <b>LoanEligibility check</b>.<br>
 *
 */
public class LoanEligibility implements java.io.Serializable  {
	
	private static final long serialVersionUID = -7035477070254322004L;
	
	private long custId = Long.MIN_VALUE;
	private String ruleCode; 
	private Object expectedResult;
	private Object actualResult;
	private String status ;
	private String failureReason;
	private CustomerEligibilityCheck customerEligibilityCheck = new CustomerEligibilityCheck();
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustId(long custId) {
		this.custId = custId;
	}
	public long getCustId() {
		return custId;
	}
	
	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}
	public String getRuleCode() {
		return ruleCode;
	}
	
	public void setExpectedResult(Object expectedResult) {
		this.expectedResult = expectedResult;
	}
	public Object getExpectedResult() {
		return expectedResult;
	}
	
	public Object getActualResult() {
		return actualResult;
	}
	public void setActualResult(Object actualResult) {
		this.actualResult = actualResult;
	}
	
	public CustomerEligibilityCheck getCustomerEligibilityCheck() {
		return customerEligibilityCheck;
	}
	public void setCustomerEligibilityCheck(
			CustomerEligibilityCheck customerEligibilityCheck) {
		this.customerEligibilityCheck = customerEligibilityCheck;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatus() {
		return status;
	}
	
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	public String getFailureReason() {
		return failureReason;
	}

	/**
	 * Check object is equal or not with Other object
	 * 
	 *  @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof LoanEligibility) {
			LoanEligibility loanEligibility = (LoanEligibility) obj;
			return equals(loanEligibility);
		}
		return false;
	}

}
