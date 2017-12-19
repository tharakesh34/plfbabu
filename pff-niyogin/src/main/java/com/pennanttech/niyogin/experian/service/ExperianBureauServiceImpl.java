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
		applicant.setPan(NiyoginUtility.getDocumentNumber(documentList, InterfaceConstants.DOC_TYPE_PAN));
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
		personalDetails.setPan(NiyoginUtility.getDocumentNumber(documentList, InterfaceConstants.DOC_TYPE_PAN));
		personalDetails.setUid_(NiyoginUtility.getDocumentNumber(documentList, InterfaceConstants.DOC_TYPE_UID));

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

	private String getResponse() {
		//String consumerResponse = "{ \"statusCode\":200, \"message\":\"Experian Bureau Consumer report extracted\", \"data\":{ \"TotalCAPS_Summary\":{ \"TotalCAPSLast7Days\":\"17\", \"TotalCAPSLast30Days\":\"17\", \"TotalCAPSLast90Days\":\"17\", \"TotalCAPSLast180Days\":\"17\" }, \"CAIS_Account\":{ \"CAIS_Summary\":{ \"Credit_Account\":{ \"CreditAccountTotal\":\"1\", \"CreditAccountActive\":\"1\", \"CreditAccountDefault\":\"0\", \"CreditAccountClosed\":\"0\", \"CADSuitFiledCurrentBalance\":\"0\" }, \"Total_Outstanding_Balance\":{ \"Outstanding_Balance_Secured\":\"61000\", \"Outstanding_Balance_Secured_Percentage\":\"100\", \"Outstanding_Balance_UnSecured\":\"0\", \"Outstanding_Balance_UnSecured_Percentage\":\"0\", \"Outstanding_Balance_All\":\"61000\" } }, \"CAIS_Account_DETAILS\":{ \"Identification_Number\":\"TELXXXXXXXX\", \"Subscriber_Name\":\"XXXXXXXXXX\", \"Account_Number\":\"XXXXXXXXXX\", \"Portfolio_Type\":\"M\", \"Account_Type\":\"02\", \"Open_Date\":\"20100301\", \"Highest_Credit_or_Original_Loan_Amount\":\"62500\", \"Terms_Duration\":[  ], \"Terms_Frequency\":[  ], \"Scheduled_Monthly_Payment_Amount\":[  ], \"Account_Status\":\"32\", \"Payment_Rating\":\"6\", \"Payment_History_Profile\":\"61666???????????????????????????????\", \"Special_Comment\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\", \"Original_Charge_off_Amount\":[  ], \"Date_Reported\":\"20161010\", \"Date_Of_First_Delinquency\":[  ], \"Date_Closed\":[  ], \"Date_of_Last_Payment\":[  ], \"SuitFiledWillfulDefaultWrittenOffStatus\":[  ], \"SuitFiled_WilfulDefault\":[  ], \"Written_off_Settled_Status\":\"03\", \"Value_of_Credits_Last_Month\":[  ], \"Occupation_Code\":[  ], \"Settlement_Amount\":[  ], \"Value_of_Collateral\":[  ], \"Type_of_Collateral\":[  ], \"Written_Off_Amt_Total\":[  ], \"Written_Off_Amt_Principal\":[  ], \"Rate_of_Interest\":[  ], \"Repayment_Tenure\":\"0\", \"Promotional_Rate_Flag\":[  ], \"Income\":[  ], \"Income_Indicator\":[  ], \"Income_Frequency_Indicator\":[  ], \"DefaultStatusDate\":[  ], \"LitigationStatusDate\":[  ], \"WriteOffStatusDate\":[  ], \"DateOfAddition\":\"20160510\", \"CurrencyCode\":\"INR\", \"Subscriber_comments\":[  ], \"Consumer_comments\":[  ], \"AccountHoldertypeCode\":\"2\", \"CAIS_Account_History\":[ { \"Year\":\"2016\", \"Month\":\"10\", \"Days_Past_Due\":\"190\", \"Asset_Classification\":\"?\" }, { \"Year\":\"2016\", \"Month\":\"09\", \"Days_Past_Due\":\"190\", \"Asset_Classification\":\"?\" }, { \"Year\":\"2016\", \"Month\":\"08\", \"Days_Past_Due\":\"40\", \"Asset_Classification\":\"?\" }, { \"Year\":\"2016\", \"Month\":\"07\", \"Days_Past_Due\":\"200\", \"Asset_Classification\":\"?\" }, { \"Year\":\"2016\", \"Month\":\"06\", \"Days_Past_Due\":\"200\", \"Asset_Classification\":\"?\" }, { \"Year\":\"2016\", \"Month\":\"05\", \"Days_Past_Due\":\"200\", \"Asset_Classification\":\"?\" } ], \"Advanced_Account_History\":[ { \"Year\":\"2016\", \"Month\":\"10\", \"Cash_Limit\":[  ], \"Credit_Limit_Amount\":\"62500\", \"Actual_Payment_Amount\":[  ], \"EMI_Amount\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\" }, { \"Year\":\"2016\", \"Month\":\"09\", \"Cash_Limit\":[  ], \"Credit_Limit_Amount\":\"62500\", \"Actual_Payment_Amount\":[  ], \"EMI_Amount\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\" }, { \"Year\":\"2016\", \"Month\":\"08\", \"Cash_Limit\":[  ], \"Credit_Limit_Amount\":\"62500\", \"Actual_Payment_Amount\":[  ], \"EMI_Amount\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\" }, { \"Year\":\"2016\", \"Month\":\"07\", \"Cash_Limit\":[  ], \"Credit_Limit_Amount\":\"62500\", \"Actual_Payment_Amount\":[  ], \"EMI_Amount\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\" }, { \"Year\":\"2016\", \"Month\":\"06\", \"Cash_Limit\":[  ], \"Credit_Limit_Amount\":\"62500\", \"Actual_Payment_Amount\":[  ], \"EMI_Amount\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\" }, { \"Year\":\"2016\", \"Month\":\"05\", \"Cash_Limit\":[  ], \"Credit_Limit_Amount\":\"62500\", \"Actual_Payment_Amount\":[  ], \"EMI_Amount\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\" } ], \"CAIS_Holder_Details\":{ \"Surname_Non_Normalized\":\"VIKAS\", \"First_Name_Non_Normalized\":\"JEO\", \"Middle_Name_1_Non_Normalized\":[  ], \"Middle_Name_2_Non_Normalized\":[  ], \"Middle_Name_3_Non_Normalized\":[  ], \"Alias\":[  ], \"Gender_Code\":\"2\", \"Income_TAX_PAN\":\"BUQPJ2311S\", \"Passport_Number\":\"J4567899\", \"Voter_ID_Number\":\"NNX4006259\", \"Date_of_birth\":\"19870726\" }, \"CAIS_Holder_Address_Details\":{ \"First_Line_Of_Address_non_normalized\":\"DRFGRHTFTR\", \"Second_Line_Of_Address_non_normalized\":\"CHEMBUR\", \"Third_Line_Of_Address_non_normalized\":[  ], \"City_non_normalized\":\"MUMBAI\", \"Fifth_Line_Of_Address_non_normalized\":[  ], \"State_non_normalized\":\"27\", \"ZIP_Postal_Code_non_normalized\":\"401107\", \"CountryCode_non_normalized\":\"IB\", \"Address_indicator_non_normalized\":[  ], \"Residence_code_non_normalized\":[  ] }, \"CAIS_Holder_Phone_Details\":{ \"Telephone_Number\":\"9003170611\", \"Telephone_Type\":[  ] }, \"CAIS_Holder_ID_Details\":[ { \"Income_TAX_PAN\":\"BUQPJ2311S\", \"PAN_Issue_Date\":[  ], \"PAN_Expiration_Date\":[  ], \"Passport_Number\":\"J4567899\", \"Passport_Issue_Date\":[  ], \"Passport_Expiration_Date\":[  ], \"Voter_ID_Number\":\"NNX4006259\", \"Voter_ID_Issue_Date\":[  ], \"Voter_ID_Expiration_Date\":[  ], \"Driver_License_Number\":[  ], \"Driver_License_Issue_Date\":[  ], \"Driver_License_Expiration_Date\":[  ], \"EMailId\":[  ] }, { \"Income_TAX_PAN\":\"BUQPJ2311S\", \"PAN_Issue_Date\":[  ], \"PAN_Expiration_Date\":[  ], \"Passport_Number\":\"J4567899\", \"Passport_Issue_Date\":[  ], \"Passport_Expiration_Date\":[  ], \"Voter_ID_Number\":\"NNX4006259\", \"Voter_ID_Issue_Date\":[  ], \"Voter_ID_Expiration_Date\":[  ], \"Driver_License_Number\":[  ], \"Driver_License_Issue_Date\":[  ], \"Driver_License_Expiration_Date\":[  ], \"EMailId\":[  ] } ] } } } }";
		String commercialResponse = "{   \"statusCode\": 200,   \"message\": \"Experian Bureau Commercial report extracted\",   \"data\": {     \"PAN\": \"\",     \"PSUMMARY\": {       \"SegmentCode\": \"PSUMMARY\",       \"GRANTOR\": {         \"SegmentCode\": \"GRANTOR\",         \"TotalCreditProviders\": \"1\",         \"CurrentCreditProviders\": \"1\",         \"TotalSameCreditProviders\": \"0\",         \"TotalOtherCreditProviders\": \"1\"       },       \"ACCSUM\": {         \"SegmentCode\": \"ACCSUM\",         \"TotalCreditAccount\": \"1\",         \"TotalActiveAccounts\": \"0\",         \"TotalClosedAccounts\": \"0\",         \"MonthFirstToCredit\": \"2\",         \"YearFirstToCredit\": \"2001\",         \"TotalActiveSTDAccount\": \"1\",         \"TotalActiveSUBAccount\": \"0\",         \"TotalActiveSMAAccount\": \"0\",         \"TotalActiveDBTAccount\": \"0\",         \"TotalActiveLSSAccount\": \"0\"       },       \"GUARANTO\": {         \"SegmentCode\": \"GUARANTO\",         \"TotalGuarantors\": \"0\"       },       \"CURRCYSTAT\": {         \"SegmentCode\": \"CURRCYSTAT\",         \"FundCurrentBalance\": [],         \"NonFundCurrentBalance\": [],         \"ShortTermsCurrentBalance\": [],         \"LongTermsCurrentBalance\": [],         \"WilfullDefaultCurrentBalance\": [],         \"SuitFiledCurrentBalance\": []       },       \"CURRCYCNCT\": {         \"SegmentCode\": \"CURRCYCNT\",         \"CurrencyCd\": \"INR\",         \"TotalNoFundedAccountType\": \"1\",         \"TotalNoNonFundedAccountType\": \"0\",         \"TotalNoShortTermAccountType\": \"1\",         \"TotalNoLongTermAccountType\": \"0\",         \"TotalNoWDAccountType\": \"0\",         \"TotalNoSFAccountType\": \"0\",         \"TotalNoDISHNR_CHQ\": \"1\"       },       \"CREDTYPE\": {         \"SegmentCode\": \"CREDTYPE\",         \"CurrencyCd\": [],         \"TotalCurrentBalance\": \"76118952\",         \"TotalCreditTypeNo\": \"1\",         \"PctTotalOwnCurrBalance\": \"0.00\",         \"PctTotalPVTCurrBalance\": \"0.00\",         \"PctTotalPUBCurrBalance\": \"0.00\",         \"PctTotalMNCCurrBalance\": \"0.00\",         \"PctTotalNBFC_OthCurrBalance\": \"100.00\",         \"PctTotalStandardCreditType\": \"100\",         \"PctTotalSubStandardCreditType\": \"0\",         \"PctTotalDoubtfulCreditType\": \"0\",         \"PctTotalLossCreditType\": \"0\",         \"PctTotalSpecMentionCreditType\": \"0\",         \"ACCTYPINFO\": {           \"SegmentCode\": \"ACCTYPINFO\",           \"AccountType\": \"190\",           \"CurrencyCd\": \"INR\",           \"TotalCurrentBalance\": \"11126222\",           \"TotalCreditTypeNo\": \"1\",           \"PctTotalStandardCreditType\": \"100\",           \"PctTotalSubStandardCreditType\": \"0\",           \"PctTotalDoubtfulCreditType\": \"0\",           \"PctTotalLossCreditType\": \"0\",           \"PctTotalSpecMentionCreditType\": \"0\"         }       },       \"ENQATTR\": {         \"SegmentCode\": \"ENQATTR\",         \"MostRecentEnqDate\": \"26092017\",         \"TotEnq\": \"375\",         \"TotEnq7days\": \"115\",         \"TotEnq30days\": \"161\",         \"TotEnq90days\": \"374\",         \"TotEnq180days\": \"375\",         \"TotEnq1month\": \"128\",         \"TotEnq2-3month\": \"246\",         \"TotEnq4-6month\": \"1\",         \"TotEnq7-12month\": [],         \"TotEnq12-24month\": [],         \"TotEnqAbove24month\": [],         \"OthEnq1month\": \"128\",         \"OthEnq2-3month\": \"246\",         \"OthEnq4-6month\": \"1\",         \"OthEnq7-12month\": [],         \"OthEnq12-24month\": [],         \"OthEnqAbove24month\": [],         \"OthMostRecentEnqDate\": \"26092017\",         \"OwnEnq1month\": [],         \"OwnEnq2-3month\": [],         \"OwnEnq4-6month\": [],         \"OwnEnq7-12month\": [],         \"OwnEnq12-24month\": [],         \"OwnEnqAbove24month\": [],         \"OwnMostRecentEnqDate\": []       },       \"CREDTYPEDERIVATIVES\": {         \"SegmentCode\": \"CREDTYPDER\",         \"CreditFacilities\": {           \"SegmentCode\": \"CREDFAC\",           \"TotNoBorrower\": \"0\",           \"TotNoGuarantor\": \"0\",           \"TotNoActiveBorr_Guran\": \"0\",           \"TotNoSelfBorrower\": \"0\",           \"TotNoSelfGuarantor\": \"0\",           \"TotNoSelfActiveBorr_Guran\": \"0\",           \"TotNoPSUBorrower\": \"0\",           \"TotNoPSUGuarantor\": \"0\",           \"TotNoPSUActiveBorr_Guran\": \"0\",           \"TotNoPVTBorrower\": \"0\",           \"TotNoPVTGuarantor\": \"0\",           \"TotNoPVTActiveBorr_Guran\": \"0\",           \"TotNoMNCBorrower\": \"0\",           \"TotNoMNCGuarantor\": \"0\",           \"TotNoMNCActiveBorr_Guran\": \"0\",           \"TotNoNBFCBorrower\": \"0\",           \"TotNoNBFCGuarantor\": \"0\", getResponse           \"TotNoNonSelfBorrower\": \"0\",           \"TotNoNonSelfGuarantor\": \"0\",           \"TotNoNonSelfActiveBorr_Guran\": \"0\"         },         \"TotalOutstandingBalance\": {           \"SegmentCode\": \"TOTBAL\",           \"TotBalBorrower\": \"0\",           \"TotBalGuarantor\": \"0\",           \"TotBalSelfBorrower\": \"0\",           \"TotBalSelfGuarantor\": \"0\",           \"TotBalPSUBorrower\": \"0\",           \"TotBalPSUGuarantor\": \"0\",           \"TotBalPVTBorrower\": \"0\",           \"TotBalPVTGuarantor\": \"0\",           \"TotBalMNCBorrower\": \"0\",           \"TotBalMNCGuarantor\": \"0\",           \"TotBalNBFCBorrower\": \"0\",           \"TotBalNBFCGuarantor\": \"0\",           \"TotBalNonSelfBorrower\": \"0\",           \"TotBalNonSelfGuarantor\": \"0\"         },         \"RecentOpenDate\": {           \"SegmentCode\": \"RCTOPNDT\",           \"RecentOpenDt\": [],           \"RecentOpenDtSelf\": [],           \"RecentOpenDtSelfActive\": [],           \"RecentOpenDtPSU\": [],           \"RecentOpenDtPVT\": [],           \"RecentOpenDtMNC\": [],           \"RecentOpenDtNBFC\": [],           \"RecentOpenDtNonSelf\": []         },         \"DelCreditFacilities\": {           \"SegmentCode\": \"DELCREDFAC\",           \"DelTotNoBorrower\": \"0\",           \"DelTotNoGuarantor\": \"0\",           \"DelTotNoSelfBorrower\": \"0\",           \"DelTotNoSelfGuarantor\": \"0\",           \"DelTotNoPSUBorrower\": \"0\",           \"DelTotNoPSUGuarantor\": \"0\",           \"DelTotNoPVTBorrower\": \"0\",           \"DelTotNoPVTGuarantor\": \"0\",           \"DelTotNoMNCBorrower\": \"0\",           \"DelTotNoMNCGuarantor\": \"0\",           \"DelTotNoNBFCBorrower\": \"0\",           \"DelTotNoNBFCGuarantor\": \"0\",           \"DelTotNoNonSelfBorrower\": \"0\",           \"DelTotNoNonSelfGuarantor\": \"0\"         },         \"DelOutstandingBalance\": {           \"SegmentCode\": \"DELBALANCE\",           \"DelTotBalBorrower\": \"0\",           \"DelTotBalGuarantor\": \"0\",           \"DelTotBalSelfBorrower\": \"0\",           \"DelTotBalSelfGuarantor\": \"0\",           \"DelTotBalPSUBorrower\": \"0\",           \"DelTotBalPSUGuarantor\": \"0\",           \"DelTotBalPVTBorrower\": \"0\",           \"DelTotBalPVTGuarantor\": \"0\",           \"DelTotBalMNCBorrower\": \"0\",           \"DelTotBalMNCGuarantor\": \"0\",           \"DelTotBalNBFCBorrower\": \"0\",           \"DelTotBalNBFCGuarantor\": \"0\",           \"DelTotBalNonSelfBorrower\": \"0\",           \"DelTotBalNonSelfGuarantor\": \"0\"         }       },       \"ACTTYPEDPDBALDerivativesNonSelf\": {         \"SegmentCode\": \"ACCNONSELF\",         \"AccountTypeSelf\": \"190\",         \"DPDDerivativesNonSelf\": {           \"SegmentCode\": \"DPDNONSELF\",           \"TotNoAccts_ACCtype\": [],           \"TotNoAccts_0DPD\": [],           \"TotNoAccts_1_30DPD\": [],           \"TotNoAccts_31_60DPD\": [],           \"TotNoAccts_61_90DPD\": [],           \"TotNoAccts_91_365DPD\": [],           \"TotNoAccts_366_730DPD\": [],           \"TotNoAccts_731_900DPD\": [],           \"TotNNoAccts_STD_Asset\": [],           \"TotNoAccts_SUB_Asset\": [],           \"TotNoAccts_DBT_Asset\": [],           \"TotNoAccts_SMA_Asset\": [],           \"TotNoAccts_LSS_Asset\": []         },         \"BALDerivativesNonSelf\": {           \"SegmentCode\": \"BALNONSELF\",           \"TotBALAccts_ACCtype\": [],           \"TotBALAccts_0DPD\": [],           \"TotBALAccts_1_30DPD\": [],           \"TotBALAccts_31_60DPD\": [],           \"TotBALAccts_61_90DPD\": [],           \"TotBALAccts_91_365DPD\": [],           \"TotBALAccts_366_730DPD\": [],           \"TotBALAccts_731_900DPD\": [],           \"TotBALAccts_STD_Asset\": [],           \"TotBALAccts_SUB_Asset\": [],           \"TotBALAccts_DBT_Asset\": [],           \"TotBALAccts_SMA_Asset\": [],           \"TotBALAccts_LSS_Asset\": []         }       }     },     \"COMMCRED\": {       \"SegmentCode\": \"COMMCRED\",       \"AccountNumber\": \"XXXXXXXXX8554\",       \"AccountPortfolioType\": \"I\",       \"AccountType\": \"190\",       \"AccountTypeDetail\": \"6\",       \"AccountCurrency\": \"INR\",       \"AccountFinRespTypeCd\": [],       \"AccountStatus\": \"O\",       \"AccountStatusDetail\": \"21\",       \"PaymentStatus\": \"45\",       \"AccountOpenDate\": \"24022001\",       \"AccountClosedDate\": [],       \"SanctionedAmount\": \"-1\",       \"AssetClassification\": \"S\",       \"CurrentBalance\": \"11126222\",       \"WilfulDefaultStatus\": \"0\",       \"WilfulDefaultDate\": [],       \"WilfulDefaultAmount\": [],       \"SuitFiledStatus\": \"0\",       \"SuitFiledDate\": [],       \"SuitFiledAmount\": \"-1\",       \"WrittenOffAmount\": \"-1\",       \"AmountOverdue\": \"0\",       \"LastReportedDate\": \"31082015\",       \"SanctionDate\": \"24022001\",       \"PaymentStatusDetail\": \"6\",       \"LoanExpiryDate\": \"24022017\",       \"LoanRenewDate\": \"23022017\",       \"RestructoringReason\": [],       \"RestructoringReasonDetail\": [],       \"SecurityCoverage\": [],       \"GuranteeCoverage\": \"5\",       \"BankRemark\": [],       \"CREDITOR\": {         \"SegmentCode\": \"CREDITOR\",         \"CreditorIndustryCd\": [],         \"CreditorName\": \"State Bank of Travancore\"       },       \"BORROWER\": {         \"SegmentCode\": \"BORROWER\",         \"BorrowerName\": \"KRANTI & KIARA ENTERPRISES\",         \"BorrowerLastReportedDate\": \"31082015\",         \"BorrowerPAN\": \"AAAAR5455N\",         \"BorrowerAddress\": \"8TH FLOOR NIKE BUILDING,S.K.MARG,WORLIMUMBAI, MUMBAI, MAHARASHTRA, 400021, IND\",         \"BorrowerCity\": \"MUMBAI\",         \"BorrowerPINCode\": \"400021\",         \"BorrowerLocationType\": \"14\",         \"StartDate\": [],         \"AccountStatusDate\": \"26072003\",         \"BorrowerCountry\": \"IND\",         \"BorrowerCIN\": \"U74900MH2006PTC161680\",         \"BorrowerTIN\": \"19430737070\",         \"BorrowerServiceTaxNo\": [],         \"BorrowerOtherID\": \"KS201020160136\",         \"BorrowerLegalConst\": [],         \"BorrowerCat\": \"06\",         \"BorrowerIndType\": \"27\",         \"BorrowerClassActivity1\": \"6\",         \"BorrowerClassActivity2\": [],         \"BorrowerClassActivity3\": [],         \"BorrowerSicCode\": [],         \"BorrowerSalesFigure\": \"370000000\",         \"BorrowerYear\": \"15080202\",         \"BorrowerEmpNo\": \"845\",         \"BorrowerCreditRating\": [],         \"BorrowerAssAuthority\": [],         \"BorrowerCreditRatingIssueDt\": [],         \"BorrowerCreditRatingExpDt\": [],         \"BorrowerMobile\": \"9820111555\",         \"BorrowerTelephoneNo\": [],         \"BorrowerTelephoneAreaCode\": [],         \"BorrowerFaxNo\": [],         \"BorrowerFaxAreaCode\": []       },       \"GUARANTOR\": [         {           \"SegmentCode\": \"GUARANTOR\",           \"GuarantorName\": [],           \"GuarantorType\": \"2\",           \"GuarantorPAN\": [],           \"GuarantorLocationType\": \"1\",           \"GuarantorAddress\": \"105, MAHI APARTMENTS, NAVRANGPURA,, D.N.NAGAR, AHMEDABAD, 380006, IND\",           \"GuarantorCity\": \"AHMEDABAD\",           \"GuarantorPINCode\": \"380006\",           \"GuarantorTelephone\": \"919825018835\",           \"GuarantorLastReportedDate\": \"31082015\",           \"GuarantorStartDate\": [],           \"GuarantorDOB\": \"07011981\",           \"GuarantorVoterID\": \"ADE5736799\",           \"GuarantorPassport\": \"G9910655\",           \"GuarantorDLNo\": [],           \"GuarantorUID\": \"117632100242\",           \"GuarantorRationCardNo\": [],           \"GuarantorDIN\": [],           \"GuarantorCIN\": [],           \"GuarantorTIN\": [],           \"GuarantorBUSSCat\": [],           \"GuarantorBUSSIndType\": [],           \"GuarantorIncorpDt\": [],           \"GuarantorRegNo\": [],           \"GuarantorServiceTaxNo\": []         },         {           \"SegmentCode\": \"GUARANTOR\",           \"GuarantorName\": \"SHARJA POLYMERS\",           \"GuarantorType\": \"1\",           \"GuarantorPAN\": [],           \"GuarantorLocationType\": \"1\",           \"GuarantorAddress\": \"103,CHITRARATH OPP HOTEL PRESIDENT,NAVRANGPURA,, AHMEDABAD, 380006, IND\",           \"GuarantorCity\": \"AHMEDABAD\",           \"GuarantorPINCode\": \"380006\",           \"GuarantorTelephone\": \"919828016835\",           \"GuarantorLastReportedDate\": \"31082015\",           \"GuarantorStartDate\": [],           \"GuarantorDOB\": [],           \"GuarantorVoterID\": [],           \"GuarantorPassport\": [],           \"GuarantorDLNo\": [],           \"GuarantorUID\": [],           \"GuarantorRationCardNo\": [],           \"GuarantorDIN\": [],           \"GuarantorCIN\": [],           \"GuarantorTIN\": [],           \"GuarantorBUSSCat\": [],           \"GuarantorBUSSIndType\": [],           \"GuarantorIncorpDt\": [],           \"GuarantorRegNo\": [],           \"GuarantorServiceTaxNo\": []         }       ],       \"STMTALRT\": {         \"SegmentCode\": \"STMTALRT\",         \"StatementDate\": [],         \"StatementExpirationDate\": [],         \"StatementTypeCode\": [],         \"ContactPhone\": [],         \"StatementText\": []       },       \"BPAYGRID\": [         {           \"SegmentCode\": \"BPAYGRID\",           \"TimePeriodInd\": \"M\",           \"Year\": \"2015\",           \"WeekNumber\": \"6\",           \"Monthvalue\": \"Aug\",           \"PaymentStatusValue\": \"45\",           \"DaysPastDue\": [],           \"AssetClassification\": \"S\",           \"HDETAILS\": {             \"SegmentCode\": \"HDETAILS\",             \"BalanceAmt\": \"11126222\"           }         },         {           \"SegmentCode\": \"BPAYGRID\",           \"TimePeriodInd\": \"M\",           \"Year\": \"2015\",           \"WeekNumber\": \"5\",           \"Monthvalue\": \"Jul\",           \"PaymentStatusValue\": \"\",           \"DaysPastDue\": [],           \"AssetClassification\": \"\",           \"HDETAILS\": {             \"SegmentCode\": \"HDETAILS\",             \"BalanceAmt\": \"\"           }         },         {           \"SegmentCode\": \"BPAYGRID\",           \"TimePeriodInd\": \"M\",           \"Year\": \"2015\",           \"WeekNumber\": \"5\",           \"Monthvalue\": \"Jun\",           \"PaymentStatusValue\": \"\",           \"DaysPastDue\": [],           \"AssetClassification\": \"\",           \"HDETAILS\": {             \"SegmentCode\": \"HDETAILS\",             \"BalanceAmt\": \"\"           }         },         {           \"SegmentCode\": \"BPAYGRID\",           \"TimePeriodInd\": \"M\",           \"Year\": \"2015\",           \"WeekNumber\": \"5\",           \"Monthvalue\": \"May\",           \"PaymentStatusValue\": \"\",           \"DaysPastDue\": [],           \"AssetClassification\": \"\",           \"HDETAILS\": {             \"SegmentCode\": \"HDETAILS\",             \"BalanceAmt\": \"\"           }         },         {           \"SegmentCode\": \"BPAYGRID\",           \"TimePeriodInd\": \"M\",           \"Year\": \"2015\",           \"WeekNumber\": \"5\",           \"Monthvalue\": \"Apr\",           \"PaymentStatusValue\": \"\",           \"DaysPastDue\": [],           \"AssetClassification\": \"\",           \"HDETAILS\": {             \"SegmentCode\": \"HDETAILS\",             \"BalanceAmt\": \"\"           }         }       ],       \"CHQDIS\": {         \"SegmentCode\": \"CHQDIS\",         \"CountInLast3Months\": \"0\",         \"CountInLast6Months\": \"0\",         \"CountInLast9Months\": \"0\",         \"CountInLast12Months\": \"0\",         \"DSHNR_CHEQUE\": [           {             \"SegmentCode\": \"DSHNRCHEQ\",             \"DSHNR_CHEQUE_DT\": \"20082016\",             \"DSHNR_CHEQUE_AM\": \"100000\",             \"DSHNR_CHEQUE_NB\": \"590261\",             \"DSHNR_CHEQUE_CT\": \"1\",             \"DSHNR_CHEQUE_ISSUE_DT\": [],             \"DSHNR_RSN_CD\": []           },           {             \"SegmentCode\": \"DSHNRCHEQ\",             \"DSHNR_CHEQUE_DT\": \"11082016\",             \"DSHNR_CHEQUE_AM\": \"100000\",             \"DSHNR_CHEQUE_NB\": \"247784\",             \"DSHNR_CHEQUE_CT\": \"2\",             \"DSHNR_CHEQUE_ISSUE_DT\": [],             \"DSHNR_RSN_CD\": []           }         ]       }     }   } }";
		return commercialResponse;
	}

	public void setConsumerUrl(String consumerUrl) {
		this.consumerUrl = consumerUrl;
	}

	public void setCommercialUrl(String commercialUrl) {
		this.commercialUrl = commercialUrl;
	}

}
