package com.pennant.backend.dao.pennydrop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.pennydrop.BankAccountValidation;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class PennyDropDAOImpl extends SequenceDao<BankAccountValidation> implements PennyDropDAO {
	private static Logger logger = LogManager.getLogger(PennyDropDAOImpl.class);

	public PennyDropDAOImpl() {
		super();
	}

	@Override
	public void savePennyDropSts(BankAccountValidation bav) {
		StringBuilder sql = new StringBuilder("Insert Into PENNY_DROP_STATUS");
		sql.append(" (AcctNum, IFSC, InitiateType, Status, Reason)");
		sql.append(" Values(?, ? , ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, bav.getAcctNum());
			ps.setString(index++, bav.getiFSC());
			ps.setString(index++, bav.getInitiateType());
			ps.setBoolean(index++, bav.isStatus());
			ps.setString(index++, bav.getReason());
		});
	}

	@Override
	public int getPennyDropCount(String accNumber, String ifsc) {
		logger.debug(Literal.ENTERING);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM PENNY_DROP_STATUS");
		selectSql.append(" WHERE AcctNum = :AccNumber AND IFSC = :ifsc AND status= :status");
		logger.debug(Literal.SQL + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AccNumber", accNumber);
		source.addValue("ifsc", ifsc);
		source.addValue("status", true);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public BankAccountValidation getPennyDropStatusByAcc(String accNum, String ifsc) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, AcctNum, IFSC, InitiateType, Status, Reason");
		sql.append(" from PENNY_DROP_STATUS");
		sql.append(" Where AcctNum = ? And IFSC = ?");

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				BankAccountValidation pds = new BankAccountValidation();

				pds.setID(rs.getLong("ID"));
				pds.setAcctNum(rs.getString("AcctNum"));
				pds.setiFSC(rs.getString("IFSC"));
				pds.setInitiateType(rs.getString("InitiateType"));
				pds.setStatus(rs.getBoolean("Status"));
				pds.setReason(rs.getString("Reason"));

				return pds;
			}, accNum, ifsc);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
