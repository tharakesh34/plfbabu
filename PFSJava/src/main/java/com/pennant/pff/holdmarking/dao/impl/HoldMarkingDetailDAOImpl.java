package com.pennant.pff.holdmarking.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.pff.holdmarking.model.HoldMarkingDetail;
import com.pennant.pff.holdmarking.upload.dao.HoldMarkingDetailDAO;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class HoldMarkingDetailDAOImpl extends SequenceDao<HoldMarkingDetail> implements HoldMarkingDetailDAO {

	public HoldMarkingDetailDAOImpl() {
		super();
	}

	@Override
	public void saveDetail(HoldMarkingDetail hmd) {
		StringBuilder sql = new StringBuilder("Insert into Hold_Marking_Details");
		sql.append(" (HeaderID, HoldID, FinID, FinReference, HoldType, Marking, MovementDate");
		sql.append(", Status, Amount, LogID, HoldReleaseReason");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMnton)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (hmd.getHoldID() <= 0) {
			hmd.setHoldID((getNextValue("SEQ_HOLD_MARKING_HEADER_HOLDID")));
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, hmd.getHeaderID());
			ps.setLong(++index, hmd.getHoldID());
			ps.setLong(++index, hmd.getFinID());
			ps.setString(++index, hmd.getFinReference());
			ps.setString(++index, hmd.getHoldType());
			ps.setString(++index, hmd.getMarking());
			ps.setDate(++index, JdbcUtil.getDate(hmd.getMovementDate()));
			ps.setString(++index, hmd.getStatus());
			ps.setBigDecimal(++index, hmd.getAmount());
			ps.setLong(++index, hmd.getLogID());
			ps.setString(++index, hmd.getHoldReleaseReason());
			ps.setInt(++index, hmd.getVersion());
			ps.setObject(++index, hmd.getCreatedBy());
			ps.setTimestamp(++index, hmd.getCreatedOn());
			ps.setObject(++index, hmd.getApprovedBy());
			ps.setTimestamp(++index, hmd.getApprovedOn());
			ps.setLong(++index, hmd.getLastMntBy());
			ps.setTimestamp(++index, hmd.getLastMntOn());
		});
	}

	@Override
	public int getCountId(long id) {
		String sql = "Select Count(ID) From Hold_Marking_Details Where HoldID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, id);
	}

	@Override
	public HoldMarkingDetail getHoldByReference(long finId, String accNum) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" hmd.HoldID, hmd.FinID, hmd.FinReference, hmd.HoldType, hmd.Marking");
		sql.append(", hmd.HoldAmount, hmd.ReleaseAmount, hmd.Balance");
		sql.append(" From Hold_Marking_Details hmd");
		sql.append(" Where hmd.FinID = ? and hmd.AccountNumber = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				HoldMarkingDetail hmd = new HoldMarkingDetail();

				hmd.setHoldID(rs.getLong("HoldID"));
				hmd.setFinID(rs.getLong("FinID"));
				hmd.setFinReference(rs.getString("FinReference"));
				hmd.setHoldType(rs.getString("HoldType"));
				hmd.setMarking(rs.getString("Marking"));
				hmd.setMovementDate(rs.getDate("MovementDate"));
				hmd.setStatus(rs.getString("Status"));
				hmd.setAmount(rs.getBigDecimal(("Amount")));
				hmd.setLogID(rs.getLong("LogID"));
				hmd.setHoldReleaseReason(rs.getString("HoldReleaseReason"));

				return hmd;

			}, finId, accNum);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<HoldMarkingDetail> getHoldDtlsByRefAndAcc(String reference, String accNumber) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" hmd.HoldID, hmd.FinID, hmd.FinReference, hmd.HoldType, hmd.Marking");
		sql.append(",hmh.HoldAmount, hmh.ReleaseAmount, hmh.Balance, hmd.MovementDate");
		sql.append(",hmh.AccountNumber, hmd.Status,hmd.HoldReleaseReason, hmh.HoldReference, hmd.Amount");
		sql.append(" From Hold_Marking_Details hmd  ");
		sql.append(" Left Join HOLD_MARKING_HEADER hmh on hmd.HoldID = hmh.HoldID");
		Object[] args = null;
		if (!StringUtils.isEmpty(reference)) {
			sql.append(" Where hmd.FinReference =?");
			args = new Object[] { reference };
		} else {
			sql.append(" Where hmh.AccountNumber =?");
			args = new Object[] { accNumber };

		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
				HoldMarkingDetail hmd = new HoldMarkingDetail();
				hmd.setHoldID(rs.getLong("HoldID"));
				hmd.setFinID(rs.getLong("FinID"));
				hmd.setFinReference(rs.getString("FinReference"));
				hmd.setHoldType(rs.getString("HoldType"));
				hmd.setMarking(rs.getString("Marking"));
				hmd.setHoldAmount(rs.getBigDecimal("HoldAmount"));
				hmd.setReleaseAmount(rs.getBigDecimal("ReleaseAmount"));
				hmd.setBalance(rs.getBigDecimal(("Balance")));
				hmd.setMovementDate(rs.getDate("MovementDate"));
				hmd.setAccountNumber(rs.getString("AccountNumber"));
				hmd.setStatus(rs.getString("Status"));
				hmd.setHoldReleaseReason(rs.getString("HoldReleaseReason"));
				hmd.setHoldReference(rs.getLong("HoldReference"));
				hmd.setAmount(rs.getBigDecimal("Amount"));

				return hmd;
			}, args);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

}