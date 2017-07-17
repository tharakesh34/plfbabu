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
 * FileName    		:  FinSuspHoldDAOImpl.java                                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.financemanagement.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.financemanagement.FinSuspHoldDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.FinSuspHold;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinSuspHold model</b> class.<br>
 * 
 */
public class FinSuspHoldDAOImpl extends BasisNextidDaoImpl<FinSuspHold>  implements FinSuspHoldDAO{

	private static Logger logger = Logger.getLogger(FinSuspHoldDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FinSuspHoldDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new FinSuspHold
	 * 
	 * @return FinSuspHold
	 */
	@Override
	public FinSuspHold getFinSuspHold() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinSuspHold");
		FinSuspHold finSuspHold = new FinSuspHold();
		if (workFlowDetails != null) {
			finSuspHold.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return finSuspHold;
	}

	/**
	 * This method get the module from method getFinSuspHold() and
	 * set the new record flag as true and return FinSuspHold()
	 * 
	 * @return FinSuspHold
	 */
	@Override
	public FinSuspHold getNewFinSuspHold() {
		logger.debug("Entering");
		FinSuspHold finSuspHold = getFinSuspHold();
		finSuspHold.setNewRecord(true);
		logger.debug("Leaving");
		return finSuspHold;
	}

	/**
	 * Fetch the Record FinSuspHold Details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinSuspHold
	 */
	@Override
	public FinSuspHold getFinSuspHoldById(final long id, String type) {
		logger.debug("Entering");
		
		FinSuspHold finSuspHold = new FinSuspHold();
		finSuspHold.setId(id);
		StringBuilder selectSql = new StringBuilder(" SELECT SuspHoldID, Product, FinType, FinReference, CustID, Active," );
		
		if(type.contains("View")){
			selectSql.append(" ProductDesc, FinTypeDesc, CustCIF, CustShrtName,");
		}
		
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  FinSuspHold");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SuspHoldID = :SuspHoldID") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSuspHold);
		RowMapper<FinSuspHold> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinSuspHold.class);

		try {
			finSuspHold = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finSuspHold = null;
		}
		logger.debug("Leaving");
		return finSuspHold;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the FinSuspHold or
	 * FinSuspHold_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete FinSuspHold Detail by
	 * key SuspHoldID
	 * 
	 * @param FinSuspHold (finSuspHold)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinSuspHold finSuspHold, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From FinSuspHold");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SuspHoldID = :SuspHoldID");
		 
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSuspHold);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinSuspHold or
	 * FinSuspHold_Temp.
	 * 
	 * save FinSuspHold Details
	 * 
	 * @param FinSuspHold (finSuspHold)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(FinSuspHold finSuspHold,String type) {
		logger.debug("Entering");
		
		if (finSuspHold.getId() == Long.MIN_VALUE) {
			finSuspHold.setSuspHoldID(getNextidviewDAO().getNextId("SeqFinSuspHold"));
		}
		
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into FinSuspHold");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SuspHoldID, Product, FinType, FinReference, CustID, Active," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:SuspHoldID, :Product, :FinType, :FinReference, :CustID, :Active, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSuspHold);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return finSuspHold.getId();
	}

	/**
	 * This method updates the Record FinSuspHold or
	 * FinSuspHold_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update FinSuspHold Details by
	 * key SuspHoldID and Version
	 * 
	 * @param FinSuspHold (finSuspHold)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinSuspHold finSuspHold,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update FinSuspHold");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Product = :Product, FinType = :FinType, " );
		updateSql.append(" FinReference = :FinReference , CustID = :CustID, Active = :Active," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where SuspHoldID = :SuspHoldID ");
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSuspHold);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Fetch the Record FinSuspHold Details by key field
	 * 
	 * @param finSuspHold
	 *            (FinSuspHold)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinSuspHold
	 */
	@Override
	public FinSuspHold getFinSuspHoldByDetails(FinSuspHold finSuspHold, String type) {
		logger.debug("Entering");
		FinSuspHold finSuspHoldTemp = null ;
		StringBuilder selectSql = new StringBuilder(" SELECT SuspHoldID, Product, FinType, FinReference, CustID, Active," );
		if(type.contains("View")){
			selectSql.append(" ProductDesc, FinTypeDesc, CustCIF, CustShrtName,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  FinSuspHold");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Product = :Product and FinType = :FinType and FinReference = :FinReference and CustID = :CustID") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSuspHold);
		RowMapper<FinSuspHold> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinSuspHold.class);

		try {
			finSuspHoldTemp = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finSuspHoldTemp = null;
		}
		logger.debug("Leaving");
		return finSuspHoldTemp;
	}
	

	/**
	 * @param product
	 * @param finType
	 * @param finReference
	 * @param custID
	 * @return
	 */
	public boolean holdSuspense(String product, String finType,String finReference, long custID) {
		logger.debug("Entering");

		FinSuspHold finSuspHold = new FinSuspHold();
		finSuspHold.setFinReference(finReference);
		finSuspHold.setProduct(product);
		finSuspHold.setFinType(finType);
		finSuspHold.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT * FROM FinSuspHold");
		selectSql.append(" Where Product = :Product or FinType = :FinType or FinReference = :FinReference or CustID = :CustID and Active=1");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSuspHold);
		RowMapper<FinSuspHold> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinSuspHold.class);

		try {
			List<FinSuspHold> finSuspHoldTemp = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
			if (!finSuspHoldTemp.isEmpty()) {
				return true;
			}
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return false;
	}
}