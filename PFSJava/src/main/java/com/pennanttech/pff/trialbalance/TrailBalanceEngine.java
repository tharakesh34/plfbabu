package com.pennanttech.pff.trialbalance;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.TrailBalance;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class TrailBalanceEngine extends DataEngineExport {
	public static DataEngineStatus EXTRACT_STATUS = new DataEngineStatus();
	private static final Logger logger = Logger.getLogger(TrailBalanceEngine.class);

	private int batchSize = 1000;
	private Date appDate = null;
	// private Date startDate = null;
	// private Date endDate = null;
	private long headerId = 0;
	private long seqNo = 0;
	private Dimension dimension = null;
	private String entityCode = null;
	private String entityDescription = null;
	private Date fromDate = null;
	private Date toDate = null;
	private String stateCode = null;
	Date financeEndDate = null;
	int recordCount = 0;
	private boolean stateWiseReport;
	private List<String> stateCodeList = null;

	private static final String QUERY_CONSOLIDATE = "select distinct HOSTACCOUNT, ACCOUNT, FINTYPE from ACCOUNTMAPPING";
	private static final String QUERY_STATE = "select distinct HOSTACCOUNT, ACCOUNT, FINTYPE, BRANCHCOUNTRY, BRANCHPROVINCE from ACCOUNTMAPPING, RMTBRANCHES";

	public enum Dimension {
		CONSOLIDATE, STATE,
	}

	private Map<String, String> parameters = new HashMap<>();

	public TrailBalanceEngine(DataSource dataSource, long userId, Date valueDate, Date appDate, Date fromDate,
			Date toDate) {
		super(dataSource, userId, App.DATABASE.name(), true, appDate, EXTRACT_STATUS);
		this.appDate = appDate;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	/**
	 * Validate the Account Mapping and Posting data
	 * 
	 * @throws Exception
	 */
	public void doHealthCheck() throws Exception {
		logger.debug(Literal.ENTERING);
		//validateAccountHistory();
		validateAccountMapping();
		validatePostings();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * validate Account Mapping
	 * 
	 * @throws Exception
	 */
	private void validateAccountMapping() throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("START_DATE", fromDate);
		paramMap.addValue("END_DATE", toDate);
		paramMap.addValue("ENTITYCODE", entityCode);
		String sql = "Select count (*) from POSTINGS where POSTDATE <= :END_DATE and POSTAMOUNT <>0  AND ENTITYCODE = :ENTITYCODE and account not in(select account from AccountMapping) ";
		logger.trace(Literal.SQL + sql.toString());
		if (parameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class) > 0) {
			EXTRACT_STATUS.setStatus("F");
			EXTRACT_STATUS.setRemarks(
					"Account mapping is not configured, please check the Account Mapping report and configure the missing accounts.");
			throw new AppException(EXTRACT_STATUS.getRemarks());
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * validate Account History
	 * 
	 * @throws Exception
	 */
	private void validateAccountHistory() throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("START_DATE", fromDate);
		paramMap.addValue("END_DATE", toDate);
		String sql = "Select count (*) from AccountsHistory where POSTDATE BETWEEN :START_DATE AND :END_DATE and TODAYCREDITS<>0 and TODAYDEBITS<>0 and accountid not in(select account from AccountMapping) ";
		logger.trace(Literal.SQL + sql.toString());
		if (parameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class) > 0) {
			EXTRACT_STATUS.setStatus("F");
			EXTRACT_STATUS.setRemarks(
					"Account mapping is not configured, please check the Account Mapping report and configure the missing accounts.");
			throw new AppException(EXTRACT_STATUS.getRemarks());
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * validate Postings Data
	 * 
	 * @throws Exception
	 */
	private void validatePostings() throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("START_DATE", fromDate);
		paramMap.addValue("END_DATE", toDate);
		paramMap.addValue("ENTITYCODE", entityCode);

		String sql = "select SUM(CASE WHEN DRORCR = 'D' Then POSTAMOUNT * -1 Else PostAmount END) from POSTINGS where POSTDATE BETWEEN :START_DATE AND :END_DATE AND ENTITYCODE = :ENTITYCODE";
		logger.trace(Literal.SQL + sql.toString());
		BigDecimal amount = parameterJdbcTemplate.queryForObject(sql, paramMap, BigDecimal.class);

		if (amount != null && BigDecimal.ZERO.compareTo(amount) != 0) {
			EXTRACT_STATUS.setStatus("F");
			EXTRACT_STATUS.setRemarks("Credit and Debit amounts not matched for the transactions between "
					.concat(DateUtil.format(fromDate, DateFormat.LONG_DATE)).concat(" and ")
					.concat(DateUtil.format(toDate, DateFormat.LONG_DATE)));
			throw new AppException(EXTRACT_STATUS.getRemarks());
		}
		logger.debug(Literal.LEAVING);
	}

	public void extractReport(Dimension dimention, String[] entityDetails, String stateCode) throws Exception {
		this.dimension = dimention;
		this.entityCode = entityDetails[0];
		this.entityDescription = entityDetails[1];
		this.stateCode = stateCode;
		if (dimension == Dimension.STATE) {
			stateWiseReport = true;
			stateCodeList = Arrays.asList(this.stateCode.split(","));
		}
		extractReport();
	}

	/**
	 * TrailBalance Data Moved to the data engine
	 * 
	 * @throws Exception
	 */
	private void extractReport() throws Exception {
		logger.debug(Literal.ENTERING);
		// Validate the Account Mapping And Posting data
		doHealthCheck();

		extract();

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("START_DATE", DateUtil.format(fromDate, "ddMMyyyy"));
		parameterMap.put("END_DATE", DateUtil.format(toDate, "ddMMyyyy"));
		parameterMap.put("HEADER_ID", seqNo);
		parameterMap.put("DIMENSION", dimension.name());
		parameterMap.put("COMPANY_NAME", entityDescription);

		if (dimension == Dimension.STATE) {
			parameterMap.put("REPORT_NAME", "State Wise Trial Balance - Ledger A/C wise");
			parameterMap.put("ENTITY_CODE", entityCode + "_TRAIL_BALANCE_");

		} else {
			parameterMap.put("REPORT_NAME", "Consolidated Trial Balance - Ledger A/C wise");
			parameterMap.put("ENTITY_CODE", entityCode + "_TRIAL_BALANCE_CONSOLIDATE_");

		}

		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("HEADER_ID", seqNo);
		filterMap.put("DIMENSION", dimension.name());
		filterMap.put("ID", headerId);

		StringBuilder builder = new StringBuilder();
		builder.append("From ");
		builder.append(DateUtil.format(fromDate, "dd-MMM-yy").toUpperCase());
		builder.append(" To ");
		builder.append(DateUtil.format(toDate, "dd-MMM-yy").toUpperCase());

		parameterMap.put("TRANSACTION_DURATION", builder.toString());
		parameterMap.put("CURRENCY",
				parameters.get("APP_DFT_CURR").concat(" - ".concat(parameters.get("APP_DFT_CURR"))));

		setFilterMap(filterMap);

		setParameterMap(parameterMap);
		if (dimension == Dimension.STATE) {
			exportData("TRIAL_BALANCE_EXPORT_STATE");
		} else {
			exportData("TRIAL_BALANCE_EXPORT_CONSOLIDATE");
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the Debit,Credit, Opening balalce and Closing bal to TrailBalance
	 * 
	 * @throws Exception
	 */
	private void extract() throws Exception {
		logger.debug(Literal.ENTERING);
		logger.info("Extracting data...");
		//Load system parameters and clear the table data
		initilize();
		createHeader();
		insertRecords();
		setCreditAndDebitAmt();
		setOpeningBalance();
		setProfiTLossBalance();
		BigDecimal profitLossAmt = calculateProfitLoss();
		deleleExtraData();
		updateClosingBalance();
		logTrialBalace(profitLossAmt);
		resetWorkingTable();
		// logTrialBalace();
		logger.debug(Literal.LEAVING);
	}

	private void getStates() {
		logger.debug(Literal.ENTERING);

		if (dimension == Dimension.STATE && StringUtils.isNotBlank(stateCode)) {
			String[] arr = stateCode.split(",");
			stateCode = String.join("','", arr);
			// stateCode = StringUtils.join(arr, "','");
			this.stateCode = "'".concat(stateCode).concat("'");
		}
		logger.debug(Literal.LEAVING);
	}

	private void insertRecords() throws Exception {

		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = null;
		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO TRIAL_BALANCE_REPORT_WORK (HeaderID, AcTypeGrpID, FinType, Account,");
		sql.append(" HostAccount, Description, GroupCode, OpeningBal, OpeningBalType, DebitAmount, ");
		sql.append(" CreditAmount, ClosingBal, ClosingBalType, PLACBALANCE");
		if (stateWiseReport) {
			sql.append(", Country, Province)");
			sql.append(" Select Distinct (:HeaderID) HeaderID, T2.AcTypeGrpID, FinType, Account,");
			sql.append(" HostAccount, T2.AcTYpeDesc Description,  T3.GroupCode, 0 OpeningBal, 'Dr' OpeningBalType,");
			sql.append(" 0 DebitAmount, 0 CreditAmount, 0 ClosingBal, 'Dr' ClosingBalType, 0 PLACBalance,");
			sql.append(" BranchCountry Country, BranchProvince Province From AccountMapping  T1 ");
			sql.append(" Inner Join RMTAccountTypes T2 on T1.AccountTYpe = T2.AcType ");
			sql.append(" Inner Join AccountTypeGroup T3  ON T3.GroupID = T2.AcTypeGrpID, RMTBranches ");
			if (StringUtils.isNotEmpty(stateCode)) {
				sql.append("Where BranchProvince IN (:BranchProvince)");
			}
		} else {
			sql.append(")");
			sql.append(" Select  Distinct  ( :HeaderID )  HeaderID,  T2.ACTYPEGRPID,  FinType,  Account, ");
			sql.append(" HostAccount,  T2.AcTYpeDesc Description,  T3.GroupCode,  0 OpeningBal, 'Dr' OpeningBalType,");
			sql.append(" 0 DebitAmount, 0 CreditAmount, 0 ClosingBal , 'Dr' ClosingBalType, 0 PLACBalance");
			sql.append(" From AccountMapping  T1 Inner Join RMTAccountTypes T2 on T1.AccountTYpe = T2.AcType ");
			sql.append(" Inner Join AccountTypeGroup T3  ON T3.GroupID = T2.AcTypeGrpID ");
		}
		paramMap = new MapSqlParameterSource();
		paramMap.addValue("HeaderID", headerId);
		paramMap.addValue("BranchProvince", stateCodeList);

		try {
			logger.trace(Literal.SQL + sql.toString());
			int count = parameterJdbcTemplate.update(sql.toString(), paramMap);
			logger.debug("Count: " + count);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception(e);
		}
		logger.debug(Literal.LEAVING);

	}

	private void setCreditAndDebitAmt() throws Exception {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("FromDate", fromDate);
		paramMap.addValue("ToDate", toDate);
		paramMap.addValue("EntityCode", entityCode);
		StringBuilder sql = new StringBuilder();

		sql.append(
				"Update TRIAL_BALANCE_REPORT_WORK set DebitAmount = T2.DebitAmount, CreditAmount = T2.CreditAmount from");

		if (stateWiseReport) {
			sql.append(
					" (Select Distinct T1.Account, SUM(Case When DrOrCr = 'D' Then PostAmount Else 0 End) DebitAmount,");
			sql.append(" SUM(Case When DrOrCr = 'C' Then PostAmount * -1 Else 0 End)  CreditAmount, T2.BranchProvince");
			sql.append(" From Postings T1 Inner Join RMTBranches T2 on T1.POSTBRANCH = T2.BRANCHCODE");
			sql.append(" Where T1.ENTITYCODE = :EntityCode and T1.PostDate >= :FromDate and T1.PostDate <= :ToDate ");
			sql.append(
					" Group By Account, BranchProvince )T2 where TRIAL_BALANCE_REPORT_WORK.Account = T2.Account And TRIAL_BALANCE_REPORT_WORK.Province = T2.BranchProvince");
		} else {
			sql.append(
					" (Select Distinct T1.Account, SUM(Case When DrOrCr = 'D' Then PostAmount Else 0 End) DebitAmount,");
			sql.append(" SUM(Case When DrOrCr = 'C' Then PostAmount * -1 Else 0 End)  CreditAmount ");
			sql.append(" From Postings T1 Where T1.ENTITYCODE = :EntityCode and T1.PostDate >= :FromDate ");
			sql.append(
					" and T1.PostDate <= :ToDate Group By Account)T2 where TRIAL_BALANCE_REPORT_WORK.Account = T2.Account  ");
		}

		try {
			logger.trace(Literal.SQL + sql.toString());
			int count = parameterJdbcTemplate.update(sql.toString(), paramMap);
			logger.debug("count " + count);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setOpeningBalance() throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("FromDate", fromDate);
		paramMap.addValue("EntityCode", entityCode);
		StringBuilder sql = new StringBuilder();

		sql.append(
				" Update TRIAL_BALANCE_REPORT_WORK set OpeningBal = T3.OpeningBalance, OpeningBalType = T3.OpeningBalType from ");
		if (stateWiseReport) {
			sql.append(" (Select Distinct T1.AccountID Account, T1.BranchProvince StateCode,");
			sql.append(" Sum(T1.ACBALANCE)*-1  OpeningBalance,");
			sql.append(" (Case When Sum(T1.ACBALANCE)*-1 > 0 Then 'Dr' else 'Cr' End) OpeningBalType");
			sql.append(" From AccountHistoryDetails T1 Inner Join( Select Distinct T2.AccountID, ");
			sql.append(" T2.BranchProvince, T2.PostBranch, T2.EntityCode, Max(T2.PostDate) PostDate");
			sql.append(" From AccountHistoryDetails T2 Where T2.PostDate < :FromDate And EntityCode = :EntityCode");
			sql.append(" Group By T2.AccountID, T2.BranchProvince, T2.PostBranch, T2.EntityCode) T2 ");
			sql.append(" ON T1.AccountID = T2.AccountID and T1.PostDate = T2.PostDate And ");
			sql.append(" T2.PostBranch = T1.PostBranch And T2.BranchProvince = T1.BranchProvince");
			sql.append(" And T2.EntityCode = T1.EntityCode Group By T1.AccountID,T1.BranchProvince)T3 ");
			sql.append(
					" where TRIAL_BALANCE_REPORT_WORK.Account = T3.Account And TRIAL_BALANCE_REPORT_WORK.Province = T3.StateCode ");

		} else {
			sql.append(" (Select Distinct T1.AccountID Account, Sum(T1.ACBALANCE)*-1  OpeningBalance,");
			sql.append(" (Case When Sum(T1.ACBALANCE)*-1 > 0 Then 'Dr' else 'Cr' End) OpeningBalType");
			sql.append(" From AccountHistoryDetails T1 Inner Join( Select Distinct T2.AccountID,");
			sql.append(" T2.BranchProvince, T2.PostBranch, T2.EntityCode, Max(T2.PostDate) PostDate");
			sql.append(" From AccountHistoryDetails T2 Where T2.PostDate < :FromDate And EntityCode = :EntityCode");
			sql.append(" Group By T2.AccountID, T2.BranchProvince, T2.PostBranch, T2.EntityCode) T2 ");
			sql.append(" ON T1.AccountID = T2.AccountID and T1.PostDate = T2.PostDate And ");
			sql.append(" T2.BranchProvince = T1.BranchProvince And T2.POSTBRANCH = T1.POSTBRANCH And ");
			sql.append(
					" T2.ENTITYCODE = T1.ENTITYCODE group by T1.ACCOUNTID)T3 where TRIAL_BALANCE_REPORT_WORK.Account = T3.Account");
		}

		try {
			logger.trace(Literal.SQL + sql.toString());
			int count = parameterJdbcTemplate.update(sql.toString(), paramMap);
			logger.debug("count " + count);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setProfiTLossBalance() throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("FinanceEndDate", getFinanceEndDate());
		paramMap.addValue("EntityCode", entityCode);
		StringBuilder sql = new StringBuilder();

		sql.append(
				" Update TRIAL_BALANCE_REPORT_WORK set PLACBalance =  T3.openingBal, OpeningBal = TRIAL_BALANCE_REPORT_WORK.OpeningBal - T3.openingBal from ");
		if (stateWiseReport) {
			sql.append(" (Select T1.AccountID, T1.BranchProvince, Sum(T1.ACBALANCE)*-1  OpeningBal ");
			sql.append(" From AccountHistoryDetails T1 Inner join (Select Distinct T2.AccountID,T2.BranchProvince,");
			sql.append(" T2.PostBranch, T2.EntityCode, Max(T2.PostDate) PostDate From AccountHistoryDetails T2");
			sql.append(" Where T2.postdate <= :FinanceEndDate And EntityCode = :EntityCode ");
			sql.append(" Group By T2.ACCOUNTID,T2.BRANCHPROVINCE,T2.POSTBRANCH,T2.ENTITYCODE) T2 ");
			sql.append(
					" ON T1.AccountID = T2.AccountID And T1.PostDate = T2.PostDate And T1.PostBranch = T2.PostBranch");
			sql.append(" And T1.BranchProvince = T2.BranchProvince And T2.EntityCode = T1.EntityCode ");
			sql.append(
					" Group By T1.AccountID,T1.BranchProvince) T3 where  TRIAL_BALANCE_REPORT_WORK.Account = T3.AccountID  ");
			sql.append(
					" And TRIAL_BALANCE_REPORT_WORK.Province = T3.BranchProvince And TRIAL_BALANCE_REPORT_WORK.GroupCode IN ('INCOME','EXPENSE') ");

		} else {
			sql.append(" (Select T1.AccountID, Sum(T1.ACBALANCE)*-1  OpeningBal ");
			sql.append(" From AccountHistoryDetails T1 Inner join (Select Distinct T2.AccountID,T2.BranchProvince,");
			sql.append(" T2.PostBranch, T2.EntityCode, Max(T2.PostDate) PostDate From AccountHistoryDetails T2");
			sql.append(" Where T2.postdate <= :FinanceEndDate And EntityCode = :EntityCode ");
			sql.append(" Group By T2.AccountID,T2.BranchProvince,T2.PostBranch,T2.EntityCode) T2 ");
			sql.append(
					" ON T1.AccountID = T2.AccountID And T1.PostDate = T2.PostDate And T1.PostBranch = T2.PostBranch");
			sql.append(" And T1.BranchProvince = T2.BranchProvince And T2.EntityCode = T1.EntityCode ");
			sql.append(
					" Group By T1.AccountID) T3  where TRIAL_BALANCE_REPORT_WORK.Account = T3.AccountID  And TRIAL_BALANCE_REPORT_WORK.GroupCode IN ('INCOME','EXPENSE')");
		}

		try {
			logger.trace(Literal.SQL + sql.toString());
			int count = parameterJdbcTemplate.update(sql.toString(), paramMap);
			logger.debug("count " + count);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private BigDecimal calculateProfitLoss() throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource parmsource = new MapSqlParameterSource();

		sql.append("SELECT SUM(PLACBALANCE) AMT ");
		sql.append(" from TRIAL_BALANCE_REPORT_WORK where GROUPCODE in ('INCOME', 'EXPENSE') ");

		BigDecimal profitLossAmt = parameterJdbcTemplate.queryForObject(sql.toString(), parmsource, BigDecimal.class);
		if (profitLossAmt == null) {
			profitLossAmt = BigDecimal.ZERO;
		}
		logger.debug(Literal.LEAVING);
		return profitLossAmt;
	}

	private void deleleExtraData() throws Exception {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = null;
		StringBuilder sql = new StringBuilder();

		sql.append(" Delete From TRIAL_BALANCE_REPORT_Work");
		sql.append(" Where (DebitAmount = 0 And CreditAmount = 0 And OpeningBal = 0) And HeaderID =:HeaderID");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("HeaderID", headerId);

		try {
			logger.trace(Literal.SQL + sql.toString());
			parameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void updateClosingBalance() throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(
				" Update TRIAL_BALANCE_REPORT_WORK set ClosingBal = T2.ClosingBal, OpeningBalType = T2.OpeningBalType , ClosingBalType = T2.ClosingBalType from ");
		if (stateWiseReport) {
			sql.append(" (Select Account, Province, (OpeningBal + CreditAmount + DebitAmount) ClosingBal,");
			sql.append(" (Case When OpeningBal > 0 Then 'Dr' else 'Cr' End) OpeningBalType, ");
			sql.append(
					" (Case When (OpeningBal + CreditAmount + DebitAmount) > 0 Then 'Dr' else 'Cr' End) ClosingBalType ");
			sql.append(
					" From TRIAL_BALANCE_REPORT_WORK) T2 where TRIAL_BALANCE_REPORT_WORK.Account = T2.Account And  TRIAL_BALANCE_REPORT_WORK.Province = T2.Province");

		} else {
			sql.append(" (Select Account, (OpeningBal + CreditAmount + DebitAmount) ClosingBal,");
			sql.append(" (Case When OpeningBal > 0 Then 'Dr' else 'Cr' End) OpeningBalType, ");
			sql.append(
					" (Case When (OpeningBal + CreditAmount + DebitAmount) > 0 Then 'Dr' else 'Cr' End) ClosingBalType ");
			sql.append(" From TRIAL_BALANCE_REPORT_WORK) T2 where TRIAL_BALANCE_REPORT_WORK.Account = T2.Account");
		}
		try {
			logger.trace(Literal.SQL + sql.toString());
			int count = parameterJdbcTemplate.update(sql.toString(), paramMap);
			logger.debug(count);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void logInFileTable() throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap = new MapSqlParameterSource();
		paramMap.addValue("HeaderID", headerId);

		StringBuilder data = new StringBuilder();
		data.append(
				" INSERT INTO TRIAL_BALANCE_REPORT_FILE(Account,AcTypeGrpID, Country, Province, HostAccount, Description,");
		data.append(" OpeningBal, OpeningBalType, DebitAmount, CreditAmount, ClosingBal, ClosingBalType, FinType)");
		data.append(" Select Distinct Account, AcTypeGrpId, Country, Province, HostAccount, Description,");
		data.append(
				" ABS(OpeningBal), OpeningBalType, ABS(DebitAmount), ABS(CreditAmount), ABS(ClosingBal), ClosingBalType, FinType");
		data.append(" From TRIAL_BALANCE_REPORT_VIEW Where HeaderID =:HeaderID");
		if (stateWiseReport) {
			data.append(" Order By Province");
		} else {
			data.append(" Order By AcTypeGrpId");
		}
		try {
			logger.trace(Literal.SQL + data.toString());
			int count = parameterJdbcTemplate.update(data.toString(), paramMap);
			logger.debug(count);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void updateOpeningBalance() throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("headerId", headerId);
		StringBuilder sql = new StringBuilder();
		sql.append(
				" MERGE INTO TRIAL_BALANCE_REPORT_WORK T1 USING (Select ACCOUNT,PROVINCE, (OPENINGBAL - PLACBALANCE) OPENINGBAL");
		sql.append(" from TRIAL_BALANCE_REPORT_WORK ) T2 ");
		sql.append(" ON (T1.ACCOUNT = T2.ACCOUNT And  T1.PROVINCE = T2.PROVINCE)");
		sql.append(" WHEN MATCHED THEN UPDATE SET T1.OPENINGBAL = T2.OPENINGBAL");
		try {
			logger.trace(Literal.SQL + sql.toString());
			int count = parameterJdbcTemplate.update(sql.toString(), paramMap);
			logger.debug(count);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setCreditOrDebitType() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = null;
		StringBuilder sql = new StringBuilder();

		sql.append(" MERGE INTO TRIAL_BALANCE_REPORT_WORK T1 USING (");
		sql.append(" Select Account, Province, ClosingBal, OpeningBal,OpeningBalType,");
		sql.append(" (CASE WHEN ClosingBalType = 'Dr' THEN 'Cr' ELSE 'Dr' END) ClosingBalType");
		sql.append(" from TRIAL_BALANCE_REPORT_WORK) T2 ON (T1.Account = T2.Acoount And T1.Province = T2.Province)");
		sql.append(
				" WHEN MATCHED THEN UPDATE SET T1.CLOSINGBAL = (0-T2.CLOSINGBAL), T1.OpeningBal = (0 - OpeningBal),");
		sql.append("T1.CLOSINGBALTYPE = T2.CLOSINGBALTYPE");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("headerId", headerId);

		try {
			logger.trace(Literal.SQL + sql.toString());
			int count = parameterJdbcTemplate.update(sql.toString(), paramMap);
			logger.debug("Count: " + count);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Based on given from date, return financial end date
	 * 
	 * @return finance last year date
	 */
	private Date getFinanceEndDate() {

		int year = 0;
		if (DateUtility.getMonth(fromDate) < 4) {
			year = DateUtility.getYear(DateUtility.getPreviousYearDate(fromDate));
		} else {
			year = DateUtility.getYear(fromDate);
		}

		String month = SysParamUtil.getValueAsString("FINANCIAL_YEAR_END_MONTH");
		String date = "31";// default value;

		String financeStartDate = date + "/" + month + "/" + Integer.toString(year);
		Date financeEndDate = null;
		try {
			financeEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(financeStartDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return financeEndDate;
	}

	private String getKey(TrailBalance openingBal) {
		if (dimension == Dimension.STATE) {
			return openingBal.getAccount().concat("-").concat(openingBal.getStateCode());
		} else {
			return openingBal.getAccount();
		}
	}

	private String getSignOfBalanceType(BigDecimal balance) {

		if (balance.compareTo(BigDecimal.ZERO) > 0) {
			if (ImplementationConstants.NEGATE_SIGN_TB) {
				return "Cr";
			} else {
				return "Dr";
			}
		} else {
			if (ImplementationConstants.NEGATE_SIGN_TB) {
				return "Dr";
			} else {
				return "Cr";
			}
		}
	}

	/**
	 * Generate the HeaderId and SeqNo for TRIAL_BALANCE_HEADER table
	 */
	private void createNextId() {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("DIMENSION", dimension.name());

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT COALESCE(MAX(SEQNO), 0) + 1");
		sql.append(" FROM TRIAL_BALANCE_HEADER WHERE DIMENSION = :DIMENSION");
		logger.trace(Literal.SQL + sql.toString());
		this.seqNo = this.parameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Long.class);

		sql = new StringBuilder();
		sql.append(" SELECT COALESCE(MAX(ID), 0) + 1");
		sql.append(" FROM TRIAL_BALANCE_HEADER");
		logger.trace(Literal.SQL + sql.toString());
		this.headerId = this.jdbcTemplate.queryForObject(sql.toString(), Long.class);
	}

	/**
	 * Insert TrailBal Data into TRIAL_BALANCE_HEADER Table
	 * 
	 * @return
	 * @throws Exception
	 */
	private long createHeader() throws Exception {
		logger.debug(Literal.ENTERING);
		logger.info("Creating the trail Balance Header..");
		createNextId();

		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TRIAL_BALANCE_HEADER(");
		sql.append(
				" ID, SEQNO, DIMENSION, COMPANYNAME, REPORTNAME, STARTDATE, ENDDATE, CURRENCY, ENTITYCODE) VALUES (");
		sql.append(
				" :ID, :SEQNO, :DIMENSION, :COMPANYNAME, :REPORTNAME, :STARTDATE, :ENDDATE, :CURRENCY, :ENTITYCODE)");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("ID", headerId);
		paramMap.addValue("SEQNO", seqNo);
		paramMap.addValue("DIMENSION", dimension.name());
		paramMap.addValue("COMPANYNAME", parameters.get("TRAIL_BALANCE_COMPANY_NAME"));
		paramMap.addValue("REPORTNAME", "Trial Balance Report");
		paramMap.addValue("STARTDATE", fromDate);
		paramMap.addValue("ENDDATE", toDate);
		paramMap.addValue("CURRENCY", parameters.get("APP_DFT_CURR"));
		paramMap.addValue("ENTITYCODE", this.entityCode);

		try {
			logger.trace(Literal.SQL + sql.toString());
			parameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to insert trail balance header.");
		}
		logger.debug(Literal.LEAVING + "SEQNO : " + seqNo);
		return seqNo;
	}

	/**
	 * Accounting Details mapped to the Trailbalalnce
	 * 
	 * @return
	 * @throws Exception
	 */
	private Map<String, TrailBalance> getLedgerAccounts() throws Exception {
		logger.debug(Literal.ENTERING);
		createHeader();

		String query = null;
		if (dimension == Dimension.STATE) {
			query = QUERY_STATE;
		} else {
			query = QUERY_CONSOLIDATE;
		}
		logger.trace(Literal.SQL + query.toString());
		logger.debug(Literal.LEAVING);
		return jdbcTemplate.query(query, new ResultSetExtractor<Map<String, TrailBalance>>() {
			@Override
			public Map<String, TrailBalance> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, TrailBalance> map = new HashMap<>();

				while (rs.next()) {
					TrailBalance item = new TrailBalance();
					item.setHeaderId(headerId);
					item.setDimention(dimension.name());
					item.setLedgerAccount(rs.getString("HOSTACCOUNT"));
					item.setAccount(rs.getString("ACCOUNT"));
					item.setFinType(rs.getString("FINTYPE"));
					item.setOpeningBalance(BigDecimal.ZERO);
					item.setOpeningBalanceType("Cr");
					item.setDebitAmount(BigDecimal.ZERO);
					item.setCreditAmount(BigDecimal.ZERO);

					if (dimension == Dimension.STATE) {
						item.setCountryCode(rs.getString("BRANCHCOUNTRY"));
						item.setStateCode(rs.getString("BRANCHPROVINCE"));
						map.put(getKey(item), item);
					} else {
						map.put(item.getAccount(), item);
					}
				}
				return map;
			}
		});
	}

	private void initilize() throws Exception {
		loadParameters();
		clearTables();
	}

	private void clearTables() {
		logger.info("Clearing staging tables..");
		logger.debug(Literal.ENTERING);
		jdbcTemplate.execute("DELETE FROM TRIAL_BALANCE_REPORT_FILE");

		if (App.DATABASE == App.Database.ORACLE) {
			jdbcTemplate
					.execute("alter table TRIAL_BALANCE_REPORT_FILE modify ID generated as identity (start with 1)");
		} else if (App.DATABASE == App.Database.SQL_SERVER) {
			jdbcTemplate.execute("dbcc checkident ('TRIAL_BALANCE_REPORT_FILE', reseed, 0)");
		} else if (App.DATABASE == App.Database.POSTGRES) {
			jdbcTemplate.execute("ALTER SEQUENCE seq_transaction_detail_report RESTART WITH 1");
		}

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("START_DATE", fromDate);
		paramMap.addValue("END_DATE", toDate);
		paramMap.addValue("DIMENSION", dimension.name());
		paramMap.addValue("ENTITYCODE", this.entityCode);

		if (dimension == Dimension.STATE) {
			paramMap.addValue("NAME", "TRIAL_BALANCE_EXPORT_STATE");
		} else {
			paramMap.addValue("NAME", "TRIAL_BALANCE_EXPORT_CONSOLIDATE");
		}

		// delete the data between start and end date if already available
		StringBuilder sql = new StringBuilder();

		sql = new StringBuilder();
		sql = sql.append("Delete from DATA_ENGINE_LOG where ID IN (");
		sql.append("SELECT ID FROM DATA_ENGINE_STATUS where filename in ");
		sql.append(" (SELECT filename FROM TRIAL_BALANCE_HEADER");
		sql.append(" WHERE STARTDATE = :START_DATE AND ENDDATE = :END_DATE AND DIMENSION = :DIMENSION ");
		sql.append(" AND ENTITYCODE = :ENTITYCODE)");
		sql.append(" AND NAME = :NAME)");
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.update(sql.toString(), paramMap);

		sql = new StringBuilder();
		sql = sql.append("Delete from DATA_ENGINE_STATUS");
		sql.append(" where filename in ");
		sql.append(" (SELECT filename FROM TRIAL_BALANCE_HEADER");
		sql.append(" WHERE STARTDATE = :START_DATE AND ENDDATE = :END_DATE AND DIMENSION = :DIMENSION ");
		sql.append(" AND ENTITYCODE = :ENTITYCODE)");
		sql.append(" AND NAME = :NAME");
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.update(sql.toString(), paramMap);

		sql = new StringBuilder();
		sql = sql.append("DELETE FROM TRIAL_BALANCE_HEADER WHERE STARTDATE = :START_DATE AND ENDDATE = :END_DATE");
		sql.append(" AND DIMENSION = :DIMENSION AND ENTITYCODE = :ENTITYCODE");
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Load PreviousFinancial Account Code And Description from SMTParameters
	 * 
	 */
	private void loadParameters() {
		logger.info("Loading parameters..");
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT SYSPARMCODE, SYSPARMVALUE, SYSPARMDESCRIPTION FROM SMTPARAMETERS where SYSPARMCODE");
		sql.append(" IN (:TRAIL_BALANCE_COMPANY_NAME, :APP_DFT_CURR, :PRFT_LOSS_GLCODE)");

		paramMap.addValue("TRAIL_BALANCE_COMPANY_NAME", "TRAIL_BALANCE_COMPANY_NAME");
		paramMap.addValue("APP_DFT_CURR", "APP_DFT_CURR");
		paramMap.addValue("PRFT_LOSS_GLCODE", "PRFT_LOSS_GLCODE");
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				if (StringUtils.equals(rs.getString("SYSPARMCODE"), "PRFT_LOSS_GLCODE")) {
					parameters.put(rs.getString("SYSPARMCODE"), rs.getString("SYSPARMVALUE"));
					parameters.put(rs.getString("SYSPARMVALUE"), rs.getString("SYSPARMDESCRIPTION"));
				} else {
					parameters.put(rs.getString("SYSPARMCODE"), rs.getString("SYSPARMVALUE"));
				}
			}
		});
		logger.debug(Literal.LEAVING);
	}

	private Map<String, TrailBalance> getAccountDetails() {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append(" select AM.HOSTACCOUNT, AM.ACCOUNT, ATG.GROUPCODE, AT.ACTYPEDESC");
		sql.append(" from ACCOUNTMAPPING AM");
		sql.append(" INNER JOIN RMTACCOUNTTYPES AT ON AT.ACTYPE = AM.ACCOUNTTYPE");
		sql.append(" INNER JOIN ACCOUNTTYPEGROUP ATG  ON ATG.GROUPID = AT.ACTYPEGRPID");

		try {
			logger.trace(Literal.SQL + sql.toString());
			return extractAccountDetails(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private Map<String, TrailBalance> extractAccountDetails(StringBuilder sql) {
		return parameterJdbcTemplate.query(sql.toString(), new MapSqlParameterSource(),
				new ResultSetExtractor<Map<String, TrailBalance>>() {
					@Override
					public Map<String, TrailBalance> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						Map<String, TrailBalance> map = new HashMap<>();

						while (rs.next()) {
							TrailBalance trailBalance = new TrailBalance();
							trailBalance.setLedgerAccount(rs.getString("HOSTACCOUNT"));
							trailBalance.setAccount(rs.getString("ACCOUNT"));
							trailBalance.setAccountType(rs.getString("GROUPCODE"));
							trailBalance.setAccountTypeDes(rs.getString("ACTYPEDESC"));

							map.put(trailBalance.getAccount(), trailBalance);
						}
						return map;
					}
				});
	}

	/**
	 * get Opening balance from Previous Month Closing balance And closing balance is the sum of credits and debits
	 * 
	 * @return List<TrailBalance>
	 */
	private List<TrailBalance> getOpeningBalance() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct T1.account,T5.ACBALANCE openingBalance,RB.BRANCHPROVINCE stateCode");
		sql.append(" From postings T1 ");
		sql.append(" INNER JOIN RMTBRANCHES RB ON RB.BRANCHCODE = T1.POSTBRANCH ");
		sql.append(" INNER JOIN ( Select T1.ACCOUNTID,T1.ACBALANCE, T1.POSTDATE from ACCOUNTSHISTORY T1 ");
		sql.append(
				" INNER JOIN ( Select T2.accountid, max(T2.postdate)postdate  from ACCOUNTSHISTORY T2 where T2.postdate < :postdate group by T2.accountid) T2");
		sql.append(" ON T1.accountid = T2.accountid and T1.postdate = T2.postdate) T5 ON T5.accountid = T1.account ");
		sql.append(" where T1.EntityCode = :EntityCode and T1.postamount<>0  and  T1.POSTDATE < :postdate ");

		if (dimension == Dimension.STATE && StringUtils.isNotBlank(stateCode)) {
			String[] arr = stateCode.split(",");
			String listofState = StringUtils.join(arr, "','");
			sql.append(" and RB.BRANCHPROVINCE in ('" + listofState + "')");
		}

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("postdate", fromDate);
		paramMap.addValue("EntityCode", entityCode);

		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		logger.trace(Literal.SQL + sql.toString());
		try {
			logger.debug(Literal.LEAVING);
			return parameterJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<TrailBalance>();
	}

	/**
	 * get Opening balance from Previous Month Closing balance And closing balance is the sum of credits and debits
	 * 
	 * @param toDate2
	 * 
	 * @return List<TrailBalance>
	 */
	private List<TrailBalance> getOpeningBalanceByDate(Date fromDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT distinct T1.account,T5.ACBALANCE openingBalance,RB.BRANCHPROVINCE stateCode, ");
		sql.append(" ATG.GROUPCODE accountType,AT.ACTYPEDESC accountTypeDes From postings T1 ");
		sql.append(" INNER JOIN RMTBRANCHES RB ON RB.BRANCHCODE = T1.POSTBRANCH ");
		sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.ACCOUNT=T1.ACCOUNT ");
		sql.append(" INNER JOIN RMTACCOUNTTYPES AT ON AT.ACTYPE = AM.ACCOUNTTYPE ");
		sql.append(" INNER JOIN ACCOUNTTYPEGROUP ATG  ON ATG.GROUPID = AT.ACTYPEGRPID ");
		sql.append(" INNER JOIN ( Select T1.ACCOUNTID,T1.ACBALANCE, T1.POSTDATE from ACCOUNTSHISTORY T1 ");
		sql.append(" INNER JOIN ( Select T2.accountid, max(T2.postdate)postdate  from ACCOUNTSHISTORY T2 ");
		sql.append(" where T2.postdate <= :FROMDATE group by T2.accountid) T2 ");
		sql.append(" ON T1.accountid = T2.accountid and T1.postdate = T2.postdate) T5 ON T5.accountid = T1.account ");
		sql.append("  where atg.GROUPCODE in ('EXPENSE','INCOME') and T1.EntityCode = :EntityCode ");
		sql.append("  and T1.postamount<>0 and  T1.POSTDATE <= :FROMDATE");

		if (dimension == Dimension.STATE && StringUtils.isNotBlank(stateCode)) {
			String[] arr = stateCode.split(",");
			String listofState = StringUtils.join(arr, "','");
			sql.append(" and RB.BRANCHPROVINCE in ('" + listofState + "')");
		}

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("FROMDATE", new SimpleDateFormat("yyyy-MM-dd").format(fromDate));
		paramMap.addValue("EntityCode", entityCode);

		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			logger.trace(Literal.SQL + sql.toString());
			return parameterJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<TrailBalance>();
	}

	private List<TrailBalance> getDebitAmount() {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = null;

		StringBuilder sql = new StringBuilder();

		if (dimension == Dimension.STATE) {
			sql.append(
					"select AM.HOSTACCOUNT ledgerAccount, AM.ACCOUNT Account, RB.BRANCHPROVINCE stateCode, sum(postAmount) debitAmount");
			sql.append(" from POSTINGS P");
			sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
			sql.append(" INNER JOIN RMTBRANCHES RB ON RB.BRANCHCODE = P.POSTBRANCH");
			sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND ENTITYCODE = :ENTITYCODE");
			sql.append(" and P.DRORCR = :DRORCR");

			if (dimension == Dimension.STATE && StringUtils.isNotBlank(stateCode)) {
				String[] arr = stateCode.split(",");
				String listofState = StringUtils.join(arr, "','");
				sql.append(" and RB.BRANCHPROVINCE in ('" + listofState + "') ");
			}

			sql.append(" group by AM.HOSTACCOUNT, AM.ACCOUNT, RB.BRANCHPROVINCE");
		} else {
			sql.append(" select AM.HOSTACCOUNT ledgerAccount, AM.ACCOUNT Account, sum(postAmount) debitAmount");
			sql.append(" from POSTINGS P");
			sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
			sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND ENTITYCODE = :ENTITYCODE");
			sql.append(" and P.DRORCR = :DRORCR");
			sql.append(" group by AM.HOSTACCOUNT, AM.ACCOUNT");
		}

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MONTH_STARTDATE", fromDate);
		paramMap.addValue("MONTH_ENDDATE", toDate);
		paramMap.addValue("DRORCR", "D");
		paramMap.addValue("ENTITYCODE", entityCode);

		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			logger.trace(Literal.SQL + sql.toString());
			return parameterJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private List<TrailBalance> getCreditAmount() {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = null;

		StringBuilder sql = new StringBuilder();

		if (dimension == Dimension.STATE) {
			sql.append(
					"select AM.HOSTACCOUNT ledgerAccount, AM.ACCOUNT Account, RB.BRANCHPROVINCE stateCode, sum(postAmount) creditAmount");
			sql.append(" from POSTINGS P");
			sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
			sql.append(" INNER JOIN RMTBRANCHES RB ON RB.BRANCHCODE = P.POSTBRANCH");
			sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND ENTITYCODE = :ENTITYCODE");
			sql.append(" and P.DRORCR = :DRORCR");

			if (dimension == Dimension.STATE && StringUtils.isNotBlank(stateCode)) {
				String[] arr = stateCode.split(",");
				String listofState = StringUtils.join(arr, "','");
				sql.append(" and RB.BRANCHPROVINCE in ('" + listofState + "') ");
			}

			sql.append(" group by AM.HOSTACCOUNT, AM.ACCOUNT, RB.BRANCHPROVINCE");
		} else {
			sql.append("select AM.HOSTACCOUNT ledgerAccount,  AM.ACCOUNT Account, sum(postAmount) creditAmount");
			sql.append(" from POSTINGS P");
			sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
			sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND ENTITYCODE = :ENTITYCODE");
			sql.append(" and P.DRORCR = :DRORCR");
			sql.append(" group by AM.HOSTACCOUNT, AM.ACCOUNT");
		}

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MONTH_STARTDATE", fromDate);
		paramMap.addValue("MONTH_ENDDATE", toDate);
		paramMap.addValue("DRORCR", "C");
		paramMap.addValue("ENTITYCODE", entityCode);

		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			logger.trace(Literal.SQL + sql.toString());
			return parameterJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private void save(Map<String, TrailBalance> accounts) throws Exception {
		logger.debug(Literal.ENTERING);
		List<TrailBalance> list = new ArrayList<>();
		for (Entry<String, TrailBalance> entry : accounts.entrySet()) {
			list.add(entry.getValue());

			if (list.size() >= batchSize) {
				save(list);
				list.clear();
			}
		}

		if (!list.isEmpty()) {
			save(list);
		}

		list = null;
		logger.debug(Literal.LEAVING);
	}

	/**
	 * save the TrailBalance month end data to the TRIAL_BALANCE_REPORT
	 * 
	 * @param list
	 * @throws SQLException
	 */
	private void save(List<TrailBalance> list) throws SQLException {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();

		List<TrailBalance> tempList = new ArrayList<TrailBalance>();

		for (TrailBalance tb : list) {
			if ((BigDecimal.ZERO.compareTo(tb.getOpeningBalance()) == 0)
					&& (BigDecimal.ZERO.compareTo(tb.getClosingBalance()) == 0)
					&& (BigDecimal.ZERO.compareTo(tb.getCreditAmount()) == 0)
					&& (BigDecimal.ZERO.compareTo(tb.getDebitAmount()) == 0)) {

			} else if ((BigDecimal.ZERO.compareTo(tb.getOpeningBalance()) != 0
					&& BigDecimal.ZERO.compareTo(tb.getOpeningBalance()) != 0
					&& BigDecimal.ZERO.compareTo(tb.getCreditAmount()) != 0
					&& BigDecimal.ZERO.compareTo(tb.getDebitAmount()) != 0)) {
				tempList.add(tb);
			} else if ((BigDecimal.ZERO.compareTo(tb.getOpeningBalance()) != 0)
					|| (BigDecimal.ZERO.compareTo(tb.getOpeningBalance()) != 0)
					|| (BigDecimal.ZERO.compareTo(tb.getCreditAmount()) != 0)
					|| (BigDecimal.ZERO.compareTo(tb.getDebitAmount()) != 0)) {
				tempList.add(tb);
			}
		}

		sql.append("INSERT INTO TRIAL_BALANCE_REPORT VALUES(");
		sql.append(" :HeaderId,");
		sql.append(" :AccountType,");
		sql.append(" :CountryCode,");
		sql.append(" :StateCode,");
		sql.append(" :FinType,");
		sql.append(" :Account,");
		sql.append(" :LedgerAccount,");
		sql.append(" :AccountTypeDes,");
		sql.append(" :OpeningBalance,");
		sql.append(" :OpeningBalanceType,");
		sql.append(" :DebitAmount,");
		sql.append(" :CreditAmount,");
		sql.append(" :ClosingBalance,");
		sql.append(" :ClosingBalanceType)");

		parameterJdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(tempList.toArray()));
		logger.debug(Literal.LEAVING);
	}

	private void logTrialBalace(BigDecimal profitLossAmt) throws Exception {
		logger.debug(Literal.ENTERING);

		logInFileTable();
		addFinancialSummary(profitLossAmt);
		addSummaryRecord();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Second last row shall be the sum of balances of income (Account type group code) minus sum of balances of expense
	 * (Account type group code) ledgers for the previous financial year
	 */
	private void addFinancialSummary(BigDecimal profitLossAmt) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		;
		MapSqlParameterSource parmsource = new MapSqlParameterSource();

		String openingBalType = "";
		if (profitLossAmt.compareTo(BigDecimal.ZERO) > 0) {
			openingBalType = "Dr";
		} else {
			openingBalType = "Cr";
		}

		String accountCode = parameters.get("PRFT_LOSS_GLCODE");
		sql = new StringBuilder();
		sql.append(
				" INSERT INTO TRIAL_BALANCE_REPORT_FILE (ACCOUNT, DESCRIPTION, OPENINGBAL, CLOSINGBAL,OpeningBalType,ClosingBalType)");
		sql.append(
				" VALUES (:ACCOUNT, :DESCRIPTION, :ProfitLossAmt, :ProfitLossAmt, :OpeningBalType, :OpeningBalType)");

		parmsource.addValue("ProfitLossAmt",
				PennantApplicationUtil.formateAmount(profitLossAmt.abs(), PennantConstants.defaultCCYDecPos));
		parmsource.addValue("ACCOUNT", accountCode);
		parmsource.addValue("DESCRIPTION", parameters.get(accountCode));
		parmsource.addValue("OpeningBalType", openingBalType);
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.update(sql.toString(), parmsource);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Sum of Debits And Credits for present Month
	 */
	private void addSummaryRecord() {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO TRIAL_BALANCE_REPORT_FILE(DEBITAMOUNT, CREDITAMOUNT) values");
		sql.append(" ((select Sum(DebitAmount) from TRIAL_BALANCE_REPORT_FILE) ,");
		sql.append(" (select Sum(CreditAmount) from TRIAL_BALANCE_REPORT_FILE))");
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.update(sql.toString(), new MapSqlParameterSource());
		logger.debug(Literal.LEAVING);
	}

	private void resetWorkingTable() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = null;
		StringBuilder sql = new StringBuilder();

		sql.append(" Delete  From TRIAL_BALANCE_REPORT_WORK Where HeaderId = :HeaderID");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("HeaderID", headerId);

		try {
			logger.trace(Literal.SQL + sql.toString());
			parameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * get previous finance year postamount for group code expense and income
	 * 
	 * @param finanaceEndDate
	 * @return postamount till date
	 */
	public BigDecimal getPreviousFinancialYearBalances(Date finanaceEndDate) {
		logger.debug("Entering");
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("POSTDATE", finanaceEndDate);

		sql.append("select sum(case when d.drorcr='C' then d.postamount else d.postamount*-1 end)  OPENING ");
		sql.append(" from PLF.RMTACCOUNTTYPES A,PLF.ACCOUNTTYPEGROUP B,PLF.ACCOUNTMAPPING C,PLF.POSTINGS D ");

		sql.append(" WHERE A.ACTYPEGRPID = B.GROUPID AND C.ACCOUNTTYPE = A.ACTYPE AND C.ACCOUNT = D.ACCOUNT ");
		sql.append("AND B.GROUPCODE IN ('INCOME','EXPENSE') AND D.POSTDATE <= :POSTDATE");
		logger.trace(Literal.SQL + sql.toString());
		logger.debug("Leaving");
		return parameterJdbcTemplate.queryForObject(sql.toString(), paramMap, BigDecimal.class);

	}

	public boolean isBatchExists(String dimention, String entity) throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("STARTDATE", fromDate);
		paramMap.addValue("ENDDATE", toDate);
		paramMap.addValue("DIMENSION", dimention);
		paramMap.addValue("ENTITYCODE", entity);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(*) from ");
		sql.append(
				" TRIAL_BALANCE_HEADER WHERE DIMENSION = :DIMENSION AND STARTDATE = :STARTDATE AND ENDDATE = :ENDDATE AND ENTITYCODE = :ENTITYCODE");

		int count = 0;
		try {
			count = this.parameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

}