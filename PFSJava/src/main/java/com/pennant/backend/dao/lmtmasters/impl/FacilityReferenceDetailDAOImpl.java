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
 * FileName    		:  FacilityReferenceDetailDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.lmtmasters.FacilityReferenceDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FacilityReferenceDetail model</b> class.<br>
 * 
 */

public class FacilityReferenceDetailDAOImpl extends BasisNextidDaoImpl<FacilityReferenceDetail> implements FacilityReferenceDetailDAO {

	private static Logger logger = Logger.getLogger(FacilityReferenceDetailDAOImpl.class);

	public FacilityReferenceDetailDAOImpl() {
		super();
	}
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new FacilityReferenceDetail
	 * 
	 * @return FacilityReferenceDetail
	 */

	@Override
	public FacilityReferenceDetail getFacilityReferenceDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FacilityReferenceDetail");
		FacilityReferenceDetail facilityReferenceDetail = new FacilityReferenceDetail();
		if (workFlowDetails != null) {
			facilityReferenceDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return facilityReferenceDetail;
	}

	/**
	 * This method get the module from method getFacilityReferenceDetail() and
	 * set the new record flag as true and return FacilityReferenceDetail()
	 * 
	 * @return FacilityReferenceDetail
	 */

	@Override
	public FacilityReferenceDetail getNewFacilityReferenceDetail() {
		logger.debug("Entering");
		FacilityReferenceDetail facilityReferenceDetail = getFacilityReferenceDetail();
		facilityReferenceDetail.setNewRecord(true);
		logger.debug("Leaving");
		return facilityReferenceDetail;
	}

	/**
	 * Fetch the Record Finance Reference Details details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FacilityReferenceDetail
	 */
	@Override
	public FacilityReferenceDetail getFacilityReferenceDetailById(final long id, String type) {
		logger.debug("Entering");
		FacilityReferenceDetail facilityReferenceDetail = getFacilityReferenceDetail();

		facilityReferenceDetail.setId(id);

		StringBuilder selectSql = new StringBuilder("Select FinRefDetailId, FinType, FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage");
		selectSql.append(",OverRide,OverRideValue, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescFinTypeDescName");
		}

		selectSql.append(" From LMTFacilityRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinRefDetailId =:FinRefDetailId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityReferenceDetail);
		RowMapper<FacilityReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FacilityReferenceDetail.class);

		try {
			facilityReferenceDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			facilityReferenceDetail = null;
		}
		logger.debug("Leaving");
		return facilityReferenceDetail;
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
	 * @return List<FacilityReferenceDetail>
	 */
	@Override
	public List<FacilityReferenceDetail> getFacilityReferenceDetail(final String financeType, String roleCode, String type) {
		logger.debug("Entering");
		FacilityReferenceDetail facilityReferenceDetail = getFacilityReferenceDetail();

		facilityReferenceDetail.setFinType(financeType);
		facilityReferenceDetail.setShowInStage(roleCode);
		StringBuilder selectSql = new StringBuilder("Select FinRefDetailId, FinType,FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage");
		selectSql.append(",OverRide,OverRideValue, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescFinTypeDescName, lovDescRefDesc");
		}

		if (StringUtils.trimToEmpty(type).contains("_AAView") || StringUtils.trimToEmpty(type).contains("_TAView")) {
			selectSql.append(",lovDescAggReportName,lovDescAggReportPath,lovDescCodelov, lovDescNamelov, lovDescAggImage");
		} else if ("_AEView".equals(StringUtils.trimToEmpty(type)) || "_TEView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescElgRuleValue,lovDescCodelov, lovDescNamelov");
		} else if ("_ASGView".equals(StringUtils.trimToEmpty(type)) || "_TSGView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescminScore,lovDescisoverride,lovDescoverrideScore,lovDescCodelov, lovDescNamelov");
		} else if ("_ACSGView".equals(StringUtils.trimToEmpty(type)) || "_TCSGView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescminScore,lovDescisoverride,lovDescoverrideScore,lovDescCodelov, lovDescNamelov");
		} else if ("_AQView".equals(StringUtils.trimToEmpty(type)) || "_TQView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescIsRemarksAllowed,lovDescCheckMinCount,lovDescCheckMaxCount, lovDescElgRuleValue ");
		} else if ("_ACView".equals(StringUtils.trimToEmpty(type)) || "_TCView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_ATView".equals(StringUtils.trimToEmpty(type)) ||"_TTView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		}
		
		selectSql.append(" From LMTFacilityRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType");
		if(StringUtils.isNotBlank(roleCode)){
			selectSql.append(" AND ShowInStage LIKE '%"+roleCode +",%' ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityReferenceDetail);
		RowMapper<FacilityReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FacilityReferenceDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

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
	 *            Reference Details (facilityReferenceDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FacilityReferenceDetail facilityReferenceDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From LMTFacilityRefDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinRefDetailId =:FinRefDetailId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityReferenceDetail);
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
	 * This method insert new Records into LMTFinRefDetail or
	 * LMTFinRefDetail_Temp. it fetches the available Sequence form
	 * SeqLMTFinRefDetail by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Finance Reference Details
	 * 
	 * @param Finance
	 *            Reference Details (facilityReferenceDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(FacilityReferenceDetail facilityReferenceDetail, String type) {
		logger.debug("Entering");
		if (facilityReferenceDetail.getId() == Long.MIN_VALUE) {
			facilityReferenceDetail.setId(getNextidviewDAO().getNextId("SeqLMTFacilityRefDetail"));
			logger.debug("get NextID:" + facilityReferenceDetail.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into LMTFacilityRefDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinRefDetailId, FinType, FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage,OverRide,OverRideValue");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinRefDetailId, :FinType, :FinRefType, :FinRefId, :IsActive, :ShowInStage, :MandInputInStage, :AllowInputInStage,:OverRide,:OverRideValue");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityReferenceDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return facilityReferenceDetail.getId();
	}

	/**
	 * This method updates the Record LMTFinRefDetail or LMTFinRefDetail_Temp.
	 * if Record not updated then throws DataAccessException with error 41004.
	 * update Finance Reference Details by key FinRefDetailId and Version
	 * 
	 * @param Finance
	 *            Reference Details (facilityReferenceDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FacilityReferenceDetail facilityReferenceDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update LMTFacilityRefDetail");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
				.append(" Set FinType = :FinType, FinRefType = :FinRefType, FinRefId = :FinRefId, IsActive = :IsActive, ShowInStage = :ShowInStage, MandInputInStage = :MandInputInStage, AllowInputInStage = :AllowInputInStage,OverRide=:OverRide,OverRideValue =:OverRideValue");
		updateSql
				.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinRefDetailId =:FinRefDetailId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityReferenceDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
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
	 * @return List<FacilityReferenceDetail>
	 */
	@Override
	public List<FacilityReferenceDetail> getFinRefDetByRoleAndFinType(final String financeType, String mandInputInStage,
			List<String> groupIds, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("Select FinRefDetailId, FinType,FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage");
		selectSql.append(",OverRide,OverRideValue, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if ("_AEView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescElgRuleValue,lovDescCodelov, lovDescNamelov,lovDescFinTypeDescName, lovDescRefDesc,lovDescFinCcyCode, lovDescProductCodeName,lovDescRuleReturnType ");
		} else if ("_ASGView".equals(StringUtils.trimToEmpty(type)) ||"_ACSGView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescminScore,lovDescisoverride,lovDescoverrideScore,lovDescCodelov, lovDescNamelov,lovDescFinTypeDescName, lovDescRefDesc ");
		}
		selectSql.append(" From LMTFacilityRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where " );
		
		if(StringUtils.isNotBlank(financeType)){
			selectSql.append(" FinType =:FinType AND ");
		}
		if(groupIds != null && groupIds.size() > 0){
			selectSql.append(" FinRefId NOT IN (:GroupIds) AND ");
		}
		
		if(mandInputInStage != null){
			if("_AEView".equals(type)){
				selectSql.append(" AllowInputInStage like '%" + mandInputInStage + ",%' AND ");
			}else{
				selectSql.append(" MandInputInStage like '%" + mandInputInStage + ",%' AND ");
			}	
		}
		selectSql.append(" IsActive = 1 ");
		
		Map<String, List<String>> parameterMap=new HashMap<String, List<String>>();
		List<String> fintype = new ArrayList<String>();
		fintype.add(financeType);
		parameterMap.put("GroupIds", groupIds);
		parameterMap.put("FinType",fintype);
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FacilityReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FacilityReferenceDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), parameterMap, typeRowMapper);

	}
	@Override
	public void deleteByFinType(String finType, String type) {
		logger.debug("Entering");
		FacilityReferenceDetail facilityReferenceDetail=new FacilityReferenceDetail();
		facilityReferenceDetail.setFinType(finType);
		StringBuilder deleteSql = new StringBuilder("Delete From LMTFacilityRefDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityReferenceDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
	}
	/**
	 * Fetch the Record Finance Reference Details details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FacilityReferenceDetail
	 */
	@Override
	public List<FacilityReferenceDetail> getFacilityReferenceDetailById(final String finType) {
		logger.debug("Entering");
		FacilityReferenceDetail facilityReferenceDetail = getFacilityReferenceDetail();
		facilityReferenceDetail.setFinType(finType);
		StringBuilder selectSql = new StringBuilder("Select * from " );
		selectSql.append(" FacilityReferenceDetail_View");
		selectSql.append(" Where FinType =:FinType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityReferenceDetail);
		RowMapper<FacilityReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FacilityReferenceDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
}