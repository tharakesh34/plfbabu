package com.pennanttech.niyogin.cibil.consumer.service;

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
import com.pennant.backend.model.finance.FinanceDetail;
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

		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		Map<String, Object> custExtMaP = new HashMap<>();
		Map<String, Object> coAppExtMap = new HashMap<>();
		
		// Execute CIBIL for Sole propritor customer
		if (StringUtils.trimToEmpty(customer.getCustTypeCode()).equals(InterfaceConstants.CUSTTYPE_SOLEPRO)) {
			custExtMaP = executeCIBIL(customerDetails, finReference);
			prepareResponseObj(custExtMaP, financeDetail);
		}
		
		
		// Execute CIBIL for co-applicants
		List<JointAccountDetail> coapplicants = financeDetail.getJountAccountDetailList();
		if (coapplicants != null && !coapplicants.isEmpty()) {
			List<Long> coApplicantIDs = new ArrayList<Long>(1);
			for (JointAccountDetail coApplicant : coapplicants) {
				long custId = getCustomerId(coApplicant.getCustCIF());
				coApplicantIDs.add(custId);
			}
			//TODO: Need solution for display co-applicant extended details
			List<CustomerDetails> coApplicantCustomers = getCoApplicants(coApplicantIDs);
			for (CustomerDetails coAppCustomerDetails : coApplicantCustomers) {
				coAppExtMap.putAll(executeCIBIL(coAppCustomerDetails, finReference));
			}
		}
		
		if(custExtMaP == null || custExtMaP.isEmpty()) {
			prepareResponseObj(coAppExtMap, financeDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * 
	 * @param customerDetails
	 * @param finReference
	 * @return
	 */
	private Map<String, Object> executeCIBIL(CustomerDetails customerDetails, String finReference) {
		logger.debug(Literal.ENTERING);
		
		CibilConsumerRequest cibilConsumerRequest = prepareRequestObj(customerDetails);
		Map<String, Object> validatedMap = null;
		Map<String, Object> extendedFieldMap = null;

		// logging fields Data
		reqSentOn = new Timestamp(System.currentTimeMillis());
		reference = finReference;

		extendedFieldMap = post(serviceUrl, cibilConsumerRequest, extConfigFileName);
		try {
			//validate the map with Configuration
			validatedMap = validateExtendedMapValues(extendedFieldMap);
			if (validatedMap != null && validatedMap.isEmpty()) {
				/*validatedMap.put("REASONCODECIBIL", statusCode);
				validatedMap.put("REMARKSCIBIL", App.getLabel("niyogin_No_Data"));*/
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			doLogError(e, serviceUrl, cibilConsumerRequest);
			throw new InterfaceException("9999", e.getMessage());
		}

		// success case logging
		doInterfaceLogging(cibilConsumerRequest, finReference);
		
		logger.debug(Literal.LEAVING);
		return validatedMap;
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

		if (customerDetails.getCustomerPhoneNumList() != null) {
			personalDetails.setMobile(NiyoginUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList()));
		}
		
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
		if (custAddres.getCustAddrHNbr() != null) {
			houseNo = custAddres.getCustAddrHNbr();
		} else {
			houseNo = custAddres.getCustFlatNbr();
		}
		address.setHouseNo(houseNo);
		String societyName = custAddres.getCustAddrLine1() + "," + custAddres.getCustAddrLine2() + ","
				+ custAddres.getCustAddrLine2();
		address.setSocietyName(societyName);
		address.setCareOf("");
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
