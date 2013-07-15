package com.pennant.backend.model.lmtmasters;

import java.util.List;

import com.pennant.backend.model.LoginUserDetails;

public class FinanceReference {

	private String finType;
	private String lovDescFinTypeDescName;
	
	private List<FinanceReferenceDetail> checkList;
	private List<FinanceReferenceDetail> aggrementList;
	private List<FinanceReferenceDetail> eligibilityRuleList;
	private List<FinanceReferenceDetail> scoringGroupList;
	private List<FinanceReferenceDetail> corpScoringGroupList;
	private List<FinanceReferenceDetail> accountingList;
	private List<FinanceReferenceDetail> mailTemplateList;
	
	private String workFlowType;
	private String lovDescWorkFlowTypeName;
	private String lovDescWorkFlowRolesName;

	private FinanceReference befImage;
	private LoginUserDetails userDetails;

	public FinanceReference() {
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

	public List<FinanceReferenceDetail> getCheckList() {
		return checkList;
	}
	public void setCheckList(List<FinanceReferenceDetail> checkList) {
		this.checkList = checkList;
	}

	public List<FinanceReferenceDetail> getAggrementList() {
		return aggrementList;
	}
	public void setAggrementList(List<FinanceReferenceDetail> aggrementList) {
		this.aggrementList = aggrementList;
	}

	public List<FinanceReferenceDetail> getEligibilityRuleList() {
		return eligibilityRuleList;
	}
	public void setEligibilityRuleList(List<FinanceReferenceDetail> eligibilityRuleList) {
		this.eligibilityRuleList = eligibilityRuleList;
	}

	public List<FinanceReferenceDetail> getScoringGroupList() {
		return scoringGroupList;
	}
	public void setScoringGroupList(List<FinanceReferenceDetail> scoringGroup) {
		this.scoringGroupList = scoringGroup;
	}
	
	public List<FinanceReferenceDetail> getCorpScoringGroupList() {
    	return corpScoringGroupList;
    }
	public void setCorpScoringGroupList(List<FinanceReferenceDetail> corpScoringGroupList) {
    	this.corpScoringGroupList = corpScoringGroupList;
    }

	public void setAccountingList(List<FinanceReferenceDetail> accountingList) {
	    this.accountingList = accountingList;
    }
	public List<FinanceReferenceDetail> getAccountingList() {
	    return accountingList;
    }
	
	public void setMailTemplateList(List<FinanceReferenceDetail> mailTemplateList) {
	    this.mailTemplateList = mailTemplateList;
    }
	public List<FinanceReferenceDetail> getMailTemplateList() {
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

	public FinanceReference getBefImage() {
		return befImage;
	}

	public void setBefImage(FinanceReference befImage) {
		this.befImage = befImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

}
