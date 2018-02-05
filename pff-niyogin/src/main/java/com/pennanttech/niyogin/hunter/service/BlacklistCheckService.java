package com.pennanttech.niyogin.hunter.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.pennanttech.niyogin.utility.NiyoginUtility;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.BlacklistCheck;
import com.pennanttech.pff.external.service.NiyoginService;

public class BlacklistCheckService extends NiyoginService implements BlacklistCheck {

	private static final Logger	logger				= Logger.getLogger(BlacklistCheckService.class);
	private final String		extConfigFileName	= "hunter.properties";
	private String				serviceUrl;

	//Hunter
	public static final String	REQ_SEND			= "REQSENDEXPHNTR";
	public static final String	STATUSCODE			= "STATUSEXPHNTR";
	public static final String	RSN_CODE			= "REASONEXPHNTR";
	public static final String	REMARKS				= "REMARKSEXPHNTR";

	public static final String	MATCH				= "HUNTERMATCH";

	/**
	 * Method for check the Hunter details of the Customer and set these details to ExtendedFieldDetails.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 */

	@Override
	public AuditHeader checkHunterDetails(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		
		if (StringUtils.isBlank(serviceUrl)) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String reference = financeMain.getFinReference();
		Map<String, Object> appplicationdata = new HashMap<>();
		HunterRequest hunterRequest = prepareRequestObj(financeDetail);
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;

		try {
			reuestString = client.getRequestString(hunterRequest);
			jsonResponse = client.post(serviceUrl, reuestString);
			//check response for error
			errorCode = getErrorCode(jsonResponse);
			errorDesc = getErrorMessage(jsonResponse);

			doInterfaceLogging(reference, reuestString, jsonResponse, errorCode, errorDesc);

			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, getTrimmedMessage(errorDesc));
			appplicationdata.put(STATUSCODE, getStatusCode(jsonResponse));
			if (StringUtils.isEmpty(errorCode)) {
				//read values from response and load it to extended map
				Map<String, Object> mapdata = getPropValueFromResp(jsonResponse, extConfigFileName);
				Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
				//add to final
				appplicationdata.putAll(mapvalidData);

				if ((Boolean) appplicationdata.get(MATCH)) {
					financeMain.setHunterGo(false);
				} else {
					financeMain.setHunterGo(true);
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorDesc = getWriteException(e);
			errorDesc = getTrimmedMessage(errorDesc);
			doExceptioLogging(reference, reuestString, jsonResponse, errorDesc);
			//As per the clint need
			financeMain.setHunterGo(false);

			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, errorDesc);
		}
		appplicationdata.put(REQ_SEND, true);
		prepareResponseObj(appplicationdata, financeDetail);
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
		customerBasicDetail.setEmailId(NiyoginUtility.getEmail(customerDetails.getCustomerEMailList()));
		customerBasicDetail.setPhone(NiyoginUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList(),
				InterfaceConstants.PHONE_TYPE_PER));

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
		if (StringUtils.isNotBlank(customerAddres.getCustAddrType())) {
			stringBuilder.append(customerAddres.getCustAddrType());
		}
		if (StringUtils.isNotBlank(customerAddres.getCustAddrHNbr())) {
			if (StringUtils.isNotBlank(stringBuilder)) {
				stringBuilder.append(",");
			}
			stringBuilder.append(customerAddres.getCustAddrHNbr());
		}
		if (StringUtils.isNotBlank(customerAddres.getCustFlatNbr())) {
			if (StringUtils.isNotBlank(stringBuilder)) {
				stringBuilder.append(",");
			}
			stringBuilder.append(customerAddres.getCustFlatNbr());
		}
		if (StringUtils.isNotBlank(customerAddres.getCustAddrStreet())) {
			if (StringUtils.isNotBlank(stringBuilder)) {
				stringBuilder.append(",");
			}
			stringBuilder.append(customerAddres.getCustAddrStreet());
		}
		if (StringUtils.isNotBlank(customerAddres.getTypeOfResidence())) {
			if (StringUtils.isNotBlank(stringBuilder)) {
				stringBuilder.append(",");
			}
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
	 * Method for prepare Success logging
	 * 
	 * @param reference
	 * @param requets
	 * @param response
	 * @param errorCode
	 * @param errorDesc
	 */
	private void doInterfaceLogging(String reference, String requets, String response, String errorCode,
			String errorDesc) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(new Timestamp(System.currentTimeMillis()));

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(InterfaceConstants.STATUS_SUCCESS);
		iLogDetail.setErrorCode(errorCode);
		if (errorDesc != null && errorDesc.length() > 200) {
			iLogDetail.setErrorDesc(errorDesc.substring(0, 190));
		}

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for failure logging.
	 * 
	 * @param reference
	 * @param requets
	 * @param response
	 * @param errorCode
	 * @param errorDesc
	 */
	private void doExceptioLogging(String reference, String requets, String response, String errorDesc) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(new Timestamp(System.currentTimeMillis()));

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(InterfaceConstants.STATUS_FAILED);
		iLogDetail.setErrorCode(InterfaceConstants.ERROR_CODE);
		iLogDetail.setErrorDesc(errorDesc);

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
