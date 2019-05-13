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
 * FileName    		:  FinCovenantTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
 * 16-05-2018       Pennant                  0.2           added the flag alwOtc                                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.dao.finance.covenant.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.covenant.CovenantsDAO;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>FinCovenantType model</b> class.<br>
 * 
 */

public class CovenantsDAOImpl extends SequenceDao<FinCovenantType> implements CovenantsDAO {
	private static Logger logger = Logger.getLogger(CovenantsDAOImpl.class);

	public CovenantsDAOImpl() {
		super();
	}

	@Override
	public Covenant getCovenant(long id, String module, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(tableType.getSuffix());
		sql.append(" Where Id = :Id And Module = :Module ");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Id", id);
		source.addValue("Module", module);

		RowMapper<Covenant> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Covenant.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<Covenant> getCovenants(final String finreference, String module, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(tableType.getSuffix());
		sql.append(" Where KeyReference = :KeyReference And Module = :Module");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("KeyReference", finreference);
		source.addValue("Module", module);

		RowMapper<Covenant> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Covenant.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<Covenant>();
	}

	@Override
	public List<CovenantDocument> getCovenantDocuments(long covenantId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select * from Covenant_Documents");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where CovenantId = :CovenantId");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CovenantId", covenantId);

		RowMapper<CovenantDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CovenantDocument.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public String save(Covenant covenant, TableType tableType) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();

		sql.append(" Insert Into Covenants");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" (Id, KeyReference, Module, CovenantTypeId, LOS, MandatoryRole, OTC, PDD, ReceivableDate");
		sql.append(", AllowWaiver, MaxAllowedDays, DocumentReceived, DocumentReceivedDate, AllowPostponement");
		sql.append(", ExtendedDate, AllowedPaymentModes, Frequency, NextFrequencyDate, GraceDays");
		sql.append(", GraceDueDate, AlertsRequired, AlertType, AlertToRoles, AlertDays, InternalUse, Remarks");
		sql.append(", AdditionalField1, AdditionalField2, AdditionalField3, AdditionalField4");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkFlowId)");
		sql.append(" values(:Id, :KeyReference, :Module , :CovenantTypeId, :Los, :MandatoryRole, :Otc, :Pdd");
		sql.append(", :ReceivableDate, :AllowWaiver, :MaxAllowedDays, :DocumentReceived, :DocumentReceivedDate");
		sql.append(", :allowPostPonement, :ExtendedDate, :AllowedPaymentModes, :Frequency, :NextFrequencyDate");
		sql.append(", :GraceDays, :GraceDueDate, :alertsRequired, :alertType, :AlertToRoles, :AlertDays");
		sql.append(", :InternalUse, :Remarks, :AdditionalField1, :AdditionalField2, :AdditionalField3");
		sql.append(", :AdditionalField4, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode");
		sql.append(", :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (covenant.getId() == Long.MIN_VALUE) {
			covenant.setId(getNextValue("SeqCovenants"));
		}

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(covenant);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
		return String.valueOf(covenant.getId());
	}

	@Override
	public void saveDocuments(List<CovenantDocument> covenantDocuments, TableType tableType) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();

		sql.append(" Insert Into Covenant_Documents");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" (Id, CovenantId, CovenantType, ReceivableDate, FrequencyDate, DocumentReceivedDate, DocumentId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkFlowId)");
		sql.append(" values(:Id, :CovenantId, :CovenantType, :ReceivableDate, :frequencyDate, :DocumentReceivedDate");
		sql.append(", :DocumentId, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug(Literal.SQL + sql.toString());

		for (CovenantDocument covenantDocument : covenantDocuments) {
			if (covenantDocument.getId() == Long.MIN_VALUE) {
				covenantDocument.setId(getNextValue("SEQCOVENANT_DOCUMENTS"));
			}
		}

		try {
			jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(covenantDocuments.toArray()));
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void update(Covenant finCovenantType, TableType tableType) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update Covenants");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));

		sql.append(" set LOS = :Los, MandatoryRole = :MandatoryRole, OTC = :Otc, PDD = :Pdd");
		sql.append(", ReceivableDate = :ReceivableDate, AllowWaiver = :AllowWaiver, MaxAllowedDays = :MaxAllowedDays");
		sql.append(", DocumentReceived = :DocumentReceived, DocumentReceivedDate = :DocumentReceivedDate");
		sql.append(", allowPostPonement = :allowPostPonement, ExtendedDate = :ExtendedDate");
		sql.append(
				", AllowedPaymentModes = :AllowedPaymentModes, Frequency = :Frequency, NextFrequencyDate = :NextFrequencyDate");
		sql.append(", GraceDays = :GraceDays, GraceDueDate = :GraceDueDate, AlertsRequired = :alertsRequired");
		sql.append(", AlertType = :alertType, AlertToRoles = :AlertToRoles, AlertDays = :AlertDays");
		sql.append(", InternalUse = :InternalUse, Remarks = :Remarks, AdditionalField1 = :AdditionalField1");
		sql.append(", AdditionalField2 = :AdditionalField2, AdditionalField3 = :AdditionalField3");
		sql.append(", AdditionalField4 = :AdditionalField4, Version = :Version, LastMntBy = :LastMntBy");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkFlowId = :WorkflowId");
		sql.append("  Where Id = :Id And Module = :Module ");

		if (!tableType.name().endsWith("_Temp")) {
			//sql.append("  AND Version= :Version-1");
		}

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateDocuments(List<CovenantDocument> covenantDocuments, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update Covenant_Documents");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));

		sql.append(" set ReceivableDate = :ReceivableDate, FrequencyDate = :frequencyDate");
		sql.append(", DocumentReceivedDate = :DocumentReceivedDate, DocumentId = :DocumentId");
		sql.append(", Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus = :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkFlowId = :WorkflowId");
		sql.append("  Where Id = :Id and CovenantType = :CovenantType");

		if (!tableType.name().endsWith("_Temp")) {
			//sql.append("  AND Version= :Version-1");
		}

		logger.debug(Literal.SQL + sql.toString());

		int[] recordCount;
		try {
			recordCount = jdbcTemplate.batchUpdate(sql.toString(),
					SqlParameterSourceUtils.createBatch(covenantDocuments.toArray()));
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		if (recordCount.length <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteByFinRef(String loanReference, String tableType) {
		logger.debug(Literal.ENTERING);
		FinCovenantType finCovenantType = new FinCovenantType();
		finCovenantType.setId(loanReference);

		StringBuilder deleteSql = new StringBuilder("Delete From Covenants");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where KeyReference = :KeyReference ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);

	}

	@Override
	public boolean isDuplicateKey(String finReference, String covenantType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "KeyReference = :KeyReference and CovenantTypeId = :CovenantTypeId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Covenants", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Covenants_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Covenants_Temp", "Covenants" }, whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("KeyReference", finReference);
		paramSource.addValue("CovenantTypeId", covenantType);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public void delete(Covenant covenant, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete from Covenants");
		sql.append(tableType.getSuffix());
		sql.append(" Where KeyReference = :KeyReference AND CovenantTypeId = :CovenantTypeId And Module = :Module ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(covenant);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteDocuments(List<CovenantDocument> documents, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from Covenant_Documents");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = :Id");

		logger.trace(Literal.SQL + sql.toString());

		int[] recordCount;
		try {
			recordCount = jdbcTemplate.batchUpdate(sql.toString(),
					SqlParameterSourceUtils.createBatch(documents.toArray()));
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		if (recordCount.length <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isExists(Covenant covenant, TableType tableType) {
		logger.debug(Literal.ENTERING);

		int count = 0;

		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM COVENANTS");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where KeyReference = :KeyReference and CovenantTypeId = :CovenantTypeId And Module = :Module");
		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(covenant);

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			count = 0;
		}

		logger.debug(Literal.LEAVING);
		return count > 0 ? true : false;
	}

	private StringBuilder getSelectQuery(String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Id, KeyReference, Module, CovenantTypeId, LOS, MandatoryRole, OTC, PDD, ReceivableDate");
		sql.append(", AllowWaiver, MaxAllowedDays, DocumentReceived, DocumentReceivedDate");
		sql.append(", AllowPostPonement, ExtendedDate, AllowedPaymentModes, Frequency, NextFrequencyDate");
		sql.append(", GraceDays, GraceDueDate, AlertsRequired, AlertType, AlertToRoles, AlertDays");
		sql.append(", InternalUse, Remarks, AdditionalField1, AdditionalField2, AdditionalField3, AdditionalField4");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", Category, CovenantTypeDescription, CovenantType, CovenantTypeCode");
		}
		sql.append(", Version, LastMntby, LastMnton, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId From Covenants");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	@Override
	public List<Covenant> getCovenantsAlertList() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append("select distinct c.Id, c.CovenantTypeId");
		sql.append(", ut.TemplateCode userTemplateCode, cust.TemplateCode customerTemplateCode");
		sql.append(", c.alerttoRoles, ct.Code, ct.Description, c.KeyReference, fm.FinType");
		sql.append(", c.frequency, c.nextfrequencydate, c.alertDays");
		sql.append(", ct.docType, dt.docTypeDesc docTypeName, ca.alertsentOn");
		sql.append(" from covenants c");
		sql.append(" inner join covenant_Types ct on ct.id = c.covenantTypeId");
		sql.append(" left join bmtdocumenttypes dt on dt.doctypeCode = ct.docType");
		sql.append(" inner join financemain fm on fm.finreference = c.KeyReference and closingstatus is null");
		sql.append(" left join covenant_documents cd on cd.covenantid = c.id");
		sql.append(" left join (select covenantId, max(alertsentOn) alertsentOn");
		sql.append(" from covenant_alerts group by covenantId) ca on ca.covenantId = c.id");
		sql.append(" left join Templates ut on ut.TemplateId = ct.userTemplate");
		sql.append(" left join Templates cust on cust.TemplateId = ct.customertemplate");
		sql.append(" where nextfrequencydate is not null and ");
		sql.append(" cd.frequencydate is null or cd.frequencydate != c.nextfrequencydate");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();

		RowMapper<Covenant> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Covenant.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}
	
	@Override
	public List<Covenant> getCovenants(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		
		sql.append("select C.Frequency, NextFrequencyDate, CovenantTypeCode, CovenantTypeDescription");
		sql.append(", DocType, DocTypeName from covenants_aview c");
		sql.append(" inner join COVENANT_TYPES_AVIEW ct on ct.id = c.covenantTypeId");
		sql.append(" left join COVENANT_DOCUMENTS cd on cd.CovenantId = c.id");
		sql.append(" where KeyReference = :KeyReference and c.NextFrequencyDate is not null and pdd = :pdd");
		sql.append(" and cd.frequencydate is null ");
		sql.append(" order by NextFrequencyDate");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("KeyReference", finReference);
		source.addValue("pdd", 1);

		RowMapper<Covenant> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Covenant.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

}