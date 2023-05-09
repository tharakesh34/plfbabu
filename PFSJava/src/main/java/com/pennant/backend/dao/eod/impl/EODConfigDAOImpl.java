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
 * * FileName : EODConfigDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-05-2017 * * Modified
 * Date : 24-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.eod.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>EODConfig</code> with set of CRUD operations.
 */
public class EODConfigDAOImpl extends SequenceDao<EODConfig> implements EODConfigDAO {
	private static Logger logger = LogManager.getLogger(EODConfigDAOImpl.class);

	public EODConfigDAOImpl() {
		super();
	}

	@Override
	public EODConfig getEODConfig(long eodConfigId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where EodConfigId = ?");

		logger.trace(Literal.SQL + sql.toString());

		EODConfigRowMapper rowMapper = new EODConfigRowMapper();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, eodConfigId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String save(EODConfig eODConfig, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (eODConfig.getEodConfigId() <= 0) {
			eODConfig.setEodConfigId(getNextValue("SeqEodConfig"));
		}

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" EODConfig");
		sql.append(tableType.getSuffix());
		sql.append(" (EodConfigId, ExtMnthRequired, MnthExtTo, Active, InExtMnth, PrvExtMnth, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", AutoEodRequired, EODStartJobFrequency, EnableAutoEod, EODAutoDisable, SendEmailRequired");
		sql.append(", SMTPHost, SMTPPort, SMTPAutenticationRequired, SMTPUserName, SMTPPwd, EncryptionType");
		sql.append(", FromEmailAddress, FromName, ToEmailAddress, CCEmailAddress, EmailNotifReqrd, PublishNotifReqrd");
		sql.append(", ReminderFrequency, DelayNotifyReq, DelayFrequency");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;

					ps.setLong(index++, eODConfig.getEodConfigId());
					ps.setBoolean(index++, eODConfig.isExtMnthRequired());
					ps.setDate(index++, JdbcUtil.getDate(eODConfig.getMnthExtTo()));
					ps.setBoolean(index++, eODConfig.isActive());
					ps.setBoolean(index++, eODConfig.isInExtMnth());
					ps.setDate(index++, JdbcUtil.getDate(eODConfig.getPrvExtMnth()));
					ps.setInt(index++, eODConfig.getVersion());
					ps.setLong(index++, eODConfig.getLastMntBy());
					ps.setTimestamp(index++, eODConfig.getLastMntOn());
					ps.setString(index++, eODConfig.getRecordStatus());
					ps.setString(index++, eODConfig.getRoleCode());
					ps.setString(index++, eODConfig.getNextRoleCode());
					ps.setString(index++, eODConfig.getTaskId());
					ps.setString(index++, eODConfig.getNextTaskId());
					ps.setString(index++, eODConfig.getRecordType());
					ps.setLong(index++, eODConfig.getWorkflowId());
					ps.setBoolean(index++, eODConfig.isAutoEodRequired());
					ps.setString(index++, eODConfig.getEODStartJobFrequency());
					ps.setBoolean(index++, eODConfig.isEnableAutoEod());
					ps.setBoolean(index++, eODConfig.isEODAutoDisable());
					ps.setBoolean(index++, eODConfig.isSendEmailRequired());
					ps.setString(index++, eODConfig.getSMTPHost());
					ps.setString(index++, eODConfig.getSMTPPort());
					ps.setBoolean(index++, eODConfig.isSMTPAutenticationRequired());
					ps.setString(index++, eODConfig.getSMTPUserName());
					ps.setString(index++, eODConfig.getSMTPPwd());
					ps.setString(index++, eODConfig.getEncryptionType());
					ps.setString(index++, eODConfig.getFromEmailAddress());
					ps.setString(index++, eODConfig.getFromName());
					ps.setString(index++, eODConfig.getToEmailAddress());
					ps.setString(index++, eODConfig.getCCEmailAddress());
					ps.setBoolean(index++, eODConfig.isEmailNotifReqrd());
					ps.setBoolean(index++, eODConfig.isPublishNotifReqrd());
					ps.setString(index++, eODConfig.getReminderFrequency());
					ps.setBoolean(index++, eODConfig.isDelayNotifyReq());
					ps.setString(index, eODConfig.getDelayFrequency());
				}
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(eODConfig.getEodConfigId());
	}

	@Override
	public void update(EODConfig eODConfig, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update");
		sql.append(" EODConfig");
		sql.append(tableType.getSuffix());
		sql.append(" set ExtMnthRequired = ?, MnthExtTo = ?, Active = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?");
		sql.append(
				", WorkflowId = ?, AutoEodRequired = ?, EODStartJobFrequency = ?, EnableAutoEod = ?, EODAutoDisable = ?");
		sql.append(", SendEmailRequired = ?, SMTPHost = ?, SMTPPort = ?, SMTPPwd = ?, SMTPAutenticationRequired = ?");
		sql.append(", SMTPUserName = ?, EncryptionType = ?, FromEmailAddress = ?, FromName = ?, ToEmailAddress = ?");
		sql.append(", CCEmailAddress = ?, EmailNotifReqrd = ?, PublishNotifReqrd = ?, ReminderFrequency = ?");
		sql.append(", DelayNotifyReq = ?, DelayFrequency = ?");
		sql.append(" Where EodConfigId = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.trace(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setBoolean(index++, eODConfig.isExtMnthRequired());
				ps.setDate(index++, JdbcUtil.getDate(eODConfig.getMnthExtTo()));
				ps.setBoolean(index++, eODConfig.isActive());
				ps.setTimestamp(index++, eODConfig.getLastMntOn());
				ps.setString(index++, eODConfig.getRecordStatus());
				ps.setString(index++, eODConfig.getRoleCode());
				ps.setString(index++, eODConfig.getNextRoleCode());
				ps.setString(index++, eODConfig.getTaskId());
				ps.setString(index++, eODConfig.getNextTaskId());
				ps.setString(index++, eODConfig.getRecordType());
				ps.setLong(index++, eODConfig.getWorkflowId());
				ps.setBoolean(index++, eODConfig.isAutoEodRequired());
				ps.setString(index++, eODConfig.getEODStartJobFrequency());
				ps.setBoolean(index++, eODConfig.isEnableAutoEod());
				ps.setBoolean(index++, eODConfig.isEODAutoDisable());
				ps.setBoolean(index++, eODConfig.isSendEmailRequired());
				ps.setString(index++, eODConfig.getSMTPHost());
				ps.setString(index++, eODConfig.getSMTPPort());
				ps.setString(index++, eODConfig.getSMTPPwd());
				ps.setBoolean(index++, eODConfig.isSMTPAutenticationRequired());
				ps.setString(index++, eODConfig.getSMTPUserName());
				ps.setString(index++, eODConfig.getEncryptionType());
				ps.setString(index++, eODConfig.getFromEmailAddress());
				ps.setString(index++, eODConfig.getFromName());
				ps.setString(index++, eODConfig.getToEmailAddress());
				ps.setString(index++, eODConfig.getCCEmailAddress());
				ps.setBoolean(index++, eODConfig.isEmailNotifReqrd());
				ps.setBoolean(index++, eODConfig.isPublishNotifReqrd());
				ps.setString(index++, eODConfig.getReminderFrequency());
				ps.setBoolean(index++, eODConfig.isDelayNotifyReq());
				ps.setString(index++, eODConfig.getDelayFrequency());

				ps.setLong(index++, eODConfig.getEodConfigId());

				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(index, eODConfig.getPrevMntOn());
				} else {
					ps.setInt(index, eODConfig.getVersion() - 1);
				}
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(EODConfig eODConfig, TableType tableType) {
		logger.debug(Literal.ENTERING);

		Object[] object = new Object[] {};
		StringBuilder sql = new StringBuilder("Delete From");
		sql.append(" EODConfig");
		sql.append(tableType.getSuffix());
		sql.append(" Where EodConfigId = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		if (tableType == TableType.TEMP_TAB) {
			object = new Object[] { eODConfig.getEodConfigId(), eODConfig.getPrevMntOn() };
		} else {
			object = new Object[] { eODConfig.getEodConfigId(), eODConfig.getVersion() - 1 };
		}

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), object);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<EODConfig> getEODConfig() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery("");

		logger.trace(Literal.SQL + sql.toString());

		EODConfigRowMapper rowMapper = new EODConfigRowMapper();

		return this.jdbcOperations.query(sql.toString(), rowMapper);
	}

	@Override
	public void updateExtMnthEnd(EODConfig eODConfig) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update");
		sql.append(" EODConfig");
		sql.append(" set InExtMnth = ?, PrvExtMnth = ?");
		sql.append(" Where EodConfigId = ?");

		logger.trace(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setBoolean(index++, eODConfig.isInExtMnth());
				ps.setDate(index++, JdbcUtil.getDate(eODConfig.getPrvExtMnth()));

				ps.setLong(index, eODConfig.getEodConfigId());
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public String getFrequency() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" EODStartJobFrequency");
		sql.append(" From EODConfig");

		logger.trace(Literal.SQL + sql.toString());
		List<String> eodFrequency = this.jdbcOperations.query(sql.toString(), new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("EODStartJobFrequency");
			}
		});

		if (CollectionUtils.isEmpty(eodFrequency)) {
			return null;
		}

		return eodFrequency.get(0);
	}

	@Override
	public boolean isAutoRequired() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AutoEodRequired");
		sql.append(" From EODConfig");

		logger.trace(Literal.SQL + sql.toString());
		List<Boolean> eodRequired = this.jdbcOperations.query(sql.toString(), new RowMapper<Boolean>() {
			@Override
			public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getBoolean("AutoEodRequired");
			}
		});

		return eodRequired.get(0);
	}

	@Override
	public boolean isAutoEODEnabled() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" EnableAutoEod");
		sql.append(" From EODConfig");

		logger.trace(Literal.SQL + sql.toString());
		List<Boolean> eodRequired = this.jdbcOperations.query(sql.toString(), new RowMapper<Boolean>() {
			@Override
			public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getBoolean("EnableAutoEod");
			}
		});

		return eodRequired.get(0);
	}

	@Override
	public void updateEnableEOD(EODConfig eODConfig) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update");
		sql.append(" EODConfig");
		sql.append(" set EnableAutoEod = ?");

		logger.trace(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setBoolean(index, eODConfig.isEnableAutoEod());
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public String getReminderFrequency() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReminderFrequency");
		sql.append(" From EODConfig");

		logger.trace(Literal.SQL + sql.toString());
		List<String> eodFrequency = this.jdbcOperations.query(sql.toString(), new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("ReminderFrequency");
			}
		});

		if (CollectionUtils.isEmpty(eodFrequency)) {
			return null;
		}

		return eodFrequency.get(0);
	}

	@Override
	public String getDelayFrequency() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DelayFrequency");
		sql.append(" From EODConfig");

		logger.trace(Literal.SQL + sql.toString());
		List<String> eodFrequency = this.jdbcOperations.query(sql.toString(), new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("DelayFrequency");
			}
		});

		if (CollectionUtils.isEmpty(eodFrequency)) {
			return null;
		}

		return eodFrequency.get(0);
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" EodConfigId, ExtMnthRequired, MnthExtTo, Active, InExtMnth, PrvExtMnth, Version");
		sql.append(", LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, AutoEodRequired, EODStartJobFrequency, EnableAutoEod");
		sql.append(", EODAutoDisable, SendEmailRequired, SMTPHost, SMTPPort, SMTPAutenticationRequired");
		sql.append(", SMTPUserName, SMTPPwd, EncryptionType, FromEmailAddress, FromName");
		sql.append(", ToEmailAddress, CCEmailAddress, EmailNotifReqrd, PublishNotifReqrd");
		sql.append(", ReminderFrequency, DelayNotifyReq, DelayFrequency");
		sql.append(" From EODConfig");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class EODConfigRowMapper implements RowMapper<EODConfig> {

		private EODConfigRowMapper() {
			super();
		}

		@Override
		public EODConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
			EODConfig eod = new EODConfig();

			eod.setEodConfigId(rs.getLong("EodConfigId"));
			eod.setExtMnthRequired(rs.getBoolean("ExtMnthRequired"));
			eod.setMnthExtTo(rs.getTimestamp("MnthExtTo"));
			eod.setActive(rs.getBoolean("Active"));
			eod.setInExtMnth(rs.getBoolean("InExtMnth"));
			eod.setPrvExtMnth(rs.getTimestamp("PrvExtMnth"));
			eod.setVersion(rs.getInt("Version"));
			eod.setLastMntOn(rs.getTimestamp("LastMntOn"));
			eod.setLastMntBy(rs.getLong("LastMntBy"));
			eod.setRecordStatus(rs.getString("RecordStatus"));
			eod.setRoleCode(rs.getString("RoleCode"));
			eod.setNextRoleCode(rs.getString("NextRoleCode"));
			eod.setTaskId(rs.getString("TaskId"));
			eod.setNextTaskId(rs.getString("NextTaskId"));
			eod.setRecordType(rs.getString("RecordType"));
			eod.setWorkflowId(rs.getLong("WorkflowId"));
			eod.setAutoEodRequired(rs.getBoolean("AutoEodRequired"));
			eod.setEODStartJobFrequency(rs.getString("EODStartJobFrequency"));
			eod.setEnableAutoEod(rs.getBoolean("EnableAutoEod"));
			eod.setEODAutoDisable(rs.getBoolean("EODAutoDisable"));
			eod.setSendEmailRequired(rs.getBoolean("SendEmailRequired"));
			eod.setSMTPHost(rs.getString("SMTPHost"));
			eod.setSMTPPort(rs.getString("SMTPPort"));
			eod.setSMTPAutenticationRequired(rs.getBoolean("SMTPAutenticationRequired"));
			eod.setSMTPUserName(rs.getString("SMTPUserName"));
			eod.setSMTPPwd(rs.getString("SMTPPwd"));
			eod.setEncryptionType(rs.getString("EncryptionType"));
			eod.setFromEmailAddress(rs.getString("FromEmailAddress"));
			eod.setFromName(rs.getString("FromName"));
			eod.setToEmailAddress(rs.getString("ToEmailAddress"));
			eod.setCCEmailAddress(rs.getString("CCEmailAddress"));
			eod.setEmailNotifReqrd(rs.getBoolean("EmailNotifReqrd"));
			eod.setPublishNotifReqrd(rs.getBoolean("PublishNotifReqrd"));
			eod.setReminderFrequency(rs.getString("ReminderFrequency"));
			eod.setDelayNotifyReq(rs.getBoolean("DelayNotifyReq"));
			eod.setDelayFrequency(rs.getString("DelayFrequency"));

			return eod;
		}
	}

	@Override
	public void updateJobDetails(String jobKey, String frequency, boolean jobEnabled) {
		String sql = "Update Job_details set cron_expression = ?, enabled = ? Where JobKey = ?";

		logger.debug(Literal.SQL.concat(sql));

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, frequency);
			ps.setBoolean(2, jobEnabled);
			ps.setString(3, jobKey);
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

}
