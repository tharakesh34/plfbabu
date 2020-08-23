package com.penanttech.pff.model.external.bre;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankingElement {
	@JsonProperty("BANK_NAME")
	private String bankName;
	@JsonProperty("BANK_BRANCH")
	private String bankBranch;
	@JsonProperty("BANK_ACCOUNT_NO")
	private String bankAccountNo;
	@JsonProperty("AVERAGE_BANK_BALANCE")
	private String averageBankBalance;
	@JsonProperty("AVERAGE_MONTHLY_CREDITS")
	private String averageMonthlyCredits;
	@JsonProperty("CONSIDER_INCOME_BANKING")
	private String considerIncomeBanking;
	@JsonProperty("PRIMARY_ACCOUNT")
	private String primaryAccount;
	@JsonProperty("REPAYMENT_FROM_THIS_ACCOUNT")
	private String repaymentFromThisAccount;
	@JsonProperty("ACCOUNT_TYPE_SBCA")
	private String accountTypeSbca;
	@JsonProperty("FINANCIAL_INSTITUTION")
	private String financialInstitution;
	@JsonProperty("NO_DEBIT")
	private String noDebit;
	@JsonProperty("NO_CREDIT")
	private String noCredit;
	@JsonProperty("INWARD_CHEQUE_BOUNCE")
	private String inwardChequeBounce;
	@JsonProperty("OUTWARD_CHEQUE_BOUNCE")
	private String outwardChequeBounce;
	@JsonProperty("TOTAL_CR")
	private String totalCr;
	@JsonProperty("TOTAL_DR")
	private String totalDr;
	@JsonProperty("INWARD_CHEQUE_RETURN_COUNT")
	private String inwardChequeReturnCount;
	@JsonProperty("OUTWARD_CHEQUE_RETURN_COUNT")
	private String outwardChequeReturnCount;
	@JsonProperty("TOTAL_DEBIT_ENTRY")
	private String totalDebitEntry;
	@JsonProperty("TOTAL_CREDIT_ENTRY")
	private String totalCreditEntry;
	
	
	@JsonCreator
	public BankingElement() {
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
