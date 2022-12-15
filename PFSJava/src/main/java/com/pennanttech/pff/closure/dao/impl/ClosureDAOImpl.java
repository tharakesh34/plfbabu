package com.pennanttech.pff.closure.dao.impl;

import org.springframework.dao.DuplicateKeyException;

import com.pennant.backend.model.finance.FinReceiptData;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.closure.dao.ClosureDAO;

public class ClosureDAOImpl extends SequenceDao<FinReceiptData> implements ClosureDAO {

	public ClosureDAOImpl() {
		super();
	}

	@Override
	public void saveClosureAmount(FinReceiptData frd) {
		StringBuilder sql = new StringBuilder("Insert into Loans_Closures");
		sql.append("(FinID, ClosureAmount) Values (?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				ps.setLong(1, frd.getFinID());
				ps.setBigDecimal(2, frd.getTotalDueAmount());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}
}
