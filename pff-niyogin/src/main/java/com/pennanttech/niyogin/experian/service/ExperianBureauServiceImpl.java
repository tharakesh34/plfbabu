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
	private static final Logger	logger						= Logger.getLogger(ExperianBureauServiceImpl.class);

	private final String		commercialConfigFileName	= "experianBureauCommercial.properties";
	private final String		consumerConfigFileName		= "experianBureauConsumer.properties";
	private String				consumerUrl;
	private String				commercialUrl;

	private String				CONSUMER_CAIS_HIST			= "$.data.CAIS_Account.CAIS_Account_DETAILS.CAIS_Account_History";
	private String				COMMERCIAL_BPAYGRID			= "$.data.COMMCRED.BPAYGRID";

	//Experian Bureau
	public static final String	REQ_SEND					= "REQSENDEXPBURU";
	public static final String	STATUSCODE					= "STATUSEXPBURU";
	public static final String	RSN_CODE					= "REASONEXPBURU";
	public static final String	REMARKS						= "REMARKSEXPBURU";

	public static final String	NO_OF_ENQUIRES				= "NOOFENQUIRES";
	public static final String	RESTRUCTURED_FLAG			= "RESTRUCTUREDLOAN";
	public static final String	SUIT_FILED_FLAG				= "SUITFILED";
	public static final String	WILLFUL_DEFAULTER_FLAG		= "WILLFULDEFAULTER";
	public static final String	WRITE_OFF_FLAG				= "EXPBWRUTEOFF";
	public static final String	SETTLED_FLAG_FLAG			= "EXPBSETTLED";
	public static final String	NO_EMI_BOUNCES_IN3M			= "EMI3MONTHS";
	public static final String	NO_EMI_BOUNCES_IN6M			= "EMI6MNTHS";
	public static final String	STATUS						= "STATUS";
	public static final String	WRITEOFF					= "25";
	public static final String	SETTLE						= "23";

	private Date				appDate						= getAppDate();

	/**
	 * Method for execute Experian Bureau service<br>
	 * - Execute Commercial bureau service for SME and CORP customers<br>
	 * - Execute Consumer service for RETAIL customer.
	 * 
	 * @param auditHeader
	 */
	@Override
	public AuditHeader executeExperianBureau(AuditHeader auditHeader) throws InterfaceException, ParseException {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();

		//process the Applicant.
		Map<String, Object> appplicationdata = null;
		appplicationdata = executeBureau(financeDetail, customerDetails);
		prepareResponseObj(appplicationdata, financeDetail);

		//process Co_Applicant's
		List<JointAccountDetail> coapplicants = financeDetail.getJountAccountDetailList();
		if (coapplicants == null || coapplicants.isEmpty()) {
			return auditHeader;
		}
		List<Long> coApplicantIDs = new ArrayList<Long>(1);
		for (JointAccountDetail coApplicant : coapplicants) {
			coApplicantIDs.add(coApplicant.getCustID());
		}

		List<CustomerDetails> coApplicantCustomers = getCoApplicants(coApplicantIDs);
		for (CustomerDetails coAppCustomerDetail : coApplicantCustomers) {
			executeBureau(financeDetail, coAppCustomerDetail);
		}
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for identify the customer and execute Bureau.
	 * 
	 * @param financeDetail
	 * @param customerDetails
	 * @return
	 */
	private Map<String, Object> executeBureau(FinanceDetail financeDetail, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		Map<String, Object> appplicationdata = null;
		if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(), InterfaceConstants.PFF_CUSTCTG_SME)) {
			appplicationdata = executeBureauForSME(financeDetail, customerDetails);
		} else if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),
				InterfaceConstants.PFF_CUSTCTG_INDIV)) {
			appplicationdata = executeBureauForINDV(financeDetail, customerDetails);
		}
		logger.debug(Literal.LEAVING);
		return appplicationdata;
	}

	/**
	 * Method for Execute the Experian Bureau For SME Customer
	 * 
	 * @param financeDetail
	 * @param customerDetails
	 * @return
	 */
	private Map<String, Object> executeBureauForSME(FinanceDetail financeDetail, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		//for Applicant
		//prepare request object
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Map<String, Object> appplicationdata = new HashMap<>();
		BureauCommercial commercial = prepareCommercialRequestObj(customerDetails);
		//send request and log
		String reference = financeMain.getFinReference();
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;
		try {
			reuestString = client.getRequestString(commercial);
			jsonResponse = client.post(commercialUrl, reuestString);
			//check response for error
			errorCode = getErrorCode(jsonResponse);
			errorDesc = getErrorMessage(jsonResponse);

			doInterfaceLogging(reference, commercialUrl, reuestString, jsonResponse, errorCode, errorDesc);

			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, getTrimmedMessage(errorDesc));
			appplicationdata.put(STATUSCODE, getStatusCode(jsonResponse));

			if (StringUtils.isEmpty(errorCode)) {
				//read values from response and load it to extended map
				Map<String, Object> mapdata = getPropValueFromResp(jsonResponse, commercialConfigFileName);
				Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
				//process the response map
				prepareCommercialExtendedMap(mapvalidData,jsonResponse);
				appplicationdata.putAll(mapvalidData);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorDesc = getWriteException(e);
			errorDesc = getTrimmedMessage(errorDesc);
			doExceptioLogging(reference, commercialUrl, reuestString, jsonResponse, errorDesc);

			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, errorDesc);
		}
		appplicationdata.put(REQ_SEND, true);

		logger.debug(Literal.LEAVING);
		return appplicationdata;
	}

	/**
	 * Method for Execute the Experian Bureau for Individual Customer.
	 * 
	 * @param financeDetail
	 * @param customerDetails
	 * @return
	 */
	private Map<String, Object> executeBureauForINDV(FinanceDetail financeDetail, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		//for Applicant
		//prepare request object
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Map<String, Object> appplicationdata = new HashMap<>();
		BureauConsumer consumer = prepareConsumerRequestObj(customerDetails);
		//send request and log
		String reference = financeMain.getFinReference();
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;
		try {
			reuestString = client.getRequestString(consumer);
			jsonResponse = client.post(consumerUrl, reuestString);
			//check response for error
			errorCode = getErrorCode(jsonResponse);
			errorDesc = getErrorMessage(jsonResponse);

			doInterfaceLogging(reference, consumerUrl, reuestString, jsonResponse, errorCode, errorDesc);

			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, getTrimmedMessage(errorDesc));
			appplicationdata.put(STATUSCODE, getStatusCode(jsonResponse));

			if (StringUtils.isEmpty(errorCode)) {
				//read values from response and load it to extended map
				Map<String, Object> mapdata = getPropValueFromResp(jsonResponse, consumerConfigFileName);
				Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
				//process the response map
				prepareConsumerExtendedMap(mapvalidData,jsonResponse);
				appplicationdata.putAll(mapvalidData);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorDesc = getWriteException(e);
			errorDesc = getTrimmedMessage(errorDesc);
			doExceptioLogging(reference, consumerUrl, reuestString, jsonResponse, errorDesc);

			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, errorDesc);
		}
		appplicationdata.put(REQ_SEND, true);

		logger.debug(Literal.LEAVING);
		return appplicationdata;
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
		commercial.setLegalEntity(getCustTypeDesc(customer.getCustTypeCode()));

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
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
		return applicant;
	}

	/**
	 * Method for prepare address request object.
	 * 
	 * @param addressList
	 * @return
	 */
	private Address preparePersonalAddress(List<CustomerAddres> addressList) {
		logger.debug(Literal.ENTERING);
		CustomerAddres address = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_PER);

		City city = getCityDetails(address);

		Address personalAddress = new Address();
		String houseNo;
		if (StringUtils.isNotBlank(address.getCustAddrHNbr())) {
			houseNo = address.getCustAddrHNbr();
		} else {
			houseNo = Objects.toString(address.getCustFlatNbr(), "");
		}
		personalAddress.setHouseNo(houseNo);
		personalAddress.setLandmark(address.getCustAddrStreet());
		if (city != null) {
			personalAddress.setCity(city.getPCCityName());
			personalAddress.setCountry(city.getLovDescPCCountryName());
			personalAddress.setPin(address.getCustAddrZIP());
			personalAddress.setState(city.getLovDescPCProvinceName());
		}
		logger.debug(Literal.LEAVING);
		return personalAddress;
	}

	/**
	 * Method for prepare address request object
	 * 
	 * @param addressList
	 * @return
	 */
	private CompanyAddress prepareCompanyAddress(List<CustomerAddres> addressList) {
		logger.debug(Literal.ENTERING);
		CompanyAddress companyAddress = new CompanyAddress();
		CustomerAddres address = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_OFF);
		City city = getCityDetails(address);

		StringBuilder stringBuilder = new StringBuilder();
		if (StringUtils.isNotBlank(address.getCustAddrType())) {
			stringBuilder.append(address.getCustAddrType());
		}
		if (StringUtils.isNotBlank(address.getCustAddrHNbr())) {
			if (StringUtils.isNotBlank(stringBuilder)) {
				stringBuilder.append(",");
			}
			stringBuilder.append(address.getCustAddrHNbr());
		}
		if (StringUtils.isNotBlank(address.getCustAddrStreet())) {
			if (StringUtils.isNotBlank(stringBuilder)) {
				stringBuilder.append(",");
			}
			stringBuilder.append(address.getCustAddrStreet());
		}
		companyAddress.setAddressLine1(stringBuilder.toString());
		companyAddress.setAddressLine2(stringBuilder.toString());
		companyAddress.setAddressLine3(stringBuilder.toString());

		if (city != null) {
			companyAddress.setCity(city.getPCCityName());
			companyAddress.setCountry(city.getLovDescPCCountryName());
			companyAddress.setPin(address.getCustAddrZIP());
			companyAddress.setState(city.getLovDescPCProvinceName());
		}
		companyAddress.setDistrict(StringUtils.isNotBlank(address.getCustDistrict()) ? address.getCustDistrict()
				: InterfaceConstants.DEFAULT_DIST);
		logger.debug(Literal.LEAVING);
		return companyAddress;
	}

	/**
	 * Method for prepare Consumer request object.
	 * 
	 * @param customerDetails
	 * @return
	 */
	private BureauConsumer prepareConsumerRequestObj(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
		return personalDetails;
	}

	/**
	 * Method for prepare address request object.
	 * 
	 * @param addressList
	 * @return
	 */
	private ConsumerAddress prepareConsumerAddress(List<CustomerAddres> addressList) {
		logger.debug(Literal.ENTERING);
		ConsumerAddress consumerAddress = new ConsumerAddress();
		CustomerAddres address = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_PER);

		City city = getCityDetails(address);

		String houseNo;
		if (address.getCustAddrHNbr() != null) {
			houseNo = address.getCustAddrHNbr();
		} else {
			houseNo = Objects.toString(address.getCustFlatNbr(), "");
		}

		consumerAddress.setHouseNo(houseNo);
		consumerAddress.setLandmark(address.getCustAddrStreet());
		consumerAddress.setCareOf(StringUtils.isNotBlank(address.getCustAddrLine3()) ? address.getCustAddrLine3()
				: InterfaceConstants.DEFAULT_CAREOF);

		if (city != null) {
			consumerAddress.setCity(city.getPCCityName());
			consumerAddress.setCountry(city.getLovDescPCCountryName());
			consumerAddress.setPin(address.getCustAddrZIP());
			consumerAddress.setState(city.getLovDescPCProvinceName());
		}
		consumerAddress.setDistrict(StringUtils.isNotBlank(address.getCustDistrict()) ? address.getCustDistrict()
				: InterfaceConstants.DEFAULT_DIST);
		consumerAddress.setSubDistrict(StringUtils.isNotBlank(address.getCustAddrLine4()) ? address.getCustAddrLine4()
				: InterfaceConstants.DEFAULT_SUBDIST);
		logger.debug(Literal.LEAVING);
		return consumerAddress;
	}

	/**
	 * Method for prepare the Extended Fields Map based on the response of Experian Bureau Commercial.
	 * 
	 * @param extendedFieldMap
	 * @return
	 * @throws ParseException
	 */
	private Map<String, Object> prepareCommercialExtendedMap(Map<String, Object> extendedFieldMap, String jsonResponse)
			throws ParseException {
		logger.debug(Literal.ENTERING);

		List<BillPayGrid> billPayGridList = null;
		String jsonEmiBounceResponse = Objects.toString(getValueFromResponse(jsonResponse, COMMERCIAL_BPAYGRID), "");
		extendedFieldMap.put(NO_EMI_BOUNCES_IN3M, true);
		extendedFieldMap.put(NO_EMI_BOUNCES_IN6M, true);
		if (!StringUtils.isEmpty(jsonEmiBounceResponse)) {
			jsonEmiBounceResponse = extendedFieldMap.get(NO_EMI_BOUNCES_IN3M).toString();
			Object responseObj = getResponseObject(jsonEmiBounceResponse, BpayGridResponse.class, true);
			@SuppressWarnings("unchecked")
			List<BpayGridResponse> bpayGridResponses = (List<BpayGridResponse>) responseObj;
			if (bpayGridResponses != null && !bpayGridResponses.isEmpty()) {
				billPayGridList = prepareBillpayGridList(bpayGridResponses);
			}
			if (billPayGridList != null && !billPayGridList.isEmpty()) {
				boolean isEmiBounceInL3M = isCommercialEMIBouncesInLastMonths(billPayGridList, 3);
				extendedFieldMap.put(NO_EMI_BOUNCES_IN3M, isEmiBounceInL3M);
				boolean isEmiBounceInL6M = isCommercialEMIBouncesInLastMonths(billPayGridList, 6);
				extendedFieldMap.put(NO_EMI_BOUNCES_IN6M, isEmiBounceInL6M);
			}

		}

		for (Entry<String, Object> entry : extendedFieldMap.entrySet()) {
			if (entry.getKey().equals(RESTRUCTURED_FLAG)) {
				extendedFieldMap.put(entry.getKey(), null);
			} else if (entry.getKey().equals(SUIT_FILED_FLAG)) {

				if (entry.getValue() != null) {
					boolean value = StringUtils.equals(entry.getValue().toString(), "1") ? true : false;
					extendedFieldMap.put(entry.getKey(), value);
				}

			} else if (entry.getKey().equals(WILLFUL_DEFAULTER_FLAG)) {

				if (entry.getValue() != null) {
					boolean value = StringUtils.equals(entry.getValue().toString(), "1") ? true : false;
					extendedFieldMap.put(entry.getKey(), value);
				}

			} else {
				extendedFieldMap.put(entry.getKey(), entry.getValue());

			}
		}
		logger.debug(Literal.LEAVING);
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
	private Map<String, Object> prepareConsumerExtendedMap(Map<String, Object> extendedFieldMap, String jsonResponse)
			throws ParseException {
		logger.debug(Literal.ENTERING);
		List<CAISAccountHistory> caisAccountHistories = null;
		String jsonEmiBounceResponse = Objects.toString(getValueFromResponse(jsonResponse, CONSUMER_CAIS_HIST), "");
		extendedFieldMap.put(NO_EMI_BOUNCES_IN3M, true);
		extendedFieldMap.put(NO_EMI_BOUNCES_IN6M, true);
		if (!StringUtils.isEmpty(jsonEmiBounceResponse)) {
			Object responseObj = getResponseObject(jsonEmiBounceResponse, CAISAccountHistory.class, true);
			caisAccountHistories = (List<CAISAccountHistory>) responseObj;
			if (caisAccountHistories != null && !caisAccountHistories.isEmpty()) {
				boolean isEmiBounceInL3M = isConsumerEMIBouncesInLastMonths(caisAccountHistories, 3);
				extendedFieldMap.put(NO_EMI_BOUNCES_IN3M, isEmiBounceInL3M);
				boolean isEmiBounceInL6M = isConsumerEMIBouncesInLastMonths(caisAccountHistories, 6);
				extendedFieldMap.put(NO_EMI_BOUNCES_IN3M, isEmiBounceInL6M);
			}

		}
		for (Entry<String, Object> entry : extendedFieldMap.entrySet()) {
			if (entry.getKey().equals(RESTRUCTURED_FLAG)) {
				extendedFieldMap.put(entry.getKey(), null);
			} else if (entry.getKey().equals(SUIT_FILED_FLAG)) {

				if (entry.getValue() != null) {
					boolean value = StringUtils.equals(entry.getValue().toString(), "01") ? true : false;
					extendedFieldMap.put(entry.getKey(), value);
				}

			} else if (entry.getKey().equals(WILLFUL_DEFAULTER_FLAG)) {

				if (entry.getValue() != null) {
					boolean value = StringUtils.equals(entry.getValue().toString(), "02") ? true : false;
					extendedFieldMap.put(entry.getKey(), value);
				}

			} else if (entry.getKey().equals(STATUS)) {
				String acc_Status = String.valueOf(extendedFieldMap.get(STATUS));
				if (StringUtils.equals(acc_Status, WRITEOFF)) {
					extendedFieldMap.put(WRITE_OFF_FLAG, true);
				} else if (StringUtils.equals(acc_Status, SETTLE)) {
					extendedFieldMap.put(SETTLED_FLAG_FLAG, true);
				}
			} else {
				extendedFieldMap.put(entry.getKey(), entry.getValue());
			}
		}
		logger.debug(Literal.LEAVING);
		return extendedFieldMap;

	}

	/**
	 * Method for prepare BillPayGrid object based on the Experian Commercial response for calculating the EMI-Bounces.
	 * 
	 * @param bpayGridResponseList
	 * @return
	 * @throws ParseExceptionString
	 */
	private List<BillPayGrid> prepareBillpayGridList(List<BpayGridResponse> bpayGridResponseList)
			throws ParseException {
		logger.debug(Literal.ENTERING);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		List<BillPayGrid> billPayGridList = new ArrayList<BillPayGrid>(1);
		for (BpayGridResponse bpayGridRes : bpayGridResponseList) {
			BillPayGrid bpayGrid = new BillPayGrid();
			bpayGrid.setBillPayDate(dateFormat.parse("01-" + bpayGridRes.getMonth() + "-" + bpayGridRes.getYear()));
			bpayGrid.setAssetClassification(bpayGridRes.getAssetClassification());
			billPayGridList.add(bpayGrid);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		Collections.sort(billPayGridList, new BpayGridResponseComparator());

		for (int i = 0; i < billPayGridList.size(); i++) {
			BillPayGrid bpayGrid = billPayGridList.get(i);
			if (NiyoginUtility.getMonthsBetween(appDate, bpayGrid.getBillPayDate()) <= numbOfMnths) {
				String assestClasification = billPayGridList.get(i).getAssetClassification();
				if (assestClasification.equals("Blank") || assestClasification.equals("?")
						|| assestClasification.equals("S")) {
					continue;
				} else {
					logger.debug(Literal.LEAVING);
					return true;
				}
			} else {
				break;
			}

		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
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
					logger.debug(Literal.LEAVING);
					return true;
				}
			} else {
				break;
			}

		}
		logger.debug(Literal.LEAVING);
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
	 * Method for prepare Success logging
	 * 
	 * @param reference
	 * @param requets
	 * @param response
	 * @param errorCode
	 * @param errorDesc
	 */
	private void doInterfaceLogging(String reference, String serviceUrl, String requets, String response,
			String errorCode, String errorDesc) {
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
	private void doExceptioLogging(String reference, String serviceUrl, String requets, String response,
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
		iLogDetail.setStatus(InterfaceConstants.STATUS_FAILED);
		iLogDetail.setErrorCode(InterfaceConstants.ERROR_CODE);
		iLogDetail.setErrorDesc(errorDesc);

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}

	public void setConsumerUrl(String consumerUrl) {
		this.consumerUrl = consumerUrl;
	}

	public void setCommercialUrl(String commercialUrl) {
		this.commercialUrl = commercialUrl;
	}

}
