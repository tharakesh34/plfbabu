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
 * FileName    		:  WebServiceServerSecurityDAOImpl.java                                                   * 	  
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennanttech.ws.auth.dao.ServerAuthDAO;
import com.pennanttech.ws.auth.model.ServerAuthentication;
/**
 * DAO methods implementation for the <b>WebServiceServerSecurity model</b> class.<br>
 * 
 */
public class ServerAuthDAOImpl extends BasisCodeDAO<ServerAuthentication> implements ServerAuthDAO{
	
	private static Logger logger = Logger.getLogger(ServerAuthDAOImpl.class);
	// Spring Named JDBC Template
		private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
		
		public ServerAuthDAOImpl() {
			super();
		}
		/**
		 * Fetch the Record  WebServiceServerSecurity details by key field
		 * 
		 * @param tokenId (String)
		 * @param  IPAddress (String)
		 * 			       
		 * @return WebServiceServerSecurity
		 */
	@Override
	public  ServerAuthentication validateServer(String tokenId, String IPAddress) {
		logger.debug("Entering ");
		ServerAuthentication webServiceServerSecurity= new ServerAuthentication();
		webServiceServerSecurity.setTokenId(tokenId);
		webServiceServerSecurity.setIpAddress(IPAddress);
		StringBuilder   selectSql = new StringBuilder(" Select UsrLogin, TokenId , IpAddress " );

	
		selectSql.append(" From webServiceServerSecurity");
		selectSql.append(" where TokenId =:TokenId AND IpAddress =:IpAddress");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(webServiceServerSecurity);
		RowMapper<ServerAuthentication> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ServerAuthentication.class);
		
		try{
			webServiceServerSecurity = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			webServiceServerSecurity = null;
		}
		logger.debug("Leaving ");
		return webServiceServerSecurity;

}
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}