package com.pennanttech.niyogin.dedup.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.dedup.model.Address;
import com.pennanttech.niyogin.dedup.model.ExperianDedup;
import com.pennanttech.niyogin.dedup.model.Phone;
import com.pennanttech.niyogin.utility.NiyoginUtility;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.ExternalDedup;
import com.pennanttech.pff.external.service.NiyoginService;

public class ExperianDedupService extends NiyoginService implements ExternalDedup {
	private static final Logger	logger				= Logger.getLogger(ExperianDedupService.class);

	private final String		extConfigFileName	= "experianDedup.properties";
	private String				serviceUrl;

	private String				APPLICANT			= "A";
	private String				COAPPLICANT			= "C";

	//Experian Dedup
	public static final String	EXP_DEDUP_REQ_SEND	= "EXDREQUESTSEND";
	public static final String	EXP_DEDUP_RSN_CODE	= "REASONCODEINTERNAL";
	public static final String	EXP_DEDUP_REMARKS	= "REMARKSINTERNAL";
	//Form Fields
	public static final String	FORM_FLDS_FACEBOOK	= "FBID";
	public static final String	FORM_FLDS_LINKEDIN	= "LINKEDID";
	public static final String	FORM_FLDS_TWITTER	= "TWITTERID";

	/**
	 * Method for check the Dedup details of the Customer and Co_Applicants and set the response details to
	 * ExtendedFieldDetails.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 */
	@Override
	public AuditHeader checkDedup(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();

		//Process Customer
		processCustomer(financeDetail, customerDetails);

		//Process Co_applicant
		processCoApplicant(financeDetail);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private void processCustomer(FinanceDetail financeDetail, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		//for Applicant
		//prepare request object
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Map<String, Object> extendedMap = financeDetail.getExtendedFieldRender().getMapValues();
		Map<String, Object> appplicationdata = new HashMap<>();
		ExperianDedup experianDedupApplicant = new ExperianDedup();
		experianDedupApplicant.setApplicantType(APPLICANT);
		prepareRequestObj(customerDetails, experianDedupApplicant, extendedMap);
		//send request and log
		String reference = financeMain.getFinReference();
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;

		try {
			reuestString = client.getRequestString(experianDedupApplicant);
			jsonResponse = client.post(serviceUrl, reuestString);
			//check response for error
			errorCode = getErrorCode(jsonResponse);
			errorDesc = getErrorMessage(jsonResponse);

			doInterfaceLogging(reference, reuestString, jsonResponse, errorCode, errorDesc);

			appplicationdata.put(EXP_DEDUP_RSN_CODE, errorCode);
			appplicationdata.put(EXP_DEDUP_REMARKS, getTrimmedMessage(errorDesc));
			//add status 
			

			if (StringUtils.isEmpty(errorCode)) {
				//read values from response and load it to extended map
				Map<String, Object> mapdata = getPropValueFromResp(jsonResponse, extConfigFileName);
				Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
				//add to final
				appplicationdata.putAll(mapvalidData);

				financeMain.setDedupMatch((Boolean) appplicationdata.get("MATCH"));
				if (financeMain.isDedupMatch()) {
					setWorkflowDetails(financeMain);
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorDesc = getWriteException(e);
			errorDesc = getTrimmedMessage(errorDesc);
			doExceptioLogging(reference, reuestString, jsonResponse, errorDesc);

			//As per the clint need
			financeMain.setDedupMatch(true);
			setWorkflowDetails(financeMain);

			appplicationdata.put(EXP_DEDUP_RSN_CODE, errorCode);
			appplicationdata.put(EXP_DEDUP_REMARKS, errorDesc);
		}
		appplicationdata.put(EXP_DEDUP_REQ_SEND, true);
		prepareResponseObj(appplicationdata, financeDetail);
		logger.debug(Literal.LEAVING);
	}

	private String getWriteException(Exception e) {
		String errorDesc;
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		errorDesc = writer.toString();
		return errorDesc;
	}

	private void processCoApplicant(FinanceDetail financeDetail) {
		//for Applicant

		List<JointAccountDetail> coapplicants = financeDetail.getJountAccountDetailList();

		if (coapplicants == null || coapplicants.isEmpty()) {
			return;
		}

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Map<String, Object> extendedMap = financeDetail.getExtendedFieldRender().getMapValues();

		List<Long> coApplicantIDs = new ArrayList<Long>(1);
		for (JointAccountDetail coApplicant : coapplicants) {
			coApplicantIDs.add(coApplicant.getCustID());
		}

		List<CustomerDetails> coApplicantCustomers = getCoApplicants(coApplicantIDs);
		for (CustomerDetails coAppCustomerDetails : coApplicantCustomers) {
			//prepare request object
			Map<String, Object> appplicationdata = new HashMap<>();
			ExperianDedup experianDedupApplicant = new ExperianDedup();
			experianDedupApplicant.setApplicantType(COAPPLICANT);
			prepareRequestObj(coAppCustomerDetails, experianDedupApplicant, extendedMap);
			//send request and log
			String reference = financeMain.getFinReference();
			String errorCode = null;
			String errorDesc = null;
			String reuestString = null;
			String jsonResponse = null;

			try {
				reuestString = client.getRequestString(experianDedupApplicant);
				jsonResponse = client.post(serviceUrl, reuestString);
				//check response for error
				errorCode = getErrorCode(jsonResponse);
				errorDesc = getErrorMessage(jsonResponse);

				doInterfaceLogging(reference, reuestString, jsonResponse, errorCode, errorDesc);

				if (StringUtils.isEmpty(errorCode)) {
					//read values from response and load it to extended map
					Map<String, Object> mapdata = getPropValueFromResp(jsonResponse, extConfigFileName);
					Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
					//add to final
					appplicationdata.putAll(mapvalidData);
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
				errorDesc = getWriteException(e);
				errorDesc = getTrimmedMessage(errorDesc);
				doExceptioLogging(reference, reuestString, jsonResponse, errorDesc);
			}

		}

	}

	private void prepareRequestObj(CustomerDetails customerDetails, ExperianDedup experianDedup,
			Map<String, Object> extendedMap) {
		logger.debug(Literal.ENTERING);
		Customer customer = customerDetails.getCustomer();
		experianDedup.setFirstName(customer.getCustShrtName());
		experianDedup.setLastName(customer.getCustShrtName());
		experianDedup.setGender(customer.getCustGenderCode());
		experianDedup.setDob(customer.getCustDOB());

		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		if (documentList != null && !documentList.isEmpty()) {
			experianDedup.setPan(getPanNumber(documentList));
			experianDedup.setAadhaar(getPanNumber(documentList));//FIXME
			experianDedup.setPassport(getPanNumber(documentList));//FIXME
		}
		List<CustomerEMail> emailList = customerDetails.getCustomerEMailList();
		if (emailList != null && !emailList.isEmpty()) {
			experianDedup.setEmailId(NiyoginUtility.getEmail(emailList));
		}
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		if (addressList != null && !addressList.isEmpty()) {
			CustomerAddres customerAddres = NiyoginUtility.getAddress(addressList);
			experianDedup.setAddress(prepareAddress(customerAddres));

		} else {
			experianDedup.setAddress(new Address());
		}

		List<CustomerPhoneNumber> phoneNumberList = customerDetails.getCustomerPhoneNumList();
		if (phoneNumberList != null && !phoneNumberList.isEmpty()) {
			CustomerPhoneNumber custPhoneNumber = NiyoginUtility.getPhone(phoneNumberList);
			experianDedup.setPhone(preparePhone(custPhoneNumber));

		} else {
			experianDedup.setPhone(new Phone());
		}

		if (extendedMap != null) {
			experianDedup.setLinkedin(getval(extendedMap.get(FORM_FLDS_FACEBOOK)));
			experianDedup.setFacebook(getval(extendedMap.get(FORM_FLDS_TWITTER)));
			experianDedup.setTwitter(getval(extendedMap.get(FORM_FLDS_LINKEDIN)));
		}
		logger.debug(Literal.LEAVING);
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

		String addrLines = customerAddres.getCustAddrType() + "," + customerAddres.getCustAddrHNbr() + ","
				+ customerAddres.getCustAddrStreet();

		address.setAddressLine1(addrLines);
		address.setAddressLine2(addrLines);
		address.setAddressLine3(addrLines);
		address.setLandmark(customerAddres.getCustAddrStreet());

		City city = getCityDetails(customerAddres);
		if (city != null) {
			address.setCity(city.getPCCityName());
			address.setState(city.getLovDescPCProvinceName());
			address.setCountry(city.getLovDescPCCountryName());
		}
		address.setPin(customerAddres.getCustAddrZIP());
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
	 * Method for prepare data and logging
	 * 
	 * @param experianDedupRequest
	 * @param reference
	 */
	private void doInterfaceLogging(String reference, String requets, String response, String errorCode,
			String errorDesc) {
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
	}

	/**
	 * Method for prepare data and logging
	 * 
	 * @param experianDedupRequest
	 * @param reference
	 */
	private void doExceptioLogging(String reference, String requets, String response, String errorDesc) {
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
	}

	/**
	 * 
	 * @param financeMain
	 */
	private void setWorkflowDetails(FinanceMain financeMain) {
		if (financeMain.isDedupMatch()) {
			financeMain.setNextRoleCode(financeMain.getRoleCode());
			financeMain.setNextTaskId(financeMain.getTaskId() + ";");
		}
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
