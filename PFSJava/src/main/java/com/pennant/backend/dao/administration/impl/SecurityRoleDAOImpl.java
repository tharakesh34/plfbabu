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
 * FileName    		:  SecurityRoleDAOImpl.java                                             * 	  
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
 *  2-08-2011        Pennant	                 0.1                                        * 
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.administration.SecurityRoleDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>SecurityRole model</b> class.<br>
 * 
 */
public class SecurityRoleDAOImpl extends BasisNextidDaoImpl<SecurityRole> implements SecurityRoleDAO {
	private static Logger logger = Logger.getLogger(SecurityRoleDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public SecurityRoleDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new SecurityRole 
	 * @return SecurityRole
	 */
	@Override
	public SecurityRole getSecurityRole() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SecurityRole");
		SecurityRole securityRole= new SecurityRole();
		if (workFlowDetails!=null){
			securityRole.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return securityRole;
	}

	

	/**
	 * Fetch the Record  SecurityRole details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityRole
	 */
	@Override
	public SecurityRole getSecurityRoleById(final long id, String type) {
		logger.debug("Entering");
		SecurityRole secRoles = new SecurityRole();
		secRoles.setId(id);
		
		StringBuilder   selectSql = new StringBuilder  ("Select RoleID, RoleApp, RoleCd, " );
		selectSql.append(" RoleDesc, RoleCategory , "  );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,lovDescRoleAppName ");	
		}
		
		selectSql.append(" From SecRoles");
		selectSql.append(StringUtils.trimToEmpty(type) );
		selectSql.append(" Where RoleID =:RoleID");
		
		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		RowMapper<SecurityRole> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityRole.class);
		try{
			secRoles = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			secRoles = null;
		}
		logger.debug("Leaving");
		return secRoles;
	}

	/**
	 * Fetch the Record SecurityRole details by key field
	 * 
	 * @param roleCode (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityRole
	 */
	@Override
	public SecurityRole getSecurityRoleByRoleCd(final String roleCd, String type) {
		logger.debug("Entering");
		SecurityRole secRoles = new SecurityRole();
		secRoles.setRoleCd(roleCd);

		StringBuilder   selectSql = new StringBuilder  ("Select RoleID, RoleApp, " );
		selectSql.append(" RoleCd, RoleDesc, RoleCategory , " );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,lovDescRoleAppName ");	
		}
		
		selectSql.append(" From SecRoles");
		selectSql.append(StringUtils.trimToEmpty(type) );
		selectSql.append(" Where RoleCd =:RoleCd");

		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		RowMapper<SecurityRole> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityRole.class);

		try{
			secRoles = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			secRoles = null;
		}
		logger.debug("Leaving");
		return secRoles;
	}

	/**
	 * @param dataSource the dataSource to set
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the SecRoles or SecRoles_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete SecurityRole by key RoleID
	 * 
	 * @param SecurityRole (securityRole)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(SecurityRole secRoles,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		String deleteSql = 	"Delete From SecRoles" + StringUtils.trimToEmpty(type) +
		" Where RoleID =:RoleID";
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		recordCount = this.namedParameterJdbcTemplate.update(deleteSql, beanParameters);
		logger.debug("deleteSql:"+deleteSql);
		try {
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into SecRoles or SecRoles_Temp.
	 * it fetches the available Sequence form SeqSecRoles by using getNextidviewDAO().getNextId() method.  
	 *
	 * save SecurityRole 
	 * 
	 * @param SecurityRole (securityRole)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 */
	public long save(SecurityRole secRoles,String type) {
		logger.debug("Entering");
		if (secRoles.getId()==Long.MIN_VALUE){
			secRoles.setId(getNextidviewDAO().getNextId("SeqSecRoles"));
			logger.debug("get NextID:"+secRoles.getId());
		}

		StringBuilder   insertSql = new StringBuilder("Insert Into SecRoles" );
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RoleID, RoleApp, RoleCd, RoleDesc, RoleCategory, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:RoleID, :RoleApp, :RoleCd, :RoleDesc, :RoleCategory, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql:"+insertSql);
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return secRoles.getId();
	}

	/**
	 * This method updates the Record SecRoles or SecRoles_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update SecurityRole by key RoleID and Version
	 * 
	 * @param SecurityRole (securityRole)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(SecurityRole secRoles, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql =new StringBuilder("Update SecRoles"); 
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set RoleApp = :RoleApp, RoleCd = :RoleCd, " );
		updateSql.append(" RoleDesc = :RoleDesc, RoleCategory = :RoleCategory , ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, ");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, " );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where RoleID =:RoleID");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql:"+updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Record SecurityRole details by key field
	 * 
	 * @param roleCode (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityRole
	 */
	@Override
	public List<SecurityRole> getApprovedSecurityRole() {
		logger.debug("Entering");
		String type = "_View"; //AView
		SecurityRole secRoles = getSecurityRole();

		StringBuilder   selectSql = new StringBuilder  ("Select RoleID, RoleApp, RoleCd, RoleDesc, RoleCategory" );
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,lovDescRoleAppName ");	
		}
		selectSql.append(" From SecRoles_View"); //SecRoles_AView

		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		RowMapper<SecurityRole> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRole.class);
		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}

	@Override
    public List<SecurityRole> getSecurityRole(String roleCode) {
		logger.debug("Entering");
		
		SecurityRole secRoles = new SecurityRole();
		secRoles.setRoleCd(roleCode);
		
		StringBuilder   selectSql = new StringBuilder  ("Select RoleDesc  From SecRoles " );
		selectSql.append(" Where RoleCd =:RoleCd");
		
		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		RowMapper<SecurityRole> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRole.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
    }

	/**
	 * Fetch the Record  SecurityRole details by key field
	 * 			          
	 * @return SecurityRole
	 */
	@Override
	public List<SecurityRole> getApprovedSecurityRoles() {
		logger.debug("Entering");

		SecurityRole secRole = new SecurityRole();
		StringBuilder selectSql = new StringBuilder("SELECT * FROM SecRoles ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRole);
		RowMapper<SecurityRole> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRole.class);

		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.debug(e);
		}
       return null;
	}
}