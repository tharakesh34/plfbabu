package com.pennanttech.niyogin.dedup.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.clients.JSONClient;
import com.pennanttech.niyogin.dedup.model.Address;
import com.pennanttech.niyogin.dedup.model.ExperianDedup;
import com.pennanttech.niyogin.dedup.model.Phone;
import com.pennanttech.niyogin.utility.ExperianUtility;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.ExternalDedup;
import com.pennanttech.pff.external.dao.NiyoginDAOImpl;
import com.pennanttech.pff.external.service.NiyoginService;

public class ExperianDedupService extends NiyoginService implements ExternalDedup {
	private static final Logger	logger				= Logger.getLogger(ExperianDedupService.class);

	private final String		extConfigFileName	= "experianDedup";
	private String				serviceUrl;
	private NiyoginDAOImpl		niyoginDAOImpl;
	private JSONClient			client;

	private String				status				= "SUCCESS";
	private String				errorCode			= null;
	private String				errorDesc			= null;
	private String				jsonResponse		= null;
	private Timestamp			reqSentOn			= null;

	@Override
	public AuditHeader checkDedup(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		String applicantType;

		//for Applicant
		applicantType = "A";
		ExperianDedup experianDedupRequest = prepareRequestObj(customerDetails, applicantType);
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		Map<String, Object> validatedMap = checkDedup(experianDedupRequest, finReference);
		prepareResponseObj(validatedMap, financeDetail);

		//for CoApplicant
		/*List<JointAccountDetail> coapplicants = financeDetail.getJountAccountDetailList();
		if (coapplicants != null && !coapplicants.isEmpty()) {
			applicantType = "C";
			List<Long> coApplicantIDs = new ArrayList<Long>(1);
			for (JointAccountDetail coApplicant : coapplicants) {
				coApplicantIDs.add(coApplicant.getCustID());
			}
			//TODO
			List<CustomerDetails> coApplicantCustomers = niyoginDAOImpl.getCoApplicants(coApplicantIDs, "_VIEW");
			for (CustomerDetails coAppCustomerDetails : coApplicantCustomers) {
				ExperianDedup experianDedupCoAppRequest = prepareRequestObj(coAppCustomerDetails, applicantType);
				finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
				Map<String, Object> coAppValidatedMap = checkDedup(experianDedupCoAppRequest, finReference);
				prepareResponseObj(coAppValidatedMap, financeDetail);
			}
		}*/

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public Map<String, Object> checkDedup(ExperianDedup experianDedupRequest, String finReference) {
		logger.debug(Literal.ENTERING);
		Map<String, Object> validatedMap = null;
		Map<String, Object> extendedFieldMap = null;

		// logging fields Data
		reqSentOn = new Timestamp(System.currentTimeMillis());
		
		try {
			logger.debug("ServiceURL : " + serviceUrl);
			jsonResponse = client.post(serviceUrl, experianDedupRequest);
			extendedFieldMap = getExtendedMapValues(jsonResponse, extConfigFileName);

			// error validation on Response status
			if (extendedFieldMap.get("ERRORCODE") != null) {
				errorCode = Objects.toString(extendedFieldMap.get("ERRORCODE"));
				errorDesc = Objects.toString(extendedFieldMap.get("ERRORMESSAGE"));
				throw new InterfaceException(errorCode, errorCode + ":" + errorDesc);
			} else {
				extendedFieldMap.remove("ERRORCODE");
				extendedFieldMap.remove("ERRORMESSAGE");
				validatedMap = validateExtendedMapValues(extendedFieldMap);
			}
			logger.info("Response : " + jsonResponse);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			status = "FAILED";
			errorCode = "9999";
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			errorDesc = writer.toString();
			doInterfaceLogging(experianDedupRequest, finReference);
			throw new InterfaceException("9999", e.getMessage());
		}
		
		// success case logging
		doInterfaceLogging(experianDedupRequest, finReference);
		
		logger.debug(Literal.LEAVING);
		return validatedMap;
	}

	/**
	 * Method for prepare data and logging
	 * 
	 * @param experianDedupRequest
	 * @param reference
	 */
	private void doInterfaceLogging(ExperianDedup experianDedupRequest, String reference) {
		InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, experianDedupRequest, jsonResponse,
				reqSentOn, status, errorCode, errorDesc, reference);
		logInterfaceDetails(interfaceLogDetail);
	}

	/**
	 * Method for Prepare the ExperianDedup Request object.
	 * 
	 * @param customerDetails
	 * @param applicantType
	 * @return experianDedup
	 */
	private ExperianDedup prepareRequestObj(CustomerDetails customerDetails, String applicantType) {
		logger.debug(Literal.ENTERING);
		ExperianDedup experianDedup = new ExperianDedup();
		Customer customer = customerDetails.getCustomer();
		experianDedup.setApplicantType(applicantType);
		experianDedup.setFirstName(customer.getCustFName());
		experianDedup.setLastName(customer.getCustLName());
		experianDedup.setGender(customer.getLovDescCustGenderCodeName());
		experianDedup.setDob(customer.getCustDOB());
		String pan = "";
		String aadhar = "";
		String passport = "";
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		if (documentList != null) {
			for (CustomerDocument document : documentList) {
				if (document.getCustDocCategory().equals("01")) {
					aadhar = document.getCustDocTitle();
				} else if (document.getCustDocCategory().equals("02")) {
					passport = document.getCustDocTitle();
				} else if (document.getCustDocCategory().equals("03")) {
					pan = document.getCustDocTitle();
				}
			}
		}
		experianDedup.setPan(pan);
		experianDedup.setAadhaar(aadhar);
		experianDedup.setPassport(passport);
		List<CustomerEMail> emailList = customerDetails.getCustomerEMailList();
		if (emailList != null && !emailList.isEmpty()) {
			experianDedup.setEmailId(ExperianUtility.getHignPriorityEmail(emailList, 5));
		}
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		if (addressList != null && !addressList.isEmpty()) {
			CustomerAddres customerAddres = ExperianUtility.getHighPriorityAddress(addressList, 5);
			experianDedup.setAddress(prepareAddress(customerAddres));
		} else {
			experianDedup.setAddress(new Address());
		}

		List<CustomerPhoneNumber> phoneNumberList = customerDetails.getCustomerPhoneNumList();
		if (phoneNumberList != null && !phoneNumberList.isEmpty()) {
			CustomerPhoneNumber custPhoneNumber = ExperianUtility.getHighPriorityPhone(phoneNumberList, 5);
			experianDedup.setPhone(preparePhone(custPhoneNumber));
		} else {
			experianDedup.setPhone(new Phone());
		}

		experianDedup.setLinkedin("");
		experianDedup.setFacebook("");
		experianDedup.setTwitter("");
		logger.debug(Literal.LEAVING);
		return experianDedup;
	}

	/**
	 * Method for Prepare the Address Request object.
	 * 
	 * @param customerAddres
	 * @return address
	 */
	// TODO: AddressLine1 to , landMark & CITY PRO COUNTRY
	private Address prepareAddress(CustomerAddres customerAddres) {
		logger.debug(Literal.ENTERING);
		Address address = new Address();
		address.setAddressLine1(customerAddres.getCustAddrHNbr());
		address.setAddressLine2(customerAddres.getCustAddrLine2());
		address.setAddressLine3(customerAddres.getCustAddrLine3());
		address.setLandmark(customerAddres.getCustAddrStreet());

		address.setCity(customerAddres.getLovDescCustAddrCityName());
		address.setPin(customerAddres.getCustAddrZIP());
		address.setState(customerAddres.getLovDescCustAddrProvinceName());
		address.setCountry(customerAddres.getLovDescCustAddrCountryName());
		address.setAddressType(customerAddres.getCustAddrType());
		logger.debug(Literal.LEAVING);
		return address;
	}

	/**
	 * Method for Prepare the Phone Request object;
	 * 
	 * @param phoneNumber
	 * @return phone
	 */
	private Phone preparePhone(CustomerPhoneNumber phoneNumber) {
		logger.debug(Literal.ENTERING);
		Phone phone = new Phone();
		phone.setPhoneNumber(phoneNumber.getPhoneNumber());
		phone.setPhoneType(phoneNumber.getPhoneTypeCode());
		logger.debug(Literal.LEAVING);
		return phone;
	}

	/**
	 * Method for prepare the Extended Field details map according to the given response.
	 * 
	 * @param extendedResMapObject
	 * @param financeDetail
	 */
	private void prepareResponseObj(Map<String, Object> extendedResMapObject, FinanceDetail financeDetail) {
		if (extendedResMapObject != null) {
			Map<String, Object> extendedMapObject = financeDetail.getExtendedFieldRender().getMapValues();
			if (extendedMapObject == null) {
				extendedMapObject = new HashMap<String, Object>();
			}
			for (Entry<String, Object> entry : extendedResMapObject.entrySet()) {
				extendedMapObject.put(entry.getKey(), entry.getValue());
			}
			financeDetail.getExtendedFieldRender().setMapValues(extendedMapObject);
		}
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public void setNiyoginDAOImpl(NiyoginDAOImpl niyoginDAOImpl) {
		this.niyoginDAOImpl = niyoginDAOImpl;
	}
	
	public void setClient(JSONClient client) {
		this.client = client;
	}
}
