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
 * * FileName : IRRScheduleDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.IRRScheduleDetailDAO;
import com.pennant.backend.model.finance.IRRScheduleDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>WIFIRRScheduleDetail model</b> class.<br>
 */
public class IRRScheduleDetailDAOImpl extends BasicDao<IRRScheduleDetail> implements IRRScheduleDetailDAO {
	private static Logger logger = LogManager.getLogger(IRRScheduleDetailDAOImpl.class);

	public IRRScheduleDetailDAOImpl() {
		super();
	}

	@Override
	public void saveList(List<IRRScheduleDetail> irrdList) {
		StringBuilder sql = new StringBuilder("Insert Into IRRScheduleDetail");
		sql.append(" (FinID, FinReference, SchDate, ProfitCalc, PrincipalCalc");
		sql.append(", RepayAmount, ClosingBalance, GapInterst)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					IRRScheduleDetail irrd = irrdList.get(i);

					int index = 1;

					ps.setLong(index++, irrd.getFinID());
					ps.setString(index++, irrd.getFinReference());
					ps.setDate(index++, JdbcUtil.getDate(irrd.getSchDate()));
					ps.setBigDecimal(index++, irrd.getProfitCalc());
					ps.setBigDecimal(index++, irrd.getPrincipalCalc());
					ps.setBigDecimal(index++, irrd.getRepayAmount());
					ps.setBigDecimal(index++, irrd.getClosingBalance());
					ps.setBigDecimal(index++, irrd.getGapInterst());
				}

				@Override
				public int getBatchSize() {
					return irrdList.size();
				}
			});
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<IRRScheduleDetail> getIRRScheduleDetailList(long finID) {
		String sql = "Select FinID, FinReference, SchDate, ProfitCalc, PrincipalCalc, RepayAmount, ClosingBalance, GapInterst From IRRScheduleDetail Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		List<IRRScheduleDetail> finSchdDetails = this.jdbcOperations.query(sql, ps -> {
			int index = 1;

			ps.setLong(index++, finID);
		}, (rs, num) -> {
			IRRScheduleDetail irrd = new IRRScheduleDetail();

			irrd.setFinID(rs.getLong("FinID"));
			irrd.setFinReference(rs.getString("FinReference"));
			irrd.setSchDate(rs.getDate("SchDate"));
			irrd.setProfitCalc(rs.getBigDecimal("ProfitCalc"));
			irrd.setPrincipalCalc(rs.getBigDecimal("PrincipalCalc"));
			irrd.setRepayAmount(rs.getBigDecimal("RepayAmount"));
			irrd.setClosingBalance(rs.getBigDecimal("ClosingBalance"));
			irrd.setGapInterst(rs.getBigDecimal("GapInterst"));

			return irrd;
		});

		return finSchdDetails.stream().sorted((f1, f2) -> DateUtil.compare(f1.getSchDate(), f2.getSchDate()))
				.collect(Collectors.toList());
	}

}