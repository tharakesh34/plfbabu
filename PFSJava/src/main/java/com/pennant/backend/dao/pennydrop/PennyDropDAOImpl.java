package com.pennant.backend.dao.pennydrop;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.model.pennydrop.BankAccountValidation;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class PennyDropDAOImpl extends SequenceDao<BankAccountValidation> implements PennyDropDAO {
	private static Logger logger = LogManager.getLogger(PennyDropDAOImpl.class);

	public PennyDropDAOImpl() {
		super();
	}

	@Override
	public void savePennyDropSts(BankAccountValidation bankAccountValidations) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder(" Insert Into PENNY_DROP_STATUS");
		insertSql.append(" (AcctNum, IFSC, InitiateType, Status, Reason)");
		insertSql.append(" Values(:AcctNum, :iFSC , :initiateType, :Status, :Reason)");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankAccountValidations);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);

	}

	@Override
	public int getPennyDropCount(String accNumber, String ifsc) {
		logger.debug(Literal.ENTERING);
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM PENNY_DROP_STATUS");
		selectSql.append(" WHERE AcctNum = :AccNumber AND IFSC = :ifsc AND status= :status");
		logger.debug(Literal.SQL + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AccNumber", accNumber);
		source.addValue("ifsc", ifsc);
		source.addValue("status", true);

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			recordCount = 0;
		}
		logger.debug(Literal.LEAVING);
		return recordCount;

	}

	@Override
	public BankAccountValidation getPennyDropStatusByAcc(String accNum, String ifsc) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, AcctNum, IFSC, InitiateType, Status, Reason");
		sql.append(" from PENNY_DROP_STATUS");
		sql.append(" Where AcctNum = ? And IFSC = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { accNum, ifsc },
					new RowMapper<BankAccountValidation>() {
						@Override
						public BankAccountValidation mapRow(ResultSet rs, int rowNum) throws SQLException {
							BankAccountValidation pds = new BankAccountValidation();

							pds.setID(rs.getLong("ID"));
							pds.setAcctNum(rs.getString("AcctNum"));
							pds.setiFSC(rs.getString("IFSC"));
							pds.setInitiateType(rs.getString("InitiateType"));
							pds.setStatus(rs.getBoolean("Status"));
							pds.setReason(rs.getString("Reason"));

							return pds;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Penny Drop details not avilable for the specified account number {} with specified IFSC {}",
					"XXXX", ifsc);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

}
