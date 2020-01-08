package com.pennant.eod.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class CustomerQueuingDAOImpl extends BasicDao<CustomerQueuing> implements CustomerQueuingDAO {
	private static Logger logger = Logger.getLogger(CustomerQueuingDAOImpl.class);

	private static final String UPDATE_SQL = "UPDATE CustomerQueuing set ThreadId=:ThreadId "
			+ "Where ThreadId = :AcThreadId";

	private static final String UPDATE_SQL_RC = "UPDATE Top(:RowCount) CustomerQueuing set ThreadId=:ThreadId "
			+ "Where ThreadId = :AcThreadId";

	private static final String UPDATE_ORCL_RC = "UPDATE CustomerQueuing set ThreadId=:ThreadId "
			+ "Where ROWNUM <=:RowCount AND ThreadId = :AcThreadId";

	private static final String START_CID_RC = "UPDATE CustomerQueuing set Progress=:Progress ,StartTime = :StartTime "
			+ "Where CustID = :CustID AND Progress=:ProgressWait";

	public CustomerQueuingDAOImpl() {
		super();
	}

	@Override
	public int prepareCustomerQueue(Date date) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO CustomerQueuing");
		sql.append("(CustID, EodDate, THREADID, PROGRESS, LOANEXIST, LimitRebuild, EodProcess)");
		sql.append(" select  distinct CustID, ?, ?, ?, ?, ?, ? FROM FinanceMain where FinIsActive = ?");

		logger.trace(Literal.SQL + sql.toString());

		int financeRecords = this.jdbcTemplate.getJdbcOperations().update(sql.toString(),
				new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setDate(1, JdbcUtil.getDate(date));
						ps.setInt(2, 0);
						ps.setInt(3, 0);
						ps.setBoolean(4, true);
						ps.setBoolean(5, false);
						ps.setBoolean(6, true);
						ps.setBoolean(7, true);

					}
				});

		sql = new StringBuilder();
		sql.append("INSERT INTO CustomerQueuing");
		sql.append("(CustID, EodDate, THREADID, PROGRESS, LOANEXIST, LimitRebuild, EodProcess)");
		sql.append(" select distinct CustomerID, ?, ?, ?, ?, ?, ? from LimitHeader lh");
		sql.append(" inner Join LIMITSTRUCTURE ls on ls.StructureCode = lh.LimitStructureCode");
		sql.append(" Where ls.Rebuild = ?");
		sql.append(" and CustomerID not in (Select Distinct CustId from CustomerQueuing) and CustomerID <> ?");

		logger.trace(Literal.SQL + sql.toString());

		int nonFinacerecords = this.jdbcTemplate.getJdbcOperations().update(sql.toString(),
				new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setDate(1, JdbcUtil.getDate(date));
						ps.setInt(2, 0);
						ps.setInt(3, 0);
						ps.setBoolean(4, false);
						ps.setBoolean(5, false);
						ps.setBoolean(6, true);
						ps.setInt(7, 1);
						ps.setInt(8, 0);

					}
				});

		return financeRecords + nonFinacerecords;
	}

	@Override
	public long getCountByProgress() {
		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setProgress(EodConstants.PROGRESS_WAIT);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(CustID) from CustomerQueuing where Progress = :Progress");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);

		long progressCount = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Long.class);

		return progressCount;
	}

	@Override
	public int getProgressCountByCust(long custID) {
		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custID);
		customerQueuing.setProgress(EodConstants.PROGRESS_IN_PROCESS);

		StringBuilder sql = new StringBuilder("SELECT COALESCE(Count(CustID),0) from CustomerQueuing ");
		sql.append(" where CustID = :CustID AND Progress = :Progress");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);

		int records = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);

		return records;
	}

	@Override
	public int updateThreadIDByRowNumber(Date date, long noOfRows, int threadId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RowCount", noOfRows);
		source.addValue("ThreadId", threadId);
		source.addValue("EodDate", date);
		source.addValue("AcThreadId", 0);

		try {

			if (noOfRows == 0) {
				logger.trace(Literal.SQL + UPDATE_SQL);
				return this.jdbcTemplate.update(UPDATE_SQL, source);

			} else {
				if (App.DATABASE == Database.SQL_SERVER) {
					logger.trace(Literal.SQL + UPDATE_SQL_RC);
					return this.jdbcTemplate.update(UPDATE_SQL_RC, source);
				} else if (App.DATABASE == Database.ORACLE) {
					logger.trace(Literal.SQL + UPDATE_ORCL_RC);
					return this.jdbcTemplate.update(UPDATE_ORCL_RC, source);
				}

			}

		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Literal.EXCEPTION, dae);
		}

		return 0;

	}

	@Override
	public void updateThreadID(Date date, int threadId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ThreadId", threadId);
		source.addValue("EodDate", date);

		StringBuilder sql = new StringBuilder("UPDATE CustomerQueuing set ThreadId=:ThreadId Where ThreadId = 0");

		logger.trace(Literal.SQL + sql.toString());

		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Literal.EXCEPTION, dae);
		}
	}

	@Override
	public void updateProgress(CustomerQueuing customerQueuing) {
		StringBuilder sql = new StringBuilder("Update CustomerQueuing");
		sql.append(" Set Progress = :Progress");
		sql.append("  Where CustID =:CustID");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

	}

	@Override
	public void update(CustomerQueuing customerQueuing, boolean start) {
		StringBuilder sql = new StringBuilder("Update CustomerQueuing set");
		if (start) {
			sql.append(" StartTime =:StartTime,");
		} else {
			sql.append(" EndTime = :EndTime,");
		}

		sql.append(" Progress = :Progress Where CustID =:CustID");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
	}

	@Override
	public void updateStatus(long custID, int progress) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);
		source.addValue("Progress", progress);
		source.addValue("EndTime", DateUtility.getSysDate());

		StringBuilder sql = new StringBuilder("Update CustomerQueuing set");
		sql.append(" EndTime = :EndTime, Progress = :Progress");
		sql.append(" Where CustID = :CustID ");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public void updateFailed(CustomerQueuing customerQueuing) {
		StringBuilder updateSql = new StringBuilder("Update CustomerQueuing set");
		updateSql.append(" EndTime = :EndTime, ThreadId = :ThreadId,");
		updateSql.append(" Progress = :Progress Where CustID =:CustID");

		logger.trace(Literal.SQL + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
	}

	@Override
	public void delete() {
		StringBuilder sql = new StringBuilder("TRUNCATE TABLE CustomerQueuing");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.getJdbcOperations().update(sql.toString());
	}

	@Override
	public void logCustomerQueuing() {

		StringBuilder sql = new StringBuilder("INSERT INTO CustomerQueuing_Log");
		sql.append(" SELECT * FROM CustomerQueuing");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.getJdbcOperations().update(sql.toString());
	}

	@Override
	public int startEODForCID(long custID) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);
		source.addValue("StartTime", DateUtility.getSysDate());
		source.addValue("ProgressWait", EodConstants.PROGRESS_WAIT);
		source.addValue("Progress", EodConstants.PROGRESS_IN_PROCESS);

		try {
			logger.trace(Literal.SQL + START_CID_RC);
			return this.jdbcTemplate.update(START_CID_RC, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
		}

		return 0;

	}

	@Override
	public List<Customer> getCustForProcess(int threadId) {
		CustomerQueuing custQueue = new CustomerQueuing();
		custQueue.setThreadId(threadId);
		custQueue.setProgress(EodConstants.PROGRESS_IN_PROCESS);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select CUST.CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode, CustDftBranch, ");
		sql.append(" CustPOB, CustCOB, CustGroupID, CustSts, CustStsChgDate, CustIsStaff, CustIndustry, ");
		sql.append(" CustSector, CustSubSector, CustEmpSts, CustSegment, CustSubSegment, CustParentCountry, ");
		sql.append(" CustResdCountry, CustRiskCountry, CustNationality, SalariedCustomer, custSuspSts, ");
		sql.append(" custSuspDate, custSuspTrigger, CustAppDate ");
		sql.append(" FROM  Customers CUST INNER JOIN CustomerQueuing CQ ");
		sql.append(" ON CUST.CustID = CQ.CustID Where ThreadID = :ThreadId and Progress=:Progress");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custQueue);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		List<Customer> customers = this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		return customers;
	}

	@Override
	public int insertCustomerQueueing(long groupId, boolean eodProcess) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("EodDate", SysParamUtil.getAppValueDate());
		source.addValue("ThreadId", 0);
		source.addValue("Progress", EodConstants.PROGRESS_IN_PROCESS);
		source.addValue("LoanExist", false);
		source.addValue("StartTime", DateUtility.getSysDate());
		source.addValue("EndTime", DateUtility.getSysDate());
		source.addValue("LimitRebuild", false);
		source.addValue("Active", true);
		source.addValue("CustGroupId", groupId);
		source.addValue("EodProcess", eodProcess);

		StringBuilder insertSql = new StringBuilder(
				"INSERT INTO CustomerQueuing (CustID, EodDate, ThreadId, Progress, LoanExist, StartTime, EndTime, LimitRebuild, EodProcess)");
		insertSql.append(
				" SELECT Distinct CustID, :EodDate, :ThreadId, :Progress, :LoanExist, :StartTime, :EndTime, :LimitRebuild, :EodProcess FROM Customers Where CustGroupId = :CustGroupId");
		insertSql.append(" And CustId NOT IN (Select Distinct CustId from CUSTOMERQUEUING)");

		logger.debug(Literal.SQL + insertSql.toString());

		this.jdbcTemplate.update(insertSql.toString(), source);

		StringBuilder updateSql = new StringBuilder("Update CustomerQueuing Set Progress = :Progress ");
		updateSql.append(
				" Where CustId IN (SELECT Distinct CustID FROM Customers Where CustGroupId = :CustGroupId And CustId IN (Select Distinct CustId from CUSTOMERQUEUING))");

		logger.trace(Literal.SQL + updateSql.toString());
		int count = this.jdbcTemplate.update(updateSql.toString(), source);

		return count;
	}

	@Override
	public void updateCustomerQueuingStatus(long custGroupId, int progress) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Progress", progress);
		source.addValue("EndTime", DateUtility.getSysDate());
		source.addValue("CustGroupId", custGroupId);

		StringBuilder sql = new StringBuilder("Update CustomerQueuing set");
		sql.append(" EndTime = :EndTime, Progress = :Progress");
		sql.append(
				" Where CustID in (SELECT Distinct CustID FROM Customers Where CustGroupId = :CustGroupId And CustId IN (Select Distinct CustId from CustomerQueuing))");

		logger.trace(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);
	}

	/**
	 * update the Rebuild flag as true if the structure has been changed.
	 */
	@Override
	public void updateLimitRebuild() {
		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Update CustomerQueuing set LimitRebuild = '1'");
		sql.append(" Where CUSTID in (Select  T1.CUSTOMERID from LimitHeader T1");
		sql.append(" Inner Join LimitStructure T2 on T2.STRUCTURECODE = T1.LIMITSTRUCTURECODE and T2.REBUILD = '1')");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
	}

	/**
	 * Insert into CustomerQueuing for Customer Rebuild
	 */
	@Override
	public void insertCustQueueForRebuild(CustomerQueuing customerQueuing) {
		StringBuilder sql = new StringBuilder(
				"INSERT INTO CustomerQueuing (CustID, EodDate, ThreadId, StartTime, Progress, LoanExist, LimitRebuild, EodProcess)");
		sql.append(
				" values (:CustID, :EodDate, :ThreadId, :StartTime, :Progress, :LoanExist, :LimitRebuild, :EodProcess)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);

		this.jdbcTemplate.update(sql.toString(), beanParameters);
	}

	/**
	 * Count by Customer ID for Customer Rebuild
	 */
	@Override
	public int getCountByCustId(long custID) {
		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custID);

		StringBuilder sql = new StringBuilder("SELECT COALESCE(Count(CustID), 0) from CustomerQueuing ");
		sql.append(" Where CustID = :CustID");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		int records = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);

		return records;
	}

	/**
	 * Insert into CustomerQueuing_Log after Customer Rebuild
	 */
	@Override
	public void logCustomerQueuingByCustId(long custID) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);

		StringBuilder sql = new StringBuilder("INSERT INTO CustomerQueuing_Log ");
		sql.append(" SELECT * FROM CustomerQueuing Where CustID = :CustID");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
	}

	/**
	 * Delete Customer after customer Rebuild Process
	 */
	@Override
	public void deleteByCustId(long custID) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);

		StringBuilder sql = new StringBuilder("Delete From CustomerQueuing Where CustID = :CustID");
		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
	}

	/**
	 * insert into CustomerQueuing_Log for Customer Group Rebuild
	 */
	@Override
	public void logCustomerQueuingByGrpId(long groupId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustGroupId", groupId);

		StringBuilder sql = new StringBuilder("INSERT INTO CustomerQueuing_Log ");
		sql.append(" SELECT * FROM CustomerQueuing ");
		sql.append(" Where CustID in (SELECT Distinct CustID FROM Customers");
		sql.append(" Where CustGroupId = :CustGroupId And CustId IN (Select Distinct CustId from CustomerQueuing))");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
	}

	/**
	 * Delete from CustomerQueuing after Customer Group Rebuild
	 */
	@Override
	public void deleteByGroupId(long groupId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustGroupId", groupId);

		StringBuilder sql = new StringBuilder("Delete From CustomerQueuing");
		sql.append(" Where CustID in (SELECT Distinct CustID FROM Customers");
		sql.append(" Where CustGroupId = :CustGroupId And CustId IN (Select Distinct CustId from CustomerQueuing))");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public long getCustQueuingCount() {
		StringBuilder sql = new StringBuilder(" SELECT COUNT(CustID) from CustomerQueuing ");
		return this.jdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), Long.class);
	}
}
