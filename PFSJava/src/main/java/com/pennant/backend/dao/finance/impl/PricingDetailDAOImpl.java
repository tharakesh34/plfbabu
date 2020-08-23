package com.pennant.backend.dao.finance.impl;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.finance.PricingDetailDAO;
import com.pennant.backend.model.finance.PricingDetail;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class PricingDetailDAOImpl extends SequenceDao<PricingDetail> implements PricingDetailDAO {

	private static Logger logger = Logger.getLogger(PricingDetailDAOImpl.class);

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
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}
}
