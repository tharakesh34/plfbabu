package com.pennant.interfaces.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.interfaces.dao.FinanceMainDAO;
import com.pennant.interfaces.model.FetchCustomerFinancesResponse;
import com.pennant.interfaces.model.FetchFinanceScheduleResponse;
import com.pennant.interfaces.model.FetchFinanceDetailsResponse;

public class FinanceMainDAOImpl implements FinanceMainDAO {

	private final static Logger logger = LoggerFactory.getLogger(FinanceMainDAOImpl.class);
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
	public List<FetchCustomerFinancesResponse> getCustomerFinanceList(String customerNo) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustomerNo", customerNo);
		StringBuilder   selectSql = new StringBuilder("SELECT * FROM CustomerFinances_View ");
		selectSql.append(" WHERE CustomerNo=:CustomerNo");
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FetchCustomerFinancesResponse> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FetchCustomerFinancesResponse.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}
	
	@Override
	public FetchFinanceDetailsResponse getFinanceDetails(String financeRef) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinanceRef", financeRef);
		StringBuilder   selectSql = new StringBuilder("SELECT * FROM FinanceDetailsbyReference_View ");
		selectSql.append(" WHERE FinanceRef=:FinanceRef");
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FetchFinanceDetailsResponse> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FetchFinanceDetailsResponse.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
	}
	
	@Override
	public List<FetchFinanceScheduleResponse> getFinanceScheduleDetails(String financeRef) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinanceRef", financeRef);
		StringBuilder   selectSql = new StringBuilder("SELECT * FROM CustomerFinances_View ");
		selectSql.append(" WHERE CustID=:CustID");
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FetchFinanceScheduleResponse> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FetchFinanceScheduleResponse.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}
}
