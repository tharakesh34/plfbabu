package com.pff.service;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.JaxenException;

import com.pennant.interfaceservice.model.InterfaceCustomer;
import com.pennant.interfaceservice.model.InterfaceCustomerAddress;
import com.pennant.interfaceservice.model.InterfaceCustomerDetail;
import com.pennant.interfaceservice.model.InterfaceCustomerDocument;
import com.pennant.interfaceservice.model.InterfaceCustomerEMail;
import com.pennant.interfaceservice.model.InterfaceCustomerEmploymentDetail;
import com.pennant.interfaceservice.model.InterfaceCustomerPhoneNumber;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.DateUtility;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.CreateCIFProcess;
import com.pff.vo.PFFMQHeaderVo;

public class CreateCIFService {
	private static Log LOG = null;
	private String requestPath=null;
	private PFFMQHeaderVo headerVo=null;;

	public CreateCIFService() {
		LOG = LogFactory.getLog(CreateCIFService.class);
	}


	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		InterfaceCustomerDetail detailsVo		=   new InterfaceCustomerDetail();	
		OMElement responseElement				=	null;	
		OMElement returnElement					= 	null;


		try {
			this.headerVo=headerVo;
			detailsVo = setRequestDetails(detailsVo, requestData);
			responseElement = generateResponseData(detailsVo,factory);

		} catch (Exception e) {
			LOG.info("processRequest()-->Exception");	
			LOG.error(e.getMessage(),e);
			this.headerVo.setMessageReturnCode("");
			this.headerVo.setMessageReturnDesc("Error :"+e.getMessage());	
		}
		returnElement = PFFXmlUtil.generateReturnElement(headerVo, factory, responseElement);
		LOG.exiting("processRequest()",returnElement);
		return returnElement;

	}


	private InterfaceCustomerDetail setRequestDetails(InterfaceCustomerDetail interfaceCustDetail,OMElement requestData) throws Exception {
		
		LOG.entering("setRequestDetails()");
		
		OMElement custDataElement=null;
		OMElement headerElement=null;
		OMElement custDetailElement=null;
		OMElement financialInfoElement = null;
		OMElement employmentInfoElement = null;
		OMElement contactDetailElement = null;	
		boolean errorFlag			= 	false;
		String errorMessage			=	null;
		int custType=0;	
		requestPath="/HB_EAI_REQUEST/Request/createCIFRetailRequest/";

		custDataElement = PFFXmlUtil.getOMElement("/HB_EAI_REQUEST/Request/createCIFRetailRequest",requestData);
		
		headerElement= PFFXmlUtil.getOMElement("/HB_EAI_REQUEST/HB_EAI_HEADER",requestData);
		String format=null;
		try {
			interfaceCustDetail.setCustCIF(PFFXmlUtil.getStringValue(requestData,true, true,"CIF",requestPath));
			this.headerVo.setRefNumber(PFFXmlUtil.getStringValue(requestData,true, true,"ReferenceNum",requestPath));
		
			if (("CREATE.CIF.RETAIL").equalsIgnoreCase(headerElement.getFirstChildWithName(new QName("MsgFormat")).getText()))
			{
				format="CREATE.CIF.RETAIL";
			}
			else
			{
				format="UPDATE.CIF.RETAIL";
			}
			if (!headerElement.getFirstChildWithName(new QName("MsgFormat")).getText().equals(format)) {

				//custDataElement = detailElement.getFirstChildWithName(new QName("SME"));
				custDetailElement = custDataElement.getFirstChildWithName(new QName("SMECustDetails"));
				financialInfoElement = custDataElement.getFirstChildWithName(new QName("FinancialInformation"));
				contactDetailElement = custDataElement.getFirstChildWithName(new QName("ContactDetails"));

				custType = 2;
			} else {
				custType = 1;
				//custDataElement = detailElement.getFirstChildWithName(new QName("Retail"));
				custDetailElement = custDataElement.getFirstChildWithName(new QName("PersonalInfo"));
				employmentInfoElement = custDataElement.getFirstChildWithName(new QName("EmploymentInfo"));
			}

			//Common tags for both SME and Retail customer
			OMElement custDocDetailElement = custDataElement.getFirstChildWithName(new QName("DocumentDetails"));
			OMElement addrElement = custDataElement.getFirstChildWithName(new QName("AddressInfo"));

			InterfaceCustomer customer= new InterfaceCustomer();

			customer.setCustCIF(interfaceCustDetail.getCustCIF());
			customer.setCustLng(PFFXmlUtil.getStringValue(custDetailElement, "LanguageCode"));
			customer.setCustIndustry(PFFXmlUtil.getStringValue(custDetailElement, "IndustryCode"));

			List<InterfaceCustomerAddress> custAddressList = null;
			List<InterfaceCustomerPhoneNumber> custPhoneNumList = null;
			List<InterfaceCustomerEMail> custEmailList = null;
			InterfaceCustomer interfaceCustomer = null;
			List<InterfaceCustomerDocument> customerDocumentList = null;
			List<InterfaceCustomerEmploymentDetail> custEmployementDetailList = null;

			switch (custType) {
			case 1:
				//Retail
				
				custAddressList = setCustomerAddress(addrElement,requestData, custType);
				custPhoneNumList = setCustomerPhoneNumber(addrElement, requestData,custType);
				custEmailList = setCustomerEmail(addrElement,requestData, custType);

				interfaceCustomer = setCustomerDetails(custDetailElement,requestData, custType);

				customerDocumentList = setCustomerDocuments(custDocDetailElement,requestData, custType);

				custEmployementDetailList = setEmploymentInfo(employmentInfoElement);

				break;
			case 2:
				//SME

				custAddressList = setCustomerAddress(addrElement, requestData,custType);
				custPhoneNumList = setCustomerPhoneNumber(addrElement, requestData,custType);
				custEmailList = setCustomerEmail(addrElement, requestData,custType);
				interfaceCustomer = setCustomerDetails(custDetailElement,requestData, custType);
				interfaceCustomer = setContactDetails(contactDetailElement, interfaceCustomer);
				customerDocumentList = setCustomerDocuments(custDocDetailElement,requestData, custType);

				// FIXME: No VO class available
				setCustFinancialInfo(financialInfoElement);


				break;
			default:
				break;
			}

			// Common for both SME and Retail Services

			// SMS Service
			//setSMSServiceInfo(custDataElement.getFirstChildWithName(new QName("SmsServices")));

			// POA Service
			setPOAServiceInfo(custDataElement.getFirstChildWithName(new QName("PowerOfAttorney")));

			// Rating Service
			setRatingServiceInfo(custDataElement.getFirstChildWithName(new QName("Rating")));

			// Relational Details
			setRelationDetails(custDataElement.getFirstChildWithName(new QName("RelationDetails")));

			// KYC Details
			setKYCDetails(custDataElement.getFirstChildWithName(new QName("KYC")));

			// Set whole customer information to List
			interfaceCustDetail.setAddressList(custAddressList);
			interfaceCustDetail.setCustomerPhoneNumList(custPhoneNumList);
			interfaceCustDetail.setCustomerEMailList(custEmailList);
			interfaceCustDetail.setCustomer(interfaceCustomer);
			interfaceCustDetail.setCustomerDocumentsList(customerDocumentList);
			interfaceCustDetail.setEmploymentDetailsList(custEmployementDetailList);
		}
		catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
			headerVo.setMessageReturnCode("9900");
			headerVo.setMessageReturnDesc(errorMessage);

		}
		if (!errorFlag){

			try {
				CreateCIFProcess process=new CreateCIFProcess();
				
				//Connection to the Database
				SqlConnection con=new SqlConnection();
				boolean flag=false;
				if(("CREATE.CIF.RETAIL").equals(headerVo.getMessageFormat()))
				{
				 flag= process.createCustomer(interfaceCustDetail, con.getConnection());
				}
				else if(("UPDATE.CIF.RETAIL").equals(headerVo.getMessageFormat()))
				{
			     flag= process.updateCustomerDetails(interfaceCustDetail, con.getConnection());
				}
				if(flag)
				{
					headerVo.setMessageReturnCode("0000");
					headerVo.setMessageReturnDesc("SUCCESS");
				}
				else
				{
					throw new Exception("");
				}
			} catch (Exception e) {
				headerVo.setMessageReturnCode("9901");
				headerVo.setMessageReturnDesc("Error :"+e.getMessage());
			} 

		} else {
			headerVo.setMessageReturnCode("9902");
			headerVo.setMessageReturnDesc("Error :"+errorMessage);
		}

		LOG.exiting("setRequestDetails()", interfaceCustDetail);
		return interfaceCustDetail;
	}


	public OMElement generateResponseData(InterfaceCustomerDetail detailsVo,OMFactory factory) throws ParseException {

		LOG.entering("generateResponseData()",detailsVo,factory);	

		OMElement  responseBody=factory.createOMElement("Reply",null);
		OMElement  reply;
		if (("CREATE.CIF.RETAIL").equalsIgnoreCase(headerVo.getMessageFormat())){
			reply=PFFXmlUtil.setOMChildElement(factory, responseBody, "createCIFRetailReply", "");
		}
		else{
			reply=PFFXmlUtil.setOMChildElement(factory, responseBody, "updateCIFRetailReply", "");
		}
		PFFXmlUtil.setOMChildElement(factory, reply, "ReferenceNum", this.headerVo.getRefNumber());
		PFFXmlUtil.getResponseStatus(factory,reply, this.headerVo.getReturnCode(), this.headerVo.getMessageReturnDesc());

		LOG.exiting("generateResponseData()", detailsVo);

		return responseBody;
	}



	/**
	 * Get list of customer contact details i.e phone,fax,mobile numbers based on the type
	 * @param element
	 * @param path
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	private List<InterfaceCustomerPhoneNumber> setCustomerPhoneNumberByType(OMElement element, OMElement requestData, String path,
			String type,boolean isNotEmpty) throws Exception {

		LOG.debug("Entering setCustomerPhoneNumberByType()");

		requestPath="/HB_EAI_REQUEST/Request/createCIFRetailRequest/AddressInfo/OfficeAddress";
		if (element == null) {
			return null;
		}
		List<InterfaceCustomerPhoneNumber> phoneList = new ArrayList<InterfaceCustomerPhoneNumber>();
		AXIOMXPath xpath = new AXIOMXPath(path);

		if(xpath != null){
			@SuppressWarnings("unchecked")
			List<OMElement> custPhoneDetailList = (List<OMElement>) xpath.selectNodes(element);
			for (OMElement omElement : custPhoneDetailList) {
				InterfaceCustomerPhoneNumber custPhoneNum = new InterfaceCustomerPhoneNumber();
				custPhoneNum.setPhoneTypeCode(type);
				custPhoneNum.setPhoneCountryCode(PFFXmlUtil.getStringValue(omElement,true, isNotEmpty,"CountryCode",path+"/"));
				custPhoneNum.setPhoneAreaCode(PFFXmlUtil.getStringValue(omElement,true, isNotEmpty,"AreaCode",path+"/"));
				custPhoneNum.setPhoneNumber(PFFXmlUtil.getStringValue(omElement,true, isNotEmpty,"SubsidiaryNumber",path+"/"));
				phoneList.add(custPhoneNum);
			}
		}
		LOG.debug("Leaving setCustomerPhoneNumberByType()");

		return phoneList;
	}

	/**
	 * 
	 * @param addrElement
	 * @param custType
	 * @return
	 * @throws Exception 
	 */
	private List<InterfaceCustomerEMail> setCustomerEmail(OMElement addrElement,OMElement requestData, int custType) throws Exception {
		
		LOG.debug("Entering setCustomerEmail()");

		if(addrElement == null) {
			return null;
		}

		List<InterfaceCustomerEMail> emailList = new ArrayList<InterfaceCustomerEMail>();

		if(custType == 1) {

			String path = "/HB_EAI_REQUEST/Request/createCIFRetailRequest/AddressInfo/";
			OMElement ofceAddElement = addrElement.getFirstChildWithName(new QName("OfficeAddress"));
			OMElement resAddElement = addrElement.getFirstChildWithName(new QName("ResidenceAddress"));
			OMElement hcAddElement = addrElement.getFirstChildWithName(new QName("HomeCountryAddress"));

			try {
				if(ofceAddElement != null) {
					String parentTag = "OfficeAddress/";
					emailList.addAll(setCustomerEmailByType(ofceAddElement,requestData,
							path+parentTag+"OfficeEmailAddresses/","OfficeEmailAddress","Office",false));
				}

				if(resAddElement != null) {
					String parentTag = "ResidenceAddress/";
					emailList.addAll(setCustomerEmailByType(resAddElement,requestData, 
							path+parentTag+"ResidenceEmailAddresses/","ResidenceEmailAddress","Residence",false));
				}
				if(resAddElement != null) {
					String parentTag = "HomeCountryAddress/";
					emailList.addAll(setCustomerEmailByType(hcAddElement,requestData, 
							path+parentTag+"HCEmailAddresses/","HCEmailAddress","HomeCountry",false));
				}

			} catch (JaxenException ex) {
				LOG.error("setCustomerPhoneNumber()-->Retail:"+ ex.getMessage());
			}

		} else {

			String path = "/HB_EAI_REQUEST/Request/createCIFRetailRequest/SME/AddressInfo/";

			OMElement estMainAddElement = addrElement.getFirstChildWithName(new QName("EstMainAddress"));
			OMElement estOtherAddElement = addrElement.getFirstChildWithName(new QName("EstOtherAddress"));

			try {
				if(estMainAddElement != null) {
					String parentTag = "EstMainAddress/";
					emailList.addAll(setCustomerEmailByType(estMainAddElement, requestData,
							path+parentTag+"EstEmailAddresses/","EstEmailAddress","EstMain",false));
				}

				if(estOtherAddElement != null) {
					String parentTag = "EstOtherAddress/";
					emailList.addAll(setCustomerEmailByType(estMainAddElement, requestData,
							path+parentTag+"EstOtherEmailAddresses/","EstOtherEmailAddress","EstOther",false));
				}
			} catch (Exception ex) {
				LOG.error("setCustomerPhoneNumber()-->SME:" + ex.getMessage());
			}
		}

		LOG.debug("Leaving setCustomerEmail()");
		
		return emailList;
	}

	/**
	 * Get Customer Email Id's based on the type
	 * @param ofceAddElement
	 * @param path
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	private List<InterfaceCustomerEMail> setCustomerEmailByType(OMElement element, OMElement requestData,String path, 
			String tagname, String type,boolean isNotEmpty) throws Exception {
		
		LOG.debug("Entering setCustomerEmailByType()");

		if(element == null) {
			return null;
		}

		List<InterfaceCustomerEMail> emailList = new ArrayList<InterfaceCustomerEMail>();
		AXIOMXPath	xpath = new AXIOMXPath(path+tagname);
		String emailType = null;
		String tag=null;

		if(xpath != null){
			@SuppressWarnings("unchecked")
			List<OMElement> custEmailList = (List<OMElement>) xpath.selectNodes(element);
			for (OMElement omElement : custEmailList) {
				InterfaceCustomerEMail custEmail = new InterfaceCustomerEMail();

				if(type.equalsIgnoreCase("office"))
				{
					emailType=PFFXmlUtil.OFFICEEMAILADDRESS;
					tag="OfficeEmailAddress";
				}
				else if(type.equalsIgnoreCase("Residence"))
				{
					emailType=PFFXmlUtil.RESIDENCEEMAILADDRESS;
					tag="ResidenceEmailAddress";
				}
				else
				{
					emailType=PFFXmlUtil.HCMAILADDRESS;
					tag="HCEmailAddress";
				}
				custEmail.setCustEMailTypeCode(emailType);
				custEmail.setCustEMail(PFFXmlUtil.getStringValue(requestData,true, isNotEmpty,tag,path+"/"));
				emailList.add(custEmail);
			}
		}
		LOG.debug("Leaving setCustomerEmailByType()");

		return emailList;
	}

	/**
	 * set Customer Address details based on the Address type
	 * @param addrElement
	 * @param custType
	 * @return
	 * @throws Exception
	 */
	private List<InterfaceCustomerAddress> setCustomerAddress(OMElement addrElement,OMElement requestData, int custType) throws Exception {
		
		LOG.debug("Entering setCustomerAddress()");		
		
		if(addrElement == null) {
			return null;
		}

		List<InterfaceCustomerAddress> addrList = new ArrayList<InterfaceCustomerAddress>();

		if (custType == 1) {

			OMElement ofceAddElement = addrElement.getFirstChildWithName(new QName("OfficeAddress"));
			OMElement resAddElement = addrElement.getFirstChildWithName(new QName("ResidenceAddress"));
			OMElement hcAddElement = addrElement.getFirstChildWithName(new QName("HomeCountryAddress"));

			if (ofceAddElement != null) {
				addrList.add(setCustomerAddressByType(ofceAddElement, requestData, "Office"));
			}

			if (resAddElement != null) {
				addrList.add(setCustomerAddressByType(resAddElement, requestData, "Residence"));
			}

			if (hcAddElement != null) {
				addrList.add(setCustomerAddressByType(hcAddElement, requestData,"HC"));
			}

		} else {
			OMElement estMainAddElement = addrElement.getFirstChildWithName(new QName("EstMainAddress"));
			OMElement estOtherAddElement = addrElement.getFirstChildWithName(new QName("EstOtherAddress"));

			if(estMainAddElement != null) {
				addrList.add(setCustomerAddressByType(estMainAddElement,requestData, "EstMain"));
			}

			if(estOtherAddElement != null) {
				addrList.add(setCustomerAddressByType(estOtherAddElement,requestData, "EstOther"));				
			}
		}
		LOG.debug("Leaving setCustomerAddress()");

		return addrList;
	}

	/**
	 * Set Customer phone number details based on the type
	 * @param element
	 * @param addType
	 * @return
	 * @throws Exception
	 */
	private InterfaceCustomerAddress setCustomerAddressByType(OMElement element,OMElement requestData, String addType) throws Exception {
		
		LOG.debug("Entering setCustomerAddressByType()");
		
		String type=null;
		boolean isNotEmpty=false;

		if(element == null) {
			return null;
		}

		InterfaceCustomerAddress address = new InterfaceCustomerAddress();
		if(addType.equalsIgnoreCase("office")){
			requestPath="/HB_EAI_REQUEST/Request/createCIFRetailRequest/AddressInfo/OfficeAddress/";
			type="OFFICE";
			isNotEmpty=false;
		}

		else if(addType.equalsIgnoreCase("Residence")){
			requestPath="/HB_EAI_REQUEST/Request/createCIFRetailRequest/AddressInfo/ResidenceAddress/";
			type="HOME_RC";
			isNotEmpty=true;
		}
		else {
			requestPath="/HB_EAI_REQUEST/Request/createCIFRetailRequest/AddressInfo/HomeCountryAddress/";
			type="HOME_PC";
			isNotEmpty=false;
		}

		address.setCustAddrType(type); 
		address.setCustPOBox(PFFXmlUtil.getStringValue(requestData,true, isNotEmpty,addType+"POBox",requestPath));
		address.setCustFlatNbr(PFFXmlUtil.getStringValue(requestData,true, isNotEmpty, addType+"FlatNo",requestPath));
		address.setCustAddrHNbr(PFFXmlUtil.getStringValue(requestData,true, isNotEmpty, addType+"BuildingName",requestPath));
		//String buildingName = PFFXmlUtil.getStringValue(element, addType+"BuildingName");
		address.setCustAddrStreet(PFFXmlUtil.getStringValue(requestData,true, isNotEmpty, addType+"StreetName",requestPath));
		address.setCustAddrHNbr(PFFXmlUtil.getStringValue(requestData,true, isNotEmpty, addType+"NearstLandmark",requestPath));
		//String emiarate = PFFXmlUtil.getStringValue(element, addType+"Emirate");
		//String emirateISO = PFFXmlUtil.getStringValue(element, addType+"EmirateISO");
		address.setCustAddrCountry(PFFXmlUtil.getStringValue(requestData,true, isNotEmpty, addType+"Country",requestPath));
		//String countryISO = PFFXmlUtil.getStringValue(element, addType+"CountryISO");

		LOG.debug("Leaving setCustomerAddressByType()");

		return address;
	}
	private InterfaceCustomer setContactDetails(OMElement contactDetailElement, InterfaceCustomer interfaceCustomer) {
		LOG.debug("Entering setContactDetails()");

		if (contactDetailElement == null) {
			return null;
		}
		
		interfaceCustomer.setCustRO1(PFFXmlUtil.getStringValue(contactDetailElement, "RelationshipManager")); 
		interfaceCustomer.setCustDftBranch(PFFXmlUtil.getStringValue(contactDetailElement, "BranchCode"));
		String s6 = contactDetailElement.getFirstChildWithName(new QName("LineManager")).getText();
		//PFFXmlUtil.getStringValue(contactDetailElement, "LineManager");
	
		/*String s1 = contactDetailElement.getFirstChildWithName(new QName("RelationShipStartDate")).getText();
		String s2 = contactDetailElement.getFirstChildWithName(new QName("RelationShipManager")).getText();
		String s3 = contactDetailElement.getFirstChildWithName(new QName("Introducer")).getText();
		String s4 = contactDetailElement.getFirstChildWithName(new QName("BranchID")).getText();
		String s5 = contactDetailElement.getFirstChildWithName(new QName("BranchIDISO")).getText();*/

		LOG.debug("Leaving setCustomerAddressByType");
		return null;

	}

	private void setKYCDetails(OMElement kycElement) {
		
		LOG.debug("Entering");

		if(kycElement == null) {
			//return null;
		}

		/*String s1 = kycElement.getFirstChildWithName(new QName("KYCRiskLevel")).getText();
		String s2 = kycElement.getFirstChildWithName(new QName("Introducer")).getText();
		String s3 = kycElement.getFirstChildWithName(new QName("ReferenceName")).getText();
		String s4 = kycElement.getFirstChildWithName(new QName("PurposeOfRelationShip")).getText();
		String s5 = kycElement.getFirstChildWithName(new QName("SourceOfIncome")).getText();
		String s6 = kycElement.getFirstChildWithName(new QName("ExpectedTypeOfTrans")).getText();
		String s7 = kycElement.getFirstChildWithName(new QName("MonthlyOutageVolume")).getText();
		String s8 = kycElement.getFirstChildWithName(new QName("MonthlyIncomeVolume")).getText();
		String s9 = kycElement.getFirstChildWithName(new QName("MaximumSingleDeposit")).getText();
		String s10 = kycElement.getFirstChildWithName(new QName("MaximumSingleWithdrawal")).getText();
		String s11 = kycElement.getFirstChildWithName(new QName("AnnualIncome")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("CountryOfOriginOfFunds")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("CountryOfSourceOfIncome")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("SourceOfWealth")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("IsKYCUptoDate")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("ListedOnStockExchange")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("NameOfExchange")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("StockCodeOfCustomer")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("CustomerVisitReport")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("InitialDeposit")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("FutureDeposit")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("AnnualTurnOver")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("ParentCompanyDetails")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("NameOfParentCompany")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("ParentCompanyPlaceOfIncorp")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("EmirateOfIncop")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("EmirateOfIncopISO")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("NameOfApexCompany")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("NoOfEmployees")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("NoOfUAEBranches")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("NoOfOverseasBranches")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("OverSeasbranches")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("NameOfAuditors")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("FinancialHighlights")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("NameOfApexCompany")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("NoOfEmployees")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("NoOfUAEBranches")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("NoOfOverseasBranches")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("OverSeasbranches")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("NameOfAuditors")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("FinancialHighlights")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("BankingRelationShip")).getText();
		String s12 = kycElement.getFirstChildWithName(new QName("PFFICertfication")).getText();
		 */
		LOG.debug("Leaving setContactDetails()");

	}

	private void setRelationDetails(OMElement relationDetailElement) {
		
		LOG.debug("Entering setRelationDetails");

		if(relationDetailElement == null) {
			//return null;
		}
		//FIXME: No VO class found

		LOG.debug("Leaving setRelationDetails");

	}

	/**
	 * Prepare Rating VO class
	 * @param ratingElement
	 */
	private void setRatingServiceInfo(OMElement ratingElement) {
		
		LOG.debug("Entering setRatingServiceInfo()");

		if(ratingElement == null) {
			//return null;
		}
		/*String s1 = ratingElement.getFirstChildWithName(new QName("InternalRating")).getText();
		String s2 = ratingElement.getFirstChildWithName(new QName("DateOfInternalRating")).getText();*/

		LOG.debug("Leaving setRatingServiceInfo()");

	}

	/**
	 * Prepare POAInfo VO Class
	 * @param poaInfoElement
	 */
	private void setPOAServiceInfo(OMElement poaInfoElement) {
		
		LOG.debug("Entering setPOAServiceInfo()");

		if(poaInfoElement == null) {
			//return null;
		}

		/*String s1 = poaInfoElement.getFirstChildWithName(new QName("POAFlag")).getText();
		String s2 = poaInfoElement.getFirstChildWithName(new QName("POACIF")).getText();
		String s3 = poaInfoElement.getFirstChildWithName(new QName("POAHolderName")).getText();
		String s4 = poaInfoElement.getFirstChildWithName(new QName("POAPassportNumber")).getText();
		String s5 = poaInfoElement.getFirstChildWithName(new QName("POAIDNumber")).getText();
		String s6 = poaInfoElement.getFirstChildWithName(new QName("POANationality")).getText();
		String s7 = poaInfoElement.getFirstChildWithName(new QName("POANationalityISO")).getText();
		String s8 = poaInfoElement.getFirstChildWithName(new QName("POAIssuanceDate")).getText();
		String s9 = poaInfoElement.getFirstChildWithName(new QName("POAExpiryDate")).getText();
		String s10 = poaInfoElement.getFirstChildWithName(new QName("POApassportExpiryDate")).getText();
		String s11 = poaInfoElement.getFirstChildWithName(new QName("POAIDExpiryDate")).getText();*/

		LOG.debug("Leaving setPOAServiceInfo()");

	}

	/**
	 * Prepare SMS Service VO class
	 * @param smsInfoElement
	 */
	private List<InterfaceCustomerPhoneNumber> setSMSServiceInfo(OMElement smsInfoElement) {
		
		LOG.debug("Entering setSMSServiceInfo()");

		if(smsInfoElement == null) {
			//return null;
		}

		List<InterfaceCustomerPhoneNumber> phoneList = new ArrayList<InterfaceCustomerPhoneNumber>();
		InterfaceCustomerPhoneNumber custSMSPhoneNum = new InterfaceCustomerPhoneNumber();

		custSMSPhoneNum.setPhoneTypeCode(PFFXmlUtil.SMSMOBILENO);
		OMElement smsElemet = smsInfoElement.getFirstChildWithName(new QName("SmsMobileNo"));
		custSMSPhoneNum.setPhoneCountryCode(smsElemet.getFirstChildWithName(new QName("CountryCode")).getText());
		custSMSPhoneNum.setPhoneAreaCode(smsElemet.getFirstChildWithName(new QName("AreaCode")).getText());
		custSMSPhoneNum.setPhoneNumber(smsElemet.getFirstChildWithName(new QName("SubsidiaryNumber")).getText());
		phoneList.add(custSMSPhoneNum);

		LOG.debug("Leaving setSMSServiceInfo()");

		return phoneList;
	}

	/**
	 * 
	 * @param employmentInfoElement
	 * @return
	 */
	private List<InterfaceCustomerEmploymentDetail> setEmploymentInfo(OMElement employmentInfoElement) {
		
		LOG.debug("Entering setEmploymentInfo()");

		if (employmentInfoElement == null) {
			//return null;
		}

		InterfaceCustomerEmploymentDetail customerEmploymentDetail = new InterfaceCustomerEmploymentDetail();
		List<InterfaceCustomerEmploymentDetail> employementList = new ArrayList<InterfaceCustomerEmploymentDetail>();

		//(employmentInfoElement.getFirstChildWithName(new QName("EmpStatus")).getText());
		//(employmentInfoElement.getFirstChildWithName(new QName("EmpStatusISO")).getText());
		//customerEmploymentDetail.setCustEmpName(employmentInfoElement.getFirstChildWithName(new QName("EmpName")).getText());//TODO: EmpName is long
		customerEmploymentDetail.setCustEmpDesg(employmentInfoElement.getFirstChildWithName(new QName("Occupation")).getText());
		customerEmploymentDetail.setCustEmpDept(employmentInfoElement.getFirstChildWithName(new QName("Department")).getText());
		String employStartDt=employmentInfoElement.getFirstChildWithName(new QName("EmpStartDate")).getText();
		if(!employStartDt.equals(""))
		{
			customerEmploymentDetail.setCustEmpFrom(DateUtility.getUtilDate(employStartDt,"yyyy-MM-dd"));
		}
		else
		{
			customerEmploymentDetail.setCustEmpFrom(null);
		}

		//(employmentInfoElement.getFirstChildWithName(new QName("SalaryCurrency")).getText());
		//(employmentInfoElement.getFirstChildWithName(new QName("Salary")).getText());
		//customerEmploymentDetail.setCustEmpDept(employmentInfoElement.getFirstChildWithName(new QName("SalaryDateFreq")).getText());
		//customerEmploymentDetail.setCustEmpDept(employmentInfoElement.getFirstChildWithName(new QName("BusinessType")).getText());
		//customerEmploymentDetail.setCustEmpDept(employmentInfoElement.getFirstChildWithName(new QName("NameOfBusiness")).getText());

		employementList.add(customerEmploymentDetail);

		LOG.debug("Leaving setEmploymentInfo()");
		return employementList;
	}

	/**
	 * 
	 * @param financialInfoElement
	 */
	private void setCustFinancialInfo(OMElement financialInfoElement) {
		
		LOG.debug("Entering setCustFinancialInfo()");

		if (financialInfoElement == null) {
			//return null;
		}

		//FIXME: No VO class is available
		/*String str = financialInfoElement.getFirstChildWithName(new QName("TotalNoOfPartners")).getText();
		String str1 = financialInfoElement.getFirstChildWithName(new QName("ModeOfOperation")).getText();
		String str2 = financialInfoElement.getFirstChildWithName(new QName("PowerOfAttorney")).getText();
		String str3 = financialInfoElement.getFirstChildWithName(new QName("AuditedFinancials")).getText();
		String str4 = financialInfoElement.getFirstChildWithName(new QName("FaxOfIndemity")).getText();

		OMElement indemityFaxNumber = financialInfoElement.getFirstChildWithName(new QName("IndemityFaxNumber"));

		String str5 = indemityFaxNumber.getFirstChildWithName(new QName("CountryCode")).getText();
		String str6 = indemityFaxNumber.getFirstChildWithName(new QName("CountryCodeISO")).getText();
		String str7 = indemityFaxNumber.getFirstChildWithName(new QName("AreaCode")).getText();
		String str8 = indemityFaxNumber.getFirstChildWithName(new QName("AreaCodeISO")).getText();
		String str9 = indemityFaxNumber.getFirstChildWithName(new QName("SubsidiaryNumber")).getText();

		String str54 = financialInfoElement.getFirstChildWithName(new QName("EmailIndemity")).getText();
		String str94 = financialInfoElement.getFirstChildWithName(new QName("IndemityEmailAddress")).getText();
		String str34 = financialInfoElement.getFirstChildWithName(new QName("ChequeBookRequest")).getText();
		String str24 = financialInfoElement.getFirstChildWithName(new QName("CurrencyOfFinancials")).getText();
		String str14 = financialInfoElement.getFirstChildWithName(new QName("CurrencyOfFinancialsISO")).getText();
		String str41 = financialInfoElement.getFirstChildWithName(new QName("TurnOver")).getText();
		String str49 = financialInfoElement.getFirstChildWithName(new QName("GrossProfit")).getText();
		String str48 = financialInfoElement.getFirstChildWithName(new QName("NetProfit")).getText();
		String str47 = financialInfoElement.getFirstChildWithName(new QName("ShareCapital")).getText();
		String str46 = financialInfoElement.getFirstChildWithName(new QName("NoOfEmployees")).getText();
		String str45 = financialInfoElement.getFirstChildWithName(new QName("NatureOfBusiness")).getText();
		String str44 = financialInfoElement.getFirstChildWithName(new QName("NatureOfBusinessISO")).getText();
		String str43 = financialInfoElement.getFirstChildWithName(new QName("ThroughputAmount")).getText();
		String str42 = financialInfoElement.getFirstChildWithName(new QName("ThroughputFrequency")).getText();
		String str66 = financialInfoElement.getFirstChildWithName(new QName("ThroughputAccount")).getText();*/

		LOG.debug("Leaving setCustFinancialInfo()");

	}

	/**
	 * Get Customer Document Details
	 * @param custDocDetailElement
	 * @param custType
	 * @return
	 * @throws Exception 
	 */
	private List<InterfaceCustomerDocument> setCustomerDocuments(OMElement custDocDetailElement, OMElement requestData,int custType) throws Exception {
		
		LOG.debug("Entering setCustomerdocuments()");

		if (custDocDetailElement == null) {
			return null;
		}
		//String requestPath="/HB_EAI_REQUEST/Request/createCIFRetailRequest/DocumentDetails/";
		//InterfaceCustomerDocument customerDocuments = new InterfaceCustomerDocument();
		List<InterfaceCustomerDocument> docList = new ArrayList<InterfaceCustomerDocument>();
		if(custType == 1) {
			
			//docDetails.setCustDocType(PFFXmlUtil.getStringValue(requestData,true, true,"IDType",requestPath));
			//(custDocDetailElement.getFirstChildWithName(new QName("IDTypeISO")).getText());
			docList.addAll(setCustDocumentsByType(custDocDetailElement,requestData, "EmiratesID" ,PFFXmlUtil.EMIRATE_ID));
			docList.addAll(setCustDocumentsByType(custDocDetailElement,requestData, "Passport" ,PFFXmlUtil.PASSPORT_ID));
			docList.addAll(setCustDocumentsByType(custDocDetailElement, requestData,"ResidenceVisa" ,PFFXmlUtil.RESIDENCE_VISA));

			//(custDocDetailElement.getFirstChildWithName(new QName("CIN")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("UID")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("USIDType")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("USIDTypeISO")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("USIDNo")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("USPerson")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("WToProvideUSInfo")).getText());
		} else {
			docList.addAll(setCustDocumentsByType(custDocDetailElement,requestData, "TradeLicense" ,PFFXmlUtil.TRADELICENSE));
			//(custDocDetailElement.getFirstChildWithName(new QName("TradeLicenseIssueAuthority")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("TradeLicenseIssueAuthorityISO")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("CommRegistrationNumber")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("ChamberMemberNumber")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("CommRegistrationIssueDate")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("CommRegistrationExpDate")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("DocumentIDNumber")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("DocumentIDType")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("DocumentIDTypeISO")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("NameAsPerID")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("IssuingAuthority")).getText());
			//(custDocDetailElement.getFirstChildWithName(new QName("IssuingAuthorityISO")).getText());
			//customerDocuments.setCustDocExpDate(DateUtility.getUtilDate(custDocDetailElement.getFirstChildWithName(new QName("IDIssueDate")).getText(),"dd-MMM-yyyy"));
			//customerDocuments.setCustDocIssuedOn(DateUtility.getUtilDate(custDocDetailElement.getFirstChildWithName(new QName("IDExpiryDate")).getText(),"dd-MMM-yyyy"));
		}

		LOG.debug("Leaving setCustomerdocuments()");

		return docList;
	}

	private List<InterfaceCustomerDocument> setCustDocumentsByType(OMElement custDocDetailElement,OMElement requestData, String docType, String idType) throws Exception {
		
		LOG.debug("Entering setCustDocumentsByType()");
		
		requestPath="/HB_EAI_REQUEST/Request/createCIFRetailRequest/DocumentDetails";
		
		if(requestData==null){
		
		return null;
		}
		
		List<InterfaceCustomerDocument> docList = new ArrayList<InterfaceCustomerDocument>();
		InterfaceCustomerDocument customerDocuments = new InterfaceCustomerDocument();
		String docID = null;
		String exp="Exp"; 
		String doc="";
		doc=docType;
		AXIOMXPath xpath = new AXIOMXPath(requestPath);
		if(xpath != null){
			@SuppressWarnings("unchecked")
			List<OMElement> custDocList = (List<OMElement>) xpath.selectNodes(requestData);
			for (OMElement omElement : custDocList) {
				if (idType.equalsIgnoreCase("EMIRATE")) {
					docID = docType + "No";
					idType="01";
				}else if(idType.equalsIgnoreCase("Passport")) {
					docID = docType + "Number";
					idType="03";
				}
				else if (idType.equalsIgnoreCase("RESVISA"))
				{
					docID = docType + "No";
					idType="08";

				}
				customerDocuments.setCustDocType(idType);
				customerDocuments.setCustDocTitle(PFFXmlUtil.getStringValue(omElement,false, false,docID,requestPath+"/"));		
				customerDocuments.setCustDocIssuedOn(PFFXmlUtil.getDateValue(omElement, false, false, docType+"IssueDate", requestPath+"/"));
				if(docType.equalsIgnoreCase("EMIRATESID")){
					doc="UaeID";
				}
				if(docType.equalsIgnoreCase("RESIDENCEVISA")){
					exp="Expiry";
				}
				customerDocuments.setCustDocExpDate(PFFXmlUtil.getDateValue(omElement, false, false, doc+exp+"Date", requestPath+"/"));	

				docList.add(customerDocuments);
			}
		}
		LOG.debug("Leaving setCustDocumentsByType()");

		return docList;
	}
	/**
	 * 
	 * @param custDetailElement
	 * @param custType
	 * @return
	 * @throws Exception 
	 */
	private InterfaceCustomer setCustomerDetails(OMElement custDetailElement,OMElement requestData, int custType) throws Exception {
		
		LOG.debug("Entering setCustomerDetails()");
		
		requestPath="/HB_EAI_REQUEST/Request/createCIFRetailRequest/PersonalInfo/";
		if (custDetailElement == null) {
			return null;
		}
		InterfaceCustomer customer = new InterfaceCustomer();
		if(custType == 1) {

			customer.setCustSts(PFFXmlUtil.getStringValue(requestData,true, true,"CustomerStatus",requestPath));
			customer.setCustSalutationCode(PFFXmlUtil.getStringValue(requestData,true, true,"Title",requestPath));
			customer.setCustShrtName((custDetailElement.getFirstChildWithName(new QName("ShortName")).getText()));
			customer.setCustCoreBank(PFFXmlUtil.getStringValue(requestData,true, true,"Mnemonic",requestPath)); 
			customer.setCustMotherMaiden(PFFXmlUtil.getStringValue(requestData,true, true,"MotherName",requestPath));
			customer.setCustFName(PFFXmlUtil.getStringValue(requestData,true, true,"FirstName",requestPath));
			customer.setCustLName(PFFXmlUtil.getStringValue(requestData,true, true,"FamilyName",requestPath));
			customer.setCustDOB(PFFXmlUtil.getDateValue(requestData,true, true,"DateOfBirth",requestPath));
			customer.setCustPOB(PFFXmlUtil.getStringValue(requestData,false, false,"PlaceOfBirth",requestPath));
			customer.setCustLng(PFFXmlUtil.getStringValue(requestData,true, true,"LanguageCode",requestPath));
			customer.setCustSector(PFFXmlUtil.getStringValue(requestData,true, true,"SectorCode",requestPath));
			customer.setCustIndustry(custDetailElement.getFirstChildWithName(new QName("IndustryCode")).getText());
			customer.setCustSegment(PFFXmlUtil.getStringValue(requestData,true, true,"Segment",requestPath));
			customer.setCustGenderCode(PFFXmlUtil.getStringValue(requestData,true, true,"Gender",requestPath));
			customer.setCustDftBranch(PFFXmlUtil.getStringValue(requestData,true, true,"BranchCode",requestPath));
			customer.setCustNationality(PFFXmlUtil.getStringValue(requestData,true, true,"Nationality",requestPath));
			customer.setCustCOB(PFFXmlUtil.getStringValue(requestData,true, true,"CountryOfbirth",requestPath));
			customer.setCustMaritalSts(PFFXmlUtil.getStringValue(requestData,true, true,"MaritalStatus",requestPath));
			customer.setNoOfDependents(PFFXmlUtil.getIntValue(requestData,false, false,"NoOfDependents",requestPath,false,0));
			//(custDetailElement.getFirstChildWithName(new QName("CustomerStatusISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("TitleISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("FullName")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("LanguageISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("SectorISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("IndustryISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("SegmentISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("ResidencyType")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("ResidencyTypeISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("FatherName")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("GenderISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("NationalityISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("DualNationality")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("DualNationalityISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("CountryOfbirthISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("MaritalStatusISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("DependentsList")).getText());//TODO: List
			//(custDetailElement.getFirstChildWithName(new QName("YearsInUAE")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("RelationshipDate")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("RelationshipManager")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("RelatedParty")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("Introducer")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("BranchCodeISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("lineManager")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("AccountOfficer")).getText());
			customer.setCustCtgCode("Retail");

		} else {

			//(custDetailElement.getFirstChildWithName(new QName("NameOfEstablishment")).getText());
			customer.setCustShrtName(custDetailElement.getFirstChildWithName(new QName("EstablishmentShortName")).getText());
			customer.setCustSts(custDetailElement.getFirstChildWithName(new QName("Mnemonic")).getText());
			customer.setCustTypeCode(custDetailElement.getFirstChildWithName(new QName("TypeOfEstablishment")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("TypeOfEstablishmentISO")).getText());
			customer.setCustIndustry(custDetailElement.getFirstChildWithName(new QName("IndustryCode")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("IndustryISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("Target")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("TargetISO")).getText());
			customer.setCustSts(custDetailElement.getFirstChildWithName(new QName("CustStatus")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("CustStatusISO")).getText());
			customer.setCustLng(custDetailElement.getFirstChildWithName(new QName("LanguageCode")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("LanguageISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("IncorporationType")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("IncorporationTypeISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("CountryOfIncorporation")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("CountryOfIncorporationISO")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("DateOfIncorporation")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("ParentCoCIF")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("Auditor")).getText());
			//(custDetailElement.getFirstChildWithName(new QName("UseChequeBook")).getText());
			customer.setCustCtgCode("SME");
		}
		LOG.debug("Leaving setCustomerDetails()");

		return customer;
	}

	/**
	 * Get Customer PhoneNumbers based on the type
	 * @param addrElement
	 * @param custType
	 * @return
	 * @throws Exception 
	 */
	private List<InterfaceCustomerPhoneNumber> setCustomerPhoneNumber(OMElement addrElement, OMElement requestData, int custType) throws Exception {
		
		LOG.debug("Entering setCustomerPhoneNumber()");
		
		if (addrElement == null) {
			return null;
		}

		List<InterfaceCustomerPhoneNumber> phoneList = new ArrayList<InterfaceCustomerPhoneNumber>();

		if(custType == 1) {

			String path = "/HB_EAI_REQUEST/Request/createCIFRetailRequest/AddressInfo/";
			OMElement ofceAddElement = addrElement.getFirstChildWithName(new QName("OfficeAddress"));
			OMElement resAddElement = addrElement.getFirstChildWithName(new QName("ResidenceAddress"));
			OMElement hcAddElement = addrElement.getFirstChildWithName(new QName("HomeCountryAddress"));

			try {
				if(ofceAddElement != null) {
					String parentTag = "OfficeAddress/";
					phoneList.addAll(setCustomerPhoneNumberByType(ofceAddElement, requestData,
							path+parentTag+"OfficePhoneNumbers/OfficePhoneNo",PFFXmlUtil.OFFICEPHONENO,true));
					phoneList.addAll(setCustomerPhoneNumberByType(ofceAddElement,  requestData,
							path+parentTag+"OfficeFaxNumbers/OfficeFaxNo",PFFXmlUtil.OFFICEFAXNO,false));
					phoneList.addAll(setCustomerPhoneNumberByType(ofceAddElement,  requestData,
							path+parentTag+"OfficeMobileNumbers/OfficeMobileNo",PFFXmlUtil.OFFICEMOBILENO,true));
				}

				if(resAddElement != null) {
					String parentTag = "ResidenceAddress/";
					phoneList.addAll(setCustomerPhoneNumberByType(resAddElement, requestData, 
							path+parentTag+"ResidencePhoneNumbers/ResidencePhoneNo",PFFXmlUtil.RESIDENCEPHONENO,false));
					phoneList.addAll(setCustomerPhoneNumberByType(resAddElement,  requestData,
							path+parentTag+"ResidenceFaxNumbers/ResidenceFaxNo",PFFXmlUtil.RESIDENCEFAXNO,false));
					phoneList.addAll(setCustomerPhoneNumberByType(resAddElement,  requestData,
							path+parentTag+"ResidenceMobileNumbers/ResidenceMobileNo",PFFXmlUtil.RESIDENCEMOBILENO,true));
				}

				if(hcAddElement != null) {
					String parentTag = "HomeCountryAddress/";
					phoneList.addAll(setCustomerPhoneNumberByType(hcAddElement,  requestData,
							path+parentTag+"HCPhoneNumbers/HCPhoneNo",PFFXmlUtil.HCPHONENO,false));
					phoneList.addAll(setCustomerPhoneNumberByType(hcAddElement,  requestData,
							path+parentTag+"HCFaxNumbers/HCFaxNo",PFFXmlUtil.HCFAXNO,false));
					phoneList.addAll(setCustomerPhoneNumberByType(hcAddElement,  requestData,
							path+parentTag+"HCMobileNumbers/HCMobileNo",PFFXmlUtil.HCMOBILENO,false));
				}
			} catch (JaxenException je) {
				
				LOG.error("setCustomerPhoneNumber()-->Retail"+je.getMessage());
			}

		} else {

			String path = "/HB_EAI_REQUEST/Request/createCIFRetailRequestSME/AddressInfo/";

			OMElement estMainAddElement = addrElement.getFirstChildWithName(new QName("EstMainAddress"));
			OMElement estOtherAddElement = addrElement.getFirstChildWithName(new QName("EstOtherAddress"));

			try {
				if(estMainAddElement != null) {
					String parentTag = "EstMainAddress/";
					phoneList.addAll(setCustomerPhoneNumberByType(estMainAddElement, requestData,
							path+parentTag+"EstPhoneNumbers/EstPhoneNo",PFFXmlUtil.ESTPHONENO,false));
					phoneList.addAll(setCustomerPhoneNumberByType(estMainAddElement, requestData,
							path+parentTag+"EstFaxNumbers/EstFaxNo",PFFXmlUtil.ESTFAXNO,false));
					phoneList.addAll(setCustomerPhoneNumberByType(estMainAddElement, requestData,
							path+parentTag+"EstMobileNumbers/EstMobileNo",PFFXmlUtil.ESTMOBILENO,false));
				}

				if(estOtherAddElement != null) {
					String parentTag = "EstOtherAddress/";
					phoneList.addAll(setCustomerPhoneNumberByType(estOtherAddElement, requestData,
							path+parentTag+"EstOtherPhoneNumbers/EstOtherPhoneNo",PFFXmlUtil.ESTOTHERPHONENO,false));
					phoneList.addAll(setCustomerPhoneNumberByType(estOtherAddElement, requestData,
							path+parentTag+"EstOtherFaxNumbers/EstOtherFaxNo",PFFXmlUtil.ESTOTHERFAXNO,false));
					phoneList.addAll(setCustomerPhoneNumberByType(estOtherAddElement, requestData,
							path+parentTag+"EstOtherMobileNumbers/EstOtherMobileNo",PFFXmlUtil.ESTOTHERMOBILENO,false));
				}
			} catch (JaxenException je) {
				LOG.error("setCustomerPhoneNumber()-->SME" + je.getMessage());
			}
		}
		
		LOG.debug("Leaving setCustomerPhoneNumber()");

		return phoneList;
	}


}
