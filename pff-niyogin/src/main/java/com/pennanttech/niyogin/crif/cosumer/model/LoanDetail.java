package com.pennanttech.niyogin.crif.cosumer.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "acctNumber", "creditGuarantor", "acctType", "dateReported", "ownershipInd", "accountStatus",
		"disbursedAmt", "disbursedDate", "lastPaymentDate","closedDate", "overdueAmt", "writeOffAmt", "currentBal",
		"combinedPaymentHistory", "matchedType", "infoAsOn", "interestRate", "linkedAccounts", "securityDetails" })
@XmlAccessorType(XmlAccessType.FIELD)
public class LoanDetail  implements Serializable{

	private static final long serialVersionUID = 6712137280931986929L;

	@XmlElement(name = "ACCT-NUMBER")
	private String acctNumber;
	
	@XmlElement(name = "CREDIT-GUARANTOR")
	private String creditGuarantor;

	@XmlElement(name = "ACCT-TYPE")
	private String acctType;

	@XmlElement(name = "DATE-REPORTED")
	private Date dateReported;
	
	@XmlElement(name = "OWNERSHIP-IND")
	private String ownershipInd;

	@XmlElement(name = "ACCOUNT-STATUS")
	private String accountStatus;


	@XmlElement(name = "DISBURSED-AMT")
	private String disbursedAmt;
	
	
	@XmlElement(name = "DISBURSED-DATE")
	private Date disbursedDate;
	
	@XmlElement(name = "LAST-PAYMENT-DATE")
	private Date lastPaymentDate;

	@XmlElement(name = "CLOSED-DATE")
	private Date closedDate;

	@XmlElement(name = "OVERDUE-AMT")
	private String overdueAmt;

	@XmlElement(name = "WRITE-OFF-AMT")
	private String writeOffAmt;

	@XmlElement(name = "CURRENT-BAL")
	private String currentBal;

	@XmlElement(name = "COMBINED-PAYMENT-HISTORY")
	private String combinedPaymentHistory;

	@XmlElement(name = "MATCHED-TYPE")
	private String matchedType;

	@XmlElement(name = "INFO-AS-ON")
	private Date infoAsOn;

	private String	interestRate;
	
	@XmlElement(name = "LINKED-ACCOUNTS")
	private List<LinkedAccounts> linkedAccounts;

	@XmlElement(name = "SECURITY-DETAILS")
	private List<SecurityDetails> securityDetails;

	public String getAcctNumber() {
		return acctNumber;
	}

	public void setAcctNumber(String acctNumber) {
		this.acctNumber = acctNumber;
	}

	public String getCreditGuarantor() {
		return creditGuarantor;
	}

	public void setCreditGuarantor(String creditGuarantor) {
		this.creditGuarantor = creditGuarantor;
	}

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}

	public Date getDateReported() {
		return dateReported;
	}

	public void setDateReported(Date dateReported) {
		this.dateReported = dateReported;
	}

	public String getOwnershipInd() {
		return ownershipInd;
	}

	public void setOwnershipInd(String ownershipInd) {
		this.ownershipInd = ownershipInd;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getDisbursedAmt() {
		return disbursedAmt;
	}

	public void setDisbursedAmt(String disbursedAmt) {
		this.disbursedAmt = disbursedAmt;
	}

	public Date getDisbursedDate() {
		return disbursedDate;
	}

	public void setDisbursedDate(Date disbursedDate) {
		this.disbursedDate = disbursedDate;
	}

	public Date getLastPaymentDate() {
		return lastPaymentDate;
	}

	public void setLastPaymentDate(Date lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public String getOverdueAmt() {
		return overdueAmt;
	}

	public void setOverdueAmt(String overdueAmt) {
		this.overdueAmt = overdueAmt;
	}

	public String getWriteOffAmt() {
		return writeOffAmt;
	}

	public void setWriteOffAmt(String writeOffAmt) {
		this.writeOffAmt = writeOffAmt;
	}

	public String getCurrentBal() {
		return currentBal;
	}

	public void setCurrentBal(String currentBal) {
		this.currentBal = currentBal;
	}

	public String getCombinedPaymentHistory() {
		return combinedPaymentHistory;
	}

	public void setCombinedPaymentHistory(String combinedPaymentHistory) {
		this.combinedPaymentHistory = combinedPaymentHistory;
	}

	public String getMatchedType() {
		return matchedType;
	}

	public void setMatchedType(String matchedType) {
		this.matchedType = matchedType;
	}

	public Date getInfoAsOn() {
		return infoAsOn;
	}

	public void setInfoAsOn(Date infoAsOn) {
		this.infoAsOn = infoAsOn;
	}

	public String getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}

	public List<LinkedAccounts> getLinkedAccounts() {
		return linkedAccounts;
	}

	public void setLinkedAccounts(List<LinkedAccounts> linkedAccounts) {
		this.linkedAccounts = linkedAccounts;
	}

	public List<SecurityDetails> getSecurityDetails() {
		return securityDetails;
	}

	public void setSecurityDetails(List<SecurityDetails> securityDetails) {
		this.securityDetails = securityDetails;
	}

	@Override
	public String toString() {
		return "LoanDetails [acctNumber=" + acctNumber + ", creditGuarantor=" + creditGuarantor + ", acctType="
				+ acctType + ", dateReported=" + dateReported + ", ownershipInd=" + ownershipInd + ", accountStatus="
				+ accountStatus + ", disbursedAmt=" + disbursedAmt + ", disbursedDate=" + disbursedDate
				+ ", lastPaymentDate=" + lastPaymentDate + ", closedDate=" + closedDate + ", overdueAmt=" + overdueAmt
				+ ", writeOffAmt=" + writeOffAmt + ", currentBal=" + currentBal + ", combinedPaymentHistory="
				+ combinedPaymentHistory + ", matchedType=" + matchedType + ", infoAsOn=" + infoAsOn + ", interestRate="
				+ interestRate + ", linkedAccounts=" + linkedAccounts + ", securityDetails=" + securityDetails + "]";
	}

}
