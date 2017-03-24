package com.pff.vo;

import java.util.List;

public class CustomerInfoVo {

	private PFFMQHeaderVo headerVo = null;
	
	private String  custCIF;
	private String accountOfficer;
	private String accountOfficerISO;
	private String issueCheque;

	// Address
	private String preferredMailingAddress;

	private String POBox;
	private String flatNo;
	private String buildingName;
	private String streetName;
	private String nearstLandmark;
	private String emirate;
	private String emirateISO;
	private String country;
	private String countryISO;
	private String emailAddress;
	private String relationShip;
	private String contactName;
	private String resType;
	private String replyType;

	// Relation CustomerContactDetail
	private String relationCode;
	private String relationCodeISO;
	private String relationShipCIF;
	private String relationPercentageShare;

	// Rating
	private String internalRating;
	private String dateOfInternalRating;

	// Indemity
	private String faxIndemity;
	private String emailIndemity;
	private String indemityEmailAddress;

	// Contact CustomerContactDetail
	private String relationShipStartDate;
	private String relationShipManager;
	private String introducer;
	private String branchID;
	private String branchIDISO;
	private String lineManager;

	private List<CustomerInfoVo> residenceList;
	private List<CustomerInfoVo> officeList;
	private List<CustomerInfoVo> hcList;
	private List<CustomerInfoVo> estList;
	private List<CustomerInfoVo> estOtherList;

	private List<CustomerContactDetail> SMSList;
	private List<CustomerContactDetail> officePhoneumbersList;
	private List<CustomerContactDetail> officeFaxNumbersList;
	private List<CustomerContactDetail> officeMobileNumbersList;
	private List<CustomerContactDetail> resPhoneumbersList;
	private List<CustomerContactDetail> resFaxNumbersList;
	private List<CustomerContactDetail> resMobileNumbersList;
	private List<CustomerContactDetail> hcPhoneumbersList;
	private List<CustomerContactDetail> hcFaxNumbersList;
	private List<CustomerContactDetail> hcMobileNumbersList;
	private List<CustomerContactDetail> hcContactNumbersList;
	private List<CustomerContactDetail> estMainPhoneumbersList;
	private List<CustomerContactDetail> estMainFaxNumbersList;
	private List<CustomerContactDetail> estMainMobileNumbersList;
	private List<CustomerContactDetail> estOtherPhoneNumbersList;
	private List<CustomerContactDetail> estOtherFaxNumbersList;
	private List<CustomerContactDetail> estOtherMobileNumbersList;
	private List<CustomerContactDetail> faxIdemityNumbersList;
	private List<CustomerContactDetail> contactNosList;

	private List<CustomerInfoVo> estMainEmailAddressList;
	private List<CustomerInfoVo> estOtherEmailAddressList;
	private List<CustomerInfoVo> officeEmailAddressList;
	private List<CustomerInfoVo> resEmailAddressList;
	private List<CustomerInfoVo> hcEmailAddressList;

	private List<CustomerContactDetail> contactNoList;


	// Document CustomerContactDetail
	private CustomerDocumentDetails documentDetails = new CustomerDocumentDetails();

	// Customer Employment Information && Financial Information && Personal Information
	private CustomerInforamtion custInformation = new CustomerInforamtion();

	// PowerOfAttorney
	private CustomerPowerOfAttorney powerOfAttorney = new CustomerPowerOfAttorney();

	// KYC CustomerContactDetail
	private KYCDetails kycDetails = new KYCDetails();

	public String getPreferredMailingAddress() {
		return preferredMailingAddress;
	}

	public void setPreferredMailingAddress(String preferredMailingAddress) {
		this.preferredMailingAddress = preferredMailingAddress;
	}

	public String getPOBox() {
		return POBox;
	}

	public void setPOBox(String pOBox) {
		POBox = pOBox;
	}

	public String getFlatNo() {
		return flatNo;
	}

	public void setFlatNo(String flatNo) {
		this.flatNo = flatNo;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getNearstLandmark() {
		return nearstLandmark;
	}

	public void setNearstLandmark(String nearstLandmark) {
		this.nearstLandmark = nearstLandmark;
	}

	public String getEmirate() {
		return emirate;
	}

	public void setEmirate(String emirate) {
		this.emirate = emirate;
	}

	public String getEmirateISO() {
		return emirateISO;
	}

	public void setEmirateISO(String emirateISO) {
		this.emirateISO = emirateISO;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryISO() {
		return countryISO;
	}

	public void setCountryISO(String countryISO) {
		this.countryISO = countryISO;
	}

	public PFFMQHeaderVo getHeaderVo() {
		return headerVo;
	}

	public void setHeaderVo(PFFMQHeaderVo headerVo) {
		this.headerVo = headerVo;
	}

	public CustomerDocumentDetails getDocumentDetails() {
		return documentDetails;
	}

	public void setDocumentDetails(CustomerDocumentDetails documentDetails) {
		this.documentDetails = documentDetails;
	}

	public CustomerPowerOfAttorney getPowerOfAttorney() {
		return powerOfAttorney;
	}

	public void setPowerOfAttorney(CustomerPowerOfAttorney powerOfAttorney) {
		this.powerOfAttorney = powerOfAttorney;
	}

	public String getRelationPercentageShare() {
		return relationPercentageShare;
	}

	public void setRelationPercentageShare(String relationPercentageShare) {
		this.relationPercentageShare = relationPercentageShare;
	}

	public String getRelationShipStartDate() {
		return relationShipStartDate;
	}

	public void setRelationShipStartDate(String relationShipStartDate) {
		this.relationShipStartDate = relationShipStartDate;
	}

	public String getRelationShipManager() {
		return relationShipManager;
	}

	public void setRelationShipManager(String relationShipManager) {
		this.relationShipManager = relationShipManager;
	}

	public String getIntroducer() {
		return introducer;
	}

	public void setIntroducer(String introducer) {
		this.introducer = introducer;
	}

	public String getBranchID() {
		return branchID;
	}

	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}

	public String getBranchIDISO() {
		return branchIDISO;
	}

	public void setBranchIDISO(String branchIDISO) {
		this.branchIDISO = branchIDISO;
	}

	public String getLineManager() {
		return lineManager;
	}

	public void setLineManager(String lineManager) {
		this.lineManager = lineManager;
	}

	public String getRelationCode() {
		return relationCode;
	}

	public void setRelationCode(String relationCode) {
		this.relationCode = relationCode;
	}

	public String getRelationCodeISO() {
		return relationCodeISO;
	}

	public void setRelationCodeISO(String relationCodeISO) {
		this.relationCodeISO = relationCodeISO;
	}

	public String getRelationShipCIF() {
		return relationShipCIF;
	}

	public void setRelationShipCIF(String relationShipCIF) {
		this.relationShipCIF = relationShipCIF;
	}

	public String getInternalRating() {
		return internalRating;
	}

	public void setInternalRating(String internalRating) {
		this.internalRating = internalRating;
	}

	public String getDateOfInternalRating() {
		return dateOfInternalRating;
	}

	public void setDateOfInternalRating(String dateOfInternalRating) {
		this.dateOfInternalRating = dateOfInternalRating;
	}

	public String getFaxIndemity() {
		return faxIndemity;
	}

	public void setFaxIndemity(String faxIndemity) {
		this.faxIndemity = faxIndemity;
	}

	public String getEmailIndemity() {
		return emailIndemity;
	}

	public void setEmailIndemity(String emailIndemity) {
		this.emailIndemity = emailIndemity;
	}

	public String getIndemityEmailAddress() {
		return indemityEmailAddress;
	}

	public void setIndemityEmailAddress(String indemityEmailAddress) {
		this.indemityEmailAddress = indemityEmailAddress;
	}

	public KYCDetails getKycDetails() {
		return kycDetails;
	}

	public void setKycDetails(KYCDetails kycDetails) {
		this.kycDetails = kycDetails;
	}

	public String getIssueCheque() {
		return issueCheque;
	}

	public void setIssueCheque(String issueCheque) {
		this.issueCheque = issueCheque;
	}

	public String getReplyType() {
		return replyType;
	}

	public void setReplyType(String replyType) {
		this.replyType = replyType;
	}

	public CustomerInforamtion getCustInformation() {
		return custInformation;
	}

	public void setCustInformation(CustomerInforamtion custInformation) {
		this.custInformation = custInformation;
	}

	public String getAccountOfficerISO() {
		return accountOfficerISO;
	}

	public void setAccountOfficerISO(String accountOfficerISO) {
		this.accountOfficerISO = accountOfficerISO;
	}

	public String getAccountOfficer() {
		return accountOfficer;
	}

	public void setAccountOfficer(String accountOfficer) {
		this.accountOfficer = accountOfficer;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public List<CustomerInfoVo> getResidenceList() {
		return residenceList;
	}

	public void setResidenceList(List<CustomerInfoVo> residenceList) {
		this.residenceList = residenceList;
	}

	public List<CustomerInfoVo> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<CustomerInfoVo> officeList) {
		this.officeList = officeList;
	}

	public List<CustomerInfoVo> getHcList() {
		return hcList;
	}

	public void setHcList(List<CustomerInfoVo> hcList) {
		this.hcList = hcList;
	}

	public List<CustomerInfoVo> getEstList() {
		return estList;
	}

	public void setEstList(List<CustomerInfoVo> estList) {
		this.estList = estList;
	}

	public List<CustomerInfoVo> getEstOtherList() {
		return estOtherList;
	}

	public void setEstOtherList(List<CustomerInfoVo> estOtherList) {
		this.estOtherList = estOtherList;
	}

	public List<CustomerContactDetail> getSMSList() {
		return SMSList;
	}

	public void setSMSList(List<CustomerContactDetail> sMSList) {
		SMSList = sMSList;
	}

	public List<CustomerContactDetail> getContactNoList() {
		return contactNoList;
	}

	public void setContactNoList(List<CustomerContactDetail> contactNoList) {
		this.contactNoList = contactNoList;
	}

	public List<CustomerContactDetail> getOfficePhoneumbersList() {
		return officePhoneumbersList;
	}

	public void setOfficePhoneumbersList(List<CustomerContactDetail> officePhoneumbersList) {
		this.officePhoneumbersList = officePhoneumbersList;
	}

	public List<CustomerContactDetail> getOfficeFaxNumbersList() {
		return officeFaxNumbersList;
	}

	public void setOfficeFaxNumbersList(List<CustomerContactDetail> officeFaxNumbersList) {
		this.officeFaxNumbersList = officeFaxNumbersList;
	}

	public List<CustomerContactDetail> getOfficeMobileNumbersList() {
		return officeMobileNumbersList;
	}

	public void setOfficeMobileNumbersList(List<CustomerContactDetail> officeMobileNumbersList) {
		this.officeMobileNumbersList = officeMobileNumbersList;
	}

	public List<CustomerContactDetail> getResPhoneumbersList() {
		return resPhoneumbersList;
	}

	public void setResPhoneumbersList(List<CustomerContactDetail> resPhoneumbersList) {
		this.resPhoneumbersList = resPhoneumbersList;
	}

	public List<CustomerContactDetail> getResFaxNumbersList() {
		return resFaxNumbersList;
	}

	public void setResFaxNumbersList(List<CustomerContactDetail> resFaxNumbersList) {
		this.resFaxNumbersList = resFaxNumbersList;
	}

	public List<CustomerContactDetail> getResMobileNumbersList() {
		return resMobileNumbersList;
	}

	public void setResMobileNumbersList(List<CustomerContactDetail> resMobileNumbersList) {
		this.resMobileNumbersList = resMobileNumbersList;
	}

	public List<CustomerContactDetail> getHcPhoneumbersList() {
		return hcPhoneumbersList;
	}

	public void setHcPhoneumbersList(List<CustomerContactDetail> hcPhoneumbersList) {
		this.hcPhoneumbersList = hcPhoneumbersList;
	}

	public List<CustomerContactDetail> getHcFaxNumbersList() {
		return hcFaxNumbersList;
	}

	public void setHcFaxNumbersList(List<CustomerContactDetail> hcFaxNumbersList) {
		this.hcFaxNumbersList = hcFaxNumbersList;
	}

	public List<CustomerContactDetail> getHcMobileNumbersList() {
		return hcMobileNumbersList;
	}

	public void setHcMobileNumbersList(List<CustomerContactDetail> hcMobileNumbersList) {
		this.hcMobileNumbersList = hcMobileNumbersList;
	}

	public List<CustomerContactDetail> getEstMainPhoneumbersList() {
		return estMainPhoneumbersList;
	}

	public void setEstMainPhoneumbersList(List<CustomerContactDetail> estMainPhoneumbersList) {
		this.estMainPhoneumbersList = estMainPhoneumbersList;
	}

	public List<CustomerContactDetail> getEstMainFaxNumbersList() {
		return estMainFaxNumbersList;
	}

	public void setEstMainFaxNumbersList(List<CustomerContactDetail> estMainFaxNumbersList) {
		this.estMainFaxNumbersList = estMainFaxNumbersList;
	}

	public List<CustomerContactDetail> getEstMainMobileNumbersList() {
		return estMainMobileNumbersList;
	}

	public void setEstMainMobileNumbersList(
			List<CustomerContactDetail> estMainMobileNumbersList) {
		this.estMainMobileNumbersList = estMainMobileNumbersList;
	}

	public List<CustomerContactDetail> getEstOtherMobileNumbersList() {
		return estOtherMobileNumbersList;
	}

	public void setEstOtherMobileNumbersList(
			List<CustomerContactDetail> estOtherMobileNumbersList) {
		this.estOtherMobileNumbersList = estOtherMobileNumbersList;
	}

	public List<CustomerContactDetail> getEstOtherPhoneNumbersList() {
		return estOtherPhoneNumbersList;
	}

	public void setEstOtherPhoneNumbersList(
			List<CustomerContactDetail> estOtherPhoneNumbersList) {
		this.estOtherPhoneNumbersList = estOtherPhoneNumbersList;
	}

	public List<CustomerContactDetail> getEstOtherFaxNumbersList() {
		return estOtherFaxNumbersList;
	}

	public void setEstOtherFaxNumbersList(List<CustomerContactDetail> estOtherFaxNumbersList) {
		this.estOtherFaxNumbersList = estOtherFaxNumbersList;
	}

	public List<CustomerContactDetail> getFaxIdemityNumbersList() {
		return faxIdemityNumbersList;
	}

	public void setFaxIdemityNumbersList(List<CustomerContactDetail> faxIdemityNumbersList) {
		this.faxIdemityNumbersList = faxIdemityNumbersList;
	}
	public List<CustomerInfoVo> getEstMainEmailAddressList() {
		return estMainEmailAddressList;
	}

	public void setEstMainEmailAddressList(
			List<CustomerInfoVo> estMainEmailAddressList) {
		this.estMainEmailAddressList = estMainEmailAddressList;
	}

	public List<CustomerInfoVo> getEstOtherEmailAddressList() {
		return estOtherEmailAddressList;
	}

	public void setEstOtherEmailAddressList(
			List<CustomerInfoVo> estOtherEmailAddressList) {
		this.estOtherEmailAddressList = estOtherEmailAddressList;
	}

	public List<CustomerInfoVo> getOfficeEmailAddressList() {
		return officeEmailAddressList;
	}

	public void setOfficeEmailAddressList(
			List<CustomerInfoVo> officeEmailAddressList) {
		this.officeEmailAddressList = officeEmailAddressList;
	}

	public List<CustomerInfoVo> getHcEmailAddressList() {
		return hcEmailAddressList;
	}

	public void setHcEmailAddressList(List<CustomerInfoVo> hcEmailAddressList) {
		this.hcEmailAddressList = hcEmailAddressList;
	}

	public String getRelationShip() {
		return relationShip;
	}

	public void setRelationShip(String relationShip) {
		this.relationShip = relationShip;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public List<CustomerContactDetail> getContactNosList() {
		return contactNosList;
	}

	public void setContactNosList(List<CustomerContactDetail> contactNosList) {
		this.contactNosList = contactNosList;
	}

	public List<CustomerContactDetail> getHcContactNumbersList() {
		return hcContactNumbersList;
	}

	public void setHcContactNumbersList(List<CustomerContactDetail> hcContactNumbersList) {
		this.hcContactNumbersList = hcContactNumbersList;
	}

	public List<CustomerInfoVo> getResEmailAddressList() {
		return resEmailAddressList;
	}

	public void setResEmailAddressList(List<CustomerInfoVo> resEmailAddressList) {
		this.resEmailAddressList = resEmailAddressList;
	}

	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

}
