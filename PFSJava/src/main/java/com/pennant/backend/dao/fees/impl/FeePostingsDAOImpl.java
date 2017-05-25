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
package com.pennant.backend.dao.fees.impl;

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
import com.pennant.backend.dao.fees.FeePostingsDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>VASRecording model</b> class.<br>
 * 
 */

public class FeePostingsDAOImpl extends BasisNextidDaoImpl<FeePostings> implements FeePostingsDAO {

	private static Logger logger = Logger.getLogger(FeePostingsDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	
	
	@Override
	public FeePostings getFeePostings() {
		logger.debug("Entering");
		FeePostings feePostings = new FeePostings();
		logger.debug("Leaving");
		return feePostings;
	}

	@Override
	public FeePostings getNewFeePostings() {
		logger.debug("Entering");
		FeePostings feePostings = new FeePostings();
		feePostings.setNewRecord(true);
		logger.debug("Leaving");
		return feePostings;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	

	@Override
	public FeePostings getFeePostingsById(long postId, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder(
				"Select PostId, PostAgainst, Reference, FeeTyeCode, PostingAmount, PostDate, ValueDate,Remarks,PartnerBankId,");
		sql.append(" Version , LastMntBy, LastMntOn,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", partnerBankName,partnerBankAc,partnerBankAcType,accountSetId");
		}
		sql.append(" From FeePostings");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PostId =:PostId");
		logger.debug("selectSql: " + sql.toString());

		RowMapper<FeePostings> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FeePostings.class);

		source = new MapSqlParameterSource();
		source.addValue("PostId", postId);
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


	@Override
	public void save(FeePostings feePostings, String type) {

		logger.debug("Entering");
		if (feePostings.getId() == Long.MIN_VALUE) {
			feePostings.setId(getNextId("SeqFeePostings"));
		}
		
		StringBuilder insertSql =new StringBuilder();
		insertSql.append("Insert Into FeePostings");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (PostId, PostAgainst, Reference, FeeTyeCode, PostingAmount, PostDate, ValueDate,Remarks,PartnerBankId,");
		insertSql.append("  Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append("	TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append("  Values(:PostId, :PostAgainst, :Reference, :FeeTyeCode, :PostingAmount, :PostDate, :ValueDate,:Remarks,:PartnerBankId,");
		insertSql.append("  :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feePostings);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	
	}
	
	@Override
	@SuppressWarnings("serial")
	public void update(FeePostings feePostings, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FeePostings");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set PostId = :PostId, PostAgainst = :PostAgainst, Reference = :Reference, FeeTyeCode = :FeeTyeCode, PostingAmount = :PostingAmount, PostDate = :PostDate, ");
		updateSql.append(" ValueDate = :ValueDate, Remarks = :Remarks, PartnerBankId = :PartnerBankId,");
		updateSql.append(" Version= :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where PostId= :PostId");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feePostings);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",String.valueOf(feePostings.getPostId()) ,feePostings.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	@Override
	@SuppressWarnings("serial")
	public void delete(FeePostings feePostings, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From feePostings");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PostId =:PostId");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feePostings);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", String.valueOf(feePostings.getPostId()),
						feePostings.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006", String.valueOf(feePostings.getPostId()),
					feePostings.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String potId, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = potId;
		parms[0][0] = PennantJavaUtil.getLabel("label_feePostingsDialog_PostingAgainst.value")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
				parms[0],parms[1]), userLanguage);
	}
	
}