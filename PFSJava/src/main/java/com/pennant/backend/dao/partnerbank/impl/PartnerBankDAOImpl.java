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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.partnerbank.PartnerBranchModes;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

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
				.append(", AcType, AlwFileDownload, InFavourLength, Active, AlwDisb, AlwPayment, AlwReceipt, HostGLCode, ProfitCenterID, CostCenterID, FileName");
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
	
	@Override
	public boolean isDuplicateKey(long partnerBankId, String PartnerBankCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "PartnerBankCode = :PartnerBankCode and PartnerBankId != :partnerBankId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("PartnerBanks", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("PartnerBanks_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "PartnerBanks_Temp", "PartnerBanks" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("partnerBankId", partnerBankId);
		paramSource.addValue("PartnerBankCode", PartnerBankCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	
	@Override
	public String save(PartnerBank partnerBank, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into PartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append(" ( PartnerBankId, PartnerBankCode, PartnerBankName, BankCode, BankBranchCode, BranchMICRCode, BranchIFSCCode, BranchCity, UtilityCode, AccountNo ");
		sql.append(", AcType, AlwFileDownload,  InFavourLength, Active, AlwDisb, AlwPayment, AlwReceipt, HostGLCode, ProfitCenterID, CostCenterID, FileName ");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values( :PartnerBankId, :PartnerBankCode, :PartnerBankName, :BankCode, :BankBranchCode, :BranchMICRCode, :BranchIFSCCode, :BranchCity, :UtilityCode, :AccountNo ");
		sql.append(", :AcType, :AlwFileDownload, :InFavourLength, :Active, :AlwDisb, :AlwPayment, :AlwReceipt, :HostGLCode, :ProfitCenterID, :CostCenterID, :FileName");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if(partnerBank.getPartnerBankId() == Long.MIN_VALUE){
			partnerBank.setPartnerBankId(getNextidviewDAO().getNextId("SEQPartnerBank"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(partnerBank);
		
		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(partnerBank.getPartnerBankId());
	}
	
	@Override
	public void update(PartnerBank partnerBank, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update PartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append(" set PartnerBankName = :PartnerBankName, BankCode = :BankCode, BankBranchCode = :BankBranchCode, BranchMICRCode = :BranchMICRCode, BranchIFSCCode = :BranchIFSCCode, BranchCity = :BranchCity, UtilityCode = :UtilityCode, AccountNo = :AccountNo");
		sql.append(" , AcType = :AcType, AlwFileDownload = :AlwFileDownload,  InFavourLength = :InFavourLength,  Active = :Active, AlwDisb = :AlwDisb, AlwPayment = :AlwPayment, AlwReceipt = :AlwReceipt, HostGLCode = :HostGLCode, ProfitCenterID = :ProfitCenterID, CostCenterID = :CostCenterID, FileName = :FileName");
		sql.append(", Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where PartnerBankId =:PartnerBankId");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(partnerBank);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PartnerBank partnerBank, TableType tableType) {
		logger.debug(Literal.ENTERING);
		

		StringBuilder sql = new StringBuilder("delete from PartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append(" Where PartnerBankId =:PartnerBankId");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(partnerBank);
		int recordCount = 0;
		
		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
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
		PartnerBankModes partnerBankModes= new PartnerBankModes();
		partnerBankModes.setPartnerBankId(partnerBank.getPartnerBankId());
		StringBuilder deleteSql = new StringBuilder("Delete From PartnerBankModes");
		deleteSql.append(" Where PartnerBankId =:PartnerBankId");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(partnerBankModes);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
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
	
	public List<PartnerBranchModes> getPartnerBranchModesId(long id) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PartnerBankId", id);

		StringBuilder selectSql = new StringBuilder("SELECT PartnerBankId, BranchCode, PaymentMode from PartnerBranchModes");
		selectSql.append(" Where PartnerBankId =:PartnerBankId");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<PartnerBranchModes> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PartnerBranchModes.class);
		List<PartnerBranchModes> PartnerBranchModeList = new ArrayList<PartnerBranchModes>();
		try {
			PartnerBranchModeList = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return PartnerBranchModeList;}

	/**
	 * Method for Deletion PartnerBranchModes Details  
	 */
	public void deletePartnerBranch(PartnerBank partnerBank) {
		logger.debug("Entering");
		PartnerBranchModes partnerBranchModes = new PartnerBranchModes();
		partnerBranchModes.setPartnerBankId(partnerBank.getPartnerBankId());
		StringBuilder deleteSql = new StringBuilder("Delete From PartnerBranchModes");
		deleteSql.append(" Where PartnerBankId =:PartnerBankId");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(partnerBranchModes);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			logger.error(e);
		}
		logger.debug("Leaving");

	}

	/**
	 * Method for Saving List Of PartnerBranchModes Details
	 */
	public void saveBranchList(List<PartnerBranchModes> partnerBranchModesList, long partnerBankId) {
		for (PartnerBranchModes partnerBranchModes : partnerBranchModesList) {
			partnerBranchModes.setPartnerBankId(partnerBankId);
		}
		StringBuilder insertSql = new StringBuilder("Insert Into PartnerBranchModes");
		insertSql.append(" ( PartnerBankId, BranchCode, PaymentMode)");
		insertSql.append(" Values(:PartnerBankId, :BranchCode, :PaymentMode)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(partnerBranchModesList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	@Override
	public int getPartnerBankbyBank(String bankCode, String type) {
		logger.debug("Entering");

		PartnerBank partnerBank = new PartnerBank();
		partnerBank.setBankCode(bankCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From PartnerBanks");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankCode =:BankCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(partnerBank);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
	
}