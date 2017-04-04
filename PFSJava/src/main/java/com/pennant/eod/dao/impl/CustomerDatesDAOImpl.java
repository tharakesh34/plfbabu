package com.pennant.eod.dao.impl;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.eod.beans.CustomerDates;
import com.pennant.eod.dao.CustomerDatesDAO;

public class CustomerDatesDAOImpl implements CustomerDatesDAO {

	private static Logger logger = Logger.getLogger(CustomerDatesDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerDatesDAOImpl() {
		super();
	}


	/**
	 * Dump the CustID, AppDate, ValueDate and NextBusinessDate fields from Customers table
	 * 
	 * @param appDate
	 * @param valueDate
	 * @param nextBusinessDate
	 */
	@Override
	public void saveCustomerDates(Date appDate, Date valueDate, Date nextBusinessDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", appDate);
		source.addValue("ValueDate", valueDate);
		source.addValue("NextBusinessDate", nextBusinessDate);

		StringBuilder insertSql = new StringBuilder("INSERT INTO CustomerDates ");
		insertSql.append(" SELECT CustID, :AppDate, :ValueDate, :NextBusinessDate FROM Customers ");
		insertSql.append(" WHERE CustID NOT IN (SELECT CustID FROM CustomerDates)");

		logger.debug("updateSql: "+ insertSql.toString());

		this.namedParameterJdbcTemplate.update(insertSql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * Update Customer business dates
	 * 
	 * @param custId
	 * @param appDate
	 * @param valueDate
	 * @param nextBusinessDate
	 */
	@Override
	public void updateCustomerDates(long custId, Date appDate, Date valueDate, Date nextBusinessDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", appDate);
		source.addValue("ValueDate", valueDate);
		source.addValue("NextBusinessDate", nextBusinessDate);
		source.addValue("CustID", custId);

		StringBuilder updateSql = new StringBuilder("UPDATE CustomerDates ");
		updateSql.append(" SET AppDate =:AppDate, ValueDate =:ValueDate, NextBusinessDate =:NextBusinessDate ");
		updateSql.append(" WHERE CustID =:CustID");

		logger.debug("updateSql: "+ updateSql.toString());

		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
	}
	
	/**
	 * get Customer business date
	 * @param custId
	 * @return 
	 */
	@Override
	public CustomerDates getCustomerDates(long custId) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custId);
		
		StringBuilder updateSql = new StringBuilder("Select AppDate,ValueDate,NextBusinessDate FROM CustomerDates ");
		updateSql.append(" WHERE CustID =:CustID");
		RowMapper<CustomerDates> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerDates.class);
		logger.debug("updateSql: "+ updateSql.toString());
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(updateSql.toString(), source,typeRowMapper);
		
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}
