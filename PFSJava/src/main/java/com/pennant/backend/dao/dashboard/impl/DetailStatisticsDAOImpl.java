package com.pennant.backend.dao.dashboard.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.dashboard.DetailStatisticsDAO;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.dashboard.DetailStatistics;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class DetailStatisticsDAOImpl extends BasicDao<DetailStatistics> implements DetailStatisticsDAO {
	private static Logger logger = LogManager.getLogger(DetailStatisticsDAOImpl.class);

	public DetailStatisticsDAOImpl() {
		super();
	}

	@Override
	public List<DetailStatistics> getAuditDetails() {
		logger.debug("Entering ");
		StringBuilder selectSql = new StringBuilder(
				"SELECT T1.AuditId, T1.AuditDate, AuditModule ModuleName, AuditReference ");
		selectSql.append("FROM AuditHeader T1 INNER JOIN ");
		selectSql.append(
				"DetailStaticAudit T2 ON T1.AuditId > T2.AuditId AND T1.AuditDate > T2.AuditDate  WHERE AuditTranType='W'");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new DetailStatistics());
		RowMapper<DetailStatistics> typeRowMapper = BeanPropertyRowMapper.newInstance(DetailStatistics.class);
		logger.debug("Leaving ");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public DetailStatistics getAuditDetail(DetailStatistics detailStatistics) {
		logger.debug("Entering ");
		StringBuilder selectSql = new StringBuilder(
				"SELECT AuditId, AuditDate, LastMntOn, RoleCode CurrentRoleCode, NextRoleCode ");
		selectSql.append("FROM adt");
		selectSql.append(ModuleUtil.getTableName(detailStatistics.getModuleName()));
		selectSql.append(" WHERE AuditId= :AuditId AND AuditDate=:AuditDate ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatistics);
		RowMapper<DetailStatistics> typeRowMapper = BeanPropertyRowMapper.newInstance(DetailStatistics.class);
		logger.debug("Leaving ");

		DetailStatistics statistics = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
				typeRowMapper);

		statistics.setAuditReference(detailStatistics.getAuditReference());
		statistics.setModuleName(detailStatistics.getModuleName());
		return statistics;

	}

	@Override
	public List<DetailStatistics> getDetailStatisticsList(DetailStatistics detailStatistics) {
		logger.debug("Entering ");
		StringBuilder selectSql = new StringBuilder("SELECT ModuleName, RoleCode,AuditReference,TimeInMS,LastMntOn ");
		selectSql.append("FROM DetailStatistics ");
		selectSql.append(
				"WHERE ModuleName=:ModuleName AND AuditReference=:AuditReference AND RecordStatus <> 0 ORDER BY LastMntOn Desc");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatistics);
		RowMapper<DetailStatistics> typeRowMapper = BeanPropertyRowMapper.newInstance(DetailStatistics.class);
		logger.debug("Leaving ");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void updateDetailStaticAudit(DetailStatistics detailStatistics) throws DataAccessException {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update DetailStaticAudit");
		updateSql.append(" Set AuditId = :AuditId,AuditDate = :AuditDate");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatistics);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void updateCompleteStatus(DetailStatistics detailStatistics) throws DataAccessException {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update DetailStatistics ");
		updateSql.append(" Set RecordStatus = :RecordStatus");
		updateSql.append(" Where ModuleName = :ModuleName and AuditReference=:AuditReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatistics);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void save(DetailStatistics detailStatistics) {
		logger.debug("Entering");
		StringBuilder insert = new StringBuilder();
		insert.append("insert into DetailStatistics VALUES(");
		insert.append(" :ModuleName,:RoleCode,:AuditReference,:TimeInMS,:LastMntOn,:RecordStatus)");
		logger.debug("updateSql: " + insert.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatistics);
		this.jdbcTemplate.update(insert.toString(), beanParameters);
		logger.debug("Leaving");
	}

	public List<ChartSetElement> getLabelAndValues(DashboardConfiguration aDashboardConfiguration) {
		String selectSql = aDashboardConfiguration.getQuery();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aDashboardConfiguration);
		RowMapper<ChartSetElement> typeRowMapper = BeanPropertyRowMapper.newInstance(ChartSetElement.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql, beanParameters, typeRowMapper);
	}
}
