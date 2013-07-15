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
 * FileName    		:  MortgageLoanDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-10-2011    														*
 *                                                                  						*
 * Modified Date    :  14-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.lmtmasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;

/**
 * Service declaration for methods that depends on <b>MortgageLoanDetail</b>.<br>
 */
public interface MortgageLoanDetailService {
	
	MortgageLoanDetail getMortgageLoanDetail();
	MortgageLoanDetail getNewMortgageLoanDetail();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	MortgageLoanDetail getMortgageLoanDetailById(String id);
	MortgageLoanDetail getApprovedMortgageLoanDetailById(String id);
	MortgageLoanDetail refresh(MortgageLoanDetail mortgageLoanDetail);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
}