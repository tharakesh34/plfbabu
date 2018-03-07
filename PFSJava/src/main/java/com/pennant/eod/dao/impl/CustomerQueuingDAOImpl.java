package com.pennant.eod.dao.impl;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;

public class CustomerQueuingDAOImpl implements CustomerQueuingDAO {

	private static Logger				logger			= Logger.getLogger(CustomerQueuingDAOImpl.class);

	private static final String			UPDATE_SQL		= "UPDATE CustomerQueuing set ThreadId=:ThreadId "
			+ "Where ThreadId = :AcThreadId";
	
	private static final String			UPDATE_SQL_RC	= "UPDATE Top(:RowCount) CustomerQueuing set ThreadId=:ThreadId "
			+ "Where ThreadId = :AcThreadId";
	
	private static final String			UPDATE_ORCL_RC	= "UPDATE CustomerQueuing set ThreadId=:ThreadId "
			+ "Where ROWNUM <=:RowCount AND ThreadId = :AcThreadId";
	

	private static final String			START_CID_RC	= "UPDATE CustomerQueuing set Progress=:Progress ,StartTime = :StartTime "
			+ "Where CustID = :CustID AND Progress=:ProgressWait";

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public CustomerQueuingDAOImpl() {
		super();
	}

	@Override
	public int prepareCustomerQueue(Date date) {
		logger.debug("Entering");

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setThreadId(0);
		customerQueuing.setProgress(0);
		customerQueuing.setEodDate(date);
		customerQueuing.setActive(true);
		customerQueuing.setLoanExist(true);
		customerQueuing.setLimitRebuild(false);
		customerQueuing.setEodProcess(true);

		StringBuilder insertSql = new StringBuilder(
				"INSERT INTO CustomerQueuing (CustID, EodDate, THREADID, PROGRESS, LOANEXIST, LimitRebuild, EodProcess)");
		insertSql.append(
				" SELECT  DISTINCT CustID, :EodDate, :ThreadId, :Progress, :LoanExist, :LimitRebuild, :EodProcess FROM FinanceMain where FinIsActive = :Active");

		logger.debug("updateSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);

		int financeRecords = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		customerQueuing.setLoanExist(false);
		
		insertSql = new StringBuilder("INSERT INTO CustomerQueuing (CustID, EodDate, THREADID, PROGRESS, LOANEXIST, LimitRebuild, EodProcess)");
		insertSql.append(" select DISTINCT CustomerID, :EodDate, :ThreadId, :Progress, :LoanExist, :LimitRebuild, :EodProcess from LimitHeader T1 ");
		insertSql.append(" Inner Join LIMITSTRUCTURE T2 on T1.LimitStructureCode = T2.StructureCode");
		insertSql.append(" Where T2.Rebuild = '1' and CustomerID Not IN (Select Distinct CustId from CustomerQueuing) and CustomerID <> 0");
		
		logger.debug("updateSql: " + insertSql.toString());
		
		beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		
		int nonFinacerecords = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");

		return financeRecords + nonFinacerecords;
	}

	@Override
	public long getCountByProgress() {
		logger.debug("Entering");

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setProgress(EodConstants.PROGRESS_WAIT);
		StringBuilder selectSql = new StringBuilder(
				"SELECT COUNT(CustID) from CustomerQueuing where Progress = :Progress");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);

		long progressCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
				Long.class);

		logger.debug("Leaving");
		return progressCount;
	}

	@Override
	public int getProgressCountByCust(long custID) {
		logger.debug("Entering");

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custID);
		customerQueuing.setProgress(EodConstants.PROGRESS_IN_PROCESS);

		StringBuilder selectSql = new StringBuilder("SELECT COALESCE(Count(CustID),0) from CustomerQueuing ");
		selectSql.append(" where CustID = :CustID AND Progress = :Progress");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);

		int records = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
				Integer.class);
		logger.debug("Leaving");

		return records;
	}

	@Override
	public int updateThreadIDByRowNumber(Date date, long noOfRows, int threadId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RowCount", noOfRows);
		source.addValue("ThreadId", threadId);
		source.addValue("EodDate", date);
		source.addValue("AcThreadId", 0);

		try {

			if (noOfRows == 0) {
				logger.debug("selectSql: " + UPDATE_SQL);
				return this.namedParameterJdbcTemplate.update(UPDATE_SQL, source);

			} else {
				if (App.DATABASE == Database.SQL_SERVER) {
					logger.debug("selectSql: " + UPDATE_SQL_RC);
					return this.namedParameterJdbcTemplate.update(UPDATE_SQL_RC, source);
				} else if (App.DATABASE == Database.ORACLE) {
					logger.debug("selectSql: " + UPDATE_ORCL_RC);
					return this.namedParameterJdbcTemplate.update(UPDATE_ORCL_RC, source);
				}

			}

		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
		}

		logger.debug("Leaving");
		return 0;

	}

	@Override
	public void updateThreadID(Date date, int threadId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ThreadId", threadId);
		source.addValue("EodDate", date);

		StringBuilder selectSql = new StringBuilder("UPDATE CustomerQueuing set ThreadId=:ThreadId Where ThreadId = 0");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			this.namedParameterJdbcTemplate.update(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
		}

		logger.debug("Leaving");

	}

	@Override
	public void updateProgress(CustomerQueuing customerQueuing) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update CustomerQueuing");
		updateSql.append(" Set Progress = :Progress");
		updateSql.append("  Where CustID =:CustID");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void update(CustomerQueuing customerQueuing, boolean start) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update CustomerQueuing set");
		if (start) {
			updateSql.append(" StartTime =:StartTime,");
		} else {
			updateSql.append(" EndTime = :EndTime,");
		}

		updateSql.append(" Progress = :Progress Where CustID =:CustID");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void updateStatus(long custID, int progress) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);
		source.addValue("Progress", progress);
		source.addValue("EndTime", DateUtility.getSysDate());

		StringBuilder updateSql = new StringBuilder("Update CustomerQueuing set");
		updateSql.append(" EndTime = :EndTime, Progress = :Progress");
		updateSql.append(" Where CustID = :CustID ");
		logger.debug("updateSql: " + updateSql.toString());

		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public void updateFailed(CustomerQueuing customerQueuing) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update CustomerQueuing set");
		updateSql.append(" EndTime = :EndTime, ThreadId = :ThreadId,");
		updateSql.append(" Progress = :Progress Where CustID =:CustID");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void delete() {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("TRUNCATE TABLE CustomerQueuing");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new CustomerQueuing());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void logCustomerQueuing(int progressSts) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Progress", progressSts);
		source.addValue("Status", EodConstants.STATUS_SUCCESS);

		StringBuilder insertSql = new StringBuilder("INSERT INTO CustomerQueuing_Log ");
		insertSql.append(" SELECT * FROM CustomerQueuing Where Progress =:Progress AND Status =:Status");

		logger.debug("updateSql: " + insertSql.toString());

		this.namedParameterJdbcTemplate.update(insertSql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public int startEODForCID(long custID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);
		source.addValue("StartTime", DateUtility.getSysDate());
		source.addValue("ProgressWait", EodConstants.PROGRESS_WAIT);
		source.addValue("Progress", EodConstants.PROGRESS_IN_PROCESS);

		try {
			logger.debug("selectSql: " + START_CID_RC);
			return this.namedParameterJdbcTemplate.update(START_CID_RC, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
		}

		logger.debug("Leaving");
		return 0;

	}

	@Override
	public List<Customer> getCustForProcess(int threadId) {
		logger.debug("Entering");

		CustomerQueuing custQueue = new CustomerQueuing();
		custQueue.setThreadId(threadId);
		custQueue.setProgress(EodConstants.PROGRESS_IN_PROCESS);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select CUST.CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode, CustDftBranch, ");
		selectSql.append(" CustPOB, CustCOB, CustGroupID, CustSts, CustStsChgDate, CustIsStaff, CustIndustry, ");
		selectSql.append(" CustSector, CustSubSector, CustEmpSts, CustSegment, CustSubSegment, CustParentCountry, ");
		selectSql.append(" CustResdCountry, CustRiskCountry, CustNationality, SalariedCustomer, custSuspSts, ");
		selectSql.append(" custSuspDate, custSuspTrigger, CustAppDate ");
		selectSql.append(" FROM  Customers CUST INNER JOIN CustomerQueuing CQ ");
		selectSql.append(" ON CUST.CustID = CQ.CustID Where ThreadID = :ThreadId and Progress=:Progress");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custQueue);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		List<Customer> customers = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		return customers;
	}
	
	@Override
	public int insertCustomerQueueing(long groupId, boolean eodProcess) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("EodDate", DateUtility.getAppValueDate());
		source.addValue("ThreadId", 0);
		source.addValue("Progress", EodConstants.PROGRESS_IN_PROCESS);
		source.addValue("LoanExist", false);
		source.addValue("StartTime", DateUtility.getSysDate());
		source.addValue("EndTime", DateUtility.getSysDate());
		source.addValue("LimitRebuild", false);
		source.addValue("Active", true);
		source.addValue("CustGroupId", groupId);
		source.addValue("EodProcess", eodProcess);

		StringBuilder insertSql = new StringBuilder("INSERT INTO CustomerQueuing (CustID, EodDate, ThreadId, Progress, LoanExist, StartTime, EndTime, LimitRebuild, EodProcess)");
		insertSql.append(" SELECT Distinct CustID, :EodDate, :ThreadId, :Progress, :LoanExist, :StartTime, :EndTime, :LimitRebuild, :EodProcess FROM Customers Where CustGroupId = :CustGroupId");
		insertSql.append(" And CustId NOT IN (Select Distinct CustId from CUSTOMERQUEUING)");

		logger.debug("insertSql: " + insertSql.toString());

		this.namedParameterJdbcTemplate.update(insertSql.toString(), source);
		
		StringBuilder updateSql = new StringBuilder("Update CustomerQueuing Set Progress = :Progress ");
		updateSql.append(" Where CustId IN (SELECT Distinct CustID FROM Customers Where CustGroupId = :CustGroupId And CustId IN (Select Distinct CustId from CUSTOMERQUEUING))");
		
		logger.debug("updateSql: " + updateSql.toString());
		int count = this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
		return count;
	}
	
	@Override
	public void updateCustomerQueuingStatus(long custGroupId, int progress) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Progress", progress);
		source.addValue("EndTime", DateUtility.getSysDate());
		source.addValue("CustGroupId", custGroupId);

		StringBuilder updateSql = new StringBuilder("Update CustomerQueuing set");
		updateSql.append(" EndTime = :EndTime, Progress = :Progress" );
		updateSql.append(" Where CustID in (SELECT Distinct CustID FROM Customers Where CustGroupId = :CustGroupId And CustId IN (Select Distinct CustId from CustomerQueuing))");

		logger.debug("updateSql: " + updateSql.toString());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
	}
	
	/**
	 * update the Rebuild flag as true if the structure has been changed.
	 */
	@Override
	public void updateLimitRebuild() {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder updateSql = new StringBuilder("Update CustomerQueuing set LimitRebuild = '1'");
		updateSql.append(" Where CUSTID in (Select  T1.CUSTOMERID from LimitHeader T1");
		updateSql.append(" Inner Join LimitStructure T2 on T2.STRUCTURECODE = T1.LIMITSTRUCTURECODE and T2.REBUILD = '1')");

		logger.debug("updateSql: " + updateSql.toString());

		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}

	/**
	 * Insert into CustomerQueuing for Customer Rebuild
	 */
	@Override
	public void insertCustQueueForRebuild(CustomerQueuing customerQueuing) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("INSERT INTO CustomerQueuing (CustID, EodDate, ThreadId, StartTime, Progress, LoanExist, LimitRebuild, EodProcess)");
		insertSql.append(" values (:CustID, :EodDate, :ThreadId, :StartTime, :Progress, :LoanExist, :LimitRebuild, :EodProcess)");

		logger.debug("updateSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);

		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Count by Customer ID for Customer Rebuild
	 */
	@Override
	public int getCountByCustId(long custID) {
		logger.debug("Entering");

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custID);

		StringBuilder selectSql = new StringBuilder("SELECT COALESCE(Count(CustID), 0) from CustomerQueuing ");
		selectSql.append(" Where CustID = :CustID");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		int records = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);

		logger.debug("Leaving");
		return records;
	}

	/**
	 * Insert into CustomerQueuing_Log after Customer Rebuild
	 */
	@Override
	public void logCustomerQueuingByCustId(long custID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);

		StringBuilder insertSql = new StringBuilder("INSERT INTO CustomerQueuing_Log ");
		insertSql.append(" SELECT * FROM CustomerQueuing Where CustID = :CustID");
		logger.debug("insertSql: " + insertSql.toString());

		this.namedParameterJdbcTemplate.update(insertSql.toString(), source);
		logger.debug("Leaving");
	}

	/**
	 * Delete Customer after customer Rebuild Process
	 */
	@Override
	public void deleteByCustId(long custID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);

		StringBuilder deleteSql = new StringBuilder("Delete From CustomerQueuing Where CustID = :CustID");
		logger.debug("deleteSql: " + deleteSql.toString());

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}

	/**
	 * insert into CustomerQueuing_Log for Customer Group Rebuild
	 */
	@Override
	public void logCustomerQueuingByGrpId(long groupId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustGroupId", groupId);

		StringBuilder insertSql = new StringBuilder("INSERT INTO CustomerQueuing_Log ");
		insertSql.append(" SELECT * FROM CustomerQueuing ");
		insertSql.append(" Where CustID in (SELECT Distinct CustID FROM Customers Where CustGroupId = :CustGroupId And CustId IN (Select Distinct CustId from CustomerQueuing))");
		logger.debug("insertSql: " + insertSql.toString());

		this.namedParameterJdbcTemplate.update(insertSql.toString(), source);
		logger.debug("Leaving");
	}

	/**
	 * Delete from CustomerQueuing after Customer Group Rebuild
	 */
	@Override
	public void deleteByGroupId(long groupId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustGroupId", groupId);

		StringBuilder deleteSql = new StringBuilder("Delete From CustomerQueuing");
		deleteSql.append(" Where CustID in (SELECT Distinct CustID FROM Customers Where CustGroupId = :CustGroupId And CustId IN (Select Distinct CustId from CustomerQueuing))");
		logger.debug("deleteSql: " + deleteSql.toString());

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}
}
