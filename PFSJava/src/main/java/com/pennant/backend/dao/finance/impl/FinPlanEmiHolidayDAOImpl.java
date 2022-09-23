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
 * * FileName : FinPlanEmiHolidayDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-10-2011 * *
 * Modified Date : 13-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.FinPlanEmiHolidayDAO;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinPlanEmiHoliday model</b> class.<br>
 */
public class FinPlanEmiHolidayDAOImpl extends BasicDao<FinPlanEmiHoliday> implements FinPlanEmiHolidayDAO {
	private static Logger logger = LogManager.getLogger(FinPlanEmiHolidayDAOImpl.class);

	public FinPlanEmiHolidayDAOImpl() {
		super();
	}

	@Override
	public List<Integer> getPlanEMIHMonthsByRef(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PlanEMIHMonth From FinPlanEMIHMonths");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForList(sql.toString(), Integer.class, finID);
	}

	@Override
	public List<Date> getPlanEMIHDatesByRef(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PlanEMIHDate From FinPlanEMIHDates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForList(sql.toString(), Date.class, finID);
	}

	@Override
	public void deletePlanEMIHMonths(long finID, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinPlanEMIHMonths");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, finID);
		});
	}

	@Override
	public void savePlanEMIHMonths(List<FinPlanEmiHoliday> emim, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinPlanEMIHMonths");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, PlanEMIHMonth)");
		sql.append(" Values(?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					FinPlanEmiHoliday emih = emim.get(i);
					int index = 1;

					ps.setLong(index++, emih.getFinID());
					ps.setString(index++, emih.getFinReference());
					ps.setInt(index, emih.getPlanEMIHMonth());
				}

				@Override
				public int getBatchSize() {
					return emim.size();
				}
			});
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void deletePlanEMIHDates(long finID, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinPlanEMIHDates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, finID);
		});
	}

	@Override
	public void savePlanEMIHDates(List<FinPlanEmiHoliday> planEMIHDates, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinPlanEMIHDates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, PlanEMIHDate)");
		sql.append(" Values(?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					FinPlanEmiHoliday emih = planEMIHDates.get(i);
					int index = 1;

					ps.setLong(index++, emih.getFinID());
					ps.setString(index++, emih.getFinReference());
					ps.setDate(index, JdbcUtil.getDate(emih.getPlanEMIHDate()));
				}

				@Override
				public int getBatchSize() {
					return planEMIHDates.size();
				}
			});
		} catch (Exception e) {
			throw e;
		}

	}

}