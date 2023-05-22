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
 * * FileName : CustomerPhoneNumberDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.financialSummary.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.finance.financialSummary.RecommendationNotesDetailsDAO;
import com.pennant.backend.model.finance.financialsummary.RecommendationNotes;
import com.pennant.backend.model.finance.financialsummary.RecommendationNotesConfiguration;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class RecommendationNotesDetailsDAOImpl extends SequenceDao<RecommendationNotesDetailsDAO>
		implements RecommendationNotesDetailsDAO {
	private static Logger logger = LogManager.getLogger(RecommendationNotesDetailsDAOImpl.class);

	public RecommendationNotesDetailsDAOImpl() {
		super();
	}

	@Override
	public List<RecommendationNotes> getRecommendationNotesDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  t1.Id, t2.FinID, t2.FinReference, t1.ParticularId, t3.Particulars, t1.Remarks");
		sql.append(", t1.Version, t1.LastMntBy, t1.LastMntOn, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" From  Recommendation_Notes_Temp t1");
		sql.append(" Left Join FinanceMain_TEMP t2 on t2.FinID =  t1.FinID");
		sql.append(" Left Join RECOMMENDATION_NOTES_CONFIG t3 on t3.id =  t1.ParticularId");
		sql.append(" Where t2.FinID = ?");
		sql.append(" UNION ALL");
		sql.append(" Select t1.Id, t2.FinID, t2.FinReference, t1.ParticularId, t3.Particulars, t1.Remarks");
		sql.append(", t1.Version, t1.LastMntBy, t1.LastMntOn, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" From  Recommendation_Notes t1");
		sql.append(" Left Join FinanceMain_TEMP t2 on t2.FinID =  t1.FinID");
		sql.append(" Left Join Recommendation_Notes_Config t3 on t3.Id =  t1.ParticularId");
		sql.append(" Where NOT Exists (Select 1 From Recommendation_Notes_Temp Where Id = t1.Id)");
		sql.append(" and t2.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setLong(index, finID);
		}, (rs, rowNum) -> {
			RecommendationNotes rn = new RecommendationNotes();

			rn.setId(rs.getLong("Id"));
			rn.setFinID(rs.getLong("FinID"));
			rn.setFinReference(rs.getString("FinReference"));
			rn.setParticularId(rs.getLong("ParticularId"));
			rn.setParticulars(rs.getString("Particulars"));
			rn.setRemarks(rs.getString("Remarks"));
			rn.setVersion(rs.getInt("Version"));
			rn.setLastMntBy(rs.getLong("LastMntBy"));
			rn.setLastMntOn(rs.getTimestamp("LastMntOn"));
			rn.setRecordStatus(rs.getString("RecordStatus"));
			rn.setRoleCode(rs.getString("RoleCode"));
			rn.setNextRoleCode(rs.getString("NextRoleCode"));
			rn.setTaskId(rs.getString("TaskId"));
			rn.setNextTaskId(rs.getString("NextTaskId"));
			rn.setRecordType(rs.getString("RecordType"));
			rn.setWorkflowId(rs.getLong("WorkflowId"));

			return rn;
		});
	}

	@Override
	public void delete(RecommendationNotes rn, String type) {
		StringBuilder sql = new StringBuilder("Delete From Recommendation_Notes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ? and FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, rn.getId());
				ps.setLong(index, rn.getFinID());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(RecommendationNotes rn, String type) {
		if (rn.getId() == Long.MIN_VALUE) {
			rn.setId(getNextValue("SeqRECOMMENDATION_NOTE"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Recommendation_Notes");
		sql.append(type);
		sql.append(" (Id, FinID, FinReference, ParticularId, Remarks");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, rn.getId());
				ps.setLong(index++, rn.getFinID());
				ps.setString(index++, rn.getFinReference());
				ps.setLong(index++, rn.getParticularId());
				ps.setString(index++, rn.getRemarks());
				ps.setInt(index++, rn.getVersion());
				ps.setLong(index++, rn.getLastMntBy());
				ps.setTimestamp(index++, rn.getLastMntOn());
				ps.setString(index++, rn.getRecordStatus());
				ps.setString(index++, rn.getRoleCode());
				ps.setString(index++, rn.getNextRoleCode());
				ps.setString(index++, rn.getTaskId());
				ps.setString(index++, rn.getNextTaskId());
				ps.setString(index++, rn.getRecordType());
				ps.setLong(index, rn.getWorkflowId());

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return rn.getId();
	}

	@Override
	public void update(RecommendationNotes rn, String type) {
		StringBuilder sql = new StringBuilder("Update Recommendation_Notes");
		sql.append(type);
		sql.append(" Set FinReference = ?, ParticularId = ?, Remarks = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ? and FinID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, rn.getFinReference());
			ps.setLong(index++, rn.getParticularId());
			ps.setString(index++, rn.getRemarks());
			ps.setInt(index++, rn.getVersion());
			ps.setLong(index++, rn.getLastMntBy());
			ps.setTimestamp(index++, rn.getLastMntOn());
			ps.setString(index++, rn.getRecordStatus());
			ps.setString(index++, rn.getRoleCode());
			ps.setString(index++, rn.getNextRoleCode());
			ps.setString(index++, rn.getTaskId());
			ps.setString(index++, rn.getNextTaskId());
			ps.setString(index++, rn.getRecordType());
			ps.setLong(index++, rn.getWorkflowId());
			ps.setLong(index++, rn.getId());
			ps.setLong(index++, rn.getFinID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, rn.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	// FIXME:FinID seems method not using
	@Override
	public int getVersion(long id, String recommendationNotesDetails) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("recommendationNotesDetails", recommendationNotesDetails);

		StringBuilder sql = new StringBuilder();
		sql.append("Select Version From RECOMMENDATION_NOTES");

		sql.append(" Where Id = :id and finReference = :finReference");

		logger.debug("insertSql: " + sql.toString());

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}

	public List<RecommendationNotesConfiguration> getRecommendationNotesConfigurationDetails() {
		String sql = "Select Id, Particulars From Recommendation_Notes_Config";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, (rs, rowNum) -> {
			RecommendationNotesConfiguration rnf = new RecommendationNotesConfiguration();

			rnf.setId(rs.getLong("Id"));
			rnf.setParticulars(rs.getString("Particulars"));

			return rnf;
		});

	}

}