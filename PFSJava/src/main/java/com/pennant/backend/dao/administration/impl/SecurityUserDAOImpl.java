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
 * * FileName : SecurityUserDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 2-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 2-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.administration.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>SecurityUsers model</b> class.<br>
 * 
 */
public class SecurityUserDAOImpl extends SequenceDao<SecurityUser> implements SecurityUserDAO {

	private static final String ERR_41004 = "41004";

	public SecurityUserDAOImpl() {
		super();
	}

	@Override
	public SecurityUser getSecurityUserById(final long usrid, String type) {
		StringBuilder sql = getSecurityUserQuery(type);
		sql.append(" Where usrid = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new SecurityUserRM(type), usrid);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public SecurityUser getSecurityUserByLogin(final String usrLogin, String type) {
		StringBuilder sql = getSecurityUserQuery(type);
		sql.append(" Where UsrLogin = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new SecurityUserRM(type), usrLogin);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(SecurityUser su, String type) {
		StringBuilder sql = new StringBuilder("Delete From SecUsers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UsrId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		LoggedInUser ud = su.getUserDetails();

		try {
			if (this.jdbcOperations.update(sql.toString(), su.getId()) > 0) {
				return;
			}

			ErrorDetail error = getError("41003", su.getUsrLogin(), ud.getLanguage());
			throw new AppException(error.getError());
		} catch (DataAccessException e) {
			ErrorDetail error = getError("41006", su.getUsrLogin(), ud.getLanguage());
			throw new DependencyFoundException(error.getError());
		}
	}

	@Override
	public long save(SecurityUser su, String type) {
		StringBuilder sql = new StringBuilder("Insert Into SecUsers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(UsrID, UsrLogin, UsrPwd, UserStaffID, UsrFName, UsrMName, UsrLName, UsrMobile, UsrEmail");
		sql.append(", UsrEnabled, UsrCanSignonFrom, UsrCanSignonTo, UsrCanOverrideLimits, UsrAcExp, UsrAcLocked");
		sql.append(", UsrLanguage, UsrDftAppId, UsrDftAppCode, UsrBranchCode, UsrDeptCode, UsrToken");
		sql.append(", UsrIsMultiBranch, UsrInvldLoginTries, UsrAcExpDt, UsrDesg, AuthType");
		sql.append(", PwdExpDt, AccountUnLockedOn, BaseLocation");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId, businessvertical, LDAPDomainName, Deleted");
		sql.append(", DisableReason, EmployeeType");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(") Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			if (su.getId() == Long.MIN_VALUE) {
				su.setId(getNextValue("SeqSecUsers"));
			}

			ps.setLong(index++, su.getUsrID());
			ps.setString(index++, su.getUsrLogin());
			ps.setString(index++, su.getUsrPwd());
			ps.setString(index++, su.getUserStaffID());
			ps.setString(index++, su.getUsrFName());
			ps.setString(index++, su.getUsrMName());
			ps.setString(index++, su.getUsrLName());
			ps.setString(index++, su.getUsrMobile());
			ps.setString(index++, su.getUsrEmail());
			ps.setBoolean(index++, su.isUsrEnabled());
			ps.setDate(index++, JdbcUtil.getDate(su.getUsrCanSignonFrom()));
			ps.setDate(index++, JdbcUtil.getDate(su.getUsrCanSignonTo()));
			ps.setBoolean(index++, su.isUsrCanOverrideLimits());
			ps.setBoolean(index++, su.isUsrAcExp());
			ps.setBoolean(index++, su.isUsrAcLocked());
			ps.setString(index++, su.getUsrLanguage());
			ps.setObject(index++, su.getUsrDftAppId());
			ps.setString(index++, su.getUsrDftAppCode());
			ps.setString(index++, su.getUsrBranchCode());
			ps.setString(index++, su.getUsrDeptCode());
			ps.setString(index++, su.getUsrToken());
			ps.setBoolean(index++, su.isUsrIsMultiBranch());
			ps.setInt(index++, su.getUsrInvldLoginTries());
			ps.setDate(index++, JdbcUtil.getDate(su.getUsrAcExpDt()));
			ps.setString(index++, su.getUsrDesg());
			ps.setString(index++, su.getAuthType());
			ps.setDate(index++, JdbcUtil.getDate(su.getPwdExpDt()));
			ps.setDate(index++, JdbcUtil.getDate(su.getAccountUnLockedOn()));
			ps.setString(index++, su.getBaseLocation());
			ps.setInt(index++, su.getVersion());
			ps.setLong(index++, su.getLastMntBy());
			ps.setTimestamp(index++, su.getLastMntOn());
			ps.setString(index++, su.getRecordStatus());
			ps.setString(index++, su.getRoleCode());
			ps.setString(index++, su.getNextRoleCode());
			ps.setString(index++, su.getTaskId());
			ps.setString(index++, su.getNextTaskId());
			ps.setString(index++, su.getRecordType());
			ps.setLong(index++, su.getWorkflowId());
			ps.setObject(index++, su.getBusinessVertical());
			ps.setString(index++, su.getldapDomainName());
			ps.setBoolean(index++, su.isDeleted());
			ps.setString(index++, su.getDisableReason());
			ps.setString(index++, su.getEmployeeType());
			ps.setObject(index++, su.getCreatedBy());
			ps.setTimestamp(index++, su.getCreatedOn());
			ps.setObject(index++, su.getApprovedBy());
			ps.setTimestamp(index++, su.getApprovedOn());
		});

		return su.getId();
	}

	@Override
	public void update(SecurityUser su, String type) {
		StringBuilder sql = new StringBuilder("Update SecUsers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set UsrLogin = ?, UsrPwd = ?, UserStaffID = ?");
		sql.append(", UsrFName = ?, UsrMName = ?, UsrLName = ?, UsrMobile = ?");
		sql.append(", UsrEmail = ?, UsrEnabled = ?, UsrCanSignonFrom = ?");
		sql.append(", UsrCanSignonTo = ?, UsrCanOverrideLimits = ?, UsrAcExp = ?");
		sql.append(", UsrAcLocked = ?, UsrLanguage = ?, UsrDftAppId = ?, UsrAcExpDt = ?, UsrDftAppCode = ?");
		sql.append(", UsrBranchCode = ?, UsrDeptCode = ?, UsrIsMultiBranch = ?, UsrInvldLoginTries = ?");
		sql.append(", UsrDesg = ?, AuthType = ?, PwdExpDt = ?, BusinessVertical = ?");
		sql.append(", AccountLockedOn = ?, AccountUnLockedOn = ?, BaseLocation = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(", LDAPDomainName = ?, DisableReason = ?, EmployeeType = ?");
		sql.append(", ApprovedBy = ?, ApprovedOn = ?");
		sql.append("  Where UsrId = ?");

		if (StringUtils.isBlank(type)) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, su.getUsrLogin());
			ps.setString(index++, su.getUsrPwd());
			ps.setString(index++, su.getUserStaffID());
			ps.setString(index++, su.getUsrFName());
			ps.setString(index++, su.getUsrMName());
			ps.setString(index++, su.getUsrLName());
			ps.setString(index++, su.getUsrMobile());
			ps.setString(index++, su.getUsrEmail());
			ps.setBoolean(index++, su.isUsrEnabled());
			ps.setDate(index++, JdbcUtil.getDate(su.getUsrCanSignonFrom()));
			ps.setDate(index++, JdbcUtil.getDate(su.getUsrCanSignonTo()));
			ps.setBoolean(index++, su.isUsrCanOverrideLimits());
			ps.setBoolean(index++, su.isUsrAcExp());
			ps.setBoolean(index++, su.isUsrAcLocked());
			ps.setString(index++, su.getUsrLanguage());
			ps.setObject(index++, su.getUsrDftAppId());
			ps.setDate(index++, JdbcUtil.getDate(su.getUsrAcExpDt()));
			ps.setString(index++, su.getUsrDftAppCode());
			ps.setString(index++, su.getUsrBranchCode());
			ps.setString(index++, su.getUsrDeptCode());
			ps.setBoolean(index++, su.isUsrIsMultiBranch());
			ps.setInt(index++, su.getUsrInvldLoginTries());
			ps.setString(index++, su.getUsrDesg());
			ps.setString(index++, su.getAuthType());
			ps.setDate(index++, JdbcUtil.getDate(su.getPwdExpDt()));
			ps.setObject(index++, su.getBusinessVertical());
			ps.setDate(index++, JdbcUtil.getDate(su.getAccountLockedOn()));
			ps.setDate(index++, JdbcUtil.getDate(su.getAccountUnLockedOn()));
			ps.setString(index++, su.getBaseLocation());
			ps.setInt(index++, su.getVersion());
			ps.setLong(index++, su.getLastMntBy());
			ps.setTimestamp(index++, su.getLastMntOn());
			ps.setString(index++, su.getRecordStatus());
			ps.setString(index++, su.getRoleCode());
			ps.setString(index++, su.getNextRoleCode());
			ps.setString(index++, su.getTaskId());
			ps.setString(index++, su.getNextTaskId());
			ps.setString(index++, su.getRecordType());
			ps.setLong(index++, su.getWorkflowId());
			ps.setString(index++, su.getldapDomainName());
			ps.setString(index++, su.getDisableReason());
			ps.setString(index++, su.getEmployeeType());
			ps.setObject(index++, su.getApprovedBy());
			ps.setTimestamp(index++, su.getApprovedOn());

			ps.setLong(index++, su.getUsrID());

			if (StringUtils.isBlank(type)) {
				ps.setInt(index++, su.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			ErrorDetail error = getError(ERR_41004, su.getUsrLogin(), su.getUserDetails().getLanguage());
			throw new AppException(error.getError());
		}
	}

	public void changePassword(SecurityUser su) {
		StringBuilder sql = new StringBuilder("Update SecUsers Set");
		sql.append(" UsrPwd = ?, UsrToken = ?, UsrAcExpDt = ?, PwdExpDt = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus = ?");
		sql.append(" where UsrId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, su.getUsrPwd());
			ps.setString(index++, su.getUsrToken());
			ps.setDate(index++, JdbcUtil.getDate(su.getUsrAcExpDt()));
			ps.setDate(index++, JdbcUtil.getDate(su.getPwdExpDt()));
			ps.setInt(index++, su.getVersion());
			ps.setLong(index++, su.getLastMntBy());
			ps.setTimestamp(index++, su.getLastMntOn());
			ps.setString(index++, su.getRecordStatus());

			ps.setLong(index++, su.getUsrID());
		});

		if (recordCount <= 0) {
			ErrorDetail error = getError(ERR_41004, su.getUsrLogin(), su.getUserDetails().getLanguage());
			throw new AppException(error.getError());
		}
		logger.debug(Literal.LEAVING);
	}

	private ErrorDetail getError(String errorId, String userLogin, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = userLogin;
		parms[0][0] = PennantJavaUtil.getLabel("label_UsrLogin") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]),
				userLanguage);
	}

	@Override
	public SecurityUserDivBranch getSecUserDivBrDetailsById(SecurityUserDivBranch divBranch, String type) {
		StringBuilder sql = new StringBuilder("Select UsrID, UserDivision, UserBranch");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", UserBranchDesc");
		}

		sql.append(" From SecurityUserDivBranch");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UsrID = ? and UserDivision = ? and UserBranch = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				SecurityUserDivBranch div = new SecurityUserDivBranch();

				div.setUsrID(rs.getLong("UsrID"));
				div.setUserDivision(rs.getString("UserDivision"));
				div.setUserBranch(rs.getString("UserBranch"));
				div.setVersion(rs.getInt("Version"));
				div.setLastMntBy(rs.getLong("LastMntBy"));
				div.setLastMntOn(rs.getTimestamp("LastMntOn"));
				div.setRecordStatus(rs.getString("RecordStatus"));
				div.setRoleCode(rs.getString("RoleCode"));
				div.setNextRoleCode(rs.getString("NextRoleCode"));
				div.setTaskId(rs.getString("TaskId"));
				div.setNextTaskId(rs.getString("NextTaskId"));
				div.setRecordType(rs.getString("RecordType"));
				div.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					div.setUserBranchDesc(rs.getString("UserBranchDesc"));
				}

				return div;
			}, divBranch.getUsrID(), divBranch.getUserDivision(), divBranch.getUserBranch());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long saveDivBranchDetails(SecurityUserDivBranch divBranch, String type) {
		List<SecurityUserDivBranch> list = new ArrayList<>();
		list.add(divBranch);

		saveDivBranchDetails(list, type);

		return divBranch.getId();
	}

	@Override
	public void saveDivBranchDetails(List<SecurityUserDivBranch> list, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert into SecurityUserDivBranch");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(UsrID, UserDivision, UserBranch");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					int index = 1;

					SecurityUserDivBranch divBranch = list.get(i);

					ps.setLong(index++, divBranch.getUsrID());
					ps.setString(index++, divBranch.getUserDivision());
					ps.setString(index++, divBranch.getUserBranch());
					ps.setInt(index++, divBranch.getVersion());
					ps.setLong(index++, divBranch.getLastMntBy());
					ps.setTimestamp(index++, divBranch.getLastMntOn());
					ps.setString(index++, divBranch.getRecordStatus());
					ps.setString(index++, divBranch.getRoleCode());
					ps.setString(index++, divBranch.getNextRoleCode());
					ps.setString(index++, divBranch.getTaskId());
					ps.setString(index++, divBranch.getNextTaskId());
					ps.setString(index++, divBranch.getRecordType());
					ps.setLong(index, divBranch.getWorkflowId());
				}

				@Override
				public int getBatchSize() {
					return list.size();
				}
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void updateDivBranchDetails(SecurityUserDivBranch divBranch, String type) {
		StringBuilder sql = new StringBuilder("Update SecurityUserDivBranch");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Version = ? , LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus= ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" where UsrID = ? and UserDivision = ? and UserBranch = ?");
		sql.append("");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, divBranch.getVersion());
			ps.setLong(index++, divBranch.getLastMntBy());
			ps.setTimestamp(index++, divBranch.getLastMntOn());
			ps.setString(index++, divBranch.getRecordStatus());
			ps.setString(index++, divBranch.getRoleCode());
			ps.setString(index++, divBranch.getNextRoleCode());
			ps.setString(index++, divBranch.getTaskId());
			ps.setString(index++, divBranch.getNextTaskId());
			ps.setString(index++, divBranch.getRecordType());
			ps.setLong(index, divBranch.getWorkflowId());

			ps.setLong(index++, divBranch.getUsrID());
			ps.setString(index++, divBranch.getUserDivision());
			ps.setString(index++, divBranch.getUserBranch());

		});

		if (recordCount <= 0) {
			LoggedInUser details = divBranch.getUserDetails();
			String usrID = String.valueOf(divBranch.getUsrID());
			ErrorDetail error = getError(ERR_41004, usrID, details.getLanguage());
			throw new AppException(error.getError());
		}
	}

	@Override
	public void deleteDivBranchDetails(SecurityUserDivBranch divBranch, String type) {
		StringBuilder sql = new StringBuilder("Delete From SecurityUserDivBranch");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UsrID = ? and UserDivision = ? and UserBranch = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		LoggedInUser details = divBranch.getUserDetails();

		String usrID = String.valueOf(divBranch.getUsrID());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, divBranch.getUsrID());
				ps.setString(index++, divBranch.getUserDivision());
				ps.setString(index++, divBranch.getUserBranch());
			});

			if (recordCount <= 0) {
				ErrorDetail error = getError("41003", usrID, details.getLanguage());
				throw new AppException(error.getError());
			}
		} catch (DataAccessException e) {
			ErrorDetail error = getError("41006", usrID, details.getLanguage());
			throw new DependencyFoundException(error.getError());
		}
	}

	private List<SecurityUserDivBranch> getDivisionBasedNonCluster(long usrID, String type) {
		StringBuilder sql = new StringBuilder("Select UsrID, UserDivision, UserBranch");
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From SecurityUserDivBranch");
		sql.append(type);
		sql.append(" Where  UsrID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			SecurityUserDivBranch sudb = new SecurityUserDivBranch();

			sudb.setUsrID(rs.getLong("UsrID"));
			sudb.setUserDivision(rs.getString("UserDivision"));
			sudb.setUserBranch(rs.getString("UserBranch"));
			sudb.setVersion(rs.getInt("Version"));
			sudb.setLastMntBy(rs.getLong("LastMntBy"));
			sudb.setLastMntOn(rs.getTimestamp("LastMntOn"));
			sudb.setRecordStatus(rs.getString("RecordStatus"));
			sudb.setRoleCode(rs.getString("RoleCode"));
			sudb.setNextRoleCode(rs.getString("NextRoleCode"));
			sudb.setTaskId(rs.getString("TaskId"));
			sudb.setNextTaskId(rs.getString("NextTaskId"));
			sudb.setRecordType(rs.getString("RecordType"));
			sudb.setWorkflowId(rs.getLong("WorkflowId"));

			return sudb;
		}, usrID);
	}

	private List<SecurityUserDivBranch> getDivisionBasedCluster(long usrID) {
		StringBuilder sql = new StringBuilder("Select ua.UsrID, ua.Division, dd.DivisionCodeDesc");
		sql.append(", ua.Branch, b.BranchDesc, ua.AccessType, ua.ClusterId, ua.ClusterType");
		sql.append(", cl.Code, cl.Name, ua.Entity, e.EntityDesc, ua.ParentCluster");
		sql.append(", ua.ParentClusterType, clp.Code ParentClusterCode, clp.Name ParentClusterName");
		sql.append(" From SecUserAccess ua");
		sql.append(" Inner Join SMTDivisionDetail dd ON dd.DivisionCode = ua.Division");
		sql.append(" Left Join RMTBranches b ON b.BranchCode = ua.Branch");
		sql.append(" Left Join Entity e ON e.EntityCode = ua.Entity");
		sql.append(" Left Join Clusters cl ON cl.Id = ua.ClusterID");
		sql.append(" Left Join Clusters clp ON clp.Id = ua.ParentCluster");
		sql.append(" Left Join Cluster_Hierarchy ch ON ch.Entity = ua.Entity and ch.ClusterType = ua.ClusterType");
		sql.append(" Where UsrID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			SecurityUserDivBranch sudb = new SecurityUserDivBranch();

			sudb.setUsrID(rs.getLong("UsrID"));
			sudb.setUserDivision(rs.getString("Division"));
			sudb.setDivisionDesc(rs.getString("DivisionCodeDesc"));
			sudb.setUserBranch(rs.getString("Branch"));
			sudb.setBranchDesc(rs.getString("BranchDesc"));
			sudb.setAccessType(rs.getString("AccessType"));
			sudb.setClusterId(JdbcUtil.getLong(rs.getObject("ClusterId")));
			sudb.setClusterType(rs.getString("ClusterType"));
			sudb.setClusterCode(rs.getString("Code"));
			sudb.setClusterName(rs.getString("Name"));
			sudb.setEntity(rs.getString("Entity"));
			sudb.setEntityDesc(rs.getString("EntityDesc"));
			sudb.setParentCluster(JdbcUtil.getLong(rs.getObject("ParentCluster")));
			sudb.setParentClusterType(rs.getString("ParentClusterType"));
			sudb.setParentClusterCode(rs.getString("ParentClusterCode"));
			sudb.setParentClusterName(rs.getString("ParentClusterName"));

			return sudb;
		}, usrID);
	}

	@Override
	public List<SecurityUserDivBranch> getSecUserDivBrList(long usrID, String type) {
		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
			return getDivisionBasedCluster(usrID);
		} else {
			return getDivisionBasedNonCluster(usrID, type);
		}
	}

	@Override
	public void deleteBranchs(SecurityUser securityUser, String type) {
		StringBuilder sql = new StringBuilder("Delete From SecurityUserDivBranch");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UsrID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, securityUser.getUsrID()));
	}

	@Override
	public int getActiveUsersCount(long userId) {
		String sql = "Select count(UsrId) From Secusers Where UsrID <> ? and UsrEnabled = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, userId, 1);
	}

	@Override
	public int getActiveUsersCount() {
		String sql = "Select count(UsrEnabled) From Secusers Where UsrEnabled = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, 1);
	}

	@Override
	public List<Entity> getEntityList(String entity) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select distinct e.EntityCode, e.EntityDesc");
		sql.append(" From Entity e");
		sql.append(" Inner Join smtdivisiondetail d on d.EntityCode = e.EntityCode");
		sql.append(" where e.EntityCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			Entity e = new Entity();

			e.setEntityCode(rs.getString("EntityCode"));
			e.setEntityDesc(rs.getString("EntityDesc"));

			return e;
		}, entity);
	}

	@Override
	public long getUserByName(String userName) {
		String sql = "Select UsrID From SecUsers Where UsrLogin = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, userName);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public List<SecurityUser> getSecUsersByRoles(String[] roles) {
		if (roles == null || roles.length == 0) {
			return new ArrayList<>();
		}

		List<String> asList = Arrays.asList(roles);

		StringBuilder sql = new StringBuilder("Select u.UsrID, u.UsrLogin, u.UsrFName");
		sql.append(", u.UsrMName, u.UsrLName, u.UsrMobile, u.UsrEmail");
		sql.append(", u.UsrBranchCode, b.BranchDesc, u.LDAPDomainName");
		sql.append(" From SecUsers u");
		sql.append(" Left Join RMTBranches b on b.BranchCode = u.UsrBranchCode");
		sql.append(" Inner Join SecuserOPerations uop on uop.UsrID = u.UsrID");
		sql.append(" Inner Join secOPerationRoles opr on opr.OprID = uop.OprID");
		sql.append(" Where opr.RoleID in (Select RoleId from SecRoles Where RoleCD in (");
		sql.append(commaJoin(asList));
		sql.append("))");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (String role : asList) {
				ps.setString(index++, role);
			}
		}, (rs, rowNum) -> {
			SecurityUser su = new SecurityUser();

			su.setUsrID(rs.getLong("UsrID"));
			su.setUsrLogin(rs.getString("UsrLogin"));
			su.setUsrFName(rs.getString("UsrFName"));
			su.setUsrMName(rs.getString("UsrMName"));
			su.setUsrLName(rs.getString("UsrLName"));
			su.setUsrMobile(rs.getString("UsrMobile"));
			su.setUsrEmail(rs.getString("UsrEmail"));
			su.setUsrBranchCode(rs.getString("UsrBranchCode"));
			su.setLovDescUsrBranchCodeName(rs.getString("BranchDesc"));
			su.setldapDomainName(rs.getString("LDAPDomainName"));

			return su;
		});
	}

	private String commaJoin(List<String> list) {
		return list.stream().map(e -> "?").collect(Collectors.joining(","));
	}

	@Override
	public SecurityUser getSecurityUserAccessToAllBranches(long id) {
		SecurityUser su = new SecurityUser();

		String sql = "Select AccessToAllBranches, LovDescUsrBranchCodeName From SecUsers_AVIEW  Where UsrID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				su.setAccessToAllBranches(rs.getBoolean("AccessToAllBranches"));
				su.setLovDescUsrBranchCodeName(rs.getString("LovDescUsrBranchCodeName"));

				return su;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			su.setUsrID(0);
		}

		return su;
	}

	private void updateLockUser(List<? extends SecurityUser> userAccounts) {
		String sql = "Update SecUsers set UsrAcLocked = ?, AccountLockedOn = ?, AccountUnLockedOn = ? where UsrID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				SecurityUser su = userAccounts.get(i);

				ps.setInt(1, 1);
				ps.setDate(2, JdbcUtil.getDate(su.getAccountLockedOn()));
				ps.setDate(3, null);
				ps.setLong(4, su.getUsrID());
			}

			@Override
			public int getBatchSize() {
				return userAccounts.size();
			}
		});
	}

	@Override
	public void lockUserAccounts() {
		StringBuilder sql = new StringBuilder();
		sql.append("Select UsrID, LastLoginOn, AccountUnLockedOn From SecUsers");
		sql.append(" where UsrAcLocked = 0 and UserType = :UserType");

		logger.debug(Literal.SQL.concat(sql.toString()));

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("UserType", "USER");

		int days = SysParamUtil.getValueAsInt(SMTParameterConstants.USR_ACCT_LOCK_DAYS);

		Date sysDate = DateUtil.getSysDate();
		List<SecurityUser> userAccounts = new ArrayList<>();
		this.jdbcTemplate.query(sql.toString(), parameterSource, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {

				SecurityUser secUsersData = new SecurityUser();
				Long userId = rs.getLong(1);
				Date lastLoginOn = rs.getDate(2);
				Date accountUnlockedOn = rs.getDate(3);

				Date startDate = null;
				if (accountUnlockedOn == null) {
					startDate = lastLoginOn;
				} else {
					startDate = accountUnlockedOn;
				}

				if (DateUtil.getDaysBetween(sysDate, startDate) > days) {
					secUsersData.setUsrID(userId);
					secUsersData.setUsrAcLocked(true);
					secUsersData.setAccountLockedOn(DateUtil.getSysDate());
					userAccounts.add(secUsersData);
				}

				if (userAccounts.size() > 1000) {
					// call batch update method
					updateLockUser(userAccounts);
					userAccounts.clear();
				}
			}
		});

		if (CollectionUtils.isNotEmpty(userAccounts)) {
			// call batch update method
			updateLockUser(userAccounts);
			userAccounts.clear();
		}
	}

	@Override
	public void updateDisableUser(List<SecurityUser> userAccounts) {
		String sql = "Update SecUsers Set UsrEnabled = ?, DisableReason = ? Where UsrID = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				SecurityUser su = userAccounts.get(i);

				int index = 1;

				ps.setBoolean(index++, su.isUsrEnabled());
				ps.setString(index++, su.getDisableReason());

				ps.setLong(index, su.getUsrID());
			}

			@Override
			public int getBatchSize() {
				return userAccounts.size();
			}
		});
	}

	@Override
	public List<SecurityUser> getDisableUserAccounts() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" UsrID, LastLoginOn, CreatedOn, UsrEnabled");
		sql.append(" From SecUsers");
		sql.append(" Where UsrEnabled = ? and UserType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setBoolean(1, true);
			ps.setString(2, "USER");
		}, (rs, num) -> {
			SecurityUser su = new SecurityUser();

			su.setUsrID(rs.getLong("UsrID"));
			su.setLastLoginOn(rs.getTimestamp("LastLoginOn"));
			su.setCreatedOn(rs.getTimestamp("CreatedOn"));
			su.setUsrEnabled(rs.getBoolean("UsrEnabled"));

			return su;
		});
	}

	private class SecurityUserRM implements RowMapper<SecurityUser> {
		private String type;

		private SecurityUserRM(String type) {
			this.type = type;
		}

		@Override
		public SecurityUser mapRow(ResultSet rs, int rowNum) throws SQLException {

			SecurityUser su = new SecurityUser();

			su.setUsrID(rs.getLong("UsrID"));
			su.setUsrLogin(rs.getString("UsrLogin"));
			su.setUsrPwd(rs.getString("UsrPwd"));
			su.setUserStaffID(rs.getString("UserStaffID"));
			su.setUsrFName(rs.getString("UsrFName"));
			su.setUsrMName(rs.getString("UsrMName"));
			su.setUsrLName(rs.getString("UsrLName"));
			su.setUsrMobile(rs.getString("UsrMobile"));
			su.setUsrEmail(rs.getString("UsrEmail"));
			su.setUsrEnabled(rs.getBoolean("UsrEnabled"));
			su.setUsrCanSignonFrom(rs.getTimestamp("UsrCanSignonFrom"));
			su.setUsrCanSignonTo(rs.getTimestamp("UsrCanSignonTo"));
			su.setUsrCanOverrideLimits(rs.getBoolean("UsrCanOverrideLimits"));
			su.setUsrAcExp(rs.getBoolean("UsrAcExp"));
			su.setUsrAcExpDt(rs.getTimestamp("UsrAcExpDt"));
			su.setUsrAcLocked(rs.getBoolean("UsrAcLocked"));
			su.setUsrLanguage(rs.getString("UsrLanguage"));
			su.setUsrDftAppId(rs.getLong("UsrDftAppId"));
			su.setUsrBranchCode(rs.getString("UsrBranchCode"));
			su.setUsrDeptCode(rs.getString("UsrDeptCode"));
			su.setUsrToken(rs.getString("UsrToken"));
			su.setUsrIsMultiBranch(rs.getBoolean("UsrIsMultiBranch"));
			su.setUsrInvldLoginTries(rs.getInt("UsrInvldLoginTries"));
			su.setUsrDesg(rs.getString("UsrDesg"));
			su.setAuthType(rs.getString("AuthType"));
			su.setUsrDftAppCode(rs.getString("UsrDftAppCode"));
			su.setPwdExpDt(rs.getTimestamp("PwdExpDt"));
			su.setUserType(rs.getString("UserType"));
			su.setBusinessVertical(JdbcUtil.getLong(rs.getObject("BusinessVertical")));
			su.setldapDomainName(rs.getString("LdapDomainName"));
			su.setDeleted(rs.getBoolean("Deleted"));
			su.setDisableReason(rs.getString("DisableReason"));
			su.setEmployeeType(rs.getString("EmployeeType"));
			su.setBaseLocation(rs.getString("BaseLocation"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				su.setLovDescUsrDftAppCodeName(rs.getString("LovDescUsrDftAppCodeName"));
				su.setLovDescUsrDeptCodeName(rs.getString("LovDescUsrDeptCodeName"));
				su.setLovDescUsrBranchCodeName(rs.getString("LovDescUsrBranchCodeName"));
				su.setLovDescUsrLanguage(rs.getString("LovDescUsrLanguage"));
				su.setLovDescUsrDesg(rs.getString("LovDescUsrDesg"));
				su.setBusinessVerticalCode(rs.getString("BusinessVerticalCode"));
				su.setBusinessVerticalDesc(rs.getString("BusinessVerticalDesc"));
			}

			su.setVersion(rs.getInt("Version"));
			su.setLastMntBy(rs.getLong("LastMntBy"));
			su.setLastMntOn(rs.getTimestamp("LastMntOn"));
			su.setRecordStatus(rs.getString("RecordStatus"));
			su.setRoleCode(rs.getString("RoleCode"));
			su.setNextRoleCode(rs.getString("NextRoleCode"));
			su.setTaskId(rs.getString("TaskId"));
			su.setNextTaskId(rs.getString("NextTaskId"));
			su.setRecordType(rs.getString("RecordType"));
			su.setWorkflowId(rs.getLong("WorkflowId"));
			su.setCreatedBy(JdbcUtil.getLong(rs.getObject("CreatedBy")));
			su.setCreatedOn(rs.getTimestamp("CreatedOn"));
			su.setApprovedBy(JdbcUtil.getLong(rs.getObject("ApprovedBy")));
			su.setApprovedOn(rs.getTimestamp("ApprovedOn"));

			return su;
		}
	}

	@Override
	public void markAsDelete(SecurityUser seu, String type) {
		String sql = "Update SecUsers Set UsrEnabled = ?, Deleted = ? Where Usrid = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;
			ps.setBoolean(index++, seu.isUsrEnabled());
			ps.setBoolean(index++, seu.isDeleted());
			ps.setLong(index, seu.getUsrID());
		});
	}

	@Override
	public boolean isUserExist(String usrLogin) {
		String sql = "Select count(UsrLogin) from SecUsers Where UsrLogin = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Integer.class, usrLogin) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public void updateUserStatus(SecurityUser user) {
		StringBuilder sql = new StringBuilder("Update SecUsers");
		sql.append(" Set UsrEnabled = ?, UsrAcExp = ?, DisableReason = ?, Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(" Where UsrID = ? and Version = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBoolean(index++, user.isUsrEnabled());
			ps.setBoolean(index++, user.isUsrAcExp());
			ps.setString(index++, user.getDisableReason());
			ps.setInt(index++, user.getVersion());
			ps.setLong(index++, user.getLastMntBy());
			ps.setTimestamp(index++, user.getLastMntOn());

			ps.setLong(index++, user.getUsrID());
			ps.setInt(index++, user.getVersion() - 1);
		});
	}

	@Override
	public List<String> getLovFieldCodeValues(String lovFieldCode) {
		String sql = "Select FieldCodeValue FROM RMTLovFieldDetail Where FieldCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForList(sql, String.class, lovFieldCode);
	}

	private StringBuilder getSecurityUserQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" UsrID, UsrLogin, UsrPwd, UserStaffID, UsrFName, UsrMName, UsrLName, UsrMobile");
		sql.append(", UsrEmail, UsrEnabled, UsrCanSignonFrom, UsrCanSignonTo, UsrCanOverrideLimits");
		sql.append(", UsrAcExp, UsrAcExpDt, UsrAcLocked, UsrLanguage, UsrDftAppId, UsrBranchCode, UsrDeptCode");
		sql.append(", UsrToken, UsrIsMultiBranch, UsrInvldLoginTries, UsrDesg, AuthType, UsrDftAppCode, BaseLocation");
		sql.append(", PwdExpDt, UserType, BusinessVertical, LdapDomainName, Deleted, DisableReason, EmployeeType");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescUsrDftAppCode, LovDescUsrDftAppCodeName, LovDescUsrDeptCodeName");
			sql.append(", LovDescUsrBranchCodeName, LovDescUsrLanguage, LovDescUsrDesg");
			sql.append(", BusinessVerticalCode, BusinessVerticalDesc");
		}

		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(" From SecUsers");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	public boolean isexisitvertical(long id) {
		String sql = "Select Count(UsrID) From SecUsers Where BusinessVertical = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, id) > 0;
	}

	@Override
	public boolean isexisitBranchCode(String branchCode) {
		String sql = "Select Count(UsrID) From SecUsers Where UsrBranchCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, branchCode) > 0;
	}

	@Override
	public boolean isDepartmentExsist(String deptCode) {
		String sql = "Select Count(UsrID) From SecUsers Where UsrDeptCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, deptCode) > 0;
	}

	@Override
	public boolean getDesignationCount(String usrDesg) {
		String sql = "Select Count(UsrID) From SecUsers Where usrDesg = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, usrDesg) > 0;
	}
}