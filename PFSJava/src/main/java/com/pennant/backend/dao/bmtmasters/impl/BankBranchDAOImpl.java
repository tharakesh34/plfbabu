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
 * FileName    		:  BankBranchDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-10-2016    														*
 *                                                                  						*
 * Modified Date    :  17-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.bmtmasters.impl;


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

import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>BankBranch model</b> class.<br>
 * 
 */

public class BankBranchDAOImpl extends BasisNextidDaoImpl<BankBranch> implements BankBranchDAO {

	private static Logger logger = Logger.getLogger(BankBranchDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public BankBranchDAOImpl(){
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new BankBranch 
	 * @return BankBranch
	 */

	@Override
	public BankBranch getBankBranch() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("BankBranch");
		BankBranch bankBranch= new BankBranch();
		if (workFlowDetails!=null){
			bankBranch.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return bankBranch;
	}


	/**
	 * This method get the module from method getBankBranch() and set the new record flag as true and return BankBranch()   
	 * @return BankBranch
	 */


	@Override
	public BankBranch getNewBankBranch() {
		logger.debug("Entering");
		BankBranch bankBranch = getBankBranch();
		bankBranch.setNewRecord(true);
		logger.debug("Leaving");
		return bankBranch;
	}

	/**
	 * Fetch the Record  Bank Branch details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return BankBranch
	 */
	@Override
	public BankBranch getBankBranchById(final long id, String type) {
		logger.debug("Entering");
		BankBranch bankBranch = getBankBranch();
		
		bankBranch.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select BankBranchID, BankCode, BranchCode, BranchDesc, City, MICR, IFSC, AddOfBranch, Nach, Dd, Dda, Ecs, Cheque, Active");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",BankName,PcCityName");
		}
		selectSql.append(" From BankBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankBranchID =:BankBranchID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		RowMapper<BankBranch> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BankBranch.class);
		
		try{
			bankBranch = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			bankBranch = null;
		}
		logger.debug("Leaving");
		return bankBranch;
	}
	
	/**
	 * Fetch the Record  Bank Branch details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return BankBranch
	 */
	@Override
	public int getBankBranchByIFSC(final String iFSC,long id, String type) {
		logger.debug("Entering");
		BankBranch bankBranch = getBankBranch();
		
		bankBranch.setIFSC(iFSC);
		bankBranch.setId(id);
		
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From BankBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where IFSC =:IFSC AND BankBranchID !=:BankBranchID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);	
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the BankBranches or BankBranches_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Bank Branch by key BankBranchID
	 * 
	 * @param Bank Branch (bankBranch)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(BankBranch bankBranch,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From BankBranches");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BankBranchID =:BankBranchID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into BankBranches or BankBranches_Temp.
	 * it fetches the available Sequence form SeqBankBranches by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Bank Branch 
	 * 
	 * @param Bank Branch (bankBranch)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(BankBranch bankBranch,String type) {
		logger.debug("Entering");
		if (bankBranch.getId()==Long.MIN_VALUE){
			bankBranch.setId(getNextidviewDAO().getNextId("SeqBankBranches"));
			logger.debug("get NextID:"+bankBranch.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into BankBranches");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BankBranchID, BankCode, BranchCode, BranchDesc, City, MICR, IFSC, AddOfBranch, Nach, Dd, Dda, Ecs, Cheque, Active");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BankBranchID, :BankCode, :BranchCode, :BranchDesc, :City, :MICR, :IFSC, :AddOfBranch, :Nach, :Dd, :Dda, :Ecs, :Cheque, :Active");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return bankBranch.getId();
	}
	
	/**
	 * This method updates the Record BankBranches or BankBranches_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Bank Branch by key BankBranchID and Version
	 * 
	 * @param Bank Branch (bankBranch)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(BankBranch bankBranch,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update BankBranches");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set BankCode = :BankCode, BranchCode = :BranchCode, BranchDesc = :BranchDesc, City = :City, MICR = :MICR, IFSC = :IFSC");
		updateSql.append(",AddOfBranch = :AddOfBranch, Nach = :Nach, Dd = :Dd, Dda = :Dda, Ecs = :Ecs, Cheque = :Cheque, Active = :Active");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where BankBranchID =:BankBranchID");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * fetch BankBranch details by it's IFSC code.
	 * 
	 * @param ifsc
	 * @param type
	 */
	@Override
	public BankBranch getBankBrachByIFSC(String ifsc, String type) {
		logger.debug("Entering");

		BankBranch bankBranch = getBankBranch();
		bankBranch.setIFSC(ifsc);

		StringBuilder selectSql = new StringBuilder("Select BankBranchID, BankCode, BranchCode,");
		selectSql.append("BranchDesc, City, MICR, IFSC, AddOfBranch, Nach, Dd, Dda, Ecs, Cheque, Active");
		
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",BankName");
		}
		selectSql.append(" From BankBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where IFSC =:IFSC");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		RowMapper<BankBranch> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BankBranch.class);

		try {
			bankBranch = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			bankBranch = null;
		}
		
		logger.debug("Leaving");
		return bankBranch;
	}

	@Override
	public BankBranch getBankBrachByCode(String bankCode, String branchCode, String type) {
		logger.debug("Entering");

		BankBranch bankBranch = getBankBranch();
		bankBranch.setBankCode(bankCode);
		bankBranch.setBranchCode(branchCode);

		StringBuilder selectSql = new StringBuilder("Select BankBranchID, BankCode, BranchCode,");
		selectSql.append("BranchDesc, City, MICR, IFSC, AddOfBranch, Nach, Dd, Dda, Ecs, Cheque, Active");
		
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",BankName,PcCityName");
		}
		selectSql.append(" From BankBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankCode =:BankCode AND BranchCode =:BranchCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		RowMapper<BankBranch> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BankBranch.class);

		try {
			bankBranch = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			bankBranch = null;
		}
		
		logger.debug("Leaving");
		return bankBranch;
	}
	
	@Override
	public int getBankBrachByBank(String bankCode, String type) {
		BankBranch bankBranch = getBankBranch();
		bankBranch.setBankCode(bankCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From BankBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankCode =:BankCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
}