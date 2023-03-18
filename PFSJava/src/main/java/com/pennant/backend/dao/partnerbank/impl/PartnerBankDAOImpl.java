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
 * * FileName : PartnerBankDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-03-2017 * * Modified
 * Date : 09-03-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-03-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.partnerbank.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.partnerbank.PartnerBranchModes;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>PartnerBank model</b> class.<br>
 * 
 */

public class PartnerBankDAOImpl extends SequenceDao<PartnerBank> implements PartnerBankDAO {
	private static Logger logger = LogManager.getLogger(PartnerBankDAOImpl.class);

	public PartnerBankDAOImpl() {
		super();
	}

	@Override
	public PartnerBank getPartnerBankById(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PartnerBankId, PartnerBankCode, PartnerBankName, BankCode, BankBranchCode, BranchMICRCode");
		sql.append(", BranchIFSCCode, BranchCity, UtilityCode, AccountNo, AcType, AlwFileDownload");
		sql.append(", InFavourLength, Active, AlwDisb, AlwPayment, AlwReceipt, HostGLCode, ProfitCenterID");
		sql.append(", CostCenterID, FileName, Entity, VanCode, DownloadType");
		sql.append(", DataEngineConfigName, SponsorBankCode, ClientCode");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", BankCodeName, BankBranchCodeName, AcTypeName, EntityDesc");
		}

		sql.append(" From PartnerBanks");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PartnerBankId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<PartnerBank>() {
				@Override
				public PartnerBank mapRow(ResultSet rs, int rowNum) throws SQLException {
					PartnerBank pb = new PartnerBank();

					pb.setPartnerBankId(rs.getLong("PartnerBankId"));
					pb.setPartnerBankCode(rs.getString("PartnerBankCode"));
					pb.setPartnerBankName(rs.getString("PartnerBankName"));
					pb.setBankCode(rs.getString("BankCode"));
					pb.setBankBranchCode(rs.getString("BankBranchCode"));
					pb.setBranchMICRCode(rs.getString("BranchMICRCode"));
					pb.setBranchIFSCCode(rs.getString("BranchIFSCCode"));
					pb.setBranchCity(rs.getString("BranchCity"));
					pb.setUtilityCode(rs.getString("UtilityCode"));
					pb.setAccountNo(rs.getString("AccountNo"));
					pb.setAcType(rs.getString("AcType"));
					pb.setAlwFileDownload(rs.getBoolean("AlwFileDownload"));
					pb.setInFavourLength(rs.getInt("InFavourLength"));
					pb.setActive(rs.getBoolean("Active"));
					pb.setAlwDisb(rs.getBoolean("AlwDisb"));
					pb.setAlwPayment(rs.getBoolean("AlwPayment"));
					pb.setAlwReceipt(rs.getBoolean("AlwReceipt"));
					pb.setHostGLCode(rs.getString("HostGLCode"));
					pb.setProfitCenterID(rs.getString("ProfitCenterID"));
					pb.setCostCenterID(rs.getString("CostCenterID"));
					pb.setFileName(rs.getString("FileName"));
					pb.setEntity(rs.getString("Entity"));
					pb.setVanCode(rs.getString("VanCode"));
					pb.setDownloadType(rs.getString("DownloadType"));
					pb.setDataEngineConfigName(rs.getString("DataEngineConfigName"));
					pb.setSponsorBankCode(rs.getString("SponsorBankCode"));
					pb.setClientCode(rs.getString("ClientCode"));
					pb.setVersion(rs.getInt("Version"));
					pb.setLastMntBy(rs.getLong("LastMntBy"));
					pb.setLastMntOn(rs.getTimestamp("LastMntOn"));
					pb.setRecordStatus(rs.getString("RecordStatus"));
					pb.setRoleCode(rs.getString("RoleCode"));
					pb.setNextRoleCode(rs.getString("NextRoleCode"));
					pb.setTaskId(rs.getString("TaskId"));
					pb.setNextTaskId(rs.getString("NextTaskId"));
					pb.setRecordType(rs.getString("RecordType"));
					pb.setWorkflowId(rs.getLong("WorkflowId"));

					if (StringUtils.trimToEmpty(type).contains("View")) {
						pb.setBankCodeName(rs.getString("BankCodeName"));
						pb.setBankBranchCodeName(rs.getString("BankBranchCodeName"));
						pb.setAcTypeName(rs.getString("AcTypeName"));
						pb.setEntityDesc(rs.getString("EntityDesc"));
					}

					return pb;
				}
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

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
		sql.append(
				" ( PartnerBankId, PartnerBankCode, PartnerBankName, BankCode, BankBranchCode, BranchMICRCode, BranchIFSCCode, BranchCity, UtilityCode, AccountNo ");
		sql.append(
				", AcType, AlwFileDownload,  InFavourLength, Active, AlwDisb, AlwPayment, AlwReceipt, HostGLCode, ProfitCenterID, CostCenterID, FileName, Entity, VanCode ");
		sql.append(", DownloadType, DataEngineConfigName, SponsorBankCode, ClientCode ");
		sql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(
				" values( :PartnerBankId, :PartnerBankCode, :PartnerBankName, :BankCode, :BankBranchCode, :BranchMICRCode, :BranchIFSCCode, :BranchCity, :UtilityCode, :AccountNo ");
		sql.append(
				", :AcType, :AlwFileDownload, :InFavourLength, :Active, :AlwDisb, :AlwPayment, :AlwReceipt, :HostGLCode, :ProfitCenterID, :CostCenterID, :FileName, :Entity, :VanCode");
		sql.append(", :DownloadType, :DataEngineConfigName, :SponsorBankCode, :ClientCode ");
		sql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (partnerBank.getPartnerBankId() == Long.MIN_VALUE) {
			partnerBank.setPartnerBankId(getNextValue("SEQPartnerBank"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(partnerBank);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
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
		sql.append(
				" set PartnerBankName = :PartnerBankName, BankCode = :BankCode, BankBranchCode = :BankBranchCode, BranchMICRCode = :BranchMICRCode, BranchIFSCCode = :BranchIFSCCode, BranchCity = :BranchCity, UtilityCode = :UtilityCode, AccountNo = :AccountNo");
		sql.append(
				" , AcType = :AcType, AlwFileDownload = :AlwFileDownload,  InFavourLength = :InFavourLength,  Active = :Active, AlwDisb = :AlwDisb, AlwPayment = :AlwPayment, AlwReceipt = :AlwReceipt, HostGLCode = :HostGLCode, ProfitCenterID = :ProfitCenterID, CostCenterID = :CostCenterID, FileName = :FileName");

		sql.append(
				", Entity = :Entity , VanCode= :VanCode, DownloadType = :DownloadType, DataEngineConfigName =:DataEngineConfigName, SponsorBankCode =:SponsorBankCode, ClientCode =:ClientCode ");

		sql.append(
				", Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where PartnerBankId =:PartnerBankId");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(partnerBank);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

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
	 * Method for Saving List Of PartnerBankModes Details
	 */
	public void saveList(List<PartnerBankModes> list, long id) {

		for (PartnerBankModes partnerBankModes : list) {
			partnerBankModes.setPartnerBankId(id);
		}
		StringBuilder insertSql = new StringBuilder("Insert Into PartnerBankModes");
		insertSql.append(" ( PartnerBankId, Purpose, PaymentMode)");
		insertSql.append(" Values(:PartnerBankId,:Purpose, :PaymentMode)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(list.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Update List Of PartnerBankModes Details
	 */
	@Override
	public void updateList(List<PartnerBankModes> list) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update PartnerBankModes");
		updateSql.append(" Set Purpose= :Purpose, PaymentMode = :PaymentMode");
		updateSql.append(" Where PartnerBankId =:PartnerBankId  AND PaymentMode =:PaymentMode ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(list.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Method for Deletion PartnerBankModes Details
	 * 
	 * @param partnerBankList
	 */

	public void deletePartner(PartnerBank partnerBank) {
		logger.debug("Entering");
		PartnerBankModes partnerBankModes = new PartnerBankModes();
		partnerBankModes.setPartnerBankId(partnerBank.getPartnerBankId());
		StringBuilder deleteSql = new StringBuilder("Delete From PartnerBankModes");
		deleteSql.append(" Where PartnerBankId =:PartnerBankId");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(partnerBankModes);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
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
		RowMapper<PartnerBankModes> typeRowMapper = BeanPropertyRowMapper.newInstance(PartnerBankModes.class);

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public int geBankCodeCount(String partnerBankCodeValue, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select Count(PartnerBankCode) From PartnerBanks");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PartnerBankCode = :PartnerBankCode");
		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PartnerBankCode", partnerBankCodeValue);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	public List<PartnerBranchModes> getPartnerBranchModesId(long id) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PartnerBankId", id);

		StringBuilder selectSql = new StringBuilder(
				"SELECT PartnerBankId, BranchCode, PaymentMode from PartnerBranchModes");
		selectSql.append(" Where PartnerBankId =:PartnerBankId");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<PartnerBranchModes> typeRowMapper = BeanPropertyRowMapper.newInstance(PartnerBranchModes.class);

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

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
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
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
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
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
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * Method for get total number of records from PartnerBanks master table.<br>
	 * 
	 * @param entityCode
	 * 
	 * @return Integer
	 */
	@Override
	public boolean isEntityCodeExistsInPartnerBank(String entityCode, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Entity", entityCode);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM PartnerBanks");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE Entity= :Entity");

		logger.debug("insertSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class) > 0;
	}

	@Override
	public String getBankCodeById(long partnerBankId) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PartnerBankId", partnerBankId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT BankCode FROM PartnerBanks");
		selectSql.append(" WHERE PartnerBankId= :PartnerBankId");
		logger.debug(Literal.SQL + selectSql.toString());
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public PartnerBank getPartnerBankByCode(String partnerBankCode, String type) {

		logger.debug("Entering");
		PartnerBank partnerBank = new PartnerBank();
		partnerBank.setPartnerBankCode(partnerBankCode);
		partnerBank.setActive(true);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(
				"Select PartnerBankId, PartnerBankCode, PartnerBankName, BankCode, BankBranchCode, BranchMICRCode, BranchIFSCCode, BranchCity, UtilityCode, AccountNo ");
		selectSql.append(
				", AcType, AlwFileDownload, InFavourLength, Active, AlwDisb, AlwPayment, AlwReceipt, HostGLCode, ProfitCenterID, CostCenterID, FileName, Entity");
		selectSql.append(", DownloadType, DataEngineConfigName, SponsorBankCode, ClientCode ");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",BankCodeName,BankBranchCodeName,AcTypeName,Entitydesc");
		}

		selectSql.append(" From PartnerBanks");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PartnerBankCode =:PartnerBankCode AND Active =:Active");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(partnerBank);
		RowMapper<PartnerBank> typeRowMapper = BeanPropertyRowMapper.newInstance(PartnerBank.class);

		try {
			partnerBank = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			partnerBank = null;
		}
		logger.debug("Leaving");
		return partnerBank;

	}

	@Override
	public String getPartnerBankCodeById(long partnerBankId) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PartnerBankId", partnerBankId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT PartnerBankCode FROM PartnerBanks");
		selectSql.append(" WHERE PartnerBankId= :PartnerBankId");
		logger.debug(Literal.SQL + selectSql.toString());
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isPartnerBankCodeExistsByEntity(String entity, String partnerbankCode, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM PARTNERBANKS");
		sql.append(type);
		sql.append(" WHERE ENTITY = ? AND PARTNERBANKCODE = ?");

		return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { entity, partnerbankCode },
				Integer.class) > 0 ? true : false;
	}

	@Override
	public List<PartnerBankModes> getPartnerBankModes(long id, String purpose) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PartnerBankId, Purpose, PaymentMode");
		sql.append(" From PartnerBankModes");
		sql.append(" Where PartnerBankId = ? And Purpose = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, id);
			ps.setString(2, purpose);
		}, (rs, i) -> {
			PartnerBankModes pbm = new PartnerBankModes();
			pbm.setPartnerBankId(rs.getLong("PartnerBankId"));
			pbm.setPurpose(rs.getString("Purpose"));
			pbm.setPaymentMode(rs.getString("PaymentMode"));
			return pbm;
		});
	}

	@Override
	public PartnerBank getPartnerBankById(long partnerBankId) {
		String sql = "Select AccountNo, AcType From PartnerBanks Where PartnerBankId = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				PartnerBank pb = new PartnerBank();

				pb.setAccountNo(rs.getString("AccountNo"));
				pb.setAcType(rs.getString("AcType"));

				return pb;

			}, partnerBankId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long getPartnerBankID(String code) {
		String sql = "Select PartnerBankId From PartnerBanks Where PartnerBankCode = ? and Active = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Long.class, code, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return Long.MIN_VALUE;
		}
	}

	@Override
	public boolean getPartnerBankbyBankBranch(String bankCode) {
		String sql = "Select Count(BankBranchCode) From PartnerBanks Where BankBranchCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, bankCode) > 0;
	}
}