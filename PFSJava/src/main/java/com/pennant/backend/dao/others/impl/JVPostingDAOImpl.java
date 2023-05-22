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
 * * FileName : JVPostingDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2013 * * Modified
 * Date : 21-06-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-06-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.others.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.others.JVPostingDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>JVPosting model</b> class.<br>
 * 
 */
public class JVPostingDAOImpl extends SequenceDao<JVPosting> implements JVPostingDAO {
	private static Logger logger = LogManager.getLogger(JVPostingDAOImpl.class);

	public JVPostingDAOImpl() {
		super();
	}

	@Override
	public JVPosting getJVPosting() {
		WorkFlowDetails wfd = WorkFlowUtil.getWorkFlowDetails("JVPosting");
		JVPosting jvp = new JVPosting();

		if (wfd != null) {
			jvp.setWorkflowId(wfd.getWorkFlowId());
		}

		return jvp;
	}

	@Override
	public JVPosting getNewJVPosting() {
		JVPosting jvp = getJVPosting();

		jvp.setNewRecord(true);

		return jvp;
	}

	@Override
	public JVPosting getJVPostingById(final long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BatchReference, Batch, PostingDate, Filename, Branch, DebitCount, CreditsCount");
		sql.append(", TotDebitsByBatchCcy, TotCreditsByBatchCcy, BatchPurpose, Currency, ExchangeRateType");
		sql.append(", ValidationStatus, BatchPostingStatus, PostAgainst, Reference, PostingDivision");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CurrencyDesc, DivisionCodeDesc");
		}

		sql.append(" From JVPostings");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BatchReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				JVPosting jvp = new JVPosting();

				jvp.setBatchReference(rs.getLong("BatchReference"));
				jvp.setBatch(rs.getString("Batch"));
				jvp.setPostingDate(rs.getTimestamp("PostingDate"));
				jvp.setFilename(rs.getString("Filename"));
				jvp.setBranch(rs.getString("Branch"));
				jvp.setDebitCount(rs.getInt("DebitCount"));
				jvp.setCreditsCount(rs.getInt("CreditsCount"));
				jvp.setTotDebitsByBatchCcy(rs.getBigDecimal("TotDebitsByBatchCcy"));
				jvp.setTotCreditsByBatchCcy(rs.getBigDecimal("TotCreditsByBatchCcy"));
				jvp.setBatchPurpose(rs.getString("BatchPurpose"));
				jvp.setCurrency(rs.getString("Currency"));
				jvp.setExchangeRateType(rs.getString("ExchangeRateType"));
				jvp.setValidationStatus(rs.getString("ValidationStatus"));
				jvp.setBatchPostingStatus(rs.getString("BatchPostingStatus"));
				jvp.setPostAgainst(rs.getString("PostAgainst"));
				jvp.setReference(rs.getString("Reference"));
				jvp.setPostingDivision(rs.getString("PostingDivision"));
				jvp.setVersion(rs.getInt("Version"));
				jvp.setLastMntBy(rs.getLong("LastMntBy"));
				jvp.setLastMntOn(rs.getTimestamp("LastMntOn"));
				jvp.setRecordStatus(rs.getString("RecordStatus"));
				jvp.setRoleCode(rs.getString("RoleCode"));
				jvp.setNextRoleCode(rs.getString("NextRoleCode"));
				jvp.setTaskId(rs.getString("TaskId"));
				jvp.setNextTaskId(rs.getString("NextTaskId"));
				jvp.setRecordType(rs.getString("RecordType"));
				jvp.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					// jvp.setCurrencyDesc(rs.getString("CurrencyDesc")); not available in bean
					jvp.setDivisionCodeDesc(rs.getString("DivisionCodeDesc"));
				}

				return jvp;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public JVPosting getJVPostingByFileName(String batchName) {
		JVPosting jVPosting = getJVPosting();
		StringBuilder sql = new StringBuilder("Select *  From JVPostings_View");
		sql.append(" Where Batch ='" + batchName + "' and PostingDate='"
				+ DateUtil.format(DateUtil.getSysDate(), PennantConstants.DBDateTimeFormat) + "'");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		RowMapper<JVPosting> typeRowMapper = BeanPropertyRowMapper.newInstance(JVPosting.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(JVPosting jvp, String type) {
		StringBuilder sql = new StringBuilder("Delete From JVPostings");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BatchReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index, jvp.getBatchReference());
			});

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	public long getReferenceSequence() {
		return getNextValue("SeqMscPostRef");
	}

	@Override
	public long save(JVPosting jvp, String type) {
		if (jvp.isNewRecord() && jvp.getBatchReference() == 0) {
			jvp.setBatchReference(createBatchReference());
		}

		if (FinanceConstants.POSTING_AGAINST_NONLOAN.equals(jvp.getPostAgainst())) {
			final JVPosting aJVPosting = new JVPosting();
			BeanUtils.copyProperties(getJVPosting(), aJVPosting);
			jvp.setReference(Long.toString(getReferenceSequence()));
		}

		StringBuilder sql = new StringBuilder("Insert Into JVPostings");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (BatchReference, PostingDate, Batch, Filename, Branch, DebitCount, CreditsCount");
		sql.append(", TotDebitsByBatchCcy, TotCreditsByBatchCcy, BatchPurpose, Currency, ExchangeRateType");
		sql.append(", ValidationStatus, BatchPostingStatus, ExpReference, Reference, PostAgainst, PostingDivision");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, jvp.getBatchReference());
			ps.setDate(index++, JdbcUtil.getDate(jvp.getPostingDate()));
			ps.setString(index++, jvp.getBatch());
			ps.setString(index++, jvp.getFilename());
			ps.setString(index++, jvp.getBranch());
			ps.setInt(index++, jvp.getDebitCount());
			ps.setInt(index++, jvp.getCreditsCount());
			ps.setBigDecimal(index++, jvp.getTotDebitsByBatchCcy());
			ps.setBigDecimal(index++, jvp.getTotCreditsByBatchCcy());
			ps.setString(index++, jvp.getBatchPurpose());
			ps.setString(index++, jvp.getCurrency());
			ps.setString(index++, jvp.getExchangeRateType());
			ps.setString(index++, jvp.getValidationStatus());
			ps.setString(index++, jvp.getBatchPostingStatus());
			ps.setString(index++, jvp.getExpReference());
			ps.setString(index++, jvp.getReference());
			ps.setString(index++, jvp.getPostAgainst());
			ps.setString(index++, jvp.getPostingDivision());
			ps.setInt(index++, jvp.getVersion());
			ps.setLong(index++, jvp.getLastMntBy());
			ps.setTimestamp(index++, jvp.getLastMntOn());
			ps.setString(index++, jvp.getRecordStatus());
			ps.setString(index++, jvp.getRoleCode());
			ps.setString(index++, jvp.getNextRoleCode());
			ps.setString(index++, jvp.getTaskId());
			ps.setString(index++, jvp.getNextTaskId());
			ps.setString(index++, jvp.getRecordType());
			ps.setLong(index, jvp.getWorkflowId());
		});

		return jvp.getBatchReference();
	}

	@Override
	public long createBatchReference() {
		return getNextValue("SeqJVpostings");
	}

	@Override
	public void update(JVPosting jvp, String type) {
		StringBuilder sql = new StringBuilder("Update JVPostings");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set PostingDate = ?, Batch = ?, Filename = ?, Branch = ?, DebitCount = ?, CreditsCount = ?");
		sql.append(", TotDebitsByBatchCcy = ?, TotCreditsByBatchCcy = ?, BatchPurpose = ?, Currency = ?");
		sql.append(", ExchangeRateType = ?, ValidationStatus = ?, BatchPostingStatus = ?, ExpReference = ?");
		sql.append(", Reference = ?, PostAgainst = ?, PostingDivision = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where BatchReference = ?");

		if (!type.endsWith("_Temp")) {
			sql.append("  and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(jvp.getPostingDate()));
			ps.setString(index++, jvp.getBatch());
			ps.setString(index++, jvp.getFilename());
			ps.setString(index++, jvp.getBranch());
			ps.setInt(index++, jvp.getDebitCount());
			ps.setInt(index++, jvp.getCreditsCount());
			ps.setBigDecimal(index++, jvp.getTotDebitsByBatchCcy());
			ps.setBigDecimal(index++, jvp.getTotCreditsByBatchCcy());
			ps.setString(index++, jvp.getBatchPurpose());
			ps.setString(index++, jvp.getCurrency());
			ps.setString(index++, jvp.getExchangeRateType());
			ps.setString(index++, jvp.getValidationStatus());
			ps.setString(index++, jvp.getBatchPostingStatus());
			ps.setString(index++, jvp.getExpReference());
			ps.setString(index++, jvp.getReference());
			ps.setString(index++, jvp.getPostAgainst());
			ps.setString(index++, jvp.getPostingDivision());
			ps.setInt(index++, jvp.getVersion());
			ps.setLong(index++, jvp.getLastMntBy());
			ps.setTimestamp(index++, jvp.getLastMntOn());
			ps.setString(index++, jvp.getRecordStatus());
			ps.setString(index++, jvp.getRoleCode());
			ps.setString(index++, jvp.getNextRoleCode());
			ps.setString(index++, jvp.getTaskId());
			ps.setString(index++, jvp.getNextTaskId());
			ps.setString(index++, jvp.getRecordType());
			ps.setLong(index++, jvp.getWorkflowId());
			ps.setLong(index++, jvp.getBatchReference());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, jvp.getVersion() - 1);
			}

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateValidationStatus(JVPosting jvp, String type) {
		StringBuilder sql = new StringBuilder("Update JVPostings");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set ValidationStatus = ?");
		sql.append(" Where BatchReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, jvp.getValidationStatus());
			ps.setLong(index, jvp.getBatchReference());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateBatchPostingStatus(JVPosting jvp, String type) {
		StringBuilder sql = new StringBuilder("Update JVPostings");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set BatchPostingStatus = ?");
		sql.append(" Where BatchReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, jvp.getBatchPostingStatus());
			ps.setLong(index, jvp.getBatchReference());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public long getBatchRerbyExpRef(String expReference) {
		String sql = "Select BatchReference From JVPostings_View Where ExpReference = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, expReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}
}