package com.pennanttech.pff.model.external.controldump;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ControlDump implements Serializable {
	static final long serialVersionUID = 1L;

	private BigDecimal ccyMinorUnits;
	private int amountScale;
	private String agreementNo;
	private long agreementId;
	private String productFlag;
	private long schemeId = 0;
	private long branchId;
	private String npaStageId;
	private String loanStatus;
	private String disbStatus;
	private Date firstDueDate;
	private Date maturityDate;
	private BigDecimal amtFin = BigDecimal.ZERO;
	private BigDecimal disbursedAmount = BigDecimal.ZERO;
	private BigDecimal emiDue = BigDecimal.ZERO;
	private BigDecimal principalDue = BigDecimal.ZERO;
	private BigDecimal interestDue = BigDecimal.ZERO;
	private BigDecimal emiReceived = BigDecimal.ZERO;
	private BigDecimal principalReceived = BigDecimal.ZERO;
	private BigDecimal interestReceived = BigDecimal.ZERO;
	private BigDecimal emiOs = BigDecimal.ZERO;
	private BigDecimal principalOs = BigDecimal.ZERO;
	private BigDecimal interestOs = BigDecimal.ZERO;
	private BigDecimal bulkRefund = BigDecimal.ZERO;
	private BigDecimal principalWaived = BigDecimal.ZERO;
	private BigDecimal emiPrincipalWaived = BigDecimal.ZERO;
	private BigDecimal emiInterestWaived = BigDecimal.ZERO;
	private BigDecimal principalAtTerm = BigDecimal.ZERO;
	private BigDecimal advanceEmi = BigDecimal.ZERO;
	private BigDecimal advanceEmiBilled = BigDecimal.ZERO;
	private BigDecimal migratedAdvanceEmi = BigDecimal.ZERO;
	private BigDecimal migratedAdvanceEmiBilled = BigDecimal.ZERO;
	private BigDecimal migratedAdvanceEmiUnbilled = BigDecimal.ZERO;
	private BigDecimal closedCanAdvEmi = BigDecimal.ZERO;
	private BigDecimal principalBalance = BigDecimal.ZERO;
	private BigDecimal interestBalance = BigDecimal.ZERO;
	private BigDecimal sohBalance = BigDecimal.ZERO;
	private long noOfUnbilledEmi;
	private BigDecimal totalInterest = BigDecimal.ZERO;
	private BigDecimal accruedAmount = BigDecimal.ZERO;
	private BigDecimal balanceUmfc = BigDecimal.ZERO;
	private BigDecimal emiInAdvanceReceivedMaker = BigDecimal.ZERO;
	private BigDecimal emiInAdvanceBilled = BigDecimal.ZERO;
	private BigDecimal emiInAdvanceUnbilled = BigDecimal.ZERO;
	private BigDecimal migAdvEmiBilledPrincomp = BigDecimal.ZERO;
	private BigDecimal migAdvEmiBilledIntcomp = BigDecimal.ZERO;
	private BigDecimal migAdvEmiUnbilledPrincomp = BigDecimal.ZERO;
	private BigDecimal migAdvEmiUnbilledIntcomp = BigDecimal.ZERO;
	private BigDecimal emiInAdvBilledPrincomp = BigDecimal.ZERO;
	private BigDecimal emiInAdvBilledIntcomp = BigDecimal.ZERO;
	private BigDecimal emiInAdvUnbilledPrincomp = BigDecimal.ZERO;
	private BigDecimal emiInAdvUnbilledIntcomp = BigDecimal.ZERO;
	private BigDecimal closCanAdvEmiPrincomp = BigDecimal.ZERO;
	private BigDecimal closCanAdvEmiIntcomp = BigDecimal.ZERO;
	private BigDecimal roundingDiffReceivable = BigDecimal.ZERO;
	private BigDecimal roundingDiffReceived = BigDecimal.ZERO;
	private BigDecimal migDifferenceReceivable = BigDecimal.ZERO;
	private BigDecimal migDifferenceReceived = BigDecimal.ZERO;
	private BigDecimal migDifferencePayable = BigDecimal.ZERO;
	private BigDecimal migDifferencePaid = BigDecimal.ZERO;
	private BigDecimal writeoffDue = BigDecimal.ZERO;
	private BigDecimal writeoffReceived = BigDecimal.ZERO;
	private BigDecimal soldSeizeReceivable = BigDecimal.ZERO;
	private BigDecimal soldSeizeReceived = BigDecimal.ZERO;
	private BigDecimal soldSeizePayable = BigDecimal.ZERO;
	private BigDecimal soldSeizePaid = BigDecimal.ZERO;
	private BigDecimal netExcessReceived = BigDecimal.ZERO;
	private BigDecimal netExcessAdjusted = BigDecimal.ZERO;
	private BigDecimal lppChargesReceivable = BigDecimal.ZERO;
	private BigDecimal lppChargesReceived = BigDecimal.ZERO;
	private BigDecimal pdcSwapChargesReceivable = BigDecimal.ZERO;
	private BigDecimal pdcSwapChargesReceived = BigDecimal.ZERO;
	private BigDecimal repoChargesReceivable = BigDecimal.ZERO;
	private BigDecimal repoChargesReceived = BigDecimal.ZERO;
	private BigDecimal foreClosureChargesDue = BigDecimal.ZERO;
	private BigDecimal foreClosureChargesReceived = BigDecimal.ZERO;
	private BigDecimal bounceChargesDue = BigDecimal.ZERO;
	private BigDecimal bounceChargesReceived = BigDecimal.ZERO;
	private BigDecimal insurRenewCharge = BigDecimal.ZERO;
	private BigDecimal insurRenewChargeRecd = BigDecimal.ZERO;
	private BigDecimal insurReceivable = BigDecimal.ZERO;
	private BigDecimal insurReceived = BigDecimal.ZERO;
	private BigDecimal insurPayable = BigDecimal.ZERO;
	private BigDecimal insurPaid = BigDecimal.ZERO;
	private String customerId;
	private String customerName;
	private long sanctionedTenure;
	private BigDecimal loanEmi = BigDecimal.ZERO;
	private BigDecimal flatRate = BigDecimal.ZERO;
	private BigDecimal effectiveRate = BigDecimal.ZERO;
	private Date agreementDate;
	private Date disbursalDate;
	private Date closureDate;
	private long noOfAdvanceEmis;
	private BigDecimal assetCost = BigDecimal.ZERO;
	private long noOfEmiOs;
	private long dpd;
	private long currentBucket;
	private String branchName;
	private String schemeName;
	private long derivedBucket;
	private String assetDesc;
	private String make;
	private String chasisNum;
	private String regdNum;
	private String engineNum;
	private BigDecimal invoiceAmt = BigDecimal.ZERO;
	private String supplierDesc;
	private String instrument;
	private Date repoDate;
	private String localOutStationFlag;
	private Date firstRepaydueDate;
	private Date createdOn;

	public ControlDump() {
	    super();
	}

	public String getAgreementNo() {
		return agreementNo;
	}

	public void setAgreementNo(String agreementNo) {
		this.agreementNo = agreementNo;
	}

	public long getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(long agreementId) {
		this.agreementId = agreementId;
	}

	public String getProductFlag() {
		return productFlag;
	}

	public void setProductFlag(String productFlag) {
		this.productFlag = productFlag;
	}

	public long getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(long schemeId) {
		this.schemeId = schemeId;
	}

	public long getBranchId() {
		return branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	public String getNpaStageId() {
		return npaStageId;
	}

	public void setNpaStageId(String npaStageId) {
		this.npaStageId = npaStageId;
	}

	public String getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(String loanStatus) {
		this.loanStatus = loanStatus;
	}

	public String getDisbStatus() {
		return disbStatus;
	}

	public void setDisbStatus(String disbStatus) {
		this.disbStatus = disbStatus;
	}

	public Date getFirstDueDate() {
		return firstDueDate;
	}

	public void setFirstDueDate(Date firstDueDate) {
		this.firstDueDate = firstDueDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public BigDecimal getAmtFin() {
		return amtFin;
	}

	public void setAmtFin(BigDecimal amtFin) {
		this.amtFin = amtFin;
	}

	public BigDecimal getDisbursedAmount() {
		return disbursedAmount;
	}

	public void setDisbursedAmount(BigDecimal disbursedAmount) {
		this.disbursedAmount = disbursedAmount;
	}

	public BigDecimal getEmiDue() {
		return emiDue;
	}

	public void setEmiDue(BigDecimal emiDue) {
		this.emiDue = emiDue;
	}

	public BigDecimal getPrincipalDue() {
		return principalDue;
	}

	public void setPrincipalDue(BigDecimal principalDue) {
		this.principalDue = principalDue;
	}

	public BigDecimal getInterestDue() {
		return interestDue;
	}

	public void setInterestDue(BigDecimal interestDue) {
		this.interestDue = interestDue;
	}

	public BigDecimal getEmiReceived() {
		return emiReceived;
	}

	public void setEmiReceived(BigDecimal emiReceived) {
		this.emiReceived = emiReceived;
	}

	public BigDecimal getPrincipalReceived() {
		return principalReceived;
	}

	public void setPrincipalReceived(BigDecimal principalReceived) {
		this.principalReceived = principalReceived;
	}

	public BigDecimal getInterestReceived() {
		return interestReceived;
	}

	public void setInterestReceived(BigDecimal interestReceived) {
		this.interestReceived = interestReceived;
	}

	public BigDecimal getEmiOs() {
		return emiOs;
	}

	public void setEmiOs(BigDecimal emiOs) {
		this.emiOs = emiOs;
	}

	public BigDecimal getPrincipalOs() {
		return principalOs;
	}

	public void setPrincipalOs(BigDecimal principalOs) {
		this.principalOs = principalOs;
	}

	public BigDecimal getInterestOs() {
		return interestOs;
	}

	public void setInterestOs(BigDecimal interestOs) {
		this.interestOs = interestOs;
	}

	public BigDecimal getBulkRefund() {
		return bulkRefund;
	}

	public void setBulkRefund(BigDecimal bulkRefund) {
		this.bulkRefund = bulkRefund;
	}

	public BigDecimal getPrincipalWaived() {
		return principalWaived;
	}

	public void setPrincipalWaived(BigDecimal principalWaived) {
		this.principalWaived = principalWaived;
	}

	public BigDecimal getEmiPrincipalWaived() {
		return emiPrincipalWaived;
	}

	public void setEmiPrincipalWaived(BigDecimal emiPrincipalWaived) {
		this.emiPrincipalWaived = emiPrincipalWaived;
	}

	public BigDecimal getEmiInterestWaived() {
		return emiInterestWaived;
	}

	public void setEmiInterestWaived(BigDecimal emiInterestWaived) {
		this.emiInterestWaived = emiInterestWaived;
	}

	public BigDecimal getPrincipalAtTerm() {
		return principalAtTerm;
	}

	public void setPrincipalAtTerm(BigDecimal principalAtTerm) {
		this.principalAtTerm = principalAtTerm;
	}

	public BigDecimal getAdvanceEmi() {
		return advanceEmi;
	}

	public void setAdvanceEmi(BigDecimal advanceEmi) {
		this.advanceEmi = advanceEmi;
	}

	public BigDecimal getAdvanceEmiBilled() {
		return advanceEmiBilled;
	}

	public void setAdvanceEmiBilled(BigDecimal advanceEmiBilled) {
		this.advanceEmiBilled = advanceEmiBilled;
	}

	public BigDecimal getMigratedAdvanceEmi() {
		return migratedAdvanceEmi;
	}

	public void setMigratedAdvanceEmi(BigDecimal migratedAdvanceEmi) {
		this.migratedAdvanceEmi = migratedAdvanceEmi;
	}

	public BigDecimal getMigratedAdvanceEmiBilled() {
		return migratedAdvanceEmiBilled;
	}

	public void setMigratedAdvanceEmiBilled(BigDecimal migratedAdvanceEmiBilled) {
		this.migratedAdvanceEmiBilled = migratedAdvanceEmiBilled;
	}

	public BigDecimal getMigratedAdvanceEmiUnbilled() {
		return migratedAdvanceEmiUnbilled;
	}

	public void setMigratedAdvanceEmiUnbilled(BigDecimal migratedAdvanceEmiUnbilled) {
		this.migratedAdvanceEmiUnbilled = migratedAdvanceEmiUnbilled;
	}

	public BigDecimal getClosedCanAdvEmi() {
		return closedCanAdvEmi;
	}

	public void setClosedCanAdvEmi(BigDecimal closedCanAdvEmi) {
		this.closedCanAdvEmi = closedCanAdvEmi;
	}

	public BigDecimal getPrincipalBalance() {
		return principalBalance;
	}

	public void setPrincipalBalance(BigDecimal principalBalance) {
		this.principalBalance = principalBalance;
	}

	public BigDecimal getInterestBalance() {
		return interestBalance;
	}

	public void setInterestBalance(BigDecimal interestBalance) {
		this.interestBalance = interestBalance;
	}

	public BigDecimal getSohBalance() {
		return sohBalance;
	}

	public void setSohBalance(BigDecimal sohBalance) {
		this.sohBalance = sohBalance;
	}

	public long getNoOfUnbilledEmi() {
		return noOfUnbilledEmi;
	}

	public void setNoOfUnbilledEmi(long noOfUnbilledEmi) {
		this.noOfUnbilledEmi = noOfUnbilledEmi;
	}

	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public BigDecimal getAccruedAmount() {
		return accruedAmount;
	}

	public void setAccruedAmount(BigDecimal accruedAmount) {
		this.accruedAmount = accruedAmount;
	}

	public BigDecimal getBalanceUmfc() {
		return balanceUmfc;
	}

	public void setBalanceUmfc(BigDecimal balanceUmfc) {
		this.balanceUmfc = balanceUmfc;
	}

	public BigDecimal getEmiInAdvanceReceivedMaker() {
		return emiInAdvanceReceivedMaker;
	}

	public void setEmiInAdvanceReceivedMaker(BigDecimal emiInAdvanceReceivedMaker) {
		this.emiInAdvanceReceivedMaker = emiInAdvanceReceivedMaker;
	}

	public BigDecimal getEmiInAdvanceBilled() {
		return emiInAdvanceBilled;
	}

	public void setEmiInAdvanceBilled(BigDecimal emiInAdvanceBilled) {
		this.emiInAdvanceBilled = emiInAdvanceBilled;
	}

	public BigDecimal getEmiInAdvanceUnbilled() {
		return emiInAdvanceUnbilled;
	}

	public void setEmiInAdvanceUnbilled(BigDecimal emiInAdvanceUnbilled) {
		this.emiInAdvanceUnbilled = emiInAdvanceUnbilled;
	}

	public BigDecimal getMigAdvEmiBilledPrincomp() {
		return migAdvEmiBilledPrincomp;
	}

	public void setMigAdvEmiBilledPrincomp(BigDecimal migAdvEmiBilledPrincomp) {
		this.migAdvEmiBilledPrincomp = migAdvEmiBilledPrincomp;
	}

	public BigDecimal getMigAdvEmiBilledIntcomp() {
		return migAdvEmiBilledIntcomp;
	}

	public void setMigAdvEmiBilledIntcomp(BigDecimal migAdvEmiBilledIntcomp) {
		this.migAdvEmiBilledIntcomp = migAdvEmiBilledIntcomp;
	}

	public BigDecimal getMigAdvEmiUnbilledPrincomp() {
		return migAdvEmiUnbilledPrincomp;
	}

	public void setMigAdvEmiUnbilledPrincomp(BigDecimal migAdvEmiUnbilledPrincomp) {
		this.migAdvEmiUnbilledPrincomp = migAdvEmiUnbilledPrincomp;
	}

	public BigDecimal getMigAdvEmiUnbilledIntcomp() {
		return migAdvEmiUnbilledIntcomp;
	}

	public void setMigAdvEmiUnbilledIntcomp(BigDecimal migAdvEmiUnbilledIntcomp) {
		this.migAdvEmiUnbilledIntcomp = migAdvEmiUnbilledIntcomp;
	}

	public BigDecimal getEmiInAdvBilledPrincomp() {
		return emiInAdvBilledPrincomp;
	}

	public void setEmiInAdvBilledPrincomp(BigDecimal emiInAdvBilledPrincomp) {
		this.emiInAdvBilledPrincomp = emiInAdvBilledPrincomp;
	}

	public BigDecimal getEmiInAdvBilledIntcomp() {
		return emiInAdvBilledIntcomp;
	}

	public void setEmiInAdvBilledIntcomp(BigDecimal emiInAdvBilledIntcomp) {
		this.emiInAdvBilledIntcomp = emiInAdvBilledIntcomp;
	}

	public BigDecimal getEmiInAdvUnbilledPrincomp() {
		return emiInAdvUnbilledPrincomp;
	}

	public void setEmiInAdvUnbilledPrincomp(BigDecimal emiInAdvUnbilledPrincomp) {
		this.emiInAdvUnbilledPrincomp = emiInAdvUnbilledPrincomp;
	}

	public BigDecimal getEmiInAdvUnbilledIntcomp() {
		return emiInAdvUnbilledIntcomp;
	}

	public void setEmiInAdvUnbilledIntcomp(BigDecimal emiInAdvUnbilledIntcomp) {
		this.emiInAdvUnbilledIntcomp = emiInAdvUnbilledIntcomp;
	}

	public BigDecimal getClosCanAdvEmiPrincomp() {
		return closCanAdvEmiPrincomp;
	}

	public void setClosCanAdvEmiPrincomp(BigDecimal closCanAdvEmiPrincomp) {
		this.closCanAdvEmiPrincomp = closCanAdvEmiPrincomp;
	}

	public BigDecimal getClosCanAdvEmiIntcomp() {
		return closCanAdvEmiIntcomp;
	}

	public void setClosCanAdvEmiIntcomp(BigDecimal closCanAdvEmiIntcomp) {
		this.closCanAdvEmiIntcomp = closCanAdvEmiIntcomp;
	}

	public BigDecimal getRoundingDiffReceivable() {
		return roundingDiffReceivable;
	}

	public void setRoundingDiffReceivable(BigDecimal roundingDiffReceivable) {
		this.roundingDiffReceivable = roundingDiffReceivable;
	}

	public BigDecimal getRoundingDiffReceived() {
		return roundingDiffReceived;
	}

	public void setRoundingDiffReceived(BigDecimal roundingDiffReceived) {
		this.roundingDiffReceived = roundingDiffReceived;
	}

	public BigDecimal getMigDifferenceReceivable() {
		return migDifferenceReceivable;
	}

	public void setMigDifferenceReceivable(BigDecimal migDifferenceReceivable) {
		this.migDifferenceReceivable = migDifferenceReceivable;
	}

	public BigDecimal getMigDifferenceReceived() {
		return migDifferenceReceived;
	}

	public void setMigDifferenceReceived(BigDecimal migDifferenceReceived) {
		this.migDifferenceReceived = migDifferenceReceived;
	}

	public BigDecimal getMigDifferencePayable() {
		return migDifferencePayable;
	}

	public void setMigDifferencePayable(BigDecimal migDifferencePayable) {
		this.migDifferencePayable = migDifferencePayable;
	}

	public BigDecimal getMigDifferencePaid() {
		return migDifferencePaid;
	}

	public void setMigDifferencePaid(BigDecimal migDifferencePaid) {
		this.migDifferencePaid = migDifferencePaid;
	}

	public BigDecimal getWriteoffDue() {
		return writeoffDue;
	}

	public void setWriteoffDue(BigDecimal writeoffDue) {
		this.writeoffDue = writeoffDue;
	}

	public BigDecimal getWriteoffReceived() {
		return writeoffReceived;
	}

	public void setWriteoffReceived(BigDecimal writeoffReceived) {
		this.writeoffReceived = writeoffReceived;
	}

	public BigDecimal getSoldSeizeReceivable() {
		return soldSeizeReceivable;
	}

	public void setSoldSeizeReceivable(BigDecimal soldSeizeReceivable) {
		this.soldSeizeReceivable = soldSeizeReceivable;
	}

	public BigDecimal getSoldSeizeReceived() {
		return soldSeizeReceived;
	}

	public void setSoldSeizeReceived(BigDecimal soldSeizeReceived) {
		this.soldSeizeReceived = soldSeizeReceived;
	}

	public BigDecimal getSoldSeizePayable() {
		return soldSeizePayable;
	}

	public void setSoldSeizePayable(BigDecimal soldSeizePayable) {
		this.soldSeizePayable = soldSeizePayable;
	}

	public BigDecimal getSoldSeizePaid() {
		return soldSeizePaid;
	}

	public void setSoldSeizePaid(BigDecimal soldSeizePaid) {
		this.soldSeizePaid = soldSeizePaid;
	}

	public BigDecimal getNetExcessReceived() {
		return netExcessReceived;
	}

	public void setNetExcessReceived(BigDecimal netExcessReceived) {
		this.netExcessReceived = netExcessReceived;
	}

	public BigDecimal getNetExcessAdjusted() {
		return netExcessAdjusted;
	}

	public void setNetExcessAdjusted(BigDecimal netExcessAdjusted) {
		this.netExcessAdjusted = netExcessAdjusted;
	}

	public BigDecimal getLppChargesReceivable() {
		return lppChargesReceivable;
	}

	public void setLppChargesReceivable(BigDecimal lppChargesReceivable) {
		this.lppChargesReceivable = lppChargesReceivable;
	}

	public BigDecimal getLppChargesReceived() {
		return lppChargesReceived;
	}

	public void setLppChargesReceived(BigDecimal lppChargesReceived) {
		this.lppChargesReceived = lppChargesReceived;
	}

	public BigDecimal getPdcSwapChargesReceivable() {
		return pdcSwapChargesReceivable;
	}

	public void setPdcSwapChargesReceivable(BigDecimal pdcSwapChargesReceivable) {
		this.pdcSwapChargesReceivable = pdcSwapChargesReceivable;
	}

	public BigDecimal getPdcSwapChargesReceived() {
		return pdcSwapChargesReceived;
	}

	public void setPdcSwapChargesReceived(BigDecimal pdcSwapChargesReceived) {
		this.pdcSwapChargesReceived = pdcSwapChargesReceived;
	}

	public BigDecimal getRepoChargesReceivable() {
		return repoChargesReceivable;
	}

	public void setRepoChargesReceivable(BigDecimal repoChargesReceivable) {
		this.repoChargesReceivable = repoChargesReceivable;
	}

	public BigDecimal getRepoChargesReceived() {
		return repoChargesReceived;
	}

	public void setRepoChargesReceived(BigDecimal repoChargesReceived) {
		this.repoChargesReceived = repoChargesReceived;
	}

	public BigDecimal getForeClosureChargesDue() {
		return foreClosureChargesDue;
	}

	public void setForeClosureChargesDue(BigDecimal foreClosureChargesDue) {
		this.foreClosureChargesDue = foreClosureChargesDue;
	}

	public BigDecimal getForeClosureChargesReceived() {
		return foreClosureChargesReceived;
	}

	public void setForeClosureChargesReceived(BigDecimal foreClosureChargesReceived) {
		this.foreClosureChargesReceived = foreClosureChargesReceived;
	}

	public BigDecimal getBounceChargesDue() {
		return bounceChargesDue;
	}

	public void setBounceChargesDue(BigDecimal bounceChargesDue) {
		this.bounceChargesDue = bounceChargesDue;
	}

	public BigDecimal getBounceChargesReceived() {
		return bounceChargesReceived;
	}

	public void setBounceChargesReceived(BigDecimal bounceChargesReceived) {
		this.bounceChargesReceived = bounceChargesReceived;
	}

	public BigDecimal getInsurRenewCharge() {
		return insurRenewCharge;
	}

	public void setInsurRenewCharge(BigDecimal insurRenewCharge) {
		this.insurRenewCharge = insurRenewCharge;
	}

	public BigDecimal getInsurRenewChargeRecd() {
		return insurRenewChargeRecd;
	}

	public void setInsurRenewChargeRecd(BigDecimal insurRenewChargeRecd) {
		this.insurRenewChargeRecd = insurRenewChargeRecd;
	}

	public BigDecimal getInsurReceivable() {
		return insurReceivable;
	}

	public void setInsurReceivable(BigDecimal insurReceivable) {
		this.insurReceivable = insurReceivable;
	}

	public BigDecimal getInsurReceived() {
		return insurReceived;
	}

	public void setInsurReceived(BigDecimal insurReceived) {
		this.insurReceived = insurReceived;
	}

	public BigDecimal getInsurPayable() {
		return insurPayable;
	}

	public void setInsurPayable(BigDecimal insurPayable) {
		this.insurPayable = insurPayable;
	}

	public BigDecimal getInsurPaid() {
		return insurPaid;
	}

	public void setInsurPaid(BigDecimal insurPaid) {
		this.insurPaid = insurPaid;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public long getSanctionedTenure() {
		return sanctionedTenure;
	}

	public void setSanctionedTenure(long sanctionedTenure) {
		this.sanctionedTenure = sanctionedTenure;
	}

	public BigDecimal getLoanEmi() {
		return loanEmi;
	}

	public void setLoanEmi(BigDecimal loanEmi) {
		this.loanEmi = loanEmi;
	}

	public BigDecimal getFlatRate() {
		return flatRate;
	}

	public void setFlatRate(BigDecimal flatRate) {
		this.flatRate = flatRate;
	}

	public BigDecimal getEffectiveRate() {
		return effectiveRate;
	}

	public void setEffectiveRate(BigDecimal effectiveRate) {
		this.effectiveRate = effectiveRate;
	}

	public Date getAgreementDate() {
		return agreementDate;
	}

	public void setAgreementDate(Date agreementDate) {
		this.agreementDate = agreementDate;
	}

	public Date getDisbursalDate() {
		return disbursalDate;
	}

	public void setDisbursalDate(Date disbursalDate) {
		this.disbursalDate = disbursalDate;
	}

	public Date getClosureDate() {
		return closureDate;
	}

	public void setClosureDate(Date closureDate) {
		this.closureDate = closureDate;
	}

	public long getNoOfAdvanceEmis() {
		return noOfAdvanceEmis;
	}

	public void setNoOfAdvanceEmis(long noOfAdvanceEmis) {
		this.noOfAdvanceEmis = noOfAdvanceEmis;
	}

	public BigDecimal getAssetCost() {
		return assetCost;
	}

	public void setAssetCost(BigDecimal assetCost) {
		this.assetCost = assetCost;
	}

	public long getNoOfEmiOs() {
		return noOfEmiOs;
	}

	public void setNoOfEmiOs(long noOfEmiOs) {
		this.noOfEmiOs = noOfEmiOs;
	}

	public long getDpd() {
		return dpd;
	}

	public void setDpd(long dpd) {
		this.dpd = dpd;
	}

	public long getCurrentBucket() {
		return currentBucket;
	}

	public void setCurrentBucket(long currentBucket) {
		this.currentBucket = currentBucket;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}

	public long getDerivedBucket() {
		return derivedBucket;
	}

	public void setDerivedBucket(long derivedBucket) {
		this.derivedBucket = derivedBucket;
	}

	public String getAssetDesc() {
		return assetDesc;
	}

	public void setAssetDesc(String assetDesc) {
		this.assetDesc = assetDesc;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getChasisNum() {
		return chasisNum;
	}

	public void setChasisNum(String chasisNum) {
		this.chasisNum = chasisNum;
	}

	public String getRegdNum() {
		return regdNum;
	}

	public void setRegdNum(String regdNum) {
		this.regdNum = regdNum;
	}

	public String getEngineNum() {
		return engineNum;
	}

	public void setEngineNum(String engineNum) {
		this.engineNum = engineNum;
	}

	public BigDecimal getInvoiceAmt() {
		return invoiceAmt;
	}

	public void setInvoiceAmt(BigDecimal invoiceAmt) {
		this.invoiceAmt = invoiceAmt;
	}

	public String getSupplierDesc() {
		return supplierDesc;
	}

	public void setSupplierDesc(String supplierDesc) {
		this.supplierDesc = supplierDesc;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public Date getRepoDate() {
		return repoDate;
	}

	public void setRepoDate(Date repoDate) {
		this.repoDate = repoDate;
	}

	public String getLocalOutStationFlag() {
		return localOutStationFlag;
	}

	public void setLocalOutStationFlag(String localOutStationFlag) {
		this.localOutStationFlag = localOutStationFlag;
	}

	public Date getFirstRepaydueDate() {
		return firstRepaydueDate;
	}

	public void setFirstRepaydueDate(Date firstRepaydueDate) {
		this.firstRepaydueDate = firstRepaydueDate;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public BigDecimal getCcyMinorUnits() {
		return ccyMinorUnits;
	}

	public void setCcyMinorUnits(BigDecimal ccyMinorUnits) {
		this.ccyMinorUnits = ccyMinorUnits;
	}

	public int getAmountScale() {
		return amountScale;
	}

	public void setAmountScale(int amountScale) {
		this.amountScale = amountScale;
	}

}