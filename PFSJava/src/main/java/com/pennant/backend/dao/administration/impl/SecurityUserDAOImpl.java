/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  SecurityUserDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  2-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *2-08-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.administration.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.app.util.DateUtility;
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
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>SecurityUsers model</b> class.<br>
 * 
 */
public class SecurityUserDAOImpl extends SequenceDao<SecurityUser> implements SecurityUserDAO {
	private static Logger logger = LogManager.getLogger(SecurityUserDAOImpl.class);

	public SecurityUserDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Security Users details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return SecurityUsers
	 */
	@Override
	public SecurityUser getSecurityUserById(final long usrid, String type) {
		StringBuilder sql = getSecurityUserQuery(type);
		sql.append(" where usrid = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { usrid }, (rs, rowNum) -> {
				return getTypeRowMapper(rs, type);
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records are not found in secusers{} for the user Id >> {} ", type, usrid);
		}
		return null;
	}

	// DE#374 :User Creation : After approving the user, work-flow before and after image values not coming properly in
	// audit table.
	private StringBuilder getSecurityUserQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" UsrID, UsrLogin, UsrPwd, UserStaffID, UsrFName, UsrMName, UsrLName, UsrMobile");
		sql.append(", UsrEmail, UsrEnabled, UsrCanSignonFrom, UsrCanSignonTo, UsrCanOverrideLimits");
		sql.append(", UsrAcExp, UsrAcExpDt, UsrAcLocked, UsrLanguage, UsrDftAppId, UsrBranchCode, UsrDeptCode");
		sql.append(", UsrToken, UsrIsMultiBranch, UsrInvldLoginTries, UsrDesg, AuthType, UsrDftAppCode");
		sql.append(", PwdExpDt, UserType, BusinessVertical, LdapDomainName");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescUsrDftAppCode, LovDescUsrDftAppCodeName, LovDescUsrDeptCodeName");
			sql.append(", LovDescUsrBranchCodeName, LovDescUsrLanguage, LovDescUsrDesg");
			sql.append(", BusinessVerticalCode, BusinessVerticalDesc");
		}
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from secusers");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	/**
	 * Fetch the Record Security Users details by key field
	 * 
	 * @param usrLogin (String)
	 * @param type     (String) ""/_Temp/_View
	 * @return SecurityUsers
	 */
	@Override
	public SecurityUser getSecurityUserByLogin(final String usrLogin, String type) {
		StringBuilder sql = getSecurityUserQuery(type);
		sql.append(" where usrlogin = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { usrLogin }, (rs, rowNum) -> {
				return getTypeRowMapper(rs, type);
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records are not found in secusers{} for the user Login >> {} ", type, usrLogin);
		}

		return null;
	}

	/**
	 * This method Deletes the Record from the SecUsers or SecUsers_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Security Users by key UsrID
	 * 
	 * @param Security Users (securityUsers)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(SecurityUser securityUser, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Delete From SecUsers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where usrid =:usrID");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		logger.trace(Literal.SQL + sql.toString());

		try {
			recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetail errorDetails = getError("41003", securityUser.getUsrLogin(),
						securityUser.getUserDetails().getLanguage());
				throw new AppException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			ErrorDetail errorDetails = getError("41006", securityUser.getUsrLogin(),
					securityUser.getUserDetails().getLanguage());
			throw new AppException(errorDetails.getError()) {
			};
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into SecUsers or SecUsers_Temp. it fetches the available Sequence form SeqSecUsers
	 * by using getNextidviewDAO().getNextId() method.
	 *
	 * save Security Users
	 * 
	 * @param Security Users (securityUsers)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(SecurityUser securityUser, String type) {
		logger.debug(Literal.ENTERING);
		if (securityUser.getId() == Long.MIN_VALUE) {
			securityUser.setId(getNextValue("SeqSecUsers"));
			logger.debug("get NextValue:" + securityUser.getId());
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into SecUsers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(UsrID, UsrLogin, UsrPwd, UserStaffID, UsrFName, UsrMName, UsrLName, UsrMobile, UsrEmail");
		sql.append(", UsrEnabled, UsrCanSignonFrom, UsrCanSignonTo, UsrCanOverrideLimits, UsrAcExp, UsrAcLocked");
		sql.append(", UsrLanguage, UsrDftAppId, UsrDftAppCode, UsrBranchCode, UsrDeptCode, UsrToken");
		sql.append(", UsrIsMultiBranch, UsrInvldLoginTries, UsrAcExpDt, UsrDesg, AuthType");
		sql.append(", PwdExpDt, AccountUnLockedOn");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, businessvertical, LDAPDomainName)");
		sql.append(" Values(:UsrID, :UsrLogin, :UsrPwd, :UserStaffID, :UsrFName, :UsrMName, :UsrLName, :UsrMobile");
		sql.append(", :UsrEmail, :UsrEnabled, :UsrCanSignonFrom, :UsrCanSignonTo, :UsrCanOverrideLimits, :UsrAcExp");
		sql.append(", :UsrAcLocked, :UsrLanguage, :UsrDftAppId, :UsrDftAppCode, :UsrBranchCode, :UsrDeptCode");
		sql.append(", :UsrToken, :UsrIsMultiBranch, :UsrInvldLoginTries, :UsrAcExpDt, :UsrDesg, :AuthType");
		sql.append(", :PwdExpDt, :AccountUnLockedOn");
		sql.append(", :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId, :businessVertical, :ldapDomainName)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return securityUser.getId();
	}

	/**
	 * This method updates the Record SecUsers or SecUsers_Temp. if Record not updated then throws DataAccessException
	 * with error 41004. update Security Users by key UsrID and Version
	 * 
	 * @param SecurityUsers (securityUser)
	 * @param type          (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(SecurityUser securityUser, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Update SecUsers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" set UsrLogin = :UsrLogin, UsrPwd = :UsrPwd, UserStaffID = :UserStaffID");
		sql.append(", UsrFName = :UsrFName, UsrMName = :UsrMName, UsrLName = :UsrLName, UsrMobile = :UsrMobile");
		sql.append(", UsrEmail = :UsrEmail, UsrEnabled = :UsrEnabled, UsrCanSignonFrom = :UsrCanSignonFrom");
		sql.append(", UsrCanSignonTo = :UsrCanSignonTo, UsrCanOverrideLimits = :UsrCanOverrideLimits");
		sql.append(", UsrAcExp = :UsrAcExp, UsrAcLocked = :UsrAcLocked, UsrLanguage = :UsrLanguage");
		sql.append(", UsrDftAppId = :UsrDftAppId, UsrAcExpDt = :UsrAcExpDt, UsrDftAppCode = :UsrDftAppCode");
		sql.append(", UsrBranchCode = :UsrBranchCode, UsrDeptCode = :UsrDeptCode");
		sql.append(", UsrIsMultiBranch = :UsrIsMultiBranch, UsrInvldLoginTries = :UsrInvldLoginTries");
		sql.append(", UsrDesg = :UsrDesg, AuthType = :AuthType, PwdExpDt = :PwdExpDt");
		sql.append(", BusinessVertical= :BusinessVertical");
		sql.append(", AccountLockedOn =:AccountLockedOn, AccountUnLockedOn =:AccountUnLockedOn");
		sql.append(", Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus = :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType");
		sql.append(", WorkflowId = :WorkflowId, LDAPDomainName = :ldapDomainName");

		sql.append(" Where UsrID =:UsrID");

		if (StringUtils.isBlank(type)) {
			sql.append(" AND Version= :Version-1");
		}

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			ErrorDetail errorDetails = getError("41004", securityUser.getUsrLogin(),
					securityUser.getUserDetails().getLanguage());
			throw new AppException(errorDetails.getError()) {
			};
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method updates the UsrPwd,UsrToken,UsrAcExpDt fields of securityUser record if Record not updated then
	 * throws DataAccessException
	 * 
	 * @param securityUser (SecurityUsers)
	 */
	public void changePassword(SecurityUser securityUser) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Update SecUsers");
		sql.append(" set UsrPwd = :UsrPwd, UsrToken = :UsrToken, UsrAcExpDt = :UsrAcExpDt, PwdExpDt = :PwdExpDt");
		sql.append(", Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus = :RecordStatus");
		sql.append(" Where UsrID =:UsrID");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			ErrorDetail errorDetails = getError("41004", securityUser.getUsrLogin(),
					securityUser.getUserDetails().getLanguage());
			throw new AppException(errorDetails.getError());
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

	/**
	 * Fetch the Record Security User Division Branch Details by key fields
	 * 
	 */
	@Override
	public SecurityUserDivBranch getSecUserDivBrDetailsById(SecurityUserDivBranch securityUserDivBranch, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" Select UsrID, UserDivision, UserBranch, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(",UserBranchDesc ");
		}
		sql.append(" From SecurityUserDivBranch");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UsrID = :UsrID And UserDivision = :UserDivision And UserBranch =:UserBranch");

		logger.debug("selectSql:" + sql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUserDivBranch);
		RowMapper<SecurityUserDivBranch> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityUserDivBranch.class);

		try {
			securityUserDivBranch = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			securityUserDivBranch = null;
		}
		logger.debug(Literal.LEAVING);
		return securityUserDivBranch;
	}

	/**
	 * This method is to Save SecurityUser Division Branch Details
	 */
	public long saveDivBranchDetails(SecurityUserDivBranch securityUserDivBranch, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append("insert into SecurityUserDivBranch");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(UsrID, UserDivision, UserBranch");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" values(:UsrID, :UserDivision, :UserBranch");
		sql.append(", :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUserDivBranch);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return securityUserDivBranch.getId();
	}

	/**
	 * This method is to Save SecurityUser Division Branch Details
	 */
	public void saveDivBranchDetails(List<SecurityUserDivBranch> list, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("insert into SecurityUserDivBranch");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(UsrID, UserDivision, UserBranch");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");

		sql.append("values(:UsrID, :UserDivision, :UserBranch");
		sql.append(", :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(list.toArray()));
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is to Update SecurityUser Division Branch Details
	 */
	@SuppressWarnings("serial")
	@Override
	public void updateDivBranchDetails(SecurityUserDivBranch securityUserDivBranch, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Update SecurityUserDivBranch");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where UsrID = :UsrID and UserDivision = :UserDivision");
		sql.append(" and UserBranch =:UserBranch");
		// sql.append(" and entity = :entity");

		logger.debug("updateSql:" + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUserDivBranch);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetail errorDetails = getError("41004", String.valueOf(securityUserDivBranch.getUsrID()),
					securityUserDivBranch.getUserDetails().getLanguage());
			throw new AppException(errorDetails.getError()) {
			};
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is to Delete Each Branch Under Division
	 */
	@SuppressWarnings("serial")
	@Override
	public void deleteDivBranchDetails(SecurityUserDivBranch securityUserDivBranch, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Delete From SecurityUserDivBranch");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UsrID = :UsrID And UserDivision = :UserDivision And UserBranch =:UserBranch");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUserDivBranch);
		logger.debug("deleteSql:" + sql.toString());

		try {
			recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetail errorDetails = getError("41003", String.valueOf(securityUserDivBranch.getUsrID()),
						securityUserDivBranch.getUserDetails().getLanguage());
				throw new AppException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			ErrorDetail errorDetails = getError("41006", String.valueOf(securityUserDivBranch.getUsrID()),
					securityUserDivBranch.getUserDetails().getLanguage());
			throw new AppException(errorDetails.getError()) {
			};
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is to Fetch SecurityUser Division Branch Details
	 */
	@Override
	public List<SecurityUserDivBranch> getSecUserDivBrList(long usrID, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();

		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
			sql.append("select t1.usrid, t1.division UserDivision, dd.divisionCodeDesc divisionDesc");
			sql.append(", t1.branch UserBranch, t2.branchdesc, t1.accessType, t1.ClusterId, t1.ClusterType");
			sql.append(", t4.code ClusterCode, t4.Name ClusterNmae, t1.Entity, t3.EntityDesc");
			sql.append(
					", t1.ParentCluster, t1.ParentClusterType, t5.code ParentClusterCode, t5.Name ParentClusterName");
			sql.append(" FROM SecUserAccess t1");
			sql.append(" inner join SMTDivisionDetail dd ON dd.DivisionCode = t1.division");
			sql.append(" left join rmtbranches t2 ON t1.branch = t2.branchcode");
			sql.append(" left join entity t3 ON t3.entitycode = t1.entity");
			sql.append(" left join clusters t4 ON t4.Id = t1.clusterId");
			sql.append(" left join clusters t5 ON t5.Id = t1.parentCluster");
			sql.append(" left join cluster_Hierarchy t6 ON t6.entity = t1.entity and t6.clusterType = t1.clusterType");
		} else {
			sql.append("select usrId, userDivision, UserBranch");
			sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode,");
			sql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
			sql.append(" FROM  SecurityUserDivBranch");
			sql.append(type);
		}
		sql.append(" Where UsrID = :UsrID");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();

		parameterSource.addValue("UsrID", usrID);

		RowMapper<SecurityUserDivBranch> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityUserDivBranch.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * This method is to Delete SecurityUser Division Branch Details Under User
	 */
	@Override
	public void deleteBranchs(SecurityUser securityUser, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete From SecurityUserDivBranch");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UsrID = :UsrID");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public int getActiveUsersCount(long userId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String sql = "select count(usrId) from secusers where usrId <> :usrId and usrenabled = :usrenabled";

		paramMap.addValue("usrId", userId);
		paramMap.addValue("usrenabled", 1);

		return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
	}

	@Override
	public int getActiveUsersCount() {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String sql = "select count(usrenabled) from secusers where usrenabled = :usrenabled";

		paramMap.addValue("usrenabled", 1);

		return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
	}

	@Override
	public List<Entity> getEntityList(String entity) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select distinct e.EntityCode, e.EntityDesc");
		sql.append(" from Entity e");
		sql.append(" inner join smtdivisiondetail d on d.entitycode = e.entitycode");
		sql.append(" where e.EntityCode = :entitycode");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("entitycode", entity);

		RowMapper<Entity> rowMapper = BeanPropertyRowMapper.newInstance(Entity.class);

		try {
			return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public long getUserByName(String userName) {
		logger.debug("Entering ");
		SecurityUser securityUser = new SecurityUser();
		securityUser.setUsrLogin(StringUtils.upperCase(userName));

		StringBuilder selectSql = new StringBuilder("Select UsrID ");

		selectSql.append("  From SecUsers");
		selectSql.append(" Where UsrLogin =:UsrLogin");

		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		RowMapper<SecurityUser> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityUser.class);

		try {
			securityUser = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			securityUser.setUsrID(0);
		}
		logger.debug("Leaving ");
		return securityUser.getUsrID();
	}

	@Override
	public List<SecurityUser> getSecUsersByRoles(String[] roles) {
		logger.debug(Literal.ENTERING);

		if (roles == null || roles.length == 0) {
			return new ArrayList<>();
		}

		StringBuilder sql = new StringBuilder();
		sql.append("select u.UsrID, u.UsrLogin, u.UsrFName, u.UsrMName, u.UsrLName, u.UsrMobile, u.UsrEmail");
		sql.append(", u.UsrBranchCode, b.BranchDesc LovDescUsrBranchCodeName, u.LDAPDomainName");
		sql.append(" from SecUsers u");
		sql.append(" left join rmtbranches b on b.branchcode = u.UsrBranchCode");
		sql.append(" inner join SecuserOPerations uop on uop.usrId = u.usrId");
		sql.append(" inner join secOPerationRoles opr on opr.oprid = uop.oprid");
		sql.append(" where opr.roleId in (select roleId from secRoles where rolecd in (:Rolecd))");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Rolecd", Arrays.asList(roles));

		RowMapper<SecurityUser> rowMapper = BeanPropertyRowMapper.newInstance(SecurityUser.class);

		try {
			return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public SecurityUser getSecurityUserAccessToAllBranches(long id) {
		logger.debug("Entering ");
		SecurityUser securityUser = new SecurityUser();
		securityUser.setId(id);

		StringBuilder selectSql = new StringBuilder(
				"Select AccessToAllBranches, LovDescUsrBranchCodeName From SecUsers_AVIEW ");
		selectSql.append(" Where UsrID = :UsrID");

		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		RowMapper<SecurityUser> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityUser.class);

		try {
			securityUser = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			securityUser.setUsrID(0);
		}
		logger.debug("Leaving ");
		return securityUser;
	}

	private void updateLockUser(List<? extends SecurityUser> userAccounts) {
		StringBuilder sql = new StringBuilder();
		sql.append("update SecUsers");
		sql.append(" set UsrAcLocked = 1, AccountLockedOn = :AccountLockedOn");
		sql.append(", AccountUnLockedOn = null where UsrID = :UsrID");

		try {
			jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(userAccounts.toArray()));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Override
	public void lockUserAccounts() {
		StringBuilder sql = new StringBuilder();
		sql.append("select UsrID, LastLoginOn, AccountUnLockedOn from SecUsers");
		sql.append(" where UsrAcLocked = 0 and UserType = :UserType");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("UserType", "USER");

		int days = SysParamUtil.getValueAsInt(SMTParameterConstants.USR_ACCT_LOCK_DAYS);

		Date sysDate = DateUtility.getSysDate();
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

		if (userAccounts.size() > 0) {
			// call batch update method
			updateLockUser(userAccounts);
			userAccounts.clear();
		}
	}

	private SecurityUser getTypeRowMapper(ResultSet rs, String type) throws SQLException {
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
		su.setBusinessVertical(rs.getLong("BusinessVertical"));
		su.setldapDomainName(rs.getString("LdapDomainName"));
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

		return su;
	}

}