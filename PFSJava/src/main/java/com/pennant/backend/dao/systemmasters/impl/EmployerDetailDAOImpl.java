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
 * * FileName : EmployerDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-07-2013 * *
 * Modified Date : 31-07-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-07-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.systemmasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.systemmasters.EmployerDetailDAO;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>EmployerDetail model</b> class.<br>
 * 
 */

public class EmployerDetailDAOImpl extends SequenceDao<EmployerDetail> implements EmployerDetailDAO {
	private static Logger logger = LogManager.getLogger(EmployerDetailDAOImpl.class);

	public EmployerDetailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Employer Detail details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return EmployerDetail
	 */
	@Override
	public EmployerDetail getEmployerDetailById(final long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" EmployerId, EmpIndustry, EmpName, EstablishDate, EmpAddrHNbr");
		sql.append(", EmpFlatNbr, EmpAddrStreet, EmpAddrLine1, EmpAddrLine2");
		sql.append(", EmpPOBox, EmpCountry, EmpProvince, EmpCity, EmpPhone, EmpFax, EmpTelexNo, EmpEmailId");
		sql.append(", EmpWebSite, ContactPersonName, ContactPersonNo, EmpAlocationType, BankRefNo");
		sql.append(", AllowDas, EmpIsActive, EmpCategory");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescIndustryDesc, LovDescCountryDesc, LovDescProvinceName, LovDescCityName");
		}

		sql.append(" From EmployerDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where EmployerId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				EmployerDetail ed = new EmployerDetail();

				ed.setEmployerId(rs.getLong("EmployerId"));
				ed.setEmpIndustry(rs.getString("EmpIndustry"));
				ed.setEmpName(rs.getString("EmpName"));
				ed.setEstablishDate(rs.getDate("EstablishDate"));
				ed.setEmpAddrHNbr(rs.getString("EmpAddrHNbr"));
				ed.setEmpFlatNbr(rs.getString("EmpFlatNbr"));
				ed.setEmpAddrStreet(rs.getString("EmpAddrStreet"));
				ed.setEmpAddrLine1(rs.getString("EmpAddrLine1"));
				ed.setEmpAddrLine2(rs.getString("EmpAddrLine2"));
				ed.setEmpPOBox(rs.getString("EmpPOBox"));
				ed.setEmpCountry(rs.getString("EmpCountry"));
				ed.setEmpProvince(rs.getString("EmpProvince"));
				ed.setEmpCity(rs.getString("EmpCity"));
				ed.setEmpPhone(rs.getString("EmpPhone"));
				ed.setEmpFax(rs.getString("EmpFax"));
				ed.setEmpTelexNo(rs.getString("EmpTelexNo"));
				ed.setEmpEmailId(rs.getString("EmpEmailId"));
				ed.setEmpWebSite(rs.getString("EmpWebSite"));
				ed.setContactPersonName(rs.getString("ContactPersonName"));
				ed.setContactPersonNo(rs.getString("ContactPersonNo"));
				ed.setEmpAlocationType(rs.getString("EmpAlocationType"));
				ed.setBankRefNo(rs.getString("BankRefNo"));
				ed.setAllowDas(rs.getBoolean("AllowDas"));
				ed.setEmpIsActive(rs.getBoolean("EmpIsActive"));
				ed.setEmpCategory(rs.getString("EmpCategory"));
				ed.setVersion(rs.getInt("Version"));
				ed.setLastMntBy(rs.getLong("LastMntBy"));
				ed.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ed.setRecordStatus(rs.getString("RecordStatus"));
				ed.setRoleCode(rs.getString("RoleCode"));
				ed.setNextRoleCode(rs.getString("NextRoleCode"));
				ed.setTaskId(rs.getString("TaskId"));
				ed.setNextTaskId(rs.getString("NextTaskId"));
				ed.setRecordType(rs.getString("RecordType"));
				ed.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					ed.setLovDescIndustryDesc(rs.getString("LovDescIndustryDesc"));
					ed.setLovDescCountryDesc(rs.getString("LovDescCountryDesc"));
					ed.setLovDescProvinceName(rs.getString("LovDescProvinceName"));
					ed.setLovDescCityName(rs.getString("LovDescCityName"));
				}

				return ed;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the EmployerDetail or EmployerDetail_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Employer Detail by key EmployerId
	 * 
	 * @param Employer Detail (employerDetail)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(EmployerDetail ed, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From EmployerDetail");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where EmployerId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			if (this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, ed.getEmployerId())) == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	/**
	 * This method insert new Records into EmployerDetail or EmployerDetail_Temp. it fetches the available Sequence form
	 * SeqEmployerDetail by using getNextidviewDAO().getNextId() method.
	 *
	 * save Employer Detail
	 * 
	 * @param Employer Detail (employerDetail)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(EmployerDetail ed, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into EmployerDetail");
		sql.append(tableType.getSuffix());
		sql.append(" (EmployerId, EmpIndustry, EmpName, EstablishDate, EmpAddrHNbr, EmpFlatNbr, EmpAddrStreet");
		sql.append(", EmpAddrLine1, EmpAddrLine2, EmpPOBox, EmpCountry, EmpProvince, EmpCity, EmpPhone, EmpFax");
		sql.append(", EmpTelexNo, EmpEmailId, EmpWebSite, ContactPersonName, ContactPersonNo, EmpAlocationType");
		sql.append(", BankRefNo, AllowDas, EmpIsActive, EmpCategory");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(")");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (ed.getId() == Long.MIN_VALUE) {
			ed.setId(getNextValue("SeqEmployerDetail"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, ed.getEmployerId());
				ps.setString(index++, ed.getEmpIndustry());
				ps.setString(index++, ed.getEmpName());
				ps.setDate(index++, JdbcUtil.getDate(ed.getEstablishDate()));
				ps.setString(index++, ed.getEmpAddrHNbr());
				ps.setString(index++, ed.getEmpFlatNbr());
				ps.setString(index++, ed.getEmpAddrStreet());
				ps.setString(index++, ed.getEmpAddrLine1());
				ps.setString(index++, ed.getEmpAddrLine2());
				ps.setString(index++, ed.getEmpPOBox());
				ps.setString(index++, ed.getEmpCountry());
				ps.setString(index++, ed.getEmpProvince());
				ps.setString(index++, ed.getEmpCity());
				ps.setString(index++, ed.getEmpPhone());
				ps.setString(index++, ed.getEmpFax());
				ps.setString(index++, ed.getEmpTelexNo());
				ps.setString(index++, ed.getEmpEmailId());
				ps.setString(index++, ed.getEmpWebSite());
				ps.setString(index++, ed.getContactPersonName());
				ps.setString(index++, ed.getContactPersonNo());
				ps.setString(index++, ed.getEmpAlocationType());
				ps.setString(index++, ed.getBankRefNo());
				ps.setObject(index++, ed.isAllowDas());
				ps.setBoolean(index++, ed.isEmpIsActive());
				ps.setString(index++, ed.getEmpCategory());
				ps.setInt(index++, ed.getVersion());
				ps.setLong(index++, ed.getLastMntBy());
				ps.setTimestamp(index++, ed.getLastMntOn());
				ps.setString(index++, ed.getRecordStatus());
				ps.setString(index++, ed.getRoleCode());
				ps.setString(index++, ed.getNextRoleCode());
				ps.setString(index++, ed.getTaskId());
				ps.setString(index++, ed.getNextTaskId());
				ps.setString(index++, ed.getRecordType());
				ps.setLong(index++, ed.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return Long.toString(ed.getEmployerId());
	}

	/**
	 * This method updates the Record EmployerDetail or EmployerDetail_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Employer Detail by key EmployerId and Version
	 * 
	 * @param Employer Detail (employerDetail)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(EmployerDetail ed, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update EmployerDetail");
		sql.append(tableType.getSuffix());
		sql.append(" Set EmpIndustry = ?, EmpName = ?, EstablishDate = ?, EmpAddrHNbr = ?");
		sql.append(", EmpFlatNbr = ?, EmpAddrStreet = ?, EmpAddrLine1 = ?, EmpAddrLine2 = ?");
		sql.append(", EmpFax = ?, EmpTelexNo = ?, EmpEmailId = ?, EmpWebSite = ?, ContactPersonName = ?");
		sql.append(", ContactPersonNo = ?, EmpAlocationType = ?, BankRefNo = ?, AllowDAS = ?, EmpIsActive = ?");
		sql.append(", EmpCategory = ?, EmpPOBox = ?, EmpCountry = ?, EmpProvince = ?, EmpCity = ?, EmpPhone = ?");
		sql.append(", Version = ? , LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where EmployerId = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, ed.getEmpIndustry());
			ps.setString(index++, ed.getEmpName());
			ps.setDate(index++, JdbcUtil.getDate(ed.getEstablishDate()));
			ps.setString(index++, ed.getEmpAddrHNbr());
			ps.setString(index++, ed.getEmpFlatNbr());
			ps.setString(index++, ed.getEmpAddrStreet());
			ps.setString(index++, ed.getEmpAddrLine1());
			ps.setString(index++, ed.getEmpAddrLine2());
			ps.setString(index++, ed.getEmpFax());
			ps.setString(index++, ed.getEmpTelexNo());
			ps.setString(index++, ed.getEmpEmailId());
			ps.setString(index++, ed.getEmpWebSite());
			ps.setString(index++, ed.getContactPersonName());
			ps.setString(index++, ed.getContactPersonNo());
			ps.setString(index++, ed.getEmpAlocationType());
			ps.setString(index++, ed.getBankRefNo());
			ps.setBoolean(index++, ed.isAllowDas());
			ps.setBoolean(index++, ed.isEmpIsActive());
			ps.setString(index++, ed.getEmpCategory());
			ps.setString(index++, ed.getEmpPOBox());
			ps.setString(index++, ed.getEmpCountry());
			ps.setString(index++, ed.getEmpProvince());
			ps.setString(index++, ed.getEmpCity());
			ps.setString(index++, ed.getEmpPhone());
			ps.setInt(index++, ed.getVersion());
			ps.setLong(index++, ed.getLastMntBy());
			ps.setTimestamp(index++, ed.getLastMntOn());
			ps.setString(index++, ed.getRecordStatus());
			ps.setString(index++, ed.getRoleCode());
			ps.setString(index++, ed.getNextRoleCode());
			ps.setString(index++, ed.getTaskId());
			ps.setString(index++, ed.getNextTaskId());
			ps.setString(index++, ed.getRecordType());
			ps.setLong(index++, ed.getWorkflowId());

			ps.setLong(index++, ed.getEmployerId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public boolean isDuplicateKey(long employerId, TableType tableType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNonTargetEmployee(String name, String category, String type) {
		String sql;
		String whereClause = "EmployerId = ? and And EmpCategory != ?";

		Object obj = new Object[] { name, category };

		switch (type.toLowerCase()) {
		case "":
			sql = QueryUtil.getCountQuery("EmployerDetail", whereClause);
			break;
		case "_temp":
			sql = QueryUtil.getCountQuery("EmployerDetail", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "EmployerDetail_Temp", "EmployerDetail" }, whereClause);
			obj = new Object[] { name, category, name, category };

			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

}