package com.pennanttech.niyogin.bre.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import com.pennanttech.niyogin.utility.ExtFieldMapConstants;
import com.pennanttech.niyogin.utility.NiyoginUtility;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.BreService;
import com.pennanttech.pff.external.service.NiyoginService;

public class BreServiceImpl extends NiyoginService implements BreService {
	private static final Logger	logger				= Logger.getLogger(BreServiceImpl.class);

	private final String		extConfigFileName	= "bre";
	private String				serviceUrl;
	private Map<String, Object>	extendedMap			= null;

	/***
	 * Method for get the BRE details of the Customer and set these details to ExtendedFieldDetails.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 */
	@Override
	public AuditHeader executeBRE(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		extendedMap = financeDetail.getExtendedFieldRender().getMapValues();
		BreData breDataRequest = prepareRequestObj(financeDetail);
		Map<String, Object> validatedMap = null;
		Map<String, Object> extendedFieldMap = null;

		// logging fields Data
		reqSentOn = new Timestamp(System.currentTimeMillis());
		reference = finReference;
		extendedFieldMap = post(serviceUrl, breDataRequest, extConfigFileName);
		try {
			validatedMap = validateExtendedMapValues(extendedFieldMap);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			doLogError(e, serviceUrl, breDataRequest);
			throw new InterfaceException("9999", e.getMessage());
		}
		
		validatedMap.put("BREREQSEND", true);
		// success case logging
		doInterfaceLogging(breDataRequest, finReference);
		prepareResponseObj(validatedMap, financeDetail);
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
		extendedMap = financeDetail.getExtendedFieldRender().getMapValues();
		BreData breData = new BreData();
		breData.setCif(customer.getCustCIF());
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
		application.setApplicationId(finMain.getFinReference());
		application.setBureau(prepareBureau(financeDetail));
		application.setSocialsc(prepareSocialsc(financeDetail));
		application.setAppliedLoanAmount(finMain.getFinAmount());
		logger.debug(Literal.LEAVING);
		return application;
	}

	private Bureau prepareBureau(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		Bureau bureau = new Bureau();
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		bureau.setLoanType(finMain.getFinType());
		bureau.setAgeOfCustomer(getAgeTillAppDate(customer.getCustDOB()));

		if(extendedMap!=null){
		bureau.setNoOfBusLoansOpenedL6M(getIntValue(ExtFieldMapConstants.NUMB_OF_BUS_LOANS_OPENED_IN_L6M));
		//TODO:
		bureau.setProdIndexHL(bureau.getProdIndexAL());
		bureau.setProdIndexAL(bureau.getProdIndexAL());
		bureau.setProdIndexCC(bureau.getProdIndexCC());
		bureau.setProdIndexBLOD(bureau.getProdIndexBLOD());
		bureau.setProdIndexCLPTL(bureau.getProdIndexCLPTL());
		
		bureau.setClsTotDisbAmtL12M(getBigDecimalValue(ExtFieldMapConstants.SUM_OF_DIS_AMT_OF_ALL_CLOSED_LOANS));
		bureau.setMinPctPaidUnSecL1M(getBigDecimalValue(ExtFieldMapConstants.MIN_PER_OF_AMT_REPAID_ACROSS_ALL_UNS_LOANS));
		bureau.setPctTotOvdhicrL1M(getBigDecimalValue(ExtFieldMapConstants.RATIO_OF_OVERDUE_AND_DIS_AMT_FOR_ALL_LOANS));
		bureau.setAlMaxPctPaidSecL1M(getBigDecimalValue(ExtFieldMapConstants.MAX_PER_OF_AMT_REPAID_ALL_ACTIVE_SEC_LOANS));
		bureau.setMonSinceL30pOvralL12M(getIntValue(ExtFieldMapConstants.MONTHS_SINCE_30_PLUS_DPD_IN_L12M));
		//TODO:
		bureau.setAlWorstAmtOvrdueSecL1M(bureau.getAlWorstAmtOvrdueSecL1M());
		bureau.setBalDisbAmtRatioL1M(bureau.getBalDisbAmtRatioL1M());
		
		bureau.setClsTotDisbAmt(getBigDecimalValue(ExtFieldMapConstants.SUM_OF_DIS_AMT_OF_ALL_CLOSED_LOANS));
		
		//TODO:
		bureau.setMaxDelinquencySecL12M(bureau.getMaxDelinquencySecL12M());
		bureau.setNoOf90DpdActvOvralL12M(bureau.getNoOf90DpdActvOvralL12M());
		bureau.setNoOf90DpdOverallL12M(bureau.getNoOf90DpdOverallL12M());
		bureau.setMinLoanamount(bureau.getMinLoanamount());
		bureau.setMaxLoanamount(bureau.getMaxLoanamount());
		bureau.setMinTenure(bureau.getMinTenure());
		bureau.setMaxTenure(bureau.getMaxTenure());
		
		bureau.setNoPrvsLoansAsOfAppDate(getBooleanValue(ExtFieldMapConstants.NO_PREVS_LOANS_AS_OF_APPLICATION_DATE) ? "1" : "0");
		bureau.setIsApplicant90PlusDpdinL6M(getBooleanValue(ExtFieldMapConstants.IS_APPLICANT_90_PLUS_DPD_IN_L6M) ? "1" : "0");
		bureau.setIsApplicantSubStandardinL6M(getBooleanValue(ExtFieldMapConstants.IS_APPLICANT_SUBSTANDARD_IN_L6M) ? "1" : "0");
		bureau.setIsApplicantReportedAsLossinL6M(getBooleanValue(ExtFieldMapConstants.IS_APPLICANT_REPORTED_AS_LOSS_IN_L6M) ? "1" : "0");
		bureau.setIsApplicantDoubtfulinL6M(getBooleanValue(ExtFieldMapConstants.IS_APPLICANT_DOUBTFUL_IN_L6M) ? "1" : "0");
		bureau.setIsApplicantMentionedAsSMA(getBooleanValue(ExtFieldMapConstants.IS_APPLICANT_MENTIONED_AS_SMA) ? "1" : "0");
		//TODO:
		String lstUpdDate=NiyoginUtility.formatDate(getDateValue(ExtFieldMapConstants.LAST_UPDATE_DT_IN_BUREAU), "yyyy-MM-dd");
		bureau.setLastUpdateDtInBureau(lstUpdDate==null?"1900-01-01":lstUpdDate);
		bureau.setNotenoughInfo(getBooleanValue(ExtFieldMapConstants.NOT_ENOUGH_INFO) ? "1" : "0");
		//TODO:
		bureau.setLoansTakenPostFinancialyrInBureau(bureau.getLoansTakenPostFinancialyrInBureau());
		bureau.setCurBusLoanDisbAmt(bureau.getCurBusLoanDisbAmt());
		bureau.setMaxPctPaidUnSecL1M(bureau.getMaxPctPaidUnSecL1M());
		bureau.setActTotDisbAmt(bureau.getActTotDisbAmt());
		bureau.setAlWorstCurBalOverallL1M(bureau.getAlWorstCurBalOverallL1M());
		bureau.setWorstCurBalOverallL1M(bureau.getWorstCurBalOverallL1M());
		bureau.setMaxPctPaidOvralL1M(bureau.getMaxPctPaidOvralL1M());
		bureau.setClPctTotBalHicrL1M(bureau.getClPctTotBalHicrL1M());
		bureau.setTotDisbAmt(bureau.getTotDisbAmt());
		bureau.setAlAvgCurBalOverallL1M(bureau.getAlAvgCurBalOverallL1M());
		bureau.setAlCurBalOvralL1M(bureau.getAlCurBalOvralL1M());
		bureau.setWorstCurBalSecL1M(bureau.getWorstCurBalSecL1M());
		bureau.setAvgCurBalSecL1M(bureau.getAvgCurBalSecL1M());
		bureau.setCurBalSecL1M(bureau.getCurBalSecL1M());
		bureau.setAlAvgCurBalSecL1M(bureau.getAlAvgCurBalSecL1M());
		bureau.setAlWorstCurBalSecL1M(bureau.getAlWorstCurBalSecL1M());
		bureau.setWorstCurBalUnSecL1M(bureau.getWorstCurBalUnSecL1M());
		bureau.setAvgCurBalOverallL1M(bureau.getAvgCurBalOverallL1M());
		bureau.setPctTotBalHicrL1M(bureau.getPctTotBalHicrL1M());
		bureau.setClMaxPctPaidUnSecL1M(bureau.getClMaxPctPaidUnSecL1M());
		bureau.setClMinPctPaidUnSecL1M(bureau.getClMinPctPaidUnSecL1M());
		bureau.setAvgCurBalUnSecL1M(bureau.getAvgCurBalUnSecL1M());
		bureau.setCurBalUnSecL1M(bureau.getCurBalUnSecL1M());
		bureau.setAlMinPctPaidUnSecL1M(bureau.getAlMinPctPaidUnSecL1M());
		bureau.setAlPctTotOvdHicrL1M(bureau.getAlPctTotOvdHicrL1M());
		bureau.setPctTotOvdBalL1M(bureau.getPctTotOvdBalL1M());
		bureau.setClMinPctPaidOvralL1M(bureau.getClMinPctPaidOvralL1M());
		bureau.setAlMaxPctPaidUnSecL1M(bureau.getAlMaxPctPaidUnSecL1M());
		bureau.setAgeOldestPrev(bureau.getAgeOldestPrev());
		bureau.setAlWorstCurtBalUnSecL1M(bureau.getAlWorstCurtBalUnSecL1M());
		bureau.setAlMaxPctPaidOvralL1M(bureau.getAlMaxPctPaidOvralL1M());
		bureau.setAlAvgCurBalUnSecL1M(bureau.getAlAvgCurBalUnSecL1M());
		bureau.setWorstAmtOvedueUnSecL1M(bureau.getWorstAmtOvedueUnSecL1M());
		bureau.setAvgAmtOvrdueUnSecL1M(bureau.getAvgAmtOvrdueUnSecL1M());
		bureau.setCurOverdueUnSecL1M(bureau.getCurOverdueUnSecL1M());
		bureau.setAlPctTotoVdbalL1M(bureau.getAlPctTotoVdbalL1M());
		bureau.setClWorPrevBalUnSecL1M(bureau.getClWorPrevBalUnSecL1M());
		bureau.setAlPctTotBalHicrL1M(bureau.getAlPctTotBalHicrL1M());
		bureau.setRatioActvTotLoans(bureau.getRatioActvTotLoans());
		bureau.setClMaxPctPaidovralL1M(bureau.getClMaxPctPaidovralL1M());
		bureau.setWorstAmtOvrdueOvralL1M(bureau.getWorstAmtOvrdueOvralL1M());
		bureau.setAvgAmtOvrdueOvralL1M(bureau.getAvgAmtOvrdueOvralL1M());
		bureau.setCurOverdueOvralL1M(bureau.getCurOverdueOvralL1M());
	
		bureau.setMaxUnSecDisbAmtL12M(getBigDecimalValue(ExtFieldMapConstants.MAX_DIS_AMT_ALL_UNSEC_LOANS_L12M));	
		//TODO
		bureau.setMaxSecDisbAmtL12M(bureau.getMaxSecDisbAmtL12M());
		bureau.setMaxUnSecDisbAmtL6M(bureau.getMaxUnSecDisbAmtL6M());
		bureau.setMinPctPaidOvralL1M(bureau.getMinPctPaidOvralL1M());
		bureau.setClWorPrevBalOvralL1M(bureau.getClWorPrevBalOvralL1M());
		bureau.setAlWorstAmtOvedueUnSecL1M(bureau.getAlWorstAmtOvedueUnSecL1M());
		bureau.setAlAvgAmtOvrdueUnSecL1M(bureau.getAlAvgAmtOvrdueUnSecL1M());
		bureau.setAlCurOverdueUnSecL1M(bureau.getAlCurOverdueUnSecL1M());
		bureau.setAlWorstAmtOvrdueOvralL1M(bureau.getAlWorstAmtOvrdueOvralL1M());
		bureau.setAlAvgAmtOvrdueOvralL1M(bureau.getAlAvgAmtOvrdueOvralL1M());
		bureau.setAlCurOverdueOvralL1M(bureau.getAlCurOverdueOvralL1M());
		bureau.setTotPrevLoans(bureau.getTotPrevLoans());
		bureau.setMaxSecDisbAmtL6M(bureau.getMaxSecDisbAmtL6M());
		bureau.setAlMinPctPaidOvralL1M(bureau.getAlMinPctPaidOvralL1M());
		bureau.setClAvgCurBalUnSecL1M(bureau.getClAvgCurBalUnSecL1M());
		bureau.setClCurBalUnSecL1M(bureau.getClCurBalUnSecL1M());
		bureau.setAlMinPctPaidSecL1M(bureau.getAlMinPctPaidSecL1M());
		bureau.setMaxDelinQuencyUnSecL12M(bureau.getMaxDelinQuencyUnSecL12M());
		bureau.setMinPctPaidSecL1M(bureau.getMinPctPaidSecL1M());
		bureau.setMaxUnSecDisbAmtL3M(bureau.getMaxUnSecDisbAmtL3M());
		bureau.setMaxSecDisbAmtL3M(bureau.getMaxSecDisbAmtL3M());
		bureau.setNoOfActvLoansOnDisbdt(bureau.getNoOfActvLoansOnDisbdt());
		bureau.setClAvgCurBalOverallL1M(bureau.getClAvgCurBalOverallL1M());
		bureau.setClCurBalOvralL1M(bureau.getClCurBalOvralL1M());
		bureau.setNoOfClosedLoansL12M(bureau.getNoOfClosedLoansL12M());
		bureau.setMaxDelinQuencyOverallL12M(bureau.getMaxDelinQuencyOverallL12M());
		bureau.setClPctTotOvdBalL1M(bureau.getClPctTotOvdBalL1M());
		bureau.setMaxPctPaidSecL1M(bureau.getMaxPctPaidSecL1M());
		bureau.setAgeLattestprev(bureau.getAgeLattestprev());
		bureau.setMonSinceL30pOvralL6M(bureau.getMonSinceL30pOvralL6M());
		bureau.setClWorstCurBalUnSecL1M(bureau.getClWorstCurBalUnSecL1M());
		bureau.setNoOfClosedLoansL6M(bureau.getNoOfClosedLoansL6M());
		bureau.setNoOf30DpdOverallL12M(bureau.getNoOf30DpdOverallL12M());
		bureau.setMaxUnSecDisbAmtL1M(bureau.getMaxUnSecDisbAmtL1M());
		bureau.setMaxDelinQuencyOverallL6M(bureau.getMaxDelinQuencyOverallL6M());
		bureau.setNoOf30DpdOverallL6M(bureau.getNoOf30DpdOverallL6M());
		bureau.setClWorstCurBalOverallL1M(bureau.getClWorstCurBalOverallL1M());
		bureau.setScore(bureau.getScore());
		bureau.setAssetClassification(bureau.getAssetClassification());

		bureau.setTotalEnquiries(getIntValue(ExtFieldMapConstants.NO_OF_ENQUIRES));
		//TODO:
		bureau.setLiveTradelines(bureau.getLiveTradelines());
		
		bureau.setRestructuredFlag(getBooleanValue(ExtFieldMapConstants.RESTRUCTURED_FLAG) ? "1" : "0");
		bureau.setSfFlag(getBooleanValue(ExtFieldMapConstants.SUIT_FILED_FLAG) ? "1" : "0");
		bureau.setWdFlag(getBooleanValue(ExtFieldMapConstants.WILLFUL_DEFAULTER_FLAG) ? "1" : "0");
		bureau.setWoFlag(getBooleanValue(ExtFieldMapConstants.WRITE_OFF_FLAG) ? "1" : "0");
		bureau.setSettledFlag(getBooleanValue(ExtFieldMapConstants.SETTLED_FLAG_FLAG) ? "1" : "0");
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
		applicant.setBusiness(oreoareBusiness(financeDetail));
		applicant.setPerfios(preparePerfios(financeDetail));
		logger.debug(Literal.LEAVING);
		return applicant;
	}

	private DeMogs prepareDemogs(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		DeMogs deMogs = new DeMogs();
		//TODO:
		deMogs.setDateOfInc(NiyoginUtility.formatDate(customer.getCustDOB(), "yyyy-MM-dd"));
		deMogs.setTypeOfIndustry(customer.getCustIndustry());
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		if (addressList != null && !addressList.isEmpty()) {
			CustomerAddres address = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_OFF);
			deMogs.setRegisteredOffcPincode(address.getCustAddrZIP());
			//TODO:
			deMogs.setOperationalOffcPincode(address.getCustAddrZIP());
			deMogs.setZipCode(address.getCustAddrZIP());
		}
		//TODO:
		deMogs.setGstin("");
		deMogs.setCategoryOfApplicant(customer.getCustTypeCode());
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		deMogs.setPanNumber(NiyoginUtility.getDocumentNumber(documentList, InterfaceConstants.DOC_TYPE_PAN));
		if(extendedMap!=null){
			deMogs.setResidenceTypeOfMDorPROPTRYorMNGNGPARTNER(getStringValue(ExtFieldMapConstants.BUSINESS_PREMISES_CUSTOMER));	
		}
		deMogs.setMobileNumber(NiyoginUtility.getPhoneNumber(customerDetails.getCustomerPhoneNumList(),
				InterfaceConstants.PHONE_TYPE_OFF));
		List<CustomerEMail> emailList = customerDetails.getCustomerEMailList();
		if (emailList != null && !emailList.isEmpty()) {
		deMogs.setEmail(NiyoginUtility.getHignPriorityEmail(emailList, 5));
		}
		deMogs.setApplicantAdhaar(NiyoginUtility.getDocumentNumber(documentList, InterfaceConstants.DOC_TYPE_UID));
		deMogs.setUdyogadhaar(" ");
		deMogs.setYrsAtCurResidencePROPorMPorMDetc(getIntValue(ExtFieldMapConstants.YR_CURRENT_RESIDENCE_CUSTOMER));
		logger.debug(Literal.LEAVING);
		return deMogs;
	}

	private Financials prepareFinancials(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		Financials financials = new Financials();
		financials.setSalesYr1(getBigDecimalValue(ExtFieldMapConstants.TURN_OVER_YR1));
		financials.setSalesYr2(getBigDecimalValue(ExtFieldMapConstants.TURN_OVER_YR2));
		//TODO:
		financials.setNetSalesOrReceiptsGrossReturns(financials.getNetSalesOrReceiptsGrossReturns());
		
		financials.setExpenseYr1(getBigDecimalValue(ExtFieldMapConstants.EXPENSE_YR1));
		financials.setExpenseYr2(getBigDecimalValue(ExtFieldMapConstants.EXPENSE_YR1));
		//TODO:
		financials.setNetPurchasesGrossReturns(BigDecimal.ZERO);
		financials.setDirectExpenses(BigDecimal.ZERO);
		financials.setIndirectExpensesSellingAndAdminAndGeneral(BigDecimal.ZERO);
		financials.setQuasiInterestFriendsOrRelativesIfAny(BigDecimal.ZERO);
		
		financials.setDepreciationYr1(getBigDecimalValue(ExtFieldMapConstants.DEPRECIATION_YR1));
		financials.setDepreciationYr2(getBigDecimalValue(ExtFieldMapConstants.DEPRECIATION_YR2));
		//TODO:
		financials.setDepreciation(BigDecimal.ZERO);
		financials.setInterestToBanksOrFinancialInstitutionsOrFinanciers(BigDecimal.ZERO);
		
		financials.setInterestOnCapitalToPartners(getBigDecimalValue(ExtFieldMapConstants.INTERST_CAPTIAL_PATNER_YR1));
		financials.setPartnersOrDirectorsRemuneration(getBigDecimalValue(ExtFieldMapConstants.PARTNERS_DIRECTORS_REMUN_YR1));
		financials.setIncomeTax(getBigDecimalValue(ExtFieldMapConstants.INCOME_TAX_YR1));
		//TODO:
		financials.setNetProfitOrLoss(BigDecimal.ZERO);
		
		financials.setEquityAndPreferrenceShareCapital(getBigDecimalValue(ExtFieldMapConstants.EQUITYSHARECAPTIAL_YR1));
		financials.setQuasiEquityDirectorsFriendsAndRelatives(getBigDecimalValue(ExtFieldMapConstants.QUASI_EQUITY_YR1));
		//TODO:
		financials.setReservesAndSurPlus(BigDecimal.ZERO);
		financials.setBorrowingFromGroupCompanies(BigDecimal.ZERO);
		financials.setTotalLoans(BigDecimal.ZERO);
		financials.setSundryCreditorsForTradeAndExpensesBillsPayable(BigDecimal.ZERO);
		
		financials.setFixedassetYr1(getBigDecimalValue(ExtFieldMapConstants.FIXED_ASSEST_YR1));
		financials.setFixedassetYr2(getBigDecimalValue(ExtFieldMapConstants.FIXED_ASSEST_YR2));
		//TODO:
		financials.setFixedAssetsNetBlock(BigDecimal.ZERO);
		
		financials.setSundryDebtorsLessThan6M(getBigDecimalValue(ExtFieldMapConstants.SUNDRY_DBTRS_LESSTHAN_6M_YR1));
		financials.setSundrydebtorsgreatorthan6M(getBigDecimalValue(ExtFieldMapConstants.SUNDRY_DBTRS_LESSTHAN_6M_YR1));
		//TODO:
		financials.setItReturnFilingDate("1990-01-01");
		financials.setLiveFinancialObligationsInEmiCcodBaleetc(BigDecimal.ZERO);
		
		financials.setSumOfEmiAllLoans(getBigDecimalValue(ExtFieldMapConstants.SUM_EMI_ALL_LOANS));
		//TODO:
		financials.setInterestObligation(BigDecimal.ZERO);
		financials.setCashCreditorOverdraft(BigDecimal.ZERO);
		financials.setDbtPosOfAlTheOutStndgLoansWhichAreNotClsInTheNext3M(BigDecimal.ZERO);
		financials.setEquityTotAanGiblenetWorth(BigDecimal.ZERO);
		
		financials.setPostFundingDSCR(getBigDecimalValue(ExtFieldMapConstants.POST_FUNDING_DSCR));
		financials.setCurrentRatio(getBigDecimalValue(ExtFieldMapConstants.CURRENT_RATIO));
		financials.setWorkingCapitalGap(getBigDecimalValue(ExtFieldMapConstants.WRKNG_CAPITAL_CYCLE_GAP));
		financials.setTurnOverYr1(getBigDecimalValue(ExtFieldMapConstants.TURN_OVER_YR1));
		financials.setTurnOverYr2(getBigDecimalValue(ExtFieldMapConstants.TURN_OVER_YR1));
		//TODO:
		financials.setTurnOverGrowthLastYr(BigDecimal.ZERO);
		financials.setTurnOverGrowthLastToLastYr(BigDecimal.ZERO);
		financials.setMinYrlyTurnOver(BigDecimal.ZERO);
		financials.setAvgValueOfTurnoverOfL3Yrs(BigDecimal.ZERO);
		
		financials.setUtilizationLimitOnODorCC(getBigDecimalValue(ExtFieldMapConstants.CC_OR_OD_UTILIZATION));
		financials.setNetworth(getBigDecimalValue(ExtFieldMapConstants.NET_WORTH));
		//TODO:
		String oldDisDate=NiyoginUtility.formatDate(getDateValue(ExtFieldMapConstants.OLDEST_LOANDISBURSED_DT), "yyyy-MM-dd");
		financials.setOldestLoanDisbursedDate(oldDisDate==null?"1990-01-01":oldDisDate);
		//TODO:
		financials.setCurrentAssets(prepareCurrentAssests(financeDetail));
		financials.setCurrentLiabilittes(prepareCurrentLiabilities(financeDetail));
		financials.setRetainedEarnings(BigDecimal.ZERO);
		financials.setEquity(BigDecimal.ZERO);
		
		financials.setExistingLoanObligation(getBigDecimalValue(ExtFieldMapConstants.EXISTING_LOAN_OBLIGATION));
		//TODO:
		financials.setDebt(BigDecimal.ZERO);
		
		financials.setTotalAssets(getBigDecimalValue(ExtFieldMapConstants.TOTAL_ASSETS_YR1));
		financials.setTotalLiabilities(getBigDecimalValue(ExtFieldMapConstants.TOTAL_LIABILITIES_YR1));
		//TODO:
		financials.setBorrowings(BigDecimal.ZERO);
		
		financials.setProfitYr1(getBigDecimalValue(ExtFieldMapConstants.NET_PROFIT_YR1));
		financials.setProfitYr2(getBigDecimalValue(ExtFieldMapConstants.NET_PROFIT_YR2));
		//TODO:
		financials.setOperationProfit(BigDecimal.ZERO);
		financials.setWorkingCapitalCycle(BigDecimal.ZERO);
		
		financials.setFundsReceived(getBigDecimalValue(ExtFieldMapConstants.FUNDS_RECEIVED));
		//TODO:
		financials.setAppliedTenor(BigDecimal.ZERO);
		
		financials.setStock(getBigDecimalValue(ExtFieldMapConstants.STOCK_YR1));
		//TODO:
		financials.setLatestDebtors(BigDecimal.ZERO);
		financials.setCash(BigDecimal.ZERO);
		financials.setBank(BigDecimal.ZERO);
		
		financials.setInvestment(getBigDecimalValue(ExtFieldMapConstants.INVESTMENTS_YR1));
		financials.setShorttermBorrowings(getBigDecimalValue(ExtFieldMapConstants.SHORT_TERM_BORROWING_YR1));
		//TODO:
		financials.setSundryDebtors(BigDecimal.ZERO);
		
		financials.setProvisions(getBigDecimalValue(ExtFieldMapConstants.PROVISIONS_YR1));
		financials.setVatServiceTaxForm26as(prepareVatServiceTaxForms(financeDetail));
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
		forms.setItems(new ArrayList<BreItem>(1));
		logger.debug(Literal.LEAVING);
		return forms;
	}

	private Business oreoareBusiness(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		Business business = new Business();
		if(extendedMap!=null){
			business.setBusPremisesOwnership(getStringValue(ExtFieldMapConstants.BUSINESS_PREMISES_CUSTOMER));			
		}
		//TODO:
		business.setOrgType(" ");
		business.setNumbOfOwnersOrShareholdingPattern(business.getNumbOfOwnersOrShareholdingPattern());
		business.setOperationalBusinessVintage(BigDecimal.ZERO);
		logger.debug(Literal.LEAVING);
		return business;
	}

	private Perfios preparePerfios(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		Perfios perfios=new Perfios();
		perfios.setAvgBankBalance(getBigDecimalValue(ExtFieldMapConstants.AVERAGE_BANK_BALANCE));
		perfios.setInwardChequeReturns(getIntValue(ExtFieldMapConstants.NO_OF_INWARD_CHEQUE_BOUNCES));
		//TODO:
		perfios.setCreditConcentrationInBankStatement(BigDecimal.ZERO);
		perfios.setMinBalChargesReported(BigDecimal.ZERO);
		
		perfios.setOdccLimit(getBigDecimalValue(ExtFieldMapConstants.OD_OR_CC_LIMIT));
		
		//TODO:
		perfios.setNoOfCreditTransactions(prepareNoOfCreditTransactions(financeDetail));
		perfios.setAmtOfCreditTransactions(prepareAmtOfCreditTransactions(financeDetail));
		perfios.setNoOfDebitTransactions(perfios.getNoOfDebitTransactions());
		perfios.setAmtOfDebtTransactions(prepareAmtOfDebtTransactions());
		perfios.setNoOfCashDeposits(prepareNoOfCashDeposits());
		perfios.setAmtOfCashDeposit(prepareAmtOfCashDeposit());
		perfios.setIntOdCc(perfios.getIntOdCc());
		
		perfios.setNoOfEmiBounce(getIntValue(ExtFieldMapConstants.EMI_BOUNCES_L6M));
		//TODO:
		perfios.setNoOfCashWithdrawls(perfios.getNoOfCashWithdrawls());
		perfios.setAmtOfCashWithdrawls(BigDecimal.ZERO);
		perfios.setNoOfChequeDeposits(0);
		perfios.setAmtOfChequeDeposits(BigDecimal.ZERO);
		perfios.setTotNoOfChequeIssues(perfios.getTotNoOfChequeIssues());
		perfios.setTotAmtOfChequeIssues(BigDecimal.ZERO);
		perfios.setTotalnoOfoutwardchequebounces(perfios.getTotalnoOfoutwardchequebounces());
		perfios.setMinOdBalance(BigDecimal.ZERO);
		perfios.setMaxOdBalance(BigDecimal.ZERO);
		
		//perfios.setIssueDateForGstnDoc(getBigDecimalValue(ExtFieldMapConstants.ISSUE_DT_FOR_GSTN_DOC));
		perfios.setIssueDateForGstnDoc("1900-01-01");
		//TODO:
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
		amtOfCreditTransactions.setItems(new ArrayList<BreItem>(1));
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
			List<CustomerDetails> coApplicantCustomers = getCoApplicantsForBre(coApplicantIDs);
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
		CodeMogs codeMogs=new CodeMogs();
		Customer customer=customerDetails.getCustomer();
		codeMogs.setDob(NiyoginUtility.formatDate(customer.getCustDOB(), "yyyy-MM-dd"));
		//TODO:
		codeMogs.setMinAge(codeMogs.getMinAge());
		codeMogs.setMaxAge(codeMogs.getMaxAge());
		codeMogs.setAuthority(" ");
		
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		if (addressList != null && !addressList.isEmpty()) {
			CustomerAddres curAddress = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_CURRES);
			codeMogs.setCurrentResidencePincode(curAddress.getCustAddrZIP());
			CustomerAddres perAddress = NiyoginUtility.getCustomerAddress(addressList, InterfaceConstants.ADDR_TYPE_PERNMENT);
			codeMogs.setPermanentResidencePincode(perAddress.getCustAddrZIP());
		}
		//TODO:
		codeMogs.setRelOfScndaryCoAppWithPrimaryCoApp(" ");
		
		List<CustomerDocument> documentList = customerDetails.getCustomerDocumentsList();
		codeMogs.setCoAppPanNumber(NiyoginUtility.getDocumentNumber(documentList, InterfaceConstants.DOC_TYPE_PAN));
		//TODO:
		codeMogs.setSalToPartnerOrDirector(BigDecimal.ZERO);
		logger.debug(Literal.LEAVING);
		return codeMogs;
	}

	private CoAppBureau prepareCoAppBureau(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		CoAppBureau coAppBureau=new CoAppBureau();
		coAppBureau.setCoAppscore(" ");
		coAppBureau.setNoOfTimes30inL6M(coAppBureau.getNoOfTimes30inL6M());
		coAppBureau.setCoAppAssetClassification(" ");
		logger.debug(Literal.LEAVING);
		return coAppBureau;
	}

	private CoAppBusiness prepareCoAppBusiness(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		CoAppBusiness coAppBusiness = new CoAppBusiness();
		Map<String, Object> custFormFields = customerDetails.getExtendedFieldRender().getMapValues();
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
	
	private String getStringValue(String key) {
		return Objects.toString(extendedMap.get(key), "");
	}

	private int getIntValue(String key) {
		int intValue = 0;
		try {
			intValue = Integer.parseInt(Objects.toString(extendedMap.get(key)));
		} catch (NumberFormatException e) {
			
		}
		return intValue;
	}

	private boolean getBooleanValue(String key) {
		boolean booleanValue = false;
		try {
			booleanValue = (Boolean) extendedMap.get(key);
		} catch (Exception e) {
			
		}
		return booleanValue;

	}

	private BigDecimal getBigDecimalValue(String key) {
		BigDecimal bigDecimalValue=BigDecimal.ZERO;
		try {
			bigDecimalValue = (BigDecimal) extendedMap.get(key);
		} catch (Exception e) {
		
		}
		return bigDecimalValue==null?BigDecimal.ZERO:bigDecimalValue;

	}

	private Date getDateValue(String key) {
		Date date;
		try {
			date = (Date) extendedMap.get(key);
		} catch (Exception e) {
			date = null;
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
