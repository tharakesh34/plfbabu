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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.UserDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * DAO methods implementation for the <b>SecUser model</b> class.<br>
 */
public class UserDAOImpl extends BasisNextidDaoImpl<SecurityUser> implements UserDAO {

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private static final Logger logger = Logger.getLogger(UserDAOImpl.class);

	public SecurityUser getNewSecUser() {
		return null;
	}

	public int getCountAllSecUser() {
		return 0;
	}


	public List<SecurityUser> getAlleUser() {
		return null;
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
 * This method updates UsrInvldLoginTries,UsrEnabled columns  of  SecUsers table
 * @param userName (String)
 * @param authPass (int)
 */

	public void updateLoginStatus(String userName,int authPass) {
		logger.debug("Entering ");

		StringBuilder	updateSql = new StringBuilder("Update SecUsers  set ");
		
		Map<String,Object> namedParameters = new HashMap<String,Object>();
		if (authPass==1){
			updateSql.append(" UsrInvldLoginTries=0 Where UsrLogin = '");
			updateSql.append(userName);
			updateSql.append("'");
			logger.debug("updateSql:"+updateSql.toString());
			this.namedParameterJdbcTemplate.update(updateSql.toString(),namedParameters);
		}else{
			
			int invalidLogins = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("MAX_INVALIDLOGINS").toString());
			
			updateSql.append(" UsrInvldLoginTries=(UsrInvldLoginTries+1), UsrEnabled=0  Where UsrLogin = '");
			updateSql.append(userName);
			updateSql.append("' and UsrInvldLoginTries >= "+invalidLogins);
			logger.debug("updateSql:"+updateSql.toString());
			int count = this.namedParameterJdbcTemplate.update(updateSql.toString(),namedParameters);
			if (count==0){
				updateSql = new StringBuilder("Update SecUsers  set ");
				updateSql.append(" UsrInvldLoginTries=(UsrInvldLoginTries+1) Where UsrLogin = '");
				updateSql.append(userName);
				updateSql.append("'");
				logger.debug("updateSql:"+updateSql.toString());
				this.namedParameterJdbcTemplate.update(updateSql.toString(),namedParameters);
			}
		}
		logger.debug("Leaving ");	
	}

/**
 * This method fetches records from SecUsers table by UsrLogin 
 * @param usrLogin (String)
 * @return secUser (SecUser)
 */
	public SecurityUser getUserByLogin(final String usrLogin) {
		logger.debug("Entering ");
		SecurityUser secUser = new SecurityUser();


		StringBuilder   selectSql = new StringBuilder("SELECT UsrID, UsrLogin, UsrPwd, UsrLName, UsrMName,UsrFName,");
		selectSql.append(" UsrMobile,UsrEmail,UsrEnabled,UsrCanSignonFrom,UsrCanSignonTo,UsrCanOverrideLimits,");
		selectSql.append("	UsrAcExp, UsrCredentialsExp,UserStaffID, UsrAcLocked,UsrLanguage,UsrDftAppCode,UsrBranchCode,UsrDeptCode,");
		selectSql.append(" UsrToken, UsrIsMultiBranch,UsrInvldLoginTries,UsrAcExpDt,LastMntOn, LastMntBy,nextRoleCode,TaskId,nextTaskId"); 
		selectSql.append(" FROM SecUsers where UsrLogin = :usrLogin");
		logger.debug("selectSql: " + selectSql.toString());
		secUser.setUsrLogin(usrLogin);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secUser);
		RowMapper<SecurityUser> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityUser.class);
		
		try {
			secUser = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
			
		} catch (EmptyResultDataAccessException e) {
			secUser =null;
		}
		logger.debug("Leaving ");
		return secUser;
	}


	public List<SecurityUser> getUserLikeLastname(String value) {
		return null;
	}



	public List<SecurityUser> getUserLikeLogin(String value) {
		return null;
	}


	public List<SecurityUser> getUserLikeEmail(String email) {
		return null;
	}



	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}


	public List<SecurityUser> getUserListByLogin(String login) {
		return null;
	}
/**
 * This method fetches records from UserRoles_View by UsrID and AppCode
 * @param userID (long)
 * @return List<SecurityRole>
 * 
 */
	public List<SecurityRole> getUserRolesByUserID(final long userID) {
		logger.debug("Entering ");
		SecurityRole secRoles = new SecurityRole();

		secRoles.setLoginUsrId(userID);
		secRoles.setLoginAppCode(PennantConstants.applicationCode);

		String selectListSql = 	"SELECT UsrID ,RoleID, RoleApp, AppCode, RoleCd, RoleDesc,RoleCategory, Version, LastMntBy,LastMntOn FROM UserRoles_View where UsrID= :LoginUsrId AND AppCode=:loginAppCode";
		logger.debug("selectSql: " + selectListSql);
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		RowMapper<SecurityRole> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRole.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectListSql, beanParameters, typeRowMapper);

	}
/**
 * This method updates the records in SecUsers table 
 * @param secUser (SecUser)
 * @throws DataAccessException
 */
	@SuppressWarnings("serial")
	public void update(SecurityUser secUser) {
		logger.debug("Entering ");

		int recordCount = 0;

		StringBuilder   updateSql =new StringBuilder("update SecUsers set UsrLogin=:UsrLogin, UsrPwd=:UsrPwd, UsrFName =:UsrFName, UsrMName=:UsrMName, " );
		updateSql .append("UsrLName=:UsrLName , UsrMobile =:UsrMobile ,UsrEmail =:UsrEmail,UsrEnabled =:UsrEnabled, UsrCanSignonFrom=:UsrCanSignonFrom,");
		updateSql .append(" UsrCanSignonTo =:UsrCanSignonTo , UsrCanOverrideLimits =:UsrCanOverrideLimits , UsrAcExp=:UsrAcExp, ");
		updateSql .append("UsrCredentialsExp =:UsrCredentialsExp, UsrAcLocked =:UsrAcLocked , UsrLanguage =:UsrLanguage , UsrDftAppCode =:UsrDftAppCode ,");
		updateSql .append(" UsrBranchCode =:UsrBranchCode , UsrDeptCode =:UsrDeptCode ,UsrToken =:UsrToken , UsrInvldLoginTries =:UsrInvldLoginTries, ");
		updateSql .append(" Version =:Version, LastMntBy =:LastMntBy , LastMntOn =:LastMntOn ,nextRoleCode=:nextRoleCode,taskId=:TaskId,nextTaskId=:nextTaskId");
		updateSql .append(" where UsrID =:UsrID  ") ;
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secUser);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		// If the number of updated records are less than or equals zero generate new exception
		if (recordCount <= 0) {
			ErrorDetails errorDetail= getError("41004", secUser.getUsrLogin(),secUser.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetail.getError()) {};
		}
		logger.debug("Leaving ");  

	}
	
	private ErrorDetails  getError(String errorId, String userLogin, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = userLogin;
		parms[0][0] = PennantJavaUtil.getLabel("label_UsrLogin")+":" +parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}
