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
 * FileName    		:  DirectorDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.customermasters.DirectorDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>DirectorDetail model</b> class.<br>
 * 
 */
public class DirectorDetailDAOImpl extends BasisNextidDaoImpl<DirectorDetail> 
									implements DirectorDetailDAO {

	private static Logger logger = Logger.getLogger(DirectorDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new DirectorDetail 
	 * @return DirectorDetail
	 */
	@Override
	public DirectorDetail getDirectorDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("DirectorDetail");
		DirectorDetail directorDetail= new DirectorDetail();
		if (workFlowDetails!=null){
			directorDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return directorDetail;
	}

	/**
	 * This method get the module from method getDirectorDetail() and set the
	 * new record flag as true and return DirectorDetail()
	 * 
	 * @return DirectorDetail
	 */
	@Override
	public DirectorDetail getNewDirectorDetail() {
		logger.debug("Entering");
		DirectorDetail directorDetail = getDirectorDetail();
		directorDetail.setNewRecord(true);
		logger.debug("Leaving");
		return directorDetail;
	}

	/**
	 * Fetch the Record  Director Detail details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DirectorDetail
	 */
	@Override
	public DirectorDetail getDirectorDetailById(final long id, String type) {
		logger.debug("Entering");
		DirectorDetail directorDetail = getDirectorDetail();
		
		directorDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select DirectorId, CustID, FirstName," );
		selectSql.append(" MiddleName, LastName, ShortName, CustGenderCode, CustSalutationCode," );
		selectSql.append(" CustAddrHNbr, CustFlatNbr, CustAddrStreet, CustAddrLine1, CustAddrLine2," );
		selectSql.append(" CustPOBox, CustAddrCity, CustAddrProvince, CustAddrCountry, CustAddrZIP," );
		selectSql.append(" CustAddrPhone, CustAddrFrom,");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescCustGenderCodeName,lovDescCustSalutationCodeName," );
			selectSql.append("lovDescCustAddrCityName,lovDescCustAddrProvinceName,lovDescCustCIF," );
			selectSql.append(" lovDescCustAddrCountryName, lovDescCustRecordType , lovDescCustShrtName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From CustomerDirectorDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DirectorId =:DirectorId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(directorDetail);
		RowMapper<DirectorDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				DirectorDetail.class);
		
		try{
			directorDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			directorDetail = null;
		}
		logger.debug("Leaving");
		return directorDetail;
	}
	
	/**
	 * Fetch the Record  Director Detail List by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return List<DirectorDetail>
	 */
	@Override
	public List<DirectorDetail> getCustomerDirectorByCustomer(final long id, String type) {
		logger.debug("Entering");
		DirectorDetail directorDetail = getDirectorDetail();
		directorDetail.setCustID(id);
		
		StringBuilder selectSql = new StringBuilder("Select DirectorId, CustID, FirstName," );
		selectSql.append(" MiddleName, LastName, ShortName, CustGenderCode, CustSalutationCode," );
		selectSql.append(" CustAddrHNbr, CustFlatNbr, CustAddrStreet, CustAddrLine1, CustAddrLine2," );
		selectSql.append(" CustPOBox, CustAddrCity, CustAddrProvince, CustAddrCountry, CustAddrZIP," );
		selectSql.append(" CustAddrPhone, CustAddrFrom,");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescCustGenderCodeName,lovDescCustSalutationCodeName," );
			selectSql.append("lovDescCustAddrCityName,lovDescCustAddrProvinceName,lovDescCustCIF," );
			selectSql.append(" lovDescCustAddrCountryName, lovDescCustRecordType , lovDescCustShrtName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From CustomerDirectorDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID =:CustID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(directorDetail);
		RowMapper<DirectorDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				DirectorDetail.class);
		
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),beanParameters, typeRowMapper);	
	}
	
	/**
	 * This method initialize the Record.
	 * @param DirectorDetail (directorDetail)
 	 * @return DirectorDetail
	 */
	@Override
	public void initialize(DirectorDetail directorDetail) {
		super.initialize(directorDetail);
	}
	
	/**
	 * This method refresh the Record.
	 * @param DirectorDetail (directorDetail)
 	 * @return void
	 */
	@Override
	public void refresh(DirectorDetail directorDetail) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the CustomerDirectorDetail or CustomerDirectorDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Director Detail by key DirectorId
	 * 
	 * @param Director Detail (directorDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(DirectorDetail directorDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From CustomerDirectorDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DirectorId =:DirectorId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(directorDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",directorDetail.getId() ,
						directorDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",directorDetail.getId() ,
					directorDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method Deletes the Records from the CustomerDirectorDetail or CustomerDirectorDetail_Temp.
	 * depend on CustomerID
	 * 
	 * @param int(customerId)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(final long customerId,String type) {
		logger.debug("Entering");
		
		DirectorDetail directorDetail = getDirectorDetail();
		directorDetail.setCustID(customerId);
		
		StringBuilder deleteSql = new StringBuilder("Delete From CustomerDirectorDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(directorDetail);
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		
	}
	/**
	 * This method insert new Records into CustomerDirectorDetail or CustomerDirectorDetail_Temp.
	 * it fetches the available Sequence form SeqCustomerDirectorDetail by using 
	 * 	getNextidviewDAO().getNextId() method.  
	 *
	 * save Director Detail 
	 * 
	 * @param Director Detail (directorDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(DirectorDetail directorDetail,String type) {
		logger.debug("Entering");
		if (directorDetail.getId()==Long.MIN_VALUE){
			directorDetail.setId(getNextidviewDAO().getNextId("SeqCustomerDirectorDetail"));
			logger.debug("get NextID:"+directorDetail.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into CustomerDirectorDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (DirectorId, CustID, FirstName, MiddleName, LastName, ShortName," );
		insertSql.append(" CustGenderCode, CustSalutationCode, CustAddrHNbr, CustFlatNbr, CustAddrStreet," );
		insertSql.append(" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince," );
		insertSql.append(" CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom ,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:DirectorId, :CustID, :FirstName, :MiddleName, :LastName," );
		insertSql.append(" :ShortName, :CustGenderCode, :CustSalutationCode, :CustAddrHNbr," );
		insertSql.append(" :CustFlatNbr, :CustAddrStreet, :CustAddrLine1, :CustAddrLine2, :CustPOBox," );
		insertSql.append(" :CustAddrCity, :CustAddrProvince, :CustAddrCountry, :CustAddrZIP," );
		insertSql.append(" :CustAddrPhone, :CustAddrFrom,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(directorDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return directorDetail.getId();
	}
	
	/**
	 * This method updates the Record CustomerDirectorDetail or CustomerDirectorDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Director Detail by key DirectorId and Version
	 * 
	 * @param Director Detail (directorDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(DirectorDetail directorDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update CustomerDirectorDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set DirectorId = :DirectorId, CustID = :CustID, FirstName = :FirstName," );
		updateSql.append(" MiddleName = :MiddleName, LastName = :LastName, ShortName = :ShortName," );
		updateSql.append(" CustGenderCode = :CustGenderCode, CustSalutationCode = :CustSalutationCode,");
		updateSql.append(" CustAddrHNbr = :CustAddrHNbr, CustFlatNbr = :CustFlatNbr," );
		updateSql.append(" CustAddrStreet = :CustAddrStreet, CustAddrLine1 = :CustAddrLine1," );
		updateSql.append(" CustAddrLine2 = :CustAddrLine2, CustPOBox = :CustPOBox," );
		updateSql.append(" CustAddrCity = :CustAddrCity, CustAddrProvince = :CustAddrProvince," );
		updateSql.append(" CustAddrCountry = :CustAddrCountry, CustAddrZIP = :CustAddrZIP," );
		updateSql.append(" CustAddrPhone = :CustAddrPhone, CustAddrFrom = :CustAddrFrom,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where DirectorId =:DirectorId");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(directorDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",directorDetail.getId() ,
					directorDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, long DirectorId, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = String.valueOf(DirectorId);
		parms[0][0] = PennantJavaUtil.getLabel("label_DirectorId")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

	
}