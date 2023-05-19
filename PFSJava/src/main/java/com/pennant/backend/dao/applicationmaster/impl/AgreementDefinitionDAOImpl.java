/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : AgreementDefinitionDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-11-2011 * *
 * Modified Date : 23-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.AgreementDefinitionDAO;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>AgreementDefinition model</b> class.<br>
 * 
 */
public class AgreementDefinitionDAOImpl extends SequenceDao<AgreementDefinition> implements AgreementDefinitionDAO {
	private static Logger logger = LogManager.getLogger(AgreementDefinitionDAOImpl.class);

	public AgreementDefinitionDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Agreement Definition details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return AgreementDefinition
	 */
	@Override
	public AgreementDefinition getAgreementDefinitionById(final long id, String type) {
		logger.debug(Literal.ENTERING);
		AgreementDefinition agreementDefinition = new AgreementDefinition();

		agreementDefinition.setId(id);

		StringBuilder selectSql = new StringBuilder("Select AggId, AggCode, AggName, ");
		selectSql.append(" AggDesc, AggReportName, AggReportPath, AggIsActive , Aggtype, AggImage, AgrRule, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, PwdProtected,");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleType, AllowMultiple,");
		selectSql.append(" ModuleName, DocType, AutoGeneration, AutoDownload");
		if (type.contains("View")) {
			selectSql.append(" , lovDescAgrRuleDesc");
		}
		selectSql.append(" From BMTAggrementDef");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AggId =:AggId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDefinition);
		RowMapper<AgreementDefinition> typeRowMapper = BeanPropertyRowMapper.newInstance(AgreementDefinition.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the Record Agreement Definition details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return AgreementDefinition
	 */
	@Override
	public AgreementDefinition getAgreementDefinitionByCode(final String aggCode, String type) {
		logger.debug(Literal.ENTERING);

		AgreementDefinition agreementDefinition = new AgreementDefinition();
		agreementDefinition.setAggCode(aggCode);

		StringBuilder selectSql = new StringBuilder("Select AggId, AggCode, AggName, AggDesc, ");
		selectSql.append(" AggReportName, AggReportPath, AggIsActive, Aggtype, AggImage, AgrRule, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, PwdProtected, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleType, AllowMultiple,");
		selectSql.append(" ModuleName,DocType, AutoGeneration, AutoDownload");
		if (type.contains("View")) {
			selectSql.append(" , lovDescAgrRuleDesc");
		}
		selectSql.append(" From BMTAggrementDef");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AggCode =:AggCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDefinition);
		RowMapper<AgreementDefinition> typeRowMapper = BeanPropertyRowMapper.newInstance(AgreementDefinition.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the BMTAggrementDef or BMTAggrementDef_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Agreement Definition by key AggCode
	 * 
	 * @param Agreement Definition (agreementDefinition)
	 * @param type      (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(AgreementDefinition agreementDefinition, TableType tableType) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder("Delete From BMTAggrementDef");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where AggCode =:AggCode");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDefinition);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into BMTAggrementDef or BMTAggrementDef_Temp.
	 *
	 * save Agreement Definition
	 * 
	 * @param Agreement Definition (agreementDefinition)
	 * @param type      (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(AgreementDefinition agreementDefinition, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (agreementDefinition.getId() == Long.MIN_VALUE) {
			agreementDefinition.setId(getNextValue("SeqBMTAggrementDef"));
			logger.debug("get NextValue:" + agreementDefinition.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into BMTAggrementDef");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (AggId, AggCode, AggName, AggDesc, AggReportName, AggReportPath, ");
		insertSql.append(" AggIsActive , Aggtype, AggImage, AgrRule, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleType, AllowMultiple,");
		insertSql.append(" ModuleName, DocType, AutoGeneration, AutoDownload, PwdProtected)");
		insertSql.append(" Values(:AggId, :AggCode, :AggName, :AggDesc, :AggReportName, ");
		insertSql.append(" :AggReportPath, :AggIsActive, :Aggtype, :AggImage, :AgrRule, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		insertSql.append(
				" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :ModuleType, :AllowMultiple, :ModuleName,");
		insertSql.append(" :DocType, :AutoGeneration, :AutoDownload, :PwdProtected)");

		logger.trace(Literal.SQL + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDefinition);
		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return String.valueOf(agreementDefinition.getId());
	}

	/**
	 * This method updates the Record BMTAggrementDef or BMTAggrementDef_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Agreement Definition by key AggCode and Version
	 * 
	 * @param Agreement Definition (agreementDefinition)
	 * @param type      (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(AgreementDefinition agreementDefinition, TableType tableType) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update BMTAggrementDef");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set AggCode = :AggCode, AggName = :AggName, AggDesc = :AggDesc, ");
		updateSql.append(" AggReportName = :AggReportName, AggReportPath = :AggReportPath, ");
		updateSql.append(" AggIsActive = :AggIsActive , Aggtype = :Aggtype, AggImage = :AggImage, AgrRule=:AgrRule, ");
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId,Doctype = :DocType, ");
		updateSql.append(" AutoGeneration = :AutoGeneration, AutoDownload = :AutoDownload, RecordType = :RecordType, ");
		updateSql.append(" WorkflowId = :WorkflowId, ModuleType = :ModuleType, AllowMultiple= :AllowMultiple,");
		updateSql.append(" ModuleName = :ModuleName, PwdProtected = :PwdProtected");
		updateSql.append(" Where AggId =:AggId");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDefinition);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(String aggCode, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "AggCode =:AggCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTAggrementDef", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTAggrementDef_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BMTAggrementDef_Temp", "BMTAggrementDef" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("AggCode", aggCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public int getAgreementDefinitionByRuleCode(String ruleCode, String type) {
		logger.debug("Entering");
		AgreementDefinition agreementDefinition = new AgreementDefinition();
		agreementDefinition.setAgrRule(ruleCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From BMTAggrementDef");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AgrRule =:AgrRule");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDefinition);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public AgreementDefinition getTemplate(long templateId) {
		StringBuilder sql = new StringBuilder("Select AggId, AggCode, AggName, AggDesc");
		sql.append(", AggReportName, AggReportPath, AggIsActive, Aggtype");
		sql.append(" From BMTAggrementDef");
		sql.append(" Where AggId = ? and AggisActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			AgreementDefinition item = new AgreementDefinition();

			item.setAggId(rs.getLong("AggId"));
			item.setAggCode(rs.getString("AggCode"));
			item.setAggName(rs.getString("AggName"));
			item.setAggDesc(rs.getString("AggDesc"));
			item.setAggReportName(rs.getString("AggReportName"));
			item.setAggReportPath(rs.getString("AggReportPath"));
			item.setAggIsActive(rs.getBoolean("AggIsActive"));
			item.setAggtype(rs.getString("Aggtype"));

			return item;
		}, templateId, 1);
	}

}