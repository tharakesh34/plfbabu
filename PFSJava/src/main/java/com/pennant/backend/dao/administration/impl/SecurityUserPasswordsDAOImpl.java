package com.pennant.backend.dao.administration.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.administration.SecurityUserPasswordsDAO;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class SecurityUserPasswordsDAOImpl extends BasicDao<SecurityUser> implements SecurityUserPasswordsDAO {
	private static Logger logger = LogManager.getLogger(SecurityUserPasswordsDAOImpl.class);

	public SecurityUserPasswordsDAOImpl() {
		super();
	}

	@Override
	public long save(SecurityUser securityUser) {
		logger.debug("Entering ");

		StringBuilder insertSql = new StringBuilder("Insert Into SecUserPasswords");
		insertSql.append(" (UsrID, UsrPwd,UsrToken,LastMntOn) Values(:UsrID,:UsrPwd,:UsrToken,:LastMntOn)");
		logger.debug("saveRecentPasswordSql:" + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return securityUser.getId();
	}

	@Override
	public List<SecurityUser> getUserPreviousPasswords(SecurityUser secUser) {
		logger.debug("Entering ");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  UsrID, UsrPwd,UsrToken,LastMntOn ");
		selectSql.append(" FROM SecUserPasswords ");
		selectSql.append(" where UsrID=:UsrID order by LastMntOn DESC ");

		logger.debug("selectUserRecentPasswordsSql : " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secUser);
		RowMapper<SecurityUser> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityUser.class);
		logger.debug("Leaving ");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void delete(SecurityUser securityUser) {
		logger.debug("Entering ");

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder("Delete From SecUserPasswords ");
		deleteSql.append(" where LastMntOn = (select min(LastMntOn) from SecUserPasswords ");
		deleteSql.append(" where UsrID =:UsrID) and UsrID =:UsrID ");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		logger.debug("deleteOldestPasswordSql:" + deleteSql.toString());

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving ");
	}
}
