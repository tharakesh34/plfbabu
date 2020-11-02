package com.pennant.backend.model.blacklist;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.audit.AuditDetail;
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
	@XmlElement
	private String custCompName;
	@XmlElement
	private Date custDOB;
	private String custCRCPR;
	private String custPassportNo;
	@XmlElement
	private String mobileNumber;
	private String custNationality;
	private Long employer;
	private String empIndustry;
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
	private String reasonCode;

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

	private String gender;
	private String lovDescCustGenderCodeName;
	private String vid;
	private String dl;
	private String addressType;
	private String houseNumber;
	private String street;
	private String city;
	private String country;
	private String state;
	private String pincode;
	private String product_Applied_In_Other_FI;
	private String forged_Document_Type;
	private String remarks;
	private String lovDescCustAddrTypeName;
	private String lovDescCustAddrCountryName;
	private String lovDescCustAddrProvinceName;
	private String lovDescCustAddrCityName;
	private String lovDescCustAddrZip;
	@XmlElement
	private String source;
	private String branch;
	private String lovDescCustBranch;
	private Date additionalField0;
	private String additionalField1;
	private String additionalField2;
	private String additionalField3;
	private String additionalField4;
	private String additionalField5;
	private String additionalField6;
	private String additionalField7;
	private String additionalField8;
	private String additionalField9;
	private String additionalField10;
	private String additionalField11;
	private String additionalField12;
	private String additionalField13;
	private String additionalField14;
	private String address;
	private String director1FirstName;
	private String director1LastName;
	private String director2FirstName;
	private String director2LastName;
	private String director3FirstName;
	private String director3LastName;
	private String director4FirstName;
	private String director4LastName;
	private String director5FirstName;
	private String director5LastName;
	private String director6FirstName;
	private String director6LastName;
	private String director7FirstName;
	private String director7LastName;
	private String director8FirstName;
	private String director8LastName;
	private String director9FirstName;
	private String director9LastName;
	private String director10FirstName;
	private String director10LastName;
	private String assOrRelConcernFName;
	private String assOrRelConcernLName;
	private String otherSourceFirstName;
	private String otherSourceLastName;
	//for Corporate
	private String director1Name;
	private String director2Name;
	private String director3Name;
	private String director4Name;
	private String director5Name;
	private String director6Name;
	private String director7Name;
	private String director8Name;
	private String director9Name;
	private String director10Name;
	private String assOrRelConcern;
	private String otherSource;
	//Like Operators
	private String likeDirector1FirstName;
	private String likeDirector1LastName;
	private String likeDirector2FirstName;
	private String likeDirector2LastName;
	private String likeDirector3FirstName;
	private String likeDirector3LastName;
	private String likeDirector4FirstName;
	private String likeDirector4LastName;
	private String likeDirector5FirstName;
	private String likeDirector5LastName;
	private String likeDirector6FirstName;
	private String likeDirector6LastName;
	private String likeDirector7FirstName;
	private String likeDirector7LastName;
	private String likeDirector8FirstName;
	private String likeDirector8LastName;
	private String likeDirector9FirstName;
	private String likeDirector9LastName;
	private String likeDirector10FirstName;
	private String likeDirector10LastName;
	private String likeAssOrRelConcernFName;
	private String likeAssOrRelConcernLName;
	private String likeOtherSourceFirstName;
	private String likeOtherSourceLastName;
	private String likeDirector1Name;
	private String likeDirector2Name;
	private String likeDirector3Name;
	private String likeDirector4Name;
	private String likeDirector5Name;
	private String likeDirector6Name;
	private String likeDirector7Name;
	private String likeDirector8Name;
	private String likeDirector9Name;
	private String likeDirector10Name;
	private String likeAssOrRelConcern;
	private String likeOtherSource;
	private String likeCustCompName;
	private String likeCustCompName2;
	private String custCompName2;

	private List<NegativeReasoncodes> negativeReasoncodeList = new ArrayList<>();
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

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
		excludeFields.add("empIndustry");
		excludeFields.add("director1FirstName");
		excludeFields.add("director1LastName");
		excludeFields.add("director2FirstName");
		excludeFields.add("director2LastName");
		excludeFields.add("director3FirstName");
		excludeFields.add("director3LastName");
		excludeFields.add("director4FirstName");
		excludeFields.add("director4LastName");
		excludeFields.add("director5FirstName");
		excludeFields.add("director5LastName");
		excludeFields.add("director6FirstName");
		excludeFields.add("director6LastName");
		excludeFields.add("director7FirstName");
		excludeFields.add("director7LastName");
		excludeFields.add("director8FirstName");
		excludeFields.add("director8LastName");
		excludeFields.add("director9FirstName");
		excludeFields.add("director9LastName");
		excludeFields.add("director10FirstName");
		excludeFields.add("director10LastName");
		excludeFields.add("assOrRelConcernFName");
		excludeFields.add("assOrRelConcernLName");
		excludeFields.add("otherSourceFirstName");
		excludeFields.add("otherSourceLastName");
		excludeFields.add("director1Name");
		excludeFields.add("director2Name");
		excludeFields.add("director3Name");
		excludeFields.add("director4Name");
		excludeFields.add("director5Name");
		excludeFields.add("director6Name");
		excludeFields.add("director7Name");
		excludeFields.add("director8Name");
		excludeFields.add("director9Name");
		excludeFields.add("director10Name");
		excludeFields.add("assOrRelConcern");
		excludeFields.add("otherSource");
		excludeFields.add("likeDirector1FirstName");
		excludeFields.add("likeDirector1LastName");
		excludeFields.add("likeDirector2FirstName");
		excludeFields.add("likeDirector2LastName");
		excludeFields.add("likeDirector3FirstName");
		excludeFields.add("likeDirector3LastName");
		excludeFields.add("likeDirector4FirstName");
		excludeFields.add("likeDirector4LastName");
		excludeFields.add("likeDirector5FirstName");
		excludeFields.add("likeDirector5LastName");
		excludeFields.add("likeDirector6FirstName");
		excludeFields.add("likeDirector6LastName");
		excludeFields.add("likeDirector7FirstName");
		excludeFields.add("likeDirector7LastName");
		excludeFields.add("likeDirector8FirstName");
		excludeFields.add("likeDirector8LastName");
		excludeFields.add("likeDirector9FirstName");
		excludeFields.add("likeDirector9LastName");
		excludeFields.add("likeDirector10FirstName");
		excludeFields.add("likeDirector10LastName");
		excludeFields.add("likeAssOrRelConcernFName");
		excludeFields.add("likeAssOrRelConcernLName");
		excludeFields.add("likeOtherSourceFirstName");
		excludeFields.add("likeOtherSourceLastName");
		excludeFields.add("likeDirector1Name");
		excludeFields.add("likeDirector2Name");
		excludeFields.add("likeDirector3Name");
		excludeFields.add("likeDirector4Name");
		excludeFields.add("likeDirector5Name");
		excludeFields.add("likeDirector6Name");
		excludeFields.add("likeDirector7Name");
		excludeFields.add("likeDirector8Name");
		excludeFields.add("likeDirector9Name");
		excludeFields.add("likeDirector10Name");
		excludeFields.add("likeAssOrRelConcern");
		excludeFields.add("likeOtherSource");
		excludeFields.add("likeCustCompName");
		excludeFields.add("likeCustCompName2");
		excludeFields.add("custCompName2");

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

	public Long getEmployer() {
		return employer;
	}

	public void setEmployer(Long employer) {
		this.employer = employer;
	}

	public String getEmpIndustry() {
		return empIndustry;
	}

	public void setEmpIndustry(String empIndustry) {
		this.empIndustry = empIndustry;
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

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public List<NegativeReasoncodes> getNegativeReasoncodeList() {
		return negativeReasoncodeList;
	}

	public void setNegativeReasoncodeList(List<NegativeReasoncodes> negativeReasoncodeList) {
		this.negativeReasoncodeList = negativeReasoncodeList;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLovDescCustGenderCodeName() {
		return lovDescCustGenderCodeName;
	}

	public void setLovDescCustGenderCodeName(String lovDescCustGenderCodeName) {
		this.lovDescCustGenderCodeName = lovDescCustGenderCodeName;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getDl() {
		return dl;
	}

	public void setDl(String dl) {
		this.dl = dl;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getProduct_Applied_In_Other_FI() {
		return product_Applied_In_Other_FI;
	}

	public void setProduct_Applied_In_Other_FI(String product_Applied_In_Other_FI) {
		this.product_Applied_In_Other_FI = product_Applied_In_Other_FI;
	}

	public String getForged_Document_Type() {
		return forged_Document_Type;
	}

	public void setForged_Document_Type(String forged_Document_Type) {
		this.forged_Document_Type = forged_Document_Type;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLovDescCustAddrTypeName() {
		return lovDescCustAddrTypeName;
	}

	public void setLovDescCustAddrTypeName(String lovDescCustAddrTypeName) {
		this.lovDescCustAddrTypeName = lovDescCustAddrTypeName;
	}

	public String getLovDescCustAddrCountryName() {
		return lovDescCustAddrCountryName;
	}

	public void setLovDescCustAddrCountryName(String lovDescCustAddrCountryName) {
		this.lovDescCustAddrCountryName = lovDescCustAddrCountryName;
	}

	public String getLovDescCustAddrProvinceName() {
		return lovDescCustAddrProvinceName;
	}

	public void setLovDescCustAddrProvinceName(String lovDescCustAddrProvinceName) {
		this.lovDescCustAddrProvinceName = lovDescCustAddrProvinceName;
	}

	public String getLovDescCustAddrCityName() {
		return lovDescCustAddrCityName;
	}

	public void setLovDescCustAddrCityName(String lovDescCustAddrCityName) {
		this.lovDescCustAddrCityName = lovDescCustAddrCityName;
	}

	public String getLovDescCustAddrZip() {
		return lovDescCustAddrZip;
	}

	public void setLovDescCustAddrZip(String lovDescCustAddrZip) {
		this.lovDescCustAddrZip = lovDescCustAddrZip;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getLovDescCustBranch() {
		return lovDescCustBranch;
	}

	public void setLovDescCustBranch(String lovDescCustBranch) {
		this.lovDescCustBranch = lovDescCustBranch;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getAdditionalField3() {
		return additionalField3;
	}

	public void setAdditionalField3(String additionalField3) {
		this.additionalField3 = additionalField3;
	}

	public String getAdditionalField1() {
		return additionalField1;
	}

	public void setAdditionalField1(String additionalField1) {
		this.additionalField1 = additionalField1;
	}

	public String getAdditionalField2() {
		return additionalField2;
	}

	public void setAdditionalField2(String additionalField2) {
		this.additionalField2 = additionalField2;
	}

	public String getAdditionalField4() {
		return additionalField4;
	}

	public void setAdditionalField4(String additionalField4) {
		this.additionalField4 = additionalField4;
	}

	public String getAdditionalField5() {
		return additionalField5;
	}

	public void setAdditionalField5(String additionalField5) {
		this.additionalField5 = additionalField5;
	}

	public String getAdditionalField6() {
		return additionalField6;
	}

	public void setAdditionalField6(String additionalField6) {
		this.additionalField6 = additionalField6;
	}

	public String getAdditionalField7() {
		return additionalField7;
	}

	public void setAdditionalField7(String additionalField7) {
		this.additionalField7 = additionalField7;
	}

	public String getAdditionalField8() {
		return additionalField8;
	}

	public void setAdditionalField8(String additionalField8) {
		this.additionalField8 = additionalField8;
	}

	public String getAdditionalField9() {
		return additionalField9;
	}

	public void setAdditionalField9(String additionalField9) {
		this.additionalField9 = additionalField9;
	}

	public String getAdditionalField10() {
		return additionalField10;
	}

	public void setAdditionalField10(String additionalField10) {
		this.additionalField10 = additionalField10;
	}

	public String getAdditionalField11() {
		return additionalField11;
	}

	public void setAdditionalField11(String additionalField11) {
		this.additionalField11 = additionalField11;
	}

	public String getAdditionalField12() {
		return additionalField12;
	}

	public void setAdditionalField12(String additionalField12) {
		this.additionalField12 = additionalField12;
	}

	public String getAdditionalField13() {
		return additionalField13;
	}

	public void setAdditionalField13(String additionalField13) {
		this.additionalField13 = additionalField13;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getAdditionalField0() {
		return additionalField0;
	}

	public void setAdditionalField0(Date additionalField0) {
		this.additionalField0 = additionalField0;
	}

	public String getDirector1FirstName() {
		return director1FirstName;
	}

	public void setDirector1FirstName(String director1FirstName) {
		this.director1FirstName = director1FirstName;
	}

	public String getDirector1LastName() {
		return director1LastName;
	}

	public void setDirector1LastName(String director1LastName) {
		this.director1LastName = director1LastName;
	}

	public String getDirector2FirstName() {
		return director2FirstName;
	}

	public void setDirector2FirstName(String director2FirstName) {
		this.director2FirstName = director2FirstName;
	}

	public String getDirector2LastName() {
		return director2LastName;
	}

	public void setDirector2LastName(String director2LastName) {
		this.director2LastName = director2LastName;
	}

	public String getDirector3FirstName() {
		return director3FirstName;
	}

	public void setDirector3FirstName(String director3FirstName) {
		this.director3FirstName = director3FirstName;
	}

	public String getDirector3LastName() {
		return director3LastName;
	}

	public void setDirector3LastName(String director3LastName) {
		this.director3LastName = director3LastName;
	}

	public String getDirector4FirstName() {
		return director4FirstName;
	}

	public void setDirector4FirstName(String director4FirstName) {
		this.director4FirstName = director4FirstName;
	}

	public String getDirector4LastName() {
		return director4LastName;
	}

	public void setDirector4LastName(String director4LastName) {
		this.director4LastName = director4LastName;
	}

	public String getDirector5FirstName() {
		return director5FirstName;
	}

	public void setDirector5FirstName(String director5FirstName) {
		this.director5FirstName = director5FirstName;
	}

	public String getDirector5LastName() {
		return director5LastName;
	}

	public void setDirector5LastName(String director5LastName) {
		this.director5LastName = director5LastName;
	}

	public String getDirector6FirstName() {
		return director6FirstName;
	}

	public void setDirector6FirstName(String director6FirstName) {
		this.director6FirstName = director6FirstName;
	}

	public String getDirector6LastName() {
		return director6LastName;
	}

	public void setDirector6LastName(String director6LastName) {
		this.director6LastName = director6LastName;
	}

	public String getDirector7FirstName() {
		return director7FirstName;
	}

	public void setDirector7FirstName(String director7FirstName) {
		this.director7FirstName = director7FirstName;
	}

	public String getDirector7LastName() {
		return director7LastName;
	}

	public void setDirector7LastName(String director7LastName) {
		this.director7LastName = director7LastName;
	}

	public String getDirector8FirstName() {
		return director8FirstName;
	}

	public void setDirector8FirstName(String director8FirstName) {
		this.director8FirstName = director8FirstName;
	}

	public String getDirector8LastName() {
		return director8LastName;
	}

	public void setDirector8LastName(String director8LastName) {
		this.director8LastName = director8LastName;
	}

	public String getDirector9FirstName() {
		return director9FirstName;
	}

	public void setDirector9FirstName(String director9FirstName) {
		this.director9FirstName = director9FirstName;
	}

	public String getDirector9LastName() {
		return director9LastName;
	}

	public void setDirector9LastName(String director9LastName) {
		this.director9LastName = director9LastName;
	}

	public String getDirector10FirstName() {
		return director10FirstName;
	}

	public void setDirector10FirstName(String director10FirstName) {
		this.director10FirstName = director10FirstName;
	}

	public String getDirector10LastName() {
		return director10LastName;
	}

	public void setDirector10LastName(String director10LastName) {
		this.director10LastName = director10LastName;
	}

	public String getOtherSourceFirstName() {
		return otherSourceFirstName;
	}

	public void setOtherSourceFirstName(String otherSourceFirstName) {
		this.otherSourceFirstName = otherSourceFirstName;
	}

	public String getOtherSourceLastName() {
		return otherSourceLastName;
	}

	public void setOtherSourceLastName(String otherSourceLastName) {
		this.otherSourceLastName = otherSourceLastName;
	}

	public String getAssOrRelConcernFName() {
		return assOrRelConcernFName;
	}

	public void setAssOrRelConcernFName(String assOrRelConcernFName) {
		this.assOrRelConcernFName = assOrRelConcernFName;
	}

	public String getAssOrRelConcernLName() {
		return assOrRelConcernLName;
	}

	public void setAssOrRelConcernLName(String assOrRelConcernLName) {
		this.assOrRelConcernLName = assOrRelConcernLName;
	}

	public String getDirector1Name() {
		return director1Name;
	}

	public void setDirector1Name(String director1Name) {
		this.director1Name = director1Name;
	}

	public String getDirector2Name() {
		return director2Name;
	}

	public void setDirector2Name(String director2Name) {
		this.director2Name = director2Name;
	}

	public String getDirector3Name() {
		return director3Name;
	}

	public void setDirector3Name(String director3Name) {
		this.director3Name = director3Name;
	}

	public String getDirector4Name() {
		return director4Name;
	}

	public void setDirector4Name(String director4Name) {
		this.director4Name = director4Name;
	}

	public String getDirector5Name() {
		return director5Name;
	}

	public void setDirector5Name(String director5Name) {
		this.director5Name = director5Name;
	}

	public String getDirector6Name() {
		return director6Name;
	}

	public void setDirector6Name(String director6Name) {
		this.director6Name = director6Name;
	}

	public String getDirector7Name() {
		return director7Name;
	}

	public void setDirector7Name(String director7Name) {
		this.director7Name = director7Name;
	}

	public String getDirector8Name() {
		return director8Name;
	}

	public void setDirector8Name(String director8Name) {
		this.director8Name = director8Name;
	}

	public String getDirector9Name() {
		return director9Name;
	}

	public void setDirector9Name(String director9Name) {
		this.director9Name = director9Name;
	}

	public String getDirector10Name() {
		return director10Name;
	}

	public void setDirector10Name(String director10Name) {
		this.director10Name = director10Name;
	}

	public String getAssOrRelConcern() {
		return assOrRelConcern;
	}

	public void setAssOrRelConcern(String assOrRelConcern) {
		this.assOrRelConcern = assOrRelConcern;
	}

	public String getOtherSource() {
		return otherSource;
	}

	public void setOtherSource(String otherSource) {
		this.otherSource = otherSource;
	}

	public String getLikeDirector1FirstName() {
		return likeDirector1FirstName;
	}

	public void setLikeDirector1FirstName(String likeDirector1FirstName) {
		this.likeDirector1FirstName = likeDirector1FirstName;
	}

	public String getLikeDirector1LastName() {
		return likeDirector1LastName;
	}

	public void setLikeDirector1LastName(String likeDirector1LastName) {
		this.likeDirector1LastName = likeDirector1LastName;
	}

	public String getLikeDirector2FirstName() {
		return likeDirector2FirstName;
	}

	public void setLikeDirector2FirstName(String likeDirector2FirstName) {
		this.likeDirector2FirstName = likeDirector2FirstName;
	}

	public String getLikeDirector2LastName() {
		return likeDirector2LastName;
	}

	public void setLikeDirector2LastName(String likeDirector2LastName) {
		this.likeDirector2LastName = likeDirector2LastName;
	}

	public String getLikeDirector3FirstName() {
		return likeDirector3FirstName;
	}

	public void setLikeDirector3FirstName(String likeDirector3FirstName) {
		this.likeDirector3FirstName = likeDirector3FirstName;
	}

	public String getLikeDirector3LastName() {
		return likeDirector3LastName;
	}

	public void setLikeDirector3LastName(String likeDirector3LastName) {
		this.likeDirector3LastName = likeDirector3LastName;
	}

	public String getLikeDirector4FirstName() {
		return likeDirector4FirstName;
	}

	public void setLikeDirector4FirstName(String likeDirector4FirstName) {
		this.likeDirector4FirstName = likeDirector4FirstName;
	}

	public String getLikeDirector4LastName() {
		return likeDirector4LastName;
	}

	public void setLikeDirector4LastName(String likeDirector4LastName) {
		this.likeDirector4LastName = likeDirector4LastName;
	}

	public String getLikeDirector5FirstName() {
		return likeDirector5FirstName;
	}

	public void setLikeDirector5FirstName(String likeDirector5FirstName) {
		this.likeDirector5FirstName = likeDirector5FirstName;
	}

	public String getLikeDirector5LastName() {
		return likeDirector5LastName;
	}

	public void setLikeDirector5LastName(String likeDirector5LastName) {
		this.likeDirector5LastName = likeDirector5LastName;
	}

	public String getLikeDirector6FirstName() {
		return likeDirector6FirstName;
	}

	public void setLikeDirector6FirstName(String likeDirector6FirstName) {
		this.likeDirector6FirstName = likeDirector6FirstName;
	}

	public String getLikeDirector6LastName() {
		return likeDirector6LastName;
	}

	public void setLikeDirector6LastName(String likeDirector6LastName) {
		this.likeDirector6LastName = likeDirector6LastName;
	}

	public String getLikeDirector7FirstName() {
		return likeDirector7FirstName;
	}

	public void setLikeDirector7FirstName(String likeDirector7FirstName) {
		this.likeDirector7FirstName = likeDirector7FirstName;
	}

	public String getLikeDirector7LastName() {
		return likeDirector7LastName;
	}

	public void setLikeDirector7LastName(String likeDirector7LastName) {
		this.likeDirector7LastName = likeDirector7LastName;
	}

	public String getLikeDirector8FirstName() {
		return likeDirector8FirstName;
	}

	public void setLikeDirector8FirstName(String likeDirector8FirstName) {
		this.likeDirector8FirstName = likeDirector8FirstName;
	}

	public String getLikeDirector8LastName() {
		return likeDirector8LastName;
	}

	public void setLikeDirector8LastName(String likeDirector8LastName) {
		this.likeDirector8LastName = likeDirector8LastName;
	}

	public String getLikeDirector9FirstName() {
		return likeDirector9FirstName;
	}

	public void setLikeDirector9FirstName(String likeDirector9FirstName) {
		this.likeDirector9FirstName = likeDirector9FirstName;
	}

	public String getLikeDirector9LastName() {
		return likeDirector9LastName;
	}

	public void setLikeDirector9LastName(String likeDirector9LastName) {
		this.likeDirector9LastName = likeDirector9LastName;
	}

	public String getLikeDirector10FirstName() {
		return likeDirector10FirstName;
	}

	public void setLikeDirector10FirstName(String likeDirector10FirstName) {
		this.likeDirector10FirstName = likeDirector10FirstName;
	}

	public String getLikeDirector10LastName() {
		return likeDirector10LastName;
	}

	public void setLikeDirector10LastName(String likeDirector10LastName) {
		this.likeDirector10LastName = likeDirector10LastName;
	}

	public String getLikeAssOrRelConcernFName() {
		return likeAssOrRelConcernFName;
	}

	public void setLikeAssOrRelConcernFName(String likeAssOrRelConcernFName) {
		this.likeAssOrRelConcernFName = likeAssOrRelConcernFName;
	}

	public String getLikeAssOrRelConcernLName() {
		return likeAssOrRelConcernLName;
	}

	public void setLikeAssOrRelConcernLName(String likeAssOrRelConcernLName) {
		this.likeAssOrRelConcernLName = likeAssOrRelConcernLName;
	}

	public String getLikeOtherSourceFirstName() {
		return likeOtherSourceFirstName;
	}

	public void setLikeOtherSourceFirstName(String likeOtherSourceFirstName) {
		this.likeOtherSourceFirstName = likeOtherSourceFirstName;
	}

	public String getLikeOtherSourceLastName() {
		return likeOtherSourceLastName;
	}

	public void setLikeOtherSourceLastName(String likeOtherSourceLastName) {
		this.likeOtherSourceLastName = likeOtherSourceLastName;
	}

	public String getLikeDirector1Name() {
		return likeDirector1Name;
	}

	public void setLikeDirector1Name(String likeDirector1Name) {
		this.likeDirector1Name = likeDirector1Name;
	}

	public String getLikeDirector2Name() {
		return likeDirector2Name;
	}

	public void setLikeDirector2Name(String likeDirector2Name) {
		this.likeDirector2Name = likeDirector2Name;
	}

	public String getLikeDirector3Name() {
		return likeDirector3Name;
	}

	public void setLikeDirector3Name(String likeDirector3Name) {
		this.likeDirector3Name = likeDirector3Name;
	}

	public String getLikeDirector4Name() {
		return likeDirector4Name;
	}

	public void setLikeDirector4Name(String likeDirector4Name) {
		this.likeDirector4Name = likeDirector4Name;
	}

	public String getLikeDirector5Name() {
		return likeDirector5Name;
	}

	public void setLikeDirector5Name(String likeDirector5Name) {
		this.likeDirector5Name = likeDirector5Name;
	}

	public String getLikeDirector6Name() {
		return likeDirector6Name;
	}

	public void setLikeDirector6Name(String likeDirector6Name) {
		this.likeDirector6Name = likeDirector6Name;
	}

	public String getLikeDirector7Name() {
		return likeDirector7Name;
	}

	public void setLikeDirector7Name(String likeDirector7Name) {
		this.likeDirector7Name = likeDirector7Name;
	}

	public String getLikeDirector8Name() {
		return likeDirector8Name;
	}

	public void setLikeDirector8Name(String likeDirector8Name) {
		this.likeDirector8Name = likeDirector8Name;
	}

	public String getLikeDirector9Name() {
		return likeDirector9Name;
	}

	public void setLikeDirector9Name(String likeDirector9Name) {
		this.likeDirector9Name = likeDirector9Name;
	}

	public String getLikeDirector10Name() {
		return likeDirector10Name;
	}

	public void setLikeDirector10Name(String likeDirector10Name) {
		this.likeDirector10Name = likeDirector10Name;
	}

	public String getLikeAssOrRelConcern() {
		return likeAssOrRelConcern;
	}

	public void setLikeAssOrRelConcern(String likeAssOrRelConcern) {
		this.likeAssOrRelConcern = likeAssOrRelConcern;
	}

	public String getLikeOtherSource() {
		return likeOtherSource;
	}

	public void setLikeOtherSource(String likeOtherSource) {
		this.likeOtherSource = likeOtherSource;
	}

	public String getLikeCustCompName() {
		return likeCustCompName;
	}

	public void setLikeCustCompName(String likeCustCompName) {
		this.likeCustCompName = likeCustCompName;
	}

	public String getAdditionalField14() {
		return additionalField14;
	}

	public void setAdditionalField14(String additionalField14) {
		this.additionalField14 = additionalField14;
	}

	public String getLikeCustCompName2() {
		return likeCustCompName2;
	}

	public void setLikeCustCompName2(String likeCustCompName2) {
		this.likeCustCompName2 = likeCustCompName2;
	}

	public String getCustCompName2() {
		return custCompName2;
	}

	public void setCustCompName2(String custCompName2) {
		this.custCompName2 = custCompName2;
	}

}