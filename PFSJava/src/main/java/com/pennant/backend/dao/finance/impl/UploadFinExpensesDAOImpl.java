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
 * * FileName : UploadFinExpensesDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2017 * *
 * Modified Date : 17-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.UploadFinExpensesDAO;
import com.pennant.backend.model.expenses.UploadFinExpenses;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>UploadFinExpenses model</b> class.<br>
 * 
 */
public class UploadFinExpensesDAOImpl extends BasicDao<UploadFinExpenses> implements UploadFinExpensesDAO {
	private static Logger logger = LogManager.getLogger(UploadFinExpensesDAOImpl.class);

	public UploadFinExpensesDAOImpl() {
		super();
	}

	@Override
	public void saveUploadFinExpenses(List<UploadFinExpenses> uploadFinExpensesList) {
		StringBuilder sql = new StringBuilder("Insert Into UploadFinExpenses");
		sql.append(" (UploadId, FinType, FinID, FinReference, FinApprovalStartDate, FinApprovalEndDate");
		sql.append(", ExpenseTypeCode, Percentage, AmountValue, Type, Status, Reason)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				UploadFinExpenses ufe = uploadFinExpensesList.get(i);

				int index = 1;

				ps.setLong(index++, ufe.getUploadId());
				ps.setString(index++, ufe.getFinType());
				ps.setLong(index++, ufe.getFinID());
				ps.setString(index++, ufe.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(ufe.getFinApprovalStartDate()));
				ps.setDate(index++, JdbcUtil.getDate(ufe.getFinApprovalEndDate()));
				ps.setString(index++, ufe.getExpenseTypeCode());
				ps.setBigDecimal(index++, ufe.getPercentage());
				ps.setBigDecimal(index++, ufe.getAmountValue());
				ps.setString(index++, ufe.getType());
				ps.setString(index++, ufe.getStatus());
				ps.setString(index++, ufe.getReason());

			}

			@Override
			public int getBatchSize() {
				return uploadFinExpensesList.size();
			}
		});
	}

}