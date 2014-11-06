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

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.RepayScheduleDetail;
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
	 * Generate Finance Pay Sequence
	 */
	public long getFinancePaySeq(FinanceRepayments financeRepayments){
		logger.debug("Entering");
		long repaySeq =0; 

		StringBuilder selectSql = new StringBuilder(" Select MAX(FinPaySeq) FROM FinRepayDetails");
		selectSql.append(" where FinReference=:FinReference AND  FinSchdDate=:FinSchdDate AND FinRpyFor=:FinRpyFor");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		repaySeq = this.namedParameterJdbcTemplate.queryForLong(selectSql.toString(), beanParameters);

		repaySeq = repaySeq + 1;
		logger.debug("Leaving");
		return repaySeq;
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
		if (financeRepayments.getId() == Long.MIN_VALUE || financeRepayments.getId()==0) {
			financeRepayments.setFinPaySeq(getFinancePaySeq(financeRepayments));
		}
		
		StringBuilder insertSql = new StringBuilder(" Insert Into FinRepayDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, FinSchdDate, FinRpyFor, FinPaySeq,LinkedTranId,");
		insertSql.append(" FinRpyAmount, FinPostDate , FinValueDate, FinBranch,");
		insertSql.append(" FinType, FinCustID, FinSchdPriPaid, FinSchdPftPaid,");
		insertSql.append(" FinTotSchdPaid, FinFee, FinWaiver, FinRefund) Values(");
		insertSql.append(" :FinReference, :FinSchdDate, :FinRpyFor, :FinPaySeq,:LinkedTranId,");
		insertSql.append(" :FinRpyAmount, :FinPostDate, :FinValueDate, :FinBranch,");
		insertSql.append(" :FinType, :FinCustID, :FinSchdPriPaid, :FinSchdPftPaid,");
		insertSql.append(" :FinTotSchdPaid,:FinFee, :FinWaiver, :FinRefund)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return financeRepayments.getId();
	}
	
	@Override
	public List<FinanceRepayments> getFinRepayListByFinRef(String finReference,boolean isRpyCancelProc, String type) {
		logger.debug("Entering");
		
		List<FinanceRepayments> repaymentList = new ArrayList<FinanceRepayments>();
		
		FinanceRepayments financeRepayments = new FinanceRepayments();
		financeRepayments.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder(" Select T1.FinReference, T1.FinPostDate, T1.FinRpyFor, T1.FinPaySeq,");
		selectSql.append(" T1.FinRpyAmount, T1.FinSchdDate, T1.FinValueDate, T1.FinBranch,");
	    selectSql.append(" T1.FinType, T1.FinCustID, T1.FinSchdPriPaid, T1.FinSchdPftPaid,");
		selectSql.append(" T1.FinTotSchdPaid, T1.FinFee, T1.FinWaiver, T1.FinRefund");
		if(isRpyCancelProc){
			selectSql.append(" ,T1.LinkedTranId");
		}
		selectSql.append(" From FinRepayDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append("  AS T1 where T1.FinReference=:FinReference ");
		if(isRpyCancelProc){
			selectSql.append(" AND T1.LinkedTranId = (Select MAX(T2.LinkedTranId) from FinRepayDetails T2  " );
			selectSql.append(" Where T1.FinReference = T2.FinReference) " );
			selectSql.append(" AND T1.LinkedTranId != 0  ORDER BY T1.FinSchdDate DESC " );
		}
 
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		RowMapper<FinanceRepayments> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceRepayments.class);

		logger.debug("Leaving");
		repaymentList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		
		if(repaymentList == null || repaymentList.isEmpty()){
			selectSql = new StringBuilder(" Select T1.FinReference, T1.FinPostDate, T1.FinRpyFor, T1.FinPaySeq,");
			selectSql.append(" T1.FinRpyAmount, T1.FinSchdDate, T1.FinValueDate, T1.FinBranch,");
		    selectSql.append(" T1.FinType, T1.FinCustID, T1.FinSchdPriPaid, T1.FinSchdPftPaid,");
			selectSql.append(" T1.FinTotSchdPaid, T1.FinFee, T1.FinWaiver, T1.FinRefund");
			if(isRpyCancelProc){
				selectSql.append(" ,T1.LinkedTranId");
			}
			selectSql.append(" From FinRepayDetails");
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append("  AS T1 where T1.FinReference=:FinReference ");
			if(isRpyCancelProc){
				selectSql.append(" AND T1.FinPostDate = (Select MAX(T2.FinPostDate) from FinRepayDetails T2  " );
				selectSql.append(" Where T1.FinReference = T2.FinReference) " );
				selectSql.append(" AND T1.LinkedTranId = 0  ORDER BY T1.FinSchdDate DESC " );
			}
	 
			logger.debug("selectSql: " + selectSql.toString());
			logger.debug("Leaving");
			repaymentList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		}
		
		return repaymentList;
	}

	@Override
    public void deleteRpyDetailbyLinkedTranId(long linkedTranId, String finReference) {
		logger.debug("Entering");
		
		FinanceRepayments financeRepayments = new FinanceRepayments();
		financeRepayments.setFinReference(finReference);
		financeRepayments.setLinkedTranId(linkedTranId);
		
		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayDetails");
		deleteSql.append(" where LinkedTranId=:LinkedTranId AND FinReference =:FinReference ");
 
		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
    }

	@Override
    public void deleteRpyDetailbyMaxPostDate(Date finPostDate, String finReference) {
		logger.debug("Entering");
		
		FinanceRepayments financeRepayments = new FinanceRepayments();
		financeRepayments.setFinReference(finReference);
		financeRepayments.setFinPostDate(finPostDate);
		
		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayDetails");
		deleteSql.append(" where FinPostDate=:FinPostDate AND FinReference=:FinReference ");
 
		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
    }

	
	@Override
    public FinRepayHeader getFinRepayHeader(String finReference, String type) {
		logger.debug("Entering");
		
		FinRepayHeader header = new FinRepayHeader();
		header.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("Select FinReference , ValueDate , FinEvent , RepayAmount , PriAmount , PftAmount , TotalRefund , " );
		selectSql.append(" TotalWaiver , InsRefund ,RepayAccountId , EarlyPayEffMtd , EarlyPayDate, SchdRegenerated, LinkedTranId");
		selectSql.append(" From FinRepayHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<FinRepayHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinRepayHeader.class);

		try {
			header = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			header = null;
		}
		logger.debug("Leaving");
		return header;    }
	
	
	@Override
    public FinRepayHeader getFinRepayHeader(String finReference, long linkedTranId, String type) {
		logger.debug("Entering");
		
		FinRepayHeader header = new FinRepayHeader();
		header.setFinReference(finReference);
		header.setLinkedTranId(linkedTranId);

		StringBuilder selectSql = new StringBuilder("Select ValueDate, FinEvent From FinRepayHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where FinReference =:FinReference AND LinkedTranId = :LinkedTranId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<FinRepayHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinRepayHeader.class);
		
		try {
			header = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			header = null;
		}
		logger.debug("Leaving");
		return header;    
	}

	@Override
    public void saveFinRepayHeader(FinRepayHeader finRepayHeader, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder(" Insert Into FinRepayHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference , ValueDate , FinEvent , RepayAmount , PriAmount , PftAmount , TotalRefund , " );
		insertSql.append(" TotalWaiver , InsRefund ,RepayAccountId , EarlyPayEffMtd  ,EarlyPayDate, SchdRegenerated, LinkedTranId) " );
		insertSql.append(" Values( :FinReference , :ValueDate , :FinEvent , :RepayAmount , :PriAmount , :PftAmount , :TotalRefund , " );
		insertSql.append(" :TotalWaiver , :InsRefund , :RepayAccountId , :EarlyPayEffMtd , :EarlyPayDate, :SchdRegenerated, :LinkedTranId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finRepayHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
    }

	@SuppressWarnings("serial")
    @Override
    public void updateFinRepayHeader(FinRepayHeader finRepayHeader, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinRepayHeader");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinReference=:FinReference , ValueDate=:ValueDate , FinEvent=:FinEvent , RepayAmount=:RepayAmount , " );
		updateSql.append(" PriAmount=:PriAmount , PftAmount=:PftAmount , TotalRefund=:TotalRefund , " );
		updateSql.append(" TotalWaiver=:TotalWaiver , InsRefund=:InsRefund ,RepayAccountId=:RepayAccountId , EarlyPayEffMtd=:EarlyPayEffMtd , " );
		updateSql.append(" EarlyPayDate=:EarlyPayDate, SchdRegenerated=:SchdRegenerated , LinkedTranId=:LinkedTranId");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finRepayHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", finRepayHeader.getFinReference(),PennantConstants.default_Language);
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
 
    }

	@Override
    public void deleteFinRepayHeader(FinRepayHeader finRepayHeader, String type) {
		logger.debug("Entering");
		
		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where FinReference=:FinReference ");
 
		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finRepayHeader);

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
    }
	
	@Override
    public void deleteFinRepayHeaderByTranId(String finReference, long linkedTranId, String type) {
		logger.debug("Entering");
		
		FinRepayHeader header = new FinRepayHeader();
		header.setFinReference(finReference);
		header.setLinkedTranId(linkedTranId);
		
		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where FinReference=:FinReference AND LinkedTranId=:LinkedTranId ");
 
		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
    }


	@Override
    public List<RepayScheduleDetail> getRpySchdList(String finReference, String type) {
		logger.debug("Entering");
		
		RepayScheduleDetail scheduleDetail = new RepayScheduleDetail();
		scheduleDetail.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder(" Select FinReference , SchDate , SchdFor , ProfitSchdBal , PrincipalSchdBal , " );
		selectSql.append(" ProfitSchd , ProfitSchdPaid , PrincipalSchd , PrincipalSchdPaid , " );
		selectSql.append(" ProfitSchdPayNow , PrincipalSchdPayNow , PenaltyAmt , DaysLate , MaxWaiver , AllowRefund , AllowWaiver , " );
		selectSql.append(" RefundReq , WaivedAmt , RepayBalance");
		selectSql.append(" From FinRepayScheduleDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where FinReference=:FinReference ");
 
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scheduleDetail);
		RowMapper<RepayScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RepayScheduleDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }

	@Override
    public void saveRpySchdList(List<RepayScheduleDetail> repaySchdList, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into FinRepayScheduleDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference , SchDate , SchdFor , LinkedTranId, ProfitSchdBal , PrincipalSchdBal , " );
		insertSql.append(" ProfitSchdPayNow , PrincipalSchdPayNow , PenaltyAmt , DaysLate , MaxWaiver , AllowRefund , AllowWaiver , " );
		insertSql.append(" ProfitSchd , ProfitSchdPaid , PrincipalSchd , PrincipalSchdPaid , " );
		insertSql.append(" RefundReq , WaivedAmt , RepayBalance )");
		insertSql.append(" Values(:FinReference , :SchDate , :SchdFor , :LinkedTranId , :ProfitSchdBal , :PrincipalSchdBal , " );
		insertSql.append(" :ProfitSchdPayNow , :PrincipalSchdPayNow , :PenaltyAmt , :DaysLate , :MaxWaiver , :AllowRefund , :AllowWaiver , " );
		insertSql.append(" :ProfitSchd , :ProfitSchdPaid , :PrincipalSchd , :PrincipalSchdPaid  , " );
		insertSql.append(" :RefundReq , :WaivedAmt , :RepayBalance )");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(repaySchdList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

    }

	@Override
    public void deleteRpySchdList(String finReference, String type) {
		logger.debug("Entering");
		
		RepayScheduleDetail scheduleDetail = new RepayScheduleDetail();
		scheduleDetail.setFinReference(finReference);
		
		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayScheduleDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where FinReference=:FinReference ");
 
		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scheduleDetail);

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
    }
	
	@Override
    public void deleteFinRepaySchListByTranId(String finReference,long linkedTranId, String type) {
		logger.debug("Entering");
		
		RepayScheduleDetail scheduleDetail = new RepayScheduleDetail();
		scheduleDetail.setFinReference(finReference);
		scheduleDetail.setLinkedTranId(linkedTranId);
		
		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayScheduleDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where FinReference=:FinReference AND LinkedTranId=:LinkedTranId ");
 
		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scheduleDetail);

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
    }
	
	/**
	 * Get Total Repayment Profit Amount till Date
	 */
	@Override
	public BigDecimal getPaidPft(String finReference , Date finPostDate){
		logger.debug("Entering");
		
		FinanceRepayments repayment = new FinanceRepayments();
		repayment.setFinReference(finReference);
		repayment.setFinPostDate(finPostDate);

		StringBuilder selectSql = new StringBuilder(" SELECT SUM(FinSchdPftPaid) FROM FinRepayDetails ");
		selectSql.append(" where FinReference=:FinReference AND  FinPostDate < :FinPostDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayment);
		BigDecimal totalPftPaid = new BigDecimal(this.namedParameterJdbcTemplate.queryForLong(selectSql.toString(), beanParameters));

		logger.debug("Leaving");
		return totalPftPaid;
	}
	
	private ErrorDetails getError(String errorId, String finReference, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
		        parms[0], parms[1]), userLanguage);
	}
	
}
