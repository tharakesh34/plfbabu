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
 * FileName    		:  FinanceTypeService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.rmtmasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.model.rmtmasters.FinanceType;

/**
 * Service Declaration for methods that depends on <b>FinanceType</b>.<br>
 * 
 */
public interface FinanceTypeService {

	FinanceType getFinanceType();

	FinanceType getNewFinanceType();

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	FinanceType getFinanceTypeById(String id);

	FinanceType getApprovedFinanceTypeById(String id);

	FinanceType refresh(FinanceType financeType);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
	
	FinanceType getCommodityFinanceType();

	FinanceType getNewCommodityFinanceType();
	
	FinanceType getFinanceTypeByFinType(String finType);

	boolean checkRIAFinance(String finType);
	
	public FinTypeAccount getFinTypeAccount();
	
	public FinTypeAccount getNewFinTypeAccount();

}