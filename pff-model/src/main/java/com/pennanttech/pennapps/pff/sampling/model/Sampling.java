package com.pennanttech.pennapps.pff.sampling.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class Sampling extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private String keyReference;
	private long createdBy;
	private Date createdOn;
	private BigDecimal interestRate;
	private Integer tenure;
	private BigDecimal foirEligibility;
	private BigDecimal emi;
	private BigDecimal loanEligibility;
	private BigDecimal irrEligibility;
	private BigDecimal totalIncome;
	private BigDecimal totalLiability;

	private String remarks;
	private Integer decision;
	private String decisionRemarks;
	private BigDecimal recommendedAmount;
	private Long resubmitReason;
	private String resubmitReasonDesc;
	private Date samplingOn;
	private Date decisionOn;
	private String collateralReference;
	private String custCategory;
	private String resubmitReasonCode;

	private int custId;
	private String custCif;
	private String custShrtName;
	private String finType;
	private String finTypeDesc;
	private String branchCode;
	private String branchDesc;
	private String finGrcRateType;
	private BigDecimal repaySpecialRate = BigDecimal.ZERO;
	private BigDecimal repayProfitRate;
	private int numberOfTerms;
	private String finccy;
	private int ccyeditfield;
	private BigDecimal loanAmountRequested;
	private BigDecimal originalLoanEligibility;
	private Map<String, String> eligibilityRules = new HashMap<>();

	private List<CustomerIncome> customerIncomeList;
	private List<CustomerExtLiability> customerExtLiabilityList;
	private List<CollateralSetup> collSetupList = new LinkedList<>();
	private CollateralSetup collateralSetup;
	private CustomerDetails customerDetails;
	private ExtendedFieldHeader extendedFieldHeader;
	private Map<String, ExtendedFieldHeader> extFieldHeaderList;
	private ExtendedFieldRender extendedFieldRender;
	private Map<String, ExtendedFieldRender> extFieldRenderList = new LinkedHashMap<>();
	private List<Customer> customers;
	private List<DocumentDetails> documents = null;
	private Map<String, Object> reamrksMap = new HashMap<>();

	/**
	 * Sampling approve details
	 */

	private Long incomeLinkId;
	private Long liabilityLinkId;
	private Long collateralLinkId;

	private BigDecimal originalTotalIncome = BigDecimal.ZERO;
	private BigDecimal originalTotalLiability = BigDecimal.ZERO;

	private Sampling befImage;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private LoggedInUser userDetails;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

	public static final String REQ_LOAN_AMOUNT_EXTEND_FIELD = "CUSTREQLOANAMOUNT";
	public static final String RULE_CODE_FOIRAMT = "FOIRAMT";
	public static final String RULE_CODE_IIRMAX = "IIRMAX";
	public static final String RULE_CODE_EMI = "EMI";

	public Sampling() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("custCif");
		excludeFields.add("custName");
		excludeFields.add("loanType");
		excludeFields.add("linkId");
		excludeFields.add("tenure");
		excludeFields.add("recommendedAmount");
		excludeFields.add("custId");
		excludeFields.add("custShrtN0ame");
		excludeFields.add("finType");
		excludeFields.add("finTypeDesc");
		excludeFields.add("branchCode");
		excludeFields.add("branchDesc");
		excludeFields.add("repayProfitRate");
		excludeFields.add("customerDetails");
		excludeFields.add("customers");
		excludeFields.add("numberOfTerms");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("extendedFieldRender");
		excludeFields.add("collateralSetup");

		excludeFields.add("incomeLinkId");
		excludeFields.add("liabilityLinkId");
		excludeFields.add("collateralLinkId");
		excludeFields.add("totalIncome");
		excludeFields.add("totalLiability");
		excludeFields.add("totalSamplingIncome");
		excludeFields.add("totalSamplingLiability");
		excludeFields.add("decisionRemarks");
		excludeFields.add("calculatedRate");

		excludeFields.add("eligibilityRules");
		excludeFields.add("loanAmountRequested");
		excludeFields.add("resubmitReasonDesc");
		excludeFields.add("REQ_LOAN_AMOUNT_EXTEND_FIELD");
		excludeFields.add("RULE_CODE_FOIRAMT");
		excludeFields.add("RULE_CODE_IIRMAX");
		excludeFields.add("RULE_CODE_EMI");
		excludeFields.add("finccy");
		excludeFields.add("ccyeditfield");
		excludeFields.add("collateralReference");
		excludeFields.add("foirAmount");
		excludeFields.add("irrMax");
		excludeFields.add("emi");
		excludeFields.add("custCategory");
		excludeFields.add("decisionRemarks");
		excludeFields.add("calculatedRate");
		excludeFields.add("finccy");
		excludeFields.add("ccyeditfield");
		excludeFields.add("resubmitReasonCode");
		excludeFields.add("custShrtName");
		excludeFields.add("extFieldHeaderList");
		excludeFields.add("repaySpecialRate");
		excludeFields.add("finGrcRateType");

		excludeFields.add("originalLoanEligibility");
		excludeFields.add("originalTotalIncome");
		excludeFields.add("originalTotalLiability");

		excludeFields.add("documents");
		excludeFields.add("reamrksMap");

		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/*
	 * public long getLinkId() { return linkId; }
	 * 
	 * public void setLinkId(long linkId) { this.linkId = linkId; }
	 */

	public String getKeyReference() {
		return keyReference;
	}

	public void setKeyReference(String keyReference) {
		this.keyReference = keyReference;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public BigDecimal getInterestRate() {
		return interestRate == null ? BigDecimal.ZERO : interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public Integer getTenure() {
		return tenure;
	}

	public void setTenure(Integer tenure) {
		this.tenure = tenure;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getDecision() {
		return decision;
	}

	public void setDecision(Integer decision) {
		this.decision = decision;
	}

	public String getDecisionRemarks() {
		return decisionRemarks;
	}

	public void setDecisionRemarks(String decisionRemarks) {
		this.decisionRemarks = decisionRemarks;
	}

	public BigDecimal getRecommendedAmount() {
		return recommendedAmount;
	}

	public void setRecommendedAmount(BigDecimal recommendedAmount) {
		this.recommendedAmount = recommendedAmount;
	}

	public Long getResubmitReason() {
		return resubmitReason;
	}

	public void setResubmitReason(Long resubmitReason) {
		this.resubmitReason = resubmitReason;
	}

	public String getResubmitReasonDesc() {
		return resubmitReasonDesc;
	}

	public void setResubmitReasonDesc(String resubmitReasonDesc) {
		this.resubmitReasonDesc = resubmitReasonDesc;
	}

	public Date getSamplingOn() {
		return samplingOn;
	}

	public void setSamplingOn(Date samplingOn) {
		this.samplingOn = samplingOn;
	}

	public Date getDecisionOn() {
		return decisionOn;
	}

	public void setDecisionOn(Date decisionOn) {
		this.decisionOn = decisionOn;
	}

	public String getCollateralReference() {
		return collateralReference;
	}

	public void setCollateralReference(String collateralReference) {
		this.collateralReference = collateralReference;
	}

	public int getCustId() {
		return custId;
	}

	public void setCustId(int custId) {
		this.custId = custId;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getFinGrcRateType() {
		return finGrcRateType;
	}

	public void setFinGrcRateType(String finGrcRateType) {
		this.finGrcRateType = finGrcRateType;
	}

	public BigDecimal getRepaySpecialRate() {
		return repaySpecialRate == null ? BigDecimal.ZERO : repaySpecialRate;
	}

	public void setRepaySpecialRate(BigDecimal repaySpecialRate) {
		this.repaySpecialRate = repaySpecialRate;
	}

	public BigDecimal getRepayProfitRate() {
		return repayProfitRate == null ? BigDecimal.ZERO : repayProfitRate;
	}

	public void setRepayProfitRate(BigDecimal repayProfitRate) {
		this.repayProfitRate = repayProfitRate;
	}

	public int getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public String getFinccy() {
		return finccy;
	}

	public void setFinccy(String finccy) {
		this.finccy = finccy;
	}

	public int getCcyeditfield() {
		return ccyeditfield;
	}

	public void setCcyeditfield(int ccyeditfield) {
		this.ccyeditfield = ccyeditfield;
	}

	public Sampling getBefImage() {
		return befImage;
	}

	public void setBefImage(Sampling befImage) {
		this.befImage = befImage;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public List<CustomerIncome> getCustomerIncomeList() {
		return customerIncomeList;
	}

	public void setCustomerIncomeList(List<CustomerIncome> customerIncomeList) {
		this.customerIncomeList = customerIncomeList;
	}

	public List<CustomerExtLiability> getCustomerExtLiabilityList() {
		return customerExtLiabilityList;
	}

	public void setCustomerExtLiabilityList(List<CustomerExtLiability> customerExtLiabilityList) {
		this.customerExtLiabilityList = customerExtLiabilityList;
	}

	public List<CollateralSetup> getCollSetupList() {
		return collSetupList;
	}

	public void setCollSetupList(List<CollateralSetup> collSetupList) {
		this.collSetupList = collSetupList;
	}

	public CollateralSetup getCollateralSetup() {
		return collateralSetup;
	}

	public void setCollateralSetup(CollateralSetup collateralSetup) {
		this.collateralSetup = collateralSetup;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public Map<String, ExtendedFieldHeader> getExtFieldHeaderList() {
		return extFieldHeaderList;
	}

	public void setExtFieldHeaderList(Map<String, ExtendedFieldHeader> extFieldHeaderList) {
		this.extFieldHeaderList = extFieldHeaderList;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public Map<String, ExtendedFieldRender> getExtFieldRenderList() {
		return extFieldRenderList;
	}

	public void setExtFieldRenderList(Map<String, ExtendedFieldRender> extFieldRenderList) {
		this.extFieldRenderList = extFieldRenderList;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public List<DocumentDetails> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentDetails> documents) {
		this.documents = documents;
	}

	public Long getIncomeLinkId() {
		return incomeLinkId;
	}

	public void setIncomeLinkId(Long incomeLinkId) {
		this.incomeLinkId = incomeLinkId;
	}

	public Long getLiabilityLinkId() {
		return liabilityLinkId;
	}

	public void setLiabilityLinkId(Long liabilityLinkId) {
		this.liabilityLinkId = liabilityLinkId;
	}

	public Long getCollateralLinkId() {
		return collateralLinkId;
	}

	public void setCollateralLinkId(Long collateralLinkId) {
		this.collateralLinkId = collateralLinkId;
	}

	public BigDecimal getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(BigDecimal totalIncome) {
		this.totalIncome = totalIncome;
	}

	public BigDecimal getTotalLiability() {
		return totalLiability;
	}

	public void setTotalLiability(BigDecimal totalLiability) {
		this.totalLiability = totalLiability;
	}

	public BigDecimal getLoanAmountRequested() {
		return loanAmountRequested;
	}

	public void setLoanAmountRequested(BigDecimal loanAmountRequested) {
		this.loanAmountRequested = loanAmountRequested;
	}

	public BigDecimal getOriginalLoanEligibility() {
		return originalLoanEligibility;
	}

	public void setOriginalLoanEligibility(BigDecimal originalLoanEligibility) {
		this.originalLoanEligibility = originalLoanEligibility;
	}

	public Map<String, String> getEligibilityRules() {
		return eligibilityRules;
	}

	public void setEligibilityRules(Map<String, String> eligibilityRules) {
		this.eligibilityRules = eligibilityRules;
	}

	public String getCustCategory() {
		return custCategory;
	}

	public void setCustCategory(String custCategory) {
		this.custCategory = custCategory;
	}

	public String getResubmitReasonCode() {
		return resubmitReasonCode;
	}

	public void setResubmitReasonCode(String resubmitReasonCode) {
		this.resubmitReasonCode = resubmitReasonCode;
	}

	public BigDecimal getFoirEligibility() {
		return foirEligibility;
	}

	public void setFoirEligibility(BigDecimal foirEligibility) {
		this.foirEligibility = foirEligibility;
	}

	public BigDecimal getEmi() {
		return emi;
	}

	public void setEmi(BigDecimal emi) {
		this.emi = emi;
	}

	public BigDecimal getLoanEligibility() {
		return loanEligibility;
	}

	public void setLoanEligibility(BigDecimal loanEligibility) {
		this.loanEligibility = loanEligibility;
	}

	public BigDecimal getIrrEligibility() {
		return irrEligibility;
	}

	public void setIrrEligibility(BigDecimal irrEligibility) {
		this.irrEligibility = irrEligibility;
	}

	public BigDecimal getOriginalTotalIncome() {
		return originalTotalIncome;
	}

	public void setOriginalTotalIncome(BigDecimal originalTotalIncome) {
		this.originalTotalIncome = originalTotalIncome;
	}

	public BigDecimal getOriginalTotalLiability() {
		return originalTotalLiability;
	}

	public void setOriginalTotalLiability(BigDecimal originalTotalLiability) {
		this.originalTotalLiability = originalTotalLiability;
	}

	public Map<String, Object> getReamrksMap() {
		return reamrksMap;
	}

	public void setReamrksMap(Map<String, Object> reamrksMap) {
		this.reamrksMap = reamrksMap;
	}

}
