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
 * * FileName : FinanceRateReviewDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-12-2011 * *
 * Modified Date : 02-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.finance.FinanceRateReviewDAO;
import com.pennant.backend.model.finance.FinanceRateReview;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>RepayInstruction model</b> class.<br>
 * 
 */

public class FinanceRateReviewDAOImpl extends BasicDao<FinanceRateReview> implements FinanceRateReviewDAO {
	private static Logger logger = LogManager.getLogger(FinanceRateReviewDAOImpl.class);

	public FinanceRateReviewDAOImpl() {
		super();
	}

	@Override
	public List<FinanceRateReview> getFinanceRateReviewById(long finID, Date date) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, RateType, Currency, ValueDate, EffectiveDate");
		sql.append(", EventFromDate, EventToDate, RecalFromdate, RecalToDate, EMIAmount");
		sql.append(" From FinanceRateReview");
		sql.append(" Where FinID = ? and ValueDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			ps.setDate(2, JdbcUtil.getDate(date));
		}, (rs, rowNum) -> {
			FinanceRateReview frr = new FinanceRateReview();

			frr.setFinID(rs.getLong("FinID"));
			frr.setFinReference(rs.getString("FinReference"));
			frr.setRateType(rs.getString("RateType"));
			frr.setCurrency(rs.getString("Currency"));
			frr.setValueDate(rs.getDate("ValueDate"));
			frr.setEffectiveDate(rs.getDate("EffectiveDate"));
			frr.setEventFromDate(rs.getDate("EventFromDate"));
			frr.setEventToDate(rs.getDate("EventToDate"));
			frr.setRecalFromdate(rs.getDate("RecalFromdate"));
			frr.setRecalToDate(rs.getDate("RecalToDate"));
			frr.seteMIAmount(rs.getBigDecimal("EMIAmount"));

			return frr;
		});

	}

	@Override
	public void save(FinanceRateReview frr) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" FinanceRateReview");
		sql.append(" (FinReference, RateType, Currency, ValueDate, EffectiveDate, EventFromDate");
		sql.append(", EventToDate, RecalFromdate, RecalToDate, EMIAmount) ");
		sql.append(" Values (?, ?, ?, ? , ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, frr.getFinReference());
			ps.setString(index++, frr.getRateType());
			ps.setString(index++, frr.getCurrency());
			ps.setDate(index++, JdbcUtil.getDate(frr.getValueDate()));
			ps.setDate(index++, JdbcUtil.getDate(frr.getEffectiveDate()));
			ps.setDate(index++, JdbcUtil.getDate(frr.getEventFromDate()));
			ps.setDate(index++, JdbcUtil.getDate(frr.getEventToDate()));
			ps.setDate(index++, JdbcUtil.getDate(frr.getRecalFromdate()));
			ps.setDate(index++, JdbcUtil.getDate(frr.getRecalToDate()));
			ps.setBigDecimal(index, frr.geteMIAmount());
		});

	}

}