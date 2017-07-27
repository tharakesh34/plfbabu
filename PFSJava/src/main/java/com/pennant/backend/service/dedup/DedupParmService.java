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

import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennanttech.pennapps.core.InterfaceException;

/**
 * Service declaration for methods that depends on <b>DedupParm</b>.<br>
 * 
 */
public interface DedupParmService {
	
	
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	DedupParm getDedupParmById(String id ,String queryModule,String querySubCode);
	DedupParm getApprovedDedupParmById(String id,String queryModule,String querySubCode);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	List<DedupParm> getDedupParmByModule(String queryModule, String querySubCode, String type);
	@SuppressWarnings("rawtypes")
	List validate(String resultQuery,CustomerDedup customerDedup);
	List<CustomerDedup> fetchCustomerDedupDetails(String userRole,CustomerDetails aCustomerDetails);
	List<FinanceDedup> fetchFinDedupDetails(String userRole, FinanceDedup aFinanceDedup,String curLoginUser,String finType);
	List<BlackListCustomers> fetchBlackListCustomers(String userRole,String finType,BlackListCustomers customer, String curUser);
	List<PoliceCaseDetail> fetchPoliceCaseCustomers(String userRole,String finType,PoliceCaseDetail policeCaseData,String curUser);
	List<FinanceReferenceDetail> getQueryCodeList(FinanceReferenceDetail financeRefDetail,
            String queryCode);
	List<CustomerDedup> fetchCustomerDedupDetails(String nextRoleCode, CustomerDedup aCustomerDedup, String curLoginUser, String finType) throws InterfaceException;
	List<CustomerDedup> getCustomerDedup(CustomerDedup customerDedup, List<DedupParm> dedupParmList) throws InterfaceException;
	List<CustomerDedup> getDedupCustomerDetails(CustomerDetails detail,String finType) throws Exception; 
}