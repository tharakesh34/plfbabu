package com.pennattech.pff.mmfl.cd.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.mmfl.cd.model.MerchantDetails;

public class MerchantDetailsDAOImpl extends SequenceDao<MerchantDetails> implements MerchantDetailsDAO {
	private static Logger logger = LogManager.getLogger(MerchantDetailsDAOImpl.class);

	public MerchantDetailsDAOImpl() {
		super();
	}

	@Override
	public MerchantDetails getMerchantDetails(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select MerchantId, MerchantName, StoreId, StoreName, StoreAddressLine1, StoreAddressLine2");
		if (type.contains("View")) {
			sql.append(" ");
		}
		sql.append(", StoreAddressLine3, StoreCity, StoreState, StoreCountry, CityName, StateName, CountryName, POSId");
		sql.append(", AvgTranPerMnth, AvgTranAmtPerMnth, TranAmtPerTran, TranAmtPerDay, AllowRefund, PeakTransPerDay");
		sql.append(", Channel, PinCode, Active, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CD_MERCHANTS");
		sql.append(type);
		sql.append(" Where MerchantId = :merchantId");

		MerchantDetails merchantDetails = new MerchantDetails();
		merchantDetails.setMerchantId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(merchantDetails);
		RowMapper<MerchantDetails> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MerchantDetails.class);

		try {
			merchantDetails = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return merchantDetails;
	}

	@Override
	public String save(MerchantDetails merchantDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("insert into CD_MERCHANTS");
		sql.append(tableType.getSuffix());
		sql.append("(MerchantId, MerchantName, StoreId, StoreName, StoreAddressLine1, StoreAddressLine2");
		sql.append(", StoreAddressLine3, StoreCity, StoreState, StoreCountry, CityName, StateName, CountryName, POSId");
		sql.append(", AvgTranPerMnth, AvgTranAmtPerMnth, TranAmtPerTran, TranAmtPerDay, AllowRefund, PeakTransPerDay");
		sql.append(", Channel, PinCode, Active, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append("(:merchantId, :merchantName, :storeId, :storeName, :storeAddressLine1, :storeAddressLine2");
		sql.append(
				", :storeAddressLine3, :storeCity, :storeState, :storeCountry, :cityName, :stateName, :countryName, :POSId");
		sql.append(
				", :avgTranPerMnth, :avgTranAmtPerMnth, :tranAmtPerTran, :tranAmtPerDay, :allowRefund, :peakTransPerDay");
		sql.append(", :channel, :pincode, :active, :Version , :LastMntBy, :LastMntOn, :RecordStatus");
		sql.append(", :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (merchantDetails.getMerchantId() == Long.MIN_VALUE) {
			merchantDetails.setMerchantId(getNextValue("SEQCD_MERCHANTS"));
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(merchantDetails);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(merchantDetails.getMerchantId());
	}

	@Override
	public void update(MerchantDetails merchantDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update CD_MERCHANTS");
		sql.append(tableType.getSuffix());
		sql.append(" set MerchantId = :merchantId, MerchantName = :MerchantName, StoreId = :StoreId");
		sql.append(", StoreName = :storeName, StoreAddressLine1 = :storeAddressLine1");
		sql.append(", StoreAddressLine2 = :storeAddressLine2, StoreAddressLine3 = :storeAddressLine3");
		sql.append(", StoreCity = :storeCity, StoreState = :storeState, StoreCountry = :storeCountry");
		sql.append(", cityName = :cityName, StateName = :stateName, CountryName = :countryName, POSId = :POSId");
		sql.append(", AvgTranPerMnth = :avgTranPerMnth, AvgTranAmtPerMnth = :avgTranAmtPerMnth");
		sql.append(", TranAmtPerTran = :tranAmtPerTran, TranAmtPerDay = :tranAmtPerDay, AllowRefund = :allowRefund");
		sql.append(
				", PeakTransPerDay = :peakTransPerDay, Channel = :channel, PinCode = :pincode, Active = :active, LastMntOn = :LastMntOn");
		sql.append(", RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordStatus = :RecordStatus, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where MerchantId = :merchantId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(merchantDetails);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(MerchantDetails merchantDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from CD_MERCHANTS");
		sql.append(tableType.getSuffix());
		sql.append(" where MerchantId = :merchantId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(merchantDetails);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(MerchantDetails merchantDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "StoreId = :storeId and PosId = :posId ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CD_MERCHANTS", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CD_MERCHANTS_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CD_MERCHANTS_Temp", "CD_MERCHANTS" }, whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("storeId", merchantDetails.getStoreId());
		paramSource.addValue("posId", merchantDetails.getPOSId());
		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public boolean isDuplicatePOSIdKey(MerchantDetails merchantDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "POSId = :posId ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CD_MERCHANTS", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CD_MERCHANTS_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CD_MERCHANTS_Temp", "CD_MERCHANTS" }, whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("posId", merchantDetails.getPOSId());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
}
