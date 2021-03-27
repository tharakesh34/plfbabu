package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinOCRHeaderDAO;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinOCRHeaderDAOImpl extends SequenceDao<FinOCRHeader> implements FinOCRHeaderDAO {
	private static Logger logger = LogManager.getLogger(FinOCRHeaderDAOImpl.class);

	public FinOCRHeaderDAOImpl() {
		super();
	}

	@Override
	public FinOCRHeader getFinOCRHeaderByRef(String reference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select HeaderID, OcrID, OcrDescription, CustomerPortion, OcrType, TotalDemand,");
		sql.append("FinReference");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinOCRHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference= :FinReference");

		logger.trace(Literal.SQL + sql.toString());
		FinOCRHeader finOCRHeader = new FinOCRHeader();
		finOCRHeader.setFinReference(reference);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOCRHeader);
		RowMapper<FinOCRHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(FinOCRHeader.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public FinOCRHeader getFinOCRHeaderById(long headerID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select HeaderID, OcrID, OcrDescription, CustomerPortion, OcrType, TotalDemand,");
		sql.append("FinReference");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinOCRHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderID= :HeaderID");

		logger.trace(Literal.SQL + sql.toString());
		FinOCRHeader finOCRHeader = new FinOCRHeader();
		finOCRHeader.setHeaderID(headerID);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOCRHeader);
		RowMapper<FinOCRHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(FinOCRHeader.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void update(FinOCRHeader finOCRHeader, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder();
		sql.append("Update FinOCRHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set OcrID =:OcrID, OcrDescription =:OcrDescription,");
		sql.append("OcrType =:OcrType ,TotalDemand=:TotalDemand,");
		sql.append("FinReference =:FinReference, CustomerPortion =:CustomerPortion,");
		sql.append("Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append("RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append("TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where HeaderID = :HeaderID ");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOCRHeader);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(FinOCRHeader finOCRHeader, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("Delete From FinOCRHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderID = :HeaderID");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOCRHeader);
		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public long save(FinOCRHeader finOCRHeader, String type) {
		logger.debug(Literal.ENTERING);

		if (finOCRHeader.getHeaderID() == Long.MIN_VALUE) {
			finOCRHeader.setHeaderID(getNextValue("SeqFinOCRHeader"));
			logger.trace("get NextID:" + finOCRHeader.getHeaderID());
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into FinOCRHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(HeaderID, OcrID, OcrDescription, CustomerPortion, OcrType, TotalDemand,");
		sql.append(" FinReference,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");

		sql.append(" Values( :HeaderID, :OcrID, :OcrDescription, :CustomerPortion, :OcrType, :TotalDemand,");
		sql.append(" :FinReference,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOCRHeader);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);

		return finOCRHeader.getHeaderID();
	}

}
