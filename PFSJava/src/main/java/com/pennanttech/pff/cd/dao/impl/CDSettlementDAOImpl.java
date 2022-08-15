package com.pennanttech.pff.cd.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.cd.dao.CDSettlementDAO;
import com.pennanttech.pff.cd.model.CDSettlementProcess;
import com.pennanttech.pff.core.util.QueryUtil;

public class CDSettlementDAOImpl extends SequenceDao<CDSettlementProcess> implements CDSettlementDAO {
	private static Logger logger = LogManager.getLogger(CDSettlementDAOImpl.class);

	public CDSettlementDAOImpl() {
		super();
	}

	@Override
	public void saveSettlementProcessRequest(MapSqlParameterSource settlementMapdata) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert into Settlement_Request");
		sql.append("(RequestBatchId, SettlementRef, CustomerRef, EMIOffer, SubPayByManfacturer, SubvensionAmount");
		sql.append(", CustName, CustAddress, CustMobile, CustEmail, StoreName, StoreAddress, StoreCountry, StoreState");
		sql.append(", StoreCity, Issuer, Category, Description, Serial, Manufacturer, TransactionAmount, Acquirer");
		sql.append(
				", ManuFactureId, TerminalId, SettlementBatch, BankInvoice, AuthCode, HostReference, TransactionDateTime");
		sql.append(
				", SettlementDateTime, BillingInvoice, TransactionStatus, Reason, ProductCategory, ProductSubCategory1");
		sql.append(", ProductSubCategory2, ModelName, MaxValueOfProduct, MerchantName)");
		sql.append(" values");
		sql.append(
				"(:RequestBatchId, :SettlementRef, :CustomerRef, :EMIOffer, :SubPayByManfacturer, :SubvensionAmount");
		sql.append(
				", :CustName, :CustAddress, :CustMobile, :CustEmail, :StoreName, :StoreAddress, :StoreCountry, :StoreState");
		sql.append(
				", :StoreCity, :Issuer, :Category, :Description, :Serial, :Manufacturer, :TransactionAmount, :Acquirer");
		sql.append(
				", :ManufactureId, :TerminalId, :SettlementBatch, :BankInvoice, :AuthCode, :HostReference, :TransactionDateTime");
		sql.append(
				", :SettlementDateTime, :BillingInvoice, :TransactionStatus, :Reason, :ProductCategory, :ProductSubCategory1");
		sql.append(", :ProductSubCategory2, :ModelName, :MaxValueOfProduct, :MerchantName)");
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

		sql = QueryUtil.getCountQuery("SETTLEMENT_REQUEST", whereClause);

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
	public boolean isDuplicateSettlementRef(String SettlementRef) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "SettlementRef = :SettlementRef ";

		sql = QueryUtil.getCountQuery("SETTLEMENT_REQUEST", whereClause);

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("SettlementRef", SettlementRef);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
}
