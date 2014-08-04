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
 * FileName    		:  EducationalLoanDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.lmtmasters.EducationalLoanDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>EducationalLoan model</b> class.<br>
 */
public class EducationalLoanDAOImpl extends BasisCodeDAO<EducationalLoan> implements EducationalLoanDAO {

	private static Logger logger = Logger.getLogger(EducationalLoanDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new EducationalLoan 
	 * @return EducationalLoan
	 */
	@Override
	public EducationalLoan getEducationalLoan() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("EducationalLoan");
		EducationalLoan educationalLoan= new EducationalLoan();
		if (workFlowDetails!=null){
			educationalLoan.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return educationalLoan;
	}

	/**
	 * This method get the module from method getEducationalLoan() and set the
	 * new record flag as true and return EducationalLoan()
	 * 
	 * @return EducationalLoan
	 */
	@Override
	public EducationalLoan getNewEducationalLoan() {
		logger.debug("Entering");
		EducationalLoan educationalLoan = getEducationalLoan();
		educationalLoan.setNewRecord(true);
		logger.debug("Leaving");
		return educationalLoan;
	}

	/**
	 * Fetch the Record  Educational Loan Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return EducationalLoan
	 */
	@Override
	public EducationalLoan getEducationalLoanByID(final String loanRef, String type) {
		logger.debug("Entering");
		EducationalLoan educationalLoan = new EducationalLoan();
		educationalLoan.setLoanRefNumber(loanRef);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select LoanRefNumber, LoanRefType, EduCourse, EduSpecialization,");
		selectSql.append(" EduCourseType, EduCourseFrom, EduCourseFromBranch, EduAffiliatedTo," );
		selectSql.append(" EduCommenceDate, EduCompletionDate, EduExpectedIncome, EduLoanFromBranch," );
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescEduCourseName,lovDescEduCourseTypeName,lovDescEduLoanFromBranchName ,");
		}
		selectSql.append(" Version ,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From LMTEducationLoanDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
    	selectSql.append(" Where LoanRefNumber =:LoanRefNumber"); 
    	
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(educationalLoan);
		RowMapper<EducationalLoan> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				EducationalLoan.class);
		try{
			educationalLoan = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			educationalLoan = null;
		}
		logger.debug("Leaving");
		return educationalLoan;
	}
	
	/**
	 * This method initialize the Record.
	 * @param EducationalLoan (educationalLoan)
 	 * @return EducationalLoan
	 */
	@Override
	public void initialize(EducationalLoan educationalLoan) {
		super.initialize(educationalLoan);
	}
	
	/**
	 * This method refresh the Record.
	 * @param EducationalLoan (educationalLoan)
 	 * @return void
	 */
	@Override
	public void refresh(EducationalLoan educationalLoan) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the LMTEducationLoanDetail or LMTEducationLoanDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Educational Loan Details by key EduLoanId
	 * 
	 * @param Educational Loan Details (educationalLoan)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(EducationalLoan educationalLoan,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From LMTEducationLoanDetail" );
		deleteSql.append(StringUtils.trimToEmpty(type)); 
		deleteSql.append(" Where LoanRefNumber =:LoanRefNumber");
		
		logger.debug("deleteSql: " + deleteSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(educationalLoan);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004",educationalLoan.getLoanRefNumber(), 
						educationalLoan.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",educationalLoan.getLoanRefNumber(), 
					educationalLoan.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into LMTEducationLoanDetail or LMTEducationLoanDetail_Temp.
	 * it fetches the available Sequence form SeqLMTEducationLoanDetail by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Educational Loan Details 
	 * 
	 * @param Educational Loan Details (educationalLoan)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */	
	@Override
	public String save(EducationalLoan educationalLoan,String type) {
		logger.debug("Entering");
				
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into LMTEducationLoanDetail");
		insertSql.append(StringUtils.trimToEmpty(type)) ;
		insertSql.append(" ( LoanRefNumber, LoanRefType, EduCourse, EduSpecialization, EduCourseType," );
		insertSql.append(" EduCourseFrom, EduCourseFromBranch, EduAffiliatedTo, EduCommenceDate," );
		insertSql.append(" EduCompletionDate, EduExpectedIncome, EduLoanFromBranch ,"); 
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId," );
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:LoanRefNumber, :LoanRefType, :EduCourse, :EduSpecialization," );
		insertSql.append(" :EduCourseType, :EduCourseFrom, :EduCourseFromBranch, :EduAffiliatedTo," );
		insertSql.append(" :EduCommenceDate, :EduCompletionDate, :EduExpectedIncome, :EduLoanFromBranch ," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());   
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(educationalLoan);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return educationalLoan.getLoanRefNumber();
	}
	
	/**
	 * This method updates the Record LMTEducationLoanDetail or LMTEducationLoanDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Educational Loan Details by key EduLoanId and Version
	 * 
	 * @param Educational Loan Details (educationalLoan)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(EducationalLoan educationalLoan,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update LMTEducationLoanDetail" );
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set LoanRefNumber = :LoanRefNumber, LoanRefType = :LoanRefType,");
		updateSql.append(" EduCourse = :EduCourse, EduSpecialization = :EduSpecialization," );
		updateSql.append(" EduCourseType = :EduCourseType, EduCourseFrom = :EduCourseFrom,"); 
		updateSql.append(" EduCourseFromBranch = :EduCourseFromBranch, EduAffiliatedTo = :EduAffiliatedTo,");
		updateSql.append(" EduCommenceDate = :EduCommenceDate, EduCompletionDate = :EduCompletionDate,");
		updateSql.append(" EduExpectedIncome = :EduExpectedIncome, EduLoanFromBranch = :EduLoanFromBranch," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId");
		updateSql.append(" Where LoanRefNumber =:LoanRefNumber");

		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());  
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(educationalLoan);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41003",educationalLoan.getLoanRefNumber(), 
					educationalLoan.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String loanRef, String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = loanRef;

		parms[0][0] = PennantJavaUtil.getLabel("label_LoanRefNumber")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
				errorId, parms[0],parms[1]), userLanguage);
	}
}