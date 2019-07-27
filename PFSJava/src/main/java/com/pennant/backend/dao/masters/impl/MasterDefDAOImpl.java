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
 * FileName    		:  MasterDefDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-05-2018    														*
 *                                                                  						*
 * Modified Date    :  19-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-05-2018       Pennant	                 0.1                                            * 
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

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import java.util.HashMap;
import java.util.Map;

import com.pennant.backend.dao.masters.MasterDefDAO;
import com.pennant.backend.model.MasterDef;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>MasterDef model</b> class.<br>
 * 
 */
public class MasterDefDAOImpl extends BasicDao<MasterDef> implements MasterDefDAO {
	private static Logger logger = Logger.getLogger(MasterDefDAOImpl.class);

	public MasterDefDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record MasterDef details by key field
	 * 
	 * @param masterType
	 *            (String)
	 * @param keyType
	 *            (String)
	 * 
	 * @return KeyCode (String)
	 */
	@Override
	public String getMasterCode(String masterType, String keyType) {
		logger.debug("Entering");

		MasterDef masterDef = new MasterDef();
		masterDef.setMasterType(masterType);
		masterDef.setKeyType(keyType);

		StringBuilder selectSql = new StringBuilder("Select Key_Code ");
		selectSql.append(" From Master_Def");
		selectSql.append(" Where Master_Type =:MasterType and Key_type=:KeyType ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(masterDef);
		logger.debug("Leaving");
		String keyCode = "";
		try {
			keyCode = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			return "";
		}
		return keyCode;
	}

	/**
	 * Fetch the Record MasterDef details by key field
	 * 
	 * @param masterType
	 *            (String)
	 * @param KeyCode
	 *            (String)
	 * 
	 * @return keyType (String)
	 */
	@Override
	public String getMasterKeyTypeByCode(String masterType, String keyCode) {
		logger.debug("Entering");

		MasterDef masterDef = new MasterDef();
		masterDef.setMasterType(masterType);
		masterDef.setKeyCode(keyCode);

		StringBuilder selectSql = new StringBuilder("Select Key_type ");
		selectSql.append(" From Master_Def");
		selectSql.append(" Where Master_Type =:MasterType and Key_Code=:keyCode ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(masterDef);
		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
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
		try {
			SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(selectSql.toString(), parameterSource);
			while (rowSet.next()) {
				map.put(rowSet.getString(1), rowSet.getString(2));
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug(Literal.LEAVING);
		return map;
	}
}
