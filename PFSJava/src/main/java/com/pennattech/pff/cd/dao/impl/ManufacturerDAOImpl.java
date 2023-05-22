package com.pennattech.pff.cd.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.cd.model.Manufacturer;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennattech.pff.cd.dao.ManufacturerDAO;

public class ManufacturerDAOImpl extends SequenceDao<Manufacturer> implements ManufacturerDAO {
	private static Logger logger = LogManager.getLogger(ManufacturerDAOImpl.class);

	public ManufacturerDAOImpl() {
		super();
	}

	@Override
	public Manufacturer getManufacturer(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ManufacturerId, Name, Description, Channel, Active, AddressLine1, AddressLine2");
		sql.append(", AddressLine3, City, State, Country, PinCodeId, Version, LastMntOn, LastMntBy");
		sql.append(", ManufacPAN, GstInNumber, ManfMobileNo, ManfEmailId, ManfacContactName, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCityName, LovDescStateName, LovDescCountryName, PinAreaDesc, PinCode");
		}
		sql.append(" From CD_MANUFACTURERS");
		sql.append(type);
		sql.append(" Where ManufacturerId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Manufacturer manf = new Manufacturer();

				manf.setManufacturerId(rs.getLong("ManufacturerId"));
				manf.setName(rs.getString("Name"));
				manf.setDescription(rs.getString("Description"));
				manf.setChannel(rs.getString("Channel"));
				manf.setActive(rs.getBoolean("Active"));
				manf.setAddressLine1(rs.getString("AddressLine1"));
				manf.setAddressLine2(rs.getString("AddressLine2"));
				manf.setAddressLine3(rs.getString("AddressLine3"));
				manf.setCity(rs.getString("City"));
				manf.setState(rs.getString("State"));
				manf.setCountry(rs.getString("Country"));
				manf.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));
				manf.setVersion(rs.getInt("Version"));
				manf.setLastMntOn(rs.getTimestamp("LastMntOn"));
				manf.setLastMntBy(rs.getLong("LastMntBy"));
				manf.setManufacPAN(rs.getString("ManufacPAN"));
				manf.setGstInNumber(rs.getString("GstInNumber"));
				manf.setManfMobileNo(rs.getString("ManfMobileNo"));
				manf.setManfEmailId(rs.getString("ManfEmailId"));
				manf.setManfacContactName(rs.getString("ManfacContactName"));
				manf.setRecordStatus(rs.getString("RecordStatus"));
				manf.setRoleCode(rs.getString("RoleCode"));
				manf.setNextRoleCode(rs.getString("NextRoleCode"));
				manf.setTaskId(rs.getString("TaskId"));
				manf.setNextTaskId(rs.getString("NextTaskId"));
				manf.setRecordType(rs.getString("RecordType"));
				manf.setWorkflowId(rs.getLong("WorkflowId"));
				if (StringUtils.trimToEmpty(type).contains("View")) {
					manf.setLovDescCityName(rs.getString("LovDescCityName"));
					manf.setLovDescStateName(rs.getString("LovDescStateName"));
					manf.setLovDescCountryName(rs.getString("LovDescCountryName"));
					manf.setPinAreaDesc(rs.getString("PinAreaDesc"));
					manf.setPinCode(rs.getString("PinCode"));
				}

				return manf;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public String save(Manufacturer manufacturer, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into CD_MANUFACTURERS");
		sql.append(tableType.getSuffix());
		sql.append(" (ManufacturerId, Name, Description, Channel");
		sql.append(", AddressLine1, AddressLine2, AddressLine3, City, State, Country, PinCodeId");
		sql.append(", ManufacPAN, GstInNumber, ManfMobileNo, ManfEmailId, ManfacContactName");
		sql.append(", Active, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values");
		sql.append("( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (manufacturer.getManufacturerId() == Long.MIN_VALUE) {
			manufacturer.setManufacturerId(getNextValue("SEQCD_MANUFACTURERS"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, manufacturer.getManufacturerId());
				ps.setString(index++, manufacturer.getName());
				ps.setString(index++, manufacturer.getDescription());
				ps.setString(index++, manufacturer.getChannel());
				ps.setBoolean(index++, manufacturer.isActive());
				ps.setString(index++, manufacturer.getAddressLine1());
				ps.setString(index++, manufacturer.getAddressLine2());
				ps.setString(index++, manufacturer.getAddressLine3());
				ps.setString(index++, manufacturer.getCity());
				ps.setString(index++, manufacturer.getState());
				ps.setString(index++, manufacturer.getCountry());
				ps.setLong(index++, manufacturer.getPinCodeId());
				ps.setString(index++, manufacturer.getManufacPAN());
				ps.setString(index++, manufacturer.getGstInNumber());
				ps.setString(index++, manufacturer.getManfMobileNo());
				ps.setString(index++, manufacturer.getManfEmailId());
				ps.setString(index++, manufacturer.getManfacContactName());
				ps.setTimestamp(index++, manufacturer.getLastMntOn());
				ps.setString(index++, manufacturer.getRecordStatus());
				ps.setString(index++, manufacturer.getRoleCode());
				ps.setString(index++, manufacturer.getNextRoleCode());
				ps.setString(index++, manufacturer.getTaskId());
				ps.setString(index++, manufacturer.getNextTaskId());
				ps.setString(index++, manufacturer.getRecordType());
				ps.setLong(index, manufacturer.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(manufacturer.getManufacturerId());
	}

	@Override
	public void update(Manufacturer manufacturer, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update CD_MANUFACTURERS");
		sql.append(tableType.getSuffix());
		sql.append(" set Name = ?, Description = ?, Channel = ?, Active = ?");
		sql.append(", AddressLine1 = ?, AddressLine2 = ?, AddressLine3 = ?");
		sql.append(", City = ?, State = ?, Country = ?, PinCodeId = ?");
		sql.append(", ManufacPAN = ?, GstInNumber = ?, ManfMobileNo = ?");
		sql.append(", ManfEmailId = ?, ManfacContactName = ?, LastMntOn = ?");
		sql.append(", RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?,  NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ManufacturerId = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, manufacturer.getName());
			ps.setString(index++, manufacturer.getDescription());
			ps.setString(index++, manufacturer.getChannel());
			ps.setBoolean(index++, manufacturer.isActive());
			ps.setString(index++, manufacturer.getAddressLine1());
			ps.setString(index++, manufacturer.getAddressLine2());
			ps.setString(index++, manufacturer.getAddressLine3());
			ps.setString(index++, manufacturer.getCity());
			ps.setString(index++, manufacturer.getState());
			ps.setString(index++, manufacturer.getCountry());
			ps.setLong(index++, manufacturer.getPinCodeId());
			ps.setString(index++, manufacturer.getManufacPAN());
			ps.setString(index++, manufacturer.getGstInNumber());
			ps.setString(index++, manufacturer.getManfMobileNo());
			ps.setString(index++, manufacturer.getManfEmailId());
			ps.setString(index++, manufacturer.getManfacContactName());
			ps.setTimestamp(index++, manufacturer.getLastMntOn());
			ps.setString(index++, manufacturer.getRecordStatus());
			ps.setString(index++, manufacturer.getRoleCode());
			ps.setString(index++, manufacturer.getNextRoleCode());
			ps.setString(index++, manufacturer.getTaskId());
			ps.setString(index++, manufacturer.getNextTaskId());
			ps.setString(index++, manufacturer.getRecordType());
			ps.setLong(index++, manufacturer.getWorkflowId());

			ps.setLong(index++, manufacturer.getManufacturerId());
			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index, manufacturer.getPrevMntOn());
			} else {
				ps.setInt(index, manufacturer.getVersion() - 1);
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(Manufacturer manufacturer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from CD_MANUFACTURERS");
		sql.append(tableType.getSuffix());
		sql.append(" where ManufacturerId = :manufacturerId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manufacturer);
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
	public boolean isDuplicateKey(Manufacturer manufacturer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "ManufacturerId = :manufacturerId ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CD_MANUFACTURERS", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CD_MANUFACTURERS_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CD_MANUFACTURERS_Temp", "CD_MANUFACTURERS" }, whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("manufacturerId", manufacturer.getManufacturerId());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	public Map<String, Object> getGSTDataMapForManufac(long oEMID) {
		Map<String, Object> map = new HashMap<>();
		String sql = "Select City, State, Country From CD_Manufacturers Where ManufacturerId = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.query(sql, (rs, rowNum) -> {
			map.put("CustBranch", rs.getString("City"));
			map.put("CustProvince", rs.getString("State"));
			map.put("CustCountry", rs.getString("Country"));
			return map;
		}, oEMID);

		return map;
	}

	@Override
	public Manufacturer getDetails(long oEMID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Name, Description, State, GstInNumber, Country");
		sql.append(", ManufacPAN, AddressLine1, AddressLine2, AddressLine3");
		sql.append(" from CD_MANUFACTURERS");
		sql.append(" Where ManufacturerId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Manufacturer manf = new Manufacturer();

				manf.setName(rs.getString("Name"));
				manf.setDescription(rs.getString("Description"));
				manf.setState(rs.getString("State"));
				manf.setGstInNumber(rs.getString("GstInNumber"));
				manf.setManufacPAN(rs.getString("ManufacPAN"));
				manf.setAddressLine1(rs.getString("AddressLine1"));
				manf.setAddressLine2(rs.getString("AddressLine2"));
				manf.setAddressLine3(rs.getString("AddressLine3"));
				manf.setCountry(rs.getString("Country"));

				return manf;
			}, oEMID);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}
}
