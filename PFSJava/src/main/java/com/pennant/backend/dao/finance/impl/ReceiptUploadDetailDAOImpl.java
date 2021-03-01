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
 * FileName    		:  UploadHeaderDAOImpl.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2017       Pennant	                 0.1                                            * 
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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class ReceiptUploadDetailDAOImpl extends SequenceDao<ReceiptUploadDetail> implements ReceiptUploadDetailDAO {

	private static Logger logger = LogManager.getLogger(ReceiptUploadDetailDAOImpl.class);

	public ReceiptUploadDetailDAOImpl() {
		super();
	}

	/**
	 * 
	 * @param id
	 * @return list<ReceiptUploadDetail>
	 *
	 */
	@Override
	public List<ReceiptUploadDetail> getUploadReceiptDetails(long id, boolean getsucessrcdOnly) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT UPLOADHEADERID, UPLOADDETAILID, ROOTID, REFERENCE, RECEIPTPURPOSE, RECEIPTAMOUNT, ");
		selectSql.append(" ALLOCATIONTYPE, UPLOADSTATUS, RECEIVEDDATE, REASON");
		selectSql.append(" from RECEIPTUPLOADDETAILS ");
		selectSql.append(" Where uploadHeaderid = :uploadHeaderId");

		if (getsucessrcdOnly) {
			selectSql.append(" and UPLOADSTATUS = '" + PennantConstants.UPLOAD_STATUS_SUCCESS + "'");
		}
		selectSql.append(" order by UPLOADDETAILID");

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("uploadHeaderId", id);

		RowMapper<ReceiptUploadDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ReceiptUploadDetail.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public ReceiptUploadDetail getUploadReceiptDetail(long headerID, long detailID) {
		logger.debug("Entering");

		ReceiptUploadDetail rud = new ReceiptUploadDetail();
		rud.setUploadheaderId(headerID);
		rud.setUploadDetailId(detailID);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT UPLOADHEADERID, UPLOADDETAILID, ROOTID, REFERENCE, RECEIPTPURPOSE, RECEIPTAMOUNT, ");
		selectSql.append(" ALLOCATIONTYPE, UPLOADSTATUS, REASON, EXCESSADJUSTTO, EFFECTSCHDMETHOD, ");
		selectSql.append(" REMARKS, VALUEDATE, RECEIVEDDATE, RECEIPTMODE, FUNDINGAC, PAYMENTREF, FAVOURNUMBER, ");
		selectSql.append(
				" BANKCODE, CHEQUEACNO CHEQUENO, TRANSACTIONREF, STATUS, DEPOSITDATE, REALIZATIONDATE, INSTRUMENTDATE, ");
		selectSql.append(" EXTREFERENCE, SUBRECEIPTMODE, RECEIPTCHANNEL, RECEIVEDFROM, PANNUMBER, COLLECTIONAGENTID ");
		selectSql.append(" from RECEIPTUPLOADDETAILS ");
		selectSql.append(" Where UploadheaderId = :UploadheaderId AND UploadDetailId = :UploadDetailId");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(rud);
		RowMapper<ReceiptUploadDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ReceiptUploadDetail.class);

		try {
			rud = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			rud = null;
		}

		logger.debug("Leaving");
		return rud;
	}

	/**
	 * This method insert new Records into UploadHeader or UploadHeader_Temp.
	 * 
	 * save Promotion
	 * 
	 * @param Promotion
	 *            (promotion)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(ReceiptUploadDetail receiptUploadDetail) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();

		if (receiptUploadDetail.getUploadDetailId() == Long.MIN_VALUE) {
			receiptUploadDetail.setUploadDetailId(getNextValue("SeqReceiptUploadDetail"));
			logger.debug("get NextID:" + receiptUploadDetail.getUploadDetailId());
		}

		sql.append(" Insert Into ReceiptUploaddetails");
		sql.append(
				" (UploadheaderId, UploadDetailId, Reference, receiptPurpose, receiptamount, allocationType, uploadStatus, reason, ");
		sql.append(
				" ROOTID,EXCESSADJUSTTO,EFFECTSCHDMETHOD,REMARKS,VALUEDATE,RECEIVEDDATE,RECEIPTMODE,FUNDINGAC,PAYMENTREF,FAVOURNUMBER,BANKCODE, ");
		sql.append(
				" CHEQUEACNO,TRANSACTIONREF,STATUS,DEPOSITDATE,REALIZATIONDATE,INSTRUMENTDATE,EXTREFERENCE,SUBRECEIPTMODE,RECEIPTCHANNEL,RECEIVEDFROM,PANNUMBER,COLLECTIONAGENTID)");
		sql.append(
				" Values (:UploadheaderId, :UploadDetailId, :reference, :receiptPurpose, :receiptAmount, :allocationType, :uploadStatus, :reason,");
		sql.append(
				" :rootId,:excessAdjustTo,:effectSchdMethod,:remarks,:valueDate,:receivedDate,:receiptMode,:fundingAc,:paymentRef,:favourNumber,:bankCode, ");
		sql.append(
				" :chequeNo,:transactionRef,:status,:depositDate,:realizationDate,:instrumentDate,:extReference,:subReceiptMode,:receiptChannel,:receivedFrom,:panNumber,:collectionAgentId)");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptUploadDetail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");
		return receiptUploadDetail.getUploadDetailId();

	}

	@Override
	public void delete(long receiptHeaderId) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ReceiptUploaddetails");
		sql.append(" where UploadHeaderId = :UploadHeaderId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("UploadHeaderId", receiptHeaderId);

		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateStatus(ReceiptUploadDetail receiptUploadDetail) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource spMapSqlParameterSource = new MapSqlParameterSource();
		spMapSqlParameterSource.addValue("UploadDetailId", receiptUploadDetail.getUploadDetailId());
		spMapSqlParameterSource.addValue("uploadStatus", receiptUploadDetail.getUploadStatus());
		spMapSqlParameterSource.addValue("reason", receiptUploadDetail.getReason().length() > 1000
				? receiptUploadDetail.getReason().substring(0, 999) : receiptUploadDetail.getReason());
		spMapSqlParameterSource.addValue("ReceiptID", receiptUploadDetail.getReceiptId());

		StringBuilder updateSql = new StringBuilder("Update ReceiptUploaddetails");
		updateSql.append(" Set uploadStatus = :uploadStatus,  reason = :reason");
		updateSql.append(" Where UploadDetailId =:UploadDetailId");

		logger.debug("updateSql: " + updateSql.toString());

		try {
			this.jdbcTemplate.update(updateSql.toString(), spMapSqlParameterSource);
		} catch (DataAccessException e) {
			logger.error("EXception" + e.getMessage());
		}

		logger.debug(Literal.LEAVING);

	}

	/**
	 * update receipt id
	 * 
	 * @param uploadDetailId
	 * @param receiptID
	 */
	@Override
	public void updateReceiptId(long uploadDetailId, long receiptID) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource spMapSqlParameterSource = new MapSqlParameterSource();
		spMapSqlParameterSource.addValue("UploadDetailId", uploadDetailId);
		spMapSqlParameterSource.addValue("ReceiptID", receiptID);

		StringBuilder updateSql = new StringBuilder("Update ReceiptUploaddetails");
		updateSql.append(" Set ReceiptID = :ReceiptID");
		updateSql.append(" Where UploadDetailId =:UploadDetailId");

		logger.debug("updateSql: " + updateSql.toString());

		try {
			this.jdbcTemplate.update(updateSql.toString(), spMapSqlParameterSource);
		} catch (DataAccessException e) {
			logger.error("Error Message" + e.getMessage());
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * update reason message for all rejected files
	 * 
	 * @param id
	 * @param errorMsg
	 */
	@Override
	public void updateRejectStatusById(String id, String errorMsg) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource spMapSqlParameterSource = new MapSqlParameterSource();
		spMapSqlParameterSource.addValue("uploadheaderid", id);
		spMapSqlParameterSource.addValue("Reason", errorMsg);

		StringBuilder updateSql = new StringBuilder("Update ReceiptUploaddetails");
		updateSql.append(" Set Reason = :Reason");
		updateSql.append(" Where uploadheaderid =:uploadheaderid");

		logger.debug("updateSql: " + updateSql.toString());

		try {
			this.jdbcTemplate.update(updateSql.toString(), spMapSqlParameterSource);
		} catch (DataAccessException e) {
			logger.error("Error Message" + e.getMessage());
		}

		logger.debug(Literal.LEAVING);

	}

	/**
	 * 
	 * Get Loan Reference if it is present in Temp table
	 */
	@Override
	public String getLoanReferenc(String finReference, String receiptFileName) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" SELECT DISTINCT REFERENCE FROM RECEIPTUPLOADDETAILS ");
		selectSql.append(" WHERE UPLOADHEADERID IN (SELECT UploadHeaderId FROM RECEIPTUPLOADHEADER_view ");
		selectSql.append(" where FileName not in ( :FileName) and uploadprogress in ("
				+ ReceiptUploadConstants.RECEIPT_DEFAULT + "," + ReceiptUploadConstants.RECEIPT_DOWNLOADED + ") )");
		selectSql.append(
				" AND REFERENCE = :Reference and uploadstatus in ('" + PennantConstants.UPLOAD_STATUS_SUCCESS + "')");

		logger.trace(Literal.SQL + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		source.addValue("FileName", receiptFileName);

		String reference = null;

		try {
			reference = jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			reference = null;
		}

		logger.debug(Literal.LEAVING);
		return reference;

	}

	/**
	 * get list of receipt uploadid
	 * 
	 * @param receiptUploadHeaderId
	 * @param tableType
	 * @return
	 */
	public List<Long> getListofReceiptUploadDetails(long receiptUploadHeaderId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
		sqlParameterSource.addValue("UPLOADHEADERID", receiptUploadHeaderId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT UPLOADDETAILID ");

		selectSql.append(" From RECEIPTUPLOADDETAILS");
		selectSql.append(" where UPLOADHEADERID = :UPLOADHEADERID");

		logger.debug("selectSql: " + selectSql.toString());

		List<Long> receiptUploadIdList = new ArrayList<>();

		try {
			receiptUploadIdList = this.jdbcTemplate.queryForList(selectSql.toString(), sqlParameterSource, Long.class);
		} catch (DataAccessException e) {
			logger.debug("error :" + e);
			receiptUploadIdList = new ArrayList<>();

		}

		logger.debug(Literal.LEAVING);

		return receiptUploadIdList;

	}

}