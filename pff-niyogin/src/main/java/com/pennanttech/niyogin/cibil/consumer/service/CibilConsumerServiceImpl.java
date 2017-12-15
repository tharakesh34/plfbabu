package com.pennanttech.niyogin.cibil.consumer.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.cibil.consumer.model.CibilConsumerAddress;
import com.pennanttech.niyogin.cibil.consumer.model.CibilConsumerRequest;
import com.pennanttech.niyogin.cibil.consumer.model.CibilPersonalDetails;
import com.pennanttech.niyogin.clients.JSONClient;
import com.pennanttech.niyogin.utility.NiyoginUtility;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.CibilConsumerService;
import com.pennanttech.pff.external.service.NiyoginService;

public class CibilConsumerServiceImpl extends NiyoginService implements CibilConsumerService {
	private static final Logger	logger				= Logger.getLogger(CibilConsumerServiceImpl.class);

	private final String		extConfigFileName	= "cibilConsumer";
	private String				serviceUrl;
	private JSONClient			client;

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

		try {
			logger.debug("ServiceURL : " + serviceUrl);
			jsonResponse = client.post(serviceUrl, cibilConsumerRequest);

			//for direct mapping fields
			extendedFieldMap = getExtendedMapValues(jsonResponse, extConfigFileName);

			// error validation on Response status
			if (extendedFieldMap.get("ERRORCODE") != null) {
				errorCode = Objects.toString(extendedFieldMap.get("ERRORCODE"));
				errorDesc = Objects.toString(extendedFieldMap.get("ERRORDESC"));
				throw new InterfaceException(errorCode, errorDesc);
			} else {
				extendedFieldMap.remove("ERRORCODE");
				extendedFieldMap.remove("ERRORDESC");
			}

			//validate the map with Configuration
			validatedMap = validateExtendedMapValues(extendedFieldMap);

			logger.info("Response : " + jsonResponse);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			status = "FAILED";
			errorCode = "9999";
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			errorDesc = writer.toString();
			doInterfaceLogging(cibilConsumerRequest, finReference);
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

		cibilConsumerRequest.setApplicationId(financeDetail.getFinReference());
		cibilConsumerRequest.setStgUniqueRefId(customer.getCustCIF());

		CibilPersonalDetails personalDetails = new CibilPersonalDetails();
		if (customer.getCustFName() != null) {
			personalDetails.setFirstName(customer.getCustFName());
		} else {
			personalDetails.setFirstName(customer.getCustShrtName());
		}
		personalDetails.setMiddleName(customer.getCustMName());
		personalDetails.setLastName(customer.getCustLName());
		personalDetails.setDob(customer.getCustDOB());
		personalDetails.setGender(customer.getLovDescCustGenderCodeName());

		if (customerDetails.getCustomerPhoneNumList() != null) {
			CustomerPhoneNumber customerPhone = NiyoginUtility
					.getHighPriorityPhone(customerDetails.getCustomerPhoneNumList(), 5);
			if (customerPhone != null) {
				personalDetails.setMobile(customerPhone.getPhoneNumber());
			} else {
				personalDetails.setMobile("");
			}
		}
		//TODO:
		personalDetails.setNomineeName(null);
		personalDetails.setNomineeRelation(null);

		String pan = "";
		String aadhar = "";
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		if (documentList != null) {
			for (CustomerDocument document : documentList) {
				if (document.getCustDocCategory().equals("01")) {
					aadhar = document.getCustDocTitle();
				} else if (document.getCustDocCategory().equals("03")) {
					pan = document.getCustDocTitle();
				}
			}
		}
		personalDetails.setPan(pan);
		personalDetails.setUid(aadhar);

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
	 * @param customerAddres
	 * @return address
	 */
	private CibilConsumerAddress prepareAddress(CustomerAddres customerAddres) {
		logger.debug(Literal.ENTERING);
		CibilConsumerAddress address = new CibilConsumerAddress();
		String houseNo;
		if (customerAddres.getCustAddrHNbr() != null) {
			houseNo = customerAddres.getCustAddrHNbr();
		} else {
			houseNo = customerAddres.getCustFlatNbr();
		}
		address.setHouseNo(houseNo);
		//TODO
		address.setSocietyName("");
		address.setCareOf("");
		address.setLandmark(customerAddres.getCustAddrStreet());
		address.setCategory(customerAddres.getCustAddrType());
		address.setCity(customerAddres.getCustAddrCity());
		address.setCountry(customerAddres.getCustAddrCountry());
		address.setDistrict(customerAddres.getCustDistrict());
		address.setPin(customerAddres.getCustAddrZIP());
		address.setState(customerAddres.getCustAddrProvince());

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

	public JSONClient getClient() {
		return client;
	}

	public void setClient(JSONClient client) {
		this.client = client;
	}

}
