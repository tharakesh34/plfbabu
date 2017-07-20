package com.pennant.mq.processutil;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.customer.InterfaceCoreCustomer;
import com.pennant.coreinterface.model.customer.InterfaceCustomerAddress;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDocument;
import com.pennant.coreinterface.model.customer.InterfaceCustomerEMail;
import com.pennant.coreinterface.model.customer.InterfaceCustomerPhoneNumber;
import com.pennant.equation.util.DateUtility;
import com.pennant.mq.dao.MQInterfaceDAO;
import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.MessageQueueClient;
import com.pennanttech.pennapps.core.InterfaceException;

public class AddNewCustomerProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(AddNewCustomerProcess.class);
	String preferedMailId = "";
	String createCIFError = "";

	public AddNewCustomerProcess() {
		super();
	}
	private MQInterfaceDAO mqInterfaceDAO;
	
	/**
	 * Process the CreateNewCustomer Request and send Response
	 * 
	 * @param customerDetail
	 * @param msgFormat
	 * @return String
	 * @throws Exception 
	 */
	public String createNewCustomer(InterfaceCustomerDetail customerDetail, String msgFormat) throws InterfaceException {
		logger.debug("Entering");

		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		
		String returnCode = createOrUpdateCustomer(customerDetail, client, factory, msgFormat);
		if(StringUtils.equals(returnCode, PFFXmlUtil.SUCCESS)) {
			return returnCode;
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * 
	 * @param customerDetail
	 * @return
	 * @throws InterfaceException
	 */
	public void updateCustomer(InterfaceCustomerDetail customerDetail, String msgFormat) throws InterfaceException {
		logger.debug("Entering");

		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());

		createOrUpdateCustomer(customerDetail, client, factory, msgFormat);

		logger.debug("Leaving");

	}

	private String createOrUpdateCustomer(InterfaceCustomerDetail customerDetail, MessageQueueClient client,
			OMFactory factory, String msgFormat) throws InterfaceException {
		logger.debug("Entering");

		OMElement request = null;
		OMElement response = null;
		AHBMQHeader header = null;

		if (customerDetail != null) {
			header = new AHBMQHeader(msgFormat);
			try {
				
				OMElement requestElement = getCreateRequestElement(customerDetail, factory, msgFormat);

				request = PFFXmlUtil.generateRequest(header, factory, requestElement);
				response = client.getRequestResponse(request.toString(), getRequestQueue(), getResponseQueue(), getWaitTime());
			} catch (Exception e) {
				logger.error("Exception: ", e);
				throw new InterfaceException("PTI3003",e.getMessage());
			}

			logger.debug("Leaving");
			String rootPath = "createCIFRetailT24Reply";
			if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.UPDATE_CUST_RETAIL)) {
				rootPath = "updateCIFRetailT24Reply";
			}
			return setCustResponseDetails(response, header,	rootPath);
		}
		return null;
	}

	/**
	 * Prepare Create Customer Request Element to send Interface through MQ
	 * @param customerDetail
	 * @param referenceNum
	 * @param factory
	 * @return
	 * @throws InterfaceException 
	 */
	private OMElement getCreateRequestElement(InterfaceCustomerDetail customerDetail,
			OMFactory factory, String msgFormat) throws InterfaceException {
		logger.debug("Entering");
		String newRefNumber = PFFXmlUtil.getReferenceNumber();
 		int custType = 2;
		OMElement createCIFRequest = null;
		OMElement request = null;

		if (StringUtils.equalsIgnoreCase(InterfaceMasterConfigUtil.CUST_RETAIL,
				customerDetail.getCustomer().getCustCtgCode())) {
			custType = 1;
		}
		
		request = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));

		switch (custType) {
		case 1:
			//Retail
			createCIFRequest = factory.createOMElement("createCIFRetailRequest",null);
			PFFXmlUtil.setOMChildElement(factory, createCIFRequest, "ReferenceNum",newRefNumber);
			PFFXmlUtil.setOMChildElement(factory, createCIFRequest, "CIF",customerDetail.getCustomer().getCustCIF());
			
			if(StringUtils.equals(msgFormat, InterfaceMasterConfigUtil.UPDATE_CUST_RETAIL)) {
				createCIFRequest = factory.createOMElement("updateCIFRetailRequest",null);
			}
			
			createCIFRequest.addChild(generatePersonalInfo(customerDetail, factory));
			createCIFRequest.addChild(generateDocDetails(customerDetail, factory));
			createCIFRequest.addChild(generateEmploymentInfo(customerDetail, factory));

			break;
		case 2:
			//SME
			createCIFRequest = factory.createOMElement("createCIFSMERequest",null);
			PFFXmlUtil.setOMChildElement(factory, createCIFRequest, "ReferenceNum",newRefNumber);
			PFFXmlUtil.setOMChildElement(factory, createCIFRequest, "CIF",customerDetail.getCustomer().getCustCIF());
			createCIFRequest.addChild(generateSMECustDetails(customerDetail, factory));
			createCIFRequest.addChild(generateSMEDocDetails(customerDetail, factory));
			createCIFRequest.addChild(generateFinancialInformation(customerDetail, factory));

			break;

		default:
			break;
		}
		request.addChild(createCIFRequest);

		createCIFRequest.addChild(generateAddressInfo(customerDetail, factory));

		//SMS Service
		createCIFRequest.addChild(generateSMSService(customerDetail, factory));

		//Power Of Atorny
		//createCIFRequest.addChild(generatePowerOfAttorney(customerDetail, factory));

		//Rating:Common
		//createCIFRequest.addChild(generateRating(customerDetail, factory));

		//Indemity:Common
		//createCIFRequest.addChild(generateIndemity(customerDetail, factory));

		//RelationDetails
		//createCIFRequest.addChild(generateRelationDetails(customerDetail, factory));

		PFFXmlUtil.setOMChildElement(factory, createCIFRequest, "IssueCheque","NO");

		//KYC Details
		createCIFRequest.addChild(generateKYCDetails(customerDetail, factory));

		logger.debug("Leaving");

		return request;
	}

	/**
	 * Generate SMS Service Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 */
	private OMNode generateSMSService(InterfaceCustomerDetail customerDetail, OMFactory factory) {
		logger.debug("Entering");

		OMElement smsService = factory.createOMElement("SmsServices", null);

		for (InterfaceCustomerPhoneNumber phoneNum : customerDetail.getCustomerPhoneNumList()) {
			String smsMobileNum = phoneNum.getPhoneTypeCode();
			if (StringUtils.equals(InterfaceMasterConfigUtil.RESIDENCEMOBILENO, smsMobileNum)) {
				smsService.addChild(generatePhoneTypes("SmsMobileNo", factory, phoneNum));
			}
		}

		logger.debug("Leaving");

		return smsService;
	}

	/**
	 * Generate KYC Detail Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 */
	private OMNode generateKYCDetails(InterfaceCustomerDetail customerDetail, OMFactory factory) {
		logger.debug("Entering");

		OMElement kycDetails = factory.createOMElement("KYC",null);

		InterfaceCoreCustomer coreCustomer = customerDetail.getInterfaceCoreCustomer();
		if(coreCustomer != null) {
			PFFXmlUtil.setOMChildElement(factory, kycDetails, "SourceOfIncome",StringUtils.trimToEmpty("Inheritance"));
			PFFXmlUtil.setOMChildElement(factory, kycDetails, "SourceOfIncomeISO",StringUtils.trimToEmpty("100001"));
		}
		logger.debug("Leaving");

		return kycDetails;
	}

	/**
	 * Generate Indemity Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 */
	@SuppressWarnings("unused")
	private OMNode generateIndemity(InterfaceCustomerDetail customerDetail,	OMFactory factory) {
		logger.debug("Entering");

		OMElement indemity = factory.createOMElement("Indemity", null);
		PFFXmlUtil.setOMChildElement(factory, indemity, "FaxIndemity", "");
		PFFXmlUtil.setOMChildElement(factory, indemity, "FaxNumber", "");

		for (InterfaceCustomerPhoneNumber phoneNum : customerDetail.getCustomerPhoneNumList()) {
			String indemityFaxType = phoneNum.getPhoneTypeCode();
			if (StringUtils.equals(InterfaceMasterConfigUtil.FAXINDEMITY, indemityFaxType)) {
				indemity.addChild(generatePhoneTypes("FaxNumber", factory, phoneNum));
			}
		}

		logger.debug("Leaving");

		return indemity;
	}

	/**
	 * Generate AddressInfo Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 * @throws InterfaceException 
	 */
	private OMNode generateAddressInfo(InterfaceCustomerDetail customerDetail, OMFactory factory) throws InterfaceException {
		logger.debug("Entering");

		OMElement addressInfo = factory.createOMElement("AddressInfo",null);

		addressInfo.addChild(getAddress(addressInfo,customerDetail,factory));
		
		PFFXmlUtil.setOMChildElement(factory, addressInfo, "PreferredMailingAddress", preferedMailId);
		logger.debug("Leaving");

		return addressInfo;
	}

	private OMNode getAddress(OMElement addressInfo, InterfaceCustomerDetail custDetail, OMFactory factory) throws InterfaceException {
		logger.debug("Entering");

		String type = "";

		for (InterfaceCustomerAddress custAddr : custDetail.getAddressList()) {

			String addrType = custAddr.getCustAddrType();
			String tag = "";
			if (StringUtils.equalsIgnoreCase("OFFICE",addrType)) {

				type = "Office";
				tag="OfficeAddress";

			} else if (StringUtils.equalsIgnoreCase("HOME_RC", addrType)) {

				type = "Residence";
				tag="ResidenceAddress";

			} else if (StringUtils.equalsIgnoreCase("HOME_PC", addrType)) {

				type = "HC";
				tag="HomeCountryAddress";

			} else if (StringUtils.equalsIgnoreCase("EstMain",addrType)) {

				type = "Est";
				tag="EstMainAddress";

			} else if (StringUtils.equalsIgnoreCase("EstOther",addrType)) {

				type = "EstOther";
				tag="EstOtherAddress";
			}
			if(!StringUtils.isBlank(tag)) {
				OMElement addTypeInfo = factory.createOMElement(tag, null);
				OMElement addrElement = setAddressInfoRequest(custDetail, custAddr, addTypeInfo, factory, type);
				addTypeInfo.addChild(addrElement);
				addressInfo.addChild(addTypeInfo);
			}


		}
		return addressInfo;
	}

	private OMElement setAddressInfoRequest(InterfaceCustomerDetail custDetail, InterfaceCustomerAddress custAddr, 
			OMElement addressInfo, OMFactory factory, String type) throws InterfaceException {
		logger.debug("Entering");

		PFFXmlUtil.setOMChildElement(factory, addressInfo, type+"POBox",custAddr.getCustPOBox());
		PFFXmlUtil.setOMChildElement(factory, addressInfo, type+"FlatNo",custAddr.getCustFlatNbr());
		PFFXmlUtil.setOMChildElement(factory, addressInfo, type+"BuildingName",custAddr.getCustAddrHNbr());
		PFFXmlUtil.setOMChildElement(factory, addressInfo, type+"StreetName",custAddr.getCustAddrStreet());
		PFFXmlUtil.setOMChildElement(factory, addressInfo, type+"NearstLandmark",custAddr.getCustAddrLine1());
		PFFXmlUtil.setOMChildElement(factory, addressInfo, type+"Emirate",custAddr.getLovDescCustAddrCityName());
		//PFFXmlUtil.setOMChildElement(factory, addressInfo, type+"EmirateISO",custAddr.getCustAddrCity());
		PFFXmlUtil.setOMChildElement(factory, addressInfo, type+"Country",custAddr.getCustAddrCountry());
		PFFXmlUtil.setOMChildElement(factory, addressInfo, type+"CountryISO",
				getMqInterfaceDAO().getPFFCode(custAddr.getCustAddrCountry(), "MCM_BMTCOUNTRIES"));

		for (InterfaceCustomerPhoneNumber phoneNum : custDetail.getCustomerPhoneNumList()) {

			int phoneTypeFlag = 0;
			String phoneType = phoneNum.getPhoneTypeCode();
			
			switch (type) {
			case "Office":
				if (StringUtils.equals(InterfaceMasterConfigUtil.OFFICEPHONENO, phoneType)) {

					phoneTypeFlag = 1;

				} else if (StringUtils.equals(InterfaceMasterConfigUtil.OFFICEFAXNO, phoneType)) {

					phoneTypeFlag = 2;

				} else if (StringUtils.equals(InterfaceMasterConfigUtil.OFFICEMOBILENO, phoneType)) {

					phoneTypeFlag = 3;
				}	
				break;
			case "Residence":
				if (StringUtils.equals(InterfaceMasterConfigUtil.RESIDENCEPHONENO, phoneType)) {

					phoneTypeFlag = 1;

				} else if (StringUtils.equals(InterfaceMasterConfigUtil.RESIDENCEFAXNO, phoneType)) {

					phoneTypeFlag = 2;

				} else if (StringUtils.equals(InterfaceMasterConfigUtil.RESIDENCEMOBILENO, phoneType)) {

					phoneTypeFlag = 3;

				}
				break;
			case "HC":
				if (StringUtils.equals(InterfaceMasterConfigUtil.HCPHONENO, phoneType)) {

					phoneTypeFlag = 1;

				} else if (StringUtils.equals(InterfaceMasterConfigUtil.HCFAXNO, phoneType)) {

					phoneTypeFlag = 2;

				} else if (StringUtils.equals(InterfaceMasterConfigUtil.HCMOBILENO, phoneType)) {

					phoneTypeFlag = 3;

				}
				break;
			case "Est":

				break;
			case "EstOther":

				break;

			default:
				break;
			}

			switch (phoneTypeFlag) {
			case 1:

				// Phone Numbers
				OMElement phoneNumbers = factory.createOMElement(type + "PhoneNumbers", null);
				phoneNumbers.addChild(generatePhoneTypes(type + "PhoneNo", factory, phoneNum));
				addressInfo.addChild(phoneNumbers);

				break;
			case 2:

				// FAX Numbers
				OMElement faxNumbers = factory.createOMElement(type	+ "FaxNumbers", null);
				faxNumbers.addChild(generatePhoneTypes(type + "FaxNo", factory,	phoneNum));
				addressInfo.addChild(faxNumbers);

				break;
			case 3:

				// Mobile Numbers
				OMElement mobileNumbers = factory.createOMElement(type + "MobileNumbers", null);
				mobileNumbers.addChild(generatePhoneTypes(type + "MobileNo", factory, phoneNum));
				addressInfo.addChild(mobileNumbers);

				break;

			default:
				break;
			}
		}
		
		OMElement email = factory.createOMElement(type+"EmailAddresses",null);
		
		for (InterfaceCustomerEMail custEmail : custDetail.getCustomerEMailList()) {
			String emailType = custEmail.getCustEMailTypeCode();
			switch (type) {
			case "Office":
				preferedMailId = custEmail.getCustEMail();
				if (StringUtils.equals(InterfaceMasterConfigUtil.OFFICEEMAILADDRESS, emailType)) {
					PFFXmlUtil.setOMChildElement(factory, email, type+"EmailAddress",custEmail.getCustEMail());
				}
				break;
			case "Residence":
				preferedMailId = custEmail.getCustEMail();
				if (StringUtils.equals(InterfaceMasterConfigUtil.RESIDENCEEMAILADDRESS, emailType)) {
					//PFFXmlUtil.setOMChildElement(factory, email, type+"EmailAddress",custEmail.getCustEMail());
				}
				break;
			default:
				break;
			}
		}

		addressInfo.addChild(email);

		logger.debug("Leaving");

		return addressInfo;

	}

	private OMNode generatePhoneTypes(String tagName, OMFactory factory, InterfaceCustomerPhoneNumber custPhoneData) {

		OMElement element = factory.createOMElement(tagName,null);

		PFFXmlUtil.setOMChildElement(factory, element, "CountryCode",custPhoneData.getPhoneCountryCode());
		PFFXmlUtil.setOMChildElement(factory, element, "AreaCode",custPhoneData.getPhoneAreaCode());
		PFFXmlUtil.setOMChildElement(factory, element, "SubsidiaryNumber",custPhoneData.getPhoneNumber());

		return element;
	}

	/**
	 * Generate RelationDetails Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 */
	@SuppressWarnings("unused")
	private OMNode generateRelationDetails(InterfaceCustomerDetail customerDetail, OMFactory factory) {
		logger.debug("Entering");

		OMElement relationDetails = factory.createOMElement("RelationDetails",null);
		InterfaceCoreCustomer coreCustomer = customerDetail.getInterfaceCoreCustomer();
		if(coreCustomer != null) {
			PFFXmlUtil.setOMChildElement(factory, relationDetails, "RelationCode",coreCustomer.getRelationCode());
			PFFXmlUtil.setOMChildElement(factory, relationDetails, "RelationCodeISO",coreCustomer.getRelationCode());
			PFFXmlUtil.setOMChildElement(factory, relationDetails, "RelationShipCIF",coreCustomer.getRelationShipCIF());
		}
		logger.debug("Leaving");

		return relationDetails;
	}

	/**
	 * Generate Rating Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 */
	@SuppressWarnings("unused")
	private OMNode generateRating(InterfaceCustomerDetail customerDetail, OMFactory factory) {
		logger.debug("Entering");

		OMElement rating = factory.createOMElement("Rating",null);
		InterfaceCoreCustomer coreCustomer = customerDetail.getInterfaceCoreCustomer();
		if(coreCustomer != null) {
			PFFXmlUtil.setOMChildElement(factory, rating, "InternalRating",coreCustomer.getInternalRating());
			PFFXmlUtil.setOMChildElement(factory, rating, "DateOfInternalRating",DateUtility.formateDate(
					coreCustomer.getDateOfInternalRating(), InterfaceMasterConfigUtil.MQDATE));
		}
		logger.debug("Leaving");

		return rating;
	}

	/**
	 * Generate PowerOfAtorny Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 */
	@SuppressWarnings("unused")
	private OMNode generatePowerOfAttorney(InterfaceCustomerDetail customerDetail, OMFactory factory) {
		logger.debug("Entering");

		OMElement powerOfAttorney = factory.createOMElement("PowerOfAttorney",null);

		InterfaceCoreCustomer coreCustomer = customerDetail.getInterfaceCoreCustomer();
		if(coreCustomer != null) {
			PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAFlag",coreCustomer.getpOAFlag());
			PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POACIF",coreCustomer.getpOACIF());
			PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAHolderName",coreCustomer.getpOAHoldersname());
			PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAPassportNumber",coreCustomer.getPassportNumber());
			PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAIDNumber",coreCustomer.getEmiratesIDNumber());
			PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POANationality",coreCustomer.getNationality());
			PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAIssuanceDate",DateUtility.formateDate(
					coreCustomer.getpOAIssuancedate(), InterfaceMasterConfigUtil.MQDATE));
			PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAExpiryDate",DateUtility.formateDate(
					coreCustomer.getpOAExpirydate(), InterfaceMasterConfigUtil.MQDATE));
			PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POApassportExpiryDate",DateUtility.formateDate(
					coreCustomer.getPassportExpiryDate(), InterfaceMasterConfigUtil.MQDATE));
			PFFXmlUtil.setOMChildElement(factory, powerOfAttorney, "POAIDExpiryDate",DateUtility.formateDate(
					coreCustomer.getEmiratesIDExpiryDate(), InterfaceMasterConfigUtil.MQDATE));
		}

		logger.debug("Leaving");

		return powerOfAttorney;
	}

	/**
	 * Generate EmployementInfo Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 */
	private OMNode generateEmploymentInfo(InterfaceCustomerDetail customerDetail, OMFactory factory) {
		logger.debug("Entering");

		OMElement employmentInfo = factory.createOMElement("EmploymentInfo",null);
		
		PFFXmlUtil.setOMChildElement(factory, employmentInfo, "EmpStatus","EMPLOYED");
		PFFXmlUtil.setOMChildElement(factory, employmentInfo, "EmpName",customerDetail.getCustEmployeeDetail().getEmpName());
		PFFXmlUtil.setOMChildElement(factory, employmentInfo, "Occupation",customerDetail.getCustEmployeeDetail().getEmpSector());
		PFFXmlUtil.setOMChildElement(factory, employmentInfo, "Department",customerDetail.getCustEmployeeDetail().getLovDescEmpDept());
		PFFXmlUtil.setOMChildElement(factory, employmentInfo, "EmpStartDate",DateUtility.formateDate(
				customerDetail.getCustEmployeeDetail().getEmpFrom(),InterfaceMasterConfigUtil.MQDATE));
		PFFXmlUtil.setOMChildElement(factory, employmentInfo, "Salary",customerDetail.getCustEmployeeDetail().getMonthlyIncome());
		
		InterfaceCoreCustomer coreCustomer = customerDetail.getInterfaceCoreCustomer();
		if(coreCustomer != null) {
			PFFXmlUtil.setOMChildElement(factory, employmentInfo, "SalaryCurrency",StringUtils.trimToEmpty(customerDetail.getCustomer().getCustBaseCcy()));
			PFFXmlUtil.setOMChildElement(factory, employmentInfo, "SalaryDateFreq",StringUtils.trimToEmpty(DateUtility.formateDate(
					coreCustomer.getSalaryDateFreq(), InterfaceMasterConfigUtil.MQDATE)));
			PFFXmlUtil.setOMChildElement(factory, employmentInfo, "BusinessType",StringUtils.trimToEmpty(coreCustomer.getBusinessType()));
			PFFXmlUtil.setOMChildElement(factory, employmentInfo, "NameOfBusiness",StringUtils.trimToEmpty(coreCustomer.getNameOfBusiness()));
		}
		

		logger.debug("Leaving");

		return employmentInfo;
	}

	/**
	 * Generate Retail DocumentDetails Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 */
	private OMNode generateDocDetails(InterfaceCustomerDetail customerDetail, OMFactory factory) {
		logger.debug("Entering");

		OMElement documentDetails = factory.createOMElement("DocumentDetails",null);
		if(customerDetail.getCustomerDocumentsList() != null) {
			for(InterfaceCustomerDocument custDoc : customerDetail.getCustomerDocumentsList()) {
				String tag1 = null;
				boolean isDocRequired = false;
				switch (custDoc.getCustDocCategory()) {
				case InterfaceMasterConfigUtil.DOCTYPE_EMIRATE:
					tag1 = "EmiratesID";
					PFFXmlUtil.setOMChildElement(factory, documentDetails, tag1+"Name",custDoc.getCustDocName());
					isDocRequired = true;
					break;
				case InterfaceMasterConfigUtil.DOCTYPE_PASSPORT:
					tag1 = "Passport";
					isDocRequired = true;
					break;
				case InterfaceMasterConfigUtil.DOCTYPE_ADDRESS:
					tag1 = "ResidenceVisa";
					isDocRequired = true;
					break;
				case InterfaceMasterConfigUtil.DOCTYPE_USID:
					tag1 = "USID";
					isDocRequired = true;
					break;
				case InterfaceMasterConfigUtil.DOCTYPE_TRADELICENCE:
					tag1 = "TradeLicense";
					isDocRequired = true;
					break;

				default:
					break;
				}
				if(isDocRequired) {
					String tag2 = "Number";
					if(StringUtils.equalsIgnoreCase(tag1,"EmiratesID") || StringUtils.equalsIgnoreCase(tag1,"ResidenceVisa")
							|| StringUtils.equalsIgnoreCase(tag1,"USID")) {
						tag2="No";
					}
					PFFXmlUtil.setOMChildElement(factory, documentDetails, tag1+tag2,custDoc.getCustDocTitle());
					PFFXmlUtil.setOMChildElement(factory, documentDetails, tag1+"IssueDate",DateUtility.formateDate(
							custDoc.getCustDocIssuedOn(),InterfaceMasterConfigUtil.MQDATE));
					if(StringUtils.equalsIgnoreCase(tag1, "EmiratesID")) {
						tag1 = "UaeID";
					}
					if(StringUtils.equalsIgnoreCase("USID", tag1)) {
						PFFXmlUtil.setOMChildElement(factory, documentDetails, tag1+"Type","Social Security");
						PFFXmlUtil.setOMChildElement(factory, documentDetails, tag1+tag2,custDoc.getCustDocTitle());
						PFFXmlUtil.setOMChildElement(factory, documentDetails, "USPerson","No");
						PFFXmlUtil.setOMChildElement(factory, documentDetails, "WToProvideUSInfo","No");
						
					}
					PFFXmlUtil.setOMChildElement(factory, documentDetails, tag1+"ExpDate",DateUtility.formateDate(
							custDoc.getCustDocExpDate(),InterfaceMasterConfigUtil.MQDATE));
				}
			}	
		}
		logger.debug("Leaving");

		return documentDetails;
	}

	/**
	 * Generate PersonalInfo Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 * @throws InterfaceException 
	 */
	private OMNode generatePersonalInfo(InterfaceCustomerDetail customerDetail, OMFactory factory) throws InterfaceException {
		logger.debug("Entering");

		OMElement personalInfo = factory.createOMElement("PersonalInfo",null);
		customerDetail.getCustomer().setCustSts("39");
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "CustomerStatus",customerDetail.getCustomer().getCustSts());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "CustomerStatusISO",
				getMqInterfaceDAO().getPFFCode(customerDetail.getCustomer().getCustSts(),"MCM_RMTCustTypes"));
		customerDetail.getCustomer().setCustSalutationCode("MR");
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "Title",customerDetail.getCustomer().getCustSalutationCode());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "TitleISO",
				getMqInterfaceDAO().getPFFCode(customerDetail.getCustomer().getCustSalutationCode(),"MCM_BMTSALUTATIONS"));
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "FullName",
				customerDetail.getCustomer().getCustFName()+" "+customerDetail.getCustomer().getCustLName());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "ShortName",customerDetail.getCustomer().getCustShrtName());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "Mnemonic","R"+customerDetail.getCustomer().getCustCIF());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "MotherName",customerDetail.getCustomer().getCustMotherMaiden());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "FirstName",customerDetail.getCustomer().getCustFName());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "FamilyName",customerDetail.getCustomer().getCustLName());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "SecondName",customerDetail.getCustomer().getCustMName());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "DateOfBirth",DateUtility.formateDate(
				customerDetail.getCustomer().getCustDOB(), InterfaceMasterConfigUtil.MQDATE));
		//PFFXmlUtil.setOMChildElement(factory, personalInfo, "PlaceOfBirth",customerDetail.getCustomer().getCustPOB());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "Language","2");
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "LanguageISO",
				getMqInterfaceDAO().getPFFCode(customerDetail.getCustomer().getCustLng(),"MCM_BMTLANGUAGE"));
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "Sector",customerDetail.getCustomer().getCustSector());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "SectorISO",
				getMqInterfaceDAO().getPFFCode(customerDetail.getCustomer().getCustSector(),"MCM_BMTSECTORS"));
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "Industry",customerDetail.getCustomer().getCustIndustry());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "IndustryISO",
				getMqInterfaceDAO().getPFFCode(customerDetail.getCustomer().getCustIndustry(), "MCM_BMTINDUSTRIES"));
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "Segment",customerDetail.getCustomer().getCustSegment());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "SegmentISO",
				getMqInterfaceDAO().getPFFCode(customerDetail.getCustomer().getCustSegment(),"MCM_BMTSEGMENTS"));
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "ResidencyType","");
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "Gender",customerDetail.getCustomer().getCustGenderCode());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "Nationality",customerDetail.getCustomer().getCustNationality());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "NationalityISO" 
				,getMqInterfaceDAO().getPFFCode(customerDetail.getCustomer().getCustNationality(), "MCM_BMTCOUNTRIES"));
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "DualNationality","US");
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "DualNationalityISO","");
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "CountryOfbirth",customerDetail.getCustomer().getCustCOB());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "CountryOfbirthISO",
				getMqInterfaceDAO().getPFFCode(customerDetail.getCustomer().getCustNationality(), "MCM_BMTCOUNTRIES"));
		customerDetail.getCustomer().setCustMaritalSts("MARRIED");
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "MaritalStatus",customerDetail.getCustomer().getCustMaritalSts());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "MaritalStatusISO",
				getMqInterfaceDAO().getPFFCode(customerDetail.getCustomer().getCustMaritalSts(), "MCM_BMTMARITALSTATUSCODES"));
		//PFFXmlUtil.setOMChildElement(factory, personalInfo, "NoOfDependents",customerDetail.getCustomer().getNoOfDependents());
		
/*		//DependentsList
		OMElement dependentsList = factory.createOMElement("DependentsList",null);
		PFFXmlUtil.setOMChildElement(factory, dependentsList, "Dependents","");
		personalInfo.addChild(dependentsList);*/
		
		//PFFXmlUtil.setOMChildElement(factory, personalInfo, "YearsInUAE","");
		//PFFXmlUtil.setOMChildElement(factory, personalInfo, "RelationshipManager",customerDetail.getCustomer().getCustRO1());
		//PFFXmlUtil.setOMChildElement(factory, personalInfo, "RelationshipManagerISO","100002");
		//PFFXmlUtil.setOMChildElement(factory, personalInfo, "RelatedParty","");
		//PFFXmlUtil.setOMChildElement(factory, personalInfo, "Introducer",InterfaceMasterConfigUtil.INTRODUCER);
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "BranchCode",customerDetail.getCustomer().getCustDftBranch());
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "BranchCodeISO",
				getMqInterfaceDAO().getPFFCode(customerDetail.getCustomer().getCustDftBranch(), "MCM_RMTBRANCHES"));
		//PFFXmlUtil.setOMChildElement(factory, personalInfo, "lineManager","");
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "AccountOfficer","200004");
		PFFXmlUtil.setOMChildElement(factory, personalInfo, "AccountOfficerISO","100234");

		logger.debug("Leaving");

		return personalInfo;
	}

	/**
	 * Generate FinancialInformation Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 */
	private OMNode generateFinancialInformation(InterfaceCustomerDetail customerDetail, OMFactory factory) {
		logger.debug("Entering");

		OMElement financialInformation = factory.createOMElement("FinancialInformation",null);

		PFFXmlUtil.setOMChildElement(factory, financialInformation, "TotalNoOfPartners","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "ModeOfOperation","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "PowerOfAttorney","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "AuditedFinancials","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "FaxOfIndemity","");

		OMElement indemityFaxNumber = factory.createOMElement("IndemityFaxNumber",null);

		PFFXmlUtil.setOMChildElement(factory, indemityFaxNumber, "CountryCode","");
		PFFXmlUtil.setOMChildElement(factory, indemityFaxNumber, "AreaCode","");
		PFFXmlUtil.setOMChildElement(factory, indemityFaxNumber, "SubsidiaryNumber","");

		financialInformation.addChild(indemityFaxNumber);

		PFFXmlUtil.setOMChildElement(factory, financialInformation, "EmailIndemity","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "IndemityEmailAddress","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "ChequeBookRequest","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "CurrencyOfFinancials","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "TurnOver","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "GrossProfit","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "NetProfit","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "ShareCapital","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "NoOfEmployees","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "NatureOfBusiness","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "ThroughboutAmount","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "ThroughboutFrequency","");
		PFFXmlUtil.setOMChildElement(factory, financialInformation, "ThroughboutAccount","");

		logger.debug("Leaving");

		return financialInformation;
	}

	/**
	 * Generate SME DocumentDetails Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 */
	private OMNode generateSMEDocDetails(InterfaceCustomerDetail customerDetail, OMFactory factory) {
		logger.debug("Entering");

		OMElement documentDetails = factory.createOMElement("DocumentDetails",null);

		PFFXmlUtil.setOMChildElement(factory, documentDetails, "TradeLicenseName","");
		PFFXmlUtil.setOMChildElement(factory, documentDetails, "TradeLicenseNumber",customerDetail.getCustomer().getCustTradeLicenceNum());
		PFFXmlUtil.setOMChildElement(factory, documentDetails, "TradeLicenseIssueAuthority","");
		PFFXmlUtil.setOMChildElement(factory, documentDetails, "CommRegistrationNumber","");
		PFFXmlUtil.setOMChildElement(factory, documentDetails, "ChamberMemberNumber","");
		PFFXmlUtil.setOMChildElement(factory, documentDetails, "DocumentIDNumber","");
		PFFXmlUtil.setOMChildElement(factory, documentDetails, "DocumentIDType","");
		PFFXmlUtil.setOMChildElement(factory, documentDetails, "NameAsPerID","");
		PFFXmlUtil.setOMChildElement(factory, documentDetails, "IssuingAuthority","");

		logger.debug("Leaving");

		return documentDetails;
	}

	/**
	 * Generate SMECustDetails Request
	 * @param customerDetail
	 * @param factory
	 * @return
	 */
	private OMNode generateSMECustDetails(InterfaceCustomerDetail customerDetail, OMFactory factory) {
		logger.debug("Entering");

		OMElement smeCustDetails = factory.createOMElement("SMECustDetails",null);

		PFFXmlUtil.setOMChildElement(factory, smeCustDetails, "TypeOfEstablishment","Establishment");
		PFFXmlUtil.setOMChildElement(factory, smeCustDetails, "Industryattr1","Industry");
		PFFXmlUtil.setOMChildElement(factory, smeCustDetails, "Target","Target");
		PFFXmlUtil.setOMChildElement(factory, smeCustDetails, "CountryOfIncorporation","Incorporation");

		logger.debug("Leaving");

		return smeCustDetails;

	}

	/**
	 * Set Customer Response Details
	 * @param response
	 * @param header
	 * @return
	 * @throws InterfaceException
	 */
	private String setCustResponseDetails(OMElement response, AHBMQHeader header, String tagName)throws InterfaceException {
		logger.debug("Entering");

		if (response == null) {
			return null;
		}

		OMElement detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/"+tagName, response);
		header = PFFXmlUtil.parseHeader(response, header);
		header= getReturnStatus(detailElement, header, response);

		if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
			throw new InterfaceException("PTI3002", header.getErrorMessage());
		}

		return header.getReturnCode();
	}

	public MQInterfaceDAO getMqInterfaceDAO() {
		return mqInterfaceDAO;
	}

	public void setMqInterfaceDAO(MQInterfaceDAO mqInterfaceDAO) {
		this.mqInterfaceDAO = mqInterfaceDAO;
	}
	
}
