/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  DedupParmDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

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
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>DedupParm model</b> class.<br>
 * 
 */
public class BlackListCustomerDAOImpl extends SequenceDao<BlackListCustomers> implements BlackListCustomerDAO {
	private static Logger logger = Logger.getLogger(BlackListCustomerDAOImpl.class);

	public BlackListCustomerDAOImpl() {
		super();
	}

	@Override
	public BlackListCustomers getBlackListCustomers() {
		logger.debug(Literal.ENTERING);
		BlackListCustomers blackListCustomers = new BlackListCustomers();
		logger.debug(Literal.LEAVING);
		return blackListCustomers;
	}

	@Override
	public BlackListCustomers getBlacklistCustomerById(String id, String type) {
		logger.debug(Literal.ENTERING);

		BlackListCustomers blacklistCustomer = new BlackListCustomers();
		blacklistCustomer.setId(id);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select CustCIF , CustFName , CustLName , ");
		selectSql.append(
				" CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , Employer, CustIsActive, ReasonCode , Source , ");
		selectSql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,");
		selectSql.append(" CustCtgCode, CustCompName, CustAadhaar, CustCin,");
		selectSql.append(
				" Gender, Vid, Dl, AddressType, HouseNumber, Street, City, Country, State, Pincode, Product_Applied_In_Other_FI, Forged_Document_Type, Remarks, ");
		selectSql.append(
				"Branch, AdditionalField0, AdditionalField1, AdditionalField2, AdditionalField3, AdditionalField4, AdditionalField5, AdditionalField6, ");
		selectSql.append(
				"AdditionalField7, AdditionalField8, AdditionalField9, AdditionalField10, AdditionalField11, AdditionalField12, AdditionalField13, Address ");
		selectSql.append(", AdditionalField14");
		if (type.contains("_View")) {
			selectSql.append(" ,lovDescNationalityDesc, lovDescEmpName, empIndustry ");
		}
		selectSql.append(" From BlackListCustomer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustCIF =:CustCIF ");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blacklistCustomer);
		RowMapper<BlackListCustomers> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BlackListCustomers.class);

		try {
			blacklistCustomer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			blacklistCustomer = null;
		}

		logger.debug(Literal.LEAVING);
		return blacklistCustomer;
	}

	@Override
	public BlackListCustomers getNewBlacklistCustomer() {
		logger.debug("Entering ");

		BlackListCustomers blackListedCustomer = getBlackListCustomers();
		blackListedCustomer.setNewRecord(true);

		logger.debug("Leaving ");
		return blackListedCustomer;
	}

	@Override
	public void saveList(List<FinBlacklistCustomer> blackList, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder("Insert Into FinBlackListDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference , CustCIF , CustFName , CustLName , ");
		insertSql.append(
				" CustShrtName , CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , ReasonCode , Source , ");
		insertSql.append(" Employer , WatchListRule , Override , OverrideUser )");
		insertSql.append(" Values( ");
		insertSql.append(" :FinReference , :CustCIF , :CustFName , :CustLName , ");
		insertSql.append(" :CustShrtName , :CustDOB , :CustCRCPR ,:CustPassportNo , :MobileNumber ,");
		insertSql.append(" :CustNationality , :ReasonCode, :Source,");
		insertSql.append(" :Employer , :WatchListRule , :Override , :OverrideUser)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(blackList.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<FinBlacklistCustomer> fetchOverrideBlackListData(String finReference, String queryCode) {
		logger.debug(Literal.ENTERING);

		BlackListCustomers blackListCustomer = new BlackListCustomers();
		blackListCustomer.setFinReference(finReference);
		blackListCustomer.setWatchListRule(queryCode);

		StringBuilder selectSql = new StringBuilder(" Select FinReference , CustCIF , CustFName , CustLName , ");
		selectSql.append(
				" CustShrtName , CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , ReasonCode , Source , ");
		selectSql.append(" Employer , WatchListRule , Override , OverrideUser ");
		selectSql.append(" From FinBlackListDetail");
		selectSql.append(" Where FinReference = :FinReference AND WatchListRule LIKE ('%");
		selectSql.append(queryCode);
		selectSql.append("%')");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blackListCustomer);
		RowMapper<FinBlacklistCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinBlacklistCustomer.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinBlacklistCustomer> fetchFinBlackList(String finReference) {
		logger.debug(Literal.ENTERING);

		FinBlacklistCustomer finBlacklistCustomer = new FinBlacklistCustomer();
		finBlacklistCustomer.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" Select FinReference , CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustShrtName , CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality ,");
		selectSql.append(" Employer , WatchListRule , Override , OverrideUser , ReasonCode , Source ");
		selectSql.append(" From FinBlackListDetail");
		selectSql.append(" Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finBlacklistCustomer);
		RowMapper<FinBlacklistCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinBlacklistCustomer.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<BlackListCustomers> fetchBlackListedCustomers(BlackListCustomers blCustData, String watchRule) {
		logger.debug(Literal.ENTERING);
		StringBuilder selectSql = new StringBuilder("");
		if (ImplementationConstants.ALLOW_SIMILARITY && App.DATABASE == Database.POSTGRES) {
			selectSql = new StringBuilder(" Select CustCIF ,");
			if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, blCustData.getCustCtgCode())) {
				selectSql.append(
						" (CustFName ||'  '|| ROUND(SIMILARITY (CustShrtName, :CustShrtName )*100) ||'%') as CustFName , ");
			} else {
				selectSql.append(
						" (CustCompName ||'  '|| ROUND(SIMILARITY (CustShrtName, :CustShrtName )*100) ||'%') as CustFName , ");
			}
			selectSql.append(
					" CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , Employer, CustIsActive, ReasonCode, Source, custaadhaar, ");
			selectSql.append(" (ROUND(SIMILARITY (Address, :Address )*100)  ||'%')  as Address");
			selectSql.append(" From BlackListCustomer_AView ");
		} else {
			selectSql = new StringBuilder(" Select CustCIF , CustFName , CustLName , ");
			selectSql.append(
					" CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , Employer, CustIsActive, ReasonCode, Source, CustCompName ");
			selectSql.append(" From BlackListCustomer_AView ");
		}
		selectSql.append(watchRule);
		if (ImplementationConstants.ALLOW_SIMILARITY && App.DATABASE == Database.POSTGRES) {
			selectSql.append(" AND CustCtgCode=:CustCtgCode");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blCustData);
		RowMapper<BlackListCustomers> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BlackListCustomers.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void updateList(List<FinBlacklistCustomer> blackList) {
		logger.debug(Literal.ENTERING);

		StringBuilder updateSql = new StringBuilder("Update FinBlackListDetail");
		updateSql.append(" Set CustFName = :CustFName,");
		updateSql.append(" CustLName = :CustLName , CustShrtName = :CustShrtName, CustDOB = :CustDOB, ");
		updateSql.append(
				" CustCRCPR= :CustCRCPR, CustPassportNo = :CustPassportNo,MobileNumber = :MobileNumber, CustNationality = :CustNationality, ReasonCode= :ReasonCode, Source= :Source , ");
		updateSql.append(
				" Employer = :Employer, WatchListRule = :WatchListRule, Override = :Override, OverrideUser = :OverrideUser");
		updateSql.append(" Where FinReference =:FinReference  AND CustCIF =:CustCIF");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(blackList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void deleteList(String finReference) {
		logger.debug(Literal.ENTERING);

		BlackListCustomers blackListCustomer = new BlackListCustomers();
		blackListCustomer.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From FinBlackListDetail");
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blackListCustomer);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void update(BlackListCustomers finBlacklistCustomer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update BlackListCustomer");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set CustFName= :CustFName , CustLName= :CustLName , ");
		updateSql.append(
				" CustDOB= :CustDOB, CustCRCPR= :CustCRCPR , CustPassportNo= :CustPassportNo , MobileNumber= :MobileNumber , CustNationality= :CustNationality, ReasonCode= :ReasonCode,  Source= :Source , ");
		updateSql.append(
				" Employer= :Employer, CustIsActive = :CustIsActive, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, ");
		updateSql.append(
				" CustCtgCode = :CustCtgCode, CustCompName = :CustCompName, CustAadhaar = :CustAadhaar, CustCin = :CustCin, ");
		updateSql.append(
				"Gender = :Gender , Vid = :Vid, Dl = :Dl, AddressType = :AddressType, HouseNumber = :HouseNumber, Street = :Street, City = :City, Country = :Country, State = :State, ");
		updateSql.append(
				" Pincode = :Pincode, Product_Applied_In_Other_FI = :Product_Applied_In_Other_FI, Forged_Document_Type = :Forged_Document_Type, Remarks = :Remarks, ");
		updateSql.append(
				"Branch = :Branch, AdditionalField0 = :AdditionalField0, AdditionalField1 = :AdditionalField1, AdditionalField2 = :AdditionalField2,");
		updateSql.append(
				" AdditionalField3 = :AdditionalField3, AdditionalField4 = :AdditionalField4, AdditionalField5 = :AdditionalField5, AdditionalField6 = :AdditionalField6,");
		updateSql.append(
				" AdditionalField7 = :AdditionalField7, AdditionalField8 = :AdditionalField8, AdditionalField9 = :AdditionalField9, AdditionalField10 = :AdditionalField10,");
		updateSql.append(
				" AdditionalField11 = :AdditionalField11, AdditionalField12 = :AdditionalField12, AdditionalField13 = :AdditionalField13, Address = :Address");
		updateSql.append(", AdditionalField14 = :AdditionalField14");
		updateSql.append(" WHERE CustCIF = :CustCIF ");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finBlacklistCustomer);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(BlackListCustomers finBlacklistCustomer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BlackListCustomer");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where CustCIF =:CustCIF");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finBlacklistCustomer);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public String save(BlackListCustomers finBlacklistCustomer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder("Insert Into BlackListCustomer");
		insertSql.append(tableType.getSuffix());
		insertSql.append(
				"(CustCIF, CustFName, CustLName , CustDOB, CustCRCPR, CustPassportNo ,MobileNumber,CustNationality, ReasonCode , Source , ");
		insertSql.append(
				" Employer, CustIsActive, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId, CustCtgCode, CustCompName, CustAadhaar, CustCin,");

		insertSql.append(
				"Gender, Vid, Dl, AddressType, HouseNumber, Street, City, Country, State, Pincode, Product_Applied_In_Other_FI, Forged_Document_Type, Remarks,");
		insertSql.append(
				" Branch, AdditionalField0, AdditionalField1, AdditionalField2, AdditionalField3, AdditionalField4, AdditionalField5, AdditionalField6,");
		insertSql.append(
				" AdditionalField7, AdditionalField8, AdditionalField9, AdditionalField10, AdditionalField11, AdditionalField12, AdditionalField13, Address,");
		insertSql.append(" AdditionalField14)");
		insertSql.append(" Values(");
		insertSql.append(
				" :CustCIF , :CustFName , :CustLName, :CustDOB , :CustCRCPR , :CustPassportNo, :MobileNumber, :CustNationality, :ReasonCode , :Source ,");
		insertSql.append(
				" :Employer, :CustIsActive, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId, :CustCtgCode, :CustCompName, :CustAadhaar, :CustCin,");
		insertSql.append(
				" :Gender, :Vid, :Dl, :AddressType, :HouseNumber, :Street, :City, :Country, :State, :Pincode, :Product_Applied_In_Other_FI, :Forged_Document_Type, :Remarks,");
		insertSql.append(
				" :Branch, :AdditionalField0, :AdditionalField1, :AdditionalField2, :AdditionalField3, :AdditionalField4, :AdditionalField5, :AdditionalField6,");
		insertSql.append(
				" :AdditionalField7, :AdditionalField8, :AdditionalField9, :AdditionalField10, :AdditionalField11, :AdditionalField12, :AdditionalField13, :Address,");
		insertSql.append(" :AdditionalField14)");
		logger.trace(Literal.SQL + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finBlacklistCustomer);
		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return finBlacklistCustomer.getId();
	}

	@Override
	public void moveData(String finReference, String suffix) {
		logger.debug(" Entering ");
		try {
			if (StringUtils.isBlank(suffix)) {
				return;
			}

			MapSqlParameterSource map = new MapSqlParameterSource();
			map.addValue("FinReference", finReference);

			StringBuilder selectSql = new StringBuilder();
			selectSql.append(" SELECT * FROM FinBlackListDetail");
			selectSql.append(" WHERE FinReference = :FinReference ");

			RowMapper<FinBlacklistCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(FinBlacklistCustomer.class);
			List<FinBlacklistCustomer> list = this.jdbcTemplate.query(selectSql.toString(), map, typeRowMapper);

			if (list != null && !list.isEmpty()) {
				saveList(list, suffix);
			}

		} catch (DataAccessException e) {
			logger.debug(e);
		}
		logger.debug(" Leaving ");

	}

	@Override
	public boolean isDuplicateKey(String custCIF, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "CustCIF =:CustCIF";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BlackListCustomer", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BlackListCustomer_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BlackListCustomer_Temp", "BlackListCustomer" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CustCIF", custCIF);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public void deleteNegativeReasonList(String blackListCIF, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder deleteSql = new StringBuilder("Delete From NegativeReasonCodes");
		deleteSql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		deleteSql.append(" Where blacklistCIF = :blackListCIF ");
		logger.debug("deleteSql: " + deleteSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("blackListCIF", blackListCIF);

		this.jdbcTemplate.update(deleteSql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteNegativeReason(long reasonId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from NegativeReasonCodes");
		sql.append(tableType.getSuffix());
		sql.append(" where Id = :id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", reasonId);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		/*
		 * if (recordCount == 0) { throw new ConcurrencyException(); }
		 */
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveNegativeReason(NegativeReasoncodes negativeReasoncodes, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into NegativeReasonCodes");
		sql.append(tableType.getSuffix());
		sql.append("( id, blackListCIF, reasonId, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append("  :Id, :blackListCIF, :ReasonId, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (negativeReasoncodes.getId() == Long.MIN_VALUE) {
			negativeReasoncodes.setId(getNextValue("SeqNegativeReasonCodes"));
			logger.debug("get NextID:" + negativeReasoncodes.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(negativeReasoncodes);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<NegativeReasoncodes> getNegativeReasonList(String blacklistCIF, String type) {

		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("blacklistCIF", blacklistCIF);

		RowMapper<NegativeReasoncodes> typeRowMapper = BeanPropertyRowMapper.newInstance(NegativeReasoncodes.class);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, blacklistCIF, reasonId, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From NegativeReasonCodes");
		sql.append(type);
		sql.append(" Where blacklistCIF = :blacklistCIF");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);

	}

}