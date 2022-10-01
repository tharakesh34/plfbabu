package com.pennanttech.pff.overdraft.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.odsettlementprocess.model.ODSettlementProcess;
import com.pennanttech.pff.overdraft.dao.OverdraftSettlementDAO;

public class OverdraftSettlementDAOImpl extends SequenceDao<ODSettlementProcess> implements OverdraftSettlementDAO {
	private static Logger logger = LogManager.getLogger(OverdraftSettlementDAOImpl.class);

	public OverdraftSettlementDAOImpl() {
		super();
	}

	@Override
	public void saveODSettlementProcessRequest(MapSqlParameterSource oDSettlementMapdata) {
		StringBuilder sql = new StringBuilder("Insert into OVERDRAFT_SETTLEMENT_REQ");
		sql.append("(RequestBatchId, TerminalId, MerchantName, CustomerId");
		sql.append(", TxnId, TxnDate, TxnType, Reference, Currency, Amount, ODSettlementRef)");
		sql.append(" Values");
		sql.append("(:RequestBatchId, :TerminalId, :MerchantName, :CustomerId");
		sql.append(", :TxnId, :TxnDate, :TxnType, :Reference, :Currency, :Amount, :ODSettlementRef)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.update(sql.toString(), oDSettlementMapdata);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public boolean isDuplicateReference(String reference, String odSettlementRef) {
		String sql = "select count(Reference) from OVERDRAFT_SETTLEMENT_REQ Where Reference = ? and ODSettlementRef = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, reference, odSettlementRef) > 0;
	}

	@Override
	public boolean isDuplicateODSettlementRef(String ODSettlementRef) {
		String sql = "select count(Reference) from OVERDRAFT_SETTLEMENT_REQ Where ODSettlementRef = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, ODSettlementRef) > 0;
	}

}
