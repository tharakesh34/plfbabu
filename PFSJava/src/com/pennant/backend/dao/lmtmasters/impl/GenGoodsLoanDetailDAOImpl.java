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
 * FileName    		:  GenGoodsLoanDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.lmtmasters.impl;


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
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.lmtmasters.GenGoodsLoanDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.GenGoodsLoanDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>GenGoodsLoanDetail model</b> class.<br>
 * 
 */

public class GenGoodsLoanDetailDAOImpl extends BasisCodeDAO<GenGoodsLoanDetail> implements GenGoodsLoanDetailDAO {

	private static Logger logger = Logger.getLogger(GenGoodsLoanDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new GenGoodsLoanDetail 
	 * @return GenGoodsLoanDetail
	 */

	@Override
	public GenGoodsLoanDetail getGenGoodsLoanDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("GenGoodsLoanDetail");
		GenGoodsLoanDetail goodsLoanDetail= new GenGoodsLoanDetail();
		if (workFlowDetails!=null){
			goodsLoanDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return goodsLoanDetail;
	}


	/**
	 * This method get the module from method getGenGoodsLoanDetail() and set the new record flag as true and return GenGoodsLoanDetail()   
	 * @return GenGoodsLoanDetail
	 */


	@Override
	public GenGoodsLoanDetail getNewGenGoodsLoanDetail() {
		logger.debug("Entering");
		GenGoodsLoanDetail goodsLoanDetail = getGenGoodsLoanDetail();
		goodsLoanDetail.setNewRecord(true);
		logger.debug("Leaving");
		return goodsLoanDetail;
	}

	/**
	 * Fetch the Record  Goods Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return GenGoodsLoanDetail
	 */
	@Override
	public GenGoodsLoanDetail getGenGoodsLoanDetailById(final String id,String itemNumber, String type) {
		logger.debug("Entering");
		GenGoodsLoanDetail goodsLoanDetail = new GenGoodsLoanDetail();
		
		goodsLoanDetail.setId(id);
		goodsLoanDetail.setItemNumber(itemNumber);
		
		StringBuilder selectSql = new StringBuilder("Select LoanRefNumber, ItemNumber, SellerID, ItemDescription, UnitPrice, Quantity, Addtional1, Addtional2, Addtional3, Addtional4, Addtional5, Addtional6, Addtional7, Addtional8");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",LovDescSellerID,lovDescSellerFax,lovDescSellerPhone");
		}
		selectSql.append(" From LMTGenGoodsLoanDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LoanRefNumber =:LoanRefNumber and ItemNumber=:ItemNumber");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(goodsLoanDetail);
		RowMapper<GenGoodsLoanDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(GenGoodsLoanDetail.class);
		
		try{
			goodsLoanDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			goodsLoanDetail = null;
		}
		logger.debug("Leaving");
		return goodsLoanDetail;
	}
	@Override
	public List<GenGoodsLoanDetail> getGenGoodsLoanDetailByFinRef(final String id, String type) {
		logger.debug("Entering");
		GenGoodsLoanDetail goodsLoanDetail = new GenGoodsLoanDetail();
		goodsLoanDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select LoanRefNumber, ItemNumber,SellerID ,ItemDescription, UnitPrice, Quantity, Addtional1, Addtional2, Addtional3, Addtional4, Addtional5, Addtional6, Addtional7, Addtional8");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",LovDescSellerID,lovDescSellerFax,lovDescSellerPhone");
		}
		selectSql.append(" From LMTGenGoodsLoanDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LoanRefNumber =:LoanRefNumber");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(goodsLoanDetail);
		RowMapper<GenGoodsLoanDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(GenGoodsLoanDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * This method initialise the Record.
	 * @param GenGoodsLoanDetail (goodsLoanDetail)
 	 * @return GenGoodsLoanDetail
	 */
	@Override
	public void initialize(GenGoodsLoanDetail goodsLoanDetail) {
		super.initialize(goodsLoanDetail);
	}
	/**
	 * This method refresh the Record.
	 * @param GenGoodsLoanDetail (goodsLoanDetail)
 	 * @return void
	 */
	@Override
	public void refresh(GenGoodsLoanDetail goodsLoanDetail) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the LMTGenGoodsLoanDetail or LMTGenGoodsLoanDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Goods Details by key LoanRefNumber
	 * 
	 * @param Goods Details (goodsLoanDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(GenGoodsLoanDetail goodsLoanDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From LMTGenGoodsLoanDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LoanRefNumber =:LoanRefNumber and ItemNumber=:ItemNumber");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(goodsLoanDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",goodsLoanDetail.getId() ,goodsLoanDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",goodsLoanDetail.getId() ,goodsLoanDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into LMTGenGoodsLoanDetail or LMTGenGoodsLoanDetail_Temp.
	 *
	 * save Goods Details 
	 * 
	 * @param Goods Details (goodsLoanDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(GenGoodsLoanDetail goodsLoanDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into LMTGenGoodsLoanDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LoanRefNumber,  ItemNumber,SellerID, ItemDescription, UnitPrice, Quantity, Addtional1, Addtional2, Addtional3, Addtional4, Addtional5, Addtional6, Addtional7, Addtional8");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:LoanRefNumber, :ItemNumber, :SellerID, :ItemDescription, :UnitPrice, :Quantity, :Addtional1, :Addtional2, :Addtional3, :Addtional4, :Addtional5, :Addtional6, :Addtional7, :Addtional8");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(goodsLoanDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return goodsLoanDetail.getId();
	}
	
	/**
	 * This method updates the Record LMTGenGoodsLoanDetail or LMTGenGoodsLoanDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Goods Details by key LoanRefNumber and Version
	 * 
	 * @param Goods Details (goodsLoanDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(GenGoodsLoanDetail goodsLoanDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update LMTGenGoodsLoanDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set LoanRefNumber = :LoanRefNumber, ItemNumber = :ItemNumber, SellerID = :SellerID, ItemDescription = :ItemDescription, UnitPrice = :UnitPrice, Quantity = :Quantity, Addtional1 = :Addtional1, Addtional2 = :Addtional2, Addtional3 = :Addtional3, Addtional4 = :Addtional4, Addtional5 = :Addtional5, Addtional6 = :Addtional6, Addtional7 = :Addtional7, Addtional8 = :Addtional8");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where LoanRefNumber =:LoanRefNumber and ItemNumber = :ItemNumber");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(goodsLoanDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",goodsLoanDetail.getId() ,goodsLoanDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String loanRefNumber, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = loanRefNumber;
		parms[0][0] = PennantJavaUtil.getLabel("label_LoanRefNumber")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}


	@Override
    public void deleteByFinRef(String loanReference, String tableType) {
		logger.debug("Entering");
		GenGoodsLoanDetail goodsLoanDetail = new GenGoodsLoanDetail();
		goodsLoanDetail.setId(loanReference);
		
		StringBuilder deleteSql = new StringBuilder("Delete From LMTGenGoodsLoanDetail");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where LoanRefNumber =:LoanRefNumber");
		logger.debug("deleteSql: " + deleteSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(goodsLoanDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	    
    }

	
}