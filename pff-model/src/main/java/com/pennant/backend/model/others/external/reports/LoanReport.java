package com.pennant.backend.model.others.external.reports;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class LoanReport extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;
	private String finReference;
	private String custName;
	private String custCIF;
	private String customerType;
	private String custCategory;
	private String finType;
	private String productDescription;
	private String scheme;
	private Date firstDisbDate;
	private Date lastDisbDate;
	private BigDecimal originalROI;
	private BigDecimal revisedROI = BigDecimal.ZERO;
	private BigDecimal sanctioAmount;
	private BigDecimal disbursementAmount;
	private BigDecimal unDisbursedAmount;
	private BigDecimal outstandingAmt_Loan_Adv;
	private BigDecimal oustandingAmt_LI_GI;
	private BigDecimal loanDebtors_Principal;
	private BigDecimal intrestAcrrualAmt;
	private BigDecimal loanDebtors_Interest;
	private BigDecimal totOutstandingAmt;
	private BigDecimal eclProvision;
	private BigDecimal propertyValue;
	private String propertyID;
	private String propertyDesc;
	private BigDecimal ltvRatio;
	private int originalTenure;
	private int revisedTenure;
	private BigDecimal interestIncome;
	private String npaStatus;
	private boolean loanStatus;
	private boolean employeeLoans;
	private BigDecimal nrmlPrincipalCollection;
	private BigDecimal pre_PaymentCollection;
	private BigDecimal foreclosureCollection;
	private Date finStartDate;
	private Date calMaturity;
	private boolean alwGrcPeriod;
	private String repayRateBasis;
	private String repayBaseRate;
	private String finCcy;
	private String repaySpecialRate;
	private BigDecimal repayMargin = BigDecimal.ZERO;
	private BigDecimal rpyMinRate = BigDecimal.ZERO;
	private BigDecimal rpyMaxRate = BigDecimal.ZERO;
	private int numberOfTerms = 0;
	private String propertyUsage;
	private String entity;
	private BigDecimal captilizedIntrest;
	private BigDecimal sanctionAmountVAS;
	private Date nextRepayDate;
	private String roundingMode;
	private int dpd;
	private int graceTerms;
	private String branchState;
	private String caste;
	private boolean quickDisb;
	private String disbTag;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private int roundingTarget;
	private String lovValue;
	private Date maturityDate;
	private List<CollateralAssignment> collateralAssignments;
	private List<FinAdvancePayments> finAdvancePayments;
	private List<VASRecording> vasRecordings;
	private List<FinanceScheduleDetail> financeScheduleDetails;
	private List<FinFeeDetail> finFeeDetails;
	private BigDecimal totalvasAmt = BigDecimal.ZERO;
	private BigDecimal totalLoanAmt = BigDecimal.ZERO;
	private BigDecimal totalDisbAmt = BigDecimal.ZERO;
	private BigDecimal loanRatio = BigDecimal.ZERO;
	private BigDecimal vasRatio = BigDecimal.ZERO;
	private BigDecimal loanOutStanding = BigDecimal.ZERO;
	private BigDecimal vasOutStanding = BigDecimal.ZERO;

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustCategory() {
		return custCategory;
	}

	public void setCustCategory(String custCategory) {
		this.custCategory = custCategory;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
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

	public BigDecimal getOriginalROI() {
		return originalROI;
	}

	public void setOriginalROI(BigDecimal originalROI) {
		this.originalROI = originalROI;
	}

	public BigDecimal getRevisedROI() {
		return revisedROI;
	}

	public void setRevisedROI(BigDecimal revisedROI) {
		this.revisedROI = revisedROI;
	}

	public BigDecimal getSanctioAmount() {
		return sanctioAmount;
	}

	public void setSanctioAmount(BigDecimal sanctioAmount) {
		this.sanctioAmount = sanctioAmount;
	}

	public BigDecimal getDisbursementAmount() {
		return disbursementAmount;
	}

	public void setDisbursementAmount(BigDecimal disbursementAmount) {
		this.disbursementAmount = disbursementAmount;
	}

	public BigDecimal getUnDisbursedAmount() {
		return unDisbursedAmount;
	}

	public void setUnDisbursedAmount(BigDecimal unDisbursedAmount) {
		this.unDisbursedAmount = unDisbursedAmount;
	}

	public BigDecimal getOutstandingAmt_Loan_Adv() {
		return outstandingAmt_Loan_Adv;
	}

	public void setOutstandingAmt_Loan_Adv(BigDecimal outstandingAmt_Loan_Adv) {
		this.outstandingAmt_Loan_Adv = outstandingAmt_Loan_Adv;
	}

	public BigDecimal getOustandingAmt_LI_GI() {
		return oustandingAmt_LI_GI;
	}

	public void setOustandingAmt_LI_GI(BigDecimal oustandingAmt_LI_GI) {
		this.oustandingAmt_LI_GI = oustandingAmt_LI_GI;
	}

	public BigDecimal getLoanDebtors_Principal() {
		return loanDebtors_Principal;
	}

	public void setLoanDebtors_Principal(BigDecimal loanDebtors_Principal) {
		this.loanDebtors_Principal = loanDebtors_Principal;
	}

	public BigDecimal getIntrestAcrrualAmt() {
		return intrestAcrrualAmt;
	}

	public void setIntrestAcrrualAmt(BigDecimal intrestAcrrualAmt) {
		this.intrestAcrrualAmt = intrestAcrrualAmt;
	}

	public BigDecimal getLoanDebtors_Interest() {
		return loanDebtors_Interest;
	}

	public void setLoanDebtors_Interest(BigDecimal loanDebtors_Interest) {
		this.loanDebtors_Interest = loanDebtors_Interest;
	}

	public BigDecimal getTotOutstandingAmt() {
		return totOutstandingAmt;
	}

	public void setTotOutstandingAmt(BigDecimal totOutstandingAmt) {
		this.totOutstandingAmt = totOutstandingAmt;
	}

	public BigDecimal getEclProvision() {
		return eclProvision;
	}

	public void setEclProvision(BigDecimal eclProvision) {
		this.eclProvision = eclProvision;
	}

	public BigDecimal getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(BigDecimal propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getPropertyID() {
		return propertyID;
	}

	public void setPropertyID(String propertyID) {
		this.propertyID = propertyID;
	}

	public String getPropertyDesc() {
		return propertyDesc;
	}

	public void setPropertyDesc(String propertyDesc) {
		this.propertyDesc = propertyDesc;
	}

	public BigDecimal getLtvRatio() {
		return ltvRatio;
	}

	public void setLtvRatio(BigDecimal ltvRatio) {
		this.ltvRatio = ltvRatio;
	}

	public int getOriginalTenure() {
		return originalTenure;
	}

	public void setOriginalTenure(int originalTenure) {
		this.originalTenure = originalTenure;
	}

	public int getRevisedTenure() {
		return revisedTenure;
	}

	public void setRevisedTenure(int revisedTenure) {
		this.revisedTenure = revisedTenure;
	}

	public BigDecimal getInterestIncome() {
		return interestIncome;
	}

	public void setInterestIncome(BigDecimal interestIncome) {
		this.interestIncome = interestIncome;
	}

	public String getNpaStatus() {
		return npaStatus;
	}

	public void setNpaStatus(String npaStatus) {
		this.npaStatus = npaStatus;
	}

	public BigDecimal getNrmlPrincipalCollection() {
		return nrmlPrincipalCollection;
	}

	public void setNrmlPrincipalCollection(BigDecimal nrmlPrincipalCollection) {
		this.nrmlPrincipalCollection = nrmlPrincipalCollection;
	}

	public BigDecimal getPre_PaymentCollection() {
		return pre_PaymentCollection;
	}

	public void setPre_PaymentCollection(BigDecimal pre_PaymentCollection) {
		this.pre_PaymentCollection = pre_PaymentCollection;
	}

	public BigDecimal getForeclosureCollection() {
		return foreclosureCollection;
	}

	public void setForeclosureCollection(BigDecimal foreclosureCollection) {
		this.foreclosureCollection = foreclosureCollection;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public Date getCalMaturity() {
		return calMaturity;
	}

	public void setCalMaturity(Date calMaturity) {
		this.calMaturity = calMaturity;
	}

	public boolean isEmployeeLoans() {
		return employeeLoans;
	}

	public void setEmployeeLoans(boolean employeeLoans) {
		this.employeeLoans = employeeLoans;
	}

	public boolean isLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(boolean loanStatus) {
		this.loanStatus = loanStatus;
	}

	public Boolean isAlwGrcPeriod() {
		return alwGrcPeriod;
	}

	public void setAlwGrcPeriod(Boolean alwGrcPeriod) {
		this.alwGrcPeriod = alwGrcPeriod;
	}

	public String getRepayRateBasis() {
		return repayRateBasis;
	}

	public void setRepayRateBasis(String repayRateBasis) {
		this.repayRateBasis = repayRateBasis;
	}

	public String getRepayBaseRate() {
		return repayBaseRate;
	}

	public void setRepayBaseRate(String repayBaseRate) {
		this.repayBaseRate = repayBaseRate;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getRepaySpecialRate() {
		return repaySpecialRate;
	}

	public void setRepaySpecialRate(String repaySpecialRate) {
		this.repaySpecialRate = repaySpecialRate;
	}

	public BigDecimal getRepayMargin() {
		return repayMargin;
	}

	public void setRepayMargin(BigDecimal repayMargin) {
		this.repayMargin = repayMargin;
	}

	public BigDecimal getRpyMinRate() {
		return rpyMinRate;
	}

	public void setRpyMinRate(BigDecimal rpyMinRate) {
		this.rpyMinRate = rpyMinRate;
	}

	public BigDecimal getRpyMaxRate() {
		return rpyMaxRate;
	}

	public void setRpyMaxRate(BigDecimal rpyMaxRate) {
		this.rpyMaxRate = rpyMaxRate;
	}

	public int getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public String getPropertyUsage() {
		return propertyUsage;
	}

	public void setPropertyUsage(String propertyUsage) {
		this.propertyUsage = propertyUsage;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public BigDecimal getCaptilizedIntrest() {
		return captilizedIntrest;
	}

	public void setCaptilizedIntrest(BigDecimal captilizedIntrest) {
		this.captilizedIntrest = captilizedIntrest;
	}

	public BigDecimal getSanctionAmountVAS() {
		return sanctionAmountVAS;
	}

	public void setSanctionAmountVAS(BigDecimal sanctionAmountVAS) {
		this.sanctionAmountVAS = sanctionAmountVAS;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public void setAlwGrcPeriod(boolean alwGrcPeriod) {
		this.alwGrcPeriod = alwGrcPeriod;
	}

	public Date getNextRepayDate() {
		return nextRepayDate;
	}

	public void setNextRepayDate(Date nextRepayDate) {
		this.nextRepayDate = nextRepayDate;
	}

	public String getRoundingMode() {
		return roundingMode;
	}

	public void setRoundingMode(String roundingMode) {
		this.roundingMode = roundingMode;
	}

	public int getDpd() {
		return dpd;
	}

	public void setDpd(int dpd) {
		this.dpd = dpd;
	}

	public int getGraceTerms() {
		return graceTerms;
	}

	public void setGraceTerms(int graceTerms) {
		this.graceTerms = graceTerms;
	}

	public String getBranchState() {
		return branchState;
	}

	public void setBranchState(String branchState) {
		this.branchState = branchState;
	}

	public String getCaste() {
		return caste;
	}

	public void setCaste(String caste) {
		this.caste = caste;
	}

	public boolean isQuickDisb() {
		return quickDisb;
	}

	public void setQuickDisb(boolean quickDisb) {
		this.quickDisb = quickDisb;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public String getDisbTag() {
		return disbTag;
	}

	public void setDisbTag(String disbTag) {
		this.disbTag = disbTag;
	}

	public int getRoundingTarget() {
		return roundingTarget;
	}

	public void setRoundingTarget(int roundingTarget) {
		this.roundingTarget = roundingTarget;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public List<CollateralAssignment> getCollateralAssignments() {
		return collateralAssignments;
	}

	public void setCollateralAssignments(List<CollateralAssignment> collateralAssignments) {
		this.collateralAssignments = collateralAssignments;
	}

	public List<FinAdvancePayments> getFinAdvancePayments() {
		return finAdvancePayments;
	}

	public void setFinAdvancePayments(List<FinAdvancePayments> finAdvancePayments) {
		this.finAdvancePayments = finAdvancePayments;
	}

	public List<VASRecording> getVasRecordings() {
		return vasRecordings;
	}

	public void setVasRecordings(List<VASRecording> vasRecordings) {
		this.vasRecordings = vasRecordings;
	}

	public List<FinanceScheduleDetail> getFinanceScheduleDetails() {
		return financeScheduleDetails;
	}

	public void setFinanceScheduleDetails(List<FinanceScheduleDetail> financeScheduleDetails) {
		this.financeScheduleDetails = financeScheduleDetails;
	}

	public List<FinFeeDetail> getFinFeeDetails() {
		return finFeeDetails;
	}

	public void setFinFeeDetails(List<FinFeeDetail> finFeeDetails) {
		this.finFeeDetails = finFeeDetails;
	}

	public BigDecimal getTotalvasAmt() {
		return totalvasAmt;
	}

	public void setTotalvasAmt(BigDecimal totalvasAmt) {
		this.totalvasAmt = totalvasAmt;
	}

	public BigDecimal getTotalLoanAmt() {
		return totalLoanAmt;
	}

	public void setTotalLoanAmt(BigDecimal totalLoanAmt) {
		this.totalLoanAmt = totalLoanAmt;
	}

	public BigDecimal getTotalDisbAmt() {
		return totalDisbAmt;
	}

	public void setTotalDisbAmt(BigDecimal totalDisbAmt) {
		this.totalDisbAmt = totalDisbAmt;
	}

	public BigDecimal getLoanRatio() {
		return loanRatio;
	}

	public void setLoanRatio(BigDecimal loanRatio) {
		this.loanRatio = loanRatio;
	}

	public BigDecimal getVasRatio() {
		return vasRatio;
	}

	public void setVasRatio(BigDecimal vasRatio) {
		this.vasRatio = vasRatio;
	}

	public BigDecimal getLoanOutStanding() {
		return loanOutStanding;
	}

	public void setLoanOutStanding(BigDecimal loanOutStanding) {
		this.loanOutStanding = loanOutStanding;
	}

	public BigDecimal getVasOutStanding() {
		return vasOutStanding;
	}

	public void setVasOutStanding(BigDecimal vasOutStanding) {
		this.vasOutStanding = vasOutStanding;
	}

}
