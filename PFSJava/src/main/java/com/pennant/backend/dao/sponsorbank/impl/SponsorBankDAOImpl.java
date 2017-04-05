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
 * FileName    		:  SponsorBankDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-03-2017    														*
 *                                                                  						*
 * Modified Date    :  09-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-03-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.sponsorbank.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.sponsorbank.SponsorBankDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.sponsorbank.SponsorBank;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>SponsorBank model</b> class.<br>
 * 
 */

public class SponsorBankDAOImpl extends BasisCodeDAO<SponsorBank> implements SponsorBankDAO {

	private static Logger				logger	= Logger.getLogger(SponsorBankDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public SponsorBankDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record SponsorBank details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return SponsorBank
	 */
	@Override
	public SponsorBank getSponsorBankById(final String id, String type) {
		logger.debug("Entering");
		SponsorBank sponsorBank = new SponsorBank();
		sponsorBank.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql
				.append("Select SponsorBankCode, SponsorBankName, BankCode, BankBranchCode, BranchMICRCode, BranchIFSCCode, BranchCity, UtilityCode, AccountNo, AccountType, Active");
		selectSql
				.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",BankCodeName,BankBranchCodeName");
		}

		selectSql.append(" From SponsorBank");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SponsorBankCode =:SponsorBankCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sponsorBank);
		RowMapper<SponsorBank> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SponsorBank.class);

		try {
			sponsorBank = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			sponsorBank = null;
		}
		logger.debug("Leaving");
		return sponsorBank;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the SponsorBank or SponsorBank_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete SponsorBank by key SponsorBankCode
	 * 
	 * @param SponsorBank
	 *            (sponsorBank)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(SponsorBank sponsorBank, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From SponsorBank");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SponsorBankCode =:SponsorBankCode");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sponsorBank);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", sponsorBank.getId(), sponsorBank.getUserDetails()
						.getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006", sponsorBank.getId(), sponsorBank.getUserDetails()
					.getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into SponsorBank or SponsorBank_Temp.
	 *
	 * save SponsorBank
	 * 
	 * @param SponsorBank
	 *            (sponsorBank)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(SponsorBank sponsorBank, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into SponsorBank");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql
				.append(" (SponsorBankCode, SponsorBankName, BankCode, BankBranchCode, BranchMICRCode, BranchIFSCCode, BranchCity, UtilityCode, AccountNo, AccountType,Active");
		insertSql
				.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql
				.append(" Values(:SponsorBankCode, :SponsorBankName, :BankCode, :BankBranchCode, :BranchMICRCode, :BranchIFSCCode, :BranchCity, :UtilityCode, :AccountNo, :AccountType, :Active");
		insertSql
				.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sponsorBank);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return sponsorBank.getId();
	}

	/**
	 * This method updates the Record SponsorBank or SponsorBank_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update SponsorBank by key SponsorBankCode and Version
	 * 
	 * @param SponsorBank
	 *            (sponsorBank)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	@Override
	public void update(SponsorBank sponsorBank, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update SponsorBank");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
				.append(" Set SponsorBankName = :SponsorBankName, BankCode = :BankCode, BankBranchCode = :BankBranchCode, BranchMICRCode = :BranchMICRCode, BranchIFSCCode = :BranchIFSCCode, BranchCity = :BranchCity, UtilityCode = :UtilityCode, AccountNo = :AccountNo, AccountType = :AccountType, Active = :Active");
		updateSql
				.append(", Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where SponsorBankCode =:SponsorBankCode");

		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sponsorBank);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", sponsorBank.getId(), sponsorBank.getUserDetails()
					.getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails getError(String errorId, String sponsorBankCode, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = sponsorBankCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_SponsorBankCode") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]),
				userLanguage);
	}

}