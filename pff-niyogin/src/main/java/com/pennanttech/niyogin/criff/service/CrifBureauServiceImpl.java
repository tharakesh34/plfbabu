package com.pennanttech.niyogin.criff.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import com.pennanttech.niyogin.criff.model.Applicant;
import com.pennanttech.niyogin.criff.model.CRIFConsumerResponse;
import com.pennanttech.niyogin.criff.model.CompanyAddress;
import com.pennanttech.niyogin.criff.model.CriffBureauCommercial;
import com.pennanttech.niyogin.criff.model.CriffBureauConsumer;
import com.pennanttech.niyogin.criff.model.CriffCommercialResponse;
import com.pennanttech.niyogin.criff.model.LoanDetail;
import com.pennanttech.niyogin.criff.model.LoanDetailsData;
import com.pennanttech.niyogin.criff.model.PaymentHistory;
import com.pennanttech.niyogin.criff.model.PersonalAddress;
import com.pennanttech.niyogin.criff.model.TradeLine;
import com.pennanttech.niyogin.utility.NiyoginUtility;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.CriffBureauService;
import com.pennanttech.pff.external.service.NiyoginService;

public class CrifBureauServiceImpl extends NiyoginService implements CriffBureauService {

	private static final Logger	logger													= Logger
			.getLogger(CrifBureauServiceImpl.class);

	private final String		commercialConfigFileName								= "crifBureauCommercial.properties";
	private final String		consumerConfigFileName									= "crifBureauConsumer.properties";

	private String				consumerUrl;
	private String				commercialUrl;

	//Experian Bureau
	//TODO:
	public static final String	REQ_SEND												= "";
	public static final String	RSN_CODE												= "REASONCODECRIF";
	public static final String	REMARKS													= "REMARKSCRIF";
	public static final String	STATUSCODE												= "";

	public static final String	OLDEST_LOANDISBURSED_DT									= "OLDESTLOANDISBUR";
	public static final String	NO_PREVS_LOANS_AS_OF_APP_DT								= "NOPREVIOUSLOANS";
	public static final String	IS_APP_SUBSTANDARD_IN_L6M								= "ISAPPLICANTSUBST";
	public static final String	IS_APP_REPORTED_AS_LOSS_IN_L6M							= "ISAPPLICANTREPOR";
	public static final String	IS_APP_DOUBTFUL_IN_L6M									= "ISAPPLICANTDOUBT";
	public static final String	IS_APP_MENTIONED_AS_SMA									= "ISAPPMENTSMA";
	public static final String	IS_APP_90PLUS_DPD_IN_L6M								= "ISAPPLICANT90DP";
	public static final String	LAST_UPDATE_DT_IN_BUREAU								= "LASTUPDATEDATE";
	public static final String	NOT_ENOUGH_INFO											= "NOTENOUGHINFO";
	public static final String	COMB_OF_PREVS_LOANS_TAKEN								= "AMBOFPRVSLOANS";
	public static final String	PRODUCT_INDEX											= "PROINDEXDETAILSHT";
	public static final String	SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS				= "SUMOFDISBURSEDAMT";
	public static final String	RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS		= "RATIOOFOVRDUEDIS";
	public static final String	NUMB_OF_BUS_LOANS_OPENED_IN_L6M							= "NOOFBUSILOANS";
	public static final String	MAX_PER_OF_AMT_REPAID_ACROSS_ALL_ACT_SEC_LOANS			= "MAXPEROFAMTREPAID";
	public static final String	MAX_DISBURSED_AMT_ACROSS_ALL_UNSECURED_LOANS_IN_L12M	= "MAXIMUMDISBURSED";
	public static final String	MIN_PER_OF_AMT_REPAID_ACROSS_ALL_UNSECURE_LOANS			= "MINIMUMPEROFAMT";
	public static final String	COMBINATION_OF_PREVIOUS_LOANS_TAKEN						= "AMBOFPRVSLOANS";
	public static final String	MONTHS_SINCE_30_PLUS_DPD_IN_L12M						= "MNTHSIN30DPDINALAS";

	private Date				appDate													= getAppDate();
	private String				pincode													= null;

	/**
	 * Method for execute CRIFF Bureau service<br>
	 * - Execute Commercial bureau service for SME and CORP customers<br>
	 * - Execute Consumer service for RETAIL customer.
	 * 
	 * @param auditHeader
	 */
	@Override
	public AuditHeader executeCriffBureau(AuditHeader auditHeader) throws InterfaceException, ParseException {
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
	 * @throws ParseException
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
		CriffBureauCommercial commercial = prepareCommercialRequestObj(customerDetails);
		//send request and log
		String reference = financeMain.getFinReference();
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;
		String statusCode = null;
		try {
			reuestString = client.getRequestString(commercial);
			jsonResponse = client.post(commercialUrl, reuestString);
			//check response for error
			errorCode = getErrorCode(jsonResponse);
			errorDesc = getErrorMessage(jsonResponse);
			statusCode = getStatusCode(jsonResponse);

			doInterfaceLogging(reference, commercialUrl, reuestString, jsonResponse, errorCode, errorDesc);

			appplicationdata.put(STATUSCODE, statusCode);
			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, getTrimmedMessage(errorDesc));

			if (StringUtils.isEmpty(errorCode)) {
				//read values from response and load it to extended map
				Map<String, Object> mapdata = getPropValueFromResp(jsonResponse, commercialConfigFileName);
				Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
				//process the response map
				Object responseObj = getResponseObject(jsonResponse, CriffCommercialResponse.class, false);
				CriffCommercialResponse commercialResponse = (CriffCommercialResponse) responseObj;
				//process the response
				prepareCommercialExtendedMap(commercialResponse, mapvalidData);
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
		CriffBureauConsumer consumer = prepareConsumerRequestObj(customerDetails);
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

			if (StringUtils.isEmpty(errorCode)) {
				//read values from response and load it to extended map
				Map<String, Object> mapdata = getPropValueFromResp(jsonResponse, consumerConfigFileName);
				Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
				//process the response map
				Object responseObj = getResponseObject(jsonResponse, CRIFConsumerResponse.class, false);
				CRIFConsumerResponse consumerResponse = (CRIFConsumerResponse) responseObj;
				//process the response
				prepareConsumerExtendedMap(consumerResponse, mapvalidData);
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
	 * Method for prepare Extended field map for commercial Bureau execution
	 * 
	 * @param commercialResponse
	 * @param extendedFieldMap
	 * @return
	 * @throws ParseException
	 */
	private Map<String, Object> prepareCommercialExtendedMap(CriffCommercialResponse commercialResponse,
			Map<String, Object> extendedFieldMap) throws ParseException {
		List<TradeLine> tradlineList = commercialResponse.getTradelines();
		if (tradlineList != null && !tradlineList.isEmpty()) {
			BigDecimal overDueAmt = BigDecimal.ZERO;
			BigDecimal disBursedAmt = BigDecimal.ZERO;
			BigDecimal disbursAmtSixMnths = BigDecimal.ZERO;
			int noBusLoanOpened = 0;
			Collections.sort(tradlineList, new SanctionDareComparator());
			//for oldest loan disbursed date
			Date disbursedDate = tradlineList.get(tradlineList.size() - 1).getSanctionDate();
			extendedFieldMap.put(OLDEST_LOANDISBURSED_DT, disbursedDate);

			List<String> paymentList = new ArrayList<>();
			BigDecimal closedloanDisbursAmt = BigDecimal.ZERO;

			for (TradeLine tradeline : tradlineList) {
				if (!tradeline.getCreditFacilityStatus().equalsIgnoreCase("Closed")) {
					extendedFieldMap.put(NO_PREVS_LOANS_AS_OF_APP_DT, true);
				}

				paymentList.add(tradeline.getPaymentHistory());

				//for sum of disbursed amount of all closed loans
				if (tradeline.getAccountStatus().equalsIgnoreCase("Closed")) {
					closedloanDisbursAmt = closedloanDisbursAmt.add(tradeline.getDisbursedAmount());
				}

				if (NiyoginUtility.getMonthsBetween(getAppDate(), tradeline.getSanctionDate()) <= 6) {
					disbursAmtSixMnths = disbursAmtSixMnths.add(tradeline.getDisbursedAmount());
					noBusLoanOpened++;
				}

				disBursedAmt = disBursedAmt.add(tradeline.getDisbursedAmount());
				overDueAmt = overDueAmt.add(tradeline.getOverdueAmount());
			}

			//for Sum of disbursed Amount of all closed loans
			extendedFieldMap.put(SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS, closedloanDisbursAmt);

			//Ratio of Overdue and Disbursement amount for all loans
			BigDecimal ratioOfOverdue = BigDecimal.ZERO;
			if (overDueAmt.compareTo(BigDecimal.ZERO) > 0) {
				ratioOfOverdue = overDueAmt.divide(disBursedAmt);
			}
			extendedFieldMap.put(RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS, ratioOfOverdue);

			//for number of business loans opened in last 6 months
			extendedFieldMap.put(NUMB_OF_BUS_LOANS_OPENED_IN_L6M, noBusLoanOpened);

			// calculte payment history details
			setPaymentHistoryDetails(extendedFieldMap, paymentList);

			//for last update date in Bureau
			Collections.sort(tradlineList, new LastReportedDateComparator());
			Date lastUpdatedDate = tradlineList.get(0).getLastReportedDate();
			//long months = getMonthsBetween(lastUpdatedDate, appDate);
			extendedFieldMap.put(LAST_UPDATE_DT_IN_BUREAU, lastUpdatedDate);
		}

		extendedFieldMap.put(PRODUCT_INDEX, getPincodeGroupId(pincode));
		return extendedFieldMap;
	}

	/**
	 * Method for prepare commercial request object
	 * 
	 * @param financeDetail
	 * @return
	 */
	private CriffBureauCommercial prepareCommercialRequestObj(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		Customer customer = customerDetails.getCustomer();

		CriffBureauCommercial commercial = new CriffBureauCommercial();
		commercial.setStgUnqRefId(customer.getCustID());
		commercial.setApplicationId(customer.getCustID());
		// prepare applicant details
		commercial.setApplicant(prepareApplicantDetails(customerDetails));
		// prepare company address details
		commercial.setCompanyAddress(prepareComapnyAddress(customerDetails.getAddressList()));
		commercial.setCompanyName(customer.getCustShrtName());
		commercial.setCompanyMobile(NiyoginUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList(),
				InterfaceConstants.PHONE_TYPE_OFF));
		commercial.setCompanyPAN(commercial.getApplicant().getPan());
		commercial.setLegalEntity(getCustTypeDesc(customer.getCustTypeCode()));

		logger.debug(Literal.LEAVING);
		return commercial;
	}

	private CriffBureauConsumer prepareConsumerRequestObj(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		Customer customer = customerDetails.getCustomer();
		CriffBureauConsumer consumer = new CriffBureauConsumer();
		consumer.setStgUnqRefId(customer.getCustID());
		consumer.setApplicationId(customer.getCustID());

		// prepare applicant details
		consumer.setApplicant(prepareApplicantDetails(customerDetails));
		consumer.getApplicant().setPersonalAddress(null);

		// prepare personla address details
		consumer.setAddress(preparePersonalAddress(customerDetails.getAddressList()));

		logger.debug(Literal.LEAVING);
		return consumer;
	}

	private Applicant prepareApplicantDetails(CustomerDetails customerDetails) {
		Customer customer = customerDetails.getCustomer();
		Applicant applicant = new Applicant();
		applicant.setFirstName(customer.getCustShrtName());
		applicant.setLastName(customer.getCustShrtName());
		applicant.setDob(NiyoginUtility.formatDate(customer.getCustDOB(), "dd-MM-yyyy"));
		applicant.setGender(InterfaceConstants.PFF_GENDER_M);
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		applicant.setPan(getPanNumber(documentList));
		applicant.setMaritalStatus(InterfaceConstants.PFF_MARITAL_STATUS);
		applicant.setMobile(NiyoginUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList(),
				InterfaceConstants.PHONE_TYPE_PER));
		// personal address details
		applicant.setPersonalAddress(preparePersonalAddress(customerDetails.getAddressList()));
		return applicant;
	}

	private PersonalAddress preparePersonalAddress(List<CustomerAddres> addressList) {
		PersonalAddress personalAddress = new PersonalAddress();
		CustomerAddres address = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_PER);

		City city = getCityDetails(address);

		personalAddress.setHouseNo(address.getCustAddrHNbr());
		personalAddress.setLandmark(address.getCustAddrStreet());
		if (city != null) {
			personalAddress.setCity(city.getPCCityName());
			personalAddress.setCountry(city.getLovDescPCCountryName());
			personalAddress.setPin(address.getCustAddrZIP());
			pincode = address.getCustAddrZIP();
			personalAddress.setState(city.getLovDescPCProvinceName());
		}
		personalAddress.setCareOf(StringUtils.isNotBlank(address.getCustAddrLine3()) ? address.getCustAddrLine3()
				: InterfaceConstants.DEFAULT_CAREOF);
		personalAddress.setDistrict(StringUtils.isNotBlank(address.getCustDistrict()) ? address.getCustDistrict()
				: InterfaceConstants.DEFAULT_DIST);
		personalAddress.setSubDistrict(StringUtils.isNotBlank(address.getCustAddrLine4()) ? address.getCustAddrLine4()
				: InterfaceConstants.DEFAULT_SUBDIST);
		return personalAddress;
	}

	private CompanyAddress prepareComapnyAddress(List<CustomerAddres> addressList) {
		CompanyAddress companyAddress = new CompanyAddress();
		CustomerAddres address = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_OFF);

		City city = getCityDetails(address);

		String addressLines = address.getCustAddrType() + "," + address.getCustAddrHNbr() + ","
				+ address.getCustAddrStreet();
		companyAddress.setAddress1(addressLines);
		companyAddress.setAddress2(addressLines);
		companyAddress.setAddress3(addressLines);

		if (city != null) {
			companyAddress.setCity(city.getPCCityName());
			companyAddress.setCountry(city.getLovDescPCCountryName());
			companyAddress.setPin(address.getCustAddrZIP());
			companyAddress.setState(city.getLovDescPCProvinceName());
		}
		companyAddress.setDistrict(StringUtils.isNotBlank(address.getCustDistrict()) ? address.getCustDistrict()
				: InterfaceConstants.DEFAULT_DIST);
		return companyAddress;
	}

	/**
	 * Method for prepare Extended field map for consumer Bureau execution
	 * 
	 * @param consumerResponse
	 * @param extendedFieldMap
	 * @return
	 * @throws ParseException
	 */
	private Map<String, Object> prepareConsumerExtendedMap(CRIFConsumerResponse consumerResponse,
			Map<String, Object> extendedFieldMap) throws ParseException {

		List<LoanDetail> loanDetailsList = new ArrayList<LoanDetail>();
		for (LoanDetailsData loanData : consumerResponse.getLoanDetailsData()) {
			loanDetailsList.add(loanData.getLoanDetail());
		}

		Collections.sort(loanDetailsList, new DisbursedDateComparator());

		//for oldest loan disbursed date
		Date disbursedDate = loanDetailsList.get(loanDetailsList.size() - 1).getDisbursedDate();
		extendedFieldMap.put(OLDEST_LOANDISBURSED_DT, disbursedDate);

		List<String> paymentList = new ArrayList<>(1);
		BigDecimal maxPerOfAmtRepaidOnSL = BigDecimal.ZERO;
		BigDecimal closedloanDisbursAmt = BigDecimal.ZERO;
		BigDecimal maxDsbursmentAmt = BigDecimal.ZERO;
		BigDecimal overDueAmt = BigDecimal.ZERO;
		BigDecimal disBursedAmt = BigDecimal.ZERO;
		BigDecimal disbursAmtSixMnths = BigDecimal.ZERO;
		int noBusLoanOpened = 0;

		//for minimum checking take first one and compare
		BigDecimal minPerOfAmtRepaidOnSL = loanDetailsList.get(0).getDisbursedAmt();
		String[] zeroAccTypes = { "Housing Loan", "Auto Loan (Personal)", "Credit Card" };
		String[] oneAccTypes = { "Business Loan General", "Business Loan Priority Sector Small Business", "Overdraft",
				"Consumer Loan", "Two-Wheeler Loan", "Personal Loan" };
		StringBuffer sb = new StringBuffer();
		for (LoanDetail loanDetail : loanDetailsList) {
			//for no previous loans as of application date
			if (!loanDetail.getAccountStatus().equalsIgnoreCase("Closed")) {
				extendedFieldMap.put(NO_PREVS_LOANS_AS_OF_APP_DT, true);
			}

			//for max amount repaid across all active secured_loans
			if (loanDetail.getSecurityDetails() != null && !loanDetail.getSecurityDetails().isEmpty()) {
				BigDecimal value = (loanDetail.getDisbursedAmt().subtract(loanDetail.getCurrentBal()))
						.divide(loanDetail.getDisbursedAmt());
				if (value.compareTo(maxPerOfAmtRepaidOnSL) > 0) {
					maxPerOfAmtRepaidOnSL = value;
				}
			}

			//sum of disbursed amount of all closed loans
			if (loanDetail.getAccountStatus().equalsIgnoreCase("Closed")) {
				closedloanDisbursAmt = closedloanDisbursAmt.add(loanDetail.getDisbursedAmt());
			}

			//Maximum disbursed Amount across all unsecured loans in the last 12 months
			Date disbursmentDate = loanDetail.getDisbursedDate();
			if (NiyoginUtility.getMonthsBetween(appDate, disbursmentDate) <= 12) {
				if (loanDetail.getSecurityDetails() != null && !loanDetail.getSecurityDetails().isEmpty()) {
					maxDsbursmentAmt = maxDsbursmentAmt.add(loanDetail.getDisbursedAmt());
				}
			}

			//for min per of amt repaid across all unsecure loans
			if (loanDetail.getSecurityDetails() != null && loanDetail.getSecurityDetails().isEmpty()) {
				BigDecimal value = (loanDetail.getDisbursedAmt().subtract(loanDetail.getCurrentBal()))
						.divide(loanDetail.getDisbursedAmt());
				if (value.compareTo(minPerOfAmtRepaidOnSL) < 0) {
					minPerOfAmtRepaidOnSL = value;
				}
			}

			paymentList.add(loanDetail.getCombinedPaymentHistory());

			//for combination of previous loans taken
			if (Arrays.asList(zeroAccTypes).contains(loanDetail.getAcctType())) {
				sb.append("0");
			} else if (Arrays.asList(oneAccTypes).contains(loanDetail.getAcctType())) {
				sb.append("1");
			}

			disBursedAmt = disBursedAmt.add(loanDetail.getDisbursedAmt());
			overDueAmt = overDueAmt.add(loanDetail.getOverdueAmt());

			if (NiyoginUtility.getMonthsBetween(getAppDate(), loanDetail.getDisbursedDate()) <= 6) {
				disbursAmtSixMnths = disbursAmtSixMnths.add(loanDetail.getDisbursedAmt());
				noBusLoanOpened++;
			}
		}

		//for  maxPerOfAmtRepaidOnSL
		maxPerOfAmtRepaidOnSL = maxPerOfAmtRepaidOnSL.multiply(new BigDecimal(100));
		extendedFieldMap.put(MAX_PER_OF_AMT_REPAID_ACROSS_ALL_ACT_SEC_LOANS, maxPerOfAmtRepaidOnSL);

		//Sum of disbursed Amount of all closed loans
		extendedFieldMap.put(SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS, closedloanDisbursAmt);

		//Maximum disbursed Amount across all unsecured loans in the last 12 months
		extendedFieldMap.put(MAX_DISBURSED_AMT_ACROSS_ALL_UNSECURED_LOANS_IN_L12M, maxDsbursmentAmt);

		//for  minPerOfAmtRepaidOnSL
		minPerOfAmtRepaidOnSL = minPerOfAmtRepaidOnSL.multiply(new BigDecimal(100));
		extendedFieldMap.put(MIN_PER_OF_AMT_REPAID_ACROSS_ALL_UNSECURE_LOANS, minPerOfAmtRepaidOnSL);

		//for  comb of previous loan taken
		extendedFieldMap.put(COMBINATION_OF_PREVIOUS_LOANS_TAKEN, sb.toString());

		//Number of business loans opened in last 6 months
		extendedFieldMap.put(NUMB_OF_BUS_LOANS_OPENED_IN_L6M, noBusLoanOpened);

		//Ratio of Overdue and Disbursement amount for all loans
		BigDecimal ratioOfOverdue = BigDecimal.ZERO;
		if (overDueAmt.compareTo(BigDecimal.ZERO) > 0) {
			ratioOfOverdue = overDueAmt.divide(disBursedAmt);
		}
		extendedFieldMap.put(RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS, ratioOfOverdue);

		// calculte payment history details
		setPaymentHistoryDetails(extendedFieldMap, paymentList);

		//for last update date in Bureau
		Collections.sort(loanDetailsList, new InfoAsOnComparator());
		Date lastUpdatedDate = loanDetailsList.get(0).getInfoAsOn();
		long months = NiyoginUtility.getMonthsBetween(lastUpdatedDate, appDate);
		if (months > 36) {
			extendedFieldMap.put(LAST_UPDATE_DT_IN_BUREAU, lastUpdatedDate);
		}

		extendedFieldMap.put(PRODUCT_INDEX, getPincodeGroupId(pincode));
		return extendedFieldMap;
	}

	private void setPaymentHistoryDetails(Map<String, Object> extendedFieldMap, List<String> paymentList)
			throws ParseException {
		List<PaymentHistory> paymentHistories = preparePaymentHistory(paymentList);
		Collections.sort(paymentHistories, new PaymentHistoryComparator());
		int zeroCount = 0;
		int crossCount = 0;
		for (PaymentHistory paymentHistory : paymentHistories) {
			if (NiyoginUtility.getMonthsBetween(getAppDate(), paymentHistory.getPaymentDate()) <= 6) {
				try {
					if (Long.valueOf(paymentHistory.getDpd()) >= 90) {
						extendedFieldMap.put(IS_APP_90PLUS_DPD_IN_L6M, true);
					}
				} catch (Exception e) {
					//In case of DPD = XXX
				}
				if (StringUtils.equals(paymentHistory.getType(), "SUB")) {
					extendedFieldMap.put(IS_APP_SUBSTANDARD_IN_L6M, true);
				}

				if (StringUtils.equals(paymentHistory.getType(), "LOS")) {
					extendedFieldMap.put(IS_APP_REPORTED_AS_LOSS_IN_L6M, true);

				}
				if (StringUtils.equals(paymentHistory.getType(), "DBT")) {
					extendedFieldMap.put(IS_APP_DOUBTFUL_IN_L6M, true);
				}
			}

			if (NiyoginUtility.getMonthsBetween(getAppDate(), paymentHistory.getPaymentDate()) <= 12) {
				if (StringUtils.equalsIgnoreCase(paymentHistory.getDpd(), "000")) {
					zeroCount++;
				} else if (StringUtils.equalsIgnoreCase(paymentHistory.getDpd(), "XXX")) {
					crossCount++;
				}
			}

			if (StringUtils.equalsIgnoreCase(paymentHistory.getType(), "SMA")) {
				extendedFieldMap.put(IS_APP_MENTIONED_AS_SMA, true);
			}
		}

		if (zeroCount < 4 || crossCount == 12) {
			extendedFieldMap.put(NOT_ENOUGH_INFO, true);
		} else {
			extendedFieldMap.put(NOT_ENOUGH_INFO, false);
		}

		//for Months since 30+DPD in the last 12 months
		Date startDate = null;
		for (PaymentHistory paymentHistory : paymentHistories) {
			try {
				long dpd = Long.parseLong(paymentHistory.getDpd());
				if (dpd > 30) {
					startDate = paymentHistory.getPaymentDate();
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if (startDate != null) {
			long dpdMonths = NiyoginUtility.getMonthsBetween(getAppDate(), startDate);
			extendedFieldMap.put(MONTHS_SINCE_30_PLUS_DPD_IN_L12M, dpdMonths);
		}
	}

	private List<PaymentHistory> preparePaymentHistory(List<String> paymentList) throws ParseException {
		List<PaymentHistory> paymentHistoryList = new ArrayList<PaymentHistory>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MMM:yyyy");
		for (String payment : paymentList) {
			String[] block = payment.split("\\|");
			for (String field : block) {
				PaymentHistory paymentHistory = new PaymentHistory();
				paymentHistory.setPaymentDate(dateFormat.parse("01:" + field.substring(0, field.indexOf(","))));
				paymentHistory.setDpd(field.substring(field.indexOf(",") + 1, field.indexOf("/")));
				paymentHistory.setType(field.substring(field.indexOf("/") + 1, field.length()));
				paymentHistoryList.add(paymentHistory);
			}
		}
		return paymentHistoryList;
	}

	/**
	 * 
	 * This Comparator class is used to sort the LoanDetailsData based on their DisbursedDate H to L
	 */
	public class DisbursedDateComparator implements Comparator<LoanDetail> {
		@Override
		public int compare(LoanDetail arg0, LoanDetail arg1) {

			return arg1.getDisbursedDate().compareTo(arg0.getDisbursedDate());
		}
	}

	/**
	 * 
	 * This Comparator class is used to sort the LoanDetailsData based on their InfoAsOnDate H to L
	 */
	public class InfoAsOnComparator implements Comparator<LoanDetail> {
		@Override
		public int compare(LoanDetail arg0, LoanDetail arg1) {

			return arg1.getInfoAsOn().compareTo(arg0.getInfoAsOn());
		}
	}

	/**
	 * 
	 * This Comparator class is used to sort the LoanDetailsData based on their DisbursedDate H to L
	 */
	public class PaymentHistoryComparator implements Comparator<PaymentHistory> {
		@Override
		public int compare(PaymentHistory arg0, PaymentHistory arg1) {

			return arg1.getPaymentDate().compareTo(arg0.getPaymentDate());
		}
	}

	/**
	 * 
	 * This Comparator class is used to sort the LoanDetailsData based on their DisbursedDate H to L
	 */
	public class SanctionDareComparator implements Comparator<TradeLine> {
		@Override
		public int compare(TradeLine arg0, TradeLine arg1) {
			return arg1.getSanctionDate().compareTo(arg0.getSanctionDate());
		}
	}

	/**
	 * 
	 * This Comparator class is used to sort the LoanDetailsData based on their InfoAsOnDate H to L
	 */
	public class LastReportedDateComparator implements Comparator<TradeLine> {
		@Override
		public int compare(TradeLine arg0, TradeLine arg1) {
			return arg1.getLastReportedDate().compareTo(arg0.getLastReportedDate());
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
