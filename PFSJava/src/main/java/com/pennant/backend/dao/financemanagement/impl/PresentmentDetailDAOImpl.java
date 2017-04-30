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
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pff.core.ConcurrencyException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;

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
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public long getSeqNumber(String tableName) {
		return getNextidviewDAO().getNextId(tableName);
	}

	@Override
	public String save(PresentmentDetail detail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (detail.getId() == Long.MIN_VALUE) {
			detail.setId(getNextidviewDAO().getNextId("SEQPRESENTMENTDETAILS"));
		}
		if (detail.getPresentmentRef() != null) {
			String reference = detail.getPresentmentRef();
			String presentmentRef = StringUtils.leftPad(String.valueOf(detail.getId()), 29 - reference.length(), "0");
			detail.setPresentmentRef(reference.concat(presentmentRef));
		}
	
		StringBuilder sql = new StringBuilder();
		sql.append(" Insert into PresentmentDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, PresentmentId, PresentmentRef, FinReference, SchDate, MandateId, SchAmtDue, schPriDue, schPftDue, schFeeDue, schInsDue,");
		sql.append(" schPenaltyDue, advanceAmt, excessID, adviseAmt, presentmentAmt, ExcludeReason, bounceID, emiNo, auxiliary1, auxiliary2, status,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :Id, :PresentmentId, :PresentmentRef, :FinReference, :SchDate, :MandateId, :SchAmtDue, :schPriDue, :schPftDue, :schFeeDue, :schInsDue,");
		sql.append(" :schPenaltyDue, :advanceAmt, :excessID, :adviseAmt, :presentmentAmt, :ExcludeReason, :bounceID, :emiNo, :auxiliary1, :auxiliary2, :status,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(detail);

		try {
			this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public ResultSet getPresentmentDetails(PresentmentHeader detailHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		int index = 0;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT T1.FINREFERENCE, T1.SCHDATE, PROFITSCHD, PRINCIPALSCHD, SCHDPRIPAID, SCHDPFTPAID, DEFSCHDDATE,");
			sql.append(" FEESCHD, SCHDFEEPAID, INSSCHD, T2.MANDATEID, T1.DEFSCHDDATE, T4.MANDATETYPE, T4.STATUS,");
			sql.append(" T4.EXPIRYDATE, T2.FINTYPE LOANTYPE, T5.BRANCHCODE,  ");
			sql.append(" T1.INSTNUMBER EMINO, T2.FINBRANCH  FROM FINSCHEDULEDETAILS T1");
			sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FINREFERENCE = T2.FINREFERENCE");
			sql.append(" INNER JOIN RMTFINANCETYPES T3 ON T2.FINTYPE = T3.FINTYPE");
			sql.append(" INNER JOIN MANDATES T4 ON T4.MANDATEID = T2.MANDATEID");
			sql.append(" INNER JOIN RMTBRANCHES T5 ON T5.BRANCHCODE = T2.FINBRANCH");
			sql.append(" WHERE (REPAYONSCHDATE = ?) AND ((SCHDATE >= ? AND SCHDATE <= ?)");
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

			sql.append(" AND  Not Exists( Select 1 from PresentmentDetails T6 where T1.FinReference = T6.FinReference ");
			sql.append(" AND T6.ExcludeReason = '0')  ORDER BY T1.SCHDATE ");

			Connection conn = DataSourceUtils.doGetConnection(this.dataSource);
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, "1");
			stmt.setDate(2, getDate(detailHeader.getFromDate()));
			stmt.setDate(3, getDate(detailHeader.getToDate()));
			stmt.setDate(4, getDate(detailHeader.getFromDate()));
			stmt.setDate(5, getDate(detailHeader.getToDate()));
			index = 5;
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

			logger.trace(Literal.SQL + sql.toString());

			rs = stmt.executeQuery();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			sql = null;
		}
		logger.debug(Literal.LEAVING);
		return rs;
	}

	private java.sql.Date getDate(Date date) {
		return DateUtility.getDate(DateUtility.formateDate(date, PennantConstants.DBDateFormat),
				PennantConstants.DBDateFormat);
	}

	@Override
	public long savePresentmentHeader(PresentmentHeader presentmentHeader) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" Insert into PresentmentHeader");
		sql.append(" (Id, Reference, PresentmentDate, PartnerBankId, FromDate, ToDate, ");
		sql.append("  Status, MandateType, FinBranch, SearchField1, SearchField2, SearchField3, SchDate,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :Id, :Reference, :PresentmentDate, :PartnerBankId, :FromDate, :ToDate, ");
		sql.append(" :Status, :MandateType, :FinBranch, :SearchField1, :SearchField2, :SearchField3, :SchDate,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(presentmentHeader);
		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return presentmentHeader.getId();
	}

	@Override
	public void updatePresentmentDetailId(long presentmentId, List<Long> detaildList) throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append("Update PRESENTMENTDETAILS set PRESENTMENTID = :PRESENTMENTID Where DETAILID  IN(:DetaildList) ");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("PRESENTMENTID", presentmentId);
		source.addValue("DetaildList", detaildList);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updatePresentmentDetailId(long presentmentId, long extractId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" update PRESENTMENTDETAILHEADER Set BATCHID = :BATCHID Where EXTRACTID = :EXTRACTID ");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("BATCHID", extractId);
		source.addValue("EXTRACTID", presentmentId);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" SELECT Id, PresentmentId, FinReference, SchDate, MandateId, SchAmtDue, schPriDue, schPftDue, schFeeDue, schInsDue,");
		sql.append(" schPenaltyDue, advanceAmt, excessID, adviseAmt, presentmentAmt, ExcludeReason, bounceID, emiNo, auxiliary1, auxiliary2, status,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		if (type.contains("View")) {
			sql.append(", mandateType, finTypeDesc, customerName ");
		}
		sql.append(" From PresentmentDetails");
		sql.append(type);
		sql.append(" Where PresentmentId = :PresentmentId ");
		if (isExclude) {
			sql.append("AND ExcludeReason = :ExcludeReason ");
		} else {
			sql.append("AND ExcludeReason <> :ExcludeReason ");
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("PresentmentId", presentmentId);
		source.addValue("ExcludeReason", 0);
		try {
			RowMapper<PresentmentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PresentmentDetail.class);
			return this.namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
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
		sql.append(" UPDATE PRESENTMENTDETAILS Set EXCLUDEREASON = :EXCLUDEREASON Where PRESENTMENTID = :PRESENTMENTID AND  ID in (:ID)");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("EXCLUDEREASON", mnualExclude);
		source.addValue("PRESENTMENTID", presentmentId);
		source.addValue("ID", list);

		try {
			this.namedParameterJdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updatePresentmentHeader(long presentmentId, int manualExclude,long partnerBankId) {
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
			this.namedParameterJdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}
}
