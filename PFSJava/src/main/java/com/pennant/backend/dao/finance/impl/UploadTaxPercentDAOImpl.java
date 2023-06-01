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
 * * FileName : UploadHeaderDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2017 * * Modified
 * Date : 17-12-2017 * * Description : * *
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
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.UploadTaxPercentDAO;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>UploadTaxPercent model</b> class.<br>
 * 
 */
public class UploadTaxPercentDAOImpl extends BasicDao<UploadTaxPercent> implements UploadTaxPercentDAO {
	private static Logger logger = LogManager.getLogger(UploadTaxPercentDAOImpl.class);

	public UploadTaxPercentDAOImpl() {
		super();
	}

	@Override
	public void saveUploadDetails(List<UploadTaxPercent> uploadDetailsList) {
		String sql = "Insert Into UploadTaxPercent (UploadId, FinID, FinReference, FeeTypeCode, TaxPercent, Status, Reason) Values (?, ?, ?, ?, ?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				UploadTaxPercent tax = uploadDetailsList.get(i);
				int index = 1;

				ps.setLong(index++, tax.getUploadId());
				ps.setLong(index++, tax.getFinID());
				ps.setString(index++, tax.getFinReference());
				ps.setString(index++, tax.getFeeTypeCode());
				ps.setBigDecimal(index++, tax.getTaxPercent());
				ps.setString(index++, tax.getStatus());
				ps.setString(index, tax.getReason());

			}

			@Override
			public int getBatchSize() {
				return uploadDetailsList.size();
			}
		});
	}

	@Override
	public List<UploadTaxPercent> getSuccesFailedCount(long uploadId) {
		String sql = "Select Count(UploadId) UploadId, Status From UploadTaxPercent Where UploadId = ?";

		logger.debug(Literal.SQL + sql);

		List<UploadTaxPercent> uploadList = this.jdbcOperations.query(sql, ps -> ps.setLong(1, uploadId),
				(rs, rowNum) -> {
					UploadTaxPercent tax = new UploadTaxPercent();

					tax.setUploadId(rs.getLong("UploadId"));
					tax.setStatus(rs.getString("Status"));

					return tax;
				});

		return uploadList.stream().sorted((l1, l2) -> StringUtils.compare(l1.getStatus(), l2.getStatus()))
				.collect(Collectors.toList());
	}

}