package com.pennanttech.pennapps.pff.finsampling.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pff.core.TableType;

public class FinSamplingDAOImpl extends SequenceDao<Sampling> implements FinSamplingDAO {
	private static Logger logger = LogManager.getLogger(FinSamplingDAOImpl.class);

	@Override
	public void updateSampling(Sampling sampling, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder query = new StringBuilder();
		query.append(" update sampling");
		query.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		query.append(" set decision =:decision, remarks = :remarks,");
		query.append(" resubmitReason = :resubmitReason, recommendedAmount = :recommendedAmount");
		query.append(" where id = :Id ");

		logger.trace(Literal.SQL + query.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sampling);
		recordCount = this.jdbcTemplate.update(query.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);

	}

	public void saveOrUpdateRemarks(Sampling sampling, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("delete from sampling_remarks");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" where samplingid=:id");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", sampling.getId());

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

		sql = new StringBuilder();
		sql.append(" insert into sampling_remarks");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" values(?,?,?)");

		jdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), getList(sampling));

		logger.debug(Literal.LEAVING);
	}

	private List<Object[]> getList(Sampling sampling) {
		List<Object[]> list = new ArrayList<>();
		for (Entry<String, Object> remarks : sampling.getReamrksMap().entrySet()) {
			list.add(new Object[] { sampling.getId(), remarks.getKey(), remarks.getValue() });
		}
		return list;
	}

	@Override
	public void saveCollateral(long samplingId, String collateralType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		List<String> linkIds = getLinkIds(samplingId);
		StringBuilder sql = new StringBuilder("insert into verification_");
		sql.append(StringUtils.trimToEmpty(collateralType));
		sql.append("_tv_temp");
		sql.append(" select * from verification_");
		sql.append(StringUtils.trimToEmpty(collateralType));
		sql.append("_tv  Where reference in(:linkIds)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("linkIds",linkIds);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private List<String> getLinkIds(long samplingId) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();

		sql.append("select linkId from link_sampling_collaterals  where samplingId=:samplingId");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("samplingId", samplingId);

		try {
			return jdbcTemplate.queryForList(sql.toString(), paramSource, String.class);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

}
