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
 * * FileName : DedupParmDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-08-2011 * * Modified
 * Date : 23-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.blacklist.NegativeReasoncodes;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>DedupParm model</b> class.<br>
 * 
 */
public class BlackListCustomerDAOImpl extends SequenceDao<BlackListCustomers> implements BlackListCustomerDAO {
	private static Logger logger = LogManager.getLogger(BlackListCustomerDAOImpl.class);

	public BlackListCustomerDAOImpl() {
		super();
	}

	@Override
	public BlackListCustomers getBlackListCustomers() {
		return new BlackListCustomers();
	}

	@Override
	public BlackListCustomers getBlacklistCustomerById(String id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustCIF, CustFName, CustLName, CustDOB, CustCRCPR, CustPassportNo, MobileNumber");
		sql.append(", CustNationality, Employer, CustIsActive, ReasonCode, Source, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId, CustCtgCode, CustCompName, CustAadhaar, CustCin, Gender, Vid, Dl");
		sql.append(", AddressType, HouseNumber, Street, City, Country, State, Pincode, Product_Applied_In_Other_FI");
		sql.append(", Forged_Document_Type, Remarks, Branch, AdditionalField0, AdditionalField1, AdditionalField2");
		sql.append(", AdditionalField3, AdditionalField4, AdditionalField5, AdditionalField6, AdditionalField7");
		sql.append(", AdditionalField8, AdditionalField9, AdditionalField10, AdditionalField11, AdditionalField12");
		sql.append(", AdditionalField13, Address, AdditionalField14");

		if (type.contains("_View")) {
			sql.append(", LovDescNationalityDesc, LovDescEmpName, EmpIndustry");
		}

		sql.append(" From BlackListCustomer");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustCIF = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				BlackListCustomers su = new BlackListCustomers();

				su.setCustCIF(rs.getString("CustCIF"));
				su.setCustFName(rs.getString("CustFName"));
				su.setCustLName(rs.getString("CustLName"));
				su.setCustDOB(rs.getTimestamp("CustDOB"));
				su.setCustCRCPR(rs.getString("CustCRCPR"));
				su.setCustPassportNo(rs.getString("CustPassportNo"));
				su.setMobileNumber(rs.getString("MobileNumber"));
				su.setCustNationality(rs.getString("CustNationality"));
				su.setEmployer(JdbcUtil.getLong(rs.getObject("Employer")));
				su.setCustIsActive(rs.getBoolean("CustIsActive"));
				su.setReasonCode(rs.getString("ReasonCode"));
				su.setSource(rs.getString("Source"));
				su.setVersion(rs.getInt("Version"));
				su.setLastMntBy(rs.getLong("LastMntBy"));
				su.setLastMntOn(rs.getTimestamp("LastMntOn"));
				su.setRecordStatus(rs.getString("RecordStatus"));
				su.setRoleCode(rs.getString("RoleCode"));
				su.setNextRoleCode(rs.getString("NextRoleCode"));
				su.setTaskId(rs.getString("TaskId"));
				su.setNextTaskId(rs.getString("NextTaskId"));
				su.setRecordType(rs.getString("RecordType"));
				su.setWorkflowId(rs.getLong("WorkflowId"));
				su.setCustCtgCode(rs.getString("CustCtgCode"));
				su.setCustCompName(rs.getString("CustCompName"));
				su.setCustAadhaar(rs.getString("CustAadhaar"));
				su.setCustCin(rs.getString("CustCin"));
				su.setGender(rs.getString("Gender"));
				su.setVid(rs.getString("Vid"));
				su.setDl(rs.getString("Dl"));
				su.setAddressType(rs.getString("AddressType"));
				su.setHouseNumber(rs.getString("HouseNumber"));
				su.setStreet(rs.getString("Street"));
				su.setCity(rs.getString("City"));
				su.setCountry(rs.getString("Country"));
				su.setState(rs.getString("State"));
				su.setPincode(rs.getString("Pincode"));
				su.setProduct_Applied_In_Other_FI(rs.getString("Product_Applied_In_Other_FI"));
				su.setForged_Document_Type(rs.getString("Forged_Document_Type"));
				su.setRemarks(rs.getString("Remarks"));
				su.setBranch(rs.getString("Branch"));
				su.setAdditionalField0(rs.getTimestamp("AdditionalField0"));
				su.setAdditionalField1(rs.getString("AdditionalField1"));
				su.setAdditionalField2(rs.getString("AdditionalField2"));
				su.setAdditionalField3(rs.getString("AdditionalField3"));
				su.setAdditionalField4(rs.getString("AdditionalField4"));
				su.setAdditionalField5(rs.getString("AdditionalField5"));
				su.setAdditionalField6(rs.getString("AdditionalField6"));
				su.setAdditionalField7(rs.getString("AdditionalField7"));
				su.setAdditionalField8(rs.getString("AdditionalField8"));
				su.setAdditionalField9(rs.getString("AdditionalField9"));
				su.setAdditionalField10(rs.getString("AdditionalField10"));
				su.setAdditionalField11(rs.getString("AdditionalField11"));
				su.setAdditionalField12(rs.getString("AdditionalField12"));
				su.setAdditionalField13(rs.getString("AdditionalField13"));
				su.setAddress(rs.getString("Address"));
				su.setAdditionalField14(rs.getString("AdditionalField14"));

				if (type.contains("_View")) {
					su.setLovDescNationalityDesc(rs.getString("LovDescNationalityDesc"));
					su.setLovDescEmpName(rs.getString("LovDescEmpName"));
					su.setEmpIndustry(rs.getString("EmpIndustry"));
				}

				return su;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public BlackListCustomers getNewBlacklistCustomer() {
		BlackListCustomers blackListedCustomer = getBlackListCustomers();
		blackListedCustomer.setNewRecord(true);

		return blackListedCustomer;
	}

	@Override
	public void saveList(List<FinBlacklistCustomer> blackList, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinBlackListDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(FinID, FinReference, CustCIF, CustFName, CustLName, CustShrtName, CustDOB, CustCRCPR");
		sql.append(", CustPassportNo, MobileNumber, CustNationality, ReasonCode, Source, Employer, WatchListRule");
		sql.append(", Override, OverrideUser, SourceCIF");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinBlacklistCustomer bl = blackList.get(i);
				int index = 1;

				ps.setLong(index++, bl.getFinID());
				ps.setString(index++, bl.getFinReference());
				ps.setString(index++, bl.getCustCIF());
				ps.setString(index++, bl.getCustFName());
				ps.setString(index++, bl.getCustLName());
				ps.setString(index++, bl.getCustShrtName());
				ps.setDate(index++, JdbcUtil.getDate(bl.getCustDOB()));
				ps.setString(index++, bl.getCustCRCPR());
				ps.setString(index++, bl.getCustPassportNo());
				ps.setString(index++, bl.getMobileNumber());
				ps.setString(index++, bl.getCustNationality());
				ps.setString(index++, bl.getReasonCode());
				ps.setString(index++, bl.getSource());
				ps.setString(index++, bl.getEmployer());
				ps.setString(index++, bl.getWatchListRule());
				ps.setBoolean(index++, bl.isOverride());
				ps.setString(index++, bl.getOverrideUser());
				ps.setString(index, bl.getSourceCIF());
			}

			@Override
			public int getBatchSize() {
				return blackList.size();
			}
		});
	}

	@Override
	public List<FinBlacklistCustomer> fetchOverrideBlackListData(String finReference, String queryCode,
			String sourceCIF) {
		StringBuilder sql = getSqlQuery();
		sql.append(" Where FinReference = ? and SourceCIF = ? and WatchListRule like (?)");

		logger.debug(Literal.SQL + sql.toString());

		FinBlacklistCustomerRM rowMapper = new FinBlacklistCustomerRM();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, finReference);
			ps.setString(index++, sourceCIF);
			ps.setString(index, "%" + queryCode + "%");
		}, rowMapper);
	}

	@Override
	public List<FinBlacklistCustomer> fetchFinBlackList(long finID) {
		StringBuilder sql = getSqlQuery();
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinBlacklistCustomerRM rowMapper = new FinBlacklistCustomerRM();
		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID), rowMapper);
	}

	@Override
	public void updateList(List<FinBlacklistCustomer> blackList) {
		StringBuilder sql = new StringBuilder("Update FinBlackListDetail");
		sql.append(" Set");
		sql.append(" CustFName = ?, CustLName = ?, CustShrtName = ?, CustDOB = ?");
		sql.append(", CustCRCPR= ?, CustPassportNo = ?, MobileNumber = ?, CustNationality = ?");
		sql.append(", ReasonCode= ?, Source= ?,  Employer = ?, WatchListRule = ?");
		sql.append(", Override = ?, OverrideUser = ?");
		sql.append(" Where FinID = ? and CustCIF = ? and SourceCIF = ? ");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinBlacklistCustomer bl = blackList.get(i);

				int index = 1;

				ps.setString(index++, bl.getCustFName());
				ps.setString(index++, bl.getCustLName());
				ps.setString(index++, bl.getCustShrtName());
				ps.setDate(index++, JdbcUtil.getDate(bl.getCustDOB()));
				ps.setString(index++, bl.getCustCRCPR());
				ps.setString(index++, bl.getCustPassportNo());
				ps.setString(index++, bl.getMobileNumber());
				ps.setString(index++, bl.getCustNationality());
				ps.setString(index++, bl.getReasonCode());
				ps.setString(index++, bl.getSource());
				ps.setString(index++, bl.getEmployer());
				ps.setString(index++, bl.getWatchListRule());
				ps.setBoolean(index++, bl.isOverride());
				ps.setString(index++, bl.getOverrideUser());

				ps.setLong(index++, bl.getFinID());
				ps.setString(index++, bl.getCustCIF());
				ps.setString(index, bl.getSourceCIF());
			}

			@Override
			public int getBatchSize() {
				return blackList.size();
			}
		});
	}

	@Override
	public void deleteList(long finID) {
		String sql = "Delete From FinBlackListDetail Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> ps.setLong(1, finID));
	}

	@Override
	public void update(BlackListCustomers blc, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update BlackListCustomer");
		sql.append(tableType.getSuffix());
		sql.append(" Set");
		sql.append(" CustFName = ?, CustLName = ?, CustDOB = ?, CustCRCPR = ?, CustPassportNo = ?");
		sql.append(", MobileNumber = ?, CustNationality = ?, ReasonCode = ?, Source = ?, Employer = ?");
		sql.append(", CustIsActive = ?, Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(", CustCtgCode = ?, CustCompName = ?, CustAadhaar = ?, CustCin = ?, Gender = ?, Vid = ?, Dl = ?");
		sql.append(", AddressType = ?, HouseNumber = ?, Street = ?, City = ?, Country = ?, State = ?, Pincode = ?");
		sql.append(", Product_Applied_In_Other_FI = ?, Forged_Document_Type = ?, Remarks = ?, Branch = ?, Address = ?");
		sql.append(", AdditionalField0 = ?, AdditionalField1 = ?, AdditionalField2 = ?, AdditionalField3 = ?");
		sql.append(", AdditionalField4 = ?, AdditionalField5 = ?, AdditionalField6 = ?, AdditionalField7 = ?");
		sql.append(", AdditionalField8 = ?, AdditionalField9 = ?, AdditionalField10 = ?, AdditionalField11 = ?");
		sql.append(", AdditionalField12 = ?, AdditionalField13 = ?, AdditionalField14 = ?");
		sql.append(" Where CustCIF = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, blc.getCustFName());
			ps.setString(index++, blc.getCustLName());
			ps.setDate(index++, JdbcUtil.getDate(blc.getCustDOB()));
			ps.setString(index++, blc.getCustCRCPR());
			ps.setString(index++, blc.getCustPassportNo());
			ps.setString(index++, blc.getMobileNumber());
			ps.setString(index++, blc.getCustNationality());
			ps.setString(index++, blc.getReasonCode());
			ps.setString(index++, blc.getSource());
			ps.setObject(index++, blc.getEmployer());
			ps.setBoolean(index++, blc.isCustIsActive());
			ps.setInt(index++, blc.getVersion());
			ps.setLong(index++, blc.getLastMntBy());
			ps.setTimestamp(index++, blc.getLastMntOn());
			ps.setString(index++, blc.getRecordStatus());
			ps.setString(index++, blc.getRoleCode());
			ps.setString(index++, blc.getNextRoleCode());
			ps.setString(index++, blc.getTaskId());
			ps.setString(index++, blc.getNextTaskId());
			ps.setString(index++, blc.getRecordType());
			ps.setLong(index++, blc.getWorkflowId());
			ps.setString(index++, blc.getCustCtgCode());
			ps.setString(index++, blc.getCustCompName());
			ps.setString(index++, blc.getCustAadhaar());
			ps.setString(index++, blc.getCustCin());
			ps.setString(index++, blc.getGender());
			ps.setString(index++, blc.getVid());
			ps.setString(index++, blc.getDl());
			ps.setString(index++, blc.getAddressType());
			ps.setString(index++, blc.getHouseNumber());
			ps.setString(index++, blc.getStreet());
			ps.setString(index++, blc.getCity());
			ps.setString(index++, blc.getCountry());
			ps.setString(index++, blc.getState());
			ps.setString(index++, blc.getPincode());
			ps.setString(index++, blc.getProduct_Applied_In_Other_FI());
			ps.setString(index++, blc.getForged_Document_Type());
			ps.setString(index++, blc.getRemarks());
			ps.setString(index++, blc.getBranch());
			ps.setString(index++, blc.getAddress());
			ps.setDate(index++, JdbcUtil.getDate(blc.getAdditionalField0()));
			ps.setString(index++, blc.getAdditionalField1());
			ps.setString(index++, blc.getAdditionalField2());
			ps.setString(index++, blc.getAdditionalField3());
			ps.setString(index++, blc.getAdditionalField4());
			ps.setString(index++, blc.getAdditionalField5());
			ps.setString(index++, blc.getAdditionalField6());
			ps.setString(index++, blc.getAdditionalField7());
			ps.setString(index++, blc.getAdditionalField8());
			ps.setString(index++, blc.getAdditionalField9());
			ps.setString(index++, blc.getAdditionalField10());
			ps.setString(index++, blc.getAdditionalField11());
			ps.setString(index++, blc.getAdditionalField12());
			ps.setString(index++, blc.getAdditionalField13());
			ps.setString(index++, blc.getAdditionalField14());

			ps.setString(index++, blc.getCustCIF());

			if (TableType.TEMP_TAB.equals(tableType)) {
				ps.setTimestamp(index, blc.getPrevMntOn());
			} else {
				ps.setInt(index, blc.getVersion() - 1);
			}

		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(BlackListCustomers blc, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From BlackListCustomer");
		sql.append(tableType.getSuffix());
		sql.append(" Where CustCIF = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				ps.setString(index++, blc.getCustCIF());
				if (TableType.TEMP_TAB.equals(tableType)) {
					ps.setTimestamp(index, blc.getPrevMntOn());
				} else {
					ps.setInt(index, blc.getVersion() - 1);
				}
			});

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(BlackListCustomers blc, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into BlackListCustomer");
		sql.append(tableType.getSuffix());
		sql.append("(CustCIF, CustFName, CustLName, CustDOB, CustCRCPR, CustPassportNo, MobileNumber");
		sql.append(", CustNationality, ReasonCode, Source, Employer, CustIsActive, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId, CustCtgCode, CustCompName, CustAadhaar, CustCin, Gender, Vid, Dl, AddressType");
		sql.append(", HouseNumber, Street, City, Country, State, Pincode, Product_Applied_In_Other_FI");
		sql.append(", Forged_Document_Type, Remarks, Branch, AdditionalField0, AdditionalField1, AdditionalField2");
		sql.append(", AdditionalField3, AdditionalField4, AdditionalField5, AdditionalField6, AdditionalField7");
		sql.append(", AdditionalField8, AdditionalField9, AdditionalField10, AdditionalField11, AdditionalField12");
		sql.append(", AdditionalField13, Address, AdditionalField14");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, blc.getCustCIF());
				ps.setString(index++, blc.getCustFName());
				ps.setString(index++, blc.getCustLName());
				ps.setDate(index++, JdbcUtil.getDate(blc.getCustDOB()));
				ps.setString(index++, blc.getCustCRCPR());
				ps.setString(index++, blc.getCustPassportNo());
				ps.setString(index++, blc.getMobileNumber());
				ps.setString(index++, blc.getCustNationality());
				ps.setString(index++, blc.getReasonCode());
				ps.setString(index++, blc.getSource());
				ps.setObject(index++, blc.getEmployer());
				ps.setBoolean(index++, blc.isCustIsActive());
				ps.setInt(index++, blc.getVersion());
				ps.setLong(index++, blc.getLastMntBy());
				ps.setTimestamp(index++, blc.getLastMntOn());
				ps.setString(index++, blc.getRecordStatus());
				ps.setString(index++, blc.getRoleCode());
				ps.setString(index++, blc.getNextRoleCode());
				ps.setString(index++, blc.getTaskId());
				ps.setString(index++, blc.getNextTaskId());
				ps.setString(index++, blc.getRecordType());
				ps.setLong(index++, blc.getWorkflowId());
				ps.setString(index++, blc.getCustCtgCode());
				ps.setString(index++, blc.getCustCompName());
				ps.setString(index++, blc.getCustAadhaar());
				ps.setString(index++, blc.getCustCin());
				ps.setString(index++, blc.getGender());
				ps.setString(index++, blc.getVid());
				ps.setString(index++, blc.getDl());
				ps.setString(index++, blc.getAddressType());
				ps.setString(index++, blc.getHouseNumber());
				ps.setString(index++, blc.getStreet());
				ps.setString(index++, blc.getCity());
				ps.setString(index++, blc.getCountry());
				ps.setString(index++, blc.getState());
				ps.setString(index++, blc.getPincode());
				ps.setString(index++, blc.getProduct_Applied_In_Other_FI());
				ps.setString(index++, blc.getForged_Document_Type());
				ps.setString(index++, blc.getRemarks());
				ps.setString(index++, blc.getBranch());
				ps.setDate(index++, JdbcUtil.getDate(blc.getAdditionalField0()));
				ps.setString(index++, blc.getAdditionalField1());
				ps.setString(index++, blc.getAdditionalField2());
				ps.setString(index++, blc.getAdditionalField3());
				ps.setString(index++, blc.getAdditionalField4());
				ps.setString(index++, blc.getAdditionalField5());
				ps.setString(index++, blc.getAdditionalField6());
				ps.setString(index++, blc.getAdditionalField7());
				ps.setString(index++, blc.getAdditionalField8());
				ps.setString(index++, blc.getAdditionalField9());
				ps.setString(index++, blc.getAdditionalField10());
				ps.setString(index++, blc.getAdditionalField11());
				ps.setString(index++, blc.getAdditionalField12());
				ps.setString(index++, blc.getAdditionalField13());
				ps.setString(index++, blc.getAddress());
				ps.setString(index, blc.getAdditionalField14());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return blc.getId();
	}

	@Override
	public void moveData(String finReference, String suffix) {
		/* FIXME : change to FinID Pre-approved(_PA) tables need to remove */

		if (StringUtils.isBlank(suffix)) {
			return;
		}

		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT * FROM FinBlackListDetail");
		selectSql.append(" WHERE FinReference = :FinReference ");

		RowMapper<FinBlacklistCustomer> typeRowMapper = BeanPropertyRowMapper.newInstance(FinBlacklistCustomer.class);
		List<FinBlacklistCustomer> list = this.jdbcTemplate.query(selectSql.toString(), map, typeRowMapper);

		if (list != null && !list.isEmpty()) {
			saveList(list, suffix);
		}

		logger.debug(" Leaving ");
	}

	@Override
	public boolean isDuplicateKey(String custCIF, TableType tableType) {
		String sql;
		String whereClause = " CustCIF = ?";

		Object[] args = new Object[] { custCIF };
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BlackListCustomer", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BlackListCustomer_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BlackListCustomer_Temp", "BlackListCustomer" }, whereClause);

			args = new Object[] { custCIF, custCIF };
			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, args) > 0;
	}

	@Override
	public void deleteNegativeReasonList(String blackListCIF, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From NegativeReasonCodes");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where BlackListCIF = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setString(1, blackListCIF));
	}

	@Override
	public void deleteNegativeReason(long reasonId, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From NegativeReasonCodes");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, reasonId));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void saveNegativeReason(NegativeReasoncodes nrc, TableType tableType) {
		if (nrc.getId() == Long.MIN_VALUE) {
			nrc.setId(getNextValue("SeqNegativeReasonCodes"));
		}

		StringBuilder sql = new StringBuilder("Insert into NegativeReasonCodes");
		sql.append(tableType.getSuffix());
		sql.append("(Id, BlackListCIF, ReasonId, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, nrc.getId());
				ps.setString(index++, nrc.getBlackListCIF());
				ps.setObject(index++, nrc.getReasonId());
				ps.setInt(index++, nrc.getVersion());
				ps.setLong(index++, nrc.getLastMntBy());
				ps.setTimestamp(index++, nrc.getLastMntOn());
				ps.setString(index++, nrc.getRecordStatus());
				ps.setString(index++, nrc.getRoleCode());
				ps.setString(index++, nrc.getNextRoleCode());
				ps.setString(index++, nrc.getTaskId());
				ps.setString(index++, nrc.getNextTaskId());
				ps.setString(index++, nrc.getRecordType());
				ps.setLong(index, nrc.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public List<NegativeReasoncodes> getNegativeReasonList(String blacklistCIF, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, BlackListCIF, ReasonId, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From NegativeReasonCodes");
		sql.append(type);
		sql.append(" Where BlackListCIF = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setString(1, blacklistCIF), (rs, rowNum) -> {
			NegativeReasoncodes nrc = new NegativeReasoncodes();

			nrc.setId(rs.getLong("Id"));
			nrc.setBlackListCIF(rs.getString("BlackListCIF"));
			nrc.setReasonId(JdbcUtil.getLong(rs.getObject("ReasonId")));
			nrc.setVersion(rs.getInt("Version"));
			nrc.setLastMntBy(rs.getLong("LastMntBy"));
			nrc.setLastMntOn(rs.getTimestamp("LastMntOn"));
			nrc.setRecordStatus(rs.getString("RecordStatus"));
			nrc.setRoleCode(rs.getString("RoleCode"));
			nrc.setNextRoleCode(rs.getString("NextRoleCode"));
			nrc.setTaskId(rs.getString("TaskId"));
			nrc.setNextTaskId(rs.getString("NextTaskId"));
			nrc.setRecordType(rs.getString("RecordType"));
			nrc.setWorkflowId(rs.getLong("WorkflowId"));

			return nrc;
		});

	}

	@Override
	public List<BlackListCustomers> fetchBlackListedCustomers(BlackListCustomers blCustData, String watchRule) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustCIF,");

		if (ImplementationConstants.ALLOW_SIMILARITY && App.DATABASE == Database.POSTGRES) {
			sql.append(" (ROUND(SIMILARITY (Address, :Address )*100)  ||'%') Address");
			if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, blCustData.getCustCtgCode())) {
				sql.append(", (CustFName ||'  '|| ROUND(SIMILARITY (CustShrtName, :CustShrtName )*100) ||'%')");
			} else {
				sql.append(", (CustCompName ||'  '|| ROUND(SIMILARITY (CustShrtName, :CustShrtName )*100) ||'%')");
			}
		}

		sql.append(" CustFName, CustLName, CustDOB, CustCRCPR, CustPassportNo, MobileNumber");
		sql.append(", CustNationality, Employer, CustIsActive, ReasonCode, Source, CustAadhaar, CustCompName");
		sql.append(" From BlackListCustomer_AView ");
		sql.append(watchRule);
		sql.append(" and CustIsActive = 1");
		if (ImplementationConstants.ALLOW_SIMILARITY && App.DATABASE == Database.POSTGRES) {
			sql.append(" and CustCtgCode = :CustCtgCode");
		}

		logger.trace(Literal.SQL + sql);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blCustData);
		RowMapper<BlackListCustomers> typeRowMapper = BeanPropertyRowMapper.newInstance(BlackListCustomers.class);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	private StringBuilder getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, CustCIF, CustFName, CustLName, CustShrtName, CustDOB, CustCRCPR");
		sql.append(", CustPassportNo, MobileNumber, CustNationality, ReasonCode, Source, Employer");
		sql.append(", WatchListRule, Override, OverrideUser, SourceCIF");
		sql.append(" From FinBlackListDetail");
		return sql;
	}

	private class FinBlacklistCustomerRM implements RowMapper<FinBlacklistCustomer> {

		@Override
		public FinBlacklistCustomer mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinBlacklistCustomer fblc = new FinBlacklistCustomer();

			fblc.setFinID(rs.getLong("FinID"));
			fblc.setFinReference(rs.getString("FinReference"));
			fblc.setCustCIF(rs.getString("CustCIF"));
			fblc.setCustFName(rs.getString("CustFName"));
			fblc.setCustLName(rs.getString("CustLName"));
			fblc.setCustShrtName(rs.getString("CustShrtName"));
			fblc.setCustDOB(rs.getTimestamp("CustDOB"));
			fblc.setCustCRCPR(rs.getString("CustCRCPR"));
			fblc.setCustPassportNo(rs.getString("CustPassportNo"));
			fblc.setMobileNumber(rs.getString("MobileNumber"));
			fblc.setCustNationality(rs.getString("CustNationality"));
			fblc.setReasonCode(rs.getString("ReasonCode"));
			fblc.setSource(rs.getString("Source"));
			fblc.setEmployer(rs.getString("Employer"));
			fblc.setWatchListRule(rs.getString("WatchListRule"));
			fblc.setOverride(rs.getBoolean("Override"));
			fblc.setOverrideUser(rs.getString("OverrideUser"));
			fblc.setSourceCIF(rs.getString("SourceCIF"));

			return fblc;
		}

	}

}