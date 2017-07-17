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
 * FileName    		:  SecurityOperationRolesDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-03-2014   														*
 *                                                                  						*
 * Modified Date    :  10-03-2014      														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-03-2014         Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.administration.SecurityOperationRolesDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityOperationRoles;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUserOperations;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

public class SecurityOperationRolesDAOImpl extends BasisNextidDaoImpl<SecurityOperation> implements
		SecurityOperationRolesDAO {
	private static Logger logger = Logger.getLogger(SecurityOperationRolesDAOImpl .class);
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public SecurityOperationRolesDAOImpl() {
		super();
	}
	
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setNamedParameterJdbcTemplate(
			NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.jdbcTemplate = namedParameterJdbcTemplate;
	}

	/**
	 * @param dataSource the dataSource to set
	 */

	@Override
	public SecurityOperationRoles getSecurityOperationRoles() {
		logger.debug("Entering ");
		return new SecurityOperationRoles();
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */

	public void setDataSource(DataSource dataSource) {	
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	/**
	 * This Method selects the records from SecOperationRoles_AView table with UsrID condition
	 * @param secOperation(secOperation)
	 * @return List<SecurityOperationRoles>
	 */
	@Override
	public List<SecurityOperationRoles> getSecOperationRolesByOprID(SecurityOperation secOperation, String type) {
		logger.debug("Entering ");
		StringBuilder  selectSql = new StringBuilder ();
		selectSql.append("SELECT  OprRoleID,OprID,RoleID,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescRoleCd,lovDescRoleDesc ");
		}
		selectSql.append(" FROM SecOperationRoles");
		selectSql.append(StringUtils.trimToEmpty(type));
		
		selectSql.append(" where OprID=:OprID");
		logger.debug("selectSql : " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secOperation);
		RowMapper<SecurityOperationRoles> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SecurityOperationRoles.class);
		logger.debug("Leaving ");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	/**
	 * This method deletes the record from securityOperationRoles with UsrID and RoleID condition
	 * @param securityOperationRoles(SecurityOperationRoles)
	 * @throws DataAccessException
	 */
	@Override
	public void delete(SecurityOperationRoles securityOperationRoles,String type) {
			
		logger.debug("Entering ");
		int recordCount = 0;
		StringBuilder  deleteSql = new StringBuilder ("Delete from SecOperationRoles");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where OprID=:OprID and RoleID =:RoleID");
		logger.debug("deleteSql:"+deleteSql);
		
		try{
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityOperationRoles);
			recordCount = this.jdbcTemplate.update( deleteSql.toString(),beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving ");
	}  
	/**
	 * Method for Deletion of SecurityOperationRoles Related List of Securityoperation
	 */
	@Override
	public void deleteById(long oprID, String type) {
		logger.debug("Entering");
		SecurityOperationRoles operationRoles = getSecurityOperationRoles();
		operationRoles.setOprID(oprID);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From SecOperationRoles");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where OprID =:OprID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(operationRoles);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	/**
	 * This method inserts new record into SecOperationRoles table 
	 * @param  securityOperationRoles(SecurityOperationRoles)
	 */
	@Override
	public void save(SecurityOperationRoles securityOperationRoles, String type) {
		logger.debug("Entering ");

		 securityOperationRoles.setId(getNextidviewDAO().getNextId("SeqSecOperationRoles"));
		logger.debug("get NextID:"+ securityOperationRoles.getOprRoleID());
		StringBuilder  insertSql = new StringBuilder ("INSERT INTO SecOperationRoles");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(OprRoleID,OprID,RoleID,Version,LastMntBy");
		insertSql.append(",LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(" Values( :OprRoleID,:OprID,:RoleID,:Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode");
		insertSql.append(",:NextRoleCode,:TaskId,:NextTaskId,:RecordType,:WorkflowId) ");
		logger.debug("insertSql:"+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityOperationRoles);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
	}

	/**
	 * This method fetches the records from SecRoles_View a) if isAssigned is "true" fetches assigned roles from
	 * SecRoles_View b) if isAssigned is "false" fetches unassigned roles from SecRoles_View
	 * 
	 * @param userId
	 *            (long)
	 * @param isAssigned
	 *            (boolean)
	 * @return SecurityRoleList (ArrayList)
	 */
	@Override
	public List<SecurityRole> getRolesByUserId(long roleId, boolean isAssigned) {
		logger.debug("Entering");
		
		SecurityOperation operations = new SecurityOperation();
		String selectSql = "";
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(operations);
		RowMapper<SecurityRole> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRole.class);
		if (isAssigned) {
			selectSql = "select * from secRoles_View where roleid in (select oprid from SecOperationRoles where roleID = :roleID)";
		} else {

			selectSql = "select * from secRoles_View where roleid not in (select oprid from SecOperationRoles where roleID = :roleID)";
		}

		return this.jdbcTemplate.query(selectSql, beanParameters, typeRowMapper);
	}

	/**
	 * This Method selects the records from OperationRoles_AView table with UsrIDand RoleID condition
	 * @param roleId (long)
	 * @return secOperationRoles (SecurityOperationRoles)
	 */
	@Override
	public SecurityOperationRoles getOperationRolesByOprAndRoleIds(long roleId) {
		logger.debug("Entering ");

		SecurityOperationRoles secOperationRoles =getSecurityOperationRoles();
		secOperationRoles.setOprID(roleId);
		secOperationRoles.setRoleID(roleId);
		
		StringBuilder  selectSql = new StringBuilder ();
		selectSql.append("SELECT  OprRoleID,OprID,RoleID,Version,LastMntBy,LastMntOn");
		selectSql.append(",RecordStatus,RoleCode,NextRoleCode,TaskId,RecordType,WorkflowId ");
		selectSql.append("FROM SecOperationRoles where OprID=:OprID and RoleID=:RoleID");
		logger.debug("selectSql : " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secOperationRoles);
		RowMapper<SecurityOperationRoles> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityOperationRoles.class);

		try {
			secOperationRoles = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			secOperationRoles = null;
		}

		logger.debug("Leaving ");
		return secOperationRoles;	

	}

	/**
	 * This method get  RoleIds count from SecOperationRoles_View
	 * @param RoleId (long)
	 * @return List<Long RoleIDs>
	 */
	@Override
	public int getRoleIdCount(long roleId) {
		int status;
		logger.debug("Entering ");
		Map<String, Long> namedParamters=Collections.singletonMap("RoleId", roleId);
		StringBuilder  selectSql = new StringBuilder ("SELECT COUNT(*) FROM SecOperationRoles_View where RoleId=:RoleId ");
		logger.debug("selectSql: " + selectSql.toString());      

		try{
			status=this.jdbcTemplate.queryForObject(selectSql.toString(), namedParamters, Integer.class);
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			status=0;
		}

		logger.debug("Leaving getRoleIdCount()");
		return status;
	}

	/**
	 * This method get  UserId count from SecOperationRoles_View
	 * @param RoleId (long)
	 * @return List<Long RoleIDs>
	 */
	@Override
	public int getOprIdCount(long oprID) {
		int status;
		logger.debug("Entering ");
		Map<String, Long> namedParamters=Collections.singletonMap("UsrID", oprID);
		StringBuilder  selectSql = new StringBuilder ("SELECT COUNT(*) FROM SecOperationRoles_View where OprID=:OprID ");
		logger.debug("selectSql: " + selectSql.toString());      

		try{
			status=this.jdbcTemplate.queryForObject(selectSql.toString(), namedParamters, Integer.class);
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			status=0;
		}

		logger.debug("Leaving getRoleIdCount()");
		return status;
	}

	/**
	 * This method updates the Record Secoperations or SecOperation_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Security Operations by key UsrID and Version
	 * 
	 * @param SecurityOperation (securityOperation)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(SecurityOperationRoles securityOperationRoles, String type) {
		logger.debug("Entering ");
		int recordCount = 0;
		
		StringBuilder   updateSql = new StringBuilder  ("Update SecOperationRoles");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set OprID = :OprID, RoleID = :RoleID,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, ");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where OprRoleID =:OprRoleID");
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql:"+updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityOperationRoles);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}

	/**
	 * This Method selects the records from OperationRoles_AView table with UsrIDand RoleID condition
	 * 
	 * @param userId
	 *            (long)
	 * @param roleId
	 *            (long)
	 * @return secOperationRoles (SecurityOperationRoles)
	 */
	@Override
	public SecurityOperationRoles getOprRolesByRoleAndOprId(long roleID, long oprId) {
		SecurityOperationRoles securityOperationRoles = new SecurityOperationRoles();
		securityOperationRoles.setRoleID(roleID);
		securityOperationRoles.setOprID(oprId);
		StringBuilder selectSql = new StringBuilder("SELECT  OprRoleID,OprID,RoleID,Version");
		selectSql.append(",LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,");
		selectSql.append("TaskId,NextTaskId,RecordType,WorkflowId,LovDescOprCode");
		selectSql.append(",LovDescRoleCode   FROM SecOperationRoles_Aview where RoleID =:RoleID and OprID=:OprID");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityOperationRoles);
		RowMapper<SecurityOperationRoles> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SecurityOperationRoles.class);

		try {
			securityOperationRoles = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			securityOperationRoles = null;
		}
		return securityOperationRoles;
	}

	/**
	 * This method deletes the record from SecOperationRoles with UsrID and RoleID condition
	 * @param securityOperationRoles(SecurityOperationRoles)
	 * @throws DataAccessException
	 */
	@Override
	public void delete(SecurityOperationRoles securityOperationRoles) {
		 logger.debug("Entering ");
		 int recordCount = 0;
		 String deleteRoleGroupSql = "Delete from SecOperationRoles  where   OprID=:OprID and RoleID =:RoleID";
		 logger.debug("deleteSql-:"+deleteRoleGroupSql);
		 try{
			 SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityOperationRoles);  
			 recordCount = this.jdbcTemplate.update( deleteRoleGroupSql,beanParameters);

			 if (recordCount <= 0) {
				throw new ConcurrencyException();
			 }
		 }catch(DataAccessException e){
			throw new DependencyFoundException(e);
		 }
		 logger.debug("Leaving ");
	 }

	/**
	 * This Method selects the records from SecOperationRoles_AView table with UsrID condition
	 * @param secOpr(SecOpr)
	 * @return List<SecurityOperationRoles>
	 */
	@Override
	public List<SecurityOperationRoles> getSecOprRolesByOprID(
			SecurityUserOperations secUserOpr, String type) {
		logger.debug("Entering ");
		StringBuilder  selectSql = new StringBuilder ();
		selectSql.append("SELECT  OprRoleID,OprID,RoleID,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescRoleCd,lovDescRoleDesc ");
		}
		selectSql.append(" FROM SecOperationRoles");
		selectSql.append(StringUtils.trimToEmpty(type));
		
		selectSql.append(" where OprID=:OprID");
		logger.debug("selectSql : " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secUserOpr);
		RowMapper<SecurityOperationRoles> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SecurityOperationRoles.class);
		logger.debug("Leaving ");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}

	// This method is used to get the count from secoperationroles table.
	@Override
	public int getOprById(long oprID, String type) {
		logger.debug("Entering");
		
		MapSqlParameterSource source  = null;
		StringBuilder sql = null;
		
		sql = new StringBuilder();
		sql.append("SELECT count(*) FROM SecOperationRoles");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where OprID = :OprID");
		logger.debug("selectSql: " + sql.toString());
		
		source = new MapSqlParameterSource();
		source.addValue("OprID", oprID);
		
		try{
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return 0;
	}
}
