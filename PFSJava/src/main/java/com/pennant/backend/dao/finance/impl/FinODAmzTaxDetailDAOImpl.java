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

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

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
				" (TaxSeqId , FinReference, ValueDate , TaxFor, Amount, TaxType , CGST , SGST , UGST , IGST , TotalGST)");
		insertSql.append(
				" Values( :TaxSeqId , :FinReference, :ValueDate , :TaxFor, :Amount, :TaxType , :CGST , :SGST , :UGST , :IGST , :TotalGST)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODAmzTaxDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return finODAmzTaxDetail.getTaxSeqId();
	}

}