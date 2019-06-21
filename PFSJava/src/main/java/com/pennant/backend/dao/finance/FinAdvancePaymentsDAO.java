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
 * FileName    		:  FinAdvancePaymentsDAO.java                                                   * 	  
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
package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinAdvancePayments;

public interface FinAdvancePaymentsDAO {

	FinAdvancePayments getFinAdvancePayments();

	FinAdvancePayments getNewFinAdvancePayments();

	FinAdvancePayments getFinAdvancePaymentsById(FinAdvancePayments finAdvancePayments, String type);

	int getAdvancePaymentsCountByPartnerBank(long partnerBankID, String type);

	void update(FinAdvancePayments finAdvancePaymentsDAO, String type);
	
	void updateLinkedTranId(FinAdvancePayments finAdvancePaymentsDAO);

	void delete(FinAdvancePayments finAdvancePaymentsDAO, String type);

	String save(FinAdvancePayments finAdvancePaymentsDAO, String type);

	List<FinAdvancePayments> getFinAdvancePaymentsByFinRef(String id, String type);

	void deleteByFinRef(String finReference, String tableType);

	void updateStatus(FinAdvancePayments finAdvancePayments, String type);

	int getBranch(long bankBranchID, String type);

	void update(long paymentId, long linkedTranId);

	void updateDisbursmentStatus(FinAdvancePayments finAdvancePayments);

	int getBankCode(String bankCode, String type);

	int getMaxPaymentSeq(String finReference);

	int getFinAdvCountByRef(String finReference, String type);

	int getAssignedPartnerBankCount(long partnerBankId, String type);

	int getCountByFinReference(String finReference);

	public List<FinAdvancePayments> getFinAdvancePaymentByFinRef(String finRefernce, Date toDate, String type);
}