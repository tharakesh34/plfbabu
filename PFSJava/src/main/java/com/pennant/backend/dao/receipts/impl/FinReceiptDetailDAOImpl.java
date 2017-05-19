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
package com.pennant.backend.dao.receipts.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinReceiptDetailDAOImpl extends BasisNextidDaoImpl<FinReceiptDetail> implements FinReceiptDetailDAO {
	private static Logger	           logger	= Logger.getLogger(FinReceiptDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinReceiptDetailDAOImpl() {
		super();
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public List<FinReceiptDetail> getReceiptHeaderByID(long receiptID, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder selectSql = new StringBuilder("Select ReceiptID , ReceiptSeqID , ReceiptType , PaymentTo , PaymentType , PayAgainstID  , ");
		selectSql.append(" Amount  , FavourNumber , ValueDate , BankCode , FavourName , DepositDate , DepositNo , PaymentRef , ");
		selectSql.append(" TransactionRef , ChequeAcNo , FundingAc , ReceivedDate , Status , Remarks, PayOrder, LogKey, ReceiptNo ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append( " ,BankCodeDesc, FundingAcDesc, PartnerBankAc, PartnerBankAcType ");
		}
		selectSql.append(" From FinReceiptDetail");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where ReceiptID =:ReceiptID ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinReceiptDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinReceiptDetail.class);

		List<FinReceiptDetail> receiptList = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		logger.debug("Leaving");
		return receiptList;
	}

	@Override
	public long save(FinReceiptDetail receiptDetail, TableType tableType) {
		logger.debug("Entering");
		if (receiptDetail.getId() == 0 || receiptDetail.getId() == Long.MIN_VALUE) {
			receiptDetail.setId(getNextidviewDAO().getNextId("SeqFinReceiptDetail"));
			logger.debug("get NextID:" + receiptDetail.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into FinReceiptDetail");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (ReceiptID , ReceiptSeqID , ReceiptType , PaymentTo , PaymentType , PayAgainstID  , Amount  , ");
		insertSql.append(" FavourNumber , ValueDate , BankCode , FavourName , DepositDate , DepositNo , PaymentRef , ");
		insertSql.append(" TransactionRef , ChequeAcNo , FundingAc , ReceivedDate , Status , Remarks, PayOrder, LogKey, ReceiptNo)");
		insertSql.append(" Values(:ReceiptID , :ReceiptSeqID , :ReceiptType , :PaymentTo , :PaymentType , :PayAgainstID  , :Amount  , ");
		insertSql.append(" :FavourNumber , :ValueDate , :BankCode , :FavourName , :DepositDate , :DepositNo , :PaymentRef , ");
		insertSql.append(" :TransactionRef , :ChequeAcNo , :FundingAc , :ReceivedDate , :Status , :Remarks, :PayOrder, :LogKey, :ReceiptNo)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return receiptDetail.getId();
	}

	@SuppressWarnings("serial")
	@Override
	public void update(FinReceiptDetail receiptDetail, TableType tableType) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinReceiptDetail");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set PaymentType=:PaymentType , Amount=:Amount , FavourNumber=:FavourNumber, ValueDate=:ValueDate, ");
		updateSql.append(" BankCode=:BankCode , FavourName=:FavourName , DepositDate=:DepositDate , DepositNo=:DepositNo , ");
		updateSql.append(" PaymentRef=:PaymentRef , TransactionRef=:TransactionRef , ChequeAcNo=:ChequeAcNo , FundingAc=:FundingAc , ");
		updateSql.append(" ReceivedDate=:ReceivedDate , Status=:Status , Remarks=:Remarks  ");
		updateSql.append(" Where ReceiptSeqID =:ReceiptSeqID");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", receiptDetail.getReceiptSeqID(), PennantConstants.default_Language);
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	@Override
	public void deleteByReceiptID(long receiptID, TableType tableType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder deleteSql = new StringBuilder(" DELETE From FinReceiptDetail");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" where ReceiptID=:ReceiptID ");

		logger.debug("selectSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public void updateReceiptStatus(long receiptID, long receiptSeqID, String status) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("ReceiptSeqID", receiptSeqID);
		source.addValue("Status", status);
		
		StringBuilder updateSql = new StringBuilder("Update FinReceiptDetail");
		updateSql.append(" Set Status=:Status ");
		updateSql.append(" Where ReceiptID =:ReceiptID AND ReceiptSeqID=:ReceiptSeqID ");

		logger.debug("updateSql: " + updateSql.toString());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}
	
	@Override
	public int getReceiptHeaderByBank(String bankCode, String type) {
		FinReceiptDetail finReceiptDetail = new FinReceiptDetail();
		finReceiptDetail.setBankCode(bankCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinReceiptDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankCode =:BankCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finReceiptDetail);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	private ErrorDetails getError(String errorId, long receiptSeqID, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = String.valueOf(receiptSeqID);
		parms[0][0] = PennantJavaUtil.getLabel("label_ReceiptSeqID") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]),
				userLanguage);
	}

}
