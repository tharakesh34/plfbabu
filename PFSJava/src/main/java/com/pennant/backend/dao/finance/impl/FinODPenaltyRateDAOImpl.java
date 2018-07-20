package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class FinODPenaltyRateDAOImpl extends SequenceDao<FinODPenaltyRate> implements FinODPenaltyRateDAO{
	private static Logger logger = Logger.getLogger(FinODPenaltyRateDAOImpl.class);

	public FinODPenaltyRateDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Finance Overdue Penalty details by key field
	 * 
	 * @param finReference
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinODPenaltyRate
	 */
	@Override
	public FinODPenaltyRate getFinODPenaltyRateByRef(final String finReference, String type) {
		logger.debug("Entering");
		FinODPenaltyRate finODPenaltyRate = new FinODPenaltyRate();
		finODPenaltyRate.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" SELECT FinReference ,FinEffectDate, ApplyODPenalty, ODIncGrcDays,");
		selectSql.append(" ODChargeType, ODGraceDays, ODChargeCalOn , ODChargeAmtOrPerc, ODAllowWaiver , ODMaxWaiverPerc ");
		selectSql.append(" From FinODPenaltyRates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODPenaltyRate);
		RowMapper<FinODPenaltyRate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODPenaltyRate.class);

		try {
			finODPenaltyRate = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finODPenaltyRate = null;
		}
		logger.debug("Leaving");
		return finODPenaltyRate;
	}

	/**
	 * Method for Finance Overdue penalty rates Deletion
	 */
	public void delete(String finReference, String type) {
		logger.debug("Entering");
		
		FinODPenaltyRate finODPenaltyRate = new FinODPenaltyRate();
		finODPenaltyRate.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder(" Delete From FinODPenaltyRates");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODPenaltyRate);
		this.jdbcTemplate.update(deleteSql.toString(),  beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Finance Overdue penalty rates Insertion
	 */
	@Override
	public String save(FinODPenaltyRate finODPenaltyRate, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinODPenaltyRates");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference ,FinEffectDate, ApplyODPenalty, ODIncGrcDays, ODChargeType, ODGraceDays,");
		insertSql.append(" ODChargeCalOn , ODChargeAmtOrPerc ,ODAllowWaiver , ODMaxWaiverPerc )");
		insertSql.append(" Values(:FinReference ,:FinEffectDate, :ApplyODPenalty, :ODIncGrcDays, :ODChargeType, :ODGraceDays, :ODChargeCalOn, ");
		insertSql.append(" :ODChargeAmtOrPerc, :ODAllowWaiver , :ODMaxWaiverPerc )");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODPenaltyRate);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finODPenaltyRate.getFinReference();

	}
	

	/**
	 * Method for Finance Overdue penalty rates Insertion
	 */
	@Override
	public void saveLog(FinODPenaltyRate finODPenaltyRate, String type) {
		logger.debug("Entering");
		
		if (finODPenaltyRate.getLogKey() == 0 || finODPenaltyRate.getLogKey() == Long.MIN_VALUE) {
			finODPenaltyRate.setLogKey(getNextValue("SeqFinODPenaltyRates"));
			logger.debug("get NextID:" + finODPenaltyRate.getLogKey());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into FinODPenaltyRates");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LogKey, FinReference ,FinEffectDate, ApplyODPenalty, ODIncGrcDays, ODChargeType, ODGraceDays,");
		insertSql.append(" ODChargeCalOn , ODChargeAmtOrPerc ,ODAllowWaiver , ODMaxWaiverPerc )");
		insertSql.append(" Values(:LogKey , :FinReference ,:FinEffectDate, :ApplyODPenalty, :ODIncGrcDays, :ODChargeType, :ODGraceDays, :ODChargeCalOn, ");
		insertSql.append(" :ODChargeAmtOrPerc, :ODAllowWaiver , :ODMaxWaiverPerc )");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODPenaltyRate);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	/**
	 * Method for Finance Overdue penalty rates Updation
	 */
	@Override
	public void update(FinODPenaltyRate finODPenaltyRate, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinODPenaltyRates");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinEffectDate = :FinEffectDate, ApplyODPenalty = :ApplyODPenalty,");
		updateSql.append(" ODIncGrcDays = :ODIncGrcDays, ODChargeType = :ODChargeType, ODChargeAmtOrPerc = :ODChargeAmtOrPerc,");
		updateSql.append(" ODGraceDays = :ODGraceDays, ODChargeCalOn = :ODChargeCalOn, ODAllowWaiver = :ODAllowWaiver,ODMaxWaiverPerc = :ODMaxWaiverPerc");
		updateSql.append(" WHERE  FinReference = :FinReference ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODPenaltyRate);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}
