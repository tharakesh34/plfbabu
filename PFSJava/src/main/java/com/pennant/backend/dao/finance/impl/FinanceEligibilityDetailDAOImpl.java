package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.FinanceEligibilityDetailDAO;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinanceEligibilityDetailDAOImpl extends BasicDao<FinanceEligibilityDetail>
		implements FinanceEligibilityDetailDAO {
	private static Logger logger = LogManager.getLogger(FinanceEligibilityDetailDAOImpl.class);

	public FinanceEligibilityDetailDAOImpl() {
		super();
	}

	@Override
	public List<FinanceEligibilityDetail> getFinElgDetailByFinRef(final long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, ElgRuleCode, RuleResultType");
		sql.append(", RuleResult, CanOverride, OverridePerc, UserOverride");

		if (type.contains("View")) {
			sql.append(", LovDescElgRuleCode, LovDescElgRuleCodeDesc");
		}

		sql.append(" From FinanceEligibilityDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
		}, (rs, rowNum) -> {
			FinanceEligibilityDetail ed = new FinanceEligibilityDetail();

			ed.setFinID(rs.getLong("FinID"));
			ed.setFinReference(rs.getString("FinReference"));
			ed.setElgRuleCode(rs.getLong("ElgRuleCode"));
			ed.setRuleResultType(rs.getString("RuleResultType"));
			ed.setRuleResult(rs.getString("RuleResult"));
			ed.setCanOverride(rs.getBoolean("CanOverride"));
			ed.setOverridePerc(rs.getInt("OverridePerc"));
			ed.setUserOverride(rs.getBoolean("UserOverride"));

			if (type.contains("View")) {
				ed.setLovDescElgRuleCode(rs.getString("LovDescElgRuleCode"));
				ed.setLovDescElgRuleCodeDesc(rs.getString("LovDescElgRuleCodeDesc"));
			}

			return ed;
		});
	}

	@Override
	public int getFinElgDetailCount(FinanceEligibilityDetail fed) {
		String sql = "Select count(FinID) From FinanceEligibilityDetail Where FinID = ? and ElgRuleCode = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, fed.getFinID(), fed.getElgRuleCode());
	}

	@Override
	public void saveList(List<FinanceEligibilityDetail> fed, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinanceEligibilityDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, ElgRuleCode, RuleResultType, RuleResult");
		sql.append(", CanOverride, OverridePerc, UserOverride, LastMntOn, LastMntBy)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceEligibilityDetail ed = fed.get(i);

				int index = 1;

				ps.setLong(index++, ed.getFinID());
				ps.setString(index++, ed.getFinReference());
				ps.setLong(index++, ed.getElgRuleCode());
				ps.setString(index++, ed.getRuleResultType());
				ps.setString(index++, ed.getRuleResult());
				ps.setBoolean(index++, ed.isCanOverride());
				ps.setInt(index++, ed.getOverridePerc());
				ps.setBoolean(index++, ed.isUserOverride());
				ps.setTimestamp(index++, ed.getLastMntOn());
				ps.setLong(index++, ed.getLastMntBy());
			}

			@Override
			public int getBatchSize() {
				return fed.size();
			}
		});
	}

	@Override
	public void updateList(List<FinanceEligibilityDetail> eligibilityDetails) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update FinanceEligibilityDetail");
		sql.append(" Set RuleResultType = ?, RuleResult = ?, CanOverride = ?, OverridePerc = ?, UserOverride = ?");
		sql.append(", LastMntOn = ?, LastMntBy = ?");
		sql.append(" Where FinID = ? and ElgRuleCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceEligibilityDetail ed = eligibilityDetails.get(i);

				int index = 1;

				ps.setString(index++, ed.getRuleResultType());
				ps.setString(index++, ed.getRuleResult());
				ps.setBoolean(index++, ed.isCanOverride());
				ps.setInt(index++, ed.getOverridePerc());
				ps.setBoolean(index++, ed.isUserOverride());
				ps.setTimestamp(index++, ed.getLastMntOn());
				ps.setLong(index++, ed.getLastMntBy());
				ps.setLong(index++, ed.getFinID());
				ps.setLong(index++, ed.getElgRuleCode());
			}

			@Override
			public int getBatchSize() {
				return eligibilityDetails.size();
			}

		});
	}

	@Override
	public void deleteByFinRef(long finID) {
		String sql = "Delete From FinanceEligibilityDetail Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> ps.setLong(1, finID));
	}

}
