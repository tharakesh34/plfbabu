package com.pennant.backend.dao.finance.impl;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.BulkRateChangeProcessDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

public class BulkRateChangeProcessDAOImpl extends BasisNextidDaoImpl<BulkProcessHeader> implements BulkRateChangeProcessDAO {

	private static Logger logger = Logger.getLogger(BulkRateChangeProcessDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new BulkProcessHeader
	 * 
	 * @return BulkProcessHeader
	 */
	@Override
	public BulkProcessHeader getBulkProcessHeader() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("BulkProcessHeader");
		BulkProcessHeader bulkProcessHeader = new BulkProcessHeader();
		if (workFlowDetails != null) {
			bulkProcessHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return bulkProcessHeader;
	}

	/**
	 * This method get the module from method getBulkProcessHeader() and set the new
	 * record flag as true and return BulkProcessHeader()
	 * 
	 * @return BulkProcessHeader
	 */
	@Override
	public BulkProcessHeader getNewBulkProcessHeader() {
		logger.debug("Entering");
		BulkProcessHeader aBulkProcessHeader = getBulkProcessHeader();
		aBulkProcessHeader.setNewRecord(true);
		logger.debug("Leaving");
		return aBulkProcessHeader;
	}

	/**
	 * Fetch the Record BulkProcessHeader Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BulkProcessHeader
	 */
	@Override
	public BulkProcessHeader getBulkProcessHeaderById(final long bulkProcessId, String type, String bulkProcessFor) {
		logger.debug("Entering");
		BulkProcessHeader bulkProcessHeader = new BulkProcessHeader();
		bulkProcessHeader.setBulkProcessId(bulkProcessId);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select BulkProcessID, BulkProcessFor, FromDate, ToDate, NewProcessedRate, ReCalType, " );
		selectSql.append(" ReCalFromDate, ReCalToDate,  ExcludeDeferement, AddTermAfter, ruleType, " );
		if(type.contains("View")){
			selectSql.append(" lovDescSqlQuery, ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BulkProcessHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where bulkProcessId = :bulkProcessId ") ;
		if(bulkProcessFor != null){
			bulkProcessHeader.setBulkProcessFor(bulkProcessFor);
			selectSql.append(" and bulkProcessFor = :bulkProcessFor ") ;
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessHeader);
		RowMapper<BulkProcessHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkProcessHeader.class);

		try {
			bulkProcessHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			bulkProcessHeader = null;
		}
		logger.debug("Leaving");
		return bulkProcessHeader;
	}

	/**
	 * Fetch the Record BulkProcessHeader Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BulkProcessHeader
	 */
	@Override
	public BulkProcessHeader getBulkProcessHeader(long bulkProcessId, Date fromDate, String type) {
		logger.debug("Entering");
		BulkProcessHeader bulkProcessHeader = new BulkProcessHeader();
		bulkProcessHeader.setBulkProcessId(bulkProcessId);
		bulkProcessHeader.setFromDate(fromDate);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select BulkProcessId, BulkProcessFor, FromDate, ToDate, NewProcessedRate, ReCalType, " );
		selectSql.append(" ReCalFromDate, ReCalToDate,  ExcludeDeferement, AddTermAfter, ruleType, " );

		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BulkProcessHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where bulkProcessId = :bulkProcessId AND  fromDate = :fromDate") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessHeader);
		RowMapper<BulkProcessHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkProcessHeader.class);

		try {
			bulkProcessHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			bulkProcessHeader = null;
		}
		logger.debug("Leaving");
		return bulkProcessHeader;
	}
	
	@Override
	public BulkProcessHeader getBulkProcessHeaderByFromAndToDates( Date fromDate, Date toDate, String type) {
		logger.debug("Entering");
		BulkProcessHeader bulkProcessHeader = new BulkProcessHeader();
		bulkProcessHeader.setFromDate(fromDate);
		bulkProcessHeader.setToDate(toDate);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select BulkProcessId, BulkProcessFor, FromDate, ToDate, NewProcessedRate, ReCalType, " );
		selectSql.append(" ReCalFromDate, ReCalToDate,  ExcludeDeferement, AddTermAfter, ruleType, " );
		
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BulkProcessHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  fromDate = :fromDate AND toDate = :toDate ") ;
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessHeader);
		RowMapper<BulkProcessHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkProcessHeader.class);
		
		try {
			bulkProcessHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			bulkProcessHeader = null;
		}
		logger.debug("Leaving");
		return bulkProcessHeader;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param BulkProcessHeader
	 *            (bulkProcessHeader)
	 * @return BulkProcessHeader
	 */
	@Override
	public void initialize(BulkProcessHeader bulkProcessHeader) {
		super.initialize(bulkProcessHeader);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param BulkProcessHeader
	 *            (bulkProcessHeader)
	 * @return void
	 */
	@Override
	public void refresh(BulkProcessHeader bulkProcessHeader) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BulkProcessHeader or
	 * BulkProcessHeader_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete BulkProcessHeader Details by key BulkProcessHeaderLevel
	 * 
	 * @param BulkProcessHeader
	 *            Details (bulkProcessHeader)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(BulkProcessHeader bulkProcessHeader, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql =new StringBuilder();
		deleteSql.append("Delete From BulkProcessHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  bulkProcessId = :bulkProcessId ");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessHeader);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),	beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", String.valueOf(bulkProcessHeader.getBulkProcessId()), 
						String.valueOf(bulkProcessHeader.getFromDate()), bulkProcessHeader.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", String.valueOf(bulkProcessHeader.getBulkProcessId()), 
					String.valueOf(bulkProcessHeader.getFromDate()), bulkProcessHeader.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BulkProcessHeader or BulkProcessHeader_Temp.
	 * 
	 * save BulkProcessHeader Details
	 * 
	 * @param BulkProcessHeader
	 *            Details (bulkProcessHeader)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(BulkProcessHeader bulkProcessHeader, String type) {
		logger.debug("Entering");
		if (bulkProcessHeader.getId() == Long.MIN_VALUE) {
			bulkProcessHeader.setBulkProcessId(getNextidviewDAO().getNextId("SeqBulkProcessHeader"));
		}
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BulkProcessHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BulkProcessId, BulkProcessFor, FromDate, Todate, NewProcessedRate, ReCalType, " );
		insertSql.append(" ReCalFromDate, ReCalToDate,  ExcludeDeferement, AddTermAfter, RuleType, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId) ");
		insertSql.append(" Values (:BulkProcessId, :BulkProcessFor, :FromDate, :ToDate, :NewProcessedRate, :ReCalType, " );
		insertSql.append(" :ReCalFromDate, :ReCalToDate,  :ExcludeDeferement, :AddTermAfter, :RuleType, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return bulkProcessHeader.getBulkProcessId();
	}

	/**
	 * This method updates the Record BulkProcessHeader or BulkProcessHeader_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update BulkProcessHeader Details by key BulkProcessHeaderLevel and Version
	 * 
	 * @param BulkProcessHeader
	 *            Details (bulkProcessHeader)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(BulkProcessHeader bulkProcessHeader, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BulkProcessHeader");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set BulkProcessId = :BulkProcessId, BulkProcessFor = :BulkProcessFor, FromDate = :FromDate, ToDate = :ToDate, " );
		updateSql.append(" NewProcessedRate = :NewProcessedRate, ReCalType = :ReCalType, ReCalFromDate = :ReCalFromDate, " );
		updateSql.append(" ReCalToDate = :ReCalToDate,  ExcludeDeferement = :ExcludeDeferement, AddTermAfter = :AddTermAfter, RuleType = :RuleType, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append("  Where BulkProcessId = :BulkProcessId ");
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails= getError("41003", String.valueOf(bulkProcessHeader.getBulkProcessId()), 
					String.valueOf(bulkProcessHeader.getFromDate()), bulkProcessHeader.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String bulkProcessHeaderLevel,String bulkProcessHeaderDecipline, String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = bulkProcessHeaderLevel;
		parms[1][1] = bulkProcessHeaderDecipline;

		parms[0][0] = PennantJavaUtil.getLabel("label_BulkRateChangeSearch_fromDate.value")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_BulkRateChangeSearch_toDate.value")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}

