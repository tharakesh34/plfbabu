package com.pennant.backend.model.blacklist;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "custCIF", "custCtgCode", "custFName", "custLName", "custShrtName", "custDOB", "custCRCPR",
		"mobileNumber" })
@XmlAccessorType(XmlAccessType.NONE)
public class BlackListCustomers extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 4313500432713459335L;
	@XmlElement(name = "cif")
	private String custCIF;
	@XmlElement(name = "firstName")
	private String custFName;
	@XmlElement(name = "lastName")
	private String custLName;
	@XmlElement(name = "shrttName")
	private String custShrtName;
	private String custCompName;
	@XmlElement
	private Date custDOB;
	private String custCRCPR;
	private String custPassportNo;
	@XmlElement
	private String mobileNumber;
	private String custNationality;
	private long employer;
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
	private String custCin;
	private String custAadhaar;

	// For Internal Use
	private String finReference;
	private String custCtgCode;
	private String queryField;
	private String overridenby;
	private boolean newRule;
	private boolean newBlacklistRecord = true;
	@XmlElement
	private String ruleCode;
	@XmlElement
	private String result;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
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
		excludeFields.add("ruleCode");
		excludeFields.add("result");
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

	public long getEmployer() {
		return employer;
	}

	public void setEmployer(long employer) {
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

	public String getCustCin() {
		return custCin;
	}

	public void setCustCin(String custCin) {
		this.custCin = custCin;
	}

	public String getCustAadhaar() {
		return custAadhaar;
	}

	public void setCustAadhaar(String custAadhaar) {
		this.custAadhaar = custAadhaar;
	}

	public String getCustCompName() {
		return custCompName;
	}

	public void setCustCompName(String custCompName) {
		this.custCompName = custCompName;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
