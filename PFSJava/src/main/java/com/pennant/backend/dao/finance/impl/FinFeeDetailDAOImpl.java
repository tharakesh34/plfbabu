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
 * FileName    		:  FinFeeDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;


import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinFeeDetail model</b> class.<br>
 * 
 */

public class FinFeeDetailDAOImpl extends BasisNextidDaoImpl<FinFeeDetail> implements FinFeeDetailDAO {

	private static Logger logger = Logger.getLogger(FinFeeDetailDAOImpl.class);
	
	public FinFeeDetailDAOImpl() {
		super();
	}
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinFeeDetail 
	 * @return FinFeeDetail
	 */

	@Override
	public FinFeeDetail getFinFeeDetail() {
		logger.debug("Entering");
		
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinFeeDetail");
		FinFeeDetail finFeeDetail= new FinFeeDetail();
		if (workFlowDetails!=null){
			finFeeDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		
		return finFeeDetail;
	}


	/**
	 * This method get the module from method getFinFeeDetail() and set the new record flag as true and return FinFeeDetail()   
	 * @return FinFeeDetail
	 */


	@Override
	public FinFeeDetail getNewFinFeeDetail() {
		logger.debug("Entering");
		
		FinFeeDetail finFeeDetail = getFinFeeDetail();
		finFeeDetail.setNewRecord(true);
		logger.debug("Leaving");
		
		return finFeeDetail;
	}

	/**
	 * Fetch the Record  Goods Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinFeeDetail
	 */
	@Override
	public FinFeeDetail getFinFeeDetailById(FinFeeDetail finFeeDetail, boolean isWIF, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FeeID, FinReference, OriginationFee, FinEvent, FeeTypeID, FeeSeq, FeeOrder, CalculatedAmount, ActualAmount," );
		selectSql.append(" WaivedAmount, PaidAmount, FeeScheduleMethod, Terms, RemainingFee, PaymentRef, CalculationType, VasReference, Status," );
		selectSql.append(" RuleCode, FixedAmount, Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd," );
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId " );
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",FeeTypeCode,FeeTypeDesc ");
		}
		if (isWIF) {
			selectSql.append(" From WIFFinFeeDetail");
		} else {
			selectSql.append(" From FinFeeDetail");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE  FeeID = :FeeID ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		
		try {
			finFeeDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finFeeDetail = null;
		}
		
		logger.debug("Leaving");
		
		return finFeeDetail;
	}
	
	@Override
	public List<FinFeeDetail> getFinFeeDetailByFinRef(final String reference,boolean isWIF, String type) {
		logger.debug("Entering");
		
		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(reference);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FeeID, FinReference, OriginationFee, FinEvent, FeeTypeID, FeeSeq, FeeOrder, CalculatedAmount, ActualAmount," );
		selectSql.append(" WaivedAmount, PaidAmount, FeeScheduleMethod, Terms, RemainingFee, PaymentRef, CalculationType,VasReference,Status, " );
		selectSql.append(" RuleCode, FixedAmount, Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd," );
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId " );
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" ,FeeTypeCode,FeeTypeDesc ");
		}
		if (isWIF) {
			selectSql.append(" From WIFFinFeeDetail");
		} else {
			selectSql.append(" From FinFeeDetail");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		logger.debug("Leaving");
		
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	@Override
	public List<FinFeeDetail> getFinFeeDetailByFinRef(final String reference,boolean isWIF, String type, String finEvent) {
		logger.debug("Entering");
		
		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(reference);
		finFeeDetail.setFinEvent(finEvent);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FeeID, FinReference, OriginationFee, FinEvent, FeeTypeID, FeeSeq, FeeOrder, CalculatedAmount, ActualAmount," );
		selectSql.append(" WaivedAmount, PaidAmount, FeeScheduleMethod, Terms, RemainingFee, PaymentRef, CalculationType,VasReference,Status," );
		selectSql.append(" RuleCode, FixedAmount, Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd," );
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId " );
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" ,FeeTypeCode,FeeTypeDesc ");
		}
		if (isWIF) {
			selectSql.append(" From WIFFinFeeDetail");
		} else {
			selectSql.append(" From FinFeeDetail");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference And FinEvent = :FinEvent");
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		
		logger.debug("Leaving");
		
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	
	@Override
	public List<FinFeeDetail> getFinScheduleFees(final String reference,boolean isWIF, String type) {
		logger.debug("Entering");
		
		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(reference);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FeeID, FinReference, OriginationFee, FinEvent, FeeTypeID, FeeSeq, FeeOrder, CalculatedAmount, ActualAmount," );
		selectSql.append(" WaivedAmount, PaidAmount, FeeScheduleMethod, Terms, RemainingFee, PaymentRef, CalculationType,VasReference,Status," );
		selectSql.append(" RuleCode, FixedAmount, Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd," );
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId " );
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" ,FeeTypeCode,FeeTypeDesc ");
		}
		if (isWIF) {
			selectSql.append(" From WIFFinFeeDetail");
		} else {
			selectSql.append(" From FinFeeDetail");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference And FeeScheduleMethod IN ('STFI', 'STNI', 'STET', 'POSP')");
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		
		logger.debug("Leaving");
		
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * Method for Fetching Fee Details only Paid by Customer upfront
	 * @param reference
	 * @param type
	 * @return
	 */
	@Override
	public List<FinFeeDetail> getPaidFinFeeDetails(final String reference, String type) {
		logger.debug("Entering");
		
		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(reference);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FeeOrder, CalculatedAmount, ActualAmount, WaivedAmount, PaidAmount, RemainingFee, VasReference,Status " );
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" ,FeeTypeCode, FeeTypeDesc ");
		}
		selectSql.append(" From FinFeeDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference And PaidAmount > 0 ");
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		
		logger.debug("Leaving");
		
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * This method refresh the Record.
	 * @param finFeeDetail (FinFeeDetail)
 	 * @return void
	 */
	@Override
	public void refresh(FinFeeDetail finFeeDetail) {
		
	}
	
	/**
	 * This method Deletes the Record from the FinFeeDetail or FinFeeDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Goods Details by key LoanRefNumber
	 * 
	 * @param Goods Details (FinFeeDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinFeeDetail finFeeDetail,boolean isWIF,String type) {
		logger.debug("Entering");
		
		StringBuilder deleteSql = new StringBuilder();
		if (isWIF) {
			deleteSql.append("Delete From WIFFinFeeDetail");
		} else {
			deleteSql.append("Delete From FinFeeDetail");
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference = :FinReference and OriginationFee = :OriginationFee and FinEvent = :FinEvent and FeeTypeID = :FeeTypeID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinFeeDetail or FinFeeDetail_Temp.
	 *
	 * save Goods Details 
	 * 
	 * @param Goods Details (FinFeeDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(FinFeeDetail finFeeDetail,boolean isWIF,String type) {
		logger.debug("Entering");
		
		if (finFeeDetail.getFeeID() == Long.MIN_VALUE) {
			finFeeDetail.setFeeID(getNextidviewDAO().getNextId("SeqFinFeeDetail"));
			logger.debug("get NextID:" + finFeeDetail.getFeeID());
		}	
		
		StringBuilder insertSql = new StringBuilder();
		if (isWIF) {
			insertSql.append(" Insert Into WIFFinFeeDetail");
		} else {
			insertSql.append(" Insert Into FinFeeDetail");
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FeeID, FinReference, OriginationFee , FinEvent, FeeTypeID, FeeSeq, FeeOrder, CalculatedAmount, ActualAmount, " );
		insertSql.append(" WaivedAmount, PaidAmount, FeeScheduleMethod, Terms, RemainingFee, PaymentRef, CalculationType,VasReference,Status,");
		insertSql.append(" RuleCode, FixedAmount, Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values( :FeeID, :FinReference, :OriginationFee , :FinEvent, :FeeTypeID, :FeeSeq, :FeeOrder, :CalculatedAmount, :ActualAmount,");
		insertSql.append(" :WaivedAmount, :PaidAmount, :FeeScheduleMethod, :Terms, :RemainingFee, :PaymentRef, :CalculationType,:VasReference,:Status,");
		insertSql.append(" :RuleCode, :FixedAmount, :Percentage, :CalculateOn, :AlwDeviation, :MaxWaiverPerc, :AlwModifyFee, :AlwModifyFeeSchdMthd,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		
		return finFeeDetail.getFeeID();
	}
	
	/**
	 * This method updates the Record FinFeeDetail or FinFeeDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Goods Details by key LoanRefNumber and Version
	 * 
	 * @param Goods Details (FinFeeDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(FinFeeDetail finFeeDetail,boolean isWIF,String type) {
		logger.debug("Entering");
		
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		if (isWIF) {
			updateSql.append("Update WIFFinFeeDetail");
		} else {
			updateSql.append("Update FinFeeDetail");
		}
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append("  Set FeeSeq = :FeeSeq, FeeOrder = :FeeOrder, CalculatedAmount = :CalculatedAmount, ActualAmount = :ActualAmount,");
		updateSql.append("  WaivedAmount = :WaivedAmount, PaidAmount = :PaidAmount, FeeScheduleMethod = :FeeScheduleMethod, Terms = :Terms,");
		updateSql.append("  RemainingFee = :RemainingFee, PaymentRef = :PaymentRef, CalculationType = :CalculationType,VasReference=:VasReference, RuleCode = :RuleCode,");
		updateSql.append("  Status=:Status,FixedAmount = :FixedAmount, Percentage = :Percentage, CalculateOn = :CalculateOn, AlwDeviation = :AlwDeviation,");
		updateSql.append("  MaxWaiverPerc = :MaxWaiverPerc, AlwModifyFee = :AlwModifyFee, AlwModifyFeeSchdMthd = :AlwModifyFeeSchdMthd,");
		updateSql.append("  Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, ");
		updateSql.append("  RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append("  RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append("  Where FeeID = :FeeID ");
		
		/*if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}*/
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * This method updates the Record FinFeeDetail or FinFeeDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Goods Details by key LoanRefNumber and Version
	 * 
	 * @param Goods Details (FinFeeDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void statusUpdate(long feeID, String status, boolean isWIF,String type) {
		logger.debug("Entering");
		
		int recordCount = 0;
		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFeeID(feeID);
		finFeeDetail.setStatus(status);
		
		StringBuilder updateSql = new StringBuilder();
		if (isWIF) {
			updateSql.append("Update WIFFinFeeDetail");
		} else {
			updateSql.append("Update FinFeeDetail");
		}
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append("  Set Status=:Status Where FeeID = :FeeID ");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug("Leaving");
	}
	
	@Override
	public void deleteByFinRef(String loanReference, boolean isWIF, String tableType) {
		logger.debug("Entering");

		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(loanReference);

		StringBuilder deleteSql = new StringBuilder();
		if (isWIF) {
			deleteSql.append("Delete From WIFFinFeeDetail");
		} else {
			deleteSql.append("Delete From FinFeeDetail");
		}
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where FinReference = :FinReference ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void deleteServiceFeesByFinRef(String loanReference, boolean isWIF, String tableType) {
		logger.debug("Entering");
		
		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(loanReference);
		
		StringBuilder deleteSql = new StringBuilder();
		if (isWIF) {
			deleteSql.append("Delete From WIFFinFeeDetail");
		} else {
			deleteSql.append("Delete From FinFeeDetail");
		}
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where FinReference = :FinReference AND OriginationFee = 0");
		logger.debug("deleteSql: " + deleteSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	}
	

	@Override
	public int getFeeSeq(FinFeeDetail finFeeDetail, boolean isWIF, String type) {
		logger.debug("Entering");

		MapSqlParameterSource parameter = null;
		StringBuilder selectSql = new StringBuilder();
		int finSeq = 0;

		try {
			selectSql.append("Select Max(FeeSeq) From ");
			
			if (isWIF) {
				selectSql.append("WIFFinFeeDetail");
			} else {
				selectSql.append("FinFeeDetail");
			}
			
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" Where FinReference = :FinReference AND FinEvent = :FinEvent");
			
			logger.debug("selectSql: " + selectSql.toString());

			parameter = new MapSqlParameterSource();
			parameter.addValue("FinReference", finFeeDetail.getFinReference());
			parameter.addValue("FinEvent", finFeeDetail.getFinEvent());

			finSeq = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), parameter, Integer.class);
		} catch (Exception e) {
			//logger.error("Exception: ", e);
		} finally {
			selectSql = null;
			parameter = null;
			logger.debug("Leaving");
		}

		return finSeq;
	}
	
	
	@Override
    public FinanceSummary getTotalFeeCharges(FinanceSummary finSummary) {
		logger.debug("Entering");
		
		FinanceSummary summary = new FinanceSummary();
		summary.setFinReference(finSummary.getFinReference());
		
		StringBuilder selectSql = new StringBuilder(" select TotalFees,TotalCharges from " );
		selectSql.append(" (select SUM(PostAmount) TotalFees, Finreference from Postings  " );
		selectSql.append(" where FinReference=:FinReference  AND AmountType='F' and DrOrCr='C' Group by Finreference) A  " );
		selectSql.append(" inner join (select SUM(PostAmount) TotalCharges, Finreference from Postings " );
		selectSql.append(" where Finreference=:FinReference  AND AmountType='C' and DrOrCr='C' Group by Finreference) B " );
		selectSql.append(" on A.Finreference = B.Finreference " );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSummary);
		RowMapper<FinanceSummary> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceSummary.class);

		try {
			summary = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
			finSummary.setTotalCharges(summary.getTotalCharges());
			finSummary.setTotalFees(summary.getTotalFees());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			summary = null;
		}
		
		logger.debug("Leaving");
		
		return finSummary;
    }
	
	/**
	 * Fetch the Record  Goods Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinFeeDetail
	 */
	@Override
	public FinFeeDetail getVasFeeDetailById(String vasReference, boolean isWIF, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FeeID, FinReference, OriginationFee, FinEvent, FeeTypeID, FeeSeq, FeeOrder, CalculatedAmount, ActualAmount," );
		selectSql.append(" WaivedAmount, PaidAmount, FeeScheduleMethod, Terms, RemainingFee, PaymentRef, CalculationType, VasReference,Status," );
		selectSql.append(" RuleCode, FixedAmount, Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd," );
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId " );
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",FeeTypeCode,FeeTypeDesc ");
		}
		if (isWIF) {
			selectSql.append(" From WIFFinFeeDetail");
		} else {
			selectSql.append(" From FinFeeDetail");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE VasReference = :VasReference ");
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		
		MapSqlParameterSource parameter = new MapSqlParameterSource();
		parameter.addValue("VasReference", vasReference);
		
		FinFeeDetail finFeeDetail = null;
		try {
			finFeeDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), parameter, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finFeeDetail = null;
		}
		
		logger.debug("Leaving");
		
		return finFeeDetail;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}