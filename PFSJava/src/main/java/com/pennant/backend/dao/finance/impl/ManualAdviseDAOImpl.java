/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  ManualAdviseDAOImpl.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.finance.AdviseDueTaxDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ManualAdvise</code> with set of CRUD operations.
 */
public class ManualAdviseDAOImpl extends SequenceDao<ManualAdvise> implements ManualAdviseDAO {
	private static Logger logger = LogManager.getLogger(ManualAdviseDAOImpl.class);

	public ManualAdviseDAOImpl() {
		super();
	}

	@Override
	public ManualAdvise getManualAdviseById(long adviseID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseType, FinReference, FeeTypeID, Sequence, AdviseAmount, BounceID");
		sql.append(", ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(
				", BalanceAmt, PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS, WaivedCGST, WaivedSGST, WaivedUGST");
		sql.append(", WaivedIGST, WaivedCESS, FinSource, DueCreation");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxApplicable, TaxComponent, TDSReq");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from ManualAdvise");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where adviseID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { adviseID },
					new RowMapper<ManualAdvise>() {
						@Override
						public ManualAdvise mapRow(ResultSet rs, int rowNum) throws SQLException {
							ManualAdvise manualAdvise = new ManualAdvise();

							manualAdvise.setAdviseID(rs.getLong("adviseID"));
							manualAdvise.setAdviseType(rs.getInt("adviseType"));
							manualAdvise.setFinReference(rs.getString("finReference"));
							manualAdvise.setFeeTypeID(rs.getLong("feeTypeID"));
							manualAdvise.setSequence(rs.getInt("sequence"));
							manualAdvise.setAdviseAmount(rs.getBigDecimal("adviseAmount"));
							manualAdvise.setBounceID(rs.getLong("BounceID"));
							manualAdvise.setReceiptID(rs.getLong("ReceiptID"));
							manualAdvise.setPaidAmount(rs.getBigDecimal("paidAmount"));
							manualAdvise.setWaivedAmount(rs.getBigDecimal("waivedAmount"));
							manualAdvise.setRemarks(rs.getString("remarks"));
							manualAdvise.setValueDate(rs.getTimestamp("ValueDate"));
							manualAdvise.setPostDate(rs.getTimestamp("PostDate"));
							manualAdvise.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
							manualAdvise.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
							manualAdvise.setPaidCGST(rs.getBigDecimal("PaidCGST"));
							manualAdvise.setPaidSGST(rs.getBigDecimal("PaidSGST"));
							manualAdvise.setPaidUGST(rs.getBigDecimal("PaidUGST"));
							manualAdvise.setPaidIGST(rs.getBigDecimal("PaidIGST"));
							manualAdvise.setPaidCESS(rs.getBigDecimal("PaidCESS"));
							manualAdvise.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
							manualAdvise.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
							manualAdvise.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
							manualAdvise.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
							manualAdvise.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
							manualAdvise.setFinSource(rs.getString("FinSource"));
							manualAdvise.setDueCreation(rs.getBoolean("DueCreation"));

							if (type.contains("View")) {
								manualAdvise.setFeeTypeCode(rs.getString("FeeTypeCode"));
								manualAdvise.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
								manualAdvise.setTaxApplicable(rs.getBoolean("taxApplicable"));
								manualAdvise.setTaxComponent(rs.getString("taxComponent"));
								manualAdvise.setTdsReq(rs.getBoolean("TDSReq"));
							}

							manualAdvise.setVersion(rs.getInt("Version"));
							manualAdvise.setLastMntOn(rs.getTimestamp("LastMntOn"));
							manualAdvise.setLastMntBy(rs.getLong("LastMntBy"));
							manualAdvise.setRecordStatus(rs.getString("RecordStatus"));
							manualAdvise.setRoleCode(rs.getString("RoleCode"));
							manualAdvise.setNextRoleCode(rs.getString("NextRoleCode"));
							manualAdvise.setTaskId(rs.getString("TaskId"));
							manualAdvise.setNextTaskId(rs.getString("NextTaskId"));
							manualAdvise.setRecordType(rs.getString("RecordType"));
							manualAdvise.setWorkflowId(rs.getLong("WorkflowId"));

							return manualAdvise;
						}
					});

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public ManualAdvise getManualAdviseByReceiptId(long receiptID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseType, FinReference, FeeTypeID, Sequence, AdviseAmount, BounceID");
		sql.append(", ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(
				", BalanceAmt, PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS, WaivedCGST, WaivedSGST, WaivedUGST");
		sql.append(", WaivedIGST, WaivedCESS, DueCreation");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, BounceCode, BounceCodeDesc");
			sql.append(", TaxApplicable, TaxComponent, FinSource, TDSReq");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from ManualAdvise");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReceiptID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { receiptID },
					new RowMapper<ManualAdvise>() {
						@Override
						public ManualAdvise mapRow(ResultSet rs, int rowNum) throws SQLException {
							ManualAdvise manualAdvise = new ManualAdvise();

							manualAdvise.setAdviseID(rs.getLong("AdviseID"));
							manualAdvise.setAdviseType(rs.getInt("AdviseType"));
							manualAdvise.setFinReference(rs.getString("FinReference"));
							manualAdvise.setFeeTypeID(rs.getLong("FeeTypeID"));
							manualAdvise.setSequence(rs.getInt("Sequence"));
							manualAdvise.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
							manualAdvise.setBounceID(rs.getLong("BounceID"));
							manualAdvise.setReceiptID(rs.getLong("ReceiptID"));
							manualAdvise.setPaidAmount(rs.getBigDecimal("PaidAmount"));
							manualAdvise.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
							manualAdvise.setRemarks(rs.getString("Remarks"));
							manualAdvise.setValueDate(rs.getTimestamp("ValueDate"));
							manualAdvise.setPostDate(rs.getTimestamp("PostDate"));
							manualAdvise.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
							manualAdvise.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
							manualAdvise.setPaidCGST(rs.getBigDecimal("PaidCGST"));
							manualAdvise.setPaidSGST(rs.getBigDecimal("PaidSGST"));
							manualAdvise.setPaidUGST(rs.getBigDecimal("PaidUGST"));
							manualAdvise.setPaidIGST(rs.getBigDecimal("PaidIGST"));
							manualAdvise.setPaidCESS(rs.getBigDecimal("PaidCESS"));
							manualAdvise.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
							manualAdvise.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
							manualAdvise.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
							manualAdvise.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
							manualAdvise.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));

							manualAdvise.setDueCreation(rs.getBoolean("DueCreation"));

							if (type.contains("View")) {
								manualAdvise.setFeeTypeCode(rs.getString("FeeTypeCode"));
								manualAdvise.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
								manualAdvise.setBounceCode(rs.getString("BounceCode"));
								manualAdvise.setBounceCodeDesc(rs.getString("BounceCodeDesc"));
								manualAdvise.setTaxApplicable(rs.getBoolean("taxApplicable"));
								manualAdvise.setTaxComponent(rs.getString("taxComponent"));
								manualAdvise.setFinSource(rs.getString("FinSource"));
								manualAdvise.setTdsReq(rs.getBoolean("TDSReq"));
							}

							manualAdvise.setVersion(rs.getInt("Version"));
							manualAdvise.setLastMntOn(rs.getTimestamp("LastMntOn"));
							manualAdvise.setLastMntBy(rs.getLong("LastMntBy"));
							manualAdvise.setRecordStatus(rs.getString("RecordStatus"));
							manualAdvise.setRoleCode(rs.getString("RoleCode"));
							manualAdvise.setNextRoleCode(rs.getString("NextRoleCode"));
							manualAdvise.setTaskId(rs.getString("TaskId"));
							manualAdvise.setNextTaskId(rs.getString("NextTaskId"));
							manualAdvise.setRecordType(rs.getString("RecordType"));
							manualAdvise.setWorkflowId(rs.getLong("WorkflowId"));

							return manualAdvise;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public String save(ManualAdvise ma, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append("(adviseID, adviseType, finReference, feeTypeID, sequence, adviseAmount, BounceID");
		sql.append(", ReceiptID, paidAmount, waivedAmount, remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS, WaivedCGST, WaivedSGST");
		sql.append(", WaivedUGST, WaivedIGST, WaivedCESS, FinSource, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, DueCreation");
		sql.append(", LinkedTranId, HoldDue");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		if (ma.getAdviseID() <= 0) {
			ma.setAdviseID(getNewAdviseID());
		}

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, JdbcUtil.setLong(ma.getAdviseID()));
				ps.setInt(index++, ma.getAdviseType());
				ps.setString(index++, ma.getFinReference());
				ps.setLong(index++, JdbcUtil.setLong(ma.getFeeTypeID()));
				ps.setInt(index++, ma.getSequence());
				ps.setBigDecimal(index++, ma.getAdviseAmount());
				ps.setLong(index++, JdbcUtil.setLong(ma.getBounceID()));
				ps.setLong(index++, JdbcUtil.setLong(ma.getReceiptID()));
				ps.setBigDecimal(index++, ma.getPaidAmount());
				ps.setBigDecimal(index++, ma.getWaivedAmount());
				ps.setString(index++, ma.getRemarks());
				ps.setDate(index++, JdbcUtil.getDate(ma.getValueDate()));
				ps.setDate(index++, JdbcUtil.getDate(ma.getPostDate()));
				ps.setBigDecimal(index++, ma.getReservedAmt());
				ps.setBigDecimal(index++, ma.getBalanceAmt());
				ps.setBigDecimal(index++, ma.getPaidCGST());
				ps.setBigDecimal(index++, ma.getPaidSGST());
				ps.setBigDecimal(index++, ma.getPaidUGST());
				ps.setBigDecimal(index++, ma.getPaidIGST());
				ps.setBigDecimal(index++, ma.getPaidCESS());
				ps.setBigDecimal(index++, ma.getWaivedCGST());
				ps.setBigDecimal(index++, ma.getWaivedSGST());
				ps.setBigDecimal(index++, ma.getWaivedUGST());
				ps.setBigDecimal(index++, ma.getWaivedIGST());
				ps.setBigDecimal(index++, ma.getWaivedCESS());
				ps.setString(index++, ma.getFinSource());
				ps.setInt(index++, ma.getVersion());
				ps.setLong(index++, JdbcUtil.setLong(ma.getLastMntBy()));
				ps.setTimestamp(index++, ma.getLastMntOn());
				ps.setString(index++, ma.getRecordStatus());
				ps.setString(index++, ma.getRoleCode());
				ps.setString(index++, ma.getNextRoleCode());
				ps.setString(index++, ma.getTaskId());
				ps.setString(index++, ma.getNextTaskId());
				ps.setString(index++, ma.getRecordType());
				ps.setLong(index++, JdbcUtil.setLong(ma.getWorkflowId()));
				ps.setBoolean(index++, ma.isDueCreation());
				ps.setLong(index++, JdbcUtil.setLong(ma.getLinkedTranId()));
				ps.setBoolean(index++, ma.isHoldDue());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		return String.valueOf(ma.getAdviseID());
	}

	@Override
	public void update(ManualAdvise manualAdvise, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append("  set adviseType = :adviseType, finReference = :finReference, feeTypeID = :feeTypeID, ");
		sql.append(" sequence = :sequence, adviseAmount = :adviseAmount, paidAmount = :paidAmount, ");
		sql.append(" waivedAmount = :waivedAmount, remarks = :remarks,BounceID=:BounceID, ReceiptID=:ReceiptID, ");
		sql.append(" ValueDate=:ValueDate, PostDate=:PostDate, ReservedAmt=:ReservedAmt, BalanceAmt=:BalanceAmt, ");
		sql.append(
				" PaidCGST=:PaidCGST, PaidSGST=:PaidSGST, PaidUGST=:PaidUGST, PaidIGST=:PaidIGST, PaidCESS =:PaidCESS, FinSource=:FinSource,");
		sql.append(
				" WaivedCGST = :WaivedCGST, WaivedSGST = :WaivedSGST, WaivedIGST = :WaivedIGST, WaivedUGST = :WaivedUGST, WaivedCESS =:WaivedCESS,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(
				" RecordType = :RecordType, WorkflowId = :WorkflowId, DueCreation =:DueCreation, LinkedTranId =:LinkedTranId");
		sql.append(" where adviseID = :adviseID ");
		// sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ManualAdvise manualAdvise, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append(" where adviseID = :adviseID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteByAdviseId(ManualAdvise manualAdvise, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append(" where adviseID = :adviseID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		jdbcTemplate.update(sql.toString(), paramSource);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<ManualAdvise> getManualAdviseByRef(String finReference, int adviseType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseAmount, PaidAmount, WaivedAmount, ReservedAmt, BalanceAmt");
		sql.append(", BounceID, ReceiptID, PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS, WaivedCGST, WaivedSGST");
		sql.append(", WaivedUGST, WaivedIGST, WaivedCESS, FinSource, DueCreation");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, BounceCode, BounceCodeDesc");
			sql.append(", taxApplicable, taxComponent, dueCreation, linkedTranId, tdsReq ");
			sql.append(", WorkflowId");
		}
		sql.append(" from ManualAdvise");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("  Where FinReference = ? and AdviseType = ?");
		sql.append(" and (AdviseAmount - PaidAmount - WaivedAmount) > 0 Order By FeeTypeID desc");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
					ps.setInt(index++, adviseType);
				}
			}, new RowMapper<ManualAdvise>() {
				@Override
				public ManualAdvise mapRow(ResultSet rs, int rowNum) throws SQLException {
					ManualAdvise manualAdvise = new ManualAdvise();

					manualAdvise.setAdviseID(rs.getLong("AdviseID"));
					manualAdvise.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
					manualAdvise.setPaidAmount(rs.getBigDecimal("PaidAmount"));
					manualAdvise.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
					manualAdvise.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
					manualAdvise.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
					manualAdvise.setBounceID(rs.getLong("BounceID"));
					manualAdvise.setReceiptID(rs.getLong("ReceiptID"));
					manualAdvise.setPaidCGST(rs.getBigDecimal("PaidCGST"));
					manualAdvise.setPaidSGST(rs.getBigDecimal("PaidSGST"));
					manualAdvise.setPaidUGST(rs.getBigDecimal("PaidUGST"));
					manualAdvise.setPaidIGST(rs.getBigDecimal("PaidIGST"));
					manualAdvise.setPaidCESS(rs.getBigDecimal("PaidCESS"));
					manualAdvise.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
					manualAdvise.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
					manualAdvise.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
					manualAdvise.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
					manualAdvise.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
					manualAdvise.setFinSource(rs.getString("FinSource"));
					manualAdvise.setDueCreation(rs.getBoolean("DueCreation"));

					if (StringUtils.trimToEmpty(type).contains("View")) {
						manualAdvise.setFeeTypeCode(rs.getString("FeeTypeCode"));
						manualAdvise.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
						manualAdvise.setBounceCode(rs.getString("BounceCode"));
						manualAdvise.setBounceCodeDesc(rs.getString("BounceCodeDesc"));
						manualAdvise.setTaxApplicable(rs.getBoolean("taxApplicable"));
						manualAdvise.setTaxComponent(rs.getString("taxComponent"));
						manualAdvise.setDueCreation(rs.getBoolean("dueCreation"));
						manualAdvise.setLinkedTranId(rs.getLong("linkedTranId"));
						manualAdvise.setTdsReq(rs.getBoolean("tdsReq"));
						manualAdvise.setWorkflowId(rs.getInt("WorkflowId"));
					}

					return manualAdvise;
				}
			});

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	/**
	 * Method for updating Manual advise Payment Details
	 * 
	 * @param adviseID
	 * @param paidAmount
	 * @param waivedAmount
	 * @param tableType
	 */
	@Override
	public void updateAdvPayment(ManualAdvise ma, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append(" set PaidAmount = PaidAmount + ?, WaivedAmount = WaivedAmount + ?, ReservedAmt = ReservedAmt + ?");
		sql.append(", BalanceAmt = BalanceAmt + ?, PaidCGST = PaidCGST + ?, PaidSGST = PaidSGST + ?");
		sql.append(", PaidUGST = PaidUGST + ?, PaidIGST = PaidIGST + ?, PaidCESS = PaidCESS + ?");
		sql.append(", WaivedCGST = WaivedCGST + ?, WaivedSGST = WaivedSGST + ?, WaivedUGST = WaivedUGST + ?");
		sql.append(", WaivedIGST = WaivedIGST + ?, WaivedCESS = WaivedCESS + ?, TdsPaid = TdsPaid + ?");
		sql.append(" WHERE AdviseID = ?");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setBigDecimal(index++, ma.getPaidAmount());
			ps.setBigDecimal(index++, ma.getWaivedAmount());
			ps.setBigDecimal(index++, ma.getReservedAmt());
			ps.setBigDecimal(index++, ma.getBalanceAmt());
			ps.setBigDecimal(index++, ma.getPaidCGST());
			ps.setBigDecimal(index++, ma.getPaidSGST());
			ps.setBigDecimal(index++, ma.getPaidUGST());
			ps.setBigDecimal(index++, ma.getPaidIGST());
			ps.setBigDecimal(index++, ma.getPaidCESS());
			ps.setBigDecimal(index++, ma.getWaivedCGST());
			ps.setBigDecimal(index++, ma.getWaivedSGST());
			ps.setBigDecimal(index++, ma.getWaivedUGST());
			ps.setBigDecimal(index++, ma.getWaivedIGST());
			ps.setBigDecimal(index++, ma.getWaivedCESS());
			ps.setBigDecimal(index++, ma.getTdsPaid());
			ps.setLong(index++, ma.getAdviseID());
		});
	}

	@Override
	public void saveMovement(ManualAdviseMovements mam, String type) {
		if (mam.getMovementID() <= 0) {
			mam.setMovementID(getNextValue("SeqManualAdviseMovements"));
		}

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" ManualAdviseMovements").append(StringUtils.trimToEmpty(type));
		sql.append(" (MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount, Status");
		sql.append(", ReceiptID, ReceiptSeqID, WaiverID, TaxHeaderId, TdsPaid");
		sql.append(", PaidCGST, PaidSGST, PaidIGST, PaidCESS, WaivedCGST, WaivedSGST, WaivedIGST, WaivedCESS");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, JdbcUtil.setLong(mam.getMovementID()));
			ps.setLong(index++, JdbcUtil.setLong(mam.getAdviseID()));
			ps.setDate(index++, JdbcUtil.getDate(mam.getMovementDate()));
			ps.setBigDecimal(index++, mam.getMovementAmount());
			ps.setBigDecimal(index++, mam.getPaidAmount());
			ps.setBigDecimal(index++, mam.getWaivedAmount());
			ps.setString(index++, mam.getStatus());
			ps.setLong(index++, JdbcUtil.setLong(mam.getReceiptID()));
			ps.setLong(index++, JdbcUtil.setLong(mam.getReceiptSeqID()));
			ps.setLong(index++, JdbcUtil.setLong(mam.getWaiverID()));
			ps.setLong(index++, JdbcUtil.setLong(mam.getTaxHeaderId()));
			ps.setBigDecimal(index++, mam.getTdsPaid());
			ps.setBigDecimal(index++, mam.getPaidCGST());
			ps.setBigDecimal(index++, mam.getPaidSGST());
			ps.setBigDecimal(index++, mam.getPaidIGST());
			ps.setBigDecimal(index++, mam.getPaidCESS());
			ps.setBigDecimal(index++, mam.getWaivedCGST());
			ps.setBigDecimal(index++, mam.getWaivedSGST());
			ps.setBigDecimal(index++, mam.getWaivedIGST());
			ps.setBigDecimal(index++, mam.getWaivedCESS());
		});
	}

	@Override
	public List<ManualAdviseMovements> getAdviseMovementsByReceipt(long receiptID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount");
		sql.append(", Status, ReceiptID, ReceiptSeqID, TaxHeaderId");
		sql.append(" From ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReceiptID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, receiptID);
				}
			}, new RowMapper<ManualAdviseMovements>() {
				@Override
				public ManualAdviseMovements mapRow(ResultSet rs, int rowNum) throws SQLException {
					ManualAdviseMovements manualAdviseMovements = new ManualAdviseMovements();

					manualAdviseMovements.setMovementID(rs.getLong("MovementID"));
					manualAdviseMovements.setAdviseID(rs.getLong("AdviseID"));
					manualAdviseMovements.setMovementDate(rs.getTimestamp("MovementDate"));
					manualAdviseMovements.setMovementAmount(rs.getBigDecimal("MovementAmount"));
					manualAdviseMovements.setPaidAmount(rs.getBigDecimal("PaidAmount"));
					manualAdviseMovements.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
					manualAdviseMovements.setStatus(rs.getString("Status"));
					manualAdviseMovements.setReceiptID(rs.getLong("ReceiptID"));
					manualAdviseMovements.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
					manualAdviseMovements.setTaxHeaderId(JdbcUtil.getLong(rs.getLong("TaxHeaderId")));

					return manualAdviseMovements;
				}
			});

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<ManualAdviseMovements> getAdviseMovements(long id) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.MovementID, T1.MovementDate, T1.MovementAmount");
		sql.append(", T1.PaidAmount, T1.WaivedAmount, T1.Status, T2.ReceiptMode, T1.TaxHeaderId");
		sql.append(", T1.PaidCGST, T1.PaidSGST, T1.PaidUGST, T1.PaidIGST, T1.PaidCESS");
		sql.append(", T1.WaivedCGST, T1.WaivedSGST, T1.WaivedUGST, T1.WaivedIGST, T1.WaivedCESS");
		sql.append(" from ManualAdviseMovements");
		sql.append(" T1 LEFT OUTER JOIN FinReceiptHeader T2 ON T1.ReceiptID = T2.ReceiptID");
		sql.append(" Where AdviseID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, id);
				}
			}, new RowMapper<ManualAdviseMovements>() {
				@Override
				public ManualAdviseMovements mapRow(ResultSet rs, int rowNum) throws SQLException {
					ManualAdviseMovements mam = new ManualAdviseMovements();

					mam.setMovementID(rs.getLong("MovementID"));
					mam.setMovementDate(rs.getTimestamp("MovementDate"));
					mam.setMovementAmount(rs.getBigDecimal("MovementAmount"));
					mam.setPaidAmount(rs.getBigDecimal("PaidAmount"));
					mam.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
					mam.setStatus(rs.getString("Status"));
					mam.setReceiptMode(rs.getString("ReceiptMode"));
					mam.setTaxHeaderId(rs.getLong("TaxHeaderId"));
					mam.setPaidCGST(rs.getBigDecimal("PaidCGST"));
					mam.setPaidSGST(rs.getBigDecimal("PaidSGST"));
					mam.setPaidUGST(rs.getBigDecimal("PaidUGST"));
					mam.setPaidIGST(rs.getBigDecimal("PaidIGST"));
					mam.setPaidCESS(rs.getBigDecimal("PaidCESS"));
					mam.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
					mam.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
					mam.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
					mam.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
					mam.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));

					return mam;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void deleteMovementsByReceiptID(long receiptID, String type) {
		logger.debug(Literal.ENTERING);

		ManualAdviseMovements movements = new ManualAdviseMovements();
		movements.setReceiptID(receiptID);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where ReceiptID = :ReceiptID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(movements);
		jdbcTemplate.update(sql.toString(), paramSource);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<ManualAdviseMovements> getAdvMovementsByReceiptSeq(long receiptID, long receiptSeqID, int adviseType,
			String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount");
		sql.append(", Status, ReceiptID, ReceiptSeqID, TaxHeaderId");

		if (StringUtils.contains(type, "View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxApplicable, TaxComponent");
		}

		sql.append(" from ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReceiptID = ? and ReceiptSeqID = ?");

		if (StringUtils.contains(type, "View")) {
			sql.append(" AND AdviseType = ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, receiptID);
					ps.setLong(index++, receiptSeqID);

					if (StringUtils.contains(type, "View")) {
						ps.setInt(index++, adviseType);
					}

				}
			}, new RowMapper<ManualAdviseMovements>() {
				@Override
				public ManualAdviseMovements mapRow(ResultSet rs, int rowNum) throws SQLException {
					ManualAdviseMovements ManualAdviseMovements = new ManualAdviseMovements();

					ManualAdviseMovements.setMovementID(rs.getLong("MovementID"));
					ManualAdviseMovements.setAdviseID(rs.getLong("AdviseID"));
					ManualAdviseMovements.setMovementDate(rs.getTimestamp("MovementDate"));
					ManualAdviseMovements.setMovementAmount(rs.getBigDecimal("MovementAmount"));
					ManualAdviseMovements.setPaidAmount(rs.getBigDecimal("PaidAmount"));
					ManualAdviseMovements.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
					ManualAdviseMovements.setStatus(rs.getString("Status"));
					ManualAdviseMovements.setReceiptID(rs.getLong("ReceiptID"));
					ManualAdviseMovements.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
					ManualAdviseMovements.setTaxHeaderId(JdbcUtil.getLong(rs.getLong("TaxHeaderId")));

					if (StringUtils.contains(type, "View")) {
						ManualAdviseMovements.setFeeTypeCode(rs.getString("FeeTypeCode"));
						ManualAdviseMovements.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
						ManualAdviseMovements.setTaxApplicable(rs.getBoolean("TaxApplicable"));
						ManualAdviseMovements.setTaxComponent(rs.getString("TaxComponent"));
					}

					return ManualAdviseMovements;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void updateMovementStatus(long receiptID, long receiptSeqID, String status, String type) {
		logger.debug(Literal.ENTERING);

		ManualAdviseMovements movements = new ManualAdviseMovements();
		movements.setReceiptID(receiptID);
		movements.setReceiptSeqID(receiptSeqID);
		movements.setStatus(status);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" set Status = :Status ");
		sql.append(" Where ReceiptID = :ReceiptID AND ReceiptSeqID=:ReceiptSeqID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(movements);
		jdbcTemplate.update(sql.toString(), paramSource);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Fetch the Reserved Payable Amounts Log details
	 */
	@Override
	public List<ManualAdviseReserve> getPayableReserveList(long receiptSeqID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptSeqID, AdviseID, ReservedAmt");
		sql.append(" from ManualAdviseReserve");
		sql.append(" Where ReceiptSeqID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, receiptSeqID);
				}
			}, new RowMapper<ManualAdviseReserve>() {
				@Override
				public ManualAdviseReserve mapRow(ResultSet rs, int rowNum) throws SQLException {
					ManualAdviseReserve manualAdviseReserve = new ManualAdviseReserve();

					manualAdviseReserve.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
					manualAdviseReserve.setAdviseID(rs.getLong("AdviseID"));
					manualAdviseReserve.setReservedAmt(rs.getBigDecimal("ReservedAmt"));

					return manualAdviseReserve;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	/**
	 * Method for Fetch the Reserved Payable Amounts Log details
	 */
	@Override
	public ManualAdviseReserve getPayableReserve(long receiptSeqID, long payAgainstID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptSeqID, AdviseID, ReservedAmt");
		sql.append(" from ManualAdviseReserve");
		sql.append(" Where ReceiptSeqID = ? and AdviseID= ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { receiptSeqID, payAgainstID },
					new RowMapper<ManualAdviseReserve>() {
						@Override
						public ManualAdviseReserve mapRow(ResultSet rs, int rowNum) throws SQLException {
							ManualAdviseReserve manualAdviseReserve = new ManualAdviseReserve();

							manualAdviseReserve.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
							manualAdviseReserve.setAdviseID(rs.getLong("AdviseID"));
							manualAdviseReserve.setReservedAmt(rs.getBigDecimal("ReservedAmt"));

							return manualAdviseReserve;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;

	}

	/**
	 * Method for Save Reserved amount against Advise ID
	 */
	@Override
	public void savePayableReserveLog(long receiptSeqID, long payAgainstID, BigDecimal reserveAmt) {
		logger.debug("Entering");

		ManualAdviseReserve reserve = new ManualAdviseReserve();
		reserve.setReceiptSeqID(receiptSeqID);
		reserve.setAdviseID(payAgainstID);
		reserve.setReservedAmt(reserveAmt);

		StringBuilder insertSql = new StringBuilder("Insert Into ManualAdviseReserve");
		insertSql.append(" (AdviseID, ReceiptSeqID, ReservedAmt )");
		insertSql.append(" Values(:AdviseID, :ReceiptSeqID, :ReservedAmt)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reserve);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for updating Reserved excess Amount after modifications
	 */
	@Override
	public void updatePayableReserveLog(long receiptID, long payAgainstID, BigDecimal diffInReserve) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptSeqID", receiptID);
		source.addValue("AdviseID", payAgainstID);
		source.addValue("PaidNow", diffInReserve);

		StringBuilder updateSql = new StringBuilder("Update ManualAdviseReserve ");
		updateSql.append(" Set ReservedAmt = ReservedAmt + :PaidNow ");
		updateSql.append(" Where ReceiptSeqID =:ReceiptSeqID AND AdviseID =:AdviseID ");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.jdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Deleting Reserved Amounts against Advise ID Processed for Utilization
	 */
	@Override
	public void deletePayableReserve(long receiptID, long payAgainstID) {
		StringBuilder sql = new StringBuilder("Delete From ManualAdviseReserve ");
		sql.append(" Where ReceiptSeqID = ?");
		if (payAgainstID != 0) {
			sql.append(" and AdviseID = ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, receiptID);
			if (payAgainstID != 0) {
				ps.setLong(index++, payAgainstID);
			}
		});
	}

	/**
	 * Method for updating Reserved amount against Advise ID
	 */
	@Override
	public void updatePayableReserve(long payAgainstID, BigDecimal reserveAmt) {
		StringBuilder sql = new StringBuilder("Update ManualAdvise ");
		sql.append(" Set ReservedAmt = ReservedAmt + ?, BalanceAmt = BalanceAmt - ?");
		sql.append(" Where AdviseID = ? and BalanceAmt >= ?");

		logger.trace(Literal.SQL + sql.toString());
		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setBigDecimal(index++, reserveAmt);
			ps.setBigDecimal(index++, reserveAmt);
			ps.setLong(index++, payAgainstID);
			ps.setBigDecimal(index++, reserveAmt);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updatePayableReserveAmount(long payAgainstID, BigDecimal reserveAmt) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AdviseID", payAgainstID);
		source.addValue("PaidNow", reserveAmt);

		StringBuilder updateSql = new StringBuilder("Update ManualAdvise ");
		updateSql.append(" Set ReservedAmt = ReservedAmt + :PaidNow, BalanceAmt = BalanceAmt - :PaidNow ");
		updateSql.append(" Where AdviseID =:AdviseID ");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.jdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Update utilization amount after amounts Approval
	 */
	@Override
	public void updateUtilise(long adviseID, BigDecimal amount, boolean noManualReserve) {
		StringBuilder sql = new StringBuilder("Update ManualAdvise");
		sql.append(" Set PaidAmount = PaidAmount + ?");

		if (!noManualReserve) {
			sql.append(", ReservedAmt = ReservedAmt - ?");
		}
		sql.append(" Where AdviseID = ? and ReservedAmt >= ?");

		logger.trace(Literal.SQL + sql.toString());
		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			if (!noManualReserve) {
				ps.setBigDecimal(index++, amount);
			}

			ps.setLong(index++, adviseID);
			ps.setBigDecimal(index, amount);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	/**
	 * Method for Update utilization amount after amounts Reversal
	 */
	@Override
	public void reverseUtilise(long adviseID, BigDecimal amount) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AdviseID", adviseID);
		source.addValue("PaidNow", amount);
		source.addValue("HoldDue", false);

		StringBuilder updateSql = new StringBuilder("Update ManualAdvise");
		updateSql.append(" Set PaidAmount = PaidAmount - :PaidNow, BalanceAmt = :PaidNow, HoldDue = :HoldDue ");
		updateSql.append(" Where AdviseID =:AdviseID");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.jdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public Date getPresentmentBounceDueDate(long receiptId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptId", receiptId);

		StringBuilder selectSql = new StringBuilder("SELECT T5.schDate	FROM MANUALADVISE M ");
		selectSql.append(
				" INNER Join PRESENTMENTDETAILS T4 on T4.RECEIPTID = M.RECEIPTID and M.RECEIPTID !=0 and T4.RECEIPTID !=0 ");
		selectSql.append(
				" INNER Join FINSCHEDULEDETAILS T5 on T4.FInreference = T5.FInreference and T4.schdate = T5.schdate ");
		selectSql.append(
				" where M.AdviseType <> 2 and	m.ADVISEAMOUNT > 0 and FEETYPEID not in (Select FEETYPEID from FEETYPES) ");
		selectSql.append(" AND M.ReceiptId =:ReceiptId ");

		logger.debug("selectSql: " + selectSql.toString());
		Date schDate = null;
		try {
			schDate = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Date.class);
		} catch (EmptyResultDataAccessException ede) {
			logger.warn("Warning:", ede);
		}
		logger.debug("Leaving");

		return schDate;
	}

	/**
	 * Method for Fetch All Bounce ID List using Reference
	 */
	@Override
	public List<Long> getBounceAdvisesListByRef(String finReference, int adviseType, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" Select AdviseId ");
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where FinReference = :FinReference AND AdviseType =:AdviseType AND BounceId > 0 ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setFinReference(finReference);
		manualAdvise.setAdviseType(adviseType);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);

		List<Long> bounceIDList = jdbcTemplate.queryForList(sql.toString(), paramSource, Long.class);
		logger.debug(Literal.LEAVING);
		return bounceIDList;
	}

	@Override
	public FinanceMain getFinanceDetails(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FM.FinReference, FT.FinType, FT.FINTYPEDESC LovDescFinTypeName, FM.FinBranch");
		sql.append(", FM.CustId, Cust.CUSTCIF LovDescCustCif, Cust.CUSTSHRTNAME LovDescCustShrtName,  SD.EntityCode");
		sql.append(", FM.FinAssetValue,FM.FINSTARTDATE, FM.MATURITYDATE,FM.FinCcy, FM.TDSApplicable");
		sql.append(" from FINANCEMAIN FM");
		sql.append(" INNER JOIN Customers Cust on FM.CUSTID=Cust.CUSTID");
		sql.append(" INNER JOIN RMTFINANCETYPES FT ON FT.FINTYPE = FM.FINTYPE");
		sql.append(" INNER JOIN  SMTDivisiondetail SD On FT.FINDIVISION = SD.DivisionCode");
		sql.append(" Where FM.FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, (rs, rowNum) -> {
				FinanceMain financeMain = new FinanceMain();

				financeMain.setFinReference(rs.getString("FinReference"));
				financeMain.setFinType(rs.getString("FinType"));
				financeMain.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
				financeMain.setFinBranch(rs.getString("FinBranch"));
				financeMain.setCustID(rs.getLong("CustID"));
				financeMain.setLovDescCustCIF(rs.getString("LovDescCustCIF"));
				financeMain.setLovDescCustShrtName(rs.getString("LovDescCustShrtName"));
				financeMain.setEntityCode(rs.getString("EntityCode"));
				financeMain.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
				financeMain.setFinStartDate(rs.getTimestamp("FinStartDate"));
				financeMain.setMaturityDate(rs.getTimestamp("MaturityDate"));
				financeMain.setFinCcy(rs.getString("FinCcy"));
				financeMain.setTDSApplicable(rs.getBoolean("TDSApplicable"));

				return financeMain;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in financemain for the specified finReference>>{}", finReference);
		}

		return null;
	}

	/**
	 * 
	 * @param reference
	 * @param type
	 * @return
	 */
	public List<ManualAdvise> getAMZManualAdviseDetails(String finRef, String type) {
		logger.debug("Entering");

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setFinReference(finRef);

		StringBuilder selectSql = new StringBuilder(" SELECT ");
		selectSql.append(
				" T1.AdviseID, T1.AdviseType, T1.FinReference, T1.FeeTypeID, T1.Sequence, T1.AdviseAmount, T1.BounceID, T1.ReceiptID,");
		selectSql.append(
				" T1.PaidAmount, T1.WaivedAmount, T1.ValueDate, T1.PostDate, T1.ReservedAmt, T1.BalanceAmt,T1.PaidCGST, T1.PaidSGST, T1.PaidUGST, T1.PaidIGST, T1.PaidCESS, T1.FinSource,");
		selectSql.append(" T1.WaivedCGST, T1.WaivedSGST, T1.WaivedUGST, T1.WaivedIGST, T1.WaivedCESS, T1.DueCreation ");
		selectSql.append(" From ManualAdvise T1 ");
		selectSql.append(" INNER JOIN FeeTypes T2 ON T1.FeeTypeID = T2.FeeTypeID AND T2.AmortzReq = 1");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where T1.AdviseType = 1 AND T1.FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(manualAdvise);
		RowMapper<ManualAdvise> typeRowMapper = BeanPropertyRowMapper.newInstance(ManualAdvise.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public BigDecimal getBalanceAmt(String finReference) {
		logger.debug("Entering");
		BigDecimal balance = BigDecimal.ZERO;
		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setFinReference(finReference);
		manualAdvise.setAdviseType(1);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT sum(adviseamount-paidamount-waivedamount) from manualAdvise");
		selectSql.append(" Where FinReference =:FinReference and AdviseType =:AdviseType");

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);

		balance = this.jdbcTemplate.queryForObject(selectSql.toString(), paramSource, BigDecimal.class);
		logger.debug("Leaving");
		return balance;

	}

	@Override
	public String getTaxComponent(long adviseID, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT TaxComponent ");
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where adviseID = :adviseID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(adviseID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		String taxComponent = null;
		try {
			taxComponent = jdbcTemplate.queryForObject(sql.toString(), paramSource, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			taxComponent = null;
		}

		logger.debug(Literal.LEAVING);
		return taxComponent;
	}

	// MIGRATION PURPOSE
	@Override
	public List<ManualAdvise> getManualAdvisesByFinRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseType, FinReference, FeeTypeID, Sequence, AdviseAmount, BounceID");
		sql.append(", ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS, WaivedCGST, WaivedSGST");
		sql.append(", WaivedUGST, WaivedIGST, WaivedCESS, DueCreation");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, BounceCode, BounceCodeDesc, taxApplicable, taxComponent, tdsReq");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from ManualAdvise");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, finReference);
		}, (rs, rowNum) -> {
			ManualAdvise manualAdvise = new ManualAdvise();

			manualAdvise.setAdviseID(rs.getLong("AdviseID"));
			manualAdvise.setAdviseType(rs.getInt("AdviseType"));
			manualAdvise.setFinReference(rs.getString("FinReference"));
			manualAdvise.setFeeTypeID(rs.getLong("FeeTypeID"));
			manualAdvise.setSequence(rs.getInt("Sequence"));
			manualAdvise.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			manualAdvise.setBounceID(rs.getLong("BounceID"));
			manualAdvise.setReceiptID(rs.getLong("ReceiptID"));
			manualAdvise.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			manualAdvise.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			manualAdvise.setRemarks(rs.getString("Remarks"));
			manualAdvise.setValueDate(rs.getTimestamp("ValueDate"));
			manualAdvise.setPostDate(rs.getTimestamp("PostDate"));
			manualAdvise.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			manualAdvise.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			manualAdvise.setPaidCGST(rs.getBigDecimal("PaidCGST"));
			manualAdvise.setPaidSGST(rs.getBigDecimal("PaidSGST"));
			manualAdvise.setPaidUGST(rs.getBigDecimal("PaidUGST"));
			manualAdvise.setPaidIGST(rs.getBigDecimal("PaidIGST"));
			manualAdvise.setPaidCESS(rs.getBigDecimal("PaidCESS"));
			manualAdvise.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
			manualAdvise.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
			manualAdvise.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
			manualAdvise.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
			manualAdvise.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
			manualAdvise.setDueCreation(rs.getBoolean("DueCreation"));

			if (type.contains("View")) {
				manualAdvise.setFeeTypeCode(rs.getString("FeeTypeCode"));
				manualAdvise.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				manualAdvise.setBounceCode(rs.getString("BounceCode"));
				manualAdvise.setBounceCodeDesc(rs.getString("BounceCodeDesc"));
				manualAdvise.setTaxApplicable(rs.getBoolean("taxApplicable"));
				manualAdvise.setTaxComponent(rs.getString("taxComponent"));
				manualAdvise.setTdsReq(rs.getBoolean("tdsReq"));
			}

			manualAdvise.setVersion(rs.getInt("Version"));
			manualAdvise.setLastMntOn(rs.getTimestamp("LastMntOn"));
			manualAdvise.setLastMntBy(rs.getLong("LastMntBy"));
			manualAdvise.setRecordStatus(rs.getString("RecordStatus"));
			manualAdvise.setRoleCode(rs.getString("RoleCode"));
			manualAdvise.setNextRoleCode(rs.getString("NextRoleCode"));
			manualAdvise.setTaskId(rs.getString("TaskId"));
			manualAdvise.setNextTaskId(rs.getString("NextTaskId"));
			manualAdvise.setRecordType(rs.getString("RecordType"));
			manualAdvise.setWorkflowId(rs.getLong("WorkflowId"));

			return manualAdvise;
		});

	}

	@Override
	public List<ManualAdviseMovements> getDMAdviseMovementsByFinRef(String finReference, String type) {

		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;

		StringBuilder selectSql = new StringBuilder(
				" Select T2.MovementID, T2.AdviseID, T2.MovementDate, T2.MovementAmount, T2.PaidAmount, ");
		selectSql.append(
				" T2.WaivedAmount, T2.Status, T2.ReceiptID, T2.ReceiptSeqID, T2.PaidCGST, T2.PaidSGST, T2.PaidUGST, T2.PaidIGST, T2.PaidCESS ");
		selectSql.append(" T2.WaivedCGST, T2.WaivedSGST, T2.WaivedUGST, T2.WaivedIGST, T2.WaivedCESS, T2.TaxHeaderId");
		selectSql.append(" From ManualAdvise");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" T1");
		selectSql.append(" Inner Join ManualAdviseMovements");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" T2 on T1.AdviseId = T2.AdviseId");
		selectSql.append(" Where FinReference = :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		try {
			RowMapper<ManualAdviseMovements> typeRowMapper = BeanPropertyRowMapper
					.newInstance(ManualAdviseMovements.class);
			return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			return null;
		} finally {
			source = null;
			logger.debug(Literal.LEAVING);
		}

	}

	@Override
	public List<ManualAdvise> getManualAdvise(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("AdviseType", FinanceConstants.MANUAL_ADVISE_RECEIVABLE);

		sql = new StringBuilder();
		sql.append(
				" Select MA.adviseID, MA.AdviseType, MA.FeeTypeID, MA.Sequence, MA.finReference, (MA.AdviseAmount - MA.PaidAmount - MA.WaivedAmount) balanceAmt,");
		sql.append(
				" MA.adviseAmount, MA.PaidAmount, MA.WaivedAmount, MA.ValueDate, MA.PostDate, MA.BounceID, MA.ReceiptID, MA.ReservedAmt,");
		sql.append(" MA.paidsgst, MA.paidugst, MA.paidigst, MA.paidcgst, MA.paidcess,");
		sql.append(
				" MA.Version, MA.LastMntOn, MA.LastMntBy,MA.RecordStatus, MA.RoleCode, MA.NextRoleCode, MA.TaskId, MA.NextTaskId, MA.RecordType, MA.WorkflowId,");
		sql.append(" FT.feetypecode, FT.FeeTypeDesc, coalesce(FT.TaxApplicable, 0) TaxApplicable, FT.TaxComponent,");
		sql.append(" MA.WaivedCGST, MA.WaivedSGST, MA.WaivedUGST, MA.WaivedIGST, MA.DUECREATION");
		sql.append(", MA.PaidCGST, MA.PaidSGST, MA.PaidIGST, MA.PaidUGST");
		sql.append(", MA.Remarks, MA.PaidCGST, MA.PaidSGST, MA.PaidUGST, MA.PaidIGST,");
		sql.append(" MA.PaidCESS, MA.FinSource,  MA.WaivedCESS, MA.LinkedTranId");
		sql.append(" From MANUALADVISE_Aview  MA");
		sql.append(" Left join FEETYPES FT on MA.FEETYPEID = FT.FEETYPEID");
		sql.append(" Where FinReference = :FinReference");
		sql.append(" ORDER by MA.adviseID");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<ManualAdvise> rowMapper = BeanPropertyRowMapper.newInstance(ManualAdvise.class);
		try {
			return jdbcTemplate.query(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			sql = null;
		}

		logger.debug(Literal.LEAVING);

		return null;
	}

	/**
	 * Method for updating Manual advise Payment Details
	 * 
	 * @param adviseID
	 * @param paidAmount
	 * @param waivedAmount
	 * @param tableType
	 */
	@Override
	public void updateWaivedAmount(ManualAdvise advise, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("update ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append(" set BalanceAmt = :BalanceAmt ,WaivedAmount=:WaivedAmount");
		sql.append(" WHERE AdviseID = :AdviseID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(advise);
		this.jdbcTemplate.update(sql.toString(), paramSource);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Get manual advise list by feetype code
	 * 
	 * @param finReference
	 * @param feeTypeCode
	 * @param type
	 * 
	 *            Ticket id:12499
	 */
	@Override
	public List<ManualAdvise> getManualAdviseByRef(String finReference, String feeTypeCode, String type) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseAmount, PaidAmount, WaivedAmount, ReservedAmt, BalanceAmt");
		sql.append(", BounceID, ReceiptID, DueCreation");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, BounceCode, BounceCodeDesc");
		}

		sql.append(" from ManualAdvise");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ? AND FeeTypeCode = ?");
		sql.append(" order by AdviseID");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
					ps.setString(index++, feeTypeCode);
				}
			}, new RowMapper<ManualAdvise>() {
				@Override
				public ManualAdvise mapRow(ResultSet rs, int rowNum) throws SQLException {
					ManualAdvise manualAdvise = new ManualAdvise();

					manualAdvise.setAdviseID(rs.getLong("AdviseID"));
					manualAdvise.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
					manualAdvise.setPaidAmount(rs.getBigDecimal("PaidAmount"));
					manualAdvise.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
					manualAdvise.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
					manualAdvise.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
					manualAdvise.setBounceID(rs.getLong("BounceID"));
					manualAdvise.setReceiptID(rs.getLong("ReceiptID"));
					manualAdvise.setDueCreation(rs.getBoolean("DueCreation"));

					if (StringUtils.trimToEmpty(type).contains("View")) {
						manualAdvise.setFeeTypeCode(rs.getString("FeeTypeCode"));
						manualAdvise.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
						manualAdvise.setBounceCode(rs.getString("BounceCode"));
						manualAdvise.setBounceCodeDesc(rs.getString("BounceCodeDesc"));
					}

					return manualAdvise;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	@Override
	public List<ManualAdvise> getManualAdviseByRefAndFeeId(int adviseType, long feeTypeID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseType, finReference, feeTypeID, sequence, adviseAmount, BounceID");
		sql.append(", ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, DueCreation");
		sql.append(" from ManualAdvise");
		sql.append(" Where  AdviseType = ? And FeeTypeID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setInt(index++, adviseType);
					ps.setLong(index++, feeTypeID);
				}
			}, new RowMapper<ManualAdvise>() {
				@Override
				public ManualAdvise mapRow(ResultSet rs, int rowNum) throws SQLException {
					ManualAdvise manualAdvise = new ManualAdvise();

					manualAdvise.setAdviseID(rs.getLong("AdviseID"));
					manualAdvise.setAdviseType(rs.getInt("AdviseType"));
					manualAdvise.setFinReference(rs.getString("finReference"));
					manualAdvise.setFeeTypeID(rs.getLong("feeTypeID"));
					manualAdvise.setSequence(rs.getInt("sequence"));
					manualAdvise.setAdviseAmount(rs.getBigDecimal("adviseAmount"));
					manualAdvise.setBounceID(rs.getLong("BounceID"));
					manualAdvise.setReceiptID(rs.getLong("ReceiptID"));
					manualAdvise.setPaidAmount(rs.getBigDecimal("PaidAmount"));
					manualAdvise.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
					manualAdvise.setRemarks(rs.getString("Remarks"));
					manualAdvise.setValueDate(rs.getTimestamp("ValueDate"));
					manualAdvise.setPostDate(rs.getTimestamp("PostDate"));
					manualAdvise.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
					manualAdvise.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
					manualAdvise.setVersion(rs.getInt("Version"));
					manualAdvise.setLastMntOn(rs.getTimestamp("LastMntOn"));
					manualAdvise.setLastMntBy(rs.getLong("LastMntBy"));
					manualAdvise.setRecordStatus(rs.getString("RecordStatus"));
					manualAdvise.setRoleCode(rs.getString("RoleCode"));
					manualAdvise.setNextRoleCode(rs.getString("NextRoleCode"));
					manualAdvise.setTaskId(rs.getString("TaskId"));
					manualAdvise.setNextTaskId(rs.getString("NextTaskId"));
					manualAdvise.setRecordType(rs.getString("RecordType"));
					manualAdvise.setWorkflowId(rs.getLong("WorkflowId"));
					manualAdvise.setDueCreation(rs.getBoolean("DueCreation"));

					return manualAdvise;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	/**
	 * Method for Update Paid Amount Only after amounts Approval
	 */
	@Override
	public void updatePaidAmountOnly(long adviseID, BigDecimal amount) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AdviseID", adviseID);
		source.addValue("PaidNow", amount);

		StringBuilder updateSql = new StringBuilder("Update ManualAdvise");
		updateSql.append(" Set PaidAmount = PaidAmount + :PaidNow, BalanceAmt = BalanceAmt - :PaidNow ");
		updateSql.append(" Where AdviseID = :AdviseID");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.jdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<ManualAdvise> getManualAdviseByRefAndFeeCode(String finReference, int adviseType, String feeTypeCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseType, finReference, feeTypeID, sequence, adviseAmount, BounceID");
		sql.append(", ReceiptID, PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt");
		sql.append(", BalanceAmt, FeeTypeCode, FeeTypeDesc, Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, DueCreation");
		sql.append(" From ManualAdvise_Aview");
		sql.append(" Where FinReference = ? and AdviseType = ? And FeeTypeCode = ?");
		sql.append(" Order By ValueDate");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
					ps.setInt(index++, adviseType);
					ps.setString(index++, feeTypeCode);
				}
			}, new RowMapper<ManualAdvise>() {
				@Override
				public ManualAdvise mapRow(ResultSet rs, int rowNum) throws SQLException {
					ManualAdvise manualAdvise = new ManualAdvise();

					manualAdvise.setAdviseID(rs.getLong("AdviseID"));
					manualAdvise.setAdviseType(rs.getInt("AdviseType"));
					manualAdvise.setFinReference(rs.getString("finReference"));
					manualAdvise.setFeeTypeID(rs.getLong("feeTypeID"));
					manualAdvise.setSequence(rs.getInt("sequence"));
					manualAdvise.setAdviseAmount(rs.getBigDecimal("adviseAmount"));
					manualAdvise.setBounceID(rs.getLong("BounceID"));
					manualAdvise.setReceiptID(rs.getLong("ReceiptID"));
					manualAdvise.setPaidAmount(rs.getBigDecimal("PaidAmount"));
					manualAdvise.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
					manualAdvise.setRemarks(rs.getString("Remarks"));
					manualAdvise.setValueDate(rs.getTimestamp("ValueDate"));
					manualAdvise.setPostDate(rs.getTimestamp("PostDate"));
					manualAdvise.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
					manualAdvise.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
					manualAdvise.setFeeTypeCode(rs.getString("FeeTypeCode"));
					manualAdvise.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
					manualAdvise.setVersion(rs.getInt("Version"));
					manualAdvise.setLastMntOn(rs.getTimestamp("LastMntOn"));
					manualAdvise.setLastMntBy(rs.getLong("LastMntBy"));
					manualAdvise.setRecordStatus(rs.getString("RecordStatus"));
					manualAdvise.setRoleCode(rs.getString("RoleCode"));
					manualAdvise.setNextRoleCode(rs.getString("NextRoleCode"));
					manualAdvise.setTaskId(rs.getString("TaskId"));
					manualAdvise.setNextTaskId(rs.getString("NextTaskId"));
					manualAdvise.setRecordType(rs.getString("RecordType"));
					manualAdvise.setWorkflowId(rs.getLong("WorkflowId"));
					manualAdvise.setDueCreation(rs.getBoolean("DueCreation"));

					return manualAdvise;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	@Override
	public List<ManualAdvise> getManualAdviseByRef(String finReference, int adviseType, String type, Date valuDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, AdviseAmount, PaidAmount, WaivedAmount, ReservedAmt, BalanceAmt");
		sql.append(", BounceID, ReceiptID, PaidCGST, PaidSGST, PaidUGST, PaidIGST, PaidCESS, WaivedCGST, WaivedSGST");
		sql.append(", WaivedUGST, WaivedIGST, WaivedCESS, DueCreation");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, BounceCode, BounceCodeDesc, taxApplicable, taxComponent, tdsReq ");
		}

		sql.append(" from ManualAdvise");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ? and AdviseType = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
					ps.setInt(index++, adviseType);
				}
			}, new RowMapper<ManualAdvise>() {
				@Override
				public ManualAdvise mapRow(ResultSet rs, int rowNum) throws SQLException {
					ManualAdvise manualAdvise = new ManualAdvise();

					manualAdvise.setAdviseID(rs.getLong("AdviseID"));
					manualAdvise.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
					manualAdvise.setPaidAmount(rs.getBigDecimal("PaidAmount"));
					manualAdvise.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
					manualAdvise.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
					manualAdvise.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
					manualAdvise.setBounceID(rs.getLong("BounceID"));
					manualAdvise.setReceiptID(rs.getLong("ReceiptID"));
					manualAdvise.setPaidCGST(rs.getBigDecimal("PaidCGST"));
					manualAdvise.setPaidSGST(rs.getBigDecimal("PaidSGST"));
					manualAdvise.setPaidUGST(rs.getBigDecimal("PaidUGST"));
					manualAdvise.setPaidIGST(rs.getBigDecimal("PaidIGST"));
					manualAdvise.setPaidCESS(rs.getBigDecimal("PaidCESS"));
					manualAdvise.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
					manualAdvise.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
					manualAdvise.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
					manualAdvise.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
					manualAdvise.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
					manualAdvise.setDueCreation(rs.getBoolean("DueCreation"));

					if (StringUtils.trimToEmpty(type).contains("View")) {
						manualAdvise.setFeeTypeCode(rs.getString("FeeTypeCode"));
						manualAdvise.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
						manualAdvise.setBounceCode(rs.getString("BounceCode"));
						manualAdvise.setBounceCodeDesc(rs.getString("BounceCodeDesc"));
						manualAdvise.setTaxApplicable(rs.getBoolean("taxApplicable"));
						manualAdvise.setTaxComponent(rs.getString("taxComponent"));
						manualAdvise.setTdsReq(rs.getBoolean("tdsReq"));
					}

					return manualAdvise;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public Date getManualAdviseDate(String finReference, Date valueDate, String type, int adviseType) {
		StringBuilder selectSql = new StringBuilder();
		Date manAdvDate = null;

		selectSql.append("select MAX(ValueDate) from MANUALADVISE ");
		selectSql.append(type);
		selectSql.append(" Where  FinReference = :FinReference AND AdviseType =:AdviseType and ValueDate>:ValueDate  ");
		selectSql.append(" AND (AdviseAmount - PaidAmount - WaivedAmount) > 0");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("FinReference", finReference);
		paramMap.addValue("ValueDate", new SimpleDateFormat("dd-MMM-yy").format(valueDate));
		paramMap.addValue("AdviseType", adviseType);

		manAdvDate = jdbcTemplate.queryForObject(selectSql.toString(), paramMap, Date.class);
		return manAdvDate;
	}

	@Override
	public List<ManualAdviseMovements> getInProcManualAdvMovmnts(List<Long> receiptList) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		source = new MapSqlParameterSource();
		source.addValue("ReceiptId", receiptList);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select AdviseID, SUM(PaidAmount) PaidAmount, SUM(WaivedAmount) WaivedAmount");
		sql.append(" From ManualAdviseMovements");
		sql.append(" Where ReceiptID IN (:ReceiptID)  GROUP BY AdviseID");

		logger.debug(Literal.SQL + sql.toString());
		RowMapper<ManualAdviseMovements> typeRowMapper = BeanPropertyRowMapper.newInstance(ManualAdviseMovements.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public boolean isManualAdviceExitsInManualMovements(long manualAdviseId) {

		StringBuilder selectSql = new StringBuilder();
		boolean adviceCondition = false;
		long adviceId = 0;

		selectSql.append("select adviseid from MANUALADVISEMOVEMENTS_Temp ");
		selectSql.append(" WHERE adviseid= :adviseid");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("adviseid", manualAdviseId);

		try {
			adviceId = jdbcTemplate.queryForObject(selectSql.toString(), paramMap, Long.class);
		} catch (EmptyResultDataAccessException er) {
			adviceId = 0;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		if (adviceId > 0) {
			adviceCondition = true;
		} else {
			adviceCondition = false;
		}
		return adviceCondition;
	}

	@Override
	public List<ManualAdviseMovements> getAdvMovementsByReceiptSeq(long receiptID, long receiptSeqID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount");
		sql.append(", Status, ReceiptID, ReceiptSeqID, TaxHeaderId,TdsPaid ");

		if (StringUtils.contains(type, "View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxApplicable, TaxComponent");
		}

		sql.append(" from ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("  Where ReceiptID = ? and ReceiptSeqID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, receiptID);
					ps.setLong(index++, receiptSeqID);
				}
			}, new RowMapper<ManualAdviseMovements>() {
				@Override
				public ManualAdviseMovements mapRow(ResultSet rs, int rowNum) throws SQLException {
					ManualAdviseMovements manualAdviseMovements = new ManualAdviseMovements();

					manualAdviseMovements.setMovementID(rs.getLong("MovementID"));
					manualAdviseMovements.setAdviseID(rs.getLong("AdviseID"));
					manualAdviseMovements.setMovementDate(rs.getTimestamp("MovementDate"));
					manualAdviseMovements.setMovementAmount(rs.getBigDecimal("MovementAmount"));
					manualAdviseMovements.setPaidAmount(rs.getBigDecimal("PaidAmount"));
					manualAdviseMovements.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
					manualAdviseMovements.setStatus(rs.getString("Status"));
					manualAdviseMovements.setReceiptID(rs.getLong("ReceiptID"));
					manualAdviseMovements.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
					manualAdviseMovements.setTaxHeaderId(JdbcUtil.getLong(rs.getLong("TaxHeaderId")));
					manualAdviseMovements.setTdsPaid(rs.getBigDecimal("TdsPaid"));

					if (StringUtils.contains(type, "View")) {
						manualAdviseMovements.setFeeTypeCode(rs.getString("FeeTypeCode"));
						manualAdviseMovements.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
						manualAdviseMovements.setTaxApplicable(rs.getBoolean("TaxApplicable"));
						manualAdviseMovements.setTaxComponent(rs.getString("TaxComponent"));
					}

					return manualAdviseMovements;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	@Override
	public ManualAdviseMovements getAdvMovByReceiptSeq(long receiptID, long receiptSeqID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount");
		sql.append(", Status, ReceiptID, ReceiptSeqID, TaxHeaderId ");

		if (StringUtils.contains(type, "View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxApplicable, TaxComponent");
		}

		sql.append(" from ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("  Where ReceiptID = ? and ReceiptSeqID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { receiptID, receiptSeqID },
					new RowMapper<ManualAdviseMovements>() {
						@Override
						public ManualAdviseMovements mapRow(ResultSet rs, int rowNum) throws SQLException {
							ManualAdviseMovements movement = new ManualAdviseMovements();

							movement.setMovementID(rs.getLong("MovementID"));
							movement.setAdviseID(rs.getLong("AdviseID"));
							movement.setMovementDate(rs.getTimestamp("MovementDate"));
							movement.setMovementAmount(rs.getBigDecimal("MovementAmount"));
							movement.setPaidAmount(rs.getBigDecimal("PaidAmount"));
							movement.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
							movement.setStatus(rs.getString("Status"));
							movement.setReceiptID(rs.getLong("ReceiptID"));
							movement.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
							movement.setTaxHeaderId(JdbcUtil.getLong(rs.getLong("TaxHeaderId")));

							if (StringUtils.contains(type, "View")) {
								movement.setFeeTypeCode(rs.getString("FeeTypeCode"));
								movement.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
								movement.setTaxApplicable(rs.getBoolean("TaxApplicable"));
								movement.setTaxComponent(rs.getString("TaxComponent"));
							}

							return movement;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;

	}

	@Override
	public List<ManualAdvise> getPreviousAdvPayments(String finReference) {
		StringBuilder sql = new StringBuilder();

		sql.append("select ma.FeeTypeID, ft.FeeTypeCode, ma.AdviseType, ma.AdviseAmount");
		sql.append(" from ManualAdvise ma");
		sql.append(" inner join FeeTypes ft on ft.FeeTypeID = ma.FeeTypeID");
		sql.append(" where FinReference = :FinReference and FeeTypeCode in (:FeeTypeCode)");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FeeTypeCode", Arrays.asList(AdvanceRuleCode.ADVINT.name(), AdvanceRuleCode.ADVEMI.name()));

		RowMapper<ManualAdvise> typeRowMapper = BeanPropertyRowMapper.newInstance(ManualAdvise.class);

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public void saveDueTaxDetail(AdviseDueTaxDetail adt) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into AdviseDueTaxDetail");
		sql.append(" (AdviseID, Amount, TaxType , CGST , SGST , UGST , IGST , CESS, TotalGST, InvoiceID)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setLong(index++, adt.getAdviseID());
				ps.setBigDecimal(index++, adt.getAmount());
				ps.setString(index++, adt.getTaxType());
				ps.setBigDecimal(index++, adt.getCGST());
				ps.setBigDecimal(index++, adt.getSGST());
				ps.setBigDecimal(index++, adt.getUGST());
				ps.setBigDecimal(index++, adt.getIGST());
				ps.setBigDecimal(index++, adt.getCESS());
				ps.setBigDecimal(index++, adt.getTotalGST());
				ps.setLong(index, adt.getInvoiceID());
			}
		});

	}

	@Override
	public boolean isAdviseDueCreated(long adviseID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AdviseID", adviseID);

		StringBuilder selectSql = new StringBuilder(" Select COUNT(*)  From AdviseDueTaxDetail");
		selectSql.append(" Where AdviseID = :AdviseID ");

		logger.debug("selectSql: " + selectSql.toString());

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			recordCount = 0;
		}

		logger.debug("Leaving");

		if (recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public AdviseDueTaxDetail getUnPaidTaxDetail(long adviseID) {
		logger.debug(Literal.ENTERING);

		AdviseDueTaxDetail taxDetail = new AdviseDueTaxDetail();
		taxDetail.setAdviseID(adviseID);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT A.AdviseID, (A.CGST - COALESCE(PAIDCGST,0)) CGST, (A.SGST - COALESCE(PAIDSGST,0)) SGST, ");
		selectSql.append(
				" (A.IGST - COALESCE(PAIDIGST,0)) IGST, (A.UGST - COALESCE(PAIDUGST,0)) UGST from AdviseDueTaxDetail A ");
		selectSql.append(
				" LEFT JOIN (Select AdviseID , SUM(PAIDCGST) PAIDCGST, SUM(PAIDSGST) PAIDSGST, SUM(PAIDIGST) PAIDIGST, SUM(PAIDUGST) PAIDUGST ");
		selectSql.append(
				" FROM ManualAdviseMovements WHERE COALESCE(STATUS, 'A') NOT IN ('B','C') GROUP BY AdviseID) M ON A.AdviseID = M.AdviseID WHERE A.AdviseID = :AdviseID ");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taxDetail);
		RowMapper<AdviseDueTaxDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(AdviseDueTaxDetail.class);

		try {
			taxDetail = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			taxDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return taxDetail;
	}

	@Override
	public Long getDebitInvoiceID(long adviseID) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AdviseID", adviseID);

		StringBuilder selectSql = new StringBuilder(" Select InvoiceID  From AdviseDueTaxDetail");
		selectSql.append(" Where AdviseID = :AdviseID ");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Method for fetching New Advise ID based on Sequence Object
	 * 
	 * @return
	 */
	@Override
	public long getNewAdviseID() {
		return getNextValue("seqManualAdvise");
	}

	@Override
	public void updateUtiliseOnly(long adviseID, BigDecimal amount) {
		StringBuilder sql = new StringBuilder("Update ManualAdvise");
		sql.append(" Set PaidAmount = PaidAmount + ?, BalanceAmt = BalanceAmt - ?");
		sql.append(" Where AdviseID = ?");

		logger.trace(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, amount);
			ps.setBigDecimal(index++, amount);
			ps.setLong(index++, adviseID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public ManualAdviseMovements getAdvMovByReceiptSeq(long receiptID, long receiptSeqID, long adviseId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount");
		sql.append(", Status, ReceiptID, ReceiptSeqID, TaxHeaderId ");

		if (StringUtils.contains(type, "View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxApplicable, TaxComponent");
		}

		sql.append(" from ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("  Where ReceiptID = ? and ReceiptSeqID = ? and AdviseID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(),
					new Object[] { receiptID, receiptSeqID, adviseId }, new RowMapper<ManualAdviseMovements>() {
						@Override
						public ManualAdviseMovements mapRow(ResultSet rs, int rowNum) throws SQLException {
							ManualAdviseMovements movement = new ManualAdviseMovements();

							movement.setMovementID(rs.getLong("MovementID"));
							movement.setAdviseID(rs.getLong("AdviseID"));
							movement.setMovementDate(rs.getTimestamp("MovementDate"));
							movement.setMovementAmount(rs.getBigDecimal("MovementAmount"));
							movement.setPaidAmount(rs.getBigDecimal("PaidAmount"));
							movement.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
							movement.setStatus(rs.getString("Status"));
							movement.setReceiptID(rs.getLong("ReceiptID"));
							movement.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
							movement.setTaxHeaderId(JdbcUtil.getLong(rs.getLong("TaxHeaderId")));

							if (StringUtils.contains(type, "View")) {
								movement.setFeeTypeCode(rs.getString("FeeTypeCode"));
								movement.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
								movement.setTaxApplicable(rs.getBoolean("TaxApplicable"));
								movement.setTaxComponent(rs.getString("TaxComponent"));
							}

							return movement;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public BigDecimal getPayableBalanceAmt(String finReference, int adviseType) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT sum(adviseamount-paidamount-waivedamount) from manualAdvise");
		sql.append(" Where FinReference = ? and AdviseType = ?");

		Object[] object = new Object[] { finReference, adviseType };
		return this.jdbcOperations.queryForObject(sql.toString(), object, BigDecimal.class);

	}

	@Override
	public BigDecimal getReceivableAmt(String finReference, boolean isBounce) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Coalesce(Sum(AdviseAmount - PaidAmount - WaivedAmount), 0) Amount");
		sql.append(" from ManualAdvise");
		sql.append(" Where FinReference = ? and AdviseType = ? ");

		if (isBounce) {
			sql.append(" And (BounceId > 0 or FeeTypeID IN (Select FeeTypeID from FeeTypes Where FeeTypeCode = ?))");
		} else {
			sql.append(" And FeeTypeID Not IN (Select FeeTypeID from FeeTypes Where FeeTypeCode = ?)");
		}

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, 1, "BOUNCE" },
					BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record is not found in ManualAdvise for the specified reference >> {} and AdviseTpe >> 1",
					finReference);
		}

		return BigDecimal.ZERO;
	}

}
