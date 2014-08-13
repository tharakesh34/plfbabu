package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomerInterfaceData implements Serializable {

	private static final long serialVersionUID = -3356689983350192952L;
	
	private String custCIF;
	private String custFName;
	private String DSRSPCPNC;
	private String defaultAccountSName;
	private String custTypeCode;
	private String custIsBlocked;
	private String custIsActive;
	private String custDftBranch;
	private String groupName;
	private String DSRSPPDAT;
	private String custParentCountry;
	private String custRiskCountry;
	private String custDOB;
	private String custSalutationCode;
	private String custGenderCode;
	private String custPOB;
	private String custPassportNo;
	private String custPassportExpiry;
	private String custIsMinor;
	private String tradeLicensenumber;
	private String tradeLicenseExpiry;
	private String visaNumber;
	private String visaExpirydate;
	private String custCoreBank;
	private String custCtgCode;
	private String custShrtName;
	private String custFNameLclLng;
	private String custShrtNameLclLng;
	private String custCOB;
	private String custRO1;
	private String custIsClosed;
	private String custIsDecease;
	private String custIsTradeFinCust;
	private String custSector;
	private String custSubSector;
	private String custProfession;
	private Object custTotalIncome;
	private String custMaritalSts;
	private String custEmpSts;
	private String custBaseCcy;
	private String custResdCountry;
	private String custNationality;
	private Object custClosedOn;
	private String custStmtFrq;
	private String custIsStmtCombined;
	private Object custStmtLastDate;
	private Object custStmtNextDate;
	private Object custFirstBusinessDate;
	private String custRelation;
	//<!-- Address Details-->
	private String custAddrType;
	private String custAddrHNbr;
	private String custFlatNbr;
	private String custAddrStreet;
	private String custAddrLine1;
	private String custAddrLine2;
	private String custPOBox;
	private String custAddrCity;
	private String custAddrProvince;
	private String custAddrCountry;
	private String custAddrZIP;
	private String custAddrPhone;
	//<!-- customer phone numbers -->	
	private String custOfficePhone;
	private String custMobile;
	private String custOtherPhone;
	private String custResPhone;
	//<!-- Email Details-->
	private String custEMailTypeCode1;
	private String custEMail1;
	private String custEMailTypeCode2;
	private String custEMail2;
	//<!-- Employee Details-->	
	private String custEmpName;
	private Object custEmpFrom;
	private String custEmpDesg;
	private List<CustomerRating>  customerRatinglist=new ArrayList<CustomerInterfaceData.CustomerRating>();
	private List<CustomerIdentity>  customerIdentitylist=new ArrayList<CustomerInterfaceData.CustomerIdentity>();
	private List<ShareHolder>  shareHolderlist=new ArrayList<CustomerInterfaceData.ShareHolder>();
	
	private String custEmpHNbr;
	private String custEMpFlatNbr;
	private String custEmpAddrStreet;
	private String custEMpAddrLine1;
	private String custEMpAddrLine2;
	private String custEmpPOBox;
	private String custEmpAddrCity;
	private String custEmpAddrProvince;
	private String custEmpAddrCountry;
	private String custEmpAddrZIP;
	private String custEmpAddrPhone;
	//	<!-- customer notes-->	
	private String custNotesTitle;
	private String custNotes;
	//<!-- customer Income-->	
	private String custIncomeType;
	private String custIncome;
	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
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
	public String getCustTypeCode() {
		return custTypeCode;
	}
	public void setCustTypeCode(String custTypeCode) {
		this.custTypeCode = custTypeCode;
	}
	public String getCustSalutationCode() {
		return custSalutationCode;
	}
	public void setCustSalutationCode(String custSalutationCode) {
		this.custSalutationCode = custSalutationCode;
	}
	public String getCustFName() {
		return custFName;
	}
	public void setCustFName(String custFName) {
		this.custFName = custFName;
	}
	
	public String getDSRSPCPNC() {
		return DSRSPCPNC;
	}
	public void setDSRSPCPNC(String dSRSPCPNC) {
		DSRSPCPNC = dSRSPCPNC;
	}
	
	public String getDefaultAccountSName() {
		return defaultAccountSName;
	}
	public void setDefaultAccountSName(String defaultAccountSName) {
		this.defaultAccountSName = defaultAccountSName;
	}
	public String getCustShrtName() {
		return custShrtName;
	}
	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}
	public String getCustFNameLclLng() {
		return custFNameLclLng;
	}
	public void setCustFNameLclLng(String custFNameLclLng) {
		this.custFNameLclLng = custFNameLclLng;
	}
	public String getCustShrtNameLclLng() {
		return custShrtNameLclLng;
	}
	public void setCustShrtNameLclLng(String custShrtNameLclLng) {
		this.custShrtNameLclLng = custShrtNameLclLng;
	}
	public String getCustDftBranch() {
		return custDftBranch;
	}
	public void setCustDftBranch(String custDftBranch) {
		this.custDftBranch = custDftBranch;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setDSRSPPDAT(String dSRSPPDAT) {
		DSRSPPDAT = dSRSPPDAT;
	}
	public String getDSRSPPDAT() {
		return DSRSPPDAT;
	}
	public String getCustGenderCode() {
		return custGenderCode;
	}
	public void setCustGenderCode(String custGenderCode) {
		this.custGenderCode = custGenderCode;
	}
	public String getCustDOB() {
		return custDOB;
	}
	public void setCustDOB(String custDOB) {
		this.custDOB = custDOB;
	}
	public String getCustPOB() {
		return custPOB;
	}
	public void setCustPOB(String custPOB) {
		this.custPOB = custPOB;
	}
	public String getCustCOB() {
		return custCOB;
	}
	public void setCustCOB(String custCOB) {
		this.custCOB = custCOB;
	}
	public String getCustPassportNo() {
		return custPassportNo;
	}
	public void setCustPassportNo(String custPassportNo) {
		this.custPassportNo = custPassportNo;
	}
	public String getCustRO1() {
		return custRO1;
	}
	public void setCustRO1(String custRO1) {
		this.custRO1 = custRO1;
	}
	public String getCustIsBlocked() {
		return custIsBlocked;
	}
	public void setCustIsBlocked(String custIsBlocked) {
		this.custIsBlocked = custIsBlocked;
	}
	public String getCustIsActive() {
		return custIsActive;
	}
	public void setCustIsActive(String custIsActive) {
		this.custIsActive = custIsActive;
	}
	public String getCustIsClosed() {
		return custIsClosed;
	}
	public void setCustIsClosed(String custIsClosed) {
		this.custIsClosed = custIsClosed;
	}
	public String getCustIsDecease() {
		return custIsDecease;
	}
	public void setCustIsDecease(String custIsDecease) {
		this.custIsDecease = custIsDecease;
	}
	public String getCustIsTradeFinCust() {
		return custIsTradeFinCust;
	}
	public void setCustIsTradeFinCust(String custIsTradeFinCust) {
		this.custIsTradeFinCust = custIsTradeFinCust;
	}
	public String getCustPassportExpiry() {
		return custPassportExpiry;
	}
	public void setCustPassportExpiry(String custPassportExpiry) {
		this.custPassportExpiry = custPassportExpiry;
	}
	
	public String getCustIsMinor() {
		return custIsMinor;
	}
	public void setCustIsMinor(String custIsMinor) {
		this.custIsMinor = custIsMinor;
	}
	public String getTradeLicensenumber() {
		return tradeLicensenumber;
	}
	public void setTradeLicensenumber(String tradeLicensenumber) {
		this.tradeLicensenumber = tradeLicensenumber;
	}
	public String getTradeLicenseExpiry() {
		return tradeLicenseExpiry;
	}
	public void setTradeLicenseExpiry(String tradeLicenseExpiry) {
		this.tradeLicenseExpiry = tradeLicenseExpiry;
	}
	public String getVisaNumber() {
		return visaNumber;
	}
	public void setVisaNumber(String visaNumber) {
		this.visaNumber = visaNumber;
	}
	public String getVisaExpirydate() {
		return visaExpirydate;
	}
	public void setVisaExpirydate(String visaExpirydate) {
		this.visaExpirydate = visaExpirydate;
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
	public String getCustProfession() {
		return custProfession;
	}
	public void setCustProfession(String custProfession) {
		this.custProfession = custProfession;
	}
	public Object getCustTotalIncome() {
		return custTotalIncome;
	}
	public void setCustTotalIncome(Object object) {
		this.custTotalIncome = object;
	}
	public String getCustMaritalSts() {
		return custMaritalSts;
	}
	public void setCustMaritalSts(String custMaritalSts) {
		this.custMaritalSts = custMaritalSts;
	}
	public String getCustEmpSts() {
		return custEmpSts;
	}
	public void setCustEmpSts(String custEmpSts) {
		this.custEmpSts = custEmpSts;
	}
	public String getCustBaseCcy() {
		return custBaseCcy;
	}
	public void setCustBaseCcy(String custBaseCcy) {
		this.custBaseCcy = custBaseCcy;
	}
	public String getCustParentCountry() {
		return custParentCountry;
	}
	public void setCustParentCountry(String custParentCountry) {
		this.custParentCountry = custParentCountry;
	}
	public String getCustResdCountry() {
		return custResdCountry;
	}
	public void setCustResdCountry(String custResdCountry) {
		this.custResdCountry = custResdCountry;
	}
	public String getCustRiskCountry() {
		return custRiskCountry;
	}
	public void setCustRiskCountry(String custRiskCountry) {
		this.custRiskCountry = custRiskCountry;
	}
	public String getCustNationality() {
		return custNationality;
	}
	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}
	public Object getCustClosedOn() {
		return custClosedOn;
	}
	public void setCustClosedOn(Object object) {
		this.custClosedOn = object;
	}
	public String getCustStmtFrq() {
		return custStmtFrq;
	}
	public void setCustStmtFrq(String custStmtFrq) {
		this.custStmtFrq = custStmtFrq;
	}
	public String getCustIsStmtCombined() {
		return custIsStmtCombined;
	}
	public void setCustIsStmtCombined(String custIsStmtCombined) {
		this.custIsStmtCombined = custIsStmtCombined;
	}
	public Object getCustStmtLastDate() {
		return custStmtLastDate;
	}
	public void setCustStmtLastDate(Object object) {
		this.custStmtLastDate = object;
	}
	public Object getCustStmtNextDate() {
		return custStmtNextDate;
	}
	public void setCustStmtNextDate(Object object) {
		this.custStmtNextDate = object;
	}
	public Object getCustFirstBusinessDate() {
		return custFirstBusinessDate;
	}
	public void setCustFirstBusinessDate(Object object) {
		this.custFirstBusinessDate = object;
	}
	public String getCustRelation() {
		return custRelation;
	}
	public void setCustRelation(String custRelation) {
		this.custRelation = custRelation;
	}
	public String getCustAddrType() {
		return custAddrType;
	}
	public void setCustAddrType(String custAddrType) {
		this.custAddrType = custAddrType;
	}
	public String getCustAddrHNbr() {
		return custAddrHNbr;
	}
	public void setCustAddrHNbr(String custAddrHNbr) {
		this.custAddrHNbr = custAddrHNbr;
	}
	public String getCustFlatNbr() {
		return custFlatNbr;
	}
	public void setCustFlatNbr(String custFlatNbr) {
		this.custFlatNbr = custFlatNbr;
	}
	public String getCustAddrStreet() {
		return custAddrStreet;
	}
	public void setCustAddrStreet(String custAddrStreet) {
		this.custAddrStreet = custAddrStreet;
	}
	public String getCustAddrLine1() {
		return custAddrLine1;
	}
	public void setCustAddrLine1(String custAddrLine1) {
		this.custAddrLine1 = custAddrLine1;
	}
	public String getCustAddrLine2() {
		return custAddrLine2;
	}
	public void setCustAddrLine2(String custAddrLine2) {
		this.custAddrLine2 = custAddrLine2;
	}
	public String getCustPOBox() {
		return custPOBox;
	}
	public void setCustPOBox(String custPOBox) {
		this.custPOBox = custPOBox;
	}
	public String getCustAddrCity() {
		return custAddrCity;
	}
	public void setCustAddrCity(String custAddrCity) {
		this.custAddrCity = custAddrCity;
	}
	public String getCustAddrProvince() {
		return custAddrProvince;
	}
	public void setCustAddrProvince(String custAddrProvince) {
		this.custAddrProvince = custAddrProvince;
	}
	public String getCustAddrCountry() {
		return custAddrCountry;
	}
	public void setCustAddrCountry(String custAddrCountry) {
		this.custAddrCountry = custAddrCountry;
	}
	public String getCustAddrZIP() {
		return custAddrZIP;
	}
	public void setCustAddrZIP(String custAddrZIP) {
		this.custAddrZIP = custAddrZIP;
	}
	public String getCustAddrPhone() {
		return custAddrPhone;
	}
	public void setCustAddrPhone(String custAddrPhone) {
		this.custAddrPhone = custAddrPhone;
	}

	public String getCustEMailTypeCode1() {
		return custEMailTypeCode1;
	}
	public void setCustEMailTypeCode1(String custEMailTypeCode1) {
		this.custEMailTypeCode1 = custEMailTypeCode1;
	}
	public String getCustEMail1() {
		return custEMail1;
	}
	public void setCustEMail1(String custEMail1) {
		this.custEMail1 = custEMail1;
	}
	public String getCustEMailTypeCode2() {
		return custEMailTypeCode2;
	}
	public void setCustEMailTypeCode2(String custEMailTypeCode2) {
		this.custEMailTypeCode2 = custEMailTypeCode2;
	}
	public String getCustEMail2() {
		return custEMail2;
	}
	public void setCustEMail2(String custEMail2) {
		this.custEMail2 = custEMail2;
	}
	public String getCustEmpName() {
		return custEmpName;
	}
	public void setCustEmpName(String custEmpName) {
		this.custEmpName = custEmpName;
	}
	public Object getCustEmpFrom() {
		return custEmpFrom;
	}
	public void setCustEmpFrom(Object object) {
		this.custEmpFrom = object;
	}
	public String getCustEmpDesg() {
		return custEmpDesg;
	}
	public void setCustEmpDesg(String custEmpDesg) {
		this.custEmpDesg = custEmpDesg;
	}
	public String getCustEmpHNbr() {
		return custEmpHNbr;
	}
	public void setCustEmpHNbr(String custEmpHNbr) {
		this.custEmpHNbr = custEmpHNbr;
	}
	public String getCustEMpFlatNbr() {
		return custEMpFlatNbr;
	}
	public void setCustEMpFlatNbr(String custEMpFlatNbr) {
		this.custEMpFlatNbr = custEMpFlatNbr;
	}
	public String getCustEmpAddrStreet() {
		return custEmpAddrStreet;
	}
	public void setCustEmpAddrStreet(String custEmpAddrStreet) {
		this.custEmpAddrStreet = custEmpAddrStreet;
	}
	public String getCustEMpAddrLine1() {
		return custEMpAddrLine1;
	}
	public void setCustEMpAddrLine1(String custEMpAddrLine1) {
		this.custEMpAddrLine1 = custEMpAddrLine1;
	}
	public String getCustEMpAddrLine2() {
		return custEMpAddrLine2;
	}
	public void setCustEMpAddrLine2(String custEMpAddrLine2) {
		this.custEMpAddrLine2 = custEMpAddrLine2;
	}
	public String getCustEmpPOBox() {
		return custEmpPOBox;
	}
	public void setCustEmpPOBox(String custEmpPOBox) {
		this.custEmpPOBox = custEmpPOBox;
	}
	public String getCustEmpAddrCity() {
		return custEmpAddrCity;
	}
	public void setCustEmpAddrCity(String custEmpAddrCity) {
		this.custEmpAddrCity = custEmpAddrCity;
	}
	public String getCustEmpAddrProvince() {
		return custEmpAddrProvince;
	}
	public void setCustEmpAddrProvince(String custEmpAddrProvince) {
		this.custEmpAddrProvince = custEmpAddrProvince;
	}
	public String getCustEmpAddrCountry() {
		return custEmpAddrCountry;
	}
	public void setCustEmpAddrCountry(String custEmpAddrCountry) {
		this.custEmpAddrCountry = custEmpAddrCountry;
	}
	public String getCustEmpAddrZIP() {
		return custEmpAddrZIP;
	}
	public void setCustEmpAddrZIP(String custEmpAddrZIP) {
		this.custEmpAddrZIP = custEmpAddrZIP;
	}
	public String getCustEmpAddrPhone() {
		return custEmpAddrPhone;
	}
	public void setCustEmpAddrPhone(String custEmpAddrPhone) {
		this.custEmpAddrPhone = custEmpAddrPhone;
	}
	public String getCustNotesTitle() {
		return custNotesTitle;
	}
	public void setCustNotesTitle(String custNotesTitle) {
		this.custNotesTitle = custNotesTitle;
	}
	public String getCustNotes() {
		return custNotes;
	}
	public void setCustNotes(String custNotes) {
		this.custNotes = custNotes;
	}
	public String getCustOfficePhone() {
		return custOfficePhone;
	}
	public void setCustOfficePhone(String custOfficePhone) {
		this.custOfficePhone = custOfficePhone;
	}
	public String getCustMobile() {
		return custMobile;
	}
	public void setCustMobile(String phoneAreaCode) {
		this.custMobile = phoneAreaCode;
	}
	public String getCustOtherPhone() {
		return custOtherPhone;
	}
	public void setCustOtherPhone(String phoneCountryCode) {
		this.custOtherPhone = phoneCountryCode;
	}
	public String getCustResPhone() {
		return custResPhone;
	}
	public void setCustResPhone(String phoneTypeCode) {
		this.custResPhone = phoneTypeCode;
	}

	public String getCustIncomeType() {
		return custIncomeType;
	}
	public void setCustIncomeType(String custIncomeType) {
		this.custIncomeType = custIncomeType;
	}
	public String getCustIncome() {
		return custIncome;
	}
	public void setCustIncome(String custIncome) {
		this.custIncome = custIncome;
	}

	public List<CustomerRating> getCustomerRatinglist() {
		return customerRatinglist;
	}
	public void setCustomerRatinglist(List<CustomerRating> customerRatinglist) {
		this.customerRatinglist = customerRatinglist;
	}



	public void setCustomerIdentitylist(List<CustomerIdentity> customerIdentitylist) {
		this.customerIdentitylist = customerIdentitylist;
	}
	public List<CustomerIdentity> getCustomerIdentitylist() {
		return customerIdentitylist;
	}



	public List<ShareHolder> getShareHolderlist() {
		return shareHolderlist;
	}
	public void setShareHolderlist(List<ShareHolder> shareHolderlist) {
		this.shareHolderlist = shareHolderlist;
	}



	public class CustomerRating{
		//<!-- customer ratings-->	
		private String custRatingType;
		private String custLongRate;
		private String custShortRate;
		
		public String getCustRatingType() {
			return custRatingType;
		}
		public void setCustRatingType(String custRatingType) {
			this.custRatingType = custRatingType;
		}
		public String getCustLongRate() {
			return custLongRate;
		}
		public void setCustLongRate(String custLongRate) {
			this.custLongRate = custLongRate;
		}
		public String getCustShortRate() {
			return custShortRate;
		}
		public void setCustShortRate(String custShortRate) {
			this.custShortRate = custShortRate;
		}
	}
	public class CustomerIdentity{
		//<!-- customer ratings-->	
		private String custIDType;
		private String custIDNumber;
		private String custIDCountry;
		private Object custIDIssueDate;
		private Object custIDExpDate;
		public String getCustIDType() {
			return custIDType;
		}
		public void setCustIDType(String custIDType) {
			this.custIDType = custIDType;
		}
		public String getCustIDNumber() {
			return custIDNumber;
		}
		public void setCustIDNumber(String custIDNumber) {
			this.custIDNumber = custIDNumber;
		}
		public String getCustIDCountry() {
			return custIDCountry;
		}
		public void setCustIDCountry(String custIDCountry) {
			this.custIDCountry = custIDCountry;
		}
		public Object getCustIDIssueDate() {
			return custIDIssueDate;
		}
		public void setCustIDIssueDate(Object custIDIssueDate) {
			this.custIDIssueDate = custIDIssueDate;
		}
		public Object getCustIDExpDate() {
			return custIDExpDate;
		}
		public void setCustIDExpDate(Object custIDExpDate) {
			this.custIDExpDate = custIDExpDate;
		}
		

	}
	public class ShareHolder{
		//<!--Customer Share Holder -->	
		private Object shareHolderIDType;
		private String shareHolderIDRef ;
		private Object shareHolderPerc ;
		private String shareHolderRole;
		private String shareHolderName;
		private String shareHolderNation;
		private String shareHolderRisk;
		private Object shareHolderDOB;
		
		public Object getShareHolderIDType() {
			return shareHolderIDType;
		}
		public void setShareHolderIDType(Object shareHolderIDType) {
			this.shareHolderIDType = shareHolderIDType;
		}
		public String getShareHolderIDRef() {
			return shareHolderIDRef;
		}
		public void setShareHolderIDRef(String shareHolderIDRef) {
			this.shareHolderIDRef = shareHolderIDRef;
		}
		public Object getShareHolderPerc() {
			return shareHolderPerc;
		}
		public void setShareHolderPerc(Object shareHolderPerc) {
			this.shareHolderPerc = shareHolderPerc;
		}
		public String getShareHolderRole() {
			return shareHolderRole;
		}
		public void setShareHolderRole(String shareHolderRole) {
			this.shareHolderRole = shareHolderRole;
		}
		public String getShareHolderName() {
			return shareHolderName;
		}
		public void setShareHolderName(String shareHolderName) {
			this.shareHolderName = shareHolderName;
		}
		public String getShareHolderNation() {
			return shareHolderNation;
		}
		public void setShareHolderNation(String shareHolderNation) {
			this.shareHolderNation = shareHolderNation;
		}
		public String getShareHolderRisk() {
			return shareHolderRisk;
		}
		public void setShareHolderRisk(String shareHolderRisk) {
			this.shareHolderRisk = shareHolderRisk;
		}
		public Object getShareHolderDOB() {
			return shareHolderDOB;
		}
		public void setShareHolderDOB(Object shareHolderDOB) {
			this.shareHolderDOB = shareHolderDOB;
		}
		
		
	}

}
