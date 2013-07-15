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
 * FileName    		:  CustomerAdditionalDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.customermasters.impl;

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
import com.pennant.backend.dao.customermasters.CustomerAdditionalDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerAdditionalDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CustomerAdditionalDetail model</b> class.<br>
 * 
 */
public class CustomerAdditionalDetailDAOImpl extends
		BasisCodeDAO<CustomerAdditionalDetail> implements CustomerAdditionalDetailDAO {

	private static Logger logger = Logger.getLogger(CustomerAdditionalDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new CustomerAdditionalDetail
	 * 
	 * @return CustomerAdditionalDetail
	 */
	@Override
	public CustomerAdditionalDetail getCustomerAdditionalDetail() {
		logger.debug("Entering ");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CustomerAdditionalDetail");
		CustomerAdditionalDetail customerAdditionalDetail= new CustomerAdditionalDetail();
		if (workFlowDetails!=null){
			customerAdditionalDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving ");
		return customerAdditionalDetail;
	}

	/**
	 * This method get the module from method getCustomerAdditionalDetail() and
	 * set the new record flag as true and return CustomerAdditionalDetail()
	 * 
	 * @return CustomerAdditionalDetail
	 */
	@Override
	public CustomerAdditionalDetail getNewCustomerAdditionalDetail() {
		logger.debug("Entering ");
		CustomerAdditionalDetail customerAdditionalDetail = getCustomerAdditionalDetail();
		customerAdditionalDetail.setNewRecord(true);
		logger.debug("Leaving ");
		return customerAdditionalDetail;
	}

	/**
	 * Fetch the Record  Customer Additional Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerAdditionalDetail
	 */
	@Override
	public CustomerAdditionalDetail getCustomerAdditionalDetailById(final long id, String type) {
		logger.debug("Entering ");
		CustomerAdditionalDetail customerAdditionalDetail = getCustomerAdditionalDetail();
		customerAdditionalDetail.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT CustID, CustRefStaffID, CustRefCustID, AcademicDecipline, CustAcademicLevel,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustAcademicLevelName, lovDescAcademicDeciplineName,lovDescCustCIF, " );
			selectSql.append("lovDescCustShrtName,lovDescCustRecordType,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  CustAdditionalDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID") ;
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAdditionalDetail);
		RowMapper<CustomerAdditionalDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerAdditionalDetail.class);
		
		try{
			customerAdditionalDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			customerAdditionalDetail = null;
		}
		logger.debug("Leaving ");
		return customerAdditionalDetail;
	}
	
	/**
	 * This method initialize the Record.
	 * @param CustomerAdditionalDetail (customerAdditionalDetail)
 	 * @return CustomerAdditionalDetail
	 */
	@Override
	public void initialize(CustomerAdditionalDetail customerAdditionalDetail) {
		super.initialize(customerAdditionalDetail);
	}
	
	/**
	 * This method refresh the Record.
	 * @param CustomerAdditionalDetail (customerAdditionalDetail)
 	 * @return void
	 */
	@Override
	public void refresh(CustomerAdditionalDetail customerAdditionalDetail) {
		
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the CustAdditionalDetails or CustAdditionalDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Customer Additional Details by key CustID
	 * 
	 * @param Customer Additional Details (customerAdditionalDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(CustomerAdditionalDetail customerAdditionalDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From CustAdditionalDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAdditionalDetail);
		
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", customerAdditionalDetail.getCustID(), 
					customerAdditionalDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		}catch(DataAccessException e){
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", customerAdditionalDetail.getCustID(), 
					customerAdditionalDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * This method insert new Records into CustAdditionalDetails or CustAdditionalDetails_Temp.
	 *
	 * save Customer Additional Details 
	 * 
	 * @param Customer Additional Details (customerAdditionalDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerAdditionalDetail customerAdditionalDetail,String type) {
		logger.debug("Entering ");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into CustAdditionalDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, CustAcademicLevel, AcademicDecipline, CustRefCustID, CustRefStaffID," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustAcademicLevel, :AcademicDecipline, :CustRefCustID, :CustRefStaffID, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAdditionalDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving ");
		return customerAdditionalDetail.getId();
	}
	
	/**
	 * This method updates the Record CustAdditionalDetails or CustAdditionalDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Customer Additional Details by key CustID and Version
	 * 
	 * @param Customer Additional Details (customerAdditionalDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(CustomerAdditionalDetail customerAdditionalDetail,String type) {
		logger.debug("Entering ");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update CustAdditionalDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustID = :CustID, CustAcademicLevel = :CustAcademicLevel, AcademicDecipline = :AcademicDecipline, " );
		updateSql.append(" CustRefCustID = :CustRefCustID, CustRefStaffID = :CustRefStaffID ," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where CustID =:CustID ");
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAdditionalDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41003", customerAdditionalDetail.getCustID(), 
					customerAdditionalDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving Update Method");
	}
	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, long custID, String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = String.valueOf(custID);
		parms[0][0] = PennantJavaUtil.getLabel("label_CustID")+ ":" + parms[1][0];
		
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}