package com.pennant.backend.dao.dda.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.dda.EODFailPostingDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.finance.DDAFTransactionLog;


public class EODFailPostingDAOImpl extends BasisNextidDaoImpl<DDAFTransactionLog> implements EODFailPostingDAO {

private static Logger logger = Logger.getLogger(EODFailPostingDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public EODFailPostingDAOImpl() {
		super();
	}
	
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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
			ddaFTransactionLog = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
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
			dDAFTransactionLog.setSeqNo(getNextidviewDAO().getNextId("SeqDDAFTransactionLog"));	
		}
		StringBuilder insertSql = new StringBuilder("Insert Into dDAFTransactionLog" );
		insertSql.append(" (SeqNo, ValueDate, FinRefence, Error, ErrorCode, ErrorDesc, NoofTries," );
        insertSql.append(" Values(:SeqNo, :ValueDate, :FinRefence, :Error, :ErrorCode, :ErrorDesc, :NoofTries");
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dDAFTransactionLog);
		logger.debug("Leaving ");
		try {
			return this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
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
		 this.namedParameterJdbcTemplate.update(updateSql.toString(),	beanParameters);

		
		logger.debug("Leaving");
	}


	
}
