package com.pennant.backend.dao.finance.impl;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.model.finance.FinStatusDetail;

public class FinStatusDetailDAOImpl implements FinStatusDetailDAO {

	private static Logger logger = Logger.getLogger(FinStatusDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FinStatusDetailDAOImpl() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public void save(FinStatusDetail finStatusDetail) {
		logger.debug("Entering");
		 
		StringBuilder insertSql = new StringBuilder(" Insert Into FinStatusDetail ");
		insertSql.append(" (FinReference, ValueDate, CustId, FinStatus, ODDays ) values");
		insertSql.append(" (:FinReference, :ValueDate, :CustId, :FinStatus, :ODDays)");

		logger.debug("insertSql: " + insertSql.toString());
		try {
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStatusDetail);
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		
	}

	@Override
	public void saveOrUpdateFinStatus(FinStatusDetail finStatusDetail) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("DELETE FROM  FinStatusDetail");
		selectSql.append(" Where FinReference =:FinReference AND ValueDate=:ValueDate");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStatusDetail);
		this.namedParameterJdbcTemplate.update(selectSql.toString(), beanParameters);
		
		save(finStatusDetail);
		
		logger.debug("Leaving");
	}
	
	public int update(FinStatusDetail finStatusDetail) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update FinStatusDetail ");
		updateSql.append(" Set FinStatus=:FinStatus  ");
		updateSql.append(" Where FinReference =:FinReference AND ValueDate=:ValueDate");

		logger.debug("insertSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStatusDetail);
		return this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
	}
	
	@Override
	public List<FinStatusDetail> getFinStatusDetailList(Date valueDate) {
		logger.debug("Entering");
		
		FinStatusDetail finStatusDetail = new FinStatusDetail();
		finStatusDetail.setValueDate(valueDate);		
		
		StringBuilder selectSql = new StringBuilder(" Select CustId , FinStatus ");
		selectSql.append(" From FinStatusDetail_View");
		selectSql.append(" Where ValueDate =:ValueDate ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStatusDetail);
		RowMapper<FinStatusDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinStatusDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	@Override
    public void updateCustStatuses(List<FinStatusDetail> custStatuses) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("Update Customers  " );
		selectSql.append(" Set CustSts = :FinStatus, CustStsChgDate= :ValueDate WHERE CustId=:CustId ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(custStatuses.toArray());
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.batchUpdate(selectSql.toString(), beanParameters);
    }

}
