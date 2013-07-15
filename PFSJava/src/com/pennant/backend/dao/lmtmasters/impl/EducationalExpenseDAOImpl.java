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
 * FileName    		:  EducationalExpenseDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.lmtmasters.EducationalExpenseDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.EducationalExpense;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>EducationalExpense model</b> class.<br>
 * 
 */

public class EducationalExpenseDAOImpl extends BasisNextidDaoImpl<EducationalExpense> implements EducationalExpenseDAO {

	private static Logger logger = Logger.getLogger(EducationalExpenseDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new EducationalExpense 
	 * @return EducationalExpense
	 */

	@Override
	public EducationalExpense getEducationalExpense() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("EducationalExpense");
		EducationalExpense educationalExpense= new EducationalExpense();
		if (workFlowDetails!=null){
			educationalExpense.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return educationalExpense;
	}


	/**
	 * This method get the module from method getEducationalExpense() and
	 *  set the new record flag as true and return EducationalExpense()   
	 * @return EducationalExpense
	 */


	@Override
	public EducationalExpense getNewEducationalExpense() {
		logger.debug("Entering");
		EducationalExpense educationalExpense = getEducationalExpense();
		educationalExpense.setNewRecord(true);
		logger.debug("Leaving");
		return educationalExpense;
	}

	/**
	 * Fetch the Record  Educational Expenses details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return EducationalExpense
	 */
	@Override
	public EducationalExpense getEducationalExpenseByID(final String loanRefNumber, long id, String type) {
		logger.debug("Entering");
		EducationalExpense educationalExpense = getEducationalExpense();
		educationalExpense.setId(id);
		educationalExpense.setLoanRefNumber(loanRefNumber);

		StringBuilder	selectSql =new StringBuilder("Select  LoanRefNumber, EduExpDetail, EduExpAmount, EduExpDate" );
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		selectSql.append(" RecordType, WorkflowId" );

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescEduExpDetailName");
		}
		selectSql.append(" From LMTEduExpenseDetail"+ StringUtils.trimToEmpty(type) +" Where LoanRefNumber =:LoanRefNumber");
		selectSql.append(" AND EduExpDetail = :EduExpDetail");

		logger.debug("selectListSql: " + selectSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(educationalExpense);
		RowMapper<EducationalExpense> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EducationalExpense.class);

		try{
			educationalExpense = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			educationalExpense = null;
		}
		logger.debug("Leaving");
		return educationalExpense;
	}

	public List<EducationalExpense> getEducationalExpenseByEduLoanId(final String  loanRefNumber, String type) {
		logger.debug("Entering");
		EducationalExpense educationalExpense = getEducationalExpense();
		educationalExpense.setLoanRefNumber(loanRefNumber);
		List<EducationalExpense>  eduExpenseList;

		StringBuilder	selectSql =new StringBuilder("Select  LoanRefNumber, EduExpDetail, EduExpAmount, EduExpDate" );
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		selectSql.append(" RecordType, WorkflowId" );

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescEduExpDetailName");
		}
		selectSql.append(" From LMTEduExpenseDetail"+ StringUtils.trimToEmpty(type) +" Where LoanRefNumber =:LoanRefNumber");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(educationalExpense);
		RowMapper<EducationalExpense> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EducationalExpense.class);

		try{
			eduExpenseList= this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			eduExpenseList = null;
		}
		logger.debug("Leaving");
		return eduExpenseList;
	}


	/**
	 * This method initialize the Record.
	 * @param EducationalExpense (educationalExpense)
	 * @return EducationalExpense
	 */
	@Override
	public void initialize(EducationalExpense educationalExpense) {
		super.initialize(educationalExpense);
	}
	/**
	 * This method refresh the Record.
	 * @param EducationalExpense (educationalExpense)
	 * @return void
	 */
	@Override
	public void refresh(EducationalExpense educationalExpense) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the LMTEduExpenseDetail or LMTEduExpenseDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Educational Expenses by key EduExpDetail and EduLoanId
	 * 
	 * @param Educational Expenses (educationalExpense)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(EducationalExpense educationalExpense,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder	deleteSql =new StringBuilder();

		deleteSql.append( "Delete From LMTEduExpenseDetail" + StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LoanRefNumber =:LoanRefNumber and EduExpDetail =:EduExpDetail");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(educationalExpense);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",String.valueOf(educationalExpense.getLovDescEduExpDetailName())
						,educationalExpense.getLoanRefNumber()
						,educationalExpense.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",String.valueOf(educationalExpense.getLovDescEduExpDetailName())
					,educationalExpense.getLoanRefNumber()
					,educationalExpense.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the LMTEduExpenseDetail or LMTEduExpenseDetail_Temp.

	 * delete Educational Expenses by key loanRefNumber
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(String  loanRefNumber,String type) {
		logger.debug("Entering");
		EducationalExpense educationalExpense=new EducationalExpense();
		educationalExpense.setLoanRefNumber(loanRefNumber);

		StringBuilder	deleteSql =new StringBuilder();
		deleteSql.append("Delete From LMTEduExpenseDetail" + StringUtils.trimToEmpty(type));
		deleteSql.append(" Where loanRefNumber =:loanRefNumber");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(educationalExpense);
		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(educationalExpense.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_EduExpDetail")+":"+valueParm[0];
		logger.debug("DeleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into LMTEduExpenseDetail or LMTEduExpenseDetail_Temp.
	 * it fetches the available Sequence form SeqLMTEduExpenseDetail by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Educational Expenses 
	 * 
	 * @param Educational Expenses (educationalExpense)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(EducationalExpense educationalExpense,String type) {
		logger.debug("Entering");
		/*	if (educationalExpense.getId()==Long.MIN_VALUE){
			educationalExpense.setId(getNextidviewDAO().getNextId("SeqLMTEduExpenseDetail"));
			logger.debug("get NextID:"+educationalExpense.getId());
		}*/
		StringBuilder	insertSql =new StringBuilder( "Insert Into LMTEduExpenseDetail" + StringUtils.trimToEmpty(type) );
		insertSql.append (" (LoanRefNumber, EduExpDetail, EduExpAmount, EduExpDate");
		insertSql.append (", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
		insertSql.append (", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append (" Values(:LoanRefNumber, :EduExpDetail, :EduExpAmount, :EduExpDate" );
		insertSql.append (", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode");
		insertSql.append (", :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(educationalExpense);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return educationalExpense.getId();
	}

	/**
	 * This method updates the Record LMTEduExpenseDetail or LMTEduExpenseDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Educational Expenses by key EduExpDetail and Version
	 * 
	 * @param Educational Expenses (educationalExpense)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	@Override
	public void update(EducationalExpense educationalExpense,String type) {
		int recordCount = 0;
		//	educationalExpense.setVersion(educationalExpense.getVersion()+1);
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update LMTEduExpenseDetail" + StringUtils.trimToEmpty(type));
		updateSql.append(" Set LoanRefNumber = :LoanRefNumber, EduExpDetail = :EduExpDetail,");
		updateSql.append(" EduExpAmount = :EduExpAmount, EduExpDate = :EduExpDate" );
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn" );
		updateSql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId" );
		updateSql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where LoanRefNumber =:LoanRefNumber and  EduExpDetail = :EduExpDetail");


		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(educationalExpense);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",String.valueOf(educationalExpense.getLovDescEduExpDetailName())
					,educationalExpense.getLoanRefNumber()
					,educationalExpense.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	private ErrorDetails  getError(String errorId, String eduExpense,String loanReference, String userLanguage){

		String[][] parms= new String[2][2];
		parms[1][0] = eduExpense;
		parms[1][1] = loanReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_EduExpDetail")+": "+parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_loanReferenceNumber")+": "+parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD
				, errorId, parms[0],parms[1]), userLanguage);
	}

}