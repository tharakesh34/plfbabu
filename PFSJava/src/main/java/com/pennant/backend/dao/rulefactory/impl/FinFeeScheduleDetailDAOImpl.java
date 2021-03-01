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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  FinFeeCharges.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  10-06-2014    
 *                                                                  
 * Modified Date    :  10-06-2014    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-06-2014       PENNANT TECHONOLOGIES	                 0.1                            * 
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

package com.pennant.backend.dao.rulefactory.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinFeeScheduleDetailDAOImpl extends BasicDao<FeeRule> implements FinFeeScheduleDetailDAO {
	private static Logger logger = LogManager.getLogger(FinFeeScheduleDetailDAOImpl.class);

	public FinFeeScheduleDetailDAOImpl() {
		super();
	}

	/**
	 * Method for saving Fee schedule Details list
	 */
	@Override
	public void saveFeeScheduleBatch(List<FinFeeScheduleDetail> feeScheduleList, boolean isWIF, String tableType) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		if (isWIF) {
			insertSql.append(" INSERT INTO WIFFinFeeScheduleDetail");
		} else {
			insertSql.append(" INSERT INTO FinFeeScheduleDetail");
		}
		insertSql.append(StringUtils.trimToEmpty(tableType));
		insertSql.append(
				" (FeeID, SchDate, SchAmount, PaidAmount, OsAmount, WaiverAmount, WriteoffAmount, CGST, SGST, UGST, IGST) ");
		insertSql.append(
				" VALUES (:FeeID, :SchDate, :SchAmount, :PaidAmount, :OsAmount, :WaiverAmount, :WriteoffAmount, :CGST, :SGST, :UGST, :IGST) ");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(feeScheduleList.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for saving Fee schedule Details list
	 */
	@Override
	public void deleteFeeScheduleBatch(long feeId, boolean isWIF, String tableType) {
		logger.debug("Entering");

		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFeeID(feeId);

		StringBuilder deleteSql = new StringBuilder();
		if (isWIF) {
			deleteSql.append(" DELETE FROM WIFFinFeeScheduleDetail");
		} else {
			deleteSql.append(" DELETE FROM FinFeeScheduleDetail");
		}
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" WHERE FeeID = :FeeID ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Method for saving Fee schedule Details list
	 */
	@Override
	public void deleteFeeScheduleBatchByFinRererence(String finReference, boolean isWIF, String tableType) {
		logger.debug("Entering");

		MapSqlParameterSource parameter = null;
		StringBuilder selectSql = new StringBuilder();

		try {
			if (isWIF) {
				selectSql.append("Delete from WIFFINFEESCHEDULEDETAIL" + StringUtils.trimToEmpty(tableType));
				selectSql.append(
						" Where FeeId IN (Select FeeID from WIFFinFeeDetail" + StringUtils.trimToEmpty(tableType));
			} else {
				selectSql.append("Delete from FINFEESCHEDULEDETAIL" + StringUtils.trimToEmpty(tableType));
				selectSql
						.append(" Where FeeId IN (Select FeeID from FinFeeDetail" + StringUtils.trimToEmpty(tableType));
			}
			selectSql.append(" Where FinReference = :FinReference)");

			logger.debug("selectSql: " + selectSql.toString());

			parameter = new MapSqlParameterSource();
			parameter.addValue("FinReference", finReference);

			this.jdbcTemplate.update(selectSql.toString(), parameter);
		} catch (Exception e) {
			// logger.error("Exception: ", e);
		} finally {
			selectSql = null;
			parameter = null;
			logger.debug("Leaving");
		}
	}

	/**
	 * Method for Fetching Fee schedule Details list based upon Reference
	 */
	@Override
	public List<FinFeeScheduleDetail> getFeeScheduleByFeeID(long feeID, boolean isWIF, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FeeID, SchDate, SchAmount, PaidAmount, OsAmount, WaiverAmount");
		sql.append(", WriteoffAmount, CGST, SGST, UGST, IGST");

		if (isWIF) {
			sql.append(" from WIFFinFeeScheduleDetail");
		} else {
			sql.append(" from FinFeeScheduleDetail");
		}

		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where FeeID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, feeID);
				}
			}, new RowMapper<FinFeeScheduleDetail>() {
				@Override
				public FinFeeScheduleDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinFeeScheduleDetail fsd = new FinFeeScheduleDetail();

					fsd.setFeeID(rs.getLong("FeeID"));
					fsd.setSchDate(rs.getTimestamp("SchDate"));
					fsd.setSchAmount(rs.getBigDecimal("SchAmount"));
					fsd.setPaidAmount(rs.getBigDecimal("PaidAmount"));
					fsd.setOsAmount(rs.getBigDecimal("OsAmount"));
					fsd.setWaiverAmount(rs.getBigDecimal("WaiverAmount"));
					fsd.setWriteoffAmount(rs.getBigDecimal("WriteoffAmount"));
					fsd.setCGST(rs.getBigDecimal("CGST"));
					fsd.setSGST(rs.getBigDecimal("SGST"));
					fsd.setUGST(rs.getBigDecimal("UGST"));
					fsd.setIGST(rs.getBigDecimal("IGST"));

					return fsd;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * Method for Fetching Fee schedule Details list based upon Reference
	 */
	@Override
	public List<FinFeeScheduleDetail> getFeeScheduleByFinID(List<Long> feeID, boolean isWIF, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(
				" SELECT FeeID, SchDate, SchAmount, PaidAmount, OsAmount, WaiverAmount, WriteoffAmount, CGST, SGST, UGST, IGST ");
		if (isWIF) {
			sql.append(" FROM WIFFinFeeScheduleDetail");
		} else {
			sql.append(" FROM FinFeeScheduleDetail");
		}
		sql.append(StringUtils.trimToEmpty(tableType));

		sql.append(" WHERE  FeeID IN(:FeeID) ");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FeeID", feeID);

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<FinFeeScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinFeeScheduleDetail.class);

		logger.debug(Literal.LEAVING);
		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		return new ArrayList<>();
	}

	@Override
	public void updateFeeSchdPaids(List<FinFeeScheduleDetail> updateFeeList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinFeeScheduleDetail");
		updateSql.append(
				" Set PaidAmount = PaidAmount + :PaidAmount, OsAmount = OsAmount - :PaidAmount, CGST = :CGST, SGST = :SGST, UGST= :UGST, IGST = :IGST ");
		updateSql.append(" Where FeeID =:FeeID AND SchDate=:SchDate ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(updateFeeList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<FinFeeScheduleDetail> getFeeScheduleBySchDate(String finReference, Date schDate) {
		logger.debug("Entering");
		FinFeeScheduleDetail feeSchd = new FinFeeScheduleDetail();
		feeSchd.setFinReference(finReference);
		feeSchd.setSchDate(schDate);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FeeID, SchDate, PaidAmount, CGST, SGST, UGST, IGST ");
		selectSql.append(" FROM FinFeeScheduleDetail_View ");
		selectSql.append(" WHERE FinReference=:FinReference AND SchDate=:SchDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeSchd);
		RowMapper<FinFeeScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinFeeScheduleDetail.class);
		List<FinFeeScheduleDetail> feeList = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return feeList;
	}

	@Override
	public List<FinFeeScheduleDetail> getFeeSchedules(String finReference, Date schDate) {
		logger.debug("Entering");
		FinFeeScheduleDetail feeSchd = new FinFeeScheduleDetail();
		feeSchd.setFinReference(finReference);
		feeSchd.setSchDate(schDate);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT FFSD.FeeID, FFSD.SchDate,FFSD.SchAmount,FFSD.PaidAmount,FFSD.WaiverAmount,FFSD.OsAmount,FFSD.WriteoffAmount, FFSD.CGST, FFSD.SGST, FFSD.UGST, FFSD.IGST ");
		selectSql.append(" FROM FinFeeScheduleDetail FFSD INNER JOIN FINFEEDETAIL FFD ON FFSD.FEEID = FFD.FEEID ");
		selectSql.append(" WHERE FFD.FinReference=:FinReference AND FFSD.SchDate=:SchDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeSchd);
		RowMapper<FinFeeScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinFeeScheduleDetail.class);
		List<FinFeeScheduleDetail> feeList = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return feeList;
	}

	@Override
	public List<FinFeeScheduleDetail> getFeeSchdTPost(String finReference, Date schDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ft.FeeTypeCode, fed.FinReference, fesd.SchDate, fesd.SchAmount, fesd.OsAmount");
		sql.append(", fesd.CGST, fesd.SGST, fesd.UGST, fesd.IGST");
		sql.append(", fesd.PaidAmount, fesd.WaiverAmount, fesd.WriteoffAmount");
		sql.append(" From FINFEESCHEDULEDETAIL fesd");
		sql.append(" Inner Join FINFEEDETAIL fed on fesd.FeeID = fed.FeeID");
		sql.append(" Inner Join FEETYPES ft on ft.FeeTypeID = fed.FeeTypeID");
		sql.append(" Where fed.FinReference= ? and fesd.SchDate = ?");

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, finReference);
			ps.setDate(index, JdbcUtil.getDate(schDate));
		}, (rs, rowNum) -> {
			FinFeeScheduleDetail fsd = new FinFeeScheduleDetail();

			fsd.setFeeTypeCode(rs.getString("FeeTypeCode"));
			fsd.setFinReference(rs.getString("FinReference"));
			fsd.setSchDate(rs.getTimestamp("SchDate"));
			fsd.setSchAmount(rs.getBigDecimal("SchAmount"));
			fsd.setOsAmount(rs.getBigDecimal("OsAmount"));
			fsd.setCGST(rs.getBigDecimal("CGST"));
			fsd.setSGST(rs.getBigDecimal("SGST"));
			fsd.setUGST(rs.getBigDecimal("UGST"));
			fsd.setIGST(rs.getBigDecimal("IGST"));
			fsd.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			fsd.setWaiverAmount(rs.getBigDecimal("WaiverAmount"));
			fsd.setWriteoffAmount(rs.getBigDecimal("WriteoffAmount"));

			return fsd;
		});
	}

	@Override
	public void updateFeePaids(List<FinFeeScheduleDetail> updateFeeList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinFeeScheduleDetail ");
		updateSql.append(
				" Set PaidAmount = :PaidAmount, WaiverAmount = :WaiverAmount, OsAmount = :OsAmount, CGST = :CGST, SGST = :SGST, UGST= :UGST, IGST = :IGST ");
		updateSql.append(" Where FeeID =:FeeID AND SchDate=:SchDate ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(updateFeeList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}
