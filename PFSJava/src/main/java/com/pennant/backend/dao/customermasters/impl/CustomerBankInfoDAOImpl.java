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
 * * FileName : CustomerBankInfoDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 * *
 * Modified Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.customermasters.CustomerBankInfoDAO;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.BankInfoSubDetail;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>CustomerBankInfo model</b> class.<br>
 * 
 */
public class CustomerBankInfoDAOImpl extends SequenceDao<CustomerBankInfo> implements CustomerBankInfoDAO {
	private static Logger logger = LogManager.getLogger(CustomerBankInfoDAOImpl.class);

	public CustomerBankInfoDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer Bank details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustomerBankInfo
	 */

	@Override
	public CustomerBankInfo getCustomerBankInfoById(long bankId, String type) {
		logger.debug("Entering");
		CustomerBankInfo customerBankInfo = new CustomerBankInfo();
		customerBankInfo.setBankId(bankId);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT bankId, CustID, BankName, AccountNumber, AccountType,SalaryAccount,");
		sql.append(
				" CreditTranNo, CreditTranAmt, CreditTranAvg, DebitTranNo, DebitTranAmt, CashDepositNo, CashDepositAmt,");
		sql.append(" CashWithdrawalNo, CashWithdrawalAmt, ChqDepositNo, ChqDepositAmt, ChqIssueNo, ChqIssueAmt,");
		sql.append(" InwardChqBounceNo, OutwardChqBounceNo, EodBalMin, EodBalMax, EodBalAvg,  BankBranch, FromDate,");
		sql.append(
				" ToDate, RepaymentFrom, NoOfMonthsBanking, LwowRatio, CcLimit, TypeOfBanks, AccountOpeningDate,AddToBenficiary,");
		sql.append(" bankBranchID, AccountHolderName, phoneNumber,");
		if (type.contains("View")) {
			sql.append(" lovDescBankName,lovDescAccountType,Ifsc,");
			sql.append(" City, Micr, BranchCode,"); // From HL
		}
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  CustomerBankInfo");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankId = :BankId");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);
		RowMapper<CustomerBankInfo> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerBankInfo.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public List<CustomerBankInfo> getBankInfoByCustomer(final long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BankId, CustID, BankName, AccountNumber, AccountType, SalaryAccount, CreditTranNo");
		sql.append(", CreditTranAmt, CreditTranAvg, DebitTranNo, DebitTranAmt, CashDepositNo, CashDepositAmt");
		sql.append(", CashWithdrawalNo, CashWithdrawalAmt, ChqDepositNo, ChqDepositAmt, ChqIssueNo");
		sql.append(", ChqIssueAmt, InwardChqBounceNo, OutwardChqBounceNo, EodBalMin, EodBalMax, EodBalAvg");
		sql.append(", BankBranch, FromDate, ToDate, RepaymentFrom, NoOfMonthsBanking, LwowRatio, CcLimit");
		sql.append(", TypeOfBanks, AccountOpeningDate, AddToBenficiary, BankBranchID, AccountHolderName");
		sql.append(", PhoneNumber, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescBankName, LovDescAccountType, IFSC");
			sql.append(", City, Micr, BranchCode"); // From HL
		}
		sql.append(" from CustomerBankInfo");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, id);
		}, (rs, rowNum) -> {
			CustomerBankInfo cbi = new CustomerBankInfo();

			cbi.setBankId(rs.getLong("BankId"));
			cbi.setCustID(rs.getLong("CustID"));
			cbi.setBankName(rs.getString("BankName"));
			cbi.setAccountNumber(rs.getString("AccountNumber"));
			cbi.setAccountType(rs.getString("AccountType"));
			cbi.setSalaryAccount(rs.getBoolean("SalaryAccount"));
			cbi.setCreditTranNo(rs.getInt("CreditTranNo"));
			cbi.setCreditTranAmt(rs.getBigDecimal("CreditTranAmt"));
			cbi.setCreditTranAvg(rs.getBigDecimal("CreditTranAvg"));
			cbi.setDebitTranNo(rs.getInt("DebitTranNo"));
			cbi.setDebitTranAmt(rs.getBigDecimal("DebitTranAmt"));
			cbi.setCashDepositNo(rs.getInt("CashDepositNo"));
			cbi.setCashDepositAmt(rs.getBigDecimal("CashDepositAmt"));
			cbi.setCashWithdrawalNo(rs.getInt("CashWithdrawalNo"));
			cbi.setCashWithdrawalAmt(rs.getBigDecimal("CashWithdrawalAmt"));
			cbi.setChqDepositNo(rs.getInt("ChqDepositNo"));
			cbi.setChqDepositAmt(rs.getBigDecimal("ChqDepositAmt"));
			cbi.setChqIssueNo(rs.getInt("ChqIssueNo"));
			cbi.setChqIssueAmt(rs.getBigDecimal("ChqIssueAmt"));
			cbi.setInwardChqBounceNo(rs.getInt("InwardChqBounceNo"));
			cbi.setOutwardChqBounceNo(rs.getInt("OutwardChqBounceNo"));
			cbi.setEodBalMin(rs.getBigDecimal("EodBalMin"));
			cbi.setEodBalMax(rs.getBigDecimal("EodBalMax"));
			cbi.setEodBalAvg(rs.getBigDecimal("EodBalAvg"));
			cbi.setBankBranch(rs.getString("BankBranch"));
			cbi.setFromDate(rs.getTimestamp("FromDate"));
			cbi.setToDate(rs.getTimestamp("ToDate"));
			cbi.setRepaymentFrom(rs.getString("RepaymentFrom"));
			cbi.setNoOfMonthsBanking(rs.getInt("NoOfMonthsBanking"));
			cbi.setLwowRatio(rs.getString("LwowRatio"));
			cbi.setCcLimit(rs.getBigDecimal("CcLimit"));
			cbi.setTypeOfBanks(rs.getString("TypeOfBanks"));
			cbi.setAccountOpeningDate(rs.getTimestamp("AccountOpeningDate"));
			cbi.setAddToBenficiary(rs.getBoolean("AddToBenficiary"));
			cbi.setBankBranchID(JdbcUtil.getLong(rs.getObject("BankBranchID")));
			cbi.setAccountHolderName(rs.getString("AccountHolderName"));
			cbi.setPhoneNumber(rs.getString("PhoneNumber"));
			cbi.setVersion(rs.getInt("Version"));
			cbi.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cbi.setLastMntBy(rs.getLong("LastMntBy"));
			cbi.setRecordStatus(rs.getString("RecordStatus"));
			cbi.setRoleCode(rs.getString("RoleCode"));
			cbi.setNextRoleCode(rs.getString("NextRoleCode"));
			cbi.setTaskId(rs.getString("TaskId"));
			cbi.setNextTaskId(rs.getString("NextTaskId"));
			cbi.setRecordType(rs.getString("RecordType"));
			cbi.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				cbi.setLovDescBankName(rs.getString("LovDescBankName"));
				cbi.setLovDescAccountType(rs.getString("LovDescAccountType"));
				cbi.setiFSC(rs.getString("IFSC"));
			}

			return cbi;
		});

	}

	/**
	 * This method Deletes the Record from the CustomerBankInfo or CustomerBankInfo_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Customer Bank by key CustID
	 * 
	 * @param Customer Bank (customerBankInfo)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerBankInfo customerBankInfo, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder sql = new StringBuilder(" Delete From CustomerBankInfo");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankId =:BankId");

		logger.debug("deleteSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);

		try {
			recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the CustomerBankInfo or CustomerBankInfo_Temp for the Customer.
	 * 
	 * @param Customer Bank (customerBankInfo)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByCustomer(long custID, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		CustomerBankInfo customerBankInfo = new CustomerBankInfo();
		customerBankInfo.setCustID(custID);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerBankInfo");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CustomerBankInfo or CustomerBankInfo_Temp.
	 *
	 * save Customer Bank
	 * 
	 * @param Customer Bank (customerBankInfo)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerBankInfo customerBankInfo, String type) {
		logger.debug("Entering");

		if (customerBankInfo.getBankId() == Long.MIN_VALUE) {
			customerBankInfo.setBankId(getNextValue("SeqCustomerBankInfo"));
			logger.debug("get NextID:" + customerBankInfo.getBankId());
		}
		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into CustomerBankInfo");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (BankId,CustID, BankName, AccountNumber, AccountType,SalaryAccount,");
		sql.append(
				" CreditTranNo, CreditTranAmt, CreditTranAvg, DebitTranNo, DebitTranAmt, CashDepositNo, CashDepositAmt,");
		sql.append(" CashWithdrawalNo, CashWithdrawalAmt, ChqDepositNo, ChqDepositAmt, ChqIssueNo, ChqIssueAmt,");
		sql.append(" InwardChqBounceNo, OutwardChqBounceNo, EodBalMin, EodBalMax, EodBalAvg,  BankBranch, FromDate,");
		sql.append(
				" ToDate, RepaymentFrom, NoOfMonthsBanking, LwowRatio, CcLimit, TypeOfBanks, AccountOpeningDate,AddToBenficiary,");
		sql.append(" bankBranchID, AccountHolderName, PhoneNumber,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:BankId, :CustID, :BankName, :AccountNumber, :AccountType, :SalaryAccount,");
		sql.append(
				" :CreditTranNo, :CreditTranAmt, :CreditTranAvg, :DebitTranNo, :DebitTranAmt, :CashDepositNo, :CashDepositAmt,");
		sql.append(" :CashWithdrawalNo, :CashWithdrawalAmt, :ChqDepositNo, :ChqDepositAmt, :ChqIssueNo, :ChqIssueAmt,");
		sql.append(
				" :InwardChqBounceNo, :OutwardChqBounceNo, :EodBalMin, :EodBalMax, :EodBalAvg, :BankBranch, :FromDate,");
		sql.append(
				" :ToDate, :RepaymentFrom, :NoOfMonthsBanking, :LwowRatio, :CcLimit, :TypeOfBanks, :AccountOpeningDate, :AddToBenficiary,");
		sql.append(" :bankBranchID, :AccountHolderName, :PhoneNumber,");
		sql.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerBankInfo.getBankId();
	}

	/**
	 * This method updates the Record CustomerBankInfo or CustomerBankInfo_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer Bank by key CustID and Version
	 * 
	 * @param Customer Bank (customerBankInfo)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerBankInfo customerBankInfo, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" Update CustomerBankInfo");
		sql.append(StringUtils.trimToEmpty(type));

		sql.append(" Set AccountNumber = :AccountNumber, AccountType = :AccountType,SalaryAccount = :SalaryAccount,");
		sql.append(
				" CreditTranNo = :CreditTranNo, CreditTranAmt = :CreditTranAmt, CreditTranAvg = :CreditTranAvg, DebitTranNo = :DebitTranNo,");
		sql.append("DebitTranAmt = :DebitTranAmt, CashDepositNo = :CashDepositNo, CashDepositAmt = :CashDepositAmt,");
		sql.append(
				"CashWithdrawalNo = :CashWithdrawalNo,CashWithdrawalAmt = :CashWithdrawalAmt, ChqDepositNo = :ChqDepositNo, ChqDepositAmt = :ChqDepositAmt,");
		sql.append(
				"ChqIssueNo = :ChqIssueNo, ChqIssueAmt = :ChqIssueAmt, InwardChqBounceNo = :InwardChqBounceNo, OutwardChqBounceNo = :OutwardChqBounceNo, EodBalMin = :EodBalMin,");
		sql.append(
				"EodBalMax = :EodBalMax, EodBalAvg = :EodBalAvg, BankBranch = :BankBranch, FromDate = :FromDate, ToDate = :ToDate, RepaymentFrom = :RepaymentFrom,");
		sql.append(
				" NoOfMonthsBanking = :NoOfMonthsBanking, LwowRatio = :LwowRatio, CcLimit = :CcLimit, TypeOfBanks = :TypeOfBanks, AccountOpeningDate = :AccountOpeningDate,");
		sql.append(
				" AddToBenficiary = :AddToBenficiary, bankBranchID = :bankBranchID, AccountHolderName = :AccountHolderName, PhoneNumber = :PhoneNumber,");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		sql.append(" Where BankId = :BankId ");

		// TODO : TEMPERORY COMMENTED, NEED TO PROVIDE PERMINANT FIX
		/*
		 * if (!type.endsWith("_Temp")) { updateSql.append("AND Version= :Version - 1"); }
		 */

		logger.debug("updateSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for get total number of records from BMTBankDetail master table.<br>
	 * 
	 * @param bankCode
	 * 
	 * @return Integer
	 */
	@Override
	public int getBankCodeCount(String bankCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BankCode", bankCode);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM BMTBankDetail");
		selectSql.append(" WHERE ");
		selectSql.append("BankCode= :BankCode");

		logger.debug("insertSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	/**
	 * Method for get total number of records from RMTAccountTypes master table.<br>
	 * 
	 * @param accType
	 * 
	 * @return Integer
	 */
	@Override
	public int getAccTypeCount(String accType) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AcType", accType);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM RMTAccountTypes");
		selectSql.append(" WHERE ");
		selectSql.append("AcType= :AcType");
		logger.debug("insertSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	/**
	 * Fetch current version of the record.
	 * 
	 * @param id
	 * @return Integer
	 */
	@Override
	public int getVersion(long id) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BankId", id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT Version FROM CustomerBankInfo");

		selectSql.append(" WHERE BankId = :BankId");

		logger.debug("insertSql: " + selectSql.toString());

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public int getCustomerBankInfoByCustBankName(long custId, String bankName, String accountNumber, long bankId,
			String type) {
		logger.debug("Entering");
		CustomerBankInfo customerBankInfo = new CustomerBankInfo();
		customerBankInfo.setCustID(custId);
		customerBankInfo.setBankName(bankName);
		customerBankInfo.setAccountNumber(accountNumber);
		customerBankInfo.setBankId(bankId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From CustomerBankInfo");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(
				" Where CustID = :custID and BankName=:BankName and AccountNumber=:AccountNumber and bankId != :bankId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public CustomerBankInfo getCustomerBankInfoByCustId(CustomerBankInfo cbi, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT bankId, CustID, BankName, AccountNumber, AccountType,SalaryAccount,");
		sql.append(
				" CreditTranNo, CreditTranAmt, CreditTranAvg, DebitTranNo, DebitTranAmt, CashDepositNo, CashDepositAmt,");
		sql.append(" CashWithdrawalNo, CashWithdrawalAmt, ChqDepositNo, ChqDepositAmt, ChqIssueNo, ChqIssueAmt,");
		sql.append(
				" InwardChqBounceNo, OutwardChqBounceNo, EodBalMin, EodBalMax, EodBalAvg, BankBranch, FromDate, ToDate, RepaymentFrom, NoOfMonthsBanking, LwowRatio, CcLimit, TypeOfBanks, AccountOpeningDate, AddToBenficiary,");
		sql.append(" bankBranchID, AccountHolderName, PhoneNumber,");
		if (type.contains("View")) {
			sql.append(" lovDescBankName,lovDescAccountType,lovDescCustCIF,lovDescCustShrtName,Ifsc,");
			sql.append(" City, Micr, BranchCode,");
		}
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  CustomerBankInfo");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = :CustID and BankName = :BankName and BankId =:BankId");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cbi);
		RowMapper<CustomerBankInfo> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerBankInfo.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			cbi = null;
		}
		return null;
	}

	@Override
	public int getCustomerBankInfoByBank(String bankCode, String type) {
		CustomerBankInfo customerBankInfo = new CustomerBankInfo();
		customerBankInfo.setBankName(bankCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From CustomerBankInfo");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankName =:BankName");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public CustomerBankInfo getSumOfAmtsCustomerBankInfoByCustId(Set<Long> custId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custId);

		StringBuilder sql = new StringBuilder();
		sql.append(
				"select coalesce(sum(CreditTranNo),0) as CreditTranNo ,coalesce(sum(CreditTranAmt),0) as CreditTranAmt, coalesce(sum(CreditTranAvg),0) as CreditTranAvg, coalesce(sum(DebitTranNo),0) as DebitTranNo,");
		sql.append(
				"coalesce(sum(DebitTranAmt),0) as DebitTranAmt, coalesce(sum(CashDepositNo),0) CashDepositNo, coalesce(sum(CashDepositAmt),0) as CashDepositAmt, coalesce(sum(CashWithdrawalNo),0) as CashWithdrawalNo,");
		sql.append(
				"coalesce(sum(CashWithdrawalAmt),0) as CashWithdrawalAmt, coalesce(sum(ChqDepositNo),0) as ChqDepositNo, coalesce(sum(ChqDepositAmt),0) as ChqDepositAmt, coalesce(sum(ChqIssueNo),0) as ChqIssueNo,");
		sql.append(
				"coalesce(sum(ChqIssueAmt),0) as ChqIssueAmt, coalesce(sum(InwardChqBounceNo),0) as InwardChqBounceNo, coalesce(sum(OutwardChqBounceNo),0) as OutwardChqBounceNo,");
		sql.append(
				"coalesce(sum (EodBalMin),0) as EodBalMin, coalesce(sum(EodBalMax),0) as EodBalMax, coalesce(sum(EodBalAvg),0) as EodBalAvg");
		sql.append(" FROM  CustomerBankInfo");
		sql.append(" Where CustID in (:CustID)");

		logger.debug("selectSql: " + sql.toString());
		RowMapper<CustomerBankInfo> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerBankInfo.class);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
	}

	@Override
	public List<BankInfoDetail> getBankInfoDetailById(long bankId, Date monthYear, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where BankId = ? and MonthYear = ?");

		logger.trace(Literal.SQL + sql.toString());

		CustomerBankRowMapper rowMapper = new CustomerBankRowMapper();

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, bankId);
				ps.setDate(index, JdbcUtil.getDate(monthYear));
			}
		}, rowMapper);
	}

	@Override
	public List<BankInfoDetail> getBankInfoDetailById(long bankId, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where BankId = ?");

		logger.trace(Literal.SQL + sql.toString());

		CustomerBankRowMapper rowMapper = new CustomerBankRowMapper();

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index, bankId);
			}
		}, rowMapper);
	}

	@Override
	public void save(BankInfoDetail bankInfoDetail, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into BankInfoDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (BankId, MonthYear, Balance, DebitNo, DebitAmt, CreditNo,");
		sql.append(" CreditAmt, BounceIn, BounceOut, ClosingBal, SanctionLimit, AvgUtilization,");
		sql.append("PeakUtilizationLevel, SettlementNo, SettlementCredits, ODCCLimit,");
		sql.append("  Interest, Trf, TotalEmi, TotalSalary, EmiBounceNo, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:BankId, :MonthYear, :Balance, :DebitNo, :DebitAmt, :CreditNo,");
		sql.append(" :CreditAmt, :BounceIn, :BounceOut, :ClosingBal, :SanctionLimit, :AvgUtilization,");
		sql.append(":PeakUtilizationLevel, :SettlementNo, :SettlementCredits, :ODCCLimit,");
		sql.append(" :Interest, :Trf, :TotalEmi, :TotalSalary, :EmiBounceNo, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoDetail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void update(BankInfoDetail bankInfoDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" Update BankInfoDetail");
		sql.append(StringUtils.trimToEmpty(type));

		sql.append(" Set BankId = :BankId, MonthYear = :MonthYear,Balance = :Balance,");
		sql.append(" DebitNo = :DebitNo, DebitAmt = :DebitAmt,CreditNo = :CreditNo,");
		sql.append(" CreditAmt = :CreditAmt, BounceIn = :BounceIn,BounceOut = :BounceOut,");
		sql.append(" ClosingBal = :ClosingBal, SanctionLimit = :SanctionLimit, AvgUtilization = :AvgUtilization,");
		sql.append(
				" PeakUtilizationLevel = :PeakUtilizationLevel, SettlementNo = :SettlementNo, SettlementCredits = :SettlementCredits, ODCCLimit = :ODCCLimit,");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" Interest =:Interest, Trf =:Trf,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		sql.append(", TotalEmi = :TotalEmi, TotalSalary = :TotalSalary, EmiBounceNo = :EmiBounceNo ");
		sql.append(" Where BankId = :BankId And MonthYear = :MonthYear");

		// TODO : TEMPERORY COMMENTED, NEED TO PROVIDE PERMINANT FIX
		/*
		 * if (!type.endsWith("_Temp")) { updateSql.append(" AND Version = :Version - 1"); }
		 */

		logger.debug("updateSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoDetail);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void delete(BankInfoDetail bankInfoDetail, String type) {
		logger.debug("Entering");
		StringBuilder sql = new StringBuilder(" Delete From BankInfoDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankId =:BankId And MonthYear =:MonthYear");

		logger.debug("deleteSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoDetail);

		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<BankInfoSubDetail> getBankInfoSubDetailById(long bankId, Date monthYear, int day, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getBankSubInfoQuery(type);
		sql.append(" Where BankId = ? and MonthYear = ? and Day = ?");

		logger.trace(Literal.SQL + sql.toString());
		BankSubInfoRowMapper rowMapper = new BankSubInfoRowMapper();

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, bankId);
				ps.setDate(index++, JdbcUtil.getDate(monthYear));
				ps.setInt(index, day);
			}
		}, rowMapper);
	}

	@Override
	public List<BankInfoSubDetail> getBankInfoSubDetailById(long bankId, Date monthYear, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getBankSubInfoQuery(type);
		sql.append(" Where BankId = ? and MonthYear = ?");

		logger.trace(Literal.SQL + sql.toString());
		BankSubInfoRowMapper rowMapper = new BankSubInfoRowMapper();

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, bankId);
				ps.setDate(index, JdbcUtil.getDate(monthYear));
			}
		}, rowMapper);
	}

	@Override
	public void save(List<BankInfoSubDetail> bankInfoSubDetail, String type) {

		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into BankInfoSubDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (BankId, MonthYear, Day, Balance,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:BankId, :MonthYear, :Day, :Balance,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + sql.toString());
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(bankInfoSubDetail.toArray());
		logger.debug("Leaving");
		this.jdbcTemplate.batchUpdate(sql.toString(), params);

	}

	@Override
	public void update(BankInfoSubDetail bankInfoSubDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" Update BankInfoSubDetail");
		sql.append(StringUtils.trimToEmpty(type));

		sql.append(" Set BankId = :BankId, MonthYear = :MonthYear,Day = :Day, Balance = :Balance,");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		sql.append(" Where BankId = :BankId And MonthYear =:MonthYear And Day = :Day");

		// TODO : TEMPERORY COMMENTED, NEED TO PROVIDE PERMINANT FIX
		/*
		 * if (!type.endsWith("_Temp")) { updateSql.append(" AND Version  = :Version - 1"); }
		 */

		logger.debug("updateSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoSubDetail);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		/*
		 * if (recordCount <= 0) { throw new ConcurrencyException(); }
		 */

		logger.debug("Leaving");
	}

	@Override
	public void delete(List<BankInfoSubDetail> bankInfoSubDetail, String type) {
		logger.debug("Entering");
		StringBuilder sql = new StringBuilder(" Delete From BankInfoSubDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankId =:BankId And MonthYear =:MonthYear And Day =:Day");

		logger.debug("deleteSql: " + sql.toString());
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(bankInfoSubDetail.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), params);
		logger.debug("Leaving");
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BankId, MonthYear, Balance, DebitNo, DebitAmt, CreditNo, CreditAmt, BounceIn");
		sql.append(
				", BounceOut, ClosingBal, SanctionLimit, AvgUtilization, PeakUtilizationLevel, SettlementNo, SettlementCredits, ODCCLimit");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(", Interest, Trf, TotalEmi, TotalSalary, EmiBounceNo");
		sql.append(" from BankInfoDetail");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private StringBuilder getBankSubInfoQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BankId, MonthYear, Day, Balance, Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from BankInfoSubDetail");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private class CustomerBankRowMapper implements RowMapper<BankInfoDetail> {

		@Override
		public BankInfoDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			BankInfoDetail bid = new BankInfoDetail();

			bid.setBankId(rs.getLong("BankId"));
			bid.setMonthYear(rs.getTimestamp("MonthYear"));
			bid.setBalance(rs.getBigDecimal("Balance"));
			bid.setDebitNo(rs.getInt("DebitNo"));
			bid.setDebitAmt(rs.getBigDecimal("DebitAmt"));
			bid.setCreditNo(rs.getInt("CreditNo"));
			bid.setCreditAmt(rs.getBigDecimal("CreditAmt"));
			bid.setBounceIn(rs.getBigDecimal("BounceIn"));
			bid.setBounceOut(rs.getBigDecimal("BounceOut"));
			bid.setClosingBal(rs.getBigDecimal("ClosingBal"));
			bid.setSanctionLimit(rs.getBigDecimal("SanctionLimit"));
			bid.setAvgUtilization(rs.getBigDecimal("AvgUtilization"));
			bid.setPeakUtilizationLevel(rs.getBigDecimal("PeakUtilizationLevel"));
			bid.setSettlementNo(rs.getInt("SettlementNo"));
			bid.setSettlementCredits(rs.getBigDecimal("SettlementCredits"));
			bid.setoDCCLimit(rs.getBigDecimal("ODCCLimit"));
			bid.setVersion(rs.getInt("Version"));
			bid.setLastMntOn(rs.getTimestamp("LastMntOn"));
			bid.setLastMntBy(rs.getLong("LastMntBy"));
			bid.setRecordStatus(rs.getString("RecordStatus"));
			bid.setRoleCode(rs.getString("RoleCode"));
			bid.setNextRoleCode(rs.getString("NextRoleCode"));
			bid.setTaskId(rs.getString("TaskId"));
			bid.setNextTaskId(rs.getString("NextTaskId"));
			bid.setRecordType(rs.getString("RecordType"));
			bid.setWorkflowId(rs.getLong("WorkflowId"));
			bid.setInterest(rs.getBigDecimal("Interest"));
			bid.setTrf(rs.getBigDecimal("Trf"));
			bid.setTotalEmi(rs.getBigDecimal("TotalEmi"));
			bid.setTotalSalary(rs.getBigDecimal("TotalSalary"));
			bid.setEmiBounceNo(rs.getInt("EmiBounceNo"));

			return bid;
		}

	}

	private class BankSubInfoRowMapper implements RowMapper<BankInfoSubDetail> {

		@Override
		public BankInfoSubDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			BankInfoSubDetail bid = new BankInfoSubDetail();

			bid.setBankId(rs.getLong("BankId"));
			bid.setMonthYear(rs.getTimestamp("MonthYear"));
			bid.setDay(rs.getInt("Day"));
			bid.setBalance(rs.getBigDecimal("Balance"));
			bid.setVersion(rs.getInt("Version"));
			bid.setLastMntOn(rs.getTimestamp("LastMntOn"));
			bid.setLastMntBy(rs.getLong("LastMntBy"));
			bid.setRecordStatus(rs.getString("RecordStatus"));
			bid.setRoleCode(rs.getString("RoleCode"));
			bid.setNextRoleCode(rs.getString("NextRoleCode"));
			bid.setTaskId(rs.getString("TaskId"));
			bid.setNextTaskId(rs.getString("NextTaskId"));
			bid.setRecordType(rs.getString("RecordType"));
			bid.setWorkflowId(rs.getLong("WorkflowId"));

			return bid;
		}

	}
}