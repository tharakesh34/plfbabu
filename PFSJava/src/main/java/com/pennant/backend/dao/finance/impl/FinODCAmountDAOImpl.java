package com.pennant.backend.dao.finance.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.dao.finance.FinODCAmountDAO;
import com.pennant.backend.model.finance.FinOverDueChargeMovement;
import com.pennant.backend.model.finance.FinOverDueCharges;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class FinODCAmountDAOImpl extends SequenceDao<FinOverDueCharges> implements FinODCAmountDAO {
	private static Logger logger = LogManager.getLogger(FinODCAmountDAOImpl.class);

	@Override
	public List<FinOverDueCharges> getFinODCAmtByFinRef(long finid, Date schdDate, String chargeType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, SchDate, PostDate, ValueDate, Amount, PaidAmount");
		sql.append(", WaivedAmount, BalanceAmt, OdPri, OdPft, FinOdTillDate, DueDays, ChargeType");
		sql.append(" From Fin_OverDue_Charges");
		sql.append(" Where FinID = ? and SchDate = ? and ChargeType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<FinOverDueCharges> list = jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinOverDueCharges fod = new FinOverDueCharges();

			fod.setId(rs.getLong("ID"));
			fod.setFinID(rs.getLong("FinID"));
			fod.setSchDate(rs.getDate("SchDate"));
			fod.setPostDate(rs.getDate("PostDate"));
			fod.setValueDate(rs.getDate("ValueDate"));
			fod.setAmount(rs.getBigDecimal("Amount"));
			fod.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			fod.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			fod.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			fod.setOdPri(rs.getBigDecimal("OdPri"));
			fod.setOdPft(rs.getBigDecimal("OdPft"));
			fod.setFinOdTillDate(rs.getDate("FinOdTillDate"));
			fod.setDueDays(rs.getInt("DueDays"));
			fod.setChargeType(rs.getString("ChargeType"));

			return fod;
		}, finid, schdDate, chargeType);

		Collections.sort(list, new Comparator<FinOverDueCharges>() {
			@Override
			public int compare(FinOverDueCharges obj1, FinOverDueCharges obj2) {
				return DateUtil.compare(obj1.getPostDate(), obj2.getPostDate());
			}
		});
		return list;
	}

	@Override
	public void updateFinODCAmts(List<FinOverDueCharges> updateList) {
		String sql = "Update Fin_OverDue_Charges Set Amount = ?, BalanceAmt = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinOverDueCharges fd = updateList.get(i);

				ps.setBigDecimal(1, fd.getAmount());
				ps.setBigDecimal(2, fd.getBalanceAmt());
				ps.setLong(3, fd.getId());
			}

			@Override
			public int getBatchSize() {
				return updateList.size();
			}
		});
	}

	@Override
	public void saveFinODCAmts(List<FinOverDueCharges> finODCAmounts) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" Fin_OverDue_Charges");
		sql.append(" (FinID, SchDate, PostDate, ValueDate, Amount, PaidAmount, WaivedAmount");
		sql.append(", BalanceAmt, OdPri, OdPft, FinOdTillDate, DueDays, ChargeType");
		sql.append(") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinOverDueCharges fd = finODCAmounts.get(i);

				int index = 0;

				ps.setLong(++index, fd.getFinID());
				ps.setDate(++index, JdbcUtil.getDate(fd.getSchDate()));
				ps.setDate(++index, JdbcUtil.getDate(fd.getPostDate()));
				ps.setDate(++index, JdbcUtil.getDate(fd.getValueDate()));
				ps.setBigDecimal(++index, fd.getAmount());
				ps.setBigDecimal(++index, fd.getPaidAmount());
				ps.setBigDecimal(++index, fd.getWaivedAmount());
				ps.setBigDecimal(++index, fd.getBalanceAmt());
				ps.setBigDecimal(++index, fd.getOdPri());
				ps.setBigDecimal(++index, fd.getOdPft());
				ps.setDate(++index, JdbcUtil.getDate(fd.getFinOdTillDate()));
				ps.setInt(++index, fd.getDueDays());
				ps.setString(++index, fd.getChargeType());
			}

			@Override
			public int getBatchSize() {
				return finODCAmounts.size();
			}
		});
	}

	@Override
	public long saveFinODCAmt(FinOverDueCharges odc) {
		StringBuilder sql = new StringBuilder("Insert Into Fin_OverDue_Charges");
		sql.append(" (FinID, SchDate, PostDate, ValueDate, Amount,");
		sql.append(" PaidAmount, WaivedAmount, BalanceAmt, OdPri, OdPft, FinOdTillDate, DueDays, ChargeType)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL.concat(sql.toString()));

		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();

			this.jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "ID" });

					int index = 1;

					ps.setLong(index++, odc.getFinID());
					ps.setDate(index++, JdbcUtil.getDate(odc.getSchDate()));
					ps.setDate(index++, JdbcUtil.getDate(odc.getPostDate()));
					ps.setDate(index++, JdbcUtil.getDate(odc.getValueDate()));
					ps.setBigDecimal(index++, odc.getAmount());
					ps.setBigDecimal(index++, odc.getPaidAmount());
					ps.setBigDecimal(index++, odc.getWaivedAmount());
					ps.setBigDecimal(index++, odc.getBalanceAmt());
					ps.setBigDecimal(index++, odc.getOdPri());
					ps.setBigDecimal(index++, odc.getOdPft());
					ps.setDate(index++, JdbcUtil.getDate(odc.getFinOdTillDate()));
					ps.setInt(index++, odc.getDueDays());
					ps.setString(index++, odc.getChargeType());

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void saveMovement(List<FinOverDueChargeMovement> movements) {
		StringBuilder sql = new StringBuilder("Insert into Fin_OverDue_Charge_Movements");
		sql.append(" (ChargeId, MovementDate, MovementAmount, PaidAmount, WaivedAmount,");
		sql.append(" Status, ReceiptID, WaiverID)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinOverDueChargeMovement odc = movements.get(i);

				int index = 1;
				ps.setLong(index++, odc.getChargeId());
				ps.setDate(index++, JdbcUtil.getDate(odc.getMovementDate()));
				ps.setBigDecimal(index++, odc.getMovementAmount());
				ps.setBigDecimal(index++, odc.getPaidAmount());
				ps.setBigDecimal(index++, odc.getWaivedAmount());
				ps.setString(index++, odc.getStatus());
				ps.setLong(index++, JdbcUtil.getLong(odc.getReceiptID()));
				ps.setLong(index++, JdbcUtil.getLong(odc.getWaiverID()));
			}

			@Override
			public int getBatchSize() {
				return movements.size();
			}
		});

	}

	@Override
	public void updateFinODCBalAmts(List<FinOverDueCharges> finODCAmounts) {
		String sql = "Update Fin_OverDue_Charges Set PaidAmount = ?, WaivedAmount = ?, BalanceAmt = ? , OdPri= ?, OdPft= ?  Where ID = ?";

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinOverDueCharges fd = finODCAmounts.get(i);

				int index = 1;

				ps.setBigDecimal(index++, fd.getPaidAmount());
				ps.setBigDecimal(index++, fd.getWaivedAmount());
				ps.setBigDecimal(index++, fd.getBalanceAmt());
				ps.setBigDecimal(index++, fd.getOdPri());
				ps.setBigDecimal(index++, fd.getOdPft());

				ps.setLong(index++, fd.getId());
			}

			@Override
			public int getBatchSize() {
				return finODCAmounts.size();
			}
		});

	}

	@Override
	public List<FinOverDueChargeMovement> getFinODCMovements(long receiptID) {
		StringBuilder sql = new StringBuilder("Select  ChargeId, MovementAmount, ");
		sql.append(" PaidAmount, WaivedAmount, Status, ReceiptID");
		sql.append(" From Fin_OverDue_Charge_Movements");
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, receiptID);
		}, (rs, i) -> {
			FinOverDueChargeMovement mam = new FinOverDueChargeMovement();
			mam.setChargeId(rs.getLong("ChargeId"));
			mam.setMovementAmount(rs.getBigDecimal("MovementAmount"));
			mam.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			mam.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			mam.setStatus(rs.getString("Status"));
			mam.setReceiptID(rs.getLong("ReceiptID"));

			return mam;
		});
	}

	@Override
	public void updateReversals(List<FinOverDueCharges> updatedODAmt) {
		String sql = "Update Fin_OverDue_Charges Set PaidAmount = PaidAmount - ?, WaivedAmount = WaivedAmount - ?, BalanceAmt= BalanceAmt + ? + ? Where ID = ? ";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinOverDueCharges fd = updatedODAmt.get(i);

				int index = 1;

				ps.setBigDecimal(index++, fd.getPaidAmount());
				ps.setBigDecimal(index++, fd.getWaivedAmount());
				ps.setBigDecimal(index++, fd.getPaidAmount());
				ps.setBigDecimal(index++, fd.getWaivedAmount());

				ps.setLong(index++, fd.getId());
			}

			@Override
			public int getBatchSize() {
				return updatedODAmt.size();
			}
		});

	}

	@Override
	public void updateMovenantStatus(long receiptID, String status) {
		String sql = "Update Fin_OverDue_Charge_Movements Set Status = ? Where ReceiptID = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql, status, receiptID);
	}

	@Override
	public List<FinOverDueCharges> getFinODCAmtByRef(long finID, String chargeType) {
		StringBuilder sql = new StringBuilder("Select Id, FinID, SchDate, PostDate,");
		sql.append(" ValueDate, Amount, PaidAmount, WaivedAmount, BalanceAmt, OdPri, OdPft, FinOdTillDate, DueDays");
		sql.append(" From Fin_OverDue_Charges");
		sql.append(" Where  FinID = ? And ChargeType = ? order by Id");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinOverDueCharges fod = new FinOverDueCharges();

			fod.setId(rs.getLong("ID"));
			fod.setFinID(rs.getLong("FinID"));
			fod.setSchDate(rs.getDate("SchDate"));
			fod.setPostDate(rs.getDate("PostDate"));
			fod.setValueDate(rs.getDate("ValueDate"));
			fod.setAmount(rs.getBigDecimal("Amount"));
			fod.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			fod.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			fod.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			fod.setOdPri(rs.getBigDecimal("OdPri"));
			fod.setOdPft(rs.getBigDecimal("OdPft"));
			fod.setFinOdTillDate(rs.getDate("FinOdTillDate"));
			fod.setDueDays(rs.getInt("DueDays"));

			return fod;
		}, finID, chargeType);
	}
}
