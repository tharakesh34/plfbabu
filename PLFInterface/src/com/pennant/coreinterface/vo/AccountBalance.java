package com.pennant.coreinterface.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class AccountBalance  implements Serializable {
	
	private static final long serialVersionUID = -1666538772911173673L;
	
	private String repayAccount;
	private BigDecimal accBalance;
	private String acHoldStatus;
	private String StatusDesc;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getRepayAccount() {
		return repayAccount;
	}
	public void setRepayAccount(String repayAccount) {
		this.repayAccount = repayAccount;
	}
	
	public BigDecimal getAccBalance() {
		return accBalance;
	}
	public void setAccBalance(BigDecimal accBalance) {
		this.accBalance = accBalance;
	}
	
	public String getAcHoldStatus() {
		return acHoldStatus;
	}
	public void setAcHoldStatus(String acHoldStatus) {
		this.acHoldStatus = acHoldStatus;
	}
	
	public String getStatusDesc() {
		return StatusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		StatusDesc = statusDesc;
	}
	
}
