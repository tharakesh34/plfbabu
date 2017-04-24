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
 * FileName    		:  PresentmentDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.financemanagement.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetailHeader;
import com.pennanttech.pff.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PresentmentDetail</code> with set of CRUD operations.
 */
public class PresentmentDetailDAOImpl extends BasisNextidDaoImpl<PresentmentDetail> implements PresentmentDetailDAO {
	private static Logger logger = Logger.getLogger(PresentmentDetailDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private DataSource dataSource;

	public PresentmentDetailDAOImpl() {
		super();
	}
	
	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource  The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	
	@Override
	public PresentmentDetail getPresentmentDetail(long detailID, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" detailID, presentmentID, finReference, schDate, schSeq, mandateID, ");
		sql.append(" schAmtDue, schPriDue, schPftDue, schFeeDue, schInsDue, schPenaltyDue, ");
		sql.append(" advanceAmt, excessID, adviseAmt, excludeReason, presentmentAmt, status, ");
		sql.append(" bounceID, ");
		if (type.contains("View")) {
			sql.append("bounceID, presentmentID,finReference,mandateID,excessID,excludeReason,status,");
			sql.append("bounceID,");
		}

		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PresentmentDetails");
		sql.append(type);
		sql.append(" Where detailID = :detailID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		PresentmentDetail presentmentDetail = new PresentmentDetail();
		presentmentDetail.setPresentmentID(detailID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(presentmentDetail);
		RowMapper<PresentmentDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PresentmentDetail.class);

		try {
			presentmentDetail = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			presentmentDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return presentmentDetail;
	}


	@Override
	public void update(PresentmentDetail presentmentDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update PresentmentDetails");
		sql.append(tableType.getSuffix());
		sql.append("  set presentmentID = :presentmentID, finReference = :finReference, schDate = :schDate, ");
		sql.append(" schSeq = :schSeq, mandateID = :mandateID, schAmtDue = :schAmtDue, ");
		sql.append(" schPriDue = :schPriDue, schPftDue = :schPftDue, schFeeDue = :schFeeDue, ");
		sql.append(" schInsDue = :schInsDue, schPenaltyDue = :schPenaltyDue, advanceAmt = :advanceAmt, ");
		sql.append(" excessID = :excessID, adviseAmt = :adviseAmt, excludeReason = :excludeReason, ");
		sql.append(" presentmentAmt = :presentmentAmt, status = :status, bounceID = :bounceID, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where detailID = :detailID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(presentmentDetail);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PresentmentDetail presentmentDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from PresentmentDetails");
		sql.append(tableType.getSuffix());
		sql.append(" where detailID = :detailID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(presentmentDetail);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}


	@Override
	public long getPresentmentDetailRef(String tableName) {
		return getNextidviewDAO().getNextId(tableName);
	}
	
	@Override
	public String save(PresentmentDetail presentmentDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (presentmentDetail.getExtractID() == Long.MIN_VALUE) {
			presentmentDetail.setExtractID(getNextidviewDAO().getNextId("SeqPresentmentDetails"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert into PresentmentDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (DetailID, ExtractID , PresentmentID, FinReference, SchDate, SchSeq, MandateID, ");
		sql.append(" SchAmtDue, SchPriDue, SchPftDue, SchFeeDue, SchInsDue, SchPenaltyDue, ");
		sql.append(" AdvanceAmt, ExcessID, AdviseAmt, ExcludeReason, PresentmentAmt, Status, ");
		sql.append(" BounceID, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :DetailID, :ExtractID, :PresentmentID, :FinReference, :SchDate, :SchSeq, :MandateID, ");
		sql.append(" :SchAmtDue, :SchPriDue, :SchPftDue, :SchFeeDue, :SchInsDue, :SchPenaltyDue, ");
		sql.append(" :AdvanceAmt, :ExcessID, :AdviseAmt, :ExcludeReason, :PresentmentAmt, :Status, ");
		sql.append(" :BounceID, :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(presentmentDetail);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void savePresentmentHeaderDetails(PresentmentDetailHeader detailHeader) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" Insert into PresentmentDetailHeader");
		sql.append("(ExtractId, ExtractReference, FromDate, ToDate, MandateType, LoanType, LastMntBy, LastMntOn) ");
		sql.append(" Values(");
		sql.append(" :ExtractId, :ExtractReference, :FromDate, :ToDate, :MandateType, :LoanType, :LastMntBy, :LastMntOn)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(detailHeader);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public ResultSet getPresentmentDetails(PresentmentDetailHeader detailHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			sql = new StringBuilder();
			sql.append(" SELECT T1.FINREFERENCE, T1.SCHDATE, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID, SCHDPFTPAID, ");
			sql.append(" FEESCHD, SCHDFEEPAID, INSSCHD, T2.MANDATEID, T1.DEFSCHDDATE, T2.MANDATEID, T4.MANDATETYPE  FROM FINSCHEDULEDETAILS T1 ");
			sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FINREFERENCE = T2.FINREFERENCE  ");
			sql.append(" INNER JOIN RMTFINANCETYPES T3 ON T2.FINTYPE = T3.FINTYPE ");
			sql.append(" INNER JOIN MANDATES T4 ON T4.MANDATEID = T2.MANDATEID ");
			sql.append(" WHERE (REPAYONSCHDATE = ?) AND ((SCHDATE >= ? AND SCHDATE <= ?) ");
			sql.append(" OR (DEFSCHDDATE >= ? AND DEFSCHDDATE <= ?)) ");

			if (StringUtils.trimToNull(detailHeader.getLoanType()) != null) {
				sql.append(" AND (FINTYPE = ?)");
			}
			if (StringUtils.trimToNull(detailHeader.getMandateType()) != null) {
				sql.append(" AND (MANDATETYPE = ?) ");
			}
			logger.trace(Literal.SQL + sql.toString());

			Connection conn = DataSourceUtils.doGetConnection(this.dataSource);
			stmt = conn.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, "1");
			stmt.setDate(2, new java.sql.Date(detailHeader.getFromDate().getTime()));
			stmt.setDate(3, new java.sql.Date(detailHeader.getToDate().getTime()));
			stmt.setDate(4, new java.sql.Date(detailHeader.getFromDate().getTime()));
			stmt.setDate(5, new java.sql.Date(detailHeader.getToDate().getTime()));

			if (StringUtils.trimToNull(detailHeader.getLoanType()) != null) {
				stmt.setString(6, detailHeader.getLoanType());
			}
			if (StringUtils.trimToNull(detailHeader.getMandateType()) != null) {
				stmt.setString(7, detailHeader.getMandateType());
			}
			rs = stmt.executeQuery();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			sql = null;
		}
		logger.debug(Literal.LEAVING);
		return rs;
	}
	
}
