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
 * FileName    		:  CheckListDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.CheckListDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CheckListDetail model</b> class.<br>
 * 
 */
public class CheckListDetailDAOImpl extends BasisNextidDaoImpl<CheckListDetail> implements CheckListDetailDAO {
	private static Logger logger = Logger.getLogger(CheckListDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public CheckListDetailDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new CheckListDetail 
	 * @return CheckListDetail
	 */
	@Override
	public CheckListDetail getCheckListDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CheckListDetail");
		CheckListDetail checkListDetail= new CheckListDetail();
		if (workFlowDetails!=null){
			checkListDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return checkListDetail;
	}

	/**
	 * This method get the module from method getCheckListDetail() and set the new record flag as true and return CheckListDetail()   
	 * @return CheckListDetail
	 */
	@Override
	public CheckListDetail getNewCheckListDetail() {
		logger.debug("Entering");
		CheckListDetail checkListDetail = getCheckListDetail();
		checkListDetail.setNewRecord(true);
		logger.debug("Leaving");
		return checkListDetail;
	}

	/**
	 * Fetch the Record  Check List Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CheckListDetail
	 */
	@Override
	public CheckListDetail getCheckListDetailById(final long id, String type) {
		logger.debug("Entering");
		CheckListDetail checkListDetail = new CheckListDetail();
		checkListDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select CheckListId, AnsSeqNo, AnsDesc, AnsCond,RemarksAllow, DocRequired,DocType, RemarksMand");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		selectSql.append(" RecordType, WorkflowId" );
		selectSql.append(" From RMTCheckListDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CheckListId =:CheckListId and AnsSeqNo = :AnsSeqNo");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkListDetail);
		RowMapper<CheckListDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CheckListDetail.class);
		
		try{
			checkListDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			checkListDetail = null;
		}
		logger.debug("Leaving");
		return checkListDetail;
	}
	
	/**
	 * Fetch the Record  Check List Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CheckListDetail
	 */
	@Override
	public CheckListDetail getCheckListDetailByDocType(String docType, String finType) {
		logger.debug("Entering");
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("DocType", docType);
		source.addValue("FinType", finType);
		source.addValue("FinRefType", FinanceConstants.PROCEDT_CHECKLIST);
		
		StringBuilder selectSql = new StringBuilder(" select * from RMTCheckListDetails where DocType=:DocType and CheckListId ");
		selectSql.append(" in (select FinRefId from LMTFinRefDetail where FinRefType=:FinRefType and  FinType=:FinType )");
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<CheckListDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CheckListDetail.class);
		
		try{
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.debug("Exception",e);
		}
		logger.debug("Leaving");
		return null;
	}
	
	public List<CheckListDetail> getCheckListDetailByChkList(final long checkListId, String type) {
		logger.debug("Entering");
		CheckListDetail checkListDetail = new CheckListDetail();
		checkListDetail.setCheckListId(checkListId);
		List<CheckListDetail>  chkListDetailList;

		StringBuilder selectSql = new StringBuilder("Select CheckListId, AnsSeqNo, AnsDesc");
		selectSql.append(", AnsCond,RemarksAllow, DocRequired,DocType, RemarksMand");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		selectSql.append(" RecordType, WorkflowId" );

		if(StringUtils.trimToEmpty(type).contains("View")){
		selectSql.append(", CategoryCode, LovDescDocCategory lovDescDocType ");
		}
		selectSql.append(" From RMTCheckListDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CheckListId =:CheckListId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkListDetail);
		RowMapper<CheckListDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CheckListDetail.class);

		try{
			chkListDetailList= this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			chkListDetailList = null;
		}
		logger.debug("Leaving");
		return chkListDetailList;
	}
	
	public List<CheckListDetail> getCheckListDetailByChkList(final Map<String, Set<Long>> checkListIdMap, String type) {
		logger.debug("Entering");
		List<CheckListDetail>  chkListDetailList;

		StringBuilder selectSql = new StringBuilder("Select CheckListId, AnsSeqNo, AnsDesc");
		selectSql.append(", AnsCond,RemarksAllow, DocRequired,DocType, RemarksMand");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		selectSql.append(" RecordType, WorkflowId" );

		if(StringUtils.trimToEmpty(type).contains("View")){
		selectSql.append(", CategoryCode, LovDescDocCategory lovDescDocType ");
		}
		selectSql.append(" From RMTCheckListDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CheckListId IN (:checkListIdMap) Order By CheckListId, AnsSeqNo");

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource beanParameters = new MapSqlParameterSource(checkListIdMap);
		RowMapper<CheckListDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CheckListDetail.class);

		try{
			chkListDetailList= this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			chkListDetailList = null;
		}
		logger.debug("Leaving");
		return chkListDetailList;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the RMTCheckListDetails or RMTCheckListDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Check List Details by key CheckListId
	 * 
	 * @param Check List Details (checkListDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(CheckListDetail checkListDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From RMTCheckListDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CheckListId =:CheckListId and AnsSeqNo = :AnsSeqNo");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkListDetail);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method Deletes the Record from the RMTCheckListDetail or RMTCheckListDetail_Temp.

	 * delete Educational Expenses by key loanRefNumber
	 * 
	 */
	public void delete(long checkListId,String type) {
		logger.debug("Entering");
		CheckListDetail checkListDetail = new CheckListDetail();
		checkListDetail.setCheckListId(checkListId);

		StringBuilder deleteSql =new StringBuilder();
		deleteSql.append("Delete From RMTCheckListDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CheckListId =:CheckListId");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkListDetail);
		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(checkListDetail.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_AnsSeqNo")+":"+valueParm[0];
		logger.debug("DeleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into RMTCheckListDetails or RMTCheckListDetails_Temp.
	 *
	 * save Check List Details 
	 * 
	 * @param Check List Details (checkListDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CheckListDetail checkListDetail,String type) {
		logger.debug("Entering");
		StringBuilder insertSql =new StringBuilder("Insert Into RMTCheckListDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CheckListId, AnsSeqNo, AnsDesc, AnsCond,RemarksAllow, DocRequired, DocType, RemarksMand");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		insertSql.append(", RecordType, WorkflowId)");
		insertSql.append(" Values( :CheckListId, :AnsSeqNo, :AnsDesc, :AnsCond, :RemarksAllow, :DocRequired,:DocType, :RemarksMand");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId ");
		insertSql.append(", :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkListDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return checkListDetail.getId();
	}
	
	/**
	 * This method updates the Record RMTCheckListDetails or RMTCheckListDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Check List Details by key CheckListId and Version
	 * 
	 * @param Check List Details (checkListDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CheckListDetail checkListDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update RMTCheckListDetails");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set AnsDesc = :AnsDesc, AnsCond = :AnsCond");
		updateSql.append(", RemarksAllow = :RemarksAllow, DocRequired = :DocRequired,DocType=:DocType, RemarksMand = :RemarksMand");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus");
		updateSql.append(", RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		updateSql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CheckListId =:CheckListId and AnsSeqNo = :AnsSeqNo");
		
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkListDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}