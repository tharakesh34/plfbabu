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
 * * FileName : FinFeeConfigDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : * * Modified Date : * *
 * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.FinFeeConfigDAO;
import com.pennant.backend.model.finance.FinFeeConfig;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>FinFeeConfigDAO </b> class.<br>
 * 
 */

public class FinFeeConfigDAOImpl extends SequenceDao<FinFeeDetail> implements FinFeeConfigDAO {
	private static Logger logger = LogManager.getLogger(FinFeeConfigDAOImpl.class);

	public FinFeeConfigDAOImpl() {
		super();
	}

	@Override
	public String save(FinFeeConfig fc, TableType tableType) {
		List<FinFeeConfig> list = new ArrayList<>();

		list.add(fc);

		saveList(list, tableType.getSuffix());

		return fc.getFinReference();
	}

	@Override
	public void saveList(List<FinFeeConfig> finFeeDetailConfig, String type) {
		String sql = getSaveQuery(type);

		logger.debug(Literal.SQL + sql);

		try {
			this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					FinFeeConfig fc = finFeeDetailConfig.get(i);
					int index = 1;

					ps.setLong(index++, fc.getFinID());
					ps.setString(index++, fc.getFinReference());
					ps.setBoolean(index++, fc.isOriginationFee());
					ps.setString(index++, fc.getFinEvent());
					ps.setLong(index++, fc.getFeeTypeID());
					ps.setInt(index++, fc.getFeeOrder());
					ps.setString(index++, fc.getFeeScheduleMethod());
					ps.setString(index++, fc.getRuleCode());
					ps.setString(index++, fc.getCalculationType());
					ps.setBigDecimal(index++, fc.getAmount());
					ps.setBigDecimal(index++, fc.getPercentage());
					ps.setString(index++, fc.getCalculateOn());
					ps.setBoolean(index++, fc.isAlwDeviation());
					ps.setBoolean(index++, fc.isAlwModifyFeeSchdMthd());
					ps.setBoolean(index++, fc.isAlwModifyFee());
					ps.setBigDecimal(index++, fc.getMaxWaiverPerc());
					ps.setLong(index++, fc.getModuleId());
					ps.setLong(index++, fc.getReferenceId());
					ps.setLong(index++, fc.getFinTypeFeeId());
					ps.setBoolean(index++, fc.isAlwPreIncomization());
					ps.setString(index++, fc.getPercType());
					ps.setString(index++, fc.getPercRule());
					ps.setLong(index++, fc.getPercRuleId());

				}

				@Override
				public int getBatchSize() {
					return finFeeDetailConfig.size();
				}
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

	}

	private String getSaveQuery(String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinFeeConfig");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, OriginationFee, FinEvent, FeeTypeID, FeeOrder");
		sql.append(", FeeScheduleMethod, RuleCode, CalculationType, Amount");
		sql.append(", Percentage, CalculateOn, AlwDeviation, AlwModifyFeeSchdMthd, AlwModifyFee");
		sql.append(", MaxWaiverPerc, ModuleId, ReferenceId, FinTypeFeeId, AlwPreIncomization");
		sql.append(", PercType, PercRule, PercRuleId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?)");

		return sql.toString();
	}

	@Override
	public List<FinFeeConfig> getFinFeeConfigList(long finID, String eventCode, boolean origination, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinTypeFeeId, FinID, FinReference, OriginationFee, FinEvent, FeeTypeID, FeeOrder");
		sql.append(", FeeScheduleMethod, CalculationType, RuleCode, Amount, Percentage, CalculateOn, AlwDeviation");
		sql.append(", MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, AlwPreIncomization");
		sql.append(", PercType, PercRule, ReferenceId, PercRuleId");

		if (type.contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, RuleDesc, TaxApplicable, TaxComponent");
		}

		sql.append(" From Finfeeconfig");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and FinEvent = ? and OriginationFee = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index++, eventCode);
			ps.setBoolean(index++, origination);
		}, (rs, rowNum) -> {
			FinFeeConfig ffc = new FinFeeConfig();

			ffc.setFinTypeFeeId(rs.getLong("FinTypeFeeId"));
			ffc.setFinID(rs.getLong("FinID"));
			ffc.setFinReference(rs.getString("FinReference"));
			ffc.setOriginationFee(rs.getBoolean("OriginationFee"));
			ffc.setFinEvent(rs.getString("FinEvent"));
			ffc.setFeeTypeID(rs.getLong("FeeTypeID"));
			ffc.setFeeOrder(rs.getInt("FeeOrder"));
			ffc.setFeeScheduleMethod(rs.getString("FeeScheduleMethod"));
			ffc.setCalculationType(rs.getString("CalculationType"));
			ffc.setRuleCode(rs.getString("RuleCode"));
			ffc.setAmount(rs.getBigDecimal("Amount"));
			ffc.setPercentage(rs.getBigDecimal("Percentage"));
			ffc.setCalculateOn(rs.getString("CalculateOn"));
			ffc.setAlwDeviation(rs.getBoolean("AlwDeviation"));
			ffc.setMaxWaiverPerc(rs.getBigDecimal("MaxWaiverPerc"));
			ffc.setAlwModifyFee(rs.getBoolean("AlwModifyFee"));
			ffc.setAlwModifyFeeSchdMthd(rs.getBoolean("AlwModifyFeeSchdMthd"));
			ffc.setAlwPreIncomization(rs.getBoolean("AlwPreIncomization"));
			ffc.setPercType(rs.getString("PercType"));
			ffc.setPercRule(rs.getString("PercRule"));
			ffc.setReferenceId(rs.getLong("ReferenceId"));
			ffc.setReferenceId(rs.getLong("PercRuleId"));

			if (type.contains("View")) {
				ffc.setFeeTypeCode(rs.getString("FeeTypeCode"));
				ffc.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				// ffc.setRuleDesc(rs.getString("RuleDesc"));
				ffc.setTaxApplicable(rs.getBoolean("TaxApplicable"));
				ffc.setTaxComponent(rs.getString("TaxComponent"));
			}

			return ffc;
		});
	}

	@Override
	public int getFinFeeConfigCountByRuleCode(String ruleCode, String type) {
		StringBuilder sql = new StringBuilder("Select Count(FinID)");
		sql.append(" From FinFeeConfig");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PercRule = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, ruleCode);
	}

	@Override
	public void update(FinFeeConfig entity, TableType tableType) {
		//
	}

	@Override
	public void delete(FinFeeConfig entity, TableType tableType) {
		//
	}

}