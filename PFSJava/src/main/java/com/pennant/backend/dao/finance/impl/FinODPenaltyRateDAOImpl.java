package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinODPenaltyRateDAOImpl extends SequenceDao<FinODPenaltyRate> implements FinODPenaltyRateDAO {
	private static Logger logger = LogManager.getLogger(FinODPenaltyRateDAOImpl.class);

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
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinEffectDate, ApplyODPenalty, ODIncGrcDays, ODChargeType, ODGraceDays");
		sql.append(", ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc, ODRuleCode");
		sql.append(", ODMinCapAmount, ODTDSReq");
		sql.append(" from FinODPenaltyRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, (rs, rowNum) -> {
				FinODPenaltyRate pr = new FinODPenaltyRate();

				pr.setFinReference(rs.getString("FinReference"));
				pr.setFinEffectDate(rs.getTimestamp("FinEffectDate"));
				pr.setApplyODPenalty(rs.getBoolean("ApplyODPenalty"));
				pr.setODIncGrcDays(rs.getBoolean("ODIncGrcDays"));
				pr.setODChargeType(rs.getString("ODChargeType"));
				pr.setODGraceDays(rs.getInt("ODGraceDays"));
				pr.setODChargeCalOn(rs.getString("ODChargeCalOn"));
				pr.setODChargeAmtOrPerc(rs.getBigDecimal("ODChargeAmtOrPerc"));
				pr.setODAllowWaiver(rs.getBoolean("ODAllowWaiver"));
				pr.setODMaxWaiverPerc(rs.getBigDecimal("ODMaxWaiverPerc"));
				pr.setODRuleCode(rs.getString("ODRuleCode"));
				pr.setoDMinCapAmount(rs.getBigDecimal("ODMinCapAmount"));
				pr.setoDTDSReq(rs.getBoolean("ODTDSReq"));

				return pr;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in FinODPenaltyRates{} for the specified finReference>> {}", finReference,
					type);
		}

		return null;
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
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Finance Overdue penalty rates Insertion
	 */
	@Override
	public String save(FinODPenaltyRate pr, String type) {
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" FinODPenaltyRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(FinReference, FinEffectDate, ApplyODPenalty, ODIncGrcDays, ODChargeType, ODGraceDays");
		sql.append(", ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc, oDRuleCode, ODMinCapAmount");
		sql.append(", ODTDSReq");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL, sql);

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, pr.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(pr.getFinEffectDate()));
			ps.setBoolean(index++, pr.isApplyODPenalty());
			ps.setBoolean(index++, pr.isODIncGrcDays());
			ps.setString(index++, pr.getODChargeType());
			ps.setInt(index++, pr.getODGraceDays());
			ps.setString(index++, pr.getODChargeCalOn());
			ps.setBigDecimal(index++, pr.getODChargeAmtOrPerc());
			ps.setBoolean(index++, pr.isODAllowWaiver());
			ps.setBigDecimal(index++, pr.getODMaxWaiverPerc());
			ps.setString(index++, pr.getODRuleCode());
			ps.setBigDecimal(index++, pr.getoDMinCapAmount());
			ps.setBoolean(index++, pr.isoDTDSReq());
		});

		return pr.getFinReference();
	}

	/**
	 * Method for Finance Overdue penalty rates Insertion
	 */
	@Override
	public void saveLog(FinODPenaltyRate pr, String type) {
		if (pr.getLogKey() == 0 || pr.getLogKey() == Long.MIN_VALUE) {
			pr.setLogKey(getNextValue("SeqFinODPenaltyRates"));
		}

		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" FinODPenaltyRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(LogKey, FinReference, FinEffectDate, ApplyODPenalty, ODIncGrcDays, ODChargeType, ODGraceDays");
		sql.append(", ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc, oDRuleCode, ODMinCapAmount");
		sql.append(", ODTDSReq");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL, sql);

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, JdbcUtil.setLong(pr.getLogKey()));
			ps.setString(index++, pr.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(pr.getFinEffectDate()));
			ps.setBoolean(index++, pr.isApplyODPenalty());
			ps.setBoolean(index++, pr.isODIncGrcDays());
			ps.setString(index++, pr.getODChargeType());
			ps.setInt(index++, pr.getODGraceDays());
			ps.setString(index++, pr.getODChargeCalOn());
			ps.setBigDecimal(index++, pr.getODChargeAmtOrPerc());
			ps.setBoolean(index++, pr.isODAllowWaiver());
			ps.setBigDecimal(index++, pr.getODMaxWaiverPerc());
			ps.setString(index++, pr.getODRuleCode());
			ps.setBigDecimal(index++, pr.getoDMinCapAmount());
			ps.setBoolean(index++, pr.isoDTDSReq());
		});
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
		updateSql.append(" ODIncGrcDays = :ODIncGrcDays, ODChargeType = :ODChargeType, ");
		updateSql.append(" ODChargeAmtOrPerc = :ODChargeAmtOrPerc, ODGraceDays = :ODGraceDays,  ");
		updateSql.append(" ODChargeCalOn = :ODChargeCalOn, ODAllowWaiver = :ODAllowWaiver,");
		updateSql.append(" ODMaxWaiverPerc = :ODMaxWaiverPerc, oDRuleCode = :oDRuleCode,  ");
		updateSql.append(" ODMinCapAmount = :ODMinCapAmount ,ODTDSReq = :ODTDSReq");
		updateSql.append(" WHERE  FinReference = :FinReference ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODPenaltyRate);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public FinODPenaltyRate getDMFinODPenaltyRateByRef(final String finReference, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinEffectDate, ApplyODPenalty, ODIncGrcDays, ODChargeType, ODGraceDays");
		sql.append(", ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc, ODRuleCode");
		sql.append(", ODMinCapAmount, ODTDSReq");
		sql.append(" from FinODPenaltyRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, (rs, rowNum) -> {
				FinODPenaltyRate pr = new FinODPenaltyRate();

				pr.setFinReference(rs.getString("FinReference"));
				pr.setFinEffectDate(rs.getTimestamp("FinEffectDate"));
				pr.setApplyODPenalty(rs.getBoolean("ApplyODPenalty"));
				pr.setODIncGrcDays(rs.getBoolean("ODIncGrcDays"));
				pr.setODChargeType(rs.getString("ODChargeType"));
				pr.setODGraceDays(rs.getInt("ODGraceDays"));
				pr.setODChargeCalOn(rs.getString("ODChargeCalOn"));
				pr.setODChargeAmtOrPerc(rs.getBigDecimal("ODChargeAmtOrPerc"));
				pr.setODAllowWaiver(rs.getBoolean("ODAllowWaiver"));
				pr.setODMaxWaiverPerc(rs.getBigDecimal("ODMaxWaiverPerc"));
				pr.setODRuleCode(rs.getString("ODRuleCode"));
				pr.setoDMinCapAmount(rs.getBigDecimal("ODMinCapAmount"));
				pr.setoDTDSReq(rs.getBoolean("ODTDSReq"));

				return pr;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records are not found in FinODPenaltyRates{} for the FinReference >> {}", type, finReference);
		}

		return null;
	}

}
