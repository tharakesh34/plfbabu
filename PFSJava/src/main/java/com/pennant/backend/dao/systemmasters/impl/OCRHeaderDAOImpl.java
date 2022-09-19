package com.pennant.backend.dao.systemmasters.impl;

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

import com.pennant.backend.dao.systemmasters.OCRHeaderDAO;
import com.pennant.backend.model.ocrmaster.OCRHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class OCRHeaderDAOImpl extends SequenceDao<OCRHeader> implements OCRHeaderDAO {
	private static Logger logger = LogManager.getLogger(OCRHeaderDAOImpl.class);

	public OCRHeaderDAOImpl() {
		super();
	}

	@Override
	public void delete(OCRHeader ocrHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder deleteSql = new StringBuilder("Delete From OCRHEADER");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where OcrID = :OcrID and HeaderID = :HeaderID ");

		logger.trace(Literal.SQL + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ocrHeader);

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
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
	public String save(OCRHeader ocrHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (ocrHeader.getId() == Long.MIN_VALUE || ocrHeader.getId() == 0) {
			ocrHeader.setId(getNextValue("SeqOCRHeader"));
		}

		StringBuilder sql = new StringBuilder("Insert Into OCRHEADER");
		sql.append(tableType.getSuffix());
		sql.append(" (HeaderID, OcrID, OcrDescription, CustomerPortion, OcrType, Active");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:HeaderID, :OcrID, :OcrDescription, :CustomerPortion, :OcrType, :Active");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode");
		sql.append(", :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ocrHeader);

		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(ocrHeader.getHeaderID());
	}

	@Override
	public void update(OCRHeader ocrHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update OCRHEADER");
		sql.append(tableType.getSuffix());
		sql.append(" Set OcrDescription = :OcrDescription, CustomerPortion = :CustomerPortion");
		sql.append(", OcrType = :OcrType, Active = :Active");
		sql.append(", Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where HeaderID = :HeaderID ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ocrHeader);
		int recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(String OcrID, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "OcrID = :OcrID ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("OCRHEADER", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("OCRHEADER_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "OCRHEADER_Temp", "OCRHEADER" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("OcrID", OcrID);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public OCRHeader getOCRHeaderById(long headerID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getOCRHeaderQuery();
		sql.append(type);
		sql.append(" Where HeaderID = :HeaderID");

		logger.trace(Literal.SQL + sql.toString());

		OCRHeader ocrHeader = new OCRHeader();
		ocrHeader.setHeaderID(headerID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(ocrHeader);
		RowMapper<OCRHeader> rowMapper = BeanPropertyRowMapper.newInstance(OCRHeader.class);

		try {
			logger.debug(Literal.LEAVING);
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("OCR HEADER not available for the specified Header Id {}, ", headerID);
		}
		logger.debug(Literal.LEAVING);
		return ocrHeader;
	}

	@Override
	public OCRHeader getOCRHeaderByOCRId(String ocrID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getOCRHeaderQuery();
		sql.append(type);
		sql.append(" Where OcrID = :OcrID");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("OcrID", ocrID);

		RowMapper<OCRHeader> rowMapper = BeanPropertyRowMapper.newInstance(OCRHeader.class);

		try {
			logger.debug(Literal.LEAVING);
			return jdbcTemplate.queryForObject(sql.toString(), parameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	private StringBuilder getOCRHeaderQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("Select HeaderID, OCRID, OCRDescription, CustomerPortion, OcrType,Active");
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From OCRHEADER");
		return sql;
	}

}