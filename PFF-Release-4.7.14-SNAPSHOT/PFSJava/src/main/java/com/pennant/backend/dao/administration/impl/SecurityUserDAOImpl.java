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

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * DAO methods implementation for the <b>SecurityUsers model</b> class.<br>
 * 
 */
public class SecurityUserDAOImpl extends BasisNextidDaoImpl<SecurityUser> implements SecurityUserDAO {
	private static Logger logger = Logger.getLogger(SecurityUserDAOImpl.class);

	public SecurityUserDAOImpl() {
		super();
	}
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	/**
	 * This method set the Work Flow id based on the module name and return the new SecurityUsers 
	 * @return SecurityUsers
	 */
	@Override
	public SecurityUser getSecurityUser() {
		logger.debug("Entering ");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SecurityUser");
		SecurityUser securityUser= new SecurityUser();
		if (workFlowDetails!=null){
			securityUser.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving ");
		return securityUser;
	}

	/**
	 * This method get the module from method getSecurityUsers() and 
	 * set the new record flag as true and return SecurityUsers()   
	 * @return SecurityUsers
	 */
	@Override
	public SecurityUser getNewSecurityUser() {
		logger.debug("Entering ");
		SecurityUser securityUser = getSecurityUser();
		securityUser.setNewRecord(true);
		logger.debug("Leaving ");
		return securityUser;
	}

	/**
	 * Fetch the Record  Security Users details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityUsers
	 */
	@Override
	public SecurityUser getSecurityUserById(final long id, String type) {
		logger.debug("Entering ");
		SecurityUser securityUser = new SecurityUser();
		securityUser.setId(id);

		StringBuilder   selectSql = new StringBuilder(" Select UsrID, UsrLogin, UsrPwd, " );
		selectSql.append(" UserStaffID, UsrFName, UsrMName, UsrLName, UsrMobile, UsrEmail,");
		selectSql.append(" UsrEnabled, UsrCanSignonFrom, UsrCanSignonTo, UsrCanOverrideLimits, " );
		selectSql.append(" UsrAcExp,UsrAcExpDt, UsrCredentialsExp, UsrAcLocked, UsrLanguage, " );
		selectSql.append(" UsrDftAppId, UsrBranchCode, UsrDeptCode, UsrToken, UsrIsMultiBranch, " );
		selectSql.append(" UsrInvldLoginTries, UsrDesg, AuthType, UserType," );
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescUsrDftAppCode , lovDescUsrDftAppCodeName , " );
			selectSql.append(" lovDescUsrDeptCodeName ,lovDescUsrBranchCodeName,LovDescUsrLanguage,");
			selectSql.append(" lovDescUsrDesg,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" From SecUsers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where UsrID =:UsrID");

		logger.debug("selectSql:" + selectSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		RowMapper<SecurityUser> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityUser.class);

		try{
			securityUser = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			securityUser = null;
		}
		logger.debug("Leaving ");
		return securityUser;
	}

	/**
	 * Fetch the Record  Security Users details by key field
	 * 
	 * @param usrLogin (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityUsers
	 */
	@Override
	public SecurityUser getSecurityUserByLogin(final String usrLogin, String type) {
		logger.debug("Entering ");
		SecurityUser securityUser = new SecurityUser();
		securityUser.setUsrLogin(usrLogin);
	
		StringBuilder selectSql= new StringBuilder("Select UsrID, UsrLogin, UsrPwd, " );
		selectSql.append(" UserStaffID, UsrFName, UsrMName, UsrLName, UsrMobile, UsrEmail, " );
		selectSql.append(" UsrEnabled, UsrCanSignonFrom, UsrCanSignonTo, UsrCanOverrideLimits, " );
		selectSql.append(" UsrAcExp,UsrAcExpDt, UsrCredentialsExp, UsrAcLocked, UsrLanguage, " );
		selectSql.append(" UsrDftAppId, UsrBranchCode, UsrDeptCode, UsrToken, UsrIsMultiBranch, " );
		selectSql.append(" UsrInvldLoginTries,UsrDesg, AuthType," );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescUsrDftAppCode,lovDescUsrDftAppCodeName , " );
			selectSql.append(" lovDescUsrDeptCodeName,lovDescUsrBranchCodeName,LovDescUsrLanguage,");
			selectSql.append(" lovDescUsrDesg");
		}
		selectSql.append("  From SecUsers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where UsrLogin =:UsrLogin");

		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		RowMapper<SecurityUser> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityUser.class);

		try{
			securityUser = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			securityUser = null;
		}
		logger.debug("Leaving ");
		return securityUser;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the SecUsers or SecUsers_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Security Users by key UsrID
	 * 
	 * @param Security Users (securityUsers)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(SecurityUser securityUser, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder   deleteSql = new StringBuilder  ("Delete From SecUsers");
		deleteSql.append(StringUtils.trimToEmpty(type));	
		deleteSql.append(" Where UsrID =:UsrID");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		logger.debug("deleteSql:"+deleteSql.toString());

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetail errorDetails= getError ("41003",securityUser.getUsrLogin() , 
						securityUser.getUserDetails().getLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			ErrorDetail errorDetails= getError ("41006",securityUser.getUsrLogin() ,
					securityUser.getUserDetails().getLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into SecUsers or SecUsers_Temp.
	 * it fetches the available Sequence form SeqSecUsers by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Security Users 
	 * 
	 * @param Security Users (securityUsers)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(SecurityUser securityUser,String type) {
		logger.debug("Entering ");
		if (securityUser.getId()==Long.MIN_VALUE){
			securityUser.setId(getNextidviewDAO().getNextId("SeqSecUsers"));
			logger.debug("get NextID:"+securityUser.getId());
		}

		StringBuilder   insertSql  = new StringBuilder  ("Insert Into SecUsers");
		insertSql .append(StringUtils.trimToEmpty(type));
		insertSql .append(" (UsrID, UsrLogin, UsrPwd, UserStaffID, UsrFName, UsrMName, " );
		insertSql .append(" UsrLName, UsrMobile, UsrEmail, UsrEnabled, UsrCanSignonFrom, " );
		insertSql .append(" UsrCanSignonTo, UsrCanOverrideLimits, UsrAcExp, UsrCredentialsExp, " );
		insertSql .append(" UsrAcLocked, UsrLanguage, UsrDftAppId, UsrDftAppCode, UsrBranchCode, " );
		insertSql .append(" UsrDeptCode, UsrToken, UsrIsMultiBranch, UsrInvldLoginTries, UsrAcExpDt, " );
		insertSql .append(" UsrDesg, AuthType," );
		insertSql .append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		insertSql .append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql .append(" Values(:UsrID, :UsrLogin, :UsrPwd, :UserStaffID, :UsrFName, :UsrMName, " );
		insertSql .append(" :UsrLName, :UsrMobile, :UsrEmail, :UsrEnabled, :UsrCanSignonFrom, " );
		insertSql .append(" :UsrCanSignonTo, :UsrCanOverrideLimits, :UsrAcExp, :UsrCredentialsExp, " );
		insertSql .append(" :UsrAcLocked, :UsrLanguage, :UsrDftAppId, :UsrDftAppCode, :UsrBranchCode, " );
		insertSql .append(" :UsrDeptCode, :UsrToken, :UsrIsMultiBranch, :UsrInvldLoginTries,:UsrAcExpDt, :UsrDesg, :AuthType,");
		insertSql .append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql .append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql:" +insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
		return securityUser.getId();
	}
	
	/**
	 * This method updates the Record SecUsers or SecUsers_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Security Users by key UsrID and Version
	 * 
	 * @param SecurityUsers (securityUser)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(SecurityUser securityUser,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder   updateSql = new StringBuilder  ("Update SecUsers");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set UsrLogin = :UsrLogin, UsrPwd = :UsrPwd, UserStaffID = :UserStaffID, UsrFName = :UsrFName, ");
		updateSql.append(" UsrMName = :UsrMName, UsrLName = :UsrLName, UsrMobile = :UsrMobile, UsrEmail = :UsrEmail, ");
		updateSql.append(" UsrEnabled = :UsrEnabled, UsrCanSignonFrom = :UsrCanSignonFrom, UsrCanSignonTo = :UsrCanSignonTo,");
		updateSql.append(" UsrCanOverrideLimits = :UsrCanOverrideLimits, UsrAcExp = :UsrAcExp, UsrCredentialsExp = :UsrCredentialsExp," );
		updateSql.append(" UsrAcLocked = :UsrAcLocked, UsrLanguage = :UsrLanguage, UsrDftAppId= :UsrDftAppId,UsrAcExpDt= :UsrAcExpDt, UsrDftAppCode = :UsrDftAppCode, ");
		updateSql.append(" UsrBranchCode = :UsrBranchCode, UsrDeptCode = :UsrDeptCode, ");
		updateSql.append(" UsrIsMultiBranch = :UsrIsMultiBranch, UsrInvldLoginTries = :UsrInvldLoginTries, UsrDesg = :UsrDesg, AuthType = :AuthType, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, ");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where UsrID =:UsrID");
		if (StringUtils.isBlank(type)) {
			updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql:"+updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetail errorDetails= getError ("41004",securityUser.getUsrLogin() , securityUser.getUserDetails().getLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method updates the UsrPwd,UsrToken,UsrAcExpDt fields of securityUser record
	 *  if Record not updated then throws DataAccessException 
	 * @param securityUser (SecurityUsers) 
	 */
	@SuppressWarnings("serial")
	public void changePassword(SecurityUser securityUser) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder   updateSql = new StringBuilder  (" Update SecUsers");
		updateSql.append(" Set UsrPwd=:UsrPwd ,UsrToken=:UsrToken,UsrAcExpDt = :UsrAcExpDt, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus ");
		updateSql.append(" Where UsrID =:UsrID");

		logger.debug("updateSql:"+updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetail errorDetails= getError ("41004",securityUser.getUsrLogin() , securityUser.getUserDetails().getLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetail  getError(String errorId, String userLogin, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = userLogin;
		parms[0][0] = PennantJavaUtil.getLabel("label_UsrLogin")+":" +parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
	
	/**
	 * Fetch the Record Security User Division Branch Details by key fields
	 * 
	 */
	@Override
	public SecurityUserDivBranch getSecUserDivBrDetailsById(SecurityUserDivBranch securityUserDivBranch, String type) {
		logger.debug("Entering ");
		
		StringBuilder   selectSql = new StringBuilder(" Select UsrID, UserDivision, UserBranch, " );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",UserBranchDesc " );
		}
		selectSql.append(" From SecurityUserDivBranch");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where UsrID = :UsrID And UserDivision = :UserDivision And UserBranch =:UserBranch");
		
		logger.debug("selectSql:" + selectSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUserDivBranch);
		RowMapper<SecurityUserDivBranch> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityUserDivBranch.class);
		
		try{
			securityUserDivBranch = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			securityUserDivBranch = null;
		}
		logger.debug("Leaving ");
		return securityUserDivBranch;
	}
	
	/**
	 * This method is to Save  SecurityUser Division Branch  Details
	 */
	public long saveDivBranchDetails(SecurityUserDivBranch securityUserDivBranch,String type) {
		logger.debug("Entering ");
	
		StringBuilder   insertSql  = new StringBuilder  ("Insert Into SecurityUserDivBranch");
		insertSql .append(StringUtils.trimToEmpty(type));
		insertSql .append(" (UsrID, UserDivision, UserBranch," );
		insertSql .append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		insertSql .append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql .append(" Values(:UsrID, :UserDivision, :UserBranch," );
		insertSql .append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql .append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql:" +insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUserDivBranch);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving ");
		return securityUserDivBranch.getId();
	}
	
	/**
	 * This method is to Update  SecurityUser Division Branch  Details
	 */
	@SuppressWarnings("serial")
	@Override
	public void updateDivBranchDetails(SecurityUserDivBranch securityUserDivBranch, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder   updateSql = new StringBuilder  ("Update SecurityUserDivBranch");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, ");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where UsrID = :UsrID And UserDivision = :UserDivision And UserBranch =:UserBranch");
		
		logger.debug("updateSql:"+updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUserDivBranch);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetail errorDetails= getError ("41004",String.valueOf(securityUserDivBranch.getUsrID()) , securityUserDivBranch.getUserDetails().getLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is to Delete  Each Branch Under Division   
	 */
	@SuppressWarnings("serial")
	@Override
	public void deleteDivBranchDetails(SecurityUserDivBranch securityUserDivBranch, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder   deleteSql = new StringBuilder  ("Delete From SecurityUserDivBranch");
		deleteSql.append(StringUtils.trimToEmpty(type));	
		deleteSql.append(" Where UsrID = :UsrID And UserDivision = :UserDivision And UserBranch =:UserBranch");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUserDivBranch);
		logger.debug("deleteSql:"+deleteSql.toString());
		
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetail errorDetails= getError ("41003",String.valueOf(securityUserDivBranch.getUsrID()) , 
						securityUserDivBranch.getUserDetails().getLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			ErrorDetail errorDetails= getError ("41006",String.valueOf(securityUserDivBranch.getUsrID()) ,
					securityUserDivBranch.getUserDetails().getLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	/**
	 *  This method is to Fetch  SecurityUser Division Branch  Details
	 */
	@Override
	public List<SecurityUserDivBranch> getSecUserDivBrList(long usrID,String type) {
		logger.debug("Entering");
		SecurityUserDivBranch securityUserDivBranch = new SecurityUserDivBranch();
		securityUserDivBranch.setUsrID(usrID);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT UsrID, UserDivision, UserBranch,");
		if (StringUtils.containsIgnoreCase(type, "VIEW")) {
			selectSql.append("UserBranchDesc,");
		}

		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  SecurityUserDivBranch");
		selectSql.append(type);
		selectSql.append(" Where UsrID = :UsrID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUserDivBranch);
		RowMapper<SecurityUserDivBranch> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityUserDivBranch.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method is to Delete  SecurityUser Division Branch  Details Under User
	 */
	@SuppressWarnings("serial")
	@Override
	public void deleteBranchs(SecurityUser securityUser, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder   deleteSql = new StringBuilder  ("Delete From SecurityUserDivBranch");
		deleteSql.append(StringUtils.trimToEmpty(type));	
		deleteSql.append(" Where UsrID = :UsrID");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		logger.debug("deleteSql:"+deleteSql.toString());
		
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetail errorDetails= getError ("41003",String.valueOf(securityUser.getUsrID()) , 
						securityUser.getUserDetails().getLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			ErrorDetail errorDetails= getError ("41006",String.valueOf(securityUser.getUsrID()) ,
					securityUser.getUserDetails().getLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	@Override
	public int getActiveUsersCount(long userId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String sql = "select count(*) from secusers where usrId <> :usrId and usrenabled = :usrenabled";

		paramMap.addValue("usrId", userId);
		paramMap.addValue("usrenabled", 1);

		return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
	}

	@Override
	public int getActiveUsersCount() {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String sql = "select count(*) from secusers where usrenabled = :usrenabled";

		paramMap.addValue("usrenabled", 1);

		return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
	}
	
	
}