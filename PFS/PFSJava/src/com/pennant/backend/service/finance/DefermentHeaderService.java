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
 * FileName    		:  DefermentHeaderService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2011    														*
 *                                                                  						*
 * Modified Date    :  02-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.DefermentHeader;

public interface DefermentHeaderService {
	
	DefermentHeader getDefermentHeader(boolean isWIF);
	DefermentHeader getNewDefermentHeader(boolean isWIF);
	AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean isWIF);
	DefermentHeader getDefermentHeaderById(String id,boolean isWIF);
	DefermentHeader getApprovedDefermentHeaderById(String id,boolean isWIF);
	DefermentHeader refresh(DefermentHeader defermentHeader);
	AuditHeader delete(AuditHeader auditHeader,boolean isWIF);
	AuditHeader doApprove(AuditHeader auditHeader,boolean isWIF);
	AuditHeader doReject(AuditHeader auditHeader,boolean isWIF);
}