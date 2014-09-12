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

package com.pennant.backend.dao.dedup.impl;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.dedup.DedupParmDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>DedupParm model</b> class.<br>
 * 
 */
public class DedupParmDAOImpl extends BasisCodeDAO<DedupParm> implements DedupParmDAO {

	private static Logger logger = Logger.getLogger(DedupParmDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new DedupParm
	 * 
	 * @return DedupParm
	 */
	@Override
	public DedupParm getDedupParm() {
		logger.debug("Entering ");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("DedupParm");
		DedupParm dedupParm= new DedupParm();
		if (workFlowDetails!=null){
			dedupParm.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving ");
		return dedupParm;
	}

	/**
	 * This method get the module from method getDedupParm() and set the new
	 * record flag as true and return DedupParm()
	 * 
	 * @return DedupParm
	 */
	@Override
	public DedupParm getNewDedupParm() {
		logger.debug("Entering");
		DedupParm dedupParm = getDedupParm();
		dedupParm.setNewRecord(true);
		logger.debug("Leaving");
		return dedupParm;
	}

	/**
	 * Fetch the Record  Dedup Parameters details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DedupParm
	 */
	@Override
	public DedupParm getDedupParmByID(final String id,String queryModule ,String querySubCode,
			String type) {
		logger.debug("Entering");
		
		DedupParm dedupParm = new DedupParm();
		dedupParm.setId(id);
		dedupParm.setQuerySubCode(querySubCode);
		dedupParm.setQueryModule(queryModule);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select QueryCode, QueryModule, QuerySubCode,QueryDesc, SQLQuery, ActualBlock, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");	
		selectSql.append(" From DedupParams");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where QueryCode = :QueryCode AND QuerySubCode=:QuerySubCode " );
		selectSql.append(" AND QueryModule=:QueryModule" );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		RowMapper<DedupParm> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DedupParm.class);

		try{
			dedupParm = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			e.printStackTrace();
			dedupParm = null;
		}
		logger.debug("Leaving");
		return dedupParm;
	}

	/**
	 * This method initialize the Record.
	 * @param DedupParm (dedupParm)
	 * @return DedupParm
	 */
	@Override
	public void initialize(DedupParm dedupParm) {
		super.initialize(dedupParm);
	}

	/**
	 * This method refresh the Record.
	 * @param DedupParm (dedupParm)
	 * @return void
	 */
	@Override
	public void refresh(DedupParm dedupParm) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * Method getting list of Data in validation of result builded Query
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List validate(String resultQuery,CustomerDedup customerDedup) {
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDedup);
		return this.namedParameterJdbcTemplate.queryForList(resultQuery, beanParameters);	
	}

	/**
	 * This method Deletes the Record from the DedupParams or DedupParams_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Dedup Parameters by key QueryCode
	 * 
	 * @param Dedup Parameters (dedupParm)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(DedupParm dedupParm,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder(" Delete From DedupParams");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where QueryCode =:QueryCode");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003",
						dedupParm.getId(), dedupParm.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) { };
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",
					dedupParm.getId(), dedupParm.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into DedupParams or DedupParams_Temp.
	 *
	 * save Dedup Parameters 
	 * 
	 * @param Dedup Parameters (dedupParm)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(DedupParm dedupParm,String type) {
		logger.debug("Entering");

		StringBuilder insertSql =new StringBuilder(" Insert Into DedupParams");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (QueryCode, QueryModule,QueryDesc, SQLQuery, ActualBlock, QuerySubCode ,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:QueryCode, :QueryModule,:QueryDesc, :SQLQuery, :ActualBlock, :QuerySubCode,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return dedupParm.getId();
	}

	/**
	 * This method updates the Record DedupParams or DedupParams_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Dedup Parameters by key QueryCode and Version
	 * 
	 * @param Dedup Parameters (dedupParm)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(DedupParm dedupParm,String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder	updateSql =new StringBuilder(" Update DedupParams");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set QueryCode = :QueryCode, QueryModule = :QueryModule," );
		updateSql.append(" QuerySubCode=:QuerySubCode, QueryDesc=:QueryDesc,SQLQuery = :SQLQuery," );
		updateSql.append(" ActualBlock = :ActualBlock, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where QueryCode =:QueryCode AND QueryModule=:QueryModule" );
		updateSql.append(" AND QuerySubCode=:QuerySubCode");

		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupParm);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails = getError("41004",
					dedupParm.getId(), dedupParm.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetched the Dedup Fields if Dedup Exist for a Customer
	 *
	 */
	public List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup dedup,String sqlQuery) {
		logger.debug("Entering");
		List<CustomerDedup> rowTypes = null;
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT * FROM CustomersDedup_View ");
		selectSql.append(StringUtils.trimToEmpty(sqlQuery));
		selectSql.append(" AND custId != :custId ");

		logger.debug("selectSql: " +  selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		ParameterizedBeanPropertyRowMapper<CustomerDedup> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(CustomerDedup.class);

		try{
			rowTypes = this.namedParameterJdbcTemplate.query(selectSql.toString(),
					beanParameters,typeRowMapper);
		}catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			logger.error(e);
			dedup = null;
		}
		logger.debug("Leaving");
		return rowTypes;
	}
	
	/**
	 * Fetched the Dedup Fields if Dedup Exist for a Customer
	 *
	 */
	public List<FinanceDedup> fetchFinDedupDetails(FinanceDedup dedup,String sqlQuery) {
		logger.debug("Entering");
		List<FinanceDedup> rowTypes = null;
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT * FROM FinanceDedup_View ");
		selectSql.append(StringUtils.trimToEmpty(sqlQuery));
		selectSql.append(" AND FinReference != :FinReference ");

		logger.debug("selectSql: " +  selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		ParameterizedBeanPropertyRowMapper<FinanceDedup> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(FinanceDedup.class);

		try{
			rowTypes = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			logger.error(e);
			dedup = null;
		}
		logger.debug("Leaving");
		return rowTypes;
	}
	
	@Override
    public FinanceDedup getFinDedupByCustId(long custID) {
		logger.debug("Entering");
		FinanceDedup financeDedup = new FinanceDedup();
		financeDedup.setCustId(custID);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT * FROM FinanceDedup_AView ");
		selectSql.append(" WHERE CustId = :CustId ");

		logger.debug("selectSql: " +  selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDedup);
		ParameterizedBeanPropertyRowMapper<FinanceDedup> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(FinanceDedup.class);

		try{
			financeDedup = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters,typeRowMapper);
		}catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			logger.error(e);
		}
		logger.debug("Leaving");
		return financeDedup;
    }

	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String QueryCode, String userLanguage){
		String[][] parms = new String[2][2];

		parms[1][0] = QueryCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_QueryCode") + ":" + parms[1][0];

		return ErrorUtil.getErrorDetail(new ErrorDetails(
				PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]), userLanguage);
	}

}