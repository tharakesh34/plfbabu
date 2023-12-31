package com.pennant.backend.dao.finance.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.finance.PricingDetailDAO;
import com.pennant.backend.model.finance.PricingDetail;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class PricingDetailDAOImpl extends SequenceDao<PricingDetail> implements PricingDetailDAO {

	private static Logger logger = LogManager.getLogger(PricingDetailDAOImpl.class);

	public PricingDetailDAOImpl() {
		super();
	}

	@Override
	public String getConfiguredTopUpFinType(String loanType) {

		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LoanType", loanType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Topuploantype FROM TopUpLoanTypeConfiguration");
		selectSql.append(" WHERE LoanType =:LoanType ");

		try {
			return jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
