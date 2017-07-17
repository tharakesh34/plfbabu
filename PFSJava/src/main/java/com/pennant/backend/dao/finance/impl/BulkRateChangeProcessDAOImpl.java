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

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.NextidviewDAO;
import com.pennant.backend.dao.finance.BulkRateChangeProcessDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.BulkRateChangeHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

public class BulkRateChangeProcessDAOImpl extends BasisCodeDAO<BulkRateChangeHeader> implements BulkRateChangeProcessDAO {

	private static Logger logger = Logger.getLogger(BulkRateChangeProcessDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private NextidviewDAO nextidviewDAO;

	public BulkRateChangeProcessDAOImpl() {
		super();
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public NextidviewDAO getNextidviewDAO() {
		return nextidviewDAO;
	}

	public void setNextidviewDAO(NextidviewDAO nextidviewDAO) {
		this.nextidviewDAO = nextidviewDAO;
	}

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new BulkRateChangeHeader
	 * 
	 * @return BulkRateChangeHeader
	 */
	@Override
	public BulkRateChangeHeader getBulkRateChangeHeader() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("BulkRateChangeHeader");
		BulkRateChangeHeader bulkRateChangeHeader = new BulkRateChangeHeader();
		if (workFlowDetails != null) {
			bulkRateChangeHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return bulkRateChangeHeader;
	}

	/**
	 * This method get the module from method getBulkRateChangeHeader() and set the new
	 * record flag as true and return BulkRateChangeHeader()
	 * 
	 * @return BulkRateChangeHeader
	 */
	@Override
	public BulkRateChangeHeader getNewBulkRateChangeHeader() {
		logger.debug("Entering");
		BulkRateChangeHeader aBulkRateChangeHeader = getBulkRateChangeHeader();
		aBulkRateChangeHeader.setNewRecord(true);
		logger.debug("Leaving");
		return aBulkRateChangeHeader;
	}

	/**
	 * This method insert new Records into BulkRateChangeHeader or BulkRateChangeHeader_Temp.
	 *
	 * save BulkRateChangeHeader
	 * 
	 * @param BulkRateChangeHeader (bulkRateChangeHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public String save(BulkRateChangeHeader bulkRateChangeHeader, String type) {
		logger.debug("Entering");

		if (StringUtils.isBlank(bulkRateChangeHeader.getBulkRateChangeRef())) {
			String appMonthName = DateUtility.format(DateUtility.getAppDate(), PennantConstants.monthYearFormat);
			String seqNo = String.valueOf(getNextidviewDAO().getNextId("SeqBulkRateChangeHeader"));
			bulkRateChangeHeader.setBulkRateChangeRef(appMonthName + " - " + seqNo);
			logger.debug("get NextID: " + bulkRateChangeHeader.getBulkRateChangeRef());
		}

		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BulkRateChangeHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BulkRateChangeRef, FinType, FromDate, ToDate, RateChange, ReCalType, RuleType," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId, Status) ");
		insertSql.append(" Values (:BulkRateChangeRef, :FinType, :FromDate, :ToDate, :RateChange, :ReCalType, :RuleType," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId, :Status)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkRateChangeHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return bulkRateChangeHeader.getBulkRateChangeRef();
	}



	/**
	 * This method updates the Record BulkRateChangeHeader or BulkRateChangeHeader_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update BulkRateChangeHeader by key Code and Version
	 * 
	 * @param BulkRateChangeHeader (bulkRateChangeHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(BulkRateChangeHeader bulkRateChangeHeader, String type) {

		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BulkRateChangeHeader");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinType = :FinType, FromDate = :FromDate, ToDate = :ToDate," );
		updateSql.append(" RateChange = :RateChange, ReCalType = :ReCalType, RuleType = :RuleType," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, Status = :Status" );
		updateSql.append("  Where BulkRateChangeRef = :BulkRateChangeRef ");

		/*if (!type.endsWith("_TEMP")) {	//TODO
			updateSql.append(" AND Version = :Version-1");
		}*/

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkRateChangeHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");

	}

	/**
	 * This method Deletes the Record from the BulkRateChangeHeader or
	 * BulkRateChangeHeader_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete BulkRateChangeHeader Details by key BulkRateChangeHeaderLevel
	 * 
	 * @param BulkRateChangeHeader
	 *            Details (bulkRateChangeHeader)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(BulkRateChangeHeader bulkRateChangeHeader, String type) {
		logger.debug("Entering");

		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From BulkRateChangeHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  BulkRateChangeRef = :BulkRateChangeRef ");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkRateChangeHeader);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),	beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Record BulkRateChangeHeader Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BulkRateChangeHeader
	 */
	@Override
	public BulkRateChangeHeader getBulkRateChangeHeaderByRef(String bulkRateChangeRef, String type) {
		logger.debug("Entering");

		BulkRateChangeHeader bulkRateChangeHeader = new BulkRateChangeHeader();
		bulkRateChangeHeader.setBulkRateChangeRef(bulkRateChangeRef);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select BulkRateChangeRef, FinType, FromDate, ToDate, RateChange, ReCalType, RuleType," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, Status" );
		if(type.contains("View")){
			selectSql.append(", lovDescSqlQuery, lovDescQueryDesc, lovDescFinTypeDesc");
		}
		selectSql.append(" FROM  BulkRateChangeHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BulkRateChangeRef = :BulkRateChangeRef ") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkRateChangeHeader);
		RowMapper<BulkRateChangeHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkRateChangeHeader.class);

		try {
			bulkRateChangeHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			bulkRateChangeHeader = null;
		}
		logger.debug("Leaving");
		return bulkRateChangeHeader;
	}

	/**
	 * 
	 */
	@Override
	public BulkRateChangeHeader getBulkRateChangeHeaderByFromAndToDates(Date fromDate, Date toDate, String type) {
		logger.debug("Entering");
		BulkRateChangeHeader bulkRateChangeHeader = new BulkRateChangeHeader();
		bulkRateChangeHeader.setFromDate(fromDate);
		bulkRateChangeHeader.setToDate(toDate);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select BulkRateChangeRef, FinType, FromDate, ToDate, RateChange, ReCalType, RuleType," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, Status" );
		if(type.contains("View")){
			selectSql.append(", lovDescSqlQuery, lovDescQueryDesc, lovDescFinTypeDesc");
		}
		selectSql.append(" FROM  BulkRateChangeHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FromDate = :FromDate AND ToDate = :ToDate ") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkRateChangeHeader);
		RowMapper<BulkRateChangeHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkRateChangeHeader.class);

		try {
			bulkRateChangeHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			bulkRateChangeHeader = null;
		}
		logger.debug("Leaving");
		return bulkRateChangeHeader;
	}

	/**
	 * This Method is used for generating The BulkRateChangeRef
	 */
	@Override
	public String getBulkRateChangeReference() {
		logger.debug("Entering");
		String appMonthName = DateUtility.format(DateUtility.getAppDate(), PennantConstants.monthYearFormat);
		String seqNo = String.valueOf(getNextidviewDAO().getNextId("SeqBulkRateChangeHeader"));
		logger.debug("BulkRateChange Ref: " + appMonthName + " - " + seqNo);

		logger.debug("Leaving");
		return appMonthName + " - " + seqNo;
	}

}
