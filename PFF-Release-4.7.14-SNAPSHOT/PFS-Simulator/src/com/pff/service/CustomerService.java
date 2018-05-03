
//OFFICE
//ResidenceAddress	HOME_RC
//HomeCountryAddress	HOME_PC

package com.pff.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.jaxen.JaxenException;

import com.pennant.exception.CustomerNotFoundException;
import com.pennant.interfaceservice.model.InterfaceCustEmployeeDetail;
import com.pennant.interfaceservice.model.InterfaceCustomer;
import com.pennant.interfaceservice.model.InterfaceCustomerAddress;
import com.pennant.interfaceservice.model.InterfaceCustomerDetail;
import com.pennant.interfaceservice.model.InterfaceCustomerDocument;
import com.pennant.interfaceservice.model.InterfaceCustomerEMail;
import com.pennant.interfaceservice.model.InterfaceCustomerPhoneNumber;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.DateUtility;
import com.pff.framework.util.PFFUtil;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.CustomerProcess;
import com.pff.vo.PFFMQHeaderVo;

public class CustomerService {

	private static Log LOG = null;
	private static String requestPath="/HB_EAI_REQUEST/Request/getCustomerDetailsRequest/";
	PFFMQHeaderVo headerVo=null;

	//PhoneTypes
	public enum PhoneType{
		OFFICE,FAX,OFFICEMOB,HOMEPHN,HOMEFAX,MOBILE,HC_PHN,HC_FAX,HC_MOB,HC_CONTACT,INDEMFAX,SMSMOB,
		ESTPHN,ESTFAX,ESTMOB,OTHERPHN,OTHERFAX,OTHERMOB		
	}

	//EmailTypes
	public enum EmailType{
		OFFICE,PERSON1,INDEMAIL,ESTMAIL,EOTHMAIL,INDEMFAX,INDEMITYEMAIADDRESS
	}

	//AddressType
	public enum AddressType{
		OFFICE,HOME_RC,HOME_PC,ESTOTHER,ESTMAIN;
	}

	//DocumentTypes
	public enum DocumentType{

		ONE,THREE,EIGHT,FIFTEEN,COMMREGNUM;
	}
	public CustomerService() {
		LOG = LogFactory.getLog(CustomerService.class);
	}
	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException, CustomerNotFoundException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		InterfaceCustomerDetail  detailsVo      =   new InterfaceCustomerDetail();
		OMElement responseElement				=	null;	
		OMElement returnElement					= 	null;

		try {
			this.headerVo=headerVo;
			detailsVo = setRequestDetails(detailsVo, requestData);
			responseElement = generateResponseData(detailsVo,factory);
			returnElement = PFFXmlUtil.generateReturnElement(headerVo, factory, responseElement);
		} 
		catch (Exception e) {
			LOG.info("processRequest()-->Exception");	
			LOG.error(e.getMessage(),e);			
			headerVo.setMessageReturnCode("9903");
			headerVo.setMessageReturnDesc("Error :"+e.getMessage());	
		}

		LOG.exiting("processRequest()",returnElement);
		return returnElement;

	}


	private InterfaceCustomerDetail setRequestDetails(InterfaceCustomerDetail custDetails,OMElement requestData) throws Exception {
		LOG.entering("setRequestDetails()");

		boolean errorFlag			= 	false;
		String errorMessage			=	null;
		try {

			this.headerVo.setRefNumber(PFFXmlUtil.getStringValue(requestData,true, true,"ReferenceNum",requestPath));
			custDetails.setCustCIF(PFFXmlUtil.getStringValue(requestData,true, true,"CIF",requestPath));
			this.headerVo.setTimeStamp(PFFXmlUtil.getStringValue(requestData,true, true,"TimeStamp",requestPath));
		} catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	

		if (!errorFlag){
			CustomerProcess process= new  CustomerProcess();
			try {
				SqlConnection con=new SqlConnection();			
				custDetails=process.fetchCustDetails(custDetails.getCustCIF(),con.getConnection());				
				this.headerVo.setMessageReturnCode("0000");
				this.headerVo.setMessageReturnDesc("SUCCESS");
			}
			catch (Exception e) {
				this.headerVo.setMessageReturnCode("9901");
				this.headerVo.setMessageReturnDesc("Error :"+e.getMessage());
			}

		} else {
			this.headerVo.setMessageReturnCode("9902");
			this.headerVo.setMessageReturnDesc("Error :"+errorMessage);

		}
		LOG.exiting("setRequestDetails", custDetails);
		return custDetails;
	}


	public OMElement generateResponseData(InterfaceCustomerDetail detailsVo,OMFactory factory) throws ParseException {
		LOG.entering("generateResponseData()",detailsVo,factory);	
		
		HashMap<String, OMElement> phoneMap = null;
		HashMap<String, OMElement> emailMap = null;
		OMElement customerType=null;
		OMElement getCustomerDetailsReply = null;
		boolean type = true;
		if(StringUtils.equalsIgnoreCase(detailsVo.getCustomer().getCustCtgCode(), "RETAIL")) {
			type = true;			
		} else {
			type = false;
		}

		OMElement responseBody = factory.createOMElement("Reply",null);

		if(!(detailsVo.getCustomer()==null)){
			phoneMap= getPhoneNumber(factory, detailsVo.getCustomer().getCustCtgCode(), detailsVo.getCustomerPhoneNumList());
			emailMap= getEmail(factory, detailsVo.getCustomer().getCustCtgCode(), detailsVo.getCustomerEMailList());

			getCustomerDetailsReply = PFFXmlUtil.setOMChildElement(factory, responseBody, "getCustomerDetailsReply","");
			PFFXmlUtil.setOMChildElement(factory,getCustomerDetailsReply, "ReferenceNum",this.headerVo.getRefNumber());	
			customerType=PFFXmlUtil.setOMChildElement(factory,getCustomerDetailsReply,"Retail","");
			PFFXmlUtil.setOMChildElement(factory,customerType, "CIF", detailsVo.getCustCIF());	
			getCustomerDetails(factory,customerType,detailsVo.getCustomer());		
			getDocumentDetails(factory,customerType,detailsVo.getCustomer().getCustCtgCode(),detailsVo.getCustomerDocumentsList());		
			getPowerOfAttorney(factory,customerType,type);
			OMElement rating=PFFXmlUtil.setOMChildElement(factory,customerType, "Rating","");
			PFFXmlUtil.setOMChildElement(factory,rating, "InternalRating","");
			PFFXmlUtil.setOMChildElement(factory,rating, "DateOfInternalRating","");	
			OMElement addInfo = factory.createOMElement("AddressInfo",null);		
			for (InterfaceCustomerAddress address : detailsVo.getAddressList()) {
				if(address != null) {
					OMElement addressElement = getAddress(factory, type, address,phoneMap,emailMap);
					if(addressElement != null) {
						addInfo.addChild(addressElement);		
					}
				}
			}
			customerType.addChild(addInfo);		
			customerType.addChild(getSMSServices(factory,phoneMap));
			getRelationDetails(factory,customerType,type);		
			getKYCDetails(factory,customerType,type);	

			if(type){
				PFFXmlUtil.setOMChildElement(factory,customerType, "IssueCheque", "");	
				customerType.addChild(getFaxIndemity(factory,detailsVo.getCustomer().getCustCtgCode(), detailsVo,phoneMap,emailMap));
				getCustomerEmpInformation(factory, customerType, type,detailsVo.getCustEmployeeDetail());}
			else{
				PFFXmlUtil.setOMChildElement(factory,customerType, "AccountOfficer","");
				PFFXmlUtil.setOMChildElement(factory,customerType, "AccountOfficerISO", "");
				getcustFinancialInfo(factory,customerType,detailsVo.getCustEmployeeDetail());
				getContactDetails(factory, customerType,type,detailsVo.getCustomer());	}	
		}
		getCustomerDetailsReply.addChild(customerType);
		PFFXmlUtil.getResponseStatus(factory, getCustomerDetailsReply, this.headerVo.getMessageReturnCode(), this.headerVo.getMessageReturnDesc());
		LOG.exiting("generateResponseData()",detailsVo);

		return responseBody;

	}

  // SMS Services
	private static OMElement getSMSServices(OMFactory factory, HashMap<String, OMElement> phoneMap) {

		LOG.entering("getSMSServices()");

		ArrayList<OMElement> smsList= new ArrayList<OMElement>();
		OMElement smsServicesElement=null;
		OMElement smsServices = null;
		OMElement phoneMapElement=null;
		if(phoneMap.containsKey(PFFUtil.SMSMOBILENO)){
			phoneMapElement=phoneMap.get(PFFUtil.SMSMOBILENO);
		}

		if(!(phoneMapElement==null)){
			smsList.add(phoneMapElement);
		}
		else{
			smsServicesElement=factory.createOMElement("SmsServices", null);
			smsServicesElement.addChild(PFFXmlUtil.getPhoneNumber(factory, "SmsMobileNo", "", "", ""));
			smsServices=smsServicesElement;
		}	
		for (OMElement smsElement : smsList) {
			smsServices=smsElement;
		}
		LOG.exiting("getSMSServices()");
		return smsServices;


	}

  //Fax Indemity
	private static OMElement getFaxIndemity(OMFactory factory, String custCtgCode,InterfaceCustomerDetail custDetails, HashMap<String, OMElement> indemityMap,HashMap<String, OMElement> emailMap) {

		LOG.entering("getFaxIndemity()");
		ArrayList<OMElement> indemityList= new ArrayList<OMElement>();
		OMElement faxindemity = null;
		switch (custDetails.getCustomer().getCustCtgCode()){
		case "Retail":

			if(emailMap.containsKey(PFFUtil.INDEMITYFAXNUMBER)){
				indemityList.add(emailMap.get(PFFUtil.INDEMITYFAXNUMBER));
			}
			else
			{
				indemityList.add(PFFXmlUtil.getPhoneNumber(factory, "FaxNumber", "", "", ""));	
			}
			if(emailMap.containsKey(PFFUtil.EMAILINDEMITY)){
				indemityList.add(emailMap.get(PFFUtil.EMAILINDEMITY));
			}
			else{
				OMElement emailIdemityElement= factory.createOMElement("EmailIndemity",null);;
				indemityList.add(emailIdemityElement);

			}
			if(emailMap.containsKey(PFFUtil.INDEMITYEMAIADDRESS)){
				indemityList.add(emailMap.get(PFFUtil.INDEMITYEMAIADDRESS));
			}
			else
			{
				OMElement emailAddressElement= factory.createOMElement("EmailAddress",null);;
				indemityList.add(emailAddressElement);	
			}
			break;
		case "SME":
			if(indemityMap.containsKey(PFFUtil.INDEMITYFAXNUMBER)){
				indemityList.add(indemityMap.get(PFFUtil.INDEMITYFAXNUMBER)); 
			}
			else
			{
				indemityList.add(PFFXmlUtil.getPhoneNumber(factory, "IndemityFaxNumber", "", "", ""));		
			}
			break;
		}
		faxindemity = factory.createOMElement("Indemity",null);
		PFFXmlUtil.setOMChildElement(factory, faxindemity, "FaxIndemity", "");
		for (OMElement indemityElement : indemityList) {
			faxindemity.addChild(indemityElement);
		}
		LOG.exiting("getFaxIndemity()");
		return faxindemity;

	}

	//Customer Address
	public static OMElement getAddress(OMFactory factory, boolean custType, InterfaceCustomerAddress customerAddress,HashMap<String, OMElement> phoneMap,HashMap<String, OMElement> emailMap){
		LOG.entering("getAddress()");
		String type="";
		OMElement address=null;
		ArrayList<OMElement> phoneList= new ArrayList<OMElement>();
		AddressType[] addressTypes=AddressType.values();
		for(AddressType addressType:addressTypes)
		{
			if(addressType.toString().equalsIgnoreCase(customerAddress.getCustAddrType())){      		

				switch (addressType) {
				case OFFICE:
					type="Office";
					if(phoneMap.containsKey(PFFUtil.OFFICEPHONENO)){
						phoneList.add(phoneMap.get(PFFUtil.OFFICEPHONENO));
						phoneList.add(emailMap.get(PFFUtil.OFFICEEMAILADDRESS));
					}

					if(phoneMap.containsKey(PFFUtil.OFFICEFAXNO)){
						phoneList.add(phoneMap.get(PFFUtil.OFFICEFAXNO));	
					}

					if(phoneMap.containsKey(PFFUtil.OFFICEMOBILENO)){
						phoneList.add(phoneMap.get(PFFUtil.OFFICEMOBILENO));	
					}		
					address = factory.createOMElement(type+"Address",null);

					break;
				case HOME_RC:
					type="Residence";
					if(phoneMap.containsKey(PFFUtil.RESIDENCEPHONENO)){
						phoneList.add(phoneMap.get(PFFUtil.RESIDENCEPHONENO));
						phoneList.add(emailMap.get(PFFUtil.RESIDENCEEMAILADDRESS));
					}

					if(phoneMap.containsKey(PFFUtil.RESIDENCEFAXNO)){
						phoneList.add(phoneMap.get(PFFUtil.RESIDENCEFAXNO));	
					}

					if(phoneMap.containsKey(PFFUtil.RESIDENCEMOBILENO)){
						phoneList.add(phoneMap.get(PFFUtil.RESIDENCEMOBILENO));	
					}

					address = factory.createOMElement(type+"Address",null);
					break;

				case HOME_PC:
					type="HC";
					if(phoneMap.containsKey(PFFUtil.HCPHONENO)){
						phoneList.add(phoneMap.get(PFFUtil.HCPHONENO));
					}

					if(phoneMap.containsKey(PFFUtil.HCFAXNO)){
						phoneList.add(phoneMap.get(PFFUtil.HCFAXNO));	
					}

					if(phoneMap.containsKey(PFFUtil.HCMOBILENO)){
						phoneList.add(phoneMap.get(PFFUtil.HCMOBILENO));	
					}
					if(phoneMap.containsKey(PFFUtil.HCCONTACTNUMBER)){
						phoneList.add(phoneMap.get(PFFUtil.HCCONTACTNUMBER));	
					}
					address = factory.createOMElement("HomeCountryAddress",null);
					break;

				case ESTMAIN:
					type="EstMain";
					if(phoneMap.containsKey(PFFUtil.ESTPHONENO)){
						phoneList.add(phoneMap.get(PFFUtil.ESTPHONENO));
						phoneList.add(emailMap.get(PFFUtil.ESTEMAILADDRESS));
					}

					if(phoneMap.containsKey(PFFUtil.ESTFAXNO)){
						phoneList.add(phoneMap.get(PFFUtil.ESTFAXNO));	
					}

					if(phoneMap.containsKey(PFFUtil.ESTMOBILENO)){
						phoneList.add(phoneMap.get(PFFUtil.ESTMOBILENO));	
					}
					address = factory.createOMElement(type+"Address",null);

					break;
				case ESTOTHER:
					type="EstOther";
					if(phoneMap.containsKey(PFFUtil.ESTOTHERPHONENO)){
						phoneList.add(phoneMap.get(PFFUtil.ESTOTHERPHONENO));
						phoneList.add(emailMap.get(PFFUtil.ESTOTHEREMAILADDRESS));
					}
					if(phoneMap.containsKey(PFFUtil.ESTOTHERFAXNO)){
						phoneList.add(phoneMap.get(PFFUtil.ESTOTHERFAXNO));	
					}
					if(emailMap.containsKey(PFFUtil.ESTOTHEREMAILADDRESS)){
						phoneList.add(phoneMap.get(PFFUtil.ESTOTHERFAXNO));	
					}				
					if(phoneMap.containsKey(PFFUtil.ESTOTHERMOBILENO)){
						phoneList.add(phoneMap.get(PFFUtil.ESTOTHERMOBILENO));	
					}
					address = factory.createOMElement(type+"Address",null);
					break;

				default:
					return null;	
				}

				if(customerAddress != null) {
					PFFXmlUtil.setOMChildElement(factory, address, type+"POBox", customerAddress.getCustPOBox());
					PFFXmlUtil.setOMChildElement(factory, address, type+"FlatNo", customerAddress.getCustFlatNbr());
					PFFXmlUtil.setOMChildElement(factory, address, type+"BuildingName", customerAddress.getCustAddrHNbr());
					PFFXmlUtil.setOMChildElement(factory, address, type+"StreetName", customerAddress.getCustAddrStreet());
					PFFXmlUtil.setOMChildElement(factory, address, type+"NearstLandmark", customerAddress.getCustAddrLine1());
					PFFXmlUtil.setOMChildElement(factory, address, type+"Country", customerAddress.getCustAddrCountry());
					PFFXmlUtil.setOMChildElement(factory, address, type+"Emirate", customerAddress.getCustAddrProvince());
				}

				for (OMElement phoneElement : phoneList) {
					if(phoneElement != null) {
						address.addChild(phoneElement );
					}
				}
			}
		}	
		LOG.exiting("getAddress()");

		return address;
	}


	public HashMap<String, OMElement> getPhoneNumber(OMFactory factory, String custType, List<InterfaceCustomerPhoneNumber> list){

		LOG.entering("getPhoneNumber()");

		HashMap<String, OMElement> phoneMap= new HashMap<String, OMElement>();
		for (InterfaceCustomerPhoneNumber number : list) {
			String tag1="";
			String tag2="";

			PhoneType[] phoneTypes = PhoneType.values();

			for (PhoneType phoneType : phoneTypes) {
				if(phoneType.toString().equals(StringUtils.trimToEmpty(number.getPhoneTypeCode())))
				{
					switch (phoneType) {

					case OFFICE:
						tag1="OfficePhoneNumbers";
						tag2="OfficePhoneNo";
						break;
					case FAX:
						tag1="OfficeFaxNumbers";
						tag2="OfficeFaxNo";
						break;
					case OFFICEMOB:
						tag1="OfficeMobileNumbers";
						tag2="OfficeMobileNo";
						break;
					case HOMEPHN:
						tag1="ResidencePhoneNumbers";
						tag2="ResidencePhoneNo";
						break;						
					case HOMEFAX:
						tag1="ResidenceFaxNumbers";
						tag2="ResidenceFaxNo";
						break;
					case MOBILE:
						tag1="ResidenceMobileNumbers";
						tag2="ResidenceMobileNo";
						break;
					case HC_PHN:
						tag1="HCPhoneNumbers";
						tag2="HCPhoneNo";
						break;
					case HC_FAX:
						tag1="HCFaxNumbers";
						tag2="HCFaxNo";
						break;
					case HC_MOB:
						tag1="HCMobileNumbers";
						tag2="HCMobileNo";
						break;
					case HC_CONTACT:
						tag1="";
						tag2="HCContactNumber";
						break;
					case INDEMFAX:
						if(custType.equalsIgnoreCase("Retail"))
						{
							tag1="";
							tag2="FaxNumber";
						}
						else
						{
							tag1="";
							tag2="IndemityFaxNumber";
						}
						break;
					case SMSMOB:
						tag1="SmsServices ";
						tag2="SmsMobileNo";
						break;
					case ESTPHN:
						tag1="EstPhoneNumbers ";
						tag2="EstPhoneNo";
						break;
					case ESTFAX:
						tag1="EstFaxNumbers ";
						tag2="EstFaxNo";
						break;
					case ESTMOB:
						tag1="EstMobileNumbers ";
						tag2="EstMobileNo";
						break;
					case OTHERPHN:
						tag1="EstOtherPhoneNumbers";
						tag2="EstOtherPhoneNo";
						break;
					case OTHERFAX:
						tag1="EstOtherFaxNumbers ";
						tag2="EstOtherFaxNo";
						break;
					case OTHERMOB:
						tag1="EstOtherMobileNumbers ";
						tag2="EstOtherMobileNo";
						break;

					default:
						break;	
					}	

					OMElement numbers=null;	
					if(!phoneMap.containsValue(number.getPhoneTypeCode())){	
						if(!tag1.equalsIgnoreCase(""))
						{
							numbers = factory.createOMElement(tag1,null);
						}

					}else{
						numbers  = phoneMap.get(number.getPhoneTypeCode());
						phoneMap.remove(number.getPhoneTypeCode());
					}

					if(!tag1.equals("")){
						numbers.addChild(PFFXmlUtil.getPhoneNumber(factory, tag2, number.getPhoneCountryCode(), number.getPhoneAreaCode(), number.getPhoneNumber()));
						phoneMap.put(number.getPhoneTypeCode(), numbers);
					}
					else
					{
						OMElement phoneno=PFFXmlUtil.getPhoneNumber(factory, tag2, number.getPhoneCountryCode(), number.getPhoneAreaCode(), number.getPhoneNumber());
						phoneMap.put(number.getPhoneTypeCode(), phoneno);
					}
				}
			}
		}
		LOG.exiting("getPhoneNumber()");
		return phoneMap;
	}


	private HashMap<String, OMElement> getEmail(OMFactory factory, String custType,List<InterfaceCustomerEMail> mailList) {
		LOG.entering("getEmail()");
		HashMap<String, OMElement> emailMap= new HashMap<String, OMElement>();
		for (InterfaceCustomerEMail email : mailList) {
			String tag1="";
			String tag2="";

			EmailType[] emailTypes=EmailType.values();
			for(EmailType emailType:emailTypes )
			{
				if(emailType.toString().equals(email.getCustEMailTypeCode()))
				{
					switch (emailType) {
					case OFFICE:
						tag1="OfficeEmailAddresses";
						tag2="OfficeEmailAddress";
						break;
					case PERSON1:
						tag1="ResidenceEmailAddresses";
						tag2="ResidenceEmailAddress";
						break;			
					case INDEMAIL:
						tag1="";
						tag2="EmailIndemity";
						break;			
					case ESTMAIL:
						tag1="EstEmailAddresses";
						tag2="EstEmailAddress";
						break;			
					case EOTHMAIL:
						tag1="EstOtherEmailAddresses";
						tag2="EstOtherEmailAddress";
						break;			
					case INDEMFAX:
						tag1="";
						tag2="IndemityFaxNumber";
						break;			
					case INDEMITYEMAIADDRESS:
						tag1="";
						tag2="EmailAddress";
						break;			

					default:
						break;	
					}	     
					OMElement mails=null;	
					if(!emailMap.containsValue(email.getCustEMailTypeCode()))
					{
						if(!tag1.equals("")){	
							emailMap.containsValue(email.getCustEMailTypeCode());
							mails = factory.createOMElement(tag1,null);
						}
					}else{
						mails  = emailMap.get(email.getCustEMailTypeCode());
						emailMap.remove(email.getCustEMailTypeCode());
					}

					if(!tag1.equals(""))
					{
						PFFXmlUtil.setOMChildElement(factory, mails,tag2, email.getCustEMail());
						emailMap.put(email.getCustEMailTypeCode(), mails);}
					else
					{
						OMElement  tag=factory.createOMElement(tag2,null);

						tag.addChild(factory.createOMText(tag, email.getCustEMail()));
						emailMap.put(email.getCustEMailTypeCode(), tag);
					}
				}
			}
		}
		LOG.exiting("getEmail()");
		return emailMap;

	}


	//	Customer Details
	public static OMElement getCustomerDetails(OMFactory factory,OMElement omElement,InterfaceCustomer customer){
		LOG.entering("getCustomerDetails()");
		OMElement custDetails=null;

		if(customer.getCustCtgCode().equalsIgnoreCase("Retail"))
		{
			custDetails = factory.createOMElement("PersonalInfo",null);		
			PFFXmlUtil.setOMChildElement(factory, custDetails, "CustomerStatus",customer.getCustSts());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "CustomerStatusISO","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "Title",customer.getCustSalutationCode());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "TitleISO","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "FullName","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "ShortName",customer.getCustShrtName());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "Mnemonic",customer.getCustCoreBank());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "MotherName",customer.getCustMotherMaiden());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "FirstName",customer.getCustFName());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "FamilyName",customer.getCustLName());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "SecondName",customer.getCustMName());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "ThirdName","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "FourthName","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "DateOfBirth",DateUtility.formateDate(customer.getCustDOB(), "yyyy-MM-dd"));
			PFFXmlUtil.setOMChildElement(factory, custDetails, "PlaceOfBirth",customer.getCustPOB());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "LanguageCode",customer.getCustLng());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "SectorCode",customer.getCustSector());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "IndustryCode",customer.getCustIndustry());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "Segment",customer.getCustSegment());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "SegmentISO","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "ResidencyType","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "ResidencyTypeISO","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "FatherName","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "Gender",customer.getCustGenderCode());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "GenderISO","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "Nationality",customer.getCustNationality());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "NationalityISO","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "DualNationality","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "DualNationalityISO","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "CountryOfbirth",customer.getCustCOB());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "CountryOfbirthISO","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "MaritalStatus",customer.getCustMaritalSts());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "MaritalStatusISO","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "NoOfDependents",customer.getNoOfDependents());

			OMElement dependentsList=PFFXmlUtil.setOMChildElement(factory, custDetails, "DependentsList","");
			PFFXmlUtil.setOMChildElement(factory, dependentsList, "Dependents","");

			PFFXmlUtil.setOMChildElement(factory, custDetails, "YearsInUAE","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "RelationshipDate","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "RelationshipManager",customer.getCustRO1());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "RelatedParty","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "Introducer","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "BranchCode",customer.getCustDftBranch());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "BranchCodeISO","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "lineManager","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "AccountOfficer","");
		}
		else
		{
			custDetails = factory.createOMElement("SMECustDetails",null);
			PFFXmlUtil.setOMChildElement(factory, custDetails, "CustStatus",customer.getCustSts());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "NameOfEstablishment",customer.getCustFName()+" "+customer.getCustLName());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "EstablishmentShortName",customer.getCustShrtName());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "Mnemonic",customer.getCustCoreBank());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "DateOfIncorporation",DateUtility.formateDate(customer.getCustDOB(), "yyyy-MM-dd"));
			PFFXmlUtil.setOMChildElement(factory, custDetails, "LanguageCode",customer.getCustLng());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "IndustryCode",customer.getCustIndustry());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "Target",customer.getCustAddlVar82());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "TypeOfEstablishment",customer.getCustTypeCode());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "IncorporationType","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "CountryOfIncorporation",customer.getCustCOB());
			PFFXmlUtil.setOMChildElement(factory, custDetails, "ParentCoCIF","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "Auditor","");
			PFFXmlUtil.setOMChildElement(factory, custDetails, "UseChequeBook","");
		}
		omElement.addChild(custDetails);
		LOG.exiting("getCustomerDetails()");
		return custDetails;
	}

	//Document Details
	public static OMElement getDocumentDetails(OMFactory factory,OMElement omElement,String custCategroy,List<InterfaceCustomerDocument> list){
		LOG.entering("getDocumentDetails()");
		String tag="";
		OMElement documentDetails = factory.createOMElement("DocumentDetails",null);

		for(InterfaceCustomerDocument docDetails:list){
			String tag2="Number";
			String type=null;
			if(docDetails.getCustDocType().equalsIgnoreCase("01")){
				type="ONE";
			}else if(docDetails.getCustDocType().equalsIgnoreCase("03")){
				type="THREE";
			}
			else if(docDetails.getCustDocType().equalsIgnoreCase("08")){
				type="EIGTHT";
			}
			else if(docDetails.getCustDocType().equalsIgnoreCase("15")){
				type="FIFTEEN";
			}
			else if(docDetails.getCustDocType().equalsIgnoreCase("COMMREGNUM")){
				type="COMMREGNUM";
			}

			DocumentType[] docType=DocumentType.values();
			for(DocumentType doc:docType){
				if(doc.toString().equalsIgnoreCase(StringUtils.trimToEmpty(type)))
				{
					switch (doc) {

					case ONE:tag="EmiratesID";
					break;
					case THREE:tag="Passport";			
					break;
					case EIGHT:tag="ResidenceVisa";			
					break;
					case FIFTEEN:tag="TradeLicense";			
					break;
					case COMMREGNUM:tag="CommRegistration";			
					break;

					default:
						break;
					}

					if(tag.equalsIgnoreCase("EmiratesID")||tag.equalsIgnoreCase("ResidenceVisa")){
						tag2="No";
					}
					PFFXmlUtil.setOMChildElement(factory, documentDetails, tag+tag2,docDetails.getCustDocTitle());	
					PFFXmlUtil.setOMChildElement(factory, documentDetails, tag+"IssueDate",DateUtility.formateDate(docDetails.getCustDocIssuedOn(), "yyyy-MM-dd"));
					if(tag.equalsIgnoreCase("EmiratesID"))		{
						tag="UaeID";
					}
					PFFXmlUtil.setOMChildElement(factory, documentDetails, tag+"ExpDate",DateUtility.formateDate(docDetails.getCustDocExpDate(), "yyyy-MM-dd"));

				}}
		if(custCategroy.equalsIgnoreCase("Retail"))			
		{
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "EmiratesIDName",docDetails.getCustDocName());
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "IDType","");
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "IDTypeISO","");
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "CIN","");
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "UID","");
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "WToProvideUSInfo","");
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "USIDType",docDetails.getCustDocCategory());
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "USIDTypeISO","");
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "USIDNo",docDetails.getCustDocTitle());
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "USPerson","");
			omElement.addChild(documentDetails);}
		else{
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "TradeLicenseName","");
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "TradeLicenseIssueAuthority","");	
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "TradeLicenseIssueAuthorityISO","");

			PFFXmlUtil.setOMChildElement(factory, documentDetails, "NameAsPerID","");	
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "IssuingAuthority","");	
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "IssuingAuthorityISO","");	

			PFFXmlUtil.setOMChildElement(factory, documentDetails, "DocumentIDType","");	
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "DocumentIDTypeISO","");	
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "DocumentIDNumber","");


			PFFXmlUtil.setOMChildElement(factory, documentDetails, "IDIssueDate","");	
			PFFXmlUtil.setOMChildElement(factory, documentDetails, "IDExpiryDate","");
			omElement.addChild(documentDetails);
		}
		}
		LOG.exiting("getDocumentDetails()");
		return documentDetails;

	}	

	//Employment Information
	public static OMElement getCustomerEmpInformation(OMFactory factory,OMElement omElment,boolean type,InterfaceCustEmployeeDetail empDetail){
		LOG.entering("getCustomerEmpInformation()");
		OMElement custInformation =null;

		custInformation = factory.createOMElement("EmploymentInfo",null);
		PFFXmlUtil.setOMChildElement(factory, custInformation, "EmpStatus",empDetail.getEmpStatus());
		PFFXmlUtil.setOMChildElement(factory, custInformation, "EmpStatusISO","");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "EmpName",empDetail.getEmpName());
		PFFXmlUtil.setOMChildElement(factory, custInformation, "Occupation",empDetail.getEmpDesg());
		PFFXmlUtil.setOMChildElement(factory, custInformation, "Department",empDetail.getEmpDept());
		PFFXmlUtil.setOMChildElement(factory, custInformation, "EmpStartDate",DateUtility.formateDate(empDetail.getEmpFrom(), "yyyy-MM-dd"));
		PFFXmlUtil.setOMChildElement(factory, custInformation, "SalaryCurrency","AED");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "Salary",empDetail.getMonthlyIncome());
		PFFXmlUtil.setOMChildElement(factory, custInformation, "SalaryDateFreq","");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "BusinessType","");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "NameOfBusiness","");


		omElment.addChild(custInformation);
		LOG.exiting("getCustomerEmpInformation()");
		return custInformation;

	}

	//Finance Information
	public static OMElement getcustFinancialInfo(OMFactory factory,OMElement omElment,InterfaceCustEmployeeDetail empDetail){
		LOG.entering("getFinanceInformation()");
		OMElement custInformation =null;

		custInformation = factory.createOMElement("FinancialInformation",null);
		PFFXmlUtil.setOMChildElement(factory, custInformation, "TurnOver",empDetail.getMonthlyIncome());
		PFFXmlUtil.setOMChildElement(factory, custInformation, "NatureOfBusiness",empDetail.getEmpSector());
		PFFXmlUtil.setOMChildElement(factory, custInformation, "NoOfEmployees","100");
		
		PFFXmlUtil.setOMChildElement(factory, custInformation, "TotalNoOfPartners","3");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "ModeOfOperation","Mode");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "PowerOfAttorney","PFF");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "AuditedFinancials","PFF");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "FaxOfIndemity","PFF");

		OMElement indemityFaxNumber=PFFXmlUtil.setOMChildElement(factory, custInformation, "IndemityFaxNumber","");
		PFFXmlUtil.setOMChildElement(factory, indemityFaxNumber, "CountryCode","789");
		PFFXmlUtil.setOMChildElement(factory, indemityFaxNumber, "AreaCode","99");
		PFFXmlUtil.setOMChildElement(factory, indemityFaxNumber, "SubsidiaryNumber","9666");

		PFFXmlUtil.setOMChildElement(factory, custInformation, "EmailIndemity","test@pennanttech.com");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "IndemityEmailAddress","test@pennanttech.com");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "ChequeBookRequest","PFF");			
		PFFXmlUtil.setOMChildElement(factory, custInformation, "CurrencyOfFinancials","AED");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "GrossProfit","1000000.00");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "NetProfit","1000000.00");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "ShareCapital","1000000.00");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "ThroughputAmount","1000000.00");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "ThroughputFrequency","PFFTest");
		PFFXmlUtil.setOMChildElement(factory, custInformation, "ThroughputAccount","PFFTest");

		omElment.addChild(custInformation);
		LOG.exiting("getFinanceInformation()");
		return custInformation;

	}

	public static OMElement getPowerOfAttorney(OMFactory factory,OMElement omElement,boolean type){
		LOG.entering("getPowerOfAttorney()");
		OMElement powerOfAttorney = factory.createOMElement("PowerOfAttorney",null);
		PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAFlag","FLAG");
		PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POACIF","CIF");
		PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAHolderName","HolderName");
		PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAPassportNumber","PassportNum");
		PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAIDNumber","IDNUmber");
		PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POANationality","Nationality");
		if(!type)
		{
			PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POANationalityISO","");
		}
		PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAIssuanceDate","2015-10-07");
		PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAExpiryDate","2015-10-07");
		PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POApassportExpiryDate","2015-10-07");
		PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAIDExpiryDate","2015-10-07");

		omElement.addChild(powerOfAttorney);
		LOG.exiting("getPowerOfAttorney()");
		return powerOfAttorney;
	}

	public static OMElement getContactDetails(OMFactory factory,OMElement omElement,boolean type, InterfaceCustomer interfaceCustomer){
		LOG.entering("getContactDetails()");
		OMElement contactDetails=null;

		if(!type)
		{
			contactDetails = factory.createOMElement("ContactDetails",null);
			PFFXmlUtil.setOMChildElement(factory,contactDetails, "RelationshipStartDate","");
			PFFXmlUtil.setOMChildElement(factory,contactDetails, "RelationshipManager",interfaceCustomer.getCustRO1());
			PFFXmlUtil.setOMChildElement(factory,contactDetails, "Introducer","");
			PFFXmlUtil.setOMChildElement(factory,contactDetails, "BranchCode",interfaceCustomer.getCustDftBranch());
			PFFXmlUtil.setOMChildElement(factory,contactDetails, "BranchIDISO","");
			PFFXmlUtil.setOMChildElement(factory,contactDetails, "LineManager","");

		}
		omElement.addChild(contactDetails);
		LOG.exiting("getContactDetails()");
		return contactDetails;
	}


	public static OMElement getRelationDetails(OMFactory factory,OMElement omElement,boolean type){
		LOG.entering("getRelationDetails()");
		OMElement relationDetails = factory.createOMElement("RelationDetails",null);
		PFFXmlUtil.setOMChildElement(factory, relationDetails, "RelationCode","RSCode");
		PFFXmlUtil.setOMChildElement(factory, relationDetails, "RelationCodeISO","");
		PFFXmlUtil.setOMChildElement(factory, relationDetails, "RelationShipCIF","RSCIF");
		if(!type)
		{	
			PFFXmlUtil.setOMChildElement(factory, relationDetails, "RelationPercentageShare","");
		}
		omElement.addChild(relationDetails);
		LOG.exiting("getRelationDetails()");
		return relationDetails;
	}

	//KYC Details
	public static OMElement getKYCDetails(OMFactory factory,OMElement omElement,boolean type){
		
		LOG.entering("getKYCDetails()");
		OMElement kycDetails = factory.createOMElement("KYC",null);
		if(type)
		{

			PFFXmlUtil.setOMChildElement(factory, kycDetails, "KYCRiskLevelISO","KYCRisk");
			PFFXmlUtil.setOMChildElement(factory, kycDetails, "ForeignPolicyExposed","KYCTest");			
			PFFXmlUtil.setOMChildElement(factory, kycDetails, "PliticalyExposed","KYCTest");
			PFFXmlUtil.setOMChildElement(factory, kycDetails, "MonthlyTurnover","1000000.00");			
		}

		PFFXmlUtil.setOMChildElement(factory, kycDetails, "Introducer","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "ReferenceName","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "PurposeOfRelationShip","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "SourceOfIncome","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "ExpectedTypeOfTrans","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "MonthlyOutageVolume","10000000.00");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "MonthlyIncomeVolume","10000000.00");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "MaximumSingleDeposit","10000000.00");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "MaximumSingleWithdrawal","10000000.00");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "AnnualIncome","10000000.00");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "CountryOfOriginOfFunds","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "CountryOfOriginOfFundsISO","");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "CountryOfSourceOfIncome","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "CountryOfSourceOfIncomeISO","");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "SourceOfWealth","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "KYCRiskLevel","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "IsKYCUptoDate",null);
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "ListedOnStockExchange","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "NameOfExchange","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "StockCodeOfCustomer","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "CustomerVisitReport","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "InitialDeposit","10000000.00");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "FutureDeposit","10000000.00");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "AnnualTurnOver","10000000.00");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "ParentCompanyDetails","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "NameOfParentCompany","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "ParentCompanyPlaceOfIncorp","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "ParentCompanyPlaceOfIncorpISO","");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "EmirateOfIncop","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "EmirateOfIncopISO","");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "NameOfApexCompany","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "NoOfEmployees","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "NoOfUAEBranches","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "NoOfOverseasBranches","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "OverSeasbranches","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "HaveBranchInUS","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "NameOfAuditors","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "FinancialHighlights","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "BankingRelationShip","KYCTest");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "BankingRelationShipISO","");
		PFFXmlUtil.setOMChildElement(factory, kycDetails, "PFFICertfication","KYCTest");


		omElement.addChild(kycDetails);
		LOG.exiting("getKYCDetails()");
		return kycDetails;
	}
}
