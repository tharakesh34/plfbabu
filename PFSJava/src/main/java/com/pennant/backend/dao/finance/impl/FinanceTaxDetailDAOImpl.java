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
 * * FileName : FinanceTaxDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-06-2017 * *
 * Modified Date : 17-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>FinanceTaxDetail</code> with set of CRUD operations.
 */
public class FinanceTaxDetailDAOImpl extends BasicDao<FinanceTaxDetail> implements FinanceTaxDetailDAO {
	private static Logger logger = LogManager.getLogger(FinanceTaxDetailDAOImpl.class);

	public FinanceTaxDetailDAOImpl() {
		super();
	}

	@Override
	public FinanceTaxDetail getFinanceTaxDetail(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, ApplicableFor, TaxCustId, TaxExempted, TaxNumber, AddrLine1, AddrLine2");
		sql.append(", AddrLine3, AddrLine4, Country, Province, City, PinCode, SezCertificateNo, SezValueDate");
		sql.append(", AddressDetail");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CountryName, ProvinceName, CityName, PinCodeName, CustCIF, CustShrtName");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId, PinCodeId");
		sql.append(" from FinTaxDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceTaxDetail td = new FinanceTaxDetail();

				td.setFinID(rs.getLong("FinID"));
				td.setFinReference(rs.getString("FinReference"));
				td.setApplicableFor(rs.getString("ApplicableFor"));
				td.setTaxCustId(rs.getLong("TaxCustId"));
				td.setTaxExempted(rs.getBoolean("TaxExempted"));
				td.setTaxNumber(rs.getString("TaxNumber"));
				td.setAddrLine1(rs.getString("AddrLine1"));
				td.setAddrLine2(rs.getString("AddrLine2"));
				td.setAddrLine3(rs.getString("AddrLine3"));
				td.setAddrLine4(rs.getString("AddrLine4"));
				td.setCountry(rs.getString("Country"));
				td.setProvince(rs.getString("Province"));
				td.setCity(rs.getString("City"));
				td.setPinCode(rs.getString("PinCode"));
				td.setSezCertificateNo(rs.getString("SezCertificateNo"));
				td.setSezValueDate(rs.getTimestamp("SezValueDate"));
				td.setAddressDetail(rs.getString("AddressDetail"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					td.setCountryName(rs.getString("CountryName"));
					td.setProvinceName(rs.getString("ProvinceName"));
					td.setCityName(rs.getString("CityName"));
					td.setPinCodeName(rs.getString("PinCodeName"));
					td.setCustCIF(rs.getString("CustCIF"));
					td.setCustShrtName(rs.getString("CustShrtName"));
				}

				td.setVersion(rs.getInt("Version"));
				td.setLastMntOn(rs.getTimestamp("LastMntOn"));
				td.setLastMntBy(rs.getLong("LastMntBy"));
				td.setRecordStatus(rs.getString("RecordStatus"));
				td.setRoleCode(rs.getString("RoleCode"));
				td.setNextRoleCode(rs.getString("NextRoleCode"));
				td.setTaskId(rs.getString("TaskId"));
				td.setNextTaskId(rs.getString("NextTaskId"));
				td.setRecordType(rs.getString("RecordType"));
				td.setWorkflowId(rs.getLong("WorkflowId"));
				td.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));

				return td;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public String save(FinanceTaxDetail td, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into FinTaxDetail");
		sql.append(tableType.getSuffix());
		sql.append("(FinID, FinReference, ApplicableFor, TaxCustId, TaxExempted, TaxNumber, AddrLine1, AddrLine2");
		sql.append(", AddrLine3, AddrLine4, Country, Province, City, PinCodeId, PinCode");
		sql.append(", SezCertificateNo, SezValueDate, AddressDetail");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values");
		sql.append(" (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, td.getFinID());
				ps.setString(index++, td.getFinReference());
				ps.setString(index++, td.getApplicableFor());
				ps.setLong(index++, td.getTaxCustId());
				ps.setBoolean(index++, td.isTaxExempted());
				ps.setString(index++, td.getTaxNumber());
				ps.setString(index++, td.getAddrLine1());
				ps.setString(index++, td.getAddrLine2());
				ps.setString(index++, td.getAddrLine3());
				ps.setString(index++, td.getAddrLine4());
				ps.setString(index++, td.getCountry());
				ps.setString(index++, td.getProvince());
				ps.setString(index++, td.getCity());
				ps.setObject(index++, td.getPinCodeId());
				ps.setString(index++, td.getPinCode());
				ps.setString(index++, td.getSezCertificateNo());
				ps.setDate(index++, JdbcUtil.getDate(td.getSezValueDate()));
				ps.setString(index++, td.getAddressDetail());
				ps.setInt(index++, td.getVersion());
				ps.setLong(index++, JdbcUtil.setLong(td.getLastMntBy()));
				ps.setTimestamp(index++, td.getLastMntOn());
				ps.setString(index++, td.getRecordStatus());
				ps.setString(index++, td.getRoleCode());
				ps.setString(index++, td.getNextRoleCode());
				ps.setString(index++, td.getTaskId());
				ps.setString(index++, td.getNextTaskId());
				ps.setString(index++, td.getRecordType());
				ps.setLong(index++, JdbcUtil.setLong(td.getWorkflowId()));

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(td.getFinReference());
	}

	@Override
	public void update(FinanceTaxDetail td, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FinTaxDetail");
		sql.append(tableType.getSuffix());
		sql.append(" Set ApplicableFor = ?, TaxCustId= ?, TaxExempted = ?, TaxNumber = ?");
		sql.append(", AddrLine1 = ?, AddrLine2 = ?, AddrLine3 = ?, AddrLine4 = ?");
		sql.append(", Country = ?, Province = ?, City = ?, PinCodeId = ?, PinCode = ?, SezCertificateNo = ?");
		sql.append(", SezValueDate = ?, AddressDetail = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, td.getApplicableFor());
			ps.setLong(index++, td.getTaxCustId());
			ps.setBoolean(index++, td.isTaxExempted());
			ps.setString(index++, td.getTaxNumber());
			ps.setString(index++, td.getAddrLine1());
			ps.setString(index++, td.getAddrLine2());
			ps.setString(index++, td.getAddrLine3());
			ps.setString(index++, td.getAddrLine4());
			ps.setString(index++, td.getCountry());
			ps.setString(index++, td.getProvince());
			ps.setString(index++, td.getCity());
			ps.setObject(index++, JdbcUtil.getLong(td.getPinCodeId()));
			ps.setString(index++, td.getPinCode());
			ps.setString(index++, td.getSezCertificateNo());
			ps.setDate(index++, JdbcUtil.getDate(td.getSezValueDate()));
			ps.setString(index++, td.getAddressDetail());
			ps.setTimestamp(index++, td.getLastMntOn());
			ps.setString(index++, td.getRecordStatus());
			ps.setString(index++, td.getRoleCode());
			ps.setString(index++, td.getNextRoleCode());
			ps.setString(index++, td.getTaskId());
			ps.setString(index++, td.getNextTaskId());
			ps.setString(index++, td.getRecordType());
			ps.setLong(index++, td.getWorkflowId());

			ps.setLong(index++, td.getFinID());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index++, td.getPrevMntOn());
			} else {
				ps.setInt(index++, td.getVersion() - 1);
			}

		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(FinanceTaxDetail td, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From FinTaxDetail");
		sql.append(tableType.getSuffix());
		sql.append(" where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), td.getFinID());
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

	}

	@Override
	public int getGSTNumberCount(long taxCustId, String taxNumber, String type) {
		StringBuilder sql = new StringBuilder("Select count(TaxNumber) From FinTaxDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where TaxCustId <> ? and TaxNumber = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, taxCustId, taxNumber);
		} catch (DataAccessException e) {
			//
		}

		return 0;
	}

	@Override
	public List<FinanceTaxDetail> getGSTNumberAndCustCIF(long taxCustId, String taxNumber, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TaxCustId, TaxNumber");
		sql.append(" From FinTaxDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where TaxCustId <> ? and TaxNumber = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, taxCustId);
			ps.setString(index++, taxNumber);
		}, (rs, rowNum) -> {
			FinanceTaxDetail gd = new FinanceTaxDetail();

			gd.setTaxCustId(rs.getLong("TaxCustId"));
			gd.setTaxNumber(rs.getString("TaxNumber"));

			return gd;
		});
	}

	public boolean isReferenceExists(long finID, String custCif) {
		String sql = "Select count(TaxNumber) From FinTaxDetail_View Where FinID = ? and  CustCif = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, finID, custCif) > 0;
		} catch (DataAccessException e) {
			//
		}
		return false;
	}

	@Override
	public void deleteFinTaxDetails(FinanceTaxDetail td, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From FinTaxDetail");
		sql.append(tableType.getSuffix());
		sql.append(" where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), td.getFinID());
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public int getFinTaxDetailsCount(long finID) {
		String sql = "Select count(FinID) FROM fintaxdetail_temp Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, Integer.class, finID);
		} catch (DataAccessException e) {
			//
		}

		return 0;
	}

}
