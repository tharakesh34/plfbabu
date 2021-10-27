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
 * * FileName : VASRecordingDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-12-2016 * * Modified
 * Date : 02-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.fees.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.fees.FeePostingsDAO;
import com.pennant.backend.model.fees.FeePostings;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>VASRecording model</b> class.<br>
 * 
 */

public class FeePostingsDAOImpl extends SequenceDao<FeePostings> implements FeePostingsDAO {
	private static Logger logger = LogManager.getLogger(FeePostingsDAOImpl.class);

	@Override
	public FeePostings getFeePostings() {
		FeePostings feePostings = new FeePostings();

		return feePostings;
	}

	@Override
	public FeePostings getNewFeePostings() {
		FeePostings feePostings = new FeePostings();

		feePostings.setNewRecord(true);

		return feePostings;
	}

	@Override
	public FeePostings getFeePostingsById(long postId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PostId, PostAgainst, Reference, FeeTyeCode, PostingAmount, PostDate, ValueDate");
		sql.append(", Remarks, PartnerBankId, PostingDivision, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", PartnerBankName, PartnerBankAc, PartnerBankAcType, AccountSetId, DivisionCodeDesc");
		}

		sql.append(" From FeePostings");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PostId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FeePostings fp = new FeePostings();

				fp.setPostId(rs.getLong("PostId"));
				fp.setPostAgainst(rs.getString("PostAgainst"));
				fp.setReference(rs.getString("Reference"));
				fp.setFeeTyeCode(rs.getString("FeeTyeCode"));
				fp.setPostingAmount(rs.getBigDecimal("PostingAmount"));
				fp.setPostDate(rs.getTimestamp("PostDate"));
				fp.setValueDate(rs.getTimestamp("ValueDate"));
				fp.setRemarks(rs.getString("Remarks"));
				fp.setPartnerBankId(rs.getLong("PartnerBankId"));
				fp.setPostingDivision(rs.getString("PostingDivision"));
				fp.setVersion(rs.getInt("Version"));
				fp.setLastMntBy(rs.getLong("LastMntBy"));
				fp.setLastMntOn(rs.getTimestamp("LastMntOn"));
				fp.setRecordStatus(rs.getString("RecordStatus"));
				fp.setRoleCode(rs.getString("RoleCode"));
				fp.setNextRoleCode(rs.getString("NextRoleCode"));
				fp.setTaskId(rs.getString("TaskId"));
				fp.setNextTaskId(rs.getString("NextTaskId"));
				fp.setRecordType(rs.getString("RecordType"));
				fp.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					fp.setPartnerBankName(rs.getString("PartnerBankName"));
					fp.setPartnerBankAc(rs.getString("PartnerBankAc"));
					fp.setPartnerBankAcType(rs.getString("PartnerBankAcType"));
					fp.setAccountSetId(rs.getString("AccountSetId"));
					fp.setDivisionCodeDesc(rs.getString("DivisionCodeDesc"));
				}

				return fp;
			}, postId);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public void save(FeePostings fp, String type) {
		if (fp.getId() == Long.MIN_VALUE) {
			fp.setId(getNextValue("SeqFeePostings"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FeePostings");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (PostId, PostAgainst, Reference, FeeTyeCode, PostingAmount, PostDate, ValueDate");
		sql.append(", Remarks, PartnerBankId, PostingDivision, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fp.getPostId());
			ps.setString(index++, fp.getPostAgainst());
			ps.setString(index++, fp.getReference());
			ps.setString(index++, fp.getFeeTyeCode());
			ps.setBigDecimal(index++, fp.getPostingAmount());
			ps.setDate(index++, JdbcUtil.getDate(fp.getPostDate()));
			ps.setDate(index++, JdbcUtil.getDate(fp.getValueDate()));
			ps.setString(index++, fp.getRemarks());
			ps.setLong(index++, fp.getPartnerBankId());
			ps.setString(index++, fp.getPostingDivision());
			ps.setInt(index++, fp.getVersion());
			ps.setLong(index++, fp.getLastMntBy());
			ps.setTimestamp(index++, fp.getLastMntOn());
			ps.setString(index++, fp.getRecordStatus());
			ps.setString(index++, fp.getRoleCode());
			ps.setString(index++, fp.getNextRoleCode());
			ps.setString(index++, fp.getTaskId());
			ps.setString(index++, fp.getNextTaskId());
			ps.setString(index++, fp.getRecordType());
			ps.setLong(index++, fp.getWorkflowId());
		});
	}

	@Override
	public void update(FeePostings fp, String type) {
		StringBuilder sql = new StringBuilder("Update FeePostings");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set PostId = ?, PostAgainst = ?, Reference = ?, FeeTyeCode = ?, PostingAmount = ?, PostDate = ?");
		sql.append(", ValueDate = ?, Remarks = ?, PartnerBankId = ?, PostingDivision = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where PostId = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fp.getPostId());
			ps.setString(index++, fp.getPostAgainst());
			ps.setString(index++, fp.getReference());
			ps.setString(index++, fp.getFeeTyeCode());
			ps.setBigDecimal(index++, fp.getPostingAmount());
			ps.setDate(index++, JdbcUtil.getDate(fp.getPostDate()));
			ps.setDate(index++, JdbcUtil.getDate(fp.getValueDate()));
			ps.setString(index++, fp.getRemarks());
			ps.setLong(index++, fp.getPartnerBankId());
			ps.setString(index++, fp.getPostingDivision());
			ps.setInt(index++, fp.getVersion());
			ps.setLong(index++, fp.getLastMntBy());
			ps.setTimestamp(index++, fp.getLastMntOn());
			ps.setString(index++, fp.getRecordStatus());
			ps.setString(index++, fp.getRoleCode());
			ps.setString(index++, fp.getNextRoleCode());
			ps.setString(index++, fp.getTaskId());
			ps.setString(index++, fp.getNextTaskId());
			ps.setString(index++, fp.getRecordType());
			ps.setLong(index++, fp.getWorkflowId());
			
			ps.setLong(index++, fp.getPostId());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index++, fp.getVersion() - 1);
			}

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(FeePostings fp, String type) {
		StringBuilder sql = new StringBuilder("Delete From FeePostings");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PostId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, fp.getPostId());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public int getAssignedPartnerBankCount(long partnerBankId, String type) {
		StringBuilder sql = new StringBuilder("Select Count(PartnerBankId)");
		sql.append(" From FeePostings");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PartnerBankId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, partnerBankId);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return 0;
	}
}