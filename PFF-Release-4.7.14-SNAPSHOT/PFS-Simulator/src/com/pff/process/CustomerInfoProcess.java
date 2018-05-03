package com.pff.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pff.framework.util.PFFXmlUtil;
import com.pff.vo.CustomerInfoVo;
import com.pff.vo.CustomerContactDetail;

public class CustomerInfoProcess {
	private CustomerContactDetail customerContactDetail=null;


	List<CustomerContactDetail> smsDetList=new ArrayList<>();

	public CustomerInfoVo detailsFetch(CustomerInfoVo detailsVo,boolean flag) throws Exception {

		if(flag)
		{
			List<CustomerInfoVo> dependentsList=new ArrayList<>();
			List<CustomerInfoVo> officeAddressList=new ArrayList<>();
			List<CustomerInfoVo> resAddressList=new ArrayList<>();
			List<CustomerInfoVo> hcAddressList=new ArrayList<>();

			//OfficePhoneNumbersList
			List<CustomerContactDetail> officePhoneNumbersList=new ArrayList<>();	   
			List<CustomerContactDetail> officeFaxNumbersList=new ArrayList<>();
			List<CustomerContactDetail> officeMobileNumbersList=new ArrayList<>();
			List<CustomerContactDetail> resPhoneNumbersList=new ArrayList<>();	   
			List<CustomerContactDetail> resFaxNumbersList=new ArrayList<>();
			List<CustomerContactDetail> resMobileNumbersList=new ArrayList<>();
			List<CustomerContactDetail> hcPhoneNumbersList=new ArrayList<>();	   
			List<CustomerContactDetail> hcFaxNumbersList=new ArrayList<>();
			List<CustomerContactDetail> hcMobileNumbersList=new ArrayList<>();
			List<CustomerContactDetail> hccontactNumbersList=new ArrayList<>();	 

			List<CustomerContactDetail> idemityFaxCountryCode=new ArrayList<>();
			List<CustomerInfoVo> officeEmailAddressesList=new ArrayList<>();
			List<CustomerInfoVo> resEmailAddressesList=new ArrayList<>();

			detailsVo.setReplyType("Retail");
			detailsVo.getHeaderVo().setRefNumber("");
			detailsVo.setIssueCheque("");
			detailsVo.getCustInformation().setCustomerStatus("");
			detailsVo.getCustInformation().setCustomerStatusISO("");
			detailsVo.getCustInformation().setTitle("");
			detailsVo.getCustInformation().setTitleISO("");
			detailsVo.getCustInformation().setFullName("");
			detailsVo.getCustInformation().setShortName("");
			detailsVo.getCustInformation().setMnemonic("");
			detailsVo.getCustInformation().setMotherName("");
			detailsVo.getCustInformation().setFirstName("");
			detailsVo.getCustInformation().setFamilyName("");
			detailsVo.getCustInformation().setSecondName("");
			detailsVo.getCustInformation().setThirdName("");
			detailsVo.getCustInformation().setFourthName("");
			detailsVo.getCustInformation().setDateOfBirth("");
			detailsVo.getCustInformation().setPlaceOfBirth("");
			detailsVo.getCustInformation().setLanguage("");
			detailsVo.getCustInformation().setLanguageISO("");
			detailsVo.getCustInformation().setSector("");
			detailsVo.getCustInformation().setSectorISO("");
			detailsVo.getCustInformation().setIndustry("");	
			detailsVo.getCustInformation().setIndustryISO("");
			detailsVo.getCustInformation().setSegment("");
			detailsVo.getCustInformation().setSectorISO("");
			detailsVo.getCustInformation().setResidencyType("");
			detailsVo.getCustInformation().setResidencyTypeISO("");
			detailsVo.getCustInformation().setFatherName("");
			detailsVo.getCustInformation().setGender("");
			detailsVo.getCustInformation().setGenderISO("");
			detailsVo.getCustInformation().setNationalityISO("");
			detailsVo.getCustInformation().setDualNationality("");
			detailsVo.getCustInformation().setDualNationalityISO("");
			detailsVo.getCustInformation().setCountryOfbirth("");
			detailsVo.getCustInformation().setCountryOfbirthISO("");
			detailsVo.getCustInformation().setMaritalStatus("");
			detailsVo.getCustInformation().setMaritalStatusISO("");
			detailsVo.getCustInformation().setNoOfDependents("");
			for(int i=0;i<1;i++){
				detailsVo.getCustInformation().setDependents("");
				dependentsList.add(detailsVo);
			}
			detailsVo.getCustInformation().setDependentsList(dependentsList);
			detailsVo.getCustInformation().setYearsInUAE("");
			detailsVo.getCustInformation().setRelationshipDate("");
			detailsVo.getCustInformation().setRelationshipManager("");
			detailsVo.getCustInformation().setRelatedParty("");
			detailsVo.setIntroducer("");
			detailsVo.getCustInformation().setBranchCode("");
			detailsVo.getCustInformation().setBranchCodeISO("");
			detailsVo.getCustInformation().setLineManager("");
			detailsVo.setAccountOfficer("");

			//Document CustomerContactDetails

			detailsVo.getDocumentDetails().setIdType(""); 
			detailsVo.getDocumentDetails().setIDTypeISO("");
			detailsVo.getDocumentDetails().setEmiratesIDNo("");
			detailsVo.getDocumentDetails().setEmiratesIDName("");
			detailsVo.getDocumentDetails().setUaeIDExpDate("");
			detailsVo.getDocumentDetails().setEmiratesIDIssueDate("");
			detailsVo.getDocumentDetails().setPassportNumber("");
			detailsVo.getDocumentDetails().setPassportIssueDate("");
			detailsVo.getDocumentDetails().setPassportExpDate("");
			detailsVo.getDocumentDetails().setCIN("");
			detailsVo.getDocumentDetails().setUID("");
			detailsVo.getDocumentDetails().setResidenceVisaNo("");
			detailsVo.getDocumentDetails().setResidenceVisaIssueDate("");
			detailsVo.getDocumentDetails().setResidenceVisaExpiryDate("");
			detailsVo.getDocumentDetails().setUSIDType("");
			detailsVo.getDocumentDetails().setUSIDTypeISO("");
			detailsVo.getDocumentDetails().setUSIDNo("");
			detailsVo.getDocumentDetails().setUSPerson("");
			detailsVo.getDocumentDetails().setWToProvideUSInfo("");

			//Employment Information

			detailsVo.getCustInformation().setEmpStatus("");
			detailsVo.getCustInformation().setEmpStatusISO("");
			detailsVo.getCustInformation().setEmpName("");
			detailsVo.getCustInformation().setOccupation("");
			detailsVo.getCustInformation().setDepartment("");
			detailsVo.getCustInformation().setEmpStartDate("");
			detailsVo.getCustInformation().setSalaryCurrency("");
			detailsVo.getCustInformation().setSalary("");
			detailsVo.getCustInformation().setSalaryDateFreq("");
			detailsVo.getCustInformation().setBusinessType("");
			detailsVo.getCustInformation().setNameOfBusiness("");


			//Address Info

			detailsVo.setPreferredMailingAddress("");
			detailsVo.setPOBox("");
			detailsVo.setFlatNo("");
			detailsVo.setBuildingName("");
			detailsVo.setStreetName("");
			detailsVo.setNearstLandmark("");
			detailsVo.setEmirate("");
			detailsVo.setEmirateISO("");
			detailsVo.setCountry("");
			detailsVo.setCountryISO("");
			officeAddressList.add(detailsVo);

			detailsVo.setOfficeList(officeAddressList);			
			for(int i=0;i<2;i++)
			{     
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				officePhoneNumbersList.add(customerContactDetail);
			}
			detailsVo.setOfficePhoneumbersList(officePhoneNumbersList);			
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				officeFaxNumbersList.add(customerContactDetail);
			}
			detailsVo.setOfficeFaxNumbersList(officeFaxNumbersList);			
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				detailsVo.setEmailAddress("hyd@pennanttech.com");
				officeEmailAddressesList.add(detailsVo);
			}
			detailsVo.setOfficeEmailAddressList(officeEmailAddressesList);
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				officeMobileNumbersList.add(customerContactDetail);
			}

			detailsVo.setOfficeMobileNumbersList(officeMobileNumbersList);

			//Residence
			detailsVo.setPreferredMailingAddress("");
			detailsVo.setPOBox("");
			detailsVo.setFlatNo("");
			detailsVo.setBuildingName("");
			detailsVo.setStreetName("");
			detailsVo.setNearstLandmark("");
			detailsVo.setEmirate("");
			detailsVo.setEmirateISO("");
			detailsVo.setCountry("");
			detailsVo.setCountryISO("");
			resAddressList.add(detailsVo);

			detailsVo.setResidenceList(resAddressList);
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				resPhoneNumbersList.add(customerContactDetail);
			}
			detailsVo.setResPhoneumbersList(resPhoneNumbersList);
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				resFaxNumbersList.add(customerContactDetail);
			}
			detailsVo.setResFaxNumbersList(resFaxNumbersList);
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				detailsVo.setEmailAddress("hyd@pennanttech.com");
				resEmailAddressesList.add(detailsVo);
			}
			detailsVo.setResEmailAddressList(resEmailAddressesList);
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				resMobileNumbersList.add(customerContactDetail);
			}
			detailsVo.setResMobileNumbersList(resMobileNumbersList);

			//HomeCountry

			detailsVo.setPreferredMailingAddress("");
			detailsVo.setPOBox("");
			detailsVo.setFlatNo("");
			detailsVo.setBuildingName("");
			detailsVo.setStreetName("");
			detailsVo.setNearstLandmark("");
			detailsVo.setEmirate("");
			detailsVo.setEmirateISO("");
			detailsVo.setCountry("");
			detailsVo.setCountryISO("");
			detailsVo.setResType("");
			hcAddressList.add(detailsVo);

			detailsVo.setHcList(hcAddressList);
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				hcPhoneNumbersList.add(customerContactDetail);
			}
			detailsVo.setHcPhoneumbersList(hcPhoneNumbersList);	
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				hcFaxNumbersList.add(customerContactDetail);
			}
			detailsVo.setHcFaxNumbersList(hcFaxNumbersList);
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				hcMobileNumbersList.add(customerContactDetail);
			}
			detailsVo.setHcMobileNumbersList(hcMobileNumbersList);
			detailsVo.setRelationShip("");
			detailsVo.setContactName("");
			for(int i=0;i<1;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				hccontactNumbersList.add(customerContactDetail);
			}
			detailsVo.setHcContactNumbersList(hccontactNumbersList);

			//fax idemity
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				idemityFaxCountryCode.add(customerContactDetail);
			}
			detailsVo.setFaxIdemityNumbersList(idemityFaxCountryCode);
			detailsVo.setEmailAddress("");
			detailsVo.setEmailIndemity("");

			//KYC CustomerContactDetail

			detailsVo.setIntroducer("");
			detailsVo.getKycDetails().setKYCRiskLevel("");
			detailsVo.getKycDetails().setKYCRiskLevelISO("");
			detailsVo.getKycDetails().setForeignPolicyExposed("");
			detailsVo.getKycDetails().setPliticalyExposed("");
			detailsVo.getKycDetails().setMonthlyTurnover("");
			detailsVo.setIntroducer("");
			detailsVo.getKycDetails().setReferenceName("");
			detailsVo.getKycDetails().setPurposeOfRelationShip("");
			detailsVo.getKycDetails().setSourceOfIncome("");
			detailsVo.getKycDetails().setExpectedTypeOfTrans("");
			detailsVo.getKycDetails().setMonthlyOutageVolume("");
			detailsVo.getKycDetails().setMonthlyIncomeVolume("");
			detailsVo.getKycDetails().setMaximumSingleDeposit("");
			detailsVo.getKycDetails().setMaximumSingleWithdrawal("");
			detailsVo.getKycDetails().setAnnualIncome("");
			detailsVo.getKycDetails().setCountryOfOriginOfFunds("");
			detailsVo.getKycDetails().setCountryOfSourceOfIncome("");
			detailsVo.getKycDetails().setSourceOfWealth("");
			detailsVo.getKycDetails().setIsKYCUptoDate("");
			detailsVo.getKycDetails().setListedOnStockExchange("");
			detailsVo.getKycDetails().setNameOfExchange("");
			detailsVo.getKycDetails().setStockCodeOfCustomer("");
			detailsVo.getKycDetails().setCustomerVisitReport("");
			detailsVo.getKycDetails().setInitialDeposit("");
			detailsVo.getKycDetails().setFutureDeposit("");
			detailsVo.getKycDetails().setAnnualTurnOver("");
			detailsVo.getKycDetails().setParentCompanyDetails("");
			detailsVo.getKycDetails().setNameOfParentCompany("");
			detailsVo.getKycDetails().setParentCompanyPlaceOfIncorp("");
			detailsVo.getKycDetails().setEmirateOfIncop("");
			detailsVo.getKycDetails().setEmirateOfIncopISO("");
			detailsVo.getKycDetails().setNameOfApexCompany("");
			detailsVo.getKycDetails().setNoOfEmployees("");
			detailsVo.getKycDetails().setNoOfUAEBranches("");
			detailsVo.getKycDetails().setNoOfOverseasBranches("");
			detailsVo.getKycDetails().setOverSeasbranches("");
			detailsVo.getKycDetails().setNameOfAuditors("");
			detailsVo.getKycDetails().setFinancialHighlights("");
			detailsVo.getKycDetails().setBankingRelationShip("");
			detailsVo.getKycDetails().setPFFICertfication("");

		}
		else
		{
			List<CustomerContactDetail> estPhoneNumbersList=new ArrayList<>();
			List<CustomerContactDetail> estFaxNumbersList=new ArrayList<>();
			List<CustomerContactDetail> estMobileNumbersList=new ArrayList<>();

			List<CustomerContactDetail> estOtherPhoneNumbersList=new ArrayList<>();	   
			List<CustomerContactDetail> estOtherFaxNumbersList=new ArrayList<>();
			List<CustomerContactDetail> estOtherMobileNumbersList=new ArrayList<>();
			List<CustomerContactDetail> estOtherContactNumbersList=new ArrayList<>();

			List<CustomerInfoVo> estEmailAddressesList=new ArrayList<>();
			List<CustomerInfoVo> estOtherEmailAddressesList=new ArrayList<>();

			List<CustomerInfoVo> estMainCustInfo=new ArrayList<>();
			List<CustomerInfoVo> estOtherCustInfo=new ArrayList<>();
			detailsVo.setReplyType("SME");
			detailsVo.setAccountOfficer("");
			detailsVo.setAccountOfficerISO("");
			detailsVo.getCustInformation().setNameOfEstablishment("");
			detailsVo.getCustInformation().setEstablishmentShortName("");
			detailsVo.getCustInformation().setMnemonic("");
			detailsVo.getCustInformation().setTypeOfEstablishment("");
			detailsVo.getCustInformation().setTypeOfEstablishmentISO("");
			detailsVo.getCustInformation().setIndustry("");
			detailsVo.getCustInformation().setIndustryISO("");
			detailsVo.getCustInformation().setTarget("");
			detailsVo.getCustInformation().setTargetISO("");
			detailsVo.getCustInformation().setCustStatus("");
			detailsVo.getCustInformation().setCustStatusISO("");
			detailsVo.getCustInformation().setLanguage("");
			detailsVo.getCustInformation().setLanguageISO("");
			detailsVo.getCustInformation().setIncorpType("");
			detailsVo.getCustInformation().setIncorpTypeISO("");
			detailsVo.getCustInformation().setCountryOfIncorp("");
			detailsVo.getCustInformation().setCountryOfIncorpISO("");
			detailsVo.getCustInformation().setDateOfIncorporation("");
			detailsVo.getCustInformation().setParentCoCIF("");
			detailsVo.getCustInformation().setAuditor("");
			detailsVo.getCustInformation().setUseChequeBook("");

			//Document CustomerContactDetail

			detailsVo.getDocumentDetails().setTradeLicenseName("");
			detailsVo.getDocumentDetails().setTradeLicenseNumber("");
			detailsVo.getDocumentDetails().setTradeLicenseIssueDate("");
			detailsVo.getDocumentDetails().setTradeLicenseExpDate("");
			detailsVo.getDocumentDetails().setTradeLicenseIssueAuthority("");
			detailsVo.getDocumentDetails().setTradeLicenseIssueAuthorityISO("");
			detailsVo.getDocumentDetails().setCommRegistrationNumber("");
			detailsVo.getDocumentDetails().setChamberMemberNumber("");
			detailsVo.getDocumentDetails().setCommRegistrationIssueDate("");
			detailsVo.getDocumentDetails().setCommRegistrationExpDate("");
			detailsVo.getDocumentDetails().setDocumentIDNumber("");
			detailsVo.getDocumentDetails().setDocumentIDType("");
			detailsVo.getDocumentDetails().setDocumentIDTypeISO("");
			detailsVo.getDocumentDetails().setNameAsPerID("");
			detailsVo.getDocumentDetails().setIssuingAuthority("");
			detailsVo.getDocumentDetails().setIssuingAuthorityISO("");
			detailsVo.getDocumentDetails().setIdIssueDate("");
			detailsVo.getDocumentDetails().setIdExpiryDate("");

			//Financial Information

			detailsVo.getCustInformation().setTotalNoOfPartners("");
			detailsVo.getCustInformation().setModeOfOperation("");
			detailsVo.getCustInformation().setPowerOfAttorney("");
			detailsVo.getCustInformation().setAuditedFinancials("");
			detailsVo.getCustInformation().setFaxOfIndemity("");


			detailsVo.getCustInformation().setIdemityFaxCtryCode("");
			detailsVo.getCustInformation().setIdemityFaxCtryCode("");
			detailsVo.getCustInformation().setIdemityFaxAreaCode("");    
			detailsVo.getCustInformation().setIdemityFaxAreaCodeISO("");
			detailsVo.getCustInformation().setIdemityFaxSubsidiaryNo("");

			detailsVo.getCustInformation().setEmailIndemity("");
			detailsVo.getCustInformation().setIndemityEmailAddress("");
			detailsVo.getCustInformation().setChequeBookRequest("");
			detailsVo.getCustInformation().setCurrencyOfFinancials("");
			detailsVo.getCustInformation().setCurrencyOfFinancialsISO("");
			detailsVo.getCustInformation().setTurnOver("");
			detailsVo.getCustInformation().setGrossProfit("");
			detailsVo.getCustInformation().setNetProfit("");
			detailsVo.getCustInformation().setShareCapital("");
			detailsVo.getCustInformation().setNoOfEmployees("");
			detailsVo.getCustInformation().setNatureOfBusiness("");
			detailsVo.getCustInformation().setNatureOfBusinessISO("");
			detailsVo.getCustInformation().setThroughputAmount("");


			//PowerOfAttorney

			detailsVo.getPowerOfAttorney().setPOANationalityISO("");

			//AddressInfo

			//For EstMain
			detailsVo.setPreferredMailingAddress("");
			detailsVo.setPOBox("");
			detailsVo.setFlatNo("");
			detailsVo.setBuildingName("");
			detailsVo.setStreetName("");
			detailsVo.setNearstLandmark("");
			detailsVo.setEmirate("");
			detailsVo.setEmirateISO("");
			detailsVo.setCountry("");
			detailsVo.setCountryISO("");
			estMainCustInfo.add(detailsVo);

			detailsVo.setEstList(estMainCustInfo);

			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();	
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				estPhoneNumbersList.add(customerContactDetail);
			}
			detailsVo.setEstMainPhoneumbersList(estPhoneNumbersList);			
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				estFaxNumbersList.add(customerContactDetail);
			}
			detailsVo.setEstMainFaxNumbersList(estFaxNumbersList);		
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				detailsVo.setEmailAddress("hyd@pennanttech.com");
				estEmailAddressesList.add(detailsVo);
			}
			detailsVo.setEstMainEmailAddressList(estEmailAddressesList);		
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				estMobileNumbersList.add(customerContactDetail);
			}

			detailsVo.setEstMainMobileNumbersList(estMobileNumbersList);
			//For EstOther
			detailsVo.setPreferredMailingAddress("");
			detailsVo.setPOBox("");
			detailsVo.setFlatNo("");
			detailsVo.setBuildingName("");
			detailsVo.setStreetName("");
			detailsVo.setNearstLandmark("");
			detailsVo.setEmirate("");
			detailsVo.setEmirateISO("");
			detailsVo.setCountry("");
			detailsVo.setCountryISO("");
			estOtherCustInfo.add(detailsVo);

			detailsVo.setEstOtherList(estMainCustInfo);
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				estOtherPhoneNumbersList.add(customerContactDetail);
			}
			detailsVo.setEstOtherPhoneNumbersList(estOtherPhoneNumbersList);
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				estOtherFaxNumbersList.add(customerContactDetail);
			}
			detailsVo.setEstOtherFaxNumbersList(estOtherFaxNumbersList);
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				detailsVo.setEmailAddress("hyd@pennanttech.com");
				estOtherEmailAddressesList.add(detailsVo);
			}
			detailsVo.setEstOtherEmailAddressList(estOtherEmailAddressesList);
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				estOtherMobileNumbersList.add(customerContactDetail);
			}
			detailsVo.setEstOtherMobileNumbersList(estOtherMobileNumbersList);
			for(int i=0;i<2;i++)
			{
				customerContactDetail=new CustomerContactDetail();
				customerContactDetail.setCountryCode("91");
				customerContactDetail.setAreaCode("40");
				customerContactDetail.setSubsidiaryNumber("12");
				estOtherContactNumbersList.add(customerContactDetail);
			}
			detailsVo.setContactNoList(estOtherContactNumbersList);

			//CustomerContactDetail

			detailsVo.setRelationShipStartDate("");
			detailsVo.setRelationShipManager("");
			detailsVo.setIntroducer("");
			detailsVo.setBranchID("");
			detailsVo.setBranchIDISO("");
			detailsVo.setLineManager("");
			//KYC CustomerContactDetail

			detailsVo.setIntroducer("");
			detailsVo.getKycDetails().setReferenceName("");
			detailsVo.getKycDetails().setPurposeOfRelationShip("");
			detailsVo.getKycDetails().setSourceOfIncome("");
			detailsVo.getKycDetails().setExpectedTypeOfTrans("");
			detailsVo.getKycDetails().setMonthlyOutageVolume("");
			detailsVo.getKycDetails().setMonthlyIncomeVolume("");
			detailsVo.getKycDetails().setMaximumSingleDeposit("");
			detailsVo.getKycDetails().setMaximumSingleWithdrawal("");
			detailsVo.getKycDetails().setAnnualIncome("");
			detailsVo.getKycDetails().setCountryOfOriginOfFunds("");
			detailsVo.getKycDetails().setCountryOfOriginOfFundsISO("");
			detailsVo.getKycDetails().setCountryOfSourceOfIncome("");
			detailsVo.getKycDetails().setCountryOfSourceOfIncomeISO("");
			detailsVo.getKycDetails().setSourceOfWealth("");
			detailsVo.getKycDetails().setKYCRiskLevel("");
			detailsVo.getKycDetails().setIsKYCUptoDate("");
			detailsVo.getKycDetails().setListedOnStockExchange("");
			detailsVo.getKycDetails().setNameOfExchange("");
			detailsVo.getKycDetails().setStockCodeOfCustomer("");
			detailsVo.getKycDetails().setCustomerVisitReport("");
			detailsVo.getKycDetails().setInitialDeposit("");
			detailsVo.getKycDetails().setFutureDeposit("");
			detailsVo.getKycDetails().setAnnualTurnOver("");
			detailsVo.getKycDetails().setParentCompanyDetails("");
			detailsVo.getKycDetails().setNameOfParentCompany("");
			detailsVo.getKycDetails().setParentCompanyPlaceOfIncorp("");
			detailsVo.getKycDetails().setParentCompanyPlaceOfIncorpISO("");
			detailsVo.getKycDetails().setEmirateOfIncop("");
			detailsVo.getKycDetails().setEmirateOfIncopISO("");
			detailsVo.getKycDetails().setNameOfApexCompany("");
			detailsVo.getKycDetails().setNoOfEmployees("");
			detailsVo.getKycDetails().setNoOfUAEBranches("");
			detailsVo.getKycDetails().setNoOfOverseasBranches("");
			detailsVo.getKycDetails().setOverSeasbranches("");
			detailsVo.getKycDetails().setHaveBranchInUS("");
			detailsVo.getKycDetails().setNameOfAuditors("");
			detailsVo.getKycDetails().setFinancialHighlights("");
			detailsVo.getKycDetails().setBankingRelationShip("");
			detailsVo.getKycDetails().setBankingRelationShipISO("");
			detailsVo.getKycDetails().setPFFICertfication("");

		}

		//PowerOfAttorney

		detailsVo.getPowerOfAttorney().setPOAFlag("");
		detailsVo.getPowerOfAttorney().setPOACIF("");
		detailsVo.getPowerOfAttorney().setPOAHolderName("");
		detailsVo.getPowerOfAttorney().setPOAPassportNumber("");
		detailsVo.getPowerOfAttorney().setPOAIDNumber("");
		detailsVo.getPowerOfAttorney().setPOANationality("");
		detailsVo.getPowerOfAttorney().setPOAIssuanceDate("");
		detailsVo.getPowerOfAttorney().setPOAExpiryDate("");
		detailsVo.getPowerOfAttorney().setPOApassportExpiryDate("");
		detailsVo.getPowerOfAttorney().setPOAIDExpiryDate("");


		//Relational CustomerContactDetail
		detailsVo.setRelationCode("");
		detailsVo.setRelationCodeISO("");
		detailsVo.setRelationShipCIF("");
		detailsVo.setRelationPercentageShare("");

		//SMS Services
		customerContactDetail=new CustomerContactDetail();
		customerContactDetail.setCountryCode("91");
		customerContactDetail.setAreaCode("40");
		customerContactDetail.setSubsidiaryNumber("12");
		this.smsDetList.add(customerContactDetail);
		detailsVo.setSMSList(smsDetList);

		//Rating      
		detailsVo.setInternalRating("");
		detailsVo.setDateOfInternalRating("");

		detailsVo.getHeaderVo().setMessageReturnCode("0000");
		detailsVo.getHeaderVo().setMessageReturnDesc("Success");
		detailsVo.getHeaderVo().setTimeStamp(PFFXmlUtil.getDateFormate(PFFXmlUtil.convertFromAS400(new BigDecimal(System.currentTimeMillis()))));

		return detailsVo;

	}
}
