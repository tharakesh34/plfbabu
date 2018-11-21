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
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT bankId, CustID, BankName, AccountNumber, AccountType,SalaryAccount,");
		selectSql.append(
				" CreditTranNo, CreditTranAmt, CreditTranAvg, DebitTranNo, DebitTranAmt, CashDepositNo, CashDepositAmt,");
		selectSql.append(" CashWithdrawalNo, CashWithdrawalAmt, ChqDepositNo, ChqDepositAmt, ChqIssueNo, ChqIssueAmt,");
		selectSql.append(" InwardChqBounceNo, OutwardChqBounceNo, EodBalMin, EodBalMax, EodBalAvg,");
		if (type.contains("View")) {
			selectSql.append(" lovDescBankName,lovDescAccountType,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerBankInfo");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankId = :BankId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);
		RowMapper<CustomerBankInfo> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerBankInfo.class);

		try {
			customerBankInfo = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
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

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT BankId,CustID, BankName, AccountNumber, AccountType,SalaryAccount,");
		selectSql.append(
				" CreditTranNo, CreditTranAmt, CreditTranAvg, DebitTranNo, DebitTranAmt, CashDepositNo, CashDepositAmt,");
		selectSql.append(" CashWithdrawalNo, CashWithdrawalAmt, ChqDepositNo, ChqDepositAmt, ChqIssueNo, ChqIssueAmt,");
		selectSql.append(" InwardChqBounceNo, OutwardChqBounceNo, EodBalMin, EodBalMax, EodBalAvg,");
		if (type.contains("View")) {
			selectSql.append(" lovDescBankName,lovDescAccountType,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerBankInfo");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);
		RowMapper<CustomerBankInfo> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerBankInfo.class);

		List<CustomerBankInfo> custBankInformation = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
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
		StringBuilder deleteSql = new StringBuilder(" Delete From CustomerBankInfo");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BankId =:BankId");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
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

		CustomerBankInfo customerBankInfo = new CustomerBankInfo();
		customerBankInfo.setCustID(custID);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerBankInfo");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
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
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerBankInfo");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BankId,CustID, BankName, AccountNumber, AccountType,SalaryAccount,");
		insertSql.append(
				" CreditTranNo, CreditTranAmt, CreditTranAvg, DebitTranNo, DebitTranAmt, CashDepositNo, CashDepositAmt,");
		insertSql.append(" CashWithdrawalNo, CashWithdrawalAmt, ChqDepositNo, ChqDepositAmt, ChqIssueNo, ChqIssueAmt,");
		insertSql.append(" InwardChqBounceNo, OutwardChqBounceNo, EodBalMin, EodBalMax, EodBalAvg,");

		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BankId, :CustID, :BankName, :AccountNumber, :AccountType, :SalaryAccount,");
		insertSql.append(
				" :CreditTranNo, :CreditTranAmt, :CreditTranAvg, :DebitTranNo, :DebitTranAmt, :CashDepositNo, :CashDepositAmt,");
		insertSql.append(
				" :CashWithdrawalNo, :CashWithdrawalAmt, :ChqDepositNo, :ChqDepositAmt, :ChqIssueNo, :ChqIssueAmt,");
		insertSql.append(" :InwardChqBounceNo, :OutwardChqBounceNo, :EodBalMin, :EodBalMax, :EodBalAvg,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

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

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerBankInfo");
		updateSql.append(StringUtils.trimToEmpty(type));

		updateSql.append(
				" Set AccountNumber = :AccountNumber, AccountType = :AccountType,SalaryAccount = :SalaryAccount,");
		updateSql.append(
				" CreditTranNo = :CreditTranNo, CreditTranAmt = :CreditTranAmt, CreditTranAvg = :CreditTranAvg, DebitTranNo = :DebitTranNo,");
		updateSql.append(
				"DebitTranAmt = :DebitTranAmt, CashDepositNo = :CashDepositNo, CashDepositAmt = :CashDepositAmt,");
		updateSql.append(
				"CashWithdrawalNo = :CashWithdrawalNo,CashWithdrawalAmt = :CashWithdrawalAmt, ChqDepositNo = :ChqDepositNo, ChqDepositAmt = :ChqDepositAmt,");
		updateSql.append(
				"ChqIssueNo = :ChqIssueNo, ChqIssueAmt = :ChqIssueAmt, InwardChqBounceNo = :InwardChqBounceNo, OutwardChqBounceNo = :OutwardChqBounceNo, EodBalMin = :EodBalMin,");
		updateSql.append("EodBalMax = :EodBalMax, EodBalAvg = :EodBalAvg,");
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where BankId = :BankId ");
		if (!type.endsWith("_Temp")) {
			updateSql.append("AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

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
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT bankId, CustID, BankName, AccountNumber, AccountType,SalaryAccount,");
		selectSql.append(
				" CreditTranNo, CreditTranAmt, CreditTranAvg, DebitTranNo, DebitTranAmt, CashDepositNo, CashDepositAmt,");
		selectSql.append(" CashWithdrawalNo, CashWithdrawalAmt, ChqDepositNo, ChqDepositAmt, ChqIssueNo, ChqIssueAmt,");
		selectSql.append(" InwardChqBounceNo, OutwardChqBounceNo, EodBalMin, EodBalMax, EodBalAvg,");
		if (type.contains("View")) {
			selectSql.append(" lovDescBankName,lovDescAccountType,lovDescCustCIF,lovDescCustShrtName,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerBankInfo");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :CustID and BankName = :BankName and BankId =:BankId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBankInfo);
		RowMapper<CustomerBankInfo> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerBankInfo.class);

		try {
			customerBankInfo = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
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

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				"select coalesce(sum(CreditTranNo),0) as CreditTranNo ,coalesce(sum(CreditTranAmt),0) as CreditTranAmt, coalesce(sum(CreditTranAvg),0) as CreditTranAvg, coalesce(sum(DebitTranNo),0) as DebitTranNo,");
		selectSql.append(
				"coalesce(sum(DebitTranAmt),0) as DebitTranAmt, coalesce(sum(CashDepositNo),0) CashDepositNo, coalesce(sum(CashDepositAmt),0) as CashDepositAmt, coalesce(sum(CashWithdrawalNo),0) as CashWithdrawalNo,");
		selectSql.append(
				"coalesce(sum(CashWithdrawalAmt),0) as CashWithdrawalAmt, coalesce(sum(ChqDepositNo),0) as ChqDepositNo, coalesce(sum(ChqDepositAmt),0) as ChqDepositAmt, coalesce(sum(ChqIssueNo),0) as ChqIssueNo,");
		selectSql.append(
				"coalesce(sum(ChqIssueAmt),0) as ChqIssueAmt, coalesce(sum(InwardChqBounceNo),0) as InwardChqBounceNo, coalesce(sum(OutwardChqBounceNo),0) as OutwardChqBounceNo,");
		selectSql.append(
				"coalesce(sum (EodBalMin),0) as EodBalMin, coalesce(sum(EodBalMax),0) as EodBalMax, coalesce(sum(EodBalAvg),0) as EodBalAvg");
		selectSql.append(" FROM  CustomerBankInfo");
		selectSql.append(" Where CustID in (:CustID)");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<CustomerBankInfo> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerBankInfo.class);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public List<BankInfoDetail> getBankInfoDetailById(long bankId, Date monthYear, String type) {
		logger.debug("Entering");
		BankInfoDetail bankInfoDetail = new BankInfoDetail();
		bankInfoDetail.setBankId(bankId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT BankId, MonthYear, Balance, DebitNo, DebitAmt, CreditNo,");
		selectSql.append(" CreditAmt, BounceIn, BounceOut, ClosingBal, ODCCLimit,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  BankInfoDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankId = :BankId And MonthYear =:MonthYear");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoDetail);
		RowMapper<BankInfoDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BankInfoDetail.class);

		List<BankInfoDetail> bankInfoDetails = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return bankInfoDetails;
	}
	
	@Override
	public List<BankInfoDetail> getBankInfoDetailById(long bankId, String type) {
		logger.debug("Entering");
		BankInfoDetail bankInfoDetail = new BankInfoDetail();
		bankInfoDetail.setBankId(bankId);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT BankId, MonthYear, Balance, DebitNo, DebitAmt, CreditNo,");
		selectSql.append(" CreditAmt, BounceIn, BounceOut, ClosingBal, ODCCLimit,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  BankInfoDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankId = :BankId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoDetail);
		RowMapper<BankInfoDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BankInfoDetail.class);
		
		List<BankInfoDetail> bankInfoDetails = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		
		logger.debug("Leaving");
		return bankInfoDetails;
	}

	@Override
	public void save(BankInfoDetail bankInfoDetail, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into BankInfoDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BankId, MonthYear, Balance, DebitNo, DebitAmt, CreditNo,");
		insertSql.append(" CreditAmt, BounceIn, BounceOut, ClosingBal, ODCCLimit,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BankId, :MonthYear, :Balance, :DebitNo, :DebitAmt, :CreditNo,");
		insertSql.append(" :CreditAmt, :BounceIn, :BounceOut, :ClosingBal, :ODCCLimit,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoDetail);

		logger.debug("Leaving");
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
	
		
	}

	@Override
	public void update(BankInfoDetail bankInfoDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update BankInfoDetail");
		updateSql.append(StringUtils.trimToEmpty(type));

		updateSql.append(" Set BankId = :BankId, MonthYear = :MonthYear,Balance = :Balance,");
		updateSql.append(" DebitNo = :DebitNo, DebitAmt = :DebitAmt,CreditNo = :CreditNo,");
		updateSql.append(" CreditAmt = :CreditAmt, BounceIn = :BounceIn,BounceOut = :BounceOut,");
		updateSql.append(" ClosingBal = :ClosingBal, ODCCLimit = :ODCCLimit,");
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where BankId = :BankId And MonthYear =:MonthYear");
		if (!type.endsWith("_Temp")) {
			updateSql.append("AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void delete(BankInfoDetail bankInfoDetail, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder(" Delete From BankInfoDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BankId =:BankId And MonthYear =:MonthYear");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoDetail);

		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<BankInfoSubDetail> getBankInfoSubDetailById(long bankId, Date monthYear, int day, String type) {
		logger.debug("Entering");
		BankInfoSubDetail bankInfoSubDetail = new BankInfoSubDetail();
		bankInfoSubDetail.setBankId(bankId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT BankId, MonthYear, Day");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  BankInfoSubDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankId = :BankId And MonthYear =:MonthYear And Day =:Day");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoSubDetail);
		RowMapper<BankInfoSubDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BankInfoSubDetail.class);

		List<BankInfoSubDetail> bankInfoSubDetails = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
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
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT BankId, MonthYear, Day, Balance,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  BankInfoSubDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankId = :BankId And MonthYear =:MonthYear");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoSubDetail);
		RowMapper<BankInfoSubDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BankInfoSubDetail.class);
		
		List<BankInfoSubDetail> bankInfoSubDetails = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		
		logger.debug("Leaving");
		return bankInfoSubDetails;
	}

	@Override
	public void save(List<BankInfoSubDetail> bankInfoSubDetail, String type) {

		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into BankInfoSubDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BankId, MonthYear, Day, Balance,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BankId, :MonthYear, :Day, :Balance,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(bankInfoSubDetail.toArray());
		logger.debug("Leaving");
		this.jdbcTemplate.batchUpdate(insertSql.toString(), params);
	
		
	}

	@Override
	public void update(BankInfoSubDetail bankInfoSubDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update BankInfoSubDetail");
		updateSql.append(StringUtils.trimToEmpty(type));

		updateSql.append(" Set BankId = :BankId, MonthYear = :MonthYear,Day = :Day, Balance = :Balance,");
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where BankId = :BankId And MonthYear =:MonthYear And Day =:Day");
		if (!type.endsWith("_Temp")) {
			updateSql.append("AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankInfoSubDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void delete(List<BankInfoSubDetail>  bankInfoSubDetail, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder(" Delete From BankInfoSubDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BankId =:BankId And MonthYear =:MonthYear And Day =:Day");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(bankInfoSubDetail.toArray());
			this.jdbcTemplate.batchUpdate(deleteSql.toString(), params);
		logger.debug("Leaving");
	}

}