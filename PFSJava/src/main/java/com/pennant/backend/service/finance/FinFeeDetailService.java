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
 * FileName    		:  FinFeeDetailService.java                                                   * 	  
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

package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinFeeDetail;

public interface FinFeeDetailService {
	List<FinFeeDetail> getFinFeeDetailById(String id,boolean isWIF,String type);
	List<AuditDetail> saveOrUpdate(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType, boolean isWIF);
	List<AuditDetail> doApprove(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType, boolean isWIF);
	List<AuditDetail> delete(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType, boolean isWIF);
	List<AuditDetail> validate(List<FinFeeDetail> finFeeDetails, long workflowId, String method, String auditTranType, String  usrLanguage,boolean isWIF);
}