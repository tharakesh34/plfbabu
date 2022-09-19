package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

/**
 * Model class for the <b>Customer table</b>.<br>
 *
 */
@XmlType(propOrder = { "custCIF", "custCtgCode", "custDftBranch", "custFName", "custLName", "custShrtName", "custDOB",
		"custCRCPR", "custSector" })
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerDedup implements Serializable {
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "cif")
	private String custCIF;
	private long finID;
	private String finReference;
	@XmlElement(name = "lastName")
	private String custLName;
	@XmlElement(name = "firstName")
	private String custFName;
	@XmlElement(name = "shortName ")
	private String custShrtName;
	private String custMotherMaiden;
	@XmlElement(name = "dateofBirth")
	private Date custDOB;
	@XmlElement(name = "custPAN")
	private String custCRCPR;
	private String custPassportNo;
	private String mobileNumber;
	private String custNationality;
	private String stage;
	private String dedupRule;
	private boolean override;
	private String overrideUser;
	private String module;
	private String likeCustFName;
	private String likeCustMName;
	private String likeCustLName;

	// Audit Purpose Fields
	private long lastMntBy;
	private String roleCode;
	private String recordStatus;

	// For Internal use //Not in the table should be exculed for audit
	private long custId = Long.MIN_VALUE;
	private String custCoreBank;
	@XmlElement(name = "categoryCode")
	private String custCtgCode;
	@XmlElement(name = "defaultBranch")
	private String custDftBranch;
	@XmlElement(name = "sector ")
	private String custSector;
	private String custSubSector;
	private String custDocType;
	private String custDocTitle;
	private String custSalutationCode;
	private Date custPassportExpiry;
	private String custCOB;
	private String custTradeLicenceNum;
	private String custVisaNum;
	private String phoneNumber;
	private String custPOB;
	private String custResdCountry;
	private String custEMail;
	private String engineNumber;
	private String chassisNumber;
	private CustomerDedup befImage;
	private String queryField;
	private String overridenby;
	private boolean isNewRule;
	private boolean newCustDedupRecord = true;
	private String tradeLicenceNo;
	private String registrationNo;
	private String titleDeedNo;
	private int appScore;
	private String sourceSystem;
	private String address;
	private String address1;
	private String address2;
	private String address3;
	private String address4;
	private String panNumber;
	private String aadharNumber;
	private String fatherName;
	private String motherName;
	@XmlElement(name = "voterId")
	private String voterID;
	private String rationCard;
	private String lpgNumber;
	@XmlElement(name = "drivingLicense")
	private String drivingLicenceNo;
	private String finType;
	@XmlElement(name = "UCIC")
	private String ucic;
	private String custCompName;
	// Added for to show the values for External customer dedup.
	private String gender;
	private String city;
	private String state;
	private String country;
	private String pincode;
	private String phoneType;
	private String emailType;
	private String addressType;
	private String score;
	private String rank;
	private String gstin;
	private String regNo;
	private String tanNo;
	private String nrgeaCard;
	private String bankAccountNo;
	private String ifscCode;
	private String corporateLicence;
	private String spouseName;
	private String employerName;
	private String ucicType;
	private String requestId;
	private String status;
	private String statusMessage;

	public CustomerDedup() {
		super();
	}

	// Getter and Setter methods
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custId");
		excludeFields.add("custCoreBank");
		excludeFields.add("custCtgCode");
		excludeFields.add("custDftBranch");
		excludeFields.add("custSector");
		excludeFields.add("custSubSector");
		excludeFields.add("custDocType");
		excludeFields.add("custDocTitle");
		excludeFields.add("custSalutationCode");
		excludeFields.add("custPassportExpiry");
		excludeFields.add("custCOB");
		excludeFields.add("custTradeLicenceNum");
		excludeFields.add("custVisaNum");
		excludeFields.add("phoneNumber");
		excludeFields.add("custPOB");
		excludeFields.add("custResdCountry");
		excludeFields.add("custEMail");
		excludeFields.add("engineNumber");
		excludeFields.add("chassisNumber");
		excludeFields.add("queryField");
		excludeFields.add("overridenby");
		excludeFields.add("isNewRule");
		excludeFields.add("newCustDedupRecord");
		excludeFields.add("likeCustFName");
		excludeFields.add("likeCustMName");
		excludeFields.add("likeCustLName");
		excludeFields.add("custMotherMaiden");
		excludeFields.add("tradeLicenceNo");
		excludeFields.add("registrationNo");
		excludeFields.add("titleDeedNo");
		excludeFields.add("appScore");
		excludeFields.add("sourceSystem");
		excludeFields.add("panNumber");
		excludeFields.add("aadharNumber");
		excludeFields.add("fatherName");
		excludeFields.add("motherName");
		excludeFields.add("voterID");
		excludeFields.add("rationCard");
		excludeFields.add("lpgNumber");
		excludeFields.add("custPassportNo");
		excludeFields.add("drivingLicenceNo");
		excludeFields.add("address");
		excludeFields.add("finType");
		excludeFields.add("ucic");
		excludeFields.add("custCompName");
		excludeFields.add("gender");
		excludeFields.add("city");
		excludeFields.add("state");
		excludeFields.add("country");
		excludeFields.add("pincode");
		excludeFields.add("phoneType");
		excludeFields.add("emailType");
		excludeFields.add("addressType");
		excludeFields.add("score");
		excludeFields.add("validatePANinPLF");
		excludeFields.add("address1");
		excludeFields.add("address2");
		excludeFields.add("address3");
		excludeFields.add("address4");
		excludeFields.add("rank");
		excludeFields.add("gstin");
		excludeFields.add("regNo");
		excludeFields.add("tanNo");
		excludeFields.add("nrgeaCard");
		excludeFields.add("bankAccountNo");
		excludeFields.add("ifscCode");
		excludeFields.add("corporateLicence");
		excludeFields.add("spouseName");
		excludeFields.add("employerName");
		excludeFields.add("ucicType");
		excludeFields.add("requestId");
		excludeFields.add("status");
		excludeFields.add("statusMessage");

		return excludeFields;
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

	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
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

	public String getCustPassportNo() {
		return custPassportNo;
	}

	public CustomerDedup getBefImage() {
		return befImage;
	}

	public void setBefImage(CustomerDedup befImage) {
		this.befImage = befImage;
	}

	public boolean isChanged() {
		boolean changed = false;

		if (befImage == null) {
			changed = true;
		} else {
			if (!StringUtils.trimToEmpty(befImage.getCustCIF()).equals(StringUtils.trim(getCustCIF()))) {
				changed = true;
			} else if (!StringUtils.trimToEmpty(befImage.getCustCoreBank())
					.equals(StringUtils.trim(getCustCoreBank()))) {
				changed = true;
			} else if (!StringUtils.trimToEmpty(befImage.getCustShrtName())
					.equals(StringUtils.trim(getCustShrtName()))) {
				changed = true;
			} else if (befImage.getCustDOB().equals(getCustDOB())) {
				changed = true;
			} else if (!StringUtils.trimToEmpty(befImage.getCustPassportNo())
					.equals(StringUtils.trim(getCustPassportNo()))) {
				changed = true;
			} else if (!StringUtils.trimToEmpty(befImage.getCustTradeLicenceNum())
					.equals(StringUtils.trim(getCustTradeLicenceNum()))) {
				changed = true;
			} else if (!StringUtils.trimToEmpty(befImage.getCustVisaNum()).equals(StringUtils.trim(getCustVisaNum()))) {
				changed = true;
			}
		}

		return changed;
	}

	public String getCustNationality() {
		return custNationality;
	}

	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public String getOverrideUser() {
		return overrideUser;
	}

	public void setOverrideUser(String overrideUser) {
		this.overrideUser = overrideUser;
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
		return isNewRule;
	}

	public void setNewRule(boolean isNewRule) {
		this.isNewRule = isNewRule;
	}

	public boolean isNewCustDedupRecord() {
		return newCustDedupRecord;
	}

	public void setNewCustDedupRecord(boolean newCustDedupRecord) {
		this.newCustDedupRecord = newCustDedupRecord;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getDedupRule() {
		return dedupRule;
	}

	public void setDedupRule(String dedupRule) {
		this.dedupRule = dedupRule;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
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

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public String getCustDftBranch() {
		return custDftBranch;
	}

	public void setCustDftBranch(String custDftBranch) {
		this.custDftBranch = custDftBranch;
	}

	public String getCustSector() {
		return custSector;
	}

	public void setCustSector(String custSector) {
		this.custSector = custSector;
	}

	public String getCustSubSector() {
		return custSubSector;
	}

	public void setCustSubSector(String custSubSector) {
		this.custSubSector = custSubSector;
	}

	public String getCustDocType() {
		return custDocType;
	}

	public void setCustDocType(String custDocType) {
		this.custDocType = custDocType;
	}

	public String getCustDocTitle() {
		return custDocTitle;
	}

	public void setCustDocTitle(String custDocTitle) {
		this.custDocTitle = custDocTitle;
	}

	public String getCustSalutationCode() {
		return custSalutationCode;
	}

	public void setCustSalutationCode(String custSalutationCode) {
		this.custSalutationCode = custSalutationCode;
	}

	public Date getCustPassportExpiry() {
		return custPassportExpiry;
	}

	public void setCustPassportExpiry(Date custPassportExpiry) {
		this.custPassportExpiry = custPassportExpiry;
	}

	public String getCustCOB() {
		return custCOB;
	}

	public void setCustCOB(String custCOB) {
		this.custCOB = custCOB;
	}

	public String getCustTradeLicenceNum() {
		return custTradeLicenceNum;
	}

	public void setCustTradeLicenceNum(String custTradeLicenceNum) {
		this.custTradeLicenceNum = custTradeLicenceNum;
	}

	public String getCustVisaNum() {
		return custVisaNum;
	}

	public void setCustVisaNum(String custVisaNum) {
		this.custVisaNum = custVisaNum;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCustPOB() {
		return custPOB;
	}

	public void setCustPOB(String custPOB) {
		this.custPOB = custPOB;
	}

	public String getCustResdCountry() {
		return custResdCountry;
	}

	public void setCustResdCountry(String custResdCountry) {
		this.custResdCountry = custResdCountry;
	}

	public String getCustEMail() {
		return custEMail;
	}

	public void setCustEMail(String custEMail) {
		this.custEMail = custEMail;
	}

	public String getEngineNumber() {
		return engineNumber;
	}

	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}

	public String getChassisNumber() {
		return chassisNumber;
	}

	public void setChassisNumber(String chassisNumber) {
		this.chassisNumber = chassisNumber;
	}

	public void setCustPassportNo(String custPassportNo) {
		this.custPassportNo = custPassportNo;
	}

	public String getCustMotherMaiden() {
		return custMotherMaiden;
	}

	public void setCustMotherMaiden(String custMotherMaiden) {
		this.custMotherMaiden = custMotherMaiden;
	}

	public String getTradeLicenceNo() {
		return tradeLicenceNo;
	}

	public void setTradeLicenceNo(String tradeLicenceNo) {
		this.tradeLicenceNo = tradeLicenceNo;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public String getTitleDeedNo() {
		return titleDeedNo;
	}

	public void setTitleDeedNo(String titleDeedNo) {
		this.titleDeedNo = titleDeedNo;
	}

	public int getAppScore() {
		return appScore;
	}

	public void setAppScore(int appScore) {
		this.appScore = appScore;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getVoterID() {
		return voterID;
	}

	public void setVoterID(String voterID) {
		this.voterID = voterID;
	}

	public String getDrivingLicenceNo() {
		return drivingLicenceNo;
	}

	public void setDrivingLicenceNo(String drivingLicenceNo) {
		this.drivingLicenceNo = drivingLicenceNo;
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

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getUcic() {
		return ucic;
	}

	public void setUcic(String uCIC) {
		this.ucic = uCIC;
	}

	public String getCustCompName() {
		return custCompName;
	}

	public void setCustCompName(String custCompName) {
		this.custCompName = custCompName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getEmailType() {
		return emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getTanNo() {
		return tanNo;
	}

	public void setTanNo(String tanNo) {
		this.tanNo = tanNo;
	}

	public String getNrgeaCard() {
		return nrgeaCard;
	}

	public void setNrgeaCard(String nrgeaCard) {
		this.nrgeaCard = nrgeaCard;
	}

	public String getBankAccountNo() {
		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getCorporateLicence() {
		return corporateLicence;
	}

	public void setCorporateLicence(String corporateLicence) {
		this.corporateLicence = corporateLicence;
	}

	public String getSpouseName() {
		return spouseName;
	}

	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}

	public String getEmployerName() {
		return employerName;
	}

	public void setEmployerName(String employerName) {
		this.employerName = employerName;
	}

	public String getUcicType() {
		return ucicType;
	}

	public void setUcicType(String ucicType) {
		this.ucicType = ucicType;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
}