package com.pennant.backend.dao.finance.impl;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.util.FinanceConstants;

public class FinLogEntryDetailDAOImpl implements FinLogEntryDetailDAO {

	private static Logger logger = Logger.getLogger(FinLogEntryDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FinLogEntryDetailDAOImpl() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public long save(FinLogEntryDetail entryDetail) {
		logger.debug("Entering");
		
		entryDetail.setLogKey(getLogKey());
		 
		StringBuilder insertSql = new StringBuilder(" Insert Into FinLogEntryDetail ");
		insertSql.append(" (FinReference, LogKey, EventAction, SchdlRecal, PostDate, ReversalCompleted ) ");
		insertSql.append(" Values (:FinReference, :LogKey, :EventAction, :SchdlRecal, :PostDate, :ReversalCompleted)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(entryDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return entryDetail.getLogKey();
	}
	
	/**
	 * Generate Linked Transaction ID
	 */
	public long getLogKey(){
		logger.debug("Entering");
		long count =0; 
		try {
			String updateSql = 	"update seqFinLogEntryDetail  set seqNo= seqNo+1 " ;
			this.namedParameterJdbcTemplate.getJdbcOperations().update(updateSql);

			String selectCountSql = "select seqNo from seqFinLogEntryDetail" ;
			count = this.namedParameterJdbcTemplate.getJdbcOperations().queryForObject(selectCountSql, Long.class);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving");
		return count;
	}
	
	@Override
	public List<FinLogEntryDetail> getFinLogEntryDetailList(String finReference, Date postDate) {
		logger.debug("Entering");
		
		FinLogEntryDetail finLogEntryDetail = new FinLogEntryDetail();
		finLogEntryDetail.setFinReference(finReference);
		finLogEntryDetail.setPostDate(postDate);
		finLogEntryDetail.setEventAction(FinanceConstants.FINSER_EVENT_SCHDRPY);
		
		
		StringBuilder selectSql = new StringBuilder(" Select T1.FinReference, T1.LogKey, T1.EventAction, T1.SchdlRecal, T1.PostDate, T1.ReversalCompleted ");
		selectSql.append(" From FinLogEntryDetail T1 ");
		selectSql.append(" Where T1.FinReference =:FinReference AND T1.PostDate >=:PostDate AND T1.ReversalCompleted = 0 " );
		selectSql.append(" AND T1.EventAction != :EventAction AND LogKey > COALESCE((select MAX(T2.LogKey) FROM FinLogEntryDetail T2 ");
		selectSql.append(" WHERE T1.FinReference = T2.FinReference AND T2.ReversalCompleted = 0  AND T2.EventAction=:EventAction ), 0)");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finLogEntryDetail);
		RowMapper<FinLogEntryDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinLogEntryDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	@Override
	public FinLogEntryDetail getFinLogEntryDetail(String finReference, String event, Date postDate) {
		logger.debug("Entering");
		
		FinLogEntryDetail finLogEntryDetail = new FinLogEntryDetail();
		finLogEntryDetail.setFinReference(finReference);
		finLogEntryDetail.setEventAction(event);
		finLogEntryDetail.setPostDate(postDate);
		
		StringBuilder selectSql = new StringBuilder(" Select T1.FinReference, T1.LogKey, T1.EventAction, T1.SchdlRecal, T1.PostDate, T1.ReversalCompleted " );
		selectSql.append(" From FinLogEntryDetail T1 Where T1.FinReference =:FinReference AND T1.EventAction=:EventAction " );
		selectSql.append(" AND T1.PostDate =:PostDate AND T1.ReversalCompleted = 0 AND " );
		selectSql.append(" LogKey = COALESCE((select MAX(T2.LogKey) FROM FinLogEntryDetail T2 WHERE T1.FinReference = T2.FinReference AND T2.ReversalCompleted = 0 ), 0) ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finLogEntryDetail);
		RowMapper<FinLogEntryDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinLogEntryDetail.class);
		logger.debug("Leaving");
		try {
			finLogEntryDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
        } catch (Exception e) {
        	logger.warn("Exception: ", e);
        	finLogEntryDetail = null;
        }
		return finLogEntryDetail;
		
	}
	
	@Override
	public void updateLogEntryStatus(FinLogEntryDetail finLogEntryDetail) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("  Update FinLogEntryDetail SET ReversalCompleted = 1 " );
		selectSql.append(" WHERE LogKey =:LogKey ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finLogEntryDetail);
		
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.update(selectSql.toString(), beanParameters);
	}
	
}
