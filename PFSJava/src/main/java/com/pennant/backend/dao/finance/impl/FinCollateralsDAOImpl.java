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
 * * FileName : FinCollateralsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinCollateralsDAO;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>FinCollaterals model</b> class.<br>
 */
public class FinCollateralsDAOImpl extends SequenceDao<FinCollaterals> implements FinCollateralsDAO {
	private static Logger logger = Logger.getLogger(FinCollateralsDAOImpl.class);

	public FinCollateralsDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record FinCollaterals Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinCollaterals
	 */
	@Override
	public FinCollaterals getFinCollateralsById(final String finReference, final long id, String type) {
		logger.debug("Entering");

		FinCollaterals finCollaterals = new FinCollaterals();
		finCollaterals.setId(id);
		finCollaterals.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				" Select FinReference, CollateralSeq, CollateralType, CustID, Reference, Ccy, Value, Coverage, TenorType, Tenor, ");
		selectSql.append(" Rate, StartDate, MaturityDate, BankName, FirstChequeNo, LastChequeNo, Status, Remarks, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		selectSql.append(" From FinCollaterals");

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND CollateralSeq=:CollateralSeq");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCollaterals);
		RowMapper<FinCollaterals> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinCollaterals.class);

		try {
			finCollaterals = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finCollaterals = null;
		}
		logger.debug("Leaving");
		return finCollaterals;
	}

	/**
	 * This method Deletes the Record from the FinCollaterals or
	 * FinCollaterals_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete FinCollaterals by key
	 * FinReference
	 * 
	 * @param FinCollaterals
	 *            (FinCollaterals)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	public void deleteByFinReference(String id, String type) {
		logger.debug("Entering");
		FinCollaterals finCollaterals = new FinCollaterals();
		finCollaterals.setFinReference(id);

		StringBuilder deleteSql = new StringBuilder("Delete From ");

		deleteSql.append(" FinCollaterals");

		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCollaterals);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinCollaterals or
	 * FinCollaterals_Temp.
	 * 
	 * save FinCollaterals
	 * 
	 * @param FinCollaterals
	 *            (FinCollaterals)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(FinCollaterals finCollaterals, String type) {
		logger.debug("Entering");

		if (finCollaterals.getCollateralSeq() == Long.MIN_VALUE) {
			finCollaterals.setCollateralSeq(getNextValue("SeqCollateral"));
		}

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" FinCollaterals");

		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (FinReference, CollateralSeq, CollateralType, CustID, Reference, Ccy, Value, Coverage, TenorType, Tenor, ");
		insertSql.append(" Rate, StartDate, MaturityDate, BankName, FirstChequeNo, LastChequeNo, Status, Remarks, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" values (:FinReference, :CollateralSeq, :CollateralType, :CustID, :Reference, :Ccy, :Value, :Coverage, :TenorType, :Tenor, ");
		insertSql.append(
				" :Rate, :StartDate, :MaturityDate, :BankName, :FirstChequeNo, :LastChequeNo, :Status, :Remarks, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId,");
		insertSql.append(" :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCollaterals);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finCollaterals.getId();
	}

	/**
	 * This method updates the Record FinCollaterals or FinCollaterals_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update FinCollaterals by key FinReference and Version
	 * 
	 * @param FinCollaterals
	 *            (FinCollaterals)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinCollaterals finCollaterals, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update ");

		updateSql.append(" FinCollaterals");

		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CollateralType = :CollateralType,");
		updateSql.append(" CustID = :CustID, Reference = :Reference, Ccy = :Ccy,");
		updateSql.append(" Value = :Value, Coverage = :Coverage, TenorType = :TenorType,");
		updateSql.append(" Tenor = :Tenor, Rate = :Rate, StartDate = :StartDate,");
		updateSql.append(" MaturityDate = :MaturityDate, BankName = :BankName, FirstChequeNo = :FirstChequeNo,");
		updateSql.append(" LastChequeNo = :LastChequeNo, Status = :Status, Remarks = :Remarks,");
		updateSql.append(" Version= :Version , LastMntBy=:LastMntBy,");
		updateSql.append(" LastMntOn= :LastMntOn, RecordStatus=:RecordStatus, RoleCode=:RoleCode,");
		updateSql.append(" NextRoleCode= :NextRoleCode, TaskId= :TaskId,");
		updateSql.append(" NextTaskId= :NextTaskId, RecordType= :RecordType, WorkflowId= :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference AND CollateralSeq = :CollateralSeq");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCollaterals);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<FinCollaterals> getFinCollateralsByFinRef(String finReference, String type) {
		logger.debug("Entering");

		FinCollaterals finCollaterals = new FinCollaterals();
		finCollaterals.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				" Select FinReference, CollateralSeq, CollateralType, CustID, Reference, Ccy, Value, Coverage, TenorType, Tenor, ");
		selectSql.append(" Rate, StartDate, MaturityDate, BankName, FirstChequeNo, LastChequeNo, Status, Remarks, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		selectSql.append(" From FinCollaterals");

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCollaterals);
		RowMapper<FinCollaterals> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinCollaterals.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void delete(FinCollaterals finCollaterals, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder("Delete From ");

		deleteSql.append(" FinCollaterals");

		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference AND CollateralSeq=:CollateralSeq ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCollaterals);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public int getFinCollateralsByBank(String bankCode, String type) {
		FinCollaterals finCollaterals = new FinCollaterals();
		finCollaterals.setBankName(bankCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinCollaterals");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankName =:BankName");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCollaterals);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
}