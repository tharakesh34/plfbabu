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
 * FileName    		:  AuditLogDAOImpl.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.audit.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.audit.AuditLogDAO;
import com.pennant.backend.model.AuditLogDetils;
import com.pennanttech.pff.core.util.ModuleUtil;

public class AuditLogDAOImpl implements AuditLogDAO{
	private static Logger logger = Logger.getLogger(AuditLogDAOImpl.class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public AuditLogDAOImpl() {
		super();
	}
	
	public List<AuditLogDetils> getLogDetails(String modelName,String whereCond){
	
		String selectListSql = 	"Select AuditId,AuditDate,AuditImage,Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType" +
		" From Adt"+ StringUtils.trimToEmpty(ModuleUtil.getTableName(modelName)) +
		" Where AuditImage='W' " + whereCond + " ORDER BY AuditDate,AuditId,AuditImage,Version " ;
		logger.debug("selectListSql: " + selectListSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new AuditLogDetils());
		RowMapper<AuditLogDetils> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AuditLogDetils.class);
		return this.namedParameterJdbcTemplate.query(selectListSql, beanParameters,typeRowMapper);
		
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	@Override
	public List<AuditLogDetils> getLogDetails(String moduleName, String[] keyFields,String recordRole,long currentUser, Object beanObject) {
		String tableName=StringUtils.trimToEmpty(ModuleUtil.getTableName(moduleName));
		StringBuffer keyFieldsSql = new StringBuffer();
		
		for (int i = 0; i < keyFields.length; i++) {
			keyFieldsSql.append(" AND ");
			keyFieldsSql.append(keyFields[i]);
			keyFieldsSql.append("=:");
			keyFieldsSql.append(keyFields[i]);
		} 
		
		String strKeyFields = keyFieldsSql.toString();
				
		StringBuffer selectListSql = new StringBuffer();
		selectListSql.append("Select AuditId,AuditDate,AuditImage,Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType FROM Adt");
		selectListSql.append(tableName);
		selectListSql.append(" Where LastMntBy=");
		selectListSql.append(currentUser);
		if(recordRole.contains(",")){
			String roleCodes[] = recordRole.split(",");
			for(int j=0;j<roleCodes.length;j++){
				selectListSql.append(" AND RoleCode<>'");
				selectListSql.append(roleCodes[j]);
				selectListSql.append("' ");
			}
		}else {
			selectListSql.append(" AND RoleCode<>'");
			selectListSql.append(recordRole);
			selectListSql.append("' ");
		}
		selectListSql.append(" AND AuditImage='W' ");
		selectListSql.append(strKeyFields);
		selectListSql.append("  AND AuditId > ");
		selectListSql.append(" (Select COALESCE(max(AuditId), 0) AuditId From Adt");
		selectListSql.append(tableName);
		selectListSql.append(" Where AuditImage='W' ");
		selectListSql.append(strKeyFields);
		selectListSql.append("  AND NextRoleCode=' ') ");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(beanObject);
		RowMapper<AuditLogDetils> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AuditLogDetils.class);
		
		logger.debug("selectListSql: " + selectListSql);
		
		return this.namedParameterJdbcTemplate.query(selectListSql.toString(), beanParameters,typeRowMapper);
	}
}
