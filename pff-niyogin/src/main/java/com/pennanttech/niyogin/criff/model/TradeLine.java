package com.pennanttech.niyogin.criff.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "accountNo", "creditGrantor", "lastReportedDate", "assetClassification", "currentBalance", "dpd",
		"creditFacilityType", "creditFacilityStatus", "sanctionedAmount", "sanctionDate", "drawingPower",
		"disbursedAmount", "overdueAmount", "issuedCurrency", "suitFiledAndWilfulDefaults", "borrowerName",
		"paymentHistory", "accountStatus", "lenderType", "writeoffAmount", "creditFacilityGroup", "derivedAccStatus" })
@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
public class TradeLine implements Serializable {

	private static final long	serialVersionUID	= -4900876489170496613L;

	@XmlElement(name = "ACCOUNT-NO")
	private String				accountNo;

	@XmlElement(name = "CREDIT-GRANTOR")
	private String				creditGrantor;

	@XmlElement(name = "LAST-REPORTED-DATE")
	private Date				lastReportedDate;

	@XmlElement(name = "ASSET-CLASSIFICATION")
	private String				assetClassification;

	@XmlElement(name = "CURRENT-BALANCE")
	private BigDecimal			currentBalance		= BigDecimal.ZERO;

	@XmlElement(name = "DPD")
	private String				dpd;

	@XmlElement(name = "CREDIT-FACILITY-TYPE")
	private String				creditFacilityType;

	@XmlElement(name = "CREDIT-FACILITY-STATUS")
	private String				creditFacilityStatus;

	@XmlElement(name = "SANCTIONED-AMOUNT")
	private BigDecimal			sanctionedAmount	= BigDecimal.ZERO;

	@XmlElement(name = "SANCTION-DATE")
	private Date				sanctionDate;

	@XmlElement(name = "DRAWING-POWER")
	private String				drawingPower;

	@XmlElement(name = "DISBURSED-AMOUNT")
	private BigDecimal			disbursedAmount		= BigDecimal.ZERO;

	@XmlElement(name = "OVERDUE-AMOUNT")
	private BigDecimal			overdueAmount		= BigDecimal.ZERO;

	@XmlElement(name = "ISSUED-CURRENCY")
	private String				issuedCurrency;

	@XmlElement(name = "SUIT-FILED-AND-WILFUL-DEFAULTS")
	private SuitfiledAndWilful	suitFiledAndWilfulDefaults;

	@XmlElement(name = "BORROWER-NAME")
	private String				borrowerName;

	@XmlElement(name = "PAYMENT-HISTORY")
	private String				paymentHistory;

	@XmlElement(name = "ACCOUNT-STATUS")
	private String				accountStatus;

	@XmlElement(name = "LENDER-TYPE")
	private String				lenderType;

	@XmlElement(name = "WRITEOFF-AMOUNT")
	private BigDecimal			writeoffAmount		= BigDecimal.ZERO;

	@XmlElement(name = "CREDIT-FACILITY-GROUP")
	private String				creditFacilityGroup;

	@XmlElement(name = "DERIVED-ACCOUNT-STATUS")
	private String				derivedAccStatus;

	public String getDerivedAccStatus() {
		return derivedAccStatus;
	}

	public void setDerivedAccStatus(String derivedAccStatus) {
		this.derivedAccStatus = derivedAccStatus;
	}

	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}

	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}

	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}

	public String getLenderType() {
		return lenderType;
	}

	public void setLenderType(String lenderType) {
		this.lenderType = lenderType;
	}

	public String getIssuedCurrency() {
		return issuedCurrency;
	}

	public void setIssuedCurrency(String issuedCurrency) {
		this.issuedCurrency = issuedCurrency;
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public String getDpd() {
		return dpd;
	}

	public void setDpd(String dpd) {
		this.dpd = dpd;
	}

	public Date getSanctionDate() {
		return sanctionDate;
	}

	public void setSanctionDate(Date sanctionDate) {
		this.sanctionDate = sanctionDate;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public BigDecimal getWriteoffAmount() {
		return writeoffAmount;
	}

	public void setWriteoffAmount(BigDecimal writeoffAmount) {
		this.writeoffAmount = writeoffAmount;
	}

	public String getCreditFacilityType() {
		return creditFacilityType;
	}

	public void setCreditFacilityType(String creditFacilityType) {
		this.creditFacilityType = creditFacilityType;
	}

	public SuitfiledAndWilful getSuitFiledAndWilfulDefaults() {
		return suitFiledAndWilfulDefaults;
	}

	public void setSuitFiledAndWilfulDefaults(SuitfiledAndWilful suitFiledAndWilfulDefaults) {
		this.suitFiledAndWilfulDefaults = suitFiledAndWilfulDefaults;
	}

	public String getCreditGrantor() {
		return creditGrantor;
	}

	public void setCreditGrantor(String creditGrantor) {
		this.creditGrantor = creditGrantor;
	}

	public String getCreditFacilityStatus() {
		return creditFacilityStatus;
	}

	public void setCreditFacilityStatus(String creditFacilityStatus) {
		this.creditFacilityStatus = creditFacilityStatus;
	}

	public String getCreditFacilityGroup() {
		return creditFacilityGroup;
	}

	public void setCreditFacilityGroup(String creditFacilityGroup) {
		this.creditFacilityGroup = creditFacilityGroup;
	}

	public BigDecimal getDisbursedAmount() {
		return disbursedAmount;
	}

	public void setDisbursedAmount(BigDecimal disbursedAmount) {
		this.disbursedAmount = disbursedAmount;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Date getLastReportedDate() {
		return lastReportedDate;
	}

	public void setLastReportedDate(Date lastReportedDate) {
		this.lastReportedDate = lastReportedDate;
	}

	public BigDecimal getSanctionedAmount() {
		return sanctionedAmount;
	}

	public void setSanctionedAmount(BigDecimal sanctionedAmount) {
		this.sanctionedAmount = sanctionedAmount;
	}

	public String getDrawingPower() {
		return drawingPower;
	}

	public void setDrawingPower(String drawingPower) {
		this.drawingPower = drawingPower;
	}

	public String getPaymentHistory() {
		return paymentHistory;
	}

	public void setPaymentHistory(String paymentHistory) {
		this.paymentHistory = paymentHistory;
	}

	public String getAssetClassification() {
		return assetClassification;
	}

	public void setAssetClassification(String assetClassification) {
		this.assetClassification = assetClassification;
	}

}
