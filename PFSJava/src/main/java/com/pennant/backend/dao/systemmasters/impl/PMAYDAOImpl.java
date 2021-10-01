package com.pennant.backend.dao.systemmasters.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

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
	public PMAY getPMAY(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, NotifiedTown, CentralAssistance, OwnedHouse, TownCode");
		sql.append(", CarpetArea, HouseholdAnnIncome, BalanceTransfer, PrimaryApplicant, PrprtyOwnedByWomen");
		sql.append(", TransactionFinType, Product, WaterSupply, Drinage, Electricity, PmayCategory");

		if (type.contains("View")) {
			sql.append(", CustCif, CustShrtName, TownName");
		}

		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PMAY");
		sql.append(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				PMAY pmay = new PMAY();

				pmay.setFinID(rs.getLong("FinID"));
				pmay.setFinReference(rs.getString("FinReference"));
				pmay.setNotifiedTown(rs.getBoolean("NotifiedTown"));
				pmay.setCentralAssistance(rs.getBoolean("CentralAssistance"));
				pmay.setOwnedHouse(rs.getBoolean("OwnedHouse"));
				pmay.setTownCode(rs.getLong("TownCode"));
				pmay.setCarpetArea(rs.getBigDecimal("CarpetArea"));
				pmay.setHouseholdAnnIncome(rs.getBigDecimal("HouseholdAnnIncome"));
				pmay.setBalanceTransfer(rs.getBoolean("BalanceTransfer"));
				pmay.setPrimaryApplicant(rs.getBoolean("PrimaryApplicant"));
				pmay.setPrprtyOwnedByWomen(rs.getBoolean("PrprtyOwnedByWomen"));
				pmay.setTransactionFinType(rs.getString("TransactionFinType"));
				pmay.setProduct(rs.getString("Product"));
				pmay.setWaterSupply(rs.getBoolean("WaterSupply"));
				pmay.setDrinage(rs.getBoolean("Drinage"));
				pmay.setElectricity(rs.getBoolean("Electricity"));
				pmay.setPmayCategory(rs.getString("PmayCategory"));

				if (type.contains("View")) {
					pmay.setCustCif(rs.getString("CustCif"));
					pmay.setCustShrtName(rs.getString("CustShrtName"));
					pmay.setTownName(rs.getString("TownName"));
				}

				pmay.setVersion(rs.getInt("Version"));
				pmay.setLastMntBy(rs.getLong("LastMntBy"));
				pmay.setLastMntOn(rs.getTimestamp("LastMntOn"));
				pmay.setRecordStatus(rs.getString("RecordStatus"));
				pmay.setRoleCode(rs.getString("RoleCode"));
				pmay.setNextRoleCode(rs.getString("NextRoleCode"));
				pmay.setTaskId(rs.getString("TaskId"));
				pmay.setNextTaskId(rs.getString("NextTaskId"));
				pmay.setRecordType(rs.getString("RecordType"));
				pmay.setWorkflowId(rs.getLong("WorkflowId"));

				return pmay;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public boolean isDuplicateKey(long finID, TableType tableType) {
		String sql;
		String whereClause = "FinID = ?";
		Object[] obj = new Object[] { finID };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("PMAY", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("PMAY_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "PMAY_Temp", "PMAY" }, whereClause);
			obj = new Object[] { finID, finID };
			break;
		}

		logger.debug(Literal.SQL + sql);

		Integer count = jdbcOperations.queryForObject(sql, Integer.class, obj);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		return exists;
	}

	@Override
	public String save(PMAY pmay, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into PMAY");
		sql.append(tableType.getSuffix());
		sql.append("(FinID, FinReference, NotifiedTown, TownCode, CentralAssistance, OwnedHouse, CarpetArea");
		sql.append(", HouseholdAnnIncome, BalanceTransfer, PrimaryApplicant, PrprtyOwnedByWomen");
		sql.append(", TransactionFinType, Product, WaterSupply, Drinage, Electricity, PmayCategory");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append("?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, pmay.getFinID());
				ps.setString(index++, pmay.getFinReference());
				ps.setBoolean(index++, pmay.isNotifiedTown());
				ps.setLong(index++, pmay.getTownCode());
				ps.setBoolean(index++, pmay.isCentralAssistance());
				ps.setBoolean(index++, pmay.isOwnedHouse());
				ps.setBigDecimal(index++, pmay.getCarpetArea());
				ps.setBigDecimal(index++, pmay.getHouseholdAnnIncome());
				ps.setBoolean(index++, pmay.isBalanceTransfer());
				ps.setBoolean(index++, pmay.isPrimaryApplicant());
				ps.setBoolean(index++, pmay.isPrprtyOwnedByWomen());
				ps.setString(index++, pmay.getTransactionFinType());
				ps.setString(index++, pmay.getProduct());
				ps.setBoolean(index++, pmay.isWaterSupply());
				ps.setBoolean(index++, pmay.isDrinage());
				ps.setBoolean(index++, pmay.isElectricity());
				ps.setString(index++, pmay.getPmayCategory());
				ps.setInt(index++, pmay.getVersion());
				ps.setLong(index++, pmay.getLastMntBy());
				ps.setTimestamp(index++, pmay.getLastMntOn());
				ps.setString(index++, pmay.getRecordStatus());
				ps.setString(index++, pmay.getRoleCode());
				ps.setString(index++, pmay.getNextRoleCode());
				ps.setString(index++, pmay.getTaskId());
				ps.setString(index++, pmay.getNextTaskId());
				ps.setString(index++, pmay.getRecordType());
				ps.setLong(index++, pmay.getWorkflowId());

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return pmay.getFinReference();
	}

	@Override
	public void update(PMAY pmay, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update PMAY");
		sql.append(tableType.getSuffix());
		sql.append(" Set NotifiedTown = ?, TownCode = ?, CentralAssistance = ?, OwnedHouse = ?, CarpetArea = ?");
		sql.append(", HouseholdAnnIncome = ?, BalanceTransfer = ?, PrimaryApplicant = ?");
		sql.append(", PrprtyOwnedByWomen = ?, TransactionFinType = ?, Product = ?, WaterSupply = ?");
		sql.append(", Drinage = ?, Electricity = ?, PmayCategory = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBoolean(index++, pmay.isNotifiedTown());
			ps.setLong(index++, pmay.getTownCode());
			ps.setBoolean(index++, pmay.isCentralAssistance());
			ps.setBoolean(index++, pmay.isOwnedHouse());
			ps.setBigDecimal(index++, pmay.getCarpetArea());
			ps.setBigDecimal(index++, pmay.getHouseholdAnnIncome());
			ps.setBoolean(index++, pmay.isBalanceTransfer());
			ps.setBoolean(index++, pmay.isPrimaryApplicant());
			ps.setBoolean(index++, pmay.isPrprtyOwnedByWomen());
			ps.setString(index++, pmay.getTransactionFinType());
			ps.setString(index++, pmay.getProduct());
			ps.setBoolean(index++, pmay.isWaterSupply());
			ps.setBoolean(index++, pmay.isDrinage());
			ps.setBoolean(index++, pmay.isElectricity());
			ps.setString(index++, pmay.getPmayCategory());
			ps.setInt(index++, pmay.getVersion());
			ps.setLong(index++, pmay.getLastMntBy());
			ps.setTimestamp(index++, pmay.getLastMntOn());
			ps.setString(index++, pmay.getRecordStatus());
			ps.setString(index++, pmay.getRoleCode());
			ps.setString(index++, pmay.getNextRoleCode());
			ps.setString(index++, pmay.getTaskId());
			ps.setString(index++, pmay.getNextTaskId());
			ps.setString(index++, pmay.getRecordType());
			ps.setLong(index++, pmay.getWorkflowId());

			ps.setLong(index++, pmay.getFinID());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index++, pmay.getPrevMntOn());
			} else {
				ps.setInt(index++, pmay.getVersion() - 1);
			}

		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PMAY pmay, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From PMAY");
		sql.append(tableType.getSuffix());
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = jdbcOperations.update(sql.toString(), pmay.getFinID());

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public boolean isFinReferenceExists(long finID) {
		String sql = "Select count(FinID) From PMAY Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID) > 0;
	}

	@Override
	public String save(PmayEligibilityLog pMaylog, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into PmayEligibilityLog");
		sql.append(tableType.getSuffix());
		sql.append("(FinID, FinReference, RecordId, PmayStatus");
		sql.append(", ErrorCode, ErrorDesc, ApplicantId, Remarks, ReqJson, RespJson)");
		sql.append(" Values (");
		sql.append("?, ?, ?, ? , ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				ps.setLong(index++, pMaylog.getFinID());
				ps.setString(index++, pMaylog.getFinReference());
				ps.setLong(index++, pMaylog.getRecordId());
				ps.setString(index++, pMaylog.getPmayStatus());
				ps.setString(index++, pMaylog.getErrorCode());
				ps.setString(index++, pMaylog.getErrorDesc());
				ps.setString(index++, pMaylog.getApplicantId());
				ps.setString(index++, pMaylog.getRemarks());
				ps.setString(index++, pMaylog.getReqJson());
				ps.setString(index++, pMaylog.getRespJson());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return pMaylog.getFinReference();
	}

	@Override
	public void update(PmayEligibilityLog pMaylog, TableType tableType) {
		StringBuilder sql = new StringBuilder("update PmayEligibilityLog");
		sql.append(tableType.getSuffix());
		sql.append(" Set RecordId = ?, PmayStatus = ?, ErrorCode = ?, ErrorDesc = ?");
		sql.append(", ApplicantId = ?, Remarks = ?, ReqJson = ?, RespJson = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, pMaylog.getRecordId());
			ps.setString(index++, pMaylog.getPmayStatus());
			ps.setString(index++, pMaylog.getErrorCode());
			ps.setString(index++, pMaylog.getErrorDesc());
			ps.setString(index++, pMaylog.getApplicantId());
			ps.setString(index++, pMaylog.getRemarks());
			ps.setString(index++, pMaylog.getReqJson());
			ps.setString(index++, pMaylog.getRespJson());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public PmayEligibilityLog getEligibilityLog(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" RecordId, PmayStatus, ErrorCode, ErrorDesc, ApplicantId, Remarks, ReqJson, RespJson");
		sql.append(" From PmayEligibilityLog");
		sql.append(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				PmayEligibilityLog pel = new PmayEligibilityLog();

				pel.setRecordId(rs.getLong("RecordId"));
				pel.setPmayStatus(rs.getString("PmayStatus"));
				pel.setErrorCode(rs.getString("ErrorCode"));
				pel.setErrorDesc(rs.getString("ErrorDesc"));
				pel.setApplicantId(rs.getString("ApplicantId"));
				pel.setRemarks(rs.getString("Remarks"));
				pel.setReqJson(rs.getString("ReqJson"));
				pel.setRespJson(rs.getString("RespJson"));

				return pel;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public long generateDocSeq() {
		return getNextValue("SeqPmayEligibilityLog");
	}

	@Override
	public List<PmayEligibilityLog> getEligibilityLogList(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, RecordId, PmayStatus");
		sql.append(", ErrorCode, ErrorDesc, ApplicantId, Remarks, ReqJson, RespJson");
		sql.append(" From PmayEligibilityLog");
		sql.append(type);
		sql.append(" Where FinID = ? order by RecordId asc");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			PmayEligibilityLog pel = new PmayEligibilityLog();

			pel.setFinID(rs.getLong("FinID"));
			pel.setFinReference(rs.getString("FinReference"));
			pel.setRecordId(rs.getLong("RecordId"));
			pel.setPmayStatus(rs.getString("PmayStatus"));
			pel.setErrorCode(rs.getString("ErrorCode"));
			pel.setErrorDesc(rs.getString("ErrorDesc"));
			pel.setApplicantId(rs.getString("ApplicantId"));
			pel.setRemarks(rs.getString("Remarks"));
			pel.setReqJson(rs.getString("ReqJson"));
			pel.setRespJson(rs.getString("RespJson"));

			return pel;

		}, finID);

	}

	@Override
	public List<PmayEligibilityLog> getAllRecordIdForPmay() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" pel.FinID, pel.FinReference, pel.RecordId, pel.PmayStatus, pel.ErrorCode, pel.ErrorDesc");
		sql.append(" pel.ApplicantId, pel.Remarks, pel.ReqJson, pel.RespJson");
		sql.append(" From Financemain_View fm ");
		sql.append("Inner Join (Select FinID From PMAY_Temp Union all Select FinID From PMAY) p on p.FinID = fm.FinID");
		sql.append(" Inner Join PmayEligibilityLog pel on p.FinID = pel.FinID");
		sql.append(" Where fm.RecordStatus <> ? and  fm.ClosingStatus is null and");
		sql.append(" fm.FinIsActive = ? and p.PmayCategory <> ? and pel.ErrorCode is null and ApplicantId is null");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, "Rejected");
			ps.setInt(index++, 1);
			ps.setString(index++, "NA");

		}, (rs, rowNum) -> {
			PmayEligibilityLog pel = new PmayEligibilityLog();

			return pel;
		});
	}

	@Override
	public void update(PmayEligibilityLog pel) {
		String sql = "Update PmayEligibilityLog Set PmayStatus = ?, ApplicantId = ? Where RecordId = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, pel.getPmayStatus());
			ps.setString(index++, pel.getApplicantId());

			ps.setLong(index++, pel.getRecordId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public String getCustCif(long finID) {
		StringBuilder sql = new StringBuilder("Select CustCif");
		sql.append(" From PmayEligibilityLog pel Inner Join (");
		sql.append(" Select FinID, CustId From Financemain_Temp Union all Select FinID, CustId From Financemain");
		sql.append(" Where not exists (Select 1 From Financemain_Temp Where FinID = Financemain.FinID");
		sql.append(")) fm on fm.finID= pel.finID");
		sql.append(" Inner Join Customers c on c.CustId = fm.CustId");
		sql.append(" Where pel.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), String.class, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public void update(String reference, String applicantId) {
		String sql = "Update Customer_Retail_Ed Set ApplicantId = ? Where Reference = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = jdbcOperations.update(sql, applicantId, reference);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}
}
