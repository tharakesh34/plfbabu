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
 * FileName    		:  FinFeeReceiptDAO.java                                                * 	  
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
package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinFeeRefundDetails;
import com.pennant.backend.model.finance.FinFeeRefundHeader;
import com.pennant.backend.model.finance.PrvsFinFeeRefund;
import com.pennanttech.pff.core.TableType;

public interface FinFeeRefundDAO {

	//Header
	long save(FinFeeRefundHeader finFeeRefundHeader, String type);

	void update(FinFeeRefundHeader finFeeRefundHeader, String type);

	void deleteFinFeeRefundHeader(FinFeeRefundHeader refundHeader, TableType tableType);

	FinFeeRefundHeader getFinFeeRefundHeaderById(long headerId, String type);

	//Details
	FinFeeRefundDetails getFinFeeRefundDetailsById(long id, String type);

	List<FinFeeRefundDetails> getFinFeeRefundDetailsByHeaderId(long headerId, String type);

	String save(FinFeeRefundDetails finFeeRefundDetails, String type);

	void update(FinFeeRefundDetails FinFeeRefund, String type);

	void deleteFinFeeRefundDetailsByID(FinFeeRefundDetails refundDetails, String tableType);

	void deleteFinFeeRefundDetailsByHeaderID(FinFeeRefundHeader refundHeader, String tableType);

	PrvsFinFeeRefund getPrvsRefundsByFeeId(long feeID);

	FinFeeRefundDetails getPrvRefundDetails(long headerId, long feeID);

}