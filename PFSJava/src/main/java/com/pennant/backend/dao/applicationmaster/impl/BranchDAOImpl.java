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
 * FileName    		:  BranchDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster.impl;

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
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>Branch model</b> class.<br>
 * 
 */
public class BranchDAOImpl extends BasisCodeDAO<Branch> implements BranchDAO {

	private static Logger logger = Logger.getLogger(BranchDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public BranchDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record  Branches details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Branch
	 */
	@Override
	public Branch getBranchById(final String id, String type) {
		logger.debug(Literal.ENTERING);
		
		Branch branch = new Branch();
		branch.setId(id);		
	
		StringBuilder selectSql = new StringBuilder("SELECT  BranchCode, BranchDesc, BranchAddrLine1," );
		selectSql.append(" BranchAddrLine2, BranchPOBox, BranchCity, BranchProvince, BranchCountry,");
		selectSql.append(" BranchFax, BranchTel, BranchSwiftBankCde, BranchSwiftCountry," );
		selectSql.append(" BranchSwiftLocCode, BranchSwiftBrnCde, BranchSortCode, BranchIsActive, NewBranchCode, MiniBranch,BranchType, ParentBranch, Region, BankRefNo," );
		selectSql.append(" BranchAddrHNbr,BranchFlatNbr,BranchAddrStreet, PinCode,");
		if(type.contains("View")){
			selectSql.append(" lovDescBranchCityName,lovDescBranchProvinceName,lovDescBranchCountryName," );
			selectSql.append("lovDescBranchSwiftCountryName,NewBranchDesc,parentBranchDesc, pinAreaDesc,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId " );
		selectSql.append(" FROM  RMTBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BranchCode =:BranchCode");

		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(branch);
		RowMapper<Branch> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(Branch.class);
		
		try{
			branch = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			branch = null;
		}
		
		logger.debug(Literal.LEAVING);
		return branch;
	}
	
	@Override
	public boolean isDuplicateKey(String branchCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "BranchCode = :branchCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTBranches", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTBranches_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTBranches_Temp", "RMTBranches" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("branchCode", branchCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public String save(Branch branch, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into RMTBranches" );
		sql.append(tableType.getSuffix());
		sql.append(" (BranchCode, BranchDesc, BranchAddrLine1, BranchAddrLine2, BranchPOBox," );
		sql.append(" BranchCity, BranchProvince, BranchCountry, BranchFax, BranchTel," );
		sql.append(" BranchSwiftBankCde, BranchSwiftCountry, BranchSwiftLocCode," );
		sql.append(" BranchSwiftBrnCde, BranchSortCode, BranchIsActive, NewBranchCode, MiniBranch,BranchType, ParentBranch, Region, BankRefNo," );
		sql.append(" BranchAddrHNbr, BranchFlatNbr, BranchAddrStreet, PinCode,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(:BranchCode, :BranchDesc, :BranchAddrLine1, :BranchAddrLine2,");
		sql.append(" :BranchPOBox, :BranchCity, :BranchProvince, :BranchCountry, :BranchFax," );
		sql.append(" :BranchTel, :BranchSwiftBankCde, :BranchSwiftCountry, :BranchSwiftLocCode," );
		sql.append(" :BranchSwiftBrnCde, :BranchSortCode, :BranchIsActive, :NewBranchCode, :MiniBranch, :BranchType, :ParentBranch, :Region, :BankRefNo,");
		sql.append(" :BranchAddrHNbr, :BranchFlatNbr, :BranchAddrStreet, :PinCode,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," ); 
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branch);
		
		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return branch.getId();
	}
	
	@Override
	public void update(Branch branch, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update RMTBranches");
		sql.append(tableType.getSuffix());
		sql.append(" set BranchDesc = :BranchDesc,");
		sql.append(" BranchAddrLine1 = :BranchAddrLine1, BranchAddrLine2 = :BranchAddrLine2,");
		sql.append(" BranchPOBox = :BranchPOBox, BranchCity = :BranchCity,");
		sql.append(" BranchProvince = :BranchProvince, BranchCountry = :BranchCountry,");
		sql.append(" BranchFax = :BranchFax,  BranchSwiftCountry = :BranchSwiftCountry,");
		sql.append(" BranchSwiftBankCde = :BranchSwiftBankCde, BranchTel = :BranchTel,");
		sql.append(" BranchSwiftLocCode = :BranchSwiftLocCode, BranchSortCode = :BranchSortCode,");
		sql.append(" BranchSwiftBrnCde = :BranchSwiftBrnCde, BranchIsActive = :BranchIsActive, NewBranchCode = :NewBranchCode,");
		sql.append(" BranchAddrHNbr = :BranchAddrHNbr, BranchFlatNbr = :BranchFlatNbr, BranchAddrStreet = :BranchAddrStreet,");
		sql.append(" MiniBranch = :MiniBranch,BranchType = :BranchType, ParentBranch = :ParentBranch, Region = :Region, BankRefNo = :BankRefNo,");
		sql.append(" PinCode = :PinCode, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where BranchCode =:BranchCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branch);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public void delete(Branch branch, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" delete from RMTBranches" );
		sql.append(tableType.getSuffix());
		sql.append(" where BranchCode =:BranchCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
	    logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branch);
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
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * Method for updating existing finance branchs with new branch 
	 * 
	 * @param branch
	 * @param type
	 * @return
	 */
	@Override
	public void updateFinanceBranch(Branch branch, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceMain" );
		updateSql.append(StringUtils.trimToEmpty(type) );
		updateSql.append(" Set FinBranch = :NewBranchCode  Where FinBranch =:BranchCode " );
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(branch);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}	
	
	/**
	 * Method for updating application state  
	 * 
	 * @param phaseName
	 * @param phaseValue
	 * @return
	 */
	@Override
	public void updateApplicationAccess(String phaseName,String phaseValue) {
		logger.debug("Entering");
		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("SysParmCode", phaseName);
		mapSource.addValue("SysParmValue", phaseValue);
		
		StringBuilder updateSql = new StringBuilder(" UPDATE SMTparameters SET SysParmValue = :SysParmValue ");
		updateSql.append(" Where SysParmCode = :SysParmCode  ");
		
		logger.debug("updateSql: "+ updateSql.toString());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), mapSource);
		logger.debug("Leaving");
	}

	@Override
	public boolean isPinCodeExists(String pinCode) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PinCode", pinCode);
		
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(PinCode)");
		selectSql.append(" From RMTBranches_View ");
		selectSql.append(" Where PinCode=:PinCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		int rcdCount =  this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		
		logger.debug("Leaving");
		return rcdCount > 0 ? true : false;
	}
}