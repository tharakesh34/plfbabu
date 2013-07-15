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
 * FileName    		:  SecLoginlogADOImpl.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  05-08-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-08-2011       Pennant	                 0.1                                            * 
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

import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.SecLoginlogDAO;
import com.pennant.backend.model.SecLoginlog;

public class SecLoginlogDAOImpl  extends BasisNextidDaoImpl<SecLoginlog> implements SecLoginlogDAO{
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private final static Logger logger = Logger.getLogger(SecLoginlogDAOImpl.class);
	public SecLoginlog getNewSecLoginlog() {
		return new SecLoginlog();
	}

	public List<SecLoginlog> getAllLogs() {
		logger.debug("Entering ");

		StringBuilder   selectSql = new StringBuilder("select LoginLogID,LoginUsrLogin,LoginTime, LoginIP, LoginBrowserType, LoginStsID, LoginSessionID, LogOutTime,");
		selectSql.append("LoginError from SecLoginLog order by LoginTime");
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<SecLoginlog> secLoginlogRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecLoginlog.class);
		List<SecLoginlog> secLoginlogs = this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), secLoginlogRowMapper);
		logger.debug("Leaving ");
		return secLoginlogs;
	}

	public List<SecLoginlog> getLogsByLoginName(String loginUsrLogin) {
		logger.debug("Entering ");
		StringBuilder   selectSql = new StringBuilder("select LoginLogID,LoginUsrLogin, LoginTime, LoginIP, LoginBrowserType, LoginStsID, LoginSessionID, LogOutTime,");
		selectSql.append("LoginError from SecLoginLog "); 
		selectSql.append(" WHERE LoginUsrLogin=:LoginUsrLogin  order by LoginTime ");
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource namedParameters = new MapSqlParameterSource("LoginUsrLogin", loginUsrLogin);
		RowMapper<SecLoginlog> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecLoginlog.class);
		logger.debug("Leaving ");
		
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), namedParameters,typeRowMapper);
	}
	public List<SecLoginlog> getAllLogsForFailed() {
		logger.debug("Entering ");
		StringBuilder   selectSql = new StringBuilder("select LoginLogID, LoginUsrLogin,LoginTime, LoginIP, LoginBrowserType, LoginStsID, LoginSessionID, LogOutTime,");
		selectSql.append("LoginError from SecLoginLog where LoginStsID=0 order by LoginTime");
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<SecLoginlog> secLoginlogRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecLoginlog.class);
		List<SecLoginlog> secLoginlogs = this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), secLoginlogRowMapper);
		logger.debug("Leaving ");
		return secLoginlogs;
	}

	public List<SecLoginlog> getAllLogsForSuccess() {
		logger.debug("Entering ");
		StringBuilder   selectSql = new StringBuilder("select LoginLogID,LoginUsrLogin ,LoginTime, LoginIP, LoginBrowserType, LoginStsID, LoginSessionID, LogOutTime,");
		selectSql.append("LoginError from SecLoginLog where LoginStsID=1 order by LoginTime");
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<SecLoginlog> secLoginlogRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecLoginlog.class);
		List<SecLoginlog> secLoginlogs = this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), secLoginlogRowMapper);
		logger.debug("Leaving ");
		return secLoginlogs;
	}

	public void saveLog(SecLoginlog logingLog) {
		logger.debug("Entering ");
		logingLog.setId(getNextId("SeqSecLoginLog"));
		StringBuilder   insertSql =new StringBuilder("INSERT INTO SecLoginLog(LoginLogID,loginUsrLogin,LoginTime,LoginIP,LoginBrowserType,LoginStsID,");
		insertSql.append("LoginSessionID,LoginError)");
		insertSql.append("VALUES (:LoginLogID,:loginUsrLogin,:LoginTime,:LoginIP,:LoginBrowserType,:LoginStsID,:LoginSessionID,:LoginError)");
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(logingLog);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
	}


	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public void logLogOut(String sessionId) {
		logger.debug("Entering ");

		SecLoginlog logingLog = new SecLoginlog();
		logingLog.setLoginSessionID(sessionId);
		logingLog.setLogOutTime(new Timestamp(System.currentTimeMillis()));

		String updateSql = 	"update SecLoginLog set LogOutTime=:LogOutTime where LoginSessionID= :LoginSessionID";
		logger.debug("insertSql: " + updateSql);
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(logingLog);
		this.namedParameterJdbcTemplate.update(updateSql, beanParameters);
		
		logger.debug("Leaving ");
	}
}