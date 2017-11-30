package com.pennanttech.niyogin.experian.consumer.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.niyogin.bureau.consumer.model.Address;
import com.pennanttech.niyogin.bureau.consumer.model.BureauConsumer;
import com.pennanttech.niyogin.bureau.consumer.model.CAISAccountHistory;
import com.pennanttech.niyogin.bureau.consumer.model.PersonalDetails;
import com.pennanttech.niyogin.clients.JSONClient;
import com.pennanttech.niyogin.utility.APIUtility;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.ExperianConsumerService;
import com.pennanttech.pff.external.service.NiyoginService;

public class ExperianConsumerServiceImpl extends NiyoginService implements ExperianConsumerService {

	private static final Logger	logger				= Logger.getLogger(ExperianConsumerServiceImpl.class);
	private final String		extConfigFileName	= "experianBureauConsumer";
	private String				serviceUrl;

	/**
	 * Method for get the ExperianBureauConsumer details of the Customer and set these details to ExtendedFieldDetails.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 */
	@Override
	public AuditHeader getExperianConsumer(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		BureauConsumer consumerRequest = prepareRequestObj(financeDetail);
		JSONClient client = new JSONClient();
		Map<String, Object> validatedMap = null;
		Map<String, Object> extendedFieldMap = null;
		try {
			logger.debug("ServiceURL : " + serviceUrl);
			String jsonResponse = client.post(serviceUrl, consumerRequest);
			extendedFieldMap = getExtendedMapValues(jsonResponse, extConfigFileName);

			//For caliculation Fields
			prepareExtendedFieldMap(extendedFieldMap);

			// TODO: Error Response validate  
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
	 * method for prepare the ExperianBureauConsumer request object.
	 * 
	 * @param financeDetail
	 * @return bureauConsumer
	 */
	private BureauConsumer prepareRequestObj(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();

		BureauConsumer bureauConsumer = new BureauConsumer();
		String appNo = financeDetail.getFinScheduleData().getFinanceMain().getApplicationNo();
		bureauConsumer.setStgUnqRefId(financeDetail.getFinReference());
		bureauConsumer.setApplicationId(appNo);
		if (customerDetails.getAddressList() != null) {
			CustomerAddres customerAddres = APIUtility.getHighPriorityAddress(customerDetails.getAddressList(), 5);
			bureauConsumer.setAddress(prepareAddress(customerAddres));
		} else {
			bureauConsumer.setAddress(new Address());
		}
		PersonalDetails personalDetails = new PersonalDetails();
		personalDetails.setFirstName(customer.getCustFName());
		personalDetails.setLastName(customer.getCustLName());
		personalDetails.setDob(customer.getCustDOB());
		personalDetails.setGender(customer.getCustGenderCode());

		if (customerDetails.getCustomerPhoneNumList() != null) {
			String mobileNo = APIUtility.getHighPriorityPhone(customerDetails.getCustomerPhoneNumList(), 5);
			personalDetails.setMobile(mobileNo);
		}
		String pan = "";
		String aadhar = "";
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		for (CustomerDocument document : documentList) {
			if (document.getCustDocCategory().equals("01")) {
				aadhar = document.getCustDocTitle();
			} else if (document.getCustDocCategory().equals("03")) {
				pan = document.getCustDocTitle();
			}
		}
		personalDetails.setPan(pan);
		personalDetails.setUid_(aadhar);
		bureauConsumer.setPersonal(personalDetails);
		logger.debug(Literal.LEAVING);
		return bureauConsumer;
	}

	/**
	 * Method for prepare the Address request object.
	 * 
	 * @param customerAddres
	 * @return address
	 */
	private Address prepareAddress(CustomerAddres customerAddres) {
		Address address = new Address();
		String houseNo;
		if (customerAddres.getCustAddrHNbr() != null) {
			houseNo = customerAddres.getCustAddrHNbr();
		} else {
			houseNo = customerAddres.getCustFlatNbr();
		}
		address.setHouseNo(houseNo);
		address.setLandmark(customerAddres.getCustAddrStreet());
		//TODO:
		address.setCareOf("");
		address.setCity(customerAddres.getCustAddrCity());
		address.setCountry(customerAddres.getCustAddrCountry());
		address.setDistrict(customerAddres.getCustDistrict());
		address.setPin(customerAddres.getCustAddrZIP());
		address.setState(customerAddres.getCustAddrProvince());
		address.setSubDistrict(customerAddres.getCustDistrict());
		return address;
	}

	/**
	 * Method for set the ExtendedFieldValues by performing calculations.
	 * 
	 * @param extendedFieldMap
	 * @throws Exception
	 */
	private void prepareExtendedFieldMap(Map<String, Object> extendedFieldMap) throws Exception {
		JSONClient jsonClient = new JSONClient();
		List<CAISAccountHistory> caisAccountHistories = null;
		if (extendedFieldMap.get("EMI3MONTHS") != null) {
			String jsonResponse = extendedFieldMap.get("EMI3MONTHS").toString();
			Object responseObj = jsonClient.getResponseObject(jsonResponse, "", CAISAccountHistory.class, true);
			caisAccountHistories = (List<CAISAccountHistory>) responseObj;
		}
		for (Entry<String, Object> entry : extendedFieldMap.entrySet()) {
			//TODO:change is required set the CurrentBalance if 01
			if (entry.getKey().equals("RESTRUCTUREDLOAN")) {
				extendedFieldMap.put(entry.getKey(), "");
			} else if (entry.getKey().equals("SUITFILED")) {
				if (entry.getValue().equals("01")) {
					extendedFieldMap.put(entry.getKey(), true);
				} else {
					extendedFieldMap.put(entry.getKey(), false);
				}
			} else if (entry.getKey().equals("WILLFULDEFAULTER")) {
				if (entry.getValue().equals("02")) {
					extendedFieldMap.put(entry.getKey(), true);
				} else {
					extendedFieldMap.put(entry.getKey(), false);
				}
			} else if (entry.getKey().equals("EMI3MONTHS")) {
				if (caisAccountHistories != null && caisAccountHistories.size() >= 2) {
					boolean isEmiBounce = isEMIBouncesInLastMonths(caisAccountHistories, 3);
					extendedFieldMap.put("EMI3MONTHS", isEmiBounce);
				}
			} else if (entry.getKey().equals("EMI6MNTHS")) {
				if (caisAccountHistories != null && caisAccountHistories.size() >= 5) {
					boolean isEmiBounce = isEMIBouncesInLastMonths(caisAccountHistories, 6);
					extendedFieldMap.put("EMI6MNTHS", isEmiBounce);
				}
			} else {
				extendedFieldMap.put(entry.getKey(), entry.getValue());
			}

		}

	}

	/**
	 * Method for prepare the Extended Field details map according to the given response.
	 * 
	 * @param extendedResMapObject
	 * @param financeDetail
	 */
	private void prepareResponseObj(Map<String, Object> validatedMap, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		if (validatedMap != null) {
			Map<String, Object> extendedMapObject = financeDetail.getExtendedFieldRender().getMapValues();
			if (extendedMapObject == null) {
				extendedMapObject = new HashMap<String, Object>();
			}
			for (Entry<String, Object> entry : validatedMap.entrySet()) {
				extendedMapObject.put(entry.getKey(), entry.getValue());
			}
			financeDetail.getExtendedFieldRender().setMapValues(extendedMapObject);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * check the assestClasification with three types("Blank","?","S") other than these three it returns true otherwise
	 * return false
	 * 
	 * @param caisAccountHistories
	 * @param no
	 * @return
	 */
	private boolean isEMIBouncesInLastMonths(List<CAISAccountHistory> caisAccountHistories, int no) {
		Collections.sort(caisAccountHistories, new CAISAccountHistoryComparator());
		for (int i = 0; i < no; i++) {
			String assestClasification = caisAccountHistories.get(i).getAssetClassification();
			if (assestClasification.equals("Blank") || assestClasification.equals("?")
					|| assestClasification.equals("S")) {
				continue;
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * This Comparator class is used to sort the CAISAccountHistory based on their Month and Year
	 */
	//TODO:  month is sufficient or year H to L
	public class CAISAccountHistoryComparator implements Comparator<CAISAccountHistory> {
		@Override
		public int compare(CAISAccountHistory arg0, CAISAccountHistory arg1) {

			return (arg0.getMonth() + arg0.getYear() - arg1.getMonth() + arg1.getYear());
		}
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
