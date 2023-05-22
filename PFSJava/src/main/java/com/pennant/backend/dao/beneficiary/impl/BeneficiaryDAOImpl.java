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
 * * FileName : BeneficiaryDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-12-2016 * * Modified
 * Date : 01-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.beneficiary.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>Beneficiary model</b> class.<br>
 * 
 */
public class BeneficiaryDAOImpl extends SequenceDao<Beneficiary> implements BeneficiaryDAO {
	private static Logger logger = LogManager.getLogger(BeneficiaryDAOImpl.class);

	public BeneficiaryDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Beneficiary details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return Beneficiary
	 */
	@Override
	public Beneficiary getBeneficiaryById(final long id, String type) {
		logger.debug("Entering");
		Beneficiary beneficiary = new Beneficiary();
		beneficiary.setId(id);

		StringBuilder selectSql = new StringBuilder(
				"Select BeneficiaryId, CustID, BankBranchID, AccNumber, AccHolderName, PhoneCountryCode, PhoneAreaCode, PhoneNumber, Email");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,BeneficiaryActive,DefaultBeneficiary");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",CustCIF,custShrtName,BranchCode,BranchDesc,BankName,City,BankCode,IFSC");
		}
		selectSql.append(" From Beneficiary");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  BeneficiaryId =:BeneficiaryId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(beneficiary);
		RowMapper<Beneficiary> typeRowMapper = BeanPropertyRowMapper.newInstance(Beneficiary.class);

		try {
			beneficiary = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			beneficiary = null;
		}
		logger.debug("Leaving");
		return beneficiary;
	}

	/**
	 * Fetch the Record Beneficiary details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return Beneficiary
	 */
	@Override
	public int getBeneficiaryByAccNo(Beneficiary beneficiary, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From Beneficiary");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  AccNumber = :AccNumber AND CustID = :CustID AND BankBranchID = :BankBranchID");
		selectSql.append(" And BeneficiaryId != :BeneficiaryId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(beneficiary);

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * Fetch the Record Beneficiary details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return Beneficiary
	 */
	@Override
	public int getBeneficiaryByBankBranchId(final String accNumber, long bankBranchId, String type) {
		logger.debug("Entering");
		Beneficiary beneficiary = new Beneficiary();
		beneficiary.setAccNumber(accNumber);
		beneficiary.setBankBranchID(bankBranchId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From Beneficiary");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  AccNumber =:AccNumber AND BankBranchID =:BankBranchID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(beneficiary);

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * This method Deletes the Record from the Beneficiary or Beneficiary_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Beneficiary by key BeneficiaryId
	 * 
	 * @param Beneficiary (beneficiary)
	 * @param type        (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Beneficiary beneficiary, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From Beneficiary");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BeneficiaryId =:BeneficiaryId");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(beneficiary);
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
	 * This method insert new Records into Beneficiary or Beneficiary_Temp. it fetches the available Sequence form
	 * SeqBeneficiary by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Beneficiary
	 * 
	 * @param Beneficiary (beneficiary)
	 * @param type        (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(Beneficiary beneficiary, String type) {
		if (beneficiary.getId() == Long.MIN_VALUE) {
			beneficiary.setId(getNextValue("SeqBeneficiary"));
		}

		StringBuilder insertSql = new StringBuilder("Insert Into Beneficiary");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (BeneficiaryId, CustID, BankBranchID, AccNumber, AccHolderName, PhoneCountryCode, PhoneAreaCode, PhoneNumber, Email");
		insertSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,BeneficiaryActive,DefaultBeneficiary)");
		insertSql.append(
				" Values(:BeneficiaryId, :CustID, :BankBranchID, :AccNumber, :AccHolderName, :PhoneCountryCode, :PhoneAreaCode, :PhoneNumber, :Email");
		insertSql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId,:BeneficiaryActive,:DefaultBeneficiary)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(beneficiary);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		return beneficiary.getId();
	}

	/**
	 * This method updates the Record Beneficiary or Beneficiary_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Beneficiary by key BeneficiaryId and Version
	 * 
	 * @param Beneficiary (beneficiary)
	 * @param type        (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(Beneficiary beneficiary, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update Beneficiary");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set CustID = :CustID, BankBranchID = :BankBranchID, AccNumber = :AccNumber, AccHolderName = :AccHolderName, PhoneCountryCode = :PhoneCountryCode, PhoneAreaCode = :PhoneAreaCode, PhoneNumber = :PhoneNumber, Email = :Email");
		updateSql.append(
				", Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(
				" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId,BeneficiaryActive=:BeneficiaryActive,DefaultBeneficiary=:DefaultBeneficiary");
		updateSql.append(" Where BeneficiaryId =:BeneficiaryId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(beneficiary);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Beneficiary Details
	 * 
	 * @param CustID
	 * 
	 * @return
	 */
	@Override
	public List<Beneficiary> getApprovedBeneficiaryByCustomerId(long custID, String type) {
		logger.debug("Entering");
		Beneficiary beneficiary = new Beneficiary();
		beneficiary.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(
				"Select BeneficiaryId, CustID, BankBranchID, AccNumber, AccHolderName, PhoneCountryCode, PhoneAreaCode, PhoneNumber, Email");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,BeneficiaryActive,DefaultBeneficiary");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",CustCIF,custShrtName,BranchCode,BranchDesc,BankName,City,BankCode,IFSC");
		}
		selectSql.append(" From Beneficiary");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID =:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(beneficiary);
		RowMapper<Beneficiary> typeRowMapper = BeanPropertyRowMapper.newInstance(Beneficiary.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public int getBranch(long bankBranchID, String type) {
		Beneficiary beneficiary = new Beneficiary();
		beneficiary.setBankBranchID(bankBranchID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From Beneficiary");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankBranchID =:BankBranchID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(beneficiary);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public int getDefaultsBeneficiary(long custID, long id, String type) {
		logger.debug("Entering");

		Beneficiary beneficiary = new Beneficiary();
		beneficiary.setCustID(custID);
		beneficiary.setBeneficiaryId(id);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From Beneficiary");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :CustID AND BeneficiaryActive = 1 AND DefaultBeneficiary = 1");
		selectSql.append(" AND BeneficiaryId != :BeneficiaryId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(beneficiary);

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public boolean checkCustID(long custid) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(CustId) From Beneficiary");
		sql.append(" Where CustId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), custid) > 0;
	}
}