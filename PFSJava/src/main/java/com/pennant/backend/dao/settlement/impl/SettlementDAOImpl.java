package com.pennant.backend.dao.settlement.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.settlement.SettlementDAO;
import com.pennant.backend.model.settlement.FinSettlementHeader;
import com.pennant.backend.model.settlement.SettlementAllocationDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class SettlementDAOImpl extends SequenceDao<FinSettlementHeader> implements SettlementDAO {
	private static Logger logger = LogManager.getLogger(SettlementDAOImpl.class);

	public SettlementDAOImpl() {
		super();
	}

	@Override
	public long save(FinSettlementHeader settlement, String tableType) {
		logger.debug(Literal.ENTERING);

		if (settlement.getID() == Long.MIN_VALUE) {
			settlement.setID(getNextValue("seqSettlement"));
		}

		StringBuilder insertSql = new StringBuilder("Insert Into Fin_Settlement_Header");
		insertSql.append(StringUtils.trimToEmpty(tableType));
		insertSql.append(" (ID, FinID, FinReference, SettlementType,");
		insertSql.append(
				" SettlementStatus, StartDate, OtsDate, EndDate, SettlementReason, settlementReasonId, SettlementAmount,");
		insertSql.append(" SettlementEndAfterGrace, NoOfGraceDays, CancelReasonCode, CancelRemarks,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");

		insertSql.append(" Values(:ID, :FinID, :FinReference, :SettlementType,");
		insertSql.append(" :SettlementStatus, :StartDate, :OtsDate, :EndDate, :SettlementReason, :settlementReasonId,");
		insertSql.append(
				" :SettlementAmount, :SettlementEndAfterGrace, :NoOfGraceDays,:CancelReasonCode, :CancelRemarks,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(settlement);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
		return settlement.getID();
	}

	@Override
	public void update(FinSettlementHeader settlement, String tableType) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		StringBuilder updateSql = new StringBuilder("Update Fin_Settlement_Header");
		updateSql.append(StringUtils.trimToEmpty(tableType));
		updateSql.append(" Set FinID = :FinID, FinReference = :FinReference, SettlementType = :SettlementType,");
		updateSql.append(" SettlementStatus = :SettlementStatus, StartDate = :StartDate, OtsDate = :OtsDate,");
		updateSql.append(
				" EndDate = :EndDate, SettlementReason = :SettlementReason, settlementReasonId = :settlementReasonId,");
		updateSql.append(" SettlementAmount = :SettlementAmount, SettlementEndAfterGrace =:SettlementEndAfterGrace,");
		updateSql.append(
				" NoOfGraceDays = :NoOfGraceDays,CancelReasonCode = :CancelReasonCode, CancelRemarks = :CancelRemarks,");
		updateSql.append(" Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType,");
		updateSql.append(" WorkflowId = :WorkflowId");
		updateSql.append(" Where ID =:ID");

		if (!tableType.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(settlement);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public FinSettlementHeader getSettlementById(long id, String type) {
		logger.debug(Literal.ENTERING);

		FinSettlementHeader settlement = new FinSettlementHeader();
		settlement.setID(id);

		StringBuilder selectSql = new StringBuilder("Select ID, FinID, FinReference, SettlementType,");
		selectSql.append(
				" SettlementStatus, StartDate, OtsDate, EndDate, SettlementReason, settlementReasonId, SettlementAmount,");
		selectSql.append(" SettlementEndAfterGrace, NoOfGraceDays,CancelReasonCode, CancelRemarks,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(", SettlementCode, SETTLEMENTREASONDESC");
		}

		selectSql.append(" From Fin_Settlement_Header");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  ID =:ID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(settlement);
		RowMapper<FinSettlementHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(FinSettlementHeader.class);

		try {
			settlement = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			settlement = null;
		}
		logger.debug(Literal.LEAVING);
		return settlement;
	}

	@Override
	public void delete(FinSettlementHeader settlement, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From Fin_Settlement_Header");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ID =:ID");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(settlement);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	@Override
	public boolean isDuplicateKey(long settlementTypeId, String finRefernce, long headerId, TableType tableType) {
		// Prepare the SQL.
		String sql;
		String whereClause = "SettlementType = :settlementTypeId AND FINREFERENCE = :finRefernce and ID != :headerId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Fin_Settlement_Header", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Fin_Settlement_Header_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Fin_Settlement_Header_Temp", "Fin_Settlement_Header" },
					whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("settlementTypeId", settlementTypeId);
		paramSource.addValue("finRefernce", finRefernce);
		paramSource.addValue("headerId", headerId);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public FinSettlementHeader getSettlementByRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		FinSettlementHeader settlement = new FinSettlementHeader();
		settlement.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("Select ID, FinID, SettlementType,");
		selectSql.append(
				" SettlementStatus, StartDate, OtsDate, EndDate, SettlementReason, settlementReasonId, SettlementAmount,");
		selectSql.append(" SettlementEndAfterGrace, NoOfGraceDays,CancelReasonCode, CancelRemarks,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		selectSql.append(" From Fin_Settlement_Header");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  finReference =:finReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(settlement);
		RowMapper<FinSettlementHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(FinSettlementHeader.class);

		try {
			settlement = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			// settlement = null;
		}
		logger.debug(Literal.LEAVING);
		return settlement;
	}

	@Override
	public void saveSettlementAllcDetails(SettlementAllocationDetail settlementAllocationDetail, String tableType) {
		logger.debug(Literal.ENTERING);

		if (settlementAllocationDetail.getID() == 0 || settlementAllocationDetail.getID() == Long.MIN_VALUE) {
			settlementAllocationDetail.setID(getNextValue("SEQSETTLEMENTALLOCATIONDTLS"));
		}

		StringBuilder insertSql = new StringBuilder("Insert Into SETTLEMENT_ALLOC_DTLS");
		insertSql.append(StringUtils.trimToEmpty(tableType));
		insertSql.append(" (HeaderID, ID, AllocationType,");
		insertSql.append(" AllocationTo,PaidAmount,TotalDue,WaivedAmount,PaidGST,");
		insertSql.append(" WaiverAccepted,WaivedGST,TaxHeaderID,TdsDue,TdsPaid,TdsWaived)");

		insertSql.append(" Values(:HeaderID, :AllocationID, :AllocationType,");
		insertSql.append(" :AllocationTo,:PaidAmount,:TotalDue,:WaivedAmount,:PaidGST,");
		insertSql.append(" :WaiverAccepted,:WaivedGST,:TaxHeaderID,:TdsDue,:TdsPaid,:TdsWaived)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(settlementAllocationDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteSettlementAllcById(SettlementAllocationDetail settlementAllocationDetail, String tableType) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From SETTLEMENT_ALLOC_DTLS");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where ID =:AllocationID and HeaderID =:HeaderID");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(settlementAllocationDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void deleteSettlementAllcByHeaderId(long headerId, String tableType) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From SETTLEMENT_ALLOC_DTLS");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where HeaderID =?");

		logger.debug("deleteSql: " + deleteSql.toString());

		this.jdbcOperations.update(deleteSql.toString(), headerId);
	}

	@Override
	public List<SettlementAllocationDetail> getSettlementAllcDetailByHdrID(long headerId, String tableType) {
		logger.debug("Entering");
		SettlementAllocationDetail allocationDetail = new SettlementAllocationDetail();
		allocationDetail.setHeaderID(headerId);

		StringBuilder selectSql = new StringBuilder("SELECT HeaderID, ID,AllocationType,");
		selectSql.append(" AllocationTo,PaidAmount,TotalDue,WaivedAmount,PaidGST,Waivedreqamount,");
		selectSql.append(" WaiverAccepted,WaivedGST,TaxHeaderID,TdsDue,TdsPaid,TdsWaived");
		selectSql.append(" FROM  SETTLEMENT_ALLOC_DTLS");
		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" Where HeaderID =:HeaderID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(allocationDetail);
		RowMapper<SettlementAllocationDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(SettlementAllocationDetail.class);
		List<SettlementAllocationDetail> list = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return list;
	}

	@Override
	public FinSettlementHeader getSettlementByFinID(long finID, String type) {
		logger.debug(Literal.ENTERING);

		FinSettlementHeader settlement = new FinSettlementHeader();
		settlement.setFinID(finID);

		StringBuilder selectSql = new StringBuilder("Select HeaderID, FinID, SettlementType,");
		selectSql.append(
				" SettlementStatus, StartDate, OtsDate, EndDate, SettlementReason, settlementReasonId, SettlementAmount,");
		selectSql.append(" SettlementEndAfterGrace, NoOfGraceDays,CancelReasonCode, CancelRemarks,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		selectSql.append(" From Fin_Settlement_Header");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  FinID =:finID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(settlement);
		RowMapper<FinSettlementHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(FinSettlementHeader.class);

		try {
			settlement = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			// settlement = null;
		}
		logger.debug(Literal.LEAVING);
		return settlement;
	}

	@Override
	public void updateSettlementStatus(long HeaderID, String status) {
		String sql = "Update Fin_Settlement_Header Set SettlementStatus = ? Where HeaderID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, status);
			ps.setLong(index++, HeaderID);

		});
	}

	@Override
	public FinSettlementHeader getInitiateSettlementByFinID(long finID, String type) {
		logger.debug(Literal.ENTERING);

		FinSettlementHeader settlement = new FinSettlementHeader();
		settlement.setFinID(finID);

		StringBuilder selectSql = new StringBuilder("Select ID, FinID, SettlementType,");
		selectSql.append(" SettlementStatus, StartDate, OtsDate, EndDate, SettlementReason, SettlementAmount,");
		selectSql.append(" SettlementEndAfterGrace, NoOfGraceDays,CancelReasonCode, CancelRemarks,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		selectSql.append(" From Fin_Settlement_Header");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  FinID =:finID and SettlementStatus='I'");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(settlement);
		RowMapper<FinSettlementHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(FinSettlementHeader.class);

		try {
			settlement = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			// settlement = null;
		}
		logger.debug(Literal.LEAVING);
		return settlement;
	}

}
