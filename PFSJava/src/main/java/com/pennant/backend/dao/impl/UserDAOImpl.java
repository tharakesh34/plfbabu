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
 *
 * FileName : UserDAOImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 10-08-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.UserDAO;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>SecUser model</b> class.<br>
 */
public class UserDAOImpl extends BasicDao<SecurityUser> implements UserDAO {
	private static final Logger logger = LogManager.getLogger(UserDAOImpl.class);

	public UserDAOImpl() {
		super();
	}

	public SecurityUser getNewSecUser() {
		return null;
	}

	public int getCountAllSecUser() {
		return 0;
	}

	public List<SecurityUser> getAlleUser() {
		return new ArrayList<SecurityUser>();
	}

	public SecurityUser getUserByID(Long usrId) {
		return null;
	}

	public SecurityUser getUserByFiluserNr(String usrNr) {
		return null;
	}

	public SecurityUser getUserByNameAndPassword(String userName, String passWord) {
		return null;
	}

	public void updateLoginStatus(long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update SecUsers set LastLoginOn = :LastLoginOn");
		sql.append(", LastFailLoginOn = :LastFailLoginOn");
		sql.append(", UsrInvldLoginTries = :UsrInvldLoginTries");
		sql.append(" Where UsrId =:UsrId");

		Timestamp loginTime = new Timestamp(System.currentTimeMillis());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("UsrId", userId);
		parameterSource.addValue("LastLoginOn", loginTime);
		parameterSource.addValue("LastFailLoginOn", null);
		parameterSource.addValue("UsrInvldLoginTries", 0);

		this.jdbcTemplate.update(sql.toString(), parameterSource);
	}

	public void updateInvalidTries(String userName, String disableReason) {
		// If parameter value is 3, on 3rd invalid login details entered, application will disable the user.
		int invalidLogins = SysParamUtil.getValueAsInt("MAX_INVALIDLOGINS") - 1;

		Timestamp loginTime = new Timestamp(System.currentTimeMillis());
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("UsrLogin", userName);
		parameterSource.addValue("UsrEnabled", 0);
		parameterSource.addValue("DisableReason", disableReason);
		parameterSource.addValue("LastFailLoginOn", null);
		parameterSource.addValue("UsrInvldLoginTries", invalidLogins);

		StringBuilder sql = new StringBuilder();
		sql.append("Update SecUsers set UsrInvldLoginTries = UsrInvldLoginTries+1");
		sql.append(", UsrEnabled = :UsrEnabled");
		sql.append(", DisableReason = :DisableReason");
		sql.append(", LastFailLoginOn = :LastFailLoginOn");
		sql.append(" Where UsrLogin = :UsrLogin");
		sql.append(" and UsrInvldLoginTries >= :UsrInvldLoginTries");

		logger.trace(Literal.SQL + sql.toString());
		int count = this.jdbcTemplate.update(sql.toString(), parameterSource);

		if (count == 0) {
			parameterSource.addValue("LastFailLoginOn", loginTime);
			sql = new StringBuilder("Update SecUsers set");
			sql.append(" LastFailLoginOn = :LastFailLoginOn");
			sql.append(", UsrInvldLoginTries = UsrInvldLoginTries+1");
			sql.append(" Where UsrLogin = :UsrLogin");

			logger.trace(Literal.SQL + sql.toString());
			this.jdbcTemplate.update(sql.toString(), parameterSource);
		}
	}

	@Override
	public SecurityUser getSecurityUserByLogin(final String usrLogin) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT SU.USRID");
		sql.append(", SU.USRLOGIN");
		sql.append(", SU.USRPWD");
		sql.append(", SU.AUTHTYPE");
		sql.append(", SU.USERTYPE");
		sql.append(" FROM SECUSERS SU");
		sql.append(" WHERE USRLOGIN = :USRLOGIN");

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("USRLOGIN", usrLogin);

		RowMapper<SecurityUser> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityUser.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method fetches records from SecUsers table by UsrLogin
	 * 
	 * @param usrLogin (String)
	 * @return secUser (SecUser)
	 */
	@Override
	public SecurityUser getUserByLogin(final String usrLogin) {
		StringBuilder sql = getSecurityUser();
		sql.append(" Where USRLOGIN = ?");

		logger.trace(Literal.SQL + sql.toString());

		UserRowMapper rowMapper = new UserRowMapper();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, usrLogin);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	/**
	 * This method fetches records from SecUsers table by UsrLogin
	 * 
	 * @param usrLogin (String)
	 * @return secUser (SecUser)
	 */
	@Override
	public SecurityUser getUserByLogin(long userId) {
		StringBuilder sql = getSecurityUser();
		sql.append(" Where SU.USRID = ?");

		logger.trace(Literal.SQL + sql.toString());

		UserRowMapper rowMapper = new UserRowMapper();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, userId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private StringBuilder getSecurityUser() {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" SU.USRID");
		sql.append(", SU.USRLOGIN");
		sql.append(", SU.USRPWD");
		sql.append(", SU.AUTHTYPE");
		sql.append(", SU.USERTYPE");
		sql.append(", SU.USRLNAME");
		sql.append(", SU.USRMNAME");
		sql.append(", SU.USRFNAME");
		sql.append(", SU.USRMOBILE");
		sql.append(", SU.USREMAIL");
		sql.append(", SU.USRENABLED");
		sql.append(", SU.USRCANSIGNONFROM");
		sql.append(", SU.USRCANSIGNONTO");
		sql.append(", SU.USRCANOVERRIDELIMITS");
		sql.append(", SU.USRACEXP");
		sql.append(", SU.USERSTAFFID");
		sql.append(", SU.USRACLOCKED");
		sql.append(", SU.USRLANGUAGE");
		sql.append(", SU.USRDFTAPPCODE");
		sql.append(", SU.USRBRANCHCODE");
		sql.append(", SU.USRDEPTCODE");
		sql.append(", SU.PWDEXPDT");
		sql.append(", SU.USRTOKEN");
		sql.append(", SU.USRISMULTIBRANCH");
		sql.append(", SU.USRINVLDLOGINTRIES");
		sql.append(", SU.USRACEXPDT");
		sql.append(", SU.LASTMNTON");
		sql.append(", SU.LASTMNTBY");
		sql.append(", SU.NEXTROLECODE");
		sql.append(", SU.TASKID");
		sql.append(", SU.NEXTTASKID");
		sql.append(", SU.LASTLOGINON");
		sql.append(", SU.LASTFAILLOGINON");
		sql.append(", B.BRANCHDESC LOVDESCUSRBRANCHCODENAME");
		sql.append(", SU.BUSINESSVERTICAL");
		sql.append(", BV.CODE BUSINESSVERTICALCODE");
		sql.append(", BV.DESCRIPTION BUSINESSVERTICALDESC");
		sql.append(", SU.LDAPDOMAINNAME");
		sql.append(", SU.DELETED");
		sql.append(", SU.DISABLEREASON");
		sql.append(", SU.CREATEDBY");
		sql.append(", SU.CREATEDON");
		sql.append(" FROM SECUSERS SU");
		sql.append(" LEFT JOIN RMTBRANCHES B ON B.BRANCHCODE = SU.USRBRANCHCODE");
		sql.append(" LEFT JOIN BUSINESS_VERTICAL BV ON  BV.ID = SU.BUSINESSVERTICAL");
		return sql;
	}

	public List<SecurityUser> getUserLikeLastname(String value) {
		return new ArrayList<SecurityUser>();
	}

	public List<SecurityUser> getUserLikeLogin(String value) {
		return new ArrayList<SecurityUser>();
	}

	public List<SecurityUser> getUserLikeEmail(String email) {
		return new ArrayList<SecurityUser>();
	}

	public List<SecurityUser> getUserListByLogin(String login) {
		return new ArrayList<SecurityUser>();
	}

	/**
	 * This method fetches records from UserOperationRoles_View by UsrID and AppCode
	 * 
	 * @param userID (long) {@link List} of {@link SecurityRole}
	 * 
	 */
	@Override
	public List<SecurityRole> getUserRolesByUserID(final long userID) {
		StringBuilder sql = new StringBuilder("select RoleCd, RoleDesc, RoleCategory");
		sql.append(" From SecUserOperations uo");
		sql.append(" Inner join SecOperationRoles opr on opr.OprID = uo.OprID");
		sql.append(" Inner join SecRoles r on r.RoleID = opr.RoleID");
		sql.append(" Where uo.UsrID = ? ");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			SecurityRole role = new SecurityRole();
			role.setRoleCd(rs.getString("RoleCd"));
			role.setRoleDesc(rs.getString("RoleDesc"));
			role.setRoleCategory(rs.getString("RoleCategory"));
			return role;
		}, userID);
	}

	@Override
	public List<SecurityRole> getMenuRoles(final long userID) {
		StringBuilder sql = new StringBuilder("select distinct r.RoleCd");
		sql.append(" From SecUserOperations uo");
		sql.append(" Inner join SecOperationRoles opr on opr.OprID = uo.OprID");
		sql.append(" Inner join SecRoles r on r.RoleID = opr.RoleID");
		sql.append(" Inner join SecRoleGroups rg on rg.RoleID = r.RoleID");
		sql.append(" Inner join SecGroupRights gr on gr.GrpID = rg.GrpID");
		sql.append(" Inner join SecRights rt on rt.RightID = gr.RightID and rt.RightType = ?");
		sql.append(" Where uo.UsrID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setInt(1, 0);
			ps.setLong(2, userID);
		}, (rs, rowNum) -> {
			SecurityRole role = new SecurityRole();

			role.setRoleCd(rs.getString("RoleCd"));

			return role;
		});
	}

	/**
	 * This method updates the records in SecUsers table
	 * 
	 * @param secUser (SecUser)
	 * @throws DataAccessException
	 */
	public void update(SecurityUser secUser) {
		logger.debug("Entering ");

		int recordCount = 0;

		StringBuilder sql = new StringBuilder(
				"update SecUsers set UsrLogin=:UsrLogin, UsrPwd=:UsrPwd, UsrFName =:UsrFName, UsrMName=:UsrMName, ");
		sql.append(
				"UsrLName=:UsrLName , UsrMobile =:UsrMobile ,UsrEmail =:UsrEmail,UsrEnabled =:UsrEnabled, UsrCanSignonFrom=:UsrCanSignonFrom,");
		sql.append(
				" UsrCanSignonTo =:UsrCanSignonTo , UsrCanOverrideLimits =:UsrCanOverrideLimits , UsrAcExp=:UsrAcExp, ");
		sql.append("UsrAcLocked =:UsrAcLocked , UsrLanguage =:UsrLanguage , UsrDftAppCode =:UsrDftAppCode ,");
		sql.append(
				" UsrBranchCode =:UsrBranchCode , UsrDeptCode =:UsrDeptCode ,UsrToken =:UsrToken , UsrInvldLoginTries =:UsrInvldLoginTries, ");
		sql.append(
				" Version =:Version, LastMntBy =:LastMntBy , LastMntOn =:LastMntOn ,nextRoleCode=:nextRoleCode,taskId=:TaskId,nextTaskId=:nextTaskId");
		sql.append(" where UsrID =:UsrID  ");

		logger.debug("updateSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secUser);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
		// If the number of updated records are less than or equals zero
		// generate new exception
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");

	}

	@Override
	public List<String> getRoleCodes(final String rolecd) {
		List<String> dbRoles = new ArrayList<>();
		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("select rolecd from secRoles sec where roleid in");
		sql.append(" (select  opr.roleId from secusers u");
		sql.append(" inner join SecuserOPerations uop on uop.usrId = u.usrId");
		if (!rolecd.contains(",")) {
			sql.append(" inner join secOPerationRoles opr on opr.oprid = uop.oprid  where sec.rolecd = :rolecd)");
			source.addValue("rolecd", rolecd);
		} else {
			sql.append(
					" inner join secOPerationRoles opr on opr.oprid = uop.oprid  where sec.rolecd in( :rolecd1, :rolecd2))");
			source.addValue("rolecd1", rolecd.split(",")[0]);
			source.addValue("rolecd2", rolecd.split(",")[1]);
		}
		logger.trace(Literal.SQL + sql.toString());

		try {
			if (rolecd.contains(",")) {
				dbRoles = this.jdbcTemplate.queryForList(sql.toString(), source, String.class);
			} else {
				dbRoles.add(this.jdbcTemplate.queryForObject(sql.toString(), source, String.class));
			}
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		logger.debug(Literal.LEAVING);
		return dbRoles;
	}

	private class UserRowMapper implements RowMapper<SecurityUser> {

		@Override
		public SecurityUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			SecurityUser securityUser = new SecurityUser();

			securityUser.setUsrID(rs.getLong("USRID"));
			securityUser.setUsrLogin(rs.getString("USRLOGIN"));
			securityUser.setUsrPwd(rs.getString("USRPWD"));
			securityUser.setAuthType(rs.getString("AUTHTYPE"));
			securityUser.setUserType(rs.getString("USERTYPE"));
			securityUser.setUsrLName(rs.getString("USRLNAME"));
			securityUser.setUsrMName(rs.getString("USRMNAME"));
			securityUser.setUsrFName(rs.getString("USRFNAME"));
			securityUser.setUsrMobile(rs.getString("USRMOBILE"));
			securityUser.setUsrEmail(rs.getString("USREMAIL"));
			securityUser.setUsrEnabled(rs.getBoolean("USRENABLED"));
			securityUser.setUsrCanSignonFrom(rs.getTime("USRCANSIGNONFROM"));
			securityUser.setUsrCanSignonTo(rs.getTime("USRCANSIGNONTO"));
			securityUser.setUsrCanOverrideLimits(rs.getBoolean("USRCANOVERRIDELIMITS"));
			securityUser.setUsrAcExp(rs.getBoolean("USRACEXP"));
			securityUser.setUserStaffID(rs.getString("USERSTAFFID"));
			securityUser.setUsrAcLocked(rs.getBoolean("USRACLOCKED"));
			securityUser.setUsrLanguage(rs.getString("USRLANGUAGE"));
			securityUser.setUsrDftAppCode(rs.getString("USRDFTAPPCODE"));
			securityUser.setUsrBranchCode(rs.getString("USRBRANCHCODE"));
			securityUser.setUsrDeptCode(rs.getString("USRDEPTCODE"));
			securityUser.setPwdExpDt(rs.getDate("PWDEXPDT"));
			securityUser.setUsrToken(rs.getString("USRTOKEN"));
			securityUser.setUsrIsMultiBranch(rs.getBoolean("USRISMULTIBRANCH"));
			securityUser.setUsrInvldLoginTries(rs.getInt("USRINVLDLOGINTRIES"));
			securityUser.setUsrAcExpDt(rs.getDate("USRACEXPDT"));
			securityUser.setLastMntOn(rs.getTimestamp("LASTMNTON"));
			securityUser.setLastMntBy(rs.getLong("LASTMNTBY"));
			securityUser.setNextRoleCode(rs.getString("NEXTROLECODE"));
			securityUser.setTaskId(rs.getString("TASKID"));
			securityUser.setNextTaskId(rs.getString("NEXTTASKID"));
			securityUser.setLastLoginOn(rs.getTimestamp("LASTLOGINON"));
			securityUser.setLastFailLoginOn(rs.getTimestamp("LASTFAILLOGINON"));
			securityUser.setLovDescUsrBranchCodeName(rs.getString("LOVDESCUSRBRANCHCODENAME"));
			securityUser.setBusinessVertical(JdbcUtil.getLong(rs.getObject("BUSINESSVERTICAL")));
			securityUser.setBusinessVerticalCode(rs.getString("BUSINESSVERTICALCODE"));
			securityUser.setBusinessVerticalDesc(rs.getString("BUSINESSVERTICALDESC"));
			securityUser.setldapDomainName(rs.getString("LDAPDomainName"));
			securityUser.setDeleted(rs.getBoolean("DELETED"));
			securityUser.setCreatedOn(rs.getTimestamp("CREATEDON"));
			securityUser.setCreatedBy(rs.getLong("CREATEDBY"));

			return securityUser;
		}

	}
}
