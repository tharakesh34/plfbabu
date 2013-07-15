package com.pennant.backend.dao.finance.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinanceScoreHeaderDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;

public class FinanceScoreHeaderDAOImpl extends BasisNextidDaoImpl<FinanceScoreHeader> implements FinanceScoreHeaderDAO {
	
	private static Logger logger = Logger.getLogger(FinanceProfitDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
    public List<FinanceScoreHeader> getFinScoreHeaderList(String finReference, String type) {
		logger.debug("Entering");
		FinanceScoreHeader scoreHeader = new FinanceScoreHeader();
		scoreHeader.setFinReference(finReference);
	    
		StringBuilder selectSql = new StringBuilder("SELECT HeaderId , FinReference , " );
		selectSql.append(" GroupId , MinScore , Override , OverrideScore , CreditWorth " );
		if(type.contains("View")){
			selectSql.append(" , GroupCode , GroupCodeDesc ");
		}	
		selectSql.append(" From FinanceScoreHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoreHeader);
		RowMapper<FinanceScoreHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceScoreHeader.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }

	@Override
    public long saveHeader(FinanceScoreHeader scoreHeader, String type) {
		logger.debug("Entering");
		
		if (scoreHeader.getHeaderId() == Long.MIN_VALUE) {
			scoreHeader.setId(getNextidviewDAO().getNextId("SeqFinanceScoreHeader"));
			logger.debug("get NextID:" + scoreHeader.getId());
		}	

		StringBuilder insertSql = new StringBuilder("INSERT INTO FinanceScoreHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (HeaderId , FinReference , GroupId , MinScore , " );
		insertSql.append(" Override , OverrideScore , CreditWorth) " );
		insertSql.append(" VALUES (:HeaderId , :FinReference , :GroupId , :MinScore , " );
		insertSql.append(" :Override , :OverrideScore , :CreditWorth) " );
		
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoreHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return scoreHeader.getId();
    }

	@Override
    public void deleteHeaderList(String finReferecne, String type) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public List<FinanceScoreDetail> getFinScoreDetailList(List<Long> headerIds, String type) {
		logger.debug("Entering");
	    
		StringBuilder selectSql = new StringBuilder(" SELECT HeaderId , SubGroupId , " );
		selectSql.append(" RuleId , MaxScore , ExecScore " );
		if(type.contains("View")){
			selectSql.append(" , SubGrpCodeDesc , RuleCode , RuleCodeDesc , CategoryType ");
		}	
		selectSql.append(" From FinanceScoreDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where HeaderId  IN(:HeaderId )");
		
		Map<String, List<Long>> parameterMap=new HashMap<String, List<Long>>();
		parameterMap.put("HeaderId", headerIds);

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceScoreDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceScoreDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), parameterMap, typeRowMapper);
    }

	@Override
    public void saveDetailList(List<FinanceScoreDetail> scoreDetails, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("INSERT INTO FinanceScoreDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (HeaderId , SubGroupId , RuleId , MaxScore , ExecScore) " );
		insertSql.append(" VALUES (:HeaderId , :SubGroupId , :RuleId , :MaxScore , :ExecScore) " );
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(scoreDetails.toArray());
		
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
    }

	@Override
    public void deleteDetailList(String finReferecne, String type) {
	    // TODO Auto-generated method stub
	    
    }

}
