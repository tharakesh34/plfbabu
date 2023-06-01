package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.finance.RestructureDAO;
import com.pennant.backend.model.finance.RestructureCharge;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.RestructureType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class RestructureDAOImpl extends SequenceDao<RestructureDetail> implements RestructureDAO {

	private static Logger logger = LogManager.getLogger(RestructureDAOImpl.class);

	@Override
	public long save(RestructureDetail rd, String type) {
		StringBuilder sql = new StringBuilder("Insert Into Restructure_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Id, FinID, FinReference, RestructureType, RestructureDate, EmiHldPeriod, PriHldPeriod");
		sql.append(", EmiPeriods, TenorChange, EmiRecal, TotNoOfRestructure, RecalculationType, ServiceRequestNo");
		sql.append(", Remark, OldBucket, NewBucket, OldEmiOs, NewEmiOs, OldBalTenure, NewBalTenure, OldMaturity");
		sql.append(", NewMaturity, LastBilledDate, LastBilledInstNo, ActLoanAmount, OldTenure, NewTenure");
		sql.append(", OldInterest, NewInterest, OldMaxUnplannedEmi, NewMaxUnplannedEmi, OldAvailedUnplanEmi");
		sql.append(", NewAvailedUnplanEmi, OldFinalEmi, NewFinalEmi, RestructureReason, OldDpd, NewDpd");
		sql.append(", OldCpzInterest, NewCpzInterest, EmiHldStartDate, EmiHldEndDate, PriHldStartDate");
		sql.append(", PriHldEndDate, AppDate, OldPOsAmount, NewPOsAmount, OldEmiOverdue, NewEmiOverdue");
		sql.append(", BounceCharge, OldPenaltyAmount, NewPenaltyAmount, OtherCharge, RestructureCharge");
		sql.append(", FinCurrAssetValue, OldExtOdDays, NewExtOdDays, RepayProfitRate");
		sql.append(", BaseRate, SplRate, Margin, GrcMaxAmount, ReceiptID");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		if (rd.getId() <= 0) {
			rd.setId(getNextValue("Seq_Restructure_Details"));
		}

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, rd.getId());
				ps.setLong(index++, rd.getFinID());
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
				ps.setString(index++, rd.getBaseRate());
				ps.setString(index++, rd.getSplRate());
				ps.setBigDecimal(index++, rd.getMargin());
				ps.setBigDecimal(index++, rd.getGrcMaxAmount());
				ps.setLong(index++, JdbcUtil.getLong(rd.getReceiptID()));
				ps.setInt(index++, rd.getVersion());
				ps.setLong(index++, rd.getLastMntBy());
				ps.setTimestamp(index++, rd.getLastMntOn());
				ps.setString(index++, rd.getRecordStatus());
				ps.setString(index++, rd.getRoleCode());
				ps.setString(index++, rd.getNextRoleCode());
				ps.setString(index++, rd.getTaskId());
				ps.setString(index++, rd.getNextTaskId());
				ps.setString(index++, rd.getRecordType());
				ps.setLong(index, rd.getWorkflowId());
			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return rd.getId();
	}

	@Override
	public RestructureDetail getRestructureDetailById(long id, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		RestructureDetailRowMapper rowMapper = new RestructureDetailRowMapper();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void update(RestructureDetail rd, String tableType) {
		StringBuilder sql = new StringBuilder("Update Restructure_Details");
		sql.append(tableType);
		sql.append(" Set FinID = ?, FinReference = ?, RestructureType = ?, RestructureDate = ?, EmiHldPeriod = ?");
		sql.append(", PriHldPeriod = ?, EmiPeriods = ?, TenorChange = ?, EmiRecal = ?, TotNoOfRestructure = ?");
		sql.append(", RecalculationType = ?, ServiceRequestNo = ?, Remark = ?, OldBucket = ?, NewBucket = ?");
		sql.append(", OldEmiOs = ?, NewEmiOs = ?, OldBalTenure = ?, NewBalTenure = ?, OldMaturity = ?");
		sql.append(", NewMaturity = ?, LastBilledDate = ?, LastBilledInstNo = ?, ActLoanAmount = ?");
		sql.append(", OldTenure = ?, NewTenure = ?, OldInterest = ?, NewInterest = ?, OldMaxUnplannedEmi = ?");
		sql.append(", NewMaxUnplannedEmi = ?, OldAvailedUnplanEmi = ?, NewAvailedUnplanEmi = ?, OldFinalEmi = ?");
		sql.append(", NewFinalEmi = ?, OldDpd = ?, NewDpd = ?, OldCpzInterest = ?, NewCpzInterest = ?");
		sql.append(", EmiHldStartDate = ?, EmiHldEndDate = ?, PriHldStartDate = ?, PriHldEndDate = ?");
		sql.append(", AppDate = ?, OldPOsAmount = ?, NewPOsAmount = ?, OldEmiOverdue = ?");
		sql.append(", NewEmiOverdue = ?, BounceCharge = ?, OldPenaltyAmount = ?, NewPenaltyAmount = ?");
		sql.append(", OtherCharge = ?, RestructureCharge = ?, RepayProfitRate = ?, BaseRate = ?");
		sql.append(", SplRate = ?, Margin = ?, FinCurrAssetValue = ?, OldExtOdDays = ?");
		sql.append(", NewExtOdDays = ?, GrcMaxAmount = ?, ReceiptID = ?, Version = ?, LastMntBy = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?,  RestructureReason = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, rd.getFinID());
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
			ps.setString(index++, rd.getBaseRate());
			ps.setString(index++, rd.getSplRate());
			ps.setBigDecimal(index++, rd.getMargin());
			ps.setBigDecimal(index++, rd.getFinCurrAssetValue());
			ps.setInt(index++, rd.getOldExtOdDays());
			ps.setInt(index++, rd.getNewExtOdDays());
			ps.setBigDecimal(index++, rd.getGrcMaxAmount());
			ps.setLong(index++, rd.getReceiptID());
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

			ps.setLong(index, rd.getId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(long id, String tableType) {
		StringBuilder sql = new StringBuilder("Delete From Restructure_Details");
		sql.append(tableType);
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, id);
		});
	}

	@Override
	public RestructureDetail getRestructureDetailByFinReference(long finID, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where Id = ");
		sql.append("(Select max(Id) From Restructure_Details");
		sql.append(type);
		sql.append(" Where FinID = ?)");

		logger.debug(Literal.SQL + sql.toString());

		RestructureDetailRowMapper rowMapper = new RestructureDetailRowMapper();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private StringBuilder sqlSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, FinReference, RestructureType, RestructureDate, EmiHldPeriod, PriHldPeriod");
		sql.append(", EmiPeriods, TenorChange, EmiRecal, TotNoOfRestructure, RecalculationType, ServiceRequestNo");
		sql.append(", Remark, OldBucket, NewBucket, OldEmiOs, NewEmiOs, OldBalTenure, NewBalTenure");
		sql.append(", OldMaturity, NewMaturity, LastBilledDate, LastBilledInstNo, ActLoanAmount, OldTenure");
		sql.append(", NewTenure, OldInterest, NewInterest, OldMaxUnplannedEmi, NewMaxUnplannedEmi");
		sql.append(", OldAvailedUnplanEmi, NewAvailedUnplanEmi, OldFinalEmi, NewFinalEmi, RestructureReason");
		sql.append(", OldDpd, NewDpd, OldCpzInterest, NewCpzInterest, EmiHldStartDate, EmiHldEndDate");
		sql.append(", PriHldStartDate, PriHldEndDate, AppDate, OldPOsAmount, NewPOsAmount");
		sql.append(", OldEmiOverdue, NewEmiOverdue, BounceCharge, OldPenaltyAmount, NewPenaltyAmount");
		sql.append(", OtherCharge, RestructureCharge, FinCurrAssetValue, OldExtOdDays, NewExtOdDays");
		sql.append(", RepayProfitRate, BaseRate, SplRate, Margin, GrcMaxAmount, ReceiptID");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", RstTypeCode, RstTypeDesc");
		sql.append(" From Restructure_Details");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class RestructureDetailRowMapper implements RowMapper<RestructureDetail> {
		private RestructureDetailRowMapper() {
			super();
		}

		@Override
		public RestructureDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			RestructureDetail rd = new RestructureDetail();

			rd.setId(rs.getLong("Id"));
			rd.setFinID(rs.getLong("FinID"));
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
			rd.setBaseRate(rs.getString("BaseRate"));
			rd.setSplRate(rs.getString("SplRate"));
			rd.setMargin(rs.getBigDecimal("Margin"));
			rd.setGrcMaxAmount(rs.getBigDecimal("GrcMaxAmount"));
			rd.setReceiptID(rs.getLong("ReceiptID"));
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
	}

	@Override
	public int saveChargeList(List<RestructureCharge> chargeList, String type) {
		StringBuilder sql = new StringBuilder("Insert Into Restructure_Charges");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (ID, RestructureId, ChargeSeq, AlocType, Capitalized, FeeCode, ActualAmount");
		sql.append(", TaxType, Cgst, Sgst, Ugst, Igst, Cess, TdsAmount, TotalAmount");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {

					RestructureCharge rc = chargeList.get(i);
					int index = 1;

					if (rc.getId() == 0 || rc.getId() == Long.MIN_VALUE) {
						rc.setId(getNextValue("SeqRESTRUCTURE_CHARGES"));
					}

					ps.setLong(index++, rc.getId());
					ps.setLong(index++, rc.getRestructureId());
					ps.setInt(index++, rc.getChargeSeq());
					ps.setString(index++, rc.getAlocType());
					ps.setBoolean(index++, rc.isCapitalized());
					ps.setString(index++, rc.getFeeCode());
					ps.setBigDecimal(index++, rc.getActualAmount());
					ps.setString(index++, rc.getTaxType());
					ps.setBigDecimal(index++, rc.getCgst());
					ps.setBigDecimal(index++, rc.getSgst());
					ps.setBigDecimal(index++, rc.getUgst());
					ps.setBigDecimal(index++, rc.getIgst());
					ps.setBigDecimal(index++, rc.getCess());
					ps.setBigDecimal(index++, rc.getTdsAmount());
					ps.setBigDecimal(index++, rc.getTotalAmount());
					ps.setInt(index++, rc.getVersion());
					ps.setLong(index++, JdbcUtil.getLong(rc.getLastMntBy()));
					ps.setTimestamp(index++, rc.getLastMntOn());
					ps.setString(index++, rc.getRecordStatus());
					ps.setString(index++, rc.getRoleCode());
					ps.setString(index++, rc.getNextRoleCode());
					ps.setString(index++, rc.getTaskId());
					ps.setString(index++, rc.getNextTaskId());
					ps.setString(index++, rc.getRecordType());
					ps.setLong(index, JdbcUtil.getLong(rc.getWorkflowId()));
				}

				@Override
				public int getBatchSize() {
					return chargeList.size();
				}
			}).length;

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void deleteChargeList(long restructureId, String type) {
		StringBuilder sql = new StringBuilder("Delete From Restructure_Charges");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where RestructureId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, restructureId));
	}

	@Override
	public void deleteChargeList(long restructureId, int chargeSeq, String type) {
		StringBuilder sql = new StringBuilder("Delete From Restructure_Charges");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where RestructureId = ? and ChargeSeq = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setLong(1, restructureId);
			ps.setInt(2, chargeSeq);
		});
	}

	@Override
	public void deleteRestructureCharges(long id, String type) {
		StringBuilder sql = new StringBuilder("Delete From Restructure_Charges");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, id));
	}

	@Override
	public List<RestructureCharge> getRestructureCharges(long restructureId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, RestructureId, ChargeSeq, AlocType, Capitalized, FeeCode, ActualAmount");
		sql.append(", TaxType, Cgst, Sgst, Ugst, Igst, Cess, TdsAmount, TotalAmount");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Restructure_Charges");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where RestructureId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, restructureId), (rs, rowNum) -> {
			RestructureCharge rc = new RestructureCharge();

			rc.setId(rs.getLong("ID"));
			rc.setRestructureId(rs.getLong("RestructureId"));
			rc.setChargeSeq(rs.getInt("ChargeSeq"));
			rc.setAlocType(rs.getString("AlocType"));
			rc.setCapitalized(rs.getBoolean("Capitalized"));
			rc.setFeeCode(rs.getString("FeeCode"));
			rc.setActualAmount(rs.getBigDecimal("ActualAmount"));
			rc.setTaxType(rs.getString("TaxType"));
			rc.setCgst(rs.getBigDecimal("Cgst"));
			rc.setSgst(rs.getBigDecimal("Sgst"));
			rc.setUgst(rs.getBigDecimal("Ugst"));
			rc.setIgst(rs.getBigDecimal("Igst"));
			rc.setCess(rs.getBigDecimal("Cess"));
			rc.setTdsAmount(rs.getBigDecimal("TdsAmount"));
			rc.setTotalAmount(rs.getBigDecimal("TotalAmount"));
			rc.setVersion(rs.getInt("Version"));
			rc.setLastMntBy(rs.getLong("LastMntBy"));
			rc.setLastMntOn(rs.getTimestamp("LastMntOn"));
			rc.setRecordStatus(rs.getString("RecordStatus"));
			rc.setRoleCode(rs.getString("RoleCode"));
			rc.setNextRoleCode(rs.getString("NextRoleCode"));
			rc.setTaskId(rs.getString("TaskId"));
			rc.setNextTaskId(rs.getString("NextTaskId"));
			rc.setRecordType(rs.getString("RecordType"));
			rc.setWorkflowId(rs.getLong("WorkflowId"));

			return rc;
		});

	}

	@Override
	public boolean isExistRestructureType(long rstTypeId, boolean isStepFinance) {
		Object object[] = new Object[] { rstTypeId };

		StringBuilder sql = new StringBuilder("Select Count(*) from Restructure_Types Where id = ?");

		if (isStepFinance) {
			sql.append(" and alwStep = ?");
			object = new Object[] { rstTypeId, isStepFinance };
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Boolean.class, object);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public boolean isExistRestructureReason(String code) {
		String sql = "Select Count(FieldCode) from RMTLovFieldDetail Where FieldCode = ? and IsActive = ? and FieldCodeValue = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Boolean.class, CalculationConstants.RESTRUCTURE_REASON, 1, code);
	}

	@Override
	public boolean checkLoanProduct(long finID) {
		String sql = "Select Count(*) from FinanceMain Where FinID = ? and FinType = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Boolean.class, finID, "LAEP");
	}

	@Override
	public RestructureType getRestructureTypeById(String rstTypeId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" RstTypeCode, RstTypeDesc, MaxEmiHoliday, MaxPriHoliday, MaxEmiTerm, MaxTotTerm");
		sql.append(" From Restructure_Types Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				RestructureType rstType = new RestructureType();
				rstType.setRstTypeCode(rs.getString("RstTypeCode"));
				rstType.setRstTypeDesc(rs.getString("RstTypeDesc"));
				rstType.setMaxEmiHoliday(rs.getInt("MaxEmiHoliday"));
				rstType.setMaxPriHoliday(rs.getInt("MaxPriHoliday"));
				rstType.setMaxEmiTerm(rs.getInt("MaxEmiTerm"));
				rstType.setMaxTotTerm(rs.getInt("MaxTotTerm"));
				return rstType;
			}, Long.parseLong(rstTypeId));
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record is not found in Restructure_Types for the specified Id >> {}", rstTypeId);
		}

		return null;
	}
}
