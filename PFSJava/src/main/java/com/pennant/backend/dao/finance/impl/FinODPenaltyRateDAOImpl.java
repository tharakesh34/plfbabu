package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

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

	@Override
	public FinODPenaltyRate getFinODPenaltyRateByRef(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinEffectDate, ApplyODPenalty, ODIncGrcDays, ODChargeType, ODGraceDays");
		sql.append(", ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc, ODRuleCode");
		sql.append(", ODMinCapAmount, ODTDSReq");
		sql.append(" from FinODPenaltyRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinODPenaltyRate pr = new FinODPenaltyRate();

				pr.setFinID(rs.getLong("FinID"));
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
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	public void delete(long finID, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinODPenaltyRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
		});
	}

	@Override
	public String save(FinODPenaltyRate pr, String type) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" FinODPenaltyRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(FinID, FinReference, FinEffectDate, ApplyODPenalty, ODIncGrcDays, ODChargeType, ODGraceDays");
		sql.append(", ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc, oDRuleCode, ODMinCapAmount");
		sql.append(", ODTDSReq");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, pr.getFinID());
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

	@Override
	public void saveLog(FinODPenaltyRate pr, String type) {
		if (pr.getLogKey() == 0 || pr.getLogKey() == Long.MIN_VALUE) {
			pr.setLogKey(getNextValue("SeqFinODPenaltyRates"));
		}

		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" FinODPenaltyRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(LogKey, FinID, FinReference, FinEffectDate, ApplyODPenalty, ODIncGrcDays, ODChargeType");
		sql.append(", ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc");
		sql.append(", ODRuleCode, ODMinCapAmount, ODTDSReq");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, JdbcUtil.setLong(pr.getLogKey()));
			ps.setLong(index++, JdbcUtil.setLong(pr.getFinID()));
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

	@Override
	public void update(FinODPenaltyRate odpr, String type) {
		StringBuilder sql = new StringBuilder("Update FinODPenaltyRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set FinEffectDate = ?, ApplyODPenalty = ?, ODIncGrcDays = ?");
		sql.append(", ODChargeType = ?, ODChargeAmtOrPerc = ?, ODGraceDays = ?");
		sql.append(", ODChargeCalOn = ?, ODAllowWaiver = ?, ODMaxWaiverPerc = ?");
		sql.append(", ODRuleCode = ?, ODMinCapAmount = ?, ODTDSReq = ?");
		sql.append(" Where  FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(odpr.getFinEffectDate()));
			ps.setBoolean(index++, odpr.isApplyODPenalty());
			ps.setBoolean(index++, odpr.isODIncGrcDays());
			ps.setString(index++, odpr.getODChargeType());
			ps.setBigDecimal(index++, odpr.getODChargeAmtOrPerc());
			ps.setInt(index++, odpr.getODGraceDays());
			ps.setString(index++, odpr.getODChargeCalOn());
			ps.setBoolean(index++, odpr.isODAllowWaiver());
			ps.setBigDecimal(index++, odpr.getODMaxWaiverPerc());
			ps.setString(index++, odpr.getODRuleCode());
			ps.setBigDecimal(index++, odpr.getoDMinCapAmount());
			ps.setBoolean(index++, odpr.isoDTDSReq());
			ps.setLong(index++, JdbcUtil.setLong(odpr.getFinID()));
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinODPenaltyRate getDMFinODPenaltyRateByRef(final long finID, String type) {
		return getFinODPenaltyRateByRef(finID, type);
	}
}
