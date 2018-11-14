package com.pennant.backend.dao.limits.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.limits.ClosedFacilityDAO;
import com.pennant.backend.model.limits.ClosedFacilityDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class ClosedFacilityDAOImpl extends BasicDao<ClosedFacilityDetail> implements ClosedFacilityDAO {
	private static Logger logger = Logger.getLogger(ClosedFacilityDAOImpl.class);

	public ClosedFacilityDAOImpl() {
		super();
	}

	/**
	 * Method for fetch Un Processed Closed Facility Details from ACP Shared schema
	 * 
	 */
	@Override
	public List<ClosedFacilityDetail> fetchClosedFacilityDetails() {
		logger.debug("Entering");

		List<ClosedFacilityDetail> list = new ArrayList<ClosedFacilityDetail>();
		ClosedFacilityDetail closedFacilityDetail = new ClosedFacilityDetail();

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT  LimitReference, FacilityStatus, ClosedDate, Processed, ProcessedDate");
		selectSql.append(" FROM  INTER.PFF_CLOSED_LIMITS");
		selectSql.append(" WHERE Processed = 0");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(closedFacilityDetail);

		try {
			list = this.jdbcTemplate.queryForList(selectSql.toString(), beanParameters, ClosedFacilityDetail.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			list = null;
		}
		logger.debug("Leaving");
		return list;
	}

	@Override
	public void updateClosedFacilityStatus(List<ClosedFacilityDetail> proClFacilityList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update INTER.PFF_CLOSED_LIMITS");
		updateSql.append(" Set Processed = :Processed, ProcessedDate =:ProcessedDate");
		updateSql.append(" Where LimitReference =:LimitReference ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(proClFacilityList.toArray());

		logger.debug("Leaving");
		try {
			this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
}
