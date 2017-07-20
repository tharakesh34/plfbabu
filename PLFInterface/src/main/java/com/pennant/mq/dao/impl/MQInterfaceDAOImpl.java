package com.pennant.mq.dao.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennant.mq.dao.MQInterfaceDAO;
import com.pennanttech.pennapps.core.InterfaceException;

public class MQInterfaceDAOImpl implements MQInterfaceDAO {
	private static Logger logger = Logger.getLogger(MQInterfaceDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public MQInterfaceDAOImpl() {
		super();
	}
	private MQInterfaceDAO mqInterfaceDAO;
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
	/**
	 * Mapping MDM code with PFF code
	 * 
	 */
    public String getMDMCode(String code,String tableName) throws InterfaceException {
		logger.debug("Entering");
		String value =new String();
		StringBuilder selectSql = new StringBuilder("Select value From "+tableName);
		selectSql.append(" where code =:Code");
		
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("Code", code);
		try {
			value = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),parameterSource,String.class); 
		} catch(Exception e) {
			logger.warn("Exception: ", e);
			throw new InterfaceException("PTI7001", "MDM Code not found");
		}
			
		logger.debug("Leaving");
		return value;
	}
	
	@Override
	/**
	 * Mapping PFF code with MDM code
	 * 
	 */
	public String getPFFCode(String value,String tableName) throws InterfaceException {
		logger.debug("Entering");
		String mdmCode =new String();
		StringBuilder selectSql = new StringBuilder("Select code From "+tableName);
		selectSql.append(" where value =:Value");

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("Value", value);

		try {
			mdmCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),parameterSource,String.class); 
		} catch(Exception e) {
			logger.warn("Exception: ", e);
			throw new InterfaceException("PTI7001", "PFF Code not found");
		}
		logger.debug("Leaving");

		return mdmCode;
	}

	public MQInterfaceDAO getMqInterfaceDAO() {
		return mqInterfaceDAO;
	}

	public void setMqInterfaceDAO(MQInterfaceDAO mqInterfaceDAO) {
		this.mqInterfaceDAO = mqInterfaceDAO;
	}
		
}


