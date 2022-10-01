package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.FinOCRDetailDAO;
import com.pennant.backend.model.finance.FinOCRDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class FinOCRDetailDAOImpl extends SequenceDao<FinOCRDetail> implements FinOCRDetailDAO {
	private static final Logger logger = LogManager.getLogger(FinOCRDetailDAOImpl.class);

	public FinOCRDetailDAOImpl() {
		super();
	}

	@Override
	public List<FinOCRDetail> getFinOCRDetailsByHeaderID(long headerID, String type) {
		StringBuilder sql = getSelectSqlQuery(type);
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, headerID), new FinOCRDetailsRM());
	}

	@Override
	public FinOCRDetail getFinOCRDetailById(long detailID, String type) {
		StringBuilder sql = getSelectSqlQuery(type);
		sql.append(" Where DetailID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FinOCRDetailsRM(), detailID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void update(FinOCRDetail ocrDtls, String type) {
		StringBuilder sql = new StringBuilder("Update FinOCRDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set HeaderID = ?, StepSequence = ?, CustomerContribution = ?");
		sql.append(", FinancerContribution = ?, Contributor = ?");
		sql.append(", Version = ? , LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where DetailID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ocrDtls.getHeaderID());
			ps.setInt(index++, ocrDtls.getStepSequence());
			ps.setBigDecimal(index++, ocrDtls.getCustomerContribution());
			ps.setBigDecimal(index++, ocrDtls.getFinancerContribution());
			ps.setString(index++, ocrDtls.getContributor());
			ps.setInt(index++, ocrDtls.getVersion());
			ps.setLong(index++, ocrDtls.getLastMntBy());
			ps.setTimestamp(index++, ocrDtls.getLastMntOn());
			ps.setString(index++, ocrDtls.getRecordStatus());
			ps.setString(index++, ocrDtls.getRoleCode());
			ps.setString(index++, ocrDtls.getNextRoleCode());
			ps.setString(index++, ocrDtls.getTaskId());
			ps.setString(index++, ocrDtls.getNextTaskId());
			ps.setString(index++, ocrDtls.getRecordType());
			ps.setLong(index++, ocrDtls.getWorkflowId());

			ps.setLong(index, ocrDtls.getDetailID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(FinOCRDetail ocrDtls, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinOCRDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DetailID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, ocrDtls.getDetailID()));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(FinOCRDetail ocrDtls, String type) {
		if (ocrDtls.getDetailID() == Long.MIN_VALUE) {
			ocrDtls.setDetailID(getNextValue("SeqFinOCRDetails"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinOCRDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(DetailID, HeaderID, CustomerContribution, FinancerContribution, Contributor, StepSequence");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ocrDtls.getDetailID());
			ps.setLong(index++, ocrDtls.getHeaderID());
			ps.setBigDecimal(index++, ocrDtls.getCustomerContribution());
			ps.setBigDecimal(index++, ocrDtls.getFinancerContribution());
			ps.setString(index++, ocrDtls.getContributor());
			ps.setInt(index++, ocrDtls.getStepSequence());
			ps.setInt(index++, ocrDtls.getVersion());
			ps.setLong(index++, ocrDtls.getLastMntBy());
			ps.setTimestamp(index++, ocrDtls.getLastMntOn());
			ps.setString(index++, ocrDtls.getRecordStatus());
			ps.setString(index++, ocrDtls.getRoleCode());
			ps.setString(index++, ocrDtls.getNextRoleCode());
			ps.setString(index++, ocrDtls.getTaskId());
			ps.setString(index++, ocrDtls.getNextTaskId());
			ps.setString(index++, ocrDtls.getRecordType());
			ps.setLong(index, ocrDtls.getWorkflowId());
		});

		return ocrDtls.getDetailID();
	}

	@Override
	public void deleteList(long headerID, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinOCRDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, headerID));

	}

	private StringBuilder getSelectSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DetailID, HeaderID, CustomerContribution, FinancerContribution, Contributor");
		sql.append(", StepSequence");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinOCRDetails");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FinOCRDetailsRM implements RowMapper<FinOCRDetail> {

		@Override
		public FinOCRDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinOCRDetail ocrDtls = new FinOCRDetail();

			ocrDtls.setDetailID(rs.getLong("DetailID"));
			ocrDtls.setHeaderID(rs.getLong("HeaderID"));
			ocrDtls.setCustomerContribution(rs.getBigDecimal("CustomerContribution"));
			ocrDtls.setFinancerContribution(rs.getBigDecimal("FinancerContribution"));
			ocrDtls.setContributor(rs.getString("Contributor"));
			ocrDtls.setStepSequence(rs.getInt("StepSequence"));
			ocrDtls.setVersion(rs.getInt("Version"));
			ocrDtls.setLastMntBy(rs.getLong("LastMntBy"));
			ocrDtls.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ocrDtls.setRecordStatus(rs.getString("RecordStatus"));
			ocrDtls.setRoleCode(rs.getString("RoleCode"));
			ocrDtls.setNextRoleCode(rs.getString("NextRoleCode"));
			ocrDtls.setTaskId(rs.getString("TaskId"));
			ocrDtls.setNextTaskId(rs.getString("NextTaskId"));
			ocrDtls.setRecordType(rs.getString("RecordType"));
			ocrDtls.setWorkflowId(rs.getLong("WorkflowId"));

			return ocrDtls;
		}
	}
}
