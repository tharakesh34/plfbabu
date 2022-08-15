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
 *********************************************************************************************
 * FILE HEADER *
 *********************************************************************************************
 *
 * FileName : FinFeeCharges.java
 * 
 * Author : PENNANT TECHONOLOGIES
 * 
 * Creation Date : 10-06-2014
 * 
 * Modified Date : 10-06-2014
 * 
 * Description :
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-06-2014 PENNANT TECHONOLOGIES 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.rulefactory.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.rulefactory.FinFeeChargesDAO;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinFeeChargesDAOImpl extends BasicDao<FeeRule> implements FinFeeChargesDAO {
	private static Logger logger = LogManager.getLogger(FinFeeChargesDAOImpl.class);

	public FinFeeChargesDAOImpl() {
		super();
	}

	@Override
	public FeeRule getFeeChargesByFinRefAndFee(long finID, String feeCode, String tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  FinID, FinReference, SchDate, FinEvent, FeeCode, SeqNo, FeeCodeDesc, FeeOrder, FeeToFinance");
		sql.append(", AllowWaiver, WaiverPerc, CalFeeAmount, FeeAmount, WaiverAmount, PaidAmount");
		sql.append(", ExcludeFromRpt, CalFeeModify, FeeMethod, ScheduleTerms");
		sql.append(" From FinFeeCharges");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where  FinID = ? and FeeCode = ? order by SchDate, FeeOrder");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
			FeeRule fr = new FeeRule();

			fr.setFinID(rs.getLong("FinID"));
			fr.setFinReference(rs.getString("FinReference"));
			fr.setSchDate(rs.getDate("SchDate"));
			fr.setFinEvent(rs.getString("FinEvent"));
			fr.setFeeCode(rs.getString("FeeCode"));
			fr.setSeqNo(rs.getInt("SeqNo"));
			fr.setFeeCodeDesc(rs.getString("FeeCodeDesc"));
			fr.setFeeOrder(rs.getInt("FeeOrder"));
			fr.setFeeToFinance(rs.getString("FeeToFinance"));
			fr.setAllowWaiver(rs.getBoolean("AllowWaiver"));
			fr.setWaiverPerc(rs.getBigDecimal("WaiverPerc"));
			fr.setCalFeeAmount(rs.getBigDecimal("CalFeeAmount"));
			fr.setFeeAmount(rs.getBigDecimal("FeeAmount"));
			fr.setWaiverAmount(rs.getBigDecimal("WaiverAmount"));
			fr.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			fr.setExcludeFromRpt(rs.getBoolean("ExcludeFromRpt"));
			fr.setCalFeeModify(rs.getBoolean("CalFeeModify"));
			fr.setFeeMethod(rs.getString("FeeMethod"));
			fr.setScheduleTerms(rs.getInt("ScheduleTerms"));

			return fr;
		}, finID, feeCode);
	}

	@Override
	public boolean updateFeeChargesByFinRefAndFee(FeeRule fr, String tableType) {
		StringBuilder sql = new StringBuilder("Update FinFeeCharges");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Set ExcludeFromRpt = ? ");
		sql.append(" Where FinID = ? and FeeCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBoolean(index++, fr.isExcludeFromRpt());
			ps.setLong(index++, fr.getFinID());
			ps.setString(index++, fr.getFeeCode());
		});

		if (recordCount <= 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void saveCharges(List<FeeRule> chargeList, boolean isWIF, String tableType) {
		StringBuilder sql = new StringBuilder();

		if (isWIF) {
			sql.append(" Insert Into WIFFinFeeCharges");
		} else {
			sql.append(" Insert Into FinFeeCharges");
		}

		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" (FinID, FinReference, SchDate, FinEvent, FeeCode, SeqNo, FeeCodeDesc, FeeOrder, FeeToFinance");
		sql.append(", AllowWaiver, WaiverPerc, CalFeeAmount, FeeAmount, WaiverAmount");
		sql.append(", PaidAmount, CalFeeModify, FeeMethod, ScheduleTerms)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FeeRule fr = chargeList.get(i);
				int index = 1;

				ps.setLong(index++, fr.getFinID());
				ps.setString(index++, fr.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(fr.getSchDate()));
				ps.setString(index++, fr.getFinEvent());
				ps.setString(index++, fr.getFeeCode());
				ps.setInt(index++, fr.getSeqNo());
				ps.setString(index++, fr.getFeeCodeDesc());
				ps.setInt(index++, fr.getFeeOrder());
				ps.setString(index++, fr.getFeeToFinance());
				ps.setBoolean(index++, fr.isAllowWaiver());
				ps.setBigDecimal(index++, fr.getWaiverPerc());
				ps.setBigDecimal(index++, fr.getCalFeeAmount());
				ps.setBigDecimal(index++, fr.getFeeAmount());
				ps.setBigDecimal(index++, fr.getWaiverAmount());
				ps.setBigDecimal(index++, fr.getPaidAmount());
				ps.setBoolean(index++, fr.isCalFeeModify());
				ps.setString(index++, fr.getFeeMethod());
				ps.setInt(index++, fr.getScheduleTerms());
			}

			@Override
			public int getBatchSize() {
				return chargeList.size();
			}
		});
	}

	@Override
	public void deleteChargesBatch(long finID, String finEvent, boolean isWIF, String tableType) {
		StringBuilder sql = new StringBuilder();

		if (isWIF) {
			sql.append(" Delete From WIFFinFeeCharges");
		} else {
			sql.append(" Delete From FinFeeCharges");
		}

		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where FinID = ? and FinEvent = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index++, finEvent);
		});
	}

	@Override
	public List<FeeRule> getFeeChargesByFinRef(long finID, String finEvent, boolean isWIF, String tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, SchDate, FinEvent, FeeCode, SeqNo, FeeCodeDesc, FeeOrder, FeeToFinance");
		sql.append(", AllowWaiver, WaiverPerc, CalFeeAmount, FeeAmount, WaiverAmount");
		sql.append(", PaidAmount, CalFeeModify, FeeMethod, ScheduleTerms");

		if (isWIF) {
			sql.append(" From WIFFinFeeCharges");
		} else {

			if (!"_VIEW".equalsIgnoreCase(tableType) && !"_AVIEW".equalsIgnoreCase(tableType)) {
				sql.append(", ExcludeFromRpt");
			}
			sql.append(" From FinFeeCharges");
		}

		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where  FinID = ?");

		if (StringUtils.isNotEmpty(finEvent)) {
			sql.append(" and FinEvent = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);

			if (StringUtils.isNotEmpty(finEvent)) {
				ps.setString(index++, finEvent);
			}

		}, (rs, num) -> {
			FeeRule fr = new FeeRule();

			fr.setFinID(rs.getLong("FinID"));
			fr.setFinReference(rs.getString("FinReference"));
			fr.setSchDate(rs.getDate("SchDate"));
			fr.setFinEvent(rs.getString("FinEvent"));
			fr.setFeeCode(rs.getString("FeeCode"));
			fr.setSeqNo(rs.getInt("SeqNo"));
			fr.setFeeCodeDesc(rs.getString("FeeCodeDesc"));
			fr.setFeeOrder(rs.getInt("FeeOrder"));
			fr.setFeeToFinance(rs.getString("FeeToFinance"));
			fr.setAllowWaiver(rs.getBoolean("AllowWaiver"));
			fr.setWaiverPerc(rs.getBigDecimal("WaiverPerc"));
			fr.setCalFeeAmount(rs.getBigDecimal("CalFeeAmount"));
			fr.setFeeAmount(rs.getBigDecimal("FeeAmount"));
			fr.setWaiverAmount(rs.getBigDecimal("WaiverAmount"));
			fr.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			fr.setCalFeeModify(rs.getBoolean("CalFeeModify"));
			fr.setFeeMethod(rs.getString("FeeMethod"));
			fr.setScheduleTerms(rs.getInt("ScheduleTerms"));

			if (!"_VIEW".equalsIgnoreCase(tableType) && !"_AVIEW".equalsIgnoreCase(tableType)) {
				fr.setExcludeFromRpt(rs.getBoolean("ExcludeFromRpt"));
			}

			return fr;
		});
	}
}
