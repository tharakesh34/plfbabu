package com.pennanttech.niyogin.cibil.consumer.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.cibil.consumer.model.CibilConsumerAddress;
import com.pennanttech.niyogin.cibil.consumer.model.CibilConsumerRequest;
import com.pennanttech.niyogin.cibil.consumer.model.CibilPersonalDetails;
import com.pennanttech.niyogin.utility.NiyoginUtility;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.CibilConsumerService;
import com.pennanttech.pff.external.service.NiyoginService;

public class CibilConsumerServiceImpl extends NiyoginService implements CibilConsumerService {
	private static final Logger	logger				= Logger.getLogger(CibilConsumerServiceImpl.class);

	private final String		extConfigFileName	= "cibilConsumer.properties";
	private String				serviceUrl;

	//CIBIL
	public static final String	REQ_SEND			= "REQSENDCIBIL";
	public static final String	STATUSCODE			= "STATUSCIBIL";
	public static final String	RSN_CODE			= "REASONCIBIL";
	public static final String	REMARKS				= "REMARKSCIBIL";

	/**
	 * Method for get the CibilConsumer details of the Customer and set these details to ExtendedFieldDetails.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 */
	@Override
	public AuditHeader getCibilConsumer(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(serviceUrl)) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		// Execute CIBIL for Sole proprietor customer
		//Process Customer
		if (StringUtils.trimToEmpty(customer.getCustTypeCode()).equals(InterfaceConstants.CUSTTYPE_SOLEPRO)) {
			processCustomer(financeDetail, customerDetails);
		}
		//Process Co_applicant
		processCoApplicant(financeDetail);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for process the CIBIL details of Applicant.
	 * 
	 * @param financeDetail
	 * @param customerDetails
	 */
	private void processCustomer(FinanceDetail financeDetail, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		//for Applicant
		//prepare request object
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Map<String, Object> appplicationdata = new HashMap<>();
		CibilConsumerRequest cibilConsumerRequest = prepareRequestObj(customerDetails);
		//send request and log
		String reference = financeMain.getFinReference();
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;
		Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());
		try {
			reuestString = client.getRequestString(cibilConsumerRequest);
			jsonResponse = client.post(serviceUrl, reuestString);
			//check response for error
			errorCode = getErrorCode(jsonResponse);
			errorDesc = getErrorMessage(jsonResponse);

			doInterfaceLogging(reference, reuestString, jsonResponse, errorCode, errorDesc, reqSentOn);

			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, getTrimmedMessage(errorDesc));
			appplicationdata.put(STATUSCODE, getStatusCode(jsonResponse));

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
			doExceptioLogging(reference, reuestString, jsonResponse, errorDesc, reqSentOn);

			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, errorDesc);
		}
		appplicationdata.put(REQ_SEND, true);
		prepareResponseObj(appplicationdata, financeDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for process the CIBIL details of Co_Applicant's.
	 * 
	 * @param financeDetail
	 */
	private void processCoApplicant(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		List<JointAccountDetail> coapplicants = financeDetail.getJountAccountDetailList();

		if (coapplicants == null || coapplicants.isEmpty()) {
			return;
		}

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String reference = financeMain.getFinReference();

		List<Long> coApplicantIDs = new ArrayList<Long>(1);
		for (JointAccountDetail coApplicant : coapplicants) {
			coApplicantIDs.add(coApplicant.getCustID());
		}

		List<CustomerDetails> coApplicantCustomers = getCoApplicants(coApplicantIDs);
		for (CustomerDetails coAppCustomerDetails : coApplicantCustomers) {
			Map<String, Object> appplicationdata = new HashMap<>();
			CibilConsumerRequest cibilConsumerRequest = prepareRequestObj(coAppCustomerDetails);
			//send request and log
			String errorCode = null;
			String errorDesc = null;
			String reuestString = null;
			String jsonResponse = null;
			Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());
			try {
				reuestString = client.getRequestString(cibilConsumerRequest);
				jsonResponse = client.post(serviceUrl, reuestString);
				//check response for error
				errorCode = getErrorCode(jsonResponse);
				errorDesc = getErrorMessage(jsonResponse);

				doInterfaceLogging(reference, reuestString, jsonResponse, errorCode, errorDesc, reqSentOn);

				appplicationdata.put(RSN_CODE, errorCode);
				appplicationdata.put(REMARKS, getTrimmedMessage(errorDesc));
				appplicationdata.put(STATUSCODE, getStatusCode(jsonResponse));

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
				doExceptioLogging(reference, reuestString, jsonResponse, errorDesc, reqSentOn);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * method for prepare the CibilConsumerRequest request object.
	 * 
	 * @param financeDetail
	 * @return cibilConsumerRequest
	 */
	private CibilConsumerRequest prepareRequestObj(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		Customer customer = customerDetails.getCustomer();
		CibilConsumerRequest cibilConsumerRequest = new CibilConsumerRequest();
		cibilConsumerRequest.setApplicationId(customer.getCustID());
		cibilConsumerRequest.setStgUniqueRefId(customer.getCustID());
		CibilPersonalDetails personalDetails = new CibilPersonalDetails();
		if (StringUtils.isNotBlank(customer.getCustFName())) {
			personalDetails.setFirstName(customer.getCustFName());
		} else {
			personalDetails.setFirstName(customer.getCustShrtName());
		}
		if (StringUtils.isNotBlank(customer.getCustMName())) {
			personalDetails.setMiddleName(customer.getCustMName());
		} else {
			personalDetails.setMiddleName("");
		}
		if (StringUtils.isNotBlank(customer.getCustLName())) {
			personalDetails.setLastName(customer.getCustLName());
		} else {
			personalDetails.setLastName(customer.getCustShrtName());
		}
		personalDetails.setDob(NiyoginUtility.formatDate(customer.getCustDOB(), "dd-MM-yyyy"));
		personalDetails.setGender(InterfaceConstants.PFF_GENDER_M);
		personalDetails.setMobile(NiyoginUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList()));
		personalDetails.setNomineeName("");
		personalDetails.setNomineeRelation("");
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		if (documentList != null) {
			String panCard = StringUtils.trimToEmpty((String) getSMTParameter("PAN_DOC_TYPE", String.class));
			personalDetails.setPan(getDocumentNumber(documentList, panCard));
			String uid = StringUtils.trimToEmpty((String) getSMTParameter("UID_DOC_TYPE", String.class));
			personalDetails.setUid(getDocumentNumber(documentList, uid));
		}
		cibilConsumerRequest.setPersonalDetails(personalDetails);

		if (customerDetails.getAddressList() != null) {
			CustomerAddres customerAddres = NiyoginUtility.getAddress(customerDetails.getAddressList());
			cibilConsumerRequest.setAddress(prepareAddress(customerAddres));

		} else {
			cibilConsumerRequest.setAddress(new CibilConsumerAddress());
		}

		logger.debug(Literal.LEAVING);
		return cibilConsumerRequest;

	}

	/**
	 * Method for prepare the Address request object.
	 * 
	 * @param custAddres
	 * @return address
	 */
	private CibilConsumerAddress prepareAddress(CustomerAddres custAddres) {
		logger.debug(Literal.ENTERING);
		CibilConsumerAddress address = new CibilConsumerAddress();
		String houseNo;
		if (StringUtils.isNotBlank(custAddres.getCustAddrHNbr())) {
			houseNo = custAddres.getCustAddrHNbr();
		} else {
			houseNo = Objects.toString(custAddres.getCustFlatNbr(), "");
		}
		address.setHouseNo(houseNo);

		StringBuilder stringBuilder = new StringBuilder();
		if (StringUtils.isNotBlank(custAddres.getCustAddrLine1())) {
			stringBuilder.append(custAddres.getCustAddrLine1());
		}
		if (StringUtils.isNotBlank(custAddres.getCustAddrLine2())) {
			if (StringUtils.isNotBlank(stringBuilder)) {
				stringBuilder.append(",");
			}
			stringBuilder.append(custAddres.getCustAddrLine2());
		}

		address.setSocietyName(stringBuilder.toString());
		address.setCareOf(custAddres.getCustAddrLine3());
		address.setLandmark(custAddres.getCustAddrStreet());
		address.setCategory(custAddres.getCustAddrType());
		City city = getCityDetails(custAddres);
		if (city != null) {
			address.setCity(city.getPCCityName());
			address.setCountry(city.getLovDescPCCountryName());
			address.setDistrict(custAddres.getCustDistrict());
			address.setPin(custAddres.getCustAddrZIP());
			address.setState(city.getLovDescPCProvinceName());
		}

		logger.debug(Literal.ENTERING);
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
	 * @param reqSentOn 
	 */
	private void doInterfaceLogging(String reference, String requets, String response, String errorCode,
			String errorDesc, Timestamp reqSentOn) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);

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
	 * @param reqSentOn 
	 */
	private void doExceptioLogging(String reference, String requets, String response, String errorDesc,
			Timestamp reqSentOn) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);

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
