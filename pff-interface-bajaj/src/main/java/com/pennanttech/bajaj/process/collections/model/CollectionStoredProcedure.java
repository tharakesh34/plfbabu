package com.pennanttech.bajaj.process.collections.model;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.StoredProcedure;

public class CollectionStoredProcedure extends StoredProcedure {

	private Logger logger = Logger.getLogger(CollectionStoredProcedure.class);

	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	Map<String, Long> inputParameters = new HashMap<String, Long>();

	/**
	 * Constructor for CollectionStoredProcedure
	 * 
	 * @param dataSource
	 */
	public CollectionStoredProcedure(DataSource dataSource, String spName) {
		super(dataSource, spName);
		super.setDataSource(dataSource);

		declareParameter(new SqlParameter("EXTRACTIONID", Types.BIGINT));
		declareParameter(new SqlOutParameter("ERRORCODE", Types.BIGINT));
		declareParameter(new SqlOutParameter("ERRORDESC", Types.NVARCHAR));
		declareParameter(new SqlOutParameter("STEP", Types.NVARCHAR));
		
		compile();
	}

	/**
	 * RepeatStatus method will be called by job definition by passing job details
	 * 
	 * @param dataSource
	 */
	public RepeatStatus execute(long extractionId) throws Exception {
		logger.debug("Entering");

		inputParameters.put("EXTRACTIONID", extractionId);

		Map<String, Object> results = execute(inputParameters);
		long errorCode = (long) results.get("ERRORCODE");

		if (errorCode == 0) {
			logger.debug(results.get("ERRORDESC"));
		} else {
			throw new SQLException("Incorrect Query" + errorCode + results.get("ERRORDESC"));
		}

		logger.debug("Leaving");

		return RepeatStatus.FINISHED;
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
