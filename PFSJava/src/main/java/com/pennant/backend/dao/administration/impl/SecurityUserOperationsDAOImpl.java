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
 * * FileName : SecurityUserOperationsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-06-2011 * *
 * Modified Date : 10-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.administration.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserOperations;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class SecurityUserOperationsDAOImpl extends SequenceDao<SecurityUser> implements SecurityUserOperationsDAO {

	public SecurityUserOperations getSecurityUserOperations() {
		return new SecurityUserOperations();
	}

	@Override
	public SecurityUserOperations getNewSecurityUserOperations() {
		SecurityUserOperations securityUserOperations = getSecurityUserOperations();
		securityUserOperations.setNewRecord(true);
		return securityUserOperations;
	}

	public List<SecurityUserOperations> getSecUserOperationsByUsrID(SecurityUser secUser, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" UsrOprID, UsrID, OprID");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescFirstName, LovDescMiddleName, LovDescLastName, LovDescOprCd ");
			sql.append(", LovDescOprDesc, LovDescUsrFName, LovDescUsrMName, LovDescUsrLName");
		}

		sql.append(" From SecUserOperations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where UsrID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			SecurityUserOperations sup = new SecurityUserOperations();

			sup.setUsrOprID(rs.getLong("UsrOprID"));
			sup.setUsrID(rs.getLong("UsrID"));
			sup.setOprID(rs.getLong("OprID"));
			sup.setVersion(rs.getInt("Version"));
			sup.setLastMntBy(rs.getLong("LastMntBy"));
			sup.setLastMntOn(rs.getTimestamp("LastMntOn"));
			sup.setRecordStatus(rs.getString("RecordStatus"));
			sup.setRoleCode(rs.getString("RoleCode"));
			sup.setNextRoleCode(rs.getString("NextRoleCode"));
			sup.setTaskId(rs.getString("TaskId"));
			sup.setNextTaskId(rs.getString("NextTaskId"));
			sup.setRecordType(rs.getString("RecordType"));
			sup.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				sup.setLovDescFirstName(rs.getString("LovDescFirstName"));
				sup.setLovDescMiddleName(rs.getString("LovDescMiddleName"));
				sup.setLovDescLastName(rs.getString("LovDescLastName"));
				sup.setLovDescOprCd(rs.getString("LovDescOprCd"));
				sup.setLovDescOprDesc(rs.getString("LovDescOprDesc"));
				sup.setLovDescUsrFName(rs.getString("LovDescUsrFName"));
				sup.setLovDescUsrMName(rs.getString("LovDescUsrMName"));
				sup.setLovDescUsrLName(rs.getString("LovDescUsrLName"));
			}
			return sup;
		}, secUser.getUsrID());
	}

	public SecurityUserOperations getUserOperationsByUsrAndRoleIds(long userId, long oprId) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select UsrOprID, UsrID, OprID, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, RecordType, WorkflowId");
		sql.append(" From SecUserOperations");
		sql.append(" Where UsrID = ? And RoleID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				SecurityUserOperations sup = new SecurityUserOperations();

				sup.setUsrOprID(rs.getLong("UsrOprID"));
				sup.setUsrID(rs.getLong("UsrID"));
				sup.setOprID(rs.getLong("OprID"));
				sup.setVersion(rs.getInt("Version"));
				sup.setLastMntBy(rs.getLong("LastMntBy"));
				sup.setLastMntOn(rs.getTimestamp("LastMntOn"));
				sup.setRecordStatus(rs.getString("RecordStatus"));
				sup.setRoleCode(rs.getString("RoleCode"));
				sup.setNextRoleCode(rs.getString("NextRoleCode"));
				sup.setTaskId(rs.getString("TaskId"));
				sup.setRecordType(rs.getString("RecordType"));
				sup.setWorkflowId(rs.getLong("WorkflowId"));

				return sup;
			}, userId, oprId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public void delete(SecurityUserOperations sup, String type) {
		StringBuilder sql = new StringBuilder("Delete from SecUserOperations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UsrID = ? And OprID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				ps.setLong(index++, sup.getUsrID());
				ps.setLong(index++, sup.getOprID());

			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	public void deleteById(final long usrID, String type) {
		StringBuilder sql = new StringBuilder("Delete From SecUserOperations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UsrID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, usrID));
	}

	public long save(SecurityUserOperations suo, String type) {
		StringBuilder sql = new StringBuilder("Insert Into SecUserOperations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(UsrOprID, UsrID, OprID, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, suo.getUsrOprID());
			ps.setLong(index++, suo.getUsrID());
			ps.setLong(index++, suo.getOprID());
			ps.setInt(index++, suo.getVersion());
			ps.setLong(index++, suo.getLastMntBy());
			ps.setTimestamp(index++, suo.getLastMntOn());
			ps.setString(index++, suo.getRecordStatus());
			ps.setString(index++, suo.getRoleCode());
			ps.setString(index++, suo.getNextRoleCode());
			ps.setString(index++, suo.getTaskId());
			ps.setString(index++, suo.getNextTaskId());
			ps.setString(index++, suo.getRecordType());
			ps.setLong(index++, suo.getWorkflowId());
		});

		return suo.getId();
	}

	public void update(SecurityUserOperations suo, String type) {
		StringBuilder sql = new StringBuilder("Update SecUserOperations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set UsrID = ?, OprID = ?,");
		sql.append(" Version = ? , LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where UsrOprID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, suo.getUsrID());
			ps.setLong(index++, suo.getOprID());
			ps.setInt(index++, suo.getVersion());
			ps.setLong(index++, suo.getLastMntBy());
			ps.setTimestamp(index++, suo.getLastMntOn());
			ps.setString(index++, suo.getRecordStatus());
			ps.setString(index++, suo.getRoleCode());
			ps.setString(index++, suo.getNextRoleCode());
			ps.setString(index++, suo.getTaskId());
			ps.setString(index++, suo.getNextTaskId());
			ps.setString(index++, suo.getRecordType());
			ps.setLong(index++, suo.getWorkflowId());

			ps.setLong(index++, suo.getUsrOprID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	public int getRoleIdCount(long roleId) {
		String sql = "Select count(RoleId) From UserOperationRoles_View where RoleId = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, roleId);
	}

	public int getUserIdCount(long userId) {
		String sql = "Select count(UsrID) From UserOperationRoles_View where UsrID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, userId);
	}

	@Override
	public List<SecurityOperation> getOperationsByUserId(long userId, boolean isAssigned) {
		SecurityUser user = new SecurityUser();
		user.setUsrID(userId);

		StringBuilder sql = new StringBuilder();
		if (isAssigned) {
			sql.append("select * from SecOperations_View where OprID in");
			sql.append(" (select RoleID from UserRoles_AView where UsrID = :UsrID)");
		} else {
			sql.append("select * from SecOperations_View where OprID not in");
			sql.append(" (select RoleID from UserRoles_AView where UsrID = :UsrID)");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(user);

		RowMapper<SecurityOperation> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityOperation.class);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public int getOprById(long oprID, String type) {
		StringBuilder sql = new StringBuilder("Select count(OprID) From SecUserOperations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where OprID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, oprID);
	}

	@Override
	public List<String> getUsersByRoles(String[] roleCodes) {
		StringBuilder sql = new StringBuilder("Select distinct U.UsrLogin");
		sql.append(" from SecUsers U");
		sql.append(" inner join SecUserOperations UO on UO.UsrID = U.UsrID");
		sql.append(" inner join SecOperationRoles OPR on OPR.OprID = UO.OprID");
		sql.append(" inner join SecRoles R on R.RoleID = OPR.RoleID");
		sql.append(" where R.RoleCd in (");

		for (int i = 0; i < roleCodes.length; i++) {
			sql.append(" ?,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			for (int i = 0; i < roleCodes.length; i++) {
				ps.setString(index++, roleCodes[i]);
			}
		}, (rs, rowNum) -> rs.getString("UsrLogin"));
	}

	@Override
	public List<String> getUsersByRoles(String[] roleCodes, String division, String branch) {
		StringBuilder sql = new StringBuilder("select distinct U.UsrLogin");
		sql.append(" from SecUsers U");
		sql.append(" inner join SecUserOperations UO on UO.UsrID = U.UsrID");
		sql.append(" inner join SecOperationRoles OPR on OPR.OprID = UO.OprID");
		sql.append(" inner join SecRoles R on R.RoleID = OPR.RoleID");
		sql.append(" inner join SecurityUserDivBranch UDB on UDB.UsrID = U.UsrID");
		sql.append(" where R.RoleCd in (");

		for (int i = 0; i < roleCodes.length; i++) {
			sql.append(" ?,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
		sql.append(" and UDB.UserDivision = ? and UDB.UserBranch = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (int i = 0; i < roleCodes.length; i++) {
				ps.setString(index++, roleCodes[i]);
			}
			ps.setString(index++, division);
			ps.setString(index, branch);
		}, (rs, rowNum) -> rs.getString("UsrLogin"));
	}

	@Override
	public List<String> getUsrMailsByRoleIds(String roleIds) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		List<String> listAsString = Arrays.asList(roleIds.split(","));
		source.addValue("RoleCds", listAsString);

		StringBuilder sql = new StringBuilder("Select distinct UsrEmail from UserOperationRoles_View");
		sql.append(" Where RoleCd IN (:RoleCds) AND COALESCE(UsrEmail, ' ') <> ' '  ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcTemplate.queryForList(sql.toString(), source, String.class);
	}

	@Override
	public long getNextValue() {
		return getNextValue("SeqSecUserOperations");
	}

	@Override
	public List<Long> getSecUserOperationIdsByUsrID(long usrID, String type) {
		String sql = "Select OprID from SecUserOperations Where UsrID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForList(sql, Long.class, usrID);
	}

	@Override
	public boolean isOpertionExists(String code, long usrID) {
		String sql = "Select Count(OprID) From SecUserOperations Where OprID = (Select OprID from Secoperations Where OprCode = ?) and UsrID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, code, usrID) > 0;
	}
}