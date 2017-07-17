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
 * FileName    		:  RuleDAOImpl.java                           
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.rulefactory.BMTRBFldCriterias;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.RuleModule;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>Rule model</b> class.<br>
 */
public class RuleDAOImpl extends BasisNextidDaoImpl<Rule> implements RuleDAO {

	private static Logger logger = Logger.getLogger(RuleDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public RuleDAOImpl() {
		super();
	}
	
	/**
	 * Get Rule by key field
	 * 
	 * @param id (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Rule
	 */
	@Override
	public Rule getRuleByID(final String id,final String module,final String event, String type) {
		logger.debug("Entering");
		
		Rule rule = new Rule();
		rule.setRuleCode(id);
		rule.setRuleModule(module);
		rule.setRuleEvent(event);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RuleId, RuleCode, RuleModule,RuleEvent, RuleCodeDesc,AllowDeviation,CalFeeModify, FeeToFinance, ");
		selectSql.append(" WaiverDecider , Waiver, WaiverPerc, SQLRule, ActualBlock,SeqOrder, ReturnType, DeviationType, GroupId,  Revolving,FixedOrVariableLimit,Active,");
		selectSql.append(" Fields, FeeTypeID,");
		if(type.contains("View")){
			selectSql.append(" lovDescGroupName , FeeTypeCode, FeeTypeDesc, ");
		}	
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From Rules");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleCode =:RuleCode AND RuleModule =:RuleModule AND RuleEvent =:RuleEvent");
		selectSql.append(" Order BY SeqOrder ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		try {
			rule = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			rule = null;
		}
		logger.debug("Leaving");
		return rule;
	}
	
	@Override
	public Rule getRuleByID(long ruleId,String type) {
		logger.debug("Entering");
		Rule rule = new Rule();
		rule.setRuleId(ruleId);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RuleId, RuleCode, RuleModule,RuleEvent, RuleCodeDesc,AllowDeviation,CalFeeModify, FeeToFinance, ");
		selectSql.append(" WaiverDecider , Waiver, WaiverPerc, SQLRule, ActualBlock,SeqOrder, ReturnType, DeviationType, GroupId,  Revolving,FixedOrVariableLimit,Active,");
		selectSql.append(" Fields, FeeTypeID,");
		if(type.contains("View")){
			selectSql.append(" lovDescGroupName , FeeTypeCode, FeeTypeDesc,");
		}	
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From Rules");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleId =:RuleId");
		selectSql.append(" Order BY SeqOrder ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		
		try {
			rule = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			rule = null;
		}
		logger.debug("Leaving");
		return rule;
	}
	
	/**
	 * Get Rule by key field
	 * 
	 * @param id (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Rule
	 */
	@Override
	public String getAmountRule(final String id,final String module,final String event) {
		logger.debug("Entering");
		
		String sqlRule = "";
		Rule rule = new Rule();
		rule.setRuleCode(id);
		rule.setRuleModule(module);
		rule.setRuleEvent(event);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT SQLRule From Rules ");
		selectSql.append(" Where RuleCode =:RuleCode AND RuleModule =:RuleModule AND RuleEvent =:RuleEvent");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);

		try {
			sqlRule = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			sqlRule = "";
		}finally{
			rule = null;
			beanParameters = null;
		}
		logger.debug("Leaving");
		return sqlRule;
	}

	/**
	 * Get Rule by key field
	 * 
	 * @param id (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Rule
	 */
	@Override
	public List<Rule> getSubHeadRuleList(List<String> subHeadRuleList) {
		logger.debug("Entering");
		
		List<Rule> sqlRule = null;
		
		if(subHeadRuleList.isEmpty()) {
			return new ArrayList<Rule>();
		}
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RuleCode", subHeadRuleList);
		source.addValue("RuleModule", RuleConstants.MODULE_SUBHEAD);
		source.addValue("RuleEvent", RuleConstants.MODULE_SUBHEAD);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RuleCode, SQLRule From Rules ");
		selectSql.append(" Where RuleCode IN(:RuleCode) AND RuleModule =:RuleModule AND RuleEvent =:RuleEvent");
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		try {
			sqlRule = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			sqlRule = new ArrayList<Rule>();
		}
		logger.debug("Leaving");
		return sqlRule;
	}
	
	/**
	 * Get Rule by key field
	 * 
	 * @param id (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Rule
	 */
	@Override
	public List<Rule> getRuleByModuleAndEvent(final String module,final String event, String type) {
		logger.debug("Entering");
		
		Rule rule = new Rule();
		rule.setRuleModule(module);
		rule.setRuleEvent(event);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RuleId, RuleCode, RuleModule,RuleEvent, RuleCodeDesc, AllowDeviation,CalFeeModify,FeeToFinance, ");
		selectSql.append(" WaiverDecider , Waiver, WaiverPerc, SQLRule, ActualBlock,SeqOrder, ReturnType, DeviationType,  Revolving,FixedOrVariableLimit,Active,");
		selectSql.append(" Fields, FeeTypeID,");
		if(type.contains("View")){
			selectSql.append(" FeeTypeCode, FeeTypeDesc,");
		}	
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From Rules");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleModule =:RuleModule AND RuleEvent =:RuleEvent");
		selectSql.append(" Order BY SeqOrder ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

	
			
	
		logger.debug("Leaving");
		return  this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Set the DataSource object to the NamedParameterJdbcTemplate
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the Rules or Rules_Temp. if
	 * Record not deleted then throws DataAccessException with error code 41004.
	 * delete Rules by key RuleCode
	 * 
	 * @param Rule (rule)
	 * 
	 * @param type (String) ""/_Temp/_View
	 * 
	 * @return void
	 * 
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Rule rule, String type) {
		logger.debug("Entering");
		
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From Rules");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where RuleCode =:RuleCode AND RuleModule =:RuleModule AND RuleEvent =:RuleEvent");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into Rules or Rules_Temp.
	 * 
	 * save Rules
	 * 
	 * @param Rules (rule)
	 * 
	 * @param type
	 *            (String) ""/_Temp/_View
	 * 
	 * @return void
	 * 
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(Rule rule, String type) {
		logger.debug("Entering");
		if (rule.getId() == Long.MIN_VALUE) {
			rule.setId(getNextidviewDAO().getNextId("SeqRules"));
			logger.debug("get NextID:" + rule.getId());
		}			
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into Rules");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RuleId,RuleCode, RuleModule,RuleEvent,RuleCodeDesc, WaiverDecider, Waiver, " );
		insertSql.append(" WaiverPerc, SQLRule, ActualBlock, SeqOrder,ReturnType,DeviationType,GroupId, AllowDeviation,CalFeeModify,FeeToFinance,  Revolving,FixedOrVariableLimit,Active");
		insertSql.append(" ,Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, Fields, feeTypeID)");
		insertSql.append(" Values(:RuleId ,:RuleCode, :RuleModule,:RuleEvent, :RuleCodeDesc, :WaiverDecider, " );
		insertSql.append(" :Waiver, :WaiverPerc, :SQLRule, :ActualBlock,:SeqOrder, :ReturnType,:DeviationType,:GroupId, :AllowDeviation, :CalFeeModify,:FeeToFinance,  :Revolving,:FixedOrVariableLimit,:Active");
		insertSql.append(" ,:Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId, :Fields, :feeTypeID)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return rule.getId();
	}

	/**
	 * This method updates the Record Rules or Rules_Temp. if Record not
	 * updated then throws DataAccessException with error 41003. update Rules
	 * by key RuleCode and Version
	 * 
	 * @param Rules (rule)
	 * 
	 * @param type (String) ""/_Temp/_View
	 * 
	 * @return void
	 * 
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(Rule rule, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update Rules");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set RuleCodeDesc = :RuleCodeDesc," );
		updateSql.append(" WaiverDecider=:WaiverDecider, Waiver =:Waiver, WaiverPerc=:WaiverPerc , SQLRule = :SQLRule,");
		updateSql.append(" AllowDeviation =:AllowDeviation, CalFeeModify = :CalFeeModify, ActualBlock = :ActualBlock ,SeqOrder=:SeqOrder,");
		updateSql.append(" ReturnType =:ReturnType,DeviationType =:DeviationType, GroupId=:GroupId,FeeToFinance=:FeeToFinance,  Revolving=:Revolving,FixedOrVariableLimit=:FixedOrVariableLimit,Active =:Active");
		updateSql.append(" ,Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId , Fields = :Fields, FeeTypeID = :FeeTypeID");
		updateSql.append(" Where RuleCode =:RuleCode AND RuleModule =:RuleModule AND RuleEvent =:RuleEvent ");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

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
			fieldList = this.namedParameterJdbcTemplate.query(selectSql.toString(), source,
			        typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
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
	public List<BMTRBFldCriterias> getOperatorsList() {
		logger.debug("Entering");
		List<BMTRBFldCriterias> fieldList = new ArrayList<BMTRBFldCriterias>();	
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" SELECT RBFldType , RBSTFld , RBFldCriteriaNames , RBFldCriteriaValues " );
		selectSql.append(" FROM BMTRBFldCriterias");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<BMTRBFldCriterias> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BMTRBFldCriterias.class);

		try {
			fieldList = this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			fieldList = null;
		}
		logger.debug("Leaving");
		return fieldList;
	}
	
	/**
	 * Method for getting List of Modules
	 */
	@Override
	public List<RuleModule> getRuleModules(String module) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RBMModule, RBMEvent, RBMFldName, RBMFldType ");
		selectSql.append(" from BMTRBFldMaster");
		if (StringUtils.isNotEmpty(module)) {
			selectSql.append(" where RBMModule='"+module+"'");
		}
	
		logger.debug(" selectSql: " + selectSql.toString());
		RowMapper<RuleModule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RuleModule.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);
	}
	
	/**
	 * Method for Fetching List of Rules using Key GroupId 
	 * for Financial Scoring Metric Calculations
	 */
	@Override
    public List<Rule> getRulesByGroupId(long groupId, String ruleModule, String ruleEvent,String type) {
		logger.debug("Entering");
		
		Rule rule = new Rule();
		rule.setGroupId(groupId);
		rule.setRuleModule(ruleModule);
		rule.setRuleEvent(ruleEvent);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  RuleCode, RuleCodeDesc,SQLRule ");
		selectSql.append(" From Rules");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleModule =:RuleModule AND RuleEvent =:RuleEvent AND GroupId=:GroupId ");
		selectSql.append(" Order BY SeqOrder ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }
	
	/**
	 * Method for Fetching List of Rules using Key GroupId 
	 * for Financial Scoring Metric Calculations
	 */
	@Override
    public List<Rule> getRulesByGroupIdList(long groupId, String categoryType, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select RuleCode, RuleCodeDesc, SqlRule, GroupId ");
		selectSql.append(" From Rules");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE GroupId In (Select ScoringId from RMTScoringMetrics_View " );
		selectSql.append(" WHERE ScoreGroupId = " + groupId);
		selectSql.append(" AND CategoryType='" + categoryType );
		selectSql.append("') ");
		selectSql.append(" order By GroupId , SeqOrder ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);
    }
	
	/**
	 * Method for Fetching List of Rules using Key GroupId 
	 * for Financial Scoring Metric Calculations
	 */
	@Override
    public List<Rule> getRulesByFinScoreGroup(List<Long> groupIds, String categoryType, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select RuleId, RuleCode, RuleCodeDesc, SqlRule, GroupId ");
		selectSql.append(" From Rules");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE GroupId In (:GroupIds) ");
		selectSql.append(" order By GroupId , SeqOrder ");
		
		Map<String, List<Long>> parameterMap=new HashMap<String, List<Long>>();
		parameterMap.put("GroupIds", groupIds);

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),parameterMap, typeRowMapper);
    }
	
	/**
	 * Method for Retriving List Of Non-Financial Rule Details 
	 */
	@Override
    public List<NFScoreRuleDetail> getNFRulesByNFScoreGroup(List<Long> groupIds, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select GroupId, NFRuleId, NFRuleDesc, MaxScore ");
		selectSql.append(" From NFScoreRuleDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE GroupId In (:GroupIds) " );
		selectSql.append(" order By GroupId , NFRuleId ");
		
		Map<String, List<Long>> parameterMap=new HashMap<String, List<Long>>();
		parameterMap.put("GroupIds", groupIds);

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<NFScoreRuleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(NFScoreRuleDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),parameterMap, typeRowMapper);
    }
	
	/**
	 * Method for Retriving List Of Non-Financial Rule Details 
	 */
	@Override
    public List<NFScoreRuleDetail> getNFRulesByGroupId(long groupId, String categoryType, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select GroupId, NFRuleId, NFRuleDesc, MaxScore ");
		selectSql.append(" From NFScoreRuleDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE GroupId In (Select ScoringId from RMTScoringMetrics_View " );
		selectSql.append(" WHERE ScoreGroupId = " + groupId);
		selectSql.append(" AND CategoryType='" + categoryType );
		selectSql.append("') ");
		selectSql.append(" order By GroupId , NFRuleId ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<NFScoreRuleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(NFScoreRuleDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);
    }
	
	/**
	 * Method for Retriving List Of Non-Financial Rule Details 
	 */
	@Override
    public List<NFScoreRuleDetail> getNFRulesByGroupId(long groupId, String type) {
		logger.debug("Entering");
		
		NFScoreRuleDetail detail = new NFScoreRuleDetail();
		detail.setGroupId(groupId);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select NFRuleId, NFRuleDesc, MaxScore ");
		selectSql.append(" From NFScoreRuleDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE GroupId =:GroupId " );
		selectSql.append(" order By NFRuleId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<NFScoreRuleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(NFScoreRuleDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),beanParameters, typeRowMapper);
    }
	
	/**
	 * Method to fetch Rule details based on list of RuleCodes and module
	 * 
	 * @param ruleCode
	 * @param module
	 * @param type
	 */
	@Override
	public List<Rule> getRuleDetails(List<String> ruleCode, String module, String type) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RuleModule", module);
		if(ruleCode != null && !ruleCode.isEmpty()) {
			source.addValue("RuleCode", ruleCode);
		} else {
			source.addValue("RuleCode", null);
		}
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RuleModule, RuleCode, RuleCodeDesc ");
		selectSql.append(" From Rules");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE RuleModule =:RuleModule AND  RuleCode In(:RuleCode)" );

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
    }

	/**
	 * Method for getRule details based on ruleCode and module
	 * 
	 * @param ruleCode
	 * @param module
	 */
	@Override
	public Rule getRuleById(String ruleCode, String module, String type) {
		logger.debug("Entering");

		Rule rule = new Rule();
		rule.setRuleCode(ruleCode);
		rule.setRuleModule(module);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RuleModule, RuleCode, RuleCodeDesc ");
		selectSql.append(" From Rules");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE RuleModule =:RuleModule AND  RuleCode =:RuleCode" );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameter = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		try {
			rule = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameter, typeRowMapper);
		} catch(EmptyResultDataAccessException dae) {
			logger.warn("Exception: ", dae);
			rule = null;
		}
		logger.debug("Leaving");

		return rule;
	}

	/**
	 * Fetch Rule Details based on List of Rule codes , ruleModule and ruleEvent
	 * 
	 * @param ruleCode
	 * @param module
	 * @param type
	 */
	@Override
	public List<Rule> getRuleDetailList(List<String> ruleCodeList, String ruleModule, String ruleEvent) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RuleModule", ruleModule);
		source.addValue("RuleEvent", ruleEvent);
		if(ruleCodeList != null && !ruleCodeList.isEmpty()) {
			source.addValue("RuleCode", ruleCodeList);
		} else {
			source.addValue("RuleCode", null);
		}
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RuleId, RuleCode, RuleModule,RuleEvent, RuleCodeDesc, AllowDeviation, CalFeeModify, FeeToFinance, ");
		selectSql.append(" WaiverDecider, Waiver, WaiverPerc, SQLRule, ActualBlock,SeqOrder, ReturnType, DeviationType, GroupId,");
		selectSql.append(" Revolving, FixedOrVariableLimit, Active, Fields ");
		selectSql.append(" From Rules ");
		selectSql.append(" WHERE RuleModule = :RuleModule AND RuleEvent = :RuleEvent AND RuleCode In (:RuleCode)" );

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
    }

	@Override
	public List<String> getAEAmountCodesList(String event) {
		MapSqlParameterSource source = null;
		List<String> aeAmountCodesList = new ArrayList<String>();

		StringBuilder selectSql = new StringBuilder("Select AmountCode From BmtAmountCodes");
		selectSql.append(" Where AllowedEvent = :AllowedEvent");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("AllowedEvent", event);

		try {
			aeAmountCodesList = this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), source, String.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");

		return aeAmountCodesList;
	}
	
}
