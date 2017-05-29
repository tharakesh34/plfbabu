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
 * FileName    		:  LimitGroupService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.limit;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.limit.LimitGroup;
import com.pennant.backend.model.limit.LimitGroupLines;
import com.pennant.backend.model.limit.LimitStructureDetail;

public interface LimitGroupService {
	
	LimitGroup getLimitGroup();
	LimitGroup getNewLimitGroup();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	LimitGroup getLimitGroupById(String id);
	LimitGroup getApprovedLimitGroupById(String id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	boolean validationCheck(String lmtGrp);
	int limitItemCheck(String lmtItem,String limitCategory, String type);
	boolean limitLineActiveCheck( String limitCategory, String ruleCode);
	String getLimitLines(String groupCode);
	String getGroupcodes(String code, boolean line);
	List<LimitGroupLines> getGroupCodesByLimitGroup(String result, boolean b);
	List<LimitStructureDetail> getStructuredetailsByLimitGroup(String category, String limitgroup, boolean isLine,String type);
	boolean isLineUsingInUtilization(String lmtline);
}