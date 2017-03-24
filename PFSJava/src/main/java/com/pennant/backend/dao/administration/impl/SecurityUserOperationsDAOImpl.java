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
 * FileName    		: SecurityUserOperationsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  10-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  10-08-2011       Pennant	                 0.1                                            * 
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserOperations;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class SecurityUserOperationsDAOImpl extends BasisNextidDaoImpl<SecurityUser> implements
		SecurityUserOperationsDAO {
	private static Logger logger = Logger.getLogger(SecurityUserOperationsDAOImpl.class);
	private NamedParameterJdbcTemplate JdbcTemplate;

	/**
	 * This method returns new SecurityUserOperations Object
	 */
	public SecurityUserOperations getSecurityUserOperations() {
		logger.debug("Entering ");
		return new SecurityUserOperations();
	}

	/**
	 * This method get the module from method getBillerDetail() and set the new
	 * record flag as true and return BillerDetail()
	 * 
	 * @return BillerDetail
	 */
	@Override
	public SecurityUserOperations getNewSecurityUserOperations() {
		logger.debug("Entering");
		SecurityUserOperations securityUserOperations = getSecurityUserOperations();
		securityUserOperations.setNewRecord(true);
		logger.debug("Leaving");
		return securityUserOperations;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */

	public void setDataSource(DataSource dataSource) {
		this.JdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
	}

	/**
	 * This Method selects the records from SecUserOperations_AView table with
	 * UsrID condition
	 * 
	 * @param secuser
	 *            (SecUser)
	 * @return List<SecurityUserOperations>**/
	 
	public List<SecurityUserOperations> getSecUserOperationsByUsrID(
			SecurityUser secUser, String type) {
		logger.debug("Entering ");
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT  UsrOprID,UsrID,OprID,");
		selectSql
				.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql
					.append(",lovDescFirstName,lovDescMiddleName,lovDescLastName,lovDescOprCd,lovDescOprDesc, lovDescUsrFName,lovDescUsrMName,lovDescUsrLName ");
		}
		selectSql.append(" FROM SecUserOperations");
		selectSql.append(StringUtils.trimToEmpty(type));

		selectSql.append(" where UsrID=:UsrID");
		logger.debug("selectSql : " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				secUser);
		RowMapper<SecurityUserOperations> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SecurityUserOperations.class);
		logger.debug("Leaving ");
		return this.JdbcTemplate.query(selectSql.toString(),
				beanParameters, typeRowMapper);
	}

	/**
	 * This Method selects the records from UserOperations_AView table with
	 * UsrIDand RoleID condition
	 * 
	 * @param userId
	 *            (long)
	 * @param oprId
	 *            (long)
	 * @return secUserOperations (SecurityUserOperations)
	 */
	public SecurityUserOperations getUserOperationsByUsrAndRoleIds(long userId, long oprId) {
		logger.debug("Entering ");

		SecurityUserOperations secUserOperations = getSecurityUserOperations();
		secUserOperations.setUsrID(userId);
		secUserOperations.setOprID(oprId);

		StringBuilder selectSql = new StringBuilder();
		selectSql
				.append("SELECT  UsrOprID,UsrID,OprID,Version,LastMntBy,LastMntOn");
		selectSql
				.append(",RecordStatus,RoleCode,NextRoleCode,TaskId,RecordType,WorkflowId ");
		selectSql
				.append("FROM SecUserOperations where UsrID=:UsrID and RoleID=:RoleID");
		logger.debug("selectSql : " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				secUserOperations);
		RowMapper<SecurityUserOperations> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SecurityUserOperations.class);

		try {
			secUserOperations = this.JdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			secUserOperations = null;
		}

		logger.debug("Leaving ");
		return secUserOperations;
	}

	/**
	 * This method deletes the record from SecUserOperations with UsrID and
	 * RoleID condition
	 * 
	 * @param securityUserOperations
	 *            (SecurityUserOperations)
	 * @throws DataAccessException
	 */
	@SuppressWarnings("serial")
	public void delete(SecurityUserOperations securityUserOperations,
			String type) {
		logger.debug("Entering ");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(
				"Delete from SecUserOperations");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where UsrID=:UsrID and OprID =:OprID");
		logger.debug("deleteSql:" + deleteSql);

		try {
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
					securityUserOperations);
			recordCount = this.JdbcTemplate.update(
					deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",
						securityUserOperations.getUsrID(),
						securityUserOperations.getUserDetails()
								.getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};

			}
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			ErrorDetails errorDetails = getError("41006",
					securityUserOperations.getUsrID(), securityUserOperations
							.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method for Deletion of SecurityUserOperations Related List of
	 * SecurityUser
	 */
	public void deleteById(final long usrID, String type) {
		logger.debug("Entering");
		SecurityUserOperations userOperations = getSecurityUserOperations();
		userOperations.setUsrID(usrID);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From SecUserOperations");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where UsrID =:UsrID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				userOperations);
		this.JdbcTemplate.update(deleteSql.toString(),
				beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method inserts new record into SecUserOperations table
	 * 
	 * @param securityUserOperations
	 *            (SecurityUserOperations)
	 */

	public long save(SecurityUserOperations securityUserOperations, String type) {
		logger.debug("Entering ");

		if (securityUserOperations.getOprID() == Long.MIN_VALUE) {
			securityUserOperations.setId(getNextidviewDAO().getNextId("SeqSecUserOperations"));
			logger.debug("get NextID:" + securityUserOperations.getId());
		}

		StringBuilder sql = new StringBuilder(
				"INSERT INTO SecUserOperations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(UsrOprID,UsrID,OprID,Version,LastMntBy");
		sql.append(",LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		sql.append(" Values( :UsrOprID,:UsrID,:OprID,:Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode");
		sql.append(",:NextRoleCode,:TaskId,:NextTaskId,:RecordType,:WorkflowId) ");
		logger.debug("insertSql:" + sql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUserOperations);
		this.JdbcTemplate.update(sql.toString(),
				beanParameters);
		logger.debug("Leaving ");
		return securityUserOperations.getId();
	}

	/**
	 * This method updates the Record SecUsers or SecUsers_Temp. if Record not
	 * updated then throws DataAccessException with error 41004. update Security
	 * Users by key UsrID and Version
	 * 
	 * @param SecurityUsers
	 *            (securityUser)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void update(SecurityUserOperations securityUserOperations,
			String type) {
		logger.debug("Entering ");
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Update SecUserOperations");
		sql.append(StringUtils.trimToEmpty(type));
		sql
		.append(" Set UsrOprID = :UsrOprID, UsrID = :UsrID, OprID = :OprID,  ");
		sql
		.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		sql
		.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, ");
		sql
		.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		sql.append(" Where UsrOprID =:UsrOprID");
		if (!type.endsWith("_Temp")) {
			sql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql:" + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				securityUserOperations);
		recordCount = this.JdbcTemplate.update(
				sql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004",
					securityUserOperations.getId(), securityUserOperations
					.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method get RoleIds count from SecUserOperations_View
	 * 
	 * @param RoleId
	 *            (long)
	 * @return List<Long RoleIDs>
	 */
	public int getRoleIdCount(long roleId) {
		int count;
		logger.debug("Entering ");
		Map<String, Long> namedParamters = Collections.singletonMap("RoleId", roleId);
		StringBuilder selectSql = new StringBuilder(
				"SELECT COUNT(*) FROM UserOperationRoles_View where RoleId=:RoleId ");
		logger.debug("selectSql: " + selectSql.toString());

		try {
			count = this.JdbcTemplate
					.queryForObject(selectSql.toString(), namedParamters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}

		logger.debug("Leaving getRoleIdCount()");
		return count;
	}

	/**
	 * This method get UserId count from UserOperations_View
	 * 
	 * @param RoleId
	 *            (long)
	 * @return List<Long RoleIDs>
	 */
	public int getUserIdCount(long userId) {
		int status;
		logger.debug("Entering ");
		Map<String, Long> namedParamters = Collections.singletonMap("UsrID", userId);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) FROM UserOperationRoles_View where UsrID=:UsrID ");
		logger.debug("selectSql: " + selectSql.toString());

		try {
			status = this.JdbcTemplate.queryForObject(selectSql.toString(), namedParamters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			status = 0;
		}

		logger.debug("Leaving getRoleIdCount()");
		return status;
	}

	/**
	 * This method fetches the records from SecOperations_View a) if isAssigned
	 * is "true" fetches assigned Operations from SecOperations_View b) if
	 * isAssigned is "false" fetches unassigned Operations from
	 * SecOperations_View
	 * 
	 * @param userId
	 *            (long)
	 * @param isAssigned
	 *            (boolean)
	 * @return SecurityRoleList (ArrayList)
	 * 
	 */
	@Override
	public List<SecurityOperation> getOperationsByUserId(long userId,
			boolean isAssigned) {
		logger.debug("Entering ");
		List<SecurityOperation> secOperationsList = new ArrayList<SecurityOperation>();
		SecurityUser user = new SecurityUser();
		user.setUsrID(userId);
		StringBuilder selectSql = new StringBuilder();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				user);
		RowMapper<SecurityOperation> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SecurityOperation.class);

		if (isAssigned) {
			selectSql.append("select * from SecOperations_View where OprID in");
			selectSql
					.append(" (select RoleID from UserRoles_AView where UsrID = :UsrID)");
		} else {
			selectSql
					.append("select * from SecOperations_View where OprID not in");
			selectSql
					.append(" (select RoleID from UserRoles_AView where UsrID = :UsrID)");
		}
		logger.debug("selectSql:" + selectSql);
		secOperationsList = this.JdbcTemplate.query(
				selectSql.toString(), beanParameters, typeRowMapper);
		logger.debug("Leaving ");
		return secOperationsList;
	}
	
	@Override
	public int getOprById(long oprID, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append("SELECT count(*) FROM SecUserOperations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where OprID = :OprID");
		logger.debug("selectSql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("OprID", oprID);

		try {
			return this.JdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return 0;
	}

	private ErrorDetails getError(String errorId, long usrId,
			String userLanguage) {
		String[][] parms = new String[2][1];

		parms[1][0] = String.valueOf(usrId);
		parms[0][0] = PennantJavaUtil.getLabel("label_UsrID") + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(
				PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]),
				userLanguage);
	}
	
	
	@Override
	public List<String> getUsersByRoles(String[] roleCodes) {
		logger.debug("Entering");

		// Build the parameter source
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RoleCodes", Arrays.asList(roleCodes));

		// Build the SQL
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct U.UsrLogin");
		sql.append(" from SecUsers U");
		sql.append(" inner join SecUserOperations UO on UO.UsrID = U.UsrID ");
		sql.append(" inner join SecOperationRoles OPR on OPR.OprID = UO.OprID  ");
		sql.append(" inner join SecRoles R on R.RoleID = OPR.RoleID");
		sql.append(" where R.RoleCd in (:RoleCodes)");
		logger.debug("SQL: " + sql.toString());

		logger.debug("Leaving ");
		return this.JdbcTemplate.queryForList(sql.toString(), source, String.class);
	}

	/**
	 * Method for fetch list of User mails Based on Role Code Details
	 * 
	 * @param roleIds
	 * @return
	 */
	@Override
	public List<String> getUsrMailsByRoleIds(String roleIds) {
		logger.debug("Entering ");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RoleIds", Arrays.asList(roleIds.split(",")));

		StringBuilder selectSql = new StringBuilder("SELECT DISTINCT UsrEmail from UserOperationRoles_View ");
		selectSql.append(" WHERE RoleId IN (:RoleIds) AND COALESCE(UsrEmail, ' ') <> ' '  ");

		logger.debug("selectSql:" + selectSql);
		logger.debug("Leaving ");
		return this.JdbcTemplate.queryForList(selectSql.toString(), source, String.class);
	}

	
}