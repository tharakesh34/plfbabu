package com.pennanttech.pff.external;

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

import com.pennant.backend.model.cersai.Cersai;

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

	public void updateCollateralAssignment(Cersai cersai, String cersaiReferenceNumber) {
		// TODO Auto-generated method stub

	}
}
