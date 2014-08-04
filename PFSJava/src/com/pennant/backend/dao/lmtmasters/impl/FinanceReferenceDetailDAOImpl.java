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
 * FileName    		:  FinanceReferenceDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-11-2011    														*
 *                                                                  						*
 * Modified Date    :  26-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-11-2011       Pennant	                 0.1                                            * 
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>FinanceReferenceDetail model</b> class.<br>
 * 
 */

public class FinanceReferenceDetailDAOImpl extends BasisNextidDaoImpl<FinanceReferenceDetail> implements FinanceReferenceDetailDAO {

	private static Logger logger = Logger.getLogger(FinanceReferenceDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new FinanceReferenceDetail
	 * 
	 * @return FinanceReferenceDetail
	 */

	@Override
	public FinanceReferenceDetail getFinanceReferenceDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceReferenceDetail");
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		if (workFlowDetails != null) {
			financeReferenceDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return financeReferenceDetail;
	}

	/**
	 * This method get the module from method getFinanceReferenceDetail() and
	 * set the new record flag as true and return FinanceReferenceDetail()
	 * 
	 * @return FinanceReferenceDetail
	 */

	@Override
	public FinanceReferenceDetail getNewFinanceReferenceDetail() {
		logger.debug("Entering");
		FinanceReferenceDetail financeReferenceDetail = getFinanceReferenceDetail();
		financeReferenceDetail.setNewRecord(true);
		logger.debug("Leaving");
		return financeReferenceDetail;
	}

	/**
	 * Fetch the Record Finance Reference Details details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceReferenceDetail
	 */
	@Override
	public FinanceReferenceDetail getFinanceReferenceDetailById(final long id, String type) {
		logger.debug("Entering");
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();

		financeReferenceDetail.setId(id);

		StringBuilder selectSql = new StringBuilder("Select FinRefDetailId, FinType, FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage");
		selectSql.append(",OverRide,OverRideValue, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescFinTypeDescName");
		}

		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinRefDetailId =:FinRefDetailId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		RowMapper<FinanceReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceReferenceDetail.class);

		try {
			financeReferenceDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			financeReferenceDetail = null;
		}
		logger.debug("Leaving");
		return financeReferenceDetail;
	}

	/**
	 * Fetch Records Details by Finance Type, Reference Type and field
	 * 
	 * @param finance
	 *            Type (String)
	 * @param reference
	 *            Type (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return List<FinanceReferenceDetail>
	 */
	@Override
	public List<FinanceReferenceDetail> getFinanceReferenceDetail(final String financeType, String roleCode, String type) {
		logger.debug("Entering");
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();

		financeReferenceDetail.setFinType(financeType);
		financeReferenceDetail.setShowInStage(roleCode);
		StringBuilder selectSql = new StringBuilder("Select FinRefDetailId, FinType,FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage");
		selectSql.append(",OverRide,OverRideValue, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescFinTypeDescName, lovDescRefDesc");
		}

		if (StringUtils.trimToEmpty(type).contains("_AAView") || StringUtils.trimToEmpty(type).contains("_TAView")) {
			selectSql.append(",lovDescAggReportName,lovDescAggReportPath,lovDescCodelov, lovDescNamelov, lovDescAggImage");
		} else if (StringUtils.trimToEmpty(type).equals("_AEView") || StringUtils.trimToEmpty(type).equals("_TEView")) {
			selectSql.append(",lovDescElgRuleValue,lovDescCodelov, lovDescNamelov");
		} else if (StringUtils.trimToEmpty(type).equals("_ASGView") || StringUtils.trimToEmpty(type).equals("_TSGView")) {
			selectSql.append(",lovDescminScore,lovDescisoverride,lovDescoverrideScore,lovDescCodelov, lovDescNamelov");
		} else if (StringUtils.trimToEmpty(type).equals("_ACSGView") || StringUtils.trimToEmpty(type).equals("_TCSGView")) {
			selectSql.append(",lovDescminScore,lovDescisoverride,lovDescoverrideScore,lovDescCodelov, lovDescNamelov");
		} else if (StringUtils.trimToEmpty(type).equals("_AQView") || StringUtils.trimToEmpty(type).equals("_TQView")) {
			selectSql.append(",lovDescIsRemarksAllowed,lovDescCheckMinCount,lovDescCheckMaxCount, lovDescElgRuleValue ");
		} else if (StringUtils.trimToEmpty(type).equals("_ACView") || StringUtils.trimToEmpty(type).equals("_TCView")) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if (StringUtils.trimToEmpty(type).equals("_ATView") || StringUtils.trimToEmpty(type).equals("_TTView")) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		}
		
		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType");
		if(!StringUtils.trimToEmpty(roleCode).equals("")){
			selectSql.append(" AND ShowInStage LIKE '%"+roleCode +",%' ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		RowMapper<FinanceReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceReferenceDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}
	
	/**
	 * Fetch Records Details by Finance Type, Reference Type and field
	 * 
	 * @param finance
	 *            Type (String)
	 * @param reference
	 *            Type (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return List<FinanceReferenceDetail>
	 */
	@Override
	public List<FinanceReferenceDetail> getAgreementListByCode(String aggCodes) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(" select AggReportName AS lovDescAggReportName, AggName AS lovDescNamelov, " );
		selectSql.append(" AggImage AS lovDescAggImage  from BMTAggrementDef " );
		selectSql.append(" WHERE AggCode IN("+aggCodes+") ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new FinanceReferenceDetail());
		RowMapper<FinanceReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceReferenceDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}
	
	/**
	 * Fetch Records Details by Finance Type and Role Code
	 * @param financeType
	 * @param roleCode
	 * @return
	 */
	@Override
	public List<Long> getMailTemplatesByFinType(final String financeType, String roleCode) {
		logger.debug("Entering");
		
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		financeReferenceDetail.setFinType(financeType);
		
		StringBuilder selectSql = new StringBuilder("Select FinRefId From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty("_ATView"));
		selectSql.append(" Where FinType =:FinType");
		if(!StringUtils.trimToEmpty(roleCode).equals("")){
			selectSql.append(" AND ShowInStage LIKE '%"+roleCode +",%' ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		RowMapper<Long> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Long.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param FinanceReferenceDetail
	 *            (financeReferenceDetail)
	 * @return FinanceReferenceDetail
	 */
	@Override
	public void initialize(FinanceReferenceDetail financeReferenceDetail) {
		super.initialize(financeReferenceDetail);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinanceReferenceDetail
	 *            (financeReferenceDetail)
	 * @return void
	 */
	@Override
	public void refresh(FinanceReferenceDetail financeReferenceDetail) {

	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the LMTFinRefDetail or
	 * LMTFinRefDetail_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Finance Reference Details by
	 * key FinRefDetailId
	 * 
	 * @param Finance
	 *            Reference Details (financeReferenceDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FinanceReferenceDetail financeReferenceDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From LMTFinRefDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinRefDetailId =:FinRefDetailId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", financeReferenceDetail.getId(), financeReferenceDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006", financeReferenceDetail.getId(), financeReferenceDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into LMTFinRefDetail or
	 * LMTFinRefDetail_Temp. it fetches the available Sequence form
	 * SeqLMTFinRefDetail by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Finance Reference Details
	 * 
	 * @param Finance
	 *            Reference Details (financeReferenceDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(FinanceReferenceDetail financeReferenceDetail, String type) {
		logger.debug("Entering");
		if (financeReferenceDetail.getId() == Long.MIN_VALUE) {
			financeReferenceDetail.setId(getNextidviewDAO().getNextId("SeqLMTFinRefDetail"));
			logger.debug("get NextID:" + financeReferenceDetail.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into LMTFinRefDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinRefDetailId, FinType, FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage,OverRide,OverRideValue");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinRefDetailId, :FinType, :FinRefType, :FinRefId, :IsActive, :ShowInStage, :MandInputInStage, :AllowInputInStage,:OverRide,:OverRideValue");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeReferenceDetail.getId();
	}

	/**
	 * This method updates the Record LMTFinRefDetail or LMTFinRefDetail_Temp.
	 * if Record not updated then throws DataAccessException with error 41004.
	 * update Finance Reference Details by key FinRefDetailId and Version
	 * 
	 * @param Finance
	 *            Reference Details (financeReferenceDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	@Override
	public void update(FinanceReferenceDetail financeReferenceDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update LMTFinRefDetail");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
				.append(" Set FinRefDetailId = :FinRefDetailId, FinType = :FinType, FinRefType = :FinRefType, FinRefId = :FinRefId, IsActive = :IsActive, ShowInStage = :ShowInStage, MandInputInStage = :MandInputInStage, AllowInputInStage = :AllowInputInStage,OverRide=:OverRide,OverRideValue =:OverRideValue");
		updateSql
				.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinRefDetailId =:FinRefDetailId");

		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeReferenceDetail.getId(), financeReferenceDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails getError(String errorId, long finRefDetailId, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = String.valueOf(finRefDetailId);
		parms[0][0] = PennantJavaUtil.getLabel("label_FinRefDetailId") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]), userLanguage);
	}

	/**
	 * Fetch Records Details by Finance Type, Reference Type and field
	 * 
	 * @param finance
	 *            Type (String)
	 * @param reference
	 *            Type (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return List<FinanceReferenceDetail>
	 */
	@Override
	public List<FinanceReferenceDetail> getFinRefDetByRoleAndFinType(final String financeType, String MandInputInStage,
			List<String> groupIds, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("Select FinRefDetailId, FinType,FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage");
		selectSql.append(",OverRide,OverRideValue, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).equals("_AEView")) {
			selectSql.append(",lovDescElgRuleValue,lovDescCodelov, lovDescNamelov,lovDescFinTypeDescName, lovDescRefDesc,lovDescFinCcyCode, lovDescProductCodeName,lovDescRuleReturnType ");
		} else if (StringUtils.trimToEmpty(type).equals("_ASGView") || StringUtils.trimToEmpty(type).equals("_ACSGView")) {
			selectSql.append(",lovDescminScore,lovDescisoverride,lovDescoverrideScore,lovDescCodelov, lovDescNamelov,lovDescFinTypeDescName, lovDescRefDesc ");
		}
		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where " );
		
		if(!StringUtils.trimToEmpty(financeType).equals("")){
			selectSql.append(" FinType =:FinType AND ");
		}
		if(groupIds != null && groupIds.size() > 0){
			selectSql.append(" FinRefId NOT IN (:GroupIds) AND ");
		}
		
		if(MandInputInStage != null){
			if(type.equals("_AEView")){
				selectSql.append(" AllowInputInStage like '%" + MandInputInStage + ",%' AND ");
			}else{
				selectSql.append(" MandInputInStage like '%" + MandInputInStage + ",%' AND ");
			}	
		}
		selectSql.append(" IsActive = 1 ");
		
		Map<String, List<String>> parameterMap=new HashMap<String, List<String>>();
		List<String> fintype = new ArrayList<String>();
		fintype.add(financeType);
		parameterMap.put("GroupIds", groupIds);
		parameterMap.put("FinType",fintype);
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceReferenceDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), parameterMap, typeRowMapper);

	}
	@Override
	public void deleteByFinType(String finType, String type) {
		logger.debug("Entering");
		FinanceReferenceDetail financeReferenceDetail=new FinanceReferenceDetail();
		financeReferenceDetail.setFinType(finType);
		StringBuilder deleteSql = new StringBuilder("Delete From LMTFinRefDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
	}
}