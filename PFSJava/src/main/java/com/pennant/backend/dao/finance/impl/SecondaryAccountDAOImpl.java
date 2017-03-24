package com.pennant.backend.dao.finance.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.SecondaryAccountDAO;
import com.pennant.backend.model.finance.SecondaryAccount;

public class SecondaryAccountDAOImpl implements SecondaryAccountDAO {

	private static Logger logger = Logger
			.getLogger(SecondaryAccountDAOImpl.class);
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	
	

	public SecondaryAccountDAOImpl() {
		super();
	}
	
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public SecondaryAccount getSecondaryAccount() {

		return null;
	}

	@Override
	public List<SecondaryAccount> getSecondaryAccountsByFinRef(String finReference,String type) {
		logger.debug("Entering");
		SecondaryAccount account = new SecondaryAccount();
		account.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder("Select finReference, priority,accountNumber,finEvent");		
		selectSql.append(" From SecondaryAccounts");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where finReference = :finReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(account);
		RowMapper<SecondaryAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecondaryAccount.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void update(SecondaryAccount secondaryAccount, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update secondaryAccounts");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
				.append(" Set Priority = :Priority,AccountNumber = :AccountNumber,");
		updateSql.append("  Where Finreference =:Finreference ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				secondaryAccount);
		recordCount = this.namedParameterJdbcTemplate.update(
				updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

		}
		logger.debug("Leaving");
	}

	@Override
	public void delete(String finReference, String type) {
		logger.debug("Entering");
		int recordCount = 0;
      
		SecondaryAccount account= new SecondaryAccount();
        account.setFinReference(finReference);
		
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From secondaryAccounts");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  finReference =:finReference ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(account);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {

			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error("Exception: ", e);

		}
		logger.debug("Leaving");
	}

	@Override
	public void save(List<SecondaryAccount> secondaryAccount,String moduleDefiner, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into SecondaryAccounts");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (finReference,priority, accountNumber , finEvent)");
		insertSql.append(" Values(:finReference,:priority, :accountNumber ,:finEvent)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(secondaryAccount.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

}
