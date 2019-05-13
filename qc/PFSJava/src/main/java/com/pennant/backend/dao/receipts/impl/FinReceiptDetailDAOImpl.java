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
 * FileName    		:  FinanceRepaymentsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.receipts.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinReceiptDetailDAOImpl extends SequenceDao<FinReceiptDetail> implements FinReceiptDetailDAO {
	private static Logger logger = Logger.getLogger(FinReceiptDetailDAOImpl.class);

	public FinReceiptDetailDAOImpl() {
		super();
	}

	@Override
	public List<FinReceiptDetail> getReceiptHeaderByID(long receiptID, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder selectSql = new StringBuilder(
				"Select ReceiptID , ReceiptSeqID , ReceiptType , PaymentTo , PaymentType , PayAgainstID  , ");
		selectSql.append(
				" Amount  , FavourNumber , ValueDate , BankCode , FavourName , DepositDate , DepositNo , PaymentRef , ");
		selectSql.append(
				" TransactionRef , ChequeAcNo , FundingAc , ReceivedDate , Status , PayOrder, LogKey, ValueDate ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" ,BankCodeDesc, fundingAcCode, FundingAcDesc, PartnerBankAc, PartnerBankAcType  ");
			if (StringUtils.trimToEmpty(type).contains("AView")) {
				selectSql.append(" ,FeeTypeCode,FeeTypeDesc ");
			}
		}
		selectSql.append(" From FinReceiptDetail");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where ReceiptID =:ReceiptID ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinReceiptDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptDetail.class);

		List<FinReceiptDetail> receiptList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		logger.debug("Leaving");
		return receiptList;
	}

	@Override
	public long save(FinReceiptDetail receiptDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);
		if (receiptDetail.getId() == 0 || receiptDetail.getId() == Long.MIN_VALUE) {
			receiptDetail.setId(getNextValue("SeqFinReceiptDetail"));
			logger.debug("get NextID:" + receiptDetail.getId());
		}

		StringBuilder sql = new StringBuilder("Insert Into FinReceiptDetail");
		sql.append(tableType.getSuffix());
		sql.append(" (ReceiptID, ReceiptSeqID, ReceiptType, PaymentTo, PaymentType, PayAgainstID, Amount");
		sql.append(", FavourNumber, ValueDate, BankCode, FavourName, DepositDate, DepositNo, PaymentRef");
		sql.append(", TransactionRef, ChequeAcNo, FundingAc, ReceivedDate, Status, PayOrder, LogKey)");
		sql.append(" Values(:ReceiptID, :ReceiptSeqID, :ReceiptType, :PaymentTo, :PaymentType, :PayAgainstID, :Amount");
		sql.append(", :FavourNumber, :ValueDate, :BankCode, :FavourName, :DepositDate, :DepositNo, :PaymentRef");
		sql.append(", :TransactionRef, :ChequeAcNo, :FundingAc, :ReceivedDate, :Status, :PayOrder, :LogKey)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptDetail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
		return receiptDetail.getId();
	}

	@Override
	public void deleteByReceiptID(long receiptID, TableType tableType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder deleteSql = new StringBuilder(" DELETE From FinReceiptDetail");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" where ReceiptID=:ReceiptID ");

		logger.debug("selectSql: " + deleteSql.toString());
		this.jdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public void updateReceiptStatus(long receiptID, long receiptSeqID, String status) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("ReceiptSeqID", receiptSeqID);
		source.addValue("Status", status);

		StringBuilder updateSql = new StringBuilder("Update FinReceiptDetail");
		updateSql.append(" Set Status=:Status ");
		updateSql.append(" Where ReceiptID = :ReceiptID AND ReceiptSeqID = :ReceiptSeqID ");

		logger.debug("updateSql: " + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public int getReceiptHeaderByBank(String bankCode, String type) {
		FinReceiptDetail finReceiptDetail = new FinReceiptDetail();
		finReceiptDetail.setBankCode(bankCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(BankCode)");
		selectSql.append(" From FinReceiptDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankCode =:BankCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finReceiptDetail);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public List<FinReceiptDetail> getFinReceiptDetailByFinRef(String finReference, long custId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("Status", "C");
		source.addValue("ReceiptPurpose", "FeePayment");
		source.addValue("RECAGAINST1", RepayConstants.RECEIPTTO_FINANCE);
		source.addValue("RECAGAINST2", RepayConstants.RECEIPTTO_CUSTOMER);
		source.addValue("CustId", String.valueOf(custId));

		List<FinReceiptDetail> finReceiptDetailsList;

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(
				" Select T1.RECEIPTID, T2.TRANSACTIONREF, T2.FAVOURNUMBER ,T1.RECEIPTMODE PaymentType, T2.AMOUNT ");
		selectSql.append(" From FINRECEIPTHEADER T1 ");
		selectSql.append(" Inner Join FINRECEIPTDETAIL T2 on T1.ReceiptID = T2.RECEIPTID");
		selectSql.append(" where ReceiptPurpose = :ReceiptPurpose And T2.Status <> :Status");
		selectSql.append(" And ((RECAGAINST = :RECAGAINST1 and T1.Reference = :FinReference) OR ");
		selectSql.append(
				" (RECAGAINST = :RECAGAINST2 and T1.Reference = :CustId And T1.RECEIPTID Not In (Select Distinct ReceiptId from FINFEERECEIPTS_View where FinReference <> :FinReference)))");

		logger.trace(Literal.SQL + selectSql.toString());

		RowMapper<FinReceiptDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptDetail.class);

		try {
			finReceiptDetailsList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finReceiptDetailsList = new ArrayList<FinReceiptDetail>();
		}

		logger.debug("Leaving");

		return finReceiptDetailsList;
	}

	@Override
	public Date getMaxReceivedDateByReference(String finReference) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select MAX(T2.receivedDate)");
		selectSql.append(" From FINRECEIPTHEADER T1 ");
		selectSql.append(" Inner Join FINRECEIPTDETAIL T2 on T1.ReceiptID = T2.RECEIPTID");
		selectSql.append(" where T1.Reference = :FinReference AND T2.Status NOT IN('C','B') ");

		logger.debug("selectSql: " + selectSql.toString());
		Date maxReceivedDate = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Date.class);
		logger.debug("Leaving");
		return maxReceivedDate;
	}

	@Override
	public List<RepayScheduleDetail> fetchRepaySchduleList(long receiptSeqId) {
		StringBuilder selectSql = new StringBuilder();
		selectSql
				.append("select  a.SCHDATE,a.WAIVEDAMT from FINREPAYSCHEDULEDETAIL  a,FINREPAYHEADER b where a.repayID=b.REPAYID and b.RECEIPTSEQID="
						+ receiptSeqId);

		BeanPropertySqlParameterSource beanParamSource = new BeanPropertySqlParameterSource(new FinReceiptDetail());

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<RepayScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(RepayScheduleDetail.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), beanParamSource, typeRowMapper);
	}

	@Override
	public List<FinReceiptDetail> getFinReceiptDetailByFinReference(String finReference) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" Select T1.Reference,T2.PaymentType,T1.ReceiptPurpose, T2.TRANSACTIONREF, T2.AMOUNT, T2.ReceivedDate");
		selectSql.append(" From FINRECEIPTHEADER T1");
		selectSql.append(" Inner Join FINRECEIPTDETAIL T2 on T1.ReceiptID = T2.RECEIPTID");
		selectSql.append(" where Reference = '" + finReference + "'");

		BeanPropertySqlParameterSource beanParamSource = new BeanPropertySqlParameterSource(new FinReceiptDetail());

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinReceiptDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptDetail.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), beanParamSource, typeRowMapper);
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

		StringBuilder selectSql = new StringBuilder("SELECT SUM(AMOUNT) from FINRECEIPTDETAIL RD ");
		selectSql.append(" INNER JOIN FINRECEIPTHEADER RH ON RH.RECEIPTID = RD.RECEIPTID ");
		selectSql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = RH.REFERENCE ");
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
		logger.debug("Entering");

		List<String> paymentTypes = new ArrayList<String>();
		paymentTypes.add(DisbursementConstants.PAYMENT_TYPE_CHEQUE);
		paymentTypes.add(DisbursementConstants.PAYMENT_TYPE_DD);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("FundingAc", fundingAc);
		source.addValue("PaymentType", paymentTypes);

		StringBuilder updateSql = new StringBuilder("Update FinReceiptDetail");
		updateSql.append(type);
		updateSql.append(" Set FundingAc = :FundingAc");
		updateSql.append(" Where ReceiptID = :ReceiptID And PaymentType in (:PaymentType)");

		logger.debug("selectSql: " + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
	}

	@Override
	public List<FinReceiptDetail> getFinReceiptDetailByExternalReference(String extReference) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" Select T1.Reference,T2.PaymentType,T1.ReceiptPurpose, T2.TRANSACTIONREF, T2.AMOUNT, T2.ReceivedDate");
		selectSql.append(" From FINRECEIPTHEADER T1");
		selectSql.append(" Inner Join FINRECEIPTDETAIL T2 on T1.ReceiptID = T2.RECEIPTID");
		selectSql.append(" where extReference = '" + extReference + "'");

		BeanPropertySqlParameterSource beanParamSource = new BeanPropertySqlParameterSource(new FinReceiptDetail());

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinReceiptDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptDetail.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), beanParamSource, typeRowMapper);
	}

	@Override
	public void cancelReceiptDetails(List<Long> receiptID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder updateSql = new StringBuilder("Update FinReceiptDetail");
		updateSql.append(" Set Status='C' ");
		updateSql.append(" Where ReceiptID IN  (:ReceiptID)  ");

		logger.debug("updateSql: " + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
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
				" select T2.receiptid, T2.receiptseqid, T2.receipttype ,T2.paymentto, T2.paymenttype, T2.payagainstid, ");
		selectSql.append(" T2.amount, T2.favournumber, T2.valuedate, T2.bankcode, T2.favourname, T2.depositdate, ");
		selectSql.append(
				" T2.depositno, T2.paymentref, T2.transactionref, T2.chequeacno, T2.fundingac, T2.receiveddate, ");
		selectSql.append(" T2.status, T2.payorder, T2.logkey ");
		selectSql.append(" From FINRECEIPTHEADER");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" T1");
		selectSql.append(" Inner Join FINRECEIPTDETAIL");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" T2 on T1.ReceiptID = T2.ReceiptID");
		selectSql.append(" where T2.Status <> :Status And T1.Reference = :FinReference");
		selectSql.append(" Order by T2.receiptid, T2.receiptseqid");

		logger.trace(Literal.SQL + selectSql.toString());

		RowMapper<FinReceiptDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptDetail.class);

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
		selectSql.append(" From FINRECEIPTHEADER T1");
		selectSql.append(" Inner Join FINRECEIPTDETAIL T2 on T1.ReceiptID = T2.RECEIPTID");
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
		selectSql.append(" From FINRECEIPTHEADER T1");
		selectSql.append(" Inner Join FINRECEIPTDETAIL T2 on T1.ReceiptID = T2.RECEIPTID");
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
		selectSql.append(" From FINRECEIPTHEADER T1");
		selectSql.append(" Inner Join FINRECEIPTDETAIL T2 on T1.ReceiptID = T2.RECEIPTID");
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
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("Status", status);

		StringBuilder updateSql = new StringBuilder("Update FinReceiptDetail");
		updateSql.append(" Set Status=:Status ");
		updateSql.append(" Where ReceiptID =:ReceiptID");

		logger.debug("updateSql: " + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public boolean isDuplicateReceipt(String finReference, String txnReference, BigDecimal receiptAmount) {
		StringBuilder selectSql = new StringBuilder();
		boolean isDuplicate = false;
		selectSql.append(" Select count(*) ");
		selectSql.append(" From FINRECEIPTHEADER T1 Inner Join FINRECEIPTDETAIL T2 ON ");
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

		StringBuilder selectSql = new StringBuilder("SELECT SUM(AMOUNT) from FINRECEIPTDETAIL_VIEW RD ");
		selectSql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = RD.REFERENCE ");
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
		selectSql.append(" From FINRECEIPTHEADER_Temp T1 ");
		selectSql.append(" Inner Join FINRECEIPTDETAIL_Temp T2 on T1.ReceiptID = T2.RECEIPTID");
		selectSql.append(" where T1.Reference = :FinReference");
		selectSql.append(" UNION ALL ");
		selectSql.append(" select coalesce(sum(T2.AMOUNT), 0) Amount ");
		selectSql.append(" From FINRECEIPTHEADER T1 ");
		selectSql.append(" Inner Join FINRECEIPTDETAIL T2 on T1.ReceiptID = T2.RECEIPTID");
		selectSql.append(
				" where ReceiptPurpose = :ReceiptPurpose And T2.Status <> :Status And T1.Reference = :FinReference and NOT (EXISTS ( SELECT 1 FROM FINRECEIPTHEADER_Temp WHERE FINRECEIPTHEADER_Temp.Reference = T1.Reference))) T");

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
		source.addValue("FINEVENT", FinanceConstants.FINSER_EVENT_EARLYRPY);

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
}