package com.pennant.backend.dao.dda.impl;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.dda.DDAProcessDAO;
import com.pennant.backend.model.finance.DDAProcessData;
import com.pennant.backend.model.limits.FinanceLimitProcess;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class DDAProcessDAOImpl extends SequenceDao<FinanceLimitProcess> implements DDAProcessDAO {
       private static Logger logger = Logger.getLogger(DDAProcessDAOImpl.class);
	 
	public DDAProcessDAOImpl() {
		super();
	}
	
	/**
	 * Method for save the DDA Validate Request and Registration request details
	 * 
	 * @param ddaProcessData
	 */
	@Override
    public long save(DDAProcessData ddaProcessData) {
		logger.debug("Entering ");
		
		if(ddaProcessData.getId()== 0 ||ddaProcessData.getId()==Long.MIN_VALUE){
			ddaProcessData.setSeqNo(getNextId("SeqDDAReferenceLog"));	
		}
		
		StringBuilder insertSql = new StringBuilder("Insert Into DDAReferenceLog" );
		insertSql.append(" (SeqNo, FinRefence, ReferenceNum, Purpose, CustCIF, CustomerType, IdType, IdNum," );
		insertSql.append(" CustomerName , BankName, AccountType, Iban, MobileNum, EmailID, CommenceOn, AllowedInstances, " );
		insertSql.append(" MaxAmount, CurrencyCode, PaymentFreq, Validation, Error, ErrorCode, ErrorDesc, ReturnCode, " );
		insertSql.append(" ReturnText, ValueDate, Active)" );
		insertSql.append(" Values(:SeqNo, :FinRefence, :ReferenceNum, :Purpose, :CustCIF, :CustomerType, :IdType, :IdNum," );
		insertSql.append(" :CustomerName , :BankName, :AccountType, :Iban, :MobileNum, :EmailID, :CommenceOn, :AllowedInstances, " );
		insertSql.append(" :MaxAmount, :CurrencyCode, :PaymentFreq, :Validation, :Error, :ErrorCode, :ErrorDesc, :ReturnCode, " );
		insertSql.append(" :ReturnText, :ValueDate, :Active)" );
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ddaProcessData);
		logger.debug("Leaving ");
		try {
			return this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch(DataAccessException e) {
			logger.error("Exception: ", e);
			return 0;
		}
	}

	/**
	 * Method for update Active status based on finReference
	 * 
	 * @param ddaProcessData
	 */
	@Override
	public void updateActiveStatus(String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinRefence", finReference);
		source.addValue("Active", 0);

		StringBuilder updateSql = new StringBuilder("UPDATE DDAReferenceLog ");
		updateSql.append(" Set  Active=:Active ");
		updateSql.append(" Where FinRefence =:FinRefence");

		logger.debug("updateSql: " + updateSql.toString());

		this.jdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");

	}

	/**
	 * Method for fetch DDA Initiation Details
	 * 
	 * @param finReference
	 * @param reqTypeValidate
	 * 
	 * @return DDAProcessData
	 */
	@Override
    public DDAProcessData getDDADetailsById(String finReference, String reqTypeValidate) {
		logger.debug("Entering");

		DDAProcessData ddaProcessData = new DDAProcessData();
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinRefence", finReference);
		source.addValue("Purpose", reqTypeValidate);
		source.addValue("Active", 1);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select SeqNo, FinRefence, ReferenceNum, Purpose, CustCIF, CustomerType, IdType, IdNum, CustomerName,");
		selectSql.append(" BankName, AccountType, Iban, MobileNum, EmailID, CommenceOn, AllowedInstances, MaxAmount, CurrencyCode, Active, ");
		selectSql.append(" MaxAmount, CurrencyCode, PaymentFreq, Validation, Error, ErrorCode, ErrorDesc, ReturnCode,  ReturnText, ValueDate, ");
		selectSql.append(" DdaAckStatus, DdaReference ");
		selectSql.append(" From DDAReferenceLog ");
		selectSql.append(" Where FinRefence =:FinRefence AND Purpose =:Purpose AND Active =:Active");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<DDAProcessData> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DDAProcessData.class);

		try {
			ddaProcessData = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
			ddaProcessData = null;
		}
		
		logger.debug("Leaving");
		return ddaProcessData;
    }

	/**
	 * Method for fetch Finance Division and category types
	 * 
	 * @param finType
	 * @return FinanceType
	 */
	@Override
    public FinanceType getFinTypeDetails(String finType) {
		logger.debug("Entering");

		FinanceType aFinanceType = new FinanceType();
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", finType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinCategory, FinDivision  From RMTFinanceTypes ");
		selectSql.append(" Where FinType =:FinType");

		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);
		
		try {
			aFinanceType = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
			aFinanceType = null;
		}
		logger.debug("Leaving");
		return aFinanceType;
		
	}
	


	@Override
	public DDAProcessData getDDADetailsByReference(String finReference,
			String reqTypeValidate) {
		logger.debug("Entering");

		DDAProcessData ddaProcessData = new DDAProcessData();
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinRefence", finReference);
		source.addValue("Purpose", reqTypeValidate);
		source.addValue("Active", 0);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select SeqNo, FinRefence, ReferenceNum, Purpose, CustCIF, CustomerType, IdType, IdNum, CustomerName,");
		selectSql.append(" BankName, AccountType, Iban, MobileNum, EmailID, CommenceOn, AllowedInstances, MaxAmount, CurrencyCode, Active, ");
		selectSql.append(" MaxAmount, CurrencyCode, PaymentFreq, Validation, Error, ErrorCode, ErrorDesc, ReturnCode,  ReturnText, ValueDate, ");
		selectSql.append(" DdaAckStatus, DdaReference ");
		selectSql.append(" From DDAReferenceLog ");
		selectSql.append(" Where FinRefence =:FinRefence AND Purpose =:Purpose AND Active =:Active");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<DDAProcessData> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DDAProcessData.class);

		try {
			ddaProcessData = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
			ddaProcessData = null;
		}
		
		logger.debug("Leaving");
		return ddaProcessData;
    }
}
