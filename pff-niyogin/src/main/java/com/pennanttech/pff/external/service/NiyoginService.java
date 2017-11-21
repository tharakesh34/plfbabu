package com.pennanttech.pff.external.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.jayway.jsonpath.JsonPath;
import com.pennanttech.dataengine.util.DateUtil;

public abstract class NiyoginService {
	private static final Logger				logger	= Logger.getLogger(NiyoginService.class);
	
	
	protected DataSource					dataSource;
	protected JdbcTemplate					jdbcTemplate;
	protected NamedParameterJdbcTemplate	namedJdbcTemplate;
	
	protected DataSourceTransactionManager transManager;
	protected DefaultTransactionDefinition transDef;
	
	public NiyoginService() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		setTransManager(dataSource);
	}

	protected Object getSMTParameter(String sysParmCode, Class<?> type) {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT SYSPARMVALUE FROM SMTPARAMETERS where SYSPARMCODE = :SYSPARMCODE");
		paramMap.addValue("SYSPARMCODE", sysParmCode);

		try {
			return namedJdbcTemplate.queryForObject(sql.toString(), paramMap, type);
		} catch (Exception e) {
			logger.error("The parameter code " + sysParmCode + " not configured.");
		} finally {
			paramMap = null;
			sql = null;
		}
		return null;
	}

	protected int updateParameter(String sysParmCode, Object value) throws Exception {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("UPDATE SMTPARAMETERS SET SYSPARMVALUE = :SYSPARMVALUE where SYSPARMCODE = :SYSPARMCODE");
		paramMap.addValue("SYSPARMCODE", sysParmCode);
		paramMap.addValue("SYSPARMVALUE", value);

		try {
			return namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error("Entering", e);
			throw new Exception("Unable to update the " + sysParmCode + ".");
		}
	}
	
	protected Date getValueDate() {
		String appDate;
		try {
			appDate = (String) getSMTParameter("APP_VALUEDATE", String.class);
			return DateUtil.parse(appDate, "yyyy-MM-dd"); // FIXME Deriving Application date should be from single place for all modules.
		} catch (Exception e) {

		}
		return null;
	}
	
	protected Date getAppDate() {
		String appDate;
		try {
			appDate = (String) getSMTParameter("APP_DATE", String.class);
			return DateUtil.parse(appDate, "yyyy-MM-dd"); // FIXME Deriving Application date should be from single place for all modules.
		} catch (Exception e) {

		}
		return null;
	}
	
	public static MapSqlParameterSource getMapSqlParameterSource(Map<String, Object> map) {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		for (Entry<String, Object> entry : map.entrySet()) {
			parmMap.addValue(entry.getKey(), entry.getValue());
		}

		return parmMap;
	}
	
	private void setTransManager(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.transManager = new DataSourceTransactionManager(dataSource);
		this.transDef = new DefaultTransactionDefinition();
		this.transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transDef.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		this.transDef.setTimeout(120);
	}
	
	protected long getSeq(String seqName) {
		logger.debug("Entering");
		StringBuilder sql = null;

		try {
			sql = new StringBuilder();
			sql.append("UPDATE ").append(seqName);
			sql.append(" SET SEQNO = SEQNO + 1");
			this.namedJdbcTemplate.update(sql.toString(), new MapSqlParameterSource());
		} catch (Exception e) {
			logger.error("Entering", e);
		}

		try {
			sql = new StringBuilder();
			sql.append("SELECT SEQNO FROM ").append(seqName);
			return this.namedJdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), Long.class);
		} catch (Exception e) {
			logger.error("Entering", e);
		}
		return 0;
	}

	/**
	 * 
	 * @param jsonResponse
	 * @return Map<String, Object>
	 */
	protected Map<String, Object> getExtendedMapValues(String jsonResponse, String extConfigFileName) {
		Map<String, Object> extendedFieldMap = new HashMap<>(1);
		try {
			Properties properties = new Properties();
			InputStream inputStream = this.getClass().getResourceAsStream("/"+extConfigFileName+".properties");
			properties.load(inputStream);
			Enumeration<?> e = properties.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				Object value = JsonPath.read(jsonResponse, properties.getProperty(key));
				if (value instanceof String) {
					extendedFieldMap.put(key, (String) JsonPath.read(jsonResponse, properties.getProperty(key)));
				} else if (value instanceof Integer) {
					extendedFieldMap.put(key, (Integer) JsonPath.read(jsonResponse, properties.getProperty(key)));
				} else if (value instanceof Boolean) {
					extendedFieldMap.put(key, (Boolean) JsonPath.read(jsonResponse, properties.getProperty(key)));
				} else if (value instanceof BigDecimal) {
					extendedFieldMap.put(key, BigDecimal.valueOf(JsonPath.read(jsonResponse, properties.getProperty(key))));
				} else {
					extendedFieldMap.put(key, JsonPath.read(jsonResponse, properties.getProperty(key)));
				}
			}
		} catch (IOException e) {
			logger.error("Exception", e);
		}
		return extendedFieldMap;
	}
}
