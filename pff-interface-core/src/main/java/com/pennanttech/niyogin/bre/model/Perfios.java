package com.pennanttech.niyogin.bre.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "avgBankBalance", "inwardChequeReturns", "creditConcentrationInBankStatement",
		"minBalChargesReported", "odccLimit", "noOfCreditTransactions", "amtOfCreditTransactions",
		"noOfDebitTransactions", "amtOfDebtTransactions", "noOfCashDeposits", "amtOfCashDeposit", "intOdCc",
		"noOfEmiBounce", "noOfCashWithdrawls", "amtOfCashWithdrawls", "noOfChequeDeposits", "amtOfChequeDeposits",
		"totNoOfChequeIssues", "totAmtOfChequeIssues", "totalnoOfoutwardchequebounces", "minOdBalance", "maxOdBalance",
		"issueDateForGstnDoc", "totCredit" })
@XmlRootElement(name = "PERFIOS")
@XmlAccessorType(XmlAccessType.FIELD)
public class Perfios {

	@XmlElement(name = "AVERAGEBANKBALANCE")
	private BigDecimal				avgBankBalance						= BigDecimal.ZERO;

	@XmlElement(name = "INWARDCHEQUERETURNS")
	private int						inwardChequeReturns;

	@XmlElement(name = "CREDITCONCENTRATIONINBANKSTATEMENT")
	private BigDecimal				creditConcentrationInBankStatement	= BigDecimal.ZERO;

	@XmlElement(name = "MINIMUMBALANCECHARGESREPORTED")
	private BigDecimal				minBalChargesReported				= BigDecimal.ZERO;

	@XmlElement(name = "ODCCLIMIT")
	private BigDecimal				odccLimit							= BigDecimal.ZERO;

	//TODO:
	@XmlElement(name = "NOOFCREDITTRANSACTIONS")
	private NoOfCreditTransactions	noOfCreditTransactions;

	//TODO:
	@XmlElement(name = "AMOUNTOFCREDITTRANSACTIONS")
	private AmtOfCreditTransactions	amtOfCreditTransactions;

	@XmlElement(name = "NOOFDEBITTRANSACTIONS")
	private int						noOfDebitTransactions;

	//TODO:
	@XmlElement(name = "AMOUNTOFDEBITTRANSACTIONS")
	private AmtOfDebtTransactions	amtOfDebtTransactions;

	@XmlElement(name = "NOOFCASHDEPOSITS")
	private NoOfCashDeposits		noOfCashDeposits;

	@XmlElement(name = "AMOUNTOFCASHDEPOSIT")
	private AmtOfCashDeposit		amtOfCashDeposit;

	@XmlElement(name = "INTODCC")
	private int						intOdCc;

	@XmlElement(name = "NOOFEMIBOUNCE")
	private int						noOfEmiBounce;

	@XmlElement(name = "NOOFCASHWITHDRAWLS")
	private int						noOfCashWithdrawls;

	@XmlElement(name = "AMOUNTOFCASHWITHDRAWLS")
	private BigDecimal				amtOfCashWithdrawls					= BigDecimal.ZERO;

	@XmlElement(name = "NOOFCHEQUEDEPOSITS")
	private int						noOfChequeDeposits;

	@XmlElement(name = "AMOUNTOFCHEQUEDEPOSITS")
	private BigDecimal				amtOfChequeDeposits					= BigDecimal.ZERO;

	@XmlElement(name = "TOTALNOOFCHEQUEISSUES")
	private int						totNoOfChequeIssues;

	@XmlElement(name = "TOTALAMOUNTOFCHEQUEISSUES")
	private BigDecimal				totAmtOfChequeIssues				= BigDecimal.ZERO;

	@XmlElement(name = "TOTALNOOFOUTWARDCHEQUEBOUNCES")
	private int						totalnoOfoutwardchequebounces;

	@XmlElement(name = "MINEODBALANCE")
	private BigDecimal				minOdBalance						= BigDecimal.ZERO;

	@XmlElement(name = "MAXEODBALANCE")
	private BigDecimal				maxOdBalance						= BigDecimal.ZERO;

	@XmlElement(name = "ISSUEDATEFORGSTNDOCUMENT")
	private String					issueDateForGstnDoc;

	@XmlElement(name = "TOTALCREDIT")
	private BigDecimal				totCredit							= BigDecimal.ZERO;

	public BigDecimal getAvgBankBalance() {
		return avgBankBalance;
	}

	public void setAvgBankBalance(BigDecimal avgBankBalance) {
		this.avgBankBalance = avgBankBalance;
	}

	public int getInwardChequeReturns() {
		return inwardChequeReturns;
	}

	public void setInwardChequeReturns(int inwardChequeReturns) {
		this.inwardChequeReturns = inwardChequeReturns;
	}

	public BigDecimal getCreditConcentrationInBankStatement() {
		return creditConcentrationInBankStatement;
	}

	public void setCreditConcentrationInBankStatement(BigDecimal creditConcentrationInBankStatement) {
		this.creditConcentrationInBankStatement = creditConcentrationInBankStatement;
	}

	public BigDecimal getMinBalChargesReported() {
		return minBalChargesReported;
	}

	public void setMinBalChargesReported(BigDecimal minBalChargesReported) {
		this.minBalChargesReported = minBalChargesReported;
	}

	public BigDecimal getOdccLimit() {
		return odccLimit;
	}

	public void setOdccLimit(BigDecimal odccLimit) {
		this.odccLimit = odccLimit;
	}

	public NoOfCreditTransactions getNoOfCreditTransactions() {
		return noOfCreditTransactions;
	}

	public void setNoOfCreditTransactions(NoOfCreditTransactions noOfCreditTransactions) {
		this.noOfCreditTransactions = noOfCreditTransactions;
	}

	public AmtOfCreditTransactions getAmtOfCreditTransactions() {
		return amtOfCreditTransactions;
	}

	public void setAmtOfCreditTransactions(AmtOfCreditTransactions amtOfCreditTransactions) {
		this.amtOfCreditTransactions = amtOfCreditTransactions;
	}

	public int getNoOfDebitTransactions() {
		return noOfDebitTransactions;
	}

	public void setNoOfDebitTransactions(int noOfDebitTransactions) {
		this.noOfDebitTransactions = noOfDebitTransactions;
	}

	public AmtOfDebtTransactions getAmtOfDebtTransactions() {
		return amtOfDebtTransactions;
	}

	public void setAmtOfDebtTransactions(AmtOfDebtTransactions amtOfDebtTransactions) {
		this.amtOfDebtTransactions = amtOfDebtTransactions;
	}

	public NoOfCashDeposits getNoOfCashDeposits() {
		return noOfCashDeposits;
	}

	public void setNoOfCashDeposits(NoOfCashDeposits noOfCashDeposits) {
		this.noOfCashDeposits = noOfCashDeposits;
	}

	public AmtOfCashDeposit getAmtOfCashDeposit() {
		return amtOfCashDeposit;
	}

	public void setAmtOfCashDeposit(AmtOfCashDeposit amtOfCashDeposit) {
		this.amtOfCashDeposit = amtOfCashDeposit;
	}

	public int getIntOdCc() {
		return intOdCc;
	}

	public void setIntOdCc(int intOdCc) {
		this.intOdCc = intOdCc;
	}

	public int getNoOfEmiBounce() {
		return noOfEmiBounce;
	}

	public void setNoOfEmiBounce(int noOfEmiBounce) {
		this.noOfEmiBounce = noOfEmiBounce;
	}

	public int getNoOfCashWithdrawls() {
		return noOfCashWithdrawls;
	}

	public void setNoOfCashWithdrawls(int noOfCashWithdrawls) {
		this.noOfCashWithdrawls = noOfCashWithdrawls;
	}

	public BigDecimal getAmtOfCashWithdrawls() {
		return amtOfCashWithdrawls;
	}

	public void setAmtOfCashWithdrawls(BigDecimal amtOfCashWithdrawls) {
		this.amtOfCashWithdrawls = amtOfCashWithdrawls;
	}

	public int getNoOfChequeDeposits() {
		return noOfChequeDeposits;
	}

	public void setNoOfChequeDeposits(int noOfChequeDeposits) {
		this.noOfChequeDeposits = noOfChequeDeposits;
	}

	public BigDecimal getAmtOfChequeDeposits() {
		return amtOfChequeDeposits;
	}

	public void setAmtOfChequeDeposits(BigDecimal amtOfChequeDeposits) {
		this.amtOfChequeDeposits = amtOfChequeDeposits;
	}

	public int getTotNoOfChequeIssues() {
		return totNoOfChequeIssues;
	}

	public void setTotNoOfChequeIssues(int totNoOfChequeIssues) {
		this.totNoOfChequeIssues = totNoOfChequeIssues;
	}

	public BigDecimal getTotAmtOfChequeIssues() {
		return totAmtOfChequeIssues;
	}

	public void setTotAmtOfChequeIssues(BigDecimal totAmtOfChequeIssues) {
		this.totAmtOfChequeIssues = totAmtOfChequeIssues;
	}

	public int getTotalnoOfoutwardchequebounces() {
		return totalnoOfoutwardchequebounces;
	}

	public void setTotalnoOfoutwardchequebounces(int totalnoOfoutwardchequebounces) {
		this.totalnoOfoutwardchequebounces = totalnoOfoutwardchequebounces;
	}

	public BigDecimal getMinOdBalance() {
		return minOdBalance;
	}

	public void setMinOdBalance(BigDecimal minOdBalance) {
		this.minOdBalance = minOdBalance;
	}

	public BigDecimal getMaxOdBalance() {
		return maxOdBalance;
	}

	public void setMaxOdBalance(BigDecimal maxOdBalance) {
		this.maxOdBalance = maxOdBalance;
	}

	public String getIssueDateForGstnDoc() {
		return issueDateForGstnDoc;
	}

	public void setIssueDateForGstnDoc(String issueDateForGstnDoc) {
		this.issueDateForGstnDoc = issueDateForGstnDoc;
	}

	public BigDecimal getTotCredit() {
		return totCredit;
	}

	public void setTotCredit(BigDecimal totCredit) {
		this.totCredit = totCredit;
	}

}
