package com.pennanttech.pennapps.pff.sampling.model;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.loanquery.QueryDetail;
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
	private String samplingTolerance;
	private List<SamplingDetail> samplingDetailsList = new ArrayList<>();

	private int custId;
	private String custCif;
	private String custShrtName;
	private String finType;
	private String finTypeDesc;
	private String branchCode;
	private String branchDesc;
	private String finGrcRateType;
	private BigDecimal repaySpecialRate;
	private BigDecimal repayProfitRate;
	private BigDecimal repayMinRate;
	private int numberOfTerms;
	private String finccy;
	private int ccyeditfield;
	private BigDecimal loanAmountRequested;
	private BigDecimal originalLoanEligibility;
	private Map<String, String> eligibilityRules = new HashMap<>();

	private List<CustomerIncome> customerIncomeList;
	private List<CustomerExtLiability> customerExtLiabilityList;
	private List<SamplingCollateral> collaterals = new LinkedList<>();
	private SamplingCollateral collateral;
	private CustomerDetails customerDetails;
	private ExtendedFieldHeader extendedFieldHeader;
	private Map<String, ExtendedFieldHeader> extFieldHeaderList;
	private ExtendedFieldRender extendedFieldRender;
	private Map<String, ExtendedFieldRender> extFieldRenderList = new LinkedHashMap<>();
	private List<Customer> customers;
	private transient List<DocumentDetails> documents = null;
	private transient Map<String, Object> reamrksMap = new HashMap<>();
	private QueryDetail queryDetail = new QueryDetail();
	private BigDecimal totalCustomerExposre;
	private BigDecimal totalCoApplicantsExposre;

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
	public static final String RULE_CODE_EMI = "PMT1LAKH";

	public Sampling() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("custCif");
		excludeFields.add("custName");
		excludeFields.add("loanType");
		excludeFields.add("linkId");
		excludeFields.add("recommendedAmount");
		excludeFields.add("custId");
		excludeFields.add("custShrtN0ame");
		excludeFields.add("finType");
		excludeFields.add("finTypeDesc");
		excludeFields.add("branchCode");
		excludeFields.add("branchDesc");
		excludeFields.add("repayProfitRate");
		excludeFields.add("repayMinRate");
		excludeFields.add("customerDetails");
		excludeFields.add("customers");
		excludeFields.add("numberOfTerms");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("extendedFieldRender");
		excludeFields.add("collateral");
		excludeFields.add("collaterals");

		excludeFields.add("incomeLinkId");
		excludeFields.add("liabilityLinkId");
		excludeFields.add("collateralLinkId");
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
		excludeFields.add("queryDetail");
		excludeFields.add("samplingTolerance");
		excludeFields.add("samplingDetailsList");
		
		excludeFields.add("totalCustomerExposre");
		excludeFields.add("totalCoApplicantsExposre");

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
	
	public BigDecimal getRepayMinRate() {
		return repayMinRate == null ? BigDecimal.ZERO : repayMinRate;
	}

	public void setRepayMinRate(BigDecimal repayMinRate) {
		this.repayMinRate = repayMinRate;
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
	
	public List<SamplingCollateral> getCollaterals() {
		return collaterals;
	}

	public void setCollaterals(List<SamplingCollateral> collaterals) {
		this.collaterals = collaterals;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static String getReqLoanAmountExtendField() {
		return REQ_LOAN_AMOUNT_EXTEND_FIELD;
	}

	public static String getRuleCodeFoiramt() {
		return RULE_CODE_FOIRAMT;
	}

	public static String getRuleCodeIirmax() {
		return RULE_CODE_IIRMAX;
	}

	public static String getRuleCodeEmi() {
		return RULE_CODE_EMI;
	}
	
	public SamplingCollateral getCollateral() {
		return collateral;
	}

	public void setCollateral(SamplingCollateral collateral) {
		this.collateral = collateral;
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
		return totalLiability == null ? BigDecimal.ZERO : totalLiability;
	}

	public void setTotalLiability(BigDecimal totalLiability) {
		this.totalLiability = totalLiability;
	}

	public BigDecimal getLoanAmountRequested() {
		return loanAmountRequested == null ? BigDecimal.ZERO : loanAmountRequested;
	}

	public void setLoanAmountRequested(BigDecimal loanAmountRequested) {
		this.loanAmountRequested = loanAmountRequested;
	}

	public BigDecimal getOriginalLoanEligibility() {
		return originalLoanEligibility == null ? BigDecimal.ZERO : originalLoanEligibility;
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
		return foirEligibility == null ? BigDecimal.ZERO:foirEligibility;
	}

	public void setFoirEligibility(BigDecimal foirEligibility) {
		this.foirEligibility = foirEligibility;
	}

	public BigDecimal getEmi() {
		return emi == null ? BigDecimal.ZERO:emi;
	}

	public void setEmi(BigDecimal emi) {
		this.emi = emi;
	}

	public BigDecimal getLoanEligibility() {
		return loanEligibility == null ? BigDecimal.ZERO:loanEligibility;
	}

	public void setLoanEligibility(BigDecimal loanEligibility) {
		this.loanEligibility = loanEligibility;
	}

	public BigDecimal getIrrEligibility() {
		return irrEligibility == null ? BigDecimal.ZERO:irrEligibility;
	}

	public void setIrrEligibility(BigDecimal irrEligibility) {
		this.irrEligibility = irrEligibility;
	}

	public BigDecimal getOriginalTotalIncome() {
		return originalTotalIncome == null ? BigDecimal.ZERO:originalTotalIncome;
	}

	public void setOriginalTotalIncome(BigDecimal originalTotalIncome) {
		this.originalTotalIncome = originalTotalIncome;
	}

	public BigDecimal getOriginalTotalLiability() {
		return originalTotalLiability == null ? BigDecimal.ZERO:originalTotalLiability;
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

	public QueryDetail getQueryDetail() {
		return queryDetail;
	}

	public void setQueryDetail(QueryDetail queryDetail) {
		this.queryDetail = queryDetail;
	}

	public List<SamplingDetail> getSamplingDetailsList() {
		return samplingDetailsList;
	}

	public void setSamplingDetailsList(List<SamplingDetail> samplingDetailsList) {
		this.samplingDetailsList = samplingDetailsList;
	}

	public String getSamplingTolerance() {
		return samplingTolerance;
	}

	public void setSamplingTolerance(String samplingTolerance) {
		this.samplingTolerance = samplingTolerance;
	}

	public BigDecimal getTotalCustomerExposre() {
		return totalCustomerExposre == null ? BigDecimal.ZERO : totalCustomerExposre;
	}

	public void setTotalCustomerExposre(BigDecimal totalCustomerExposre) {
		this.totalCustomerExposre = totalCustomerExposre;
	}

	public BigDecimal getTotalCoApplicantsExposre() {
		return totalCoApplicantsExposre == null ? BigDecimal.ZERO : totalCoApplicantsExposre;
	}

	public void setTotalCoApplicantsExposre(BigDecimal totalCoApplicantsExposre) {
		this.totalCoApplicantsExposre = totalCoApplicantsExposre;
	}

}
