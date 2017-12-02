package com.pennanttech.niyogin.experian.commercial.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
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
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.clients.JSONClient;
import com.pennanttech.niyogin.experian.commercial.model.Address;
import com.pennanttech.niyogin.experian.commercial.model.Applicant;
import com.pennanttech.niyogin.experian.commercial.model.BpayGridResponse;
import com.pennanttech.niyogin.experian.commercial.model.BureauCommercial;
import com.pennanttech.niyogin.experian.commercial.model.CompanyAddress;
import com.pennanttech.niyogin.utility.ExperianUtility;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.ExperianCommercialService;
import com.pennanttech.pff.external.service.NiyoginService;

/**
 * Method for get the ExperianCommercialService details of the Customer and set these details to ExtendedFieldDetails.
 * 
 * @param auditHeader
 * @return auditHeader
 */
public class ExperianCommercialServiceImpl extends NiyoginService implements ExperianCommercialService {
	private static final Logger	logger							= Logger.getLogger(ExperianCommercialServiceImpl.class);

	private final String		extConfigFileName				= "experianBureauCommercial";
	private String				serviceUrl;
	private JSONClient			client;

	private final String		NO_EMI_BOUNCES_IN_3_MONTHS		= "EMI3MONTHS";
	private final String		RESTRUCTURED_LOAN_AND_AMOUNT	= "RESTRUCTUREDLOAN";
	private final String		SUIT_FILED						= "SUITFILED";
	private final String		WILLFUL_DEFAULTER				= "WILLFULDEFAULTER";
	private final String		NO_EMI_BOUNCES_IN_SIX_MONTHS	= "EMI6MNTHS";

	private String				status							= "SUCCESS";
	private String				errorCode						= null;
	private String				errorDesc						= null;
	private String				jsonResponse					= null;
	private Timestamp			reqSentOn						= null;

	@Override
	public AuditHeader getBureauCommercial(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		BureauCommercial commercialRequest = prepareRequestObj(financeDetail);
		Map<String, Object> validatedMap = null;
		Map<String, Object> extendedFieldMap = null;

		// logging fields Data
		reqSentOn = new Timestamp(System.currentTimeMillis());

		try {
			logger.debug("ServiceURL : " + serviceUrl);
			jsonResponse = client.post(serviceUrl, commercialRequest);
			extendedFieldMap = getExtendedMapValues(jsonResponse, extConfigFileName);

			//For caliculation Fields
			prepareExtendedFieldMap(extendedFieldMap);

			// error validation on Response status
			if (extendedFieldMap.get("ERRORCODE") != null) {
				errorCode = Objects.toString(extendedFieldMap.get("ERRORCODE"));
				errorDesc = Objects.toString(extendedFieldMap.get("ERRORDESC"));
				throw new InterfaceException(errorCode, errorCode + ":" + errorDesc);
			} else {
				extendedFieldMap.remove("ERRORCODE");
				extendedFieldMap.remove("ERRORDESC");
				validatedMap = validateExtendedMapValues(extendedFieldMap);
			}

			logger.info("Response : " + jsonResponse);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			status = "FAILED";
			errorCode = "9999";
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			errorDesc = writer.toString();
			doInterfaceLogging(commercialRequest, finReference);
			throw new InterfaceException("9999", e.getMessage());
		}
		prepareResponseObj(validatedMap, financeDetail);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * method for prepare the ExperianBureauCommercial request object.
	 * 
	 * @param financeDetail
	 * @return bureauCommercial
	 */
	private BureauCommercial prepareRequestObj(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();

		BureauCommercial bureauCommercial = new BureauCommercial();
		String appId = financeDetail.getFinScheduleData().getFinanceMain().getApplicationNo();
		bureauCommercial.setApplicationId(appId);
		bureauCommercial.setStgUnqRefId(financeDetail.getFinReference());

		Applicant applicant = new Applicant();
		applicant.setFirstName(customer.getCustFName());
		applicant.setLastName(customer.getCustLName());
		applicant.setDob(customer.getCustDOB());
		applicant.setGender(customer.getCustGenderCode());

		String pan = "";
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		if (documentList != null) {
			for (CustomerDocument document : documentList) {
				if (document.getCustDocCategory().equals("03")) {
					pan = document.getCustDocTitle();
					break;
				}
			}
		}
		applicant.setPan(pan);
		if (customerDetails.getCustomerPhoneNumList() != null) {
			CustomerPhoneNumber customerPhone = ExperianUtility
					.getHighPriorityPhone(customerDetails.getCustomerPhoneNumList(), 5);
			if (customerPhone != null) {
				applicant.setMobile(customerPhone.getPhoneNumber());
			} else {
				applicant.setMobile("");
			}
		}

		applicant.setMaritalStatus(customer.getCustMaritalSts());

		if (customerDetails.getAddressList() != null) {
			CustomerAddres customerAddres = ExperianUtility.getHighPriorityAddress(customerDetails.getAddressList(), 5);
			applicant.setAddress(prepareAddress(customerAddres));
		} else {
			applicant.setAddress(new Address());
		}
		bureauCommercial.setApplicant(applicant);
		bureauCommercial.setCompanyName(customer.getCustShrtName());
		//FIXME:Ganesh
		CompanyAddress companyAddress = new CompanyAddress();
		companyAddress.setAddressLine1("");
		companyAddress.setAddressLine2("");
		companyAddress.setAddressLine3("");
		companyAddress.setCity("");
		companyAddress.setCountry("");
		companyAddress.setDistrict("");
		companyAddress.setPin("");
		companyAddress.setState("");
		bureauCommercial.setCompanyAddress(companyAddress);
		//FIXME:Ganesh
		bureauCommercial.setCompanyMobile("");
		bureauCommercial.setCompanyPan("");
		bureauCommercial.setLegalEntity("");
		logger.debug(Literal.LEAVING);
		return bureauCommercial;
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
		address.setCity(customerAddres.getCustAddrCity());
		address.setCountry(customerAddres.getCustAddrCountry());
		address.setPin(customerAddres.getCustAddrZIP());
		address.setState(customerAddres.getCustAddrProvince());
		return address;
	}

	/**
	 * Method for set the ExtendedFieldValues by performing calculations.
	 * 
	 * @param extendedFieldMap
	 * @throws Exception
	 */
	private void prepareExtendedFieldMap(Map<String, Object> extendedFieldMap) throws Exception {
		List<BpayGridResponse> bpayGridResponses = null;
		if (extendedFieldMap.get(NO_EMI_BOUNCES_IN_3_MONTHS) != null) {
			String jsonEmoBounceResponse = extendedFieldMap.get(NO_EMI_BOUNCES_IN_3_MONTHS).toString();
			Object responseObj = client.getResponseObject(jsonEmoBounceResponse, "", BpayGridResponse.class, true);
			bpayGridResponses = (List<BpayGridResponse>) responseObj;
		}

		for (Entry<String, Object> entry : extendedFieldMap.entrySet()) {
			//TODO:change is required
			if (entry.getKey().equals(RESTRUCTURED_LOAN_AND_AMOUNT)) {
				extendedFieldMap.put(entry.getKey(), "");
			} else if (entry.getKey().equals(SUIT_FILED)) {
				if (entry.getValue() != null) {
					try {
						int value = Integer.parseInt(entry.getValue().toString());
						if (value == 1) {
							extendedFieldMap.put(entry.getKey(), true);
						} else {
							extendedFieldMap.put(entry.getKey(), false);
						}
					} catch (NumberFormatException e) {
						throw new InterfaceException("9999", "");
					}

				}
			} else if (entry.getKey().equals(WILLFUL_DEFAULTER)) {
				if (entry.getValue() != null) {
					try {
						int value = Integer.parseInt(entry.getValue().toString());
						if (value == 1) {
							extendedFieldMap.put(entry.getKey(), true);
						} else {
							extendedFieldMap.put(entry.getKey(), false);
						}
					} catch (NumberFormatException e) {
						throw new InterfaceException("9999", "");
					}

				}
			} else if (entry.getKey().equals(NO_EMI_BOUNCES_IN_3_MONTHS)) {
				if (bpayGridResponses != null && bpayGridResponses.size() >= 2) {
					boolean isEmiBounce = isEMIBouncesInLastMonths(bpayGridResponses, 3);
					extendedFieldMap.put(entry.getKey(), isEmiBounce);
				}

			} else if (entry.getKey().equals(NO_EMI_BOUNCES_IN_SIX_MONTHS)) {
				if (bpayGridResponses != null && bpayGridResponses.size() >= 5) {
					boolean isEmiBounce = isEMIBouncesInLastMonths(bpayGridResponses, 6);
					extendedFieldMap.put(entry.getKey(), isEmiBounce);
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
	 * @param bpayGridResponses
	 * @param no
	 * @return
	 */
	private boolean isEMIBouncesInLastMonths(List<BpayGridResponse> bpayGridResponses, int no) {
		Collections.sort(bpayGridResponses, new BpayGridResponseComparator());
		for (int i = 0; i < no; i++) {
			String assestClasification = bpayGridResponses.get(i).getAssetClassification();
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
	 * This Comparator class is used to sort the BpayGridResponse based on their Month and Year
	 */
	//TODO:  month is sufficient or year H to L
	public class BpayGridResponseComparator implements Comparator<BpayGridResponse> {
		@Override
		public int compare(BpayGridResponse arg0, BpayGridResponse arg1) {

			return (arg0.getMonthvalue() + arg0.getYear()) - (arg1.getMonthvalue() + arg1.getYear());
		}
	}
	
	/**
	 * Method for prepare data and logging
	 * 
	 * @param commercialRequest
	 * @param reference
	 */
	private void doInterfaceLogging(BureauCommercial commercialRequest, String reference) {
		InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, commercialRequest, jsonResponse, reqSentOn,
				status, errorCode, errorDesc, reference);
		logInterfaceDetails(interfaceLogDetail);
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
	public void setClient(JSONClient client) {
		this.client = client;
	}

}
