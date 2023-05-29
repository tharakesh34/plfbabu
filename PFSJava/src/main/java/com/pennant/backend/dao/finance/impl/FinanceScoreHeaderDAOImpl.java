package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.FinanceScoreHeaderDAO;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinanceScoreHeaderDAOImpl extends SequenceDao<FinanceScoreHeader> implements FinanceScoreHeaderDAO {
	private static Logger logger = LogManager.getLogger(FinanceScoreHeaderDAOImpl.class);

	public FinanceScoreHeaderDAOImpl() {
		super();
	}

	@Override
	public List<FinanceScoreHeader> getFinScoreHeaderList(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, FinID, FinReference, GroupId, MinScore, Override");
		sql.append(", OverrideScore, CreditWorth, CustId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", GroupCode, GroupCodeDesc");
		}

		sql.append(" from FinanceScoreHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID), (rs, rowNum) -> {
			FinanceScoreHeader fsh = new FinanceScoreHeader();

			fsh.setHeaderId(rs.getLong("HeaderId"));
			fsh.setFinID(rs.getLong("FinID"));
			fsh.setFinReference(rs.getString("FinReference"));
			fsh.setGroupId(rs.getLong("GroupId"));
			fsh.setMinScore(rs.getInt("MinScore"));
			fsh.setOverride(rs.getBoolean("Override"));
			fsh.setOverrideScore(rs.getInt("OverrideScore"));
			fsh.setCreditWorth(rs.getString("CreditWorth"));
			fsh.setCustId(rs.getLong("CustId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				fsh.setGroupCode(rs.getString("GroupCode"));
				fsh.setGroupCodeDesc(rs.getString("GroupCodeDesc"));
			}

			return fsh;
		});
	}

	@Override
	public List<FinanceScoreHeader> getFinScoreHeaderList(String finReference, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, FinID, FinReference, GroupId, MinScore, Override");
		sql.append(", OverrideScore, CreditWorth, CustId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", GroupCode, GroupCodeDesc");
		}

		sql.append(" from FinanceScoreHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setString(1, finReference), (rs, rowNum) -> {
			FinanceScoreHeader fsh = new FinanceScoreHeader();

			fsh.setHeaderId(rs.getLong("HeaderId"));
			fsh.setFinID(rs.getLong("FinID"));
			fsh.setFinReference(rs.getString("FinReference"));
			fsh.setGroupId(rs.getLong("GroupId"));
			fsh.setMinScore(rs.getInt("MinScore"));
			fsh.setOverride(rs.getBoolean("Override"));
			fsh.setOverrideScore(rs.getInt("OverrideScore"));
			fsh.setCreditWorth(rs.getString("CreditWorth"));
			fsh.setCustId(rs.getLong("CustId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				fsh.setGroupCode(rs.getString("GroupCode"));
				fsh.setGroupCodeDesc(rs.getString("GroupCodeDesc"));
			}

			return fsh;
		});
	}

	@Override
	public long saveHeader(FinanceScoreHeader sh, String type) {
		if (sh.getHeaderId() == Long.MIN_VALUE) {
			sh.setId(getNextValue("SeqFinanceScoreHeader"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinanceScoreHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (HeaderId, FinID, FinReference, GroupId, MinScore");
		sql.append(", Override, OverrideScore, CreditWorth, CustId)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, sh.getHeaderId());
			ps.setLong(index++, sh.getFinID());
			ps.setString(index++, sh.getFinReference());
			ps.setLong(index++, sh.getGroupId());
			ps.setInt(index++, sh.getMinScore());
			ps.setBoolean(index++, sh.isOverride());
			ps.setInt(index++, sh.getOverrideScore());
			ps.setString(index++, sh.getCreditWorth());
			ps.setLong(index, sh.getCustId());

		});

		return sh.getId();
	}

	@Override
	public void deleteHeaderList(long finID, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinanceScoreHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, finID));
	}

	@Override
	public List<FinanceScoreDetail> getFinScoreDetailList(List<Long> headerIds, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, SubGroupId, RuleId, MaxScore, ExecScore");
		if (type.contains("View")) {
			sql.append(", SubGrpCodeDesc, RuleCode, RuleCodeDesc, CategoryType");
		}
		sql.append(" From FinanceScoreDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderId in(");

		int i = 0;

		while (i < headerIds.size()) {
			sql.append(" ?,");
			i++;
		}

		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			for (Long id : headerIds) {
				ps.setLong(index++, id);
			}
		}, (rs, rowNum) -> {
			FinanceScoreDetail fsd = new FinanceScoreDetail();

			fsd.setHeaderId(rs.getLong("HeaderId"));
			fsd.setSubGroupId(rs.getLong("SubGroupId"));
			fsd.setRuleId(rs.getLong("RuleId"));
			fsd.setMaxScore(rs.getBigDecimal("MaxScore"));
			fsd.setExecScore(rs.getBigDecimal("ExecScore"));

			if (type.contains("View")) {
				fsd.setSubGrpCodeDesc(rs.getString("SubGrpCodeDesc"));
				fsd.setRuleCode(rs.getString("RuleCode"));
				fsd.setRuleCodeDesc(rs.getString("RuleCodeDesc"));
				fsd.setCategoryType(rs.getString("CategoryType"));
			}

			return fsd;
		});
	}

	@Override
	public void saveDetailList(List<FinanceScoreDetail> scoreDetails, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinanceScoreDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (HeaderId, SubGroupId, RuleId, MaxScore, ExecScore)");
		sql.append(" Values(?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceScoreDetail fsd = scoreDetails.get(i);
				int index = 1;

				ps.setLong(index++, fsd.getHeaderId());
				ps.setLong(index++, fsd.getSubGroupId());
				ps.setLong(index++, fsd.getRuleId());
				ps.setBigDecimal(index++, fsd.getMaxScore());
				ps.setBigDecimal(index, fsd.getExecScore());
			}

			@Override
			public int getBatchSize() {
				return scoreDetails.size();
			}
		});
	}

	@Override
	public void deleteDetailList(List<Long> headerIds, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinanceScoreDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderId in (");

		int i = 0;
		while (i < headerIds.size()) {
			sql.append(" ?,");
			i++;
		}

		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			for (Long id : headerIds) {
				ps.setLong(index++, id);
			}
		});

	}

	@Override
	public boolean deleteHeader(FinanceScoreHeader scoreHeader, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinanceScoreHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderId = ? and FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setLong(1, scoreHeader.getHeaderId());
			ps.setLong(2, scoreHeader.getFinID());
		}) > 0;
	}

	@Override
	public void deleteDetailList(long headerId, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinanceScoreHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, headerId));
	}
}
