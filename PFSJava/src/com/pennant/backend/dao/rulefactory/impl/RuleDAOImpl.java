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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rulefactory.BMTRBFldCriterias;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.RuleModule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Rule model</b> class.<br>
 */
public class RuleDAOImpl extends BasisNextidDaoImpl<Rule> implements RuleDAO {

	private static Logger logger = Logger.getLogger(RuleDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method will create a new object for Rule and set the Work flow
	 * details if available.
	 * 
	 * @return Rule
	 */
	@Override
	public Rule getRule() {
		logger.debug("Entering");

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Rule");
		Rule rule = new Rule();

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
	public Rule getNewRule() {
		logger.debug("Entering");
		Rule rule = getRule();
		rule.setNewRecord(true);
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
	public Rule getRuleByID(final String id,final String module,final String event, String type) {
		logger.debug("Entering");
		
		Rule rule = new Rule();
		rule.setRuleCode(id);
		rule.setRuleModule(module);
		rule.setRuleEvent(event);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RuleId, RuleCode, RuleModule,RuleEvent, RuleCodeDesc,");
		selectSql.append(" WaiverDecider , Waiver, WaiverPerc, SQLRule, ActualBlock,AddFeeCharges,SeqOrder, ReturnType, GroupId, ");
		if(type.contains("View")){
			selectSql.append(" lovDescRuleModuleName, lovDescGroupName , ");
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
			logger.error(e);
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
			logger.error(e);
			sqlRule = "";
		}finally{
			rule = null;
			beanParameters = null;
		}
		logger.debug("Leaving");
		return sqlRule;
	}
	
	/**
	 * Method for Fetching SubHead Rule Queries
	 */
	@Override
    public List<ValueLabel> getSubHeadAmountRule() {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RuleCode AS Label, SQLRule AS Value From Rules ");
		/*selectSql.append(" WHERE RuleCode IN(SELECT Distinct AccountSubHeadRule from RMTTransactionEntry where Account = '" );
		selectSql.append(PennantConstants.GLNPL);
		selectSql.append("')");*/

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ValueLabel> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ValueLabel.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);
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
		selectSql.append(" SELECT RuleId, RuleCode, RuleModule,RuleEvent, RuleCodeDesc,");
		selectSql.append(" WaiverDecider , Waiver, WaiverPerc, SQLRule, ActualBlock,AddFeeCharges,SeqOrder, ReturnType,");
		if(type.contains("View")){
			selectSql.append(" lovDescRuleModuleName,");
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
	 * This method initialize the Record.
	 * 
	 * @param rule (Rule)
	 * 
	 * @return void
	 */
	@Override
	public void initialize(Rule rule) {
		super.initialize(rule);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param rule (Rule)
	 * 
	 * @return void
	 */
	@Override
	public void refresh(Rule rule) {
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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails = getError("41004",rule.getRuleCode(), rule.getRuleModule(),
						rule.getRuleEvent(),rule.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", rule.getRuleCode(), rule.getRuleModule(),
					rule.getRuleEvent(), rule.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
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
		insertSql.append(" WaiverPerc, SQLRule, ActualBlock, AddFeeCharges,SeqOrder,ReturnType,GroupId,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:RuleId ,:RuleCode, :RuleModule,:RuleEvent, :RuleCodeDesc, :WaiverDecider, " );
		insertSql.append(" :Waiver, :WaiverPerc, :SQLRule, :ActualBlock,:AddFeeCharges,:SeqOrder, :ReturnType,:GroupId,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

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
	@SuppressWarnings("serial")
	@Override
	public void update(Rule rule, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update Rules");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set RuleCode = :RuleCode, RuleModule = :RuleModule, RuleEvent =:RuleEvent,RuleCodeDesc = :RuleCodeDesc," );
		updateSql.append(" WaiverDecider=:WaiverDecider, Waiver =:Waiver, WaiverPerc=:WaiverPerc , SQLRule = :SQLRule, " );
		updateSql.append(" ActualBlock = :ActualBlock ,AddFeeCharges =:AddFeeCharges,SeqOrder=:SeqOrder,ReturnType =:ReturnType, GroupId=:GroupId,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where RuleCode =:RuleCode AND RuleModule =:RuleModule AND RuleEvent =:RuleEvent ");
		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rule);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in update method");
			ErrorDetails errorDetails = getError("41003",rule.getRuleCode(), rule.getRuleModule(),
					rule.getRuleEvent(), rule.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method return the columns list of the tables sent as parameters
	 * 
	 * @param module (String)
	 * 
	 * @param event (String)
	 * 
	 * @return List
	 * 
	 * @throws EmptyResultDataAccessException
	 * 
	 */
	public List<BMTRBFldDetails> getFieldList(String module, String event) {
		logger.debug("Entering");
		
		List<BMTRBFldDetails> fieldList = new ArrayList<BMTRBFldDetails>();	
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RBModule , RBEvent , RBFldName , RBFldDesc," );
		selectSql.append(" RBFldType ,RBFldLen , RBForCalFlds, RBForBldFlds, RBFldTableName , RBSTFlds ");
		selectSql.append(" FROM BMTRBFldDetails");
		selectSql.append(" WHERE RBModule='"+module+"' AND RBEvent='"+event+"'" );

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<BMTRBFldDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BMTRBFldDetails.class);

		try {
			fieldList = this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);
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
			logger.error(e);
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
		if (module!=null && !module.equals("")) {
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
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String code,String module,String event, String userLanguage){
		String[][] parms= new String[2][3]; 
		parms[1][0] =code;
		parms[1][1] =module;
		parms[1][2] =event;
		
		parms[0][0] = PennantJavaUtil.getLabel("label_RuleCode")+":" +parms[1][0]+" "+
						PennantJavaUtil.getLabel("label_RuleModule")+":" +parms[1][1];
		parms[0][1] = PennantJavaUtil.getLabel("label_RuleEvent")+":" +parms[1][2];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

}
