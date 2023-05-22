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
 * * FileName : FinFeeReceiptDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 1-06-2017 * * Modified
 * Date : 1-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 1-06-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinFeeReceipt model</b> class.<br>
 * 
 */

public class FinFeeReceiptDAOImpl extends SequenceDao<FinFeeReceipt> implements FinFeeReceiptDAO {
	private static Logger logger = LogManager.getLogger(FinFeeReceiptDAOImpl.class);

	public FinFeeReceiptDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Goods Details details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinFeeReceipt
	 */
	@Override
	public FinFeeReceipt getFinFeeReceiptById(FinFeeReceipt finFeeReceipt, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT ID, FeeID, ReceiptID, PaidAmount,");
		selectSql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, PaidTds");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(
					",ReceiptAmount, FeeTypeCode, FeeTypeDesc, FEETYPEID, ReceiptType, ReceiptReference, transactionRef, favourNumber, vasReference ");
		}

		selectSql.append(" From FinFeeReceipts");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE  Id = :Id");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeReceipt);
		RowMapper<FinFeeReceipt> typeRowMapper = BeanPropertyRowMapper.newInstance(FinFeeReceipt.class);

		try {
			finFeeReceipt = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finFeeReceipt = null;
		}

		logger.debug("Leaving");

		return finFeeReceipt;
	}

	/**
	 * Method for Checking Count of Receipt by using Receipt ID
	 */
	@Override
	public boolean isFinFeeReceiptAllocated(long receiptID, String type) {
		logger.debug("Entering");

		FinFeeReceipt receipt = new FinFeeReceipt();
		receipt.setReceiptID(receiptID);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Count(*) From FinFeeReceipts");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE  ReceiptID = :ReceiptID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receipt);
		int count = 0;
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			count = 0;
		}

		logger.debug("Leaving");
		return count > 0 ? true : false;
	}

	@Override
	public List<FinFeeReceipt> getFinFeeReceiptByFinRef(final List<Long> feeIds, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FeeID, ReceiptID, PaidAmount, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, PaidTds");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", ReceiptAmount, FeeTypeCode, FeeTypeDesc, FeeTypeId, ReceiptType");
			sql.append(", TransactionRef, FavourNumber, VasReference");
		}

		sql.append(" from FinFeeReceipts");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FeeID IN (");

		int i = 0;
		while (i < feeIds.size()) {
			sql.append(" ?,");
			i++;
		}

		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
		sql.append(" and PaidAmount > ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			for (Long feeId : feeIds) {
				ps.setLong(index++, feeId);
			}
			ps.setInt(index, 0);

		}, (rs, rowNum) -> {
			FinFeeReceipt gstD = new FinFeeReceipt();

			gstD.setId(rs.getLong("Id"));
			gstD.setFeeID(rs.getLong("FeeID"));
			gstD.setReceiptID(rs.getLong("ReceiptID"));
			gstD.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			gstD.setVersion(rs.getInt("Version"));
			gstD.setLastMntBy(rs.getLong("LastMntBy"));
			gstD.setLastMntOn(rs.getTimestamp("LastMntOn"));
			gstD.setRecordStatus(rs.getString("RecordStatus"));
			gstD.setRoleCode(rs.getString("RoleCode"));
			gstD.setNextRoleCode(rs.getString("NextRoleCode"));
			gstD.setTaskId(rs.getString("TaskId"));
			gstD.setNextTaskId(rs.getString("NextTaskId"));
			gstD.setRecordType(rs.getString("RecordType"));
			gstD.setWorkflowId(rs.getLong("WorkflowId"));
			gstD.setPaidTds(rs.getBigDecimal("PaidTds"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				gstD.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
				gstD.setFeeTypeCode(rs.getString("FeeTypeCode"));
				gstD.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				gstD.setFeeTypeId(rs.getLong("FeeTypeId"));
				gstD.setReceiptType(rs.getString("ReceiptType"));
				gstD.setTransactionRef(rs.getString("TransactionRef"));
				gstD.setFavourNumber(rs.getString("FavourNumber"));
				gstD.setVasReference(rs.getString("VasReference"));
			}

			return gstD;
		});
	}

	/**
	 * This method Deletes the Record from the FinFeeReceipt or FinFeeReceipt_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Details by key LoanRefNumber
	 * 
	 * @param Goods Details (FinFeeReceipt)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinFeeReceipt finFeeDetail, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinFeeReceipts");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Id = :Id and ReceiptID = :ReceiptID and FeeID = :FeeID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinFeeReceipt or FinFeeReceipt_Temp.
	 * 
	 * save Goods Details
	 * 
	 * @param Goods Details (FinFeeReceipt)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(FinFeeReceipt finFeeReceipt, String type) {
		logger.debug("Entering");

		if (finFeeReceipt.getId() == Long.MIN_VALUE) {
			finFeeReceipt.setId(getNextValue("SeqFinFeeReceipts"));
			logger.debug("get NextID:" + finFeeReceipt.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into FinFeeReceipts");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Id, FeeID, ReceiptID, PaidAmount, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, PaidTds)");
		insertSql.append(" Values( :Id, :FeeID, :ReceiptID, :PaidAmount,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId, :PaidTds)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeReceipt);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");

		return finFeeReceipt.getId();
	}

	/**
	 * This method updates the Record FinFeeReceipt or FinFeeReceipt_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Details by key LoanRefNumber and Version
	 * 
	 * @param Goods Details (FinFeeReceipt)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinFeeReceipt finFeeDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update FinFeeReceipts");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set FeeID = :FeeID, ReceiptID = :ReceiptID, PaidAmount = :PaidAmount, ");
		updateSql.append(
				"  Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, ");
		updateSql.append(
				"  RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append("  RecordType = :RecordType, WorkflowId = :WorkflowId, PaidTds = :PaidTds");
		updateSql.append("  Where Id = :Id ");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving");
	}

	@Override
	public List<FinFeeReceipt> getFinFeeReceiptByFeeId(long feeId, String type) {

		logger.debug("Entering");

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FeeID", feeId);

		StringBuilder selectSql = new StringBuilder(" SELECT ID, FeeID, ReceiptID, PaidAmount,");
		selectSql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, PaidTds");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(
					",ReceiptAmount, FeeTypeCode, FeeTypeDesc, FeeTypeID, ReceiptType, transactionRef, favourNumber, vasReference,FeeTypeId ");
		}
		selectSql.append(" From FinFeeReceipts");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append("  Where FeeID =:FeeID");

		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		RowMapper<FinFeeReceipt> typeRowMapper = BeanPropertyRowMapper.newInstance(FinFeeReceipt.class);

		return this.jdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);

	}

	@Override
	public BigDecimal getUpfrontFee(long feeId, String tableType) {
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select sum(paidamount) from finfeereceipts");
		selectSql.append(tableType);
		selectSql.append(" WHERE  feeid = :feeId");
		selectSql.append(" group by feeid");

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("feeId", feeId);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);
	}

	// Getting FinFee and Receipt details for Disbursement Memo Report.
	@Override
	public List<Map<String, Object>> getFeeDetails(String finReference) {
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				"select ffd.feeid,frh.receiptid,ffd.vasreference,frh.receiptdate,ffd.actualamount,ft.feetypedesc,ffd.feeschedulemethod");
		selectSql.append(" from finfeedetail_temp ffd");
		selectSql.append(" left join  finfeereceipts ffr on ffr.feeid = ffd.feeid");
		selectSql.append(" left join  finreceiptheader frh on frh.receiptid = ffr.receiptid");
		selectSql.append(" left join FEETYPES ft on ft.FEETYPEID = ffd.feetypeid");
		selectSql.append(" where ffd.finreference=:finReference");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("finReference", finReference);

		return this.jdbcTemplate.queryForList(selectSql.toString(), source);
	}

	@Override
	public List<FinFeeReceipt> getFinFeeReceiptByReceiptId(long receiptID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ID, FeeID, ReceiptID, PaidAmount,  Version, LastMntBy, LastMntOn, RecordStatus,");
		sql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, PaidTds");
		sql.append(" From FinFeeReceipts");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE  ReceiptID = :ReceiptID");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		RowMapper<FinFeeReceipt> typeRowMapper = BeanPropertyRowMapper.newInstance(FinFeeReceipt.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public void deleteFinFeeReceiptByReceiptId(long receiptID, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinFeeReceipts");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ReceiptID = :ReceiptID ");
		logger.debug(Literal.SQL + deleteSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		try {
			this.jdbcTemplate.update(deleteSql.toString(), source);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<FinFeeReceipt> getFinFeeReceiptByFeeType(String finrReference, String feeType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, FeeID, ReceiptID, PaidAmount, PaidTds, ReceiptAmount, ReceiptType");
		sql.append(", FeeTypeCode, FeeTypeDesc, FeeTypeID, TransactionRef, FavourNumber, VasReference");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinFeeReceipts_Tview");
		sql.append(" Where Finreference = ? And FeeTypeCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinFeeReceipt feeRec = new FinFeeReceipt();

			feeRec.setId(rs.getLong("ID"));
			feeRec.setFeeID(rs.getLong("FeeID"));
			feeRec.setReceiptID(rs.getLong("ReceiptID"));
			feeRec.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			feeRec.setPaidTds(rs.getBigDecimal("PaidTds"));
			feeRec.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			feeRec.setReceiptType(rs.getString("ReceiptType"));
			feeRec.setFeeTypeCode(rs.getString("FeeTypeCode"));
			feeRec.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
			feeRec.setFeeTypeId(rs.getLong("FeeTypeID"));
			feeRec.setTransactionRef(rs.getString("TransactionRef"));
			feeRec.setFavourNumber(rs.getString("FavourNumber"));
			feeRec.setVasReference(rs.getString("VasReference"));
			feeRec.setVersion(rs.getInt("Version"));
			feeRec.setLastMntBy(rs.getLong("LastMntBy"));
			feeRec.setLastMntOn(rs.getTimestamp("LastMntOn"));
			feeRec.setRecordStatus(rs.getString("RecordStatus"));
			feeRec.setRoleCode(rs.getString("RoleCode"));
			feeRec.setNextRoleCode(rs.getString("NextRoleCode"));
			feeRec.setTaskId(rs.getString("TaskId"));
			feeRec.setNextTaskId(rs.getString("NextTaskId"));
			feeRec.setRecordType(rs.getString("RecordType"));
			feeRec.setWorkflowId(rs.getLong("WorkflowId"));

			return feeRec;
		}, finrReference, feeType);

	}

}