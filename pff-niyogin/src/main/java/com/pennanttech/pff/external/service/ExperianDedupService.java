package com.pennanttech.pff.external.service;

import java.util.List;

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
import com.pennanttech.niyogin.model.dedup.Address;
import com.pennanttech.niyogin.model.dedup.ExperianDedup;
import com.pennanttech.niyogin.model.dedup.Phone;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.ExternalDedup;

public class ExperianDedupService extends NiyoginService implements ExternalDedup {
	private static final Logger logger = Logger.getLogger(ExperianDedupService.class);

	@Override
	public AuditHeader checkDedup(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();

		// TODO for loop here for Applicant and CoApplicant
		String applicantType = null;

		ExperianDedup experianDedupRequest = prepareRequestObj(customerDetails, applicantType);
		ExperianDedup experianDedupResponse = null;

		// TODO: End point URL and service name to be received
		String serviceURL = (String) getSMTParameter("EXPERIAN_DEDUP_REQUEST_URL", String.class);
		JSONClient client = new JSONClient();
		try {
			logger.debug("ServiceURL : " + serviceURL);
			experianDedupResponse = (ExperianDedup) client.postProcess(serviceURL, "DedupService", experianDedupRequest,
					ExperianDedup.class);
			logger.info("Response : " + experianDedupResponse.toString());
		} catch (Exception exception) {
			logger.error("Exception: ", exception);
			throw new InterfaceException("9999", exception.getMessage());
		}
		// set the Dat into ExtendedFields
		AuditHeader resAuditHeader = prepareAuditHeader(experianDedupResponse, auditHeader);

		logger.debug(Literal.LEAVING);
		return resAuditHeader;
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

	// set Data to the Audit Header
	private AuditHeader prepareAuditHeader(ExperianDedup experianDedupResponse, AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}
}
