package com.pennant.backend.dao.dashboard.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.dashboard.DetailStatisticsHeaderDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.dashboard.DetailStatisticsHeader;

public class DetailStatisticsHeaderDAOImpl extends BasisCodeDAO<DetailStatisticsHeader> implements DetailStatisticsHeaderDAO{
	private static Logger logger = Logger.getLogger(DetailStatisticsHeaderDAOImpl .class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public DetailStatisticsHeaderDAOImpl() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
	public List<DetailStatisticsHeader> getDetailStatisticsHeaderByRoleCode(String roleCode){
		logger.debug("Entering ");
		DetailStatisticsHeader detailStatisticsHeader = new DetailStatisticsHeader();
		detailStatisticsHeader.setRoleCode(roleCode);
		
		StringBuilder   selectSql = new StringBuilder("SELECT ModuleName,RoleCode,RecordCount ");
		selectSql.append("FROM DetailStatisticsHeader WHERE RoleCode=:RoleCode ");
		logger.debug("selectSql: " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatisticsHeader);
		RowMapper<DetailStatisticsHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DetailStatisticsHeader.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}

	@Override
	public List<DetailStatisticsHeader> getDetailStatisticsHeaderByModuleName(String moduleName) {
		logger.debug("Entering ");
		DetailStatisticsHeader detailStatisticsHeader = new DetailStatisticsHeader();
		detailStatisticsHeader.setModuleName(moduleName);
		
		StringBuilder   selectSql = new StringBuilder("SELECT ModuleName,RoleCode,RecordCount ");
		selectSql.append("FROM DetailStatisticsHeader WHERE ModuleName=:ModuleName");
		logger.debug("selectSql: " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatisticsHeader);
		RowMapper<DetailStatisticsHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DetailStatisticsHeader.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	
	@Override
	public void save(DetailStatisticsHeader detailStatisticsHeader) {
		logger.debug("Entering");
		StringBuilder insert = new StringBuilder();
		insert.append("insert into DetailStatisticsHeader VALUES(");
		insert.append(" :ModuleName,:RoleCode,:RecordCount)" );
		logger.debug("updateSql: "+ insert.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatisticsHeader);
		this.namedParameterJdbcTemplate.update(insert.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public boolean isExists(DetailStatisticsHeader detailStatisticsHeader) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select Count(ModuleName) from DetailStatisticsHeader ");
		selectSql.append(" Where ModuleName=:ModuleName AND RoleCode=:RoleCode" );
		logger.debug("updateSql: "+ selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detailStatisticsHeader);
		int count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		if(count!=0){
			return true;
		}
		
		logger.debug("Leaving");
		return false;
	}

	@Override
	public void update(DetailStatisticsHeader statisticsHeader, boolean decrease) throws DataAccessException{
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update DetailStatisticsHeader");
		if(decrease){
			updateSql.append(" Set recordCount = recordCount-1" );	
		}else{
			updateSql.append(" Set recordCount = recordCount+1" );
		}
		
		updateSql.append(" Where ModuleName = :ModuleName and RoleCode=:RoleCode" );

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(statisticsHeader);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	public List<DetailStatisticsHeader> getDetailStatisticsHeaderGroupByRole() {
		logger.debug("Entering ");
		List<DetailStatisticsHeader> list = new ArrayList<DetailStatisticsHeader>();
		StringBuilder selectSql = new StringBuilder(" select RoleCode, sum(RecordCount) lovDescTotRecordCount ");
		selectSql.append(" from DetailStatisticsHeader group by RoleCode order by RoleCode");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new DetailStatisticsHeader());
		RowMapper<DetailStatisticsHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DetailStatisticsHeader.class);
		try {
			list = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			list = null;
		}
		logger.debug("Leaving ");
		return list;

	}

	public List<DetailStatisticsHeader> getDetailStsHeaderGroupByModule(String roles) {
		logger.debug("Entering ");
		List<DetailStatisticsHeader> list = new ArrayList<DetailStatisticsHeader>();
		StringBuilder selectSql = new StringBuilder("   select modulename,sum(recordcount) lovDescTotRecordCount ");
		selectSql.append("  from DetailStatisticsHeader where rolecode in (" );
		selectSql.append( roles);
		selectSql.append(") group by Modulename ");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new DetailStatisticsHeader());
		RowMapper<DetailStatisticsHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DetailStatisticsHeader.class);
		try {
			list = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			list = null;
		}
		logger.debug("Leaving ");
		return list;
	}
}
