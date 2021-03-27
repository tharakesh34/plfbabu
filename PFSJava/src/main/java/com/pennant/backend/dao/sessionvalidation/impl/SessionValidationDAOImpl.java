package com.pennant.backend.dao.sessionvalidation.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.sessionvalidation.SessionValidationDAO;
import com.pennant.backend.model.sessionvalidation.SessionValidation;

public class SessionValidationDAOImpl implements SessionValidationDAO {

	private static Logger logger = LogManager.getLogger(SessionValidationDAOImpl.class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public void save(SessionValidation sessionValidation) {
		logger.debug("Entering ");

		StringBuilder insertSql = new StringBuilder("Insert Into App_SessionValidation ");
		insertSql.append(" (EntityCode, AgentId, UserToken, UserTokenExpiry ,RegistrationId )");
		insertSql.append(" Values(:EntityCode, :AgentId, :UserToken, :UserTokenExpiry ,:RegistrationId )");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sessionValidation);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
	}

	@Override
	public SessionValidation getSessionById(long agentId) {
		logger.debug("Entering");
		SessionValidation sessionValidation = new SessionValidation();
		sessionValidation.setAgentId(agentId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT EntityCode,AgentId,UserToken,UserTokenExpiry,RegistrationId ");
		selectSql.append(" FROM  App_SessionValidation");
		selectSql.append(" Where AgentId =:AgentId");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sessionValidation);
		RowMapper<SessionValidation> typeRowMapper = BeanPropertyRowMapper.newInstance(SessionValidation.class);

		logger.debug("selectSql: " + selectSql.toString());

		try {
			sessionValidation = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			sessionValidation = null;
		}

		logger.debug("Leaving");
		return sessionValidation;
	}

	@Override
	public void update(SessionValidation sessionValidation) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update App_SessionValidation");
		updateSql.append(" Set RegistrationId=:RegistrationId Where AgentId=:AgentId");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sessionValidation);

		try {
			this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.debug("Error" + e.getMessage());
		}

		logger.debug("Leaving");
	}

	@Override
	public List<SessionValidation> getActiveSessions() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				"Select EntityCode, AgentID, UserToken, UserTokenExpiry, RegistrationId");
		selectSql.append(" From App_SessionValidation");

		logger.debug("selectSql:" + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new SessionValidation());
		RowMapper<SessionValidation> typeRowMapper = BeanPropertyRowMapper.newInstance(SessionValidation.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

}
