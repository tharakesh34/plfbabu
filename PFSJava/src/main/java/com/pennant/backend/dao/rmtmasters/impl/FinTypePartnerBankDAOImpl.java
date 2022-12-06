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
 * * FileName : FinTypePartnerBankDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * *
 * Modified Date : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.rmtmasters.impl;

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

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>FinTypePartnerBank</code> with set of CRUD operations.
 */
public class FinTypePartnerBankDAOImpl extends SequenceDao<FinTypePartnerBank> implements FinTypePartnerBankDAO {
	private static Logger logger = LogManager.getLogger(FinTypePartnerBankDAOImpl.class);

	public FinTypePartnerBankDAOImpl() {
		super();
	}

	@Override
	public FinTypePartnerBank getFinTypePartnerBank(String finType, long iD, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" iD, finType, purpose, paymentMode, partnerBankID, vanApplicable, BranchCode, ClusterId");
		if (type.contains("View")) {
			sql.append(", PartnerBankName, PartnerBankCode, BranchDesc, Name, ClusterCode, ClusterType");
		}
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From FinTypePartnerBanks");
		sql.append(type);
		sql.append(" Where iD = :iD and FinType = :FinType");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		FinTypePartnerBank finTypePartnerBank = new FinTypePartnerBank();
		finTypePartnerBank.setID(iD);
		finTypePartnerBank.setFinType(finType);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finTypePartnerBank);
		RowMapper<FinTypePartnerBank> rowMapper = BeanPropertyRowMapper.newInstance(FinTypePartnerBank.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinTypePartnerBank> getFinTypePartnerBank(String finType, String type) {
		logger.debug(Literal.ENTERING);

		FinTypePartnerBank finTypePartnerBank = new FinTypePartnerBank();
		finTypePartnerBank.setFinType(finType);

		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" iD, finType, purpose, paymentMode, partnerBankID, vanApplicable, BranchCode, ClusterId");
		if (type.contains("View")) {
			sql.append(", PartnerBankName, PartnerBankCode, BranchDesc, Name, ClusterCode, ClusterType");
		}
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From FinTypePartnerBanks");
		sql.append(type);
		sql.append(" Where FinType = :FinType");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypePartnerBank);
		RowMapper<FinTypePartnerBank> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypePartnerBank.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public String save(FinTypePartnerBank finTypePartnerBank, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		if (finTypePartnerBank.getId() == Long.MIN_VALUE) {
			finTypePartnerBank.setId(getNextValue("SeqFinTypePartnerBanks"));
			logger.debug("get NextID:" + finTypePartnerBank.getId());
		}
		StringBuilder sql = new StringBuilder(" insert into FinTypePartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append(" (iD, finType, purpose, paymentMode, partnerBankID, vanApplicable, BranchCode, ClusterId");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :iD, :finType, :purpose, :paymentMode, :partnerBankID, :vanApplicable, :branchCode, :clusterId");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId");
		sql.append(", :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finTypePartnerBank);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(finTypePartnerBank.getID());
	}

	@Override
	public void update(FinTypePartnerBank finTypePartnerBank, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update FinTypePartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append("  set finType = :finType, purpose = :purpose, paymentMode = :paymentMode");
		sql.append(", partnerBankID = :partnerBankID, vanApplicable = :vanApplicable, branchCode = :branchCode");
		sql.append(", clusterId = :clusterId, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RoleCode = :RoleCode, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where iD = :iD ");
		// sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finTypePartnerBank);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(FinTypePartnerBank finTypePartnerBank, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from FinTypePartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append(" where iD = :iD ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finTypePartnerBank);
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

	@Override
	public void deleteByFinType(String finType, String tableType) {
		logger.debug(Literal.ENTERING);

		FinTypePartnerBank finTypePartnerBank = new FinTypePartnerBank();
		finTypePartnerBank.setFinType(finType);

		// Prepare the SQL.
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypePartnerBanks");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where FinType =:FinType");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypePartnerBank);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public int getPartnerBankCount(String finType, String paymentType, String purpose, long partnerBankID) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Fintype", finType);
		source.addValue("PaymentMode", paymentType);
		source.addValue("Purpose", purpose);
		source.addValue("PartnerBankID", partnerBankID);

		StringBuilder sql = new StringBuilder("SELECT COUNT(*) From FinTypePartnerBanks");
		sql.append(" Where Fintype = :Fintype AND PaymentMode = :PaymentMode");
		sql.append(" AND Purpose = :Purpose AND PartnerBankID = :PartnerBankID");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			return 0;
		}
	}

	/**
	 * Method for Fetching Count for Assigned PartnerBank
	 */
	@Override
	public int getAssignedPartnerBankCount(long partnerBankId, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PartnerBankId", partnerBankId);

		StringBuilder selectSql = new StringBuilder(" Select Count(1) ");
		selectSql.append(" From FinTypePartnerBanks");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PartnerBankId = :PartnerBankId ");

		logger.debug("selectSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public FinTypePartnerBank getFinTypePartnerBankByPartnerBankCode(String partnerBankCode, String finType,
			String paymentMode) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append("ID, FinType, Purpose, PaymentMode, PARTNERBANKID, PartnerBankCode, PARTNERBANKNAME, ACTIVE");
		sql.append(", ACCOUNTNO, ACCOUNTTYPE, ENTITYCODE, branchcode, branchdesc, clusterid, name, ClusterType");
		sql.append(", VERSION, LASTMNTBY, LASTMNTON, RECORDSTATUS, ROLECODE");
		sql.append(", NEXTROLECODE, TASKID, NEXTTASKID, RECORDTYPE, WORKFLOWID From FinTypePartnerBanks_AView");
		sql.append(" Where PartnerBankCode = :PartnerBankCode and FinType = :FinType And Purpose = :Purpose");
		sql.append(" And PaymentMode = :PaymentMode");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		FinTypePartnerBank finTypePartnerBank = new FinTypePartnerBank();
		finTypePartnerBank.setPartnerBankCode(partnerBankCode);
		finTypePartnerBank.setFinType(finType);
		finTypePartnerBank.setPurpose(AccountConstants.PARTNERSBANK_PAYMENT);
		finTypePartnerBank.setPaymentMode(paymentMode);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finTypePartnerBank);
		RowMapper<FinTypePartnerBank> rowMapper = BeanPropertyRowMapper.newInstance(FinTypePartnerBank.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinTypePartnerBank> getFintypePartnerBankByFinTypeAndPurpose(String finType, String purpose,
			String paymentType, String branchCode, long clusterId) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" iD, finType, purpose, paymentMode, partnerBankID, vanApplicable, BranchCode, BranchDesc");
		sql.append(", ClusterId, ClusterCode, Name, ClusterType, AccountNo, AccountType, PartnerbankCode");
		sql.append(", PartnerbankName,Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinTypePartnerBanks_AView");
		sql.append(" Where finType = :finType and Purpose = :purpose and PaymentMode = :paymentMode");
		if (ImplementationConstants.PARTNERBANK_MAPPING_BRANCH_OR_CLUSTER.equals("B")) {
			sql.append(" And (BranchCode = :branchCode)");
		} else {
			sql.append(" And (ClusterId = :clusterId)");
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		FinTypePartnerBank finTypePartnerBank = new FinTypePartnerBank();
		finTypePartnerBank.setFinType(finType);
		finTypePartnerBank.setPurpose(purpose);
		finTypePartnerBank.setPaymentMode(paymentType);
		if (ImplementationConstants.PARTNERBANK_MAPPING_BRANCH_OR_CLUSTER.equals("B")) {
			finTypePartnerBank.setBranchCode(branchCode);
		} else {
			finTypePartnerBank.setClusterId(clusterId);
		}

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finTypePartnerBank);
		RowMapper<FinTypePartnerBank> rowMapper = BeanPropertyRowMapper.newInstance(FinTypePartnerBank.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);

	}

	@Override
	public List<Long> getClusterByPartnerbankCode(long partnerbankId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Distinct ClusterId");
		sql.append(" From FinTypePartnerBanks");
		sql.append(" Where partnerbankId = ? And clusterId is not null");

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, JdbcUtil.getLong(partnerbankId));
		}, (rs, rowNum) -> {
			return rs.getLong("ClusterId");
		});
	}

	@Override
	public List<FinTypePartnerBank> getFintypePartnerBankByBranch(List<String> branchCode, long clusterId) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" iD, finType, purpose, paymentMode, partnerBankID, vanApplicable, BranchCode, BranchDesc");
		sql.append(", ClusterId, ClusterCode, Name, ClusterType, AccountNo, AccountType, Partnerbankcode");
		sql.append(", PartnerbankName, Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinTypePartnerBanks_AView");
		sql.append(" Where");
		if (ImplementationConstants.PARTNERBANK_MAPPING_BRANCH_OR_CLUSTER.equals("B")) {
			sql.append(" (BranchCode in (:branchCode))");
		} else if (ImplementationConstants.PARTNERBANK_MAPPING_BRANCH_OR_CLUSTER.equals("C")) {
			sql.append(" (ClusterId in (:clusterId))");
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinTypePartnerBank> rowMapper = BeanPropertyRowMapper.newInstance(FinTypePartnerBank.class);

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		if (ImplementationConstants.PARTNERBANK_MAPPING_BRANCH_OR_CLUSTER.equals("B")) {
			mapSqlParameterSource.addValue("branchCode", branchCode);
		} else if (ImplementationConstants.PARTNERBANK_MAPPING_BRANCH_OR_CLUSTER.equals("C")) {
			mapSqlParameterSource.addValue("clusterId", clusterId);
		}

		return jdbcTemplate.query(sql.toString(), mapSqlParameterSource, rowMapper);

	}

	@Override
	public int getPartnerBankCountByCluster(String finType, String paymentType, String purpose, long partnerBankID,
			String branchCode, long clusterId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		source.addValue("Fintype", finType);
		source.addValue("PaymentMode", paymentType);
		source.addValue("Purpose", purpose);
		source.addValue("PartnerBankID", partnerBankID);
		if (ImplementationConstants.PARTNERBANK_MAPPING_BRANCH_OR_CLUSTER.equals("B")) {
			source.addValue("branchCode", branchCode);
		} else if (ImplementationConstants.PARTNERBANK_MAPPING_BRANCH_OR_CLUSTER.equals("C")) {
			source.addValue("clusterId", clusterId);
		}

		StringBuilder sql = new StringBuilder("SELECT COUNT(*) From FinTypePartnerBanks");
		sql.append(" Where Fintype = :Fintype AND PaymentMode = :PaymentMode");
		sql.append(" AND Purpose = :Purpose AND PartnerBankID = :PartnerBankID");
		if (ImplementationConstants.PARTNERBANK_MAPPING_BRANCH_OR_CLUSTER.equals("B")) {
			sql.append(" AND BranchCode = :branchCode");
		} else if (ImplementationConstants.PARTNERBANK_MAPPING_BRANCH_OR_CLUSTER.equals("C")) {
			sql.append(" AND ClusterId = :clusterId");
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			return 0;
		}
	}

	@Override
	public List<FinTypePartnerBank> getFinTypePartnerBank(String finType, String type, String mode, String purpose,
			String entityCode) {
		logger.debug(Literal.ENTERING);

		FinTypePartnerBank finTypePartnerBank = new FinTypePartnerBank();
		finTypePartnerBank.setFinType(finType);
		finTypePartnerBank.setPaymentMode(mode);
		finTypePartnerBank.setPurpose(purpose);
		finTypePartnerBank.setEntityCode(entityCode);

		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" iD, finType, purpose, paymentMode, partnerBankID, vanApplicable, branchCode, clusterId");
		if (type.contains("View")) {
			sql.append(", PartnerBankName, PartnerBankCode, BranchDesc, ClusterCode, Name");
		}
		if (type.contains("AView")) {
			sql.append(", AccountNo, AccountType");
		}
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From FinTypePartnerBanks");
		sql.append(type);
		sql.append(" Where FinType = :FinType and PaymentMode = :PaymentMode and  Purpose = :Purpose And Active=1 ");
		if (type.contains("AView")) {
			sql.append("and  EntityCode = :EntityCode");
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypePartnerBank);
		RowMapper<FinTypePartnerBank> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypePartnerBank.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

}
