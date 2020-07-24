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
 * FileName    		:  FinFeeReceiptDAOImpl.java                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  1-06-2017    														*
 *                                                                  						*
 * Modified Date    :  1-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 1-06-2017       Pennant	                 0.1                                            * 
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

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.OverdueTaxMovement;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinFeeReceipt model</b> class.<br>
 * 
 */
public class FinODAmzTaxDetailDAOImpl extends SequenceDao<FinODAmzTaxDetail> implements FinODAmzTaxDetailDAO {
	private static Logger logger = Logger.getLogger(FinODAmzTaxDetailDAOImpl.class);

	public FinODAmzTaxDetailDAOImpl() {
		super();
	}

	/**
	 * This method insert new Records into FinODAmzTaxDetail or FinODAmzTaxDetail_Temp.
	 * 
	 * save Goods Details
	 * 
	 * @param Goods
	 *            Details (FinODAmzTaxDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(FinODAmzTaxDetail finODAmzTaxDetail) {
		logger.debug("Entering");

		if (finODAmzTaxDetail.getTaxSeqId() == Long.MIN_VALUE || finODAmzTaxDetail.getTaxSeqId() == 0) {
			finODAmzTaxDetail.setTaxSeqId(getNextValue("SeqFinODAmzTaxDetail"));
			logger.debug("get NextID:" + finODAmzTaxDetail.getTaxSeqId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into FinODAmzTaxDetail");
		insertSql.append(
				" (TaxSeqId , FinReference, ValueDate , PostDate, TaxFor, Amount, TaxType , CGST , SGST , UGST , IGST , TotalGST, PaidAmount, WaivedAmount, InvoiceID)");
		insertSql.append(
				" Values( :TaxSeqId , :FinReference, :ValueDate , :PostDate, :TaxFor, :Amount, :TaxType , :CGST , :SGST , :UGST , :IGST , :TotalGST, :PaidAmount, :WaivedAmount, :InvoiceID)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODAmzTaxDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return finODAmzTaxDetail.getTaxSeqId();
	}

	@Override
	public void saveTaxReceivable(FinTaxReceivable finTaxReceivable) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder();

		insertSql.append(" INSERT INTO FinTaxReceivable");
		insertSql.append(" (FinReference, TaxFor, ReceivableAmount, CGST, IGST, UGST, SGST, CESS ) ");
		insertSql.append(" VALUES (:FinReference, :TaxFor, :ReceivableAmount, :CGST, :IGST, :UGST, :SGST, :CESS) ");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTaxReceivable);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public FinTaxReceivable getFinTaxReceivable(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		FinTaxReceivable taxReceivable = new FinTaxReceivable();
		taxReceivable.setFinReference(finReference);
		taxReceivable.setTaxFor(type);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinReference, TaxFor, ReceivableAmount, CGST, IGST, UGST, SGST, CESS ");
		selectSql.append(" FROM FinTaxReceivable");
		selectSql.append(" WHERE  FinReference = :FinReference AND TaxFor=:TaxFor ");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taxReceivable);
		RowMapper<FinTaxReceivable> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinTaxReceivable.class);

		try {
			taxReceivable = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			taxReceivable = null;
		}

		logger.debug(Literal.LEAVING);
		return taxReceivable;
	}

	@Override
	public void updateTaxReceivable(FinTaxReceivable taxReceivable) {
		logger.debug(Literal.ENTERING);

		StringBuilder updateSql = new StringBuilder("Update FinTaxReceivable");
		updateSql.append(
				" Set ReceivableAmount = :ReceivableAmount, CGST = :CGST, IGST = :IGST, UGST = :UGST, SGST = :SGST, CESS = :CESS ");
		updateSql.append(" Where FinReference =:FinReference AND TaxFor=:TaxFor ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taxReceivable);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveTaxIncome(FinTaxIncomeDetail finTaxIncomeDetail) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder();

		insertSql.append(" INSERT INTO FinTaxIncomeDetail ");
		insertSql.append(" (RepayID, TaxFor, ReceivedAmount, CGST, IGST, UGST, SGST, CESS ) ");
		insertSql.append(" VALUES (:RepayID, :TaxFor, :ReceivedAmount, :CGST, :IGST, :UGST, :SGST, :CESS) ");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTaxIncomeDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public FinTaxIncomeDetail getFinTaxIncomeDetail(long repayID, String type) {
		logger.debug(Literal.ENTERING);

		FinTaxIncomeDetail taxIncomeDetail = new FinTaxIncomeDetail();
		taxIncomeDetail.setRepayID(repayID);
		taxIncomeDetail.setTaxFor(type);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select RepayID, TaxFor, ReceivedAmount, CGST, IGST, UGST, SGST, CESS ");
		selectSql.append(" FROM FinTaxIncomeDetail ");
		selectSql.append(" WHERE  RepayID = :RepayID AND TaxFor=:TaxFor ");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taxIncomeDetail);
		RowMapper<FinTaxIncomeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinTaxIncomeDetail.class);

		try {
			taxIncomeDetail = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			taxIncomeDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return taxIncomeDetail;
	}

	@Override
	public boolean isDueCreatedForDate(String finReference, Date valueDate, String taxFor) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("ValueDate", valueDate);
		source.addValue("TaxFor", taxFor);

		StringBuilder selectSql = new StringBuilder(" Select COUNT(*)  From FinODAmzTaxDetail");
		selectSql.append(" Where FinReference = :FinReference AND ValueDate= :ValueDate AND TaxFor = :TaxFor ");

		logger.trace(Literal.SQL + selectSql.toString());

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			recordCount = 0;
		}

		if (recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<FinODAmzTaxDetail> getFinODAmzTaxDetail(String finReference) {
		logger.debug(Literal.ENTERING);

		FinODAmzTaxDetail finODAmzTaxDetail = new FinODAmzTaxDetail();
		finODAmzTaxDetail.setFinReference(finReference);

		List<FinODAmzTaxDetail> finODAmzTaxDetailList;

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" Select TaxSeqId, FinReference, ValueDate, TaxFor, Amount, TaxType, TotalGST,PaidAmount, WaivedAmount, InvoiceID ");
		selectSql.append(" FROM FinODAmzTaxDetail Where FinReference =:FinReference ");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODAmzTaxDetail);
		RowMapper<FinODAmzTaxDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinODAmzTaxDetail.class);

		try {
			finODAmzTaxDetailList = jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finODAmzTaxDetailList = null;
		}
		logger.debug(Literal.LEAVING);

		return finODAmzTaxDetailList;
	}

	@Override
	public List<FinODAmzTaxDetail> getODTaxList(String finReference) {
		logger.debug(Literal.ENTERING);

		FinODAmzTaxDetail finODAmzTaxDetail = new FinODAmzTaxDetail();
		finODAmzTaxDetail.setFinReference(finReference);

		List<FinODAmzTaxDetail> finODAmzTaxDetailList;

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" Select TaxSeqId, FinReference, ValueDate, TaxFor, Amount, TaxType, TotalGST,PaidAmount, WaivedAmount, InvoiceID ");
		selectSql.append(
				" FROM FinODAmzTaxDetail Where FinReference =:FinReference AND (Amount - PaidAmount - WaivedAmount) > 0 ");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODAmzTaxDetail);
		RowMapper<FinODAmzTaxDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinODAmzTaxDetail.class);

		try {
			finODAmzTaxDetailList = jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finODAmzTaxDetailList = null;
		}
		logger.debug(Literal.LEAVING);

		return finODAmzTaxDetailList;
	}

	@Override
	public List<FinTaxIncomeDetail> getFinTaxIncomeList(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		FinTaxIncomeDetail taxIncomeDetail = new FinTaxIncomeDetail();
		taxIncomeDetail.setFinReference(finReference);
		taxIncomeDetail.setTaxFor(type);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" Select T.RepayID, H.ValueDate PostDate, D.ReceivedDate ValueDate,  T.TaxFor, T.ReceivedAmount, ");
		selectSql.append(" T.CGST, T.IGST, T.UGST, T.SGST, T.CESS  FROM FinTaxIncomeDetail T ");
		selectSql.append(" INNER JOIN FInRepayHeader H ON T.RepayID = H.RepayID ");
		selectSql.append(
				" INNER JOIN FinReceiptDetail D ON H.ReceiptSeqID = D.ReceiptSeqID AND D.Status NOT IN ('B', 'C') ");
		selectSql.append(" WHERE H.FinReference  = :FinReference AND T.TaxFor = :TaxFor ");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taxIncomeDetail);
		RowMapper<FinTaxIncomeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinTaxIncomeDetail.class);

		List<FinTaxIncomeDetail> incomeList = null;
		try {
			incomeList = jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			incomeList = null;
		}

		logger.debug(Literal.LEAVING);
		return incomeList;
	}

	@Override
	public void updateODTaxDueList(List<FinODAmzTaxDetail> updateDueList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinODAmzTaxDetail SET ");
		updateSql.append(" PaidAmount=:PaidAmount, WaivedAmount=:WaivedAmount ");
		updateSql.append(" Where FinReference =:FinReference AND valueDate = :valueDate AND TaxFor=:TaxFor ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(updateDueList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void saveTaxList(List<OverdueTaxMovement> taxMovements) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into OverdueTaxMovements");
		insertSql.append(
				" (InvoiceID, ValueDate, SchDate, TaxFor, FinReference, PaidAmount, WaivedAmount, TaxHeaderId )");
		insertSql.append(
				" Values(:InvoiceID, :ValueDate, :SchDate, :TaxFor, :FinReference, :PaidAmount, :WaivedAmount, :TaxHeaderId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(taxMovements.toArray());
		try {
			this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		}
		logger.debug("Leaving");
	}

}