package com.pennant.eod.dao.impl;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;

public class CustomerQueuingDAOImpl implements CustomerQueuingDAO {

	private static Logger				logger			= Logger.getLogger(CustomerQueuingDAOImpl.class);

	private static final String			UPDATE_SQL	= "UPDATE CustomerQueuing set ThreadId=:ThreadId "
																+ "Where ThreadId IS NULL AND EodDate = :EodDate";
	private static final String			UPDATE_ORCL	= "UPDATE CustomerQueuing set ThreadId=:ThreadId "
																+ "Where  ThreadId IS NULL AND EodDate = :EodDate";
	private static final String			UPDATE_SQL_RC	= "UPDATE Top(:RowCount) CustomerQueuing set ThreadId=:ThreadId "
			+ "Where ThreadId IS NULL AND EodDate = :EodDate";
	private static final String			UPDATE_ORCL_RC	= "UPDATE CustomerQueuing set ThreadId=:ThreadId "
			+ "Where ROWNUM <=:RowCount AND ThreadId IS NULL AND EodDate = :EodDate";

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public CustomerQueuingDAOImpl() {
		super();
	}

	@Override
	public int prepareCustomerQueue(Date date) {
		logger.debug("Entering");

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setEodDate(date);

		StringBuilder insertSql = new StringBuilder("INSERT INTO CustomerQueuing (CustID,EodDate)");
		insertSql.append(" SELECT  DISTINCT CustID,:EodDate FROM FinanceMain where FinIsActive = 1");

		logger.debug("updateSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		return this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

	}

	@Override
	public long getCountByProgress(Date date) {

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setEodDate(date);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(CustID) from CustomerQueuing where EodDate=:EodDate");
		selectSql.append(" AND Progress IS NULL");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Long.class);
	}

	@Override
	public int getProgressCountByCust(long custID) {
		logger.debug("Entering");

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custID);
		customerQueuing.setProgress(EodConstants.PROGRESS_COMPLETED);

		StringBuilder selectSql = new StringBuilder("SELECT COALESCE(Count(CustID),0) from CustomerQueuing ");
		selectSql.append(" where CustID = :CustID AND Progress != :Progress");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public void updateThreadIDByRowNumber(Date date, long noOfRows, String threadId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RowCount", noOfRows);
		source.addValue("ThreadId", threadId);
		source.addValue("EodDate", date);
		try {
			
			if (noOfRows == 0) {
				if (App.DATABASE == Database.SQL_SERVER) {
					logger.debug("selectSql: " + UPDATE_SQL_RC);
					this.namedParameterJdbcTemplate.update(UPDATE_SQL, source);

				} else if (App.DATABASE == Database.ORACLE) {
					logger.debug("selectSql: " + UPDATE_ORCL_RC);
					this.namedParameterJdbcTemplate.update(UPDATE_ORCL, source);
				}
				
			}else{
				if (App.DATABASE == Database.SQL_SERVER) {
					logger.debug("selectSql: " + UPDATE_SQL_RC);
					this.namedParameterJdbcTemplate.update(UPDATE_SQL_RC, source);
				} else if (App.DATABASE == Database.ORACLE) {
					logger.debug("selectSql: " + UPDATE_ORCL_RC);
					this.namedParameterJdbcTemplate.update(UPDATE_ORCL_RC, source);
				}

			}
	
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
		}
		logger.debug("Leaving");

	}

	@Override
	public void updateThreadID(Date date, String threadId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ThreadId", threadId);
		source.addValue("EodDate", date);

		StringBuilder selectSql = new StringBuilder(
				"UPDATE CustomerQueuing set ThreadId=:ThreadId Where ThreadId IS NULL and EodDate=:EodDate");

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
		updateSql.append("  Where CustID =:CustID and EodDate=:EodDate");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerQueuing);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void delete() {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Delete from CustomerQueuing");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new CustomerQueuing());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Method for logging the every day history of Customer Queuing status.
	 */
	@Override
	public void logCustomerQueuing() {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Progress", EodConstants.PROGRESS_COMPLETED);
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


}
