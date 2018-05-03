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
 * FileName    		:  SecurityGroupDAOImpl.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-08-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.administration.SecurityGroupDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>SecurityGroup model</b> class.<br>
 * 
 */
public class SecurityGroupDAOImpl extends BasisNextidDaoImpl<SecurityGroup> implements SecurityGroupDAO {
	private static Logger logger = Logger.getLogger(SecurityGroupDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public SecurityGroupDAOImpl() {
		super();
	}
	
	

	/**
	 * Fetch the Record  SecurityGroup details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityGroup
	 */
	@Override
	public SecurityGroup getSecurityGroupById(final long id, String type) {
		logger.debug("Entering");
		SecurityGroup securityGroup = new SecurityGroup();
		securityGroup.setId(id);

		StringBuilder   selectSql = new StringBuilder("Select GrpID, GrpCode, GrpDesc , ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From SecGroups");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GrpID =:GrpID");
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityGroup);
		RowMapper<SecurityGroup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityGroup.class);

		try{
			securityGroup = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), 
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			securityGroup = null;
		}
		logger.debug("Leaving");
		return securityGroup;
	}

	/**
	 * Fetch the Record  SecurityGroup details by key field
	 * 
	 * @param  String(grpCode),
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityGroup
	 */
	@Override
	public SecurityGroup getSecurityGroupByCode(final String  grpCode, String type) {

		logger.debug("Entering ");

		SecurityGroup securityGroup = new SecurityGroup();
		securityGroup.setGrpCode(grpCode);

		StringBuilder selectSql = 	new StringBuilder("Select GrpID, GrpCode, GrpDesc, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From SecGroups");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GrpCode =:GrpCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityGroup);
		RowMapper<SecurityGroup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityGroup.class);

		try{
			securityGroup = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			securityGroup = null;
		}
		logger.debug("Leaving ");
		return securityGroup;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the SecGroups or SecGroups_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete SecurityGroup by key GrpID
	 * 
	 * @param SecurityGroup (securityGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(SecurityGroup securityGroup,String type) {

		logger.debug("Entering ");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From SecGroups");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where GrpID =:GrpID");

		logger.debug("deleteSql: " + deleteSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityGroup);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug("Leaving ");

	}

	/**
	 * This method insert new Records into SecGroups or SecGroups_Temp.
	 * it fetches the available Sequence form SeqSecGroups by using 
	 * getNextidviewDAO().getNextId() method.  
	 *
	 * save SecurityGroup 
	 * 
	 * @param SecurityGroup (securityGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(SecurityGroup securityGroup,String type) {
		logger.debug("Entering ");
		
		if (securityGroup.getId()==Long.MIN_VALUE){
			securityGroup.setId(getNextidviewDAO().getNextId("SeqSecGroups"));
			logger.debug("get NextID:"+securityGroup.getId());
		}

		StringBuilder insertSql =new StringBuilder("Insert Into SecGroups");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (GrpID, GrpCode, GrpDesc, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId )");
		insertSql.append(" Values(:GrpID, :GrpCode, :GrpDesc, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId )");
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityGroup);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
		return securityGroup.getId();
	}

	/**
	 * This method updates the Record SecGroups or SecGroups_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update SecurityGroup by key GrpID and Version
	 * 
	 * @param SecurityGroup (securityGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(SecurityGroup securityGroup,String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		StringBuilder updateSql =new StringBuilder("Update SecGroups");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set GrpCode = :GrpCode, GrpDesc = :GrpDesc, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, " );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, ");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where GrpID =:GrpID");

		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityGroup);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}
}