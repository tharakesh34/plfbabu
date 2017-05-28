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
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;

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

		StringBuilder insertSql = new StringBuilder(
				"INSERT INTO CustomerQueuing (CustID, EodDate, THREADID, PROGRESS)");
		insertSql.append(
				" SELECT  DISTINCT CustID, :EodDate, :ThreadId, :Progress FROM FinanceMain where FinIsActive = :Active");

		logger.debug("updateSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);

		int records = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return records;

	}

	@Override
	public long getCountByProgress() {
		logger.debug("Entering");

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setProgress(0);
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
		customerQueuing.setProgress(EodConstants.PROGRESS_WAIT);

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
	public void updateSucess(long custID) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);
		source.addValue("Progress", EodConstants.PROGRESS_SUCCESS);
		source.addValue("EndTime", DateUtility.getSysDate());

		StringBuilder updateSql = new StringBuilder("Update CustomerQueuing set");
		updateSql.append(" EndTime = :EndTime,");
		updateSql.append(" Progress = :Progress Where CustID=:CustID ");
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
}
