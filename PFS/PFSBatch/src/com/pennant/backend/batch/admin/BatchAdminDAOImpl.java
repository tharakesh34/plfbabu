package com.pennant.backend.batch.admin;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepExecution;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

public class BatchAdminDAOImpl implements BatchAdminDAO{


	private static Logger logger = Logger.getLogger(BatchAdminDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	
	@Override
	public BatchProcess getCurrentBatch() {
		logger.debug("Entering");

		BatchProcess batchProcess = null;
		StringBuilder query = new StringBuilder();

		query.append(" select isnull(max(JOB_EXECUTION_ID), 0) jobId from BATCH_JOB_EXECUTION");	
		
		logger.debug("selectSql: " + query.toString());

		RowMapper<BatchProcess> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BatchProcess.class);

		try{
			batchProcess = this.namedParameterJdbcTemplate.getJdbcOperations().queryForObject(query.toString(), typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			batchProcess = null;
		} finally {
			query = null;
			typeRowMapper = null;
		}
		logger.debug("Leaving");
		return batchProcess;
	}
		
	@Override
	public List<BatchProcess> getStepDetails(StepExecution stepExecution) {
		logger.debug("Entering");
		List<BatchProcess> list = new ArrayList<BatchProcess>();
		BatchProcess batchProcess = new BatchProcess();
		batchProcess.setStepId(stepExecution.getId());
		StringBuilder query = new StringBuilder();
		SqlParameterSource beanParameters = null;

		query.append(" SELECT STEP_FIN_REF as finRef,STEP_CUSTID as custId ,STEP_FINBRANCH as finBranch,");
		query.append(" STEP_FINTYPE as finType, STEP_DETAIL as detailFields");
		query.append(" FROM BATCH_STEP_DETAILS WITH (nolock) where STEP_ID  =:stepId");

		logger.debug("selectSql: " + query.toString());

		try{
			beanParameters = new BeanPropertySqlParameterSource(batchProcess);
			RowMapper<BatchProcess> rowMapper = ParameterizedBeanPropertyRowMapper .newInstance(BatchProcess.class);
			list = this.namedParameterJdbcTemplate.query(query.toString(), beanParameters, rowMapper);	
		}catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		} finally	{
			batchProcess = null;
			query = null;
			beanParameters = null;
		}
		logger.debug("Leaving");
		return list;
	}
	

	private void saveStepDetails(BatchProcess batchProcess) {
		logger.debug("Entering");
		StringBuilder query = new StringBuilder();
		SqlParameterSource beanParameters = null;
		query.append(" INSERT INTO BATCH_STEP_DETAILS VALUES(");
		query.append(" :stepId, :finRef, :custId, :finBranch, :finType,");
		query.append(" :field1, :field2, :field3, :field4, :field5,"); 
		query.append(" :detailFields)");


		logger.debug("insertSql: "+ query.toString());
		beanParameters = new BeanPropertySqlParameterSource(batchProcess);
		try {
			this.namedParameterJdbcTemplate.update(query.toString(), beanParameters);
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			query = null;
			beanParameters  = null;			
		}
		
		logger.debug("Leaving ");
	}
	

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public void saveStepDetails(String finRef, String details, long stepId) {
		BatchProcess batchProcess = new BatchProcess();
		
		batchProcess.setStepId(stepId);
		batchProcess.setFinRef(finRef);
/*		fields.append("Profit Shedule - ");
		fields.append(String.valueOf(fpd.getTotalPftSchd()));
		fields.append(",");

		fields.append("Profit Capitalization - ");
		fields.append(String.valueOf(fpd.getTotalPftCpz()));
		fields.append(",");

		fields.append("Profit Paid - ");
		fields.append(String.valueOf(fpd.getTotalPftPaid()));
		fields.append(",");

		fields.append("Profit Balance - ");
		fields.append(String.valueOf(fpd.getTotalPftBal()));
		fields.append(",");

		fields.append("Profit Paid in Adv - ");
		fields.append(String.valueOf(fpd.getTotalPftPaidInAdv()));
		fields.append(",");*/

		batchProcess.setDetailFields(details);


		saveStepDetails(batchProcess);
		
		batchProcess = null;

	}
	

}
