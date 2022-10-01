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
 * * FileName : FinCovenantTypeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * *
 * Modified Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * 16-05-2018 Pennant 0.2 added the flag alwOtc * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.covenant.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.finance.covenant.CovenantsDAO;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>FinCovenantType model</b> class.<br>
 * 
 */

public class CovenantsDAOImpl extends SequenceDao<FinCovenantType> implements CovenantsDAO {
	private static Logger logger = LogManager.getLogger(CovenantsDAOImpl.class);

	public CovenantsDAOImpl() {
		super();
	}

	@Override
	public Covenant getCovenant(long id, String module, TableType tableType) {
		StringBuilder sql = getSelectQuery(tableType.getSuffix());
		sql.append(" Where Id = ? and Module = ?");

		logger.debug(Literal.SQL + sql.toString());

		CovenantsRowMapper rowMapper = new CovenantsRowMapper(tableType.getSuffix());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, id, module);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<Covenant> getCovenants(final String finreference, String module, TableType tableType) {
		StringBuilder sql = getSelectQuery(tableType.getSuffix());
		sql.append(" Where KeyReference = ? And Module = ?");

		logger.debug(Literal.SQL + sql.toString());

		CovenantsRowMapper rowMapper = new CovenantsRowMapper(tableType.getSuffix());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, finreference);
			ps.setString(index, module);
		}, rowMapper);
	}

	@Override
	public List<CovenantDocument> getCovenantDocuments(long covenantId, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select * From Covenant_Documents");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where CovenantId = :CovenantId");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CovenantId", covenantId);

		RowMapper<CovenantDocument> typeRowMapper = BeanPropertyRowMapper.newInstance(CovenantDocument.class);

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public String save(Covenant covenant, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into Covenants");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" (Id, KeyReference, Module, CovenantTypeId, Los, MandatoryRole, Otc, Pdd, ReceivableDate");
		sql.append(", AllowWaiver, MaxAllowedDays, DocumentReceived, DocumentReceivedDate, AllowPostponement");
		sql.append(", ExtendedDate, AllowedPaymentModes, Frequency, NextFrequencyDate, GraceDays");
		sql.append(", GraceDueDate, AlertsRequired, AlertType, AlertToRoles, AlertDays, InternalUse, Remarks");
		sql.append(", Remarks1, AdditionalField1, AdditionalField2, AdditionalField3, AdditionalField4");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkFlowId)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		if (covenant.getId() == Long.MIN_VALUE) {
			covenant.setId(getNextValue("SeqCovenants"));
		}

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, covenant.getId());
			ps.setString(index++, covenant.getKeyReference());
			ps.setString(index++, covenant.getModule());
			ps.setLong(index++, covenant.getCovenantTypeId());
			ps.setBoolean(index++, covenant.isLos());
			ps.setString(index++, covenant.getMandatoryRole());
			ps.setBoolean(index++, covenant.isOtc());
			ps.setBoolean(index++, covenant.isPdd());
			ps.setDate(index++, JdbcUtil.getDate(covenant.getReceivableDate()));
			ps.setBoolean(index++, covenant.isAllowWaiver());
			ps.setInt(index++, covenant.getMaxAllowedDays());
			ps.setBoolean(index++, covenant.isDocumentReceived());
			ps.setDate(index++, JdbcUtil.getDate(covenant.getDocumentReceivedDate()));
			ps.setBoolean(index++, covenant.isAllowPostPonement());
			ps.setDate(index++, JdbcUtil.getDate(covenant.getExtendedDate()));
			ps.setString(index++, covenant.getAllowedPaymentModes());
			ps.setString(index++, covenant.getFrequency());
			ps.setDate(index++, JdbcUtil.getDate(covenant.getNextFrequencyDate()));
			ps.setInt(index++, covenant.getGraceDays());
			ps.setDate(index++, JdbcUtil.getDate(covenant.getGraceDueDate()));
			ps.setBoolean(index++, covenant.isAlertsRequired());
			ps.setString(index++, covenant.getAlertType());
			ps.setString(index++, covenant.getAlertToRoles());
			ps.setInt(index++, covenant.getAlertDays());
			ps.setBoolean(index++, covenant.isInternalUse());
			ps.setString(index++, covenant.getRemarks());
			ps.setBytes(index++, covenant.getRemarks1());
			ps.setString(index++, covenant.getAdditionalField1());
			ps.setString(index++, covenant.getAdditionalField2());
			ps.setString(index++, covenant.getAdditionalField3());
			ps.setString(index++, covenant.getAdditionalField4());
			ps.setInt(index++, covenant.getVersion());
			ps.setLong(index++, covenant.getLastMntBy());
			ps.setTimestamp(index++, covenant.getLastMntOn());
			ps.setString(index++, covenant.getRecordStatus());
			ps.setString(index++, covenant.getRoleCode());
			ps.setString(index++, covenant.getNextRoleCode());
			ps.setString(index++, covenant.getTaskId());
			ps.setString(index++, covenant.getNextTaskId());
			ps.setString(index++, covenant.getRecordType());
			ps.setLong(index, covenant.getWorkflowId());
		});

		return String.valueOf(covenant.getId());
	}

	@Override
	public void saveDocuments(List<CovenantDocument> documents, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into Covenant_Documents");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" (Id, CovenantId, CovenantType, ReceivableDate, FrequencyDate, DocumentReceivedDate");
		sql.append(", DocumentId, OriginalDocument, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					CovenantDocument cd = documents.get(i);
					int index = 1;

					if (cd.getId() == Long.MIN_VALUE) {
						cd.setId(getNextValue("SEQCOVENANT_DOCUMENTS"));
					}

					ps.setLong(index++, cd.getId());
					ps.setLong(index++, cd.getCovenantId());
					ps.setString(index++, cd.getCovenantType());
					ps.setDate(index++, JdbcUtil.getDate(cd.getReceivableDate()));
					ps.setDate(index++, JdbcUtil.getDate(cd.getFrequencyDate()));
					ps.setDate(index++, JdbcUtil.getDate(cd.getDocumentReceivedDate()));
					ps.setObject(index++, cd.getDocumentId());
					ps.setBoolean(index++, cd.isOriginalDocument());
					ps.setInt(index++, cd.getVersion());
					ps.setLong(index++, cd.getLastMntBy());
					ps.setTimestamp(index++, cd.getLastMntOn());
					ps.setString(index++, cd.getRecordStatus());
					ps.setString(index++, cd.getRoleCode());
					ps.setString(index++, cd.getNextRoleCode());
					ps.setString(index++, cd.getTaskId());
					ps.setString(index++, cd.getNextTaskId());
					ps.setString(index++, cd.getRecordType());
					ps.setLong(index, cd.getWorkflowId());
				}

				@Override
				public int getBatchSize() {
					return documents.size();
				}
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void update(Covenant covenant, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Covenants");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Set Los = ?, MandatoryRole = ?, Otc = ?, Pdd = ?");
		sql.append(", ReceivableDate = ?, AllowWaiver = ?, MaxAllowedDays = ?");
		sql.append(", DocumentReceived = ?, DocumentReceivedDate = ?, AllowPostPonement = ?, ExtendedDate = ?");
		sql.append(", AllowedPaymentModes = ?, Frequency = ?, NextFrequencyDate = ?, GraceDays = ?");
		sql.append(", GraceDueDate = ?, AlertsRequired = ?, AlertType = ?, AlertToRoles = ?, AlertDays = ?");
		sql.append(", InternalUse = ?, Remarks = ?, Remarks1 = ?, AdditionalField1 = ?");
		sql.append(", AdditionalField2 = ?, AdditionalField3 = ?, AdditionalField4 = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkFlowId = ?");
		sql.append("  Where Id = ? And Module = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBoolean(index++, covenant.isLos());
			ps.setString(index++, covenant.getMandatoryRole());
			ps.setBoolean(index++, covenant.isOtc());
			ps.setBoolean(index++, covenant.isPdd());
			ps.setDate(index++, JdbcUtil.getDate(covenant.getReceivableDate()));
			ps.setBoolean(index++, covenant.isAllowWaiver());
			ps.setInt(index++, covenant.getMaxAllowedDays());
			ps.setBoolean(index++, covenant.isDocumentReceived());
			ps.setDate(index++, JdbcUtil.getDate(covenant.getDocumentReceivedDate()));
			ps.setBoolean(index++, covenant.isAllowPostPonement());
			ps.setDate(index++, JdbcUtil.getDate(covenant.getExtendedDate()));
			ps.setString(index++, covenant.getAllowedPaymentModes());
			ps.setString(index++, covenant.getFrequency());
			ps.setDate(index++, JdbcUtil.getDate(covenant.getNextFrequencyDate()));
			ps.setInt(index++, covenant.getGraceDays());
			ps.setDate(index++, JdbcUtil.getDate(covenant.getGraceDueDate()));
			ps.setBoolean(index++, covenant.isAlertsRequired());
			ps.setString(index++, covenant.getAlertType());
			ps.setString(index++, covenant.getAlertToRoles());
			ps.setInt(index++, covenant.getAlertDays());
			ps.setBoolean(index++, covenant.isInternalUse());
			ps.setString(index++, covenant.getRemarks());
			ps.setBytes(index++, covenant.getRemarks1());
			ps.setString(index++, covenant.getAdditionalField1());
			ps.setString(index++, covenant.getAdditionalField2());
			ps.setString(index++, covenant.getAdditionalField3());
			ps.setString(index++, covenant.getAdditionalField4());
			ps.setInt(index++, covenant.getVersion());
			ps.setLong(index++, covenant.getLastMntBy());
			ps.setTimestamp(index++, covenant.getLastMntOn());
			ps.setString(index++, covenant.getRecordStatus());
			ps.setString(index++, covenant.getRoleCode());
			ps.setString(index++, covenant.getNextRoleCode());
			ps.setString(index++, covenant.getTaskId());
			ps.setString(index++, covenant.getNextTaskId());
			ps.setString(index++, covenant.getRecordType());
			ps.setLong(index++, covenant.getWorkflowId());

			ps.setLong(index++, covenant.getId());
			ps.setString(index, covenant.getModule());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateDocuments(List<CovenantDocument> documents, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Covenant_Documents");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Set ReceivableDate = ?, FrequencyDate = ?, DocumentReceivedDate = ?");
		sql.append(", DocumentId = ?, OriginalDocument= ?, Version = ?, LastMntBy = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkFlowId = ?");
		sql.append(" Where Id = ? and CovenantType = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int[] recordCount = jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					CovenantDocument cd = documents.get(i);
					int index = 1;

					ps.setDate(index++, JdbcUtil.getDate(cd.getReceivableDate()));
					ps.setDate(index++, JdbcUtil.getDate(cd.getFrequencyDate()));
					ps.setDate(index++, JdbcUtil.getDate(cd.getDocumentReceivedDate()));
					ps.setObject(index++, cd.getDocumentId());
					ps.setBoolean(index++, cd.isOriginalDocument());
					ps.setInt(index++, cd.getVersion());
					ps.setLong(index++, cd.getLastMntBy());
					ps.setTimestamp(index++, cd.getLastMntOn());
					ps.setString(index++, cd.getRecordStatus());
					ps.setString(index++, cd.getRoleCode());
					ps.setString(index++, cd.getNextRoleCode());
					ps.setString(index++, cd.getTaskId());
					ps.setString(index++, cd.getNextTaskId());
					ps.setString(index++, cd.getRecordType());
					ps.setLong(index++, cd.getWorkflowId());

					ps.setLong(index++, cd.getId());
					ps.setString(index, cd.getCovenantType());
				}

				@Override
				public int getBatchSize() {
					return documents.size();
				}
			});

			if (recordCount.length <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void delete(Covenant covenant, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from Covenants");
		sql.append(tableType.getSuffix());
		sql.append(" Where KeyReference = ? and CovenantTypeId = ? and Module = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, covenant.getKeyReference());
				ps.setLong(index++, covenant.getCovenantTypeId());
				ps.setString(index, covenant.getModule());
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void deleteDocuments(List<CovenantDocument> documents, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from Covenant_Documents");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					CovenantDocument cd = documents.get(i);

					int index = 1;

					ps.setLong(index, cd.getId());
				}

				@Override
				public int getBatchSize() {
					return documents.size();
				}
			}).length;

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

	}

	@Override
	public boolean isExists(Covenant covenant, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select Count(KeyReference) From Covenants");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where KeyReference = ? and CovenantTypeId = ? and Module = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, covenant.getKeyReference(),
				covenant.getCovenantTypeId(), covenant.getModule()) > 0;
	}

	@Override
	public List<Covenant> getCovenantsAlertList() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Distinct c.Id, c.CovenantTypeId, ut.TemplateCode, cust.TemplateCode CustTemplateCode");
		sql.append(", c.AlertToRoles, ct.Code, ct.Description, c.KeyReference, fm.FinType, c.Frequency");
		sql.append(", c.NextFrequencyDate, c.AlertDays, ct.DocType, dt.DocTypeDesc, ca.AlertsentOn");
		sql.append(" From Covenants c");
		sql.append(" Inner Join Covenant_Types ct on ct.Id = c.covenantTypeId");
		sql.append(" Left Join BmtDocumentTypes dt on dt.doctypeCode = ct.docType");
		sql.append(" Inner Join Financemain fm on fm.FinReference = c.KeyReference and ClosingStatus is null");
		sql.append(" Left Join Covenant_Documents cd on cd.Covenantid = c.Id");
		sql.append(" Left Join (Select CovenantId, max(alertsentOn) AlertSentOn");
		sql.append(" From Covenant_Alerts group by CovenantId) ca on ca.covenantId = c.Id");
		sql.append(" Left Join Templates ut on ut.TemplateId = ct.UserTemplate");
		sql.append(" Left Join Templates cust on cust.TemplateId = ct.CustomerTemplate");
		sql.append(" Where NextFrequencyDate is not null and ");
		sql.append(" cd.FrequencyDate is null or cd.FrequencyDate != c.NextFrequencyDate");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, num) -> {
			Covenant c = new Covenant();

			c.setId(rs.getLong("Id"));
			c.setCovenantTypeId(rs.getLong("CovenantTypeId"));
			c.setUserTemplateCode(rs.getString("TemplateCode"));
			c.setCustomerTemplateCode(rs.getString("CustTemplateCode"));
			c.setAlertToRoles(rs.getString("AlertToRoles"));
			c.setCode(rs.getString("Code"));
			c.setDescription(rs.getString("Description"));
			c.setKeyReference(rs.getString("KeyReference"));
			// c.setFinType(rs.getString("FinType"));
			c.setFrequency(rs.getString("Frequency"));
			c.setNextFrequencyDate(rs.getTimestamp("NextFrequencyDate"));
			c.setAlertDays(rs.getInt("AlertDays"));
			c.setDocType(rs.getString("DocType"));
			c.setDocTypeName(rs.getString("DocTypeDesc"));
			c.setAlertsentOn(rs.getDate("AlertsentOn"));

			return c;
		});

	}

	@Override
	public List<Covenant> getCovenants(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" c.Frequency, NextFrequencyDate, CovenantTypeCode, CovenantTypeDescription, ct.DocType");
		sql.append(", DocTypeName, c.id, c.ReceivableDate, c.AllowPostponement ");
		sql.append(", c.ExtendedDate, c.GraceDays, c.GraceDueDate ");
		sql.append(" from covenants_aview c");
		sql.append(" inner join COVENANT_TYPES_AVIEW ct on ct.id = c.covenantTypeId");
		sql.append(" left join COVENANT_DOCUMENTS cd on cd.CovenantId = c.id");
		sql.append(" where KeyReference = ? and c.NextFrequencyDate is not null and pdd = ?");
		sql.append(" and cd.frequencydate is null");
		sql.append(" order by NextFrequencyDate");

		logger.debug(Literal.SQL + sql.toString());

		List<Covenant> list = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, finReference);
			ps.setInt(index, 1);
		}, (rs, rowNum) -> {
			Covenant c = new Covenant();

			c.setFrequency(rs.getString("Frequency"));
			c.setNextFrequencyDate(rs.getTimestamp("NextFrequencyDate"));
			c.setCovenantTypeCode(rs.getString("CovenantTypeCode"));
			c.setCovenantTypeDescription(rs.getString("CovenantTypeDescription"));
			c.setDocType(rs.getString("DocType"));
			c.setDocTypeName(rs.getString("DocTypeName"));
			c.setId(rs.getLong("Id"));
			c.setReceivableDate(rs.getTimestamp("ReceivableDate"));
			c.setAllowPostPonement(rs.getBoolean("AllowPostponement"));
			c.setExtendedDate(rs.getTimestamp("ExtendedDate"));
			c.setGraceDays(rs.getInt("GraceDays"));
			c.setGraceDueDate(rs.getTimestamp("GraceDueDate"));

			return c;
		});

		return list.stream().sorted((l1, l2) -> DateUtil.compare(l1.getNextFrequencyDate(), l2.getNextFrequencyDate()))
				.collect(Collectors.toList());
	}

	@Override
	public void deleteDocumentByDocumentId(Long documentId, String tableType) {
		StringBuilder sql = new StringBuilder("Delete from Covenant_Documents");
		sql.append(tableType);
		sql.append(" Where DocumentId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, documentId));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	private StringBuilder getSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, KeyReference, Module, CovenantTypeId, Los, MandatoryRole, Otc, Pdd, ReceivableDate");
		sql.append(", AllowWaiver, MaxAllowedDays, DocumentReceived, DocumentReceivedDate, AllowPostPonement");
		sql.append(", ExtendedDate, AllowedPaymentModes, Frequency, NextFrequencyDate, GraceDays, GraceDueDate");
		sql.append(", AlertsRequired, AlertType, AlertToRoles, AlertDays, InternalUse, Remarks");
		sql.append(", Remarks1, AdditionalField1, AdditionalField2, AdditionalField3, AdditionalField4");
		sql.append(", Version, LastMntby, LastMnton, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", Category, CovenantTypeDescription, CovenantType, CovenantTypeCode");
		}

		sql.append(" From Covenants");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class CovenantsRowMapper implements RowMapper<Covenant> {
		private String type;

		private CovenantsRowMapper(String type) {
			this.type = type;
		}

		@Override
		public Covenant mapRow(ResultSet rs, int rowNum) throws SQLException {
			Covenant c = new Covenant();

			c.setId(rs.getLong("Id"));
			c.setKeyReference(rs.getString("KeyReference"));
			c.setModule(rs.getString("Module"));
			c.setCovenantTypeId(rs.getLong("CovenantTypeId"));
			c.setLos(rs.getBoolean("Los"));
			c.setMandatoryRole(rs.getString("MandatoryRole"));
			c.setOtc(rs.getBoolean("Otc"));
			c.setPdd(rs.getBoolean("Pdd"));
			c.setReceivableDate(rs.getTimestamp("ReceivableDate"));
			c.setAllowWaiver(rs.getBoolean("AllowWaiver"));
			c.setMaxAllowedDays(rs.getInt("MaxAllowedDays"));
			c.setDocumentReceived(rs.getBoolean("DocumentReceived"));
			c.setDocumentReceivedDate(rs.getTimestamp("DocumentReceivedDate"));
			c.setAllowPostPonement(rs.getBoolean("AllowPostPonement"));
			c.setExtendedDate(rs.getTimestamp("ExtendedDate"));
			c.setAllowedPaymentModes(rs.getString("AllowedPaymentModes"));
			c.setFrequency(rs.getString("Frequency"));
			c.setNextFrequencyDate(rs.getTimestamp("NextFrequencyDate"));
			c.setGraceDays(rs.getInt("GraceDays"));
			c.setGraceDueDate(rs.getTimestamp("GraceDueDate"));
			c.setAlertsRequired(rs.getBoolean("AlertsRequired"));
			c.setAlertType(rs.getString("AlertType"));
			c.setAlertToRoles(rs.getString("AlertToRoles"));
			c.setAlertDays(rs.getInt("AlertDays"));
			c.setInternalUse(rs.getBoolean("InternalUse"));
			c.setRemarks(rs.getString("Remarks"));
			c.setRemarks1(rs.getBytes("Remarks1"));
			c.setAdditionalField1(rs.getString("AdditionalField1"));
			c.setAdditionalField2(rs.getString("AdditionalField2"));
			c.setAdditionalField3(rs.getString("AdditionalField3"));
			c.setAdditionalField4(rs.getString("AdditionalField4"));
			c.setVersion(rs.getInt("Version"));
			c.setLastMntBy(rs.getLong("LastMntBy"));
			c.setLastMntOn(rs.getTimestamp("LastMntOn"));
			c.setRecordStatus(rs.getString("RecordStatus"));
			c.setRoleCode(rs.getString("RoleCode"));
			c.setNextRoleCode(rs.getString("NextRoleCode"));
			c.setTaskId(rs.getString("TaskId"));
			c.setNextTaskId(rs.getString("NextTaskId"));
			c.setRecordType(rs.getString("RecordType"));
			c.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				c.setCategory(rs.getString("Category"));
				c.setCovenantTypeDescription(rs.getString("CovenantTypeDescription"));
				c.setCovenantType(rs.getString("CovenantType"));
				c.setCovenantTypeCode(rs.getString("CovenantTypeCode"));
			}

			return c;
		}

	}

	@Override
	public List<Covenant> getCovenants(List<String> list, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, LOS, MandatoryRole, OTC, PDD, Frequency, ReceivableDate from Covenants");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));

		if (CollectionUtils.isNotEmpty(list)) {
			sql.append(" Where Id in (");
			sql.append(list.stream().map(e -> "?").collect(Collectors.joining(",")));
			sql.append(")");
		}

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (String cov : list) {
				ps.setString(index++, cov);
			}
		}, (rs, rowNum) -> {
			Covenant cov = new Covenant();

			cov.setId(rs.getLong("Id"));
			cov.setLos(rs.getBoolean("LOS"));
			cov.setMandatoryRole(rs.getString("MandatoryRole"));
			cov.setOtc(rs.getBoolean("OTC"));
			cov.setPdd(rs.getBoolean("PDD"));
			cov.setFrequency(rs.getString("Frequency"));
			cov.setReceivableDate(rs.getDate("ReceivableDate"));

			return cov;
		});
	}

	@Override
	public List<CovenantDocument> getCovenantDocuments(List<Long> covList, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, CovenantId, CovenantType, ReceivableDate, FrequencyDate");
		sql.append(", DocumentReceivedDate, DocumentId, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" From Covenant_Documents");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));

		if (CollectionUtils.isNotEmpty(covList)) {
			sql.append(" Where CovenantId in (");
			sql.append(covList.stream().map(e -> "?").collect(Collectors.joining(",")));
			sql.append(")");
		}

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (Long covDoc : covList) {
				ps.setLong(index++, covDoc);
			}
		}, (rs, rowNum) -> {
			CovenantDocument cd = new CovenantDocument();

			cd.setId(rs.getLong("Id"));
			cd.setCovenantId(rs.getLong("CovenantId"));
			cd.setCovenantType(rs.getString("CovenantType"));
			cd.setReceivableDate(rs.getDate("ReceivableDate"));
			cd.setFrequencyDate(rs.getDate("FrequencyDate"));
			cd.setDocumentReceivedDate(rs.getDate("DocumentReceivedDate"));
			cd.setDocumentId(rs.getLong("DocumentId"));
			cd.setVersion(rs.getInt("Version"));
			cd.setLastMntBy(rs.getLong("LastMntBy"));
			cd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cd.setRecordStatus(rs.getString("RecordStatus"));
			cd.setRoleCode(rs.getString("RoleCode"));
			cd.setNextRoleCode(rs.getString("NextRoleCode"));
			cd.setTaskId(rs.getString("TaskId"));
			cd.setNextTaskId(rs.getString("NextTaskId"));
			cd.setRecordType(rs.getString("RecordType"));
			cd.setWorkflowId(rs.getLong("WorkFlowId"));

			return cd;
		});
	}

	@Override
	public void updateCovenantDocuments(List<CovenantDocument> cdList, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Covenant_Documents");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" set CovenantType = ?, ReceivableDate = ?, FrequencyDate = ?");
		sql.append(", DocumentReceivedDate = ?, DocumentId = ?");
		sql.append(" Where CovenantId = ?");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				CovenantDocument cd = cdList.get(i);
				int index = 1;

				ps.setString(index++, cd.getCovenantType());
				ps.setDate(index++, JdbcUtil.getDate(cd.getReceivableDate()));
				ps.setDate(index++, JdbcUtil.getDate(cd.getFrequencyDate()));
				ps.setDate(index++, JdbcUtil.getDate(cd.getDocumentReceivedDate()));
				ps.setLong(index++, cd.getDocumentId());

				ps.setLong(index, cd.getCovenantId());
			}

			@Override
			public int getBatchSize() {
				return cdList.size();
			}
		});
	}

	@Override
	public List<Covenant> getCovenantsId(List<Long> list, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, LOS, MandatoryRole, OTC, PDD, Frequency, ReceivableDate");
		sql.append(" From Covenants");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));

		if (CollectionUtils.isNotEmpty(list)) {
			sql.append(" Where Id in (");
			sql.append(list.stream().map(e -> "?").collect(Collectors.joining(",")));
			sql.append(")");
		}

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (Long cov : list) {
				ps.setLong(index++, cov);
			}
		}, (rs, rowNum) -> {
			Covenant cov = new Covenant();

			cov.setId(rs.getLong("Id"));
			cov.setLos(rs.getBoolean("LOS"));
			cov.setMandatoryRole(rs.getString("MandatoryRole"));
			cov.setOtc(rs.getBoolean("OTC"));
			cov.setPdd(rs.getBoolean("PDD"));
			cov.setFrequency(rs.getString("Frequency"));
			cov.setReceivableDate(rs.getDate("ReceivableDate"));

			return cov;
		});
	}

	@Override
	public boolean getCovenantsWithOtc(String finRef) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" coalesce(count(ca.Covenanttypeid), 0) From Covenants_aView ca");
		sql.append(" Inner Join Covenant_Types ct on ct.Id = ca.Covenanttypeid");
		sql.append(" Where KeyReference = ? and Otc = ?");
		sql.append(" and ca.Id not in (select CovenantId from Covenant_Documents Where");
		sql.append(" CovenantId = ca.Id and ct.docType = CovenantType) ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finRef, 1) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

}