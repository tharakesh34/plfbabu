package com.pennanttech.niyogin.experian.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
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
import com.pennanttech.niyogin.experian.model.Address;
import com.pennanttech.niyogin.experian.model.Applicant;
import com.pennanttech.niyogin.experian.model.BillPayGrid;
import com.pennanttech.niyogin.experian.model.BpayGridResponse;
import com.pennanttech.niyogin.experian.model.BureauCommercial;
import com.pennanttech.niyogin.experian.model.BureauConsumer;
import com.pennanttech.niyogin.experian.model.CAISAccountHistory;
import com.pennanttech.niyogin.experian.model.CompanyAddress;
import com.pennanttech.niyogin.experian.model.ConsumerAddress;
import com.pennanttech.niyogin.experian.model.PersonalDetails;
import com.pennanttech.niyogin.utility.NiyoginUtility;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.ExperianBureauService;
import com.pennanttech.pff.external.service.NiyoginService;

public class ExperianBureauServiceImpl extends NiyoginService implements ExperianBureauService {
	private static final Logger	logger							= Logger.getLogger(ExperianBureauServiceImpl.class);
	private String				extConfigFileName;
	private String				consumerUrl;
	private String				commercialUrl;

	private final String		SUIT_FILED						= "SUITFILED";
	private final String		WILLFUL_DEFAULTER				= "WILLFULDEFAULTER";
	private final String		NO_EMI_BOUNCES_IN_3_MONTHS		= "EMI3MONTHS";
	private final String		NO_EMI_BOUNCES_IN_6_MONTHS		= "EMI6MNTHS";
	private final String		RESTRUCTURED_LOAN_AND_AMOUNT	= "RESTRUCTUREDLOAN";

	private final String		STATUS							= "STATUS";
	private final String		WRITEOFF						= "25";
	private final String		SETTLE							= "23";

	private Date				appDate							= getAppDate();

	private Object				requestObject					= null;
	private String				serviceUrl						= null;

	/**
	 * Method for execute Experian Bureau service<br>
	 *   - Execute Commercial bureau service for SME and CORP customers<br>.
	 *   - Execute Consumer service for RETAIL customer.
	 * 
	 * @param auditHeader
	 */
	@Override
	public AuditHeader executeExperianBureau(AuditHeader auditHeader) throws InterfaceException, ParseException {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();

		reqSentOn = new Timestamp(System.currentTimeMillis());

		//validate the map with configuration
		Map<String, Object> validatedExtendedMap = executeBureau(financeDetail, customerDetails);

		// Execute Bureau for co-applicants
		List<JointAccountDetail> coapplicants = financeDetail.getJountAccountDetailList();
		if (coapplicants != null && !coapplicants.isEmpty()) {
			List<Long> coApplicantIDs = new ArrayList<Long>(1);
			for (JointAccountDetail coApplicant : coapplicants) {
				coApplicantIDs.add(coApplicant.getCustID());
			}
			//TODO: Need solution for display co-applicant extended details
			Map<String, Object> extendedFieldMapForCoApp = new HashMap<>();
			List<CustomerDetails> coApplicantCustomers = getCoApplicants(coApplicantIDs);
			for (CustomerDetails coAppCustomerDetails : coApplicantCustomers) {
				extendedFieldMapForCoApp.putAll(executeBureau(financeDetail, coAppCustomerDetails));
			}
		}

		// success case logging
		doInterfaceLogging(requestObject, finReference);

		if(validatedExtendedMap != null && validatedExtendedMap.isEmpty()) {
			validatedExtendedMap.put("REASONCODE", statusCode);
			validatedExtendedMap.put("REMARKSEXPERIANBEA", App.getLabel("niyogin_No_Data"));
		}
		prepareResponseObj(validatedExtendedMap, financeDetail);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for identify the customer and execute Bureau.
	 * 
	 * @param financeDetail
	 * @param customerDetails
	 * @return
	 * @throws ParseException
	 */
	private Map<String, Object> executeBureau(FinanceDetail financeDetail, CustomerDetails customerDetails)
			throws ParseException {
		logger.debug(Literal.ENTERING);
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		Map<String, Object> extendedFieldMap = null;
		Map<String, Object> validatedExtendedMap = null;

		reference = finReference;

		if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(), InterfaceConstants.PFF_CUSTCTG_SME)) {
			BureauCommercial commercial = prepareCommercialRequestObj(customerDetails);
			serviceUrl = commercialUrl;
			extConfigFileName = "experianBureauCommercial";
			requestObject = commercial;
		} else if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),
				InterfaceConstants.PFF_CUSTCTG_INDIV)) {
			BureauConsumer consumer = prepareConsumerRequestObj(customerDetails);
			serviceUrl = consumerUrl;
			extConfigFileName = "experianBureauConsumer";
			requestObject = consumer;
		}

		extendedFieldMap = post(serviceUrl, requestObject, extConfigFileName);

	
		try {

			if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),
					InterfaceConstants.PFF_CUSTCTG_SME)) {
				extendedFieldMap = prepareCommercialExtendedMap(extendedFieldMap);
			} else if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),
					InterfaceConstants.PFF_CUSTCTG_INDIV)) {
				extendedFieldMap = prepareConsumerExtendedMap(extendedFieldMap);
			}

			//validate the map with configuration
			validatedExtendedMap = validateExtendedMapValues(extendedFieldMap);

		} catch (Exception e) {
			logger.error("Exception: ", e);
			doLogError(e, serviceUrl, requestObject);
			throw new InterfaceException("9999", e.getMessage());
		}

		logger.debug(Literal.LEAVING);
		return validatedExtendedMap;

	}

	/**
	 * Method for prepare commercial request object.
	 * 
	 * @param customerDetails
	 * @return
	 */
	private BureauCommercial prepareCommercialRequestObj(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		Customer customer = customerDetails.getCustomer();

		BureauCommercial commercial = new BureauCommercial();
		commercial.setApplicationId(customer.getCustID());
		commercial.setStgUnqRefId(customer.getCustID());
		commercial.setApplicant(prepareApplicant(customerDetails));
		commercial.setCompanyName(customer.getCustShrtName());
		commercial.setCompanyAddress(prepareCompanyAddress(customerDetails.getAddressList()));
		commercial.setCompanyMobile(NiyoginUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList(),
				InterfaceConstants.PHONE_TYPE_OFF));
		commercial.setCompanyPan(commercial.getApplicant().getPan());
		commercial.setLegalEntity(customer.getLovDescCustTypeCodeName());

		logger.debug(Literal.LEAVING);
		return commercial;

	}

	/**
	 * Method for prepare applicant request object.
	 * 
	 * @param customerDetails
	 * @return
	 */
	private Applicant prepareApplicant(CustomerDetails customerDetails) {
		Customer customer = customerDetails.getCustomer();
		Applicant applicant = new Applicant();
		applicant.setFirstName(customer.getCustShrtName());
		applicant.setLastName(customer.getCustShrtName());
		applicant.setDob(NiyoginUtility.formatDate(customer.getCustDOB(), "dd-MM-yyyy"));
		applicant.setGender(InterfaceConstants.PFF_GENDER_M);
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		applicant.setPan(getPanNumber(documentList));
		applicant.setMobile(NiyoginUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList(),
				InterfaceConstants.PHONE_TYPE_PER));

		applicant.setMaritalStatus(InterfaceConstants.PFF_MARITAL_STATUS);
		applicant.setAddress(preparePersonalAddress(customerDetails.getAddressList()));
		return applicant;
	}

	/**
	 * Method for prepare address request object.
	 * 
	 * @param addressList
	 * @return
	 */
	private Address preparePersonalAddress(List<CustomerAddres> addressList) {
		CustomerAddres address = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_PER);

		City city = getCityDetails(address);

		Address personalAddress = new Address();
		String houseNo;
		if (address.getCustAddrHNbr() != null) {
			houseNo = address.getCustAddrHNbr();
		} else {
			houseNo = address.getCustFlatNbr();
		}
		personalAddress.setHouseNo(houseNo);
		personalAddress.setLandmark(address.getCustAddrStreet());
		personalAddress.setCity(city.getPCCityName());
		personalAddress.setCountry(city.getLovDescPCCountryName());
		personalAddress.setPin(address.getCustAddrZIP());
		personalAddress.setState(city.getLovDescPCProvinceName());
		return personalAddress;
	}

	/**
	 * Method for prepare address request object
	 * 
	 * @param addressList
	 * @return
	 */
	private CompanyAddress prepareCompanyAddress(List<CustomerAddres> addressList) {
		CompanyAddress companyAddress = new CompanyAddress();
		CustomerAddres address = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_OFF);
		City city = getCityDetails(address);
		String addrLines = address.getCustAddrType() + "," + address.getCustAddrHNbr() + ","
				+ address.getCustAddrStreet();
		companyAddress.setAddressLine1(addrLines);
		companyAddress.setAddressLine2(addrLines);
		companyAddress.setAddressLine3(addrLines);
		companyAddress.setCity(city.getPCCityName());
		companyAddress.setCountry(city.getLovDescPCCountryName());
		companyAddress.setPin(address.getCustAddrZIP());
		companyAddress.setState(city.getLovDescPCProvinceName());
		companyAddress.setDistrict(StringUtils.isNotBlank(address.getCustDistrict()) ? address.getCustDistrict()
				: InterfaceConstants.DEFAULT_DIST);
		return companyAddress;
	}

	/**
	 * Method for prepare Consumer request object.
	 * 
	 * @param customerDetails
	 * @return
	 */
	private BureauConsumer prepareConsumerRequestObj(CustomerDetails customerDetails) {
		BureauConsumer bureauConsumer = new BureauConsumer();
		Customer customer = customerDetails.getCustomer();
		bureauConsumer.setStgUnqRefId(customer.getCustID());
		bureauConsumer.setApplicationId(customer.getCustID());
		bureauConsumer.setAddress(prepareConsumerAddress(customerDetails.getAddressList()));
		bureauConsumer.setPersonal(prepareConsumerPersonalDetails(customerDetails));

		logger.debug(Literal.LEAVING);
		return bureauConsumer;

	}

	/**
	 * Method for prepare personaldetails request object.
	 * 
	 * @param customerDetails
	 * @return
	 */
	private PersonalDetails prepareConsumerPersonalDetails(CustomerDetails customerDetails) {
		PersonalDetails personalDetails = new PersonalDetails();
		Customer customer = customerDetails.getCustomer();

		personalDetails.setFirstName(customer.getCustShrtName());
		personalDetails.setLastName(customer.getCustShrtName());
		personalDetails.setDob(NiyoginUtility.formatDate(customer.getCustDOB(), "dd-MM-yyyy"));
		personalDetails.setGender(InterfaceConstants.PFF_GENDER_M);
		personalDetails.setMobile(NiyoginUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList(),
				InterfaceConstants.PHONE_TYPE_PER));
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		personalDetails.setPan(getPanNumber(documentList));
		personalDetails.setUid_(getPanNumber(documentList));//FIXME

		return personalDetails;
	}

	/**
	 * Method for prepare address request object.
	 * 
	 * @param addressList
	 * @return
	 */
	private ConsumerAddress prepareConsumerAddress(List<CustomerAddres> addressList) {
		ConsumerAddress consumerAddress = new ConsumerAddress();
		CustomerAddres address = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_PER);

		City city = getCityDetails(address);

		String houseNo;
		if (address.getCustAddrHNbr() != null) {
			houseNo = address.getCustAddrHNbr();
		} else {
			houseNo = address.getCustFlatNbr();
		}

		consumerAddress.setHouseNo(houseNo);
		consumerAddress.setLandmark(address.getCustAddrStreet());
		consumerAddress.setCareOf(StringUtils.isNotBlank(address.getCustAddrLine3()) ? address.getCustAddrLine3()
				: InterfaceConstants.DEFAULT_CAREOF);
		consumerAddress.setCity(city.getPCCityName());
		consumerAddress.setCountry(city.getLovDescPCCountryName());
		consumerAddress.setPin(address.getCustAddrZIP());
		consumerAddress.setState(city.getLovDescPCProvinceName());
		consumerAddress.setDistrict(StringUtils.isNotBlank(address.getCustDistrict()) ? address.getCustDistrict()
				: InterfaceConstants.DEFAULT_DIST);
		consumerAddress.setSubDistrict(StringUtils.isNotBlank(address.getCustAddrLine4()) ? address.getCustAddrLine4()
				: InterfaceConstants.DEFAULT_SUBDIST);
		return consumerAddress;
	}

	/**
	 * Method for prepare the Extended Fields Map based on the response of Experian Bureau Commercial.
	 * 
	 * @param extendedFieldMap
	 * @return
	 * @throws ParseException
	 */
	private Map<String, Object> prepareCommercialExtendedMap(Map<String, Object> extendedFieldMap)
			throws ParseException {
		List<BillPayGrid> billPayGridList = null;
		if (extendedFieldMap.get(NO_EMI_BOUNCES_IN_3_MONTHS) != null) {
			String jsonEmoBounceResponse = extendedFieldMap.get(NO_EMI_BOUNCES_IN_3_MONTHS).toString();
			Object responseObj = getResponseObject(jsonEmoBounceResponse, BpayGridResponse.class, true);
			@SuppressWarnings("unchecked")
			List<BpayGridResponse> bpayGridResponses = (List<BpayGridResponse>) responseObj;
			if (bpayGridResponses != null && !bpayGridResponses.isEmpty()) {
				billPayGridList = prepareBillpayGridList(bpayGridResponses);
			}
		} else {
			extendedFieldMap.remove(NO_EMI_BOUNCES_IN_3_MONTHS);
			extendedFieldMap.remove(NO_EMI_BOUNCES_IN_6_MONTHS);
		}

		for (Entry<String, Object> entry : extendedFieldMap.entrySet()) {
			//TODO:
			if (entry.getKey().equals(RESTRUCTURED_LOAN_AND_AMOUNT)) {
				extendedFieldMap.put(entry.getKey(), null);
			} else if (entry.getKey().equals(SUIT_FILED)) {

				if (entry.getValue() != null) {
					boolean value = StringUtils.equals(entry.getValue().toString(), "1") ? true : false;
					extendedFieldMap.put(entry.getKey(), value);
				}

			} else if (entry.getKey().equals(WILLFUL_DEFAULTER)) {

				if (entry.getValue() != null) {
					boolean value = StringUtils.equals(entry.getValue().toString(), "1") ? true : false;
					extendedFieldMap.put(entry.getKey(), value);
				}

			} else if (entry.getKey().equals(NO_EMI_BOUNCES_IN_3_MONTHS)) {

				if (billPayGridList != null && !billPayGridList.isEmpty()) {
					boolean isEmiBounce = isCommercialEMIBouncesInLastMonths(billPayGridList, 3);
					extendedFieldMap.put(entry.getKey(), isEmiBounce);
				}

			} else if (entry.getKey().equals(NO_EMI_BOUNCES_IN_6_MONTHS)) {

				if (billPayGridList != null && !billPayGridList.isEmpty()) {
					boolean isEmiBounce = isCommercialEMIBouncesInLastMonths(billPayGridList, 6);
					extendedFieldMap.put(entry.getKey(), isEmiBounce);
				}

			} else {
				extendedFieldMap.put(entry.getKey(), entry.getValue());

			}
		}

		return extendedFieldMap;
	}

	/**
	 * Method for prepare the Extended Fields Map based on the response of Experian Bureau Consumer.
	 * 
	 * @param extendedFieldMap
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> prepareConsumerExtendedMap(Map<String, Object> extendedFieldMap) throws ParseException {

		List<CAISAccountHistory> caisAccountHistories = null;
		if (extendedFieldMap.get(NO_EMI_BOUNCES_IN_3_MONTHS) != null) {
			String jsonEmiBounceResponse = extendedFieldMap.get(NO_EMI_BOUNCES_IN_3_MONTHS).toString();
			Object responseObj = getResponseObject(jsonEmiBounceResponse, CAISAccountHistory.class, true);
			caisAccountHistories = (List<CAISAccountHistory>) responseObj;
		} else {
			extendedFieldMap.remove(NO_EMI_BOUNCES_IN_3_MONTHS);
			extendedFieldMap.remove(NO_EMI_BOUNCES_IN_6_MONTHS);
		}
		for (Entry<String, Object> entry : extendedFieldMap.entrySet()) {
			//TODO:
			if (entry.getKey().equals(RESTRUCTURED_LOAN_AND_AMOUNT)) {
				extendedFieldMap.put(entry.getKey(), null);
			} else if (entry.getKey().equals(SUIT_FILED)) {

				if (entry.getValue() != null) {
					boolean value = StringUtils.equals(entry.getValue().toString(), "01") ? true : false;
					extendedFieldMap.put(entry.getKey(), value);
				}

			} else if (entry.getKey().equals(WILLFUL_DEFAULTER)) {

				if (entry.getValue() != null) {
					boolean value = StringUtils.equals(entry.getValue().toString(), "02") ? true : false;
					extendedFieldMap.put(entry.getKey(), value);
				}

			} else if (entry.getKey().equals(NO_EMI_BOUNCES_IN_3_MONTHS)) {

				if (caisAccountHistories != null && !caisAccountHistories.isEmpty()) {
					boolean isEmiBounce = isConsumerEMIBouncesInLastMonths(caisAccountHistories, 3);
					extendedFieldMap.put(entry.getKey(), isEmiBounce);
				}

			} else if (entry.getKey().equals(NO_EMI_BOUNCES_IN_6_MONTHS)) {

				if (caisAccountHistories != null && !caisAccountHistories.isEmpty()) {
					boolean isEmiBounce = isConsumerEMIBouncesInLastMonths(caisAccountHistories, 6);
					extendedFieldMap.put(entry.getKey(), isEmiBounce);
				}

			} else if (entry.getKey().equals(STATUS)) {
				String acc_Status = String.valueOf(extendedFieldMap.get(STATUS));
				if (StringUtils.equals(acc_Status, WRITEOFF)) {
					extendedFieldMap.put("EXPBWRUTEOFF", true);
				} else if (StringUtils.equals(acc_Status, SETTLE)) {
					extendedFieldMap.put("EXPBSETTLED", true);
				}
			} else {
				extendedFieldMap.put(entry.getKey(), entry.getValue());
			}

		}
		return extendedFieldMap;

	}

	/**
	 * Method for prepare BillPayGrid object based on the Experian Commercial response for calculating the EMI-Bounces.
	 * 
	 * @param bpayGridResponseList
	 * @return
	 * @throws ParseException
	 */
	private List<BillPayGrid> prepareBillpayGridList(List<BpayGridResponse> bpayGridResponseList)
			throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		List<BillPayGrid> billPayGridList = new ArrayList<BillPayGrid>(1);
		for (BpayGridResponse bpayGridRes : bpayGridResponseList) {
			BillPayGrid bpayGrid = new BillPayGrid();
			bpayGrid.setBillPayDate(dateFormat.parse("01-" + bpayGridRes.getMonth() + "-" + bpayGridRes.getYear()));
			bpayGrid.setAssetClassification(bpayGridRes.getAssetClassification());
			billPayGridList.add(bpayGrid);
		}

		return billPayGridList;
	}

	/**
	 * check the assestClasification with three types("Blank","?","S") other than these three it returns true otherwise
	 * return false
	 * 
	 * @param billPayGridList
	 * @param bpayGridResponses.size()
	 * @return
	 * @throws ParseException
	 */
	private boolean isCommercialEMIBouncesInLastMonths(List<BillPayGrid> billPayGridList, int numbOfMnths)
			throws ParseException {
		Collections.sort(billPayGridList, new BpayGridResponseComparator());

		for (int i = 0; i < billPayGridList.size(); i++) {
			BillPayGrid bpayGrid = billPayGridList.get(i);
			if (NiyoginUtility.getMonthsBetween(appDate, bpayGrid.getBillPayDate()) <= numbOfMnths) {
				String assestClasification = billPayGridList.get(i).getAssetClassification();
				if (assestClasification.equals("Blank") || assestClasification.equals("?")
						|| assestClasification.equals("S")) {
					continue;
				} else {
					return true;
				}
			} else {
				break;
			}

		}
		return false;
	}

	/**
	 * check the assestClasification with three types("Blank","?","S") other than these three it returns true otherwise
	 * return false
	 * 
	 * @param caisAccountHistories
	 * @param no
	 * @return
	 * @throws ParseException
	 */
	private boolean isConsumerEMIBouncesInLastMonths(List<CAISAccountHistory> caisAccountHistories, int numbOfMnths)
			throws ParseException {
		Collections.sort(caisAccountHistories, new CAISAccountHistoryComparator());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		for (int i = 0; i < caisAccountHistories.size(); i++) {
			CAISAccountHistory casisHistory = caisAccountHistories.get(i);
			Date bpayDate = dateFormat.parse("01-" + casisHistory.getMonth() + "-" + casisHistory.getYear());
			if (NiyoginUtility.getMonthsBetween(appDate, bpayDate) <= numbOfMnths) {
				String assestClasification = caisAccountHistories.get(i).getAssetClassification();
				if (assestClasification.equals("Blank") || assestClasification.equals("?")
						|| assestClasification.equals("S")) {
					continue;
				} else {
					return true;
				}
			} else {
				break;
			}

		}
		return false;
	}

	/**
	 * 
	 * This Comparator class is used to sort the BillPayGrid based on their bpayDate H to L
	 */
	public class BpayGridResponseComparator implements Comparator<BillPayGrid> {
		@Override
		public int compare(BillPayGrid arg0, BillPayGrid arg1) {
			return arg1.getBillPayDate().compareTo(arg0.getBillPayDate());
		}

	}

	/**
	 * 
	 * This Comparator class is used to sort the CAISAccountHistory based on their Month and Year H to L
	 */
	public class CAISAccountHistoryComparator implements Comparator<CAISAccountHistory> {
		@Override
		public int compare(CAISAccountHistory arg0, CAISAccountHistory arg1) {

			return (arg0.getMonth() + arg0.getYear() - arg1.getMonth() + arg1.getYear());
		}
	}

	/**
	 * Method for prepare data and logging
	 * 
	 * @param consumerRequest
	 * @param reference
	 */
	private void doInterfaceLogging(Object requestObj, String reference) {
		InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, requestObj, jsonResponse, reqSentOn,
				status, errorCode, errorDesc, reference);
		logInterfaceDetails(interfaceLogDetail);
	}

	public void setConsumerUrl(String consumerUrl) {
		this.consumerUrl = consumerUrl;
	}

	public void setCommercialUrl(String commercialUrl) {
		this.commercialUrl = commercialUrl;
	}

}
