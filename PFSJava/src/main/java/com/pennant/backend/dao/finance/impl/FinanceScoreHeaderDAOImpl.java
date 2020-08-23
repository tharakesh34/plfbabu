package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinanceScoreHeaderDAO;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinanceScoreHeaderDAOImpl extends SequenceDao<FinanceScoreHeader> implements FinanceScoreHeaderDAO {
	private static Logger logger = Logger.getLogger(FinanceScoreHeaderDAOImpl.class);

	public FinanceScoreHeaderDAOImpl() {
		super();
	}

	@Override
	public List<FinanceScoreHeader> getFinScoreHeaderList(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, FinReference, GroupId, MinScore, Override");
		sql.append(", OverrideScore, CreditWorth, CustId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", GroupCode, GroupCodeDesc");
		}

		sql.append(" from FinanceScoreHeader");
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
			}, new RowMapper<FinanceScoreHeader>() {
				@Override
				public FinanceScoreHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceScoreHeader fsh = new FinanceScoreHeader();

					fsh.setHeaderId(rs.getLong("HeaderId"));
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
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public long saveHeader(FinanceScoreHeader scoreHeader, String type) {
		logger.debug("Entering");

		if (scoreHeader.getHeaderId() == Long.MIN_VALUE) {
			scoreHeader.setId(getNextValue("SeqFinanceScoreHeader"));
			logger.debug("get NextID:" + scoreHeader.getId());
		}

		StringBuilder insertSql = new StringBuilder("INSERT INTO FinanceScoreHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (HeaderId , FinReference , GroupId , MinScore , ");
		insertSql.append(" Override , OverrideScore , CreditWorth, CustId) ");
		insertSql.append(" VALUES (:HeaderId , :FinReference , :GroupId , :MinScore , ");
		insertSql.append(" :Override , :OverrideScore , :CreditWorth, :CustId) ");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoreHeader);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return scoreHeader.getId();
	}

	@Override
	public void deleteHeaderList(String finReferecne, String type) {
		logger.debug("Entering");

		FinanceScoreHeader header = new FinanceScoreHeader();
		header.setFinReference(finReferecne);

		StringBuilder deleteSql = new StringBuilder(" DELETE FROM FinanceScoreHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" WHERE FinReference=:FinReference");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<FinanceScoreDetail> getFinScoreDetailList(List<Long> headerIds, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(" SELECT HeaderId , SubGroupId , ");
		selectSql.append(" RuleId , MaxScore , ExecScore ");
		if (type.contains("View")) {
			selectSql.append(" , SubGrpCodeDesc , RuleCode , RuleCodeDesc , CategoryType ");
		}
		selectSql.append(" From FinanceScoreDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where HeaderId  IN(:HeaderId )");

		Map<String, List<Long>> parameterMap = new HashMap<String, List<Long>>();
		parameterMap.put("HeaderId", headerIds);

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceScoreDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScoreDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), parameterMap, typeRowMapper);
	}

	@Override
	public void saveDetailList(List<FinanceScoreDetail> scoreDetails, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("INSERT INTO FinanceScoreDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (HeaderId , SubGroupId , RuleId , MaxScore , ExecScore) ");
		insertSql.append(" VALUES (:HeaderId , :SubGroupId , :RuleId , :MaxScore , :ExecScore) ");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(scoreDetails.toArray());

		logger.debug("Leaving");
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
	}

	@Override
	public void deleteDetailList(List<Long> headerIdList, String type) {
		logger.debug("Entering");
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("HeaderIdList", headerIdList);

		StringBuilder deleteSql = new StringBuilder(" DELETE FROM FinanceScoreDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" WHERE HeaderId IN(:HeaderIdList) ");

		logger.debug("deleteSql: " + deleteSql.toString());
		logger.debug("Leaving");
		this.jdbcTemplate.update(deleteSql.toString(), parameterSource);

	}

	@Override
	public boolean deleteHeader(FinanceScoreHeader scoreHeader, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder(" DELETE FROM FinanceScoreHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" WHERE HeaderId=:HeaderId AND FinReference=:FinReference");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoreHeader);
		int recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
		if (recordCount <= 0) {
			return false;
		}
		return true;
	}

	@Override
	public void deleteDetailList(long headerId, String type) {
		logger.debug("Entering");

		FinanceScoreDetail detail = new FinanceScoreDetail();
		detail.setHeaderId(headerId);

		StringBuilder deleteSql = new StringBuilder(" DELETE FROM FinanceScoreDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" WHERE HeaderId=:HeaderId");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		logger.debug("Leaving");
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
	}

}
