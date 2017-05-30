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
 * FileName    		:  DataImportDAOImpl.java                                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-03-2012    														*
 *                                                                  						*
 * Modified Date    :  20-03-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 **/
package com.pennant.backend.dao.messages.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.messages.OfflineUserMessagesBackupDAO;
import com.pennant.backend.model.messages.OfflineUsersMessagesBackup;

/**
 * @author s057
 *
 */
public class OfflineUserMessagesBackupDAOImpl implements OfflineUserMessagesBackupDAO {
	Logger logger=Logger.getLogger(OfflineUserMessagesBackupDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public OfflineUserMessagesBackupDAOImpl() {
		super();
	}

	/**
	 * This Method selects the records from UserRoles_AView table with UsrID condition
	 * @param secuser(SecUser)
	 * @return List<OfflineUsersMessagesBackup>
	 */
	@Override
	public List<OfflineUsersMessagesBackup> getMessagesBackupByUserId(String toUsrId){
		logger.debug("Entering ");
		OfflineUsersMessagesBackup offlineUsersMessagesBackup=new OfflineUsersMessagesBackup();
		offlineUsersMessagesBackup.setToUsrID(toUsrId);
		StringBuilder  selectSql = new StringBuilder ();
		selectSql.append("SELECT  TOUSRID,FROMUSRID,SENDTIME,MESSAGE");
		selectSql.append("  FROM OFFLINEUSRMESSAGESBACKUP where ToUsrID=:ToUsrID order by SENDTIME Asc ");
		logger.debug("selectSql : " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(offlineUsersMessagesBackup);
		RowMapper<OfflineUsersMessagesBackup> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(OfflineUsersMessagesBackup.class);
		logger.debug("Leaving ");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			return null;
		}	
	}

	@Override
	public void save(List<OfflineUsersMessagesBackup> offlineusrmsgBkpList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into OFFLINEUSRMESSAGESBACKUP" );
		insertSql.append(" (TOUSRID, FROMUSRID, SENDTIME, MESSAGE)" );
		insertSql.append(" Values(:ToUsrID, :FromUsrID, :SendTime, :Message)" );		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(offlineusrmsgBkpList.toArray());

       this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");


	}

	@Override
	public void delete(String toUsrId) {
		logger.debug("Entering");
		Map<String, String> namedParamters=Collections.singletonMap("TOUSRID", toUsrId);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From OFFLINEUSRMESSAGESBACKUP" );
		deleteSql.append(" Where TOUSRID =:TOUSRID ");
		logger.debug("deleteSql: "+ deleteSql.toString());

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), namedParamters);

		logger.debug("Leaving");
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}
