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
 * FileName    		:  AuthorizationDetailDAOImpl.java                                      * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2012    														*
 *                                                                  						*
 * Modified Date    :  21-06-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.masters.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.masters.AuthorizationDetailDAO;
import com.pennant.backend.model.masters.AuthorizationDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;

/**
 * DAO methods implementation for the <b>AuthorizationDetail model</b> class.<br>
 * 
 */
public class AuthorizationDetailDAOImpl extends
		BasisNextidDaoImpl<AuthorizationDetail> implements
		AuthorizationDetailDAO {
	private static Logger logger = Logger
			.getLogger(AuthorizationDetailDAOImpl.class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public AuthorizationDetail getAuthorizationDetail() {
		logger.debug("Entering");

		AuthorizationDetail authorizationDetail = new AuthorizationDetail();

		logger.debug("Leaving");
		return authorizationDetail;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
	}

	@Override
	public long save(AuthorizationDetail authorizationDetail, String type) {
		logger.debug("Entering");
		if (authorizationDetail.getId() == Long.MIN_VALUE) {
			authorizationDetail.setId(getNextidviewDAO().getNextId("SeqAuthDetails"));
			logger.debug("get NextID:" + authorizationDetail.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into AuthDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (UPPAuthId, AuthChannelId, AuthChannelIP, Status");
		insertSql
				.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql
				.append(" Values(:UPPAuthId, :AuthChannelId, :AuthChannelIP, :Status");
		insertSql
				.append(", :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				authorizationDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(),
				beanParameters);
		logger.debug("Leaving");
		return authorizationDetail.getId();
	}

	@Override
	public void update(AuthorizationDetail authorizationDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update AuthDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
				.append(" Set AuthChannelId = :AuthChannelId, AuthChannelIP = :AuthChannelIP, Status = :Status");
		updateSql
				.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where UPPAuthId =:UPPAuthId ");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				authorizationDetail);
		recordCount = this.namedParameterJdbcTemplate.update(
				updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<AuthorizationDetail> getAuthorizationDetails(String channelCode, String channelIP) {
		logger.debug("Entering");
		AuthorizationDetail authorizationDetail = getAuthorizationDetail();
		authorizationDetail.setChannelCode(channelCode);
		authorizationDetail.setAuthChannelIP(channelIP);

		StringBuilder selectSql = new StringBuilder(
				"Select UPPAuthId, AuthChannelId,ChannelName,ChannelCode, AuthChannelIP, Status");
		selectSql
				.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From AuthDetails_AView");
		selectSql
				.append(" Where ChannelCode =:ChannelCode AND AuthChannelIP=:AuthChannelIP");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				authorizationDetail);
		RowMapper<AuthorizationDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AuthorizationDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),
				beanParameters, typeRowMapper);
	}

}