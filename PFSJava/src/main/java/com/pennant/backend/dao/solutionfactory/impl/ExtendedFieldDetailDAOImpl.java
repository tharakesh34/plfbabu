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
 * FileName    		:  ExtendedFieldDetailDAOImpl.java                                      * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 19-06-2018       Sai Krishna              0.2          story #413 Allow scriptlet for    * 
 *                                                        extended fields without UI.       * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.solutionfactory.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>ExtendedFieldDetail model</b> class.<br>
 */
public class ExtendedFieldDetailDAOImpl extends BasicDao<ExtendedFieldDetail> implements ExtendedFieldDetailDAO {
	private static Logger logger = LogManager.getLogger(ExtendedFieldDetailDAOImpl.class);

	private NamedParameterJdbcTemplate auditJdbcTemplate;

	private enum FieldType {
		TEXT,
		UPPERTEXT,
		STATICCOMBO,
		MULTISTATICCOMBO,
		EXTENDEDCOMBO,
		MULTIEXTENDEDCOMBO,
		DATE,
		DATETIME,
		TIME,
		INT,
		LONG,
		ACTRATE,
		DECIMAL,
		CURRENCY,
		RADIO,
		PERCENTAGE,
		BOOLEAN,
		MULTILINETEXT,
		ACCOUNT,
		FREQUENCY,
		BASERATE,
		ADDRESS,
		PHONE,
		LISTFIELD
	}

	public ExtendedFieldDetailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Extended Field Detail details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ExtendedFieldDetail
	 */
	@Override
	public ExtendedFieldDetail getExtendedFieldDetailById(final long id, String name, int extendedType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ModuleId = ? and FieldName = ? and ExtendedType = ?");

		logger.trace(Literal.SQL + sql.toString());

		ExtendedFieldRowMapper rowMapper = new ExtendedFieldRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id, name, extendedType },
					rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ModuleId, FieldName, FieldType, FieldLength, FieldPrec, FieldLabel, FieldMandatory");
		sql.append(", FieldConstraint, FieldSeqOrder, FieldList, FieldDefaultValue, FieldMinValue");
		sql.append(", Filters, FieldMaxValue, FieldUnique, MultiLine, ParentTag, InputElement, Editable");
		sql.append(", Visible, AllowInRule, ValFromScript, Scriptlet, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", DefValue, AgrField"); //HL

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescModuleName, LovDescSubModuleName");
		}

		sql.append(" from ExtendedFieldDetail");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	/**
	 * This method Deletes the Record from the ExtendedFieldDetail or ExtendedFieldDetail_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete Extended Field Detail by key ModuleId
	 * 
	 * @param Extended
	 *            Field Detail (extendedFieldDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ExtendedFieldDetail extendedFieldDetail, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From ExtendedFieldDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ModuleId =:ModuleId AND FieldName =:FieldName  AND ExtendedType = :ExtendedType");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
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
	 * Method for Deletion of Extended Detail List of ExtendedDetail
	 */
	@Override
	public void deleteByExtendedFields(final long id, String type) {
		logger.debug(Literal.ENTERING);
		ExtendedFieldDetail extendedFieldDetail = new ExtendedFieldDetail();
		extendedFieldDetail.setId(id);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From ExtendedFieldDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ModuleId =:ModuleId ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into ExtendedFieldDetail or ExtendedFieldDetail_Temp. it fetches the available
	 * Sequence form SeqExtendedFieldDetail by using getNextidviewDAO().getNextId() method.
	 *
	 * save Extended Field Detail
	 * 
	 * @param Extended
	 *            Field Detail (extendedFieldDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(ExtendedFieldDetail extendedFieldDetail, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder("Insert Into ExtendedFieldDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ModuleId, FieldName, FieldType, FieldLength, FieldPrec, FieldLabel, ");
		insertSql.append(" FieldMandatory, FieldConstraint, FieldSeqOrder, FieldList, Filters, ");
		insertSql.append(" FieldDefaultValue, FieldMinValue, FieldMaxValue, FieldUnique, MultiLine,ParentTag,");
		insertSql.append(
				" InputElement,Editable, ExtendedType,AllowInRule ,ValFromScript, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, Visible,  Scriptlet,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ModuleId, :FieldName, :FieldType, :FieldLength, :FieldPrec, ");
		insertSql.append(" :FieldLabel, :FieldMandatory, :FieldConstraint, :FieldSeqOrder, ");
		insertSql.append(" :FieldList, :Filters, :FieldDefaultValue, :FieldMinValue, ");
		insertSql.append(
				" :FieldMaxValue, :FieldUnique, :MultiLine,:ParentTag,:InputElement,:Editable,:ExtendedType, :AllowInRule, :ValFromScript, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		insertSql.append(" :NextRoleCode, :Visible, :Scriptlet, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.debug("Exception: ", e);
			throw e;
		}

		logger.debug(Literal.LEAVING);
		return extendedFieldDetail.getId();
	}

	/**
	 * This method updates the Record ExtendedFieldDetail or ExtendedFieldDetail_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Extended Field Detail by key ModuleId and Version
	 * 
	 * @param Extended
	 *            Field Detail (extendedFieldDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(ExtendedFieldDetail extendedFieldDetail, String type) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update ExtendedFieldDetail");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FieldType = :FieldType, ");
		updateSql.append(" FieldLength = :FieldLength, FieldPrec = :FieldPrec, FieldLabel = :FieldLabel, ");
		updateSql.append(" FieldMandatory = :FieldMandatory, FieldConstraint = :FieldConstraint, ");
		updateSql.append(" FieldSeqOrder = :FieldSeqOrder, Filters = :Filters,");
		updateSql.append(" FieldList = :FieldList, FieldDefaultValue = :FieldDefaultValue, ");
		updateSql.append(
				" FieldMinValue = :FieldMinValue, FieldMaxValue = :FieldMaxValue,Editable = :Editable, ValFromScript = :ValFromScript, Scriptlet = :Scriptlet,");
		updateSql.append(
				" FieldUnique = :FieldUnique, MultiLine =:MultiLine ,ParentTag =:ParentTag,InputElement =:InputElement,ExtendedType =:ExtendedType, AllowInRule=:AllowInRule,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, ");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ModuleId =:ModuleId AND FieldName =:FieldName AND ExtendedType = :ExtendedType");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<ExtendedFieldDetail> getExtendedFieldDetailById(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ModuleId =?");
		sql.append(" order by ParentTag DESC ,FieldSeqOrder ASC");

		logger.trace(Literal.SQL + sql.toString());

		ExtendedFieldRowMapper rowMapper = new ExtendedFieldRowMapper(type);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, id);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * Method for using getting the list of details for Additional Details by FinanceType
	 */
	@Override
	public List<ExtendedFieldDetail> getExtendedFieldDetailBySubModule(String subModule, String type) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldDetail extendedFieldDetail = new ExtendedFieldDetail();
		extendedFieldDetail.setLovDescSubModuleName(subModule);

		StringBuilder selectSql = new StringBuilder("Select ModuleId, FieldName, FieldType, ");
		selectSql.append(" FieldLength, FieldPrec, FieldLabel, FieldMandatory, FieldConstraint, Filters,");
		selectSql.append(" FieldSeqOrder, FieldList, FieldDefaultValue, FieldMinValue, Editable, visible,");
		selectSql.append(
				" FieldMaxValue, FieldUnique, MultiLine, ParentTag, InputElement,AllowInRule, ValFromScript, Scriptlet, DefValue, ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" lovDescModuleName,lovDescSubModuleName , ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		selectSql.append(" NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From ExtendedFieldDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where lovDescSubModuleName =:lovDescSubModuleName order by FieldSeqOrder ASC");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		RowMapper<ExtendedFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ExtendedFieldDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<ExtendedFieldDetail> getExtendedFieldNameById(long id, String type) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldDetail extendedFieldDetail = new ExtendedFieldDetail();
		extendedFieldDetail.setId(id);

		StringBuilder selectSql = new StringBuilder("Select ModuleId, FieldName, FieldType, ");
		selectSql.append(" FieldLength, FieldPrec, FieldLabel, FieldMandatory, FieldConstraint, Filters,");
		selectSql.append(" FieldSeqOrder, FieldList, FieldDefaultValue, FieldMinValue, Editable, ");
		selectSql.append(
				" FieldMaxValue, FieldUnique, MultiLine, ParentTag, InputElement, AllowInRule, Visible, ValFromScript, Scriptlet, DefValue, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From ExtendedFieldDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ModuleId =:ModuleId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		RowMapper<ExtendedFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ExtendedFieldDetail.class);
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for Altering Extended Field Created table with new column
	 */
	@Override
	public void alter(ExtendedFieldDetail fieldDetail, String type, boolean drop, boolean recreate, boolean isAudit) {
		logger.debug(Literal.ENTERING);

		//fieldDetail.setLovDescErroDesc(null);
		StringBuilder syntax = new StringBuilder();

		syntax.append("alter table ");
		if (isAudit) {
			syntax.append("Adt");
		}
		syntax.append(fieldDetail.getLovDescTableName());
		syntax.append(StringUtils.trimToEmpty(type));
		syntax.append(" ");

		if (drop) {
			StringBuilder sql = new StringBuilder(syntax.toString());
			sql.append("drop column ");

			if (StringUtils.equals(fieldDetail.getFieldType(), ExtendedFieldConstants.FIELDTYPE_BASERATE)) {
				sql.append(fieldDetail.getFieldName());
				sql.append("_BR , ");
				sql.append(fieldDetail.getFieldName());
				sql.append("_SR , ");
				sql.append(fieldDetail.getFieldName());
				sql.append("_MR ");
			} else if (StringUtils.equals(fieldDetail.getFieldType(), ExtendedFieldConstants.FIELDTYPE_PHONE)) {
				sql.append(fieldDetail.getFieldName());
				sql.append("_CC , ");
				sql.append(fieldDetail.getFieldName());
				sql.append("_AC , ");
				sql.append(fieldDetail.getFieldName());
				sql.append("_SC ");
			} else {
				sql.append(fieldDetail.getFieldName());
			}

			try {
				if (isAudit) {
					this.auditJdbcTemplate.getJdbcOperations().update(sql.toString());
				} else {
					this.jdbcTemplate.getJdbcOperations().update(sql.toString());
				}
			} catch (Exception e) {
				logger.debug("Exception: ", e);
			}
		}

		if (recreate) {

			StringBuilder sql = new StringBuilder(syntax.toString());
			if (PennantConstants.RECORD_TYPE_UPD.equals(fieldDetail.getRecordType())) {
				if (App.DATABASE == Database.ORACLE) {
					sql.append("modify ");
				} else {
					sql.append("alter column ");
				}
			} else {
				sql.append("add ");
			}

			if (App.DATABASE == Database.ORACLE && (FieldType.valueOf(fieldDetail.getFieldType()) == FieldType.BASERATE
					|| FieldType.valueOf(fieldDetail.getFieldType()) == FieldType.PHONE)) {
				sql.append("(" + fieldDetail.getFieldName());
			} else {
				sql.append(fieldDetail.getFieldName());
			}

			if (!ExtendedFieldConstants.FIELDTYPE_BOOLEAN.equals(fieldDetail.getFieldType())
					&& PennantConstants.RECORD_TYPE_UPD.equals(fieldDetail.getRecordType())
					&& App.DATABASE == Database.POSTGRES
					&& (!ExtendedFieldConstants.FIELDTYPE_BASERATE.equals(fieldDetail.getFieldType())
							&& !ExtendedFieldConstants.FIELDTYPE_PHONE.equals(fieldDetail.getFieldType()))) {
				sql.append(" TYPE ");
			}

			sql.append(getDatatype(fieldDetail));
			logger.debug("SQL: " + sql.toString());

			int recordCount = 0;
			try {
				if (isAudit) {
					this.auditJdbcTemplate.getJdbcOperations().update(sql.toString());
				} else {
					this.jdbcTemplate.getJdbcOperations().update(sql.toString());
				}
			} catch (DataAccessException e) {
				fieldDetail.setLovDescErroDesc(e.getMessage());
				throw new AppException(e.getMessage(), e);
			}

			if (recordCount < 0) {
				throw new ConcurrencyException();
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private String getDatatype(ExtendedFieldDetail fieldDetail) {
		StringBuilder datatype = new StringBuilder();

		switch (FieldType.valueOf(fieldDetail.getFieldType())) {
		case TEXT:
		case UPPERTEXT:
		case MULTILINETEXT:
		case EXTENDEDCOMBO:
		case STATICCOMBO:
		case MULTISTATICCOMBO:
		case MULTIEXTENDEDCOMBO:
		case RADIO:
		case LISTFIELD:
			if (App.DATABASE == Database.ORACLE) {
				datatype.append(" varchar2(");
				datatype.append(fieldDetail.getFieldLength());
				datatype.append(") ");
			} else {
				datatype.append(" varchar(");
				datatype.append(fieldDetail.getFieldLength());
				datatype.append(") ");
			}
			break;
		case CURRENCY:
			if (App.DATABASE == Database.ORACLE) {
				datatype.append(" number(");
				datatype.append(fieldDetail.getFieldLength());
				datatype.append(", 0) ");
			} else if (App.DATABASE == Database.POSTGRES) {
				datatype.append(" numeric(");
				datatype.append(fieldDetail.getFieldLength());
				datatype.append(", 0) ");
			} else {
				datatype.append(" decimal(");
				datatype.append(fieldDetail.getFieldLength());
				datatype.append(", 0) ");
			}
			break;
		case INT:
			if (App.DATABASE == Database.ORACLE) {
				datatype.append(" number(10,0) ");
			} else if (App.DATABASE == Database.POSTGRES) {
				datatype.append(" integer ");
			} else {
				datatype.append(" int ");
			}
			break;
		case LONG:
			if (App.DATABASE == Database.ORACLE) {
				datatype.append(" number (19,0) ");
			} else {
				datatype.append(" bigint ");
			}
			break;
		case ACTRATE:
		case DECIMAL:
		case PERCENTAGE:
			if (App.DATABASE == Database.ORACLE) {
				datatype.append(" number(");
				datatype.append(fieldDetail.getFieldLength()).append(", ");
				datatype.append(fieldDetail.getFieldPrec()).append(") ");
			} else if (App.DATABASE == Database.POSTGRES) {
				datatype.append(" numeric(");
				datatype.append(fieldDetail.getFieldLength()).append(", ");
				datatype.append(fieldDetail.getFieldPrec()).append(") ");
			} else {
				datatype.append(" decimal(");
				datatype.append(fieldDetail.getFieldLength()).append(", ");
				datatype.append(fieldDetail.getFieldPrec()).append(") ");
			}
			break;
		case DATE:
		case DATETIME:
		case TIME:
			if (App.DATABASE == Database.ORACLE) {
				datatype.append(" date ");
			} else if (App.DATABASE == Database.POSTGRES) {
				datatype.append(" timestamp without time zone ");
			} else {
				datatype.append(" datetime ");
			}
			break;
		case BOOLEAN:
			if (App.DATABASE == Database.ORACLE) {
				datatype.append(" number (1,0) ");
			} else if (App.DATABASE == Database.POSTGRES) {
				if (!PennantConstants.RECORD_TYPE_UPD.equals(fieldDetail.getRecordType())) {
					datatype.append(" boolean ");
					datatype.append(" DEFAULT FALSE ");
				} else {
					datatype.append(" SET DEFAULT FALSE  ");
				}
			} else {
				datatype.append(" bit DEFAULT (0) ");
			}
			break;
		case ACCOUNT:
			if (App.DATABASE == Database.ORACLE) {
				datatype.append(" varchar2(50) ");
			} else {
				datatype.append(" varchar(50) ");
			}
			break;
		case FREQUENCY:
			if (App.DATABASE == Database.ORACLE) {
				datatype.append(" varchar2(5) ");
			} else {
				datatype.append(" varchar(5) ");
			}
			break;
		case BASERATE:
			if (App.DATABASE == Database.ORACLE) {
				datatype.append("_BR varchar2(8) , ");
				datatype.append(fieldDetail.getFieldName());
				datatype.append("_SR varchar2(8) , ");
				datatype.append(fieldDetail.getFieldName());
				datatype.append("_MR number(13,9) ) ");
			} else if (App.DATABASE == Database.POSTGRES) {
				datatype.append("_BR  ");
				if (!ExtendedFieldConstants.FIELDTYPE_BOOLEAN.equals(fieldDetail.getFieldType())
						&& PennantConstants.RECORD_TYPE_UPD.equals(fieldDetail.getRecordType())) {
					datatype.append("TYPE varchar(8) , ");
					datatype.append(" alter ");
					datatype.append(fieldDetail.getFieldName() + "_SR ");
					datatype.append("TYPE varchar(8) , ");
					datatype.append(" alter ");
					datatype.append(fieldDetail.getFieldName() + "_MR ");
					datatype.append("TYPE decimal(13,9) ");
				} else {
					datatype.append("varchar(8) , ");
					datatype.append(" add ");
					datatype.append(fieldDetail.getFieldName());
					datatype.append("_SR varchar(8) , ");
					datatype.append(" add ");
					datatype.append(fieldDetail.getFieldName());
					datatype.append("_MR decimal(13,9) ");
				}
			} else {
				datatype.append("_BR varchar(8) , ");
				datatype.append(fieldDetail.getFieldName());
				datatype.append("_SR varchar(8) , ");
				datatype.append(fieldDetail.getFieldName());
				datatype.append("_MR decimal(13,9) ");
			}
			break;
		case ADDRESS://TODO : Divide columns into multiple based on component definition
			if (App.DATABASE == Database.ORACLE) {
				datatype.append(" varchar2(100) ");
			} else {
				datatype.append(" varchar(100) ");
			}
			break;
		case PHONE:
			if (App.DATABASE == Database.ORACLE) {
				datatype.append("_CC varchar2(4) , ");
				datatype.append(fieldDetail.getFieldName());
				datatype.append("_AC varchar2(4) , ");
				datatype.append(fieldDetail.getFieldName());
				datatype.append("_SC varchar2(8) ) ");
			} else if (App.DATABASE == Database.POSTGRES) {
				datatype.append("_CC ");
				if (!ExtendedFieldConstants.FIELDTYPE_BOOLEAN.equals(fieldDetail.getFieldType())
						&& PennantConstants.RECORD_TYPE_UPD.equals(fieldDetail.getRecordType())) {
					datatype.append("TYPE varchar(4) , ");
					datatype.append(" alter ");
					datatype.append(fieldDetail.getFieldName() + "_AC ");
					datatype.append("TYPE varchar(4) , ");
					datatype.append(" alter ");
					datatype.append(fieldDetail.getFieldName() + "_SC ");
					datatype.append("TYPE varchar(8) ");
				} else {
					datatype.append("varchar(4) , ");
					datatype.append(" add ");
					datatype.append(fieldDetail.getFieldName());
					datatype.append("_AC varchar(4) , ");
					datatype.append(" add ");
					datatype.append(fieldDetail.getFieldName());
					datatype.append("_SC varchar(8) ");
				}
			} else {
				datatype.append("_CC varchar(4) , ");
				datatype.append(fieldDetail.getFieldName());
				datatype.append("_AC varchar(4) , ");
				datatype.append(fieldDetail.getFieldName());
				datatype.append("_SC varchar(8) ");
			}
			break;
		}
		return datatype.toString();
	}

	@Override
	public void saveAdditional(final String id, Map<String, Object> mappedValues, String type, String tableName) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder(" INSERT INTO " + tableName);
		insertSql.append(StringUtils.trimToEmpty(type));

		if (mappedValues.containsKey("FinReference")) {
			mappedValues.remove("FinReference");
		}
		mappedValues.put("FinReference", id);

		List<String> list = new ArrayList<String>(mappedValues.keySet());
		String columnames = "";
		String columnValues = "";
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() - 1) {
				columnames = columnames.concat(list.get(i)).concat(" , ");
				columnValues = columnValues.concat(":").concat(list.get(i)).concat(" , ");
			} else {
				columnames = columnames.concat(list.get(i));
				columnValues = columnValues.concat(":").concat(list.get(i));
			}
		}
		insertSql.append(" (".concat(columnames).concat(") values (").concat(columnValues).concat(")"));
		logger.debug("insertSql: " + insertSql.toString());
		this.jdbcTemplate.update(insertSql.toString(), mappedValues);
		logger.debug(Literal.LEAVING);

	}

	@Override
	public void saveAdditional(String primaryKeyColumn, final Serializable id, HashMap<String, Object> mappedValues,
			String type, String tableName) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" INSERT INTO " + tableName);
		sql.append(StringUtils.trimToEmpty(type));

		if (mappedValues.containsKey(primaryKeyColumn)) {
			mappedValues.remove(primaryKeyColumn);
		}
		mappedValues.put(primaryKeyColumn, id);

		List<String> list = new ArrayList<String>(mappedValues.keySet());
		String columnames = "";
		String columnValues = "";
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() - 1) {
				columnames = columnames.concat(list.get(i)).concat(" , ");
				columnValues = columnValues.concat(":").concat(list.get(i)).concat(" , ");
			} else {
				columnames = columnames.concat(list.get(i));
				columnValues = columnValues.concat(":").concat(list.get(i));
			}
		}
		sql.append(" (").append(columnames).append(") values (").append(columnValues).append(")");
		logger.debug("insertSql: " + sql.toString());
		this.jdbcTemplate.update(sql.toString(), mappedValues);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Method for Retrive Extended Field Values depend on SubModule
	 */
	@Override
	public Map<String, Object> retrive(String tableName, String id, String type) {
		logger.debug(Literal.ENTERING);
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuilder selectSql = new StringBuilder("Select * from " + tableName);
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where FinReference ='" + id + "'");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			map = this.jdbcTemplate.queryForMap(selectSql.toString(), map);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			if ("_Temp".equals(type)) {
				selectSql = new StringBuilder("Select * from " + tableName);
				selectSql.append(" where FinReference ='" + id + "'");

				logger.debug("selectSql: " + selectSql.toString());
				try {
					map = this.jdbcTemplate.queryForMap(selectSql.toString(), map);
				} catch (EmptyResultDataAccessException ex) {
					logger.warn("Exception: ", ex);
					map = null;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return map;
	}

	/**
	 * Method for Retrieve Extended Field Values depend on SubModule
	 */
	@Override
	public Map<String, Object> retrive(String tableName, String primaryKeyColumn, Serializable id, String type) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		Map<String, Object> map = new HashMap<String, Object>();

		StringBuilder query = new StringBuilder("Select * from ");
		query.append(tableName);
		query.append(type);
		query.append(" where ");
		query.append(primaryKeyColumn);
		query.append(" = :Id ");

		source.addValue("ColumnName", primaryKeyColumn);
		source.addValue("Id", id);

		logger.debug("selectSql: " + query.toString());
		try {
			map = this.jdbcTemplate.queryForMap(query.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			if ("_Temp".equals(type)) {
				query = new StringBuilder("Select * from " + tableName);
				query.append(" where " + primaryKeyColumn + " ='" + id + "'");

				logger.debug("selectSql: " + query.toString());
				try {
					map = this.jdbcTemplate.queryForMap(query.toString(), map);
				} catch (EmptyResultDataAccessException ex) {
					logger.warn("Exception: ", ex);
					map = null;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return map;
	}

	/**
	 * Method for Find Extended Field Values are Exist or not
	 */
	@Override
	public boolean isExist(String tableName, String id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder("Select FinReference from " + tableName);
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where FinReference ='" + id + "'");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			this.jdbcTemplate.getJdbcOperations().queryForObject(selectSql.toString(), String.class);
			return true;
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return false;
		}
	}

	/**
	 * Method for Find Extended Field Values are Exist or not
	 */
	@Override
	public boolean isExist(String tableName, String primaryKeyColumn, Serializable id, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder query = new StringBuilder();
		query.append("Select " + primaryKeyColumn + " from ");
		query.append(tableName);
		query.append(StringUtils.trimToEmpty(type));
		query.append(" where ");
		query.append(primaryKeyColumn);
		query.append("  = :Id");

		source.addValue("Id", id);

		logger.debug("selectSql: " + query.toString());
		try {
			this.jdbcTemplate.queryForObject(query.toString(), source, String.class);
			return true;
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return false;
		}
	}

	@Override
	public void updateAdditional(Map<String, ?> mappedValues, final String id, String type, String tableName) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder(" UPDATE " + tableName);
		insertSql.append(StringUtils.trimToEmpty(type));
		List<String> list = new ArrayList<String>(mappedValues.keySet());
		String query = "";

		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				query = " set ".concat(list.get(i)).concat("=:").concat(list.get(i));
			} else {
				query = query.concat(",").concat(list.get(i)).concat("=:").concat(list.get(i));
			}
		}
		insertSql.append(query);
		insertSql.append(" where FinReference='").append(id).append("'");

		logger.debug("insertSql: " + insertSql.toString());
		this.jdbcTemplate.update(insertSql.toString(), mappedValues);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateAdditional(String primaryKeyColumn, final Serializable id, HashMap<String, Object> mappedValues,
			String type, String tableName) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder(" UPDATE " + tableName);
		insertSql.append(StringUtils.trimToEmpty(type));
		List<String> list = new ArrayList<String>(mappedValues.keySet());
		String query = "";

		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				query = " set ".concat(list.get(i)).concat("=:").concat(list.get(i));
			} else {
				query = query.concat(",").concat(list.get(i)).concat("=:").concat(list.get(i));
			}
		}
		insertSql.append(query);
		insertSql.append(" where ").append(primaryKeyColumn).append("='").append(id).append("'");

		logger.debug("insertSql: " + insertSql.toString());
		this.jdbcTemplate.update(insertSql.toString(), mappedValues);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method Deletes the Record from the ExtendedFieldDetail or ExtendedFieldDetail_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete Extended Field Detail by key ModuleId
	 * 
	 * @param Extended
	 *            Field Detail (extendedFieldDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteAdditional(final String id, String tableName, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder deleteSql = new StringBuilder("Delete From " + tableName);
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where FinReference ='" + id + "'");

		logger.debug("deleteSql: " + deleteSql.toString());
		this.jdbcTemplate.getJdbcOperations().update(deleteSql.toString());
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteAdditional(String primaryKeyColumn, final Serializable id, String type, String tableName) {
		logger.debug(Literal.ENTERING);
		StringBuilder deleteSql = new StringBuilder("Delete From " + tableName);
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where " + primaryKeyColumn + " ='" + id + "'");

		logger.debug("deleteSql: " + deleteSql.toString());
		this.jdbcTemplate.getJdbcOperations().update(deleteSql.toString());
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void revertColumn(ExtendedFieldDetail efd) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From ExtendedFieldDetail_Temp");
		sql.append(" Where ModuleId =:ModuleId AND FieldName =:FieldName");

		try {
			this.jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource(efd));
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);

			logger.debug(Literal.LEAVING);
		}
	}

	@Override
	public List<ExtendedFieldDetail> getExtendedFieldDetailById(long id, int extendedType, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ModuleId = ? and ExtendedType = ?");
		sql.append(" order by FieldSeqOrder ASC");

		logger.trace(Literal.SQL + sql.toString());

		ExtendedFieldRowMapper rowMapper = new ExtendedFieldRowMapper(type);

		List<ExtendedFieldDetail> efd = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, id);
			ps.setLong(index++, extendedType);

		}, rowMapper);
		return sortExtendFields(efd);
	}

	private List<ExtendedFieldDetail> sortExtendFields(List<ExtendedFieldDetail> efd) {
		return efd.stream().sorted((fld1, fld2) -> Integer.compare(fld1.getFieldSeqOrder(), fld2.getFieldSeqOrder()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ExtendedFieldDetail> getExtendedFieldDetailForRule() {
		logger.debug(Literal.ENTERING);
		ExtendedFieldDetail extendedFieldDetail = new ExtendedFieldDetail();
		extendedFieldDetail.setAllowInRule(true);

		StringBuilder selectSql = new StringBuilder("Select ModuleId, FieldName, FieldType, ");
		selectSql.append(" FieldLength, FieldPrec, FieldLabel, FieldMandatory, FieldConstraint, Filters,");
		selectSql.append(" FieldSeqOrder, FieldList, FieldDefaultValue, FieldMinValue, Editable, ");
		selectSql.append(" FieldMaxValue, FieldUnique, MultiLine, ParentTag, InputElement,AllowInRule, ValFromScript,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" ,lovDescModuleName,lovDescSubModuleName ");
		selectSql.append(" From ExtendedFieldDetail_AView");
		selectSql.append(
				" Where AllowInRule=:AllowInRule order by lovDescModuleName ASC,lovDescSubModuleName ASC, ParentTag DESC ,FieldSeqOrder ASC");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		RowMapper<ExtendedFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ExtendedFieldDetail.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public ExtendedFieldDetail getExtendedFieldDetailById(long id, String fieldName, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ModuleId = ? and FieldName= ?");

		logger.trace(Literal.SQL + sql.toString());

		ExtendedFieldRowMapper rowMapper = new ExtendedFieldRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id, fieldName }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<String> getFieldNames(String moduleName, String subModuleName) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select EFD.FIELDNAME from EXTENDEDFIELDHEADER EFH ");
		sql.append("inner join ExtendedFieldDetail EFD ON EFD.ModuleId = EFH.MODULEID");
		sql.append(
				" where EFH.ModuleName = :moduleName and EFH.SUBMODULENAME= :subModuleName and EFD.extendedtype = :extendedType");
		sql.append(" AND EFD.FIELDTYPE NOT IN('GROUPBOX','LISTBOX','TABPANEL','BUTTON') ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("moduleName", moduleName);
		paramSource.addValue("subModuleName", subModuleName);
		paramSource.addValue("extendedType", 0);
		try {
			return this.jdbcTemplate.queryForList(sql.toString(), paramSource, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	public void setAuditDataSource(DataSource dataSource) {
		this.auditJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	// Extended fields Extended combobox description--04-09-2019
	@Override
	public String getExtFldDesc(String sql) {

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(String.class);
		try {
			return this.jdbcTemplate.queryForObject(sql, beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
		}
		return null;
	}

	@Override
	public String getExtFldDesc(String tableName, String value) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("value", value);

		StringBuilder sql = new StringBuilder("SELECT Value From ".concat(tableName));
		sql.append(" where key = :value");

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
		}
		return null;
	}

	@Override
	public String getExtFldIndustryMargin(String tableName, String type, String industry, String segment,
			String product) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Type", type);
		source.addValue("Industry", industry);
		source.addValue("Segment", segment);
		source.addValue("Product", product);

		StringBuilder sql = new StringBuilder("SELECT Margin From ".concat(tableName));
		sql.append(" where Type = :Type and");
		sql.append(" Industry = :Industry and");
		sql.append(" Segment = :Segment and");
		sql.append(" Product = :Product");

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
		}
		return null;
	}

	private class ExtendedFieldRowMapper implements RowMapper<ExtendedFieldDetail> {
		private String type;

		private ExtendedFieldRowMapper(String type) {
			this.type = type;
		}

		@Override
		public ExtendedFieldDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			ExtendedFieldDetail efd = new ExtendedFieldDetail();

			efd.setModuleId(rs.getLong("ModuleId"));
			efd.setFieldName(rs.getString("FieldName"));
			efd.setFieldType(rs.getString("FieldType"));
			efd.setFieldLength(rs.getInt("FieldLength"));
			efd.setFieldPrec(rs.getInt("FieldPrec"));
			efd.setFieldLabel(rs.getString("FieldLabel"));
			efd.setFieldMandatory(rs.getBoolean("FieldMandatory"));
			efd.setFieldConstraint(rs.getString("FieldConstraint"));
			efd.setFieldSeqOrder(rs.getInt("FieldSeqOrder"));
			efd.setFieldList(rs.getString("FieldList"));
			efd.setFieldDefaultValue(rs.getString("FieldDefaultValue"));
			efd.setFieldMinValue(rs.getLong("FieldMinValue"));
			efd.setFilters(rs.getString("Filters"));
			efd.setFieldMaxValue(rs.getLong("FieldMaxValue"));
			efd.setFieldUnique(rs.getBoolean("FieldUnique"));
			efd.setMultiLine(rs.getInt("MultiLine"));
			efd.setParentTag(rs.getString("ParentTag"));
			efd.setInputElement(rs.getBoolean("InputElement"));
			efd.setEditable(rs.getBoolean("Editable"));
			efd.setVisible(rs.getBoolean("Visible"));
			efd.setAllowInRule(rs.getBoolean("AllowInRule"));
			efd.setValFromScript(rs.getBoolean("ValFromScript"));
			efd.setScriptlet(rs.getString("Scriptlet"));
			efd.setVersion(rs.getInt("Version"));
			efd.setLastMntBy(rs.getLong("LastMntBy"));
			efd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			efd.setRecordStatus(rs.getString("RecordStatus"));
			efd.setRoleCode(rs.getString("RoleCode"));
			efd.setNextRoleCode(rs.getString("NextRoleCode"));
			efd.setTaskId(rs.getString("TaskId"));
			efd.setNextTaskId(rs.getString("NextTaskId"));
			efd.setRecordType(rs.getString("RecordType"));
			efd.setWorkflowId(rs.getLong("WorkflowId"));
			efd.setDefValue(rs.getString("DefValue"));
			efd.setAgrField(rs.getString("AgrField"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				efd.setLovDescModuleName(rs.getString("LovDescModuleName"));
				efd.setLovDescSubModuleName(rs.getString("LovDescSubModuleName"));
			}

			return efd;
		}
	}

	@Override
	public Map<String, Object> getValueByFieldName(String reference, String moduleName, String subModuleName,
			String event, String field, String type) {

		StringBuilder sql = null;

		Map<String, Object> values = null;
		MapSqlParameterSource source = null;
		sql = new StringBuilder();
		sql.append(" select ".concat(field));
		sql.append(" from ");
		sql.append(moduleName);
		sql.append("_");
		sql.append(subModuleName);
		sql.append("_ed");
		sql.append(type);
		sql.append(" where reference = :reference");

		source = new MapSqlParameterSource();
		source.addValue("reference", reference);
		try {
			values = jdbcTemplate.queryForMap(sql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return values;
	}

	@Override
	public List<ExtendedFieldDetail> getCollateralExtDetails(String moduleName, String subModuleName) {

		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" fieldtype,agrfield,fieldName,fieldList FROM extendedFieldDetail ed");
		sql.append(" inner join  extendedfieldheader eh on eh.moduleid = ed.moduleid");
		sql.append(" where eh.modulename= :moduleName and eh.submodulename = :subModuleName and  agrfield is not null");

		logger.debug(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("moduleName", moduleName);
		source.addValue("subModuleName", subModuleName);

		RowMapper<ExtendedFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ExtendedFieldDetail.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

}