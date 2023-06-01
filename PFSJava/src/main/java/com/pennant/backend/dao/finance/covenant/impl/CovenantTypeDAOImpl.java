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
 * * FileName : CovenantTypeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-02-2019 * * Modified
 * Date : 06-02-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-02-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.covenant.impl;

import java.util.List;

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

import com.pennant.backend.dao.finance.covenant.CovenantTypeDAO;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class CovenantTypeDAOImpl extends SequenceDao<CovenantType> implements CovenantTypeDAO {
	private static Logger logger = LogManager.getLogger(CovenantTypeDAOImpl.class);

	public CovenantTypeDAOImpl() {
		super();
	}

	@Override
	public CovenantType getCovenantType(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select Id, Code, Description, CovenantType, Category, DocType");
		sql.append(", AllowPostPonement, MaxAllowedDays, AllowedPaymentModes");
		sql.append(", AlertsRequired, Frequency, GraceDays, AlertDays, AlertType");
		sql.append(", AlertToRoles, UserTemplate, CustomerTemplate");

		if (type.contains("View")) {
			sql.append(", DocTypeName, UserTemplateName, CustomerTemplateName, userTemplateCode, customerTemplateCode");
		}

		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From COVENANT_TYPES");
		sql.append(type);
		sql.append(" Where id = :id");

		CovenantType covenantType = new CovenantType();
		covenantType.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(covenantType);
		RowMapper<CovenantType> rowMapper = BeanPropertyRowMapper.newInstance(CovenantType.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return covenantType;
		}
	}

	@Override
	public String save(CovenantType covenantType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("insert into COVENANT_TYPES");
		sql.append(tableType.getSuffix());
		sql.append("(Id, Code, Description, CovenantType, Category, DocType");
		sql.append(", AllowPostPonement, MaxAllowedDays, AllowedPaymentModes");
		sql.append(", AlertsRequired, Frequency, GraceDays, AlertDays, AlertType");
		sql.append(", AlertToRoles, UserTemplate, CustomerTemplate");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append("(:Id, :Code, :Description, :CovenantType, :Category, :DocType");
		sql.append(", :AllowPostPonement, :MaxAllowedDays, :AllowedPaymentModes");
		sql.append(", :AlertsRequired, :Frequency, :GraceDays, :AlertDays, :AlertType");
		sql.append(", :AlertToRoles, :UserTemplate, :CustomerTemplate");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (covenantType.getId() == Long.MIN_VALUE) {
			covenantType.setId(getNextValue("SEQCOVENANT_TYPES"));
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(covenantType);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(covenantType.getId());
	}

	@Override
	public void update(CovenantType covenantType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update COVENANT_TYPES");
		sql.append(tableType.getSuffix());

		sql.append(" set Description = :Description, CovenantType = :CovenantType, Category = :Category");
		sql.append(", DocType = :DocType, AllowPostPonement = :AllowPostPonement, MaxAllowedDays = :MaxAllowedDays");
		sql.append(", AllowedPaymentModes = :AllowedPaymentModes, AlertsRequired = :AlertsRequired");
		sql.append(", GraceDays = :GraceDays, AlertDays = :AlertDays, AlertType = :AlertType");
		sql.append(", Frequency = :Frequency, AlertToRoles = :AlertToRoles");
		sql.append(", UserTemplate = :UserTemplate, CustomerTemplate = :CustomerTemplate");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where Id = :Id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(covenantType);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(CovenantType covenantType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from COVENANT_TYPES");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(covenantType);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(CovenantType covenantType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "Code = :Code and CovenantType = :CovenantType and Category = :Category";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("COVENANT_TYPES", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("COVENANT_TYPES_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "COVENANT_TYPES_Temp", "COVENANT_TYPES" }, whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Code", covenantType.getCode());
		paramSource.addValue("CovenantType", covenantType.getCovenantType());
		paramSource.addValue("Category", covenantType.getCategory());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public CovenantType getCovenantTypeId(String code, String category, String type) {
		logger.debug(Literal.ENTERING);

		CovenantType covenant = new CovenantType();
		covenant.setCode(code);
		covenant.setCategory(category);

		StringBuilder sql = new StringBuilder();
		sql.append(
				"Select id,code,description,category,graceDays,alertDays,alertType,alertToRoles,frequency,covenantType, ");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		sql.append(" From COVENANT_TYPES");
		sql.append(type);
		sql.append(" Where code = :code and category=:category ");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(covenant);
		RowMapper<CovenantType> typeRowMapper = BeanPropertyRowMapper.newInstance(CovenantType.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<CovenantType> getCvntTypesByCatgy(String categoryName, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Code, Description, CovenantType, Category, DocType");
		sql.append(", AllowPostPonement, MaxAllowedDays, AllowedPaymentModes");
		sql.append(", AlertsRequired, Frequency, GraceDays, AlertDays, AlertType");
		sql.append(", AlertToRoles, UserTemplate, CustomerTemplate");

		if (type.contains("View")) {
			sql.append(", DocTypeName, UserTemplateName, CustomerTemplateName, UserTemplateCode, CustomerTemplateCode");
		}

		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From COVENANT_TYPES");
		sql.append(type);
		sql.append(" Where Category = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> ps.setString(1, categoryName), (rs, i) -> {
			CovenantType ct = new CovenantType();

			ct.setId(rs.getLong("Id"));
			ct.setCode(rs.getString("Code"));
			ct.setDescription(rs.getString("Description"));
			ct.setCovenantType(rs.getString("CovenantType"));
			ct.setCategory(rs.getString("Category"));
			ct.setDocType(rs.getString("DocType"));
			ct.setAllowPostPonement(rs.getBoolean("AllowPostPonement"));
			ct.setMaxAllowedDays(rs.getInt("MaxAllowedDays"));
			ct.setAllowedPaymentModes(rs.getString("AllowedPaymentModes"));
			ct.setAlertsRequired(rs.getBoolean("AlertsRequired"));
			ct.setFrequency(rs.getString("Frequency"));
			ct.setGraceDays(rs.getInt("GraceDays"));
			ct.setAlertDays(rs.getInt("AlertDays"));
			ct.setAlertType(rs.getString("AlertType"));
			ct.setAlertToRoles(rs.getString("AlertToRoles"));
			ct.setUserTemplate(JdbcUtil.getLong(rs.getObject("UserTemplate")));
			ct.setCustomerTemplate(JdbcUtil.getLong(rs.getObject("CustomerTemplate")));

			if (type.contains("View")) {
				ct.setDocTypeName(rs.getString("DocTypeName"));
				ct.setUserTemplateName(rs.getString("UserTemplateName"));
				ct.setCustomerTemplateName(rs.getString("CustomerTemplateName"));
				ct.setUserTemplateCode(rs.getString("UserTemplateCode"));
				ct.setCustomerTemplateCode(rs.getString("CustomerTemplateCode"));
			}

			ct.setVersion(rs.getInt("Version"));
			ct.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ct.setLastMntBy(rs.getLong("LastMntBy"));
			ct.setRecordStatus(rs.getString("RecordStatus"));
			ct.setRoleCode(rs.getString("RoleCode"));
			ct.setNextRoleCode(rs.getString("NextRoleCode"));
			ct.setTaskId(rs.getString("TaskId"));
			ct.setNextTaskId(rs.getString("NextTaskId"));
			ct.setRecordType(rs.getString("RecordType"));
			ct.setWorkflowId(rs.getLong("WorkflowId"));

			return ct;
		});
	}

	@Override
	public List<String> getRules() {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select rolecd from  operation_roles_view");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		return this.jdbcTemplate.queryForList(selectSql.toString(), source, String.class);
	}

}
