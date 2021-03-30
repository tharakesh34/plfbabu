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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.financemanagement.impl.PresentmentDetailExtractService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PresentmentHeader</code> with set of CRUD operations.
 */
public class PresentmentDetailDAOImpl extends SequenceDao<PresentmentHeader> implements PresentmentDetailDAO {
	private static Logger logger = LogManager.getLogger(PresentmentDetailDAOImpl.class);

	public PresentmentDetailDAOImpl() {
		super();
	}

	@Override
	public PresentmentHeader getPresentmentHeader(long id, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Id, Reference, PresentmentDate, PartnerBankId, FromDate, ToDate, PresentmentType");
		sql.append(", Status, MandateType, LoanType, FinBranch, SchDate, DBStatusId, EntityCode");
		sql.append(", ImportStatusId, TotalRecords, ProcessedRecords, SuccessRecords, FailedRecords");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(", PartnerBankCode, PartnerBankName, PartnerAcctNumber, PartnerAcctType");
		}
		sql.append(" From PresentmentHeader");
		sql.append(type);
		sql.append(" Where id = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), new Object[] { id }, (rs, rowNum) -> {
				PresentmentHeader ph = new PresentmentHeader();
				ph.setId(rs.getLong("Id"));
				ph.setReference(rs.getString("Reference"));
				ph.setPresentmentDate(rs.getDate("PresentmentDate"));
				ph.setPartnerBankId(rs.getLong("PartnerBankId"));
				ph.setFromDate(rs.getDate("FromDate"));
				ph.setToDate(rs.getDate("ToDate"));
				ph.setPresentmentType(rs.getString("PresentmentType"));
				ph.setStatus(rs.getInt("Status"));
				ph.setMandateType(rs.getString("MandateType"));
				ph.setLoanType(rs.getString("LoanType"));
				ph.setFinBranch(rs.getString("FinBranch"));
				ph.setSchdate(rs.getDate("SchDate"));
				ph.setdBStatusId(rs.getLong("DBStatusId"));
				ph.setEntityCode(rs.getString("EntityCode"));
				ph.setImportStatusId(rs.getLong("ImportStatusId"));
				ph.setTotalRecords(rs.getInt("TotalRecords"));
				ph.setProcessedRecords(rs.getInt("ProcessedRecords"));
				ph.setSuccessRecords(rs.getInt("SuccessRecords"));
				ph.setFailedRecords(rs.getInt("FailedRecords"));
				ph.setVersion(rs.getInt("Version"));
				ph.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ph.setLastMntBy(rs.getLong("LastMntBy"));
				ph.setRecordStatus(rs.getString("RecordStatus"));
				ph.setRoleCode(rs.getString("RoleCode"));
				ph.setNextRoleCode(rs.getString("NextRoleCode"));
				ph.setTaskId(rs.getString("TaskId"));
				ph.setNextTaskId(rs.getString("NextTaskId"));
				ph.setRecordType(rs.getString("RecordType"));
				ph.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.containsIgnoreCase(type, "View")) {
					ph.setPartnerBankCode(rs.getString("PartnerBankCode"));
					ph.setPartnerBankName(rs.getString("PartnerBankName"));
					ph.setPartnerAcctNumber(rs.getString("PartnerAcctNumber"));
					ph.setPartnerAcctType(rs.getString("PartnerAcctType"));
				}

				return ph;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records not exists in PresentmentHeader{} table/view for the specified Id >> {}", type, id);
		}

		return null;
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
		return getNextValue(tableName);
	}

	private void setReferences(PresentmentDetail presentment) {
		if (presentment.getId() == Long.MIN_VALUE) {
			presentment.setId(getNextValue("SEQPRESENTMENTDETAILS"));
		}

		if (presentment.getPresentmentRef() == null) {
			return;
		}

		String reference = presentment.getPresentmentRef();
		String presentmentRef = StringUtils.leftPad(String.valueOf(presentment.getId()), 29 - reference.length(), "0");
		presentment.setPresentmentRef(reference.concat(presentmentRef));
	}

	@Override
	public long saveList(List<PresentmentDetail> presentments) {
		presentments.forEach(this::setReferences);

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert into PresentmentDetails");
		sql.append(" (Id, PresentmentId, PresentmentRef, FinReference, SchDate, MandateId, SchAmtDue, SchPriDue");
		sql.append(", SchPftDue, SchFeeDue, SchInsDue, SchPenaltyDue, AdvanceAmt, ExcessID, AdviseAmt, PresentmentAmt");
		sql.append(", ExcludeReason, BounceID, EmiNo, TDSAmount, Status, ReceiptID");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				int i = 1;
				PresentmentDetail pd = presentments.get(index);

				ps.setLong(i++, pd.getId());
				ps.setLong(i++, pd.getPresentmentId());
				ps.setString(i++, pd.getPresentmentRef());
				ps.setString(i++, pd.getFinReference());
				ps.setDate(i++, JdbcUtil.getDate(pd.getSchDate()));
				ps.setLong(i++, pd.getMandateId());
				ps.setBigDecimal(i++, pd.getSchAmtDue());
				ps.setBigDecimal(i++, pd.getSchPriDue());
				ps.setBigDecimal(i++, pd.getSchPftDue());
				ps.setBigDecimal(i++, pd.getSchFeeDue());
				ps.setBigDecimal(i++, pd.getSchInsDue());
				ps.setBigDecimal(i++, pd.getSchPenaltyDue());
				ps.setBigDecimal(i++, pd.getAdvanceAmt());
				ps.setLong(i++, pd.getExcessID());
				ps.setBigDecimal(i++, pd.getAdviseAmt());
				ps.setBigDecimal(i++, pd.getPresentmentAmt());
				ps.setLong(i++, pd.getExcludeReason());
				ps.setLong(i++, pd.getBounceID());
				ps.setInt(i++, pd.getEmiNo());
				ps.setBigDecimal(i++, pd.gettDSAmount());
				ps.setString(i++, pd.getStatus());
				ps.setLong(i++, pd.getReceiptID());
				ps.setInt(i++, pd.getVersion());
				ps.setLong(i++, pd.getLastMntBy());
				ps.setTimestamp(i++, pd.getLastMntOn());
				ps.setString(i++, pd.getRecordStatus());
				ps.setString(i++, pd.getRoleCode());
				ps.setString(i++, pd.getNextRoleCode());
				ps.setString(i++, pd.getTaskId());
				ps.setString(i++, pd.getNextTaskId());
				ps.setString(i++, pd.getRecordType());
				ps.setLong(i++, pd.getWorkflowId());

			}

			@Override
			public int getBatchSize() {
				return presentments.size();
			}
		}).length;
	}

	private String extactPresentmentQuery(PresentmentHeader ph) {
		StringBuilder sql = new StringBuilder();
		sql = new StringBuilder();
		sql.append(
				" SELECT T1.FINREFERENCE, T1.SCHDATE, T1.SCHSEQ, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID, SCHDPFTPAID, DEFSCHDDATE,");
		sql.append(
				" FEESCHD, SCHDFEEPAID, INSSCHD, T2.MANDATEID, T1.DEFSCHDDATE, T4.MANDATETYPE, T4.EMANDATESOURCE, T4.STATUS,");
		sql.append(" T4.EXPIRYDATE, T2.FINTYPE LOANTYPE, T5.BRANCHCODE, T1.TDSAMOUNT, T6.BANKCODE, T7.ENTITYCODE,");
		sql.append(" T1.INSTNUMBER EMINO, T2.FINBRANCH, T1.BPIORHOLIDAY, T2.BPITREATMENT");
		sql.append(", T2.GRCADVTYPE, T2.ADVTYPE, T2.GRCPERIODENDDATE,T2.ADVSTAGE ,T4.PARTNERBANKID ");
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

		if (StringUtils.trimToNull(ph.getMandateType()) != null) {
			if (StringUtils.equals(MandateConstants.TYPE_EMANDATE, ph.getMandateType())
					&& StringUtils.isNotEmpty(ph.getEmandateSource())) {
				sql.append(" AND (T4.MANDATETYPE = ?) AND (T4.EMANDATESOURCE = ?) ");
			} else {
				sql.append(" AND (T4.MANDATETYPE = ?) ");
			}
		}

		if (StringUtils.trimToNull(ph.getLoanType()) != null) {
			sql.append(" AND (T2.FINTYPE IN ( ");
			String[] loanTypes = ph.getLoanType().split(",");
			for (int i = 0; i < loanTypes.length; i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append("?");
			}
			sql.append("))");
		}

		if (StringUtils.trimToNull(ph.getFinBranch()) != null) {
			sql.append(" AND (T2.FINBRANCH IN ( ");
			String[] finBranches = ph.getFinBranch().split(",");
			for (int i = 0; i < finBranches.length; i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append("?");
			}
			sql.append("))");
		}

		if (StringUtils.trimToNull(ph.getEntityCode()) != null) {
			sql.append(" AND (T7.ENTITYCODE = ?) ");
		}

		// For Presentment records not exit in the PresentmentDetails table with that scheduleDate
		sql.append(" AND Not Exists( Select 1 from PresentmentDetails T6 where T1.FinReference = T6.FinReference ");
		sql.append(
				" AND T6.SCHDATE = T1.SCHDATE  AND (T6.ExcludeReason = '0' OR T6.ExcludeReason = '1' OR T6.ExcludeReason = '12' OR T6.ExcludeReason = '13' )) ");

		// if record is manual exclude and batch not complete approve in that case record not extracted again until
		// batch is complete approve.
		// #Bug Fix related to 135196
		sql.append(
				" AND Not Exists( Select 1 from PresentmentDetails T7 where T1.FinReference = T7.FinReference AND T7.SCHDATE = T1.SCHDATE ");
		sql.append(
				" AND T7.ExcludeReason = '6' AND T7.PresentmentID IN (Select ID FROM PRESENTMENTHEADER Where  Status =1 OR  Status =2 OR Status =3 )) ");

		if (SysParamUtil.isAllowed(SMTParameterConstants.GROUP_BATCH_BY_BANK) && isGroupByPartnerBank(ph)) {
			sql.append("ORDER BY T6.BANKCODE, T1.DEFSCHDDATE, T7.EntityCode, T4.PARTNERBANKID");
		} else if (SysParamUtil.isAllowed(SMTParameterConstants.GROUP_BATCH_BY_BANK) && !isGroupByPartnerBank(ph)) {
			sql.append("ORDER BY T6.BANKCODE, T1.DEFSCHDDATE, T7.EntityCode");
		} else if (isGroupByPartnerBank(ph)) {
			sql.append("ORDER BY  T1.DEFSCHDDATE, T7.EntityCode, T4.PARTNERBANKID");
		} else if (SysParamUtil.isAllowed(SMTParameterConstants.GROUP_BATCH_BY_BANK)) {
			sql.append("ORDER BY T6.BANKCODE, T1.DEFSCHDDATE, T7.EntityCode");
		} else {
			sql.append("ORDER BY  T1.DEFSCHDDATE, T7.EntityCode");
		}
		return sql.toString();
	}

	private boolean isGroupByPartnerBank(PresentmentHeader ph) {
		if (ImplementationConstants.GROUP_BATCH_BY_PARTNERBANK
				&& !MandateConstants.TYPE_PDC.equals(ph.getMandateType())) {
			return true;
		}
		return false;
	}

	@Override
	public void extactPresentments(PresentmentHeader ph, PresentmentDetailExtractService service) {
		String sql = extactPresentmentQuery(ph);

		jdbcOperations.query(sql.toString(), ps -> {
			ps.setInt(1, 1);
			ps.setBigDecimal(2, BigDecimal.ZERO);
			ps.setDate(3, DateUtil.getSqlDate(ph.getFromDate()));
			ps.setDate(4, DateUtil.getSqlDate(ph.getToDate()));
			ps.setDate(5, DateUtil.getSqlDate(ph.getFromDate()));
			ps.setDate(6, DateUtil.getSqlDate(ph.getToDate()));
			int index = 6;
			if (StringUtils.trimToNull(ph.getMandateType()) != null) {
				index = index + 1;
				ps.setString(index, ph.getMandateType());
				if (StringUtils.equals(MandateConstants.TYPE_EMANDATE, ph.getMandateType())
						&& StringUtils.isNotEmpty(ph.getEmandateSource())) {
					index = index + 1;
					ps.setString(index, ph.getEmandateSource());
				}
			}

			if (StringUtils.trimToNull(ph.getLoanType()) != null) {
				String[] loanTypes = ph.getLoanType().split(",");
				int i = 0;
				for (i = 1; i <= loanTypes.length; i++) {
					ps.setString(i + index, loanTypes[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(ph.getFinBranch()) != null) {
				String[] finBranches = ph.getFinBranch().split(",");
				int i = 0;
				for (i = 1; i <= finBranches.length; i++) {
					ps.setString(i + index, finBranches[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(ph.getEntityCode()) != null) {
				index = index + 1;
				ps.setString(index, ph.getEntityCode());
			}
		}, rs -> {
			service.processPresentment(ph, rs);
		});
	}

	private String extactRePresentmentQuery(PresentmentHeader ph) {
		StringBuilder sql = new StringBuilder();
		sql = new StringBuilder();
		sql.append(
				" SELECT T1.FINREFERENCE, T1.SCHDATE, T1.SCHSEQ, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID, SCHDPFTPAID, DEFSCHDDATE,");
		sql.append(
				" FEESCHD, SCHDFEEPAID, INSSCHD, T2.MANDATEID, T1.DEFSCHDDATE, T4.MANDATETYPE, T4.STATUS, T4.PARTNERBANKID,");
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

		if (StringUtils.trimToNull(ph.getMandateType()) != null) {
			sql.append(" AND (T4.MANDATETYPE = ?) ");
		}

		if (StringUtils.trimToNull(ph.getLoanType()) != null) {
			sql.append(" AND (T2.FINTYPE IN ( ");
			String[] loanTypes = ph.getLoanType().split(",");
			for (int i = 0; i < loanTypes.length; i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append("?");
			}
			sql.append("))");
		}

		if (StringUtils.trimToNull(ph.getFinBranch()) != null) {
			sql.append(" AND (T2.FINBRANCH IN ( ");
			String[] finBranches = ph.getFinBranch().split(",");
			for (int i = 0; i < finBranches.length; i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append("?");
			}
			sql.append("))");
		}

		if (StringUtils.trimToNull(ph.getEntityCode()) != null) {
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
				" AND ( T6.ExcludeReason = '0' OR T6.ExcludeReason = '1' OR T6.ExcludeReason = '12' OR T6.ExcludeReason = '13' ) AND ( T6.Status='I' OR T6.Status='S' OR T6.Status='A') ) ");

		// if record is manual exclude and batch not complete approve in
		// that case record not extracted again until batch is complete
		// approve.
		// #Bug Fix related to 135196
		sql.append(
				" AND Not Exists( Select 1 from PresentmentDetails T7 where T1.FinReference = T7.FinReference AND T7.SCHDATE = T1.SCHDATE ");
		sql.append(
				" AND T7.ExcludeReason = '6' AND T7.PresentmentID IN (Select ID FROM PRESENTMENTHEADER Where Status =1 OR  Status =2 OR Status =3 )) ");

		sql.append(" ORDER BY T1.DEFSCHDDATE, T6.BANKCODE ,T7.EntityCode ");

		return sql.toString();
	}

	@Override
	public void extactRePresentments(PresentmentHeader ph, PresentmentDetailExtractService service) {
		String sql = extactRePresentmentQuery(ph);

		jdbcOperations.query(sql.toString(), ps -> {
			ps.setInt(1, 1);
			ps.setBigDecimal(2, BigDecimal.ZERO);
			ps.setDate(3, DateUtil.getSqlDate(ph.getFromDate()));
			ps.setDate(4, DateUtil.getSqlDate(ph.getToDate()));
			ps.setDate(5, DateUtil.getSqlDate(ph.getFromDate()));
			ps.setDate(6, DateUtil.getSqlDate(ph.getToDate()));
			int index = 6;
			if (StringUtils.trimToNull(ph.getMandateType()) != null) {
				index = index + 1;
				ps.setString(index, ph.getMandateType());
			}

			if (StringUtils.trimToNull(ph.getLoanType()) != null) {
				String[] loanTypes = ph.getLoanType().split(",");
				int i = 0;
				for (i = 1; i <= loanTypes.length; i++) {
					ps.setString(i + index, loanTypes[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(ph.getFinBranch()) != null) {
				String[] finBranches = ph.getFinBranch().split(",");
				int i = 0;
				for (i = 1; i <= finBranches.length; i++) {
					ps.setString(i + index, finBranches[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(ph.getEntityCode()) != null) {
				index = index + 1;
				ps.setString(index, ph.getEntityCode());
			}
		}, rs -> {
			service.processPresentment(ph, rs);
		});
	}

	private String extactPDCPresentmentQuery(PresentmentHeader header) {
		StringBuilder sql = new StringBuilder();
		sql = new StringBuilder();
		sql.append(
				" SELECT T1.FINREFERENCE, T1.SCHDATE, T1.SCHSEQ, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID, SCHDPFTPAID,");
		sql.append(
				" DEFSCHDDATE, FEESCHD, SCHDFEEPAID, INSSCHD, T2.MANDATEID, T1.DEFSCHDDATE, T2.FINREPAYMETHOD MANDATETYPE, T8.CHEQUESTATUS STATUS,");
		sql.append(
				" T8.CHEQUEDATE, T8.CHEQUEDETAILSID, T2.FINTYPE LOANTYPE, T5.BRANCHCODE, T1.TDSAMOUNT, T6.BANKCODE, T7.ENTITYCODE,");
		sql.append(" T1.INSTNUMBER EMINO, T2.FINBRANCH, T1.BPIORHOLIDAY, T2.BPITREATMENT");
		sql.append(", T2.GRCADVTYPE, T2.ADVTYPE, T2.GRCPERIODENDDATE,T2.ADVSTAGE");
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

		if (StringUtils.trimToNull(header.getMandateType()) != null) {
			sql.append(" AND (T2.FINREPAYMETHOD = ?) ");
		}

		if (StringUtils.trimToNull(header.getLoanType()) != null) {
			sql.append(" AND (T2.FINTYPE IN ( ");
			String[] loanTypes = header.getLoanType().split(",");
			for (int i = 0; i < loanTypes.length; i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append("?");
			}
			sql.append("))");
		}

		if (StringUtils.trimToNull(header.getFinBranch()) != null) {
			sql.append(" AND (T2.FINBRANCH IN ( ");
			String[] finBranches = header.getFinBranch().split(",");
			for (int i = 0; i < finBranches.length; i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append("?");
			}
			sql.append("))");
		}

		if (StringUtils.trimToNull(header.getEntityCode()) != null) {
			sql.append(" AND (T7.ENTITYCODE = ?) ");
		}

		// For Presentment records not exit in the PresentmentDetails table with that scheduleDate
		sql.append(" AND Not Exists( Select 1 from PresentmentDetails T6 where T1.FinReference = T6.FinReference ");
		sql.append(
				" AND T6.SCHDATE = T1.SCHDATE  AND (T6.ExcludeReason = '0' OR T6.ExcludeReason = '1'  OR T6.ExcludeReason = '12' OR T6.ExcludeReason = '13' )) ");

		// if record is manual exclude and batch not complete approve in that case record not extracted again until
		// batch is complete approve.
		// #Bug Fix related to 135196
		sql.append(
				" AND Not Exists( Select 1 from PresentmentDetails T7 where T1.FinReference = T7.FinReference AND T7.SCHDATE = T1.SCHDATE ");
		sql.append(
				" AND T7.ExcludeReason = '6' AND T7.PresentmentID IN (Select ID FROM PRESENTMENTHEADER Where Status =1 OR  Status =2 OR Status =3 )) ");

		sql.append(" ORDER BY T1.DEFSCHDDATE, T6.BANKCODE ,T7.EntityCode ");
		return sql.toString();
	}

	@Override
	public void extactPDCPresentments(PresentmentHeader ph, PresentmentDetailExtractService service) {
		String sql = extactPDCPresentmentQuery(ph);

		jdbcOperations.query(sql.toString(), ps -> {
			ps.setInt(1, 1);
			ps.setBigDecimal(2, BigDecimal.ZERO);
			ps.setDate(3, DateUtil.getSqlDate(ph.getFromDate()));
			ps.setDate(4, DateUtil.getSqlDate(ph.getToDate()));
			ps.setDate(5, DateUtil.getSqlDate(ph.getFromDate()));
			ps.setDate(6, DateUtil.getSqlDate(ph.getToDate()));
			int index = 6;
			if (StringUtils.trimToNull(ph.getMandateType()) != null) {
				index = index + 1;
				ps.setString(index, ph.getMandateType());
			}

			if (StringUtils.trimToNull(ph.getLoanType()) != null) {
				String[] loanTypes = ph.getLoanType().split(",");
				int i = 0;
				for (i = 1; i <= loanTypes.length; i++) {
					ps.setString(i + index, loanTypes[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(ph.getFinBranch()) != null) {
				String[] finBranches = ph.getFinBranch().split(",");
				int i = 0;
				for (i = 1; i <= finBranches.length; i++) {
					ps.setString(i + index, finBranches[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(ph.getEntityCode()) != null) {
				index = index + 1;
				ps.setString(index, ph.getEntityCode());
			}
		}, rs -> {
			service.processPDCPresentment(ph, rs);
		});

	}

	private String extactPDCRePresentmentQuery(PresentmentHeader ph) {
		StringBuilder sql = new StringBuilder();
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

		if (StringUtils.trimToNull(ph.getMandateType()) != null) {
			sql.append(" AND (T2.FINREPAYMETHOD = ?) ");
		}

		if (StringUtils.trimToNull(ph.getLoanType()) != null) {
			sql.append(" AND (T2.FINTYPE IN ( ");
			String[] loanTypes = ph.getLoanType().split(",");
			for (int i = 0; i < loanTypes.length; i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append("?");
			}
			sql.append("))");
		}

		if (StringUtils.trimToNull(ph.getFinBranch()) != null) {
			sql.append(" AND (T2.FINBRANCH IN ( ");
			String[] finBranches = ph.getFinBranch().split(",");
			for (int i = 0; i < finBranches.length; i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append("?");
			}
			sql.append("))");
		}

		if (StringUtils.trimToNull(ph.getEntityCode()) != null) {
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
				" AND ( T6.ExcludeReason = '0' OR T6.ExcludeReason = '1' OR T6.ExcludeReason = '12' OR T6.ExcludeReason = '13' ) AND ( T6.Status='I' OR T6.Status='S' OR T6.Status='A') ) ");

		// if record is manual exclude and batch not complete approve in
		// that case record not extracted again until batch is complete
		// approve.
		// #Bug Fix related to 135196
		sql.append(
				" AND Not Exists( Select 1 from PresentmentDetails T7 where T1.FinReference = T7.FinReference AND T7.SCHDATE = T1.SCHDATE ");
		sql.append(
				" AND T7.ExcludeReason = '6' AND T7.PresentmentID IN (Select ID FROM PRESENTMENTHEADER Where Status =1 OR  Status =2 OR Status =3 )) ");

		sql.append(" ORDER BY T1.DEFSCHDDATE, T6.BANKCODE ,T7.EntityCode ");
		return sql.toString();
	}

	@Override
	public void extactPDCRePresentments(PresentmentHeader ph, PresentmentDetailExtractService service) {
		String sql = extactPDCRePresentmentQuery(ph);

		jdbcOperations.query(sql.toString(), ps -> {
			ps.setInt(1, 1);
			ps.setBigDecimal(2, BigDecimal.ZERO);
			ps.setDate(3, DateUtil.getSqlDate(ph.getFromDate()));
			ps.setDate(4, DateUtil.getSqlDate(ph.getToDate()));
			ps.setDate(5, DateUtil.getSqlDate(ph.getFromDate()));
			ps.setDate(6, DateUtil.getSqlDate(ph.getToDate()));
			int index = 6;
			if (StringUtils.trimToNull(ph.getMandateType()) != null) {
				index = index + 1;
				ps.setString(index, ph.getMandateType());
			}

			if (StringUtils.trimToNull(ph.getLoanType()) != null) {
				String[] loanTypes = ph.getLoanType().split(",");
				int i = 0;
				for (i = 1; i <= loanTypes.length; i++) {
					ps.setString(i + index, loanTypes[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(ph.getFinBranch()) != null) {
				String[] finBranches = ph.getFinBranch().split(",");
				int i = 0;
				for (i = 1; i <= finBranches.length; i++) {
					ps.setString(i + index, finBranches[i - 1]);
				}
				index = index + i - 1;
			}

			if (StringUtils.trimToNull(ph.getEntityCode()) != null) {
				index = index + 1;
				ps.setString(index, ph.getEntityCode());
			}
		}, rs -> {
			service.processPDCPresentment(ph, rs);
		});

	}

	@Override
	public long savePresentmentHeader(PresentmentHeader ph) {
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" PresentmentHeader");
		sql.append("(Id, Reference, PresentmentDate, PartnerBankId, FromDate, ToDate, PresentmentType");
		sql.append(", Status, MandateType, EmandateSource, FinBranch, Schdate, LoanType, ImportStatusId");
		sql.append(", TotalRecords, ProcessedRecords, SuccessRecords, FailedRecords, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId, dBStatusId, bankCode, EntityCode");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?");
		sql.append(")");

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, JdbcUtil.setLong(ph.getId()));
			ps.setString(index++, ph.getReference());
			ps.setDate(index++, JdbcUtil.getDate(ph.getPresentmentDate()));
			ps.setLong(index++, JdbcUtil.setLong(ph.getPartnerBankId()));
			ps.setDate(index++, JdbcUtil.getDate(ph.getFromDate()));
			ps.setDate(index++, JdbcUtil.getDate(ph.getToDate()));
			ps.setString(index++, ph.getPresentmentType());
			ps.setInt(index++, ph.getStatus());
			ps.setString(index++, ph.getMandateType());
			ps.setString(index++, ph.getEmandateSource());
			ps.setString(index++, ph.getFinBranch());
			ps.setDate(index++, JdbcUtil.getDate(ph.getSchdate()));
			ps.setString(index++, ph.getLoanType());
			ps.setLong(index++, JdbcUtil.setLong(ph.getImportStatusId()));
			ps.setInt(index++, ph.getTotalRecords());
			ps.setInt(index++, ph.getProcessedRecords());
			ps.setInt(index++, ph.getSuccessRecords());
			ps.setInt(index++, ph.getFailedRecords());
			ps.setInt(index++, ph.getVersion());
			ps.setLong(index++, JdbcUtil.setLong(ph.getLastMntBy()));
			ps.setTimestamp(index++, ph.getLastMntOn());
			ps.setString(index++, ph.getRecordStatus());
			ps.setString(index++, ph.getRoleCode());
			ps.setString(index++, ph.getNextRoleCode());
			ps.setString(index++, ph.getTaskId());
			ps.setString(index++, ph.getNextTaskId());
			ps.setString(index++, ph.getRecordType());
			ps.setLong(index++, JdbcUtil.setLong(ph.getWorkflowId()));
			ps.setLong(index++, JdbcUtil.setLong(ph.getdBStatusId()));
			ps.setString(index++, ph.getBankCode());
			ps.setString(index++, ph.getEntityCode());
		});

		return ph.getId();
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
			RowMapper<PresentmentDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(PresentmentDetail.class);
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
	public int updatePresentmentDetials(long presentmentId, List<Long> list, int mnualExclude) {
		int count = 0;
		List<PresentmentDetail> presements = new ArrayList<>();

		for (Long id : list) {
			PresentmentDetail pd = new PresentmentDetail();
			pd.setId(id);
			pd.setPresentmentId(presentmentId);
			pd.setExcludeReason(mnualExclude);

			presements.add(pd);

			if (presements.size() == PennantConstants.CHUNK_SIZE) {
				count = count + updatePresentmentDetials(presements);
				presements.clear();
			}

			if (presements.size() > 0) {
				count = count + updatePresentmentDetials(presements);
			}
		}
		return count;
	}

	private int updatePresentmentDetials(List<PresentmentDetail> presements) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" PresentmentDetails");
		sql.append(" Set ExcludeReason = ?");
		sql.append(" Where PresentmentID = ? and ID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				PresentmentDetail item = presements.get(index);
				ps.setInt(1, item.getExcludeReason());
				ps.setLong(2, item.getPresentmentId());
				ps.setLong(3, item.getId());
			}

			@Override
			public int getBatchSize() {
				return presements.size();
			}
		}).length;
	}

	@Override
	public void updatePresentmentHeader(long presentmentId, int manualExclude, long partnerBankId) {
		String sql = " UPDATE PresentmentHeader Set Status = ?, PartnerBankId = ? Where ID = ?";
		logger.trace(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, manualExclude);
			ps.setLong(2, partnerBankId);
			ps.setLong(3, presentmentId);
		});
	}

	@Override
	public int updateSchdWithPresentmentId(List<PresentmentDetail> presenetments) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update FinScheduleDetails Set PresentmentId = ?");
		sql.append(" Where FinReference = ? and SchDate = ? and  SchSeq = ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				PresentmentDetail pd = presenetments.get(index);
				ps.setLong(1, pd.getPresentmentId());
				ps.setString(2, pd.getFinReference());
				ps.setDate(3, JdbcUtil.getDate(pd.getSchDate()));
				ps.setInt(4, pd.getSchSeq());
			}

			@Override
			public int getBatchSize() {
				return presenetments.size();
			}
		}).length;
	}

	@Override
	public void updatePresentmentIdAsZero(long presentmentId) {
		String sql = "Update FinScheduleDetails Set PresentmentId = ? Where PresentmentId = ?";
		logger.trace(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, 0);
			ps.setLong(2, presentmentId);
		});
	}

	@Override
	public void updatePresentmentIdAsZero(List<Long> presentmentIds) {
		String sql = "Update FinScheduleDetails Set PresentmentId = ? Where PresentmentId = ?";

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Long id = presentmentIds.get(i);
				ps.setLong(1, 0);
				ps.setLong(2, id);
			}

			@Override
			public int getBatchSize() {
				return presentmentIds.size();
			}
		});

	}

	@Override
	public void deletePresentmentDetails(long presentmentId) {
		String sql = "Delete from PresentmentDetails where PresentmentId = ?";

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql, ps -> {
			ps.setLong(1, presentmentId);
		});
	}

	@Override
	public void deletePresentmentHeader(long id) {
		String sql = "Delete from PresentmentHeader where Id = ?";

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql, ps -> {
				ps.setLong(1, id);
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public PresentmentDetail getPresentmentDetail(String presentmentRef, String type) {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" id, presentmentId, finReference, schDate, mandateId, schAmtDue, schPriDue, schPftDue");
		sql.append(", schFeeDue, schInsDue, schPenaltyDue, advanceAmt, excessID, adviseAmt, presentmentAmt");
		sql.append(", tDSAmount, excludeReason, emiNo, status, version, lastMntBy, lastMntOn, recordStatus");
		sql.append(", roleCode, nextRoleCode, taskId, nextTaskId, recordType, workflowId, presentmentRef");
		sql.append(", ecsReturn, receiptID, errorCode, errorDesc, manualAdviseId");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(", MandateType");
		}
		sql.append(" from presentmentdetails");
		sql.append(type);
		sql.append("  where presentmentRef = ?");

		PresentmentDetailRowMapper rowMapper = new PresentmentDetailRowMapper(type);
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { presentmentRef }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}

		return null;

	}

	@Override
	public List<PresentmentDetail> getPresentmentDetail(long presentmentId, boolean includeData) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t.Id, t.PresentmentId, t.FinReference, t.SchDate, t.MandateId, t.SchAmtDue, t.SchPriDue");
		sql.append(", t.SchPftDue, t.SchFeeDue, t.SchInsDue, t.SchPenaltyDue, t.AdvanceAmt, t.ExcessID");
		sql.append(", t.AdviseAmt, t.PresentmentAmt, t.EmiNo, t.Status, t.PresentmentRef, t.EcsReturn");
		sql.append(", t.ReceiptID, t.ExcludeReason, t.Version, t.LastMntOn, t.LastMntBy, t.RecordStatus");
		sql.append(", t.RoleCode, t.NextRoleCode, t.TaskId, t.NextTaskId, t.RecordType, t.WorkflowId");

		if (includeData) {
			sql.append(", pb.AccountNo, pb.AcType");
		}

		sql.append(" From PresentmentDetails t");

		if (includeData) {
			sql.append(" inner join PresentmentHeader ph on ph.ID = t.PresentmentID");
			sql.append(" inner join PartnerBanks pb on pb.PartnerBankID = ph.PartnerBankID ");
		}

		sql.append(" Where t.PresentmentId = ?");

		if (includeData) {
			sql.append(" and (t.ExcludeReason = ? or t.ExcludeReason = ?) and t.Status <> ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, presentmentId);

			if (includeData) {
				ps.setInt(index++, RepayConstants.PEXC_EMIINCLUDE);
				ps.setInt(index++, RepayConstants.PEXC_EMIINADVANCE);
				ps.setString(index++, RepayConstants.PEXC_APPROV);
			}
		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setId(rs.getLong("Id"));
			pd.setPresentmentId(rs.getLong("PresentmentId"));
			pd.setFinReference(rs.getString("FinReference"));
			pd.setSchDate(rs.getTimestamp("SchDate"));
			pd.setMandateId(rs.getLong("MandateId"));
			pd.setSchAmtDue(rs.getBigDecimal("SchAmtDue"));
			pd.setSchPriDue(rs.getBigDecimal("SchPriDue"));
			pd.setSchPftDue(rs.getBigDecimal("SchPftDue"));
			pd.setSchFeeDue(rs.getBigDecimal("SchFeeDue"));
			pd.setSchInsDue(rs.getBigDecimal("SchInsDue"));
			pd.setSchPenaltyDue(rs.getBigDecimal("SchPenaltyDue"));
			pd.setAdvanceAmt(rs.getBigDecimal("AdvanceAmt"));
			pd.setExcessID(rs.getLong("ExcessID"));
			pd.setAdviseAmt(rs.getBigDecimal("AdviseAmt"));
			pd.setPresentmentAmt(rs.getBigDecimal("PresentmentAmt"));
			pd.setEmiNo(rs.getInt("EmiNo"));
			pd.setStatus(rs.getString("Status"));
			pd.setPresentmentRef(rs.getString("PresentmentRef"));
			pd.setEcsReturn(rs.getString("EcsReturn"));
			pd.setReceiptID(rs.getLong("ReceiptID"));
			pd.setExcludeReason(rs.getInt("ExcludeReason"));
			pd.setVersion(rs.getInt("Version"));
			pd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			pd.setLastMntBy(rs.getLong("LastMntBy"));
			pd.setRecordStatus(rs.getString("RecordStatus"));
			pd.setRoleCode(rs.getString("RoleCode"));
			pd.setNextRoleCode(rs.getString("NextRoleCode"));
			pd.setTaskId(rs.getString("TaskId"));
			pd.setNextTaskId(rs.getString("NextTaskId"));
			pd.setRecordType(rs.getString("RecordType"));
			pd.setWorkflowId(rs.getLong("WorkflowId"));

			if (includeData) {
				pd.setAccountNo(rs.getString("AccountNo"));
				pd.setAcType(rs.getString("AcType"));
			}

			return pd;
		});

	}

	@Override
	public List<PresentmentDetail> getPresentmenToPost(long custId, Date schData) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.CustId, fm.FinBranch, fm.FinType, pd.Id, pd.PresentmentId");
		sql.append(", pd.FinReference, pd.SchDate, pd.MandateId, pd.AdvanceAmt, pd.ExcessID");
		sql.append(", pd.PresentmentAmt, pd.ExcludeReason, pd.BounceID, pb.AccountNo, pb.AcType, pb.PartnerBankId");
		sql.append(" From PresentmentDetails pd ");
		sql.append(" Inner join PresentmentHeader ph on ph.Id = pd.PresentmentId");
		sql.append(" Inner join PartnerBanks pb on pb.PartnerBankId = ph.PartnerBankId");
		sql.append(" Inner join Financemain fm on pd.FinReference = fm.FinReference");
		sql.append(" Where fm.CustId = ? and pd.SchDate = ? and pd.Status = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, custId);
			ps.setDate(index++, JdbcUtil.getDate(schData));
			ps.setString(index++, RepayConstants.PEXC_APPROV);
		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setFinType(rs.getString("FinType"));
			pd.setId(rs.getLong("Id"));
			pd.setPresentmentId(rs.getLong("PresentmentId"));
			pd.setFinReference(rs.getString("FinReference"));
			pd.setSchDate(rs.getTimestamp("SchDate"));
			pd.setMandateId(rs.getLong("MandateId"));
			pd.setAdvanceAmt(rs.getBigDecimal("AdvanceAmt"));
			pd.setExcessID(rs.getLong("ExcessID"));
			pd.setPresentmentAmt(rs.getBigDecimal("PresentmentAmt"));
			pd.setExcludeReason(rs.getInt("ExcludeReason"));
			pd.setBounceID(rs.getLong("BounceID"));
			pd.setAccountNo(rs.getString("AccountNo"));
			pd.setAcType(rs.getString("AcType"));

			return pd;
		});

	}

	@Override
	public void updateReceptId(long id, long receiptID) {
		String sql = "Update PresentmentDetails set ReceiptId = ? Where ID = ?";

		logger.trace(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, receiptID);
			ps.setLong(2, id);
		});
	}

	@Override
	public void updatePresentmentDetails(String presentmentRef, String status, long bounceId, long manualAdviseId,
			String errorDesc) {

		StringBuffer sql = new StringBuffer();
		sql.append("Update Presentmentdetails set");
		sql.append(" Status = ?, BounceID = ?, ErrorDesc = ?, ManualAdviseId = ?");
		sql.append(" Where PresentmentRef = ?");

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, status);
			ps.setLong(index++, bounceId);
			ps.setString(index++, errorDesc);
			ps.setLong(index++, manualAdviseId);
			ps.setString(index, presentmentRef);
		});
	}

	@Override
	public void updatePresentmentDetails(String presentmentRef, String status, String errorCode, String errorDesc) {
		String sql = "Update PresentmentDetails set Status = ?, ErrorCode = ?, ErrorDesc = ? Where PresentmentRef = ?";

		jdbcOperations.update(sql, ps -> {
			int index = 1;
			ps.setString(index++, status);
			ps.setString(index++, errorCode);
			ps.setString(index++, errorDesc);
			ps.setString(index, presentmentRef);
		});
	}

	/**
	 * Method for Fetching Count for Assigned partnerBankId to Different Finances/Commitments
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

	@Override
	public List<PresentmentDetail> getPresentmensByExcludereason(long presentmentId, int excludeReason) {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" id, PresentmentId, FinReference, SchDate, MandateId, SchAmtDue, SchPriDue, SchPftDue");
		sql.append(", SchFeeDue, SchInsDue, SchPenaltyDue, AdvanceAmt, ExcessID, AdviseAmt, PresentmentAmt");
		sql.append(", TDSAmount, ExcludeReason, EmiNo, Status, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", roleCode, nextRoleCode, taskId, nextTaskId, recordType, workflowId, presentmentRef");
		sql.append(", EcsReturn, ReceiptID, ErrorCode, ErrorDesc, ManualAdviseId");
		sql.append(" From PresentmentDetails");
		sql.append(" where PresentmentId = ? and ExcludeReason = ?");

		logger.trace(Literal.SQL + sql.toString());

		PresentmentDetailRowMapper rowMapper = new PresentmentDetailRowMapper("");

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, presentmentId);
			ps.setInt(2, excludeReason);
		}, rowMapper);
	}

	@Override
	public PresentmentDetail getPresentmentDetailByFinRefAndPresID(String finReference, long presentmentId,
			String type) {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" id, PresentmentId, FinReference, SchDate, MandateId, schAmtDue, schPriDue, schPftDue");
		sql.append(", schFeeDue, schInsDue, schPenaltyDue, advanceAmt, excessID, adviseAmt, presentmentAmt");
		sql.append(", tDSAmount, excludeReason, emiNo, status, version, lastMntBy, lastMntOn, recordStatus");
		sql.append(", roleCode, nextRoleCode, taskId, nextTaskId, recordType, workflowId, presentmentRef");
		sql.append(", ecsReturn, receiptID, errorCode, errorDesc, manualAdviseId");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(", MandateType");
		}
		sql.append(" from PresentmentDetails");
		sql.append(type);
		sql.append(" where FinReference = ? and PresentmentId = ?");

		logger.trace(Literal.SQL + sql.toString());

		PresentmentDetailRowMapper rowMapper = new PresentmentDetailRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, presentmentId },
					rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Records not found in PresentmentDetails {} table/view for the specified FinReference >> {} and PresentmentId >> {}",
					type, finReference, presentmentId);
		}

		return null;
	}

	@Override
	public boolean searchIncludeList(long presentmentId, int excludereason) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select Count(*)");
		sql.append(" From PresentmentDetails");
		sql.append(" Where PresentmentId = ? and ExcludeReason = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), new Object[] { presentmentId, excludereason },
					Integer.class) > 0 ? true : false;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"There is no Include/Exlude presentments for the specified PresentmentId >> and ExcludeReason >> {}",
					presentmentId, excludereason);
		}

		return false;
	}

	@Override
	public List<Long> getExcludePresentmentDetailIdList(long presentmentId, boolean isExclude) {
		StringBuilder sql = new StringBuilder("Select ID");
		sql.append(" From PresentmentDetails");
		sql.append(" Where PresentmentId = ?");

		if (isExclude) {
			sql.append(" and ExcludeReason != ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, presentmentId);

			if (isExclude) {
				ps.setInt(index, 0);
			}

		}, (rs, rowNum) -> rs.getLong("ID"));

	}

	private class PresentmentDetailRowMapper implements RowMapper<PresentmentDetail> {
		private String type;

		private PresentmentDetailRowMapper(String type) {
			this.type = type;
		}

		@Override
		public PresentmentDetail mapRow(ResultSet rs, int arg1) throws SQLException {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setId(rs.getLong("id"));
			pd.setPresentmentId(rs.getLong("presentmentId"));
			pd.setFinReference(rs.getString("finReference"));
			pd.setSchDate(rs.getDate("schDate"));
			pd.setMandateId(rs.getLong("mandateId"));
			pd.setSchAmtDue(rs.getBigDecimal("schAmtDue"));
			pd.setSchPriDue(rs.getBigDecimal("schPriDue"));
			pd.setSchPftDue(rs.getBigDecimal("schPftDue"));
			pd.setSchFeeDue(rs.getBigDecimal("schFeeDue"));
			pd.setSchInsDue(rs.getBigDecimal("schInsDue"));
			pd.setSchPenaltyDue(rs.getBigDecimal("schPenaltyDue"));
			pd.setAdvanceAmt(rs.getBigDecimal("advanceAmt"));
			pd.setExcessID(rs.getLong("excessID"));
			pd.setAdviseAmt(rs.getBigDecimal("adviseAmt"));
			pd.setPresentmentAmt(rs.getBigDecimal("presentmentAmt"));
			pd.settDSAmount(rs.getBigDecimal("tDSAmount"));
			pd.setExcludeReason(rs.getInt("excludeReason"));
			pd.setEmiNo(rs.getInt("emiNo"));
			pd.setStatus(rs.getString("status"));
			pd.setVersion(rs.getInt("version"));
			pd.setLastMntBy(rs.getLong("lastMntBy"));
			pd.setLastMntOn(rs.getTimestamp("lastMntOn"));
			pd.setRecordStatus(rs.getString("recordStatus"));
			pd.setRoleCode(rs.getString("roleCode"));
			pd.setNextRoleCode(rs.getString("nextRoleCode"));
			pd.setTaskId(rs.getString("taskId"));
			pd.setNextTaskId(rs.getString("nextTaskId"));
			pd.setRecordType(rs.getString("recordType"));
			pd.setWorkflowId(rs.getLong("workflowId"));
			pd.setPresentmentRef(rs.getString("presentmentRef"));
			pd.setEcsReturn(rs.getString("ecsReturn"));
			pd.setReceiptID(rs.getLong("receiptID"));
			pd.setErrorCode(rs.getString("errorCode"));
			pd.setErrorDesc(rs.getString("errorDesc"));
			pd.setManualAdviseId(rs.getLong("manualAdviseId"));

			if (StringUtils.containsIgnoreCase(type, "View")) {
				pd.setMandateType(rs.getString("MandateType"));
			}

			return pd;

		}

	}

	@Override
	public void updateStatusAgainstReseipId(String status, long receiptID) {
		String sql = "Update PRESENTMENTDETAILS set Status = ? Where ReceiptId = ?";
		this.jdbcOperations.update(sql, new Object[] { status, receiptID });
	}

	@Override
	public List<PresentmentHeader> getPresentmentHeaderList(Date fromDate, Date toDate, int status) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Id, Reference, EntityCode, Schdate, BankCode, BankName, PartnerBankName");
		sql.append(", FromDate, ToDate, PresentmentDate, Status, MandateType");
		sql.append(", RecordStatus, RecordType");
		sql.append(" From PresentmentHeader_view");
		sql.append(" Where FromDate = ? and ToDate = ? and Status = ?");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("fromDate", fromDate);
		source.addValue("toDate", toDate);
		source.addValue("status", status);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setDate(1, JdbcUtil.getDate(fromDate));
			ps.setDate(2, JdbcUtil.getDate(toDate));
			ps.setInt(3, status);
		}, (rs, rowNum) -> {
			PresentmentHeader ph = new PresentmentHeader();

			ph.setId(rs.getLong("Id"));
			ph.setReference(rs.getString("Reference"));
			ph.setEntityCode(rs.getString("EntityCode"));
			ph.setSchdate(rs.getDate("Schdate"));
			ph.setBankCode(rs.getString("BankCode"));
			ph.setBankName(rs.getString("BankName"));
			ph.setPartnerBankName(rs.getString("PartnerBankName"));
			ph.setFromDate(rs.getDate("FromDate"));
			ph.setToDate(rs.getDate("ToDate"));
			ph.setPresentmentDate(rs.getDate("PresentmentDate"));
			ph.setStatus(rs.getInt("Status"));
			ph.setMandateType(rs.getString("MandateType"));
			ph.setRecordStatus(rs.getString("RecordStatus"));
			ph.setRecordType(rs.getString("RecordType"));

			return ph;
		});
	}

	@Override
	public List<Long> getIncludeList(long id) {
		StringBuilder sql = new StringBuilder("Select ID");
		sql.append(" From PresentmentDetails");
		sql.append(" where PresentmentId = ? and ExcludeReason = ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.queryForList(sql.toString(), new Object[] { id, RepayConstants.PEXC_EMIINCLUDE },
				Long.class);
	}

	@Override
	public List<Long> getExcludeList(long id) {
		StringBuilder sql = new StringBuilder("Select ID");
		sql.append(" From PresentmentDetails");
		sql.append(" where PresentmentId = ? and ExcludeReason != ? and ExcludeReason != ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.queryForList(sql.toString(),
				new Object[] { id, RepayConstants.PEXC_EMIINCLUDE, RepayConstants.PEXC_MANUAL_EXCLUDE }, Long.class);
	}

	@Override
	public boolean isPresentmentInProcess(String finReference) {
		StringBuilder sql = new StringBuilder("Select count(*)");
		sql.append(" From PresentmentDetails");
		sql.append(" Where FinReference = ? and Status = ?");

		logger.trace(Literal.SQL + sql.toString());


		return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, "A" },
				Integer.class) > 0;

	}

	@Override
	public List<PresentmentDetail> getIncludePresentments(List<Long> headerIdList) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" pd.Id, pd.PresentmentId, pd.FinReference, ph.Reference");
		sql.append(" From PresentmentDetails pd");
		sql.append(" Inner join PresentmentHeader ph on ph.id = pd.PresentmentId");
		sql.append(" Where PresentmentId in(");
		sql.append(headerIdList.stream().map(e -> "?").collect(Collectors.joining(",")));
		sql.append(") and ExcludeReason = ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (Long id : headerIdList) {
				ps.setLong(index++, id);
			}
			ps.setInt(index++, RepayConstants.PEXC_EMIINCLUDE);
		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();
			pd.setId(rs.getLong("Id"));
			pd.setPresentmentId(rs.getLong("PresentmentId"));
			pd.setFinReference(rs.getString("FinReference"));
			pd.setBatchReference(rs.getString("Reference"));
			return pd;
		});
	}

	public Presentment getPresentmentByBatchId(String batchId, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT TXN_REF, BR_CODE, CustomerId, AGREEMENTNO, CHEQUEAMOUNT, ");
		sql.append(" UMRN_NO , MICR_CODE, AccountNo, DEST_ACC_HOLDER, ACC_TYPE, ");
		sql.append(" BATCHID, IFSC, SETILMENT_DATE, CYCLEDATE");
		sql.append(" From PRESENTMENT_REQ_DETAILS");
		sql.append(type);
		sql.append(" Where BATCHID = ?");

		logger.trace(Literal.SQL + sql.toString());
		try {
			return jdbcOperations.queryForObject(sql.toString(), new Object[] { batchId }, (rs, rowNum) -> {
				Presentment presentment = new Presentment();
				presentment.setTxnReference(rs.getLong("txn_ref"));
				presentment.setBrCode(rs.getString("br_code"));
				presentment.setCustomerId(rs.getLong("customerid"));
				presentment.setAgreementNo(rs.getString("agreementno"));
				presentment.setChequeAmount(rs.getBigDecimal("chequeamount"));
				presentment.setUmrnNo(rs.getString("umrn_no"));
				presentment.setMicrCode(rs.getString("micr_code"));
				presentment.setAccountNo(rs.getString("accountno"));
				presentment.setDestAccHolder(rs.getString("dest_acc_holder"));
				presentment.setAccType(rs.getLong("acc_type"));
				presentment.setBatchId(rs.getString("batchid"));
				presentment.setIFSC(rs.getString("ifsc"));
				presentment.setSetilmentDate(rs.getDate("setilment_date"));
				presentment.setCycleDate(rs.getDate("cycledate"));
				return presentment;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records not exists in PRESENTMENT_REQ_DETAILS{} table/view for the specified BATCHID >> {} ",
					type, batchId);
		}

		return null;
	}

	@Override
	public Long getApprovedPresentmentCount(long presentmentId) {
		StringBuilder sql = new StringBuilder("Select Count(*)");
		sql.append(" From PresentmentDetails");
		sql.append(" Where PresentmentId = ? and Status = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(),
					new Object[] { presentmentId, RepayConstants.PEXC_APPROV }, Long.class);

		} catch (EmptyResultDataAccessException e) {
			logger.warn("There is no approved Presentments for the specified PresentmentId >> {}", presentmentId);
		}

		return null;
	}

	@Override
	public String getPresentmentReference(long presentmentId, String finreference) {
		StringBuilder sql = new StringBuilder("Select PresentmentRef");
		sql.append(" From PresentmentDetails");
		sql.append(" Where Id = ? and FinReference = ? and Status = ?");
		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(),
					new Object[] { presentmentId, finreference, RepayConstants.PEXC_APPROV }, String.class);

		} catch (EmptyResultDataAccessException e) {
			logger.error(
					"Record not foun in PresentmentDetails tables for the specified Id >> {}, FinReference >> {}, Status >> {}",
					presentmentId, finreference, RepayConstants.PEXC_APPROV);
		}
		return null;
	}

	@Override
	public FinanceMain getDefualtPostingDetails(String finReference, Date schDate) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select fm.FinReference, fm.CustID, fm.FinCcy, fm.FinType, fm.Finbranch");
		sql.append(", pb.AccountNo PartnerBankAc, pb.AcType PartnerBankAcType, ph.EntityCode");
		sql.append(" from FinanceMain fm");
		sql.append(" Inner join PresentmentDetails pd on pd.FinReference = fm.FinReference");
		sql.append(" Inner join PresentmentHeader ph on ph.id = pd.presentmentid");
		sql.append(" Inner join PartnerBanks pb on pb.PartnerBankId = ph.PartnerBankId");
		sql.append(" Where pd.FinReference = ? and Pd.schDate = ?");
		sql.append(" and pd.Status = ? and Pd.ExcludeReason = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(),
					new Object[] { finReference, schDate, RepayConstants.PEXC_APPROV, RepayConstants.PEXC_EMIINCLUDE },
					(rs, rowNum) -> {
						FinanceMain fm = new FinanceMain();
						fm.setFinReference(rs.getString("FinReference"));
						fm.setCustID(rs.getLong("CustID"));
						fm.setFinCcy(rs.getString("FinCcy"));
						fm.setFinType(rs.getString("FinType"));
						fm.setFinBranch(rs.getString("Finbranch"));
						fm.setPartnerBankAc(rs.getString("PartnerBankAc"));
						fm.setPartnerBankAcType(rs.getString("PartnerBankAcType"));
						fm.setEntityCode(rs.getString("EntityCode"));
						return fm;
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in FinanceMain table for the specified FinReference >> {} and SchDate >> {}",
					finReference, schDate);
		}
		return null;
	}

	@Override
	public String getPresementStatus(String presentmentRef) {
		StringBuffer sql = new StringBuffer();
		sql.append("Select Status from PresentmentDetails");
		sql.append(" Where PresentmentRef = ?  and (STATUS = ? OR STATUS = ?)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(),
					new Object[] { presentmentRef, RepayConstants.PAYMENT_APPROVE, RepayConstants.PAYMENT_FAILURE },
					String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Presentment Details not existis for the spcified PresentmentRef >> {} with status as >> {} or >> {}",
					presentmentRef, RepayConstants.PAYMENT_APPROVE, RepayConstants.PAYMENT_FAILURE);
		}

		return null;
	}

	@Override
	public PresentmentDetail getPresentmentDetail(String batchId) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Id, PresentmentId, PresentmentRef, FinReference, MandateId");
		sql.append(", SchDate, Status, PresentmentAmt, EmiNo, BounceId");
		sql.append(" FROM PresentmentDetails");
		sql.append(" Where PresentmentRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { batchId }, (rs, rowNum) -> {
				PresentmentDetail pd = new PresentmentDetail();
				pd.setId(rs.getLong("Id"));
				pd.setPresentmentId(rs.getLong("PresentmentId"));
				pd.setPresentmentRef(rs.getString("PresentmentRef"));
				pd.setFinReference(rs.getString("FinReference"));
				pd.setMandateId(rs.getLong("MandateId"));
				pd.setSchDate(rs.getDate("SchDate"));
				pd.setStatus(rs.getString("Status"));
				pd.setPresentmentAmt(rs.getBigDecimal("PresentmentAmt"));
				pd.setEmiNo(rs.getInt("emiNo"));

				return pd;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Presentment Details not existis for the spcified PresentmentRef >> {}", batchId);
		}

		return null;
	}

	@Override
	public void updatePresentmentDetail(String presentmentRef, String status) {
		StringBuffer sql = new StringBuffer("Update Presentmentdetails");
		sql.append(" set Status = ?, ErrorDesc = ?");
		sql.append(" Where PresentmentRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, status);
			ps.setString(index++, null);
			ps.setString(index++, presentmentRef);
		});
	}

	@Override
	public void updatePresentmentDetail(String presentmentRef, String status, Long linkedTranId) {
		StringBuffer sql = new StringBuffer("Update Presentmentdetails");
		sql.append(" Set Status = ?, ErrorDesc = ?, LinkedTranId = ?");
		sql.append(" Where PresentmentRef = ?");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, status);
			ps.setString(index++, null);
			ps.setLong(index++, linkedTranId);
			ps.setString(index++, presentmentRef);
		});
	}

	@Override
	public long getPresentmentId(String presentmentRef) {
		String sql = "Select PresentmentId from PresentmentDetails where PresentmentRef = ?";

		logger.trace(Literal.SQL, sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { presentmentRef }, Long.class);
		} catch (Exception e) {
			logger.warn("Record not found in PresentmentDetails table for the specified PresentmentRef >> {}",
					presentmentRef);
		}

		return Long.MIN_VALUE;
	}

}
