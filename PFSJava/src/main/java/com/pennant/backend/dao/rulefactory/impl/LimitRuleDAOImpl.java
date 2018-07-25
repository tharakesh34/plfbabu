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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  LimtRuleDAOImpl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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

package com.pennant.backend.dao.rulefactory.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.LimitFilterQuery;
import com.pennant.backend.model.rulefactory.LimitFldCriterias;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>Rule model</b> class.<br>
 */
public class LimitRuleDAOImpl extends SequenceDao<LimitFilterQuery> implements LimitRuleDAO {
   private static Logger logger = Logger.getLogger(LimitRuleDAOImpl.class);

	
	public LimitRuleDAOImpl() {
		super();
	}
	
	/**
	 * This method will create a new object for Rule and set the Work flow
	 * details if available.
	 * 
	 * @return Rule
	 */
	@Override
	public LimitFilterQuery getLimitRule() {
		logger.debug("Entering");

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LimitFilterQuery");
		LimitFilterQuery rule = new LimitFilterQuery();

		if (workFlowDetails != null) {
			rule.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		logger.debug("Leaving");
		return rule;
	}

	/**
	 * This method will get the object from the method getRule() method and set
	 * the new Rule flag.
	 * 
	 * @return Rule
	 */
	@Override
	public LimitFilterQuery getNewLimitRule() {
		logger.debug("Entering");
		LimitFilterQuery rule = getLimitRule();
		rule.setNewRecord(true);
		logger.debug("Leaving");
		return rule;
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
	public LimitFilterQuery getLimitRuleByID(final String id,String queryModule ,String querySubCode,
			String type) {
		logger.debug("Entering");
		
		LimitFilterQuery dedupParm = new LimitFilterQuery();
		dedupParm.setQueryCode(id);
		dedupParm.setQuerySubCode(querySubCode);
		dedupParm.setQueryModule(queryModule);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select QueryId, QueryCode, QueryModule, QuerySubCode,QueryDesc, SQLQuery, ActualBlock,Active, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");	
		selectSql.append(" From LimitParams");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where QueryCode = :QueryCode AND QuerySubCode=:QuerySubCode " );
		selectSql.append(" AND QueryModule=:QueryModule" );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		RowMapper<LimitFilterQuery> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitFilterQuery.class);

		try{
			dedupParm = this.jdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
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
	public List<LimitFilterQuery> getLimitRuleByModule(String queryModule ,String querySubCode,String type) {
		logger.debug("Entering");
		 List<LimitFilterQuery> dedupParmList = new  ArrayList<LimitFilterQuery>();
		LimitFilterQuery dedupParm = new LimitFilterQuery();
		dedupParm.setQueryModule(queryModule);
		dedupParm.setQuerySubCode(querySubCode);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select QueryId, QueryCode, QueryModule, QuerySubCode,QueryDesc, SQLQuery, ActualBlock,Active, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");	
		selectSql.append(" From LimitParams");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where QueryModule=:QueryModule " );
		selectSql.append(" AND QuerySubCode=:QuerySubCode " );
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		RowMapper<LimitFilterQuery> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitFilterQuery.class);
		
		try{
			dedupParmList= this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
		}
		logger.debug("Leaving");
		return dedupParmList;
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
	public void delete(LimitFilterQuery dedupParm,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder(" Delete From LimitParams");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where QueryCode =:QueryCode");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		try{
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

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
	public long save(LimitFilterQuery dedupParm,String type) {
		logger.debug("Entering");
		
		if (dedupParm.getQueryId() == Long.MIN_VALUE) {
			dedupParm.setQueryId(getNextId("SeqLimitParams"));
			logger.debug("get NextID:" + dedupParm.getQueryId());
		}

		StringBuilder insertSql =new StringBuilder(" Insert Into LimitParams");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (QueryId , QueryCode, QueryModule,QueryDesc, SQLQuery, ActualBlock, QuerySubCode ,Active,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:QueryId , :QueryCode, :QueryModule,:QueryDesc, :SQLQuery, :ActualBlock, :QuerySubCode,:Active,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

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
	public void update(LimitFilterQuery dedupParm,String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder	updateSql =new StringBuilder(" Update LimitParams");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set QueryId = :QueryId ," );
		updateSql.append(" QueryDesc=:QueryDesc,SQLQuery = :SQLQuery," );
		updateSql.append(" ActualBlock = :ActualBlock, Active =:Active,");
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
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * This method return the columns list of the tables sent as parameters
	 * 
	 * @param module
	 *            (String)
	 * 
	 * @param event
	 *            (String)
	 * 
	 * @return List
	 * 
	 * @throws EmptyResultDataAccessException
	 * 
	 */
	public List<BMTRBFldDetails> getFieldList(String module, String event) {
		logger.debug("Entering");

		// As the event is part of primary key and storing space, passing " "
		if (StringUtils.isEmpty(event)) {
			event = " ";
		}

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RBModule", module);
		source.addValue("RBEvent", event);

		List<BMTRBFldDetails> fieldList = new ArrayList<BMTRBFldDetails>();
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RBModule , RBEvent , RBFldName , RBFldDesc,");
		selectSql
		        .append(" RBFldType ,RBFldLen , RBForCalFlds, RBForBldFlds, RBFldTableName , RBSTFlds ");
		selectSql.append(" FROM BMTRBFldDetails");
		selectSql.append(" WHERE RBModule = :RBModule AND RBEvent = :RBEvent");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<BMTRBFldDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(BMTRBFldDetails.class);

		try {
			fieldList = this.jdbcTemplate.query(selectSql.toString(), source,
			        typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			fieldList = null;
		}

		logger.debug("Leaving");
		return fieldList;
	}
	
	/**
	 * This method return the Operators list of the tables sent as parameters
	 * 
	 * @return List
	 */
	public List<LimitFldCriterias> getOperatorsList() {
		logger.debug("Entering");
		List<LimitFldCriterias> fieldList = new ArrayList<LimitFldCriterias>();	
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" SELECT QBFldType , QBSTFld , QBFldCriteriaNames , QBFldCriteriaValues " );
		selectSql.append(" FROM LimitFldCriterias");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<LimitFldCriterias> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitFldCriterias.class);

		try {
			fieldList = this.jdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			fieldList = null;
		}
		logger.debug("Leaving");
		return fieldList;
	}

}
