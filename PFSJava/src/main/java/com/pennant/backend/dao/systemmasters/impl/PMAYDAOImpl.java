package com.pennant.backend.dao.systemmasters.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.pennant.backend.dao.systemmasters.PMAYDAO;
import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.model.finance.PmayEligibilityLog;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class PMAYDAOImpl extends SequenceDao<PMAY> implements PMAYDAO {
	private static Logger logger = LogManager.getLogger(PMAYDAOImpl.class);

	public PMAYDAOImpl() {
		super();
	}

	@Override
	public PMAY getPMAY(String finReference, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference ,NotifiedTown ,CentralAssistance ,OwnedHouse, TownCode,");
		sql.append(" CarpetArea ,HouseholdAnnIncome ,BalanceTransfer ,PrimaryApplicant ,PrprtyOwnedByWomen ,");
		sql.append(" TransactionFinType ,Product ,WaterSupply ,Drinage ,Electricity ,PmayCategory,");
		if (type.contains("View")) {
			sql.append(" CustCif , custShrtName ,TownName,");
		}
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		sql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PMAY");
		sql.append(type);
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<PMAY> rowMapper = BeanPropertyRowMapper.newInstance(PMAY.class);
		try {
			return jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public boolean isDuplicateKey(String finReference, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "finReference = :finReference";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("PMAY", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("PMAY_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "PMAY_Temp", "PMAY" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("finReference", finReference);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(PMAY pmay, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into PMAY");
		sql.append(tableType.getSuffix());
		sql.append("(finReference , notifiedTown,TownCode, centralAssistance,ownedHouse, carpetArea,");
		sql.append("householdAnnIncome, balanceTransfer, primaryApplicant, prprtyOwnedByWomen, ");
		sql.append("transactionFinType, product, waterSupply, drinage, electricity , pmayCategory, ");
		sql.append("Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		sql.append("NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId) ");
		sql.append("values(");
		sql.append(":FinReference , :NotifiedTown, :TownCode, :CentralAssistance ,:OwnedHouse, :CarpetArea, ");
		sql.append(":HouseholdAnnIncome, :BalanceTransfer, :PrimaryApplicant, :PrprtyOwnedByWomen, ");
		sql.append(":TransactionFinType, :Product, :WaterSupply, :Drinage, :Electricity, :PmayCategory, ");
		sql.append(":Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		sql.append(":NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pmay);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return pmay.getFinReference();
	}

	@Override
	public void update(PMAY pmay, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update PMAY");
		sql.append(tableType.getSuffix());
		sql.append(
				" set NotifiedTown = :NotifiedTown, TownCode = :TownCode , CentralAssistance = :CentralAssistance, ");
		sql.append(" OwnedHouse = :OwnedHouse,  CarpetArea = :CarpetArea,HouseholdAnnIncome = :HouseholdAnnIncome, ");
		sql.append(" BalanceTransfer = :BalanceTransfer, PrimaryApplicant = :PrimaryApplicant, ");
		sql.append(" TransactionFinType = :TransactionFinType, Product = :Product,  ");
		sql.append(" PrprtyOwnedByWomen = :PrprtyOwnedByWomen,WaterSupply = :WaterSupply,");
		sql.append(" Drinage = :Drinage, Electricity = :Electricity, PmayCategory = :PmayCategory, ");
		sql.append(" Version = :Version, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, ");
		sql.append(" RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		sql.append(" NextTaskId = :NextTaskId,RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where finReference = :FinReference");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pmay);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PMAY pmay, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from PMAY");
		sql.append(tableType.getSuffix());
		sql.append(" Where finReference = :finReference");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pmay);
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
	public boolean isFinReferenceExists(String finReference) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from PMAY ");
		sql.append(" Where FinReference = :FinReference ");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}
		return false;

	}

	@Override
	public String save(PmayEligibilityLog pMaylog, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into PmayEligibilityLog");
		sql.append(tableType.getSuffix());
		sql.append(" ( finReference , recordId, pmayStatus,");
		sql.append(" errorCode, errorDesc, applicantId, remarks, RespJson ,ReqJson )");

		sql.append(" values(");
		sql.append(" :FinReference , :RecordId, :PmayStatus,");
		sql.append(" :ErrorCode, :ErrorDesc, :ApplicantId, :Remarks, :RespJson , :ReqJson )");
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pMaylog);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return pMaylog.getFinReference();
	}

	@Override
	public void update(PmayEligibilityLog pMaylog, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update PmayEligibilityLog");
		sql.append(tableType.getSuffix());
		sql.append("  set  RecordId = :RecordId, ");
		sql.append("  PmayStatus = :PmayStatus, ErrorCode = :ErrorCode, ErrorDesc = :ErrorDesc, ");
		sql.append("  ApplicantId = :ApplicantId, Remarks = :Remarks, RespJson = :RespJson , ReqJson = :ReqJson");
		sql.append("  Where finReference = :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pMaylog);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public PmayEligibilityLog getEligibilityLog(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" finReference , recordId, pmayStatus,");
		sql.append(" errorCode, errorDesc, applicantId, remarks, RespJson ,ReqJson  ");
		sql.append(" From PmayEligibilityLog");
		sql.append(type);
		sql.append(" Where finReference = :finReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		PmayEligibilityLog log = new PmayEligibilityLog();
		log.setFinReference(finReference);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(log);
		RowMapper<PmayEligibilityLog> rowMapper = BeanPropertyRowMapper.newInstance(PmayEligibilityLog.class);

		try {
			log = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			log = null;
		}

		logger.debug(Literal.LEAVING);
		return log;
	}

	@Override
	public long generateDocSeq() {
		logger.debug(Literal.ENTERING);
		long pmayEligibilityLogId = getNextValue("SeqPmayEligibilityLog");
		logger.debug("get NextID:" + pmayEligibilityLogId);
		logger.debug(Literal.LEAVING);
		return pmayEligibilityLogId;
	}

	@Override
	public List<PmayEligibilityLog> getEligibilityLogList(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" finReference , recordId, pmayStatus,");
		sql.append(" errorCode, errorDesc, applicantId, remarks, RespJson ,ReqJson ");
		sql.append(" From PmayEligibilityLog");
		sql.append(type);
		sql.append(" Where finReference = :finReference Order by recordId Asc");

		source.addValue("finReference", finReference);
		RowMapper<PmayEligibilityLog> typeRowMapper = BeanPropertyRowMapper.newInstance(PmayEligibilityLog.class);

		logger.debug("sql: " + sql.toString());

		List<PmayEligibilityLog> pmayEligibilityLoglist = new ArrayList<>();
		try {
			pmayEligibilityLoglist = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			pmayEligibilityLoglist = new ArrayList<>();
		}

		logger.debug(Literal.LEAVING);

		return pmayEligibilityLoglist;
	}

	@Override
	public List<PmayEligibilityLog> getAllRecordIdForPmay() {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" t3.FINREFERENCE,t3.RECORDID,t3.PMAYSTATUS,t3.ERRORCODE,t3.ERRORDESC,");
		sql.append(" t3.APPLICANTID,t3.REMARKS,t3.RESPJSON,t3.REQJSON from financemain_view t1");
		sql.append(" inner join ( select *  from PMAY_Temp ");
		sql.append(" union  ");
		sql.append(" select *  from PMAY ) t2 on t1.finreference = t2.finreference");
		sql.append(" inner join PMAYELIGIBILITYLOG t3 on t2.finreference = t3.finreference");
		sql.append(" where t1.recordstatus <> 'Rejected' and  t1.CLOSINGSTATUS is null and");
		sql.append(" t1.finisactive= 1 and");
		sql.append(" t2.PMAYCATEGORY <> 'NA' ");
		sql.append(" and t3.ERRORCODE is null and APPLICANTID is null ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		RowMapper<PmayEligibilityLog> typeRowMapper = BeanPropertyRowMapper.newInstance(PmayEligibilityLog.class);
		MapSqlParameterSource source = new MapSqlParameterSource();

		List<PmayEligibilityLog> pmayEligibilityLoglist = new ArrayList<>();
		try {
			pmayEligibilityLoglist = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			pmayEligibilityLoglist = new ArrayList<>();
		}
		logger.debug(Literal.LEAVING);
		return pmayEligibilityLoglist;
	}

	@Override
	public void update(PmayEligibilityLog pmayEligibilityLog) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update PmayEligibilityLog");
		sql.append("  set ");
		//, ErrorCode = :ErrorCode, ErrorDesc = :ErrorDesc,Remarks = :Remarks
		sql.append("  PmayStatus = :PmayStatus, ");
		sql.append("  ApplicantId = :ApplicantId ");
		sql.append("  Where RecordId = :RecordId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pmayEligibilityLog);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			logger.debug(Literal.LEAVING);
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public String getCustCif(String finreference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select  t3.custcif from PMAYELIGIBILITYLOG t1 ");
		sql.append(" inner join  ");
		sql.append("(select finreference,custid  from FINANCEMAIN_temp ");
		sql.append(" union select finreference,custid  from  FINANCEMAIN  ");
		sql.append(" WHERE NOT EXISTS (SELECT 1 FROM FINANCEMAIN_temp WHERE finReference = FINANCEMAIN.finReference )");
		sql.append(" t2 on t1.finreference= t2.finreference ");
		sql.append(
				" inner join CUSTOMERS t3 on t3.custid = t2.custid where  t1.finreference= :finreference  group by  t1.finreference,t3.custcif ");

		logger.trace(Literal.SQL + sql.toString());

		Map<String, String> mapVal = new HashMap<String, String>();
		mapVal.put("finreference", finreference);
		logger.debug(Literal.LEAVING);
		return jdbcTemplate.queryForObject(sql.toString(), mapVal, String.class);
	}

	@Override
	public void update(String reference, String applicantId) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update customer_retail_ed");
		sql.append("  set ");
		sql.append("  ApplicantId = :ApplicantId ");
		sql.append("  Where Reference = :Reference ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		Map<String, String> mapVal = new HashMap<String, String>();
		mapVal.put("Reference", reference);
		mapVal.put("ApplicantId", applicantId);

		int recordCount = jdbcTemplate.update(sql.toString(), mapVal);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			logger.debug(Literal.LEAVING);
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
}
