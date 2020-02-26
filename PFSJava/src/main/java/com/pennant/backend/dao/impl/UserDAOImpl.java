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
 *
 * FileName    		:  UserDAOImpl.java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  10-08-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.UserDAO;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>SecUser model</b> class.<br>
 */
public class UserDAOImpl extends BasicDao<SecurityUser> implements UserDAO {
	private static final Logger logger = Logger.getLogger(UserDAOImpl.class);

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

	public void updateInvalidTries(String userName) {
		logger.debug(Literal.ENTERING);

		//If parameter value is 3, on 3rd invalid login details entered,  application will disable the user. 
		int invalidLogins = SysParamUtil.getValueAsInt("MAX_INVALIDLOGINS") - 1;

		Timestamp loginTime = new Timestamp(System.currentTimeMillis());
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("UsrLogin", userName);
		parameterSource.addValue("UsrEnabled", 0);
		parameterSource.addValue("LastFailLoginOn", null);
		parameterSource.addValue("UsrInvldLoginTries", invalidLogins);

		StringBuilder sql = new StringBuilder();
		sql.append("Update SecUsers set UsrInvldLoginTries = UsrInvldLoginTries+1");
		sql.append(", UsrEnabled = :UsrEnabled");
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

		logger.debug(Literal.LEAVING);
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

		RowMapper<SecurityUser> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityUser.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	/**
	 * This method fetches records from SecUsers table by UsrLogin
	 * 
	 * @param usrLogin
	 *            (String)
	 * @return secUser (SecUser)
	 */
	@Override
	public SecurityUser getUserByLogin(final String usrLogin) {
		StringBuilder sql = getSecurityUser();
		sql.append(" WHERE USRLOGIN = :USRLOGIN");

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("USRLOGIN", usrLogin);

		RowMapper<SecurityUser> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityUser.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	/**
	 * This method fetches records from SecUsers table by UsrLogin
	 * 
	 * @param usrLogin
	 *            (String)
	 * @return secUser (SecUser)
	 */
	@Override
	public SecurityUser getUserByLogin(long userId) {
		StringBuilder sql = getSecurityUser();
		sql.append(" WHERE SU.USRID = :USRID");

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("USRID", userId);

		RowMapper<SecurityUser> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityUser.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	private StringBuilder getSecurityUser() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT SU.USRID");
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
	 * @param userID
	 *            (long) {@link List} of {@link SecurityRole}
	 * 
	 */
	@Override
	public List<SecurityRole> getUserRolesByUserID(final long userID) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("UsrID", userID);

		StringBuilder sql = new StringBuilder("select RoleCd, RoleDesc, RoleCategory");
		sql.append(" from SecUserOperations uo");
		sql.append(" inner join SecOperationRoles opr on opr.OprID = uo.OprID");
		sql.append(" inner join SecRoles r on r.RoleID = opr.RoleID");
		sql.append(" where uo.UsrID = :UsrID");
		logger.trace(Literal.SQL + sql.toString());

		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), paramSource, new RowMapper<SecurityRole>() {

			@Override
			public SecurityRole mapRow(ResultSet rs, int rowNum) throws SQLException {
				SecurityRole role = new SecurityRole();
				role.setRoleCd(rs.getString("RoleCd"));
				role.setRoleDesc(rs.getString("RoleDesc"));
				role.setRoleCategory(rs.getString("RoleCategory"));
				return role;
			}
		});
	}

	/**
	 * This method updates the records in SecUsers table
	 * 
	 * @param secUser
	 *            (SecUser)
	 * @throws DataAccessException
	 */
	public void update(SecurityUser secUser) {
		logger.debug("Entering ");

		int recordCount = 0;

		StringBuilder updateSql = new StringBuilder(
				"update SecUsers set UsrLogin=:UsrLogin, UsrPwd=:UsrPwd, UsrFName =:UsrFName, UsrMName=:UsrMName, ");
		updateSql.append(
				"UsrLName=:UsrLName , UsrMobile =:UsrMobile ,UsrEmail =:UsrEmail,UsrEnabled =:UsrEnabled, UsrCanSignonFrom=:UsrCanSignonFrom,");
		updateSql.append(
				" UsrCanSignonTo =:UsrCanSignonTo , UsrCanOverrideLimits =:UsrCanOverrideLimits , UsrAcExp=:UsrAcExp, ");
		updateSql.append("UsrAcLocked =:UsrAcLocked , UsrLanguage =:UsrLanguage , UsrDftAppCode =:UsrDftAppCode ,");
		updateSql.append(
				" UsrBranchCode =:UsrBranchCode , UsrDeptCode =:UsrDeptCode ,UsrToken =:UsrToken , UsrInvldLoginTries =:UsrInvldLoginTries, ");
		updateSql.append(
				" Version =:Version, LastMntBy =:LastMntBy , LastMntOn =:LastMntOn ,nextRoleCode=:nextRoleCode,taskId=:TaskId,nextTaskId=:nextTaskId");
		updateSql.append(" where UsrID =:UsrID  ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secUser);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
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

		}
		logger.debug(Literal.LEAVING);
		return dbRoles;
	}

	

	

}
