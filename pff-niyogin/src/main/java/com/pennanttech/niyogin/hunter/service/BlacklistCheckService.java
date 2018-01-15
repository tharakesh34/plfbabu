package com.pennanttech.niyogin.hunter.service;

import java.sql.Timestamp;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.hunter.model.Address;
import com.pennanttech.niyogin.hunter.model.CustomerBasicDetail;
import com.pennanttech.niyogin.hunter.model.HunterRequest;
import com.pennanttech.niyogin.utility.ExtFieldMapConstants;
import com.pennanttech.niyogin.utility.NiyoginUtility;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
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
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String finReference = financeMain.getFinReference();
		HunterRequest hunterRequest = prepareRequestObj(financeDetail);
		Map<String, Object> validatedMap = null;
		Map<String, Object> extendedFieldMap = null;

		// logging fields Data
		reqSentOn = new Timestamp(System.currentTimeMillis());
		reference = finReference;

		try {
			extendedFieldMap = post(serviceUrl, hunterRequest, extConfigFileName);
		} catch (InterfaceException e) {
			financeMain.setHunterGo(false);
			throw new InterfaceException(e.getErrorCode(), e.getErrorMessage());
		}

		extendedFieldMap.put(ExtFieldMapConstants.HUNTER_REQ_SEND, true);

		try {
			validatedMap = validateExtendedMapValues(extendedFieldMap);
			if (validatedMap != null && validatedMap.isEmpty()) {
				validatedMap.put("REASONCODEHUNTER", statusCode);
				validatedMap.put("REMARKSHUNTER", App.getLabel("niyogin_No_Data"));
				validatedMap.put("HUNTREQSEND", true);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			financeMain.setHunterGo(false);
			doLogError(e, serviceUrl, hunterRequest);
			throw new InterfaceException("9999", e.getMessage());
		}

		if ((Boolean) validatedMap.get(ExtFieldMapConstants.HUNTER_MATCH)) {
			financeMain.setHunterGo(false);
		} else {
			financeMain.setHunterGo(true);
		}

		// success case logging
		doInterfaceLogging(hunterRequest, finReference);

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
		hunterRequest.setProductCode(financeMain.getFinType());
		hunterRequest.setAppDate(getAppDate());
		CustomerBasicDetail customerBasicDetail = new CustomerBasicDetail();

		customerBasicDetail.setName(customer.getCustShrtName());
		customerBasicDetail.setLoanAmount(financeMain.getFinAmount());

		if (customerDetails.getCustomerEMailList() != null) {
			customerBasicDetail.setEmailId(NiyoginUtility.getEmail(customerDetails.getCustomerEMailList()));
		}

		if (customerDetails.getCustomerPhoneNumList() != null) {
			customerBasicDetail.setPhone(NiyoginUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList(),
					InterfaceConstants.PHONE_TYPE_PER));
		}

		Address address = null;
		if (customerDetails.getAddressList() != null) {
			address = prepareAddress(NiyoginUtility.getAddress(customerDetails.getAddressList()));
		} else {
			address = new Address();
		}
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
	 * Method for prepare data and logging
	 * 
	 * @param hunterRequest
	 * @param reference
	 */
	private void doInterfaceLogging(HunterRequest hunterRequest, String reference) {
		InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, hunterRequest, jsonResponse, reqSentOn,
				status, errorCode, errorDesc, reference);
		logInterfaceDetails(interfaceLogDetail);
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
