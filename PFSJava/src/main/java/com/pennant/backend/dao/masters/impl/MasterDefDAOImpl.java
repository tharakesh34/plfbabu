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

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.masters.MasterDefDAO;
import com.pennant.backend.dao.systemmasters.impl.CityDAOImpl;
import com.pennant.backend.model.MasterDef;

/**
 * DAO methods implementation for the <b>MasterDef model</b> class.<br>
 * 
 */
public class MasterDefDAOImpl implements MasterDefDAO {

	private static Logger logger = Logger.getLogger(CityDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public MasterDefDAOImpl() {
		super();
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * Fetch the Record  MasterDef details by key field
	 * 
	 * @param masterType (String)
	 * @param  keyType (String)
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
		selectSql.append(" Where Master_Type =:MasterType and Key_type=:KeyType " );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(masterDef);
		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
	}

}
