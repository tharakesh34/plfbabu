package com.pennant.backend.dao.limits.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.limits.LimitInterfaceDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limits.ClosedFacilityDetail;
import com.pennant.backend.model.limits.FinanceLimitProcess;
import com.pennant.backend.model.limits.LimitDetail;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class LimitInterfaceDAOImpl extends SequenceDao<FinanceLimitProcess> implements LimitInterfaceDAO {
	private static Logger logger = LogManager.getLogger(LimitInterfaceDAOImpl.class);

	public LimitInterfaceDAOImpl() {
		super();
	}

	@Override
	public void saveFinLimitUtil(FinanceLimitProcess flp) {
		if (flp.getId() == 0 || flp.getId() == Long.MIN_VALUE) {
			flp.setFinLimitId(getNextValue("SeqFinanceLimitProcess"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinanceLimitProcess");
		sql.append(" (FinLimitId, FinID, FinReference, RequestType, ReferenceNum, CustCIF, LimitRef, ResStatus");
		sql.append(", ResMessage, ErrorCode, ErrorMsg, ValueDate, DealAmount)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, flp.getFinLimitId());
			ps.setLong(index++, flp.getFinID());
			ps.setString(index++, flp.getFinReference());
			ps.setString(index++, flp.getRequestType());
			ps.setString(index++, flp.getReferenceNum());
			ps.setString(index++, flp.getCustCIF());
			ps.setString(index++, flp.getLimitRef());
			ps.setString(index++, flp.getResStatus());
			ps.setString(index++, flp.getResMessage());
			ps.setString(index++, flp.getErrorCode());
			ps.setString(index++, flp.getErrorMsg());
			ps.setDate(index++, JdbcUtil.getDate(flp.getValueDate()));
			ps.setBigDecimal(index++, flp.getDealAmount());
		});
	}

	@Override
	public void saveCustomerLimitDetails(LimitDetail ld) {
		StringBuilder sql = new StringBuilder("Insert Into CustomerLimitDetails (");
		sql.append(" CustCIF, LimitRef, LimitDesc, RevolvingType, LimitExpiryDate, LimitCcy");
		sql.append(", ApprovedLimitCcy, ApprovedLimit, OutstandingAmtCcy, OutstandingAmt, BlockedAmtCcy");
		sql.append(", BlockedAmt, ReservedAmtCcy, ReservedAmt, AvailableAmtCcy, AvailableAmt, Notes");
		sql.append("  ) Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, ld.getCustCIF());
			ps.setString(index++, ld.getLimitRef());
			ps.setString(index++, ld.getLimitDesc());
			ps.setString(index++, ld.getRevolvingType());
			ps.setDate(index++, JdbcUtil.getDate(ld.getLimitExpiryDate()));
			ps.setString(index++, ld.getLimitCcy());
			ps.setString(index++, ld.getApprovedLimitCcy());
			ps.setBigDecimal(index++, ld.getApprovedLimit());
			ps.setString(index++, ld.getOutstandingAmtCcy());
			ps.setBigDecimal(index++, ld.getOutstandingAmt());
			ps.setString(index++, ld.getBlockedAmtCcy());
			ps.setBigDecimal(index++, ld.getBlockedAmt());
			ps.setString(index++, ld.getReservedAmtCcy());
			ps.setBigDecimal(index++, ld.getReservedAmt());
			ps.setString(index++, ld.getAvailableAmtCcy());
			ps.setBigDecimal(index++, ld.getAvailableAmt());
			ps.setString(index++, ld.getNotes());
		});
	}

	@Override
	public FinanceLimitProcess getLimitUtilDetails(FinanceLimitProcess flps) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select * From (Select FinLimitId, FinID, FinReference, RequestType, ReferenceNum, CustCIF");
		sql.append(", LimitRef, ResStatus, ResMessage, ErrorCode, ErrorMsg, ValueDate");
		sql.append(", DealAmount, row_number() over (order by Valuedate desc)");
		sql.append(" row_num From FinanceLimitProcess Where FinID = ? and RequestType = ?");
		sql.append(" and CustCIF = ?) T Where row_num < = 1");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				FinanceLimitProcess flp = new FinanceLimitProcess();

				flp.setFinLimitId(rs.getLong("FinLimitId"));
				flp.setFinID(rs.getLong("FinID"));
				flp.setFinReference(rs.getString("FinReference"));
				flp.setRequestType(rs.getString("RequestType"));
				flp.setReferenceNum(rs.getString("ReferenceNum"));
				flp.setCustCIF(rs.getString("CustCIF"));
				flp.setLimitRef(rs.getString("LimitRef"));
				flp.setResStatus(rs.getString("ResStatus"));
				flp.setResMessage(rs.getString("ResMessage"));
				flp.setErrorCode(rs.getString("ErrorCode"));
				flp.setErrorMsg(rs.getString("ErrorMsg"));
				flp.setValueDate(rs.getTimestamp("ValueDate"));
				flp.setDealAmount(rs.getBigDecimal("DealAmount"));
				return flp;
			}, flps.getFinID(), flps.getRequestType(), flps.getCustCIF());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public LimitDetail getCustomerLimitDetails(String limitRef) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustCIF, LimitRef, LimitDesc, RevolvingType");
		sql.append(", LimitExpiryDate, LimitCcy, ApprovedLimitCcy, ApprovedLimit, OutstandingAmtCcy, OutstandingAmt");
		sql.append(", ReservedAmtCcy, ReservedAmt, AvailableAmtCcy, AvailableAmt, Notes, BlockedAmtCcy, BlockedAmt");
		sql.append("  From  CustomerLimitDetails");
		sql.append("  Where LimitRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				LimitDetail ld = new LimitDetail();

				ld.setCustCIF(rs.getString("CustCIF"));
				ld.setLimitRef(rs.getString("LimitRef"));
				ld.setLimitDesc(rs.getString("LimitDesc"));
				ld.setRevolvingType(rs.getString("RevolvingType"));
				ld.setLimitExpiryDate(rs.getDate("LimitExpiryDate"));
				ld.setLimitCcy(rs.getString("LimitCcy"));
				ld.setApprovedLimitCcy(rs.getString("ApprovedLimitCcy"));
				ld.setApprovedLimit(rs.getBigDecimal("ApprovedLimit"));
				ld.setOutstandingAmtCcy(rs.getString("OutstandingAmtCcy"));
				ld.setOutstandingAmt(rs.getBigDecimal("OutstandingAmt"));
				ld.setReservedAmtCcy(rs.getString("ReservedAmtCcy"));
				ld.setReservedAmt(rs.getBigDecimal("ReservedAmt"));
				ld.setAvailableAmtCcy(rs.getString("AvailableAmtCcy"));
				ld.setAvailableAmt(rs.getBigDecimal("AvailableAmt"));
				ld.setNotes(rs.getString("Notes"));
				ld.setBlockedAmtCcy(rs.getString("BlockedAmtCcy"));
				ld.setBlockedAmt(rs.getBigDecimal("BlockedAmt"));
				return ld;
			}, limitRef);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateCustomerLimitDetails(LimitDetail ld) {
		StringBuilder sql = new StringBuilder("Update CustomerLimitDetails");
		sql.append(" Set LimitDesc = ?, RevolvingType = ?, LimitExpiryDate = ?, LimitCcy = ?");
		sql.append(", ApprovedLimitCcy = ?, ApprovedLimit = ?, OutstandingAmtCcy = ?, OutstandingAmt = ?");
		sql.append(", BlockedAmtCcy = ?, BlockedAmt = ?, ReservedAmtCcy = ?, ReservedAmt = ?");
		sql.append(", AvailableAmtCcy = ?, AvailableAmt = ?, Notes = ?");
		sql.append(" Where LimitRef = ? and CustCIF = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, ld.getLimitDesc());
			ps.setString(index++, ld.getRevolvingType());
			ps.setDate(index++, JdbcUtil.getDate(ld.getLimitExpiryDate()));
			ps.setString(index++, ld.getLimitCcy());
			ps.setString(index++, ld.getApprovedLimitCcy());
			ps.setBigDecimal(index++, ld.getApprovedLimit());
			ps.setString(index++, ld.getOutstandingAmtCcy());
			ps.setBigDecimal(index++, ld.getOutstandingAmt());
			ps.setString(index++, ld.getBlockedAmtCcy());
			ps.setBigDecimal(index++, ld.getBlockedAmt());
			ps.setString(index++, ld.getReservedAmtCcy());
			ps.setBigDecimal(index++, ld.getReservedAmt());
			ps.setString(index++, ld.getAvailableAmtCcy());
			ps.setBigDecimal(index++, ld.getAvailableAmt());
			ps.setString(index++, ld.getNotes());
			ps.setString(index++, ld.getLimitRef());
			ps.setString(index++, ld.getCustCIF());
		});
	}

	@Override
	public boolean saveClosedFacilityDetails(List<ClosedFacilityDetail> proClFacilityList) {
		StringBuilder sql = new StringBuilder("Insert Into ClosedFaciltyDetails (");
		sql.append("LimitReference, FacilityStatus, ClosedDate, Processed, ProcessedDate");
		sql.append(") Values (?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		int[] recordCount;
		try {
			recordCount = this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ClosedFacilityDetail cfd = proClFacilityList.get(i);

					int index = 1;

					ps.setString(index++, cfd.getLimitReference());
					ps.setString(index++, cfd.getFacilityStatus());
					ps.setDate(index++, JdbcUtil.getDate(cfd.getClosedDate()));
					ps.setBoolean(index++, cfd.isProcessed());
					ps.setDate(index++, JdbcUtil.getDate(cfd.getProcessedDate()));

				}

				@Override
				public int getBatchSize() {
					return proClFacilityList.size();
				}
			});
			if (recordCount.length > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			//
			throw e;
		}
	}

	@Override
	public FinanceMain getFinanceMainByRef(String finReference, String type, boolean isRejectFinance) {
		StringBuilder sql = new StringBuilder("Select  FinID, FinReference, RoleCode");
		if (isRejectFinance) {
			sql.append(" From RejectFinancemain");
		} else {
			sql.append(" From Financemain");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setRoleCode(rs.getString("RoleCode"));

				return fm;

			}, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
