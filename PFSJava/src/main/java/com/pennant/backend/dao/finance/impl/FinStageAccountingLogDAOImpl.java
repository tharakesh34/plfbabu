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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  PostingsDAOImpl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  07-02-2012    
 *                                                                  
 * Modified Date    :  07-02-2012    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-02-2012       PENNANT TECHONOLOGIES	                 0.1                            * 
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

package com.pennant.backend.dao.finance.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinStageAccountingLogDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.FinStageAccountingLog;

/**
 * DAO methods implementation for the <b>ReturnDataSet model</b> class.<br>
 */
public class FinStageAccountingLogDAOImpl extends BasisCodeDAO<FinStageAccountingLog> implements FinStageAccountingLogDAO {

	private static Logger logger = Logger.getLogger(FinStageAccountingLogDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public FinStageAccountingLogDAOImpl() {
		super();
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
    public long getLinkedTranId(String finReference, String finEvent, String roleCode) {
		logger.debug("Entering");
		
		FinStageAccountingLog stageAccountingLog = new FinStageAccountingLog();
		stageAccountingLog.setFinReference(finReference);
		stageAccountingLog.setRoleCode(roleCode);
		stageAccountingLog.setProcessed(false);

		StringBuilder selectSql = new StringBuilder(" SELECT LinkedTranId FROM FinStageAccountingLog");
		selectSql.append(" WHERE FinReference=:FinReference AND RoleCode = :RoleCode AND FinEvent= :FinEvent AND Processed =:Processed" );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stageAccountingLog);
		
		long linkedTranId = 0;
		try {
			linkedTranId = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Long.class);
		} catch (Exception e) {
			logger.info(e);
			linkedTranId = 0;
		}
		
		logger.debug("Leaving");
		return linkedTranId;
    }
	
	/**
	 * Method for fetch List of Transactions & Id's for Finance
	 * @param finReference
	 * @return
	 */
	@Override
	public List<Long> getLinkedTranIdList(String finReference, String finEvent) {
		logger.debug("Entering");
		
		FinStageAccountingLog stageAccountingLog = new FinStageAccountingLog();
		stageAccountingLog.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder(" SELECT LinkedTranId FROM FinStageAccountingLog");
		selectSql.append(" WHERE FinReference=:FinReference AND FinEvent= :FinEvent " );
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stageAccountingLog);
		
		List<Long> linkedTranIdList = this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters, Long.class);
		logger.debug("Leaving");
		return linkedTranIdList;
	}

	@Override
    public void saveStageAccountingLog(FinStageAccountingLog stageAccountingLog) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into FinStageAccountingLog");
		insertSql.append(" (FinReference, FinEvent, RoleCode, LinkedTranId,Processed) ");
		insertSql.append(" Values(:FinReference,:FinEvent, :RoleCode, :LinkedTranId,:Processed)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stageAccountingLog);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
    }

	@Override
    public void deleteByRefandRole(String finReference,String finEvent, String roleCode) {
		logger.debug("Entering");
		
		FinStageAccountingLog stageAccountingLog = new FinStageAccountingLog();
		stageAccountingLog.setFinReference(finReference);
		stageAccountingLog.setRoleCode(roleCode);
		
		StringBuilder deleteSql = new StringBuilder(" DELETE FROM FinStageAccountingLog");	
		deleteSql.append(" WHERE FinReference =:FinReference AND FinEvent= :FinEvent AND RoleCode=:RoleCode ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stageAccountingLog);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
    }
/*
 * (non-Javadoc)
 * @see com.pennant.backend.dao.finance.FinStageAccountingLogDAO#update(java.lang.String, java.lang.String, boolean)
 * updating processed as true in  finstageAccountingLog table
 */
	@Override
	public void update(String finReference, String finEvent, boolean processed) {
		logger.debug("Entering");
		
		FinStageAccountingLog finStageAccountLog = new FinStageAccountingLog();
		finStageAccountLog.setFinReference(finReference);
		finStageAccountLog.setFinEvent(finEvent);
		finStageAccountLog.setProcessed(processed);
		
		StringBuilder updateSql = new StringBuilder("Update FinStageAccountingLog");
		updateSql.append(" Set Processed='"+1 +"' " );
		updateSql.append(" Where FinReference =:FinReference AND FinEvent= :FinEvent AND Processed=:Processed ");
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStageAccountLog);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}


