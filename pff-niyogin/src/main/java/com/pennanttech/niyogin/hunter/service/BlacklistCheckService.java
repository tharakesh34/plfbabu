package com.pennanttech.niyogin.hunter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.niyogin.clients.JSONClient;
import com.pennanttech.niyogin.hunter.model.Address;
import com.pennanttech.niyogin.hunter.model.CustomerBasicDetail;
import com.pennanttech.niyogin.hunter.model.HunterRequest;
import com.pennanttech.niyogin.hunter.model.HunterResponse;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.BlacklistCheck;
import com.pennanttech.pff.external.service.NiyoginService;

public class BlacklistCheckService extends NiyoginService implements BlacklistCheck {

	private static final Logger	logger				= Logger.getLogger(BlacklistCheckService.class);
	private final String		extConfigFileName	= "hunter";
	private String				serviceUrl;

	/**
	 * Method for check the Hunter details of the Customer and set these details to ExtendedFieldDetails.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 */

	@Override
	public AuditHeader checkHunterDetails(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		HunterRequest hunterRequest = prepareRequestObj(financeDetail);
		JSONClient client = new JSONClient();
		Map<String, Object> validatedMap = null;
		Map<String, Object> extendedFieldMap = null;
		try {
			logger.debug("ServiceURL : " + serviceUrl);
			String jsonResponse = client.post(serviceUrl, "", hunterRequest, HunterResponse.class);
			extendedFieldMap = getExtendedMapValues(jsonResponse, extConfigFileName);

			// validate Response status
			int errorCount = Integer.parseInt(extendedFieldMap.get("ERRORCOUNT").toString());
			if (errorCount > 0) {
				throw new InterfaceException(Objects.toString(extendedFieldMap.get("ERRORCODE")),
						Objects.toString(extendedFieldMap.get("ERRORDESC")));
			} else {
				extendedFieldMap.remove("ERRORCOUNT");
				extendedFieldMap.remove("ERRORCODE");
				extendedFieldMap.remove("ERRORDESC");
				validatedMap = validateExtendedMapValues(extendedFieldMap);
			}

			logger.info("Response : " + jsonResponse);
		} catch (Exception exception) {
			logger.error("Exception: ", exception);
			throw new InterfaceException("9999", exception.getMessage());
		}
		prepareResponseObj(validatedMap, financeDetail);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * method for prepare the Hunter request object.
	 * 
	 * @param financeDetail
	 * @return hunterRequest
	 */
	private HunterRequest prepareRequestObj(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		HunterRequest hunterRequest = new HunterRequest();
		hunterRequest.setIdentifier(customer.getCustCIF());
		hunterRequest.setProductCode(financeMain.getFinCategory());
		hunterRequest.setAppDate(getAppDate());
		CustomerBasicDetail customerBasicDetail = new CustomerBasicDetail();
		StringBuilder builder = new StringBuilder();

		if (StringUtils.isNotBlank(customer.getCustFName())) {
			builder.append(customer.getCustFName());
		}
		if (StringUtils.isNotBlank(customer.getCustMName())) {
			builder.append(" ");
			builder.append(customer.getCustMName());
		}

		if (StringUtils.isNotBlank(customer.getCustLName())) {
			builder.append(" ");
			builder.append(customer.getCustLName());
		}
		if (builder.length() == 0 && StringUtils.isNotBlank(customer.getCustShrtName())) {
			builder.append(customer.getCustShrtName());
		}

		customerBasicDetail.setName(builder.toString());
		customerBasicDetail.setLoanAmount(financeMain.getFinAmount());
		customerBasicDetail.setEmailId(getHignPriorityEmail(customerDetails.getCustomerEMailList(), 5));
		long phoneNo=Long.parseLong(getHighPriorityPhone(customerDetails.getCustomerPhoneNumList(), 5));
		customerBasicDetail.setPhone(phoneNo);
		Address address = prepareAddress(getHighPriorityAddress(customerDetails.getAddressList(), 5));
		customerBasicDetail.setAddress(address);
		hunterRequest.setOrg(customerBasicDetail);
		logger.debug(Literal.LEAVING);
		return hunterRequest;
	}

	/**
	 * Method for prepare the Address request object.
	 * 
	 * @param customerAddres
	 * @return address
	 */
	private Address prepareAddress(CustomerAddres customerAddres) {
		logger.debug(Literal.ENTERING);
		Address address = new Address();

		StringBuilder stringBuilder = new StringBuilder();
		if (customerAddres.getCustAddrType() != null) {
			stringBuilder.append(customerAddres.getCustAddrType());
		}
		if (customerAddres.getCustAddrHNbr() != null) {
			stringBuilder.append(",");
			stringBuilder.append(customerAddres.getCustAddrHNbr());
		}
		if (customerAddres.getCustFlatNbr() != null) {
			stringBuilder.append(",");
			stringBuilder.append(customerAddres.getCustFlatNbr());
		}
		if (customerAddres.getCustAddrStreet() != null) {
			stringBuilder.append(",");
			stringBuilder.append(customerAddres.getCustAddrStreet());
		}
		if (customerAddres.getTypeOfResidence() != null) {
			stringBuilder.append(",");
			stringBuilder.append(customerAddres.getTypeOfResidence());
		}
		address.setAddress(stringBuilder.toString());
		address.setCity(customerAddres.getCustAddrCity());
		address.setState(customerAddres.getCustAddrProvince());
		address.setCountry(customerAddres.getCustAddrCountry());
		address.setPin(customerAddres.getCustAddrZIP());
		logger.debug(Literal.LEAVING);
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

}
