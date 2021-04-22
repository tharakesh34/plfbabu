package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.RestructureDAO;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class RestructureDAOImpl extends SequenceDao<RestructureDetail> implements RestructureDAO {

	private static Logger logger = LogManager.getLogger(RestructureDAOImpl.class);

	@Override
	public long save(RestructureDetail rd, String type) {
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" Restructure_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Id, FinReference, RestructureType, RestructureDate, EmiHldPeriod, PriHldPeriod");
		sql.append(", EmiPeriods, TenorChange, EmiRecal, TotNoOfRestructure, RecalculationType, ServiceRequestNo");
		sql.append(", Remark, OldBucket, NewBucket, OldEmiOs, NewEmiOs, OldBalTenure, NewBalTenure, OldMaturity");
		sql.append(", NewMaturity, LastBilledDate, LastBilledInstNo, ActLoanAmount, OldTenure, NewTenure");
		sql.append(", OldInterest, NewInterest, OldMaxUnplannedEmi, NewMaxUnplannedEmi, OldAvailedUnplanEmi");
		sql.append(", NewAvailedUnplanEmi, OldFinalEmi, NewFinalEmi, RestructureReason, OldDpd, NewDpd");
		sql.append(", OldCpzInterest, NewCpzInterest, EmiHldStartDate, EmiHldEndDate, PriHldStartDate");
		sql.append(", PriHldEndDate, AppDate, OldPOsAmount, NewPOsAmount, OldEmiOverdue, NewEmiOverdue");
		sql.append(", BounceCharge, OldPenaltyAmount, NewPenaltyAmount, OtherCharge, RestructureCharge");
		sql.append(", FinCurrAssetValue, OldExtOdDays, NewExtOdDays, RepayProfitRate, GrcMaxAmount, Version");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(");");

		logger.trace(Literal.SQL + sql.toString());

		if (rd.getId() <= 0) {
			rd.setId(getNextValue("Seq_Restructure_Details"));
		}

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, JdbcUtil.setLong(rd.getId()));
				ps.setString(index++, rd.getFinReference());
				ps.setString(index++, rd.getRestructureType());
				ps.setDate(index++, JdbcUtil.getDate(rd.getRestructureDate()));
				ps.setInt(index++, rd.getEmiHldPeriod());
				ps.setInt(index++, rd.getPriHldPeriod());
				ps.setInt(index++, rd.getEmiPeriods());
				ps.setBoolean(index++, rd.isTenorChange());
				ps.setBoolean(index++, rd.isEmiRecal());
				ps.setInt(index++, rd.getTotNoOfRestructure());
				ps.setString(index++, rd.getRecalculationType());
				ps.setString(index++, rd.getServiceRequestNo());
				ps.setString(index++, rd.getRemark());
				ps.setInt(index++, rd.getOldBucket());
				ps.setInt(index++, rd.getNewBucket());
				ps.setBigDecimal(index++, rd.getOldEmiOs());
				ps.setBigDecimal(index++, rd.getNewEmiOs());
				ps.setInt(index++, rd.getOldBalTenure());
				ps.setInt(index++, rd.getNewBalTenure());
				ps.setDate(index++, JdbcUtil.getDate(rd.getOldMaturity()));
				ps.setDate(index++, JdbcUtil.getDate(rd.getNewMaturity()));
				ps.setDate(index++, JdbcUtil.getDate(rd.getLastBilledDate()));
				ps.setInt(index++, rd.getLastBilledInstNo());
				ps.setBigDecimal(index++, rd.getActLoanAmount());
				ps.setInt(index++, rd.getOldTenure());
				ps.setInt(index++, rd.getNewTenure());
				ps.setBigDecimal(index++, rd.getOldInterest());
				ps.setBigDecimal(index++, rd.getNewInterest());
				ps.setInt(index++, rd.getOldMaxUnplannedEmi());
				ps.setInt(index++, rd.getNewMaxUnplannedEmi());
				ps.setInt(index++, rd.getOldAvailedUnplanEmi());
				ps.setInt(index++, rd.getNewAvailedUnplanEmi());
				ps.setBigDecimal(index++, rd.getOldFinalEmi());
				ps.setBigDecimal(index++, rd.getNewFinalEmi());
				ps.setString(index++, rd.getRestructureReason());
				ps.setInt(index++, rd.getOldDpd());
				ps.setInt(index++, rd.getNewDpd());
				ps.setBigDecimal(index++, rd.getOldCpzInterest());
				ps.setBigDecimal(index++, rd.getNewCpzInterest());
				ps.setDate(index++, JdbcUtil.getDate(rd.getEmiHldStartDate()));
				ps.setDate(index++, JdbcUtil.getDate(rd.getEmiHldEndDate()));
				ps.setDate(index++, JdbcUtil.getDate(rd.getPriHldStartDate()));
				ps.setDate(index++, JdbcUtil.getDate(rd.getPriHldEndDate()));
				ps.setDate(index++, JdbcUtil.getDate(rd.getAppDate()));
				ps.setBigDecimal(index++, rd.getOldPOsAmount());
				ps.setBigDecimal(index++, rd.getNewPOsAmount());
				ps.setBigDecimal(index++, rd.getOldEmiOverdue());
				ps.setBigDecimal(index++, rd.getNewEmiOverdue());
				ps.setBigDecimal(index++, rd.getBounceCharge());
				ps.setBigDecimal(index++, rd.getOldPenaltyAmount());
				ps.setBigDecimal(index++, rd.getNewPenaltyAmount());
				ps.setBigDecimal(index++, rd.getOtherCharge());
				ps.setBigDecimal(index++, rd.getRestructureCharge());
				ps.setBigDecimal(index++, rd.getFinCurrAssetValue());
				ps.setInt(index++, rd.getOldExtOdDays());
				ps.setInt(index++, rd.getNewExtOdDays());
				ps.setBigDecimal(index++, rd.getRepayProfitRate());
				ps.setBigDecimal(index++, rd.getGrcMaxAmount());
				ps.setInt(index++, rd.getVersion());
				ps.setLong(index++, JdbcUtil.setLong(rd.getLastMntBy()));
				ps.setTimestamp(index++, rd.getLastMntOn());
				ps.setString(index++, rd.getRecordStatus());
				ps.setString(index++, rd.getRoleCode());
				ps.setString(index++, rd.getNextRoleCode());
				ps.setString(index++, rd.getTaskId());
				ps.setString(index++, rd.getNextTaskId());
				ps.setString(index++, rd.getRecordType());
				ps.setLong(index++, JdbcUtil.setLong(rd.getWorkflowId()));
			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return rd.getId();
	}

	@Override
	public RestructureDetail getRestructureDetailById(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinReference, RestructureType, RestructureDate, EmiHldPeriod, PriHldPeriod");
		sql.append(", EmiPeriods, TenorChange, EmiRecal, TotNoOfRestructure, RecalculationType, ServiceRequestNo");
		sql.append(", Remark, OldBucket, NewBucket, OldEmiOs, NewEmiOs, OldBalTenure, NewBalTenure");
		sql.append(", OldMaturity, NewMaturity, LastBilledDate, LastBilledInstNo, ActLoanAmount, OldTenure");
		sql.append(", NewTenure, OldInterest, NewInterest, OldMaxUnplannedEmi, NewMaxUnplannedEmi");
		sql.append(", OldAvailedUnplanEmi, NewAvailedUnplanEmi, OldFinalEmi, NewFinalEmi, RestructureReason");
		sql.append(", OldDpd, NewDpd, OldCpzInterest, NewCpzInterest, EmiHldStartDate, EmiHldEndDate");
		sql.append(", PriHldStartDate, PriHldEndDate, AppDate, OldPOsAmount, NewPOsAmount");
		sql.append(", OldEmiOverdue, NewEmiOverdue, BounceCharge, OldPenaltyAmount, NewPenaltyAmount");
		sql.append(", OtherCharge, RestructureCharge, FinCurrAssetValue, OldExtOdDays, NewExtOdDays");
		sql.append(", RepayProfitRate, GrcMaxAmount, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", RstTypeCode, RstTypeDesc");
		sql.append(" FROM Restructure_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id },
					new RowMapper<RestructureDetail>() {
						@Override
						public RestructureDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
							RestructureDetail rd = new RestructureDetail();
							rd.setId(rs.getLong("Id"));
							rd.setFinReference(rs.getString("FinReference"));
							rd.setRestructureType(rs.getString("RestructureType"));
							rd.setRestructureDate(rs.getTimestamp("RestructureDate"));
							rd.setEmiHldPeriod(rs.getInt("EmiHldPeriod"));
							rd.setPriHldPeriod(rs.getInt("PriHldPeriod"));
							rd.setEmiPeriods(rs.getInt("EmiPeriods"));
							rd.setTenorChange(rs.getBoolean("TenorChange"));
							rd.setEmiRecal(rs.getBoolean("EmiRecal"));
							rd.setTotNoOfRestructure(rs.getInt("TotNoOfRestructure"));
							rd.setRecalculationType(rs.getString("RecalculationType"));
							rd.setServiceRequestNo(rs.getString("ServiceRequestNo"));
							rd.setRemark(rs.getString("Remark"));
							rd.setOldBucket(rs.getInt("OldBucket"));
							rd.setNewBucket(rs.getInt("NewBucket"));
							rd.setOldEmiOs(rs.getBigDecimal("OldEmiOs"));
							rd.setNewEmiOs(rs.getBigDecimal("NewEmiOs"));
							rd.setOldBalTenure(rs.getInt("OldBalTenure"));
							rd.setNewBalTenure(rs.getInt("NewBalTenure"));
							rd.setOldMaturity(rs.getTimestamp("OldMaturity"));
							rd.setNewMaturity(rs.getTimestamp("NewMaturity"));
							rd.setLastBilledDate(rs.getTimestamp("LastBilledDate"));
							rd.setLastBilledInstNo(rs.getInt("LastBilledInstNo"));
							rd.setActLoanAmount(rs.getBigDecimal("ActLoanAmount"));
							rd.setOldTenure(rs.getInt("OldTenure"));
							rd.setNewTenure(rs.getInt("NewTenure"));
							rd.setOldInterest(rs.getBigDecimal("OldInterest"));
							rd.setNewInterest(rs.getBigDecimal("NewInterest"));
							rd.setOldMaxUnplannedEmi(rs.getInt("OldMaxUnplannedEmi"));
							rd.setNewMaxUnplannedEmi(rs.getInt("NewMaxUnplannedEmi"));
							rd.setOldAvailedUnplanEmi(rs.getInt("OldAvailedUnplanEmi"));
							rd.setNewAvailedUnplanEmi(rs.getInt("NewAvailedUnplanEmi"));
							rd.setOldFinalEmi(rs.getBigDecimal("OldFinalEmi"));
							rd.setNewFinalEmi(rs.getBigDecimal("NewFinalEmi"));
							rd.setRestructureReason(rs.getString("RestructureReason"));
							rd.setOldDpd(rs.getInt("OldDpd"));
							rd.setNewDpd(rs.getInt("NewDpd"));
							rd.setOldCpzInterest(rs.getBigDecimal("OldCpzInterest"));
							rd.setNewCpzInterest(rs.getBigDecimal("NewCpzInterest"));
							rd.setEmiHldStartDate(rs.getTimestamp("EmiHldStartDate"));
							rd.setEmiHldEndDate(rs.getTimestamp("EmiHldEndDate"));
							rd.setPriHldStartDate(rs.getTimestamp("PriHldStartDate"));
							rd.setPriHldEndDate(rs.getTimestamp("PriHldEndDate"));
							rd.setAppDate(rs.getTimestamp("AppDate"));
							rd.setOldPOsAmount(rs.getBigDecimal("OldPOsAmount"));
							rd.setNewPOsAmount(rs.getBigDecimal("NewPOsAmount"));
							rd.setOldEmiOverdue(rs.getBigDecimal("OldEmiOverdue"));
							rd.setNewEmiOverdue(rs.getBigDecimal("NewEmiOverdue"));
							rd.setBounceCharge(rs.getBigDecimal("BounceCharge"));
							rd.setOldPenaltyAmount(rs.getBigDecimal("OldPenaltyAmount"));
							rd.setNewPenaltyAmount(rs.getBigDecimal("NewPenaltyAmount"));
							rd.setOtherCharge(rs.getBigDecimal("OtherCharge"));
							rd.setRestructureCharge(rs.getBigDecimal("RestructureCharge"));
							rd.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
							rd.setOldExtOdDays(rs.getInt("OldExtOdDays"));
							rd.setNewExtOdDays(rs.getInt("NewExtOdDays"));
							rd.setRepayProfitRate(rs.getBigDecimal("RepayProfitRate"));
							rd.setGrcMaxAmount(rs.getBigDecimal("GrcMaxAmount"));
							rd.setVersion(rs.getInt("Version"));
							rd.setLastMntBy(rs.getLong("LastMntBy"));
							rd.setLastMntOn(rs.getTimestamp("LastMntOn"));
							rd.setRecordStatus(rs.getString("RecordStatus"));
							rd.setRoleCode(rs.getString("RoleCode"));
							rd.setNextRoleCode(rs.getString("NextRoleCode"));
							rd.setTaskId(rs.getString("TaskId"));
							rd.setNextTaskId(rs.getString("NextTaskId"));
							rd.setRecordType(rs.getString("RecordType"));
							rd.setWorkflowId(rs.getLong("WorkflowId"));
							rd.setRstTypeCode(rs.getString("RstTypeCode"));
							rd.setRstTypeDesc(rs.getString("RstTypeDesc"));
							return rd;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record is not found in Restructure_Details for the specified Id >> {}", id);
		}
		return null;
	}

	@Override
	public void update(RestructureDetail rd, String tableType) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" Restructure_Details");
		sql.append(tableType);
		sql.append(" set finReference = ?, restructureType = ?, restructureDate = ?, emiHldPeriod = ?");
		sql.append(", priHldPeriod = ?, emiPeriods = ?, tenorChange = ?, emiRecal = ?, totNoOfRestructure = ?");
		sql.append(", recalculationType = ?, serviceRequestNo = ?, remark = ?, oldBucket = ?, newBucket = ?");
		sql.append(", oldEmiOs = ?, newEmiOs = ?, oldBalTenure = ?, newBalTenure = ?, oldMaturity = ?");
		sql.append(", newMaturity = ?, lastBilledDate = ?, lastBilledInstNo = ?, actLoanAmount = ?");
		sql.append(", oldTenure = ?, newTenure = ?, oldInterest = ?, newInterest = ?, oldMaxUnplannedEmi = ?");
		sql.append(", newMaxUnplannedEmi = ?, oldAvailedUnplanEmi = ?, newAvailedUnplanEmi = ?, oldFinalEmi = ?");
		sql.append(", newFinalEmi = ?, oldDpd = ?, newDpd = ?, oldCpzInterest = ?, newCpzInterest = ?");
		sql.append(", emiHldStartDate = ?, emiHldEndDate = ?, priHldStartDate = ?, priHldEndDate = ?");
		sql.append(", AppDate = ?, oldPOsAmount = ?, newPOsAmount = ?, oldEmiOverdue = ?");
		sql.append(", newEmiOverdue = ?, bounceCharge = ?, oldPenaltyAmount = ?, newPenaltyAmount = ?");
		sql.append(", otherCharge = ?, restructureCharge = ?, repayProfitRate = ?, finCurrAssetValue = ?");
		sql.append(", oldExtOdDays = ?, newExtOdDays = ?, grcMaxAmount = ?, Version = ?, LastMntBy = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?,  restructureReason = ?");
		sql.append(" where Id = ?");

		logger.trace(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, rd.getFinReference());
			ps.setString(index++, rd.getRestructureType());
			ps.setDate(index++, JdbcUtil.getDate(rd.getRestructureDate()));
			ps.setInt(index++, rd.getEmiHldPeriod());
			ps.setInt(index++, rd.getPriHldPeriod());
			ps.setInt(index++, rd.getEmiPeriods());
			ps.setBoolean(index++, rd.isTenorChange());
			ps.setBoolean(index++, rd.isEmiRecal());
			ps.setInt(index++, rd.getTotNoOfRestructure());
			ps.setString(index++, rd.getRecalculationType());
			ps.setString(index++, rd.getServiceRequestNo());
			ps.setString(index++, rd.getRemark());
			ps.setInt(index++, rd.getOldBucket());
			ps.setInt(index++, rd.getNewBucket());
			ps.setBigDecimal(index++, rd.getOldEmiOs());
			ps.setBigDecimal(index++, rd.getNewEmiOs());
			ps.setInt(index++, rd.getOldBalTenure());
			ps.setInt(index++, rd.getNewBalTenure());
			ps.setDate(index++, JdbcUtil.getDate(rd.getOldMaturity()));
			ps.setDate(index++, JdbcUtil.getDate(rd.getNewMaturity()));
			ps.setDate(index++, JdbcUtil.getDate(rd.getLastBilledDate()));
			ps.setInt(index++, rd.getLastBilledInstNo());
			ps.setBigDecimal(index++, rd.getActLoanAmount());
			ps.setInt(index++, rd.getOldTenure());
			ps.setInt(index++, rd.getNewTenure());
			ps.setBigDecimal(index++, rd.getOldInterest());
			ps.setBigDecimal(index++, rd.getNewInterest());
			ps.setInt(index++, rd.getOldMaxUnplannedEmi());
			ps.setInt(index++, rd.getNewMaxUnplannedEmi());
			ps.setInt(index++, rd.getOldAvailedUnplanEmi());
			ps.setInt(index++, rd.getNewAvailedUnplanEmi());
			ps.setBigDecimal(index++, rd.getOldFinalEmi());
			ps.setBigDecimal(index++, rd.getNewFinalEmi());
			ps.setInt(index++, rd.getOldDpd());
			ps.setInt(index++, rd.getNewDpd());
			ps.setBigDecimal(index++, rd.getOldCpzInterest());
			ps.setBigDecimal(index++, rd.getNewCpzInterest());
			ps.setDate(index++, JdbcUtil.getDate(rd.getEmiHldStartDate()));
			ps.setDate(index++, JdbcUtil.getDate(rd.getEmiHldEndDate()));
			ps.setDate(index++, JdbcUtil.getDate(rd.getPriHldStartDate()));
			ps.setDate(index++, JdbcUtil.getDate(rd.getPriHldEndDate()));
			ps.setDate(index++, JdbcUtil.getDate(rd.getAppDate()));
			ps.setBigDecimal(index++, rd.getOldPOsAmount());
			ps.setBigDecimal(index++, rd.getNewPOsAmount());
			ps.setBigDecimal(index++, rd.getOldEmiOverdue());
			ps.setBigDecimal(index++, rd.getNewEmiOverdue());
			ps.setBigDecimal(index++, rd.getBounceCharge());
			ps.setBigDecimal(index++, rd.getOldPenaltyAmount());
			ps.setBigDecimal(index++, rd.getNewPenaltyAmount());
			ps.setBigDecimal(index++, rd.getOtherCharge());
			ps.setBigDecimal(index++, rd.getRestructureCharge());
			ps.setBigDecimal(index++, rd.getRepayProfitRate());
			ps.setBigDecimal(index++, rd.getFinCurrAssetValue());
			ps.setInt(index++, rd.getOldExtOdDays());
			ps.setInt(index++, rd.getNewExtOdDays());
			ps.setBigDecimal(index++, rd.getGrcMaxAmount());
			ps.setInt(index++, rd.getVersion());
			ps.setLong(index++, rd.getLastMntBy());
			ps.setTimestamp(index++, rd.getLastMntOn());
			ps.setString(index++, rd.getRecordStatus());
			ps.setString(index++, rd.getRoleCode());
			ps.setString(index++, rd.getNextRoleCode());
			ps.setString(index++, rd.getTaskId());
			ps.setString(index++, rd.getNextTaskId());
			ps.setString(index++, rd.getRecordType());
			ps.setLong(index++, rd.getWorkflowId());
			ps.setString(index++, rd.getRestructureReason());
			ps.setLong(index++, rd.getId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(long id, String tableType) {
		StringBuilder sql = new StringBuilder("Delete From");
		sql.append(" Restructure_Details");
		sql.append(tableType);
		sql.append(" where Id = ?");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), new Object[] { id });
	}

	@Override
	public RestructureDetail getRestructureDetailByFinReference(String finReference, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  Id, FinReference, RestructureType, RestructureDate, EmiHldPeriod, PriHldPeriod");
		sql.append(", EmiPeriods, TenorChange, EmiRecal, TotNoOfRestructure, RecalculationType, ServiceRequestNo");
		sql.append(", Remark, OldBucket, NewBucket, OldEmiOs, NewEmiOs, OldBalTenure, NewBalTenure");
		sql.append(", OldMaturity, NewMaturity, LastBilledDate, LastBilledInstNo, ActLoanAmount, OldTenure");
		sql.append(", NewTenure, OldInterest, NewInterest, OldMaxUnplannedEmi, NewMaxUnplannedEmi");
		sql.append(", OldAvailedUnplanEmi, NewAvailedUnplanEmi, OldFinalEmi, NewFinalEmi, RestructureReason");
		sql.append(", OldDpd, NewDpd, OldCpzInterest, NewCpzInterest, EmiHldStartDate, EmiHldEndDate");
		sql.append(", PriHldStartDate, PriHldEndDate, AppDate, OldPOsAmount, NewPOsAmount");
		sql.append(", OldEmiOverdue, NewEmiOverdue, BounceCharge, OldPenaltyAmount, NewPenaltyAmount");
		sql.append(", OtherCharge, RestructureCharge, FinCurrAssetValue, OldExtOdDays, NewExtOdDays");
		sql.append(", RepayProfitRate, GrcMaxAmount, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", RstTypeCode, RstTypeDesc");
		sql.append(" FROM  Restructure_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ");
		sql.append("(SELECT MAX(id) FROM Restructure_Details" + type + " Where FinReference = ?)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<RestructureDetail>() {
						@Override
						public RestructureDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
							RestructureDetail rd = new RestructureDetail();
							rd.setId(rs.getLong("Id"));
							rd.setFinReference(rs.getString("FinReference"));
							rd.setRestructureType(rs.getString("RestructureType"));
							rd.setRestructureDate(rs.getTimestamp("RestructureDate"));
							rd.setEmiHldPeriod(rs.getInt("EmiHldPeriod"));
							rd.setPriHldPeriod(rs.getInt("PriHldPeriod"));
							rd.setEmiPeriods(rs.getInt("EmiPeriods"));
							rd.setTenorChange(rs.getBoolean("TenorChange"));
							rd.setEmiRecal(rs.getBoolean("EmiRecal"));
							rd.setTotNoOfRestructure(rs.getInt("TotNoOfRestructure"));
							rd.setRecalculationType(rs.getString("RecalculationType"));
							rd.setServiceRequestNo(rs.getString("ServiceRequestNo"));
							rd.setRemark(rs.getString("Remark"));
							rd.setOldBucket(rs.getInt("OldBucket"));
							rd.setNewBucket(rs.getInt("NewBucket"));
							rd.setOldEmiOs(rs.getBigDecimal("OldEmiOs"));
							rd.setNewEmiOs(rs.getBigDecimal("NewEmiOs"));
							rd.setOldBalTenure(rs.getInt("OldBalTenure"));
							rd.setNewBalTenure(rs.getInt("NewBalTenure"));
							rd.setOldMaturity(rs.getTimestamp("OldMaturity"));
							rd.setNewMaturity(rs.getTimestamp("NewMaturity"));
							rd.setLastBilledDate(rs.getTimestamp("LastBilledDate"));
							rd.setLastBilledInstNo(rs.getInt("LastBilledInstNo"));
							rd.setActLoanAmount(rs.getBigDecimal("ActLoanAmount"));
							rd.setOldTenure(rs.getInt("OldTenure"));
							rd.setNewTenure(rs.getInt("NewTenure"));
							rd.setOldInterest(rs.getBigDecimal("OldInterest"));
							rd.setNewInterest(rs.getBigDecimal("NewInterest"));
							rd.setOldMaxUnplannedEmi(rs.getInt("OldMaxUnplannedEmi"));
							rd.setNewMaxUnplannedEmi(rs.getInt("NewMaxUnplannedEmi"));
							rd.setOldAvailedUnplanEmi(rs.getInt("OldAvailedUnplanEmi"));
							rd.setNewAvailedUnplanEmi(rs.getInt("NewAvailedUnplanEmi"));
							rd.setOldFinalEmi(rs.getBigDecimal("OldFinalEmi"));
							rd.setNewFinalEmi(rs.getBigDecimal("NewFinalEmi"));
							rd.setRestructureReason(rs.getString("RestructureReason"));
							rd.setOldDpd(rs.getInt("OldDpd"));
							rd.setNewDpd(rs.getInt("NewDpd"));
							rd.setOldCpzInterest(rs.getBigDecimal("OldCpzInterest"));
							rd.setNewCpzInterest(rs.getBigDecimal("NewCpzInterest"));
							rd.setEmiHldStartDate(rs.getTimestamp("EmiHldStartDate"));
							rd.setEmiHldEndDate(rs.getTimestamp("EmiHldEndDate"));
							rd.setPriHldStartDate(rs.getTimestamp("PriHldStartDate"));
							rd.setPriHldEndDate(rs.getTimestamp("PriHldEndDate"));
							rd.setAppDate(rs.getTimestamp("AppDate"));
							rd.setOldPOsAmount(rs.getBigDecimal("OldPOsAmount"));
							rd.setNewPOsAmount(rs.getBigDecimal("NewPOsAmount"));
							rd.setOldEmiOverdue(rs.getBigDecimal("OldEmiOverdue"));
							rd.setNewEmiOverdue(rs.getBigDecimal("NewEmiOverdue"));
							rd.setBounceCharge(rs.getBigDecimal("BounceCharge"));
							rd.setOldPenaltyAmount(rs.getBigDecimal("OldPenaltyAmount"));
							rd.setNewPenaltyAmount(rs.getBigDecimal("NewPenaltyAmount"));
							rd.setOtherCharge(rs.getBigDecimal("OtherCharge"));
							rd.setRestructureCharge(rs.getBigDecimal("RestructureCharge"));
							rd.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
							rd.setOldExtOdDays(rs.getInt("OldExtOdDays"));
							rd.setNewExtOdDays(rs.getInt("NewExtOdDays"));
							rd.setRepayProfitRate(rs.getBigDecimal("RepayProfitRate"));
							rd.setGrcMaxAmount(rs.getBigDecimal("GrcMaxAmount"));
							rd.setVersion(rs.getInt("Version"));
							rd.setLastMntBy(rs.getLong("LastMntBy"));
							rd.setLastMntOn(rs.getTimestamp("LastMntOn"));
							rd.setRecordStatus(rs.getString("RecordStatus"));
							rd.setRoleCode(rs.getString("RoleCode"));
							rd.setNextRoleCode(rs.getString("NextRoleCode"));
							rd.setTaskId(rs.getString("TaskId"));
							rd.setNextTaskId(rs.getString("NextTaskId"));
							rd.setRecordType(rs.getString("RecordType"));
							rd.setWorkflowId(rs.getLong("WorkflowId"));
							rd.setRstTypeCode(rs.getString("RstTypeCode"));
							rd.setRstTypeDesc(rs.getString("RstTypeDesc"));

							return rd;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record is not found in Restructure_Details for the specified reference >> {}", finReference);
		}
		return null;
	}
}
