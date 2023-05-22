package com.penanttech.pff.model.external.bre;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankingElement {
	@XmlElement(name = "BANK_NAME")
	private String bankName;
	@XmlElement(name = "BANK_BRANCH")
	private String bankBranch;
	@XmlElement(name = "BANK_ACCOUNT_NO")
	private String bankAccountNo;
	@XmlElement(name = "AVERAGE_BANK_BALANCE")
	private String averageBankBalance;
	@XmlElement(name = "AVERAGE_MONTHLY_CREDITS")
	private String averageMonthlyCredits;
	@XmlElement(name = "CONSIDER_INCOME_BANKING")
	private String considerIncomeBanking;
	@XmlElement(name = "PRIMARY_ACCOUNT")
	private String primaryAccount;
	@XmlElement(name = "REPAYMENT_FROM_THIS_ACCOUNT")
	private String repaymentFromThisAccount;
	@XmlElement(name = "ACCOUNT_TYPE_SBCA")
	private String accountTypeSbca;
	@XmlElement(name = "FINANCIAL_INSTITUTION")
	private String financialInstitution;
	@XmlElement(name = "NO_DEBIT")
	private String noDebit;
	@XmlElement(name = "NO_CREDIT")
	private String noCredit;
	@XmlElement(name = "INWARD_CHEQUE_BOUNCE")
	private String inwardChequeBounce;
	@XmlElement(name = "OUTWARD_CHEQUE_BOUNCE")
	private String outwardChequeBounce;
	@XmlElement(name = "TOTAL_CR")
	private String totalCr;
	@XmlElement(name = "TOTAL_DR")
	private String totalDr;
	@XmlElement(name = "INWARD_CHEQUE_RETURN_COUNT")
	private String inwardChequeReturnCount;
	@XmlElement(name = "OUTWARD_CHEQUE_RETURN_COUNT")
	private String outwardChequeReturnCount;
	@XmlElement(name = "TOTAL_DEBIT_ENTRY")
	private String totalDebitEntry;
	@XmlElement(name = "TOTAL_CREDIT_ENTRY")
	private String totalCreditEntry;

	@JsonCreator
	public BankingElement() {
	    super();
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getBankAccountNo() {
		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}

	public String getAverageBankBalance() {
		return averageBankBalance;
	}

	public void setAverageBankBalance(String averageBankBalance) {
		this.averageBankBalance = averageBankBalance;
	}

	public String getAverageMonthlyCredits() {
		return averageMonthlyCredits;
	}

	public void setAverageMonthlyCredits(String averageMonthlyCredits) {
		this.averageMonthlyCredits = averageMonthlyCredits;
	}

	public String getConsiderIncomeBanking() {
		return considerIncomeBanking;
	}

	public void setConsiderIncomeBanking(String considerIncomeBanking) {
		this.considerIncomeBanking = considerIncomeBanking;
	}

	public String getPrimaryAccount() {
		return primaryAccount;
	}

	public void setPrimaryAccount(String primaryAccount) {
		this.primaryAccount = primaryAccount;
	}

	public String getRepaymentFromThisAccount() {
		return repaymentFromThisAccount;
	}

	public void setRepaymentFromThisAccount(String repaymentFromThisAccount) {
		this.repaymentFromThisAccount = repaymentFromThisAccount;
	}

	public String getAccountTypeSbca() {
		return accountTypeSbca;
	}

	public void setAccountTypeSbca(String accountTypeSbca) {
		this.accountTypeSbca = accountTypeSbca;
	}

	public String getFinancialInstitution() {
		return financialInstitution;
	}

	public void setFinancialInstitution(String financialInstitution) {
		this.financialInstitution = financialInstitution;
	}

	public String getNoDebit() {
		return noDebit;
	}

	public void setNoDebit(String noDebit) {
		this.noDebit = noDebit;
	}

	public String getNoCredit() {
		return noCredit;
	}

	public void setNoCredit(String noCredit) {
		this.noCredit = noCredit;
	}

	public String getInwardChequeBounce() {
		return inwardChequeBounce;
	}

	public void setInwardChequeBounce(String inwardChequeBounce) {
		this.inwardChequeBounce = inwardChequeBounce;
	}

	public String getOutwardChequeBounce() {
		return outwardChequeBounce;
	}

	public void setOutwardChequeBounce(String outwardChequeBounce) {
		this.outwardChequeBounce = outwardChequeBounce;
	}

	public String getTotalCr() {
		return totalCr;
	}

	public void setTotalCr(String totalCr) {
		this.totalCr = totalCr;
	}

	public String getTotalDr() {
		return totalDr;
	}

	public void setTotalDr(String totalDr) {
		this.totalDr = totalDr;
	}

	public String getInwardChequeReturnCount() {
		return inwardChequeReturnCount;
	}

	public void setInwardChequeReturnCount(String inwardChequeReturnCount) {
		this.inwardChequeReturnCount = inwardChequeReturnCount;
	}

	public String getOutwardChequeReturnCount() {
		return outwardChequeReturnCount;
	}

	public void setOutwardChequeReturnCount(String outwardChequeReturnCount) {
		this.outwardChequeReturnCount = outwardChequeReturnCount;
	}

	public String getTotalDebitEntry() {
		return totalDebitEntry;
	}

	public void setTotalDebitEntry(String totalDebitEntry) {
		this.totalDebitEntry = totalDebitEntry;
	}

	public String getTotalCreditEntry() {
		return totalCreditEntry;
	}

	public void setTotalCreditEntry(String totalCreditEntry) {
		this.totalCreditEntry = totalCreditEntry;
	}

}
