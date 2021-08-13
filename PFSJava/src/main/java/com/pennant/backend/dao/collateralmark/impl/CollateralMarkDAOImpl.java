package com.pennant.backend.dao.collateralmark.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.collateralmark.CollateralMarkDAO;
import com.pennant.backend.model.collateral.FinCollateralMark;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class CollateralMarkDAOImpl extends SequenceDao<FinCollateralMark> implements CollateralMarkDAO {
	private static Logger logger = LogManager.getLogger(CollateralMarkDAOImpl.class);

	public CollateralMarkDAOImpl() {
		super();
	}

	@Override
	public int save(FinCollateralMark fcm) {
		if (fcm.getId() == 0 || fcm.getId() == Long.MIN_VALUE) {
			fcm.setFinCollateralId(getNextValue("SeqCollateralMarkLog"));
		}

		StringBuilder sql = new StringBuilder("Insert Into CollateralMarkLog");
		sql.append(" (FinCollateralId, FinID, FinReference, ReferenceNum, Status");
		sql.append(", Reason, BranchCode, DepositID, InsAmount, BlockingDate");
		sql.append(", ReturnCode, ReturnText, Processed)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, fcm.getFinCollateralId());
				ps.setLong(index++, fcm.getFinID());
				ps.setString(index++, fcm.getFinReference());
				ps.setString(index++, fcm.getReferenceNum());
				ps.setString(index++, fcm.getStatus());
				ps.setString(index++, fcm.getReason());
				ps.setString(index++, fcm.getBranchCode());
				ps.setString(index++, fcm.getDepositID());
				ps.setBigDecimal(index++, fcm.getInsAmount());
				ps.setDate(index++, JdbcUtil.getDate(fcm.getBlockingDate()));
				ps.setString(index++, fcm.getReturnCode());
				ps.setString(index++, fcm.getReturnText());
				ps.setBoolean(index++, fcm.isProcessed());

			});
		} catch (DataAccessException e) {
			return 0;
		}
	}

	@Override
	public FinCollateralMark getCollateralById(String depositId) {
		StringBuilder sql = getSqlQuery();
		sql.append(" Where DepositID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinCollateralMarkRM rowMapper = new FinCollateralMarkRM();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, depositId);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public FinCollateralMark getCollatDeMarkStatus(long finID, String markStatus) {
		StringBuilder sql = getSqlQuery();
		sql.append(" Where FinID = ? and Status = ? and Processed = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinCollateralMarkRM rowMapper = new FinCollateralMarkRM();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID, markStatus, 1);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public List<FinCollateralMark> getCollateralList(long finID) {
		List<FinCollateralMark> fcmList = new ArrayList<>();

		FinCollateralMark fcm = getCollatDeMarkStatus(finID, "MARK");
		if (fcm != null) {
			fcmList.add(fcm);
		}

		return fcmList;
	}

	private class FinCollateralMarkRM implements RowMapper<FinCollateralMark> {

		@Override
		public FinCollateralMark mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinCollateralMark fcm = new FinCollateralMark();

			fcm.setFinCollateralId(rs.getLong("FinCollateralId"));
			fcm.setFinID(rs.getLong("FinID"));
			fcm.setFinReference(rs.getString("FinReference"));
			fcm.setReferenceNum(rs.getString("ReferenceNum"));
			fcm.setStatus(rs.getString("Status"));
			fcm.setReason(rs.getString("Reason"));
			fcm.setBranchCode(rs.getString("BranchCode"));
			fcm.setDepositID(rs.getString("DepositID"));
			fcm.setInsAmount(rs.getBigDecimal("InsAmount"));
			fcm.setBlockingDate(rs.getDate("BlockingDate"));
			fcm.setReturnCode(rs.getString("ReturnCode"));
			fcm.setReturnText(rs.getString("ReturnText"));
			fcm.setProcessed(rs.getBoolean("Processed"));

			return fcm;
		}

	}

	private StringBuilder getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinCollateralId, FinID, FinReference, ReferenceNum");
		sql.append(", Status, Reason, BranchCode, DepositID, InsAmount");
		sql.append(", BlockingDate, ReturnCode, ReturnText, Processed");
		sql.append(" From CollateralMarkLog");

		return sql;
	}

}
