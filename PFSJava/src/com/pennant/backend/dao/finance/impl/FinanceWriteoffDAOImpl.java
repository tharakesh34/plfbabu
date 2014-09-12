package com.pennant.backend.dao.finance.impl;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinanceWriteoffDAOImpl implements FinanceWriteoffDAO {

	private static Logger logger = Logger.getLogger(FinanceWriteoffDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
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
		selectSql.append(" WriteoffProfit , AdjAmount , Remarks " );
		selectSql.append(" From FinWriteoffDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWriteoff);
		RowMapper<FinanceWriteoff> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceWriteoff.class);
		
		try{
			financeWriteoff = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
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
		
		StringBuilder selectSql = new StringBuilder("Select MAX(SeqNo) " );
		selectSql.append(" From FinWriteoffDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND WriteoffDate=:WriteoffDate");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWriteoff);
		
		try{
			seqNo = this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(), beanParameters);	
		}catch (EmptyResultDataAccessException e) {
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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails= getError("41003",financeWriteoff.getFinReference() ,PennantConstants.default_Language);
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",financeWriteoff.getFinReference() ,PennantConstants.default_Language);
			throw new DataAccessException(errorDetails.getError()) {};
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
		insertSql.append(" WriteoffProfit , AdjAmount , Remarks )");
		insertSql.append(" Values(:FinReference , :WriteoffDate , :SeqNo , :WrittenoffPri , :WrittenoffPft , :CurODPri , :CurODPft , " );
		insertSql.append(" :UnPaidSchdPri , :UnPaidSchdPft , :PenaltyAmount , :ProvisionedAmount , :WriteoffPrincipal , " );
		insertSql.append(" :WriteoffProfit , :AdjAmount , :Remarks )");
		
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
	@SuppressWarnings("serial")
    @Override
	public void update(FinanceWriteoff financeWriteoff,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinWriteoffDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinReference=:FinReference , WriteoffDate=:WriteoffDate , SeqNo=:SeqNo , WrittenoffPri=:WrittenoffPri , " );
		updateSql.append(" WrittenoffPft=:WrittenoffPft , CurODPri=:CurODPri , CurODPft=:CurODPft , UnPaidSchdPri=:UnPaidSchdPri , " );
		updateSql.append(" UnPaidSchdPft=:UnPaidSchdPft , PenaltyAmount=:PenaltyAmount , ProvisionedAmount=:ProvisionedAmount , " );
		updateSql.append(" WriteoffPrincipal=:WriteoffPrincipal , WriteoffProfit=:WriteoffProfit , AdjAmount=:AdjAmount , Remarks=:Remarks " );
		updateSql.append(" Where FinReference =:FinReference");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWriteoff);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",financeWriteoff.getFinReference() ,PennantConstants.default_Language);
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String finReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
	
}
