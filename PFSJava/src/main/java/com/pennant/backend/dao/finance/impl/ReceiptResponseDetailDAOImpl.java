/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : UploadHeaderDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2017 * * Modified
 * Date : 17-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-12-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.ReceiptResponseDetailDAO;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class ReceiptResponseDetailDAOImpl extends SequenceDao<ReceiptUploadDetail> implements ReceiptResponseDetailDAO {

	private static Logger logger = LogManager.getLogger(ReceiptResponseDetailDAOImpl.class);

	public ReceiptResponseDetailDAOImpl() {
		super();
	}

	@Override
	public long saveReceiptResponseFileHeader(String fileName) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append(" INSERT INTO ReceiptBatchFileHeader");
		sql.append(" (ID, FileName, StartTime)");
		sql.append(" VALUES( :ID, :FileName, :StartTime)");

		long batchId = getNextValue("SeqReceiptResponse");

		source.addValue("ID", batchId);
		source.addValue("FileName", fileName);
		source.addValue("StartTime", SysParamUtil.getAppDate());

		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return batchId;
	}

	@Override
	public List<ReceiptUploadDetail> getReceiptResponseDetails() {

		logger.debug("Entering");

		ReceiptUploadDetail receiptUploadDetail = new ReceiptUploadDetail();
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select REFERENCE,RECEIPTPURPOSE,RECEIPTAMOUNT,ALLOCATIONTYPE,ID,EXCESSADJUSTTO,");
		selectSql.append(" EFFECTSCHDMETHOD,REMARKS,VALUEDATE, RECEIVEDDATE,RECEIPTMODE,FUNDINGAC,");
		selectSql.append(" PAYMENTREF,FAVOURNUMBER,BANKCODE,CHEQUEACNO as chequeNo,TRANSACTIONREF,STATUS,");
		selectSql.append(" DEPOSITDATE,REALIZATIONDATE,INSTRUMENTDATE");

		selectSql.append(" From Auto_ReceiptDetails");

		selectSql.append(" Where PICKUPFLAG = 0 and PICKUPBATCHID = -1 order by id");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptUploadDetail);
		RowMapper<ReceiptUploadDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(ReceiptUploadDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<UploadAlloctionDetail> getReceiptResponseAllocationDetails(String rootId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ID", rootId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select ALLOCATIONTYPE,REFERENCECODE,PAIDAMOUNT,WAIVEDAMOUNT");

		selectSql.append(" From Auto_ReceiptAllocations");

		selectSql.append(" Where ID = :ID");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<UploadAlloctionDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(UploadAlloctionDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public void updateReceiptResponseFileHeader(long batchId, int recordCount, int sCount, int fCount, String remarks) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder query = new StringBuilder();
		query.append(" UPDATE ReceiptBatchFileHeader SET EndTime = :EndTime, TotalRecords = :TotalRecords,");
		query.append(
				" SUCCESSRECORDS = :SucessRecords, FAILEDRECORDS = :FailedRecords, Remarks = :Remarks Where ID = :ID");

		source.addValue("EndTime", DateUtil.getSysDate());
		source.addValue("TotalRecords", recordCount);
		source.addValue("SucessRecords", sCount);
		source.addValue("FailedRecords", fCount);
		if (StringUtils.trimToNull(remarks) != null) {
			remarks = (remarks.length() >= 200) ? remarks.substring(0, 198) : remarks;
		}
		source.addValue("Remarks", remarks);
		source.addValue("ID", batchId);

		this.jdbcTemplate.update(query.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateReceiptResponseDetails(ReceiptUploadDetail receiptresponseDetail, long jobid) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder query = new StringBuilder();
		query.append(" UPDATE Auto_ReceiptDetails SET PICKUPFLAG = :PICKUPFLAG, PICKUPBATCHID = :PICKUPBATCHID,");
		query.append(" RESPONSESTATUS = :RESPONSESTATUS, ERRORMESSAGE = :ERRORMESSAGE, PICKUPDATE = :PICKUPDATE");
		query.append(" Where Id = :Id");

		source.addValue("PICKUPFLAG", 1);
		source.addValue("PICKUPBATCHID", jobid);
		source.addValue("RESPONSESTATUS", receiptresponseDetail.getProcessingStatus());

		source.addValue("ERRORMESSAGE", receiptresponseDetail.getReason());
		source.addValue("PICKUPDATE", SysParamUtil.getAppDate());
		source.addValue("Id", receiptresponseDetail.getId());

		this.jdbcTemplate.update(query.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateReceiptResponseId(String rootId, long receiptID) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder query = new StringBuilder();
		query.append(" UPDATE Auto_ReceiptDetails SET RECEIPTID = :RECEIPTID ");
		query.append(" Where Id = :Id");

		source.addValue("RECEIPTID", receiptID);
		source.addValue("Id", rootId);

		this.jdbcTemplate.update(query.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * update jobid ,as not to pick up again
	 * 
	 * @param jobid
	 */
	@Override
	public void updatePickBatchId(long jobid) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder query = new StringBuilder();
		query.append(" UPDATE Auto_ReceiptDetails SET PICKUPBATCHID = :PICKUPBATCHID ");
		query.append(" Where PICKUPBATCHID = -1");

		source.addValue("PICKUPBATCHID", jobid);

		this.jdbcTemplate.update(query.toString(), source);
		logger.debug(Literal.LEAVING);
	}

}