package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

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
	public List<FinanceEligibilityDetail> getFinElgDetailByFinRef(final String finReference, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, ElgRuleCode, RuleResultType");
		sql.append(", RuleResult, CanOverride, OverridePerc, UserOverride");

		if (type.contains("View")) {
			sql.append(", LovDescElgRuleCode, LovDescElgRuleCodeDesc");
		}

		sql.append(" from FinanceEligibilityDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, finReference);
		}, (rs, rowNum) -> {
			FinanceEligibilityDetail ed = new FinanceEligibilityDetail();

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
	public int getFinElgDetailCount(FinanceEligibilityDetail financeEligibilityDetail) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select count(*) ");
		selectSql.append(" From FinanceEligibilityDetail");
		selectSql.append(" Where FinReference =:FinReference and ElgRuleCode = :ElgRuleCode ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeEligibilityDetail);
		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public void saveList(List<FinanceEligibilityDetail> eligibilityDetails, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinanceEligibilityDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (FinReference, ElgRuleCode, RuleResultType,RuleResult ,CanOverride, OverridePerc, UserOverride, LastMntOn, LastMntBy )");
		insertSql.append(
				" Values(:FinReference, :ElgRuleCode, :RuleResultType, :RuleResult, :CanOverride, :OverridePerc, :UserOverride, :LastMntOn, :LastMntBy  )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(eligibilityDetails.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void updateList(List<FinanceEligibilityDetail> eligibilityDetails) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update FinanceEligibilityDetail");
		updateSql.append(" Set RuleResultType = :RuleResultType, RuleResult = :RuleResult ,");
		updateSql.append(" CanOverride = :CanOverride , OverridePerc = :OverridePerc, UserOverride = :UserOverride, ");
		updateSql.append(" LastMntOn = :LastMntOn , LastMntBy = :LastMntBy ");
		updateSql.append("  Where FinReference =:FinReference and ElgRuleCode = :ElgRuleCode ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(eligibilityDetails.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void deleteByFinRef(String finReference) {
		logger.debug("Entering");

		FinanceEligibilityDetail detail = new FinanceEligibilityDetail();
		detail.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" DELETE FROM FinanceEligibilityDetail ");
		deleteSql.append(" Where FinReference =:FinReference ");

		logger.debug("updateSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}
