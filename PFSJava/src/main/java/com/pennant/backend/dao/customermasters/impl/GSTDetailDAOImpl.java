/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : GSTDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified
 * Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.customermasters.GSTDetailDAO;
import com.pennant.backend.model.customermasters.GSTDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class GSTDetailDAOImpl extends SequenceDao<GSTDetail> implements GSTDetailDAO {
	private static Logger logger = LogManager.getLogger(GSTDetailDAOImpl.class);

	public GSTDetailDAOImpl() {
		super();
	}

	@Override
	public GSTDetail getGSTDetailByID(final long id, String typeCode, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, CustID, GstNumber, Address, AddressLine1, AddressLine2, AddressLine3, AddressLine4");
		sql.append(", CityCode, StateCode, CountryCode, PinCode, PinCodeId");
		sql.append(", Tin, TinName, TinAddress, DefaultGST");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CountryName, StateName, CityName, PinCodeName");
		}

		sql.append(" From Gst_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				GSTDetail gst = new GSTDetail();

				gst.setId(rs.getLong("Id"));
				gst.setCustID(rs.getLong("CustID"));
				gst.setGstNumber(rs.getString("GstNumber"));
				gst.setAddress(rs.getString("Address"));
				gst.setAddressLine1(rs.getString("AddressLine1"));
				gst.setAddressLine2(rs.getString("AddressLine2"));
				gst.setAddressLine3(rs.getString("AddressLine3"));
				gst.setAddressLine4(rs.getString("AddressLine4"));
				gst.setCityCode(rs.getString("CityCode"));
				gst.setStateCode(rs.getString("StateCode"));
				gst.setCountryCode(rs.getString("CountryCode"));
				gst.setPinCode(rs.getString("PinCode"));
				gst.setPinCodeId(rs.getLong("PinCodeId"));
				gst.setTin(rs.getBoolean("Tin"));
				gst.setTinName(rs.getBoolean("TinName"));
				gst.setTinAddress(rs.getBoolean("TinAddress"));
				gst.setDefaultGST(rs.getBoolean("DefaultGST"));
				gst.setVersion(rs.getInt("Version"));
				gst.setLastMntBy(rs.getLong("LastMntBy"));
				gst.setLastMntOn(rs.getTimestamp("LastMntOn"));
				gst.setRecordStatus(rs.getString("RecordStatus"));
				gst.setRoleCode(rs.getString("RoleCode"));
				gst.setNextRoleCode(rs.getString("NextRoleCode"));
				gst.setTaskId(rs.getString("TaskId"));
				gst.setNextTaskId(rs.getString("NextTaskId"));
				gst.setRecordType(rs.getString("RecordType"));
				gst.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					gst.setCountryName(rs.getString("CountryName"));
					gst.setCityName(rs.getString("CityName"));
					gst.setStateName(rs.getString("StateName"));
					gst.setPinCodeName(rs.getString("PinCodeName"));
				}

				return gst;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(GSTDetail gstDetail, String type) {
		StringBuilder sql = new StringBuilder("Delete From Gst_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ? and CustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				ps.setLong(1, gstDetail.getId());
				ps.setLong(2, gstDetail.getCustID());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(GSTDetail gst, String type) {
		StringBuilder sql = new StringBuilder("Insert Into Gst_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Id, CustID, GstNumber, Address, AddressLine1, AddressLine2, AddressLine3, AddressLine4");
		sql.append(", CityCode, StateCode, CountryCode, PinCode, PinCodeId, Tin, TinAddress, TinName, DefaultGST");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			if (gst.getId() == Long.MIN_VALUE) {
				gst.setId(getNextValue("SeqGst_Details"));
			}

			ps.setLong(index++, gst.getId());
			ps.setLong(index++, gst.getCustID());
			ps.setString(index++, gst.getGstNumber());
			ps.setString(index++, gst.getAddress());
			ps.setString(index++, gst.getAddressLine1());
			ps.setString(index++, gst.getAddressLine2());
			ps.setString(index++, gst.getAddressLine3());
			ps.setString(index++, gst.getAddressLine4());
			ps.setString(index++, gst.getCityCode());
			ps.setString(index++, gst.getStateCode());
			ps.setString(index++, gst.getCountryCode());
			ps.setString(index++, gst.getPinCode());
			ps.setObject(index++, gst.getPinCodeId());
			ps.setBoolean(index++, gst.isTin());
			ps.setBoolean(index++, gst.isTinAddress());
			ps.setBoolean(index++, gst.isTinName());
			ps.setBoolean(index++, gst.isDefaultGST());
			ps.setInt(index++, gst.getVersion());
			ps.setLong(index++, gst.getLastMntBy());
			ps.setTimestamp(index++, gst.getLastMntOn());
			ps.setString(index++, gst.getRecordStatus());
			ps.setString(index++, gst.getRoleCode());
			ps.setString(index++, gst.getNextRoleCode());
			ps.setString(index++, gst.getTaskId());
			ps.setString(index++, gst.getNextTaskId());
			ps.setString(index++, gst.getRecordType());
			ps.setLong(index, gst.getWorkflowId());
		});

		return gst.getId();
	}

	@Override
	public void update(GSTDetail gst, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Update Gst_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set GstNumber = ?");
		sql.append(", Address = ?, AddressLine1 = ?, AddressLine2 = ?, AddressLine3 = ?, AddressLine4 = ?");
		sql.append(", CityCode = ?, StateCode = ?, CountryCode = ?, PinCode = ?, PinCodeId = ?");
		sql.append(", Tin = ?, TinAddress = ?, TinName = ?, DefaultGST = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where  Id = ? and CustID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, gst.getGstNumber());
			ps.setString(index++, gst.getAddress());
			ps.setString(index++, gst.getAddressLine1());
			ps.setString(index++, gst.getAddressLine2());
			ps.setString(index++, gst.getAddressLine3());
			ps.setString(index++, gst.getAddressLine4());
			ps.setString(index++, gst.getCityCode());
			ps.setString(index++, gst.getStateCode());
			ps.setString(index++, gst.getCountryCode());
			ps.setString(index++, gst.getPinCode());
			ps.setObject(index++, gst.getPinCodeId());
			ps.setBoolean(index++, gst.isTin());
			ps.setBoolean(index++, gst.isTinAddress());
			ps.setBoolean(index++, gst.isTinName());
			ps.setBoolean(index++, gst.isDefaultGST());
			ps.setInt(index++, gst.getVersion());
			ps.setLong(index++, gst.getLastMntBy());
			ps.setTimestamp(index++, gst.getLastMntOn());
			ps.setString(index++, gst.getRecordStatus());
			ps.setString(index++, gst.getRoleCode());
			ps.setString(index++, gst.getNextRoleCode());
			ps.setString(index++, gst.getTaskId());
			ps.setString(index++, gst.getNextTaskId());
			ps.setString(index++, gst.getRecordType());
			ps.setLong(index++, gst.getWorkflowId());

			ps.setLong(index++, gst.getId());
			ps.setLong(index++, gst.getCustID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, gst.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	public void deleteByCustomer(final long id, String type) {
		StringBuilder sql = new StringBuilder("Delete From Gst_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, id));
	}

	@Override
	public List<GSTDetail> getGSTDetailById(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.Id, T1.CustID, T1.GstNumber");
		sql.append(", T1.Address, T1.AddressLine1, T1.AddressLine2, T1.AddressLine3, T1.AddressLine4");
		sql.append(", T1.CityCode, T1.StateCode, T1.CountryCode, T1.PinCode, T1.PinCodeId");
		sql.append(", T1.Tin, T1.TinName, T1.TinAddress, T1.DefaultGST");
		sql.append(", T1.Version, T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode");
		sql.append(", T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId");
		sql.append(", T5.CustShrtName, T5.CustCIF");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", T1.CityName, T1.StateName, T1.CountryName, T1.PinCodeName");
		}

		sql.append(" from Gst_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" T1 Left Join Customers T5 ON T1.CustID = T5.CustID");
		sql.append(" Where T1.CustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, id), (rs, rowNum) -> {
			GSTDetail gst = new GSTDetail();

			gst.setId(rs.getLong("Id"));
			gst.setCustID(rs.getLong("CustID"));
			gst.setGstNumber(rs.getString("GstNumber"));
			gst.setAddress(rs.getString("Address"));
			gst.setAddressLine1(rs.getString("AddressLine1"));
			gst.setAddressLine2(rs.getString("AddressLine2"));
			gst.setAddressLine3(rs.getString("AddressLine3"));
			gst.setAddressLine4(rs.getString("AddressLine4"));
			gst.setCityCode(rs.getString("CityCode"));
			gst.setStateCode(rs.getString("StateCode"));
			gst.setCountryCode(rs.getString("CountryCode"));
			gst.setPinCode(rs.getString("PinCode"));
			gst.setPinCodeId(rs.getLong("PinCodeId"));
			gst.setTin(rs.getBoolean("Tin"));
			gst.setTinName(rs.getBoolean("TinName"));
			gst.setTinAddress(rs.getBoolean("TinAddress"));
			gst.setDefaultGST(rs.getBoolean("DefaultGST"));
			gst.setVersion(rs.getInt("Version"));
			gst.setLastMntBy(rs.getLong("LastMntBy"));
			gst.setLastMntOn(rs.getTimestamp("LastMntOn"));
			gst.setRecordStatus(rs.getString("RecordStatus"));
			gst.setRoleCode(rs.getString("RoleCode"));
			gst.setNextRoleCode(rs.getString("NextRoleCode"));
			gst.setTaskId(rs.getString("TaskId"));
			gst.setNextTaskId(rs.getString("NextTaskId"));
			gst.setRecordType(rs.getString("RecordType"));
			gst.setWorkflowId(rs.getLong("WorkflowId"));
			gst.setCustShrtName(rs.getString("CustShrtName"));
			gst.setCustCIF(rs.getString("CustCIF"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				gst.setCityName(rs.getString("CityName"));
				gst.setStateName(rs.getString("StateName"));
				gst.setCountryName(rs.getString("CountryName"));
				gst.setPinCodeName(rs.getString("PinCodeName"));
			}

			return gst;
		});
	}

	@Override
	public GSTDetail getDefaultGSTDetailById(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.CustID, T1.GstNumber");
		sql.append(", T1.Address, T1.AddressLine1, T1.AddressLine2, T1.AddressLine3, T1.AddressLine4");
		sql.append(", T1.CityCode, T1.StateCode, T1.CountryCode, T1.PinCode, T5.CustShrtName, T5.CustCIF");
		sql.append(" From Gst_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" T1 Left Join Customers T5 on T1.CustID = T5.CustID");
		sql.append(" Where T1.CustID = ? and T1.DefaultGST = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				GSTDetail gst = new GSTDetail();

				gst.setCustID(rs.getLong("CustID"));
				gst.setGstNumber(rs.getString("GstNumber"));
				gst.setAddress(rs.getString("Address"));
				gst.setAddressLine1(rs.getString("AddressLine1"));
				gst.setAddressLine2(rs.getString("AddressLine2"));
				gst.setAddressLine3(rs.getString("AddressLine3"));
				gst.setAddressLine4(rs.getString("AddressLine4"));
				gst.setCityCode(rs.getString("CityCode"));
				gst.setStateCode(rs.getString("StateCode"));
				gst.setCountryCode(rs.getString("CountryCode"));
				gst.setPinCode(rs.getString("PinCode"));
				gst.setCustShrtName(rs.getString("CustShrtName"));
				gst.setCustCIF(rs.getString("CustCIF"));

				return gst;
			}, id, true);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(GSTDetail gstDetail, TableType tableType) {
		long custID = gstDetail.getCustID();
		String gstState = gstDetail.getStateCode();

		String whereClause = "CustID = ? and StateCode = ?";

		Object[] obj = new Object[] { custID, gstState };

		String sql;
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Gst_Details", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Gst_Details_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Gst_Details_Temp", "Gst_Details" }, whereClause);

			obj = new Object[] { custID, gstState, custID, gstState };

			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}
}