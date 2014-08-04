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
 * FileName    		:  ContractorAssetDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-09-2013    														*
 *                                                                  						*
 * Modified Date    :  27-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-09-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.contractor.impl;


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
import com.pennant.backend.dao.finance.contractor.ContractorAssetDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>ContractorAssetDetail model</b> class.<br>
 * 
 */

public class ContractorAssetDetailDAOImpl extends BasisCodeDAO<ContractorAssetDetail> implements ContractorAssetDetailDAO {

	private static Logger logger = Logger.getLogger(ContractorAssetDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new ContractorAssetDetail 
	 * @return ContractorAssetDetail
	 */

	@Override
	public ContractorAssetDetail getContractorAssetDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ContractorAssetDetail");
		ContractorAssetDetail contractorAssetDetail= new ContractorAssetDetail();
		if (workFlowDetails!=null){
			contractorAssetDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return contractorAssetDetail;
	}


	/**
	 * This method get the module from method getContractorAssetDetail() and set the new record flag as true and return ContractorAssetDetail()   
	 * @return ContractorAssetDetail
	 */


	@Override
	public ContractorAssetDetail getNewContractorAssetDetail() {
		logger.debug("Entering");
		ContractorAssetDetail contractorAssetDetail = getContractorAssetDetail();
		contractorAssetDetail.setNewRecord(true);
		logger.debug("Leaving");
		return contractorAssetDetail;
	}

	/**
	 * Fetch the Record  Finance Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ContractorAssetDetail
	 */
	@Override
	public ContractorAssetDetail getContractorAssetDetailById(final String finReference, long contractorId, String type) {
		logger.debug("Entering");
		ContractorAssetDetail contractorAssetDetail = new ContractorAssetDetail();
		
		contractorAssetDetail.setFinReference(finReference);
		contractorAssetDetail.setContractorId(contractorId);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, ContractorId, CustID, AssetDesc, AssetValue, TotClaimAmt, TotAdvanceAmt");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(", lovDescCustCIF, lovDescCustShrtName");
		}
		selectSql.append(" From FinContractorAssetDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		selectSql.append(" AND ContractorId = :ContractorId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contractorAssetDetail);
		RowMapper<ContractorAssetDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ContractorAssetDetail.class);
		
		try{
			contractorAssetDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			contractorAssetDetail = null;
		}
		logger.debug("Leaving");
		return contractorAssetDetail;
	}
	
	@Override
	public List<ContractorAssetDetail> getContractorDetailDetailByFinRef(final String finRefNo, String type) {
		logger.debug("Entering");
		ContractorAssetDetail contractorAssetDetail = new ContractorAssetDetail();
		
		contractorAssetDetail.setFinReference(finRefNo);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, ContractorId, CustID,  AssetDesc, AssetValue , TotClaimAmt, TotAdvanceAmt");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(", lovDescCustCIF, lovDescCustShrtName");
		}
		selectSql.append(" From FinContractorAssetDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contractorAssetDetail);
		RowMapper<ContractorAssetDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ContractorAssetDetail.class);
		
		logger.debug("Leaving");
		return  this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		
		
	}
	
	/**
	 * This method initialise the Record.
	 * @param ContractorAssetDetail (contractorAssetDetail)
 	 * @return ContractorAssetDetail
	 */
	@Override
	public void initialize(ContractorAssetDetail contractorAssetDetail) {
		super.initialize(contractorAssetDetail);
	}
	/**
	 * This method refresh the Record.
	 * @param ContractorAssetDetail (contractorAssetDetail)
 	 * @return void
	 */
	@Override
	public void refresh(ContractorAssetDetail contractorAssetDetail) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinContractorAssetDetails or FinContractorAssetDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Details by key FinReference
	 * 
	 * @param Finance Details (contractorAssetDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(ContractorAssetDetail contractorAssetDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinContractorAssetDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		deleteSql.append(" AND  ContractorId =:ContractorId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contractorAssetDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",contractorAssetDetail.getId() ,contractorAssetDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",contractorAssetDetail.getId() ,contractorAssetDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinContractorAssetDetails or FinContractorAssetDetails_Temp.
	 *
	 * save Finance Details 
	 * 
	 * @param Finance Details (contractorAssetDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(ContractorAssetDetail contractorAssetDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinContractorAssetDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, ContractorId, CustID, AssetDesc, AssetValue, TotClaimAmt, TotAdvanceAmt");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :ContractorId, :CustID, :AssetDesc, :AssetValue, :TotClaimAmt, :TotAdvanceAmt");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contractorAssetDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return contractorAssetDetail.getId();
	}
	
	/**
	 * This method updates the Record FinContractorAssetDetails or FinContractorAssetDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Finance Details by key FinReference and Version
	 * 
	 * @param Finance Details (contractorAssetDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(ContractorAssetDetail contractorAssetDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinContractorAssetDetails");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinReference = :FinReference, ContractorId = :ContractorId, CustID =:CustID, AssetDesc = :AssetDesc, AssetValue = :AssetValue, TotClaimAmt = :TotClaimAmt, TotAdvanceAmt = :TotAdvanceAmt");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference AND ContractorId = :ContractorId");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contractorAssetDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",contractorAssetDetail.getId() ,contractorAssetDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String finReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
	
	
	@SuppressWarnings("serial")
    @Override
	public void deleteByFinRef(String finReference, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		ContractorAssetDetail contractorAssetDetail = new ContractorAssetDetail();
		contractorAssetDetail.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From FinContractorAssetDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference = :FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contractorAssetDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",String.valueOf(contractorAssetDetail.getContractorId()),contractorAssetDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",String.valueOf(contractorAssetDetail.getContractorId()) ,contractorAssetDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
}