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
 * FileName    		:  FinMandateService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-10-2016    														*
 *                                                                  						*
 * Modified Date    :  26-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.mandate;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.mandate.Mandate;

public interface FinMandateService {

	Mandate getMnadateByID(long mandateID);

	void saveOrUpdate(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType);

	void doRejct(FinanceDetail financeDetail, AuditHeader auditHeader);

	void doApprove(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType);

	List<Mandate> getMnadateByCustID(long custID, long mandateID);
	
	void validateMandate(AuditDetail auditDetail,FinanceDetail financeDetail );
	
	void promptMandate(AuditDetail auditDetail,FinanceDetail financeDetail );

}