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
 * * FileName : PresentmentDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-05-2017 * *
 * Modified Date : 01-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.financemanagement.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.financemanagement.impl.PresentmentDetailExtractService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.core.presentment.PresentmentResponseRowmapper;
import com.pennant.pff.extension.CustomerExtension;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

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

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
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
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long getSeqNumber(String tableName) {
		return getNextValue(tableName);
	}

	private void setReferences(PresentmentDetail presentment) {
		if (presentment.getId() == Long.MIN_VALUE) {
			presentment.setId(getNextValue("SeqPresentmentDetails"));
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
		sql.append(" (Id, PresentmentId, PresentmentRef, FinID, FinReference, SchDate, MandateId");
		sql.append(", SchAmtDue, SchPriDue, SchPftDue, SchFeeDue, SchInsDue, SchPenaltyDue, AdvanceAmt, ExcessID");
		sql.append(", AdviseAmt, PresentmentAmt, ExcludeReason, BounceID, EmiNo, TDSAmount, Status, ReceiptID");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				int i = 1;
				PresentmentDetail pd = presentments.get(index);

				ps.setLong(i++, pd.getId());
				ps.setLong(i++, pd.getHeaderId());
				ps.setString(i++, pd.getPresentmentRef());
				ps.setLong(i++, pd.getFinID());
				ps.setString(i++, pd.getFinReference());
				ps.setDate(i++, JdbcUtil.getDate(pd.getSchDate()));
				ps.setObject(i++, pd.getMandateId());
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
				ps.setLong(i, pd.getWorkflowId());

			}

			@Override
			public int getBatchSize() {
				return presentments.size();
			}
		}).length;
	}

	private String extactPresentmentQuery(PresentmentHeader ph) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T2.FinID, T1.FINREFERENCE, T1.SCHDATE, T1.SCHSEQ, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID");
		sql.append(", SCHDPFTPAID, DEFSCHDDATE, FEESCHD, SCHDFEEPAID, T2.MANDATEID");
		sql.append(", T1.DEFSCHDDATE, T4.MANDATETYPE, T4.EMANDATESOURCE, T4.STATUS, T4.EXPIRYDATE");
		sql.append(", T2.FINTYPE LOANTYPE, T5.BRANCHCODE, T1.TDSAMOUNT, T6.BANKCODE, T7.ENTITYCODE");
		sql.append(", T1.INSTNUMBER EMINO, T2.FINBRANCH, T1.BPIORHOLIDAY, T2.BPITREATMENT, T2.GRCADVTYPE, T2.ADVTYPE");
		sql.append(", T2.GRCPERIODENDDATE, T2.ADVSTAGE, T4.PARTNERBANKID, T1.TDSPAID, T8.DUEDATE, T2.PRODUCTCATEGORY");
		sql.append(" FROM FINSCHEDULEDETAILS T1");
		sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FinID = T2.FinID");
		sql.append(" INNER JOIN RMTFINANCETYPES T3 ON T2.FINTYPE = T3.FINTYPE");
		sql.append(" INNER JOIN MANDATES T4 ON T4.MANDATEID = T2.MANDATEID");
		sql.append(" INNER JOIN RMTBRANCHES T5 ON T5.BRANCHCODE = T2.FINBRANCH");
		sql.append(" INNER JOIN BANKBRANCHES T6 ON T4.BANKBRANCHID = T6.BANKBRANCHID");
		sql.append(" INNER JOIN SMTDIVISIONDETAIL T7 ON T7.DIVISIONCODE=T3.FINDIVISION");
		sql.append(" LEFT JOIN (SELECT FinID, FEETYPEID, SUM(PAIDAMOUNT) PAIDAMOUNT");
		sql.append(", SUM(ADVISEAMOUNT) ADVISEAMOUNT, DUEDATE");
		sql.append(" FROM MANUALADVISE GROUP BY FinID, FEETYPEID, DUEDATE)");
		sql.append(" T8 ON T3.OVERDRAFTTXNCHRGFEETYPE = T8.FEETYPEID");
		sql.append(" AND T8.FinID = T2.FinID AND T8.DUEDATE = T1.SCHDATE");
		sql.append(" WHERE (T2.FINISACTIVE = ?) AND ((T1.PROFITSCHD + T1.PRINCIPALSCHD ");
		sql.append("+ T1.FEESCHD +  COALESCE((T8.ADVISEAMOUNT), 0) - T1.SCHDPFTPAID ");
		sql.append(" - T1.SCHDPRIPAID - T1.SCHDFEEPAID -  COALESCE((T8.PAIDAMOUNT), 0)) > ?)");
		sql.append(" AND ((SCHDATE >= ? AND SCHDATE <= ? OR (DEFSCHDDATE >= ? AND DEFSCHDDATE <= ?))) ");

		if (StringUtils.trimToNull(ph.getMandateType()) != null) {
			if (InstrumentType.isEMandate(ph.getMandateType()) && StringUtils.isNotEmpty(ph.getEmandateSource())) {
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
		sql.append(" AND Not Exists( Select 1 from PresentmentDetails T6 where T1.FinID = T6.FinID ");
		sql.append(
				" AND T6.SCHDATE = T1.SCHDATE  AND (T6.ExcludeReason = '0' OR T6.ExcludeReason = '1' OR T6.ExcludeReason = '12' OR T6.ExcludeReason = '13' )) ");

		// if record is manual exclude and batch not complete approve in that case record not extracted again until
		// batch is complete approve.
		// #Bug Fix related to 135196
		sql.append(
				" AND Not Exists( Select 1 from PresentmentDetails T7 where T1.FinID = T7.FinID AND T7.SCHDATE = T1.SCHDATE ");
		sql.append(
				" AND T7.ExcludeReason = '6' AND T7.PresentmentID IN (Select ID FROM PresentmentHeader Where  Status =1 OR  Status =2 OR Status =3 )) ");
		sql.append(" AND T4.STARTDATE <= T1.SCHDATE");

		if (SysParamUtil.isAllowed(SMTParameterConstants.GROUP_BATCH_BY_BANK) && isGroupByPartnerBank(ph)) {
			sql.append(" ORDER BY T6.BANKCODE, T1.DEFSCHDDATE, T7.EntityCode, T4.PARTNERBANKID");
		} else if (SysParamUtil.isAllowed(SMTParameterConstants.GROUP_BATCH_BY_BANK)) {
			sql.append(" ORDER BY T6.BANKCODE, T1.DEFSCHDDATE, T7.EntityCode");
		} else if (isGroupByPartnerBank(ph)) {
			sql.append(" ORDER BY T1.DEFSCHDDATE, T7.EntityCode, T4.PARTNERBANKID");
		} else {
			sql.append(" ORDER BY T1.DEFSCHDDATE, T7.EntityCode");
		}
		return sql.toString();
	}

	private boolean isGroupByPartnerBank(PresentmentHeader ph) {
		return (ImplementationConstants.GROUP_BATCH_BY_PARTNERBANK && !InstrumentType.isPDC(ph.getMandateType()));

	}

	@Override
	public void extactPresentments(PresentmentHeader ph, PresentmentDetailExtractService service) {

		ph.setAppDate(SysParamUtil.getAppDate());

		String sql = extactPresentmentQuery(ph);

		jdbcOperations.query(sql, ps -> {
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

				if (InstrumentType.isEMandate(ph.getMandateType()) && StringUtils.isNotEmpty(ph.getEmandateSource())) {
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
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" T2.FinID, T1.FINREFERENCE, T1.SCHDATE, T1.SCHSEQ, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID");
		sql.append(", SCHDPFTPAID, DEFSCHDDATE, FEESCHD, SCHDFEEPAID, T2.MANDATEID, T1.DEFSCHDDATE");
		sql.append(", T4.MANDATETYPE, T4.STATUS, T4.PARTNERBANKID, T4.EXPIRYDATE, T2.FINTYPE LOANTYPE, T5.BRANCHCODE");
		sql.append(", T1.TDSAMOUNT, T6.BANKCODE, T7.ENTITYCODE, T1.INSTNUMBER EMINO, T2.FINBRANCH, T1.BPIORHOLIDAY");
		sql.append(", T2.BPITREATMENT, T1.TDSPAID, T8.DUEDATE, T2.PRODUCTCATEGORY");
		sql.append(" FROM FINSCHEDULEDETAILS T1");
		sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FinID = T2.FinID");
		sql.append(" INNER JOIN RMTFINANCETYPES T3 ON T2.FINTYPE = T3.FINTYPE");
		sql.append(" INNER JOIN MANDATES T4 ON T4.MANDATEID = T2.MANDATEID");
		sql.append(" INNER JOIN RMTBRANCHES T5 ON T5.BRANCHCODE = T2.FINBRANCH");
		sql.append(" INNER JOIN BANKBRANCHES T6 ON T4.BANKBRANCHID = T6.BANKBRANCHID");
		sql.append(" INNER JOIN SMTDIVISIONDETAIL T7 ON T7.DIVISIONCODE=T3.FINDIVISION");
		sql.append(" LEFT JOIN (SELECT FinID, FEETYPEID, SUM(PAIDAMOUNT) PAIDAMOUNT");
		sql.append(", SUM(ADVISEAMOUNT) ADVISEAMOUNT,DUEDATE");
		sql.append(" FROM MANUALADVISE GROUP BY FinID, FEETYPEID, DUEDATE) T8");
		sql.append(" ON T3.OVERDRAFTTXNCHRGFEETYPE = T8.FEETYPEID");
		sql.append(" AND T8.FinID = T2.FinID AND T8.DUEDATE = T1.SCHDATE");
		sql.append(" WHERE (T2.FINISACTIVE = ?) AND ((T1.PROFITSCHD + T1.PRINCIPALSCHD");
		sql.append(" + T1.FEESCHD + COALESCE((T8.ADVISEAMOUNT), 0) - T1.SCHDPFTPAID ");
		sql.append(
				" - T1.SCHDPRIPAID - T1.SCHDFEEPAID - COALESCE((T8.PAIDAMOUNT), 0)) > ?) AND ((SCHDATE >= ? AND SCHDATE <= ?)");
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
				" AND Exists( Select 1 from PresentmentDetails T6 where T1.FinID = T6.FinID AND T6.SCHDATE = T1.SCHDATE  AND ( T6.Status='B' OR T6.Status='F' )) ");
		sql.append(
				" AND Not Exists( Select 1 from PresentmentDetails T6 where T1.FinID = T6.FinID AND T6.SCHDATE = T1.SCHDATE ");

		// And Status not exists with I,A,S (extract,approved,success) And
		// ExcludeReason (EMIINCLUDE 0, EMIINADVANCE 1)
		sql.append(
				" AND ( T6.ExcludeReason = '0' OR T6.ExcludeReason = '1' OR T6.ExcludeReason = '12' OR T6.ExcludeReason = '13' ) AND ( T6.Status='I' OR T6.Status='S' OR T6.Status='A') ) ");

		// if record is manual exclude and batch not complete approve in
		// that case record not extracted again until batch is complete
		// approve.
		// #Bug Fix related to 135196
		sql.append(
				" AND Not Exists( Select 1 from PresentmentDetails T7 where T1.FinID = T7.FinID AND T7.SCHDATE = T1.SCHDATE ");
		sql.append(
				" AND T7.ExcludeReason = '6' AND T7.PresentmentID IN (Select ID FROM PresentmentHeader Where Status =1 OR  Status =2 OR Status =3 )) ");

		sql.append(" ORDER BY T1.DEFSCHDDATE, T6.BANKCODE ,T7.EntityCode ");

		return sql.toString();
	}

	@Override
	public void extactRePresentments(PresentmentHeader ph, PresentmentDetailExtractService service) {

		ph.setAppDate(SysParamUtil.getAppDate());

		String sql = extactRePresentmentQuery(ph);

		jdbcOperations.query(sql, ps -> {
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
				" SELECT T2.FinID, T1.FINREFERENCE, T1.SCHDATE, T1.SCHSEQ, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID, SCHDPFTPAID,");
		sql.append(
				" DEFSCHDDATE, FEESCHD, SCHDFEEPAID, T2.MANDATEID, T1.DEFSCHDDATE, T2.FINREPAYMETHOD MANDATETYPE, T8.CHEQUESTATUS STATUS,");
		sql.append(
				" T8.CHEQUEDATE, T8.CHEQUEDETAILSID, T2.FINTYPE LOANTYPE, T5.BRANCHCODE, T1.TDSAMOUNT, T6.BANKCODE, T7.ENTITYCODE,");
		sql.append(" T1.INSTNUMBER EMINO, T2.FINBRANCH, T1.BPIORHOLIDAY, T2.BPITREATMENT");
		sql.append(", T2.GRCADVTYPE, T2.ADVTYPE, T2.GRCPERIODENDDATE,T2.ADVSTAGE, T1.TDSPAID, T2.PRODUCTCATEGORY");
		sql.append(" FROM FINSCHEDULEDETAILS T1");
		sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FinID = T2.FinID");
		sql.append(" INNER JOIN RMTFINANCETYPES T3 ON T2.FINTYPE = T3.FINTYPE");
		sql.append(" INNER JOIN CHEQUEHEADER T4 ON T4.FinID = T1.FinID");
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
		sql.append(" AND Not Exists( Select 1 from PresentmentDetails T6 where T1.FinID = T6.FinID ");
		sql.append(
				" AND T6.SCHDATE = T1.SCHDATE  AND (T6.ExcludeReason = '0' OR T6.ExcludeReason = '1'  OR T6.ExcludeReason = '12' OR T6.ExcludeReason = '13' )) ");

		// if record is manual exclude and batch not complete approve in that case record not extracted again until
		// batch is complete approve.
		// #Bug Fix related to 135196
		sql.append(
				" AND Not Exists( Select 1 from PresentmentDetails T7 where T1.FinID = T7.FinID AND T7.SCHDATE = T1.SCHDATE ");
		sql.append(
				" AND T7.ExcludeReason = '6' AND T7.PresentmentID IN (Select ID FROM PresentmentHeader Where Status =1 OR  Status =2 OR Status =3 )) ");

		sql.append(" ORDER BY T1.DEFSCHDDATE, T6.BANKCODE ,T7.EntityCode ");
		return sql.toString();
	}

	@Override
	public void extactPDCPresentments(PresentmentHeader ph, PresentmentDetailExtractService service) {
		String sql = extactPDCPresentmentQuery(ph);

		jdbcOperations.query(sql, ps -> {
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
				" SELECT T2.FinID, T1.FINREFERENCE, T1.SCHDATE, T1.SCHSEQ, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID, SCHDPFTPAID,");
		sql.append(
				" DEFSCHDDATE, FEESCHD, SCHDFEEPAID, T2.MANDATEID, T1.DEFSCHDDATE, T2.FINREPAYMETHOD MANDATETYPE, T8.CHEQUESTATUS STATUS,");
		sql.append(
				" T8.CHEQUEDATE, T8.CHEQUEDETAILSID, T2.FINTYPE LOANTYPE, T5.BRANCHCODE, T1.TDSAMOUNT, T6.BANKCODE, T7.ENTITYCODE,");
		sql.append(
				" T1.INSTNUMBER EMINO, T2.FINBRANCH, T1.BPIORHOLIDAY, T2.BPITREATMENT, T1.TDSPAID, T2.PRODUCTCATEGORY");
		sql.append(" FROM FINSCHEDULEDETAILS T1");
		sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FinID = T2.FinID");
		sql.append(" INNER JOIN RMTFINANCETYPES T3 ON T2.FINTYPE = T3.FINTYPE");
		sql.append(" INNER JOIN CHEQUEHEADER T4 ON T4.FinID = T1.FinID");
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
				" AND Exists( Select 1 from PresentmentDetails T6 where T1.FinID = T6.FinID AND T6.SCHDATE = T1.SCHDATE  AND ( T6.Status='B' OR T6.Status='F' )) ");
		sql.append(
				" AND Not Exists( Select 1 from PresentmentDetails T6 where T1.FinID = T6.FinID AND T6.SCHDATE = T1.SCHDATE ");

		// And Status not exists with I,A,S (extract,approved,success) And
		// ExcludeReason (EMIINCLUDE 0, EMIINADVANCE 1)
		sql.append(
				" AND ( T6.ExcludeReason = '0' OR T6.ExcludeReason = '1' OR T6.ExcludeReason = '12' OR T6.ExcludeReason = '13' ) AND ( T6.Status='I' OR T6.Status='S' OR T6.Status='A') ) ");

		// if record is manual exclude and batch not complete approve in
		// that case record not extracted again until batch is complete
		// approve.
		// #Bug Fix related to 135196
		sql.append(
				" AND Not Exists( Select 1 from PresentmentDetails T7 where T1.FinID = T7.FinID AND T7.SCHDATE = T1.SCHDATE ");
		sql.append(
				" AND T7.ExcludeReason = '6' AND T7.PresentmentID IN (Select ID FROM PresentmentHeader Where Status =1 OR  Status =2 OR Status =3 )) ");

		sql.append(" ORDER BY T1.DEFSCHDDATE, T6.BANKCODE ,T7.EntityCode ");
		return sql.toString();
	}

	@Override
	public void extactPDCRePresentments(PresentmentHeader ph, PresentmentDetailExtractService service) {
		String sql = extactPDCRePresentmentQuery(ph);

		jdbcOperations.query(sql, ps -> {
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

			ps.setLong(index++, ph.getId());
			ps.setString(index++, ph.getReference());
			ps.setDate(index++, JdbcUtil.getDate(ph.getPresentmentDate()));
			ps.setObject(index++, ph.getPartnerBankId());
			ps.setDate(index++, JdbcUtil.getDate(ph.getFromDate()));
			ps.setDate(index++, JdbcUtil.getDate(ph.getToDate()));
			ps.setString(index++, ph.getPresentmentType());
			ps.setInt(index++, ph.getStatus());
			ps.setString(index++, ph.getMandateType());
			ps.setString(index++, ph.getEmandateSource());
			ps.setString(index++, ph.getFinBranch());
			ps.setDate(index++, JdbcUtil.getDate(ph.getSchdate()));
			ps.setString(index++, ph.getLoanType());
			ps.setLong(index++, ph.getImportStatusId());
			ps.setInt(index++, ph.getTotalRecords());
			ps.setInt(index++, ph.getProcessedRecords());
			ps.setInt(index++, ph.getSuccessRecords());
			ps.setInt(index++, ph.getFailedRecords());
			ps.setInt(index++, ph.getVersion());
			ps.setLong(index++, ph.getLastMntBy());
			ps.setTimestamp(index++, ph.getLastMntOn());
			ps.setString(index++, ph.getRecordStatus());
			ps.setString(index++, ph.getRoleCode());
			ps.setString(index++, ph.getNextRoleCode());
			ps.setString(index++, ph.getTaskId());
			ps.setString(index++, ph.getNextTaskId());
			ps.setString(index++, ph.getRecordType());
			ps.setLong(index++, ph.getWorkflowId());
			ps.setLong(index++, ph.getdBStatusId());
			ps.setString(index++, ph.getBankCode());
			ps.setString(index, ph.getEntityCode());
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
				" SELECT Id, PresentmentId, FinID, FinReference, PresentmentRef, SchDate, MandateId, SchAmtDue, schPriDue, schPftDue, schFeeDue,");
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
		logger.debug(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("PresentmentId", presentmentId);
		source.addValue("ExcludeReason", 0);
		source.addValue("IMPORTSTATUS", RepayConstants.PEXC_IMPORT);
		source.addValue("FAILEDSTATUS", RepayConstants.PEXC_FAILURE);

		RowMapper<PresentmentDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(PresentmentDetail.class);

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public int updatePresentmentDetials(long presentmentId, List<Long> list, int mnualExclude) {
		int count = 0;
		List<PresentmentDetail> presements = new ArrayList<>();

		for (Long id : list) {
			PresentmentDetail pd = new PresentmentDetail();
			pd.setId(id);
			pd.setHeaderId(presentmentId);
			pd.setExcludeReason(mnualExclude);

			presements.add(pd);

			if (presements.size() == PennantConstants.CHUNK_SIZE) {
				count = count + updatePresentmentDetials(presements);
				presements.clear();
			}
		}

		if (presements.size() > 0) {
			count = count + updatePresentmentDetials(presements);
		}

		return count;
	}

	private int updatePresentmentDetials(List<PresentmentDetail> presements) {
		StringBuilder sql = new StringBuilder("Update PresentmentDetails Set ExcludeReason = ? Where ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				PresentmentDetail item = presements.get(index);
				ps.setInt(1, item.getExcludeReason());
				ps.setLong(2, item.getId());
			}

			@Override
			public int getBatchSize() {
				return presements.size();
			}
		}).length;
	}

	@Override
	public void updatePresentmentHeader(long presentmentId, int manualExclude, Long partnerBankId) {
		String sql = " UPDATE PresentmentHeader Set Status = ?, PartnerBankId = ? Where ID = ?";
		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, manualExclude);
			ps.setLong(2, partnerBankId);
			ps.setObject(3, presentmentId);
		});
	}

	@Override
	public int updateSchdWithPresentmentId(List<PresentmentDetail> presenetments) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update FinScheduleDetails Set PresentmentId = ?");
		sql.append(" Where FinID = ? and SchDate = ? and  SchSeq = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				PresentmentDetail pd = presenetments.get(index);
				ps.setLong(1, pd.getId());
				ps.setLong(2, pd.getFinID());
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
		logger.debug(Literal.SQL + sql);

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

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			ps.setLong(1, presentmentId);
		});
	}

	@Override
	public void deletePresentmentHeader(long id) {
		String sql = "Delete from PresentmentHeader where Id = ?";

		logger.debug(Literal.SQL + sql);

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
		sql.append(" Id, PresentmentId, FinID, FinReference, SchDate, MandateId, SchAmtDue, SchPriDue, SchPftDue");
		sql.append(", SchFeeDue, SchInsDue, SchPenaltyDue, AdvanceAmt, ExcessID, AdviseAmt, PresentmentAmt");
		sql.append(", TDSAmount, ExcludeReason, EmiNo, Status, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, PresentmentRef");
		sql.append(", ECSReturn, ReceiptID, ErrorCode, ErrorDesc, ManualAdviseId");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(", MandateType");
		}
		sql.append(" From PresentmentDetails");
		sql.append(type);
		sql.append(" Where PresentmentRef = ?");

		PresentmentDetailRowMapper rowMapper = new PresentmentDetailRowMapper(type);
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, presentmentRef);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetail(long presentmentId, boolean includeData) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" pd.Id, pd.PresentmentId, pd.FinID, pd.FinReference");
		sql.append(", pd.SchDate, pd.MandateId, pd.SchAmtDue, pd.SchPriDue");
		sql.append(", pd.SchPftDue, pd.SchFeeDue, pd.SchInsDue, pd.SchPenaltyDue, pd.AdvanceAmt, pd.ExcessID");
		sql.append(", pd.AdviseAmt, pd.PresentmentAmt, pd.EmiNo, pd.Status, pd.PresentmentRef, pd.EcsReturn");
		sql.append(", pd.ReceiptID, pd.ExcludeReason, pd.Version, pd.LastMntOn, pd.LastMntBy, pd.RecordStatus");
		sql.append(", pd.RoleCode, pd.NextRoleCode, pd.TaskId, pd.NextTaskId, pd.RecordType, pd.WorkflowId");

		if (includeData) {
			sql.append(", pb.AccountNo, pb.AcType");
		}

		sql.append(" From PresentmentDetails pd");

		if (includeData) {
			sql.append(" inner join PresentmentHeader ph on ph.ID = pd.PresentmentID");
			sql.append(" inner join PartnerBanks pb on pb.PartnerBankID = ph.PartnerBankID ");
		}

		sql.append(" Where pd.PresentmentId = ?");

		if (includeData) {
			sql.append(" and (pd.ExcludeReason = ? or pd.ExcludeReason = ?) and pd.Status <> ?");
			sql.append(" and pd.Receiptid = ? ");
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, presentmentId);

			if (includeData) {
				ps.setInt(index++, RepayConstants.PEXC_EMIINCLUDE);
				ps.setInt(index++, RepayConstants.PEXC_EMIINADVANCE);
				ps.setString(index++, RepayConstants.PEXC_APPROV);
				ps.setInt(index, 0);
			}
		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setId(rs.getLong("Id"));
			pd.setHeaderId(rs.getLong("PresentmentId"));
			pd.setFinID(rs.getLong("FinID"));
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
	public List<PresentmentDetail> getPresentmenToPost(Customer customer, Date schData) {
		long custID = customer.getCustID();
		String corBankID = customer.getCustCoreBank();

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.CustId, fm.FinBranch, fm.FinType, pd.Id, pd.PresentmentId");
		sql.append(", fm.FinID, pd.FinReference, pd.SchDate, pd.MandateId, ph.MandateType, pd.AdvanceAmt, pd.ExcessID");
		sql.append(", pd.PresentmentAmt, pd.ExcludeReason, pd.BounceID, pb.AccountNo, pb.AcType, pb.PartnerBankId");
		sql.append(" From PresentmentDetails pd ");
		sql.append(" Inner join PresentmentHeader ph on ph.Id = pd.PresentmentId");
		sql.append(" Left join PartnerBanks pb on pb.PartnerBankId = ph.PartnerBankId");
		sql.append(" Inner join FinanceMain fm on pd.FinID = fm.FinID");
		sql.append(" Inner Join Customers cu on cu.CustID = fm.CustID");

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" Where fm.CustId = ? and pd.SchDate = ? and pd.Status = ?");
		} else {
			sql.append(" Where cu.CustCoreBank = ? and pd.SchDate = ? and pd.Status = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			if (CustomerExtension.CUST_CORE_BANK_ID) {
				ps.setString(index++, corBankID);
			} else {
				ps.setLong(index++, custID);
			}

			ps.setDate(index++, JdbcUtil.getDate(schData));
			ps.setString(index, RepayConstants.PEXC_APPROV);
		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setFinType(rs.getString("FinType"));
			pd.setId(rs.getLong("Id"));
			pd.setHeaderId(rs.getLong("PresentmentId"));
			pd.setFinID(rs.getLong("FinID"));
			pd.setFinReference(rs.getString("FinReference"));
			pd.setSchDate(rs.getTimestamp("SchDate"));
			pd.setMandateId(rs.getLong("MandateId"));
			pd.setInstrumentType(rs.getString("MandateType"));
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
	public void updateReceptId(PresentmentDetail pd) {
		String sql = "Update PresentmentDetails set ReceiptId = ? Where ID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, pd.getReceiptID());

			ps.setLong(index, pd.getId());
		});
	}

	/**
	 * Method for Fetching Count for Assigned partnerBankId to Different Finances/Commitments
	 */
	@Override
	public int getAssignedPartnerBankCount(long partnerBankId, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PartnerBankId", partnerBankId);

		StringBuilder selectSql = new StringBuilder(" Select Count(1) ");
		selectSql.append(" From PresentmentHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PartnerBankId = :PartnerBankId ");

		logger.debug("selectSql: " + selectSql.toString());
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public String getPaymenyMode(String presentmentRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PresentmentRef", presentmentRef);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select mandatetype from PresentmentHeader PH ");
		sql.append(" Inner Join PresentmentDetails PD ON PD.presentmentid = PH.id ");
		sql.append(" Where PD.presentmentref = :PresentmentRef ");
		logger.debug("selectSql: " + sql.toString());

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<PresentmentDetail> getPresentmensByExcludereason(long presentmentId, int excludeReason) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, PresentmentId, FinID, FinReference, SchDate, MandateId, SchAmtDue, SchPriDue, SchPftDue");
		sql.append(", SchFeeDue, SchInsDue, SchPenaltyDue, AdvanceAmt, ExcessID, AdviseAmt, PresentmentAmt");
		sql.append(", TDSAmount, ExcludeReason, EmiNo, Status, PresentmentRef");
		sql.append(", EcsReturn, ReceiptID, ErrorCode, ErrorDesc, ManualAdviseId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PresentmentDetails");
		sql.append(" where PresentmentId = ? and ExcludeReason = ?");

		logger.debug(Literal.SQL + sql.toString());

		PresentmentDetailRowMapper rowMapper = new PresentmentDetailRowMapper("");

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, presentmentId);
			ps.setInt(2, excludeReason);
		}, rowMapper);
	}

	@Override
	public PresentmentDetail getPresentmentDetailByFinRefAndPresID(long finID, long presentmentId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, PresentmentId, FinID, FinReference, SchDate, MandateId, SchAmtDue, SchPriDue, SchPftDue");
		sql.append(", SchFeeDue, SchInsDue, SchPenaltyDue, AdvanceAmt, ExcessID, AdviseAmt, PresentmentAmt");
		sql.append(", TDSAmount, ExcludeReason, EmiNo, Status, PresentmentRef");
		sql.append(", EcsReturn, ReceiptID, ErrorCode, ErrorDesc, ManualAdviseId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(", MandateType");
		}
		sql.append(" From PresentmentDetails");
		sql.append(type);
		sql.append(" where FinID = ? and PresentmentId = ?");

		logger.debug(Literal.SQL + sql.toString());

		PresentmentDetailRowMapper rowMapper = new PresentmentDetailRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID, presentmentId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean searchIncludeList(long presentmentId, int excludereason) {
		String sql = "Select Count(Id) From PresentmentDetails Where PresentmentId = ? and ExcludeReason = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, presentmentId, excludereason) > 0;
	}

	@Override
	public List<Long> getExcludePresentmentDetailIdList(long presentmentId, boolean isExclude) {
		StringBuilder sql = new StringBuilder("Select ID");
		sql.append(" From PresentmentDetails");
		sql.append(" Where PresentmentId = ?");

		if (isExclude) {
			sql.append(" and ExcludeReason != ?");
		}

		logger.debug(Literal.SQL + sql.toString());

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

			pd.setId(rs.getLong("Id"));
			pd.setHeaderId(rs.getLong("PresentmentId"));
			pd.setFinID(rs.getLong("FinID"));
			pd.setFinReference(rs.getString("FinReference"));
			pd.setSchDate(rs.getDate("SchDate"));
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
			pd.settDSAmount(rs.getBigDecimal("TDSAmount"));
			pd.setExcludeReason(rs.getInt("ExcludeReason"));
			pd.setEmiNo(rs.getInt("EmiNo"));
			pd.setStatus(rs.getString("Status"));
			pd.setPresentmentRef(rs.getString("PresentmentRef"));
			pd.setEcsReturn(rs.getString("EcsReturn"));
			pd.setReceiptID(rs.getLong("ReceiptID"));
			pd.setErrorCode(rs.getString("ErrorCode"));
			pd.setErrorDesc(rs.getString("ErrorDesc"));
			pd.setManualAdviseId(JdbcUtil.getLong(rs.getObject("ManualAdviseId")));
			pd.setVersion(rs.getInt("Version"));
			pd.setLastMntBy(rs.getLong("LastMntBy"));
			pd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			pd.setRecordStatus(rs.getString("RecordStatus"));
			pd.setRoleCode(rs.getString("RoleCode"));
			pd.setNextRoleCode(rs.getString("NextRoleCode"));
			pd.setTaskId(rs.getString("TaskId"));
			pd.setNextTaskId(rs.getString("NextTaskId"));
			pd.setRecordType(rs.getString("RecordType"));
			pd.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.containsIgnoreCase(type, "View")) {
				pd.setMandateType(rs.getString("MandateType"));
			}

			return pd;

		}

	}

	@Override
	public void updateStatusAgainstReseipId(String status, long receiptID) {
		String sql = "Update PresentmentDetails Set Status = ? Where ReceiptId = ?";
		this.jdbcOperations.update(sql, new Object[] { status, receiptID });
	}

	@Override
	public List<PresentmentHeader> getPresentmentHeaderList(Date fromDate, Date toDate, int status) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Id, Reference, EntityCode, Schdate, BankCode, BankName, PartnerBankName");
		sql.append(", FromDate, ToDate, PresentmentDate, Status, MandateType");
		sql.append(", RecordStatus, RecordType");
		sql.append(", LoanType, PartnerAcctNumber, PartnerBankId");
		sql.append(" From PresentmentHeader_view");
		sql.append(" Where FromDate = ? and ToDate = ? and Status = ?");

		logger.debug(Literal.SQL + sql.toString());

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
			ph.setLoanType(rs.getString("LoanType"));
			ph.setPartnerAcctNumber(rs.getString("PartnerAcctNumber"));
			ph.setPartnerBankId(rs.getLong("PartnerBankId"));

			return ph;
		});
	}

	@Override
	public List<Long> getIncludeList(long id) {
		String sql = "Select ID From PresentmentDetails Where PresentmentId = ? and ExcludeReason = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForList(sql, Long.class, id, RepayConstants.PEXC_EMIINCLUDE);
	}

	@Override
	public List<Long> getExcludeList(long id) {
		String sql = "Select ID From PresentmentDetails Where PresentmentId = ? and ExcludeReason != ? and ExcludeReason != ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForList(sql, Long.class, id, RepayConstants.PEXC_EMIINCLUDE,
				RepayConstants.PEXC_MANUAL_EXCLUDE);
	}

	@Override
	public boolean isPresentmentInProcess(long finID) {
		String sql = "Select count(ID) From PresentmentDetails Where FinID = ? and Status = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID, "A") > 0;

	}

	@Override
	public List<PresentmentDetail> getIncludePresentments(List<Long> headerIdList) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" pd.Id, pd.PresentmentId, pd.FinID, pd.FinReference, ph.Reference");
		sql.append(" From PresentmentDetails pd");
		sql.append(" Inner join PresentmentHeader ph on ph.id = pd.PresentmentId");
		sql.append(" Where PresentmentId in(");
		sql.append(headerIdList.stream().map(e -> "?").collect(Collectors.joining(",")));
		sql.append(") and ExcludeReason = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (Long id : headerIdList) {
				ps.setLong(index++, id);
			}
			ps.setInt(index, RepayConstants.PEXC_EMIINCLUDE);
		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();
			pd.setId(rs.getLong("Id"));
			pd.setHeaderId(rs.getLong("PresentmentId"));
			pd.setFinID(rs.getLong("FinID"));
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

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
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
				presentment.setAccType(rs.getString("acc_type"));
				presentment.setBatchId(rs.getString("batchid"));
				presentment.setIFSC(rs.getString("ifsc"));
				presentment.setSetilmentDate(rs.getDate("setilment_date"));
				presentment.setCycleDate(rs.getDate("cycledate"));
				return presentment;
			}, batchId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getDefualtPostingDetails(long finID, Date schDate) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select fm.FinID, fm.FinReference, fm.CustID, fm.FinCcy, fm.FinType, fm.Finbranch");
		sql.append(", pb.AccountNo PartnerBankAc, pb.AcType PartnerBankAcType, ph.EntityCode");
		sql.append(" from FinanceMain fm");
		sql.append(" Inner join PresentmentDetails pd on pd.FinID = fm.FinID");
		sql.append(" Inner join PresentmentHeader ph on ph.id = pd.presentmentid");
		sql.append(" Inner join PartnerBanks pb on pb.PartnerBankId = ph.PartnerBankId");
		sql.append(" Where pd.FinID = ? and Pd.schDate = ?");
		sql.append(" and pd.Status = ? and Pd.ExcludeReason = ?");

		logger.debug(Literal.SQL + sql.toString());

		Object[] parameters = new Object[] { finID, schDate, RepayConstants.PEXC_APPROV,
				RepayConstants.PEXC_EMIINCLUDE };

		try {
			return this.jdbcOperations.queryForObject(sql.toString(),

					(rs, rowNum) -> {
						FinanceMain fm = new FinanceMain();
						fm.setFinID(rs.getLong("FinID"));
						fm.setFinReference(rs.getString("FinReference"));
						fm.setCustID(rs.getLong("CustID"));
						fm.setFinCcy(rs.getString("FinCcy"));
						fm.setFinType(rs.getString("FinType"));
						fm.setFinBranch(rs.getString("Finbranch"));
						fm.setPartnerBankAc(rs.getString("PartnerBankAc"));
						fm.setPartnerBankAcType(rs.getString("PartnerBankAcType"));
						fm.setEntityCode(rs.getString("EntityCode"));
						return fm;
					}, parameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updatePresentmentDetail(long id, String status, String utrNumber) {
		String sql = "Update PresentmentDetails set Status = ?, ErrorDesc = ?, UTR_Number = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;
			ps.setString(index++, status);
			ps.setString(index++, null);
			ps.setLong(index++, id);
			ps.setString(index, utrNumber);
		});
	}

	@Override
	public void updatePresentmentDetail(long id, String status, Long linkedTranId, String utrNumber) {
		String sql = "Update PresentmentDetails Set Status = ?, ErrorDesc = ?, LinkedTranId = ?, UTR_Number = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setString(index++, status);
			ps.setString(index++, null);
			ps.setObject(index++, linkedTranId);
			ps.setLong(index++, id);
			ps.setString(index, utrNumber);
		});
	}

	@Override
	public void updatePresentmentDetail(PresentmentDetail pd) {
		StringBuilder sql = new StringBuilder("Update PresentmentDetails Set");
		sql.append(" Status = ?, ErrorCode = ?, ErrorDesc = ?, LinkedTranId = ?");
		sql.append(", BounceID = ?, ManualAdviseId = ?, UTR_Number = ?, FateCorrection = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, pd.getStatus());
			ps.setString(index++, pd.getErrorCode());
			ps.setString(index++, pd.getErrorDesc());
			ps.setObject(index++, pd.getLinkedTranId());
			ps.setObject(index++, pd.getBounceID());
			ps.setObject(index++, pd.getManualAdviseId());
			ps.setString(index++, pd.getUtrNumber());
			ps.setString(index++, pd.getFateCorrection());

			ps.setLong(index, pd.getId());
		});
	}

	@Override
	public int updateChequeStatus(long chequeDetailsId, String chequestatus) {
		String sql = "Update ChequeDetail Set Chequestatus = ? where ChequeDetailsId = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, chequestatus);
			ps.setLong(2, chequeDetailsId);
		});
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetails(long headerId, int threadId) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" PRD.BRANCH_CODE, FM.FINID, FM.FINREFERENCE, PRD.HOST_REFERENCE,  PRD.INSTALMENT_NO");
		sql.append(", PRD.AMOUNT_CLEARED, PRD.CLEARING_DATE, PRD.CLEARING_STATUS, PRD.BOUNCE_CODE, BOUNCE_REMARKS");
		sql.append(", PD.ID, PD.PRESENTMENTID, PD.MANDATEID, PH.MANDATETYPE");
		sql.append(", PD.SCHDATE, PD.SCHAMTDUE, PD.SCHPRIDUE, PD.SCHPFTDUE");
		sql.append(", PD.SCHFEEDUE, PD.SCHINSDUE, PD.SCHPENALTYDUE");
		sql.append(", PD.ADVANCEAMT, PD.EXCESSID, PD.ADVISEAMT, PD.PRESENTMENTAMT");
		sql.append(", PD.TDSAMOUNT, PD.EXCLUDEREASON, PD.EMINO, PD.STATUS, PD.PRESENTMENTREF");
		sql.append(", PD.ECSRETURN, PD.RECEIPTID, PD.ERRORCODE, PD.ERRORDESC, PD.MANUALADVISEID");
		sql.append(", FM.FINISACTIVE, FM.FINTYPE, PRD.ACCOUNT_NUMBER, PRD.UTR_Number, PRD.FateCorrection");
		sql.append(" FROM Presentment_Resp_Dtls PRD");
		sql.append(" INNER JOIN PresentmentDetails PD ON PD.PRESENTMENTREF = PRD.PRESENTMENT_REFERENCE");
		sql.append(" INNER JOIN PresentmentHeader PH ON PH.ID = PD.PRESENTMENTID");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINID = PD.FINID");
		sql.append(" INNER JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = PH.PARTNERBANKID");
		sql.append(" where PRD.HEADER_ID = ? AND THREAD_ID = ?");

		logger.debug(Literal.SQL + sql);

		PresentmentResponseRowmapper rowMapper = new PresentmentResponseRowmapper();

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, headerId);
			ps.setInt(2, threadId);
		}, rowMapper);
	}

	@Override
	public long logHeader(String fileName, String entityCode, String event, int totalRecords) {
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO Presentment_Resp_Header");
		sql.append(" (BATCH_NAME, ENTITY_CODE, EVENT, TOTAL_RECORDS, START_TIME)");
		sql.append(" VALUES(?, ?, ?, ?, ? )");

		logger.debug(Literal.SQL + sql.toString());

		KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 1;

					ps.setString(index++, fileName);
					ps.setString(index++, entityCode);
					ps.setString(index++, event);
					ps.setInt(index++, totalRecords);
					ps.setTimestamp(index, curTimeStamp);

					return ps;
				}
			}, keyHolder);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return keyHolder.getKey().longValue();
	}

	@Override
	public void updateHeader(long headerId, long deExecutionId, int totalRecords, int successRecords, int failedRecords,
			String status, String remarks) {

		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());

		StringBuilder sql = new StringBuilder();

		sql.append("UPDATE Presentment_Resp_Header SET");
		sql.append(" DE_EXECUTION_ID = ?, TOTAL_RECORDS = ?, SUCESS_RECORDS = ?, FAILURE_RECORDS = ?,");
		sql.append(" STATUS = ?, REMARKS = ?, END_TIME = ? WHERE ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, deExecutionId);
			ps.setInt(index++, totalRecords);
			ps.setInt(index++, successRecords);
			ps.setInt(index++, failedRecords);
			ps.setString(index++, status);
			ps.setString(index++, remarks);
			ps.setTimestamp(index++, curTimeStamp);
			ps.setLong(index, headerId);

		});
	}

	@Override
	public int getMinIDByHeaderID(long headerId) {
		String sql = "select COALESCE(min(ID), 0) from Presentment_Resp_Dtls where Header_Id = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, Integer.class, headerId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public int getMaxIDByHeaderID(long headerId) {
		String sql = "select COALESCE(max(ID), 0) from Presentment_Resp_Dtls where Header_Id = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, Integer.class, headerId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public int logRespDetails(long importHeaderId, long headerId) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO Presentment_Resp_Dtls (");
		sql.append("HEADER_ID, PRESENTMENT_REFERENCE, FINREFERENCE, HOST_REFERENCE, INSTALMENT_NO, AMOUNT_CLEARED");
		sql.append(", CLEARING_DATE, CLEARING_STATUS, BOUNCE_CODE, BOUNCE_REMARKS, REASON_CODE, BANK_CODE, BANK_NAME");
		sql.append(", BRANCH_CODE, BRANCH_NAME, PARTNER_BANK_CODE, PARTNER_BANK_NAME");
		sql.append(", BANK_ADDRESS, ACCOUNT_NUMBER, IFSC_CODE, UMRN_NO, MICR_CODE, CHEQUE_SERIAL_NO");
		sql.append(", CORPORATE_USER_NO, CORPORATE_USER_NAME, DEST_ACC_HOLDER, DEBIT_CREDIT_FLAG, UTR_Number");
		sql.append(", FateCorrection)");
		sql.append(" SELECT ?, BatchID, AgreementNo, BFLReferenceNo, InstalmentNo, AmountCleared, ClearingDate");
		sql.append(", Status, ReasonCode, Bounce_Remarks, ReasonCode, NULL, BANK_NAME");
		sql.append(", BranchCode, BranchCode, Partner_Bank, Partner_Bank");
		sql.append(", Bank_Address, AccNumber, IFSC, UMRN_NO, MICR_CODE, ChequeSerialNo");
		sql.append(", Corporate_User_No, Corporate_Name, Dest_Acc_holder, Debit_Credit_Flag, UTR_Number");
		sql.append(", FateCorrection");
		sql.append(" from Presentment_FileImport WHERE HEADER_ID = ?");

		return jdbcOperations.update(sql.toString(), ps -> {
			ps.setLong(1, headerId);
			ps.setLong(2, importHeaderId);
		});
	}

	@Override
	public int updateThreadID(long headerId, long from, long to, int thread) {
		StringBuilder sql = new StringBuilder();

		sql.append("UPDATE Presentment_Resp_Dtls SET THREAD_ID = ?");
		sql.append(" WHERE ID >= ? AND ID <= ? AND HEADER_ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setInt(index++, thread);
			ps.setLong(index++, from);
			ps.setLong(index++, to);
			ps.setLong(index, headerId);

		});
	}

	@Override
	public List<Integer> getThreads(long headerId) {
		String sql = "SELECT DISTINCT THREAD_ID FROM Presentment_Resp_Dtls WHERE HEADER_ID = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForList(sql, Integer.class, headerId);
	}

	@Override
	public void logRespDetailError(long headerId, long detailId, String errorCode, String errorDesc) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO PRESENTMENT_RESP_DTLS_ERRORS");
		sql.append("(HEADER_ID, DETAIL_ID, ERROR_CODE, ERROR_DESCRIPTION)");
		sql.append(" VALUES(?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 1;

					ps.setLong(index++, headerId);
					ps.setLong(index++, detailId);
					ps.setString(index++, errorCode);
					ps.setString(index, errorDesc);

					return ps;
				}
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void updateDataEngineLog(long id, String presentmentRef, String errorCode, String errorDesc) {
		boolean rcdInserted = true;

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO DATA_ENGINE_LOG");
		sql.append(" (StatusId, KeyId, Status, Reason)");
		sql.append(" VALUES(?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 1;

					ps.setLong(index++, id);
					ps.setString(index++, presentmentRef);
					ps.setString(index++, errorCode);
					ps.setString(index, errorDesc);

					return ps;
				}
			});
		} catch (DuplicateKeyException e) {
			logger.warn(Message.RECORD_EXISTS);
			rcdInserted = false;
		}

		if (!rcdInserted) {
			StringBuilder updSql = new StringBuilder("UPDATE DATA_ENGINE_LOG");
			updSql.append(" SET STATUS = ?, REASON = ? WHERE STATUSID = ? AND KEYID = ?");

			logger.debug(Literal.SQL + updSql.toString());

			jdbcOperations.update(updSql.toString(), ps -> {
				int index = 1;
				ps.setString(index++, errorCode);
				ps.setString(index++, errorDesc);
				ps.setLong(index++, id);
				ps.setString(index, presentmentRef);

			});
		}
	}

	@Override
	public List<DataEngineLog> getDEExceptions(long id) {
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" Id, StatusId, KeyId, Status, Reason from DATA_ENGINE_LOG ");
		sql.append(" where StatusId = ?");

		logger.debug(Literal.SQL + sql.toString());
		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {

			DataEngineLog de = new DataEngineLog();
			de.setId(rs.getLong("Id"));
			de.setStatusId(rs.getLong("StatusId"));
			de.setKeyId(rs.getString("KeyId"));
			de.setStatus(rs.getString("Status"));
			de.setReason(rs.getString("Reason"));

			return de;
		}, id);
	}

	@Override
	public int logRespDetailsLog(long headerId) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO PRESENTMENT_RESP_DTLS_LOG");
		sql.append("(HEADER_ID, PRESENTMENT_REFERENCE, FINREFERENCE, HOST_REFERENCE, INSTALMENT_NO, AMOUNT_CLEARED");
		sql.append(", CLEARING_DATE, CLEARING_STATUS, BOUNCE_CODE, BOUNCE_REMARKS, REASON_CODE, BANK_CODE, BANK_NAME");
		sql.append(", BRANCH_CODE, BRANCH_NAME, PARTNER_BANK_CODE, PARTNER_BANK_NAME");
		sql.append(", BANK_ADDRESS, ACCOUNT_NUMBER, IFSC_CODE, UMRN_NO, MICR_CODE, CHEQUE_SERIAL_NO");
		sql.append(", CORPORATE_USER_NO, CORPORATE_USER_NAME, DEST_ACC_HOLDER, DEBIT_CREDIT_FLAG, PROCESS_FLAG");
		sql.append(", THREAD_ID, UTR_Number)");
		sql.append(" SELECT HEADER_ID, PRESENTMENT_REFERENCE, FINREFERENCE, HOST_REFERENCE, INSTALMENT_NO ");
		sql.append(", AMOUNT_CLEARED, CLEARING_DATE, CLEARING_STATUS, BOUNCE_CODE, BOUNCE_REMARKS, REASON_CODE");
		sql.append(", BANK_CODE, BANK_NAME, BRANCH_CODE, BRANCH_NAME, PARTNER_BANK_CODE, PARTNER_BANK_NAME");
		sql.append(", BANK_ADDRESS, ACCOUNT_NUMBER, IFSC_CODE, UMRN_NO, MICR_CODE, CHEQUE_SERIAL_NO");
		sql.append(", CORPORATE_USER_NO, CORPORATE_USER_NAME, DEST_ACC_HOLDER, DEBIT_CREDIT_FLAG , PROCESS_FLAG");
		sql.append(", THREAD_ID, UTR_Number from Presentment_Resp_Dtls Where HEADER_ID = ? ");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.update(sql.toString(), ps -> {
			ps.setLong(1, headerId);
		});
	}

	@Override
	public List<Long> getPresentmentHeaderIdsByHeaderId(long headerId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT PH.ID FROM Presentment_Resp_Dtls PRD");
		sql.append(" INNER JOIN PresentmentDetails PD ON PD.PRESENTMENTREF = PRD.PRESENTMENT_REFERENCE");
		sql.append(" INNER JOIN PresentmentHeader PH ON PH.ID = PD.PRESENTMENTID");
		sql.append(" WHERE PRD.HEADER_ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.queryForList(sql.toString(), Long.class, headerId);
	}

	@Override
	public List<String> getStatusListByHeader(Long id) {
		String sql = "Select Status From PresentmentDetails Where PresentmentID = ? and ExcludeReason = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForList(sql, String.class, id, 0);
	}

	@Override
	public void updateHeaderCounts(Long id, int successCount, int failedCount) {
		String sql = "UPDATE PresentmentHeader SET SUCCESSRECORDS = ?, FAILEDRECORDS = ? WHERE ID = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			ps.setInt(1, successCount);
			ps.setInt(2, failedCount);
			ps.setLong(3, id);
		});

	}

	@Override
	public void updateHeaderStatus(Long id, int status) {
		String sql = "UPDATE PresentmentHeader SET STATUS = ? WHERE ID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;
			ps.setInt(index++, status);
			ps.setLong(index, id);
		});
	}

	@Override
	public void truncate(String tableName) {
		String sql = "TRUNCATE TABLE " + tableName;
		logger.debug(Literal.SQL + sql);
		this.jdbcOperations.execute(sql);
	}

	@Override
	public void deleteByHeaderId(long headerId) {
		String sql = "DELETE FROM Presentment_FileImport WHERE HEADER_ID = ?";
		logger.debug(Literal.SQL + sql);
		this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, headerId);
		});
	}

	@Override
	public List<String> getUnProcessedPrentmntRef(long headerId) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Presentment_Reference From Presentment_Resp_Dtls Where Presentment_Reference not in ");
		sql.append("(Select PresentmentRef From PresentmentDetails) and Header_Id = ? ");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, headerId);
		}, (rs, rowNum) -> {
			return rs.getString("Presentment_Reference");
		});

	}

	@Override
	public List<PresentmentDetail> getPresentmentStatusByFinRef(long finID) {
		String sql = "Select Status, PresentmentId, ID FROM PresentmentDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, ps -> {
			ps.setLong(1, finID);
		}, (rs, i) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setStatus(rs.getString("Status"));
			pd.setHeaderId(rs.getLong("PresentmentId"));
			pd.setId(rs.getLong("ID"));

			return pd;
		});
	}

	@Override
	public void logRequest(long headerId, Presentment presentment) {
		StringBuilder sql = new StringBuilder("INSERT INTO Presentment_FileImport");
		sql.append("(HEADER_ID, BRANCHCODE, AGREEMENTNO, INSTALMENTNO, BFLREFERENCENO, BATCHID, AMOUNTCLEARED");
		sql.append(", CLEARINGDATE, STATUS, REASONCODE, UMRN_NO");
		sql.append(") VALUES (");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, headerId);
			ps.setString(index++, presentment.getBrCode());
			ps.setString(index++, presentment.getAgreementNo());
			ps.setString(index++, "0");
			ps.setString(index++, presentment.getBrCode());
			ps.setString(index++, presentment.getBatchId());
			ps.setBigDecimal(index++, presentment.getChequeAmount());
			ps.setDate(index++, JdbcUtil.getDate(presentment.getSetilmentDate()));
			ps.setString(index++, presentment.getStatus());
			ps.setString(index++, presentment.getReturnReason());
			ps.setString(index, presentment.getUmrnNo());
		});

	}

	@Override
	public Long getApprovedPresentmentCount(long presentmentId) {
		StringBuilder sql = new StringBuilder("Select Count(ID)");
		sql.append(" From PresentmentDetails");
		sql.append(" Where PresentmentId = ? and Status = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Long.class, presentmentId,
				RepayConstants.PEXC_APPROV);
	}

	@Override
	public PresentmentDetail getPresentmentByRef(String presentmentRef) {
		String sql = "Select FinID, FinReference, Status, ErrorCode, ErrorDesc from PresentmentDetails Where PresentmentRef = ?";

		try {
			return jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				PresentmentDetail pd = new PresentmentDetail();

				pd.setFinID(rs.getLong("FinID"));
				pd.setFinReference(rs.getString("FinReference"));
				pd.setStatus(rs.getString("Status"));
				pd.setErrorCode(rs.getString("ErrorCode"));
				pd.setErrorDesc(rs.getString("ErrorDesc"));

				return pd;
			}, presentmentRef);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public PresentmentDetail getPresentmentById(long presentmentId) {
		String sql = "Select FinID, FinReference, Status, ErrorCode, ErrorDesc, PresentmentRef from PresentmentDetails Where ID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				PresentmentDetail pd = new PresentmentDetail();

				pd.setFinID(rs.getLong("FinID"));
				pd.setFinReference(rs.getString("FinReference"));
				pd.setStatus(rs.getString("Status"));
				pd.setErrorCode(rs.getString("ErrorCode"));
				pd.setErrorDesc(rs.getString("ErrorDesc"));
				pd.setPresentmentRef(rs.getString("PresentmentRef"));

				return pd;
			}, presentmentId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public boolean isFileProcessed(String fileName) {
		String sql = "Select count(1) from Presentment_Resp_Header Where Batch_Name= ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, fileName) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public String getPresentmentType(long id) {
		String sql = "Select presentmenttype from PresentmentHeader Where ID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<Long> getManualExcludeList(long id) {
		String sql = "Select ID From PresentmentDetails Where PresentmentId = ? and ExcludeReason  = ?";

		logger.trace(Literal.SQL + sql);

		return jdbcOperations.query(sql, ps -> {
			ps.setLong(1, id);
			ps.setInt(2, RepayConstants.PEXC_MANUAL_EXCLUDE);
		}, (rs, i) -> {
			return JdbcUtil.getLong(rs.getObject(1));
		});
	}

	@Override
	public PresentmentDetail getRePresentmentDetails(String presentmentRef) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ph.PresentmentType, fh.ReceiptDate");
		sql.append(" From PresentmentDetails pd");
		sql.append(" Inner Join PresentmentHeader ph on ph.ID = pd.PresentmentID");
		sql.append(" Inner Join FinReceiptHeader fh on fh.ReceiptID = pd.ReceiptID");
		sql.append(" Where pd.PresentmentRef = ? and ph.PresentmentType = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				PresentmentDetail pd = new PresentmentDetail();
				pd.setPresentmentType(rs.getString("PresentmentType"));
				pd.setRepresentmentDate(rs.getDate("ReceiptDate"));
				return pd;
			}, presentmentRef, PennantConstants.PROCESS_REPRESENTMENT);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getApprovedPresentmentCount(String finReference) {
		String sql = "Select Count(FinReference) From PresentmentDetails Where FinReference = ? and Status = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finReference, RepayConstants.PEXC_APPROV);
	}

	@Override
	public void updateReceptIdAndAmounts(PresentmentDetail pd) {
		String sql = "Update PresentmentDetails set ReceiptId = ?, PresentmentAmt = ?, Lppamount = ?, BounceAmount = ? Where ID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, pd.getReceiptID());
			ps.setBigDecimal(index++, pd.getPresentmentAmt());
			ps.setBigDecimal(index++, pd.getLppAmount());
			ps.setBigDecimal(index++, pd.getBounceAmount());

			ps.setLong(index, pd.getId());
		});
	}

	@Override
	public void updateProgess(long headerID, int progress) {
		String sql = "Update PRESENTMENT_RESP_HEADER set PROGRESS = ? Where ID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, progress);
			ps.setLong(2, headerID);
		});

	}

	@Override
	public PresentmentDetail getRePresentmentDetail(String finReference, Date SchDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, PresentmentId, FinID, FinReference, PresentmentRef, SchDate, MandateId, SchAmtDue");
		sql.append(", SchPriDue, SchPftDue, SchFeeDue, SchInsDue, SchPenaltyDue");
		sql.append(", AdvanceAmt, ExcessID, AdviseAmt, PresentmentAmt, fm.FinisActive");
		sql.append(", TDSAmount, ExcludeReason, BounceID, EmiNo, Status, ErrorCode, ErrorDesc");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PresentmentDetails pd");
		sql.append(" Inner Join FinanceMain fm on fm.FinId = pd.FinID");
		sql.append(" Where fm.FinReference = ? and pd.SchdDate <= ?, fm.FinIsActive = ?, pd.Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				PresentmentDetail pd = new PresentmentDetail();

				pd.setId(rs.getLong("Id"));
				pd.setHeaderId(rs.getLong("PresentmentId"));
				pd.setFinID(rs.getLong("FinID"));
				pd.setFinReference(rs.getString("FinReference"));
				pd.setPresentmentRef(rs.getString("PresentmentRef"));
				pd.setSchDate(rs.getDate("SchDate"));
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
				pd.setFinisActive(rs.getBoolean("FinisActive"));
				pd.settDSAmount(rs.getBigDecimal("TDSAmount"));
				pd.setExcludeReason(rs.getInt("ExcludeReason"));
				pd.setBounceID(rs.getLong("BounceID"));
				pd.setEmiNo(rs.getInt("EmiNo"));
				pd.setStatus(rs.getString("Status"));
				pd.setErrorCode(rs.getString("ErrorCode"));
				pd.setErrorDesc(rs.getString("ErrorDesc"));
				pd.setVersion(rs.getInt("Version"));
				pd.setLastMntBy(rs.getLong("LastMntBy"));
				pd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				pd.setRecordStatus(rs.getString("RecordStatus"));
				pd.setRoleCode(rs.getString("RoleCode"));
				pd.setNextRoleCode(rs.getString("NextRoleCode"));
				pd.setTaskId(rs.getString("TaskId"));
				pd.setNextTaskId(rs.getString("NextTaskId"));
				pd.setRecordType(rs.getString("RecordType"));
				pd.setWorkflowId(rs.getLong("WorkflowId"));

				return pd;
			}, finReference, SchDate, 1, "B");
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}

	}

	@Override
	public String getBackOfficeNameByBranchCode(String branchCode) {
		String sql = "Select Name From Clusters Where Id In (Select ClusterId from RMTBranches where BranchCode = ?)";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, branchCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}

	}

	@Override
	public Long getLatestMandateId(long finID) {
		String sql = "Select pd.ID, pd.MandateID From PresentmentHeader ph Inner Join PresentmentDetails pd on pd.PresentmentID = ph.ID Where pd.FinID = ? and ph.MandateType in (?, ?, ?, ?) and pd.Status not in (?, ?) and  pd.ExcludeReason in (?, ?)";

		logger.debug(Literal.SQL.concat(sql));

		List<PresentmentDetail> list = this.jdbcOperations.query(sql, ps -> {
			int index = 0;

			ps.setLong(++index, finID);
			ps.setString(++index, InstrumentType.NACH.code());
			ps.setString(++index, InstrumentType.SI.code());
			ps.setString(++index, InstrumentType.EMANDATE.name());
			ps.setString(++index, InstrumentType.PDC.code());
			ps.setString(++index, "I");
			ps.setString(++index, "F");
			ps.setLong(++index, RepayConstants.PEXC_EMIINCLUDE);
			ps.setLong(++index, RepayConstants.PEXC_MANUAL_EXCLUDE);
		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setId(rs.getLong("ID"));
			pd.setMandateId(rs.getLong("MandateID"));

			return pd;
		});

		list = list.stream().sorted((l1, l2) -> Long.valueOf(l2.getId()).compareTo(Long.valueOf(l1.getId())))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list.get(0).getMandateId();
	}

}
