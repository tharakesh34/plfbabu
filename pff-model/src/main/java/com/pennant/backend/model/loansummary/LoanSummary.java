package com.pennant.backend.model.loansummary;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.pennant.backend.model.finance.FinODDetails;

@XmlRootElement(name = "loanSummary")
@XmlAccessorType(XmlAccessType.NONE)
public class LoanSummary {

	private long finID;
	private String finReference;
	@XmlElement(name = "loanPrincipal")
	private BigDecimal totalPriSchd;
	@XmlElement(name = "paidPft")
	private BigDecimal schdPftPaid;
	@XmlElement(name = "paidPri")
	private BigDecimal schdPriPaid;
	@XmlElement
	private BigDecimal totalCpz;
	@XmlElement(name = "DPD")
	private Integer finCurODDays;
	@XmlElement(name = "DPDString")
	private String dPDString;
	@XmlElement
	private Date nextSchDate;
	@XmlElement
	private Date maturityDate;
	@XmlElement(name = "finActiveStatus")
	private String finStatus;
	@XmlElement(name = "lastRepayDate")
	private Date finLastRepayDate;
	@XmlElement(name = "outstandingPri")
	private BigDecimal outStandPrincipal;
	@XmlElement(name = "outstandingPft")
	private BigDecimal outStandProfit;
	@XmlElement(name = "outstandingTotal")
	private BigDecimal totalOutStanding;
	@XmlElement(name = "overduePri")
	private BigDecimal overDuePrincipal;
	@XmlElement(name = "overduePft")
	private BigDecimal overDueProfit;
	@XmlElement(name = "overdueTotal")
	private BigDecimal totalOverDue;
	@XmlElement
	private BigDecimal overDueCharges;
	@XmlElement(name = "overdueTotalIncludeCharges")
	private BigDecimal totalOverDueIncCharges;
	@XmlElement(name = "overdueInst")
	private Long overDueInstlments;
	@XmlElement
	private Long numberOfTerms;
	@XmlElement
	private BigDecimal totalRepayAmt;
	@XmlElement
	private BigDecimal effectiveRateOfReturn;
	@XmlElement
	private BigDecimal totalGracePft;
	@XmlElement
	private BigDecimal totalGraceCpz;
	@XmlElement
	private BigDecimal totalGrossGrcPft;
	@XmlElement
	private BigDecimal totalProfit;
	@XmlElement(name = "feeChargeAmount")
	private BigDecimal feeChargeAmt;
	@XmlElement
	private Integer loanTenor;
	@XmlElement
	private Date firstDisbDate;
	@XmlElement
	private Date lastDisbDate;
	@XmlElement
	private BigDecimal nextRepayAmount;
	@XmlElement
	private BigDecimal firstEmiAmount;
	@XmlElement(name = "loanBalanceTenure")
	private Integer futureInst;
	@XmlElement
	private Integer futureTenor;
	@XmlElement
	private Date firstInstDate;
	@XmlElement
	private BigDecimal paidTotal;
	@XmlElement
	private BigDecimal advPaymentAmount;
	@XmlElementWrapper(name = "overdueCharges")
	@XmlElement(name = "overdueCharge")
	private List<FinODDetails> finODDetail;
	@XmlElement
	private Boolean fullyDisb;
	@XmlElement(name = "limitBalance")
	private BigDecimal sanctionAmt;
	@XmlElement(name = "billedAmount")
	private BigDecimal utilizedAmt;
	@XmlElement(name = "unbilledAmount")
	private BigDecimal availableAmt;
	@XmlElement
	private BigDecimal dueCharges;
	@XmlElement
	private BigDecimal overDueAmount;
	@XmlElement(name = "loanInstallmentAmt")
	private BigDecimal loanEMI;
	@XmlElement(name = "loanForeclosureAmount")
	private BigDecimal foreClosureAmount;
	@XmlElement(name = "loanInstallmentNo")
	private Integer installmentNo;
	@XmlElement(name = "loanDueDate")
	private Date dueDate;
	@XmlElement
	private BigDecimal loanTotPrincipal;
	@XmlElement
	private BigDecimal loanTotInterest;
	@XmlElement
	private BigDecimal overDueEMI;
	@XmlElement
	private String vehicleNo;
	@XmlElement
	private String migratedNo;
	@XmlElement
	private Date lastInstDate;
	@XmlElement
	private String assetCost;
	@XmlElement
	private String assetModel;
	@XmlElement
	private String categoryID;
	@XmlElement
	private String categoryDesc;
	@XmlElement
	private BigDecimal interestComponent;
	@XmlElement
	private BigDecimal principalComponent;
	@XmlElement
	private BigDecimal totalAmount;

	public LoanSummary() {
		super();
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getTotalPriSchd() {
		return totalPriSchd;
	}

	public void setTotalPriSchd(BigDecimal totalPriSchd) {
		this.totalPriSchd = totalPriSchd;
	}

	public BigDecimal getSchdPftPaid() {
		return schdPftPaid;
	}

	public void setSchdPftPaid(BigDecimal schdPftPaid) {
		this.schdPftPaid = schdPftPaid;
	}

	public BigDecimal getSchdPriPaid() {
		return schdPriPaid;
	}

	public void setSchdPriPaid(BigDecimal schdPriPaid) {
		this.schdPriPaid = schdPriPaid;
	}

	public BigDecimal getTotalCpz() {
		return totalCpz;
	}

	public void setTotalCpz(BigDecimal totalCpz) {
		this.totalCpz = totalCpz;
	}

	public Integer getFinCurODDays() {
		return finCurODDays;
	}

	public void setFinCurODDays(Integer finCurODDays) {
		this.finCurODDays = finCurODDays;
	}

	public Date getNextSchDate() {
		return nextSchDate;
	}

	public void setNextSchDate(Date nextSchDate) {
		this.nextSchDate = nextSchDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getFinStatus() {
		return finStatus;
	}

	public void setFinStatus(String finStatus) {
		this.finStatus = finStatus;
	}

	public Date getFinLastRepayDate() {
		return finLastRepayDate;
	}

	public void setFinLastRepayDate(Date finLastRepayDate) {
		this.finLastRepayDate = finLastRepayDate;
	}

	public BigDecimal getOutStandPrincipal() {
		return outStandPrincipal;
	}

	public void setOutStandPrincipal(BigDecimal outStandPrincipal) {
		this.outStandPrincipal = outStandPrincipal;
	}

	public BigDecimal getOutStandProfit() {
		return outStandProfit;
	}

	public void setOutStandProfit(BigDecimal outStandProfit) {
		this.outStandProfit = outStandProfit;
	}

	public BigDecimal getTotalOutStanding() {
		return totalOutStanding;
	}

	public void setTotalOutStanding(BigDecimal totalOutStanding) {
		this.totalOutStanding = totalOutStanding;
	}

	public BigDecimal getOverDuePrincipal() {
		return overDuePrincipal;
	}

	public void setOverDuePrincipal(BigDecimal overDuePrincipal) {
		this.overDuePrincipal = overDuePrincipal;
	}

	public BigDecimal getOverDueProfit() {
		return overDueProfit;
	}

	public void setOverDueProfit(BigDecimal overDueProfit) {
		this.overDueProfit = overDueProfit;
	}

	public BigDecimal getTotalOverDue() {
		return totalOverDue;
	}

	public void setTotalOverDue(BigDecimal totalOverDue) {
		this.totalOverDue = totalOverDue;
	}

	public BigDecimal getOverDueCharges() {
		return overDueCharges;
	}

	public void setOverDueCharges(BigDecimal overDueCharges) {
		this.overDueCharges = overDueCharges;
	}

	public BigDecimal getTotalOverDueIncCharges() {
		return totalOverDueIncCharges;
	}

	public void setTotalOverDueIncCharges(BigDecimal totalOverDueIncCharges) {
		this.totalOverDueIncCharges = totalOverDueIncCharges;
	}

	public Long getOverDueInstlments() {
		return overDueInstlments;
	}

	public void setOverDueInstlments(Long overDueInstlments) {
		this.overDueInstlments = overDueInstlments;
	}

	public Long getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(Long numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public BigDecimal getTotalRepayAmt() {
		return totalRepayAmt;
	}

	public void setTotalRepayAmt(BigDecimal totalRepayAmt) {
		this.totalRepayAmt = totalRepayAmt;
	}

	public BigDecimal getEffectiveRateOfReturn() {
		return effectiveRateOfReturn;
	}

	public void setEffectiveRateOfReturn(BigDecimal effectiveRateOfReturn) {
		this.effectiveRateOfReturn = effectiveRateOfReturn;
	}

	public BigDecimal getTotalGracePft() {
		return totalGracePft;
	}

	public void setTotalGracePft(BigDecimal totalGracePft) {
		this.totalGracePft = totalGracePft;
	}

	public BigDecimal getTotalGraceCpz() {
		return totalGraceCpz;
	}

	public void setTotalGraceCpz(BigDecimal totalGraceCpz) {
		this.totalGraceCpz = totalGraceCpz;
	}

	public BigDecimal getTotalGrossGrcPft() {
		return totalGrossGrcPft;
	}

	public void setTotalGrossGrcPft(BigDecimal totalGrossGrcPft) {
		this.totalGrossGrcPft = totalGrossGrcPft;
	}

	public BigDecimal getTotalProfit() {
		return totalProfit;
	}

	public void setTotalProfit(BigDecimal totalProfit) {
		this.totalProfit = totalProfit;
	}

	public BigDecimal getFeeChargeAmt() {
		return feeChargeAmt;
	}

	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
		this.feeChargeAmt = feeChargeAmt;
	}

	public Integer getLoanTenor() {
		return loanTenor;
	}

	public void setLoanTenor(Integer loanTenor) {
		this.loanTenor = loanTenor;
	}

	public Date getFirstDisbDate() {
		return firstDisbDate;
	}

	public void setFirstDisbDate(Date firstDisbDate) {
		this.firstDisbDate = firstDisbDate;
	}

	public Date getLastDisbDate() {
		return lastDisbDate;
	}

	public void setLastDisbDate(Date lastDisbDate) {
		this.lastDisbDate = lastDisbDate;
	}

	public BigDecimal getNextRepayAmount() {
		return nextRepayAmount;
	}

	public void setNextRepayAmount(BigDecimal nextRepayAmount) {
		this.nextRepayAmount = nextRepayAmount;
	}

	public BigDecimal getFirstEmiAmount() {
		return firstEmiAmount;
	}

	public void setFirstEmiAmount(BigDecimal firstEmiAmount) {
		this.firstEmiAmount = firstEmiAmount;
	}

	public Integer getFutureInst() {
		return futureInst;
	}

	public void setFutureInst(Integer futureInst) {
		this.futureInst = futureInst;
	}

	public Integer getFutureTenor() {
		return futureTenor;
	}

	public void setFutureTenor(Integer futureTenor) {
		this.futureTenor = futureTenor;
	}

	public Date getFirstInstDate() {
		return firstInstDate;
	}

	public void setFirstInstDate(Date firstInstDate) {
		this.firstInstDate = firstInstDate;
	}

	public BigDecimal getPaidTotal() {
		return paidTotal;
	}

	public void setPaidTotal(BigDecimal paidTotal) {
		this.paidTotal = paidTotal;
	}

	public BigDecimal getAdvPaymentAmount() {
		return advPaymentAmount;
	}

	public void setAdvPaymentAmount(BigDecimal advPaymentAmount) {
		this.advPaymentAmount = advPaymentAmount;
	}

	public List<FinODDetails> getFinODDetail() {
		return finODDetail;
	}

	public void setFinODDetail(List<FinODDetails> finODDetail) {
		this.finODDetail = finODDetail;
	}

	public Boolean getFullyDisb() {
		return fullyDisb;
	}

	public void setFullyDisb(Boolean fullyDisb) {
		this.fullyDisb = fullyDisb;
	}

	public BigDecimal getSanctionAmt() {
		return sanctionAmt;
	}

	public void setSanctionAmt(BigDecimal sanctionAmt) {
		this.sanctionAmt = sanctionAmt;
	}

	public BigDecimal getUtilizedAmt() {
		return utilizedAmt;
	}

	public void setUtilizedAmt(BigDecimal utilizedAmt) {
		this.utilizedAmt = utilizedAmt;
	}

	public BigDecimal getAvailableAmt() {
		return availableAmt;
	}

	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}

	public BigDecimal getDueCharges() {
		return dueCharges;
	}

	public void setDueCharges(BigDecimal dueCharges) {
		this.dueCharges = dueCharges;
	}

	public BigDecimal getOverDueAmount() {
		return overDueAmount;
	}

	public void setOverDueAmount(BigDecimal overDueAmount) {
		this.overDueAmount = overDueAmount;
	}

	public BigDecimal getLoanEMI() {
		return loanEMI;
	}

	public void setLoanEMI(BigDecimal loanEMI) {
		this.loanEMI = loanEMI;
	}

	public BigDecimal getForeClosureAmount() {
		return foreClosureAmount;
	}

	public void setForeClosureAmount(BigDecimal foreClosureAmount) {
		this.foreClosureAmount = foreClosureAmount;
	}

	public Integer getInstallmentNo() {
		return installmentNo;
	}

	public void setInstallmentNo(Integer installmentNo) {
		this.installmentNo = installmentNo;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getLoanTotPrincipal() {
		return loanTotPrincipal;
	}

	public void setLoanTotPrincipal(BigDecimal loanTotPrincipal) {
		this.loanTotPrincipal = loanTotPrincipal;
	}

	public BigDecimal getLoanTotInterest() {
		return loanTotInterest;
	}

	public void setLoanTotInterest(BigDecimal loanTotInterest) {
		this.loanTotInterest = loanTotInterest;
	}

	public BigDecimal getOverDueEMI() {
		return overDueEMI;
	}

	public void setOverDueEMI(BigDecimal overDueEMI) {
		this.overDueEMI = overDueEMI;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public String getMigratedNo() {
		return migratedNo;
	}

	public void setMigratedNo(String migratedNo) {
		this.migratedNo = migratedNo;
	}

	public Date getLastInstDate() {
		return lastInstDate;
	}

	public void setLastInstDate(Date lastInstDate) {
		this.lastInstDate = lastInstDate;
	}

	public String getDPDString() {
		return dPDString;
	}

	public void setDPDString(String dPDString) {
		this.dPDString = dPDString;
	}

	public String getAssetCost() {
		return assetCost;
	}

	public void setAssetCost(String assetCost) {
		this.assetCost = assetCost;
	}

	public String getAssetModel() {
		return assetModel;
	}

	public void setAssetModel(String assetModel) {
		this.assetModel = assetModel;
	}

	public String getdPDString() {
		return dPDString;
	}

	public void setdPDString(String dPDString) {
		this.dPDString = dPDString;
	}

	public String getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(String categoryID) {
		this.categoryID = categoryID;
	}

	public String getCategoryDesc() {
		return categoryDesc;
	}

	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

	public BigDecimal getInterestComponent() {
		return interestComponent;
	}

	public void setInterestComponent(BigDecimal interestComponent) {
		this.interestComponent = interestComponent;
	}

	public BigDecimal getPrincipalComponent() {
		return principalComponent;
	}

	public void setPrincipalComponent(BigDecimal principalComponent) {
		this.principalComponent = principalComponent;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
}
