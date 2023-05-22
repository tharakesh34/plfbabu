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
 *
 * FileName : AuditHeaderDAOImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.audit.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.CoalesceSqlParameterSource;

public class AuditHeaderDAOImpl extends SequenceDao<AuditHeader> implements AuditHeaderDAO {
	private static Logger logger = LogManager.getLogger(AuditHeaderDAOImpl.class);

	public AuditHeaderDAOImpl() {
		super();
	}

	public AuditHeader getNewAuditHeader() {
		return new AuditHeader();
	}

	public long addAudit(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader.setAuditDate(new Timestamp(System.currentTimeMillis()));

		long id = addAuditHeader(auditHeader);
		auditHeader.setId(id);

		if (auditHeader.getAuditDetail() != null) {
			createAuditDetails(auditHeader, auditHeader.getAuditDetail());
		}

		if (auditHeader.getAuditDetails() != null && auditHeader.getAuditDetails().size() > 0) {
			createAuditDetails(auditHeader);
		}

		logger.debug(Literal.LEAVING);
		return id;
	}

	private long addAuditHeader(AuditHeader auditHeader) {
		logger.debug("addAuditHeader");
		auditHeader.setId(getNextValue("SeqAuditHeader"));
		StringBuilder insertSql = new StringBuilder();

		insertSql.append(
				"insert into AuditHeader (AuditId,AuditDate,AuditUsrId,AuditModule,AuditBranchCode,AuditDeptCode,AuditTranType,");
		insertSql.append("AuditCustNo,AuditAccNo,AuditLoanNo,AuditReference,AuditSystemIP,AuditSessionID,");
		insertSql.append("AuditInfo,Overide,AuditOveride,");
		insertSql.append("AuditPrinted,AuditRecovered,AuditErrorForRecocvery) ");
		insertSql.append(
				"values(:AuditId,:AuditDate,:AuditUsrId,:AuditModule,:AuditBranchCode,:AuditDeptCode,:AuditTranType,");
		insertSql.append(":AuditCustNo,:AuditAccNo,:AuditLoanNo,:AuditReference,:AuditSystemIP,:AuditSessionID,");
		insertSql.append(":AuditInfo, :Overide,:AuditOveride,");
		insertSql.append(":AuditPrinted,:AuditRecovered,:AuditErrorForRecocvery)");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(auditHeader);
		jdbcTemplate.update(insertSql.toString(), beanParameters);
		return auditHeader.getId();
	}

	private void addAuditDetails(Object modelData, String sql, boolean isExtendedModule, long auditId,
			Timestamp auditDate, int auditSeq, String auditImage) {
		logger.debug(Literal.ENTERING);
		logger.trace(Literal.SQL + sql);

		if (isExtendedModule) {
			ExtendedFieldRender fieldRender = (ExtendedFieldRender) modelData;
			MapSqlParameterSource parameterSource = new MapSqlParameterSource(fieldRender.getAuditMapValues());
			parameterSource.addValue("AUDIT_HEADER_AUDIT_ID", auditId);
			parameterSource.addValue("AUDIT_HEADER_AUDIT_DATE", auditDate);
			parameterSource.addValue("AUDIT_HEADER_AUDIT_SEQ", auditSeq);
			parameterSource.addValue("AUDIT_HEADER_AUDIT_IMAGE", auditImage);

			jdbcTemplate.update(sql, parameterSource);
		} else {
			CoalesceSqlParameterSource parameterSource = new CoalesceSqlParameterSource(modelData);
			parameterSource.addValue("AUDIT_HEADER_AUDIT_ID", auditId);
			parameterSource.addValue("AUDIT_HEADER_AUDIT_DATE", auditDate);
			parameterSource.addValue("AUDIT_HEADER_AUDIT_SEQ", auditSeq);
			parameterSource.addValue("AUDIT_HEADER_AUDIT_IMAGE", auditImage);

			jdbcTemplate.update(sql, parameterSource);
		}

		logger.debug(Literal.LEAVING);
	}

	public void createAuditDetails(AuditHeader auditHeader) {
		List<AuditDetail> auditDetails = auditHeader.getAuditDetails();

		if (auditDetails != null && !auditDetails.isEmpty()) {
			for (int i = 0; i < auditDetails.size(); i++) {
				createAuditDetails(auditHeader, auditDetails.get(i));
			}
		}
	}

	private void createAuditDetails(AuditHeader auditHeader, AuditDetail auditDetail) {
		logger.debug(Literal.ENTERING);
		boolean after = false;
		boolean before = false;
		boolean workFlow = false;

		if (auditDetail != null && auditDetail.getModelData() != null) {
			if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_ADD)) {
				after = true;
			} else if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
					.equalsIgnoreCase(PennantConstants.TRAN_UPD)) {
				after = true;
				before = true;
			} else if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
					.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
				before = true;
			} else if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
					.equalsIgnoreCase(PennantConstants.TRAN_WF)) {
				workFlow = true;
			}

			if (before && auditDetail.getBefImage() != null) {
				addAuditDetails(auditDetail.getBefImage(), getInsertQry(auditDetail.getBefImage(), auditDetail),
						auditDetail.isExtended(), auditHeader.getId(), auditHeader.getAuditDate(),
						auditDetail.getAuditSeq(), PennantConstants.TRAN_BEF_IMG);
			}

			if (after) {
				addAuditDetails(auditDetail.getModelData(), getInsertQry(auditDetail.getModelData(), auditDetail),
						auditDetail.isExtended(), auditHeader.getId(), auditHeader.getAuditDate(),
						auditDetail.getAuditSeq(), PennantConstants.TRAN_AFT_IMG);
			}

			if (workFlow) {
				addAuditDetails(auditDetail.getModelData(), getInsertQry(auditDetail.getModelData(), auditDetail),
						auditDetail.isExtended(), auditHeader.getId(), auditHeader.getAuditDate(),
						auditDetail.getAuditSeq(), PennantConstants.TRAN_WF_IMG);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private String getInsertQry(Object dataObject, AuditDetail auditDetail) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into Adt");
		if (auditDetail.isExtended()) {
			sql.append(((ExtendedFieldRender) dataObject).getTableName());
		} else {
			sql.append(ModuleUtil.getTableName(dataObject.getClass().getSimpleName()));
		}
		sql.append(" (");
		sql.append("AuditId, AuditDate, AuditSeq, AuditImage, ");
		sql.append(auditDetail.getAuditField());
		sql.append(") VALUES (");
		sql.append(":AUDIT_HEADER_AUDIT_ID, :AUDIT_HEADER_AUDIT_DATE, ");
		sql.append(":AUDIT_HEADER_AUDIT_SEQ, :AUDIT_HEADER_AUDIT_IMAGE, ");
		sql.append(auditDetail.getAuditValue());
		sql.append(")");
		return sql.toString();
	}

	@Override
	public boolean checkUserAccess(Long userId, String tableName, String whereCondition, Object[] arguments) {
		StringBuilder sql = new StringBuilder();
		sql.append("select RecordStatus, LastMntBy from");
		sql.append(" Adt").append(tableName);
		sql.append("  ");
		sql.append(whereCondition);
		sql.append(" and Version = ? order by AuditId desc");

		return jdbcOperations.query(sql.toString(), new ResultSetExtractor<Boolean>() {
			@Override
			public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					if (PennantConstants.RCD_STATUS_SAVED.equals(rs.getString(1))) {
						return true;
					}
					if (userId == rs.getLong(2)) {
						return false;
					}
				}
				return true;
			}

		}, arguments);
	}
}
