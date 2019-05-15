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
 * Creation Date    :  01-05-2017    														*
 *                                                                  						*
 * Modified Date    :  01-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-05-2017       PENNANT	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PresentmentHeader</code> with set
 * of CRUD operations.
 */
public class PresentmentDetailDAOImpl extends SequenceDao<PresentmentHeader> implements PresentmentDetailDAO {
	private static Logger logger = Logger.getLogger(PresentmentDetailDAOImpl.class);

	private DataSource dataSource;

	public PresentmentDetailDAOImpl() {
		super();
	}

	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.dataSource = dataSource;
	}

	@Override
	public PresentmentHeader getPresentmentHeader(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT  id, reference, presentmentDate, partnerBankId, fromDate, toDate, presentmentType, ");
		sql.append(" status, mandateType, loanType, finBranch, schdate, dBStatusId,EntityCode, ");
		sql.append(" importStatusId, totalRecords, processedRecords, successRecords, failedRecords, ");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(" ,PartnerBankCode,PartnerBankName,PartnerAcctNumber,PartnerAcctType");
		}
		sql.append(" From PresentmentHeader");
		sql.append(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		PresentmentHeader presentmentHeader = new PresentmentHeader();
		presentmentHeader.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(presentmentHeader);
		RowMapper<PresentmentHeader> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PresentmentHeader.class);

		try {
			presentmentHeader = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			presentmentHeader = null;
		}
		logger.debug(Literal.LEAVING);
		return presentmentHeader;
	}

	@Override
	public String save(PresentmentHeader presentmentHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" Insert into PresentmentHeader");
		sql.append(tableType.getSuffix());
		sql.append("(id, reference, presentmentDate, partnerBankId, fromDate, toDate, ");
		sql.append(" status, mandateType, loanType, finBranch, schdate, presentmentType,");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :id, :reference, :presentmentDate, :partnerBankId, :fromDate, :toDate, ");
		sql.append(" :status, :mandateType, :loanType, :finBranch, :schdate, :presentmentType,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(presentmentHeader);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(presentmentHeader.getId());
	}

	@Override
	public void update(PresentmentHeader presentmentHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update PresentmentHeader");
		sql.append(tableType.getSuffix());
		sql.append(
				"  set reference = :reference, presentmentDate = :presentmentDate, partnerBankId = :partnerBankId, ");
		sql.append(" fromDate = :fromDate, toDate = :toDate, status = :status, ");
		sql.append(" mandateType = :mandateType, loanType = :loanType, finBranch = :finBranch, ");
		sql.append(" schdate = :schdate, presentmentType = :presentmentType, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(presentmentHeader);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PresentmentHeader presentmentHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from PresentmentHeader");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(presentmentHeader);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
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
	public long getSeqNumber(String tableName) {
		return getNextId(tableName);
	}

	@Override
	public long save(PresentmentDetail detail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (detail.getId() == Long.MIN_VALUE) {
			detail.setId(getNextId("SEQPRESENTMENTDETAILS"));
		}
		if (detail.getPresentmentRef() != null) {
			String reference = detail.getPresentmentRef();
			String presentmentRef = StringUtils.leftPad(String.valueOf(detail.getId()), 29 - reference.length(), "0");
			detail.setPresentmentRef(reference.concat(presentmentRef));
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert into PresentmentDetails");
		sql.append(tableType.getSuffix());
		sql.append(
				" (Id, PresentmentId, PresentmentRef, FinReference, SchDate, MandateId, SchAmtDue, schPriDue, schPftDue, schFeeDue, schInsDue,");
		sql.append(
				" schPenaltyDue, advanceAmt, excessID, adviseAmt, presentmentAmt, ExcludeReason, bounceID, emiNo, tDSAmount, status, receiptID,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(
				" :Id, :PresentmentId, :PresentmentRef, :FinReference, :SchDate, :MandateId, :SchAmtDue, :schPriDue, :schPftDue, :schFeeDue, :schInsDue,");
		sql.append(
				" :schPenaltyDue, :advanceAmt, :excessID, :adviseAmt, :presentmentAmt, :ExcludeReason, :bounceID, :emiNo, :tDSAmount, :status, :receiptID,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(detail);
		try {
			this.jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return detail.getId();
	}

	@Override
	public List<Object> getPresentmentDetails(PresentmentHeader detailHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		int index = 0;
		List<Object> list = new ArrayList<Object>();
		try {
			sql = new StringBuilder();
			sql.append(
					" SELECT T1.FINREFERENCE, T1.SCHDATE, T1.SCHSEQ, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID, SCHDPFTPAID, DEFSCHDDATE,");
			sql.append(" FEESCHD, SCHDFEEPAID, INSSCHD, T2.MANDATEID, T1.DEFSCHDDATE, T4.MANDATETYPE, T4.STATUS,");
			sql.append(" T4.EXPIRYDATE, T2.FINTYPE LOANTYPE, T5.BRANCHCODE, T1.TDSAMOUNT, T6.BANKCODE, T7.ENTITYCODE,");
			sql.append(" T1.INSTNUMBER EMINO, T2.FINBRANCH");
			sql.append(", T2.GRCADVTYPE, T2.ADVTYPE, T2.GRCPERIODENDDATE");
			sql.append(" FROM FINSCHEDULEDETAILS T1");
			sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FINREFERENCE = T2.FINREFERENCE");
			sql.append(" INNER JOIN RMTFINANCETYPES T3 ON T2.FINTYPE = T3.FINTYPE");
			sql.append(" INNER JOIN MANDATES T4 ON T4.MANDATEID = T2.MANDATEID");
			sql.append(" INNER JOIN RMTBRANCHES T5 ON T5.BRANCHCODE = T2.FINBRANCH");
			sql.append(" INNER JOIN BANKBRANCHES T6 ON T4.BANKBRANCHID = T6.BANKBRANCHID");
			sql.append(" INNER JOIN SMTDIVISIONDETAIL T7 ON T7.DIVISIONCODE=T3.FINDIVISION");
			sql.append(
					" WHERE (T2.FINISACTIVE = ?) AND ((T1.PROFITSCHD + T1.PRINCIPALSCHD + T1.FEESCHD - T1.SCHDPFTPAID - T1.SCHDPRIPAID - T1.SCHDFEEPAID) > ?)");
			sql.append(" AND ((SCHDATE >= ? AND SCHDATE <= ?)");
			sql.append(" OR (DEFSCHDDATE >= ? AND DEFSCHDDATE <= ?)) ");

			if (StringUtils.trimToNull(detailHeader.getMandateType()) != null) {
				sql.append(" AND (T4.MANDATETYPE = ?) ");
			}

			if (StringUtils.trimToNull(detailHeader.getLoanType()) != null) {
				sql.append(" AND (T2.FINTYPE IN ( ");
				String[] loanTypes = detailHeader.getLoanType().split(",");
				for (int i = 0; i < loanTypes.length; i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append("?");
				}
				sql.append("))");
			}

			if (StringUtils.trimToNull(detailHeader.getFinBranch()) != null) {
				sql.append(" AND (T2.FINBRANCH IN ( ");
				String[] finBranches = detailHeader.getFinBranch().split(",");
				for (int i = 0; i < finBranches.length; i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append("?");
				}
				sql.append("))");
			}

			if (StringUtils.trimToNull(detailHeader.getEntityCode()) != null) {
				sql.append(" AND (T7.ENTITYCODE = ?) ");
			}

			sql.append(
					" AND  Not Exists( Select 1 from PresentmentDetails T6 where T1.FinReference = T6.FinReference AND T6.SCHDATE = T1.SCHDATE ");
			sql.append(" AND T6.ExcludeReason = '0')  ORDER BY T1.DEFSCHDDATE, T6.BANKCODE ,T7.EntityCode ");
			// sql.append(" AND T6.ExcludeReason = '0' AND T6.ExcludeReason <>
			// '6' AND T6.STATUS <> 'A') ORDER BY T1.DEFSCHDDATE ");

			Connection conn = DataSourceUtils.doGetConnection(this.dataSource);
			stmt = conn.prepareStatement(sql.toString());
			stmt.setInt(1, 1);
			stmt.setBigDecimal(2, BigDecimal.ZERO);
			stmt.setDate(3, DateUtil.getSqlDate(detailHeader.getFromDate()));
			stmt.setDate(4, DateUtil.getSqlDate(detailHeader.getToDate()));
			stmt.setDate(5, DateUtil.getSqlDate(detailHeader.getFromDate()));
			stmt.setDate(6, DateUtil.getSqlDate(detailHeader.getToDate()));
			index = 6;
			if (StringUtils.trimToNull(detailHeader.getMandateType()) != null) {
				index = index + 1;
				stmt.setString(index, detailHeader.getMandateType());
			}

			if (StringUtils.trimToNull(detailHeader.getLoanType()) != null) {
				String[] loanTypes = detailHeader.getLoanType().split(",");
				int i = 0;
				for (i = 1; i <= loanTypes.length; i++) {
					stmt.setString(i + index, loanTypes[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(detailHeader.getFinBranch()) != null) {
				String[] finBranches = detailHeader.getFinBranch().split(",");
				int i = 0;
				for (i = 1; i <= finBranches.length; i++) {
					stmt.setString(i + index, finBranches[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(detailHeader.getEntityCode()) != null) {
				index = index + 1;
				stmt.setString(index, detailHeader.getEntityCode());
			}

			logger.trace(Literal.SQL + sql.toString());

			rs = stmt.executeQuery();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			sql = null;
		}
		logger.debug(Literal.LEAVING);
		list.add(rs);
		list.add(stmt);

		return list;
	}

	@Override
	public List<Object> getPDCPresentmentDetails(PresentmentHeader presentmentHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		int index = 0;
		List<Object> list = new ArrayList<Object>();
		try {
			sql = new StringBuilder();
			sql.append(
					" SELECT T1.FINREFERENCE, T1.SCHDATE, T1.SCHSEQ, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID, SCHDPFTPAID,");
			sql.append(
					" DEFSCHDDATE, FEESCHD, SCHDFEEPAID, INSSCHD, T2.MANDATEID, T1.DEFSCHDDATE, T2.FINREPAYMETHOD MANDATETYPE, T8.CHEQUESTATUS STATUS,");
			sql.append(
					" T8.CHEQUEDATE, T8.CHEQUEDETAILSID, T2.FINTYPE LOANTYPE, T5.BRANCHCODE, T1.TDSAMOUNT, T6.BANKCODE, T7.ENTITYCODE,");
			sql.append(" T1.INSTNUMBER EMINO, T2.FINBRANCH");
			sql.append(", T2.GRCADVTYPE, T2.ADVTYPE, T2.GRCPERIODENDDATE");
			sql.append(" FROM FINSCHEDULEDETAILS T1");
			sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FINREFERENCE = T2.FINREFERENCE");
			sql.append(" INNER JOIN RMTFINANCETYPES T3 ON T2.FINTYPE = T3.FINTYPE");
			sql.append(" INNER JOIN CHEQUEHEADER T4 ON T4.FINREFERENCE = T1.FINREFERENCE");
			sql.append(" INNER JOIN CHEQUEDETAIL T8 ON T8.HEADERID = T4.HEADERID AND T8.EMIREFNO = T1.INSTNUMBER");
			sql.append(" INNER JOIN RMTBRANCHES T5 ON T5.BRANCHCODE = T2.FINBRANCH");
			sql.append(" INNER JOIN BANKBRANCHES T6 ON T8.BANKBRANCHID = T6.BANKBRANCHID");
			sql.append(" INNER JOIN SMTDIVISIONDETAIL T7 ON T7.DIVISIONCODE=T3.FINDIVISION");
			sql.append(
					" WHERE (T2.FINISACTIVE = ?) AND ((T1.PROFITSCHD + T1.PRINCIPALSCHD + T1.FEESCHD - T1.SCHDPFTPAID - T1.SCHDPRIPAID - T1.SCHDFEEPAID) > ?)");
			sql.append(" AND ((SCHDATE >= ? AND SCHDATE <= ?)");
			sql.append(" OR (DEFSCHDDATE >= ? AND DEFSCHDDATE <= ?)) ");

			if (StringUtils.trimToNull(presentmentHeader.getMandateType()) != null) {
				sql.append(" AND (T2.FINREPAYMETHOD = ?) ");
			}

			if (StringUtils.trimToNull(presentmentHeader.getLoanType()) != null) {
				sql.append(" AND (T2.FINTYPE IN ( ");
				String[] loanTypes = presentmentHeader.getLoanType().split(",");
				for (int i = 0; i < loanTypes.length; i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append("?");
				}
				sql.append("))");
			}

			if (StringUtils.trimToNull(presentmentHeader.getFinBranch()) != null) {
				sql.append(" AND (T2.FINBRANCH IN ( ");
				String[] finBranches = presentmentHeader.getFinBranch().split(",");
				for (int i = 0; i < finBranches.length; i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append("?");
				}
				sql.append("))");
			}

			if (StringUtils.trimToNull(presentmentHeader.getEntityCode()) != null) {
				sql.append(" AND (T7.ENTITYCODE = ?) ");
			}

			sql.append(
					" AND  Not Exists( Select 1 from PresentmentDetails T6 where T1.FinReference = T6.FinReference AND T6.SCHDATE = T1.SCHDATE ");
			sql.append(" AND T6.ExcludeReason = '0')  ORDER BY T1.DEFSCHDDATE, T6.BANKCODE ,T7.EntityCode ");
			Connection conn = DataSourceUtils.doGetConnection(this.dataSource);
			stmt = conn.prepareStatement(sql.toString());
			stmt.setInt(1, 1);
			stmt.setBigDecimal(2, BigDecimal.ZERO);
			stmt.setDate(3, DateUtil.getSqlDate(presentmentHeader.getFromDate()));
			stmt.setDate(4, DateUtil.getSqlDate(presentmentHeader.getToDate()));
			stmt.setDate(5, DateUtil.getSqlDate(presentmentHeader.getFromDate()));
			stmt.setDate(6, DateUtil.getSqlDate(presentmentHeader.getToDate()));
			index = 6;
			if (StringUtils.trimToNull(presentmentHeader.getMandateType()) != null) {
				index = index + 1;
				stmt.setString(index, presentmentHeader.getMandateType());
			}

			if (StringUtils.trimToNull(presentmentHeader.getLoanType()) != null) {
				String[] loanTypes = presentmentHeader.getLoanType().split(",");
				int i = 0;
				for (i = 1; i <= loanTypes.length; i++) {
					stmt.setString(i + index, loanTypes[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(presentmentHeader.getFinBranch()) != null) {
				String[] finBranches = presentmentHeader.getFinBranch().split(",");
				int i = 0;
				for (i = 1; i <= finBranches.length; i++) {
					stmt.setString(i + index, finBranches[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(presentmentHeader.getEntityCode()) != null) {
				index = index + 1;
				stmt.setString(index, presentmentHeader.getEntityCode());
			}

			logger.trace(Literal.SQL + sql.toString());

			rs = stmt.executeQuery();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			sql = null;
		}
		logger.debug(Literal.LEAVING);
		list.add(rs);
		list.add(stmt);

		return list;
	}

	@Override
	public long savePresentmentHeader(PresentmentHeader presentmentHeader) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" Insert into PresentmentHeader");
		sql.append(" (Id, Reference, PresentmentDate, PartnerBankId, FromDate, ToDate, PresentmentType,");
		sql.append(
				"  Status, MandateType, FinBranch, Schdate, LoanType, ImportStatusId, TotalRecords, ProcessedRecords, SuccessRecords, FailedRecords,");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, dBStatusId, bankCode,EntityCode)");
		sql.append(" values(");
		sql.append(" :Id, :Reference, :PresentmentDate, :PartnerBankId, :FromDate, :ToDate, :PresentmentType,");
		sql.append(
				" :Status, :MandateType, :FinBranch, :Schdate, :LoanType, :ImportStatusId, :TotalRecords, :ProcessedRecords, :SuccessRecords, :FailedRecords,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :dBStatusId, :bankCode, :EntityCode)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(presentmentHeader);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return presentmentHeader.getId();
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, boolean isApprove,
			String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(
				" SELECT Id, PresentmentId, FinReference, PresentmentRef, SchDate, MandateId, SchAmtDue, schPriDue, schPftDue, schFeeDue,");
		sql.append(
				" schInsDue, schPenaltyDue, advanceAmt, excessID, adviseAmt, presentmentAmt, tDSAmount, excludeReason, bounceID, emiNo, status,");
		sql.append(" ErrorCode, ErrorDesc, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		if (type.contains("View")) {
			sql.append(" ,mandateType ,finTypeDesc, finType, customerName ");
		}
		sql.append(" From PresentmentDetails");
		sql.append(type);
		sql.append(" Where PresentmentId = :PresentmentId ");
		if (isExclude) {
			if (isApprove) {
				sql.append("AND ExcludeReason = :ExcludeReason AND (Status = :IMPORTSTATUS OR Status = :FAILEDSTATUS)");
			} else {
				sql.append("AND ExcludeReason = :ExcludeReason");
			}
		} else {
			sql.append("AND ExcludeReason <> :ExcludeReason ");
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("PresentmentId", presentmentId);
		source.addValue("ExcludeReason", 0);
		source.addValue("IMPORTSTATUS", RepayConstants.PEXC_IMPORT);
		source.addValue("FAILEDSTATUS", RepayConstants.PEXC_FAILURE);
		try {
			RowMapper<PresentmentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(PresentmentDetail.class);
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}
	}

	@Override
	public void updatePresentmentDetials(long presentmentId, List<Long> list, int mnualExclude) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(
				" UPDATE PRESENTMENTDETAILS Set EXCLUDEREASON = :EXCLUDEREASON Where PRESENTMENTID = :PRESENTMENTID AND  ID in (:ID)");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("EXCLUDEREASON", mnualExclude);
		source.addValue("PRESENTMENTID", presentmentId);
		source.addValue("ID", list);

		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updatePresentmentHeader(long presentmentId, int manualExclude, long partnerBankId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" UPDATE PRESENTMENTHEADER Set STATUS = :STATUS, PARTNERBANKID = :PARTNERBANKID Where ID = :ID");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("STATUS", manualExclude);
		source.addValue("PARTNERBANKID", partnerBankId);
		source.addValue("ID", presentmentId);

		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateFinScheduleDetails(long id, String finReference, Date schDate, int schSeq) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" UPDATE FINSCHEDULEDETAILS Set PresentmentId = :id Where FinReference = :FinReference AND ");
		sql.append(" SchDate = :SchDate AND SchSeq = :SchSeq");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("FinReference", finReference);
		source.addValue("SchDate", schDate);
		source.addValue("SchSeq", schSeq);
		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updatePresentmentIdAsZero(long presentmentId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" UPDATE FINSCHEDULEDETAILS Set PresentmentId = :SetId Where PresentmentId = :PresentmentId ");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("PresentmentId", presentmentId);
		source.addValue("SetId", 0);
		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deletePresentmentDetails(long presentmentId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		// Prepare the SQL.
		sql = new StringBuilder();
		sql.append(" delete from PRESENTMENTDETAILS");
		sql.append(" where PresentmentId = :PresentmentId");

		source = new MapSqlParameterSource();
		source.addValue("PresentmentId", presentmentId);

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.update(sql.toString(), source);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		} finally {
			source = null;
			sql = null;
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deletePresentmentHeader(long id) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		// Prepare the SQL.
		sql = new StringBuilder();
		sql.append(" Delete from PRESENTMENTHEADER");
		sql.append(" where ID = :ID");

		source = new MapSqlParameterSource();
		source.addValue("ID", id);

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.update(sql.toString(), source);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		} finally {
			source = null;
			sql = null;
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public PresentmentDetail getPresentmentDetail(String presentmentRef, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append(
				" Select id, presentmentId, finReference, schDate, mandateId, schAmtDue, schPriDue, schPftDue, schFeeDue, schInsDue, ");
		sql.append(
				" schPenaltyDue, advanceAmt, excessID, adviseAmt, presentmentAmt, Emino, status, presentmentRef, ecsReturn, receiptID,");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(" ,mandateType");
		}
		sql.append(" From PRESENTMENTDETAILS");
		sql.append(type);
		sql.append("  WHERE PresentmentRef = :PresentmentRef");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PresentmentRef", presentmentRef);
		RowMapper<PresentmentDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PresentmentDetail.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetail(long presentmentId, boolean includeData) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append(
				" Select T.id, T.presentmentId, T.finReference, T.schDate, T.mandateId, T.schAmtDue, T.schPriDue, T.schPftDue, T.schFeeDue, T.schInsDue, ");
		sql.append(
				" T.schPenaltyDue, T.advanceAmt, T.excessID, T.adviseAmt, T.presentmentAmt, T.Emino, T.status, T.presentmentRef, T.ecsReturn, T.receiptID, T.excludeReason,");
		sql.append(
				" T.Version, T.LastMntOn, T.LastMntBy,T.RecordStatus, T.RoleCode, T.NextRoleCode, T.TaskId, T.NextTaskId, T.RecordType, T.WorkflowId ");
		if (includeData) {
			sql.append(",PB.Accountno,PB.actype ");
		}
		sql.append(" From PRESENTMENTDETAILS T INNER JOIN PRESENTMENTHEADER PH ON PH.ID=T.PRESENTMENTID ");

		if (includeData) {
			sql.append(" INNER JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = PH.PARTNERBANKID ");
		}
		sql.append(" WHERE T.PresentmentId = :PresentmentId");
		if (includeData) {
			sql.append(
					" AND (T.ExcludeReason = :PEXC_EMIINCLUDE or T.ExcludeReason = :PEXC_EMIINADVANCE ) AND T.Status <> :Status ");
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("PresentmentId", presentmentId);
		if (includeData) {
			source.addValue("PEXC_EMIINCLUDE", RepayConstants.PEXC_EMIINCLUDE);
			source.addValue("PEXC_EMIINADVANCE", RepayConstants.PEXC_EMIINADVANCE);
			source.addValue("Status", RepayConstants.PEXC_APPROV);
		}

		RowMapper<PresentmentDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PresentmentDetail.class);
		try {
			return this.jdbcTemplate.query(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<PresentmentDetail> getPresentmenToPost(long custId, Date schData) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append(
				" SELECT FM.CUSTID,FM.FINBRANCH, FM.FINTYPE,PD.ID, PD.PRESENTMENTID, PD.FINREFERENCE, PD.SCHDATE, PD.MANDATEID, ");
		sql.append(" PD.ADVANCEAMT, PD.EXCESSID, PD.PRESENTMENTAMT, PD.EXCLUDEREASON, PD.BOUNCEID , ");
		sql.append(" PB.ACCOUNTNO, PB.ACTYPE ");
		sql.append(" FROM PRESENTMENTDETAILS PD ");
		sql.append(" INNER JOIN PRESENTMENTHEADER PH ON PH.ID = PD.PRESENTMENTID ");
		sql.append(" INNER JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = PH.PARTNERBANKID ");
		sql.append(" INNER JOIN FINANCEMAIN FM ON PD.FINREFERENCE = FM.FINREFERENCE  ");
		sql.append(" WHERE FM.CUSTID =:CustId AND PD.SCHDATE = :SchDate  ");
		sql.append(" AND PD.STATUS = :STATUS  ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", custId);
		source.addValue("SchDate", schData);
		source.addValue("STATUS", RepayConstants.PEXC_APPROV);

		RowMapper<PresentmentDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PresentmentDetail.class);
		try {
			return jdbcTemplate.query(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void updateReceptId(long id, long receiptID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" Update PRESENTMENTDETAILS set RECEIPTID = :RECEIPTID Where ID = :ID ");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("ID", id);
		source.addValue("RECEIPTID", receiptID);
		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);
	}

	// Update the presentment status and bounceid
	@Override
	public void updatePresentmentDetails(String presentmentRef, String status, long bounceId, long manualAdviseId,
			String errorDesc) {
		logger.debug(Literal.ENTERING);

		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append("Update Presentmentdetails set Status = :Status, BounceID = :BounceID, ErrorDesc = :ErrorDesc, ");
		sql.append("ManualAdviseId = :ManualAdviseId  Where PresentmentRef = :PresentmentRef");

		source.addValue("Status", status);
		source.addValue("PresentmentRef", presentmentRef);
		source.addValue("BounceID", bounceId);
		source.addValue("ManualAdviseId", manualAdviseId);
		source.addValue("ErrorDesc", errorDesc);
		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception {}", e);
			throw e;
		} finally {
			source = null;
			sql = null;
		}

		logger.debug(Literal.LEAVING);
	}

	// Update the presentment status
	@Override
	public void updatePresentmentDetails(String presentmentRef, String status, String errorCode, String errorDesc) {
		logger.debug(Literal.ENTERING);

		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append(
				"Update Presentmentdetails set Status = :Status, ErrorCode = :ErrorCode, ErrorDesc = :ErrorDesc Where PresentmentRef = :PresentmentRef ");

		source.addValue("Status", status);
		source.addValue("PresentmentRef", presentmentRef);
		source.addValue("ErrorCode", errorCode);
		if (StringUtils.trimToNull(errorDesc) != null) {
			errorDesc = (errorDesc.length() >= 1000) ? errorDesc.substring(0, 988) : errorDesc;
		}
		source.addValue("ErrorDesc", errorDesc);
		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception {}", e);
			throw e;
		} finally {
			source = null;
			sql = null;
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Fetching Count for Assigned partnerBankId to Different
	 * Finances/Commitments
	 */
	@Override
	public int getAssignedPartnerBankCount(long partnerBankId, String type) {
		logger.debug("Entering");

		int assignedCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PartnerBankId", partnerBankId);

		StringBuilder selectSql = new StringBuilder(" Select Count(1) ");
		selectSql.append(" From PresentmentHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PartnerBankId = :PartnerBankId ");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			assignedCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
			assignedCount = 0;
		}
		logger.debug("Leaving");
		return assignedCount;
	}

	@Override
	public String getPaymenyMode(String presentmentRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PresentmentRef", presentmentRef);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select mandatetype from presentmentheader PH ");
		sql.append(" Inner Join presentmentdetails PD ON PD.presentmentid = PH.id ");
		sql.append(" Where PD.presentmentref = :PresentmentRef ");
		logger.debug("selectSql: " + sql.toString());

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
		}
		logger.debug("Leaving");
		return null;
	}

	// MIGRATION PURPOSE
	@Override
	public List<PresentmentHeader> getPresentmentHeadersByRef(String reference, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();

		sql.append(" SELECT  id, reference, presentmentDate, partnerBankId, fromDate, toDate, ");
		sql.append(" status, mandateType, loanType, finBranch, schdate, dBStatusId,EntityCode, ");
		sql.append(" importStatusId, totalRecords, processedRecords, successRecords, failedRecords, ");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PresentmentHeader");
		sql.append(type);
		sql.append(" Where reference = :Reference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		try {
			RowMapper<PresentmentHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(PresentmentHeader.class);
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			return null;
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}
	}

	@Override
	public List<PresentmentDetail> getDMPresentmentDetailsByRef(String finReference, String type) {

		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" SELECT * ");
		sql.append(" From PresentmentDetails");
		sql.append(StringUtils.trim(type));
		sql.append(" Where FinReference = :FinReference ORDER by SchDate");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		try {
			RowMapper<PresentmentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(PresentmentDetail.class);
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			return null;
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}

	}

	/**
	 * get exclude list which doesnot contain exclude reason EMIINclude and EMI
	 * Advance
	 * 
	 * @param presentmentId
	 */
	@Override
	public List<PresentmentDetail> getExcludeDetails(long presentmentId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ID");
		sql.append(" FROM PRESENTMENTDETAILS ");
		sql.append(" WHERE EXCLUDEREASON NOT IN ('" + RepayConstants.PEXC_EMIINCLUDE + "','"
				+ RepayConstants.PEXC_EMIINADVANCE + "')  AND PRESENTMENTID=:PRESENTMENTID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PRESENTMENTID", presentmentId);

		RowMapper<PresentmentDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PresentmentDetail.class);
		try {
			return this.jdbcTemplate.query(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Method for get the Re-Presentment Details.
	 * 
	 * @param presentmentHeader
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Object> getPDCRePresentmentDetails(PresentmentHeader presentmentHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		int index = 0;
		List<Object> list = new ArrayList<Object>();
		try {
			sql = new StringBuilder();
			sql.append(
					" SELECT T1.FINREFERENCE, T1.SCHDATE, T1.SCHSEQ, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID, SCHDPFTPAID,");
			sql.append(
					" DEFSCHDDATE, FEESCHD, SCHDFEEPAID, INSSCHD, T2.MANDATEID, T1.DEFSCHDDATE, T2.FINREPAYMETHOD MANDATETYPE, T8.CHEQUESTATUS STATUS,");
			sql.append(
					" T8.CHEQUEDATE, T8.CHEQUEDETAILSID, T2.FINTYPE LOANTYPE, T5.BRANCHCODE, T1.TDSAMOUNT, T6.BANKCODE, T7.ENTITYCODE,");
			sql.append(" T1.INSTNUMBER EMINO, T2.FINBRANCH  FROM FINSCHEDULEDETAILS T1");
			sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FINREFERENCE = T2.FINREFERENCE");
			sql.append(" INNER JOIN RMTFINANCETYPES T3 ON T2.FINTYPE = T3.FINTYPE");
			sql.append(" INNER JOIN CHEQUEHEADER T4 ON T4.FINREFERENCE = T1.FINREFERENCE");
			sql.append(" INNER JOIN CHEQUEDETAIL T8 ON T8.HEADERID = T4.HEADERID AND T8.EMIREFNO = T1.INSTNUMBER");
			sql.append(" INNER JOIN RMTBRANCHES T5 ON T5.BRANCHCODE = T2.FINBRANCH");
			sql.append(" INNER JOIN BANKBRANCHES T6 ON T8.BANKBRANCHID = T6.BANKBRANCHID");
			sql.append(" INNER JOIN SMTDIVISIONDETAIL T7 ON T7.DIVISIONCODE=T3.FINDIVISION");
			sql.append(
					" WHERE (T2.FINISACTIVE = ?) AND ((T1.PROFITSCHD + T1.PRINCIPALSCHD + T1.FEESCHD - T1.SCHDPFTPAID - T1.SCHDPRIPAID - T1.SCHDFEEPAID) > ?)");
			sql.append(" AND ((SCHDATE >= ? AND SCHDATE <= ?)");
			sql.append(" OR (DEFSCHDDATE >= ? AND DEFSCHDDATE <= ?)) ");

			if (StringUtils.trimToNull(presentmentHeader.getMandateType()) != null) {
				sql.append(" AND (T2.FINREPAYMETHOD = ?) ");
			}

			if (StringUtils.trimToNull(presentmentHeader.getLoanType()) != null) {
				sql.append(" AND (T2.FINTYPE IN ( ");
				String[] loanTypes = presentmentHeader.getLoanType().split(",");
				for (int i = 0; i < loanTypes.length; i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append("?");
				}
				sql.append("))");
			}

			if (StringUtils.trimToNull(presentmentHeader.getFinBranch()) != null) {
				sql.append(" AND (T2.FINBRANCH IN ( ");
				String[] finBranches = presentmentHeader.getFinBranch().split(",");
				for (int i = 0; i < finBranches.length; i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append("?");
				}
				sql.append("))");
			}

			if (StringUtils.trimToNull(presentmentHeader.getEntityCode()) != null) {
				sql.append(" AND (T7.ENTITYCODE = ?) ");
			}

			// For Representment record must be exists in PresentmentDetails
			// with Bounce or Fail
			sql.append(
					" AND Exists( Select 1 from PresentmentDetails T6 where T1.FinReference = T6.FinReference AND T6.SCHDATE = T1.SCHDATE  AND ( T6.Status='B' OR T6.Status='F' )) ");
			sql.append(
					" AND Not Exists( Select 1 from PresentmentDetails T6 where T1.FinReference = T6.FinReference AND T6.SCHDATE = T1.SCHDATE ");

			// And Status not exists with I,A,S (extract,approved,success) And
			// ExcludeReason (EMIINCLUDE 0, EMIINADVANCE 1)
			sql.append(
					" AND ( T6.ExcludeReason = '0' OR T6.ExcludeReason = '1' ) AND ( T6.Status='I' OR T6.Status='S' OR T6.Status='A') ) ");

			// if record is manual exclude and batch not complete approve in
			// that case record not extracted again until batch is complete
			// approve.
			// #Bug Fix related to 135196
			sql.append(
					" AND Not Exists( Select 1 from PresentmentDetails T7 where T1.FinReference = T7.FinReference AND T7.SCHDATE = T1.SCHDATE ");
			sql.append(
					" AND T7.ExcludeReason = '6' AND T7.PresentmentID IN (Select ID FROM PRESENTMENTHEADER Where Status !> 3 )) ");

			sql.append(" ORDER BY T1.DEFSCHDDATE, T6.BANKCODE ,T7.EntityCode ");

			Connection conn = DataSourceUtils.doGetConnection(this.dataSource);
			stmt = conn.prepareStatement(sql.toString());
			stmt.setInt(1, 1);
			stmt.setBigDecimal(2, BigDecimal.ZERO);
			stmt.setDate(3, DateUtil.getSqlDate(presentmentHeader.getFromDate()));
			stmt.setDate(4, DateUtil.getSqlDate(presentmentHeader.getToDate()));
			stmt.setDate(5, DateUtil.getSqlDate(presentmentHeader.getFromDate()));
			stmt.setDate(6, DateUtil.getSqlDate(presentmentHeader.getToDate()));
			index = 6;
			if (StringUtils.trimToNull(presentmentHeader.getMandateType()) != null) {
				index = index + 1;
				stmt.setString(index, presentmentHeader.getMandateType());
			}

			if (StringUtils.trimToNull(presentmentHeader.getLoanType()) != null) {
				String[] loanTypes = presentmentHeader.getLoanType().split(",");
				int i = 0;
				for (i = 1; i <= loanTypes.length; i++) {
					stmt.setString(i + index, loanTypes[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(presentmentHeader.getFinBranch()) != null) {
				String[] finBranches = presentmentHeader.getFinBranch().split(",");
				int i = 0;
				for (i = 1; i <= finBranches.length; i++) {
					stmt.setString(i + index, finBranches[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(presentmentHeader.getEntityCode()) != null) {
				index = index + 1;
				stmt.setString(index, presentmentHeader.getEntityCode());
			}

			logger.trace(Literal.SQL + sql.toString());

			rs = stmt.executeQuery();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			sql = null;
		}
		logger.debug(Literal.LEAVING);
		list.add(rs);
		list.add(stmt);

		return list;
	}

	@Override
	public List<Object> getRePresentmentDetails(PresentmentHeader detailHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		int index = 0;
		List<Object> list = new ArrayList<Object>();
		try {
			sql = new StringBuilder();
			sql.append(
					" SELECT T1.FINREFERENCE, T1.SCHDATE, T1.SCHSEQ, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID, SCHDPFTPAID, DEFSCHDDATE,");
			sql.append(" FEESCHD, SCHDFEEPAID, INSSCHD, T2.MANDATEID, T1.DEFSCHDDATE, T4.MANDATETYPE, T4.STATUS,");
			sql.append(" T4.EXPIRYDATE, T2.FINTYPE LOANTYPE, T5.BRANCHCODE, T1.TDSAMOUNT, T6.BANKCODE, T7.ENTITYCODE,");
			sql.append(" T1.INSTNUMBER EMINO, T2.FINBRANCH  FROM FINSCHEDULEDETAILS T1");
			sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FINREFERENCE = T2.FINREFERENCE");
			sql.append(" INNER JOIN RMTFINANCETYPES T3 ON T2.FINTYPE = T3.FINTYPE");
			sql.append(" INNER JOIN MANDATES T4 ON T4.MANDATEID = T2.MANDATEID");
			sql.append(" INNER JOIN RMTBRANCHES T5 ON T5.BRANCHCODE = T2.FINBRANCH");
			sql.append(" INNER JOIN BANKBRANCHES T6 ON T4.BANKBRANCHID = T6.BANKBRANCHID");
			sql.append(" INNER JOIN SMTDIVISIONDETAIL T7 ON T7.DIVISIONCODE=T3.FINDIVISION");
			sql.append(
					" WHERE (T2.FINISACTIVE = ?) AND ((T1.PROFITSCHD + T1.PRINCIPALSCHD + T1.FEESCHD - T1.SCHDPFTPAID - T1.SCHDPRIPAID - T1.SCHDFEEPAID) > ?)");
			sql.append(" AND ((SCHDATE >= ? AND SCHDATE <= ?)");
			sql.append(" OR (DEFSCHDDATE >= ? AND DEFSCHDDATE <= ?)) ");

			if (StringUtils.trimToNull(detailHeader.getMandateType()) != null) {
				sql.append(" AND (T4.MANDATETYPE = ?) ");
			}

			if (StringUtils.trimToNull(detailHeader.getLoanType()) != null) {
				sql.append(" AND (T2.FINTYPE IN ( ");
				String[] loanTypes = detailHeader.getLoanType().split(",");
				for (int i = 0; i < loanTypes.length; i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append("?");
				}
				sql.append("))");
			}

			if (StringUtils.trimToNull(detailHeader.getFinBranch()) != null) {
				sql.append(" AND (T2.FINBRANCH IN ( ");
				String[] finBranches = detailHeader.getFinBranch().split(",");
				for (int i = 0; i < finBranches.length; i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append("?");
				}
				sql.append("))");
			}

			if (StringUtils.trimToNull(detailHeader.getEntityCode()) != null) {
				sql.append(" AND (T7.ENTITYCODE = ?) ");
			}

			// For Representment record must be exists in PresentmentDetails
			// with Bounce or Fail
			sql.append(
					" AND Exists( Select 1 from PresentmentDetails T6 where T1.FinReference = T6.FinReference AND T6.SCHDATE = T1.SCHDATE  AND ( T6.Status='B' OR T6.Status='F' )) ");
			sql.append(
					" AND Not Exists( Select 1 from PresentmentDetails T6 where T1.FinReference = T6.FinReference AND T6.SCHDATE = T1.SCHDATE ");

			// And Status not exists with I,A,S (extract,approved,success) And
			// ExcludeReason (EMIINCLUDE 0, EMIINADVANCE 1)
			sql.append(
					" AND ( T6.ExcludeReason = '0' OR T6.ExcludeReason = '1' ) AND ( T6.Status='I' OR T6.Status='S' OR T6.Status='A') ) ");

			// if record is manual exclude and batch not complete approve in
			// that case record not extracted again until batch is complete
			// approve.
			// #Bug Fix related to 135196
			sql.append(
					" AND Not Exists( Select 1 from PresentmentDetails T7 where T1.FinReference = T7.FinReference AND T7.SCHDATE = T1.SCHDATE ");
			sql.append(
					" AND T7.ExcludeReason = '6' AND T7.PresentmentID IN (Select ID FROM PRESENTMENTHEADER Where Status !> 3 )) ");

			sql.append(" ORDER BY T1.DEFSCHDDATE, T6.BANKCODE ,T7.EntityCode ");

			Connection conn = DataSourceUtils.doGetConnection(this.dataSource);
			stmt = conn.prepareStatement(sql.toString());
			stmt.setInt(1, 1);
			stmt.setBigDecimal(2, BigDecimal.ZERO);
			stmt.setDate(3, DateUtil.getSqlDate(detailHeader.getFromDate()));
			stmt.setDate(4, DateUtil.getSqlDate(detailHeader.getToDate()));
			stmt.setDate(5, DateUtil.getSqlDate(detailHeader.getFromDate()));
			stmt.setDate(6, DateUtil.getSqlDate(detailHeader.getToDate()));
			index = 6;
			if (StringUtils.trimToNull(detailHeader.getMandateType()) != null) {
				index = index + 1;
				stmt.setString(index, detailHeader.getMandateType());
			}

			if (StringUtils.trimToNull(detailHeader.getLoanType()) != null) {
				String[] loanTypes = detailHeader.getLoanType().split(",");
				int i = 0;
				for (i = 1; i <= loanTypes.length; i++) {
					stmt.setString(i + index, loanTypes[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(detailHeader.getFinBranch()) != null) {
				String[] finBranches = detailHeader.getFinBranch().split(",");
				int i = 0;
				for (i = 1; i <= finBranches.length; i++) {
					stmt.setString(i + index, finBranches[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(detailHeader.getEntityCode()) != null) {
				index = index + 1;
				stmt.setString(index, detailHeader.getEntityCode());
			}

			logger.trace(Literal.SQL + sql.toString());

			rs = stmt.executeQuery();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			sql = null;
		}
		logger.debug(Literal.LEAVING);
		list.add(rs);
		list.add(stmt);

		return list;
	}
}
