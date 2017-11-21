package com.pennanttech.niyogin.hunter.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.clients.JSONClient;
import com.pennanttech.niyogin.hunter.model.Address;
import com.pennanttech.niyogin.hunter.model.HunterRequest;
import com.pennanttech.niyogin.hunter.model.HunterResponse;
import com.pennanttech.niyogin.hunter.model.Org;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.BlacklistCheck;
import com.pennanttech.pff.external.service.NiyoginService;

public class BlacklistCheckService extends NiyoginService implements BlacklistCheck {

	private static final Logger logger = Logger.getLogger(BlacklistCheckService.class);

	@Override
	public AuditHeader checkHunterDetails(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		long identifier = 0;
		HunterRequest hunterRequest = prepareRequestObj(financeDetail, identifier);
		HunterResponse hunterResponse = null;

		// TODO: End point URL and service name to be received
		//String serviceURL="https://soadev.niyogin.in/gates/1.0/sweeps/doOnlineMatching";
		String serviceURL = (String) getSMTParameter("EXPERIAN_HUNTER_REQUEST_URL", String.class);
		JSONClient client = new JSONClient();
		try {
			logger.debug("ServiceURL : " + serviceURL);
			hunterResponse = (HunterResponse) client.postProcess(serviceURL, "HunterService", hunterRequest,
					HunterResponse.class);
			logger.info("Response : " + hunterResponse.toString());
		} catch (Exception exception) {
			logger.error("Exception: ", exception);
			throw new InterfaceException("9999", exception.getMessage());
		}
		// set the Dat into ExtendedFields
		AuditHeader resAuditHeader = prepareAuditHeader(hunterResponse, auditHeader);

		logger.debug(Literal.LEAVING);
		return resAuditHeader;
	}

	private HunterRequest prepareRequestObj(FinanceDetail financeDetail, long identifier) {
		logger.debug(Literal.ENTERING);
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		HunterRequest hunterRequest = new HunterRequest();
		hunterRequest.setIdentifier(identifier);
		hunterRequest.setProductCode(financeDetail.getFinScheduleData().getFinanceMain().getFinCategory());
		Org org = new Org();
		// TODO:ADD These Fields
		// hunterRequest.setAppDate(appDate);
		// org.setName(name);
		// org.setLoanAmount(loanAmount);

		org.setEmailId(getHignPriorityEmail(customerDetails.getCustomerEMailList(), 5));
		long phoneNo = Long.parseLong(getHighPriorityPhone(customerDetails.getCustomerPhoneNumList(), 5));
		org.setPhone(phoneNo);
		Address address = prepareAddress(getHighPriorityAddress(customerDetails.getAddressList(), 5));
		if (address != null) {
			org.setAddress(address);
		}
		logger.debug(Literal.LEAVING);
		return hunterRequest;
	}

	private Address prepareAddress(CustomerAddres customerAddres) {
		Address address = new Address();
		// TODO: ADD CUSTOMER ADDRESS
		// address.setAddress(address);
		address.setCity(customerAddres.getCustAddrCity());
		address.setState(customerAddres.getCustAddrProvince());
		address.setCountry(customerAddres.getCustAddrCountry());
		address.setPin(customerAddres.getCustAddrZIP());
		return address;
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
	 * @return String CustomerPhoneNumber
	 */
	private String getHighPriorityPhone(List<CustomerPhoneNumber> customerPhoneNumList, int priority) {
		for (CustomerPhoneNumber customerPhoneNumber : customerPhoneNumList) {
			if (customerPhoneNumber.getPhoneTypePriority() == priority) {
				return customerPhoneNumber.getPhoneNumber();
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
	 * @return CustomerAddres
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

	private AuditHeader prepareAuditHeader(HunterResponse hunterResponse, AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
		return null;
	}
}
