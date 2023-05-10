package com.pennanttech.pff.subvention.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.subventionprocess.model.SubventionProcess;

public class SubventionProcessDAOImpl extends SequenceDao<SubventionProcess> implements SubventionProcessDAO {
	private static Logger logger = LogManager.getLogger(SubventionProcessDAOImpl.class);

	public SubventionProcessDAOImpl() {
		super();
	}

	@Override
	public void saveSubventionProcessRequest(MapSqlParameterSource settlementMapdata) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Insert into Subvention_Request");
		sql.append(" ( Hostreference, Issuer, Acquirer, Merchantusername, Manufacturername, Storename, Storecity");
		sql.append(", Storestate, Manufactureid, Terminalid, Emioffer, Rrn, Bankapprovalcode, Transactiondatetime");
		sql.append(", Settlementdatetime, Transactionamount, Txnstatus, Productcategory, Subcat1, Subcat2, Subcat3 ");
		sql.append(", Productsrno, cardhash, Emimodel, Posid, Discountrate, Discountamount");
		sql.append(", Cashbackrate, Cashbackamount, Nbfccashbackrate, Nbfccashbackamount, LinkedTranId ) ");
		sql.append(" values");
		sql.append(
				" ( :HostReference, :Issuer, :Acquirer, :Merchantusername, :Manufacturername, :Storename, :Storecity");
		sql.append(
				", :Storestate, :Manufactureid, :Terminalid, :Emioffer, :Rrn, :Bankapprovalcode, :Transactiondatetime");
		sql.append(
				", :Settlementdatetime, :Transactionamount, :Txnstatus, :Productcategory, :Subcat1, :Subcat2, :Subcat3");
		sql.append(", :Productsrno, :Cardhash, :Emimodel, :Posid, :Discountrate, :Discountamount");
		sql.append(", :Cashbackrate, :Cashbackamount, :Nbfccashbackrate, :Nbfccashbackamount, :LinkedTranId ) ");

		logger.trace(Literal.SQL + sql.toString());
		try {
			jdbcTemplate.update(sql.toString(), settlementMapdata);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateHostReference(String hostReference) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "HOSTREFERENCE = :HostReference ";

		sql = QueryUtil.getCountQuery("SUBVENTION_REQUEST", whereClause);

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("HostReference", hostReference);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public long getLinkedTranIdByHostReference(String hostReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select LinkedTranId from SUBVENTION_REQUEST");
		sql.append("  WHERE HOSTREFERENCE = :HOSTREFERENCE");
		logger.debug(Literal.SQL + sql.toString());

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("HOSTREFERENCE", hostReference);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), paramMap, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}
}
