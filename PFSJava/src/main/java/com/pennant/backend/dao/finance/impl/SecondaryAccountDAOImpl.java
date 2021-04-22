package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.finance.SecondaryAccountDAO;
import com.pennant.backend.model.finance.SecondaryAccount;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class SecondaryAccountDAOImpl extends BasicDao<SecondaryAccount> implements SecondaryAccountDAO {
	private static Logger logger = LogManager.getLogger(SecondaryAccountDAOImpl.class);

	public SecondaryAccountDAOImpl() {
		super();
	}

	@Override
	public SecondaryAccount getSecondaryAccount() {

		return null;
	}

	@Override
	public List<SecondaryAccount> getSecondaryAccountsByFinRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, Priority, AccountNumber, FinEvent");
		sql.append(" from SecondaryAccounts");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where finReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
				}
			}, new RowMapper<SecondaryAccount>() {
				@Override
				public SecondaryAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
					SecondaryAccount sa = new SecondaryAccount();

					sa.setFinReference(rs.getString("FinReference"));
					sa.setPriority(rs.getInt("Priority"));
					sa.setAccountNumber(rs.getString("AccountNumber"));
					sa.setFinEvent(rs.getString("FinEvent"));

					return sa;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void update(SecondaryAccount secondaryAccount, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update secondaryAccounts");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Priority = :Priority,AccountNumber = :AccountNumber,");
		updateSql.append("  Where Finreference =:Finreference ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secondaryAccount);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

		}
		logger.debug("Leaving");
	}

	@Override
	public void delete(String finReference, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		SecondaryAccount account = new SecondaryAccount();
		account.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From secondaryAccounts");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  finReference =:finReference ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(account);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {

			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error("Exception: ", e);

		}
		logger.debug("Leaving");
	}

	@Override
	public void save(List<SecondaryAccount> secondaryAccount, String moduleDefiner, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into SecondaryAccounts");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (finReference,priority, accountNumber , finEvent)");
		insertSql.append(" Values(:finReference,:priority, :accountNumber ,:finEvent)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(secondaryAccount.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

}
