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
 * FileName    		:  DedupParmService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.dedup;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.policecase.PoliceCase;

/**
 * Service declaration for methods that depends on <b>DedupParm</b>.<br>
 * 
 */
public interface DedupParmService {
	
	DedupParm getDedupParm();
	DedupParm getNewDedupParm();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	DedupParm getDedupParmById(String id ,String queryModule,String querySubCode);
	DedupParm getApprovedDedupParmById(String id,String queryModule,String querySubCode);
	DedupParm refresh(DedupParm dedupParm);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	@SuppressWarnings("rawtypes")
	List validate(String resultQuery,CustomerDedup customerDedup);
	List<CustomerDedup> fetchCustomerDedupDetails(String userRole,CustomerDetails aCustomerDetails);
	FinanceDedup getCustomerById(long custID);
	List<FinanceDedup> fetchFinDedupDetails(String userRole, FinanceDedup aFinanceDedup);
	List<BlackListCustomers> fetchBlackListCustomers(String userRole,String finType,BlackListCustomers customer);
	List<PoliceCase> fetchPoliceCaseCustomers(String userRole,String finType,PoliceCase policeCaseData);
	List<FinanceReferenceDetail> getQueryCodeList(FinanceReferenceDetail financeRefDetail,
            String queryCode);
}