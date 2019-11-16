package com.pennant.backend.dao.finance.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class FinStatusDetailDAOImpl extends BasicDao<FinStatusDetail> implements FinStatusDetailDAO {
	private static Logger logger = Logger.getLogger(FinStatusDetailDAOImpl.class);

	public FinStatusDetailDAOImpl() {
		super();
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
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
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
		this.jdbcTemplate.update(selectSql.toString(), beanParameters);

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
		return this.jdbcTemplate.update(updateSql.toString(), beanParameters);
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
		RowMapper<FinStatusDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinStatusDetail.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void updateCustStatuses(List<FinStatusDetail> custStatuses) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Update Customers  ");
		selectSql.append(" Set CustSts = :FinStatus, CustStsChgDate= :ValueDate WHERE CustId=:CustId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(custStatuses.toArray());
		logger.debug("Leaving");
		this.jdbcTemplate.batchUpdate(selectSql.toString(), beanParameters);
	}

	public List<FinStatusDetail> getFinStatusDetailByRefId(String finReference) {
		logger.debug("Entering");

		FinStatusDetail finStatusDetail = new FinStatusDetail();
		finStatusDetail.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" Select CustId , FinStatus, ValueDate, OdDays, FinReference");
		selectSql.append(" From FinStatusDetail");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStatusDetail);
		RowMapper<FinStatusDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinStatusDetail.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}
}
