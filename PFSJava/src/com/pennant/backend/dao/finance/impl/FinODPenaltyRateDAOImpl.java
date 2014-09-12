package com.pennant.backend.dao.finance.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinODPenaltyRateDAOImpl extends BasisCodeDAO<FinODPenaltyRate> implements FinODPenaltyRateDAO{

	private static Logger logger = Logger.getLogger(FinODPenaltyRateDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
			finODPenaltyRate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finODPenaltyRate = null;
		}
		logger.debug("Leaving");
		return finODPenaltyRate;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param FinODPenaltyRate
	 *            (finODPenaltyRate)
	 * @return FinODPenaltyRate
	 */
	@Override
	public void initialize(FinODPenaltyRate finODPenaltyRate) {
		super.initialize(finODPenaltyRate);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinODPenaltyRate
	 *            (finODPenaltyRate)
	 * @return void
	 */
	@Override
	public void refresh(FinODPenaltyRate finODPenaltyRate) {

	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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
		this.namedParameterJdbcTemplate.update(deleteSql.toString(),  beanParameters);
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
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finODPenaltyRate.getFinReference();

	}

	/**
	 * Method for Finance Overdue penalty rates Updation
	 */
	@SuppressWarnings("serial")
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
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", finODPenaltyRate.getFinReference(), "EN");
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}

	private ErrorDetails getError(String errorId, String finReference, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]), userLanguage);
	}

}
