package com.pennant.backend.model.limits;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class LimitDetail implements Serializable {
	private static final long serialVersionUID = -9101415951762667428L;

	// request fields
	private String referenceNumber;
	private String limitRef;
	private String branchCode;

	// response fields
	private String custCIF;
	private String limitDesc;
	private String revolvingType;
	private Date limitExpiryDate;
	private String limitCcy;
	private String approvedLimitCcy;
	private BigDecimal approvedLimit = BigDecimal.ZERO;
	private String outstandingAmtCcy;
	private BigDecimal outstandingAmt = BigDecimal.ZERO;
	private String blockedAmtCcy;
	private BigDecimal blockedAmt = BigDecimal.ZERO;
	private String reservedAmtCcy;
	private BigDecimal reservedAmt = BigDecimal.ZERO;
	private String availableAmtCcy;
	private BigDecimal availableAmt = BigDecimal.ZERO;
	private int tenor;
	private String tenorUnit;
	private String repricingFrequency;
	private String repaymentTerm;
	private String limitAvailabilityPeriod;
	private Date finalMaturityDate;
	private String pricingIndex;
	private BigDecimal spread = BigDecimal.ZERO;
	private BigDecimal minimumPrice = BigDecimal.ZERO;
	private BigDecimal maximumPricing = BigDecimal.ZERO;
	private String pricingSchema;
	private BigDecimal commissionPercent = BigDecimal.ZERO;
	private BigDecimal commissionAmount = BigDecimal.ZERO;
	private String commissionFreq;
	private String studyFee;
	private BigDecimal margin = BigDecimal.ZERO;
	private BigDecimal hamJad = BigDecimal.ZERO;
	private BigDecimal otherFeePercent = BigDecimal.ZERO;
	private BigDecimal otherFeeAmount = BigDecimal.ZERO;
	private String covenant;
	private String termsConditions;
	private String notes;
	private String returnCode;
	private String returnText;
	private String timeStamp;
	
	//CustomerLimitService Required fields
	private long 		headerId;
	private String 		ruleCode;
	private String 		ruleValue;
	private String 		customerGroup;
	private String 		customerId;
	private String 		responsibleBranch;
	private String 		currency;
	private Date 		expiryDate;
	private Date 		reviewDate;

	private String 		limitStructureCode;
	private long 		detailId;
    private String 		limitItem;
   	private BigDecimal 	limitSanctioned;
	private BigDecimal 	calculatedLimit;
	private BigDecimal 	utilisedLimit;
	private boolean 	limitCheck;
	private String 		conditionRule;
	private String 		sqlRule;
	private String 		returnType;
	
	public LimitDetail() {
		//
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getLimitRef() {
		return limitRef;
	}

	public void setLimitRef(String limitRef) {
		this.limitRef = limitRef;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getLimitDesc() {
		return limitDesc;
	}

	public void setLimitDesc(String limitDesc) {
		this.limitDesc = limitDesc;
	}

	public String getRevolvingType() {
		return revolvingType;
	}

	public void setRevolvingType(String revolvingType) {
		this.revolvingType = revolvingType;
	}

	public Date getLimitExpiryDate() {
		return limitExpiryDate;
	}

	public void setLimitExpiryDate(Date limitExpiryDate) {
		this.limitExpiryDate = limitExpiryDate;
	}

	public String getLimitCcy() {
		return limitCcy;
	}

	public void setLimitCcy(String limitCcy) {
		this.limitCcy = limitCcy;
	}

	public String getApprovedLimitCcy() {
		return approvedLimitCcy;
	}

	public void setApprovedLimitCcy(String approvedLimitCcy) {
		this.approvedLimitCcy = approvedLimitCcy;
	}

	public BigDecimal getApprovedLimit() {
		return approvedLimit;
	}

	public void setApprovedLimit(BigDecimal approvedLimit) {
		this.approvedLimit = approvedLimit;
	}

	public String getOutstandingAmtCcy() {
		return outstandingAmtCcy;
	}

	public void setOutstandingAmtCcy(String outstandingAmtCcy) {
		this.outstandingAmtCcy = outstandingAmtCcy;
	}

	public BigDecimal getOutstandingAmt() {
		return outstandingAmt;
	}

	public void setOutstandingAmt(BigDecimal outstandingAmt) {
		this.outstandingAmt = outstandingAmt;
	}

	public String getBlockedAmtCcy() {
		return blockedAmtCcy;
	}

	public void setBlockedAmtCcy(String blockedAmtCcy) {
		this.blockedAmtCcy = blockedAmtCcy;
	}

	public BigDecimal getBlockedAmt() {
		return blockedAmt;
	}

	public void setBlockedAmt(BigDecimal blockedAmt) {
		this.blockedAmt = blockedAmt;
	}

	public String getReservedAmtCcy() {
		return reservedAmtCcy;
	}

	public void setReservedAmtCcy(String reservedAmtCcy) {
		this.reservedAmtCcy = reservedAmtCcy;
	}

	public BigDecimal getReservedAmt() {
		return reservedAmt;
	}

	public void setReservedAmt(BigDecimal reservedAmt) {
		this.reservedAmt = reservedAmt;
	}

	public String getAvailableAmtCcy() {
		return availableAmtCcy;
	}

	public void setAvailableAmtCcy(String availableAmtCcy) {
		this.availableAmtCcy = availableAmtCcy;
	}

	public BigDecimal getAvailableAmt() {
		return availableAmt;
	}

	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}

	public int getTenor() {
		return tenor;
	}

	public void setTenor(int tenor) {
		this.tenor = tenor;
	}

	public String getTenorUnit() {
		return tenorUnit;
	}

	public void setTenorUnit(String tenorUnit) {
		this.tenorUnit = tenorUnit;
	}

	public String getRepricingFrequency() {
		return repricingFrequency;
	}

	public void setRepricingFrequency(String repricingFrequency) {
		this.repricingFrequency = repricingFrequency;
	}

	public String getRepaymentTerm() {
		return repaymentTerm;
	}

	public void setRepaymentTerm(String repaymentTerm) {
		this.repaymentTerm = repaymentTerm;
	}

	public String getLimitAvailabilityPeriod() {
		return limitAvailabilityPeriod;
	}

	public void setLimitAvailabilityPeriod(String limitAvailabilityPeriod) {
		this.limitAvailabilityPeriod = limitAvailabilityPeriod;
	}

	public Date getFinalMaturityDate() {
		return finalMaturityDate;
	}

	public void setFinalMaturityDate(Date finalMaturityDate) {
		this.finalMaturityDate = finalMaturityDate;
	}

	public String getPricingIndex() {
		return pricingIndex;
	}

	public void setPricingIndex(String pricingIndex) {
		this.pricingIndex = pricingIndex;
	}

	public BigDecimal getSpread() {
		return spread;
	}

	public void setSpread(BigDecimal spread) {
		this.spread = spread;
	}

	public BigDecimal getMinimumPrice() {
		return minimumPrice;
	}

	public BigDecimal getMaximumPricing() {
		return maximumPricing;
	}

	public void setMaximumPricing(BigDecimal maximumPricing) {
		this.maximumPricing = maximumPricing;
	}

	public void setMinimumPrice(BigDecimal minimumPrice) {
		this.minimumPrice = minimumPrice;
	}

	public String getPricingSchema() {
		return pricingSchema;
	}

	public void setPricingSchema(String pricingSchema) {
		this.pricingSchema = pricingSchema;
	}

	public BigDecimal getCommissionPercent() {
		return commissionPercent;
	}

	public void setCommissionPercent(BigDecimal commissionPercent) {
		this.commissionPercent = commissionPercent;
	}

	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}

	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	public String getCommissionFreq() {
		return commissionFreq;
	}

	public void setCommissionFreq(String commissionFreq) {
		this.commissionFreq = commissionFreq;
	}

	public String getStudyFee() {
		return studyFee;
	}

	public void setStudyFee(String studyFee) {
		this.studyFee = studyFee;
	}

	public BigDecimal getMargin() {
		return margin;
	}

	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}

	public BigDecimal getHamJad() {
		return hamJad;
	}

	public void setHamJad(BigDecimal hamJad) {
		this.hamJad = hamJad;
	}

	public BigDecimal getOtherFeePercent() {
		return otherFeePercent;
	}

	public void setOtherFeePercent(BigDecimal otherFeePercent) {
		this.otherFeePercent = otherFeePercent;
	}

	public BigDecimal getOtherFeeAmount() {
		return otherFeeAmount;
	}

	public void setOtherFeeAmount(BigDecimal otherFeeAmount) {
		this.otherFeeAmount = otherFeeAmount;
	}

	public String getCovenant() {
		return covenant;
	}

	public void setCovenant(String covenant) {
		this.covenant = covenant;
	}

	public String getTermsConditions() {
		return termsConditions;
	}

	public void setTermsConditions(String termsConditions) {
		this.termsConditions = termsConditions;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getRuleValue() {
		return ruleValue;
	}

	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}

	public String getCustomerGroup() {
		return customerGroup;
	}

	public void setCustomerGroup(String customerGroup) {
		this.customerGroup = customerGroup;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getResponsibleBranch() {
		return responsibleBranch;
	}

	public void setResponsibleBranch(String responsibleBranch) {
		this.responsibleBranch = responsibleBranch;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getLimitStructureCode() {
		return limitStructureCode;
	}

	public void setLimitStructureCode(String limitStructureCode) {
		this.limitStructureCode = limitStructureCode;
	}

	public long getDetailId() {
		return detailId;
	}

	public void setDetailId(long detailId) {
		this.detailId = detailId;
	}

	public String getLimitItem() {
		return limitItem;
	}

	public void setLimitItem(String limitItem) {
		this.limitItem = limitItem;
	}

	public BigDecimal getLimitSanctioned() {
		return limitSanctioned;
	}

	public void setLimitSanctioned(BigDecimal limitSanctioned) {
		this.limitSanctioned = limitSanctioned;
	}

	public BigDecimal getCalculatedLimit() {
		return calculatedLimit;
	}

	public void setCalculatedLimit(BigDecimal calculatedLimit) {
		this.calculatedLimit = calculatedLimit;
	}

	public BigDecimal getUtilisedLimit() {
		return utilisedLimit;
	}

	public void setUtilisedLimit(BigDecimal utilisedLimit) {
		this.utilisedLimit = utilisedLimit;
	}

	public boolean isLimitCheck() {
		return limitCheck;
	}

	public void setLimitCheck(boolean limitCheck) {
		this.limitCheck = limitCheck;
	}

	public String getConditionRule() {
		return conditionRule;
	}

	public void setConditionRule(String conditionRule) {
		this.conditionRule = conditionRule;
	}
	
	public String getSqlRule() {
		return sqlRule;
	}

	public void setSqlRule(String sqlRule) {
		this.sqlRule = sqlRule;
	}
	
	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
}
