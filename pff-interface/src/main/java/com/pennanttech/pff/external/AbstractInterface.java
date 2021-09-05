package com.pennanttech.pff.external;

import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennanttech.dataengine.model.DataEngineStatus;

public class AbstractInterface {
	protected DataSource dataSource;

	protected JdbcTemplate jdbcTemplate;
	protected NamedParameterJdbcTemplate namedJdbcTemplate;
	protected JdbcOperations jdbcOperations;

	protected DataSourceTransactionManager transManager;
	protected DefaultTransactionDefinition transDef;

	public AbstractInterface() {
		super();
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcOperations = namedJdbcTemplate.getJdbcOperations();

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

	protected void updateDEStatus(DataEngineStatus status) {
		String sql = "UPDATE DATA_ENGINE_STATUS Set Status = ?, SuccessRecords = ?, FailedRecords = ?,  Remarks = ? WHERE Id = ?";

		this.namedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setString(index++, status.getStatus());
			ps.setLong(index++, status.getSuccessRecords());
			ps.setLong(index++, status.getFailedRecords());
			ps.setString(index++, status.getRemarks());
			ps.setLong(index++, status.getId());
		});
	}

	protected void logError(long id, String keyId, String status, String reason) {
		if (reason == null) {
			return;
		}

		String sql = "INSERT INTO DATA_ENGINE_LOG (StatusId, KeyId, Status, Reason) VALUES(?, ?, ?, ?)";

		this.namedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setLong(index++, id);
			ps.setString(index++, keyId);
			ps.setString(index++, status);
			ps.setString(index, reason.length() > 2000 ? reason.substring(0, 1999) : reason);
		});
	}

}
