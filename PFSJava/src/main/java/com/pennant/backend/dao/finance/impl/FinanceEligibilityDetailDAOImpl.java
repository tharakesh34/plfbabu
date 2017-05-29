package com.pennant.backend.dao.finance.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinanceEligibilityDetailDAO;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;

public class FinanceEligibilityDetailDAOImpl implements FinanceEligibilityDetailDAO {
	
	private static Logger logger = Logger.getLogger(FinanceEligibilityDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FinanceEligibilityDetailDAOImpl() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
	public List<FinanceEligibilityDetail> getFinElgDetailByFinRef(final String finReference, String type) {
		logger.debug("Entering");
		FinanceEligibilityDetail financeEligibilityDetail = new FinanceEligibilityDetail();
		financeEligibilityDetail.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, ElgRuleCode, " );
		selectSql.append(" RuleResultType,RuleResult ,CanOverride, OverridePerc, UserOverride");
		if(type.contains("View")){
			selectSql.append(" ,LovDescElgRuleCode, LovDescElgRuleCodeDesc " );
		}
		selectSql.append(" From FinanceEligibilityDetail");
		selectSql.append(StringUtils.trimToEmpty(type)); 
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeEligibilityDetail);
		RowMapper<FinanceEligibilityDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceEligibilityDetail.class);
		logger.debug("Leaving");
		return  this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	@Override
	public int getFinElgDetailCount(FinanceEligibilityDetail financeEligibilityDetail) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("Select count(*) ");
		selectSql.append(" From FinanceEligibilityDetail");
		selectSql.append(" Where FinReference =:FinReference and ElgRuleCode = :ElgRuleCode ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeEligibilityDetail);
		logger.debug("Leaving");
		return  this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);	
	}
	
	@Override
	public void saveList(List<FinanceEligibilityDetail> eligibilityDetails,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinanceEligibilityDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, ElgRuleCode, RuleResultType,RuleResult ,CanOverride, OverridePerc, UserOverride, LastMntOn, LastMntBy )");
		insertSql.append(" Values(:FinReference, :ElgRuleCode, :RuleResultType, :RuleResult, :CanOverride, :OverridePerc, :UserOverride, :LastMntOn, :LastMntBy  )");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(eligibilityDetails.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void updateList(List<FinanceEligibilityDetail> eligibilityDetails) {
		logger.debug("Entering");
		
		StringBuilder updateSql =new StringBuilder();
		updateSql.append("Update FinanceEligibilityDetail");
		updateSql.append(" Set RuleResultType = :RuleResultType, RuleResult = :RuleResult ," );
		updateSql.append(" CanOverride = :CanOverride , OverridePerc = :OverridePerc, UserOverride = :UserOverride, " );
		updateSql.append(" LastMntOn = :LastMntOn , LastMntBy = :LastMntBy " );
		updateSql.append("  Where FinReference =:FinReference and ElgRuleCode = :ElgRuleCode ");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(eligibilityDetails.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	@Override
	public void deleteByFinRef(String finReference) {
		logger.debug("Entering");
		
		FinanceEligibilityDetail detail = new FinanceEligibilityDetail();
		detail.setFinReference(finReference);
		
		StringBuilder deleteSql =new StringBuilder();
		deleteSql.append(" DELETE FROM FinanceEligibilityDetail ");
		deleteSql.append(" Where FinReference =:FinReference ");
		
		logger.debug("updateSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
}
