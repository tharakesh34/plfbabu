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
 * FileName    		:  PartnerBankDAOImpl.java                                                   * 	  
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

package com.pennant.backend.dao.partnerbank.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>PartnerBank model</b> class.<br>
 * 
 */

public class PartnerBankDAOImpl extends BasisNextidDaoImpl<PartnerBank> implements PartnerBankDAO {

	private static Logger				logger	= Logger.getLogger(PartnerBankDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public PartnerBankDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record PartnerBank details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return PartnerBank
	 */
	@Override
	public PartnerBank getPartnerBankById(long id, String type) {
		logger.debug("Entering");
		PartnerBank partnerBank = new PartnerBank();
		partnerBank.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql
				.append("Select PartnerBankId, PartnerBankCode, PartnerBankName, BankCode, BankBranchCode, BranchMICRCode, BranchIFSCCode, BranchCity, UtilityCode, AccountNo ");
		selectSql
				.append(", AcType, AlwFileDownload, InFavourLength, Active, AlwDisb, AlwPayment, AlwReceipt");
		selectSql
				.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",BankCodeName,BankBranchCodeName,AcTypeName");
		}

		selectSql.append(" From PartnerBanks");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PartnerBankId =:PartnerBankId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(partnerBank);
		RowMapper<PartnerBank> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PartnerBank.class);

		try {
			partnerBank = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			partnerBank = null;
		}
		logger.debug("Leaving");
		return partnerBank;
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
	 * This method Deletes the Record from the PartnerBank or PartnerBank_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete PartnerBank by key PartnerBankCode
	 * 
	 * @param PartnerBank
	 *            (partnerBank)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(PartnerBank partnerBank, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From PartnerBanks");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PartnerBankId =:PartnerBankId");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(partnerBank);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", partnerBank.getPartnerBankCode(), partnerBank.getUserDetails()
						.getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006", partnerBank.getPartnerBankCode(), partnerBank.getUserDetails()
					.getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into PartnerBank or PartnerBank_Temp.
	 *
	 * save PartnerBank
	 * 
	 * @param PartnerBank
	 *            (partnerBank)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(PartnerBank partnerBank, String type) {
		logger.debug("Entering");
		if(partnerBank.getPartnerBankId() == Long.MIN_VALUE){
			partnerBank.setPartnerBankId(getNextidviewDAO().getNextId("SEQPartnerBank"));
		}

		StringBuilder insertSql = new StringBuilder("Insert Into PartnerBanks");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql
				.append(" ( PartnerBankId, PartnerBankCode, PartnerBankName, BankCode, BankBranchCode, BranchMICRCode, BranchIFSCCode, BranchCity, UtilityCode, AccountNo ");
		insertSql
				.append(", AcType, AlwFileDownload,  InFavourLength, Active, AlwDisb, AlwPayment, AlwReceipt");
		insertSql
				.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql
				.append(" Values( :PartnerBankId, :PartnerBankCode, :PartnerBankName, :BankCode, :BankBranchCode, :BranchMICRCode, :BranchIFSCCode, :BranchCity, :UtilityCode, :AccountNo ");
		insertSql
				.append(", :AcType, :AlwFileDownload, :InFavourLength, :Active, :AlwDisb, :AlwPayment, :AlwReceipt");
		insertSql
				.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(partnerBank);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return partnerBank.getPartnerBankCode();
	}

	
	/**
	 * This method updates the Record PartnerBank or PartnerBank_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update PartnerBank by key PartnerBankCode and Version
	 * 
	 * @param PartnerBank
	 *            (partnerBank)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	@Override
	public void update(PartnerBank partnerBank, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update PartnerBanks");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
				.append(" Set PartnerBankName = :PartnerBankName, BankCode = :BankCode, BankBranchCode = :BankBranchCode, BranchMICRCode = :BranchMICRCode, BranchIFSCCode = :BranchIFSCCode, BranchCity = :BranchCity, UtilityCode = :UtilityCode, AccountNo = :AccountNo");
		updateSql
				.append(" , AcType = :AcType, AlwFileDownload = :AlwFileDownload,  InFavourLength = :InFavourLength,  Active = :Active, AlwDisb = :AlwDisb, AlwPayment = :AlwPayment, AlwReceipt = :AlwReceipt");
		updateSql
				.append(", Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where PartnerBankId =:PartnerBankId");

		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(partnerBank);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", partnerBank.getPartnerBankCode(), partnerBank.getUserDetails()
					.getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails getError(String errorId, String partnerBankCode, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = partnerBankCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_PartnerBankCode") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]),
				userLanguage);
	}

	/**
	 * Method for Saving List Of PartnerBankModes Details
	 */
	public void saveList(List<PartnerBankModes> list,long id) {
	
		for (PartnerBankModes partnerBankModes : list) {
			partnerBankModes.setPartnerBankId(id);
		}
		StringBuilder insertSql = new StringBuilder("Insert Into PartnerBankModes");
		insertSql.append(" ( PartnerBankId, Purpose, PaymentMode)");
		insertSql.append(" Values(:PartnerBankId,:Purpose, :PaymentMode)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(list.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Update List Of PartnerBankModes Details
	 */
	@Override
	public void updateList(List<PartnerBankModes> list) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update PartnerBankModes");
		updateSql
				.append(" Set Purpose= :Purpose, PaymentMode = :PaymentMode");
		updateSql.append(" Where PartnerBankId =:PartnerBankId  AND PaymentMode =:PaymentMode ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(list.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");		
	}
	/**
	 * Method for Deletion PartnerBankModes Details  
	 * @param partnerBankList
	 */
	
	public void deletePartner(PartnerBank partnerBank) {
		logger.debug("Entering");
		int recordCount = 0;
		PartnerBankModes partnerBankModes= new PartnerBankModes();
		partnerBankModes.setPartnerBankId(partnerBank.getPartnerBankId());
		StringBuilder deleteSql = new StringBuilder("Delete From PartnerBankModes");
		deleteSql.append(" Where PartnerBankId =:PartnerBankId");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(partnerBankModes);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			logger.error(e);
		}
		logger.debug("Leaving");
	}

	public List<PartnerBankModes> getPartnerBankModesId(long partnerBankId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PartnerBankId", partnerBankId);

		StringBuilder selectSql = new StringBuilder("SELECT PartnerBankId, Purpose,PaymentMode from PartnerBankModes");
		selectSql.append(" Where PartnerBankId =:PartnerBankId");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<PartnerBankModes> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PartnerBankModes.class);
		List<PartnerBankModes> PartnerBankModeList = new ArrayList<PartnerBankModes>();
		try {
			PartnerBankModeList = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return PartnerBankModeList;}

	
	@Override
	public int geBankCodeCount(String partnerBankCodeValue, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select Count(PartnerBankCode) From PartnerBanks");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PartnerBankCode = :PartnerBankCode");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("PartnerBankCode", partnerBankCodeValue);

		try {
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");

		return count;
	}

	

}