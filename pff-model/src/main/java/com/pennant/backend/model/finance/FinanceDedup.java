package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Model class for the <b>Customer table</b>.<br>
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceDedup implements Serializable {
	private static final long serialVersionUID = 1L;
	private long custId = Long.MIN_VALUE;
	private long finID;
	@XmlElement
	private String finReference;
	private String dupReference;
	@XmlElement
	private String custCIF;
	private String custFName;
	private String custMName;
	private String custLName;
	private String custShrtName;
	private String custMotherMaiden;
	private String likeCustFName;
	private String likeCustMName;
	private String likeCustLName;
	private String custNationality;
	private String custParentCountry;
	private Date custDOB;
	private boolean newRecord = false;

	// new fields
	private String mobileNumber;
	private String chassisNumber;
	private String engineNumber;
	private BigDecimal financeAmount;
	private BigDecimal profitAmount;
	@XmlElement
	private String financeType;
	private Date startDate;
	private String stage = "";
	private String stageDesc = "";
	private String dedupeRule;
	private String custCRCPR;
	private String overrideUser;
	private String rules;
	private String fatherName;
	private String motherName;
	private String aadharNumber;
	private String panNumber;
	private String voterID;
	private String rationCard;
	private String lpgNumber;
	private String drivingLicenceNo;

	// Audit Purpose Fields
	private long lastMntBy;
	private String roleCode;
	private String recordStatus;

	private String custPassportNo;
	private String custCPRNo;
	private String custCRNo;

	private String titleDeedNo;
	private String registrationNo;
	private String tradeLicenceNo;
	private String finLimitRef;

	private FinanceDedup befImage;
	private Map<String, String> overridenMap;
	private String dedupFields;
	private boolean override;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custId");
		excludeFields.add("custFName");
		excludeFields.add("custMName");
		excludeFields.add("custLName");
		excludeFields.add("custMotherMaiden");
		excludeFields.add("likeCustFName");
		excludeFields.add("likeCustMName");
		excludeFields.add("likeCustLName");
		excludeFields.add("custNationality");
		excludeFields.add("custParentCountry");
		excludeFields.add("custDOB");
		excludeFields.add("stageDesc");
		excludeFields.add("custPassportNo");
		excludeFields.add("custCPRNo");
		excludeFields.add("custCRNo");
		excludeFields.add("befImage");
		excludeFields.add("dedupList");
		excludeFields.add("dedupFields");
		excludeFields.add("rules");
		excludeFields.add("newRecord");
		excludeFields.add("override");
		excludeFields.add("overridenMap");
		excludeFields.add("fatherName");
		excludeFields.add("motherName");
		excludeFields.add("aadharNumber");
		excludeFields.add("panNumber");
		excludeFields.add("voterID");
		excludeFields.add("rationCard");
		excludeFields.add("lpgNumber");
		excludeFields.add("drivingLicenceNo");
		return excludeFields;
	}

	public FinanceDedup() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public long getCustId() {
		return custId;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	} 
	
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinReference() {
		return finReference;
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

	public String getCustMName() {
		return custMName;
	}

	public void setCustMName(String custMName) {
		this.custMName = custMName;
	}

	public String getCustLName() {
		return custLName;
	}

	public void setCustLName(String custLName) {
		this.custLName = custLName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public String getCustMotherMaiden() {
		return custMotherMaiden;
	}

	public void setCustMotherMaiden(String custMotherMaiden) {
		this.custMotherMaiden = custMotherMaiden;
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

	public String getCustPassportNo() {
		return custPassportNo;
	}

	public void setCustPassportNo(String custPassportNo) {
		this.custPassportNo = custPassportNo;
	}

	public String getCustCPRNo() {
		return custCPRNo;
	}

	public void setCustCPRNo(String custCPRNo) {
		this.custCPRNo = custCPRNo;
	}

	public String getCustCRNo() {
		return custCRNo;
	}

	public void setCustCRNo(String custCRNo) {
		this.custCRNo = custCRNo;
	}

	public String getCustNationality() {
		return custNationality;
	}

	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public void setCustParentCountry(String custParentCountry) {
		this.custParentCountry = custParentCountry;
	}

	public String getCustParentCountry() {
		return custParentCountry;
	}

	public Date getCustDOB() {
		return custDOB;
	}

	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public String getTitleDeedNo() {
		return titleDeedNo;
	}

	public void setTitleDeedNo(String titleDeedNo) {
		this.titleDeedNo = titleDeedNo;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public String getTradeLicenceNo() {
		return tradeLicenceNo;
	}

	public void setTradeLicenceNo(String tradeLicenceNo) {
		this.tradeLicenceNo = tradeLicenceNo;
	}

	public FinanceDedup getBefImage() {
		return befImage;
	}

	public void setBefImage(FinanceDedup befImage) {
		this.befImage = befImage;
	}

	public Map<String, String> getOverridenMap() {
		return overridenMap;
	}

	public void setOverridenMap(Map<String, String> overridenMap) {
		this.overridenMap = overridenMap;
	}

	public String getDedupFields() {
		return dedupFields;
	}

	public void setDedupFields(String dedupFields) {
		this.dedupFields = dedupFields;
	}

	public boolean isChanged() {
		boolean changed = false;

		if (befImage == null) {
			changed = true;
		} else {

		}
		return changed;
	}

	public BigDecimal getFinanceAmount() {
		return financeAmount;
	}

	public void setFinanceAmount(BigDecimal financeAmount) {
		this.financeAmount = financeAmount;
	}

	public BigDecimal getProfitAmount() {
		return profitAmount;
	}

	public void setProfitAmount(BigDecimal profitAmount) {
		this.profitAmount = profitAmount;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getDedupeRule() {
		return dedupeRule;
	}

	public void setDedupeRule(String dedupeRule) {
		this.dedupeRule = dedupeRule;
	}

	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}

	public String getStageDesc() {
		return stageDesc;
	}

	public void setStageDesc(String stageDesc) {
		this.stageDesc = stageDesc;
	}

	public String getOverrideUser() {
		return overrideUser;
	}

	public void setOverrideUser(String overrideUser) {
		this.overrideUser = overrideUser;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getDupReference() {
		return dupReference;
	}

	public void setDupReference(String dupReference) {
		this.dupReference = dupReference;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getChassisNumber() {
		return chassisNumber;
	}

	public void setChassisNumber(String chassisNumber) {
		this.chassisNumber = chassisNumber;
	}

	public String getEngineNumber() {
		return engineNumber;
	}

	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}

	public String getFinanceType() {
		return financeType;
	}

	public void setFinanceType(String financeType) {
		this.financeType = financeType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public String getFinLimitRef() {
		return finLimitRef;
	}

	public void setFinLimitRef(String finLimitRef) {
		this.finLimitRef = finLimitRef;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getVoterID() {
		return voterID;
	}

	public void setVoterID(String voterID) {
		this.voterID = voterID;
	}

	public String getRationCard() {
		return rationCard;
	}

	public void setRationCard(String rationCard) {
		this.rationCard = rationCard;
	}

	public String getLpgNumber() {
		return lpgNumber;
	}

	public void setLpgNumber(String lpgNumber) {
		this.lpgNumber = lpgNumber;
	}

	public String getDrivingLicenceNo() {
		return drivingLicenceNo;
	}

	public void setDrivingLicenceNo(String drivingLicenceNo) {
		this.drivingLicenceNo = drivingLicenceNo;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

}
