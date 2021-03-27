package com.pennant.backend.dao.finance.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinanceMainExtDAO;
import com.pennant.backend.model.finance.FinanceMainExt;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class FinanceMainExtDAOImpl extends BasicDao<FinanceMainExt> implements FinanceMainExtDAO {
	private static Logger logger = LogManager.getLogger(FinanceMainExtDAOImpl.class);

	public FinanceMainExtDAOImpl() {
		super();
	}

	/**
	 * Method to save the FinanceMain Extension details
	 * 
	 */
	@Override
	public void save(FinanceMainExt financeMainExt) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinanceMainExt");
		insertSql.append(" (FinReference, RepayIBAN, ProcessFlag, NstlAccNum, IfscCode, Remarks1, Remarks2 )");
		insertSql.append(" VALUES(:FinReference, :RepayIBAN, :ProcessFlag, :NstlAccNum, :IfscCode, ");
		insertSql.append(":Remarks1, :Remarks2 )");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMainExt);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Record from FinanceMain Extension details by key field
	 * 
	 * @param finReference
	 *            (String)
	 * @return FinanceMainExt
	 */
	@Override
	public FinanceMainExt getFinanceMainExtByRef(String finReference) {
		logger.debug("Entering");

		FinanceMainExt financeMainExt = new FinanceMainExt();
		financeMainExt.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinReference, RepayIBAN, ProcessFlag, NstlAccNum, IfscCode, ");
		selectSql.append("  Remarks1, Remarks2 ");
		selectSql.append(" From FinanceMainExt");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMainExt);
		RowMapper<FinanceMainExt> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceMainExt.class);

		try {
			financeMainExt = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			financeMainExt = null;
		}
		logger.debug("Leaving");
		return financeMainExt;
	}

	/**
	 * This method updates the Record in FinanceMainExt.
	 * 
	 * @param financeMainExt
	 */

	@Override
	public void update(FinanceMainExt financeMainExt) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update ");

		updateSql.append(" FinanceMainExt");
		updateSql.append(" Set RepayIBAN = :RepayIBAN,");
		updateSql.append(" ProcessFlag =:ProcessFlag, NstlAccNum =:NstlAccNum , IfscCode=:IfscCode, ");
		updateSql.append("Remarks1 = :Remarks1, Remarks2 = :Remarks2 ");
		updateSql.append(" WHERE FinReference =:FinReference ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMainExt);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Method for fetch Repay Account IBAN
	 * 
	 */
	@Override
	public String getRepayIBAN(String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder("SELECT RepayIBAN");
		selectSql.append(" From FinanceMainExt");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			return null;
		}
	}

	/**
	 * The below method is used to fetch the NSTL customer information<br>
	 * 
	 * 1. The ProcessFlag having 1 and NstlAccNum flags are having values(NOT NULL) when customer is created from
	 * PFF.<br>
	 * 2. The ProcessFlag having 0 and NstlAccNum flags are having NULL values when customer is created from T24.<br>
	 * 
	 * @param finReference
	 * @param processFlag
	 * @return FinanceMainExt
	 *
	 */
	@Override
	public FinanceMainExt getNstlAccNumber(String finReference, boolean processFlag) {
		logger.debug("Entering");

		FinanceMainExt financeMainExt = new FinanceMainExt();
		financeMainExt.setFinReference(finReference);
		financeMainExt.setProcessFlag(processFlag);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference, RepayIBAN, ProcessFlag, NstlAccNum");
		selectSql.append(" From FinanceMainExt");
		selectSql.append(" Where FinReference =:FinReference AND ProcessFlag = :ProcessFlag");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMainExt);
		RowMapper<FinanceMainExt> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceMainExt.class);

		try {
			financeMainExt = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			financeMainExt = null;
		}
		logger.debug("Leaving");
		return financeMainExt;
	}
}
