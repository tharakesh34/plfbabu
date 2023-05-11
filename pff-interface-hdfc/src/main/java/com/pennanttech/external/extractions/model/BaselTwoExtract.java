package com.pennanttech.external.extractions.model;

import java.math.BigDecimal;
import java.util.Date;

public class BaselTwoExtract {

	private long agreementId;
	private long customerId;
	private String icnStatus;
	private Date icnAcquired;
	private Date loanApplDate;
	private int totalEmis;
	private String dnd;
	private String appliedBefore;
	private String oldAgmtNo;
	private String micrLocation;
	private BigDecimal fees;
	private String fixedFloat;
	private BigDecimal dealerComm;
	private BigDecimal manfDisc;
	private String promotionCode;
	private String rcAvailStatus;
	private String emiType;
	private Date rcAcquired;
	private String rest;
	private String custAccount;
	private int noBounces;
	private BigDecimal chargesPaid;
	private Date prevNPA;
	private String securtFlag;
	private Date securtDt;
	private String dpdString;
	private BigDecimal totAmtPaidMnthChq;
	private BigDecimal totAmtPaidMnthCash;
	private String rescheduled;
	private Date rescheduleEffDt;
	private String closureType;
	private BigDecimal prinLossClosure;
	private Date ibpcStart;
	private Date ibpcEnd;
	private int monthsInPrevJob;
	private int monthsIncurrJob;
	private int monthsInCurrResidence;
	private BigDecimal rentPM;
	private String deviation;
	private int vehicleAge;
	private int appScore;
	private int ricScore;
	private String incomeProofReceived;
	private BigDecimal totalExperience;
	private String employerCategory;
	private int monthsInCity;
	private String eligibility;
	private BigDecimal obligations;
	private BigDecimal obligationsn;

	public long getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(long agreementId) {
		this.agreementId = agreementId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getIcnStatus() {
		return icnStatus;
	}

	public void setIcnStatus(String icnStatus) {
		this.icnStatus = icnStatus;
	}

	public Date getIcnAcquired() {
		return icnAcquired;
	}

	public void setIcnAcquired(Date icnAcquired) {
		this.icnAcquired = icnAcquired;
	}

	public Date getLoanApplDate() {
		return loanApplDate;
	}

	public void setLoanApplDate(Date loanApplDate) {
		this.loanApplDate = loanApplDate;
	}

	public int getTotalEmis() {
		return totalEmis;
	}

	public void setTotalEmis(int totalEmis) {
		this.totalEmis = totalEmis;
	}

	public String getDnd() {
		return dnd;
	}

	public void setDnd(String dnd) {
		this.dnd = dnd;
	}

	public String getAppliedBefore() {
		return appliedBefore;
	}

	public void setAppliedBefore(String appliedBefore) {
		this.appliedBefore = appliedBefore;
	}

	public String getOldAgmtNo() {
		return oldAgmtNo;
	}

	public void setOldAgmtNo(String oldAgmtNo) {
		this.oldAgmtNo = oldAgmtNo;
	}

	public String getMicrLocation() {
		return micrLocation;
	}

	public void setMicrLocation(String micrLocation) {
		this.micrLocation = micrLocation;
	}

	public BigDecimal getFees() {
		return fees;
	}

	public void setFees(BigDecimal fees) {
		this.fees = fees;
	}

	public String getFixedFloat() {
		return fixedFloat;
	}

	public void setFixedFloat(String fixedFloat) {
		this.fixedFloat = fixedFloat;
	}

	public BigDecimal getDealerComm() {
		return dealerComm;
	}

	public void setDealerComm(BigDecimal dealerComm) {
		this.dealerComm = dealerComm;
	}

	public BigDecimal getManfDisc() {
		return manfDisc;
	}

	public void setManfDisc(BigDecimal manfDisc) {
		this.manfDisc = manfDisc;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getRcAvailStatus() {
		return rcAvailStatus;
	}

	public void setRcAvailStatus(String rcAvailStatus) {
		this.rcAvailStatus = rcAvailStatus;
	}

	public String getEmiType() {
		return emiType;
	}

	public void setEmiType(String emiType) {
		this.emiType = emiType;
	}

	public Date getRcAcquired() {
		return rcAcquired;
	}

	public void setRcAcquired(Date rcAcquired) {
		this.rcAcquired = rcAcquired;
	}

	public String getRest() {
		return rest;
	}

	public void setRest(String rest) {
		this.rest = rest;
	}

	public String getCustAccount() {
		return custAccount;
	}

	public void setCustAccount(String custAccount) {
		this.custAccount = custAccount;
	}

	public int getNoBounces() {
		return noBounces;
	}

	public void setNoBounces(int noBounces) {
		this.noBounces = noBounces;
	}

	public BigDecimal getChargesPaid() {
		return chargesPaid;
	}

	public void setChargesPaid(BigDecimal chargesPaid) {
		this.chargesPaid = chargesPaid;
	}

	public Date getPrevNPA() {
		return prevNPA;
	}

	public void setPrevNPA(Date prevNPA) {
		this.prevNPA = prevNPA;
	}

	public String getSecurtFlag() {
		return securtFlag;
	}

	public void setSecurtFlag(String securtFlag) {
		this.securtFlag = securtFlag;
	}

	public Date getSecurtDt() {
		return securtDt;
	}

	public void setSecurtDt(Date securtDt) {
		this.securtDt = securtDt;
	}

	public String getDpdString() {
		return dpdString;
	}

	public void setDpdString(String dpdString) {
		this.dpdString = dpdString;
	}

	public BigDecimal getTotAmtPaidMnthChq() {
		return totAmtPaidMnthChq;
	}

	public void setTotAmtPaidMnthChq(BigDecimal totAmtPaidMnthChq) {
		this.totAmtPaidMnthChq = totAmtPaidMnthChq;
	}

	public BigDecimal getTotAmtPaidMnthCash() {
		return totAmtPaidMnthCash;
	}

	public void setTotAmtPaidMnthCash(BigDecimal totAmtPaidMnthCash) {
		this.totAmtPaidMnthCash = totAmtPaidMnthCash;
	}

	public String getRescheduled() {
		return rescheduled;
	}

	public void setRescheduled(String rescheduled) {
		this.rescheduled = rescheduled;
	}

	public Date getRescheduleEffDt() {
		return rescheduleEffDt;
	}

	public void setRescheduleEffDt(Date rescheduleEffDt) {
		this.rescheduleEffDt = rescheduleEffDt;
	}

	public String getClosureType() {
		return closureType;
	}

	public void setClosureType(String closureType) {
		this.closureType = closureType;
	}

	public BigDecimal getPrinLossClosure() {
		return prinLossClosure;
	}

	public void setPrinLossClosure(BigDecimal prinLossClosure) {
		this.prinLossClosure = prinLossClosure;
	}

	public Date getIbpcStart() {
		return ibpcStart;
	}

	public void setIbpcStart(Date ibpcStart) {
		this.ibpcStart = ibpcStart;
	}

	public Date getIbpcEnd() {
		return ibpcEnd;
	}

	public void setIbpcEnd(Date ibpcEnd) {
		this.ibpcEnd = ibpcEnd;
	}

	public int getMonthsInPrevJob() {
		return monthsInPrevJob;
	}

	public void setMonthsInPrevJob(int monthsInPrevJob) {
		this.monthsInPrevJob = monthsInPrevJob;
	}

	public int getMonthsIncurrJob() {
		return monthsIncurrJob;
	}

	public void setMonthsIncurrJob(int monthsIncurrJob) {
		this.monthsIncurrJob = monthsIncurrJob;
	}

	public int getMonthsInCurrResidence() {
		return monthsInCurrResidence;
	}

	public void setMonthsInCurrResidence(int monthsInCurrResidence) {
		this.monthsInCurrResidence = monthsInCurrResidence;
	}

	public BigDecimal getRentPM() {
		return rentPM;
	}

	public void setRentPM(BigDecimal rentPM) {
		this.rentPM = rentPM;
	}

	public String getDeviation() {
		return deviation;
	}

	public void setDeviation(String deviation) {
		this.deviation = deviation;
	}

	public int getVehicleAge() {
		return vehicleAge;
	}

	public void setVehicleAge(int vehicleAge) {
		this.vehicleAge = vehicleAge;
	}

	public int getAppScore() {
		return appScore;
	}

	public void setAppScore(int appScore) {
		this.appScore = appScore;
	}

	public int getRicScore() {
		return ricScore;
	}

	public void setRicScore(int ricScore) {
		this.ricScore = ricScore;
	}

	public String getIncomeProofReceived() {
		return incomeProofReceived;
	}

	public void setIncomeProofReceived(String incomeProofReceived) {
		this.incomeProofReceived = incomeProofReceived;
	}

	public BigDecimal getTotalExperience() {
		return totalExperience;
	}

	public void setTotalExperience(BigDecimal totalExperience) {
		this.totalExperience = totalExperience;
	}

	public String getEmployerCategory() {
		return employerCategory;
	}

	public void setEmployerCategory(String employerCategory) {
		this.employerCategory = employerCategory;
	}

	public int getMonthsInCity() {
		return monthsInCity;
	}

	public void setMonthsInCity(int monthsInCity) {
		this.monthsInCity = monthsInCity;
	}

	public String getEligibility() {
		return eligibility;
	}

	public void setEligibility(String eligibility) {
		this.eligibility = eligibility;
	}

	public BigDecimal getObligations() {
		return obligations;
	}

	public void setObligations(BigDecimal obligations) {
		this.obligations = obligations;
	}

	public BigDecimal getObligationsn() {
		return obligationsn;
	}

	public void setObligationsn(BigDecimal obligationsn) {
		this.obligationsn = obligationsn;
	}

}
