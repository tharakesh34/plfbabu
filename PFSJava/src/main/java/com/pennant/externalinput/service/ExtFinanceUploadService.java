package com.pennant.externalinput.service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.jaxen.JaxenException;
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.accounts.AccountsDAO;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.applicationmaster.SplRateDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.RequestDetail;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.externalinput.ExtFinanceData;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ExtFinanceUploadService {
	private static final Logger		logger				= Logger.getLogger(ExtFinanceUploadService.class);

	private boolean					isError				= false;

	private FinanceTypeDAO			financeTypeDAO;
	private BranchDAO				branchDAO;
	private FinanceMainDAO			financeMainDAO;
	private CurrencyDAO				currencyDAO;
	private CustomerDAO				customerDAO;
	private BaseRateDAO				baseRateDAO;
	private SplRateDAO				splRateDAO;
	private AccountsDAO				accountsDAO;
	private StepPolicyDetailDAO		stepPolicyDetailDAO;
	private FinanceDetailService	financeDetailService;
	private AccountInterfaceService	accountInterfaceService;
	private AccountEngineExecution	engineExecution;
	private FinTypeAccountingDAO	finTypeAccountingDAO;

	private BigDecimal				zeroValue			= BigDecimal.ZERO;
	private FinanceMain				finMain;
	private FinanceMain				oldFinMain;
	private FinanceDisbursement		disbursementDetails	= new FinanceDisbursement();
	private FinScheduleData			finScheduleData		= new FinScheduleData();
	private long					userID				= 1L;
	private String					userLangauge;
	private RequestDetail			requestDetail;

	FinanceType						finType				= null;
	SplRate							splRate				= null;
	BaseRate						baseRate			= null;
	Customer						customer			= null;

	/**
	 * Method to process External finance Detail through Excel file
	 * 
	 * @param finInput
	 */
	public String procExternalFinance(InputStream finInput, LoggedInUser usrDetails) {
		logger.debug("Entering");
		userID = usrDetails.getUserId();
		userLangauge = usrDetails.getLanguage();
		String status = "";
		int rcdCount = 0;
		int successRcdCount = 0;
		status = successRcdCount + " records has been processed out of " + rcdCount;

		try {
			System.out.println("======================        START     ==========================");
			System.out.println("==================================================================");
			System.out.println("----> START EXTENDED FINANCE UPLOAD  --------> :: "
					+ DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
			POIFSFileSystem finFileSystem = new POIFSFileSystem(finInput);
			HSSFWorkbook finWorkBook = new HSSFWorkbook(finFileSystem);
			HSSFSheet finSheet = finWorkBook.getSheetAt(0);
			@SuppressWarnings("rawtypes")
			Iterator rowIter = finSheet.rowIterator();
			ExtFinanceData extFinData;
			HSSFRow finRow;

			while (rowIter.hasNext()) {
				finRow = (HSSFRow) rowIter.next();
				if (finRow.getRowNum() <= 1) {
					continue;
				}

				// New Object creation on each Record
				finScheduleData = new FinScheduleData();

				if (!"END".equals(finRow.getCell(0).toString())) {
					rcdCount = rcdCount + 1;
					extFinData = new ExtFinanceData();
					extFinData = prepareExtFinanceData(finRow, extFinData);
					extFinData = validateExtFinanceData(extFinData, new FinanceMain());

					if ("E".equals(extFinData.getRecordStatus())) {
						// TODO
						logger.fatal("Need to Decide");
					} else {
						// Save Finance Data into DataBase
						successRcdCount = successRcdCount + 1;
						processFinanceData(usrDetails, extFinData);
					}
				} else {
					status = successRcdCount + " records has been processed out of " + rcdCount;
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			return status;
		}
		logger.debug("Leaving");
		return status;
	}

	private void processFinanceData(LoggedInUser userDetails, ExtFinanceData extFinData) throws IOException,
			InterfaceException, JaxenException {
		logger.debug("Entering");

		finScheduleData.getDisbursementDetails().clear();
		getFinMain().setRecordType(PennantConstants.RECORD_TYPE_NEW);
		getFinMain().setWorkflowId(0);
		getFinMain().setNewRecord(true);
		getFinMain().setUserDetails(userDetails);

		finScheduleData.setFinanceMain(getFinMain());
		disbursementDetails.setDisbDate(getFinMain().getFinStartDate());
		disbursementDetails.setDisbAmount(getFinMain().getFinAmount());
		finScheduleData.getDisbursementDetails().add(disbursementDetails);
		finScheduleData.setErrorDetails(new ArrayList<ErrorDetail>());
		finScheduleData.setRepayInstructions(new ArrayList<RepayInstruction>());

		// Step Policy Details
		if (finType.isStepFinance()) {
			List<StepPolicyDetail> stepPolicyList = getStepPolicyDetailDAO().getStepPolicyDetailListByID(
					finType.getDftStepPolicy(), "_AView");
			finScheduleData.resetStepPolicyDetails(stepPolicyList);

			if (finMain.isAllowGrcPeriod()) {
				finScheduleData.getStepPolicyDetails().get(0).setInstallments(12);
				finScheduleData.getStepPolicyDetails().get(1).setInstallments(12);
				finScheduleData.getStepPolicyDetails().get(2).setInstallments(12);
				finScheduleData.getStepPolicyDetails().get(3).setInstallments(12);
				finScheduleData.getFinanceMain().setGraceTerms(12);
			} else {
				finScheduleData.getStepPolicyDetails().get(0).setInstallments(15);
				finScheduleData.getStepPolicyDetails().get(1).setInstallments(15);
				finScheduleData.getStepPolicyDetails().get(2).setInstallments(15);
				finScheduleData.getStepPolicyDetails().get(3).setInstallments(15);
			}

			finScheduleData.getFinanceMain().setStepFinance(true);
			finScheduleData.getFinanceMain().setStepPolicy(finType.getDftStepPolicy());
		}

		finScheduleData = ScheduleGenerator.getNewSchd(finScheduleData);
		if (finScheduleData.getFinanceScheduleDetails().size() != 0) {
			
			finScheduleData = ScheduleCalculator.getCalSchd(finScheduleData, BigDecimal.ZERO);
			finScheduleData.setSchduleGenerated(true);
		}

		// Reset Data
		finScheduleData.getFinanceMain().setEqualRepay(this.oldFinMain.isEqualRepay());
		finScheduleData.getFinanceMain().setCalculateRepay(this.oldFinMain.isCalculateRepay());
		finScheduleData.getFinanceMain().setRecalType(this.oldFinMain.getRecalType());
		finScheduleData.getFinanceMain().setLastRepayDate(this.oldFinMain.getFinStartDate());
		finScheduleData.getFinanceMain().setLastRepayPftDate(this.oldFinMain.getFinStartDate());
		finScheduleData.getFinanceMain().setLastRepayRvwDate(this.oldFinMain.getFinStartDate());
		finScheduleData.getFinanceMain().setLastRepayCpzDate(this.oldFinMain.getFinStartDate());

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		int lastIndx = finScheduleData.getFinanceScheduleDetails().size() - 1;
		BigDecimal lastInstPft = finScheduleData.getFinanceScheduleDetails().get(lastIndx).getProfitSchd();

		// VALIDATE RESULTS

		finScheduleData.getFinanceMain().setFinRemarks("SUCCESS");

		if (!extFinData.getExpTotalPft().equals(financeMain.getTotalProfit())) {
			finScheduleData.getFinanceMain().setFinRemarks("FAILED-Mismatch in Total Profit");
		} else if (!extFinData.getExpFirstInst().equals(financeMain.getFirstRepay())) {
			finScheduleData.getFinanceMain().setFinRemarks("FAILED-Mismatch in First Repay");
		} else if (!extFinData.getExpLastInst().equals(financeMain.getLastRepay())) {
			finScheduleData.getFinanceMain().setFinRemarks("FAILED-Mismatch in Last Repay");
		} else if (!extFinData.getExpLastInstPft().equals(lastInstPft)) {
			finScheduleData.getFinanceMain().setFinRemarks("FAILED-Mismatch in Last Repay Schd. Profit");
		} /*
		 * else if (!extFinData.getExpRateAtStart().equals(financeMain.getGrcPftRate())) {
		 * finScheduleData.getFinanceMain().setFinRemarks("FAILED-Mismatch in Grace Profit Rate"); } else if
		 * (!extFinData.getExpRateAtGrcEnd().equals(financeMain.getRepayProfitRate())) {
		 * finScheduleData.getFinanceMain().setFinRemarks("FAILED-Mismatch in Repay Profit Rate"); }
		 */
		finScheduleData.setFinanceMain(financeMain);

		FinanceDetail afinanceDetail = new FinanceDetail();
		afinanceDetail.setUserAction("");
		afinanceDetail.setExtSource(true);

		// prepare accounting
		afinanceDetail.setReturnDataSetList(processAccounting(finScheduleData));
		afinanceDetail.setAccountingEventCode("");

		finScheduleData.setFinReference(getFinMain().getFinReference());
		afinanceDetail.setFinScheduleData(finScheduleData);
		finScheduleData.getFinanceMain().setMigratedFinance(true);

		AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, afinanceDetail);
		AuditHeader auditHeader = new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null,
				null, auditDetail, getFinMain().getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
		getFinanceDetailService().doApprove(auditHeader, false);

		logger.debug("Leaving");
	}

	private List<ReturnDataSet> processAccounting(FinScheduleData data) {
		logger.debug(" Entering ");

		//FIXME: PV: 25 APR 17: DOUBT WHETHER IT WORKS OR NOT. REQUIRES CLEANUP WHEN IT IS USED
		// Amount Code details calculation
		AEEvent aeEvent = new AEEvent();
		aeEvent = AEAmounts.procAEAmounts(data.getFinanceMain(), data.getFinanceScheduleDetails(),
				new FinanceProfitDetail(), AccountEventConstants.ACCEVENT_ADDDBSP, data.getFinanceMain()
						.getFinStartDate(), data.getFinanceMain().getFinStartDate());

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		aeEvent.setDataMap(dataMap);

		// Building Account Entry Details
		try {
			aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
			return aeEvent.getReturnDataSet();
		} catch (Exception e) {
			logger.debug(e);
		}
		return null;

	}

	/***
	 * Method to set excel data to external finance bean.
	 * 
	 * @param finRow
	 * @param extFinData
	 * */

	/***
	 * Method to set excel data to external finanace bean.
	 * 
	 * @param finRow
	 * @param extFinData
	 * */
	public ExtFinanceData prepareExtFinanceData(HSSFRow finRow, ExtFinanceData extFinData) {
		logger.debug("Entering");

		System.out.println("----> START PREPARING EXT FINANCE DATA TO FINANCE MAIN OBJ  --------> :: "
				+ DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));

		BigDecimal decimal100 = BigDecimal.valueOf(100);
		String strYES = "YES";

		// Finance Type
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(1)))) {
			extFinData.setFinType(getValue(finRow.getCell(1)));
		}

		try {
			finType = getFinanceTypeDAO().getFinanceTypeByID(extFinData.getFinType(), "");
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		// Finance Reference
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(2)))) {
			extFinData.setFinReference(getValue(finRow.getCell(2)));
		}

		// Finance Currency
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(3)))) {
			extFinData.setFinCcy(getValue(finRow.getCell(3)));
		}

		// Rate Basis
		extFinData.setRepayRateBasis(getValue(finRow.getCell(4)));

		// Repayment Schedule Method
		extFinData.setScheduleMethod(getValue(finRow.getCell(5)));

		// Allow Grace

		if (StringUtils.trimToEmpty(finRow.getCell(6).toString()).equals(strYES)) {
			extFinData.setAllowGrcPeriod(true);

			if (StringUtils.trimToEmpty(finRow.getCell(8).toString()).equals(strYES)) {
				extFinData.setAllowGrcCpz(true);
			} else {
				extFinData.setAllowGrcCpz(false);
			}
		} else {
			extFinData.setAllowGrcPeriod(false);
			extFinData.setAllowGrcCpz(false);
		}

		extFinData.setScheduleMethod(getValue(finRow.getCell(5)));

		// Grace Schedule Method If applicable
		extFinData.setGrcSchdMthd(getValue(finRow.getCell(7)));

		// Profit Days Basis
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(12)))) {
			extFinData.setProfitDaysBasis(getValue(finRow.getCell(12)));
		} else {
			extFinData.setProfitDaysBasis(finType.getFinDaysCalType());
		}

		// Customer
		extFinData.setLovDescCustCIF(getValue(finRow.getCell(13)));

		try {
			Customer customer = getCustomerDAO().getCustomerByCIF(extFinData.getLovDescCustCIF(), "");
			if (customer != null) {
				if (StringUtils.trimToNull(customer.getCustDftBranch()) != null) {
					extFinData.setFinBranch(StringUtils.trim(customer.getCustDftBranch()));
				}
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		// Finance Start Date
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(14)))) {
			extFinData.setFinStartDate(finRow.getCell(14).getDateCellValue());
		}

		// Finance Amount
		extFinData.setFinAmount(getDecimalValue(finRow.getCell(15)).multiply(decimal100));

		// Finance Asset Value
		extFinData.setFinAssetValue(extFinData.getFinAmount());

		// Down Payment to Bank, Supplier and Total
		extFinData.setDpToBank(getDecimalValue(finRow.getCell(16)).multiply(decimal100));
		extFinData.setDpToSupplier(getDecimalValue(finRow.getCell(17)).multiply(decimal100));
		extFinData.setDownPayment(extFinData.getDpToBank().add(extFinData.getDpToSupplier()));

		// Requested Repayment Amount
		extFinData.setReqRepayAmount(getDecimalValue(finRow.getCell(18)).multiply(decimal100));

		if (extFinData.getReqRepayAmount().compareTo(zeroValue) > 0) {
			extFinData.setCalculateRepay(false);
			extFinData.setEqualRepay(false);
		} else {
			extFinData.setCalculateRepay(true);
			extFinData.setEqualRepay(true);
		}

		// PREPARE DEFAULT DATA

		// Grace Base Rate
		if (finType != null) {
			extFinData.setGraceBaseRate(finType.getFinGrcBaseRate());

			// Grace Special Rate
			extFinData.setGraceSpecialRate(finType.getFinGrcSplRate());

			// Grace Margin
			extFinData.setGrcMargin(finType.getFinGrcMargin());
		}

		// Grace Profit Rate
		if (extFinData.getGraceBaseRate() != null) {
			baseRate = getBaseRate(extFinData.getGraceBaseRate(), extFinData.getFinCcy());
			extFinData.setGrcPftRate(baseRate == null ? zeroValue : baseRate.getBRRate().add(zeroValue)
					.add(extFinData.getGrcMargin()));
		} else {
			extFinData.setGrcPftRate(finType.getFinGrcIntRate());
		}

		// Disbursement Account Id
		if ("AED".equals(extFinData.getFinCcy())) {
			extFinData.setDisbAccountId(extFinData.getLovDescCustCIF().concat("00001"));
		} else {
			extFinData.setDisbAccountId(extFinData.getLovDescCustCIF().concat("00002"));
		}

		// Repayments Account Id
		extFinData.setRepayAccountId(extFinData.getDisbAccountId());

		// Source ID
		extFinData.setFinSourceID("EXT");

		// Repay Profit Rate
		if (finType.getFinBaseRate() != null) {
			baseRate = getBaseRate(finType.getFinBaseRate(), finType.getFinCcy());
			extFinData.setRepayProfitRate(baseRate == null ? zeroValue : baseRate.getBRRate().add(zeroValue)
					.add(extFinData.getRepayMargin()));
		} else {
			extFinData.setRepayProfitRate(finType.getFinIntRate());
		}

		// LOAD EXPECTED RESULTS
		extFinData.setExpGracePft(round(getDecimalValue(finRow.getCell(19)).multiply(decimal100)));
		extFinData.setExpGraceCpz(round(getDecimalValue(finRow.getCell(20)).multiply(decimal100)));
		extFinData.setExpGorssGracePft(round(getDecimalValue(finRow.getCell(21)).multiply(decimal100)));
		extFinData.setExpRepayPft(round(getDecimalValue(finRow.getCell(22)).multiply(decimal100)));
		extFinData.setExpTotalPft(round(getDecimalValue(finRow.getCell(23)).multiply(decimal100)));
		extFinData.setExpFirstInst(round(getDecimalValue(finRow.getCell(24)).multiply(decimal100)));
		extFinData.setExpLastInst(round(getDecimalValue(finRow.getCell(25)).multiply(decimal100)));
		extFinData.setExpLastInstPft(round(getDecimalValue(finRow.getCell(26)).multiply(decimal100)));
		extFinData.setExpRateAtStart(getDecimalValue(finRow.getCell(27)));
		extFinData.setExpRateAtGrcEnd(getDecimalValue(finRow.getCell(28)));

		logger.debug("Leaving");
		System.out.println("----> END SETTING EXT FINANCE DATA TO FINANCE MAIN OBJ  --------> :: "
				+ DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
		return extFinData;
	}

	/*
	 * ================================================================================================================
	 * VALDIATE EXTERNAL INPUT DATA
	 * ================================================================================================================
	 */
	public ExtFinanceData validateExtFinanceData(ExtFinanceData extFinData, FinanceMain financeMain)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		System.out.println("----> START VALIDATION FINANCE UPLOAD  --------> :: "
				+ DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));

		// Validate REFERENCE
		if (extFinData.getFinReference() == null) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					new ErrorDetail("41002", "", new String[] { "Finance Reference", "" }), userLangauge).getError());
			return extFinData;
		}

		// Check whether Finance already exists with same reference and return if so
		if (getFinanceDetailService().isFinReferenceExits(extFinData.getFinReference(), "", false)) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					new ErrorDetail("30506", "", new String[] { "Finance Reference", extFinData.getFinReference() }),
					userLangauge).getError());
			return extFinData;
		}
		finScheduleData.setFinReference(extFinData.getFinReference());
		finScheduleData.setFinanceType(finType);

		financeMain.setFinReference(extFinData.getFinReference());
		financeMain.setFinType(extFinData.getFinType());
		financeMain.setFinCcy(extFinData.getFinCcy());
		financeMain.setProfitDaysBasis(extFinData.getProfitDaysBasis());

		try {

			customer = getCustomerDAO().getCustomerByCIF(extFinData.getLovDescCustCIF(), "");

			if (customer == null) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
						new ErrorDetail("41002", "", new String[] { "Customer", extFinData.getLovDescCustCIF() }),
						userLangauge).getError());
				return extFinData;
			}

			financeMain.setCustID(customer.getCustID());
			financeMain.setLovDescCustCIF(extFinData.getLovDescCustCIF());
			financeMain.setLovDescCustFName(customer.getCustFName());
			financeMain.setLovDescCustShrtName(customer.getCustShrtName());
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					new ErrorDetail("41002", "", new String[] { "Customer", extFinData.getLovDescCustCIF() }),
					userLangauge).getError());
			return extFinData;
		}

		financeMain.setFinBranch(extFinData.getFinBranch());
		financeMain.setFinStartDate(extFinData.getFinStartDate());
		financeMain.setFinContractDate(extFinData.getFinStartDate());
		financeMain.setFinAmount(extFinData.getFinAmount());
		financeMain.setDisbAccountId(extFinData.getDisbAccountId());
		financeMain.setRepayAccountId(extFinData.getRepayAccountId());
		financeMain.setDownPayment(extFinData.getDownPayment());
		financeMain.setDownPayBank(extFinData.getDpToBank());
		financeMain.setDownPaySupl(extFinData.getDpToSupplier());
		financeMain.setDownPayAccount(financeMain.getRepayAccountId());
		financeMain.setDefferments(finType.getFinDftTerms());

		if (finType.isAlwPlanDeferment()) {
			financeMain.setPlanDeferCount(finType.getPlanDeferCount());
		}

		// Collateral Reference
		financeMain.setFinCommitmentRef(StringUtils.isBlank(extFinData.getFinCommitmentRef()) ? "" : extFinData
				.getFinCommitmentRef());

		// Depreciation Frequency
		if (finType.isFinDepreciationReq()) {
			financeMain.setDepreciationFrq(finType.getFinDepreciationFrq());
			financeMain.setNextDepDate(FrequencyUtil.getNextDate(financeMain.getDepreciationFrq(), 1,
					financeMain.getFinStartDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
			financeMain.setNextDepDate(DateUtility.getDate(DateUtility.formatUtilDate(financeMain.getNextDepDate(),
					PennantConstants.dateFormat)));
		}

		financeMain.setFinIsActive(true);

		/*
		 * ------------------------------------------------------------------------------------------------------------
		 * GRACE PERIOD
		 * ------------------------------------------------------------------------------------------------------------
		 */

		if (extFinData.isAllowGrcPeriod()) {
			financeMain.setAllowGrcPeriod(true);
			validateGraceDetails(extFinData, financeMain, finType);
			financeMain.setCpzAtGraceEnd(finType.isFinIsIntCpzAtGrcEnd());
		} else {
			financeMain.setAllowGrcPeriod(false);
			financeMain.setGrcPeriodEndDate(extFinData.getFinStartDate());
		}

		/*
		 * ------------------------------------------------------------------------------------------------------------
		 * REPAY PERIOD
		 * ------------------------------------------------------------------------------------------------------------
		 */

		int terms = finType.getFinDftTerms();

		if (extFinData.isAllowGrcPeriod()) {
			terms = terms - 12;
		}

		financeMain.setNumberOfTerms(terms);
		financeMain.setFinRepaymentAmount(extFinData.getReqRepayAmount());
		financeMain.setRepayRateBasis(extFinData.getRepayRateBasis());

		// Repay Rate
		if (finType.getFinBaseRate() != null) {
			financeMain.setRepayBaseRate(finType.getFinBaseRate());
			baseRate = getBaseRate(financeMain.getRepayBaseRate(), financeMain.getFinCcy());
			financeMain.setRepayMargin(finType.getFinMargin());
			financeMain.setRepayProfitRate(baseRate == null ? zeroValue : baseRate.getBRRate().add(zeroValue)
					.add(financeMain.getRepayMargin()));
		} else {
			financeMain.setRepayProfitRate(finType.getFinIntRate());
		}

		financeMain.setScheduleMethod(extFinData.getScheduleMethod());

		// Repay profit Frequency & Date
		financeMain.setRepayPftFrq(finType.getFinDftIntFrq());

		financeMain.setNextRepayPftDate(FrequencyUtil.getNextDate(financeMain.getRepayPftFrq(), 1,
				financeMain.getGrcPeriodEndDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
		financeMain.setNextRepayPftDate(DateUtility.getDate(DateUtility.formatUtilDate(
				financeMain.getNextRepayPftDate(), PennantConstants.dateFormat)));

		// Allow Repay Review
		if (finType.isFinIsRvwAlw()) {
			financeMain.setAllowRepayRvw(finType.isFinIsRvwAlw());
			financeMain.setRepayRvwFrq(finType.getFinRvwFrq());
			financeMain.setNextRepayRvwDate(FrequencyUtil.getNextDate(financeMain.getRepayRvwFrq(), 1,
					financeMain.getGrcPeriodEndDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
			financeMain.setNextRepayRvwDate(DateUtility.getDate(DateUtility.formatUtilDate(
					financeMain.getNextRepayRvwDate(), PennantConstants.dateFormat)));
		} else {
			financeMain.setRepayRvwFrq("");
		}
		financeMain.setRvwRateApplFor(finType.getFinRvwRateApplFor());

		// Allow Repay Capitalization
		if (finType.isFinIsIntCpz()) {
			financeMain.setAllowRepayCpz(finType.isFinIsIntCpz());
			financeMain.setRepayCpzFrq(finType.getFinCpzFrq());
			financeMain.setNextRepayCpzDate(FrequencyUtil.getNextDate(financeMain.getRepayCpzFrq(), 1,
					financeMain.getGrcPeriodEndDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
			financeMain.setNextRepayCpzDate(DateUtility.getDate(DateUtility.formatUtilDate(
					financeMain.getNextRepayCpzDate(), PennantConstants.dateFormat)));
		} else {
			financeMain.setRepayCpzFrq("");
		}

		// Repay Frequency
		financeMain.setRepayFrq(finType.getFinRpyFrq());
		financeMain.setNextRepayDate(FrequencyUtil.getNextDate(financeMain.getRepayFrq(), 1,
				financeMain.getGrcPeriodEndDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
		financeMain.setNextRepayDate(DateUtility.getDate(DateUtility.formatUtilDate(financeMain.getNextRepayDate(),
				PennantConstants.dateFormat)));

		// Maturity Date
		List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(financeMain.getRepayFrq(),
				financeMain.getNumberOfTerms(), financeMain.getNextRepayDate(), "A", true, 0).getScheduleList();
		if (scheduleDateList != null) {
			Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
			financeMain.setMaturityDate(calendar.getTime());
			financeMain.setMaturityDate(DateUtility.getDate(DateUtility.formatUtilDate(financeMain.getMaturityDate(),
					PennantConstants.dateFormat)));
		}

		financeMain.setFinRepayPftOnFrq(finType.isFinRepayPftOnFrq());

		financeMain.setCalculateRepay(extFinData.isCalculateRepay());
		financeMain.setEqualRepay(extFinData.isEqualRepay());

		// OTHER DEFAULST
		financeMain.setLastRepayDate(financeMain.getFinStartDate());
		financeMain.setLastRepayPftDate(financeMain.getFinStartDate());
		financeMain.setLastRepayCpzDate(financeMain.getFinStartDate());
		financeMain.setLastRepayRvwDate(financeMain.getFinStartDate());
		financeMain.setLastDepDate(financeMain.getFinStartDate());

		// FINANCE ASSET VALUE
		financeMain.setFinAssetValue(financeMain.getFinAmount());
		financeMain.setFinCurrAssetValue(financeMain.getFinAmount());

		// Source ID
		financeMain.setFinSourceID(StringUtils.isBlank(extFinData.getFinSourceID()) ? "EXT" : extFinData
				.getFinSourceID());

		// Set recal type from finance type
		if (finType.isFinIsRvwAlw()) {
			financeMain.setRecalType(finType.getFinSchCalCodeOnRvw());
		} else {
			financeMain.setRecalType("");
		}

		// Version
		financeMain.setVersion(1);

		// User Details
		financeMain.setLastMntBy(userID);
		financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		// Record Status
		financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		setFinMain(financeMain);

		// Storing original finance main data to reset
		oldFinMain = new FinanceMain();
		BeanUtils.copyProperties(financeMain, oldFinMain);
		System.out.println("----> END VALIDATION FINANCE UPLOAD  --------> :: "
				+ DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
		return extFinData;
	}

	/*
	 * ================================================================================================================
	 * Method to validate grace period details
	 * ================================================================================================================
	 */

	private boolean validateGraceDetails(ExtFinanceData extFinData, FinanceMain financeMain, FinanceType finType) {

		logger.debug("Entering");
		// Grace Period End Date
		financeMain.setGrcPeriodEndDate(DateUtility.addMonths(financeMain.getFinStartDate(), 12));
		financeMain.setGrcRateBasis(finType.getFinGrcRateType());
		financeMain.setGrcSchdMthd(extFinData.getGrcSchdMthd());

		// Grace Base Rate
		if (finType.getFinGrcBaseRate() != null) {
			baseRate = getBaseRate(extFinData.getGraceBaseRate(), extFinData.getFinCcy());
			financeMain.setGraceBaseRate(extFinData.getGraceBaseRate());
			financeMain.setGrcMargin(finType.getFinGrcMargin());
			financeMain.setGrcPftRate(baseRate == null ? zeroValue : baseRate.getBRRate().add(zeroValue)
					.add(financeMain.getGrcMargin()));
		} else {
			financeMain.setGrcPftRate(finType.getFinGrcIntRate());
		}
		// financeMain.setGrcProfitDaysBasis(finType.getFinDaysCalType());
		financeMain.setGrcProfitDaysBasis(extFinData.getProfitDaysBasis());
		financeMain.setGrcPftFrq(finType.getFinGrcDftIntFrq());

		// Grace Profit Frequency
		financeMain.setNextGrcPftDate(FrequencyUtil.getNextDate(financeMain.getGrcPftFrq(), 1,
				financeMain.getFinStartDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
		financeMain.setNextGrcPftDate(DateUtility.getDate(DateUtility.formatUtilDate(financeMain.getNextGrcPftDate(),
				PennantConstants.dateFormat)));
		if (financeMain.getNextGrcPftDate().after(financeMain.getGrcPeriodEndDate())) {
			financeMain.setNextGrcPftDate(financeMain.getGrcPeriodEndDate());
		}

		financeMain.setAllowGrcPftRvw(finType.isFinGrcIsRvwAlw());

		// Grace profit Review Frequency
		if (financeMain.isAllowGrcPftRvw()) {
			financeMain.setGrcPftRvwFrq(finType.getFinGrcRvwFrq());

			// Next Grace profit Review Date
			financeMain.setNextGrcPftRvwDate(FrequencyUtil.getNextDate(financeMain.getGrcPftRvwFrq(), 1,
					financeMain.getFinStartDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
			financeMain.setNextGrcPftRvwDate(DateUtility.getDate(DateUtility.formatUtilDate(
					financeMain.getNextGrcPftRvwDate(), PennantConstants.dateFormat)));
			if (financeMain.getNextGrcPftRvwDate().after(financeMain.getGrcPeriodEndDate())) {
				financeMain.setNextGrcPftRvwDate(financeMain.getGrcPeriodEndDate());
			}

		} else {
			financeMain.setNextGrcPftRvwDate(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
			financeMain.setGrcPftRvwFrq("");
		}

		// Allow Grace Capitalization
		if (extFinData.isAllowGrcCpz()) {
			financeMain.setAllowGrcCpz(true);
			financeMain.setGrcCpzFrq(finType.getFinGrcCpzFrq());
			financeMain.setNextGrcCpzDate(FrequencyUtil.getNextDate(financeMain.getGrcCpzFrq(), 1,
					financeMain.getFinStartDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
			financeMain.setNextGrcCpzDate(DateUtility.getDate(DateUtility.formatUtilDate(
					financeMain.getNextGrcCpzDate(), PennantConstants.dateFormat)));
			if (financeMain.getNextGrcCpzDate().after(financeMain.getGrcPeriodEndDate())) {
				financeMain.setNextGrcCpzDate(financeMain.getGrcPeriodEndDate());
			}
		}

		// Allow Grace repay
		if (extFinData.getGrcSchdMthd().equals(CalculationConstants.SCHMTHD_NOPAY)) {
			financeMain.setAllowGrcRepay(false);
		} else {
			financeMain.setAllowGrcRepay(true);
		}
		financeMain.setGrcSchdMthd(extFinData.getGrcSchdMthd());

		return true;
	}

	/**
	 * Method to get Base Rate
	 * 
	 * @param extFinData
	 * @return BaseRate
	 * */
	private BaseRate getBaseRate(String baseRate, String currency) {
		Date dateValueDate = DateUtility.getAppDate();
		return getBaseRateDAO().getBaseRateByType(baseRate, currency, dateValueDate);
	}

	public void processExtFinanceData(LoggedInUser userDetails, ExtFinanceData extFinData) throws IOException,
			InterfaceException, JaxenException {
		logger.debug("Entering");

		finScheduleData.getDisbursementDetails().clear();
		getFinMain().setRecordType(PennantConstants.RECORD_TYPE_NEW);
		getFinMain().setWorkflowId(0);
		getFinMain().setNewRecord(true);
		getFinMain().setUserDetails(userDetails);

		finScheduleData.setFinanceMain(getFinMain());
		disbursementDetails.setDisbDate(getFinMain().getFinStartDate());
		disbursementDetails.setDisbAmount(getFinMain().getFinAmount());
		finScheduleData.getDisbursementDetails().add(disbursementDetails);
		finScheduleData.setErrorDetails(new ArrayList<ErrorDetail>());
		finScheduleData.setRepayInstructions(new ArrayList<RepayInstruction>());

		// Step Policy Details
		if (finType != null && finType.isStepFinance()) {
			List<StepPolicyDetail> stepPolicyList = getStepPolicyDetailDAO().getStepPolicyDetailListByID(
					finType.getDftStepPolicy(), "_AView");
			finScheduleData.resetStepPolicyDetails(stepPolicyList);

			if (finMain.isAllowGrcPeriod()) {
				finScheduleData.getStepPolicyDetails().get(0).setInstallments(12);
				finScheduleData.getStepPolicyDetails().get(1).setInstallments(12);
				finScheduleData.getStepPolicyDetails().get(2).setInstallments(12);
				finScheduleData.getStepPolicyDetails().get(3).setInstallments(12);
				finScheduleData.getFinanceMain().setGraceTerms(12);
			} else {
				finScheduleData.getStepPolicyDetails().get(0).setInstallments(15);
				finScheduleData.getStepPolicyDetails().get(1).setInstallments(15);
				finScheduleData.getStepPolicyDetails().get(2).setInstallments(15);
				finScheduleData.getStepPolicyDetails().get(3).setInstallments(15);
			}

			finScheduleData.getFinanceMain().setStepFinance(true);
			finScheduleData.getFinanceMain().setStepPolicy(finType.getDftStepPolicy());
		}

		finScheduleData = ScheduleGenerator.getNewSchd(finScheduleData);
		if (finScheduleData.getFinanceScheduleDetails().size() != 0) {
			
			finScheduleData = ScheduleCalculator.getCalSchd(finScheduleData, BigDecimal.ZERO);
			finScheduleData.setSchduleGenerated(true);
		}

		// Reset Data
		finScheduleData.getFinanceMain().setEqualRepay(this.oldFinMain.isEqualRepay());
		finScheduleData.getFinanceMain().setCalculateRepay(this.oldFinMain.isCalculateRepay());
		finScheduleData.getFinanceMain().setRecalType(this.oldFinMain.getRecalType());
		finScheduleData.getFinanceMain().setLastRepayDate(this.oldFinMain.getFinStartDate());
		finScheduleData.getFinanceMain().setLastRepayPftDate(this.oldFinMain.getFinStartDate());
		finScheduleData.getFinanceMain().setLastRepayRvwDate(this.oldFinMain.getFinStartDate());
		finScheduleData.getFinanceMain().setLastRepayCpzDate(this.oldFinMain.getFinStartDate());

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		int lastIndx = finScheduleData.getFinanceScheduleDetails().size() - 1;
		BigDecimal lastInstPft = BigDecimal.ZERO;
		if (finScheduleData.getFinanceScheduleDetails().size() > 0) {
			lastInstPft = finScheduleData.getFinanceScheduleDetails().get(lastIndx).getProfitSchd();
		}

		// VALIDATE RESULTS

		finScheduleData.getFinanceMain().setFinRemarks("SUCCESS");

		if (!extFinData.getExpTotalPft().equals(financeMain.getTotalProfit())) {
			finScheduleData.getFinanceMain().setFinRemarks("FAILED-Mismatch in Total Profit");
		} else if (!extFinData.getExpFirstInst().equals(financeMain.getFirstRepay())) {
			finScheduleData.getFinanceMain().setFinRemarks("FAILED-Mismatch in First Repay");
		} else if (!extFinData.getExpLastInst().equals(financeMain.getLastRepay())) {
			finScheduleData.getFinanceMain().setFinRemarks("FAILED-Mismatch in Last Repay");
		} else if (!extFinData.getExpLastInstPft().equals(lastInstPft)) {
			finScheduleData.getFinanceMain().setFinRemarks("FAILED-Mismatch in Last Repay Schd. Profit");
		} /*
		 * else if (!extFinData.getExpRateAtStart().equals(financeMain.getGrcPftRate())) {
		 * finScheduleData.getFinanceMain().setFinRemarks( "FAILED-Mismatch in Grace Profit Rate"); } else if
		 * (!extFinData.getExpRateAtGrcEnd ().equals(financeMain.getRepayProfitRate())) { finScheduleData.getFinanceMain
		 * ().setFinRemarks("FAILED-Mismatch in Repay Profit Rate"); }
		 */
		finScheduleData.setFinanceMain(financeMain);

		FinanceDetail afinanceDetail = new FinanceDetail();
		afinanceDetail.setUserAction("");
		afinanceDetail.setExtSource(true);

		// prepare accounting
		//afinanceDetail.setReturnDataSetList(processAccounting(finScheduleData));
		afinanceDetail.setAccountingEventCode("");

		finScheduleData.setFinReference(getFinMain().getFinReference());
		afinanceDetail.setFinScheduleData(finScheduleData);
		finScheduleData.getFinanceMain().setMigratedFinance(true);

		AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, afinanceDetail);
		AuditHeader auditHeader = new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null,
				null, auditDetail, getFinMain().getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
		getFinanceDetailService().doApprove(auditHeader, false);

		logger.debug("Leaving");
	}

	/*
	 * ================================================================================================================
	 * VALDIATE EXTERNAL INPUT DATA
	 * ================================================================================================================
	 */
	public ExtFinanceData validateExtFinanceDatafromWebservice(ExtFinanceData extFinData, FinanceMain financeMain)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		System.out.println("----> START VALIDATION FINANCE UPLOAD  --------> :: "
				+ DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
		finType = getFinanceTypeDAO().getFinanceTypeByID(extFinData.getFinType(), "");
		// Validate REFERENCE
		if (extFinData.getFinReference() == null) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					new ErrorDetail("41002", "", new String[] { "Finance Reference", "" }), userLangauge).getError());
			return extFinData;
		}

		// Check whether Finance already exists with same reference and return if so
		if (getFinanceDetailService() != null
				&& getFinanceDetailService().isFinReferenceExits(extFinData.getFinReference(), "", false)) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					new ErrorDetail("30506", "", new String[] { "Finance Reference", extFinData.getFinReference() }),
					userLangauge).getError());
			return extFinData;
		}
		finScheduleData.setFinReference(extFinData.getFinReference());
		finScheduleData.setFinanceType(finType);

		financeMain.setFinReference(extFinData.getFinReference());
		financeMain.setFinType(extFinData.getFinType());
		financeMain.setFinCcy(extFinData.getFinCcy());
		financeMain.setProfitDaysBasis(extFinData.getProfitDaysBasis());
		financeMain.setGrcRateBasis(finType.getFinGrcRateType());//Added by Ravi

		try {
			if (getCustomerDAO() != null) {
				customer = getCustomerDAO().getCustomerByCIF(extFinData.getLovDescCustCIF(), "");

				if (customer == null) {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
							new ErrorDetail("41002", "", new String[] { "Customer", extFinData.getLovDescCustCIF() }),
							userLangauge).getError());
					return extFinData;
				}

				financeMain.setCustID(customer.getCustID());
				financeMain.setLovDescCustCIF(extFinData.getLovDescCustCIF());
				financeMain.setLovDescCustFName(customer.getCustFName());
				financeMain.setLovDescCustShrtName(customer.getCustShrtName());
			} else {
				financeMain.setLovDescCustCIF(extFinData.getLovDescCustCIF());
			}
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					new ErrorDetail("41002", "", new String[] { "Customer", extFinData.getLovDescCustCIF() }),
					userLangauge).getError());
			return extFinData;
		}

		financeMain.setFinBranch(extFinData.getFinBranch());
		financeMain.setFinStartDate(extFinData.getFinStartDate());
		financeMain.setFinContractDate(extFinData.getFinStartDate());
		financeMain.setFinAmount(extFinData.getFinAmount());
		financeMain.setDisbAccountId(extFinData.getDisbAccountId());
		financeMain.setRepayAccountId(extFinData.getRepayAccountId());
		financeMain.setDownPayment(extFinData.getDownPayment());
		financeMain.setDownPayBank(extFinData.getDpToBank());
		financeMain.setDownPaySupl(extFinData.getDpToSupplier());
		financeMain.setDownPayAccount(financeMain.getRepayAccountId());
		financeMain.setDefferments(finType.getFinDftTerms());

		if (finType.isAlwPlanDeferment()) {
			financeMain.setPlanDeferCount(finType.getPlanDeferCount());
		}

		// Collateral Reference
		financeMain.setFinCommitmentRef(StringUtils.isBlank(extFinData.getFinCommitmentRef()) ? "" : extFinData
				.getFinCommitmentRef());

		// Depreciation Frequency
		if (finType.isFinDepreciationReq()) {
			financeMain.setDepreciationFrq(finType.getFinDepreciationFrq());
			financeMain.setNextDepDate(FrequencyUtil.getNextDate(financeMain.getDepreciationFrq(), 1,
					financeMain.getFinStartDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
			financeMain.setNextDepDate(DateUtility.getDate(DateUtility.formatUtilDate(financeMain.getNextDepDate(),
					PennantConstants.dateFormat)));
		}

		financeMain.setFinIsActive(true);

		/*
		 * ------------------------------------------------------------------------------------------------------------
		 * GRACE PERIOD
		 * ------------------------------------------------------------------------------------------------------------
		 */

		if (extFinData.isAllowGrcPeriod()) {
			financeMain.setAllowGrcPeriod(true);
			validateGraceDetails(extFinData, financeMain, finType);
			financeMain.setCpzAtGraceEnd(finType.isFinIsIntCpzAtGrcEnd());
		} else {
			financeMain.setAllowGrcPeriod(false);
			financeMain.setGrcPeriodEndDate(extFinData.getFinStartDate());
		}

		/*
		 * ------------------------------------------------------------------------------------------------------------
		 * REPAY PERIOD
		 * ------------------------------------------------------------------------------------------------------------
		 */
		int terms = finType.getFinDftTerms();

		if (extFinData.isAllowGrcPeriod()) {
			terms = terms - 12;
		}

		financeMain.setNumberOfTerms(terms);
		financeMain.setFinRepaymentAmount(extFinData.getReqRepayAmount());
		financeMain.setRepayRateBasis(extFinData.getRepayRateBasis());

		// Repay Rate
		if (finType.getFinBaseRate() != null) {
			financeMain.setRepayBaseRate(finType.getFinBaseRate());
			baseRate = getBaseRate(financeMain.getRepayBaseRate(), financeMain.getFinCcy());
			financeMain.setRepayMargin(finType.getFinMargin());
			financeMain.setRepayProfitRate(baseRate == null ? zeroValue : baseRate.getBRRate().add(zeroValue)
					.add(financeMain.getRepayMargin()));
		} else {
			financeMain.setRepayProfitRate(finType.getFinIntRate());
		}

		financeMain.setScheduleMethod(extFinData.getScheduleMethod());

		// Repay profit Frequency & Date
		financeMain.setRepayPftFrq(finType.getFinDftIntFrq());

		if (financeMain.getRepayPftFrq() != null) {
			financeMain.setNextRepayPftDate(FrequencyUtil.getNextDate(financeMain.getRepayPftFrq(), 1,
					financeMain.getGrcPeriodEndDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
		}
		if (financeMain.getNextRepayPftDate() != null) {
			financeMain.setNextRepayPftDate(DateUtility.getDate(DateUtility.formatUtilDate(
					financeMain.getNextRepayPftDate(), PennantConstants.dateFormat)));
		}

		// Allow Repay Review
		if (finType.isFinIsRvwAlw()) {
			financeMain.setAllowRepayRvw(finType.isFinIsRvwAlw());
			financeMain.setRepayRvwFrq(finType.getFinRvwFrq());
			financeMain.setNextRepayRvwDate(FrequencyUtil.getNextDate(financeMain.getRepayRvwFrq(), 1,
					financeMain.getGrcPeriodEndDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
			financeMain.setNextRepayRvwDate(DateUtility.getDate(DateUtility.formatUtilDate(
					financeMain.getNextRepayRvwDate(), PennantConstants.dateFormat)));
		} else {
			financeMain.setRepayRvwFrq("");
		}
		financeMain.setRvwRateApplFor(finType.getFinRvwRateApplFor());

		// Allow Repay Capitalization
		if (finType.isFinIsIntCpz()) {
			financeMain.setAllowRepayCpz(finType.isFinIsIntCpz());
			financeMain.setRepayCpzFrq(finType.getFinCpzFrq());
			financeMain.setNextRepayCpzDate(FrequencyUtil.getNextDate(financeMain.getRepayCpzFrq(), 1,
					financeMain.getGrcPeriodEndDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
			financeMain.setNextRepayCpzDate(DateUtility.getDate(DateUtility.formatUtilDate(
					financeMain.getNextRepayCpzDate(), PennantConstants.dateFormat)));
		} else {
			financeMain.setRepayCpzFrq("");
		}

		// Repay Frequency
		financeMain.setRepayFrq(finType.getFinRpyFrq());
		if (financeMain.getRepayFrq() != null) {
			financeMain.setNextRepayDate(FrequencyUtil.getNextDate(financeMain.getRepayFrq(), 1,
					financeMain.getGrcPeriodEndDate(), "A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
		}
		if (financeMain.getNextRepayDate() != null) {
			financeMain.setNextRepayDate(DateUtility.getDate(DateUtility.formatUtilDate(financeMain.getNextRepayDate(),
					PennantConstants.dateFormat)));
		}

		// Maturity Date
		if (financeMain.getRepayFrq() != null && financeMain.getNextRepayDate() != null) {
			List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(financeMain.getRepayFrq(),
					financeMain.getNumberOfTerms(), financeMain.getNextRepayDate(), "A", true, 0).getScheduleList();
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				financeMain.setMaturityDate(calendar.getTime());
				financeMain.setMaturityDate(DateUtility.getDate(DateUtility.formatUtilDate(
						financeMain.getMaturityDate(), PennantConstants.dateFormat)));
			}
		}
		financeMain.setFinRepayPftOnFrq(finType.isFinRepayPftOnFrq());

		financeMain.setCalculateRepay(extFinData.isCalculateRepay());
		financeMain.setEqualRepay(extFinData.isEqualRepay());

		// OTHER DEFAULST
		financeMain.setLastRepayDate(financeMain.getFinStartDate());
		financeMain.setLastRepayPftDate(financeMain.getFinStartDate());
		financeMain.setLastRepayCpzDate(financeMain.getFinStartDate());
		financeMain.setLastRepayRvwDate(financeMain.getFinStartDate());
		financeMain.setLastDepDate(financeMain.getFinStartDate());

		// FINANCE ASSET VALUE
		financeMain.setFinAssetValue(financeMain.getFinAmount());
		financeMain.setFinCurrAssetValue(financeMain.getFinAmount());

		// Source ID
		financeMain.setFinSourceID(StringUtils.isBlank(extFinData.getFinSourceID()) ? "EXT" : extFinData
				.getFinSourceID());

		// Set recal type from finance type
		if (finType != null && finType.isFinIsRvwAlw()) {
			financeMain.setRecalType(finType.getFinSchCalCodeOnRvw());
		} else {
			financeMain.setRecalType("");
		}

		// Version
		financeMain.setVersion(1);

		// User Details
		financeMain.setLastMntBy(userID);
		financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		// Record Status
		financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		setFinMain(financeMain);

		// Storing original finance main data to reset
		oldFinMain = new FinanceMain();
		BeanUtils.copyProperties(financeMain, oldFinMain);
		System.out.println("----> END VALIDATION FINANCE UPLOAD  --------> :: "
				+ DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
		return extFinData;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public BranchDAO getBranchDAO() {
		return branchDAO;
	}

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	public CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public BaseRateDAO getBaseRateDAO() {
		return baseRateDAO;
	}

	public void setBaseRateDAO(BaseRateDAO baseRateDAO) {
		this.baseRateDAO = baseRateDAO;
	}

	public SplRateDAO getSplRateDAO() {
		return splRateDAO;
	}

	public void setSplRateDAO(SplRateDAO splRateDAO) {
		this.splRateDAO = splRateDAO;
	}

	public AccountsDAO getAccountsDAO() {
		return accountsDAO;
	}

	public void setAccountsDAO(AccountsDAO accountsDAO) {
		this.accountsDAO = accountsDAO;
	}

	public FinanceMain getFinMain() {
		return finMain;
	}

	public void setFinMain(FinanceMain finMain) {
		this.finMain = finMain;
	}

	public FinanceMain getOldFinMain() {
		return oldFinMain;
	}

	public void setOldFinMain(FinanceMain oldFinMain) {
		this.oldFinMain = oldFinMain;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	private String getValue(HSSFCell cell) {
		if (cell != null) {
			return StringUtils.trimToEmpty(cell.toString());
		}
		return "";
	}

	private BigDecimal getDecimalValue(HSSFCell cell) {
		String strValue = null;
		if (cell != null) {
			strValue = StringUtils.trimToNull(cell.toString());

			if (strValue == null) {
				return BigDecimal.ZERO;
			} else {
				return new BigDecimal(cell.getNumericCellValue());
			}
		}
		return BigDecimal.ZERO;
	}

	private BigDecimal round(BigDecimal value) {
		return value.setScale(0, RoundingMode.HALF_DOWN);
	}

	public StepPolicyDetailDAO getStepPolicyDetailDAO() {
		return stepPolicyDetailDAO;
	}

	public void setStepPolicyDetailDAO(StepPolicyDetailDAO stepPolicyDetailDAO) {
		this.stepPolicyDetailDAO = stepPolicyDetailDAO;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public RequestDetail getRequestDetail() {
		return requestDetail;
	}

	public void setRequestDetail(RequestDetail aRequestDetail) {
		this.requestDetail = aRequestDetail;
	}

	public FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}

	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

}
