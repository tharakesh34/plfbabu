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
 * FileName    		:  BranchDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Branch model</b> class.<br>
 * 
 */
public class BranchDAOImpl extends BasisCodeDAO<Branch> implements BranchDAO {

	private static Logger logger = Logger.getLogger(BranchDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new Branch
	 * 
	 * @return Branch
	 */
	@Override
	public Branch getBranch() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Branch");
		Branch branch= new Branch();
		if (workFlowDetails!=null){
			branch.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return branch;
	}

	/**
	 * This method get the module from method getBranch() and set the new record
	 * flag as true and return Branch()
	 * 
	 * @return Branch
	 */
	@Override
	public Branch getNewBranch() {
		logger.debug("Entering");
		Branch branch = getBranch();
		branch.setNewRecord(true);
		logger.debug("Leaving");
		return branch;
	}

	/**
	 * Fetch the Record  Branches details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Branch
	 */
	@Override
	public Branch getBranchById(final String id, String type) {
		logger.debug("Entering");
		Branch branch = new Branch();
		branch.setId(id);		
	
		StringBuilder selectSql = new StringBuilder("SELECT  BranchCode, BranchDesc, BranchAddrLine1," );
		selectSql.append(" BranchAddrLine2, BranchPOBox, BranchCity, BranchProvince, BranchCountry,");
		selectSql.append(" BranchFax, BranchTel, BranchSwiftBankCde, BranchSwiftCountry," );
		selectSql.append(" BranchSwiftLocCode, BranchSwiftBrnCde, BranchSortCode, BranchIsActive," );
		if(type.contains("View")){
			selectSql.append(" lovDescBranchCityName,lovDescBranchProvinceName,lovDescBranchCountryName," );
			selectSql.append("lovDescBranchSwiftCountryName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId " );
		selectSql.append(" FROM  RMTBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BranchCode =:BranchCode");

		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(branch);
		RowMapper<Branch> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(Branch.class);
		
		try{
			branch = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			branch = null;
		}
		logger.debug("Leaving");
		return branch;
	}
	
	/**
	 * This method initialize the Record.
	 * @param Branch (branch)
 	 * @return Branch
	 */
	@Override
	public void initialize(Branch branch) {
		super.initialize(branch);
	}
	
	/**
	 * This method refresh the Record.
	 * @param Branch (branch)
 	 * @return void
	 */
	@Override
	public void refresh(Branch branch) {
		
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the RMTBranches or RMTBranches_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Branches by key BranchCode
	 * 
	 * @param Branches (branch)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Branch branch,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTBranches" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where BranchCode =:BranchCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(branch);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003", branch.getBranchCode(), 
						branch.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", branch.getBranchCode(), 
					branch.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into RMTBranches or RMTBranches_Temp.
	 *
	 * save Branches 
	 * 
	 * @param Branches (branch)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Branch branch,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTBranches" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (BranchCode, BranchDesc, BranchAddrLine1, BranchAddrLine2, BranchPOBox," );
		insertSql.append(" BranchCity, BranchProvince, BranchCountry, BranchFax, BranchTel," );
		insertSql.append(" BranchSwiftBankCde, BranchSwiftCountry, BranchSwiftLocCode," );
		insertSql.append(" BranchSwiftBrnCde, BranchSortCode, BranchIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BranchCode, :BranchDesc, :BranchAddrLine1, :BranchAddrLine2,");
		insertSql.append(" :BranchPOBox, :BranchCity, :BranchProvince, :BranchCountry, :BranchFax," );
		insertSql.append(" :BranchTel, :BranchSwiftBankCde, :BranchSwiftCountry, :BranchSwiftLocCode," );
		insertSql.append(" :BranchSwiftBrnCde, :BranchSortCode, :BranchIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(branch);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return branch.getId();
	}
	
	/**
	 * This method updates the Record RMTBranches or RMTBranches_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Branches by key BranchCode and Version
	 * 
	 * @param Branches (branch)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Branch branch,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update RMTBranches" );
		updateSql.append(StringUtils.trimToEmpty(type) );
		updateSql.append(" Set BranchCode = :BranchCode, BranchDesc = :BranchDesc," );
		updateSql.append(" BranchAddrLine1 = :BranchAddrLine1, BranchAddrLine2 = :BranchAddrLine2,");
		updateSql.append(" BranchPOBox = :BranchPOBox, BranchCity = :BranchCity," );
		updateSql.append(" BranchProvince = :BranchProvince, BranchCountry = :BranchCountry," );
		updateSql.append(" BranchFax = :BranchFax,  BranchSwiftCountry = :BranchSwiftCountry,");
		updateSql.append(" BranchSwiftBankCde = :BranchSwiftBankCde, BranchTel = :BranchTel," );
		updateSql.append(" BranchSwiftLocCode = :BranchSwiftLocCode, BranchSortCode = :BranchSortCode," );
		updateSql.append(" BranchSwiftBrnCde = :BranchSwiftBrnCde, BranchIsActive = :BranchIsActive," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where BranchCode =:BranchCode ");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(branch);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004", branch.getBranchCode(), 
					branch.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId,String branchCode, String userLanguage){
		String[][] parms= new String[2][1]; 
		
		parms[1][0] = branchCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_BranchCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
				errorId, parms[0],parms[1]), userLanguage);
	}

}