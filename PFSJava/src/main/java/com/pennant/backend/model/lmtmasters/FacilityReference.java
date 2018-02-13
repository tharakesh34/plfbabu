package com.pennant.backend.model.lmtmasters;

import java.util.List;

import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FacilityReference {

	private String finType;
	private String lovDescFinTypeDescName;
	
	private List<FacilityReferenceDetail> checkList;
	private List<FacilityReferenceDetail> aggrementList;
	private List<FacilityReferenceDetail> eligibilityRuleList;
	private List<FacilityReferenceDetail> scoringGroupList;
	private List<FacilityReferenceDetail> corpScoringGroupList;
	private List<FacilityReferenceDetail> accountingList;
	private List<FacilityReferenceDetail> mailTemplateList;
	
	private String workFlowType;
	private String lovDescWorkFlowTypeName;
	private String lovDescWorkFlowRolesName;

	private FacilityReference befImage;
	private LoggedInUser userDetails;

	public FacilityReference() {
		super();
	}

	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getLovDescFinTypeDescName() {
		return lovDescFinTypeDescName;
	}
	public void setLovDescFinTypeDescName(String lovDescFinTypeDescName) {
		this.lovDescFinTypeDescName = lovDescFinTypeDescName;
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

	public List<FacilityReferenceDetail> getEligibilityRuleList() {
		return eligibilityRuleList;
	}
	public void setEligibilityRuleList(List<FacilityReferenceDetail> eligibilityRuleList) {
		this.eligibilityRuleList = eligibilityRuleList;
	}

	public List<FacilityReferenceDetail> getScoringGroupList() {
		return scoringGroupList;
	}
	public void setScoringGroupList(List<FacilityReferenceDetail> scoringGroup) {
		this.scoringGroupList = scoringGroup;
	}
	
	public List<FacilityReferenceDetail> getCorpScoringGroupList() {
    	return corpScoringGroupList;
    }
	public void setCorpScoringGroupList(List<FacilityReferenceDetail> corpScoringGroupList) {
    	this.corpScoringGroupList = corpScoringGroupList;
    }

	public void setAccountingList(List<FacilityReferenceDetail> accountingList) {
	    this.accountingList = accountingList;
    }
	public List<FacilityReferenceDetail> getAccountingList() {
	    return accountingList;
    }
	
	public void setMailTemplateList(List<FacilityReferenceDetail> mailTemplateList) {
	    this.mailTemplateList = mailTemplateList;
    }
	public List<FacilityReferenceDetail> getMailTemplateList() {
	    return mailTemplateList;
    }

	public String getWorkFlowType() {
		return workFlowType;
	}

	public void setWorkFlowType(String workFlowType) {
		this.workFlowType = workFlowType;
	}

	public String getLovDescWorkFlowTypeName() {
		return lovDescWorkFlowTypeName;
	}

	public void setLovDescWorkFlowTypeName(String lovDescWorkFlowTypeName) {
		this.lovDescWorkFlowTypeName = lovDescWorkFlowTypeName;
	}

	public String getLovDescWorkFlowRolesName() {
		return lovDescWorkFlowRolesName;
	}

	public void setLovDescWorkFlowRolesName(String lovDescWorkFlowRolesName) {
		this.lovDescWorkFlowRolesName = lovDescWorkFlowRolesName;
	}

	public FacilityReference getBefImage() {
		return befImage;
	}

	public void setBefImage(FacilityReference befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

}
