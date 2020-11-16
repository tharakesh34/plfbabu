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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private static Logger logger = LogManager.getLogger(RuleDAOImpl.class);

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
		logger.debug(Literal.ENTERING);

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
			logger.warn("Rule not exist for the specified Rule Code {}, Module {} and Event {}", id, module, type);
		}

		return null;
	}

	private StringBuilder getSelectQuery(String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT RuleId, RuleCode, RuleModule, RuleEvent, RuleCodeDesc, AllowDeviation");
		sql.append(
				", CalFeeModify, FeeToFinance, WaiverDecider, Waiver, WaiverPerc, SQLRule, SPLRule, ActualBlock, SeqOrder");
		sql.append(", ReturnType, DeviationType, GroupId, Revolving, FixedOrVariableLimit, Active, Fields, FeeTypeID");
		if (type.contains("View")) {
			sql.append(", LovDescGroupName, FeeTypeCode, FeeTypeDesc");
		}
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));

		logger.debug(Literal.LEAVING);
		return sql;
	}

	@Override
	public Rule getActiveRuleByID(final String id, final String module, final String event, String type,
			boolean active) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" RuleId, RuleCode, RuleModule, RuleEvent, RuleCodeDesc, AllowDeviation");
		sql.append(", CalFeeModify, FeeToFinance, WaiverDecider, Waiver, WaiverPerc, SQLRule");
		sql.append(", ActualBlock, SeqOrder, ReturnType, DeviationType, GroupId, Revolving");
		sql.append(", FixedOrVariableLimit, Active,  Fields, FeeTypeID");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescGroupName , FeeTypeCode, FeeTypeDesc");
		}

		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where RuleCode = ? and RuleModule = ? and RuleEvent =? and Active = ?");
		sql.append(" Order BY SeqOrder");

		try {
			return jdbcOperations.queryForObject(sql.toString(), new Object[] { id, module, event, active },
					new RowMapper<Rule>() {

						@Override
						public Rule mapRow(ResultSet rs, int i) throws SQLException {
							Rule rule = new Rule();

							rule.setRuleId(rs.getLong("RuleId"));
							rule.setRuleCode(rs.getString("RuleCode"));
							rule.setRuleModule(rs.getString("RuleModule"));
							rule.setRuleEvent(rs.getString("RuleEvent"));
							rule.setRuleCodeDesc(rs.getString("RuleCodeDesc"));
							rule.setAllowDeviation(rs.getBoolean("AllowDeviation"));
							rule.setCalFeeModify(rs.getBoolean("CalFeeModify"));
							rule.setFeeToFinance(rs.getString("FeeToFinance"));
							rule.setWaiverDecider(rs.getString("WaiverDecider"));
							rule.setWaiver(rs.getBoolean("Waiver"));
							rule.setWaiverPerc(rs.getBigDecimal("WaiverPerc"));
							rule.setSQLRule(rs.getString("SQLRule"));
							rule.setActualBlock(rs.getString("ActualBlock"));
							rule.setSeqOrder(rs.getInt("SeqOrder"));
							rule.setReturnType(rs.getString("ReturnType"));
							rule.setDeviationType(rs.getString("DeviationType"));
							rule.setGroupId(rs.getLong("GroupId"));
							rule.setRevolving(rs.getBoolean("Revolving"));
							rule.setFixedOrVariableLimit(rs.getString("FixedOrVariableLimit"));
							rule.setActive(rs.getBoolean("Active"));
							rule.setFields(rs.getString("Fields"));
							rule.setFeeTypeID(rs.getLong("FeeTypeID"));

							if (StringUtils.trimToEmpty(type).contains("View")) {
								rule.setLovDescGroupName(rs.getString("LovDescGroupName"));
								rule.setFeeTypeCode(rs.getString("FeeTypeCode"));
								rule.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
							}

							rule.setVersion(rs.getInt("Version"));
							rule.setLastMntBy(rs.getLong("LastMntBy"));
							rule.setLastMntOn(rs.getTimestamp("LastMntOn"));
							rule.setRecordStatus(rs.getString("RecordStatus"));
							rule.setRoleCode(rs.getString("RoleCode"));
							rule.setNextRoleCode(rs.getString("NextRoleCode"));
							rule.setTaskId(rs.getString("TaskId"));
							rule.setNextTaskId(rs.getString("NextTaskId"));
							rule.setRecordType(rs.getString("RecordType"));
							rule.setWorkflowId(rs.getLong("WorkflowId"));

							return rule;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public Rule getRuleByID(long ruleId, String type) {
		logger.debug(Literal.ENTERING);

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
			logger.warn("Rule not exist for the specified Rule Id {}, Module {} and Event {}", ruleId);
			rule = null;
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
		logger.debug(Literal.ENTERING);

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
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

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
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		Rule rule = new Rule();
		rule.setRuleModule(module);
		rule.setRuleEvent(event);

		StringBuilder sql = getSelectQuery(type);
		sql.append(" Where RuleModule =:RuleModule AND RuleEvent =:RuleEvent");
		sql.append(" Order BY SeqOrder ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.ENTERING);

		if (rule.getId() == Long.MIN_VALUE) {
			rule.setId(getNextId("SeqRules"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (RuleId,RuleCode, RuleModule, RuleEvent, RuleCodeDesc, WaiverDecider, Waiver");
		sql.append(", WaiverPerc, SQLRule,SPLRule, ActualBlock, SeqOrder, ReturnType, DeviationType, GroupId");
		sql.append(", AllowDeviation, CalFeeModify, FeeToFinance, Revolving, FixedOrVariableLimit, Active");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, Fields, feeTypeID)");
		sql.append(" Values(:RuleId,:RuleCode, :RuleModule, :RuleEvent, :RuleCodeDesc, :WaiverDecider, :Waiver");
		sql.append(", :WaiverPerc, :SQLRule, :SPLRule, :ActualBlock,:SeqOrder, :ReturnType, :DeviationType, :GroupId");
		sql.append(", :AllowDeviation, :CalFeeModify, :FeeToFinance, :Revolving, :FixedOrVariableLimit, :Active");
		sql.append(", :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId, :Fields, :feeTypeID)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);

		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder sql = new StringBuilder();

		sql.append("Update Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set RuleCodeDesc = :RuleCodeDesc,");
		sql.append(
				" WaiverDecider = :WaiverDecider, Waiver = :Waiver, WaiverPerc = :WaiverPerc, SQLRule = :SQLRule, sPLRule = :sPLRule");
		sql.append(", AllowDeviation =:AllowDeviation, CalFeeModify = :CalFeeModify, ActualBlock = :ActualBlock");
		sql.append(", SeqOrder = :SeqOrder, ReturnType = :ReturnType, DeviationType = :DeviationType");
		sql.append(", GroupId = :GroupId, FeeToFinance = :FeeToFinance,  Revolving = :Revolving");
		sql.append(", FixedOrVariableLimit = :FixedOrVariableLimit, Active = :Active");
		sql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus = :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType");
		sql.append(", WorkflowId = :WorkflowId, Fields = :Fields, FeeTypeID = :FeeTypeID");
		sql.append(" Where RuleCode = :RuleCode and RuleModule =:RuleModule and RuleEvent =:RuleEvent");
		if (!type.endsWith("_Temp")) {
			sql.append("  AND Version= :Version-1");
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateRuleByID(Rule rule, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		String updateSql = "update Rules set Active= :Active  , RecordType = :RecordType, "
				+ "  lastMntBy= :lastMntBy ,lastMntOn= :lastMntOn  " + "where RuleId= :RuleId";

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
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
	@Override
	public List<BMTRBFldDetails> getFieldList(String module, String event) {
		logger.debug(Literal.ENTERING);

		// As the event is part of primary key and storing space, passing " "
		if (StringUtils.isEmpty(event)) {
			event = " ";
		}

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT RBModule, RBEvent, RBFldName, RBFldDesc");
		sql.append(", RBFldType, RBFldLen, RBForCalFlds, RBForBldFlds, RBFldTableName, RBSTFlds");
		sql.append(" FROM BMTRBFldDetails");
		sql.append(" WHERE RBModule = :RBModule AND RBEvent = :RBEvent");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RBModule", module);
		source.addValue("RBEvent", event);

		RowMapper<BMTRBFldDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BMTRBFldDetails.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Field details not available for the specified Module {} and Event {}", module, event);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * This method return the Operators list of the tables sent as parameters
	 * 
	 * @return List
	 */
	@Override
	public List<BMTRBFldCriterias> getOperatorsList() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT RBFldType , RBSTFld , RBFldCriteriaNames , RBFldCriteriaValues ");
		sql.append(" FROM BMTRBFldCriterias");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<BMTRBFldCriterias> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BMTRBFldCriterias.class);

		try {
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Field details not available.");
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * Method for getting List of Modules
	 */
	@Override
	public List<RuleModule> getRuleModules(String module) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT RBMModule, RBMEvent, RBMFldName, RBMFldType");
		sql.append(" from BMTRBFldMaster");
		if (StringUtils.isNotEmpty(module)) {
			sql.append(" where RBMModule = :RBMModule");
		}

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RBMModule", module);

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<RuleModule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RuleModule.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	/**
	 * Method for Fetching List of Rules using Key GroupId for Financial Scoring Metric Calculations
	 */
	@Override
	public List<Rule> getRulesByGroupId(long groupId, String ruleModule, String ruleEvent, String type) {
		logger.debug(Literal.ENTERING);

		Rule rule = new Rule();
		rule.setGroupId(groupId);
		rule.setRuleModule(ruleModule);
		rule.setRuleEvent(ruleEvent);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT  RuleCode, RuleCodeDesc,SQLRule");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where RuleModule =:RuleModule AND RuleEvent =:RuleEvent AND GroupId=:GroupId ");
		sql.append(" Order BY SeqOrder");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for Fetching List of Rules using Key GroupId for Financial Scoring Metric Calculations
	 */
	@Override
	public List<Rule> getRulesByGroupIdList(long groupId, String categoryType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select RuleCode, RuleCodeDesc, SqlRule, GroupId");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE GroupId In (Select ScoringId from RMTScoringMetrics_View");
		sql.append(" WHERE ScoreGroupId = :ScoreGroupId");
		sql.append(" AND CategoryType= :CategoryType");
		sql.append(")");
		sql.append(" order By GroupId , SeqOrder");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ScoreGroupId", groupId);
		source.addValue("CategoryType", categoryType);

		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	/**
	 * Method for Fetching List of Rules using Key GroupId for Financial Scoring Metric Calculations
	 */
	@Override
	public List<Rule> getRulesByFinScoreGroup(List<Long> groupIds, String categoryType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select RuleId, RuleCode, RuleCodeDesc, SqlRule, GroupId");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE GroupId In (:GroupIds)");
		sql.append(" order By GroupId, SeqOrder ");

		Map<String, List<Long>> parameterMap = new HashMap<String, List<Long>>();
		parameterMap.put("GroupIds", groupIds);

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), parameterMap, typeRowMapper);
	}

	/**
	 * Method for Retriving List Of Non-Financial Rule Details
	 */
	@Override
	public List<NFScoreRuleDetail> getNFRulesByNFScoreGroup(List<Long> groupIds, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select GroupId, NFRuleId, NFRuleDesc, MaxScore");
		sql.append(" From NFScoreRuleDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE GroupId In (:GroupIds)");
		sql.append(" order By GroupId, NFRuleId");

		Map<String, List<Long>> parameterMap = new HashMap<String, List<Long>>();
		parameterMap.put("GroupIds", groupIds);

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<NFScoreRuleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(NFScoreRuleDetail.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), parameterMap, typeRowMapper);
	}

	/**
	 * Method for Retriving List Of Non-Financial Rule Details
	 */
	@Override
	public List<NFScoreRuleDetail> getNFRulesByGroupId(long groupId, String categoryType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select GroupId, NFRuleId, NFRuleDesc, MaxScore");
		sql.append(" From NFScoreRuleDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE GroupId In (Select ScoringId from RMTScoringMetrics_View");
		sql.append(" WHERE ScoreGroupId = :ScoreGroupId");
		sql.append(" AND CategoryType= :CategoryType");
		sql.append(")");
		sql.append(" order By GroupId , NFRuleId ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ScoreGroupId", groupId);
		source.addValue("CategoryType", categoryType);

		RowMapper<NFScoreRuleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(NFScoreRuleDetail.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	/**
	 * Method for Retriving List Of Non-Financial Rule Details
	 */
	@Override
	public List<NFScoreRuleDetail> getNFRulesByGroupId(long groupId, String type) {
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT RuleModule, RuleCode, RuleCodeDesc");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE RuleModule =:RuleModule AND  RuleCode In(:RuleCode)");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RuleModule", module);
		if (ruleCode != null && !ruleCode.isEmpty()) {
			source.addValue("RuleCode", ruleCode);
		} else {
			source.addValue("RuleCode", null);
		}

		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		Rule rule = new Rule();
		rule.setRuleCode(ruleCode);
		rule.setRuleModule(module);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * ");
		sql.append(" From Rules");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE RuleModule =:RuleModule AND  RuleCode =:RuleCode");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameter = new BeanPropertySqlParameterSource(rule);
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		try {
			logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public List<String> getAEAmountCodesList(String event) {
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.LEAVING);
		return aeAmountCodesList;
	}

	@Override
	public List<Rule> getGSTRuleDetails(String ruleModule, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RuleModule", ruleModule);

		StringBuilder sql = getSelectQuery(type);
		sql.append(" WHERE RuleModule = :RuleModule");

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	// ### 08-05-2018 Start Development Iteam 81

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
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Fields1", fieldName);
		source.addValue("Fields2", "%," + fieldName + "%");
		source.addValue("Fields3", "%" + fieldName + ",%");

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(RuleId) ");
		sql.append(" From Rules");
		sql.append(" Where Fields = :Fields1 OR Fields LIKE :Fields2 OR Fields LIKE :Fields3");

		logger.trace("selectSql: " + sql.toString());

		Integer count = 0;
		try {
			count = jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		if (count > 0) {
			logger.debug(Literal.ENTERING);
			return true;
		}
		return false;
	}

	// ### 08-05-2018 End Development Iteam 81

	@Override
	public List<Rule> fetchEligibilityRules(List<String> ruleCodes) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM rules");
		sql.append(" WHERE Active = :Active and Rulemodule = :RuleModule and RuleCode in (:RuleCode)");

		logger.trace(Literal.SQL + sql.toString());

		Map<String, Object> map = new HashMap<>();
		map.put("Active", 1);
		map.put("RuleModule", "ELGRULE");
		map.put("RuleCode", ruleCodes);

		RowMapper<Rule> ruleMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
		logger.debug(Literal.LEAVING);
		return jdbcTemplate.query(sql.toString(), map, ruleMapper);
	}

	@Override
	public String getRuleCodeDesc(long ruleId, String module, String ruleCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT RuleCodeDesc");
		sql.append(" From Rules");

		StringBuilder whereClause = new StringBuilder();

		MapSqlParameterSource params = new MapSqlParameterSource();

		if (StringUtils.isNotBlank(String.valueOf(ruleId))) {
			appenFilter(whereClause, ruleId, "RuleId", "RuleId", params);
		}

		if (StringUtils.isNotBlank(module)) {
			appenFilter(whereClause, module, "RuleModule", "RuleModule", params);
		}

		if (StringUtils.isNotBlank(ruleCode)) {
			appenFilter(whereClause, ruleCode, "RuleCode", "RuleCode", params);
		}

		sql.append(whereClause.toString());

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), params, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Rule Detail not avilable for specified Request");
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	private void appenWhere(StringBuilder whereClause, Object fieldValue, String column, String parameterName,
			MapSqlParameterSource parameterSource) {
		logger.debug(Literal.ENTERING);

		if (whereClause.length() > 0) {
			whereClause.append(" and");
		} else {
			whereClause.append(" where");
		}

		whereClause.append(" ").append(column).append(" = :").append(parameterName);
		parameterSource.addValue(parameterName, fieldValue);
		logger.debug(Literal.LEAVING);
	}

	private void appenFilter(StringBuilder whereClause, Object fieldValue, String column, String parameterName,
			MapSqlParameterSource parameterSource) {
		logger.debug(Literal.ENTERING);
		if (fieldValue != null) {
			appenWhere(whereClause, fieldValue, column, parameterName, parameterSource);
		}

		parameterSource.addValue(parameterName, fieldValue);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public int getRuleCodeCount(String ruleCode, String ruleEvent, String ruleModule) {
		logger.debug("Entering");

		Rule rule = new Rule();
		rule.setRuleCode(ruleCode);
		rule.setRuleEvent(ruleEvent);
		rule.setRuleModule(ruleModule);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From Rules");
		selectSql.append(" Where RuleCode =:RuleCode ANd RuleEvent =:RuleEvent And RuleModule = :RuleModule");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}

}
