/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : Facility.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-11-2013 * * Modified Date :
 * 25-11-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 25-11-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.model.facility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.collateral.Collateral;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Facility table</b>.<br>
 * 
 */
public class Facility extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String cAFReference;
	private String facilityType;
	private long custID;
	private String custCIF;
	private String custShrtName;
	private String custCtgCode;
	private String custCoreBank;
	private Date custDOB;
	private String custTypeDesc;
	private Date startDate;
	private String presentingUnit;
	private String countryOfDomicile;
	private String countryOfDomicileName;
	private Date deadLine;
	private String countryOfRisk;
	private String countryOfRiskName;
	private Date establishedDate;
	private String natureOfBusiness;
	private String natureOfBusinessName;
	private String sICCode;
	private String countryManager;
	private String countryManagerName;
	private String customerRiskType;
	private String relationshipManager;
	private long customerGroup;
	private String customerGroupName;
	private String custGrpCodeName;
	private Date nextReviewDate;
	private String userRole;
	private String sICCodeName;
	private String customerRiskTypeName;
	
	private String reviewCenter;
	private String countryLimitAdeq;
	private String levelOfApproval;
	private BigDecimal	countryExposure;
	private BigDecimal countryLimit;
	private String custRelation;
	
	private String customerBackGround;
	private String strength;
	private String weaknesses;
	private String sourceOfRepayment;
	private String adequacyOfCashFlows;
	private String typesOfSecurities;
	private String guaranteeDescription;
	private String financialSummary;
	private String mitigants;
	
	private String purpose;
	private Date   interim;
	private String accountRelation;
	private String antiMoneyLaunderClear;
	private String limitAndAncillary;
	private String antiMoneyLaunderSection;
	private boolean overriddeCirculation;
	private boolean dedupFound = false;
	private boolean skipDedup = false;
	
	private boolean newRecord = false;
	private String lovValue;
	private Facility befImage;
	private LoggedInUser userDetails;
	
	private boolean abuser;
	private boolean securityCollateral;
	private BigDecimal amountBD = BigDecimal.ZERO;
	private BigDecimal amountUSD = BigDecimal.ZERO;
	private BigDecimal  maturity = BigDecimal.ZERO;
	
	private List<Collateral> collaterals=new ArrayList<Collateral>();
	private List<FacilityDetail> facilityDetails=new ArrayList<FacilityDetail>();
	private List<CustomerRating> customerRatings=new ArrayList<CustomerRating>();
	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();
	
	private List<FacilityReferenceDetail> checkList = new ArrayList<FacilityReferenceDetail>();
	private List<FacilityReferenceDetail> aggrementList = new ArrayList<FacilityReferenceDetail>();
	private List<FacilityReferenceDetail> scoringGroupList = new ArrayList<FacilityReferenceDetail>();
	private List<FacilityReferenceDetail> corpScoringGroupList = new ArrayList<FacilityReferenceDetail>();
	//Taken from finance details
	private List<FacilityReferenceDetail> finRefDetailsList;
	private CustomerEligibilityCheck customerEligibilityCheck;
	private Map<Long, Long> lovDescSelAnsCountMap = new HashMap<Long, Long>();
	private List<FinanceCheckListReference> financeCheckList = new ArrayList<FinanceCheckListReference>();
	//Scoring
	private boolean sufficientScore;
	private List<ScoringMetrics> finScoringMetricList = new ArrayList<ScoringMetrics>();
	private List<ScoringMetrics> nonFinScoringMetricList = new ArrayList<ScoringMetrics>();
	private HashMap<Long, List<ScoringSlab>> scoringSlabs = new HashMap<Long, List<ScoringSlab>>();
	private HashMap<Long, List<ScoringMetrics>> scoringMetrics = new HashMap<Long, List<ScoringMetrics>>();
	private List<FinanceScoreHeader> finScoreHeaderList = new ArrayList<FinanceScoreHeader>();
	private HashMap<Long, List<FinanceScoreDetail>> scoreDetailListMap = new HashMap<Long, List<FinanceScoreDetail>>();
	
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	
	
	public boolean isNew() {
		return isNewRecord();
	}

	public Facility(String cAFReference) {
		super();
		this.cAFReference=cAFReference;
	}

	public Facility() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("countryOfDomicileName");
		excludeFields.add("countryOfRiskName");
		excludeFields.add("natureOfBusinessName");
		excludeFields.add("countryManagerName");
		excludeFields.add("customerGroupName");
		excludeFields.add("custGrpCodeName");
		excludeFields.add("sICCodeName");
		excludeFields.add("customerRiskTypeName");
		
		excludeFields.add("custCIF");
		excludeFields.add("custShrtName");
		excludeFields.add("custCtgCode");
		excludeFields.add("custCoreBank");
		excludeFields.add("custDOB");
		excludeFields.add("custTypeDesc");
		
		excludeFields.add("userRole");
		
		excludeFields.add("aggrementList");
		excludeFields.add("checkList");
		excludeFields.add("scoringGroupList");
		excludeFields.add("corpScoringGroupList");
		excludeFields.add("documentDetailsList");
		
		excludeFields.add("customerEligibilityCheck");
		excludeFields.add("customerScoringCheck");
		
		excludeFields.add("finRefDetailsList");
		excludeFields.add("financeCheckList");
		
		excludeFields.add("sufficientScore");
		excludeFields.add("finScoringMetricList");
		excludeFields.add("nonFinScoringMetricList");
		excludeFields.add("scoringSlabs");
		excludeFields.add("scoringMetrics");
		excludeFields.add("finScoreHeaderList");
		excludeFields.add("scoreDetailListMap");
		excludeFields.add("collaterals");
		excludeFields.add("facilityDetails");
		excludeFields.add("customerRatings");

		excludeFields.add("abuser");
		excludeFields.add("securityCollateral");
		excludeFields.add("amountBD");
		excludeFields.add("amountUSD");
		excludeFields.add("maturity");
		excludeFields.add("custRelation");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return cAFReference;
	}

	public void setId(String id) {
		this.cAFReference = id;
	}

	public String getCAFReference() {
		return cAFReference;
	}

	public void setCAFReference(String cAFReference) {
		this.cAFReference = cAFReference;
	}

	public String getFacilityType() {
    	return facilityType;
    }

	public void setFacilityType(String facilityType) {
    	this.facilityType = facilityType;
    }

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}
	
	public String getCustCIF() {
    	return custCIF;
    }

	public void setCustCIF(String custCif) {
    	this.custCIF = custCif;
    }

	public String getCustShrtName() {
    	return custShrtName;
    }

	public void setCustShrtName(String custShrtName) {
    	this.custShrtName = custShrtName;
    }
	

	public String getCustCtgCode() {
    	return custCtgCode;
    }

	public void setCustCtgCode(String custCtgCode) {
    	this.custCtgCode = custCtgCode;
    }
	
	public String getCustCoreBank() {
    	return custCoreBank;
    }

	public void setCustCoreBank(String custCoreBank) {
    	this.custCoreBank = custCoreBank;
    }

	public Date getCustDOB() {
    	return custDOB;
    }

	public void setCustDOB(Date custDOB) {
    	this.custDOB = custDOB;
    }

	public String getCustTypeDesc() {
    	return custTypeDesc;
    }

	public void setCustTypeDesc(String custTypeDesc) {
    	this.custTypeDesc = custTypeDesc;
    }

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getPresentingUnit() {
		return presentingUnit;
	}

	public void setPresentingUnit(String presentingUnit) {
		this.presentingUnit = presentingUnit;
	}

	public String getCountryOfDomicile() {
		return countryOfDomicile;
	}

	public void setCountryOfDomicile(String countryOfDomicile) {
		this.countryOfDomicile = countryOfDomicile;
	}

	public String getCountryOfDomicileName() {
		return this.countryOfDomicileName;
	}

	public void setCountryOfDomicileName(String countryOfDomicileName) {
		this.countryOfDomicileName = countryOfDomicileName;
	}

	public Date getDeadLine() {
		return deadLine;
	}

	public void setDeadLine(Date deadLine) {
		this.deadLine = deadLine;
	}

	public String getCountryOfRisk() {
		return countryOfRisk;
	}

	public void setCountryOfRisk(String countryOfRisk) {
		this.countryOfRisk = countryOfRisk;
	}

	public String getCountryOfRiskName() {
		return this.countryOfRiskName;
	}

	public void setCountryOfRiskName(String countryOfRiskName) {
		this.countryOfRiskName = countryOfRiskName;
	}

	public Date getEstablishedDate() {
		return establishedDate;
	}

	public void setEstablishedDate(Date establishedDate) {
		this.establishedDate = establishedDate;
	}

	public String getNatureOfBusiness() {
		return natureOfBusiness;
	}

	public void setNatureOfBusiness(String natureOfBusiness) {
		this.natureOfBusiness = natureOfBusiness;
	}

	public String getNatureOfBusinessName() {
		return this.natureOfBusinessName;
	}

	public void setNatureOfBusinessName(String natureOfBusinessName) {
		this.natureOfBusinessName = natureOfBusinessName;
	}

	public String getSICCode() {
		return sICCode;
	}

	public void setSICCode(String sICCode) {
		this.sICCode = sICCode;
	}

	public String getCountryManager() {
		return countryManager;
	}

	public void setCountryManager(String countryManager) {
		this.countryManager = countryManager;
	}

	public String getCountryManagerName() {
		return this.countryManagerName;
	}

	public void setCountryManagerName(String countryManagerName) {
		this.countryManagerName = countryManagerName;
	}

	public String getCustomerRiskType() {
		return customerRiskType;
	}

	public void setCustomerRiskType(String customerRiskType) {
		this.customerRiskType = customerRiskType;
	}

	public String getRelationshipManager() {
		return relationshipManager;
	}

	public void setRelationshipManager(String relationshipManager) {
		this.relationshipManager = relationshipManager;
	}

	public long getCustomerGroup() {
		return customerGroup;
	}

	public void setCustomerGroup(long customerGroup) {
		this.customerGroup = customerGroup;
	}

	public String getCustomerGroupName() {
		return this.customerGroupName;
	}

	public void setCustomerGroupName(String customerGroupName) {
		this.customerGroupName = customerGroupName;
	}

	public String getCustRelation() {
    	return custRelation;
    }

	public void setCustRelation(String custRelation) {
    	this.custRelation = custRelation;
    }

	public Date getNextReviewDate() {
		return nextReviewDate;
	}

	public void setNextReviewDate(Date nextReviewDate) {
		this.nextReviewDate = nextReviewDate;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public Facility getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Facility beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<FacilityReferenceDetail> getCheckList() {
    	return checkList;
    }

	public void setCheckList(List<FacilityReferenceDetail> checkList) {
    	this.checkList = checkList;
    }

	public List<FacilityReferenceDetail> getAggrementList() {
    	return aggrementList;
    }

	public void setAggrementList(List<FacilityReferenceDetail> aggrementList) {
    	this.aggrementList = aggrementList;
    }

	public List<FacilityReferenceDetail> getScoringGroupList() {
    	return scoringGroupList;
    }

	public void setScoringGroupList(List<FacilityReferenceDetail> scoringGroupList) {
    	this.scoringGroupList = scoringGroupList;
    }

	public List<FacilityReferenceDetail> getCorpScoringGroupList() {
    	return corpScoringGroupList;
    }

	public void setCorpScoringGroupList(List<FacilityReferenceDetail> corpScoringGroupList) {
    	this.corpScoringGroupList = corpScoringGroupList;
    }

	public List<DocumentDetails> getDocumentDetailsList() {
    	return documentDetailsList;
    }

	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
    	this.documentDetailsList = documentDetailsList;
    }

	public CustomerEligibilityCheck getCustomerEligibilityCheck() {
    	return customerEligibilityCheck;
    }

	public void setCustomerEligibilityCheck(CustomerEligibilityCheck customerEligibilityCheck) {
    	this.customerEligibilityCheck = customerEligibilityCheck;
    }

	public void setFinRefDetailsList(List<FacilityReferenceDetail> finRefDetailsList) {
	    this.finRefDetailsList = finRefDetailsList;
    }

	public List<FacilityReferenceDetail> getFinRefDetailsList() {
	    return finRefDetailsList;
    }

	public void setLovDescSelAnsCountMap(Map<Long, Long> lovDescSelAnsCountMap) {
	    this.lovDescSelAnsCountMap = lovDescSelAnsCountMap;
    }

	public Map<Long, Long> getLovDescSelAnsCountMap() {
	    return lovDescSelAnsCountMap;
    }

	public void setFinanceCheckList(List<FinanceCheckListReference> financeCheckList) {
	    this.financeCheckList = financeCheckList;
    }
	

	public List<FinanceCheckListReference> getFinanceCheckList() {
	    return financeCheckList;
    }

	public List<ScoringMetrics> getFinScoringMetricList() {
    	return finScoringMetricList;
    }

	public void setFinScoringMetricList(List<ScoringMetrics> finScoringMetricList) {
    	this.finScoringMetricList = finScoringMetricList;
    }

	
	
	public List<ScoringMetrics> getNonFinScoringMetricList() {
    	return nonFinScoringMetricList;
    }

	public void setNonFinScoringMetricList(List<ScoringMetrics> nonFinScoringMetricList) {
    	this.nonFinScoringMetricList = nonFinScoringMetricList;
    }

	public void setFinScoreHeaderList(List<FinanceScoreHeader> finScoreHeaderList) {
	    this.finScoreHeaderList = finScoreHeaderList;
    }
	

	public List<FinanceScoreHeader> getFinScoreHeaderList() {
	    return finScoreHeaderList;
    }

	public void setScoringMetrics(Long id, List<ScoringMetrics> scoringMetrics) {
		if (this.scoringMetrics == null) {
			this.scoringMetrics = new HashMap<Long, List<ScoringMetrics>>();
		} else {
			if (this.scoringMetrics.containsKey(id)) {
				this.scoringMetrics.remove(id);
			}
		}
		this.scoringMetrics.put(id, scoringMetrics);
	}
	
	public void setScoringMetrics(HashMap<Long, List<ScoringMetrics>> scoringMetrics) {
		if (this.scoringMetrics == null) {
			this.scoringMetrics = new HashMap<Long, List<ScoringMetrics>>();
		} 
		this.scoringMetrics = scoringMetrics;
	}

	public HashMap<Long, List<ScoringMetrics>> getScoringMetrics() {
	    return scoringMetrics;
    }
	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
	    this.auditDetailMap = auditDetailMap;
    }

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
	    return auditDetailMap;
    }
	public void setScoringSlabs(Long id, List<ScoringSlab> scoringSlabs) {
		if (this.scoringSlabs == null) {
			this.scoringSlabs = new HashMap<Long, List<ScoringSlab>>();
		} else {
			if (this.scoringSlabs.containsKey(id)) {
				this.scoringSlabs.remove(id);
			}
		}
		this.scoringSlabs.put(id, scoringSlabs);
	}
	
	public void setScoringSlabs(HashMap<Long, List<ScoringSlab>> scoringSlabs) {
		if (this.scoringSlabs == null) {
			this.scoringSlabs = new HashMap<Long, List<ScoringSlab>>();
		} 
		this.scoringSlabs = scoringSlabs;
	}

	public HashMap<Long, List<ScoringSlab>> getScoringSlabs() {
	    return scoringSlabs;
    }

	public void setScoreDetailListMap(HashMap<Long, List<FinanceScoreDetail>> scoreDetailListMap) {
	    this.scoreDetailListMap = scoreDetailListMap;
    }

	public HashMap<Long, List<FinanceScoreDetail>> getScoreDetailListMap() {
	    return scoreDetailListMap;
    }

	public void setSufficientScore(boolean sufficientScore) {
	    this.sufficientScore = sufficientScore;
    }

	public boolean isSufficientScore() {
	    return sufficientScore;
    }

	public void setUserRole(String userRole) {
	    this.userRole = userRole;
    }

	public String getUserRole() {
	    return userRole;
    }

	public void setCollaterals(List<Collateral> collaterals) {
	    this.collaterals = collaterals;
    }

	public List<Collateral> getCollaterals() {
	    return collaterals;
    }

	public void setFacilityDetails(List<FacilityDetail> facilityDetails) {
	    this.facilityDetails = facilityDetails;
    }

	public List<FacilityDetail> getFacilityDetails() {
	    return facilityDetails;
    }

	public void setCustomerRatings(List<CustomerRating> customerRatings) {
	    this.customerRatings = customerRatings;
    }

	public List<CustomerRating> getCustomerRatings() {
	    return customerRatings;
    }

	public void setAbuser(boolean abuser) {
	    this.abuser = abuser;
    }

	public boolean isAbuser() {
	    return abuser;
    }

	public void setSecurityCollateral(boolean securityCollateral) {
	    this.securityCollateral = securityCollateral;
    }

	public boolean isSecurityCollateral() {
	    return securityCollateral;
    }

	public void setAmountBD(BigDecimal amountBD) {
	    this.amountBD = amountBD;
    }

	public BigDecimal getAmountBD() {
	    return amountBD;
    }

	public void setMaturity(BigDecimal maturity) {
	    this.maturity = maturity;
    }

	public BigDecimal getMaturity() {
	    return maturity;
    }

	public void setAmountUSD(BigDecimal amountUSD) {
	    this.amountUSD = amountUSD;
    }

	public BigDecimal getAmountUSD() {
	    return amountUSD;
    }

	public void setSICCodeName(String sICCodeName) {
	    this.sICCodeName = sICCodeName;
    }

	public String getSICCodeName() {
	    return sICCodeName;
    }

	public void setCustomerRiskTypeName(String customerRiskTypeName) {
	    this.customerRiskTypeName = customerRiskTypeName;
    }

	public String getCustomerRiskTypeName() {
	    return customerRiskTypeName;
    }

	public void setCustGrpCodeName(String custGrpCodeName) {
	    this.custGrpCodeName = custGrpCodeName;
    }

	public String getCustGrpCodeName() {
	    return custGrpCodeName;
    }

	public String getReviewCenter() {
    	return reviewCenter;
    }

	public void setReviewCenter(String reviewCenter) {
    	this.reviewCenter = reviewCenter;
    }

	public String getCountryLimitAdeq() {
    	return countryLimitAdeq;
    }

	public void setCountryLimitAdeq(String countryLimitAdeq) {
    	this.countryLimitAdeq = countryLimitAdeq;
    }

	public String getLevelOfApproval() {
    	return levelOfApproval;
    }

	public void setLevelOfApproval(String levelOfAoorival) {
    	this.levelOfApproval = levelOfAoorival;
    }

	public BigDecimal getCountryExposure() {
    	return countryExposure;
    }

	public void setCountryExposure(BigDecimal countryExposure) {
    	this.countryExposure = countryExposure;
    }

	public BigDecimal getCountryLimit() {
    	return countryLimit;
    }

	public void setCountryLimit(BigDecimal countryLimit) {
    	this.countryLimit = countryLimit;
    }

	public String getCustomerBackGround() {
	    return customerBackGround;
    }

	public void setCustomerBackGround(String customerBackGround) {
	    this.customerBackGround = customerBackGround;
    }

	public String getStrength() {
	    return strength;
    }

	public void setStrength(String strength) {
	    this.strength = strength;
    }

	public String getWeaknesses() {
	    return weaknesses;
    }

	public void setWeaknesses(String weaknesses) {
	    this.weaknesses = weaknesses;
    }

	public String getSourceOfRepayment() {
	    return sourceOfRepayment;
    }

	public void setSourceOfRepayment(String sourceOfRepayment) {
	    this.sourceOfRepayment = sourceOfRepayment;
    }

	public String getAdequacyOfCashFlows() {
	    return adequacyOfCashFlows;
    }

	public void setAdequacyOfCashFlows(String adequacyOfCashFlows) {
	    this.adequacyOfCashFlows = adequacyOfCashFlows;
    }

	public String getTypesOfSecurities() {
	    return typesOfSecurities;
    }

	public void setTypesOfSecurities(String typesOfSecurities) {
	    this.typesOfSecurities = typesOfSecurities;
    }

	public String getGuaranteeDescription() {
	    return guaranteeDescription;
    }

	public void setGuaranteeDescription(String guaranteeDescription) {
	    this.guaranteeDescription = guaranteeDescription;
    }

	public String getFinancialSummary() {
	    return financialSummary;
    }

	public void setFinancialSummary(String financialSummary) {
	    this.financialSummary = financialSummary;
    }

	public String getMitigants() {
	    return mitigants;
    }

	public void setMitigants(String mitigants) {
	    this.mitigants = mitigants;
    }

	public void setPurpose(String purpose) {
	    this.purpose = purpose;
    }

	public String getPurpose() {
	    return purpose;
    }

	public void setInterim(Date interim) {
	    this.interim = interim;
    }

	public Date getInterim() {
	    return interim;
    }

	public void setAccountRelation(String accountRelation) {
	    this.accountRelation = accountRelation;
    }

	public String getAccountRelation() {
	    return accountRelation;
    }

	public void setAntiMoneyLaunderClear(String antiMoneyLaunderClear) {
	    this.antiMoneyLaunderClear = antiMoneyLaunderClear;
    }

	public String getAntiMoneyLaunderClear() {
	    return antiMoneyLaunderClear;
    }

	public void setLimitAndAncillary(String limitAndAncillary) {
	    this.limitAndAncillary = limitAndAncillary;
    }

	public String getLimitAndAncillary() {
	    return limitAndAncillary;
    }

	public void setAntiMoneyLaunderSection(String antiMoneyLaunderSection) {
	    this.antiMoneyLaunderSection = antiMoneyLaunderSection;
    }

	public String getAntiMoneyLaunderSection() {
	    return antiMoneyLaunderSection;
    }

	public void setOverriddeCirculation(boolean overriddeCirculation) {
	    this.overriddeCirculation = overriddeCirculation;
    }

	public boolean isOverriddeCirculation() {
	    return overriddeCirculation;
    }

	public void setDedupFound(boolean dedupFound) {
	    this.dedupFound = dedupFound;
    }

	public boolean isDedupFound() {
	    return dedupFound;
    }

	public void setSkipDedup(boolean skipDedup) {
	    this.skipDedup = skipDedup;
    }

	public boolean isSkipDedup() {
	    return skipDedup;
    }	
}
