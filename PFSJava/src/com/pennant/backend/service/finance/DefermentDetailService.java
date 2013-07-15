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
 * FileName    		:  DeferementDetailService.java                                                   * 	  
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

import java.util.Date;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.DefermentDetail;

public interface DefermentDetailService {
	
	DefermentDetail getDefermentDetail(boolean isWIF);
	DefermentDetail getNewDefermentDetail(boolean isWIF);
	AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean isWIF);
	DefermentDetail getDefermentDetailById(String id,Date schdDate, boolean isWIF);
	DefermentDetail getApprovedDefermentDetailById(String id,Date schdDate,boolean isWIF);
	DefermentDetail refresh(DefermentDetail defermentDetail);
	AuditHeader delete(AuditHeader auditHeader,boolean isWIF);
	AuditHeader doApprove(AuditHeader auditHeader,boolean isWIF);
	AuditHeader doReject(AuditHeader auditHeader,boolean isWIF);
}