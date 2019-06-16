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
 * FileName    		:  RuleDAOImpl.java                           							*
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-06-2011    														*
 *                                                                  						*
 * Modified Date    :  03-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 * 08-05-2019		Srinivasa Varma			 0.2		  Development Iteam 81              *  
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.rulefactory.BMTRBFldCriterias;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.RuleModule;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>Rule model</b> class.<br>
 */
public class RuleDAOImpl extends SequenceDao<Rule> implements RuleDAO {
	private static Logger logger = Logger.getLogger(RuleDAOImpl.class);

	public RuleDAOImpl() {
		super();
	}

	/**
	 * Get Rule by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Rule
	 */
	@Override
	public Rule getRuleByID(final String id, final String module, final String event, String type) {
		Rule rule = new Rule();
		rule.setRuleCode(id);
		rule.setRuleModule(module);
		rule.setRuleEvent(event);

		StringBuilder sql = getSelectQuery(type);
		sql.append(" Where RuleCode =:RuleCode AND RuleModule =:RuleModule AND RuleEvent =:RuleEvent");
		sql.append(" Order BY SeqOrder ");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private StringBuilder getSelectQuery(String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT RuleId, RuleCode, RuleModule, RuleEvent, RuleCodeDesc, AllowDeviation");
		sql.append(", CalFeeModify, FeeToFinance, WaiverDecider, Waiver, WaiverPerc, SQLRule, ActualBlock, SeqOrder");
		sql.append(", ReturnType, DeviationType, GroupId, Revolving, FixedOrVariableLimit, Active, Fields, FeeTypeID");
		if (type.contains("View")) {
			sql.append(", LovDescGroupName, FeeTypeCode, FeeTypeDesc");
		}
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	@Override
	public Rule getRuleByID(long ruleId, String type) {
		Rule rule = new Rule();
		rule.setRuleId(ruleId);

		StringBuilder sql = getSelectQuery(type);
		sql.append(" Where RuleId =:RuleId");
		sql.append(" Order BY SeqOrder ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		return rule;
	}

	/**
	 * Get Rule by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Rule
	 */
	@Override
	public String getAmountRule(final String id, final String module, final String event) {
		Rule rule = new Rule();
		rule.setRuleCode(id);
		rule.setRuleModule(module);
		rule.setRuleEvent(event);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT SQLRule From Rules ");
		sql.append(" Where RuleCode =:RuleCode AND RuleModule =:RuleModule AND RuleEvent =:RuleEvent");

		logger.trace("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return "";
	}

	/**
	 * Get Rule by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Rule
	 */
	@Override
	public List<Rule> getSubHeadRuleList(List<String> subHeadRuleList) {

		if (subHeadRuleList.isEmpty()) {
			return new ArrayList<Rule>();
		}

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RuleCode", subHeadRuleList);
		source.addValue("RuleModule", RuleConstants.MODULE_SUBHEAD);
		source.addValue("RuleEvent", RuleConstants.MODULE_SUBHEAD);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT RuleCode, SQLRule From Rules ");
		sql.append(" Where RuleCode IN(:RuleCode) AND RuleModule =:RuleModule AND RuleEvent =:RuleEvent");

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return new ArrayList<Rule>();
	}

	/**
	 * Get Rule by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Rule
	 */
	@Override
	public List<Rule> getRuleByModuleAndEvent(final String module, final String event, String type) {
		Rule rule = new Rule();
		rule.setRuleModule(module);
		rule.setRuleEvent(event);

		StringBuilder sql = getSelectQuery(type);
		sql.append(" Where RuleModule =:RuleModule AND RuleEvent =:RuleEvent");
		sql.append(" Order BY SeqOrder ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method Deletes the Record from the Rules or Rules_Temp. if Record not deleted then throws
	 * DataAccessException with error code 41004. delete Rules by key RuleCode
	 * 
	 * @param Rule
	 *            (rule)
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
	public void delete(Rule rule, String type) {
		int recordCount = 0;
		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where RuleCode =:RuleCode AND RuleModule =:RuleModule AND RuleEvent =:RuleEvent");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);

		try {
			recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

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
	 * @param Rules
	 *            (rule)
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
			rule.setId(getNextId("SeqRules"));
			logger.debug("get NextID:" + rule.getId());
		}
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into Rules");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RuleId,RuleCode, RuleModule,RuleEvent,RuleCodeDesc, WaiverDecider, Waiver, ");
		insertSql.append(
				" WaiverPerc, SQLRule, ActualBlock, SeqOrder,ReturnType,DeviationType,GroupId, AllowDeviation,CalFeeModify,FeeToFinance,  Revolving,FixedOrVariableLimit,Active");
		insertSql.append(" ,Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, Fields, feeTypeID)");
		insertSql.append(" Values(:RuleId ,:RuleCode, :RuleModule,:RuleEvent, :RuleCodeDesc, :WaiverDecider, ");
		insertSql.append(
				" :Waiver, :WaiverPerc, :SQLRule, :ActualBlock,:SeqOrder, :ReturnType,:DeviationType,:GroupId, :AllowDeviation, :CalFeeModify,:FeeToFinance,  :Revolving,:FixedOrVariableLimit,:Active");
		insertSql.append(" ,:Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId, :Fields, :feeTypeID)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return rule.getId();
	}

	/**
	 * This method updates the Record Rules or Rules_Temp. if Record not updated then throws DataAccessException with
	 * error 41003. update Rules by key RuleCode and Version
	 * 
	 * @param Rules
	 *            (rule)
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
	public void update(Rule rule, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update Rules");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set RuleCodeDesc = :RuleCodeDesc,");
		updateSql
				.append(" WaiverDecider=:WaiverDecider, Waiver =:Waiver, WaiverPerc=:WaiverPerc , SQLRule = :SQLRule,");
		updateSql.append(
				" AllowDeviation =:AllowDeviation, CalFeeModify = :CalFeeModify, ActualBlock = :ActualBlock ,SeqOrder=:SeqOrder,");
		updateSql.append(
				" ReturnType =:ReturnType,DeviationType =:DeviationType, GroupId=:GroupId,FeeToFinance=:FeeToFinance,  Revolving=:Revolving,FixedOrVariableLimit=:FixedOrVariableLimit,Active =:Active");
		updateSql.append(" ,Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId , Fields = :Fields, FeeTypeID = :FeeTypeID");
		updateSql.append(" Where RuleCode =:RuleCode AND RuleModule =:RuleModule AND RuleEvent =:RuleEvent ");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
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
		selectSql.append(" RBFldType ,RBFldLen , RBForCalFlds, RBForBldFlds, RBFldTableName , RBSTFlds ");
		selectSql.append(" FROM BMTRBFldDetails");
		selectSql.append(" WHERE RBModule = :RBModule AND RBEvent = :RBEvent");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<BMTRBFldDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BMTRBFldDetails.class);

		try {
			fieldList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
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
		List<BMTRBFldCriterias> fieldList = new ArrayList<BMTRBFldCriterias>();
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT RBFldType , RBSTFld , RBFldCriteriaNames , RBFldCriteriaValues ");
		sql.append(" FROM BMTRBFldCriterias");

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<BMTRBFldCriterias> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BMTRBFldCriterias.class);

		try {
			return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return fieldList;
	}

	/**
	 * Method for getting List of Modules
	 */
	@Override
	public List<RuleModule> getRuleModules(String module) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT RBMModule, RBMEvent, RBMFldName, RBMFldType ");
		sql.append(" from BMTRBFldMaster");
		if (StringUtils.isNotEmpty(module)) {
			sql.append(" where RBMModule='" + module + "'");
		}

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<RuleModule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RuleModule.class);
		return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), typeRowMapper);
	}

	/**
	 * Method for Fetching List of Rules using Key GroupId for Financial Scoring Metric Calculations
	 */
	@Override
	public List<Rule> getRulesByGroupId(long groupId, String ruleModule, String ruleEvent, String type) {
		Rule rule = new Rule();
		rule.setGroupId(groupId);
		rule.setRuleModule(ruleModule);
		rule.setRuleEvent(ruleEvent);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT  RuleCode, RuleCodeDesc,SQLRule ");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where RuleModule =:RuleModule AND RuleEvent =:RuleEvent AND GroupId=:GroupId ");
		sql.append(" Order BY SeqOrder");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for Fetching List of Rules using Key GroupId for Financial Scoring Metric Calculations
	 */
	@Override
	public List<Rule> getRulesByGroupIdList(long groupId, String categoryType, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select RuleCode, RuleCodeDesc, SqlRule, GroupId ");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE GroupId In (Select ScoringId from RMTScoringMetrics_View ");
		sql.append(" WHERE ScoreGroupId = " + groupId);
		sql.append(" AND CategoryType='" + categoryType);
		sql.append("') ");
		sql.append(" order By GroupId , SeqOrder ");

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), typeRowMapper);
	}

	/**
	 * Method for Fetching List of Rules using Key GroupId for Financial Scoring Metric Calculations
	 */
	@Override
	public List<Rule> getRulesByFinScoreGroup(List<Long> groupIds, String categoryType, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select RuleId, RuleCode, RuleCodeDesc, SqlRule, GroupId ");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE GroupId In (:GroupIds) ");
		sql.append(" order By GroupId , SeqOrder ");

		Map<String, List<Long>> parameterMap = new HashMap<String, List<Long>>();
		parameterMap.put("GroupIds", groupIds);

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		return this.jdbcTemplate.query(sql.toString(), parameterMap, typeRowMapper);
	}

	/**
	 * Method for Retriving List Of Non-Financial Rule Details
	 */
	@Override
	public List<NFScoreRuleDetail> getNFRulesByNFScoreGroup(List<Long> groupIds, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select GroupId, NFRuleId, NFRuleDesc, MaxScore ");
		sql.append(" From NFScoreRuleDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE GroupId In (:GroupIds) ");
		sql.append(" order By GroupId , NFRuleId ");

		Map<String, List<Long>> parameterMap = new HashMap<String, List<Long>>();
		parameterMap.put("GroupIds", groupIds);

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<NFScoreRuleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(NFScoreRuleDetail.class);
		return this.jdbcTemplate.query(sql.toString(), parameterMap, typeRowMapper);
	}

	/**
	 * Method for Retriving List Of Non-Financial Rule Details
	 */
	@Override
	public List<NFScoreRuleDetail> getNFRulesByGroupId(long groupId, String categoryType, String type) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select GroupId, NFRuleId, NFRuleDesc, MaxScore ");
		sql.append(" From NFScoreRuleDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE GroupId In (Select ScoringId from RMTScoringMetrics_View ");
		sql.append(" WHERE ScoreGroupId = " + groupId);
		sql.append(" AND CategoryType='" + categoryType);
		sql.append("') ");
		sql.append(" order By GroupId , NFRuleId ");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<NFScoreRuleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(NFScoreRuleDetail.class);
		return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), typeRowMapper);
	}

	/**
	 * Method for Retriving List Of Non-Financial Rule Details
	 */
	@Override
	public List<NFScoreRuleDetail> getNFRulesByGroupId(long groupId, String type) {
		NFScoreRuleDetail detail = new NFScoreRuleDetail();
		detail.setGroupId(groupId);

		StringBuilder sql = new StringBuilder();
		sql.append(" select NFRuleId, NFRuleDesc, MaxScore ");
		sql.append(" From NFScoreRuleDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE GroupId =:GroupId ");
		sql.append(" order By NFRuleId ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<NFScoreRuleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(NFScoreRuleDetail.class);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
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
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RuleModule", module);
		if (ruleCode != null && !ruleCode.isEmpty()) {
			source.addValue("RuleCode", ruleCode);
		} else {
			source.addValue("RuleCode", null);
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT RuleModule, RuleCode, RuleCodeDesc ");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE RuleModule =:RuleModule AND  RuleCode In(:RuleCode)");

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	/**
	 * Method for getRule details based on ruleCode and module
	 * 
	 * @param ruleCode
	 * @param module
	 */
	@Override
	public Rule getRuleById(String ruleCode, String module, String type) {
		Rule rule = new Rule();
		rule.setRuleCode(ruleCode);
		rule.setRuleModule(module);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT RuleModule, RuleCode, RuleCodeDesc ");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE RuleModule =:RuleModule AND  RuleCode =:RuleCode");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameter = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameter, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Literal.EXCEPTION, dae);
		}

		return null;
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
		StringBuilder sql = new StringBuilder();
		sql.append("select RuleId, RuleCode, RuleModule,RuleEvent, RuleCodeDesc, AllowDeviation, CalFeeModify");
		sql.append(", FeeToFinance, WaiverDecider, Waiver, WaiverPerc, SQLRule, ActualBlock,SeqOrder, ReturnType");
		sql.append(", DeviationType, GroupId, Revolving, FixedOrVariableLimit, Active, Fields");
		sql.append(" From Rules ");
		sql.append(" WHERE RuleModule = :RuleModule AND RuleEvent = :RuleEvent AND RuleCode In (:RuleCode)");

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RuleModule", ruleModule);
		source.addValue("RuleEvent", ruleEvent);

		if (ruleCodeList != null && !ruleCodeList.isEmpty()) {
			source.addValue("RuleCode", ruleCodeList);
		} else {
			source.addValue("RuleCode", null);
		}

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
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
			aeAmountCodesList = this.jdbcTemplate.queryForList(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return aeAmountCodesList;
	}

	@Override
	public List<Rule> getGSTRuleDetails(String ruleModule, String type) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RuleModule", ruleModule);

		StringBuilder sql = getSelectQuery(type);
		sql.append(" WHERE RuleModule = :RuleModule");

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	//### 08-05-2018 Start Development Iteam 81 

	/**
	 * Get Rule by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Rule
	 */
	@Override
	public boolean isFieldAssignedToRule(final String fieldName) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Fields1", fieldName);
		source.addValue("Fields2", "%," + fieldName + "%");
		source.addValue("Fields3", "%" + fieldName + ",%");

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(RuleId) ");
		sql.append(" From Rules");
		sql.append(" Where Fields=:Fields1 OR Fields LIKE :Fields2 OR Fields LIKE :Fields3");

		logger.trace("selectSql: " + sql.toString());

		Integer count = 0;
		try {
			count = jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		if (count > 0) {
			return true;
		}
		return false;
	}

	//### 08-05-2018 End Development Iteam 81

	@Override
	public List<Rule> fetchEligibilityRules(List<String> ruleCodes) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM rules ");
		sql.append("WHERE active=:Active and rulemodule=:RuleModule AND RuleCode in (:RuleCode)");

		Map<String, Object> map = new HashMap<>();
		map.put("Active", 1);
		map.put("RuleModule", "ELGRULE");
		map.put("RuleCode", ruleCodes);

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<Rule> ruleMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		return jdbcTemplate.query(sql.toString(), map, ruleMapper);
	}

}
