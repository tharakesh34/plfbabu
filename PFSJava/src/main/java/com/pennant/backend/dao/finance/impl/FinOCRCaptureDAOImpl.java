package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinOCRCaptureDAO;
import com.pennant.backend.model.finance.FinOCRCapture;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinOCRCaptureDAOImpl extends SequenceDao<FinOCRCapture> implements FinOCRCaptureDAO {
	private static final Logger logger = LogManager.getLogger(FinOCRCaptureDAOImpl.class);

	public FinOCRCaptureDAOImpl() {
		super();
	}

	@Override
	public List<FinOCRCapture> getFinOCRCaptureDetailsByRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select ID, FinReference, DisbSeq, DemandAmount, PaidAmount, Remarks, ReceiptDate");
		sql.append(", FileName, DocumentRef, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinOCRCapture");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference= :FinReference");

		logger.trace(Literal.SQL + sql.toString());
		FinOCRCapture finOCRCapture = new FinOCRCapture();
		finOCRCapture.setFinReference(finReference);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOCRCapture);
		RowMapper<FinOCRCapture> typeRowMapper = BeanPropertyRowMapper.newInstance(FinOCRCapture.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public FinOCRCapture getFinOCRCaptureDetailById(long ID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select ID, FinReference, DisbSeq, DemandAmount, PaidAmount, Remarks, ReceiptDate");
		sql.append(",FileName, DocumentRef, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinOCRCapture");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id= :Id");

		logger.trace(Literal.SQL + sql.toString());
		FinOCRCapture finOCRDetail = new FinOCRCapture();
		finOCRDetail.setId(ID);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOCRDetail);
		RowMapper<FinOCRCapture> typeRowMapper = BeanPropertyRowMapper.newInstance(FinOCRCapture.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void update(FinOCRCapture FinOCRDetail, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder();
		sql.append("Update FinOCRCapture");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set FinReference =:FinReference, DisbSeq =:DisbSeq, DemandAmount =:DemandAmount,");
		sql.append(" PaidAmount =:PaidAmount, Remarks =:Remarks, ReceiptDate =:ReceiptDate, FileName =:FileName");
		sql.append(", DocumentRef =:DocumentRef, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append("RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append("TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where Id = :Id ");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(FinOCRDetail);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(FinOCRCapture FinOCRDetail, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("Delete From FinOCRCapture");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = :Id");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(FinOCRDetail);
		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public long save(FinOCRCapture finOCRDetail, String type) {
		logger.debug(Literal.ENTERING);

		if (finOCRDetail.getId() == Long.MIN_VALUE) {
			finOCRDetail.setId(getNextValue("SeqFinOCRCapture"));
			logger.trace("get NextID:" + finOCRDetail.getId());
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into FinOCRCapture");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(Id, FinReference, DisbSeq, DemandAmount, PaidAmount, Remarks, ReceiptDate");
		sql.append(", FileName, DocumentRef, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");

		sql.append(" Values( :Id, :FinReference, :DisbSeq, :DemandAmount, :PaidAmount, :Remarks, :ReceiptDate");
		sql.append(", :FileName, :DocumentRef, :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode");
		sql.append(", :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOCRDetail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);

		return finOCRDetail.getId();
	}

	@Override
	public void deleteList(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder("Delete From FinOCRCapture");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference");
		logger.debug("deleteSql: " + sql.toString());
		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}
}
