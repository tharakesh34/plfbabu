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
 * FileName    		:  CustomerBankInfoDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.customermasters.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.customermasters.CustomerBankInfoDAO;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.BankInfoSubDetail;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>CustomerBankInfo model</b> class.<br>
 * 
 */
public class CustomerBankInfoDAOImpl extends SequenceDao<CustomerBankInfo> implements CustomerBankInfoDAO {
	private static Logger logger = Logger.getLogger(CustomerBankInfoDAOImpl.class);

	public CustomerBankInfoDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer Bank details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
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
		sql.append(" bankBranchID, AccountHolderName,");
		if (type.contains("View")) {
			sql.append(" lovDescBankName,lovDescAccountType,Ifsc,");
		}
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  CustomerBankInfo");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankId = :BankId");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);
		RowMapper<CustomerBankInfo> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerBankInfo.class);

		try {
			customerBankInfo = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerBankInfo = null;
		}
		logger.debug("Leaving");
		return customerBankInfo;
	}

	/**
	 * Method to return the customer email based on given customer id
	 */
	public List<CustomerBankInfo> getBankInfoByCustomer(final long id, String type) {
		logger.debug("Entering");
		CustomerBankInfo customerBankInfo = new CustomerBankInfo();
		customerBankInfo.setId(id);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT BankId,CustID, BankName, AccountNumber, AccountType,SalaryAccount,");
		sql.append(
				" CreditTranNo, CreditTranAmt, CreditTranAvg, DebitTranNo, DebitTranAmt, CashDepositNo, CashDepositAmt,");
		sql.append(" CashWithdrawalNo, CashWithdrawalAmt, ChqDepositNo, ChqDepositAmt, ChqIssueNo, ChqIssueAmt,");
		sql.append(" InwardChqBounceNo, OutwardChqBounceNo, EodBalMin, EodBalMax, EodBalAvg,  BankBranch, FromDate,");
		sql.append(
				" ToDate, RepaymentFrom, NoOfMonthsBanking, LwowRatio, CcLimit, TypeOfBanks, AccountOpeningDate,AddToBenficiary,");
		sql.append(" bankBranchID, AccountHolderName,");
		if (type.contains("View")) {
			sql.append(" lovDescBankName,lovDescAccountType,Ifsc,");
		}
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  CustomerBankInfo");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = :custID");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);
		RowMapper<CustomerBankInfo> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerBankInfo.class);

		List<CustomerBankInfo> custBankInformation = this.jdbcTemplate.query(sql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return custBankInformation;
	}

	/**
	 * This method Deletes the Record from the CustomerBankInfo or CustomerBankInfo_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Customer Bank by key CustID
	 * 
	 * @param Customer
	 *            Bank (customerBankInfo)
	 * @param type
	 *            (String) ""/_Temp/_View
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
	 * @param Customer
	 *            Bank (customerBankInfo)
	 * @param type
	 *            (String) ""/_Temp/_View
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
	 * @param Customer
	 *            Bank (customerBankInfo)
	 * @param type
	 *            (String) ""/_Temp/_View
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
		sql.append(" bankBranchID, AccountHolderName,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:BankId, :CustID, :BankName, :AccountNumber, :AccountType, :SalaryAccount,");
		sql.append(
				" :CreditTranNo, :CreditTranAmt, :CreditTranAvg, :DebitTranNo, :DebitTranAmt, :CashDepositNo, :CashDepositAmt,");
		sql.append(" :CashWithdrawalNo, :CashWithdrawalAmt, :ChqDepositNo, :ChqDepositAmt, :ChqIssueNo, :ChqIssueAmt,");
		sql.append(
				" :InwardChqBounceNo, :OutwardChqBounceNo, :EodBalMin, :EodBalMax, :EodBalAvg, :BankBranch, :FromDate,");
		sql.append(
				" :ToDate, :RepaymentFrom, :NoOfMonthsBanking, :LwowRatio, :CcLimit, :TypeOfBanks, :AccountOpeningDate, :AddToBenficiary,");
		sql.append(" :bankBranchID, :AccountHolderName,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
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
	 * @param Customer
	 *            Bank (customerBankInfo)
	 * @param type
	 *            (String) ""/_Temp/_View
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
				" AddToBenficiary = :AddToBenficiary, bankBranchID = :bankBranchID, AccountHolderName = :AccountHolderName,");
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

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM BMTBankDetail");
		selectSql.append(" WHERE ");
		selectSql.append("BankCode= :BankCode");

		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount;
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
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM RMTAccountTypes");
		selectSql.append(" WHERE ");
		selectSql.append("AcType= :AcType");
		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount;
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

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM CustomerBankInfo");

		selectSql.append(" WHERE BankId = :BankId");

		logger.debug("insertSql: " + selectSql.toString());

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		return recordCount;
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

		try {
			int bankRcdCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
			logger.debug("Leaving");
			return bankRcdCount;
		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		}
	}

	@Override
	public CustomerBankInfo getCustomerBankInfoByCustId(CustomerBankInfo customerBankInfo, String type) {
		logger.debug("Entering");
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT bankId, CustID, BankName, AccountNumber, AccountType,SalaryAccount,");
		sql.append(
				" CreditTranNo, CreditTranAmt, CreditTranAvg, DebitTranNo, DebitTranAmt, CashDepositNo, CashDepositAmt,");
		sql.append(" CashWithdrawalNo, CashWithdrawalAmt, ChqDepositNo, ChqDepositAmt, ChqIssueNo, ChqIssueAmt,");
		sql.append(
				" InwardChqBounceNo, OutwardChqBounceNo, EodBalMin, EodBalMax, EodBalAvg, BankBranch, FromDate, ToDate, RepaymentFrom, NoOfMonthsBanking, LwowRatio, CcLimit, TypeOfBanks, AccountOpeningDate, AddToBenficiary,");
		sql.append(" bankBranchID, AccountHolderName,");
		if (type.contains("View")) {
			sql.append(" lovDescBankName,lovDescAccountType,lovDescCustCIF,lovDescCustShrtName,Ifsc,");
		}
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  CustomerBankInfo");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = :CustID and BankName = :BankName and BankId =:BankId");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);
		RowMapper<CustomerBankInfo> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerBankInfo.class);
		try {
			customerBankInfo = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerBankInfo = null;
		}
		logger.debug("Leaving");
		return customerBankInfo;
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
		RowMapper<CustomerBankInfo> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerBankInfo.class);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
	}

	@Override
	public List<BankInfoDetail> getBankInfoDetailById(long bankId, Date monthYear, String type) {
		logger.debug("Entering");
		BankInfoDetail bankInfoDetail = new BankInfoDetail();
		bankInfoDetail.setBankId(bankId);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT BankId, MonthYear, Balance, DebitNo, DebitAmt, CreditNo,");
		sql.append(
				" CreditAmt, BounceIn, BounceOut, ClosingBal, SanctionLimit, AvgUtilization, PeakUtilizationLevel, ODCCLimit,");
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  BankInfoDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankId = :BankId And MonthYear =:MonthYear");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoDetail);
		RowMapper<BankInfoDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BankInfoDetail.class);

		List<BankInfoDetail> bankInfoDetails = this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);

		logger.debug("Leaving");
		return bankInfoDetails;
	}

	@Override
	public List<BankInfoDetail> getBankInfoDetailById(long bankId, String type) {
		logger.debug("Entering");
		BankInfoDetail bankInfoDetail = new BankInfoDetail();
		bankInfoDetail.setBankId(bankId);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT BankId, MonthYear, Balance, DebitNo, DebitAmt, CreditNo,");
		sql.append(
				" CreditAmt, BounceIn, BounceOut, ClosingBal, SanctionLimit, AvgUtilization, PeakUtilizationLevel, ODCCLimit,");
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  BankInfoDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankId = :BankId");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoDetail);
		RowMapper<BankInfoDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BankInfoDetail.class);

		List<BankInfoDetail> bankInfoDetails = this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);

		logger.debug("Leaving");
		return bankInfoDetails;
	}

	@Override
	public void save(BankInfoDetail bankInfoDetail, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into BankInfoDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (BankId, MonthYear, Balance, DebitNo, DebitAmt, CreditNo,");
		sql.append(
				" CreditAmt, BounceIn, BounceOut, ClosingBal, SanctionLimit, AvgUtilization, peakUtilizationLevel, ODCCLimit,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:BankId, :MonthYear, :Balance, :DebitNo, :DebitAmt, :CreditNo,");
		sql.append(
				" :CreditAmt, :BounceIn, :BounceOut, :ClosingBal, :SanctionLimit, :AvgUtilization, :PeakUtilizationLevel, :ODCCLimit,");
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
		sql.append(" PeakUtilizationLevel = :PeakUtilizationLevel, ODCCLimit = :ODCCLimit,");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
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
		logger.debug("Entering");
		BankInfoSubDetail bankInfoSubDetail = new BankInfoSubDetail();
		bankInfoSubDetail.setBankId(bankId);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT BankId, MonthYear, Day");
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  BankInfoSubDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankId = :BankId And MonthYear =:MonthYear And Day =:Day");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoSubDetail);
		RowMapper<BankInfoSubDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BankInfoSubDetail.class);

		List<BankInfoSubDetail> bankInfoSubDetails = this.jdbcTemplate.query(sql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return bankInfoSubDetails;
	}

	@Override
	public List<BankInfoSubDetail> getBankInfoSubDetailById(long bankId, Date monthYear, String type) {
		logger.debug("Entering");
		BankInfoSubDetail bankInfoSubDetail = new BankInfoSubDetail();
		bankInfoSubDetail.setBankId(bankId);
		bankInfoSubDetail.setMonthYear(monthYear);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT BankId, MonthYear, Day, Balance,");
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  BankInfoSubDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankId = :BankId And MonthYear =:MonthYear");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoSubDetail);
		RowMapper<BankInfoSubDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BankInfoSubDetail.class);

		List<BankInfoSubDetail> bankInfoSubDetails = this.jdbcTemplate.query(sql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return bankInfoSubDetails;
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

}