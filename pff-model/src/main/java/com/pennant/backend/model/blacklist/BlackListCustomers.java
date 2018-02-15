package com.pennant.backend.model.blacklist;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class BlackListCustomers extends AbstractWorkflowEntity {
    private static final long serialVersionUID = 4313500432713459335L;
    
	private String custCIF;
	private String custFName;
	private String custLName;
	private String custShrtName;
	private Date custDOB;
	private String custCRCPR;
	private String custPassportNo;
	private String mobileNumber;
	private String custNationality;
	private String employer;
	private String watchListRule;
	private boolean override;
	private String overrideUser;
	private boolean newRecord;
	private String lovValue;
	private BlackListCustomers befImage;
	private LoggedInUser userDetails;
	private String lovDescNationalityDesc;
	private String lovDescEmpName;
	private String likeCustFName;
	private String likeCustMName;
	private String likeCustLName;
	private boolean custIsActive;	

	// For Internal Use
	private String finReference;
	private String custCtgCode;
	private String queryField;
	private String overridenby;
	private boolean newRule;
	private boolean newBlacklistRecord = true;

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("custCtgCode");
		excludeFields.add("finReference");
		excludeFields.add("queryField");
		excludeFields.add("overridenby");
		excludeFields.add("newBlacklistRecord");
		excludeFields.add("watchListRule");
		excludeFields.add("override");
		excludeFields.add("overrideUser");
		excludeFields.add("custShrtName");
		excludeFields.add("lovDescNationalityDesc");
		excludeFields.add("newRule");
		excludeFields.add("likeCustFName");
		excludeFields.add("likeCustMName");
		excludeFields.add("likeCustLName");
		return excludeFields;
	}
	
	
	public boolean isNew() {
		return isNewRecord();
	}

	public BlackListCustomers() {
		super();
	}

	public BlackListCustomers(String id) {
		super();
		this.setId(id);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return custCIF;
	}

	public void setId(String id) {
		this.custCIF = id;
	}
	
	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustFName() {
		return custFName;
	}

	public void setCustFName(String custFName) {
		this.custFName = custFName;
	}

	public String getCustLName() {
		return custLName;
	}

	public void setCustLName(String custLName) {
		this.custLName = custLName;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public Date getCustDOB() {
		return custDOB;
	}

	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}

	public String getCustPassportNo() {
		return custPassportNo;
	}

	public void setCustPassportNo(String custPassportNo) {
		this.custPassportNo = custPassportNo;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getCustNationality() {
		return custNationality;
	}

	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getWatchListRule() {
		return watchListRule;
	}

	public void setWatchListRule(String watchListRule) {
		this.watchListRule = watchListRule;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}
	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public String getOverrideUser() {
		return overrideUser;
	}

	public void setOverrideUser(String overrideUser) {
		this.overrideUser = overrideUser;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getQueryField() {
		return queryField;
	}

	public void setQueryField(String queryField) {
		this.queryField = queryField;
	}

	public String getOverridenby() {
		return overridenby;
	}

	public void setOverridenby(String overridenby) {
		this.overridenby = overridenby;
	}

	public boolean isNewRule() {
	    return newRule;
    }

	public void setNewRule(boolean newRule) {
	    this.newRule = newRule;
    }


	public boolean isNewBlacklistRecord() {
		return newBlacklistRecord;
	}

	public void setNewBlacklistRecord(boolean newBlacklistRecord) {
		this.newBlacklistRecord = newBlacklistRecord;
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

	public BlackListCustomers getBefImage() {
		return befImage;
	}

	public void setBefImage(BlackListCustomers befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescNationalityDesc() {
	    return lovDescNationalityDesc;
    }

	public void setLovDescNationalityDesc(String lovDescNationalityDesc) {
	    this.lovDescNationalityDesc = lovDescNationalityDesc;
    }

	public String getLovDescEmpName() {
	    return lovDescEmpName;
    }

	public void setLovDescEmpName(String lovDescEmpName) {
	    this.lovDescEmpName = lovDescEmpName;
    }
	
	public String getLikeCustFName() {
		return likeCustFName;
	}

	public void setLikeCustFName(String likeCustFName) {
		this.likeCustFName = likeCustFName;
	}

	public String getLikeCustMName() {
		return likeCustMName;
	}

	public void setLikeCustMName(String likeCustMName) {
		this.likeCustMName = likeCustMName;
	}

	public String getLikeCustLName() {
		return likeCustLName;
	}

	public void setLikeCustLName(String likeCustLName) {
		this.likeCustLName = likeCustLName;
	}
	
	public boolean isCustIsActive() {
		return custIsActive;
	}

	public void setCustIsActive(boolean custIsActive) {
		this.custIsActive = custIsActive;
	}
	
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
