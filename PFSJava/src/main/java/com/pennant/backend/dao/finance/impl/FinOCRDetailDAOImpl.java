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

import com.pennant.backend.dao.finance.FinOCRDetailDAO;
import com.pennant.backend.model.finance.FinOCRDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinOCRDetailDAOImpl extends SequenceDao<FinOCRDetail> implements FinOCRDetailDAO {
	private static final Logger logger = LogManager.getLogger(FinOCRDetailDAOImpl.class);

	public FinOCRDetailDAOImpl() {
		super();
	}

	@Override
	public List<FinOCRDetail> getFinOCRDetailsByHeaderID(long headerID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select DetailID, HeaderID , CustomerContribution, FinancerContribution, Contributor,");
		sql.append("StepSequence");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinOCRDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderID= :HeaderID");

		logger.trace(Literal.SQL + sql.toString());
		FinOCRDetail finOCRDetail = new FinOCRDetail();
		finOCRDetail.setHeaderID(headerID);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOCRDetail);
		RowMapper<FinOCRDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinOCRDetail.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public FinOCRDetail getFinOCRDetailById(long detailID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select DetailID, HeaderID , CustomerContribution, FinancerContribution, Contributor,");
		sql.append("StepSequence");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinOCRDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DetailID= :DetailID");

		logger.trace(Literal.SQL + sql.toString());
		FinOCRDetail finOCRDetail = new FinOCRDetail();
		finOCRDetail.setDetailID(detailID);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOCRDetail);
		RowMapper<FinOCRDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinOCRDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void update(FinOCRDetail FinOCRDetail, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder();
		sql.append("Update FinOCRDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set HeaderID =:HeaderID, StepSequence =:StepSequence, ");
		sql.append("CustomerContribution =:CustomerContribution,");
		sql.append("FinancerContribution =:FinancerContribution, Contributor =:Contributor,");
		sql.append("Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append("RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append("TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where DetailID = :DetailID ");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(FinOCRDetail);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(FinOCRDetail FinOCRDetail, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("Delete From FinOCRDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DetailID = :DetailID");
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
	public long save(FinOCRDetail finOCRDetail, String type) {
		logger.debug(Literal.ENTERING);

		if (finOCRDetail.getDetailID() == Long.MIN_VALUE) {
			finOCRDetail.setDetailID(getNextValue("SeqFinOCRDetails"));
			logger.trace("get NextID:" + finOCRDetail.getDetailID());
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into FinOCRDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(DetailID, HeaderID , CustomerContribution, FinancerContribution, Contributor,");
		sql.append(" StepSequence, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");

		sql.append(" Values( :DetailID, :HeaderID , :CustomerContribution, :FinancerContribution, :Contributor,");
		sql.append(":StepSequence, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOCRDetail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);

		return finOCRDetail.getDetailID();
	}

	@Override
	public void deleteList(long headerID, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder("Delete From FinOCRDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderID = :HeaderID");
		logger.debug("deleteSql: " + sql.toString());
		source = new MapSqlParameterSource();
		source.addValue("HeaderID", headerID);
		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}
}
