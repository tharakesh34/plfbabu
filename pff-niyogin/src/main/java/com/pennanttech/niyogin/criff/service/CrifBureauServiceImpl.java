package com.pennanttech.niyogin.criff.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.pennanttech.niyogin.utility.ExperianUtility;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.CriffBureauService;
import com.pennanttech.pff.external.dao.NiyoginDAOImpl;
import com.pennanttech.pff.external.service.NiyoginService;

public class CrifBureauServiceImpl extends NiyoginService implements CriffBureauService {

	private static final Logger	logger				= Logger.getLogger(CrifBureauServiceImpl.class);
	private String				extConfigFileName	= "crifBureauConsumer";
	private String				consumerUrl;
	private String				commercialUrl;
	private JSONClient			client;
	private NiyoginDAOImpl		niyoginDAOImpl;

	private String				OLDEST_LOANDISBURSED_DATE="OLDESTLOANDISBUR";
	private String				NO_PREVIOUS_LOANS_AS_OF_APPLICATION_DATE="NOPREVIOUSLOANS";
	private String				IS_APPLICANT_90_PLUS_DPD_IN_LAST_SIX_MONTHS="ISAPPLICANT90DP";
	private String				IS_APPLICANT_SUBSTANDARD_IN_LAST_SIX_MONTHS="ISAPPLICANTSUBST";
	private String				IS_APPLICANT_REPORTED_AS_LOSS_IN_LAST_SIX_MONTHS="ISAPPLICANTREPOR";
	private String				IS_APPLICANT_DOUBTFUL_IN_LAST_SIX_MONTHS="ISAPPLICANTDOUBT";
	private String				IS_APPLICANT_MENTIONED_AS_SMA="ISAPPMENTSMA";
	private String				LAST_UPDATE_DATE_IN_BUREAU="LASTUPDATEDATE";
	private String				NOT_ENOUGH_INFO="NOTENOUGHINFO";
	private String				MAX_PER_OF_AMT_REPAID_ACROSS_ALL_ACTIVE_SECURED_LOANS="MAXPEROFAMTREPAID";
	private String				SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS="SUMOFDISBURSEDAMT";
	private String				MAX_DISBURSED_AMT_ACROSS_ALL_UNSECURED_LOANS_IN_THE_LAST_12_MONTHS="MAXIMUMDISBURSED";
	private String				MIN_PER_OF_AMT_REPAID_ACROSS_ALL_UNSECURE_LOANS="MINIMUMPEROFAMT";
	private String				MONTHS_SINCE_30_PLUS_DPD_IN_THE_LAST_12_MONTHS="MNTHSIN30DPDINALAS";
	private String				NUMBER_OF_BUSINESS_LOANS_OPENED_IN_LAST_6_MONTHS="NOOFBUSILOANS";
	private String				RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS="RATIOOFOVRDUEDIS";
	private String				COMBINATION_OF_PREVIOUS_LOANS_TAKEN="AMBOFPRVSLOANS";
	private String				PRODUCT_INDEX="PROINDEXDETAILSHT";

	private Date				appDate				= getAppDate();
	private String				pincode				= null;

	private String				errorDesc			= null;
	private String				jsonResponse		= null;
	private Timestamp			reqSentOn			= null;
	private String				status				= InterfaceConstants.STATUS_SUCCESS;
	private String 				errorCode			= InterfaceConstants.INTFACE_ERROR_CD;
	private Object				requestObject       = null;
	private String				serviceUrl       	= null;

	@Override
	public AuditHeader executeCriffBureau(AuditHeader auditHeader) throws InterfaceException, ParseException {
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
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		try {
			if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),
					InterfaceConstants.PFF_CUSTCTG_SME)) {
				CriffBureauCommercial commercial = prepareCommercialRequestObj(customerDetails);
				serviceUrl = commercialUrl;
				jsonResponse = client.post(serviceUrl, commercial);
				//jsonResponse = getResponse();
				extConfigFileName = "crifBureauCommercial";
				requestObject = commercial;
			} else if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),
					InterfaceConstants.PFF_CUSTCTG_INDIV)) {
				CriffBureauConsumer consumer = prepareConsumerRequestObj(customerDetails);
				serviceUrl = consumerUrl;
				jsonResponse = client.post(serviceUrl, consumer);
				extConfigFileName = "crifBureauConsumer";
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
		} finally {
			requestObject = null;
		}

		//for Straight forwardFields It works
		Map<String, Object> extendedFieldMap = getExtendedMapValues(jsonResponse, extConfigFileName);

		// error validation on Response status
		if (extendedFieldMap.get("ERRORCODE") != null) {
			errorCode = Objects.toString(extendedFieldMap.get("ERRORCODE"));
			errorDesc = Objects.toString(extendedFieldMap.get("ERRORMESSAGE"));
			throw new InterfaceException(errorCode, errorDesc);
		} else {
			extendedFieldMap.remove("ERRORCODE");
			extendedFieldMap.remove("ERRORMESSAGE");
		}

		if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(), InterfaceConstants.PFF_CUSTCTG_SME)) {
			Object responseObj = client.getResponseObject(jsonResponse, CriffCommercialResponse.class, false);
			CriffCommercialResponse commercialResponse = (CriffCommercialResponse) responseObj;
			extendedFieldMap = prepareCommercialExtendedMap(commercialResponse, extendedFieldMap);
		} else if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),
				InterfaceConstants.PFF_CUSTCTG_INDIV)) {
			Object responseObj = client.getResponseObject(jsonResponse, CRIFConsumerResponse.class, false);
			CRIFConsumerResponse consumerResponse = (CRIFConsumerResponse) responseObj;
			extendedFieldMap = prepareConsumerExtendedMap(consumerResponse, extendedFieldMap);
		}
		return extendedFieldMap;
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
			BigDecimal disbursAmtSixMnths =  BigDecimal.ZERO;
			int noBusLoanOpened = 0;
			Collections.sort(tradlineList, new SanctionDareComparator());
			//for oldest loan disbursed date
			Date disbursedDate = tradlineList.get(tradlineList.size() - 1).getSanctionDate();
			extendedFieldMap.put(OLDEST_LOANDISBURSED_DATE, disbursedDate);

			List<String> paymentList = new ArrayList<>();
			BigDecimal closedloanDisbursAmt =  BigDecimal.ZERO;

			for (TradeLine tradeline : tradlineList) {
				if (!tradeline.getCreditFacilityStatus().equalsIgnoreCase("Closed")) {
					extendedFieldMap.put(NO_PREVIOUS_LOANS_AS_OF_APPLICATION_DATE, true);
				}

				paymentList.add(tradeline.getPaymentHistory());

				//for sum of disbursed amount of all closed loans
				if (tradeline.getAccountStatus().equalsIgnoreCase("Closed")) {
					closedloanDisbursAmt = closedloanDisbursAmt.add(tradeline.getDisbursedAmount());
				}

				if (getMonthsBetween(getAppDate(), tradeline.getSanctionDate()) <= 6) {
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
			if(overDueAmt.compareTo(BigDecimal.ZERO) > 0) {
				ratioOfOverdue = overDueAmt.divide(disBursedAmt);
			}
			extendedFieldMap.put(RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS, ratioOfOverdue);

			//for number of business loans opened in last 6 months
			extendedFieldMap.put(NUMBER_OF_BUSINESS_LOANS_OPENED_IN_LAST_6_MONTHS, noBusLoanOpened);

			// calculte payment history details
			setPaymentHistoryDetails(extendedFieldMap, paymentList);

			//for last update date in Bureau
			Collections.sort(tradlineList, new LastReportedDateComparator());
			Date lastUpdatedDate = tradlineList.get(0).getLastReportedDate();
			//long months = getMonthsBetween(lastUpdatedDate, appDate);
			extendedFieldMap.put(LAST_UPDATE_DATE_IN_BUREAU, lastUpdatedDate);
		}

		extendedFieldMap.put(PRODUCT_INDEX, niyoginDAOImpl.getPincodeGroupId(pincode));
		return extendedFieldMap;
	}

	private String getResponse() {
		String response = "{\"statusCode\": 200,\"message\": \"Crif Bureau commercial report extracted\",\"data\": [  { \"ACCOUNT-NO\": \"XXXX\", \"CREDIT-GRANTOR\": \"XXXX\", \"LAST-REPORTED-DATE\": \"31-03-2012\", \"ASSET-CLASSIFICATION\": \"Sub Standard\", \"CURRENT-BALANCE\": \"786300000\", \"DPD\": \"0\", \"CREDIT-FACILITY-TYPE\": \"TL(Long)\", \"CREDIT-FACILITY-STATUS\": \"ACTIVE\", \"SANCTIONED-AMOUNT\": \"78,63,00,000\", \"SANCTION-DATE\": \"29-04-2017\", \"DRAWING-POWER\": \"787636231\", \"DISBURSED-AMOUNT\": \"25000\", \"OVERDUE-AMOUNT\": \"0\", \"ISSUED-CURRENCY\": \"INR\", \"SUIT-FILED-AND-WILFUL-DEFAULTS\": {\"SUIT-FILED-STATUS\": \"Not a Suit Filed Case\",\"DATE-OF-SUIT\": \"29-12-2011\",\"WILFUL-DEFAULTER\": \"Not Wilful Defaulter\",\"SUIT-AMOUNT\": \"0\",\"WILFUL-DEFAULT-AS-ON\": \"29-02-2012\" }, \"BORROWER-NAME\": \"ANKUR DRUGS AND PHARMA LIMITED\", \"PAYMENT-HISTORY\": \"Mar:2012,XXX/SUB|Feb:2012,DDD/DDD|Jan:2012,DDD/DDD|\", \"ACCOUNT-STATUS\": \"S04\", \"LENDER-TYPE\": \"NAB\", \"WRITEOFF-AMOUNT\": \"0\", \"CREDIT-FACILITY-GROUP\": \"TL\", \"DERIVED-ACCOUNT-STATUS\": \"S05\"  },  { \"ACCOUNT-NO\": \"XXXX\", \"CREDIT-GRANTOR\": \"XXXX\", \"LAST-REPORTED-DATE\": \"31-03-2013\", \"ASSET-CLASSIFICATION\": \"Loss\", \"CURRENT-BALANCE\": \"533697247\", \"DPD\": \"0\", \"CREDIT-FACILITY-TYPE\": \"TL(Long)\", \"CREDIT-FACILITY-STATUS\": \"ACTIVE\", \"SANCTIONED-AMOUNT\": \"533700000\", \"SANCTION-DATE\": \"28-05-2017\", \"DRAWING-POWER\": \"533700000\", \"DISBURSED-AMOUNT\": \"25000\", \"OVERDUE-AMOUNT\": \"0\", \"ISSUED-CURRENCY\": \"INR\", \"SUIT-FILED-AND-WILFUL-DEFAULTS\": {\"WILFUL-DEFAULTER\": \"Not Wilful Defaulter\",\"SUIT-AMOUNT\": \"0\" }, \"BORROWER-NAME\": \"ANKUR DRUGS AND PHARMA LIMITED\", \"PAYMENT-HISTORY\": \"Mar:2017,92/LOS|Feb:2017,91/LOS|Jan:2017,45/LOS|APR:2017,58/DBT|Nov:2016,XXX/LOS|JUL:2017,96/SUP|JUN:2017,97/DBT|Aug:2016,XXX/SMA|Jul:2017,92/LOS|Jun:2017,96/LOS|May:2012,XXX/LOS|Apr:2017,XXX/SUP|Mar:2017,76/SUB|Feb:2012,xxx/XXX|Jan:2012,xxx/XXX|\", \"ACCOUNT-STATUS\": \"S04\", \"LENDER-TYPE\": \"NAB\", \"WRITEOFF-AMOUNT\": \"0\", \"CREDIT-FACILITY-GROUP\": \"TL\", \"DERIVED-ACCOUNT-STATUS\": \"S05\"  },  { \"ACCOUNT-NO\": \"XXXX\", \"CREDIT-GRANTOR\": \"XXXX\", \"LAST-REPORTED-DATE\": \"30-04-2013\", \"ASSET-CLASSIFICATION\": \"Doubtful\", \"CURRENT-BALANCE\": \"371450644\", \"DPD\": \"0\", \"CREDIT-FACILITY-TYPE\": \"TL(Long)\", \"CREDIT-FACILITY-STATUS\": \"ACTIVE\", \"SANCTIONED-AMOUNT\": \"364700000\", \"SANCTION-DATE\": \"18-07-2017\", \"DRAWING-POWER\": \"364700000\", \"DISBURSED-AMOUNT\": \"25000\", \"OVERDUE-AMOUNT\": \"0\", \"ISSUED-CURRENCY\": \"INR\", \"SUIT-FILED-AND-WILFUL-DEFAULTS\": {\"WILFUL-DEFAULTER\": \"Not Wilful Defaulter\",\"SUIT-AMOUNT\": \"0\" }, \"BORROWER-NAME\": \"ANKUR DRUGS AND PHARMA LTD\", \"PAYMENT-HISTORY\": \"Mar:2017,92/LOS|Feb:2017,91/LOS|Jan:2017,45/LOS|Dec:2016,58/LOS|Nov:2016,XXX/LOS|Oct:2016,XXX/SUP|Sep:2016,000/DBT|Aug:2016,XXX/SMA|Jul:2017,92/LOS|Jun:2017,96/LOS|May:2012,XXX/LOS|Apr:2017,XXX/SUP|Mar:2017,76/SUB|Feb:2012,xxx/XXX|Jan:2012,xxx/XXX|\", \"ACCOUNT-STATUS\": \"Closed\", \"LENDER-TYPE\": \"NAB\", \"WRITEOFF-AMOUNT\": \"0\", \"CREDIT-FACILITY-GROUP\": \"TL\", \"DERIVED-ACCOUNT-STATUS\": \"S05\"  },  { \"ACCOUNT-NO\": \"XXXX\", \"CREDIT-GRANTOR\": \"XXXX\", \"LAST-REPORTED-DATE\": \"31-03-2012\", \"ASSET-CLASSIFICATION\": \"Sub Standard\", \"CURRENT-BALANCE\": \"320772818\", \"DPD\": \"0\", \"CREDIT-FACILITY-TYPE\": \"TL(Long)\", \"CREDIT-FACILITY-STATUS\": \"ACTIVE\", \"SANCTIONED-AMOUNT\": \"327400000\", \"SANCTION-DATE\": \"29-12-2011\", \"DRAWING-POWER\": \"337723071\", \"DISBURSED-AMOUNT\": \"25000\", \"OVERDUE-AMOUNT\": \"0\", \"ISSUED-CURRENCY\": \"INR\", \"SUIT-FILED-AND-WILFUL-DEFAULTS\": {\"SUIT-FILED-STATUS\": \"Not a Suit Filed Case\",\"DATE-OF-SUIT\": \"29-12-2011\",\"WILFUL-DEFAULTER\": \"Not Wilful Defaulter\",\"SUIT-AMOUNT\": \"0\",\"WILFUL-DEFAULT-AS-ON\": \"29-02-2012\" }, \"BORROWER-NAME\": \"ANKUR DRUGS AND PHARMA LIMITED\", \"PAYMENT-HISTORY\": \"Mar:2012,XXX/SUB|Feb:2012,DDD/DDD|Jan:2012,DDD/DDD|\", \"ACCOUNT-STATUS\": \"S04\", \"LENDER-TYPE\": \"NAB\", \"WRITEOFF-AMOUNT\": \"0.0\", \"CREDIT-FACILITY-GROUP\": \"TL\", \"DERIVED-ACCOUNT-STATUS\": \"S05\"  },  { \"ACCOUNT-NO\": \"XXXX\", \"CREDIT-GRANTOR\": \"XXXX\", \"LAST-REPORTED-DATE\": \"30-09-2011\", \"ASSET-CLASSIFICATION\": \"Standard\", \"CURRENT-BALANCE\": \"211522845\", \"DPD\": \"0\", \"CREDIT-FACILITY-TYPE\": \"TL(Long)\", \"CREDIT-FACILITY-STATUS\": \"ACTIVE\", \"SANCTIONED-AMOUNT\": \"350000000\", \"SANCTION-DATE\": \"30-07-2007\", \"DRAWING-POWER\": \"147628350\", \"DISBURSED-AMOUNT\": \"25000\", \"OVERDUE-AMOUNT\": \"0\", \"ISSUED-CURRENCY\": \"INR\", \"SUIT-FILED-AND-WILFUL-DEFAULTS\": {\"SUIT-FILED-STATUS\": \"Not a Suit Filed Case\",\"WILFUL-DEFAULTER\": \"Not Wilful Defaulter\",\"SUIT-AMOUNT\": \"0\" }, \"BORROWER-NAME\": \"ANKUR DRUGS AND PHARMA LIMITED\", \"PAYMENT-HISTORY\": \"Sep:2011,XXX/STD|Aug:2011,DDD/DDD|Jul:2011,DDD/DDD|Jun:2011,XXX/STD|May:2011,DDD/DDD|Apr:2011,DDD/DDD|Mar:2011,XXX/STD|Feb:2011,DDD/DDD|Jan:2011,DDD/DDD|Dec:2010,XXX/STD|Nov:2010,DDD/DDD|Oct:2010,DDD/DDD|Sep:2010,XXX/STD|Aug:2010,DDD/DDD|Jul:2010,DDD/DDD|Jun:2010,XXX/STD|May:2010,DDD/DDD|Apr:2010,DDD/DDD|Mar:2010,XXX/STD|Feb:2010,DDD/DDD|Jan:2010,DDD/DDD|Dec:2009,XXX/STD|Nov:2009,DDD/DDD|Oct:2009,DDD/DDD|Sep:2009,XXX/STD|Aug:2009,DDD/DDD|Jul:2009,DDD/DDD|Jun:2009,XXX/STD|May:2009,DDD/DDD|Apr:2009,DDD/DDD|Mar:2009,XXX/STD|Feb:2009,DDD/DDD|Jan:2009,DDD/DDD|Dec:2008,XXX/STD|Nov:2008,DDD/DDD|Oct:2008,DDD/DDD\", \"ACCOUNT-STATUS\": \"Closed\", \"LENDER-TYPE\": \"NAB\", \"WRITEOFF-AMOUNT\": \"0.0\", \"CREDIT-FACILITY-GROUP\": \"TL\", \"DERIVED-ACCOUNT-STATUS\": \"S04\"  },  { \"ACCOUNT-NO\": \"XXXX\", \"CREDIT-GRANTOR\": \"XXXX\", \"LAST-REPORTED-DATE\": \"31-03-2013\", \"ASSET-CLASSIFICATION\": \"Doubtful\", \"CURRENT-BALANCE\": \"191087930\", \"DPD\": \"0\", \"CREDIT-FACILITY-TYPE\": \"TL(Medium)\", \"CREDIT-FACILITY-STATUS\": \"ACTIVE\", \"SANCTIONED-AMOUNT\": \"191100000\", \"SANCTION-DATE\": \"10-10-2011\", \"DRAWING-POWER\": \"191100000\", \"ISSUED-CURRENCY\": \"INR\", \"SUIT-FILED-AND-WILFUL-DEFAULTS\": {\"WILFUL-DEFAULTER\": \"Not Wilful Defaulter\",\"SUIT-AMOUNT\": \"0\" }, \"BORROWER-NAME\": \"ANKUR DRUGS AND PHARMA LTD\", \"PAYMENT-HISTORY\": \"Mar:2013,XXX/DBT|Feb:2013,XXX/DBT|Jan:2013,XXX/DBT|Dec:2012,XXX/DBT|Nov:2012,XXX/DBT|Oct:2012,XXX/DBT|Sep:2012,XXX/DBT|Aug:2012,XXX/SUB|Jul:2012,XXX/SUB|Jun:2012,XXX/SUB|May:2012,XXX/SUB|Apr:2012,XXX/SMA|Mar:2012,XXX/SMA|Feb:2012,XXX/SMA|Jan:2012,XXX/SMA|Dec:2011,XXX/SMA|Nov:2011,XXX/STD|\", \"ACCOUNT-STATUS\": \"S04\", \"LENDER-TYPE\": \"NAB\", \"WRITEOFF-AMOUNT\": \"0\", \"CREDIT-FACILITY-GROUP\": \"TL\", \"DERIVED-ACCOUNT-STATUS\": \"S05\"  }] } ";
		//String response = "{\"statusCode\": 200,\"message\": \"Crif Bureau commercial report extracted\",\"data\": [  { \"ACCOUNT-NO\": \"XXXX\", \"CREDIT-GRANTOR\": \"XXXX\", \"LAST-REPORTED-DATE\": \"31-03-2012\", \"ASSET-CLASSIFICATION\": \"Sub Standard\", \"CURRENT-BALANCE\": \"786300000\", \"DPD\": \"0\", \"CREDIT-FACILITY-TYPE\": \"TL(Long)\", \"CREDIT-FACILITY-STATUS\": \"ACTIVE\", \"SANCTIONED-AMOUNT\": \"78,63,00,000\", \"SANCTION-DATE\": \"29-12-2011\", \"DRAWING-POWER\": \"787636231\", \"DISBURSED-AMOUNT\": \"25000\", \"OVERDUE-AMOUNT\": \"10\", \"ISSUED-CURRENCY\": \"INR\", \"SUIT-FILED-AND-WILFUL-DEFAULTS\": {\"SUIT-FILED-STATUS\": \"Not a Suit Filed Case\",\"DATE-OF-SUIT\": \"29-12-2011\",\"WILFUL-DEFAULTER\": \"Not Wilful Defaulter\",\"SUIT-AMOUNT\": \"0\",\"WILFUL-DEFAULT-AS-ON\": \"29-02-2012\" }, \"BORROWER-NAME\": \"ANKUR DRUGS AND PHARMA LIMITED\", \"PAYMENT-HISTORY\": \"Mar:2012,XXX/SUB|Feb:2012,DDD/DDD|Jan:2012,DDD/DDD|\", \"ACCOUNT-STATUS\": \"S04\", \"LENDER-TYPE\": \"NAB\", \"WRITEOFF-AMOUNT\": \"0\", \"CREDIT-FACILITY-GROUP\": \"TL\", \"DERIVED-ACCOUNT-STATUS\": \"S05\"  },  { \"ACCOUNT-NO\": \"XXXX\", \"CREDIT-GRANTOR\": \"XXXX\", \"LAST-REPORTED-DATE\": \"31-03-2013\", \"ASSET-CLASSIFICATION\": \"Loss\", \"CURRENT-BALANCE\": \"533697247\", \"DPD\": \"0\", \"CREDIT-FACILITY-TYPE\": \"TL(Long)\", \"CREDIT-FACILITY-STATUS\": \"ACTIVE\", \"SANCTIONED-AMOUNT\": \"533700000\", \"SANCTION-DATE\": \"28-12-2011\", \"DRAWING-POWER\": \"533700000\", \"DISBURSED-AMOUNT\": \"25000\", \"OVERDUE-AMOUNT\": \"0\", \"ISSUED-CURRENCY\": \"INR\", \"SUIT-FILED-AND-WILFUL-DEFAULTS\": {\"WILFUL-DEFAULTER\": \"Not Wilful Defaulter\",\"SUIT-AMOUNT\": \"0\" }, \"BORROWER-NAME\": \"ANKUR DRUGS AND PHARMA LIMITED\", \"PAYMENT-HISTORY\": \"Mar:2013,XXX/LOS|Feb:2013,XXX/LOS|Jan:2013,XXX/LOS|Dec:2012,XXX/LOS|Nov:2012,XXX/LOS|Oct:2012,XXX/LOS|Sep:2012,XXX/LOS|Aug:2012,XXX/LOS|Jul:2012,XXX/LOS|Jun:2012,XXX/LOS|May:2012,XXX/LOS|Apr:2012,XXX/DBT|Mar:2012,xxx/XXX|Feb:2012,xxx/XXX|Jan:2012,xxx/XXX|\", \"ACCOUNT-STATUS\": \"S04\", \"LENDER-TYPE\": \"NAB\", \"WRITEOFF-AMOUNT\": \"0\", \"CREDIT-FACILITY-GROUP\": \"TL\", \"DERIVED-ACCOUNT-STATUS\": \"S05\"  },  { \"ACCOUNT-NO\": \"XXXX\", \"CREDIT-GRANTOR\": \"XXXX\", \"LAST-REPORTED-DATE\": \"30-04-2013\", \"ASSET-CLASSIFICATION\": \"Doubtful\", \"CURRENT-BALANCE\": \"371450644\", \"DPD\": \"0\", \"CREDIT-FACILITY-TYPE\": \"TL(Long)\", \"CREDIT-FACILITY-STATUS\": \"ACTIVE\", \"SANCTIONED-AMOUNT\": \"364700000\", \"SANCTION-DATE\": \"18-10-2011\", \"DRAWING-POWER\": \"364700000\", \"DISBURSED-AMOUNT\": \"25000\", \"OVERDUE-AMOUNT\": \"0\", \"ISSUED-CURRENCY\": \"INR\", \"SUIT-FILED-AND-WILFUL-DEFAULTS\": {\"WILFUL-DEFAULTER\": \"Not Wilful Defaulter\",\"SUIT-AMOUNT\": \"0\" }, \"BORROWER-NAME\": \"ANKUR DRUGS AND PHARMA LTD\", \"PAYMENT-HISTORY\": \"Apr:2013,XXX/DBT|Mar:2013,XXX/DBT|Feb:2013,XXX/DBT|Jan:2013,XXX/DBT|Dec:2012,XXX/SUB|Nov:2012,XXX/SUB|Oct:2012,XXX/SUB|Sep:2012,XXX/SUB|Aug:2012,XXX/SUB|Jul:2012,XXX/SUB|Jun:2012,XXX/STD|May:2012,XXX/STD|Apr:2012,XXX/STD|Mar:2012,xxx/XXX|Feb:2012,xxx/XXX|Jan:2012,xxx/XXX|Dec:2011,xxx/XXX|Nov:2011,xxx/XXX|\", \"ACCOUNT-STATUS\": \"S04\", \"LENDER-TYPE\": \"NAB\", \"WRITEOFF-AMOUNT\": \"0\", \"CREDIT-FACILITY-GROUP\": \"TL\", \"DERIVED-ACCOUNT-STATUS\": \"S05\"  },  { \"ACCOUNT-NO\": \"XXXX\", \"CREDIT-GRANTOR\": \"XXXX\", \"LAST-REPORTED-DATE\": \"31-03-2012\", \"ASSET-CLASSIFICATION\": \"Sub Standard\", \"CURRENT-BALANCE\": \"320772818\", \"DPD\": \"0\", \"CREDIT-FACILITY-TYPE\": \"TL(Long)\", \"CREDIT-FACILITY-STATUS\": \"ACTIVE\", \"SANCTIONED-AMOUNT\": \"327400000\", \"SANCTION-DATE\": \"29-12-2011\", \"DRAWING-POWER\": \"337723071\", \"DISBURSED-AMOUNT\": \"25000\", \"OVERDUE-AMOUNT\": \"0\", \"ISSUED-CURRENCY\": \"INR\", \"SUIT-FILED-AND-WILFUL-DEFAULTS\": {\"SUIT-FILED-STATUS\": \"Not a Suit Filed Case\",\"DATE-OF-SUIT\": \"29-12-2011\",\"WILFUL-DEFAULTER\": \"Not Wilful Defaulter\",\"SUIT-AMOUNT\": \"0\",\"WILFUL-DEFAULT-AS-ON\": \"29-02-2012\" }, \"BORROWER-NAME\": \"ANKUR DRUGS AND PHARMA LIMITED\", \"PAYMENT-HISTORY\": \"Mar:2012,XXX/SUB|Feb:2012,DDD/DDD|Jan:2012,DDD/DDD|\", \"ACCOUNT-STATUS\": \"S04\", \"LENDER-TYPE\": \"NAB\", \"WRITEOFF-AMOUNT\": \"0.0\", \"CREDIT-FACILITY-GROUP\": \"TL\", \"DERIVED-ACCOUNT-STATUS\": \"S05\"  },  { \"ACCOUNT-NO\": \"XXXX\", \"CREDIT-GRANTOR\": \"XXXX\", \"LAST-REPORTED-DATE\": \"30-09-2011\", \"ASSET-CLASSIFICATION\": \"Standard\", \"CURRENT-BALANCE\": \"211522845\", \"DPD\": \"0\", \"CREDIT-FACILITY-TYPE\": \"TL(Long)\", \"CREDIT-FACILITY-STATUS\": \"ACTIVE\", \"SANCTIONED-AMOUNT\": \"350000000\", \"SANCTION-DATE\": \"30-07-2007\", \"DRAWING-POWER\": \"147628350\", \"DISBURSED-AMOUNT\": \"25000\", \"OVERDUE-AMOUNT\": \"0\", \"ISSUED-CURRENCY\": \"INR\", \"SUIT-FILED-AND-WILFUL-DEFAULTS\": {\"SUIT-FILED-STATUS\": \"Not a Suit Filed Case\",\"WILFUL-DEFAULTER\": \"Not Wilful Defaulter\",\"SUIT-AMOUNT\": \"0\" }, \"BORROWER-NAME\": \"ANKUR DRUGS AND PHARMA LIMITED\", \"PAYMENT-HISTORY\": \"Sep:2011,XXX/STD|Aug:2011,DDD/DDD|Jul:2011,DDD/DDD|Jun:2011,XXX/STD|May:2011,DDD/DDD|Apr:2011,DDD/DDD|Mar:2011,XXX/STD|Feb:2011,DDD/DDD|Jan:2011,DDD/DDD|Dec:2010,XXX/STD|Nov:2010,DDD/DDD|Oct:2010,DDD/DDD|Sep:2010,XXX/STD|Aug:2010,DDD/DDD|Jul:2010,DDD/DDD|Jun:2010,XXX/STD|May:2010,DDD/DDD|Apr:2010,DDD/DDD|Mar:2010,XXX/STD|Feb:2010,DDD/DDD|Jan:2010,DDD/DDD|Dec:2009,XXX/STD|Nov:2009,DDD/DDD|Oct:2009,DDD/DDD|Sep:2009,XXX/STD|Aug:2009,DDD/DDD|Jul:2009,DDD/DDD|Jun:2009,XXX/STD|May:2009,DDD/DDD|Apr:2009,DDD/DDD|Mar:2009,XXX/STD|Feb:2009,DDD/DDD|Jan:2009,DDD/DDD|Dec:2008,XXX/STD|Nov:2008,DDD/DDD|Oct:2008,DDD/DDD\", \"ACCOUNT-STATUS\": \"S04\", \"LENDER-TYPE\": \"NAB\", \"WRITEOFF-AMOUNT\": \"0.0\", \"CREDIT-FACILITY-GROUP\": \"TL\", \"DERIVED-ACCOUNT-STATUS\": \"S04\"  },  { \"ACCOUNT-NO\": \"XXXX\", \"CREDIT-GRANTOR\": \"XXXX\", \"LAST-REPORTED-DATE\": \"31-03-2013\", \"ASSET-CLASSIFICATION\": \"Doubtful\", \"CURRENT-BALANCE\": \"191087930\", \"DPD\": \"0\", \"CREDIT-FACILITY-TYPE\": \"TL(Medium)\", \"CREDIT-FACILITY-STATUS\": \"ACTIVE\", \"SANCTIONED-AMOUNT\": \"191100000\", \"SANCTION-DATE\": \"10-10-2011\", \"DRAWING-POWER\": \"191100000\", \"ISSUED-CURRENCY\": \"INR\", \"SUIT-FILED-AND-WILFUL-DEFAULTS\": {\"WILFUL-DEFAULTER\": \"Not Wilful Defaulter\",\"SUIT-AMOUNT\": \"0\" }, \"BORROWER-NAME\": \"ANKUR DRUGS AND PHARMA LTD\", \"PAYMENT-HISTORY\": \"Mar:2013,XXX/DBT|Feb:2013,XXX/DBT|Jan:2013,XXX/DBT|Dec:2012,XXX/DBT|Nov:2012,XXX/DBT|Oct:2012,XXX/DBT|Sep:2012,XXX/DBT|Aug:2012,XXX/SUB|Jul:2012,XXX/SUB|Jun:2012,XXX/SUB|May:2012,XXX/SUB|Apr:2012,XXX/SMA|Mar:2012,XXX/SMA|Feb:2012,XXX/SMA|Jan:2012,XXX/SMA|Dec:2011,XXX/SMA|Nov:2011,XXX/STD|\", \"ACCOUNT-STATUS\": \"S04\", \"LENDER-TYPE\": \"NAB\", \"WRITEOFF-AMOUNT\": \"0\", \"CREDIT-FACILITY-GROUP\": \"TL\", \"DERIVED-ACCOUNT-STATUS\": \"S05\"  }] } ";
		return response;
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
		commercial.setCompanyMobile(ExperianUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList(),
				InterfaceConstants.PHONE_TYPE_OFF));
		commercial.setCompanyPAN(commercial.getApplicant().getPan());
		commercial.setLegalEntity(customer.getLovDescCustTypeCodeName());

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
		applicant.setDob(formatDate(customer.getCustDOB(), "dd-MM-yyyy"));
		applicant.setGender(InterfaceConstants.PFF_GENDER_M);
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		applicant.setPan(ExperianUtility.getDocumentNumber(documentList, InterfaceConstants.DOC_TYPE_PAN));
		applicant.setMaritalStatus(InterfaceConstants.PFF_MARITAL_STATUS);
		applicant.setMobile(ExperianUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList(), 
				InterfaceConstants.PHONE_TYPE_PER));
		// personal address details
		applicant.setPersonalAddress(preparePersonalAddress(customerDetails.getAddressList()));
		return applicant;
	}

	private PersonalAddress preparePersonalAddress(List<CustomerAddres> addressList) {
		PersonalAddress personalAddress = new PersonalAddress();
		CustomerAddres address = ExperianUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_PER);

		City city = niyoginDAOImpl.getCityById(address.getCustAddrCountry(), address.getCustAddrProvince(), 
				address.getCustAddrCity(),"_AView");

		personalAddress.setHouseNo(address.getCustAddrHNbr());
		personalAddress.setLandmark(address.getCustAddrStreet());
		personalAddress.setCity(city.getPCCityName());
		personalAddress.setCountry(city.getLovDescPCCountryName());
		personalAddress.setPin(address.getCustAddrZIP());
		pincode = address.getCustAddrZIP();
		personalAddress.setState(city.getLovDescPCProvinceName());
		personalAddress.setCareOf(StringUtils.isNotBlank(address.getCustAddrLine3())?address.getCustAddrLine3():
			InterfaceConstants.DEFAULT_CAREOF);
		personalAddress.setDistrict(StringUtils.isNotBlank(address.getCustDistrict())?address.getCustDistrict():
			InterfaceConstants.DEFAULT_DIST);
		personalAddress.setSubDistrict(StringUtils.isNotBlank(address.getCustAddrLine4())?address.getCustAddrLine4():
			InterfaceConstants.DEFAULT_SUBDIST);
		return personalAddress;
	}

	private CompanyAddress prepareComapnyAddress(List<CustomerAddres> addressList) {
		CompanyAddress companyAddress = new CompanyAddress();
		CustomerAddres address = ExperianUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_OFF);

		City city = niyoginDAOImpl.getCityById(address.getCustAddrCountry(), address.getCustAddrProvince(), 
				address.getCustAddrCity(),"_AView");

		companyAddress.setAddress1(address.getCustAddrType()+","+address.getCustAddrHNbr()+","+address.getCustAddrStreet());
		companyAddress.setAddress2(address.getCustAddrType()+","+address.getCustAddrHNbr()+","+address.getCustAddrStreet());
		companyAddress.setAddress3(address.getCustAddrType()+","+address.getCustAddrHNbr()+","+address.getCustAddrStreet());
		companyAddress.setCity(city.getPCCityName());
		companyAddress.setCountry(city.getLovDescPCCountryName());
		companyAddress.setPin(address.getCustAddrZIP());
		companyAddress.setState(city.getLovDescPCProvinceName());
		companyAddress.setDistrict(StringUtils.isNotBlank(address.getCustDistrict())?address.getCustDistrict():
			InterfaceConstants.DEFAULT_DIST);
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
		extendedFieldMap.put(OLDEST_LOANDISBURSED_DATE, disbursedDate);

		List<String> paymentList = new ArrayList<>(1);
		BigDecimal maxPerOfAmtRepaidOnSL = BigDecimal.ZERO;
		BigDecimal closedloanDisbursAmt = BigDecimal.ZERO;
		BigDecimal maxDsbursmentAmt = BigDecimal.ZERO;
		BigDecimal overDueAmt = BigDecimal.ZERO;
		BigDecimal disBursedAmt = BigDecimal.ZERO;
		BigDecimal disbursAmtSixMnths =  BigDecimal.ZERO;
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
				extendedFieldMap.put(NO_PREVIOUS_LOANS_AS_OF_APPLICATION_DATE, true);
			}

			//for max amount repaid across all active secured_loans
			if (loanDetail.getSecurityDetails() != null && !loanDetail.getSecurityDetails().isEmpty()) {
				BigDecimal value = (loanDetail.getDisbursedAmt().subtract(loanDetail.getCurrentBal())).divide(loanDetail.getDisbursedAmt());
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
			if (getMonthsBetween(appDate, disbursmentDate) <= 12) {
				if (loanDetail.getSecurityDetails() != null && !loanDetail.getSecurityDetails().isEmpty()) {
					maxDsbursmentAmt = maxDsbursmentAmt.add(loanDetail.getDisbursedAmt());
				}
			}

			//for min per of amt repaid across all unsecure loans
			if (loanDetail.getSecurityDetails() != null && loanDetail.getSecurityDetails().isEmpty()) {
				BigDecimal value = (loanDetail.getDisbursedAmt().subtract(loanDetail.getCurrentBal())).divide(loanDetail.getDisbursedAmt());
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

			if (getMonthsBetween(getAppDate(), loanDetail.getDisbursedDate()) <= 6) {
				disbursAmtSixMnths = disbursAmtSixMnths.add(loanDetail.getDisbursedAmt());
				noBusLoanOpened++;
			}
		}

		//for  maxPerOfAmtRepaidOnSL
		maxPerOfAmtRepaidOnSL = maxPerOfAmtRepaidOnSL.multiply(new BigDecimal(100));
		extendedFieldMap.put(MAX_PER_OF_AMT_REPAID_ACROSS_ALL_ACTIVE_SECURED_LOANS, maxPerOfAmtRepaidOnSL);

		//Sum of disbursed Amount of all closed loans
		extendedFieldMap.put(SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS, closedloanDisbursAmt);

		//Maximum disbursed Amount across all unsecured loans in the last 12 months
		extendedFieldMap.put(MAX_DISBURSED_AMT_ACROSS_ALL_UNSECURED_LOANS_IN_THE_LAST_12_MONTHS, maxDsbursmentAmt);

		//for  minPerOfAmtRepaidOnSL
		minPerOfAmtRepaidOnSL = minPerOfAmtRepaidOnSL.multiply(new BigDecimal(100));
		extendedFieldMap.put(MIN_PER_OF_AMT_REPAID_ACROSS_ALL_UNSECURE_LOANS, minPerOfAmtRepaidOnSL);

		//for  comb of previous loan taken
		extendedFieldMap.put(COMBINATION_OF_PREVIOUS_LOANS_TAKEN, sb.toString());

		//Number of business loans opened in last 6 months
		extendedFieldMap.put(NUMBER_OF_BUSINESS_LOANS_OPENED_IN_LAST_6_MONTHS, noBusLoanOpened);

		//Ratio of Overdue and Disbursement amount for all loans
		BigDecimal ratioOfOverdue = BigDecimal.ZERO;
		if(overDueAmt.compareTo(BigDecimal.ZERO) > 0) {
			ratioOfOverdue = overDueAmt.divide(disBursedAmt);
		}
		extendedFieldMap.put(RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS, ratioOfOverdue);

		// calculte payment history details
		setPaymentHistoryDetails(extendedFieldMap, paymentList);

		//for last update date in Bureau
		Collections.sort(loanDetailsList, new InfoAsOnComparator());
		Date lastUpdatedDate = loanDetailsList.get(0).getInfoAsOn();
		long months = getMonthsBetween(lastUpdatedDate, appDate);
		if (months > 36) {
			extendedFieldMap.put(LAST_UPDATE_DATE_IN_BUREAU, lastUpdatedDate);
		}

		extendedFieldMap.put(PRODUCT_INDEX, niyoginDAOImpl.getPincodeGroupId(pincode));
		return extendedFieldMap;
	}

	private void setPaymentHistoryDetails(Map<String, Object> extendedFieldMap, List<String> paymentList) throws ParseException {
		List<PaymentHistory> paymentHistories = preparePaymentHistory(paymentList);
		Collections.sort(paymentHistories, new PaymentHistoryComparator());
		int zeroCount = 0;
		int crossCount = 0;
		for (PaymentHistory paymentHistory : paymentHistories) {
			if (getMonthsBetween(getAppDate(), paymentHistory.getPaymentDate()) <= 6) {
				try {
					if (Long.valueOf(paymentHistory.getDpd()) >= 90) {
						extendedFieldMap.put(IS_APPLICANT_90_PLUS_DPD_IN_LAST_SIX_MONTHS, true);
					}
				} catch (Exception e) {
					//In case of DPD = XXX
				}
				if (StringUtils.equals(paymentHistory.getType(), "SUB")) {
					extendedFieldMap.put(IS_APPLICANT_SUBSTANDARD_IN_LAST_SIX_MONTHS, true);
				}

				if (StringUtils.equals(paymentHistory.getType(), "LOS")) {
					extendedFieldMap.put(IS_APPLICANT_REPORTED_AS_LOSS_IN_LAST_SIX_MONTHS, true);

				}
				if (StringUtils.equals(paymentHistory.getType(), "DBT")) {
					extendedFieldMap.put(IS_APPLICANT_DOUBTFUL_IN_LAST_SIX_MONTHS, true);
				}
			}

			if (getMonthsBetween(getAppDate(), paymentHistory.getPaymentDate()) <= 12) {
				if (StringUtils.equalsIgnoreCase(paymentHistory.getDpd(), "000")) {
					zeroCount++;
				} else if (StringUtils.equalsIgnoreCase(paymentHistory.getDpd(), "XXX")) {
					crossCount++;
				}
			}

			if (StringUtils.equalsIgnoreCase(paymentHistory.getType(), "SMA")) {
				extendedFieldMap.put(IS_APPLICANT_MENTIONED_AS_SMA, true);
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
			long dpdMonths = getMonthsBetween(getAppDate(), startDate);
			extendedFieldMap.put(MONTHS_SINCE_30_PLUS_DPD_IN_THE_LAST_12_MONTHS, dpdMonths);
		}
	}

	private List<PaymentHistory> preparePaymentHistory(List<String> paymentList)
			throws ParseException {
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
	 * Count Number of days between Util Dates
	 * 
	 * @param date1
	 *            (Date)
	 * 
	 * @param date2
	 *            (Date)
	 * 
	 * @return int
	 */
	public static int getDaysBetween(java.util.Date date1, java.util.Date date2) {

		if (date1 == null || date2 == null) {
			return -1;
		}
		GregorianCalendar gc1 = convert(date1);
		GregorianCalendar gc2 = convert(date2);
		if (gc1.get(Calendar.YEAR) == gc2.get(Calendar.YEAR)) {
			return Math.abs(gc1.get(Calendar.DAY_OF_YEAR) - gc2.get(Calendar.DAY_OF_YEAR));
		}
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		long days = (time1 - time2) / (1000 * 60 * 60 * 24);

		return Math.abs((int) days);
	}

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

	public void setNiyoginDAOImpl(NiyoginDAOImpl niyoginDAOImpl) {
		this.niyoginDAOImpl = niyoginDAOImpl;
	}

	public void setClient(JSONClient client) {
		this.client = client;
	}
}
