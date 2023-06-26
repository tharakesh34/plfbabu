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
 * * FileName : BranchDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified Date :
 * 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>Branch model</b> class.<br>
 * 
 */
public class BranchDAOImpl extends BasicDao<Branch> implements BranchDAO {
	private static Logger logger = LogManager.getLogger(BranchDAOImpl.class);

	public BranchDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Branches details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Branch
	 */
	@Override
	public Branch getBranchById(final String id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BranchCode, BranchDesc, BranchAddrLine1, BranchAddrLine2, BranchPOBox, BranchCity");
		sql.append(", BranchProvince, BranchCountry, BranchFax, BranchTel, BranchSwiftBankCde, BranchSwiftCountry");
		sql.append(", BranchSwiftLocCode, BranchSwiftBrnCde, BranchSortCode, BranchIsActive, NewBranchCode");
		sql.append(", MiniBranch, BranchType, ParentBranch, Region, BankRefNo, BranchAddrHNbr, BranchFlatNbr");
		sql.append(
				", BranchAddrStreet, PinCode, DefChequeDDPrintLoc, Entity, ClusterId, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, PinCodeId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescBranchCityName, LovDescBranchProvinceName, LovDescBranchCountryName");
			sql.append(", LovDescBranchSwiftCountryName, NewBranchDesc, ParentBranchDesc");
			sql.append(", PinAreaDesc, EntityDesc, ClusterCode, ClusterName");
		}

		sql.append(" from RMTBranches");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BranchCode = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Branch b = new Branch();

				b.setBranchCode(rs.getString("BranchCode"));
				b.setBranchDesc(rs.getString("BranchDesc"));
				b.setBranchAddrLine1(rs.getString("BranchAddrLine1"));
				b.setBranchAddrLine2(rs.getString("BranchAddrLine2"));
				b.setBranchPOBox(rs.getString("BranchPOBox"));
				b.setBranchCity(rs.getString("BranchCity"));
				b.setBranchProvince(rs.getString("BranchProvince"));
				b.setBranchCountry(rs.getString("BranchCountry"));
				b.setBranchFax(rs.getString("BranchFax"));
				b.setBranchTel(rs.getString("BranchTel"));
				b.setBranchSwiftBankCde(rs.getString("BranchSwiftBankCde"));
				b.setBranchSwiftCountry(rs.getString("BranchSwiftCountry"));
				b.setBranchSwiftLocCode(rs.getString("BranchSwiftLocCode"));
				b.setBranchSwiftBrnCde(rs.getString("BranchSwiftBrnCde"));
				b.setBranchSortCode(rs.getString("BranchSortCode"));
				b.setBranchIsActive(rs.getBoolean("BranchIsActive"));
				b.setNewBranchCode(rs.getString("NewBranchCode"));
				b.setMiniBranch(rs.getBoolean("MiniBranch"));
				b.setBranchType(rs.getString("BranchType"));
				b.setParentBranch(rs.getString("ParentBranch"));
				b.setRegion(rs.getString("Region"));
				b.setBankRefNo(rs.getString("BankRefNo"));
				b.setBranchAddrHNbr(rs.getString("BranchAddrHNbr"));
				b.setBranchFlatNbr(rs.getString("BranchFlatNbr"));
				b.setBranchAddrStreet(rs.getString("BranchAddrStreet"));
				b.setPinCode(rs.getString("PinCode"));
				b.setDefChequeDDPrintLoc(rs.getString("DefChequeDDPrintLoc"));
				b.setEntity(rs.getString("Entity"));
				b.setClusterId(JdbcUtil.getLong(rs.getObject("ClusterId")));
				b.setVersion(rs.getInt("Version"));
				b.setLastMntBy(rs.getLong("LastMntBy"));
				b.setLastMntOn(rs.getTimestamp("LastMntOn"));
				b.setRecordStatus(rs.getString("RecordStatus"));
				b.setRoleCode(rs.getString("RoleCode"));
				b.setNextRoleCode(rs.getString("NextRoleCode"));
				b.setTaskId(rs.getString("TaskId"));
				b.setNextTaskId(rs.getString("NextTaskId"));
				b.setRecordType(rs.getString("RecordType"));
				b.setWorkflowId(rs.getLong("WorkflowId"));
				b.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					b.setLovDescBranchCityName(rs.getString("LovDescBranchCityName"));
					b.setLovDescBranchProvinceName(rs.getString("LovDescBranchProvinceName"));
					b.setLovDescBranchCountryName(rs.getString("LovDescBranchCountryName"));
					b.setLovDescBranchSwiftCountryName(rs.getString("LovDescBranchSwiftCountryName"));
					b.setNewBranchDesc(rs.getString("NewBranchDesc"));
					b.setParentBranchDesc(rs.getString("ParentBranchDesc"));
					b.setPinAreaDesc(rs.getString("PinAreaDesc"));
					b.setEntityDesc(rs.getString("EntityDesc"));
					b.setClusterCode(rs.getString("ClusterCode"));
					b.setClusterName(rs.getString("ClusterName"));
				}

				return b;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		logger.debug(Literal.LEAVING);
		return null;
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

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

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
		StringBuilder sql = new StringBuilder("insert into RMTBranches");
		sql.append(tableType.getSuffix());
		sql.append(" (BranchCode, BranchDesc, BranchAddrLine1, BranchAddrLine2, BranchPOBox,");
		sql.append(" BranchCity, BranchProvince, BranchCountry, BranchFax, BranchTel,");
		sql.append(" BranchSwiftBankCde, BranchSwiftCountry, BranchSwiftLocCode,");
		sql.append(" BranchSwiftBrnCde, BranchSortCode, BranchIsActive, NewBranchCode, MiniBranch,");
		sql.append(" BranchType, ParentBranch, Region, BankRefNo, BranchAddrHNbr, BranchFlatNbr,");
		sql.append(" BranchAddrStreet, PinCode, PinCodeId, DefChequeDDPrintLoc,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId, Entity, ClusterId)");
		sql.append(" values(:BranchCode, :BranchDesc, :BranchAddrLine1, :BranchAddrLine2,");
		sql.append(" :BranchPOBox, :BranchCity, :BranchProvince, :BranchCountry, :BranchFax,");
		sql.append(" :BranchTel, :BranchSwiftBankCde, :BranchSwiftCountry, :BranchSwiftLocCode,");
		sql.append(" :BranchSwiftBrnCde, :BranchSortCode, :BranchIsActive, :NewBranchCode, :MiniBranch,");
		sql.append(" :BranchType, :ParentBranch, :Region, :BankRefNo, :BranchAddrHNbr, :BranchFlatNbr,");
		sql.append(" :BranchAddrStreet, :PinCode, :PinCodeId, :DefChequeDDPrintLoc,");
		sql.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId, :Entity, :ClusterId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branch);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
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
		sql.append(" BranchSwiftBrnCde = :BranchSwiftBrnCde, BranchIsActive = :BranchIsActive,");
		sql.append(" NewBranchCode = :NewBranchCode, BranchAddrHNbr = :BranchAddrHNbr,");
		sql.append(" BranchFlatNbr = :BranchFlatNbr, BranchAddrStreet = :BranchAddrStreet, MiniBranch = :MiniBranch,");
		sql.append(" BranchType = :BranchType, ParentBranch = :ParentBranch, Region = :Region,");
		sql.append(" BankRefNo = :BankRefNo, PinCode = :PinCode, Entity = :Entity, ClusterId = :ClusterId,");
		sql.append(" PinCodeId = :PinCodeId, DefChequeDDPrintLoc = :DefChequeDDPrintLoc,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where BranchCode =:BranchCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branch);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

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
		StringBuilder sql = new StringBuilder(" delete from RMTBranches");
		sql.append(tableType.getSuffix());
		sql.append(" where BranchCode =:BranchCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branch);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
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
	 * Method for updating existing finance branchs with new branch
	 * 
	 * @param branch
	 * @param type
	 * @return
	 */
	@Override
	public void updateFinanceBranch(Branch branch, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinBranch = :NewBranchCode  Where FinBranch =:BranchCode ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(branch);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
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
	public void updateApplicationAccess(String phaseName, String phaseValue) {
		logger.debug("Entering");
		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("SysParmCode", phaseName);
		mapSource.addValue("SysParmValue", phaseValue);

		StringBuilder updateSql = new StringBuilder("UPDATE SMTparameters SET SysParmValue = :SysParmValue ");
		updateSql.append(" Where SysParmCode = :SysParmCode  ");

		logger.debug("updateSql: " + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), mapSource);
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
		int rcdCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);

		logger.debug("Leaving");
		return rcdCount > 0 ? true : false;
	}

	@Override
	public List<Branch> getBrachDetailsByBranchCode(List<String> finBranches) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("finBranches", finBranches);

		List<Branch> finFeeDetailsList = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" Select BranchCode, BRANCHPROVINCE from RMTBRANCHES");
		sql.append(" WHERE BranchCode in (:finBranches)");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<Branch> typeRowMapper = BeanPropertyRowMapper.newInstance(Branch.class);

		try {
			finFeeDetailsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finFeeDetailsList = new ArrayList<Branch>();
		} finally {
			logger.debug(Literal.LEAVING);
		}

		return finFeeDetailsList;
	}

	@Override
	public boolean getUnionTerrotory(String cpProvince) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("cpProvince", cpProvince);

		boolean unionterrotory = false;

		StringBuilder sql = new StringBuilder();
		sql.append(" Select UNIONTERRITORY from RMTCOUNTRYVSPROVINCE where CPPROVINCE = :cpProvince");

		logger.trace(Literal.SQL + sql.toString());

		try {
			unionterrotory = this.jdbcTemplate.queryForObject(sql.toString(), source, Boolean.class);
		} catch (EmptyResultDataAccessException e) {
			unionterrotory = false;
		} finally {
			logger.debug(Literal.LEAVING);
		}

		return unionterrotory;
	}

	@Override
	public String getBranchDesc(String id, String type) {
		logger.debug(Literal.ENTERING);

		Branch branch = new Branch();
		branch.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT  BranchDesc ");
		selectSql.append(" FROM  RMTBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BranchCode =:BranchCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(branch);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isActiveBranch(String branch) {
		String sql = "Select coalesce(count(BranchCode), 0) From RMTBranches Where BranchCode = ? and BranchIsActive = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, branch, 1) > 0;
	}

	@Override
	public List<String> getBranchCodeByClusterId(long clusterId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BranchCode");
		sql.append(" From RMTBranches");
		sql.append(" Where ClusterId = ?");

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, JdbcUtil.getLong(clusterId));
		}, (rs, rowNum) -> rs.getString("BranchCode"));
	}

	@Override
	public List<String> getBranchCodes(String entityCode, String clusterCode) {
		String sql = "Select b.BranchCode From RMTBranches b Inner Join Clusters c on c.ID = b.ClusterID Where c.Entity = ? and c.Code = ? order by b.LastMntOn";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForList(sql, String.class, entityCode, clusterCode);
	}

	@Override
	public List<String> getBranchCodesByEntity(String entityCode) {
		String sql = "Select BranchCode From RMTBranches Where Entity = ? order by LastMntOn";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForList(sql, String.class, entityCode);
	}

	@Override
	public List<String> getBranchCodesByClusterID(String entityCode, long clusterID) {
		String sql = "Select b.BranchCode From RMTBranches b Inner Join Clusters c on c.ID = b.ClusterID Where b.Entity = ? and c.ID = ? order by b.LastMntOn";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForList(sql, String.class, entityCode, clusterID);
	}

}