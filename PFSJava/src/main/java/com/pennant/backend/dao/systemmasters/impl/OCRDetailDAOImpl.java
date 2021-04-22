package com.pennant.backend.dao.systemmasters.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.systemmasters.OCRDetailDAO;
import com.pennant.backend.model.ocrmaster.OCRDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class OCRDetailDAOImpl extends SequenceDao<OCRDetail> implements OCRDetailDAO {
	private static Logger logger = LogManager.getLogger(OCRDetailDAOImpl.class);

	public OCRDetailDAOImpl() {
		super();
	}

	@Override
	public OCRDetail getOCRDetail(long headerID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select StepSequence, HeaderID, Contributor, CustomerContribution, FinancerContribution");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From OCRDETAILS");
		sql.append(type);
		sql.append(" Where DetailID = :DetailID ");

		logger.trace(Literal.SQL + sql.toString());

		OCRDetail ocrDetail = new OCRDetail();
		ocrDetail.setHeaderID(headerID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(ocrDetail);
		RowMapper<OCRDetail> rowMapper = BeanPropertyRowMapper.newInstance(OCRDetail.class);

		try {
			ocrDetail = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("OCR Details not avilable for the specified header Id {} ", headerID);
			ocrDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return ocrDetail;
	}

	@Override
	public void delete(OCRDetail ocrDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from OCRDETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" where HeaderID = :HeaderID AND DetailID = :DetailID ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(ocrDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public String save(OCRDetail ocrDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (ocrDetail.getId() == Long.MIN_VALUE || ocrDetail.getId() == 0) {
			ocrDetail.setId(getNextValue("SeqOCRDetails"));
		}

		StringBuilder sql = new StringBuilder("insert into OCRDETAILS");
		sql.append(tableType.getSuffix());
		sql.append("(StepSequence, HeaderID, Contributor, CustomerContribution, FinancerContribution, DetailID");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append("(:StepSequence, :HeaderID, :Contributor, :CustomerContribution, :FinancerContribution, :DetailID");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(ocrDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(ocrDetail.getHeaderID());
	}

	@Override
	public void update(OCRDetail ocrDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("update OCRDETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" set StepSequence = :StepSequence, Contributor = :Contributor");
		sql.append(", CustomerContribution = :CustomerContribution, FinancerContribution = :FinancerContribution");
		sql.append(", Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus = :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where HeaderID = :HeaderID AND DetailID = :DetailID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(ocrDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<OCRDetail> getOCRDetailList(long headerID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select StepSequence, HeaderID, Contributor, CustomerContribution, FinancerContribution, DetailID");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From OCRDETAILS");
		sql.append(type);
		sql.append(" Where HeaderID = :HeaderID");

		logger.trace(Literal.SQL + sql.toString());

		OCRDetail ocrDetail = new OCRDetail();
		ocrDetail.setHeaderID(headerID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(ocrDetail);
		RowMapper<OCRDetail> rowMapper = BeanPropertyRowMapper.newInstance(OCRDetail.class);

		List<OCRDetail> ocrDetails = jdbcTemplate.query(sql.toString(), paramSource, rowMapper);

		logger.debug(Literal.LEAVING);
		return ocrDetails;
	}

	@Override
	public void deleteList(OCRDetail ocrDetail, TableType type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Delete From OCRDETAILS");
		sql.append(type.getSuffix());
		sql.append(" Where HeaderID = :HeaderID ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("HeaderID", ocrDetail.getHeaderID());
		try {
			jdbcTemplate.update(sql.toString(), parameterSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(int stepSequence, long headerID, long detailID, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "StepSequence = :StepSequence and CustomerContribution = :CustomerContribution and FinancierContribution = :FinancierContribution";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("OCRDETAILS", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("OCRDETAILS_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "OCRDETAILS_Temp", "OCRDETAILS" }, whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("StepSequence", stepSequence);
		paramSource.addValue("CustomerContribution", headerID);
		paramSource.addValue("FinancierContribution", detailID); // FIXME the where condition seems wrong

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}
