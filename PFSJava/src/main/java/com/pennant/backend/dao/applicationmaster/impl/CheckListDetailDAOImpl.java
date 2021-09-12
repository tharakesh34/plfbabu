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
 * * FileName : CheckListDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-12-2011 * *
 * Modified Date : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.applicationmaster.CheckListDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>CheckListDetail model</b> class.<br>
 * 
 */
public class CheckListDetailDAOImpl extends BasicDao<CheckListDetail> implements CheckListDetailDAO {
	private static Logger logger = LogManager.getLogger(CheckListDetailDAOImpl.class);

	public CheckListDetailDAOImpl() {
		super();
	}

	@Override
	public CheckListDetail getCheckListDetail() {
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CheckListDetail");
		CheckListDetail checkListDetail = new CheckListDetail();

		if (workFlowDetails != null) {
			checkListDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		return checkListDetail;
	}

	@Override
	public CheckListDetail getNewCheckListDetail() {
		CheckListDetail checkListDetail = getCheckListDetail();
		checkListDetail.setNewRecord(true);

		return checkListDetail;
	}

	@Override
	public CheckListDetail getCheckListDetailById(final long id, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CheckListId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new CheckListDetailRM(type), id);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public CheckListDetail getCheckListDetailByDocType(String docType, String finType) {
		StringBuilder sql = getSqlQuery("");
		sql.append(" Where DocType = ? and CheckListId in (");
		sql.append("Select FinRefId From LMTFinRefDetail Where FinRefType = ? and FinType = ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new CheckListDetailRM(""), docType,
					FinanceConstants.PROCEDT_CHECKLIST, finType);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	public List<CheckListDetail> getCheckListDetailByChkList(final long checkListId, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where CheckListId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, checkListId), new CheckListDetailRM(type));
	}

	public List<CheckListDetail> getCheckListDetailByChkList(final Set<Long> checkListId, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where CheckListId IN (");
		sql.append(checkListId.stream().map(e -> "? ").collect(Collectors.joining(",")));
		sql.append(") Order By CheckListId, AnsSeqNo");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (Long id : checkListId) {
				ps.setLong(index++, id);
			}
		}, new CheckListDetailRM(type));

	}

	public void delete(CheckListDetail checkListDetail, String type) {
		StringBuilder sql = new StringBuilder("Delete From RMTCheckListDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CheckListId = ? and AnsSeqNo = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, checkListDetail.getCheckListId());
				ps.setLong(index++, checkListDetail.getAnsSeqNo());
			});
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	public void delete(long checkListId, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Delete From RMTCheckListDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CheckListId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, checkListId));
	}

	@Override
	public long save(CheckListDetail cld, String type) {
		StringBuilder sql = new StringBuilder("Insert Into RMTCheckListDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (CheckListId, AnsSeqNo, AnsDesc, AnsCond, RemarksAllow, DocRequired, DocType, RemarksMand");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, cld.getCheckListId());
			ps.setLong(index++, cld.getAnsSeqNo());
			ps.setString(index++, cld.getAnsDesc());
			ps.setString(index++, cld.getAnsCond());
			ps.setBoolean(index++, cld.isRemarksAllow());
			ps.setBoolean(index++, cld.isDocRequired());
			ps.setString(index++, cld.getDocType());
			ps.setBoolean(index++, cld.isRemarksMand());
			ps.setInt(index++, cld.getVersion());
			ps.setLong(index++, JdbcUtil.setLong(cld.getLastMntBy()));
			ps.setTimestamp(index++, cld.getLastMntOn());
			ps.setString(index++, cld.getRecordStatus());
			ps.setString(index++, cld.getRoleCode());
			ps.setString(index++, cld.getNextRoleCode());
			ps.setString(index++, cld.getTaskId());
			ps.setString(index++, cld.getNextTaskId());
			ps.setString(index++, cld.getRecordType());
			ps.setLong(index++, JdbcUtil.setLong(cld.getWorkflowId()));
		});

		return cld.getId();
	}

	@Override
	public void update(CheckListDetail cld, String type) {
		StringBuilder sql = new StringBuilder("Update RMTCheckListDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set AnsDesc = ?, AnsCond = ?");
		sql.append(", RemarksAllow = ?, DocRequired = ?, DocType = ?, RemarksMand = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus= ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where CheckListId = ? and AnsSeqNo = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, cld.getAnsDesc());
			ps.setString(index++, cld.getAnsCond());
			ps.setBoolean(index++, cld.isRemarksAllow());
			ps.setBoolean(index++, cld.isDocRequired());
			ps.setString(index++, cld.getDocType());
			ps.setBoolean(index++, cld.isRemarksMand());
			ps.setInt(index++, cld.getVersion());
			ps.setLong(index++, JdbcUtil.setLong(cld.getLastMntBy()));
			ps.setTimestamp(index++, cld.getLastMntOn());
			ps.setString(index++, cld.getRecordStatus());
			ps.setString(index++, cld.getRoleCode());
			ps.setString(index++, cld.getNextRoleCode());
			ps.setString(index++, cld.getTaskId());
			ps.setString(index++, cld.getNextTaskId());
			ps.setString(index++, cld.getRecordType());
			ps.setLong(index++, JdbcUtil.setLong(cld.getWorkflowId()));

			ps.setLong(index++, cld.getCheckListId());
			ps.setLong(index++, cld.getAnsSeqNo());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index++, cld.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CheckListId, AnsSeqNo, AnsDesc, RemarksAllow, RemarksMand, DocRequired, DocType, AnsCond");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CategoryCode, LovDescDocCategory");
		}

		sql.append(" From RMTCheckListDetails");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class CheckListDetailRM implements RowMapper<CheckListDetail> {
		String type;

		public CheckListDetailRM(String type) {
			this.type = type;
		}

		@Override
		public CheckListDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			CheckListDetail cld = new CheckListDetail();

			cld.setCheckListId(rs.getLong("CheckListId"));
			cld.setAnsSeqNo(rs.getLong("AnsSeqNo"));
			cld.setAnsDesc(rs.getString("AnsDesc"));
			cld.setRemarksAllow(rs.getBoolean("RemarksAllow"));
			cld.setRemarksMand(rs.getBoolean("RemarksMand"));
			cld.setDocRequired(rs.getBoolean("DocRequired"));
			cld.setDocType(rs.getString("DocType"));
			cld.setAnsCond(rs.getString("AnsCond"));
			cld.setVersion(rs.getInt("Version"));
			cld.setLastMntBy(rs.getLong("LastMntBy"));
			cld.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cld.setRecordStatus(rs.getString("RecordStatus"));
			cld.setRoleCode(rs.getString("RoleCode"));
			cld.setNextRoleCode(rs.getString("NextRoleCode"));
			cld.setTaskId(rs.getString("TaskId"));
			cld.setNextTaskId(rs.getString("NextTaskId"));
			cld.setRecordType(rs.getString("RecordType"));
			cld.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				cld.setCategoryCode(rs.getString("CategoryCode"));
				cld.setLovDescDocType(rs.getString("LovDescDocCategory"));
			}

			return cld;
		}
	}

}