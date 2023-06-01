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
 * * FileName : UploadAllocationDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-07-2018 * *
 * Modified Date : 13-07-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-07-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.UploadAllocationDetailDAO;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class UploadAllocationDetailDAOImpl extends SequenceDao<UploadAlloctionDetail>
		implements UploadAllocationDetailDAO {

	private static Logger logger = LogManager.getLogger(UploadAllocationDetailDAOImpl.class);

	public UploadAllocationDetailDAOImpl() {
		super();
	}

	/**
	 * This method insert new Records into UploadHeader or UploadHeader_Temp.
	 * 
	 * save Promotion
	 * 
	 * @param Promotion (promotion)
	 * @param type      (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(List<UploadAlloctionDetail> uploadAllocationDetailList, long details, String rootId) {
		uploadAllocationDetailList.forEach(uad -> {
			if (uad.getUploadAlloctionDetailId() == Long.MIN_VALUE) {
				uad.setUploadAlloctionDetailId(getNextValue("SeqUploadAllocationDetail"));
			}
			uad.setUploadDetailId(details);
			uad.setRootId(rootId);
		});

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" UploadAlloctionDetails");
		sql.append("(UploadDetailId, UploadAlloctionDetailId, rootId, AllocationType, ReferenceCode, PaidAmount");
		sql.append(", WaivedAmount");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				UploadAlloctionDetail uad = uploadAllocationDetailList.get(i);
				int index = 1;
				ps.setLong(index++, uad.getUploadDetailId());
				ps.setLong(index++, uad.getUploadAlloctionDetailId());
				ps.setString(index++, uad.getRootId());
				ps.setString(index++, uad.getAllocationType());
				ps.setString(index++, uad.getReferenceCode());
				ps.setBigDecimal(index++, uad.getPaidAmount());
				ps.setBigDecimal(index, uad.getWaivedAmount());
			}

			@Override
			public int getBatchSize() {
				return uploadAllocationDetailList.size();
			}
		});
	}

	@Override
	public void delete(long uploadDetailId) {
		StringBuilder sql = new StringBuilder("Delete From UploadAlloctiondetails");
		sql.append(" where UploadDetailId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), new Object[] { uploadDetailId });
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public List<UploadAlloctionDetail> getUploadedAllocatations(long ulDetailID) {
		List<UploadAlloctionDetail> upldAllctnDtlList = new ArrayList<>();
		UploadAlloctionDetail ulAlocDetail = new UploadAlloctionDetail();
		ulAlocDetail.setUploadDetailId(ulDetailID);

		StringBuilder sql = new StringBuilder(" Select");
		sql.append(" UploadDetailId, UploadAlloctionDetailId,rootId,");
		sql.append(" AllocationType, ReferenceCode, PaidAmount, WaivedAmount");
		sql.append(" From UploadAlloctionDetails");
		sql.append(" Where UploadDetailId = ?");

		logger.trace(Literal.SQL + sql.toString());

		upldAllctnDtlList = this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, ulDetailID), (rs, rowNum) -> {
			UploadAlloctionDetail uad = new UploadAlloctionDetail();
			uad.setUploadDetailId(JdbcUtil.getLong(rs.getLong("UploadDetailId")));
			uad.setUploadAlloctionDetailId(JdbcUtil.getLong(rs.getLong("UploadAlloctionDetailId")));
			uad.setRootId(rs.getString("RootId"));
			uad.setAllocationType(rs.getString("AllocationType"));
			uad.setReferenceCode(rs.getString("ReferenceCode"));
			uad.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			uad.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			return uad;

		});

		return upldAllctnDtlList.stream().sorted(
				(uad1, uad2) -> Long.compare(uad1.getUploadAlloctionDetailId(), uad2.getUploadAlloctionDetailId()))
				.collect(Collectors.toList());
	}

}