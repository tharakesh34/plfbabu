package com.pennant.externalinput.service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.accounts.AccountsDAO;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.applicationmaster.SplRateDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.DefermentHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.equation.util.HostConnection;
import com.pennant.externalinput.ExtFinanceData;

public class ExtFinanceUploadService {

	private final static Logger logger = Logger.getLogger(ExtFinanceUploadService.class);

	private boolean isError = false;

	private FinanceTypeDAO financeTypeDAO;
	private BranchDAO branchDAO;
	private FinanceMainDAO financeMainDAO;
	private CurrencyDAO currencyDAO;
	private CustomerDAO customerDAO;
	private BaseRateDAO baseRateDAO;
	private SplRateDAO splRateDAO;
	private AccountsDAO accountsDAO;
	private FinanceDetailService financeDetailService;
	private AccountInterfaceService accountInterfaceService;
	private HostConnection hostConnection;

	private BigDecimal zeroValue = BigDecimal.ZERO;
	private Date dateValueDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
	private FinanceMain finMain;
	private FinanceMain old_finMain;
	private FinanceDisbursement disbursementDetails = new FinanceDisbursement();
	private FinScheduleData finScheduleData = new FinScheduleData();
	private long userID = 1L;
	private String userLangauge;
	private String dayOfFrq;

	FinanceType finType = null;
	SplRate splRate = null;
	BaseRate baseRate = null;
	Customer customer = null;
	HashMap<String, String> branchMap = new HashMap<String, String>();
	HashMap<String, FinanceType> finTypeMap = new HashMap<String, FinanceType>();
	HashMap<String, String> finCcyMap = new HashMap<String, String>();
	HashMap<String, Customer> customerMap = new HashMap<String, Customer>();
	HashMap<String, String> accountMap = new HashMap<String, String>();
	HashMap<String, BaseRate> baseRateMap = new HashMap<String, BaseRate>();
	HashMap<String, SplRate> splRateMap = new HashMap<String, SplRate>();

	/**
	 * Method to process External finance Detail through Excel file
	 * 
	 * @param finInput
	 */
	public String procExternalFinance(InputStream finInput, LoginUserDetails usrDetails) {
		logger.debug("Entering");
		userID = usrDetails.getLoginUsrID();
		userLangauge = usrDetails.getUsrLanguage();
		String status = "";
		int rcdCount = 0;
		int successRcdCount = 0;
		status = successRcdCount + " records has been processed out of " + rcdCount;

		boolean isUseKeyData = false;

		try {
			System.out
			        .println("======================        START     ==========================");
			System.out
			        .println("==================================================================");
			System.out.println("----> START EXTENDED FINANCE UPLOAD  --------> :: "
			        + DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
			POIFSFileSystem finFileSystem = new POIFSFileSystem(finInput);
			HSSFWorkbook finWorkBook = new HSSFWorkbook(finFileSystem);
			HSSFSheet finSheet = finWorkBook.getSheetAt(0);
			@SuppressWarnings("rawtypes")
			Iterator rowIter = finSheet.rowIterator();
			ExtFinanceData extFinData;
			HSSFRow finRow;

			String strKeyField = null;

			while (rowIter.hasNext()) {
				finRow = (HSSFRow) rowIter.next();
				if (finRow.getRowNum() == 0) {
					continue;
				}

				strKeyField = StringUtils.trimToEmpty(getValue(finRow.getCell(9)));
				if (strKeyField.equals("GENDATA")) {
					isUseKeyData = true;
				} else {
					isUseKeyData = false;
				}

				if (!finRow.getCell(0).toString().equals("END")) {
					rcdCount = rcdCount + 1;
					extFinData = new ExtFinanceData();

					if (isUseKeyData) {
						extFinData = prepareExtFinanceData(finRow, extFinData);
					} else {
						extFinData = setExtFinanceData(finRow, extFinData);
					}

					extFinData = validateExtFinanceData(extFinData, new FinanceMain());
					if ("E".equals(extFinData.getRecordStatus())) {
						//TODO need to decide
					} else {
						// Save Finance Data into DataBase
						successRcdCount = successRcdCount + 1;
						processFinanceData(usrDetails);
					}
				} else {
					//FIXME Don't Know why it is called here 
					//getHostConnection().disConnection();
					status = successRcdCount + " records has been processed out of " + rcdCount;
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			return status;
		}
		logger.debug("Leaving");
		return status;
	}

	private void processFinanceData(LoginUserDetails userDetails) throws IOException,
	        AccountNotFoundException {
		logger.debug("Entering");

		finScheduleData.getDisbursementDetails().clear();
		getFinMain().setRecordType(PennantConstants.RECORD_TYPE_NEW);
		getFinMain().setWorkflowId(0);
		getFinMain().setNewRecord(true);
		getFinMain().setUserDetails(userDetails);

		finScheduleData.getFinanceType().setLovDescAssetCodeName("");
		finScheduleData.setFinanceMain(getFinMain());
		disbursementDetails.setDisbDate(getFinMain().getFinStartDate());
		disbursementDetails.setDisbAmount(getFinMain().getFinAmount());
		finScheduleData.getDisbursementDetails().add(disbursementDetails);
		finScheduleData.setErrorDetails(new ArrayList<ErrorDetails>());
		finScheduleData.setDefermentHeaders(new ArrayList<DefermentHeader>());
		finScheduleData.setDefermentDetails(new ArrayList<DefermentDetail>());
		finScheduleData.setRepayInstructions(new ArrayList<RepayInstruction>());
		finScheduleData = ScheduleGenerator.getNewSchd(finScheduleData);
		if (finScheduleData.getFinanceScheduleDetails().size() != 0) {
			finScheduleData = ScheduleCalculator.getCalSchd(finScheduleData);
			finScheduleData.setSchduleGenerated(true);
		}
		//Reset Data
		finScheduleData.getFinanceMain().setEqualRepay(this.old_finMain.isEqualRepay());
		finScheduleData.getFinanceMain().setCalculateRepay(this.old_finMain.isCalculateRepay());
		finScheduleData.getFinanceMain().setRecalType(this.old_finMain.getRecalType());
		finScheduleData.getFinanceMain().setLastRepayDate(this.old_finMain.getFinStartDate());
		finScheduleData.getFinanceMain().setLastRepayPftDate(this.old_finMain.getFinStartDate());
		finScheduleData.getFinanceMain().setLastRepayRvwDate(this.old_finMain.getFinStartDate());
		finScheduleData.getFinanceMain().setLastRepayCpzDate(this.old_finMain.getFinStartDate());

		FinanceDetail afinanceDetail = new FinanceDetail();
		afinanceDetail.setUserAction("");
		afinanceDetail.setExtSource(true);
		afinanceDetail.setAccountingEventCode("");
		finScheduleData.setFinReference(getFinMain().getFinReference());
		afinanceDetail.setFinScheduleData(finScheduleData);
		finScheduleData.getFinanceMain().setMigratedFinance(true);

		AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, afinanceDetail);
		AuditHeader auditHeader = new AuditHeader(afinanceDetail.getFinScheduleData()
		        .getFinReference(), null, null, null, auditDetail, getFinMain().getUserDetails(),
		        new HashMap<String, ArrayList<ErrorDetails>>());
		getFinanceDetailService().doApprove(auditHeader, false);

		logger.debug("Leaving");
	}

	/***
	 * Method to set excel data to external finance bean.
	 * 
	 * @param finRow
	 * @param extFinData
	 * */
	public ExtFinanceData setExtFinanceData(HSSFRow finRow, ExtFinanceData extFinData) {
		logger.debug("Entering");

		System.out
		        .println("----> START SETTING EXT FINANCE DATA TO FINANCE MAIN OBJ  --------> :: "
		                + DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
		int intValue = 0;
		BigDecimal decimalValue = null;
		Date dateValue = null;
		String stringValue = null;

		// Finance Reference
		extFinData.setFinReference(finRow.getCell(0).toString());

		// Branch
		if (finRow.getCell(1) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(1).toString()).equals("")) {
			stringValue = finRow.getCell(1).toString();
			extFinData.setFinBranch(stringValue);
		}

		// Finance Type
		if (finRow.getCell(2) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(2).toString()).equals("")) {
			stringValue = finRow.getCell(2).toString();
			extFinData.setFinType(stringValue);
		}

		// Finance Remarks
		if (finRow.getCell(3) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(3).toString()).equals("")) {
			stringValue = finRow.getCell(3).toString();
			extFinData.setFinRemarks(stringValue);
		}

		// Finance Currency
		if (finRow.getCell(4) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(4).toString()).equals("")) {
			stringValue = finRow.getCell(4).toString();
			extFinData.setFinCcy(stringValue);
		}

		// Finance Schedule Method
		if (finRow.getCell(5) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(5).toString()).equals("")) {
			stringValue = finRow.getCell(5).toString();
			extFinData.setScheduleMethod(stringValue);
		}

		// Finance Profit Days Basis
		if (finRow.getCell(6) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(6).toString()).equals("")) {
			stringValue = finRow.getCell(6).toString();
			extFinData.setProfitDaysBasis(stringValue);
		}

		// Finance Start Date
		if (finRow.getCell(7) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(7).toString()).equals("")) {
			dateValue = (Date) finRow.getCell(7).getDateCellValue();
			extFinData.setFinStartDate(dateValue);
		}

		// Finance Amount
		if (finRow.getCell(8) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(8).toString()).equals("")) {
			decimalValue = new BigDecimal(finRow.getCell(8).getNumericCellValue());
			extFinData.setFinAmount(decimalValue);
		}

		// Finance Asset Value
		if (finRow.getCell(9) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(9).toString()).equals("")) {
			decimalValue = new BigDecimal(finRow.getCell(9).getNumericCellValue());
			extFinData.setFinAssetValue(decimalValue);
		}

		// Customer CIF
		if (finRow.getCell(16) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(16).toString()).equals("")) {
			stringValue = finRow.getCell(16).toString();
			extFinData.setLovDescCustCIF(stringValue);
		}

		// Disbursement Account Id
		if (finRow.getCell(10) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(10).toString()).equals("")) {
			extFinData.setDisbAccountId(finRow.getCell(10).toString());
		}

		// Repayment Account Id
		if (finRow.getCell(11) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(11).toString()).equals("")) {
			extFinData.setRepayAccountId(finRow.getCell(11).toString());
		}

		// Finance Account
		if (finRow.getCell(12) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(12).toString()).equals("")) {
			extFinData.setFinAccount(finRow.getCell(12).toString());
		}

		// Customer Profit Account Id
		if (finRow.getCell(13) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(13).toString()).equals("")) {
			extFinData.setFinCustPftAccount(finRow.getCell(13).toString());
		}

		// Source ID
		if (finRow.getCell(14) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(14).toString()).equals("")) {
			extFinData.setFinSourceID(finRow.getCell(14).toString());
		}

		// Number of Terms
		if (finRow.getCell(15) == null) {
			intValue = 0;
		} else {
			intValue = (int) Math.round(finRow.getCell(15).getNumericCellValue());
		}
		extFinData.setNumberOfTerms(intValue);

		// Deferments
		if (finRow.getCell(17) == null) {
			intValue = -1;
		} else {
			intValue = (int) Math.round(finRow.getCell(17).getNumericCellValue());
		}
		extFinData.setDefferments(intValue);

		// Frequency Deferments
		if (finRow.getCell(18) == null) {
			intValue = -1;
		} else {
			intValue = (int) Math.round(finRow.getCell(18).getNumericCellValue());
		}
		extFinData.setFrqDefferments(intValue);

		// Collateral Reference
		if (finRow.getCell(19) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(19).toString()).equals("")) {
			extFinData.setFinCommitmentRef(finRow.getCell(19).toString());
		}

		// Depreciation Frequency
		if (finRow.getCell(20) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(20).toString()).equals("")) {
			extFinData.setDepreciationFrq(finRow.getCell(20).toString());
		}

		// Next Depreciation Date
		if (finRow.getCell(21) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(21).toString()).equals("")) {
			dateValue = (Date) finRow.getCell(21).getDateCellValue();
			extFinData.setNextDepDate(dateValue);
		}

		// Contract Date
		if (finRow.getCell(22) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(22).toString()).equals("")) {
			dateValue = (Date) finRow.getCell(22).getDateCellValue();
			extFinData.setFinContractDate(dateValue);
		}

		// Grace Rate Basis
		if (finRow.getCell(23) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(23).toString()).equals("")) {
			extFinData.setGrcRateBasis(finRow.getCell(23).toString());
		}

		// Grace Period End Date
		if (finRow.getCell(24) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(24).toString()).equals("")) {
			dateValue = (Date) finRow.getCell(24).getDateCellValue();
			extFinData.setGrcPeriodEndDate(dateValue);
		}

		// Allow Grace Period
		if (finRow.getCell(25) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(25).toString()).equals("")) {
			extFinData.setAllowGrcPeriod((Boolean) finRow.getCell(25).getBooleanCellValue());
		}

		// Grace Base Rate
		if (finRow.getCell(26) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(26).toString()).equals("")) {
			extFinData.setGraceBaseRate(finRow.getCell(26).toString());
		}

		// Grace Special Rate
		if (finRow.getCell(27) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(27).toString()).equals("")) {
			extFinData.setGraceSpecialRate(finRow.getCell(27).toString());
		}

		// Grace Margin
		if (finRow.getCell(28) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(28).toString()).equals("")) {
			decimalValue = new BigDecimal(finRow.getCell(28).toString());
			extFinData.setGrcMargin(decimalValue);
		}

		// Grace Profit Rate
		if (finRow.getCell(29) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(29).toString()).equals("")) {
			decimalValue = new BigDecimal(finRow.getCell(29).toString());
			extFinData.setGrcPftRate(decimalValue);
		}

		// Grace profit Frequency
		if (finRow.getCell(30) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(30).toString()).equals("")) {
			extFinData.setGrcPftFrq(finRow.getCell(30).toString());
		}

		// Next Grace profit Date
		if (finRow.getCell(31) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(31).toString()).equals("")) {
			dateValue = (Date) finRow.getCell(31).getDateCellValue();
			extFinData.setNextGrcPftDate(dateValue);
		}

		// Allow Grace Profit Review
		if (finRow.getCell(32) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(32).toString()).equals("")) {
			extFinData.setAllowGrcPftRvw((Boolean) finRow.getCell(32).getBooleanCellValue());
		}

		// Grace profit Review Frequency
		if (finRow.getCell(33) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(33).toString()).equals("")) {
			extFinData.setGrcPftRvwFrq(finRow.getCell(33).toString());
		}

		// Next Grace profit Review Date
		if (finRow.getCell(34) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(34).toString()).equals("")) {
			dateValue = (Date) finRow.getCell(34).getDateCellValue();
			extFinData.setNextGrcPftRvwDate(dateValue);
		}

		// Allow Grace Capitalization
		if (finRow.getCell(35) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(35).toString()).equals("")) {
			extFinData.setAllowGrcCpz((Boolean) finRow.getCell(35).getBooleanCellValue());
		}

		// Grace Cpz Frequency
		if (finRow.getCell(36) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(36).toString()).equals("")) {
			extFinData.setGrcCpzFrq(finRow.getCell(36).toString());
		}

		// Next Grace Cpz Date
		if (finRow.getCell(37) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(37).toString()).equals("")) {
			dateValue = (Date) finRow.getCell(37).getDateCellValue();
			extFinData.setNextGrcCpzDate(dateValue);
		}

		// Capitalization at grace End
		if (finRow.getCell(38) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(38).toString()).equals("")) {
			extFinData.setCpzAtGraceEnd((Boolean) finRow.getCell(38).getBooleanCellValue());
		}

		// Allow Grace repay
		if (finRow.getCell(39) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(39).toString()).equals("")) {
			extFinData.setAllowGrcRepay((Boolean) finRow.getCell(39).getBooleanCellValue());
		}

		// Grace Schedule Method
		if (finRow.getCell(40) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(40).toString()).equals("")) {
			stringValue = finRow.getCell(40).toString();
			extFinData.setGrcSchdMthd(stringValue);
		}

		// Repay Rate Basis
		if (finRow.getCell(41) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(41).toString()).equals("")) {
			extFinData.setRepayRateBasis(finRow.getCell(41).toString());
		}

		// Repay Base Rate
		if (finRow.getCell(42) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(42).toString()).equals("")) {
			extFinData.setRepayBaseRate(finRow.getCell(42).toString());
		}

		// Repay Special Rate
		if (finRow.getCell(43) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(43).toString()).equals("")) {
			extFinData.setRepaySpecialRate(finRow.getCell(43).toString());
		}

		// Repay Margin
		if (finRow.getCell(44) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(44).toString()).equals("")) {
			decimalValue = new BigDecimal(finRow.getCell(44).toString());
			extFinData.setRepayMargin(decimalValue);
		}

		// Repay Profit Rate
		if (finRow.getCell(45) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(45).toString()).equals("")) {
			decimalValue = new BigDecimal(finRow.getCell(45).toString());
			extFinData.setRepayProfitRate(decimalValue);
		}

		// Repay Frequency
		if (finRow.getCell(46) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(46).toString()).equals("")) {
			extFinData.setRepayFrq(finRow.getCell(46).toString());
		}

		// Next Repay Date
		if (finRow.getCell(47) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(47).toString()).equals("")) {
			dateValue = (Date) finRow.getCell(47).getDateCellValue();
			extFinData.setNextRepayDate(dateValue);
		}

		// Repay ProfitFrequency
		if (finRow.getCell(48) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(48).toString()).equals("")) {
			extFinData.setRepayPftFrq(finRow.getCell(48).toString());
		}

		// Next Repay Date
		if (finRow.getCell(49) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(49).toString()).equals("")) {
			dateValue = (Date) finRow.getCell(49).getDateCellValue();
			extFinData.setNextRepayPftDate(dateValue);
		}

		// Allow Repay Review
		if (finRow.getCell(50) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(50).toString()).equals("")) {
			extFinData.setAllowRepayRvw((Boolean) finRow.getCell(50).getBooleanCellValue());
		}

		// Repay Review Frequency
		if (finRow.getCell(51) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(51).toString()).equals("")) {
			extFinData.setRepayRvwFrq(finRow.getCell(51).toString());
		}

		// Next Repay Review Date
		if (finRow.getCell(52) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(52).toString()).equals("")) {
			dateValue = (Date) finRow.getCell(52).getDateCellValue();
			extFinData.setNextRepayRvwDate(dateValue);
		}

		// Allow Repay Capitalization
		if (finRow.getCell(53) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(53).toString()).equals("")) {
			extFinData.setAllowRepayCpz((Boolean) finRow.getCell(53).getBooleanCellValue());
		}

		// Repay Cpz Frequency
		if (finRow.getCell(54) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(54).toString()).equals("")) {
			extFinData.setRepayCpzFrq(finRow.getCell(54).toString());
		}

		// Next Reapy Cpz Date
		if (finRow.getCell(55) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(55).toString()).equals("")) {
			dateValue = (Date) finRow.getCell(55).getDateCellValue();
			extFinData.setNextRepayCpzDate(dateValue);
		}

		// Maturity Date
		if (finRow.getCell(56) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(56).toString()).equals("")) {
			dateValue = (Date) finRow.getCell(56).getDateCellValue();
			extFinData.setMaturityDate(dateValue);
		}

		// DownPayment
		if (finRow.getCell(57) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(57).toString()).equals("")) {
			decimalValue = new BigDecimal(finRow.getCell(57).toString());
			extFinData.setDownPayment(decimalValue);
		}

		// Required Repay Amount
		if (finRow.getCell(58) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(58).toString()).equals("")) {
			decimalValue = new BigDecimal(finRow.getCell(58).toString());
			extFinData.setReqRepayAmount(decimalValue);
		}

		// Allow Indicative Rate
		if (finRow.getCell(59) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(59).toString()).equals("")) {
			extFinData.setAlwIndRate((Boolean) finRow.getCell(59).getBooleanCellValue());
		}

		// Indicative Base rate
		if (finRow.getCell(60) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(60).toString()).equals("")) {
			extFinData.setIndBaseRate(finRow.getCell(60).toString());
		}

		// Calculate Repay
		if (finRow.getCell(61) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(61).toString()).equals("")) {
			extFinData.setCalculateRepay((Boolean) finRow.getCell(61).getBooleanCellValue());
		}

		// Equal Repay
		if (finRow.getCell(62) != null
		        && !StringUtils.trimToEmpty(finRow.getCell(62).toString()).equals("")) {
			extFinData.setEqualRepay((Boolean) finRow.getCell(62).getBooleanCellValue());
		}

		logger.debug("Leaving");
		System.out.println("----> END SETTING EXT FINANCE DATA TO FINANCE MAIN OBJ  --------> :: "
		        + DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
		return extFinData;
	}

	/***
	 * Method to set excel data to external finanace bean.
	 * 
	 * @param finRow
	 * @param extFinData
	 * */
	public ExtFinanceData prepareExtFinanceData(HSSFRow finRow, ExtFinanceData extFinData) {
		logger.debug("Entering");

		System.out
		        .println("----> START PREPARING EXT FINANCE DATA TO FINANCE MAIN OBJ  --------> :: "
		                + DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
		int intValue = 0;

		// Finance Type
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(1)))) {
			extFinData.setFinType(getValue(finRow.getCell(1)));
		}

		try {
			if (!finTypeMap.containsKey(extFinData.getFinType())) {
				finType = getFinanceTypeDAO().getFinanceTypeByID(extFinData.getFinType(), "");
				if (finType != null) {
					finTypeMap.put(extFinData.getFinType(), finType);
				}
			} else {
				finType = finTypeMap.get(extFinData.getFinType());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Finance Remarks
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(2)))) {
			extFinData.setFinRemarks(getValue(finRow.getCell(2)));
		}

		// Finance Currency
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(3)))) {
			extFinData.setFinCcy(getValue(finRow.getCell(3)));
		}

		// Finance Start Date
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(4)))) {
			extFinData.setFinStartDate(finRow.getCell(4).getDateCellValue());
		}

		// Finance Amount		
		extFinData.setFinAmount(getDecimalValue(finRow.getCell(5)));

		// Finance Asset Value
		extFinData.setFinAssetValue(getDecimalValue(finRow.getCell(6)));

		// Number of Terms
		if (getValue(finRow.getCell(7)) == "") {
			intValue = 0;
		} else {
			intValue = (int) Math.round(finRow.getCell(7).getNumericCellValue());
		}

		extFinData.setNumberOfTerms(intValue);

		//PREPARE DEFAULT DATA
		// Branch
		extFinData.setLovDescCustCIF(getValue(finRow.getCell(8)));
		try {
			Customer customer = getCustomerDAO().getCustomerByCIF(extFinData.getLovDescCustCIF(),"");
			if (customer != null) {
				if (StringUtils.trimToNull(customer.getCustDftBranch()) != null) {
					extFinData.setFinBranch(StringUtils.trim(customer.getCustDftBranch()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Finance Reference
		extFinData.setFinReference(getValue(finRow.getCell(1)).concat(
		        extFinData.getLovDescCustCIF()).concat(extFinData.getFinCcy().substring(0, 1)));

		// Finance Schedule Method
		extFinData.setScheduleMethod(finType.getFinSchdMthd());

		// Finance Profit Days Basis
		extFinData.setProfitDaysBasis(finType.getFinDaysCalType());

		// Disbursement Account Id
		if (extFinData.getFinCcy().equals("BHD")) {
			extFinData.setDisbAccountId(extFinData.getFinBranch()
			        .concat(extFinData.getLovDescCustCIF()).concat("002"));
		} else {
			extFinData.setDisbAccountId(extFinData.getFinBranch()
			        .concat(extFinData.getLovDescCustCIF()).concat("001"));
		}

		// Repayments Account Id
		extFinData.setRepayAccountId(extFinData.getDisbAccountId());

		// Finance Account
		extFinData.setFinAccount("");

		// Customer Profit Account Id
		extFinData.setFinCustPftAccount("");

		// Source ID
		extFinData.setFinSourceID("EXT");

		// Deferments
		if (finType.isFinIsAlwDifferment()) {
			extFinData.setDefferments(finType.getFinMaxDifferment());
		} else {
			extFinData.setDefferments(-1);
		}

		// Frequency Deferments
		extFinData.setFrqDefferments(finType.getFinMaxFrqDifferment());

		// Collateral Reference
		extFinData.setFinCommitmentRef("");

		// Depreciation Frequency & Next Depreciation Date				
		extFinData.setDepreciationFrq(finType.getFinDepreciationFrq());
		extFinData.setNextDepDate(null);

		// Contract Date
		extFinData.setFinContractDate((Date) SystemParameterDetails
		        .getSystemParameterValue(PennantConstants.APP_DATE_VALUE));

		// Grace Rate Basis
		if ("REDUCE".equals(finType.getFinGrcRateType())) {
			extFinData.setGrcRateBasis("R");					//TODO
		} else {
			extFinData.setGrcRateBasis("F");
		}

		// Grace Period End Date		
		if (finType.isFInIsAlwGrace()) {
			extFinData
			        .setGrcPeriodEndDate(DateUtility.getYearEndDate(extFinData.getFinStartDate()));
		} else {
			extFinData.setGrcPeriodEndDate(extFinData.getFinStartDate());
		}

		// Allow Grace Period
		extFinData.setAllowGrcPeriod(finType.isFInIsAlwGrace());

		// Grace Base Rate
		extFinData.setGraceBaseRate(finType.getFinGrcBaseRate());

		// Grace Special Rate
		extFinData.setGraceSpecialRate(finType.getFinGrcSplRate());

		// Grace Margin
		extFinData.setGrcMargin(finType.getFinGrcMargin());

		// Grace Profit Rate
		if (extFinData.getGraceBaseRate() != null) {
			try {
				baseRate = getBaseRate(extFinData.getGraceBaseRate());
				baseRateMap.put(extFinData.getRepaySpecialRate(), baseRate);
			} catch (Exception e) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("41002", "", new String[] { "Special Rate",
				                extFinData.getRepaySpecialRate() }), userLangauge).getError());
				//return extFinData;
			}

			if (extFinData.getGraceSpecialRate() != null) {
				try {
					splRate = getSplRate(extFinData.getGraceSpecialRate());
					splRateMap.put(extFinData.getRepaySpecialRate(), splRate);
				} catch (Exception e) {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("41002", "", new String[] { "Base Rate",
					                extFinData.getRepayBaseRate() }), userLangauge).getError());
					//return extFinData;
				}
			}

			if (splRate == null) {
				extFinData.setGrcPftRate(baseRate.getBRRate().add(zeroValue)
				        .add(extFinData.getGrcMargin()));
			} else {
				extFinData.setGrcPftRate(baseRate.getBRRate().add(splRate.getSRRate())
				        .add(extFinData.getGrcMargin()));
			}
		} else {
			extFinData.setGrcPftRate(zeroValue);
		}

		// Grace profit Frequency
		dayOfFrq = String.valueOf(DateUtility.getDay(extFinData.getFinStartDate()));

		if (DateUtility.getDay(extFinData.getFinStartDate()) < 10) {
			dayOfFrq = "0".concat(dayOfFrq);
		}

		if (isError) {

		}
		extFinData.setGrcPftFrq(finType.getFinGrcDftIntFrq());

		if (finType.getFinGrcDftIntFrq() != null) {
			extFinData.setGrcPftFrq(finType.getFinGrcDftIntFrq().substring(0, 3).concat(dayOfFrq));
		}

		//Next Grace profit Date
		extFinData.setNextGrcPftDate(null);

		// Allow Grace Profit Review
		extFinData.setAllowGrcPftRvw(finType.isFinGrcIsRvwAlw());

		// Grace profit Review Frequency
		extFinData.setGrcPftRvwFrq(finType.getFinGrcRvwFrq());

		if (finType.getFinGrcRvwFrq() != null) {
			extFinData.setGrcPftRvwFrq(finType.getFinGrcRvwFrq().substring(0, 3).concat(dayOfFrq));
		}

		// Next Grace profit Review Date
		extFinData.setNextGrcPftRvwDate(null);

		// Allow Grace Capitalization
		extFinData.setAllowGrcCpz(finType.isFinGrcIsIntCpz());

		// Grace Cpz Frequency
		extFinData.setGrcCpzFrq(finType.getFinGrcCpzFrq());

		if (finType.getFinGrcCpzFrq() != null) {
			extFinData.setGrcCpzFrq(finType.getFinGrcCpzFrq().substring(0, 3).concat(dayOfFrq));
		}

		// Next Grace Cpz Date
		extFinData.setNextGrcCpzDate(null);

		// Capitalization at grace End
		extFinData.setCpzAtGraceEnd(true);

		// Allow Grace repay
		extFinData.setAllowGrcRepay(finType.isFinIsAlwGrcRepay());

		// Grace Schedule Method
		if (extFinData.isAllowGrcRepay()) {
			extFinData.setGrcSchdMthd(finType.getFinGrcSchdMthd());
		}

		// Repay Rate Basis
		if ("REDUCE".equals(finType.getFinRateType())) {
			extFinData.setRepayRateBasis("R");						//TODO
		} else {
			extFinData.setRepayRateBasis("F");
		}

		// Repay Base Rate
		extFinData.setRepayBaseRate(finType.getFinBaseRate());

		// Repay Special Rate
		extFinData.setRepaySpecialRate(finType.getFinSplRate());

		// Repay Margin
		extFinData.setRepayMargin(finType.getFinMargin());

		// Repay Profit Rate	
		if (extFinData.getRepayBaseRate() != null) {
			try {
				baseRate = getBaseRate(extFinData.getRepayBaseRate());
				baseRateMap.put(extFinData.getRepayBaseRate(), baseRate);
			} catch (Exception e) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("41002", "", new String[] { "Special Rate",
				                extFinData.getRepaySpecialRate() }), userLangauge).getError());
			}
			if (extFinData.getRepaySpecialRate() != null) {
				try {
					splRate = getSplRate(extFinData.getRepaySpecialRate());
					splRateMap.put(extFinData.getRepaySpecialRate(), splRate);
				} catch (Exception e) {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("41002", "", new String[] { "Base Rate",
					                extFinData.getRepayBaseRate() }), userLangauge).getError());
				}
			}

			if (splRate == null) {
				extFinData.setRepayProfitRate(baseRate.getBRRate().add(zeroValue)
				        .add(extFinData.getRepayMargin()));
			} else {
				extFinData.setRepayProfitRate(baseRate.getBRRate().add(splRate.getSRRate())
				        .add(extFinData.getRepayMargin()));
			}
		} else {
			extFinData.setRepayProfitRate(extFinData.getRepayProfitRate() == null ? zeroValue
			        : extFinData.getRepayProfitRate());
		}

		// Repay Frequency		
		extFinData.setRepayFrq(finType.getFinRpyFrq());

		if (finType.getFinRpyFrq() != null) {
			extFinData.setRepayFrq(finType.getFinRpyFrq().substring(0, 3).concat(dayOfFrq));
		}

		// Next Repay Date
		extFinData.setNextRepayDate(null);

		// Repay ProfitFrequency 
		extFinData.setRepayPftFrq(finType.getFinDftIntFrq());

		if (finType.getFinDftIntFrq() != null) {
			extFinData.setRepayPftFrq(finType.getFinDftIntFrq().substring(0, 3).concat(dayOfFrq));
		}

		// Next Repay pftDate		
		extFinData.setNextRepayPftDate(null);

		// Allow Repay Review
		extFinData.setAllowRepayRvw(finType.isFinIsRvwAlw());

		// Repay Review Frequency		
		extFinData.setRepayRvwFrq(finType.getFinRpyFrq());

		if (finType.getFinRpyFrq() != null) {
			extFinData.setRepayRvwFrq(finType.getFinRpyFrq().substring(0, 3).concat(dayOfFrq));
		}

		// Next Repay Review Date
		extFinData.setNextRepayRvwDate(null);

		// Allow Repay Capitalization
		extFinData.setAllowRepayCpz(finType.isFinIsIntCpz());

		// Repay Cpz Frequency  & Next Reapy Cpz Date		
		if (extFinData.isAllowRepayCpz()) {
			extFinData.setRepayCpzFrq(finType.getFinCpzFrq());

			if (finType.getFinCpzFrq() != null) {
				extFinData.setRepayCpzFrq(finType.getFinCpzFrq().substring(0, 3).concat(dayOfFrq));
			}

		}

		// Maturity Date
		extFinData.setMaturityDate(null);

		//TODO
		//HARD CODED FOR 6 MONTHS FOR TESTING PURPOSE (To handle Bullet Capitalize Deals)
		if (intValue == 1) {
			extFinData.setMaturityDate(DateUtility.addMonths(extFinData.getFinStartDate(), 6));
		}

		// DownPayment
		BigDecimal minDownPayment = finType.getFinMinDownPayAmount();
		extFinData.setDownPayment((extFinData.getFinAmount().multiply(minDownPayment))
		        .divide(new BigDecimal(100)));

		// Required Repay Amount
		extFinData.setReqRepayAmount(zeroValue);

		// Allow Indicative Rate
		extFinData.setAlwIndRate(finType.isFinAlwIndRate());

		// Indicative Base rate		
		if (extFinData.isAlwIndRate()) {
			extFinData.setIndBaseRate(finType.getFinIndBaseRate());
		}

		// Calculate Repay
		extFinData.setCalculateRepay(true);

		// Equal Repay
		extFinData.setEqualRepay(finType.isFinFrEqrepayment());

		logger.debug("Leaving");
		System.out.println("----> END SETTING EXT FINANCE DATA TO FINANCE MAIN OBJ  --------> :: "
		        + DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
		return extFinData;
	}

	/*
	 * VALDIATE EXTERNAL INPUT DATA
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
			        new ErrorDetails("41002", "", new String[] { "Finance Reference", "" }),
			        userLangauge).getError());
			return extFinData;
		}

		// Check whether Finance already exists with same reference and return if so
		if (getFinanceDetailService().isFinReferenceExits(extFinData.getFinReference(), "", false)) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("E0006", "", new String[] { "Finance Reference",
			                extFinData.getFinReference() }), userLangauge).getError());
			return extFinData;
		}
		financeMain.setFinReference(extFinData.getFinReference());

		// Validate BRANCH
		if (extFinData.getFinBranch() == null) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Finance Branch",
			                extFinData.getFinBranch() }), userLangauge).getError());
			return extFinData;
		}

		try {
			if (!branchMap.containsKey(extFinData.getFinBranch())) {
				Branch branch = getBranchDAO().getBranchById(extFinData.getFinBranch(), "");
				if (branch != null) {
					financeMain.setFinBranch(extFinData.getFinBranch());
					branchMap.put(extFinData.getFinBranch(), extFinData.getFinBranch());
				} else {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("41002", "", new String[] { "Finance Branch",
					                extFinData.getFinBranch() }), userLangauge).getError());
					return extFinData;
				}
			} else {
				financeMain.setFinBranch(extFinData.getFinBranch());
			}
		} catch (Exception e) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Finance Branch",
			                extFinData.getFinBranch() }), userLangauge).getError());
			return extFinData;
		}

		// Validate FINANCETYPE
		if (extFinData.getFinType() == null) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Finance Type",
			                extFinData.getFinType() }), userLangauge).getError());
			return extFinData;
		}

		try {
			if (!finTypeMap.containsKey(extFinData.getFinType())) {
				finType = getFinanceTypeDAO().getFinanceTypeByID(extFinData.getFinType(), "");
				if (finType != null) {
					financeMain.setFinType(extFinData.getFinType());
					finTypeMap.put(extFinData.getFinType(), finType);
				} else {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("41002", "", new String[] { "Finance Type",
					                extFinData.getFinType() }), userLangauge).getError());
					return extFinData;
				}
			} else {
				finType = finTypeMap.get(extFinData.getFinType());
				financeMain.setFinType(extFinData.getFinType());
			}
		} catch (Exception e) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Finance Type",
			                extFinData.getFinType() }), userLangauge).getError());
			return extFinData;
		}
		finScheduleData.setFinanceType(finType);

		// REMARKS
		financeMain.setFinRemarks(extFinData.getFinRemarks() == null ? "" : extFinData
		        .getFinRemarks());

		// VALIDATE CURRENCY
		if (extFinData.getFinCcy() == null) {
			if (finType != null && finType.getFinCcy() != null) {
				financeMain.setFinCcy(finType.getFinCcy());
			} else {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("41002", "", new String[] { "Finance Currency",
				                extFinData.getFinCcy() }), userLangauge).getError());
				return extFinData;
			}
		} else {
			try {
				if (!finCcyMap.containsKey(extFinData.getFinCcy())) {
					String currency = getCurrencyDAO().getCurrencyById(extFinData.getFinCcy());
					if (currency != null) {
						financeMain.setFinCcy(extFinData.getFinCcy());
						finCcyMap.put(extFinData.getFinCcy(), extFinData.getFinCcy());
					} else {
						extFinData.setRecordStatus("E");
						extFinData.setErrDesc(ErrorUtil.getErrorDetail(
						        new ErrorDetails("41002", "", new String[] { "Finance Currency",
						                extFinData.getFinCcy() }), userLangauge).getError());
						return extFinData;
					}
				} else {
					financeMain.setFinCcy(extFinData.getFinCcy());
				}
			} catch (Exception e) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("41002", "", new String[] { "Finance Currency",
				                extFinData.getFinCcy() }), userLangauge).getError());
				return extFinData;
			}

		}

		// SCHEDULE METHOD
		if (extFinData.getScheduleMethod() == null && finType.getFinSchdMthd() != null) {
			financeMain.setScheduleMethod(finType.getFinSchdMthd());
		} else if (!extFinData.getScheduleMethod().equals(CalculationConstants.EQUAL)
		        && !extFinData.getScheduleMethod().equals(CalculationConstants.PFT)
		        && !extFinData.getScheduleMethod().equals(CalculationConstants.PRI_PFT)
		        && !extFinData.getScheduleMethod().equals(CalculationConstants.PRI)
		        && !extFinData.getScheduleMethod().equals(CalculationConstants.NOPAY)) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Scheudle Method",
			                extFinData.getScheduleMethod() }), userLangauge).getError());
			return extFinData;
		} else {
			financeMain.setScheduleMethod(extFinData.getScheduleMethod());
		}

		// PROFIT DAYS BASIS
		if (extFinData.getProfitDaysBasis() == null
		        && finType.getLovDescFinDaysCalTypeName() != null) {
			financeMain.setProfitDaysBasis(finType.getLovDescFinDaysCalTypeName());
		} else if (!extFinData.getProfitDaysBasis().equals(CalculationConstants.IDB_30U360)
		        && !extFinData.getProfitDaysBasis().equals(CalculationConstants.IDB_30E360)
		        && !extFinData.getProfitDaysBasis().equals(CalculationConstants.IDB_30E360I)
		        && !extFinData.getProfitDaysBasis().equals(CalculationConstants.IDB_30EP360)
		        && !extFinData.getProfitDaysBasis().equals(CalculationConstants.IDB_ACT_ICMS)
		        && !extFinData.getProfitDaysBasis().equals(CalculationConstants.IDB_ACT_ISDA)
		        && !extFinData.getProfitDaysBasis().equals(CalculationConstants.IDB_ACT_AFB)
		        && !extFinData.getProfitDaysBasis().equals(CalculationConstants.IDB_ACT_365FIXED)
		        && !extFinData.getProfitDaysBasis().equals(CalculationConstants.IDB_ACT_360)
		        && !extFinData.getProfitDaysBasis().equals(CalculationConstants.IDB_ACT_365LEAP)) {

			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Profit Days Basis",
			                extFinData.getProfitDaysBasis() }), userLangauge).getError());
			return extFinData;
		} else {
			financeMain.setProfitDaysBasis(extFinData.getProfitDaysBasis());
		}

		// START DATE
		if (extFinData.getFinStartDate() == null) {
			financeMain.setFinStartDate(dateValueDate);
		} else {
			financeMain.setFinStartDate(extFinData.getFinStartDate());
		}
		financeMain.setLastRepayDate(financeMain.getFinStartDate());
		financeMain.setLastRepayPftDate(financeMain.getFinStartDate());
		financeMain.setLastRepayCpzDate(financeMain.getFinStartDate());
		financeMain.setLastRepayRvwDate(financeMain.getFinStartDate());
		financeMain.setLastDepDate(financeMain.getFinStartDate());

		// FINANCE AMOUNT
		if (extFinData.getFinAmount() == null) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("E0007", "", new String[] { "Finance Amount", "" }),
			        userLangauge).getError());
			return extFinData;
		}

		if (extFinData.getFinAmount().compareTo(zeroValue) <= 0) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("E0007", "", new String[] { extFinData.getFinAmount()
			                .toString() }), userLangauge).getError());
			return extFinData;
		}

		// Minimum amount validation
		if (finType.getFinMinAmount().compareTo(zeroValue) > 0
		        && extFinData.getFinAmount().compareTo(finType.getFinMinAmount()) < 0) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("E0007", "", new String[] { finType.getFinMinAmount()
			                .toString() }), userLangauge).getError());
			return extFinData;
		}

		// Maximum amount validation
		if (finType.getFinMaxAmount().compareTo(zeroValue) > 0
		        && extFinData.getFinAmount().compareTo(finType.getFinMaxAmount()) > 0) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("E0008", "", new String[] { finType.getFinMaxAmount()
			                .toString() }), userLangauge).getError());
			return extFinData;
		}
		financeMain.setFinAmount(extFinData.getFinAmount());

		// FINANCE ASSET VALUE
		if (extFinData.getFinAssetValue() == null) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Finance Asset Value", "" }),
			        userLangauge).getError());
			return extFinData;
		}

		financeMain.setFinAssetValue(extFinData.getFinAssetValue());
		financeMain.setFinCurrAssetValue(extFinData.getFinAssetValue());

		// CUSTOMER DATA
		if (extFinData.getLovDescCustCIF() == null) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Customer",
			                extFinData.getLovDescCustCIF() }), userLangauge).getError());
			return extFinData;
		}

		try {
			if (!customerMap.containsKey(extFinData.getLovDescCustCIF())) {
				customer = getCustomerDAO().getCustomerByCIF(extFinData.getLovDescCustCIF(),"");
				if (customer != null) {
					customerMap.put(extFinData.getLovDescCustCIF(), customer);
				} else {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("41002", "", new String[] { "Customer",
					                extFinData.getLovDescCustCIF() }), userLangauge).getError());
					return extFinData;
				}
			} else {
				customer = customerMap.get(extFinData.getLovDescCustCIF());
			}
			financeMain.setCustID(customer.getCustID());
			financeMain.setLovDescCustCIF(extFinData.getLovDescCustCIF());
			financeMain.setLovDescCustFName(customer.getCustFName());
			financeMain.setLovDescCustShrtName(customer.getCustShrtName());
		} catch (Exception e) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Customer",
			                extFinData.getLovDescCustCIF() }), userLangauge).getError());
			return extFinData;
		}

		// Disbursement Account Id
		if (extFinData.getDisbAccountId() == null) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Disbursement Account",
			                extFinData.getDisbAccountId() }), userLangauge).getError());
			return extFinData;
		}

		// Repayment Account Id
		if (extFinData.getRepayAccountId() == null) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Repayment Account",
			                extFinData.getRepayAccountId() }), userLangauge).getError());
			return extFinData;
		}

		System.out.println("----> START EQUATION HITTING FOR DISB A/C CHECK  --------> :: "
		        + DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
		List<IAccounts> iAccountsList = new ArrayList<IAccounts>();
		IAccounts iAccount = new IAccounts();
		iAccount.setAcCustCIF(financeMain.getLovDescCustCIF());
		iAccount.setAcBranch(financeMain.getFinBranch());
		iAccount.setAcCcy(financeMain.getFinCcy());
		iAccount.setFlagCreateIfNF(false);
		iAccount.setFlagCreateNew(false);
		iAccount.setInternalAc(false);

		iAccount.setAcType("DISB");
		iAccount.setAccountId(extFinData.getDisbAccountId());
		iAccountsList.add(iAccount);

		iAccount.setAcType("REPAY");
		iAccount.setAccountId(extFinData.getRepayAccountId());
		iAccountsList.add(iAccount);

		// Fetch account ids from Equation
		try {
			iAccountsList = getAccountInterfaceService().fetchExistAccount(iAccountsList, "N");
		} catch (Exception e) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Account",
			                extFinData.getDisbAccountId() }), userLangauge).getError());
			return extFinData;
		}

		System.out.println("----> END EQUATION HITTING FOR DISB A/C CHECK  --------> :: "
		        + DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
		try {
			if (accountMap.containsKey(extFinData.getDisbAccountId())) {
				financeMain.setDisbAccountId(extFinData.getDisbAccountId());
			}

			String errorCode = StringUtils.trimToEmpty(iAccountsList.get(0).getErrorCode());

			if (!(errorCode.equals("") || errorCode.equals("0000"))) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("41002", "", new String[] { "Disbursement Account",
				                extFinData.getDisbAccountId() }), userLangauge).getError());
				return extFinData;
			} else {
				financeMain.setDisbAccountId(iAccountsList.get(0).getAccountId());
			}

		} catch (Exception e) {
			e.printStackTrace();
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Disbursement Account",
			                extFinData.getDisbAccountId() }), userLangauge).getError());
			return extFinData;
		}

		try {
			if (accountMap.containsKey(extFinData.getRepayAccountId())) {
				financeMain.setRepayAccountId(extFinData.getRepayAccountId());
			}
			String errorCode = StringUtils.trimToEmpty(iAccountsList.get(1).getErrorCode());
			if (!(errorCode.equals("") || errorCode.equals("0000"))) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("41002", "", new String[] { "Repay Account",
				                extFinData.getRepayAccountId() }), userLangauge).getError());
				return extFinData;
			} else {
				financeMain.setRepayAccountId(iAccountsList.get(1).getAccountId());
			}
		} catch (Exception e) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Repayment Account",
			                extFinData.getRepayAccountId() }), userLangauge).getError());
			return extFinData;
		}

		// Source ID
		financeMain
		        .setFinSourceID(StringUtils.trimToEmpty(extFinData.getFinSourceID()).equals("") ? "EXT"
		                : extFinData.getFinSourceID());

		// NUMBER OF TERMS
		if (extFinData.getNumberOfTerms() > 0 && extFinData.getMaturityDate() != null) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("E0011", "", new String[] { String.valueOf(extFinData
			                .getNumberOfTerms()) }), userLangauge).getError());
			return extFinData;
		}

		if (extFinData.getNumberOfTerms() < 0) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("51003", "", new String[] { String.valueOf(extFinData
			                .getNumberOfTerms()) }), userLangauge).getError());
			return extFinData;
		}

		// If both not requested and default found
		if (extFinData.getNumberOfTerms() == 0 && extFinData.getMaturityDate() == null) {
			financeMain.setNumberOfTerms(finType.getFinDftTerms());

			if (finType.getFinDftTerms() <= 0) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("51003", "", new String[] { String.valueOf(extFinData
				                .getNumberOfTerms()) }), userLangauge).getError());
				return extFinData;
			}

		}

		// Requested terms less than minimum allowed terms
		if (finType.getFinMinTerm() != 0 && extFinData.getNumberOfTerms() < finType.getFinMinTerm()) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("51003", "", new String[] { String.valueOf(extFinData
			                .getNumberOfTerms()) }), userLangauge).getError());
			return extFinData;
		}

		// Requested terms less than maximum allowed terms
		if (finType.getFinMaxTerm() != 0 && extFinData.getNumberOfTerms() > finType.getFinMaxTerm()) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("51003", "", new String[] { String.valueOf(extFinData
			                .getNumberOfTerms()) }), userLangauge).getError());
			return extFinData;
		}
		financeMain.setNumberOfTerms(extFinData.getNumberOfTerms());

		// REPAY DEFERMENTS
		if (finType.isFinIsAlwDifferment()) {

			if (extFinData.getDefferments() < 0) {
				financeMain.setDefferments(finType.getFinMaxDifferment());
			} else if (extFinData.getDefferments() >= 0
			        && extFinData.getDefferments() <= finType.getFinMaxDifferment()) {
				financeMain.setDefferments(extFinData.getDefferments());
			} else {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("E0032", "", new String[] {
				                String.valueOf(extFinData.getDefferments()),
				                String.valueOf(finType.getFinMaxDifferment()) }), userLangauge)
				        .getError());
				return extFinData;
			}
		}

		// FREQUENCY DEFERMENT
		if (finType.isFinIsAlwFrqDifferment()) {

			if (extFinData.getFrqDefferments() < 0) {
				financeMain.setFrqDefferments(finType.getFinMaxFrqDifferment());
			} else if (extFinData.getFrqDefferments() >= 0
			        && extFinData.getFrqDefferments() <= finType.getFinMaxFrqDifferment()) {
				financeMain.setFrqDefferments(extFinData.getFrqDefferments());
			} else {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("E0032", "", new String[] {
				                String.valueOf(extFinData.getFrqDefferments()),
				                String.valueOf(finType.getFinMaxFrqDifferment()) }), userLangauge)
				        .getError());
				return extFinData;
			}
		}

		// Collateral Reference
		financeMain.setFinCommitmentRef(StringUtils.trimToEmpty(extFinData.getFinCommitmentRef())
		        .equals("") ? "" : extFinData.getFinCommitmentRef());

		// Depreciation Frequency
		if (finType.isFinDepreciationReq()) {
			if (extFinData.getDepreciationFrq() == null) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("C0001", "", new String[] { extFinData
				                .getDepreciationFrq() }), userLangauge).getError());
				return extFinData;
			}

			if (FrequencyUtil.validateFrequency(extFinData.getDepreciationFrq()) != null) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("C0001", "", new String[] { extFinData
				                .getDepreciationFrq() }), userLangauge).getError());
				return extFinData;
			}

			financeMain.setDepreciationFrq(extFinData.getDepreciationFrq());

			// Next Depreciation Date
			if (extFinData.getNextDepDate() == null) {
				financeMain.setNextDepDate(FrequencyUtil.getNextDate(
				        financeMain.getDepreciationFrq(), 1, financeMain.getFinStartDate(), "A",
				        false).getNextFrequencyDate());
				financeMain.setNextDepDate(DateUtility.getDate(DateUtility.formatUtilDate(
				        financeMain.getNextDepDate(), PennantConstants.dateFormat)));
			} else if (extFinData.getNextDepDate().before(financeMain.getFinStartDate())) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("E0012", "", new String[] {
				                extFinData.getNextDepDate().toString(),
				                financeMain.getFinStartDate().toString() }), userLangauge)
				        .getError());
				return extFinData;
			} else {
				financeMain.setNextDepDate(extFinData.getNextDepDate());
			}
		}

		// Finance Contract Date
		if (extFinData.getFinContractDate() == null) {
			financeMain.setFinContractDate(extFinData.getFinStartDate());
		} else if (extFinData.getFinContractDate().after(financeMain.getFinStartDate())) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("E0010", "", new String[] {
			                extFinData.getFinContractDate().toString(),
			                financeMain.getFinStartDate().toString() }), userLangauge).getError());
			return extFinData;
		} else {
			financeMain.setFinContractDate(extFinData.getFinContractDate());
		}

		/*
		 * GRACE PERIOD
		 */
		// ALLOW GRACE
		if (!finType.isFInIsAlwGrace()) {
			financeMain.setAllowGrcPeriod(false);
			financeMain.setGrcPeriodEndDate(extFinData.getFinStartDate());
		} else {
			financeMain.setAllowGrcPeriod(true);
		}

		if (financeMain.isAllowGrcPeriod()) {
			if (!validateGraceDetails(extFinData, financeMain, finType)) {
				return extFinData;
			}
		} else {
			financeMain.setGrcCpzFrq("");
			financeMain.setNextGrcCpzDate(null);
			financeMain.setGrcPftFrq("");
			financeMain.setNextGrcPftDate(null);
			financeMain.setGrcPftRvwFrq("");
			financeMain.setNextGrcPftRvwDate(null);
		}

		// Capitalization at grace End
		financeMain.setCpzAtGraceEnd(true);

		/*
		 * REPAY PERIOD
		 */
		// Repay Rate Basis
		if (extFinData.getRepayRateBasis() == null && finType.getFinRateType() != null) {
			financeMain.setRepayRateBasis(finType.getFinRateType());
		} else if (!extFinData.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_R)
		        && !extFinData.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_F)) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Rate Basis",
			                extFinData.getRepayRateBasis() }), userLangauge).getError());
			return extFinData;
		} else {
			financeMain.setRepayRateBasis(extFinData.getRepayRateBasis());
		}

		// Repay Base Rate
		if (finType.getFinBaseRate() != null) {
			if (extFinData.getRepayBaseRate() == null || extFinData.getRepayBaseRate().equals("")) {
				financeMain.setRepayBaseRate(finType.getFinBaseRate());
			} else {
				try {
					if (!baseRateMap.containsKey(extFinData.getRepayBaseRate())) {
						baseRate = getBaseRate(extFinData.getRepayBaseRate());
						if (baseRate != null) {
							baseRateMap.put(extFinData.getRepayBaseRate(), baseRate);
						} else {
							extFinData.setRecordStatus("E");
							extFinData.setErrDesc(ErrorUtil.getErrorDetail(
							        new ErrorDetails("41002", "", new String[] { "Base Rate",
							                extFinData.getRepayBaseRate() }), userLangauge)
							        .getError());
							return extFinData;
						}
					} else {
						baseRate = baseRateMap.get(extFinData.getRepayBaseRate());
					}
					financeMain.setRepayBaseRate(extFinData.getRepayBaseRate());
				} catch (Exception e) {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("41002", "", new String[] { "Base Rate",
					                extFinData.getRepayBaseRate() }), userLangauge).getError());
					return extFinData;
				}
			}
		}
		// Repay Special Rate
		if (finType.getFinSplRate() != null) {
			if (extFinData.getRepaySpecialRate() == null
			        || extFinData.getRepaySpecialRate().equals("")) {
				financeMain.setRepaySpecialRate(finType.getFinSplRate());
			} else {
				try {
					if (!splRateMap.containsKey(extFinData.getRepaySpecialRate())) {
						splRate = getSplRate(extFinData.getRepaySpecialRate());
						if (splRate != null) {
							splRateMap.put(extFinData.getRepaySpecialRate(), splRate);
						} else {
							extFinData.setRecordStatus("E");
							extFinData.setErrDesc(ErrorUtil.getErrorDetail(
							        new ErrorDetails("41002", "", new String[] { "Special Rate",
							                extFinData.getRepaySpecialRate() }), userLangauge)
							        .getError());
							return extFinData;
						}
					} else {
						splRate = splRateMap.get(extFinData.getRepaySpecialRate());
					}
					financeMain.setRepaySpecialRate(extFinData.getRepaySpecialRate());
				} catch (Exception e) {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("41002", "", new String[] { "Special Rate",
					                extFinData.getRepaySpecialRate() }), userLangauge).getError());
					return extFinData;
				}
			}
		}

		// Repay Margin
		if (extFinData.getRepayMargin() != null) {
			financeMain.setRepayMargin(extFinData.getRepayMargin());
		} else if (finType.getFinMargin() != null) {
			financeMain.setRepayMargin(finType.getFinMargin());
		}

		// Calculate Repay Profit Rate
		if (financeMain.getRepayBaseRate() != null) {
			if (splRate == null) {
				financeMain.setRepayProfitRate(baseRate.getBRRate().add(zeroValue)
				        .add(financeMain.getRepayMargin()));
			} else {
				financeMain.setRepayProfitRate(baseRate.getBRRate().add(splRate.getSRRate())
				        .add(financeMain.getRepayMargin()));
			}
		} else {
			financeMain.setRepayProfitRate(extFinData.getRepayProfitRate() == null ? zeroValue
			        : extFinData.getRepayProfitRate());
		}

		// Repay profit Frequency
		if (finType.getFinDftIntFrq() != null) {
			if (FrequencyUtil.validateFrequency(extFinData.getRepayPftFrq()) != null) {
				if (FrequencyUtil.validateFrequency(finType.getFinDftIntFrq()) != null) {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("C0001", "", new String[] { extFinData
					                .getRepayPftFrq() }), userLangauge).getError());
					return extFinData;
				}
				financeMain.setRepayPftFrq(extFinData.getRepayPftFrq());
			}
			financeMain.setRepayPftFrq(extFinData.getRepayPftFrq());

			// Next Repay profit Date		
			if (extFinData.getNextRepayPftDate() == null) {
				financeMain.setNextRepayPftDate(FrequencyUtil.getNextDate(
				        financeMain.getRepayPftFrq(), 1, financeMain.getGrcPeriodEndDate(), "A",
				        false).getNextFrequencyDate());
				financeMain.setNextRepayPftDate(DateUtility.getDate(DateUtility.formatUtilDate(
				        financeMain.getNextRepayPftDate(), PennantConstants.dateFormat)));
			} else if (extFinData.getNextRepayPftDate().before(financeMain.getGrcPeriodEndDate())) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("E0024", "", new String[] {
				                extFinData.getNextRepayPftDate().toString(),
				                financeMain.getGrcPeriodEndDate().toString() }), userLangauge)
				        .getError());
				return extFinData;
			} else {
				financeMain.setNextRepayPftDate(extFinData.getNextRepayPftDate());
			}
		} else {
			financeMain.setRepayPftFrq("");
		}

		// Allow Repay Review
		if (finType.isFinIsRvwAlw() && extFinData.isAllowRepayRvw()) {
			financeMain.setAllowRepayRvw(extFinData.isAllowRepayRvw());
		} else {
			financeMain.setAllowRepayRvw(finType.isFinIsRvwAlw());
		}

		if (financeMain.isAllowRepayRvw()) {
			// Repay Review Frequency
			if (FrequencyUtil.validateFrequency(extFinData.getRepayRvwFrq()) != null) {
				if (FrequencyUtil.validateFrequency(finType.getFinRpyFrq()) != null) {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("C0001", "", new String[] { extFinData
					                .getRepayRvwFrq() }), userLangauge).getError());
					return extFinData;
				}
				financeMain.setRepayRvwFrq(finType.getFinRpyFrq());
			}
			financeMain.setRepayRvwFrq(extFinData.getRepayRvwFrq());

			// Next Repay Review Date
			if (extFinData.getNextRepayRvwDate() == null) {
				financeMain.setNextRepayRvwDate(FrequencyUtil.getNextDate(
				        financeMain.getRepayRvwFrq(), 1, financeMain.getGrcPeriodEndDate(), "A",
				        false).getNextFrequencyDate());
				financeMain.setNextRepayRvwDate(DateUtility.getDate(DateUtility.formatUtilDate(
				        financeMain.getNextRepayRvwDate(), PennantConstants.dateFormat)));
			} else if (extFinData.getNextRepayRvwDate().before(financeMain.getGrcPeriodEndDate())) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("E0025", "", new String[] {
				                extFinData.getNextRepayRvwDate().toString(),
				                financeMain.getGrcPeriodEndDate().toString() }), userLangauge)
				        .getError());
				return extFinData;
			} else {
				financeMain.setNextRepayRvwDate(extFinData.getNextRepayRvwDate());
			}

			//Review Rate Applied For in Repay
			financeMain.setFinRvwRateApplFor(finType.getFinRvwRateApplFor());
		} else {
			financeMain.setRepayRvwFrq("");
			financeMain.setFinRvwRateApplFor("");
		}

		// Allow Repay Capitalization
		if (finType.isFinIsIntCpz() && extFinData.isAllowRepayCpz()) {
			financeMain.setAllowRepayCpz(extFinData.isAllowRepayCpz());
		} else {
			financeMain.setAllowRepayCpz(finType.isFinIsIntCpz());
		}

		if (financeMain.isAllowRepayCpz()) {
			// Repay Cpz Frequency
			if (FrequencyUtil.validateFrequency(extFinData.getRepayCpzFrq()) != null) {
				if (FrequencyUtil.validateFrequency(finType.getFinCpzFrq()) != null) {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("C0001", "",
					                new String[] { "Repay Capitalization Frequency" }),
					        userLangauge).getError());
					return extFinData;
				}
				financeMain.setRepayCpzFrq(finType.getFinCpzFrq());
			}
			financeMain.setRepayCpzFrq(extFinData.getRepayCpzFrq());

			// Next Reapy Cpz Date
			if (extFinData.getNextRepayCpzDate() == null) {
				financeMain.setNextRepayCpzDate(FrequencyUtil.getNextDate(
				        financeMain.getRepayCpzFrq(), 1, financeMain.getGrcPeriodEndDate(), "A",
				        false).getNextFrequencyDate());
				financeMain.setNextRepayCpzDate(DateUtility.getDate(DateUtility.formatUtilDate(
				        financeMain.getNextRepayCpzDate(), PennantConstants.dateFormat)));
			}

			if (financeMain.getNextRepayCpzDate().before(financeMain.getGrcPeriodEndDate())) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("E0026", "", new String[] {
				                extFinData.getNextRepayCpzDate().toString(),
				                financeMain.getGrcPeriodEndDate().toString() }), userLangauge)
				        .getError());
				return extFinData;
			}

			if (financeMain.getNextRepayCpzDate().before(financeMain.getNextRepayPftDate())) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("E0029", "", new String[] {
				                extFinData.getNextRepayCpzDate().toString(),
				                financeMain.getNextRepayPftDate().toString() }), userLangauge)
				        .getError());
				return extFinData;
			}

			//financeMain.setNextRepayCpzDate(extFinData.getNextRepayCpzDate());
		} else {
			financeMain.setRepayCpzFrq("");
		}

		// Repay Frequency
		if (FrequencyUtil.validateFrequency(extFinData.getRepayFrq()) != null) {
			if (FrequencyUtil.validateFrequency(finType.getFinRpyFrq()) != null) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("C0001", "", new String[] { extFinData.getRepayFrq() }),
				        userLangauge).getError());
				return extFinData;
			}
			financeMain.setRepayFrq(finType.getFinRpyFrq());
		}
		financeMain.setRepayFrq(extFinData.getRepayFrq());

		// Next Repay Date
		if (extFinData.getNextRepayDate() == null) {
			financeMain.setNextRepayDate(FrequencyUtil.getNextDate(financeMain.getRepayFrq(), 1,
			        financeMain.getGrcPeriodEndDate(), "A", false).getNextFrequencyDate());
			financeMain.setNextRepayDate(DateUtility.getDate(DateUtility.formatUtilDate(
			        financeMain.getNextRepayDate(), PennantConstants.dateFormat)));
		} else {
			financeMain.setNextRepayDate(extFinData.getNextRepayDate());
		}

		if (financeMain.getGrcPeriodEndDate().after(financeMain.getNextRepayDate())) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("E0023", "", new String[] {
			                extFinData.getNextRepayDate().toString(),
			                financeMain.getGrcPeriodEndDate().toString() }), userLangauge)
			        .getError());
			return extFinData;
		}

		//financeMain.setNextRepayDate(financeMain.getNextRepayDate());

		// Maturity Date
		if (extFinData.getMaturityDate() == null) {
			if (financeMain.getNumberOfTerms() > 0) {
				List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(
				        financeMain.getRepayFrq(), financeMain.getNumberOfTerms(),
				        financeMain.getNextRepayDate(), "A", true).getScheduleList();
				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					financeMain.setMaturityDate(calendar.getTime());
					financeMain.setMaturityDate(DateUtility.getDate(DateUtility.formatUtilDate(
					        financeMain.getMaturityDate(), PennantConstants.dateFormat)));
				}
			}
		}

		// Set Next Repayment date to maturiy date
		if (financeMain.getMaturityDate().before(financeMain.getNextRepayDate())) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("E0028", "", new String[] {
			                extFinData.getMaturityDate().toString(),
			                financeMain.getNextRepayDate().toString() }), userLangauge).getError());
			return extFinData;
		}

		//Set Next Repay Profit date to maturiy date
		if (financeMain.getMaturityDate().before(financeMain.getNextRepayPftDate())) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("E0028", "", new String[] {
			                extFinData.getMaturityDate().toString(),
			                financeMain.getNextRepayPftDate().toString() }), userLangauge)
			        .getError());
			return extFinData;
		}

		//Set Next Rpay Capitalization date to maturiy date
		if (financeMain.isAllowRepayCpz()
		        && financeMain.getMaturityDate().before(financeMain.getNextRepayCpzDate())) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("E0028", "", new String[] {
			                extFinData.getMaturityDate().toString(),
			                financeMain.getNextRepayCpzDate().toString() }), userLangauge)
			        .getError());
			return extFinData;
		}

		// Set depreciation date to maturiy date
		if (financeMain.getNextDepDate().after(financeMain.getMaturityDate())) {
			financeMain.setNextDepDate(financeMain.getMaturityDate());
		}

		if (financeMain.getNumberOfTerms() == 1) {
			financeMain.setNextRepayDate(financeMain.getMaturityDate());
		}

		// DownPayment
		if (finType.isFinIsDwPayRequired()) {
			if (extFinData.getDownPayment() != null) {
				if (extFinData.getDownPayment().compareTo(
				        getPercentageValue(financeMain.getFinAmount(),
				                finType.getFinMinDownPayAmount())) >= 0) {
					financeMain.setDownPayment(extFinData.getDownPayment());
				} else {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("S0001", "", new String[] {
					                "Down Payment",
					                getPercentageValue(financeMain.getFinAmount(),
					                        finType.getFinMinDownPayAmount()).toString() }),
					        userLangauge).getError());
					return extFinData;
				}
			} else {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("S0001", "", new String[] {
				                "Down Payment",
				                getPercentageValue(financeMain.getFinAmount(),
				                        finType.getFinMinDownPayAmount()).toString() }),
				        userLangauge).getError());
				return extFinData;
			}
		} else {
			financeMain.setDownPayment(zeroValue);
		}

		// Required Repay Amount
		if (extFinData.getReqRepayAmount() != null) {
			financeMain.setFinRepaymentAmount(extFinData.getReqRepayAmount());
		} else {
			financeMain.setFinRepaymentAmount(zeroValue);
		}

		// Allow Indicative Rate
		if (finType.isFinAlwIndRate() && extFinData.isAlwIndRate()) {
			financeMain.setAlwIndRate(extFinData.isAlwIndRate());
		} else {
			financeMain.setAlwIndRate(false);
		}

		// Indicative Base rate
		if (financeMain.isAlwIndRate()) {
			if (extFinData.getIndBaseRate() == null) {
				if (finType.getFinIndBaseRate() != null) {
					financeMain.setIndBaseRate(finType.getFinIndBaseRate());
				}
			} else {
				try {
					if (!baseRateMap.containsKey(extFinData.getIndBaseRate())) {
						baseRate = getBaseRate(extFinData.getIndBaseRate());
						if (baseRate != null) {
							baseRateMap.put(extFinData.getIndBaseRate(), baseRate);
						} else {
							extFinData.setRecordStatus("E");
							extFinData.setErrDesc(ErrorUtil
							        .getErrorDetail(
							                new ErrorDetails("41002", "", new String[] {
							                        "Base Rate", "" }), userLangauge).getError());
							return extFinData;
						}
					} else {
						baseRate = baseRateMap.get(extFinData.getIndBaseRate());
					}
					financeMain.setIndBaseRate(finType.getFinIndBaseRate());
				} catch (Exception e) {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("41002", "", new String[] { "Base Rate", "" }),
					        userLangauge).getError());
					return extFinData;
				}
			}
		}

		// Calculate Repay
		financeMain.setCalculateRepay(true);
		financeMain.setReqRepayAmount(zeroValue);
		if (extFinData.getReqRepayAmount() != null
		        && extFinData.getReqRepayAmount().compareTo(zeroValue) == 1) {
			financeMain.setCalculateRepay(false);
			financeMain.setReqRepayAmount(extFinData.getReqRepayAmount());
		}

		// Equal Repay
		financeMain.setEqualRepay(finType.isFinFrEqrepayment());

		// Set recal type from finance type
		if (finType.isFinIsRvwAlw()) {
			financeMain.setRecalType(finType.getFinSchCalCodeOnRvw());
		} else {
			financeMain.setRecalType("");
		}

		//Finance is Active
		financeMain.setFinIsActive(true);

		//Version
		financeMain.setVersion(1);

		// User Details
		financeMain.setLastMntBy(userID);
		financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		// Record Status
		financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		setFinMain(financeMain);

		// Storing original financemain data to reset
		old_finMain = new FinanceMain();
		BeanUtils.copyProperties(financeMain, old_finMain);
		System.out.println("----> END VALIDATION FINANCE UPLOAD  --------> :: "
		        + DateUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
		return extFinData;
	}

	private static BigDecimal getPercentageValue(BigDecimal amount, BigDecimal percent) {
		BigDecimal bigDecimal = BigDecimal.ZERO;

		if (amount != null) {
			bigDecimal = (amount.multiply(unFormateAmount(percent, 2).divide(new BigDecimal(100))))
			        .divide(new BigDecimal(100));
		}
		return bigDecimal;
	}

	private static BigDecimal unFormateAmount(BigDecimal amount, int dec) {

		if (amount == null) {
			return BigDecimal.ZERO;
		}
		BigInteger bigInteger = amount.multiply(new BigDecimal(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}

	/**
	 * Method to validate grace period detials
	 * 
	 * @param extFinData
	 * @param financeMain
	 * @param finType
	 * @param alwGrace
	 * 
	 * @return boolean
	 * */
	private boolean validateGraceDetails(ExtFinanceData extFinData, FinanceMain financeMain,
	        FinanceType finType) {

		logger.debug("Entering");

		// Grace Period End Date
		if (extFinData.getGrcPeriodEndDate() == null) {
			financeMain.setGrcPeriodEndDate(extFinData.getFinStartDate());
		}

		if (extFinData.getGrcPeriodEndDate().before(financeMain.getFinStartDate())) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("E0018", "", new String[] {
			                extFinData.getGrcPeriodEndDate().toString(),
			                financeMain.getFinStartDate().toString() }), userLangauge).getError());
			return false;
		} else {
			financeMain.setGrcPeriodEndDate(extFinData.getGrcPeriodEndDate());
		}

		// Grace Rate Basis
		if (extFinData.getGrcRateBasis() == null && finType.getFinGrcRateType() != null) {
			financeMain.setGrcRateBasis(finType.getFinGrcRateType());
		} else if (!extFinData.getGrcRateBasis().equals(CalculationConstants.RATE_BASIS_R)			//TODO CalculationConstants.IDB_ACT_360																						
		        && !extFinData.getGrcRateBasis().equals(CalculationConstants.RATE_BASIS_F)) {
			extFinData.setRecordStatus("E");
			extFinData.setErrDesc(ErrorUtil.getErrorDetail(
			        new ErrorDetails("41002", "", new String[] { "Rate Basis",
			                extFinData.getGrcRateBasis() }), userLangauge).getError());
			return false;
		} else {
			financeMain.setGrcRateBasis(extFinData.getGrcRateBasis());
		}

		// Grace Base Rate
		if (finType.getFinGrcBaseRate() != null) {
			if (extFinData.getGraceBaseRate() == null || extFinData.getGraceBaseRate().equals("")) {
				financeMain.setGraceBaseRate(finType.getFinGrcBaseRate());
			} else {
				try {
					if (!baseRateMap.containsKey(extFinData.getGraceBaseRate())) {
						baseRate = getBaseRate(extFinData.getGraceBaseRate());
						if (baseRate != null) {
							baseRateMap.put(extFinData.getGraceBaseRate(), baseRate);
						} else {
							extFinData.setRecordStatus("E");
							extFinData.setErrDesc(ErrorUtil.getErrorDetail(
							        new ErrorDetails("41002", "", new String[] { "Base Rate",
							                extFinData.getGraceBaseRate() }), userLangauge)
							        .getError());
							return false;
						}
					} else {
						baseRate = baseRateMap.get(extFinData.getRepayBaseRate());
					}
					financeMain.setGraceBaseRate(extFinData.getGraceBaseRate());
				} catch (Exception e) {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("41002", "", new String[] { "Base Rate",
					                extFinData.getGraceBaseRate() }), userLangauge).getError());
					return false;
				}
			}
		}

		// Grace Special Rate
		if (finType.getFinGrcSplRate() != null) {
			if (extFinData.getGraceSpecialRate() == null
			        || extFinData.getGraceSpecialRate().equals("")) {
				financeMain.setGraceSpecialRate(finType.getFinGrcSplRate());
			} else {
				try {
					if (!splRateMap.containsKey(extFinData.getGraceSpecialRate())) {
						splRate = getSplRate(extFinData.getGraceSpecialRate());
						if (splRate != null) {
							splRateMap.put(extFinData.getGraceSpecialRate(), splRate);
						} else {
							extFinData.setRecordStatus("E");
							extFinData.setErrDesc(ErrorUtil.getErrorDetail(
							        new ErrorDetails("41002", "", new String[] { "Special Rate",
							                extFinData.getGraceSpecialRate() }), userLangauge)
							        .getError());
							return false;
						}
					} else {
						splRate = splRateMap.get(extFinData.getGraceSpecialRate());
					}
					financeMain.setGraceSpecialRate(extFinData.getGraceSpecialRate());
				} catch (Exception e) {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("41002", "", new String[] { "Special Rate",
					                extFinData.getGraceSpecialRate() }), userLangauge).getError());
					return false;
				}
			}
		}

		// Grace Margin
		if (extFinData.getGrcMargin() != null) {
			financeMain.setGrcMargin(extFinData.getGrcMargin());
		} else if (finType.getFinGrcMargin() != null) {
			financeMain.setGrcMargin(finType.getFinGrcMargin());
		}

		// Calculate Grace Profit Rate
		if (financeMain.getGraceBaseRate() != null) {
			if (splRate == null) {
				financeMain.setGrcPftRate(baseRate.getBRRate().add(zeroValue)
				        .add(financeMain.getGrcMargin()));
			} else {
				financeMain.setGrcPftRate(baseRate.getBRRate().add(splRate.getSRRate())
				        .add(financeMain.getGrcMargin()));
			}
		} else {
			financeMain.setGrcPftRate(extFinData.getGrcPftRate() == null ? zeroValue : extFinData
			        .getGrcPftRate());
		}

		// Grace Profit Frequency
		if (finType.getFinGrcDftIntFrq() != null) {
			if (FrequencyUtil.validateFrequency(extFinData.getGrcPftFrq()) != null) {
				if (FrequencyUtil.validateFrequency(finType.getFinGrcDftIntFrq()) != null) {
					extFinData.setRecordStatus("E");
					extFinData.setErrDesc(ErrorUtil.getErrorDetail(
					        new ErrorDetails("C0001", "",
					                new String[] { extFinData.getGrcPftFrq() }), userLangauge)
					        .getError());
					return false;
				}
				financeMain.setGrcPftFrq(finType.getFinGrcDftIntFrq());
			}
			financeMain.setGrcPftFrq(extFinData.getGrcPftFrq());

			// Next Grace Profit Date
			if (extFinData.getNextGrcPftDate() == null) {
				financeMain.setNextGrcPftDate(FrequencyUtil.getNextDate(financeMain.getGrcPftFrq(),
				        1, financeMain.getFinStartDate(), "A", false).getNextFrequencyDate());
				financeMain.setNextGrcPftDate(DateUtility.getDate(DateUtility.formatUtilDate(
				        financeMain.getNextGrcPftDate(), PennantConstants.dateFormat)));
				if (financeMain.getNextGrcPftDate().after(financeMain.getGrcPeriodEndDate())) {
					financeMain.setNextGrcPftDate(financeMain.getGrcPeriodEndDate());
				}
			} else if (extFinData.getNextGrcPftDate().after(financeMain.getGrcPeriodEndDate())) {
				financeMain.setNextGrcPftDate(financeMain.getGrcPeriodEndDate());
			} else {
				financeMain.setNextGrcPftDate(extFinData.getNextGrcPftDate());
			}
		} else {
			financeMain.setGrcPftFrq("");
		}

		// Allow Grace Profit Review
		if (finType.isFinGrcIsRvwAlw()) {
			if (extFinData.isAllowGrcPftRvw()) {
				financeMain.setAllowGrcPftRvw(extFinData.isAllowGrcPftRvw());
			}
		} else {
			financeMain.setAllowGrcPftRvw(finType.isFinGrcIsRvwAlw());
		}

		// Grace profit Review Frequency
		if (financeMain.isAllowGrcPftRvw()) {
			if (finType.getFinGrcRvwFrq() != null) {
				if (FrequencyUtil.validateFrequency(extFinData.getGrcPftRvwFrq()) != null) {
					if (FrequencyUtil.validateFrequency(finType.getFinGrcRvwFrq()) != null) {
						extFinData.setRecordStatus("E");
						extFinData.setErrDesc(ErrorUtil.getErrorDetail(
						        new ErrorDetails("C0001", "", new String[] { extFinData
						                .getGrcPftRvwFrq() }), userLangauge).getError());
						return false;
					}
					financeMain.setGrcPftRvwFrq(finType.getFinGrcRvwFrq());
				}
				financeMain.setGrcPftRvwFrq(extFinData.getGrcPftRvwFrq());

				// Next Grace profit Review Date
				if (extFinData.getNextGrcPftRvwDate() == null) {
					financeMain.setNextGrcPftRvwDate(FrequencyUtil.getNextDate(
					        financeMain.getGrcPftRvwFrq(), 1, financeMain.getFinStartDate(), "A",
					        false).getNextFrequencyDate());
					financeMain.setNextGrcPftRvwDate(DateUtility.getDate(DateUtility
					        .formatUtilDate(financeMain.getNextGrcPftRvwDate(),
					                PennantConstants.dateFormat)));
					if (financeMain.getNextGrcPftRvwDate().after(financeMain.getGrcPeriodEndDate())) {
						financeMain.setNextGrcPftRvwDate(financeMain.getGrcPeriodEndDate());
					}
				} else if (extFinData.getNextGrcPftRvwDate().after(
				        financeMain.getGrcPeriodEndDate())) {
					financeMain.setNextGrcPftRvwDate(financeMain.getGrcPeriodEndDate());
				} else {
					financeMain.setNextGrcPftRvwDate(extFinData.getNextGrcPftRvwDate());
				}
				//Review Rate Applied For in Grace
				financeMain.setFinGrcRvwRateApplFor(finType.getFinGrcRvwRateApplFor());
			} else {
				financeMain.setGrcPftRvwFrq("");
				financeMain.setFinGrcRvwRateApplFor("");
			}
		} else {
			financeMain.setNextGrcPftRvwDate((Date) SystemParameterDetails
			        .getSystemParameterValue("APP_DFT_ENDDATE"));
			financeMain.setGrcPftRvwFrq("");
			financeMain.setFinGrcRvwRateApplFor("");
		}

		// Allow Grace Capitalization
		if (finType.isFinGrcIsIntCpz()) {
			if (extFinData.isAllowGrcCpz()) {
				financeMain.setAllowGrcCpz(extFinData.isAllowGrcCpz());
			}
		} else {
			financeMain.setAllowGrcCpz(finType.isFinGrcIsIntCpz());
		}

		if (financeMain.isAllowGrcCpz()) {
			// Grace Cpz Frequency
			if (!StringUtils.trimToEmpty(finType.getFinGrcCpzFrq()).equals("")) {
				if (FrequencyUtil.validateFrequency(extFinData.getGrcCpzFrq()) != null) {
					if (FrequencyUtil.validateFrequency(finType.getFinGrcCpzFrq()) != null) {
						extFinData.setRecordStatus("E");
						extFinData.setErrDesc(ErrorUtil.getErrorDetail(
						        new ErrorDetails("C0001", "", new String[] { extFinData
						                .getGrcCpzFrq() }), userLangauge).getError());
						return false;
					}
					financeMain.setGrcCpzFrq(finType.getFinGrcCpzFrq());
				}
				financeMain.setGrcCpzFrq(extFinData.getGrcCpzFrq());

				// Next Grace Cpz Date
				if (extFinData.getNextGrcCpzDate() == null) {
					financeMain.setNextGrcCpzDate(FrequencyUtil.getNextDate(
					        financeMain.getGrcCpzFrq(), 1, financeMain.getFinStartDate(), "A",
					        false).getNextFrequencyDate());
					financeMain.setNextGrcCpzDate(DateUtility.getDate(DateUtility.formatUtilDate(
					        financeMain.getNextGrcCpzDate(), PennantConstants.dateFormat)));
					if (financeMain.getNextGrcCpzDate().after(financeMain.getGrcPeriodEndDate())) {
						financeMain.setNextGrcCpzDate(financeMain.getGrcPeriodEndDate());
					}
				} else if (extFinData.getNextGrcCpzDate().after(financeMain.getGrcPeriodEndDate())) {
					financeMain.setNextGrcCpzDate(financeMain.getGrcPeriodEndDate());
				} else {
					financeMain.setNextGrcCpzDate(extFinData.getNextGrcCpzDate());
				}
			} else {
				financeMain.setGrcCpzFrq("");
			}
		} else {
			financeMain.setNextGrcCpzDate((Date) SystemParameterDetails
			        .getSystemParameterValue("APP_DFT_ENDDATE"));
			financeMain.setGrcCpzFrq("");
		}

		// Allow Grace repay
		if (finType.isFinIsAlwGrcRepay()) {
			if (extFinData.isAllowGrcRepay()) {
				financeMain.setAllowGrcRepay(extFinData.isAllowGrcRepay());
			}
		} else {
			financeMain.setAllowGrcRepay(finType.isFinIsAlwGrcRepay());
		}

		// Grace Schedule Method
		if (financeMain.isAllowGrcRepay()) {
			if (extFinData.getGrcSchdMthd() == null) {
				financeMain.setGrcSchdMthd(finType.getFinGrcSchdMthd());
			} else if (!extFinData.getGrcSchdMthd().equals(CalculationConstants.PFT)
			        && !extFinData.getGrcSchdMthd().equals(CalculationConstants.NOPAY)) {
				extFinData.setRecordStatus("E");
				extFinData.setErrDesc(ErrorUtil.getErrorDetail(
				        new ErrorDetails("41002", "", new String[] { "Scheudle Method",
				                extFinData.getGrcSchdMthd() }), userLangauge).getError());
				return false;
			} else {
				financeMain.setGrcSchdMthd(extFinData.getGrcSchdMthd());
			}
		}
		return true;
	}

	/**
	 * Method to get Base Rate
	 * 
	 * @param extFinData
	 * @return BaseRate
	 * */
	private BaseRate getBaseRate(String baseRate) {
		return getBaseRateDAO().getBaseRateByType(baseRate, dateValueDate);
	}

	/**
	 * Method to get Special Rate
	 * 
	 * @param extFinData
	 * @return Special Rate
	 * */
	private SplRate getSplRate(String specialRate) {
		return getSplRateDAO().getSplRateByID(specialRate, dateValueDate);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public FinanceMain getOld_finMain() {
		return old_finMain;
	}

	public void setOld_finMain(FinanceMain oldFinMain) {
		this.old_finMain = oldFinMain;
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

	public void setHostConnection(HostConnection hostConnection) {
		this.hostConnection = hostConnection;
	}

	public HostConnection getHostConnection() {
		return hostConnection;
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

}
