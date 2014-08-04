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
 * FileName    		:  MortgageLoanDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-10-2011    														*
 *                                                                  						*
 * Modified Date    :  14-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-10-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.lmtmasters.MortgageLoanDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>MortgageLoanDetail model</b> class.<br>
 */
public class MortgageLoanDetailDAOImpl extends BasisCodeDAO<MortgageLoanDetail> implements MortgageLoanDetailDAO {

	private static Logger logger = Logger.getLogger(MortgageLoanDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new MortgageLoanDetail
	 * 
	 * @return MortgageLoanDetail
	 */
	@Override
	public MortgageLoanDetail getMortgageLoanDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("MortgageLoanDetail");
		MortgageLoanDetail mortgageLoanDetail= new MortgageLoanDetail();
		if (workFlowDetails!=null){
			mortgageLoanDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return mortgageLoanDetail;
	}

	/**
	 * This method get the module from method getMortgageLoanDetail() and set
	 * the new record flag as true and return MortgageLoanDetail()
	 * 
	 * @return MortgageLoanDetail
	 */
	@Override
	public MortgageLoanDetail getNewMortgageLoanDetail() {
		logger.debug("Entering");
		MortgageLoanDetail mortgageLoanDetail = getMortgageLoanDetail();
		mortgageLoanDetail.setNewRecord(true);
		logger.debug("Leaving");
		return mortgageLoanDetail;
	}

	/**
	 * Fetch the Record  Mortgage Loan Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return MortgageLoanDetail
	 */
	public MortgageLoanDetail getMortgageLoanDetailById(final String id, String type) {
		logger.debug("Entering");
		MortgageLoanDetail mortgageLoanDetail = new MortgageLoanDetail();
		mortgageLoanDetail.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select LoanRefNumber, MortgProperty, MortgCurrentValue," );
		selectSql.append(" MortgPurposeOfLoan, MortgPropertyRelation, MortgOwnership, MortgAddrHNbr," );
		selectSql.append(" MortgAddrFlatNbr, MortgAddrStreet, MortgAddrLane1, MortgAddrLane2," );
		selectSql.append(" MortgAddrPOBox, MortgAddrCountry, MortgAddrProvince, MortgAddrCity," );
		selectSql.append(" MortgAddrZIP, MortgAddrPhone,");
		selectSql.append(" MortDeedNo,MortRegistrationNo,MortAreaSF,MortAreaSM,MortPricePF,MortAge,MortFinRatio,MortStatus,");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescMortgPropertyName,lovDescMortgPropertyRelationName," );
			selectSql.append(" lovDescMortgOwnershipName,lovDescMortgAddrCountryName," );
			selectSql.append(" lovDescMortgAddrProvinceName,lovDescMortgAddrCityName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From LMTMortgageLoanDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LoanRefNumber =:LoanRefNumber");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mortgageLoanDetail);
		RowMapper<MortgageLoanDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MortgageLoanDetail.class);
		try{
			mortgageLoanDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			mortgageLoanDetail = null;
		}
		logger.debug("Leaving");
		return mortgageLoanDetail;
	}

	/**
	 * This method initialize the Record.
	 * @param MortgageLoanDetail (mortgageLoanDetail)
	 * @return MortgageLoanDetail
	 */
	@Override
	public void initialize(MortgageLoanDetail mortgageLoanDetail) {
		super.initialize(mortgageLoanDetail);
	}
	
	/**
	 * This method refresh the Record.
	 * @param MortgageLoanDetail (mortgageLoanDetail)
	 * @return void
	 */
	@Override
	public void refresh(MortgageLoanDetail mortgageLoanDetail) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the LMTMortgageLoanDetail or LMTMortgageLoanDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Mortgage Loan Detail by key MortgLoanId
	 * 
	 * @param Mortgage Loan Detail (mortgageLoanDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(MortgageLoanDetail mortgageLoanDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From LMTMortgageLoanDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LoanRefNumber =:LoanRefNumber");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mortgageLoanDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",mortgageLoanDetail.getLoanRefNumber(),
						mortgageLoanDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",mortgageLoanDetail.getLoanRefNumber() ,
					mortgageLoanDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into LMTMortgageLoanDetail or LMTMortgageLoanDetail_Temp.
	 * it fetches the available Sequence form SeqLMTMortgageLoanDetail by using 
	 * 		getNextidviewDAO().getNextId() method.  
	 *
	 * save Mortgage Loan Detail 
	 * 
	 * @param Mortgage Loan Detail (mortgageLoanDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(MortgageLoanDetail mortgageLoanDetail,String type) {
		logger.debug("Entering");

		StringBuilder insertSql =new StringBuilder();
		insertSql.append(" Insert Into LMTMortgageLoanDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LoanRefNumber, MortgProperty, MortgCurrentValue, MortgPurposeOfLoan," );
		insertSql.append(" MortgPropertyRelation, MortgOwnership, MortgAddrHNbr, MortgAddrFlatNbr," );
		insertSql.append(" MortgAddrStreet, MortgAddrLane1, MortgAddrLane2, MortgAddrPOBox, MortgAddrCountry,");
		insertSql.append(" MortgAddrProvince, MortgAddrCity, MortgAddrZIP, MortgAddrPhone,");
		insertSql.append(" MortDeedNo,MortRegistrationNo,MortAreaSF,MortAreaSM,MortPricePF,MortAge,MortFinRatio,MortStatus,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:LoanRefNumber, :MortgProperty, :MortgCurrentValue," );
		insertSql.append(" :MortgPurposeOfLoan, :MortgPropertyRelation, :MortgOwnership, :MortgAddrHNbr," );
		insertSql.append(" :MortgAddrFlatNbr, :MortgAddrStreet, :MortgAddrLane1, :MortgAddrLane2," );
		insertSql.append(" :MortgAddrPOBox, :MortgAddrCountry, :MortgAddrProvince, :MortgAddrCity," );
		insertSql.append(" :MortgAddrZIP, :MortgAddrPhone,");
		insertSql.append(" :MortDeedNo,:MortRegistrationNo,:MortAreaSF,:MortAreaSM,:MortPricePF,:MortAge,:MortFinRatio,:MortStatus,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mortgageLoanDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return mortgageLoanDetail.getId();
	}

	/**
	 * This method updates the Record LMTMortgageLoanDetail or LMTMortgageLoanDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Mortgage Loan Detail by key MortgLoanId and Version
	 * 
	 * @param Mortgage Loan Detail (mortgageLoanDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(MortgageLoanDetail mortgageLoanDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder();
		updateSql.append(" Update LMTMortgageLoanDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set LoanRefNumber = :LoanRefNumber," );
		updateSql.append(" MortgProperty = :MortgProperty, MortgCurrentValue = :MortgCurrentValue," );
		updateSql.append(" MortgPurposeOfLoan = :MortgPurposeOfLoan," );
		updateSql.append(" MortgPropertyRelation = :MortgPropertyRelation, MortgOwnership = :MortgOwnership,");
		updateSql.append(" MortgAddrHNbr = :MortgAddrHNbr, MortgAddrFlatNbr = :MortgAddrFlatNbr," );
		updateSql.append(" MortgAddrStreet = :MortgAddrStreet, MortgAddrLane1 = :MortgAddrLane1," );
		updateSql.append(" MortgAddrLane2 = :MortgAddrLane2, MortgAddrPOBox = :MortgAddrPOBox," );
		updateSql.append(" MortgAddrCountry = :MortgAddrCountry, MortgAddrPhone = :MortgAddrPhone," );
		updateSql.append(" MortgAddrProvince = :MortgAddrProvince, MortgAddrCity = :MortgAddrCity,");
		updateSql.append(" MortgAddrZIP = :MortgAddrZIP," );
		updateSql.append(" MortDeedNo=:MortDeedNo, MortRegistrationNo=:MortRegistrationNo, MortAreaSF=:MortAreaSF," );
		updateSql.append(" MortAreaSM=:MortAreaSM, MortPricePF=:MortPricePF, MortAge=:MortAge, MortFinRatio=:MortFinRatio, MortStatus=:MortStatus," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId");
		updateSql.append(" Where LoanRefNumber =:LoanRefNumber");

		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mortgageLoanDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",mortgageLoanDetail.getLoanRefNumber() ,mortgageLoanDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails  getError(String errorId, String loanRefNumber, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] =loanRefNumber;
		parms[0][0] = PennantJavaUtil.getLabel("label_LoanRefNumber")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
				errorId, parms[0],parms[1]), userLanguage);
	}


}