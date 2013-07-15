package com.pennant.backend.model.finance;

public class AgreementDetail {
	private String dayOfContarctDate = "";
	private String monthOfContarctDate = "";
	private String yearOfContarctDate = "";
	private String custSalutation = "";
	private String custFullName = "";
	private String custCIF = "";
	private String bankName = "";
	private String finCcy = "";
	private String finAmount = "";
	private String contractDate = "";
	private String agentName = "";
	private String agentAddr1 = "";
	private String agentAddr2 = "";
	private String agentCity = "";
	private String agentCountry = "";
	private String disbursementAmt;
	private String financeStartDate;
	private String disbursementAccount;

	public String getDayOfContarctDate() {
		return dayOfContarctDate;
	}

	public void setDayOfContarctDate(String dayOfContarctDate) {
		this.dayOfContarctDate = dayOfContarctDate;
	}

	public String getMonthOfContarctDate() {
		return monthOfContarctDate;
	}

	public void setMonthOfContarctDate(String monthOfContarctDate) {
		this.monthOfContarctDate = monthOfContarctDate;
	}

	public String getYearOfContarctDate() {
		return yearOfContarctDate;
	}

	public void setYearOfContarctDate(String yearOfContarctDate) {
		this.yearOfContarctDate = yearOfContarctDate;
	}

	public String getCustSalutation() {
		return custSalutation;
	}

	public void setCustSalutation(String custSalutation) {
		this.custSalutation = custSalutation;
	}

	public String getCustFullName() {
		return custFullName;
	}

	public void setCustFullName(String custFullName) {
		this.custFullName = custFullName;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(String finAmount) {
		this.finAmount = finAmount;
	}

	public String getContractDate() {
		return contractDate;
	}

	public void setContractDate(String contractDate) {
		this.contractDate = contractDate;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentAddr1() {
		return agentAddr1;
	}

	public void setAgentAddr1(String agentAddr1) {
		this.agentAddr1 = agentAddr1;
	}

	public String getAgentAddr2() {
		return agentAddr2;
	}

	public void setAgentAddr2(String agentAddr2) {
		this.agentAddr2 = agentAddr2;
	}

	public String getAgentCity() {
		return agentCity;
	}

	public void setAgentCity(String agentCity) {
		this.agentCity = agentCity;
	}

	public String getAgentCountry() {
		return agentCountry;
	}

	public void setAgentCountry(String agentCountry) {
		this.agentCountry = agentCountry;
	}

	public void setDisbursementAccount(String disbursementAccount) {
	    this.disbursementAccount = disbursementAccount;
    }

	public String getDisbursementAccount() {
	    return disbursementAccount;
    }



	public void setDisbursementAmt(String disbursementAmt) {
	    this.disbursementAmt = disbursementAmt;
    }

	public String getDisbursementAmt() {
	    return disbursementAmt;
    }

	public void setFinanceStartDate(String financeStartDate) {
	    this.financeStartDate = financeStartDate;
    }

	public String getFinanceStartDate() {
	    return financeStartDate;
    }

}
