package com.pennanttech.pff.trialbalance;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.SysParamUtil;
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
	private static final Logger logger = LogManager.getLogger(TrailBalanceEngine.class);

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

	public enum Dimension {
		CONSOLIDATE, STATE,
	}

	private Map<String, String> parameters = new HashMap<>();

	public TrailBalanceEngine(DataSource dataSource, long userId, Date valueDate, Date appDate, Date fromDate,
			Date toDate) {
		super(dataSource, userId, App.DATABASE.name(), true, appDate, EXTRACT_STATUS);
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

		if (parameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class) <= 0) {
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
			parameterMap.put("ENTITY_CODE", entityCode + "_TRIAL_BALANCE_");

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
		// Load system parameters and clear the table data
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

		String sql = getCreditDebitQuery();

		try {
			logger.trace(Literal.SQL + sql);
			int count = parameterJdbcTemplate.update(sql, paramMap);
			logger.debug("count " + count);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private String getCreditDebitQuery() {
		if (App.DATABASE.equals(App.Database.ORACLE)) {
			return getCreditDebitQueryOracle();
		} else {
			return getCreditDebitQueryDefault();
		}
	}

	private String getCreditDebitQueryOracle() {
		StringBuilder sql = new StringBuilder();
		sql.append(" MERGE INTO TRIAL_BALANCE_REPORT_WORK T1 USING");
		sql.append("(Select Distinct");
		sql.append(" T1.Account");
		sql.append(", SUM(Case When DrOrCr = 'D' Then PostAmount Else 0 End) DebitAmount");
		sql.append(", SUM(Case When DrOrCr = 'C' Then PostAmount * -1 Else 0 End)  CreditAmount");

		if (stateWiseReport) {
			sql.append(", T2.BranchProvince");
			sql.append(" From Postings T1");
			sql.append(" Inner Join RMTBranches T2 on T1.POSTBRANCH = T2.BRANCHCODE");
			sql.append(" Where T1.ENTITYCODE = :EntityCode and T1.PostDate >= :FromDate and T1.PostDate <= :ToDate");
			sql.append(" Group By Account, BranchProvince ) T2");
			sql.append(" ON (T1.Account = T2.Account And T1.Province = T2.BranchProvince)");
		} else {
			sql.append(" From Postings T1");
			sql.append(" Where T1.ENTITYCODE = :EntityCode and T1.PostDate >= :FromDate");
			sql.append(" and T1.PostDate <= :ToDate Group By Account) T2 ON (T1.Account = T2.Account)");
		}
		sql.append(" WHEN MATCHED THEN UPDATE SET T1.DebitAmount = T2.DebitAmount, T1.CreditAmount = T2.CreditAmount ");

		return sql.toString();
	}

	private String getCreditDebitQueryDefault() {
		StringBuilder sql = new StringBuilder();
		sql.append("Update TRIAL_BALANCE_REPORT_WORK SET");
		sql.append(" DebitAmount = T2.DebitAmount");
		sql.append(", CreditAmount = T2.CreditAmount");
		sql.append(" from (select Distinct");
		sql.append(" T1.Account");
		sql.append(", SUM(Case When DrOrCr = 'D' Then PostAmount Else 0 End) DebitAmount");
		sql.append(", SUM(Case When DrOrCr = 'C' Then PostAmount * -1 Else 0 End)  CreditAmount");

		if (stateWiseReport) {
			sql.append(", T2.BranchProvince");
			sql.append(" From Postings T1");
			sql.append(" Inner Join RMTBranches T2 on T1.POSTBRANCH = T2.BRANCHCODE");
			sql.append(" Where T1.ENTITYCODE = :EntityCode and T1.PostDate >= :FromDate and T1.PostDate <= :ToDate");
			sql.append(" Group By Account, BranchProvince) T2");
			sql.append(" Where TRIAL_BALANCE_REPORT_WORK.Account = T2.Account");
			sql.append(" and TRIAL_BALANCE_REPORT_WORK.Province = T2.BranchProvince");
		} else {
			sql.append(" From Postings T1");
			sql.append(" Where T1.ENTITYCODE = :EntityCode and T1.PostDate >= :FromDate and T1.PostDate <= :ToDate");
			sql.append(" Group By Account) T2");
			sql.append(" where TRIAL_BALANCE_REPORT_WORK.Account = T2.Account");
		}

		return sql.toString();
	}

	private void setOpeningBalance() throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("FromDate", fromDate);
		paramMap.addValue("EntityCode", entityCode);
		paramMap.addValue("ToDate", toDate);

		StringBuilder sql = new StringBuilder();

		sql = getOpeningBalanceQuery();

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

	private StringBuilder getOpeningBalanceQuery() {
		if (App.DATABASE.equals(App.Database.ORACLE)) {
			return getOpeningBalanceQueryOracle();
		} else {
			return getOpeningBalanceQueryDefault();
		}
	}

	private StringBuilder getOpeningBalanceQueryOracle() {
		StringBuilder sql = new StringBuilder();
		sql.append(" MERGE INTO TRIAL_BALANCE_REPORT_WORK T1 USING ");

		if (stateWiseReport) {
			sql.append(
					" (Select Distinct T1.Account, SUM(Case When DrOrCr = 'D' Then PostAmount Else 0 End) DebitAmount,");
			sql.append(" SUM(Case When DrOrCr = 'C' Then PostAmount * -1 Else 0 End)  CreditAmount, T2.BranchProvince");
			sql.append(" From Postings T1 Inner Join RMTBranches T2 on T1.POSTBRANCH = T2.BRANCHCODE");
			sql.append(" Where T1.ENTITYCODE = :EntityCode and T1.PostDate >= :FromDate and T1.PostDate <= :ToDate ");
			sql.append(
					" Group By Account, BranchProvince )T2 ON (T1.Account = T2.Account And T1.Province = T2.BranchProvince) ");
		} else {
			sql.append(
					" (Select Distinct T1.Account, SUM(Case When DrOrCr = 'D' Then PostAmount Else 0 End) DebitAmount,");
			sql.append(" SUM(Case When DrOrCr = 'C' Then PostAmount * -1 Else 0 End)  CreditAmount ");
			sql.append(" From Postings T1 Where T1.ENTITYCODE = :EntityCode and T1.PostDate >= :FromDate ");
			sql.append(" and T1.PostDate <= :ToDate Group By Account)T2 ON (T1.Account = T2.Account)");
		}
		sql.append(" WHEN MATCHED THEN UPDATE SET T1.DebitAmount = T2.DebitAmount, T1.CreditAmount = T2.CreditAmount ");
		return sql;
	}

	private StringBuilder getOpeningBalanceQueryDefault() {
		StringBuilder sql = new StringBuilder();
		sql.append("Update TRIAL_BALANCE_REPORT_WORK SET");
		sql.append(" OpeningBal = T3.OpeningBalance");
		sql.append(", OpeningBalType = T3.OpeningBalType from");
		if (stateWiseReport) {
			sql.append(" (Select Distinct");
			sql.append(" ac.AcNumber Account");
			sql.append(", T1.BranchProvince StateCode");
			sql.append(", Sum(T1.ACBALANCE)*-1  OpeningBalance");
			sql.append(", (Case When Sum(T1.ACBALANCE)*-1 > 0 Then 'Dr' else 'Cr' End) OpeningBalType");
			sql.append(" From Accounts_History_Details T1");
			sql.append(" inner join accounts ac on ac.id = t1.accountid");
			sql.append(" Inner Join(select Distinct");
			sql.append(" T2.AccountID");
			sql.append(", T2.BranchProvince");
			sql.append(", T2.PostBranch");
			sql.append(", T2.EntityCode");
			sql.append(", Max(T2.PostDate) PostDate");
			sql.append(" From Accounts_History_Details T2");
			sql.append(" Where T2.PostDate < :FromDate And EntityCode = :EntityCode");
			sql.append(" Group By T2.AccountID, T2.BranchProvince, T2.PostBranch, T2.EntityCode) T2");
			sql.append(" ON T1.AccountID = T2.AccountID");
			sql.append(" and T1.PostDate = T2.PostDate");
			sql.append(" and T2.PostBranch = T1.PostBranch");
			sql.append(" and T2.BranchProvince = T1.BranchProvince");
			sql.append(" and T2.EntityCode = T1.EntityCode");
			sql.append(" Group By ac.AcNumber,T1.BranchProvince) T3");
			sql.append(" where TRIAL_BALANCE_REPORT_WORK.Account = T3.Account");
			sql.append(" and TRIAL_BALANCE_REPORT_WORK.Province = T3.StateCode");

		} else {
			sql.append(" (Select Distinct");
			sql.append(" ac.AcNumber Account");
			sql.append(", Sum(T1.ACBALANCE)*-1  OpeningBalance,");
			sql.append(" (Case When Sum(T1.ACBALANCE)*-1 > 0 Then 'Dr' else 'Cr' End) OpeningBalType");
			sql.append(" From Accounts_History_Details T1");
			sql.append(" inner join accounts ac on ac.id = t1.accountid");
			sql.append(" Inner Join( Select Distinct");
			sql.append(" T2.AccountID");
			sql.append(", T2.BranchProvince");
			sql.append(", T2.PostBranch");
			sql.append(", T2.EntityCode");
			sql.append(", Max(T2.PostDate) PostDate");
			sql.append(" From Accounts_History_Details T2");
			sql.append(" Where T2.PostDate < :FromDate And EntityCode = :EntityCode");
			sql.append(" Group By T2.AccountID, T2.BranchProvince, T2.PostBranch, T2.EntityCode) T2");
			sql.append(" ON T1.AccountID = T2.AccountID");
			sql.append(" and T1.PostDate = T2.PostDate");
			sql.append(" and T2.BranchProvince = T1.BranchProvince");
			sql.append(" and T2.POSTBRANCH = T1.POSTBRANCH");
			sql.append(" and T2.ENTITYCODE = T1.ENTITYCODE");
			sql.append(" group by ac.AcNumber) T3");
			sql.append(" where TRIAL_BALANCE_REPORT_WORK.Account = T3.Account");
		}
		return sql;

	}

	private void setProfiTLossBalance() throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("FinanceEndDate", getFinanceEndDate());
		paramMap.addValue("EntityCode", entityCode);

		StringBuilder sql = new StringBuilder();

		sql = getProfiTLossBalanceQuery();

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

	private StringBuilder getProfiTLossBalanceQuery() {
		if (App.DATABASE.equals(App.Database.ORACLE)) {
			return getProfiTLossBalanceQueryOracle();
		} else {
			return getProfiTLossBalanceQueryDefault();
		}
	}

	private StringBuilder getProfiTLossBalanceQueryOracle() {

		StringBuilder sql = new StringBuilder();
		sql.append(" MERGE INTO TRIAL_BALANCE_REPORT_WORK T1 USING (");
		if (stateWiseReport) {
			sql.append(" Select T1.AccountID, T1.BranchProvince, Sum(T1.ACBALANCE)*-1  OpeningBal ");
			sql.append(" From AccountHistoryDetails T1 Inner join (Select Distinct T2.AccountID,T2.BranchProvince,");
			sql.append(" T2.PostBranch, T2.EntityCode, Max(T2.PostDate) PostDate From AccountHistoryDetails T2");
			sql.append(" Where T2.postdate <= :FinanceEndDate And EntityCode = :EntityCode ");
			sql.append(" Group By T2.ACCOUNTID,T2.BRANCHPROVINCE,T2.POSTBRANCH,T2.ENTITYCODE) T2 ");
			sql.append(
					" ON T1.AccountID = T2.AccountID And T1.PostDate = T2.PostDate And T1.PostBranch = T2.PostBranch");
			sql.append(" And T1.BranchProvince = T2.BranchProvince And T2.EntityCode = T1.EntityCode ");
			sql.append(" Group By T1.AccountID,T1.BranchProvince) T3 ON  (T1.Account = T3.AccountID ");
			sql.append(" And T1.Province = T3.BranchProvince And T1.GroupCode IN ('INCOME','EXPENSE')) ");

		} else {
			sql.append(" Select T1.AccountID, Sum(T1.ACBALANCE)*-1  OpeningBal ");
			sql.append(" From AccountHistoryDetails T1 Inner join (Select Distinct T2.AccountID,T2.BranchProvince,");
			sql.append(" T2.PostBranch, T2.EntityCode, Max(T2.PostDate) PostDate From AccountHistoryDetails T2");
			sql.append(" Where T2.postdate <= :FinanceEndDate And EntityCode = :EntityCode ");
			sql.append(" Group By T2.AccountID,T2.BranchProvince,T2.PostBranch,T2.EntityCode) T2 ");
			sql.append(
					" ON T1.AccountID = T2.AccountID And T1.PostDate = T2.PostDate And T1.PostBranch = T2.PostBranch");
			sql.append(" And T1.BranchProvince = T2.BranchProvince And T2.EntityCode = T1.EntityCode ");
			sql.append(" Group By T1.AccountID) T3 ON  (T1.Account = T3.AccountID ");
			sql.append(" And T1.GroupCode IN ('INCOME','EXPENSE')) ");
		}

		sql.append(
				" WHEN MATCHED THEN UPDATE SET T1.PLACBalance =  T3.openingBal, T1.OpeningBal = T1.OpeningBal - T3.openingBal");
		return sql;
	}

	private StringBuilder getProfiTLossBalanceQueryDefault() {
		StringBuilder sql = new StringBuilder();
		sql.append(" Update TRIAL_BALANCE_REPORT_WORK set");
		sql.append(" PLACBalance = T3.openingBal");
		sql.append(", OpeningBal = TRIAL_BALANCE_REPORT_WORK.OpeningBal - T3.openingBal");
		sql.append(" from (");
		if (stateWiseReport) {
			sql.append("select ac.AcNumber AccountID");
			sql.append(", T1.BranchProvince");
			sql.append(", Sum(T1.ACBALANCE)*-1  OpeningBal");
			sql.append(" From Accounts_History_Details T1");
			sql.append(" inner join accounts ac on ac.id = t1.accountid");
			sql.append(" Inner join (");
			sql.append(" select Distinct T2.AccountID");
			sql.append(", T2.BranchProvince");
			sql.append(", T2.PostBranch");
			sql.append(", T2.EntityCode");
			sql.append(", Max(T2.PostDate) PostDate");
			sql.append(" From Accounts_History_Details T2");
			sql.append(" Where T2.postdate <= :FinanceEndDate");
			sql.append(" and EntityCode = :EntityCode");
			sql.append(" Group By T2.ACCOUNTID, T2.BRANCHPROVINCE, T2.POSTBRANCH, T2.ENTITYCODE) T2");
			sql.append(" ON T1.AccountID = T2.AccountID");
			sql.append(" and T1.PostDate = T2.PostDate");
			sql.append(" and T1.PostBranch = T2.PostBranch");
			sql.append(" and T1.BranchProvince = T2.BranchProvince");
			sql.append(" and T2.EntityCode = T1.EntityCode ");
			sql.append(" Group By ac.AcNumber, T1.BranchProvince) T3");
			sql.append(" where TRIAL_BALANCE_REPORT_WORK.Account = T3.AccountID");
			sql.append(" and TRIAL_BALANCE_REPORT_WORK.Province = T3.BranchProvince");
			sql.append(" and TRIAL_BALANCE_REPORT_WORK.GroupCode IN ('INCOME','EXPENSE') ");

		} else {
			sql.append(" Select ac.AcNumber AccountID, Sum(T1.ACBALANCE)*-1  OpeningBal ");
			sql.append(
					" From Accounts_History_Details T1 inner join accounts ac on ac.id = t1.accountid Inner join (Select Distinct T2.AccountID,T2.BranchProvince,");
			sql.append(" T2.PostBranch, T2.EntityCode, Max(T2.PostDate) PostDate From Accounts_History_Details T2");
			sql.append(" Where T2.postdate <= :FinanceEndDate And EntityCode = :EntityCode ");
			sql.append(" Group By T2.AccountID,T2.BranchProvince,T2.PostBranch,T2.EntityCode) T2 ");
			sql.append(
					" ON T1.AccountID = T2.AccountID And T1.PostDate = T2.PostDate And T1.PostBranch = T2.PostBranch");
			sql.append(" And T1.BranchProvince = T2.BranchProvince And T2.EntityCode = T1.EntityCode ");
			sql.append(
					" Group By ac.AcNumber) T3  where TRIAL_BALANCE_REPORT_WORK.Account = T3.AccountID  And TRIAL_BALANCE_REPORT_WORK.GroupCode IN ('INCOME','EXPENSE')");
		}
		return sql;
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

		if (App.DATABASE.equals(App.Database.ORACLE)) {
			sql.append(" MERGE INTO TRIAL_BALANCE_REPORT_WORK T1 USING (");
			if (stateWiseReport) {
				sql.append(" Select Account, Province, (OpeningBal + CreditAmount + DebitAmount) ClosingBal,");
				sql.append(" (Case When OpeningBal > 0 Then 'Dr' else 'Cr' End) OpeningBalType, ");
				sql.append(
						" (Case When (OpeningBal + CreditAmount + DebitAmount) > 0 Then 'Dr' else 'Cr' End) ClosingBalType ");
				sql.append(
						" From TRIAL_BALANCE_REPORT_WORK) T2  ON (T1.Account = T2.Account And  T1.Province = T2.Province)");

			} else {
				sql.append(" Select Account, (OpeningBal + CreditAmount + DebitAmount) ClosingBal,");
				sql.append(" (Case When OpeningBal > 0 Then 'Dr' else 'Cr' End) OpeningBalType, ");
				sql.append(
						" (Case When (OpeningBal + CreditAmount + DebitAmount) > 0 Then 'Dr' else 'Cr' End) ClosingBalType ");
				sql.append(" From TRIAL_BALANCE_REPORT_WORK) T2  ON (T1.Account = T2.Account)");
			}
			sql.append(
					" WHEN MATCHED THEN UPDATE SET T1.ClosingBal = T2.ClosingBal, T1.OpeningBalType = T2.OpeningBalType,");
			sql.append(" T1.ClosingBalType = T2.ClosingBalType ");
		} else {

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

	/**
	 * Based on given from date, return financial end date
	 * 
	 * @return finance last year date
	 */
	private Date getFinanceEndDate() {

		int year = 0;
		if (DateUtil.getMonth(fromDate) < 4) {
			year = DateUtil.getYear(DateUtil.addYears(fromDate, -1));
		} else {
			year = DateUtil.getYear(fromDate);
		}

		String month = SysParamUtil.getValueAsString("FINANCIAL_YEAR_END_MONTH");
		String date = "31";// default value;

		String financeStartDate = date + "/" + month + "/" + Integer.toString(year);
		Date financeEndDate = null;
		try {
			financeEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(financeStartDate);
		} catch (ParseException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return financeEndDate;
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

	private void initilize() throws Exception {
		loadParameters();
		clearTables();
	}

	private void clearTables() {
		logger.info("Clearing staging tables..");
		logger.debug(Literal.ENTERING);
		jdbcTemplate.execute("DELETE FROM TRIAL_BALANCE_REPORT_FILE");

		if (App.DATABASE.equals(App.Database.ORACLE)) {
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
		sql = sql.append("Delete from DATA_ENGINE_LOG where StatusId IN (");
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

		String openingBalType = "";
		if (profitLossAmt.compareTo(BigDecimal.ZERO) > 0) {
			openingBalType = "Dr";
		} else {
			openingBalType = "Cr";
		}

		String accountCode = parameters.get("PRFT_LOSS_GLCODE");

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO TRIAL_BALANCE_REPORT_FILE(");
		sql.append("ACCOUNT, DESCRIPTION, OPENINGBAL, CLOSINGBAL, OpeningBalType, ClosingBalType");
		sql.append(") VALUES (");
		sql.append(":ACCOUNT, :DESCRIPTION, :ProfitLossAmt, :ProfitLossAmt, :OpeningBalType, :OpeningBalType)");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parmsource = new MapSqlParameterSource();
		parmsource.addValue("ProfitLossAmt",
				PennantApplicationUtil.formateAmount(profitLossAmt.abs(), PennantConstants.defaultCCYDecPos));
		parmsource.addValue("ACCOUNT", accountCode);
		parmsource.addValue("DESCRIPTION", parameters.get(accountCode));
		parmsource.addValue("OpeningBalType", openingBalType);

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

	public boolean isBatchExists(String dimention, String entity) throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("STARTDATE", fromDate);
		paramMap.addValue("ENDDATE", toDate);
		paramMap.addValue("DIMENSION", dimention);
		paramMap.addValue("ENTITYCODE", entity);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(*) from TRIAL_BALANCE_HEADER");
		sql.append(" WHERE DIMENSION = :DIMENSION");
		sql.append(" AND STARTDATE = :STARTDATE");
		sql.append(" AND ENDDATE = :ENDDATE");
		sql.append(" AND ENTITYCODE = :ENTITYCODE");

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