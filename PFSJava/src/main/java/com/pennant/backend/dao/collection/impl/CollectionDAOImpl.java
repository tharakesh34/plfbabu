/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CollectionDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified
 * Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.collection.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.collection.CollectionDAO;
import com.pennant.backend.model.collection.Collection;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

/**
 * DAO methods implementation for the <b>Collection model</b> class.<br>
 */
public class CollectionDAOImpl extends BasicDao<Collection> implements CollectionDAO {
	private static Logger logger = LogManager.getLogger(CollectionDAOImpl.class);

	public CollectionDAOImpl() {
		super();
	}

	/**
	 * get the Collection Tables List
	 */
	public List<Collection> getCollectionTablesList() {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder(
				"select TABLE_NAME TableName, Status, ERROR_DESC ErrorMessage, EFFECTED_COUNT InsertCount");
		selectSql.append(" From COLLECTIONS_TABLES");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<Collection> typeRowMapper = BeanPropertyRowMapper.newInstance(Collection.class);
		List<Collection> collections = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

		logger.debug("Leaving");

		return collections;
	}

	/**
	 * get the Collection Tables List
	 */
	public int getCollectionExecutionSts() {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("Select count(*) from DATAEXTRACTIONS where Progress in (0,1)");

		logger.debug("selectSql: " + selectSql.toString());

		int count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);

		logger.debug("Leaving");
		return count;
	}

}