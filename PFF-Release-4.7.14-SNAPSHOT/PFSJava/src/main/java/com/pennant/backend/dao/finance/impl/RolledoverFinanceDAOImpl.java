package com.pennant.backend.dao.finance.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.RolledoverFinanceDAO;
import com.pennant.backend.model.finance.RolledoverFinanceDetail;
import com.pennant.backend.model.finance.RolledoverFinanceHeader;

public class RolledoverFinanceDAOImpl implements RolledoverFinanceDAO {

	private static Logger logger = Logger.getLogger(RolledoverFinanceDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public RolledoverFinanceDAOImpl() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
    public RolledoverFinanceHeader getRolledoverFinanceHeader(String finReference, String type) {
		logger.debug("Entering");
		
		RolledoverFinanceHeader header = new RolledoverFinanceHeader();
		header.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" Select FinReference, CustPayment, PaymentAccount, LatePayAmount, LatePayWaiverAmount ");
		selectSql.append(" From RolledoverFinanceHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<RolledoverFinanceHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(RolledoverFinanceHeader.class);

		try {
			header = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), 
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			header = null;
		}
		logger.debug("Leaving");
		return header;
    }

	@Override
    public List<RolledoverFinanceDetail> getRolledoverDetailList(String finReference, String type) {
		logger.debug("Entering");
		
		RolledoverFinanceDetail detail = new RolledoverFinanceDetail();
		detail.setNewFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" Select FinReference, NewFinReference, RolloverAmount, CustPayment,");
		selectSql.append(" StartDate, RolloverDate, FinAmount, TotalProfit, ProfitRate, TotalPftBal, TotalPriBal ");
		selectSql.append(" From RolledoverFinanceDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where NewFinReference =:NewFinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<RolledoverFinanceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(RolledoverFinanceDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
    }
	
	@Override
    public void saveHeader(RolledoverFinanceHeader header, String type) {
		logger.debug("Entering");
		 
		StringBuilder insertSql = new StringBuilder(" Insert Into RolledoverFinanceHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, CustPayment, PaymentAccount, LatePayAmount, LatePayWaiverAmount) ");
		insertSql.append(" VALUES(:FinReference, :CustPayment, :PaymentAccount, :LatePayAmount, :LatePayWaiverAmount)");

		logger.debug("insertSql: " + insertSql.toString());
		try {
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
    }

	@Override
    public void deleteHeader(String finReference, String type) {
		logger.debug("Entering");
		
		Map<String,String> map=new HashMap<String, String>();
		map.put("FinReference", finReference);
		
		StringBuilder deleteSql = new StringBuilder(" Delete From RolledoverFinanceHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference = :FinReference ");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), map);	
		logger.debug("Leaving");
    }

	@Override
    public void updateHeader(RolledoverFinanceHeader header, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update RolledoverFinanceHeader");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustPayment=:CustPayment, PaymentAccount=:PaymentAccount, ");
		updateSql.append(" LatePayAmount=:LatePayAmount , LatePayWaiverAmount=:LatePayWaiverAmount ");
		updateSql.append(" Where FinReference =:FinReference ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
    }

	@Override
    public void saveDetailList(List<RolledoverFinanceDetail> details, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into RolledoverFinanceDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, NewFinReference, RolloverAmount, CustPayment)");
		insertSql.append(" Values(:FinReference, :NewFinReference, :RolloverAmount, :CustPayment)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(details.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
    }

	@Override
    public void deleteListByRef(String finReference, String type) {
		logger.debug("Entering");
		RolledoverFinanceDetail detail = new RolledoverFinanceDetail();
		detail.setNewFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From RolledoverFinanceDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where NewFinReference =:NewFinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
    }

}
