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

package com.pennanttech.pennapps.pff.verification.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>TechnicalVerification</code> with set of CRUD operations.
 */
public class TechnicalVerificationDAOImpl extends SequenceDao<TechnicalVerification>
		implements TechnicalVerificationDAO {
	@Autowired
	private ExtendedFieldDetailDAO extendedFieldDetailDAO;

	private static Logger logger = LogManager.getLogger(TechnicalVerificationDAOImpl.class);

	public TechnicalVerificationDAOImpl() {
		super();
	}

	@Override
	public String save(TechnicalVerification technicalVerification, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into verification_tv");
		sql.append(tableType.getSuffix());
		sql.append(" (verificationId, agentCode, agentName,  type,  verifiedDate, status, reason,");
		sql.append(" summaryRemarks, sourceFormName, verificationFormName, observationRemarks, valuationAmount,");
		sql.append(" documentName ,documentRef,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values (:verificationId, :agentCode, :agentName,  :type,  :verifiedDate, :status, :reason,");
		sql.append(" :summaryRemarks, :sourceFormName, :verificationFormName, :observationRemarks, :valuationAmount,");
		sql.append(" :documentName, :documentRef,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(technicalVerification);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return String.valueOf(technicalVerification.getId());
	}

	@Override
	public List<TechnicalVerification> getList(String keyReference) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select * From verification_tv_view");
		sql.append(" Where keyreference = :keyreference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("keyreference", keyReference);

		RowMapper<TechnicalVerification> rowMapper = BeanPropertyRowMapper.newInstance(TechnicalVerification.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

	@Override
	public List<TechnicalVerification> getList(String[] custCif) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append("select * from verification_tv_view");
		sql.append(" Where cif in(:custCif) and date is not null");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("custCif", Arrays.asList(custCif));

		RowMapper<TechnicalVerification> rowMapper = BeanPropertyRowMapper.newInstance(TechnicalVerification.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

	@Override
	public void saveCollateral(String reference, String collateralType, long verificationId) {
		logger.debug(Literal.ENTERING);

		List<String> filedNames = extendedFieldDetailDAO.getFieldNames(CollateralConstants.MODULE_NAME, collateralType);

		StringBuilder fileds = new StringBuilder("verificationId");
		for (String fieldName : filedNames) {
			if (fileds.length() > 0) {
				fileds.append(",");
			}
			fileds.append(fieldName);
		}

		StringBuilder sql = new StringBuilder();
		if (App.DATABASE == Database.ORACLE) {
			fileds.append(" ,Seqno,Reference,VERSION,LASTMNTBY,LASTMNTON,RECORDSTATUS,ROLECODE,");
			fileds.append(" NEXTROLECODE,TASKID,NEXTTASKID,RECORDTYPE,WORKFLOWID ");

			// Prepare the SQL.
			sql.append("insert into collateral_");
			sql.append(collateralType);
			sql.append("_ed_tv (").append(fileds.toString()).append(") select * from(");

			sql.append(" select ").append(fileds.toString().replace("verificationId", ":verificationId"))
					.append(" from (select * from collateral_");
			sql.append(collateralType).append("_ed");
			sql.append("_temp");
			sql.append(" t1  union all select * from collateral_");
			sql.append(collateralType).append("_ed");
			sql.append(" t1  where not exists (select 1 from collateral_");
			sql.append(collateralType).append("_ed");
			sql.append("_temp");
			sql.append(" where reference = t1.reference and seqno = t1.seqno))) t where t.reference = :reference ");
		} else {

			// Prepare the SQL.
			sql.append("insert into collateral_");
			sql.append(collateralType);
			sql.append("_ed_tv");

			sql.append(" select :verificationId,* from (select * from collateral_");
			sql.append(collateralType).append("_ed");
			sql.append("_temp");
			sql.append(" t1  union all select * from collateral_");
			sql.append(collateralType).append("_ed");
			sql.append(" t1  where not exists (select 1 from collateral_");
			sql.append(collateralType).append("_ed");
			sql.append("_temp");
			sql.append(" where reference = t1.reference and seqno = t1.seqno)) t where t.reference = :reference ");
		}

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("reference", reference);
		paramSource.addValue("verificationId", verificationId);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void update(TechnicalVerification tv, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update verification_tv");
		sql.append(tableType.getSuffix());
		sql.append(
				" set verifiedDate = :verifiedDate, type = :type, agentCode = :agentCode, agentName = :agentName, status = :status,");
		sql.append(
				" reason = :reason, summaryremarks = :summaryRemarks,  valuationAmount = :valuationAmount, Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" documentName = :documentName, documentRef = :documentRef,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where VerificationId = :VerificationId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(tv);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(TechnicalVerification technicalVerification, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from verification_tv");
		sql.append(tableType.getSuffix());
		sql.append(" where verificationid = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(technicalVerification);
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
	 * Fetching the TechnicalVerification details using the verification id
	 */
	@Override
	public TechnicalVerification getTechnicalVerification(long id, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select verificationId, agentCode, agentName, type,  verifiedDate, status, reason,");
		sql.append(" summaryRemarks, sourceFormName, verificationFormName, observationRemarks,  valuationAmount,");
		sql.append(" documentname, documentRef,");
		if (type.contains("View")) {
			sql.append(
					" cif, custId, custName, keyReference, collateralType, collateralRef,contactNumber1, createdon, ");
			sql.append(
					" contactNumber2, collateralCcy, collateralLoc, reasonCode, reasonDesc, agencyName, agency, productCategory, verificationcategory,");
			sql.append("loanType, lovDescLoanTypeName, sourcingBranch, lovDescSourcingBranch,");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  Verification_Tv");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where verificationId = :verificationId ");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("verificationId", id);

		RowMapper<TechnicalVerification> typeRowMapper = BeanPropertyRowMapper.newInstance(TechnicalVerification.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<TechnicalVerification> getTvListByCollRef(String collRef) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select * From verification_tv_view");
		sql.append(" Where collateralRef = :collateralRef");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("collateralRef", collRef);

		RowMapper<TechnicalVerification> rowMapper = BeanPropertyRowMapper.newInstance(TechnicalVerification.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

	@Override
	public List<Verification> getTvValuation(List<Long> verificationIDs, String type) {
		logger.debug(Literal.ENTERING); // Prepare the SQL.
		StringBuilder sql = new StringBuilder(
				"SELECT VERIFICATIONID AS ID, COLLATERALREF REFERENCEFOR, VALUATIONAMOUNT, AGENCYNAME, ");
		sql.append(
				" FINALVALAMT, DECISIONONVAL AS FINALVALDECISION, FINALVALREMARKS,COLLATERALTYPE,RECORDSTATUS AS TVRECORDSTATUS");
		sql.append(" FROM VERIFICATION_TV");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" WHERE RECORDSTATUS = :RECORDSTATUS ");
		sql.append(" AND VERIFICATIONID IN (:VERIFICATIONIDS) ");
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("VERIFICATIONIDS", verificationIDs);
		paramSource.addValue("RECORDSTATUS", PennantConstants.RCD_STATUS_APPROVED);
		RowMapper<Verification> typeRowMapper = BeanPropertyRowMapper.newInstance(Verification.class);

		return jdbcTemplate.query(sql.toString(), paramSource, typeRowMapper);
	}

	public void updateValuationAmount(Verification verification, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update verification_tv");
		sql.append(tableType.getSuffix());
		sql.append(
				" set valasperpe = :finalValAsPerPE, finalvalamt = :finalValAmt, decisiononval = :finalValDecision, finalvalremarks = :finalValRemarks ");
		sql.append(" where VerificationId = :id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(verification);
		jdbcTemplate.update(sql.toString(), paramSource);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public Map<String, Object> getCostOfPropertyValue(String collRef, String subModuleName, String column) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select T.").append(column).append(" from (select T1.");
		sql.append(column).append(", T1.reference from collateral_");
		sql.append(subModuleName);
		sql.append("_ed T1");
		sql.append(" union all");
		sql.append(" select T1.").append(column).append(", T1.reference from collateral_");
		sql.append(subModuleName);
		sql.append("_ed_temp T1");
		sql.append(" where not exists");
		sql.append(" (SELECT  1 FROM  collateral_");
		sql.append(subModuleName);
		sql.append("_ed");
		sql.append(" where reference = T1.reference))t where t.reference = :reference");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reference", collRef);

		try {
			return jdbcTemplate.queryForMap(sql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return new HashMap<>();
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			return new HashMap<>();
		}
	}

	@Override
	public String getPropertyCity(String collRef, String subModuleName) {

		String column = "CITY";
		// Agency filter issue based on Collateral City
		String value = ImplementationConstants.VER_TV_COLL_ED_ADDR_COLUMN;
		if (StringUtils.isNotBlank(value)) {
			column = value;
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" select t.").append(column).append(" from (select T1.").append(column);
		sql.append(", T1.reference from collateral_");
		sql.append(subModuleName);
		sql.append("_ed T1");
		sql.append(" union all");
		sql.append(" select T1.").append(column).append(", T1.reference from collateral_");
		sql.append(subModuleName);
		sql.append("_ed_temp T1");
		sql.append(" where not exists");
		sql.append(" (SELECT  1 FROM  collateral_");
		sql.append(subModuleName);
		sql.append("_ed");
		sql.append(" where reference = T1.reference))t where t.reference = :reference");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reference", collRef);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			return null;
		}
	}

	@Override
	public String getCollaterlType(long id) {
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT collateralType From verification_tv_view ");
		sql.append(" where verificationid = :id");

		// Execute the SQL, binding the arguments.
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", id);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<Verification> getTvListByCollRefAndFinRef(String collRef, String finRef) {
		String sql = "Select FinalValAmt, ValAsPerPE FinalValAsPerPE, ValuationAmount From Verification_TV_View Where CollateralRef = ? and KeyReference = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.query(sql, (rs, rowNum) -> {
			Verification ver = new Verification();

			ver.setFinalValAmt(rs.getBigDecimal("FinalValAmt"));
			ver.setFinalValAsPerPE(rs.getBigDecimal("FinalValAsPerPE"));
			ver.setValuationAmount(rs.getBigDecimal("ValuationAmount"));
			return ver;
		}, collRef, finRef);
	}
}
