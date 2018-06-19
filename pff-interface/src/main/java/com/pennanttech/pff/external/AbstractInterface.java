package com.pennanttech.pff.external;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class AbstractInterface {
	private static final Logger logger = Logger.getLogger(AbstractInterface.class);

	protected DataSource dataSource;
	
	protected JdbcTemplate jdbcTemplate;
	protected NamedParameterJdbcTemplate namedJdbcTemplate;

	protected DataSourceTransactionManager transManager;
	protected DefaultTransactionDefinition transDef;

	public AbstractInterface() {
		super();
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		setTransManager(dataSource);
	}

	public Object getSMTParameter(String sysParmCode, Class<?> type) {
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
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to update the " + sysParmCode + ".");
		}
	}

	protected Date getValueDate() {
		String appDate;
		try {
			appDate = (String) getSMTParameter("APP_VALUEDATE", String.class);
			return DateUtil.parse(appDate, "yyyy-MM-dd"); // FIXME Deriving
															// Application date
															// should be from
															// single place for
															// all modules.
		} catch (Exception e) {

		}
		return null;
	}

	protected Date getAppDate() {
		String appDate;
		try {
			appDate = (String) getSMTParameter("APP_DATE", String.class);
			return DateUtil.parse(appDate, "yyyy-MM-dd"); // FIXME Deriving
															// Application date
															// should be from
															// single place for
															// all modules.
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
}
