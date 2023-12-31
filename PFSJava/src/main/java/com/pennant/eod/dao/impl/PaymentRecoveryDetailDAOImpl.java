package com.pennant.eod.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.eod.beans.PaymentRecoveryDetail;
import com.pennant.eod.dao.PaymentRecoveryDetailDAO;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class PaymentRecoveryDetailDAOImpl extends BasicDao<PaymentRecoveryDetail> implements PaymentRecoveryDetailDAO {
	private static Logger logger = LogManager.getLogger(PaymentRecoveryDetailDAOImpl.class);

	@Override
	public void save(List<PaymentRecoveryDetail> detail) {
		logger.debug(" Entering ");

		StringBuilder insertSql = new StringBuilder("Insert Into PaymentRecoveryDetail");
		insertSql.append(
				"(BatchRefNumber,TransactionReference,PrimaryDebitAccount,SecondaryDebitAccounts,CreditAccount,ScheduleDate,");
		insertSql.append(
				"FinanceReference,CustomerReference,DebitCurrency,CreditCurrency,PaymentAmount,TransactionPurpose,FinanceBranch,FinanceType,");
		insertSql.append(
				"FinancePurpose,SysTranRef,PrimaryAcDebitAmt,SecondaryAcDebitAmt,PaymentStatus,Priority,FinRpyFor,FinEvent) Values ");
		insertSql.append(
				"(:BatchRefNumber, :TransactionReference, :PrimaryDebitAccount, :SecondaryDebitAccounts, :CreditAccount, :ScheduleDate, ");
		insertSql.append(
				":FinanceReference, :CustomerReference, :DebitCurrency, :CreditCurrency, :PaymentAmount, :TransactionPurpose, :FinanceBranch, ");
		insertSql.append(
				":FinanceType, :FinancePurpose, :SysTranRef, :PrimaryAcDebitAmt, :SecondaryAcDebitAmt, :PaymentStatus,:Priority,:FinRpyFor,:FinEvent)");
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(detail.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

	}

	@Override
	public void update(List<PaymentRecoveryDetail> detail) {
		logger.debug(" Entering ");
		StringBuilder insertSql = new StringBuilder("Update PaymentRecoveryDetail");
		insertSql.append(
				" set SysTranRef=:SysTranRef, PrimaryAcDebitAmt= :PrimaryAcDebitAmt, SecondaryAcDebitAmt=:SecondaryAcDebitAmt,");
		insertSql.append(" PaymentStatus=:PaymentStatus where TransactionReference=:TransactionReference");
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(detail.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

	}

	@Override
	public void update(PaymentRecoveryDetail detail) {
		logger.debug(" Entering ");
		StringBuilder insertSql = new StringBuilder("Update PaymentRecoveryDetail");
		insertSql.append(
				" set SysTranRef=:SysTranRef, PrimaryAcDebitAmt= :PrimaryAcDebitAmt,SecondaryAcDebitAmt=:SecondaryAcDebitAmt,");
		insertSql.append("PaymentStatus=:PaymentStatus where TransactionReference=:TransactionReference");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

	}

	@Override
	public List<PaymentRecoveryDetail> getPaymentRecoveryDetails(String batchReferenceNumber) {
		logger.debug(" Entering ");
		PaymentRecoveryDetail header = new PaymentRecoveryDetail();
		header.setBatchRefNumber(batchReferenceNumber);

		StringBuilder selectSql = new StringBuilder("SELECT * From PaymentRecoveryDetail");
		selectSql.append(" where BatchRefNumber=:BatchRefNumber order by PrimaryDebitAccount,priority ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<PaymentRecoveryDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(PaymentRecoveryDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<PaymentRecoveryDetail> getPaymentRecoveryByid(String bathRef, String finreference, Date scheduleDate,
			String finEvent) {
		logger.debug(" Entering ");
		PaymentRecoveryDetail header = new PaymentRecoveryDetail();
		header.setBatchRefNumber(bathRef);
		header.setFinanceReference(finreference);
		header.setScheduleDate(scheduleDate);
		header.setFinEvent(finEvent);
		StringBuilder selectSql = new StringBuilder("SELECT * From PaymentRecoveryDetail");
		selectSql.append(
				" where BatchRefNumber=:BatchRefNumber and FinanceReference = :FinanceReference and ScheduleDate=:ScheduleDate");
		if (!StringUtils.isBlank(finEvent)) {
			selectSql.append("  and FinEvent=:FinEvent");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<PaymentRecoveryDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(PaymentRecoveryDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<PaymentRecoveryDetail> getPaymentRecoveryByCustomer(String bathRef, String customerID) {
		logger.debug(" Entering ");
		PaymentRecoveryDetail header = new PaymentRecoveryDetail();
		header.setBatchRefNumber(bathRef);
		header.setCustomerReference(customerID);

		StringBuilder selectSql = new StringBuilder("SELECT * From PaymentRecoveryDetail");
		selectSql.append(" where BatchRefNumber=:BatchRefNumber and CustomerReference = :CustomerReference");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<PaymentRecoveryDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(PaymentRecoveryDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
}
