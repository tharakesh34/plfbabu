/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant PFF-API Application Framework and related Products. 
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
 * FileName    		:  WebServiceUserSecurityDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  07-10-2016    														*
 *                                                                  						*
 * Modified Date    :  07-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-10-2016       Pennant	                 0.1                                            * 
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
package com.pennanttech.ws.auth.dao.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.impl.CityDAOImpl;
import com.pennanttech.ws.auth.dao.UserAuthDAO;
import com.pennanttech.ws.auth.model.UserAuthentication;
/**
 * DAO methods implementation for the <b>WebServiceUserSecurity model</b> class.<br>
 * 
 */
public class UserAuthDAOImpl extends BasisCodeDAO< UserAuthentication> implements UserAuthDAO  {
private static Logger logger = Logger.getLogger(CityDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public UserAuthDAOImpl() {
		super();
	}
	/**
	 * Fetch the Record  WebServiceUserSecurity details by key field
	 * 
	 * @param tokenId (String)
	 * @param  expiry (Timestamp)
	 * 			       
	 * @return WebServiceUserSecurity
	 */
@Override
public  UserAuthentication validateSession(String tokenId) {
	logger.debug("Entering ");
	UserAuthentication webServiceUserSecurity= new UserAuthentication();
	webServiceUserSecurity.setTokenId(tokenId);
	StringBuilder   selectSql = new StringBuilder(" Select UsrLogin, TokenId, Expiry " );


	selectSql.append(" From webServiceUserSecurity");
	selectSql.append(" Where TokenId =:TokenId");
	logger.debug("selectSql: " + selectSql.toString());
	SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(webServiceUserSecurity);
	RowMapper<UserAuthentication> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(UserAuthentication.class);
	
	try{
		webServiceUserSecurity = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
	}catch (EmptyResultDataAccessException e) {
		logger.warn("Exception: ", e);
		webServiceUserSecurity = null;
	}
	logger.debug("Leaving ");
	return webServiceUserSecurity;

}
/**
 * @param dataSource the dataSource to set
 */
public void setDataSource(DataSource dataSource) {
	this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
}
/**
 * This method insert new Records into webServiceUserSecurity 
 * 
 * save WebServiceUserSecurity
 * 
 * @param UserAuthentication
 *            (webServiceUserSecurity)
 *
 * @return String(TokenId)
 * @throws DataAccessException
 * 
 */
@Override
public String createSession(UserAuthentication webServiceUserSecurity) {
	
	
	StringBuilder insertSql = new StringBuilder("Insert Into webServiceUserSecurity" );

	insertSql.append("( UsrLogin , TokenId, Expiry )" );
	insertSql.append(" Values(:UsrLogin, :TokenId, :Expiry)" );

	SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(webServiceUserSecurity);
	this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
	
	return webServiceUserSecurity.getTokenId();
}

/**
 * This method update the Records into webServiceUserSecurity 
 * 
 * update WebServiceUserSecurity
 * 
 * @param UserAuthentication
 *            (webServiceUserSecurity)
 *
 * 
 * @throws DataAccessException
 * 
 */

@Override
public void updateSession(UserAuthentication webServiceUserSecurity) {
	
	StringBuilder updateSql = new StringBuilder("update webServiceUserSecurity" );

	updateSql.append(" Set Expiry =:Expiry" );
	updateSql.append(" where TokenId =:TokenId");
	

	SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(webServiceUserSecurity);
	this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
	
}
}
