package com.pennant.backend.dao.dashboard.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.dashboard.DetailStatisticsDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.dashboard.DetailStatistics;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennanttech.pennapps.core.util.ModuleUtil;


public class DetailStatisticsDAOImpl extends BasisCodeDAO<DetailStatistics> implements DetailStatisticsDAO{
	private static Logger logger = Logger.getLogger(DetailStatisticsDAOImpl .class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public DetailStatisticsDAOImpl() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public List<DetailStatistics> getAuditDetails() {
		logger.debug("Entering ");
		StringBuilder   selectSql = new StringBuilder("SELECT T1.AuditId, T1.AuditDate, AuditModule ModuleName, AuditReference ");
		selectSql.append("FROM AuditHeader T1 INNER JOIN ");
		selectSql.append("DetailStaticAudit T2 ON T1.AuditId > T2.AuditId AND T1.AuditDate > T2.AuditDate  WHERE AuditTranType='W'");
		logger.debug("selectSql: " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new DetailStatistics());
		RowMapper<DetailStatistics> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DetailStatistics.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}


	@Override
	public DetailStatistics getAuditDetail(DetailStatistics detailStatistics){
		logger.debug("Entering ");
		StringBuilder   selectSql = new StringBuilder("SELECT AuditId, AuditDate, LastMntOn, RoleCode CurrentRoleCode, NextRoleCode ");
		selectSql.append("FROM adt");
		selectSql.append(ModuleUtil.getTableName(detailStatistics.getModuleName()));
		selectSql.append(" WHERE AuditId= :AuditId AND AuditDate=:AuditDate ");
		logger.debug("selectSql: " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatistics);
		RowMapper<DetailStatistics> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DetailStatistics.class);
		logger.debug("Leaving ");

		DetailStatistics statistics = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,typeRowMapper);

		statistics.setAuditReference(detailStatistics.getAuditReference());
		statistics.setModuleName(detailStatistics.getModuleName());
		return statistics;

	}


	@Override
	public List<DetailStatistics> getDetailStatisticsList(DetailStatistics detailStatistics ){
		logger.debug("Entering ");
		StringBuilder   selectSql = new StringBuilder("SELECT ModuleName, RoleCode,AuditReference,TimeInMS,LastMntOn ");
		selectSql.append("FROM DetailStatistics ");
		selectSql.append("WHERE ModuleName=:ModuleName AND AuditReference=:AuditReference AND RecordStatus <> 0 ORDER BY LastMntOn Desc");
		logger.debug("selectSql: " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatistics);
		RowMapper<DetailStatistics> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DetailStatistics.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}

	@Override
	public void updateDetailStaticAudit(DetailStatistics detailStatistics) throws DataAccessException {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update DetailStaticAudit");
		updateSql.append(" Set AuditId = :AuditId,AuditDate = :AuditDate" );

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatistics);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	@Override
	public void updateCompleteStatus(DetailStatistics detailStatistics) throws DataAccessException{
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update DetailStatistics ");
		updateSql.append(" Set RecordStatus = :RecordStatus" );
		updateSql.append(" Where ModuleName = :ModuleName and AuditReference=:AuditReference" );

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatistics);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void save(DetailStatistics detailStatistics) {
		logger.debug("Entering");
		StringBuilder insert = new StringBuilder();
		insert.append("insert into DetailStatistics VALUES(");
		insert.append(" :ModuleName,:RoleCode,:AuditReference,:TimeInMS,:LastMntOn,:RecordStatus)" );
		logger.debug("updateSql: "+ insert.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatistics);
		this.namedParameterJdbcTemplate.update(insert.toString(), beanParameters);
		logger.debug("Leaving");
	}

	public List<ChartSetElement> getLabelAndValues(DashboardConfiguration aDashboardConfiguration){
		String selectSql = aDashboardConfiguration.getQuery();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				aDashboardConfiguration);
		RowMapper<ChartSetElement> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ChartSetElement.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql,
				beanParameters, typeRowMapper);
	}
}
