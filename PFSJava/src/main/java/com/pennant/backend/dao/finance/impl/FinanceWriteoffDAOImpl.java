package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.model.finance.FinWriteoffPayment;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

public class FinanceWriteoffDAOImpl implements FinanceWriteoffDAO {

	private static Logger logger = Logger.getLogger(FinanceWriteoffDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public FinanceWriteoffDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record  FinanceWriteoff details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceWriteoff
	 */
	@Override
	public FinanceWriteoff getFinanceWriteoffById(final String finReference, String type) {
		logger.debug("Entering");
		
		FinanceWriteoff financeWriteoff = new FinanceWriteoff();
		financeWriteoff.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference , WriteoffDate , SeqNo , WrittenoffPri , WrittenoffPft , " );
		selectSql.append(" CurODPri , CurODPft , UnPaidSchdPri , UnPaidSchdPft , PenaltyAmount , ProvisionedAmount , WriteoffPrincipal , " );
		selectSql.append(" WriteoffProfit , AdjAmount , Remarks, WrittenoffAcc, " );
		selectSql.append(" WrittenoffIns,WrittenoffIncrCost,WrittenoffSuplRent,WrittenoffSchFee, " );
		selectSql.append(" UnpaidIns,UnpaidIncrCost,UnpaidSuplRent,UnpaidSchFee, " );
		selectSql.append(" WriteoffIns,WriteoffIncrCost,WriteoffSuplRent,WriteoffSchFee " );
		selectSql.append(" From FinWriteoffDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWriteoff);
		RowMapper<FinanceWriteoff> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceWriteoff.class);
		
		try{
			financeWriteoff = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeWriteoff = null;
		}
		logger.debug("Leaving");
		return financeWriteoff;
	}
	
	/**
	 * Fetch the Record  FinanceWriteoff details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceWriteoff
	 */
	@Override
	public int getMaxFinanceWriteoffSeq(final String finReference, Date writeoffDate, String type) {
		logger.debug("Entering");
		
		int seqNo = 0;
		FinanceWriteoff financeWriteoff = new FinanceWriteoff();
		financeWriteoff.setFinReference(finReference);
		financeWriteoff.setWriteoffDate(writeoffDate);
		
		StringBuilder selectSql = new StringBuilder("Select COALESCE( MAX(SeqNo),0) " );
		selectSql.append(" From FinWriteoffDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND WriteoffDate=:WriteoffDate");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWriteoff);
		
		try{
			seqNo = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			seqNo = 0;
		}
		logger.debug("Leaving");
		return seqNo;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinanceWriteoff or FinanceWriteoff_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete FinanceWriteoff by key FinReference
	 * 
	 * @param FinanceWriteoff (financeWriteoff)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(String finReference,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		FinanceWriteoff financeWriteoff = new FinanceWriteoff();
		financeWriteoff.setFinReference(finReference);
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinWriteoffDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWriteoff);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);

		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinanceWriteoff or FinanceWriteoff_Temp.
	 *
	 * save FinanceWriteoff 
	 * 
	 * @param FinanceWriteoff (financeWriteoff)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(FinanceWriteoff financeWriteoff,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinWriteoffDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference , WriteoffDate , SeqNo , WrittenoffPri , WrittenoffPft , CurODPri , CurODPft , " );
		insertSql.append(" UnPaidSchdPri , UnPaidSchdPft , PenaltyAmount , ProvisionedAmount , WriteoffPrincipal , " );
		insertSql.append(" WriteoffProfit , AdjAmount , Remarks, WrittenoffAcc, ");
		insertSql.append(" WrittenoffIns,WrittenoffIncrCost,WrittenoffSuplRent,WrittenoffSchFee, ");
		insertSql.append(" UnpaidIns,UnpaidIncrCost,UnpaidSuplRent,UnpaidSchFee,");
		insertSql.append(" WriteoffIns,WriteoffIncrCost,WriteoffSuplRent,WriteoffSchFee)");
		insertSql.append(" Values(:FinReference , :WriteoffDate , :SeqNo , :WrittenoffPri , :WrittenoffPft , :CurODPri , :CurODPft , " );
		insertSql.append(" :UnPaidSchdPri , :UnPaidSchdPft , :PenaltyAmount , :ProvisionedAmount , :WriteoffPrincipal , " );
		insertSql.append(" :WriteoffProfit , :AdjAmount , :Remarks, :WrittenoffAcc,");
		insertSql.append(" :WrittenoffIns,:WrittenoffIncrCost,:WrittenoffSuplRent,:WrittenoffSchFee, ");
		insertSql.append(" :UnpaidIns,:UnpaidIncrCost,:UnpaidSuplRent,:UnpaidSchFee,");
		insertSql.append(" :WriteoffIns,:WriteoffIncrCost,:WriteoffSuplRent,:WriteoffSchFee)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWriteoff);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeWriteoff.getFinReference();
	}
	
	/**
	 * This method updates the Record FinanceWriteoff or FinanceWriteoff_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update FinanceWriteoff by key FinReference and Version
	 * 
	 * @param FinanceWriteoff (financeWriteoff)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinanceWriteoff financeWriteoff,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinWriteoffDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set WriteoffDate=:WriteoffDate , SeqNo=:SeqNo , WrittenoffPri=:WrittenoffPri , " );
		updateSql.append(" WrittenoffPft=:WrittenoffPft , CurODPri=:CurODPri , CurODPft=:CurODPft , UnPaidSchdPri=:UnPaidSchdPri , " );
		updateSql.append(" UnPaidSchdPft=:UnPaidSchdPft , PenaltyAmount=:PenaltyAmount , ProvisionedAmount=:ProvisionedAmount , " );
		updateSql.append(" WriteoffPrincipal=:WriteoffPrincipal , WriteoffProfit=:WriteoffProfit , AdjAmount=:AdjAmount , Remarks=:Remarks, WrittenoffAcc=:WrittenoffAcc," );
		updateSql.append(" WrittenoffIns=:WrittenoffIns, ");
		updateSql.append(" WrittenoffIncrCost=:WrittenoffIncrCost,WrittenoffSuplRent=:WrittenoffSuplRent,WrittenoffSchFee=:WrittenoffSchFee," );
		updateSql.append(" UnpaidIns=:UnpaidIns, " );
		updateSql.append(" UnpaidIncrCost=:UnpaidIncrCost,UnpaidSuplRent=:UnpaidSuplRent,UnpaidSchFee=:UnpaidSchFee," );
		updateSql.append(" WriteoffIns=:WriteoffIns, " );
		updateSql.append(" WriteoffIncrCost=:WriteoffIncrCost,WriteoffSuplRent=:WriteoffSuplRent,WriteoffSchFee=:WriteoffSchFee" );
		updateSql.append(" Where FinReference =:FinReference");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWriteoff);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	@Override
	public FinWriteoffPayment getFinWriteoffPaymentById(String finReference,
			String type) {
		
		FinWriteoffPayment finWriteoffPayment = new FinWriteoffPayment();
		finWriteoffPayment.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, WriteoffPayAmount, WriteoffPayAccount, LinkedTranId,SeqNo,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinWriteoffPayment");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finWriteoffPayment);
		RowMapper<FinWriteoffPayment> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinWriteoffPayment.class);
		
		try{
			finWriteoffPayment = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finWriteoffPayment = null;
		}
		logger.debug("Leaving");
		return finWriteoffPayment;
	}

	@Override
	public void deletefinWriteoffPayment(String finReference,long seqNo, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		FinWriteoffPayment finWriteoffPayment = new FinWriteoffPayment();
		finWriteoffPayment.setFinReference(finReference);
		finWriteoffPayment.setSeqNo(seqNo);
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinWriteoffPayment");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference AND SeqNo =:SeqNo");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finWriteoffPayment);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
		
	}

	@Override
	public String saveFinWriteoffPayment(FinWriteoffPayment finWriteoffPayment,
			String type) {
		logger.debug("Entering");

		StringBuilder insertSql =new StringBuilder("Insert Into FinWriteoffPayment");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, WriteoffPayAmount, WriteoffPayAccount,LinkedTranId,SeqNo,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :WriteoffPayAmount, :WriteoffPayAccount,:LinkedTranId,:SeqNo,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finWriteoffPayment);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finWriteoffPayment.getFinReference();
	}

	@Override
	public void updateFinWriteoffPayment(FinWriteoffPayment finWriteoffPayment,
			String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinWriteoffPayment");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set WriteoffPayAmount = :WriteoffPayAmount, WriteoffPayAccount = :WriteoffPayAccount,LinkedTranId=:LinkedTranId,SeqNo=:SeqNo,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finWriteoffPayment);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
		
	}
	
	public BigDecimal getTotalFinWriteoffDetailAmt(String finReference) {
		logger.debug("Entering");

		FinanceWriteoff financeWriteoff = new FinanceWriteoff();
		financeWriteoff.setFinReference(finReference);

		// Get Sum of Total Payment Amount(profits and Principals)
		StringBuilder selectSql = new StringBuilder(" select sum(WriteoffPrincipal )+sum(WriteoffProfit) + sum(WriteoffIns) + " );
		selectSql.append(" sum(WriteoffIncrCost) + sum(WriteoffSuplRent) + sum(WriteoffSchFee) ");
		selectSql.append(" from FinWriteoffDetail  where FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				financeWriteoff);

		BigDecimal writeoffAmount = BigDecimal.ZERO;
		try {
			writeoffAmount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			writeoffAmount = BigDecimal.ZERO;
		}

		logger.debug("Leaving");
		return writeoffAmount;
	
    }
	public BigDecimal getTotalWriteoffPaymentAmount(String finReference) {
		logger.debug("Entering");
		
		FinWriteoffPayment finwriteoffPayment = new FinWriteoffPayment();
		finwriteoffPayment.setFinReference(finReference);
		
		// Get Sum of Total Payment Amount(profits and Principals)
		StringBuilder selectSql = new StringBuilder(" select sum(WriteoffPayAmount) " );
		selectSql.append(" from FinWriteoffPayment  where FinReference = :FinReference ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				finwriteoffPayment);
		
		BigDecimal finwriteoffPayAmount = BigDecimal.ZERO;
		try {
			finwriteoffPayAmount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finwriteoffPayAmount = BigDecimal.ZERO;
		}
		if(finwriteoffPayAmount == null){
			finwriteoffPayAmount = BigDecimal.ZERO;
		}
		logger.debug("Leaving");
		return finwriteoffPayAmount;
		
	}

	@Override
	public Date getFinWriteoffDate(String finReference) {
      logger.debug("Entering");
		
      FinanceWriteoff financeWriteoff = new FinanceWriteoff();
		financeWriteoff.setFinReference(finReference);
		StringBuilder selectSql = new StringBuilder(" select WriteoffDate " );
		selectSql.append(" from FinWriteoffDetail  where FinReference = :FinReference ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				financeWriteoff);
		
		Date finWriteoffDate = null;
		try {
			finWriteoffDate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, Date.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finWriteoffDate = DateUtility.getAppDate();
		}
		logger.debug("Leaving");
		return finWriteoffDate;
	}
	
	public long getfinWriteoffPaySeqNo(String finreference,String type) {
		logger.debug("Entering");

		long seqNo= 0;
		
		FinWriteoffPayment finwriteoffPayment = new FinWriteoffPayment();
		finwriteoffPayment.setFinReference(finreference);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select COALESCE(max(SeqNo), 0)  From FinWriteoffPayment ");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finwriteoffPayment);
		
		try{
			seqNo = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Long.class);	
		} catch (EmptyResultDataAccessException dae) {
			seqNo =  Long.MIN_VALUE;
		}

		logger.debug("Leaving");
		return seqNo;
	}

	
}
