package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class FinContributorHeader extends AbstractWorkflowEntity {
	
    private static final long serialVersionUID = -3924845567358313810L;

	private String finReference;
	private int minContributors;
	private int maxContributors;
	private BigDecimal minContributionAmt;
	private BigDecimal maxContributionAmt;
	private int curContributors;
	private BigDecimal curContributionAmt;
	private BigDecimal curBankInvestment;
	private BigDecimal avgMudaribRate;
	private boolean alwContributorsToLeave;
	private boolean alwContributorsToJoin;
	
	private boolean newRecord=false;
	private String lovValue;
	private FinContributorHeader befImage;
	private LoggedInUser userDetails;

	private List<FinContributorDetail> contributorDetailList = new ArrayList<FinContributorDetail>();
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinContributorHeader() {
		super();
	}

	public FinContributorHeader(String id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("contributorDetailList");
		
		return excludeFields;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return finReference;
	}
	public void setId (String id) {
		this.finReference = id;
	}

	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }

	public int getMinContributors() {
    	return minContributors;
    }
	public void setMinContributors(int minContributors) {
    	this.minContributors = minContributors;
    }

	public int getMaxContributors() {
    	return maxContributors;
    }
	public void setMaxContributors(int maxContributors) {
    	this.maxContributors = maxContributors;
    }

	public BigDecimal getMinContributionAmt() {
    	return minContributionAmt;
    }
	public void setMinContributionAmt(BigDecimal minContributionAmt) {
    	this.minContributionAmt = minContributionAmt;
    }

	public BigDecimal getMaxContributionAmt() {
    	return maxContributionAmt;
    }
	public void setMaxContributionAmt(BigDecimal maxContributionAmt) {
    	this.maxContributionAmt = maxContributionAmt;
    }

	public int getCurContributors() {
    	return curContributors;
    }
	public void setCurContributors(int curContributors) {
    	this.curContributors = curContributors;
    }

	public BigDecimal getCurContributionAmt() {
    	return curContributionAmt;
    }
	public void setCurContributionAmt(BigDecimal curContributionAmt) {
    	this.curContributionAmt = curContributionAmt;
    }

	public BigDecimal getCurBankInvestment() {
    	return curBankInvestment;
    }
	public void setCurBankInvestment(BigDecimal curBankInvestment) {
    	this.curBankInvestment = curBankInvestment;
    }

	public BigDecimal getAvgMudaribRate() {
    	return avgMudaribRate;
    }
	public void setAvgMudaribRate(BigDecimal avgMudaribRate) {
    	this.avgMudaribRate = avgMudaribRate;
    }

	public boolean isAlwContributorsToLeave() {
    	return alwContributorsToLeave;
    }
	public void setAlwContributorsToLeave(boolean alwContributorsToLeave) {
    	this.alwContributorsToLeave = alwContributorsToLeave;
    }

	public boolean isAlwContributorsToJoin() {
    	return alwContributorsToJoin;
    }
	public void setAlwContributorsToJoin(boolean alwContributorsToJoin) {
    	this.alwContributorsToJoin = alwContributorsToJoin;
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

	public FinContributorHeader getBefImage() {
    	return befImage;
    }
	public void setBefImage(FinContributorHeader befImage) {
    	this.befImage = befImage;
    }

	public LoggedInUser getUserDetails() {
    	return userDetails;
    }
	public void setUserDetails(LoggedInUser userDetails) {
    	this.userDetails = userDetails;
    }
	
	public void setContributorDetailList(List<FinContributorDetail> contributorDetailList) {
	    this.contributorDetailList = contributorDetailList;
    }

	public List<FinContributorDetail> getContributorDetailList() {
	    return contributorDetailList;
    }

}
