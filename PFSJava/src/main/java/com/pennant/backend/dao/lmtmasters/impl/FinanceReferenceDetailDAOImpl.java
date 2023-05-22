/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinanceReferenceDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-11-2011 * *
 * Modified Date : 26-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-11-2011 Pennant 0.1 * * 28-05-2018 Sai Krishna 0.2 bugs #388 Get active notifications* only from the process
 * editor. * * 13-06-2018 Siva 0.3 Stage Accounting Modifications * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.lmtmasters.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>FinanceReferenceDetail model</b> class.<br>
 * 
 */

public class FinanceReferenceDetailDAOImpl extends SequenceDao<FinanceReferenceDetail>
		implements FinanceReferenceDetailDAO {
	private static Logger logger = LogManager.getLogger(FinanceReferenceDetailDAOImpl.class);

	public FinanceReferenceDetailDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceReferenceDetail
	 * 
	 * @return FinanceReferenceDetail
	 */

	@Override
	public FinanceReferenceDetail getFinanceReferenceDetail() {
		logger.debug(Literal.ENTERING);
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceReferenceDetail");
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		if (workFlowDetails != null) {
			financeReferenceDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug(Literal.LEAVING);
		return financeReferenceDetail;
	}

	/**
	 * This method get the module from method getFinanceReferenceDetail() and set the new record flag as true and return
	 * FinanceReferenceDetail()
	 * 
	 * @return FinanceReferenceDetail
	 */

	@Override
	public FinanceReferenceDetail getNewFinanceReferenceDetail() {
		logger.debug(Literal.ENTERING);
		FinanceReferenceDetail financeReferenceDetail = getFinanceReferenceDetail();
		financeReferenceDetail.setNewRecord(true);
		logger.debug(Literal.LEAVING);
		return financeReferenceDetail;
	}

	/**
	 * Fetch the Record Finance Reference Details details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return FinanceReferenceDetail
	 */
	@Override
	public FinanceReferenceDetail getFinanceReferenceDetailById(final long id, String type) {
		logger.debug(Literal.ENTERING);
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();

		financeReferenceDetail.setId(id);

		StringBuilder selectSql = new StringBuilder(
				"Select FinRefDetailId, FinType,FinEvent, FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage, ");
		selectSql.append(
				" OverRide,OverRideValue,AllowDeviation, AllowWaiver, AllowPostpone, AllowExpire, Version , LastMntBy, LastMntOn,");
		selectSql.append(" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescFinTypeDescName");
		}

		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinRefDetailId =:FinRefDetailId");

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		RowMapper<FinanceReferenceDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinanceReferenceDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch Records Details by Finance Type, Reference Type and field
	 * 
	 * @param finance   Type (String)
	 * @param reference Type (int)
	 * @param type      (String) ""/_Temp/_View
	 * @return List<FinanceReferenceDetail>
	 */
	@Override
	public List<FinanceReferenceDetail> getFinanceReferenceDetail(final String financeType, final String finEvent,
			String roleCode, String type) {
		logger.debug(Literal.ENTERING);
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();

		financeReferenceDetail.setFinType(financeType);
		financeReferenceDetail.setFinEvent(finEvent);
		financeReferenceDetail.setShowInStage(roleCode);

		StringBuilder selectSql = new StringBuilder(
				"Select FinRefDetailId, FinType,FinEvent, FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage, ");
		selectSql
				.append("OverRide,OverRideValue, AllowDeviation, AllowWaiver, AllowPostpone, AllowExpire, AlertType, ");
		selectSql.append(
				"Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescFinTypeDescName, lovDescRefDesc");
		}

		if (StringUtils.trimToEmpty(type).contains("_AAView") || StringUtils.trimToEmpty(type).contains("_TAView")) {
			selectSql.append(
					",lovDescAggReportName,lovDescAggReportPath,lovDescCodelov, lovDescNamelov, lovDescAggImage, lovDescAggRuleName,AggType,AllowMultiple,ModuleType");
		} else if ("_AEView".equals(StringUtils.trimToEmpty(type)) || "_TEView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescElgRuleValue,lovDescCodelov, lovDescNamelov");
		} else if ("_ASGView".equals(StringUtils.trimToEmpty(type))
				|| "_TSGView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescminScore,lovDescisoverride,lovDescoverrideScore,lovDescCodelov, lovDescNamelov");
		} else if ("_ACSGView".equals(StringUtils.trimToEmpty(type))
				|| "_TCSGView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(",lovDescminScore,lovDescisoverride,lovDescoverrideScore,lovDescCodelov, lovDescNamelov");
		} else if ("_AQView".equals(StringUtils.trimToEmpty(type)) || "_TQView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(
					",lovDescIsRemarksAllowed,lovDescCheckMinCount,lovDescCheckMaxCount, lovDescElgRuleValue, lovDescRuleReturnType ");
		} else if ("_ACView".equals(StringUtils.trimToEmpty(type)) || "_TCView".equals(StringUtils.trimToEmpty(type))) {
			// selectSql.append(" ,lovDescRefDesc , lovDescNamelov "); ////////////////////Stage Accounting with Stage
			// Accounting Rules change///////////
			selectSql.append(" , lovDescStgRuleValue, lovDescCodelov, lovDescNamelov ");
		} else if ("_ATView".equals(StringUtils.trimToEmpty(type)) || "_TTView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov, lovDescCodelov, ResendReq ");
		} else if ("_AFDView".equals(StringUtils.trimToEmpty(type))
				|| "_TFDView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_ACDView".equals(StringUtils.trimToEmpty(type))
				|| "_TCDView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_ABDView".equals(StringUtils.trimToEmpty(type))
				|| "_TBDView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_APCView".equals(StringUtils.trimToEmpty(type))
				|| "_TPCView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_ARCView".equals(StringUtils.trimToEmpty(type))
				|| "_TRCView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_ALDView".equals(StringUtils.trimToEmpty(type))
				|| "_TLDView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_TATView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescRefDesc , lovDescNamelov ");
		} else if ("_TFSView".equals(StringUtils.trimToEmpty(type))) {
			selectSql.append(" ,lovDescCodelov, lovDescRefDesc , lovDescNamelov ");
		}

		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType AND FinEvent = :FinEvent");
		if (StringUtils.isNotBlank(roleCode)) {
			selectSql.append(" AND ShowInStage LIKE '%" + roleCode + ",%' ");
		}

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		RowMapper<FinanceReferenceDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinanceReferenceDetail.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}

	/**
	 * Fetch Records Details by Finance Type, Reference Type and field
	 * 
	 * @param finance   Type (String)
	 * @param reference Type (int)
	 * @param type      (String) ""/_Temp/_View
	 * @return List<FinanceReferenceDetail>
	 */
	@Override
	public List<FinanceReferenceDetail> getFinanceProcessEditorDetails(final String financeType, final String finEvent,
			String type) {
		StringBuilder sql = getSqlQuery(StringUtils.trimToEmpty(type));
		sql.append(" Where FinType = ? and FinEvent = ? and IsActive = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, financeType);
			ps.setString(index++, finEvent);
			ps.setBoolean(index, true);
		}, (rs, rowNum) -> {
			return getRowMapper(rs, StringUtils.trimToEmpty(type));
		});

	}

	/**
	 * Fetch Records Details by Finance Type, Reference Type and field
	 * 
	 * @param finance   Type (String)
	 * @param reference Type (int)
	 * @param type      (String) ""/_Temp/_View
	 * @return List<FinanceReferenceDetail>
	 */
	@Override
	public List<FinanceReferenceDetail> getAgreementListByCode(String aggCodes) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder(
				" select AggReportName lovDescAggReportName, AggName lovDescNamelov, ");
		selectSql.append(" AggImage lovDescAggImage ,AggType,AllowMultiple,ModuleType from BMTAggrementDef ");
		selectSql.append(" WHERE AggCode IN(" + aggCodes + ") ");

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new FinanceReferenceDetail());
		RowMapper<FinanceReferenceDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinanceReferenceDetail.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}

	/**
	 * Fetch Records Details by Finance Type and Role Code
	 * 
	 * @param financeType
	 * @param roleCode
	 * @return
	 */
	@Override
	public List<Long> getRefIdListByFinType(final String financeType, String finEvent, String roleCode, String type) {
		logger.debug(Literal.ENTERING);

		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		financeReferenceDetail.setFinType(financeType);
		financeReferenceDetail.setFinEvent(finEvent);

		StringBuilder selectSql = new StringBuilder("Select FinRefId ");
		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType AND FinEvent=:FinEvent ");

		if (StringUtils.isNotBlank(roleCode)) {
			selectSql.append(" AND MandInputInStage LIKE '%" + roleCode + ",%' ");
		}

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForList(selectSql.toString(), beanParameters, Long.class);
	}

	/**
	 * Fetch Records Details by Finance Type and Role Code
	 * 
	 * @param financeType
	 * @param roleCode
	 * @return
	 */
	@Override
	public Map<Long, String> getTemplateIdList(final String financeType, String finEvent, String roleCode,
			List<String> lovCodeList) {
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.SQL + selectSql.toString());

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
			}
		});
		logger.debug(Literal.LEAVING);
		return map;
	}

	@Override
	public boolean resendNotification(String finType, String finEvent, String role, List<String> templateTyeList) {

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
		logger.debug(Literal.SQL + sql.toString());

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
	public FinanceReferenceDetail getTemplateId(final String financeType, String finEvent, String roleCode,
			String lovCodeList) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", financeType);
		source.addValue("FinEvent", finEvent);
		source.addValue("CodeLovList", lovCodeList);

		StringBuilder selectSql = new StringBuilder("Select FinRefId, LovDescCodeLov ");
		selectSql.append(" From LMTFinRefDetail_ATView ");
		selectSql.append(" Where FinType =:FinType AND FinEvent=:FinEvent ");

		if (StringUtils.isNotBlank(roleCode)) {
			selectSql.append(" AND MandInputInStage LIKE '%" + roleCode + ",%' ");
		}

		selectSql.append(" AND LovDescCodeLov = :CodeLovList ");

		logger.debug(Literal.SQL + selectSql.toString());
		RowMapper<FinanceReferenceDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinanceReferenceDetail.class);
		logger.debug(Literal.LEAVING);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the LMTFinRefDetail or LMTFinRefDetail_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Finance Reference Details by key FinRefDetailId
	 * 
	 * @param Finance Reference Details (financeReferenceDetail)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinanceReferenceDetail financeReferenceDetail, String type) {
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into LMTFinRefDetail or LMTFinRefDetail_Temp. it fetches the available Sequence
	 * form SeqLMTFinRefDetail by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Finance Reference Details
	 * 
	 * @param Finance Reference Details (financeReferenceDetail)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(FinanceReferenceDetail financeReferenceDetail, String type) {
		logger.debug(Literal.ENTERING);
		if (financeReferenceDetail.getId() == Long.MIN_VALUE) {
			financeReferenceDetail.setId(getNextValue("SeqLMTFinRefDetail"));
			logger.debug("get NextValue:" + financeReferenceDetail.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into LMTFinRefDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (FinRefDetailId, FinType, FinEvent, FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage, AllowInputInStage,OverRide,OverRideValue, ");
		insertSql.append(" AllowDeviation, AllowWaiver, AllowPostpone, AllowExpire, AlertType,");
		insertSql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ResendReq)");
		insertSql.append(
				" Values(:FinRefDetailId, :FinType, :FinEvent, :FinRefType, :FinRefId, :IsActive, :ShowInStage, :MandInputInStage, :AllowInputInStage,:OverRide,:OverRideValue,");
		insertSql.append(" :AllowDeviation, :AllowWaiver, :AllowPostpone, :AllowExpire, :AlertType, ");
		insertSql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :ResendReq)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
		return financeReferenceDetail.getId();
	}

	/**
	 * This method updates the Record LMTFinRefDetail or LMTFinRefDetail_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Reference Details by key FinRefDetailId and Version
	 * 
	 * @param Finance Reference Details (financeReferenceDetail)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinanceReferenceDetail financeReferenceDetail, String type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		StringBuilder updateSql = new StringBuilder("Update LMTFinRefDetail");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinRefId = :FinRefId, ");
		updateSql.append(" IsActive = :IsActive, ShowInStage = :ShowInStage, MandInputInStage = :MandInputInStage, ");
		updateSql.append(" AllowInputInStage = :AllowInputInStage,OverRide=:OverRide,OverRideValue =:OverRideValue, ");
		updateSql.append(
				" AllowDeviation = :AllowDeviation, AllowWaiver = :AllowWaiver, AllowPostpone = :AllowPostpone,  AllowExpire = :AllowExpire, AlertType = :AlertType, ");
		updateSql.append(
				" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, ");
		updateSql.append(
				" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, ");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, ResendReq = :ResendReq");
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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Fetch Records Details by Finance Type, Reference Type and field
	 * 
	 * @param finance   Type (String)
	 * @param reference Type (int)
	 * @param type      (String) ""/_Temp/_View
	 * @return List<FinanceReferenceDetail>
	 */
	@Override
	public List<FinanceReferenceDetail> getFinRefDetByRoleAndFinType(final String financeType, String finEvent,
			String mandInputInStage, List<String> groupIds, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinRefDetailId, FinType, FinEvent, FinRefType, FinRefId, IsActive, ShowInStage");
		sql.append(", MandInputInStage, AllowInputInStage, AllowDeviation, AllowWaiver, AllowPostpone");
		sql.append(", AllowExpire, OverRide, OverRideValue, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if ("_AEView".equals(StringUtils.trimToEmpty(type))) {
			sql.append(", LovDescElgRuleValue, LovDescCodelov, LovDescNamelov, LovDescFinTypeDescName");
			sql.append(", LovDescRefDesc, LovDescFinCcyCode, LovDescProductCodeName, LovDescRuleReturnType");
		} else if ("_ASGView".equals(StringUtils.trimToEmpty(type))
				|| "_ACSGView".equals(StringUtils.trimToEmpty(type))) {
			sql.append(", LovDescminScore, LovDescisoverride, LovDescoverrideScore");
			sql.append(", LovDescCodelov, LovDescNamelov, LovDescFinTypeDescName, LovDescRefDesc");
		} else if ("_AAView".contains(StringUtils.trimToEmpty(type))
				|| "_TAView".contains(StringUtils.trimToEmpty(type))) {
			sql.append(", LovDescFinTypeDescName, LovDescRefDesc, LovDescAggReportName");
			sql.append(", LovDescAggReportPath, LovDescCodelov, LovDescNamelov, LovDescAggImage, LovDescAggRuleName");
			sql.append(", AggType, AllowMultiple, ModuleType");
		}
		sql.append(" from LMTFinRefDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ");

		if (StringUtils.isNotBlank(financeType)) {
			sql.append(" FinType = ? and");
		}
		if (StringUtils.isNotBlank(finEvent)) {
			sql.append(" FinEvent = ? and");
		}
		if (CollectionUtils.isNotEmpty(groupIds)) {
			sql.append(" FinRefId NOT IN (");
			int i = 0;
			while (i < groupIds.size()) {
				sql.append(" ?,");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(") and");
		}

		if (mandInputInStage != null) {
			if ("_AEView".equals(type)) {
				sql.append(" AllowInputInStage");
			} else {
				sql.append(" MandInputInStage");
			}
			sql.append(" like '%").append(mandInputInStage).append(",%' and");
		}
		sql.append(" IsActive = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			if (StringUtils.isNotBlank(financeType)) {
				ps.setString(index++, financeType);
			}
			if (StringUtils.isNotBlank(finEvent)) {
				ps.setString(index++, finEvent);
			}
			if (CollectionUtils.isNotEmpty(groupIds)) {
				for (String id : groupIds) {
					ps.setString(index++, id);
				}
			}

			ps.setBoolean(index, true);

		}, (rs, rowNum) -> {
			FinanceReferenceDetail frd = new FinanceReferenceDetail();

			frd.setFinRefDetailId(rs.getLong("FinRefDetailId"));
			frd.setFinType(rs.getString("FinType"));
			frd.setFinEvent(rs.getString("FinEvent"));
			frd.setFinRefType(rs.getInt("FinRefType"));
			frd.setFinRefId(rs.getLong("FinRefId"));
			frd.setIsActive(rs.getBoolean("IsActive"));
			frd.setShowInStage(rs.getString("ShowInStage"));
			frd.setMandInputInStage(rs.getString("MandInputInStage"));
			frd.setAllowInputInStage(rs.getString("AllowInputInStage"));
			frd.setAllowDeviation(rs.getBoolean("AllowDeviation"));
			frd.setAllowWaiver(rs.getBoolean("AllowWaiver"));
			frd.setAllowPostpone(rs.getBoolean("AllowPostpone"));
			frd.setAllowExpire(rs.getBoolean("AllowExpire"));
			frd.setOverRide(rs.getBoolean("OverRide"));
			frd.setOverRideValue(rs.getInt("OverRideValue"));
			frd.setVersion(rs.getInt("Version"));
			frd.setLastMntBy(rs.getLong("LastMntBy"));
			frd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			frd.setRecordStatus(rs.getString("RecordStatus"));
			frd.setRoleCode(rs.getString("RoleCode"));
			frd.setNextRoleCode(rs.getString("NextRoleCode"));
			frd.setTaskId(rs.getString("TaskId"));
			frd.setNextTaskId(rs.getString("NextTaskId"));
			frd.setRecordType(rs.getString("RecordType"));
			frd.setWorkflowId(rs.getLong("WorkflowId"));

			if ("_AEView".equals(StringUtils.trimToEmpty(type))) {
				frd.setLovDescElgRuleValue(rs.getString("LovDescElgRuleValue"));
				frd.setLovDescCodelov(rs.getString("LovDescCodelov"));
				frd.setLovDescNamelov(rs.getString("LovDescNamelov"));
				frd.setLovDescFinTypeDescName(rs.getString("LovDescFinTypeDescName"));
				frd.setLovDescRefDesc(rs.getString("LovDescRefDesc"));
				frd.setLovDescFinCcyCode(rs.getString("LovDescFinCcyCode"));
				frd.setLovDescProductCodeName(rs.getString("LovDescProductCodeName"));
				frd.setLovDescRuleReturnType(rs.getString("LovDescRuleReturnType"));
			} else if ("_ASGView".equals(StringUtils.trimToEmpty(type))
					|| "_ACSGView".equals(StringUtils.trimToEmpty(type))) {
				frd.setLovDescminScore(rs.getInt("LovDescminScore"));
				frd.setLovDescisoverride(rs.getBoolean("LovDescisoverride"));
				frd.setLovDescoverrideScore(rs.getInt("LovDescoverrideScore"));
				frd.setLovDescCodelov(rs.getString("LovDescCodelov"));
				frd.setLovDescNamelov(rs.getString("LovDescNamelov"));
				frd.setLovDescFinTypeDescName(rs.getString("LovDescFinTypeDescName"));
				frd.setLovDescRefDesc(rs.getString("LovDescRefDesc"));
			} else if ("_AAView".contains(StringUtils.trimToEmpty(type))
					|| "_TAView".contains(StringUtils.trimToEmpty(type))) {
				frd.setLovDescFinTypeDescName(rs.getString("LovDescFinTypeDescName"));
				frd.setLovDescRefDesc(rs.getString("LovDescRefDesc"));
				frd.setLovDescCodelov(rs.getString("LovDescCodelov"));
				frd.setLovDescNamelov(rs.getString("LovDescNamelov"));
				frd.setLovDescAggReportName(rs.getString("LovDescAggReportName"));
				frd.setLovDescAggReportPath(rs.getString("LovDescAggReportPath"));
				frd.setLovDescAggImage(rs.getString("LovDescAggImage"));
				frd.setLovDescAggRuleName(rs.getString("LovDescAggRuleName"));
				frd.setAggType(rs.getString("AggType"));
				frd.setAllowMultiple(rs.getBoolean("AllowMultiple"));
				frd.setModuleType(rs.getString("ModuleType"));
			}

			return frd;
		});

	}

	@Override
	public void deleteByFinType(String finType, String finEvent, String type) {
		logger.debug(Literal.ENTERING);
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		financeReferenceDetail.setFinType(finType);
		financeReferenceDetail.setFinEvent(finEvent);

		StringBuilder deleteSql = new StringBuilder("Delete From LMTFinRefDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType AND FinEvent =:FinEvent ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for fetching list of Reference Details based on Finance Type
	 */
	@Override
	public List<FinanceReferenceDetail> getFinanceRefListByFinType(String finType, String type) {
		logger.debug(Literal.ENTERING);

		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		financeReferenceDetail.setFinType(finType);

		StringBuilder selectSql = new StringBuilder(
				"Select FinRefDetailId, FinType,FinEvent, FinRefType, FinRefId, IsActive, ShowInStage, ");
		selectSql.append("MandInputInStage, AllowInputInStage, ");
		selectSql.append("OverRide,OverRideValue, AllowDeviation, AllowWaiver, AllowPostpone, AllowExpire, ");
		selectSql.append(
				"Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType ");

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		RowMapper<FinanceReferenceDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinanceReferenceDetail.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, CollateralType, FirstChequeNo, LastChequeNo, Status");
		sql.append(" From FinCollaterals");
		sql.append(" Where FinReference = ? and CollateralType = ?");

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				FinCollaterals fc = new FinCollaterals();

				fc.setFinReference(rs.getString("FinReference"));
				fc.setCollateralType(rs.getString("CollateralType"));
				fc.setFirstChequeNo(rs.getString("FirstChequeNo"));
				fc.setLastChequeNo(rs.getString("LastChequeNo"));
				fc.setStatus(rs.getString("Status"));

				return fc;
			}, finReference, collateralType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Record is not found in FinCollaterals for the specified FinReference >> {} and CollateralType >> {}",
					finReference, collateralType);
		}

		return null;
	}

	@Override
	public int getFinanceReferenceDetailByRuleCode(long ruleId, String type) {
		logger.debug(Literal.ENTERING);
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();

		financeReferenceDetail.setId(ruleId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinRefId =:FinRefId");

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public String getAllowedRolesByCode(String finType, int finRefType, String limitCode, String finEvent) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MandInputInStage");
		sql.append(" from LMTFinRefDetail");
		sql.append(" Where FinType = ? and FinRefType = ?");
		sql.append(" and isActive = ? and FinRefId in (");
		sql.append(" Select LimitId From LimitCodeDetail");
		sql.append(" Where LimitCode = ?)");

		if (StringUtils.isNotBlank(finEvent)) {
			sql.append(" and FinEvent = ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		try {
			Object[] args = new Object[] { finType, finRefType, 1, limitCode };

			if (StringUtils.isNotBlank(finEvent)) {
				args = new Object[] { finType, finRefType, 1, limitCode, finEvent };
			}

			return this.jdbcOperations.queryForObject(sql.toString(), String.class, args);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	// ### 06-05-2018 - Start - story #361(Tuleap server) Manual Deviations

	@Override
	public String getWorkflowType(String finType, String finEvent, String module) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
		source.addValue("FinEvent", finEvent);
		source.addValue("ModuleName", module);

		StringBuilder selectSql = new StringBuilder("Select WorkFlowType  From lmtfinanceworkflowdef ");
		selectSql.append(" Where FinType =:FinType AND FinEvent =:FinEvent AND ModuleName =:ModuleName");

		logger.debug(Literal.SQL + selectSql.toString());

		logger.debug(Literal.LEAVING);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long getWorkflowIdByType(String workflowType) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("WorkFlowType", workflowType);
		source.addValue("WorkFlowActive", 1);

		StringBuilder selectSql = new StringBuilder("Select WorkFlowId From workflowdetails ");
		selectSql.append(" Where WorkFlowType =:WorkFlowType AND WorkFlowActive =:WorkFlowActive");

		logger.debug(Literal.SQL + selectSql.toString());

		logger.debug(Literal.LEAVING);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public long getLimitIdByLimitCode(String limitCode) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitCode", limitCode);
		source.addValue("Active", 1);

		StringBuilder selectSql = new StringBuilder("Select LimitId From limitcodedetail ");
		selectSql.append(" Where LimitCode =:LimitCode AND Active =:Active");

		logger.debug(Literal.SQL + selectSql.toString());

		logger.debug(Literal.LEAVING);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public String authorities(String finType, int finRefType, long limitid) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
		source.addValue("FinRefId", limitid);
		source.addValue("FinRefType", finRefType);

		StringBuilder selectSql = new StringBuilder("Select MandInputInStage  From LmtFinRefDetail ");
		selectSql.append(" Where FinType =:FinType AND FinRefId =:FinRefId AND FinRefType =:FinRefType");

		logger.debug(Literal.SQL + selectSql.toString());

		logger.debug(Literal.LEAVING);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
	// ### 06-05-2018 - End

	/**
	 * Fetch Records Details by Finance Type, Ref Type and Role Code
	 * 
	 * @param financeType
	 * @param roleCode
	 * @param finRefType
	 * @return
	 */
	@Override
	public List<Long> getRefIdListByRefType(final String financeType, String finEvent, String roleCode,
			int finRefType) {
		logger.debug(Literal.ENTERING);

		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		financeReferenceDetail.setFinType(financeType);
		financeReferenceDetail.setFinEvent(finEvent);
		financeReferenceDetail.setFinRefType(finRefType);

		StringBuilder selectSql = new StringBuilder("Select FinRefId ");
		selectSql.append(" From LMTFinRefDetail");
		selectSql.append(" Where FinType =:FinType AND FinEvent=:FinEvent AND FinRefType=:FinRefType ");

		if (StringUtils.isNotBlank(roleCode)) {
			selectSql.append(" AND MandInputInStage LIKE '%" + roleCode + ",%' ");
		}

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForList(selectSql.toString(), beanParameters, Long.class);
	}

	@Override
	public List<FinanceReferenceDetail> getFinanceRefListByFinType(String finType, String stage, String type) {
		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		financeReferenceDetail.setFinType(finType);
		StringBuilder selectSql = new StringBuilder(
				"SELECT finrefdetailid, fintype, lovdescfintypedescname, finreftype, finrefid, ");
		selectSql.append(
				"lovdescrefdesc, isactive, showinstage, mandinputinstage, allowinputinstage, allowdeviation, allowwaiver, allowpostpone,");
		selectSql.append(
				" allowexpire, version, lastmntby, lastmnton, recordstatus, rolecode, nextrolecode, taskid, nexttaskid, recordtype, workflowid,");
		selectSql.append(
				" lovdescisremarksallowed, lovdesccheckmincount, lovdesccheckmaxcount, override, overridevalue, lovdescelgrulevalue, lovdescrulereturntype, finevent, alerttype ");
		selectSql.append("FROM lmtfinrefdetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType ");
		if (StringUtils.isNotBlank(stage)) {
			selectSql.append(" AND ShowInStage LIKE '%" + stage + ",%' ");
		}
		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeReferenceDetail);
		RowMapper<FinanceReferenceDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinanceReferenceDetail.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}

	@Override
	public List<FinanceReferenceDetail> getFinanceProcessEditorDetails(final String financeType, final String finEvent,
			Integer finRefType, String type) {

		StringBuilder sql = getSqlQuery(StringUtils.trimToEmpty(type));
		sql.append(" Where FinType = ? and FinEvent = ?  and FinRefType = ? and IsActive = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, financeType);
			ps.setString(index++, finEvent);
			ps.setInt(index++, finRefType);
			ps.setBoolean(index, true);
		}, (rs, rowNum) -> {
			return getRowMapper(rs, StringUtils.trimToEmpty(type));
		});
	}

	@Override
	public List<FinanceReferenceDetail> getAgreemantsListByFinType(String finType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select FinType, LovDescFinTypeDescName, LovDescAggImage");
		sql.append(", LovDescNameLov, LovDescCodeLov, LovDescRefDesc, LovDescAggReportName, AggType");
		sql.append("  from LMTFINREFDETAIL_TAview  Where FinType = :FinType");

		MapSqlParameterSource mapSqlParameter = new MapSqlParameterSource();
		mapSqlParameter.addValue("FinType", finType);

		RowMapper<FinanceReferenceDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinanceReferenceDetail.class);

		return this.jdbcTemplate.query(sql.toString(), mapSqlParameter, typeRowMapper);
	}

	@Override
	public List<SecurityUser> getUpLevelUsers(long usrId, String branch) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder(
				"SELECT UsrDesg,UsrLogin,UsrEmail,UsrFName, UsrMName, UsrLName, UsrMobile,UsrBranchCode,UsrDeptCode From SecUsers s");
		selectSql.append(" join secUserHierarchy u on u.Reporting_To = s.UsrId ");
		selectSql.append(" where u.usrid =:usrId and u.depth <> 0 and u.Branch =:branch");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("usrId", usrId);
		source.addValue("branch", branch);

		RowMapper<SecurityUser> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityUser.class);

		logger.debug("SelectSql: " + selectSql.toString());

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	private StringBuilder getSqlQuery(String tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinRefDetailId, FinType, FinRefType, FinRefId, IsActive, ShowInStage, MandInputInStage");
		sql.append(", AllowInputInStage, AllowDeviation, AllowWaiver, AllowPostpone, AllowExpire, OverRide");
		sql.append(", OverRideValue, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, FinEvent, AlertType");
		if (tableType.contains("VASVIEW") || tableType.contains("FINVIEW")) {
			sql.append(", LovDescFinTypeDescName");
			sql.append(", LovDescAggImage, LovDescNamelov, LovDescCodelov, LovDescRefDesc, LovDescAggReportName");
			sql.append(", LovDescAggReportPath, AllowMultiple, LovDescIsRemarksAllowed, LovDescCheckMinCount");
			sql.append(", LovDescCheckMaxCount");
		}
		if (tableType.contains("FINVIEW")) {
			sql.append(", LovDescFinCcyCode, LovDescProductCodeName, LovDescAggRuleName");
			sql.append(", AggType, ModuleType, LovDescElgRuleValue, LovDescRuleReturnType, LovDescminScore");
			sql.append(", LovDescisoverride, LovDescoverrideScore, TabCode");
		}

		sql.append(" from LMTFINREFDETAIL");
		sql.append(tableType);

		return sql;
	}

	private FinanceReferenceDetail getRowMapper(ResultSet rs, String tableType) throws SQLException {
		FinanceReferenceDetail rd = new FinanceReferenceDetail();

		rd.setFinRefDetailId(rs.getLong("FinRefDetailId"));
		rd.setFinType(rs.getString("FinType"));
		rd.setFinRefType(rs.getInt("FinRefType"));
		rd.setFinRefId(rs.getLong("FinRefId"));
		rd.setIsActive(rs.getBoolean("IsActive"));
		rd.setShowInStage(rs.getString("ShowInStage"));
		rd.setMandInputInStage(rs.getString("MandInputInStage"));
		rd.setAllowInputInStage(rs.getString("AllowInputInStage"));
		rd.setAllowDeviation(rs.getBoolean("AllowDeviation"));
		rd.setAllowWaiver(rs.getBoolean("AllowWaiver"));
		rd.setAllowPostpone(rs.getBoolean("AllowPostpone"));
		rd.setAllowExpire(rs.getBoolean("AllowExpire"));
		rd.setOverRide(rs.getBoolean("OverRide"));
		rd.setOverRideValue(rs.getInt("OverRideValue"));
		rd.setVersion(rs.getInt("Version"));
		rd.setLastMntBy(rs.getLong("LastMntBy"));
		rd.setLastMntOn(rs.getTimestamp("LastMntOn"));
		rd.setRecordStatus(rs.getString("RecordStatus"));
		rd.setRoleCode(rs.getString("RoleCode"));
		rd.setNextRoleCode(rs.getString("NextRoleCode"));
		rd.setTaskId(rs.getString("TaskId"));
		rd.setNextTaskId(rs.getString("NextTaskId"));
		rd.setRecordType(rs.getString("RecordType"));
		rd.setWorkflowId(rs.getLong("WorkflowId"));
		rd.setFinEvent(rs.getString("FinEvent"));
		rd.setAlertType(rs.getString("AlertType"));

		if (tableType.contains("VASVIEW") || tableType.contains("FINVIEW")) {
			rd.setLovDescFinTypeDescName(rs.getString("LovDescFinTypeDescName"));
			rd.setLovDescAggImage(rs.getString("LovDescAggImage"));
			rd.setLovDescNamelov(rs.getString("LovDescNamelov"));
			rd.setLovDescCodelov(rs.getString("LovDescCodelov"));
			rd.setLovDescRefDesc(rs.getString("LovDescRefDesc"));
			rd.setLovDescAggReportName(rs.getString("LovDescAggReportName"));
			rd.setLovDescAggReportPath(rs.getString("LovDescAggReportPath"));
			rd.setAllowMultiple(rs.getBoolean("AllowMultiple"));
			rd.setLovDescIsRemarksAllowed(rs.getBoolean("LovDescIsRemarksAllowed"));
			rd.setLovDescCheckMinCount(rs.getLong("LovDescCheckMinCount"));
			rd.setLovDescCheckMaxCount(rs.getLong("LovDescCheckMaxCount"));
		}
		if (tableType.contains("FINVIEW")) {
			rd.setLovDescFinCcyCode(rs.getString("LovDescFinCcyCode"));
			rd.setLovDescProductCodeName(rs.getString("LovDescProductCodeName"));
			rd.setLovDescAggRuleName(rs.getString("LovDescAggRuleName"));
			rd.setAggType(rs.getString("AggType"));
			rd.setModuleType(rs.getString("ModuleType"));
			rd.setLovDescElgRuleValue(rs.getString("LovDescElgRuleValue"));
			rd.setLovDescRuleReturnType(rs.getString("LovDescRuleReturnType"));
			rd.setLovDescminScore(rs.getInt("LovDescminScore"));
			rd.setLovDescisoverride(rs.getBoolean("LovDescisoverride"));
			rd.setLovDescoverrideScore(rs.getInt("LovDescoverrideScore"));
			rd.setTabCode(rs.getString("TabCode"));
		}

		return rd;
	}

	@Override
	public List<FinanceReferenceDetail> getLMTFinRefDetails(String limitCode, String finType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" frd.MandInputInStage, frd.FinEvent");
		sql.append(" From LMTFinRefDetail frd");
		sql.append(" Inner Join LimitCodeDetail lcd on frd.FinRefID = lcd.LimitID and frd.FinRefType = 12");
		sql.append(" Where lcd.LimitCode = ? and frd.FinType = ? and frd.IsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, limitCode);
			ps.setString(index++, finType);
			ps.setBoolean(index, true);
		}, (rs, rowNum) -> {
			FinanceReferenceDetail frd = new FinanceReferenceDetail();

			frd.setLovDescNamelov(limitCode);
			frd.setFinType(finType);
			frd.setMandInputInStage(rs.getString("MandInputInStage"));
			frd.setFinEvent(rs.getString("FinEvent"));

			return frd;
		});
	}

	@Override
	public boolean isTabCodeExists(String tabCode, String finType, String type, String event) {
		StringBuilder sql = new StringBuilder("Select Count(TabCode) From LMTFINREFDETAIL");
		sql.append(type);
		sql.append(" Where TabCode = ? and Fintype = ? and FinEvent = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), tabCode, finType,
				event) > 0;
	}

}
