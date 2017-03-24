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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

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
		logger.debug("Entering");
		BlackListCustomers blackListCustomers = new BlackListCustomers();
		logger.debug("Leaving");
		return blackListCustomers;
    }
	
	@Override
    public BlackListCustomers getBlacklistCustomerById(String id, String type) {
		logger.debug("Entering");
		
		BlackListCustomers blacklistCustomer= new BlackListCustomers();
		blacklistCustomer.setId(id);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , Employer, ");
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
		
		logger.debug("Leaving");
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
		logger.debug("Entering");
		
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
		
		logger.debug("Leaving");
    }
	
	@Override
    public List<FinBlacklistCustomer> fetchOverrideBlackListData(String finReference, String queryCode) {
		logger.debug("Entering");
		
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

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }
	
	@Override
    public List<FinBlacklistCustomer> fetchFinBlackList(String finReference) {
		logger.debug("Entering");
		
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

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }

	@Override
    public List<BlackListCustomers> fetchBlackListedCustomers(BlackListCustomers blCustData, String watchRule) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder(" Select CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , Employer ");
		selectSql.append(" From BlackListCustomer ");
		selectSql.append(watchRule);

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blCustData);
		RowMapper<BlackListCustomers> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BlackListCustomers.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }

	@Override
    public void updateList(List<FinBlacklistCustomer> blackList) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update FinBlackListDetail");
		updateSql.append(" Set FinReference = :FinReference, CustCIF = :CustCIF, CustFName = :CustFName," );
		updateSql.append(" CustLName = :CustLName , CustShrtName = :CustShrtName, CustDOB = :CustDOB, " );
		updateSql.append(" CustCRCPR= :CustCRCPR, CustPassportNo = :CustPassportNo,MobileNumber = :MobileNumber, CustNationality = :CustNationality," );
		updateSql.append(" Employer = :Employer, WatchListRule = :WatchListRule, Override = :Override, OverrideUser = :OverrideUser" );
		updateSql.append(" Where FinReference =:FinReference  AND CustCIF =:CustCIF");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(blackList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	    
    }
	
	@Override
    public void deleteList(String finReference) {
		logger.debug("Entering");
		
		BlackListCustomers blackListCustomer = new BlackListCustomers();
		blackListCustomer.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From FinBlackListDetail");
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blackListCustomer);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		
		logger.debug("Leaving");
    }

	@SuppressWarnings("serial")
	@Override
	public void update(BlackListCustomers finBlacklistCustomer, String type) {
		logger.debug("Entering");
		
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update BlackListCustomer");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustCIF=:CustCIF , CustFName=:CustFName , CustLName=:CustLName , " );
		updateSql.append(" CustDOB=:CustDOB , CustCRCPR=:CustCRCPR , CustPassportNo=:CustPassportNo , MobileNumber=:MobileNumber , CustNationality=:CustNationality, " );
		updateSql.append(" Employer=:Employer,Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" WHERE CustCIF=:CustCIF ");
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finBlacklistCustomer);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails = getError("41003",finBlacklistCustomer.getCustCIF(), finBlacklistCustomer.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		
		logger.debug("Leaving");
	}
	
	@SuppressWarnings("serial")
	@Override
	public void delete(BlackListCustomers finBlacklistCustomer, String type) {
		logger.debug("Entering");
		
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BlackListCustomer");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustCIF =:CustCIF");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finBlacklistCustomer);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",finBlacklistCustomer.getCustCIF(), 
						finBlacklistCustomer.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006",finBlacklistCustomer.getCustCIF(), 
					finBlacklistCustomer.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		
		logger.debug("Leaving");

	}
	@Override
	public String save(BlackListCustomers finBlacklistCustomer, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into BlackListCustomer");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(CustCIF, CustFName, CustLName , CustDOB, CustCRCPR, CustPassportNo ,MobileNumber,CustNationality, ");
		insertSql.append(" Employer,Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(");
		insertSql.append(" :CustCIF , :CustFName , :CustLName , :CustDOB ,  :CustCRCPR , :CustPassportNo ,:MobileNumber , :CustNationality, ");
		insertSql.append(" :Employer, :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finBlacklistCustomer);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return finBlacklistCustomer.getId();
	}
	
	private ErrorDetails  getError(String errorId, String custCIF, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = String.valueOf(custCIF);
		parms[0][0] = PennantJavaUtil.getLabel("label_custCIF")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
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

}