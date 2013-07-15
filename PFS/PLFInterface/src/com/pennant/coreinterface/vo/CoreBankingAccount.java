package com.pennant.coreinterface.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class CoreBankingAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accountBranch;
	private String accountBasic;
	private String accountSuffix;
	private String accountNumber;
	private String accountShortName;
	private String customerNumber;
	private String customerLocation;
	private String customerName;
	private String accountType;
	private String accountCurrency;
	private String accountCurrencyEdit;
	private String accountDeceased;
	private String accountBlocked;
	private String accountInactive;
	private String accountClosed;
	private String accountBalance;
	private String customerType;
	private String analysisCode;
	private String sundryCode;
	private BigDecimal accountOpenDate;

	
	public CoreBankingAccount() {
		super();
	}

	public String getAccountBranch() {
		return accountBranch;
	}

	public void setAccountBranch(String accountBranch) {
		this.accountBranch = accountBranch;
	}

	public String getAccountBasic() {
		return accountBasic;
	}

	public void setAccountBasic(String accountBasic) {
		this.accountBasic = accountBasic;
	}

	public String getAccountSuffix() {
		return accountSuffix;
	}

	public void setAccountSuffix(String accountSuffix) {
		this.accountSuffix = accountSuffix;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountShortName() {
		return accountShortName;
	}

	public void setAccountShortName(String accountShortName) {
		this.accountShortName = accountShortName;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getCustomerLocation() {
		return customerLocation;
	}

	public void setCustomerLocation(String customerLocation) {
		this.customerLocation = customerLocation;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountCurrency() {
		return accountCurrency;
	}

	public void setAccountCurrency(String accountCurrency) {
		this.accountCurrency = accountCurrency;
	}

	public String getAccountCurrencyEdit() {
		return accountCurrencyEdit;
	}

	public void setAccountCurrencyEdit(String accountCurrencyEdit) {
		this.accountCurrencyEdit = accountCurrencyEdit;
	}

	public String getAccountDeceased() {
		return accountDeceased;
	}

	public void setAccountDeceased(String accountDeceased) {
		this.accountDeceased = accountDeceased;
	}

	public String getAccountBlocked() {
		return accountBlocked;
	}

	public void setAccountBlocked(String accountBlocked) {
		this.accountBlocked = accountBlocked;
	}

	public String getAccountInactive() {
		return accountInactive;
	}

	public void setAccountInactive(String accountInactive) {
		this.accountInactive = accountInactive;
	}

	public String getAccountClosed() {
		return accountClosed;
	}

	public void setAccountClosed(String accountClosed) {
		this.accountClosed = accountClosed;
	}

	public String getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(String accountBalance) {
		this.accountBalance = accountBalance;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getAnalysisCode() {
		return analysisCode;
	}

	public void setAnalysisCode(String analysisCode) {
		this.analysisCode = analysisCode;
	}

	public String getSundryCode() {
		return sundryCode;
	}

	public void setSundryCode(String sundryCode) {
		this.sundryCode = sundryCode;
	}

	public BigDecimal getAccountOpenDate() {
		return accountOpenDate;
	}

	public void setAccountOpenDate(BigDecimal accountOpenDate) {
		this.accountOpenDate = accountOpenDate;
	}

}
