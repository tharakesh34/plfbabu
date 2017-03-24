package com.pennanttech.dbengine;

import java.sql.Timestamp;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennanttech.dataengine.model.DataEngineStatus;

public class DataEngineDBAccess {

	private static final Logger logger = Logger.getLogger(DataEngineDBAccess.class);

	protected DataSource appDataSource;
	protected NamedParameterJdbcTemplate appJdbcTemplate;

	public DataEngineDBAccess(DataSource dataSource, String appDBName) {
		this.appDataSource = dataSource;
		this.appJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	protected void saveBatchStatus(DataEngineStatus executionStatus) {
		logger.debug("Entering");
		
		StringBuffer query = new StringBuffer();

		query.append(" INSERT INTO DATA_ENGINE_STATUS");
		query.append(" (Id, Name, Status, UserId, StartTime, FileName, Reference, ValueDate)");
		query.append(" VALUES( :Id, :Name, 'I', :UserId, :StartTime, :FileName, :Reference, :ValueDate)");

		try {
			executionStatus.setId(getNextId("SEQ_DATA_ENGINE_STATUS"));
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(executionStatus);
			this.appJdbcTemplate.update(query.toString(), beanParameters);

		} catch (Exception e) {
			logger.error("Exception:", e);
		}
		logger.debug("Leaving");
	}

	protected long getNextId(String seqName) {
		logger.debug("Entering");
		
		String update = "update " + seqName + " set seqNo= seqNo+1 ";
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(seqName);

		this.appJdbcTemplate.update(update, beanParameters);

		String select = "select seqNo from " + seqName;
		logger.debug("Leaving");
		return this.appJdbcTemplate.getJdbcOperations().queryForObject(select, Long.class);
	}

	protected void updateBatchStatus(String status, String remarks, int processedCount, int successCount, int failedCount, int totalRecords, DataEngineStatus executionStatus) {
		logger.debug("Entering");
		
		executionStatus.setStatus(status);
		executionStatus.setEndTime(new Timestamp(System.currentTimeMillis()));
		executionStatus.setRemarks(remarks.length() > 2000 ? remarks.substring(0, 1999) : remarks);
		executionStatus.setProcessedRecords(processedCount);
		executionStatus.setSuccessRecords(successCount);
		executionStatus.setFailedRecords(failedCount);
		executionStatus.setTotalRecords(totalRecords);

		try {
			updateBatchStatus(executionStatus);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		
		logger.debug("Leaving");
	}

	private void updateBatchStatus(DataEngineStatus dataEngineStatus) {
		StringBuffer query = new StringBuffer();
		query.append(" UPDATE DATA_ENGINE_STATUS SET FileName = :FileName, Status = :Status, TotalRecords = :TotalRecords, ProcessedRecords = :ProcessedRecords, ");
		query.append(" SuccessRecords = :SuccessRecords, FailedRecords = :FailedRecords, EndTime = :EndTime, Remarks = :Remarks ");
		query.append(" WHERE Id = :Id");

		try {
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataEngineStatus);
			this.appJdbcTemplate.update(query.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
	}
}
