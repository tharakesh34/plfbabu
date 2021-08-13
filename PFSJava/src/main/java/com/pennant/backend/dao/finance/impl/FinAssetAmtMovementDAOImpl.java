package com.pennant.backend.dao.finance.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.finance.FinAssetAmtMovementDAO;
import com.pennant.backend.model.finance.FinAssetAmtMovement;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinAssetAmtMovementDAOImpl extends SequenceDao<FinAssetAmtMovement> implements FinAssetAmtMovementDAO {
	private static Logger logger = LogManager.getLogger(FinAssetAmtMovementDAOImpl.class);

	public FinAssetAmtMovementDAOImpl() {
		super();
	}

	@Override
	public List<FinAssetAmtMovement> getFinAssetAmtMovements(long finID, String movementType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinServiceInstID, FinID, FinReference, MovementDate, MovementOrder");
		sql.append(", MovementType, MovementAmount, AvailableAmt, SanctionedAmt");
		sql.append(", RevisedSanctionedAmt, DisbursedAmt, LastMntBy, LastMntOn");
		sql.append(" From FinAssetAmtMovements");
		sql.append(" Where FinID = ? and MovementType = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<FinAssetAmtMovement> list = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			ps.setString(2, movementType);
		}, (rs, rowNum) -> {
			FinAssetAmtMovement fam = new FinAssetAmtMovement();

			fam.setFinServiceInstID(rs.getLong("FinServiceInstID"));
			fam.setFinID(rs.getLong("FinID"));
			fam.setFinReference(rs.getString("FinReference"));
			fam.setMovementDate(rs.getDate("MovementDate"));
			fam.setMovementOrder(rs.getLong("MovementOrder"));
			fam.setMovementType(rs.getString("MovementType"));
			fam.setMovementAmount(rs.getBigDecimal("MovementAmount"));
			fam.setAvailableAmt(rs.getBigDecimal("AvailableAmt"));
			fam.setSanctionedAmt(rs.getBigDecimal("SanctionedAmt"));
			fam.setRevisedSanctionedAmt(rs.getBigDecimal("RevisedSanctionedAmt"));
			fam.setDisbursedAmt(rs.getBigDecimal("DisbursedAmt"));
			fam.setLastMntBy(rs.getLong("LastMntBy"));
			fam.setLastMntOn(rs.getTimestamp("LastMntOn"));

			return fam;
		});

		return list.stream().sorted((l1, l2) -> Long.compare(l2.getFinServiceInstID(), l1.getFinServiceInstID()))
				.collect(Collectors.toList());
	}

	@Override
	public void saveFinAssetAmtMovement(FinAssetAmtMovement faam) {
		StringBuilder sql = new StringBuilder("Insert Into FinAssetAmtMovements");
		sql.append("(FinServiceInstID, FinID, FinReference, MovementDate, MovementOrder");
		sql.append(", MovementType, MovementAmount, AvailableAmt, SanctionedAmt");
		sql.append(", RevisedSanctionedAmt, DisbursedAmt, LastMntBy, LastMntOn)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, faam.getFinServiceInstID());
			ps.setLong(index++, faam.getFinID());
			ps.setString(index++, faam.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(faam.getMovementDate()));
			ps.setLong(index++, faam.getMovementOrder());
			ps.setString(index++, faam.getMovementType());
			ps.setBigDecimal(index++, faam.getMovementAmount());
			ps.setBigDecimal(index++, faam.getAvailableAmt());
			ps.setBigDecimal(index++, faam.getSanctionedAmt());
			ps.setBigDecimal(index++, faam.getRevisedSanctionedAmt());
			ps.setBigDecimal(index++, faam.getDisbursedAmt());
			ps.setLong(index++, faam.getLastMntBy());
			ps.setTimestamp(index++, faam.getLastMntOn());
		});
	}
}