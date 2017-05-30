package com.pennant.pff.channelsinterface;

import java.util.ArrayList;
import java.util.List;






import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang.StringUtils;
import org.jaxen.JaxenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pennant.interfaces.model.CoreCustomer;
import com.pennant.interfaces.model.CustEmployeeDetail;
import com.pennant.interfaces.model.Customer;
import com.pennant.interfaces.model.CustomerAddres;
import com.pennant.interfaces.model.CustomerDetails;
import com.pennant.interfaces.model.CustomerDocument;
import com.pennant.interfaces.model.CustomerEMail;
import com.pennant.interfaces.model.CustomerPhoneNumber;
import com.pennant.pff.interfaces.util.FinanceConstants;
import com.pennant.pff.interfaces.util.XmlUtil;


enum ADDRESSTYPES {
	RETAILRESIDENCE,RETAILOFFICE, RETAILHOMECOUNTRY, SMEESTMAIN, SMEESTOTHER, PHONECODES
};

public class FetchCustomerInfo {

	final Logger logger = LoggerFactory.getLogger(FetchCustomerInfo.class);


	public CustomerDetails getCustomerInfo(OMElement customerElement, PFFDataAccess pffDataAccess) throws Exception {
		logger.debug("Entering");

		if (customerElement == null) {
			return null;
		}

		String updateCIFRoot = "/HB_EAI_REQUEST/Request/updateCIFRetailRequest/";

		CustomerDetails customerDetails = new CustomerDetails();

		String custCIF = XmlUtil.getStringValue(customerElement, true, true, "CIF", updateCIFRoot);
		customerDetails.setCustCIF(custCIF);

		// Customer Details information
		Customer customer = new Customer();
		customer.setCustCIF(custCIF);

		customer.setCustCtgCode("RETAIL");
		OMElement custDetailElement = customerElement.getFirstChildWithName(new QName("PersonalInfo"));
		customer = setCustomerDetails(custDetailElement, customer);

		customer.setCustCoreBank(custCIF);
		if (customer.getCustSegment() != null) {
			customer.setCustAddlVar82(pffDataAccess.getMDMCode(customer.getCustSegment(), "MCM_BMTSegments"));
			customer.setCustSegment(pffDataAccess.getMDMCode(customer.getCustSegment(), "MCM_BMTSegments"));
		}

		if (customer.getCustTypeCode() != null) {
			customer.setCustTypeCode(pffDataAccess.getMDMCode(customer.getCustTypeCode(), "MCM_RMTCUSTTYPES"));
		}
		if (customer.getCustLng() != null) {
			customer.setCustLng(pffDataAccess.getMDMCode(customer.getCustLng(), "MCM_BMTLANGUAGE"));
		}
		if (customer.getCustSalutationCode() != null) {
			customer.setCustSalutationCode(pffDataAccess.getMDMCode(customer.getCustSalutationCode(), "MCM_BMTSALUTATIONS"));
		}
		if (customer.getCustIndustry() != null) {
			customer.setCustIndustry(pffDataAccess.getMDMCode(customer.getCustIndustry(), "mcm_bmtindustries"));
		}
		if (customer.getCustSector() != null) {
			customer.setCustSector(pffDataAccess.getMDMCode(customer.getCustSector(), "mcm_bmtsectors"));
		}

		if (customer.getCustDftBranch() != null) {
			customer.setCustDftBranch(pffDataAccess.getMDMCode(customer.getCustDftBranch(), "mcm_rmtbranches"));
		}
		if (customer.getCustNationality() != null) {
			customer.setCustNationality(pffDataAccess.getMDMCode(customer.getCustNationality(), "mcm_bmtcountries"));
		}

		if (customer.getCustMaritalSts() != null) {
			customer.setCustMaritalSts(pffDataAccess.getMDMCode(customer.getCustMaritalSts(), "MCM_BMTMARITALSTATUSCODES"));
		}

		if (customer.getCustCOB() != null) {
			customer.setCustCOB(pffDataAccess.getMDMCode(customer.getCustCOB(), "mcm_bmtcountries"));
		}

		OMElement addrElement = customerElement.getFirstChildWithName(new QName("AddressInfo"));

		// Address Details information
		customerDetails.setAddressList(setCustomerAddress(addrElement, custCIF, pffDataAccess));

		// Phone number Details information
		customerDetails.setCustomerPhoneNumList(setCustomerPhoneNumber(addrElement, custCIF));

		// Email Details information
		customerDetails.setCustomerEMailList(setCustomerEmail(addrElement, custCIF));

		// Customer Document Details information
		OMElement custDocDetailElement = customerElement.getFirstChildWithName(new QName("DocumentDetails"));
		customerDetails.setCustomerDocumentsList(setCustomerdocuments(custDocDetailElement,custCIF));

		CoreCustomer coreCustomer  = new CoreCustomer();
		coreCustomer.setNewRecord(true);

		// Customer Employee Details information
		OMElement employmentInfoElement = customerElement.getFirstChildWithName(new QName("EmploymentInfo"));
		customerDetails.setCustEmployeeDetail(setEmploymentInfo(employmentInfoElement, coreCustomer));

		// Indemity FAX Service
		List<CustomerPhoneNumber> custPhoneList = setIndemityFaxInfo(customerElement.getFirstChildWithName(new QName("Indemity")), custCIF);
		if(custPhoneList != null) {
			if (customerDetails.getCustomerPhoneNumList() != null && !customerDetails.getCustomerPhoneNumList().isEmpty()) {
				customerDetails.getCustomerPhoneNumList().addAll(custPhoneList);
			} else {
				customerDetails.setCustomerPhoneNumList(custPhoneList);
			}
		}

		// Indemity EMail Service
		List<CustomerEMail> custEmailList = setIndemityEmailInfo(customerElement.getFirstChildWithName(new QName("Indemity")), custCIF);
		if(custEmailList != null) {
			if (customerDetails.getCustomerEMailList() != null && !customerDetails.getCustomerEMailList().isEmpty()) {
				customerDetails.getCustomerEMailList().addAll(custEmailList);
			} else {
				customerDetails.setCustomerEMailList(custEmailList);
			}
		}

		// Set whole customer information to List
		customerDetails.setCustomer(customer);

		// SMS Service
		List<CustomerPhoneNumber> smsPhoneList = setSMSServiceInfo(customerElement.getFirstChildWithName(new QName("SmsServices")), custCIF);
		if(smsPhoneList != null) {
			if(customerDetails.getCustomerPhoneNumList() != null && !customerDetails.getCustomerPhoneNumList().isEmpty()){
				customerDetails.getCustomerPhoneNumList().addAll(smsPhoneList);
			} else {
				customerDetails.setCustomerPhoneNumList(smsPhoneList);
			}
		}
		// POA Service
		coreCustomer = setPOAServiceInfo(customerElement.getFirstChildWithName(new QName("PowerOfAttorney")), coreCustomer);

		// Rating Service
		coreCustomer = setRatingServiceInfo(customerElement.getFirstChildWithName(new QName("Rating")), coreCustomer);

		// Relational Details
		coreCustomer = setRelationDetails(customerElement.getFirstChildWithName(new QName("RelationDetails")),coreCustomer);

		// KYC Details
		coreCustomer = setKYCDetails(customerElement.getFirstChildWithName(new QName("KYC")),coreCustomer);

		customerDetails.setCoreCustomer(coreCustomer);

		logger.debug("Leaving");
		return customerDetails;
	}

	/**
	 * 
	 * @param employmentInfoElement
	 * @param coreCustomer 
	 * @return
	 */
	private CustEmployeeDetail setEmploymentInfo(OMElement employmentInfoElement, CoreCustomer coreCustomer) {
		logger.debug("Entering");

		if (employmentInfoElement == null) {
			return null;
		}

		CustEmployeeDetail customerEmployeeDetail = new CustEmployeeDetail();

		customerEmployeeDetail.setEmpStatus(XmlUtil.getStringValue(employmentInfoElement, "EmpStatus"));
		customerEmployeeDetail.setEmpName(0);//PFFXmlUtil.getStringValue(employmentInfoElement, "EmpName")
		customerEmployeeDetail.setEmpDesg(XmlUtil.getStringValue(employmentInfoElement, "Occupation"));
		customerEmployeeDetail.setEmpDept(XmlUtil.getStringValue(employmentInfoElement, "Department"));
		customerEmployeeDetail.setEmpFrom(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(
				employmentInfoElement, "EmpStartDate"), "dd/MM/yyyy"));
		customerEmployeeDetail.setMonthlyIncome(XmlUtil.getBigDecimalValue(employmentInfoElement, "Salary"));

		//Not Available fields saved in CoreCustomer Object
		coreCustomer.setSalaryCurrency(XmlUtil.getStringValue(employmentInfoElement, "SalaryCurrency"));
		coreCustomer.setSalary(XmlUtil.getBigDecimalValue(employmentInfoElement, "Salary"));
		coreCustomer.setSalaryDateFreq(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(employmentInfoElement, 
				"SalaryDateFreq"), "dd/MM/yyyy"));
		coreCustomer.setBusinessType(XmlUtil.getStringValue(employmentInfoElement, "BusinessType"));
		coreCustomer.setNameOfBusiness(XmlUtil.getStringValue(employmentInfoElement, "NameOfBusiness"));

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
	private List<CustomerDocument> setCustomerdocuments(OMElement custDocDetailElement, String custCIF) {
		logger.debug("Entering");

		if (custDocDetailElement == null) {
			return null;
		}

		List<CustomerDocument> docList = new ArrayList<CustomerDocument>();

		// custDocDetailElement.getFirstChildWithName(new QName("IDType")).getText();
		docList.addAll(setCustDocumentsByType(custDocDetailElement,	"EmiratesID", FinanceConstants.getDocEmiratesId(), custCIF));
		docList.addAll(setCustDocumentsByType(custDocDetailElement,	"Passport", FinanceConstants.getDocPassportId(), custCIF));
		docList.addAll(setCustDocumentsByType(custDocDetailElement,	"ResidenceVisa", FinanceConstants.getDocResidenceVisa(), custCIF));
		docList.addAll(setCustDocumentsByType(custDocDetailElement,	"USID", FinanceConstants.getDocUSID(), custCIF));

		logger.debug("Leaving");

		return docList;
	}

	private List<CustomerDocument> setCustDocumentsByType(OMElement custDocDetailElement, String docType, String[] idType, String custCIF) {
		logger.debug("Entering");

		List<CustomerDocument> docList = new ArrayList<CustomerDocument>();
		CustomerDocument customerDocuments = new CustomerDocument();
		String docID;

		switch (docType) {
		case "EmiratesID":
			docID = docType + "No";
			customerDocuments.setCustDocIssuedOn(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(custDocDetailElement, docType + "IssueDate"),
					"dd/MM/yyyy"));
			customerDocuments.setCustDocExpDate(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(custDocDetailElement, "UaeIDExpDate"),
					"dd/MM/yyyy"));

			break;
		case "ResidenceVisa":
			docID = docType + "No";
			customerDocuments.setCustDocIssuedOn(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(custDocDetailElement, docType + "IssueDate"),
					"dd/MM/yyyy"));
			customerDocuments.setCustDocExpDate(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(custDocDetailElement, docType + "ExpDate"),
					"dd/MM/yyyy"));

			break;
		case "USID":
			docID = docType + "No";
			break;

		default:
			docID = docType + "Number";
			customerDocuments.setCustDocIssuedOn(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(custDocDetailElement, docType + "IssueDate"),
					"dd/MM/yyyy"));
			customerDocuments.setCustDocExpDate(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(custDocDetailElement, docType + "ExpDate"),
					"dd/MM/yyyy"));

			break;
		}
		customerDocuments.setLovDescCustCIF(custCIF);
		customerDocuments.setCustDocCategory(idType[0]);
		customerDocuments.setLovDescCustDocCategory(idType[1]);
		customerDocuments.setCustDocTitle(XmlUtil.getStringValue(custDocDetailElement, docID));

		if(StringUtils.isNotBlank(customerDocuments.getCustDocTitle())) {
			docList.add(customerDocuments);
		}
		logger.debug("Leaving");

		return docList;
	}

	/**
	 * 
	 * @param addrElement
	 * @param custType
	 * @return
	 */
	private List<CustomerEMail> setCustomerEmail(OMElement addrElement, String custCIF) {
		logger.debug("Entering");

		if (addrElement == null) {
			return null;
		}

		List<CustomerEMail> emailList = new ArrayList<CustomerEMail>();


		String path = "/HB_EAI_REPLY/Reply/getCustomerDetailsReply/Retail/AddressInfo/";
		OMElement ofceAddElement = addrElement.getFirstChildWithName(new QName("OfficeAddress"));
		OMElement resAddElement = addrElement.getFirstChildWithName(new QName("ResidenceAddress"));

		try {
			if (ofceAddElement != null) {
				String parentTag = "OfficeAddress/";
				emailList.addAll(setCustomerEmailByType(ofceAddElement, path+ parentTag + "OfficeEmailAddresses/",
						"OfficeEmailAddress",FinanceConstants.getPersonalEmail(), custCIF));
			}

			if (resAddElement != null) {
				String parentTag = "ResidenceAddress/";
				emailList.addAll(setCustomerEmailByType(resAddElement, path + parentTag + "ResidenceEmailAddresses/",
						"ResidenceEmailAddress", FinanceConstants.getResidenceEmail(), custCIF));
			}

		} catch (Exception ex) {
			logger.error(ex.getMessage());
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
	 * @throws Exception
	 */
	private List<CustomerEMail> setCustomerEmailByType(OMElement element, String path, String tagname, String[] type, String custCIF)
			throws JaxenException {
		logger.debug("Entering");

		if (element == null) {
			return null;
		}

		List<CustomerEMail> emailList = new ArrayList<CustomerEMail>();
		AXIOMXPath xpath = new AXIOMXPath(path + tagname);

		if (xpath != null) {
			@SuppressWarnings("unchecked")
			List<OMElement> custEmailList = (List<OMElement>) xpath.selectNodes(element);
			for (OMElement omElement : custEmailList) {
				CustomerEMail custEmail = new CustomerEMail();
				custEmail.setLovDescCustCIF(custCIF);
				custEmail.setCustEMailTypeCode(type[0]);
				custEmail.setLovDescCustEMailTypeCode(type[1]);
				custEmail.setCustEMail(omElement.getText());
				emailList.add(custEmail);
			}
		}
		logger.debug("Leaving");

		return emailList;
	}

	/**
	 * Get Customer Phone numbers based on the type
	 * 
	 * @param addrElement
	 * @param custType
	 * @return
	 */
	private List<CustomerPhoneNumber> setCustomerPhoneNumber(OMElement addrElement, String custCIF) {
		logger.debug("Entering");

		if (addrElement == null) {
			return null;
		}

		List<CustomerPhoneNumber> phoneList = new ArrayList<CustomerPhoneNumber>();


		String path = "/HB_EAI_REQUEST/Request/updateCIFRetailRequest/AddressInfo/";
		OMElement ofceAddElement = addrElement.getFirstChildWithName(new QName("OfficeAddress"));
		OMElement resAddElement = addrElement.getFirstChildWithName(new QName("ResidenceAddress"));
		OMElement hcAddElement = addrElement.getFirstChildWithName(new QName("HomeCountryAddress"));

		try {
			if (ofceAddElement != null) {
				String parentTag = "OfficeAddress/";
				phoneList = setCustomerPhoneNumberByType(ofceAddElement, path + parentTag+ "OfficePhoneNumbers/OfficePhoneNo",
						FinanceConstants.OFFICEPHONENO, phoneList, custCIF);
				phoneList = setCustomerPhoneNumberByType(ofceAddElement, path + parentTag+ "OfficeFaxNumbers/OfficeFaxNo",
						FinanceConstants.OFFICEFAXNO, phoneList, custCIF);
				phoneList = setCustomerPhoneNumberByType(ofceAddElement, path + parentTag+ "OfficeMobileNumbers/OfficeMobileNo",
						FinanceConstants.OFFICEMOBILENO, phoneList, custCIF);
			}

			if (resAddElement != null) {
				String parentTag = "ResidenceAddress/";
				phoneList = setCustomerPhoneNumberByType(resAddElement,  path + parentTag+ "ResidencePhoneNumbers/ResidencePhoneNo",
						FinanceConstants.RESIDENCEPHONENO, phoneList, custCIF);
				phoneList = setCustomerPhoneNumberByType(resAddElement,  path + parentTag+ "ResidenceFaxNumbers/ResidenceFaxNo",
						FinanceConstants.RESIDENCEFAXNO, phoneList, custCIF);
				phoneList = setCustomerPhoneNumberByType(resAddElement,	 path + parentTag+ "ResidenceMobileNumbers/ResidenceMobileNo",
						FinanceConstants.RESIDENCEMOBILENO, phoneList, custCIF);
			}

			if (hcAddElement != null) {
				String parentTag = "HomeCountryAddress/";
				phoneList = setCustomerPhoneNumberByType(hcAddElement, path + parentTag + "HCPhoneNumbers/HCPhoneNo", 
						FinanceConstants.HCPHONENO, phoneList, custCIF);
				phoneList = setCustomerPhoneNumberByType(hcAddElement,	path + parentTag + "HCFaxNumbers/HCFaxNo", 
						FinanceConstants.HCFAXNO, phoneList, custCIF);
				phoneList = setCustomerPhoneNumberByType(hcAddElement,	path + parentTag + "HCMobileNumbers/HCMobileNo", 
						FinanceConstants.HCMOBILENO, phoneList, custCIF);
			}
		} catch (JaxenException je) {
			logger.error(je.getMessage());
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
	private List<CustomerPhoneNumber> setCustomerPhoneNumberByType(OMElement element, String path, String type, 
			List<CustomerPhoneNumber> phoneList, String custCIF) throws JaxenException {
		logger.debug("Entering");

		if (element == null) {
			return phoneList;
		}

		AXIOMXPath xpath = new AXIOMXPath(path);

		if (xpath != null) {
			@SuppressWarnings("unchecked")
			List<OMElement> custPhoneDetailList = (List<OMElement>) xpath.selectNodes(element);
			for (OMElement omElement : custPhoneDetailList) {
				CustomerPhoneNumber custPhoneNum = new CustomerPhoneNumber();
				custPhoneNum.setLovDescCustCIF(custCIF);
				custPhoneNum.setPhoneTypeCode(type);
				custPhoneNum.setPhoneCountryCode(XmlUtil.getStringValue(omElement, "CountryCode"));
				custPhoneNum.setPhoneAreaCode(XmlUtil.getStringValue(omElement, "AreaCode"));
				custPhoneNum.setPhoneNumber(XmlUtil.getStringValue(omElement, "SubsidiaryNumber"));
				phoneList.add(custPhoneNum);
			}
		}
		logger.debug("Leaving");

		return phoneList;
	}

	/**
	 * Prepare Indemity Service VO class
	 * 
	 * @param indemityInfoElement
	 */
	private List<CustomerPhoneNumber> setIndemityFaxInfo(OMElement indemityInfoElement, String custCIF) {
		logger.debug("Entering");

		if (indemityInfoElement == null) {
			return null;
		}

		List<CustomerPhoneNumber> phoneList = new ArrayList<CustomerPhoneNumber>();
		CustomerPhoneNumber custSMSPhoneNum = new CustomerPhoneNumber();

		custSMSPhoneNum.setPhoneTypeCode(FinanceConstants.FAXINDEMITY);
		OMElement indemityElemet = indemityInfoElement.getFirstChildWithName(new QName("FaxNumber"));
		custSMSPhoneNum.setLovDescCustCIF(custCIF);
		custSMSPhoneNum.setPhoneCountryCode(XmlUtil.getStringValue(indemityElemet, "CountryCode"));
		custSMSPhoneNum.setPhoneAreaCode(XmlUtil.getStringValue(indemityElemet, "AreaCode"));
		custSMSPhoneNum.setPhoneNumber(XmlUtil.getStringValue(indemityElemet, "SubsidiaryNumber"));
		phoneList.add(custSMSPhoneNum);

		logger.debug("Leaving");
		return phoneList;
	}

	private List<CustomerEMail> setIndemityEmailInfo(OMElement indemEmailElement, String custCIF) {
		logger.debug("Entering");

		if (indemEmailElement == null) {
			return null;
		}

		List<CustomerEMail> emailList = new ArrayList<CustomerEMail>();
		CustomerEMail custEmail = new CustomerEMail();
		custEmail.setLovDescCustCIF(custCIF);
		custEmail.setCustEMailTypeCode(FinanceConstants.getIndemityEmail()[0]);
		custEmail.setLovDescCustEMailTypeCode(FinanceConstants.getIndemityEmail()[1]);
		custEmail.setCustEMail(XmlUtil.getStringValue(indemEmailElement, "EmailAddress"));
		emailList.add(custEmail);

		return emailList;
	}

	private List<CustomerAddres> setCustomerAddress(OMElement addrElement, String custCIF, PFFDataAccess pffDataAccess) {
		logger.debug("Entering");

		if (addrElement == null) {
			return null;
		}

		List<CustomerAddres> addrList = new ArrayList<CustomerAddres>();


		addrList = setCustomerAddressByType(addrElement,FinanceConstants.getRetailOfficeAddress(),
				ADDRESSTYPES.RETAILOFFICE, addrList, custCIF, pffDataAccess);
		addrList = setCustomerAddressByType(addrElement,FinanceConstants.getRetailResidenceAddress(),
				ADDRESSTYPES.RETAILRESIDENCE, addrList, custCIF,pffDataAccess);
		addrList = setCustomerAddressByType(addrElement,FinanceConstants.getRetailHomeCountryAddress(),
				ADDRESSTYPES.RETAILHOMECOUNTRY, addrList, custCIF, pffDataAccess);

		logger.debug("Leaving");

		return addrList;
	}

	/**
	 * Prepare SMS Service VO class
	 * 
	 * @param smsInfoElement
	 */
	private List<CustomerPhoneNumber> setSMSServiceInfo(OMElement smsInfoElement, String custCIF) {
		logger.debug("Entering");

		if (smsInfoElement == null) {
			return null;
		}

		List<CustomerPhoneNumber> phoneList = new ArrayList<CustomerPhoneNumber>();
		CustomerPhoneNumber custSMSPhoneNum = new CustomerPhoneNumber();

		custSMSPhoneNum.setPhoneTypeCode(FinanceConstants.SMSMOBILENO);
		OMElement smsElemet = smsInfoElement.getFirstChildWithName(new QName("SmsMobileNo"));
		custSMSPhoneNum.setLovDescCustCIF(custCIF);
		custSMSPhoneNum.setPhoneCountryCode(XmlUtil.getStringValue(smsElemet, "CountryCode"));
		custSMSPhoneNum.setPhoneAreaCode(XmlUtil.getStringValue(smsElemet, "AreaCode"));
		custSMSPhoneNum.setPhoneNumber(XmlUtil.getStringValue(smsElemet, "SubsidiaryNumber"));
		phoneList.add(custSMSPhoneNum);

		logger.debug("Leaving");
		return phoneList;
	}
	/**
	 * Method for Preparation of Customer Details
	 * @param custDetailElement
	 * @param custType
	 * @return
	 * @throws Exception 
	 */
	private Customer setCustomerDetails(OMElement custDetailElement, Customer customer) throws Exception {
		logger.debug("Entering");

		if (custDetailElement == null) {
			return null;
		}
		String personalInfoRoot = "/HB_EAI_REQUEST/Request/updateCIFRetailRequest/PersonalInfo/";

		// customer.setCustSts(PFFXmlUtil.getStringValue(custDetailElement, "CustomerStatusISO"));
		customer.setCustSegment(XmlUtil.getStringValue(custDetailElement, true, true, "SegmentISO", personalInfoRoot));
		customer.setCustTypeCode(XmlUtil.getStringValue(custDetailElement, true, true,  "CustomerStatusISO", personalInfoRoot));
		customer.setCustSalutationCode(XmlUtil.getStringValue(custDetailElement, true, true,  "TitleISO", personalInfoRoot));
		customer.setCustShrtName(XmlUtil.getStringValue(custDetailElement, true, true,  "ShortName", personalInfoRoot));
		//customer.setCustCoreBank(XmlUtil.getStringValue(custDetailElement, true, true,  "CIF", personalInfoRoot));
		customer.setCustMotherMaiden(XmlUtil.getStringValue(custDetailElement, true, true,  "MotherName", personalInfoRoot));
		customer.setCustFName(XmlUtil.getStringValue(custDetailElement, true, true,  "FirstName", personalInfoRoot));
		customer.setCustLName(XmlUtil.getStringValue(custDetailElement, true, true,  "FamilyName", personalInfoRoot));
		customer.setCustMName(XmlUtil.getStringValue(custDetailElement, true, true,  "SecondName", personalInfoRoot));
		customer.setCustDOB(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(custDetailElement, true, true,  "DateOfBirth", 
				personalInfoRoot), "dd/MM/yyyy"));
		//customer.setCustPOB(XmlUtil.getStringValue(custDetailElement, true, true,  "PlaceOfBirth", personalInfoRoot));
		customer.setCustLng(XmlUtil.getStringValue(custDetailElement, true, true,  "LanguageISO", personalInfoRoot));
		customer.setCustSector(XmlUtil.getStringValue(custDetailElement, true, true,  "SectorISO", personalInfoRoot));
		customer.setCustIndustry(XmlUtil.getStringValue(custDetailElement, true, true,  "IndustryISO", personalInfoRoot));

		customer.setCustGenderCode(XmlUtil.getStringValue(custDetailElement, true, true,  "Gender", personalInfoRoot));
		customer.setCustNationality(XmlUtil.getStringValue(custDetailElement, true, true,  "NationalityISO", personalInfoRoot));
		customer.setCustCOB(XmlUtil.getStringValue(custDetailElement, true, true,  "CountryOfbirthISO", personalInfoRoot));
		customer.setCustMaritalSts(XmlUtil.getStringValue(custDetailElement, true, true,  "MaritalStatusISO", personalInfoRoot));
		int dependents = 0;
		if(XmlUtil.getStringValue(custDetailElement,true, true, "NoOfDependents", personalInfoRoot) != null){
			dependents = Integer.parseInt(XmlUtil.getStringValue(custDetailElement, true, true,  "NoOfDependents", personalInfoRoot));
		}
		customer.setNoOfDependents(dependents);
		//customer.setCustRO1(PFFXmlUtil.getStringValue(custDetailElement, "RelationshipManagerISO"));
		customer.setCustDftBranch(XmlUtil.getStringValue(custDetailElement, true, true,  "BranchCodeISO", personalInfoRoot));
		// PFFXmlUtil.getStringValue(custDetailElement, "ResidencyType");
		// PFFXmlUtil.getStringValue(custDetailElement, "lineManager");

		logger.debug("Leaving");

		return customer;
	}

	/**
	 * Set Customer phone number details based on the type
	 * 
	 * @param addElement
	 * @param addType
	 * @return
	 * @throws Exception
	 */
	private List<CustomerAddres> setCustomerAddressByType(OMElement element, String[] addressTypes, ADDRESSTYPES type,
			List<CustomerAddres> list, String custCIF, PFFDataAccess pffDataAccess) {
		logger.debug("Entering");

		String[] keyTags = getkeyTags(type);

		if (element == null) {
			return list;
		}

		OMElement addElement = element.getFirstChildWithName(new QName(keyTags[0]));

		if (addElement == null) {
			return list;
		}

		CustomerAddres address = new CustomerAddres();

		address.setLovDescCustCIF(custCIF);
		address.setCustAddrType(addressTypes[0]);
		address.setLovDescCustAddrTypeName(addressTypes[1]);
		address.setCustPOBox(XmlUtil.getStringValue(addElement, keyTags[1]));
		address.setCustFlatNbr(XmlUtil.getStringValue(addElement, keyTags[2]));
		address.setCustAddrHNbr(XmlUtil.getStringValue(addElement, keyTags[3]));
		address.setCustAddrStreet(XmlUtil.getStringValue(addElement, keyTags[4]));
		address.setCustAddrLine1(XmlUtil.getStringValue(addElement, keyTags[5]));
		address.setCustAddrCountry(XmlUtil.getStringValue(addElement, keyTags[9]));
		if (address.getCustAddrCountry() != null) {
			address.setCustAddrCountry(pffDataAccess.getMDMCode(address.getCustAddrCountry(), "mcm_bmtcountries"));
		}
		address.setCustAddrProvince(XmlUtil.getStringValue(addElement, keyTags[6]));

		list.add(address);

		logger.debug("Leaving");

		return list;
	}

	/**
	 * Method for Preparation of KYC Details
	 * @param kycElement
	 * @param coreCustomer 
	 */
	private CoreCustomer setKYCDetails(OMElement kycElement, CoreCustomer coreCustomer) {
		logger.debug("Entering");

		if (kycElement == null || coreCustomer == null) {
			return coreCustomer;
		}

		coreCustomer.setKycRiskLevel(XmlUtil.getStringValue(kycElement, "KYCRiskLevel"));
		coreCustomer.setIntroducer(XmlUtil.getStringValue(kycElement, "Introducer"));
		coreCustomer.setReferenceName(XmlUtil.getStringValue(kycElement, "ReferenceName")); 
		coreCustomer.setPurposeOfRelationShip(XmlUtil.getStringValue(kycElement, "PurposeOfRelationShip")); 

		logger.debug("Leaving");
		return coreCustomer;
	}

	/**
	 * Method for preparation of Relation Details
	 * @param relationDetailElement
	 * @param coreCustomer 
	 */
	private CoreCustomer setRelationDetails(OMElement relationDetailElement, CoreCustomer coreCustomer) {
		logger.debug("Entering");

		if (relationDetailElement == null || coreCustomer == null) {
			return coreCustomer;
		}
		coreCustomer.setRelationCode(XmlUtil.getStringValue(relationDetailElement, "RelationCode"));
		coreCustomer.setRelationShipCIF(XmlUtil.getStringValue(relationDetailElement, "RelationShipCIF"));

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
	private CoreCustomer setRatingServiceInfo(OMElement ratingElement, CoreCustomer coreCustomer) {
		logger.debug("Entering");

		if (ratingElement == null || coreCustomer == null) {
			return coreCustomer;
		}

		coreCustomer.setInternalRating(XmlUtil.getStringValue(ratingElement, "InternalRating")); 
		coreCustomer.setDateOfInternalRating(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(ratingElement, "DateOfInternalRating"), "dd/MM/yyyy"));

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
	private CoreCustomer setPOAServiceInfo(OMElement poaInfoElement, CoreCustomer coreCustomer) {
		logger.debug("Entering");

		if (poaInfoElement == null || coreCustomer == null) {
			return coreCustomer;
		}
		coreCustomer.setpOAFlag(XmlUtil.getStringValue(poaInfoElement, "POAFlag")); 
		coreCustomer.setpOACIF(XmlUtil.getStringValue(poaInfoElement, "POACIF"));
		coreCustomer.setpOAHoldersname(XmlUtil.getStringValue(poaInfoElement, "POAHolderName")); 
		coreCustomer.setPassportNumber(XmlUtil.getStringValue(poaInfoElement, "POAPassportNumber")); 
		coreCustomer.setEmiratesIDNumber(XmlUtil.getStringValue(poaInfoElement, "POAIDNumber")); 
		coreCustomer.setNationality(XmlUtil.getStringValue(poaInfoElement, "POANationality")); 
		coreCustomer.setpOAIssuancedate(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(poaInfoElement, "POAIssuanceDate"), "dd/MM/yyyy")); 
		coreCustomer.setpOAExpirydate(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(poaInfoElement, "POAExpiryDate"), "dd/MM/yyyy"));  
		coreCustomer.setPassportExpiryDate(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(poaInfoElement, "POApassportExpiryDate"), "dd/MM/yyyy"));  
		coreCustomer.setEmiratesIDExpiryDate(XmlUtil.convertDateFromMQ(XmlUtil.getStringValue(poaInfoElement, "POAIDExpiryDate"), "dd/MM/yyyy")); 

		logger.debug("Leaving");

		return coreCustomer;
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

}
