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
import org.apache.commons.lang3.ObjectUtils;
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

	private static final Logger	logger													= Logger.getLogger(CrifBureauServiceImpl.class);

	private final String		commercialConfigFileName								= "crifBureauCommercial.properties";
	private final String		consumerConfigFileName									= "crifBureauConsumer.properties";

	private String				consumerUrl;
	private String				commercialUrl;

	//Experian Bureau
	public static final String	REQ_SEND												= "REQSENDCRIF";
	public static final String	STATUSCODE												= "STATUSCRIF";
	public static final String	RSN_CODE												= "REASONCRIF";
	public static final String	REMARKS													= "REMARKSCRIF";

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
	public static final String	MONTHS_SINCE_30_PLUS_DPD_IN_L12M						= "MNTHSIN30DPDINALAS";
	
	//for CoAPP
	private final String		COAPP_REQ_SEND												= "CAREQSENDCRIF";
	private final String		COAPP_STATUSCODE											= "CASTATUSCRIF";
	private final String		COAPP_RSN_CODE												= "CAREASONCRIF";
	private final String		COAPP_REMARKS												= "CAREMARKSCRIF";
	private final String		COAPP_OLDEST_LOANDISBURSED_DT								= "CAOLDESTLOANDISBUR";
	private final String		COAPP_NO_PREVS_LOANS_AS_OF_APP_DT							= "CANOPREVIOUSLOANS";
	private final String		COAPP_IS_APP_SUBSTANDARD_IN_L6M								= "CAISAPPLICANTSUBST";
	private final String		COAPP_IS_APP_REPORTED_AS_LOSS_IN_L6M						= "CAISAPPLICANTREPOR";
	private final String		COAPP_IS_APP_DOUBTFUL_IN_L6M								= "CAISAPPLICANTDOUBT";
	private final String		COAPP_IS_APP_MENTIONED_AS_SMA								= "CAISAPPMENTSMA";
	private final String		COAPP_IS_APP_90PLUS_DPD_IN_L6M								= "CAISAPPLICANT90DP";
	private final String		COAPP_LAST_UPDATE_DT_IN_BUREAU								= "CALASTUPDATEDATE";
	private final String		COAPP_NOT_ENOUGH_INFO										= "CANOTENOUGHINFO";
	private final String		COAPP_COMB_OF_PREVS_LOANS_TAKEN								= "CAAMBOFPRVSLOANS";
	private final String		COAPP_PRODUCT_INDEX											= "CAPROINDEXDETAILSHT";
	private final String		COAPP_SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS				= "CASUMOFDISBURSEDAMT";
	private final String		COAPP_RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS	= "CARATIOOFOVRDUEDIS";
	private final String		COAPP_NUMB_OF_BUS_LOANS_OPENED_IN_L6M						= "CANOOFBUSILOANS";
	private final String		COAPP_MAX_PER_OF_AMT_REPAID_ACROSS_ALL_ACT_SEC_LOANS		= "CANOOFBUSILOANS";
	private final String		COAPP_MAX_DISBURSED_AMT_ACROSS_ALL_UNSECURED_LOANS_IN_L12M	= "CAMAXIMUMDISBURSED";
	private final String		COAPP_MIN_PER_OF_AMT_REPAID_ACROSS_ALL_UNSECURE_LOANS		= "CAMINIMUMPEROFAMT";
	private final String		COAPP_MONTHS_SINCE_30_PLUS_DPD_IN_L12M						= "CAMNTHSIN30DPDINALAS";

	private Date				appDate													= getAppDate();
	private String				pincode													= null;
	private final String		ACCOUNT_STATUS_CLOSED										= "S07";

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
		
		if (StringUtils.isBlank(consumerUrl) && StringUtils.isBlank(commercialUrl)) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		

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
		Map<String, Object> coAppplicantsdata = new HashMap<>();
		List<CustomerDetails> coApplicantCustomers = getCoApplicants(coApplicantIDs);
		for (CustomerDetails coAppCustomerDetail : coApplicantCustomers) {
			appplicationdata=executeBureau(financeDetail, coAppCustomerDetail);
			processCoAppResponse(coAppplicantsdata,appplicationdata);
		}
		Map<String, Object> mapvalidData = validateExtendedMapValues(coAppplicantsdata);
		prepareResponseObj(mapvalidData, financeDetail);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for prepare the CoApplicants data as a String value by separating each CoApplicant data with delimiter.
	 * 
	 * @param coAppplicantsdata
	 * @param appplicationdata
	 */
	private void processCoAppResponse(Map<String, Object> coAppplicantsdata, Map<String, Object> appplicationdata) {
		logger.debug(Literal.ENTERING);
		if (appplicationdata != null) {
			
			prepareListData(COAPP_REQ_SEND,REQ_SEND, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_STATUSCODE,STATUSCODE, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_RSN_CODE,RSN_CODE, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_REMARKS,REMARKS, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_OLDEST_LOANDISBURSED_DT,OLDEST_LOANDISBURSED_DT, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_NO_PREVS_LOANS_AS_OF_APP_DT,NO_PREVS_LOANS_AS_OF_APP_DT, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_IS_APP_SUBSTANDARD_IN_L6M,IS_APP_SUBSTANDARD_IN_L6M, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_IS_APP_REPORTED_AS_LOSS_IN_L6M,IS_APP_REPORTED_AS_LOSS_IN_L6M, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_IS_APP_DOUBTFUL_IN_L6M,IS_APP_DOUBTFUL_IN_L6M, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_IS_APP_MENTIONED_AS_SMA,IS_APP_MENTIONED_AS_SMA, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_IS_APP_90PLUS_DPD_IN_L6M,IS_APP_90PLUS_DPD_IN_L6M, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_LAST_UPDATE_DT_IN_BUREAU, LAST_UPDATE_DT_IN_BUREAU, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_NOT_ENOUGH_INFO, NOT_ENOUGH_INFO, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_COMB_OF_PREVS_LOANS_TAKEN, COMB_OF_PREVS_LOANS_TAKEN, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_PRODUCT_INDEX, PRODUCT_INDEX, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS,SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS, RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_NUMB_OF_BUS_LOANS_OPENED_IN_L6M,NUMB_OF_BUS_LOANS_OPENED_IN_L6M, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_MAX_PER_OF_AMT_REPAID_ACROSS_ALL_ACT_SEC_LOANS, MAX_PER_OF_AMT_REPAID_ACROSS_ALL_ACT_SEC_LOANS, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_MAX_DISBURSED_AMT_ACROSS_ALL_UNSECURED_LOANS_IN_L12M, MAX_DISBURSED_AMT_ACROSS_ALL_UNSECURED_LOANS_IN_L12M, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_MIN_PER_OF_AMT_REPAID_ACROSS_ALL_UNSECURE_LOANS, MIN_PER_OF_AMT_REPAID_ACROSS_ALL_UNSECURE_LOANS, coAppplicantsdata, appplicationdata);
			prepareListData(COAPP_MONTHS_SINCE_30_PLUS_DPD_IN_L12M, MONTHS_SINCE_30_PLUS_DPD_IN_L12M, coAppplicantsdata, appplicationdata);

		}
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Method for combining both coApplicantsMap data and currentDataMap data by appending a delimeter.
	 * 
	 * @param curMapKey
	 * @param coAppKey
	 * @param coApplicantsMap
	 * @param currentDataMap
	 * @return
	 */
	private void prepareListData(String coAppKey, String curMapKey, Map<String, Object> coApplicantsMap,
			Map<String, Object> currentDataMap) {
		String value = null;
		value = getval(coApplicantsMap.get(coAppKey)) + getval(currentDataMap.get(curMapKey)) + LIST_DELIMETER;
		coApplicantsMap.put(coAppKey, value);
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
		String reference = financeMain.getFinReference();
		CriffBureauCommercial commercial = prepareCommercialRequestObj(customerDetails, reference);
		//send request and log
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;
		Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());
		try {
			reuestString = client.getRequestString(commercial);
			jsonResponse = client.post(commercialUrl, reuestString);
			//check response for error
			errorCode = getErrorCode(jsonResponse);
			errorDesc = getErrorMessage(jsonResponse);

			doInterfaceLogging(reference, commercialUrl, reuestString, jsonResponse, errorCode, errorDesc, reqSentOn);

			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, getTrimmedMessage(errorDesc));
			appplicationdata.put(STATUSCODE, getStatusCode(jsonResponse));

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
			doExceptioLogging(reference, commercialUrl, reuestString, jsonResponse, errorDesc, reqSentOn);

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
		String reference = financeMain.getFinReference();
		CriffBureauConsumer consumer = prepareConsumerRequestObj(customerDetails, reference);
		//send request and log
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;
		Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());
		try {
			reuestString = client.getRequestString(consumer);
			jsonResponse = client.post(consumerUrl, reuestString);
			//check response for error
			errorCode = getErrorCode(jsonResponse);
			errorDesc = getErrorMessage(jsonResponse);

			doInterfaceLogging(reference, consumerUrl, reuestString, jsonResponse, errorCode, errorDesc, reqSentOn);

			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, getTrimmedMessage(errorDesc));
			appplicationdata.put(STATUSCODE, getStatusCode(jsonResponse));

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
			doExceptioLogging(reference, consumerUrl, reuestString, jsonResponse, errorDesc, reqSentOn);

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
			Map<String, Object> extendedFieldMap) {
		logger.debug(Literal.ENTERING);
		List<TradeLine> tradlineList = commercialResponse.getTradelines();
		if (tradlineList != null && !tradlineList.isEmpty()) {
			BigDecimal totSanctionAmt = BigDecimal.ZERO;
			BigDecimal totCurrentBal = BigDecimal.ZERO;
			BigDecimal disbursAmtSixMnths = BigDecimal.ZERO;
			int noBusLoanOpened = 0;
			Date disbursedDate = tradlineList.get(0).getSanctionDate();
			Date lastUpdatedDate = tradlineList.get(0).getLastReportedDate();
			List<String> paymentList = new ArrayList<>();
			BigDecimal closedloanDisbursAmt = BigDecimal.ZERO;

			for (TradeLine tradeline : tradlineList) {
				if (!tradeline.getCreditFacilityStatus().equalsIgnoreCase("Closed")) {
					extendedFieldMap.put(NO_PREVS_LOANS_AS_OF_APP_DT, true);
				}

				paymentList.add(tradeline.getPaymentHistory());

				//for sum of disbursed amount of all closed loans
				// Sum(SanctionedAmt) where Account-Status is Closed in Last 12 months.
				if (tradeline.getSanctionDate() != null
						&& NiyoginUtility.getMonthsBetween(getAppDate(), tradeline.getSanctionDate()) <= 12) {
					if (tradeline.getAccountStatus().equalsIgnoreCase(ACCOUNT_STATUS_CLOSED)) {
						closedloanDisbursAmt = closedloanDisbursAmt.add(tradeline.getDisbursedAmount());
					}
				}

				//noBusLoanOpened: If Sanctioned_DT is within last 6 months as on loan application date, then loan is considered.
				if (tradeline.getSanctionDate() != null
						&& NiyoginUtility.getMonthsBetween(getAppDate(), tradeline.getSanctionDate()) <= 6) {
					disbursAmtSixMnths = disbursAmtSixMnths.add(tradeline.getDisbursedAmount());
					noBusLoanOpened++;
				}

				totSanctionAmt = totSanctionAmt.add(tradeline.getSanctionedAmount());
				totCurrentBal = totCurrentBal.add(tradeline.getCurrentBalance());
				//for oldest loan disbursed date
				if (disbursedDate != null && tradeline.getSanctionDate() != null) {
					if (disbursedDate.compareTo(tradeline.getSanctionDate()) > 0) {
						disbursedDate = tradeline.getSanctionDate();
					}
				} else {
					disbursedDate = null;
				}

				//for last update date in Bureau
				if (lastUpdatedDate != null && tradeline.getLastReportedDate() != null) {
					if (lastUpdatedDate.compareTo(tradeline.getLastReportedDate()) < 0) {
						lastUpdatedDate = tradeline.getLastReportedDate();
					}
				} else {
					lastUpdatedDate = null;
				}
			}

			//for Sum of disbursed Amount of all closed loans
			extendedFieldMap.put(SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS, closedloanDisbursAmt);

			//Ratio of Overdue and Disbursement amount for all loans
			//Formula = ((Total Sanctioned Amount - Total Current Balance)/Total Sanctioned Amount)*100
			BigDecimal ratioOfOverdue = BigDecimal.ZERO;
			if (totSanctionAmt.compareTo(BigDecimal.ZERO) > 0) {
				ratioOfOverdue = (totSanctionAmt.subtract(totCurrentBal)).divide(totSanctionAmt);
				ratioOfOverdue = ratioOfOverdue.multiply(new BigDecimal(100));
			}
			extendedFieldMap.put(RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS, ratioOfOverdue);

			//for number of business loans opened in last 6 months
			extendedFieldMap.put(NUMB_OF_BUS_LOANS_OPENED_IN_L6M, noBusLoanOpened);

			// calculte payment history details
			setPaymentHistoryDetails(extendedFieldMap, paymentList);

			//for last update date in Bureau
			extendedFieldMap.put(LAST_UPDATE_DT_IN_BUREAU, lastUpdatedDate);
			//for oldest loan disbursed date
			extendedFieldMap.put(OLDEST_LOANDISBURSED_DT, disbursedDate);
		}

		extendedFieldMap.put(PRODUCT_INDEX, getPincodeGroupId(pincode));
		logger.debug(Literal.LEAVING);
		return extendedFieldMap;
	}

	/**
	 * Method for prepare commercial request object
	 * 
	 * @param financeDetail
	 * @param finReference
	 * @return
	 */
	private CriffBureauCommercial prepareCommercialRequestObj(CustomerDetails customerDetails, String finReference) {
		logger.debug(Literal.ENTERING);
		Customer customer = customerDetails.getCustomer();

		CriffBureauCommercial commercial = new CriffBureauCommercial();
		commercial.setStgUnqRefId(customer.getCustID());
		commercial.setApplicationId(customer.getCustID());
		commercial.setCustCIF(customer.getCustCIF());
		commercial.setFinReference(finReference);
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

	/**
	 * Method for prepare the consumer request object.
	 * 
	 * @param customerDetails
	 * @param finReference
	 * @return
	 */
	private CriffBureauConsumer prepareConsumerRequestObj(CustomerDetails customerDetails, String finReference) {
		logger.debug(Literal.ENTERING);
		Customer customer = customerDetails.getCustomer();
		CriffBureauConsumer consumer = new CriffBureauConsumer();
		consumer.setStgUnqRefId(customer.getCustID());
		consumer.setApplicationId(customer.getCustID());
		consumer.setCustCIF(customer.getCustCIF());
		consumer.setFinReference(finReference);
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
			Map<String, Object> extendedFieldMap) {

		List<LoanDetail> loanDetailsList = new ArrayList<LoanDetail>();
		for (LoanDetailsData loanData : consumerResponse.getLoanDetailsData()) {
			loanDetailsList.add(loanData.getLoanDetail());
		}

		List<String> paymentList = new ArrayList<>(1);
		BigDecimal maxPerOfAmtRepaidOnSL = BigDecimal.ZERO;
		BigDecimal closedloanDisbursAmt = BigDecimal.ZERO;
		BigDecimal maxDsbursmentAmt = BigDecimal.ZERO;
		BigDecimal overDueAmt = BigDecimal.ZERO;
		BigDecimal disBursedAmt = BigDecimal.ZERO;
		BigDecimal disbursAmtSixMnths = BigDecimal.ZERO;
		int noBusLoanOpened = 0;
		BigDecimal tempValueForMaxAmt=BigDecimal.ZERO;
		BigDecimal tempValueForMinAmt=BigDecimal.ZERO;

		//for minimum checking take first one and compare
		BigDecimal minPerOfAmtRepaidOnSL = loanDetailsList.get(0).getDisbursedAmt();
		String[] zeroAccTypes = { "Housing Loan", "Auto Loan (Personal)", "Credit Card" };
		String[] oneAccTypes = { "Business Loan General", "Business Loan Priority Sector Small Business", "Overdraft",
				"Consumer Loan", "Two-Wheeler Loan", "Personal Loan" };
		StringBuffer sb = new StringBuffer();
		Date oldDisbursedDate = loanDetailsList.get(0).getDisbursedDate();
		Date lastUpdatedDate = loanDetailsList.get(0).getInfoAsOn();
		for (LoanDetail loanDetail : loanDetailsList) {
			//for no previous loans as of application date
			if (!loanDetail.getAccountStatus().equalsIgnoreCase("Closed")) {
				extendedFieldMap.put(NO_PREVS_LOANS_AS_OF_APP_DT, true);
			}

			//for max amount repaid across all active secured_loans
			if (loanDetail.getSecurityDetails() != null && !loanDetail.getSecurityDetails().isEmpty()) {
				BigDecimal paidAmt = loanDetail.getDisbursedAmt().subtract(loanDetail.getCurrentBal());
				if (loanDetail.getDisbursedAmt().compareTo(BigDecimal.ZERO) > 0 && paidAmt.compareTo(BigDecimal.ZERO) > 0) {
					tempValueForMaxAmt = paidAmt.divide(loanDetail.getDisbursedAmt());
				}
				if (tempValueForMaxAmt.compareTo(maxPerOfAmtRepaidOnSL) > 0) {
					maxPerOfAmtRepaidOnSL = tempValueForMaxAmt;
				}
			}

			//sum of disbursed amount of all closed loans
			if (loanDetail.getAccountStatus().equalsIgnoreCase("Closed")) {
				closedloanDisbursAmt = closedloanDisbursAmt.add(loanDetail.getDisbursedAmt());
			}

			//Maximum disbursed Amount across all unsecured loans in the last 12 months
			Date disbursmentDate = loanDetail.getDisbursedDate();
			if (disbursmentDate != null && NiyoginUtility.getMonthsBetween(appDate, disbursmentDate) <= 12) {
				if (loanDetail.getSecurityDetails() != null && loanDetail.getSecurityDetails().isEmpty()) {
					maxDsbursmentAmt = maxDsbursmentAmt.add(loanDetail.getDisbursedAmt());
				}
			}

			//for min per of amt repaid across all unsecure loans
			if (loanDetail.getSecurityDetails() != null && loanDetail.getSecurityDetails().isEmpty()) {
				BigDecimal paidAmt=loanDetail.getDisbursedAmt().subtract(loanDetail.getCurrentBal());
				if(loanDetail.getDisbursedAmt().compareTo(BigDecimal.ZERO)>0&&paidAmt.compareTo(BigDecimal.ZERO)>0){
					tempValueForMinAmt=paidAmt.divide(loanDetail.getDisbursedAmt());
				}
				if (tempValueForMinAmt.compareTo(minPerOfAmtRepaidOnSL) < 0) {
					minPerOfAmtRepaidOnSL = tempValueForMinAmt;
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

			if (loanDetail.getDisbursedDate()!=null && NiyoginUtility.getMonthsBetween(getAppDate(), loanDetail.getDisbursedDate()) <= 6) {
				disbursAmtSixMnths = disbursAmtSixMnths.add(loanDetail.getDisbursedAmt());
				noBusLoanOpened++;
			}
			//for oldest loan disbursed date
			if (oldDisbursedDate != null && loanDetail.getDisbursedDate() != null) {
				if (oldDisbursedDate.compareTo(loanDetail.getDisbursedDate()) > 0) {
					oldDisbursedDate = loanDetail.getDisbursedDate();
				}
			} else {
				oldDisbursedDate = null;
			}
			//for last update date in Bureau
			if (lastUpdatedDate != null && loanDetail.getInfoAsOn() != null) {
				if (lastUpdatedDate.compareTo(loanDetail.getInfoAsOn()) < 0) {
					lastUpdatedDate = loanDetail.getInfoAsOn();
				}
			} else {
				lastUpdatedDate = null;
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
		extendedFieldMap.put(COMB_OF_PREVS_LOANS_TAKEN, sb.toString());

		//Number of business loans opened in last 6 months
		extendedFieldMap.put(NUMB_OF_BUS_LOANS_OPENED_IN_L6M, noBusLoanOpened);

		//Ratio of Overdue and Disbursement amount for all loans
		BigDecimal ratioOfOverdue = BigDecimal.ZERO;
		if (overDueAmt.compareTo(BigDecimal.ZERO) > 0 && disBursedAmt.compareTo(BigDecimal.ZERO) > 0) {
			ratioOfOverdue = overDueAmt.divide(disBursedAmt);
		}
		extendedFieldMap.put(RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS, ratioOfOverdue);

		// calculte payment history details
		setPaymentHistoryDetails(extendedFieldMap, paymentList);

		//for last update date in Bureau
		if (lastUpdatedDate != null) {
			long months = NiyoginUtility.getMonthsBetween(lastUpdatedDate, appDate);
			if (months > 36) {
				extendedFieldMap.put(LAST_UPDATE_DT_IN_BUREAU, lastUpdatedDate);
			}
		}
		//for oldest loan disbursed date
		extendedFieldMap.put(OLDEST_LOANDISBURSED_DT, oldDisbursedDate);

		extendedFieldMap.put(PRODUCT_INDEX, getPincodeGroupId(pincode));
		return extendedFieldMap;
	}

	/**
	 * Method for calculate paymentHistory details.
	 * 
	 * @param extendedFieldMap
	 * @param paymentList
	 */
	private void setPaymentHistoryDetails(Map<String, Object> extendedFieldMap, List<String> paymentList) {
		List<PaymentHistory> paymentHistories = preparePaymentHistory(paymentList);
		Collections.sort(paymentHistories, new PaymentHistoryComparator());
		int zeroCount = 0;
		int crossCount = 0;
		for (PaymentHistory paymentHistory : paymentHistories) {
			if (paymentHistory.getPaymentDate() != null) {
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
		//Difference in Months since most recent occurrence of DPD>=30 or (SUB, DBT, LOS, SMA) and Loan App Date.
		Date startDate = null;
		for (PaymentHistory paymentHistory : paymentHistories) {
			try {
				long dpd = Long.parseLong(paymentHistory.getDpd());
				if (dpd >= 30 && paymentHistory.getPaymentDate() != null) {
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

	/**
	 * Method for prepare the paymentHistory Response Object.
	 * 
	 * @param paymentList
	 * @return
	 */
	private List<PaymentHistory> preparePaymentHistory(List<String> paymentList) {
		List<PaymentHistory> paymentHistoryList = new ArrayList<PaymentHistory>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MMM:yyyy");
		for (String payment : paymentList) {
			String[] block = payment.split("\\|");
			for (String field : block) {
				PaymentHistory paymentHistory = new PaymentHistory();
				Date paymentDate = null;
				try {
					paymentDate = dateFormat.parse("01:" + field.substring(0, field.indexOf(",")));
				} catch (ParseException e) {
					//In case of invalid Date format
					logger.error("Exception: ", e);
				}
				paymentHistory.setPaymentDate(paymentDate);
				paymentHistory.setDpd(field.substring(field.indexOf(",") + 1, field.indexOf("/")));
				paymentHistory.setType(field.substring(field.indexOf("/") + 1, field.length()));
				paymentHistoryList.add(paymentHistory);
			}
		}
		return paymentHistoryList;
	}

	/**
	 * 
	 * This Comparator class is used to sort the PaymentHistory Data based on their PaymentDate, Sorts from Latest Date
	 * to Oldest Date.
	 */
	public class PaymentHistoryComparator implements Comparator<PaymentHistory> {
		@Override
		public int compare(PaymentHistory arg0, PaymentHistory arg1) {

			return ObjectUtils.compare(arg1.getPaymentDate(), arg0.getPaymentDate());
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
	 * @param reqSentOn
	 */
	private void doInterfaceLogging(String reference, String serviceUrl, String requets, String response,
			String errorCode, String errorDesc, Timestamp reqSentOn) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);

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
	 * @param reqSentOn
	 */
	private void doExceptioLogging(String reference, String serviceUrl, String requets, String response,
			String errorDesc, Timestamp reqSentOn) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);

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
