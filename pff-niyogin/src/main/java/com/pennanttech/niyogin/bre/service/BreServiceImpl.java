package com.pennanttech.niyogin.bre.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennanttech.niyogin.bre.model.Applicant;
import com.pennanttech.niyogin.bre.model.ApplicantDetails;
import com.pennanttech.niyogin.bre.model.ApplicantFinancials;
import com.pennanttech.niyogin.bre.model.Business;
import com.pennanttech.niyogin.bre.model.CoApplicant;
import com.pennanttech.niyogin.bre.model.Perfios;
import com.pennanttech.niyogin.bre.model.BreData;
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
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		extendedMap = financeDetail.getExtendedFieldRender().getMapValues();
		BreData breDataReq = new BreData();
		//TODO:
		breDataReq.setCif(customer.getCustID());
		breDataReq.setApplicationId(finMain.getFinReference());
		breDataReq.setAppDate(NiyoginUtility.formatDate(finMain.getFinContractDate(), "yyyy-MM-dd"));

		if (extendedMap != null) {
			breDataReq.setNoOfBusLoansOpenedLast6m(
					getIntegerValue(ExtFieldMapConstants.NUMB_OF_BUS_LOANS_OPENED_IN_L6M));
			breDataReq.setPcttotovdhicrl1m(
					getBigDecimalValue(ExtFieldMapConstants.RATIO_OF_OVERDUE_AND_DIS_AMT_FOR_ALL_LOANS));
			breDataReq.setMaxPerAmtPaidSecLn1m(
					getBigDecimalValue(ExtFieldMapConstants.MAX_PER_OF_AMT_REPAID_ALL_ACTIVE_SEC_LOANS));
			breDataReq.setMonsincel30povrall12m(
					getIntegerValue(ExtFieldMapConstants.MONTHS_SINCE_30_PLUS_DPD_IN_L12M));
			breDataReq.setMinPerPaidUnsecLn1m(
					getBigDecimalValue(ExtFieldMapConstants.MIN_PER_OF_AMT_REPAID_ACROSS_ALL_UNS_LOANS));
			breDataReq.setClsoesLnDisAmt(
					getBigDecimalValue(ExtFieldMapConstants.SUM_OF_DIS_AMT_OF_ALL_CLOSED_LOANS));
			breDataReq.setMaxUnSecDisbamt12m(
					getBigDecimalValue(ExtFieldMapConstants.MAX_DIS_AMT_ALL_UNSEC_LOANS_L12M));
			breDataReq.setTotalEnquiries(getIntegerValue(ExtFieldMapConstants.NO_OF_ENQUIRES));

			//TODO:
			breDataReq.setLiveTradelines("0");

			breDataReq.setRestructuredFlag(getBooleanValue(ExtFieldMapConstants.RESTRUCTURED_FLAG) ? "1" : "0");
			breDataReq.setSfFlag(getBooleanValue(ExtFieldMapConstants.SUIT_FILED_FLAG) ? "1" : "0");
			breDataReq.setWdFlag(getBooleanValue(ExtFieldMapConstants.WILLFUL_DEFAULTER_FLAG) ? "1" : "0");
			breDataReq.setWoFlag(getBooleanValue(ExtFieldMapConstants.WRITE_OFF_FLAG) ? "1" : "0");
			breDataReq.setSettledFlag(getBooleanValue(ExtFieldMapConstants.SETTLED_FLAG_FLAG) ? "1" : "0");
			breDataReq.setIsApplicant90PlusDpdInLastSixMonths(
					getBooleanValue(ExtFieldMapConstants.IS_APPLICANT_90_PLUS_DPD_IN_L6M) ? "Y" : "N");

			//TODO"
			breDataReq.setLoansTakenPostFinYrInBureau("0");
			breDataReq.setMaximumTenure(getStringValue(ExtFieldMapConstants.MAXIMUM_TENURE));
			breDataReq.setProdIndexal(getStringValue(ExtFieldMapConstants.PRODUCTINDEX_AL));
			breDataReq.setProdIndexblod(getStringValue(ExtFieldMapConstants.PRODUCTINDEX_BLOD));
			breDataReq.setProdIndexcc(getStringValue(ExtFieldMapConstants.PRODUCTINDEX_CC));
			breDataReq.setProdIndexclptl(getStringValue(ExtFieldMapConstants.PRODUCTINDEX_CLPTL));
			breDataReq.setProdIndexHL(getStringValue(ExtFieldMapConstants.PRODUCTINDEX_HL));

			breDataReq.setApplicant(prepareApplicant(financeDetail));
			breDataReq.setCoApplicants(prepareCoApplicants(financeDetail));
		}

		logger.debug(Literal.LEAVING);
		return breDataReq;
	}

	/**
	 * Method for prepare the Applicant request object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private Applicant prepareApplicant(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		Applicant applicant = new Applicant();
		applicant.setDetails(prepareApplicantDetails(financeDetail));
		applicant.setFinancials(prepareFinancials(financeDetail));
		applicant.setBusiness(prepareApplicantBusiness(financeDetail));
		applicant.setPerfios(preparePerfios(financeDetail));
		logger.debug(Literal.LEAVING);
		return applicant;
	}

	/**
	 * Method for prepare the ApplicantDetails request object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private ApplicantDetails prepareApplicantDetails(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		ApplicantDetails applicantDetails = new ApplicantDetails();
		applicantDetails.setTypeOfIndustry(customer.getCustSector());
		applicantDetails.setResTypeOfMdOrPropreitorOrManagingPartner(
				getStringValue(ExtFieldMapConstants.BUSINESS_PREMISES_CUSTOMER));
		applicantDetails.setYrsAtCurrentResPropOrMpOrMdEtc(
				getIntegerValue(ExtFieldMapConstants.YR_CURRENT_RESIDENCE_CUSTOMER));
		if (customerDetails.getAddressList() != null && !customerDetails.getAddressList().isEmpty()) {
			CustomerAddres addres = null;
			addres = NiyoginUtility.getCustomerAddress(customerDetails.getAddressList(),
					InterfaceConstants.ADDR_TYPE_OFF);
			applicantDetails.setRegOfficePincode(addres.getCustAddrZIP());
		}
		logger.debug(Literal.LEAVING);
		return applicantDetails;
	}

	/**
	 * Method for prepare the ApplicantFinancials request object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private ApplicantFinancials prepareFinancials(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		ApplicantFinancials financials = new ApplicantFinancials();
		if (extendedMap != null) {
			financials.setVatOrServiceTaxOrForm26As(
					getBigDecimalValue(ExtFieldMapConstants.VAT_OR_SERVICETAX_OR_FORM26AS));

			financials.setReservesAndSurplus("");
			financials.setIntrestObligation("");
			financials.setCurrentRatio("");
			financials.setTurnOverYr2("");
			financials.setTurnOverGrowthLastYr("");
			financials.setTurnOverGrowthLastToLastYr("");

			financials.setDepreciationYr1(getBigDecimalValue(ExtFieldMapConstants.DEPRECIATION_YR1));
			financials.setDepreciationYr2(getBigDecimalValue(ExtFieldMapConstants.DEPRECIATION_YR2));

			financials.setCurrentAssets("");
			financials.setCurrentLiabilittes("");

			financials.setSumAllEMILoans(getBigDecimalValue(ExtFieldMapConstants.SUM_EMI_ALL_LOANS));
			financials.setSalesYr1(getBigDecimalValue(ExtFieldMapConstants.TURN_OVER_YR1));
			financials.setSalesYr2(getBigDecimalValue(ExtFieldMapConstants.TURN_OVER_YR2));

			financials.setEquity("");

			financials.setEquityAndPreferrenceShareCapital(
					getBigDecimalValue(ExtFieldMapConstants.EQUITYSHARECAPTIAL_YR1));
			financials.setTotalAssets(getBigDecimalValue(ExtFieldMapConstants.TOTAL_ASSETS_YR1));
			financials.setExpenseYr1(getBigDecimalValue(ExtFieldMapConstants.EXPENSE_YR1));
			financials.setExpenseYr2(getBigDecimalValue(ExtFieldMapConstants.Expense_Yr2));
			financials.setTotalLiabilities(getBigDecimalValue(ExtFieldMapConstants.TOTAL_LIABILITIES_YR1));
			financials.setProfitYr1(getBigDecimalValue(ExtFieldMapConstants.NET_PROFIT_YR1));
			financials.setProfitYr2(getBigDecimalValue(ExtFieldMapConstants.NET_PROFIT_YR2));
			financials.setFixedAssetYr1(getBigDecimalValue(ExtFieldMapConstants.FIXED_ASSEST_YR1));
			financials.setFixedAssetYr2(getBigDecimalValue(ExtFieldMapConstants.FIXED_ASSEST_YR2));
			financials.setIncomeTax(getBigDecimalValue(ExtFieldMapConstants.INCOME_TAX_YR1));

			financials.setTotalLoans("");
			financials.setLiveFinObligationsInEMICodBaletc("");
			financials.setWorkingCapitalCycle("");

			financials.setInterestOnCapitalToPartners(
					getBigDecimalValue(ExtFieldMapConstants.INTERST_CAPTIAL_PATNER_YR1));

			financials.setNetProfitOrLoss("");

			financials.setUtilizationLimitOnodorcc(getBigDecimalValue(ExtFieldMapConstants.CC_OR_OD_Utilization));
			financials.setQuasiEquityDirectorsFrndsAndRelatives(
					getBigDecimalValue(ExtFieldMapConstants.QUASI_EQUITY_YR1));
		}
		logger.debug(Literal.LEAVING);
		return financials;
	}

	/**
	 * Method for prepare the Business request object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private Business prepareApplicantBusiness(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		Business business = new Business();
		//TODO:
		business.setBusPremisesOwnership("");
		business.setOrgType("");
		business.setNumberOfOwnersOrShareholdingPattern("");
		business.setOperationalBusVintage("");
		logger.debug(Literal.LEAVING);
		return business;
	}

	/**
	 * Method for prepare the Perfious request object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private Perfios preparePerfios(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		Perfios perfios = new Perfios();
		if (extendedMap != null) {
			perfios.setAvgBankBalance(getBigDecimalValue(ExtFieldMapConstants.AVERAGE_BANK_BALANCE));
			perfios.setInwardChequeReturns(getIntegerValue(ExtFieldMapConstants.NO_OF_INWARD_CHEQUE_BOUNCES));
			perfios.setOdccLimit(getBigDecimalValue(ExtFieldMapConstants.OD_OR_CC_LIMIT));
			perfios.setAmotOfCreditTransactions(
					getIntegerValue(ExtFieldMapConstants.TOTAL_NO_OF_DBT_TRANSACTIONS));

			//TODO:
			perfios.setIntoDcc(getStringValue(ExtFieldMapConstants.INTEREST_ON_OD_CC));
		}
		logger.debug(Literal.LEAVING);
		return perfios;
	}

	/**
	 * Method For prepare the Coapplicant's request object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private List<CoApplicant> prepareCoApplicants(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		List<CoApplicant> coapplicantList = new ArrayList<>();

		List<JointAccountDetail> coapplicants = financeDetail.getJountAccountDetailList();
		if (coapplicants != null && !coapplicants.isEmpty()) {
			List<Long> coApplicantIDs = new ArrayList<Long>(1);
			for (JointAccountDetail coApplicant : coapplicants) {
				coApplicantIDs.add(coApplicant.getCustID());
			}
			List<CustomerDetails> coApplicantCustomers = getCoApplicants(coApplicantIDs);
			for (CustomerDetails customerDetails : coApplicantCustomers) {
				Customer customer = customerDetails.getCustomer();
				CoApplicant coApplicant = new CoApplicant();
				coApplicant.setDob(NiyoginUtility.formatDate(customer.getCustDOB(), "yyyy-MM-dd"));
				//TODO:
				coApplicant.setSalaryToPartnerOrDirector("");
				coApplicant.setMaxWorkExperience(0);
				coapplicantList.add(coApplicant);
			}
		}

		logger.debug(Literal.LEAVING);
		return coapplicantList;
	}

	private String getStringValue(String key) {
		return Objects.toString(extendedMap.get(key), "");
	}

	private int getIntegerValue(String key) {
		Integer intValue;
		try {
			intValue = Integer.parseInt(String.valueOf(extendedMap.get(key)));
		} catch (NumberFormatException e) {
			intValue = null;
		}
		return intValue;
	}

	private boolean getBooleanValue(String key) {
		Boolean booleanValue;
		try {
			booleanValue = (Boolean) extendedMap.get(key);
		} catch (Exception e) {
			booleanValue = null;
		}
		return booleanValue;

	}

	private BigDecimal getBigDecimalValue(String key) {
		BigDecimal bigDecimalValue;
		try {
			bigDecimalValue = (BigDecimal) extendedMap.get(key);
		} catch (Exception e) {
			bigDecimalValue = null;
		}
		return bigDecimalValue;

	}
}
