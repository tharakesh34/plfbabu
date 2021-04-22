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
 * FileName    		:  AcademicDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.batchupload.fileprocessor.dao.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.batchupload.fileprocessor.dao.BatchUploadConfigDAO;
import com.pennant.batchupload.model.BatchUploadConfig;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

/**
 * Data access layer implementation for <code>BatchUploadConfig</code> with set of CRUD operations.
 */
public class BatchUploadConfigDAOImpl extends BasicDao<BatchUploadConfig> implements BatchUploadConfigDAO {
	private static Logger logger = LogManager.getLogger(BatchUploadConfigDAOImpl.class);

	public BatchUploadConfigDAOImpl() {
		super();
	}

	@Override
	public List<BatchUploadConfig> getActiveConfiguration() {
		logger.debug("entering");
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" Label, Url, IsActive, ExtraHeader");
		sql.append(" From BatchUploadConfig");
		sql.append(" Where IsActive = 1");

		logger.trace("SQL" + sql.toString());

		BatchUploadConfig batchUploadConfig = new BatchUploadConfig();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(batchUploadConfig);

		logger.debug("leaving");
		return jdbcTemplate.query(sql.toString(), beanParameters,
				BeanPropertyRowMapper.newInstance(BatchUploadConfig.class));
	}
}
