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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.OverdueTaxMovement;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinFeeReceipt model</b> class.<br>
 * 
 */
public class FinODAmzTaxDetailDAOImpl extends SequenceDao<FinODAmzTaxDetail> implements FinODAmzTaxDetailDAO {
	private static Logger logger = LogManager.getLogger(FinODAmzTaxDetailDAOImpl.class);

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
	public long save(FinODAmzTaxDetail oatd) {
		if (oatd.getTaxSeqId() == Long.MIN_VALUE || oatd.getTaxSeqId() == 0) {
			oatd.setTaxSeqId(getNextValue("SeqFinODAmzTaxDetail"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into FinODAmzTaxDetail(");
		sql.append("TaxSeqId, FinReference, ValueDate, PostDate, TaxFor, Amount, TaxType");
		sql.append(", CGST, SGST, UGST, IGST, TotalGST, PaidAmount, WaivedAmount, InvoiceID)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, oatd.getTaxSeqId());
			ps.setString(index++, oatd.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(oatd.getValueDate()));
			ps.setDate(index++, JdbcUtil.getDate(oatd.getPostDate()));
			ps.setString(index++, oatd.getTaxFor());
			ps.setBigDecimal(index++, oatd.getAmount());
			ps.setString(index++, oatd.getTaxType());
			ps.setBigDecimal(index++, oatd.getCGST());
			ps.setBigDecimal(index++, oatd.getSGST());
			ps.setBigDecimal(index++, oatd.getUGST());
			ps.setBigDecimal(index++, oatd.getIGST());
			ps.setBigDecimal(index++, oatd.getTotalGST());
			ps.setBigDecimal(index++, oatd.getPaidAmount());
			ps.setBigDecimal(index++, oatd.getWaivedAmount());
			ps.setLong(index, JdbcUtil.getLong(oatd.getInvoiceID()));
		});

		return oatd.getTaxSeqId();
	}

	@Override
	public void saveTaxReceivable(FinTaxReceivable tr) {
		StringBuilder sql = new StringBuilder();

		sql.append("INSERT INTO FinTaxReceivable");
		sql.append(" (FinReference, TaxFor, ReceivableAmount, CGST, IGST, UGST, SGST, CESS)");
		sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?) ");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, tr.getFinReference());
			ps.setString(index++, tr.getTaxFor());
			ps.setBigDecimal(index++, tr.getReceivableAmount());
			ps.setBigDecimal(index++, tr.getCGST());
			ps.setBigDecimal(index++, tr.getIGST());
			ps.setBigDecimal(index++, tr.getUGST());
			ps.setBigDecimal(index++, tr.getSGST());
			ps.setBigDecimal(index, tr.getCESS());

		});
	}

	@Override
	public FinTaxReceivable getFinTaxReceivable(String finReference, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, TaxFor, ReceivableAmount, CGST, IGST, UGST, SGST, CESS");
		sql.append(" from FinTaxReceivable");
		sql.append(" WHERE  FinReference = ? and TaxFor= ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, type },
					(rs, rowNum) -> {
						FinTaxReceivable tr = new FinTaxReceivable();

						tr.setFinReference(rs.getString("FinReference"));
						tr.setTaxFor(rs.getString("TaxFor"));
						tr.setReceivableAmount(rs.getBigDecimal("ReceivableAmount"));
						tr.setCGST(rs.getBigDecimal("CGST"));
						tr.setIGST(rs.getBigDecimal("IGST"));
						tr.setUGST(rs.getBigDecimal("UGST"));
						tr.setSGST(rs.getBigDecimal("SGST"));
						tr.setCESS(rs.getBigDecimal("CESS"));

						return tr;
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in FinTaxReceivable table for the specified FinReference >> {} and tyoe{}",
					finReference, type, finReference);
		}

		return null;
	}

	@Override
	public void updateTaxReceivable(FinTaxReceivable tr) {
		StringBuilder sql = new StringBuilder("Update FinTaxReceivable");
		sql.append(" Set ReceivableAmount = ?, CGST = ?, IGST = ?, UGST = ?, SGST = ?, CESS = ? ");
		sql.append(" Where FinReference = ? and TaxFor= ?");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, tr.getReceivableAmount());
			ps.setBigDecimal(index++, tr.getCGST());
			ps.setBigDecimal(index++, tr.getIGST());
			ps.setBigDecimal(index++, tr.getUGST());
			ps.setBigDecimal(index++, tr.getSGST());
			ps.setBigDecimal(index++, tr.getCESS());
			ps.setString(index++, tr.getFinReference());
			ps.setString(index, tr.getTaxFor());

		});
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
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Count(*)");
		sql.append(" From FinODAmzTaxDetail");
		sql.append(" Where FinReference = ? and ValueDate= ? and TaxFor = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			Object[] object = new Object[] { finReference, JdbcUtil.getDate(valueDate), taxFor };
			return this.jdbcOperations.queryForObject(sql.toString(), object, Integer.class) > 0 ? true : false;
		} catch (EmptyResultDataAccessException e) {
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