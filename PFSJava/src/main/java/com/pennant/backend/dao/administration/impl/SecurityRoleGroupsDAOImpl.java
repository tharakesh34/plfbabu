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
 * FileName    		: SecurityRoleGroupsDAOImpl.java														*                           
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
package com.pennant.backend.dao.administration.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.administration.SecurityRoleGroupsDAO;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class SecurityRoleGroupsDAOImpl extends SequenceDao<SecurityRole> implements SecurityRoleGroupsDAO {
	private static Logger logger = Logger.getLogger(SecurityRoleGroupsDAOImpl.class);
	
	 public SecurityRoleGroupsDAOImpl() {
		 super();
	 }
	 
	
	 /**
	  * This method returns new SecurityRoleGroups Object
	  */
	 public SecurityRoleGroups getSecRoleGroups(){
		 logger.debug("Entering ");
		 return new SecurityRoleGroups();
	 }

	/**
	 * This method Selects the SecurityRoleGroups records from SecRoleGroups
	 * 
	 * @param secRoles
	 *            (SecurityRoleGroups)
	 * @return {@link List} of {@link SecurityRoleGroups}
	 */
	public List<SecurityRoleGroups> getSecRoleGroupsByRoleID(SecurityRole secRoles) {
		logger.debug("Entering ");

		List<SecurityRoleGroups> list = new ArrayList<SecurityRoleGroups>();
		StringBuilder selectSql = new StringBuilder(" SELECT  RoleGrpID , GrpID , RoleID , ");
		selectSql.append(" Version , LastMntBy , LastMntOn , RecordStatus , RoleCode , ");
		selectSql.append(" NextRoleCode , TaskId , NextTaskId , RecordType , WorkflowId , ");
		selectSql.append(" LovDescGrpCode , LovDescRoleCode ");
		selectSql.append(" FROM SecRoleGroups_AView where RoleID =:RoleID ");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		RowMapper<SecurityRoleGroups> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SecurityRoleGroups.class);
		try {
			list = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			list = null;
		}
		logger.debug("Leaving ");
		return list;
	}

	/**
	 * This method inserts new record into SecRoleGroups
	 * 
	 * @param securityRoleGroups
	 *            (SecurityRoleGroups)
	 */
	public void save(SecurityRoleGroups securityRoleGroups) {
		 logger.debug("Entering");

		if (securityRoleGroups.getRoleGrpID() == Long.MIN_VALUE) {
			 securityRoleGroups.setId(getNextId("SeqSecRoleGroups"));
			 logger.debug("get NextID:"+ securityRoleGroups.getRoleGrpID());
		 }

		 StringBuilder   insertSql = new StringBuilder(" INSERT INTO SecRoleGroups " );
		 insertSql.append(" ( RoleGrpID , GrpID , RoleID , " );
		 insertSql.append(" Version , LastMntBy , LastMntOn , RecordStatus , RoleCode , " );
		 insertSql.append(" NextRoleCode , TaskId , NextTaskId , RecordType , WorkflowId ) ");
		 insertSql.append(" VALUES (:RoleGrpID , :GrpID , :RoleID , " );
		 insertSql.append(" :Version , :LastMntBy , :LastMntOn , :RecordStatus, ");
		 insertSql.append(" :RoleCode , :NextRoleCode , :TaskId , :NextTaskId , :RecordType , :WorkflowId ) ");
		 logger.debug("insertSql:" + insertSql.toString());
		try {
			 SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityRoleGroups);
			 this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
		}
		
		logger.debug("Leaving");
	}
	
	 /**
	  * This method deletes record from SecRoleGroups  with GrpID and RoleID condition
	  * @param securityRoleGroups (SecurityRoleGroups)
	  * @throws DataAccessException
	  * 
	  */
	public void delete(SecurityRoleGroups securityRoleGroups) {
		 logger.debug("Entering");

		 int recordCount = 0;
		 String deleteRoleGroupSql = " Delete from SecRoleGroups where GrpID=:GrpID and RoleID =:RoleID";
		 logger.debug("deleteSql-:"+deleteRoleGroupSql);
		try {
			 SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityRoleGroups);  
			 recordCount = this.jdbcTemplate.update( deleteRoleGroupSql,beanParameters);

			 if (recordCount <= 0) {
				throw new ConcurrencyException();
			 }
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	 }
	 /**
	  * This method deletes record from SecRoleGroups  with RoleID condition
	  * @throws DataAccessException
	  * 
	  */
	 public  void  deleteByRoleID(SecurityRoleGroups securityRoleGroups){
		 logger.debug("Entering ");
		 String deleteUserRolesSql = "Delete from SecRoleGroups where RoleID =:RoleID";
		 logger.debug("deleteSql:"+deleteUserRolesSql);
		 try{
			 SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityRoleGroups);  
			 this.jdbcTemplate.update( deleteUserRolesSql,beanParameters);
		 }catch(Exception e){
			 logger.warn("Exception: ", e);
		 }

		 logger.debug("Leaving ");
	 }

	 /**
	  * This method get  RoleIds count from SecRoleGroups_view
	  * @param RoleId (long)
	  * @return int
	  */
	 public int getRoleIdCount(long roleId){
		 int status;
		 logger.debug("Entering ");
		 Map<String, Long> namedParamters=Collections.singletonMap("RoleId", roleId);
		 String selectSql = "SELECT COUNT(*) FROM SecRoleGroups_view where RoleId=:RoleId ";
		 logger.debug("selectSql: " + selectSql);      

		 try{
			 status=this.jdbcTemplate.queryForObject(selectSql, namedParamters, Integer.class);
		 }catch (EmptyResultDataAccessException e) {
			 logger.warn("Exception: ", e);
			 status=0;
		 }

		 logger.debug("Leaving ");
		 return status;
	 }

	 /**
	  * This method get  GroupIds count from SecRoleGroups_view
	  * @return int
	  */
	 public int getGroupIdCount(long groupId){
		 int status;
		 logger.debug("Entering ");
		 Map<String, Long> namedParamters=Collections.singletonMap("GrpID", groupId);

		 String selectSql = "SELECT COUNT(*) FROM SecRoleGroups_view where GrpID=:GrpID ";
		 logger.debug("selectSql:" + selectSql);      

		 try{
			 status=this.jdbcTemplate.queryForObject(selectSql, namedParamters, Integer.class);
		 }catch (EmptyResultDataAccessException e) {
			 logger.warn("Exception: ", e);
			 status=0;
		 }

		 logger.debug("Leaving ");
		 return status;
	 }

	/**
	 * This method fetches the records from secGroups_View a) if isAssigned is "true" fetches assigned roles from
	 * secGroups_View b) if isAssigned is "false" fetches unassigned roles from secGroups_View
	 * 
	 * @param roleId
	 *            (long)
	 * 
	 * @param isAssigned
	 *            (boolean)
	 * @return {@link List} of {@link SecurityRoleGroups}
	 **/
	@Override
	public List<SecurityGroup> getGroupsByRoleId(long roleId, boolean isAssigned) {
		SecurityRole roles = new SecurityRole();
		roles.setRoleID(roleId);
		String selectSql = "";
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(roles);
		RowMapper<SecurityGroup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityGroup.class);
		if (isAssigned) {
			selectSql = "select * from secGroups_View where grpid in (select grpid from secRoleGroups where roleID = :roleID)";
		} else {

			selectSql = "select * from secGroups_View where grpid not in (select grpid from secRoleGroups where roleID = :roleID)";
		}

		return this.jdbcTemplate.query(selectSql, beanParameters, typeRowMapper);
	}

	/**
	 * This method fetches records from SecRoleGroups_AView with "roleID and groupId" condition
	 * 
	 * @param roleID
	 *            (long)
	 * @param groupId
	 *            (long)
	 * @return secRolesGroups (SecurityRoleGroups)
	 */
	public SecurityRoleGroups getRoleGroupsByRoleAndGrpId(long roleID, long groupId) {

		SecurityRoleGroups secRolesGroups = new SecurityRoleGroups();
		secRolesGroups.setRoleID(roleID);
		secRolesGroups.setGrpID(groupId);
		StringBuilder selectSql = new StringBuilder("SELECT  RoleGrpID , GrpID , RoleID , ");
		selectSql.append(" Version , LastMntBy , LastMntOn , RecordStatus , RoleCode , NextRoleCode,");
		selectSql.append(" TaskId , NextTaskId , RecordType , WorkflowId , ");
		selectSql.append(" LovDescGrpCode , LovDescRoleCode ");
		selectSql.append(" FROM SecRoleGroups_AView where RoleID =:RoleID and GrpID=:GrpID ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRolesGroups);
		RowMapper<SecurityRoleGroups> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SecurityRoleGroups.class);

		try {
			secRolesGroups = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			secRolesGroups = null;
		}
		return secRolesGroups;
	}

	/**
	 * This method Selects the SecurityRoleGroups records from SecRoleGroups
	 * 
	 * @param secRoles
	 *            (SecurityRoleGroups)
	 * @return List<SecurityRoleGroups>
	 */
	@Override
	public List<SecurityRoleGroups> getRoleGroupsByRoleID(long roleId, String type) {
		logger.debug("Entering ");
		List<SecurityRoleGroups> list = new ArrayList<SecurityRoleGroups>();

		SecurityRoleGroups roleGroups = new SecurityRoleGroups();
		roleGroups.setRoleID(roleId);

		StringBuilder selectSql = new StringBuilder("SELECT  RoleGrpID,GrpID,RoleID,Version");
		selectSql.append(",LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,");
		selectSql.append("TaskId,NextTaskId,RecordType,WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",LovDescGrpCode,LovDescRoleCode ");
		}
		selectSql.append(" FROM SecRoleGroups");
		selectSql.append(type);
		selectSql.append(" WHERE RoleID = :RoleID");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(roleGroups);
		RowMapper<SecurityRoleGroups> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SecurityRoleGroups.class);
		try {
			list = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			list = null;
		}
		logger.debug("Leaving ");
		return list;
	}

	@Override
	public long getNextValue() {
		return getNextId("SeqSecRoleGroups");
	}
}
