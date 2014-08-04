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
 * FileName    		:  MenuDetailsDAO.java													*                           
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
package com.pennant.backend.dao.impl;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.StoredProcedureUtil;
import com.pennant.backend.dao.MenuDetailsDAO;
import com.pennant.backend.model.MenuDetails;

/**
 * DAO methods implementation for the <b>MenuDetails model</b> class.<br>
 * 
 */

public class MenuDetailsDAOImpl extends BasisNextidDaoImpl<MenuDetails> implements MenuDetailsDAO {
	
	private static Logger logger = Logger.getLogger(MenuDetailsDAOImpl.class);

	// Spring JDBC Templates
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}

	public List<MenuDetails> getMenuDetailsByApp(String appCode) {
		logger.debug("Entering");
		String selectListSql = 	"select MenuId, MenuApp, MenuCode, MenuRef, MenuZulPath, T1.LastMntBy, T1.LastMntOn,T2.AppCode from PTMenuDetails T1,PTApplicationDetails T2 "+
								" where AppID=MenuApp and AppCode=:AppCode" ;
		
		SqlParameterSource namedParameters = new MapSqlParameterSource("AppCode", appCode);
		RowMapper<MenuDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MenuDetails.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectListSql, namedParameters,typeRowMapper);
		
	}
	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param procedureName (String)
	 * @param userLogin (String)
	 * @param inputParamMap (Map)
	 * @param outputParamMap (Map)
	 * @param userLanguage (String)
	 * @return Map
	 */
    @Override
    public Map<String, Object> getLastLoginInfo(String procedureName, String usrLogin,  Map<String, Object> inputParamMap, Map<String, Object> outputParamMap) {
	  return new StoredProcedureUtil(this.dataSource, procedureName, inputParamMap, outputParamMap).execute(usrLogin);    
    }
		
}		

