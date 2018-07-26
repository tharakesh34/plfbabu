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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.pennanttech.pennapps.core.App;
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

	/**
	 * This method updates UsrInvldLoginTries,UsrEnabled columns of SecUsers
	 * table
	 * 
	 * @param userName
	 *            (String)
	 * @param authPass
	 *            (int)
	 */

	public void updateLoginStatus(String userName, int authPass) {
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder("Update SecUsers  set ");

		Timestamp  loginTime = new Timestamp(System.currentTimeMillis());
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("UsrLogin", userName);
		if (authPass == 1) {
			namedParameters.put("LastLoginOn", loginTime);
			namedParameters.put("LastFailLoginOn", null);
			namedParameters.put("UsrInvldLoginTries", 0);
			
			updateSql.append("LastLoginOn =:LastLoginOn,LastFailLoginOn=:LastFailLoginOn,UsrInvldLoginTries=:UsrInvldLoginTries");
			updateSql.append(" Where UsrLogin =:UsrLogin");
			logger.debug("updateSql:" + updateSql.toString());
			this.jdbcTemplate.update(updateSql.toString(), namedParameters);
		} else {
			//If parameter value is 3, on 3rd invalid login details entered,  application will disable the user. 
			int invalidLogins = SysParamUtil.getValueAsInt("MAX_INVALIDLOGINS") - 1 ;
			namedParameters.put("UsrEnabled", 0);
			namedParameters.put("LastFailLoginOn", null);
			namedParameters.put("invalidLogins", invalidLogins);
			
			updateSql.append(" UsrInvldLoginTries=(UsrInvldLoginTries+1), UsrEnabled=:UsrEnabled,LastFailLoginOn =:LastFailLoginOn  Where UsrLogin = :UsrLogin");
			updateSql.append(" and UsrInvldLoginTries >= :invalidLogins");
			logger.debug("updateSql:" + updateSql.toString());
			int count = this.jdbcTemplate.update(updateSql.toString(), namedParameters);
			if (count == 0) {
				namedParameters.put("LastFailLoginOn", loginTime);
				updateSql = new StringBuilder("Update SecUsers  set ");
				updateSql.append("LastFailLoginOn =:LastFailLoginOn");
				updateSql.append(",UsrInvldLoginTries=(UsrInvldLoginTries+1) Where UsrLogin = :UsrLogin");
				logger.debug("updateSql:" + updateSql.toString());
				this.jdbcTemplate.update(updateSql.toString(), namedParameters);
			}
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering");

		SecurityUser secUser = new SecurityUser();

		StringBuilder selectSql = new StringBuilder("SELECT UsrID, UsrLogin, UsrPwd, AuthType, UserType, UsrLName, UsrMName,UsrFName,");
		selectSql.append(" UsrMobile,UsrEmail,UsrEnabled,UsrCanSignonFrom,UsrCanSignonTo,UsrCanOverrideLimits,");
		selectSql.append(" UsrAcExp, UserStaffID, UsrAcLocked,UsrLanguage,UsrDftAppCode,UsrBranchCode,UsrDeptCode,PwdExpDt,");
		selectSql.append(" UsrToken, UsrIsMultiBranch,UsrInvldLoginTries,UsrAcExpDt,LastMntOn, LastMntBy,nextRoleCode,TaskId,nextTaskId,LastLoginOn,LastFailLoginOn");
		selectSql.append(" FROM SecUsers where UsrLogin = :usrLogin");
		//FIXME Satish : Password should not retrieved to avoid this we have commented out the log printing. 
//		logger.debug("selectSql: " + selectSql.toString());
		secUser.setUsrLogin(usrLogin);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secUser);
		RowMapper<SecurityUser> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityUser.class);

		try {
			secUser = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
//			logger.warn("Exception: ", e);
			secUser = null;
		}

		logger.debug("Leaving");
		return secUser;
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
	 * This method fetches records from UserOperationRoles_View by UsrID and
	 * AppCode
	 * 
	 * @param userID
	 *            (long)
	 * {@link List} of {@link SecurityRole}
	 * 
	 */
	@Override
	public List<SecurityRole> getUserRolesByUserID(final long userID) {
		logger.debug(Literal.ENTERING);
		
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("UsrID", userID);
		paramSource.addValue("RoleApp", App.ID);
		
		StringBuilder sql = new StringBuilder("select RoleCd, RoleDesc,RoleCategory ");
		sql.append(" from SecUserOperations UO  ");
		sql.append(" inner join SecOperationRoles OPR on OPR.OprID = UO.OprID ");
		sql.append(" inner join SecRoles R on R.RoleID = OPR.RoleID ");
		sql.append("  where UO.UsrID = :UsrID and R.RoleApp = :RoleApp ");
		logger.trace(Literal.SQL + sql.toString());

		RowMapper<SecurityRole> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRole.class);
		logger.debug(Literal.LEAVING);

		
		return this.jdbcTemplate.query(sql.toString(), paramSource, typeRowMapper);
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
		updateSql
				.append("UsrLName=:UsrLName , UsrMobile =:UsrMobile ,UsrEmail =:UsrEmail,UsrEnabled =:UsrEnabled, UsrCanSignonFrom=:UsrCanSignonFrom,");
		updateSql
				.append(" UsrCanSignonTo =:UsrCanSignonTo , UsrCanOverrideLimits =:UsrCanOverrideLimits , UsrAcExp=:UsrAcExp, ");
		updateSql
				.append("UsrAcLocked =:UsrAcLocked , UsrLanguage =:UsrLanguage , UsrDftAppCode =:UsrDftAppCode ,");
		updateSql
				.append(" UsrBranchCode =:UsrBranchCode , UsrDeptCode =:UsrDeptCode ,UsrToken =:UsrToken , UsrInvldLoginTries =:UsrInvldLoginTries, ");
		updateSql
				.append(" Version =:Version, LastMntBy =:LastMntBy , LastMntOn =:LastMntOn ,nextRoleCode=:nextRoleCode,taskId=:TaskId,nextTaskId=:nextTaskId");
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

}
