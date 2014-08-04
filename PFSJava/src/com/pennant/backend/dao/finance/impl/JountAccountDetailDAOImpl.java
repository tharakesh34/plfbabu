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
 * FileName    		:  JountAccountDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-09-2013    														*
 *                                                                  						*
 * Modified Date    :  10-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-09-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;


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
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>JountAccountDetail model</b> class.<br>
 * 
 */

public class JountAccountDetailDAOImpl extends BasisNextidDaoImpl<JointAccountDetail> implements JountAccountDetailDAO {

	private static Logger logger = Logger.getLogger(JountAccountDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new JountAccountDetail 
	 * @return JountAccountDetail
	 */

	@Override
	public JointAccountDetail getJountAccountDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("JountAccountDetail");
		JointAccountDetail jountAccountDetail= new JointAccountDetail();
		if (workFlowDetails!=null){
			jountAccountDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return jountAccountDetail;
	}


	/**
	 * This method get the module from method getJountAccountDetail() and set the new record flag as true and return JountAccountDetail()   
	 * @return JountAccountDetail
	 */


	@Override
	public JointAccountDetail getNewJountAccountDetail() {
		logger.debug("Entering");
		JointAccountDetail jountAccountDetail = getJountAccountDetail();
		jountAccountDetail.setNewRecord(true);
		logger.debug("Leaving");
		return jountAccountDetail;
	}

	/**
	 * Fetch the Record  Jount Account Details details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return JountAccountDetail
	 */
	@Override
	public JointAccountDetail getJountAccountDetailById(final long id, String type) {
		logger.debug("Entering");
		JointAccountDetail jountAccountDetail = new JointAccountDetail();
		
		jountAccountDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select JointAccountId, FinReference, CustCIF, IncludeRepay, RepayAccountId");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",LovDescCIFName");
		}
		selectSql.append(" From FinJointAccountDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where JointAccountId =:JointAccountId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
		RowMapper<JointAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(JointAccountDetail.class);
		
		try{
			jountAccountDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			jountAccountDetail = null;
		}
		logger.debug("Leaving");
		return jountAccountDetail;
	}
	
	/**
	 * This method initialise the Record.
	 * @param JointAccountDetail (jountAccountDetail)
 	 * @return JountAccountDetail
	 */
	@Override
	public void initialize(JointAccountDetail jountAccountDetail) {
		super.initialize(jountAccountDetail);
	}
	/**
	 * This method refresh the Record.
	 * @param JointAccountDetail (jountAccountDetail)
 	 * @return void
	 */
	@Override
	public void refresh(JointAccountDetail jountAccountDetail) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinJointAccountDetails or FinJointAccountDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Jount Account Details by key JointAccountId
	 * 
	 * @param Jount Account Details (jountAccountDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(JointAccountDetail jountAccountDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinJointAccountDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where JointAccountId =:JointAccountId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",String.valueOf(jountAccountDetail.getJointAccountId()),jountAccountDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",String.valueOf(jountAccountDetail.getJointAccountId()) ,jountAccountDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinJointAccountDetails or FinJointAccountDetails_Temp.
	 * it fetches the available Sequence form SeqFinJointAccountDetails by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Jount Account Details 
	 * 
	 * @param Jount Account Details (jountAccountDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(JointAccountDetail jountAccountDetail,String type) {
		logger.debug("Entering");
		if (jountAccountDetail.getId()==Long.MIN_VALUE){
			jountAccountDetail.setId(getNextidviewDAO().getNextId("SeqFinJointAccountDetails"));
			logger.debug("get NextID:"+jountAccountDetail.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinJointAccountDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (JointAccountId, FinReference, CustCIF, IncludeRepay, RepayAccountId");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:JointAccountId, :FinReference, :CustCIF, :IncludeRepay, :RepayAccountId");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return jountAccountDetail.getId();
	}
	
	/**
	 * This method updates the Record FinJointAccountDetails or FinJointAccountDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Jount Account Details by key JointAccountId and Version
	 * 
	 * @param Jount Account Details (jountAccountDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(JointAccountDetail jountAccountDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinJointAccountDetails");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set JointAccountId = :JointAccountId, FinReference = :FinReference, CustCIF = :CustCIF, IncludeRepay = :IncludeRepay, RepayAccountId = :RepayAccountId");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where JointAccountId =:JointAccountId");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",String.valueOf(jountAccountDetail.getJointAccountId()) ,jountAccountDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String jointAccountId, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = jointAccountId;
		parms[0][0] = PennantJavaUtil.getLabel("label_JointAccountId")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}


	@Override
    public JointAccountDetail getJountAccountDetailByRefId(String finReference, String custCIF,
            String type) {
		logger.debug("Entering");
		JointAccountDetail jountAccountDetail = new JointAccountDetail();
		
		jountAccountDetail.setFinReference(finReference);
		jountAccountDetail.setCustCIF(custCIF);
		
		StringBuilder selectSql = new StringBuilder("Select JointAccountId, FinReference, CustCIF, IncludeRepay, RepayAccountId");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",LovDescCIFName");
		}
		selectSql.append(" From FinJointAccountDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference and CustCIF = :CustCIF ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
		RowMapper<JointAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(JointAccountDetail.class);
		
		try{
			jountAccountDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			jountAccountDetail = null;
		}
		logger.debug("Leaving");
		return jountAccountDetail;
	}


	@SuppressWarnings("serial")
    @Override
    public void deleteByFinRef(String finReference, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		JointAccountDetail jountAccountDetail = new JointAccountDetail();
		jountAccountDetail.setFinReference(finReference);
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinJointAccountDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference = :FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",String.valueOf(jountAccountDetail.getJointAccountId()),jountAccountDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",String.valueOf(jountAccountDetail.getJointAccountId()) ,jountAccountDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}


	@Override
    public List<JointAccountDetail> getJountAccountDetailByFinRef(String finReference, String type) {
		logger.debug("Entering");
		
		JointAccountDetail jountAccountDetail = new JointAccountDetail();
		jountAccountDetail.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder("Select ");
		selectSql.append(" JointAccountId, FinReference, CustCIF, IncludeRepay, RepayAccountId,");
		// selectSql.append(" PrimaryExposure, SecondaryExposure, GuarantorExposure, WorstStatus, Status");
		selectSql.append(" Version , ");
		selectSql.append(" LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(",LovDescCIFName");		
		selectSql.append(" From FinJointAccountDetails");		
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
		RowMapper<JointAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(JointAccountDetail.class);
				
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
    
	}

	
	@Override
    public List<FinanceExposure> getPrimaryExposureList(JointAccountDetail jointAccountDetail) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		RowMapper<FinanceExposure> typeRowMapper = null;
		
		StringBuilder query = new StringBuilder();		
		query.append(" SELECT T1.FinType, T6.FinTypeDesc, T1.FinReference,"); 
		query.append(" T1.FinStartDate, T1.MaturityDate, T1.FinAmount as FinanceAmt,"); 
		query.append(" T2.TotalPriBal as CurrentExpoSure, T1.FinCcy as finCcy, T7.ccyEditField as ccyEditField, T4.custStsDescription as status,"); 
		query.append(" T5.custStsDescription WorstStatus, T3.CustCIF ");
		query.append(" FROM  FinanceMain AS T1"); 
		query.append(" INNER JOIN FinPftDetails AS T2 ON T1.FinReference = T2.FinReference"); 
		query.append(" INNER JOIN Customers AS T3 ON T3.CustId = T1.CustID ");
		query.append(" LEFT JOIN BMTCustStatusCodes T4 ON T4.CustStsCode=T3.CustSts"); 
		query.append(" LEFT JOIN BMTCustStatusCodes T5 ON T5.CustStsCode=T1.FinStatus ");
		query.append(" INNER JOIN RMTFinanceTypes T6 ON T6.FinType = T1.FinType ");
		query.append(" INNER JOIN RMTCurrencies T7 ON T7.CcyCode = T1.FinCcy ");
		query.append(" WHERE T3.CustCIF=:custCIF AND T1.FinIsActive = '1'");
		query.append(" ORDER BY T1.FINSTARTDATE ASC");
		
		
		logger.debug("selectSql: " + query.toString());
		beanParameters = new BeanPropertySqlParameterSource(jointAccountDetail);
		typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceExposure.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(query.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			beanParameters = null;
			typeRowMapper = null;
			query = null;
		}
		return null;
	}

	@Override
    public List<FinanceExposure> getSecondaryExposureList(JointAccountDetail jointAccountDetail) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		RowMapper<FinanceExposure> typeRowMapper = null;
		StringBuilder query = null;
		
		query = new StringBuilder();
		query.append(" SELECT T1.FinType, T6.FinTypeDesc, T1.FinReference,"); 
		query.append(" T1.FinStartDate, T1.MaturityDate, T1.FinAmount as FinanceAmt,"); 
		query.append(" T2.TotalPriBal as CurrentExpoSure, T1.FinCcy as finCcy, T8.ccyEditField as ccyEditField, T4.custStsDescription as status,"); 
		query.append(" T5.custStsDescription WorstStatus, T3.CustCIF ");
		query.append(" FROM  FinanceMain AS T1"); 
		query.append(" INNER JOIN FinPftDetails AS T2 ON T1.FinReference = T2.FinReference"); 
		query.append(" INNER JOIN Customers AS T3 ON T3.CustId = T1.CustID ");
		query.append(" LEFT JOIN BMTCustStatusCodes T4 ON T4.CustStsCode=T3.CustSts"); 
		query.append(" LEFT JOIN BMTCustStatusCodes T5 ON T5.CustStsCode=T1.FinStatus ");
		query.append(" INNER JOIN RMTFinanceTypes T6 ON T6.FinType = T1.FinType ");
		query.append(" INNER JOIN FinJointAccountDetails_View T7  ON T7.FinReference=T1.FinReference");
		query.append(" INNER JOIN RMTCurrencies T8 ON T8.CcyCode = T1.FinCcy ");
		query.append(" WHERE T7.CustCIF=:custCIF  AND T1.FinIsActive = '1'");
		
		logger.debug("selectSql: " + query.toString());
		beanParameters = new BeanPropertySqlParameterSource(jointAccountDetail);
		typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceExposure.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(query.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			beanParameters = null;
			typeRowMapper = null;
			query = null;
		}
		return null;
	}

	@Override
    public List<FinanceExposure> getGuarantorExposureList(JointAccountDetail jointAccountDetail) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		RowMapper<FinanceExposure> typeRowMapper = null;
		StringBuilder query = null;
		
		query = new StringBuilder();
		query.append(" SELECT T1.FinType, T6.FinTypeDesc, T1.FinReference,"); 
		query.append(" T1.FinStartDate, T1.MaturityDate, T1.FinAmount as FinanceAmt,"); 
		query.append(" T2.TotalPriBal as CurrentExpoSure, T1.FinCcy as finCcy, T8.ccyEditField as ccyEditField, T4.custStsDescription as status,"); 
		query.append(" T5.custStsDescription WorstStatus, T3.CustCIF ");
		query.append(" FROM  FinanceMain AS T1"); 
		query.append(" INNER JOIN FinPftDetails AS T2 ON T1.FinReference = T2.FinReference"); 
		query.append(" INNER JOIN Customers AS T3 ON T3.CustId = T1.CustID ");
		query.append(" LEFT JOIN BMTCustStatusCodes T4 ON T4.CustStsCode=T3.CustSts"); 
		query.append(" LEFT JOIN BMTCustStatusCodes T5 ON T5.CustStsCode=T1.FinStatus ");
		query.append(" INNER JOIN RMTFinanceTypes T6 ON T6.FinType = T1.FinType ");
		query.append(" INNER JOIN FinGuarantorsDetails_View T7  ON T7.FinReference=T1.FinReference");	
		query.append(" INNER JOIN RMTCurrencies T8 ON T8.CcyCode = T1.FinCcy ");
		query.append(" WHERE T7.GuarantorCIF=:custCIF  AND T1.FinIsActive = '1'");
		
		logger.debug("selectSql: " + query.toString());
		beanParameters = new BeanPropertySqlParameterSource(jointAccountDetail);
		typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceExposure.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(query.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			beanParameters = null;
			typeRowMapper = null;
			query = null;
		}
		return null;
	}
	
	@Override
    public FinanceExposure getOverDueDetails(FinanceExposure exposer) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		RowMapper<FinanceExposure> typeRowMapper = null;
		StringBuilder query = null;
		
		query = new StringBuilder();
		query.append(" SELECT SUM(FinCurODAmt) as OverdueAmt, MAX(FinCurODDays) as PastdueDays");
		query.append(" FROM  FinanceMain FM");
		query.append(" INNER JOIN FinODDetails OD ON OD.FinReference = FM.FinReference");
		query.append(" WHERE FM.FinReference=:FinReference ");
		
		logger.debug("selectSql: " + query.toString());
		beanParameters = new BeanPropertySqlParameterSource(exposer);
		typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceExposure.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForObject(query.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			beanParameters = null;
			typeRowMapper = null;
			query = null;
		}
		return null;
	}
	
}