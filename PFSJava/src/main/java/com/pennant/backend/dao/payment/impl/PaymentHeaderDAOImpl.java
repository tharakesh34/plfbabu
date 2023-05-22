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
 * * FileName : PaymentHeaderDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * * Modified
 * Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.payment.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.util.RepayConstants;
import com.pennant.pff.knockoff.KnockOffType;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PaymentHeader</code> with set of CRUD operations.
 */
public class PaymentHeaderDAOImpl extends SequenceDao<PaymentHeader> implements PaymentHeaderDAO {
	private static Logger logger = LogManager.getLogger(PaymentHeaderDAOImpl.class);

	public PaymentHeaderDAOImpl() {
		super();
	}

	@Override
	public PaymentHeader getPaymentHeader(long paymentId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PaymentId, PaymentType, PaymentAmount, CreatedOn, ApprovedOn, Status, FinID, FinReference");
		if (type.contains("View")) {
			sql.append(", PaymentType, Status, CustId, CustCoreBank");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PaymentHeader");
		sql.append(type);
		sql.append(" Where PaymentId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				PaymentHeader ph = new PaymentHeader();
				ph.setPaymentId(rs.getLong("PaymentId"));
				ph.setPaymentType(rs.getString("PaymentType"));
				ph.setPaymentAmount(rs.getBigDecimal("PaymentAmount"));
				ph.setCreatedOn(rs.getTimestamp("CreatedOn"));
				ph.setApprovedOn(rs.getDate("ApprovedOn"));
				ph.setStatus(rs.getString("Status"));
				ph.setFinID(rs.getLong("FinID"));
				ph.setFinReference(rs.getString("FinReference"));

				if (type.contains("View")) {
					ph.setPaymentType(rs.getString("PaymentType"));
					ph.setStatus(rs.getString("Status"));
					ph.setCustID(rs.getLong("CustId"));
					ph.setCustCoreBank(rs.getString("CustCoreBank"));
				}

				ph.setVersion(rs.getInt("Version"));
				ph.setLastMntBy(rs.getLong("LastMntBy"));
				ph.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ph.setRecordStatus(rs.getString("RecordStatus"));
				ph.setRoleCode(rs.getString("RoleCode"));
				ph.setNextRoleCode(rs.getString("NextRoleCode"));
				ph.setTaskId(rs.getString("TaskId"));
				ph.setNextTaskId(rs.getString("NextTaskId"));
				ph.setRecordType(rs.getString("RecordType"));
				ph.setWorkflowId(rs.getLong("WorkflowId"));

				return ph;
			}, paymentId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String save(PaymentHeader ph, TableType tableType) {
		if (ph.getPaymentId() <= 0) {
			ph.setPaymentId(getNextValue("SeqPaymentHeader"));
		}

		StringBuilder sql = new StringBuilder("Insert Into PaymentHeader");
		sql.append(tableType.getSuffix());
		sql.append("( PaymentId, PaymentType, PaymentAmount, CreatedOn, ApprovedOn, Status, FinID, FinReference");
		sql.append(", LinkedTranId, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, ph.getPaymentId());
				ps.setString(index++, ph.getPaymentType());
				ps.setBigDecimal(index++, ph.getPaymentAmount());
				ps.setDate(index++, JdbcUtil.getDate(ph.getCreatedOn()));
				ps.setDate(index++, JdbcUtil.getDate(ph.getApprovedOn()));
				ps.setString(index++, ph.getStatus());
				ps.setLong(index++, ph.getFinID());
				ps.setString(index++, ph.getFinReference());
				ps.setLong(index++, ph.getLinkedTranId());
				ps.setInt(index++, ph.getVersion());
				ps.setLong(index++, ph.getLastMntBy());
				ps.setTimestamp(index++, ph.getLastMntOn());
				ps.setString(index++, ph.getRecordStatus());
				ps.setString(index++, ph.getRoleCode());
				ps.setString(index++, ph.getNextRoleCode());
				ps.setString(index++, ph.getTaskId());
				ps.setString(index++, ph.getNextTaskId());
				ps.setString(index++, ph.getRecordType());
				ps.setLong(index, ph.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(ph.getPaymentId());
	}

	@Override
	public void update(PaymentHeader ph, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update PaymentHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Set");
		sql.append(" PaymentType = ?, PaymentAmount = ?, CreatedOn = ?, ApprovedOn = ?, Status = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where PaymentId = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, ph.getPaymentType());
			ps.setBigDecimal(index++, ph.getPaymentAmount());
			ps.setDate(index++, JdbcUtil.getDate(ph.getCreatedOn()));
			ps.setDate(index++, JdbcUtil.getDate(ph.getApprovedOn()));
			ps.setString(index++, ph.getStatus());
			ps.setTimestamp(index++, ph.getLastMntOn());
			ps.setString(index++, ph.getRecordStatus());
			ps.setString(index++, ph.getRoleCode());
			ps.setString(index++, ph.getNextRoleCode());
			ps.setString(index++, ph.getTaskId());
			ps.setString(index++, ph.getNextTaskId());
			ps.setString(index++, ph.getRecordType());
			ps.setLong(index++, ph.getWorkflowId());

			ps.setLong(index, ph.getPaymentId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(PaymentHeader paymentHeader, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from PaymentHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Where PaymentId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			if (jdbcOperations.update(sql.toString(), paymentHeader.getPaymentId()) == 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

	}

	@Override
	public boolean isDuplicateKey(long paymentId, TableType tableType) {
		String sql;
		String whereClause = "PaymentId = ?";

		Object[] args = new Object[] { paymentId };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("PaymentHeader", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("PaymentHeader_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "PaymentHeader_Temp", "PaymentHeader" }, whereClause);

			args = new Object[] { paymentId, paymentId };
			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, args) > 0;
	}

	@Override
	public FinanceMain getFinanceDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, ft.FinType, ft.FinTypeDesc, ft.FinDivision");
		sql.append(", fm.CalRoundingMode, fm.RoundingTarget, fm.FinBranch, fm.CustID, cu.CustCif");
		sql.append(", cu.CustShrtName, curr.CcyCode, fm.FinStartDate, fm.MaturityDate, div.EntityCode");
		sql.append(", fm.ClosingStatus, fm.WRITEOFFLOAN, h.HoldStatus ");
		sql.append(" From FinanceMainMaintenance_View fm");
		sql.append(" Inner Join Customers cu on cu.CustID = fm.CustID");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join RMTCurrencies curr on curr.CcyCode = fm.FinCcy");
		sql.append(" Inner Join SMTDivisionDetail div on div.DivisionCode = ft.FinDivision");
		sql.append(" LEFT JOIN Fin_Hold_Details h ON fm.FINID = h.FINID ");
		sql.append(" Where fm.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinType(rs.getString("FinType"));
				fm.setLovDescFinTypeName(rs.getString("FinTypeDesc"));
				fm.setFinPurpose(rs.getString("FinDivision"));
				fm.setCalRoundingMode(rs.getString("CalRoundingMode"));
				fm.setRoundingTarget(rs.getInt("RoundingTarget"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setLovDescCustCIF(rs.getString("CustCif"));
				fm.setLovDescCustShrtName(rs.getString("CustShrtName"));
				fm.setFinCcy(rs.getString("CcyCode"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setEntityCode(rs.getString("EntityCode"));
				fm.setLovDescEntityCode(rs.getString("EntityCode"));
				fm.setClosingStatus(rs.getString("ClosingStatus"));
				fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
				fm.setHoldStatus(rs.getString("HoldStatus"));

				return fm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinExcessAmount> getfinExcessAmount(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ExcessID, FinID, FinReference, AmountType, Amount, BalanceAmt, ReservedAmt, ReceiptID");
		sql.append(", ValueDate, PostDate From FinExcessAmount Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinExcessAmount fea = new FinExcessAmount();

			fea.setExcessID(rs.getLong("ExcessID"));
			fea.setFinID(rs.getLong("FinID"));
			fea.setFinReference(rs.getString("FinReference"));
			fea.setAmountType(rs.getString("AmountType"));
			fea.setAmount(rs.getBigDecimal("Amount"));
			fea.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			fea.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
			fea.setReceiptID(JdbcUtil.getLong(rs.getObject("ReceiptID")));
			fea.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));
			fea.setPostDate(JdbcUtil.getDate(rs.getDate("PostDate")));

			return fea;
		}, finID);
	}

	@Override
	public List<ManualAdvise> getManualAdvise(long finID) {
		StringBuilder sql = getSqlQuery();
		sql.append(" Where FinID = ? and ma.AdviseType = ? and ma.Status is null");

		logger.trace(Literal.SQL + sql.toString());

		List<ManualAdvise> list = jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			ps.setInt(2, 2);
		}, (rs, i) -> {
			return getRowMapper(rs);
		});

		return list.stream().sorted((l1, l2) -> l1.getValueDate().compareTo(l2.getValueDate()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ManualAdvise> getManualAdviseForEnquiry(long finID) {
		StringBuilder sql = getSqlQuery();
		sql.append(" Where FinID = ? and ma.AdviseType = ? and PaidAmount > ?");

		logger.debug(Literal.SQL + sql.toString());

		List<ManualAdvise> list = jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			ps.setInt(2, 2);
			ps.setInt(3, 0);
		}, (rs, i) -> {
			return getRowMapper(rs);
		});

		return list.stream().sorted((l1, l2) -> l1.getValueDate().compareTo(l2.getValueDate()))
				.collect(Collectors.toList());
	}

	private ManualAdvise getRowMapper(ResultSet rs) throws SQLException {
		ManualAdvise ma = new ManualAdvise();

		ma.setAdviseID(rs.getLong("AdviseID"));
		ma.setFinID(rs.getLong("FinID"));
		ma.setFinReference(rs.getString("FinReference"));
		ma.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
		ma.setAdviseType(rs.getInt("AdviseType"));
		ma.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
		ma.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
		ma.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));
		ma.setPaidAmount(rs.getBigDecimal("PaidAmount"));
		ma.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
		ma.setFeeTypeCode(rs.getString("FeeTypeCode"));
		ma.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
		ma.setTaxApplicable(rs.getBoolean("TaxApplicable"));
		ma.setTaxComponent(rs.getString("TaxComponent"));
		ma.setPaidCGST(rs.getBigDecimal("PaidCGST"));
		ma.setPaidSGST(rs.getBigDecimal("PaidSGST"));
		ma.setPaidIGST(rs.getBigDecimal("PaidIGST"));
		ma.setPaidUGST(rs.getBigDecimal("PaidUGST"));
		ma.setPaidCESS(rs.getBigDecimal("PaidCESS"));
		ma.setWaivedCGST(rs.getBigDecimal("WaivedCGST"));
		ma.setWaivedSGST(rs.getBigDecimal("WaivedSGST"));
		ma.setWaivedIGST(rs.getBigDecimal("WaivedIGST"));
		ma.setWaivedUGST(rs.getBigDecimal("WaivedUGST"));
		ma.setWaivedCESS(rs.getBigDecimal("WaivedCESS"));
		ma.setHoldDue(rs.getBoolean("HoldDue"));
		ma.setRefundable(rs.getBoolean("Refundable"));
		return ma;
	}

	private StringBuilder getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseID, FinID, FinReference, BalanceAmt, ma.AdviseType, AdviseAmount, ReservedAmt, ValueDate");
		sql.append(", PaidAmount, WaivedAmount, FeeTypeCode, FeeTypeDesc, ft.TaxApplicable, ft.TaxComponent");
		sql.append(", PaidCGST, PaidIGST, PaidSGST, PaidUGST, PaidCESS");
		sql.append(", WaivedCGST, WaivedIGST, WaivedSGST, WaivedUGST, WaivedCESS, HoldDue, Refundable");
		sql.append(" From ManualAdvise ma");
		sql.append(" Inner Join FeeTypes ft on ft.FeeTypeId = ma.FeeTypeId");

		return sql;
	}

	/**
	 * Method for fetching New PaymentHeaderId based on Sequence Object
	 * 
	 * @return
	 */
	@Override
	public long getNewPaymentHeaderId() {
		return getNextValue("SeqPaymentHeader");
	}

	@Override
	public Map<Long, BigDecimal> getAdvisesInProgess(long finId) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" SUM(AMOUNT) AMOUNT,FRD.PAYAGAINSTID FROM FINRECEIPTDETAIL_TEMP FRD ");
		sql.append(" INNER JOIN FINRECEIPTHEADER_TEMP FR ON FR.RECEIPTID = FRD.RECEIPTID ");
		sql.append(" WHERE FRD.PAYMENTTYPE IN('PAYABLE') AND FINID = ? ");
		sql.append(" GROUP BY FRD.PAYAGAINSTID");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), ps -> {
				ps.setLong(1, finId);
			}, new ResultSetExtractor<Map<Long, BigDecimal>>() {

				@Override
				public Map<Long, BigDecimal> extractData(ResultSet rs) throws SQLException, DataAccessException {
					Map<Long, BigDecimal> rcMap = new HashMap<>();
					while (rs.next()) {
						rcMap.put(rs.getLong("PAYAGAINSTID"), rs.getBigDecimal("AMOUNT"));
					}
					return rcMap;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("No advises are inprogress for the finId >> " + finId);
		}

		return new HashMap<>();
	}

	@Override
	public BigDecimal getInProgressExcessAmt(long finId, Long receiptId) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" COALESCE(EXCESSAMOUNT,0) AMOUNT,FINID FROM FINREPAYHEADER WHERE RECEIPTSEQID IN ( ");
		sql.append(" SELECT RECEIPTSEQID FROM FINRECEIPTDETAIL_TEMP WHERE STATUS IN (?, ?)");
		if (receiptId != null) {
			sql.append(" and ReceiptId = ?");
		}
		sql.append(")");
		sql.append(" AND EXCESSAMOUNT > 0  AND FINID = ? ");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), ps -> {
				int index = 1;
				ps.setString(index++, RepayConstants.PAYSTATUS_BOUNCE);
				ps.setString(index++, RepayConstants.PAYSTATUS_CANCEL);
				if (receiptId != null) {
					ps.setLong(index++, receiptId);
				}
				ps.setLong(index++, finId);
			}, new ResultSetExtractor<BigDecimal>() {
				@Override
				public BigDecimal extractData(ResultSet rs) throws SQLException, DataAccessException {
					BigDecimal amount = BigDecimal.ZERO;
					while (rs.next()) {
						amount = rs.getBigDecimal("AMOUNT");
					}
					return amount;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("No Excess Receipts are inprogress for the finId >> " + finId);
		}

		return BigDecimal.ZERO;
	}

	@Override
	public boolean isRefundInProcess(long finId) {
		String sql = "Select count(FinId) From PaymentHeader_Temp Where FinId = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, finId) > 0;
	}

	@Override
	public Long getPaymetIDByReceiptID(long receiptId) {
		StringBuilder sql = new StringBuilder("Select ph.PaymentID");
		sql.append(" From FinexcessAmount fa");
		sql.append(" Inner Join PaymentDetails_Temp pd on pd.ReferenceID = fa.ExcessID");
		sql.append(" Inner Join PaymentHeader_Temp ph on ph.PaymentID = pd.PaymentID");
		sql.append(" Where fa.ReceiptId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, receiptId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}

	}

	@Override
	public List<Long> getReceiptPurpose(long receiptId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" rch.ReceiptID From FinReceiptHeader_Temp rch");
		sql.append(" Inner Join FinReceiptDetail_Temp rcd on rcd.receiptId = rch.receiptId");
		sql.append(" Where rcd.PayAgainstId = ? and rch.ReceiptModeStatus not in (?, ?) and rch.KnockOffType != ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
				return rs.getLong(1);
			}, receiptId, RepayConstants.PAYSTATUS_BOUNCE, RepayConstants.PAYSTATUS_CANCEL,
					KnockOffType.CROSS_LOAN.code());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getPaymenttId(long paymentId) {
		String sql = "Select Count(PaymentInstructionId) From PAYMENTINSTRUCTIONS Where PaymentId = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), paymentId);
	}

	@Override
	public void updateTransactionRef(long paymentId, String transactionRef) {
		String sql = "Update PAYMENTINSTRUCTIONS Set TransactionRef = ? Where PaymentId = ? ";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, transactionRef);
			ps.setLong(2, paymentId);
		});
	}

	@Override
	public int getPaymenttId(long paymentId, String finReference) {
		String sql = "Select Count(paymentId) From PaymentHeader Where paymentId = ? and finReference = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), paymentId,
				finReference);
	}

	@Override
	public boolean isRefundProvided(long finId) {
		String sql = "Select count(FinID) From PaymentHeader Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, finId) > 0;
	}
}
