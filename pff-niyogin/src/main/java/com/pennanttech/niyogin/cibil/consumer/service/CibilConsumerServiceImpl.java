package com.pennanttech.niyogin.cibil.consumer.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
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

	private final String		extConfigFileName	= "cibilConsumer";
	private String				serviceUrl;

	/**
	 * Method for get the CibilConsumer details of the Customer and set these details to ExtendedFieldDetails.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 */
	@Override
	public AuditHeader getCibilConsumer(AuditHeader auditHeader) throws InterfaceException {

		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();

		CibilConsumerRequest cibilConsumerRequest = prepareRequestObj(financeDetail);
		Map<String, Object> validatedMap = null;
		Map<String, Object> extendedFieldMap = null;

		// logging fields Data
		reqSentOn = new Timestamp(System.currentTimeMillis());
		reference = finReference;

		extendedFieldMap = post(serviceUrl, cibilConsumerRequest, extConfigFileName);
		try {
			//validate the map with Configuration
			validatedMap = validateExtendedMapValues(extendedFieldMap);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			doLogError(e, serviceUrl, cibilConsumerRequest);
			throw new InterfaceException("9999", e.getMessage());
		}
		// success case logging
		doInterfaceLogging(cibilConsumerRequest, finReference);

		prepareResponseObj(validatedMap, financeDetail);

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * method for prepare the CibilConsumerRequest request object.
	 * 
	 * @param financeDetail
	 * @return cibilConsumerRequest
	 */
	private CibilConsumerRequest prepareRequestObj(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		CibilConsumerRequest cibilConsumerRequest = new CibilConsumerRequest();

		cibilConsumerRequest.setApplicationId(customer.getCustID());
		cibilConsumerRequest.setStgUniqueRefId(customer.getCustID());

		CibilPersonalDetails personalDetails = new CibilPersonalDetails();
		if (customer.getCustFName() != null) {
			personalDetails.setFirstName(customer.getCustFName());
		} else {
			personalDetails.setFirstName(customer.getCustShrtName());
		}
		personalDetails.setMiddleName(customer.getCustMName());
		personalDetails.setLastName(customer.getCustLName());
		personalDetails.setDob(NiyoginUtility.formatDate(customer.getCustDOB(), "dd-MM-yyyy"));
		personalDetails.setGender(InterfaceConstants.PFF_GENDER_M);

		if (customerDetails.getCustomerPhoneNumList() != null) {
			CustomerPhoneNumber customerPhone =null;
			customerPhone= NiyoginUtility.getHighPriorityPhone(customerDetails.getCustomerPhoneNumList(), 5);
			if (customerPhone != null) {
				personalDetails.setMobile(customerPhone.getPhoneNumber());
			} else {
				personalDetails.setMobile("");
			}
		}
		//TODO:
		personalDetails.setNomineeName(null);
		personalDetails.setNomineeRelation(null);

		
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		if (documentList != null) {
			personalDetails.setPan(NiyoginUtility.getDocumentNumber(documentList, InterfaceConstants.DOC_TYPE_PAN));
			personalDetails.setUid(NiyoginUtility.getDocumentNumber(documentList, InterfaceConstants.DOC_TYPE_UID));
		}

		cibilConsumerRequest.setPersonalDetails(personalDetails);

		if (customerDetails.getAddressList() != null) {
			CustomerAddres customerAddres = NiyoginUtility.getHighPriorityAddress(customerDetails.getAddressList(), 5);
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
		if (custAddres.getCustAddrHNbr() != null) {
			houseNo = custAddres.getCustAddrHNbr();
		} else {
			houseNo = custAddres.getCustFlatNbr();
		}
		address.setHouseNo(houseNo);
		String societyName=custAddres.getCustAddrLine1()+","+custAddres.getCustAddrLine2()+","+custAddres.getCustAddrLine2();
		address.setSocietyName(societyName);
		address.setCareOf("");
		address.setLandmark(custAddres.getCustAddrStreet());
		address.setCategory(custAddres.getCustAddrType());
		
		City city=getCityDetails(custAddres);
		
		address.setCity(city.getPCCityName());
		address.setCountry(city.getLovDescPCCountryName());
		address.setDistrict(custAddres.getCustDistrict());
		address.setPin(custAddres.getCustAddrZIP());
		address.setState(city.getLovDescPCProvinceName());

		logger.debug(Literal.ENTERING);
		return address;

	}

	/**
	 * Method for prepare data and logging
	 * 
	 * @param consumerRequest
	 * @param reference
	 */
	private void doInterfaceLogging(CibilConsumerRequest consumerRequest, String reference) {
		InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, consumerRequest, jsonResponse, reqSentOn,
				status, errorCode, errorDesc, reference);
		logInterfaceDetails(interfaceLogDetail);
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
