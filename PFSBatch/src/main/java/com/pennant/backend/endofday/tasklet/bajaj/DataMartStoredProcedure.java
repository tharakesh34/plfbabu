package com.pennant.backend.endofday.tasklet.bajaj;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.StoredProcedure;

public class DataMartStoredProcedure extends StoredProcedure implements Tasklet {
	private Logger logger = Logger.getLogger(DataMartStoredProcedure.class);
	
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

 	Map<String,Long> inputParameters = new HashMap<String, Long>();

	
	/**
	 * Constructor for storedprocedure Tasklet
	 * 
	 * @param dataSource
	 */
	
	public DataMartStoredProcedure (DataSource dataSource,String spName) {
		super(dataSource, spName);
		super.setDataSource(dataSource);
 	
		declareParameter(new SqlParameter("BATCH_ID", Types.BIGINT));
 		
		declareParameter(new SqlOutParameter("ERROR_CODE", Types.BIGINT));
		declareParameter(new SqlOutParameter("ERROR_DESC", Types.NVARCHAR));
        compile();
	}

	/**
	 * RepeatStatus method will be called by job definition by passing job details
	 * 
	 * @param dataSource
	 */
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
			throws Exception {
		
		inputParameters.put("BATCH_ID", getBatchID());
  		
 	//	try {
		Map<String,Object> results;
		results = execute(inputParameters);
			long errorCode = 	(long) results.get("ERROR_CODE");	
			if(errorCode== 0){
				logger.debug(results.get("ERROR_DESC"));
			}else{
				throw new SQLException("Incorrect Query"+ errorCode+results.get("ERROR_DESC"));
			}
  
				return RepeatStatus.FINISHED;
	}

	
	
	public Long getBatchID() {
		logger.debug("Entering ");

		Long batchID = null;

		Map<String, Long> namedParamters = Collections.singletonMap("DATAMART", batchID);
		StringBuilder selectSql = new StringBuilder("select max(BatchID) from Datamart_Header ");
		logger.debug("selectSql: " + selectSql.toString());

		try {
			batchID = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), namedParamters,
					 Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			batchID = null;
		}

		logger.debug("Leaving ");
		return batchID;
	}

	public Map<String, Long> getInParameters() {
		return inputParameters;
	}
	public void setInParameters(Map<String, Long> inParameters) {
		this.inputParameters = inParameters;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
}
