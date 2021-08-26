/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : DedupParmDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-08-2011 * * Modified
 * Date : 23-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.findedup.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.findedup.FinanceDedupeDAO;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceDedup model</b> class.<br>
 * 
 */
public class FinanceDedupeDAOImpl extends BasicDao<FinanceDedup> implements FinanceDedupeDAO {
	private static Logger logger = LogManager.getLogger(FinanceDedupeDAOImpl.class);

	public FinanceDedupeDAOImpl() {
		super();
	}

	@Override
	public void saveList(List<FinanceDedup> dedups, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinDedupDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, DupReference, CustCIF, CustCRCPR, CustShrtName");
		sql.append(", MobileNumber, StartDate, FinanceAmount, FinanceType, ProfitAmount");
		sql.append(", Stage, DedupeRule, OverrideUser, FinLimitRef)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceDedup fd = dedups.get(i);

				int index = 1;

				ps.setLong(index++, fd.getFinID());
				ps.setString(index++, fd.getFinReference());
				ps.setString(index++, fd.getDupReference());
				ps.setString(index++, fd.getCustCIF());
				ps.setString(index++, fd.getCustCRCPR());
				ps.setString(index++, fd.getCustShrtName());
				ps.setString(index++, fd.getMobileNumber());
				ps.setDate(index++, JdbcUtil.getDate(fd.getStartDate()));
				ps.setBigDecimal(index++, fd.getFinanceAmount());
				ps.setString(index++, fd.getFinanceType());
				ps.setBigDecimal(index++, fd.getProfitAmount());
				ps.setString(index++, fd.getStage());
				ps.setString(index++, fd.getDedupeRule());
				ps.setString(index++, fd.getOverrideUser());
				ps.setString(index++, fd.getFinLimitRef());
			}

			@Override
			public int getBatchSize() {
				return dedups.size();
			}
		});

	}

	@Override
	public void updateList(List<FinanceDedup> dedups) {
		StringBuilder sql = new StringBuilder("Update FinDedupDetail");
		sql.append(" Set CustCIF = ?, CustCRCPR = ?, CustShrtName = ?, MobileNumber = ?");
		sql.append(", StartDate = ?, FinanceAmount = ?, FinanceType = ?, ProfitAmount= ?");
		sql.append(", Stage = ?, DedupeRule = ?, OverrideUser = ?, FinLimitRef = ?");
		sql.append(" Where FinID = ? and DupReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceDedup fd = dedups.get(i);
				int index = 1;

				ps.setString(index++, fd.getCustCIF());
				ps.setString(index++, fd.getCustCRCPR());
				ps.setString(index++, fd.getCustShrtName());
				ps.setString(index++, fd.getMobileNumber());
				ps.setDate(index++, JdbcUtil.getDate(fd.getStartDate()));
				ps.setBigDecimal(index++, fd.getFinanceAmount());
				ps.setString(index++, fd.getFinanceType());
				ps.setBigDecimal(index++, fd.getProfitAmount());
				ps.setString(index++, fd.getStage());
				ps.setString(index++, fd.getDedupeRule());
				ps.setString(index++, fd.getOverrideUser());
				ps.setString(index++, fd.getFinLimitRef());

				ps.setLong(index++, fd.getFinID());
				ps.setString(index++, fd.getDupReference());
			}

			@Override
			public int getBatchSize() {
				return dedups.size();
			}
		});
	}

	@Override
	public List<FinanceDedup> fetchOverrideDedupData(long finID, String queryCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" d.FinID, d.FinReference, d.DupReference, d.CustCIF, d.CustCRCPR, d.CustShrtName");
		sql.append(", d.MobileNumber, d.StartDate, d.FinanceAmount, d.FinanceType, d.ProfitAmount");
		sql.append(", d.Stage, d.DedupeRule, d.OverrideUser, s.RoleDesc StageDesc, d.FinLimitRef");
		sql.append(" From FinDedupDetail d");
		sql.append(" Left Outer Join SecRoles s on s.RoleCd = d.Stage");
		sql.append(" Where d.FinID = ? and d.DedupeRule LIKE  ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			ps.setString(2, "%," + queryCode.trim() + ",%");
		}, (rs, i) -> {
			FinanceDedup fd = new FinanceDedup();

			fd.setFinID(rs.getLong("FinID"));
			fd.setFinReference(rs.getString("FinReference"));
			fd.setDupReference(rs.getString("DupReference"));
			fd.setCustCIF(rs.getString("CustCIF"));
			fd.setCustCRCPR(rs.getString("CustCRCPR"));
			fd.setCustShrtName(rs.getString("CustShrtName"));
			fd.setMobileNumber(rs.getString("MobileNumber"));
			fd.setStartDate(JdbcUtil.getDate(rs.getDate("StartDate")));
			fd.setFinanceAmount(rs.getBigDecimal("FinanceAmount"));
			fd.setFinanceType(rs.getString("FinanceType"));
			fd.setProfitAmount(rs.getBigDecimal("ProfitAmount"));
			fd.setStage(rs.getString("Stage"));
			fd.setDedupeRule(rs.getString("DedupeRule"));
			fd.setOverrideUser(rs.getString("OverrideUser"));
			fd.setStageDesc(rs.getString("StageDesc"));
			fd.setFinLimitRef(rs.getString("FinLimitRef"));

			return fd;
		});

	}

	// no calling available
	@Override
	public void deleteList(String finReference) {
		logger.debug("Entering");
		FinanceDedup dedup = new FinanceDedup();
		dedup.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From FinDedupDetail");
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void moveData(String finReference, String suffix) {
		/* FIXME : change to FinID Pre-approved(_PA) tables need to remove */
		try {
			if (StringUtils.isBlank(suffix)) {
				return;
			}

			MapSqlParameterSource map = new MapSqlParameterSource();
			map.addValue("FinReference", finReference);

			StringBuilder selectSql = new StringBuilder();
			selectSql.append(" SELECT * FROM FinDedupDetail");
			selectSql.append(" WHERE FinReference = :FinReference ");

			RowMapper<FinanceDedup> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceDedup.class);
			List<FinanceDedup> list = this.jdbcTemplate.query(selectSql.toString(), map, typeRowMapper);

			if (list != null && !list.isEmpty()) {
				saveList(list, suffix);
			}

		} catch (DataAccessException e) {
			//
		}
	}

	@Override
	public List<FinanceDedup> fetchFinanceDedup(FinanceDedup dedup, String queryCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustCIF, FinanceType, FinID, FinReference");
		sql.append(" From FinanceDedup_View");
		sql.append(queryCode.trim());

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		RowMapper<FinanceDedup> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceDedup.class);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);

	}

}