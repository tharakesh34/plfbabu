package com.pennattech.pff.cd.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.cd.model.MerchantDetails;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennattech.pff.cd.dao.MerchantDetailsDAO;

public class MerchantDetailsDAOImpl extends SequenceDao<MerchantDetails> implements MerchantDetailsDAO {
	private static Logger logger = LogManager.getLogger(MerchantDetailsDAOImpl.class);

	public MerchantDetailsDAOImpl() {
		super();
	}

	@Override
	public MerchantDetails getMerchantDetails(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MerchantId, MerchantName, StoreId, StoreName, StoreAddressLine1, StoreAddressLine2");
		sql.append(", StoreAddressLine3, StoreCity, StoreState, StoreCountry, CityName, StateName");
		sql.append(", CountryName, POSId, AvgTranPerMnth, AvgTranAmtPerMnth, TranAmtPerTran, TranAmtPerDay");
		sql.append(", AllowRefund, PeakTransPerDay, Channel, Pincode, MerchPAN, GstInNumber, MerchMobileNo");
		sql.append(", MerchEmailId, Active, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CD_MERCHANTS");
		sql.append(type);
		sql.append(" Where MerchantId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				MerchantDetails merch = new MerchantDetails();

				merch.setMerchantId(rs.getLong("MerchantId"));
				merch.setMerchantName(rs.getString("MerchantName"));
				merch.setStoreId(rs.getLong("StoreId"));
				merch.setStoreName(rs.getString("StoreName"));
				merch.setStoreAddressLine1(rs.getString("StoreAddressLine1"));
				merch.setStoreAddressLine2(rs.getString("StoreAddressLine2"));
				merch.setStoreAddressLine3(rs.getString("StoreAddressLine3"));
				merch.setStoreCity(rs.getString("StoreCity"));
				merch.setStoreState(rs.getString("StoreState"));
				merch.setStoreCountry(rs.getString("StoreCountry"));
				merch.setCityName(rs.getString("CityName"));
				merch.setStateName(rs.getString("StateName"));
				merch.setCountryName(rs.getString("CountryName"));
				merch.setPOSId(rs.getInt("POSId"));
				merch.setAvgTranPerMnth(rs.getBigDecimal("AvgTranPerMnth"));
				merch.setAvgTranAmtPerMnth(rs.getBigDecimal("AvgTranAmtPerMnth"));
				merch.setTranAmtPerTran(rs.getBigDecimal("TranAmtPerTran"));
				merch.setTranAmtPerDay(rs.getBigDecimal("TranAmtPerDay"));
				merch.setAllowRefund(rs.getBoolean("AllowRefund"));
				merch.setPeakTransPerDay(rs.getInt("PeakTransPerDay"));
				merch.setChannel(rs.getString("Channel"));
				merch.setPincode(rs.getString("Pincode"));
				merch.setMerchPAN(rs.getString("MerchPAN"));
				merch.setGstInNumber(rs.getString("GstInNumber"));
				merch.setMerchMobileNo(rs.getString("MerchMobileNo"));
				merch.setMerchEmailId(rs.getString("MerchEmailId"));
				merch.setActive(rs.getBoolean("Active"));
				merch.setVersion(rs.getInt("Version"));
				merch.setLastMntOn(rs.getTimestamp("LastMntOn"));
				merch.setLastMntBy(rs.getLong("LastMntBy"));
				merch.setRecordStatus(rs.getString("RecordStatus"));
				merch.setRoleCode(rs.getString("RoleCode"));
				merch.setNextRoleCode(rs.getString("NextRoleCode"));
				merch.setTaskId(rs.getString("TaskId"));
				merch.setNextTaskId(rs.getString("NextTaskId"));
				merch.setRecordType(rs.getString("RecordType"));
				merch.setWorkflowId(rs.getLong("WorkflowId"));

				return merch;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public String save(MerchantDetails merchantDetails, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into CD_MERCHANTS");
		sql.append(tableType.getSuffix());
		sql.append("(MerchantId, MerchantName, StoreId, StoreName, StoreAddressLine1, StoreAddressLine2");
		sql.append(", StoreAddressLine3, StoreCity, StoreState, StoreCountry, CityName, StateName, CountryName, POSId");
		sql.append(", AvgTranPerMnth, AvgTranAmtPerMnth, TranAmtPerTran, TranAmtPerDay, AllowRefund, PeakTransPerDay");
		sql.append(", Channel, PinCode, MerchPAN, GstInNumber, MerchMobileNo, MerchEmailId");
		sql.append(", Active, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (merchantDetails.getMerchantId() == Long.MIN_VALUE) {
			merchantDetails.setMerchantId(getNextValue("SEQCD_MERCHANTS"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, merchantDetails.getMerchantId());
				ps.setString(index++, merchantDetails.getMerchantName());
				ps.setLong(index++, merchantDetails.getStoreId());
				ps.setString(index++, merchantDetails.getStoreName());
				ps.setString(index++, merchantDetails.getStoreAddressLine1());
				ps.setString(index++, merchantDetails.getStoreAddressLine2());
				ps.setString(index++, merchantDetails.getStoreAddressLine3());
				ps.setString(index++, merchantDetails.getStoreCity());
				ps.setString(index++, merchantDetails.getStoreState());
				ps.setString(index++, merchantDetails.getStoreCountry());
				ps.setString(index++, merchantDetails.getCityName());
				ps.setString(index++, merchantDetails.getStateName());
				ps.setString(index++, merchantDetails.getCountryName());
				ps.setLong(index++, merchantDetails.getPOSId());
				ps.setBigDecimal(index++, merchantDetails.getAvgTranPerMnth());
				ps.setBigDecimal(index++, merchantDetails.getAvgTranAmtPerMnth());
				ps.setBigDecimal(index++, merchantDetails.getTranAmtPerTran());
				ps.setBigDecimal(index++, merchantDetails.getTranAmtPerDay());
				ps.setBoolean(index++, merchantDetails.isAllowRefund());
				ps.setInt(index++, merchantDetails.getPeakTransPerDay());
				ps.setString(index++, merchantDetails.getChannel());
				ps.setString(index++, merchantDetails.getPincode());
				ps.setString(index++, merchantDetails.getMerchPAN());
				ps.setString(index++, merchantDetails.getGstInNumber());
				ps.setString(index++, merchantDetails.getMerchMobileNo());
				ps.setString(index++, merchantDetails.getMerchEmailId());
				ps.setBoolean(index++, merchantDetails.isActive());
				ps.setInt(index++, merchantDetails.getVersion());
				ps.setLong(index++, merchantDetails.getLastMntBy());
				ps.setTimestamp(index++, merchantDetails.getLastMntOn());
				ps.setString(index++, merchantDetails.getRecordStatus());
				ps.setString(index++, merchantDetails.getRoleCode());
				ps.setString(index++, merchantDetails.getNextRoleCode());
				ps.setString(index++, merchantDetails.getTaskId());
				ps.setString(index++, merchantDetails.getNextTaskId());
				ps.setString(index++, merchantDetails.getRecordType());
				ps.setLong(index, merchantDetails.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(merchantDetails.getMerchantId());
	}

	@Override
	public void update(MerchantDetails merchantDetails, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update CD_MERCHANTS");
		sql.append(tableType.getSuffix());
		sql.append(" set MerchantName = ?, StoreId = ?, StoreName = ?, StoreAddressLine1 = ?");
		sql.append(", StoreAddressLine2 = ?, StoreAddressLine3 = ?, StoreCity = ?, StoreState = ?, StoreCountry = ?");
		sql.append(", CityName = ?, StateName = ?, CountryName = ?, POSId = ?, MerchPAN = ?, GstInNumber = ?");
		sql.append(", MerchMobileNo = ?, MerchEmailId = ?, AvgTranPerMnth = ?, AvgTranAmtPerMnth = ?");
		sql.append(", TranAmtPerTran = ?, TranAmtPerDay = ?, AllowRefund = ?, PeakTransPerDay = ?, Channel = ?");
		sql.append(", PinCode = ?, Active = ?, Version = ?, LastMntBy = ?, LastMntOn = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordStatus = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where MerchantId = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, merchantDetails.getMerchantName());
			ps.setLong(index++, merchantDetails.getStoreId());
			ps.setString(index++, merchantDetails.getStoreName());
			ps.setString(index++, merchantDetails.getStoreAddressLine1());
			ps.setString(index++, merchantDetails.getStoreAddressLine2());
			ps.setString(index++, merchantDetails.getStoreAddressLine3());
			ps.setString(index++, merchantDetails.getStoreCity());
			ps.setString(index++, merchantDetails.getStoreState());
			ps.setString(index++, merchantDetails.getStoreCountry());
			ps.setString(index++, merchantDetails.getCityName());
			ps.setString(index++, merchantDetails.getStateName());
			ps.setString(index++, merchantDetails.getCountryName());
			ps.setLong(index++, merchantDetails.getPOSId());
			ps.setString(index++, merchantDetails.getMerchPAN());
			ps.setString(index++, merchantDetails.getGstInNumber());
			ps.setString(index++, merchantDetails.getMerchMobileNo());
			ps.setString(index++, merchantDetails.getMerchEmailId());
			ps.setBigDecimal(index++, merchantDetails.getAvgTranPerMnth());
			ps.setBigDecimal(index++, merchantDetails.getAvgTranAmtPerMnth());
			ps.setBigDecimal(index++, merchantDetails.getTranAmtPerTran());
			ps.setBigDecimal(index++, merchantDetails.getTranAmtPerDay());
			ps.setBoolean(index++, merchantDetails.isAllowRefund());
			ps.setInt(index++, merchantDetails.getPeakTransPerDay());
			ps.setString(index++, merchantDetails.getChannel());
			ps.setString(index++, merchantDetails.getPincode());
			ps.setBoolean(index++, merchantDetails.isActive());
			ps.setInt(index++, merchantDetails.getVersion());
			ps.setLong(index++, merchantDetails.getLastMntBy());
			ps.setTimestamp(index++, merchantDetails.getLastMntOn());
			ps.setString(index++, merchantDetails.getRoleCode());
			ps.setString(index++, merchantDetails.getNextRoleCode());
			ps.setString(index++, merchantDetails.getTaskId());
			ps.setString(index++, merchantDetails.getNextTaskId());
			ps.setString(index++, merchantDetails.getRecordStatus());
			ps.setString(index++, merchantDetails.getRecordType());
			ps.setLong(index++, merchantDetails.getWorkflowId());

			ps.setLong(index++, merchantDetails.getMerchantId());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index, merchantDetails.getPrevMntOn());
			} else {
				ps.setInt(index, merchantDetails.getVersion() - 1);
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(MerchantDetails merchantDetails, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from CD_MERCHANTS");
		sql.append(tableType.getSuffix());
		sql.append(" Where MerchantId = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());
		int recordCount = 0;

		try {
			recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				ps.setLong(index++, merchantDetails.getMerchantId());

				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(index, merchantDetails.getPrevMntOn());
				} else {
					ps.setInt(index, merchantDetails.getVersion() - 1);
				}
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public boolean isDuplicateKey(MerchantDetails md, TableType tableType) {
		String sql;
		String whereClause = "StoreId = ? and PosId = ?";
		Object[] obj = new Object[] { md.getStoreId(), md.getPOSId() };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CD_MERCHANTS", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CD_MERCHANTS_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CD_MERCHANTS_Temp", "CD_MERCHANTS" }, whereClause);
			obj = new Object[] { md.getStoreId(), md.getPOSId(), md.getStoreId(), md.getPOSId() };
			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public boolean isDuplicatePOSIdKey(MerchantDetails md, TableType tableType) {
		String sql;
		String whereClause = "PosId = ?";
		Object[] obj = new Object[] { md.getStoreId(), md.getPOSId() };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CD_MERCHANTS", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CD_MERCHANTS_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CD_MERCHANTS_Temp", "CD_MERCHANTS" }, whereClause);
			obj = new Object[] { md.getPOSId(), md.getPOSId() };
			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	public Map<String, Object> getGSTDataMapForMerch(long mId) {
		Map<String, Object> map = new HashMap<>();

		String sql = "Select StoreCity, StoreState, StoreCountry From CD_Merchants Where storeId = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			map.put("CustBranch", rs.getString("StoreCity"));
			map.put("CustProvince", rs.getString("StoreState"));
			map.put("CustCountry", rs.getString("StoreCountry"));

			return map;
		}, mId);

		return map;
	}

	@Override
	public MerchantDetails getDetails(String mId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" StoreName, GstInNumber, StoreState, StoreCity, StoreCountry");
		sql.append(", StoreAddressLine1, StoreAddressLine2, StoreAddressLine3, MerchPAN");
		sql.append(" from CD_MERCHANTS");
		sql.append(" Where StoreId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				MerchantDetails merch = new MerchantDetails();

				merch.setStoreName(rs.getString("StoreName"));
				merch.setStoreState(rs.getString("StoreState"));
				merch.setGstInNumber(rs.getString("GstInNumber"));
				merch.setStoreCountry(rs.getString("StoreCountry"));
				merch.setStoreAddressLine1(rs.getString("StoreAddressLine1"));
				merch.setStoreAddressLine2(rs.getString("StoreAddressLine2"));
				merch.setStoreAddressLine3(rs.getString("StoreAddressLine3"));
				merch.setMerchPAN(rs.getString("MerchPAN"));

				return merch;
			}, mId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}
}
