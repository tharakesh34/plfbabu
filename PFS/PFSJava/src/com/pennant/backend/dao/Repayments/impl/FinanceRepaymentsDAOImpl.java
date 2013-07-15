/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinanceRepaymentsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.Repayments.impl;

import java.util.Date;
import java.util.List;

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
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinanceRepaymentsDAOImpl extends BasisCodeDAO<FinanceRepayments> implements FinanceRepaymentsDAO {
	private static Logger	           logger	= Logger.getLogger(FinanceRepaymentsDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	/**
	 * Fetch the Record Finance Repayments Details details by key field
	 * 
	 * @param finReference
	 *            (String)
	 * @param finSchdDate
	 *            (Date)
	 * @param finPaySeq
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceRepayments
	 */
	@Override
	public FinanceRepayments getFinanceRepaymentsById(String finReference, Date finSchdDate, int finPaySeq, String type) {
		logger.debug("Entering");
		FinanceRepayments financeRepayments = new FinanceRepayments();
		financeRepayments.setFinReference(finReference);
		financeRepayments.setFinSchdDate(finSchdDate);
		financeRepayments.setFinPaySeq(finPaySeq);
		StringBuilder selectSql = new StringBuilder("select FinReference, FinPostDate, FinRpyFor, FinPaySeq,");
		selectSql.append(" FinRpyAmount, FinSchdDate, FinValueDate, FinBranch,");
	    selectSql.append(" FinType, FinCustID, FinSchdPriPaid, FinSchdPftPaid,");
		selectSql.append(" FinTotSchdPaid, FinFee, FinWaiver, FinRefund");
		selectSql.append(" From FinRepayDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where FinReference=:FinReference and FinPostDate=:FinPostDate and FinPaySeq=:FinPaySeq ");
		selectSql.append(" and FinRpyFor= :FinRpyFor");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		RowMapper<FinanceRepayments> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceRepayments.class);

		try {
			financeRepayments = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			financeRepayments = null;
		}
		logger.debug("Leaving");
		return financeRepayments;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param FinanceRepayments
	 *            (financeRepayments)
	 * @return FinanceRepayments
	 */
	@Override
	public void initialize(FinanceRepayments financeRepayments) {
		super.initialize(financeRepayments);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinanceRepayments
	 *            (academic)
	 * @return void
	 */
	@Override
	public void refresh(FinanceRepayments entity) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the FinanceRepayments or
	 * FinanceRepayments if Record not deleted then throws DataAccessException
	 * with error 41003. delete FinanceRepayments Details by key AcademicLevel
	 * 
	 * @param FinanceRepayments
	 *            Details (financeRepayments)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	@SuppressWarnings("serial")
	public void delete(FinanceRepayments financeRepayments, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinRepayDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where FinReference=:FinReference and FinPostDate=:FinPostDate and FinPaySeq=:FinPaySeq");
		deleteSql.append(" and FinRpyFor= :FinRpyFor");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);

		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails = getError("41006", "", "", "");
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");

	}

	/**
	 * This method Deletes the Record from the FinanceRepayments or
	 * FinanceRepayments if Record not deleted then throws DataAccessException
	 * with error 41003. delete FinanceRepayments Details by key AcademicLevel
	 * 
	 * @param FinanceRepayments
	 *            Details (financeRepayments)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	@SuppressWarnings({ "serial", "unused" })
	public void deleteByFinRef(String finref, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		FinanceRepayments financeRepayments=new FinanceRepayments();
		financeRepayments.setFinReference(finref);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinRepayDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where FinReference=:FinReference ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

	/*		if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004", "", "", "");
				throw new DataAccessException(errorDetails.getError()) {
				};
			}*/
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails = getError("41006", "", "", "");
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");

	}
	
	/**
	 * Method for save List Of Repayment details at once
	 */
	@Override
	public void saveRepayList(List<FinanceRepayments> repaymentList, String type) {
		logger.debug("Entering");
		for (int i = 0; i < repaymentList.size(); i++) {
			save(repaymentList.get(i), type);
		}
		logger.debug("Leaving");
	}
	
	
	/**
	 * Generate Finance Pay Sequence
	 */
	public long getFinancePaySeq(FinanceRepayments financeRepayments){
		logger.debug("Entering");
		long count =0; 
		try {
			String updateSql = 	"update SeqFinRepayDetails  set seqNo= seqNo+1 " ;
			this.namedParameterJdbcTemplate.getJdbcOperations().update(updateSql);

			String selectCountSql = "select seqNo from SeqFinRepayDetails" ;
			count = this.namedParameterJdbcTemplate.getJdbcOperations().queryForLong(selectCountSql);
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("Leaving"+count);
		return count;
	}

	/**
	 * This method insert new Records into FinanceRepayments .
	 * 
	 * save Finance Repayments
	 * 
	 * @param FinanceRepayments
	 *            Details (financeRepayments)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(FinanceRepayments financeRepayments, String type) {
		logger.debug("Entering");
		if (financeRepayments.getId() == Long.MIN_VALUE) {
			financeRepayments.setFinPaySeq(getFinancePaySeq(financeRepayments));
		}
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into FinRepayDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, FinPostDate, FinRpyFor, FinPaySeq,LinkedTranId,");
		insertSql.append(" FinRpyAmount, FinSchdDate, FinValueDate, FinBranch,");
		insertSql.append(" FinType, FinCustID, FinSchdPriPaid, FinSchdPftPaid,");
		insertSql.append(" FinTotSchdPaid, FinFee, FinWaiver, FinRefund) Values(");
		insertSql.append(" :FinReference, :FinPostDate, :FinRpyFor, :FinPaySeq,:LinkedTranId,");
		insertSql.append(" :FinRpyAmount, :FinSchdDate, :FinValueDate, :FinBranch,");
		insertSql.append(" :FinType, :FinCustID, :FinSchdPriPaid, :FinSchdPftPaid,");
		insertSql.append(" :FinTotSchdPaid,:FinFee, :FinWaiver, :FinRefund)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return financeRepayments.getId();
	}

	/**
	 * This method updates the Record FinanceRepayments . if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update FinanceRepayments by key financeRepayments and Version
	 * 
	 * @param FinanceRepayments
	 *            Details (financeRepayments)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinanceRepayments financeRepayments, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update FinRepayDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" (Set FinReference=:FinReference, FinPostDate = :FinPostDate, FinRpyFor = :FinRpyFor,");
		updateSql.append(" FinPaySeq= :FinPaySeq, FinRpyAmount= :FinRpyAmount, FinSchdDate= :FinSchdDate,");
		updateSql.append(" FinValueDate= :FinValueDate, FinBranch=:FinBranch,");
		updateSql.append(" FinType=:FinType, FinCustID=:FinCustID, FinSchdPriPaid=:FinSchdPriPaid,");
		updateSql.append(" FinSchdPftPaid=:FinSchdPftPaid, FinTotSchdPaid= :FinTotSchdPaid,");
		updateSql.append(" FinFee=: FinFee,FinWaiver= :FinWaiver, FinRefund= :FinRefund)");
		updateSql.append(" where FinReference=:FinReference and FinPostDate=:FinPostDate and FinPaySeq=:FinPaySeq ");
		updateSql.append(" and FinRpyFor= :FinRpyFor");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

/*		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41003", "", "", "");
			throw new DataAccessException(errorDetails.getError()) {
			};
		}*/
		logger.debug("Leaving");

	}
	
	@Override
	public List<FinanceRepayments> getFinRepayListById(String finReference, Date finSchdDate,String type) {
		logger.debug("Entering");
		FinanceRepayments financeRepayments = new FinanceRepayments();
		financeRepayments.setFinReference(finReference);
		financeRepayments.setFinSchdDate(finSchdDate);	
		StringBuilder selectSql = new StringBuilder("select FinReference, FinPostDate, FinRpyFor, FinPaySeq,");
		selectSql.append(" FinRpyAmount, FinSchdDate, FinValueDate, FinBranch,");
	    selectSql.append(" FinType, FinCustID, FinSchdPriPaid, FinSchdPftPaid,");
		selectSql.append(" FinTotSchdPaid, FinFee, FinWaiver, FinRefund");
		selectSql.append(" From FinRepayDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where FinReference=:FinReference and FinPostDate=:FinPostDate and FinPaySeq=:FinPaySeq ");
		selectSql.append(" and FinRpyFor= :FinRpyFor");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		RowMapper<FinanceRepayments> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceRepayments.class);

	
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	@Override
	public List<FinanceRepayments> getFinRepayListByFinRef(String finReference,String type) {
		logger.debug("Entering");
		FinanceRepayments financeRepayments = new FinanceRepayments();
		financeRepayments.setFinReference(finReference);
		StringBuilder selectSql = new StringBuilder("select FinReference, FinPostDate, FinRpyFor, FinPaySeq,");
		selectSql.append(" FinRpyAmount, FinSchdDate, FinValueDate, FinBranch,");
	    selectSql.append(" FinType, FinCustID, FinSchdPriPaid, FinSchdPftPaid,");
		selectSql.append(" FinTotSchdPaid, FinFee, FinWaiver, FinRefund");
		selectSql.append(" From FinRepayDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where FinReference=:FinReference ");
 
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		RowMapper<FinanceRepayments> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceRepayments.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails getError(String errorId, String finref, String fintype, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = String.valueOf(finref);
		parms[0][0] = PennantJavaUtil.getLabel("label_AccountSetid") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]), userLanguage);
	}


}
