package com.pennant.backend.model.lmtmasters;

import java.util.List;

import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinanceReference {

	private String finType;
	private String finEvent;
	private String lovDescFinTypeDescName;
	
	private List<FinanceReferenceDetail> checkList;
	private List<FinanceReferenceDetail> aggrementList;
	private List<FinanceReferenceDetail> eligibilityRuleList;
	private List<FinanceReferenceDetail> scoringGroupList;
	private List<FinanceReferenceDetail> corpScoringGroupList;
	private List<FinanceReferenceDetail> accountingList;
	private List<FinanceReferenceDetail> mailTemplateList;
	private List<FinanceReferenceDetail> financeDedupeList;
	private List<FinanceReferenceDetail> customerDedupeList;
	private List<FinanceReferenceDetail> blackListDedupeList;
	private List<FinanceReferenceDetail> policeDedupeList;
	private List<FinanceReferenceDetail> returnChequeList;
	private List<FinanceReferenceDetail> limitCodeDetailList;
	private List<FinanceReferenceDetail> tatNotificationList;

	private String workFlowType;
	private String lovDescWorkFlowTypeName;
	private String lovDescWorkFlowRolesName;

	private FinanceReference befImage;
	private LoggedInUser userDetails;

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
	
	public List<FinanceReferenceDetail> getFinanceDedupeList() {
	    return financeDedupeList;
    }
	public void setFinanceDedupeList(List<FinanceReferenceDetail> financeDedupeList) {
	    this.financeDedupeList = financeDedupeList;
    }
	
	public List<FinanceReferenceDetail> getCustomerDedupeList() {
		return customerDedupeList;
	}
	public void setCustomerDedupeList(List<FinanceReferenceDetail> customerDedupeList) {
		this.customerDedupeList = customerDedupeList;
	}

	public List<FinanceReferenceDetail> getBlackListDedupeList() {
	    return blackListDedupeList;
    }
	public void setBlackListDedupeList(List<FinanceReferenceDetail> blackListDedupeList) {
	    this.blackListDedupeList = blackListDedupeList;
    }

	public List<FinanceReferenceDetail> getPoliceDedupeList() {
	    return policeDedupeList;
    }

	public void setPoliceDedupeList(List<FinanceReferenceDetail> policeDedupeList) {
	    this.policeDedupeList = policeDedupeList;
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<FinanceReferenceDetail> getLimitCodeDetailList() {
		return limitCodeDetailList;
	}

	public void setLimitCodeDetailList(List<FinanceReferenceDetail> limitCodeDetailList) {
		this.limitCodeDetailList = limitCodeDetailList;
	}
	
	public List<FinanceReferenceDetail> getTatNotificationList() {
		return tatNotificationList;
	}

	public void setTatNotificationList(List<FinanceReferenceDetail> tatNotificationList) {
		this.tatNotificationList = tatNotificationList;
	}
	
	public String getFinEvent() {
	    return finEvent;
    }
	public void setFinEvent(String finEvent) {
	    this.finEvent = finEvent;
    }

	public List<FinanceReferenceDetail> getReturnChequeList() {
		return returnChequeList;
	}

	public void setReturnChequeList(List<FinanceReferenceDetail> returnChequeList) {
		this.returnChequeList = returnChequeList;
	}

}
