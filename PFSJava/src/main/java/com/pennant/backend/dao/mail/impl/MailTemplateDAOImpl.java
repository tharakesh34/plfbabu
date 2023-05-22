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
 * * FileName : MailTemplateDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-10-2012 * * Modified
 * Date : 04-10-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-10-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.mail.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>MailTemplate model</b> class.<br>
 * 
 */
public class MailTemplateDAOImpl extends SequenceDao<MailTemplate> implements MailTemplateDAO {
	private static Logger logger = LogManager.getLogger(MailTemplateDAOImpl.class);

	public MailTemplateDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Mail Template details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return MailTemplate
	 */
	@Override
	public MailTemplate getMailTemplateById(final long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TemplateId, TemplateFor, Module, TemplateCode, Event, TemplateDesc, SmsTemplate");
		sql.append(", SmsContent, EmailTemplate, EmailContent, EmailFormat, EmailSubject, EmailSendTo");
		sql.append(", TurnAroundTime, Repeat, Active, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from Templates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where TemplateId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				MailTemplate t = new MailTemplate();

				t.setTemplateId(rs.getLong("TemplateId"));
				t.setTemplateFor(rs.getString("TemplateFor"));
				t.setModule(rs.getString("Module"));
				t.setTemplateCode(rs.getString("TemplateCode"));
				t.setEvent(rs.getString("Event"));
				t.setTemplateDesc(rs.getString("TemplateDesc"));
				t.setSmsTemplate(rs.getBoolean("SmsTemplate"));
				t.setSmsContent(rs.getString("SmsContent"));
				t.setEmailTemplate(rs.getBoolean("EmailTemplate"));
				t.setEmailContent(rs.getBytes("EmailContent"));
				t.setEmailFormat(rs.getString("EmailFormat"));
				t.setEmailSubject(rs.getString("EmailSubject"));
				t.setEmailSendTo(rs.getString("EmailSendTo"));
				t.setTurnAroundTime(rs.getInt("TurnAroundTime"));
				t.setRepeat(rs.getBoolean("Repeat"));
				t.setActive(rs.getBoolean("Active"));
				t.setVersion(rs.getInt("Version"));
				t.setLastMntBy(rs.getLong("LastMntBy"));
				t.setLastMntOn(rs.getTimestamp("LastMntOn"));
				t.setRecordStatus(rs.getString("RecordStatus"));
				t.setRoleCode(rs.getString("RoleCode"));
				t.setNextRoleCode(rs.getString("NextRoleCode"));
				t.setTaskId(rs.getString("TaskId"));
				t.setNextTaskId(rs.getString("NextTaskId"));
				t.setRecordType(rs.getString("RecordType"));
				t.setWorkflowId(rs.getLong("WorkflowId"));

				return t;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record are not found in Templates{} for the TemplateId >> {}", type, id);
		}

		return null;
	}

	/**
	 * Fetch the Record Mail Template details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return MailTemplate
	 */
	@Override
	public MailTemplate getMailTemplateByCode(String code, String type) {
		logger.debug("Entering");
		MailTemplate mailTemplate = new MailTemplate();

		mailTemplate.setTemplateCode(code);

		StringBuilder selectSql = new StringBuilder(" Select TemplateId, TemplateFor, Module, TemplateCode,Event, ");
		selectSql.append(" TemplateDesc, SmsTemplate, SmsContent, EmailTemplate, EmailContent, EmailFormat, ");
		selectSql.append(" EmailSubject, EmailSendTo, TurnAroundTime, Repeat, Active, ");
		selectSql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From Templates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where TemplateCode = :TemplateCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
		RowMapper<MailTemplate> typeRowMapper = BeanPropertyRowMapper.newInstance(MailTemplate.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the Templates or Templates_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Mail Template by key TemplateCode
	 * 
	 * @param Mail Template (mailTemplate)
	 * @param type (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(MailTemplate mailTemplate, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From Templates");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where TemplateId =:TemplateId ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into Templates or Templates_Temp.
	 *
	 * save Mail Template
	 * 
	 * @param Mail Template (mailTemplate)
	 * @param type (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(MailTemplate mailTemplate, String type) {
		logger.debug("Entering");
		if (mailTemplate.getId() == Long.MIN_VALUE) {
			mailTemplate.setId(getNextValue("SeqMailTemplate"));
			logger.debug("get NextValue:" + mailTemplate.getId());
		}

		StringBuilder insertSql = new StringBuilder(" Insert Into Templates");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (TemplateId,  TemplateFor, Module, Event, TemplateCode, TemplateDesc, SmsTemplate, SmsContent, ");
		insertSql.append(
				" EmailTemplate, EmailContent, EmailFormat, EmailSubject, EmailSendTo, TurnAroundTime, Repeat, Active, ");
		insertSql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:TemplateId, :TemplateFor, :Module, :Event, :TemplateCode, :TemplateDesc, :SmsTemplate, :SmsContent, ");
		insertSql.append(
				" :EmailTemplate, :EmailContent, :EmailFormat, :EmailSubject, :EmailSendTo, :TurnAroundTime, :Repeat, :Active,");
		insertSql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return mailTemplate.getId();
	}

	/**
	 * This method updates the Record Templates or Templates_Temp. if Record not updated then throws DataAccessException
	 * with error 41004. update Mail Template by key TemplateCode and Version
	 * 
	 * @param Mail Template (mailTemplate)
	 * @param type (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(MailTemplate mailTemplate, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update Templates");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
				.append(" Set TemplateFor=:TemplateFor, Module=:Module, Event =:Event, TemplateCode = :TemplateCode, ");
		updateSql.append(" TemplateDesc = :TemplateDesc, SmsTemplate = :SmsTemplate, SmsContent = :SmsContent, ");
		updateSql.append(" EmailTemplate = :EmailTemplate, EmailContent = :EmailContent, EmailFormat = :EmailFormat, ");
		updateSql.append(" EmailSubject = :EmailSubject, EmailSendTo= :EmailSendTo,TurnAroundTime=:TurnAroundTime, ");
		updateSql.append(" Repeat = :Repeat, Active = :Active ,");
		updateSql.append(
				" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, ");
		updateSql.append(
				" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, ");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where TemplateId =:TemplateId ");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	// TODO: Need to be checked whether this is required or not
	public List<MailTemplate> getMailTemplates() {
		MailTemplate mailTemplate = new MailTemplate();
		StringBuilder selectSql = new StringBuilder(" Select TemplateId, TemplateFor, Module, Event, TemplateCode, ");
		selectSql.append(" TemplateDesc, SmsTemplate, SmsContent, EmailTemplate, EmailContent, EmailFormat, ");
		selectSql.append(" EmailSubject, EmailSendTo, TurnAroundTime, Repeat, Active, ");
		selectSql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From Templates");
		selectSql.append(" Where EmailTemplate = 1 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
		RowMapper<MailTemplate> typeRowMapper = BeanPropertyRowMapper.newInstance(MailTemplate.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public int getMailTemplateByCode(String templateCode, long id, String type) {
		MailTemplate mailTemplate = new MailTemplate();
		mailTemplate.setTemplateCode(templateCode);
		mailTemplate.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From Templates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where TemplateCode =:TemplateCode AND TemplateId !=:TemplateId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public MailTemplate getTemplateByCode(String templateCode) {
		String sql = "Select EmailTemplate, SmsTemplate, SmsContent, EmailContent, Active From Templates where TemplateCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, (rs, roNum) -> {
			MailTemplate mt = new MailTemplate();

			mt.setEmailTemplate(rs.getBoolean("EmailTemplate"));
			mt.setSmsTemplate(rs.getBoolean("SmsTemplate"));
			mt.setSmsContent(rs.getString("SmsContent"));
			mt.setEmailContent(rs.getBytes("EmailContent"));
			mt.setActive(rs.getBoolean("Active"));

			return mt;
		}, templateCode);
	}
}