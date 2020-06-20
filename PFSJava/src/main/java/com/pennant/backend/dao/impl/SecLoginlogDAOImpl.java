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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.SecLoginlogDAO;
import com.pennant.backend.model.SecLoginlog;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class SecLoginlogDAOImpl extends SequenceDao<SecLoginlog> implements SecLoginlogDAO {
	private static final Logger logger = Logger.getLogger(SecLoginlogDAOImpl.class);

	public SecLoginlogDAOImpl() {
		super();
	}

	@Override
	public long saveLog(SecLoginlog logingLog) {
		logger.debug(Literal.ENTERING);

		logingLog.setId(getNextValue("SeqSecLoginLog"));
		StringBuilder insertSql = new StringBuilder(
				"INSERT INTO SecLoginLog(LoginLogID,loginUsrLogin,LoginTime,LoginIP,LoginBrowserType,LoginStsID,");
		insertSql.append("LoginSessionID,LoginError)");
		insertSql.append(
				"VALUES (:LoginLogID,:loginUsrLogin,:LoginTime,:LoginIP,:LoginBrowserType,:LoginStsID,:LoginSessionID,:LoginError)");

		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(logingLog);
		jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return logingLog.getId();
	}

	@Override
	public void logLogOut(long loginLogId) {
		logger.debug("Entering ");

		SecLoginlog logingLog = new SecLoginlog();
		logingLog.setLoginLogID(loginLogId);
		logingLog.setLogOutTime(new Timestamp(System.currentTimeMillis()));

		String updateSql = "update SecLoginLog set LogOutTime=:LogOutTime where LoginLogID= :LoginLogID";
		logger.debug("insertSql: " + updateSql);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(logingLog);
		jdbcTemplate.update(updateSql, beanParameters);

		logger.debug("Leaving ");
	}

	@Override
	public String[] getLoginUsers(Date loginTime) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" distinct LoginUsrLogin");
		sql.append(" From SecLoginLog");
		sql.append(" Where logintime >= ?");
		sql.append(" and logouttime is NULL");

		logger.trace(Literal.SQL + sql.toString());

		try {
			List<String> query = jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setDate(index++, JdbcUtil.getDate(loginTime));
				}

			}, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("LoginUsrLogin");
				}
			});

			if (query.size() == 0) {
				return null;
			} else {
				int i = 0;
				String[] activeUsers = new String[query.size()];
				for (String user : query) {
					activeUsers[i] = user;
					i++;
				}
				return activeUsers;
			}
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

		return null;
	}

}
