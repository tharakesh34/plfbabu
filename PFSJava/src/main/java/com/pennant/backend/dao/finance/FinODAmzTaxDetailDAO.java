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

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.OverdueTaxMovement;

public interface FinODAmzTaxDetailDAO {

	long save(FinODAmzTaxDetail amzTaxDetail);

	void saveTaxReceivable(FinTaxReceivable finTaxReceivable);

	void updateTaxReceivable(FinTaxReceivable taxReceivable);

	FinTaxReceivable getFinTaxReceivable(String finReference, String type);

	void saveTaxIncome(FinTaxIncomeDetail finTaxIncomeDetail);

	FinTaxIncomeDetail getFinTaxIncomeDetail(long repayID, String type);

	boolean isDueCreatedForDate(String finRef, Date valueDate, String taxFor);

	List<FinODAmzTaxDetail> getFinODAmzTaxDetail(String finReference);

	List<FinTaxIncomeDetail> getFinTaxIncomeList(String finReference, String type);

	List<FinODAmzTaxDetail> getODTaxList(String finReference);

	void updateODTaxDueList(List<FinODAmzTaxDetail> updateDueList);

	void saveTaxList(List<OverdueTaxMovement> taxMovements);

}