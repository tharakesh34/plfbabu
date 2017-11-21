package com.pennanttech.niyogin.dedup.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.clients.JSONClient;
import com.pennanttech.niyogin.dedup.model.Address;
import com.pennanttech.niyogin.dedup.model.ExperianDedup;
import com.pennanttech.niyogin.dedup.model.Phone;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.ExternalDedup;
import com.pennanttech.pff.external.service.NiyoginService;

public class ExperianDedupService extends NiyoginService implements ExternalDedup {
	private static final Logger logger = Logger.getLogger(ExperianDedupService.class);

	// Published API service name.
	private final String serviceName = "DedupService";
	private final String extConfigFileName = "experianDedup";
	
	@Override
	public AuditHeader checkDedup(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();

		String applicantType = "A";
		ExperianDedup experianDedupRequest = prepareRequestObj(customerDetails, applicantType);

		String serviceURL = "";//(String) getSMTParameter("EXPERIAN_DEDUP_REQUEST_URL", String.class);
		JSONClient client = new JSONClient();
		try {
			logger.debug("ServiceURL : " + serviceURL);
			//String jsonResponse = client.post(serviceURL, serviceName, experianDedupRequest, ExperianDedup.class);
			//Map<String, Object> extendedMapObject = getExtendedMapValues(jsonResponse, extConfigFileName);
			//financeDetail.getExtendedFieldRender().setMapValues(extendedMapObject);
			//ServiceTaskDetail;
			//String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
			//logServiceTaskDetails(prepareTaskDetail(customerDetails, finReference));
			//logger.info("Response : " + jsonResponse);
		} catch (Exception exception) {
			logger.error("Exception: ", exception);
			throw new InterfaceException("9999", exception.getMessage());
		}
		
		// Method for prepare the extendedField details with dedup response
		//prepareResponseObject(experianDedupResponse, customerDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private Object prepareTaskDetail() {
		//logServiceTaskDetails
		return null;
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
		experianDedup.setGender(customer.getCustGenderCode());
		experianDedup.setDob(customer.getCustDOB());
		String pan = "";
		String aadhar = "";
		String passport = "";
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		for (CustomerDocument document : documentList) {
			if (document.getCustDocCategory().equals("01")) {
				aadhar = document.getCustDocTitle();
			} else if (document.getCustDocCategory().equals("02")) {
				passport = document.getCustDocTitle();
			} else if (document.getCustDocCategory().equals("03")) {
				pan = document.getCustDocTitle();
			}
		}
		experianDedup.setPan(pan);
		experianDedup.setAadhaar(aadhar);
		experianDedup.setPassport(passport);

		experianDedup.setEmailId(getHignPriorityEmail(customerDetails.getCustomerEMailList(), 5));
		CustomerAddres customerAddres = getHighPriorityAddress(customerDetails.getAddressList(), 5);
		if (customerAddres != null) {
			experianDedup.setAddress(prepareAddress(customerAddres));
		}
		CustomerPhoneNumber customerPhoneNumber = getHighPriorityPhone(customerDetails.getCustomerPhoneNumList(), 5);
		if (customerPhoneNumber != null) {
			experianDedup.setPhone(preparePhone(customerPhoneNumber));
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
	// TODO: AddressLine1 to 3 &landMark
	private Address prepareAddress(CustomerAddres customerAddres) {
		logger.debug(Literal.ENTERING);
		Address address = new Address();
		address.setAddressLine1(customerAddres.getCustAddrLine1());
		address.setAddressLine2(customerAddres.getCustAddrLine2());
		address.setAddressLine3(customerAddres.getCustAddrLine3());
		address.setLandmark("");

		address.setCity(customerAddres.getCustAddrCity());
		address.setPin(customerAddres.getCustAddrZIP());
		address.setState(customerAddres.getCustAddrProvince());
		address.setCountry(customerAddres.getCustAddrCountry());
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
	 * Method to get the High priority email.
	 * 
	 * @param customerEMailList
	 * @param priority
	 * @return String EmailId
	 */
	private String getHignPriorityEmail(List<CustomerEMail> customerEMailList, int priority) {
		for (CustomerEMail customerEMail : customerEMailList) {
			if (customerEMail.getCustEMailPriority() == priority) {
				return customerEMail.getCustEMail();
			}
		}
		if (priority > 1) {
			getHignPriorityEmail(customerEMailList, priority - 1);
		}
		return null;
	}

	/**
	 * Method to get the High priority PhoneNumeber
	 * 
	 * @param customerPhoneNumList
	 * @param priority
	 * @return CustomerPhoneNumber
	 */
	private CustomerPhoneNumber getHighPriorityPhone(List<CustomerPhoneNumber> customerPhoneNumList, int priority) {
		for (CustomerPhoneNumber customerPhoneNumber : customerPhoneNumList) {
			if (customerPhoneNumber.getPhoneTypePriority() == priority) {
				return customerPhoneNumber;
			}
		}
		if (priority > 1) {
			getHighPriorityPhone(customerPhoneNumList, priority - 1);
		}
		return null;
	}

	/**
	 * Method to get the High Priority Address.
	 * 
	 * @param addressList
	 * @param priority
	 * @return
	 */
	private CustomerAddres getHighPriorityAddress(List<CustomerAddres> addressList, int priority) {
		for (CustomerAddres customerAddres : addressList) {
			if (customerAddres.getCustAddrPriority() == priority) {
				return customerAddres;
			}
		}
		if (priority > 1) {
			getHighPriorityAddress(addressList, priority - 1);
		}
		return null;
	}

	/**
	 * Method to set customer experian dedup response values into extended fields.
	 * 
	 * @param expDedupRes
	 * @param customerDetails
	 * @return Map<String, Object>
	 */
	private Map<String, Object> prepareResponseObject(ExperianDedup expDedupRes, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		
		Map<String, Object> extendedValues = customerDetails.getExtendedFieldRender().getMapValues();
		
		logger.debug(Literal.LEAVING);

		return extendedValues;
	}
}
