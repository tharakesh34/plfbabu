package com.pennant.mq.processutil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.customer.InterfaceCoreCustomer;
import com.pennant.coreinterface.model.customer.InterfaceCustEmployeeDetail;
import com.pennant.coreinterface.model.customer.InterfaceCustomer;
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

enum ADDRESSTYPES {
	RETAILRESIDENCE,RETAILOFFICE, RETAILHOMECOUNTRY, SMEESTMAIN, SMEESTOTHER, PHONECODES
};

public class FetchCustomerInfoProcess extends MQProcess {

	private static final Logger logger = Logger.getLogger(FetchCustomerInfoProcess.class);

	public FetchCustomerInfoProcess() {
		super();
	}

	private MQInterfaceDAO mqInterfaceDAO;
	
	/**
	 * Process the GetCustomerDetails based on the given CIF and Message format and send response
	 * 
	 * @param custCIF
	 * @param msgFormat
	 * @return
	 * @throws JaxenException
	 * @throws InterfaceException
	 */
	public InterfaceCustomerDetail getCustomerFullDetails(String custCIF, String msgFormat) throws JaxenException {
		logger.debug("Entering");

		if (custCIF == null || "".equals(custCIF)) {
			throw new InterfaceException("PTI3001", "Customer Cannot Be Blank");
		}

		// Set MQ Message configuration details
		setConfigDetails(InterfaceMasterConfigUtil.MQ_CONFIG_KEY);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		String referenceNum = PFFXmlUtil.getReferenceNumber();
		AHBMQHeader header = new AHBMQHeader(msgFormat);
		MessageQueueClient client = new MessageQueueClient(getServiceConfigKey());
		OMElement response = null;

		try {

			// Generate Request Element for MQ Call
			OMElement request = PFFXmlUtil.generateRequest(header, factory, getRequestElement(custCIF, referenceNum, factory));

			// Fetch Response element from Client using MQ call
			response = client.getRequestResponse(request.toString(), getRequestQueue(), getResponseQueue(), getWaitTime());

		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		}		
		logger.debug("Leaving");
		return setCustomerDetails(response, header);

	}

	/**
	 * Prepare FetchCustomer Details Request Element to send Interface through MQ
	 * 
	 * @param custCIF
	 * @param referenceNum
	 * @param factory
	 * @return
	 */
	private OMElement getRequestElement(String custCIF, String referenceNum, OMFactory factory) {
		logger.debug("Entering");

		OMElement requestElement = factory.createOMElement(new QName(InterfaceMasterConfigUtil.REQUEST));
		OMElement customerRequest = factory.createOMElement("getCustomerDetailsRequest", null);

		PFFXmlUtil.setOMChildElement(factory, customerRequest, "ReferenceNum", referenceNum);
		PFFXmlUtil.setOMChildElement(factory, customerRequest, "CIF", custCIF);
		PFFXmlUtil.setOMChildElement(factory, customerRequest, "TimeStamp",	
				PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.XML_DATETIME));

		requestElement.addChild(customerRequest);
		logger.debug("Leaving");
		return requestElement;
	}

	/**
	 * Prepare Customer object from processed Response Element
	 * 
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws JaxenException
	 * @throws InterfaceException
	 */
	private InterfaceCustomerDetail setCustomerDetails(OMElement responseElement, AHBMQHeader header)
			throws JaxenException {
		logger.debug("Entering");

		if (responseElement == null) {
			return null;
		}

		InterfaceCustomerDetail interfaceCustDetail = new InterfaceCustomerDetail();

		int custType = 0;
		OMElement detailElement;
		OMElement custDataElement = null;
		OMElement custDetailElement = null;
		OMElement financialInfoElement = null;
		OMElement employmentInfoElement = null;
		OMElement contactDetailElement = null;

		detailElement = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/getCustomerDetailsReply", responseElement);
		header = PFFXmlUtil.parseHeader(responseElement, header);
		header = getReturnStatus(detailElement, header,responseElement);

		if (!StringUtils.equals(PFFXmlUtil.SUCCESS, header.getReturnCode())) {
			throw new InterfaceException("PTI3002", header.getErrorMessage());
		}

		custDataElement = detailElement.getFirstChildWithName(new QName("Retail"));
		if ( custDataElement.getFirstChildWithName(new QName("PersonalInfo")) == null) {

			// Fetch Data for SME Category Customer
			custDetailElement = custDataElement.getFirstChildWithName(new QName("SMECustDetails"));
			financialInfoElement = custDataElement.getFirstChildWithName(new QName("FinancialInformation"));
			contactDetailElement = custDataElement.getFirstChildWithName(new QName("ContactDetails"));

			custType = 2;
		} else {

			// Fetch Data for Retail Category Customer
			custType = 1;
			custDetailElement = custDataElement.getFirstChildWithName(new QName("PersonalInfo"));
			employmentInfoElement = custDataElement.getFirstChildWithName(new QName("EmploymentInfo"));
		}

		// Common tags for both SME and Retail customer
		OMElement custDocDetailElement = custDataElement.getFirstChildWithName(new QName("DocumentDetails"));
		OMElement addrElement = custDataElement.getFirstChildWithName(new QName("AddressInfo"));
		String custCIF = PFFXmlUtil.getStringValue(custDataElement, "CIF");

		// Customer Details information
		InterfaceCustomer customer = new InterfaceCustomer();
		interfaceCustDetail.setCustCIF(custCIF);

		customer.setCustCIF(custCIF);
		customer.setCustCtgCode(InterfaceMasterConfigUtil.CUST_RETAIL);
		customer = setCustomerDetails(custDetailElement, customer, custType);

		customer.setCustCoreBank(custCIF);
		if (customer.getCustSegment() != null) {
			customer.setCustAddlVar82(getMqInterfaceDAO().getMDMCode(customer.getCustSegment(), "MCM_BMTSegments"));
			customer.setCustSegment(getMqInterfaceDAO().getMDMCode(customer.getCustSegment(), "MCM_BMTSegments"));
		}

		if (customer.getCustTypeCode() != null) {
			customer.setCustTypeCode(getMqInterfaceDAO().getMDMCode(customer.getCustTypeCode(), "MCM_RMTCUSTTYPES"));
		}
		if (customer.getCustLng() != null) {
			customer.setCustLng(getMqInterfaceDAO().getMDMCode(customer.getCustLng(), "MCM_BMTLANGUAGE"));
		}
		if (customer.getCustSalutationCode() != null) {
			customer.setCustSalutationCode(getMqInterfaceDAO().getMDMCode(customer.getCustSalutationCode(), "MCM_BMTSALUTATIONS"));
		}
		if (customer.getCustIndustry() != null) {
			customer.setCustIndustry(getMqInterfaceDAO().getMDMCode(customer.getCustIndustry(), "mcm_bmtindustries"));
		}
		if (customer.getCustSector() != null) {
			customer.setCustSector(getMqInterfaceDAO().getMDMCode(customer.getCustSector(), "mcm_bmtsectors"));
		}

		if (customer.getCustDftBranch() != null) {
			customer.setCustDftBranch(getMqInterfaceDAO().getMDMCode(customer.getCustDftBranch(), "mcm_rmtbranches"));
		}
		if (customer.getCustNationality() != null) {
			customer.setCustNationality(getMqInterfaceDAO().getMDMCode(customer.getCustNationality(), "mcm_bmtcountries"));
		}

		if (customer.getCustMaritalSts() != null) {
			customer.setCustMaritalSts(getMqInterfaceDAO().getMDMCode(customer.getCustMaritalSts(), "MCM_BMTMARITALSTATUSCODES"));
		}

		if (customer.getCustCOB() != null) {
			customer.setCustCOB(getMqInterfaceDAO().getMDMCode(customer.getCustCOB(), "mcm_bmtcountries"));
		}

		// Address Details information
		interfaceCustDetail.setAddressList(setCustomerAddress(addrElement, custType, custCIF));

		// Phone number Details information
		interfaceCustDetail.setCustomerPhoneNumList(setCustomerPhoneNumber(addrElement, custType, custCIF));

		// Email Details information
		interfaceCustDetail.setCustomerEMailList(setCustomerEmail(addrElement, custType, custCIF));

		// Customer Document Details information
		interfaceCustDetail.setCustomerDocumentsList(setCustomerdocuments(custDocDetailElement, custType, custCIF));

		// Customer Employment Details
		InterfaceCustEmployeeDetail custEmployeeDetail = null;

		InterfaceCoreCustomer coreCustomer  = new InterfaceCoreCustomer();
		coreCustomer.setNewRecord(true);

		switch (custType) {
		case 1:
			// Retail
			custEmployeeDetail = setEmploymentInfo(employmentInfoElement, coreCustomer);

			// Indemity FAX Service
			List<InterfaceCustomerPhoneNumber> custPhoneList = setIndemityFaxInfo(custDataElement.getFirstChildWithName(
					new QName("Indemity")), custCIF);
			if(custPhoneList != null) {
				if(interfaceCustDetail.getCustomerPhoneNumList() != null && !interfaceCustDetail.getCustomerPhoneNumList().isEmpty()){
					interfaceCustDetail.getCustomerPhoneNumList().addAll(custPhoneList);
				} else {
					interfaceCustDetail.setCustomerPhoneNumList(custPhoneList);
				}
			}	

			// Indemity EMail Service
			List<InterfaceCustomerEMail> custEmailList = setIndemityEmailInfo(custDataElement.getFirstChildWithName(
					new QName("Indemity")), custCIF);
			if(custEmailList != null) {
				if(interfaceCustDetail.getCustomerEMailList() != null  && !interfaceCustDetail.getCustomerEMailList().isEmpty()){
					interfaceCustDetail.getCustomerEMailList().addAll(custEmailList);
				} else {
					interfaceCustDetail.setCustomerEMailList(custEmailList);
				}
			}
			break;
		case 2:
			// SME
			customer = setContactDetails(contactDetailElement, customer);
			custEmployeeDetail = setCustFinancialInfo(financialInfoElement, coreCustomer);
			break;
		default:
			break;
		}

		// Set whole customer information to List
		interfaceCustDetail.setCustomer(customer);
		interfaceCustDetail.setCustEmployeeDetail(custEmployeeDetail);

		// SMS Service
		List<InterfaceCustomerPhoneNumber> custSMSPhoneList = setSMSServiceInfo(custDataElement.getFirstChildWithName(
				new QName("SmsServices")), custCIF);
		if(custSMSPhoneList != null) {
			if(interfaceCustDetail.getCustomerPhoneNumList() != null && !interfaceCustDetail.getCustomerPhoneNumList().isEmpty()){
				interfaceCustDetail.getCustomerPhoneNumList().addAll(custSMSPhoneList);
			} else {
				interfaceCustDetail.setCustomerPhoneNumList(custSMSPhoneList);
			}
		}	
		
		// POA Service
		coreCustomer = setPOAServiceInfo(custDataElement.getFirstChildWithName(new QName("PowerOfAttorney")), coreCustomer);

		// Rating Service
		coreCustomer = setRatingServiceInfo(custDataElement.getFirstChildWithName(new QName("Rating")), coreCustomer);

		// Relational Details
		coreCustomer = setRelationDetails(custDataElement.getFirstChildWithName(new QName("RelationDetails")),coreCustomer);

		// KYC Details
		coreCustomer = setKYCDetails(custDataElement.getFirstChildWithName(new QName("KYC")),coreCustomer);

		interfaceCustDetail.setInterfaceCoreCustomer(coreCustomer);

		logger.debug("Leaving");
		return interfaceCustDetail;
	}

	/**
	 * Method for Preparation of Contact Details information
	 * @param contactDetailElement
	 * @param customer
	 * @return
	 */
	private InterfaceCustomer setContactDetails(OMElement contactDetailElement, InterfaceCustomer customer) {
		logger.debug("Entering");

		if (contactDetailElement == null) {
			return null;
		}
		customer.setCustRO1(PFFXmlUtil.getStringValue(contactDetailElement, "RelationshipManager")); 
		customer.setCustDftBranch(PFFXmlUtil.getStringValue(contactDetailElement, "BranchCode"));
		//PFFXmlUtil.getStringValue(contactDetailElement, "LineManager");

		logger.debug("Leaving");
		return customer;
	}

	/**
	 * Method for Preparation of KYC Details
	 * @param kycElement
	 * @param coreCustomer 
	 */
	private InterfaceCoreCustomer setKYCDetails(OMElement kycElement, InterfaceCoreCustomer coreCustomer) {
		logger.debug("Entering");

		if (kycElement == null || coreCustomer == null) {
			return coreCustomer;
		}

		coreCustomer.setKycRiskLevel(PFFXmlUtil.getStringValue(kycElement, "KYCRiskLevel"));
		coreCustomer.setIntroducer(PFFXmlUtil.getStringValue(kycElement, "Introducer"));
		coreCustomer.setReferenceName(PFFXmlUtil.getStringValue(kycElement, "ReferenceName")); 
		coreCustomer.setPurposeOfRelationShip(PFFXmlUtil.getStringValue(kycElement, "PurposeOfRelationShip")); 
		coreCustomer.setPurposeOfRelationShip(PFFXmlUtil.getStringValue(kycElement, "SourceOfIncome")); 
		coreCustomer.setExpectedTypeOfTrans(PFFXmlUtil.getStringValue(kycElement,"ExpectedTypeOfTrans")); 
		coreCustomer.setMonthlyOutageVolume(PFFXmlUtil.getStringValue(kycElement, "MonthlyOutageVolume"));
		coreCustomer.setMonthlyIncomeVolume(PFFXmlUtil.getStringValue(kycElement, "MonthlyIncomeVolume"));
		coreCustomer.setMaximumSingleDeposit(PFFXmlUtil.getStringValue(kycElement, "MaximumSingleDeposit")); 
		coreCustomer.setMaximumSingleWithdrawal(PFFXmlUtil.getStringValue(kycElement, "MaximumSingleWithdrawal")); 
		coreCustomer.setAnnualIncome(PFFXmlUtil.getStringValue(kycElement, "AnnualIncome")); 
		coreCustomer.setCountryOfOriginOfFunds(PFFXmlUtil.getStringValue(kycElement, "CountryOfOriginOfFunds")); 
		coreCustomer.setCountryOfSourceOfIncome(PFFXmlUtil.getStringValue(kycElement, "CountryOfSourceOfIncome")); 
		coreCustomer.setSourceOfWealth(PFFXmlUtil.getStringValue(kycElement, "SourceOfWealth")); 
		coreCustomer.setIsKYCUptoDate(PFFXmlUtil.getStringValue(kycElement, "IsKYCUptoDate"));
		coreCustomer.setListedOnStockExchange(PFFXmlUtil.getStringValue(kycElement, "ListedOnStockExchange")); 
		coreCustomer.setNameOfExchange(PFFXmlUtil.getStringValue(kycElement, "NameOfExchange")); 
		coreCustomer.setStockCodeOfCustomer(PFFXmlUtil.getStringValue(kycElement, "StockCodeOfCustomer")); 
		coreCustomer.setCustomerVisitReport(PFFXmlUtil.getStringValue(kycElement, "CustomerVisitReport")); 
		coreCustomer.setInitialDeposit(PFFXmlUtil.getStringValue(kycElement, "InitialDeposit")); 
		coreCustomer.setFutureDeposit(PFFXmlUtil.getStringValue(kycElement, "FutureDeposit")); 
		coreCustomer.setAnnualTurnOver(PFFXmlUtil.getStringValue(kycElement, "AnnualTurnOver")); 
		coreCustomer.setParentCompanyDetails(PFFXmlUtil.getStringValue(kycElement, "ParentCompanyDetails")); 
		coreCustomer.setNameOfParentCompany(PFFXmlUtil.getStringValue(kycElement, "NameOfParentCompany")); 
		coreCustomer.setParentCompanyPlaceOfIncorp(PFFXmlUtil.getStringValue(kycElement, "ParentCompanyPlaceOfIncorp")); 
		coreCustomer.setEmirateOfIncop(PFFXmlUtil.getStringValue(kycElement, "EmirateOfIncop")); 
		coreCustomer.setNameOfApexCompany(PFFXmlUtil.getStringValue(kycElement, "NameOfApexCompany")); 
		coreCustomer.setNoOfEmployees(PFFXmlUtil.getStringValue(kycElement, "NoOfEmployees"));
		coreCustomer.setNoOfUAEBranches(PFFXmlUtil.getStringValue(kycElement, "NoOfUAEBranches"));
		coreCustomer.setNoOfOverseasBranches(PFFXmlUtil.getStringValue(kycElement, "NoOfOverseasBranches")); 
		coreCustomer.setOverSeasbranches(PFFXmlUtil.getStringValue(kycElement, "OverSeasbranches")); 
		coreCustomer.setNameOfAuditors(PFFXmlUtil.getStringValue(kycElement, "NameOfAuditors")); 
		coreCustomer.setFinancialHighlights(PFFXmlUtil.getStringValue(kycElement, "FinancialHighlights")); 
		coreCustomer.setBankingRelationShip(PFFXmlUtil.getStringValue(kycElement, "BankingRelationShip")); 
		coreCustomer.setpFFICertfication(PFFXmlUtil.getStringValue(kycElement, "PFFICertfication"));

		logger.debug("Leaving");
		return coreCustomer;
	}

	/**
	 * Method for preparation of Relation Details
	 * @param relationDetailElement
	 * @param coreCustomer 
	 */
	private InterfaceCoreCustomer setRelationDetails(OMElement relationDetailElement, InterfaceCoreCustomer coreCustomer) {
		logger.debug("Entering");

		if (relationDetailElement == null || coreCustomer == null) {
			return coreCustomer;
		}
		coreCustomer.setRelationCode(PFFXmlUtil.getStringValue(relationDetailElement, "RelationCode"));
		coreCustomer.setRelationShipCIF(PFFXmlUtil.getStringValue(relationDetailElement, "RelationShipCIF"));

		logger.debug("Leaving");
		return coreCustomer;
	}

	/**
	 * Method for preparation of Rating Details
	 * 
	 * @param ratingElement
	 * @param coreCustomer 
	 * @return 
	 */
	private InterfaceCoreCustomer setRatingServiceInfo(OMElement ratingElement, InterfaceCoreCustomer coreCustomer) {
		logger.debug("Entering");

		if (ratingElement == null || coreCustomer == null) {
			return coreCustomer;
		}

		coreCustomer.setInternalRating(PFFXmlUtil.getStringValue(ratingElement, "InternalRating")); 
		coreCustomer.setDateOfInternalRating(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(ratingElement, "DateOfInternalRating"), 
				InterfaceMasterConfigUtil.SHORT_DATE));

		logger.debug("Leaving");
		return coreCustomer;
	}

	/**
	 * Method for preparation of Power of Attorney Details
	 * 
	 * @param poaInfoElement
	 * @param coreCustomer 
	 * @return 
	 */
	private InterfaceCoreCustomer setPOAServiceInfo(OMElement poaInfoElement, InterfaceCoreCustomer coreCustomer) {
		logger.debug("Entering");

		if (poaInfoElement == null || coreCustomer == null) {
			return coreCustomer;
		}
		coreCustomer.setpOAFlag(PFFXmlUtil.getStringValue(poaInfoElement, "POAFlag")); 
		coreCustomer.setpOACIF(PFFXmlUtil.getStringValue(poaInfoElement, "POACIF"));
		coreCustomer.setpOAHoldersname(PFFXmlUtil.getStringValue(poaInfoElement, "POAHolderName")); 
		coreCustomer.setPassportNumber(PFFXmlUtil.getStringValue(poaInfoElement, "POAPassportNumber")); 
		coreCustomer.setEmiratesIDNumber(PFFXmlUtil.getStringValue(poaInfoElement, "POAIDNumber")); 
		coreCustomer.setNationality(PFFXmlUtil.getStringValue(poaInfoElement, "POANationality")); 
		coreCustomer.setpOAIssuancedate(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(poaInfoElement, "POAIssuanceDate"),
				InterfaceMasterConfigUtil.SHORT_DATE)); 
		coreCustomer.setpOAExpirydate(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(poaInfoElement, "POAExpiryDate"),
				InterfaceMasterConfigUtil.SHORT_DATE));  
		coreCustomer.setPassportExpiryDate(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(poaInfoElement, "POApassportExpiryDate"),
				InterfaceMasterConfigUtil.SHORT_DATE));  
		coreCustomer.setEmiratesIDExpiryDate(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(poaInfoElement, "POAIDExpiryDate"),
				InterfaceMasterConfigUtil.SHORT_DATE)); 

		logger.debug("Leaving");

		return coreCustomer;
	}

	/**
	 * Prepare SMS Service VO class
	 * 
	 * @param smsInfoElement
	 */
	private List<InterfaceCustomerPhoneNumber> setSMSServiceInfo(OMElement smsInfoElement, String custCIF) {
		logger.debug("Entering");

		if (smsInfoElement == null) {
			return null;
		}

		List<InterfaceCustomerPhoneNumber> phoneList = new ArrayList<InterfaceCustomerPhoneNumber>();
		InterfaceCustomerPhoneNumber custSMSPhoneNum = new InterfaceCustomerPhoneNumber();

		custSMSPhoneNum.setPhoneTypeCode(InterfaceMasterConfigUtil.SMSMOBILENO);
		OMElement smsElemet = smsInfoElement.getFirstChildWithName(new QName("SmsMobileNo"));
		custSMSPhoneNum.setLovDescCustCIF(custCIF);
		custSMSPhoneNum.setPhoneCountryCode(PFFXmlUtil.getStringValue(smsElemet, "CountryCode"));
		custSMSPhoneNum.setPhoneAreaCode(PFFXmlUtil.getStringValue(smsElemet, "AreaCode"));
		custSMSPhoneNum.setPhoneNumber(PFFXmlUtil.getStringValue(smsElemet, "SubsidiaryNumber"));
		phoneList.add(custSMSPhoneNum);

		logger.debug("Leaving");
		return phoneList;
	}

	/**
	 * Prepare Indemity Service VO class
	 * 
	 * @param indemityInfoElement
	 */
	private List<InterfaceCustomerPhoneNumber> setIndemityFaxInfo(OMElement indemityInfoElement, String custCIF) {
		logger.debug("Entering");

		if (indemityInfoElement == null) {
			return null;
		}

		List<InterfaceCustomerPhoneNumber> phoneList = new ArrayList<InterfaceCustomerPhoneNumber>();
		InterfaceCustomerPhoneNumber custSMSPhoneNum = new InterfaceCustomerPhoneNumber();

		custSMSPhoneNum.setPhoneTypeCode(InterfaceMasterConfigUtil.FAXINDEMITY);
		OMElement indemityElemet = indemityInfoElement.getFirstChildWithName(new QName("FaxNumber"));
		custSMSPhoneNum.setLovDescCustCIF(custCIF);
		custSMSPhoneNum.setPhoneCountryCode(PFFXmlUtil.getStringValue(indemityElemet, "CountryCode"));
		custSMSPhoneNum.setPhoneAreaCode(PFFXmlUtil.getStringValue(indemityElemet, "AreaCode"));
		custSMSPhoneNum.setPhoneNumber(PFFXmlUtil.getStringValue(indemityElemet, "SubsidiaryNumber"));
		phoneList.add(custSMSPhoneNum);

		logger.debug("Leaving");
		return phoneList;
	}

	private List<InterfaceCustomerEMail> setIndemityEmailInfo(OMElement indemEmailElement, String custCIF) {
		logger.debug("Entering");

		if (indemEmailElement == null) {
			return null;
		}

		List<InterfaceCustomerEMail> emailList = new ArrayList<InterfaceCustomerEMail>();
		InterfaceCustomerEMail custEmail = new InterfaceCustomerEMail();
		custEmail.setLovDescCustCIF(custCIF);
		custEmail.setCustEMailTypeCode(InterfaceMasterConfigUtil.getIndemityEmail()[0]);
		custEmail.setLovDescCustEMailTypeCode(InterfaceMasterConfigUtil.getIndemityEmail()[1]);
		custEmail.setCustEMail(PFFXmlUtil.getStringValue(indemEmailElement, "EmailAddress"));
		emailList.add(custEmail);

		return emailList;
	}

	/**
	 * 
	 * @param employmentInfoElement
	 * @param coreCustomer 
	 * @return
	 */
	private InterfaceCustEmployeeDetail setEmploymentInfo(OMElement employmentInfoElement, InterfaceCoreCustomer coreCustomer) {
		logger.debug("Entering");

		if (employmentInfoElement == null) {
			return null;
		}

		InterfaceCustEmployeeDetail customerEmployeeDetail = new InterfaceCustEmployeeDetail();

		customerEmployeeDetail.setEmpStatus(PFFXmlUtil.getStringValue(employmentInfoElement, "EmpStatus"));
		customerEmployeeDetail.setEmpName(0);//PFFXmlUtil.getStringValue(employmentInfoElement, "EmpName")
		customerEmployeeDetail.setEmpDesg(PFFXmlUtil.getStringValue(employmentInfoElement, "Occupation"));
		customerEmployeeDetail.setEmpDept(PFFXmlUtil.getStringValue(employmentInfoElement, "Department"));
		customerEmployeeDetail.setEmpFrom(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(
				employmentInfoElement, "EmpStartDate"),InterfaceMasterConfigUtil.SHORT_DATE));
		customerEmployeeDetail.setMonthlyIncome(PFFXmlUtil.getBigDecimalValue(employmentInfoElement, "Salary"));

		//Not Available fields saved in CoreCustomer Object
		coreCustomer.setSalaryCurrency(PFFXmlUtil.getStringValue(employmentInfoElement, "SalaryCurrency"));
		coreCustomer.setSalary(PFFXmlUtil.getBigDecimalValue(employmentInfoElement, "Salary"));
		coreCustomer.setSalaryDateFreq(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(employmentInfoElement, "SalaryDateFreq"),
				InterfaceMasterConfigUtil.SHORT_DATE));
		coreCustomer.setBusinessType(PFFXmlUtil.getStringValue(employmentInfoElement, "BusinessType"));
		coreCustomer.setNameOfBusiness(PFFXmlUtil.getStringValue(employmentInfoElement, "NameOfBusiness"));

		logger.debug("Leaving");

		return customerEmployeeDetail;
	}

	/**
	 * 
	 * @param financialInfoElement
	 * @param coreCustomer 
	 * @return 
	 */
	private InterfaceCustEmployeeDetail setCustFinancialInfo(OMElement financialInfoElement, InterfaceCoreCustomer coreCustomer) {
		logger.debug("Entering");

		if (financialInfoElement == null) {
			return null;
		}

		InterfaceCustEmployeeDetail customerEmployeeDetail = new InterfaceCustEmployeeDetail();

		customerEmployeeDetail.setEmpStatus("SME");
		customerEmployeeDetail.setMonthlyIncome(new BigDecimal(PFFXmlUtil.getStringValue(financialInfoElement, "TurnOver")));
		customerEmployeeDetail.setEmpSector(PFFXmlUtil.getStringValue(financialInfoElement, "NatureOfBusiness"));

		// Saved the Not Available fields in CoreCustomer
		coreCustomer.setNoOfEmployees(PFFXmlUtil.getStringValue(financialInfoElement, "NoOfEmployees"));
		coreCustomer.setTotalNoOfPartners(PFFXmlUtil.getIntValue(financialInfoElement, "TotalNoOfPartners")); 
		coreCustomer.setModeOfOperation(PFFXmlUtil.getStringValue(financialInfoElement, "ModeOfOperation"));
		coreCustomer.setPowerOfAttorney(PFFXmlUtil.getStringValue(financialInfoElement, "PowerOfAttorney"));
		coreCustomer.setAuditedFinancials(PFFXmlUtil.getStringValue(financialInfoElement, "AuditedFinancials"));
		coreCustomer.setFaxOfIndemity(PFFXmlUtil.getStringValue(financialInfoElement, "FaxOfIndemity"));
		coreCustomer.setEmailIndemity(PFFXmlUtil.getStringValue(financialInfoElement, "EmailIndemity"));//FIXME:duplicated
		coreCustomer.setIndemityEmailAddress(PFFXmlUtil.getStringValue(financialInfoElement, "IndemityEmailAddress"));
		coreCustomer.setChequeBookRequest(PFFXmlUtil.getStringValue(financialInfoElement, "ChequeBookRequest"));
		coreCustomer.setCurrencyOfFinancials(PFFXmlUtil.getStringValue(financialInfoElement, "CurrencyOfFinancials"));
		coreCustomer.setGrossProfit(PFFXmlUtil.getBigDecimalValue(financialInfoElement, "GrossProfit"));
		coreCustomer.setNetProfit(PFFXmlUtil.getBigDecimalValue(financialInfoElement, "NetProfit"));
		coreCustomer.setShareCapital(PFFXmlUtil.getBigDecimalValue(financialInfoElement, "ShareCapital"));
		coreCustomer.setThroughputAmount(PFFXmlUtil.getBigDecimalValue(financialInfoElement, "ThroughputAmount"));
		coreCustomer.setThroughputFrequency(PFFXmlUtil.getStringValue(financialInfoElement, "ThroughputFrequency"));
		coreCustomer.setThroughputAccount(PFFXmlUtil.getStringValue(financialInfoElement, "ThroughputAccount"));

		logger.debug("Leaving");
		return customerEmployeeDetail;

	}

	/**
	 * Method for Preparation of Customer Document Details
	 * 
	 * @param custDocDetailElement
	 * @param custType
	 * @return
	 */
	private List<InterfaceCustomerDocument> setCustomerdocuments(OMElement custDocDetailElement, int custType, String custCIF) {
		logger.debug("Entering");

		if (custDocDetailElement == null) {
			return null;
		}

		List<InterfaceCustomerDocument> docList = new ArrayList<InterfaceCustomerDocument>();

		if (custType == 1) {
			// custDocDetailElement.getFirstChildWithName(new QName("IDType")).getText();
			docList.addAll(setCustDocumentsByType(custDocDetailElement,	"EmiratesID", InterfaceMasterConfigUtil.getDocEmiratesId(), custCIF));
			docList.addAll(setCustDocumentsByType(custDocDetailElement,	"Passport", InterfaceMasterConfigUtil.getDocPassportId(), custCIF));
			docList.addAll(setCustDocumentsByType(custDocDetailElement,	"ResidenceVisa", InterfaceMasterConfigUtil.getDocResidenceVisa(), custCIF));
			docList.addAll(setCustDocumentsByType(custDocDetailElement,	"USID", InterfaceMasterConfigUtil.getDocUSID(), custCIF));

		} else {
			docList.addAll(setCustDocumentsByType(custDocDetailElement,	"TradeLicense", InterfaceMasterConfigUtil.getDocTradeLicence(), custCIF));
			docList.addAll(setCustDocumentsByType(custDocDetailElement,	"CommRegistration", InterfaceMasterConfigUtil.getDocCommRegistration(), custCIF));
			// PFFXmlUtil.getStringValue(custDocDetailElement, "DocumentIDNumber");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "TradeLicenseIssueAuthority");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "TradeLicenseIssueAuthorityISO");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "CommRegistrationNumber");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "ChamberMemberNumber");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "CommRegistrationIssueDate");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "CommRegistrationExpDate");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "DocumentIDType");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "DocumentIDTypeISO");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "NameAsPerID");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "IssuingAuthority");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "IssuingAuthorityISO");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "IDIssueDate");
			// PFFXmlUtil.getStringValue(custDocDetailElement, "IDExpiryDate");
		}

		logger.debug("Leaving");

		return docList;
	}

	private List<InterfaceCustomerDocument> setCustDocumentsByType(OMElement custDocDetailElement, String docType, String[] idType, String custCIF) {

		logger.debug("Entering");

		List<InterfaceCustomerDocument> docList = new ArrayList<InterfaceCustomerDocument>();
		InterfaceCustomerDocument customerDocuments = new InterfaceCustomerDocument();
		String docID;

		switch (docType) {
		case "EmiratesID":
			docID = docType + "No";
			customerDocuments.setCustDocIssuedOn(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(custDocDetailElement, docType + "IssueDate"),
					InterfaceMasterConfigUtil.SHORT_DATE));
			customerDocuments.setCustDocExpDate(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(custDocDetailElement, "UaeIDExpDate"),
					InterfaceMasterConfigUtil.SHORT_DATE));

			break;
		case "ResidenceVisa":
			docID = docType + "No";
			customerDocuments.setCustDocIssuedOn(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(custDocDetailElement, docType + "IssueDate"),
					InterfaceMasterConfigUtil.SHORT_DATE));
			customerDocuments.setCustDocExpDate(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(custDocDetailElement, docType + "ExpDate"),
					InterfaceMasterConfigUtil.SHORT_DATE));

			break;
		case "USID":
			docID = docType + "No";
			break;

		default:
			docID = docType + "Number";
			customerDocuments.setCustDocIssuedOn(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(custDocDetailElement, docType + "IssueDate"),
					InterfaceMasterConfigUtil.SHORT_DATE));
			customerDocuments.setCustDocExpDate(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(custDocDetailElement, docType + "ExpDate"),
					InterfaceMasterConfigUtil.SHORT_DATE));

			break;
		}
		customerDocuments.setLovDescCustCIF(custCIF);
		customerDocuments.setCustDocCategory(idType[0]);
		customerDocuments.setLovDescCustDocCategory(idType[1]);
		customerDocuments.setCustDocTitle(PFFXmlUtil.getStringValue(custDocDetailElement, docID));

		if(StringUtils.isNotBlank(customerDocuments.getCustDocTitle())) {
			docList.add(customerDocuments);
		}
		logger.debug("Leaving");

		return docList;
	}

	/**
	 * Method for Preparation of Customer Details
	 * @param custDetailElement
	 * @param custType
	 * @return
	 */
	private InterfaceCustomer setCustomerDetails(OMElement custDetailElement,
			InterfaceCustomer customer, int custType) {
		logger.debug("Entering");

		if (custDetailElement == null) {
			return null;
		}

		if (custType == 1) {

			// customer.setCustSts(PFFXmlUtil.getStringValue(custDetailElement, "CustomerStatusISO"));
			customer.setCustSegment(PFFXmlUtil.getStringValue(custDetailElement, "SegmentISO"));
			customer.setCustTypeCode(PFFXmlUtil.getStringValue(custDetailElement, "CustomerStatusISO"));
			customer.setCustSalutationCode(PFFXmlUtil.getStringValue(custDetailElement, "TitleISO"));
			customer.setCustShrtName(PFFXmlUtil.getStringValue(custDetailElement, "ShortName"));
			customer.setCustCoreBank(PFFXmlUtil.getStringValue(custDetailElement, "CIF"));
			customer.setCustMotherMaiden(PFFXmlUtil.getStringValue(custDetailElement, "MotherName"));
			customer.setCustFName(PFFXmlUtil.getStringValue(custDetailElement, "FirstName"));
			customer.setCustLName(PFFXmlUtil.getStringValue(custDetailElement, "FamilyName"));
			customer.setCustMName(PFFXmlUtil.getStringValue(custDetailElement, "SecondName"));
			customer.setCustDOB(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(custDetailElement, "DateOfBirth"),
					InterfaceMasterConfigUtil.SHORT_DATE));
			customer.setCustPOB(PFFXmlUtil.getStringValue(custDetailElement, "PlaceOfBirth"));
			customer.setCustLng(PFFXmlUtil.getStringValue(custDetailElement, "LanguageISO"));
			customer.setCustSector(PFFXmlUtil.getStringValue(custDetailElement, "SectorISO"));
			customer.setCustIndustry(PFFXmlUtil.getStringValue(custDetailElement, "IndustryISO"));

			customer.setCustGenderCode(PFFXmlUtil.getStringValue(custDetailElement, "Gender"));
			customer.setCustNationality(PFFXmlUtil.getStringValue(custDetailElement, "NationalityISO"));
			customer.setCustCOB(PFFXmlUtil.getStringValue(custDetailElement, "CountryOfbirth"));
			customer.setCustMaritalSts(PFFXmlUtil.getStringValue(custDetailElement, "MaritalStatusISO"));
			int dependents = 0;
			if(PFFXmlUtil.getStringValue(custDetailElement, "NoOfDependents") != null){
				dependents = Integer.parseInt(PFFXmlUtil.getStringValue(custDetailElement, "NoOfDependents"));
			}
			customer.setNoOfDependents(dependents);
			//customer.setCustRO1(PFFXmlUtil.getStringValue(custDetailElement, "RelationshipManagerISO"));
			customer.setCustDftBranch(PFFXmlUtil.getStringValue(custDetailElement, "BranchCodeISO"));
			customer.setCustAddlVar83(PFFXmlUtil.getStringValue(custDetailElement, "RelatedParty"));
			// PFFXmlUtil.getStringValue(custDetailElement, "ResidencyType");
			// PFFXmlUtil.getStringValue(custDetailElement, "lineManager");

		} else {

			customer.setCustSts(PFFXmlUtil.getStringValue(custDetailElement, "CustStatus"));
			customer.setCustFName(PFFXmlUtil.getStringValue(custDetailElement,"NameOfEstablishment"));
			customer.setCustShrtName(PFFXmlUtil.getStringValue(custDetailElement, "EstablishmentShortName"));
			customer.setCustCoreBank(PFFXmlUtil.getStringValue(custDetailElement, "Mnemonic"));
			customer.setCustDOB(DateUtility.convertDateFromMQ(PFFXmlUtil.getStringValue(custDetailElement, "DateOfIncorporation"),
					InterfaceMasterConfigUtil.SHORT_DATE));
			customer.setCustLng(PFFXmlUtil.getStringValue(custDetailElement, "LanguageCode"));
			customer.setCustIndustry(PFFXmlUtil.getStringValue(custDetailElement, "IndustryCode"));
			customer.setCustAddlVar82(PFFXmlUtil.getStringValue(custDetailElement, "Target"));
			customer.setCustTypeCode(PFFXmlUtil.getStringValue(custDetailElement, "TypeOfEstablishment"));
			customer.setCustCOB(PFFXmlUtil.getStringValue(custDetailElement, "CountryOfIncorporation"));
			// PFFXmlUtil.getStringValue(custDetailElement, "IncorporationType");
			// PFFXmlUtil.getStringValue(custDetailElement, "ParentCoCIF");
			// PFFXmlUtil.getStringValue(custDetailElement, "Auditor");
			// PFFXmlUtil.getStringValue(custDetailElement, "UseChequeBook");
		}
		logger.debug("Leaving");

		return customer;
	}

	/**
	 * Get Customer Phone numbers based on the type
	 * 
	 * @param addrElement
	 * @param custType
	 * @return
	 */
	private List<InterfaceCustomerPhoneNumber> setCustomerPhoneNumber(OMElement addrElement, int custType, String custCIF) {
		logger.debug("Entering");

		if (addrElement == null) {
			return null;
		}

		List<InterfaceCustomerPhoneNumber> phoneList = new ArrayList<InterfaceCustomerPhoneNumber>();

		if (custType == 1) {

			String path = "/HB_EAI_REPLY/Reply/getCustomerDetailsReply/Retail/AddressInfo/";
			OMElement ofceAddElement = addrElement.getFirstChildWithName(new QName("OfficeAddress"));
			OMElement resAddElement = addrElement.getFirstChildWithName(new QName("ResidenceAddress"));
			OMElement hcAddElement = addrElement.getFirstChildWithName(new QName("HomeCountryAddress"));

			try {
				if (ofceAddElement != null) {
					String parentTag = "OfficeAddress/";
					phoneList = setCustomerPhoneNumberByType(ofceAddElement, path + parentTag+ "OfficePhoneNumbers/OfficePhoneNo",
							InterfaceMasterConfigUtil.OFFICEPHONENO, phoneList, custCIF);
					phoneList = setCustomerPhoneNumberByType(ofceAddElement, path + parentTag+ "OfficeFaxNumbers/OfficeFaxNo",
							InterfaceMasterConfigUtil.OFFICEFAXNO, phoneList, custCIF);
					phoneList = setCustomerPhoneNumberByType(ofceAddElement, path + parentTag+ "OfficeMobileNumbers/OfficeMobileNo",
							InterfaceMasterConfigUtil.OFFICEMOBILENO, phoneList, custCIF);
				}

				if (resAddElement != null) {
					String parentTag = "ResidenceAddress/";
					phoneList = setCustomerPhoneNumberByType(resAddElement, path + parentTag+ "ResidencePhoneNumbers/ResidencePhoneNo",
							InterfaceMasterConfigUtil.RESIDENCEPHONENO, phoneList, custCIF);
					phoneList = setCustomerPhoneNumberByType(resAddElement, path + parentTag+ "ResidenceFaxNumbers/ResidenceFaxNo",
							InterfaceMasterConfigUtil.RESIDENCEFAXNO, phoneList, custCIF);
					phoneList = setCustomerPhoneNumberByType(resAddElement,	path+ parentTag+ "ResidenceMobileNumbers/ResidenceMobileNo",
							InterfaceMasterConfigUtil.RESIDENCEMOBILENO, phoneList, custCIF);
				}

				if (hcAddElement != null) {
					String parentTag = "HomeCountryAddress/";
					phoneList = setCustomerPhoneNumberByType(hcAddElement, path + parentTag + "HCPhoneNumbers/HCPhoneNo", 
							InterfaceMasterConfigUtil.HCPHONENO, phoneList, custCIF);
					phoneList = setCustomerPhoneNumberByType(hcAddElement,	path + parentTag + "HCFaxNumbers/HCFaxNo", 
							InterfaceMasterConfigUtil.HCFAXNO, phoneList, custCIF);
					phoneList = setCustomerPhoneNumberByType(hcAddElement,	path + parentTag + "HCMobileNumbers/HCMobileNo", 
							InterfaceMasterConfigUtil.HCMOBILENO, phoneList, custCIF);
				}
			} catch (JaxenException je) {
				logger.error("Exception: ", je);
			}

		} else {

			String path = "/HB_EAI_REPLY/Reply/getCustomerDetailsReply/SME/AddressInfo/";

			OMElement estMainAddElement = addrElement.getFirstChildWithName(new QName("EstMainAddress"));
			OMElement estOtherAddElement = addrElement.getFirstChildWithName(new QName("EstOtherAddress"));

			try {
				if (estMainAddElement != null) {
					String parentTag = "EstMainAddress/";
					phoneList = setCustomerPhoneNumberByType(estMainAddElement, path + parentTag+ "EstPhoneNumbers/EstPhoneNo",
							InterfaceMasterConfigUtil.ESTPHONENO, phoneList, custCIF);
					phoneList = setCustomerPhoneNumberByType(estMainAddElement, path + parentTag+ "EstFaxNumbers/EstFaxNo",	
							InterfaceMasterConfigUtil.ESTFAXNO, phoneList, custCIF);
					phoneList = setCustomerPhoneNumberByType(estMainAddElement, path + parentTag+ "EstMobileNumbers/EstMobileNo",
							InterfaceMasterConfigUtil.ESTMOBILENO, phoneList, custCIF);
				}

				if (estOtherAddElement != null) {
					String parentTag = "EstOtherAddress/";
					phoneList = setCustomerPhoneNumberByType(estOtherAddElement, path + parentTag+ "EstOtherPhoneNumbers/EstOtherPhoneNo",
							InterfaceMasterConfigUtil.ESTOTHERPHONENO, phoneList, custCIF);
					phoneList = setCustomerPhoneNumberByType(estOtherAddElement, path + parentTag+ "EstOtherFaxNumbers/EstOtherFaxNo",
							InterfaceMasterConfigUtil.ESTOTHERFAXNO, phoneList, custCIF);
					phoneList = setCustomerPhoneNumberByType(estOtherAddElement, path + parentTag+ "EstOtherMobileNumbers/EstOtherMobileNo",
							InterfaceMasterConfigUtil.ESTOTHERMOBILENO, phoneList, custCIF);
				}
			} catch (JaxenException je) {
				logger.error("Exception: ", je);
			}
		}
		logger.debug("Leaving");

		return phoneList;
	}

	/**
	 * Get list of customer contact details i.e phone,fax,mobile numbers based on the type
	 * 
	 * @param element
	 * @param path
	 * @param type
	 * @param phoneList
	 * @return
	 * @throws JaxenException
	 */
	private List<InterfaceCustomerPhoneNumber> setCustomerPhoneNumberByType(OMElement element, String path, String type, 
			List<InterfaceCustomerPhoneNumber> phoneList, String custCIF) throws JaxenException {
		logger.debug("Entering");

		if (element == null) {
			return phoneList;
		}

		AXIOMXPath xpath = new AXIOMXPath(path);

		@SuppressWarnings("unchecked")
		List<OMElement> custPhoneDetailList = (List<OMElement>) xpath.selectNodes(element);
		for (OMElement omElement : custPhoneDetailList) {
			InterfaceCustomerPhoneNumber custPhoneNum = new InterfaceCustomerPhoneNumber();
			custPhoneNum.setLovDescCustCIF(custCIF);
			custPhoneNum.setPhoneTypeCode(type);
			custPhoneNum.setPhoneCountryCode(PFFXmlUtil.getStringValue(omElement, "CountryCode"));
			custPhoneNum.setPhoneAreaCode(PFFXmlUtil.getStringValue(omElement, "AreaCode"));
			custPhoneNum.setPhoneNumber(PFFXmlUtil.getStringValue(omElement, "SubsidiaryNumber"));
			phoneList.add(custPhoneNum);
		}
		
		logger.debug("Leaving");

		return phoneList;
	}

	/**
	 * 
	 * @param addrElement
	 * @param custType
	 * @return
	 */
	private List<InterfaceCustomerEMail> setCustomerEmail(OMElement addrElement, int custType, String custCIF) {
		logger.debug("Entering");

		if (addrElement == null) {
			return null;
		}

		List<InterfaceCustomerEMail> emailList = new ArrayList<InterfaceCustomerEMail>();

		if (custType == 1) {

			String path = "/HB_EAI_REPLY/Reply/getCustomerDetailsReply/Retail/AddressInfo/";
			OMElement ofceAddElement = addrElement.getFirstChildWithName(new QName("OfficeAddress"));
			OMElement resAddElement = addrElement.getFirstChildWithName(new QName("ResidenceAddress"));

			try {
				if (ofceAddElement != null) {
					String parentTag = "OfficeAddress/";
					emailList.addAll(setCustomerEmailByType(ofceAddElement, path+ parentTag + "OfficeEmailAddresses/",
							"OfficeEmailAddress",InterfaceMasterConfigUtil.getPersonalEmail(), custCIF));
				}

				if (resAddElement != null) {
					String parentTag = "ResidenceAddress/";
					emailList.addAll(setCustomerEmailByType(resAddElement, path + parentTag + "ResidenceEmailAddresses/",
							"ResidenceEmailAddress", InterfaceMasterConfigUtil.getResidenceEmail(), custCIF));
				}

			} catch (Exception ex) {
				logger.error("Exception: ", ex);
			}

		} else {

			String path = "/HB_EAI_REPLY/Reply/getCustomerDetailsReply/SME/AddressInfo/";

			OMElement estMainAddElement = addrElement.getFirstChildWithName(new QName("EstMainAddress"));
			OMElement estOtherAddElement = addrElement.getFirstChildWithName(new QName("EstOtherAddress"));

			try {
				if (estMainAddElement != null) {
					String parentTag = "EstMainAddress/";
					emailList.addAll(setCustomerEmailByType(estMainAddElement, path + parentTag + "EstEmailAddresses/",
							"EstEmailAddress", InterfaceMasterConfigUtil.getEstMainEmail(), custCIF));
				}

				if (estOtherAddElement != null) {
					String parentTag = "EstOtherAddress/";
					emailList.addAll(setCustomerEmailByType(estMainAddElement, path + parentTag + "EstOtherEmailAddresses/", 
							"EstOtherEmailAddress", InterfaceMasterConfigUtil.getEstOtherEmail(), custCIF));
				}
			} catch (Exception ex) {
				logger.error("Exception: ", ex);
			}
		}
		logger.debug("Leaving");
		return emailList;
	}

	/**
	 * Get Customer Email Id's based on the type
	 * 
	 * @param ofceAddElement
	 * @param path
	 * @param type
	 * @return
	 * @throws JaxenException
	 * @throws InterfaceException
	 */
	private List<InterfaceCustomerEMail> setCustomerEmailByType(OMElement element, String path, String tagname,
			String[] type, String custCIF) throws JaxenException {
		logger.debug("Entering");

		if (element == null) {
			return null;
		}

		List<InterfaceCustomerEMail> emailList = new ArrayList<InterfaceCustomerEMail>();
		AXIOMXPath xpath = new AXIOMXPath(path + tagname);

		@SuppressWarnings("unchecked")
		List<OMElement> custEmailList = (List<OMElement>) xpath.selectNodes(element);
		for (OMElement omElement : custEmailList) {
			InterfaceCustomerEMail custEmail = new InterfaceCustomerEMail();
			custEmail.setLovDescCustCIF(custCIF);
			custEmail.setCustEMailTypeCode(type[0]);
			custEmail.setLovDescCustEMailTypeCode(type[1]);
			custEmail.setCustEMail(omElement.getText());
			emailList.add(custEmail);
		}
		logger.debug("Leaving");

		return emailList;
	}

	/**
	 * set Customer Address details based on the Address type
	 * 
	 * @param addrElement
	 * @param custType
	 * @return
	 * @throws Exception
	 */
	private List<InterfaceCustomerAddress> setCustomerAddress(
			OMElement addrElement, int custType, String custCIF) throws InterfaceException {
		logger.debug("Entering");

		if (addrElement == null) {
			return null;
		}

		List<InterfaceCustomerAddress> addrList = new ArrayList<InterfaceCustomerAddress>();

		if (custType == 1) {

			addrList = setCustomerAddressByType(addrElement,InterfaceMasterConfigUtil.getRetailOfficeAddress(),
					ADDRESSTYPES.RETAILOFFICE, addrList, custCIF);
			addrList = setCustomerAddressByType(addrElement,InterfaceMasterConfigUtil.getRetailResidenceAddress(),
					ADDRESSTYPES.RETAILRESIDENCE, addrList, custCIF);
			addrList = setCustomerAddressByType(addrElement,InterfaceMasterConfigUtil.getRetailHomeCountryAddress(),
					ADDRESSTYPES.RETAILHOMECOUNTRY, addrList, custCIF);

		} else {

			addrList = setCustomerAddressByType(addrElement,InterfaceMasterConfigUtil.getSMEEstMainAddress(),
					ADDRESSTYPES.SMEESTMAIN, addrList, custCIF);
			addrList = setCustomerAddressByType(addrElement,InterfaceMasterConfigUtil.getSMEEstOtherAddress(),
					ADDRESSTYPES.SMEESTOTHER, addrList, custCIF);

		}
		logger.debug("Leaving");

		return addrList;
	}

	/**
	 * Set Customer phone number details based on the type
	 * 
	 * @param addElement
	 * @param addType
	 * @return
	 * @throws Exception
	 */
	private List<InterfaceCustomerAddress> setCustomerAddressByType(OMElement element, String[] addressTypes, ADDRESSTYPES type,
			List<InterfaceCustomerAddress> list, String custCIF)throws InterfaceException {
		logger.debug("Entering");

		String[] keyTags = getkeyTags(type);

		if (element == null) {
			return list;
		}

		OMElement addElement = element.getFirstChildWithName(new QName(keyTags[0]));

		if (addElement == null) {
			return list;
		}

		InterfaceCustomerAddress address = new InterfaceCustomerAddress();

		address.setLovDescCustCIF(custCIF);
		address.setCustAddrType(addressTypes[0]);
		address.setLovDescCustAddrTypeName(addressTypes[1]);
		address.setCustPOBox(PFFXmlUtil.getStringValue(addElement, keyTags[1]));
		address.setCustFlatNbr(PFFXmlUtil.getStringValue(addElement, keyTags[2]));
		address.setCustAddrHNbr(PFFXmlUtil.getStringValue(addElement, keyTags[3]));
		address.setCustAddrStreet(PFFXmlUtil.getStringValue(addElement, keyTags[4]));
		address.setCustAddrLine1(PFFXmlUtil.getStringValue(addElement, keyTags[5]));
		address.setCustAddrCountry(PFFXmlUtil.getStringValue(addElement, keyTags[8]));
		if (address.getCustAddrCountry() != null) {
			address.setCustAddrCountry(getMqInterfaceDAO().getMDMCode(address.getCustAddrCountry(), "mcm_bmtcountries"));
		}
		address.setCustAddrProvince(PFFXmlUtil.getStringValue(addElement, keyTags[6]));
		// String landMark = PFFXmlUtil.getStringValue(addElement, keyTags[5]);
		// String emiarate = PFFXmlUtil.getStringValue(addElement, keyTags[6]);
		// String emirateISO = PFFXmlUtil.getStringValue(addElement, keyTags[7]);
		// String countryISO = PFFXmlUtil.getStringValue(addElement, keyTags[9]);

		list.add(address);

		logger.debug("Leaving");

		return list;
	}

	private String[] getkeyTags(ADDRESSTYPES type) {
		String[] keyTags = new String[10];

		switch (type) {

		case RETAILOFFICE:
			keyTags = new String[] { "OfficeAddress", "OfficePOBox",
					"OfficeFlatNo", "OfficeBuildingName", "OfficeStreetName",
					"OfficeNearstLandmark", "OfficeEmirate",
					"OfficeEmirateISO", "OfficeCountry", "OfficeCountryISO" };
			break;
		case RETAILRESIDENCE:
			keyTags = new String[] { "ResidenceAddress", "ResidencePOBox",
					"ResidenceFlatNo", "ResidenceBuildingName", "ResidenceStreetName",
					"ResidenceNearstLandmark", "ResidenceEmirate",
					"ResidenceEmirateISO", "ResidenceCountry", "ResidenceCountryISO" };
			break;
		case RETAILHOMECOUNTRY:
			keyTags = new String[] { "HomeCountryAddress", "HCPOBox",
					"HCFlatNo", "HCBuildingName", "HCStreetName",
					"HCNearestLandmark", "HCEmirate",
					"HCEmirateISO", "HCCountryCode", "HCCountryCodeISO" };
			break;
		case SMEESTMAIN:
			keyTags = new String[] { "EstMainAddress", "EstMainPOBox",
					"EstMainFlatNo", "EstMainBuildingName", "EstMainStreetName",
					"EstMainNearstLandmark", "EstMainEmirate",
					"EstMainEmirateISO", "EstMainCountry", "HomeCountryCountryISO" };
			break;
		case SMEESTOTHER:
			keyTags = new String[] {"EstOtherAddress", "EstOtherPOBox",
					"EstOtherFlatNo", "EstOtherBuildingName", "EstOtherStreetName",
					"EstOtherNearstLandmark", "EstOtherEmirate",
					"EstOtherEmirateISO", "EstOtherCountry", "EstOtherCountryISO" };
			break;
		case PHONECODES:
			keyTags = new String[] { "OfficeAddress", "EstPOBox",
					"EstFlatNo", "EstBuildingName", "EstStreetName",
					"EstNearstLandmark", "EstEmirate",
					"EstEmirateISO", "EstCountry", "EstCountryISO" };
			break;
		default:
			break;
		}
		return keyTags;
	}

	public MQInterfaceDAO getMqInterfaceDAO() {
		return mqInterfaceDAO;
	}

	public void setMqInterfaceDAO(MQInterfaceDAO mqInterfaceDAO) {
		this.mqInterfaceDAO = mqInterfaceDAO;
	}


}
