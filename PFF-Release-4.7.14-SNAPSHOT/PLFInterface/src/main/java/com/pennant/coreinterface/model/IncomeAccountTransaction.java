package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class IncomeAccountTransaction implements Serializable { 

	private static final long serialVersionUID = -1666538772911173673L;

	public IncomeAccountTransaction() {
		super();
	}

	private String incomeAccount;
	private BigDecimal profitAmount;
	private BigDecimal manualAmount;
	private BigDecimal pffPostingAmount;
	private Date lastMntOn;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//


	public String getIncomeAccount() {
		return incomeAccount;
	}
	public void setIncomeAccount(String incomeAccount) {
		this.incomeAccount = incomeAccount;
	}

	public BigDecimal getProfitAmount() {
		return profitAmount;
	}
	public void setProfitAmount(BigDecimal profitAmount) {
		this.profitAmount = profitAmount;
	}

	public BigDecimal getManualAmount() {
		return manualAmount;
	}
	public void setManualAmount(BigDecimal manualAmount) {
		this.manualAmount = manualAmount;
	}

	public BigDecimal getPffPostingAmount() {
		return pffPostingAmount;
	}
	public void setPffPostingAmount(BigDecimal pffPostingAmount) {
		this.pffPostingAmount = pffPostingAmount;
	}

	public Date getLastMntOn() {
		return lastMntOn;
	}
	public void setLastMntOn(Date lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

}
