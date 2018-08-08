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
 * FileName    		:  FinanceReferenceDetailDAOImpl.java                                   * 	  
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
 * 28-05-2018       Sai Krishna              0.2          bugs #388 Get active notifications* 
 *                                                        only from the process editor.     * 
 *                                                                                          * 
 * 13-06-2018       Siva					 0.3        Stage Accounting Modifications      * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.lmtmasters.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.coreinterface.model.handlinginstructions.HandlingInstruction;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceReferenceDetail model</b> class.<br>
 * 
 */

public class FinanceReferenceDetailDAOImpl extends SequenceDao<FinanceReferenceDetail> implements FinanceReferenceDetailDAO {
    private static Logger logger = Logger.getLogger(FinanceReferenceDetailDAOImpl.class);

	public FinanceReferenceDetailDAOImpl() {
		super();
	}
	

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

		StringBuilder selectSql = new StringBuilder("Select FinRefDetailId, FinType,FinEvent, FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage, ");
		selectSql.append(" OverRide,OverRideValue,AllowDeviation, AllowWaiver, AllowPostpone, AllowExpire, Version , LastMntBy, LastMntOn,");
		selectSql.append(" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

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
			financeReferenceDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
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
	public List<FinanceReferenceDetail> getFinanceReferenceDetail(final String financeType,final String finEvent,
			String roleCode, String type) {
		logger.debug("Entering");
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();

		financeReferenceDetail.setFinType(financeType);
		financeReferenceDetail.setFinEvent(finEvent);
		financeReferenceDetail.setShowInStage(roleCode);
		
		StringBuilder selectSql = new StringBuilder("Select FinRefDetailId, FinType,FinEvent, FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage, ");
		selectSql.append("OverRide,OverRideValue, AllowDeviation, AllowWaiver, AllowPostpone, AllowExpire, AlertType, ");
		selectSql.append("Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescFinTypeDescName, lovDescRefDesc");
		}

		if (StringUtils.trimToEmpty(type).contains("_AAView") || StringUtils.trimToEmpty(type).contains("_TAView")) {
			selectSql.append(",lovDescAggReportName,lovDescAggReportPath,lovDescCodelov, lovDescNamelov, lovDescAggImage, lovDescAggRuleName,AggType,AllowMultiple,ModuleType");
		} else if ("_AEView".equals(StringUtils.trimToEmpty(type)) || "_TEView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescElgRuleValue,lovDescCodelov, lovDescNamelov");
		} else if ("_ASGView".equals(StringUtils.trimToEmpty(type)) || "_TSGView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescminScore,lovDescisoverride,lovDescoverrideScore,lovDescCodelov, lovDescNamelov");
		} else if ("_ACSGView".equals(StringUtils.trimToEmpty(type)) || "_TCSGView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescminScore,lovDescisoverride,lovDescoverrideScore,lovDescCodelov, lovDescNamelov");
		} else if ("_AQView".equals(StringUtils.trimToEmpty(type)) || "_TQView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescIsRemarksAllowed,lovDescCheckMinCount,lovDescCheckMaxCount, lovDescElgRuleValue, lovDescRuleReturnType ");
		} else if ("_ACView".equals(StringUtils.trimToEmpty(type)) || "_TCView".equals(StringUtils.trimToEmpty(type))) {
			//selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");     ////////////////////Stage Accounting with Stage Accounting Rules change///////////
			selectSql.append(" , lovDescStgRuleValue, lovDescCodelov, lovDescNamelov ");	
		} else if ("_ATView".equals(StringUtils.trimToEmpty(type)) || "_TTView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov, lovDescCodelov ");
		} else if ("_AFDView".equals(StringUtils.trimToEmpty(type)) || "_TFDView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_ACDView".equals(StringUtils.trimToEmpty(type)) || "_TCDView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_ABDView".equals(StringUtils.trimToEmpty(type)) || "_TBDView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_APCView".equals(StringUtils.trimToEmpty(type)) || "_TPCView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_ARCView".equals(StringUtils.trimToEmpty(type)) || "_TRCView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_ALDView".equals(StringUtils.trimToEmpty(type)) || "_TLDView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_TATView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_TFSView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescCodelov, lovDescRefDesc , lovDescNamelov ");
		}
		
		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType AND FinEvent = :FinEvent ");
		if(StringUtils.isNotBlank(roleCode)){
			selectSql.append(" AND ShowInStage LIKE '%"+roleCode +",%' ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		RowMapper<FinanceReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceReferenceDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

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
	public List<FinanceReferenceDetail> getFinanceProcessEditorDetails(final String financeType,final String finEvent, String type) {
		logger.debug("Entering");
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		financeReferenceDetail.setFinType(financeType);
		financeReferenceDetail.setFinEvent(finEvent);
		
		StringBuilder selectSql = new StringBuilder("Select * FROM  LMTFINREFDETAIL");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType AND FinEvent = :FinEvent and ISACTIVE = 1 ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		RowMapper<FinanceReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceReferenceDetail.class);
		
		List<FinanceReferenceDetail> financeReferenceDetails = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);  
		
		logger.debug("Leaving");
		return financeReferenceDetails;
		
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

		StringBuilder selectSql = new StringBuilder(" select AggReportName lovDescAggReportName, AggName lovDescNamelov, " );
		selectSql.append(" AggImage lovDescAggImage ,AggType,AllowMultiple,ModuleType from BMTAggrementDef " );
		selectSql.append(" WHERE AggCode IN("+aggCodes+") ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new FinanceReferenceDetail());
		RowMapper<FinanceReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceReferenceDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}
	
	/**
	 * Fetch Records Details by Finance Type and Role Code
	 * @param financeType
	 * @param roleCode
	 * @return
	 */
	@Override
	public List<Long> getRefIdListByFinType(final String financeType, String finEvent, String roleCode, String type) {
		logger.debug("Entering");
		
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		financeReferenceDetail.setFinType(financeType);
		financeReferenceDetail.setFinEvent(finEvent);
		
		StringBuilder selectSql = new StringBuilder("Select FinRefId ");
		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType AND FinEvent=:FinEvent ");
		
		if(StringUtils.isNotBlank(roleCode)){
			selectSql.append(" AND MandInputInStage LIKE '%"+roleCode +",%' ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForList(selectSql.toString(), beanParameters, Long.class);
	}

	/**
	 * Fetch Records Details by Finance Type and Role Code
	 * @param financeType
	 * @param roleCode
	 * @return
	 */
	@Override
	public Map<Long, String> getTemplateIdList(final String financeType, String finEvent, String roleCode, List<String> lovCodeList) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		// bugs #388 Get active notifications only from the process editor.
		StringBuilder selectSql = new StringBuilder("Select FinRefId , LovDescCodeLov ");
		selectSql.append(" From LMTFinRefDetail_ATView ");
		selectSql.append(" Where FinType =:FinType AND FinEvent =:FinEvent and IsActive = 1 ");

		if (StringUtils.isNotBlank(roleCode)) {
			selectSql.append(" AND MandInputInStage LIKE '%" + roleCode + ",%' ");
		}

		if (lovCodeList != null && !lovCodeList.isEmpty()) {
			selectSql.append(" AND LovDescCodeLov IN (:CodeLovList) ");
		}
		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("FinType", financeType);
		source.addValue("FinEvent", finEvent);
		source.addValue("CodeLovList", lovCodeList);
		
		final Map<Long, String> map = new HashMap<Long, String>();

		jdbcTemplate.query(selectSql.toString(), source, new ResultSetExtractor<Map<Long, String>>() {
			public Map<Long, String> extractData(ResultSet rs) throws SQLException {
				while (rs.next()) {
					map.put(rs.getLong("FinRefId"), rs.getString("LovDescCodeLov"));
				}
				return map;
			};
		});
		logger.debug(Literal.LEAVING);
		return map;
	}
	
	@Override
	public boolean resendNotification(String finType, String finEvent, String role,
			List<String> templateTyeList) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select resendReq From ");
		sql.append("  LMTFinRefDetail_ATView ");
		sql.append(" Where FinType =:FinType AND FinEvent =:FinEvent and IsActive = 1 ");

		if (StringUtils.isNotBlank(role)) {
			sql.append(" AND MandInputInStage LIKE '%" + role + ",%' ");
		}

		if (templateTyeList != null && !templateTyeList.isEmpty()) {
			sql.append(" AND LovDescCodeLov IN (:CodeLovList) ");
		}
		logger.debug("selectSql: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
		source.addValue("FinEvent", finEvent);
		source.addValue("CodeLovList", templateTyeList);

		List<Integer> list = jdbcTemplate.queryForList(sql.toString(), source, Integer.class);

		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0) == 1;
		}

		return false;
	}

	/**
	 * 
	 * @param financeType
	 * @param roleCode
	 * @param lovCodeList
	 * @return
	 */
	@Override
	public FinanceReferenceDetail getTemplateId(final String financeType, String finEvent, String roleCode, String lovCodeList) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", financeType);
		source.addValue("FinEvent", finEvent);
		source.addValue("CodeLovList", lovCodeList);
		
		StringBuilder selectSql = new StringBuilder("Select FinRefId, LovDescCodeLov ");
		selectSql.append(" From LMTFinRefDetail_ATView ");
		selectSql.append(" Where FinType =:FinType AND FinEvent=:FinEvent ");
		
		if(StringUtils.isNotBlank(roleCode)){
			selectSql.append(" AND MandInputInStage LIKE '%"+roleCode +",%' ");
		}
		
		selectSql.append(" AND LovDescCodeLov = :CodeLovList ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceReferenceDetail.class);
		logger.debug("Leaving");
		
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.debug(e);
		}
		return null;
		
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
	@Override
	public void delete(FinanceReferenceDetail financeReferenceDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From LMTFinRefDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinRefDetailId =:FinRefDetailId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
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
			financeReferenceDetail.setId(getNextId("SeqLMTFinRefDetail"));
			logger.debug("get NextID:" + financeReferenceDetail.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into LMTFinRefDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinRefDetailId, FinType, FinEvent, FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage,OverRide,OverRideValue, ");
		insertSql.append(" AllowDeviation, AllowWaiver, AllowPostpone, AllowExpire, AlertType,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinRefDetailId, :FinType, :FinEvent, :FinRefType, :FinRefId, :IsActive, :ShowInStage, :MandInputInStage, :AllowInputInStage,:OverRide,:OverRideValue,");
		insertSql.append(" :AllowDeviation, :AllowWaiver, :AllowPostpone, :AllowExpire, :AlertType, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
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

	@Override
	public void update(FinanceReferenceDetail financeReferenceDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update LMTFinRefDetail");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinRefId = :FinRefId, ");
		updateSql.append(" IsActive = :IsActive, ShowInStage = :ShowInStage, MandInputInStage = :MandInputInStage, ");
		updateSql.append(" AllowInputInStage = :AllowInputInStage,OverRide=:OverRide,OverRideValue =:OverRideValue, ");
		updateSql.append(" AllowDeviation = :AllowDeviation, AllowWaiver = :AllowWaiver, AllowPostpone = :AllowPostpone,  AllowExpire = :AllowExpire, AlertType = :AlertType, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, ");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, ");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinRefDetailId =:FinRefDetailId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

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
	 * @return List<FinanceReferenceDetail>
	 */
	@Override
	public List<FinanceReferenceDetail> getFinRefDetByRoleAndFinType(final String financeType, String finEvent, String mandInputInStage,
			List<String> groupIds, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("Select FinRefDetailId, FinType,FinEvent,FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage,");
		selectSql.append(" AllowDeviation, AllowWaiver, AllowPostpone, AllowExpire,");
		selectSql.append(" OverRide,OverRideValue, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if ("_AEView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescElgRuleValue,lovDescCodelov, lovDescNamelov,lovDescFinTypeDescName, lovDescRefDesc,lovDescFinCcyCode, lovDescProductCodeName,lovDescRuleReturnType ");
		} else if ("_ASGView".equals(StringUtils.trimToEmpty(type)) || "_ACSGView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescminScore,lovDescisoverride,lovDescoverrideScore,lovDescCodelov, lovDescNamelov,lovDescFinTypeDescName, lovDescRefDesc ");
		} else if ("_AAView".contains(StringUtils.trimToEmpty(type)) || "_TAView".contains(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescFinTypeDescName, lovDescRefDesc,lovDescAggReportName,lovDescAggReportPath,lovDescCodelov, lovDescNamelov, lovDescAggImage, lovDescAggRuleName,AggType,AllowMultiple,ModuleType");
		}
		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where " );
		
		if(StringUtils.isNotBlank(financeType)){
			selectSql.append(" FinType =:FinType AND ");
		}
		if(StringUtils.isNotBlank(finEvent)){
			selectSql.append(" FinEvent =:FinEvent AND ");
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
		List<String> finevent = new ArrayList<String>();
		finevent.add(finEvent);
		parameterMap.put("GroupIds", groupIds);
		parameterMap.put("FinType",fintype);
		parameterMap.put("FinEvent",finevent);
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceReferenceDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), parameterMap, typeRowMapper);

	}
	@Override
	public void deleteByFinType(String finType, String finEvent, String type) {
		logger.debug("Entering");
		FinanceReferenceDetail financeReferenceDetail=new FinanceReferenceDetail();
		financeReferenceDetail.setFinType(finType);
		financeReferenceDetail.setFinEvent(finEvent);
		
		StringBuilder deleteSql = new StringBuilder("Delete From LMTFinRefDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType AND FinEvent =:FinEvent ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Method for fetching list of Reference Details based on Finance Type
	 */
	@Override
    public List<FinanceReferenceDetail> getFinanceRefListByFinType(String finType, String type) {
		logger.debug("Entering");
		
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		financeReferenceDetail.setFinType(finType);
		
		StringBuilder selectSql = new StringBuilder("Select FinRefDetailId, FinType,FinEvent, FinRefType, FinRefId, IsActive, ShowInStage, ");
		selectSql.append("MandInputInStage, AllowInputInStage, ");
		selectSql.append("OverRide,OverRideValue, AllowDeviation, AllowWaiver, AllowPostpone, AllowExpire, ");
		selectSql.append("Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		RowMapper<FinanceReferenceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceReferenceDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }

	/**
	 * Method for save the Handling Instruction request and response details
	 * 
	 * @param handlingInstruction
	 */
	@Override
	public void saveHandlInstructionDetails(HandlingInstruction handlingInstruction) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into HandleInstructionLog");
		insertSql.append(" (ReferenceNum, MaintenanceCode, FinanceRef, InstallmentDate, NewMaturityDate, Remarks, TimeStamp)");
		insertSql.append(" Values(:ReferenceNum, :MaintenanceCode, :FinanceRef, :InstallmentDate, :NewMaturityDate, :Remarks, :TimeStamp)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(handlingInstruction);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for fetching FinCollateral details based on collateral type 
	 * 
	 * @param finReference
	 * @param collateralType
	 * @return FinCollaterals
	 */
	@Override
	public FinCollaterals getFinCollaterals(String finReference, String collateralType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("CollateralType", collateralType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  FinReference, CollateralType, FirstChequeNo, LastChequeNo, Status" );
		selectSql.append(" FROM  FinCollaterals");
		selectSql.append(" Where FinReference =:FinReference AND CollateralType =:CollateralType");

		logger.debug("selectSql: " + selectSql.toString());		

		FinCollaterals finCollaterals = null;
		try {
			RowMapper<FinCollaterals> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinCollaterals.class);
			finCollaterals = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finCollaterals = null;
		}
		logger.debug("Leaving");
		return finCollaterals;
	}
	
	@Override
	public int getFinanceReferenceDetailByRuleCode(long ruleId, String type) {
		logger.debug("Entering");
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();

		financeReferenceDetail.setId(ruleId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinRefId =:FinRefId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public String getAllowedRolesByCode(String finType, int finRefType, String quickDisbCode) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
		source.addValue("FinRefType", finRefType);
		source.addValue("LimitCode", quickDisbCode);
		
		StringBuilder selectSql = new StringBuilder("Select MandInputInStage  From LMTFinRefDetail ");
		selectSql.append(" Where FinType =:FinType AND FinRefType =:FinRefType AND ");
		selectSql.append(" FinRefId in(select limitId from limitcodedetail where LimitCode =:LimitCode)");

		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (Exception e) {
			logger.warn("Exception", e);
			return null;
		}
    }
	// ### 06-05-2018 - Start - story #361(Tuleap server) Manual Deviations

	@Override
	public String getWorkflowType(String finType, String finEvent, String module) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
		source.addValue("FinEvent", finEvent);
		source.addValue("ModuleName", module);

		StringBuilder selectSql = new StringBuilder("Select WorkFlowType  From lmtfinanceworkflowdef ");
		selectSql.append(" Where FinType =:FinType AND FinEvent =:FinEvent AND ModuleName =:ModuleName");

		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (Exception e) {
			logger.warn("Exception", e);
			return null;
		}
	}

	@Override
	public long getWorkflowIdByType(String workflowType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("WorkFlowType", workflowType);
		source.addValue("WorkFlowActive", 1);

		StringBuilder selectSql = new StringBuilder("Select WorkFlowId From workflowdetails ");
		selectSql.append(" Where WorkFlowType =:WorkFlowType AND WorkFlowActive =:WorkFlowActive");

		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (Exception e) {
			logger.warn("Exception", e);
			return 0;
		}

	}

	@Override
	public long getLimitIdByLimitCode(String limitCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitCode", limitCode);
		source.addValue("Active", 1);

		StringBuilder selectSql = new StringBuilder("Select LimitId From limitcodedetail ");
		selectSql.append(" Where LimitCode =:LimitCode AND Active =:Active");

		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (Exception e) {
			logger.warn("Exception", e);
			return 0;
		}

	}

	@Override
	public String authorities(String finType, int finRefType, long limitid) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
		source.addValue("FinRefId", limitid);
		source.addValue("FinRefType", finRefType);

		StringBuilder selectSql = new StringBuilder("Select MandInputInStage  From LmtFinRefDetail ");
		selectSql.append(" Where FinType =:FinType AND FinRefId =:FinRefId AND FinRefType =:FinRefType");

		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (Exception e) {
			logger.warn("Exception", e);
			return null;
		}
	}
	// ### 06-05-2018 - End
	
	/**
	 * Fetch Records Details by Finance Type, Ref Type and Role Code
	 * @param financeType
	 * @param roleCode
	 * @param finRefType
	 * @return
	 */
	@Override
	public List<Long> getRefIdListByRefType(final String financeType, String finEvent, String roleCode,int finRefType) {
		logger.debug("Entering");
		
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		financeReferenceDetail.setFinType(financeType);
		financeReferenceDetail.setFinEvent(finEvent);
		financeReferenceDetail.setFinRefType(finRefType);
		
		StringBuilder selectSql = new StringBuilder("Select FinRefId ");
		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(" Where FinType =:FinType AND FinEvent=:FinEvent AND FinRefType=:FinRefType ");
		
		if(StringUtils.isNotBlank(roleCode)){
			selectSql.append(" AND MandInputInStage LIKE '%"+roleCode +",%' ");
		}
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		
		logger.debug("Leaving");
		return this.jdbcTemplate.queryForList(selectSql.toString(), beanParameters, Long.class);
	}

}
