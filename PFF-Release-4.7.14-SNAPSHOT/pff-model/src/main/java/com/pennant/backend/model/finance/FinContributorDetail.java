package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinContributorDetail extends AbstractWorkflowEntity implements Entity {

    private static final long serialVersionUID = -7356577575758061061L;
    
	private String finReference;
	private long contributorBaseNo;
	private long custID;
	private String lovDescContributorCIF;
	private String contributorName;
	private BigDecimal contributorInvest;
	private String investAccount;
	private Date investDate;
	private Date recordDate;
	private BigDecimal totalInvestPerc;
	private BigDecimal mudaribPerc;
	private boolean newRecord=false;
	private String lovValue;
	private FinContributorDetail befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public FinContributorDetail() {
		super();
	}

	public FinContributorDetail(String id) {
		super();
		this.setFinReference(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getId() {
		return contributorBaseNo;
	}
	public void setId (long id) {
		this.contributorBaseNo = id;
	}

	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }

	public long getContributorBaseNo() {
    	return contributorBaseNo;
    }
	public void setContributorBaseNo(long contributorBaseNo) {
    	this.contributorBaseNo = contributorBaseNo;
    }

	public void setCustID(long custID) {
	    this.custID = custID;
    }

	public long getCustID() {
	    return custID;
    }

	public String getLovDescContributorCIF() {
    	return lovDescContributorCIF;
    }
	public void setLovDescContributorCIF(String lovDescContributorCIF) {
    	this.lovDescContributorCIF = lovDescContributorCIF;
    }

	public String getContributorName() {
    	return contributorName;
    }
	public void setContributorName(String contributorName) {
    	this.contributorName = contributorName;
    }

	public BigDecimal getContributorInvest() {
    	return contributorInvest;
    }
	public void setContributorInvest(BigDecimal contributorInvest) {
    	this.contributorInvest = contributorInvest;
    }

	public String getInvestAccount() {
    	return investAccount;
    }
	public void setInvestAccount(String investAccount) {
    	this.investAccount = investAccount;
    }

	public Date getInvestDate() {
    	return investDate;
    }
	public void setInvestDate(Date investDate) {
    	this.investDate = investDate;
    }

	public BigDecimal getTotalInvestPerc() {
    	return totalInvestPerc;
    }
	public void setTotalInvestPerc(BigDecimal totalInvestPerc) {
    	this.totalInvestPerc = totalInvestPerc;
    }

	public BigDecimal getMudaribPerc() {
    	return mudaribPerc;
    }
	public void setMudaribPerc(BigDecimal mudaribPerc) {
    	this.mudaribPerc = mudaribPerc;
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

	public FinContributorDetail getBefImage() {
    	return befImage;
    }
	public void setBefImage(FinContributorDetail befImage) {
    	this.befImage = befImage;
    }

	public LoggedInUser getUserDetails() {
    	return userDetails;
    }
	public void setUserDetails(LoggedInUser userDetails) {
    	this.userDetails = userDetails;
    }
	
	public void setRecordDate(Date recordDate) {
	    this.recordDate = recordDate;
    }

	public Date getRecordDate() {
	    return recordDate;
    }
}
