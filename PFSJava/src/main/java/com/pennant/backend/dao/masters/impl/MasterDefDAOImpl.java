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
 * * FileName : MasterDefDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-05-2018 * * Modified
 * Date : 19-05-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-05-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.masters.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.pennant.backend.dao.masters.MasterDefDAO;
import com.pennant.backend.model.MasterDef;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>MasterDef model</b> class.<br>
 * 
 */
public class MasterDefDAOImpl extends BasicDao<MasterDef> implements MasterDefDAO {
	private static Logger logger = LogManager.getLogger(MasterDefDAOImpl.class);

	public MasterDefDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record MasterDef details by key field
	 * 
	 * @param masterType (String)
	 * @param keyType    (String)
	 * 
	 * @return KeyCode (String)
	 */
	@Override
	public String getMasterCode(String masterType, String keyType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Key_Code");
		sql.append(" From Master_Def");
		sql.append(" Where Master_Type = ? and Key_type = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> rs.getString(1), masterType, keyType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Record is not found in Master_Def table for the specified Master_Type >> {} and Key_type >> {}",
					masterType, keyType);
		}

		return "";
	}

	/**
	 * Fetch the Record MasterDef details by key field
	 * 
	 * @param masterType (String)
	 * @param KeyCode    (String)
	 * 
	 * @return keyType (String)
	 */
	@Override
	public String getMasterKeyTypeByCode(String masterType, String keyCode) {
		MasterDef masterDef = new MasterDef();
		masterDef.setMasterType(masterType);
		masterDef.setKeyCode(keyCode);

		StringBuilder sql = new StringBuilder("Select Key_type");
		sql.append(" From Master_Def");
		sql.append(" Where Master_Type =:MasterType and Key_Code=:keyCode ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(masterDef);
		logger.debug("Leaving");

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in Master_Def table for the specified Master_Type >> {} and Key_Code >> {} ",
					masterType, keyCode);
		}

		return null;
	}

	@Override
	public Map<String, String> getMasterDef(String masterType) {
		logger.debug(Literal.ENTERING);
		Map<String, String> map = new HashMap<String, String>();

		StringBuilder selectSql = new StringBuilder(" SELECT Key_type, Key_Code From Master_Def ");
		selectSql.append(" where Master_Type =:MasterType ");

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("MasterType", masterType);

		SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(selectSql.toString(), parameterSource);
		while (rowSet.next()) {
			map.put(rowSet.getString(1), rowSet.getString(2));
		}

		logger.debug(Literal.LEAVING);
		return map;
	}
}
