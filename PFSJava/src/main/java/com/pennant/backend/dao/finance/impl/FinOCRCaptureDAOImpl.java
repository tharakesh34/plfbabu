package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.pennant.backend.dao.finance.FinOCRCaptureDAO;
import com.pennant.backend.model.finance.FinOCRCapture;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinOCRCaptureDAOImpl extends SequenceDao<FinOCRCapture> implements FinOCRCaptureDAO {
	private static final Logger logger = LogManager.getLogger(FinOCRCaptureDAOImpl.class);

	public FinOCRCaptureDAOImpl() {
		super();
	}

	@Override
	public List<FinOCRCapture> getFinOCRCaptureDetailsByRef(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, FinID, FinReference, DisbSeq, DemandAmount, PaidAmount, Remarks, ReceiptDate");
		sql.append(", FileName, DocumentRef, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinOCRCapture");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID), (rs, num) -> {
			FinOCRCapture ocr = new FinOCRCapture();

			ocr.setId(rs.getLong("ID"));
			ocr.setFinID(rs.getLong("FinID"));
			ocr.setFinReference(rs.getString("FinReference"));
			ocr.setDisbSeq(rs.getInt("DisbSeq"));
			ocr.setDemandAmount(rs.getBigDecimal("DemandAmount"));
			ocr.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			ocr.setRemarks(rs.getString("Remarks"));
			ocr.setReceiptDate(rs.getDate("ReceiptDate"));
			ocr.setFileName(rs.getString("FileName"));
			ocr.setDocumentRef(JdbcUtil.getLong(rs.getObject("DocumentRef")));
			ocr.setVersion(rs.getInt("Version"));
			ocr.setLastMntBy(rs.getLong("LastMntBy"));
			ocr.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ocr.setRecordStatus(rs.getString("RecordStatus"));
			ocr.setRoleCode(rs.getString("RoleCode"));
			ocr.setNextRoleCode(rs.getString("NextRoleCode"));
			ocr.setTaskId(rs.getString("TaskId"));
			ocr.setNextTaskId(rs.getString("NextTaskId"));
			ocr.setRecordType(rs.getString("RecordType"));
			ocr.setWorkflowId(rs.getLong("WorkflowId"));

			return ocr;
		});

	}

	@Override
	public void update(FinOCRCapture ocr, String type) {
		StringBuilder sql = new StringBuilder("Update FinOCRCapture");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set FinID = ?, FinReference = ?, DisbSeq = ?, DemandAmount = ?, PaidAmount = ?");
		sql.append(", Remarks = ?, ReceiptDate = ?, FileName = ?, DocumentRef = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ocr.getFinID());
			ps.setString(index++, ocr.getFinReference());
			ps.setInt(index++, ocr.getDisbSeq());
			ps.setBigDecimal(index++, ocr.getDemandAmount());
			ps.setBigDecimal(index++, ocr.getPaidAmount());
			ps.setString(index++, ocr.getRemarks());
			ps.setDate(index++, JdbcUtil.getDate(ocr.getReceiptDate()));
			ps.setString(index++, ocr.getFileName());
			ps.setObject(index++, ocr.getDocumentRef());
			ps.setInt(index++, ocr.getVersion());
			ps.setLong(index++, ocr.getLastMntBy());
			ps.setTimestamp(index++, ocr.getLastMntOn());
			ps.setString(index++, ocr.getRecordStatus());
			ps.setString(index++, ocr.getRoleCode());
			ps.setString(index++, ocr.getNextRoleCode());
			ps.setString(index++, ocr.getTaskId());
			ps.setString(index++, ocr.getNextTaskId());
			ps.setString(index++, ocr.getRecordType());
			ps.setLong(index++, ocr.getWorkflowId());
			ps.setLong(index, ocr.getId());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public void delete(FinOCRCapture ocr, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinOCRCapture");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, ocr.getId()));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

	}

	@Override
	public long save(FinOCRCapture ocr, String type) {
		if (ocr.getId() == Long.MIN_VALUE) {
			ocr.setId(getNextValue("SeqFinOCRCapture"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinOCRCapture");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(Id, FinID, FinReference, DisbSeq, DemandAmount, PaidAmount, Remarks, ReceiptDate");
		sql.append(", FileName, DocumentRef, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ocr.getId());
			ps.setLong(index++, ocr.getFinID());
			ps.setString(index++, ocr.getFinReference());
			ps.setInt(index++, ocr.getDisbSeq());
			ps.setBigDecimal(index++, ocr.getDemandAmount());
			ps.setBigDecimal(index++, ocr.getPaidAmount());
			ps.setString(index++, ocr.getRemarks());
			ps.setDate(index++, JdbcUtil.getDate(ocr.getReceiptDate()));
			ps.setString(index++, ocr.getFileName());
			ps.setObject(index++, ocr.getDocumentRef());
			ps.setInt(index++, ocr.getVersion());
			ps.setLong(index++, ocr.getLastMntBy());
			ps.setTimestamp(index++, ocr.getLastMntOn());
			ps.setString(index++, ocr.getRecordStatus());
			ps.setString(index++, ocr.getRoleCode());
			ps.setString(index++, ocr.getNextRoleCode());
			ps.setString(index++, ocr.getTaskId());
			ps.setString(index++, ocr.getNextTaskId());
			ps.setString(index++, ocr.getRecordType());
			ps.setLong(index, ocr.getWorkflowId());
		});

		return ocr.getId();
	}

	@Override
	public void deleteList(long finID, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinOCRCapture");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, finID);
		});
	}
}
