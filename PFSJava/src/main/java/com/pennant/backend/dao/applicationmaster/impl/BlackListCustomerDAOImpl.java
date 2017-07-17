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

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>DedupParm model</b> class.<br>
 * 
 */
public class BlackListCustomerDAOImpl extends BasisCodeDAO<BlackListCustomers> implements BlackListCustomerDAO {
	private static Logger logger = Logger.getLogger(BlackListCustomerDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
		
		BlackListCustomers blacklistCustomer= new BlackListCustomers();
		blacklistCustomer.setId(id);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , Employer, CustIsActive, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(type.contains("_View")) {
			selectSql.append(" ,lovDescNationalityDesc,lovDescEmpName ");
		}
		selectSql.append(" From BlackListCustomer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustCIF =:CustCIF ");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blacklistCustomer);
		RowMapper<BlackListCustomers> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BlackListCustomers.class);

		try {
			blacklistCustomer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
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
    public void saveList(List<FinBlacklistCustomer> blackList,String type) {
		logger.debug(Literal.ENTERING);
		
    	StringBuilder insertSql = new StringBuilder("Insert Into FinBlackListDetail");
    	insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference , CustCIF , CustFName , CustLName , ");
		insertSql.append(" CustShrtName , CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , ");
		insertSql.append(" Employer , WatchListRule , Override , OverrideUser )");
		insertSql.append(" Values( ");
		insertSql.append(" :FinReference , :CustCIF , :CustFName , :CustLName , ");
		insertSql.append(" :CustShrtName , :CustDOB , :CustCRCPR ,:CustPassportNo , :MobileNumber , :CustNationality , ");
		insertSql.append(" :Employer , :WatchListRule , :Override , :OverrideUser)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(blackList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		
		logger.debug(Literal.LEAVING);
    }
	
	@Override
    public List<FinBlacklistCustomer> fetchOverrideBlackListData(String finReference, String queryCode) {
		logger.debug(Literal.ENTERING);
		
		BlackListCustomers blackListCustomer = new BlackListCustomers();
		blackListCustomer.setFinReference(finReference);
		blackListCustomer.setWatchListRule(queryCode);

		StringBuilder selectSql = new StringBuilder(" Select FinReference , CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustShrtName , CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , ");
		selectSql.append(" Employer , WatchListRule , Override , OverrideUser ");
		selectSql.append(" From FinBlackListDetail");
		selectSql.append(" Where FinReference = :FinReference AND WatchListRule LIKE ('%");
		selectSql.append(queryCode);
		selectSql.append("%')");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blackListCustomer);
		RowMapper<FinBlacklistCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinBlacklistCustomer.class);

		logger.debug(Literal.LEAVING);
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }
	
	@Override
    public List<FinBlacklistCustomer> fetchFinBlackList(String finReference) {
		logger.debug(Literal.ENTERING);
		
		FinBlacklistCustomer finBlacklistCustomer = new FinBlacklistCustomer();
		finBlacklistCustomer.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" Select FinReference , CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustShrtName , CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , ");
		selectSql.append(" Employer , WatchListRule , Override , OverrideUser ");
		selectSql.append(" From FinBlackListDetail");
		selectSql.append(" Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finBlacklistCustomer);
		RowMapper<FinBlacklistCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinBlacklistCustomer.class);

		logger.debug(Literal.LEAVING);
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }

	@Override
    public List<BlackListCustomers> fetchBlackListedCustomers(BlackListCustomers blCustData, String watchRule) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder selectSql = new StringBuilder(" Select CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , Employer, CustIsActive ");
		selectSql.append(" From BlackListCustomer ");
		selectSql.append(watchRule);

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blCustData);
		RowMapper<BlackListCustomers> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BlackListCustomers.class);

		logger.debug(Literal.LEAVING);
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }

	@Override
    public void updateList(List<FinBlacklistCustomer> blackList) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder updateSql = new StringBuilder("Update FinBlackListDetail");
		updateSql.append(" Set CustFName = :CustFName," );
		updateSql.append(" CustLName = :CustLName , CustShrtName = :CustShrtName, CustDOB = :CustDOB, " );
		updateSql.append(" CustCRCPR= :CustCRCPR, CustPassportNo = :CustPassportNo,MobileNumber = :MobileNumber, CustNationality = :CustNationality," );
		updateSql.append(" Employer = :Employer, WatchListRule = :WatchListRule, Override = :Override, OverrideUser = :OverrideUser" );
		updateSql.append(" Where FinReference =:FinReference  AND CustCIF =:CustCIF");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(blackList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		
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
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		
		logger.debug(Literal.LEAVING);
    }

	@Override
	public void update(BlackListCustomers finBlacklistCustomer, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update BlackListCustomer");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set CustFName=:CustFName , CustLName=:CustLName , " );
		updateSql.append(" CustDOB=:CustDOB, CustCRCPR=:CustCRCPR , CustPassportNo=:CustPassportNo , MobileNumber=:MobileNumber , CustNationality=:CustNationality, " );
		updateSql.append(" Employer=:Employer, CustIsActive = :CustIsActive, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" WHERE CustCIF=:CustCIF ");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finBlacklistCustomer);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);
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
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);
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
		insertSql.append("(CustCIF, CustFName, CustLName , CustDOB, CustCRCPR, CustPassportNo ,MobileNumber,CustNationality, ");
		insertSql.append(" Employer, CustIsActive, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(");
		insertSql.append(" :CustCIF , :CustFName , :CustLName, :CustDOB , :CustCRCPR , :CustPassportNo, :MobileNumber, :CustNationality, ");
		insertSql.append(" :Employer, :CustIsActive, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finBlacklistCustomer);
		try{
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return finBlacklistCustomer.getId();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
    public void moveData(String finReference, String suffix) {
		logger.debug(" Entering ");
		try {
	        if (StringUtils.isBlank(suffix)) {
	            return;
	        }
	        
	        MapSqlParameterSource map=new MapSqlParameterSource();
	        map.addValue("FinReference", finReference);
	        
	        StringBuilder selectSql = new StringBuilder();
	        selectSql.append(" SELECT * FROM FinBlackListDetail");
	        selectSql.append(" WHERE FinReference = :FinReference ");
	        
	        RowMapper<FinBlacklistCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinBlacklistCustomer.class);
	        List<FinBlacklistCustomer> list = this.namedParameterJdbcTemplate.query(selectSql.toString(), map,typeRowMapper);
	        
	        if (list!=null && !list.isEmpty()) {
	        	saveList(list,suffix);
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

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}