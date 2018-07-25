package com.pennant.backend.dao.dda.impl;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.dda.EODFailPostingDAO;
import com.pennant.backend.model.finance.DDAFTransactionLog;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;


public class EODFailPostingDAOImpl extends SequenceDao<DDAFTransactionLog> implements EODFailPostingDAO {
     private static Logger logger = Logger.getLogger(EODFailPostingDAOImpl.class);
	
	
	public EODFailPostingDAOImpl() {
		super();
	}
	
	
	
	
	@Override
	public DDAFTransactionLog getDDAFTranDetailsById(String finReference) {

		logger.debug("Entering");

		DDAFTransactionLog ddaFTransactionLog = new DDAFTransactionLog();
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinRefence", finReference);
		
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinRefence,valueDate, error, errorCode, errorDesc, noofTries");
		selectSql.append(" From DDAFTransactionLog ");
		selectSql.append(" Where FinRefence =:FinRefence");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<DDAFTransactionLog> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DDAFTransactionLog.class);

		try {
			ddaFTransactionLog = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
			ddaFTransactionLog = null;
		}
		
		logger.debug("Leaving");
		return ddaFTransactionLog;
    
	}


	@Override
	public long saveFailPostings(DDAFTransactionLog dDAFTransactionLog) {
         logger.debug("Entering ");
		
		if(dDAFTransactionLog.getId()== 0 ||dDAFTransactionLog.getId()==Long.MIN_VALUE){
			dDAFTransactionLog.setSeqNo(getNextId("SeqDDAFTransactionLog"));	
		}
		StringBuilder insertSql = new StringBuilder("Insert Into dDAFTransactionLog" );
		insertSql.append(" (SeqNo, ValueDate, FinRefence, Error, ErrorCode, ErrorDesc, NoofTries," );
        insertSql.append(" Values(:SeqNo, :ValueDate, :FinRefence, :Error, :ErrorCode, :ErrorDesc, :NoofTries");
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dDAFTransactionLog);
		logger.debug("Leaving ");
		try {
			return this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch(DataAccessException e) {
			logger.error("Exception: ", e);
			return 0;
		}
	}


	@Override
	public void updateFailPostings(DDAFTransactionLog dDAFTransactionLog) {
	
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update DDAFTransactionLog");
		
		updateSql.append(" Set ValueDate = :ValueDate, Error = :Error, ErrorCode = :ErrorCode,ErrorDesc = :ErrorDesc");
		updateSql.append(" Where FinRefence =:FinRefence ");
		

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dDAFTransactionLog);
		 this.jdbcTemplate.update(updateSql.toString(),	beanParameters);

		
		logger.debug("Leaving");
	}


	
}
