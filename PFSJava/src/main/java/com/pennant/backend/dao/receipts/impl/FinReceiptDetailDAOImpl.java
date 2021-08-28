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
 * * FileName : FinanceRepaymentsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.receipts.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinReceiptDetailDAOImpl extends SequenceDao<FinReceiptDetail> implements FinReceiptDetailDAO {
	private static Logger logger = LogManager.getLogger(FinReceiptDetailDAOImpl.class);

	public FinReceiptDetailDAOImpl() {
		super();
	}

	@Override
	public List<FinReceiptDetail> getReceiptHeaderByID(long receiptID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptID, ReceiptSeqID, ReceiptType, PaymentTo, PaymentType, PayAgainstID");
		sql.append(", Amount, FavourNumber, ValueDate, BankCode, FavourName, DepositDate, DepositNo");
		sql.append(", PaymentRef, TransactionRef, ChequeAcNo, FundingAc, ReceivedDate, Status, PayOrder, LogKey");
		sql.append(", BankBranchID");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", BankCodeDesc, fundingAcCode, FundingAcDesc, PartnerBankAc, PartnerBankAcType");
			sql.append(", iFSC, BranchDesc");
			if (StringUtils.trimToEmpty(type).contains("AView")) {
				sql.append(", FeeTypeCode, FeeTypeDesc");
			}
		}

		sql.append(" From FinReceiptDetail");
		sql.append(StringUtils.trim(type));
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, receiptID);
		}, (rs, rowNum) -> {
			FinReceiptDetail rd = new FinReceiptDetail();

			rd.setReceiptID(rs.getLong("ReceiptID"));
			rd.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
			rd.setReceiptType(rs.getString("ReceiptType"));
			rd.setPaymentTo(rs.getString("PaymentTo"));
			rd.setPaymentType(rs.getString("PaymentType"));
			rd.setPayAgainstID(rs.getLong("PayAgainstID"));
			rd.setAmount(rs.getBigDecimal("Amount"));
			rd.setFavourNumber(rs.getString("FavourNumber"));
			rd.setValueDate(rs.getTimestamp("ValueDate"));
			rd.setBankCode(rs.getString("BankCode"));
			rd.setFavourName(rs.getString("FavourName"));
			rd.setDepositDate(rs.getTimestamp("DepositDate"));
			rd.setDepositNo(rs.getString("DepositNo"));
			rd.setPaymentRef(rs.getString("PaymentRef"));
			rd.setTransactionRef(rs.getString("TransactionRef"));
			rd.setChequeAcNo(rs.getString("ChequeAcNo"));
			rd.setFundingAc(rs.getLong("FundingAc"));
			rd.setReceivedDate(rs.getTimestamp("ReceivedDate"));
			rd.setStatus(rs.getString("Status"));
			rd.setPayOrder(rs.getInt("PayOrder"));
			rd.setLogKey(rs.getLong("LogKey"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				rd.setBankCodeDesc(rs.getString("BankCodeDesc"));
				rd.setFundingAcCode(rs.getString("fundingAcCode"));
				rd.setFundingAcDesc(rs.getString("FundingAcDesc"));
				rd.setPartnerBankAc(rs.getString("PartnerBankAc"));
				rd.setPartnerBankAcType(rs.getString("PartnerBankAcType"));

				if (StringUtils.trimToEmpty(type).contains("AView")) {
					rd.setFeeTypeCode(rs.getString("FeeTypeCode"));
					rd.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				}
			}

			return rd;
		});
	}

	@Override
	public long save(FinReceiptDetail receiptDetail, TableType tableType) {
		if (receiptDetail.getId() == 0 || receiptDetail.getId() == Long.MIN_VALUE) {
			receiptDetail.setId(getNextValue("SeqFinReceiptDetail"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinReceiptDetail");
		sql.append(tableType.getSuffix());
		sql.append(" (ReceiptID, ReceiptSeqID, ReceiptType, PaymentTo, PaymentType, PayAgainstID, Amount");
		sql.append(", FavourNumber, ValueDate, BankCode, FavourName, DepositDate, DepositNo, PaymentRef");
		sql.append(", TransactionRef, ChequeAcNo, FundingAc, ReceivedDate, Status, PayOrder, LogKey, BankBranchID");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, receiptDetail.getReceiptID());
			ps.setLong(index++, receiptDetail.getReceiptSeqID());
			ps.setString(index++, receiptDetail.getReceiptType());
			ps.setString(index++, receiptDetail.getPaymentTo());
			ps.setString(index++, receiptDetail.getPaymentType());
			ps.setLong(index++, receiptDetail.getPayAgainstID());
			ps.setBigDecimal(index++, receiptDetail.getAmount());
			ps.setString(index++, receiptDetail.getFavourNumber());
			ps.setDate(index++, JdbcUtil.getDate(receiptDetail.getValueDate()));
			ps.setString(index++, receiptDetail.getBankCode());
			ps.setString(index++, receiptDetail.getFavourName());
			ps.setDate(index++, JdbcUtil.getDate(receiptDetail.getDepositDate()));
			ps.setString(index++, receiptDetail.getDepositNo());
			ps.setString(index++, receiptDetail.getPaymentRef());
			ps.setString(index++, receiptDetail.getTransactionRef());
			ps.setString(index++, receiptDetail.getChequeAcNo());
			ps.setLong(index++, receiptDetail.getFundingAc());
			ps.setDate(index++, JdbcUtil.getDate(receiptDetail.getReceivedDate()));
			ps.setString(index++, receiptDetail.getStatus());
			ps.setInt(index++, receiptDetail.getPayOrder());
			ps.setLong(index++, receiptDetail.getLogKey());
			ps.setLong(index++, receiptDetail.getBankBranchID());
		});

		return receiptDetail.getId();
	}

	@Override
	public void deleteByReceiptID(long receiptID, TableType tableType) {
		String sql = "Delete From FinReceiptDetail".concat(tableType.getSuffix()).concat(" Where ReceiptID = ?");

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, receiptID);
	}

	@Override
	public void updateReceiptStatus(long receiptID, long receiptSeqID, String status) {
		String sql = "Update FinReceiptDetail Set Status = ? Where ReceiptID = ? And ReceiptSeqID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;
			ps.setString(index++, status);
			ps.setLong(index++, receiptID);
			ps.setLong(index++, receiptSeqID);
		});
	}

	@Override
	public int getReceiptHeaderByBank(String bankCode, String type) {
		StringBuilder sql = new StringBuilder("Select count(BankCode)");
		sql.append(" From FinReceiptDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, bankCode);
	}

	@Override
	public List<FinReceiptDetail> getFinReceiptDetailByFinRef(String finReference, long custId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("Status", "C");
		source.addValue("ReceiptPurpose", "FeePayment");
		source.addValue("RECAGAINST1", RepayConstants.RECEIPTTO_FINANCE);
		source.addValue("RECAGAINST2", RepayConstants.RECEIPTTO_CUSTOMER);
		source.addValue("CustId", String.valueOf(custId));

		StringBuilder sql = new StringBuilder();
		sql.append("Select T1.RECEIPTID, T1.TRANSACTIONREF, T2.FAVOURNUMBER");
		sql.append(", T1.RECEIPTMODE PaymentType, T1.ReceiptAmount");
		sql.append(" From FinReceiptHeader T1 ");
		sql.append(" Inner Join FINRECEIPTDETAIL T2 on T1.ReceiptID = T2.RECEIPTID");
		sql.append(" where ReceiptPurpose = :ReceiptPurpose And T2.Status <> :Status");
		sql.append(" And ((RECAGAINST = :RECAGAINST1 and T1.Reference = :FinReference) OR ");
		sql.append(" (RECAGAINST = :RECAGAINST2 and T1.Reference = :CustId");
		sql.append(" and T1.RECEIPTID not in");
		sql.append(" (Select Distinct ReceiptId from FINFEERECEIPTS_View where FinReference <> :FinReference)))");

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinReceiptDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinReceiptDetail.class);

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public Date getMaxReceivedDateByReference(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" max(T1.ValueDate)");
		sql.append(" From FinReceiptHeader rch");
		sql.append(" Inner Join FinReceiptDetail rcd on rcd.ReceiptId = rch.ReceiptId");
		sql.append(" Where rch.Reference = ? AND rcd.Status not in (?, ?) ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Date.class, finReference, "B", "C");
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public List<RepayScheduleDetail> fetchRepaySchduleList(long receiptSeqId) {
		StringBuilder sql = new StringBuilder("Select rsd.SchDate, rsd.WaivedAmt");
		sql.append(" From FinRepayScheduleDetail rsd");
		sql.append(" Inner join FinRepayHeader rh on rh.RepayId = rsd.RepayId");
		sql.append(" Where rh.ReceiptSeqId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			RepayScheduleDetail rsd = new RepayScheduleDetail();

			rsd.setSchDate(rs.getDate("SchDate"));
			rsd.setWaivedAmt(rs.getBigDecimal("WaivedAmt"));

			return rsd;

		}, receiptSeqId);
	}

	/**
	 * Method for fetching Summing up of all Disbursed Amounts by Date
	 */
	@Override
	public BigDecimal getReceiptAmountPerDay(String product, Date receiptDate, String receiptMode, long custID) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PRODUCTCATEGORY", product);
		source.addValue("RECEIVEDDATE", receiptDate);
		source.addValue("PAYMENTTYPE", receiptMode);
		source.addValue("CustID", custID);

		StringBuilder selectSql = new StringBuilder("SELECT SUM(AMOUNT) from FinReceiptDetail RD ");
		selectSql.append(" INNER JOIN FinReceiptHeader RH ON RH.RECEIPTID = RD.RECEIPTID ");
		selectSql.append(" INNER JOIN FinanceMain FM ON FM.FINREFERENCE = RH.REFERENCE ");
		selectSql.append(
				" WHERE RD.STATUS NOT IN ('C','B') AND RD.RECEIVEDDATE = :RECEIVEDDATE AND FM.PRODUCTCATEGORY = :PRODUCTCATEGORY AND FM.CUSTID = :CustID ");
		selectSql.append(" AND RD.PAYMENTTYPE = :PAYMENTTYPE ");
		logger.debug("selectSql: " + selectSql.toString());
		BigDecimal amount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}
		logger.debug("Leaving");
		return amount;
	}

	@Override
	public void updateFundingAcByReceiptID(long receiptID, long fundingAc, String type) {
		StringBuilder sql = new StringBuilder("Update FinReceiptDetail");
		sql.append(type);
		sql.append(" Set FundingAc = ?");
		sql.append(" Where ReceiptID = ? and PaymentType in (?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, fundingAc);
			ps.setLong(index++, receiptID);
			ps.setString(index++, DisbursementConstants.PAYMENT_TYPE_CHEQUE);
			ps.setString(index++, DisbursementConstants.PAYMENT_TYPE_DD);
		});

	}

	@Override
	public List<FinReceiptDetail> getFinReceiptDetailByReference(String reference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" rch.Reference, rch.ReceiptMode, rch.ReceiptPurpose, rch.TransactionRef");
		sql.append(", rch.ReceiptAmount, rch.ReceivedDate");
		sql.append(" From FinReceiptHeader rch");
		sql.append(" Inner Join FinReceiptDetail rcd on rcd.ReceiptID = rch.ReceiptID");
		sql.append(" Where Reference = :Reference");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinReceiptDetail rcd = new FinReceiptDetail();

			rcd.setReference(rs.getString("Reference"));
			// rcd.setReceiptMode(rs.getInt("ReceiptMode"));
			rcd.setReceiptPurpose(rs.getString("ReceiptPurpose"));
			rcd.setTransactionRef(rs.getString("TransactionRef"));
			rcd.setAmount(rs.getBigDecimal("ReceiptAmount"));
			rcd.setReceivedDate(rs.getDate("ReceivedDate"));

			return rcd;

		}, reference);
	}

	@Override
	public void cancelReceiptDetails(List<Long> receiptIdList) {
		StringBuilder sql = new StringBuilder("Update FinReceiptDetail");
		sql.append(" Set Status = ?");
		sql.append(" Where ReceiptID in (");
		sql.append(JdbcUtil.getInCondition(receiptIdList));
		sql.append(" )");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, "C");

			for (Long receiptId : receiptIdList) {
				ps.setLong(index++, receiptId);
			}

		});
	}

	@Override
	public List<FinReceiptDetail> getDMFinReceiptDetailByFinRef(String finReference, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("Status", "C");

		List<FinReceiptDetail> finReceiptDetailsList;

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(
				" select T1.receiptid, T2.receiptseqid, T2.receipttype ,T2.paymentto, T1.ReceiptMode, T2.payagainstid, ");
		selectSql.append(" T2.amount, T2.favournumber, T1.ValueDate, T2.bankcode, T2.favourname, T1.Depositdate, ");
		selectSql.append(
				" T2.depositno, T2.paymentref, T1.TransactionRef, T2.chequeacno, T2.fundingac, T1.ReceivedDate, ");
		selectSql.append(" T2.status, T2.payorder, T2.logkey ");
		selectSql.append(" From FinReceiptHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" T1");
		selectSql.append(" Inner Join FinReceiptDetail");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" T2 on T1.ReceiptID = T2.ReceiptID");
		selectSql.append(" where T2.Status <> :Status And T1.Reference = :FinReference");
		selectSql.append(" Order by T2.receiptid, T2.receiptseqid");

		logger.trace(Literal.SQL + selectSql.toString());

		RowMapper<FinReceiptDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinReceiptDetail.class);

		try {
			finReceiptDetailsList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finReceiptDetailsList = new ArrayList<FinReceiptDetail>();
		}
		logger.debug("Leaving");
		return finReceiptDetailsList;
	}

	/**
	 * 29-10-2018, Ticket id:124998 get receipt ID at receipt mode status A and at schd purpose return boolean condition
	 */
	@Override
	public long getReceiptIdByReceiptDetails(FinReceiptHeader receiptHeader, String purpose) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select T1.receiptid ");
		selectSql.append(" From FinReceiptHeader T1");
		selectSql.append(" Inner Join FinReceiptDetail T2 on T1.ReceiptID = T2.RECEIPTID");
		selectSql.append(
				" where Reference = :Finreference and T1.ReceiptPurpose = :ReceiptPurpose and T1.RECEIPTMODE = :RECEIPTMODE ");
		selectSql.append(
				" and  T2.FundingAc = :FundingAc and T2.FAVOURNUMBER = :FAVOURNUMBER and T1.RECEIPTMODESTATUS = :RECEIPTMODESTATUS  ");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Finreference", receiptHeader.getReference());
		source.addValue("ReceiptPurpose", purpose);
		source.addValue("RECEIPTMODE", receiptHeader.getReceiptMode());
		source.addValue("FundingAc", receiptHeader.getReceiptDetails().get(0).getFundingAc());
		source.addValue("FAVOURNUMBER", receiptHeader.getReceiptDetails().get(0).getFavourNumber());
		source.addValue("RECEIPTMODESTATUS", RepayConstants.PAYSTATUS_APPROVED);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		long count = 0;
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (DataAccessException e) {
			logger.debug(e);
			count = 0;
		}
		return count;
	}

	/**
	 * 29-10-2018, Ticket id:124998 check receipt details exits with given by favour number return boolean condition
	 */
	@Override
	public boolean isFinReceiptDetailExitsByFavourNo(FinReceiptHeader receiptHeader, String purpose) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select count(*) ");
		selectSql.append(" From FinReceiptHeader T1");
		selectSql.append(" Inner Join FinReceiptDetail T2 on T1.ReceiptID = T2.RECEIPTID");
		selectSql.append(
				" where Reference = :Finreference and T1.ReceiptPurpose = :ReceiptPurpose and T1.RECEIPTMODE = :RECEIPTMODE ");
		selectSql.append(" and  T2.BANKCODE = :BANKCODE and T2.FAVOURNUMBER = :FAVOURNUMBER ");
		selectSql.append(" and  T1.receiptmodeStatus not in ('C')");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Finreference", receiptHeader.getReference());
		source.addValue("ReceiptPurpose", purpose);
		source.addValue("RECEIPTMODE", receiptHeader.getReceiptMode());
		source.addValue("BANKCODE", receiptHeader.getReceiptDetails().get(0).getBankCode());
		source.addValue("FAVOURNUMBER", receiptHeader.getReceiptDetails().get(0).getFavourNumber());

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		int count = 0;
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.debug(e);
			count = 0;
		}

		if (count > 0) {
			return true;
		}

		return false;
	}

	/**
	 * 29-10-2018, Ticket id:124998 check receipt details exits with given by Transaction Ref return boolean condition
	 */
	@Override
	public boolean isFinReceiptDetailExitsByTransactionRef(FinReceiptHeader receiptHeader, String purpose) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select count(*) ");
		selectSql.append(" From FinReceiptHeader T1");
		selectSql.append(" Inner Join FinReceiptDetail T2 on T1.ReceiptID = T2.RECEIPTID");
		selectSql.append(
				" where Reference = :Finreference and T1.ReceiptPurpose = :ReceiptPurpose and T1.RECEIPTMODE = :RECEIPTMODE ");
		selectSql.append(" and  T2.TRANSACTIONREF = :TRANSACTIONREF ");
		selectSql.append(" and  T1.receiptmodeStatus not in ('C')");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Finreference", receiptHeader.getReference());
		source.addValue("ReceiptPurpose", purpose);
		source.addValue("RECEIPTMODE", receiptHeader.getReceiptMode());
		source.addValue("TRANSACTIONREF", receiptHeader.getReceiptDetails().get(0).getTransactionRef());

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		int count = 0;
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.debug(e);
			count = 0;
		}

		if (count > 0) {
			return true;
		}

		return false;
	}

	/**
	 * updating status Ticket id:124998
	 */
	@Override
	public void updateReceiptStatusByReceiptId(long receiptID, String status) {
		String sql = "Update FinReceiptDetail Set Status = ? Where ReceiptID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, status);
			ps.setLong(2, receiptID);
		});
	}

	@Override
	public boolean isDuplicateReceipt(String finReference, String txnReference, BigDecimal receiptAmount) {
		StringBuilder selectSql = new StringBuilder();
		boolean isDuplicate = false;
		selectSql.append(" Select count(*) ");
		selectSql.append(" From FinReceiptHeader T1 Inner Join FinReceiptDetail T2 ON ");
		selectSql.append(" T1.ReceiptID = T2.RECEIPTID where Reference = :Reference AND ");
		selectSql.append(" TransactionRef = :TransactionRef AND ReceiptAmount = :ReceiptAmount");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		source.addValue("TransactionRef", txnReference);
		source.addValue("ReceiptAmount", receiptAmount);

		logger.debug("selectSql: " + selectSql.toString());

		try {
			int count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
			if (count > 0) {
				isDuplicate = true;
				logger.debug("Duplcate Receipt Transaction");
			}
		} catch (DataAccessException e) {
			logger.debug(e);
		}

		return isDuplicate;
	}

	@Override
	public BigDecimal getReceiptAmountPerDay(Date receiptDate, String receiptMode, long custID) {

		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RECEIVEDDATE", receiptDate);
		source.addValue("PAYMENTTYPE", receiptMode);
		source.addValue("CustID", custID);

		StringBuilder selectSql = new StringBuilder("SELECT SUM(AMOUNT) from FinReceiptDetail_VIEW RD ");
		selectSql.append(" INNER JOIN FinanceMain FM ON FM.FINREFERENCE = RD.REFERENCE ");
		selectSql.append(
				" WHERE RD.STATUS NOT IN ('C','B') AND RD.RECEIVEDDATE = :RECEIVEDDATE AND FM.CUSTID = :CustID ");
		selectSql.append(" AND RD.PAYMENTTYPE = :PAYMENTTYPE ");
		logger.debug("selectSql: " + selectSql.toString());
		BigDecimal amount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}
		logger.debug("Leaving");
		return amount;
	}

	@Override
	public BigDecimal getFinReceiptDetailsByFinRef(String finReference) {
		logger.debug("Entering");

		BigDecimal totalAmount = BigDecimal.ZERO;

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("Status", "C");
		source.addValue("ReceiptPurpose", "FeePayment");

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" select SUM(Amount) Amount from ( select coalesce(sum(T2.AMOUNT), 0) Amount ");
		selectSql.append(" From FinReceiptHeader_Temp T1 ");
		selectSql.append(" Inner Join FinReceiptDetail_Temp T2 on T1.ReceiptID = T2.RECEIPTID");
		selectSql.append(" where T1.Reference = :FinReference");
		selectSql.append(" UNION ALL ");
		selectSql.append(" select coalesce(sum(T2.AMOUNT), 0) Amount ");
		selectSql.append(" From FinReceiptHeader T1 ");
		selectSql.append(" Inner Join FinReceiptDetail T2 on T1.ReceiptID = T2.RECEIPTID");
		selectSql.append(
				" where ReceiptPurpose = :ReceiptPurpose And T2.Status <> :Status And T1.Reference = :FinReference and NOT (EXISTS ( SELECT 1 FROM FinReceiptHeader_Temp WHERE FinReceiptHeader_Temp.Reference = T1.Reference))) T");

		logger.trace(Literal.SQL + selectSql.toString());

		try {
			totalAmount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			totalAmount = BigDecimal.ZERO;
		}

		logger.debug("Leaving");

		return totalAmount;
	}

	@Override
	public BigDecimal getUtilizedPartPayAmtByDate(FinReceiptHeader receiptHeader, Date startDate, Date endDate) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select sum(PriAmount) from FinRepayHeader FRH ");
		selectSql.append(" INNER JOIN FinReceiptDetail RCD ON FRH.ReceiptSeqID = RCD.ReceiptSeqID ");
		selectSql.append(" INNER JOIN FinReceiptHeader RCH ON RCD.ReceiptID = RCH.ReceiptID ");
		selectSql.append(" WHERE FRH.FinReference = :Finreference ");
		selectSql.append(" AND FRH.Finevent = :FINEVENT AND RCH.ReceiptModeStatus NOT IN ('B', 'C') ");
		selectSql.append(" AND RCD.ReceivedDate >= :STARTDATE AND  RCD.ReceivedDate <= :ENDDATE");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Finreference", receiptHeader.getReference());
		source.addValue("STARTDATE", startDate);
		source.addValue("ENDDATE", endDate);
		source.addValue("FINEVENT", FinServiceEvent.EARLYRPY);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		BigDecimal value = BigDecimal.ZERO;
		try {
			value = this.jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);
		} catch (DataAccessException e) {
			logger.debug(e);
			value = BigDecimal.ZERO;
		}
		if (value == null) {
			return BigDecimal.ZERO;
		}
		return value;
	}

	@Override
	public Date getMaxReceiptDate(String finReference, String receiptPurpose, TableType tableType) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		source.addValue("ReceiptPurpose", receiptPurpose);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select MAX(VALUEDATE)  From FinReceiptHeader");
		selectSql.append(tableType.getSuffix());
		selectSql.append(" where Reference = :Reference AND ReceiptPurpose = :ReceiptPurpose ");

		logger.debug(Literal.SQL + selectSql.toString());

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Date.class);
	}

	@Override
	public List<FinReceiptDetail> getNonLanReceiptHeader(long receiptID, String type) {

		StringBuilder sql = new StringBuilder(
				"Select ReceiptID, ReceiptSeqID, ReceiptType, PaymentTo, PaymentType, PayAgainstID , ");
		sql.append(" Amount, FavourNumber, ValueDate, BankCode, FavourName, DepositDate, DepositNo, PaymentRef, ");
		sql.append(" TransactionRef, ChequeAcNo, FundingAc, ReceivedDate, Status, PayOrder, LogKey, ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(" BankCodeDesc, fundingAcCode, FundingAcDesc, PartnerBankAc, PartnerBankAcType  ");
		}
		sql.append(" From NonLanFinReceiptDetail");
		sql.append(StringUtils.trim(type));
		sql.append(" Where ReceiptID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, receiptID);
				}
			}, new RowMapper<FinReceiptDetail>() {
				@Override
				public FinReceiptDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinReceiptDetail frd = new FinReceiptDetail();
					frd.setReceiptID(rs.getLong("ReceiptID"));
					frd.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
					frd.setReceiptType(rs.getString("ReceiptType"));
					frd.setPaymentTo(rs.getString("PaymentTo"));
					frd.setPaymentType(rs.getString("PaymentType"));
					frd.setPayAgainstID(rs.getLong("PayAgainstID"));
					frd.setAmount(rs.getBigDecimal("Amount"));
					frd.setFavourNumber(rs.getString("FavourNumber"));
					frd.setValueDate(rs.getTimestamp("ValueDate"));
					frd.setBankCode(rs.getString("BankCode"));
					frd.setFavourName(rs.getString("FavourName"));
					frd.setDepositDate(rs.getTimestamp("DepositDate"));
					frd.setDepositNo(rs.getString("DepositNo"));
					frd.setPaymentRef(rs.getString("PaymentRef"));
					frd.setTransactionRef(rs.getString("TransactionRef"));
					frd.setChequeAcNo(rs.getString("ChequeAcNo"));
					frd.setFundingAc(rs.getLong("FundingAc"));
					frd.setReceivedDate(rs.getTimestamp("ReceivedDate"));
					frd.setStatus(rs.getString("Status"));
					frd.setPayOrder(rs.getInt("PayOrder"));
					frd.setLogKey(rs.getLong("LogKey"));
					if (StringUtils.trimToEmpty(type).contains("View")) {
						frd.setBankCodeDesc(rs.getString("BankCodeDesc"));
						frd.setFundingAcCode(rs.getString("fundingAcCode"));
						frd.setFundingAcDesc(rs.getString("FundingAcDesc"));
						frd.setPartnerBankAc(rs.getString("PartnerBankAc"));
						frd.setPartnerBankAcType(rs.getString("PartnerBankAcType"));
					}

					return frd;
				}
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.info(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public String getReceiptSourceAccType(String receiptSource) {
		logger.info(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select Account_Type From Receipt_Source_Account_Types ");
		sql.append(" Where Receipt_Source = ? ");

		logger.trace(Literal.SQL + sql.toString());
		String accType = null;
		try {
			accType = this.jdbcOperations.queryForObject(sql.toString(), new Object[] { receiptSource },
					new RowMapper<String>() {
						@Override
						public String mapRow(ResultSet rs, int rowNum) throws SQLException {
							return rs.getString("Account_Type");
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.info(Literal.LEAVING);
		return accType;
	}
}