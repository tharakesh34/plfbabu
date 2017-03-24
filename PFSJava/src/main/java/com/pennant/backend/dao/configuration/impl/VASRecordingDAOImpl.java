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
 * FileName    		:  VASRecordingDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2016    														*
 *                                                                  						*
 * Modified Date    :  02-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.configuration.impl;

import java.util.ArrayList;
import java.util.List;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.VASConsatnts;

/**
 * DAO methods implementation for the <b>VASRecording model</b> class.<br>
 * 
 */

public class VASRecordingDAOImpl extends BasisCodeDAO<VASRecording> implements VASRecordingDAO {

	private static Logger logger = Logger.getLogger(VASRecordingDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public VASRecordingDAOImpl() {
		super();
	}
 
	/**
	 * This method set the Work Flow id based on the module name and return the new VASRecording 
	 * @return VASRecording
	 */

	@Override
	public VASRecording getVASRecording() {
		logger.debug("Entering");
		logger.debug("Leaving");
		return new VASRecording();
	}
 
	/**
	 * This method get the module from method getVASRecording() and set the new record flag as true and return VASRecording()   
	 * @return VASRecording
	 */
	@Override
	public VASRecording getNewVASRecording() {
		logger.debug("Entering");
		VASRecording vASRecording = getVASRecording();
		vASRecording.setNewRecord(true);
		logger.debug("Leaving");
		return vASRecording;
	}

	/**
	 * Fetch the Record VASRecording details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VASRecording
	 */
	@Override
	public VASRecording getVASRecordingByReference(String vasReference, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder("Select ProductCode, PostingAgainst, PrimaryLinkRef, VasReference, Fee, RenewalFee, FeePaymentMode,");
		sql.append(" ValueDate, AccrualTillDate, RecurringDate, DsaId, DmaId, FulfilOfficerId, ReferralId, Version, LastMntBy, LastMntOn,");
		sql.append(" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,VasStatus,FinanceProcess");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", ProductDesc, DsaIdDesc, DmaIDDesc, FulfilOfficerIdDesc, ReferralIdDesc ");
			sql.append(", ProductType, ProductTypeDesc, ProductCtg, ProductCtgDesc, ManufacturerDesc ");
		}
		sql.append(" From VASRecording");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where VasReference =:VasReference");
		logger.debug("selectSql: " + sql.toString());

		RowMapper<VASRecording> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VASRecording.class);

		source = new MapSqlParameterSource();
		source.addValue("VasReference", vasReference);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		} finally {
			source = null;
			sql = null;
		}
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Fetch the Record VASRecording details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VASRecording
	 */
	@Override
	public List<VASRecording> getVASRecordingsByLinkRef(String primaryLinkRef, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;
		List<VASRecording> vasRecordingList = new ArrayList<>();

		sql = new StringBuilder("Select ProductCode, PostingAgainst, PrimaryLinkRef, VasReference, Fee, RenewalFee, FeePaymentMode,");
		sql.append(" ValueDate, AccrualTillDate, RecurringDate, DsaId, DmaId, FulfilOfficerId, ReferralId, Version, LastMntBy, LastMntOn,");
		sql.append(" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,VasStatus,FinanceProcess");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", ProductDesc, DsaIdDesc, DmaIDDesc, FulfilOfficerIdDesc, ReferralIdDesc ");
			sql.append(", ProductType, ProductTypeDesc, ProductCtg, ProductCtgDesc, ManufacturerDesc ");
		}
		sql.append(" From VASRecording");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PrimaryLinkRef =:PrimaryLinkRef");
		logger.debug("selectSql: " + sql.toString());

		RowMapper<VASRecording> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VASRecording.class);

		source = new MapSqlParameterSource();
		source.addValue("PrimaryLinkRef", primaryLinkRef);
		vasRecordingList = this.namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
		logger.debug("Leaving");
		return vasRecordingList;
	}
	
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the VASRecording or VASRecording_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete VASRecording by key ProductCode
	 * 
	 * @param VASRecording (vASRecording)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(VASRecording vASRecording,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From VASRecording");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where VasReference =:VasReference");
	
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASRecording);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",vASRecording.getVasReference() ,vASRecording.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006",vASRecording.getVasReference() ,vASRecording.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method Deletes the Record from the VASRecording or VASRecording_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete VASRecording by key ProductCode
	 * 
	 * @param VASRecording (vASRecording)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteByPrimaryLinkRef(String primaryLinkRef,String type) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PrimaryLinkRef", primaryLinkRef);
		
		StringBuilder deleteSql = new StringBuilder("Delete From VASRecording");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PrimaryLinkRef =:PrimaryLinkRef");
	
		logger.debug("deleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into VASRecording or VASRecording_Temp.
	 *
	 * save VASRecording 
	 * 
	 * @param VASRecording (vASRecording)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(VASRecording vASRecording, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder();
		insertSql.append("Insert Into VASRecording");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ProductCode, PostingAgainst, PrimaryLinkRef, VasReference, Fee, RenewalFee, FeePaymentMode, ValueDate, AccrualTillDate, RecurringDate,");
		insertSql.append("  DsaId, DmaId, FulfilOfficerId, ReferralId, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append("	TaskId, NextTaskId, RecordType, WorkflowId,VasStatus,FinanceProcess)");
		insertSql.append("  Values(:ProductCode, :PostingAgainst, :PrimaryLinkRef, :VasReference, :Fee, :RenewalFee, :FeePaymentMode, :ValueDate, :AccrualTillDate,");
		insertSql.append("  :RecurringDate, :DsaId, :DmaId, :FulfilOfficerId, :ReferralId,");
		insertSql.append("  :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId,:VasStatus,:FinanceProcess)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASRecording);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vASRecording.getVasReference();
	}
	
	/**
	 * This method updates the Record VASRecording or VASRecording_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update VASRecording by key ProductCode and Version
	 * 
	 * @param VASRecording (vASRecording)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(VASRecording vASRecording,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update VASRecording");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ProductCode = :ProductCode, PostingAgainst = :PostingAgainst, PrimaryLinkRef = :PrimaryLinkRef, VasReference = :VasReference, Fee = :Fee, RenewalFee = :RenewalFee, FeePaymentMode = :FeePaymentMode, ");
		updateSql.append(" ValueDate = :ValueDate, AccrualTillDate = :AccrualTillDate, RecurringDate = :RecurringDate, DsaId = :DsaId, DmaId = :DmaId, FulfilOfficerId = :FulfilOfficerId, ReferralId = :ReferralId,");
		updateSql.append(" Version= :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId,VasStatus = :VasStatus,FinanceProcess =:FinanceProcess");
		updateSql.append(" Where VasReference = :VasReference");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASRecording);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",vASRecording.getVasReference() ,vASRecording.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails  getError(String errorId, String finReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_VasReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
				parms[0],parms[1]), userLanguage);
	}

	/*
	 * Check whether reference is exists or not
	 */
	@Override
	public boolean isVasReferenceExists(String reference, String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select Count(VasReference) from VASRecording");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where VasReference = :VasReference");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("VasReference", reference);
		try {
			if (this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return false;
	}

	

	@Override
	public boolean updateVasReference(long oldReference, long newReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" UPDATE  SeqVasReference  SET Seqno = :newReference Where Seqno = :oldReference");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("newReference", newReference);
		source.addValue("oldReference", oldReference);

		try {
			if (this.namedParameterJdbcTemplate.update(sql.toString(), source) == 1) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			source = null;
			sql = null;
		}
		logger.debug("Leaving");
		return false;
	}


	@Override
	public String getCustomerCif(String primaryLinkRef, String postingAgainst) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;
		String custCif = null;

		sql = new StringBuilder();
		if (VASConsatnts.VASAGAINST_FINANCE.equals(postingAgainst)) {
			sql.append(" Select CU.CustCIF from FinanceMain FM Inner Join Customers CU ON FM.CustID = CU.CustID");
			sql.append(" Where FM.FinReference = :PrimaryLinkRef");
		} else if (VASConsatnts.VASAGAINST_COLLATERAL.equals(postingAgainst)) {
			sql.append(" Select CU.CustCIF from CollateralSetup CO Inner Join Customers CU ON CO.DepositorId = CU.CustID");
			sql.append(" Where CO.CollateralRef = :PrimaryLinkRef");
		}

		logger.debug("Sql: " + sql.toString());
		
		try {
			if(StringUtils.isNotEmpty(sql.toString())){
				
				source = new MapSqlParameterSource();
				source.addValue("PrimaryLinkRef", primaryLinkRef);
				
				custCif = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, String.class);
			}
		} catch (Exception e) {
			logger.error(e);
			custCif = null;
		} finally {
			source = null;
			sql = null;
		}
		logger.debug("Leaving");
		return custCif;
	}

}