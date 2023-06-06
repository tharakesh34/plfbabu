package com.pennant.pff.holdmarking.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.holdmarking.model.HoldMarkingHeader;
import com.pennant.pff.holdmarking.upload.dao.HoldMarkingHeaderDAO;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class HoldMarkingHeaderDAOImpl extends SequenceDao<HoldMarkingHeader> implements HoldMarkingHeaderDAO {

	public HoldMarkingHeaderDAOImpl() {
		super();
	}

	@Override
	public long saveHeader(HoldMarkingHeader hmh) {
		StringBuilder sql = new StringBuilder("Insert into Hold_Marking_Header");
		sql.append(" (ID, HoldID, FinID, FinReference, HoldReference, AccountNumber, HoldAmount");
		sql.append(" , ReleaseAmount, Balance)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (hmh.getId() <= 0) {
			hmh.setHoldID((getNextValue("SEQ_HOLD_MARKING_HEADER_HOLDID")));
			hmh.setId((getNextValue("SEQ_HOLD_MARKING_HEADER_ID")));
		}

		hmh.setHoldReference(hmh.getHoldID());

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, hmh.getId());
			ps.setLong(++index, hmh.getHoldID());
			ps.setLong(++index, hmh.getFinID());
			ps.setString(++index, hmh.getFinReference());
			ps.setLong(++index, hmh.getHoldReference());
			ps.setString(++index, hmh.getAccountNumber());
			ps.setBigDecimal(++index, hmh.getHoldAmount());
			ps.setBigDecimal(++index, hmh.getReleaseAmount());
			ps.setBigDecimal(++index, hmh.getBalance());
		});

		return hmh.getId();
	}

	@Override
	public int getCountFinId(long finId, String accNum) {
		String sql = "Select Count(ID) From HOLD_MARKING_HEADER Where FinId = ? and AccountNumber = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, finId, accNum);
	}

	@Override
	public List<HoldMarkingHeader> getHoldByFinId(long finId, String accNum) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" hmh.Id, hmh.HoldID, hmh.FinID, hmh.FinReference, hmh.HoldReference");
		sql.append(", hmh.AccountNumber, hmh.HoldAmount, hmh.ReleaseAmount, hmh.Balance");
		sql.append(" From HOLD_MARKING_HEADER hmh");
		sql.append(" Where  hmh.FinId = ? and hmh.AccountNumber = ? ");
		sql.append(" and hmh.Balance > ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			HoldMarkingHeader hmh = new HoldMarkingHeader();

			hmh.setId(rs.getLong("Id"));
			hmh.setHoldID(rs.getLong("HoldID"));
			hmh.setFinID(rs.getLong("FinID"));
			hmh.setFinReference(rs.getString("FinReference"));
			hmh.setHoldReference(rs.getLong("HoldReference"));
			hmh.setAccountNumber(rs.getString("AccountNumber"));
			hmh.setHoldAmount(rs.getBigDecimal("HoldAmount"));
			hmh.setReleaseAmount(rs.getBigDecimal("ReleaseAmount"));
			hmh.setBalance(rs.getBigDecimal(("Balance")));

			return hmh;
		}, finId, accNum, 0);
	}

	@Override
	public List<HoldMarkingHeader> getHoldListByFinId(long finId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" hmh.Id, hmh.HoldID, hmh.FinID, hmh.FinReference, hmh.HoldReference");
		sql.append(", hmh.AccountNumber, hmh.HoldAmount, hmh.ReleaseAmount, hmh.Balance");
		sql.append(" From HOLD_MARKING_HEADER hmh");
		sql.append(" Where hmh.FinID = ? and hmh.Balance > ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			HoldMarkingHeader hmh = new HoldMarkingHeader();

			hmh.setId(rs.getLong("Id"));
			hmh.setHoldID(rs.getLong("HoldID"));
			hmh.setFinID(rs.getLong("FinID"));
			hmh.setFinReference(rs.getString("FinReference"));
			hmh.setHoldReference(rs.getLong("HoldReference"));
			hmh.setAccountNumber(rs.getString("AccountNumber"));
			hmh.setHoldAmount(rs.getBigDecimal("HoldAmount"));
			hmh.setReleaseAmount(rs.getBigDecimal("ReleaseAmount"));
			hmh.setBalance(rs.getBigDecimal(("Balance")));

			return hmh;
		}, finId, 0);
	}

	@Override
	public List<HoldMarkingHeader> getHoldByAccNum(String accNum) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" hmh.Id, hmh.HoldID, hmh.FinID, hmh.FinReference, hmh.HoldReference");
		sql.append(", hmh.AccountNumber, hmh.HoldAmount, hmh.ReleaseAmount, hmh.Balance");
		sql.append(", fpd.CurODDays, fm.FinType, fm.FinStartDate, fpd.TotalPriBal");
		sql.append(", fm.WriteoffLoan, fm.FinRepayMethod");
		sql.append(" From HOLD_MARKING_HEADER hmh");
		sql.append(" Inner Join FinPftDetails fpd On fpd.FinID = hmh.FinID");
		sql.append(" Inner Join FinanceMain fm On fm.FinID = hmh.FinID");
		sql.append(" Where hmh.AccountNumber = ? and fm.FinRepayMethod = ? and hmh.Balance > ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			HoldMarkingHeader hmh = new HoldMarkingHeader();

			hmh.setId(rs.getLong("Id"));
			hmh.setHoldID(rs.getLong("HoldID"));
			hmh.setFinID(rs.getLong("FinID"));
			hmh.setFinReference(rs.getString("FinReference"));
			hmh.setHoldReference(rs.getLong("HoldReference"));
			hmh.setAccountNumber(rs.getString("AccountNumber"));
			hmh.setHoldAmount(rs.getBigDecimal("HoldAmount"));
			hmh.setReleaseAmount(rs.getBigDecimal("ReleaseAmount"));
			hmh.setBalance(rs.getBigDecimal(("Balance")));
			hmh.setCurODDays(rs.getInt("CurODDays"));
			hmh.setFinType(rs.getString("FinType"));
			hmh.setFinStartDate(rs.getDate("FinStartDate"));
			hmh.setTotalPriBal(rs.getBigDecimal("TotalPriBal"));
			hmh.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
			hmh.setFinRepayMethod(rs.getString("FinRepayMethod"));

			return hmh;
		}, accNum, InstrumentType.SI.code(), 0);
	}

	@Override
	public void updateHeader(HoldMarkingHeader hmh) {
		String sql = "Update HOLD_MARKING_HEADER Set Balance = ?, ReleaseAmount = ? Where FinID = ? and HoldId = ?";

		logger.debug(Literal.SQL.concat(sql));

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			ps.setBigDecimal(1, hmh.getBalance());
			ps.setBigDecimal(2, hmh.getReleaseAmount());
			ps.setLong(3, hmh.getFinID());
			ps.setLong(4, hmh.getHoldID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<HoldMarkingHeader> getAutoHold(long finId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" hmh.Id, hmh.HoldID, hmh.FinID, hmh.FinReference, hmh.HoldReference");
		sql.append(" , hmh.AccountNumber, hmh.HoldAmount, hmh.ReleaseAmount, hmh.Balance");
		sql.append(" From HOLD_MARKING_HEADER hmh");
		sql.append(" Left Join HOLD_MARKING_DETAILS hmd on hmh.HoldID = hmd.HoldID");
		sql.append(" Where  hmh.FinId = ? and hmd.HoldType = ? and hmd.Marking = ? and hmh.Balance > ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			HoldMarkingHeader hmh = new HoldMarkingHeader();

			hmh.setId(rs.getLong("Id"));
			hmh.setHoldID(rs.getLong("HoldID"));
			hmh.setFinID(rs.getLong("FinID"));
			hmh.setFinReference(rs.getString("FinReference"));
			hmh.setHoldReference(rs.getLong("HoldReference"));
			hmh.setAccountNumber(rs.getString("AccountNumber"));
			hmh.setHoldAmount(rs.getBigDecimal("HoldAmount"));
			hmh.setReleaseAmount(rs.getBigDecimal("ReleaseAmount"));
			hmh.setBalance(rs.getBigDecimal(("Balance")));

			return hmh;
		}, finId, PennantConstants.REMOVE_HOLD_MARKING, PennantConstants.AUTO_ASSIGNMENT, 0);
	}

	@Override
	public BigDecimal getHoldBalance(long finID, String accountNumber) {
		String sql = "Select coalesce(sum(balance), 0) from Hold_Marking_Header  where FinID = ? and AccountNumber = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID, accountNumber);
	}

}