package com.pennanttech.niyogin.experian.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

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
import com.pennanttech.niyogin.clients.JSONClient;
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
import com.pennanttech.pff.external.dao.NiyoginDAOImpl;
import com.pennanttech.pff.external.service.NiyoginService;

public class ExperianBureauServiceImpl extends NiyoginService implements ExperianBureauService {
	private static final Logger	logger							= Logger.getLogger(ExperianBureauServiceImpl.class);
	private String				extConfigFileName;
	private String				consumerUrl;
	private String				commercialUrl;
	private JSONClient			client;
	private NiyoginDAOImpl		niyoginDAOImpl;

	private final String		SUIT_FILED						= "SUITFILED";
	private final String		WILLFUL_DEFAULTER				= "WILLFULDEFAULTER";
	private final String		NO_EMI_BOUNCES_IN_3_MONTHS		= "EMI3MONTHS";
	private final String		NO_EMI_BOUNCES_IN_6_MONTHS		= "EMI6MNTHS";
	private final String		RESTRUCTURED_LOAN_AND_AMOUNT	= "RESTRUCTUREDLOAN";

	private final String		STATUS							= "STATUS";
	private final String		WRITEOFF						= "25";
	private final String		SETTLE							= "23";

	private Date				appDate							= getAppDate();

	private String				jsonResponse					= null;
	private String				errorDesc						= null;
	private Timestamp			reqSentOn						= null;
	private String				status							= InterfaceConstants.STATUS_SUCCESS;
	private String				errorCode						= InterfaceConstants.INTFACE_ERROR_CD;
	private Object				requestObject					= null;
	private String				serviceUrl						= null;


	@Override
	public AuditHeader executeExperianBureau(AuditHeader auditHeader) throws InterfaceException, ParseException {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();

		reqSentOn = new Timestamp(System.currentTimeMillis());

		// Execute Bureau for Actual customer
		Map<String, Object> extendedFieldMap = executeBureau(financeDetail, customerDetails);

		// Execute Bureau for co-applicants
		List<JointAccountDetail> coapplicants = financeDetail.getJountAccountDetailList();
		if (coapplicants != null && !coapplicants.isEmpty()) {
			List<Long> coApplicantIDs = new ArrayList<Long>(1);
			for (JointAccountDetail coApplicant : coapplicants) {
				coApplicantIDs.add(coApplicant.getCustID());
			}
			//TODO: Need solution for display co-applicant extended details
			Map<String, Object> extendedFieldMapForCoApp = new HashMap<>();
			List<CustomerDetails> coApplicantCustomers = niyoginDAOImpl.getCoApplicants(coApplicantIDs, "_VIEW");
			for (CustomerDetails coAppCustomerDetails : coApplicantCustomers) {
				extendedFieldMapForCoApp.putAll(executeBureau(financeDetail, coAppCustomerDetails));
			}
		}

		//validate the map with configuration
		Map<String, Object> validatedExtendedMap = validateExtendedMapValues(extendedFieldMap);

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
		try {
			if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),
					InterfaceConstants.PFF_CUSTCTG_SME)) {
				BureauCommercial commercial = prepareCommercialRequestObj(customerDetails);
				serviceUrl = commercialUrl;
				jsonResponse = client.post(serviceUrl, commercial);
				//jsonResponse = getResponse();
				extConfigFileName = "experianBureauCommercial";
				requestObject = commercial;
			} else if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),
					InterfaceConstants.PFF_CUSTCTG_INDIV)) {
				BureauConsumer consumer = prepareConsumerRequestObj(customerDetails);
				serviceUrl = consumerUrl;
				jsonResponse = client.post(serviceUrl, consumer);
				//jsonResponse = getResponse();
				extConfigFileName = "experianBureauConsumer";
				requestObject = consumer;
			}
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
			status = InterfaceConstants.STATUS_FAILED;
			StringWriter writer = new StringWriter();
			exp.printStackTrace(new PrintWriter(writer));
			errorDesc = writer.toString();
			doInterfaceLogging(requestObject, finReference);
			throw new InterfaceException(errorCode, exp.getMessage());
		}

		//for Straight forwardFields It works
		Map<String, Object> extendedFieldMap = getExtendedMapValues(jsonResponse, extConfigFileName);

		// error validation on Response status
		if (extendedFieldMap.get("ERRORCODE") != null) {
			errorCode = Objects.toString(extendedFieldMap.get("ERRORCODE"));
			errorDesc = Objects.toString(extendedFieldMap.get("ERRORDESC"));
			throw new InterfaceException(errorCode, errorDesc);
		} else {
			extendedFieldMap.remove("ERRORCODE");
			extendedFieldMap.remove("ERRORDESC");
		}

		if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(), InterfaceConstants.PFF_CUSTCTG_SME)) {
			extendedFieldMap = prepareCommercialExtendedMap(extendedFieldMap);
		} else if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),
				InterfaceConstants.PFF_CUSTCTG_INDIV)) {
			extendedFieldMap = prepareConsumerExtendedMap(extendedFieldMap);
		}
		logger.debug(Literal.LEAVING);
		return extendedFieldMap;

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
		applicant.setDob(formatDate(customer.getCustDOB(), "dd-MM-yyyy"));
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
		City city = niyoginDAOImpl.getCityById(address.getCustAddrCountry(), address.getCustAddrProvince(),
				address.getCustAddrCity(), "_AView");

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
		City city = niyoginDAOImpl.getCityById(address.getCustAddrCountry(), address.getCustAddrProvince(),
				address.getCustAddrCity(), "_AView");
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
		personalDetails.setDob(formatDate(customer.getCustDOB(), "dd-MM-yyyy"));
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
		City city = niyoginDAOImpl.getCityById(address.getCustAddrCountry(), address.getCustAddrProvince(),
				address.getCustAddrCity(), "_AView");

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
			Object responseObj = client.getResponseObject(jsonEmoBounceResponse, BpayGridResponse.class, true);
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
			Object responseObj = client.getResponseObject(jsonEmiBounceResponse, CAISAccountHistory.class, true);
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
			if (getMonthsBetween(appDate, bpayGrid.getBillPayDate()) <= numbOfMnths) {
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
			if (getMonthsBetween(appDate, bpayDate) <= numbOfMnths) {
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
	 * Method for return the number Of months between two dates
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getMonthsBetween(java.util.Date date1, java.util.Date date2) {

		if (date1 == null || date2 == null) {
			return -1;
		}
		if (date1.before(date2)) {
			java.util.Date temp = date2;
			date2 = date1;
			date1 = temp;
		}
		int years = convert(date1).get(Calendar.YEAR) - convert(date2).get(Calendar.YEAR);
		int months = convert(date1).get(Calendar.MONTH) - convert(date2).get(Calendar.MONTH);
		months += years * 12;
		if (convert(date1).get(Calendar.DATE) < convert(date2).get(Calendar.DATE)) {
			months--;
		}

		return months;
	}

	public static GregorianCalendar convert(java.util.Date date) {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return gc;
	}

	/**
	 * Method for prepare data and logging
	 * 
	 * @param consumerRequest
	 * @param reference
	 */
	private void doInterfaceLogging(Object requestObj, String reference) {
		InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, requestObj, jsonResponse, reqSentOn, status,
				errorCode, errorDesc, reference);
		logInterfaceDetails(interfaceLogDetail);
	}

	private String getResponse() {
		//String consumerResponse = "{ \"statusCode\":200, \"message\":\"Experian Bureau Consumer report extracted\", \"data\":{ \"TotalCAPS_Summary\":{ \"TotalCAPSLast7Days\":\"17\", \"TotalCAPSLast30Days\":\"17\", \"TotalCAPSLast90Days\":\"17\", \"TotalCAPSLast180Days\":\"17\" }, \"CAIS_Account\":{ \"CAIS_Summary\":{ \"Credit_Account\":{ \"CreditAccountTotal\":\"1\", \"CreditAccountActive\":\"1\", \"CreditAccountDefault\":\"0\", \"CreditAccountClosed\":\"0\", \"CADSuitFiledCurrentBalance\":\"0\" }, \"Total_Outstanding_Balance\":{ \"Outstanding_Balance_Secured\":\"61000\", \"Outstanding_Balance_Secured_Percentage\":\"100\", \"Outstanding_Balance_UnSecured\":\"0\", \"Outstanding_Balance_UnSecured_Percentage\":\"0\", \"Outstanding_Balance_All\":\"61000\" } }, \"CAIS_Account_DETAILS\":{ \"Identification_Number\":\"TELXXXXXXXX\", \"Subscriber_Name\":\"XXXXXXXXXX\", \"Account_Number\":\"XXXXXXXXXX\", \"Portfolio_Type\":\"M\", \"Account_Type\":\"02\", \"Open_Date\":\"20100301\", \"Highest_Credit_or_Original_Loan_Amount\":\"62500\", \"Terms_Duration\":[  ], \"Terms_Frequency\":[  ], \"Scheduled_Monthly_Payment_Amount\":[  ], \"Account_Status\":\"32\", \"Payment_Rating\":\"6\", \"Payment_History_Profile\":\"61666???????????????????????????????\", \"Special_Comment\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\", \"Original_Charge_off_Amount\":[  ], \"Date_Reported\":\"20161010\", \"Date_Of_First_Delinquency\":[  ], \"Date_Closed\":[  ], \"Date_of_Last_Payment\":[  ], \"SuitFiledWillfulDefaultWrittenOffStatus\":[  ], \"SuitFiled_WilfulDefault\":[  ], \"Written_off_Settled_Status\":\"03\", \"Value_of_Credits_Last_Month\":[  ], \"Occupation_Code\":[  ], \"Settlement_Amount\":[  ], \"Value_of_Collateral\":[  ], \"Type_of_Collateral\":[  ], \"Written_Off_Amt_Total\":[  ], \"Written_Off_Amt_Principal\":[  ], \"Rate_of_Interest\":[  ], \"Repayment_Tenure\":\"0\", \"Promotional_Rate_Flag\":[  ], \"Income\":[  ], \"Income_Indicator\":[  ], \"Income_Frequency_Indicator\":[  ], \"DefaultStatusDate\":[  ], \"LitigationStatusDate\":[  ], \"WriteOffStatusDate\":[  ], \"DateOfAddition\":\"20160510\", \"CurrencyCode\":\"INR\", \"Subscriber_comments\":[  ], \"Consumer_comments\":[  ], \"AccountHoldertypeCode\":\"2\", \"CAIS_Account_History\":[ { \"Year\":\"2016\", \"Month\":\"10\", \"Days_Past_Due\":\"190\", \"Asset_Classification\":\"?\" }, { \"Year\":\"2016\", \"Month\":\"09\", \"Days_Past_Due\":\"190\", \"Asset_Classification\":\"?\" }, { \"Year\":\"2016\", \"Month\":\"08\", \"Days_Past_Due\":\"40\", \"Asset_Classification\":\"?\" }, { \"Year\":\"2016\", \"Month\":\"07\", \"Days_Past_Due\":\"200\", \"Asset_Classification\":\"?\" }, { \"Year\":\"2016\", \"Month\":\"06\", \"Days_Past_Due\":\"200\", \"Asset_Classification\":\"?\" }, { \"Year\":\"2016\", \"Month\":\"05\", \"Days_Past_Due\":\"200\", \"Asset_Classification\":\"?\" } ], \"Advanced_Account_History\":[ { \"Year\":\"2016\", \"Month\":\"10\", \"Cash_Limit\":[  ], \"Credit_Limit_Amount\":\"62500\", \"Actual_Payment_Amount\":[  ], \"EMI_Amount\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\" }, { \"Year\":\"2016\", \"Month\":\"09\", \"Cash_Limit\":[  ], \"Credit_Limit_Amount\":\"62500\", \"Actual_Payment_Amount\":[  ], \"EMI_Amount\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\" }, { \"Year\":\"2016\", \"Month\":\"08\", \"Cash_Limit\":[  ], \"Credit_Limit_Amount\":\"62500\", \"Actual_Payment_Amount\":[  ], \"EMI_Amount\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\" }, { \"Year\":\"2016\", \"Month\":\"07\", \"Cash_Limit\":[  ], \"Credit_Limit_Amount\":\"62500\", \"Actual_Payment_Amount\":[  ], \"EMI_Amount\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\" }, { \"Year\":\"2016\", \"Month\":\"06\", \"Cash_Limit\":[  ], \"Credit_Limit_Amount\":\"62500\", \"Actual_Payment_Amount\":[  ], \"EMI_Amount\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\" }, { \"Year\":\"2016\", \"Month\":\"05\", \"Cash_Limit\":[  ], \"Credit_Limit_Amount\":\"62500\", \"Actual_Payment_Amount\":[  ], \"EMI_Amount\":[  ], \"Current_Balance\":\"61000\", \"Amount_Past_Due\":\"750\" } ], \"CAIS_Holder_Details\":{ \"Surname_Non_Normalized\":\"VIKAS\", \"First_Name_Non_Normalized\":\"JEO\", \"Middle_Name_1_Non_Normalized\":[  ], \"Middle_Name_2_Non_Normalized\":[  ], \"Middle_Name_3_Non_Normalized\":[  ], \"Alias\":[  ], \"Gender_Code\":\"2\", \"Income_TAX_PAN\":\"BUQPJ2311S\", \"Passport_Number\":\"J4567899\", \"Voter_ID_Number\":\"NNX4006259\", \"Date_of_birth\":\"19870726\" }, \"CAIS_Holder_Address_Details\":{ \"First_Line_Of_Address_non_normalized\":\"DRFGRHTFTR\", \"Second_Line_Of_Address_non_normalized\":\"CHEMBUR\", \"Third_Line_Of_Address_non_normalized\":[  ], \"City_non_normalized\":\"MUMBAI\", \"Fifth_Line_Of_Address_non_normalized\":[  ], \"State_non_normalized\":\"27\", \"ZIP_Postal_Code_non_normalized\":\"401107\", \"CountryCode_non_normalized\":\"IB\", \"Address_indicator_non_normalized\":[  ], \"Residence_code_non_normalized\":[  ] }, \"CAIS_Holder_Phone_Details\":{ \"Telephone_Number\":\"9003170611\", \"Telephone_Type\":[  ] }, \"CAIS_Holder_ID_Details\":[ { \"Income_TAX_PAN\":\"BUQPJ2311S\", \"PAN_Issue_Date\":[  ], \"PAN_Expiration_Date\":[  ], \"Passport_Number\":\"J4567899\", \"Passport_Issue_Date\":[  ], \"Passport_Expiration_Date\":[  ], \"Voter_ID_Number\":\"NNX4006259\", \"Voter_ID_Issue_Date\":[  ], \"Voter_ID_Expiration_Date\":[  ], \"Driver_License_Number\":[  ], \"Driver_License_Issue_Date\":[  ], \"Driver_License_Expiration_Date\":[  ], \"EMailId\":[  ] }, { \"Income_TAX_PAN\":\"BUQPJ2311S\", \"PAN_Issue_Date\":[  ], \"PAN_Expiration_Date\":[  ], \"Passport_Number\":\"J4567899\", \"Passport_Issue_Date\":[  ], \"Passport_Expiration_Date\":[  ], \"Voter_ID_Number\":\"NNX4006259\", \"Voter_ID_Issue_Date\":[  ], \"Voter_ID_Expiration_Date\":[  ], \"Driver_License_Number\":[  ], \"Driver_License_Issue_Date\":[  ], \"Driver_License_Expiration_Date\":[  ], \"EMailId\":[  ] } ] } } } }";
		String commercialResponse = "{ \"statusCode\":200, \"message\":\"Elink Data\", \"data\":{ \"NGSYSMSG\":{ \"SegmentCode\":\"NGSYSMSG\", \"systemMessageCode\":\"0\", \"systemMessageText\":\"Record Found\", \"systemOutputFormatVersion\":\"1.6\", \"systemMessageDescription\":\"\" }, \"NGINQUIRY\":{ \"SegmentCode\":\"NGINQUIRY\", \"InqUserId\":\"expcu03\", \"InqBureauMemberId\":\"3388\", \"InqBureauMemberIndustry\":\"\", \"InqBureauMemberName\":\"Test Bank\", \"InqRefNum\":\"BR001\", \"InqPurposeCd\":\"3\", \"InqAcctTypeCd\":\"020\", \"InqAmt\":\"50000\", \"InqAmtMonetaryCd\":\"\", \"InqProductName\":\"Experian Credit Information Report (Business)\", \"InqProductCd\":\"INBCIR001\", \"InqProductSearchTypeCd\":\"1\", \"Frequency\":\"6\", \"InqCompanyName\":\"KRANTI And KIARA ENTERPRISES\", \"EnquiryApplicationType\":\"2\", \"DurationofAgreement\":\"1\", \"InqLegalEntity\":\"11\", \"InqCompanyBankAccNumber\":\"\", \"InqPAN\":\"AAAAR5455N\", \"NGINQADDR\":{ \"SegmentCode\":\"NGINQADDR\", \"AddrType\":\"14\", \"InqCompanyAddress\":\"8TH FLOOR NIKE BUILDING S.K.MARG WORLI\", \"InqCompanyCity\":\"MUMBAI\", \"InqCompanyPINCode\":\"560001\", \"CountryCode\":\"IND\" }, \"NGINQTEL\":{ \"SegmentCode\":\"NGINQTEL\", \"PhoneNumber\":\"9820111555\", \"PhoneType\":\"6\" }, \"PERINPUT\":{ \"SegmentCode\":\"PERINPUT\", \"FirstGivenName\":\"Nitin\", \"MiddleName\":\"\", \"OtherMiddleNames\":\"\", \"IndiaMiddleName3\":\"\", \"FamilyName\":\"Jain\", \"BirthYear\":\"1981\", \"BirthMonth\":\"03\", \"BirthDay\":\"05\", \"Gender\":\"1\", \"MaritalStatus\":\"\", \"Relationship\":\"\", \"PERINPADDR\":{ \"SegmentCode\":\"PERINPADDR\", \"AddrType\":\"\", \"LocalityName\":\"1/29 EAST ST  URANTHARAYANKUDIKADU\", \"RegionCode\":\"33\", \"CountryCode\":\"IND\", \"IndiaAddressL1\":\"02\", \"IndiaAddressL2\":\"\", \"IndiaAddressL3\":\"THANJAVUR\", \"Landmark\":\"\", \"PostalCode\":\"614625\" }, \"PERINPPHN\":{ \"SegmentCode\":\"PERINPPHN\", \"PhoneType\":\"6\", \"PhoneNumber\":\"9551542844\" }, \"PERINPIDC\":{ \"SegmentCode\":\"PERINPIDC\", \"IdNumberType\":\"10\", \"IndiaIdNumber\":\"AAAAR5455N\" } } }, \"COMMBRPT\":{ \"SegmentCode\":\"COMMBRPT\", \"BurProductSearchResultCd\":\"3\", \"BurProductHitNoHitInd\":\"Y\", \"BurProductValueAddInd\":\"\", \"BurRptNum\":\"1513145606442\", \"BurRptTitle\":\"Experian Credit Information Report (Business)\", \"BurRptDate\":\"13122017\", \"BurRptTime\":\"11:43:26 AM\", \"BurRptLanguageCd\":\"ENG\" }, \"BUSINESS\":{ \"SegmentCode\":\"BUSINESS\", \"BureauAccuMatchId\":\"MzIyNDU0OA==\", \"BureauAccuMatchIdTypeCd\":\"BIN\", \"BureauAddDate\":\"31082015\", \"BusinessName\":\"KRANTI KIARA\", \"BusinessShortName\":\"K & K ENT\", \"LegalDescription\":\"12 \", \"IndustryType\":\"27 \", \"IndustryTypeDetail\":\"   \", \"ALIASNAM\":{ \"SegmentCode\":\"ALIASNAM\", \"AliasName\":\"K & K ENT\", \"AliasType\":\"1  \" }, \"PSUMMARY\":{ \"SegmentCode\":\"PSUMMARY\", \"GRANTOR\":{ \"SegmentCode\":\"GRANTOR\", \"TotalCreditProviders\":\"1\", \"CurrentCreditProviders\":\"1\", \"TotalSameCreditProviders\":\"0\", \"TotalOtherCreditProviders\":\"1\" }, \"ACCSUM\":{ \"SegmentCode\":\"ACCSUM\", \"TotalCreditAccount\":\"1\", \"TotalClosedAccounts\":\"0\", \"MonthFirstToCredit\":\"2\", \"YearFirstToCredit\":\"2001\" }, \"GUARANTO\":{ \"SegmentCode\":\"GUARANTO\", \"TotalGuarantors\":\"0\" }, \"CURRCYSTAT\":{ \"SegmentCode\":\"CURRCYSTAT\", \"FundCurrentBalance\":\"\", \"NonFundCurrentBalance\":\"\", \"ShortTermsCurrentBalance\":\"\", \"LongTermsCurrentBalance\":\"\", \"WilfullDefaultCurrentBalance\":\"\", \"SuitFiledCurrentBalance\":\"\" }, \"CURRCYCNCT\":{ \"SegmentCode\":\"CURRCYCNT\", \"CurrencyCd\":\"INR\", \"TotalNoFundedAccountType\":\"1\", \"TotalNoNonFundedAccountType\":\"0\", \"TotalNoShortTermAccountType\":\"1\", \"TotalNoLongTermAccountType\":\"0\", \"TotalNoWDAccountType\":\"0\", \"TotalNoSFAccountType\":\"0\" }, \"CREDTYPE\":{ \"SegmentCode\":\"CREDTYPE\", \"CurrencyCd\":\"INR\", \"TotalCurrentBalance\":\"88992000\", \"TotalCreditTypeNo\":\"1\", \"PctTotalStandardCreditType\":\"100\", \"PctTotalSubStandardCreditType\":\"0\", \"PctTotalDoubtfulCreditType\":\"0\", \"PctTotalLossCreditType\":\"0\", \"PctTotalSpecMentionCreditType\":\"0\", \"ACCTYPINFO\":{ \"SegmentCode\":\"ACCTYPINFO\", \"AccountType\":\"190\", \"CurrencyCd\":\"INR\", \"TotalCurrentBalance\":\"88992000\", \"TotalCreditTypeNo\":\"1\", \"PctTotalStandardCreditType\":\"100\", \"PctTotalSubStandardCreditType\":\"0\", \"PctTotalDoubtfulCreditType\":\"0\", \"PctTotalLossCreditType\":\"0\", \"PctTotalSpecMentionCreditType\":\"0\" } }, \"ENQATTR\":{ \"SegmentCode\":\"ENQATTR\", \"MostRecentEnqDate\":\"13122017\", \"TotEnq\":\"733\", \"TotEnq7days\":\"38\", \"TotEnq30days\":\"147\", \"TotEnq90days\":\"480\", \"TotEnq180days\":\"733\" }- }, \"COMMCRED\":{ \"SegmentCode\":\"COMMCRED\", \"AccountNumber\":\"XXXXXXXXX8554\", \"AccountPortfolioType\":\"I\", \"AccountType\":\"190\", \"AccountCurrency\":\"INR\", \"AccountFinRespTypeCd\":\"\", \"AccountStatus\":\"O\", \"PaymentStatus\":\"45\", \"AccountOpenDate\":\"24022001\", \"AccountClosedDate\":\"\", \"SanctionedAmount\":\"-1\", \"AssetClassification\":\"S\", \"CurrentBalance\":\"11126222\", \"WilfulDefaultStatus\":\"0\", \"WilfulDefaultDate\":\"\", \"SuitFiledStatus\":\"0\", \"SuitFiledDate\":\"\", \"SuitFiledAmount\":\"-1\", \"LastReportedDate\":\"31082015\", \"SanctionDate\":\"24022001\", \"CREDITOR\":{ \"SegmentCode\":\"CREDITOR\", \"CreditorIndustryCd\":\"\", \"CreditorName\":\"State Bank of Travancore\" }, \"BORROWER\":{ \"SegmentCode\":\"BORROWER\", \"BorrowerName\":\"KRANTI & -KIARA ENTERPRISES\", \"BorrowerLastReportedDate\":\"31082015\", \"BorrowerPAN\":\"AAAAR5455N\", \"BorrowerAddress\":\"8TH FLOOR NIKE BUILDING,S.K.MARG,WORLIMUMBAI, MUMBAI, MAHARASHTRA, 400021, IND\", \"BorrowerCity\":\"MUMBAI\", \"BorrowerPINCode\":\"400021\", \"BorrowerLocationType\":\"14 \", \"StartDate\":\"\", \"AccountStatusDate\":\"26072003\" }, \"GUARANTOR\":[ { \"SegmentCode\":\"GUARANTOR\", \"GuarantorName\":\" \", \"GuarantorType\":\"2  \", \"GuarantorPAN\":\"\", \"GuarantorLocationType\":\"1  \", \"GuarantorAddress\":\"105, MAHI APARTMENTS, NAVRANGPURA,, D.N.NAGAR, AHMEDABAD, 380006, IND\", \"GuarantorCity\":\"AHMEDABAD\", \"GuarantorPINCode\":\"380006\", \"GuarantorTelephone\":\"919825018835\", \"GuarantorLastReportedDate\":\"31082015\", \"GuarantorStartDate\":\"\" }, { \"SegmentCode\":\"GUARANTOR\", \"GuarantorName\":\"SHARJA POLYMERS\", \"GuarantorType\":\"1  \", \"GuarantorPAN\":\"ABBPR2679G\", \"GuarantorLocationType\":\"1  \", \"GuarantorAddress\":\"103,CHITRARATH OPP HOTEL PRESIDENT,NAVRANGPURA,, AHMEDABAD, 380006, IND\", \"GuarantorCity\":\"AHMEDABAD\", \"GuarantorPINCode\":\"380006\", \"GuarantorTelephone\":\"919828016835\", \"GuarantorLastReportedDate\":\"31082015\", \"GuarantorStartDate\":\"\" } ], \"STMTALRT\":{ \"SegmentCode\":\"STMTALRT\", \"StatementDate\":\"\", \"StatementExpirationDate\":\"\", \"StatementTypeCode\":\"   \", \"ContactPhone\":\"\", \"StatementText\":\" \" }, \"BPAYGRID\":[ { \"SegmentCode\":\"BPAYGRID\", \"TimePeriodInd\":\"M\", \"Year\":\"2015\", \"WeekNumber\":\"5\", \"Monthvalue\":\"Aug\", \"PaymentStatusValue\":\"45\", \"DaysPastDue\":\"\", \"AssetClassification\":\"S  \", \"HDETAILS\":{ \"SegmentCode\":\"HDETAILS\", \"BalanceAmt\":\"11126222\" } }, { \"SegmentCode\":\"BPAYGRID\", \"TimePeriodInd\":\"M\", \"Year\":\"2015\", \"WeekNumber\":\"5\", \"Monthvalue\":\"Jul\", \"PaymentStatusValue\":\"\", \"DaysPastDue\":\"\", \"AssetClassification\":\"\", \"HDETAILS\":{ \"SegmentCode\":\"HDETAILS\", \"BalanceAmt\":\"\" } }, { \"SegmentCode\":\"BPAYGRID\", \"TimePeriodInd\":\"M\", \"Year\":\"2015\", \"WeekNumber\":\"5\", \"Monthvalue\":\"Jun\", \"PaymentStatusValue\":\"\", \"DaysPastDue\":\"\", \"AssetClassification\":\"\", \"HDETAILS\":{ \"SegmentCode\":\"HDETAILS\", \"BalanceAmt\":\"\" } }, { \"SegmentCode\":\"BPAYGRID\", \"TimePeriodInd\":\"M\", \"Year\":\"2015\", \"WeekNumber\":\"4\", \"Monthvalue\":\"May\", \"PaymentStatusValue\":\"\", \"DaysPastDue\":\"\", \"AssetClassification\":\"\", \"HDETAILS\":{ \"SegmentCode\":\"HDETAILS\", \"BalanceAmt\":\"\" } }, { \"SegmentCode\":\"BPAYGRID\", \"TimePeriodInd\":\"M\", \"Year\":\"2015\", \"WeekNumber\":\"5\", \"Monthvalue\":\"Apr\", \"PaymentStatusValue\":\"\", \"DaysPastDue\":\"\", \"AssetClassification\":\"\", \"HDETAILS\":{ \"SegmentCode\":\"HDETAILS\", \"BalanceAmt\":\"\" } }, { \"SegmentCode\":\"BPAYGRID\", \"TimePeriodInd\":\"M\", \"Year\":\"2015\", \"WeekNumber\":\"5\", \"Monthvalue\":\"Mar\", \"PaymentStatusValue\":\"\", \"DaysPastDue\":\"\", \"AssetClassification\":\"\", \"HDETAILS\":{ \"SegmentCode\":\"HDETAILS\", \"BalanceAmt\":\"\" } }, { \"SegmentCode\":\"BPAYGRID\", \"TimePeriodInd\":\"M\", \"Year\":\"2015\", \"WeekNumber\":\"4\", \"Monthvalue\":\"Feb\", \"PaymentStatusValue\":\"\", \"DaysPastDue\":\"\", \"AssetClassification\":\"\", \"HDETAILS\":{ \"SegmentCode\":\"HDETAILS\", \"BalanceAmt\":\"\" } }, { \"SegmentCode\":\"BPAYGRID\", \"TimePeriodInd\":\"M\", \"Year\":\"2015\", \"WeekNumber\":\"5\", \"Monthvalue\":\"Jan\", \"PaymentStatusValue\":\"\", \"DaysPastDue\":\"\", \"AssetClassification\":\"\", \"HDETAILS\":{ \"SegmentCode\":\"HDETAILS\", \"BalanceAmt\":\"\" } }, { \"SegmentCode\":\"BPAYGRID\", \"TimePeriodInd\":\"M\", \"Year\":\"2014\", \"WeekNumber\":\"4\", \"Monthvalue\":\"Dec\", \"PaymentStatusValue\":\"\", \"DaysPastDue\":\"\", \"AssetClassification\":\"\", \"HDETAILS\":{ \"SegmentCode\":\"HDETAILS\", \"BalanceAmt\":\"\" } }, { \"SegmentCode\":\"BPAYGRID\", \"TimePeriodInd\":\"M\", \"Year\":\"2014\", \"WeekNumber\":\"4\", \"Monthvalue\":\"Nov\", \"PaymentStatusValue\":\"\", \"DaysPastDue\":\"\", \"AssetClassification\":\"\", \"HDETAILS\":{ \"SegmentCode\":\"HDETAILS\", \"BalanceAmt\":\"\" } } ] } } } }";
		return commercialResponse;
	}

	public void setConsumerUrl(String consumerUrl) {
		this.consumerUrl = consumerUrl;
	}

	public void setCommercialUrl(String commercialUrl) {
		this.commercialUrl = commercialUrl;
	}

	public void setClient(JSONClient client) {
		this.client = client;
	}

	public void setNiyoginDAOImpl(NiyoginDAOImpl niyoginDAOImpl) {
		this.niyoginDAOImpl = niyoginDAOImpl;
	}

}
