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
 * FileName    		:  CommidityLoanDetailDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.lmtmasters.SharesDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.model.lmtmasters.SharesDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CommidityLoanDetail model</b> class.<br>
 * 
 */

public class SharesDetailDAOImpl extends BasisCodeDAO<CommidityLoanDetail> implements SharesDetailDAO {

	private static Logger logger = Logger.getLogger(SharesDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new SharesDetail 
	 * @return SharesDetail
	 */

	@Override
	public SharesDetail getSharesDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("sharesDetail");
		SharesDetail sharesDetail= new SharesDetail();
		if (workFlowDetails!=null){
			sharesDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return sharesDetail;
	}


	/**
	 * This method get the module from method getCommidityLoanDetail() and set the new record flag as true and return SharesDetail()   
	 * @return SharesDetail
	 */


	@Override
	public SharesDetail getNewSharesDetail() {
		logger.debug("Entering");
		SharesDetail sharesDetail = getSharesDetail();
		sharesDetail.setNewRecord(true);
		logger.debug("Leaving");
		return sharesDetail;
	}

	/**
	 * Fetch the Record  Share Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SharesDetail
	 */
	@Override
	public SharesDetail getSharesDetailById(final String id, String companyName, String type) {
		logger.debug("Entering");
		SharesDetail sharesDetail = new SharesDetail();
		
		sharesDetail.setId(id);
		sharesDetail.setCompanyName(companyName);
		
		StringBuilder selectSql = new StringBuilder("Select LoanRefNumber, CompanyName, Quantity, FaceValue, TotalFaceValue, MarketValue, TotalMarketValue ");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
	
		selectSql.append(" From LMTSharesDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LoanRefNumber =:LoanRefNumber and CompanyName=:CompanyName");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sharesDetail);
		RowMapper<SharesDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SharesDetail.class);
		
		try{
			sharesDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			sharesDetail = null;
		}
		logger.debug("Leaving");
		return sharesDetail;
	}
	@Override
	public List<SharesDetail> getSharesDetailDetailByFinRef(final String id, String type) {
		logger.debug("Entering");
		SharesDetail sharesDetail = new SharesDetail();
		sharesDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select LoanRefNumber, CompanyName, Quantity, FaceValue, TotalFaceValue, MarketValue, TotalMarketValue ");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" From LMTSharesDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LoanRefNumber =:LoanRefNumber");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sharesDetail);
		RowMapper<SharesDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SharesDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * This method initialise the Record.
	 * @param CommidityLoanDetail (CommidityLoanDetail)
 	 * @return CommidityLoanDetail
	 */
	@Override
	public void initialize(SharesDetail sharesDetail) {
		super.initialize(sharesDetail);
	}
	/**
	 * This method refresh the Record.
	 * @param CommidityLoanDetail (CommidityLoanDetail)
 	 * @return void
	 */
	@Override
	public void refresh(SharesDetail sharesDetail) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the LMTCommidityLoanDetail or LMTCommidityLoanDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Goods Details by key LoanRefNumber
	 * 
	 * @param Goods Details (CommidityLoanDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(SharesDetail sharesDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From LMTSharesDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LoanRefNumber =:LoanRefNumber and CompanyName=:CompanyName");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sharesDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",sharesDetail.getId() ,sharesDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",sharesDetail.getId() ,sharesDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into LMTCommidityLoanDetail or LMTCommidityLoanDetail_Temp.
	 *
	 * save Goods Details 
	 * 
	 * @param Goods Details (CommidityLoanDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(SharesDetail sharesDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into LMTSharesDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LoanRefNumber, CompanyName, Quantity, FaceValue, TotalFaceValue, MarketValue, TotalMarketValue");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:LoanRefNumber, :CompanyName, :Quantity, :FaceValue, :TotalFaceValue, :MarketValue, :TotalMarketValue ");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sharesDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return sharesDetail.getId();
	}
	
	/**
	 * This method updates the Record LMTCommidityLoanDetail or LMTCommidityLoanDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Goods Details by key LoanRefNumber and Version
	 * 
	 * @param Goods Details (CommidityLoanDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(SharesDetail sharesDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update LMTSharesDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set LoanRefNumber=:LoanRefNumber, CompanyName=:CompanyName, Quantity=:Quantity, FaceValue=:FaceValue, TotalFaceValue=:TotalFaceValue, MarketValue=:MarketValue, TotalMarketValue=:TotalMarketValue ");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where LoanRefNumber =:LoanRefNumber and CompanyName=:CompanyName");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sharesDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",sharesDetail.getId() ,sharesDetail.getUserDetails().getUsrLanguage());
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
		SharesDetail sharesDetail = new SharesDetail();
		sharesDetail.setId(loanReference);
		
		StringBuilder deleteSql = new StringBuilder("Delete From LMTSharesDetail");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where LoanRefNumber =:LoanRefNumber ");
		logger.debug("deleteSql: " + deleteSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sharesDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	    
    }

	
}