package com.pennanttech.niyogin.crif.cosumer.service;

import java.io.PrintWriter;
import java.io.StringWriter;
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

import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.bureau.consumer.model.Address;
import com.pennanttech.niyogin.bureau.consumer.model.BureauConsumer;
import com.pennanttech.niyogin.bureau.consumer.model.PersonalDetails;
import com.pennanttech.niyogin.clients.JSONClient;
import com.pennanttech.niyogin.crif.cosumer.model.CRIFConsumerResponse;
import com.pennanttech.niyogin.crif.cosumer.model.LoanDetail;
import com.pennanttech.niyogin.crif.cosumer.model.LoanDetailsData;
import com.pennanttech.niyogin.crif.model.PaymentHistory;
import com.pennanttech.niyogin.utility.ExperianUtility;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.CrifConsumerService;
import com.pennanttech.pff.external.dao.NiyoginDAOImpl;
import com.pennanttech.pff.external.service.NiyoginService;

public class CrifConsumerServiceImpl extends NiyoginService implements CrifConsumerService {

	private static final Logger	logger				= Logger.getLogger(CrifConsumerServiceImpl.class);
	//TODO:
	private final String		extConfigFileName	= "crifBureauConsumer";
	private String				serviceUrl;
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
	private String				SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS="DISTURBMENTAMOUNT";
	private String				MAX_DISBURSED_AMT_ACROSS_ALL_UNSECURED_LOANS_IN_THE_LAST_12_MONTHS="MAXIMUMDISBURSED";
	private String				MIN_PER_OF_AMT_REPAID_ACROSS_ALL_UNSECURE_LOANS="MINIMUMPEROFAMT";
	private String				MONTHS_SINCE_30_PLUS_DPD_IN_THE_LAST_12_MONTHS="MNTHSIN30DPDINALAS";
	private String				NUMBER_OF_BUSINESS_LOANS_OPENED_IN_LAST_6_MONTHS="NOOFBUSILOANS";
	private String				RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS="RATIOOFOVRDUEDIS";
	private String				COMBINATION_OF_PREVIOUS_LOANS_TAKEN="AMBOFPRVSLOANS";
	private String				PRODUCT_INDEX="PROINDEXDETAILSHT";

	private Date				appDate				= getAppDate();
	private String				pincode				= null;
	
	private String				status				= "SUCCESS";
	private String				errorCode			= null;
	private String				errorDesc			= null;
	private String				jsonResponse		= null;
	private Timestamp			reqSentOn			= null;

	@Override
	public AuditHeader getCrifBureauConsumer(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		BureauConsumer consumerRequest = prepareRequestObj(financeDetail);
		Map<String, Object> validatedMap = null;
		Map<String, Object> extendedFieldMap = null;

		// logging fields Data
		reqSentOn = new Timestamp(System.currentTimeMillis());

		try {
			logger.debug("ServiceURL : " + serviceUrl);
			jsonResponse = client.post(serviceUrl, consumerRequest);
			Object responseObj = client.getResponseObject(jsonResponse, "dd-MM-YYYY", CRIFConsumerResponse.class,
					false);
			CRIFConsumerResponse consumerResponse = (CRIFConsumerResponse) responseObj;
			//for Straight forwardFields It works
			extendedFieldMap = getExtendedMapValues(jsonResponse, extConfigFileName);

			// error validation on Response status
			if (extendedFieldMap.get("ERRORCODE") != null) {
				errorCode = Objects.toString(extendedFieldMap.get("ERRORCODE"));
				errorDesc = Objects.toString(extendedFieldMap.get("ERRORMESSAGE"));
				throw new InterfaceException(errorCode, errorDesc);
			} else {
				extendedFieldMap.remove("ERRORCODE");
				extendedFieldMap.remove("ERRORMESSAGE");
			}

			//For caliculation Fields
			prepareExtendedFieldMap(consumerResponse, extendedFieldMap);

			//validate the map with configuration
			validatedMap = validateExtendedMapValues(extendedFieldMap);

			logger.info("Response : " + jsonResponse);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			status = "FAILED";
			errorCode = "9999";
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			errorDesc = writer.toString();
			doInterfaceLogging(consumerRequest, finReference);
			throw new InterfaceException("9999", e.getMessage());
		}
		prepareResponseObj(validatedMap, financeDetail);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private BureauConsumer prepareRequestObj(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();

		BureauConsumer bureauConsumer = new BureauConsumer();
		//TODO:
		String appId = financeDetail.getFinScheduleData().getFinanceMain().getApplicationNo();
		bureauConsumer.setStgUnqRefId(financeDetail.getFinReference());
		bureauConsumer.setApplicationId(appId);
		if (customerDetails.getAddressList() != null) {
			CustomerAddres customerAddres = ExperianUtility.getHighPriorityAddress(customerDetails.getAddressList(), 5);
			bureauConsumer.setAddress(prepareAddress(customerAddres));
		} else {
			bureauConsumer.setAddress(new Address());
		}
		PersonalDetails personalDetails = new PersonalDetails();
		personalDetails.setFirstName(customer.getCustFName());
		personalDetails.setLastName(customer.getCustLName());
		personalDetails.setDob(customer.getCustDOB());
		//TODO:
		personalDetails.setGender(customer.getCustGenderCode());

		if (customerDetails.getCustomerPhoneNumList() != null) {
			CustomerPhoneNumber custPhone;
			custPhone = ExperianUtility.getHighPriorityPhone(customerDetails.getCustomerPhoneNumList(), 5);
			if (custPhone != null) {
				personalDetails.setMobile(custPhone.getPhoneNumber());
			} else {
				personalDetails.setMobile("");
			}
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
		pincode = customerAddres.getCustAddrZIP();
		niyoginDAOImpl.getPincodeGroupId(pincode);
		return address;
	}

	//TODO:
	private Map<String, Object> prepareExtendedFieldMap(CRIFConsumerResponse consumerResponse,
			Map<String, Object> extendedFieldMap) {

		List<LoanDetail> loanDetailsList = new ArrayList<LoanDetail>();
		for (LoanDetailsData loanData : consumerResponse.getLoanDetailsData()) {
			loanDetailsList.add(loanData.getLoanDetail());
		}

		Collections.sort(loanDetailsList, new DisbursedDateComparator());

		////for oldest loan disbursed date
		Date disbursedDate = loanDetailsList.get(loanDetailsList.size() - 1).getDisbursedDate();
		extendedFieldMap.put(OLDEST_LOANDISBURSED_DATE, disbursedDate);

		String[] types = { "SUP", "STP", "LSS", "DBT", "SMA" };
		List<String> paymentList = null;
		long maxPerOfAmtRepaidOnSL = 0;
		long closedloanDisbursAmt = 0;
		long maxDsbursmentAmt = 0;
		//for minimum checking take first one and compare
		long minPerOfAmtRepaidOnSL = Long.parseLong(loanDetailsList.get(0).getDisbursedAmt());
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
				long disbursMentAmt = Long.parseLong(loanDetail.getDisbursedAmt());
				long currentBal = Long.parseLong(loanDetail.getCurrentBal());
				long value = (disbursMentAmt - currentBal) / disbursMentAmt;
				if (value > maxPerOfAmtRepaidOnSL) {
					maxPerOfAmtRepaidOnSL = value;
				}
			}
			//for sum of disbursed amount of all closed loans
			if (loanDetail.getAccountStatus().equalsIgnoreCase("Closed")) {
				long disBursAmt = Long.parseLong(loanDetail.getDisbursedAmt());
				closedloanDisbursAmt = closedloanDisbursAmt + disBursAmt;
			}

			//Maximum disbursed Amount across all unsecured loans in the last 12 months
			Date disbursmentDate = loanDetail.getDisbursedDate();
			if (getMonthsBetween(disbursmentDate, appDate) <= 12) {
				if (loanDetail.getSecurityDetails() != null && !loanDetail.getSecurityDetails().isEmpty()) {
					long amt = Long.parseLong(loanDetail.getDisbursedAmt());
					maxDsbursmentAmt = maxDsbursmentAmt + amt;
				}
			}

			//for min per of amt repaid across all unsecure loans
			if (loanDetail.getSecurityDetails() != null && loanDetail.getSecurityDetails().isEmpty()) {
				long disbursMentAmt = Long.parseLong(loanDetail.getDisbursedAmt());
				long currentBal = Long.parseLong(loanDetail.getCurrentBal());
				long value = (disbursMentAmt - currentBal) / disbursMentAmt;
				if (value < minPerOfAmtRepaidOnSL) {
					minPerOfAmtRepaidOnSL = value;
				}
			}

			//for DPD related Calculation
			if (Arrays.asList(types).contains(loanDetail.getCombinedPaymentHistory())) {
				if (paymentList == null) {
					paymentList = new ArrayList<String>();
				}
				paymentList.add(loanDetail.getCombinedPaymentHistory());
			}

			//for combination of previous loans taken
			if (Arrays.asList(zeroAccTypes).contains(loanDetail.getAcctType())) {
				sb.append("0");
			} else if (Arrays.asList(oneAccTypes).contains(loanDetail.getAcctType())) {
				sb.append("1");
			}

		}

		//for  maxPerOfAmtRepaidOnSL
		maxPerOfAmtRepaidOnSL = maxPerOfAmtRepaidOnSL * 100;
		extendedFieldMap.put(MAX_PER_OF_AMT_REPAID_ACROSS_ALL_ACTIVE_SECURED_LOANS, maxPerOfAmtRepaidOnSL);

		//for Sum of disbursed Amount of all closed loans
		extendedFieldMap.put(SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS, closedloanDisbursAmt);

		//Maximum disbursed Amount across all unsecured loans in the last 12 months
		extendedFieldMap.put(MAX_DISBURSED_AMT_ACROSS_ALL_UNSECURED_LOANS_IN_THE_LAST_12_MONTHS, maxDsbursmentAmt);

		//for  minPerOfAmtRepaidOnSL
		minPerOfAmtRepaidOnSL = minPerOfAmtRepaidOnSL * 100;
		extendedFieldMap.put(MIN_PER_OF_AMT_REPAID_ACROSS_ALL_UNSECURE_LOANS, minPerOfAmtRepaidOnSL);

		//for  comb of previous loan taken
		extendedFieldMap.put(COMBINATION_OF_PREVIOUS_LOANS_TAKEN, sb.toString());

		//for number of business loans opened in last 6 months
		long disbursAmtSixMnths = 0;
		for (LoanDetail loanDetail : loanDetailsList) {
			if (getMonthsBetween(loanDetail.getDisbursedDate(), getAppDate()) <= 6) {
				long amt = Long.parseLong(loanDetail.getDisbursedAmt());
				disbursAmtSixMnths = disbursAmtSixMnths + amt;
			} else {
				break;
			}
		}
		extendedFieldMap.put(NUMBER_OF_BUSINESS_LOANS_OPENED_IN_LAST_6_MONTHS, disbursAmtSixMnths);

		//TODO : loanApp date validation is required
		//Ratio of Overdue and Disbursement amount for all loans
		long overDueAmt = 0;
		long disBursedAmt = 0;
		for (LoanDetail loanDetail : loanDetailsList) {
			long disbursedValue = Long.valueOf(loanDetail.getDisbursedAmt());
			long overDueValue = Long.valueOf(loanDetail.getOverdueAmt());
			disBursedAmt = disBursedAmt + disbursedValue;
			overDueAmt = overDueAmt + overDueValue;
		}
		long gcd = gcd(overDueAmt, disBursedAmt);
		String ratio = overDueAmt / gcd + ":" + disBursedAmt / gcd;
		extendedFieldMap.put(RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS, ratio);

		Map<String, List<PaymentHistory>> paymentMap = preparePaymentHistory(paymentList, types);

		//for Is applicant 90+DPD in last six months
		if (paymentMap.get("SUP") != null) {
			for (PaymentHistory paymentHistory : paymentMap.get("SUP")) {
				long ddp = 0;
				try {
					ddp = Long.parseLong(paymentHistory.getDdp());
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
				if (ddp >= 90) {
					extendedFieldMap.put(IS_APPLICANT_90_PLUS_DPD_IN_LAST_SIX_MONTHS, true);
				}
			}
		}

		//for Is applicant Substandard in last six months
		if (paymentMap.get("STP") != null) {
			for (PaymentHistory paymentHistory : paymentMap.get("STP")) {
				long ddp = 0;
				try {
					ddp = Long.parseLong(paymentHistory.getDdp());
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
				if (ddp >= 90) {
					extendedFieldMap.put(IS_APPLICANT_SUBSTANDARD_IN_LAST_SIX_MONTHS, true);
				}
			}
		}

		//for Is applicant reported as Loss in last six months
		if (paymentMap.get("LSS") != null) {
			for (PaymentHistory paymentHistory : paymentMap.get("LSS")) {
				long ddp = 0;
				try {
					ddp = Long.parseLong(paymentHistory.getDdp());
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
				if (ddp >= 90) {
					extendedFieldMap.put(IS_APPLICANT_REPORTED_AS_LOSS_IN_LAST_SIX_MONTHS, true);
				}
			}
		}
		//for Is applicant Doubtful in last six months
		if (paymentMap.get("DBT") != null) {
			for (PaymentHistory paymentHistory : paymentMap.get("DBT")) {
				long ddp = 0;
				try {
					ddp = Long.parseLong(paymentHistory.getDdp());
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
				if (ddp >= 90) {
					extendedFieldMap.put(IS_APPLICANT_DOUBTFUL_IN_LAST_SIX_MONTHS, true);
				}
			}
		}

		//for is applicant mentioned as SMA
		if (paymentMap.get("SMA") != null) {
			for (PaymentHistory paymentHistory : paymentMap.get("SMA")) {
				long ddp = 0;
				try {
					ddp = Long.parseLong(paymentHistory.getDdp());
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
				if (ddp >= 90) {
					extendedFieldMap.put(IS_APPLICANT_MENTIONED_AS_SMA, true);
				}
			}
		}

		//for last update date in Bureau
		Collections.sort(loanDetailsList, new InfoAsOnComparator());
		Date lastUpdatedDate = loanDetailsList.get(0).getInfoAsOn();
		long months = getMonthsBetween(lastUpdatedDate, appDate);
		//TODO:
		if (months > 36) {
			extendedFieldMap.put(LAST_UPDATE_DATE_IN_BUREAU, lastUpdatedDate);
		}

		//TODO:
		//for Not enough information
		List<PaymentHistory> paymentHistoryList = paymentMap.get("COMBINED_PAYMENT_HISTORY");
		Collections.sort(paymentHistoryList, new PaymentHistoryComparator());
		int zeroCount = 0;
		int crossCount = 0;
		for (int i = 0; i < 12; i++) {
			if (paymentHistoryList.get(i).getDdp().equals("000"))
				;
			{
				zeroCount++;
			}
			if (paymentHistoryList.get(i).getDdp().equalsIgnoreCase("xxx"))
				;
			{
				crossCount++;
			}
		}
		if (zeroCount <= 4 || crossCount == 12) {
			extendedFieldMap.put(NOT_ENOUGH_INFO, true);
		} else {
			extendedFieldMap.put(NOT_ENOUGH_INFO, false);
		}

		//for Months since 30+DPD in the last 12 months
		Date startDate = null;
		Date endDate;
		long dpdMonths;
		for (PaymentHistory paymentHistory : paymentHistoryList) {
			long dpd = 0;
			try {
				dpd = Long.parseLong(paymentHistory.getDdp());
			} catch (NumberFormatException e) {
				// TODO: handle exception
			}

			if (dpd >= 30) {
				startDate = paymentHistory.getPaymentDate();
				break;
			}
		}
		if (startDate != null) {
			if (getDaysBetween(startDate, appDate) > 30) {
				endDate = paymentHistoryList.get(paymentHistoryList.size() - 1).getPaymentDate();
				dpdMonths = getMonthsBetween(startDate, endDate);
				extendedFieldMap.put(MONTHS_SINCE_30_PLUS_DPD_IN_THE_LAST_12_MONTHS, dpdMonths);
			}
		}

		long grpId = 0;
		if (pincode != null) {
			grpId = niyoginDAOImpl.getPincodeGroupId(pincode);
		}
		extendedFieldMap.put(PRODUCT_INDEX, grpId);

		return extendedFieldMap;
	}

	private Map<String, List<PaymentHistory>> preparePaymentHistory(List<String> paymentList, String[] types) {
		List<PaymentHistory> paymentHistoryList = new ArrayList<PaymentHistory>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MMM:yyyy");
		Map<String, List<PaymentHistory>> paymentMap = new HashMap<String, List<PaymentHistory>>();
		for (String str : paymentList) {
			String[] splittedArray = str.split("|");
			for (String s : splittedArray) {

				PaymentHistory paymentHistory = new PaymentHistory();
				//TODO:
				try {
					paymentHistory.setPaymentDate(dateFormat.parse("01:" + s.substring(0, s.indexOf(","))));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				paymentHistory.setDdp(s.substring(s.indexOf(",") + 1, s.indexOf("/")));
				paymentHistory.setType(s.substring(s.indexOf("/") + 1, s.length()));
				paymentHistoryList.add(paymentHistory);
				switch (paymentHistory.getType()) {
				case "SUP":
					if (paymentMap.get("SUP") == null) {
						List<PaymentHistory> supPayments = new ArrayList<PaymentHistory>();
						paymentMap.put("SUP", supPayments);
					}
					paymentMap.get("SUP").add(paymentHistory);
					break;

				case "STP":
					if (paymentMap.get("STP") == null) {
						List<PaymentHistory> stpPayments = new ArrayList<PaymentHistory>();
						paymentMap.put("STP", stpPayments);
					}
					paymentMap.get("STP").add(paymentHistory);
					break;

				case "LSS":
					if (paymentMap.get("LSS") == null) {
						List<PaymentHistory> lssPayments = new ArrayList<PaymentHistory>();
						paymentMap.put("LSS", lssPayments);
					}
					paymentMap.get("LSS").add(paymentHistory);
					break;

				case "DBT":
					if (paymentMap.get("DBT") == null) {
						List<PaymentHistory> dbtPayments = new ArrayList<PaymentHistory>();
						paymentMap.put("DBT", dbtPayments);
					}
					paymentMap.get("DBT").add(paymentHistory);
					break;

				case "SMA":
					if (paymentMap.get("SMA") == null) {
						List<PaymentHistory> smaPayments = new ArrayList<PaymentHistory>();
						paymentMap.put("SMA", smaPayments);
					}
					paymentMap.get("SMA").add(paymentHistory);
					break;

				default:
					break;
				}

			}
		}
		paymentMap.put("COMBINED_PAYMENT_HISTORY", paymentHistoryList);
		return paymentMap;
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

	//TODO:
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
	 * Method to fond the GreatestCommonDivisor.
	 * 
	 * @param overDueAmt
	 * @param disBursedAmt
	 * @return gcd.
	 */
	private long gcd(long overDueAmt, long disBursedAmt) {
		if (disBursedAmt == 0)
			return overDueAmt;
		else
			return gcd(disBursedAmt, overDueAmt % disBursedAmt);
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
	 * Method for prepare data and logging
	 * 
	 * @param consumerRequest
	 * @param reference
	 */
	private void doInterfaceLogging(BureauConsumer consumerRequest, String reference) {
		InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, consumerRequest, jsonResponse, reqSentOn,
				status, errorCode, errorDesc, reference);
		logInterfaceDetails(interfaceLogDetail);
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public void setNiyoginDAOImpl(NiyoginDAOImpl niyoginDAOImpl) {
		this.niyoginDAOImpl = niyoginDAOImpl;
	}

	public void setClient(JSONClient client) {
		this.client = client;
	}

}
