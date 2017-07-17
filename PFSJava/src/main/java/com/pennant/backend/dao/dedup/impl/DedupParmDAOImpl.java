/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  DedupParmDAOImpl.java                                                * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.dedup.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.dedup.DedupParmDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>DedupParm model</b> class.<br>
 * 
 */
public class DedupParmDAOImpl extends BasisNextidDaoImpl<DedupParm> implements DedupParmDAO {
	private static Logger logger = Logger.getLogger(DedupParmDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public DedupParmDAOImpl() {
		super();
	}


	/**
	 * Fetch the Record  Dedup Parameters details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DedupParm
	 */
	@Override
	public DedupParm getDedupParmByID(final String id,String queryModule ,String querySubCode,
			String type) {
		logger.debug("Entering");
		
		DedupParm dedupParm = new DedupParm();
		dedupParm.setQueryCode(id);
		dedupParm.setQuerySubCode(querySubCode);
		dedupParm.setQueryModule(queryModule);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select QueryId, QueryCode, QueryModule, QuerySubCode,QueryDesc, SQLQuery, ActualBlock, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");	
		selectSql.append(" From DedupParams");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where QueryCode = :QueryCode AND QuerySubCode=:QuerySubCode " );
		selectSql.append(" AND QueryModule=:QueryModule" );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		RowMapper<DedupParm> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DedupParm.class);

		try{
			dedupParm = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			dedupParm = null;
		}
		logger.debug("Leaving");
		return dedupParm;
	}
	/**
	 * Fetch the Record  Dedup Parameters details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DedupParm
	 */
	@Override
	public List<DedupParm> getDedupParmByModule(String queryModule ,String querySubCode,String type) {
		logger.debug("Entering");
		
		DedupParm dedupParm = new DedupParm();
		dedupParm.setQueryModule(queryModule);
		dedupParm.setQuerySubCode(querySubCode);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select QueryId, QueryCode, QueryModule, QuerySubCode,QueryDesc, SQLQuery, ActualBlock, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");	
		selectSql.append(" From DedupParams");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where QueryModule=:QueryModule " );
		selectSql.append(" AND QuerySubCode=:QuerySubCode " );
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		RowMapper<DedupParm> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DedupParm.class);
		
		try{
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * Method getting list of Data in validation of result builded Query
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List validate(String resultQuery,CustomerDedup customerDedup) {
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDedup);
		return this.namedParameterJdbcTemplate.queryForList(resultQuery, beanParameters);	
	}

	/**
	 * This method Deletes the Record from the DedupParams or DedupParams_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Dedup Parameters by key QueryCode
	 * 
	 * @param Dedup Parameters (dedupParm)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(DedupParm dedupParm,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder(" Delete From DedupParams");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where QueryCode =:QueryCode");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into DedupParams or DedupParams_Temp.
	 *
	 * save Dedup Parameters 
	 * 
	 * @param Dedup Parameters (dedupParm)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(DedupParm dedupParm,String type) {
		logger.debug("Entering");
		
		if (dedupParm.getQueryId() == Long.MIN_VALUE) {
			dedupParm.setQueryId(getNextidviewDAO().getNextId("SeqDedupParams"));
			logger.debug("get NextID:" + dedupParm.getQueryId());
		}

		StringBuilder insertSql =new StringBuilder(" Insert Into DedupParams");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (QueryId , QueryCode, QueryModule,QueryDesc, SQLQuery, ActualBlock, QuerySubCode ,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:QueryId , :QueryCode, :QueryModule,:QueryDesc, :SQLQuery, :ActualBlock, :QuerySubCode,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return dedupParm.getQueryId();
	}

	/**
	 * This method updates the Record DedupParams or DedupParams_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Dedup Parameters by key QueryCode and Version
	 * 
	 * @param Dedup Parameters (dedupParm)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(DedupParm dedupParm,String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder	updateSql =new StringBuilder(" Update DedupParams");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set QueryId = :QueryId ," );
		updateSql.append(" QueryDesc=:QueryDesc,SQLQuery = :SQLQuery," );
		updateSql.append(" ActualBlock = :ActualBlock, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where QueryCode =:QueryCode AND QueryModule=:QueryModule" );
		updateSql.append(" AND QuerySubCode=:QuerySubCode");

		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetched the Dedup Fields if Dedup Exist for a Customer
	 *
	 */
	public List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup dedup,String sqlQuery) {
		logger.debug("Entering");
		List<CustomerDedup> rowTypes = null;
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT * FROM CustomersDedup_View ");
		selectSql.append(StringUtils.trimToEmpty(sqlQuery));
		selectSql.append(" AND custId != :custId ");

		logger.debug("selectSql: " +  selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		ParameterizedBeanPropertyRowMapper<CustomerDedup> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(CustomerDedup.class);

		try{
			rowTypes = this.namedParameterJdbcTemplate.query(selectSql.toString(),
					beanParameters,typeRowMapper);
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			dedup = null;
		}
		logger.debug("Leaving");
		return rowTypes;
	}
	
	/**
	 * Fetched the Dedup Fields if Dedup Exist for a Customer
	 *
	 */
	public List<FinanceDedup> fetchFinDedupDetails(FinanceDedup dedup,String sqlQuery) {
		logger.debug("Entering");
		List<FinanceDedup> rowTypes = null;
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT * FROM FinanceDedup_View ");
		selectSql.append(StringUtils.trimToEmpty(sqlQuery));
		selectSql.append(" AND FinReference != :FinReference ");

		logger.debug("selectSql: " +  selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		ParameterizedBeanPropertyRowMapper<FinanceDedup> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(FinanceDedup.class);

		try{
			rowTypes = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			dedup = null;
		}
		logger.debug("Leaving");
		return rowTypes;
	}
	
	/**
	 * Method for Fetching List of Query Details based on Execution Stage & Finance Type
	 */
	@Override
    public List<FinanceReferenceDetail> getQueryCodeList(FinanceReferenceDetail financeRefDetail, String tableType) {
		logger.debug("Entering");
		
		List<FinanceReferenceDetail> finRefDetail = null;
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select alertType, lovDescNamelov, OverRide,lovDescRefDesc from LMTFinRefDetail");
		selectSql.append(tableType);
		selectSql.append(" Where MandInputInStage LIKE('%"+financeRefDetail.getMandInputInStage()+"%') AND FinType = :FinType AND IsActive = 1 ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRefDetail);
		RowMapper<FinanceReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceReferenceDetail.class);

		try {
			finRefDetail = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finRefDetail = null;
		}
		
		logger.debug("Leaving");
	    return finRefDetail;
    }

	@Override
    public List<String> getRuleFieldNames(String moduleType) {
		logger.debug("Entering");
		
		List<String> fieldNames = null;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("QueryModule",moduleType);
		StringBuilder selectSql = new StringBuilder(" Select FieldName From DedupFields where QueryModule =:QueryModule");
		logger.debug("selectSql: " + selectSql.toString());
		
		try{
			fieldNames = this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), mapSqlParameterSource, null);	
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			fieldNames = new ArrayList<String>();
		}
		logger.debug("Leaving");
		return fieldNames;
    }
}