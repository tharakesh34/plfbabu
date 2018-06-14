package com.pennanttech.niyogin.bre.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.bre.model.AmtOfCashDeposit;
import com.pennanttech.niyogin.bre.model.AmtOfCreditTransactions;
import com.pennanttech.niyogin.bre.model.AmtOfDebtTransactions;
import com.pennanttech.niyogin.bre.model.Applicant;
import com.pennanttech.niyogin.bre.model.Application;
import com.pennanttech.niyogin.bre.model.BreData;
import com.pennanttech.niyogin.bre.model.BreItem;
import com.pennanttech.niyogin.bre.model.Bureau;
import com.pennanttech.niyogin.bre.model.Business;
import com.pennanttech.niyogin.bre.model.CoAppBureau;
import com.pennanttech.niyogin.bre.model.CoAppBusiness;
import com.pennanttech.niyogin.bre.model.CoAppElement;
import com.pennanttech.niyogin.bre.model.CoApplicant;
import com.pennanttech.niyogin.bre.model.CodeMogs;
import com.pennanttech.niyogin.bre.model.CurrentAssests;
import com.pennanttech.niyogin.bre.model.CurrentLiabilities;
import com.pennanttech.niyogin.bre.model.DeMogs;
import com.pennanttech.niyogin.bre.model.Financials;
import com.pennanttech.niyogin.bre.model.NoOfCashDeposits;
import com.pennanttech.niyogin.bre.model.NoOfCreditTransactions;
import com.pennanttech.niyogin.bre.model.Perfios;
import com.pennanttech.niyogin.bre.model.SOCIALSC;
import com.pennanttech.niyogin.bre.model.VatServiceTaxForms;
import com.pennanttech.niyogin.criff.service.CrifBureauServiceImpl;
import com.pennanttech.niyogin.experian.service.ExperianBureauServiceImpl;
import com.pennanttech.niyogin.utility.ExtFieldMapConstants;
import com.pennanttech.niyogin.utility.NiyoginUtility;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.BreService;
import com.pennanttech.pff.external.service.NiyoginService;

public class BreServiceImpl extends NiyoginService implements BreService {
	private static final Logger	logger						= Logger.getLogger(BreServiceImpl.class);

	private final String		extConfigFileName			= "bre.properties";
	private String				serviceUrl;
	private Map<String, Object>	extendedMap					= null;

	//BRE
	public static final String	REQ_SEND					= "REQSENDEXPBRE";
	public static final String	STATUSCODE					= "STATUSEXPBRE";
	public static final String	RSN_CODE					= "REASONEXPBRE";
	public static final String	REMARKS						= "REMARKSEXPBRE";
	//BRE LIST FIELDS
	public static final String	LOOKALIKEVALUE				= "LOOKALIKEVALUE";
	public static final String	LOOKALIKEELEMENT			= "LOOKALIKEELE";
	public static final String	EXPERTSCOREVALUES			= "EXPERTSCOREVALUES";
	public static final String	EXPERTSCOREELEMENTS			= "EXPERTSCOREELEMENT";
	public static final String	POLICYREASONCODE			= "POLICYREASONCODE";

	private String				PATH_LOOKALIKESCOREVALUES	= "$.data.OUTAPPLICATION.CALL2.LOOKALIKESCOREVALUES.item[*]";
	private String				PATH_LOOKALIKESCOREELEMENTS	= "$.data.OUTAPPLICATION.CALL2.LOOKALIKESCOREELEMENTS.item[*]";
	private String				PATH_EXPERTSCOREVALUES		= "$.data.OUTAPPLICATION.CALL2.EXPERTSCOREVALUES.item[*]";
	private String				PATH_EXPERTSCOREELEMENTS	= "$.data.OUTAPPLICATION.CALL2.EXPERTSCOREELEMENTS.item[*]";
	private String				PATH_POLICYREASONCODE		= "$.data.OUTAPPLICATION.CALL2.POLICYSORTEDREASONCODETABLE1.item[*]";

	/***
	 * Method for get the BRE details of the Customer and set these details to ExtendedFieldDetails.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 */
	@Override
	public AuditHeader executeBRE(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		
		if (StringUtils.isBlank(serviceUrl)) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		extendedMap = getExtendedMapValues(financeDetail);

		Map<String, Object> appplicationdata = new HashMap<>();
		BreData breDataRequest = prepareRequestObj(financeDetail);

		//send request and log
		String reference = financeMain.getFinReference();
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;
		Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());
		try {
			reuestString = client.getRequestString(breDataRequest);
			jsonResponse = client.post(serviceUrl, reuestString);
			//check response for error
			errorCode = getErrorCode(jsonResponse);
			errorDesc = getErrorMessage(jsonResponse);

			doInterfaceLogging(reference, reuestString, jsonResponse, errorCode, errorDesc, reqSentOn);

			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, getTrimmedMessage(errorDesc));
			appplicationdata.put(STATUSCODE, getStatusCode(jsonResponse));

			if (StringUtils.isEmpty(errorCode)) {
				//read values from response and load it to extended map
				Map<String, Object> mapdata = getPropValueFromResp(jsonResponse, extConfigFileName);
				//process the Response
				prepareBreExtendedMap(jsonResponse, mapdata);
				Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
				//add to final
				appplicationdata.putAll(mapvalidData);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorDesc = getWriteException(e);
			errorDesc = getTrimmedMessage(errorDesc);
			doExceptioLogging(reference, reuestString, jsonResponse, errorDesc, reqSentOn);

			appplicationdata.put(RSN_CODE, errorCode);
			appplicationdata.put(REMARKS, errorDesc);
		}
		appplicationdata.put(REQ_SEND, true);
		prepareResponseObj(appplicationdata, financeDetail);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for prepare the BreData Request Object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private BreData prepareRequestObj(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		extendedMap = getExtendedMapValues(financeDetail);
		BreData breData = new BreData();
		breData.setCif(StringUtils.trimToNull(customer.getCustCIF()));
		breData.setApplication(prepareApplication(financeDetail));
		breData.setApplicant(prepareApplicant(financeDetail));
		breData.setCoApplicant(prepareCoApplicant(financeDetail));
		logger.debug(Literal.LEAVING);
		return breData;
	}

	/**
	 * Method for prepare the Application Request Object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private Application prepareApplication(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		Application application = new Application();
		application.setDateOfApplication(NiyoginUtility.formatDate(getAppDate(), "yyyy-MM-dd"));
		application.setApplicationId(StringUtils.trimToNull(finMain.getFinReference()));
		application.setBureau(prepareBureau(financeDetail));
		application.setSocialsc(prepareSocialsc(financeDetail));
		application.setAppliedLoanAmount(formateAmount(finMain.getFinAmount()));
		logger.debug(Literal.LEAVING);
		return application;
	}

	private Bureau prepareBureau(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		Bureau bureau = new Bureau();
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		bureau.setLoanType(StringUtils.trimToNull(finMain.getFinType()));
		bureau.setAgeOfCustomer(getAgeTillAppDate(customer.getCustDOB()));

		if (extendedMap != null) {
			//getting data from CRIFF
			bureau.setNoOfBusLoansOpenedL6M(getIntValue(CrifBureauServiceImpl.NUMB_OF_BUS_LOANS_OPENED_IN_L6M));
			bureau.setClsTotDisbAmtL12M(getBigDecimalValue(CrifBureauServiceImpl.SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS));
			bureau.setMinPctPaidUnSecL1M(getBigDecimalValue(CrifBureauServiceImpl.MIN_PER_OF_AMT_REPAID_ACROSS_ALL_UNSECURE_LOANS));
			bureau.setPctTotOvdhicrL1M(getBigDecimalValue(CrifBureauServiceImpl.RATIO_OF_OVERDUE_AND_DISBURSEMENT_AMT_FOR_ALL_LOANS));
			bureau.setAlMaxPctPaidSecL1M(getBigDecimalValue(CrifBureauServiceImpl.MAX_PER_OF_AMT_REPAID_ACROSS_ALL_ACT_SEC_LOANS));
			bureau.setMonSinceL30pOvralL12M(getIntValue(CrifBureauServiceImpl.MONTHS_SINCE_30_PLUS_DPD_IN_L12M));
			bureau.setClsTotDisbAmt(getBigDecimalValue(CrifBureauServiceImpl.SUM_OF_DISBURSED_AMT_OF_ALL_CLOSED_LOANS));
			bureau.setNoPrvsLoansAsOfAppDate(getBooleanValue(CrifBureauServiceImpl.NO_PREVS_LOANS_AS_OF_APP_DT) ? "1" : "0");
			bureau.setIsApplicant90PlusDpdinL6M(getBooleanValue(CrifBureauServiceImpl.IS_APP_90PLUS_DPD_IN_L6M) ? "1" : "0");
			bureau.setIsApplicantSubStandardinL6M(getBooleanValue(CrifBureauServiceImpl.IS_APP_SUBSTANDARD_IN_L6M) ? "1" : "0");
			bureau.setIsApplicantReportedAsLossinL6M(getBooleanValue(CrifBureauServiceImpl.IS_APP_REPORTED_AS_LOSS_IN_L6M) ? "1" : "0");
			bureau.setIsApplicantDoubtfulinL6M(getBooleanValue(CrifBureauServiceImpl.IS_APP_DOUBTFUL_IN_L6M) ? "1" : "0");
			bureau.setIsApplicantMentionedAsSMA(getBooleanValue(CrifBureauServiceImpl.IS_APP_MENTIONED_AS_SMA) ? "1" : "0");
			String lstUpdDate = NiyoginUtility.formatDate(getDateValue(CrifBureauServiceImpl.LAST_UPDATE_DT_IN_BUREAU),"yyyy-MM-dd");
			bureau.setLastUpdateDtInBureau(lstUpdDate);
			bureau.setNotenoughInfo(getBooleanValue(CrifBureauServiceImpl.NOT_ENOUGH_INFO) ? "1" : "0");
			bureau.setMaxUnSecDisbAmtL12M(getBigDecimalValue(CrifBureauServiceImpl.MAX_DISBURSED_AMT_ACROSS_ALL_UNSECURED_LOANS_IN_L12M));

			//getting data from EXPERIAN BUREAU
			bureau.setTotalEnquiries(getIntValue(ExperianBureauServiceImpl.NO_OF_ENQUIRES));
			bureau.setRestructuredFlag(getBooleanValue(ExperianBureauServiceImpl.RESTRUCTURED_FLAG) ? "1" : "0");
			bureau.setSfFlag(getBooleanValue(ExperianBureauServiceImpl.SUIT_FILED_FLAG) ? "1" : "0");
			bureau.setWdFlag(getBooleanValue(ExperianBureauServiceImpl.WILLFUL_DEFAULTER_FLAG) ? "1" : "0");
			bureau.setWoFlag(getBooleanValue(ExperianBureauServiceImpl.WRITE_OFF_FLAG) ? "1" : "0");
			bureau.setSettledFlag(getBooleanValue(ExperianBureauServiceImpl.SETTLED_FLAG_FLAG) ? "1" : "0");

			//TODO:
			bureau.setProdIndexHL(0);
			bureau.setProdIndexAL(0);
			bureau.setProdIndexCC(0);
			bureau.setProdIndexBLOD(0);
			bureau.setProdIndexCLPTL(0);
			bureau.setAlWorstAmtOvrdueSecL1M(BigDecimal.ZERO);
			bureau.setBalDisbAmtRatioL1M(BigDecimal.ZERO);
			bureau.setMaxDelinquencySecL12M(BigDecimal.ZERO);
			bureau.setNoOf90DpdActvOvralL12M(0);
			bureau.setNoOf90DpdOverallL12M(0);
			bureau.setMinLoanamount(BigDecimal.ZERO);
			bureau.setMaxLoanamount(BigDecimal.ZERO);
			bureau.setMinTenure(BigDecimal.ZERO);
			bureau.setMaxTenure(BigDecimal.ZERO);
			bureau.setLoansTakenPostFinancialyrInBureau(BigDecimal.ZERO);
			bureau.setCurBusLoanDisbAmt(BigDecimal.ZERO);
			bureau.setMaxPctPaidUnSecL1M(BigDecimal.ZERO);
			bureau.setActTotDisbAmt(BigDecimal.ZERO);
			bureau.setAlWorstCurBalOverallL1M(BigDecimal.ZERO);
			bureau.setWorstCurBalOverallL1M(BigDecimal.ZERO);
			bureau.setMaxPctPaidOvralL1M(BigDecimal.ZERO);
			bureau.setClPctTotBalHicrL1M(BigDecimal.ZERO);
			bureau.setTotDisbAmt(BigDecimal.ZERO);
			bureau.setAlAvgCurBalOverallL1M(BigDecimal.ZERO);
			bureau.setAlCurBalOvralL1M(BigDecimal.ZERO);
			bureau.setWorstCurBalSecL1M(BigDecimal.ZERO);
			bureau.setAvgCurBalSecL1M(BigDecimal.ZERO);
			bureau.setCurBalSecL1M(BigDecimal.ZERO);
			bureau.setAlAvgCurBalSecL1M(BigDecimal.ZERO);
			bureau.setAlWorstCurBalSecL1M(BigDecimal.ZERO);
			bureau.setWorstCurBalUnSecL1M(BigDecimal.ZERO);
			bureau.setAvgCurBalOverallL1M(BigDecimal.ZERO);
			bureau.setPctTotBalHicrL1M(BigDecimal.ZERO);
			bureau.setClMaxPctPaidUnSecL1M(BigDecimal.ZERO);
			bureau.setClMinPctPaidUnSecL1M(BigDecimal.ZERO);
			bureau.setAvgCurBalUnSecL1M(BigDecimal.ZERO);
			bureau.setCurBalUnSecL1M(BigDecimal.ZERO);
			bureau.setAlMinPctPaidUnSecL1M(BigDecimal.ZERO);
			bureau.setAlPctTotOvdHicrL1M(BigDecimal.ZERO);
			bureau.setPctTotOvdBalL1M(BigDecimal.ZERO);
			bureau.setClMinPctPaidOvralL1M(BigDecimal.ZERO);
			bureau.setAlMaxPctPaidUnSecL1M(BigDecimal.ZERO);
			bureau.setAgeOldestPrev(BigDecimal.ZERO);
			bureau.setAlWorstCurtBalUnSecL1M(BigDecimal.ZERO);
			bureau.setAlMaxPctPaidOvralL1M(BigDecimal.ZERO);
			bureau.setAlAvgCurBalUnSecL1M(BigDecimal.ZERO);
			bureau.setWorstAmtOvedueUnSecL1M(BigDecimal.ZERO);
			bureau.setAvgAmtOvrdueUnSecL1M(BigDecimal.ZERO);
			bureau.setCurOverdueUnSecL1M(BigDecimal.ZERO);
			bureau.setAlPctTotoVdbalL1M(BigDecimal.ZERO);
			bureau.setClWorPrevBalUnSecL1M(BigDecimal.ZERO);
			bureau.setAlPctTotBalHicrL1M(BigDecimal.ZERO);
			bureau.setRatioActvTotLoans(BigDecimal.ZERO);
			bureau.setClMaxPctPaidovralL1M(BigDecimal.ZERO);
			bureau.setWorstAmtOvrdueOvralL1M(BigDecimal.ZERO);
			bureau.setAvgAmtOvrdueOvralL1M(BigDecimal.ZERO);
			bureau.setCurOverdueOvralL1M(BigDecimal.ZERO);
			bureau.setMaxSecDisbAmtL12M(BigDecimal.ZERO);
			bureau.setMaxUnSecDisbAmtL6M(BigDecimal.ZERO);
			bureau.setMinPctPaidOvralL1M(BigDecimal.ZERO);
			bureau.setClWorPrevBalOvralL1M(BigDecimal.ZERO);
			bureau.setAlWorstAmtOvedueUnSecL1M(BigDecimal.ZERO);
			bureau.setAlAvgAmtOvrdueUnSecL1M(BigDecimal.ZERO);
			bureau.setAlCurOverdueUnSecL1M(BigDecimal.ZERO);
			bureau.setAlWorstAmtOvrdueOvralL1M(BigDecimal.ZERO);
			bureau.setAlAvgAmtOvrdueOvralL1M(BigDecimal.ZERO);
			bureau.setAlCurOverdueOvralL1M(BigDecimal.ZERO);
			bureau.setTotPrevLoans(BigDecimal.ZERO);
			bureau.setMaxSecDisbAmtL6M(BigDecimal.ZERO);
			bureau.setAlMinPctPaidOvralL1M(BigDecimal.ZERO);
			bureau.setClAvgCurBalUnSecL1M(BigDecimal.ZERO);
			bureau.setClCurBalUnSecL1M(BigDecimal.ZERO);
			bureau.setAlMinPctPaidSecL1M(BigDecimal.ZERO);
			bureau.setMaxDelinQuencyUnSecL12M(BigDecimal.ZERO);
			bureau.setMinPctPaidSecL1M(BigDecimal.ZERO);
			bureau.setMaxUnSecDisbAmtL3M(BigDecimal.ZERO);
			bureau.setMaxSecDisbAmtL3M(BigDecimal.ZERO);
			bureau.setNoOfActvLoansOnDisbdt(BigDecimal.ZERO);
			bureau.setClAvgCurBalOverallL1M(BigDecimal.ZERO);
			bureau.setClCurBalOvralL1M(BigDecimal.ZERO);
			bureau.setNoOfClosedLoansL12M(BigDecimal.ZERO);
			bureau.setMaxDelinQuencyOverallL12M(BigDecimal.ZERO);
			bureau.setClPctTotOvdBalL1M(BigDecimal.ZERO);
			bureau.setMaxPctPaidSecL1M(BigDecimal.ZERO);
			bureau.setAgeLattestprev(BigDecimal.ZERO);
			bureau.setMonSinceL30pOvralL6M(BigDecimal.ZERO);
			bureau.setClWorstCurBalUnSecL1M(BigDecimal.ZERO);
			bureau.setNoOfClosedLoansL6M(BigDecimal.ZERO);
			bureau.setNoOf30DpdOverallL12M(BigDecimal.ZERO);
			bureau.setMaxUnSecDisbAmtL1M(BigDecimal.ZERO);
			bureau.setMaxDelinQuencyOverallL6M(BigDecimal.ZERO);
			bureau.setNoOf30DpdOverallL6M(BigDecimal.ZERO);
			bureau.setClWorstCurBalOverallL1M(BigDecimal.ZERO);
			bureau.setScore(BigDecimal.ZERO);
			bureau.setAssetClassification(BigDecimal.ZERO);
			bureau.setLiveTradelines(BigDecimal.ZERO);

		}
		logger.debug(Literal.LEAVING);
		return bureau;
	}

	private List<SOCIALSC> prepareSocialsc(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		List<SOCIALSC> socialsList = new ArrayList<>(1);
		logger.debug(Literal.LEAVING);
		return socialsList;
	}

	/**
	 * Method for prepare the Applicant Request Object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private Applicant prepareApplicant(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		Applicant applicant = new Applicant();
		applicant.setDemogs(prepareDemogs(financeDetail));
		applicant.setFinancials(prepareFinancials(financeDetail));
		applicant.setBusiness(preoareBusiness(financeDetail));
		applicant.setPerfios(preparePerfios(financeDetail));
		logger.debug(Literal.LEAVING);
		return applicant;
	}

	private DeMogs prepareDemogs(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		
		DeMogs deMogs = new DeMogs();
		deMogs.setTypeOfIndustry(StringUtils.trimToNull(customer.getLovDescCustSectorName()));
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		if (addressList != null && !addressList.isEmpty()) {
			CustomerAddres address = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_OFF);
			deMogs.setRegisteredOffcPincode(StringUtils.trimToNull(address.getCustAddrZIP()));
			//TODO:
			deMogs.setOperationalOffcPincode(StringUtils.trimToNull(address.getCustAddrZIP()));
			deMogs.setZipCode(StringUtils.trimToNull(address.getCustAddrZIP()));
		}
				
		deMogs.setCategoryOfApplicant(StringUtils.trimToNull(customer.getCustTypeCode()));
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		deMogs.setPanNumber(StringUtils.trimToNull(getPanNumber(documentList)));
		if (extendedMap != null) {
			//getting data from web or mobile
			deMogs.setResidenceTypeOfMDorPROPTRYorMNGNGPARTNER(getStringValue(ExtFieldMapConstants.BUSINESS_PREMISES_CUSTOMER));
			deMogs.setYrsAtCurResidencePROPorMPorMDetc(getIntValue(ExtFieldMapConstants.YR_CURRENT_RESIDENCE_CUSTOMER));
		}
		
		String phoneNumber = NiyoginUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList(),InterfaceConstants.PHONE_TYPE_OFF);
		deMogs.setMobileNumber(StringUtils.trimToNull(phoneNumber));
		
		List<CustomerEMail> emailList = customerDetails.getCustomerEMailList();
		deMogs.setEmail(StringUtils.trimToNull(NiyoginUtility.getEmail(emailList)));
		
		//TODO:
		deMogs.setDateOfInc(NiyoginUtility.formatDate(customer.getCustDOB(), "yyyy-MM-dd"));
		deMogs.setGstin(null);
		deMogs.setApplicantAdhaar(StringUtils.trimToNull(getPanNumber(documentList)));//FIXME
		deMogs.setUdyogadhaar(null);
		
		logger.debug(Literal.LEAVING);
		return deMogs;
	}

	private Financials prepareFinancials(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		
		Financials financials = new Financials();
		//getting data from pdfExtraction
		financials.setExpenseYr1(getBigDecimalValue(ExtFieldMapConstants.EXPENSE_YR1));
		financials.setExpenseYr2(getBigDecimalValue(ExtFieldMapConstants.EXPENSE_YR2));
		financials.setDepreciationYr1(getBigDecimalValue(ExtFieldMapConstants.DEPRECIATION_YR1));
		financials.setDepreciationYr2(getBigDecimalValue(ExtFieldMapConstants.DEPRECIATION_YR2));
		financials.setInterestOnCapitalToPartners(getBigDecimalValue(ExtFieldMapConstants.INTERST_CAPTIAL_PATNER_YR1));
		financials.setIncomeTax(getBigDecimalValue(ExtFieldMapConstants.INCOME_TAX_YR1));
		financials.setEquityAndPreferrenceShareCapital(getBigDecimalValue(ExtFieldMapConstants.EQUITYSHARECAPTIAL_YR1));
		financials.setQuasiEquityDirectorsFriendsAndRelatives(getBigDecimalValue(ExtFieldMapConstants.QUASI_EQUITY_YR1));
		financials.setFixedassetYr1(getBigDecimalValue(ExtFieldMapConstants.FIXED_ASSEST_YR1));
		financials.setFixedassetYr2(getBigDecimalValue(ExtFieldMapConstants.FIXED_ASSEST_YR2));
		financials.setSundryDebtorsLessThan6M(getBigDecimalValue(ExtFieldMapConstants.SUNDRY_DBTRS_LESSTHAN_6M_YR1));
		financials.setSundrydebtorsgreatorthan6M(getBigDecimalValue(ExtFieldMapConstants.SUNDRY_DBTRS_LESSTHAN_6M_YR1));
		financials.setTurnOverYr1(getBigDecimalValue(ExtFieldMapConstants.TURN_OVER_YR1));
		financials.setTurnOverYr2(getBigDecimalValue(ExtFieldMapConstants.TURN_OVER_YR2));
		financials.setTotalAssets(getBigDecimalValue(ExtFieldMapConstants.TOTAL_ASSETS_YR1));
		financials.setTotalLiabilities(getBigDecimalValue(ExtFieldMapConstants.TOTAL_LIABILITIES_YR1));
		financials.setProfitYr1(getBigDecimalValue(ExtFieldMapConstants.NET_PROFIT_YR1));
		financials.setProfitYr2(getBigDecimalValue(ExtFieldMapConstants.NET_PROFIT_YR2));
		financials.setFundsReceived(getBigDecimalValue(ExtFieldMapConstants.FUNDS_RECEIVED));
		financials.setStock(getBigDecimalValue(ExtFieldMapConstants.STOCK_YR1));
		financials.setInvestment(getBigDecimalValue(ExtFieldMapConstants.INVESTMENTS_YR1));
		financials.setShorttermBorrowings(getBigDecimalValue(ExtFieldMapConstants.SHORT_TERM_BORROWING_YR1));
		BigDecimal provisionsYr1 = getBigDecimalValue(ExtFieldMapConstants.PROVISIONS_YR1);
		BigDecimal othCurntLiabilitiesYr1 = getBigDecimalValue(ExtFieldMapConstants.OTH_CURNT_LIABILITIES_YR1);
		financials.setProvisions(provisionsYr1.add(othCurntLiabilitiesYr1));
		financials.setInterestObligation(getBigDecimalValue(ExtFieldMapConstants.INTEREST_TO_BANKS_YR1));
		financials.setNetProfitOrLoss(getBigDecimalValue(ExtFieldMapConstants.NET_PROFIT_YR1));
		financials.setReservesAndSurPlus(getBigDecimalValue(ExtFieldMapConstants.RESERVES_AND_SURPLUS_YR1));
		BigDecimal longTermBorwngYr1 = getBigDecimalValue(ExtFieldMapConstants.LONG_TERM_BORWNG_YR1);
		BigDecimal shortTermBorwngYr1 = getBigDecimalValue(ExtFieldMapConstants.SHORT_TERM_BORWNG_YR1);
		financials.setTotalLoans(longTermBorwngYr1.add(shortTermBorwngYr1));
		financials.setSundryCreditorsForTradeAndExpensesBillsPayable(getBigDecimalValue(ExtFieldMapConstants.SUNDRY_CREDITORS_YR1));
		financials.setCash(getBigDecimalValue(ExtFieldMapConstants.CASH_AND_BANK_YR1));
		
		//getting data from web or mobile
		financials.setSumOfEmiAllLoans(getBigDecimalValue(ExtFieldMapConstants.SUM_EMI_ALL_LOANS));
		financials.setExistingLoanObligation(getBigDecimalValue(ExtFieldMapConstants.EXISTING_LOAN_OBLIGATION));
		
		//getting data from BRE
		financials.setPostFundingDSCR(getBigDecimalValue(ExtFieldMapConstants.POST_FUNDING_DSCR));
		financials.setCurrentRatio(getBigDecimalValue(ExtFieldMapConstants.CURRENT_RATIO));
		financials.setWorkingCapitalGap(getBigDecimalValue(ExtFieldMapConstants.WRKNG_CAPITAL_CYCLE_GAP));
		financials.setNetworth(getBigDecimalValue(ExtFieldMapConstants.NET_WORTH));
		
		//getting data from PREFIOUS
		financials.setUtilizationLimitOnODorCC(getBigDecimalValue(ExtFieldMapConstants.CC_OR_OD_UTILIZATION));
		
		//getting data from CRIFF
		String oldDisDate = NiyoginUtility.formatDate(getDateValue(CrifBureauServiceImpl.OLDEST_LOANDISBURSED_DT),"yyyy-MM-dd");
		financials.setOldestLoanDisbursedDate(oldDisDate);
		
		financials.setCurrentAssets(prepareCurrentAssests(financeDetail));
		financials.setCurrentLiabilittes(prepareCurrentLiabilities(financeDetail));
		financials.setVatServiceTaxForm26as(prepareVatServiceTaxForms(financeDetail));
		
		//TODO:
		//fields already used in financial Turnover YR1,YR2
		financials.setSalesYr1(getBigDecimalValue(ExtFieldMapConstants.TURN_OVER_YR1));
		financials.setSalesYr2(getBigDecimalValue(ExtFieldMapConstants.TURN_OVER_YR2));
		financials.setNetSalesOrReceiptsGrossReturns(BigDecimal.ZERO);
		financials.setNetPurchasesGrossReturns(BigDecimal.ZERO);
		financials.setDirectExpenses(BigDecimal.ZERO);
		financials.setIndirectExpensesSellingAndAdminAndGeneral(BigDecimal.ZERO);
		financials.setQuasiInterestFriendsOrRelativesIfAny(BigDecimal.ZERO);
		financials.setDepreciation(BigDecimal.ZERO);
		financials.setInterestToBanksOrFinancialInstitutionsOrFinanciers(BigDecimal.ZERO);
		financials.setBorrowingFromGroupCompanies(BigDecimal.ZERO);
		financials.setFixedAssetsNetBlock(BigDecimal.ZERO);
		financials.setItReturnFilingDate(null);
		financials.setLiveFinancialObligationsInEmiCcodBaleetc(BigDecimal.ZERO);
		financials.setCashCreditorOverdraft(BigDecimal.ZERO);
		financials.setDbtPosOfAlTheOutStndgLoansWhichAreNotClsInTheNext3M(BigDecimal.ZERO);
		financials.setEquityTotAanGiblenetWorth(BigDecimal.ZERO);
		financials.setTurnOverGrowthLastYr(BigDecimal.ZERO);
		financials.setTurnOverGrowthLastToLastYr(BigDecimal.ZERO);
		financials.setMinYrlyTurnOver(BigDecimal.ZERO);
		financials.setAvgValueOfTurnoverOfL3Yrs(BigDecimal.ZERO);
		financials.setRetainedEarnings(BigDecimal.ZERO);
		financials.setEquity(BigDecimal.ZERO);
		financials.setDebt(BigDecimal.ZERO);
		financials.setBorrowings(BigDecimal.ZERO);
		financials.setOperationProfit(BigDecimal.ZERO);
		financials.setWorkingCapitalCycle(BigDecimal.ZERO);
		financials.setAppliedTenor(BigDecimal.ZERO);
		financials.setLatestDebtors(BigDecimal.ZERO);
		financials.setBank(BigDecimal.ZERO);
		financials.setSundryDebtors(BigDecimal.ZERO);
		financials.setPartnersOrDirectorsRemuneration(BigDecimal.ZERO);

		logger.debug(Literal.LEAVING);
		return financials;
	}

	private CurrentAssests prepareCurrentAssests(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CurrentAssests currentAssests = new CurrentAssests();
		currentAssests.setItems(new ArrayList<BreItem>(1));
		logger.debug(Literal.LEAVING);
		return currentAssests;
	}

	private CurrentLiabilities prepareCurrentLiabilities(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CurrentLiabilities currentLiabilities = new CurrentLiabilities();
		currentLiabilities.setItems(new ArrayList<BreItem>(1));
		logger.debug(Literal.LEAVING);
		return currentLiabilities;
	}

	private VatServiceTaxForms prepareVatServiceTaxForms(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		VatServiceTaxForms forms = new VatServiceTaxForms();
		List<BigDecimal> vatList = null;
		if (extendedMap != null) {
			vatList = new ArrayList<BigDecimal>(6);
			vatList.add(getBigDecimalValue(ExtFieldMapConstants.VAT_MONTH1));
			vatList.add(getBigDecimalValue(ExtFieldMapConstants.VAT_MONTH2));
			vatList.add(getBigDecimalValue(ExtFieldMapConstants.VAT_MONTH3));
			vatList.add(getBigDecimalValue(ExtFieldMapConstants.VAT_MONTH4));
			vatList.add(getBigDecimalValue(ExtFieldMapConstants.VAT_MONTH5));
			vatList.add(getBigDecimalValue(ExtFieldMapConstants.VAT_MONTH6));
		}
		forms.setItems(vatList);
		logger.debug(Literal.LEAVING);
		return forms;
	}

	private Business preoareBusiness(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		Business business = new Business();
		if (extendedMap != null) {
			business.setBusPremisesOwnership(getStringValue(ExtFieldMapConstants.BUSINESS_PREMISES_CUSTOMER));
		}
		business.setOrgType(getCustTypeDesc(customer.getCustTypeCode()));
		//TODO:
		business.setNumbOfOwnersOrShareholdingPattern(0);
		business.setOperationalBusinessVintage(BigDecimal.ZERO);
		logger.debug(Literal.LEAVING);
		return business;
	}

	private Perfios preparePerfios(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		
		Perfios perfios = new Perfios();
		//getting data from PREFIOUS
		perfios.setAvgBankBalance(getBigDecimalValue(ExtFieldMapConstants.AVERAGE_BANK_BALANCE));
		perfios.setInwardChequeReturns(getIntValue(ExtFieldMapConstants.NO_OF_INWARD_CHEQUE_BOUNCES));
		perfios.setOdccLimit(getBigDecimalValue(ExtFieldMapConstants.OD_OR_CC_LIMIT));
		perfios.setNoOfEmiBounce(getIntValue(ExtFieldMapConstants.EMI_BOUNCES_L6M));
		
		perfios.setNoOfCreditTransactions(prepareNoOfCreditTransactions(financeDetail));
		perfios.setAmtOfCreditTransactions(prepareAmtOfCreditTransactions(financeDetail));
		perfios.setAmtOfDebtTransactions(prepareAmtOfDebtTransactions());
		perfios.setNoOfCashDeposits(prepareNoOfCashDeposits());
		perfios.setAmtOfCashDeposit(prepareAmtOfCashDeposit());
		
		//TODO:
		perfios.setCreditConcentrationInBankStatement(BigDecimal.ZERO);
		perfios.setMinBalChargesReported(BigDecimal.ZERO);
		perfios.setNoOfDebitTransactions(0);
		perfios.setIntOdCc(0);
		perfios.setNoOfCashWithdrawls(0);
		perfios.setAmtOfCashWithdrawls(BigDecimal.ZERO);
		perfios.setNoOfChequeDeposits(0);
		perfios.setAmtOfChequeDeposits(BigDecimal.ZERO);
		perfios.setTotNoOfChequeIssues(0);
		perfios.setTotAmtOfChequeIssues(BigDecimal.ZERO);
		perfios.setTotalnoOfoutwardchequebounces(0);
		perfios.setMinOdBalance(BigDecimal.ZERO);
		perfios.setMaxOdBalance(BigDecimal.ZERO);
		//field type is date but in extFieldDetails it is Bigdecimal.
		//perfios.setIssueDateForGstnDoc(getBigDecimalValue(ExtFieldMapConstants.ISSUE_DT_FOR_GSTN_DOC));
		perfios.setIssueDateForGstnDoc(null);
		perfios.setTotCredit(BigDecimal.ZERO);
		logger.debug(Literal.LEAVING);
		return perfios;
	}

	private NoOfCreditTransactions prepareNoOfCreditTransactions(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		NoOfCreditTransactions creditTransactions = new NoOfCreditTransactions();
		creditTransactions.setItems(new ArrayList<BreItem>(1));
		logger.debug(Literal.LEAVING);
		return creditTransactions;
	}

	private AmtOfCreditTransactions prepareAmtOfCreditTransactions(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		AmtOfCreditTransactions amtOfCreditTransactions = new AmtOfCreditTransactions();
		List<BigDecimal> creditTransactions = null;
		if (extendedMap != null) {
			creditTransactions = new ArrayList<BigDecimal>(6);
			creditTransactions.add(getBigDecimalValue(ExtFieldMapConstants.FUND_RECEIVED_MNTH1));
			creditTransactions.add(getBigDecimalValue(ExtFieldMapConstants.FUND_RECEIVED_MNTH2));
			creditTransactions.add(getBigDecimalValue(ExtFieldMapConstants.FUND_RECEIVED_MNTH3));
			creditTransactions.add(getBigDecimalValue(ExtFieldMapConstants.FUND_RECEIVED_MNTH4));
			creditTransactions.add(getBigDecimalValue(ExtFieldMapConstants.FUND_RECEIVED_MNTH5));
			creditTransactions.add(getBigDecimalValue(ExtFieldMapConstants.FUND_RECEIVED_MNTH6));
		}
		amtOfCreditTransactions.setItems(creditTransactions);
		logger.debug(Literal.LEAVING);
		return amtOfCreditTransactions;
	}

	private AmtOfDebtTransactions prepareAmtOfDebtTransactions() {
		logger.debug(Literal.ENTERING);
		AmtOfDebtTransactions amtOfDebtTransactions = new AmtOfDebtTransactions();
		amtOfDebtTransactions.setItems(new ArrayList<BreItem>(1));
		logger.debug(Literal.LEAVING);
		return amtOfDebtTransactions;
	}

	private NoOfCashDeposits prepareNoOfCashDeposits() {
		logger.debug(Literal.ENTERING);
		NoOfCashDeposits noOfCashDeposits = new NoOfCashDeposits();
		noOfCashDeposits.setItems(new ArrayList<BreItem>(1));
		logger.debug(Literal.LEAVING);
		return noOfCashDeposits;
	}

	private AmtOfCashDeposit prepareAmtOfCashDeposit() {
		logger.debug(Literal.ENTERING);
		AmtOfCashDeposit amtOfCashDeposit = new AmtOfCashDeposit();
		amtOfCashDeposit.setItems(new ArrayList<BreItem>(1));
		logger.debug(Literal.LEAVING);
		return amtOfCashDeposit;
	}

	/**
	 * Method for prepare the CoApplicant Request Object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private CoApplicant prepareCoApplicant(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CoApplicant coApplicant = new CoApplicant();
		coApplicant.setCoAppElements(prepareCoAppElements(financeDetail));
		logger.debug(Literal.LEAVING);
		return coApplicant;
	}

	private List<CoAppElement> prepareCoAppElements(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		List<CoAppElement> coAppElements = new ArrayList<CoAppElement>(1);
		List<JointAccountDetail> coapplicants = financeDetail.getJountAccountDetailList();
		if (coapplicants != null && !coapplicants.isEmpty()) {
			List<Long> coApplicantIDs = new ArrayList<Long>(1);
			for (JointAccountDetail coApplicant : coapplicants) {
				coApplicantIDs.add(coApplicant.getCustID());
			}
			List<CustomerDetails> coApplicantCustomers = getCoApplicantsWithExtFields(coApplicantIDs);
			for (CustomerDetails customerDetails : coApplicantCustomers) {
				CoAppElement coAppElement = new CoAppElement();
				coAppElement.setCodeMogs(prepareCodeMogs(customerDetails));
				coAppElement.setCoAppBureau(prepareCoAppBureau(customerDetails));
				coAppElement.setCoAppBusiness(prepareCoAppBusiness(customerDetails));
				coAppElements.add(coAppElement);
			}
		}
		logger.debug(Literal.LEAVING);
		return coAppElements;
	}

	private CodeMogs prepareCodeMogs(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		Customer customer = customerDetails.getCustomer();
		
		CodeMogs codeMogs = new CodeMogs();
		codeMogs.setDob(NiyoginUtility.formatDate(customer.getCustDOB(), "yyyy-MM-dd"));
		
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		if (addressList != null && !addressList.isEmpty()) {
			CustomerAddres curAddress=null;
			CustomerAddres perAddress=null;
			curAddress = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_CURRES);
			perAddress = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_PERNMENT);
			codeMogs.setCurrentResidencePincode(StringUtils.trimToNull(curAddress.getCustAddrZIP()));
			codeMogs.setPermanentResidencePincode(StringUtils.trimToNull(perAddress.getCustAddrZIP()));
		}

		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		codeMogs.setCoAppPanNumber(StringUtils.trimToNull(getPanNumber(documentList)));
		codeMogs.setSalToPartnerOrDirector(getBigDecimalValue(ExtFieldMapConstants.PARTNERS_DIRECTORS_REMUN_YR1));
		
		//TODO:
		codeMogs.setMinAge(0);
		codeMogs.setMaxAge(0);
		codeMogs.setAuthority(null);
		codeMogs.setRelOfScndaryCoAppWithPrimaryCoApp(null);
		logger.debug(Literal.LEAVING);
		return codeMogs;
	}

	private CoAppBureau prepareCoAppBureau(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		CoAppBureau coAppBureau = new CoAppBureau();
		coAppBureau.setCoAppscore(null);
		coAppBureau.setNoOfTimes30inL6M(coAppBureau.getNoOfTimes30inL6M());
		coAppBureau.setCoAppAssetClassification(null);
		logger.debug(Literal.LEAVING);
		return coAppBureau;
	}

	private CoAppBusiness prepareCoAppBusiness(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		CoAppBusiness coAppBusiness = new CoAppBusiness();
		Map<String, Object> custFormFields = getExtendedMapValues(customerDetails);
		if (custFormFields != null) {
			try {

				int workExp = (int) custFormFields.get(ExtFieldMapConstants.MAX_WORK_EXPERIENCE);
				coAppBusiness.setMaxWorkExperience(workExp);
			} catch (Exception e) {
			}
		}
		logger.debug(Literal.LEAVING);
		return coAppBusiness;
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
	private void doInterfaceLogging(String reference, String requets, String response, String errorCode,
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
		iLogDetail.setStatus(InterfaceConstants.STATUS_SUCCESS);
		iLogDetail.setErrorCode(errorCode);
		if (errorDesc != null && errorDesc.length() > 200) {
			iLogDetail.setErrorDesc(errorDesc.substring(0, 190));
		}

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Method for the prepare the BRE response extended map
	 * 
	 * @param jsonResponse
	 * @param mapdata
	 */
	private void prepareBreExtendedMap(String jsonResponse, Map<String, Object> mapdata) {
		logger.debug(Literal.ENTERING);
		mapdata.put(LOOKALIKEVALUE, getListFieldData(jsonResponse, PATH_LOOKALIKESCOREVALUES));
		mapdata.put(LOOKALIKEELEMENT, getListFieldData(jsonResponse, PATH_LOOKALIKESCOREELEMENTS));
		mapdata.put(EXPERTSCOREVALUES, getListFieldData(jsonResponse, PATH_EXPERTSCOREVALUES));
		mapdata.put(EXPERTSCOREELEMENTS, getListFieldData(jsonResponse, PATH_EXPERTSCOREELEMENTS));
		mapdata.put(POLICYREASONCODE, getListFieldData(jsonResponse, PATH_POLICYREASONCODE));
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Method to prepare the Extendedfield detail ListField data by seperating the each list cell value with regix
	 * 
	 * @param jsonResponse
	 * @param jsonPath
	 * @return String with regix seperated listCell's
	 */
	@SuppressWarnings("unchecked")
	public String getListFieldData(String jsonResponse, String jsonPath) {
		logger.debug(Literal.ENTERING);
		StringBuilder builder = new StringBuilder();
		Object responseObj = null;

		String listFieldsData = Objects.toString(getValueFromResponse(jsonResponse, jsonPath), "");
		if (!StringUtils.isEmpty(listFieldsData)) {
			try {
				responseObj = getResponseObject(listFieldsData, String.class, true);
				List<String> dataList = (List<String>) responseObj;
				if (dataList != null && !dataList.isEmpty()) {
					for (String value : dataList) {
						if (!value.contains(LIST_DELIMETER)) {
							builder.append(value);
						} else {
							builder.append(value.replaceAll(LIST_DELIMETER, ""));
						}
						builder.append(LIST_DELIMETER);
					
					}
				}
			} catch (Exception e) {
				logger.error("Exception : ", e);
			}
		}
		logger.debug(Literal.LEAVING);
		return builder.toString();
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
	private void doExceptioLogging(String reference, String requets, String response, String errorDesc,
			Timestamp reqSentOn) {
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

	private String getStringValue(String key) {
		String stringValue = null;
		if(extendedMap != null) {
			stringValue = Objects.toString(extendedMap.get(key), null);
		}
		return stringValue;
	}

	private int getIntValue(String key) {
		int intValue = 0;
		try {
			if(extendedMap != null) {
				intValue = Integer.parseInt(Objects.toString(extendedMap.get(key)));
			}
		} catch (NumberFormatException e) {
			logger.error("Exception", e);
		}
		return intValue;
	}

	private boolean getBooleanValue(String key) {
		boolean booleanValue = false;
		try {
			if(extendedMap != null) {
				booleanValue = (Boolean) extendedMap.get(key);
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return booleanValue;

	}

	private BigDecimal getBigDecimalValue(String key) {
		BigDecimal bigDecimalValue = BigDecimal.ZERO;
		try {
			if(extendedMap != null) {
				bigDecimalValue = (BigDecimal) extendedMap.get(key);
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return bigDecimalValue == null ? BigDecimal.ZERO : bigDecimalValue;

	}

	private Date getDateValue(String key) {
		Date date = null;
		try {
			if (extendedMap != null) {
				date = (Date) extendedMap.get(key);
			}
		} catch (Exception e) {
			date = null;
			logger.error("Exception", e);
		}
		return date;
	}

	private int getAgeTillAppDate(Date custDob) {
		int years = 0;
		Date appDate = getAppDate();
		if (custDob != null && custDob.compareTo(appDate) < 0) {
			int months = NiyoginUtility.getMonthsBetween(appDate, custDob);
			years = months / 12;
		}
		return years;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
